package com.jxcia202.jspdemo1.jdbc;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class ConnectionFactory {
    private static String driver;
    private static String url;
    private static String user;
    private static String password;

    static{
        try{
            Properties properties = new Properties();
            InputStream input = ConnectionFactory.class.getClassLoader().getResourceAsStream("db.properties");
            properties.load(input);
            driver = properties.getProperty("driver");
            url = properties.getProperty("url");
            user = properties.getProperty("user");
            password = properties.getProperty("password");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static Connection getConnection(){
        try{
            Class.forName(driver);
            return DriverManager.getConnection(url,user,password);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
