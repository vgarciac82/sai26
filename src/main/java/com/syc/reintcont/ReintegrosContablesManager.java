package com.syc.reintcont;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReintegrosContablesManager {

    public static final Logger log = LoggerFactory.getLogger(ReintegrosContablesManager.class);

    public ReintegrosContablesManager() {
        super();
    }

    public static boolean actualizaFechaAplicacion(Connection conn, int folio, String fAplicacion) throws SQLException, ParseException {
        PreparedStatement pstmntUp = null;
        boolean retval = false;
        try {
            String sSQL = "UPDATE tReintegroEncabezado SET fAplicacion=? WHERE nFolioReintegro=?";
            log.debug("Object: {}", sSQL);
            pstmntUp = conn.prepareStatement(sSQL);
            log.debug("Object: {}", fAplicacion + " " + folio);
            pstmntUp.setString(1, fAplicacion);
            pstmntUp.setInt(2, folio);
            retval = pstmntUp.execute();
        } catch (SQLException s) {
            log.warn("Object: {}", s);
            s.printStackTrace();
        } finally {
            if (pstmntUp != null)
                pstmntUp.close();
            pstmntUp = null;
        }
        return retval;
    }

    public static String validaMes(Connection conn, Caso c, int folio, String fAcredit) {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String retval = "";
        int mes = Integer.parseInt(fAcredit.substring(3, 5));
        ;
        try {
            String sSQL = "SELECT mesAbierto FROM dbo.tMesesContables WITH (NOLOCK) WHERE cCentroContable = (SELECT TOP 1 cCentroContable FROM dbo.tReintegroDetalle WHERE nFolioReintegro = ?) AND nMes = ?";
            pstmnt = conn.prepareStatement(sSQL);
            log.debug("Object: {}", sSQL);
            pstmnt.setInt(1, folio);
            pstmnt.setInt(2, mes);
            log.debug("Object: {}", "Folio: " + folio + " Mes: " + mes);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                retval = rs.getString(1);
                log.debug("Object: {}", "Mes " + mes + " abierto: " + retval);
            }
        } catch (SQLException s) {
            log.warn("Object: {}", s);
            s.printStackTrace();
        } finally {
            if (pstmnt != null)
                pstmnt = null;
        }
        return retval;
    }

    public static void autorizaReintegro(Connection conn, Caso c, String nNumSicop, String cRecMotivSicop, String nNumMAP, String cRecMotivMAP, String ulogin, String prefixPath, String fAcredit, String year) throws SQLException, GestionException {
        PreparedStatement pstmnt = null;
        int nIdCaso;
        String cSQLString = "";
        ResultSet rs = null;
        int nExiste = 0;
        try {
            nIdCaso = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
            String sSQL = "INSERT INTO tReintegroAutEncabezado (nFolioReintegroaut, fSolicitud, cTipoReintegro, cRamo, cUnidadResponsable, cDocumentoHaplicado, aEjercicioFiscal, observaciones, concepto, u_login, " + "cUnidadResponsableContable,nFolioTramiteSicop, cTipoPoliza, fAplicacion,cdescripcionpoliza, formaDePago, clvRastreo,fichaDeposito, clvBanco,cuentaBancaria,lc,importeLC,clcSicop,folioDependencia,mvto,aviso,tipoAviso,causaAviso) " + "SELECT nFolioReintegro, fSolicitud, cTipoReintegro, cRamo,cUnidadResponsable, 'N', aEjercicioFiscal, observaciones, concepto, " + "u_login, 'RHQ',nFolioTramiteSicop, CASE WHEN ID_TIPOREINTEGRO = 3 THEN 'DI'ELSE 'EG' END,'" + fAcredit + "','AUTORIZACION DE REINTEGRO CONTABLE FOLIO ' + CAST(nFolioReintegro as varchar),formaDePago, clvRastreo,fichaDeposito, clvBanco,cuentaBancaria,lc,importeLC,clcSicop,folioDependencia,mvto,aviso,tipoAviso,causaAviso FROM tReintegroEncabezado WITH (NOLOCK) WHERE nFolioReintegro = ?";
            log.debug("Object: {}", sSQL);
            pstmnt = conn.prepareStatement(sSQL);
            pstmnt.setInt(1, nIdCaso);
            log.debug("Object: {}", "idCaso: " + nIdCaso);
            pstmnt.execute();
            String sSQL2 = "SELECT tipoAviso FROM tReintegroEncabezado WITH (NOLOCK) WHERE nFolioReintegro = ?";
            pstmnt = conn.prepareStatement(sSQL2);
            log.debug("Object: {}", sSQL2);
            pstmnt.setInt(1, nIdCaso);
            log.debug("Object: {}", "idCaso: " + nIdCaso);
            rs = pstmnt.executeQuery();
            String tipoRein = "";
            if (rs.next()) {
                tipoRein = rs.getString(1);
            }
            /*
			pstmnt = conn.prepareStatement("SELECT CASE WHEN COUNT(*) <> 0 THEN 'SI' ELSE 'NO' END AS compRG "  
											+" FROM tRelacionGastosCompromisoDetalle "
											+" WHERE nFolioRelacionGastos IN (SELECT nFolioPAGO "
											+"								FROM dbo.tPagadoEncabezado "
											+"								WHERE caNoContrarrecibo IN (SELECT cxp "
											+"															FROM dbo.tReintegroDetalle "
											+"															WHERE nFolioReintegro = ?))");
			pstmnt.setInt(1, nIdCaso);
			rs = pstmnt.executeQuery();
			String tipoRG = "";*/
            /*SI la relacion de gastos esta en una integrada que genero compromiso Reintegra al compromiso*/
            /*NO la relacion de gastos esta en una integrada menor a 24,000 Reintegra al disponible*/
            /*if (rs.next()) {
			tipoRG = rs.getString(1);
			}*/
            String query = "INSERT INTO tReintegroAutDetalle(nFolioReintegroaut,nDocRenglon,noCLC,secCLC,EP,mImporteCLC,cMes,movto,cEvento, mImporte, mImporteNegativo, cCentroContable, nCapitulo,cxp,RFC,nFolioDependencia,ALM,nRenglonPagado,obgt,ctab,nidprograma,cSubPrograma,FFM,mAmortizacionAnticipo,mPasivoDiferido,cPasivo,mImportePC) " + " SELECT rd.nfolioreintegro, " + "       rd.ndocrenglon, " + "       rd.noclc, " + "       rd.secclc, " + "       rd.ep, " + "       rd.mimporteclc, " + "       rd.cmes, " + "       rd.movto, " + "       (SELECT CASE  " + "                 WHEN ctipopago = 'PAGOOBRA' THEN CASE WHEN SUBSTRING(EP,40,1) = '4' THEN 'RNGIP' ELSE 'RNC' END " + "                 WHEN ctipopago = 'PAGODIVERSO' THEN CASE WHEN SUBSTRING(EP,40,1) = '4' THEN 'RNGIP' ELSE 'RNC' END " + "                 WHEN ctipopago = 'PAGODIRECTO' THEN cat_re.cPREFIJO_EVENTO " + "                 WHEN ctipopago = 'RELACIONGASTOS' THEN cat_re.cPREFIJO_EVENTO " + "                 WHEN ctipopago = 'NOMINA' THEN cat_re.cPREFIJO_EVENTO " + "                 WHEN ctipopago = 'AJENAS' THEN CASE WHEN SUBSTRING(EP,40,1) = '4' THEN 'RNGIP' ELSE 'RN' END " + "                 WHEN ctipopago = 'FEDERALIZADO' THEN CASE WHEN SUBSTRING(EP,40,1) = '4' THEN 'RNGIP' ELSE 'RNC' END " + "                 ELSE '' " + "               END AS dTipoPago " + "        FROM   tpagadoencabezado WITH(nolock) " + "        WHERE  canocontrarrecibo = rd.cxp)" + "         " + "       + Substring( (SELECT DISTINCT cevento FROM tPagadoDetalle ed WITH(NOLOCK) WHERE " + "       ed.ep=rd.ep " + "       AND ed.nfoliopago=(SELECT nfoliopago FROM tpagadoencabezado WITH(NOLOCK) WHERE " + "       canocontrarrecibo=rd.cxp) AND ed.ctipopago=(SELECT ctipopago FROM " + "       tpagadoencabezado WITH(NOLOCK) WHERE canocontrarrecibo=rd.cxp)) , 2, " + "       " + "       Len((SELECT " + "       DISTINCT " + "       cevento FROM tPagadoDetalle ed WITH(NOLOCK) WHERE ed.ep=rd.ep AND " + "       ed.nfoliopago=(SELECT " + "       nfoliopago FROM tpagadoencabezado WITH(NOLOCK) WHERE canocontrarrecibo=rd.cxp) AND " + "       ed.ctipopago=(SELECT ctipopago FROM tpagadoencabezado WITH(NOLOCK) WHERE " + "       canocontrarrecibo=rd.cxp)))), " + "       rd.mimporte, " + "       rd.mimportenegativo, " + "       rd.ccentrocontable, " + "       rd.ncapitulo, " + "       rd.cxp, " + "       rd.rfc, " + "       rd.nfoliodependencia, " + "       (SELECT TOP(1) alm " + "        FROM   tpagadodetalle WITH(nolock), " + "               tpagadoencabezado WITH(nolock) " + "        WHERE  ep = rd.ep " + "               AND nfoliosiaff = rd.noclc " + "               AND tpagadodetalle.nfoliopagado = tpagadoencabezado.nfoliopagado " + "               AND tpagadoencabezado.canocontrarrecibo = rd.cxp) AS ALM, " + "       rd.nrenglonpagado," + "       rd.obgt, " + "		 rd.ctab, " + "       nidprograma, " + "       cSubPrograma, " + "		 rd.OBGT + CASE WHEN RIGHT('00' + LTRIM(RTRIM(nidprograma)),2) = '13' THEN '0000' ELSE '" + year + "' END + RIGHT('00' + LTRIM(RTRIM(nidprograma)),2) + RIGHT('00' + LTRIM(RTRIM(cSubPrograma)),2) + '00' AS ffm, 0 AS mAmortizacionAnticipo, " + " 		 rd.mPasivoDiferido*-1," + " 		 rd.cPasivo, " + " 		 rd.mImportePC " + "FROM   treintegrodetalle rd WITH (nolock) " + "INNER JOIN tReintegroEncabezado re WITH(nolock) " + "ON re.nFolioReintegro = rd.nFolioReintegro " + "INNER JOIN tCatalogo_Reintegros cat_re WITH(nolock) " + "ON re.id_tiporeintegro = cat_re.id_reintegro " + "WHERE  rd.nfolioreintegro = ?";
            log.debug("Object: {}", query.toString());
            pstmnt = conn.prepareStatement(query);
            pstmnt.setInt(1, nIdCaso);
            log.debug("Object: {}", "idCaso: " + nIdCaso);
            pstmnt.execute();
            conn.commit();
        } catch (SQLException s) {
            conn.rollback();
            log.warn("Object: {}", s);
            s.printStackTrace();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            if (rs != null)
                rs.close();
            pstmnt = null;
            rs = null;
        }
    }

    public static ReintegroContEncabezado getReintegroEncabezadoNuevo(Connection conn, int folio) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        ReintegroContEncabezado re = null;
        pstm = conn.prepareStatement("SELECT * FROM tReintegroEncabezado with(nolock) WHERE nFolioReintegro = ?");
        log.debug("Object: {}", "Leyendo tReintegroEncabezado del Folio: " + folio);
        pstm.setInt(1, folio);
        res = pstm.executeQuery();
        if (res.next()) {
            re = new ReintegroContEncabezado();
            re.setfSolicitud(res.getString("fSolicitud"));
            re.setcTipoReintegro(res.getString("cTipoReintegro"));
            re.setfAplicacion(res.getString("fAplicacion"));
            re.setcRamo(res.getString("cRamo"));
            re.setcUnidadResponsable(res.getString("cUnidadResponsable"));
            re.setcUnidadResponsableContable(res.getString("cUnidadResponsableContable"));
            re.setcDocumentoHaplicado(res.getString("cDocumentoHaplicado"));
            re.setnFolioPoliza(res.getInt("nFolioPoliza"));
            re.setcTipoPoliza(res.getString("cTipoPoliza"));
            re.setaEjercicioFiscal(res.getString("aEjercicioFiscal"));
            re.setObservaciones(res.getString("observaciones"));
            re.setConcepto(res.getString("concepto"));
            re.setfCancelacion(res.getString("fCancelacion"));
            re.setfExpiracion(res.getString("fExpiracion"));
            re.setfAcreditacion(res.getString("fAcredit"));
            re.setFormaDePago(res.getString("formaDePago"));
            re.setClvRastreo(res.getString("clvRastreo"));
            re.setFichaDeposito(res.getString("fichaDeposito"));
            re.setClvBanco(res.getString("clvBanco"));
            re.setCuentaBancaria(res.getString("cuentaBancaria"));
            re.setLc(res.getString("lc"));
            DecimalFormat formatter = new DecimalFormat("###,###.##");
            re.setImporteLC(formatter.format(res.getDouble("importeLC")));
            re.setCausaAviso(res.getString("causaAviso"));
            re.setTipoAviso(res.getString("tipoAviso"));
            re.setMovimiento(res.getString("mvto"));
            re.setFolioDependencia(res.getString("folioDependencia"));
            re.setAviso(res.getString("aviso"));
        }
        if (pstm != null) {
            pstm.close();
        }
        if (res != null) {
            res.close();
        }
        return re;
    }

    public static ReintegroContDetalle getReintegroDetalleNuevo(Connection conn, int folio) throws SQLException {
        ResultSet res = null;
        PreparedStatement pstm = null;
        ReintegroContDetalle rd = null;
        try {
            pstm = conn.prepareStatement("SELECT TOP 1 * FROM tReintegroDetalle with(nolock) WHERE nFolioReintegro = ?");
            pstm.setInt(1, folio);
            res = pstm.executeQuery();
            if (res.next()) {
                rd = new ReintegroContDetalle();
                rd.setCtab(res.getString("CTAB"));
                rd.setRfc(res.getString("RFC"));
            }
            return rd;
        } finally {
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(res);
        }
    }

    public static String getCorreoRevisor(Connection conn, Caso c) throws SQLException {
        String to = "";
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String sSQL = "select top(1) u_email from CG_USUARIO where U_LOGIN in (select distinct(B_CO_RESPONSABLE_EJEC) from CG_BITACORA where B_C_FOLIO=? and B_ID_OPER=2)";
        log.debug("Object: {}", sSQL);
        pstmnt = conn.prepareStatement(sSQL);
        if (c != null && c.getFolio() != null)
            pstmnt.setString(1, c.getFolio());
        else
            //no encontrara nada, solo es para que corra el qry
            pstmnt.setString(1, "BXX");
        rs = pstmnt.executeQuery();
        boolean enviar = false;
        if (rs.next()) {
            to = (enviar ? ";" : "") + rs.getString(1);
            enviar = true;
        }
        return to;
    }

    public static String getListaCorreos(Connection conn, Caso c) throws SQLException {
        String to = "";
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String sSQL = "select distinct u_email from CG_USUARIO where U_LOGIN in (select distinct(B_CO_RESPONSABLE_EJEC) from CG_BITACORA where B_C_FOLIO=?)";
        pstmnt = conn.prepareStatement(sSQL);
        if (c != null && c.getFolio() != null)
            pstmnt.setString(1, c.getFolio());
        else
            //no encontrara nada, solo es para que corra el qry
            pstmnt.setString(1, "BXX");
        rs = pstmnt.executeQuery();
        boolean enviar = false;
        while (rs.next()) {
            to += (enviar ? ";" : "") + rs.getString(1);
            enviar = true;
        }
        return to;
    }

    public static void insertaReintegro(Connection conn, ReintegroContEncabezado reinE, ArrayList<ReintegroContDetalle> reinDetalles, int folio, String folioCompleto, Usuario usuario) throws Exception {
        PreparedStatement psInsertDetalle = null;
        PreparedStatement psInsertEncabezado = null;
        try {
            psInsertEncabezado = conn.prepareStatement("INSERT INTO tReintegroEncabezado(nFolioReintegro,fSolicitud,cTipoReintegro,cRamo,cUnidadResponsable,cDocumentoHaplicado,aEjercicioFiscal,observaciones,concepto,U_LOGIN,formaDePago,clvRastreo,fichaDeposito,clvBanco,cuentaBancaria,lc,importeLC,cTipoPoliza,fAplicacion,cUnidadResponsableContable,folioDependencia,mvto,aviso,tipoAviso,causaAviso,fAcredit,ID_TIPOREINTEGRO) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            //folio del reintegro
            psInsertEncabezado.setInt(1, folio);
            //fecha de hoy, pasar como parametro o recalcular
            psInsertEncabezado.setString(2, reinE.getfSolicitud());
            //cTipoReintegro dependiendo si es de compromiso, directa
            psInsertEncabezado.setString(3, reinE.getcTipoReintegro());
            //cRamo
            psInsertEncabezado.setString(4, reinE.getcRamo());
            //cUnidadResponsable
            psInsertEncabezado.setString(5, reinE.getcUnidadResponsable());
            psInsertEncabezado.setString(6, reinE.getcDocumentoHaplicado());
            //aEjercicioFiscal (2012,2013...)
            psInsertEncabezado.setString(7, reinE.getaEjercicioFiscal());
            //observaciones
            psInsertEncabezado.setString(8, reinE.getObservaciones());
            //concepto
            psInsertEncabezado.setString(9, reinE.getConcepto());
            psInsertEncabezado.setString(10, usuario.getLogin());
            //forma de pago (efectivo, transferencia) [catalogo]
            psInsertEncabezado.setString(11, reinE.getFormaDePago());
            //clave de rastreo
            psInsertEncabezado.setString(12, reinE.getClvRastreo());
            //ficha de deposito en el banco
            psInsertEncabezado.setString(13, reinE.getFichaDeposito());
            //clave del pago en el banco
            psInsertEncabezado.setString(14, reinE.getClvBanco());
            //cuenta bancaria
            psInsertEncabezado.setString(15, reinE.getCuentaBancaria());
            //linea de captura clave
            psInsertEncabezado.setString(16, reinE.getLc());
            //importe de la linea de captura
            psInsertEncabezado.setDouble(17, Double.parseDouble(reinE.getImporteLC()));
            //cTipoPoliza
            psInsertEncabezado.setString(18, reinE.getcTipoPoliza());
            psInsertEncabezado.setString(19, reinE.getfAplicacion());
            psInsertEncabezado.setString(20, reinE.getcUnidadResponsableContable());
            psInsertEncabezado.setString(21, reinE.getFolioDependencia());
            psInsertEncabezado.setString(22, reinE.getMovimiento());
            psInsertEncabezado.setString(23, reinE.getAviso());
            psInsertEncabezado.setString(24, reinE.getTipoAviso());
            psInsertEncabezado.setString(25, reinE.getCausaAviso());
            psInsertEncabezado.setString(26, reinE.getfAcreditacion());
            psInsertEncabezado.setInt(27, reinE.getnId_TipoReintegro());
            psInsertEncabezado.execute();
            log.debug("Object: {}", psInsertEncabezado.toString());
            psInsertDetalle = conn.prepareStatement("INSERT INTO tReintegroDetalle(nFolioReintegro,nDocRenglon,noCLC,secCLC,cEvento,EP,mImporteCLC,mImporte,mImporteNegativo,cMes,cCentroContable,nCapitulo,cxp,RFC,nRenglonPagado,nFolioDependencia,ALM,obgt,ctab)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            for (Iterator<ReintegroContDetalle> i = reinDetalles.iterator(); i.hasNext(); ) {
                ReintegroContDetalle rd = i.next();
                psInsertDetalle.setInt(1, reinE.getnFolioReintegro());
                psInsertDetalle.setInt(2, Integer.parseInt(rd.getnDocRenglon().replace(".0", "")));
                psInsertDetalle.setString(3, rd.getnSIAFF());
                psInsertDetalle.setString(4, rd.getSecCLC());
                psInsertDetalle.setString(5, rd.getcEvento());
                psInsertDetalle.setString(6, rd.getEP());
                psInsertDetalle.setDouble(7, rd.getmImporteCLC());
                psInsertDetalle.setDouble(8, rd.getmImporteCLC());
                psInsertDetalle.setDouble(9, rd.getmImporteCLC() * -1);
                psInsertDetalle.setInt(10, rd.getMes());
                psInsertDetalle.setString(11, rd.getcCentroContable());
                psInsertDetalle.setString(12, rd.getcPartida());
                psInsertDetalle.setString(13, rd.getCxp());
                psInsertDetalle.setString(14, rd.getRfc());
                psInsertDetalle.setString(15, rd.getRenglonPagado());
                psInsertDetalle.setString(16, rd.getFolioDependenciaSicop());
                psInsertDetalle.setString(17, rd.getAlm());
                psInsertDetalle.setString(18, rd.getObgt());
                psInsertDetalle.setString(19, rd.getCtab());
                psInsertDetalle.addBatch();
                log.debug("Object: {}", psInsertDetalle.toString());
            }
            psInsertDetalle.executeBatch();
            conn.commit();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            e.printStackTrace();
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn(e2.getMessage(), e2);
                    e2.printStackTrace();
                }
            throw e;
        } finally {
            CloseObject.closeObject(psInsertDetalle, false);
        }
    }

    public static String folioDependencia(Connection conn, String caNoContrarrecibo, String EP, int folio) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String retval = "";
        try {
            pstmnt = conn.prepareStatement("SELECT NCLC_43 FROM vReintegrosCLCSICOP with(nolock) WHERE FolioSIAFF=? and EP=? ");
            pstmnt.setString(1, caNoContrarrecibo);
            pstmnt.setString(2, EP);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                retval = rs.getString(1);
            }
        } catch (SQLException s) {
            log.warn("Object: {}", s);
            s.printStackTrace();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static String getRFC(Connection conn, String caNoContrarrecibo, String ep) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String retval = "";
        try {
            String queryRFC = "SELECT top(1) ed.RFC FROM tEjercidoDetalle ed with(nolock), tEjercidoEncabezado ee with(NOLOCK) WHERE ee.caNoContrarrecibo = ? AND ee.nFolioEjercido = ed.nFolioEjercido and ed.EP=?";
            pstmnt = conn.prepareStatement(queryRFC);
            pstmnt.setString(1, caNoContrarrecibo);
            pstmnt.setString(2, ep);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                retval = rs.getString(1);
            }
        } catch (SQLException s) {
            log.warn("Object: {}", s);
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
            //pstmnt = conn.prepareStatement("SELECT nDocRenglon FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=? and nDocRenglon not in(SELECT nRenglonPagado FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=?) order by pd.mImporteNeto");
            pstmnt = conn.prepareStatement("SELECT nDocRenglon FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=?  order by pd.mImporteNeto");
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
            log.warn("Object: {}", s);
            s.printStackTrace();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static String getcPartida(Connection conn, String EP) throws SQLException {
        String cPartida = "";
        ResultSet rs = null;
        PreparedStatement pstm = null;
        pstm = conn.prepareStatement("select cPartida from tCatalogoEP with (nolock) where EP=?");
        pstm.setString(1, EP);
        rs = pstm.executeQuery();
        if (rs.next()) {
            cPartida = rs.getString(1);
        }
        if (pstm != null) {
            pstm.close();
        }
        if (rs != null) {
            rs.close();
        }
        String res = "";
        if (cPartida != null && !cPartida.equals("")) {
            res = cPartida.substring(0, 1);
            for (int i = 1; i <= 4; i++) res += "0";
        } else {
            res = "La EP " + EP + " no existe en el catalogo";
        }
        return res;
    }

    public static int secCLC(Connection conn, String caNoContrarrecibo, String EP, int folio) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        int retval = 0;
        try {
            pstmnt = conn.prepareStatement("SELECT SEC FROM vReintegrosCLCSICOP with(nolock) WHERE FolioSIAFF=? and EP=? and SEC not in (SELECT secCLC FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=? and noCLC=? and EP=?)");
            pstmnt.setString(1, caNoContrarrecibo);
            pstmnt.setString(2, EP);
            pstmnt.setInt(3, folio);
            pstmnt.setString(4, caNoContrarrecibo);
            pstmnt.setString(5, EP);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                retval = rs.getInt(1);
            } else {
                retval = -1;
            }
        } catch (SQLException s) {
            log.warn("Object: {}", s);
            s.printStackTrace();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static String getALM(Connection conn, String caNoContrarrecibo, String EP, int cMes, int folio, int renglon) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String retval = "";
        try {
            //pstmnt = conn.prepareStatement("SELECT nDocRenglon FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=? and nDocRenglon not in(SELECT nRenglonPagado FROM tReintegroDetallePaso with(nolock) WHERE nFolioReintegro=?) order by pd.mImporteNeto");
            pstmnt = conn.prepareStatement("SELECT ALM FROM tPagadoDetalle pd with(nolock), tPagadoEncabezado pe with(nolock) WHERE caNoContrarrecibo=? and pd.nFolioPagado=pe.nFolioPagado and EP=? and nMes=? and nDocRenglon=?");
            pstmnt.setString(1, caNoContrarrecibo);
            pstmnt.setString(2, EP);
            pstmnt.setInt(3, cMes);
            pstmnt.setInt(4, renglon);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                retval = rs.getString(1);
            }
        } catch (SQLException s) {
            log.warn("Object: {}", s);
            s.printStackTrace();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static boolean actualizaCtaBancaria(Connection conn, int folio, String ctab) throws SQLException, ParseException {
        PreparedStatement pstmntUp = null;
        PreparedStatement pstmntUp2 = null;
        boolean retval = false;
        boolean retval2 = false;
        try {
            pstmntUp = conn.prepareStatement("UPDATE tReintegroEncabezado SET cuentaBancaria=? WHERE nFolioReintegro=?");
            pstmntUp.setString(1, ctab);
            pstmntUp.setInt(2, folio);
            retval = pstmntUp.execute();
            if (ctab != null && ctab != "") {
                pstmntUp2 = conn.prepareStatement("UPDATE dbo.tReintegroDetalle SET CTAB = ? WHERE nFolioReintegro = ?");
                pstmntUp2.setString(1, ctab);
                pstmntUp2.setInt(2, folio);
                retval2 = pstmntUp2.execute();
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            if (pstmntUp != null)
                pstmntUp.close();
            pstmntUp = null;
        }
        if (!retval || !retval2)
            return false;
        else
            return true;
    }

    public static boolean validaSIAmortiza(Connection conn, int folio) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String amortizacion = null;
        boolean esAmortizacion = false;
        try {
            ps = conn.prepareStatement("SELECT CASE WHEN SUM(DET.mAmortizacionAnticipo) > 0 THEN 'S' ELSE 'N' END AS amortizacion \r\n" + " FROM tPAGODIVERSOEncabezado ENC \r\n" + " JOIN tPAGODIVERSODetalle DET ON ENC.nFolioPAGODIVERSO = DET.nFolioPAGODIVERSO\r\n" + " JOIN tReintegroDetalle REINT ON ENC.caNoContrarrecibo = cxp\r\n" + " WHERE nFolioReintegro = ? ");
            ps.setInt(1, folio);
            rs = ps.executeQuery();
            if (rs.next()) {
                amortizacion = rs.getString(1);
            }
            if (amortizacion.equals("S"))
                esAmortizacion = true;
        } catch (SQLException e) {
            log.warn(e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (ps != null)
                ps.close();
            ps = null;
        }
        return esAmortizacion;
    }

    public static boolean actualizaAmortizacion(Connection conn, int folio) throws SQLException {
        PreparedStatement pstmntUp = null;
        boolean retval = false;
        try {
            pstmntUp = conn.prepareStatement("UPDATE REINT \r\n" + "	SET REINT.mAmortizacionAnticipo = DET.mAmortizacionAnticipo \r\n" + "FROM tPAGODIVERSOEncabezado ENC \r\n" + "	JOIN tPAGODIVERSODetalle DET ON ENC.nFolioPAGODIVERSO = DET.nFolioPAGODIVERSO\r\n" + "	JOIN tReintegroAutDetalle REINT ON ENC.caNoContrarrecibo = cxp\r\n" + "WHERE nFolioReintegroaut = ? AND DET.nDocRenglon = REINT.nDocRenglon");
            pstmntUp.setInt(1, folio);
            retval = pstmntUp.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pstmntUp != null)
                pstmntUp.close();
            pstmntUp = null;
        }
        return retval;
    }
}
