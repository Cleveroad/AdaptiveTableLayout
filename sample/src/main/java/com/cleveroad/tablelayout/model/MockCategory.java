package com.cleveroad.tablelayout.model;

import java.util.Map;
import java.util.WeakHashMap;

public class MockCategory {
    private final int mSize;
    private final String mName;
    private final Map<Integer, MockItem> mNexusMap;

    public MockCategory(String name, int itemsCount) {
        mName = name;
        mSize = itemsCount;
        mNexusMap = new WeakHashMap<>(mSize);
    }

    public int size() {
        return mSize;
    }

    public MockItem get(int i) {
        MockItem mockItem = mNexusMap.get(i);
        if (mockItem == null) {
            mockItem = new MockItem(i, mSize);
            mNexusMap.put(i, mockItem);
        }
        return mockItem;
    }

    public String getName() {
        return mName;
    }
}
