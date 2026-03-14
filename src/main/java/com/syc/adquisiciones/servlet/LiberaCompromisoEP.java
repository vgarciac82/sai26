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
import com.syc.contable.CompromisoBussinessLogic;
import com.syc.contable.ContableInterface;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "LiberaCompromisoEP", urlPatterns = { "/servlet/LiberaCompromisoEP" })
public class LiberaCompromisoEP extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static String jndiName = null;

    private static Logger log = LoggerFactory.getLogger(PedidoServlet.class);

    private Connection conn = null;

    private Statement stm = null;

    private ResultSet rs = null;

    private CallableStatement cmst = null;

    private CallableStatement cmst1 = null;

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
        String folioCaso = "";
        String cIdSolicitud = request.getParameter("cIdSolicitud");
        int nIdLineaSolicitud = Integer.parseInt(request.getParameter("nIdLineaSolicitud"));
        String ejercicio = request.getParameter("cEjercicio");
        int folio = 0;
        HttpSession session = request.getSession(false);
        usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        int cCentroContable = Integer.parseInt(usuario.getPropiedad("CCENTROCONTABLE").getValor());
        int outputValue = 0;
        String ramo = usuario.getU_Ramo();
        String uE = usuario.getU_UR();
        arrayObj = new JSONArray();
        jsonObj = new JSONObject();
        String DATE_FORMAT = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String today = sdf.format(c1.getTime());
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        Connection conn1 = null;
        if (log.isDebugEnabled())
            log.debug("Iniciando Caso");
        try {
            out = response.getWriter();
            conn = cbl.getConnection();
            stm = conn.createStatement();
            rs = stm.executeQuery("select * from mSolicitudLineasApartado with(nolock) " + "where cIdSolicitud = '" + cIdSolicitud + "' and nIdLineaSolicitud = " + nIdLineaSolicitud + " " + "and (mes01+mes02+mes03+mes04+mes05+mes06+mes07+mes08+mes09+mes10+mes11+mes12) > 0");
            if (rs.next()) {
                conn1 = cbl.getConnection();
                Caso c = iniciaCaso(request, "25");
                folioCaso = c.getFolio();
                log.info("Object: {}", "Folio caso:" + c.getFolio());
                int indice = folioCaso.lastIndexOf('-') + 1;
                folio = Integer.parseInt(folioCaso.substring(indice));
                Map<String, String> datos = new HashMap<String, String>();
                datos.put("FOLIO", folioCaso);
                datos.put("FECHA_DOCUMENTO", today);
                datos.put("EJERCICIO_FISCAL", (String) session.getAttribute(GestionInterface.ATT_PedidoEjercicio));
                datos.put("OPERADOR", usuario.getNombre());
                datos.put("MONEDA", "MXP");
                datos.put("APLICADO_CONT", "false");
                datos.put("CONCEPTO_MOV", "Aplicación de PreCompromiso");
                CasoDatoManager.update(conn1, c.getIdTC(), c.getIdCaso(), datos);
                session.setAttribute(GestionInterface.ATT_CASE, c);
                cmst = conn.prepareCall("{?= call sp_mInsertaApartadoLineas (?,?,?,?,?,?)}");
                cmst.registerOutParameter(1, Types.INTEGER);
                cmst.setString(2, cIdSolicitud);
                cmst.setInt(3, nIdLineaSolicitud);
                cmst.setInt(4, folio);
                cmst.setInt(5, cCentroContable);
                cmst.setString(6, ramo);
                cmst.setString(7, uE);
                cmst.execute();
                outputValue = cmst.getInt(1);
                if (outputValue == 0) {
                    conn1.commit();
                    String respuesta = aplicaContablemente2(request, response, session, c, conn, folio, ejercicio, new String[] { "CONSULTA_APARTADO" }, new String[] { "CONSULTA_APTD" });
                    if (respuesta == "DOCUMENTO DE APARTADO APLICADO CONTABLEMENTE") {
                        jsonObj.put("Col1", "" + outputValue);
                        conn.commit();
                    } else {
                        conn.rollback();
                        conn1.rollback();
                    }
                } else {
                    conn.rollback();
                    conn1.rollback();
                    jsonObj.put("Col1", "" + -1);
                }
            } else
                jsonObj.put("Col1", "" + 9);
        } catch (Exception exc) {
            log.error("Iniciando Caso", exc);
            throw new ServletException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (conn1 != null)
                    conn.close();
                if (cmst != null)
                    cmst.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            String destino = arrayObj.put(jsonObj).toString();
            out.println(destino);
            cmst = null;
            conn = null;
            conn1 = null;
        }
    }

    public synchronized String aplicaContablemente2(HttpServletRequest request, HttpServletResponse response, HttpSession session, Caso c, Connection conn1, int folio, String ejercicio, String[] responsable, String[] nombre) throws ServletException, IOException {
        //Este método es una copia del método de financiero
        //Se hizo una copia para poder recibir el error
        session = request.getSession(false);
        Calendar year = Calendar.getInstance();
        String ejercicioFiscal = String.valueOf(year.get(Calendar.YEAR));
        out = response.getWriter();
        ArrayList<String> arrLResult = new ArrayList<String>();
        String mensaje = "";
        //Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        if (c == null) {
            response.sendRedirect("../index.jsp");
            return mensaje = "NO EXISTE CASO";
        }
        String cCentroContable = "";
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
        ////////////////////////////////////////////////////////////////////////////
        try {
            conn = cbl.getConnection();
            Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
            acr = conInt.aplicarContableNuevo(conn1, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), "");
            arrLResult = (ArrayList) acr.getMessageList();
            if (acr.isSuccess()) {
                //Confirma moviemientos contaboles correctos
                cmst1 = conn.prepareCall("{?= call pa_validaAplicacionContable (?,?,?,?,?)}");
                cmst1.registerOutParameter(1, Types.INTEGER);
                cmst1.setInt(2, folio);
                cmst1.setString(3, ejercicioFiscal);
                cmst1.setString(4, "S");
                cmst1.setString(5, "PR");
                cmst1.setString(6, "APARTADO");
                cmst1.execute();
                int outputValue = cmst1.getInt(1);
                if (outputValue == 0) {
                    //Recargando el caso por los cambios de la aplicacion contable (exitosa)
                    Caso sc = new Caso();
                    sc.setIdCaso(c.getIdCaso());
                    c = CasoManager.select(conn1, sc);
                    log.debug("Object: {}", c.getCasoDato("APLICADO_CONT").getValor());
                    avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                    log.debug("Object: {}", "Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
                    mensaje = "DOCUMENTO DE APARTADO APLICADO CONTABLEMENTE";
                    conn.commit();
                } else {
                    conn.rollback();
                    switch(outputValue) {
                        case 1:
                            mensaje = "EL DOCUMENTO NO SE APLICO CONTABLEMENTE (NO SE ENCONTRO EL REGISTRO DEL APARTADO).";
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
                mensaje = !"".equals(mensaje) ? mensaje : arrLResult.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mensaje = e.getMessage();
        } finally {
            try {
                if (cmst1 != null)
                    cmst1.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
            cmst1 = null;
        }
        return mensaje;
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
