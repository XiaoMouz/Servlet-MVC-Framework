package com.jxcia202.jspdemo1.bean.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jxcia202.jspdemo1.bean.User;

import java.util.Date;

public class ResetBean {

    @JsonProperty("input")
    public String input;

    public ResetBean(String input){
        this.input = input;
    }
    public ResetBean(){}; // for Jackson

    public String trackID;
    public Date requestDate;
    public User user = null;

    public boolean isEmail(){
        return input.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
    }
}
