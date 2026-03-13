package com.syc.contable.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.contable.IAnteProyectoBusinessLogic;
import com.syc.contable.core.IAnteproyectoEncabezado;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.CasoOperacionBusinessLogic;
import com.syc.gestion.EmpleadoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.Empleado;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "IAnteproyectoServlet", urlPatterns = { "/gstnmngr/IAnteproyecto", "/gstnmngr/IAnteproyecto/CargaAnteProyecto", "/gstnmngr/IntegraAnteproyecto" })
public class IAnteproyectoServlet extends HttpServlet implements GestionInterface {

    private static final Logger log = Logger.getLogger(IAnteproyectoServlet.class);

    private static final long serialVersionUID = 1L;

    private static String jniName = null;

    private static String folioGenerator = null;

    //para obtener el ejercicio fiscal en diferentes funciones
    private static AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);

    String DATE_FORMAT = "dd/MM/yyyy";

    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

    // today
    Calendar c1 = Calendar.getInstance();

    String today = sdf.format(c1.getTime());

    /**
     * Directorio temporal donde se almacenara el archivo de carga.
     */
    private static String TEMP_DIR = "";

    public IAnteproyectoServlet() {
        super();
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
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    /**
     * The doPost method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to
     * post.
     *
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String inserta = request.getParameter("inserta");
        AdecuacionBusinessLogic adec = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        IAnteProyectoBusinessLogic ibl = new IAnteProyectoBusinessLogic(GestionInterface.ATT_CONEXION);
        String mensajeRetorno = "";
        String accion = request.getRequestURI().indexOf("IAnteproyecto") > 0 ? "CargaAnteProyecto" : (request.getRequestURI().indexOf("IntegraAnteproyecto") > 0 ? "IntegraAnteproyecto" : "ConsultaAnteProyecto");
        int folio = 0;
        HttpSession session = request.getSession(false);
        Caso caso = (Caso) session.getAttribute(ATT_CASE);
        if (caso != null)
            folio = new Integer(caso.getFolio().substring(caso.getFolio().lastIndexOf('-') + 1)).intValue();
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            log.warn("Peticion sin usuario o sesion invalida");
            response.sendRedirect("../index.jsp");
            return;
        }
        if ("CargaAnteProyecto".equals(accion)) {
            List<?> fileItems = null;
            Iterator<?> iter = null;
            InputStream archivoCargaIS = null;
            DataInputStream archivoCargaStream = null;
            String nombreDestino = "";
            boolean error = false;
            IAnteproyectoEncabezado enc = new IAnteproyectoEncabezado();
            enc.setbIntegrado("N");
            try {
                enc.setaEjercicioFiscal(adec.obtenEjercicioFiscal());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                ibl.borraTCorrida(folio);
                fileItems = Util.parseRequest(request, IAnteproyectoServlet.TEMP_DIR, -1);
                iter = fileItems.iterator();
                String nombreArchivo = "";
                enc.setfCarga(today);
                while (iter.hasNext()) {
                    FileItem item = (FileItem) iter.next();
                    if (item.isFormField()) {
                        String area = "";
                        if ("area".equalsIgnoreCase(item.getFieldName()))
                            area = item.getString();
                        String dl = "";
                        if ("dl".equalsIgnoreCase(item.getFieldName()))
                            dl = item.getString();
                        String oc = "";
                        if ("oc".equalsIgnoreCase(item.getFieldName()))
                            oc = item.getString();
                        String un = "";
                        if ("un".equalsIgnoreCase(item.getFieldName()))
                            un = item.getString();
                        if (area != null && !"".equals(area)) {
                            enc.setcArea(area);
                            enc.setbArea("1");
                        }
                        if (dl != null && !"".equals(dl)) {
                            enc.setbDL("1");
                        }
                        if (oc != null && !"".equals(oc)) {
                            enc.setbOC("1");
                        }
                        if (un != null && !"".equals(un)) {
                            enc.setbUN("1");
                        }
                        String descripcion = "";
                        if ("descripcion".equalsIgnoreCase(item.getFieldName())) {
                            descripcion = item.getString();
                            enc.setcDescripcion(descripcion);
                        }
                        String unormativa = "";
                        if ("unormativa".equalsIgnoreCase(item.getFieldName())) {
                            unormativa = item.getString();
                            enc.setcUnidadNormativa(unormativa);
                        }
                        String uresponsable = "";
                        if ("uedl".equalsIgnoreCase(item.getFieldName())) {
                            uresponsable = item.getString();
                            enc.setcUnidadResponsable(uresponsable);
                        }
                        String FOLIO = "";
                        if ("FOLIO".equalsIgnoreCase(item.getFieldName())) {
                            FOLIO = item.getString();
                            enc.setFolio(new Integer(FOLIO.substring(FOLIO.lastIndexOf('-') + 1)).intValue());
                        }
                        String id_caso = "";
                        if ("id_caso".equalsIgnoreCase(item.getFieldName())) {
                            id_caso = item.getString();
                            enc.setId_caso(Integer.parseInt(id_caso));
                        }
                        String responsableSig = "";
                        if ("grupo".equalsIgnoreCase(item.getFieldName())) {
                            responsableSig = item.getString();
                            enc.setResponsable(responsableSig);
                        }
                        String responsableSigInt = "";
                        if ("grupoInt".equalsIgnoreCase(item.getFieldName())) {
                            responsableSigInt = item.getString();
                            enc.setResponsableInt(responsableSigInt);
                        }
                    } else {
                        archivoCargaIS = item.getInputStream();
                        archivoCargaStream = new DataInputStream(item.getInputStream());
                        nombreArchivo = item.getName();
                        String extension = Util.getFileExtencion(nombreArchivo);
                        if (!"xls".equalsIgnoreCase(extension))
                            throw new Exception("No se puede procesar archivos [" + extension + "] Corrija e intente de nuevo");
                        nombreDestino = IAnteproyectoServlet.TEMP_DIR + "carga_archivo_anteproyecto_" + System.currentTimeMillis() + "." + extension;
                        log.info("Copiando archivo :" + nombreArchivo);
                        Util.copiaArchivo(archivoCargaStream, nombreDestino);
                        item.delete();
                        log.debug("Procesando archivo:" + nombreArchivo);
                        IAnteProyectoBusinessLogic iapbl = new IAnteProyectoBusinessLogic(u.getLogin());
                        mensajeRetorno = "Error de carga: ";
                        int i;
                        List<String> mensajes = iapbl.cargaExcelValidacionAnteProyecto(nombreDestino, folio);
                        if (mensajes.size() > 0) {
                            error = true;
                            for (i = 0; i < mensajes.size(); i++) mensajeRetorno += "\\n" + mensajes.get(i).replace("'", "").replace('"', ' ');
                        }
                        List<String> validacionesUR = ibl.validacionAnteProyectoUR(folio, u.getU_UR());
                        if (validacionesUR.size() > 0) {
                            error = true;
                            for (i = 0; i < validacionesUR.size(); i++) mensajeRetorno += "\\n" + validacionesUR.get(i).replace("'", "").replace('"', ' ');
                        }
                        List<String> validacionesDup = ibl.validacionAnteProyectoDuplicidad(folio);
                        if (validacionesDup.size() > 0) {
                            error = true;
                            for (i = 0; i < validacionesDup.size(); i++) mensajeRetorno += "\\n" + validacionesDup.get(i).replace("'", "").replace('"', ' ');
                        }
                        List<String> validacionesCat = ibl.validacionAnteProyectoCatalogos(folio);
                        if (validacionesCat.size() > 0) {
                            error = true;
                            for (i = 0; i < validacionesCat.size(); i++) mensajeRetorno += "\\n" + validacionesCat.get(i).replace("'", "").replace('"', ' ');
                        }
                        if (!error) {
                            ibl.insertaIAnteproyectoEncabezado(enc);
                            ibl.insertaIAnteproyectoValidado(folio);
                            mensajeRetorno = "Archivo cargado exitosamente";
                        }
                    }
                }
            } catch (Exception exc) {
                log.error(exc, exc);
                mensajeRetorno = "No se pudo procesar el excel debido al siguiente error:\\n" + exc;
            } finally {
                if (archivoCargaStream != null)
                    try {
                        archivoCargaStream.close();
                    } catch (Exception e) {
                        log.error("Error cerrando flujo DataInputStream" + e);
                    }
                if (archivoCargaIS != null)
                    try {
                        archivoCargaIS.close();
                    } catch (Exception e) {
                        log.error("Error cerrando flujo InputStream" + e);
                    }
                archivoCargaIS = null;
                archivoCargaStream = null;
                if (!"".equals(nombreDestino)) {
                    File toDelete = new File(nombreDestino);
                    if (!toDelete.delete())
                        toDelete.deleteOnExit();
                }
            }
            session.setAttribute("MENSAJE_CARGA", mensajeRetorno);
            response.sendRedirect("../../plantillasCasos/ianteproyecto.jsp");
        } else if ("IntegraAnteproyecto".equals(accion)) {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            int id_oper = 3;
            try {
                String grupoInt = "";
                String grupo = "";
                if (u.getGrupo("INT_ANTPROYECTO_GRF") != null) {
                    //&& request.getParameter("integraGRF")!=null && "1".equals(request.getParameter("integraGRF"))){
                    id_oper = 6;
                    grupoInt = "INTEGRADOR_ANTPROYECTO_GRF";
                    grupo = "INT_ANTPROYECTO_GRF";
                } else if (u.getGrupo("INT_ANTPROYECTO_UN_" + u.getU_UR()) != null) {
                    id_oper = 4;
                    grupoInt = "INTEGRADOR_ANTPROYECTO_UN_" + u.getU_UR();
                    grupo = "INT_ANTPROYECTO_UN_" + u.getU_UR();
                }
                if (u.getGrupo("INT_ANTPROYECTO_OC_" + u.getU_UR()) != null) {
                    grupoInt = "INTEGRADOR_ANTPROYECTO_OC_" + u.getU_UR();
                    grupo = "INT_ANTPROYECTO_OC_" + u.getU_UR();
                }
                if (u.getGrupo("INT_ANTPROYECTO_DL_" + u.getU_UR()) != null) {
                    grupoInt = "INTEGRADOR_ANTPROYECTO_DL_" + u.getU_UR();
                    grupo = "INT_ANTPROYECTO_DL_" + u.getU_UR();
                }
                String folioIant = ibl.integrado(u.getU_UR(), grupoInt, id_oper);
                if (folioIant == null || "".equals(folioIant)) {
                    Caso cIant = null;
                    FolioGeneratorInterface fg = null;
                    try {
                        ClassLoader cl = getClass().getClassLoader();
                        Class<?> clase = cl.loadClass(folioGenerator);
                        fg = (FolioGeneratorInterface) clase.newInstance();
                    } catch (ClassNotFoundException exc) {
                        log.error("Generador de folios", exc);
                        throw new ServletException(exc);
                    } catch (InstantiationException exc) {
                        log.error("Generador de folios", exc);
                        throw new ServletException(exc);
                    } catch (IllegalAccessException exc) {
                        log.error("Generador de folios", exc);
                        throw new ServletException(exc);
                    }
                    try {
                        cIant = generaCaso(u, 40, fg, grupoInt, "Integrador AnteProyecto", response, request, id_oper);
                        IAnteproyectoEncabezado enc = new IAnteproyectoEncabezado();
                        enc.setaEjercicioFiscal(adec.obtenEjercicioFiscal());
                        enc.setcArea("");
                        enc.setcUnidadNormativa("");
                        enc.setcUnidadResponsable(u.getU_UR());
                        enc.setfCarga(today);
                        enc.setFolio(new Integer(cIant.getFolio().substring(cIant.getFolio().lastIndexOf('-') + 1)).intValue());
                        enc.setId_caso(cIant.getIdCaso());
                        enc.setbIntegrado("S");
                        enc.setResponsableInt(grupoInt);
                        enc.setResponsable(grupo);
                        ibl.insertaIAnteproyectoEncabezado(enc);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ibl.insertaIntegraCaptura(new Integer(cIant.getFolio().substring(cIant.getFolio().lastIndexOf('-') + 1)).intValue(), folio);
                } else {
                    int folioInt = new Integer(folioIant.substring(folioIant.lastIndexOf('-') + 1)).intValue();
                    ibl.insertaIntegraCaptura(folioInt, folio);
                }
                Map<?, ?> m = CasoDatoManager.readValuesCasoDato(request, caso.getCasoDato(), true);
                String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
                cbl.avanzaCaso(caso, u.getLogin(), "", new String[] { "CONSULTA_INTANTEPROYECTO" }, new String[] { "consulta_intanteproyecto" }, m, prefixPath);
                response.sendRedirect("../caso/principal.jsp");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            //int idCasoReintegro = new Integer(cIant.getFolio().substring(cIant.getFolio().lastIndexOf('-') + 1)).intValue();
            //String folioCasoReintegro = cIant.getFolio();
        } else {
            response.sendRedirect("../../plantillasCasos/ianteproyecto.jsp");
        }
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException
     *             if an error occurs
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            TEMP_DIR = (String) ic.lookup("java:comp/env/TemporaryDirectory");
            if (TEMP_DIR == null) {
                TEMP_DIR = "../upload/PEF/";
                log.info("Environment Entry \"TEMP_DIR\" nula usando default \"" + TEMP_DIR + "\"");
            } else
                log.info("dataSourceRefName=" + TEMP_DIR);
        } catch (NamingException exc) {
            TEMP_DIR = "../upload/PEF/";
            log.info("Ocurrio un error que evito que se cargara la entrada \"TEMP_DIR\"" + exc);
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + TEMP_DIR + "\"");
        }
        try {
            File f = new File(TEMP_DIR);
            if (!f.exists())
                if (!f.mkdirs())
                    throw new Exception("No se puede crear el directorio temporal " + TEMP_DIR);
        } catch (Exception e) {
            log.error("No fue posible crear automaticamente el directorio temporal: " + TEMP_DIR + " Solicite su creacion manual");
        }
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
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

    public static Caso generaCaso(Usuario u, int idTCaso, FolioGeneratorInterface fg, String opResponsable, String concepto, HttpServletResponse response, HttpServletRequest request, int oper) throws GestionException, SQLException, Exception {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("../index.jsp");
        }
        CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
        Caso c = casoTx.IniciaCaso(u, idTCaso, fg);
        CasoOperacion co = c.getCasoOperacion(0);
        co.setIdOperacion(oper);
        co.setResponsable(opResponsable);
        CasoOperacionBusinessLogic cobl = new CasoOperacionBusinessLogic(jniName);
        cobl.updateCasoOperacion(co);
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        Empleado e = new Empleado();
        EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
        e.setClaveUsuario(u.getLogin());
        e = ebl.getEmpleado(e);
        // Variables del caso
        c.getCasoDato("FOLIO").setValor(c.getFolio());
        c.getCasoDato("OPERADOR").setValor(u.getLogin());
        c.getCasoDato("FECHA_DOCUMENTO").setValor(fecha);
        c.getCasoDato("EJERCICIO_FISCAL").setValor(adecProy.obtenEjercicioFiscal());
        c.getCasoDato("CONCEPTO_MOV").setValor(concepto);
        c.getCasoDato("MONEDA").setValor("MXP");
        c.getCasoDato("ID_AREA_ORIGEN").setValor(e.getClaveArea());
        c.getCasoDato("ID_DL_ORIGEN").setValor(u.getU_UR());
        Map<String, String> m = new HashMap<String, String>();
        m.put("FOLIO", c.getFolio());
        m.put("OPERADOR", u.getLogin());
        m.put("FECHA_DOCUMENTO", fecha);
        m.put("EJERCICIO_FISCAL", adecProy.obtenEjercicioFiscal());
        m.put("CONCEPTO_MOV", "Integracion Anteproyecto");
        m.put("MONEDA", "MXP");
        m.put("ID_AREA_ORIGEN", e.getClaveArea());
        m.put("ID_DL_ORIGEN", u.getU_UR());
        c.setIdGabinete(casoTx.creaExpediente(u.getLogin(), c));
        // Guarda las variables de caso.
        c = casoTx.actualizaCasoDato(c, m);
        return c;
    }
}
