package com.axtel.request;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Base64;

public class MultipartObject {

    Map<String, String> params = new LinkedHashMap<>();

    Map<String, File> files = new LinkedHashMap<>();

    /**
     * @return the params
     */
    public Map<String, String> getParams() {
        return params;
    }

    /**
     * @param params
     *            the params to set
     */
    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    /**
     * @return the files
     */
    public Map<String, File> getFiles() {
        return files;
    }

    /**
     * @param files
     *            the files to set
     */
    public void setFiles(Map<String, File> files) {
        this.files = files;
    }
}
