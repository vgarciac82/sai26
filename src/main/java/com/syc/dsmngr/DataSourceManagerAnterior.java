package com.syc.dsmngr;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public abstract class DataSourceManagerAnterior {

    private static Logger log = LoggerFactory.getLogger(DataSourceManagerAnterior.class);

    private static DataSource ds = null;

    private static DataSourceManagerAnterior dsm = null;

    static boolean changed = false;

    public void init() {
        if (ds != null)
            return;
        String jndiName;
        try {
            InitialContext ic = new InitialContext();
            jndiName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jndiName == null) {
                jndiName = "jdbc/gestion";
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jndiName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jndiName);
        } catch (NamingException exc) {
            jndiName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jndiName + "\"");
        }
        init(jndiName);
    }

    public void init(String jniName) {
        if (ds != null)
            return;
        Context initContext;
        try {
            initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            ds = (DataSource) envContext.lookup(jniName);
        } catch (NamingException ne) {
            try {
                initContext = new InitialContext();
                Context envContext = (Context) initContext.lookup("java:comp/env");
                ds = (DataSource) envContext.lookup(jniName);
            } catch (NamingException nexc) {
                try {
                    initContext = new InitialContext();
                    ds = (DataSource) initContext.lookup(jniName);
                } catch (NamingException exc) {
                    ne.printStackTrace();
                    exc.printStackTrace();
                    throw new RuntimeException("No se encontro la fuente '" + jniName + "'");
                }
            }
        }
    }

    public Connection getConnection() throws SQLException {
        Connection conn = ds.getConnection();
        log.debug("Object: {}", "Defaults: Autocommit = " + conn.getAutoCommit() + ", TransactionIsolation = " + transactionIsolationToString(conn.getTransactionIsolation()));
        if (conn.getAutoCommit() == true) {
            conn.setAutoCommit(false);
            changed = true;
        }
        if (conn.getTransactionIsolation() != Connection.TRANSACTION_READ_COMMITTED) {
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            changed = true;
        }
        /*
				if (conn.getTransactionIsolation() !=
					Connection.TRANSACTION_SERIALIZABLE ) {
					                       conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE );
					                       changed = true;
					               }
		*/
        if (changed == true) {
            log.debug("Object: {}", "Gestion: Autocommit = " + conn.getAutoCommit() + ", TransactionIsolation = " + transactionIsolationToString(conn.getTransactionIsolation()));
            changed = false;
        }
        //return new SyCConnection(conn);
        return conn;
    }

    public static Connection getConnection(String jniName) throws SQLException {
        if (dsm == null)
            dsm = new DataSourceManagerAnterior() {
            };
        if (ds == null)
            dsm.init(jniName);
        return dsm.getConnection();
    }

    private String transactionIsolationToString(int transactionType) {
        String s = "TRANSACTION_UNKNOW";
        switch(transactionType) {
            case Connection.TRANSACTION_READ_UNCOMMITTED:
                s = "TRANSACTION_READ_UNCOMMITTED";
                break;
            case Connection.TRANSACTION_READ_COMMITTED:
                s = "TRANSACTION_READ_COMMITTED";
                break;
            case Connection.TRANSACTION_REPEATABLE_READ:
                s = "TRANSACTION_REPEATABLE_READ";
                break;
            case Connection.TRANSACTION_SERIALIZABLE:
                s = "TRANSACTION_SERIALIZABLE";
                break;
            case Connection.TRANSACTION_NONE:
                s = "TRANSACTION_NONE";
                break;
        }
        return s;
    }

    abstract class SyCConnection implements Connection {

        private Connection conn;

        public SyCConnection(Connection conn) {
            this.conn = conn;
        }

        public void clearWarnings() throws SQLException {
            conn.clearWarnings();
        }

        public void close() throws SQLException {
            // Se da commit por sugerencia de JRB
            conn.commit();
            conn.close();
        }

        public void commit() throws SQLException {
            conn.commit();
        }

        /*
		public Array createArrayOf(String typeName, Object[] elements) throws SQLException {

			return conn.createArrayOf(typeName, elements);
		}

		public Blob createBlob() throws SQLException {

			return conn.createBlob();
		}

		public Clob createClob() throws SQLException {

			return conn.createClob();
		}

		public NClob createNClob() throws SQLException {

			return conn.createNClob();
		}

		public SQLXML createSQLXML() throws SQLException {

			return conn.createSQLXML();
		}
		*/
        public Statement createStatement() throws SQLException {
            return conn.createStatement();
        }

        public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            return conn.createStatement(resultSetType, resultSetConcurrency);
        }

        public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        /*
		public Struct createStruct(String typeName, Object[] attributes) throws SQLException {

			return conn.createStruct(typeName, attributes);
		}
		*/
        public boolean getAutoCommit() throws SQLException {
            return conn.getAutoCommit();
        }

        public String getCatalog() throws SQLException {
            return conn.getCatalog();
        }

        /*
		public Properties getClientInfo() throws SQLException {

			return conn.getClientInfo();
		}

		public String getClientInfo(String name) throws SQLException {

			return conn.getClientInfo(name);
		}
		*/
        public int getHoldability() throws SQLException {
            return conn.getHoldability();
        }

        public DatabaseMetaData getMetaData() throws SQLException {
            return conn.getMetaData();
        }

        public int getTransactionIsolation() throws SQLException {
            return conn.getTransactionIsolation();
        }

        public Map getTypeMap() throws SQLException {
            return conn.getTypeMap();
        }

        public SQLWarning getWarnings() throws SQLException {
            return conn.getWarnings();
        }

        public boolean isClosed() throws SQLException {
            return conn.isClosed();
        }

        public boolean isReadOnly() throws SQLException {
            return conn.isReadOnly();
        }

        /*
		public boolean isValid(int timeout) throws SQLException {

			return conn.isValid(timeout);
		}
		*/
        public String nativeSQL(String sql) throws SQLException {
            return conn.nativeSQL(sql);
        }

        public CallableStatement prepareCall(String sql) throws SQLException {
            return conn.prepareCall(sql);
        }

        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return conn.prepareCall(sql, resultSetType, resultSetConcurrency);
        }

        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        public PreparedStatement prepareStatement(String sql) throws SQLException {
            return conn.prepareStatement(sql);
        }

        public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            return conn.prepareStatement(sql, autoGeneratedKeys);
        }

        public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            return conn.prepareStatement(sql, columnIndexes);
        }

        public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            return conn.prepareStatement(sql, columnNames);
        }

        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }

        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        public void releaseSavepoint(Savepoint savepoint) throws SQLException {
            conn.releaseSavepoint(savepoint);
        }

        public void rollback() throws SQLException {
            conn.rollback();
        }

        public void rollback(Savepoint savepoint) throws SQLException {
            conn.rollback(savepoint);
        }

        public void setAutoCommit(boolean autoCommit) throws SQLException {
            conn.setAutoCommit(autoCommit);
        }

        public void setCatalog(String catalog) throws SQLException {
            conn.setCatalog(catalog);
        }

        /*
		public void setClientInfo(Properties properties) throws SQLClientInfoException {

			conn.setClientInfo(properties);
		}

		public void setClientInfo(String name, String value) throws SQLClientInfoException {

			setClientInfo(name, value);
		}
		*/
        public void setHoldability(int holdability) throws SQLException {
            conn.setHoldability(holdability);
        }

        public void setReadOnly(boolean readOnly) throws SQLException {
            conn.setReadOnly(readOnly);
        }

        public Savepoint setSavepoint() throws SQLException {
            return conn.setSavepoint();
        }

        public Savepoint setSavepoint(String name) throws SQLException {
            return conn.setSavepoint(name);
        }

        public void setTransactionIsolation(int level) throws SQLException {
            conn.setTransactionIsolation(level);
        }

        public void setTypeMap(Map arg0) throws SQLException {
            conn.setTypeMap(arg0);
        }
        /*
		public boolean isWrapperFor(Class arg0) throws SQLException {

			return conn.isWrapperFor(arg0);
		}

		public Object unwrap(Class arg0) throws SQLException {

			return conn.unwrap(arg0);
		}
		*/
    }
}
