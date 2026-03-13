package com.syc.contable;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface AccountingEventProcessorInterface {

	public List<AccountingMovement> getMovements(Connection conn, String event, Map<String, String> values) throws AccountingEventProcessorException;
}
