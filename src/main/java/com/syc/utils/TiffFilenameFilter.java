package com.syc.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Base64;

public class TiffFilenameFilter implements FilenameFilter {

    public TiffFilenameFilter() {
    }

    public boolean accept(File directory, String fileName) {
        return fileName.toLowerCase().endsWith(".tif");
    }
}
