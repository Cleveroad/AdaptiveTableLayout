package com.cleveroad.tablelayout.adapter;

import com.bumptech.glide.Glide;
import com.cleveroad.library.LinkedTableAdapter;
import com.cleveroad.library.ViewHolderImpl;
import com.cleveroad.tablelayout.R;
import com.cleveroad.tablelayout.datasource.ArtistDataSource;
import com.cleveroad.tablelayout.model.ArtistModel;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ArtistLinkedTableAdapter extends LinkedTableAdapter<ViewHolderImpl> {
    private static final int COLUMN_PHOTO = 0;
    private static final int COLUMN_ARTIST = 1;
    private static final int COLUMN_SONG = 2;
    private static final int COLUMN_GENRES = 3;
    private static final int[] COLORS = new int[]{
            0xffe62a10, 0xffe91e63, 0xff9c27b0, 0xff673ab7, 0xff3f51b5,
            0xff5677fc, 0xff03a9f4, 0xff00bcd4, 0xff009688, 0xff259b24,
            0xff8bc34a, 0xffcddc39, 0xffffeb3b, 0xffffc107, 0xffff9800, 0xffff5722};

    private final LayoutInflater mLayoutInflater;
    private final ArtistDataSource mArtistDataSource;

    public ArtistLinkedTableAdapter(Context context, ArtistDataSource artistDataSource) {
        mLayoutInflater = LayoutInflater.from(context);
        mArtistDataSource = artistDataSource;
    }

    @Override
    public int getRowCount() {
        return mArtistDataSource.getRowsCount();
    }

    @Override
    public int getColumnCount() {
        return mArtistDataSource.getColumnsCount();
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateItemViewHolder(@NonNull ViewGroup parent) {
        return new TestViewHolder(mLayoutInflater.inflate(R.layout.item_card, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateColumnHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TestHeaderColumnViewHolder(mLayoutInflater.inflate(R.layout.item_header_column, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateRowHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TestHeaderRowViewHolder(mLayoutInflater.inflate(R.layout.item_header_row, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateLeftTopHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TestHeaderLeftTopViewHolder(mLayoutInflater.inflate(R.layout.item_header_left_top, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderImpl viewHolder, int row, int column) {
        TestViewHolder vh = (TestViewHolder) viewHolder;
        ArtistModel artistModel = mArtistDataSource.getArtist(row);
        String itemData = artistModel.getFieldByIndex(column).trim();

        switch (column) {
            case COLUMN_PHOTO:
                vh.tvText.setVisibility(View.GONE);
                vh.ivImage.setVisibility(View.VISIBLE);
                Glide.with(vh.ivImage.getContext())
                        .load(itemData)
                        .centerCrop()
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .into(vh.ivImage);
                break;
            case COLUMN_ARTIST:
            case COLUMN_SONG:
            case COLUMN_GENRES:
            default:
                vh.tvText.setVisibility(View.VISIBLE);
                vh.ivImage.setVisibility(View.GONE);
                vh.tvText.setText(itemData);
                break;
        }
    }

    @Override
    public void onBindHeaderColumnViewHolder(@NonNull ViewHolderImpl viewHolder, int column) {
        TestHeaderColumnViewHolder vh = (TestHeaderColumnViewHolder) viewHolder;

        vh.tvText.setText(mArtistDataSource.getColumnHeader(column));
        int color = COLORS[column % COLORS.length];
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{ColorUtils.setAlphaComponent(color, 30), 0x00000000});
        gd.setCornerRadius(0f);
        vh.vGradient.setBackground(gd);
        vh.vLine.setBackgroundColor(color);
    }

    @Override
    public void onBindHeaderRowViewHolder(@NonNull ViewHolderImpl viewHolder, int row) {
        TestHeaderRowViewHolder vh = (TestHeaderRowViewHolder) viewHolder;
        vh.tvText.setText(String.valueOf(row + 1));
    }

    @Override
    public void onBindLeftTopHeaderViewHolder(@NonNull ViewHolderImpl viewHolder) {
        TestHeaderLeftTopViewHolder vh = (TestHeaderLeftTopViewHolder) viewHolder;
        vh.tvText.setText(R.string.artists_header);
    }

    @Override
    public int getColumnWidth(int column) {
        switch (column) {
            case COLUMN_ARTIST:
                return 400;
            case COLUMN_GENRES:
                return 410;
            case COLUMN_PHOTO:
            case COLUMN_SONG:
                return 300;
            default:
                return 100;
        }
    }

    @Override
    public int getHeaderColumnHeight() {
        return 200;
    }

    @Override
    public int getRowHeight(int row) {
        return 400;
    }

    @Override
    public int getHeaderRowWidth() {
        return 300;
    }

    @Override
    public void onViewHolderRecycled(@NonNull ViewHolderImpl viewHolder) {
        //do nothing
    }

    //------------------------------------- view holders ------------------------------------------

    private static class TestViewHolder extends ViewHolderImpl {
        TextView tvText;
        ImageView ivImage;

        private TestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
        }
    }

    private static class TestHeaderColumnViewHolder extends ViewHolderImpl {
        TextView tvText;
        View vGradient;
        View vLine;

        private TestHeaderColumnViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
            vGradient = itemView.findViewById(R.id.vGradient);
            vLine = itemView.findViewById(R.id.vLine);
        }
    }

    private static class TestHeaderRowViewHolder extends ViewHolderImpl {
        TextView tvText;

        TestHeaderRowViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
        }
    }

    private static class TestHeaderLeftTopViewHolder extends ViewHolderImpl {
        TextView tvText;

        private TestHeaderLeftTopViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
        }
    }
}
