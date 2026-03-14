package com.syc.contable.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import com.syc.gestion.core.Caso;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetencionManager {

    /*
	 * TODO VGC Esto no funciona por ser estatico. En un ambiente paralelo se
	 * sobreescribiria para todos los hilos. Cambiar
	 */
    static String ur;

    private static final Logger log = LoggerFactory.getLogger(RetencionManager.class);

    public static void setUR(String UR) {
        ur = UR;
    }

    public static String getUR() {
        return ur;
    }

    public static Retencion filtraFolioSAI(Connection conn, String folioSAI) throws Exception {
        Retencion retencion = null;
        StringBuilder SqlH = new StringBuilder();
        SqlH.append(" SELECT ISNULL(RIGHT('000000' + NCOM_15,6), '') nCompromisoSICOP, pagado.cTipoPago tipoPago, REPLACE(REPLACE(pagado.cDescripcionPoliza, CHAR(10), ''), CHAR(13),'') cConcepto, RFC_184 cIdRFC, YEAR(pagado.faplicacion) aEjercicioFiscal ");
        SqlH.append(" , CASE WHEN cTipoPago = 'RELACIONGASTOS' THEN (SELECT cNombre FROM tRELACIONGASTOSEncabezado (NOLOCK) WHERE caNoContrarrecibo = pagado.caNoContrarrecibo)  ");
        SqlH.append("		WHEN cTipoPago = 'PAGODIRECTO' THEN (SELECT (select dNombre + ' ' + ISNULL(dapellidoPaterno,'') + ' ' + ISNULL(dApellidoMaterno,'') from tBeneficiario(nolock) where dRFC =TPAGODIRECTOENCABEZADO.cIdRfc)  FROM TPAGODIRECTOENCABEZADO (NOLOCK) WHERE caNoContrarrecibo = pagado.caNoContrarrecibo) ");
        SqlH.append("		WHEN cTipoPago = 'PAGODIVERSO' THEN (SELECT Nombre FROM tpagodiversoencabezado (NOLOCK) WHERE caNoContrarrecibo = pagado.caNoContrarrecibo)  ");
        SqlH.append("		WHEN cTipoPago = 'FEDERALIZADO' THEN (SELECT Nombre FROM tpagofederalizadoEncabezado (NOLOCK) WHERE caNoContrarrecibo = pagado.caNoContrarrecibo)  END NOMBRE ");
        SqlH.append(" , CASE WHEN cTipoPago = 'RELACIONGASTOS' THEN (SELECT (SELECT TOP 1  sCuenta_bancaria FROM tLayoutsCreadosRelacionGastosHeader (nolock) where sNocontrarrecibo = tRELACIONGASTOSEncabezado.canocontrarrecibo) FROM tRELACIONGASTOSEncabezado (NOLOCK) WHERE caNoContrarrecibo = pagado.caNoContrarrecibo)  ");
        SqlH.append("		WHEN cTipoPago = 'PAGODIRECTO' THEN (SELECT CTAB  FROM TPAGODIRECTOENCABEZADO (NOLOCK) WHERE caNoContrarrecibo = pagado.caNoContrarrecibo)  ");
        SqlH.append("		WHEN cTipoPago = 'PAGODIVERSO' THEN (SELECT CTAB FROM tpagodiversoencabezado (NOLOCK) WHERE caNoContrarrecibo = pagado.caNoContrarrecibo)  ");
        SqlH.append("		WHEN cTipoPago = 'FEDERALIZADO' THEN (SELECT CTAB FROM tpagofederalizadoEncabezado (NOLOCK) WHERE caNoContrarrecibo = pagado.caNoContrarrecibo)  END CTAB ");
        SqlH.append(", CASE WHEN cTipoPago = 'RELACIONGASTOS' THEN (SELECT nFolioRELACIONGASTOS FROM tRELACIONGASTOSEncabezado (NOLOCK) WHERE caNoContrarrecibo = pagado.caNoContrarrecibo)  ");
        SqlH.append("		WHEN cTipoPago = 'PAGODIRECTO' THEN (SELECT nFolioPagoDirecto FROM TPAGODIRECTOENCABEZADO (NOLOCK) WHERE caNoContrarrecibo = pagado.caNoContrarrecibo) ");
        SqlH.append("		WHEN cTipoPago = 'PAGODIVERSO' THEN (SELECT nFolioPAGODIVERSO FROM tpagodiversoencabezado (NOLOCK) WHERE caNoContrarrecibo = pagado.caNoContrarrecibo)  ");
        SqlH.append("		WHEN cTipoPago = 'FEDERALIZADO' THEN (SELECT nFolioPAGOFEDERALIZADO FROM tpagofederalizadoEncabezado (NOLOCK) WHERE caNoContrarrecibo = pagado.caNoContrarrecibo)  END nFolioPago, ");
        SqlH.append("  caNoContrarrecibo , (select TOP 1 cPasivo from tRELACIONGASTOSDetalle where nFolioRELACIONGASTOS = nFolioPAGO and pagado.cTipoPago = 'RELACIONGASTOS') cPasivo");
        SqlH.append(" FROM tPagadoEncabezado pagado with (nolock)  ");
        SqlH.append(" INNER JOIN CLC_SICOP sicop with (nolock) ");
        SqlH.append(" 	ON sicop.FOLIO_SIAFF_112 = nFolioSIAFF ");
        SqlH.append(" WHERE cDocumentoHaplicado = 'S' ");
        SqlH.append(" 	AND caNoContrarrecibo = ? ");
        StringBuilder SqlD = new StringBuilder();
        SqlD.append("SELECT ROW_NUMBER () over (order by nMes)  nDocRenglon, EP, RFC, nMes, mSaldoArrastre ");
        SqlD.append("	FROM ( ");
        SqlD.append("	SELECT DISTINCT EP, rfc , CAST( SUBSTRING(ncuenta,10,2) as int) nMes, mSaldoArrastre ");
        SqlD.append("	FROM tPagadoDetalle det with (nolock)  inner join tSaldos saldos with (nolock)  on det.EP = saldos.cSubCuenta and nCuenta like '82106%'");
        SqlD.append("   WHERE nFolioPagado = (SELECT nFolioPagado FROM tPagadoEncabezado (nolock) WHERE caNoContrarrecibo = ? )   ");
        SqlD.append("					and  mSaldoArrastre > 0 and CAST( SUBSTRING(ncuenta,10,2) as int) <= MONTH(getdate())  and det.mImporteNeto > 0");
        SqlD.append(" ) as Pagado ");
        SqlD.append(" order by nMes ");
        retencion = queryPagado(conn, folioSAI, SqlH.toString(), SqlD.toString(), "");
        return retencion;
    }

    public static Retencion filtraFolioIP(Connection conn, String folioSAI) throws Exception {
        Retencion retencion = null;
        String SqlH = "SELECT '' nCompromisoSICOP, pagado.cTipoPago tipoPago, pagado.cDescripcionPoliza cConcepto, YEAR(pagado.faplicacion) aEjercicioFiscal " + ", CASE WHEN pagado.cTipoPago = 'RELACIONGASTOS' THEN (SELECT cIdrfc FROM tRELACIONGASTOSEncabezado (NOLOCK) WHERE caNoContrarrecibo = pagado.caNoContrarrecibo)  " + "		WHEN pagado.cTipoPago = 'PAGODIRECTO' THEN (SELECT cIdrfc  FROM TPAGODIRECTOENCABEZADO (NOLOCK) WHERE caNoContrarrecibo = pagado.caNoContrarrecibo)  " + "		WHEN pagado.cTipoPago = 'PAGODIVERSO' THEN (SELECT RFC FROM tpagodiversoencabezado (NOLOCK) WHERE caNoContrarrecibo = pagado.caNoContrarrecibo)  " + "		WHEN pagado.cTipoPago = 'FEDERALIZADO' THEN (SELECT rfc FROM tpagofederalizadoEncabezado (NOLOCK) WHERE caNoContrarrecibo = pagado.caNoContrarrecibo)  END cIdRFC " + ", CASE WHEN pagado.cTipoPago = 'RELACIONGASTOS' THEN (SELECT cNombre FROM tRELACIONGASTOSEncabezado (NOLOCK) WHERE caNoContrarrecibo = pagado.caNoContrarrecibo)  " + "		WHEN pagado.cTipoPago = 'PAGODIRECTO' THEN (SELECT (select dNombre + ' ' + ISNULL(dapellidoPaterno,'') + ' ' + ISNULL(dApellidoMaterno,'') from tBeneficiario(nolock) where dRFC =TPAGODIRECTOENCABEZADO.cIdRfc)  FROM TPAGODIRECTOENCABEZADO (NOLOCK) WHERE caNoContrarrecibo = pagado.caNoContrarrecibo)  " + "		WHEN pagado.cTipoPago = 'PAGODIVERSO' THEN (SELECT Nombre FROM tpagodiversoencabezado (NOLOCK) WHERE caNoContrarrecibo = pagado.caNoContrarrecibo)  " + "		WHEN pagado.cTipoPago = 'FEDERALIZADO' THEN (SELECT Nombre FROM tpagofederalizadoEncabezado (NOLOCK) WHERE caNoContrarrecibo = pagado.caNoContrarrecibo) END NOMBRE " + ", CASE WHEN pagado.cTipoPago = 'RELACIONGASTOS' THEN (SELECT (SELECT TOP 1 sCuenta_bancaria FROM tLayoutsCreadosRelacionGastosHeader (nolock) where sNocontrarrecibo = tRELACIONGASTOSEncabezado.canocontrarrecibo) FROM tRELACIONGASTOSEncabezado (NOLOCK) WHERE caNoContrarrecibo = pagado.caNoContrarrecibo)  " + "		WHEN pagado.cTipoPago = 'PAGODIRECTO' THEN (SELECT CTAB  FROM TPAGODIRECTOENCABEZADO (NOLOCK) WHERE caNoContrarrecibo = pagado.caNoContrarrecibo)  " + "		WHEN pagado.cTipoPago = 'PAGODIVERSO' THEN (SELECT CTAB FROM tpagodiversoencabezado (NOLOCK) WHERE caNoContrarrecibo = pagado.caNoContrarrecibo)  " + "		WHEN pagado.cTipoPago = 'FEDERALIZADO' THEN (SELECT CTAB FROM tpagofederalizadoEncabezado (NOLOCK) WHERE caNoContrarrecibo = pagado.caNoContrarrecibo)  END CTAB, caNoContrarrecibo, nFolioPago " + " FROM tPagadoEncabezado pagado (nolock) " + " INNER JOIN tPagadoDetalle det (nolock)  " + "    ON pagado.cTipoPago = det.ctipopago and pagado.nFolioPAGO = det.nfoliopago " + " WHERE pagado.cDocumentoHaplicado = 'S' AND PAGADO.cTipoPago <> 'AJENAS' " + " 	AND SUBSTRING(det.ep,40,1) = 4 " + " 	AND caNoContrarrecibo = ?";
        StringBuilder SqlD = new StringBuilder();
        SqlD.append("SELECT ROW_NUMBER () over (order by nMes)  nDocRenglon, EP, RFC, nMes, mSaldoArrastre ");
        SqlD.append("	FROM ( ");
        SqlD.append("	SELECT DISTINCT EP, rfc , CAST( SUBSTRING(ncuenta,10,2) as int) nMes, mSaldoArrastre ");
        SqlD.append("	FROM tPagadoDetalle det with (nolock)  inner join tSaldos saldos with (nolock)  on det.EP = saldos.cSubCuenta and nCuenta like '82106%'");
        SqlD.append("   WHERE nFolioPagado = (SELECT nFolioPagado FROM tPagadoEncabezado (nolock) WHERE caNoContrarrecibo = ? )   ");
        SqlD.append("					and  mSaldoArrastre > 0 and CAST( SUBSTRING(ncuenta,10,2) as int) <= MONTH(getdate())  and det.mImporteNeto > 0");
        SqlD.append(" ) as Pagado ");
        SqlD.append(" order by nMes ");
        retencion = queryPagado(conn, folioSAI, SqlH, SqlD.toString(), "");
        return retencion;
    }

    public static void insertarEncabezado(Connection conn, RetencionEncabezado encabezado) throws Exception {
        PreparedStatement pstmnt = null;
        String insertQuery = "INSERT INTO tRetencionEncabezado (nFolioRetencion, nIdCaso, cUnidadResponsable, fCarga, fAplicacion, cRamo, nOrigenPPTO, aEjercicioFiscal, cCentroContable " + ", cIdRFC, CTAB, NOMBRE, cConcepto, mImporteRetencion, cTipoPago, caNoContrarrecibo, cIdUsuarioCaptura, nEnviadoSICOP, nCompromisoSICOP, cUnidadResponsableContable, nFolioPoliza, cTipoPoliza, cEsIP, nFolioPago, cDescripcionPoliza) " + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            pstmnt = conn.prepareStatement(insertQuery);
            // introducir parametros para condiciones
            pstmnt.setInt(1, encabezado.getnFolioRetencion());
            pstmnt.setInt(2, encabezado.getnIdCaso());
            pstmnt.setString(3, encabezado.getcUnidadResponsable());
            pstmnt.setDate(4, encabezado.getfCarga());
            pstmnt.setDate(5, encabezado.getfAplicacion());
            pstmnt.setString(6, encabezado.getcRamo());
            pstmnt.setInt(7, encabezado.getnOrigenPPTO());
            pstmnt.setString(8, encabezado.getaEjercicioFiscal());
            pstmnt.setString(9, encabezado.getcCentroContable());
            pstmnt.setString(10, encabezado.getcIdRFC());
            pstmnt.setString(11, encabezado.getCTAB());
            pstmnt.setString(12, encabezado.getNOMBRE().trim());
            pstmnt.setString(13, encabezado.getcConcepto());
            pstmnt.setDouble(14, encabezado.getmImporteRetencion());
            pstmnt.setString(15, encabezado.getTipoPago());
            pstmnt.setString(16, encabezado.getCaNoContrarrecibo().trim());
            pstmnt.setString(17, encabezado.getcIdUsuarioCaptura());
            pstmnt.setInt(18, encabezado.getnEnviadoSICOP());
            pstmnt.setString(19, encabezado.getnCompromisoSICOP());
            pstmnt.setString(20, encabezado.getcUnidadResponsableContable());
            pstmnt.setInt(21, encabezado.getnFolioPoliza());
            pstmnt.setString(22, "EG");
            pstmnt.setString(23, encabezado.getEsIP());
            pstmnt.setInt(24, encabezado.getnFolioPago());
            pstmnt.setString(25, encabezado.getcConcepto());
            log.debug("Object: {}", insertQuery);
            pstmnt.execute();
        } catch (Exception e) {
            throw e;
        }
    }

    public static void insertarDetalle(Connection conn, List<RetencionDetalle> retenciones) throws Exception {
        PreparedStatement pstmnt = null;
        try {
            for (int i = 0; i < retenciones.size(); i++) {
                RetencionDetalle rd = new RetencionDetalle();
                rd = retenciones.get(i);
                String insertQuery = "INSERT INTO tRetencionDetalle ( nFolioRetencion, nDocRenglon, cMes, cEvento, ep, mImporte, m2Millar, mObra5, mImporteFlete4, mISRHonorarios, mISRArrenda, mRetImpuestoCedular, mImporteIvaArrenda, mImporteIvaHonorarios, mImporteISRLaudos, mISROtros, mImporteIva6" + "	,RFC, OBGT, cUnidadResponsable, cPasivo, mPasivoDiferido, cEjercicio, cCentroContable	) " + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ,?,?,?,?,?, ?,?)";
                pstmnt = conn.prepareStatement(insertQuery);
                pstmnt.setInt(1, rd.getnFolioRetencion());
                pstmnt.setInt(2, rd.getnDocRenglon());
                pstmnt.setString(3, rd.getcMes());
                pstmnt.setString(4, rd.getcEvento());
                pstmnt.setString(5, rd.getEP());
                pstmnt.setDouble(6, rd.getmImporte());
                pstmnt.setDouble(7, rd.getM2Millar());
                pstmnt.setDouble(8, rd.getmObra5());
                pstmnt.setDouble(9, rd.getmImporteFlete4());
                pstmnt.setDouble(10, rd.getmISRHonorarios());
                pstmnt.setDouble(11, rd.getmISRArrenda());
                pstmnt.setDouble(12, rd.getmRetImpuestoCedular());
                pstmnt.setDouble(13, rd.getmImporteIvaArrenda());
                pstmnt.setDouble(14, rd.getmImporteIvaHonorarios());
                pstmnt.setDouble(15, rd.getmImporteISRLaudos());
                pstmnt.setDouble(16, rd.getmISROtros());
                pstmnt.setDouble(17, rd.getmImporteIva6());
                pstmnt.setString(18, rd.getRfc());
                pstmnt.setString(19, rd.getObgt());
                pstmnt.setString(20, rd.getcUnidadResponsable());
                pstmnt.setString(21, rd.getcPasivo());
                pstmnt.setDouble(22, rd.getmPasivoDiferido());
                pstmnt.setString(23, rd.getcEjercicio());
                pstmnt.setDouble(24, rd.getcCentroContable());
                pstmnt.execute();
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public static Retencion queryPagado(Connection conn, String folio, String SqlH, String SqlD, String CXP) throws URInaccesibleException, CLCNoPagadaException, Exception {
        PreparedStatement pstmntH = null;
        ResultSet rsH = null;
        PreparedStatement pstmntD = null;
        ResultSet rsD = null;
        Retencion retencion = null;
        // Realizar conexion
        log.debug("Object: {}", "[SQLH]" + SqlH + "[CxP=" + folio + "]");
        log.debug("Object: {}", "[SQLD]" + SqlD + "[CxP=" + folio + "]");
        try {
            pstmntH = conn.prepareStatement(SqlH);
            pstmntH.setString(1, folio);
            rsH = pstmntH.executeQuery();
            if (rsH.next()) {
                RetencionEncabezado encabezado = null;
                List<RetencionDetalle> detalle = null;
                retencion = new Retencion();
                encabezado = new RetencionEncabezado();
                encabezado.setTipoPago(rsH.getString("tipoPago"));
                encabezado.setCaNoContrarrecibo(rsH.getString("caNoContrarrecibo"));
                encabezado.setcIdRFC(rsH.getString("cIdRFC"));
                encabezado.setCTAB(rsH.getString("CTAB"));
                encabezado.setNOMBRE(rsH.getString("NOMBRE"));
                encabezado.setcConcepto(rsH.getString("cConcepto"));
                encabezado.setnCompromisoSICOP(rsH.getString("nCompromisoSICOP"));
                encabezado.setcPasivo_C(rsH.getString("cPasivo"));
                encabezado.setnFolioPago(rsH.getInt("nFolioPago"));
                // Si no tiene compromiso entonces lo tome del disponible (1)
                if ("".equals(encabezado.getnCompromisoSICOP())) {
                    encabezado.setnOrigenPPTO(1);
                } else
                    encabezado.setnOrigenPPTO(4);
                detalle = new ArrayList<RetencionDetalle>();
                pstmntD = conn.prepareStatement(SqlD);
                pstmntD.setString(1, folio);
                rsD = pstmntD.executeQuery();
                while (rsD.next()) {
                    RetencionDetalle rDetalle = new RetencionDetalle();
                    rDetalle.setnDocRenglon(rsD.getInt("nDocRenglon"));
                    rDetalle.setcMes(rsD.getString("nMes"));
                    rDetalle.setEP(rsD.getString("EP"));
                    rDetalle.setRfc(rsD.getString("rfc"));
                    rDetalle.setmImporte(rsD.getDouble("mSaldoArrastre"));
                    detalle.add(rDetalle);
                }
                retencion.setEncabezado(encabezado);
                retencion.setDetalle(detalle);
            } else {
                throw new CLCNoPagadaException("La CLC " + folio + " no se encuentra pagada");
            }
            return retencion;
        } catch (Exception s) {
            log.error(s.getMessage(), s);
            throw s;
        } finally {
            CloseObject.closeObject(pstmntH, false);
            CloseObject.closeObject(rsH, false);
            CloseObject.closeObject(pstmntD, false);
            CloseObject.closeObject(rsD, false);
        }
    }

    public static RetencionEncabezado getRetencionEncabezado(Connection conn, int folio) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        RetencionEncabezado re = null;
        try {
            pstm = conn.prepareStatement("SELECT * FROM tRetencionEncabezado WITH(NOLOCK) WHERE nFolioRetencion = ?");
            pstm.setInt(1, folio);
            res = pstm.executeQuery();
            if (res.next()) {
                re = new RetencionEncabezado();
                re.setnFolioRetencion(Integer.parseInt(res.getString("nFolioRetencion")));
                re.setnIdCaso(Integer.parseInt(res.getString("nIDCaso")));
                re.setfCarga(res.getDate("fCarga"));
                re.setfAplicacion(res.getDate("fAplicacion"));
                re.setcRamo(res.getString("cRamo"));
                re.setnOrigenPPTO(res.getInt("nOrigenPPTO"));
                re.setTipoPago(res.getString("cTipoPago"));
                re.setaEjercicioFiscal(res.getString("aEjercicioFiscal"));
                re.setcCentroContable(res.getString("cCentroContable"));
                re.setcIdRFC(res.getString("cIdRFC"));
                re.setCTAB(res.getString("CTAB"));
                re.setNOMBRE(res.getString("Nombre"));
                re.setcConcepto(res.getString("cConcepto"));
                re.setCaNoContrarrecibo(res.getString("caNoContrarrecibo"));
                re.setcIdUsuarioCaptura(res.getString("cIdUsuarioCaptura"));
                re.setnCompromisoSICOP(res.getString("nCompromisoSICOP"));
                re.setmImporteRetencion(res.getDouble("mImporteRetencion"));
                re.setnFolioSICOP(res.getInt("nFolioSolSICOP"));
                re.setnFolioPago(res.getInt("nFolioPago"));
            }
        } finally {
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(res);
        }
        return re;
    }

    public static List<RetencionDetalle> getRetencionDetalle(Connection conn, int folio) throws SQLException {
        ResultSet rs = null;
        PreparedStatement pstm = null;
        List<RetencionDetalle> reteDetalle = new ArrayList<RetencionDetalle>();
        try {
            pstm = conn.prepareStatement("SELECT * FROM tRetencionDetalle WITH(NOLOCK) WHERE nFolioRetencion=?");
            pstm.setInt(1, folio);
            rs = pstm.executeQuery();
            while (rs.next()) {
                RetencionDetalle detalle = new RetencionDetalle();
                detalle.setnDocRenglon(rs.getInt("nDocRenglon"));
                detalle.setcMes(rs.getString("cMes"));
                detalle.setEP(rs.getString("EP"));
                detalle.setmImporte(rs.getDouble("mImporte"));
                detalle.setM2Millar(rs.getDouble("m2Millar"));
                detalle.setmObra5(rs.getInt("mObra5"));
                detalle.setmImporteFlete4(rs.getDouble("mImporteFlete4"));
                detalle.setmISRHonorarios(rs.getDouble("mISRHonorarios"));
                detalle.setmImporteIvaHonorarios(rs.getDouble("mImporteIvaHonorarios"));
                detalle.setmImporteIvaArrenda(rs.getDouble("mImporteIvaArrenda"));
                detalle.setmRetImpuestoCedular(rs.getDouble("mRetImpuestoCedular"));
                detalle.setmImporteISRLaudos(rs.getDouble("mImporteISRLaudos"));
                detalle.setmISROtros(rs.getDouble("mISROtros"));
                detalle.setmImporteIva6(rs.getDouble("mImporteIva6"));
                detalle.setRfc(rs.getString("RFC"));
                reteDetalle.add(detalle);
            }
            return reteDetalle;
        } finally {
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(rs);
        }
    }

    // Solo aplica para laudos
    public static String actualizaEvento(Connection conn, String cxp) throws Exception {
        PreparedStatement ps = null;
        String esLiquidacion = "";
        String cevento = "";
        ResultSet rs = null;
        StringBuilder sql = new StringBuilder();
        sql.append("select cEsLiquidacion from tRELACIONGASTOSEncabezado rg ");
        sql.append(" inner join tComprobacionLaudos laudos ");
        sql.append(" on rg.nFolioRELACIONGASTOS = laudos.nFolioRELACIONGASTOS ");
        sql.append(" where caNoContrarrecibo = ?");
        try {
            String query = sql.toString();
            ps = conn.prepareStatement(query);
            ps.setString(1, cxp);
            rs = ps.executeQuery();
            if (rs.next()) {
                esLiquidacion = rs.getString("cEsLiquidacion");
            }
            if ("S".equals(esLiquidacion)) {
                cevento = "DD_RETE_02";
            } else if ("N".equals(esLiquidacion)) {
                cevento = "DD_RETE_01";
            }
            return cevento;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static String validaMes(Connection conn, Caso c, String fApl) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String queryCC = "SELECT TOP 1 cunidadResponsable FROM tRetencionEncabezado WITH( NOLOCK )  WHERE nFolioRetencion = ?";
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
                    ps = conn.prepareStatement("SELECT top 1 mesAbierto FROM dbo.tMesesContables WITH (NOLOCK) WHERE cUnidadResponsable = (" + queryCC + ") AND nMes = ?");
                    ps.setInt(1, nIdCaso);
                    ps.setInt(2, mes);
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

    public static void descartaRete(Connection conn, String uLogin, Caso c) throws Exception {
        int nFolio = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf("-") + 1));
        descartaRetencion(conn, uLogin, nFolio);
    }

    public static void descartaRetencion(Connection conn, String uLogin, int nFolio) throws Exception {
        String queryExiste = "SELECT COUNT(*) AS Existe FROM tRetencionEncabezado (NOLOCK) WHERE nFolioRetencion = ? group by cDocumentoHaplicado";
        String queryContEnc = "DELETE dbo.tRetencionEncabezado WHERE nFolioRetencion = ?";
        String queryContDet = "DELETE dbo.tRetencionDetalle WHERE nFolioRetencion = ?";
        PreparedStatement psEncabezado = null, psDetalle = null, psExiste = null;
        ResultSet rsExiste = null;
        int existe = 0;
        try {
            psExiste = conn.prepareStatement(queryExiste);
            psExiste.setInt(1, nFolio);
            rsExiste = psExiste.executeQuery();
            if (rsExiste.next()) {
                existe = rsExiste.getInt("Existe");
            }
            if (existe > 0) {
                psDetalle = conn.prepareStatement(queryContDet);
                psDetalle.setInt(1, nFolio);
                psDetalle.execute();
                psEncabezado = conn.prepareStatement(queryContEnc);
                psEncabezado.setInt(1, nFolio);
                psEncabezado.execute();
            }
        } finally {
            CloseObject.closeObject(rsExiste, false);
            CloseObject.closeObject(psEncabezado, false);
            CloseObject.closeObject(psDetalle, false);
            CloseObject.closeObject(psExiste, false);
        }
    }

    public static ArrayList<String> generaArchivoSICOP(Connection conn, int folio) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntH = null;
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        try {
            // Genera el encabezado del layout
            StringBuilder query = new StringBuilder();
            query.append("SELECT 'H' AS Header, CONVERT(nvarchar(10), GETDATE(),103),CONVERT(nvarchar(10), GETDATE(),103),cRamo,cRamo,cRamo,'RHQ' UnidadResponsable,");
            query.append(" 'RHQ' UnidadResponsable,'RHQ' UnidadResponsable,'N' ID_TIPO_MOVIMIENTO,nOrigenPpto,'7' AS TipoSol,'MXN' TipoMoneda,	'1' TipoCambio,'1' TIPO_PAGO,");
            query.append("			'1' AS CveLeyenda, ben.CBEN ");
            query.append(" , case when ben.CBEN = 'S04929' THEN '072320005134113068' ELSE CBEN END CBEN ");
            query.append(",'16RHQ','FAC','' FechaReferencia,'' Referencia1,'' Referencia2,' Retenciones del pago ' + caNoContrarrecibo,");
            query.append(" caNoContrarrecibo NO_ACMI,ret.nFolioRetencion AuxiliarComodin,caNoContrarrecibo CTR,  ");
            query.append(" CONVERT(decimal(17, 2), ISNULL(SUM(mISRArrenda + mISRHonorarios + mISROtros + mImporteISRLaudos),0)) isr, ");
            query.append(" CONVERT(decimal(17, 2), ISNULL(SUM(mImporteIva6 + mImporteIvaArrenda + mImporteIvaHonorarios + mImporteFlete4),0)) iva,");
            query.append(" CONVERT(decimal(17, 2), ISNULL(SUM(mObra5),0)) obra, 	");
            query.append(" CONVERT(decimal(17, 2), ISNULL(SUM(m2Millar),0)) m2millar, ");
            query.append(" CONVERT(decimal(17, 2), ISNULL(SUM(mRetImpuestoCedular),0)) cedular, ");
            query.append(" 0 penas, 0 contribucion, 0 iva, 0 IVAANT,");
            query.append(" 'NA' ID_DESTINO_GASTO ");
            query.append(" FROM tretencionEncabezado ret WITH (NOLOCK) ");
            query.append(" INNER JOIN tRetencionDetalle DET WITH (NOLOCK) ");
            query.append(" ON DET.nFolioRetencion = ret.nFolioRetencion");
            query.append(" inner join tBeneficiario ben WITH (NOLOCK)");
            query.append(" on ret.cIdRFC = ben.dRFC");
            query.append(" WHERE ret.nFolioRetencion = ?");
            query.append(" GROUP BY cRamo, ret.cUnidadResponsable, nOrigenPPTO, CBEN, CTAB, caNoContrarrecibo, ret.nFolioRetencion");
            String Sql = query.toString();
            pstmntH = conn.prepareStatement(Sql);
            pstmntH.setInt(1, folio);
            log.debug("Object: {}", Sql.toString());
            rs = pstmntH.executeQuery();
            log.debug("Inicia proceso de layout de retenciones encabezado");
            // Inserta el encabezado en el arrayList
            while (rs.next()) {
                String encabezado = rs.getString(1) + "," + rs.getString(2) + "," + rs.getString(3).trim() + "," + rs.getString(4).trim() + "," + rs.getString(5).trim() + "," + rs.getString(6).trim() + "," + rs.getString(7).trim() + "," + rs.getString(8).trim() + "," + rs.getString(9).trim() + "," + rs.getString(10).trim() + "," + rs.getString(11).trim() + "," + rs.getString(12).trim() + "," + rs.getString(13).trim() + "," + rs.getString(14).trim() + "," + rs.getString(15).trim() + "," + rs.getString(16).trim() + "," + rs.getString(17).trim() + "," + rs.getString(18).trim() + "," + rs.getString(19).trim() + "," + rs.getString(20).trim() + "," + rs.getString(21).trim() + "," + rs.getString(22).trim() + "," + rs.getString(23).trim() + "," + rs.getString(24).trim().replaceAll(",", " ") + "," + rs.getString(25).trim() + "," + rs.getString(26).trim() + "," + rs.getString(27).trim() + "," + rs.getString(28).trim() + "," + rs.getString(29).trim() + "," + rs.getString(30).trim() + "," + rs.getString(31).trim() + "," + rs.getString(32).trim() + "," + rs.getString(33).trim() + "," + rs.getString(34).trim() + "," + rs.getString(35).trim() + "," + rs.getString(36).trim() + "," + rs.getString(37).trim();
                encabezado = encabezado + "";
                arrListaComp.add(encabezado);
            }
            // Detalle de la Retencion
            StringBuilder query2 = new StringBuilder();
            query2.append("	SELECT '1' ID_EVENTO,  ");
            query2.append(" '24.0.001' EVENTO,  ");
            query2.append(" SUBSTRING(RDD.EP,6,2) ID_RAMO_ML,  ");
            query2.append(" 'RHQ',  SUBSTRING(RDD.EP,1,4) aEjercicioFiscal,  SUBSTRING(RDD.EP,13,1) cGrupoFuncional,  SUBSTRING(RDD.EP,15,1) cFuncion,  SUBSTRING(RDD.EP,17,2) cSubFuncion,  CASE WHEN SUBSTRING(RDD.EP,20,2) IN (SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK)) THEN '00' ELSE SUBSTRING(RDD.EP,20,2) END AS cProgramaGeneral,   SUBSTRING(RDD.EP,23,3) cActividadInstitucional,   SUBSTRING(RDD.EP,27,4) cProgramaPresupuestario,   SUBSTRING(RDD.EP,32,1) CCAP_157,   SUBSTRING(RDD.EP,33,1)CCON_158,  SUBSTRING(RDD.EP,34,1) CPARG_300,   SUBSTRING(RDD.EP,35,2) CPAR_159,  SUBSTRING(RDD.EP,38,1) cTipoGasto,  SUBSTRING(RDD.EP,40,1) cFuenteFinanciamiento,   SUBSTRING(RDD.EP,42,2) cEntidadFederativa,   SUBSTRING(RDD.EP,45,11)cCartera,   '0000000000',   '00'CCOP_163,   '000' PL,  '000' OFI,  ");
            query2.append(" '00000' AUX1,  ");
            query2.append(" '00000' AUX2,  ");
            query2.append(" '0000000000' AUX3,  ");
            query2.append(" RDE.ncompromisoSICOP NoComp,");
            query2.append(" CONVERT(decimal(17, 2),SUM(RDD.mImporte)) MONTO,  ");
            query2.append(" RDD.cMes AS MES_149,  ");
            query2.append(" '0' NRES,  ");
            query2.append(" 'PI' TIPO_CONTRATO,  ");
            query2.append(" '240' CONC_MOV,  ");
            query2.append(" CONVERT(decimal(17, 2), 0) isr, CONVERT(decimal(17, 2), 0) iva, CONVERT(decimal(17, 2), 0) mill5, CONVERT(decimal(17, 2), 0) mill2, CONVERT(decimal(17, 2), 0) cedular,");
            query2.append(" CONVERT(decimal(17, 2), 0) DCD_PENALIZACION,   ");
            query2.append(" CONVERT(decimal(17,2), 0) DCD_CONTRIBUCION,  ");
            query2.append(" CONVERT(decimal(17, 2), 0) IVADES_45,  		");
            query2.append(" '' id_ctr_intdet   ");
            query2.append(" FROM tRetencionDetalle RDD WITH (NOLOCK)  ");
            query2.append(" INNER JOIN tRetencionEncabezado RDE WITH (NOLOCK)  ");
            query2.append("		ON (RDE.nFolioRetencion = RDD.nFolioRetencion)  ");
            query2.append(" WHERE RDE.nFolioRetencion = ? ");
            query2.append("	GROUP BY SUBSTRING(RDD.EP,61,3), RDD.EP,  RDE.nFolioRetencion,	RDD.cMes, RDE.nCompromisoSICOP");
            String Sql2 = query2.toString();
            log.debug("Object: {}", Sql2.toString());
            pstmntD = conn.prepareStatement(Sql2);
            pstmntD.setInt(1, folio);
            rs2 = pstmntD.executeQuery();
            log.debug("Inicia proceso detalle retenciones");
            while (rs2.next()) {
                String token = new String();
                StringBuffer detalle = new StringBuffer();
                for (int i = 1; i < 41; i++) {
                    detalle.append(token).append(rs2.getString(i).trim().replaceAll(",", " "));
                    token = ",";
                }
                token = "";
                detalle.append("");
                arrListaComp.add(detalle.toString());
            }
            return arrListaComp;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(pstmntH, false);
            CloseObject.closeObject(pstmntD, false);
        }
    }

    public static ArrayList<String> generaCompromisoSICOP(Connection conn, int folio) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntH = null;
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        try {
            // Genera el encabezado del layout de compromiso
            StringBuilder query = new StringBuilder();
            query.append(" SELECT   'H'  AS Header, ");
            query.append("		    convert(varchar, trete.fAplicacion ,103) fAplicacion,   ");
            query.append("		    convert(varchar,  tRete.fcarga,103) fcarga,   ");
            query.append("		          tRete.cramo,  ");
            query.append("		          tRete.cramo,  ");
            query.append("		          tRete.cramo,  ");
            query.append("		          tRete.cUnidadResponsableContable,  ");
            query.append("		          tRete.cUnidadResponsableContable,  ");
            query.append("		          tRete.cUnidadResponsableContable,  ");
            query.append("		          'A' AS MOVTO, ");
            query.append("				  1 COMPROMISO,");
            query.append("				  7 EROGACION,");
            query.append("				  1 TIPO,");
            query.append("				  pag.integracion,");
            query.append("				  REPLACE(REPLACE(REPLACE(LEFT(tRete.cConcepto, 70), CHAR(10), ''), CHAR(13), ''), CHAR(9), '')  AS cConceptoContrato,  ");
            query.append("				  'S04929'                   AS cben,  ");
            query.append("		          'CNF010405EG1'             AS cidrfc, ");
            query.append("				  'TANIA ANANI LIMON MAGAÑA' AS REPRESENTANTE_LEGAL,");
            query.append("				  '0'                        AS TPROC, ");
            query.append("				   0                         AS ESQ_PRECIO, ");
            query.append("				  '0'                        AS CONTRATACION,");
            query.append("		          convert(varchar,Getdate(),103) AS fcontratoini,  ");
            query.append("		          convert(varchar,DATEADD(DAY,5,GETDATE() ),103)  AS fcontratofin,  ");
            query.append("				  convert(varchar,Getdate(),103) AS ffirmacontrato , ");
            query.append("		          'N'                        AS ES_PLURIANUAL,  ");
            query.append("				  ''                         AS APROB_PLA, ");
            query.append("		          ''                         AS ACTO_JURIDICO,");
            query.append("				   SUM(Abs(tDet.mimporte))   AS MONTO_MONORI,");
            query.append("				  'MXN'						 AS TIPO_MONEDA,  ");
            query.append("				  '1'                        AS TCAM, ");
            query.append("				  SUM(Abs(tDet.mimporte))    AS MONTO_EJER,  ");
            query.append("		          SUM(Abs(tDet.mimporte))    AS MONTO_MIN,  ");
            query.append("		          SUM(Abs(tDet.mimporte))    AS MONTO_MAX,");
            query.append("				  'N'                        AS CONV_MOD,");
            query.append("			      ''                         AS NUM_CONV_MOD,  ");
            query.append("		          ''                         AS FECHA_MOD_CONV,  ");
            query.append("				  ''                         AS CODIGO_CONTRATO,");
            query.append("				  ''                         AS CODIGO_EXP,");
            query.append("				  ''                         AS NOPROCEDIMIENTO,");
            query.append("				  month(trete.faplicacion) mes, ");
            query.append("				  ''                         AS ID_CTR_INT,");
            query.append("				  ''                         AS TTRANS_21, ");
            query.append("				 'RETE' + CAST(tRete.nFolioRetencion AS varchar(3)) + TRETE.cUnidadResponsable FolioInterno, ");
            query.append("		         'RETE' + CAST(tRete.nFolioRetencion AS varchar(3)) + TRETE.cUnidadResponsable,");
            query.append("				 'N'                         AS ETIQUETA_COMPRANET,  ");
            query.append("		         'RETENCIONES ISR LAUDOS'    AS JUSTIFICA_COMPRANET,  ");
            query.append("		         '0'                         AS IVA_MON_ORIG_414, ");
            query.append("				 '0'                         AS IMP_CONT_SIVA_413,  ");
            query.append("		         '0'                         AS IMP_CONV_MOD_415, ");
            query.append("		  	 	 ''							 AS fTermino_Ultima  ");
            query.append("		   FROM   tRetencionEncabezado tRete (Nolock)");
            query.append("		          INNER JOIN v_AplicarEjercidoPagadoEncabezado PAG (Nolock)");
            query.append("				  ON tRete.caNoContrarrecibo = PAG.caNoContrarrecibo");
            query.append("				  inner join tRetencionDetalle tDet (Nolock)");
            query.append("				  on tRete.nFolioRetencion = tDet.nFolioRetencion ");
            query.append("		   WHERE tRete.nFolioRetencion = ? ");
            query.append("		   GROUP  BY tRete.nFolioRetencion,  tRete.faplicacion,  tRete.fcarga,  tRete.cramo,  tRete.nFolioRetencion,  tRete.cConcepto,");
            query.append("			tRete.cUnidadResponsableContable,  PAG.integracion ,tRete.cunidadResponsable,    tRete.cunidadresponsable, tRete.ccentrocontable");
            String sql = query.toString();
            pstmntH = conn.prepareStatement(sql);
            pstmntH.setInt(1, folio);
            log.debug("Object: {}", sql.toString());
            rs = pstmntH.executeQuery();
            log.debug("Inicia proceso de layout de retenciones encabezado");
            // Inserta el encabezado en el arrayList
            while (rs.next()) {
                String token = new String();
                StringBuffer encabezado = new StringBuffer();
                for (int i = 1; i < 51; i++) {
                    encabezado.append(token).append(rs.getString(i).trim().replaceAll(",", " "));
                    token = ",";
                }
                token = "";
                encabezado.append("");
                arrListaComp.add(encabezado.toString());
            }
            // Detalle del compromiso de la Retencion
            String Sql2 = "	SELECT '668' as ID_EVENTO" + "			 , '304_TOCN' as EVENTO" + "			 , tCEP.cRamo" + "			 , tCEP.cUnidadResponsableEP" + "			 , tCEP.aEjercicioFiscal" + "			 , tCEP.cGrupoFuncional" + "			 , tCEP.cFuncion" + "			 , tCEP.cSubFuncion" + "			 , SUBSTRING( dbo.CambiaEPPlurianual(tCD.EP), 20, 2) AS cProgramaGeneral" + "			 , tCEP.cActividadInstitucional" + "			 , tCEP.cProgramaPresupuestario" + "			 , SUBSTRING(tCEP.cPartida,1,1)" + "			 , SUBSTRING(tCEP.cPartida,2,1)" + "			 , SUBSTRING(tCEP.cPartida,3,1)" + "			 , SUBSTRING(tCEP.cPartida,4,2)" + "			 , tCEP.cTipoGasto" + "			 , tCEP.cFuenteFinanciamiento" + "			 , tCEP.cEntidadFederativa" + "			 , SUBSTRING( dbo.CambiaEPCarteraMeta(tCEP.EP),45, 11) AS cCartera" + "			 , '000000' + '0000' as CAU " + "			 , '00' as COP" + "			 , '000' as PL" + "			 , '000' as OF_" + "			 , '00000' as AUX1" + "			 , '00000' as AUX2" + "			 , '0000000000' as AUX3" + "			 , '' as Suficiencia" + "			 , '' as Sol_OLI" + "			 , SUM(CASE tCD.cMes when 1 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Enero" + "			 , SUM(CASE tCD.cMes when 2 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Febrero" + "			 , SUM(CASE tCD.cMes when 3 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Marzo" + "			 , SUM(CASE tCD.cMes when 4 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Abril" + "			 , SUM(CASE tCD.cMes when 5 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Mayo" + "			 , SUM(CASE tCD.cMes when 6 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Junio" + "			 , SUM(CASE tCD.cMes when 7 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Julio" + "			 , SUM(CASE tCD.cMes when 8 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Agosto" + "			 , SUM(CASE tCD.cMes when 9 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Septiembre" + "			 , SUM(CASE tCD.cMes when 10 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Octubre" + "			 , SUM(CASE tCD.cMes when 11 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Noviembre" + "			 , SUM(CASE tCD.cMes when 12 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Diciembre" + "			 , SUM( convert(decimal(14,2), abs(tCD.mImporte)) ) as Importe" + "			 	FROM tRetencionDetalle tCD (NOLOCK),tCatalogoEP tCEP (NOLOCK), tRetencionEncabezado tCE (NOLOCK)" + "			 	WHERE tCD.nFolioRetencion = ?" + "			 	AND tCD.EP = tCEP.EP" + "			 	AND tCE.nFolioRetencion = tCD.nFolioRetencion " + "			 	group by tCEP.cRamo,  tCEP.aEjercicioFiscal, tCEP.cUnidadResponsableEP, tCEP.cGrupoFuncional, tCEP.cFuncion, tCEP.cSubFuncion, " + "			  SUBSTRING( dbo.CambiaEPPlurianual(tCD.EP), 20, 2), tCEP.cActividadInstitucional, SUBSTRING( dbo.CambiaEPCarteraMeta(tCEP.EP),45, 11)," + "			  tCEP.cProgramaPresupuestario, tCEP.cPartida, tCEP.cTipoGasto, tCEP.cFuenteFinanciamiento, tCEP.cEntidadFederativa, tCEP.cCartera";
            log.debug("Object: {}", Sql2.toString());
            pstmntD = conn.prepareStatement(Sql2);
            pstmntD.setInt(1, folio);
            rs2 = pstmntD.executeQuery();
            log.debug("Inicia proceso compromiso detalle retenciones");
            while (rs2.next()) {
                String token = new String();
                StringBuffer detalle = new StringBuffer();
                for (int i = 1; i < 42; i++) {
                    detalle.append(token).append(rs2.getString(i).trim().replaceAll(",", " "));
                    token = ",";
                }
                token = "";
                detalle.append("");
                arrListaComp.add(detalle.toString());
            }
            return arrListaComp;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(pstmntH, false);
            CloseObject.closeObject(pstmntD, false);
        }
    }

    public static String buscaCompromiso(Connection conn, int folio) throws Exception {
        String folioSicop = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT nFolioAutSICOP ");
        sql.append("	 FROM tCompromisoEncabezado comp (nolock) where cIdContrato in ");
        sql.append("	 ( SELECT integracion from tRetencionEncabezado ret (nolock)");
        sql.append("		 inner join v_AplicarEjercidoPagadoEncabezado pag (nolock)");
        sql.append("		 on ret.caNoContrarrecibo = pag.caNoContrarrecibo ");
        sql.append("		 where nFolioRetencion = ? )");
        try {
            String query = sql.toString();
            ps = conn.prepareStatement(query);
            ps.setInt(1, folio);
            rs = ps.executeQuery();
            if (rs.next()) {
                folioSicop = rs.getString(1);
            }
            log.debug("Object: {}", "El folio de sicop es " + folioSicop);
        } finally {
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rs, false);
        }
        return folioSicop;
    }
}
