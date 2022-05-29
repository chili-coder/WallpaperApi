package com.sohaghlab.wallpaperapi;

public class WallpaperModel {

    private int id;
    private String imageUrl;
    private String mediumUrl;
    private String orginalUrl;
    private String alt;

    public WallpaperModel() {
    }

    public WallpaperModel(int id, String imageUrl, String mediumUrl, String orginalUrl, String alt) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.mediumUrl = mediumUrl;
        this.orginalUrl = orginalUrl;
        this.alt = alt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMediumUrl() {
        return mediumUrl;
    }

    public void setMediumUrl(String mediumUrl) {
        this.mediumUrl = mediumUrl;
    }

    public String getOrginalUrl() {
        return orginalUrl;
    }

    public void setOrginalUrl(String orginalUrl) {
        this.orginalUrl = orginalUrl;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }
}
