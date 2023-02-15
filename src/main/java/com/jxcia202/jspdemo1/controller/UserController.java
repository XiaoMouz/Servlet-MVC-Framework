package com.jxcia202.jspdemo1.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jxcia202.jspdemo1.bean.User;
import com.jxcia202.jspdemo1.framework.GetMapping;
import com.jxcia202.jspdemo1.framework.ModelAndView;
import com.jxcia202.jspdemo1.framework.PostMapping;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
public class UserController {

    private Map<String, User> userDatabase = new HashMap<>() {
        {
//            List<User> users = List.of( //
//                    new User("bob@example.com", "bob123", "Bob", "This is bob."), new User("tom@example.com", "tomcat", "Tom", "This is tom."));
//            users.forEach(user -> {
//                put(user.email, user);
//            });
            // todo: "get user from database"
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
