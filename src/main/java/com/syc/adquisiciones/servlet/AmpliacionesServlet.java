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
import java.io.UnsupportedEncodingException;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.adquisiciones.OperacionesAmpliaciones;
import com.syc.adquisiciones.businessLogic.ContratoAmpImpl;
import com.syc.adquisiciones.core.DatosContrato;
import com.syc.adquisiciones.core.DatosContratoAmp;
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
import java.util.Map;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "AmpliacionesServlet", urlPatterns = { "/servlet/AmpliacionesServlet" })
public class AmpliacionesServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 1L;

    private static String jndiName = null;

    private static Logger log = LoggerFactory.getLogger(AmpliacionesServlet.class);

    private Connection conn = null;

    private CallableStatement cmst = null;

    private JSONArray arrayObj;

    private JSONObject jsonObj;

    private PrintWriter out = null;

    private String folioGenerator = null;

    private Usuario usuario;

    private String cEjercicio, today;

    private PreparedStatement pstmt = null;

    private ResultSet rs = null;

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
        arrayObj = new JSONArray();
        jsonObj = new JSONObject();
        HttpSession session = request.getSession(false);
        //Valida que hata una sesión activa
        if (session == null) {
            log.warn("No hay sesion");
            response.sendRedirect("../index.jsp");
            return;
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
                        //devuelveContablementeDes(request, response,session,new String[] { "CAPTURA_PRECOMPROMISO" }, new String[] { "captura_precomp" });
                        devuelveContablementeDes(request, response, session, new String[] { "CONSULTA_DEVOLUCION" }, new String[] { "consulta_devolucion" });
                    else
                        //devuelveContablemente(request, response,session,new String[] { "CAPTURA_PRECOMPROMISO" }, new String[] { "captura_precomp" });
                        devuelveContablemente(request, response, session, new String[] { "CONSULTA_DEVOLUCION" }, new String[] { "consulta_devolucion" });
                    break;
                }
            case 5:
                {
                    apruebaAmplContPlu(strParam, request, response);
                    break;
                }
            case 6:
                {
                    devuelveContratoPlu(strParam, response);
                    break;
                }
            case 9:
                {
                    //Devuelve contablemente desde ventanilla
                    //devuelveContablementeVentanilla(request, response,session,new String[] { "CAPTURA_PRECOMPROMISO" }, new String[] { "captura_precomp" });
                    devuelveContablementeVentanilla(request, response, session, new String[] { "CONSULTA_DEVOLUCION" }, new String[] { "consulta_devolucion" });
                    break;
                }
            case //Ajusta centavos ampliaciones
            10:
                {
                    try {
                        ajustaCentavosAmpliaciones(request, response);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        log.error(e.getMessage(), e);
                        e.printStackTrace();
                    }
                    break;
                }
            default:
                log.warn("Operación desconocida");
                break;
        }
    }

    private void ajustaCentavosAmpliaciones(HttpServletRequest request, HttpServletResponse response) throws ServletException, JSONException, UnsupportedEncodingException {
        OperacionesAmpliaciones operaciones = null;
        DatosContratoAmp contAmpliacion = null;
        DatosContrato contrato = null;
        ArrayList<DatosContratoAmp> listAmpliaciones = null;
        String msg = null;
        String destino = null;
        try {
            operaciones = new ContratoAmpImpl();
            contAmpliacion = new DatosContratoAmp();
            contrato = new DatosContrato();
            listAmpliaciones = new ArrayList<DatosContratoAmp>();
            out = response.getWriter();
            contrato.setcIdContratoDef(request.getParameter("cIdContratoDef"));
            contrato.setcTipoContrato(request.getParameter("cTipoContrato"));
            contrato.setcUE(request.getParameter("cUE"));
            contrato.setcEjercicio(request.getParameter("cEjercicio"));
            contrato.setnIdConsecutivo((request.getParameter("nIdConsecutivo") == null || "".equals(request.getParameter("nIdConsecutivo"))) ? 0 : Integer.parseInt(request.getParameter("nIdConsecutivo")));
            contAmpliacion.setcIdUERequi(request.getParameter("cIdUERequi"));
            contAmpliacion.setmMontoConIVA((request.getParameter("mMontoConIVA") == null || "".equals(request.getParameter("mMontoConIVA"))) ? 0.00 : Double.parseDouble(request.getParameter("mMontoConIVA")));
            contAmpliacion.setnConsecutivoAmpliacion((request.getParameter("nConsecutivoAmpliacion") == null || "".equals(request.getParameter("nConsecutivoAmpliacion"))) ? 0 : Integer.parseInt(request.getParameter("nConsecutivoAmpliacion")));
            listAmpliaciones.add(contAmpliacion);
            contrato.setAmpliacion(listAmpliaciones);
            msg = operaciones.AjustaCentavos(contrato);
        } catch (Exception e) {
            msg = e.getMessage();
            log.error(e.getMessage(), e);
            e.printStackTrace();
        } finally {
            jsonObj.put("msg", msg);
            destino = arrayObj.put(jsonObj).toString();
            out.println(new String(destino.getBytes("UTF-8"), "ISO-8859-1"));
            jsonObj = null;
            arrayObj = null;
            operaciones = null;
            contAmpliacion = null;
            listAmpliaciones = null;
            contrato = null;
            msg = null;
            destino = null;
        }
    }

    private synchronized void apruebaContratoDiverso(String strParam, HttpServletRequest request, HttpServletResponse response) throws ServletException {
        // se utliza para generar contratos diversos
        String[] param = strParam.split(",");
        int outputValue = -1;
        String query = "";
        try {
            out = response.getWriter();
            conn = DataSourceManager.getConnection(jndiName);
            if (log.isDebugEnabled())
                log.debug("Inicia contrato diverso convenio");
            cmst = conn.prepareCall("{?= call pa_contratoDiversoExtra (?,?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, param[0]);
            cmst.setString(3, param[1]);
            cmst.setString(4, param[2]);
            cmst.setInt(5, Integer.parseInt(param[3]));
            //cmst.setInt(5, 1);
            cmst.execute();
            outputValue = cmst.getInt(1);
            log.info("Object: {}", "outputValue:" + outputValue);
            if (outputValue == 0) {
                query = "UPDATE mContratoAmpliacion SET nIdEstado=2 WHERE (cIdTipoContrato+'-'+cIdUnidadEjecutora+'-'+CONVERT(VARCHAR,nIdConsecutivo))='" + param[1] + "' AND nIdConsecutivoAmpliacion=" + param[3];
                Util.updateQuery(query, conn);
                Util.bitacoraMovimientos(param[1] + "/" + param[0] + "-AMP-" + param[3], "Ampliación aprobada", usuario.getLogin(), conn);
                conn.commit();
            } else {
                conn.rollback();
                if (outputValue == 1) {
                    log.info("Tipo documento Pedido. Error al insertar EP en la tabla tPedidoEP_TMP.");
                }
                if (outputValue == 2) {
                    log.warn("Tipo documento Pedido. Error al insertar en la tabla pContratoDiversoConvenio.");
                }
                if (outputValue == 3) {
                    log.warn("Tipo documento Pedido. Error al insertar en la tabla tContratoEP.");
                }
                if (outputValue == 4) {
                    log.info("Tipo documento Contrato. Error al insertar EP en la tabla tContratoEP_TMP.");
                }
                if (outputValue == 5) {
                    log.warn("Tipo documento Contrato. Error al insertar en la tabla pContratoDiversoConvenio.");
                }
                if (outputValue == 6) {
                    log.warn("Tipo documento Contrato. Error al insertar en la tabla tContratoEP.");
                }
            }
        } catch (Exception exc) {
            outputValue = -1;
            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            exc.printStackTrace();
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

    private synchronized void apruebaAmplContPlu(String strParam, HttpServletRequest request, HttpServletResponse response) throws ServletException {
        // se utliza para generar contratos diversos
        String[] param = strParam.split(",");
        int outputValue = -1;
        try {
            out = response.getWriter();
            conn = DataSourceManager.getConnection(jndiName);
            if (log.isDebugEnabled())
                log.debug("Inicia contrato diverso convenio");
            cmst = conn.prepareCall("{?= call pa_contratoPluAmpl (?,?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, param[0]);
            cmst.setString(3, param[1]);
            cmst.setString(4, param[2]);
            cmst.setInt(5, Integer.parseInt(param[3]));
            //cmst.setInt(5, 1);
            cmst.execute();
            outputValue = cmst.getInt(1);
            log.info("Object: {}", "outputValue:" + outputValue);
            if (outputValue == 0)
                conn.commit();
            else {
                conn.rollback();
                if (outputValue == 1) {
                    log.info("Tipo documento Pedido. Error al insertar EP en la tabla tPedidoEP_TMP.");
                }
                if (outputValue == 2) {
                    log.warn("Tipo documento Pedido. Error al insertar en la tabla pContratoDiversoConvenio.");
                }
                if (outputValue == 3) {
                    log.warn("Tipo documento Pedido. Error al insertar en la tabla tContratoEP.");
                }
                if (outputValue == 4) {
                    log.info("Tipo documento Contrato Plurianual. Error al insertar EP en la tabla tContratoEP_TMP.");
                }
                if (outputValue == 5) {
                    log.warn("Tipo documento Contrato Plurianual. Error al insertar en la tabla pContratoDiversoConvenio.");
                }
                if (outputValue == 6) {
                    log.warn("Tipo documento Contrato Plurianual. Error al insertar en la tabla tContratoEP.");
                }
            }
        } catch (Exception exc) {
            outputValue = -1;
            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            exc.printStackTrace();
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

    /*	
	
	private synchronized void apruebaContratoDiversoDes(String strParam, HttpServletRequest request,HttpServletResponse response) throws ServletException {
		// se utliza para generar contratos diversos
		String param[] = strParam.split(",");
		try {
			out = response.getWriter();
			int outputValue = 0;
			conn = DataSourceManager.getConnection(jndiName);
			Statement stmt = conn.createStatement();
			String queryCons = "select distinct paep.cCentroContable, cUnidadResponsable "
				   			   +"from (select ca.cIdContrato,LEFT(RIGHT(RTRIM(ca.cClaveEP), 7),3) as ue, ca.cClaveEP, ca.cCentroContable from m"+param[2]+"AmpliacionEP ca with(nolock) where cId"+param[2]+"='"+param[1]+"' and nIdConsecutivoAmpliacion="+param[4]+") paep inner join tCatalogoURCC urcc on paep.cCentroContable = urcc.cCentroContable " 
				   			   +"where cId"+param[2]+" = '"+param[1]+"' and paep.ue=urcc.cUnidadResponsable";
			
			rs = stmt.executeQuery(queryCons);
			while (rs.next()){
				try {
					String centroContable = rs.getString(1);
					String ur = rs.getString(2);
					Caso c = iniciaCasoDes(usuario, ur, "9");
					String folioCaso = c.getFolio();
					log.info("Folio caso: " + c.getFolio());
					int indice = folioCaso.lastIndexOf('-') + 1;
					int folio = Integer.parseInt(folioCaso.substring(indice));
					
					cmst = conn.prepareCall("{?= call pa_contratoDiversoExtraDes (?,?,?,?,?,?,?,?,?)}");
					cmst.registerOutParameter(1, Types.INTEGER);
					cmst.setString(2, param[0]);
					cmst.setString(3, param[1]);
					cmst.setString(4, param[3]);
					cmst.setString(5, folio + "");
					cmst.setString(6, folioCaso);
					cmst.setString(7, param[2]);
					cmst.setString(8, param[4]);
					cmst.setString(9, centroContable);
					cmst.setString(10, ur);
					log.info("antes de ejecurtar");
					cmst.execute();
					log.info("despues de ejecutar");
					outputValue = cmst.getInt(1);
					log.info("outputValue:"+outputValue);
					if (outputValue != 0)
						conn.rollback();
				} catch (GestionException exc) {
					log.error("Error iniciando Caso", exc);
					throw new ServletException(exc);
				}
			}
			
			conn.commit();
			
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
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}
			
			cmst = null;
			conn = null;
			rs=null;
		}
		
	}
	*/
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
                log.info("Object: {}", "folio: " + folio);
                log.info("Object: {}", "folioCaso: " + folioCaso);
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
                conn = DataSourceManager.getConnection(jndiName);
                CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), datos);
                session.setAttribute(GestionInterface.ATT_CASE, c);
                conn.commit();
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
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                exc.printStackTrace();
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
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

    @SuppressWarnings("unchecked")
    private synchronized void devuelveContablemente(HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre) throws ServletException, IOException {
        session = request.getSession(false);
        out = response.getWriter();
        String validaSaldo = "";
        ArrayList<String> arrLResult = new ArrayList<String>();
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (usuario == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        //Valida Centro de Costos
        String cCentroContable = "";
        String mensaje = "";
        PreparedStatement pstm2 = null;
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
        //int folioPrecompromiso=Integer.parseInt(request.getParameter("nFolioPrecompromiso"));
        try {
            conn = cbl.getConnection();
            Caso c;
            if ((c = (Caso) session.getAttribute(GestionInterface.ATT_CASE)) == null)
                c = getCaso(request);
            if (c == null) {
                log.error("Error en Aplicacion contable:");
                return;
            }
            //obtenemos el consecutivo del folio de precompromiso
            int folioPrecompromiso = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
            Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
            acr = conInt.cancelarAppContableNueva(conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), "");
            arrLResult = (ArrayList) acr.getMessageList();
            if (acr.isSuccess()) {
                cmst = conn.prepareCall("{?= call pa_validaAplicacionContable (?,?,?,?,?)}");
                cmst.registerOutParameter(1, Types.INTEGER);
                cmst.setInt(2, folioPrecompromiso);
                cmst.setString(3, request.getParameter("cEjercicio"));
                cmst.setString(4, "C");
                cmst.setString(5, "CO");
                cmst.setString(6, "PRECOMPROMISO");
                cmst.execute();
                int outputValue = cmst.getInt(1);
                if (outputValue == 0) {
                    //ACTUALIZA ESTATUS
                    String query = "UPDATE mContratoAmpliacion SET nIdEstado=2 where cIdContratoDefinitivo='" + request.getParameter("cIdDocumentoDefinitivo") + "' AND nIdConsecutivoAmpliacion=" + request.getParameter("consAmp");
                    if (request.getParameter("cIdDocumentoDefinitivo").indexOf("PE") != -1) {
                        query = "UPDATE mPedidoAmpliacion SET nIdEstado=2 where cIdPedidoDefinitivo='" + request.getParameter("cIdDocumentoDefinitivo") + "' AND nIdConsecutivoAmpliacion=" + request.getParameter("consAmp");
                    }
                    Util.updateQuery(query, conn);
                    //guarda bitacora
                    Util.bitacoraMovimientos(request.getParameter("cIdDocumentoDefinitivo") + "-AMP-" + request.getParameter("consAmp"), "Devuelve el precompromiso", usuario.getLogin(), conn);
                    //Se guarda la relación de precompromisos con el contrato
                    pstm2 = conn.prepareStatement("delete mRelPedContPrecomComp where cFolioPrecom=? and nConsecutivoPrecom=?");
                    pstm2.setString(1, c.getFolio());
                    pstm2.setInt(2, folioPrecompromiso);
                    pstm2.executeUpdate();
                    //Recargando el caso
                    Caso sc = new Caso();
                    sc.setIdCaso(c.getIdCaso());
                    c = CasoManager.select(conn, sc);
                    // Una vez que ha hecho la aplicación contable avanza el caso
                    avanzaCaso(request, c, usuario, prefixPath, responsable, nombre);
                    log.debug("Object: {}", c.getCasoDato("APLICADO_CONT").getValor());
                    log.debug("Object: {}", "Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
                    mensaje = "DOCUMENTO DE PRECOMPROMISO CANCELADO CONTABLEMENTE";
                    log.debug("Object: {}", "Termina Aplicacion contable" + new Timestamp(System.currentTimeMillis()));
                    //if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor())||mensaje.contains("APLICADO"))
                    cmst = conn.prepareCall("{call pa_actualizapContratoDiversoConvenioCancelado (?)}");
                    cmst.setInt(1, folioPrecompromiso);
                    cmst.execute();
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
                if (pstm2 != null)
                    pstm2.close();
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
            pstm2 = null;
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
        Connection conn = null;
        AplicarContableReturn acr = null;
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        ResultSet rs = null;
        Statement stmt = null;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            stmt = conn.createStatement();
            conncbl = cbl.getConnection();
            Integer consecutivo = Integer.parseInt(request.getParameter("consAmp"));
            String idDocumento = request.getParameter("cIdDocumento");
            String tipo = request.getParameter("tipo");
            String queryCons = "select distinct paep.cCentroContable, cUnidadResponsable " + "from (select ca.cId" + tipo + ",LEFT(RIGHT(RTRIM(ca.cClaveEP), 7),3) as ue, ca.cClaveEP, ca.cCentroContable from m" + tipo + "AmpliacionEP ca with(nolock) where cId" + tipo + "=SUBSTRING('" + idDocumento + "',1,LEN('" + idDocumento + "')-charindex('-', reverse('" + idDocumento + "'))-4) and nIdConsecutivoAmpliacion='" + consecutivo + "') paep inner join tCatalogoURCC urcc on paep.cCentroContable = urcc.cCentroContable " + "where cId" + tipo + " = SUBSTRING('" + idDocumento + "',1,LEN('" + idDocumento + "')-charindex('-', reverse('" + idDocumento + "'))-4) and paep.ue=urcc.cUnidadResponsable";
            //"select distinct cCentroContable from m" + tipo + "AmpliacionEP where cId" + tipo + " = '" + idDocumento + "' and nIdConsecutivoAmpliacion = " + consecutivo;
            rs = stmt.executeQuery(queryCons);
            while (rs.next()) {
                String centroContable = rs.getString(1);
                Caso c = getCaso(request, centroContable, rs.getString(2));
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
                            //ACTUALIZA ESTATUS
                            String query = "UPDATE mContratoAmpliacion SET nIdEstado=2 where (cIdContratoDefinitivo+'-AMP-'+convert(varchar,nIdConsecutivoAmpliacion))='" + idDocumento + "' AND nIdConsecutivoAmpliacion=" + consecutivo;
                            if (idDocumento.indexOf("PE") != -1) {
                                query = "UPDATE mPedidoAmpliacion SET nIdEstado=2 where (cIdPedidoDefinitivo+'-AMP-'+convert(varchar,nIdConsecutivoAmpliacion))='" + idDocumento + "' AND nIdConsecutivoAmpliacion=" + consecutivo;
                            }
                            Util.updateQuery(query, conn);
                            //guarda bitacora
                            Util.bitacoraMovimientos(idDocumento, "Devuelve el precompromiso", usuario.getLogin(), conn);
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
                            conncbl.rollback();
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
        Caso c = null;
        try {
            //conn = DataSourceManager.getConnection(jndiName);
            conncbl = cbl.getConnection();
            String tipoPago = request.getParameter("tipoPago");
            String centroContable = request.getParameter("cContableVentanilla");
            //String ur  = request.getParameter("ur");
            String docDefinitivo = request.getParameter("contratoDefinitivo");
            String origen = request.getParameter("origen");
            if (tipoPago != null && tipoPago.equals("1")) {
                c = getCasoVentanilla(docDefinitivo, centroContable);
            } else {
                if (origen.equals("PEDIDO AMPLIACION")) {
                    c = getCasoPedidoVentanilla(docDefinitivo);
                } else {
                    c = getCasoContratoVentanilla(docDefinitivo);
                }
            }
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
                        mensaje = "DOCUMENTO DE PRECOMPROMISO CANCELADO CONTABLEMENTE";
                        //se actualiza tabla con los nuevos montos
                        cmst = conncbl.prepareCall("{call pa_actualizapContratoDiversoConvenioCancelado (?)}");
                        cmst.setInt(1, folioPrecompromiso);
                        cmst.execute();
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

    private Caso getCaso(HttpServletRequest req) {
        //Debido a que el Modulo de adquisiciones y servicios no se encuentra en un flujo pero se necesita crear un caso para el precompromiso
        // se creo este método que obtiene el ID de caso desde base de datos y no de sesión
        String sql;
        String ejercicio = req.getParameter("cEjercicio");
        String cIdPedido = "";
        Integer nIdConsecutivo = Integer.parseInt(req.getParameter("consAmp"));
        if (req.getParameter("tipo").equals("Pedido")) {
            //cIdPedido = req.getParameter("cIdPedido");
            cIdPedido = req.getParameter("cIdDocumento").toString() + "-AMP-" + nIdConsecutivo;
            ;
            sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mPedidoAmpliacion p, CG_CASO c, CG_CASO_OPERACION o " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cEjercicio=?" + " and p.cIdPedido=? " + " and p.nIdConsecutivoAmpliacion=?";
        } else {
            //cIdPedido = req.getParameter("cIdContrato");
            cIdPedido = req.getParameter("cIdDocumento");
            sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mContratoAmpliacion p, CG_CASO c, CG_CASO_OPERACION o " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cEjercicio=?" + " and p.cIdContrato=?" + " and p.nIdConsecutivoAmpliacion=?";
        }
        Caso c = null;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, ejercicio);
            pstmt.setString(2, cIdPedido);
            pstmt.setInt(3, nIdConsecutivo);
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

    private Caso getCaso(HttpServletRequest req, String centroContable, String ur) {
        //Debido a que el Modulo de adquisiciones y servicios no se encuentra en un flujo pero se necesita crear un caso para el precompromiso
        // se creo este método que obtiene el ID de caso desde base de datos y no de sesión
        String sql;
        Integer nIdConsecutivo = Integer.parseInt(req.getParameter("consAmp"));
        String docDefinitivo = req.getParameter("cIdDocumentoDefinitivo").toString() + "-AMP-" + nIdConsecutivo;
        //Apartir de los atributos gurdados en sesión se hace un QRY para obtener el ID_CASO
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

    private Caso getCasoPedidoVentanilla(String docDefinitivo) {
        //Debido a que el Modulo de adquisiciones y servicios no se encuentra en un flujo pero se necesita crear un caso para el precompromiso
        // se creo este método que obtiene el ID de caso desde base de datos y no de sesión
        String sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER  " + "FROM (select ca.C_FOLIO_PRE " + "from mPedido c, mPedidoAmpliacion ca " + "where ca.cIdTipoPedido+'-'+ca.cIdUnidadEjecutora+'-'+CONVERT(varchar,ca.nIdConsecutivo)=c.cIdPedido " + "and c.cIdPedidoDefinitivo+'-AMP-'+convert(varchar,ca.nIdConsecutivoAmpliacion)=?) as p, CG_CASO c, CG_CASO_OPERACION o " + "where p.C_FOLIO_PRE=c.C_FOLIO and c.ID_CASO=o.ID_CASO";
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

    private Caso getCasoContratoVentanilla(String docDefinitivo) {
        //Debido a que el Modulo de adquisiciones y servicios no se encuentra en un flujo pero se necesita crear un caso para el precompromiso
        // se creo este método que obtiene el ID de caso desde base de datos y no de sesión
        String sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER  " + "FROM (select ca.C_FOLIO_PRE " + "from mContrato c, mContratoAmpliacion ca " + "where ca.cIdTipoContrato+'-'+ca.cIdUnidadEjecutora+'-'+CONVERT(varchar,ca.nIdConsecutivo)=c.cIdContrato " + "and c.cIdContratoDefinitivo+'-AMP-'+convert(varchar,ca.nIdConsecutivoAmpliacion)=?) as p, CG_CASO c, CG_CASO_OPERACION o " + "where p.C_FOLIO_PRE=c.C_FOLIO and c.ID_CASO=o.ID_CASO";
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

    private Caso getCasoVentanilla(String docDefinitivo, String centroContable) {
        //Debido a que el Modulo de adquisiciones y servicios no se encuentra en un flujo pero se necesita crear un caso para el precompromiso
        // se creo este método que obtiene el ID de caso desde base de datos y no de sesión
        String sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mDocumentoFolio p, CG_CASO c, CG_CASO_OPERACION o " + " where p.C_FOLIO_PRE=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and p.cIdDocumentoDefinitivo=?" + " and p.cCentroContable=?";
        Caso c = null;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, docDefinitivo);
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

    @SuppressWarnings("unchecked")
    private synchronized void aplicaContablemente(String strParam, HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre) throws ServletException, IOException {
        //Este método es una copia del método de financiero
        //Se hizo una copia para poder recibir el error
        session = request.getSession(false);
        out = response.getWriter();
        String validaSaldo = "";
        PreparedStatement pstm2 = null;
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
        AplicarContableReturn acr = null;
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
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
                    //Actualiza el estatus
                    String query = "UPDATE mContratoAmpliacion SET nIdEstado=3 where (cIdContratoDefinitivo+'-AMP-'+convert(varchar,nIdConsecutivoAmpliacion))='" + request.getParameter("cIdContrato") + "'";
                    if (request.getParameter("cIdContrato").indexOf("PE") != -1) {
                        query = "UPDATE mPedidoAmpliacion SET nIdEstado=3 where (cIdPedidoDefinitivo+'-AMP-'+convert(varchar,nIdConsecutivoAmpliacion))='" + request.getParameter("cIdContrato") + "'";
                    }
                    Util.updateQuery(query, conn);
                    //Guarda en bitacora
                    Util.bitacoraMovimientos(request.getParameter("cIdContrato"), "Precompromiso Generado", usuario.getLogin(), conn);
                    //Se guarda la relación de precompromisos con el contrato
                    pstm2 = conn.prepareStatement("insert into mRelPedContPrecomComp values(?,?,?,null,null)");
                    pstm2.setString(1, request.getParameter("cIdContrato"));
                    pstm2.setString(2, c.getFolio());
                    pstm2.setInt(3, Integer.parseInt(request.getParameter("nFolioPrecompromiso")));
                    pstm2.executeUpdate();
                    //Recargando el caso
                    Caso sc = new Caso();
                    sc.setIdCaso(c.getIdCaso());
                    c = CasoManager.select(conn, sc);
                    // Una vez que ha hecho la aplicación contable avanza el caso A CONSULTA PAGOS
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
                    conn.rollback();
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
                e.printStackTrace();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            log.error("Error occurred", "Error en Aplicacion contable:" + e.getMessage());
            mensaje = "ERROR INESPERADO DE LA APLICACION CONTABLE(MOTOR CONTABLE O NO AVANZO EL CASO).";
        } finally {
            try {
                if (pstm2 != null)
                    pstm2.close();
                if (conn != null)
                    conn.close();
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
            cmst = null;
            pstm2 = null;
        }
    }

    private synchronized void devuelveContratoDiverso(String strParam, HttpServletResponse response) {
        String[] param = strParam.split(",");
        int outputValue = -1;
        try {
            //El stored procedure pa_devuelvePedido recibe como parametros tanto el folio de cIdPedido(modulo RM), cIdContrato( modulo Financiero)
            // y el cEjercicio. Elimina los registros de contrato diverso de las tablas pContratoDiverso y tContratoEP
            out = response.getWriter();
            conn = DataSourceManager.getConnection(jndiName);
            cmst = conn.prepareCall("{?= call pa_devuelveContratoDiverso (?,?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, param[0]);
            cmst.setString(3, param[1]);
            cmst.setString(4, param[2]);
            cmst.setString(5, param[3]);
            cmst.execute();
            outputValue = cmst.getInt(1);
            if (outputValue == 0) {
                Util.bitacoraMovimientos(param[1], "Devuelve ampliación", usuario.getLogin(), conn);
                conn.commit();
            } else
                conn.rollback();
            System.out.println("Parametro de salida del procedimiento=" + outputValue);
        } catch (SQLException e1) {
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
        }
        cmst = null;
        conn = null;
    }

    private synchronized void devuelveContratoPlu(String strParam, HttpServletResponse response) {
        String[] param = strParam.split(",");
        int outputValue = -1;
        try {
            //El stored procedure pa_devuelvePedido recibe como parametros tanto el folio de cIdPedido(modulo RM), cIdContrato( modulo Financiero)
            // y el cEjercicio. Elimina los registros de contrato diverso de las tablas pContratoDiverso y tContratoEP
            out = response.getWriter();
            conn = DataSourceManager.getConnection(jndiName);
            cmst = conn.prepareCall("{?= call pa_devuelveContratoPluDiverso (?,?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, param[0]);
            cmst.setString(3, param[1]);
            cmst.setString(4, param[2]);
            cmst.setString(5, param[3]);
            cmst.execute();
            outputValue = cmst.getInt(1);
            if (outputValue == 0)
                conn.commit();
            else
                conn.rollback();
            System.out.println("Parametro de salida del procedimiento=" + outputValue);
        } catch (SQLException e1) {
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
        }
        cmst = null;
        conn = null;
    }
}
