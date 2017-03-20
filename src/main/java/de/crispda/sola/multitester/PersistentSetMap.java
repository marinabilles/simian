package de.crispda.sola.multitester;

import com.google.common.collect.Sets;

import java.util.*;

public class PersistentSetMap {
    private final Map<TransitionSequence, Set<Transition>> internalMap;

    public PersistentSetMap() {
        internalMap = new HashMap<>();
    }

    public void clear() {
        internalMap.clear();
    }

    public void setToEmpty(TransitionSequence state) {
        if (internalMap.containsKey(state)) {
            internalMap.get(state).clear();
        } else {
            internalMap.put(state, new HashSet<>());
        }
    }

    public Optional<Set<Transition>> get(TransitionSequence state) {
        return Optional.ofNullable(internalMap.get(state));
    }

    public void insert(TransitionSequence key, Transition newTransition) {
        if (!internalMap.containsKey(key)) {
            internalMap.put(key, Sets.newHashSet(newTransition));
        } else {
            internalMap.get(key).add(newTransition);
        }
    }

    public void put(TransitionSequence key, Set<Transition> newSet) {
        internalMap.put(key, newSet);
    }
}
