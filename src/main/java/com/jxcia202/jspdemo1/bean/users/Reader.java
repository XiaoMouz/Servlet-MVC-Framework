package com.jxcia202.jspdemo1.bean.users;

import com.jxcia202.jspdemo1.bean.User;

import java.util.Date;

public class Reader extends User {
    final UserLevel level = UserLevel.READER;

    public Reader(int id, String username, String password, String email, String token, String registerIp, String lastLoginIp, Date lastLoginTime, Date registerTime) {
        super(id, username, password, email, token, registerIp, lastLoginIp, lastLoginTime, registerTime);
    }

    public UserLevel getLevel() {
        return level;
    }
}
