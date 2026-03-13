package com.syc.utils;

import java.io.File;
import java.io.FilenameFilter;

public class TiffFilenameFilter implements FilenameFilter {

	  public TiffFilenameFilter() {
		 
	  }

	  public boolean accept(File directory, String fileName) {
	      return fileName.toLowerCase().endsWith(".tif");
	  }

}
