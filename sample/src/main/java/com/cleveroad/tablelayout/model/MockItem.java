package com.cleveroad.tablelayout.model;

public class MockItem {
    private final int mColNumber;
    private final int mColCount;

    public MockItem(int colNumber, int count) {
        mColNumber = colNumber;
        mColCount = count;
    }

    public String getProp(int i) {
        return String.valueOf(mColNumber) + ";" + String.valueOf(i);
    }

    public int propsCount() {
        return mColCount;
    }
}
