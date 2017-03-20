package de.crispda.sola.multitester;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class UnorderedPair<T> {
    public final T first;
    public final T second;
    private final Set<T> set;

    public UnorderedPair(T first, T second) {
        this.first = first;
        this.second = second;
        set = new HashSet<>();
        set.add(first);
        set.add(second);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnorderedPair<?> that = (UnorderedPair<?>) o;
        return Objects.equals(set, that.set);
    }

    @Override
    public int hashCode() {
        return Objects.hash(set);
    }
}
