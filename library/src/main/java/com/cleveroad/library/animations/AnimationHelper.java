package com.cleveroad.library.animations;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static com.cleveroad.library.adapter.TableAdapter.ViewHolder;

public class AnimationHelper {
    private static final long ANIMATION_DURATION = 500;
    private AnimatorSet mAnimatorSet = null;
    private AnimatorHelperListener mListener;

    public AnimationHelper(AnimatorHelperListener listener) {
        mListener = listener;
    }

    public void changeColumns(final List<List<ViewHolder>> bodyViewHolders, final List<ViewHolder> columnHeaders,
                              final int fromColumn, final int toColumn, Animator.AnimatorListener listener) {
        if (mAnimatorSet != null) {
            mAnimatorSet.end();
            mAnimatorSet.removeAllListeners();
        }
        mAnimatorSet = new AnimatorSet();
        final List<Animator> animators = new ArrayList<>(2);

        List<ViewHolder> fromViewHolders = getColumnViewHolders(bodyViewHolders, fromColumn);
        ValueAnimator fromAnimator = null;
        ValueAnimator shiftViewsAnimator = null;
        if (!fromViewHolders.isEmpty()) {

            int from = 0;
            int to = 0;
            if (fromColumn < toColumn) {
                to = mListener.getColumnsWidth(fromColumn + 1, toColumn - fromColumn + 1) - mListener.getColumnWidth(fromColumn + 1);
            } else {
                to = -mListener.getColumnsWidth(toColumn + 1, fromColumn - toColumn);
            }

            for (ViewHolder viewHolder : getColumnViewHolders(bodyViewHolders, fromColumn)) {
                View view = viewHolder.getItemView();
                view.bringToFront();
            }

            ViewHolder viewHolder = getColumnHeaderViewHolder(columnHeaders, fromColumn);
            if (viewHolder != null) {
                View view = viewHolder.getItemView();
                view.bringToFront();
            }

            fromAnimator = generateAnimator(from, to);
            fromAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    for (ViewHolder viewHolder : getColumnViewHolders(bodyViewHolders, fromColumn)) {
                        View view = viewHolder.getItemView();
                        view.setTranslationX(value);
                    }
                    ViewHolder header = getColumnHeaderViewHolder(columnHeaders, fromColumn);
                    if (header != null) {
                        View view = header.getItemView();
                        view.setTranslationX(value);
                    }
                }
            });
            animators.add(fromAnimator);
        }

        List<ViewHolder> shiftViewHolders = getShiftColumnViewHolders(bodyViewHolders, fromColumn, toColumn);
        if (!shiftViewHolders.isEmpty()) {
            int from = 0;
            int to = (int) Math.signum(fromColumn - toColumn) * mListener.getColumnWidth(fromColumn + 1);
            shiftViewsAnimator = generateAnimator(from, to);

            shiftViewsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    for (List<ViewHolder> vhs : bodyViewHolders) {
                        for (ViewHolder viewHolder : vhs) {
                            int column = viewHolder.getColumnIndex();
                            if ((column > fromColumn && column <= toColumn) ||
                                    (column >= toColumn && column < fromColumn)) {
                                View view = viewHolder.getItemView();
                                view.setTranslationX(value);
                            }
                        }
                    }

                    for (ViewHolder viewHolder : columnHeaders) {
                        int column = viewHolder.getColumnIndex();
                        if ((column > fromColumn && column <= toColumn) ||
                                (column >= toColumn && column < fromColumn)) {
                            View view = viewHolder.getItemView();
                            view.setTranslationX(value);
                        }
                    }
                }
            });
            animators.add(shiftViewsAnimator);
        }
        if (!animators.isEmpty()) {
            mAnimatorSet.playTogether(animators);
            mAnimatorSet.addListener(listener);
            mAnimatorSet.start();
        }

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

    @Nullable
    private ViewHolder getColumnHeaderViewHolder(List<ViewHolder> headerViewHolders, int position) {
        for (ViewHolder vh : headerViewHolders) {
            if (vh.getColumnIndex() == position) {
                return vh;
            }
        }
        return null;
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


    public interface AnimatorHelperListener {
        int getColumnWidth(int position);

        int getColumnsWidth(int startPosition, int count);
    }
}
