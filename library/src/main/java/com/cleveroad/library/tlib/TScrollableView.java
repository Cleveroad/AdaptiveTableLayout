package com.cleveroad.library.tlib;

public interface TScrollableView {
    boolean removeCallbacks(Runnable action);

    boolean post(Runnable action);

    void scrollBy(int x, int y);

}
