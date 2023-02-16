package com.jxcia202.jspdemo1.util;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.*;

import java.io.InputStream;
import java.util.Properties;


public class MailSystemUtil {
    private static String host;
    private static String username;
    private static String password;

    static{
        try{
            Properties properties = new Properties();
            InputStream input = ConnectionUtil.class.getClassLoader().getResourceAsStream("mail.properties");
            properties.load(input);
            host = properties.getProperty("host");
            username = properties.getProperty("user");
            password = properties.getProperty("password");
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 使用jakarata mail发送邮件
     * @param to email
     * @param subject 标题
     * @param content 内容
     */
    public static void sendMail(String to, String subject, String content) {
        // 属性
        Properties properties = System.getProperties();
        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host); // 邮件服务器
        properties.put("mail.smtp.auth", "true"); // 验证要求
        properties.setProperty("mail.user", username); // 邮件验证用户名
        properties.setProperty("mail.password", password); // 邮件验证密码


        // 获取默认session对象
        Session session = Session.getDefaultInstance(properties);

        try{
            // 创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);

            // Set From: 头部头字段
            message.setFrom(new InternetAddress(username));

            // Set To: 头部头字段
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));

            // Set Subject: 头部头字段
            message.setSubject(subject);

            // 设置消息体
            message.setText(content);

            // 发送消息
            Transport.send(message);
            System.out.println("Sent message successfully to" + to);
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
