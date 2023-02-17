package com.jxcia202.jspdemo1.bean.account;

public class ProfileBean {
    public String username;
    public String email;
    public String password;
    public String newPassword;
    public String avatarLink;

    @Override
    public String toString() {
        return "UserBean{" +
                "username='" + username + '\'' +
                ", email=" + email +
                ", password='" + password + '\'' +
                ", newPassword='" + newPassword + '\'' +
                ", avatarLink='" + avatarLink + '\'' +
                '}';
    }
}
