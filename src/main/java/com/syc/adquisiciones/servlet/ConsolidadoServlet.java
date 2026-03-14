package com.syc.adquisiciones.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.fileupload2.core.FileUploadException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.adquisiciones.util.Util;
import com.syc.contable.CompromisoBussinessLogic;
import com.syc.contable.ContableInterface;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.dsmngr.DataSourceManager;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.CasoOperacionBusinessLogic;
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

@WebServlet(name = "ConsolidadoServlet", urlPatterns = { "/servlet/ConsolidadoServlet" })
public class ConsolidadoServlet extends HttpServlet implements GestionInterface {

    /**
     */
    private static final long serialVersionUID = 3222444087267831621L;

    private String tempDir = null;

    private static String jndiName = null;

    private static Logger log = LoggerFactory.getLogger(ConsolidadoServlet.class);

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
        tempDir = config.getInitParameter("tempDir");
        if (tempDir == null) {
            tempDir = config.getServletContext().getRealPath("/") + "upload" + File.separator;
            File fDir = new File(tempDir);
            if (!fDir.exists())
                if (!fDir.mkdirs())
                    throw new ServletException("No se pudo crear el directorio " + tempDir);
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
        cEjercicio = (String) session.getAttribute(GestionInterface.ATT_ConEjercicio);
        if (cEjercicio == null) {
            if (request.getParameter("cEjercicio") != null) {
                cEjercicio = request.getParameter("cEjercicio");
            }
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
        if (request.getParameter("aprobarFechaVigencia") != null) {
            aprobarRechazarVigencia(request, response, session);
            return;
        }
        if (request.getParameter("nuevaFecha") != null) {
            ampliacionVigencia(request, response, session);
            return;
        }
        if (request.getParameter("requisicion") != null) {
            try {
                CasoBusinessLogic cbl = new CasoBusinessLogic(jndiName);
                Connection conn = null;
                conn = cbl.getConnection();
                String tipo, ue;
                int consecutivo;
                //Datos del consolidado
                tipo = (String) session.getAttribute(GestionInterface.ATT_ConTipoConsolidado);
                ue = (String) session.getAttribute(GestionInterface.ATT_ConUnidadEjec);
                consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_ConConsecutivo));
                PreparedStatement pstm = null;
                Caso c = null;
                log.debug("Object: {}", "folio " + request.getParameter("nFolioAmpliacion"));
                if (request.getParameter("nFolioAmpliacion") != "") {
                    c = cbl.getCaso(Integer.parseInt(request.getParameter("nFolioAmpliacion")));
                } else {
                    c = iniciaCaso(request, GestionInterface.IDTC_APARTADO + "");
                    String folioCaso = c.getFolio();
                    int indice = folioCaso.lastIndexOf('-') + 1;
                    int folio = Integer.parseInt(folioCaso.substring(indice));
                    try {
                        jsonObj.put("Folio1", "" + folio);
                        jsonObj.put("Folio2", "" + folioCaso);
                        log.debug("Object: {}", "Folio+++++++++++++++++++" + folio + "++++++++++++++++++++++++++++");
                        log.debug("Object: {}", "Folio+++++++++++++++++++" + folioCaso + "++++++++++++++++++++++++++++");
                        String destino = arrayObj.put(jsonObj).toString();
                        Map<String, String> datos = new HashMap<String, String>();
                        //Argumentos para llenar la tabla de CG_CASO_DATO y que se muestren en el inbox
                        datos.put("FOLIO", folioCaso);
                        datos.put("FECHA_DOCUMENTO", today);
                        datos.put("EJERCICIO_FISCAL", cEjercicio);
                        datos.put("OPERADOR", usuario.getNombre());
                        datos.put("MONEDA", "MXP");
                        datos.put("APLICADO_CONT", "false");
                        datos.put("CONCEPTO_MOV", "Ampliacion requisiciones consolidadas");
                        conn1 = DataSourceManager.getConnection(jndiName);
                        CasoDatoManager.update(conn1, c.getIdTC(), c.getIdCaso(), datos);
                        //	session.setAttribute(GestionInterface.ATT_CASE, c);
                        conn1.commit();
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    } catch (SQLException eSQL) {
                        eSQL.printStackTrace();
                    }
                }
                if (c.getIdGabinete() == -1)
                    c.setIdGabinete(cbl.creaExpediente(usuario.getLogin(), c));
                List fileItems = parseRequest(request, session.getId() + File.separator);
                Iterator i = fileItems.iterator();
                String docName = null;
                while (i.hasNext()) {
                    FileItem item = (FileItem) i.next();
                    if (item.isFormField())
                        if ("nombre".equals(item.getFieldName())) {
                            docName = item.getString();
                            break;
                        }
                }
                i = fileItems.iterator();
                while (i.hasNext()) {
                    FileItem item = (FileItem) i.next();
                    if (item.isFormField())
                        continue;
                    try {
                        String tmpFile = (new File(item.getName())).getName();
                        int pos = tmpFile.indexOf(".") != -1 ? tmpFile.lastIndexOf('.') + 1 : -1;
                        int pos1 = 0;
                        String ext = pos != -1 ? tmpFile.substring(pos) : "";
                        //Fortimax fimx = new Fortimax(select);
                        pos = tmpFile.lastIndexOf('.');
                        pos1 = tmpFile.lastIndexOf('\\') + 1;
                        String filename = tmpFile.substring(pos1, pos);
                        cbl.recibeDocumentoGestionRequisicion(c, "vigencias", ext, new DataInputStream(item.getInputStream()));
                        item.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (c != null) {
                    int indice = c.getFolio().lastIndexOf('-') + 1;
                    int folio = Integer.parseInt(c.getFolio().substring(indice));
                    pstm = conn.prepareStatement("UPDATE mConsolidado SET FOLIO_AMPLIACION = ?, consecutivoAmpliacion=?" + " WHERE cIdTipoConsolidado = ? " + " and cIdUnidadEjecutora = ?" + " and nIdConsecutivo = ?" + " and cEjercicio=?");
                    pstm.setString(1, c.getFolio());
                    pstm.setInt(2, folio);
                    pstm.setString(3, tipo);
                    pstm.setString(4, ue);
                    pstm.setInt(5, consecutivo);
                    pstm.setString(6, request.getParameter("cEjercicio"));
                    pstm.executeUpdate();
                    avanzaCaso(request, response, session, c, new String[] { "VENTANILLA_APARTADO" }, new String[] { "VIGENCIA_CONSOLIDADO" });
                    conn.commit();
                } else {
                    return;
                }
            } catch (GestionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return;
        }
        //Obtiene la operación que se manda como parámetro en la llamada GET
        int tipoOperacion = Integer.parseInt(request.getParameter("operacion"));
        log.debug("Object: {}", "operacion: " + tipoOperacion);
        String strParam = request.getParameter("Param");
        switch(tipoOperacion) {
            case 1:
                //El preCompromiso es un tipo de caso 14
                generaGuardaCaso(request, response, session, (GestionInterface.IDTC_PRECOMMATERIALES + ""), "Aplicación de PreCompromiso Materiales");
                break;
            case 2:
                aplicaContablemente(strParam, request, response, session, new String[] { "CONSULTA_PRECOMPROMISO" }, new String[] { "consulta_precomp" });
                break;
            case 3:
                devuelveContablemente(strParam, request, response, session);
                break;
            case 4:
                avanzaCaso(request, response, session, new String[] { "CONSULTA_APARTADO" }, new String[] { "CONSULTA_APTD" });
                break;
            case 5:
                nuevoAutomatico(request, response, session);
                break;
            case 6:
                generaPartidasUnoAuno(request, response);
                break;
        }
        java.util.Date df = new java.util.Date();
        System.out.println("Finaliza time: " + df.toString());
        System.out.println("Segundos diferencia:  " + (df.getTime() - di.getTime()) / 1000);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private void generaPartidasUnoAuno(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String cIdConsolidado = request.getParameter("cIdConsolidado");
            try {
                out = response.getWriter();
                CallableStatement cmst = null;
                conn = DataSourceManager.getConnection(jndiName);
                log.debug("llama el storeProcedure :pa_mAgregaPartidaUnoAuno");
                cmst = conn.prepareCall("{call pa_mAgregaPartidaUnoAuno (?,?)}");
                cmst.setString(1, cIdConsolidado);
                cmst.registerOutParameter(2, Types.INTEGER);
                cmst.execute();
                int outputValue = cmst.getInt(2);
                jsonObj.put("respuesta", "" + outputValue);
                if (outputValue == 0) {
                    conn.commit();
                } else {
                    conn.rollback();
                }
                String repuesta = arrayObj.put(jsonObj).toString();
                out.println(repuesta);
            } catch (JSONException e1) {
                e1.printStackTrace();
                log.error("Error occurred", "Error de json:" + e1);
            } catch (SQLException eSQL) {
                eSQL.printStackTrace();
                log.error("Error occurred", "Error ejecutando store procedure: pa_mAgregaPartidaUnoAuno" + eSQL);
                conn.rollback();
            } finally {
                if (cmst != null) {
                    cmst.close();
                }
                if (conn != null) {
                    conn.close();
                }
                cmst = null;
                conn = null;
            }
        } catch (Exception e) {
            // TODO: handle exception
            log.error("Error occurred", "Error generando partidas de consolidado uno a uno. " + e);
            e.printStackTrace();
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
                log.debug("Object: {}", "Folio+++++++++++++++++++" + folio + "++++++++++++++++++++++++++++");
                log.debug("Object: {}", "Folio+++++++++++++++++++" + folioCaso + "++++++++++++++++++++++++++++");
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

    private synchronized void aplicaContablemente(String strParam, HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre) throws ServletException, IOException {
        //Este método es una copia del método de financiero
        //Se hizo una copia para poder recibir el error
        session = request.getSession(false);
        out = response.getWriter();
        String validaSaldo = "";
        ArrayList<String> arrLResult = new ArrayList<String>();
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        PreparedStatement pstm = null;
        CallableStatement cmst = null, cmst1 = null, cmst2 = null;
        Connection conn = null, conn1 = null;
        String tipo, ue, cIdConsolidado;
        int consecutivo;
        int partidas;
        partidas = Integer.parseInt(request.getParameter("partidas"));
        //Datos del consolidado
        tipo = (String) session.getAttribute(GestionInterface.ATT_ConTipoConsolidado);
        ue = (String) session.getAttribute(GestionInterface.ATT_ConUnidadEjec);
        consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_ConConsecutivo));
        cIdConsolidado = tipo + '-' + ue + '-' + consecutivo;
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
        log.debug("Object: {}", "Inicia aplicacion presupuestal" + new Timestamp(System.currentTimeMillis()));
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        AplicarContableReturn acr = null;
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        try {
            conn = cbl.getConnection();
            //Valida Cuenta del disponible
            String cNumCuentaDisponible = cuantaDisponible(conn, cIdConsolidado);
            if ("-1".equals(cNumCuentaDisponible)) {
                conn.rollback();
                jsonObj.put("Aplica", "0");
                mensaje = "Hay incongruencia en la cuaenta del disponible de cada requisicion. No puedes combinar recurso fiscal y radicado.";
                return;
            } else {
                //Actualiza la cuenta del disponible
                actualizaCuantaDisponible(conn, cIdConsolidado, cNumCuentaDisponible);
            }
            conn1 = cbl.getConnection();
            Map<String, String> m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
            acr = conInt.aplicarContableNuevo(conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), validaSaldo);
            arrLResult = (ArrayList) acr.getMessageList();
            if (acr.isSuccess()) {
                log.debug("Object: {}", request.getParameter("nFolioPrecompromiso"));
                cmst = conn.prepareCall("{?= call pa_validaAplicacionContable (?,?,?,?,?)}");
                cmst.registerOutParameter(1, Types.INTEGER);
                cmst.setInt(2, Integer.parseInt(request.getParameter("nFolioPrecompromiso")));
                cmst.setString(3, request.getParameter("cEjercicio"));
                cmst.setString(4, "S");
                cmst.setString(5, "PR");
                cmst.setString(6, "PRECOMMATERIALES");
                cmst.execute();
                int outputValue = cmst.getInt(1);
                if (outputValue == 0) {
                    jsonObj.put("Aplica", "1");
                    pstm = conn.prepareStatement("UPDATE mConsolidado SET nIdEstado = 2 , ConsecutivoPRECOMP = ?, C_FOLIO_PRE = ? " + " WHERE cIdTipoConsolidado = ? " + " and cIdUnidadEjecutora = ?" + " and nIdConsecutivo = ?" + " and cEjercicio=?");
                    log.debug("Object: {}", "folio " + request.getParameter("nFolioPrecompromiso"));
                    log.debug("Object: {}", "folio " + request.getParameter("folioCasoPreCompromiso"));
                    pstm.setString(1, request.getParameter("nFolioPrecompromiso"));
                    pstm.setString(2, request.getParameter("folioCasoPreCompromiso"));
                    pstm.setString(3, tipo);
                    pstm.setString(4, ue);
                    pstm.setInt(5, consecutivo);
                    pstm.setString(6, request.getParameter("cEjercicio"));
                    pstm.executeUpdate();
                    //registra la vigencia del precompromiso
                    cmst1 = conn.prepareCall("{call sp_mVigenciaPrecompromiso (?,?,?,?,?)}");
                    cmst1.setString(1, request.getParameter("cEjercicio"));
                    cmst1.setString(2, ue);
                    cmst1.setString(3, cIdConsolidado);
                    cmst1.setString(4, request.getParameter("folioCasoPreCompromiso"));
                    cmst1.setDate(5, Date.valueOf("2014-01-01"));
                    cmst1.execute();
                    //inserta en las tablas de mPartidasPrecompromiso
                    if (partidas == 1) {
                        log.debug("Object: {}", "cidconsolidado " + cIdConsolidado);
                        cmst2 = conn.prepareCall("{call sp_mPartidasPrecompromiso (?)}");
                        cmst2.setString(1, cIdConsolidado);
                        cmst2.execute();
                    }
                    //Recargando el caso
                    Caso sc = new Caso();
                    sc.setIdCaso(c.getIdCaso());
                    c = CasoManager.select(conn, sc);
                    // Una vez que ha hecho la aplicación contable avanza el caso A CONSULTA PAGOS
                    //////////////////////////////////////////
                    avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                    //////////////////////////////////////////
                    log.debug("Object: {}", c.getCasoDato("APLICADO_CONT").getValor());
                    log.debug("Object: {}", "Termina Aplicacion presupuestal " + new Timestamp(System.currentTimeMillis()));
                    mensaje = "DOCUMENTO DE PRECOMPROMISO APLICADO PRESUPUESTALMENTE.";
                    conn.commit();
                } else {
                    conn.rollback();
                    jsonObj.put("Aplica", "0");
                    switch(outputValue) {
                        case 1:
                            mensaje = "EL DOCUMENTO NO SE APLICO PRESUPUESTALMENTE (NO SE ENCONTRO EL REGISTRO DEL PRECOMPROMISO).";
                            break;
                        case 2:
                            mensaje = "EL DOCUMENTO NO SE APLICO PRESUPUESTALMENTE (NO EXISTE NINGUN REGISTRO DE POLIZA DEL DOCUMENTO).";
                            break;
                        case 3:
                            mensaje = "EL DOCUMENTO NO SE APLICO PRESUPUESTALMENTE (LOS MOVIMIENTOS DE LA AFECTACION PRESUPUESTAL NO ESTAN COMPLETOS).";
                            break;
                        default:
                            mensaje = "ERROR INESPERADO EN LA CONFIRMACION DE LA APLICACION PRESUPUESTAL.";
                    }
                    cmst1 = conn1.prepareCall("{call pa_fallaAplicacionContableConsolidado (?,?,?,?,?,?)}");
                    cmst1.setString(1, tipo);
                    cmst1.setString(2, ue);
                    cmst1.setInt(3, consecutivo);
                    cmst1.setString(4, request.getParameter("cEjercicio"));
                    cmst1.setString(5, request.getParameter("nFolioPrecompromiso"));
                    cmst1.setInt(6, partidas);
                    cmst1.execute();
                    conn1.commit();
                }
            } else {
                conn.rollback();
                jsonObj.put("Aplica", "0");
                cmst1 = conn1.prepareCall("{call pa_fallaAplicacionContableConsolidado (?,?,?,?,?,?)}");
                cmst1.setString(1, tipo);
                cmst1.setString(2, ue);
                cmst1.setInt(3, consecutivo);
                cmst1.setString(4, request.getParameter("cEjercicio"));
                cmst1.setString(5, request.getParameter("nFolioPrecompromiso"));
                cmst1.setInt(6, partidas);
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
            log.error("Error occurred", "Error en Aplicacion Presupuestal:" + e.getMessage());
            mensaje = "ERROR INESPERADO DE LA APLICACION PRESUPUESTAL(MOTOR CONTABLE O NO AVANZO EL CASO).";
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

    private synchronized void devuelveContablemente(String strParam, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        session = request.getSession(false);
        out = response.getWriter();
        //String validaSaldo="";
        ArrayList<String> arrLResult = new ArrayList<String>();
        PreparedStatement pstm = null, pstm1 = null, pstm2 = null, pstm3 = null;
        CallableStatement cmst = null;
        Connection conn = null, conn1 = null;
        AplicarContableReturn acr = null;
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        String tipo, ue, idConsolidado;
        int consecutivo;
        //Datos del consolidado
        tipo = (String) session.getAttribute(GestionInterface.ATT_ConTipoConsolidado);
        ue = (String) session.getAttribute(GestionInterface.ATT_ConUnidadEjec);
        consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_ConConsecutivo));
        int partidas = Integer.parseInt(request.getParameter("partidas"));
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
            mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Presupuestal, Consulte a su administrador.";
        }
        ContableInterface conInt = new AplicacionContable();
        log.debug("Object: {}", "Inicia aplicacion presupuestal" + new Timestamp(System.currentTimeMillis()));
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        try {
            conn = cbl.getConnection();
            conn1 = cbl.getConnection();
            Caso c;
            if ((c = (Caso) session.getAttribute(GestionInterface.ATT_CASE)) == null)
                c = getCaso(session);
            if (c == null) {
                log.error("Error en Aplicacion presupuestal:");
                return;
            }
            Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
            acr = conInt.cancelarAppContableNueva(conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), "");
            arrLResult = (ArrayList) acr.getMessageList();
            if (acr.isSuccess()) {
                log.debug("Object: {}", "ejercicio " + request.getParameter("cEjercicio"));
                log.debug("Object: {}", "folio " + request.getParameter("nFolioPrecompromiso"));
                cmst = conn.prepareCall("{?= call pa_validaAplicacionContable (?,?,?,?,?)}");
                cmst.registerOutParameter(1, Types.INTEGER);
                cmst.setInt(2, Integer.parseInt(request.getParameter("nFolioPrecompromiso")));
                cmst.setString(3, request.getParameter("cEjercicio"));
                cmst.setString(4, "C");
                cmst.setString(5, "PR");
                cmst.setString(6, "PRECOMMATERIALES");
                cmst.execute();
                int outputValue = cmst.getInt(1);
                if (outputValue == 0) {
                    jsonObj.put("Devuelve", "0");
                    int estado;
                    log.debug("Object: {}", partidas);
                    if (partidas == 0) {
                        // no devuelve a capturado, solo elimina folios, el precompromiso viene de disponible
                        estado = 2;
                    } else {
                        // se cambia el estado a capturado, el precompromiso viene de un apartado
                        estado = 1;
                    }
                    pstm = conn.prepareStatement("UPDATE mConsolidado SET nIdEstado=?,  ConsecutivoPRECOMP = null, C_FOLIO_PRE=null " + " WHERE cIdTipoConsolidado = ? " + " and cIdUnidadEjecutora = ?" + " and nIdConsecutivo = ?" + " and cEjercicio=?");
                    pstm.setInt(1, estado);
                    pstm.setString(2, tipo);
                    pstm.setString(3, ue);
                    pstm.setInt(4, consecutivo);
                    pstm.setString(5, request.getParameter("cEjercicio"));
                    pstm.executeUpdate();
                    // cuando el consolidado se devuelve las vigencias de los apartados se actualiza 5 dias mas a partir del dia de la devolucion
                    if (partidas == 1) {
                        // el precompromiso viene de un apartado
                        idConsolidado = tipo + '-' + ue + '-' + consecutivo;
                        pstm2 = conn.prepareCall("{call sp_mActualizaVigenciaApartado (?)}");
                        pstm2.setString(1, idConsolidado);
                        pstm2.execute();
                        pstm3 = conn.prepareStatement("DELETE FROM mPartidasPrecompromiso where cIdConsolidado=?");
                        pstm3.setString(1, idConsolidado);
                        pstm3.executeUpdate();
                    }
                    log.debug("Object: {}", "Termina Aplicacion Presupuestal " + new Timestamp(System.currentTimeMillis()));
                    mensaje = "DOCUMENTO DE PRECOMPROMISO CANCELADO PRESUPUESTALMENTE";
                    log.debug("Object: {}", "Termina Aplicacion presupuestal" + new Timestamp(System.currentTimeMillis()));
                    conn.commit();
                } else {
                    conn.rollback();
                    switch(outputValue) {
                        case 1:
                            mensaje = "EL DOCUMENTO NO SE CANCELO PRESUPUESTALMENTE (NO SE ENCONTRO EL REGISTRO DEL PRECOMPROMISO).FAVOR DE INTENTAR NUEVAMENTE";
                            break;
                        case 2:
                            mensaje = "EL DOCUMENTO NO SE CANCELO PRESUPUESTALMENTE (NO EXISTE NINGUN REGISTRO DE POLIZA DEL DOCUMENTO).FAVOR DE INTENTAR NUEVAMENTE";
                            break;
                        case 3:
                            mensaje = "EL DOCUMENTO NO SE CANCELO PRESUPUESTALMENTE (LOS MOVIMIENTOS DE LA AFECTACION PRESUPUESTAL NO ESTAN COMPLETOS).FAVOR DE INTENTAR NUEVAMENTE";
                            break;
                        default:
                            mensaje = "ERROR INESPERADO EN LA CONFIRMACION DE LA CANCELACION PRESUPUESTAL.FAVOR DE INTENTAR NUEVAMENTE";
                    }
                    jsonObj.put("Devuelve", "1");
                    //hubo un error al aplicar contablemente ,actualiza estado de la tabla de consolidado
                    pstm1 = conn1.prepareStatement("UPDATE mConsolidado SET nIdEstado = 2 " + " WHERE cIdTipoConsolidado = ? " + " and cIdUnidadEjecutora = ?" + " and nIdConsecutivo = ?" + " and cEjercicio=?");
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
                pstm1 = conn1.prepareStatement("UPDATE mConsolidado SET nIdEstado = 2 " + " WHERE cIdTipoConsolidado = ? " + " and cIdUnidadEjecutora = ?" + " and nIdConsecutivo = ?" + " and cEjercicio=?");
                pstm1.setString(1, tipo);
                pstm1.setString(2, ue);
                pstm1.setInt(3, consecutivo);
                pstm1.setString(4, request.getParameter("cEjercicio"));
                pstm1.executeUpdate();
                conn1.commit();
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error en Aplicacion Presupuestal:" + e.getMessage());
            try {
                jsonObj.put("Devuelve", "0");
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
            mensaje = "ERROR INESPERADO DE LA CANCELACION PRESUPUESTAL(MOTOR CONTABLE O NO AVANZO EL CASO).FAVOR DE INTENTAR NUEVAMENTE";
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

    private Caso getCaso(HttpSession session) {
        //Debido a que el Modulo de adquisiciones y servicios no se encuentra en un flujo pero se necesita crear un caso para el precompromiso
        // se creo este método que obtiene el ID de caso desde base de datos y no de sesión
        String ejercicio = (String) session.getAttribute(GestionInterface.ATT_ConEjercicio);
        String sql, tipo, ue;
        int consecutivo;
        tipo = (String) session.getAttribute(GestionInterface.ATT_ConTipoConsolidado);
        ue = (String) session.getAttribute(GestionInterface.ATT_ConUnidadEjec);
        consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_ConConsecutivo));
        //Apartir de los atributos guardados en sesión se hace un QRY para obtener el ID_CASO
        sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mConsolidado p, CG_CASO c, CG_CASO_OPERACION o " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cEjercicio=?" + " and p.cIdTipoConsolidado=?" + " and p.cIdUnidadEjecutora=?" + " and p.nIdConsecutivo=?";
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
                log.debug("Object: {}", "idCaso " + idCaso);
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
            log.error("Error occurred", "Error en Aplicacion Presupuestal:" + e.getMessage());
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

    private synchronized void aprobarRechazarVigencia(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws ServletException, IOException {
        String aprobar = (String) req.getParameter("aprobarFechaVigencia");
        try {
            CasoOperacionBusinessLogic cobl = new CasoOperacionBusinessLogic("jdbc/gestion");
            Connection conn = null;
            conn = cobl.getConnection();
            AplicacionManager.aprobarRechazarVigencia(conn, req.getParameter("folio"), aprobar.equals("1") ? true : false, req.getParameter("motivo"));
            AplicacionManager.setIdOperacionVigenciaPrecompromiso(conn, req.getParameter("folio"), 3, "CONSULTA_PRECOMPROMISO");
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            throw new ServletException();
        }
    }

    private synchronized void ampliacionVigencia(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        //Metodo utilizado para generar los casos de Precompromiso y Compromiso
        out = response.getWriter();
        Caso c = null;
        Connection conn = null;
        CasoBusinessLogic cbl = new CasoBusinessLogic(jndiName);
        String tipo, ue, idConsolidado;
        int consecutivo;
        //Datos del consolidado
        tipo = (String) session.getAttribute(GestionInterface.ATT_ConTipoConsolidado);
        ue = (String) session.getAttribute(GestionInterface.ATT_ConUnidadEjec);
        consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_ConConsecutivo));
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
            mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Presupuestal, Consulte a su administrador.";
        }
        ContableInterface conInt = new AplicacionContable();
        log.debug("Object: {}", "Inicia aplicacion presupuestal" + new Timestamp(System.currentTimeMillis()));
        //	CompromisoBussinessLogic cbl =new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        try {
            conn = cbl.getConnection();
            conn1 = cbl.getConnection();
            //			if( (c = (Caso) session.getAttribute(GestionInterface.ATT_CASE))== null)
            //				c= getCaso(session);
            //				c=cbl.getCaso(c.getIdCaso());
            c = cbl.getCaso(Integer.parseInt(request.getParameter("idCaso")));
            if (c == null) {
                log.error("Error en Aplicacion presupuestal:");
                return;
            }
            try {
                log.debug("Object: {}", "fecha " + c.getCasoDato("FECHA_DOCUMENTO").getValor());
                log.debug("Object: {}", "fecha " + request.getParameter("nuevaFecha"));
                log.debug("Object: {}", "folio " + request.getParameter("nFolioPrecompromiso"));
                cmst = conn.prepareCall("{?= call pa_ampliacionVigenciaPrecompromiso (?,?)}");
                cmst.registerOutParameter(1, Types.INTEGER);
                cmst.setString(2, request.getParameter("nFolioPrecompromiso"));
                cmst.setString(3, request.getParameter("nuevaFecha"));
                cmst.execute();
                int outputValue = cmst.getInt(1);
                if (outputValue == 0) {
                    // se realizo
                    if (c.getIdGabinete() == -1)
                        c.setIdGabinete(cbl.creaExpediente(usuario.getLogin(), c));
                    List fileItems = parseRequest(request, session.getId() + File.separator);
                    Iterator i = fileItems.iterator();
                    String docName = null;
                    while (i.hasNext()) {
                        FileItem item = (FileItem) i.next();
                        if (item.isFormField())
                            if ("nombre".equals(item.getFieldName())) {
                                docName = item.getString();
                                break;
                            }
                    }
                    i = fileItems.iterator();
                    while (i.hasNext()) {
                        FileItem item = (FileItem) i.next();
                        if (item.isFormField())
                            continue;
                        try {
                            String tmpFile = (new File(item.getName())).getName();
                            int pos = tmpFile.indexOf(".") != -1 ? tmpFile.lastIndexOf('.') + 1 : -1;
                            int pos1 = 0;
                            String ext = pos != -1 ? tmpFile.substring(pos) : "";
                            //Fortimax fimx = new Fortimax(select);
                            pos = tmpFile.lastIndexOf('.');
                            pos1 = tmpFile.lastIndexOf('\\') + 1;
                            String filename = tmpFile.substring(pos1, pos);
                            cbl.recibeDocumentoGestionPreCompromiso(c, new DataInputStream(item.getInputStream()), ext);
                            AplicacionManager.setIdOperacionVigencia(conn, c.getFolio(), 4, "CONSULTA_PRECOMPROMISO");
                            item.delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    //Imprime el jsonObj para pasarlo como respuesta al ajax
                    String destino = arrayObj.put(jsonObj).toString();
                    out.println(destino);
                    conn.commit();
                    jsonObj.put("Devuelve", "true");
                } else {
                    conn.rollback();
                    switch(outputValue) {
                        case 1:
                            mensaje = "ERROR AL SOLICITAR LA AMPLIACION. FAVOR DE INTENTAR NUEVAMENTE";
                            break;
                        default:
                            mensaje = "ERROR AL SOLICITAR LA AMPLIACION. FAVOR DE INTENTAR NUEVAMENTE";
                    }
                    jsonObj.put("Devuelve", mensaje);
                }
            } catch (Exception e) {
                log.error("Error occurred", "Error en Aplicacion presupuestal:" + e.getMessage());
                mensaje = "ERROR AL SOLICITAR LA AMPLIACION. FAVOR DE INTENTAR NUEVAMENTE";
                jsonObj.put("Devuelve", mensaje);
                conn.rollback();
            }
        } catch (Exception exc) {
            try {
                jsonObj.put("Devuelve", "false");
                String destino = arrayObj.put(jsonObj).toString();
                out.println(destino);
                conn.rollback();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                exc.printStackTrace();
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
    }

    private List parseRequest(HttpServletRequest req, String idSession) throws ServletException {
        ServletFileUpload upload = new ServletFileUpload();
        upload.setRepositoryPath(tempDir);
        // Directorio temporal de carga de archivos
        // Si el archivo excede este tamaño, ocurre un excepcion FileUploadException
        // -1 sin limite
        upload.setSizeMax(-1);
        try {
            return upload.parseRequest(req);
        } catch (FileUploadException fe) {
            fe.printStackTrace();
            throw new ServletException("Error de recepcion " + fe.getMessage());
        }
    }

    private void avanzaCaso(HttpServletRequest request, HttpServletResponse response, HttpSession session, Caso c, String[] responsable, String[] nombre) throws ServletException, IOException {
        //al momento de crear el compromiso, se necesita avanzar el caso de precompromiso para que ya no aparezca en el Inbox
        //		Caso c = null;
        //		usuario = (Usuario) session.getAttribute(ATT_USER);
        //
        //		log.debug("Buscando caso en ATT_CASE");
        //		c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        //
        //		if( c == null ){
        //			log.debug("fallo. Buscando caso en BD");
        //			c = getCaso(session);
        //		}
        try {
            String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
            //avanzaCaso(request, c, usuario, prefixPath, new String[] { "CONSULTA_PRECOMPROMISO" }, new String[] { "consulta_precomp" });
            avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
            try {
                out = response.getWriter();
                jsonObj.put("Success", "true");
                String destino = arrayObj.put(jsonObj).toString();
                out.println(destino);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error en Aplicacion presupuestal:" + e.getMessage());
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

    private void avanzaCaso(HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre) throws ServletException, IOException {
        //al momento de crear el compromiso, se necesita avanzar el caso de precompromiso para que ya no aparezca en el Inbox
        Caso c = null;
        usuario = (Usuario) session.getAttribute(ATT_USER);
        log.debug("Buscando caso en ATT_CASE");
        c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        if (c == null) {
            log.debug("fallo. Buscando caso en BD");
            c = getCaso(session);
        }
        try {
            String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
            //avanzaCaso(request, c, usuario, prefixPath, new String[] { "CONSULTA_PRECOMPROMISO" }, new String[] { "consulta_precomp" });
            avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
            try {
                out = response.getWriter();
                jsonObj.put("Success", "true");
                String destino = arrayObj.put(jsonObj).toString();
                out.println(destino);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error en Aplicacion presupuestal:" + e.getMessage());
        }
    }

    private String cuantaDisponible(Connection conn, String cIdConsolidado) throws SQLException {
        String cNumCuentaDisp = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String query = " select distinct cNumCuentaDisp from mConsolidadoSolicitud consolSol WITH (NOLOCK)   inner join mSolicitud as sol WITH (NOLOCK) on sol.cIdSolicitud=consolSol.cIdSolicitud " + "where cIdConsolidado='" + cIdConsolidado + "'";
            log.info("Object: {}", "query que obtiene la cuenta del disponible: " + query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                cNumCuentaDisp = rs.getString("cNumCuentaDisp");
                count++;
                log.info("Object: {}", "Count=" + count + "  cNumCuentaDisp=" + cNumCuentaDisp);
            }
            if (count != 1) {
                cNumCuentaDisp = "-1";
                log.warn("Error en la cuenta del disponible.");
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            rs = null;
            ps = null;
        }
        return cNumCuentaDisp;
    }

    private void actualizaCuantaDisponible(Connection conn, String cIdConsolidado, String cNumCuentaDisp) throws SQLException {
        PreparedStatement ps = null;
        try {
            String query = "update mConsolidado set cNumCuentaDisp='" + cNumCuentaDisp + "' where cIdConsolidado='" + cIdConsolidado + "'";
            log.info("Object: {}", "query que actualiza la cuenta del disponible en el consolidado: " + query);
            ps = conn.prepareStatement(query);
            ps.executeUpdate();
        } finally {
            if (ps != null) {
                ps.close();
            }
            ps = null;
        }
    }

    private void nuevoAutomatico(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        Connection conn = null;
        CallableStatement cmst = null;
        out = response.getWriter();
        String mensaje = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        String respuesta = "";
        try {
            int alcance = Integer.parseInt(request.getParameter("Alcance"));
            int consecutivoSolicitud = Integer.parseInt(request.getParameter("nIdConsecutivoSolicitud").replace(" ", ""));
            String cIdSolicitut = request.getParameter("cIdTipoSolicitud") + "-" + request.getParameter("cIdUnidadEjecutoraSolicitud") + "-" + consecutivoSolicitud;
            conn = DataSourceManager.getConnection(jndiName);
            //Validación si existe la requi en un consolidado
            String query = "select distinct mc.cIdUsuarioCreacion,mc.cIdConsolidado,u.U_NOMBRE from mConsolidado as mc with(nolock) " + "inner join mConsolidadoPreseleccionSolicitudes as mcps with(nolock) on mc.cIdConsolidado=mcps.cIdConsolidado " + "inner join cg_usuario as u with(nolock) on u.U_LOGIN=mc.cIdUsuarioCreacion " + "where mcps.cIdSolicitud='" + cIdSolicitut + "'";
            log.info("Object: {}", "query que valida si la requisición ya se encuentra en un consolidado : " + query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                mensaje = "La requisicion " + cIdSolicitut + " ya se encuentra en el consolidado " + rs.getString("cIdConsolidado") + ".\nEl usuario creador fue " + rs.getString("U_NOMBRE");
                respuesta = "0";
                jsonObj.put("mensaje", mensaje);
                jsonObj.put("respuesta", respuesta);
                String destino = arrayObj.put(jsonObj).toString();
                out.println(destino);
                conn.rollback();
                return;
            }
            //se crea el consolidado
            String desc = request.getParameter("cDescripcion");
            cmst = conn.prepareCall("{call pa_mConsolidadoAutomaticoPrecompromiso (?,?,?,?,?,?,?,?,?,?,?,?)}");
            cmst.setString(1, request.getParameter("cEjercicio"));
            cmst.setString(2, request.getParameter("cIdTipoConsolidado"));
            cmst.setString(3, request.getParameter("cIdUnidadEjecutora").replace(" ", ""));
            cmst.setString(4, desc);
            cmst.setInt(5, alcance);
            //usuario
            cmst.setString(6, usuario.getLogin());
            cmst.setString(7, request.getParameter("cIdTipoSolicitud"));
            cmst.setString(8, request.getParameter("cIdUnidadEjecutoraSolicitud"));
            cmst.setInt(9, consecutivoSolicitud);
            cmst.setString(10, request.getParameter("cNotas"));
            cmst.registerOutParameter(11, Types.INTEGER);
            cmst.registerOutParameter(12, Types.VARCHAR);
            cmst.execute();
            int outputValue = cmst.getInt(11);
            switch(outputValue) {
                case 0:
                    String cIdConsolidado = cmst.getString(12);
                    Util.bitacoraMovimientos(cIdConsolidado, "CREA CONSOLIDADO NUEVO AUTOMATICO", usuario.getLogin(), conn);
                    mensaje = cIdConsolidado;
                    String[] datos = cIdConsolidado.split("-");
                    session.setAttribute(ATT_ConTipoConsolidado, request.getParameter("cIdTipoConsolidado"));
                    session.setAttribute(ATT_ConUnidadEjec, request.getParameter("cIdUnidadEjecutora").replace(" ", ""));
                    session.setAttribute(ATT_ConConsecutivo, datos[2]);
                    conn.commit();
                    break;
                case 1:
                    mensaje = "";
                    conn.rollback();
                    break;
                case 2:
                    mensaje = "";
                    conn.rollback();
                    break;
                case 15:
                    mensaje = "";
                    conn.rollback();
                    break;
                default:
                    mensaje = "ERROR INESPERADO";
            }
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            try {
                jsonObj.put("Aplica", "0");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            log.error("Error occurred", "Error :" + e.getMessage());
            mensaje = "ERROR INESPERADO";
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (conn1 != null)
                    conn1.close();
                if (cmst != null)
                    cmst.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
        }
        try {
            //Regresa el mensaje de la aplicación contable para que sea mostrado en el JSP
            jsonObj.put("Contable1", mensaje);
            String destino = arrayObj.put(jsonObj).toString();
            out.println(destino);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        conn = null;
        cmst = null;
    }
}
