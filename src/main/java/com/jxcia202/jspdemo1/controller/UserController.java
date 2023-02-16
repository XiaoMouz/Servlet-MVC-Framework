package com.jxcia202.jspdemo1.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import java.util.Date;

import com.jxcia202.jspdemo1.bean.User;
import com.jxcia202.jspdemo1.bean.account.LoginBean;
import com.jxcia202.jspdemo1.bean.account.RegisterBean;
import com.jxcia202.jspdemo1.bean.users.Administrator;
import com.jxcia202.jspdemo1.bean.users.Editor;
import com.jxcia202.jspdemo1.bean.users.Maintainer;
import com.jxcia202.jspdemo1.bean.users.Reader;
import com.jxcia202.jspdemo1.framework.GetMapping;
import com.jxcia202.jspdemo1.framework.ModelAndView;
import com.jxcia202.jspdemo1.framework.PostMapping;
import com.jxcia202.jspdemo1.jdbc.ConnectionFactory;
import com.jxcia202.jspdemo1.util.EncryptionUtil;
import com.sun.tools.javac.Main;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
public class UserController {
    Connection remote = ConnectionFactory.getConnection();
    private  ArrayList<User> getDBUsers() throws SQLException {
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
    private Map<String, User> userDatabase = new HashMap<>() {
        {
            try{
                getDBUsers().forEach(user -> {put(user.getUsername(),user);});
            }catch (Exception e){
                List<User> offlineUsers = List.of(
                        new Administrator(1,"XiaoMouz","123000","gxiaomouz@gmail.com","114514","127.0.0.1","127.0.0.1",new Date(),new Date()),
                        new Editor(2,"test", "woshinidie","test@gmail.com","11451419","127.0.0.1","127.0.0.1",new Date(),new Date()),
                        new Reader(3,"a reader","nimeimama","reader@gmail.com","1919810","127.0.0.1","127.0.0.1",new Date(),new Date())
                );
                offlineUsers.forEach(user -> {put(user.getUsername(),user);});
            }
        }

    };

    private boolean insertNewUser(User user) throws SQLException{
        String sql = "insert into user(username,password,email,token,registerIp,lastLoginIp,lastLoginTime,registerTime) values(?,?,?,?,?,?,?,?)";
        PreparedStatement statement = remote.prepareStatement(sql);
        statement.setString(1,user.getUsername());
        statement.setString(2,user.getPassword());
        statement.setString(3,user.getEmail());
        statement.setString(4,user.getToken());
        statement.setString(5,user.getRegisterIp());
        statement.setString(6,user.getLastLoginIp());
        statement.setDate(7,new java.sql.Date(user.getLastLoginTime().getTime()));
        statement.setDate(8,new java.sql.Date(user.getRegisterTime().getTime()));
        return statement.executeUpdate() > 0;
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("/login.html");
    }

    @PostMapping("/login")
    public ModelAndView login(LoginBean bean, HttpServletResponse response, HttpSession session) throws IOException {
        User user = userDatabase.get(bean.username);
        if (user == null || !user.getPassword().equals(bean.password)) {
            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.write("{\"error\":\"Bad username or password\"}");
            pw.flush();
        } else {
            session.setAttribute("user", user);
            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.write("{\"result\":true}");
            pw.flush();
        }
        return null;
    }

    @GetMapping("/logout")
    public ModelAndView logout(HttpSession session) {
        session.removeAttribute("user");
        return new ModelAndView("redirect:./");
    }

    @GetMapping("/register")
    public ModelAndView register(HttpSession session){
        return new ModelAndView("/register.html");
    }

    @PostMapping("/register")
    public ModelAndView register(RegisterBean bean, HttpServletResponse response, HttpSession session, HttpServletRequest request) throws IOException {
        if (bean == null || bean.username == null || bean.password == null || bean.email == null) {
            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.write("{\"error\":\" 403: Bean is null\"}");
            pw.flush();
        }
        User user = userDatabase.get(bean.username);
        if (user != null) {
            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.write("{\"error\":\"Username already exists\"}");
            pw.flush();
            return null;
        }
        // 将password使用md5二次加密作为token
        String token = EncryptionUtil.getMD5Result(bean.password);
        // 获取客户端Ip地址
        String remoteAddr = request.getRemoteAddr();
        user = new Reader(-1, bean.username, bean.password, bean.email, token, remoteAddr, remoteAddr, new Date(), new Date());
        userDatabase.put(user.getUsername(), user);
        try{
            if(insertNewUser(user)) {
                session.setAttribute("user", user);
                response.setContentType("application/json");
                PrintWriter pw = response.getWriter();
                pw.write("{\"result\":true}");
                pw.flush();
            }
        } catch (SQLException e) {
            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.write("{\"error\":\""+e.toString()+"\"}");
            pw.flush();
        }
        return null;
    }




    @GetMapping("/user/profile")
    public ModelAndView profile(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:./signin");
        }
        return new ModelAndView("/profile.html", "user", user);
    }
}
