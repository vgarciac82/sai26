package com.axtel.cfdi.stamp.core;


import java.nio.file.Path;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;



public abstract class InvoiceReportPrinter {

	private Map<String, Object> params = new HashMap<>();

	public abstract Path printReport( Connection conn, InvoiceRequest request );
}
