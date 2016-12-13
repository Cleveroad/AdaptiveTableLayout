package com.cleveroad.library;

public interface TScrollableView {
    boolean removeCallbacks(Runnable action);

    boolean post(Runnable action);

    void scrollBy(int x, int y);

}
