package com.syc.adquisiciones.servlet;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.adquisiciones.util.Util;
import com.syc.contable.CompromisoBussinessLogic;
import com.syc.contable.ContableInterface;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ContratoRemanenteServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 1L;

    private static String jndiName = null;

    private static Logger log = LoggerFactory.getLogger(ContratoRemanenteServlet.class);

    private Connection conn = null;

    private Connection conn1 = null;

    private CallableStatement cmst = null;

    private PreparedStatement pstmt = null;

    private ResultSet rs = null;

    private JSONArray arrayObj;

    private JSONObject jsonObj;

    private PrintWriter out = null;

    private String folioGenerator = null;

    private Usuario usuario;

    private String cEjercicio, today;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jndiName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jndiName == null) {
                jndiName = "jdbc/gestion";
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jndiName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jndiName);
        } catch (NamingException exc) {
            jndiName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jndiName + "\"");
        }
        try {
            InitialContext ic = new InitialContext();
            folioGenerator = (String) ic.lookup("java:comp/env/folioGeneratorInterface");
            if (folioGenerator == null) {
                folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
                log.info("Object: {}", "Environment Entry \"folioGeneratorInterface\" nula usando default \"" + folioGenerator + "\"");
            } else
                log.info("Object: {}", "folioGeneratorInterface=" + folioGenerator);
        } catch (NamingException exc) {
            folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
            log.info("Object: {}", "Environment Entry \"folioGeneratorInterface\" no definida usando default \"" + folioGenerator + "\"");
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        java.util.Date di = new java.util.Date();
        System.out.println("Entrando time: " + di.toString());
        arrayObj = new JSONArray();
        jsonObj = new JSONObject();
        HttpSession session = request.getSession(false);
        if (session == null) {
            log.warn("No se logro crear la sesion");
            throw new ServletException("No se logro crear la sesion");
        }
        //cEjercicio = (String)session.getAttribute(GestionInterface.ATT_PedidoEjercicio);
        String DATE_FORMAT = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        today = sdf.format(c1.getTime());
        usuario = (Usuario) session.getAttribute(ATT_USER);
        if (usuario == null) {
            log.warn("No hay Usuario en sesion");
            session.invalidate();
            response.sendRedirect("../index.jsp");
            return;
        }
        int tipoOperacion = Integer.parseInt(request.getParameter("operacion"));
        String strParam = request.getParameter("Param");
        switch(tipoOperacion) {
            case 0:
                /*
			String tipoPago = strParam.split(",")[4];
			if ("1".equalsIgnoreCase(tipoPago)) apruebaContratoDiversoDes(strParam,request, response);
			else apruebaContrato(strParam,request, response);
			break;
			*/
                apruebaContrato(strParam, request, response);
                break;
            case 1:
                devuelveContratoPlurianualidadDiverso(strParam, response);
                break;
            case 2:
                //El preCompromiso es un tipo de caso 14
                generaGuardaCaso(request, response, session, (GestionInterface.IDTC_PRECOMPROMISO + ""), "Aplicación de PreCompromiso");
                break;
            case 3:
                aplicaContablemente(strParam, request, response, session, new String[] { "VENTANILLA_PRECOMPROMISO" }, new String[] { "autoriza_precomp" });
                break;
            case 4:
                //Al devolver el precompromiso se cancela contablemente y se regresa a un tipo de usuario sin asignar
                String tipoPagoDevuelve = request.getParameter("tipoPago");
                if ("1".equalsIgnoreCase(tipoPagoDevuelve))
                    devuelveContablementeDes(strParam, request, response, session, new String[] { "CONSULTA_PRECOMPROMISO" }, new String[] { "consulta_precomp" });
                else
                    devuelveContablemente(strParam, request, response, session, new String[] { "CONSULTA_PRECOMPROMISO" }, new String[] { "consulta_precomp" });
                break;
            case 5:
                validaPrecompromiso(strParam, response);
                break;
            case 6:
                //El Compromiso es un tipo de caso 7
                generaGuardaCaso(request, response, session, (GestionInterface.IDTC_COMPROMISO + ""), "Aplicación de Compromiso");
                break;
            case 7:
                //consulta_compromiso	CONSULTA_PAGOS
                aplicaContablemente(strParam, request, response, session, new String[] { "CONSULTA_PAGOS" }, new String[] { "consulta_compromiso" });
                break;
            case 8:
                Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
                try {
                    String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
                    avanzaCaso(request, c, usuario, prefixPath, new String[] { "CONSULTA_PRECOMPROMISO" }, new String[] { "consulta_precomp" });
                    try {
                        out = response.getWriter();
                        jsonObj.put("Col1", "true");
                        String destino = arrayObj.put(jsonObj).toString();
                        out.println(destino);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                } catch (Exception e) {
                    log.error("Error occurred", "Error en Aplicacion contable:" + e.getMessage());
                }
                break;
            case 9:
                //cancela contablemente desde ventanilla
                devuelveContablementeVentanilla(strParam, request, response, session, new String[] { "CONSULTA_PRECOMPROMISO" }, new String[] { "consulta_precomp" });
                break;
            case 10:
                precomprometer(request, response);
                break;
            case 11:
                devuelvePrecompromiso(request, response);
                break;
            case 12:
                compromete(request, response, session, new String[] { "CONSULTA_PAGOS" }, new String[] { "consulta_compromiso" });
                break;
        }
        java.util.Date df = new java.util.Date();
        System.out.println("Finaliza time: " + df.toString());
        System.out.println("Segundos diferencia:  " + (df.getTime() - di.getTime()) / 1000);
    }

    private synchronized void compromete(HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre) {
        boolean actualiza = false;
        PreparedStatement pstm = null;
        String mensaje = "Compromiso(s) generado(s) correctamente.";
        String Folios = "";
        String destino = "";
        CambiaPropiedadesUsuario cpu = new CambiaPropiedadesUsuario();
        String ueOriginal = usuario.getU_UR();
        String cCentroContableOrig = usuario.getPropiedad("CCENTROCONTABLE").getValor();
        CallableStatement cmst = null, cmst2 = null;
        try {
            out = response.getWriter();
            String cIdContratoDefinitivo = request.getParameter("cIdContratoDefinitivo");
            String cEjercicio = request.getParameter("cEjercicio");
            String cIdRFC = request.getParameter("cIdRFC");
            String isRadicado = request.getParameter("esRadicado") == null ? "N" : request.getParameter("esRadicado");
            AplicarContableReturn acr = null;
            ContableInterface conInt = new AplicacionContable();
            conn = DataSourceManager.getConnection(jndiName);
            log.info("Query para obtener todos los precompromisos de un contrato. ");
            String query = "select pe.nFolioPreCompromiso,rpc.cFolioPrecom " + ",pe.cCentroContable " + ",cUnidadResponsable from tPreCompromisoEncabezado pe with(Nolock) " + "inner join mRelPedContPrecomComp as rpc with(Nolock) on pe.cIdContrato=rpc.cIdPedContDef " + "and pe.nFolioPreCompromiso=rpc.nConsecutivoPrecom and pe.cDocumentoHaplicado='S' and pe.cIdContrato=?";
            log.info("Object: {}", query.toString());
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, cIdContratoDefinitivo);
            rs = pstmt.executeQuery();
            int nFolioPrecom;
            String folioComp;
            int folioCompromiso;
            int retVal = 0;
            String validaSaldo = "";
            String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
            String token = "";
            Caso c = null, sc = null;
            while (rs.next()) {
                cpu.cambiaCentroContableUE(rs.getString("cUnidadResponsable"), rs.getString("cCentroContable"), usuario);
                //obtiene folio de compromiso
                folioComp = generaGuardaCaso1(request, response, session, (GestionInterface.IDTC_COMPROMISO + ""), "Aplicación de Compromiso");
                if (folioComp == null) {
                    //no se genero correctamente folio
                    actualiza = false;
                    mensaje = "NO SE PUDO GENERAR EL FOLIO DE COMPROMISO INTENTE NUEVAMENTE";
                    return;
                } else {
                    c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
                    if (c == null) {
                        actualiza = false;
                        mensaje = "No se pudo obtener el caso de la session.";
                        log.warn("No se pudo obtener el caso de la session.");
                        return;
                    }
                    nFolioPrecom = rs.getInt("nFolioPreCompromiso");
                    Folios = Folios + token + folioComp;
                    token = ",";
                    folioCompromiso = Integer.parseInt(folioComp.substring(folioComp.lastIndexOf('-') + 1));
                    //Crear encabezado y detalle
                    retVal = creaEncabezadoDetalleComp(cIdContratoDefinitivo, nFolioPrecom, folioCompromiso, isRadicado, folioComp, usuario, conn);
                    if (retVal != 0) {
                        mensaje = "Error al crear el encabezado y detlle.";
                        log.info("Error al crear el encabezado y detlle.");
                        actualiza = false;
                        return;
                    }
                    //Aplicación contable
                    Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                    acr = conInt.aplicarContableNuevo(conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), validaSaldo);
                    if (!acr.isSuccess()) {
                        mensaje = "Error en la aplicación contable para el folio precompromiso=" + nFolioPrecom;
                        log.info("Error en la aplicación contable.");
                        actualiza = false;
                        return;
                    }
                    //Guardaar en bitacora los movimientos
                    Util.bitacoraMovimientos(cIdContratoDefinitivo, "Generación de compromiso para contratos pluriaunales con Folio=" + folioCompromiso, usuario.getLogin(), conn);
                    //Recargando el caso
                    sc = new Caso();
                    sc.setIdCaso(c.getIdCaso());
                    c = CasoManager.select(conn, sc);
                    // Una vez que ha hecho la aplicación contable avanza el caso
                    avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                    log.debug("Object: " + String.valueOf(c.getCasoDato("APLICADO_CONT").getValor()));
                    mensaje = "DOCUMENTO DE COMPROMISO APLICADO CONTABLEMENTE";
                    actualiza = true;
                }
            }
            //Actualiza el estatus del contrato
            if (actualiza) {
                if (cIdContratoDefinitivo.indexOf("AMP") != -1) {
                    pstm = conn.prepareStatement("UPDATE mContratoPluAmpliacion SET nIdEstado = 4" + " WHERE cIdContratoDefinitivo+'-AMP-'+convert(varchar,nIdConsecutivoAmpliacion) = ? ");
                    pstm.setString(1, cIdContratoDefinitivo);
                    pstm.executeUpdate();
                    log.info("Actualización de estatus para ampliaciones de contratos plurianuales");
                } else {
                    pstm = conn.prepareStatement("UPDATE mPlurianualidadContrato SET nIdEstado = 4 " + " WHERE cIdContratoDefinitivo = ? ");
                    pstm.setString(1, cIdContratoDefinitivo);
                    pstm.executeUpdate();
                    //retenciones y anticipo
                    cmst = conn.prepareCall("{call pa_agregaAnticipoAmortizacion (?,?,?,?)}");
                    cmst.setString(1, cEjercicio);
                    cmst.setString(2, cIdContratoDefinitivo);
                    cmst.setInt(3, 0);
                    cmst.setInt(4, 0);
                    cmst.execute();
                    cmst2 = conn.prepareCall("{call pa_mAgreRetenAutomaticoPLU (?,?,?,?)}");
                    cmst2.setString(1, cEjercicio);
                    cmst2.setString(2, cIdRFC);
                    cmst2.setString(3, cIdContratoDefinitivo);
                    cmst2.setString(4, cCentroContableOrig);
                    cmst2.execute();
                    log.info("Actualización de estatus para contratos plurianuales");
                }
            }
            conn.commit();
        } catch (Exception e) {
            // TODO: handle exception
            try {
                mensaje = e.getMessage();
                actualiza = false;
                conn.rollback();
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            e.printStackTrace();
            log.error(e.getMessage(), e);
        } finally {
            cpu.cambiaCentroContableUE(ueOriginal, cCentroContableOrig, usuario);
            try {
                jsonObj.put("STATUS", mensaje);
                jsonObj.put("FOLIOS", Folios);
                jsonObj.put("COMP", actualiza);
                if (rs != null) {
                    rs.close();
                }
                if (pstm != null) {
                    pstm.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (cmst != null) {
                    cmst.close();
                }
                if (cmst2 != null) {
                    cmst2.close();
                }
                if (conn != null) {
                    conn.close();
                }
                destino = arrayObj.put(jsonObj).toString();
                out.println(destino);
                jsonObj = null;
                arrayObj = null;
                rs = null;
                pstm = null;
                pstmt = null;
                cmst = null;
                cmst2 = null;
                conn = null;
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    private int creaEncabezadoDetalleComp(String cIdContratoDefinitivo, int nFolioPrecom, int nFolioComp, String isRadicado, String folioCasoCompromiso, Usuario usuario, Connection conn) throws SQLException {
        Statement stmEnc = null, stmDet = null, stmPrecom = null;
        int retval = -1;
        try {
            //Encabezado
            String query = "insert into tCompromisoEncabezado select " + nFolioComp + ",fCarga,substring(cIdContrato,1,patindex('%/%' , cIdContrato)+4),cTipoContrato,fAplicacion,cCentroContable, " + " cRamo,cUnidadResponsable,null,null,caNoPreCompromiso,nEnviadoSICOP,cTipoPoliza,nMes,cRevisado,aEjercicioFiscal,cUnidadResponsableContable, " + " nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza,'" + usuario.getLogin() + "','" + isRadicado + "' from tPreCompromisoEncabezado with(nolock) " + " where nFolioPreCompromiso=" + nFolioPrecom + " and cIdContrato='" + cIdContratoDefinitivo + "'";
            log.info("Object: {}", query.toString());
            stmEnc = conn.createStatement();
            stmEnc.executeUpdate(query);
            //Detalle
            String sql = "insert into tCompromisoDetalle (nFolioCompromiso,nDocRenglon,EP,cEvento,mImporte,mImporteNegativo,cMes,cCentroContable) select " + nFolioComp + ",nDocRenglon,EP,'CMP002',convert(money,str(mImporte,15,2))as mImporte, " + " convert(money,str(mImporteNegativo,15,2))as mImporteNegativo,cMes,cCentroContable from tPreCompromisoDetalle with(nolock) " + " where nFolioPreCompromiso=" + nFolioPrecom;
            log.info("Object: {}", sql.toString());
            stmDet = conn.createStatement();
            stmDet.executeUpdate(sql);
            //Actualiza tprecompromiso
            String sqlPrecom = "update tPreCompromisoEncabezado set C_FOLIO_COMP='" + folioCasoCompromiso + "', ConsecutivoCOMP=" + nFolioComp + ",nStatusFinanciero=1 " + " where cIdContrato='" + cIdContratoDefinitivo + "' and nFolioPreCompromiso=" + nFolioPrecom;
            log.info("Object: {}", sqlPrecom.toString());
            stmPrecom = conn.createStatement();
            stmPrecom.executeUpdate(sqlPrecom);
            retval = 0;
        } finally {
            if (stmEnc != null) {
                stmEnc.close();
            }
            if (stmDet != null) {
                stmDet.close();
            }
            if (stmPrecom != null) {
                stmPrecom.close();
            }
            stmEnc = null;
            stmDet = null;
            stmPrecom = null;
        }
        return retval;
    }

    private synchronized void devuelvePrecompromiso(HttpServletRequest request, HttpServletResponse response) {
        String destino = "";
        String ueOriginal = usuario.getU_UR();
        String cCentroContableOrig = usuario.getPropiedad("CCENTROCONTABLE").getValor();
        CambiaPropiedadesUsuario cpu = new CambiaPropiedadesUsuario();
        String sqlRel = "", sqlDoc = "";
        Statement stmRel = null, stmDoc = null;
        PreparedStatement pstm = null;
        boolean actualiza = false;
        String Folios = "";
        String token = "";
        try {
            conn = DataSourceManager.getConnection(jndiName);
            AplicarContableReturn acr = null;
            out = response.getWriter();
            jsonObj.put("PRECOM", "true");
            ContableInterface conInt = new AplicacionContable();
            String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
            Caso sc = null, c = null;
            int id_caso = 0;
            String cIdContratoDefinitivo = request.getParameter("cIdContratoDefinitivo");
            String query = "select enc.nFolioPreCompromiso,det.cCentroContable,enc.cUnidadResponsable,caso.ID_CASO,mdoc.C_FOLIO_PRE " + " from tPreCompromisoEncabezado as enc with(Nolock) " + " inner join tPreCompromisoDetalle as det with(Nolock) on enc.nFolioPreCompromiso=det.nFolioPreCompromiso " + " and enc.cDocumentoHaplicado='S' and enc.cIdContrato='" + cIdContratoDefinitivo + "' " + " inner join mDocumentoFolio as mdoc with(Nolock) on mdoc.ConsecutivoPRECOMP=enc.nFolioPreCompromiso and mdoc.cIdUnidadResponsable=enc.cUnidadResponsable " + " and det.cCentroContable=mdoc.cCentroContable " + " inner join CG_CASO as caso with(nolock) on caso.C_FOLIO=mdoc.C_FOLIO_PRE " + " inner join CG_CASO_OPERACION as oper with(Nolock) on oper.ID_CASO=caso.ID_CASO " + " group by enc.nFolioPreCompromiso,det.cCentroContable,enc.cUnidadResponsable,caso.ID_CASO,mdoc.C_FOLIO_PRE";
            log.info("Object: {}", query.toString());
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            int nFolioPrecom = 0;
            String cFolioPrecom = "";
            while (rs.next()) {
                //Cambia el centro contable al usuario
                cpu.cambiaCentroContableUE(rs.getString("cUnidadResponsable"), rs.getString("cCentroContable"), usuario);
                //Se obtiene el cso
                sc = new Caso();
                id_caso = rs.getInt("ID_CASO");
                nFolioPrecom = rs.getInt("nFolioPreCompromiso");
                cFolioPrecom = rs.getString("C_FOLIO_PRE");
                sc.setIdCaso(id_caso);
                c = CasoManager.select(conn, sc);
                //Aplicacion contable
                if (c != null) {
                    Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                    acr = conInt.cancelarAppContableNueva(conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), "");
                    if (!acr.isSuccess()) {
                        conn.rollback();
                        actualiza = false;
                        return;
                    }
                    //Elinar los datos de las tablas mDocumentoFolio, mRelPedContPrecomComp
                    sqlDoc = "delete mDocumentoFolio where cIdDocumentoDefinitivo='" + cIdContratoDefinitivo + "' and ConsecutivoPRECOMP=" + nFolioPrecom;
                    log.info("Object: {}", sqlDoc.toString());
                    stmDoc = conn.createStatement();
                    stmDoc.executeUpdate(sqlDoc);
                    sqlRel = "delete mRelPedContPrecomComp where cIdPedContDef='" + cIdContratoDefinitivo + "' and nConsecutivoPrecom=" + nFolioPrecom;
                    log.info("Object: {}", sqlRel.toString());
                    stmRel = conn.createStatement();
                    stmRel.executeUpdate(sqlRel);
                    //Guardaar en bitacora los movimientos
                    Util.bitacoraMovimientos(cIdContratoDefinitivo, "Cancelación de precompromiso para contratos pluriaunales con Folio=" + nFolioPrecom, usuario.getLogin(), conn);
                    //Recargando el caso
                    Caso scc = new Caso();
                    scc.setIdCaso(c.getIdCaso());
                    c = CasoManager.select(conn, scc);
                    //VENTANILLA_PRECOMPROMISO
                    String[] responsable = new String[] { "CONSULTA_PRECOMPROMISO" };
                    //autoriza_precomp
                    String[] nombre = new String[] { "consulta_precomp" };
                    avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                    log.debug("Object: " + String.valueOf(c.getCasoDato("APLICADO_CONT").getValor()));
                    Folios = Folios + token + cFolioPrecom;
                    token = ",";
                    actualiza = true;
                }
            }
            //Se cambia de estatus
            if (actualiza) {
                if (cIdContratoDefinitivo.indexOf("AMP") != -1) {
                    pstm = conn.prepareStatement("UPDATE mContratoPluAmpliacion SET nIdEstado = 2 , ConsecutivoPRECOMP = null, C_FOLIO_PRE = null " + " WHERE cIdContratoDefinitivo+'-AMP-'+convert(varchar,nIdConsecutivoAmpliacion) = ? ");
                    pstm.setString(1, cIdContratoDefinitivo);
                    pstm.executeUpdate();
                    log.info("Object: {}", "Se devuelve la ampliación del contrto plurianual: " + cIdContratoDefinitivo);
                } else {
                    pstm = conn.prepareStatement("UPDATE mPlurianualidadContrato SET nIdEstado = 2 , ConsecutivoPRECOMP = null, C_FOLIO_PRE = null " + " WHERE cIdContratoDefinitivo = ? ");
                    pstm.setString(1, cIdContratoDefinitivo);
                    pstm.executeUpdate();
                    log.info("Object: {}", "Se devuelve el contrato plurianual " + cIdContratoDefinitivo);
                }
                //falta actualizar la tabla de ampliaciones de contratos plurianuales
                jsonObj.put("STATUS", "Precompromisos Cancelados correctamente.");
                jsonObj.put("FOLIOS", Folios);
            }
            conn.commit();
        } catch (Exception e) {
            // TODO: handle exception
            try {
                conn.rollback();
                jsonObj.put("PRECOM", "false");
                jsonObj.put("STATUS", e.getMessage());
            } catch (JSONException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            e.printStackTrace();
            log.error(e.getMessage(), e);
        } finally {
            cpu.cambiaCentroContableUE(ueOriginal, cCentroContableOrig, usuario);
            destino = arrayObj.put(jsonObj).toString();
            out.println(destino);
            jsonObj = null;
            arrayObj = null;
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
                rs = null;
                pstmt = null;
                conn = null;
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                log.error(e.getMessage(), e);
            }
        }
    }

    private synchronized void precomprometer(HttpServletRequest request, HttpServletResponse response) {
        String destino = "";
        try {
            String cadTabla = request.getParameter("tablaDatos");
            int tipoPago = Integer.parseInt(request.getParameter("ntipoPago"));
            String esRadicado = request.getParameter("esRadicado") == null ? "N" : request.getParameter("esRadicado");
            String cEevento = "PRECOM";
            String descripPoliza = request.getParameter("descripPoliza") == null ? "Precompromiso de Contratos Plurianuales" : new String(request.getParameter("descripPoliza").getBytes("ISO-8859-1"), "UTF-8");
            String[] arrayTabla = cadTabla.split(",");
            ArrayList<List<String>> tabla = Util.creaArray(arrayTabla);
            out = response.getWriter();
            jsonObj.put("PRECOM", "true");
            jsonObj.put("STATUS", "");
            jsonObj.put("FOLIOS", "");
            switch(tipoPago) {
                case 0:
                    precompromisoCentralizado(request, tabla, esRadicado, descripPoliza, cEevento);
                    break;
                case //Descentralizados
                1:
                    precompromisoDesCentralizado(request, tabla, esRadicado, descripPoliza, cEevento);
                    break;
                default:
                    log.info("Opción desconocida.");
                    break;
            }
        } catch (Exception e) {
            // TODO: handle exception
            log.error(e.getMessage(), e);
            e.printStackTrace();
            try {
                jsonObj.put("PRECOM", "false");
                jsonObj.put("STATUS", e);
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } finally {
            destino = arrayObj.put(jsonObj).toString();
            out.println(destino);
            jsonObj = null;
            arrayObj = null;
            if (out != null) {
                out.close();
            }
            out = null;
        }
    }

    private synchronized void precompromisoCentralizado(HttpServletRequest request, ArrayList<List<String>> tabla, String esRadicado, String descripPoliza, String cEvento) throws JSONException, SQLException, ServletException, IOException, GestionException {
        Caso caso = null;
        String folio;
        int val = -1;
        PreparedStatement pstm = null;
        boolean actualiza = false;
        AplicarContableReturn acr = null;
        ContableInterface conInt = new AplicacionContable();
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        String validaSaldo = "";
        try {
            conn = DataSourceManager.getConnection(jndiName);
            String cIdContratoDefinitivo = request.getParameter("cIdContratoDefinitivo");
            String cEjercicio = request.getParameter("cEjercicio");
            String[] param = new String[6];
            param[0] = cIdContratoDefinitivo;
            param[1] = esRadicado;
            param[2] = cEjercicio;
            param[3] = descripPoliza;
            //Obtiene un nuevo caso.
            caso = generaGuardaCaso(usuario.getU_UR(), (GestionInterface.IDTC_PRECOMPROMISO + ""), descripPoliza);
            folio = caso.getFolio().substring(caso.getFolio().lastIndexOf("-") + 1);
            param[4] = folio;
            param[5] = caso.getFolio();
            //Crea Encbezado y detalle
            val = creaEncDetPreCompromiso(usuario, conn, param, tabla, cEvento, false);
            if (val != 0) {
                conn.rollback();
                actualiza = false;
                jsonObj.put("PRECOM", "false");
                jsonObj.put("STATUS", "Error al crear encabezado y detalle.");
                return;
            }
            //Aplicación contable
            Map m = CasoDatoManager.readValuesCasoDato(request, caso.getCasoDato(), true);
            acr = conInt.aplicarContableNuevo(conn, caso, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), validaSaldo);
            if (!acr.isSuccess()) {
                conn.rollback();
                actualiza = false;
                jsonObj.put("PRECOM", "false");
                jsonObj.put("STATUS", "Error en la aplicación contble.");
                return;
            }
            //Guardaar en bitacora los movimientos
            Util.bitacoraMovimientos(cIdContratoDefinitivo, "Generación de precompromiso para contratos pluriaunales con Folio=" + folio, usuario.getLogin(), conn);
            // Una vez que ha hecho la aplicación contable avanza el caso A CONSULTA
            //VENTANILLA_PRECOMPROMISO
            String[] responsable = new String[] { "CONSULTA_PRECOMPROMISO" };
            //autoriza_precomp
            String[] nombre = new String[] { "consulta_precomp" };
            avanzaCaso(request, caso, usuario, prefixPath, responsable, nombre);
            log.debug("Object: " + String.valueOf(caso.getCasoDato("APLICADO_CONT").getValor()));
            actualiza = true;
            //Se cambia de estatus
            if (actualiza) {
                if (param[0].indexOf("AMP") != -1) {
                    pstm = conn.prepareStatement("UPDATE mContratoPluAmpliacion SET nIdEstado = 3 , ConsecutivoPRECOMP = ?, C_FOLIO_PRE = ? " + " WHERE cIdContratoDefinitivo+'-AMP-'+convert(varchar,nIdConsecutivoAmpliacion) = ? ");
                    pstm.setString(1, param[4]);
                    pstm.setString(2, param[5]);
                    pstm.setString(3, param[0]);
                    pstm.executeUpdate();
                    log.info("Actualización de estatus para ampliaciones de contratos plurianuales");
                } else {
                    pstm = conn.prepareStatement("UPDATE mPlurianualidadContrato SET nIdEstado = 3 , ConsecutivoPRECOMP = ?, C_FOLIO_PRE = ? " + " WHERE cIdContratoDefinitivo = ? ");
                    pstm.setString(1, param[4]);
                    pstm.setString(2, param[5]);
                    pstm.setString(3, param[0]);
                    pstm.executeUpdate();
                    log.info("Actualización de estatus para contratos plurianuales");
                }
                jsonObj.put("STATUS", "Precompromisos generados correctamente.");
                jsonObj.put("FOLIOS", param[5]);
            }
            conn.commit();
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
                pstm = null;
                conn = null;
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private synchronized void precompromisoDesCentralizado(HttpServletRequest request, ArrayList<List<String>> tabla, String esRadicado, String descripPoliza, String cEevento) throws SQLException, ServletException, IOException, JSONException, GestionException {
        String ueOriginal = usuario.getU_UR();
        String cCentroContableOrig = usuario.getPropiedad("CCENTROCONTABLE").getValor();
        String cIdContratoDefinitivo = request.getParameter("cIdContratoDefinitivo");
        String cEjercicio = request.getParameter("cEjercicio");
        CambiaPropiedadesUsuario cpu = new CambiaPropiedadesUsuario();
        PreparedStatement pstm = null;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            String sql = "select *from v_obtieneCentroContable with(Nolock) where cIdContratoDefinitivo='" + cIdContratoDefinitivo + "' and cEjercicio='" + cEjercicio + "'";
            log.info("Object: {}", sql.toString());
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            String folio;
            String[] param = new String[6];
            param[0] = cIdContratoDefinitivo;
            param[1] = esRadicado;
            param[2] = cEjercicio;
            param[3] = descripPoliza;
            int val = -1;
            boolean actualiza = false;
            AplicarContableReturn acr = null;
            ContableInterface conInt = new AplicacionContable();
            Caso caso = null;
            String validaSaldo = "";
            String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
            String Folios = "";
            String token = "";
            while (rs.next()) {
                //Cambia el centro contable al usuario
                cpu.cambiaCentroContableUE(rs.getString("ur"), rs.getString("centroContable"), usuario);
                //Obtiene un nuevo caso.
                caso = generaGuardaCaso(rs.getString("ur"), (GestionInterface.IDTC_PRECOMPROMISO + ""), descripPoliza);
                //caso = generaGuardaCaso(conn, usuario, request, rsEnc.getString("aEjercicioFiscal"), GestionInterface.IDTC_COMPROMISO);
                folio = caso.getFolio().substring(caso.getFolio().lastIndexOf("-") + 1);
                param[4] = folio;
                param[5] = caso.getFolio();
                Folios = Folios + token + caso.getFolio();
                token = ",";
                //Crea Encbezado y detalle
                val = creaEncDetPreCompromiso(usuario, conn, param, tabla, cEevento, true);
                if (val != 0) {
                    conn.rollback();
                    actualiza = false;
                    jsonObj.put("PRECOM", "false");
                    jsonObj.put("STATUS", "Error al crear encaezado y detalle.");
                    return;
                }
                //Aplicación contable
                Map m = CasoDatoManager.readValuesCasoDato(request, caso.getCasoDato(), true);
                acr = conInt.aplicarContableNuevo(conn, caso, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), validaSaldo);
                if (!acr.isSuccess()) {
                    conn.rollback();
                    actualiza = false;
                    jsonObj.put("PRECOM", "false");
                    jsonObj.put("STATUS", "Error en la aplicación contble.");
                    return;
                }
                //Guardaar en bitacora los movimientos
                Util.bitacoraMovimientos(cIdContratoDefinitivo, "Generación de precompromiso para contratos pluriaunales con Folio=" + folio, usuario.getLogin(), conn);
                // Una vez que ha hecho la aplicación contable avanza el caso A CONSULTA
                //VENTANILLA_PRECOMPROMISO
                String[] responsable = new String[] { "CONSULTA_PRECOMPROMISO" };
                //autoriza_precomp
                String[] nombre = new String[] { "consulta_precomp" };
                avanzaCaso(request, caso, usuario, prefixPath, responsable, nombre);
                log.debug("Object: " + String.valueOf(caso.getCasoDato("APLICADO_CONT").getValor()));
                caso = null;
                actualiza = true;
            }
            //Se cambia de estatus
            if (actualiza) {
                if (param[0].indexOf("AMP") != -1) {
                    pstm = conn.prepareStatement("UPDATE mContratoPluAmpliacion SET nIdEstado = 3 , ConsecutivoPRECOMP = ?, C_FOLIO_PRE = ? " + " WHERE cIdContratoDefinitivo+'-AMP-'+convert(varchar,nIdConsecutivoAmpliacion) = ? ");
                    pstm.setString(1, param[4]);
                    pstm.setString(2, param[5]);
                    pstm.setString(3, param[0]);
                    pstm.executeUpdate();
                    log.info("Actualización de estatus para ampliaciones de contratos plurianuales");
                } else {
                    pstm = conn.prepareStatement("UPDATE mPlurianualidadContrato SET nIdEstado = 3 , ConsecutivoPRECOMP = ?, C_FOLIO_PRE = ? " + " WHERE cIdContratoDefinitivo = ? ");
                    pstm.setString(1, param[4]);
                    pstm.setString(2, param[5]);
                    pstm.setString(3, param[0]);
                    pstm.executeUpdate();
                    log.info("Actualización de estatus para contratos plurianuales");
                }
                jsonObj.put("STATUS", "Precompromisos generados correctamente.");
                jsonObj.put("FOLIOS", Folios);
            }
            //conn.rollback();
            conn.commit();
        } finally {
            cpu.cambiaCentroContableUE(ueOriginal, cCentroContableOrig, usuario);
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            conn = null;
            pstmt = null;
            pstm = null;
            rs = null;
        }
    }

    public int creaEncDetPreCompromiso(Usuario usuario, Connection conn, String[] param, ArrayList<List<String>> tabla, String cEevento, boolean isDescentralizado) throws SQLException {
        String queryEnc = "", queryDet = "", queryRel = "", queryDoc = "";
        Statement stmEnc = null, stmDet = null, stmRel = null, stmDoc = null;
        int retval = -1;
        int seqFolio;
        String seqValue;
        String caNoContrarrecibo;
        String DATE_FORMAT = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        Calendar c2 = Calendar.getInstance();
        c2.add(Calendar.DATE, 8);
        String vigencia = sdf.format(c2.getTime());
        String cEeventoAux = cEevento;
        //String cidContrato,String esRadicado,String cEjercicio,String descripPoliza,int folio,String cFolioPrecom
        try {
            CFSequenceManager sequence = CFSequenceManager.getInstance();
            if ("S".equals(param[1])) {
                cEevento = "R_" + cEevento;
            }
            stmEnc = conn.createStatement();
            //Crea el encabezado de la liberacion del compromiso.
            seqFolio = sequence.nextVal("CO-" + usuario.getPropiedad("CCENTROCONTABLE").getValor());
            seqValue = "" + (100000 + seqFolio);
            //seqValue = seqValue.substring(seqValue.length() - 6);
            //seqValue = "1" + seqValue.substring(seqValue.length() - 5);
            seqValue = usuario.getPropiedad("CCENTROCONTABLE").getValor() + "CO" + param[2] + seqValue;
            caNoContrarrecibo = seqValue;
            queryEnc = "INSERT INTO tPreCompromisoEncabezado " + " values (" + param[4] + ",GETDATE(),'" + param[0] + "','DI',GETDATE()," + usuario.getPropiedad("CCENTROCONTABLE").getValor() + "," + usuario.getU_Ramo() + ",'" + usuario.getU_UR() + "',NULL, NULL, '" + caNoContrarrecibo + "' , 0 , 'CO' , " + "DATEPART(MONTH,GETDATE()), NULL , '" + param[2] + "', 'RHQ' , NULL , NULL , '" + param[3] + "', 0,'" + vigencia + "',NULL,NULL)";
            log.info("Object: {}", queryEnc.toString());
            stmEnc.executeUpdate(queryEnc);
            //Crea el detalle de la liberacion del precompromiso.
            stmDet = conn.createStatement();
            stmRel = conn.createStatement();
            stmDoc = conn.createStatement();
            List<String> fila = new ArrayList<String>();
            Iterator<List<String>> itr = tabla.iterator();
            String ue = "";
            int i = 1;
            String ff = "";
            while (itr.hasNext()) {
                fila = itr.next();
                ff = (fila.get(0)).substring(39, 40);
                if ("4".equals(ff) || "N".equals(param[1])) {
                    cEevento = cEeventoAux;
                } else {
                    cEevento = "R_" + cEevento;
                }
                ue = (fila.get(0)).substring(56, 59);
                if (isDescentralizado) {
                    if (Double.parseDouble(fila.get(2)) > 0 && ue.equals(usuario.getU_UR())) {
                        queryDet = "INSERT INTO tPreCompromisoDetalle (nFolioPreCompromiso,nDocRenglon,EP,cEvento,mImporte,mImporteNegativo,cMes,cCentroContable)" + " values(" + param[4] + "," + i + ",'" + fila.get(0) + "','" + cEevento + "'," + fila.get(2) + ",-" + fila.get(2) + "," + fila.get(1) + "," + usuario.getPropiedad("CCENTROCONTABLE").getValor() + ")";
                        log.info("Object: {}", queryDet.toString());
                        stmDet.executeUpdate(queryDet);
                        i++;
                    }
                } else {
                    if (Double.parseDouble(fila.get(2)) > 0) {
                        queryDet = "INSERT INTO tPreCompromisoDetalle (nFolioPreCompromiso,nDocRenglon,EP,cEvento,mImporte,mImporteNegativo,cMes,cCentroContable)" + " values(" + param[4] + "," + i + ",'" + fila.get(0) + "','" + cEevento + "'," + fila.get(2) + ",-" + fila.get(2) + "," + fila.get(1) + "," + usuario.getPropiedad("CCENTROCONTABLE").getValor() + ")";
                        log.info("Object: {}", queryDet.toString());
                        stmDet.executeUpdate(queryDet);
                        i++;
                    }
                }
                cEevento = cEeventoAux;
                fila = null;
                fila = new ArrayList<String>();
            }
            if (i == 1) {
                //No hay detalle
                log.warn("No hay  detalle.");
                return -1;
            }
            //Se guarda la relación de precompromisos con el contrato
            queryRel = "insert into mRelPedContPrecomComp values('" + param[0] + "','" + param[5] + "'," + param[4] + ",NULL,NULL)";
            queryDoc = "insert into mDocumentoFolio values('" + param[2] + "','" + param[0] + "'," + usuario.getPropiedad("CCENTROCONTABLE").getValor() + ",'" + usuario.getU_UR() + "',NULL,NULL,'" + param[5] + "'," + param[4] + ")";
            log.info("Object: {}", queryRel.toString());
            log.info("Object: {}", queryDoc.toString());
            stmRel.executeUpdate(queryRel);
            stmDoc.executeUpdate(queryDoc);
            retval = 0;
        } finally {
            if (stmEnc != null) {
                stmEnc.close();
            }
            if (stmDet != null) {
                stmDet.close();
            }
            if (stmRel != null) {
                stmRel.close();
            }
            if (stmDoc != null) {
                stmDoc.close();
            }
            stmEnc = null;
            stmDet = null;
            stmDoc = null;
            stmRel = null;
        }
        return retval;
    }

    private Caso getCaso(HttpSession session, String centroContable) {
        //Debido a que el Modulo de adquisiciones y servicios no se encuentra en un flujo pero se necesita crear un caso para el precompromiso
        // se creo este método que obtiene el ID de caso desde base de datos y no de sesión
        String sql, tipo, ue, definitivo, subpartida, ejercicio;
        //int consecutivo;
        ejercicio = (String) session.getAttribute(GestionInterface.ATT_ContratoEjercicioPlurianual);
        ue = (String) session.getAttribute(GestionInterface.ATT_ContratoUnidadEjecPlurianual);
        definitivo = (String) session.getAttribute(GestionInterface.ATT_ContratoPlurianualDefinitivo);
        //Apartir de los atributos guardados en sesión se hace un QRY para obtener el ID_CASO
        sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mDocumentoFolio p, CG_CASO c, CG_CASO_OPERACION o " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cIdDocumentoDefinitivo=?" + " and p.cCentroContable=?";
        Caso c = null;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, definitivo);
            pstmt.setString(2, centroContable);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String idCaso = rs.getString("ID_CASO");
                if (idCaso == null) {
                    log.error("Llamada invalida, sin identificador de caso");
                    throw new GestionException("Llamada inválida, sin identificador de caso");
                }
                int id_caso = Integer.parseInt(idCaso);
                if (id_caso <= 0) {
                    log.error("Llamada invalida, identificador de caso menor o igual a cero (<= 0)");
                    throw new GestionException("Llamada inválida, identificador de caso menor o igual a cero (<= 0)");
                }
                // Una vez que se ha obtenido el ID_CASO se utiliza el CasoManager para obtner el objeto tipo caso
                Caso sc = new Caso();
                sc.setIdCaso(id_caso);
                c = CasoManager.select(conn, sc);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error occurred", "Error en Aplicacion contable:" + e.getMessage());
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (pstmt != null)
                    pstmt.close();
                if (rs != null)
                    rs.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
            pstmt = null;
            rs = null;
        }
        return c;
    }

    private Caso getCaso(HttpSession session, String centroContable, String definitivo, String tipoPago) {
        //Debido a que el Modulo de adquisiciones y servicios no se encuentra en un flujo pero se necesita crear un caso para el precompromiso
        // se creo este método que obtiene el ID de caso desde base de datos y no de sesión
        String sql;
        //int consecutivo;
        if ("1".equalsIgnoreCase(tipoPago)) {
            //Apartir de los atributos guardados en sesión se hace un QRY para obtener el ID_CASO
            sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mDocumentoFolio p, CG_CASO c, CG_CASO_OPERACION o " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cIdDocumentoDefinitivo=?" + " and p.cCentroContable=?";
        } else {
            sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mPlurianualidadContrato p, CG_CASO c, CG_CASO_OPERACION o " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cIdContratoDefinitivo=?";
        }
        Caso c = null;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            pstmt = conn.prepareStatement(sql);
            if ("1".equalsIgnoreCase(tipoPago)) {
                pstmt.setString(1, definitivo);
                pstmt.setString(2, centroContable);
            } else {
                pstmt.setString(1, definitivo);
            }
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String idCaso = rs.getString("ID_CASO");
                if (idCaso == null) {
                    log.error("Llamada invalida, sin identificador de caso");
                    throw new GestionException("Llamada inválida, sin identificador de caso");
                }
                int id_caso = Integer.parseInt(idCaso);
                if (id_caso <= 0) {
                    log.error("Llamada invalida, identificador de caso menor o igual a cero (<= 0)");
                    throw new GestionException("Llamada inválida, identificador de caso menor o igual a cero (<= 0)");
                }
                // Una vez que se ha obtenido el ID_CASO se utiliza el CasoManager para obtner el objeto tipo caso
                Caso sc = new Caso();
                sc.setIdCaso(id_caso);
                c = CasoManager.select(conn, sc);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error occurred", "Error en Aplicacion contable:" + e.getMessage());
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (pstmt != null)
                    pstmt.close();
                if (rs != null)
                    rs.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
            pstmt = null;
            rs = null;
        }
        return c;
    }

    private Caso getCaso(HttpSession session) {
        String ejercicio = (String) session.getAttribute(GestionInterface.ATT_ContratoEjercicioPlurianual);
        String sql, tipo, ue, cIdContratoDefinitivo;
        int consecutivo;
        ue = (String) session.getAttribute(GestionInterface.ATT_ContratoUnidadEjecPlurianual);
        cIdContratoDefinitivo = (String) session.getAttribute(GestionInterface.ATT_ContratoPlurianualDefinitivo);
        sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mPlurianualidadContrato p, CG_CASO c, CG_CASO_OPERACION o " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cEjercicio=?" + " and p.cIdContratoDefinitivo=?" + " and p.cIdUnidadEjecutora=?";
        Caso c = null;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, ejercicio);
            pstmt.setString(2, cIdContratoDefinitivo);
            pstmt.setString(3, ue);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String idCaso = rs.getString("ID_CASO");
                if (idCaso == null) {
                    log.error("Llamada invalida, sin identificador de caso");
                    throw new GestionException("Llamada inválida, sin identificador de caso");
                }
                int id_caso = Integer.parseInt(idCaso);
                if (id_caso <= 0) {
                    log.error("Llamada invalida, identificador de caso menor o igual a cero (<= 0)");
                    throw new GestionException("Llamada inválida, identificador de caso menor o igual a cero (<= 0)");
                }
                Caso sc = new Caso();
                sc.setIdCaso(id_caso);
                c = CasoManager.select(conn, sc);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error occurred", "Error en Aplicacion contable:" + e.getMessage());
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (pstmt != null)
                    pstmt.close();
                if (rs != null)
                    rs.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
            pstmt = null;
            rs = null;
        }
        return c;
    }

    private synchronized void validaPrecompromiso(String strParam, HttpServletResponse response) throws ServletException {
        String[] param = strParam.split(",");
        try {
            out = response.getWriter();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            conn = DataSourceManager.getConnection(jndiName);
            cmst = conn.prepareCall("{?= call pa_validaVentanilaPrecompromiso (?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, param[0]);
            cmst.setString(3, param[1]);
            cmst.execute();
            int outputValue = cmst.getInt(1);
            if (outputValue == 0)
                conn.commit();
            else
                conn.rollback();
            try {
                jsonObj.put("Col1", "" + outputValue);
                String destino = arrayObj.put(jsonObj).toString();
                out.println(destino);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            if (cmst != null)
                try {
                    cmst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        cmst = null;
        conn = null;
    }

    private synchronized void aplicaContablemente(String strParam, HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre) throws ServletException, IOException {
        //Se hizo una copia para poder recibir el error
        session = request.getSession(false);
        out = response.getWriter();
        String validaSaldo = "";
        ArrayList<String> arrLResult = new ArrayList<String>();
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        PreparedStatement pstm = null;
        CallableStatement cmst = null, cmst1 = null;
        Connection conn = null, conn1 = null;
        if (c == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        //Valida Centro de Costos
        //Validaciones de financiero
        String cCentroContable = "";
        String mensaje = "";
        if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
            cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
        }
        if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
            mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
        }
        ContableInterface conInt = new AplicacionContable();
        log.debug("Object: " + String.valueOf("Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis())));
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        AplicarContableReturn acr = null;
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        try {
            conn = cbl.getConnection();
            conn1 = cbl.getConnection();
            Map<String, String> m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
            acr = conInt.aplicarContableNuevo(conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), validaSaldo);
            arrLResult = (ArrayList) acr.getMessageList();
            if (acr.isSuccess()) {
                cmst = conn.prepareCall("{?= call pa_validaAplicacionContable (?,?,?,?,?)}");
                cmst.registerOutParameter(1, Types.INTEGER);
                cmst.setInt(2, Integer.parseInt(request.getParameter("nFolioPrecompromiso")));
                cmst.setString(3, request.getParameter("cEjercicio"));
                cmst.setString(4, "S");
                cmst.setString(5, "CO");
                cmst.setString(6, "PRECOMPROMISO");
                cmst.execute();
                int outputValue = cmst.getInt(1);
                if (outputValue == 0) {
                    jsonObj.put("Aplica", "1");
                    pstm = conn.prepareStatement("UPDATE mPlurianualidadContrato SET nIdEstado = 3 , ConsecutivoPRECOMP = ?, C_FOLIO_PRE = ? " + " WHERE cIdContratoDefinitivo = ? ");
                    pstm.setString(1, request.getParameter("nFolioPrecompromiso"));
                    pstm.setString(2, request.getParameter("folioCasoPreCompromiso"));
                    pstm.setString(3, request.getParameter("cContratoDefinitivo"));
                    pstm.executeUpdate();
                    //Recargando el caso
                    Caso sc = new Caso();
                    sc.setIdCaso(c.getIdCaso());
                    c = CasoManager.select(conn, sc);
                    // Una vez que ha hecho la aplicaciÃ³n contable avanza el caso A CONSULTA PAGOS
                    avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                    log.debug("Object: " + String.valueOf(c.getCasoDato("APLICADO_CONT").getValor()));
                    log.debug("Object: " + String.valueOf("Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis())));
                    mensaje = "DOCUMENTO DE PRECOMPROMISO APLICADO CONTABLEMENTE.";
                    conn.commit();
                } else {
                    conn.rollback();
                    jsonObj.put("Aplica", "0");
                    switch(outputValue) {
                        case 1:
                            mensaje = "EL DOCUMENTO NO SE APLICO CONTABLEMENTE (NO SE ENCONTRO EL REGISTRO DEL PRECOMPROMISO).";
                            break;
                        case 2:
                            mensaje = "EL DOCUMENTO NO SE APLICO CONTABLEMENTE (NO EXISTE NINGUN REGISTRO DE POLIZA DEL DOCUMENTO).";
                            break;
                        case 3:
                            mensaje = "EL DOCUMENTO NO SE APLICO CONTABLEMENTE (LOS MOVIMIENTOS DE LA AFECTACION CONTABLE NO ESTAN COMPLETOS).";
                            break;
                        default:
                            mensaje = "ERROR INESPERADO EN LA CONFIRMACION DE LA APLICACION CONTABLE.";
                    }
                    cmst1 = conn1.prepareCall("{call pa_fallaAplicacionContablePedidoContratoPlurianual (?,?,?,?)}");
                    cmst1.setString(1, request.getParameter("cContratoDefinitivo"));
                    cmst1.setString(2, request.getParameter("cEjercicio"));
                    cmst1.setString(3, request.getParameter("nFolioPrecompromiso"));
                    cmst1.setString(4, "CONTRATO");
                    cmst1.execute();
                    conn1.commit();
                }
            } else {
                conn.rollback();
                jsonObj.put("Aplica", "0");
                cmst1 = conn1.prepareCall("{call pa_fallaAplicacionContablePedidoContratoPlurianual (?,?,?,?)}");
                cmst1.setString(1, request.getParameter("cContratoDefinitivo"));
                cmst1.setString(2, request.getParameter("cEjercicio"));
                cmst1.setString(3, request.getParameter("nFolioPrecompromiso"));
                cmst1.setString(4, "CONTRATO");
                cmst1.execute();
                conn1.commit();
                mensaje = !"".equals(mensaje) ? mensaje : arrLResult.get(0);
            }
        } catch (Exception e) {
            try {
                conn.rollback();
                conn1.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            try {
                jsonObj.put("Aplica", "0");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            log.error("Error occurred", "Error en Aplicacion contable:" + e.getMessage());
            mensaje = "ERROR INESPERADO DE LA APLICACION CONTABLE(MOTOR CONTABLE O NO AVANZO EL CASO).";
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (conn1 != null)
                    conn1.close();
                if (cmst != null)
                    cmst.close();
                if (pstm != null)
                    pstm.close();
                if (cmst1 != null)
                    cmst1.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            try {
                //Regresa el mensaje de la aplicaciÃ³n contable para que sea mostrado en el JSP
                //mensaje=!"".equals(mensaje)?mensaje:arrLResult.get(0);
                jsonObj.put("Contable1", mensaje);
                String destino = arrayObj.put(jsonObj).toString();
                out.println(destino);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            conn = null;
            conn1 = null;
            cmst = null;
            cmst1 = null;
            pstm = null;
        }
    }

    @SuppressWarnings("unchecked")
    private synchronized void devuelveContablementeVentanilla(String strParam, HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre) throws ServletException, IOException {
        log.debug("Object: " + String.valueOf("Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis())));
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        Connection conncbl = null;
        AplicarContableReturn acr = null;
        CallableStatement cmst = null;
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        String cContableVentanilla = request.getParameter("cContableVentanilla");
        String definitivo = request.getParameter("contratoDefinitivo");
        String tipoPago = request.getParameter("tipoPago");
        ArrayList<String> arrLResult = new ArrayList<String>();
        ContableInterface conInt = new AplicacionContable();
        String mensaje = "";
        session = request.getSession(false);
        out = response.getWriter();
        try {
            //conn = DataSourceManager.getConnection(jndiName);
            conncbl = cbl.getConnection();
            Caso c = getCaso(session, cContableVentanilla, definitivo, tipoPago);
            if (c != null) {
                Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                acr = conInt.cancelarAppContableNueva(conncbl, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), "");
                arrLResult = (ArrayList) acr.getMessageList();
                if (acr.isSuccess()) {
                    cmst = conncbl.prepareCall("{?= call pa_validaAplicacionContable (?,?,?,?,?)}");
                    cmst.registerOutParameter(1, Types.INTEGER);
                    cmst.setInt(2, Integer.parseInt(request.getParameter("nFolioPreCompromiso")));
                    cmst.setString(3, request.getParameter("cEjercicio"));
                    cmst.setString(4, "C");
                    cmst.setString(5, "CO");
                    cmst.setString(6, "PRECOMPROMISO");
                    cmst.execute();
                    int outputValue = cmst.getInt(1);
                    if (outputValue == 0) {
                        //Recargando el caso
                        Caso sc = new Caso();
                        sc.setIdCaso(c.getIdCaso());
                        c = CasoManager.select(conncbl, sc);
                        // Una vez que ha hecho la aplicación contable avanza el caso
                        avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                        log.debug("Object: " + String.valueOf(c.getCasoDato("APLICADO_CONT").getValor()));
                        log.debug("Object: " + String.valueOf("Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis())));
                        mensaje = "DOCUMENTO DE PRECOMPROMISO CANCELADO CONTABLEMENTE";
                        conncbl.commit();
                    } else {
                        switch(outputValue) {
                            case 1:
                                mensaje = "EL DOCUMENTO NO SE CANCELO CONTABLEMENTE (NO SE ENCONTRO EL REGISTRO DEL PRECOMPROMISO).FAVOR DE INTENTAR NUEVAMENTE";
                                break;
                            case 2:
                                mensaje = "EL DOCUMENTO NO SE CANCELO CONTABLEMENTE (NO EXISTE NINGUN REGISTRO DE POLIZA DEL DOCUMENTO).FAVOR DE INTENTAR NUEVAMENTE";
                                break;
                            case 3:
                                mensaje = "EL DOCUMENTO NO SE CANCELO CONTABLEMENTE (LOS MOVIMIENTOS DE LA AFECTACION CONTABLE NO ESTAN COMPLETOS).FAVOR DE INTENTAR NUEVAMENTE";
                                break;
                            default:
                                mensaje = "ERROR INESPERADO EN LA CONFIRMACION DE LA CANCELACION CONTABLE.FAVOR DE INTENTAR NUEVAMENTE";
                        }
                    }
                } else {
                    conncbl.rollback();
                    mensaje = !"".equals(mensaje) ? mensaje : arrLResult.get(0);
                }
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error en Aplicacion contable:" + e.getMessage());
            mensaje = "ERROR INESPERADO DE LA CANCELACION CONTABLE(MOTOR CONTABLE O NO AVANZO EL CASO).FAVOR DE INTENTAR NUEVAMENTE";
            try {
                conncbl.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (conncbl != null)
                    conncbl.close();
                if (cmst != null)
                    cmst.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            log.debug("Object: " + String.valueOf("Termina Aplicacion contable" + new Timestamp(System.currentTimeMillis())));
            //if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor())||mensaje.contains("APLICADO"))
            try {
                mensaje = !"".equals(mensaje) ? mensaje : arrLResult.get(0);
                jsonObj.put("Contable1", mensaje);
                String destino = arrayObj.put(jsonObj).toString();
                out.println(destino);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            conncbl = null;
            cmst = null;
        }
    }

    @SuppressWarnings("unchecked")
    private synchronized void devuelveContablementeDes(String strParam, HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre) throws ServletException, IOException {
        // la diferencia con el metodo de 'aplicaContablemente' es que hace una llamada de cancelación al motor contable que recibe diferentes parámetros
        session = request.getSession(false);
        out = response.getWriter();
        ArrayList<String> arrLResult = new ArrayList<String>();
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (usuario == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        //Valida Centro de Costos
        String mensaje = "";
        ContableInterface conInt = new AplicacionContable();
        log.debug("Object: " + String.valueOf("Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis())));
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        Connection conncbl = null;
        Statement stmt = null;
        ResultSet rs = null;
        AplicarContableReturn acr = null;
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            conncbl = cbl.getConnection();
            stmt = conn.createStatement();
            String cIdContratoDefinitivo = (String) session.getAttribute(GestionInterface.ATT_ContratoPlurianualDefinitivo);
            String ejercicio = (String) session.getAttribute(GestionInterface.ATT_ContratoEjercicioPlurianual);
            String queryCons = "select DISTINCT cIdEntidadContable,cIdUnidadEjecutora from tContratoEP_TMP with(Nolock) where cIdContratoDefinitivo='" + cIdContratoDefinitivo + "'" + " and cEjercicio='" + ejercicio + "'";
            rs = stmt.executeQuery(queryCons);
            while (rs.next()) {
                String centroContable = rs.getString(1);
                Caso c = getCaso(session, centroContable);
                if (c != null) {
                    Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                    acr = conInt.cancelarAppContableNueva(conncbl, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), "");
                    arrLResult = (ArrayList) acr.getMessageList();
                    if (acr.isSuccess()) {
                        conncbl.commit();
                    } else {
                        conncbl.rollback();
                    }
                    //Recargando el caso
                    Caso sc = new Caso();
                    sc.setIdCaso(c.getIdCaso());
                    c = CasoManager.select(conncbl, sc);
                    avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                    log.debug("Object: " + String.valueOf(c.getCasoDato("APLICADO_CONT").getValor()));
                }
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error en Aplicacion contable:" + e.getMessage());
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            try {
                if (conncbl != null)
                    conncbl.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
            conncbl = null;
            rs = null;
            stmt = null;
        }
        log.debug("Object: " + String.valueOf("Termina Aplicacion contable" + new Timestamp(System.currentTimeMillis())));
        //if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor())||mensaje.contains("APLICADO"))
        try {
            mensaje = !"".equals(mensaje) ? mensaje : arrLResult.get(0);
            jsonObj.put("Contable1", mensaje);
            String destino = arrayObj.put(jsonObj).toString();
            out.println(destino);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    //////////////////////////////////////////////////////////////
    private synchronized void devuelveContablemente(String strParam, HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre) throws ServletException, IOException {
        session = request.getSession(false);
        out = response.getWriter();
        String validaSaldo = "";
        ArrayList<String> arrLResult = new ArrayList<String>();
        PreparedStatement pstm = null, pstm1 = null;
        CallableStatement cmst = null;
        Connection conn = null, conn1 = null;
        AplicarContableReturn acr = null;
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (usuario == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        //Valida Centro de Costos
        String cCentroContable = "";
        String mensaje = "";
        if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
            cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
        }
        if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
            mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
        }
        ContableInterface conInt = new AplicacionContable();
        log.debug("Object: " + String.valueOf("Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis())));
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        try {
            conn = cbl.getConnection();
            conn1 = cbl.getConnection();
            Caso c;
            if ((c = (Caso) session.getAttribute(GestionInterface.ATT_CASE)) == null)
                c = getCaso(session);
            if (c == null) {
                log.error("Error en Aplicacion contable:");
                return;
            }
            Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
            acr = conInt.cancelarAppContableNueva(conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), "");
            arrLResult = (ArrayList) acr.getMessageList();
            if (acr.isSuccess()) {
                cmst = conn.prepareCall("{?= call pa_validaAplicacionContable (?,?,?,?,?)}");
                cmst.registerOutParameter(1, Types.INTEGER);
                cmst.setInt(2, Integer.parseInt(request.getParameter("nFolioPrecompromiso")));
                cmst.setString(3, request.getParameter("cEjercicio"));
                cmst.setString(4, "C");
                cmst.setString(5, "CO");
                cmst.setString(6, "PRECOMPROMISO");
                cmst.execute();
                int outputValue = cmst.getInt(1);
                if (outputValue == 0) {
                    jsonObj.put("Devuelve", "1");
                    pstm = conn.prepareStatement("UPDATE mPlurianualidadContrato SET nIdEstado = 2 , ConsecutivoPRECOMP = null, C_FOLIO_PRE=null " + " WHERE cIdContratoDefinitivo = ? " + " and cEjercicio=?");
                    pstm.setString(1, request.getParameter("cContratoDefinitivo"));
                    pstm.setString(2, request.getParameter("cEjercicio"));
                    pstm.executeUpdate();
                    Caso sc = new Caso();
                    sc.setIdCaso(c.getIdCaso());
                    c = CasoManager.select(conn, sc);
                    // Una vez que ha hecho la aplicación contable avanza el caso
                    avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                    log.debug("Object: " + String.valueOf(c.getCasoDato("APLICADO_CONT").getValor()));
                    log.debug("Object: " + String.valueOf("Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis())));
                    mensaje = "DOCUMENTO DE PRECOMPROMISO CANCELADO CONTABLEMENTE";
                    log.debug("Object: " + String.valueOf("Termina Aplicacion contable" + new Timestamp(System.currentTimeMillis())));
                    conn.commit();
                } else {
                    conn.rollback();
                    switch(outputValue) {
                        case 1:
                            mensaje = "EL DOCUMENTO NO SE CANCELO CONTABLEMENTE (NO SE ENCONTRO EL REGISTRO DEL PRECOMPROMISO).FAVOR DE INTENTAR NUEVAMENTE";
                            break;
                        case 2:
                            mensaje = "EL DOCUMENTO NO SE CANCELO CONTABLEMENTE (NO EXISTE NINGUN REGISTRO DE POLIZA DEL DOCUMENTO).FAVOR DE INTENTAR NUEVAMENTE";
                            break;
                        case 3:
                            mensaje = "EL DOCUMENTO NO SE CANCELO CONTABLEMENTE (LOS MOVIMIENTOS DE LA AFECTACION CONTABLE NO ESTAN COMPLETOS).FAVOR DE INTENTAR NUEVAMENTE";
                            break;
                        default:
                            mensaje = "ERROR INESPERADO EN LA CONFIRMACION DE LA CANCELACION CONTABLE.FAVOR DE INTENTAR NUEVAMENTE";
                    }
                    jsonObj.put("Devuelve", "0");
                    //hubo un error al aplicar contablemente ,actualiza status de la tabla de mpedido
                    pstm1 = conn1.prepareStatement("UPDATE mPlurianualidadContrato SET nIdEstado = 3 " + " WHERE cIdContratoDefinitivo = ? " + " and cEjercicio=?");
                    pstm1.setString(1, request.getParameter("cContratoDefinitivo"));
                    pstm1.setString(2, request.getParameter("cEjercicio"));
                    pstm1.executeUpdate();
                    conn1.commit();
                }
            } else {
                conn.rollback();
                jsonObj.put("Devuelve", "0");
                mensaje = !"".equals(mensaje) ? mensaje : arrLResult.get(0);
                //hubo un error al aplicar contablemente ,actualiza status de la tabla de mpedido
                pstm1 = conn1.prepareStatement("UPDATE mPlurianualidadContrato SET nIdEstado = 3 " + " WHERE cIdContratoDefinitivo = ? " + " and cEjercicio=?");
                pstm1.setString(1, request.getParameter("cContratoDefinitivo"));
                pstm1.setString(2, request.getParameter("cEjercicio"));
                pstm1.executeUpdate();
                conn1.commit();
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error en Aplicacion contable:" + e.getMessage());
            try {
                jsonObj.put("Devuelve", "0");
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
            mensaje = "ERROR INESPERADO DE LA CANCELACION CONTABLE(MOTOR CONTABLE O NO AVANZO EL CASO).FAVOR DE INTENTAR NUEVAMENTE";
            try {
                conn.rollback();
                conn1.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (conn1 != null)
                    conn1.close();
                if (cmst != null)
                    cmst.close();
                if (pstm != null)
                    pstm.close();
                if (pstm1 != null)
                    pstm1.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            try {
                mensaje = !"".equals(mensaje) ? mensaje : arrLResult.get(0);
                jsonObj.put("Contable1", mensaje);
                String destino = arrayObj.put(jsonObj).toString();
                out.println(destino);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            conn = null;
            conn1 = null;
            cmst = null;
            pstm = null;
            pstm1 = null;
        }
    }

    /////////////////////////////////////////////////////////////////
    /*
	
	private synchronized void devuelveContablemente(String strParam, HttpServletRequest request, HttpServletResponse response,HttpSession session,String[] responsable, String[] nombre) throws ServletException, IOException {
		session = request.getSession(false);
		out = response.getWriter();
		String validaSaldo="";
		ArrayList<String> arrLResult = new ArrayList<String>();
		Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
		if (usuario == null) {
			response.sendRedirect("../index.jsp");
			return;
		}
		//Valida Centro de Costos
		String cCentroContable = "";
		String mensaje = "";
		if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
			cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
		}
		if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
			mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
		}
		ContableInterface conInt = new AplicacionContable();
		log.debug("Inicia aplicacion contable"+new Timestamp(System.currentTimeMillis()));
		CompromisoBussinessLogic cbl =new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
		Connection conn = null;
		AplicarContableReturn acr=null;
		String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
		try{
			conn = cbl.getConnection();
			Caso c; 
			if( (c = (Caso) session.getAttribute(GestionInterface.ATT_CASE))== null)
				c= getCaso(session);
			if(c==null){
				log.error("Error en Aplicacion contable:");
				return;
			}
			Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
			acr= conInt.cancelarAppContableNueva(conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(),"");
			arrLResult = (ArrayList) acr.getMessageList();
			if (acr.isSuccess()) {
				conn.commit();	
			} else {
				conn.rollback();
			}
			//Recargando el caso
			Caso sc = new Caso();
			sc.setIdCaso(c.getIdCaso());
			c = CasoManager.select(conn, sc);
			avanzaCaso(request, c, usuario, prefixPath,  responsable, nombre);
			log.debug(c.getCasoDato("APLICADO_CONT").getValor());
			
		}
		catch(Exception e){
			log.error("Error en Aplicacion contable:"+e.getMessage());
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}
			conn = null;
		}
		log.debug("Termina Aplicacion contable"+new Timestamp(System.currentTimeMillis()));
		//if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor())||mensaje.contains("APLICADO"))
			try {
				mensaje=!"".equals(mensaje)?mensaje:arrLResult.get(0);
				jsonObj.put("Contable1", mensaje);
				String destino = arrayObj.put(jsonObj).toString();
				out.println(destino);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
	}
	
	*/
    private void avanzaCaso(HttpServletRequest req, Caso c, Usuario u, String prefixPath, String[] responsable, String[] nombre) throws GestionException, ServletException, IOException {
        if (c == null) {
            log.error("Llamada invalida, sin Caso seleccionado");
            throw new GestionException("Llamada inválida, sin Caso seleccionado");
        }
        if (c.getIdCaso() <= 0) {
            log.error("Llamada invalida, identificador de caso menor o igual a cero (<= 0)");
            throw new GestionException("Llamada inválida, identificador de caso menor o igual a cero (<= 0)");
        }
        if (c.getCasoOperacion(0).getIdOperacion() <= 0) {
            log.error("Llamada invalida, sin identificador de caso operacion menor o igual a cero (<= 0)");
            throw new GestionException("Llamada inválida, sin identificador de caso operación menor o igual a cero (<= 0)");
        }
        Map m = CasoDatoManager.readValuesCasoDato(req, c.getCasoDato(), true);
        CasoBusinessLogic cbl = new CasoBusinessLogic(jndiName);
        cbl.avanzaCaso(c, u.getLogin(), "", responsable, nombre, m, prefixPath);
    }

    /*
	private synchronized void apruebaContratoDiversoDes(String strParam, HttpServletRequest request,HttpServletResponse response) throws ServletException {
		// se utliza para generar contratos diversos
		String param[] = strParam.split(",");
		Statement stmt=null;
		ResultSet rs=null;
		try {
			out = response.getWriter();
			int outputValue = 0;
			conn = DataSourceManager.getConnection(jndiName);
			cmst = conn.prepareCall("{?= call pa_validaContratoPlurianualFinanciero (?,?,?)}");
			cmst.registerOutParameter(1, Types.INTEGER);
			cmst.setString(2, param[0]);
			cmst.setString(3, param[1]);
			cmst.setString(4, param[2]);
			cmst.execute();
			outputValue = cmst.getInt(1);
			if (outputValue == 0 || outputValue==4){
				conn.commit();
				if (log.isDebugEnabled())
					log.debug("Iniciando Caso");
					
			stmt = conn.createStatement();
			String queryCons = "select DISTINCT cIdEntidadContable,cIdUnidadEjecutora from tContratoEP_TMP with(Nolock) where cIdContratoDefinitivo='"+param[1]+"'" + " and cEjercicio='"+param[0]+"'";
			
			rs = stmt.executeQuery(queryCons);
			while (rs.next()){
				try {
					String centroContable = rs.getString(1);
					String ur = rs.getString(2);
					//Genera un tipo de caso de Contrato Diverso
					Caso c = iniciaCasoDes(usuario, ur, "9");
					String folioCaso = c.getFolio();
					int indice = folioCaso.lastIndexOf('-') + 1;
					int folio = Integer.parseInt(folioCaso.substring(indice));
					//se avanza caso para que no se vea la operacion  en el inbox
					String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
					avanzaCaso(request, c, usuario, prefixPath, new String[] {"CONSULTA_CONTRATODIVERSO"}, new String[] {"consulta_contrato"});
					cmst = conn.prepareCall("{?= call pa_apruebaContratoPlurianualDes(?,?,?,?,?,?,?)}");
					cmst.registerOutParameter(1, Types.INTEGER);
					cmst.setString(2, param[0]);
					cmst.setString(3, param[1]);
					cmst.setString(4, param[3]);
					cmst.setString(5, folio + "");
					cmst.setString(6, folioCaso);
					cmst.setString(7, param[2]);
					cmst.setString(8,centroContable);
					cmst.execute();
					outputValue = cmst.getInt(1);
					if (outputValue != 0)
						conn.rollback();
				} catch (GestionException exc) {
					log.error("Iniciando Caso", exc);
					throw new ServletException(exc);
				}
			}
			
			conn.commit();
			
			}else
				conn.rollback();
			
			try {
				jsonObj.put("Col1", "" + outputValue);
				String destino = arrayObj.put(jsonObj).toString();
				out.println(destino);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (cmst != null)
					cmst.close();
				
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}
			cmst = null;
			conn = null;
			rs=null;
			stmt=null;
		}
	}

	
	*/
    private void apruebaContrato(String strParam, HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String[] param = strParam.split(",");
        try {
            out = response.getWriter();
            int outputValue;
            int folio;
            String folioCaso = null;
            conn = DataSourceManager.getConnection(jndiName);
            cmst = conn.prepareCall("{?= call pa_validaContratoPlurianualFinanciero (?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, param[0]);
            cmst.setString(3, param[1]);
            cmst.setString(4, param[2]);
            cmst.execute();
            log.info("Object: {}", "exec pa_validaContratoPlurianualFinanciero('" + param[0] + "','" + param[1] + "','" + param[2] + "')");
            outputValue = cmst.getInt(1);
            //caso de exito o con folio existente
            if (outputValue == 0 || outputValue == 4) {
                conn.commit();
                if (log.isDebugEnabled())
                    log.debug("Iniciando Caso");
                try {
                    // Contrato Diverso
                    //valor bandera para folio existente en el stored procedure
                    folio = -1;
                    //No tiene folio previo
                    if (outputValue == 0) {
                        //Genera un tipo de caso de Contrato Diverso
                        Caso c = iniciaCaso(request, "9");
                        folioCaso = c.getFolio();
                        int indice = folioCaso.lastIndexOf('-') + 1;
                        folio = Integer.parseInt(folioCaso.substring(indice));
                        //se avanza caso para que no se vea la operacion  en el inbox
                        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
                        avanzaCaso(request, c, usuario, prefixPath, new String[] { "CONSULTA_CONTRATODIVERSO" }, new String[] { "consulta_contrato" });
                    }
                    cmst = conn.prepareCall("{?= call pa_apruebaContratoPlurianual(?,?,?,?,?)}");
                    cmst.registerOutParameter(1, Types.INTEGER);
                    cmst.setString(2, param[0]);
                    cmst.setString(3, param[1]);
                    cmst.setString(4, param[3]);
                    cmst.setString(5, folio + "");
                    cmst.setString(6, folioCaso);
                    cmst.execute();
                    outputValue = cmst.getInt(1);
                    log.info("Object: {}", "exec pa_apruebaContratoPlurianual('" + param[0] + "','" + param[1] + "','" + param[2] + "','" + folio + "','" + folioCaso + "')");
                    if (outputValue == 0) {
                        Util.bitacoraMovimientos(param[1], "APRUEBA CONTRATO PLURIANUAL", usuario.getLogin(), conn);
                        conn.commit();
                    } else
                        conn.rollback();
                } catch (GestionException exc) {
                    log.error("Iniciando Caso", exc);
                    throw new ServletException(exc);
                }
            } else
                conn.rollback();
            try {
                jsonObj.put("Col1", "" + outputValue);
                String destino = arrayObj.put(jsonObj).toString();
                out.println(destino);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (cmst != null)
                    cmst.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            cmst = null;
            conn = null;
        }
    }

    private void devuelveContratoPlurianualidadDiverso(String strParam, HttpServletResponse response) {
        String[] param = strParam.split(",");
        try {
            out = response.getWriter();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            conn = DataSourceManager.getConnection(jndiName);
            cmst = conn.prepareCall("{?= call pa_devuelveContratoPlurianual(?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, param[0]);
            cmst.setString(3, param[1]);
            cmst.setString(4, param[2]);
            cmst.execute();
            log.info("Object: {}", "exec pa_devuelveContratoPlurianual('" + param[0] + "','" + param[1] + "','" + param[2] + "')");
            int outputValue = cmst.getInt(1);
            if (outputValue == 0) {
                Util.bitacoraMovimientos(param[1], "SE DEVUELVE EL CONTRATO PLURIANUAL", usuario.getLogin(), conn);
                conn.commit();
            } else
                conn.rollback();
            try {
                jsonObj.put("Col1", "" + outputValue);
                String destino = arrayObj.put(jsonObj).toString();
                out.println(destino);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            if (cmst != null)
                try {
                    cmst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        cmst = null;
        conn = null;
    }

    private synchronized void generaGuardaCaso(HttpServletRequest request, HttpServletResponse response, HttpSession session, String tipoCaso, String CONCEPTO_MOV) throws ServletException, IOException {
        out = response.getWriter();
        int folio;
        String folioCaso = null;
        try {
            String ur = request.getParameter("ur");
            Caso c = null;
            if (ur != null) {
                c = iniciaCasoDes(usuario, ur, tipoCaso);
            } else {
                c = iniciaCaso(request, tipoCaso);
            }
            folioCaso = c.getFolio();
            int indice = folioCaso.lastIndexOf('-') + 1;
            folio = Integer.parseInt(folioCaso.substring(indice));
            try {
                jsonObj.put("Folio1", "" + folio);
                jsonObj.put("Folio2", "" + folioCaso);
                String destino = arrayObj.put(jsonObj).toString();
                Map<String, String> datos = new HashMap<String, String>();
                datos.put("FOLIO", folioCaso);
                datos.put("FECHA_DOCUMENTO", today);
                datos.put("EJERCICIO_FISCAL", cEjercicio);
                datos.put("OPERADOR", usuario.getNombre());
                datos.put("MONEDA", "MXP");
                datos.put("APLICADO_CONT", "false");
                datos.put("CONCEPTO_MOV", CONCEPTO_MOV);
                conn1 = DataSourceManager.getConnection(jndiName);
                CasoDatoManager.update(conn1, c.getIdTC(), c.getIdCaso(), datos);
                session.setAttribute(GestionInterface.ATT_CASE, c);
                conn1.commit();
                out.println(destino);
            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (SQLException eSQL) {
                eSQL.printStackTrace();
            }
        } catch (GestionException exc) {
            log.error("Iniciando Caso", exc);
            throw new ServletException(exc);
        } finally {
            try {
                if (conn1 != null)
                    conn1.close();
            } catch (SQLException exc) {
                exc.printStackTrace();
                log.warn("Cerrando conexion a base de datos", exc);
            }
        }
        conn1 = null;
    }

    private synchronized Caso generaGuardaCaso(String ur, String tipoCaso, String CONCEPTO_MOV) throws ServletException, IOException {
        int folio;
        String folioCaso = null;
        Caso c = null;
        try {
            c = iniciaCasoDes(usuario, ur, tipoCaso);
            folioCaso = c.getFolio();
            int indice = folioCaso.lastIndexOf('-') + 1;
            folio = Integer.parseInt(folioCaso.substring(indice));
            try {
                Map<String, String> datos = new HashMap<String, String>();
                datos.put("FOLIO", folioCaso);
                datos.put("FECHA_DOCUMENTO", today);
                datos.put("EJERCICIO_FISCAL", cEjercicio);
                datos.put("OPERADOR", usuario.getNombre());
                datos.put("MONEDA", "MXP");
                datos.put("APLICADO_CONT", "false");
                datos.put("CONCEPTO_MOV", CONCEPTO_MOV);
                conn1 = DataSourceManager.getConnection(jndiName);
                CasoDatoManager.update(conn1, c.getIdTC(), c.getIdCaso(), datos);
                conn1.commit();
            } catch (SQLException eSQL) {
                eSQL.printStackTrace();
            }
        } catch (GestionException exc) {
            log.error("Iniciando Caso", exc);
            throw new ServletException(exc);
        } finally {
            try {
                if (conn1 != null)
                    conn1.close();
            } catch (SQLException exc) {
                exc.printStackTrace();
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn1 = null;
        }
        return c;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private synchronized Caso iniciaCaso(HttpServletRequest req, String tCaso) throws GestionException {
        HttpSession session = req.getSession(false);
        // contrato diverso
        CasoBusinessLogic casoTx = new CasoBusinessLogic(jndiName);
        if (tCaso == null) {
            log.error("Identificador de Tipo de Caso, vacio");
            throw new GestionException("Identificador de Tipo de Caso, vacio");
        }
        int idTC = Integer.parseInt(tCaso);
        if (idTC <= 0) {
            log.error("Identificador de Tipo de Caso, menor o igual a cero (<= 0)");
            throw new GestionException("Identificador de Tipo de Caso, menor o igual a cero (<= 0)");
        }
        Caso c = null;
        FolioGeneratorInterface fg = null;
        try {
            ClassLoader cl = getClass().getClassLoader();
            Class clase = cl.loadClass(folioGenerator);
            fg = (FolioGeneratorInterface) clase.newInstance();
        } catch (ClassNotFoundException exc) {
            log.error("Generador de folios", exc);
            throw new GestionException(exc);
        } catch (InstantiationException exc) {
            log.error("Generador de folios", exc);
            throw new GestionException(exc);
        } catch (IllegalAccessException exc) {
            log.error("Generador de folios", exc);
            throw new GestionException(exc);
        } catch (Exception exc) {
            log.error("algo raro paso", exc);
            exc.printStackTrace();
            throw new GestionException(exc);
        }
        c = casoTx.IniciaCaso(usuario, idTC, fg);
        if (c == null) {
            log.error("No se logro crear el caso");
            throw new GestionException("No se logró crear el caso");
        }
        ITree tree = casoTx.getArbolCaso(c);
        session.setAttribute(ATT_TREE, tree);
        return c;
    }

    private synchronized Caso iniciaCasoDes(Usuario user, String ur, String tCaso) throws GestionException {
        //Este metodo es una copia del metodo del GestionServlet para iniciar casos
        // contrato diverso
        CasoBusinessLogic casoTx = new CasoBusinessLogic(jndiName);
        if (tCaso == null) {
            log.error("Identificador de Tipo de Caso, vacio");
            throw new GestionException("Identificador de Tipo de Caso, vacio");
        }
        int idTC = Integer.parseInt(tCaso);
        if (idTC <= 0) {
            log.error("Identificador de Tipo de Caso, menor o igual a cero (<= 0)");
            throw new GestionException("Identificador de Tipo de Caso, menor o igual a cero (<= 0)");
        }
        Caso c = casoTx.IniciaCaso(user.getNombre(), ur, idTC);
        if (c == null) {
            log.error("No se logro crear el caso");
            throw new GestionException("No se logró crear el caso");
        }
        return c;
    }

    private synchronized String generaGuardaCaso1(HttpServletRequest request, HttpServletResponse response, HttpSession session, String tipoCaso, String CONCEPTO_MOV) {
        String folioCaso = null;
        Connection conn1 = null;
        try {
            conn1 = DataSourceManager.getConnection(jndiName);
            Caso c = null;
            c = iniciaCaso(request, tipoCaso);
            folioCaso = c.getFolio();
            Map<String, String> datos = new HashMap<String, String>();
            //Argumentos para llenar la tabla de CG_CASO_DATO y que se muestren en el inbox
            datos.put("FOLIO", folioCaso);
            datos.put("FECHA_DOCUMENTO", today);
            datos.put("EJERCICIO_FISCAL", cEjercicio);
            datos.put("OPERADOR", usuario.getNombre());
            datos.put("MONEDA", "MXP");
            datos.put("APLICADO_CONT", "false");
            datos.put("CONCEPTO_MOV", CONCEPTO_MOV);
            CasoDatoManager.update(conn1, c.getIdTC(), c.getIdCaso(), datos);
            session.setAttribute(GestionInterface.ATT_CASE, c);
            conn1.commit();
        } catch (Exception e) {
            folioCaso = null;
            try {
                conn1.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (conn1 != null)
                    conn1.close();
            } catch (SQLException exc) {
                exc.printStackTrace();
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn1 = null;
        }
        return folioCaso;
    }
}
