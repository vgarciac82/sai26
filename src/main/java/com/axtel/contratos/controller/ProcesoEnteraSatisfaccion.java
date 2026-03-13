package com.axtel.contratos.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.axtel.contratos.ProcesoEnteraSatisfaccionBusinessLogic;
import com.axtel.contratos.entities.DatActaHechos;
import com.axtel.contratos.entities.DatAnexo1A;
import com.axtel.contratos.entities.DatEnteraSatisfaccion;
import com.axtel.contratos.penalties.servlet.PenaltiesServlet;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "ProcesoEnteraSatisfaccion", urlPatterns = { "/ProcesoEnteraSatisfaccion" })
public class ProcesoEnteraSatisfaccion extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger log = Logger.getLogger(PenaltiesServlet.class);

    private static String jndiName = null;

    private static String tempDir = null;

    private static String folioGenerator = null;

    private static String REPORT_PATH = "";

    public ProcesoEnteraSatisfaccion() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Usuario u = null;
        HttpSession session = null;
        PrintWriter out = null;
        JSONObject jsonObj = null;
        JSONArray arrayObj = null;
        ProcesoEnteraSatisfaccionBusinessLogic process = null;
        DatEnteraSatisfaccion dat = null;
        ITree tree = null;
        CasoBusinessLogic casoTx = null;
        try {
            session = request.getSession(false);
            if (session == null) {
                log.warn("No hay sesion");
                response.sendRedirect("../index.jsp");
                return;
            }
            u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            process = new ProcesoEnteraSatisfaccionBusinessLogic();
            arrayObj = new JSONArray();
            out = response.getWriter();
            int tipoOperacion = (null == request.getParameter("operation") || "".equals(request.getParameter("operation"))) ? 0 : Integer.parseInt(request.getParameter("operation"));
            switch(tipoOperacion) {
                case 0:
                    log.warn("No se recibió el tipo de operación");
                    break;
                case //query
                1:
                    jsonObj = process.getData(request.getParameter("cFolio"));
                    jsonObj.put("MENSAJE", "Consulta de datos");
                    jsonObj.put("RESPUESTA", true);
                    break;
                case //save
                2:
                    jsonObj = new JSONObject();
                    dat = fillObjectData(request, u);
                    process.saveInformation(dat);
                    jsonObj.put("MENSAJE", "Datos Guardados.");
                    jsonObj.put("RESPUESTA", true);
                    break;
                case //
                3:
                    jsonObj = new JSONObject();
                    dat = fillObjectData(request, u);
                    process.setDocName("Anexo1A");
                    process.setDocNameJasper("rptAnexo1A.jasper");
                    process.setFileExtension("pdf");
                    process.setReportPath(ProcesoEnteraSatisfaccion.REPORT_PATH);
                    process.setUsuario(u);
                    process.setDocument("ENTERASATISFACCION");
                    ((ProcesoEnteraSatisfaccionBusinessLogic) process).setDatEnteraSatisfaccion(dat);
                    ((ProcesoEnteraSatisfaccionBusinessLogic) process).getDocumento();
                    casoTx = new CasoBusinessLogic(jndiName);
                    tree = casoTx.getArbolCaso(casoTx.getCaso(dat.getcFolio()));
                    session.setAttribute(GestionInterface.ATT_TREE, tree);
                    jsonObj.put("MENSAJE", "Documento generado, lo puede consultar en la pestaña adjuntos.");
                    jsonObj.put("RESPUESTA", true);
                    break;
                case //
                4:
                    jsonObj = new JSONObject();
                    dat = fillObjectData(request, u);
                    process.setDocName("Acta_Hechos");
                    process.setDocNameJasper("rptActaCircunstaciadaHechos.jasper");
                    process.setFileExtension("pdf");
                    process.setReportPath(ProcesoEnteraSatisfaccion.REPORT_PATH);
                    process.setUsuario(u);
                    process.setDocument("ENTERASATISFACCION");
                    ((ProcesoEnteraSatisfaccionBusinessLogic) process).setDatEnteraSatisfaccion(dat);
                    ((ProcesoEnteraSatisfaccionBusinessLogic) process).getDocumento();
                    casoTx = new CasoBusinessLogic(jndiName);
                    tree = casoTx.getArbolCaso(casoTx.getCaso(dat.getcFolio()));
                    session.setAttribute(GestionInterface.ATT_TREE, tree);
                    jsonObj.put("MENSAJE", "Documento generado, lo puede consultar en la pestaña adjuntos.");
                    jsonObj.put("RESPUESTA", true);
                    break;
                case //save
                5:
                    jsonObj = new JSONObject();
                    dat = fillObjectData(request, u);
                    process.saveValidate(dat);
                    jsonObj.put("MENSAJE", "Datos Guardados.");
                    jsonObj.put("RESPUESTA", true);
                    break;
                case //send
                6:
                    jsonObj = new JSONObject();
                    dat = fillObjectData(request, u);
                    process.setDocName("Anexo1A");
                    process.setDocNameJasper("rptActaCircunstaciadaHechos.jasper");
                    process.setFileExtension("pdf");
                    process.setReportPath(ProcesoEnteraSatisfaccion.REPORT_PATH);
                    process.setUsuario(u);
                    process.setDocument("ENTERASATISFACCION");
                    ((ProcesoEnteraSatisfaccionBusinessLogic) process).setDatEnteraSatisfaccion(dat);
                    ((ProcesoEnteraSatisfaccionBusinessLogic) process).sendProcess();
                    jsonObj.put("MENSAJE", "Datos Guardados.");
                    jsonObj.put("RESPUESTA", true);
                    break;
                default:
                    log.warn("Operación desconocida");
                    break;
            }
        } catch (Exception e) {
            jsonObj = new JSONObject();
            try {
                jsonObj.put("MENSAJE", e.getMessage());
                jsonObj.put("RESPUESTA", false);
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            String destino = new String(arrayObj.put(jsonObj).toString().getBytes("UTF-8"), "ISO-8859-1");
            out.println(destino);
            if (out != null) {
                out.flush();
                out.close();
            }
            out = null;
            jsonObj = null;
            arrayObj = null;
            process = null;
            dat = null;
            casoTx = null;
        }
    }

    private DatEnteraSatisfaccion fillObjectData(HttpServletRequest request, Usuario u) throws Exception {
        DatEnteraSatisfaccion dat = new DatEnteraSatisfaccion();
        DatAnexo1A anexo = new DatAnexo1A();
        DatActaHechos acta = null;
        dat.setcFolio(request.getParameter("cFolio"));
        dat.setcFolioFirmante(request.getParameter("cFolioFirmante"));
        dat.setcIdContratoDefinitivo(request.getParameter("cIdContratoDefinitivo"));
        dat.setcIdRFC(request.getParameter("cIdRFC"));
        dat.setcNoContratoCNET(request.getParameter("cNumCNET"));
        dat.setcPuestoFirmante(request.getParameter("cPuestoFirmante"));
        dat.setcObservacionesTramite(request.getParameter("cObservacionesTramite"));
        dat.setnServPrestEnteraSatisfaccion((null == request.getParameter("nServPrestEnteraSatisfaccion") || "".equals(request.getParameter("nServPrestEnteraSatisfaccion"))) ? 0 : Integer.parseInt(request.getParameter("nServPrestEnteraSatisfaccion")));
        dat.setnCentroTrabajo((null == request.getParameter("nCentroTrabajo") || "".equals(request.getParameter("nCentroTrabajo"))) ? 0 : Integer.parseInt(request.getParameter("nCentroTrabajo")));
        dat.setnIdLinea((null == request.getParameter("nIdLinea") || "".equals(request.getParameter("nIdLinea"))) ? 0 : Integer.parseInt(request.getParameter("nIdLinea")));
        dat.setnIdPeriodoPago((null == request.getParameter("nIdPeriodoPago") || "".equals(request.getParameter("nIdPeriodoPago"))) ? 0 : Integer.parseInt(request.getParameter("nIdPeriodoPago")));
        dat.setnNumEmpFirmante((null == request.getParameter("nNumEmpFirmante") || "".equals(request.getParameter("nNumEmpFirmante"))) ? 0 : Integer.parseInt(request.getParameter("nNumEmpFirmante")));
        dat.setcUsuarioCaptura(u.getLogin());
        dat.setcUsuarioValida(u.getLogin());
        dat.setnIdEstatus((null == request.getParameter("nIdEstate") || "".equals(request.getParameter("nIdEstate"))) ? 0 : Integer.parseInt(request.getParameter("nIdEstate")));
        dat.setnServicioEnteraSatisfaccion((null == request.getParameter("nServicioEnteraSatisfaccion") || "".equals(request.getParameter("nServicioEnteraSatisfaccion"))) ? 0 : Integer.parseInt(request.getParameter("nServicioEnteraSatisfaccion")));
        dat.setnIdCaso((null == request.getParameter("idCaso") || "".equals(request.getParameter("idCaso"))) ? 0 : Integer.parseInt(request.getParameter("idCaso")));
        anexo.setcAnioFormalizacion(request.getParameter("cAnioFormalizacion"));
        anexo.setcDeclaraccion(request.getParameter("cDeclaraccion"));
        anexo.setcDescripcionServicio(request.getParameter("cDescripcionServicio"));
        anexo.setcEjercicioPago(request.getParameter("cEjercicioPago"));
        anexo.setcInmueble(request.getParameter("cInmueble"));
        anexo.setcMesFormalizacion(request.getParameter("cMesFormalizacion"));
        anexo.setnDiaformalizacion((null == request.getParameter("nDiaformalizacion") || "".equals(request.getParameter("nDiaformalizacion"))) ? 0 : Integer.parseInt(request.getParameter("nDiaformalizacion")));
        dat.setAnexo(anexo);
        if (dat.getnServPrestEnteraSatisfaccion() == 2) {
            acta = new DatActaHechos();
            acta.setcDescripCierreHechos(request.getParameter("cDescripCierreHechos"));
            acta.setcDescripcion1(request.getParameter("cDescripcion1"));
            acta.setcDescripcion2(request.getParameter("cDescripcion2"));
            acta.setcDescripcionHechos(request.getParameter("cDescripcionHechos"));
            acta.setcFolioTestigo1(request.getParameter("cFolioTestigo1"));
            acta.setcFolioTestigo2(request.getParameter("cFolioTestigo2"));
            acta.setcPuestoTestigo1(request.getParameter("cPuestoTestigo1"));
            acta.setcPuestoTestigo2(request.getParameter("cPuestoTestigo2"));
            acta.setcLugarAdscripcion(request.getParameter("cLugarAdscripcion"));
            acta.setnNumEmpTestigo1((null == request.getParameter("nNumEmpTestigo1") || "".equals(request.getParameter("nNumEmpTestigo1"))) ? 0 : Integer.parseInt(request.getParameter("nNumEmpTestigo1")));
            acta.setnNumEmpTestigo2((null == request.getParameter("nNumEmpTestigo2") || "".equals(request.getParameter("nNumEmpTestigo2"))) ? 0 : Integer.parseInt(request.getParameter("nNumEmpTestigo2")));
            dat.setActaHechos(acta);
        }
        return dat;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        REPORT_PATH = getServletContext().getRealPath("Reportes");
        tempDir = config.getInitParameter("tempDir");
        if (tempDir == null) {
            //tempDir = config.getServletContext().getRealPath("/") +  "upload" + File.separator;
            tempDir = config.getServletContext().getRealPath("/") + ".." + File.separator + "upload" + File.separator;
            File fDir = new File(tempDir);
            if (!fDir.exists())
                if (!fDir.mkdirs())
                    throw new ServletException("No se pudo crear el directorio " + tempDir);
        }
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
        // Put your code here
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
}
