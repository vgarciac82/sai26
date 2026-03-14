package com.axtel.cfdi.stamp.repository;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.axtel.cfdi.stamp.core.Drive;
import java.util.Base64;

public class DriveRepository implements DriveRepositoryInterface {

    private ResultSetHandler<Drive> resultHandler = new BeanHandler<Drive>(Drive.class);

    private QueryRunner runner = new QueryRunner();

    private static final Logger log = LoggerFactory.getLogger(DriveRepository.class);

    @Override
    public Drive findById(Connection connection, String drive) throws SQLException {
        StringBuilder querySelect = new StringBuilder("SELECT ");
        try {
            log.info("Object: {}", "Buscando Drive " + drive);
            querySelect.append(DRIVE_FIELDS).append(" FROM drive WHERE drive = ?");
            log.trace("Object: {}", "Ejecutando: " + querySelect);
            Drive d = runner.query(connection, querySelect.toString(), resultHandler, drive);
            log.debug("Object: {}", "Encontrado: " + d);
            return d;
        } finally {
            querySelect = null;
        }
    }

    @Override
    public Drive selectActive(Connection connection) throws SQLException {
        StringBuilder querySelect = new StringBuilder("SELECT ");
        try {
            log.info("Buscando Drive activo ");
            querySelect.append(DRIVE_FIELDS).append(" FROM drive WHERE drive_status = '1'");
            log.trace("Object: {}", "Ejecutando: " + querySelect);
            Drive d = runner.query(connection, querySelect.toString(), resultHandler);
            log.debug("Object: {}", "Encontrado: " + d);
            return d;
        } finally {
            querySelect = null;
        }
    }
}
