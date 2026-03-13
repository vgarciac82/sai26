package com.axtel.web.utils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebUtils {

    private static final Logger log = LoggerFactory.getLogger(WebUtils.class);

    public static String findJNIName(ServletConfig config) {
        String jniName;
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        return jniName;
    }
}
