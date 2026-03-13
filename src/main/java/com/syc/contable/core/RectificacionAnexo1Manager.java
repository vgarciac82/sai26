package com.syc.contable.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RectificacionAnexo1Manager {

    /*
	 * TODO VGC Esto no funciona por ser estatico. En un ambiente paralelo se
	 * sobreescribiria para todos los hilos. Cambiar
	 */
    static String ur;

    private static final Logger log = LoggerFactory.getLogger(RectificacionAnexo1Manager.class);

    public static void setUR(String UR) {
        ur = UR;
    }

    public static String getUR() {
        return ur;
    }

    public static StringBuffer filtraTipoCLC(Connection conn, String tipoCLC) throws Exception {
        StringBuffer sbTipo = new StringBuffer();
        return sbTipo;
    }

    public static boolean insertarEncabezado(Connection conn, int folio, String nIDCaso, String cEjercicio, String cRamo, String cUnidad, String cCentroContable, java.sql.Date fExp, java.sql.Date fApl, String conceptoRectificacion, String cTipoMovto, String nOrigenPPTO, String nMes, String oficioRectif, String ctr_int, String cTipoRectificacion, String nFolioSICOP, String caNoContrarrecibo, String cTipoPoliza, String cDescripcionPoliza, String u_login, String nFolioPoliza, String cUnidadResponsableContable, String nFolioSIAFF, String totalDebe, String totalDice) throws SQLException {
        Integer nFolioRectificacion = folio;
        Integer id_caso = Integer.parseInt(nIDCaso);
        Integer folioSICOP = Integer.parseInt(nFolioSICOP);
        Integer origenPPTO = Integer.parseInt(nOrigenPPTO);
        Integer mes = Integer.parseInt(nMes);
        PreparedStatement pstmnt = null;
        String insertQuery = "INSERT INTO tRectificaAnexo1Encabezado(nFolioRectificaAnexo1,nIDCaso,aEjercicioFiscal,cRamo,cUnidadResponsable" + ",cCentroContable,fExp,fAplicacion,cConceptoRectificacion,cTipoMovto,nOrigenPPTO" + "	 ,nMes,oficioRectif,ctr_int,cTipoRectificacion,nFolioSICOP,caNoContrarrecibo" + " ,nFolioPoliza,cTipoPoliza,cDescripcionPoliza,cU_LoginCaptura" + "	,cUnidadResponsableContable,nFolioSIAFF,totalDebe,totalDice) " + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
        // ejecuta el query y guarda resultados en un ResultSet
        if (pstmnt.execute()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean insertarDetalle(Connection conn, ArrayList<RectificacionAnexo1Detalle> rectificaciones, String caNoContrarrecibo) throws SQLException {
        PreparedStatement pstmnt = null;
        boolean val = false;
        try {
            // if(!rd.getRenglon().contains("R")){ //esto lo hago porque marthi
            // pone una R en la tabla para saber si es repetido
            for (int i = 0; i < rectificaciones.size(); i++) {
                RectificacionAnexo1Detalle rd = new RectificacionAnexo1Detalle();
                rd = rectificaciones.get(i);
                String insertQuery = "INSERT INTO tRectificaAnexo1Detalle( " + "		nFolioRectificaAnexo1," + "		nDocRenglon," + "		cEvento," + "		EP," + "		mImporte," + "		mImporteNegativo," + "		cMes," + "		cCentroContable," + "		CTAB," + "		OBGT" + "		,caNoContrarrecibo" + ") " + "VALUES(" + "		?," + "		?," + "		?," + "		?," + "		?," + "		?," + "		?," + "		?," + "		(SELECT TOP 1 sCUENTA_BANCARIA FROM tLayoutsCreadosAnexo1Header WITH(NOLOCK) WHERE cEstatus='ACTIVO' AND sAuxiliarComodin='" + caNoContrarrecibo.trim() + "' ORDER BY ID DESC)," + " 		SUBSTRING(?,32,5)," + "		?" + ")";
                pstmnt = conn.prepareStatement(insertQuery);
                pstmnt.setInt(1, rd.getFolio());
                pstmnt.setInt(2, i + 1);
                pstmnt.setString(3, rd.getEvento());
                pstmnt.setString(4, rd.getEp());
                pstmnt.setDouble(5, rd.getImporte());
                pstmnt.setDouble(6, rd.getImporteneg());
                pstmnt.setInt(7, rd.getMes());
                pstmnt.setString(8, rd.getCentro());
                pstmnt.setString(9, rd.getEp());
                pstmnt.setString(10, rd.getcaNoContrarrecibo());
                val = pstmnt.execute();
            }
            // }
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
        }
        return val;
    }

    public static void autorizaRectificacion(Connection conn, Caso c, String ulogin, String prefixPath, String tipoAplicacion, String fApl) throws SQLException, GestionException {
        PreparedStatement pstmnt = null;
        PreparedStatement pstmnt2 = null;
        int nIdCaso;
        String cSQLString = "";
        ResultSet rs = null;
        int nExiste = 0;
        try {
            nIdCaso = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
            cSQLString = "select COUNT(*) from tRectificacionAutEncabezado with (nolock) WHERE nFolioRectificacionAut = " + nIdCaso;
            pstmnt = conn.prepareStatement(cSQLString);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                nExiste = rs.getInt(1);
            }
            if (nExiste == 0) {
                pstmnt = conn.prepareStatement("INSERT INTO tRectificacionAutEncabezado (nFolioRectificacionAut, nIdCaso, aEjercicioFiscal, cRamo, cUnidadResponsable, cCentroContable, fExp, fAplicacion, cConceptoRectificacion, cTipoMovto, " + "nOrigenPPTO,nMes, oficioRectif, ctr_int,cTipoRectificacion, nCLC, nFolioSicop,nFolioSAI, nFolioSIAFF,caNoContrarrecibo,cTipoPoliza,fCancelacion,cU_LoginCaptura,cRevisado,cU_LoginRevision,cMotivoRechazo,nFolioTramiteSicop,cUnidadResponsableContable,totalDebe) SELECT nFolioRectificacion, nIdCaso, aEjercicioFiscal, cRamo, cUnidadResponsable, cCentroContable, fExp, " + "'" + fApl + "', cConceptoRectificacion, cTipoMovto, " + "nOrigenPPTO,nMes, oficioRectif, ctr_int,cTipoRectificacion, nCLC, nFolioSicop,nFolioSAI, nFolioSIAFF,caNoContrarrecibo,'DI',fCancelacion,cU_LoginCaptura,cRevisado,cU_LoginRevision,cMotivoRechazo,nFolioTramiteSicop,cUnidadResponsableContable,totalDebe FROM tRectificacionEncabezado WITH (NOLOCK) WHERE nFolioRectificacion = ?");
                pstmnt.setInt(1, nIdCaso);
                pstmnt.execute();
                pstmnt = conn.prepareStatement("SELECT cTipoRectificacion FROM tRectificacionEncabezado WITH (NOLOCK) WHERE nFolioRectificacion = ?");
                pstmnt.setInt(1, nIdCaso);
                rs = pstmnt.executeQuery();
                String tipoRect = "";
                if (rs.next()) {
                    tipoRect = rs.getString(1);
                }
                pstmnt2 = conn.prepareStatement("SELECT * FROM tEventoRectificacion where ");
                pstmnt = conn.prepareStatement("INSERT INTO tRectificacionAutDetalle(nFolioRectificacionAut,nDocRenglon,EP,cEvento,mImporte, mImporteNegativo, cMes, cCentroContable, OBGT) " + "SELECT rd.nFolioRectificacion,rd.nDocRenglon,rd.EP, CASE '" + tipoAplicacion + "' WHEN 'Rectificacion' THEN CASE (SUBSTRING(cEvento,1,4)) WHEN 'DICE' THEN 'DICE_' + cNoEvento WHEN 'DEBE' THEN 'DEBE_DECIR_' + cNoEvento END " + " WHEN 'Reclasificacion' THEN CASE (SUBSTRING(cEvento,1,4)) WHEN 'DICE' THEN 'DICE_AUT_DI' WHEN 'DEBE' THEN 'DEBE_DECIR_A' END END as cEvento,rd.mImporte,rd.mImporteNegativo,rd.cMes, rd.cCentroContable, SUBSTRING(EP,32,5) AS obgt " + " FROM tRectificacionDetalle rd WITH (NOLOCK) JOIN tEventoRectificacion WITH (NOLOCK) ON SUBSTRING(EP,32,5) = cPartida WHERE rd.nFolioRectificacion = ?");
                pstmnt.setInt(1, nIdCaso);
                pstmnt.execute();
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

    public static RectificacionAnexo1Encabezado getRectificacionEncabezado(Connection conn, int folio) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        RectificacionAnexo1Encabezado re = null;
        pstm = conn.prepareStatement("SELECT * FROM tRectificaAnexo1Encabezado WITH(NOLOCK) WHERE nFolioRectificaAnexo1 = ?");
        pstm.setInt(1, folio);
        res = pstm.executeQuery();
        if (res.next()) {
            re = new RectificacionAnexo1Encabezado();
            re.setnFolioRectificacion(Integer.parseInt(res.getString("nFolioRectificaAnexo1")));
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

    public static RectificacionAnexo1Encabezado getRectificacionEncabezadoSicop(Connection conn, int folio) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        RectificacionAnexo1Encabezado re = null;
        String query = "SELECT  'H' AS posicion1 , " + "	r.fAplicacion , " + "	r.fExp , " + "	r.cRamo , " + "	r.cRamo , " + "	r.cRamo , " + "	r.cUnidadResponsable , " + "	r.cUnidadResponsable , " + "	r.cUnidadResponsable , " + "	ISNULL(r.cTipoMovto, '') AS cTipoMovto , " + "	ISNULL(r.nOrigenPPTO, '') AS nOrigenPPTO , " + "	ISNULL(r.cConceptoRectificacion, '') AS cConceptoRectificacion , " + "	ISNULL(R.ctr_int, '') AS ctr_int , " + "	R.oficioRectif , " + "	R.oficioRectif , " + "	R.nFolioRectificaAnexo1 , " + "	R.nIDCaso , " + "	R.totalDICE , " + "	R.totalDEBE " + "FROM tRectificaAnexo1Encabezado r WITH ( NOLOCK ) " + "WHERE nFolioRectificaAnexo1 = ? ";
        pstm = conn.prepareStatement(query);
        pstm.setInt(1, folio);
        res = pstm.executeQuery();
        if (res.next()) {
            re = new RectificacionAnexo1Encabezado();
            re.setPosicion1(res.getString("posicion1"));
            re.setcRamo(res.getString("cRamo"));
            re.setcUnidadResponsable(res.getString("cUnidadResponsable"));
            re.setnOrigenPPTO(res.getString("nOrigenPPTO"));
            re.setcConceptoRectificacion(res.getString("cConceptoRectificacion"));
            re.setnFolioRectificacion(Integer.parseInt(res.getString("nFolioRectificaAnexo1")));
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
        String query = "SELECT DISTINCT " + "	ISNULL(r.nOrigenPPTO, 0) AS nOrigenPPTO , " + "	'0' , " + "	r.nFolioSICOP , " + "	d.nDocRenglon , " + "	e.cRamoEP , " + "	e.cUnidadResponsableEP , " + "	e.aEjercicioFiscal , " + "	e.cGrupoFuncional , " + "	e.cFuncion , " + "	e.cSubFuncion , " + "	e.cProgramaGeneral , " + "	e.cActividadInstitucional , " + "	e.cProgramaPresupuestario , " + "	SUBSTRING(e.cPartida, 1, 1) AS cCapitulo , " + "	SUBSTRING(e.cPartida, 2, 1) AS cConcepto , " + "	SUBSTRING(e.cPartida, 3, 1) AS cPartida , " + "	SUBSTRING(e.cPartida, 4, 2) AS cPartidaEspecifica , " + "	e.cTipoGasto , " + "	e.cFuenteFinanciamiento , " + "	e.cEntidadFederativa , " + "	e.cCartera , " + "	'0000000000' AS cUnidadEjecutora , " + "	'00' AS cUnidadNormativa , " + "	'000' , " + "	'000' , " + "	'00000' , " + "	'00000' , " + "	'0000000000' , " + "	CONVERT(VARCHAR, d.mImporte) , " + "	d.cMes , " + "	'' AS cFillRellen1 , " + "	'S04929' AS CBEN, " + "	'' AS cFillRellen2 , " + "	'0' AS sol_oli , " + "	CASE WHEN cPartida = '35801' THEN 'GD' " + "	ELSE 'PN' " + "	END ID_TIPO_CONCEPTO , " + "	'000' AS tipo_concepto , " + "	'0' AS concepto_mov , " + "	'0' retencion_isr , " + "	'0' , " + "	CASE WHEN d.cEvento LIKE '%DICE%' THEN 'A' " + "	ELSE 'C' " + "	END , " + "	ISNULL(R.ctr_int, '') AS ctr_int " + "FROM tRectificaAnexo1Detalle d WITH ( NOLOCK ) , " + "	tRectificaAnexo1Encabezado r WITH ( NOLOCK ) , " + "	tCatalogoEP e WITH ( NOLOCK ) , " + "	tBeneficiario b WITH ( NOLOCK ) " + "WHERE d.nFolioRectificaAnexo1 = r.nFolioRectificaAnexo1 " + "	AND d.EP = e.EP " + "	AND b.dRFC = 'TESOFE' " + "	AND r.nFolioRectificaAnexo1 = ? " + "	AND d.nFolioRectificaAnexo1 = ? ";
        //TODO: Cambio CBEN por S04929 "	b.CBEN , " +
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

    public static ArrayList<RectificacionAnexo1Detalle> getRectificacionDetalle(Connection conn, int folio) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        ArrayList<RectificacionAnexo1Detalle> rds = new ArrayList<RectificacionAnexo1Detalle>();
        RectificacionAnexo1Detalle rd = null;
        pstm = conn.prepareStatement("SELECT * FROM tRectificaAnexo1Detalle WITH(NOLOCK) WHERE nFolioRectificaAnexo1=?");
        pstm.setInt(1, folio);
        res = pstm.executeQuery();
        while (res.next()) {
            rd = new RectificacionAnexo1Detalle();
            rd.setRenglon(res.getInt("nDocRenglon"));
            rd.setEvento(res.getString("cEvento"));
            rd.setEp(res.getString("EP"));
            rd.setImporte(res.getDouble("mImporte"));
            rd.setImporteneg(res.getDouble("mImporte"));
            rd.setMes(res.getInt("cMes"));
            rd.setCentro(res.getString("cCentroContable"));
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

    public static void actualizaFechaAplicacion(Connection conn, int folio) throws SQLException, ParseException {
        PreparedStatement pstmntUp = null;
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String today = sdf.format(c1.getTime());
        try {
            pstmntUp = conn.prepareStatement("UPDATE tRectificaAnexo1Encabezado set fAplicacion=? where nFolioRectificaAnexo1=?");
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
        int nIdCaso;
        ResultSet rs = null;
        String mensaje = "";
        int mes = Integer.parseInt(fApl.substring(3, 5));
        ;
        try {
            nIdCaso = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
            ps = conn.prepareStatement("SELECT mesAbierto FROM tMesesContables WITH (NOLOCK) WHERE cCentroContable = (SELECT TOP 1 cCentroContable FROM tRectificaAnexo1Encabezado WHERE nFolioRectificaAnexo1 = ?) AND nMes = ?");
            ps.setInt(1, nIdCaso);
            ps.setInt(2, mes);
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

    public static int secCLCRect(Connection conn, String caNoContrarrecibo, String EP) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        int retval = 0;
        String ep = EP.substring(0, 55);
        try {
            pstmnt = conn.prepareStatement("SELECT SEC FROM vReintegrosCLCSICOP with(nolock) WHERE FolioSIAFF=? and EP=?");
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
            //pstmnt.setInt(4, folio);
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
        String queryRemanente = "SELECT Remanente FROM vIngresoAnexo1Remanente WHERE canocontrarrecibo=? AND ep=? AND nDocRenglon=?";
        log.trace("Query: " + queryRemanente + "[" + cxp + ", " + ep + ", " + nRenglon + "]");
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

    public static String getEventoRectificacionAnexo1(Connection conn, String ep, String tipo) throws SQLException {
        String evento = "";
        ResultSet rs = null;
        PreparedStatement pstm = null;
        String query = "SELECT dbo.fn_evento_rectificacion_Anexo1(?,?) AS evento";
        log.trace("Obteniendo Evento: " + query + "[" + ep + "," + tipo + "]");
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
}
