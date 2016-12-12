package com.cleveroad.library.tlib;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;

import java.util.Collection;


public class TAnimationHelper {
    private static final long ANIMATION_DURATION = 500;
    //    private AnimatorSet mAnimatorSet = null;
    private TAnimatorHelperListener mListener;

    public TAnimationHelper(TAnimatorHelperListener listener) {
        mListener = listener;
    }

    public void changeColumns(final Collection<TTableAdapter.ViewHolder> columnHolders, int fromColumnIndex, int toColumnIndex, Animator.AnimatorListener listener) {
        ValueAnimator shiftViewsAnimator = null;
        if (!columnHolders.isEmpty()) {

            int from = 0;
            int to = 0;
            if (fromColumnIndex < toColumnIndex) {
                // shift left to right
                to = mListener.getColumnWidth(fromColumnIndex);
            } else {
                // shift right to left
                to = -mListener.getColumnWidth(toColumnIndex);
            }

            shiftViewsAnimator = generateAnimator(from, to);
            shiftViewsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    for (TTableAdapter.ViewHolder viewHolder : columnHolders) {
                        View view = viewHolder.getItemView();
                        view.setTranslationX(value);
                    }
//                    TTableAdapter.ViewHolder header = getColumnHeaderViewHolder(columnHeaders, fromColumn);
//                    if (header != null) {
//                        View view = header.getItemView();
//                        view.setTranslationX(value);
//                    }
                }
            });
            shiftViewsAnimator.setDuration(ANIMATION_DURATION);
            shiftViewsAnimator.start();
        }

    }

    private ValueAnimator generateAnimator(int fromValue, int toValue) {
        ValueAnimator animator = new ValueAnimator();
        animator.setIntValues(fromValue, toValue);
        animator.setDuration(ANIMATION_DURATION);
        return animator;
    }


    interface TAnimatorHelperListener {
        int getColumnWidth(int position);

        int getColumnsWidth(int startPosition, int count);
    }
}
