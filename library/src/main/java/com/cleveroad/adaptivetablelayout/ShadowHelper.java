package com.cleveroad.adaptivetablelayout;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

class ShadowHelper {
    private static final int LAYOUT_DIRECTION_LTR = 0;
    @Nullable
    private View mRightShadow;
    @Nullable
    private View mLeftShadow;
    @Nullable
    private View mTopShadow;
    @Nullable
    private View mBottomShadow;
    @Nullable
    private View mColumnsHeadersShadow;
    @Nullable
    private View mRowsHeadersShadow;
    private View mParentView;

    public ShadowHelper(View v) {
        mParentView = v;
    }

    private int getLayoutDirection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return mParentView.getLayoutDirection();
        } else {
            return 0;
        }
    }

    @NonNull
    View addColumnsHeadersShadow(ViewGroup group) {
        if (mColumnsHeadersShadow == null) {
            mColumnsHeadersShadow = new View(group.getContext());
            mColumnsHeadersShadow.setBackgroundResource(R.drawable.shadow_bottom);
            group.addView(mColumnsHeadersShadow, 0);
        }
        return mColumnsHeadersShadow;
    }

    @Nullable
    View getColumnsHeadersShadow() {
        return mColumnsHeadersShadow;
    }

    @NonNull
    View addRowsHeadersShadow(ViewGroup group) {
        if (mRowsHeadersShadow == null) {
            mRowsHeadersShadow = new View(group.getContext());
            mRowsHeadersShadow.setBackgroundResource(getLayoutDirection() == LAYOUT_DIRECTION_LTR
                    ? R.drawable.shadow_right
                    : R.drawable.shadow_left);
            group.addView(mRowsHeadersShadow, 0);
        }
        return mRowsHeadersShadow;
    }

    @Nullable
    View getRowsHeadersShadow() {
        return mRowsHeadersShadow;
    }

    void removeColumnsHeadersShadow(ViewGroup group) {
        if (mColumnsHeadersShadow != null) {
            group.removeView(mColumnsHeadersShadow);
            mColumnsHeadersShadow = null;
        }
    }

    void removeRowsHeadersShadow(ViewGroup group) {
        if (mRowsHeadersShadow != null) {
            group.removeView(mRowsHeadersShadow);
            mRowsHeadersShadow = null;
        }
    }

    @NonNull
    View addLeftShadow(ViewGroup group) {
        if (mLeftShadow == null) {
            mLeftShadow = new View(group.getContext());
            mLeftShadow.setBackgroundResource(R.drawable.shadow_left);
            group.addView(mLeftShadow, 0);
        }
        return mLeftShadow;
    }

    @Nullable
    View getLeftShadow() {
        return mLeftShadow;
    }

    @NonNull
    View addRightShadow(ViewGroup group) {
        if (mRightShadow == null) {
            mRightShadow = new View(group.getContext());
            mRightShadow.setBackgroundResource(R.drawable.shadow_right);
            group.addView(mRightShadow, 0);
        }
        return mRightShadow;
    }

    @Nullable
    View getRightShadow() {
        return mRightShadow;
    }

    @NonNull
    View addTopShadow(ViewGroup group) {
        if (mTopShadow == null) {
            mTopShadow = new View(group.getContext());
            mTopShadow.setBackgroundResource(R.drawable.shadow_top);
            group.addView(mTopShadow, 0);
        }
        return mTopShadow;
    }

    @Nullable
    View getTopShadow() {
        return mTopShadow;
    }

    @NonNull
    View addBottomShadow(ViewGroup group) {
        if (mBottomShadow == null) {
            mBottomShadow = new View(group.getContext());
            mBottomShadow.setBackgroundResource(R.drawable.shadow_bottom);
            group.addView(mBottomShadow, 0);
        }
        return mBottomShadow;
    }

    @Nullable
    View getBottomShadow() {
        return mBottomShadow;
    }

    void removeAllDragAndDropShadows(ViewGroup group) {
        if (mLeftShadow != null) {
            group.removeView(mLeftShadow);
            mLeftShadow = null;
        }

        if (mRightShadow != null) {
            group.removeView(mRightShadow);
            mRightShadow = null;
        }

        if (mTopShadow != null) {
            group.removeView(mTopShadow);
            mTopShadow = null;
        }

        if (mBottomShadow != null) {
            group.removeView(mBottomShadow);
            mBottomShadow = null;
        }
    }
}
