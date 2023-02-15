package com.jxcia202.jspdemo1.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import com.jxcia202.jspdemo1.bean.User;
import com.jxcia202.jspdemo1.bean.users.Administrator;
import com.jxcia202.jspdemo1.bean.users.Editor;
import com.jxcia202.jspdemo1.bean.users.Maintainer;
import com.jxcia202.jspdemo1.bean.users.Reader;
import com.jxcia202.jspdemo1.framework.GetMapping;
import com.jxcia202.jspdemo1.framework.ModelAndView;
import com.jxcia202.jspdemo1.framework.PostMapping;
import com.jxcia202.jspdemo1.jdbc.ConnectionFactory;
import com.sun.tools.javac.Main;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
public class UserController {

    private Map<String, User> userDatabase = new HashMap<>() {
        {
            try{
                ArrayList<User> onlineUsers = new ArrayList<User>();
                Connection remote = ConnectionFactory.getConnection();
                PreparedStatement statement = remote.prepareStatement("select * from user");
                ResultSet resultSet = statement.executeQuery();
                while(resultSet.next()){
                        switch(resultSet.getString("userlevel")){
                            case "ADMINISTRATOR": onlineUsers.add(new Administrator(resultSet.getInt("id"),resultSet.getString("username"),resultSet.getString("email"),resultSet.getString("token"),resultSet.getString("registerIp"),resultSet.getString("lastLoginIp"),resultSet.getDate("lastLoginTime"),resultSet.getDate("registerTime")));break;
                            case "EDITOR": onlineUsers.add(new Editor(resultSet.getInt("id"),resultSet.getString("username"),resultSet.getString("email"),resultSet.getString("token"),resultSet.getString("registerIp"),resultSet.getString("lastLoginIp"),resultSet.getDate("lastLoginTime"),resultSet.getDate("registerTime")));break;
                            case "MAINTAINER": onlineUsers.add(new Maintainer(resultSet.getInt("id"),resultSet.getString("username"),resultSet.getString("email"),resultSet.getString("token"),resultSet.getString("registerIp"),resultSet.getString("lastLoginIp"),resultSet.getDate("lastLoginTime"),resultSet.getDate("registerTime")));break;
                            case "READER": onlineUsers.add(new Reader(resultSet.getInt("id"),resultSet.getString("username"),resultSet.getString("email"),resultSet.getString("token"),resultSet.getString("registerIp"),resultSet.getString("lastLoginIp"),resultSet.getDate("lastLoginTime"),resultSet.getDate("registerTime")));break;
                            default: break;
                        }
                }
                onlineUsers.forEach(user -> {put(user.getUsername(),user);});
            }catch (Exception e){
                List<User> offlineUsers = List.of(
                        new Administrator(1,"XiaoMouz","gxiaomouz@gmail.com","114514","127.0.0.1","127.0.0.1",new Date(),new Date()),
                        new Editor(2,"test","test@gmail.com","11451419","127.0.0.1","127.0.0.1",new Date(),new Date()),
                        new Reader(3,"a reader","reader@gmail.com","1919810","127.0.0.1","127.0.0.1",new Date(),new Date())
                );
                offlineUsers.forEach(user -> {put(user.getUsername(),user);});
            }
        }
    };

    @GetMapping("/signin")
    public ModelAndView signin() {
        return new ModelAndView("/signin.html");
    }

    // todo: "sign in logic"
//    @PostMapping("/signin")
//    public ModelAndView doSignin(SignInBean bean, HttpServletResponse response, HttpSession session) throws IOException {
//        User user = userDatabase.get(bean.email);
//        if (user == null || !user.password.equals(bean.password)) {
//            response.setContentType("application/json");
//            PrintWriter pw = response.getWriter();
//            pw.write("{\"error\":\"Bad email or password\"}");
//            pw.flush();
//        } else {
//            session.setAttribute("user", user);
//            response.setContentType("application/json");
//            PrintWriter pw = response.getWriter();
//            pw.write("{\"result\":true}");
//            pw.flush();
//        }
//        return null;
//    }

    @GetMapping("/signout")
    public ModelAndView signout(HttpSession session) {
        session.removeAttribute("user");
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/user/profile")
    public ModelAndView profile(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/signin");
        }
        return new ModelAndView("/profile.html", "user", user);
    }
}
