package com.jxcia202.jspdemo1.controller;

import com.jxcia202.jspdemo1.bean.User;
import com.jxcia202.jspdemo1.bean.users.*;
import com.jxcia202.jspdemo1.framework.GetMapping;
import com.jxcia202.jspdemo1.framework.ModelAndView;
import com.jxcia202.jspdemo1.framework.PostMapping;
import com.jxcia202.jspdemo1.util.ConnectionUtil;
import com.jxcia202.jspdemo1.util.SiteSetUtil;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    Connection remote = ConnectionUtil.getConnection();
    private HashMap<String, String> settings = new HashMap<String, String>() {
        {
            try {
                HashMap<String, String> sets = SiteSetUtil.getSiteSetting();
                sets.forEach((k, v) -> {
                    this.put(k, v);
                });
            } catch (SQLException e) {
                logger.error("Failed in 'SettingController' class, the 'settingMap' is null.");
            }
        }
    };
    private Map<String, User> users = new HashMap<>() {
        {
            try{
                SiteSetUtil.getDBUsers().forEach(user -> {put(user.getUsername(),user);});
            }catch (Exception e){
                logger.error("Failed in 'SettingController' class, the 'users' is null.");
            }
        }
    };

    private boolean deleteUser(User user) throws SQLException{
        users.remove(user.getUsername());
        PreparedStatement statement = remote.prepareStatement("delete from user where id = ?");
        statement.setInt(1,user.getId());
        return statement.executeUpdate() == 1;
    }
    private boolean upgradeUser(User user, UserLevel level) throws SQLException{
        user.setLevel(level);
        users.replace(user.getUsername(),user);
        PreparedStatement statement = remote.prepareStatement("update user set userlevel = ? where id = ?");
        statement.setString(1,user.getLevel().toString());
        statement.setInt(2,user.getId());
        return statement.executeUpdate() == 1;
    }
    private boolean updateSetting(String name, String value) throws SQLException{
        settings.replace(name,value);
        PreparedStatement statement = remote.prepareStatement("update settings set value = ? where name = ?");
        statement.setString(1,value);
        statement.setString(2,name);
        return statement.executeUpdate() == 1;
    }

    @GetMapping("/general")
    public ModelAndView general(HttpSession session){
        if(session.getAttribute("user") == null)
            return new ModelAndView("/login.html");
        session.setAttribute("settings",settings);
        return new ModelAndView("/settings/general.html");
    }
}
