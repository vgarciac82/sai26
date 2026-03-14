package com.syc.contable.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import com.syc.contable.ReintegrosBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class RectificacionPresupuestariaManager {

    /*
	 * TODO VGC Esto no funciona por ser estatico. En un ambiente paralelo se
	 * sobreescribiria para todos los hilos. Cambiar
	 */
    static String ur;

    private static final Logger log = LoggerFactory.getLogger(RectificacionPresupuestariaManager.class);

    public static void setUR(String UR) {
        ur = UR;
    }

    public static String getUR() {
        return ur;
    }

    public static Rectificacion filtraCLC(Connection conn, String clc) throws CLCNoPagadaException, Exception {
        Rectificacion rectificacion = null;
        String SqlH = "SELECT " + "			case cTipoPago  when 'PAGOOBRA' then 'COMPROMISO'" + "							when 'PAGODIVERSO' then 'COMPROMISO'" + "							when 'PAGODIRECTO' then 'DIRECTA' " + "							when 'RELACIONGASTOS' then 'DIRECTA' " + "							when 'NOMINA' then 'DIRECTA' when 'AJENAS' then 'DIRECTA' when 'FEDERALIZADO' then 'DIRECTA' " + "							ELSE '' END AS dTipoPago " + "			,nFolioPagado" + "			,caNoContrarrecibo" + "			,nFolioPoliza" + "			,nFolioSIAFF" + "			,cDescripcionPoliza" + "			,cUnidadResponsableContable" + "			,nFolioSICOP " + " FROM tPagadoEncabezado WHERE nCLC = ? AND cDocumentoHaplicado = 'S'";
        String SqlD = "SELECT * FROM tPagadoDetalle WHERE nFolioPagado = (SELECT nFolioPagado FROM tPagadoEncabezado WHERE nCLC = ? )  ORDER BY nDocRenglon";
        rectificacion = queryPagado(conn, clc, SqlH, SqlD, "");
        return rectificacion;
    }

    public static Rectificacion filtraFolioSICOP(Connection conn, String folioSICOP, String UR, String CXP) throws Exception {
        setUR(UR);
        return filtraFolioSICOP(conn, folioSICOP, CXP);
    }

    public static Rectificacion filtraFolioSICOP(Connection conn, String folioSICOP, String UR, String CXP, String contrarrecibo) throws Exception {
        setUR(UR);
        return filtraFolioSICOPRect(conn, folioSICOP, CXP, contrarrecibo);
    }

    public static Rectificacion filtraFolioSICOP(Connection conn, String folioSICOP, String CXP) throws Exception {
        Rectificacion rectificacion = null;
        String SqlH = "SELECT CASE cTipoPago WHEN 'PAGOOBRA' THEN 'COMPROMISO' WHEN 'PAGODIVERSO' THEN 'COMPROMISO' WHEN 'PAGODIRECTO' THEN 'DIRECTA' WHEN 'RELACIONGASTOS' THEN 'DIRECTA' WHEN 'NOMINA' THEN 'COMPROMISO' when 'AJENAS' then 'DIRECTA' when 'FEDERALIZADO' then 'COMPROMISO' " + "ELSE '' END AS dTipoPago,nFolioPagado,cTipoPago,cDocumentoHaplicado,caNoContrarrecibo,nFolioPoliza,nFolioSIAFF,cDescripcionPoliza,cUnidadResponsableContable,nFolioSICOP FROM tPagadoEncabezado WITH(NOLOCK) WHERE nFolioSICOP = ? ";
        String SqlD = "SELECT * FROM tPagadoDetalle WITH(NOLOCK) WHERE nFolioPagado = (SELECT nFolioPagado FROM tPagadoEncabezado WHERE nFolioSICOP = ? ) ORDER BY nDocRenglon";
        if (!"".equals(CXP) && CXP != null) {
            SqlH = "SELECT CASE ctipopago " + "         WHEN 'PAGOOBRA' THEN 'COMPROMISO' " + "         WHEN 'PAGODIVERSO' THEN 'COMPROMISO' " + "         WHEN 'PAGODIRECTO' THEN 'DIRECTA' " + "         WHEN 'RELACIONGASTOS' THEN 'DIRECTA' " + "         WHEN 'NOMINA' THEN 'COMPROMISO' " + "         WHEN 'AJENAS' THEN 'DIRECTA' " + "         WHEN 'FEDERALIZADO' THEN 'COMPROMISO' " + "         ELSE '' " + "       END AS dTipoPago, " + "       nfoliopagado, " + "       ctipopago, " + "       cdocumentohaplicado, " + "       canocontrarrecibo, " + "       nfoliopoliza, " + "       nfoliosiaff, " + "       cdescripcionpoliza, " + "       cunidadresponsablecontable, " + "       nfoliosicop " + "FROM   tpagadoencabezado WITH(nolock) " + "WHERE  nfoliosicop = ? " + "       AND canocontrarrecibo = ? ";
            SqlD = "SELECT * FROM   tpagadodetalle WITH(nolock) " + "WHERE  nfoliopagado = (SELECT nfoliopagado " + "                       FROM   tpagadoencabezado " + "                       WHERE  nfoliosicop = ? " + "                              AND canocontrarrecibo = ?) " + "ORDER  BY ndocrenglon ";
        }
        rectificacion = queryPagado(conn, folioSICOP, SqlH, SqlD, CXP);
        return rectificacion;
    }

    public static Rectificacion filtraFolioSICOPRect(Connection conn, String folioSICOP, String CXP, String contrarrecibo) throws Exception {
        Rectificacion rectificacion = null;
        String SqlH = "SELECT CASE cTipoPago WHEN 'PAGOOBRA' THEN 'COMPROMISO' WHEN 'PAGODIVERSO' THEN 'COMPROMISO' WHEN 'PAGODIRECTO' THEN 'DIRECTA' WHEN 'RELACIONGASTOS' THEN 'DIRECTA' WHEN 'NOMINA' THEN 'COMPROMISO' when 'AJENAS' then 'DIRECTA' when 'FEDERALIZADO' then 'COMPROMISO' " + "ELSE '' END AS dTipoPago,nFolioPagado,cTipoPago,cDocumentoHaplicado,caNoContrarrecibo,nFolioPoliza, ISNULL( nFolioSIAFF, -1 ) AS nFolioSIAFF,cDescripcionPoliza,cUnidadResponsableContable,nFolioSICOP FROM tPagadoEncabezado WITH(NOLOCK) WHERE nFolioSICOP = ? ";
        String SqlD = "SELECT * FROM tPagadoDetalle WITH(NOLOCK) WHERE nFolioPagado = (SELECT nFolioPagado FROM tPagadoEncabezado WHERE nFolioSICOP = ? ) ORDER BY nDocRenglon";
        if (!"".equals(CXP) && CXP != null) {
            SqlH = "SELECT CASE ctipopago WHEN 'PAGOOBRA' THEN 'COMPROMISO' WHEN 'PAGODIVERSO' THEN 'COMPROMISO' WHEN 'PAGODIRECTO' THEN 'DIRECTA' WHEN 'RELACIONGASTOS' THEN 'DIRECTA' WHEN 'NOMINA' THEN 'COMPROMISO' " + "         WHEN 'AJENAS' THEN '' WHEN 'FEDERALIZADO' THEN 'COMPROMISO' ELSE '' END AS dTipoPago, nfoliopagado, ctipopago, cdocumentohaplicado, canocontrarrecibo, " + "       nfoliopoliza, ISNULL( nFolioSIAFF, -1 ) AS nFolioSIAFF, cdescripcionpoliza, cunidadresponsablecontable, nfoliosicop FROM   tpagadoencabezado WITH(nolock) WHERE  nfoliosicop = ? AND canocontrarrecibo = ? ";
            //						+ "       '" + CXP + "'             AS caNoContrarrecibo, "
            if ("".equals(contrarrecibo)) {
                SqlD = "SELECT '" + CXP + "'             AS caNoContrarrecibo \r\n" + "	, Isnull(ffm.nidprograma, -1)  programa\r\n" + "	, Isnull(ffm.csubprograma, -1) SubPrograma\r\n" + "	, ISNULL((SELECT CASE WHEN cEvento LIKE 'DICE%' THEN PAGDET.mImporte - RECDET.mImporte END AS IMPORTE FROM tRectificacionAutDetalle AS RECDET WITH (NOLOCK) WHERE RECENC.nFolioRectificacionAut = RECDET.nFolioRectificacionAut AND cEvento LIKE 'DICE%' AND PAGDET.EP = RECDET.EP AND RECENC.cDocumentoHaplicado = 'S' AND PAGDET.cMes = RECDET.cMes),PAGDET.mImporte) AS mImportePagado\r\n" + "	, ISNULL((SELECT CASE WHEN cEvento LIKE 'DICE%' THEN RECDET.EP END AS IMPORTE FROM tRectificacionAutDetalle AS RECDET WITH (NOLOCK) WHERE RECENC.nFolioRectificacionAut = RECDET.nFolioRectificacionAut AND cEvento LIKE 'DICE%' AND PAGDET.EP = RECDET.EP AND RECENC.cDocumentoHaplicado = 'S'), PAGDET.EP) AS EPPAG\r\n" + "   , ISNULL((SELECT CASE WHEN cEvento LIKE 'DICE%' THEN RECDET.nDocRenglon END AS ndogrenglon FROM tRectificacionAutDetalle AS RECDET WITH (NOLOCK) WHERE RECENC.nFolioRectificacionAut = RECDET.nFolioRectificacionAut AND cEvento LIKE 'DICE%' AND PAGDET.EP = RECDET.EP AND RECENC.cDocumentoHaplicado = 'S' AND PAGDET.cMes = RECDET.cMes), PAGDET.nDocRenglon) AS nDocRenglonP \r\n" + "   , ISNULL((SELECT CASE WHEN cEvento LIKE 'DICE%' THEN RECDET.cMes END AS ndogrenglon FROM tRectificacionAutDetalle AS RECDET WITH (NOLOCK) WHERE RECENC.nFolioRectificacionAut = RECDET.nFolioRectificacionAut AND cEvento LIKE 'DICE%' AND PAGDET.EP = RECDET.EP AND RECENC.cDocumentoHaplicado = 'S' AND PAGDET.cMes = RECDET.cMes), PAGDET.nMes) AS nMesP \r\n" + "  FROM tPagadoEncabezado AS PAGENC WITH (NOLOCK)\r\n" + "  JOIN tPagadoDetalle AS PAGDET WITH (NOLOCK) ON PAGENC.nFolioPagado = PAGDET.nFolioPagado\r\n" + "  LEFT JOIN tRectificacionAutEncabezado AS RECENC WITH (NOLOCK) ON PAGENC.caNoContrarrecibo = RECENC.caNoContrarrecibo\r\n" + "  LEFT OUTER JOIN (SELECT 'FEDERALIZADO'         AS cTipoPago, \r\n" + "	nfoliopagofederalizado AS nFolioPago,  \r\n" + "	nidprograma,  \r\n" + "	csubprograma  \r\n" + "	FROM   tpagofederalizadoencabezado WITH(nolock)  \r\n" + "	 WHERE  cdocumentohaplicado = 'S'  \r\n" + "	AND nidprograma IS NOT NULL  \r\n" + "	AND csubprograma IS NOT NULL  \r\n" + "	UNION  \r\n" + "	SELECT 'RELACIONGASTOS'     AS cTipoPago, \r\n" + "	nfoliorelaciongastos AS nFolioPago, \r\n" + "	nidprograma,  \r\n" + "	csubprograma  \r\n" + "	FROM   dbo.trelaciongastosencabezado \r\n" + "	WHERE  nidprograma IS NOT NULL  \r\n" + "	AND csubprograma IS NOT NULL \r\n" + "	AND cdocumentohaplicado = 'S') FFM \r\n" + "	ON PAGDET.ctipopago = FFM.ctipopago  \r\n" + "	AND PAGDET.nfoliopago = FFM.nfoliopago \r\n" + "  WHERE  PAGENC.nFolioSICOP = " + folioSICOP + " \r\n" + "	AND PAGENC.caNoContrarrecibo = '" + CXP + "' \r\n" + "	AND (mPasivoDiferido = 0 OR mPasivoDiferido IS NULL)\r\n" + "   AND (ISNULL((SELECT CASE WHEN cEvento LIKE 'DICE%' THEN RECDET.mImporte - PAGDET.mImporte END AS IMPORTE FROM tRectificacionAutDetalle AS RECDET WITH (NOLOCK) WHERE RECENC.nFolioRectificacionAut = RECDET.nFolioRectificacionAut AND cEvento LIKE 'DICE%' AND PAGDET.EP = RECDET.EP ),PAGDET.mImporte)) <> 0 \r\n" + "\r\n" + "	UNION\r\n" + "\r\n" + "	SELECT '" + CXP + "'             AS caNoContrarrecibo \r\n" + "	, Isnull(ffm.nidprograma, -1)  programa\r\n" + "	, Isnull(ffm.csubprograma, -1) SubPrograma\r\n" + "	, ISNULL((CASE WHEN RECDET.cEvento LIKE 'DEBE%' THEN RECDET.mImporte END),0) AS mImportePagado \r\n" + "	, ISNULL((CASE WHEN RECDET.cEvento LIKE 'DEBE%' THEN RECDET.EP END), '') AS EPPAG \r\n" + "	, ISNULL((CASE WHEN RECDET.cEvento LIKE 'DEBE%' THEN RECDET.nDocRenglon END), 0) AS nDocRenglonP \r\n" + "	, ISNULL((CASE WHEN RECDET.cEvento LIKE 'DEBE%' THEN RECDET.cMes END), 0) AS nMesP \r\n" + "  FROM tPagadoEncabezado AS PAGENC WITH (NOLOCK)\r\n" + "  LEFT JOIN tRectificacionAutEncabezado AS RECENC WITH (NOLOCK) ON PAGENC.caNoContrarrecibo = RECENC.caNoContrarrecibo\r\n" + "  LEFT JOIN tRectificacionAutDetalle AS RECDET WITH (NOLOCK) ON RECENC.nFolioRectificacionAut = RECDET.nFolioRectificacionAut and RECDET.cEvento LIKE 'DEBE%' " + "  LEFT OUTER JOIN (SELECT 'FEDERALIZADO'         AS cTipoPago, \r\n" + "	nfoliopagofederalizado AS nFolioPago,  \r\n" + "	nidprograma,  \r\n" + "	csubprograma  \r\n" + "	FROM   tpagofederalizadoencabezado WITH(nolock)  \r\n" + "	 WHERE  cdocumentohaplicado = 'S'  \r\n" + "	AND nidprograma IS NOT NULL  \r\n" + "	AND csubprograma IS NOT NULL  \r\n" + "	UNION  \r\n" + "	SELECT 'RELACIONGASTOS'     AS cTipoPago, \r\n" + "	nfoliorelaciongastos AS nFolioPago, \r\n" + "	nidprograma,  \r\n" + "	csubprograma  \r\n" + "	FROM   dbo.trelaciongastosencabezado \r\n" + "	WHERE  nidprograma IS NOT NULL  \r\n" + "	AND csubprograma IS NOT NULL \r\n" + "	AND cdocumentohaplicado = 'S') FFM \r\n" + "	ON PAGENC.ctipopago = FFM.ctipopago  \r\n" + "	AND PAGENC.nfoliopago = FFM.nfoliopago \r\n" + "  WHERE  PAGENC.nFolioSICOP = ? \r\n" + "	AND PAGENC.caNoContrarrecibo = ? \r\n" + "   AND (ISNULL((CASE WHEN RECDET.cEvento LIKE 'DEBE%' THEN RECDET.mImporte END),0)) <> 0 \r\n";
            } else {
                SqlD = " SELECT nDocRenglon AS nDocRenglonP, " + " 		Ep as EPPAG, " + " 		mTotal AS mImportePagado, " + " 		cMes AS nMesP, " + " 		caNoContrarrecibo, " + " 		'-1' AS SubPrograma, " + " 		'-1' AS programa " + " FROM tOperAjenasDetalle WITH (NOLOCK) " + " WHERE nFolioOperAjenas = (SELECT nFolioOperAjenas " + "							FROM tOperAjenasEncabezado WITH (NOLOCK) " + "							WHERE caNoContrarrecibo = ? ) " + " ORDER  BY nDocRenglon ";
            }
        }
        rectificacion = queryPagadoRect(conn, folioSICOP, SqlH, SqlD, CXP, contrarrecibo);
        return rectificacion;
    }

    public static Rectificacion filtraFolioSIAFF(Connection conn, String folioSIAFF) throws Exception {
        Rectificacion rectificacion = null;
        String SqlH = "SELECT " + "			case cTipoPago  when 'PAGOOBRA' then 'COMPROMISO'" + "							when 'PAGODIVERSO' then 'COMPROMISO'" + "							when 'PAGODIRECTO' then 'DIRECTA' " + "							when 'RELACIONGASTOS' then 'DIRECTA' " + "							when 'NOMINA' then 'COMPROMISO' when 'AJENAS' then 'DIRECTA' when 'FEDERALIZADO' then 'COMPROMISO' " + "							ELSE '' END AS dTipoPago " + "			,nFolioPagado" + "			,caNoContrarrecibo" + "			,nFolioPoliza" + "			,cTipoPoliza" + "			,cDescripcionPoliza" + "			,cUnidadResponsableContable" + "			,nFolioSICOP " + " FROM tPagadoEncabezado WHERE nFolioSIAFF = ? AND cDocumentoHaplicado = 'S'";
        String SqlD = "SELECT * FROM tPagadoDetalle WHERE nFolioPagado = (SELECT nFolioPagado FROM tPagadoEncabezado WHERE nFolioSIAFF = ? ) ORDER BY nDocRenglon";
        rectificacion = queryPagado(conn, folioSIAFF, SqlH, SqlD, "");
        return rectificacion;
    }

    public static Rectificacion filtraFolioSAI(Connection conn, String folioSAI) throws Exception {
        Rectificacion rectificacion = null;
        String SqlH = "SELECT " + "			case cTipoPago  when 'PAGOOBRA' then 'COMPROMISO'" + "							when 'PAGODIVERSO' then 'COMPROMISO'" + "							when 'PAGODIRECTO' then 'DIRECTA' " + "							when 'RELACIONGASTOS' then 'DIRECTA' " + "							when 'NOMINA' then 'COMPROMISO' when 'AJENAS' then 'DIRECTA' when 'FEDERALIZADO' then 'COMPROMISO' " + "							ELSE '' END AS dTipoPago " + "			,nFolioPAGado" + "			,caNoContrarrecibo" + "			,nFolioPoliza" + "			,cTipoPoliza" + "			,cDescripcionPoliza" + "			,cUnidadResponsableContable" + "			,nFolioSICOP" + " FROM tPagadoEncabezado with (nolock) WHERE nFolioSAI = ? AND cDocumentoHaplicado = 'S'";
        String SqlD = "SELECT * FROM tPagadoDetalle with (nolock) WHERE nFolioPagado = (SELECT nFolioPagado FROM tPagadoEncabezado WHERE nFolioSAI = ? ) ORDER BY nDocRenglon";
        rectificacion = queryPagado(conn, folioSAI, SqlH, SqlD, "");
        return rectificacion;
    }

    public static StringBuffer filtraTipoCLC(Connection conn, String tipoCLC) throws Exception {
        StringBuffer sbTipo = new StringBuffer();
        return sbTipo;
    }

    public static boolean insertarEncabezado(Connection conn, int folio, String nIDCaso, String cEjercicio, String cRamo, String cUnidad, String cCentroContable, java.sql.Date fExp, java.sql.Date fApl, String conceptoRectificacion, String cTipoMovto, String nOrigenPPTO, String nMes, String oficioRectif, String ctr_int, String cTipoRectificacion, String nFolioSICOP, String caNoContrarrecibo, String cTipoPoliza, String cDescripcionPoliza, String u_login, String nFolioPoliza, String cUnidadResponsableContable, String nFolioSIAFF, String totalDebe, String totalDice, String esIP) throws SQLException {
        Integer nFolioRectificacion = folio;
        Integer id_caso = Integer.parseInt(nIDCaso);
        Integer folioSICOP = Integer.parseInt(nFolioSICOP);
        Integer origenPPTO = Integer.parseInt(nOrigenPPTO);
        Integer mes = Integer.parseInt(nMes);
        PreparedStatement pstmnt = null;
        String insertQuery = "INSERT INTO tRectificacionEncabezado(nFolioRectificacion,nIDCaso,aEjercicioFiscal,cRamo,cUnidadResponsable" + ",cCentroContable,fExp,fAplicacion,cConceptoRectificacion,cTipoMovto,nOrigenPPTO" + "	 ,nMes,oficioRectif,ctr_int,cTipoRectificacion,nFolioSICOP,caNoContrarrecibo" + " ,nFolioPoliza,cTipoPoliza,cDescripcionPoliza,cU_LoginCaptura" + "	,cUnidadResponsableContable,nFolioSIAFF,totalDebe,totalDice, cEsIP) " + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        pstmnt = conn.prepareStatement(insertQuery);
        // introducir parametros para condiciones
        pstmnt.setInt(1, nFolioRectificacion);
        pstmnt.setInt(2, id_caso);
        pstmnt.setString(3, cEjercicio);
        pstmnt.setString(4, cRamo);
        pstmnt.setString(5, cUnidad);
        pstmnt.setString(6, cCentroContable);
        pstmnt.setDate(7, fExp);
        pstmnt.setDate(8, fApl);
        pstmnt.setString(9, conceptoRectificacion);
        pstmnt.setString(10, cTipoMovto);
        pstmnt.setInt(11, origenPPTO);
        pstmnt.setInt(12, mes);
        pstmnt.setString(13, oficioRectif);
        pstmnt.setString(14, ctr_int);
        pstmnt.setString(15, cTipoRectificacion);
        pstmnt.setInt(16, folioSICOP);
        pstmnt.setString(17, caNoContrarrecibo.trim());
        pstmnt.setString(18, nFolioPoliza);
        pstmnt.setString(19, cTipoPoliza);
        pstmnt.setString(20, cDescripcionPoliza);
        pstmnt.setString(21, u_login);
        pstmnt.setString(22, "RHQ");
        pstmnt.setString(23, nFolioSIAFF);
        pstmnt.setDouble(24, Double.parseDouble(totalDebe));
        pstmnt.setDouble(25, Double.parseDouble(totalDice));
        pstmnt.setString(26, esIP);
        // ejecuta el query y guarda resultados en un ResultSet
        if (pstmnt.execute()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean insertarDetalle(Connection conn, ArrayList<RectificacionDetalle> rectificaciones) throws SQLException {
        PreparedStatement pstmnt = null;
        boolean val = false;
        try {
            // if(!rd.getRenglon().contains("R")){ //esto lo hago porque marthi
            // pone una R en la tabla para saber si es repetido
            for (int i = 0; i < rectificaciones.size(); i++) {
                RectificacionDetalle rd = new RectificacionDetalle();
                rd = rectificaciones.get(i);
                String insertQuery = "INSERT INTO tRectificacionDetalle(nFolioRectificacion,nDocRenglon,cEvento,EP,mImporte,mImporteNegativo,cMes,cCentroContable,nidprograma,cSubPrograma,caNoContrarrecibo) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
                pstmnt = conn.prepareStatement(insertQuery);
                pstmnt.setInt(1, rd.getFolio());
                pstmnt.setInt(2, i + 1);
                pstmnt.setString(3, rd.getEvento());
                pstmnt.setString(4, rd.getEp());
                pstmnt.setDouble(5, rd.getImporte());
                pstmnt.setDouble(6, rd.getImporteneg());
                pstmnt.setInt(7, rd.getMes());
                pstmnt.setString(8, rd.getCentro());
                pstmnt.setString(9, rd.getnidprograma());
                pstmnt.setString(10, rd.getcSubPrograma());
                pstmnt.setString(11, rd.getcaNoContrarrecibo());
                val = pstmnt.execute();
            }
            // }
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
        }
        return val;
    }

    public static Rectificacion queryPagado(Connection conn, String criterio, String SqlH, String SqlD, String CXP) throws URInaccesibleException, CLCNoPagadaException, Exception {
        PreparedStatement pstmntH = null;
        ResultSet rsH = null;
        PreparedStatement pstmntD = null;
        ResultSet rsD = null;
        PreparedStatement pstmntT = null;
        ResultSet rsT = null;
        Rectificacion rectificacion = null;
        ReintegrosBusinessLogic recPresBL = new ReintegrosBusinessLogic(GestionInterface.ATT_CONEXION);
        // Realizar conexion
        log.debug("Object: {}", "[SQLH]" + SqlH + "[CxP=" + criterio + "]");
        log.debug("Object: {}", "[SQLD]" + SqlD + "[CxP=" + criterio + "]");
        try {
            pstmntH = conn.prepareStatement(SqlH);
            // introducir parametros para condiciones
            pstmntH.setString(1, criterio);
            if (!"".equals(CXP) && CXP != null)
                pstmntH.setString(2, CXP);
            // ejecuta el query y guarda resultados en un ResultSet
            rsH = pstmntH.executeQuery();
            boolean mismaUR = true;
            // Mientras traiga resultados de Encabezado
            if (rsH.next()) {
                RectificacionEncabezado encabezado = null;
                List<RectificacionDetalle> detalle = null;
                rectificacion = new Rectificacion();
                if ("S".equals(rsH.getString("cDocumentoHaplicado"))) {
                    encabezado = new RectificacionEncabezado();
                    encabezado.setTipoPago(rsH.getString("dTipoPago"));
                    encabezado.setFolioPagado(rsH.getInt("nFolioPAGado"));
                    encabezado.setCaNoContrarrecibo(rsH.getString("caNoContrarrecibo"));
                    encabezado.setFolioPolizaPagado(rsH.getInt("nFolioPoliza"));
                    encabezado.setnFolioSIAFF(rsH.getString("nFolioSIAFF"));
                    encabezado.setcDescripcionPoliza(rsH.getString("cDescripcionPoliza"));
                    encabezado.setcUnidadResponsableContable(rsH.getString("cUnidadResponsableContable"));
                    encabezado.setnFolioSicop(rsH.getString("nFolioSICOP"));
                    if (getUR() != null && !"GERENCIA".equals(getUR())) {
                        String tipoPago = ("FEDERALIZADO".equalsIgnoreCase(rsH.getString("cTipoPago")) ? "PAGO" : ("AJENAS".equalsIgnoreCase(rsH.getString("cTipoPago")) ? "OPER" : "")) + rsH.getString("cTipoPago");
                        tipoPago += "Encabezado";
                        String tabla = "t" + tipoPago;
                        String query = "SELECT cUnidadResponsable FROM " + tabla + " WHERE caNoContrarrecibo=?";
                        pstmntT = conn.prepareStatement(query);
                        // introducir parametros para condiciones
                        pstmntT.setString(1, rsH.getString("caNoContrarrecibo"));
                        // ejecuta el query y guarda resultados en un ResultSet
                        rsT = pstmntT.executeQuery();
                        while (rsT.next()) {
                            if (!getUR().trim().equals(rsT.getString("cUnidadResponsable").trim())) {
                                mismaUR = false;
                            }
                        }
                    }
                    if (mismaUR) {
                        detalle = new ArrayList<RectificacionDetalle>();
                        pstmntD = conn.prepareStatement(SqlD);
                        pstmntD.setString(1, criterio);
                        if (!"".equals(CXP) && CXP != null)
                            pstmntD.setString(2, CXP);
                        rsD = pstmntD.executeQuery();
                        // Mientras traiga resultados de Detalles
                        while (rsD.next()) {
                            RectificacionDetalle rDetalle = new RectificacionDetalle();
                            double nRemanente = recPresBL.getRemanente(rsD.getString("EP"), encabezado.getCaNoContrarrecibo(), Integer.parseInt(rsD.getString("nDocRenglon")));
                            rDetalle.setImporteRemanente(nRemanente);
                            rDetalle.setImporte(rsD.getDouble("mImporteNeto"));
                            rDetalle.setRenglon(rsD.getInt("nDocRenglon"));
                            rDetalle.setMes(rsD.getInt("nMes"));
                            rDetalle.setEp(rsD.getString("EP"));
                            rDetalle.setEvento("DICE");
                            detalle.add(rDetalle);
                        }
                        rectificacion.setEncabezado(encabezado);
                        rectificacion.setDetalle(detalle);
                    } else {
                        throw new URInaccesibleException("La Unidad Ejecutora del pago no corresponde a la del usuario: " + getUR());
                    }
                } else {
                    throw new CLCNoPagadaException("La CLC " + CXP + " no se encuentra pagada");
                }
            }
            return rectificacion;
        } catch (Exception s) {
            log.error(s.getMessage(), s);
            throw s;
        } finally {
            CloseObject.closeObject(pstmntH, false);
            CloseObject.closeObject(rsH, false);
            CloseObject.closeObject(pstmntD, false);
            CloseObject.closeObject(rsD, false);
            CloseObject.closeObject(pstmntT, false);
            CloseObject.closeObject(rsT, false);
        }
    }

    public static Rectificacion queryPagadoRect(Connection conn, String criterio, String SqlH, String SqlD, String CXP, String contrarrecibo) throws URInaccesibleException, CLCNoPagadaException, Exception {
        PreparedStatement pstmntH = null;
        ResultSet rsH = null;
        PreparedStatement pstmntD = null;
        ResultSet rsD = null;
        PreparedStatement pstmntT = null;
        ResultSet rsT = null;
        Rectificacion rectificacion = null;
        ReintegrosBusinessLogic recPresBL = new ReintegrosBusinessLogic(GestionInterface.ATT_CONEXION);
        // Realizar conexion
        log.debug("Object: {}", "[SQLH]" + SqlH + "[CxP=" + criterio + "]");
        log.debug("Object: {}", "[SQLD]" + SqlD + "[CxP=" + criterio + "]");
        try {
            pstmntH = conn.prepareStatement(SqlH);
            // introducir parametros para condiciones
            pstmntH.setString(1, criterio);
            if (!"".equals(CXP) && CXP != null)
                pstmntH.setString(2, CXP);
            // ejecuta el query y guarda resultados en un ResultSet
            rsH = pstmntH.executeQuery();
            boolean mismaUR = true;
            // Mientras traiga resultados de Encabezado
            if (rsH.next()) {
                RectificacionEncabezado encabezado = null;
                List<RectificacionDetalle> detalle = null;
                rectificacion = new Rectificacion();
                if ("S".equals(rsH.getString("cDocumentoHaplicado"))) {
                    encabezado = new RectificacionEncabezado();
                    encabezado.setTipoPago(rsH.getString("dTipoPago"));
                    encabezado.setFolioPagado(rsH.getInt("nFolioPAGado"));
                    encabezado.setCaNoContrarrecibo(rsH.getString("caNoContrarrecibo"));
                    encabezado.setFolioPolizaPagado(rsH.getInt("nFolioPoliza"));
                    encabezado.setnFolioSIAFF(rsH.getString("nFolioSIAFF"));
                    encabezado.setcDescripcionPoliza(rsH.getString("cDescripcionPoliza"));
                    encabezado.setcUnidadResponsableContable(rsH.getString("cUnidadResponsableContable"));
                    encabezado.setnFolioSicop(rsH.getString("nFolioSICOP"));
                    if (getUR() != null && !"GERENCIA".equals(getUR())) {
                        String tipoPago = ("FEDERALIZADO".equalsIgnoreCase(rsH.getString("cTipoPago")) ? "PAGO" : ("AJENAS".equalsIgnoreCase(rsH.getString("cTipoPago")) ? "OPER" : "")) + rsH.getString("cTipoPago");
                        tipoPago += "Encabezado";
                        String tabla = "t" + tipoPago;
                        String query = "SELECT cUnidadResponsable FROM " + tabla + " WHERE caNoContrarrecibo=?";
                        pstmntT = conn.prepareStatement(query);
                        // introducir parametros para condiciones
                        pstmntT.setString(1, rsH.getString("caNoContrarrecibo"));
                        // ejecuta el query y guarda resultados en un ResultSet
                        rsT = pstmntT.executeQuery();
                        while (rsT.next()) {
                            if (!getUR().trim().equals(rsT.getString("cUnidadResponsable").trim())) {
                                mismaUR = false;
                            }
                        }
                    }
                    if (mismaUR) {
                        detalle = new ArrayList<RectificacionDetalle>();
                        pstmntD = conn.prepareStatement(SqlD);
                        if ("".equals(contrarrecibo)) {
                            pstmntD.setString(1, criterio);
                        } else {
                            pstmntD.setString(1, CXP);
                        }
                        if (!"".equals(CXP) && CXP != null)
                            if ("".equals(contrarrecibo)) {
                                pstmntD.setString(2, CXP);
                            }
                        //else{
                        //pstmntD.setString(2, contrarrecibo);
                        //}
                        rsD = pstmntD.executeQuery();
                        // Mientras traiga resultados de Detalles
                        while (rsD.next()) {
                            RectificacionDetalle rDetalle = new RectificacionDetalle();
                            double nRemanente = recPresBL.getRemanente(rsD.getString("EPPAG"), encabezado.getCaNoContrarrecibo(), Integer.parseInt(rsD.getString("nDocRenglonP")));
                            BigDecimal remanenteBig = new BigDecimal(0);
                            remanenteBig = getRemanenteBig(conn, rsD.getString("EPPAG"), encabezado.getCaNoContrarrecibo(), Integer.parseInt(rsD.getString("nDocRenglonP")));
                            rDetalle.setRemanente(remanenteBig);
                            rDetalle.setimporteNeto(rsD.getBigDecimal("mImportePagado").setScale(2, RoundingMode.HALF_UP));
                            rDetalle.setRenglon(rsD.getInt("nDocRenglonP"));
                            rDetalle.setMes(rsD.getInt("nMesP"));
                            rDetalle.setEp(rsD.getString("EPPAG"));
                            rDetalle.setEvento("DICE");
                            rDetalle.setcaNoContrarrecibo(rsD.getString("caNoContrarrecibo"));
                            rDetalle.setcSubPrograma(rsD.getString("SubPrograma"));
                            rDetalle.setnidprograma(rsD.getString("programa"));
                            detalle.add(rDetalle);
                        }
                        rectificacion.setEncabezado(encabezado);
                        rectificacion.setDetalle(detalle);
                    } else {
                        throw new URInaccesibleException("La Unidad Ejecutora del pago no corresponde a la del usuario: " + getUR());
                    }
                } else {
                    throw new CLCNoPagadaException("La CLC " + CXP + " no se encuentra pagada");
                }
            }
            return rectificacion;
        } catch (Exception s) {
            log.error(s.getMessage(), s);
            throw s;
        } finally {
            CloseObject.closeObject(pstmntH, false);
            CloseObject.closeObject(rsH, false);
            CloseObject.closeObject(pstmntD, false);
            CloseObject.closeObject(rsD, false);
            CloseObject.closeObject(pstmntT, false);
            CloseObject.closeObject(rsT, false);
        }
    }

    @SuppressWarnings("resource")
    public static void autorizaRectificacion(Connection conn, Caso c, String ulogin, String prefixPath, String tipoAplicacion, String fApl) throws SQLException, GestionException {
        PreparedStatement pstmnt = null, pstmnt1 = null;
        int nIdCaso;
        String cSQLString = "", IngPropio = "";
        ResultSet rs = null, rs2 = null, rs3 = null, rs4 = null;
        int nExiste = 0;
        String EsIP = "", EsFFM = "N", esAL = "N";
        //String sqlInsertDetalle = "";
        StringBuilder sqlInsertDetalle = new StringBuilder();
        StringBuilder IngFFM = new StringBuilder();
        StringBuilder AL = new StringBuilder();
        try {
            nIdCaso = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
            cSQLString = "select COUNT(*) from tRectificacionAutEncabezado with (nolock) WHERE nFolioRectificacionAut = " + nIdCaso;
            pstmnt = conn.prepareStatement(cSQLString);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                nExiste = rs.getInt(1);
            }
            IngPropio = "SELECT cEsIP FROM dbo.tRectificacionEncabezado WHERE nFolioRectificacion = " + nIdCaso;
            pstmnt = conn.prepareStatement(IngPropio);
            rs2 = pstmnt.executeQuery();
            if (rs2.next()) {
                EsIP = rs2.getString(1);
            }
            if (nExiste == 0) {
                pstmnt = conn.prepareStatement("INSERT INTO tRectificacionAutEncabezado (nFolioRectificacionAut, nIdCaso, aEjercicioFiscal, cRamo, cUnidadResponsable, cCentroContable, fExp, fAplicacion, cConceptoRectificacion, cTipoMovto, " + "nOrigenPPTO,nMes, oficioRectif, ctr_int,cTipoRectificacion, nCLC, nFolioSicop,nFolioSAI, nFolioSIAFF,caNoContrarrecibo,cTipoPoliza,fCancelacion,cU_LoginCaptura,cRevisado,cU_LoginRevision,cMotivoRechazo,nFolioTramiteSicop,cUnidadResponsableContable,totalDebe, cEsIP, cDescripcionPoliza) SELECT nFolioRectificacion, nIdCaso, aEjercicioFiscal, cRamo, cUnidadResponsable, cCentroContable, fExp, " + "'" + fApl + "', cConceptoRectificacion, cTipoMovto, " + "nOrigenPPTO,nMes, oficioRectif, ctr_int,cTipoRectificacion, nCLC, nFolioSicop,nFolioSAI, nFolioSIAFF,caNoContrarrecibo,'DI',fCancelacion,cU_LoginCaptura,cRevisado,cU_LoginRevision,cMotivoRechazo,nFolioTramiteSicop,cUnidadResponsableContable,totalDebe,cEsIP,cConceptoRectificacion FROM tRectificacionEncabezado WITH (NOLOCK) WHERE nFolioRectificacion = ?");
                pstmnt.setInt(1, nIdCaso);
                pstmnt.execute();
                /*pstmnt = conn.prepareStatement("SELECT cTipoRectificacion FROM tRectificacionEncabezado WITH (NOLOCK) WHERE nFolioRectificacion = ?");
				pstmnt.setInt(1, nIdCaso);
				rs = pstmnt.executeQuery();
				String tipoRect = "";
				if (rs.next()) 
					tipoRect = rs.getString(1);
					*/
            }
            IngFFM.append("SELECT CASE WHEN RFC = 'BMN930209927' THEN 'S' ELSE 'N' END esFFM ");
            IngFFM.append("FROM dbo.tRectificacionEncabezado AS r ");
            IngFFM.append(" JOIN dbo.tPagadoEncabezado AS p ON r.caNoContrarrecibo = p.caNoContrarrecibo ");
            IngFFM.append(" JOIN dbo.tPagadoDetalle AS pd ON p.nFolioPagado = pd.nFolioPagado ");
            IngFFM.append(" WHERE mPasivoDiferido = 0 AND nFolioRectificacion = " + nIdCaso);
            pstmnt = conn.prepareStatement(IngFFM.toString());
            rs3 = pstmnt.executeQuery();
            if (rs3.next()) {
                EsFFM = rs3.getString(1);
            }
            AL.append("SELECT TOP 1 CASE WHEN SUBSTRING(cEvento, 3,4) = 'ALAL' THEN 'S' ELSE 'N' END esAL\r\n");
            AL.append(" FROM dbo.tRectificacionEncabezado AS r \r\n");
            AL.append(" JOIN dbo.tPagadoEncabezado AS p ON r.caNoContrarrecibo = p.caNoContrarrecibo \r\n");
            AL.append(" JOIN dbo.tPagadoDetalle AS pd ON p.nFolioPagado = pd.nFolioPagado \r\n");
            AL.append(" WHERE mPasivoDiferido = 0 AND nFolioRectificacion = " + nIdCaso);
            pstmnt1 = conn.prepareStatement(AL.toString());
            rs4 = pstmnt1.executeQuery();
            if (rs4.next()) {
                esAL = rs4.getString(1);
            }
            /*ARLA PARA CALCULAR EL EVENTO DE UN PAGO DE ALMACEN tEventoRectificacion_AL*/
            if ("S".equals(esAL)) {
                sqlInsertDetalle.append("INSERT INTO tRectificacionAutDetalle(nFolioRectificacionAut,nDocRenglon,EP,cEvento,mImporte, mImporteNegativo, cMes, cCentroContable, OBGT, nidprograma, cSubPrograma, caNoContrarrecibo) ");
                sqlInsertDetalle.append("SELECT rd.nfoliorectificacion, ");
                sqlInsertDetalle.append("       rd.ndocrenglon, ");
                sqlInsertDetalle.append("       rd.ep, ");
                sqlInsertDetalle.append("       CASE ( Substring(cevento, 1, 4) ) ");
                sqlInsertDetalle.append("       	WHEN 'DICE' THEN 'DICE_AL_DI_' + cnoevento ");
                sqlInsertDetalle.append("          WHEN 'DEBE' THEN 'DEBE_DECIR_AL_' + cnoevento ");
                sqlInsertDetalle.append("       END AS cEvento, ");
                sqlInsertDetalle.append("       rd.mimporte, ");
                sqlInsertDetalle.append("       rd.mimportenegativo, ");
                sqlInsertDetalle.append("       rd.cmes, ");
                sqlInsertDetalle.append("       rd.ccentrocontable, ");
                sqlInsertDetalle.append("       Substring(ep, 32, 5) AS obgt ");
                sqlInsertDetalle.append("       , rd.nidprograma ");
                sqlInsertDetalle.append("       , rd.cSubPrograma ");
                sqlInsertDetalle.append("       , rd.caNoContrarrecibo ");
                sqlInsertDetalle.append("FROM   trectificaciondetalle rd WITH (nolock) ");
                sqlInsertDetalle.append("       JOIN tRectificacionEncabezado re WITH (NOLOCK) ");
                sqlInsertDetalle.append("       	ON rd.nFolioRectificacion = re.nFolioRectificacion  ");
                sqlInsertDetalle.append("       LEFT OUTER JOIN tEventoRectificacion_AL WITH (nolock) ");
                sqlInsertDetalle.append("         ON Substring(ep, 32, 5) = cpartida ");
                sqlInsertDetalle.append("WHERE  rd.nfoliorectificacion = ?");
            } else if ("N".equals(EsIP) && "N".equals(EsFFM)) {
                /*VGC20160615 Se cambia el calculo del evento. Siempre se calcula sin importar si es reclasificacion o rectificacion. Esto debido al cambio en los tipos de subcuentas del gasto.*/
                sqlInsertDetalle.append("INSERT INTO tRectificacionAutDetalle(nFolioRectificacionAut,nDocRenglon,EP,cEvento,mImporte, mImporteNegativo, cMes, cCentroContable, OBGT, nidprograma, cSubPrograma, caNoContrarrecibo) ");
                sqlInsertDetalle.append("SELECT rd.nfoliorectificacion, ");
                sqlInsertDetalle.append("       rd.ndocrenglon, ");
                sqlInsertDetalle.append("       rd.ep, ");
                sqlInsertDetalle.append("       CASE ( Substring(cevento, 1, 4) ) ");
                sqlInsertDetalle.append("       	WHEN 'DICE' THEN 'DICE_DI_' + cnoevento ");
                sqlInsertDetalle.append("          WHEN 'DEBE' THEN 'DEBE_DECIR_' + cnoevento ");
                sqlInsertDetalle.append("       END AS cEvento, ");
                sqlInsertDetalle.append("       rd.mimporte, ");
                sqlInsertDetalle.append("       rd.mimportenegativo, ");
                sqlInsertDetalle.append("       rd.cmes, ");
                sqlInsertDetalle.append("       rd.ccentrocontable, ");
                sqlInsertDetalle.append("       Substring(ep, 32, 5) AS obgt ");
                sqlInsertDetalle.append("       , rd.nidprograma ");
                sqlInsertDetalle.append("       , rd.cSubPrograma ");
                sqlInsertDetalle.append("       , rd.caNoContrarrecibo ");
                sqlInsertDetalle.append("FROM   trectificaciondetalle rd WITH (nolock) ");
                sqlInsertDetalle.append("       JOIN tRectificacionEncabezado re WITH (NOLOCK) ");
                sqlInsertDetalle.append("       	ON rd.nFolioRectificacion = re.nFolioRectificacion  ");
                sqlInsertDetalle.append("       LEFT OUTER JOIN teventorectificacion WITH (nolock) ");
                sqlInsertDetalle.append("         ON Substring(ep, 32, 5) = cpartida ");
                sqlInsertDetalle.append("WHERE  rd.nfoliorectificacion = ?");
            } else /*ARLA SE CAMBIA EL CALCULO DE EVENTO SI ES RECTIFICACION DE INGRESO PROPIO LEERA LA TABLA tEventoRectificacion_IP*/
            if ("S".equals(EsIP) && "N".equals(EsFFM)) {
                sqlInsertDetalle.append("INSERT INTO tRectificacionAutDetalle(nFolioRectificacionAut,nDocRenglon,EP,cEvento,mImporte, mImporteNegativo, cMes, cCentroContable, OBGT, CTAB, nidprograma, cSubPrograma, caNoContrarrecibo) ");
                sqlInsertDetalle.append("SELECT rd.nfoliorectificacion, ");
                sqlInsertDetalle.append("       rd.ndocrenglon, ");
                sqlInsertDetalle.append("       rd.ep, ");
                sqlInsertDetalle.append("       CASE ( Substring(cevento, 1, 4) ) ");
                sqlInsertDetalle.append("             WHEN 'DICE' THEN 'DICE_IP_DI_' + cnoevento ");
                sqlInsertDetalle.append("             WHEN 'DEBE' THEN 'DEBE_DECIR_IP_' + cnoevento ");
                sqlInsertDetalle.append("       END AS cEvento, ");
                sqlInsertDetalle.append("       rd.mimporte, ");
                sqlInsertDetalle.append("       rd.mimportenegativo, ");
                sqlInsertDetalle.append("       rd.cmes, ");
                sqlInsertDetalle.append("       rd.ccentrocontable, ");
                sqlInsertDetalle.append("       Substring(ep, 32, 5) AS obgt ");
                sqlInsertDetalle.append("		 , (SELECT GP_VALOR FROM CG_GRUPO_PROPIEDADES WITH (NOLOCK) WHERE GP_NOMBRE = 'CUENTA INGRESOS PROPIOS') AS ctab");
                sqlInsertDetalle.append("       , rd.nidprograma ");
                sqlInsertDetalle.append("       , rd.cSubPrograma ");
                sqlInsertDetalle.append("       , rd.caNoContrarrecibo ");
                sqlInsertDetalle.append("FROM   trectificaciondetalle rd WITH (nolock) ");
                sqlInsertDetalle.append("       JOIN tRectificacionEncabezado re WITH (NOLOCK) ");
                sqlInsertDetalle.append("       	ON rd.nFolioRectificacion = re.nFolioRectificacion  ");
                sqlInsertDetalle.append("       LEFT OUTER JOIN tEventoRectificacion_IP WITH (nolock) ");
                sqlInsertDetalle.append("       	ON Substring(ep, 32, 5) = cpartida ");
                sqlInsertDetalle.append("WHERE  rd.nfoliorectificacion = ?");
            } else /*ARLA SE CAMBIA EL CALCULO DE EVENTO SI ES UN PAGO DEL FONDO FORESTAL MEXICANO Y ES DE INGRESO FISCAL*/
            if ("N".equals(EsIP) && "S".equals(EsFFM)) {
                sqlInsertDetalle.append("INSERT INTO tRectificacionAutDetalle(nFolioRectificacionAut,nDocRenglon,EP,cEvento,mImporte, mImporteNegativo, cMes, cCentroContable, OBGT, nidprograma, cSubPrograma, caNoContrarrecibo, FFM) ");
                sqlInsertDetalle.append("SELECT rd.nfoliorectificacion, ");
                sqlInsertDetalle.append("       rd.ndocrenglon, ");
                sqlInsertDetalle.append("       rd.ep, ");
                sqlInsertDetalle.append("       CASE ( Substring(cevento, 1, 4) ) ");
                sqlInsertDetalle.append("             WHEN 'DICE' THEN 'DICE_FFM_DI_' + cnoevento ");
                sqlInsertDetalle.append("             WHEN 'DEBE' THEN 'DEBE_DECIR_FFM_' + cnoevento ");
                sqlInsertDetalle.append("       END AS cEvento, ");
                sqlInsertDetalle.append("       rd.mimporte, ");
                sqlInsertDetalle.append("       rd.mimportenegativo, ");
                sqlInsertDetalle.append("       rd.cmes, ");
                sqlInsertDetalle.append("       rd.ccentrocontable, ");
                sqlInsertDetalle.append("       Substring(ep, 32, 5) AS obgt ");
                sqlInsertDetalle.append("       , rd.nidprograma ");
                sqlInsertDetalle.append("       , rd.cSubPrograma ");
                sqlInsertDetalle.append("       , rd.caNoContrarrecibo ");
                sqlInsertDetalle.append(" 	  	, CONVERT(VARCHAR(5), Substring(ep, 32, 5)) + CONVERT(VARCHAR(4), CASE WHEN LTRIM(RTRIM(nidprograma)) = '13' AND LTRIM(RTRIM(cSubPrograma)) = '45' THEN '0000' ELSE SUBSTRING(EP,1,4) END) + RIGHT('00' + LTRIM(RTRIM(nidprograma)),2) + RIGHT('00' + LTRIM(RTRIM(cSubPrograma)),2) + '00' AS ffm ");
                sqlInsertDetalle.append("FROM   trectificaciondetalle rd WITH (nolock) ");
                sqlInsertDetalle.append("       JOIN tRectificacionEncabezado re WITH (NOLOCK) ");
                sqlInsertDetalle.append("       	ON rd.nFolioRectificacion = re.nFolioRectificacion  ");
                sqlInsertDetalle.append("       LEFT OUTER JOIN teventorectificacion_FFM WITH (nolock) ");
                sqlInsertDetalle.append("         ON Substring(ep, 32, 5) = cpartida ");
                sqlInsertDetalle.append("WHERE  rd.nfoliorectificacion = ? ");
            }
            pstmnt = conn.prepareStatement(sqlInsertDetalle.toString());
            pstmnt.setInt(1, nIdCaso);
            pstmnt.execute();
            conn.commit();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            if (rs != null)
                rs.close();
            pstmnt = null;
            rs = null;
        }
    }

    public static void autorizaRectificacionMil(Connection conn, Caso c, String ulogin, String prefixPath, String tipoAplicacion) throws SQLException, GestionException {
        PreparedStatement pstmnt = null;
        int nIdCaso;
        String cSQLString = "";
        ResultSet rs = null;
        int nExiste = 0;
        try {
            nIdCaso = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
            cSQLString = "select COUNT(*) from tRectificacionAutEncabezadoMil with (nolock) WHERE nFolioRectificacionMilAut = " + nIdCaso;
            pstmnt = conn.prepareStatement(cSQLString);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                nExiste = rs.getInt(1);
            }
            if (nExiste == 0) {
                pstmnt = conn.prepareStatement("INSERT INTO tRectificacionAutEncabezadoMil (nFolioRectificacionMilAut, nIdCaso, aEjercicioFiscal, cRamo, cUnidadResponsable, cCentroContable, fExp, fAplicacion, cConceptoRectificacion, cTipoMovto, " + "nOrigenPPTO,nMes, oficioRectif, ctr_int,cTipoRectificacion, nCLC, nFolioSicop,nFolioSAI, nFolioSIAFF,caNoContrarrecibo,caNoContrarreciboMil,cTipoPoliza,fCancelacion,cU_LoginCaptura,cRevisado,cU_LoginRevision,cMotivoRechazo,nFolioTramiteSicop,cUnidadResponsableContable,totalDebe,cDescripcionPoliza) SELECT nFolioRectificacionMil, nIdCaso, aEjercicioFiscal, cRamo, cUnidadResponsable, cCentroContable, fExp, fAplicacion, cConceptoRectificacion, cTipoMovto, " + "nOrigenPPTO,nMes, oficioRectif, ctr_int,cTipoRectificacion, nCLC, nFolioSicop,nFolioSAI, nFolioSIAFF,caNoContrarrecibo,caNoContrarreciboMil,'DI',fCancelacion,cU_LoginCaptura,cRevisado,cU_LoginRevision,cMotivoRechazo,nFolioTramiteSicop,cUnidadResponsableContable,totalDebe,cConceptoRectificacion FROM tRectificacionEncabezadoMil WITH (NOLOCK) WHERE nFolioRectificacionMil = ?");
                // pstmnt.setString(1, nNumSicop);
                /*
				 * pstmnt.setString(2, cRecMotivSicop); pstmnt.setString(3,
				 * nNumMAP); pstmnt.setString(4, cRecMotivMAP);
				 */
                pstmnt.setInt(1, nIdCaso);
                pstmnt.execute();
                pstmnt = conn.prepareStatement("SELECT cTipoRectificacion FROM tRectificacionEncabezadoMil WITH (NOLOCK) WHERE nFolioRectificacionMil = ?");
                pstmnt.setInt(1, nIdCaso);
                rs = pstmnt.executeQuery();
                String tipoRect = "";
                if (rs.next()) {
                    tipoRect = rs.getString(1);
                }
                /*
				 * if ("DIRECTA".equals(tipoRect)) { tipoRect = "DICE_AUT_DI"; }
				 * else { tipoRect = "DICE_AUT_CO"; }
				 */
                pstmnt = conn.prepareStatement("INSERT INTO tRectificacionAutDetalleMil(nFolioRectificacionMilAut,nDocRenglon,EP,cEvento,mImporte, mImporteNegativo, cMes, cCentroContable, OBGT) " + "SELECT rd.nFolioRectificacionMil,rd.nDocRenglon,rd.EP,CASE ( Substring(cevento, 1, 4) ) " + " WHEN 'DICE' THEN 'DICE_DI_' + cnoevento " + " WHEN 'DEBE' THEN 'DEBE_DECIR_' + cnoevento END AS cEvento,rd.mImporte,rd.mImporteNegativo,rd.cMes, rd.cCentroContable,SUBSTRING(EP,32,5) AS obgt " + " FROM tRectificacionDetalleMil rd WITH (NOLOCK) JOIN tEventoRectificacion WITH (NOLOCK) ON SUBSTRING(EP,32,5) = cPartida WHERE rd.nFolioRectificacionMil = ?");
                pstmnt.setInt(1, nIdCaso);
                // pstmnt.setInt(1, nIdCaso); GETEVENTO AUTORIZADO
                pstmnt.execute();
                // CasoBusinessLogic cbl = new
                // CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            }
            conn.commit();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            if (rs != null)
                rs.close();
            pstmnt = null;
            rs = null;
        }
    }

    public static RectificacionEncabezado getRectificacionEncabezado(Connection conn, int folio) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        RectificacionEncabezado re = null;
        pstm = conn.prepareStatement("SELECT * FROM tRectificacionEncabezado WITH(NOLOCK) WHERE nFolioRectificacion = ?");
        pstm.setInt(1, folio);
        res = pstm.executeQuery();
        if (res.next()) {
            re = new RectificacionEncabezado();
            re.setnFolioRectificacion(Integer.parseInt(res.getString("nFolioRectificacion")));
            re.setnIdCaso(Integer.parseInt(res.getString("nIDCaso")));
            re.setfExp(res.getString("fExp"));
            re.setfAplicacion(res.getString("fAplicacion"));
            re.setcTipoMovto(res.getString("cTipoMovto"));
            re.setOficioRectif(res.getString("oficioRectif"));
            re.setCtr_int(res.getString("ctr_int"));
            re.setTotalDice(res.getString("totalDice"));
            re.setTotalDebe(res.getString("totalDebe"));
            re.setaEjercicioFiscal(res.getString("aEjercicioFiscal"));
            re.setCaNoContrarrecibo(res.getString("caNoContrarrecibo"));
            re.setcCentroContable(res.getString("cCentroContable"));
            re.setcRamo(res.getString("cRamo"));
            re.setnOrigenPPTO(res.getString("nOrigenPPTO"));
            re.setcConceptoRectificacion(res.getString("cConceptoRectificacion"));
            re.setnFolioSIAFF(res.getString("nFolioSIAFF"));
            re.setnFolioSicop(res.getString("nFolioSICOP"));
            re.setcTipoRectificacion(res.getString("cTipoRectificacion"));
            re.setcDescripcionPoliza(res.getString("cDescripcionPoliza"));
        }
        if (pstm != null) {
            pstm.close();
        }
        if (res != null) {
            res.close();
        }
        return re;
    }

    public static RectificacionEncabezado getRectificacionEncabezadoMil(Connection conn, int folio) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        RectificacionEncabezado re = null;
        pstm = conn.prepareStatement("SELECT * FROM tRectificacionEncabezadoMil WITH(NOLOCK) WHERE nFolioRectificacionMil = ?");
        pstm.setInt(1, folio);
        res = pstm.executeQuery();
        if (res.next()) {
            re = new RectificacionEncabezado();
            re.setnFolioRectificacion(res.getInt("nFolioRectificacionMil"));
            re.setnIdCaso(Integer.parseInt(res.getString("nIDCaso")));
            re.setfExp(res.getString("fExp"));
            re.setfAplicacion(res.getString("fAplicacion"));
            re.setcTipoMovto(res.getString("cTipoMovto"));
            re.setOficioRectif(res.getString("oficioRectif"));
            re.setCtr_int(res.getString("ctr_int"));
            re.setTotalDice(res.getString("totalDice"));
            re.setTotalDebe(res.getString("totalDebe"));
            re.setaEjercicioFiscal(res.getString("aEjercicioFiscal"));
            re.setCaNoContrarrecibo(res.getString("caNoContrarrecibo"));
            re.setCaNoContrarreciboMil(res.getString("caNoContrarreciboMil"));
            re.setcCentroContable(res.getString("cCentroContable"));
            re.setcRamo(res.getString("cRamo"));
            re.setnOrigenPPTO(res.getString("nOrigenPPTO"));
            re.setcConceptoRectificacion(res.getString("cConceptoRectificacion"));
            re.setnFolioSIAFF(res.getString("nFolioSIAFF"));
            re.setnFolioSicop(res.getString("nFolioSICOP"));
            re.setcTipoRectificacion(res.getString("cTipoRectificacion"));
            re.setcDescripcionPoliza(res.getString("cDescripcionPoliza"));
        }
        if (pstm != null) {
            pstm.close();
        }
        if (res != null) {
            res.close();
        }
        return re;
    }

    public static RectificacionEncabezado getRectificacionEncabezadoSicop(Connection conn, int folio) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        RectificacionEncabezado re = null;
        pstm = conn.prepareStatement("select 'H' as posicion1, r.fAplicacion, r.fExp, r.cRamo, r.cRamo, r.cRamo, r.cUnidadResponsable, r.cUnidadResponsable, r.cUnidadResponsable, isnull(r.cTipoMovto,'') as cTipoMovto, " + " isnull(r.nOrigenPPTO,'') as nOrigenPPTO, ISNULL(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(r.cConceptoRectificacion, Char(13), ''),Char(10), ''),Char(9), ''),',',''),'.',''),'\"',''),'') as cConceptoRectificacion, " + " isnull(R.ctr_int,'') as ctr_int, R.oficioRectif, R.oficioRectif, R.nFolioRectificacion, R.nIDCaso, R.totalDICE,R.totalDEBE " + " from tRectificacionEncabezado r with (nolock) WHERE nFolioRectificacion=?");
        pstm.setInt(1, folio);
        res = pstm.executeQuery();
        if (res.next()) {
            re = new RectificacionEncabezado();
            re.setPosicion1(res.getString("posicion1"));
            re.setcRamo(res.getString("cRamo"));
            re.setcUnidadResponsable(res.getString("cUnidadResponsable"));
            re.setnOrigenPPTO(res.getString("nOrigenPPTO"));
            re.setcConceptoRectificacion(res.getString("cConceptoRectificacion"));
            re.setnFolioRectificacion(Integer.parseInt(res.getString("nFolioRectificacion")));
            re.setnIdCaso(Integer.parseInt(res.getString("nIDCaso")));
            re.setfExp(res.getString("fExp"));
            re.setfAplicacion(res.getString("fAplicacion"));
            re.setcTipoMovto(res.getString("cTipoMovto"));
            re.setOficioRectif(res.getString("oficioRectif"));
            re.setCtr_int(res.getString("ctr_int"));
            re.setTotalDice(res.getString("totalDice"));
            re.setTotalDebe(res.getString("totalDebe"));
        }
        if (pstm != null) {
            pstm.close();
        }
        if (res != null) {
            res.close();
        }
        return re;
    }

    public static StringBuilder getRectificacionDetalleSicop(Connection conn, int folio) throws Exception {
        ResultSet res = null;
        PreparedStatement pstm = null;
        //String string = "";
        StringBuilder query = new StringBuilder();
        StringBuilder string = new StringBuilder();
        query.append("select distinct isnull(r.nOrigenPPTO,0) as nOrigenPPTO, ");
        query.append("'0', r.nFolioSICOP, d.nDocRenglon, e.cRamoEP, e.cUnidadResponsableEP, ");
        query.append("e.aEjercicioFiscal, e.cGrupoFuncional, e.cFuncion, ");
        query.append("e.cSubFuncion, SUBSTRING(dbo.CambiaEPPlurianual(d.EP), 20,2) AS cProgramaGeneral, ");
        query.append("e.cActividadInstitucional, e.cProgramaPresupuestario, substring(e.cPartida,1,1) as cCapitulo, ");
        query.append("substring(e.cPartida,2,1) as cConcepto, substring(e.cPartida,3,1) as cPartida, substring(e.cPartida,4,2) as cPartidaEspecifica, ");
        query.append("e.cTipoGasto, e.cFuenteFinanciamiento, e.cEntidadFederativa, SUBSTRING( dbo.CambiaEPPlurianual(d.EP), 45,11) AS cartera, '0000000000' as cUnidadEjecutora, ");
        query.append("'00' as cUnidadNormativa, '000','000','00000','00000','0000000000', CONVERT(VARCHAR,d.mImporte), d.cMes, '' as cFillRellen1 , ");
        query.append(" CASE	WHEN	P.cTipoPago = 'RELACIONGASTOS' THEN 'S04929' ");
        query.append("			WHEN (	P.cTipoPago = 'PAGODIVERSO' AND (SELECT DIV.cEsRelacionGastos FROM tPAGODIVERSOEncabezado DIV WITH (NOLOCK) WHERE DIV.caNoContrarrecibo = r.caNoContrarrecibo) = 'S') THEN 'S04929' ");
        query.append("			ELSE b.CBEN END AS CBEN, ");
        query.append("'' as cFillRellen2 , '0' as sol_oli, CASE WHEN cPartida='35801' THEN 'GD' ELSE 'PN' END ID_TIPO_CONCEPTO, '000' as tipo_concepto, ");
        query.append("'0' as concepto_mov, ");
        query.append("'0' retencion_isr, '0', CASE WHEN d.cEvento LIKE '%DICE%' THEN 'A' ELSE 'C' END, isnull(R.ctr_int,'') as ctr_int ");
        query.append("FROM tRectificacionDetalle d with (nolock), tRectificacionEncabezado r with (nolock), tCatalogoEP e with (nolock), tPagadoEncabezado P with (nolock), tPagadoDetalle dp  with (nolock), tBeneficiario b with (nolock) ");
        query.append("WHERE d.nFolioRectificacion =  r.nFolioRectificacion and d.EP =  e.EP and r.caNoContrarrecibo = P.caNoContrarrecibo and p.nFolioPagado = dp.nFolioPagado and dp.RFC = b.dRFC AND ISNULL( dp.mPasivoDiferido, 0.00 ) = 0.00 ");
        query.append("AND r.nFolioRectificacion= ? AND d.nFolioRectificacion= ? ");
        pstm = conn.prepareStatement(query.toString());
        try {
            pstm.setInt(1, folio);
            pstm.setInt(2, folio);
            res = pstm.executeQuery();
            int i = 1;
            while (res.next()) {
                //(1);
                string.append("\r\n" + "764");
                string.append("," + res.getString(2));
                string.append("," + res.getString(3));
                //(4);
                string.append("," + i++);
                string.append("," + res.getString(5));
                string.append("," + res.getString(6));
                string.append("," + res.getString(7));
                string.append("," + res.getString(8));
                string.append("," + res.getString(9));
                string.append("," + res.getString(10));
                string.append("," + res.getString(11));
                string.append("," + res.getString(12));
                string.append("," + res.getString(13));
                string.append("," + res.getString(14));
                string.append("," + res.getString(15));
                string.append("," + res.getString(16));
                string.append("," + res.getString(17));
                string.append("," + res.getString(18));
                string.append("," + res.getString(19));
                string.append("," + res.getString(20));
                string.append("," + res.getString(21));
                string.append("," + res.getString(22));
                string.append("," + res.getString(23));
                string.append("," + res.getString(24));
                string.append("," + res.getString(25));
                string.append("," + res.getString(26));
                string.append("," + res.getString(27));
                string.append("," + res.getString(28));
                string.append("," + res.getString(29));
                string.append("," + res.getString(30));
                string.append("," + res.getString(31));
                string.append("," + res.getString(32));
                string.append("," + res.getString(33));
                string.append("," + res.getString(34));
                string.append("," + res.getString(35));
                string.append("," + res.getString(36));
                string.append("," + res.getString(37));
                string.append("," + res.getString(38));
                string.append("," + res.getString(39));
                string.append("," + res.getString(40));
                string.append("," + res.getString(41));
            }
        } catch (Exception e) {
            throw e;
        } finally {
            CloseObject.closeObject(res, false);
            CloseObject.closeObject(pstm, false);
        }
        return string;
    }

    public static ArrayList<RectificacionDetalle> getRectificacionDetalle(Connection conn, int folio) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        ArrayList<RectificacionDetalle> rds = new ArrayList<RectificacionDetalle>();
        RectificacionDetalle rd = null;
        pstm = conn.prepareStatement("SELECT * FROM tRectificacionDetalle WITH(NOLOCK) WHERE nFolioRectificacion=?");
        pstm.setInt(1, folio);
        res = pstm.executeQuery();
        while (res.next()) {
            rd = new RectificacionDetalle();
            rd.setRenglon(res.getInt("nDocRenglon"));
            rd.setEvento(res.getString("cEvento"));
            rd.setEp(res.getString("EP"));
            rd.setImporte(res.getDouble("mImporte"));
            rd.setImporteneg(res.getDouble("mImporte"));
            rd.setMes(res.getInt("cMes"));
            rd.setCentro(res.getString("cCentroContable"));
            rd.setnidprograma(res.getString("nidprograma"));
            rds.add(rd);
        }
        if (pstm != null) {
            pstm.close();
        }
        if (res != null) {
            res.close();
        }
        return rds;
    }

    // CAMBIAR CON LA MODIFICACIÓN QUE SE HIZO DEL TIPO DE CONCEPTO Y MOVIMIENTO
    // QUE SE PASÓ AL DETALLE Y SE QUITÓ DEL ENCABEZADO
    public static void insertaRectificacionMil(Connection conn, RectificacionEncabezado recE, ArrayList<RectificacionDetalle> recDetalles, int folio, String folioCompleto) throws Exception {
        PreparedStatement psInsertDetalle = null;
        PreparedStatement psInsertEncabezado = null;
        try {
            psInsertEncabezado = conn.prepareStatement("INSERT INTO tRectificacionEncabezadoMil(nFolioRectificacionMil,nIDCaso,cDocumentoHaplicado,aEjercicioFiscal,cRamo,cUnidadResponsable" + " ,cCentroContable,fExp,fAplicacion,cConceptoRectificacion,cTipoMovto,nOrigenPPTO,nMes,oficioRectif,ctr_int,cTipoRectificacion,nCLC" + " ,nFolioSICOP,nFolioSAI,nFolioSIAFF,caNoContrarrecibo,caNoContrarreciboMil,nFolioPoliza,cTipoPoliza,cDescripcionPoliza,nFolioPolizaCancelacion" + " ,fCancelacion,nFolioTramiteSicop,cUnidadResponsableContable,totalDice,totalDebe)" + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            psInsertEncabezado.setInt(1, folio);
            psInsertEncabezado.setInt(2, recE.getnIdCaso());
            psInsertEncabezado.setString(3, "N");
            psInsertEncabezado.setString(4, recE.getaEjercicioFiscal());
            psInsertEncabezado.setString(5, recE.getcRamo());
            psInsertEncabezado.setString(6, recE.getcUnidadResponsable());
            psInsertEncabezado.setString(7, recE.getcCentroContable());
            psInsertEncabezado.setString(8, recE.getfExp());
            psInsertEncabezado.setString(9, recE.getfAplicacion());
            psInsertEncabezado.setString(10, recE.getcConceptoRectificacion());
            if (recE.getcTipoMovto().length() > 1)
                recE.setcTipoMovto("N");
            psInsertEncabezado.setString(11, recE.getcTipoMovto());
            if ("N/A".equals(recE.getnOrigenPPTO()))
                recE.setnOrigenPPTO("1");
            psInsertEncabezado.setString(12, recE.getnOrigenPPTO());
            psInsertEncabezado.setInt(13, recE.getnMes());
            psInsertEncabezado.setString(14, folioCompleto);
            psInsertEncabezado.setString(15, recE.getCtr_int());
            psInsertEncabezado.setString(16, recE.getcTipoRectificacion());
            psInsertEncabezado.setString(17, recE.getnCLC());
            psInsertEncabezado.setString(18, recE.getnFolioSicop());
            psInsertEncabezado.setString(19, recE.getnFolioSAI());
            psInsertEncabezado.setString(20, recE.getnFolioSIAFF());
            psInsertEncabezado.setString(21, recE.getCaNoContrarrecibo().trim());
            psInsertEncabezado.setString(22, recE.getCaNoContrarreciboMil().trim());
            psInsertEncabezado.setString(23, recE.getnFolioPoliza());
            psInsertEncabezado.setString(24, recE.getcTipoPoliza());
            psInsertEncabezado.setString(25, recE.getcDescripcionPoliza());
            psInsertEncabezado.setString(26, recE.getnFolioPolizaCancelacion());
            psInsertEncabezado.setString(27, recE.getfCancelacion());
            psInsertEncabezado.setString(28, recE.getnFolioTramiteSicop());
            psInsertEncabezado.setString(29, recE.getcUnidadResponsableContable());
            psInsertEncabezado.setString(30, recE.getTotalDice());
            psInsertEncabezado.setString(31, recE.getTotalDebe());
            log.debug("Object: {}", "[" + folio + "]");
            log.debug("Object: {}", "[" + recE.getnIdCaso() + "]");
            log.debug("Object: {}", "[" + "N" + "]");
            log.debug("Object: {}", "[" + recE.getaEjercicioFiscal() + "]");
            log.debug("Object: {}", "[" + recE.getcRamo() + "]");
            log.debug("Object: {}", "[" + recE.getcUnidadResponsable() + "]");
            log.debug("Object: {}", "[" + recE.getcCentroContable() + "]");
            log.debug("Object: {}", "[" + recE.getfExp() + "]");
            log.debug("Object: {}", "[" + recE.getfAplicacion() + "]");
            log.debug("Object: {}", "[" + recE.getcConceptoRectificacion() + "]");
            log.debug("Object: {}", "[" + recE.getcTipoMovto() + "]");
            log.debug("Object: {}", "[" + recE.getnOrigenPPTO() + "]");
            log.debug("Object: {}", "[" + recE.getnMes() + "]");
            log.debug("Object: {}", "[" + folioCompleto + "]");
            log.debug("Object: {}", "[" + recE.getCtr_int() + "]");
            log.debug("Object: {}", "[" + recE.getcTipoRectificacion() + "]");
            log.debug("Object: {}", "[" + recE.getnCLC() + "]");
            log.debug("Object: {}", "[" + recE.getnFolioSicop() + "]");
            log.debug("Object: {}", "[" + recE.getnFolioSAI() + "]");
            log.debug("Object: {}", "[" + recE.getnFolioSIAFF() + "]");
            log.debug("Object: {}", "[" + recE.getCaNoContrarrecibo().trim() + "]");
            log.debug("Object: {}", "[" + recE.getCaNoContrarreciboMil().trim() + "]");
            log.debug("Object: {}", "[" + recE.getnFolioPoliza() + "]");
            log.debug("Object: {}", "[" + recE.getcTipoPoliza() + "]");
            log.debug("Object: {}", "[" + recE.getcDescripcionPoliza() + "]");
            log.debug("Object: {}", "[" + recE.getnFolioPolizaCancelacion() + "]");
            log.debug("Object: {}", "[" + recE.getfCancelacion() + "]");
            log.debug("Object: {}", "[" + recE.getnFolioTramiteSicop() + "]");
            log.debug("Object: {}", "[" + recE.getcUnidadResponsableContable() + "]");
            log.debug("Object: {}", "[" + recE.getTotalDice() + "]");
            log.debug("Object: {}", "[" + recE.getTotalDebe() + "]");
            // psInsertEncabezado.setString(32, recE.getTipoConcepto()); //se
            // quitó del encabezado
            psInsertEncabezado.execute();
            psInsertDetalle = conn.prepareStatement("INSERT INTO tRectificacionDetalleMil(nFolioRectificacionMil,nDocRenglon,cEvento,EP,mImporte,mImporteNegativo,cMes,cCentroContable,tipoConcepto,tipoMovimiento)VALUES(?,?,?,?,?,?,?,?,?,?)");
            for (Iterator<RectificacionDetalle> i = recDetalles.iterator(); i.hasNext(); ) {
                RectificacionDetalle rd = i.next();
                psInsertDetalle.setInt(1, recE.getnFolioRectificacion());
                psInsertDetalle.setInt(2, rd.getRenglon());
                psInsertDetalle.setString(3, rd.getEvento());
                psInsertDetalle.setString(4, rd.getEp());
                psInsertDetalle.setDouble(5, rd.getImporte());
                psInsertDetalle.setDouble(6, rd.getImporte() * -1);
                psInsertDetalle.setInt(7, rd.getMes());
                psInsertDetalle.setString(8, recE.getcCentroContable());
                psInsertDetalle.setString(9, rd.getConcepto());
                psInsertDetalle.setString(10, rd.getMovimiento());
                psInsertDetalle.addBatch();
            }
            psInsertDetalle.executeBatch();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas con rollback: " + e2, e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(psInsertDetalle, false);
        }
    }

    public static boolean borraRecitificacionMil(Connection conn, int folioRectificacion) throws SQLException {
        PreparedStatement pstmnt = null;
        boolean retval = false;
        try {
            String queryEnc = "DELETE FROM tRectificacionEncabezadoMil WHERE nFolioRectificacionMil = ?";
            String queryDet = "DELETE FROM tRectificacionDetalleMil WHERE nFolioRectificacionMil = ?";
            pstmnt = conn.prepareStatement(queryDet);
            pstmnt.setInt(1, folioRectificacion);
            pstmnt.execute();
            pstmnt = conn.prepareStatement(queryEnc);
            pstmnt.setInt(1, folioRectificacion);
            pstmnt.execute();
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static void actualizaFechaAplicacion(Connection conn, int folio) throws SQLException, ParseException {
        PreparedStatement pstmntUp = null;
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String today = sdf.format(c1.getTime());
        try {
            pstmntUp = conn.prepareStatement("UPDATE tRectificacionEncabezadoMil set fAplicacion=? where nFolioRectificacionMil=?");
            pstmntUp.setString(1, today);
            pstmntUp.setInt(2, folio);
            pstmntUp.execute();
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            if (pstmntUp != null)
                pstmntUp.close();
            pstmntUp = null;
        }
    }

    public static RectificacionEncabezado getRectificacionEncabezadoSicopMil(Connection conn, int folio) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        RectificacionEncabezado re = null;
        pstm = conn.prepareStatement("select 'H' as posicion1, r.fAplicacion, r.fExp, r.cRamo, r.cRamo, r.cRamo, r.cUnidadResponsable, r.cUnidadResponsable, r.cUnidadResponsable, isnull(r.cTipoMovto,'') as cTipoMovto, " + " isnull(r.nOrigenPPTO,'') as nOrigenPPTO, isnull(r.cConceptoRectificacion,'') as cConceptoRectificacion, isnull(R.oficioRectif,'') as ctr_int, R.oficioRectif, R.oficioRectif, R.nFolioRectificacionMil, R.nIDCaso, R.totalDICE,R.totalDEBE " + " from tRectificacionEncabezadoMil r with (nolock) WHERE nFolioRectificacionMil=?");
        pstm.setInt(1, folio);
        res = pstm.executeQuery();
        if (res.next()) {
            re = new RectificacionEncabezado();
            re.setPosicion1(res.getString("posicion1"));
            re.setcRamo(res.getString("cRamo"));
            re.setcUnidadResponsable(res.getString("cUnidadResponsable"));
            re.setnOrigenPPTO(res.getString("nOrigenPPTO"));
            re.setcConceptoRectificacion(res.getString("cConceptoRectificacion"));
            re.setnFolioRectificacion(Integer.parseInt(res.getString("nFolioRectificacionMil")));
            re.setnIdCaso(Integer.parseInt(res.getString("nIDCaso")));
            re.setfExp(res.getString("fExp"));
            re.setfAplicacion(res.getString("fAplicacion"));
            re.setcTipoMovto(res.getString("cTipoMovto"));
            re.setOficioRectif(res.getString("oficioRectif"));
            re.setCtr_int(res.getString("ctr_int"));
            re.setTotalDice(res.getString("totalDice"));
            re.setTotalDebe(res.getString("totalDebe"));
        }
        if (pstm != null) {
            pstm.close();
        }
        if (res != null) {
            res.close();
        }
        return re;
    }

    public static StringBuilder getRectificacionDetalleSicopMil(Connection conn, int folio) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        StringBuilder string = new StringBuilder();
        pstm = conn.prepareStatement("select distinct isnull(r.nOrigenPPTO,0) as nOrigenPPTO,'0',r.nFolioSICOP, d.nDocRenglon, e.cRamoEP, e.cUnidadResponsableEP, e.aEjercicioFiscal, e.cGrupoFuncional, e.cFuncion, " + " e.cSubFuncion, e.cProgramaGeneral, e.cActividadInstitucional, e.cProgramaPresupuestario, substring(e.cPartida,1,1) as cCapitulo," + " substring(e.cPartida,2,1) as cConcepto, substring(e.cPartida,3,1) as cPartida, substring(e.cPartida,4,2) as cPartidaEspecifica, " + " e.cTipoGasto, e.cFuenteFinanciamiento, e.cEntidadFederativa, e.cCartera, '0000000000' as cUnidadEjecutora," + " '00' as cUnidadNormativa, '000','000','00000','00000','0000000000', CONVERT(VARCHAR,d.mImporte), d.cMes, '' as cFillRellen1 , b.sSicop, '' as cFillRellen2 , '0' as sol_oli, dp.ID_TIPO_CONCEPTO, '000' as tipo_concepto," + " '0' as concepto_mov," + " '0' retencion_isr, '0', CASE WHEN d.cEvento LIKE '%DICE%' THEN 'A' ELSE 'C' END, isnull(R.oficioRectif,'') as ctr_int from tRectificacionDetalleMil d with (nolock), tRectificacionEncabezadoMil r with (nolock), tCatalogoEP e with (nolock), tPagadoEncabezado P with (nolock), tPagadoDetalle dp  with (nolock), tBeneficiarioCapituloMil b with (nolock)" + " WHERE d.nFolioRectificacionMil =  r.nFolioRectificacionMil and d.EP =  e.EP and r.caNoContrarrecibo = P.caNoContrarrecibo and p.nFolioPagado = dp.nFolioPagado and dp.RFC = b.sCodigoEntidad AND r.nFolioRectificacionMil=? AND d.nFolioRectificacionMil=?");
        try {
            pstm.setInt(1, folio);
            pstm.setInt(2, folio);
            res = pstm.executeQuery();
            int i = 1;
            while (res.next()) {
                string.append("\r\n" + res.getString(1));
                string.append("," + res.getString(2));
                string.append("," + res.getString(3));
                //(4);
                string.append("," + i++);
                string.append("," + res.getString(5));
                string.append("," + res.getString(6));
                string.append("," + res.getString(7));
                string.append("," + res.getString(8));
                string.append("," + res.getString(9));
                string.append("," + res.getString(10));
                string.append("," + res.getString(11));
                string.append("," + res.getString(12));
                string.append("," + res.getString(13));
                string.append("," + res.getString(14));
                string.append("," + res.getString(15));
                string.append("," + res.getString(16));
                string.append("," + res.getString(17));
                string.append("," + res.getString(18));
                string.append("," + res.getString(19));
                string.append("," + res.getString(20));
                string.append("," + res.getString(21));
                string.append("," + res.getString(22));
                string.append("," + res.getString(23));
                string.append("," + res.getString(24));
                string.append("," + res.getString(25));
                string.append("," + res.getString(26));
                string.append("," + res.getString(27));
                string.append("," + res.getString(28));
                string.append("," + res.getString(29));
                string.append("," + res.getString(30));
                string.append("," + res.getString(31));
                string.append("," + res.getString(32));
                string.append("," + res.getString(33));
                string.append("," + res.getString(34));
                string.append("," + res.getString(35));
                string.append("," + res.getString(36));
                string.append("," + res.getString(37));
                string.append("," + res.getString(38));
                string.append("," + res.getString(39));
                string.append("," + res.getString(40));
                string.append("," + res.getString(41));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            CloseObject.closeObject(res);
            CloseObject.closeObject(pstm);
        }
        return string;
    }

    public static String validaEvento(Connection conn, Caso c, String tipo) throws Exception {
        PreparedStatement ps = null;
        int nIdCaso;
        ResultSet rs = null;
        String mensaje = "";
        try {
            nIdCaso = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
            ps = conn.prepareStatement("EXEC dbo.sp_valida_evento_rectificacion @folio = ?, @tipo = ?");
            ps.setInt(1, nIdCaso);
            ps.setString(2, tipo);
            rs = ps.executeQuery();
            while (rs.next()) {
                mensaje += rs.getString(1);
            }
            return mensaje;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static String tipoRectificacion(Connection conn, Caso c, String tipo) throws Exception {
        PreparedStatement ps = null;
        int nIdCaso;
        ResultSet rs = null;
        String tipoAplicacion = "";
        try {
            nIdCaso = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
            ps = conn.prepareStatement("EXEC dbo.sp_valida_es_Rectificacion_Reclasificacion @folio = ?, @tipo = ?");
            ps.setInt(1, nIdCaso);
            ps.setString(2, tipo);
            rs = ps.executeQuery();
            while (rs.next()) {
                tipoAplicacion += rs.getString(1);
            }
            return tipoAplicacion;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static String validaMes(Connection conn, Caso c, String tipo, String fApl) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String queryCC = "SELECT TOP 1 cCentroContable FROM tRectificacionEncabezado WITH( NOLOCK ) WHERE nFolioRectificacion = ?";
        String queryUR = "SELECT TOP 1 cUnidadResponsable FROM tRectificacionEncabezado WITH( NOLOCK ) WHERE nFolioRectificacion = ?";
        int nIdCaso;
        String mensaje = "";
        int mes = Integer.parseInt(fApl.substring(3, 5));
        String efActivo = "";
        try {
            String[] componentesFApl = fApl.split("/");
            String ejercicioFApl = componentesFApl[2];
            efActivo = EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal();
            if (ejercicioFApl.equals(ejercicioFApl)) {
                Calendar hoy = new GregorianCalendar();
                int mesActual = hoy.get(Calendar.MONTH) + 1;
                if (mesActual == mes)
                    mensaje = "S";
                else {
                    nIdCaso = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
                    ps = conn.prepareStatement("SELECT mesAbierto FROM dbo.tMesesContables WITH (NOLOCK) WHERE cCentroContable = (" + queryCC + ") AND nMes = ? AND cUnidadResponsable = (" + queryUR + ")");
                    ps.setInt(1, nIdCaso);
                    ps.setInt(2, mes);
                    ps.setInt(3, nIdCaso);
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        mensaje += rs.getString(1);
                    }
                }
            } else {
                String fApp = "31/12/" + efActivo;
                if (fApp.equals(fApl))
                    mensaje = "S";
                else
                    throw new Exception("El ejercicio fiscal es diferente al año en curso por lo que la fecha esperada es: " + fApp);
            }
            return mensaje;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static BigDecimal getRemanenteBig(Connection conn, String ep, String cxp, int nRenglon) throws SQLException {
        BigDecimal remanente = new BigDecimal(0);
        ResultSet rs = null;
        PreparedStatement pstm = null;
        //1
        String //1
        queryRemanente = //2
        "" + "SELECT impRect - impReint AS  Remanente\r\n" + "FROM (\r\n" + "  SELECT ISNULL((SELECT CASE WHEN cEvento LIKE 'DICE%' THEN PAGDET.mImporte - RECDET.mImporte END AS IMPORTE FROM tRectificacionAutDetalle AS RECDET WITH (NOLOCK) WHERE RECENC.nFolioRectificacionAut = RECDET.nFolioRectificacionAut AND cEvento LIKE 'DICE%' AND PAGDET.EP = RECDET.EP AND PAGDET.cMes = RECDET.cMes),PAGDET.mImporte) AS impRect\r\n" + "	, ISNULL((SELECT CASE WHEN cEvento LIKE 'DICE%' THEN RECDET.EP END AS IMPORTE FROM tRectificacionAutDetalle AS RECDET WITH (NOLOCK) WHERE RECENC.nFolioRectificacionAut = RECDET.nFolioRectificacionAut AND cEvento LIKE 'DICE%' AND PAGDET.EP = RECDET.EP AND PAGDET.cMes = RECDET.cMes), PAGDET.EP) AS EPPAG\r\n" + "	, ISNULL((SELECT CASE WHEN cEvento LIKE 'DICE%' THEN RECDET.nDocRenglon END AS ndogrenglon FROM tRectificacionAutDetalle AS RECDET WITH (NOLOCK) WHERE RECENC.nFolioRectificacionAut = RECDET.nFolioRectificacionAut AND cEvento LIKE 'DICE%' AND PAGDET.EP = RECDET.EP AND PAGDET.cMes = RECDET.cMes), PAGDET.nDocRenglon) AS ndogrenglonP\r\n" + "	, ISNULL(REINTDET.mImporte, 0) AS impReint\r\n" + "  FROM tPagadoEncabezado AS PAGENC WITH (NOLOCK)\r\n" + "  JOIN tPagadoDetalle AS PAGDET WITH (NOLOCK) ON PAGENC.nFolioPagado = PAGDET.nFolioPagado\r\n" + "  LEFT JOIN tRectificacionAutEncabezado AS RECENC WITH (NOLOCK) ON PAGENC.caNoContrarrecibo = RECENC.caNoContrarrecibo	\r\n" + "  LEFT JOIN tReintegroAutDetalle AS REINTDET WITH (NOLOCK) ON PAGENC.caNoContrarrecibo = CXP\r\n" + "  WHERE PAGENC.caNoContrarrecibo = ?	\r\n" + "	AND (PAGDET.mPasivoDiferido = 0 OR PAGDET.mPasivoDiferido IS NULL)\r\n" + "\r\n" + "	UNION\r\n" + "\r\n" + "  SELECT \r\n" + "	ISNULL((CASE WHEN RECDET.cEvento LIKE 'DEBE%' THEN RECDET.mImporte END),0) AS impRect\r\n" + "	, ISNULL((CASE WHEN RECDET.cEvento LIKE 'DEBE%' THEN RECDET.EP END), '') AS EPPAG\r\n" + "	, ISNULL((CASE WHEN RECDET.cEvento LIKE 'DEBE%' THEN RECDET.nDocRenglon END), 0) AS ndogrenglonP	\r\n" + "	, ISNULL(REINTDET.mImporte, 0) AS impReint\r\n" + "  FROM tPagadoEncabezado AS PAGENC WITH (NOLOCK)\r\n" + "  JOIN tPagadoDetalle AS PAGDET WITH (NOLOCK) ON PAGENC.nFolioPagado = PAGDET.nFolioPagado\r\n" + "  LEFT JOIN tRectificacionAutEncabezado AS RECENC WITH (NOLOCK) ON PAGENC.caNoContrarrecibo = RECENC.caNoContrarrecibo	\r\n" + "  LEFT JOIN tRectificacionAutDetalle AS RECDET WITH (NOLOCK) ON RECENC.nFolioRectificacionAut = RECDET.nFolioRectificacionAut and RECDET.cEvento LIKE 'DEBE%'	\r\n" + "  LEFT JOIN tReintegroAutDetalle AS REINTDET WITH (NOLOCK) ON PAGENC.caNoContrarrecibo = CXP\r\n" + "  WHERE PAGENC.caNoContrarrecibo = ?	\r\n" + //3
        "	AND (PAGDET.mPasivoDiferido = 0 OR PAGDET.mPasivoDiferido IS NULL)\r\n" + //4
        ") tbl\r\n" + "WHERE EPPAG = ?\r\n" + "	AND ndogrenglonP = ?";
        //                                                                1  2  3  4
        log.trace("Object: {}", String.format("Ejecutando query para remanente. [%s][%s,%s,%s,%d]", queryRemanente, cxp, cxp, ep, nRenglon));
        pstm = conn.prepareStatement(queryRemanente);
        pstm.setString(1, cxp);
        pstm.setString(2, cxp);
        pstm.setString(3, ep);
        pstm.setInt(4, nRenglon);
        rs = pstm.executeQuery();
        if (rs.next()) {
            remanente = rs.getBigDecimal(1).setScale(2, RoundingMode.HALF_UP);
        }
        if (pstm != null) {
            pstm.close();
        }
        if (rs != null) {
            rs.close();
        }
        return remanente;
    }
}
