package com.thebrokenrail.modupdater.util;

import java.util.HashMap;
import java.util.Map;

public class HardcodedData {
    public static ConfigObject getData(String modID) {
        if ("fabric".equals(modID)) {
            Map<String, Object> map = new HashMap<>();
            map.put("strategy", "maven");
            map.put("repository", "https://maven.fabricmc.net");
            map.put("group", "net.fabricmc.fabric-api");
            map.put("artifact", "fabric-api");
            return new ConfigObject.ConfigObjectHardcoded(map);
        } else {
            return null;
        }
    }
}
