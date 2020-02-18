package org.lists.utils;

public interface Accumulator<T, R> {
    R accumulate(T t, R r);
}
