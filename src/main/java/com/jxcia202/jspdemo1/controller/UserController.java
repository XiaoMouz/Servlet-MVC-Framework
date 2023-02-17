package com.jxcia202.jspdemo1.controller;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

import com.jxcia202.jspdemo1.bean.User;
import com.jxcia202.jspdemo1.bean.account.*;
import com.jxcia202.jspdemo1.bean.users.Reader;
import com.jxcia202.jspdemo1.bean.users.UserLevel;
import com.jxcia202.jspdemo1.framework.GetMapping;
import com.jxcia202.jspdemo1.framework.ModelAndView;
import com.jxcia202.jspdemo1.framework.PostMapping;
import com.jxcia202.jspdemo1.util.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    Connection remote = ConnectionUtil.getConnection();
    private Map<String, User> userDatabase = new HashMap<>() {
        {
            try{
                SiteSetUtil.getDBUsers().forEach(user -> {put(user.getUsername(),user);});
            }catch (Exception e){
                logger.error("Failed in 'UserController' class, the 'userDatabase' is null.");
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

    private void updateProfile(ProfileBean user) throws SQLException{
        String sql = "update user set email = ?,password = ?, avatarLink =? where username = ?";
        PreparedStatement statement = remote.prepareStatement(sql);
        statement.setString(1,user.email);
        statement.setString(2,user.password);
        statement.setString(3,user.avatarLink);
        statement.setString(4,user.username);
    }

    private ModelAndView resetSent(ResetBean bean, HttpServletResponse response, String trackID, User user) throws IOException {
        bean.trackID = trackID;
        bean.requestDate = new Date();
        bean.user = user;
        MailSystemUtil.sendMail(user.getEmail(), "Recovery Account", "Your verify Code is " + trackID + ",Please enter it in 10 minutes");
        JsonUtil.responseJson(response, JsonType.SUCCESS, "Reset password email sent");
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
            JsonUtil.responseJson(response, JsonType.ERROR, "Username or password is incorrect");
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
        JsonUtil.responseJson(response, JsonType.SUCCESS, "Login success");
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
            JsonUtil.responseJson(response, JsonType.ERROR, "Invalid request");
        }
        // 检查用户名是否为纯字母数字
        if (!bean.username.matches("[a-zA-Z0-9]+")) {
            JsonUtil.responseJson(response, JsonType.ERROR, "Username must be alphanumeric");
            return null;
        }
        // 检查邮箱格式是否正确
        if (!bean.email.matches("[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+")) {
            JsonUtil.responseJson(response, JsonType.ERROR, "Invalid email address");
            return null;
        }

        User user = userDatabase.get(bean.username);
        if (user != null) {
            JsonUtil.responseJson(response, JsonType.ERROR, "Username already exists");
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
                JsonUtil.responseJson(response, JsonType.SUCCESS, "Register success");
            }
        } catch (SQLException e) {
            JsonUtil.responseJson(response, JsonType.ERROR, "Register failed, Server have problem");
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
                JsonUtil.responseJson(response, JsonType.ERROR, "Username does not exist");
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
            JsonUtil.responseJson(response, JsonType.ERROR, "Email does not exist");
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
            JsonUtil.responseJson(response, JsonType.ERROR, "Invalid request");
            return null;
        }
        if (!bean.trackID.equals(resetBean.trackID)) {
            JsonUtil.responseJson(response, JsonType.ERROR, "Invalid verify code");
            return null;
        }
        if (new Date().getTime() - resetBean.requestDate.getTime() > 600000) {
            JsonUtil.responseJson(response, JsonType.ERROR, "Verify time expired");
            return null;
        }
        User user = userDatabase.get(resetBean.input);
        user.setPassword(bean.password);
        user.setToken(EncryptionUtil.getMD5Result(bean.password));
        userDatabase.put(user.getUsername(), user);
        try {
            updateUserPassword(user);
            JsonUtil.responseJson(response, JsonType.SUCCESS, "Reset password success");
            return null;
        }catch (SQLException e) {
            JsonUtil.responseJson(response, JsonType.ERROR, "Reset password failed, Server have problem");
            return null;
        }
    }

    @GetMapping("/profile")
    public ModelAndView profile(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:./login");
        }
        return new ModelAndView("user/profile.html", "user", user);
    }

    @PostMapping("/profile")
    public ModelAndView profile(ProfileBean bean, HttpServletResponse response, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            JsonUtil.responseJson(response, JsonType.ERROR, "Invalid request: Not Login");
            return null;
        }
        if(user.getLevel()!= UserLevel.ADMINISTRATOR&&user.getUsername()    != bean.username){
            JsonUtil.responseJson(response, JsonType.ERROR, "Invalid request: No access");
            return null;
        }
        if(user.getUsername()!=bean.username){
            User match = userDatabase.get(bean.username);
            if(match==null){
                JsonUtil.responseJson(response, JsonType.ERROR, "Invalid request: Unknown User");
                return null;
            }
        }
        if (bean.password == null) {
            bean.password = user.getPassword();
        }
        if (bean.email == null) {
            bean.email = user.getEmail();
        }
        if (bean.avatarLink == null){
            bean.avatarLink = user.getAvatarLink();
        }
        try{
            updateProfile(bean);
            if(user.getUsername()!=bean.username)
                user = userDatabase.get(bean.username);
            user.setPassword(bean.password);
            user.setEmail(bean.email);
            user.setAvatarLink(bean.avatarLink);
            userDatabase.replace(bean.username, user);
            session.setAttribute("user", user);
            JsonUtil.responseJson(response,JsonType.SUCCESS,"Update User profile");
        } catch (SQLException e) {
            logger.error(e.toString());
        }
        return null;
    }
}
