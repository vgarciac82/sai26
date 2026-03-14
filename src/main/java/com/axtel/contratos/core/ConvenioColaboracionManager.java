package com.axtel.contratos.core;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ConvenioColaboracionManager {

    private static final Logger log = LoggerFactory.getLogger(ConvenioColaboracionManager.class);

    public static int insertaJustificacionCNET(Connection conn, ConvenioColaboracionEncabezado cc, String justificacion) throws SQLException {
        PreparedStatement psTC = null;
        StringBuilder qInsertTipoContrato = new StringBuilder();
        qInsertTipoContrato.append("INSERT INTO tContratosJustCNET( cIdContrato, cTipoContrato, cJustificacion)");
        qInsertTipoContrato.append("VALUES(?,?,?)");
        int insertados = 0;
        try {
            log.info("Object: {}", "Se ejecutara: [" + qInsertTipoContrato + "]\n" + cc);
            psTC = conn.prepareStatement(qInsertTipoContrato.toString());
            int parm = 1;
            psTC.setString(parm++, cc.getIdContrato());
            psTC.setString(parm++, "DI");
            psTC.setString(parm++, justificacion);
            insertados += psTC.executeUpdate();
            return insertados;
        } finally {
            CloseObject.closeObject(psTC);
        }
    }

    public static int insertaConvenioColaboracionEncabezado(Connection conn, ConvenioColaboracionEncabezado cc) throws SQLException {
        PreparedStatement ps = null;
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tConvenioColaboracion ");
        query.append("           (nFolioConvenioColaboracion ");
        query.append("           ,cIdContrato ");
        query.append("           ,cIdRFC ");
        query.append("           ,cIdUnidadAdministrativa ");
        query.append("           ,cIdTipoAdjudicacion ");
        query.append("           ,lEsPlurianual ");
        query.append("           ,fAdjudicacion ");
        query.append("           ,fConvenioIni ");
        query.append("           ,fConvenioFin ");
        query.append("           ,fFirmaConvenio ");
        query.append("           ,fCaptura ");
        query.append("           ,cConceptoConvenio ");
        query.append("           ,mImporteConvenio ");
        query.append("           ,mImporteBruto ");
        query.append("           ,mImporteIVA ");
        query.append("           ,mImporteTotal ");
        query.append("           ,nPorcIVAAplicable ");
        query.append("           ,cIDUsuarioCaptura ");
        query.append("           ,cUnidadEjecutora) ");
        query.append("     VALUES ");
        query.append("           (?, ");
        query.append("           ?, ");
        query.append("           ?, ");
        query.append("           ?, ");
        query.append("           ?, ");
        query.append("           ?, ");
        query.append("           ?, ");
        query.append("           ?, ");
        query.append("           ?, ");
        query.append("           ?, ");
        query.append("           ?, ");
        query.append("           ?, ");
        query.append("           ?, ");
        query.append("           ?, ");
        query.append("           ?, ");
        query.append("           ?, ");
        query.append("           ?, ");
        query.append("           ?, ");
        query.append("           ?) ");
        int insertados = 0;
        try {
            log.info("Object: {}", "Se ejecutara: [" + query + "]\n" + cc);
            ps = conn.prepareStatement(query.toString());
            int parm = 1;
            ps.setInt(parm++, cc.getFolioConvenioColaboracion());
            ps.setString(parm++, cc.getIdContrato());
            ps.setString(parm++, cc.getRfc());
            ps.setString(parm++, cc.getUnidadEjecutora());
            ps.setString(parm++, cc.getIdTipoAdjudicacion());
            ps.setString(parm++, cc.getEsPlurianual());
            ps.setDate(parm++, new Date(cc.getFechaFirmaConvenio().getTime()));
            ps.setDate(parm++, new Date(cc.getFechaConvenioInicio().getTime()));
            ps.setDate(parm++, new Date(cc.getFechaFinConvenio().getTime()));
            ps.setDate(parm++, new Date(cc.getFechaFirmaConvenio().getTime()));
            ps.setDate(parm++, new Date(cc.getFechaCaptura().getTime()));
            ps.setString(parm++, cc.getConceptoConvenio());
            ps.setBigDecimal(parm++, cc.getmImporteTotal());
            ps.setBigDecimal(parm++, cc.getmImporteBruto());
            ps.setBigDecimal(parm++, cc.getmImporteIVA());
            ps.setBigDecimal(parm++, cc.getmImporteTotal());
            ps.setInt(parm++, cc.getPorcIvaAplicable());
            ps.setString(parm++, cc.getLoginCaptura());
            ps.setString(parm++, cc.getUnidadEjecutora());
            insertados = ps.executeUpdate();
            return insertados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    /**
     * Inserta un renglon del detalle del convenio.
     *
     * @param conn
     *            Conexion activa a la base de datos.
     * @param cDet
     *            Renglon a insertar
     *
     * @return numero de registros insertados.
     * @throws SQLException
     */
    public static int insertaConvenioColaboracionDetalle(Connection conn, List<ConvenioColaboracionDetalle> cDet) throws SQLException {
        int insertados = 0;
        for (ConvenioColaboracionDetalle renglon : cDet) {
            if (!existeConvenioColaboracionDetalle(conn, renglon))
                insertados += insertaConvenioColaboracionDetalle(conn, renglon);
        }
        return insertados;
    }

    public static boolean existeConvenioColaboracionDetalle(Connection conn, ConvenioColaboracionDetalle renglon) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT COUNT(*) AS existe FROM tConvenioColaboracionDetalle WITH(NOLOCK) WHERE nFolioConvenioColaboracion = ? AND ep = ?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, renglon.getFolioConvenioColaboracion());
            ps.setString(2, renglon.getEp());
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1) > 0;
            return false;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    /**
     * @param conn
     * @param cDet
     * @return
     * @throws SQLException
     */
    public static int insertaConvenioColaboracionDetalle(Connection conn, ConvenioColaboracionDetalle cDet) throws SQLException {
        PreparedStatement ps = null;
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tConvenioColaboracionDetalle ");
        query.append("           (nFolioConvenioColaboracion ");
        query.append("           ,ep) ");
        query.append("     VALUES (?, ");
        query.append("             ?) ");
        int insertados = 0;
        try {
            log.info("Object: {}", "Se ejecutara: [" + query + "]\n" + cDet);
            ps = conn.prepareStatement(query.toString());
            int parm = 1;
            ps.setInt(parm++, cDet.getFolioConvenioColaboracion());
            ps.setString(parm++, cDet.getEp());
            insertados = ps.executeUpdate();
            return insertados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }
}
