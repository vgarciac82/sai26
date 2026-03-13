package com.syc.egresos;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;


public class ResponseJSON {

	private List<String>	errorList;
	private List<String>	messageList;
	private boolean			success;

	public ResponseJSON( boolean success, List<String> errorList, List<String> messageList ) {
		super();
		this.success = success;
		this.errorList = errorList == null ? new ArrayList<String>() : errorList;
		this.messageList = messageList == null ? new ArrayList<String>() : messageList;
	}

	private List<String> getErrorList() {
		return errorList;
	}

	private List<String> getMessageList() {
		return messageList;
	}

	private boolean isSuccess() {
		return success;
	}

	public String toJSON() throws JSONException {
		JSONObject responseJSON = new JSONObject();
		responseJSON.put( "success", isSuccess() );
		responseJSON.put( "errorList", getErrorList() );
		responseJSON.put( "messageList", getMessageList() );
		return responseJSON.toString();
	}
}
