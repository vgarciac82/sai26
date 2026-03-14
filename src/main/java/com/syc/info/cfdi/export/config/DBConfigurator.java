package com.syc.info.cfdi.export.config;

import java.util.Properties;
import com.syc.cfdi.util.CFDIUtils;
import java.util.Base64;

public class DBConfigurator {

    private String userName;

    private String password;

    private String driverClassName;

    private String url;

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
        Properties dbProperties = CFDIUtils.loadFileProperties(propertiesFilePath);
        dbConfig.setDriverClassName(dbProperties.getProperty("driverClassName"));
        dbConfig.setPassword(dbProperties.getProperty("password"));
        dbConfig.setUrl(dbProperties.getProperty("url"));
        dbConfig.setUserName(dbProperties.getProperty("userName"));
        return dbConfig;
    }
}
