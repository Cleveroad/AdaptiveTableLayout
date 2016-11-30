package com.cleveroad.library;

import android.annotation.SuppressLint;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * The class which helps to calculate sum of the array ranges
 */
class LazyIntArrayCalc {
    private final List<Range> mRanges = new ArrayList<>();
    private int[] mArray;

    LazyIntArrayCalc(int array[]) {
        setArray(array);
    }

    LazyIntArrayCalc() {
        this(new int[0]);
    }

    /**
     * The ranges will be recalculated
     */
    void invalidate() {
        mRanges.clear();
        mRanges.addAll(arrayToRanges(mArray));
    }

    /**
     * @return the size of array
     */
    public int getSize() {
        return mArray.length;
    }

    /**
     * @param array array of integers
     */
    void setArray(int array[]) {
        mArray = array;
        invalidate();
    }

    /**
     *
     * @param index
     * @return
     */
    int getItem(int index) { //TODO: improve algorithm, without invalidate
        return mArray[index];
    }

    /**
     * After changing array, you should call {@code LazyIntArrayCalc{@link #invalidate()}}
     * @param index
     * @param value
     */
    void setItem(int index, int value) {
        mArray[index] = value;
    }

    int getArraySum() {
        return getArraySum(0, mArray.length);
    }

    @SuppressLint("UseSparseArrays")
    int getArraySum(final int firstIndex, final int count) {

        int result = 0;

        int endIndex = firstIndex + count;
        for(Range range : mRanges) {

            int rangeEndIndex = range.mStartIndex + range.mSize;
            if(rangeEndIndex >= firstIndex && range.mStartIndex <= endIndex) {
                int possibleCount = Math.min(rangeEndIndex, endIndex) - Math.max(range.mStartIndex, firstIndex);
                result += range.mValue * possibleCount;
            }
        }

        return result;
    }

    /**
     * The function, which transforms an array of integers to the list of array ranges
     * For example, input data:
     * [1,1,1,1,1,2,3,3,3]
     * Output data
     * [{startIndex=0;value=1;size=5},{startIndex=5;value=2;size=1},{startIndex=6;value=3;size=3}]
     * @param array an array which will be transformed to the ranges
     * @return list of array ranges
     */
    private static List<Range> arrayToRanges(int array[]) {
        List<Range> result = new ArrayList<>();

        if(array.length > 0) {
            Range range = null;
            for (int i = 0; i < array.length; i++) {
                int currentVal = array[i];
                if(range == null) {
                    range = new Range(i, currentVal, 1);
                } else if(range.mValue == currentVal) {
                    range.mSize++;
                } else {
                    result.add(range);
                    range = new Range(i, currentVal, 1);
                }
            }

            if(range != null) {
                result.add(range);
            }
        }

        return result;
    }


    /**
     * The range of array
     */
    static class Range {
        private int mStartIndex, mValue, mSize;

        Range() {

        }

        Range(int rangeStart, int value, int size) {
            this.mStartIndex = rangeStart;
            this.mValue = value;
            this.mSize = size;
        }
    }
}
