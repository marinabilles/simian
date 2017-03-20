package de.crispda.sola.multitester;

import java.util.*;

public class SetMap<T, S> extends HashMap<T, Set<S>> {
    public void add(T t, S s) {
        if (containsKey(t)) {
            Set<S> list = get(t);
            list.add(s);
        } else {
            Set<S> list = new HashSet<>();
            list.add(s);
            put(t, list);
        }
    }
}
