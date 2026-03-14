package com.syc.adquisiciones.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.axtel.contratos.ContractStatus;
import com.syc.adquisiciones.ContratoCap4Interface;
import com.syc.adquisiciones.core.ContratoCap4Impl;
import com.syc.adquisiciones.core.DatosContratoCap4;
import com.syc.adquisiciones.core.FechasContratacion;
import com.syc.adquisiciones.core.PartidasContratoCap4;
import com.syc.adquisiciones.util.Util;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ContratoCap4Servlet", urlPatterns = { "/servlet/ContratoCap4Servlet" })
public class ContratoCap4Servlet extends HttpServlet {

    private static final long serialVersionUID = -8349642683144876100L;

    private static String jndiName = null;

    private static Logger log = LoggerFactory.getLogger(ContratoCap4Servlet.class);

    private Connection conn = null;

    private JSONArray arrayObj;

    private JSONObject jsonObj;

    private PrintWriter out = null;

    private String folioGenerator = null;

    public ContratoCap4Servlet() {
        super();
    }

    public void destroy() {
        super.destroy();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        boolean isModuloPluris = false;
        try {
            if (session == null) {
                log.warn("No hay sesion");
                response.sendRedirect("../index.jsp");
                return;
            }
            session.setAttribute(GestionInterface.ATT_EstatusContratCap4, request.getParameter("nIdEstatus"));
            session.setAttribute(GestionInterface.ATT_ContratCap4Definitivo, request.getParameter("cIdContratoDefinitivo"));
            session.setAttribute(GestionInterface.ATT_ContratCap4Abierto, request.getParameter("nesAbierto"));
            isModuloPluris = StringUtils.isBlank(request.getParameter("esModuloPluris")) ? false : true;
            if (isModuloPluris) {
                response.sendRedirect("../Generador/SAICYS/ContratoPlurianualCap4.jsp?tab=2");
            } else {
                response.sendRedirect("../Generador/SAICYS/ContratoCap4.jsp?tab=2");
            }
        } catch (Exception e) {
            // TODO: handle exception
            log.error("Object: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String mensaje = "";
        boolean respuesta = false;
        try {
            if (session == null) {
                log.warn("No hay sesion");
                response.sendRedirect("../index.jsp");
                return;
            }
            ContratoCap4Interface cont = new ContratoCap4Impl();
            Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            conn = DataSourceManager.getConnection(jndiName);
            DatosContratoCap4 datosCont = fullObject(request);
            arrayObj = new JSONArray();
            jsonObj = new JSONObject();
            out = response.getWriter();
            int tipoOperacion = Integer.parseInt(request.getParameter("operacion"));
            log.debug("Object: {}", "operacion: " + tipoOperacion);
            String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
            switch(tipoOperacion) {
                case //Nuevo
                0:
                    mensaje = "Contrato creado correctamente.";
                    respuesta = cont.nuevo(conn, session, usuario, datosCont);
                    if (respuesta) {
                        session.setAttribute(GestionInterface.ATT_EstatusContratCap4, 1 + "");
                        session.setAttribute(GestionInterface.ATT_ContratCap4Definitivo, datosCont.getcIdcontratoDefinitivo());
                        session.setAttribute(GestionInterface.ATT_ContratCap4Abierto, 0 + "");
                    }
                    break;
                case //Guarda caratula del Contrato
                1:
                    mensaje = "El contrato se guardo correctamente";
                    ArrayList<FechasContratacion> arrayDates = setArrayDates(request);
                    datosCont.setArrayFechas(arrayDates);
                    respuesta = cont.guardaContrato(conn, usuario, datosCont);
                    break;
                case //Guardar Partidas
                2:
                    mensaje = "Partidas guardadas correctamente";
                    ArrayList<PartidasContratoCap4> arrayItems = setArrayItems(request);
                    datosCont.setArrayPartidas(arrayItems);
                    respuesta = cont.guardaContratoPartidas(conn, usuario, datosCont);
                    break;
                case //Aprueba el Contrato
                3:
                    mensaje = new String("El contrato se aprobó  correctamente".getBytes("UTF-8"), "ISO-8859-1");
                    respuesta = cont.apruebaContrato(conn, request, usuario, datosCont, folioGenerator, jndiName, prefixPath);
                    if (respuesta) {
                        session.setAttribute(GestionInterface.ATT_EstatusContratCap4, 2 + "");
                    }
                    break;
                case //devulve el contrato
                4:
                    mensaje = new String("El contrato se devolvió  correctamente".getBytes("UTF-8"), "ISO-8859-1");
                    respuesta = cont.devuelveContrato(conn, usuario, datosCont);
                    if (respuesta) {
                        session.setAttribute(GestionInterface.ATT_EstatusContratCap4, 1 + "");
                    }
                    break;
                case //Precopromete
                5:
                    mensaje = "Precompromiso creado correctamente";
                    String cadTabla = request.getParameter("tablaDatos");
                    String[] arrayTabla = cadTabla.split(",");
                    ArrayList<List<String>> tabla = Util.creaArray(arrayTabla);
                    respuesta = cont.precomprometer(conn, request, usuario, datosCont, tabla, prefixPath, jndiName);
                    if (respuesta) {
                        session.setAttribute(GestionInterface.ATT_EstatusContratCap4, 3 + "");
                    }
                    break;
                case //Devolver pre-compromiso
                6:
                    mensaje = "El precompromiso se cancelo correctamente";
                    respuesta = cont.devuelvePrecompromiso(conn, request, usuario, datosCont, prefixPath, jndiName);
                    if (respuesta) {
                        session.setAttribute(GestionInterface.ATT_EstatusContratCap4, 2 + "");
                    }
                    break;
                case //Compromete
                7:
                    mensaje = "Compromiso creado correctamente";
                    respuesta = cont.comprometer(conn, request, usuario, datosCont, jndiName, prefixPath);
                    if (respuesta) {
                        session.setAttribute(GestionInterface.ATT_EstatusContratCap4, 4 + "");
                    }
                    break;
                case //Aprueba Ampliación
                8:
                    mensaje = new String("La  ampliación se aprobó correctamente".getBytes("UTF-8"), "ISO-8859-1");
                    respuesta = cont.apruebaAmpliacionContrato(conn, usuario, datosCont);
                    break;
                case //devuelve la ampliaion
                9:
                    mensaje = new String("La ampliación del contrato se devolvió correctamente".getBytes("UTF-8"), "ISO-8859-1");
                    respuesta = cont.devuelveAmpliacion(conn, usuario, datosCont);
                    break;
                case //pre-compromete el recurso de la ampliación
                10:
                    mensaje = new String("Precompromiso creado correctamente".getBytes("UTF-8"), "ISO-8859-1");
                    String cadTbla = request.getParameter("tablaDatos");
                    String[] arrayTbla = cadTbla.split(",");
                    ArrayList<List<String>> tbla = Util.creaArray(arrayTbla);
                    respuesta = cont.precomprometeAmpliacion(conn, request, tbla, usuario, datosCont, jndiName, prefixPath);
                    break;
                case //devolver pre-compromiso de una ampliación
                11:
                    mensaje = "El precompromiso se cancelo correctamente";
                    respuesta = cont.devuelvePrecompromisoAmpliacion(conn, request, usuario, datosCont, jndiName, prefixPath);
                    break;
                case //compromiso de una ampliaion
                12:
                    mensaje = "Compromiso creado correctamente";
                    respuesta = cont.comprometeAmpliacion(conn, request, usuario, datosCont, jndiName, prefixPath);
                    break;
                case //Nuevo convenio
                13:
                    mensaje = "Convenio Creado correctamente";
                    respuesta = cont.nuevoConvenio(conn, session, usuario, datosCont);
                    break;
                case //Actualiza convenio
                14:
                    mensaje = "Convenio Actualizado correctamente";
                    respuesta = cont.updateConvenio(conn, session, usuario, datosCont);
                    break;
                case //Elimina convenio
                15:
                    mensaje = "Convenio Eliminado correctamente";
                    respuesta = cont.deleteConvenio(conn, session, usuario, datosCont);
                    break;
                case //Migrar Contrato plurianual
                16:
                    mensaje = "Contrato migrado correctamente";
                    respuesta = cont.migraContratoPlurianual(conn, session, usuario, datosCont);
                    session.setAttribute(GestionInterface.ATT_ContratCap4Definitivo, datosCont.getcIdcontratoDefinitivo());
                    break;
                case //Save Partidas Contrato plurianual
                17:
                    mensaje = "Datos Guardados";
                    ArrayList<PartidasContratoCap4> array = setArrayItems(request);
                    datosCont.setArrayPartidas(array);
                    respuesta = cont.savePartidasContPlurianual(conn, session, usuario, datosCont);
                    break;
                case //Aprueba el Contrato plurianual cap 4
                18:
                    mensaje = new String("El contrato se aprobó  correctamente".getBytes("UTF-8"), "ISO-8859-1");
                    respuesta = cont.apruebaContratoPluri(conn, request, usuario, datosCont, folioGenerator, jndiName, prefixPath);
                    if (respuesta) {
                        session.setAttribute(GestionInterface.ATT_EstatusContratCap4, ContractStatus.BUDGET);
                    }
                    break;
                case //devulve el contrato
                19:
                    mensaje = new String("El contrato se devolvió  correctamente".getBytes("UTF-8"), "ISO-8859-1");
                    respuesta = cont.devuelveContratoPluri(conn, usuario, datosCont);
                    if (respuesta) {
                        session.setAttribute(GestionInterface.ATT_EstatusContratCap4, ContractStatus.CAPTURED);
                    }
                    break;
                case //Precopromete
                20:
                    mensaje = "Precompromiso creado correctamente";
                    String cadTabl = request.getParameter("tablaDatos");
                    String[] arrayTabl = cadTabl.split(",");
                    ArrayList<List<String>> table = Util.creaArray(arrayTabl);
                    respuesta = cont.precomprometerContratoPluri(conn, request, usuario, datosCont, table, prefixPath, jndiName);
                    if (respuesta) {
                        session.setAttribute(GestionInterface.ATT_EstatusContratCap4, 3 + "");
                    }
                    break;
                case //Devolver pre-compromiso
                21:
                    mensaje = "El precompromiso se cancelo correctamente";
                    respuesta = cont.devuelvePrecompromisoContratoPluri(conn, request, usuario, datosCont, prefixPath, jndiName);
                    if (respuesta) {
                        session.setAttribute(GestionInterface.ATT_EstatusContratCap4, ContractStatus.BUDGET);
                    }
                    break;
                case //Compromete
                22:
                    mensaje = "Compromiso creado correctamente";
                    respuesta = cont.comprometerContratoPluri(conn, request, usuario, datosCont, jndiName, prefixPath);
                    if (respuesta) {
                        session.setAttribute(GestionInterface.ATT_EstatusContratCap4, ContractStatus.APPROVED);
                    }
                    break;
                default:
                    log.warn("Operación incorrecta");
                    break;
            }
            if (respuesta) {
                conn.commit();
            } else {
                mensaje = "Error. Notifique a soporte t\u00e9nico SAI.";
                conn.rollback();
            }
        } catch (Exception e) {
            // TODO: handle exception
            try {
                respuesta = false;
                conn.rollback();
                e.printStackTrace();
                mensaje = "Error: " + new String(e.getMessage().getBytes("UTF-8"), "ISO-8859-1");
                log.error("Object: {}", e.getMessage());
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                mensaje = "Error: " + new String(e1.getMessage().getBytes("UTF-8"), "ISO-8859-1");
                log.error("Object: {}", e1.getMessage());
            }
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                conn = null;
                jsonObj.put("RESPUESTA", respuesta);
                jsonObj.put("MENSAJE", mensaje);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                log.error("Object: {}", e.getMessage());
            } catch (JSONException ex) {
                ex.printStackTrace();
                log.error("Object: {}", ex.getMessage());
            }
            String destino = arrayObj.put(jsonObj).toString();
            out.println(destino);
        }
    }

    private DatosContratoCap4 fullObject(HttpServletRequest request) throws Exception {
        DatosContratoCap4 datosCont = new DatosContratoCap4();
        datosCont.setcIdcontratoDefinitivo(request.getParameter("cIdContratoDefinitivo") == null ? "" : request.getParameter("cIdContratoDefinitivo"));
        datosCont.setcIdUnidadEjecutora(request.getParameter("cIdUnidadEjecutora") == null ? "" : request.getParameter("cIdUnidadEjecutora"));
        datosCont.setcActividadEconomica(request.getParameter("actEconomContratoCap4") == null ? 1 : Integer.parseInt(request.getParameter("actEconomContratoCap4")));
        datosCont.setcDescripcion(request.getParameter("cDescripcion") == null ? "" : request.getParameter("cDescripcion"));
        datosCont.setcNoContCNET(request.getParameter("cnumCompranet") == null ? "" : request.getParameter("cnumCompranet"));
        datosCont.setcNoProcedimientoCNET(request.getParameter("cnumProcedimientoCompranet") == null ? "" : request.getParameter("cnumProcedimientoCompranet"));
        datosCont.setnCondContratoCNET(request.getParameter("nCodContratoCNET") == null ? "" : request.getParameter("nCodContratoCNET"));
        datosCont.setnCondExpedienteCNET(request.getParameter("nCodExpedienteCNET") == null ? "" : request.getParameter("nCodExpedienteCNET"));
        datosCont.setcFolioMASCP(request.getParameter("cFolioMASCP") == null ? "" : request.getParameter("cFolioMASCP"));
        datosCont.setcOficioDG(request.getParameter("cOficioDG") == null ? "" : request.getParameter("cOficioDG"));
        datosCont.setnEsPlurianual(request.getParameter("isPlurianual") == null ? 0 : Integer.parseInt(request.getParameter("isPlurianual")));
        datosCont.setnEsAbierto(request.getParameter("isAbierto") == null ? 0 : Integer.parseInt(request.getParameter("isAbierto")));
        datosCont.setTotalPlurianual(request.getParameter("montoTotalPluri") == null ? 0.0 : Double.parseDouble(request.getParameter("montoTotalPluri")));
        datosCont.setcCuentaDisponible(request.getParameter("cCuentaDisponible") == null ? "82106" : request.getParameter("cCuentaDisponible"));
        //String.valueOf(anio)
        datosCont.setcEjercicio(Util.obtieneEjercicioFiscalActivo(conn));
        datosCont.setcIdRFC(request.getParameter("cIdRFC") == null ? "" : request.getParameter("cIdRFC"));
        datosCont.setnEsDescentralizado(request.getParameter("nEsDescentralizado") == null ? 0 : Integer.parseInt(request.getParameter("nEsDescentralizado")));
        datosCont.setnIdCategoria(request.getParameter("nIdCategoria") == null ? 1 : Integer.parseInt(request.getParameter("nIdCategoria")));
        datosCont.setnIdFundamentoLeg(request.getParameter("nIdFundamentoLeg") == null ? 2 : Integer.parseInt(request.getParameter("nIdFundamentoLeg")));
        datosCont.setDescripPoliza(request.getParameter("descripPoliza") == null ? "" : request.getParameter("descripPoliza"));
        datosCont.setnIdAmpliacion(request.getParameter("nIdAmpliacion") == null ? -1 : Integer.parseInt(request.getParameter("nIdAmpliacion")));
        datosCont.setnComprometeMax(request.getParameter("nComprometeMax") == null ? 0 : Integer.parseInt(request.getParameter("nComprometeMax")));
        return datosCont;
    }

    private ArrayList<FechasContratacion> setArrayDates(HttpServletRequest request) throws Exception {
        ArrayList<FechasContratacion> arrayFechas = new ArrayList<FechasContratacion>();
        FechasContratacion fechasCont = null;
        log.debug("Armando el array de fechas");
        String cadenaFechas = request.getParameter("cadenaFechas");
        String[] arrayTabla = cadenaFechas.split(",");
        //1-02/05/2016,2-03/05/2016,4-04/05/2016,12-05/05/2016,17-06/05/2016,18-09/05/2016
        String[] arrayValores = null;
        for (int i = 0; i < arrayTabla.length; i++) {
            fechasCont = new FechasContratacion();
            arrayValores = arrayTabla[i].split("-");
            fechasCont.setIdFecha(Integer.parseInt(arrayValores[0]));
            fechasCont.setValue(arrayValores[1]);
            log.info("Object: {}", "IdFecha=" + arrayValores[0] + " fecha=" + arrayValores[1]);
            arrayFechas.add(fechasCont);
            fechasCont = null;
            arrayValores = null;
        }
        return arrayFechas;
    }

    private ArrayList<PartidasContratoCap4> setArrayItems(HttpServletRequest request) throws Exception {
        ArrayList<PartidasContratoCap4> arrayPartida = new ArrayList<PartidasContratoCap4>();
        PartidasContratoCap4 partidaCont = null;
        log.debug("Armando el array de partidas");
        //cadenaPartidas+=aData[0]+"-"+$("#cDescripAdi_"+aData[0]).val()+"-"+cantMin+"-"+cantMax+"-"+precioU+"-"+precioUMax
        //             +"-"+mMontoNetoLine+"-"+mMontoNetoMin+"-"+mMontoNetoMax+"-"+mMontoNetoPluri+"-"+nIdIVA+"-"+mMontoNetoLineOrig+"-"+mMontoNetoMaxOrig+"-"+cidUniMed;
        String cadenaPartidas = request.getParameter("cadenaPartidas");
        log.info("Object: {}", "Cadena : " + cadenaPartidas);
        String[] arrayTupla = cadenaPartidas.split(",");
        String[] arrayValores = null;
        for (int i = 0; i < arrayTupla.length; i++) {
            partidaCont = new PartidasContratoCap4();
            arrayValores = arrayTupla[i].split("-");
            partidaCont.setnIdContatoPartida(Integer.parseInt(arrayValores[0]));
            partidaCont.setcDescripAdi(arrayValores[1]);
            partidaCont.setnCantidadMin(Integer.parseInt(arrayValores[2]));
            partidaCont.setnCantidadMax(Integer.parseInt(arrayValores[3]));
            partidaCont.setmPrecioU(Math.round(Double.parseDouble(arrayValores[4]) * 100) / 100.0d);
            partidaCont.setmPrecioUMax(Math.round(Double.parseDouble(arrayValores[5]) * 100) / 100.0d);
            partidaCont.setmMontoNetoLinea(Math.round(Double.parseDouble(arrayValores[6]) * 100) / 100.0d);
            partidaCont.setmMontoNetoMin(Math.round(Double.parseDouble(arrayValores[7]) * 100) / 100.0d);
            partidaCont.setmMontoNetoMax(Math.round(Double.parseDouble(arrayValores[8]) * 100) / 100.0d);
            partidaCont.setmMontoNetoPluri(Math.round(Double.parseDouble(arrayValores[9]) * 100) / 100.0d);
            partidaCont.setnIdIVA(Integer.parseInt(arrayValores[10]));
            partidaCont.setmMontoNetoLineaOrig(Math.round(Double.parseDouble(arrayValores[11]) * 100) / 100.0d);
            partidaCont.setmMontoNetoMaxOrig(Math.round(Double.parseDouble(arrayValores[12]) * 100) / 100.0d);
            partidaCont.setcIdUnidadMedida(arrayValores[13]);
            log.info("Object: {}", "IdContatoPartida=" + arrayValores[0] + " DescripAdi=" + arrayValores[1] + " CantidadMin=" + arrayValores[2] + " CantidadMax=" + arrayValores[3] + " PrecioU=" + arrayValores[4] + " PrecioUMa=" + arrayValores[5] + " MontoNetoLinea=" + arrayValores[6] + " MontoNetoMin=" + arrayValores[7] + " MontoNetoMax=" + arrayValores[8] + " MontoNetoPluri=" + arrayValores[9] + " nIdIVA=" + arrayValores[10] + " MontoNetoLineaOrig=" + arrayValores[11] + " MontoNetoMaxOrig=" + arrayValores[12] + " cidUniMed=" + arrayValores[13]);
            arrayPartida.add(partidaCont);
            arrayValores = null;
            partidaCont = null;
        }
        return arrayPartida;
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException if an error occurs
     */
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
        // Put your code here
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
}
