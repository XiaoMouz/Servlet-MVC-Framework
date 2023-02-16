package com.jxcia202.jspdemo1.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import java.util.Date;

import com.jxcia202.jspdemo1.bean.User;
import com.jxcia202.jspdemo1.bean.account.LoginBean;
import com.jxcia202.jspdemo1.bean.account.RegisterBean;
import com.jxcia202.jspdemo1.bean.account.ResetBean;
import com.jxcia202.jspdemo1.bean.account.VerifyBean;
import com.jxcia202.jspdemo1.bean.users.Administrator;
import com.jxcia202.jspdemo1.bean.users.Editor;
import com.jxcia202.jspdemo1.bean.users.Maintainer;
import com.jxcia202.jspdemo1.bean.users.Reader;
import com.jxcia202.jspdemo1.framework.GetMapping;
import com.jxcia202.jspdemo1.framework.ModelAndView;
import com.jxcia202.jspdemo1.framework.PostMapping;
import com.jxcia202.jspdemo1.util.ConnectionUtil;
import com.jxcia202.jspdemo1.util.EncryptionUtil;
import com.jxcia202.jspdemo1.util.MailSystemUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    Connection remote = ConnectionUtil.getConnection();
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
    private ResetBean resetBean = null;

    private boolean insertNewUser(User user) throws SQLException{
        String sql = "insert into user(username,password,email,token,registerIp,lastLoginIp,lastLoginTime,registerTime) values(?,?,?,?,?,?,?,?)";
        PreparedStatement statement = remote.prepareStatement(sql);
        statement.setString(1,user.getUsername());
        statement.setString(2,user.getPassword());
        statement.setString(3,user.getEmail());
        statement.setString(4,user.getToken());
        statement.setString(5,user.getRegisterIp());
        statement.setString(6,user.getLastLoginIp());
        statement.setTimestamp(7,new java.sql.Timestamp(user.getLastLoginTime().getTime()));
        statement.setTimestamp(8,new java.sql.Timestamp(user.getRegisterTime().getTime()));
        return statement.executeUpdate() > 0;
    }

    private void updateUserLoginTimeAndIp(User user) throws SQLException{
        String sql = "update user set lastLoginIp = ?,lastLoginTime = ? where username = ?";
        PreparedStatement statement = remote.prepareStatement(sql);
        statement.setString(1,user.getLastLoginIp());
        statement.setTimestamp(2,new java.sql.Timestamp(user.getLastLoginTime().getTime()));
        statement.setString(3,user.getUsername());
    }

    private void updateUserPassword(User user) throws SQLException{
        String sql = "update user set password = ? where username = ?";
        PreparedStatement statement = remote.prepareStatement(sql);
        statement.setString(1,user.getPassword());
        statement.setString(2,user.getUsername());
        statement.executeUpdate();
    }

    private ModelAndView resetSent(ResetBean bean, HttpServletResponse response, String trackID, User user) throws IOException {
        bean.trackID = trackID;
        bean.requestDate = new Date();
        bean.user = user;
        MailSystemUtil.sendMail(user.getEmail(), "Recovery Account", "Your verify Code is " + trackID + ",Please enter it in 10 minutes");
        response.setContentType("application/json");
        PrintWriter pw = response.getWriter();
        pw.write("{\"result\":true}");
        pw.flush();
        return null;
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("/user/login.html");
    }

    @PostMapping("/login")
    public ModelAndView login(LoginBean bean, HttpServletResponse response, HttpSession session, HttpServletRequest request) throws IOException {
        User user = userDatabase.get(bean.username);
        if (user == null || !user.getPassword().equals(bean.password)) {
            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.write("{\"error\":\"Bad username or password\"}");
            pw.flush();
            return null;
        }
        session.setAttribute("user", user);
        user.setLastLoginIp(request.getRemoteAddr());
        user.setLastLoginTime(new Date());
        try{
            updateUserLoginTimeAndIp(user);
        }catch (Exception e){
            logger.error(user.getUsername()+"update ip failed");
        }
        response.setContentType("application/json");
        PrintWriter pw = response.getWriter();
        pw.write("{\"result\":true}");
        pw.flush();
        return null;
    }

    @GetMapping("/logout")
    public ModelAndView logout(HttpSession session) {
        session.removeAttribute("user");
        return new ModelAndView("redirect:./");
    }

    @GetMapping("/register")
    public ModelAndView register(HttpSession session){
        return new ModelAndView("/user/register.html");
    }

    @PostMapping("/register")
    public ModelAndView register(RegisterBean bean, HttpServletResponse response, HttpSession session, HttpServletRequest request) throws IOException {
        if (bean == null || bean.username == null || bean.password == null || bean.email == null) {
            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.write("{\"error\":\" 403: Bean is null\"}");
            pw.flush();
        }
        // 检查用户名是否为纯字母数字
        if (!bean.username.matches("[a-zA-Z0-9]+")) {
            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.write("{\"error\":\"Username must be alphanumeric\"}");
            pw.flush();
            return null;
        }
        // 检查邮箱格式是否正确
        if (!bean.email.matches("[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+")) {
            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.write("{\"error\":\"Email format is incorrect\"}");
            pw.flush();
            return null;
        }
        // 检查密码是否符合6-16位
        if (bean.password.length() < 6 || bean.password.length() > 16) {
            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.write("{\"error\":\"Password must be 6-16 characters\"}");
            pw.flush();
            return null;
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

    @GetMapping("/reset")
    public ModelAndView reset() {
        return new ModelAndView("/user/reset.html");
    }

    @PostMapping("/reset")
    public ModelAndView reset(ResetBean bean, HttpServletResponse response) throws IOException {
        Random r = new Random();
        String trackID = String.valueOf(r.nextInt(100000000));
        resetBean = bean;
        if (!bean.isEmail()) {
            User user = userDatabase.get(bean.input);
            if (user == null) {
                response.setContentType("application/json");
                PrintWriter pw = response.getWriter();
                pw.write("{\"error\":\"Username does not exist\"}");
                pw.flush();
                return null;
            }
            return resetSent(bean, response, trackID, user);
        }
        User user = null;
        for (Map.Entry<String, User> entry : userDatabase.entrySet()) {
            if (entry.getValue().getEmail().equals(bean.input)) {
                user = entry.getValue();
                break;
            }
        }
        if (user == null) {
            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.write("{\"error\":\"Email does not exist\"}");
            pw.flush();
            return null;
        }
        return resetSent(bean, response, trackID, user);
    }

    @GetMapping("/verify")
    public ModelAndView verify() {
        if(resetBean == null){
            return new ModelAndView("redirect:./reset");
        }
        return new ModelAndView("/user/verify.html");
    }

    @PostMapping("/verify")
    public ModelAndView verify(VerifyBean bean, HttpServletResponse response) throws IOException {
        if (resetBean == null) {
            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.write("{\"error\":\"Please request reset first\"}");
            pw.flush();
            return null;
        }
        if (!bean.trackID.equals(resetBean.trackID)) {
            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.write("{\"error\":\"Verify code is incorrect\"}");
            pw.flush();
            return null;
        }
        if (new Date().getTime() - resetBean.requestDate.getTime() > 600000) {
            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.write("{\"error\":\"Verify code is expired\"}");
            pw.flush();
            return null;
        }
        User user = userDatabase.get(resetBean.input);
        user.setPassword(bean.password);
        user.setToken(EncryptionUtil.getMD5Result(bean.password));
        userDatabase.put(user.getUsername(), user);
        try {
            updateUserPassword(user);
            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.write("{\"result\":true}");
            pw.flush();
            return null;
        }catch (SQLException e) {
            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.write("{\"error\":\""+e.toString()+"\"}");
            pw.flush();
            return null;
        }
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
