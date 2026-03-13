package com.syc.adquisiciones.servlet;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
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
import com.syc.adquisiciones.businessLogic.ProgramaAnualBusinessLogic;
import com.syc.adquisiciones.core.DatosPAAS;
import com.syc.adquisiciones.core.Respuesta;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioPropiedades;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "ProgramaAnualServlet", urlPatterns = { "/servlet/ProgramaAnualServlet" })
public class ProgramaAnualServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static String jndiName = null;

    private static Logger log = Logger.getLogger(ProgramaAnualServlet.class);

    private Connection conn = null;

    private CallableStatement cmst = null;

    private PreparedStatement pstmt = null;

    private ResultSet rs = null;

    private JSONArray arrayObj;

    private JSONObject jsonObj;

    private PrintWriter out = null;

    private static String tempDir = "";

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
        tempDir = config.getInitParameter("tempDir");
        if (tempDir == null) {
            //tempDir = config.getServletContext().getRealPath("/") +  "upload" + File.separator;
            tempDir = config.getServletContext().getRealPath("/") + ".." + File.separator + "upload" + File.separator;
            File fDir = new File(tempDir);
            if (!fDir.exists())
                if (!fDir.mkdirs())
                    throw new ServletException("No se pudo crear el directorio " + tempDir);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        HttpSession session = null;
        int tipoOperacion = 0;
        try {
            session = request.getSession(false);
            if (session == null) {
                log.info("no hay sessión");
                response.sendRedirect("../index.jsp");
                return;
            }
            arrayObj = new JSONArray();
            jsonObj = new JSONObject();
            usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            tipoOperacion = Integer.parseInt(request.getParameter("operacion"));
            String strParam = request.getParameter("Param");
            switch(tipoOperacion) {
                case 0:
                    altaProgramaAnual(strParam, request, response);
                    break;
                case 1:
                    precargaPAAS(strParam, request, response);
                    break;
                case 2:
                    layoutPAAS(request, response, session);
                    break;
                case //Elimina cucop
                3:
                    deleteCUCOP(request, response);
                    break;
                case //consulta PAAS cap mil
                4:
                    operacionesPAASCap1(request, response);
                    break;
                default:
                    log.info("Operación incorrecta. No existe este tipo de operación");
                    //ejemploUploadLayout();
                    break;
            }
        } catch (Exception e) {
            // TODO: handle exception
            log.error("No hay session: " + e);
            e.printStackTrace();
        }
    }

    private void operacionesPAASCap1(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, JSONException {
        String destino;
        ProgramaAnualBusinessLogic business = null;
        DatosPAAS dat = null;
        boolean respuesta = false;
        String mensaje = "";
        Respuesta resp = null;
        int opcion = Integer.parseInt(request.getParameter("opcion"));
        Map propiedades = usuario.getPropiedades();
        UsuarioPropiedades up = (UsuarioPropiedades) propiedades.get("CCENTROCONTABLE");
        try {
            this.out = response.getWriter();
            business = new ProgramaAnualBusinessLogic();
            switch(opcion) {
                case 1:
                    log.info("Alta programa Anual capitulo mil");
                    dat = llenaDatos(request);
                    resp = business.altaProgramaAnualCapMil(dat);
                    mensaje = resp.getMsg();
                    respuesta = resp.isResp();
                    break;
                case 2:
                    log.info("Las propiedades del usuario cambiara la UnidadEjecutora='A03' y su centroContable=10");
                    up.setValor("10");
                    usuario.setU_UR("A03");
                    usuario.setPropiedad("CCENTROCONTABLE", up);
                    jsonObj = business.consultaSelects();
                    mensaje = "consulta correcta";
                    respuesta = true;
                    break;
                case 3:
                    log.info("Actualiza montos por capitulo");
                    dat = llenaDatos(request);
                    jsonObj = business.actualizaMontosPAASCapMil(dat);
                    mensaje = "consulta correcta";
                    respuesta = true;
                    break;
                case 4:
                    log.info("Elimina cucop cap mil");
                    dat = llenaDatos(request);
                    resp = business.deleteCUCOPCapMil(dat);
                    mensaje = resp.getMsg();
                    respuesta = resp.isResp();
                    if (respuesta) {
                        jsonObj = business.actualizaMontosPAASCapMil(dat);
                    }
                    break;
                case 5:
                    log.info("Consulta datos del calendario PAAS cap Mil");
                    dat = llenaDatos(request);
                    jsonObj = business.datosCalendario(dat);
                    mensaje = "consulta correcta";
                    respuesta = true;
                    break;
                case 6:
                    log.info("Inicia el guardado del calendario PAAS cap Mil");
                    dat = llenaDatos(request);
                    resp = business.guardaCalendarioPAASCapMil(dat);
                    mensaje = resp.getMsg();
                    respuesta = resp.isResp();
                    if (respuesta) {
                        jsonObj = business.datosCalendario(dat);
                    }
                    break;
                default:
                    log.info("Opción incorrecta. No existe este tipo de opción");
                    break;
            }
        } catch (Exception e) {
            respuesta = false;
            mensaje = e.getMessage();
            log.error(e);
        } finally {
            business = null;
            dat = null;
            up = null;
            propiedades = null;
            try {
                this.jsonObj.put("RESPUESTA", respuesta);
                this.jsonObj.put("MENSAJE", mensaje);
            } catch (JSONException e) {
                log.error(e);
            }
            String str = new String(this.arrayObj.put(this.jsonObj).toString().getBytes("UTF-8"), "ISO-8859-1");
            this.out.println(str);
        }
    }

    private DatosPAAS llenaDatos(HttpServletRequest request) throws Exception {
        DatosPAAS dat = new DatosPAAS();
        dat.setcCentroContable((request.getParameter("cCentroContable") == null) ? "" : request.getParameter("cCentroContable"));
        dat.setcEjercicio((request.getParameter("cEjercicio") == null) ? "" : request.getParameter("cEjercicio"));
        dat.setcLogin(this.usuario.getLogin());
        dat.setcUnidadEjecutora((request.getParameter("cUnidadEjecutora") == null) ? "" : request.getParameter("cUnidadEjecutora"));
        dat.setcPartidaCapMil((request.getParameter("cPartidaCapMil") == null) ? "" : request.getParameter("cPartidaCapMil"));
        dat.setcCucopEliminar((request.getParameter("cCucopEliminar") == null) ? "" : request.getParameter("cCucopEliminar"));
        dat.setcPartidaCapMilEliminar((request.getParameter("cIdPartidaCapMilEliminar") == null) ? "" : request.getParameter("cIdPartidaCapMilEliminar"));
        dat.setcPartidaEliminar((request.getParameter("cIdPartidaEliminar") == null) ? "" : request.getParameter("cIdPartidaEliminar"));
        dat.setcCadTabla((request.getParameter("cadTabla") == null) ? "" : request.getParameter("cadTabla"));
        dat.setmMontoBrutoPlurianual((request.getParameter("montoBrutoPluri") == null || "".equalsIgnoreCase(request.getParameter("montoBrutoPluri"))) ? 0 : Integer.parseInt(request.getParameter("montoBrutoPluri")));
        dat.setmMontoDestMiPyme((request.getParameter("pymeEdita") == null || "".equalsIgnoreCase(request.getParameter("pymeEdita"))) ? 0 : Integer.parseInt(request.getParameter("pymeEdita")));
        dat.setmMontoEstimadoComprasNoCubiertas((request.getParameter("porcentajeEdita") == null || "".equalsIgnoreCase(request.getParameter("porcentajeEdita"))) ? 0 : Integer.parseInt(request.getParameter("porcentajeEdita")));
        dat.setnCantidaEjercicios((request.getParameter("nEjerciciosPluris") == null || "".equalsIgnoreCase(request.getParameter("nEjerciciosPluris"))) ? 0 : Integer.parseInt(request.getParameter("nEjerciciosPluris")));
        dat.setnPlurianual((request.getParameter("isPluri") == null || "".equalsIgnoreCase(request.getParameter("isPluri"))) ? 0 : Integer.parseInt(request.getParameter("isPluri")));
        dat.setcProcedencia((request.getParameter("tipoProcedimientoEdita") == null || "".equalsIgnoreCase(request.getParameter("tipoProcedimientoEdita"))) ? "N" : request.getParameter("tipoProcedimientoEdita"));
        dat.setcTipoProcedimiento((request.getParameter("tipoAdjudicacion") == null || "".equalsIgnoreCase(request.getParameter("tipoAdjudicacion"))) ? "LP" : request.getParameter("tipoAdjudicacion"));
        dat.setnIVA((request.getParameter("ivaEdita") == null || "".equalsIgnoreCase(request.getParameter("ivaEdita"))) ? 16 : Integer.parseInt(request.getParameter("ivaEdita")));
        return dat;
    }

    private void deleteCUCOP(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, JSONException {
        ProgramaAnualBusinessLogic business = null;
        Respuesta resp = null;
        try {
            out = response.getWriter();
            String cIdUnidadEjecutora = (null == request.getParameter("cIdUnidadEjecutora") ? "" : request.getParameter("cIdUnidadEjecutora"));
            String cCucop = (null == request.getParameter("cCucop") ? "" : request.getParameter("cCucop"));
            String cPartida = (null == request.getParameter("cPartida") ? "" : request.getParameter("cPartida"));
            business = new ProgramaAnualBusinessLogic();
            resp = business.deleteCUCOP(cIdUnidadEjecutora, cCucop, cPartida);
        } catch (Exception e) {
            if (resp == null) {
                resp = new Respuesta();
            }
            resp.setMsg(e.getMessage().toString());
            e.printStackTrace();
        } finally {
            business = null;
            jsonObj.put("MSG", resp.getMsg());
            jsonObj.put("RESP", resp.isResp());
            String destino = arrayObj.put(jsonObj).toString();
            out.println(destino);
        }
    }

    private void ejemploUploadLayout() {
        try {
            Util ut = new Util();
            String nameFile = "EjemploLayoutPAAS.csv";
            String pathFile = "C:\\Users\\Humberto\\Desktop\\LayoutDetallePAAS.csv";
            ut.uploadFileServerBD(nameFile, pathFile);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private void layoutPAAS(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        List<?> fileItems = null;
        Iterator<?> iter = null;
        DataInputStream archivoCargaStream = null;
        String nombreDestino = "";
        Caso c = null;
        String[] responsable;
        String[] nombre;
        String resp = "true";
        String urlDet = "";
        String urlEnc = "";
        String url = "";
        String UE = "";
        String destino = "";
        String dominio = "";
        PreparedStatement pstmnt = null;
        boolean cargaArchivo = false;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(jndiName);
            fileItems = Util.parseRequest(request, ProgramaAnualServlet.tempDir, -1);
            iter = fileItems.iterator();
            out = response.getWriter();
            String nombreArchivo = "";
            String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
            String tipoCaso = "47";
            String CONCEPTO_MOV = "Layout PAAS";
            String DATE_FORMAT = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            // today
            Calendar c1 = Calendar.getInstance();
            today = sdf.format(c1.getTime());
            conn = DataSourceManager.getConnection(jndiName);
            String rutaRemoto = "";
            String[] splitRuta = null;
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                if (item.isFormField()) {
                    if ("ejercicio".equals(item.getFieldName()))
                        cEjercicio = item.getString();
                    if ("UE".equals(item.getFieldName()))
                        UE = item.getString();
                    item.delete();
                    continue;
                }
                archivoCargaStream = new DataInputStream(item.getInputStream());
                nombreArchivo = item.getName();
                String extension = Util.getFileExtencion(nombreArchivo);
                if (!"csv".equalsIgnoreCase(extension)) {
                    resp = "No se puede procesar archivos [" + extension + "] Corrija e intente de nuevo";
                    throw new Exception("No se puede procesar archivos [" + extension + "] Corrija e intente de nuevo");
                }
                //Crear el caso
                responsable = new String[] { "VENTANILLA_LAYOUTPAAS" };
                nombre = new String[] { "VENTANILLA_LAYOUTPAAS" };
                if (c == null) {
                    c = generaGuardaCaso(request, response, session, prefixPath, tipoCaso, CONCEPTO_MOV, responsable, nombre);
                }
                nombreDestino = generaNombre(extension, UE);
                log.info("creando el arbol para el archivo :" + nombreArchivo);
                if ("uploadfileEncabezado".equals(item.getFieldName())) {
                    urlEnc = nombreDestino;
                    cbl.recibeDocumentoGestion(c, 0, "Layout_" + UE + "_Enc", extension, new DataInputStream(item.getInputStream()), true);
                }
                if ("uploadfileDetallado".equals(item.getFieldName())) {
                    urlDet = nombreDestino;
                    cbl.recibeDocumentoGestion(c, 0, "Layout_" + UE + "_Det", extension, new DataInputStream(item.getInputStream()), true);
                }
                log.info("Copiando archivo :" + nombreArchivo);
                //Util.copiaArchivo(archivoCargaStream, nombreDestino);
                rutaRemoto = ConfiguraAplicativoManager.obtenRutaRemoto(conn);
                splitRuta = rutaRemoto.split("/");
                dominio = ConfiguraAplicativoManager.obtenDominioRemoto(conn);
                dominio = dominio.replace(".", "");
                cargaArchivo = Util.uploadStreamServerBD(nombreDestino, archivoCargaStream, conn);
                if (!cargaArchivo) {
                    resp = "No se pudo copiar el rchivo " + nombreDestino + " en el server.";
                    log.info("No se pudo copiar el rchivo " + nombreDestino + " en el server.");
                    throw new Exception("No se pudo copiar el rchivo " + nombreDestino + " en el server.");
                }
                item.delete();
            }
            //query precarga la ue del PAAS
            int retval = -1;
            pstmnt = conn.prepareStatement("insert into mProgramaAnual  VALUES (?, ?, ?, getdate(), ?)");
            pstmnt.setString(1, cEjercicio);
            pstmnt.setString(2, UE);
            pstmnt.setString(3, usuario.getPropiedad("CCENTROCONTABLE").getValor());
            pstmnt.setString(4, usuario.getLogin());
            retval = pstmnt.executeUpdate();
            // query bulck insert encabezado
            String query = "BULK INSERT mProgramaAnualDetalle FROM '" + dominio + splitRuta[3] + "\\" + urlEnc + "' WITH (FIELDTERMINATOR = ',',ROWTERMINATOR = '\\n')";
            url = urlEnc;
            if (retval != -1) {
                resp = bulckInsert(conn, query);
            } else {
                resp = "Error al crear la unidad del PAAS.";
            }
            // query bulck insert detalle
            if ("true".equalsIgnoreCase(resp)) {
                query = "BULK INSERT mProgramaAnualDetallePeriodo FROM '" + dominio + splitRuta[3] + "\\" + urlDet + "' WITH (FIELDTERMINATOR = ',',ROWTERMINATOR = '\\n')";
                url = urlDet;
                resp = bulckInsert(conn, query);
                //avanza caso
                if ("true".equalsIgnoreCase(resp)) {
                    responsable = new String[] { "CONSULTA_LAYOUTPAAS" };
                    nombre = new String[] { "CONSULTA_LAYOUTPAAS" };
                    if (!avanzaCaso(request, conn, c, usuario, prefixPath, responsable, nombre)) {
                        resp = "Error al avanzar el caso";
                    }
                } else {
                    responsable = new String[] { "TERMINAR" };
                    nombre = new String[] { "TERMINAR" };
                    if (!avanzaCaso(request, conn, c, usuario, prefixPath, responsable, nombre)) {
                        resp = "Error al terminar el caso";
                    }
                }
            } else {
                responsable = new String[] { "TERMINAR" };
                nombre = new String[] { "TERMINAR" };
                if (!avanzaCaso(request, conn, c, usuario, prefixPath, responsable, nombre)) {
                    resp = "Error al terminar el caso";
                }
            }
            if ("true".equalsIgnoreCase(resp)) {
                jsonObj.put("Respuesta", "Termina Satisfactoriamente. Con Folio:" + c.getFolio());
                conn.commit();
            } else {
                jsonObj.put("Respuesta", ".\n" + url + ".\n" + resp);
                conn.rollback();
            }
            destino = arrayObj.put(jsonObj).toString();
        } catch (Exception e) {
            // TODO: handle exception
            try {
                jsonObj.put("Respuesta", ".\n" + url + ".\n" + new String(e.getMessage().getBytes("UTF-8"), "ISO-8859-1"));
                destino = arrayObj.put(jsonObj).toString();
                System.out.println("Respuesta: " + destino);
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (UnsupportedEncodingException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }
            e.printStackTrace();
            log.error("Error: " + e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    log.error("Error al cerrar la coneccion." + e);
                }
            }
            conn = null;
            out.println(destino);
        }
    }

    private synchronized Caso generaGuardaCaso(HttpServletRequest request, HttpServletResponse response, HttpSession session, String prefixPath, String tipoCaso, String CONCEPTO_MOV, String[] responsable, String[] nombre) throws ServletException, IOException {
        //Metodo utilizado para generar los casos
        int folio;
        String folioCaso = null;
        Caso c = null;
        Connection conn = null;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            CasoBusinessLogic cbl = new CasoBusinessLogic(jndiName);
            c = iniciaCaso(tipoCaso);
            folioCaso = c.getFolio();
            log.debug("Caso obtenido: " + folioCaso);
            int indice = folioCaso.lastIndexOf('-') + 1;
            folio = Integer.parseInt(folioCaso.substring(indice));
            //Argumentos para llenar la tabla de CG_CASO_DATO y que se muestren en el inbox
            Map<String, String> datos = new HashMap<String, String>();
            datos.put("FOLIO", folioCaso);
            datos.put("FECHA_DOCUMENTO", today);
            datos.put("EJERCICIO_FISCAL", cEjercicio);
            datos.put("OPERADOR", usuario.getNombre());
            datos.put("CONCEPTO_MOV", CONCEPTO_MOV);
            datos.put("MONEDA", "MXP");
            datos.put("APLICADO_CONT", "false");
            //Actualiza el caso en BD con Map<> datos
            CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), datos);
            //Caso sc solo tiene el id caso para hacer un select de toda su info
            //y actualizar asi los valores de Caso c
            Caso sc = new Caso();
            sc.setIdCaso(c.getIdCaso());
            c = CasoManager.select(conn, sc);
            avanzaCaso(request, conn, sc, usuario, prefixPath, responsable, nombre);
            //session.setAttribute(GestionInterface.ATT_CASE, c);
            if (c.getIdGabinete() == -1) {
                c.setIdGabinete(cbl.creaExpediente(usuario.getLogin(), c));
                cbl.recibeDocumentoGestion(c, new DataInputStream(request.getInputStream()));
            }
            conn.commit();
        } catch (Exception exc) {
            try {
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
        return c;
    }

    @SuppressWarnings("finally")
    private boolean avanzaCaso(HttpServletRequest request, Connection conn, Caso c, Usuario usuario, String prefixPath, String[] responsable, String[] nombre) {
        PreparedStatement pstmnt = null;
        boolean respuesta = true;
        try {
            //Avanza el caso
            Caso sc = new Caso();
            sc.setIdCaso(c.getIdCaso());
            c = CasoManager.select(conn, sc);
            //avanzaCaso(request, c, usuario, prefixPath, new String[] { "VENTANILLA_APARTADO" },	new String[] { "VENTANILLA_APTD" });
            if (c == null) {
                log.error("Llamada invalida, sin Caso seleccionado");
                respuesta = false;
                throw new GestionException("Llamada inválida, sin Caso seleccionado");
            }
            if (c.getIdCaso() <= 0) {
                log.error("Llamada invalida, identificador de caso menor o igual a cero (<= 0)");
                respuesta = false;
                throw new GestionException("Llamada inválida, identificador de caso menor o igual a cero (<= 0)");
            }
            if (c.getCasoOperacion(0).getIdOperacion() <= 0) {
                log.error("Llamada invalida, sin identificador de caso operacion menor o igual a cero (<= 0)");
                respuesta = false;
                throw new GestionException("Llamada inválida, sin identificador de caso operación menor o igual a cero (<= 0)");
            }
            Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
            CasoBusinessLogic cbl = new CasoBusinessLogic(jndiName);
            cbl.avanzaCaso(c, usuario.getLogin(), "", responsable, nombre, m, prefixPath);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            log.error("Error: " + e);
        } finally {
            return respuesta;
        }
    }

    private String bulckInsert(Connection conn, String query) throws SQLException {
        Statement stm = null;
        ResultSet rs = null;
        PreparedStatement pstm = null;
        String resp = "true";
        try {
            log.info(query);
            pstm = conn.prepareStatement(query);
            pstm.executeUpdate();
        } catch (Exception e) {
            // TODO: handle exception
            resp = new String(e.getMessage());
            e.printStackTrace();
            log.error("Error: " + e);
        }
        return resp;
    }

    private void leeCSV() {
        String csvFile = "C:/ejemploCSV.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] country = line.split(cvsSplitBy);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Done");
    }

    public void altaProgramaAnual(String strParam, HttpServletRequest request, HttpServletResponse response) {
        //String strParam = request.getParameter("Param");
        String[] param = strParam.split(",");
        int outputValue;
        try {
            out = response.getWriter();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            conn = DataSourceManager.getConnection(jndiName);
            cmst = conn.prepareCall("{?= call pa_altaPAA (?,?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, param[0].trim());
            cmst.setString(3, param[1].trim());
            cmst.setString(4, param[2].trim());
            cmst.setString(5, param[3]);
            cmst.execute();
            outputValue = cmst.getInt(1);
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
            cmst = null;
            conn = null;
        }
    }

    public void precargaPAAS(String strParam, HttpServletRequest request, HttpServletResponse response) {
        //String strParam = request.getParameter("Param");
        String[] param = strParam.split(",");
        int outputValue;
        try {
            out = response.getWriter();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            conn = DataSourceManager.getConnection(jndiName);
            cmst = conn.prepareCall("{?= call pa_mPrecargaPAA (?,?,?,?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, param[0].trim());
            cmst.setString(3, param[1].trim());
            cmst.setString(4, param[2].trim());
            cmst.setString(5, param[3]);
            cmst.setInt(6, Integer.parseInt(param[4]));
            cmst.setBoolean(7, Boolean.parseBoolean(param[5]));
            cmst.execute();
            outputValue = cmst.getInt(1);
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
            cmst = null;
            conn = null;
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private List parseRequest(HttpServletRequest req) throws ServletException {
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

    private synchronized Caso iniciaCaso(String tCaso) throws GestionException {
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
        log.error(usuario + "_" + idTC + "_" + fg);
        if (c == null) {
            log.error("No se logro crear el caso");
            throw new GestionException("No se logró crear el caso");
        }
        return c;
    }

    public static synchronized String generaNombre(String extension, String UE) {
        String idRandom = String.valueOf(Math.round((1 + Math.random()) * 10000));
        String idArchivoFinal = GestionInterface.PREFIX_TEMP.substring(0, GestionInterface.PREFIX_TEMP.length() - idRandom.length()) + idRandom;
        String nombreDestino = "LAYOUTPAAS_" + UE + "_" + System.currentTimeMillis() + "_" + idArchivoFinal + "." + extension;
        log.info("LAYOUTPAAS_" + UE + "_" + System.currentTimeMillis() + "_" + idArchivoFinal + "." + extension);
        return nombreDestino;
    }
}
