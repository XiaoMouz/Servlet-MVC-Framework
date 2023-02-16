package com.jxcia202.jspdemo1.bean;

import java.util.Date;

public class SinglePost {
    private int postid;
    private String content;
    private Date releaseDate;
    private Date updateDate;
    private User author;
    private String headImgLink;

    public int getPostid() {
        return postid;
    }

    public void setPostid(int postid) {
        this.postid = postid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getHeadImgLink() {
        return headImgLink;
    }

    public void setHeadImgLink(String headImgLink) {
        this.headImgLink = headImgLink;
    }
}
