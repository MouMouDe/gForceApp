package com.oymotion.gforcedev.gforce;

import java.util.HashMap;

/** Reset gforce device's firmware, prepare for OAD download.
 *  Created by Ethan on 2016/12/26 0003.
 */
public class gForceOadResetService extends gForceService {
    private final static String TAG = gForceOadResetService.class.getSimpleName();

    protected gForceOadResetService() {
    }

    private static final String UUID_SERVICE = "f000ffd0-0451-4000-b000-000000000000";

    private static final String UUID_RESET = "f000ffd1-0451-4000-b000-000000000000";

    private static final HashMap<String, String> CHARACTERISTIC_MAP = new HashMap<String, String>();

    static {
        CHARACTERISTIC_MAP.put(UUID_RESET, "Firmware Reset");
    }

    @Override
    public String getUUID() {
        return UUID_SERVICE;
    }

    @Override
    public String getName() {
        return "Firmware Upgrade";
    }

    @Override
    public String getCharacteristicName(String uuid) {
        if (!CHARACTERISTIC_MAP.containsKey(uuid))
            return "Unknown";
        return CHARACTERISTIC_MAP.get(uuid);
    }
}
