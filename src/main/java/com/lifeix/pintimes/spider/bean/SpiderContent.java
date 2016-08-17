package com.lifeix.pintimes.spider.bean;

/**
 * Created by neoyin on 15/11/9.
 */
public class SpiderContent {

    private String html;

    private Long post_author;

    private String post_content;

    private String post_title;

    private String post_excerpt;

    private String post_content_filtered;

    private String source_url;

    private double weight;

    private Long categoryId;


    public SpiderContent() {
    }


    public SpiderContent(String html, Long post_author,Long categoryId, String post_content, String post_title, String source_url, double weight) {
        this.html = html;
        this.post_author = post_author;
        this.categoryId = categoryId;
        this.post_content = post_content;
        this.post_title = post_title;
        this.source_url = source_url;
        this.weight = weight;
    }

    public SpiderContent(String html, Long post_author, String post_content, String post_title, String post_excerpt, String post_content_filtered, String source_url, double weight) {
        this.html = html;
        this.post_author = post_author;
        this.post_content = post_content;
        this.post_title = post_title;
        this.post_excerpt = post_excerpt;
        this.post_content_filtered = post_content_filtered;
        this.source_url = source_url;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "SpiderContent{" +
                ", post_author=" + post_author +
                ", post_content='" + post_content + '\'' +
                ", post_title='" + post_title + '\'' +
                ", post_excerpt='" + post_excerpt + '\'' +
                ", post_content_filtered='" + post_content_filtered + '\'' +
                ", source_url='" + source_url + '\'' +
                ", weight=" + weight +
                '}';
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public Long getPost_author() {
        return post_author;
    }

    public void setPost_author(Long post_author) {
        this.post_author = post_author;
    }

    public String getPost_content() {
        return post_content;
    }

    public void setPost_content(String post_content) {
        this.post_content = post_content;
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getPost_excerpt() {
        return post_excerpt;
    }

    public void setPost_excerpt(String post_excerpt) {
        this.post_excerpt = post_excerpt;
    }

    public String getPost_content_filtered() {
        return post_content_filtered;
    }

    public void setPost_content_filtered(String post_content_filtered) {
        this.post_content_filtered = post_content_filtered;
    }

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    //ID, post_author, post_date, post_date_gmt, post_content, post_title, post_excerpt, post_status, comment_status, ping_status, post_password, post_name, to_ping, pinged, post_modified, post_modified_gmt, post_content_filtered, post_parent, guid, menu_order, post_type, post_mime_type, comment_count, view_count, source_url, weight

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
