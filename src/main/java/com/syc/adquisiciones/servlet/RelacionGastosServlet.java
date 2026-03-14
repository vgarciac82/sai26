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
import com.syc.contable.AccountingEngine;
import com.syc.contable.CompromisoBussinessLogic;
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

@WebServlet(name = "RelacionGastosServlet", urlPatterns = { "/servlet/RelacionGastosServlet" })
public class RelacionGastosServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 1L;

    private static String jndiName = null;

    private static Logger log = LoggerFactory.getLogger(RelacionGastosServlet.class);

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
        //Obtiene la operación que se manda como parámetro en la llamada GET
        int tipoOperacion = Integer.parseInt(request.getParameter("operacion"));
        log.debug("Object: {}", "operacion: " + tipoOperacion);
        String strParam = request.getParameter("Param");
        //String tipoPago = "";
        switch(tipoOperacion) {
            case 1:
                //El preCompromiso es un tipo de caso 14
                generaGuardaCaso(request, response, session, (GestionInterface.IDTC_RELACIONGASTO + ""), "Aplicación Relacion de Gastos");
                break;
            case 2:
                //Al momento de aplicar contablemente el precompromiso se cambia a ventanilla de precompromiso para que sea aprobado
                aplicaContablementeApartado(strParam, request, response, session, new String[] { "AUTORIZA_RELACIONGASTOS" }, new String[] { "autoriza_factura" });
                break;
            case 3:
                devuelveContablemente(strParam, request, response, session, new String[] { "CONSULTA_RELACIONGASTOS" }, new String[] { "consulta_factura" });
                break;
            case 4:
                copiaRelacionGastos(session, response, request);
                break;
        }
        java.util.Date df = new java.util.Date();
        System.out.println("Finaliza time: " + df.toString());
        System.out.println("Segundos diferencia:  " + (df.getTime() - di.getTime()) / 1000);
    }

    private synchronized void aplicaContablementeApartado(String strParam, HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre) throws ServletException, IOException {
        session = request.getSession(false);
        out = response.getWriter();
        //Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        PreparedStatement pstm = null, pstm1 = null;
        CallableStatement cmst1 = null;
        Connection conn = null, conn1 = null;
        AccountingEngine accEng = new AccountingEngine();
        accEng.setValidaInsuficienciaDeSaldo(true);
        String cIdDocumento = (String) session.getAttribute(GestionInterface.ATT_RelacionGastosFolio);
        String folioApartadoMake = request.getParameter("nFolioApartado");
        String cCentroContable = "";
        String mensaje = "";
        Caso c;
        if ((c = (Caso) session.getAttribute(GestionInterface.ATT_CASE)) == null)
            c = getCaso(session);
        if (c == null) {
            log.error("Error en Aplicacion contable:");
            return;
        }
        //Valida Centro de Costos
        //Validaciones de financiero
        if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE"))
            cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
        if (cCentroContable.isEmpty() || cCentroContable.equals(""))
            mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
        log.debug("Object: {}", "Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()));
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        try {
            conn = cbl.getConnection();
            conn1 = cbl.getConnection();
            if (accEng.makeAccountingApplication(conn, "PAGOAPARTADO", folioApartadoMake, "tPagoApartadoEncabezado", "tPagoApartadoDetalle", "nFolioPagoApartado")) {
                jsonObj.put("Aplica", "1");
                pstm = conn.prepareStatement("UPDATE mRelacionGastos SET nIdEstado = 3  , ConsecutivoRELGASTOSAPA=? " + " WHERE cIdDocumento = ? " + " and cEjercicio=?");
                pstm.setInt(1, Integer.parseInt(folioApartadoMake));
                pstm.setString(2, cIdDocumento);
                pstm.setString(3, request.getParameter("cEjercicio"));
                pstm.executeUpdate();
                //Recargando el caso
                Caso sc = new Caso();
                sc.setIdCaso(c.getIdCaso());
                c = CasoManager.select(conn, sc);
                // Una vez que ha hecho la aplicación contable avanza el caso A CONSULTA PAGOS
                avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                log.debug("Object: {}", c.getCasoDato("APLICADO_CONT").getValor());
                log.debug("Object: {}", "Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
                mensaje = "DOCUMENTO DE PRECOMPROMISO APLICADO CONTABLEMENTE.";
                pstm1 = conn.prepareStatement("delete from CG_CASO_OPERACION where ID_CASO=(select ID_CASO from CG_CASO with(nolock) where  C_FOLIO=? ) and (CO_RESPONSABLE='VENTANILLA_RELACIONGASTOS' or CO_RESPONSABLE='CONSULTA_RELACIONGASTOS') and (ID_OPER=1 OR ID_OPER=3)");
                pstm1.setString(1, c.getFolio());
                pstm1.executeUpdate();
                conn.commit();
            } else {
                conn.rollback();
                jsonObj.put("Aplica", "0");
                cmst1 = conn1.prepareCall("{call pa_fallaAplicacionContableRelacionGastos (?,?,?,?)}");
                cmst1.setString(1, request.getParameter("cEjercicio"));
                cmst1.setString(2, request.getParameter("nFolioRelacionGastos"));
                cmst1.setString(3, cIdDocumento);
                cmst1.setString(4, folioApartadoMake);
                mensaje = "NO SE PUDO APLICAR CONTABLEMENTE.";
                conn1.commit();
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
                if (pstm != null)
                    pstm.close();
                if (pstm1 != null)
                    pstm1.close();
                if (cmst1 != null)
                    cmst1.close();
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
            conn1 = null;
            cmst1 = null;
            pstm = null;
            pstm1 = null;
        }
    }

    private synchronized void devuelveContablemente(String strParam, HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre) throws ServletException, IOException {
        session = request.getSession(false);
        out = response.getWriter();
        PreparedStatement pstm = null, pstm1 = null;
        CallableStatement cmst1 = null;
        Connection conn = null, conn1 = null;
        AccountingEngine accEng = new AccountingEngine();
        accEng.setValidaInsuficienciaDeSaldo(true);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        String cEjercicio = (String) session.getAttribute(GestionInterface.ATT_RelacionGastosEjercicio);
        String cIdDocumento = (String) session.getAttribute(GestionInterface.ATT_RelacionGastosFolio);
        String folioApartadoMake = request.getParameter("nFolioApartado");
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
            if (accEng.cancelAccountingApplication(conn, "PAGOAPARTADO", folioApartadoMake, "tPagoApartadoEncabezado", "tPagoApartadoDetalle", "nFolioPagoApartado")) {
                jsonObj.put("Devuelve", "1");
                pstm = conn.prepareStatement("UPDATE mRelacionGastos SET nIdEstado = 1 , ConsecutivoRELGASTOSAPA=NULL , C_FOLIO_RELGASTOSAPA = NULL " + " WHERE cIdDocumento = ? " + " and cEjercicio=?");
                pstm.setString(1, cIdDocumento);
                pstm.setString(2, cEjercicio);
                pstm.executeUpdate();
                //borramos las tablas que cargamos anteriormente
                cmst1 = conn.prepareCall("{call borraTablasRelacionGastosMateriales (?,?,?,?)}");
                cmst1.setInt(1, Integer.parseInt(request.getParameter("nFolioRelacionGastos")));
                cmst1.setString(2, request.getParameter("cEjercicio"));
                cmst1.setString(3, cIdDocumento);
                cmst1.setInt(4, Integer.parseInt(request.getParameter("nFolioApartado")));
                cmst1.execute();
                //Recargando el caso
                Caso sc = new Caso();
                sc.setIdCaso(c.getIdCaso());
                c = CasoManager.select(conn, sc);
                avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                mensaje = "DOCUMENTO DE PRECOMPROMISO CANCELADO CONTABLEMENTE";
                conn.commit();
            } else {
                conn.rollback();
                jsonObj.put("Devuelve", "0");
                pstm1 = conn1.prepareStatement("UPDATE mRelacionGastos SET nIdEstado = 3 " + " WHERE cIdDocumento = ? " + " and cEjercicio=?");
                pstm1.setString(1, cIdDocumento);
                pstm1.setString(2, cEjercicio);
                pstm1.executeUpdate();
                conn1.commit();
            }
        } catch (Exception e) {
            try {
                conn.rollback();
                conn1.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            try {
                jsonObj.put("Devuelve", "0");
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
                if (pstm != null)
                    pstm.close();
                if (pstm1 != null)
                    pstm1.close();
                if (cmst1 != null)
                    cmst1.close();
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
            conn1 = null;
            cmst1 = null;
            pstm = null;
            pstm1 = null;
        }
    }

    private Caso getCaso(HttpSession session) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql;
        String cEjercicio = (String) session.getAttribute(GestionInterface.ATT_RelacionGastosEjercicio);
        String cIdDocumento = (String) session.getAttribute(GestionInterface.ATT_RelacionGastosFolio);
        //Apartir de los atributos gurdados en sesión se hace un QRY para obtener el ID_CASO
        sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mRelacionGastos rg (NOLOCK), CG_CASO c (NOLOCK), CG_CASO_OPERACION o (NOLOCK)" + " where rg.C_FOLIO_RELGASTOS=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and rg.cEjercicio=?" + " and rg.cIdDocumento=? ";
        Caso c = null;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cEjercicio);
            pstmt.setString(2, cIdDocumento);
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

    private synchronized void generaGuardaCaso(HttpServletRequest request, HttpServletResponse response, HttpSession session, String tipoCaso, String CONCEPTO_MOV) throws ServletException, IOException {
        //Metodo utilizado para generar los casos de Precompromiso y Compromiso
        out = response.getWriter();
        int folio;
        String folioCaso = null;
        Connection conn1 = null;
        try {
            Caso c = null;
            if (c == null) {
                c = iniciaCaso(request, tipoCaso);
                if (c == null) {
                    log.debug("no se pudo crear");
                    return;
                }
            }
            String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
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
                avanzaCaso(request, c, usuario, prefixPath, new String[] { "CONSULTA_RELACIONGASTOS" }, new String[] { "consulta_factura" });
                session.setAttribute(GestionInterface.ATT_CASE, c);
                conn1.commit();
                out.println(destino);
            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (SQLException eSQL) {
                try {
                    conn1.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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

    private void copiaRelacionGastos(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        int outputValue = -1;
        Connection conn = null;
        CallableStatement cmst = null;
        String link = "-1";
        try {
            out = response.getWriter();
            conn = DataSourceManager.getConnection(jndiName);
            cmst = conn.prepareCall("{?=call pa_copiaRelacionGastosMaterialesPagoDirecto (?,?,?,?,?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, request.getParameter("cIdDocumento"));
            cmst.setString(3, request.getParameter("cEjercicio"));
            cmst.setString(4, request.getParameter("cIdUnidadEjecutora"));
            cmst.setInt(5, Integer.parseInt(request.getParameter("nFolioRelacionGastosNew")));
            cmst.setInt(6, Integer.parseInt(request.getParameter("nFolioRelacionGastosOld")));
            cmst.setString(7, "RELACION_GASTOS");
            cmst.setString(8, request.getParameter("folioCasoRelacionGastosNew"));
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

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
