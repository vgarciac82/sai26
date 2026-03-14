package com.axtel.contabilidad.reintegrosCaja;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import com.axtel.contabilidad.reintegrosCaja.core.ReintegrosCajaDetalle;
import com.axtel.contabilidad.reintegrosCaja.core.ReintegrosCajaEncabezado;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.core.impl.EgresoREINTEGROCAJAEncabezado;
import com.syc.gestion.core.Caso;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.contabilidad.utils.db.RSToTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ana
 */
public class ReintegrosCajaManager {

    static String ur;

    private static final Logger log = LoggerFactory.getLogger(ReintegrosCajaManager.class);

    public static ReintegrosCajaEncabezado getReintegrosCajaEncabezado(Connection conn, int folio) throws Exception {
        ResultSet rs = null;
        PreparedStatement pstm = null;
        ReintegrosCajaEncabezado rce = null;
        pstm = conn.prepareStatement("SELECT * FROM tReintegroCajaEncabezado WITH(NOLOCK) WHERE nFolioReintegrocaja = ?");
        try {
            pstm.setInt(1, folio);
            rs = pstm.executeQuery();
            if (rs.next()) {
                rce = new ReintegrosCajaEncabezado();
                rce.setnFolioReintegrocaja(Integer.parseInt(rs.getString("nFolioReintegrocaja")));
                rce.setfCreacion(rs.getString("fCreacion"));
                rce.setfAplicacion(rs.getString("fAplicacion"));
                rce.setcMes(rs.getInt("cMes"));
                rce.setcTipoPoliza(rs.getString("cTipoPoliza"));
                rce.setnFolioPoliza(rs.getInt("nFolioPoliza"));
                rce.setcDescripcionPoliza(rs.getString("cDescripcionPoliza"));
                rce.setU_LOGIN(rs.getString("U_LOGIN"));
                rce.setcDocumentohAplicado(rs.getString("cDocumentohAplicado"));
                rce.setcMotivoRechazo(rs.getString("cMotivoRechazo"));
                rce.setnFolioPolizaCancelacion(rs.getInt("nFolioPolizaCancelacion"));
                rce.setfCancelacion(rs.getString("fCancelacion"));
                rce.setaEjercicioFiscal(rs.getString("aEjercicioFiscal"));
                rce.setcRamo(rs.getString("cRamo"));
                rce.setcUnidadEjecutora(rs.getString("cUnidadEjecutora"));
                rce.setcIdUsuarioCaptura(rs.getString("cIdUsuarioCaptura"));
                rce.setID_CASO(rs.getInt("ID_CASO"));
                rce.setmMontoSolicitud(rs.getDouble("mMontoSolicitud"));
                rce.setcEsFirmaElectronica(rs.getString("cEsFirmaElectronica"));
                rce.setnNumEmpleadoVoBo(rs.getInt("nNumEmpleadoVoBo"));
                rce.setnNumEmpleadoAut(rs.getInt("nNumEmpleadoAut"));
                rce.setnNumEmpleadoElab(rs.getInt("nNumEmpleadoElab"));
                rce.setnEnviadoSICOP(rs.getInt("nEnviadoSICOP"));
            }
        } catch (Exception e) {
            throw e;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pstm, false);
        }
        return rce;
    }

    public static ReintegrosCajaDetalle getReintegrosCajaDetalle(Connection conn, int folio) throws Exception {
        ResultSet rs = null;
        PreparedStatement pstm = null;
        ReintegrosCajaDetalle rcd = null;
        pstm = conn.prepareStatement("SELECT DET.*\r\n" + ", BEN.dNombre + ' ' + dApellidoPaterno + ' ' + dApellidoMaterno AS dNombre\r\n" + ", ISNULL(dBanco,'N/A') + ' ' + ISNULL(dCuentaBancaria,'') AS ctaBeneficiario\r\n" + ", cDescripcion AS dFFM\r\n" + ", strNombreBeneficiario AS nomCTAB\r\n" + ", cUnidadEjecutora + ' ' + D_DESCRIPCION AS cUnidadEjecutora\r\n" + "FROM tReintegroCajaDetalle AS DET WITH(NOLOCK) \r\n" + "LEFT JOIN tBeneficiario AS BEN WITH(NOLOCK) ON DET.RFC = BEN.dRFC\r\n" + "LEFT JOIN tBeneficiarioCuentasBancarias AS CTAB WITH (NOLOCK) ON DET.RFC = CTAB.dRFC AND nCuentaBeneficiario = dCuentaBancaria\r\n" + "LEFT JOIN tSubcuentasFFM AS FFM WITH (NOLOCK) ON DET.FFM = FFM.cSubcuenta\r\n" + "LEFT JOIN (SELECT * FROM tUECuentasBancarias WITH (NOLOCK)\r\n" + "UNION\r\n" + "SELECT * FROM tUECuentasBancariasFFM ) AS UECTA\r\n" + "ON DET.CTAB = UECTA.strClabe AND DET.cUnidadResponsable = UECTA.strUnidadEjecutora									\r\n" + "LEFT JOIN tCatUnidadEjecutora AS UE WITH (NOLOCK) ON DET.cUnidadResponsable = UE.cUnidadEjecutora\r\n" + "WHERE nFolioReintegrocaja = ? ");
        try {
            pstm.setInt(1, folio);
            rs = pstm.executeQuery();
            if (rs.next()) {
                rcd = new ReintegrosCajaDetalle();
                rcd.setnDocRenglon(rs.getInt("nDocRenglon"));
                rcd.setcEvento(rs.getString("cEvento"));
                rcd.setcEventoDestino(rs.getString("cEventoDestino"));
                rcd.setmImporte(rs.getDouble("mImporte"));
                rcd.setmImporteNegativo(rs.getDouble("mImporteNegativo"));
                rcd.setALM(rs.getString("ALM"));
                rcd.setCTAB(rs.getString("CTAB"));
                rcd.setOBGT(rs.getString("OBGT"));
                rcd.setRFC(rs.getString("RFC"));
                rcd.setEP(rs.getString("EP"));
                rcd.setnCuentaBeneficiario(rs.getString("nCuentaBeneficiario"));
                rcd.setFFM(rs.getString("FFM"));
                rcd.setcCentroContable(rs.getString("cCentroContable"));
                rcd.setcUnidadResponsable(rs.getString("cUnidadEjecutora"));
                rcd.setdNombre(rs.getString("dNombre"));
                rcd.setctaBeneficiario(rs.getString("ctaBeneficiario"));
                rcd.setdFFM(rs.getString("dFFM"));
                rcd.setnomCTAB(rs.getString("nomCTAB"));
            }
        } catch (Exception e) {
            throw e;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pstm, false);
        }
        return rcd;
    }

    public static String existenDatos(Connection conn, int nFolioReintegrocaja) throws Exception {
        ResultSet rs = null;
        PreparedStatement pstmnt = null;
        String mensaje = "NO";
        try {
            pstmnt = conn.prepareStatement("SELECT nFolioReintegrocaja FROM tReintegroCajaEncabezado WITH(NOLOCK) WHERE nFolioReintegrocaja = ?");
            pstmnt.setInt(1, nFolioReintegrocaja);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                mensaje = "SI";
            }
        } catch (Exception e) {
            throw e;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pstmnt, false);
        }
        return mensaje;
    }

    public static String existenDatosAut(Connection conn, int nFolioReinegroCaja) throws Exception {
        ResultSet rs = null;
        PreparedStatement pstmnt = null;
        String mensaje = "NO";
        try {
            pstmnt = conn.prepareStatement("SELECT nFolioReintegroCajaAut FROM tReintegroCajaAutEncabezado WITH(NOLOCK) WHERE nFolioReintegroCajaAut = ?");
            pstmnt.setInt(1, nFolioReinegroCaja);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                mensaje = "SI";
            }
        } catch (Exception e) {
            throw e;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pstmnt, false);
        }
        return mensaje;
    }

    public static String getTipoPolizaEvento(Connection conn, String cEvento) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String tipoPoliza = null;
        String[] componenteEvento = cEvento.split("_");
        int grupo = Integer.parseInt(componenteEvento[0]);
        int subGrupo = Integer.parseInt(componenteEvento[1]);
        int evento = Integer.parseInt(componenteEvento[2]);
        String query = " SELECT DISTINCT cTipoPoliza " + " FROM tEventoManual WITH(NOLOCK)" + " WHERE cIdGrupoEvento = ? " + "		AND cIdSubGrupoEvento = ? " + "		AND cIdEventoManual = ? ";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, grupo);
            ps.setInt(2, subGrupo);
            ps.setInt(3, evento);
            rs = ps.executeQuery();
            if (rs.next()) {
                tipoPoliza = rs.getString("cTipoPoliza");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return tipoPoliza;
    }

    public static String validaMes(Connection conn, Caso c, String fApl) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String queryCC = "SELECT TOP 1 cUnidadResponsable FROM tReintegroCajaDetalle WITH( NOLOCK ) WHERE nFolioReintegrocaja = ?";
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

    public static void insertarEncabezado(Connection conn, ReintegrosCajaEncabezado encabezado) throws Exception {
        PreparedStatement pstmnt = null;
        String insertQuery = null;
        try {
            insertQuery = "INSERT INTO tReintegroCajaEncabezado " + "(nFolioReintegrocaja,\r\n" + "fCreacion,\r\n" + "fAplicacion,\r\n" + "cMes,\r\n" + "cTipoPoliza,\r\n" + "nFolioPoliza,\r\n" + "cDescripcionPoliza,\r\n" + "U_LOGIN,\r\n" + "cDocumentohAplicado,\r\n" + "cMotivoRechazo,\r\n" + "cUnidadResponsableContable,\r\n" + "nFolioPolizaCancelacion,\r\n" + "fCancelacion,\r\n" + "aEjercicioFiscal,\r\n" + "cRamo,\r\n" + "cUnidadEjecutora,\r\n" + "nNumEmpleadoElab,\r\n" + "nEnviadoSICOP\r\n," + "cIdUsuarioCaptura,\r\n" + "ID_CASO,\r\n" + "mMontoSolicitud\r\n" + ") " + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmnt = conn.prepareStatement(insertQuery);
            // introducir parametros para condiciones
            pstmnt.setInt(1, encabezado.getnFolioReintegrocaja());
            pstmnt.setString(2, encabezado.getfCreacion());
            pstmnt.setString(3, encabezado.getfAplicacion());
            pstmnt.setInt(4, encabezado.getcMes());
            pstmnt.setString(5, encabezado.getcTipoPoliza());
            pstmnt.setInt(6, encabezado.getnFolioPoliza());
            pstmnt.setString(7, encabezado.getcDescripcionPoliza());
            pstmnt.setString(8, encabezado.getU_LOGIN());
            pstmnt.setString(9, encabezado.getcDocumentohAplicado());
            pstmnt.setString(10, encabezado.getcMotivoRechazo());
            pstmnt.setString(11, encabezado.getcUnidadResponsableContable());
            pstmnt.setInt(12, encabezado.getnFolioPolizaCancelacion());
            pstmnt.setString(13, encabezado.getfCancelacion());
            pstmnt.setString(14, encabezado.getaEjercicioFiscal());
            pstmnt.setString(15, encabezado.getcRamo());
            pstmnt.setString(16, encabezado.getcUnidadEjecutora());
            pstmnt.setInt(17, encabezado.getnNumEmpleadoElab());
            pstmnt.setInt(18, encabezado.getnEnviadoSICOP());
            pstmnt.setString(19, encabezado.getcIdUsuarioCaptura());
            pstmnt.setInt(20, encabezado.getID_CASO());
            pstmnt.setDouble(21, encabezado.getmMontoSolicitud());
            log.debug("Object: {}", insertQuery);
            pstmnt.execute();
        } catch (Exception e) {
            throw e;
        } finally {
            CloseObject.closeObject(pstmnt, false);
        }
    }

    public static void insertarDetalle(Connection conn, ReintegrosCajaDetalle detalle) throws Exception {
        PreparedStatement pstmnt = null;
        try {
            String insertQuery = "INSERT INTO tReintegroCajaDetalle " + "( nFolioReintegroCaja,\r\n" + "nDocRenglon,\r\n" + "cEvento,\r\n" + "cEventoDestino,\r\n" + "mImporte,\r\n" + "mImporteNegativo,\r\n" + "ALM,\r\n" + "CTAB,\r\n" + "OBGT,\r\n" + "RFC,\r\n" + "EP,\r\n" + "nCuentaBeneficiario,\r\n" + "FFM,\r\n" + "cCentroContable,\r\n" + "cUnidadResponsable\r\n" + " ) " + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmnt = conn.prepareStatement(insertQuery);
            pstmnt.setInt(1, detalle.getnFolioReintegroCaja());
            pstmnt.setInt(2, detalle.getnDocRenglon());
            pstmnt.setString(3, detalle.getcEvento());
            pstmnt.setString(4, detalle.getcEventoDestino());
            pstmnt.setDouble(5, detalle.getmImporte());
            pstmnt.setDouble(6, detalle.getmImporteNegativo());
            pstmnt.setString(7, detalle.getALM());
            pstmnt.setString(8, detalle.getCTAB());
            pstmnt.setString(9, detalle.getOBGT());
            pstmnt.setString(10, detalle.getRFC());
            pstmnt.setString(11, detalle.getEP());
            pstmnt.setString(12, detalle.getnCuentaBeneficiario());
            pstmnt.setString(13, detalle.getFFM());
            pstmnt.setString(14, detalle.getcCentroContable());
            pstmnt.setString(15, detalle.getcUnidadResponsable());
            log.debug("Object: {}", insertQuery);
            pstmnt.execute();
        } catch (Exception e) {
            throw e;
        } finally {
            CloseObject.closeObject(pstmnt, false);
        }
    }

    public static void insertarEncabezadoAut(Connection conn, String tipoPoliza, HttpServletRequest request) throws Exception {
        PreparedStatement pstmnt = null;
        String insertQuery = "";
        String cFechaAplicacion = request.getParameter("fechaAplicacion");
        String login = request.getParameter("u_login");
        int cMes = Integer.parseInt(cFechaAplicacion.substring(3, 5));
        int nFolioReinegroCaja = new Integer(request.getParameter("nFolioReintegrocaja")).intValue();
        try {
            insertQuery = "INSERT INTO tReintegroCajaAutEncabezado ( nFolioReintegroCajaAut, fCreacion, fAplicacion, cMes, cTipoPoliza, nFolioPoliza, cDescripcionPoliza, U_LOGIN, cDocumentohAplicado, cMotivoRechazo, cUnidadResponsableContable, nFolioPolizaCancelacion, fCancelacion, aEjercicioFiscal, cRamo, cUnidadEjecutora, mMontoSolicitud )\r\n" + "SELECT ?, fCreacion, ?, ?, ?, 0, cDescripcionPoliza, ?, NULL, NULL, cUnidadResponsableContable, 0, NULL, aEjercicioFiscal, cRamo, cUnidadEjecutora, mMontoSolicitud\r\n " + " FROM tReintegroCajaEncabezado WITH(NOLOCK)" + " WHERE nFolioReintegrocaja = ? ";
            pstmnt = conn.prepareStatement(insertQuery);
            pstmnt.setInt(1, nFolioReinegroCaja);
            pstmnt.setString(2, cFechaAplicacion);
            pstmnt.setInt(3, cMes);
            pstmnt.setString(4, tipoPoliza);
            pstmnt.setString(5, login);
            pstmnt.setInt(6, nFolioReinegroCaja);
            log.debug("Object: {}", insertQuery);
            pstmnt.execute();
        } catch (Exception e) {
            throw e;
        } finally {
            CloseObject.closeObject(pstmnt, false);
        }
    }

    public static void insertarDetalleAut(Connection conn, HttpServletRequest request) throws Exception {
        PreparedStatement pstmnt = null;
        String insertQuery = "";
        int nFolioReinegroCaja = new Integer(request.getParameter("nFolioReintegrocaja")).intValue();
        try {
            insertQuery = "INSERT INTO tReintegroCajaAutDetalle ( nFolioReintegroCajaAut, nDocRenglon, cEvento, mImporte, mImporteNegativo, ALM, CTAB, OBGT, RFC, EP, nCuentaBeneficiario, FFM, cCentroContable, cUnidadResponsable )\r\n" + " SELECT nFolioReintegroCaja, nDocRenglon, cEventoDestino, mImporte, mImporteNegativo, ALM, CTAB, OBGT, RFC, EP, nCuentaBeneficiario, FFM, cCentroContable, cUnidadResponsable \r\n" + " FROM tReintegroCajaDetalle WITH(NOLOCK)\r\n" + " WHERE nFolioReintegroCaja = ? ";
            pstmnt = conn.prepareStatement(insertQuery);
            pstmnt.setInt(1, nFolioReinegroCaja);
            log.debug("Object: {}", insertQuery);
            pstmnt.execute();
        } catch (Exception e) {
            throw e;
        } finally {
            CloseObject.closeObject(pstmnt, false);
        }
    }

    public static EgresoEncabezado cargEncabezado(Connection conn, int folioEgreso) throws Exception {
        //LO LLAMA EgresoREINTEGROCAJAEncabezado
        ResultSet rs = null;
        PreparedStatement ps = null;
        StringBuilder query = new StringBuilder();
        query.append("SELECT nFolioReintegrocaja AS folioPago,");
        query.append("	fCreacion,");
        query.append("	fAplicacion,");
        query.append("	cMes,");
        query.append("	cTipoPoliza,");
        query.append("	nFolioPoliza,");
        query.append("	cDescripcionPoliza,");
        query.append("	U_LOGIN,");
        query.append("	cDocumentohAplicado,");
        query.append("	cMotivoRechazo,");
        query.append("	cUnidadResponsableContable,");
        query.append("	nFolioPolizaCancelacion,");
        query.append("	fCancelacion,");
        query.append("	aEjercicioFiscal,");
        query.append("	cRamo,");
        query.append("	cUnidadEjecutora,");
        query.append("	cIdUsuarioCaptura,");
        query.append("	ID_CASO,");
        query.append("	mMontoSolicitud,");
        query.append("	cEsFirmaElectronica,");
        query.append("	nNumEmpleadoVoBo,");
        query.append("	nNumEmpleadoAut,");
        query.append("	nNumEmpleadoElab,");
        query.append("	nEnviadoSICOP,");
        query.append("	'REINTEGROCAJA' AS tipoPago ");
        query.append("FROM tReintegroCajaEncabezado ");
        query.append("WHERE nFolioReintegrocaja = ? ");
        try {
            EgresoREINTEGROCAJAEncabezado encabezado = new EgresoREINTEGROCAJAEncabezado();
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, folioEgreso);
            rs = ps.executeQuery();
            Map<String, String> resultObj = RSToTable.rsToMapCaseSensitive(rs);
            // Se convierte el formato de fecha
            DateConverter converter = new DateConverter(null);
            converter.setPatterns(new String[] { "dd/mm/yyyy", "yyyy-MM-dd" });
            ConvertUtils.register(converter, Date.class);
            BeanUtils.populate(encabezado, resultObj);
            return encabezado;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static void MotivoRechazoSolicitudFirmaElectronica(Connection conn, String folio, String cancelReason) throws Exception {
        PreparedStatement ps = null;
        String query = "UPDATE tReintegroCajaEncabezado SET cMotivoRechazo = ? where nFolioReintegroCaja = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, cancelReason);
            ps.setInt(2, Integer.parseInt(folio));
            ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static String[] PolizaCancelacion(Connection conn, String nFolioReintegroCaja, String Encabezado, String Detalle, String Folio) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String[] datos = new String[3];
        try {
            ps = conn.prepareStatement("SELECT nFolioPolizaCancelacion, cTipoPoliza, fCancelacion FROM " + Encabezado + " WITH (NOLOCK) WHERE " + Folio + " = ?");
            ps.setString(1, nFolioReintegroCaja);
            rs = ps.executeQuery();
            while (rs.next()) {
                datos[0] = rs.getString(1);
                datos[1] = rs.getString(2);
                datos[2] = rs.getString(3);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
        return datos;
    }

    public static String readfCancelacion(Connection conn, String nFolioReintegroCaja, String Encabezado, String Folio) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String fcancelacion = "";
        String query = "SELECT fCancelacion FROM " + Encabezado + " WITH(NOLOCK) WHERE " + Folio + " = ?";
        log.trace("Object: {}", "Select[" + query + "]");
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(nFolioReintegroCaja));
            rs = ps.executeQuery();
            while (rs.next()) {
                fcancelacion = rs.getString(1);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
        return fcancelacion;
    }

    public static void validaGabineteCaso(Connection conn, Caso c) throws Exception {
        if (c.getIdGabinete() <= 0) {
            PreparedStatement psUpdate = null;
            PreparedStatement psSel = null;
            ResultSet rsIDGabinete = null;
            StringBuilder queryUpdate = new StringBuilder();
            queryUpdate.append("UPDATE cg_caso SET c_id_gabinete = ? WHERE c_folio = ?");
            StringBuilder queryConsultaGabinete = new StringBuilder();
            queryConsultaGabinete.append("SELECT id_gabinete FROM imxoperajenas WHERE folio = ?");
            try {
                psSel = conn.prepareStatement(queryConsultaGabinete.toString());
                psSel.setString(1, c.getFolio());
                rsIDGabinete = psSel.executeQuery();
                if (rsIDGabinete.next()) {
                    int idGabiente = rsIDGabinete.getInt(1);
                    if (idGabiente < 0)
                        throw new Exception("No se encontro gabinete para el folio: " + c.getFolio() + " por lo que no puede continuar el proceso.");
                    psUpdate = conn.prepareStatement(queryUpdate.toString());
                    psUpdate.setInt(1, idGabiente);
                    psUpdate.setString(2, c.getFolio());
                    psUpdate.executeUpdate();
                    c.setIdGabinete(idGabiente);
                } else
                    throw new Exception("No se encontro gabinete para el folio: " + c.getFolio() + " por lo que no puede continuar el proceso.");
            } finally {
                CloseObject.closeObject(rsIDGabinete);
                CloseObject.closeObject(psUpdate);
            }
        }
    }

    public static void actializaFecha(Connection conn, String folio, String cFechaAplicacion) throws Exception {
        PreparedStatement ps = null;
        String query = "UPDATE tReintegroCajaEncabezado SET fAplicacion = ? where nFolioReintegroCaja = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, cFechaAplicacion);
            ps.setInt(2, Integer.parseInt(folio));
            ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }
}
