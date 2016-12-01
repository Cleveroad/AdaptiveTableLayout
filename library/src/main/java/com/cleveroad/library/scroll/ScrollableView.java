package com.cleveroad.library.scroll;

public interface ScrollableView {
    boolean removeCallbacks(Runnable action);

    boolean post(Runnable action);

    void scrollBy(int x, int y);

//    int getMaxScrollX();
//
//    int getMaxScrollY();
}
