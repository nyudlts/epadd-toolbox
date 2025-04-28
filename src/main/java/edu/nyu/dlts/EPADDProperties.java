package edu.nyu.dlts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class EPADDProperties {
    private Map<String, String> properties = new HashMap<String, String>();

    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    public HashSet<String> getKeys() {
        HashSet<String> keys = new HashSet<String>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            keys.add(entry.getKey());   
        }
        return keys;
    }
}


