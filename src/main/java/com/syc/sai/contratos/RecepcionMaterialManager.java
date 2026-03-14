package com.syc.sai.contratos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import org.apache.commons.lang.StringUtils;
import com.syc.adquisiciones.core.DatosRecepcionFIEL;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

/**
 * Administrador de la recepcion de material.
 *
 * @author vicente.garcia
 */
public class RecepcionMaterialManager {

    private static final Logger log = LoggerFactory.getLogger(RecepcionMaterialManager.class);

    /**
     * Actualiza el estatus de una recepcion de material.
     *
     * @param conn
     *            Conexion activa a la DB
     * @param recepcionMaterial
     *            Recepcion a actualizar
     * @param status
     *            Nuevos estatus de la RM
     * @return Numero de registros actualizados.
     * @throws SQLException
     */
    public static int avanzaEstatus(Connection conn, DatosRecepcionFIEL recepcionMaterial, int status) throws SQLException {
        log.info("Object: {}", "Cambiando estatus a: " + status + " en la RM: " + recepcionMaterial);
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE mRecepcionpMat SET nIdEstadoRecepMat = ? WHERE cIdpedContDef = ? AND cIdRecepMat = ?");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sb.toString());
            ps.setInt(1, status);
            ps.setString(2, recepcionMaterial.getcIdPedContDef());
            ps.setString(3, recepcionMaterial.getcIdRecepcionMat());
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static void saveAutorizacionRM(Connection conn, DatosRecepcionFIEL recepcionMaterial) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tNotaAutorizaRM( ");
        query.append("	cIDContrato, ");
        query.append("	cIDRecepMat, ");
        query.append("	dMotivoNota, ");
        query.append("	cFolioNota, ");
        query.append("	cNumeroEmpleado, ");
        query.append("	cIdContratoDefinitivo,");
        query.append("	cIDUsuarioCaptura,");
        query.append("	nIdEstatusAntentaNotaFirmada");
        query.append(")values( ");
        query.append("	?,  ");
        query.append("	?, ");
        query.append("	?,  ");
        query.append("	?,  ");
        query.append("	?,  ");
        query.append("	?,  ");
        query.append("	?,  ");
        query.append("	?  )");
        PreparedStatement ps = null;
        ResultSet rsKey = null;
        try {
            if (StringUtils.isBlank(recepcionMaterial.getContratoCNET()))
                findContratoCNET(conn, recepcionMaterial);
            ps = conn.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, recepcionMaterial.getContratoCNET());
            ps.setString(2, recepcionMaterial.getcIdRecepcionMat());
            ps.setString(3, recepcionMaterial.getMotivoAutorizacion());
            ps.setString(4, recepcionMaterial.getFolioNota());
            ps.setInt(5, recepcionMaterial.getNumeroEmpleado());
            ps.setString(6, recepcionMaterial.getcIdPedContDef());
            ps.setString(7, recepcionMaterial.getIdUsuarioCaptura());
            ps.setInt(8, recepcionMaterial.getnIdEstatusAtentaNotaFirmada());
            int insertados = ps.executeUpdate();
            rsKey = ps.getGeneratedKeys();
            rsKey.next();
            recepcionMaterial.setIdNota(rsKey.getInt(1));
            log.info("Object: {}", "Se insertaron: " + insertados + " solicitudes de autorizacion de RM");
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    /**
     * @param conn
     * @param recepcionMaterial
     * @throws SQLException
     */
    private static void findContratoCNET(Connection conn, DatosRecepcionFIEL recepcionMaterial) throws SQLException {
        log.info("Object: {}", "Insertando RM: " + recepcionMaterial);
        StringBuilder query = new StringBuilder();
        query.append("select cNoContratoCNET from v_mListadoContratos where cIdContratoDefinitivo=?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, recepcionMaterial.getcIdPedContDef());
            rs = ps.executeQuery();
            if (rs.next())
                recepcionMaterial.setContratoCNET(rs.getString(1));
            else
                recepcionMaterial.setContratoCNET("");
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    /**
     * Lee una atenta nota para una recepcion de material.
     *
     * @param conn
     *            Conexion a la base de datos activa
     * @param folioNota
     *            Folio de la atenta nota
     * @return Datos ampliados de la RM
     * @throws SQLException
     * @throws ParseException
     */
    public static DatosRecepcionFIEL read(Connection conn, int folioNota) throws SQLException, ParseException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	* ");
        query.append("  FROM	vAtentaNotaRMFIEL WITH(NOLOCK) ");
        query.append(" WHERE	nIDNota = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, folioNota);
            rs = ps.executeQuery();
            if (rs.next()) {
                DatosRecepcionFIEL rm = new DatosRecepcionFIEL();
                rm.setIdNota(folioNota);
                rm.setcIdPedContDef(rs.getString("cidcontratodefinitivo"));
                rm.setcIdRecepcionMat(rs.getString("cidrecepmat"));
                rm.setFolioNota(rs.getString("cfolionota"));
                rm.setMotivoAutorizacion(rs.getString("dmotivonota"));
                rm.setFechaNota(Util.stringToDate(rs.getString("dFechaNota"), "dd/MM/yyyy"));
                rm.setNumeroEmpleado(rs.getInt("cnumeroempleado"));
                rm.setIdEstatusRM(rs.getInt("idEstatusRM"));
                rm.setIdUsuarioCaptura(rs.getString("cIDUsuarioCaptura"));
                rm.setContratoCNET(rs.getString("cnocontratocnet"));
                rm.setnIdEntraAlmacen(rs.getInt("nIdEntraAlmacen"));
                return rm;
            } else {
                throw new SQLException("No se encontro la Nota de autorizacion con folio : " + folioNota);
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }
}
