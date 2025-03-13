package com.exaroton.proxy;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public interface Sets {
    /**
     * Create a new set with the union of the given sets
     * @param set the sets
     * @return the union of the sets
     * @param <T> the type of the set
     */
    @SafeVarargs
    static <T> Set<T> union(Set<T>... set) {
        Set<T> result = new HashSet<>();
        for (Set<T> s : set) {
            result.addAll(s);
        }
        return Collections.unmodifiableSet(result);
    }
}
