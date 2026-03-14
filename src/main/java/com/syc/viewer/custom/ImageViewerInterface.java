package com.syc.viewer.custom;

import java.io.File;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Base64;

public interface ImageViewerInterface {

    public void init(HttpServletRequest req, String jniName);

    public File[] getFilesList();
}
