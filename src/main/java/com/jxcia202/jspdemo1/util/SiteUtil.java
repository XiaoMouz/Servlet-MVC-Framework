package com.jxcia202.jspdemo1.util;

import com.jxcia202.jspdemo1.bean.SinglePost;
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

public class SiteUtil {
    public static ArrayList<User> getDBUsers() throws SQLException {
        Connection remote = ConnectionUtil.getConnection();
        ArrayList<User> onlineUsers = new ArrayList<>();
        assert remote != null;
        PreparedStatement statement = remote.prepareStatement("select * from user");
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()){
            switch(resultSet.getString("userlevel")){
                case "ADMINISTRATOR": onlineUsers.add(new Administrator(resultSet.getInt("id"),resultSet.getString("username"),resultSet.getString("password"),resultSet.getString("email"),resultSet.getString("token"),resultSet.getString("registerIp"),resultSet.getString("lastLoginIp"),resultSet.getDate("lastLoginTime"),resultSet.getDate("registerTime")));break;
                case "EDITOR": onlineUsers.add(new Editor(resultSet.getInt("id"),resultSet.getString("username"),resultSet.getString("password"),resultSet.getString("email"),resultSet.getString("token"),resultSet.getString("registerIp"),resultSet.getString("lastLoginIp"),resultSet.getDate("lastLoginTime"),resultSet.getDate("registerTime")));break;
                case "MAINTAINER": onlineUsers.add(new Maintainer(resultSet.getInt("id"),resultSet.getString("username"),resultSet.getString("password"),resultSet.getString("email"),resultSet.getString("token"),resultSet.getString("registerIp"),resultSet.getString("lastLoginIp"),resultSet.getDate("lastLoginTime"),resultSet.getDate("registerTime")));break;
                default: onlineUsers.add(new Reader(resultSet.getInt("id"),resultSet.getString("username"),resultSet.getString("password"),resultSet.getString("email"),resultSet.getString("token"),resultSet.getString("registerIp"),resultSet.getString("lastLoginIp"),resultSet.getDate("lastLoginTime"),resultSet.getDate("registerTime")));break;
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

    public static HashMap<Integer, SinglePost> getAllPosts() throws SQLException{
        Connection remote = ConnectionUtil.getConnection();
        HashMap<Integer,SinglePost> posts = new HashMap<>();
        PreparedStatement statement = remote.prepareStatement("select * from posts");
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()){
            posts.put(resultSet.getInt("postid"),new SinglePost(
                    resultSet.getInt("postid"),
                    resultSet.getString("title"),
                    resultSet.getString("content"),
                    resultSet.getDate("releaseDate"),
                    resultSet.getDate("updateDate"),
                    resultSet.getInt("authorId"),
                    resultSet.getString("authorname"),
                    resultSet.getString("headImgLink")
                    ));
        }
        return posts;
    }

    public static User findUser(String username){
        try {
            Connection remote = ConnectionUtil.getConnection();
            assert remote != null;
            PreparedStatement statement = remote.prepareStatement("select * from user where username = ?");
            statement.setString(1,username);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                switch(resultSet.getString("userlevel")){
                    case "ADMINISTRATOR": return new Administrator(resultSet.getInt("id"),resultSet.getString("username"),resultSet.getString("password"),resultSet.getString("email"),resultSet.getString("token"),resultSet.getString("registerIp"),resultSet.getString("lastLoginIp"),resultSet.getDate("lastLoginTime"),resultSet.getDate("registerTime"));
                    case "EDITOR": return new Editor(resultSet.getInt("id"),resultSet.getString("username"),resultSet.getString("password"),resultSet.getString("email"),resultSet.getString("token"),resultSet.getString("registerIp"),resultSet.getString("lastLoginIp"),resultSet.getDate("lastLoginTime"),resultSet.getDate("registerTime"));
                    case "MAINTAINER": return new Maintainer(resultSet.getInt("id"),resultSet.getString("username"),resultSet.getString("password"),resultSet.getString("email"),resultSet.getString("token"),resultSet.getString("registerIp"),resultSet.getString("lastLoginIp"),resultSet.getDate("lastLoginTime"),resultSet.getDate("registerTime"));
                    case "READER": return new Reader(resultSet.getInt("id"),resultSet.getString("username"),resultSet.getString("password"),resultSet.getString("email"),resultSet.getString("token"),resultSet.getString("registerIp"),resultSet.getString("lastLoginIp"),resultSet.getDate("lastLoginTime"),resultSet.getDate("registerTime"));
                    default: return null;
                }
            }else{
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
