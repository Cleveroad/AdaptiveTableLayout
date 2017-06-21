package com.cleveroad.adaptivetablelayout;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

/**
 * same as {@link AdaptiveTableManager}, but support rtl direction
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
class AdaptiveTableManagerRTL extends AdaptiveTableManager {

    private ViewGroup parentView;

    public AdaptiveTableManagerRTL(ViewGroup parentView) {
        this.parentView = parentView;
    }

    @Override
    int getColumnByXWithShift(int x, int shiftEveryStep) {
        if (parentView.getLayoutDirection() == View.LAYOUT_DIRECTION_LTR) {
            return super.getColumnByXWithShift(x, shiftEveryStep);
        } else {
            checkForInit();
            int sum = 0;
            // header offset
            int tempX = x;
            if (tempX <= sum) {
                return 0;
            }
            for (int count = getColumnWidths().length, i = 0; i < count; i++) {
                int nextSum = sum + getColumnWidths()[i] + shiftEveryStep;
                if (tempX > sum && tempX < nextSum) {
                    return i;
                } else if (tempX < nextSum) {
                    return i - 1;
                }
                sum = nextSum;
            }
            return getColumnWidths().length - 1;
        }
    }

}
