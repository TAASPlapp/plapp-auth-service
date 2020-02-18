package org.lists.utils;

import java.util.ArrayList;
import java.util.List;

public class Lists {
    public static <T> List<T> filter(List<T> list, Predicate<Boolean, T> criteria) {
        List<T> result = new ArrayList<>();

        for (T t : list)
            if (criteria.apply(t))
                result.add(t);

        return result;
    }

    public static <T> Boolean satisfies(List<T> list, Predicate<Boolean, T> criteria) {
        for (T t : list)
            if (criteria.apply(t))
                return true;
        return false;
    }

    public static <T> Boolean satisfiesAll(List<T> list, Predicate<Boolean, T> criteria) {
        for (T t : list)
            if (!criteria.apply(t))
                return false;
        return true;
    }

    public static <T> void apply(List<T> list, Applicable<T> method) {
        for (T t : list)
            method.apply(t);
    }

    public static <T, R> R reduce(List<T> list, Accumulator<T, R> accumulator) {
        R r = null;
        for (T t : list)
            r = accumulator.accumulate(t, r);
        return r;
    }
}
