package com.cleveroad.library.animations;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static com.cleveroad.library.adapter.TableAdapter.ViewHolder;

public class AnimationHelper {
    private static final long ANIMATION_DURATION = 300;
    private AnimatorSet mAnimatorSet = null;
    private AnimatorHelperListener mListener;

    public AnimationHelper(AnimatorHelperListener listener) {
        mListener = listener;
    }

    void changeColumns(final List<List<ViewHolder>> bodyViewHolders, final List<ViewHolder> columnHeaders, final int fromColumn, final int toColumn) {
        if (mAnimatorSet != null) {
            mAnimatorSet.end();
        }

        List<ViewHolder> fromViewHolders = getColumnViewHolders(bodyViewHolders, fromColumn);

        if (!fromViewHolders.isEmpty()) {
            int from = 0;
            int to = mListener.getColumnsWidth(0, toColumn) - mListener.getColumnWidth(fromColumn);
            ValueAnimator animator = generateAnimator(from, to);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    for (ViewHolder viewHolder : getColumnViewHolders(bodyViewHolders, fromColumn)) {
                        View view = viewHolder.getItemView();
                        view.setTranslationX(value);
                    }
                }
                
            });
        }

        List<ViewHolder> shiftViewHolders = getShiftColumnViewHolders(bodyViewHolders, fromColumn, toColumn);
    }

    @NonNull
    private List<ViewHolder> getColumnViewHolders(List<List<ViewHolder>> bodyViewHolders, int position) {
        final List<ViewHolder> viewHolders = new ArrayList<>();
        for (List<ViewHolder> vhs : bodyViewHolders) {
            for (ViewHolder vh : vhs) {
                if (vh.getColumnIndex() == position) {
                    viewHolders.add(vh);
                }
            }
        }
        return viewHolders;
    }

    @NonNull
    private List<ViewHolder> getShiftColumnViewHolders(List<List<ViewHolder>> bodyViewHolders, int fromColumn, int toColumn) {
        final List<ViewHolder> viewHolders = new ArrayList<>();
        for (List<ViewHolder> vhs : bodyViewHolders) {
            for (ViewHolder viewHolder : vhs) {
                int column = viewHolder.getColumnIndex();
                if ((column > fromColumn && column <= toColumn) ||
                        (column >= toColumn && column < fromColumn)) {
                    viewHolders.add(viewHolder);
                }
            }
        }
        return viewHolders;
    }

    private ValueAnimator generateAnimator(int fromValue, int toValue) {
        ValueAnimator animator = new ValueAnimator();
        animator.setIntValues(fromValue, toValue);
        animator.setDuration(ANIMATION_DURATION);
        return animator;
    }


    interface AnimatorHelperListener {
        int getColumnWidth(int position);

        int getColumnsWidth(int startPosition, int count);
    }
}
