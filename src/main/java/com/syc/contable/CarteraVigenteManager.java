package com.syc.contable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CarteraVigenteManager {

    private static final Logger log = LoggerFactory.getLogger(CarteraVigenteManager.class);

    public static String validaExisteCartera(Connection conn, String ep) throws Exception {
        ResultSet rs = null;
        String respuesta = "";
        PreparedStatement ps = null;
        String query = "select COUNT (idproyecto) as existe  from vcarteravigente where idproyecto = ( select  SUBSTRING('" + ep + "', 45,11))";
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                respuesta = "OK";
            }
            return respuesta;
        } finally {
            try {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(ps, false);
            } catch (Exception e) {
                log.warn(e);
            } finally {
                rs = null;
                ps = null;
            }
        }
    }

    public static String validaVigenciaCartera(Connection conn, String ep) throws Exception {
        ResultSet rs = null;
        String respuesta = "";
        PreparedStatement ps = null;
        String query = "select estatus from vcarteravigente where idproyecto = ( select  SUBSTRING('" + ep + "', 45,11))  and estatus ='Vigente'";
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                respuesta = "OK";
            }
            return respuesta;
        } finally {
            try {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(ps, false);
            } catch (Exception e) {
                log.warn(e);
            } finally {
                rs = null;
                ps = null;
            }
        }
    }

    public static String validaEntidadFederativa(Connection conn, String ep) throws Exception {
        ResultSet rs = null;
        String respuesta = "";
        PreparedStatement ps = null;
        String query = "select idestado from vcarteravigente where idproyecto = ( select  SUBSTRING('" + ep + "', 45,11))  and estatus ='Vigente' and idestado = (select  SUBSTRING( '" + ep + "' , 42,2))";
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                respuesta = "OK";
            }
            return respuesta;
        } finally {
            try {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(ps, false);
            } catch (Exception e) {
                log.warn(e);
            } finally {
                rs = null;
                ps = null;
            }
        }
    }

    public static double getMontoTotalCartera(Connection conn, String ep) throws Exception {
        ResultSet rs = null;
        double cantidad = 0.0d;
        PreparedStatement ps = null;
        String query = "select idestado from vcarteravigente where idproyecto = ( select  SUBSTRING('" + ep + "', 45,11))  and estatus ='Vigente' " + " and idestado = (select  SUBSTRING( '" + ep + "' , 42,2)) and idcvepres like + '% (select  SUBSTRING( '" + ep + "' , 32,5))" + "%'";
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                cantidad = rs.getDouble("monto");
            }
            return cantidad;
        } finally {
            try {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(ps, false);
            } catch (Exception e) {
                log.warn(e);
            } finally {
                rs = null;
                ps = null;
            }
        }
    }
}
