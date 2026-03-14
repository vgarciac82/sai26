package com.syc.viewer.custom;

import java.io.File;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Base64;

public class imageViewerDefault implements ImageViewerInterface {

    public void init(HttpServletRequest req, String jniName) {
        // Nada que hacer
    }

    public File[] getFilesList() {
        return null;
    }
}
