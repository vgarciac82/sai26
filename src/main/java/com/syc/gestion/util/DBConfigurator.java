package com.syc.gestion.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Base64;

public class DBConfigurator {

    private String userName;

    private String password;

    private String driverClassName;

    private String url;

    private int ejercicio;

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName
     *            the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the driverClassName
     */
    public String getDriverClassName() {
        return driverClassName;
    }

    /**
     * @param driverClassName
     *            the driverClassName to set
     */
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    public static DBConfigurator instance(String propertiesFilePath) throws Exception {
        DBConfigurator dbConfig = new DBConfigurator();
        Properties dbProperties = loadFileProperties(propertiesFilePath);
        dbConfig.setDriverClassName(dbProperties.getProperty("driverClassName"));
        dbConfig.setPassword(dbProperties.getProperty("password"));
        dbConfig.setUrl(dbProperties.getProperty("url"));
        dbConfig.setUserName(dbProperties.getProperty("userName"));
        return dbConfig;
    }

    public static Properties loadFileProperties(String path) throws Exception {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(path);
            prop.load(input);
            return prop;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getEjercicio() {
        return ejercicio;
    }

    public void setEjercicio(int ejercicio) {
        this.ejercicio = ejercicio;
    }
}
