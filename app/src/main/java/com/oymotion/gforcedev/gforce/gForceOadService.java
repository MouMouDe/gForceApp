package com.oymotion.gforcedev.gforce;

import java.util.HashMap;

/** gforce OAD service, used only in OAD mode.
 *  Created by Ethan on 2017/1/4 0004.
 */
public class gForceOadService extends gForceService {
    private final static String TAG = gForceOadService.class.getSimpleName();

    protected gForceOadService() {
    }

    private static final String UUID_SERVICE = "f000ffc0-0451-4000-b000-000000000000";

    private static final String UUID_IMG_IDENTIFY = "f000ffc1-0451-4000-b000-000000000000";

    private static final String UUID_IMG_BLOCK = "f000ffc2-0451-4000-b000-000000000000";

    private static final HashMap<String, String> CHARACTERISTIC_MAP = new HashMap<String, String>();

    static {
        CHARACTERISTIC_MAP.put(UUID_IMG_IDENTIFY, "Image Identify");
        CHARACTERISTIC_MAP.put(UUID_IMG_BLOCK, "Image Block");
    }

    @Override
    public String getUUID() {
        return UUID_SERVICE;
    }

    @Override
    public String getName() {
        return "OAD Service";
    }

    @Override
    public String getCharacteristicName(String uuid) {
        if (!CHARACTERISTIC_MAP.containsKey(uuid))
            return "Unknown";
        return CHARACTERISTIC_MAP.get(uuid);
    }
}
