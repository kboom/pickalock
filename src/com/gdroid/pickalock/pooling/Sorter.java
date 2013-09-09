package com.gdroid.pickalock.pooling;

import java.util.Comparator;

public interface Sorter<T> {
    public void sort(T[] array, int count, Comparator<T> comparator);
}

