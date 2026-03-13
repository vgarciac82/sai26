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
import org.apache.log4j.Logger;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import com.axtel.contratos.ContractStatus;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.adquisiciones.manager.ContratacionFormalizadaManager;
import com.syc.adquisiciones.util.Util;
import com.syc.contable.AccountingEngine;
import com.syc.contable.CompromisoBussinessLogic;
import com.syc.contable.ContableInterface;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import org.jfree.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "PedidoServlet", urlPatterns = { "/servlet/PedidoServlet" })
public class PedidoServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 1L;

    private static String jndiName = null;

    private static Logger log = Logger.getLogger(PedidoServlet.class);

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

    private String message = "";

    private String folioCompromisos = "";

    public void init(ServletConfig config) throws ServletException {
        //Crea la conexión a BD
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jndiName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jndiName == null) {
                jndiName = "jdbc/gestion";
                log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jndiName + "\"");
            } else
                log.info("dataSourceRefName=" + jndiName);
        } catch (NamingException exc) {
            jndiName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jndiName + "\"");
        }
        try {
            InitialContext ic = new InitialContext();
            folioGenerator = (String) ic.lookup("java:comp/env/folioGeneratorInterface");
            if (folioGenerator == null) {
                folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
                log.info("Environment Entry \"folioGeneratorInterface\" nula usando default \"" + folioGenerator + "\"");
            } else
                log.info("folioGeneratorInterface=" + folioGenerator);
        } catch (NamingException exc) {
            folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
            log.info("Environment Entry \"folioGeneratorInterface\" no definida usando default \"" + folioGenerator + "\"");
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Inicialización de variables
        java.util.Date di = new java.util.Date();
        System.out.println("Entrando time: " + di.toString());
        arrayObj = new JSONArray();
        jsonObj = new JSONObject();
        HttpSession session = request.getSession(false);
        if (session == null) {
            log.warn("No se logro crear la sesion");
            throw new ServletException("No se logro crear la sesion");
        }
        cEjercicio = (String) session.getAttribute(GestionInterface.ATT_PedidoEjercicio);
        if (cEjercicio == null || cEjercicio.equals("")) {
            if (request.getParameter("cEjercicio") != null)
                cEjercicio = request.getParameter("cEjercicio");
        }
        String DATE_FORMAT = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        today = sdf.format(c1.getTime());
        //Valida que hata una sesión activa
        if (session == null) {
            log.warn("No hay sesion");
            response.sendRedirect("../index.jsp");
            return;
        }
        usuario = (Usuario) session.getAttribute(ATT_USER);
        if (usuario == null) {
            log.warn("No hay Usuario en sesion");
            session.invalidate();
            response.sendRedirect("../index.jsp");
            return;
        }
        //Obtiene la operación que se manda como parámetro en la llamada GET
        int tipoOperacion = Integer.parseInt(request.getParameter("operacion"));
        log.debug("operacion: " + tipoOperacion);
        String strParam = request.getParameter("Param");
        String tipoPago = "";
        switch(tipoOperacion) {
            case 0:
                apruebaContratoDiverso(strParam, request, response);
                break;
            case 1:
                devuelveContratoDiverso(strParam, response);
                break;
            case 2:
                //El preCompromiso es un tipo de caso 14
                generaGuardaCaso(request, response, session, (GestionInterface.IDTC_PRECOMPROMISO + ""), "Aplicación de PreCompromiso");
                break;
            case 3:
                //Al momento de aplicar contablemente el precompromiso se cambia a ventanilla de precompromiso para que sea aprobado
                aplicaContablemente(strParam, request, response, session, new String[] { "VENTANILLA_PRECOMPROMISO" }, new String[] { "autoriza_precomp" });
                //aplicaContablemente(strParam,request, response,session,new String[] { "CONSULTA_PRECOMPROMISO" }, new String[] { "consulta_precomp" });
                break;
            case 4:
                tipoPago = request.getParameter("tipoPago");
                if ("1".equalsIgnoreCase(tipoPago))
                    devuelveContablementeDes(strParam, request, response, session, new String[] { "CONSULTA_PRECOMPROMISO" }, new String[] { "consulta_precomp" });
                else
                    devuelveContablemente(strParam, request, response, session, new String[] { "CONSULTA_PRECOMPROMISO" }, new String[] { "consulta_precomp" });
                break;
            case 5:
                //Siver para el JSP de ventanilla para validar datos como Fechas, montos y proveedor
                validaPrecompromiso(strParam, response);
                break;
            case 6:
                //El Compromiso es un tipo de caso 7
                generaGuardaCaso(request, response, session, (GestionInterface.IDTC_COMPROMISO + ""), "Aplicación de Compromiso");
                break;
            case 7:
                //consulta_compromiso	CONSULTA_PAGOS. Despues de generar el compromiso se envia al grupo de CONSULTA_PAGOS para que sea aprobado.
                aplicaContablementeCompromiso(strParam, request, response, session, new String[] { "CONSULTA_PAGOS" }, new String[] { "consulta_compromiso" });
                break;
            case 8:
                //al momento de crear el compromiso, se necesita avanzar el caso de precompromiso para que ya no aparezca en el Inbox
                Caso avanzaCaso = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
                //unicamente guardo el atributo de session
                session.setAttribute("avanzaCasoCompromiso", avanzaCaso);
                break;
            case 9:
                //Devuelve contablemente desde ventanilla
                devuelveContablementeVentanilla(request, response, session, new String[] { "CONSULTA_PRECOMPROMISO" }, new String[] { "consulta_precomp" });
                break;
            case 10:
                validaPrecompromisoModificadoAmpliacion(strParam, response);
                break;
            case 11:
                avanzaCasoCompromiso(session, response, request);
                break;
            case 12:
                copiaPedido(session, response, request);
                break;
            case 13:
                liberaSaldo(request, usuario);
                break;
            case 14:
                aplicaContComprDescentralizado(strParam, request, response, session, new String[] { "CONSULTA_PAGOS" }, new String[] { "consulta_compromiso" });
                break;
            case 15:
                try {
                    autorizaCompromiso(strParam, response, request, session);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
        }
        java.util.Date df = new java.util.Date();
        System.out.println("Finaliza time: " + df.toString());
        System.out.println("Segundos diferencia:  " + (df.getTime() - di.getTime()) / 1000);
    }

    private synchronized void avanzaCasoCompromiso(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        String mensajeAvanzaCaso = "";
        try {
            out = response.getWriter();
            Caso avanzaCaso = (Caso) session.getAttribute("avanzaCasoCompromiso");
            String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
            avanzaCaso(request, avanzaCaso, usuario, prefixPath, new String[] { "CONSULTA_PRECOMPROMISO" }, new String[] { "consulta_precomp" });
            mensajeAvanzaCaso = "true";
            jsonObj.put("Col1", "true");
        } catch (Exception e) {
            log.error("Error en Aplicacion contable:" + e.getMessage());
            mensajeAvanzaCaso = "false";
        } finally {
            try {
                session.removeAttribute("avanzaCasoCompromiso");
                jsonObj.put("Contable1", mensajeAvanzaCaso);
                String destino = arrayObj.put(jsonObj).toString();
                out.println(destino);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }

    private Caso getCaso(HttpSession session) {
        //Debido a que el Modulo de adquisiciones y servicios no se encuentra en un flujo pero se necesita crear un caso para el precompromiso
        // se creo este método que obtiene el ID de caso desde base de datos y no de sesión
        String ejercicio = (String) session.getAttribute(GestionInterface.ATT_PedidoEjercicio);
        String sql, tipo, ue;
        int consecutivo;
        tipo = (String) session.getAttribute(GestionInterface.ATT_PedidoTipoPedido);
        ue = (String) session.getAttribute(GestionInterface.ATT_PedidoUnidadEjec);
        consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_PedidoConsecutivo));
        //Apartir de los atributos gurdados en sesión se hace un QRY para obtener el ID_CASO
        sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mPedido p, CG_CASO c, CG_CASO_OPERACION o " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cEjercicio=?" + " and p.cIdTipoPedido=?" + " and p.cIdUnidadEjecutora=?" + " and p.nIdConsecutivo=?";
        Caso c = null;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, ejercicio);
            pstmt.setString(2, tipo);
            pstmt.setString(3, ue);
            pstmt.setInt(4, consecutivo);
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
            log.error("Error en Aplicacion contable:" + e.getMessage());
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

    private Caso getCaso(HttpSession session, String centroContable, String ur) {
        //Debido a que el Modulo de adquisiciones y servicios no se encuentra en un flujo pero se necesita crear un caso para el precompromiso
        // se creo este método que obtiene el ID de caso desde base de datos y no de sesión
        String sql;
        String tipo = (String) session.getAttribute(GestionInterface.ATT_PedidoTipoPedido);
        String ue = (String) session.getAttribute(GestionInterface.ATT_PedidoUnidadEjec);
        Integer consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_PedidoConsecutivo));
        String docDefinitivo = tipo + "-" + ue + "-" + consecutivo;
        //Apartir de los atributos guardados en sesión se hace un QRY para obtener el ID_CASO
        sql = "select MAX(c.ID_CASO) ID_CASO, MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " from mPedido p (NOLOCK) " + " left join mDocumentoFolio df (NOLOCK) on p.cIdPedidoDefinitivo = df.cIdDocumentoDefinitivo " + " left join cg_caso c (NOLOCK) on df.C_FOLIO_PRE = c.C_FOLIO " + " left join CG_CASO_OPERACION o (NOLOCK) on c.ID_CASO = o.id_caso " + " where p.cIdPedido = '" + docDefinitivo + "' and df.cCentroContable = '" + centroContable + "' and df.cIdUnidadResponsable = '" + ur + "'";
        log.debug("SQL: " + sql);
        Caso c = null;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            pstmt = conn.prepareStatement(sql);
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
            log.error("Error en Aplicacion contable:" + e.getMessage());
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

    private Caso getCasoVentanilla(String docDefinitivo) {
        //Debido a que el Modulo de adquisiciones y servicios no se encuentra en un flujo pero se necesita crear un caso para el precompromiso
        // se creo este método que obtiene el ID de caso desde base de datos y no de sesión
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mPedido p (NOLOCK), CG_CASO c (NOLOCK), CG_CASO_OPERACION o (NOLOCK) " + " where p.C_FOLIO_PRE = c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cIdPedidoDefinitivo=?";
        Caso c = null;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, docDefinitivo);
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
            log.error("Error en Aplicacion contable:" + e.getMessage());
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

    private Caso getCasoVentanilla(String docDefinitivo, String centroContable, String ur) {
        //Debido a que el Modulo de adquisiciones y servicios no se encuentra en un flujo pero se necesita crear un caso para el precompromiso
        // se creo este metodo que obtiene el ID de caso desde base de datos y no de sesión
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mDocumentoFolio p (NOLOCK), CG_CASO c (NOLOCK), CG_CASO_OPERACION o (NOLOCK) " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cIdDocumentoDefinitivo=?" + " and p.cCentroContable=?" + " and p.cIdUnidadResponsable=?";
        Caso c = null;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, docDefinitivo);
            pstmt.setString(2, centroContable);
            pstmt.setString(3, ur);
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
            log.error("Error en Aplicacion contable:" + e.getMessage());
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

    private synchronized void autorizaCompromiso(String strParam, HttpServletResponse response, HttpServletRequest request, HttpSession session) throws ServletException, SQLException, JSONException {
        String[] param = strParam.split(",");
        Connection conn = null;
        CallableStatement cmst = null;
        PrintWriter out = null;
        int outputValue = -1;
        int resp = -1;
        String mensaje = "";
        int ntipoPago = 0;
        try {
            out = response.getWriter();
            conn = DataSourceManager.getConnection(jndiName);
            cmst = conn.prepareCall("{?= call pa_validaVentanilaPrecompromiso (?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, param[0]);
            cmst.setString(3, param[1]);
            cmst.execute();
            outputValue = cmst.getInt(1);
            if (outputValue == 0) {
                String[] responsable = { "CONSULTA_PAGOS" };
                String[] nombre = { "consulta_compromiso" };
                if (ntipoPago == 0) {
                    resp = aplicacionContableCompromiso(strParam, request, response, session, responsable, nombre, conn);
                } else {
                    resp = aplicacionContableCompromisoDes(strParam, request, response, session, responsable, nombre, conn);
                }
                //Bitácora
                Util.bitacoraMovimientos(param[1], "APRUEBA_PRECOMPROMISO_FINANCIERO", usuario.getLogin(), conn);
                if (0 == resp) {
                    mensaje = "DOCUMENTO DE COMPROMISO APLICADO CONTABLEMENTE";
                    //Valisa si existe algun precompromiso (sin aplicar) de pedido o contrato que libera saldo a disponible
                    resp = liberaSaldo(request, usuario, conn);
                    if (resp == 0) {
                        session.setAttribute(GestionInterface.ATT_EstadoPedido, ContractStatus.APPROVED);
                        conn.commit();
                    } else {
                        mensaje = "No se puede autorizar el compromiso en SAI.\nNo se pudo liberar el recurso extra en precompromiso.";
                        conn.rollback();
                        return;
                    }
                } else {
                    switch(outputValue) {
                        case 1:
                            mensaje = "EL DOCUMENTO NO SE APLICO CONTABLEMENTE (NO SE ENCONTRO EL REGISTRO DEL COMPROMISO).FAVOR DE INTENTAR NUEVAMENTE";
                            break;
                        case 2:
                            mensaje = "EL DOCUMENTO NO SE APLICO CONTABLEMENTE (NO EXISTE NINGUN REGISTRO DE POLIZA DEL DOCUMENTO).FAVOR DE INTENTAR NUEVAMENTE";
                            break;
                        case 3:
                            mensaje = "EL DOCUMENTO NO SE APLICO CONTABLEMENTE (LOS MOVIMIENTOS DE LA AFECTACION CONTABLE NO ESTAN COMPLETOS).FAVOR DE INTENTAR NUEVAMENTE";
                            break;
                        case 4:
                            mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
                            break;
                        case 5:
                            mensaje = "No se puede autorizar el compromiso en SAI. El contrato tiene compromisos pendientes por autorizar en SICOP.";
                            break;
                        case 6:
                            mensaje = "No se puede autorizar el compromiso en SAI. El contrato tiene pagos pendientes por autorizar en SICOP.";
                            break;
                        case 7:
                            mensaje = "NO SE PUDO GENERAR EL FOLIO DE COMPROMISO INTENTE NUEVAMENTE";
                            break;
                        case 8:
                            mensaje = "NO SE PUDO GENERAR EL FOLIO DE COMPROMISO INTENTE NUEVAMENTE";
                            response.sendRedirect("../index.jsp");
                            break;
                        case 9:
                            mensaje = "No se puede autorizar el compromiso en SAI.\n" + message;
                            break;
                        case 10:
                            mensaje = "No se puede autorizar el compromiso en SAI.\nNo se pudo liberar el recurso extra en precompromiso.";
                            break;
                        case 11:
                            mensaje = "No hay precompromiso";
                            break;
                        default:
                            mensaje = "ERROR INESPERADO EN LA CONFIRMACION DE LA APLICACION CONTABLE.FAVOR DE INTENTAR NUEVAMENTE";
                            break;
                    }
                    conn.rollback();
                    return;
                }
            } else {
                switch(outputValue) {
                    case 1:
                        mensaje = "Este proveedor no está dado de alta en los beneficiarios";
                        break;
                    case 2:
                        mensaje = "Las fechas no respetan el orden";
                        break;
                    case 3:
                        mensaje = "Los montos no coinciden con la suma total";
                        break;
                    case -1:
                        mensaje = "No se pudo realizar la validacion intentelo nuevamente por favor.";
                        break;
                    default:
                        mensaje = "ERROR INESPERADO CONTACTE A SOPORTE SAI.";
                        break;
                }
                conn.rollback();
            }
        } catch (Exception e1) {
            outputValue = -1;
            try {
                conn.rollback();
            } catch (SQLException e) {
                Log.error(e.getLocalizedMessage().toString());
                e.printStackTrace();
            }
            e1.printStackTrace();
            mensaje = e1.getMessage().toString();
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (cmst != null) {
                cmst.close();
            }
            cmst = null;
            conn = null;
            jsonObj.put("Contable1", mensaje);
            jsonObj.put("FolioCompromiso", folioCompromisos);
            String destino = arrayObj.put(jsonObj).toString();
            out.println(destino);
        }
    }

    private synchronized int aplicacionContableCompromisoDes(String strParam, HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre, Connection conn) throws ServletException, IOException, SQLException {
        session = request.getSession(false);
        String validaSaldo = "";
        ArrayList<String> arrLResult = new ArrayList<String>();
        CallableStatement cmst = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        int outputValue = -1;
        String cCentroContable = "";
        if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
            cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
        }
        if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
            return 4;
        }
        ContableInterface conInt = new AplicacionContable();
        log.debug("Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()));
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        AplicarContableReturn acr = null;
        int folioCompromiso;
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        String cIdContratoDefinitivo = request.getParameter("contratoDefinitivo");
        String cEjercicio = request.getParameter("cEjercicio");
        String origen = request.getParameter("origen");
        String tipoOperacion = request.getParameter("tipoOperacion");
        String ueOriginal = usuario.getU_UR();
        String cCentroContableOrig = usuario.getPropiedad("CCENTROCONTABLE").getValor();
        String cuentaDispRadicado = request.getParameter("cuentaDispRadicado");
        String isRadicado = "N";
        if ("82109".equals(cuentaDispRadicado)) {
            isRadicado = "S";
        }
        CambiaPropiedadesUsuario cpu = new CambiaPropiedadesUsuario();
        try {
            ConfiguraAplicativoBusinessLogic configApp = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
            boolean esSAIAlterno = "true".equals(configApp.getSystemSetting("SAI_AMBIENTAL")) || "true".equals(configApp.getSystemSetting("SAI_FONDEN"));
            if (!esSAIAlterno) {
                //Validar si hay compromisos por autorizar en sicop
                if (cIdContratoDefinitivo.indexOf("PE") < 0 && Util.hayCompromisoPendienteAutSICOP(conn, cIdContratoDefinitivo)) {
                    log.info("Hay compromisos pendientes por autorizar en SICOP.");
                    return 5;
                }
                //Validar si hay pagos por autorizar en sicop
                if (cIdContratoDefinitivo.indexOf("PE") < 0 && Util.hayPagosPendienteAutSICOP(conn, cIdContratoDefinitivo)) {
                    log.info("Hay pagos pendientes por autorizar en SICOP.");
                    return 6;
                }
            }
            log.info("Query para obtener todos los precompromisos de un pedico/contrato descentralizado. ");
            String query = "select pe.nFolioPreCompromiso,rpc.cFolioPrecom " + ",pe.cCentroContable " + ",cUnidadResponsable from tPreCompromisoEncabezado pe with(Nolock) " + "inner join mRelPedContPrecomComp as rpc with(Nolock) on pe.cIdContrato=rpc.cIdPedContDef " + "and pe.nFolioPreCompromiso=rpc.nConsecutivoPrecom and pe.cDocumentoHaplicado='S' and pe.cIdContrato=?";
            log.info(query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, cIdContratoDefinitivo);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                cpu.cambiaCentroContableUE(rs.getString("cUnidadResponsable"), rs.getString("cCentroContable"), usuario);
                //obtiene folio de compromiso
                folioCompromisos = generaGuardaCaso1(request, response, session, (GestionInterface.IDTC_COMPROMISO + ""), "Aplicación de Compromiso");
                if (folioCompromisos == null) {
                    //no se genero correctamente folio
                    return 7;
                } else {
                    Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
                    if (c == null) {
                        return 8;
                    }
                    //se compromete el precompromiso
                    folioCompromiso = Integer.parseInt(folioCompromisos.substring(folioCompromisos.lastIndexOf('-') + 1));
                    cmst = conn.prepareCall("{?= call pa_comprometePrecompromisoVarios (?,?,?,?,?,?,?,?,?,?)}");
                    cmst.registerOutParameter(1, Types.INTEGER);
                    cmst.setString(2, rs.getString("cFolioPrecom"));
                    cmst.setInt(3, Integer.parseInt(rs.getString("nFolioPreCompromiso")));
                    cmst.setString(4, cIdContratoDefinitivo);
                    cmst.setInt(5, folioCompromiso);
                    cmst.setString(6, cEjercicio);
                    cmst.setString(7, folioCompromisos);
                    cmst.setString(8, origen);
                    cmst.setString(9, tipoOperacion);
                    cmst.setString(10, usuario.getLogin());
                    cmst.setString(11, isRadicado);
                    cmst.execute();
                    outputValue = cmst.getInt(1);
                    if (outputValue == 0) {
                        if ((cIdContratoDefinitivo.indexOf("PE") < 0 && Util.esRecursoFiscal(conn, cIdContratoDefinitivo)) && !esSAIAlterno) {
                            //Recargando el caso
                            Caso sc = new Caso();
                            sc.setIdCaso(c.getIdCaso());
                            c = CasoManager.select(conn, sc);
                            // Una vez que ha hecho la aplicación contable avanza el caso
                            avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                            log.debug(c.getCasoDato("APLICADO_CONT").getValor());
                            log.debug("Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
                            return 0;
                        } else {
                            Map<String, String> m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                            acr = conInt.aplicarContableNuevo(conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), validaSaldo);
                            arrLResult = (ArrayList) acr.getMessageList();
                            if (acr.isSuccess()) {
                                cmst = conn.prepareCall("{?= call pa_validaAplicacionContable (?,?,?,?,?)}");
                                cmst.registerOutParameter(1, Types.INTEGER);
                                //cmst.setInt(2, Integer.parseInt(request.getParameter("nFolioCompromiso").toString()));
                                cmst.setInt(2, folioCompromiso);
                                cmst.setString(3, request.getParameter("cEjercicio").toString());
                                cmst.setString(4, "S");
                                cmst.setString(5, "CO");
                                cmst.setString(6, "COMPROMISO");
                                cmst.execute();
                                outputValue = cmst.getInt(1);
                                if (outputValue == 0) {
                                    //Recargando el caso
                                    Caso sc = new Caso();
                                    sc.setIdCaso(c.getIdCaso());
                                    c = CasoManager.select(conn, sc);
                                    // Una vez que ha hecho la aplicación contable avanza el caso
                                    avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                                    log.debug(c.getCasoDato("APLICADO_CONT").getValor());
                                    log.debug("Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
                                    return 0;
                                } else {
                                    return outputValue;
                                }
                            } else {
                                message = arrLResult.get(0);
                                return 9;
                            }
                        }
                    } else {
                        return outputValue;
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            log.error("Error en Aplicacion contable:" + e.getMessage());
            message = e.getMessage();
            return 9;
        } finally {
            cpu.cambiaCentroContableUE(ueOriginal, cCentroContableOrig, usuario);
            if (cmst != null) {
                cmst.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            pstmt = null;
            cmst = null;
        }
        return 0;
    }

    private synchronized void validaPrecompromiso(String strParam, HttpServletResponse response) throws ServletException {
        String[] param = strParam.split(",");
        Connection conn = null;
        CallableStatement cmst = null;
        PrintWriter out = null;
        int outputValue = -1;
        try {
            out = response.getWriter();
            // El stored procedurre se encarga de validar  que sea un RFC valido, que las fechas tengan el orden adecuado y que los montos coincidan
            // Los parámetros que recibe son cEjercicio y cIdContrato
            conn = DataSourceManager.getConnection(jndiName);
            cmst = conn.prepareCall("{?= call pa_validaVentanilaPrecompromiso (?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, param[0]);
            cmst.setString(3, param[1]);
            cmst.execute();
            outputValue = cmst.getInt(1);
            if (outputValue == 0)
                conn.commit();
            else
                conn.rollback();
        } catch (Exception e1) {
            outputValue = -1;
            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            e1.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (cmst != null)
                    cmst.close();
                try {
                    jsonObj.put("Col1", "" + outputValue);
                    String destino = arrayObj.put(jsonObj).toString();
                    out.println(destino);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            } catch (SQLException exc) {
                exc.printStackTrace();
                log.warn("Cerrando conexion a base de datos", exc);
            }
            cmst = null;
            conn = null;
        }
    }

    private synchronized void validaPrecompromisoModificadoAmpliacion(String strParam, HttpServletResponse response) throws ServletException {
        String[] param = strParam.split(",");
        Connection conn = null;
        CallableStatement cmst = null;
        PrintWriter out = null;
        int outputValue = -1;
        try {
            out = response.getWriter();
            // El stored procedurre se encarga de validar  que sea un RFC valido, que las fechas tengan el orden adecuado y que los montos coincidan
            // Los parámetros que recibe son cEjercicio y cIdContrato
            conn = DataSourceManager.getConnection(jndiName);
            cmst = conn.prepareCall("{?= call pa_validaVentanilaPrecompromisoModificadoAmpliacion (?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, param[0]);
            cmst.setString(3, param[1]);
            //Se cambio para los rfcs raros como cañuelas
            cmst.setString(4, new String(param[2].getBytes("ISO-8859-1"), "UTF-8"));
            cmst.execute();
            outputValue = cmst.getInt(1);
            if (outputValue == 0)
                conn.commit();
            else
                conn.rollback();
        } catch (Exception e1) {
            outputValue = -1;
            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            e1.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (cmst != null)
                    cmst.close();
                try {
                    jsonObj.put("Col1", "" + outputValue);
                    String destino = arrayObj.put(jsonObj).toString();
                    out.println(destino);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            } catch (SQLException exc) {
                exc.printStackTrace();
                log.warn("Cerrando conexion a base de datos", exc);
            }
            cmst = null;
            conn = null;
        }
    }

    private synchronized void aplicaContablemente(String strParam, HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre) throws ServletException, IOException {
        //Este método es una copia del método de financiero
        //Se hizo una copia para poder recibir el error
        session = request.getSession(false);
        out = response.getWriter();
        ArrayList<String> arrLResult = new ArrayList<String>();
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        PreparedStatement pstm = null, pstm2 = null;
        CallableStatement cmst = null, cmst1 = null;
        Connection conn = null, conn1 = null;
        Statement stm = null;
        ResultSet rs = null;
        String tipo = (String) session.getAttribute(GestionInterface.ATT_PedidoTipoPedido);
        String ue = (String) session.getAttribute(GestionInterface.ATT_PedidoUnidadEjec);
        int consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_PedidoConsecutivo));
        AccountingEngine accEng = new AccountingEngine();
        accEng.setValidaInsuficienciaDeSaldo(Boolean.TRUE);
        String cCentroContable = "";
        String mensaje = "";
        String cPedidoDefinitivo = tipo + "-" + ue + "-" + consecutivo + "/" + request.getParameter("cEjercicio");
        if (c == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        //Valida Centro de Costos
        //Validaciones de financiero
        if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE"))
            cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
        if (cCentroContable.isEmpty() || cCentroContable.equals(""))
            mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
        ContableInterface conInt = new AplicacionContable();
        log.debug("Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()));
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        AplicarContableReturn acr = null;
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        try {
            conn = cbl.getConnection();
            conn1 = cbl.getConnection();
            stm = conn.createStatement();
            Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
            acr = conInt.aplicarContableNuevo(conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), "");
            arrLResult = (ArrayList) acr.getMessageList();
            if (acr.isSuccess()) {
                //Confirma si la aplicacion contable se realizo correctamente
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
                    pstm = conn.prepareStatement("UPDATE mpedido SET nIdEstado = 3 , ConsecutivoPRECOMP = ?, C_FOLIO_PRE = ? " + " WHERE cIdTipoPedido = ? " + " and cIdUnidadEjecutora = ?" + " and nIdConsecutivo = ?" + " and cEjercicio=?");
                    pstm.setString(1, request.getParameter("nFolioPrecompromiso"));
                    pstm.setString(2, request.getParameter("folioCasoPreCompromiso"));
                    pstm.setString(3, tipo);
                    pstm.setString(4, ue);
                    pstm.setInt(5, consecutivo);
                    pstm.setString(6, request.getParameter("cEjercicio"));
                    pstm.executeUpdate();
                    //Se guarda la relación de precompromisos con el contrato
                    pstm2 = conn.prepareStatement("insert into mRelPedContPrecomComp values(?,?,?,null,null)");
                    pstm2.setString(1, cPedidoDefinitivo);
                    pstm2.setString(2, request.getParameter("folioCasoPreCompromiso"));
                    pstm2.setInt(3, Integer.parseInt(request.getParameter("nFolioPrecompromiso")));
                    pstm2.executeUpdate();
                    //Recargando el caso
                    Caso sc = new Caso();
                    sc.setIdCaso(c.getIdCaso());
                    c = CasoManager.select(conn, sc);
                    // Una vez que ha hecho la aplicación contable avanza el caso A CONSULTA PAGOS
                    avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                    log.debug(c.getCasoDato("APLICADO_CONT").getValor());
                    log.debug("Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
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
                    cmst1 = conn1.prepareCall("{call pa_fallaAplicacionContablePedidoContrato (?,?,?,?,?,?)}");
                    cmst1.setString(1, tipo);
                    cmst1.setString(2, ue);
                    cmst1.setInt(3, consecutivo);
                    cmst1.setString(4, request.getParameter("cEjercicio"));
                    cmst1.setString(5, request.getParameter("nFolioPrecompromiso"));
                    cmst1.setString(6, "PEDIDO");
                    cmst1.execute();
                    conn1.commit();
                }
            } else {
                conn.rollback();
                jsonObj.put("Aplica", "0");
                cmst1 = conn1.prepareCall("{call pa_fallaAplicacionContablePedidoContrato (?,?,?,?,?,?)}");
                cmst1.setString(1, tipo);
                cmst1.setString(2, ue);
                cmst1.setInt(3, consecutivo);
                cmst1.setString(4, request.getParameter("cEjercicio"));
                cmst1.setString(5, request.getParameter("nFolioPrecompromiso"));
                cmst1.setString(6, "PEDIDO");
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
            log.error("Error en Aplicacion contable:" + e.getMessage());
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
                if (pstm2 != null)
                    pstm2.close();
                if (cmst1 != null)
                    cmst1.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            try {
                //Regresa el mensaje de la aplicación contable para que sea mostrado en el JSP
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
            pstm2 = null;
        }
    }

    private synchronized void aplicaContComprDescentralizado(String strParam, HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre) throws ServletException, IOException {
        session = request.getSession(false);
        out = response.getWriter();
        String validaSaldo = "";
        ArrayList<String> arrLResult = new ArrayList<String>();
        Connection conn = null;
        CallableStatement cmst = null;
        ResultSet rs = null;
        ContratacionFormalizadaManager formManager = null;
        PreparedStatement pstmt = null;
        String cCentroContable = "";
        String mensaje = "";
        if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
            cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
        }
        if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
            mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
        }
        ContableInterface conInt = new AplicacionContable();
        log.debug("Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()));
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        AplicarContableReturn acr = null;
        String folioComp = "";
        int folioCompromiso;
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        String cIdContratoDefinitivo = request.getParameter("contratoDefinitivo");
        String cEjercicio = request.getParameter("cEjercicio");
        String origen = request.getParameter("origen");
        String tipoOperacion = request.getParameter("tipoOperacion");
        String ueOriginal = usuario.getU_UR();
        String cCentroContableOrig = usuario.getPropiedad("CCENTROCONTABLE").getValor();
        String cuentaDispRadicado = request.getParameter("cuentaDispRadicado");
        String isRadicado = "N";
        if ("82109".equals(cuentaDispRadicado)) {
            isRadicado = "S";
        }
        CambiaPropiedadesUsuario cpu = new CambiaPropiedadesUsuario();
        List<Integer> listFoliosComp = null;
        try {
            conn = cbl.getConnection();
            formManager = new ContratacionFormalizadaManager();
            ConfiguraAplicativoBusinessLogic configApp = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
            boolean esSAIAlterno = "true".equals(configApp.getSystemSetting("SAI_AMBIENTAL")) || "true".equals(configApp.getSystemSetting("SAI_FONDEN"));
            if (!esSAIAlterno) {
                //Validar si hay compromisos por autorizar en sicop
                if (cIdContratoDefinitivo.indexOf("PE") < 0 && Util.hayCompromisoPendienteAutSICOP(conn, cIdContratoDefinitivo)) {
                    log.info("Hay compromisos pendientes por autorizar en SICOP.");
                    mensaje = "No se puede autorizar el compromiso en SAI. El contrato tiene compromisos pendientes por autorizar en SICOP.";
                    return;
                }
                //Validar si hay pagos por autorizar en sicop
                if (cIdContratoDefinitivo.indexOf("PE") < 0 && Util.hayPagosPendienteAutSICOP(conn, cIdContratoDefinitivo)) {
                    log.info("Hay pagos pendientes por autorizar en SICOP.");
                    mensaje = "No se puede autorizar el compromiso en SAI. El contrato tiene pagos pendientes por autorizar en SICOP.";
                    return;
                }
            }
            //Se migran las Garantias
            formManager.addContratoConGarantia(conn, cIdContratoDefinitivo);
            log.info("Query para obtener todos los precompromisos de un contrato descentralizado. ");
            String query = "select pe.nFolioPreCompromiso,rpc.cFolioPrecom " + ",pe.cCentroContable " + ",cUnidadResponsable from tPreCompromisoEncabezado pe with(Nolock) " + "inner join mRelPedContPrecomComp as rpc with(Nolock) on pe.cIdContrato=rpc.cIdPedContDef " + "and pe.nFolioPreCompromiso=rpc.nConsecutivoPrecom and pe.cDocumentoHaplicado='S' and pe.cIdContrato=?";
            log.info(query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, cIdContratoDefinitivo);
            rs = pstmt.executeQuery();
            listFoliosComp = new ArrayList<>();
            while (rs.next()) {
                cpu.cambiaCentroContableUE(rs.getString("cUnidadResponsable"), rs.getString("cCentroContable"), usuario);
                //obtiene folio de compromiso
                folioComp = generaGuardaCaso1(request, response, session, (GestionInterface.IDTC_COMPROMISO + ""), "Aplicación de Compromiso");
                if (folioComp == null) {
                    //no se genero correctamente folio
                    mensaje = "NO SE PUDO GENERAR EL FOLIO DE COMPROMISO INTENTE NUEVAMENTE";
                    return;
                } else {
                    Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
                    if (c == null) {
                        response.sendRedirect("../index.jsp");
                        return;
                    }
                    //se compromete el precompromiso
                    folioCompromiso = Integer.parseInt(folioComp.substring(folioComp.lastIndexOf('-') + 1));
                    listFoliosComp.add(folioCompromiso);
                    cmst = conn.prepareCall("{?= call pa_comprometePrecompromisoVarios (?,?,?,?,?,?,?,?,?,?)}");
                    cmst.registerOutParameter(1, Types.INTEGER);
                    cmst.setString(2, rs.getString("cFolioPrecom"));
                    cmst.setInt(3, Integer.parseInt(rs.getString("nFolioPreCompromiso")));
                    cmst.setString(4, cIdContratoDefinitivo);
                    cmst.setInt(5, folioCompromiso);
                    cmst.setString(6, cEjercicio);
                    cmst.setString(7, folioComp);
                    cmst.setString(8, origen);
                    cmst.setString(9, tipoOperacion);
                    cmst.setString(10, usuario.getLogin());
                    cmst.setString(11, isRadicado);
                    cmst.execute();
                    if ((cIdContratoDefinitivo.indexOf("CT") < 0 && cIdContratoDefinitivo.indexOf("PE") < 0 && Util.esRecursoFiscal(conn, cIdContratoDefinitivo)) && !esSAIAlterno) {
                        //Recargando el caso
                        Caso sc = new Caso();
                        sc.setIdCaso(c.getIdCaso());
                        c = CasoManager.select(conn, sc);
                        // Una vez que ha hecho la aplicación contable avanza el caso
                        avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                        log.debug(c.getCasoDato("APLICADO_CONT").getValor());
                        log.debug("Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
                        mensaje = "DOCUMENTO DE COMPROMISO APLICADO CONTABLEMENTE";
                    } else {
                        Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                        acr = conInt.aplicarContableNuevo(conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), validaSaldo);
                        arrLResult = (ArrayList) acr.getMessageList();
                        if (acr.isSuccess()) {
                            cmst = conn.prepareCall("{?= call pa_validaAplicacionContable (?,?,?,?,?)}");
                            cmst.registerOutParameter(1, Types.INTEGER);
                            //cmst.setInt(2, Integer.parseInt(request.getParameter("nFolioCompromiso").toString()));
                            cmst.setInt(2, folioCompromiso);
                            cmst.setString(3, request.getParameter("cEjercicio").toString());
                            cmst.setString(4, "S");
                            cmst.setString(5, "CO");
                            cmst.setString(6, "COMPROMISO");
                            cmst.execute();
                            int outputValue = cmst.getInt(1);
                            if (outputValue == 0) {
                                //Recargando el caso
                                Caso sc = new Caso();
                                sc.setIdCaso(c.getIdCaso());
                                c = CasoManager.select(conn, sc);
                                // Una vez que ha hecho la aplicación contable avanza el caso
                                avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                                log.debug(c.getCasoDato("APLICADO_CONT").getValor());
                                log.debug("Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
                                mensaje = "DOCUMENTO DE COMPROMISO APLICADO CONTABLEMENTE";
                            } else {
                                switch(outputValue) {
                                    case 1:
                                        mensaje = "EL DOCUMENTO NO SE APLICO CONTABLEMENTE (NO SE ENCONTRO EL REGISTRO DEL COMPROMISO).FAVOR DE INTENTAR NUEVAMENTE";
                                        break;
                                    case 2:
                                        mensaje = "EL DOCUMENTO NO SE APLICO CONTABLEMENTE (NO EXISTE NINGUN REGISTRO DE POLIZA DEL DOCUMENTO).FAVOR DE INTENTAR NUEVAMENTE";
                                        break;
                                    case 3:
                                        mensaje = "EL DOCUMENTO NO SE APLICO CONTABLEMENTE (LOS MOVIMIENTOS DE LA AFECTACION CONTABLE NO ESTAN COMPLETOS).FAVOR DE INTENTAR NUEVAMENTE";
                                        break;
                                    default:
                                        mensaje = "ERROR INESPERADO EN LA CONFIRMACION DE LA APLICACION CONTABLE.FAVOR DE INTENTAR NUEVAMENTE";
                                }
                                conn.rollback();
                                return;
                            }
                        } else {
                            conn.rollback();
                            mensaje = !"".equals(mensaje) ? mensaje : arrLResult.get(0);
                        }
                    }
                }
            }
            if (listFoliosComp.size() > 1) {
                Util.integraFoliosCompromiso(conn, cIdContratoDefinitivo, cEjercicio);
            }
            session.setAttribute(GestionInterface.ATT_EstadoContrato, 4);
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
                e.printStackTrace();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            log.error("Error en Aplicacion contable:" + e.getMessage());
            mensaje = "ERROR INESPERADO DE LA APLICACION CONTABLE(MOTOR CONTABLE O NO AVANZO EL CASO).FAVOR DE INTENTAR NUEVAMENTE";
        } finally {
            try {
                cpu.cambiaCentroContableUE(ueOriginal, cCentroContableOrig, usuario);
                if (conn != null)
                    conn.close();
                if (cmst != null)
                    cmst.close();
                if (listFoliosComp != null)
                    listFoliosComp.clear();
                listFoliosComp = null;
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            try {
                jsonObj.put("Contable1", mensaje);
                jsonObj.put("FolioCompromiso", folioComp);
                String destino = arrayObj.put(jsonObj).toString();
                out.println(destino);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            conn = null;
            cmst = null;
        }
    }

    private synchronized int aplicacionContableCompromiso(String strParam, HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre, Connection conn) throws ServletException, IOException, SQLException {
        session = request.getSession(false);
        String validaSaldo = "";
        ArrayList<String> arrLResult = new ArrayList<String>();
        CallableStatement cmst = null;
        CallableStatement cmst2 = null;
        String cCentroContable = "";
        if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
            cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
        }
        if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
            return 4;
        }
        ContableInterface conInt = new AplicacionContable();
        log.debug("Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()));
        AplicarContableReturn acr = null;
        int folioCompromiso;
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        String cIdContratoDefinitivo = request.getParameter("contratoDefinitivo");
        String cEjercicio = request.getParameter("cEjercicio");
        String origen = request.getParameter("origen");
        String folioPrecompromiso = request.getParameter("nFolioPreCompromiso");
        String folioPrecompromisoPRCP = request.getParameter("nFolioPreCompromisoPRCP");
        String tipoOperacion = request.getParameter("tipoOperacion");
        String cuentaDispRadicado = request.getParameter("cuentaDispRadicado");
        String isRadicado = "N";
        int outputValue = -1;
        if ("82109".equals(cuentaDispRadicado)) {
            isRadicado = "S";
        }
        try {
            ConfiguraAplicativoBusinessLogic configApp = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
            boolean esSAIAlterno = "true".equals(configApp.getSystemSetting("SAI_AMBIENTAL")) || "true".equals(configApp.getSystemSetting("SAI_FONDEN"));
            if (!Util.hayPrecompromiso(conn, cIdContratoDefinitivo)) {
                log.info("No hay precompromiso");
                return 11;
            }
            if (!esSAIAlterno) {
                //Validar si hay compromisos por autorizar en sicop
                if (Util.hayCompromisoPendienteAutSICOP(conn, cIdContratoDefinitivo)) {
                    log.info("Hay compromisos pendientes por autorizar en SICOP.");
                    return 5;
                }
                //Validar si hay pagos por autorizar en sicop
                if (Util.hayPagosPendienteAutSICOP(conn, cIdContratoDefinitivo)) {
                    log.info("Hay pagos pendientes por autorizar en SICOP.");
                    return 6;
                }
            }
            //obtiene folio de compromiso
            folioCompromisos = generaGuardaCaso1(request, response, session, (GestionInterface.IDTC_COMPROMISO + ""), "Aplicación de Compromiso");
            if (folioCompromisos == null) {
                //no se genero correctamente folio
                return 7;
            } else {
                Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
                if (c == null) {
                    return 8;
                }
                //se compromete el precompromiso
                folioCompromiso = Integer.parseInt(folioCompromisos.substring(folioCompromisos.lastIndexOf('-') + 1));
                cmst = conn.prepareCall("{?= call pa_comprometePrecompromisoVarios (?,?,?,?,?,?,?,?,?,?)}");
                cmst.registerOutParameter(1, Types.INTEGER);
                cmst.setString(2, folioPrecompromisoPRCP);
                cmst.setInt(3, Integer.parseInt(folioPrecompromiso));
                cmst.setString(4, cIdContratoDefinitivo);
                cmst.setInt(5, folioCompromiso);
                cmst.setString(6, cEjercicio);
                cmst.setString(7, folioCompromisos);
                cmst.setString(8, origen);
                cmst.setString(9, tipoOperacion);
                cmst.setString(10, usuario.getLogin());
                cmst.setString(11, isRadicado);
                cmst.execute();
                outputValue = cmst.getInt(1);
                if (outputValue == 0) {
                    if ((Util.esRecursoFiscal(conn, cIdContratoDefinitivo)) && !esSAIAlterno) {
                        //Recargando el caso
                        Caso sc = new Caso();
                        sc.setIdCaso(c.getIdCaso());
                        c = CasoManager.select(conn, sc);
                        // Una vez que ha hecho la aplicación contable avanza el caso
                        avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                        log.debug(c.getCasoDato("APLICADO_CONT").getValor());
                        log.debug("Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
                        return 0;
                    } else {
                        Map<String, String> m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                        acr = conInt.aplicarContableNuevo(conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), validaSaldo);
                        arrLResult = (ArrayList) acr.getMessageList();
                        if (acr.isSuccess()) {
                            cmst2 = conn.prepareCall("{?= call pa_validaAplicacionContable (?,?,?,?,?)}");
                            cmst2.registerOutParameter(1, Types.INTEGER);
                            //cmst.setInt(2, Integer.parseInt(request.getParameter("nFolioCompromiso").toString()));
                            cmst2.setInt(2, folioCompromiso);
                            cmst2.setString(3, request.getParameter("cEjercicio").toString());
                            cmst2.setString(4, "S");
                            cmst2.setString(5, "CO");
                            cmst2.setString(6, "COMPROMISO");
                            cmst2.execute();
                            outputValue = cmst.getInt(1);
                            if (outputValue == 0) {
                                //Recargando el caso
                                Caso sc = new Caso();
                                sc.setIdCaso(c.getIdCaso());
                                c = CasoManager.select(conn, sc);
                                // Una vez que ha hecho la aplicación contable avanza el caso
                                avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                                log.debug(c.getCasoDato("APLICADO_CONT").getValor());
                                log.debug("Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
                                return outputValue;
                            } else {
                                return outputValue;
                            }
                        } else {
                            message = arrLResult.get(0);
                            return 9;
                        }
                    }
                } else {
                    return outputValue;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            log.error(e.getMessage().toString());
            e.printStackTrace();
            return 9;
        } finally {
            if (cmst != null)
                cmst.close();
            if (cmst2 != null)
                cmst2.close();
            cmst = null;
            cmst2 = null;
        }
    }

    private synchronized void aplicaContablementeCompromiso(String strParam, HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre) throws ServletException, IOException {
        //Este método es una copia del método de financiero
        //Se hizo una copia para poder recibir el error
        session = request.getSession(false);
        out = response.getWriter();
        String validaSaldo = "";
        ArrayList<String> arrLResult = new ArrayList<String>();
        Connection conn = null;
        CallableStatement cmst = null;
        ContratacionFormalizadaManager formManager = null;
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
        log.debug("Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()));
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        AplicarContableReturn acr = null;
        String folioComp = "";
        int folioCompromiso;
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        String cIdContratoDefinitivo = request.getParameter("contratoDefinitivo");
        String cEjercicio = request.getParameter("cEjercicio");
        String origen = request.getParameter("origen");
        String folioPrecompromiso = request.getParameter("nFolioPreCompromiso");
        String folioPrecompromisoPRCP = request.getParameter("nFolioPreCompromisoPRCP");
        String tipoOperacion = request.getParameter("tipoOperacion");
        String cuentaDispRadicado = request.getParameter("cuentaDispRadicado");
        String isRadicado = "N";
        if ("82109".equals(cuentaDispRadicado)) {
            isRadicado = "S";
        }
        try {
            conn = cbl.getConnection();
            formManager = new ContratacionFormalizadaManager();
            ConfiguraAplicativoBusinessLogic configApp = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
            boolean esSAIAlterno = "true".equals(configApp.getSystemSetting("SAI_AMBIENTAL")) || "true".equals(configApp.getSystemSetting("SAI_FONDEN"));
            if (!Util.hayPrecompromiso(conn, cIdContratoDefinitivo)) {
                log.info("No hay precompromiso");
                mensaje = "No hay precompromiso";
                return;
            }
            if (!esSAIAlterno) {
                //Validar si hay compromisos por autorizar en sicop
                if (cIdContratoDefinitivo.indexOf("PE") < 0 && Util.hayCompromisoPendienteAutSICOP(conn, cIdContratoDefinitivo)) {
                    log.info("Hay compromisos pendientes por autorizar en SICOP.");
                    mensaje = "No se puede autorizar el compromiso en SAI. El contrato tiene compromisos pendientes por autorizar en SICOP.";
                    return;
                }
                //Validar si hay pagos por autorizar en sicop
                if (cIdContratoDefinitivo.indexOf("PE") < 0 && Util.hayPagosPendienteAutSICOP(conn, cIdContratoDefinitivo)) {
                    log.info("Hay pagos pendientes por autorizar en SICOP.");
                    mensaje = "No se puede autorizar el compromiso en SAI. El contrato tiene pagos pendientes por autorizar en SICOP.";
                    return;
                }
            }
            //obtiene folio de compromiso
            folioComp = generaGuardaCaso1(request, response, session, (GestionInterface.IDTC_COMPROMISO + ""), "Aplicación de Compromiso");
            if (folioComp == null) {
                //no se genero correctamente folio
                mensaje = "NO SE PUDO GENERAR EL FOLIO DE COMPROMISO INTENTE NUEVAMENTE";
            } else {
                Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
                if (c == null) {
                    response.sendRedirect("../index.jsp");
                    return;
                }
                //se compromete el precompromiso
                folioCompromiso = Integer.parseInt(folioComp.substring(folioComp.lastIndexOf('-') + 1));
                cmst = conn.prepareCall("{?= call pa_comprometePrecompromisoVarios (?,?,?,?,?,?,?,?,?,?)}");
                cmst.registerOutParameter(1, Types.INTEGER);
                cmst.setString(2, folioPrecompromisoPRCP);
                cmst.setInt(3, Integer.parseInt(folioPrecompromiso));
                cmst.setString(4, cIdContratoDefinitivo);
                cmst.setInt(5, folioCompromiso);
                cmst.setString(6, cEjercicio);
                cmst.setString(7, folioComp);
                cmst.setString(8, origen);
                cmst.setString(9, tipoOperacion);
                cmst.setString(10, usuario.getLogin());
                cmst.setString(11, isRadicado);
                cmst.execute();
                //Se migran las Garantias
                formManager.addContratoConGarantia(conn, cIdContratoDefinitivo);
                if ((cIdContratoDefinitivo.indexOf("CT") < 0 && cIdContratoDefinitivo.indexOf("PE") < 0 && Util.esRecursoFiscal(conn, cIdContratoDefinitivo)) && !esSAIAlterno) {
                    //Recargando el caso
                    Caso sc = new Caso();
                    sc.setIdCaso(c.getIdCaso());
                    c = CasoManager.select(conn, sc);
                    // Una vez que ha hecho la aplicación contable avanza el caso
                    avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                    log.debug(c.getCasoDato("APLICADO_CONT").getValor());
                    log.debug("Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
                    mensaje = "DOCUMENTO DE COMPROMISO APLICADO CONTABLEMENTE EN SAI Y PENDIENTE DE APLICAR EN SICOP";
                    session.setAttribute(GestionInterface.ATT_EstadoContrato, ContractStatus.APPROVED);
                    conn.commit();
                } else {
                    Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                    acr = conInt.aplicarContableNuevo(conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), validaSaldo);
                    arrLResult = (ArrayList) acr.getMessageList();
                    if (acr.isSuccess()) {
                        cmst = conn.prepareCall("{?= call pa_validaAplicacionContable (?,?,?,?,?)}");
                        cmst.registerOutParameter(1, Types.INTEGER);
                        //cmst.setInt(2, Integer.parseInt(request.getParameter("nFolioCompromiso").toString()));
                        cmst.setInt(2, folioCompromiso);
                        cmst.setString(3, request.getParameter("cEjercicio").toString());
                        cmst.setString(4, "S");
                        cmst.setString(5, "CO");
                        cmst.setString(6, "COMPROMISO");
                        cmst.execute();
                        int outputValue = cmst.getInt(1);
                        if (outputValue == 0) {
                            //Recargando el caso
                            Caso sc = new Caso();
                            sc.setIdCaso(c.getIdCaso());
                            c = CasoManager.select(conn, sc);
                            // Una vez que ha hecho la aplicación contable avanza el caso
                            avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                            log.debug(c.getCasoDato("APLICADO_CONT").getValor());
                            log.debug("Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
                            mensaje = "DOCUMENTO DE COMPROMISO APLICADO CONTABLEMENTE";
                            session.setAttribute(GestionInterface.ATT_EstadoContrato, ContractStatus.APPROVED);
                            conn.commit();
                        } else {
                            switch(outputValue) {
                                case 1:
                                    mensaje = "EL DOCUMENTO NO SE APLICO CONTABLEMENTE (NO SE ENCONTRO EL REGISTRO DEL COMPROMISO).FAVOR DE INTENTAR NUEVAMENTE";
                                    break;
                                case 2:
                                    mensaje = "EL DOCUMENTO NO SE APLICO CONTABLEMENTE (NO EXISTE NINGUN REGISTRO DE POLIZA DEL DOCUMENTO).FAVOR DE INTENTAR NUEVAMENTE";
                                    break;
                                case 3:
                                    mensaje = "EL DOCUMENTO NO SE APLICO CONTABLEMENTE (LOS MOVIMIENTOS DE LA AFECTACION CONTABLE NO ESTAN COMPLETOS).FAVOR DE INTENTAR NUEVAMENTE";
                                    break;
                                default:
                                    mensaje = "ERROR INESPERADO EN LA CONFIRMACION DE LA APLICACION CONTABLE.FAVOR DE INTENTAR NUEVAMENTE";
                            }
                        }
                    } else {
                        conn.rollback();
                        mensaje = !"".equals(mensaje) ? mensaje : arrLResult.get(0);
                    }
                }
            }
        } catch (Exception e) {
            try {
                conn.rollback();
                e.printStackTrace();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            log.error("Error en Aplicacion contable:" + e.getMessage());
            mensaje = "ERROR INESPERADO DE LA APLICACION CONTABLE(MOTOR CONTABLE O NO AVANZO EL CASO).FAVOR DE INTENTAR NUEVAMENTE";
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (cmst != null)
                    cmst.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
                exc.printStackTrace();
            }
            try {
                //Regresa el mensaje de la aplicación contable para que sea mostrado en el JSP
                //mensaje=!"".equals(mensaje)?mensaje:arrLResult.get(0);
                jsonObj.put("Contable1", mensaje);
                jsonObj.put("FolioCompromiso", folioComp);
                String destino = arrayObj.put(jsonObj).toString();
                out.println(destino);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            conn = null;
            cmst = null;
        }
    }

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
        String tipo, ue;
        int consecutivo;
        tipo = (String) session.getAttribute(GestionInterface.ATT_PedidoTipoPedido);
        ue = (String) session.getAttribute(GestionInterface.ATT_PedidoUnidadEjec);
        consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_PedidoConsecutivo));
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
        log.debug("Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()));
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
                    pstm = conn.prepareStatement("UPDATE mpedido SET nIdEstado = 2 , ConsecutivoPRECOMP = null, C_FOLIO_PRE=null " + " WHERE cIdTipoPedido = ? " + " and cIdUnidadEjecutora = ?" + " and nIdConsecutivo = ?" + " and cEjercicio=?");
                    pstm.setString(1, tipo);
                    pstm.setString(2, ue);
                    pstm.setInt(3, consecutivo);
                    pstm.setString(4, request.getParameter("cEjercicio"));
                    pstm.executeUpdate();
                    Caso sc = new Caso();
                    sc.setIdCaso(c.getIdCaso());
                    c = CasoManager.select(conn, sc);
                    // Una vez que ha hecho la aplicación contable avanza el caso
                    avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                    log.debug(c.getCasoDato("APLICADO_CONT").getValor());
                    log.debug("Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
                    mensaje = "DOCUMENTO DE PRECOMPROMISO CANCELADO CONTABLEMENTE";
                    log.debug("Termina Aplicacion contable" + new Timestamp(System.currentTimeMillis()));
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
                    pstm1 = conn1.prepareStatement("UPDATE mpedido SET nIdEstado = 3 " + " WHERE cIdTipoPedido = ? " + " and cIdUnidadEjecutora = ?" + " and nIdConsecutivo = ?" + " and cEjercicio=?");
                    pstm1.setString(1, tipo);
                    pstm1.setString(2, ue);
                    pstm1.setInt(3, consecutivo);
                    pstm1.setString(4, request.getParameter("cEjercicio"));
                    pstm1.executeUpdate();
                    conn1.commit();
                }
            } else {
                conn.rollback();
                jsonObj.put("Devuelve", "0");
                mensaje = !"".equals(mensaje) ? mensaje : arrLResult.get(0);
                //hubo un error al aplicar contablemente ,actualiza status de la tabla de mpedido
                pstm1 = conn1.prepareStatement("UPDATE mpedido SET nIdEstado = 3 " + " WHERE cIdTipoPedido = ? " + " and cIdUnidadEjecutora = ?" + " and nIdConsecutivo = ?" + " and cEjercicio=?");
                pstm1.setString(1, tipo);
                pstm1.setString(2, ue);
                pstm1.setInt(3, consecutivo);
                pstm1.setString(4, request.getParameter("cEjercicio"));
                pstm1.executeUpdate();
                conn1.commit();
            }
        } catch (Exception e) {
            log.error("Error en Aplicacion contable:" + e.getMessage());
            try {
                e.printStackTrace();
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
                exc.printStackTrace();
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

    private synchronized void devuelveContablementeDes(String strParam, HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre) throws ServletException, IOException {
        // la diferencia con el metodo de 'aplicaContablemente' es que hace una llamada de cancelación al motor contable que recibe diferentes parámetros
        session = request.getSession(false);
        out = response.getWriter();
        String validaSaldo = "";
        ArrayList<String> arrLResult = new ArrayList<String>();
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (usuario == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        String mensaje = "";
        ContableInterface conInt = new AplicacionContable();
        log.debug("Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()));
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        AplicarContableReturn acr = null;
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        try {
            String tipo = (String) session.getAttribute(GestionInterface.ATT_PedidoTipoPedido);
            String ue = (String) session.getAttribute(GestionInterface.ATT_PedidoUnidadEjec);
            Integer consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_PedidoConsecutivo));
            String ejercicio = (String) session.getAttribute(GestionInterface.ATT_PedidoEjercicio);
            String sql = "select df.cCentroContable as cIdEntidadContable, df.cIdUnidadResponsable as ClaveInterna ,df.ConsecutivoPRECOMP" + " from mDocumentoFolio df with(nolock) ,mPedido p with(nolock)" + " where cIdDocumentoDefinitivo= p.cIdPedidoDefinitivo" + " and p.cIdTipoPedido=?" + " and p.cIdUnidadEjecutora=?" + " and p.nIdConsecutivo=?";
            conn = DataSourceManager.getConnection(jndiName);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tipo);
            pstmt.setString(2, ue);
            pstmt.setInt(3, consecutivo);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String centroContable = rs.getString(1);
                String ur = rs.getString(2);
                int folioPrecom = rs.getInt(3);
                log.debug("Centro: " + centroContable + "__Unidad: " + ur);
                Caso c = getCaso(session, centroContable, ur);
                log.debug("Folio: " + c.getFolio());
                if (c != null) {
                    Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                    acr = conInt.cancelarAppContableNueva(conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), "");
                    arrLResult = (ArrayList) acr.getMessageList();
                    if (acr.isSuccess()) {
                        cmst = conn.prepareCall("{?= call pa_validaAplicacionContable (?,?,?,?,?)}");
                        cmst.registerOutParameter(1, Types.INTEGER);
                        cmst.setInt(2, folioPrecom);
                        cmst.setString(3, ejercicio);
                        cmst.setString(4, "C");
                        cmst.setString(5, "CO");
                        cmst.setString(6, "PRECOMPROMISO");
                        cmst.execute();
                        int outputValue = cmst.getInt(1);
                        if (outputValue == 0) {
                            //Recargando el caso
                            Caso sc = new Caso();
                            sc.setIdCaso(c.getIdCaso());
                            c = CasoManager.select(conn, sc);
                            // Una vez que ha hecho la aplicación contable avanza el caso
                            avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                            log.debug(c.getCasoDato("APLICADO_CONT").getValor());
                            log.debug("Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
                            mensaje = "DOCUMENTO DE PRECOMPROMISO CANCELADO CONTABLEMENTE";
                            conn.commit();
                            log.debug("Termina Aplicacion contable" + new Timestamp(System.currentTimeMillis()));
                            //if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor())||mensaje.contains("APLICADO"))
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
                        conn.rollback();
                        mensaje = !"".equals(mensaje) ? mensaje : arrLResult.get(0);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error en Aplicacion contable:" + e.getMessage());
            mensaje = "ERROR INESPERADO DE LA CANCELACION CONTABLE(MOTOR CONTABLE O NO AVANZO EL CASO).FAVOR DE INTENTAR NUEVAMENTE";
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (pstmt != null)
                    pstmt.close();
                if (rs != null)
                    rs.close();
                if (cmst != null)
                    cmst.close();
            } catch (SQLException exc) {
                exc.printStackTrace();
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
            rs = null;
            pstmt = null;
            cmst = null;
        }
    }

    @SuppressWarnings("unchecked")
    private synchronized void devuelveContablementeVentanilla(HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre) throws ServletException, IOException {
        // la diferencia con el metodo de 'aplicaContablemente' es que hace una llamada de cancelación al motor contable que recibe diferentes parámetros
        session = request.getSession(false);
        out = response.getWriter();
        ArrayList<String> arrLResult = new ArrayList<String>();
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (usuario == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        String mensaje = "";
        ContableInterface conInt = new AplicacionContable();
        log.debug("Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()));
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        Connection conncbl = null;
        AplicarContableReturn acr = null;
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        Caso c = null;
        try {
            //conn = DataSourceManager.getConnection(jndiName);
            conncbl = cbl.getConnection();
            String tipoPago = request.getParameter("tipoPago");
            String centroContable = request.getParameter("cContableVentanilla");
            String ur = request.getParameter("ur");
            String docDefinitivo = request.getParameter("contratoDefinitivo");
            if (tipoPago != null && tipoPago.equals("1")) {
                c = getCasoVentanilla(docDefinitivo, centroContable, ur);
            } else {
                c = getCasoVentanilla(docDefinitivo);
            }
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
                        log.debug(c.getCasoDato("APLICADO_CONT").getValor());
                        log.debug("Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
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
            log.error("Error en Aplicacion contable:" + e.getMessage());
            mensaje = "ERROR INESPERADO DE LA CANCELACION CONTABLE(MOTOR CONTABLE O NO AVANZO EL CASO).FAVOR DE INTENTAR NUEVAMENTE";
            e.printStackTrace();
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
                exc.printStackTrace();
                log.warn("Cerrando conexion a base de datos", exc);
            }
            log.debug("Termina Aplicacion contable" + new Timestamp(System.currentTimeMillis()));
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

    private void avanzaCaso(HttpServletRequest req, Caso c, Usuario u, String prefixPath, String[] responsable, String[] nombre) throws GestionException, ServletException, IOException {
        //Método para avanzar el caso. Se utliza tanto para pre-compromiso como para compromiso
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

    private void apruebaContratoDiverso(String strParam, HttpServletRequest request, HttpServletResponse response) {
        String[] param = strParam.split(",");
        int outputValue = -1;
        int folio;
        String folioCaso = null;
        Connection conn = null;
        CallableStatement cmst = null;
        PrintWriter out = null;
        try {
            out = response.getWriter();
            conn = DataSourceManager.getConnection(jndiName);
            cmst = conn.prepareCall("{?= call pa_validaPedidoFinanciero (?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, param[0]);
            cmst.setString(3, param[1]);
            cmst.execute();
            outputValue = cmst.getInt(1);
            //caso de exito o con folio existente
            if (outputValue == 0 || outputValue == 4) {
                //conn.commit();
                if (log.isDebugEnabled())
                    log.debug("Iniciando Caso");
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
                cmst = conn.prepareCall("{?= call pa_apruebaPedido(?,?,?,?,?)}");
                cmst.registerOutParameter(1, Types.INTEGER);
                cmst.setString(2, param[0]);
                cmst.setString(3, param[1]);
                cmst.setString(4, param[2]);
                cmst.setString(5, folio + "");
                cmst.setString(6, folioCaso);
                cmst.execute();
                outputValue = cmst.getInt(1);
                if (outputValue == 0)
                    conn.commit();
                else
                    conn.rollback();
            } else
                conn.rollback();
        } catch (Exception e1) {
            outputValue = -1;
            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            e1.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (cmst != null)
                    cmst.close();
                try {
                    jsonObj.put("Col1", "" + outputValue);
                    String destino = arrayObj.put(jsonObj).toString();
                    out.println(destino);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
                exc.printStackTrace();
            }
            cmst = null;
            conn = null;
        }
    }

    private void devuelveContratoDiverso(String strParam, HttpServletResponse response) {
        String[] param = strParam.split(",");
        int outputValue = -1;
        Connection conn = null;
        CallableStatement cmst = null;
        PrintWriter out = null;
        try {
            out = response.getWriter();
            conn = DataSourceManager.getConnection(jndiName);
            cmst = conn.prepareCall("{?= call pa_devuelvePedido (?,?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, param[0]);
            cmst.setString(3, param[1]);
            cmst.setString(4, param[2]);
            cmst.setString(5, param[3]);
            cmst.execute();
            log.debug("pa_devuelvePedido " + param[0] + "," + param[1] + "," + param[2] + "," + param[3]);
            outputValue = cmst.getInt(1);
            if (outputValue == 0)
                conn.commit();
            else
                conn.rollback();
            System.out.println("Parametro de salida del procedimiento=" + outputValue);
        } catch (SQLException e1) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            outputValue = -1;
            e1.printStackTrace();
        } catch (IOException e) {
            outputValue = -1;
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (cmst != null)
                    cmst.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
                exc.printStackTrace();
            }
            try {
                jsonObj.put("Col1", "" + outputValue);
                String destino = arrayObj.put(jsonObj).toString();
                out.println(destino);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            cmst = null;
            conn = null;
        }
    }

    /*
	
	
	private void apruebaContratoDiverso(String strParam, HttpServletRequest request,HttpServletResponse response) {
		String param[] = strParam.split(",");
		int outputValue=-1;
		int folio;
		String folioCaso=null;
		try {
			out = response.getWriter();
			conn = DataSourceManager.getConnection(jndiName);
			cmst = conn.prepareCall("{?= call pa_validaPedidoFinanciero (?,?)}");
			cmst.registerOutParameter(1, Types.INTEGER);
			cmst.setString(2, param[0]);
			cmst.setString(3, param[1]);
			cmst.execute();
			outputValue = cmst.getInt(1);
			//caso de exito o con folio existente
			if (outputValue == 0 || outputValue==4){
				//conn.commit();
				if (log.isDebugEnabled())
					log.debug("Iniciando Caso");
					// Contrato Diverso
					//valor bandera para folio existente en el stored procedure
					folio=-1;
					//No tiene folio previo
					if( outputValue==0){
						//Genera un tipo de caso de Contrato Diverso
						Caso c = iniciaCaso(request,"9");
						folioCaso = c.getFolio();
						int indice = folioCaso.lastIndexOf('-') + 1;
						 folio = Integer.parseInt(folioCaso.substring(indice));
							//se avanza caso para que no se vea la operacion  en el inbox
						 String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
						 avanzaCaso(request, c, usuario, prefixPath, new String[] {"CONSULTA_CONTRATODIVERSO"}, new String[] {"consulta_contrato"});
						 
					}
					cmst = conn.prepareCall("{?= call pa_apruebaPedido(?,?,?,?,?)}");
					cmst.registerOutParameter(1, Types.INTEGER);
					cmst.setString(2, param[0]);
					cmst.setString(3, param[1]);
					cmst.setString(4, param[2]);
					cmst.setString(5, folio + "");
					cmst.setString(6, folioCaso);
					cmst.execute();
					outputValue = cmst.getInt(1);
					if (outputValue == 0)
						conn.commit();
						else
						conn.rollback();
					}
				else
					conn.rollback();
				
			} catch (Exception e1) {
				outputValue=-1;
				try {
					conn.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				e1.printStackTrace();
			} 
			finally {
				try {
					if (conn != null)
						conn.close();
					if (cmst != null)
						cmst.close();
					
					try {
						jsonObj.put("Col1", "" + outputValue);
						String destino = arrayObj.put(jsonObj).toString();
						out.println(destino);
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
								
				} catch (SQLException exc) {
					log.warn("Cerrando conexion a base de datos", exc);
				}
				cmst = null;
				conn = null;
			}
			
		}
		
	private void devuelveContratoDiverso(String strParam, HttpServletResponse response) {
		String param[] = strParam.split(",");
		int outputValue=-1;
		
		try {
			out = response.getWriter();
			conn = DataSourceManager.getConnection(jndiName);
			cmst = conn.prepareCall("{?= call pa_devuelvePedido (?,?,?,?)}");
			cmst.registerOutParameter(1, Types.INTEGER);
			cmst.setString(2, param[0]);
			cmst.setString(3, param[1]);
			cmst.setString(4, param[2]);
			cmst.setString(5, param[3]);
			cmst.execute();
			log.debug("pa_devuelvePedido " + param[0] + "," + param[1] + "," + param[2]+ ","+param[3]);
			outputValue = cmst.getInt(1);
			if (outputValue == 0)
				conn.commit();
			else
				conn.rollback();
			System.out.println("Parametro de salida del procedimiento="
					+ outputValue);
			
		} catch (SQLException e1) {
			try {
				conn.rollback();
			} catch (SQLException e) {
			e.printStackTrace();
			}
			outputValue=-1;
			e1.printStackTrace();
			}catch(IOException e){
			outputValue=-1;	
			e.printStackTrace();	
			}
		
		finally {
			try {
				if (conn != null)
					conn.close();
				if (cmst != null)
				cmst.close();	
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}
			try {
					jsonObj.put("Col1", "" + outputValue);
					String destino = arrayObj.put(jsonObj).toString();
					out.println(destino);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}		
				cmst = null;
				conn = null;	
		}
		
	}
	
	*/
    private void copiaPedido(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        int outputValue = -1;
        Connection conn = null;
        CallableStatement cmst = null;
        String link = "-1";
        try {
            out = response.getWriter();
            conn = DataSourceManager.getConnection(jndiName);
            cmst = conn.prepareCall("{?=call pa_CopiaPedidoContrato (?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, request.getParameter("cIdPedidoDefinitivo"));
            cmst.setString(3, "PEDIDO");
            cmst.setString(4, request.getParameter("ConsecutivoProcedimientoCopia"));
            cmst.execute();
            outputValue = cmst.getInt(1);
            if (outputValue == 0) {
                link = "1";
                conn.commit();
            } else
                conn.rollback();
        } catch (Exception e1) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // outputValue=-1;
            e1.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (cmst != null)
                    cmst.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
                exc.printStackTrace();
            }
            try {
                jsonObj.put("Col1", link);
                String destino = arrayObj.put(jsonObj).toString();
                out.println(destino);
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
            cmst = null;
            conn = null;
        }
    }

    private synchronized void generaGuardaCaso(HttpServletRequest request, HttpServletResponse response, HttpSession session, String tipoCaso, String CONCEPTO_MOV) throws ServletException, IOException {
        //Metodo utilizado para generar los casos de Precompromiso y Compromiso
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
                //Argumentos para llenar la tabla de CG_CASO_DATO y que se muestren en el inbox
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
            conn1 = null;
        }
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

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private synchronized Caso iniciaCaso(HttpServletRequest req, String tCaso) throws GestionException {
        //Este metodo es una copia del metodo del GestionServlet para iniciar casos
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

    private synchronized int liberaSaldo(HttpServletRequest request, Usuario usuario, Connection conn) {
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        AccountingEngine accEng = new AccountingEngine();
        CasoBusinessLogic cabl = new CasoBusinessLogic(jndiName);
        accEng.setValidaInsuficienciaDeSaldo(Boolean.TRUE);
        Statement stm = null, stm2 = null;
        ResultSet rs = null, rs2 = null;
        Caso c = new Caso();
        int resp = 10;
        try {
            stm = conn.createStatement();
            stm2 = conn.createStatement();
            //Obtiene el caso
            rs = stm.executeQuery("select " + "pd.nFolioPreCompromiso, ltrim(rtrim(pe.cUnidadResponsable)) as cUnidadResponsable " + "from tPrecompromisoEncabezado pe with(nolock) " + "inner join tPreCompromisoDetalle pd with(nolock) " + "on pe.nFolioPreCompromiso = pd.nFolioPreCompromiso " + "inner join mDocumentoFolio df with(nolock) " + "on df.ConsecutivoPRECOMP = pd.nFolioPreCompromiso " + "and df.cIdDocumentoDefinitivo = pe.cIdContrato " + "where " + "pe.cIdContrato = '" + request.getParameter("contratoDefinitivo") + "' and " + "pd.cEvento = 'PRECOMMAT_DISP' and " + "(pe.cDocumentoHaplicado = '' or pe.cDocumentoHaplicado is null) " + "group by " + "pd.nFolioPreCompromiso, pe.cUnidadResponsable");
            while (rs.next()) {
                String cIdUnidadResponsable = rs.getString("cUnidadResponsable");
                int nFolioPrecompromiso = rs.getInt("nFolioPreCompromiso");
                rs2 = stm2.executeQuery("select c.ID_CASO " + "from CG_CASO c with(nolock) " + "where C_FOLIO='PRCP-" + cIdUnidadResponsable + "-" + nFolioPrecompromiso + "'");
                while (rs2.next()) {
                    String ID_CASO = rs2.getString("ID_CASO");
                    c.setIdCaso(Integer.parseInt(ID_CASO));
                    c = CasoManager.select(conn, c);
                    if (accEng.makeAccountingApplication(conn, "PRECOMPROMISO", nFolioPrecompromiso + "", "tPrecompromisoEncabezado", "tPreCompromisoDetalle", "nFolioPreCompromiso")) {
                        Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                        cabl.avanzaCaso(c, usuario.getLogin(), "", new String[] { "CONSULTA_PRECOMPROMISO" }, new String[] { "consulta_precomp" }, m, prefixPath);
                        log.info("Se aplico contablemente el precompromiso:" + nFolioPrecompromiso);
                        Util.bitacoraMovimientos(request.getParameter("contratoDefinitivo"), "LIBERA_SALDO_PRECOMPROMISO FOLIO=" + nFolioPrecompromiso, usuario.getLogin(), conn);
                    }
                }
                if (rs2 != null) {
                    rs2.close();
                    rs2 = null;
                }
            }
            resp = 0;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            resp = 10;
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (rs2 != null)
                    rs2.close();
                if (stm != null)
                    stm.close();
                if (stm2 != null)
                    stm2.close();
            } catch (SQLException exc) {
                exc.printStackTrace();
            }
            stm = null;
            stm2 = null;
            rs = null;
            rs2 = null;
        }
        return resp;
    }

    private synchronized void liberaSaldo(HttpServletRequest request, Usuario usuario) {
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        AccountingEngine accEng = new AccountingEngine();
        CasoBusinessLogic cabl = new CasoBusinessLogic(jndiName);
        accEng.setValidaInsuficienciaDeSaldo(Boolean.TRUE);
        Connection conn = null;
        Statement stm = null, stm2 = null;
        ResultSet rs = null, rs2 = null;
        Caso c = new Caso();
        try {
            conn = DataSourceManager.getConnection(jndiName);
            stm = conn.createStatement();
            stm2 = conn.createStatement();
            //Obtiene el caso
            rs = stm.executeQuery("select " + "pd.nFolioPreCompromiso, ltrim(rtrim(pe.cUnidadResponsable)) as cUnidadResponsable " + "from tPrecompromisoEncabezado pe with(nolock) " + "inner join tPreCompromisoDetalle pd with(nolock) " + "on pe.nFolioPreCompromiso = pd.nFolioPreCompromiso " + "inner join mDocumentoFolio df with(nolock) " + "on df.ConsecutivoPRECOMP = pd.nFolioPreCompromiso " + "and df.cIdDocumentoDefinitivo = pe.cIdContrato " + "where " + "pe.cIdContrato = '" + request.getParameter("pedidoDefinitivo") + "' and " + "pd.cEvento = 'PRECOMMAT_DISP' and " + "(pe.cDocumentoHaplicado = '' or pe.cDocumentoHaplicado is null) " + "group by " + "pd.nFolioPreCompromiso, pe.cUnidadResponsable");
            while (rs.next()) {
                String cIdUnidadResponsable = rs.getString("cUnidadResponsable");
                int nFolioPrecompromiso = rs.getInt("nFolioPreCompromiso");
                rs2 = stm2.executeQuery("select c.ID_CASO " + "from CG_CASO c with(nolock) " + "where C_FOLIO='PRCP-" + cIdUnidadResponsable + "-" + nFolioPrecompromiso + "'");
                while (rs2.next()) {
                    String ID_CASO = rs2.getString("ID_CASO");
                    c.setIdCaso(Integer.parseInt(ID_CASO));
                    c = CasoManager.select(conn, c);
                    if (accEng.makeAccountingApplication(conn, "PRECOMPROMISO", nFolioPrecompromiso + "", "tPrecompromisoEncabezado", "tPreCompromisoDetalle", "nFolioPreCompromiso")) {
                        Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                        cabl.avanzaCaso(c, usuario.getLogin(), "", new String[] { "CONSULTA_PRECOMPROMISO" }, new String[] { "consulta_precomp" }, m, prefixPath);
                        log.info("Se aplico contablemente el precompromiso:" + nFolioPrecompromiso);
                    }
                }
                if (rs2 != null) {
                    rs2.close();
                    rs2 = null;
                }
            }
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (rs2 != null)
                    rs2.close();
                if (stm != null)
                    stm.close();
                if (stm2 != null)
                    stm2.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                exc.printStackTrace();
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
            stm = null;
            stm2 = null;
            rs = null;
            rs2 = null;
        }
    }
}
