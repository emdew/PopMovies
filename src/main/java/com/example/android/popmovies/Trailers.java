package com.example.android.popmovies;

public class Trailers {

    private String youtubeKey;

    public Trailers(String youtubeKey) {
        this.youtubeKey = youtubeKey;
    }

    public String getYoutubeKey() {
        return youtubeKey;
    }

    public void setYoutubeKey(String youtubeKey) {
        this.youtubeKey = youtubeKey;
    }
}
