package com.jxcia202.jspdemo1.bean.users;

import com.jxcia202.jspdemo1.bean.User;

import java.util.Date;

public class Editor extends User {
    final UserLevel level = UserLevel.EDITOR;

    public Editor(int id, String username, String email, String token, String registerIp, String lastLoginIp, Date lastLoginTime, Date registerTime) {
        super(id, username, email, token, registerIp, lastLoginIp, lastLoginTime, registerTime);
    }

    public UserLevel getLevel() {
        return level;
    }
}
