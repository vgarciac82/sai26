package com.axtel.cfdi.stamp.core;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VolumenRepository implements VolumenRepositoryInterface {

    private static final Logger log = LoggerFactory.getLogger(VolumenRepository.class);

    private ResultSetHandler<Volumen> resultHandler = new BeanHandler<Volumen>(Volumen.class);

    private QueryRunner runner = new QueryRunner();

    @Override
    public Volumen select(Connection connection, String volumen) throws SQLException {
        StringBuilder querySelect = new StringBuilder("SELECT ");
        try {
            log.info("Object: {}", "Buscando Volumen " + volumen);
            querySelect.append(VOLUMEN_FIELDS).append(" FROM volumen WHERE volumen = ?");
            log.trace("Object: {}", "Ejecutando: " + querySelect);
            Volumen v = runner.query(connection, querySelect.toString(), resultHandler, volumen);
            log.debug("Object: {}", "Encontrado: " + v);
            return v;
        } finally {
            querySelect = null;
        }
    }

    @Override
    public Volumen insert(Connection connection, Volumen volumen) throws SQLException {
        QueryRunner runner = new QueryRunner();
        StringBuilder queryInsert = new StringBuilder();
        queryInsert.append("INSERT INTO volumen ");
        queryInsert.append("(volumen, drive, base_path, directory_path, capacity, volume_type) ");
        queryInsert.append("VALUES(?, ?, ?, ?, ?, ?) ");
        Volumen newVolumen = runner.insert(connection, queryInsert.toString(), resultHandler, volumen.getVolumen(), volumen.getDriveUnit().getDrive(), volumen.getDriveUnit().getBasePath(), volumen.getDirectoryPath(), volumen.getCapacity(), volumen.getVolumeType());
        log.info("Object: {}", "Se inserto: \n" + volumen + "\n [ " + newVolumen + "]");
        return newVolumen;
    }

    @Override
    public Volumen getActive(Connection connection) throws SQLException {
        StringBuilder querySelect = new StringBuilder("SELECT ");
        try {
            log.debug("Buscando Volumen Activo ");
            querySelect.append(VOLUMEN_FIELDS).append(" FROM volumen WHERE capacidad = 1");
            log.trace("Object: {}", "Ejecutando: " + querySelect);
            Volumen v = runner.query(connection, querySelect.toString(), resultHandler);
            log.info("Object: {}", "Volumen Activo: " + v);
            return v;
        } finally {
            querySelect = null;
        }
    }

    @Override
    public Volumen getActive(Connection connection, Drive drive) throws SQLException {
        StringBuilder querySelect = new StringBuilder("SELECT ");
        try {
            log.info("Object: {}", "Buscando Volumen activo en  " + drive);
            querySelect.append(VOLUMEN_FIELDS).append(" FROM volumen WHERE drive = ? AND base_path = ? and capacity = '1'");
            log.trace("Object: {}", "Ejecutando: " + querySelect);
            Volumen v = runner.query(connection, querySelect.toString(), resultHandler, drive.getDrive(), drive.getBasePath());
            v.setDriveUnit(drive);
            log.debug("Object: {}", "Encontrado: " + v);
            return v;
        } finally {
            querySelect = null;
        }
    }

    @Override
    public Integer countByVolumen(Connection connection, Volumen volumen) throws SQLException {
        Integer files = null;
        StringBuilder querySelect = new StringBuilder("SELECT COUNT(*) FROM imx_pagina WITH(nolock) WHERE volumen = ?");
        try {
            log.info("Object: {}", "Contando archivos en Volumen " + volumen.getVolumen());
            log.trace("Object: {}", "Ejecutando: " + querySelect);
            files = runner.query(connection, querySelect.toString(), new ScalarHandler<Integer>(), volumen.getVolumen());
            log.debug("Object: {}", "Encontrados: " + files + " archivos en volumen: " + volumen.getVolumen());
            return files == null ? new Integer(0) : files;
        } finally {
            querySelect = null;
        }
    }

    @Override
    public void closeVolumen(Connection connection, String volumen, Volumen parentVolumen) throws SQLException {
        log.info("Object: {}", "Cerrando volumen: " + volumen);
        StringBuilder queryUpdate = new StringBuilder("UPDATE imx_volumen SET capacity = '0' WHERE volumen = ? AND UNIDAD_DISCO = ? AND TIPO_VOLUMEN = ?");
        try {
            log.trace("Object: {}", "Se ejecutara : " + queryUpdate + "[" + volumen + "," + parentVolumen.getDriveUnit().getDrive() + "," + parentVolumen.getVolumeType() + "]");
            int updated = runner.update(connection, queryUpdate.toString(), volumen, parentVolumen.getDriveUnit().getDrive(), parentVolumen.getVolumeType());
            log.debug("Object: {}", "Se afectaron " + updated + " registros");
        } finally {
            queryUpdate = null;
        }
    }
}
