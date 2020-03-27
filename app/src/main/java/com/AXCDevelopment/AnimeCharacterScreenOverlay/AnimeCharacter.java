package com.AXCDevelopment.AnimeCharacterScreenOverlay;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

public class AnimeCharacter{

    // name of the character
    private String name;
    // character tags, i.e. anime, gender
    private String[] tags;
    // location of image when stationary
    private int imageStatLocation;
    // location of image when moving
    private int imageMoveLocation;
    // location of audio
    private int audioLocation;

    public AnimeCharacter(String name, String[] tags, int imageStatLocation, int imageMoveLocation, int audioLocation) {
        this.name = name;
        this.tags = tags;
        this.imageStatLocation = imageStatLocation;
        this.imageMoveLocation = imageMoveLocation;
        this.audioLocation = audioLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public Bitmap getImageStat() {
        return BitmapFactory.decodeResource(Resources.getSystem(), imageStatLocation);
    }

    public void setImageStatLocation(int imageStatLocation) {
        this.imageStatLocation = imageStatLocation;
    }

    public int getImageStatLocation() {
        return imageStatLocation;
    }

    public int getAudioLocation() {
        return audioLocation;
    }

    public void setAudioLocation(int audioLocation) {
        this.audioLocation = audioLocation;
    }

    @NonNull
    @Override
    public String toString() {
        return getName();
    }
}
