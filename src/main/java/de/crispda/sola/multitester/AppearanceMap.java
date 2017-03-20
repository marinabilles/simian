package de.crispda.sola.multitester;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AppearanceMap {
    private final Map<UnorderedPair<Transition>, AppearanceList> internalMap;

    public AppearanceMap() {
        internalMap = new HashMap<>();
    }

    public void add(UnorderedPair<Transition> pair, Appearance appearance) {
        if (!internalMap.containsKey(pair)) {
            AppearanceList al = new AppearanceList();
            al.add(appearance);
            internalMap.put(pair, al);
        } else {
            AppearanceList al = internalMap.get(pair);
            al.add(appearance);
        }
    }

    public Collection<AppearanceList> values() {
        return internalMap.values();
    }

    public Set<Map.Entry<UnorderedPair<Transition>, AppearanceList>> entrySet() {
        return internalMap.entrySet();
    }
}
