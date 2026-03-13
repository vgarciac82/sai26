package com.syc.applet.filters;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class AppletFirmaPDFFileFilter extends FileFilter {
	public String getDescription() {
		return "Llave privada (*.pk)";
	}

	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		if (f.getName().endsWith(".pk")) {
			return true;
		}

		return false;
	}

}
