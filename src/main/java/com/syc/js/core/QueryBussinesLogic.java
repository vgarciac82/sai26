package com.syc.js.core;

import java.sql.Connection;
import java.sql.SQLException;
import com.syc.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class QueryBussinesLogic extends DataSourceManager {

    private Logger log = LoggerFactory.getLogger(QueryBussinesLogic.class);

    public QueryBussinesLogic(String jniName) {
        super.init(jniName);
    }

    public Object makeQuery(String sql, boolean isXML) throws QueryException {
        Object obj = null;
        Connection conn = null;
        try {
            conn = getConnection();
            if (isXML)
                obj = QueryManager.makeXMLQuery(conn, sql);
            else
                obj = QueryManager.makeJSONQuery(conn, sql);
        } catch (SQLException exc) {
            log.error("Ejecutando JSQuery", exc);
            throw new QueryException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return obj;
    }
}
