package com.syc.utils;

import java.util.StringTokenizer;

public class ToolBox {

	public static String getFileExtension2(String fileName) {
		return getFilePart(fileName, true);
	}

	public static String getFileName(String fileName) {
		return getFilePart(fileName, false);
	}

	public static String getFilePart(String fileName, boolean getLastToken) {
		String retVal = "";
		String firstPart = "";
		StringTokenizer st = new StringTokenizer(fileName, ".");
		while (st.hasMoreTokens()){
			firstPart += retVal;
			retVal = st.nextToken();
		}
		if (getLastToken) {
			return retVal;
		} else {
			return firstPart;
		}
	}
	
	public static String getFileExtension(String fileName) {
		String retVal = fileName;
		int dotPos = retVal.indexOf('.');
		while (dotPos>-1) {
			retVal = retVal.substring(dotPos+1);
			dotPos = retVal.indexOf('.');
		}
		return retVal.toLowerCase();
	}

	public static String removeFileExtension(String fileName) {
		String retVal = fileName;
		int dotPos1 = retVal.indexOf('.');
		int dotPos2 = -1;
		while (dotPos1>-1) {
			dotPos2 = dotPos1;
			retVal = retVal.substring(dotPos1+1);
			dotPos1 = retVal.indexOf('.');
		}
		return fileName.toLowerCase().substring(0,dotPos2);
	}
	
	

}
