package com.AXCDevelopment.AnimeCharacterScreenOverlay;

public class AnimeCharacter {

    // name of the character
    private String name;
    // character tags, i.e. anime, gender
    private String[] tags;
    // location of images
    private String[][] imageLocation;
    // location of audio
    private String[][] audioLocation;

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

    public String[][] getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String[][] imageLocation) {
        this.imageLocation = imageLocation;
    }

    public String[][] getAudioLocation() {
        return audioLocation;
    }

    public void setAudioLocation(String[][] audioLocation) {
        this.audioLocation = audioLocation;
    }

}
