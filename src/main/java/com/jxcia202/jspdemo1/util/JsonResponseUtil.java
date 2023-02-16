package com.jxcia202.jspdemo1.util;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class JsonResponseUtil {
    public static void responseJson(HttpServletResponse response, JsonType type, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter pw = response.getWriter();
        switch(type){
            case SUCCESS:pw.write("{\"result\":\""+message +"\"}");break;
            case ERROR:pw.write("{\"error\":\""+message +"\"}");break;
            default:pw.write("{\"error\":\"Unknown error\"}");break;
        }
        pw.flush();
    }
}

