package com.lifeix.pintimes.spider.bean;

import java.util.List;

/**
 * Created by neoyin on 15/11/10.
 */
public class SpiderConfig {

    /**主页地址*/
    private String homePage;
    /**抓取地址*/
    private String parentUrl;

    /**图片存储位置*/
    private String imagePath;
    /**内容dom */
    private String descOffset;
    /**图片dom*/
    private String imageOffset;
    /**title dom*/
    private String titleOffset;
    /**过滤内容dom*/
    private List<String> filterOffset;

    /**如果有分页时*/
    private String pageOffset;

    private List<String> filterStr;
    /**字符集*/
    private String charSet;
    /**url存储位置保证链接唯一*/
    private String postPath;
    /**定时任务*/
    private String cronTigger;
    /**将要抓取的内容列表*/
    private String contentsOffset;
    /**尾部添加内容*/
    private String descDetal;
    /**发表内容用户ID*/
    private Long authorId;
    /**内容分类*/
    private Long categoryId;
    /**时间间隔*/
    private long timeSec ;

    private double weight;

    private String username;
    private String passwd;


    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public long getTimeSec() {
        return timeSec;
    }

    public void setTimeSec(long timeSec) {
        this.timeSec = timeSec;
    }

    public String getPageOffset() {
        return pageOffset;
    }

    public void setPageOffset(String pageOffset) {
        this.pageOffset = pageOffset;
    }

    public String getDescDetal() {
        return descDetal;
    }

    public void setDescDetal(String descDetal) {
        this.descDetal = descDetal;
    }
    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getContentsOffset() {
        return contentsOffset;
    }

    public void setContentsOffset(String contentsOffset) {
        this.contentsOffset = contentsOffset;
    }

    public List<String> getFilterOffset() {
        return filterOffset;
    }

    public void setFilterOffset(List<String> filterOffset) {
        this.filterOffset = filterOffset;
    }

    public List<String> getFilterStr() {
        return filterStr;
    }

    public void setFilterStr(List<String> filterStr) {
        this.filterStr = filterStr;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getParentUrl() {
        return parentUrl;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDescOffset() {
        return descOffset;
    }

    public void setDescOffset(String descOffset) {
        this.descOffset = descOffset;
    }

    public String getImageOffset() {
        return imageOffset;
    }

    public void setImageOffset(String imageOffset) {
        this.imageOffset = imageOffset;
    }

    public String getTitleOffset() {
        return titleOffset;
    }

    public void setTitleOffset(String titleOffset) {
        this.titleOffset = titleOffset;
    }


    public String getCharSet() {
        return charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    public String getPostPath() {
        return postPath;
    }

    public void setPostPath(String postPath) {
        this.postPath = postPath;
    }

    public String getCronTigger() {
        return cronTigger;
    }

    public void setCronTigger(String cronTigger) {
        this.cronTigger = cronTigger;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    @Override
    public String toString() {
        return "SpiderConfig{" +
                "homePage='" + homePage + '\'' +
                ", parentUrl='" + parentUrl + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", descOffset='" + descOffset + '\'' +
                ", imageOffset='" + imageOffset + '\'' +
                ", titleOffset='" + titleOffset + '\'' +
                ", filterOffset=" + filterOffset +
                ", pageOffset='" + pageOffset + '\'' +
                ", filterStr=" + filterStr +
                ", charSet='" + charSet + '\'' +
                ", postPath='" + postPath + '\'' +
                ", cronTigger='" + cronTigger + '\'' +
                ", contentsOffset='" + contentsOffset + '\'' +
                ", descDetal='" + descDetal + '\'' +
                ", authorId=" + authorId +
                '}';
    }


}
