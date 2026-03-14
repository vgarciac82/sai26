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
import com.syc.adquisiciones.businessLogic.ContratoModificadoBusiness;
import com.syc.adquisiciones.core.ContratoModificado;
import com.syc.adquisiciones.util.Util;
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
import java.util.List;
import java.util.Map;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "ContratoModificadoServlet", urlPatterns = { "/servlet/ContratoModificadoServlet" })
public class ContratoModificadoServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 1L;

    private static String jndiName = null;

    private static Logger log = LoggerFactory.getLogger(ContratoModificadoServlet.class);

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
        cEjercicio = (String) session.getAttribute(GestionInterface.ATT_ContratoModificatorioEjercicio);
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
        ContratoModificadoBusiness contModB = null;
        String msg = null;
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
                break;
            case 4:
                //Al devolver el precompromiso se cancela contablemente y se regresa a un tipo de usuario sin asignar
                String tipoPago = request.getParameter("tipoPago");
                if (tipoPago.equals("1"))
                    devuelveContablementeDes(strParam, request, response, session, new String[] { "CONSULTA_DEVOLUCION" }, new String[] { "consulta_devolucion" });
                else
                    devuelveContablemente(strParam, request, response, session, new String[] { "CONSULTA_DEVOLUCION" }, new String[] { "consulta_devolucion" });
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
                aplicaContablemente(strParam, request, response, session, new String[] { "CONSULTA_PAGOS" }, new String[] { "consulta_compromiso" });
                break;
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
                //Devuelve contablemente desde ventanilla
                devuelveContablementeVentanilla(request, response, session, new String[] { "CONSULTA_DEVOLUCION" }, new String[] { "consulta_devolucion" });
                break;
            case 10:
                //Devuelve contablemente desde ventanilla
                checaFechasModificatorio(request, response);
                break;
            case 11:
                //Guarda partidas de contrato modificado del tipo de mod 4 "Cambio de UE"
                String cadTabla = request.getParameter("arregloDatos");
                String cIdContratoDefinitivo = request.getParameter("cIdContratoDefinitivo");
                int nConsecutivoMod = (null == request.getParameter("nConsecutivoMod") || "".equalsIgnoreCase(request.getParameter("nConsecutivoMod")) ? 0 : Integer.parseInt(request.getParameter("nConsecutivoMod")));
                String[] arrayTabla = cadTabla.split(",");
                ArrayList<List<String>> tabla;
                try {
                    out = response.getWriter();
                    tabla = Util.creaArray(arrayTabla);
                    contModB = new ContratoModificadoBusiness();
                    msg = contModB.guardaCambioUEPartidasContMod(cIdContratoDefinitivo, nConsecutivoMod, tabla, usuario);
                } catch (Exception e) {
                    msg = e.getMessage();
                    e.printStackTrace();
                } finally {
                    try {
                        jsonObj.put("MENSAJE", "" + msg);
                    } catch (JSONException e) {
                        msg = e.getMessage();
                        e.printStackTrace();
                    }
                    String destino = arrayObj.put(jsonObj).toString();
                    out.println(destino);
                }
                break;
            case //Valida y guarda fechas
            12:
                contModB = new ContratoModificadoBusiness();
                out = response.getWriter();
                try {
                    ContratoModificado contMod = fillObject(request);
                    msg = contModB.saveAndValidateDates(jndiName, contMod, usuario);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    msg = e.getMessage();
                    e.printStackTrace();
                } finally {
                    try {
                        jsonObj.put("MENSAJE", "" + msg);
                    } catch (JSONException e) {
                        msg = e.getMessage();
                        e.printStackTrace();
                    }
                    String destino = new String(arrayObj.put(jsonObj).toString().getBytes("UTF-8"), "ISO-8859-1");
                    out.println(destino);
                    out.flush();
                    out.close();
                    out = null;
                }
                break;
        }
        java.util.Date df = new java.util.Date();
        System.out.println("Finaliza time: " + df.toString());
        System.out.println("Segundos diferencia:  " + (df.getTime() - di.getTime()) / 1000);
    }

    private ContratoModificado fillObject(HttpServletRequest request) {
        ContratoModificado contMod = new ContratoModificado();
        contMod.setcIdContratoDefinitivo(request.getParameter("cIdContratoDefinitivo"));
        contMod.setnConsecutivoModificacion(Integer.parseInt(request.getParameter("cConsecutivoMod")));
        contMod.setcObjetoConv(request.getParameter("objConv"));
        contMod.setcNoConvenio(request.getParameter("cNoConvenio"));
        contMod.setfFechaInicio(request.getParameter("fechaInicio"));
        contMod.setfFechaFin(request.getParameter("fechaFin"));
        contMod.setfFechaFormalizacion(request.getParameter("fechaFormalizacion"));
        return contMod;
    }

    private Caso getCaso(HttpSession session) {
        //Debido a que el Modulo de adquisiciones y servicios no se encuentra en un flujo pero se necesita crear un caso para el precompromiso
        // se creo este método que obtiene el ID de caso desde base de datos y no de sesión
        String sql;
        Integer consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_ContratoModificatorioConsecutivo));
        String definitivo = (String) session.getAttribute(GestionInterface.ATT_ContratoModificatorioDefinitivo);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        //Apartir de los atributos guardados en sesión se hace un QRY para obtener el ID_CASO
        sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mContratoModificado p, CG_CASO c, CG_CASO_OPERACION o " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cIdContratoDefinitivo=?" + " and p.nConsecutivoModificacion=?";
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
        String sql;
        Integer consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_ContratoModificatorioConsecutivo));
        String definitivo = (String) session.getAttribute(GestionInterface.ATT_ContratoModificatorioDefinitivo);
        String docDefinitivo = definitivo + "#M" + consecutivo;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        //Apartir de los atributos guardados en sesión se hace un QRY para obtener el ID_CASO
        sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mDocumentoFolio p, CG_CASO c, CG_CASO_OPERACION o " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cIdDocumentoDefinitivo=?" + " and p.cCentroContable=?" + " and p.cIdUnidadResponsable=?";
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
            sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mContratoModificado p, CG_CASO c, CG_CASO_OPERACION o " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cContratoDefinitivo=?";
        } else {
            sql = "SELECT TOP 1 MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mReduccionContratoModificatorio p, CG_CASO c, CG_CASO_OPERACION o " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cIdContratoDefinitivo+'#R'+convert(varchar,nConsecutivoModificacion)=?";
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
                cmst.setString(3, "CONTRATO");
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
                if (cmst != null)
                    cmst.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
            pstmt = null;
            rs = null;
            cmst = null;
        }
        return c;
    }

    private Caso getCasoVentanilla(String docDefinitivo, String centroContable, String ur) {
        //Debido a que el Modulo de adquisiciones y servicios no se encuentra en un flujo pero se necesita crear un caso para el precompromiso
        // se creo este método que obtiene el ID de caso desde base de datos y no de sesión
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int outputValue = 0;
        String sql = "";
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
                sql = "SELECT TOP 1 MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mReduccionContratoModificatorio p, CG_CASO c, CG_CASO_OPERACION o " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cIdContratoDefinitivo+'#R'+convert(varchar,nConsecutivoModificacion)=?";
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
            if (docDefinitivo.indexOf("#R") > 0) {
                cmst = conn.prepareCall("{?= call sp_mDevuelveReduccion (?,?)}");
                cmst.registerOutParameter(1, Types.INTEGER);
                //@cIdDocumentoDefinitivo
                cmst.setString(2, docDefinitivo);
                //@tipo
                cmst.setString(3, "CONTRATO");
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
                if (cmst != null)
                    cmst.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
            pstmt = null;
            rs = null;
            cmst = null;
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
        PreparedStatement pstm2 = null;
        try {
            conn = cbl.getConnection();
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
                    //Se guarda la relación de precompromisos con el contrato
                    pstm2 = conn.prepareStatement("insert into mRelPedContPrecomComp values(?,?,?,null,null)");
                    pstm2.setString(1, request.getParameter("cIdContrato"));
                    pstm2.setString(2, request.getParameter("folioCasoPreCompromiso"));
                    pstm2.setInt(3, Integer.parseInt(request.getParameter("nFolioPrecompromiso")));
                    pstm2.executeUpdate();
                    //Recargando el caso
                    Caso sc = new Caso();
                    sc.setIdCaso(c.getIdCaso());
                    c = CasoManager.select(conn, sc);
                    // Una vez que ha hecho la aplicación contable avanza el caso
                    avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                    log.debug("Object: {}", c.getCasoDato("APLICADO_CONT").getValor());
                    log.debug("Object: {}", "Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
                    mensaje = "DOCUMENTO DE PRECOMPROMISO APLICADO CONTABLEMENTE";
                    conn.commit();
                } else {
                    //cmst = conn.prepareCall("{?= call pa_cancelaAplicacionContableApartado (?,?)}");
                    //cmst.setInt(2, Integer.parseInt(request.getParameter("nFolioPrecompromiso").toString()));
                    //cmst.setString(3, request.getParameter("cEjercicio").toString());
                    //cmst.execute();
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
                mensaje = !"".equals(mensaje) ? mensaje : arrLResult.get(0);
            }
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            log.error("Error occurred", "Error en Aplicacion contable:" + e.getMessage());
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
                //Regresa el mensaje de la aplicación contable para que sea mostrado en el JSP
                //mensaje=!"".equals(mensaje)?mensaje:arrLResult.get(0);
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

    @SuppressWarnings("unchecked")
    private synchronized void devuelveContablemente(String strParam, HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre) throws ServletException, IOException {
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
                    //Recargando el caso
                    Caso sc = new Caso();
                    sc.setIdCaso(c.getIdCaso());
                    c = CasoManager.select(conn, sc);
                    // Una vez que ha hecho la aplicación contable avanza el caso
                    avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                    log.debug("Object: {}", c.getCasoDato("APLICADO_CONT").getValor());
                    log.debug("Object: {}", "Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
                    mensaje = "DOCUMENTO DE PRECOMPROMISO CANCELADO CONTABLEMENTE";
                    conn.commit();
                    log.debug("Object: {}", "Termina Aplicacion contable" + new Timestamp(System.currentTimeMillis()));
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
        } catch (Exception e) {
            log.error("Error occurred", "Error en Aplicacion contable:" + e.getMessage());
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
                mensaje = !"".equals(mensaje) ? mensaje : arrLResult.get(0);
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
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        Connection conncbl = null;
        AplicarContableReturn acr = null;
        ContableInterface conInt = new AplicacionContable();
        log.debug("Object: {}", "Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()));
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            conncbl = cbl.getConnection();
            stmt = conn.createStatement();
            Integer consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_ContratoModificatorioConsecutivo));
            String definitivo = (String) session.getAttribute(GestionInterface.ATT_ContratoModificatorioDefinitivo);
            String isConvEjercicioAnt = (String) session.getAttribute(GestionInterface.ATT_ContratoIsModificatorioEjercicioAnt);
            String queryCons = "select distinct centroContable, left(claveInterna,3) as cIdunidadEjecutora  from fn_mApartadoContratoModificado('" + definitivo + "', " + consecutivo + ")";
            if ("1".equals(isConvEjercicioAnt)) {
                queryCons = "select distinct centroContable, left(claveInterna,3) as cIdunidadEjecutora  from fn_mApartadoContratoEjercAntModificado('" + definitivo + "', " + consecutivo + ")";
            }
            rs = stmt.executeQuery(queryCons);
            while (rs.next()) {
                String centroContable = rs.getString(1);
                String ur = rs.getString(2);
                Caso c = getCaso(session, centroContable, ur);
                if (c != null) {
                    int folioPrecompromiso = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
                    Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                    acr = conInt.cancelarAppContableNueva(conncbl, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), "");
                    arrLResult = (ArrayList) acr.getMessageList();
                    if (acr.isSuccess()) {
                        cmst = conncbl.prepareCall("{?= call pa_validaAplicacionContable (?,?,?,?,?)}");
                        cmst.registerOutParameter(1, Types.INTEGER);
                        cmst.setInt(2, folioPrecompromiso);
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
                            cmst = conncbl.prepareCall("{call pa_actualizapContratoDiversoConvenioCancelado (?)}");
                            cmst.setInt(1, folioPrecompromiso);
                            cmst.execute();
                            mensaje = "DOCUMENTO DE PRECOMPROMISO CANCELADO CONTABLEMENTE";
                            conncbl.commit();
                            log.debug("Object: {}", "Termina Aplicacion contable" + new Timestamp(System.currentTimeMillis()));
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
                        conncbl.rollback();
                        mensaje = !"".equals(mensaje) ? mensaje : arrLResult.get(0);
                    }
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
                if (conn != null)
                    conn.close();
                if (conncbl != null)
                    conncbl.close();
                if (stmt != null)
                    stmt.close();
                if (rs != null)
                    rs.close();
                if (cmst != null)
                    cmst.close();
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
            rs = null;
            stmt = null;
            cmst = null;
            conncbl = null;
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
            //conn = DataSourceManager.getConnection(jndiName);
            conncbl = cbl.getConnection();
            String tipoPago = request.getParameter("tipoPago");
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
                /////
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
        String[] param = strParam.split(",");
        Statement stmt = null;
        ResultSet rs = null;
        int outputValue = -1;
        Caso c = null;
        try {
            out = response.getWriter();
            int folio = -1;
            String folioCaso = null;
            conn = DataSourceManager.getConnection(jndiName);
            stmt = conn.createStatement();
            String queryCons = "SELECT C_FOLIO, ConsecutivoCDIV FROM mContratoModificado WITH (NOLOCK) where cIdContratoDefinitivo = '" + param[1] + "' AND nConsecutivoModificacion = " + param[2];
            rs = stmt.executeQuery(queryCons);
            if (rs.next()) {
                folioCaso = rs.getString(1);
                folio = rs.getInt(2);
            }
            String pa = "pa_apruebaContratoModificado";
            if (param[1].indexOf("PLU") == 0 && "0".equals(param[5]) && "1".equals(param[6])) {
                pa = "pa_apruebaContratoPluModificado";
            }
            if ("1".equals(param[5])) {
                pa = "pa_apruebaContratoEjercAntModificado";
                c = Util.generaGuardaCaso(usuario.getU_UR(), (GestionInterface.IDTC_CONTRATO_DIVERSO + ""), "Convenio Ejercicio anterior", usuario, jndiName, param[0]);
                folioCaso = c.getFolio();
                int indice = folioCaso.lastIndexOf('-') + 1;
                folio = Integer.parseInt(folioCaso.substring(indice));
                //se avanza caso para que no se vea la operacion  en el inbox
                String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
                avanzaCaso(request, c, usuario, prefixPath, new String[] { "CONSULTA_CONTRATODIVERSO" }, new String[] { "consulta_contrato" });
            }
            cmst = conn.prepareCall("{? = call " + pa + " (?,?,?,?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, param[0]);
            cmst.setString(3, param[1]);
            cmst.setInt(4, Integer.parseInt(param[2]));
            cmst.setString(5, param[3]);
            cmst.setString(6, folio + "");
            cmst.setString(7, folioCaso);
            cmst.execute();
            outputValue = cmst.getInt(1);
            if (outputValue == 0)
                conn.commit();
            else
                conn.rollback();
        } catch (SQLException e1) {
            outputValue = -1;
            e1.printStackTrace();
        } catch (IOException e1) {
            outputValue = -1;
            e1.printStackTrace();
        } catch (GestionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                jsonObj.put("Col1", "" + outputValue);
                String destino = arrayObj.put(jsonObj).toString();
                out.println(destino);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
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

    private synchronized void devuelveContratoDiverso(String strParam, HttpServletResponse response) {
        String[] param = strParam.split(",");
        int outputValue = -1;
        try {
            out = response.getWriter();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            //Elimina los registros de contrato diverso de las tablas pContratoDiverso y tContratoEP
            conn = DataSourceManager.getConnection(jndiName);
            cmst = conn.prepareCall("{? = call pa_devuelveContratoModificado (?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, param[0]);
            cmst.setString(3, param[1]);
            cmst.setInt(4, Integer.parseInt(param[2]));
            cmst.execute();
            outputValue = cmst.getInt(1);
            if (outputValue == 0)
                conn.commit();
            else
                conn.rollback();
        } catch (SQLException e1) {
            outputValue = -1;
            e1.printStackTrace();
        } finally {
            try {
                jsonObj.put("Col1", "" + outputValue);
                String destino = arrayObj.put(jsonObj).toString();
                out.println(destino);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
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

    private void checaFechasModificatorio(HttpServletRequest request, HttpServletResponse response) {
        String fAdjudicacion = request.getParameter("fechaFormalizacion");
        String fInicio = request.getParameter("fechaInicio");
        String fFin = request.getParameter("fechaFin");
        String cIdContratoDefinitivo = request.getParameter("cIdContratoDefinitivo");
        int outputValue = -1;
        try {
            out = response.getWriter();
            conn = DataSourceManager.getConnection(jndiName);
            cmst = conn.prepareCall("{?= call pa_revisaFechasModificatorio (?,?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, fAdjudicacion);
            cmst.setString(3, fInicio);
            cmst.setString(4, fFin);
            cmst.setString(5, cIdContratoDefinitivo);
            cmst.execute();
            outputValue = cmst.getInt(1);
            if (outputValue == 0)
                conn.commit();
            else
                conn.rollback();
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
}
