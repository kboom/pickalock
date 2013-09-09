package com.gdroid.pickalock.pooling;
import java.util.Arrays;
import java.util.Comparator;


public class StandardSorter<T> implements Sorter<T> {

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public void sort(Object[] array, int count, Comparator comparator) {
        Arrays.sort(array, 0, count, comparator);
    }

}
