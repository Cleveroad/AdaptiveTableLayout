package com.cleveroad.adaptivetablelayout;

import android.annotation.TargetApi;
import android.os.Build;

/**
 * same as {@link AdaptiveTableManager}, but support rtl direction
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
class AdaptiveTableManagerRTL extends AdaptiveTableManager {

    private LayoutDirectionHelper mLayoutDirectionHelper;

    AdaptiveTableManagerRTL(LayoutDirectionHelper layoutDirectionHelper) {
        mLayoutDirectionHelper = layoutDirectionHelper;
    }

    @Override
    int getColumnByXWithShift(int x, int shiftEveryStep) {
        if (!mLayoutDirectionHelper.isRTL()) {
            return super.getColumnByXWithShift(x, shiftEveryStep);
        } else {
            checkForInit();
            int sum = 0;
            if (x <= sum) {
                return 0;
            }
            for (int count = getColumnWidths().length, i = 0; i < count; i++) {
                int nextSum = sum + getColumnWidths()[i] + shiftEveryStep;
                if (x > sum && x < nextSum) {
                    return i;
                } else if (x < nextSum) {
                    return i - 1;
                }
                sum = nextSum;
            }
            return getColumnWidths().length - 1;
        }
    }
}
