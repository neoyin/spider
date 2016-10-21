package com.lifeix.bed.client;

/**
 * Created by neoyin on 16/9/28.
 */
public class BedImagePO {

    private String w;
    private String h;
    private String path;

    public String getW() {
        return w;
    }

    public void setW(String w) {
        this.w = w;
    }

    public String getH() {
        return h;
    }

    public void setH(String h) {
        this.h = h;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "BedImagePO{" +
                "w='" + w + '\'' +
                ", h='" + h + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    public BedImagePO(String w, String h, String path) {
        this.w = w;
        this.h = h;
        this.path = path;
    }

    public BedImagePO() {
    }
}
