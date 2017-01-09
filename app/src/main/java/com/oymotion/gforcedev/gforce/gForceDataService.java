package com.oymotion.gforcedev.gforce;

import java.util.HashMap;

/** gForce data and gForce control service.
 *  Created by Ethan on 2016/12/26 0003.
 */
public class gForceDataService extends gForceService {
    private final static String TAG = gForceDataService.class.getSimpleName();

    protected gForceDataService() {
    }

    private static final String UUID_SERVICE = "0000fff0-0000-1000-8000-00805f9b34fb";

    private static final String UUID_GFORCE_DATA = "0000fff4-0000-1000-8000-00805f9b34fb";

    private static final HashMap<String, String> CHARACTERISTIC_MAP = new HashMap<String, String>();

    static {
        CHARACTERISTIC_MAP.put(UUID_GFORCE_DATA, "gForce Data");
    }

    @Override
    public String getUUID() {
        return UUID_SERVICE;
    }

    @Override
    public String getName() {
        return "gForce Data";
    }

    @Override
    public String getCharacteristicName(String uuid) {
        if (!CHARACTERISTIC_MAP.containsKey(uuid))
            return "Unknown";
        return CHARACTERISTIC_MAP.get(uuid);
    }
}
