/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oymotion.gforcedev;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import com.oymotion.gforcedev.adapters.BleServicesAdapter;
import com.oymotion.gforcedev.adapters.BleServicesAdapter.OnServiceItemClickListener;
import com.oymotion.gforcedev.gforce.gForceService;
import com.oymotion.gforcedev.gforce.gForceServices;
import com.oymotion.gforcedev.info.BleInfoService;
import com.oymotion.gforcedev.info.BleInfoServices;

import static java.lang.Boolean.TRUE;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BleService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceServicesActivity extends Activity {
    private final static String TAG = DeviceServicesActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    // GAP profile
    private static final String UUID_GAP_SRV = "00001800-0000-1000-8000-00805f9b34fb";
    private static final String UUID_GAP_DEVICE_NAME_CHAR = "00002a00-0000-1000-8000-00805f9b34fb";
    private static final String UUID_GAP_APPEARANCE_CHAR = "00002a01-0000-1000-8000-00805f9b34fb";
    private static final String UUID_GAP_PPF_CHAR = "00002a02-0000-1000-8000-00805f9b34fb";
    private static final String UUID_GAP_RECCON_ADDR_CHAR = "00002a03-0000-1000-8000-00805f9b34fb";
    private static final String UUID_GAP_PPCP_CHAR = "00002a04-0000-1000-8000-00805f9b34fb";

    // Device information profile
    private static final String UUID_DEV_INFO_SRV = "0000180a-0000-1000-8000-00805f9b34fb";
    private static final String UUID_DEV_SYSTEM_ID_CHAR = "00002a23-0000-1000-8000-00805f9b34fb";
    private static final String UUID_DEV_MODEL_NUM_CHAR = "00002a24-0000-1000-8000-00805f9b34fb";
    private static final String UUID_DEV_SERIAL_NUM_CHAR = "00002a25-0000-1000-8000-00805f9b34fb";
    private static final String UUID_DEV_FIRMWARE_REV_CHAR = "00002a26-0000-1000-8000-00805f9b34fb";
    private static final String UUID_DEV_HARDWARE_REV_CHAR = "00002a27-0000-1000-8000-00805f9b34fb";
    private static final String UUID_DEV_SOFTWARE_REV_CHAR = "00002a28-0000-1000-8000-00805f9b34fb";
    private static final String UUID_DEV_MANUFACTURER_NAME_CHAR = "00002a29-0000-1000-8000-00805f9b34fb";
    private static final String UUID_DEV_CERT_CHAR = "00002a2a-0000-1000-8000-00805f9b34fb";
    private static final String UUID_DEV_PNP_ID_CHAR = "00002a50-0000-1000-8000-00805f9b34fb";

    // gForce Data profile
    private static final String UUID_GFORCE_DATA_SRV = "0000fff0-0000-1000-8000-00805f9b34fb";
    private static final String UUID_GFORCE_DATA_CHAR = "0000fff4-0000-1000-8000-00805f9b34fb";

    // OAD reset profile
    private static final String UUID_OAD_RESET_SRV = "f000ffd0-0451-4000-b000-000000000000";
    private static final String UUID_OAD_RESET_CHAR = "f000ffd1-0451-4000-b000-000000000000";

    // OAD profile
    public static final String UUID_OAD_SRV = "f000ffc0-0451-4000-b000-000000000000";
    public static final String UUID_OAD_IMG_ID_CHAR = "f000ffc1-0451-4000-b000-000000000000";
    public static final String UUID_OAD_IMG_BLK_CHAR = "f000ffc2-0451-4000-b000-000000000000";

    private TextView connectionState;
    private TextView dataField;
    
    private ExpandableListView gattServicesList;
    private BleServicesAdapter gattServiceAdapter;

    private String deviceName;
    private String deviceAddress;
    private BleService bleService;
    private boolean isConnected = false;

    private byte[] imageData = new byte[0];
    private BluetoothGattService oadSrv = null;
    private BluetoothGattCharacteristic oadImgIdentifyChar = null;
    private BluetoothGattCharacteristic oadImgBlockChar = null;
    private int blockNum = 0;

    private String gapDevName = null;
    private String gapAppearance = null;
    private String gapPpf = null;
    private String gapReconnAddr = null;
    private String gapPpcp = null;

    private String devSysId = null;
    private String devModelNum = null;
    private String devSerialNum = null;
    private String devFirmwareRev = null;
    private String devHardwareRev = null;
    private String devSoftwareRev = null;
    private String devManufacturerName = null;
    private String devCertId = null;
    private String devPnpId = null;

	private OnServiceItemClickListener serviceListener;

    // Code to manage Service lifecycle.
    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bleService = ((BleService.LocalBinder) service).getService();
            if (!bleService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            bleService.connect(deviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bleService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BleService.ACTION_GATT_CONNECTED.equals(action)) {
                isConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BleService.ACTION_GATT_DISCONNECTED.equals(action)) {
                isConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BleService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(bleService.getSupportedGattServices());

                if (gattServiceAdapter != null) {
                    // If GAP service discovered, read characteristic value.
                    if (gattServiceAdapter.containsGroup(UUID_GAP_SRV)) {
                        if (gattServiceAdapter.containsChild(UUID_GAP_DEVICE_NAME_CHAR)) {
                            bleService.read(UUID_GAP_SRV, UUID_GAP_DEVICE_NAME_CHAR, null);
                        }
                        if (gattServiceAdapter.containsChild(UUID_GAP_APPEARANCE_CHAR)) {
                            bleService.read(UUID_GAP_SRV, UUID_GAP_APPEARANCE_CHAR, null);
                        }
                        if (gattServiceAdapter.containsChild(UUID_GAP_PPF_CHAR)) {
                            bleService.read(UUID_GAP_SRV, UUID_GAP_PPF_CHAR, null);
                        }
                        if (gattServiceAdapter.containsChild(UUID_GAP_RECCON_ADDR_CHAR)) {
                            bleService.read(UUID_GAP_SRV, UUID_GAP_RECCON_ADDR_CHAR, null);
                        }
                        if (gattServiceAdapter.containsChild(UUID_GAP_PPCP_CHAR)) {
                            bleService.read(UUID_GAP_SRV, UUID_GAP_PPCP_CHAR, null);
                        }
                    }

                    // If device information service discovered, read characteristic value.
                    if (gattServiceAdapter.containsGroup(UUID_DEV_INFO_SRV)) {
                        if (gattServiceAdapter.containsChild(UUID_DEV_SYSTEM_ID_CHAR)) {
                            bleService.read(UUID_DEV_INFO_SRV, UUID_DEV_SYSTEM_ID_CHAR, null);
                        }
                        if (gattServiceAdapter.containsChild(UUID_DEV_MODEL_NUM_CHAR)) {
                            bleService.read(UUID_DEV_INFO_SRV, UUID_DEV_MODEL_NUM_CHAR, null);
                        }
                        if (gattServiceAdapter.containsChild(UUID_DEV_SERIAL_NUM_CHAR)) {
                            bleService.read(UUID_DEV_INFO_SRV, UUID_DEV_SERIAL_NUM_CHAR, null);
                        }
                        if (gattServiceAdapter.containsChild(UUID_DEV_FIRMWARE_REV_CHAR)) {
                            bleService.read(UUID_DEV_INFO_SRV, UUID_DEV_FIRMWARE_REV_CHAR, null);
                        }
                        if (gattServiceAdapter.containsChild(UUID_DEV_HARDWARE_REV_CHAR)) {
                            bleService.read(UUID_DEV_INFO_SRV, UUID_DEV_HARDWARE_REV_CHAR, null);
                        }
                        if (gattServiceAdapter.containsChild(UUID_DEV_SOFTWARE_REV_CHAR)) {
                            bleService.read(UUID_DEV_INFO_SRV, UUID_DEV_SOFTWARE_REV_CHAR, null);
                        }
                        if (gattServiceAdapter.containsChild(UUID_DEV_MANUFACTURER_NAME_CHAR)) {
                            bleService.read(UUID_DEV_INFO_SRV, UUID_DEV_MANUFACTURER_NAME_CHAR, null);
                        }
                        if (gattServiceAdapter.containsChild(UUID_DEV_CERT_CHAR)) {
                            bleService.read(UUID_DEV_INFO_SRV, UUID_DEV_CERT_CHAR, null);
                        }
                        if (gattServiceAdapter.containsChild(UUID_DEV_PNP_ID_CHAR)) {
                            bleService.read(UUID_DEV_INFO_SRV, UUID_DEV_PNP_ID_CHAR, null);
                        }
                    }

                    // If gForce OAD service discovered, should first enable characteristic
                    // notification, to prepare for OAD flow.
                    if (gattServiceAdapter.containsGroup(UUID_OAD_SRV)) {
                        if (gattServiceAdapter.containsChild(UUID_OAD_IMG_ID_CHAR)) {
                            bleService.notifyConfig(UUID_OAD_SRV, UUID_OAD_IMG_ID_CHAR, TRUE);
                            Log.d(TAG, "Enable OAD image identity char notify");
                        }
                        if (gattServiceAdapter.containsChild(UUID_OAD_IMG_BLK_CHAR)) {
                            bleService.notifyConfig(UUID_OAD_SRV, UUID_OAD_IMG_BLK_CHAR, TRUE);
                            Log.d(TAG, "Enable OAD image block char notify");
                        }
                    }
                }
            } else if (BleService.ACTION_DATA_AVAILABLE.equals(action)) {
                String srvUuid = intent.getStringExtra(BleService.EXTRA_SERVICE_UUID);
                String charUuid = intent.getStringExtra(BleService.EXTRA_CHARACTERISTIC_UUID);
                byte[] data = intent.getByteArrayExtra(BleService.EXTRA_DATA);

                // GAP profile data
                if (srvUuid.equals(UUID_GAP_SRV)) {
                    if (charUuid.equals(UUID_GAP_DEVICE_NAME_CHAR)) {
                        gapDevName = new String(data);
                    } else if (charUuid.equals(UUID_GAP_APPEARANCE_CHAR)) {
                        gapAppearance = new String(data);
                    } else if (charUuid.equals(UUID_GAP_PPF_CHAR)) {

                    } else if (charUuid.equals(UUID_GAP_RECCON_ADDR_CHAR)) {

                    } else if (charUuid.equals(UUID_GAP_PPCP_CHAR)) {
                        double connIntervalMin = ((data[0] & 0xFF) | ((data[1] & 0xFF) << 8)) * 1.25;
                        double connIntervalMax = ((data[2] & 0xFF) | ((data[3] & 0xFF) << 8)) * 1.25;
                        int connLatency = ((data[4] & 0xFF) | ((data[5] & 0xFF) << 8));
                        int connTimeout = ((data[6] & 0xFF) | ((data[7] & 0xFF) << 8)) * 10;

//                        StringBuilder gapPpcpBuilder = new StringBuilder();

                        gapPpcp = null;
                    }
                }
                // Device information data
                else if (srvUuid.equals(UUID_DEV_INFO_SRV)) {
                    if (charUuid.equals(UUID_DEV_SYSTEM_ID_CHAR)) {

                    } else if (charUuid.equals(UUID_DEV_MODEL_NUM_CHAR)) {

                    } else if (charUuid.equals(UUID_DEV_SERIAL_NUM_CHAR)) {

                    } else if (charUuid.equals(UUID_DEV_FIRMWARE_REV_CHAR)) {

                    } else if (charUuid.equals(UUID_DEV_HARDWARE_REV_CHAR)) {

                    } else if (charUuid.equals(UUID_DEV_SOFTWARE_REV_CHAR)) {

                    } else if (charUuid.equals(UUID_DEV_MANUFACTURER_NAME_CHAR)) {

                    } else if (charUuid.equals(UUID_DEV_CERT_CHAR)) {

                    } else if (charUuid.equals(UUID_DEV_PNP_ID_CHAR)) {

                    }
                }
                // OAD profile data
                else if (srvUuid.equals(UUID_OAD_SRV)) {
                    if (charUuid.equals(UUID_OAD_IMG_BLK_CHAR)) {
                        Log.d(TAG, "OAD image block number received");
                        Log.d(TAG, "data len: " + data.length);

                        int reqBlockNum = (int) ((data[0] & 0xFF) + ((data[1] & 0xFF) << 8));
                        Log.d(TAG, "reqBlockNum = " + reqBlockNum);
                        if ( (reqBlockNum == blockNum)
                                && ((blockNum * 16) < imageData.length)
                                && (data.length == 2) ) {
                            byte[] blockBuf = readBlock(blockNum, imageData);
                            byte[] blockWithBlkNumBuf = addBlknumToBlock(data, blockBuf);

                            Log.d(TAG, "imageData Len " + imageData.length);
                            Log.d(TAG, "blockNum: " + blockNum + ",  blockBuf len: " + blockWithBlkNumBuf.length);
                            dumpBytes(blockWithBlkNumBuf);
                            // Write OAD Image Block Characteristic.
                            bleService.write(UUID_OAD_SRV,
                                    UUID_OAD_IMG_BLK_CHAR,
                                    blockWithBlkNumBuf);
                            blockNum++;
                        }
                        if (data.length != 2) {
                            dumpBytes(data);
                        }

                    } else if (charUuid.equals(UUID_OAD_IMG_ID_CHAR)) {
                        Log.d(TAG, "OAD image identify char notify recieved");
                    }
                }
                else {
                    displayData(srvUuid, charUuid, new String(data));
                }
            }
        }
    };

    // If a given GATT characteristic is selected, check for supported features.  For example
    // 'Read', 'Write', 'Notify' and 'Indicate' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    Log.d(TAG, "item click");
                    if (gattServiceAdapter == null)
                        return false;

                    final BluetoothGattService service = gattServiceAdapter.getGroup(groupPosition);
                    final BluetoothGattCharacteristic characteristic = gattServiceAdapter.getChild(groupPosition, childPosition);

                    final BleInfoService infoService = BleInfoServices
                            .getService(service.getUuid().toString());
                    final gForceService gforceService = gForceServices
                            .getService(service.getUuid().toString());

                    if (infoService != null) {
                        Log.d(TAG, "infoService != null");
                        if (characteristic.getProperties() == BluetoothGattCharacteristic.PROPERTY_READ) {
                            bleService.read(service.getUuid().toString(),
                                    characteristic.getUuid().toString(), null);
                            Log.d(TAG, "read char");
                        }
                    } else if (gforceService != null) {
                        Log.d(TAG, "gforceService != null");
                        if (gforceService.getUUID().equals("f000ffd0-0451-4000-b000-000000000000")) {
                            // gForce firmware reset Service
                            if (characteristic.getUuid().toString().equals("f000ffd1-0451-4000-b000-000000000000")) {
                                // reset characteristic
                                byte[] wrtVal = {0x01, 0x02, 0x03};
                                bleService.write(service.getUuid().toString(),
                                        characteristic.getUuid().toString(),
                                        wrtVal);
                                Log.d(TAG, "OAD reset write");
                            }
                        } else if (gforceService.getUUID().equals("0000fff0-0000-1000-8000-00805f9b34fb")) {
                            // gForce Data Service
                            Log.d(TAG, "gForce Data Service");
                            if (characteristic.getUuid().toString().equals("0000fff4-0000-1000-8000-00805f9b34fb")) {
                                // gForce data characteristic
                                bleService.notifyConfig(service.getUuid().toString(),
                                        characteristic.getUuid().toString(),
                                        TRUE);
                                Log.d(TAG, "gForce data notify enable");
                            }
                        } else if (gforceService.getUUID().equals("f000ffc0-0451-4000-b000-000000000000")) {
                            // gForce OAD Service
                            Log.d(TAG, "gForce OAD Service");

                            oadSrv = service;
                            oadImgIdentifyChar = characteristic;

                            // select a binary file to write
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            // any file format
                            intent.setType("*/*");
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            startActivityForResult(intent,1);
                        }
                    } else {
                        // do nothing
                    }

                    return true;
                }
            };

    private final BleServicesAdapter.OnServiceItemClickListener demoClickListener = new BleServicesAdapter.OnServiceItemClickListener() {
        @Override
        public void onDemoClick(BluetoothGattService service) {
        	Log.d(TAG, "onDemoClick: service" +service.getUuid().toString());
        }

        @Override
        public void onServiceEnabled(BluetoothGattService service, boolean enabled) {
            // may enable some notifications here.
        }

        @Override
        public void onServiceUpdated(BluetoothGattService service) {

        }
    };

    private void clearUI() {
        gattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        dataField.setText(R.string.no_data);
    }

	public void setServiceListener(OnServiceItemClickListener listener) {
		this.serviceListener = listener;
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        final Intent intent = getIntent();
        deviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        deviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(deviceAddress);
        gattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
        gattServicesList.setOnChildClickListener(servicesListClickListner);
        connectionState = (TextView) findViewById(R.id.connection_state);
        dataField = (TextView) findViewById(R.id.data_value);
        
        getActionBar().setTitle(deviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        final Intent gattServiceIntent = new Intent(this, BleService.class);
        bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
        if (bleService != null) {
            final boolean result = bleService.connect(deviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(gattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        bleService = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Log.d(TAG, uri.toString());

            bleService.notifyConfig(UUID_OAD_SRV, UUID_OAD_IMG_ID_CHAR, TRUE);
            Log.d(TAG, "Enable OAD image identity char notify");

            bleService.notifyConfig(UUID_OAD_SRV, UUID_OAD_IMG_BLK_CHAR, TRUE);
            Log.d(TAG, "Enable OAD image block char notify");

            String file_path = uri.toString().substring(5);
            try {
                // Read firmware image file from device's local storage.
                imageData = readImageFile(file_path);
                byte[] blockBuf = readBlock(blockNum, imageData);

                Log.d(TAG, "imageData Len and header: " + imageData.length);
                dumpBytes(blockBuf);
                // Write OAD Image Identify Characteristic.
                bleService.write(oadSrv.getUuid().toString(),
                        oadImgIdentifyChar.getUuid().toString(),
                        blockBuf);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    // Dump contents in byte array, use hex format.
    public void dumpBytes(byte[] inputBytes) {
        if (inputBytes != null && inputBytes.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(inputBytes.length);
            for (byte byteChar : inputBytes) {
                stringBuilder.append(String.format("%02X ", byteChar));
            }
            Log.d(TAG, stringBuilder.toString());
        }
    }

    // add block number for OAD block data, 2 bytes of block number + 16 bytes of block data
    public byte[] addBlknumToBlock(byte[] blkNumBytes, byte[] blockBytes) {
        byte[] addBlknumBlkBytes = new byte[18];
        addBlknumBlkBytes[0] = blkNumBytes[0];
        addBlknumBlkBytes[1] = blkNumBytes[1];
        for (int i = 0; i < 16; i++) {
            addBlknumBlkBytes[i + 2] = blockBytes[i];
        }
        return addBlknumBlkBytes;
    }

    // read a block data(16 bytes) from image file
    public byte[] readBlock(int blockNumber, byte[] firmwareImageData) {
        byte[] blockData = new byte[16];
        for (int i = 0; i < 16; i++) {
            blockData[i] = firmwareImageData[i + blockNumber * 16];
        }
        return blockData;
    }

    // read file to memory
    public byte[] readImageFile(String fileName) throws IOException {

        File file = new File(fileName);

        FileInputStream fis = new FileInputStream(file);

        int length = fis.available();

        Log.d(TAG, "Binary File Len: " + length);

        byte [] buffer = new byte[length];
        fis.read(buffer);

        fis.close();
        return buffer;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (isConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                bleService.connect(deviceAddress);
                return true;
            case R.id.menu_disconnect:
                bleService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String srvUuid, String charUuid, String data) {
		if (data != null) {
            dataField.setText(data);
            if (gattServiceAdapter != null) {
//                gattServiceAdapter.notifyDataSetChanged();
            }
		}
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;

        gattServiceAdapter = new BleServicesAdapter(this, gattServices);
        gattServiceAdapter.setServiceListener(demoClickListener);
        gattServicesList.setAdapter(gattServiceAdapter);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
