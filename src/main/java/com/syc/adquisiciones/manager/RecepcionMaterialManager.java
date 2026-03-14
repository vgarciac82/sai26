package com.syc.adquisiciones.manager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import com.syc.adquisiciones.core.RecepcionMaterial;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class RecepcionMaterialManager {

    private static final String DELETE_QUERY = "DELETE FROM mRecepcionpMat WHERE cIdRecepMat = ? AND cIdpedContDef = ?";

    private static final String INSERT_QUERY = "INSERT INTO mRecepcionpMat (cIdRecepMat, nIdConsecutivoRecepM, cEjercicio, cIdpedContDef, nCantidad, " + "mMontoConIVA, mMontoSinIVA, mMontoIVA, nIdEstadoRecepMat, cUnidadEjecutora, cIdAlmacen, isServicio, mMontoOtrosImp, " + "isFactAmort, cObservaciones, isAmortizaEjercAnt, mDescuentoConIVA, mDescuentoSinIVA, mDescuentoIVA, nIdEntraAlmacen) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final Logger log = LoggerFactory.getLogger(RecepcionMaterialManager.class);

    private static final String SELECT_QUERY = "SELECT * FROM mRecepcionpMat WITH(NOLOCK) WHERE cIdRecepMat = ? AND cIdpedContDef = ?";

    private static final String UPDATE_QUERY = "UPDATE mRecepcionpMat SET nIdConsecutivoRecepM = ?, cEjercicio = ?, nCantidad = ?, " + "mMontoConIVA = ?, mMontoSinIVA = ?, mMontoIVA = ?, nIdEstadoRecepMat = ?, cUnidadEjecutora = ?, cIdAlmacen = ?, " + "isServicio = ?, mMontoOtrosImp = ?, isFactAmort = ?, cObservaciones = ?, isAmortizaEjercAnt = ?, " + "mDescuentoConIVA = ?, mDescuentoSinIVA = ?, mDescuentoIVA = ?, nIdEntraAlmacen = ? " + "WHERE cIdRecepMat = ? AND cIdpedContDef = ?";

    public static void changeStatus(Connection conn, String contract, String id, int status) throws SQLException {
        String query = "UPDATE mRecepcionpMat SET nIdEstadoRecepMat = ? WHERE cIdpedContDef = ? AND cIdRecepMat = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, status);
            ps.setString(2, contract);
            ps.setString(3, id);
            log.trace("Object: {}", "Updating status of reception [" + id + "] in contract [" + contract + "] to [" + status + "]");
            int updated = ps.executeUpdate();
            if (updated == 0) {
                log.warn("Object: {}", "No reception updated for contract [" + contract + "] and id [" + id + "]");
            }
        } catch (SQLException e) {
            log.error("Error updating status of reception [" + id + "] in contract [" + contract + "]: " + e, e);
            throw e;
        }
    }

    public static void delete(Connection conn, String idRecepMat, String idPedContDef) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(DELETE_QUERY)) {
            ps.setString(1, idRecepMat);
            ps.setString(2, idPedContDef);
            ps.executeUpdate();
        }
    }

    private static BigDecimal getNullableBigDecimal(ResultSet rs, String columnName) throws SQLException {
        BigDecimal value = rs.getBigDecimal(columnName);
        return rs.wasNull() ? null : value;
    }

    private static Boolean getNullableBoolean(ResultSet rs, String columnName) throws SQLException {
        boolean value = rs.getBoolean(columnName);
        return rs.wasNull() ? null : value;
    }

    private static Integer getNullableInt(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : value;
    }

    public static void insert(Connection conn, RecepcionMaterial recepcion) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_QUERY)) {
            ps.setString(1, recepcion.getIdRecepMat());
            ps.setInt(2, recepcion.getIdConsecutivoRecepM());
            ps.setString(3, recepcion.getEjercicio());
            ps.setString(4, recepcion.getIdPedContDef());
            setNullableInt(ps, 5, recepcion.getCantidad());
            setNullableBigDecimal(ps, 6, recepcion.getMontoConIVA());
            setNullableBigDecimal(ps, 7, recepcion.getMontoSinIVA());
            setNullableBigDecimal(ps, 8, recepcion.getMontoIVA());
            ps.setLong(9, recepcion.getIdEstadoRecepMat());
            ps.setString(10, recepcion.getUnidadEjecutora());
            ps.setString(11, recepcion.getIdAlmacen());
            setNullableBoolean(ps, 12, recepcion.getEsServicio());
            setNullableBigDecimal(ps, 13, recepcion.getMontoOtrosImp());
            ps.setBoolean(14, recepcion.isFactAmort());
            ps.setString(15, recepcion.getObservaciones());
            setNullableBoolean(ps, 16, recepcion.getAmortizaEjercAnt());
            setNullableBigDecimal(ps, 17, recepcion.getDescuentoConIVA());
            setNullableBigDecimal(ps, 18, recepcion.getDescuentoSinIVA());
            setNullableBigDecimal(ps, 19, recepcion.getDescuentoIVA());
            ps.setInt(20, recepcion.getIdEntraAlmacen());
            ps.executeUpdate();
        }
    }

    private static RecepcionMaterial mapToRecepcionMaterial(ResultSet rs) throws SQLException {
        RecepcionMaterial recepcion = new RecepcionMaterial();
        recepcion.setIdRecepMat(rs.getString("cIdRecepMat"));
        recepcion.setIdConsecutivoRecepM(rs.getInt("nIdConsecutivoRecepM"));
        recepcion.setEjercicio(rs.getString("cEjercicio"));
        recepcion.setIdPedContDef(rs.getString("cIdpedContDef"));
        recepcion.setCantidad(getNullableInt(rs, "nCantidad"));
        recepcion.setMontoConIVA(getNullableBigDecimal(rs, "mMontoConIVA"));
        recepcion.setMontoSinIVA(getNullableBigDecimal(rs, "mMontoSinIVA"));
        recepcion.setMontoIVA(getNullableBigDecimal(rs, "mMontoIVA"));
        recepcion.setIdEstadoRecepMat(rs.getLong("nIdEstadoRecepMat"));
        recepcion.setUnidadEjecutora(rs.getString("cUnidadEjecutora"));
        recepcion.setIdAlmacen(rs.getString("cIdAlmacen"));
        recepcion.setEsServicio(getNullableBoolean(rs, "isServicio"));
        recepcion.setMontoOtrosImp(getNullableBigDecimal(rs, "mMontoOtrosImp"));
        recepcion.setFactAmort(rs.getBoolean("isFactAmort"));
        recepcion.setObservaciones(rs.getString("cObservaciones"));
        recepcion.setAmortizaEjercAnt(getNullableBoolean(rs, "isAmortizaEjercAnt"));
        recepcion.setDescuentoConIVA(getNullableBigDecimal(rs, "mDescuentoConIVA"));
        recepcion.setDescuentoSinIVA(getNullableBigDecimal(rs, "mDescuentoSinIVA"));
        recepcion.setDescuentoIVA(getNullableBigDecimal(rs, "mDescuentoIVA"));
        recepcion.setIdEntraAlmacen(rs.getInt("nIdEntraAlmacen"));
        return recepcion;
    }

    public static RecepcionMaterial select(Connection conn, String idRecepMat, String idPedContDef) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_QUERY)) {
            ps.setString(1, idRecepMat);
            ps.setString(2, idPedContDef);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapToRecepcionMaterial(rs);
                }
            }
        }
        return null;
    }

    public static List<RecepcionMaterial> selectActiveByContract(Connection conn, String contract) throws SQLException {
        String query = "SELECT * FROM mRecepcionpMat WITH(NOLOCK) WHERE cIdpedContDef = ? AND nIdEstadoRecepMat = 2";
        List<RecepcionMaterial> recepciones = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, contract);
            log.trace("Object: {}", "Retrieving active receptions for contract [" + contract + "]");
            rs = ps.executeQuery();
            while (rs.next()) {
                recepciones.add(mapToRecepcionMaterial(rs));
            }
            return recepciones;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }

    private static void setNullableBigDecimal(PreparedStatement ps, int index, BigDecimal value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.DECIMAL);
        } else {
            ps.setBigDecimal(index, value);
        }
    }

    private static void setNullableBoolean(PreparedStatement ps, int index, Boolean value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.BOOLEAN);
        } else {
            ps.setBoolean(index, value);
        }
    }

    private static void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }

    public static void update(Connection conn, RecepcionMaterial recepcion) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_QUERY)) {
            ps.setInt(1, recepcion.getIdConsecutivoRecepM());
            ps.setString(2, recepcion.getEjercicio());
            setNullableInt(ps, 3, recepcion.getCantidad());
            setNullableBigDecimal(ps, 4, recepcion.getMontoConIVA());
            setNullableBigDecimal(ps, 5, recepcion.getMontoSinIVA());
            setNullableBigDecimal(ps, 6, recepcion.getMontoIVA());
            ps.setLong(7, recepcion.getIdEstadoRecepMat());
            ps.setString(8, recepcion.getUnidadEjecutora());
            ps.setString(9, recepcion.getIdAlmacen());
            setNullableBoolean(ps, 10, recepcion.getEsServicio());
            setNullableBigDecimal(ps, 11, recepcion.getMontoOtrosImp());
            ps.setBoolean(12, recepcion.isFactAmort());
            ps.setString(13, recepcion.getObservaciones());
            setNullableBoolean(ps, 14, recepcion.getAmortizaEjercAnt());
            setNullableBigDecimal(ps, 15, recepcion.getDescuentoConIVA());
            setNullableBigDecimal(ps, 16, recepcion.getDescuentoSinIVA());
            setNullableBigDecimal(ps, 17, recepcion.getDescuentoIVA());
            ps.setInt(18, recepcion.getIdEntraAlmacen());
            ps.setString(19, recepcion.getIdRecepMat());
            ps.setString(20, recepcion.getIdPedContDef());
            ps.executeUpdate();
        }
    }
}
