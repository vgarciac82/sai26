package com.syc.contable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import com.syc.contable.core.RectificacionAnexo1Manager;
import com.syc.contable.core.RectificacionIngresoFiscalDetalle;
import com.syc.contable.core.RectificacionIngresoFiscalEncabezado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class RectificacionIngresoFiscalManager {

    static String ur;

    private static final Logger log = LoggerFactory.getLogger(RectificacionAnexo1Manager.class);

    public static void setUR(String UR) {
        ur = UR;
    }

    public static String getUR() {
        return ur;
    }

    public static RectificacionIngresoFiscalEncabezado getRectificacionEncabezado(Connection conn, int folio) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        RectificacionIngresoFiscalEncabezado re = null;
        pstm = conn.prepareStatement("SELECT * FROM tRectificacionIngresosEncabezado WITH(NOLOCK) WHERE nFolioRectificaIngreso = ?");
        pstm.setInt(1, folio);
        res = pstm.executeQuery();
        if (res.next()) {
            re = new RectificacionIngresoFiscalEncabezado();
            re.setnFolioRectificaIngreso(Integer.parseInt(res.getString("nFolioRectificaIngreso")));
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

    public static ArrayList<RectificacionIngresoFiscalDetalle> getRectificacionDetalle(Connection conn, int folio) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        ArrayList<RectificacionIngresoFiscalDetalle> rds = new ArrayList<RectificacionIngresoFiscalDetalle>();
        RectificacionIngresoFiscalDetalle rd = null;
        pstm = conn.prepareStatement("SELECT * FROM tRectificacionIngresosDetalle WITH (NOLOCK) WHERE nFolioRectificaIngreso = ?");
        pstm.setInt(1, folio);
        res = pstm.executeQuery();
        while (res.next()) {
            rd = new RectificacionIngresoFiscalDetalle();
            rd.setnDocRenglon(res.getInt("nDocRenglon"));
            rd.setcEvento(res.getString("cEvento"));
            rd.setEP(res.getString("EP"));
            rd.setmImporte(res.getDouble("mImporte"));
            rd.setmImporteNegativo(res.getDouble("mImporte"));
            rd.setcMes(res.getInt("cMes"));
            rd.setcCentroContable(res.getString("cCentroContable"));
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

    public static String getEventoRectificaIngresoFiscal(Connection conn, String ep, String tipo) throws SQLException {
        String evento = "";
        ResultSet rs = null;
        PreparedStatement pstm = null;
        String query = "SELECT dbo.fn_Evento_RectificacionIngresoFiscal( ?, ? ) AS evento";
        log.trace("Object: {}", "Obteniendo Evento: " + query + "[" + ep + "," + tipo + "]");
        pstm = conn.prepareStatement(query);
        pstm.setString(1, ep);
        pstm.setString(2, tipo);
        rs = pstm.executeQuery();
        if (rs.next())
            evento = rs.getString(1);
        if (pstm != null)
            pstm.close();
        if (rs != null)
            rs.close();
        return evento;
    }

    public static int secCLCRect(Connection conn, String caNoContrarrecibo, String EP) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        int retval = 0;
        String ep = EP.substring(0, 55);
        try {
            pstmnt = conn.prepareStatement(" SELECT SEC FROM vReintegrosCLCSICOP with(nolock) WHERE FolioSIAFF = ? and EP = ?");
            pstmnt.setString(1, String.format("%.0f", Double.parseDouble(caNoContrarrecibo)));
            pstmnt.setString(2, ep);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                retval = rs.getInt(1);
            } else {
                retval = -1;
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int[] getNDocRenglon(Connection conn, String caNoContrarrecibo, String EP, int cMes, int folio) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        int[] retval = new int[100];
        try {
            int i = 0;
            pstmnt = conn.prepareStatement("SELECT nDocRenglon FROM tconsolidacionrelaciongastosEncabezado Encabezado WITH(NOLOCK) INNER JOIN tconsolidacionrelaciongastosdetalle Detalle WITH(NOLOCK) ON Encabezado.nFolioConsolidacion = Detalle.nFolioConsolidacion WHERE Encabezado.nIdIntegracion=? AND ep=? AND cmes=?");
            pstmnt.setString(1, caNoContrarrecibo);
            pstmnt.setString(2, EP);
            pstmnt.setInt(3, cMes);
            rs = pstmnt.executeQuery();
            retval[0] = -1;
            while (rs.next()) {
                retval[i] = rs.getInt(1);
                i++;
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static double getRemanente(Connection conn, String ep, String cxp, int nRenglon) throws SQLException {
        double remanente = 0.00;
        ResultSet rs = null;
        PreparedStatement pstm = null;
        String queryRemanente = "SELECT Remanente FROM vRegistroIngresoFiscalRemanente WHERE canocontrarrecibo=? AND ep=? AND nDocRenglon=?";
        log.trace("Object: {}", "Query: " + queryRemanente + "[" + cxp + ", " + ep + ", " + nRenglon + "]");
        pstm = conn.prepareStatement(queryRemanente);
        pstm.setString(1, cxp);
        pstm.setString(2, ep);
        pstm.setInt(3, nRenglon);
        rs = pstm.executeQuery();
        if (rs.next()) {
            remanente = rs.getDouble(1);
        }
        if (pstm != null) {
            pstm.close();
        }
        if (rs != null) {
            rs.close();
        }
        return remanente;
    }

    public static boolean insertarEncabezado(Connection conn, int folio, String nIDCaso, String cEjercicio, String cRamo, String cUnidad, String cCentroContable, java.sql.Date fExp, java.sql.Date fApl, String conceptoRectificacion, String cTipoMovto, String nOrigenPPTO, String nMes, String oficioRectif, String ctr_int, String cTipoRectificacion, String nFolioSICOP, String caNoContrarrecibo, String cTipoPoliza, String cDescripcionPoliza, String u_login, String nFolioPoliza, String cUnidadResponsableContable, String nFolioSIAFF, String totalDebe, String totalDice) throws SQLException {
        Integer nFolioRectificacion = folio;
        Integer id_caso = Integer.parseInt(nIDCaso);
        Integer folioSICOP = Integer.parseInt(nFolioSICOP);
        Integer origenPPTO = Integer.parseInt(nOrigenPPTO);
        Integer mes = Integer.parseInt(nMes);
        PreparedStatement pstmnt = null;
        String insertQuery = "INSERT INTO tRectificacionIngresosEncabezado " + "( nFolioRectificaIngreso, nIDCaso, aEjercicioFiscal, cRamo, cUnidadResponsable" + " , cCentroContable, fExp, fAplicacion, cConceptoRectificacion, cTipoMovto, nOrigenPPTO " + "	, nMes, oficioRectif, ctr_int, cTipoRectificacion, nFolioSICOP, caNoContrarrecibo" + " , nFolioPoliza, cTipoPoliza, cDescripcionPoliza, cU_LoginCaptura" + "	, cUnidadResponsableContable, nFolioSIAFF, totalDebe, totalDice) " + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        pstmnt = conn.prepareStatement(insertQuery);
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
        if (pstmnt.execute()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean insertarDetalle(Connection conn, ArrayList<RectificacionIngresoFiscalDetalle> rectificaciones, String caNoContrarrecibo) throws SQLException {
        PreparedStatement pstmnt = null;
        boolean val = false;
        try {
            for (int i = 0; i < rectificaciones.size(); i++) {
                RectificacionIngresoFiscalDetalle rd = new RectificacionIngresoFiscalDetalle();
                rd = rectificaciones.get(i);
                String insertQuery = "	INSERT INTO tRectificacionIngresosDetalle " + " ( " + "		nFolioRectificaIngreso, " + "		nDocRenglon, " + "		cEvento, " + "		EP, " + "		mImporte, " + "		mImporteNegativo, " + "		cMes, " + "		cCentroContable, " + "		CTAB, " + "		OBGT, " + "		caNoContrarrecibo " + " ) " + "VALUES(" + "		?," + "		?," + "		?," + "		?," + "		?," + "		?," + "		?," + "		?," + "		(SELECT TOP 1 sCUENTA_BANCARIA FROM tLayoutsCreadosRegistroIngresoEncabezado WITH(NOLOCK) WHERE cEstatus='ACTIVO' AND sAuxiliarComodin='" + caNoContrarrecibo.trim() + "' ORDER BY ID DESC)," + " 		SUBSTRING(?,32,5)," + "		?" + ")";
                pstmnt = conn.prepareStatement(insertQuery);
                pstmnt.setInt(1, rd.getnFolioRectificaIngreso());
                pstmnt.setInt(2, i + 1);
                pstmnt.setString(3, rd.getcEvento());
                pstmnt.setString(4, rd.getEP());
                pstmnt.setDouble(5, rd.getmImporte());
                pstmnt.setDouble(6, rd.getmImporteNegativo());
                pstmnt.setInt(7, rd.getcMes());
                pstmnt.setString(8, rd.getcCentroContable());
                pstmnt.setString(9, rd.getEP());
                pstmnt.setString(10, rd.getcaNoContrarrecibo());
                val = pstmnt.execute();
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
        }
        return val;
    }

    public static void actualizaFechaAplicacion(Connection conn, int folio) throws SQLException, ParseException {
        PreparedStatement pstmntUp = null;
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String today = sdf.format(c1.getTime());
        try {
            pstmntUp = conn.prepareStatement("UPDATE tRectificacionIngresosEncabezado SET fAplicacion = ? WHERE nFolioRectificaIngreso = ? ");
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

    public static RectificacionIngresoFiscalEncabezado getRectificacionEncabezadoSicop(Connection conn, int folio) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        RectificacionIngresoFiscalEncabezado re = null;
        String query = "SELECT  'H' AS posicion1 , " + "	r.fAplicacion , " + "	r.fExp , " + "	r.cRamo , " + "	r.cRamo , " + "	r.cRamo , " + "	r.cUnidadResponsable , " + "	r.cUnidadResponsable , " + "	r.cUnidadResponsable , " + "	ISNULL(r.cTipoMovto, '') AS cTipoMovto , " + "	ISNULL(r.nOrigenPPTO, '') AS nOrigenPPTO , " + "	ISNULL(r.cConceptoRectificacion, '') AS cConceptoRectificacion , " + "	ISNULL(R.ctr_int, '') AS ctr_int , " + "	R.oficioRectif , " + "	R.oficioRectif , " + "	R.nFolioRectificaIngreso , " + "	R.nIDCaso , " + "	R.totalDICE , " + "	R.totalDEBE " + "FROM tRectificacionIngresosEncabezado r WITH ( NOLOCK ) " + "WHERE nFolioRectificaIngreso = ? ";
        pstm = conn.prepareStatement(query);
        pstm.setInt(1, folio);
        res = pstm.executeQuery();
        if (res.next()) {
            re = new RectificacionIngresoFiscalEncabezado();
            re.setPosicion1(res.getString("posicion1"));
            re.setcRamo(res.getString("cRamo"));
            re.setcUnidadResponsable(res.getString("cUnidadResponsable"));
            re.setnOrigenPPTO(res.getString("nOrigenPPTO"));
            re.setcConceptoRectificacion(res.getString("cConceptoRectificacion"));
            re.setnFolioRectificaIngreso(Integer.parseInt(res.getString("nFolioRectificaIngreso")));
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

    public static String getRectificacionDetalleSicop(Connection conn, int folio) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        String string = "";
        String query = "SELECT DISTINCT " + "	ISNULL(r.nOrigenPPTO, 0) AS nOrigenPPTO , " + "	'0' , " + "	r.nFolioSICOP , " + "	d.nDocRenglon , " + "	e.cRamoEP , " + "	e.cUnidadResponsableEP , " + "	e.aEjercicioFiscal , " + "	e.cGrupoFuncional , " + "	e.cFuncion , " + "	e.cSubFuncion , " + "	e.cProgramaGeneral , " + "	e.cActividadInstitucional , " + "	e.cProgramaPresupuestario , " + "	SUBSTRING(e.cPartida, 1, 1) AS cCapitulo , " + "	SUBSTRING(e.cPartida, 2, 1) AS cConcepto , " + "	SUBSTRING(e.cPartida, 3, 1) AS cPartida , " + "	SUBSTRING(e.cPartida, 4, 2) AS cPartidaEspecifica , " + "	e.cTipoGasto , " + "	e.cFuenteFinanciamiento , " + "	e.cEntidadFederativa , " + "	e.cCartera , " + "	'0000000000' AS cUnidadEjecutora , " + "	'00' AS cUnidadNormativa , " + "	'000' , " + "	'000' , " + "	'00000' , " + "	'00000' , " + "	'0000000000' , " + "	CONVERT(VARCHAR, d.mImporte) , " + "	d.cMes , " + "	'' AS cFillRellen1 , " + "	'S04929' AS CBEN, " + "	'' AS cFillRellen2 , " + "	'0' AS sol_oli , " + "	CASE WHEN cPartida = '35801' THEN 'GD' " + "	ELSE 'PN' " + "	END ID_TIPO_CONCEPTO , " + "	'000' AS tipo_concepto , " + "	'0' AS concepto_mov , " + "	'0' retencion_isr , " + "	'0' , " + "	CASE WHEN d.cEvento LIKE '%DICE%' THEN 'A' " + "	ELSE 'C' " + "	END , " + "	ISNULL(R.ctr_int, '') AS ctr_int " + " FROM tRectificacionIngresosDetalle d WITH ( NOLOCK ) , " + "	tRectificacionIngresosEncabezado r WITH ( NOLOCK ) , " + "	tCatalogoEP e WITH ( NOLOCK ) , " + "	tBeneficiario b WITH ( NOLOCK ) " + "WHERE d.nFolioRectificaIngreso = r.nFolioRectificaIngreso " + "	AND d.EP = e.EP " + "	AND b.dRFC = 'TESOFE' " + "	AND r.nFolioRectificaIngreso = ? " + "	AND d.nFolioRectificaIngreso = ? ";
        pstm = conn.prepareStatement(query);
        pstm.setInt(1, folio);
        pstm.setInt(2, folio);
        res = pstm.executeQuery();
        int i = 1;
        while (res.next()) {
            string += "\r\n" + "764";
            string += "," + res.getString(2);
            string += "," + res.getString(3);
            string += "," + i++;
            string += "," + res.getString(5);
            string += "," + res.getString(6);
            string += "," + res.getString(7);
            string += "," + res.getString(8);
            string += "," + res.getString(9);
            string += "," + res.getString(10);
            string += "," + res.getString(11);
            string += "," + res.getString(12);
            string += "," + res.getString(13);
            string += "," + res.getString(14);
            string += "," + res.getString(15);
            string += "," + res.getString(16);
            string += "," + res.getString(17);
            string += "," + res.getString(18);
            string += "," + res.getString(19);
            string += "," + res.getString(20);
            string += "," + res.getString(21);
            string += "," + res.getString(22);
            string += "," + res.getString(23);
            string += "," + res.getString(24);
            string += "," + res.getString(25);
            string += "," + res.getString(26);
            string += "," + res.getString(27);
            string += "," + res.getString(28);
            string += "," + res.getString(29);
            string += "," + res.getString(30);
            string += "," + res.getString(31);
            string += "," + res.getString(32);
            string += "," + res.getString(33);
            string += "," + res.getString(34);
            string += "," + res.getString(35);
            string += "," + res.getString(36);
            string += "," + res.getString(37);
            string += "," + res.getString(38);
            string += "," + res.getString(39);
            string += "," + res.getString(40);
            string += "," + res.getString(41);
        }
        if (pstm != null) {
            pstm.close();
        }
        if (res != null) {
            res.close();
        }
        return string;
    }
}
