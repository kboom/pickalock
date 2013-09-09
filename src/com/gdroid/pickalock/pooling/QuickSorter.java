package com.gdroid.pickalock.pooling;

import java.util.Comparator;

public class QuickSorter<T> implements Sorter<T> {
    
	public void sort(T[] array, int count, Comparator<T> comparator) {
        quicksort(array, 0, count - 1, comparator);
    }
	
    public void quicksort(T[] a, int left, int right, Comparator<T> comparator) {
        if (right <= left) return;
        int i = partition(a, left, right, comparator);
        quicksort(a, left, i - 1, comparator);
        quicksort(a, i + 1, right, comparator);
    }
       
    private int partition(T[] a, int left, int right, Comparator<T> comparator) {
        int i = left - 1;
        int j = right;
        while (true) {
            while (comparator.compare(a[++i], a[right]) < 0) {  
            }      
            while (comparator.compare(a[right], a[--j]) < 0) {  
                if (j == left) { 
                    break;               
                }
            }
            if (i >= j) {
                break;                
            }
            T swap = a[i];           
            a[i] = a[j];
            a[j] = swap;
        }
        T swap = a[i];
        a[i] = a[right];
        a[right] = swap;
        return i;
    }
}

