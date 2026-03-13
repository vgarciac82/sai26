package com.syc.applet.filters;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class AppletFirmaPDFFileFilterCer extends FileFilter {
	public String getDescription() {
		return "Certificado digital (*.cer)";
	}

	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		if (f.getName().endsWith(".cer")) {
			return true;
		}

		return false;
	}

}
