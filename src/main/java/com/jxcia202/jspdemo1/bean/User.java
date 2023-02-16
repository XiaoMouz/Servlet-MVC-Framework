package com.jxcia202.jspdemo1.bean;

import com.jxcia202.jspdemo1.bean.users.Reader;
import com.jxcia202.jspdemo1.bean.users.UserLevel;

import java.util.Date;

public abstract class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private String token;
    private String registerIp;
    private String lastLoginIp;
    private Date lastLoginTime;
    private Date registerTime;
    private String avatarLink;

    public User(int id, String username, String password, String  email, String token, String registerIp, String lastLoginIp, Date lastLoginTime, Date registerTime) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.token = token;
        this.registerIp = registerIp;
        this.lastLoginIp = lastLoginIp;
        this.lastLoginTime = lastLoginTime;
        this.registerTime = registerTime;
    }

    public int getId(){
        return this.id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRegisterIp() {
        return registerIp;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }

    abstract public UserLevel getLevel();
    abstract public void setLevel(UserLevel level);
}
