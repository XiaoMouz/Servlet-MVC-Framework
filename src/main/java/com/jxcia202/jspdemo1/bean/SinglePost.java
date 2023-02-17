package com.jxcia202.jspdemo1.bean;

import com.jxcia202.jspdemo1.util.SiteUtil;

import java.util.Date;

public class SinglePost {
    private int postid;
    private String title;
    private String content;
    private Date releaseDate;
    private Date updateDate;
    private User author;
    private String headImgLink;

    public SinglePost(int postid, String title, String content, java.sql.Date releaseDate, java.sql.Date updateDate, int authorId, String authorname, String headImgLink) {
        this.postid = postid;
        this.title = title;
        this.content = content;
        this.releaseDate = releaseDate;
        this.updateDate = updateDate;
        try{
            this.author = SiteUtil.findUser(authorname);
        }catch (Exception e){
            e.printStackTrace();
            this.author = null;
        }

        this.headImgLink = headImgLink;
    }

    public int getPostid() {
        return postid;
    }

    public void setPostid(int postid) {
        this.postid = postid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
