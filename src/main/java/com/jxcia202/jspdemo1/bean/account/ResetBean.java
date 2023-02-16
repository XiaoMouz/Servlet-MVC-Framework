package com.jxcia202.jspdemo1.bean.account;

import java.util.Date;

public class ResetBean {
    public String input;
    public ResetBean(String input){
        this.input = input;
    }
    private String trackID;
    private Date requestDate;

    public boolean isEmail(){
        return input.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
    }

    public String getTrackID() {
        return trackID;
    }

    public void setTrackID(String trackID) {
        this.trackID = trackID;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }
}
