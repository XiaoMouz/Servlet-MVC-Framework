package com.jxcia202.jspdemo1.controller;

import com.jxcia202.jspdemo1.bean.User;
import com.jxcia202.jspdemo1.framework.GetMapping;
import com.jxcia202.jspdemo1.framework.ModelAndView;
import jakarta.servlet.http.HttpSession;

public class IndexController {
    @GetMapping("/")
    public ModelAndView index(HttpSession session) {
        // todo: "user login requesting logic"
//        User user = (User) session.getAttribute("user");
//        return new ModelAndView("/index.html", "user", user);
        return null;
    }
}
