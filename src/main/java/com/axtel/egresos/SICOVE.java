package com.axtel.egresos;


public interface SICOVE {

	final String	PARAM_INBOX_TYPE					= "INBOX_TYPE";
	final String	PARAM_REQUEST_FOLIO					= "REQUEST_FOLIO";
	final String	PARAM_REQUEST_STATUS				= "REQUEST_STATUS";
	final String	PARAM_REQUEST_ACCOUNT				= "REQUEST_ACCOUNT";
	final String	PARAM_ID_PROCESS					= "ID_PROCESS";

	final int		CAPTURE_FUELING						= 1;
	final int		WAITING_FOR_FUELING_AUTHORIZATION	= 2;
	final int		WAITING_FOR_SUPPLIER_PROVISIONING	= 3;
	final int		FUELING_REQUEST_AUTHORIZED			= 4;
	final int		FUELING_REQUEST_REJECTED			= 5;
	final int		FUELING_REQUEST_DISCARD				= 6;
	final int		CAPTURE_VERIFICATION				= 7;
	final int		VALIDATING_VERIFICATION				= 8;
	final int		VERIFIED_FUELING_REQUEST			= 9;

}
