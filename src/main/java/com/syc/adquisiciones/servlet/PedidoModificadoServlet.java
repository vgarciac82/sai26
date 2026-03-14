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
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.CasoBusinessLogic;
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
import java.util.Map;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "PedidoModificadoServlet", urlPatterns = { "/servlet/PedidoModificadoServlet" })
public class PedidoModificadoServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 1L;

    private static String jndiName = null;

    private static Logger log = LoggerFactory.getLogger(PedidoModificadoServlet.class);

    private Connection conn = null;

    private Connection conn1 = null;

    private CallableStatement cmst = null;

    private JSONArray arrayObj;

    private JSONObject jsonObj;

    private PrintWriter out = null;

    private String folioGenerator = null;

    private Usuario usuario;

    private String cEjercicio;

    private String today;

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
        cEjercicio = (String) session.getAttribute(GestionInterface.ATT_PedidoModificatorioEjercicio);
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
        log.debug("Object: {}", "operacion: " + tipoOperacion);
        String strParam = request.getParameter("Param");
        switch(tipoOperacion) {
            case 0:
                {
                    apruebaContratoDiverso(strParam, request, response);
                    break;
                }
            case 1:
                {
                    devuelveContratoDiverso(strParam, response);
                    break;
                }
            case 2:
                {
                    //El preCompromiso es un tipo de caso 14
                    generaGuardaCaso(request, response, session, (GestionInterface.IDTC_PRECOMPROMISO + ""), "Aplicación de PreCompromiso");
                    break;
                }
            case 3:
                {
                    //Al momento de aplicar contablemente el precompromiso se cambia a ventanilla de precompromiso para que sea aprobado
                    aplicaContablemente(strParam, request, response, session, new String[] { "VENTANILLA_PRECOMPROMISO" }, new String[] { "autoriza_precomp" });
                    break;
                }
            case 4:
                {
                    //Al devolver el precompromiso se cancela contablemente y se regresa a un tipo de usuario sin asignar
                    String tipoPago = request.getParameter("tipoPago");
                    if (tipoPago.equals("1"))
                        devuelveContablementeDes(request, response, session, new String[] { "CAPTURA_PRECOMPROMISO" }, new String[] { "captura_precomp" });
                    else
                        devuelveContablemente(request, response, session, new String[] { "CAPTURA_PRECOMPROMISO" }, new String[] { "captura_precomp" });
                    break;
                }
            case 5:
                {
                    //Siver para el JSP de ventanilla para validar datos como Fechas, montos y proveedor
                    validaPrecompromiso(strParam, response);
                    break;
                }
            case 6:
                {
                    //El Compromiso es un tipo de caso 7
                    generaGuardaCaso(request, response, session, (GestionInterface.IDTC_COMPROMISO + ""), "Aplicación de Compromiso");
                    break;
                }
            case 7:
                {
                    //consulta_compromiso	CONSULTA_PAGOS. Despues de generar el compromiso se envia al grupo de CONSULTA_PAGOS para que sea aprobado.
                    aplicaContablemente(strParam, request, response, session, new String[] { "CONSULTA_PAGOS" }, new String[] { "consulta_compromiso" });
                    break;
                }
            case 8:
                {
                    //al momento de crear el compromiso, se necesita avanzar el caso de precompromiso para que ya no aparezca en el Inbox
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
                }
            case 9:
                {
                    //Devuelve contablemente desde ventanilla
                    devuelveContablementeVentanilla(request, response, session, new String[] { "CAPTURA_PRECOMPROMISO" }, new String[] { "captura_precomp" });
                    break;
                }
        }
        java.util.Date df = new java.util.Date();
        System.out.println("Finaliza time: " + df.toString());
        System.out.println("Segundos diferencia:  " + (df.getTime() - di.getTime()) / 1000);
    }

    private Caso getCaso(HttpSession session) {
        //Debido a que el Modulo de adquisiciones y servicios no se encuentra en un flujo pero se necesita crear un caso para el precompromiso
        // se creo este método que obtiene el ID de caso desde base de datos y no de sesión
        String sql;
        Integer consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_PedidoModificatorioConsecutivo));
        String definitivo = (String) session.getAttribute(GestionInterface.ATT_PedidoModificatorioDefinitivo);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        //Apartir de los atributos guardados en sesión se hace un QRY para obtener el ID_CASO
        sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mPedidoModificado p, CG_CASO c, CG_CASO_OPERACION o " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cIdPedidoDefinitivo=?" + " and p.nConsecutivoModificacion=?";
        Caso c = null;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, definitivo);
            pstmt.setInt(2, consecutivo);
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

    private Caso getCaso(HttpSession session, String centroContable, String ur) {
        //Debido a que el Modulo de adquisiciones y servicios no se encuentra en un flujo pero se necesita crear un caso para el precompromiso
        // se creo este método que obtiene el ID de caso desde base de datos y no de sesión
        String sql = "";
        Integer consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_PedidoModificatorioConsecutivo));
        String definitivo = (String) session.getAttribute(GestionInterface.ATT_PedidoModificatorioDefinitivo);
        String docDefinitivo = definitivo + "#M" + consecutivo;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        //Apartir de los atributos guardados en sesión se hace un QRY para obtener el ID_CASO
        Caso c = null;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            if (docDefinitivo.indexOf("#M") > 0) {
                sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mDocumentoFolio p, CG_CASO c, CG_CASO_OPERACION o " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cIdDocumentoDefinitivo=?" + " and p.cCentroContable=?" + " and p.cIdUnidadResponsable=?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, docDefinitivo);
                pstmt.setString(2, centroContable);
                pstmt.setString(3, ur);
            } else {
                sql = "SELECT TOP 1 MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mReduccionPedidoModificatorio p, CG_CASO c, CG_CASO_OPERACION o " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cIdPedidoDefinitivo+'#R'+convert(varchar,nConsecutivoModificacion)=?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, docDefinitivo);
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

    private Caso getCasoVentanilla(String docDefinitivo) {
        //Debido a que el Modulo de adquisiciones y servicios no se encuentra en un flujo pero se necesita crear un caso para el precompromiso
        // se creo este método que obtiene el ID de caso desde base de datos y no de sesión
        String sql = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int outputValue = 0;
        if (docDefinitivo.indexOf("#M") > 0) {
            sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mPedidoModificado p, CG_CASO c, CG_CASO_OPERACION o " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cPedidoDefinitivo=?";
        } else {
            sql = "SELECT TOP 1 MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mReduccionPedidoModificatorio p, CG_CASO c, CG_CASO_OPERACION o " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cIdPedidoDefinitivo+'#R'+convert(varchar,nConsecutivoModificacion)=?";
        }
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
            if (docDefinitivo.indexOf("#R") > 0) {
                cmst = conn.prepareCall("{?= call sp_mDevuelveReduccion (?,?)}");
                cmst.registerOutParameter(1, Types.INTEGER);
                //@cIdDocumentoDefinitivo
                cmst.setString(2, docDefinitivo);
                //@tipo
                cmst.setString(3, "PEDIDO");
                cmst.execute();
                outputValue = cmst.getInt(1);
                conn.commit();
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

    private Caso getCasoVentanilla(String docDefinitivo, String centroContable, String ur) {
        //Debido a que el Modulo de adquisiciones y servicios no se encuentra en un flujo pero se necesita crear un caso para el precompromiso
        // se creo este metodo que obtiene el ID de caso desde base de datos y no de sesión
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int outputValue = 0;
        String sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mDocumentoFolio p, CG_CASO c, CG_CASO_OPERACION o " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cIdDocumentoDefinitivo=?" + " and p.cCentroContable=?" + " and p.cIdUnidadResponsable=?";
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
            if (docDefinitivo.indexOf("#R") > 0) {
                cmst = conn.prepareCall("{?= call sp_mDevuelveReduccion (?,?)}");
                cmst.registerOutParameter(1, Types.INTEGER);
                //@cIdDocumentoDefinitivo
                cmst.setString(2, docDefinitivo);
                //@tipo
                cmst.setString(3, "PEDIDO");
                cmst.execute();
                outputValue = cmst.getInt(1);
                conn.commit();
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
            // El stored procedurre se encarga de validar  que sea un RFC valido, que las fechas tengan el orden adecuado y que los montos coincidan
            // Los parámetros que recibe son cEjercicio y cIdContrato
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
                if (cmst != null)
                    cmst.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
        }
        cmst = null;
        conn = null;
    }

    @SuppressWarnings("unchecked")
    private synchronized void aplicaContablemente(String strParam, HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre) throws ServletException, IOException {
        //Este método es una copia del método de financiero
        //Se hizo una copia para poder recibir el error
        session = request.getSession(false);
        out = response.getWriter();
        String validaSaldo = "";
        ArrayList<String> arrLResult = new ArrayList<String>();
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
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
            jsonObj.put("Contable1", mensaje);
            String destino = arrayObj.put(jsonObj).toString();
            out.println(destino);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private synchronized void devuelveContablemente(HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre) throws ServletException, IOException {
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
                conn.commit();
            } else {
                conn.rollback();
            }
            //Recargando el caso
            Caso sc = new Caso();
            sc.setIdCaso(c.getIdCaso());
            c = CasoManager.select(conn, sc);
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
    private synchronized void devuelveContablementeDes(HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre) throws ServletException, IOException {
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
        log.debug("Object: {}", "Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()));
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        Connection conncbl = null;
        AplicarContableReturn acr = null;
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            stmt = conn.createStatement();
            Integer consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_PedidoModificatorioConsecutivo));
            String definitivo = (String) session.getAttribute(GestionInterface.ATT_PedidoModificatorioDefinitivo);
            String queryCons = "select distinct centroContable, left(claveInterna,3) from fn_mApartadoPedidoModificado('" + definitivo + "', " + consecutivo + ")";
            rs = stmt.executeQuery(queryCons);
            conncbl = cbl.getConnection();
            while (rs.next()) {
                String centroContable = rs.getString(1);
                String ur = rs.getString(2);
                Caso c = getCaso(session, centroContable, ur);
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
                if (stmt != null)
                    stmt.close();
                if (rs != null)
                    rs.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
            stmt = null;
            rs = null;
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
        log.debug("Object: {}", "Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()));
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        Connection conncbl = null;
        AplicarContableReturn acr = null;
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            conncbl = cbl.getConnection();
            String tipoPago = request.getParameter("nTipoPago");
            String centroContable = request.getParameter("cContableVentanilla");
            String ur = request.getParameter("ur");
            String docDefinitivo = request.getParameter("contratoDefinitivo");
            Caso c = null;
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

    private synchronized void apruebaContratoDiverso(String strParam, HttpServletRequest request, HttpServletResponse response) throws ServletException {
        // se utliza para generar contratos diversos
        Statement stmt = null;
        ResultSet rs = null;
        String[] param = strParam.split(",");
        try {
            out = response.getWriter();
            int outputValue;
            int folio = -1;
            String folioCaso = null;
            conn = DataSourceManager.getConnection(jndiName);
            stmt = conn.createStatement();
            String queryCons = "SELECT C_FOLIO, ConsecutivoCDIV FROM mPedidoModificado WITH (NOLOCK) where cIdPedidoDefinitivo = '" + param[1] + "' AND nConsecutivoModificacion = " + param[2];
            rs = stmt.executeQuery(queryCons);
            if (rs.next()) {
                folioCaso = rs.getString(1);
                folio = rs.getInt(2);
            }
            /*
			try {
				//Comentado porque no se debe generar un nuevo contrato diverso
				//Y el try porque al no haber caso, no hay gestion exception
				if(folioCaso == null){
					//Genera un tipo de caso de Contrato Diverso
					Caso c = iniciaCaso(request, "9");
					folioCaso = c.getFolio();
					int indice = folioCaso.lastIndexOf('-') + 1;
					 folio = Integer.parseInt(folioCaso.substring(indice));
						//se avanza caso para que no se vea la operacion  en el inbox
					 String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
					 avanzaCaso(request, c, usuario, prefixPath, new String[] {"CONSULTA_CONTRATODIVERSO"}, new String[] {"consulta_contrato"});
					 
				}
				*/
            cmst = conn.prepareCall("{? = call pa_apruebaPedidoModificado (?,?,?,?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, param[0]);
            cmst.setString(3, param[1]);
            cmst.setInt(4, Integer.parseInt(param[2]));
            cmst.setString(5, param[3]);
            cmst.setString(6, folio + "");
            cmst.setString(7, folioCaso);
            //log.debug("call pa_apruebaPedidoModificado "+param[0]+","+param[1]+","+param[2]+","+param[3]+","+folio+","+folioCaso);
            cmst.execute();
            outputValue = cmst.getInt(1);
            if (outputValue == 0) {
                conn.commit();
            } else
                conn.rollback();
            /*} catch (GestionException exc) {
				log.error("Iniciando Caso", exc);
				throw new ServletException(exc);
			}*/
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
                if (stmt != null)
                    stmt.close();
                if (rs != null)
                    rs.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            cmst = null;
            conn = null;
            stmt = null;
            rs = null;
        }
    }

    //	private synchronized void apruebaContratoDiversoDes(String strParam, HttpServletRequest request,HttpServletResponse response) throws ServletException {
    //		// se utliza para generar contratos diversos
    //		String param[] = strParam.split(",");
    //		try {
    //			out = response.getWriter();
    //			int outputValue = 0;
    //			conn = DataSourceManager.getConnection(jndiName);
    //			Statement stmt = conn.createStatement();
    //			String queryCons = "select distinct centroContable, left(claveInterna,3) from fn_mApartadoPedidoModificado('" + param[1] + "', " +  param[2] + ")";
    //			ResultSet rs = stmt.executeQuery(queryCons);
    //			while (rs.next()){
    //				try {
    //					String centroContable = rs.getString(1);
    //					String ur = rs.getString(2);
    //					//Genera un tipo de caso de Contrato Diverso
    //					Caso c = iniciaCasoDes(usuario, ur, "9");
    //					String folioCaso = c.getFolio();
    //					int indice = folioCaso.lastIndexOf('-') + 1;
    //					int folio = Integer.parseInt(folioCaso.substring(indice));
    //					//se avanza caso para que no se vea la operacion  en el inbox
    //					String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
    //					avanzaCaso(request, c, usuario, prefixPath, new String[] {"CONSULTA_CONTRATODIVERSO"}, new String[] {"consulta_contrato"});
    //
    //					cmst = conn.prepareCall("{? = call pa_apruebaPedidoModificadoDes (?,?,?,?,?,?,?)}");
    //					cmst.registerOutParameter(1, Types.INTEGER);
    //					cmst.setString(2, param[0]);
    //					cmst.setString(3, param[1]);
    //					cmst.setInt(4, Integer.parseInt(param[2]));
    //					cmst.setString(5, param[3]);
    //					cmst.setString(6, folio + "");
    //					cmst.setString(7, folioCaso);
    //					cmst.setString(8, centroContable);
    //					cmst.execute();
    //					outputValue = cmst.getInt(1);
    //					if (outputValue != 0)
    //						conn.rollback();
    //				} catch (GestionException exc) {
    //					log.error("Iniciando Caso", exc);
    //					throw new ServletException(exc);
    //				}
    //			}
    //
    //			conn.commit();
    //
    //			try {
    //				jsonObj.put("Col1", "" + outputValue);
    //				String destino = arrayObj.put(jsonObj).toString();
    //				out.println(destino);
    //			} catch (JSONException e1) {
    //				e1.printStackTrace();
    //			}
    //		} catch (SQLException e1) {
    //			e1.printStackTrace();
    //		} catch (IOException e1) {
    //			e1.printStackTrace();
    //		} finally {
    //			try {
    //				if (conn != null)
    //					conn.close();
    //				if (cmst != null)
    //					cmst.close();
    //			} catch (SQLException exc) {
    //				log.warn("Cerrando conexion a base de datos", exc);
    //			}
    //			cmst = null;
    //			conn = null;
    //		}
    //	}
    private synchronized void devuelveContratoDiverso(String strParam, HttpServletResponse response) {
        String[] param = strParam.split(",");
        try {
            out = response.getWriter();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            //Elimina los registros de contrato diverso de las tablas pContratoDiverso y tContratoEP
            conn = DataSourceManager.getConnection(jndiName);
            cmst = conn.prepareCall("{? = call pa_devuelvePedidoModificado (?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, param[0]);
            cmst.setString(3, param[1]);
            cmst.setInt(4, Integer.parseInt(param[2]));
            cmst.execute();
            int outputValue = cmst.getInt(1);
            if (outputValue == 0)
                conn.commit();
            else
                conn.rollback();
            System.out.println("Parametro de salida del procedimiento=" + outputValue);
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
        }
        conn1 = null;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    @SuppressWarnings("unchecked")
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
}
