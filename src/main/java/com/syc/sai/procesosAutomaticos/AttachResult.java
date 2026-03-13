package com.syc.sai.procesosAutomaticos;

public class AttachResult {

	private String	fileName;
	private String	result;
	boolean			success;

	public AttachResult(String fileName, String result, boolean success) {
		super();
		this.fileName = fileName;
		this.result = result;
		this.success = success;
	}

	public String getFileName() {
		return fileName;
	}

	public String getResult() {
		return result;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	@Override
	public String toString() {
		return "AttachResult [fileName=" + fileName + ", result=" + result + ", success=" + success + "]";
	}

}
