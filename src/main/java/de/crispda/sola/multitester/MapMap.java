package de.crispda.sola.multitester;

import java.util.HashMap;
import java.util.Map;

public class MapMap<K, K1, V> extends HashMap<K, Map<K1, V>> {
    private static final long serialVersionUID = 1L;
    public void add(K k, K1 k1, V v) {
        if (containsKey(k)) {
            Map<K1, V> map = get(k);
            if (!map.containsKey(k1))
                map.put(k1, v);
        } else {
            Map<K1, V> map = new HashMap<>();
            map.put(k1, v);
            put(k, map);
        }
    }
}
