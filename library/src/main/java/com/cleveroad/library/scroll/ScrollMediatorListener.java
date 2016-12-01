package com.cleveroad.library.scroll;

import android.content.Context;

public interface ScrollMediatorListener {

    Context getContext();

    int getMaxScrollX();

    int getMaxScrollY();

    int getTouchSlop();

    int getMaxVelocity();

    int getMinVelocity();

    int getLayoutWidth();
}
