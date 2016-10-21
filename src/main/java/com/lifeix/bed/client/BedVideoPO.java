package com.lifeix.bed.client;

/**
 * Created by neoyin on 16/9/28.
 */
public class BedVideoPO {

    private String htmlUrl;

    private BedImagePO img;

    private String url;

    @Override
    public String toString() {
        return "BedVideoPO{" +
                "htmlUrl='" + htmlUrl + '\'' +
                ", img=" + img +
                ", url='" + url + '\'' +
                '}';
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public BedImagePO getImg() {
        return img;
    }

    public void setImg(BedImagePO img) {
        this.img = img;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public BedVideoPO() {

    }

    public BedVideoPO(String htmlUrl, BedImagePO img, String url) {

        this.htmlUrl = htmlUrl;
        this.img = img;
        this.url = url;
    }
}
