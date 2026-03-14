package com.syc.adquisiciones.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.contable.CompromisoBussinessLogic;
import com.syc.contable.ContableInterface;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "PasivoServlet", urlPatterns = { "/servlet/PasivoServlet" })
public class PasivoServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 1L;

    private static String jndiName = null;

    private static Logger log = LoggerFactory.getLogger(PedidoServlet.class);

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
        cEjercicio = (String) session.getAttribute(GestionInterface.ATT_PedidoEjercicio);
        String DATE_FORMAT = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        today = sdf.format(c1.getTime());
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
        int tipoOperacion = Integer.parseInt(request.getParameter("operacion"));
        String strParam = request.getParameter("Param");
        switch(tipoOperacion) {
            case 0:
                /*
			String tipoPago = strParam.split(",")[4];
			if ("1".equalsIgnoreCase(tipoPago)) apruebaContratoDiversoDes(strParam,request, response);
			else apruebaContratoPasivoDiverso(strParam,request, response);
			break;
			*/
                apruebaContratoPasivoDiverso(strParam, request, response);
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
        }
        java.util.Date df = new java.util.Date();
        System.out.println("Finaliza time: " + df.toString());
        System.out.println("Segundos diferencia:  " + (df.getTime() - di.getTime()) / 1000);
    }

    private Caso getCaso(HttpSession session, String centroContable) {
        //Debido a que el Modulo de adquisiciones y servicios no se encuentra en un flujo pero se necesita crear un caso para el precompromiso
        // se creo este método que obtiene el ID de caso desde base de datos y no de sesión
        String ejercicio = (String) session.getAttribute(GestionInterface.ATT_PedidoEjercicio);
        String sql, tipo, ue, definitivo, subpartida;
        //int consecutivo;
        ejercicio = (String) session.getAttribute(GestionInterface.ATT_ContratoEjercicioPasivo);
        ue = (String) session.getAttribute(GestionInterface.ATT_ContratoUnidadEjecPasivo);
        definitivo = (String) session.getAttribute(GestionInterface.ATT_ContratoPasivoDefinitivo);
        //Apartir de los atributos guardados en sesión se hace un QRY para obtener el ID_CASO
        sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mDocumentoFolio p (NOLOCK), CG_CASO c (NOLOCK), CG_CASO_OPERACION o (NOLOCK)" + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cIdDocumentoDefinitivo=?" + " and p.cCentroContable=?";
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
            sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mDocumentoFolio p (NOLOCK), CG_CASO c (NOLOCK), CG_CASO_OPERACION o (NOLOCK) " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cIdDocumentoDefinitivo=?" + " and p.cCentroContable=?";
        } else {
            sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mPasivoContrato p (NOLOCK), CG_CASO c (NOLOCK), CG_CASO_OPERACION o (NOLOCK) " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cIdContratoDefinitivo=?";
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
        String ejercicio = (String) session.getAttribute(GestionInterface.ATT_PedidoEjercicio);
        String sql, ue, definitivo;
        ejercicio = (String) session.getAttribute(GestionInterface.ATT_ContratoEjercicioPasivo);
        ue = (String) session.getAttribute(GestionInterface.ATT_ContratoUnidadEjecPasivo);
        definitivo = (String) session.getAttribute(GestionInterface.ATT_ContratoPasivoDefinitivo);
        sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mPasivoContrato p (NOLOCK), CG_CASO c (NOLOCK), CG_CASO_OPERACION o (NOLOCK) " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cEjercicio=?" + " and p.cIdUnidadEjecutora=?" + " and p.cIdContratoDefinitivo=?";
        Caso c = null;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, ejercicio);
            pstmt.setString(2, ue);
            pstmt.setString(3, definitivo);
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
        log.debug("Object: {}", "Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()));
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        AplicarContableReturn acr = null;
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        try {
            conn = cbl.getConnection();
            conn1 = cbl.getConnection();
            Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
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
                    pstm = conn.prepareStatement("UPDATE mPasivoContrato SET nIdEstado = 3 , ConsecutivoPRECOMP = ?, C_FOLIO_PRE = ? " + " WHERE cIdContratoDefinitivo = ? ");
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
                    log.debug("Object: {}", c.getCasoDato("APLICADO_CONT").getValor());
                    log.debug("Object: {}", "Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
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
                    cmst1 = conn1.prepareCall("{call pa_fallaAplicacionContablePedidoContratoPasivo (?,?,?,?)}");
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
                cmst1 = conn1.prepareCall("{call pa_fallaAplicacionContablePedidoContratoPasivo (?,?,?,?)}");
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
        log.debug("Object: {}", "Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()));
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
                    pstm = conn.prepareStatement("UPDATE mPasivoContrato SET nIdEstado = 2 , ConsecutivoPRECOMP = null, C_FOLIO_PRE=null " + " WHERE cIdContratoDefinitivo = ? " + " and cEjercicio=?");
                    pstm.setString(1, request.getParameter("cContratoDefinitivo"));
                    pstm.setString(2, request.getParameter("cEjercicio"));
                    pstm.executeUpdate();
                    Caso sc = new Caso();
                    sc.setIdCaso(c.getIdCaso());
                    c = CasoManager.select(conn, sc);
                    // Una vez que ha hecho la aplicación contable avanza el caso
                    avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                    log.debug("Object: {}", c.getCasoDato("APLICADO_CONT").getValor());
                    log.debug("Object: {}", "Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
                    mensaje = "DOCUMENTO DE PRECOMPROMISO CANCELADO CONTABLEMENTE";
                    log.debug("Object: {}", "Termina Aplicacion contable" + new Timestamp(System.currentTimeMillis()));
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
                    pstm1 = conn1.prepareStatement("UPDATE mPasivoContrato SET nIdEstado = 3 " + " WHERE cIdContratoDefinitivo = ? " + " and cEjercicio=?");
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
                pstm1 = conn1.prepareStatement("UPDATE mPasivoContrato SET nIdEstado = 3 " + " WHERE cIdContratoDefinitivo = ? " + " and cEjercicio=?");
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
        log.debug("Object: {}", "Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()));
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
            String cIdContratoDefinitivo = (String) session.getAttribute(GestionInterface.ATT_ContratoPasivoDefinitivo);
            String queryCons = "select DISTINCT cIdEntidadContable,cIdUnidadEjecutora from tContratoEP_TMP with(Nolock) where cIdContratoDefinitivo='" + cIdContratoDefinitivo + "'";
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
                    log.debug("Object: {}", c.getCasoDato("APLICADO_CONT").getValor());
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
        log.debug("Object: {}", "Termina Aplicacion contable" + new Timestamp(System.currentTimeMillis()));
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

    @SuppressWarnings("unchecked")
    private synchronized void devuelveContablementeVentanilla(String strParam, HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre) throws ServletException, IOException {
        log.debug("Object: {}", "Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()));
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
                        log.debug("Object: {}", c.getCasoDato("APLICADO_CONT").getValor());
                        log.debug("Object: {}", "Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
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
            log.debug("Object: {}", "Termina Aplicacion contable" + new Timestamp(System.currentTimeMillis()));
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

    /*
	
	
	@SuppressWarnings("unchecked")
	private synchronized void devuelveContablementeVentanilla(String strParam, HttpServletRequest request, HttpServletResponse response,HttpSession session,String[] responsable, String[] nombre) throws ServletException, IOException {
		// la diferencia con el metodo de 'aplicaContablemente' es que hace una llamada de cancelación al motor contable que recibe diferentes parámetros
		session = request.getSession(false);
		out = response.getWriter();
		ArrayList<String> arrLResult = new ArrayList<String>();
		Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
		if (usuario == null) {
			response.sendRedirect("../index.jsp");
			return;
		}
		//Valida Centro de Costos
		String mensaje = "";
		
		ContableInterface conInt = new AplicacionContable();
		log.debug("Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()));
		CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
		Connection conncbl = null;
		Statement stmt=null;
		ResultSet rs=null;
		AplicarContableReturn acr=null;
		String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
		String cContableVentanilla = request.getParameter("cContableVentanilla");
		String definitivo = request.getParameter("contratoDefinitivo");
		String tipoPago = request.getParameter("tipoPago");
		
		
		try{
			conn = DataSourceManager.getConnection(jndiName);
			stmt = conn.createStatement();
			conncbl = cbl.getConnection();
			Caso c = getCaso(session, cContableVentanilla,definitivo,tipoPago);
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
						log.debug(c.getCasoDato("APLICADO_CONT").getValor());
					}
				
			
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
			conncbl=null;
			rs=null;
			stmt=null;
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

    private void apruebaContratoPasivoDiverso(String strParam, HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String[] param = strParam.split(",");
        try {
            out = response.getWriter();
            int outputValue;
            int folio;
            String folioCaso = null;
            conn = DataSourceManager.getConnection(jndiName);
            cmst = conn.prepareCall("{?= call pa_validaContratoPasivo (?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, param[0]);
            cmst.setString(3, param[1]);
            cmst.setString(4, param[2]);
            cmst.execute();
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
                    cmst = conn.prepareCall("{?= call pa_apruebaContratoPasivo(?,?,?,?,?,?)}");
                    cmst.registerOutParameter(1, Types.INTEGER);
                    cmst.setString(2, param[0]);
                    cmst.setString(3, param[1]);
                    cmst.setString(4, param[3]);
                    cmst.setString(5, folio + "");
                    cmst.setString(6, folioCaso);
                    cmst.setString(7, param[2]);
                    cmst.execute();
                    outputValue = cmst.getInt(1);
                    if (outputValue == 0)
                        conn.commit();
                    else
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

    /*
	private synchronized void apruebaContratoDiversoDes(String strParam, HttpServletRequest request,HttpServletResponse response) throws ServletException {
		// se utliza para generar contratos diversos
		String param[] = strParam.split(",");
		Statement stmt=null;
		try {
			out = response.getWriter();
			int outputValue = 0;
			conn = DataSourceManager.getConnection(jndiName);
			
			
			cmst = conn.prepareCall("{?= call pa_validaContratoPasivo (?,?,?)}");
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
			String queryCons = "select DISTINCT cIdEntidadContable,cIdUnidadEjecutora from tContratoEP_TMP with(Nolock) where cIdContratoDefinitivo='"+param[1]+"'" + " and cEjercicio='"+param[0]+"'" ;
			
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
					cmst = conn.prepareCall("{?= call pa_apruebaContratoPasivoDes(?,?,?,?,?,?,?)}");
					cmst.registerOutParameter(1, Types.INTEGER);
					cmst.setString(2, param[0]);
					cmst.setString(3, param[1]);
					cmst.setString(4, param[3]);
					cmst.setString(5, folio + "");
					cmst.setString(6, folioCaso);
					cmst.setString(7, param[2]);
					cmst.setString(8,centroContable);
					cmst.execute();
					cmst.execute();
					outputValue = cmst.getInt(1);
					if (outputValue == 0)
						conn.commit();
					else
						conn.rollback();
					
					
				} catch (GestionException exc) {
					log.error("Iniciando Caso", exc);
					throw new ServletException(exc);
				}
			}
			
			
			
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
    private void devuelveContratoPlurianualidadDiverso(String strParam, HttpServletResponse response) {
        String[] param = strParam.split(",");
        try {
            out = response.getWriter();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            conn = DataSourceManager.getConnection(jndiName);
            cmst = conn.prepareCall("{?= call pa_devuelveContratoPasivo(?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, param[0]);
            cmst.setString(3, param[1]);
            cmst.setString(4, param[2]);
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
}
