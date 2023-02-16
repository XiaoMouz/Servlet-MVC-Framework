package com.jxcia202.jspdemo1.bean.account;

public class RegisterBean {
    public String username;
    public String email;
    public String password;

    @Override
    public String toString() {
        return "RegisterBean{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
