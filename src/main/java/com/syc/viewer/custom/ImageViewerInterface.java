package com.syc.viewer.custom;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

public interface ImageViewerInterface {

	public void init(HttpServletRequest req, String jniName);

	public File[] getFilesList();
}
