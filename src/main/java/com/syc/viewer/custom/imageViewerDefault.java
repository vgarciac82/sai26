package com.syc.viewer.custom;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

public class imageViewerDefault implements ImageViewerInterface {

	public void init(HttpServletRequest req, String jniName) {
		// Nada que hacer
	}

	public File[] getFilesList() {
		return null;
	}
}
