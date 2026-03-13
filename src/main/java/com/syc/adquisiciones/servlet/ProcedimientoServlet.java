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
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.adquisiciones.businessLogic.ProcedimientoBusinesLogic;
import com.syc.adquisiciones.core.DatosProcedimiento;
import com.syc.adquisiciones.util.Util;
import com.syc.contable.AccountingEngine;
import com.syc.contable.CompromisoBussinessLogic;
import com.syc.contable.ContableInterface;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.dsmngr.DataSourceManager;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "ProcedimientoServlet", urlPatterns = { "/servlet/ProcedimientoServlet" })
public class ProcedimientoServlet extends HttpServlet implements GestionInterface {

    private Usuario usuario;

    private PrintWriter out = null;

    private static final long serialVersionUID = 1L;

    private String jndiName = null;

    private String folioGenerator = null;

    private static Logger log = Logger.getLogger(ProcedimientoServlet.class);

    private String tempDir = null;

    private JSONArray arrayObj;

    private JSONObject jsonObj;

    private String cEjercicio, today;

    private Connection conn = null;

    private Connection conn1 = null;

    private CallableStatement cmst = null;

    private PreparedStatement pstmt = null;

    private ResultSet rs = null;

    public void init(ServletConfig config) throws ServletException {
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
        //int cmd =Integer.parseInt(request.getParameter("cmd"));
        arrayObj = new JSONArray();
        jsonObj = new JSONObject();
        Connection conn = null;
        CallableStatement cmst = null;
        try {
            out = response.getWriter();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        /////////////////////////////////////
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
        if (request.getParameter("nuevaFecha") != null) {
            ampliacionVigencia(request, response, session);
            return;
        }
        //Obtiene la operación que se manda como parámetro en la llamada GET
        int tipoOperacion = Integer.parseInt(request.getParameter("operacion"));
        log.debug("operacion: " + tipoOperacion);
        String cEjercicio = request.getParameter("cEjercicio");
        String tipoProceso = request.getParameter("tipoProceso");
        String nIdConsecutivo = request.getParameter("nIdConsecutivo");
        String cIdUnidadEjecutora = request.getParameter("cIdUnidadEjecutora");
        String cIdTipoProcedimiento = request.getParameter("cIdTipoProcedimiento");
        String cIdConsolidado = request.getParameter("cIdConsolidado");
        String val = request.getParameter("val");
        //String nFolioPrecompromiso=request.getParameter("nFolioPrecompromiso");
        String strParam = cEjercicio + "," + cIdTipoProcedimiento + "," + cIdUnidadEjecutora + "," + nIdConsecutivo + "," + tipoProceso + "," + cIdConsolidado + "," + val;
        switch(tipoOperacion) {
            case 1:
                //El preCompromiso es un tipo de caso 14
                generaGuardaCaso(request, response, session, (GestionInterface.IDTC_PRECOMMATERIALES + ""), "Aplicación de PreCompromiso materiales");
                break;
            case 2:
                adjudica(strParam, response);
                break;
            case 3:
                devuelveProcedimiento(strParam, response);
                break;
            case 4:
                aplicaContablemente(strParam, request, response, session, new String[] { "CONSULTA_PRECOMPROMISO" }, new String[] { "consulta_precomp" });
                break;
            case 5:
                devuelveContablemente(strParam, request, response, session);
                break;
            case 6:
                cancelaPrecomConsolidado(session, request, response, cIdConsolidado);
                break;
            case 7:
                generaGuardaCasoConsolidado(request, response, session, (GestionInterface.IDTC_PRECOMMATERIALES + ""), "Aplicación de PreCompromiso materiales");
                break;
            case 8:
                creaguardaProcedimiento(request, response);
                break;
            case 9:
                modificaCantidadLineaRequi(request, response);
                break;
            case 10:
                reclasificaTipoProced(request, response, usuario);
                break;
        }
        java.util.Date df = new java.util.Date();
        System.out.println("Finaliza time: " + df.toString());
        System.out.println("Segundos diferencia:  " + (df.getTime() - di.getTime()) / 1000);
        arrayObj = null;
        jsonObj = null;
        cmst = null;
        conn = null;
    }

    private synchronized void generaGuardaCaso(HttpServletRequest request, HttpServletResponse response, HttpSession session, String tipoCaso, String CONCEPTO_MOV) throws ServletException, IOException {
        //Metodo utilizado para generar los casos de Precompromiso y Compromiso
        out = response.getWriter();
        int folio;
        String folioCaso = null;
        try {
            String ur = request.getParameter("ur");
            Caso c = null;
            c = iniciaCaso(request, tipoCaso);
            folioCaso = c.getFolio();
            int indice = folioCaso.lastIndexOf('-') + 1;
            folio = Integer.parseInt(folioCaso.substring(indice));
            try {
                jsonObj.put("Folio1", "" + folio);
                jsonObj.put("Folio2", "" + folioCaso);
                log.debug("Folio+++++++++++++++++++" + folio + "++++++++++++++++++++++++++++");
                log.debug("Folio+++++++++++++++++++" + folioCaso + "++++++++++++++++++++++++++++");
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

    private void adjudica(String strParam, HttpServletResponse response) throws ServletException, IOException {
        String[] param = strParam.split(",");
        Connection conn = null;
        CallableStatement cmst = null, cmst1 = null;
        out = response.getWriter();
        String mensaje = "";
        try {
            int val = Integer.parseInt(param[3]);
            int tipoProceso = Integer.parseInt(param[4]);
            //String cIdProcedimiento=param[1]+'-'+param[2]+'-'+val;
            conn = DataSourceManager.getConnection(jndiName);
            cmst = conn.prepareCall("{call pa_mGeneraPedidosyContratos (?,?,?,?,?,?)}");
            cmst.setString(1, param[0]);
            cmst.setString(2, param[1]);
            cmst.setString(3, param[2]);
            cmst.setInt(4, val);
            cmst.registerOutParameter(5, Types.INTEGER);
            cmst.setInt(6, tipoProceso);
            cmst.execute();
            log.info("pa_mGeneraPedidosyContratos '" + param[0] + "','" + param[1] + "','" + param[2] + "'," + val + ",0," + tipoProceso);
            int outputValue = cmst.getInt(5);
            switch(outputValue) {
                case 0:
                    mensaje = "El Procedimiento  se ha Adjudicado Correctamente.";
                    cmst1 = conn.prepareCall("{call InsertmSaldoPrecompromisoMateriales(?,?,?,?)}");
                    cmst1.setString(1, param[0]);
                    cmst1.setString(2, param[1]);
                    cmst1.setString(3, param[2]);
                    cmst1.setInt(4, val);
                    cmst1.execute();
                    conn.commit();
                    break;
                case 1:
                    mensaje = "Hubo un error en la Adjudicacion.";
                    conn.rollback();
                    break;
                case 2:
                    mensaje = "Existen Proveedores sin Partidas Adjudicadas,eliminelos o agregue lineas";
                    conn.rollback();
                    break;
                case 3:
                    mensaje = "No puede adjudicar el procedimiento por que se estan generando contratos cuando deberian de ser puros pedidos.";
                    conn.rollback();
                    break;
                case 15:
                    mensaje = "No puede adjudicar el procedimiento completo,le falta asignar ganador y cumple con la evaluacion tecnica ";
                    conn.rollback();
                    break;
                case 16:
                    mensaje = "No puede adjudicar el procedimiento, falta capturar el n\u00famero de procedimiento compranet.";
                    conn.rollback();
                    break;
                case 17:
                    mensaje = "No puede adjudicar el procedimiento, se est\u00e1 generando un pedido y el tipo de procedimiento debe ser :Adjudicación Directa Artículo 42 de la LAASSP Pedidos.";
                    conn.rollback();
                    break;
                default:
                    mensaje = "ERROR INESPERADO. Contacte al Administrador";
            }
        } catch (Exception e) {
            try {
                conn.rollback();
                e.printStackTrace();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            try {
                jsonObj.put("Aplica", "0");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            log.error("Error :" + e.getMessage());
            mensaje = "ERROR INESPERADO AL ADJUDICAR EL PROCEDIMIENTO";
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
            jsonObj.put("Contable1", new String(mensaje.getBytes("UTF-8"), "ISO-8859-1"));
            String destino = arrayObj.put(jsonObj).toString();
            out.println(destino);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        conn = null;
        cmst = null;
    }

    private void devuelveProcedimiento(String strParam, HttpServletResponse response) throws ServletException, IOException {
        String[] param = strParam.split(",");
        String mensaje = "";
        out = response.getWriter();
        PreparedStatement ps = null;
        Connection conn = null;
        CallableStatement cmst = null;
        JSONArray arrayObj = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        AccountingEngine accEng = new AccountingEngine();
        accEng.setValidaInsuficienciaDeSaldo(false);
        try {
            int val1 = Integer.parseInt(param[3]);
            conn = DataSourceManager.getConnection(jndiName);
            ///// buscar los precompromisos que se generaron
            String cIdProcedimiento = param[1] + '-' + param[2] + '-' + val1;
            String temFolio = "";
            ps = conn.prepareStatement("select nFolioPreCompromiso from fn_consultaPrecompromisoFinanciero ('" + cIdProcedimiento + "')");
            rs = ps.executeQuery();
            while (rs.next()) {
                String folioApartadoCancel = String.valueOf(rs.getInt(1));
                if (temFolio != folioApartadoCancel) {
                    temFolio = folioApartadoCancel;
                    accEng.cancelAccountingApplication(conn, "PRECOMPROMISO", folioApartadoCancel, "tPrecompromisoEncabezado", "tPrecompromisoDetalle", "nFolioPreCompromiso");
                    mensaje = "SE DEVUELVE CORRECTAMENTE EL PROCEDIMIENTO";
                }
            }
            cmst = conn.prepareCall("{call pa_mProcedimientoDevuelve1(?,?,?,?,?)}");
            cmst.setString(1, param[0]);
            cmst.setString(2, param[1]);
            cmst.setString(3, param[2]);
            cmst.setInt(4, val1);
            cmst.registerOutParameter(5, Types.INTEGER);
            cmst.execute();
            int outputVal = cmst.getInt(5);
            log.debug(outputVal);
            switch(outputVal) {
                case 1:
                    mensaje = "NO SE PUEDE DEVOLVER EL PROCEDIMIENTO PORQUE TIENE UNO O MAS CONTRATOS APROBADOS";
                    conn.rollback();
                    break;
                case 2:
                    mensaje = "NO SE PUEDE DEVOLVER EL PROCEDIMIENTO PORQUE LOS CONTRATOS TIENE UNO O MAS OFICIOS APROBADOS";
                    conn.rollback();
                    break;
                case 3:
                    mensaje = "NO SE PUEDE DEVOLVER EL PROCEDIMIENTO PORQUE TIENE UNO O MAS PEDIDOS APROBADOS";
                    conn.rollback();
                    break;
                case 4:
                    mensaje = "NO SE PUEDE DEVOLVER EL PROCEDIMIENTO PORQUE LOS PEDIDOS TIENEN UNO O MAS OFICIOS APROBADOS";
                    conn.rollback();
                    break;
                case 5:
                    mensaje = "ERROR DE BORRADO DE TABLA ";
                    conn.rollback();
                    break;
                case 6:
                    mensaje = "Se devuelve correctamente el procedimiento";
                    conn.commit();
                    break;
                default:
                    mensaje = "ERROR INESPERADO";
            }
        } catch (Exception e) {
            log.error("Error en Aplicacion contable:" + e.getMessage());
            mensaje = "ERROR INESPERADO DE LA CANCELACION CONTABLE";
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
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
            try {
                //Regresa el mensaje de la aplicación contable para que sea mostrado en el JSP
                jsonObj.put("Contable1", mensaje);
                String destino = arrayObj.put(jsonObj).toString();
                out.println(destino);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }

    private synchronized void aplicaContablemente(String strParam, HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre) throws ServletException, IOException {
        session = request.getSession(false);
        out = response.getWriter();
        String validaSaldo = "";
        ArrayList<String> arrLResult = new ArrayList<String>();
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        PreparedStatement pstm = null, ps = null;
        CallableStatement cmst = null, cmst1 = null, cmst2 = null;
        Connection conn = null, conn1 = null;
        String tipo, ue, cIdProcedimiento, cIdConsolidado;
        int consecutivo;
        String[] param = strParam.split(",");
        int val = Integer.parseInt(param[3]);
        int tipoProceso = Integer.parseInt(param[4]);
        cIdConsolidado = param[5];
        //Datos del procedimiento
        tipo = (String) session.getAttribute(GestionInterface.ATT_ProTipoProcedimiento);
        ue = (String) session.getAttribute(GestionInterface.ATT_ProUnidadEjecutora);
        consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_ProConsecutivo));
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
        log.debug("Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()));
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        AplicarContableReturn acr = null;
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        try {
            conn = cbl.getConnection();
            conn1 = cbl.getConnection();
            if (param[6].equalsIgnoreCase("1")) {
                /////cancelar el documento del consolidado/////////////
                AccountingEngine accEng = new AccountingEngine();
                accEng.setValidaInsuficienciaDeSaldo(false);
                ps = conn.prepareStatement("select a.nFolioPrecomMateriales from tPrecomMaterialesEncabezado a with(nolock) inner join mConsolidado b with(nolock) on a.cIdConsolidado=b.cIdConsolidado " + " and a.nFolioPrecomMateriales=b.ConsecutivoPRECOMP " + " where a.cDocumentoHaplicado='S' and b.cIdConsolidado='" + cIdConsolidado + "'");
                rs = ps.executeQuery();
                while (rs.next()) {
                    String folioPrecom = String.valueOf(rs.getInt(1));
                    accEng.cancelAccountingApplication(conn, "PRECOMMATERIALES", folioPrecom, "tPrecomMaterialesEncabezado", "tPrecomMaterialesDetalle", "nFolioPrecomMateriales");
                }
                ///// fin de cancelacion del documento///////
            }
            Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
            acr = conInt.aplicarContableNuevo(conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), validaSaldo);
            arrLResult = (ArrayList) acr.getMessageList();
            if (acr.isSuccess()) {
                log.debug(request.getParameter("nFolioPrecompromiso"));
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
                    pstm = conn.prepareStatement("UPDATE mProcedimiento SET  ConsecutivoPRECOMP = ?, C_FOLIO_PRE = ? " + " WHERE cIdTipoProcedimiento = ? " + " and cIdUnidadEjecutora = ?" + " and nIdConsecutivo = ?" + " and cEjercicio=?");
                    log.debug("folio " + request.getParameter("nFolioPrecompromiso"));
                    log.debug("folio " + request.getParameter("folioCasoPreCompromiso"));
                    pstm.setString(1, request.getParameter("nFolioPrecompromiso"));
                    pstm.setString(2, request.getParameter("folioCasoPreCompromiso"));
                    pstm.setString(3, tipo);
                    pstm.setString(4, ue);
                    pstm.setInt(5, consecutivo);
                    pstm.setString(6, request.getParameter("cEjercicio"));
                    pstm.executeUpdate();
                    //registra la vigencia del precompromiso
                    cIdProcedimiento = tipo + '-' + ue + '-' + consecutivo;
                    cmst1 = conn.prepareCall("{call sp_mVigenciaPrecompromiso (?,?,?,?,?)}");
                    cmst1.setString(1, request.getParameter("cEjercicio"));
                    cmst1.setString(2, ue);
                    cmst1.setString(3, cIdProcedimiento);
                    cmst1.setString(4, request.getParameter("folioCasoPreCompromiso"));
                    cmst1.setDate(5, Date.valueOf("2014-01-01"));
                    cmst1.execute();
                    //adjudicar el procedimiento
                    //////////////////////////////
                    cmst = conn.prepareCall("{call pa_mGeneraPedidosyContratos (?,?,?,?,?,?)}");
                    //ejercicio
                    cmst.setString(1, param[0]);
                    //tipo
                    cmst.setString(2, param[1]);
                    //unidad
                    cmst.setString(3, param[2]);
                    //consecutivo
                    cmst.setInt(4, val);
                    cmst.registerOutParameter(5, Types.INTEGER);
                    //tipoProceso
                    cmst.setInt(6, tipoProceso);
                    cmst.execute();
                    int outputValue1 = cmst.getInt(5);
                    switch(outputValue1) {
                        case 0:
                            mensaje = "El Procedimiento  se ha Adjudicado Correctamente.";
                            // Una vez que ha hecho la aplicación contable  y se ha adjudicado se  avanza el caso A CONSULTA PAGOS
                            //////////////////////////////////////////
                            avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                            //////////////////////////////////////////
                            cmst2 = conn.prepareCall("{call InsertmSaldoPrecompromisoMateriales(?,?,?,?)}");
                            cmst2.setString(1, param[0]);
                            cmst2.setString(2, param[1]);
                            cmst2.setString(3, param[2]);
                            cmst2.setInt(4, val);
                            cmst2.execute();
                            conn.commit();
                            break;
                        case 1:
                            mensaje = "Hubo un error en la Adjudicacion.";
                            conn.rollback();
                            cmst1 = conn1.prepareCall("{call pa_fallaAplicacionContableProcedimiento (?,?,?,?,?)}");
                            cmst1.setString(1, tipo);
                            cmst1.setString(2, ue);
                            cmst1.setInt(3, consecutivo);
                            cmst1.setString(4, request.getParameter("cEjercicio"));
                            cmst1.setString(5, request.getParameter("nFolioPrecompromiso"));
                            cmst1.execute();
                            avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                            conn1.commit();
                            break;
                        case 2:
                            mensaje = "Existen Proveedores sin Partidas Adjudicadas,eliminelos o agregue lineas";
                            conn.rollback();
                            cmst1 = conn1.prepareCall("{call pa_fallaAplicacionContableProcedimiento (?,?,?,?,?)}");
                            cmst1.setString(1, tipo);
                            cmst1.setString(2, ue);
                            cmst1.setInt(3, consecutivo);
                            cmst1.setString(4, request.getParameter("cEjercicio"));
                            cmst1.setString(5, request.getParameter("nFolioPrecompromiso"));
                            cmst1.execute();
                            avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                            conn1.commit();
                            break;
                        case 15:
                            mensaje = "No puede adjudicar el procedimiento completo,le falta asignar ganador y cumple con la evaluacion tecnica ";
                            conn.rollback();
                            cmst1 = conn1.prepareCall("{call pa_fallaAplicacionContableProcedimiento (?,?,?,?,?)}");
                            cmst1.setString(1, tipo);
                            cmst1.setString(2, ue);
                            cmst1.setInt(3, consecutivo);
                            cmst1.setString(4, request.getParameter("cEjercicio"));
                            cmst1.setString(5, request.getParameter("nFolioPrecompromiso"));
                            cmst1.execute();
                            avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                            conn1.commit();
                            break;
                        default:
                            mensaje = "ERROR INESPERADO";
                            conn.rollback();
                            cmst1 = conn1.prepareCall("{call pa_fallaAplicacionContableProcedimiento (?,?,?,?,?)}");
                            cmst1.setString(1, tipo);
                            cmst1.setString(2, ue);
                            cmst1.setInt(3, consecutivo);
                            cmst1.setString(4, request.getParameter("cEjercicio"));
                            cmst1.setString(5, request.getParameter("nFolioPrecompromiso"));
                            cmst1.execute();
                            avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                            conn1.commit();
                            break;
                    }
                    ///////////////////////////////////////////////
                    //Recargando el caso
                    Caso sc = new Caso();
                    sc.setIdCaso(c.getIdCaso());
                    c = CasoManager.select(conn, sc);
                    log.debug(c.getCasoDato("APLICADO_CONT").getValor());
                    log.debug("Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
                    //	mensaje = "DOCUMENTO DE PRECOMPROMISO APLICADO CONTABLEMENTE.";
                    //	conn.commit();
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
                    cmst1 = conn1.prepareCall("{call pa_fallaAplicacionContableProcedimiento (?,?,?,?,?)}");
                    cmst1.setString(1, tipo);
                    cmst1.setString(2, ue);
                    cmst1.setInt(3, consecutivo);
                    cmst1.setString(4, request.getParameter("cEjercicio"));
                    cmst1.setString(5, request.getParameter("nFolioPrecompromiso"));
                    avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                    cmst1.execute();
                    conn1.commit();
                }
            } else {
                conn.rollback();
                jsonObj.put("Aplica", "0");
                cmst1 = conn1.prepareCall("{call pa_fallaAplicacionContableProcedimiento (?,?,?,?,?)}");
                cmst1.setString(1, tipo);
                cmst1.setString(2, ue);
                cmst1.setInt(3, consecutivo);
                cmst1.setString(4, request.getParameter("cEjercicio"));
                cmst1.setString(5, request.getParameter("nFolioPrecompromiso"));
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
            log.error("Error en Aplicacion contable:" + e.getMessage());
        }
    }

    private Caso getCaso(HttpSession session) {
        String ejercicio = (String) session.getAttribute(GestionInterface.ATT_ProEjercicio);
        String sql, tipo, ue;
        int consecutivo;
        //Datos del procedimiento
        tipo = (String) session.getAttribute(GestionInterface.ATT_ProTipoProcedimiento);
        ue = (String) session.getAttribute(GestionInterface.ATT_ProUnidadEjecutora);
        consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_ProConsecutivo));
        //Apartir de los atributos guardados en sesión se hace un QRY para obtener el ID_CASO
        sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mProcedimiento p, CG_CASO c, CG_CASO_OPERACION o " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cEjercicio=?" + " and p.cIdTipoProcedimiento=?" + " and p.cIdUnidadEjecutora=?" + " and p.nIdConsecutivo=?";
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
                log.debug("idCaso " + idCaso);
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

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private synchronized void devuelveContablemente(String strParam, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        session = request.getSession(false);
        out = response.getWriter();
        ArrayList<String> arrLResult = new ArrayList<String>();
        PreparedStatement pstm = null, pstm1 = null, ps = null, pstm2 = null, pstm3 = null;
        CallableStatement cmst = null;
        Connection conn = null, conn1 = null;
        AplicarContableReturn acr = null;
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        AccountingEngine accEng = new AccountingEngine();
        accEng.setValidaInsuficienciaDeSaldo(false);
        String tipo, ue, idProcedimiento;
        int consecutivo;
        //Datos del procedimiento
        tipo = (String) session.getAttribute(GestionInterface.ATT_ProTipoProcedimiento);
        ue = (String) session.getAttribute(GestionInterface.ATT_ProUnidadEjecutora);
        consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_ProConsecutivo));
        idProcedimiento = tipo + '-' + ue + '-' + consecutivo;
        if (usuario == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        String[] param = strParam.split(",");
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
            ///// buscar los precompromisos que se generaron
            String temFolio = "";
            ps = conn.prepareStatement("select nFolioPreCompromiso from fn_consultaPrecompromisoFinanciero ('" + idProcedimiento + "')");
            rs = ps.executeQuery();
            while (rs.next()) {
                String folioApartadoCancel = String.valueOf(rs.getInt(1));
                if (temFolio != folioApartadoCancel) {
                    temFolio = folioApartadoCancel;
                    //	accEng.cancelAccountingApplication(conn, "PRECOMMATERIALES",folioApartadoCancel, "tPrecomMaterialesEncabezado", "tPrecomMaterialesDetalle", "nFolioPrecomMateriales");
                    accEng.cancelAccountingApplication(conn, "PRECOMPROMISO", folioApartadoCancel, "tPrecompromisoEncabezado", "tPrecompromisoDetalle", "nFolioPreCompromiso");
                }
            }
            Caso caso = getCaso(session);
            Map m = CasoDatoManager.readValuesCasoDato(request, caso.getCasoDato(), true);
            acr = conInt.cancelarAppContableNueva(conn, caso, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), "");
            arrLResult = (ArrayList) acr.getMessageList();
            if (acr.isSuccess()) {
                log.debug("ejercicio " + request.getParameter("cEjercicio"));
                log.debug("folio " + request.getParameter("nFolioPrecompromiso"));
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
                    jsonObj.put("Aplica", "1");
                    pstm = conn.prepareStatement("UPDATE mProcedimiento SET nIdEstado = 1 , ConsecutivoPRECOMP = null, C_FOLIO_PRE = null " + " WHERE cIdTipoProcedimiento = ? " + " and cIdUnidadEjecutora = ?" + " and nIdConsecutivo = ?" + " and cEjercicio=?");
                    pstm.setString(1, tipo);
                    pstm.setString(2, ue);
                    pstm.setInt(3, consecutivo);
                    pstm.setString(4, request.getParameter("cEjercicio"));
                    pstm.executeUpdate();
                    //recuperar el precompromiso del consolidado en caso de que exista
                    if (param[6].compareToIgnoreCase("0") != 0) {
                        accEng.makeAccountingApplication(conn, "PRECOMMATERIALES", param[6], "tPrecomMaterialesEncabezado", "tPrecomMaterialesDetalle", "nFolioPrecomMateriales");
                        pstm2 = conn.prepareStatement("UPDATE mConsolidado SET  ConsecutivoPRECOMP = ?, C_FOLIO_PRE = ? " + " WHERE cIdConsolidado=?");
                        String folio = "PRMT-" + ue + "-" + param[6];
                        pstm2.setString(1, param[6]);
                        pstm2.setString(2, folio);
                        pstm2.setString(3, param[5]);
                        pstm2.executeUpdate();
                        pstm3 = conn.prepareStatement("DELETE FROM mPrecomMaterialesDetalleTmp WHERE cIdConsolidado=? and cIdProcedimiento=?");
                        pstm3.setString(1, param[5]);
                        pstm3.setString(2, idProcedimiento);
                        pstm3.executeUpdate();
                    }
                    log.debug("Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
                    cmst = conn.prepareCall("{call pa_mProcedimientoDevuelve1(?,?,?,?,?)}");
                    cmst.setString(1, request.getParameter("cEjercicio"));
                    cmst.setString(2, tipo);
                    cmst.setString(3, ue);
                    cmst.setInt(4, consecutivo);
                    cmst.registerOutParameter(5, Types.INTEGER);
                    cmst.execute();
                    int outputVal = cmst.getInt(5);
                    switch(outputVal) {
                        case 1:
                            mensaje = "NO SE PUEDE DEVOLVER EL PROCEDIMIENTO PORQUE TIENE UNO O MAS CONTRATOS APROBADOS";
                            conn.rollback();
                            break;
                        case 2:
                            mensaje = "NO SE PUEDE DEVOLVER EL PROCEDIMIENTO PORQUE LOS CONTRATOS TIENE UNO O MAS OFICIOS APROBADOS";
                            conn.rollback();
                            break;
                        case 3:
                            mensaje = "NO SE PUEDE DEVOLVER EL PROCEDIMIENTO PORQUE TIENE UNO O MAS PEDIDOS APROBADOS";
                            conn.rollback();
                            break;
                        case 4:
                            mensaje = "NO SE PUEDE DEVOLVER EL PROCEDIMIENTO PORQUE LOS PEDIDOS TIENEN UNO O MAS OFICIOS APROBADOS";
                            conn.rollback();
                            break;
                        case 5:
                            mensaje = "ERROR DE BORRADO DE TABLA";
                            conn.rollback();
                            break;
                        case 6:
                            mensaje = "SE DEVUELVE CORRECTAMENTE EL PROCEDIMIENTO";
                            conn.commit();
                            break;
                        default:
                            mensaje = "ERROR INESPERADO";
                            conn.rollback();
                            break;
                    }
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
                    jsonObj.put("Devuelve", "1");
                    //hubo un error al aplicar contablemente ,actualiza estado de la tabla de procedimiento
                    pstm1 = conn1.prepareStatement("UPDATE mProcedimiento SET nIdEstado = 2 " + " WHERE cIdTipoProcedimiento = ? " + " and cIdUnidadEjecutora = ?" + " and nIdConsecutivo = ?" + " and cEjercicio=?");
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
                pstm1 = conn1.prepareStatement("UPDATE mProcedimiento SET nIdEstado = 2 " + " WHERE cIdTipoProcedimiento = ? " + " and cIdUnidadEjecutora = ?" + " and nIdConsecutivo = ?" + " and cEjercicio=?");
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

    private synchronized void ampliacionVigencia(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        //Metodo utilizado para generar los casos de Precompromiso y Compromiso
        out = response.getWriter();
        Caso c = null;
        Connection conn = null;
        CasoBusinessLogic cbl = new CasoBusinessLogic(jndiName);
        String tipo, ue;
        int consecutivo;
        //Datos del procedimiento
        tipo = (String) session.getAttribute(GestionInterface.ATT_ProTipoProcedimiento);
        ue = (String) session.getAttribute(GestionInterface.ATT_ProUnidadEjecutora);
        consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_ProConsecutivo));
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
        try {
            conn = cbl.getConnection();
            conn1 = cbl.getConnection();
            c = cbl.getCaso(Integer.parseInt(request.getParameter("idCaso")));
            if (c == null) {
                log.error("Error en Aplicacion contable:");
                return;
            }
            try {
                log.debug("fecha " + c.getCasoDato("FECHA_DOCUMENTO").getValor());
                log.debug("fecha " + request.getParameter("nuevaFecha"));
                log.debug("folio " + request.getParameter("nFolioPrecompromiso"));
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
                log.error("Error en Aplicacion contable:" + e.getMessage());
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

    private void reclasificaTipoProced(HttpServletRequest request, HttpServletResponse response, Usuario usuario) {
        DatosProcedimiento datProced = null;
        ProcedimientoBusinesLogic busines = null;
        String destino = null;
        try {
            out = response.getWriter();
            datProced = new DatosProcedimiento();
            datProced.setcIdProcedimiento(request.getParameter("cIdProcedimiento"));
            datProced.setcIdRFC(request.getParameter("cIdRFC"));
            datProced.setmMontoNetoContrato(request.getParameter("mMontoNetoContrato") != null && !"".equalsIgnoreCase(request.getParameter("mMontoNetoContrato")) ? Double.parseDouble(request.getParameter("mMontoNetoContrato")) : -1);
            datProced.setcIdContratoDef(request.getParameter("cIdContratoDef"));
            datProced.setcTipoProcedimiento(request.getParameter("cTipoProcedimiento"));
            datProced.setcUnidadEjecutora(request.getParameter("cUnidadEjecutora"));
            datProced.setnCategoriaProcedimiento(request.getParameter("nCategoriaProcedimiento") != null && !"".equalsIgnoreCase(request.getParameter("nCategoriaProcedimiento")) ? Integer.parseInt(request.getParameter("nCategoriaProcedimiento")) : -1);
            datProced.setnCategoriaProcedimientoNuevo(request.getParameter("nCategoriaProcedimientoNuevo") != null && !"".equalsIgnoreCase(request.getParameter("nCategoriaProcedimientoNuevo")) ? Integer.parseInt(request.getParameter("nCategoriaProcedimientoNuevo")) : -1);
            datProced.setnConsecutivoProcedimiento(request.getParameter("nConsecutivoProcedimiento") != null && !"".equalsIgnoreCase(request.getParameter("nConsecutivoProcedimiento")) ? Integer.parseInt(request.getParameter("nConsecutivoProcedimiento")) : -1);
            datProced.setnFundamentoLegal(request.getParameter("nFundamentoLegal") != null && !"".equalsIgnoreCase(request.getParameter("nFundamentoLegal")) ? Integer.parseInt(request.getParameter("nFundamentoLegal")) : -1);
            datProced.setnFundamentoLegalNuevo(request.getParameter("nFundamentoLegalNuevo") != null && !"".equalsIgnoreCase(request.getParameter("nFundamentoLegalNuevo")) ? Integer.parseInt(request.getParameter("nFundamentoLegalNuevo")) : -1);
            String cadTabla = request.getParameter("arrayFechas");
            String[] arrayTabla = cadTabla.split(",");
            datProced.setFechas(Util.creaArray(arrayTabla));
            busines = new ProcedimientoBusinesLogic();
            jsonObj.put("mensaje", busines.reclasificaTipoProcedimiento(datProced, usuario));
        } catch (Exception e) {
            try {
                jsonObj.put("mensaje", e.getMessage().toString());
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            log.error(e);
            e.printStackTrace();
        } finally {
            datProced = null;
            busines = null;
            destino = arrayObj.put(jsonObj).toString();
            out.println(destino);
        }
    }

    private synchronized void modificaCantidadLineaRequi(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        CasoBusinessLogic cbl = new CasoBusinessLogic(jndiName);
        CallableStatement cmst = null;
        int respuesta = -1;
        String cadenaMeses = "";
        try {
            log.info("Inicio del metodo modificaCantidadLineaRequi");
            out = response.getWriter();
            String cEjercicio = request.getParameter("cEjercicio");
            String cIdUEConsolidado = request.getParameter("cIdUEConsolidado");
            String TipoConsolidado = request.getParameter("TipoConsolidado");
            int ConsecutivoConsolidado = Integer.parseInt(request.getParameter("ConsecutivoConsolidado"));
            int lineaConsolidado = Integer.parseInt(request.getParameter("lineaConsolidado"));
            int nCantidad = Integer.parseInt(request.getParameter("nCantidad"));
            try {
                conn = cbl.getConnection();
                log.info("query que modica las cantidades de la requisicion en el procedimiento : sp_mModificaCantRequisiciones");
                cmst = conn.prepareCall("{call sp_mModificaCantRequisiciones (?,?,?,?,?,?,?,?)}");
                cmst.setString(1, cEjercicio);
                cmst.setString(2, cIdUEConsolidado);
                cmst.setString(3, TipoConsolidado);
                cmst.setInt(4, ConsecutivoConsolidado);
                cmst.setInt(5, lineaConsolidado);
                cmst.setInt(6, nCantidad);
                cmst.registerOutParameter(7, Types.INTEGER);
                cmst.registerOutParameter(8, Types.VARCHAR);
                cmst.execute();
                respuesta = cmst.getInt(7);
                cadenaMeses = cmst.getString(8);
                Util.bitacoraMovimientos(TipoConsolidado + "-" + cIdUEConsolidado + "-" + ConsecutivoConsolidado, "Modifica Cantidad, Nueva cantidad a guardar " + nCantidad, usuario.getLogin(), conn);
                conn.commit();
                log.info("Se ejecuto correctamente el store procedure de modificación de cantidades.");
            } catch (SQLException exc) {
                // TODO: handle exception
                log.error("Error de SQL.");
                conn.rollback();
                exc.printStackTrace();
            } finally {
                if (conn != null)
                    conn.close();
                if (cmst != null)
                    cmst.close();
                conn = null;
                cmst = null;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            try {
                jsonObj.put("respuesta", respuesta);
                jsonObj.put("cadenaMeses", cadenaMeses);
            } catch (JSONException e2) {
                // TODO: handle exception
                log.error("Error del JsonObj");
                e2.printStackTrace();
            }
            String destino = arrayObj.put(jsonObj).toString();
            out.println(destino);
        }
    }

    private synchronized void creaguardaProcedimiento(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        CasoBusinessLogic cbl = new CasoBusinessLogic(jndiName);
        CallableStatement cmst = null;
        CallableStatement cmstFechas = null;
        PreparedStatement ps = null, ps2 = null;
        PreparedStatement pstm = null, pstmnt = null, pstmntFechas = null;
        ResultSet rs = null, rs2 = null;
        out = response.getWriter();
        String mensaje = "";
        String respuesta = "0";
        String cIdConsolidado = request.getParameter("cIdconsolidado");
        String cEjercicio = request.getParameter("cEjercicio");
        String cIdTipoProcedimiento = request.getParameter("cIdTipoProcedimiento");
        String cIdUnidadEjecutora = request.getParameter("cIdUnidadEjecutora");
        String cIdProcedimiento = "";
        try {
            try {
                conn = cbl.getConnection();
                //Validar que el consolidado no este en un procedimiento
                String query = "select cIdProcedimiento,(select U_NOMBRE from cg_usuario where U_LOGIN=cIdUsuarioCreacion) nombreUsuario " + "from mProcedimiento with(Nolock) where nIdEstado in(1,2) and cIdConsolidado='" + cIdConsolidado + "'";
                log.info("query que valida si el consolidado ya se encuentra en un procedimiento : " + query);
                ps = conn.prepareStatement(query);
                rs = ps.executeQuery();
                if (rs.next()) {
                    mensaje = "El consolidado " + cIdConsolidado + " ya se encuentra en el procedimiento " + rs.getString("cIdProcedimiento") + ".\nEl usuario creador fue " + rs.getString("nombreUsuario");
                    jsonObj.put("mensaje", mensaje);
                    jsonObj.put("respuesta", respuesta);
                    return;
                }
                //Se obtiene el consecutivo del procedimiento
                String query2 = "select nIdConsecutivo as nIdConsecutivo from fn_GetConsecutivoProcedimiento('" + cEjercicio + "','" + cIdTipoProcedimiento + "','" + cIdUnidadEjecutora + "')";
                log.info("query que obtiene el consecutivo del procedimiento : " + query2);
                ps2 = conn.prepareStatement(query2);
                rs2 = ps2.executeQuery();
                int consecutivoProc = 0;
                if (rs2.next()) {
                    consecutivoProc = rs2.getInt("nIdConsecutivo");
                    cIdProcedimiento = cIdTipoProcedimiento + "-" + cIdUnidadEjecutora + "-" + consecutivoProc;
                }
                if (consecutivoProc == 0) {
                    mensaje = "Error al obtener el consecutivo del procedimiento";
                    jsonObj.put("mensaje", mensaje);
                    jsonObj.put("respuesta", respuesta);
                    return;
                }
                //se checa si es un procedimiento de arrendamientos
                String[] consolidado = new String[3];
                consolidado = cIdConsolidado.split("-");
                String categoria = request.getParameter("categoria");
                //request.getParameter("descripcion");
                String descripcion = new String(request.getParameter("descripcion").getBytes("ISO-8859-1"), "UTF-8");
                String numero_externo = request.getParameter("numero_externo");
                int tipoProceso = Integer.parseInt(request.getParameter("tipoProceso"));
                int Activo = Integer.parseInt(request.getParameter("Activo"));
                int nPorcentajeIVA = Integer.parseInt(request.getParameter("nPorcentajeIVA"));
                int isPlurianual = Integer.parseInt(request.getParameter("isPlurianual"));
                if ("PA".equalsIgnoreCase(cIdTipoProcedimiento) || "PL".equalsIgnoreCase(cIdTipoProcedimiento)) {
                    String cboUnidadEjecutoraInmueble = request.getParameter("cboUnidadEjecutoraInmueble");
                    int cboDenominacionInmueble = Integer.parseInt(request.getParameter("cboDenominacionInmueble"));
                    String descripcionOtros = request.getParameter("descripcionOtros");
                    float area_construida = Float.parseFloat(request.getParameter("area_construida"));
                    float area_rentable = Float.parseFloat(request.getParameter("area_rentable"));
                    float numero_empleados = Float.parseFloat(request.getParameter("numero_empleados"));
                    String direccion_inmueble = request.getParameter("direccion_inmueble");
                    log.info("query que crea el procedimiento de arrendamiento : sp_mProcedimientoArrendamientos");
                    cmst = conn.prepareCall("{call sp_mProcedimientoArrendamientos (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
                    cmst.setString(1, cEjercicio);
                    cmst.setString(2, cIdTipoProcedimiento);
                    cmst.setString(3, cIdUnidadEjecutora);
                    cmst.setInt(4, consecutivoProc);
                    cmst.setString(5, consolidado[0]);
                    cmst.setString(6, consolidado[2]);
                    cmst.setString(7, categoria);
                    cmst.setString(8, descripcion);
                    cmst.setString(9, usuario.getLogin());
                    cmst.setInt(10, 1);
                    cmst.setString(11, usuario.getPropiedad("CCENTROCONTABLE").getValor());
                    cmst.setString(12, numero_externo);
                    cmst.setInt(13, tipoProceso);
                    cmst.setInt(14, Activo);
                    cmst.setInt(15, nPorcentajeIVA);
                    //cboUnidadEjecutoraInmueble
                    cmst.setString(16, cboUnidadEjecutoraInmueble);
                    //cboDenominacionInmueble
                    cmst.setInt(17, cboDenominacionInmueble);
                    //descripcionOtros
                    cmst.setString(18, descripcionOtros);
                    //area_construida
                    cmst.setFloat(19, area_construida);
                    //area_rentable
                    cmst.setFloat(20, area_rentable);
                    //numero_empleados
                    cmst.setFloat(21, numero_empleados);
                    //direccion_inmueble
                    cmst.setString(22, direccion_inmueble);
                    cmst.setInt(23, isPlurianual);
                    cmst.execute();
                    mensaje = "El Procedimiento  se ha creado Correctamente.";
                    respuesta = "1";
                } else {
                    log.info("query que crea el procedimiento : sp_mProcedimiento");
                    cmst = conn.prepareCall("{call sp_mProcedimiento (?,?,?,?,?,?,?,?,?,?,?,?,?,? ,?,?)}");
                    cmst.setString(1, cEjercicio);
                    cmst.setString(2, cIdTipoProcedimiento);
                    cmst.setString(3, cIdUnidadEjecutora);
                    cmst.setInt(4, consecutivoProc);
                    cmst.setString(5, consolidado[0]);
                    cmst.setString(6, consolidado[2]);
                    cmst.setString(7, categoria);
                    cmst.setString(8, descripcion);
                    cmst.setString(9, usuario.getLogin());
                    cmst.setInt(10, 1);
                    cmst.setString(11, usuario.getPropiedad("CCENTROCONTABLE").getValor());
                    cmst.setString(12, numero_externo);
                    cmst.setInt(13, tipoProceso);
                    cmst.setInt(14, Activo);
                    cmst.setInt(15, nPorcentajeIVA);
                    cmst.setInt(16, isPlurianual);
                    cmst.execute();
                    mensaje = "El Procedimiento  se ha creado Correctamente.";
                    respuesta = "1";
                }
                //Insertar requisitos al procedimiento cuando se cumplan las condiciones
                int cProcedimientoCumple = 1;
                if ("PA".equalsIgnoreCase(cIdTipoProcedimiento) || "PL".equalsIgnoreCase(cIdTipoProcedimiento)) {
                    cProcedimientoCumple = 0;
                    String query3 = "insert into mRequisitoProcedimiento (cIdRequisito, cIdProcedimiento, cRequerido, cCumple, cObservaciones) " + "select r.cIdRequisito, p.cIdProcedimiento, r.cRequerido, '' as cCumple, '' as cObservacones from mProcedimiento as p WITH (NOLOCK)," + " mCatalogoRequisitosProcedimiento as r WITH (NOLOCK) where p.cIdProcedimiento ='" + cIdProcedimiento + "' and (p.cIdTipoProcedimiento = 'PS' or p.cIdTipoProcedimiento = 'PN')";
                    log.info("query que agrega los requisitos a un procedimiento de arrendamiento :" + query3);
                    pstmnt = conn.prepareStatement(query3);
                    pstmnt.executeUpdate();
                    respuesta = "1";
                }
                String query4 = "UPDATE mProcedimiento set cCumpleRequisitos =" + cProcedimientoCumple + " where cIdProcedimiento = '" + cIdProcedimiento + "'";
                pstm = conn.prepareStatement(query4);
                pstm.executeUpdate();
                //Insertar Fechas del procedimiento
                //1|01/10/2014,2|02/10/2014,4|03/10/2014,12|14/10/2014,13|31/10/2014
                String cadenaFechas = request.getParameter("arrayFechas");
                int cantidadFechas = Integer.parseInt(request.getParameter("cantidadFechas"));
                String[] arrayFecha = cadenaFechas.split(",");
                String[] numFecha;
                String query5 = "";
                int i;
                int cont = 0;
                log.info("cadena de fechas : " + cadenaFechas);
                for (i = 0; i < arrayFecha.length; i++) {
                    numFecha = arrayFecha[i].split("-");
                    query5 = "INSERT INTO mProcedimientoFechas VALUES('" + numFecha[0] + "','" + cIdProcedimiento + "',CONVERT(DATE,'" + numFecha[1] + "',103))";
                    log.info("query que agrega las fechas del procedimiento : " + query5);
                    pstmntFechas = conn.prepareStatement(query5);
                    pstmntFechas.executeUpdate();
                    cont++;
                }
                if (cont != cantidadFechas) {
                    mensaje = "Error al guardar fechas";
                    respuesta = "0";
                    jsonObj.put("respuesta", respuesta);
                    jsonObj.put("cEjercicio", mensaje);
                    conn.rollback();
                    log.error("Error al guardar fechas.");
                    return;
                }
                //			cmstFechas = conn.prepareCall("{call pa_mInsertaFechasProcedimiento (?,?)}");
                //			cmstFechas.setString(1, cadenaFechas);
                //			cmstFechas.setString(2, cIdProcedimiento);
                //			cmstFechas.execute();
                respuesta = "1";
                conn.commit();
                jsonObj.put("respuesta", respuesta);
                jsonObj.put("cEjercicio", cEjercicio);
                jsonObj.put("cIdTipoProcedimiento", cIdTipoProcedimiento);
                jsonObj.put("cIdUnidadEjecutora", cIdUnidadEjecutora);
                jsonObj.put("nIdConsecutivo", consecutivoProc);
            } catch (SQLException exc) {
                // TODO: handle exception
                conn.rollback();
                log.error("Error de la creación del procedimiento.");
                exc.printStackTrace();
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            try {
                conn.rollback();
                respuesta = "0";
                mensaje = "Error al crear el procedimiento.";
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            try {
                jsonObj.put("respuesta", "0");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (ps != null)
                    ps.close();
                if (cmst != null)
                    cmst.close();
                if (cmstFechas != null)
                    cmstFechas.close();
                if (ps2 != null)
                    ps2.close();
                if (pstm != null)
                    pstm.close();
                if (pstmnt != null)
                    pstmnt.close();
                if (pstmntFechas != null)
                    pstmntFechas.close();
                if (rs != null)
                    rs.close();
                if (rs2 != null)
                    rs2.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            try {
                jsonObj.put("respuesta", respuesta);
                jsonObj.put("mensaje", mensaje);
                String destino = arrayObj.put(jsonObj).toString();
                out.println(destino);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            conn = null;
            cmst = null;
            ps = null;
            ps2 = null;
            cmstFechas = null;
            pstmntFechas = null;
            pstm = null;
            pstmnt = null;
            rs = null;
            rs2 = null;
        }
    }

    private List parseRequest(HttpServletRequest req, String idSession) throws ServletException {
        DiskFileUpload upload = new DiskFileUpload();
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

    private void cancelaPrecomConsolidado(HttpSession session, HttpServletRequest request, HttpServletResponse response, String cIdConsolidado) throws IOException {
        session = request.getSession(false);
        out = response.getWriter();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        String cCentroContable = "";
        String mensaje = "";
        if (usuario == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
            cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
        }
        if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
            mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
        }
        log.debug("Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()));
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        try {
            conn = cbl.getConnection();
            AccountingEngine accEng = new AccountingEngine();
            accEng.setValidaInsuficienciaDeSaldo(false);
            ps = conn.prepareStatement("select a.nFolioPrecomMateriales from tPrecomMaterialesEncabezado a inner join mConsolidado b on a.cIdConsolidado=b.cIdConsolidado " + " and a.nFolioPrecomMateriales=b.ConsecutivoPRECOMP " + " where a.cDocumentoHaplicado='s' and b.cIdConsolidado='" + cIdConsolidado + "'");
            rs = ps.executeQuery();
            String folioPrecom = String.valueOf(rs.getInt(0));
            accEng.cancelAccountingApplication(conn, "PRECOMMATERIALES", folioPrecom, "tPrecomMaterialesEncabezado", "tPrecomMaterialesDetalle", "nFolioPrecomMateriales");
            conn.commit();
        } catch (Exception e) {
            log.error("Error en Aplicacion contable:" + e.getMessage());
            try {
                jsonObj.put("Devuelve", "0");
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
            mensaje = "ERROR INESPERADO DE LA CANCELACION CONTABLE(MOTOR CONTABLE O NO AVANZO EL CASO).FAVOR DE INTENTAR NUEVAMENTE";
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (cmst != null)
                    cmst.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            try {
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

    private synchronized void generaGuardaCasoConsolidado(HttpServletRequest request, HttpServletResponse response, HttpSession session, String tipoCaso, String CONCEPTO_MOV) throws ServletException, IOException {
        //Metodo utilizado para generar los casos de Precompromiso y Compromiso
        out = response.getWriter();
        int folio;
        String folioCaso = null;
        try {
            String ur = request.getParameter("ur");
            Caso c = null;
            c = iniciaCaso(request, tipoCaso);
            folioCaso = c.getFolio();
            int indice = folioCaso.lastIndexOf('-') + 1;
            folio = Integer.parseInt(folioCaso.substring(indice));
            try {
                jsonObj.put("Folio1", "" + folio);
                jsonObj.put("Folio2", "" + folioCaso);
                log.debug("Folio+++++++++++++++++++" + folio + "++++++++++++++++++++++++++++");
                log.debug("Folio+++++++++++++++++++" + folioCaso + "++++++++++++++++++++++++++++");
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
                String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
                avanzaCaso(request, c, usuario, prefixPath, new String[] { "CONSULTA_PRECOMPROMISO" }, new String[] { "consulta_precomp" });
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
}
