package com.axtel.proveedores.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.lang.StringUtils;
import com.axtel.proveedores.exception.ProveedorException;
import com.axtel.proveedores.model.Proveedor;
import com.syc.cfdi.db.CloseObject;
import java.util.Base64;

public class ProveedorDAO {

    public static Proveedor selectByRfc(Connection conn, String rfc) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT cidrfc               AS rfc, ");
        query.append("        ccalle               AS calle, ");
        query.append("        ccodigopostal        AS codigoPostal, ");
        query.append("        ccolonia             AS colonia, ");
        query.append("        ccurp                AS curp, ");
        query.append("        cemail               AS correo, ");
        query.append("        cgiro                AS giro, ");
        query.append("        cidentidadfederativa AS cIdEntidadFederativa, ");
        query.append("        cidunidadejecutora   AS unidadEjecutora, ");
        query.append("        cmunicipio           AS municipio, ");
        query.append("        cnumeroexterno       AS numeroExterno, ");
        query.append("        cnumerointerno       AS numeroInterno, ");
        query.append("        cnumeroregistro      AS numeroRegistro, ");
        query.append("        cnumerorepse         AS numeroREPSE, ");
        query.append("        crazonsocial         AS razonSocial, ");
        query.append("        crepresentantelegal  AS representanteLegal, ");
        query.append("        curl                 AS url, ");
        query.append("        lhabilitado          AS habilitado, ");
        query.append("        nidpyme              AS nIdPyme, ");
        query.append("        cEsRESICO            AS esResico, ");
        query.append("        idRegimenFiscal      AS idRegimenFiscal, ");
        query.append("		   case when len(replace(cIdRFC,'-',''))=13 then 1 else 0 end nPersonaFisica, ");
        query.append(" 	   case when len(replace(cIdRFC,'-',''))=13 then 0 else 1 end nPersonaMoral ");
        query.append(" FROM   mcatalogoproveedor WITH(NOLOCK) ");
        query.append(" WHERE  cidrfc = ?	 ");
        ResultSetHandler<Proveedor> h = new BeanHandler<Proveedor>(Proveedor.class);
        QueryRunner run = new QueryRunner();
        Proveedor p = run.query(conn, query.toString(), h, rfc);
        return p;
    }

    public int insert() {
        return 0;
    }

    public int delete() {
        return 0;
    }

    public static boolean numeroREPSECapturado(Connection conn, String rfc) throws ProveedorException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder querySel = new StringBuilder();
        querySel.append("SELECT cnumerorepse ");
        querySel.append("FROM   mcatalogoproveedor ");
        querySel.append("WHERE  Replace(cidrfc, '-', '') = Replace(?, '-', '')  ");
        try {
            ps = conn.prepareStatement(querySel.toString());
            ps.setString(1, rfc);
            rs = ps.executeQuery();
            if (rs.next())
                return !StringUtils.isBlank(rs.getString(1));
            else
                throw new ProveedorException("No existe informacion respecto al proveedor: " + rfc);
        } catch (Exception e) {
            throw new ProveedorException(e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static int convertRESICO(Connection conn, String rfc) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append(" UPDATE mcatalogoproveedor ");
        query.append(" SET    cEsRESICO = 'S', ");
        query.append("        idRegimenFiscal = 626 ");
        query.append(" WHERE  REPLACE(cidrfc,'-','') = REPLACE(?,'-','')  ");
        PreparedStatement ps = null;
        int actualizados = 0;
        try {
            ps = conn.prepareStatement(query.toString());
            int param = 1;
            ps.setString(param++, rfc);
            actualizados = ps.executeUpdate();
            return actualizados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int convertSupplierRESICO(Connection conn, String rfc) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append(" UPDATE tAltaProveedor ");
        query.append(" SET    id_regimen_fiscal = 626 ");
        query.append(" WHERE  REPLACE(cidrfc,'-','') = REPLACE(?,'-','')  ");
        PreparedStatement ps = null;
        int actualizados = 0;
        try {
            ps = conn.prepareStatement(query.toString());
            int param = 1;
            ps.setString(param++, rfc);
            actualizados = ps.executeUpdate();
            return actualizados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int update(Connection conn, Proveedor proveedor) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append(" UPDATE mcatalogoproveedor ");
        query.append(" SET    ccalle = ?, ");
        query.append("        ccodigopostal = ?, ");
        query.append("        ccolonia = ?, ");
        query.append("        ccurp = ?, ");
        query.append("        cemail = ?, ");
        query.append("        cgiro = ?, ");
        query.append("        cidentidadfederativa = ?, ");
        query.append("        cidunidadejecutora = ?, ");
        query.append("        cmunicipio = ?, ");
        query.append("        cnumeroexterno = ?, ");
        query.append("        cnumerointerno = ?, ");
        query.append("        cnumeroregistro = ?, ");
        query.append("        cnumerorepse = ?, ");
        query.append("        crazonsocial = ?, ");
        query.append("        crepresentantelegal = ?, ");
        query.append("        curl = ?, ");
        query.append("        lhabilitado = ?, ");
        query.append("        nidpyme = ?, ");
        query.append("        cEsResico = ?, ");
        query.append("        idRegimenFiscal = ? ");
        query.append(" WHERE  cidrfc = ?  ");
        PreparedStatement ps = null;
        int actualizados = 0;
        try {
            ps = conn.prepareStatement(query.toString());
            int param = 1;
            ps.setString(param++, proveedor.getCalle());
            ps.setString(param++, proveedor.getCodigoPostal());
            ps.setString(param++, proveedor.getColonia());
            ps.setString(param++, proveedor.getCurp());
            ps.setString(param++, proveedor.getCorreo());
            ps.setString(param++, proveedor.getGiro());
            ps.setString(param++, proveedor.getCIdEntidadFederativa());
            ps.setString(param++, proveedor.getUnidadEjecutora());
            ps.setString(param++, proveedor.getMunicipio());
            ps.setString(param++, proveedor.getNumeroExterno());
            ps.setString(param++, proveedor.getNumeroInterno());
            ps.setString(param++, proveedor.getNumeroRegistro());
            ps.setString(param++, proveedor.getNumeroREPSE());
            ps.setString(param++, proveedor.getRazonSocial());
            ps.setString(param++, proveedor.getRepresentanteLegal());
            ps.setString(param++, proveedor.getUrl());
            ps.setInt(param++, proveedor.getHabilitado());
            ps.setInt(param++, proveedor.getNIdPyme());
            ps.setString(param++, proveedor.getEsResico());
            ps.setInt(param++, proveedor.getIdRegimenFiscal());
            ps.setString(param++, proveedor.getRfc());
            actualizados = ps.executeUpdate();
            return actualizados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }
}
