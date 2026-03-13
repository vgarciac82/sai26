package com.syc.gestion.util;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.Logger;

public class StandAloneConnection {
	
	private static final Logger	log				= Logger.getLogger(StandAloneConnection.class);
	private Connection			conn			= null;
	private DBConfigurator		dbConfigurator	= null;

	public StandAloneConnection() throws Exception {
		dbConfigurator = DBConfigurator.instance(Util.dbPropertiesFilePath);
		Class.forName(dbConfigurator.getDriverClassName());
	}

	public Connection getStandAloneConnection() throws Exception {

		return getConnection(dbConfigurator, false);
	}

	public synchronized Connection getConnection(DBConfigurator dbConfigurator, boolean autoCommit) throws Exception {

		if (conn == null || (conn != null && conn.isClosed()))
			conn = DriverManager.getConnection(dbConfigurator.getUrl(), dbConfigurator.getUserName(), dbConfigurator.getPassword());

		conn.setAutoCommit(autoCommit);

		return conn;

	}

	public static synchronized Connection getConnection(DBConfigurator dbConfigurator) throws Exception {

		Class.forName(dbConfigurator.getDriverClassName());
		Connection	conn = DriverManager.getConnection(dbConfigurator.getUrl(), dbConfigurator.getUserName(), dbConfigurator.getPassword());
		conn.setAutoCommit(false);
		return conn;

	}
	
	public synchronized void closeConnection() {
		try {
			if (conn != null)
				conn.close();
		} catch (Exception e) {
			log.warn("Problemas cerrando DB Connection: " + e);
		} finally {
			conn = null;
		}
	}
}
