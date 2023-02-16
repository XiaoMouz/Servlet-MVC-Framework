package com.jxcia202.jspdemo1.bean.users;

import com.jxcia202.jspdemo1.bean.User;

import java.util.Date;

public class Editor extends User {
    UserLevel level = UserLevel.EDITOR;

    public Editor(int id, String username, String password, String email, String token, String registerIp, String lastLoginIp, Date lastLoginTime, Date registerTime) {
        super(id, username, password, email, token, registerIp, lastLoginIp, lastLoginTime, registerTime);
    }

    @Override
    public UserLevel getLevel() {
        return level;
    }

    @Override
    public void setLevel(UserLevel level) {
        this.level = level;
    }
}
