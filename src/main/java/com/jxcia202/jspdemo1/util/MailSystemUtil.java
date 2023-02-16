package com.jxcia202.jspdemo1.util;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.InputStream;
import java.util.Properties;


public class MailSystemUtil {
    private static String host;
    private static String username;
    private static String password;
    private static Properties properties = null;
    static{
        try{
            properties = System.getProperties();
            InputStream input = ConnectionUtil.class.getClassLoader().getResourceAsStream("mail.properties");
            properties.load(input);
            host = properties.getProperty("host");
            username = properties.getProperty("username");
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
        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host); // 邮件服务器
        properties.put("mail.smtp.auth", "true"); // 验证要求


        // 获取默认session对象
        Session session =  Session.getDefaultInstance(properties,new Authenticator(){
            public PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(username,password); //发件人邮件用户名、授权码
            }
        });

        try{
            // 创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);

            // Set From: 头部头字段
            message.setFrom(username);

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
