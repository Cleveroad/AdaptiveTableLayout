package com.cleveroad.tablelayout.model;

import com.cleveroad.tablelayout.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ArtistModel implements Serializable {
    private int mPosition;
    private String mPhotoUrl;
    private String mName;
    private String mSong;
    private List<String> mGenres;

    public ArtistModel() {
        mGenres = new ArrayList<>();
    }

    public ArtistModel(String photoUrl, String name, String song, List<String> genres) {
        mPhotoUrl = photoUrl;
        mName = name;
        mSong = song;
        mGenres = genres;
    }

    public ArtistModel(int position, String photoUrl, String name, String song, List<String> genres) {
        mPosition = position;
        mPhotoUrl = photoUrl;
        mName = name;
        mSong = song;
        mGenres = genres;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        mPhotoUrl = photoUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getSong() {
        return mSong;
    }

    public void setSong(String song) {
        mSong = song;
    }

    public List<String> getGenres() {
        return mGenres;
    }

    public void setGenres(List<String> genres) {
        mGenres = genres;
    }

    public String getFieldByIndex(int index) {
        switch (index) {
//            case 0:
//                return String.valueOf(mPosition);
            case 0:
                return mPhotoUrl;
            case 1:
                return mName;
            case 2:
                return mSong;
            case 3: {
                return StringUtils.toString(mGenres, ", ");
            }
            default:
                return null;
        }
    }
}
