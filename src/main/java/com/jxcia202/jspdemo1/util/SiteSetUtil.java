package com.jxcia202.jspdemo1.util;

import com.jxcia202.jspdemo1.bean.User;
import com.jxcia202.jspdemo1.bean.users.Administrator;
import com.jxcia202.jspdemo1.bean.users.Editor;
import com.jxcia202.jspdemo1.bean.users.Maintainer;
import com.jxcia202.jspdemo1.bean.users.Reader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SiteSetUtil {
    public static ArrayList<User> getDBUsers() throws SQLException {
        Connection remote = ConnectionUtil.getConnection();
        ArrayList<User> onlineUsers = new ArrayList<User>();
        PreparedStatement statement = remote.prepareStatement("select * from user");
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()){
            switch(resultSet.getString("userlevel")){
                case "ADMINISTRATOR": onlineUsers.add(new Administrator(resultSet.getInt("id"),resultSet.getString("username"),resultSet.getString("password"),resultSet.getString("email"),resultSet.getString("token"),resultSet.getString("registerIp"),resultSet.getString("lastLoginIp"),resultSet.getDate("lastLoginTime"),resultSet.getDate("registerTime")));break;
                case "EDITOR": onlineUsers.add(new Editor(resultSet.getInt("id"),resultSet.getString("username"),resultSet.getString("password"),resultSet.getString("email"),resultSet.getString("token"),resultSet.getString("registerIp"),resultSet.getString("lastLoginIp"),resultSet.getDate("lastLoginTime"),resultSet.getDate("registerTime")));break;
                case "MAINTAINER": onlineUsers.add(new Maintainer(resultSet.getInt("id"),resultSet.getString("username"),resultSet.getString("password"),resultSet.getString("email"),resultSet.getString("token"),resultSet.getString("registerIp"),resultSet.getString("lastLoginIp"),resultSet.getDate("lastLoginTime"),resultSet.getDate("registerTime")));break;
                case "READER": onlineUsers.add(new Reader(resultSet.getInt("id"),resultSet.getString("username"),resultSet.getString("password"),resultSet.getString("email"),resultSet.getString("token"),resultSet.getString("registerIp"),resultSet.getString("lastLoginIp"),resultSet.getDate("lastLoginTime"),resultSet.getDate("registerTime")));break;
                default: break;
            }
        }
        return onlineUsers;
    }

    public static HashMap<String,String> getSiteSetting() throws SQLException {
        Connection remote = ConnectionUtil.getConnection();
        HashMap<String,String> settingMap = new HashMap<String,String>();
        PreparedStatement statement = remote.prepareStatement("select * from settings");
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()){
            settingMap.put(resultSet.getString("name"),resultSet.getString("value"));
        }
        return settingMap;
    }
}
