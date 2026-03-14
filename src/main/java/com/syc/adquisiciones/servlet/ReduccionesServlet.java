package com.syc.adquisiciones.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import java.util.Base64;

@WebServlet(name = "ReduccionesServlet", urlPatterns = { "/servlet/ReduccionesServlet" })
public class ReduccionesServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static String jndiName = null;

    private static Logger log = LoggerFactory.getLogger(ReduccionesServlet.class);

    private Connection conn = null;

    private CallableStatement cmst = null;

    private JSONArray arrayObj;

    private JSONObject jsonObj;

    private PrintWriter out = null;

    private String folioGenerator = null;

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

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        // Just puts "destroy" string in log
        super.destroy();
        // Put your code here
    }

    /**
     * The doGet method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to get.
     *
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        HttpSession session = request.getSession(false);
        usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        int outputValue = 0;
        arrayObj = new JSONArray();
        jsonObj = new JSONObject();
        String DATE_FORMAT = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String today = sdf.format(c1.getTime());
        //Parametros del procedimiento almacenado
        String ramo = usuario.getU_Ramo();
        String uE = usuario.getU_UR();
        String[] centrosContables = request.getParameter("centrosContables").toString().trim().replace("|", "&").split("&");
        String[] numCompromisos = request.getParameter("numCompromisos").toString().trim().replace("|", "&").split("&");
        String[] tem;
        String folioCaso = "";
        int folio = 0;
        String cEjercicio = request.getParameter("cEjercicio");
        String cIdDocumentoDefinitivo = request.getParameter("cIdDocumentoDefinitivo");
        int consecutivoModificacion = Integer.parseInt(request.getParameter("cConsecutivoMod"));
        String centralizado = request.getParameter("centralizado");
        String tipo = request.getParameter("tipo");
        if (log.isDebugEnabled())
            log.debug("Iniciando Caso");
        try {
            out = response.getWriter();
            try {
                conn = DataSourceManager.getConnection(jndiName);
                Caso c;
                if (centralizado.toUpperCase().equals("DESCENTRALIZADO")) {
                    for (int i = 0; i < centrosContables.length; i++) {
                        tem = centrosContables[i].split("-");
                        c = iniciaCaso(request, "14", tem[0]);
                        folioCaso = c.getFolio();
                        log.info("Object: {}", "Folio caso: " + c.getFolio());
                        int indice = folioCaso.lastIndexOf('-') + 1;
                        folio = Integer.parseInt(folioCaso.substring(indice));
                        Map<String, String> datos = new HashMap<String, String>();
                        datos.put("FOLIO", folioCaso);
                        datos.put("FECHA_DOCUMENTO", today);
                        datos.put("EJERCICIO_FISCAL", (String) session.getAttribute(GestionInterface.ATT_PedidoEjercicio));
                        datos.put("OPERADOR", usuario.getNombre());
                        datos.put("MONEDA", "MXP");
                        datos.put("APLICADO_CONT", "false");
                        datos.put("CONCEPTO_MOV", "Reduccion de Compromiso");
                        CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), datos);
                        session.setAttribute(GestionInterface.ATT_CASE, c);
                        conn.commit();
                        cmst = conn.prepareCall("{?= call sp_mReduccionesCompromiso (?,?,?,?,?,?,?,?,?,?,?)}");
                        cmst.registerOutParameter(1, Types.INTEGER);
                        //@cEjercicio
                        cmst.setString(2, cEjercicio);
                        //@cIdDocumentoDefinitivo
                        cmst.setString(3, cIdDocumentoDefinitivo);
                        //@centroContable
                        cmst.setString(4, tem[1]);
                        //@ramo
                        cmst.setString(5, ramo);
                        //@cIdUnidadEjecutora
                        cmst.setString(6, tem[0]);
                        //@cFolio
                        cmst.setInt(7, folio);
                        //@nConsecutivoMod
                        cmst.setInt(8, consecutivoModificacion);
                        //@CENT
                        cmst.setString(9, centralizado);
                        //@caNoCompromiso
                        cmst.setString(10, numCompromisos[i]);
                        //@tipo
                        cmst.setString(11, tipo);
                        //@folio Caso
                        cmst.setString(12, folioCaso);
                        cmst.execute();
                        outputValue = cmst.getInt(1);
                        if (outputValue == 0) {
                            Caso sc = new Caso();
                            sc.setIdCaso(c.getIdCaso());
                            c = CasoManager.select(conn, sc);
                            String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
                            avanzaCaso(request, c, usuario, prefixPath, new String[] { "VENTANILLA_PRECOMPROMISO" }, new String[] { "autoriza_precomp" });
                            conn.commit();
                        } else {
                            conn.rollback();
                            break;
                        }
                    }
                } else {
                    c = iniciaCaso(request, "14", usuario.getU_UR());
                    folioCaso = c.getFolio();
                    log.info("Object: {}", "Folio caso: " + c.getFolio());
                    int indice = folioCaso.lastIndexOf('-') + 1;
                    folio = Integer.parseInt(folioCaso.substring(indice));
                    Map<String, String> datos = new HashMap<String, String>();
                    datos.put("FOLIO", folioCaso);
                    datos.put("FECHA_DOCUMENTO", today);
                    datos.put("EJERCICIO_FISCAL", (String) session.getAttribute(GestionInterface.ATT_PedidoEjercicio));
                    datos.put("OPERADOR", usuario.getNombre());
                    datos.put("MONEDA", "MXP");
                    datos.put("APLICADO_CONT", "false");
                    datos.put("CONCEPTO_MOV", "Reduccion de Compromiso");
                    CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), datos);
                    session.setAttribute(GestionInterface.ATT_CASE, c);
                    conn.commit();
                    cmst = conn.prepareCall("{?= call sp_mReduccionesCompromiso (?,?,?,?,?,?,?,?,?,?,?)}");
                    cmst.registerOutParameter(1, Types.INTEGER);
                    //@cEjercicio
                    cmst.setString(2, cEjercicio);
                    //@cIdDocumentoDefinitivo
                    cmst.setString(3, cIdDocumentoDefinitivo);
                    //@centroContable
                    cmst.setString(4, request.getParameter("centrosContables"));
                    //@ramo
                    cmst.setString(5, ramo);
                    //@cIdUnidadEjecutora
                    cmst.setString(6, uE);
                    //@cFolio
                    cmst.setInt(7, folio);
                    //@nConsecutivoMod
                    cmst.setInt(8, consecutivoModificacion);
                    //@CENT
                    cmst.setString(9, centralizado);
                    //@caNoCompromiso
                    cmst.setString(10, request.getParameter("numCompromisos"));
                    //@tipo
                    cmst.setString(11, tipo);
                    //@folio Caso
                    cmst.setString(12, folioCaso);
                    cmst.execute();
                    outputValue = cmst.getInt(1);
                    if (outputValue == 0) {
                        Caso sc = new Caso();
                        sc.setIdCaso(c.getIdCaso());
                        c = CasoManager.select(conn, sc);
                        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
                        avanzaCaso(request, c, usuario, prefixPath, new String[] { "VENTANILLA_PRECOMPROMISO" }, new String[] { "autoriza_precomp" });
                        conn.commit();
                    } else {
                        conn.rollback();
                    }
                }
            } catch (GestionException exc) {
                log.error("Iniciando Caso", exc);
                throw new ServletException(exc);
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
            try {
                jsonObj.put("Col1", "" + outputValue);
                String destino = arrayObj.put(jsonObj).toString();
                out.println(destino);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }

    private synchronized void aplicaContablemente2(HttpServletRequest request, HttpServletResponse response, HttpSession session, Caso c, String[] responsable, String[] nombre) throws ServletException, IOException {
        //Este método es una copia del método de financiero
        //Se hizo una copia para poder recibir el error
        session = request.getSession(false);
        out = response.getWriter();
        String validaSaldo = "";
        ArrayList<String> arrLResult = new ArrayList<String>();
        //Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
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
        Connection conn = null;
        AplicarContableReturn acr = null;
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        try {
            conn = cbl.getConnection();
            Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
            acr = conInt.aplicarContableNuevo(conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), validaSaldo);
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
            // Una vez que ha hecho la aplicación contable avanza el caso
            avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
            log.debug("Object: {}", c.getCasoDato("APLICADO_CONT").getValor());
        } catch (Exception e) {
            log.error("Error occurred", "Error en Aplicacion contable:" + e.getMessage());
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        log.debug("Object: {}", "Termina Aplicacion contable" + new Timestamp(System.currentTimeMillis()));
        try {
            //Regresa el mensaje de la aplicación contable para que sea mostrado en el JSP
            mensaje = !"".equals(mensaje) ? mensaje : arrLResult.get(0);
            jsonObj.put("Col1", mensaje);
            String destino = arrayObj.put(jsonObj).toString();
            out.println(destino);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    private synchronized Caso iniciaCaso(HttpServletRequest req, String tCaso, String ur) throws GestionException {
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
        c = casoTx.IniciaCaso(usuario.getLogin(), ur, idTC);
        if (c == null) {
            log.error("No se logro crear el caso");
            throw new GestionException("No se logró crear el caso");
        }
        ITree tree = casoTx.getArbolCaso(c);
        session.setAttribute(GestionInterface.ATT_TREE, tree);
        return c;
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

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
