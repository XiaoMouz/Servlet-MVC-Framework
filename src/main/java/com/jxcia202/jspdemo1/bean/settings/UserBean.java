package com.jxcia202.jspdemo1.bean.settings;

import com.jxcia202.jspdemo1.bean.users.UserLevel;

public class UserBean {
    public String username;
    public String userLevel;
    public String action;

    @Override
    public String toString() {
        return "UserBean{" +
                "username='" + username + '\'' +
                ", userLevel=" + userLevel +
                ", action='" + action + '\'' +
                '}';
    }
}
