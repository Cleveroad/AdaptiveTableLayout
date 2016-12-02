package com.cleveroad.library.scroll;

import android.content.Context;

public interface ScrollMediatorListener {

    Context getContext();

    /**
     * Chech if long tap was on header
     *
     * @param x long touch x
     * @param y long touch y
     * @return true if x,y in header rect.
     */
    boolean canStartDragging(int x, int y);

    int getMaxScrollX();

    int getMaxScrollY();

    int getTouchSlop();

    int getMaxVelocity();

    int getMinVelocity();

    int getLayoutWidth();
}
