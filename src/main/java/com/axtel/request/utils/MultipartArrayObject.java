package com.axtel.request.utils;


import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class MultipartArrayObject {

	private Map<String, List<String>>	params	= new LinkedHashMap<>();
	private Map<String, List<File>>		files	= new LinkedHashMap<>();

	public MultipartArrayObject( ) {

	}

	/**
	 * @return the params
	 */
	public Map<String, List<String>> getParams() {
		return params;
	}

	/**
	 * @param params
	 *            the params to set
	 */
	public void setParams( Map<String, List<String>> params ) {
		this.params = params;
	}

	/**
	 * @return the files
	 */
	public Map<String, List<File>> getFiles() {
		return files;
	}

	/**
	 * @param files
	 *            the files to set
	 */
	public void setFiles( Map<String, List<File>> files ) {
		this.files = files;
	}

}
