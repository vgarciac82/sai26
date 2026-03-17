/**
 */
package com.syc.implementacion.tesoreria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.axtel.contabilidad.reintegrosCaja.ReintegrosCajaManager;
import com.lowagie.text.Rectangle;
import com.syc.contable.core.PagosDiversosManager;
import com.syc.contable.core.SolicitudPagoFirmaElectronica;
import com.syc.egresos.DocumentacionComprobatoriaManager;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.core.impl.EgresoPAGODIRECTOEncabezado;
import com.syc.egresos.core.impl.EgresoREINTEGROCAJAEncabezado;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.Documento;
import com.syc.gestion.TipoCasoInterface;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionServlet;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.firmaElectronica.core.FirmaElectronicaManager;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

/**
 * @author Propietario
 */
public class EgresosInterface implements TipoCasoInterface {

    public static final String SICOP = "SICOP";

    public static final Rectangle RECT_VOBO = new Rectangle(30, 19, 214, 118);

    public static final Rectangle RECT_AUT = new Rectangle(400, 19, 581, 118);

    public static final Rectangle RECT_VOBO_CAJA = new Rectangle(313, 382, 450, 526);

    public static final Rectangle RECT_AUT_CAJA = new Rectangle(462, 382, 564, 526);

    public static final String USER_PRM = "u";

    public static final String DOCUMENT_PRM = "d";

    public static final String FOLIO_PRM = "f";

    public static final String ORDEN = "o";

    public static final Rectangle RECT_VOBO_REINTEGROCAJA = new Rectangle(313, 382, 450, 526);

    public static final Rectangle RECT_AUT_REINTEGROCAJA = new Rectangle(462, 382, 564, 526);

    private static Logger log = LoggerFactory.getLogger(EgresosInterface.class);

    public static Map<String, Integer> TRAMITE_OPCION = new HashMap<String, Integer>();

    {
        TRAMITE_OPCION.put("TPAGODIRECTOENCABEZADO", 106);
        TRAMITE_OPCION.put("TRELACIONGASTOSENCABEZADO", 109);
        TRAMITE_OPCION.put("TPAGOOBRAENCABEZADO", 112);
        TRAMITE_OPCION.put("TPAGODIVERSOENCABEZADO", 113);
        TRAMITE_OPCION.put("TPAGOFEDERALIZADOENCABEZADO", 215);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.syc.gestion.TipoCasoInterface#buscaPorExpediente(com.syc.gestion.
	 * core.Caso, java.lang.String)
	 */
    public boolean buscaPorExpediente(Caso c, String u_login) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.syc.gestion.TipoCasoInterface#onIniciaCaso(java.sql.Connection,
	 * java.lang.String, com.syc.gestion.core.Caso)
	 */
    public void onIniciaCaso(Connection conn, String u_login, Caso c) throws SQLException {
        // TODO Auto-generated method stub
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.syc.gestion.TipoCasoInterface#onCreateExpediente(java.sql.Connection,
	 * java.lang.String, com.syc.gestion.core.Caso,
	 * com.syc.fortimax.core.Aplicacion)
	 */
    public void onCreateExpediente(Connection conn, String u_login, Caso c, Aplicacion app) throws SQLException {
        // TODO Auto-generated method stub
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.syc.gestion.TipoCasoInterface#onRecibeDocumento(java.sql.Connection,
	 * com.syc.gestion.core.Caso, com.syc.fortimax.core.Documento, boolean)
	 */
    public void onRecibeDocumento(Connection conn, Caso c, Documento d, boolean isSaveEvent) throws SQLException {
        // TODO Auto-generated method stub
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.syc.gestion.TipoCasoInterface#onEjecutaCaso(java.sql.Connection,
	 * java.lang.String, com.syc.gestion.core.Caso, int)
	 */
    public void onEjecutaCaso(Connection conn, String u_login, Caso c, int id_caso_oper) throws SQLException {
        // TODO Auto-generated method stub
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.syc.gestion.TipoCasoInterface#onAvanzaCaso(java.sql.Connection,
	 * java.lang.String, com.syc.gestion.core.Caso, int)
	 */
    public void onAvanzaCaso(Connection conn, String u_login, Caso c, int id_caso_oper) throws SQLException {
        Caso cActual = new Caso();
        cActual.setFolio(c.getFolio());
        c = CasoManager.select(conn, cActual);
        String tipoPago = c.getTipoCaso().getGavetaAsociada();
        int folioPago = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
        if ("PAGODIVERSO".equalsIgnoreCase(tipoPago)) {
            try {
                DocumentacionComprobatoriaManager.regeneraDocComprobatoria(conn, tipoPago, folioPago);
                if (PagosDiversosManager.esPagoTiendaDigital(conn, folioPago)) {
                    PagosDiversosManager.actualizaPagoTienda(conn, folioPago, true);
                }
            } catch (Exception e) {
                throw new SQLException(e);
            }
        } else if ("REINTEGROCAJA".equalsIgnoreCase(tipoPago)) {
            log.trace("Avanzando tramite de Reintegro Años Anteriores de Caja ");
            log.info("Object: {}", "onAvanzaCaso ---> u_login " + u_login);
            log.info("Object: {}", "onAvanzaCaso ---> c " + c);
            log.info("Object: {}", "onAvanzaCaso ---> id_caso_oper " + id_caso_oper);
            CasoOperacion operacion = c.getCasoOperacion(0);
            int nFolio = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
            log.info("Object: {}", "onAvanzaCaso ---> getIdOperacion " + operacion.getIdOperacion());
            log.info("Object: {}", "onAvanzaCaso ---> getOperacion().getIdOperacion() " + operacion.getOperacion().getIdOperacion());
            log.info("Object: {}", "onAvanzaCaso ---> getOperacion().getResponsable() " + operacion.getOperacion().getResponsable());
            log.info("Object: {}", "onAvanzaCaso ---> Tipo Pago: " + c.getTipoCaso().getGavetaAsociada());
            log.info("Object: {}", "onAvanzaCaso ---> Folio Pago: " + nFolio);
            /*
			 * Se carga el encabezado para saber si fue firma electronica. Si
			 * asi fue se procesa la firma.
			 */
            if (id_caso_oper == 1) {
                try {
                    ReintegrosCajaManager.validaGabineteCaso(conn, c);
                    EgresoEncabezado oaEncabezado = new EgresoREINTEGROCAJAEncabezado();
                    oaEncabezado = oaEncabezado.cargaEncabezado(nFolio);
                    SolicitudFirmaElectronica solicitudPagoPrinter = new SolicitudPagoFirmaElectronica();
                    solicitudPagoPrinter.setDetail("tReintegroCajaDetalle");
                    solicitudPagoPrinter.setDocument(c.getTipoCaso().getGavetaAsociada());
                    solicitudPagoPrinter.setField("nFolioReintegroCaja");
                    solicitudPagoPrinter.setFileExtension("pdf");
                    solicitudPagoPrinter.setHeader("tReintegroCajaEncabezado");
                    solicitudPagoPrinter.setIdField(nFolio);
                    solicitudPagoPrinter.setReportPath(GestionServlet.reportPath);
                    solicitudPagoPrinter.setDocName("Solicitud Firmada");
                    if ("S".equalsIgnoreCase(StringUtils.trimToEmpty("" + oaEncabezado.getEsFirmaElectronica()))) {
                        Usuario u = new Usuario();
                        u.setLogin(u_login);
                        //Su = UsuarioManager.select( conn, u );
                        solicitudPagoPrinter.setUsuario(u);
                        FirmaElectronicaManager.generaArchivoFirma(conn, solicitudPagoPrinter, "Solicitud Firmada", false);
                        solicitudPagoPrinter.notificaOperacionPendiente(conn, SolicitudFirmaElectronica.VO_BO);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new SQLException(e);
                }
            }
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.syc.gestion.TipoCasoInterface#onTerminaCaso(java.sql.Connection,
	 * java.lang.String, com.syc.gestion.core.Caso, java.lang.String,
	 * java.lang.String[], java.lang.String[], java.util.Map)
	 */
    public void onTerminaCaso(Connection conn, String u_login, Caso c, String observ, String[] resp, String[] oper, @SuppressWarnings("rawtypes") Map data) throws SQLException {
        log.info("Object: {}", "Eliminando factiuras del folio " + c.getFolio() + " por el usuario " + u_login);
        String queryValidaAplicado = "SELECT  cDocumentoHaplicado FROM t" + c.getTipoCaso().getGavetaAsociada() + "encabezado WITH(nolock) WHERE nFolio" + c.getTipoCaso().getGavetaAsociada() + " = ?";
        String queryBorraCaso = " DELETE FROM tpagofactura WHERE ctipopago = ? AND nfoliopago = ?";
        PreparedStatement psSelect = null;
        PreparedStatement psDelete = null;
        ResultSet rs = null;
        String sSqlExisteInfoVuelos = " SELECT CASE WHEN COUNT(*) > 0 THEN '1' ELSE '0' END AS existe FROM tInfoBoleto WITH (NOLOCK) WHERE nFolioRelacionGastos = ? ";
        PreparedStatement psExisteVuelos = null;
        String sSqlExisteVuelosPago = " SELECT CASE WHEN COUNT(*) > 0 THEN '1' ELSE '0' END AS existe FROM tPagoDiversoBoletajeAvion WITH (NOLOCK) WHERE nFolioPagoDiverso = ? ";
        PreparedStatement psExisteVuelosPago = null;
        int nFolio = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1), 10);
        PreparedStatement psUpdateLayoutVuelos = null;
        PreparedStatement psDeleteInfoVuelos = null;
        try {
            psSelect = conn.prepareStatement(queryValidaAplicado);
            psSelect.setInt(1, nFolio);
            rs = psSelect.executeQuery();
            if (rs.next()) {
                String cDocumentoHAplicado = rs.getString("cDocumentoHaplicado");
                if ("S".equalsIgnoreCase(cDocumentoHAplicado))
                    throw new Exception("No se puede descartar el tramite " + c.getFolio() + " debido a que ha sido aplicado contablemene");
                else if (StringUtils.isBlank(cDocumentoHAplicado)) {
                    String query = "UPDATE t" + c.getTipoCaso().getGavetaAsociada() + "encabezado SET cDocumentoHaplicado = 'C' WHERE nFolio" + c.getTipoCaso().getGavetaAsociada() + " = ?";
                    PreparedStatement psUpdate = conn.prepareStatement(query);
                    psUpdate.setInt(1, nFolio);
                    psUpdate.executeUpdate();
                    psUpdate.close();
                    psUpdate = null;
                }
            }
            CloseObject.closeObject(rs);
            psDelete = conn.prepareStatement(queryBorraCaso);
            psDelete.setString(1, c.getTipoCaso().getGavetaAsociada());
            psDelete.setInt(2, nFolio);
            if ("RELACIONGASTOS".equalsIgnoreCase(c.getTipoCaso().getGavetaAsociada())) {
                psExisteVuelos = conn.prepareStatement(sSqlExisteInfoVuelos);
                psExisteVuelos.setInt(1, nFolio);
                rs = psExisteVuelos.executeQuery();
                if (rs.next()) {
                    String existe = rs.getString("existe");
                    if ("1".equalsIgnoreCase(existe)) {
                        String sUpdateLayoutVuelos = " UPDATE	vuelosdet SET vuelosdet.Status = ( CASE WHEN infoboleto.bEsVueloVigente = 0 THEN 'A' ELSE 'V' END ) " + " FROM		tLayoutVuelosDet vuelosdet WITH (NOLOCK) " + " INNER JOIN tInfoBoleto infoboleto WITH(NOLOCK) " + " ON " + " ( " + "	infoboleto.cNumeroBoleto = vuelosdet.cReferencia " + "	AND infoboleto.RFCVuelo = vuelosdet.RFC " + "	AND infoboleto.cNombreRFC = vuelosdet.cNombre " + "	AND infoboleto.mImporteBoleto = vuelosdet.mTotal " + " ) " + " WHERE infoboleto.nFolioRelacionGastos = ? ";
                        String sDeleteInfoVuelos = " DELETE FROM tInfoBoleto WHERE nFolioRelacionGastos = ? ";
                        psUpdateLayoutVuelos = conn.prepareStatement(sUpdateLayoutVuelos);
                        psUpdateLayoutVuelos.setInt(1, nFolio);
                        psDeleteInfoVuelos = conn.prepareStatement(sDeleteInfoVuelos);
                        psDeleteInfoVuelos.setInt(1, nFolio);
                        int updateVuelos = psUpdateLayoutVuelos.executeUpdate();
                        log.debug("Object: " + String.valueOf("Se Actualizaron: " + updateVuelos + " Vuelos."));
                        int deleteVuelos = psDeleteInfoVuelos.executeUpdate();
                        log.debug("Object: " + String.valueOf("Se eliminaron: " + deleteVuelos + " Vuelos."));
                    }
                }
            } else if ("PAGODIVERSO".equalsIgnoreCase(c.getTipoCaso().getGavetaAsociada())) {
                psExisteVuelosPago = conn.prepareStatement(sSqlExisteVuelosPago);
                psExisteVuelosPago.setInt(1, nFolio);
                rs = psExisteVuelosPago.executeQuery();
                if (rs.next()) {
                    String existe = rs.getString("existe");
                    if ("1".equalsIgnoreCase(existe)) {
                        String sDeleteVuelosPago = " DELETE FROM tPagoDiversoBoletajeAvion WHERE nFolioPagoDiverso = ? ";
                        psDeleteInfoVuelos = conn.prepareStatement(sDeleteVuelosPago);
                        psDeleteInfoVuelos.setInt(1, nFolio);
                        int deleteVuelos = psDeleteInfoVuelos.executeUpdate();
                        log.debug("Object: " + String.valueOf("Se eliminaron: " + deleteVuelos + " Vuelos del Pago."));
                    }
                }
            }
            if ("PAGODIRECTO".equalsIgnoreCase(c.getTipoCaso().getGavetaAsociada())) {
                EgresoPAGODIRECTOEncabezado epde = new EgresoPAGODIRECTOEncabezado();
                epde.setTipoPago(c.getTipoCaso().getGavetaAsociada());
                epde.setFolioPagoDirecto(nFolio);
                epde.setFolioPago(nFolio);
                epde.rechazaPago(conn, "Pago descartado");
            }
            int afectados = psDelete.executeUpdate();
            log.debug("Object: " + String.valueOf("Se borraron " + afectados + " facturas"));
        } catch (Exception e) {
            throw new SQLException(e);
        } finally {
            try {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(psDelete, false);
                CloseObject.closeObject(psSelect, false);
                CloseObject.closeObject(psExisteVuelos, false);
                CloseObject.closeObject(psExisteVuelosPago, false);
                CloseObject.closeObject(psUpdateLayoutVuelos, false);
                CloseObject.closeObject(psDeleteInfoVuelos, false);
            } catch (Exception e) {
            }
        }
    }

    public void onVenceCaso(Connection conn, String folio, int id_caso, int id_tc, int id_oper, int porc) throws SQLException {
        try {
            String[] folioSeparado = folio.split("-");
            String tipoPago = null;
            int nfolio = Integer.parseInt(folioSeparado[2]);
            if (// Pago Directo
            folioSeparado[0].equalsIgnoreCase("PDIR"))
                tipoPago = "PagoDirecto";
            else if (// Pago
            folioSeparado[0].equalsIgnoreCase("PDIV"))
                // Diverso
                tipoPago = "PagoDiverso";
            else if (// Pago
            folioSeparado[0].equalsIgnoreCase("PFED"))
                // Federalizado
                tipoPago = "PagoFederalizado";
            else if (// Pago Obra
            folioSeparado[0].equalsIgnoreCase("POBR"))
                tipoPago = "PagoObra";
            else if (// Pago Obra
            folioSeparado[0].equalsIgnoreCase("RELG"))
                tipoPago = "RelacionGastos";
            log.info("Ejecutando: onVenceCaso de la Interface: Pagos ");
            String correo = extraeEmail(conn, id_caso, id_tc);
            /*
																 * se extrae
																 * email
																 */
            switch(id_oper) {
                case // captura
                1:
                    /* envia correo en ese rango de porcentaje vencido */
                    // if (porc > 80 && porc < 87) {
                    if (porc > 800 && porc < 4000) {
                        if (!StringUtils.isEmpty(correo)) {
                            if ("true".equalsIgnoreCase(ConfiguraAplicativoManager.getSystemSetting(conn, "AMBIENTE_DESARROLLO")))
                                correo = ConfiguraAplicativoManager.getSystemSetting(conn, "CORREO_ALERTAS_DESARROLLO");
                            String subject, body;
                            subject = "Registro de " + tipoPago + " inconcluso";
                            body = "<B>Atencion</b></br>" + "Se notifica que el tramite de " + tipoPago + " con folio: " + folio + " esta por vencerse.<br><br>" + "Por lo anterior es necesario que verifique la informacion para concluir o en su defecto descartar el tramite del sistema, " + "ya que si no se atiende se descartara automaticamente perdiendo toda la informacion contenida en dicho tramite. <br><br>";
                            log.info("Object: {}", "Enviando Correo al siguiente destinatario: " + correo);
                            AlarmaManager.procesaAlarmaCNF(conn, "", null, null, subject, correo, body);
                        } else /*
							 * si no existe en bitacora se revisa si existen
							 * datos en tabla
							 */
                        if (!existeInfo(conn, folio, tipoPago, nfolio)) {
                            if (cancelaCaso(conn, id_caso, id_tc, id_oper)) {
                                borraFactura(conn, tipoPago, nfolio);
                                cancelaTabla(conn, nfolio, tipoPago);
                            }
                        }
                        /*
						 * ya se cumplio el porcentaje vencido se descarta
						 * tramite
						 */
                    } else if (porc > 100) {
                        if (!existeInfo(conn, folio, tipoPago, nfolio)) {
                            if (cancelaCaso(conn, id_caso, id_tc, id_oper)) {
                                borraFactura(conn, tipoPago, nfolio);
                                cancelaTabla(conn, nfolio, tipoPago);
                            }
                        }
                    }
                    break;
                case // autoriza
                2:
                    if (!StringUtils.isEmpty(correo)) {
                        if ("true".equalsIgnoreCase(ConfiguraAplicativoManager.getSystemSetting(conn, "AMBIENTE_DESARROLLO")))
                            correo = ConfiguraAplicativoManager.getSystemSetting(conn, "CORREO_ALERTAS_DESARROLLO");
                        String subject, body;
                        subject = "Registro de " + tipoPago + " inconcluso";
                        body = "<B>Atencion</b></br>" + "Se notifica que el tramite de " + tipoPago + " con folio: " + folio + " esta en espera de autorizacion.<br><br>" + "Por lo anterior es necesario que verifique la informacion para autorizar o en su defecto descartar el tramite del sistema. <br><br>";
                        log.info("Object: {}", "Enviando Correo al siguiente destinatario: " + correo);
                        AlarmaManager.procesaAlarmaCNF(conn, "", null, null, subject, correo, body);
                    }
                    break;
                default:
                    log.warn("Object: {}", "No existe tareas para esta operacion: " + tipoPago + "-" + folio + "-Operacion:" + id_oper);
                    break;
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    private void borraFactura(Connection conn, String tipoPago, int nFolio) {
        String queryBorraCaso = " DELETE FROM tpagofactura WHERE ctipopago = ? AND nfoliopago = ?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(queryBorraCaso);
            ps.setString(1, tipoPago);
            ps.setInt(2, nFolio);
            int afectados = ps.executeUpdate();
            log.debug("Object: " + String.valueOf("Se borrarn " + afectados + " facturas"));
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public boolean cancelaCaso(Connection conn, int id_caso, int id_tc, int id_operAnterior) {
        String query = "SELECT ID_OPER,O_RESPONSABLE FROM CG_OPERACION WITH (NOLOCK) WHERE O_RESPONSABLE LIKE 'Consulta%' AND ID_TC=  ? ";
        boolean success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int id_oper = 0;
        String o_responsable = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, id_tc);
            rs = ps.executeQuery();
            log.info("Object: {}", ps.toString());
            if (rs.next()) {
                id_oper = rs.getInt("ID_OPER");
                o_responsable = rs.getString("O_RESPONSABLE");
            }
            query = "UPDATE CG_CASO_OPERACION SET ID_OPER=?,CO_RESPONSABLE=? WHERE ID_CASO=? AND ID_TC=?";
            ps = conn.prepareStatement(query);
            ps.setInt(1, id_oper);
            ps.setString(2, o_responsable);
            ps.setInt(3, id_caso);
            ps.setInt(4, id_tc);
            log.info("Object: {}", ps.toString());
            success = ps.executeUpdate() > 0;
            if (success) {
                query = "INSERT INTO tCasosVencidosCancelados VALUES(?,?,?,getdate())";
                ps = conn.prepareStatement(query);
                ps.setInt(1, id_tc);
                ps.setInt(2, id_caso);
                ps.setInt(3, id_operAnterior);
                log.info("Object: {}", ps.toString());
                success = ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
        log.info("Object: {}", "Se Mueve a Consulta el caso " + id_caso + " : " + success);
        return success;
    }

    public void cancelaTabla(Connection conn, int nfolio, String tipoPago) {
        String query;
        boolean success = false;
        PreparedStatement ps = null;
        try {
            query = "UPDATE T" + tipoPago + "Encabezado SET cDocumentoHaplicado='C' WHERE nFolio" + tipoPago + " = " + nfolio;
            ps = conn.prepareStatement(query);
            log.info("Object: {}", ps.toString());
            success = ps.executeUpdate() > 0;
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        } finally {
            CloseObject.closeObject(ps);
        }
        log.info("Object: {}", "Se cancela Pago: " + tipoPago + " - " + nfolio + " : " + success);
    }

    public boolean existeInfo(Connection conn, String folio, String tipoPago, int nfolio) {
        boolean existe = false;
        String query = "SELECT * FROM T" + tipoPago + "Encabezado WITH (NOLOCK) WHERE nFolio" + tipoPago + " = " + nfolio + " AND cDocumentoHaplicado='S' ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            log.info("Object: {}", ps.toString());
            if (rs.next()) {
                existe = true;
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
        log.info("Object: {}", "Existe Informacion en BD: " + tipoPago + " Folio: " + nfolio + " : " + existe);
        return existe;
    }

    public String extraeEmail(Connection conn, int id_caso, int id_tc) {
        String email = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "" + "SELECT TOP(1) US.U_EMAIL " + "FROM   CG_USUARIO US  WITH (nolock) " + "       INNER JOIN CG_BITACORA BI  WITH (nolock) " + "               ON US.U_LOGIN = BI.B_CO_RESPONSABLE_EJEC " + "WHERE  BI.B_ID_CASO = ? " + "       AND BI.B_ID_TC = ? " + "ORDER  BY BI.ID_BITACORA";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, id_caso);
            ps.setInt(2, id_tc);
            log.info("Object: {}", ps.toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                email = rs.getString("U_EMAIL");
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
        return email;
    }

    public void onSolicitaFirmaElectronica(Caso c, Usuario u, String reportPath) throws SQLException {
        return;
    }
}
