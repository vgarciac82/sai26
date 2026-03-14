package com.syc.adquisiciones.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
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
import com.syc.contable.CompromisoBussinessLogic;
import com.syc.contable.ContableInterface;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "MantenimientoLineasConsolidado", urlPatterns = { "/servlet/MantenimientoLineasConsolidado" })
public class MantenimientoLineasConsolidado extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 7569044431275478821L;

    private static String jndiName = null;

    private static Logger log = LoggerFactory.getLogger(MantenimientoLineasConsolidado.class);

    private JSONArray arrayObj;

    private JSONObject jsonObj;

    private PrintWriter out = null;

    private Usuario usuario;

    public void init(ServletConfig config) throws ServletException {
        //Crea la conexión a BD
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
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            log.warn("No se logro crear la sesion");
            throw new ServletException("No se logro crear la sesion");
        }
        usuario = (Usuario) session.getAttribute(ATT_USER);
        if (usuario == null) {
            log.warn("No hay Usuario en sesion");
            session.invalidate();
            response.sendRedirect("../index.jsp");
            return;
        }
        aplicaContablemente(request, response, session);
    }

    private synchronized void aplicaContablemente(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        //session = request.getSession(false);
        out = response.getWriter();
        String validaSaldo = "";
        ArrayList<String> arrLResult = new ArrayList<String>();
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        PreparedStatement pstm = null;
        CallableStatement cmst = null, cmst1 = null;
        Connection conn = null, conn1 = null;
        arrayObj = new JSONArray();
        jsonObj = new JSONObject();
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        String idCons = request.getParameter("idCons");
        int nIdLineaCons = Integer.parseInt(request.getParameter("nIdLineaCons"));
        int nFolioPrecom = Integer.parseInt(request.getParameter("nFolioPrecom"));
        String ramo = request.getParameter("ramo");
        String unidadE = request.getParameter("unidadE");
        int operacion = Integer.parseInt(request.getParameter("operacion"));
        if (c == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
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
        //String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        CasoBusinessLogic cb = new CasoBusinessLogic(jndiName);
        int outputValue1 = 0;
        try {
            conn = cbl.getConnection();
            conn1 = cbl.getConnection();
            //crea encabezado y detalle
            log.info("crea encabezado y detalle qye se va a cancelar o liberar de presupuesto");
            cmst1 = conn.prepareCall("{?= call sp_mInsertaApartadoLineasConsolidado (?,?,?,?,?,?,?)}");
            if (operacion == 3) {
                //Cancela el precompromiso de todas las lineas del consolidado que no se adjudicaron
                cmst1 = conn.prepareCall("{?= call sp_mLiberaProcomMaterialesLineasSinAdjudicar (?,?,?,?,?,?,?)}");
            }
            cmst1.registerOutParameter(1, Types.INTEGER);
            //@cIdConsolidado
            cmst1.setString(2, idCons);
            ////@nIdLineaConsolidado
            cmst1.setInt(3, nIdLineaCons);
            //@nFolioPrecom
            cmst1.setInt(4, nFolioPrecom);
            //@centroContable
            cmst1.setString(5, cCentroContable);
            //@ramo
            cmst1.setString(6, ramo);
            //@cIdUnidadEjecutora
            cmst1.setString(7, unidadE);
            //@operacion
            cmst1.setInt(8, operacion);
            cmst1.execute();
            outputValue1 = cmst1.getInt(1);
            //Aplicar contablemente
            System.out.println("Valor devuelto por el procedimiento almacenado para crear encabezado y detalle :" + outputValue1);
            if (outputValue1 == 0) {
                Map<String, String> m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                acr = conInt.aplicarContableNuevo(conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), validaSaldo);
                if (acr.isSuccess()) {
                    log.debug("Object: {}", nFolioPrecom);
                    cmst = conn.prepareCall("{?= call pa_validaAplicacionContable (?,?,?,?,?)}");
                    cmst.registerOutParameter(1, Types.INTEGER);
                    cmst.setInt(2, nFolioPrecom);
                    cmst.setString(3, request.getParameter("cEjercicio"));
                    cmst.setString(4, "S");
                    cmst.setString(5, "PR");
                    cmst.setString(6, "PRECOMMATERIALES");
                    cmst.execute();
                    int outputValue = cmst.getInt(1);
                    if (outputValue == 0) {
                        jsonObj.put("Aplica", "1");
                        // Una vez que ha hecho la aplicación contable avanza el caso
                        //////////////////////////////////////////
                        cb.avanzaCaso(c, usuario.getLogin(), "", new String[] { "CONSULTA_PRECOMPROMISO" }, new String[] { "consulta_precomp" }, m, prefixPath);
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
                    }
                } else {
                    conn.rollback();
                    jsonObj.put("Aplica", "0");
                    mensaje = !"".equals(mensaje) ? mensaje : arrLResult.get(0);
                }
            } else {
                conn1.rollback();
                jsonObj.put("Contable1", mensaje);
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
        }
    }
}
