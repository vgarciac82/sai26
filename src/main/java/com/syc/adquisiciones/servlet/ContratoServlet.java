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
import com.jenkov.prizetags.tree.itf.ITree;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "ContratoServlet", urlPatterns = { "/servlet/ContratoServlet" })
public class ContratoServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 1L;

    private static String jndiName = null;

    private static Logger log = Logger.getLogger(ContratoServlet.class);

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
                generaGuardaCaso(request, response, session, (GestionInterface.IDTC_PRECOMPROMISO + ""), "Aplicación de PreCompromiso");
                break;
            case 3:
                aplicaContablemente(strParam, request, response, session, new String[] { "VENTANILLA_PRECOMPROMISO" }, new String[] { "autoriza_precomp" });
                //aplicaContablemente(strParam,request, response,session,new String[] { "CONSULTA_PRECOMPROMISO" }, new String[] { "consulta_precomp" });
                break;
            case 4:
                tipoPago = request.getParameter("tipoPago");
                if ("1".equalsIgnoreCase(tipoPago)) {
                    log.debug("descentralizado");
                    //devuelveContablementeDes(strParam,request, response,session,new String[] { "CAPTURA_PRECOMPROMISO" }, new String[] { "captura_precomp" });
                    devuelveContablementeDes(strParam, request, response, session, new String[] { "CONSULTA_PRECOMPROMISO" }, new String[] { "consulta_precomp" });
                } else {
                    log.debug("centralizado");
                    //devuelveContablemente(strParam,request, response,session,new String[] { "CAPTURA_PRECOMPROMISO" }, new String[] { "captura_precomp" });
                    devuelveContablemente(strParam, request, response, session, new String[] { "CONSULTA_PRECOMPROMISO" }, new String[] { "consulta_precomp" });
                }
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
                    log.error("Error en Aplicacion contable:" + e.getMessage());
                }
                break;
            case 9:
                //Devuelve contablemente desde ventanilla
                devuelveContablementeVentanilla(request, response, session, new String[] { "CONSULTA_PRECOMPROMISO" }, new String[] { "consulta_precomp" });
                break;
            case 10:
                copiaContrato(session, response, request);
                break;
            case 11:
                liberaSaldo(request, usuario);
                break;
        }
        java.util.Date df = new java.util.Date();
        System.out.println("Finaliza time: " + df.toString());
        System.out.println("Segundos diferencia:  " + (df.getTime() - di.getTime()) / 1000);
    }

    private Caso getCaso(HttpSession session) {
        String ejercicio = (String) session.getAttribute(GestionInterface.ATT_PedidoEjercicio);
        String sql, tipo, ue;
        int consecutivo;
        ejercicio = (String) session.getAttribute(GestionInterface.ATT_ContratoEjercicio);
        tipo = (String) session.getAttribute(GestionInterface.ATT_ContratoTipoContrato);
        ue = (String) session.getAttribute(GestionInterface.ATT_ContratoUnidadEjec);
        consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_ContratoConsecutivo));
        sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mContrato p (NOLOCK), CG_CASO c (NOLOCK), CG_CASO_OPERACION o (NOLOCK)" + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cEjercicio=?" + " and p.cIdTipoContrato=?" + " and p.cIdUnidadEjecutora=?" + " and p.nIdConsecutivo=?";
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
        String tipo = (String) session.getAttribute(GestionInterface.ATT_ContratoTipoContrato);
        String ue = (String) session.getAttribute(GestionInterface.ATT_ContratoUnidadEjec);
        Integer consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_ContratoConsecutivo));
        String docDefinitivo = tipo + "-" + ue + "-" + consecutivo;
        //Apartir de los atributos guardados en sesión se hace un QRY para obtener el ID_CASO
        sql = "select MAX(c.ID_CASO) ID_CASO, MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " from mContrato con " + " left join mDocumentoFolio df on con.cIdContratoDefinitivo = df.cIdDocumentoDefinitivo " + " left join cg_caso c on df.C_FOLIO_PRE = c.C_FOLIO " + " left join CG_CASO_OPERACION o on c.ID_CASO = o.id_caso " + " where con.cIdContrato = '" + docDefinitivo + "' and df.cCentroContable = '" + centroContable + "' and df.cIdUnidadResponsable = '" + ur + "'";
        log.debug(sql);
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
        String sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mContrato p, CG_CASO c, CG_CASO_OPERACION o " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cIdContratoDefinitivo=?";
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
        // se creo este método que obtiene el ID de caso desde base de datos y no de sesión
        PreparedStatement pstmt = null;
        ResultSet rs = null;
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

    /*
	private synchronized void validaPrecompromiso ( String strParam, HttpServletResponse response) throws ServletException {

		String param[] = strParam.split(",");
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
				cmst = null;
				conn = null;
		}
		
	}
	*/
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

    private synchronized void aplicaContablemente(String strParam, HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre) throws ServletException, IOException {
        session = request.getSession(false);
        out = response.getWriter();
        String validaSaldo = "";
        ArrayList<String> arrLResult = new ArrayList<String>();
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        PreparedStatement pstm = null, pstm2 = null;
        CallableStatement cmst = null, cmst1 = null;
        Connection conn = null, conn1 = null;
        Statement stm = null;
        ResultSet rs = null;
        String tipo = (String) session.getAttribute(GestionInterface.ATT_ContratoTipoContrato);
        String ue = (String) session.getAttribute(GestionInterface.ATT_ContratoUnidadEjec);
        int consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_ContratoConsecutivo));
        AccountingEngine accEng = new AccountingEngine();
        accEng.setValidaInsuficienciaDeSaldo(Boolean.TRUE);
        String cCentroContable = "";
        String mensaje = "";
        String cContratoDefinitivo = request.getParameter("cIdContratoDefinitivo");
        if (c == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        //Valida Centro de Costos
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
            stm = conn.createStatement();
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
                    mensaje = "DOCUMENTO DE PRECOMPROMISO APLICADO CONTABLEMENTE.";
                    jsonObj.put("Aplica", "1");
                    log.info("folioCasoPreCompromiso : " + request.getParameter("folioCasoPreCompromiso"));
                    pstm = conn.prepareStatement("UPDATE mContrato SET nIdEstado = 3 , ConsecutivoPRECOMP = ?, C_FOLIO_PRE = ? " + " WHERE cIdTipoContrato = ? " + " and cIdUnidadEjecutora = ?" + " and nIdConsecutivo = ?" + " and cEjercicio=?");
                    pstm.setString(1, request.getParameter("nFolioPrecompromiso"));
                    pstm.setString(2, request.getParameter("folioCasoPreCompromiso"));
                    pstm.setString(3, tipo);
                    pstm.setString(4, ue);
                    pstm.setInt(5, consecutivo);
                    pstm.setString(6, request.getParameter("cEjercicio"));
                    pstm.executeUpdate();
                    //Se guarda la relación de precompromisos con el contrato
                    pstm2 = conn.prepareStatement("insert into mRelPedContPrecomComp values(?,?,?,null,null)");
                    pstm2.setString(1, cContratoDefinitivo);
                    pstm2.setString(2, request.getParameter("folioCasoPreCompromiso"));
                    pstm2.setInt(3, Integer.parseInt(request.getParameter("nFolioPrecompromiso")));
                    pstm2.executeUpdate();
                    //Recargando el caso
                    Caso sc = new Caso();
                    sc.setIdCaso(c.getIdCaso());
                    c = CasoManager.select(conn, sc);
                    // Una vez que ha hecho la aplicación contable avanza el caso
                    avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                    log.debug(c.getCasoDato("APLICADO_CONT").getValor());
                    log.debug("Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
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
                    cmst1.setString(6, "CONTRATO");
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
                cmst1.setString(6, "CONTRATO");
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
                if (pstm != null)
                    pstm.close();
                if (pstm2 != null)
                    pstm2.close();
                if (cmst1 != null)
                    cmst1.close();
                if (cmst != null)
                    cmst.close();
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

    private synchronized void devuelveContablemente(String strParam, HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre) throws ServletException, IOException {
        session = request.getSession(false);
        out = response.getWriter();
        String validaSaldo = "";
        PreparedStatement pstm = null, pstm1 = null;
        CallableStatement cmst = null;
        ArrayList<String> arrLResult = new ArrayList<String>();
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        String tipo, ue;
        int consecutivo;
        tipo = (String) session.getAttribute(GestionInterface.ATT_ContratoTipoContrato);
        ue = (String) session.getAttribute(GestionInterface.ATT_ContratoUnidadEjec);
        consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_ContratoConsecutivo));
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
        Connection conn = null, conn1 = null;
        AplicarContableReturn acr = null;
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
                    //Recargando el caso
                    mensaje = "DOCUMENTO DE PRECOMPROMISO CANCELADO CONTABLEMENTE";
                    jsonObj.put("Devuelve", "1");
                    pstm = conn.prepareStatement("UPDATE mContrato SET nIdEstado = 2 , ConsecutivoPRECOMP = null, C_FOLIO_PRE=null " + " WHERE cIdTipoContrato = ? " + " and cIdUnidadEjecutora = ?" + " and nIdConsecutivo = ?" + " and cEjercicio=?");
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
                    conn.commit();
                    log.debug("Termina Aplicacion contable" + new Timestamp(System.currentTimeMillis()));
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
                    pstm1 = conn1.prepareStatement("UPDATE mContrato SET nIdEstado = 3 " + " WHERE cIdTipoContrato = ? " + " and cIdUnidadEjecutora = ?" + " and nIdConsecutivo = ?" + " and cEjercicio=?");
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
                //hubo un error al aplicar contablemente ,actualiza status de la tabla de mpedido
                pstm1 = conn1.prepareStatement("UPDATE mContrato SET nIdEstado = 3 " + " WHERE cIdTipoContrato = ? " + " and cIdUnidadEjecutora = ?" + " and nIdConsecutivo = ?" + " and cEjercicio=?");
                pstm1.setString(1, tipo);
                pstm1.setString(2, ue);
                pstm1.setInt(3, consecutivo);
                pstm1.setString(4, request.getParameter("cEjercicio"));
                pstm1.executeUpdate();
                conn1.commit();
                mensaje = !"".equals(mensaje) ? mensaje : arrLResult.get(0);
            }
        } catch (Exception e) {
            log.error("Error en Aplicacion contable:" + e.getMessage());
            mensaje = "ERROR INESPERADO DE LA CANCELACION CONTABLE(MOTOR CONTABLE O NO AVANZO EL CASO).FAVOR DE INTENTAR NUEVAMENTE";
            try {
                conn.rollback();
                conn1.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            try {
                jsonObj.put("Devuelve", "0");
            } catch (JSONException e2) {
                e2.printStackTrace();
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
        log.debug("Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()));
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        AplicarContableReturn acr = null;
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        try {
            String tipo = (String) session.getAttribute(GestionInterface.ATT_ContratoTipoContrato);
            String ue = (String) session.getAttribute(GestionInterface.ATT_ContratoUnidadEjec);
            Integer consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_ContratoConsecutivo));
            String ejercicio = (String) session.getAttribute(GestionInterface.ATT_ContratoEjercicio);
            String sql = "select df.cCentroContable as cIdEntidadContable, df.cIdUnidadResponsable as ClaveInterna ,df.ConsecutivoPRECOMP" + " from mDocumentoFolio df with(nolock) ,mContrato p with(nolock)" + " where cIdDocumentoDefinitivo= p.cIdContratoDefinitivo" + " and p.cIdTipoContrato=?" + " and p.cIdUnidadEjecutora=?" + " and p.nIdConsecutivo=?";
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

    private void copiaContrato(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        int outputValue = -1;
        Connection conn = null;
        CallableStatement cmst = null;
        String link = "-1";
        try {
            out = response.getWriter();
            conn = DataSourceManager.getConnection(jndiName);
            cmst = conn.prepareCall("{?=call pa_CopiaPedidoContrato (?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, request.getParameter("cIdContratoDefinitivo"));
            cmst.setString(3, "CONTRATO");
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
            cmst = conn.prepareCall("{?= call pa_validaContratoFinanciero (?,?)}");
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
                cmst = conn.prepareCall("{?= call pa_apruebaContrato(?,?,?,?,?)}");
                cmst.registerOutParameter(1, Types.INTEGER);
                cmst.setString(2, param[0]);
                cmst.setString(3, param[1]);
                cmst.setString(4, param[2]);
                cmst.setString(5, folio + "");
                cmst.setString(6, folioCaso);
                cmst.execute();
                outputValue = cmst.getInt(1);
                log.info("exec pa_apruebaContrato('" + param[0] + "','" + param[1] + "','" + param[2] + "','" + folio + "','" + folioCaso + "')");
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
            cmst = conn.prepareCall("{?= call pa_devuelveContrato (?,?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, param[0]);
            cmst.setString(3, param[1]);
            cmst.setString(4, param[2]);
            cmst.setString(5, param[3]);
            cmst.execute();
            log.debug("pa_devuelveContrato " + param[0] + "," + param[1] + "," + param[2] + "," + param[3]);
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
			cmst = conn.prepareCall("{?= call pa_validaContratoFinanciero (?,?)}");
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
					cmst = conn.prepareCall("{?= call pa_apruebaContrato(?,?,?,?,?)}");
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
			cmst = conn.prepareCall("{?= call pa_devuelveContrato (?,?,?,?)}");
			cmst.registerOutParameter(1, Types.INTEGER);
			cmst.setString(2, param[0]);
			cmst.setString(3, param[1]);
			cmst.setString(4, param[2]);
			cmst.setString(5, param[3]);
			cmst.execute();
			log.debug("pa_devuelveContrato " + param[0] + "," + param[1] + "," + param[2]+ "," + param[3]);
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
            conn1 = null;
        }
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
                        conn.commit();
                    }
                }
                if (rs2 != null) {
                    rs2.close();
                    rs2 = null;
                }
            }
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (stm != null)
                    stm.close();
                if (stm2 != null)
                    stm2.close();
                if (rs != null)
                    rs.close();
                if (rs2 != null)
                    rs2.close();
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
