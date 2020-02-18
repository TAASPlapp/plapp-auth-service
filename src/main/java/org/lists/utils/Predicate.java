package org.lists.utils;

public interface Predicate<R, T> {
    R apply(T t);
}