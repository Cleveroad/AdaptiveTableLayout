package com.cleveroad.tablelayout.model;

import com.cleveroad.tablelayout.utils.StringUtils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class SongModel implements Parcelable {
    public static final Creator<SongModel> CREATOR = new Creator<SongModel>() {
        @Override
        public SongModel createFromParcel(Parcel in) {
            return new SongModel(in);
        }

        @Override
        public SongModel[] newArray(int size) {
            return new SongModel[size];
        }
    };
    private String mCoverUrl;
    private String mArtistName;
    private String mSongName;
    private String mAlbumName;
    private String mTime;
    private List<String> mGenres;
    private int mChartRaiting;
    private long mVotesCount;

    public SongModel() {
    }

    public SongModel(String coverUrl, String artistName, String songName, String albumName,
                     String time, List<String> genres, int chartRaiting, long votesCount) {
        mCoverUrl = coverUrl;
        mArtistName = artistName;
        mSongName = songName;
        mAlbumName = albumName;
        mTime = time;
        mGenres = genres;
        mChartRaiting = chartRaiting;
        mVotesCount = votesCount;
    }

    protected SongModel(Parcel in) {
        mCoverUrl = in.readString();
        mArtistName = in.readString();
        mSongName = in.readString();
        mAlbumName = in.readString();
        mTime = in.readString();
        mGenres = in.createStringArrayList();
        mChartRaiting = in.readInt();
        mVotesCount = in.readLong();
    }

    public String getCoverUrl() {
        return mCoverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        mCoverUrl = coverUrl;
    }

    public String getArtistName() {
        return mArtistName;
    }

    public void setArtistName(String artistName) {
        mArtistName = artistName;
    }

    public String getSongName() {
        return mSongName;
    }

    public void setSongName(String songName) {
        mSongName = songName;
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public void setAlbumName(String albumName) {
        mAlbumName = albumName;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public List<String> getGenres() {
        return mGenres;
    }

    public void setGenres(List<String> genres) {
        mGenres = genres;
    }

    public int getChartRaiting() {
        return mChartRaiting;
    }

    public void setChartRaiting(int chartRaiting) {
        mChartRaiting = chartRaiting;
    }

    public long getVotesCount() {
        return mVotesCount;
    }

    public void setVotesCount(long votesCount) {
        mVotesCount = votesCount;
    }

    public String getFieldByIndex(int index) {
        switch (index) {
            case 0:
                return mCoverUrl;
            case 1:
                return mArtistName;
            case 2:
                return mSongName;
            case 3:
                return mAlbumName;
            case 4:
                return mTime;
            case 5:
                return StringUtils.toString(mGenres, ", ");
            case 6:
                return String.valueOf(mChartRaiting);
            case 7:
                return String.valueOf(mVotesCount);
            default:
                return null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCoverUrl);
        dest.writeString(mArtistName);
        dest.writeString(mSongName);
        dest.writeString(mAlbumName);
        dest.writeString(mTime);
        dest.writeStringList(mGenres);
        dest.writeInt(mChartRaiting);
        dest.writeLong(mVotesCount);
    }
}
