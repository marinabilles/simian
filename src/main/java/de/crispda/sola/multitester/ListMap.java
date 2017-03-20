package de.crispda.sola.multitester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListMap<T, S> extends HashMap<T, List<S>> {
    private static final long serialVersionUID = 1L;
    public void add(T t, S s) {
        if (containsKey(t)) {
            List<S> list = get(t);
            list.add(s);
        } else {
            List<S> list = new ArrayList<>();
            list.add(s);
            put(t, list);
        }
    }
}
