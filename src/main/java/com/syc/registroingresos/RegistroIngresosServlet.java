package com.syc.registroingresos;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;
//import org.json.JSONArray;
//import org.json.JSONObject;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "RegistroIngresosServlet", urlPatterns = { "/servlet/RegistroIngresosServlet" })
public class RegistroIngresosServlet extends HttpServlet {

    private static final long serialVersionUID = 3830246252504144684L;

    private static final Logger log = Logger.getLogger(RegistroIngresosServlet.class);

    private String jniName = null;

    private static String folioGenerator = null;

    //private JSONArray arrayObj;
    //private JSONObject jsonObj;
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    //para obtener el ejercicio fiscal en diferentes funciones
    AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);

    RegistroIngresosBussinesLogic registroIngreso = new RegistroIngresosBussinesLogic(GestionInterface.ATT_CONEXION);

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String today = sdf.format(c1.getTime());
        //arrayObj = new JSONArray();
        //jsonObj = new JSONObject();
        AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        String aEjercicioFiscal = "";
        try {
            aEjercicioFiscal = adecProy.obtenEjercicioFiscal();
        } catch (Exception e) {
            throw new ServletException(e);
        }
        if (Integer.parseInt(aEjercicioFiscal) != c1.get(Calendar.YEAR))
            today = "31/12/" + aEjercicioFiscal;
        //String pathUrl = request.getContextPath();
        //String pathBase = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + pathUrl + "/";
        String mensaje = "";
        //String cancelaDocumento = request.getParameter("cancelaDocumento");
        //String folio = request.getParameter("folio");
        String inserta = request.getParameter("inserta");
        String aplica = request.getParameter("aplica");
        //String cancela = request.getParameter("cancela");
        String tipoIngreso = request.getParameter("tipoIngreso");
        String nApartado = request.getParameter("nApartado");
        //request.getParameter("fAplica");
        String fAplica = today;
        String cPrograma = request.getParameter("cPrograma");
        //String pathURL = request.getContextPath();
        //String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + pathURL + "/";
        if (inserta != null && !"".equals(inserta) && "1".equals(inserta)) {
            HttpSession session = request.getSession(false);
            PrintWriter out = response.getWriter();
            if (session == null) {
                response.sendRedirect("../index.jsp");
                return;
            }
            //Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            //Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
            // Map<?,?> m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
            // String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
            try {
                out.println(new String(InsertsRegistrosIngresos(response, request)));
                if ("14".equals(cPrograma)) {
                    registroIngreso.ActualizaRendimientosGreenMex(response, request);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                log.warn(e);
                out.println("Error: " + e.getMessage());
            } catch (GestionException e) {
                e.printStackTrace();
                log.warn(e);
                out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                log.warn(e);
                out.println("Error: " + e.getMessage());
            }
        }
        if (aplica != null && !"".equals(aplica) && "1".equals(aplica)) {
            if (tipoIngreso != null && !"".equals(tipoIngreso) && "1".equals(tipoIngreso)) {
                HttpSession session = request.getSession(false);
                PrintWriter out = response.getWriter();
                if (session == null) {
                    response.sendRedirect("../index.jsp");
                    return;
                }
                Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
                Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
                Map<?, ?> m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
                try {
                    mensaje = registroIngreso.autorizaRegistroIngresosApartado("nFolioApartado", "tApartadoEncabezado", nApartado, "tApartadoDetalle", "APARTADO", fAplica, c, m, prefixPath, usuario.getLogin(), usuario);
                    out.println(new String(mensaje));
                } catch (SQLException e) {
                    e.printStackTrace();
                    log.warn(e);
                    out.println("Error: " + e.getMessage());
                } catch (GestionException e) {
                    e.printStackTrace();
                    log.warn(e);
                    out.println("Error: " + e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    log.warn(e);
                    out.println("Error: " + e.getMessage());
                }
            } else {
                HttpSession session = request.getSession(false);
                PrintWriter out = response.getWriter();
                if (session == null) {
                    response.sendRedirect("../index.jsp");
                    return;
                }
                Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
                Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
                Map<?, ?> m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
                try {
                    mensaje = registroIngreso.autorizaRegistroIngresos(c, m, prefixPath, usuario.getLogin(), usuario);
                    out.println(new String(mensaje));
                } catch (SQLException e) {
                    e.printStackTrace();
                    log.warn(e);
                    out.println("Error: " + e.getMessage());
                } catch (GestionException e) {
                    e.printStackTrace();
                    log.warn(e);
                    out.println("Error: " + e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    log.warn(e);
                    out.println("Error: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException if an error occurs
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
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

    public String InsertsRegistrosIngresos(HttpServletResponse response, HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession(false);
        String mensaje = "";
        try {
            Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            int idCasoRegistroIngreso = Integer.parseInt(request.getParameter("folio"));
            int apartado = Integer.parseInt(request.getParameter("apartado"));
            String folioCasoRegistroIngreso = request.getParameter("folio");
            String sTipoIngreso = request.getParameter("nTipoIngreso");
            String cTipoIngreso = request.getParameter("cTipoIngreso");
            String fechaApl = request.getParameter("fechaApl");
            String fechaCap = request.getParameter("fechaCap");
            String cPrograma = request.getParameter("cPrograma");
            String cConcepto = new String(request.getParameter("cConcepto").replaceAll("\r\n", "").replaceAll("\n", "").getBytes("ISO-8859-1"), "UTF-8");
            String CTAB = request.getParameter("CTAB");
            String caNoContrarrecibo = request.getParameter("caNoContrarrecibo");
            String importeIP = request.getParameter("mImporteIP");
            String[] renglon = request.getParameterValues("renglon[]");
            String[] ep = request.getParameterValues("ep[]");
            String[] mes = request.getParameterValues("mes[]");
            String[] importe = request.getParameterValues("importe[]");
            RegistrosIngresosEncabezado rie = new RegistrosIngresosEncabezado();
            rie.setnTipoIngreso(sTipoIngreso);
            rie.setcTipoIngreso(cTipoIngreso);
            rie.setfAplicacion(fechaApl);
            rie.setfCaptura(fechaCap);
            rie.setcPrograma(cPrograma);
            rie.setcConcepto(cConcepto);
            rie.setaEjercicioFiscal(adecProy.obtenEjercicioFiscal());
            rie.setcCentroContable(u.getPropiedad("CCENTROCONTABLE").getValor());
            rie.setcRamo(u.getU_Ramo());
            rie.setcUnidadResponsableContable("RHQ");
            //TODO: Modificar Tipo Poliza para Recurso Fiscal
            if ("1".equals(sTipoIngreso)) {
                //TIPO POLIZA REGISTRO INGRESO FISCAL
                rie.setcTipoPoliza("PR");
            } else {
                //TIPO POLIZA REGISTRO INGRESO PROPIO
                rie.setcTipoPoliza("IN");
            }
            //rie.setcDocumentoHaplicado("N");
            rie.setcUnidadResponsable(u.getU_UR());
            rie.setnFolioRegistroIngreso(idCasoRegistroIngreso);
            rie.setnFolioApartado(apartado);
            rie.setcaNoContrarrecibo(caNoContrarrecibo);
            ArrayList<RegistrosIngresosDetalle> rd = new ArrayList<RegistrosIngresosDetalle>();
            RegistrosIngresosDetalle rdaux;
            double sumaImportes = 0;
            if ("1".equals(sTipoIngreso)) {
                for (int i = 0; i < renglon.length; i++) {
                    rdaux = new RegistrosIngresosDetalle();
                    rdaux.setEP(ep[i]);
                    rdaux.setnMes(Integer.parseInt(mes[i]));
                    rdaux.setcCentroContable(u.getPropiedad("CCENTROCONTABLE").getValor());
                    rdaux.setmImporte(Double.parseDouble(importe[i]));
                    rdaux.setnDocRenglon((i + 1));
                    rdaux.setCTAB(CTAB);
                    sumaImportes += rdaux.getmImporte();
                    rd.add(rdaux);
                }
            } else if ("2".equals(sTipoIngreso)) {
                rdaux = new RegistrosIngresosDetalle();
                rdaux.setEP("");
                rdaux.setnMes(0);
                rdaux.setcCentroContable(u.getPropiedad("CCENTROCONTABLE").getValor());
                rdaux.setmImporte(Double.parseDouble(importeIP));
                rdaux.setnDocRenglon(1);
                rdaux.setCTAB(CTAB);
                sumaImportes += rdaux.getmImporte();
                rd.add(rdaux);
            }
            rie.setmImporte(sumaImportes);
            mensaje = registroIngreso.insertaRegistroIngreso(rie, rd, idCasoRegistroIngreso, folioCasoRegistroIngreso, u);
            if ("2".equals(sTipoIngreso)) {
                RegistroIngresoRazonSocial rzip = new RegistroIngresoRazonSocial();
                String cClave = request.getParameter("cClave");
                String fRecepcionRecurso = request.getParameter("fRecepcionRecurso");
                String cOrigenTransferencia = request.getParameter("cOrigenTransferencia");
                String cNombreGestion = request.getParameter("cNombreGestion");
                int nesExtranjero = Integer.parseInt(request.getParameter("nesExtranjero"));
                String cesDonativo = request.getParameter("cesDonativo");
                String cComprobanteFiscal = request.getParameter("cComprobanteFiscal");
                rzip.setnFolioRegistroIngreso(idCasoRegistroIngreso);
                rzip.setcClave(cClave);
                rzip.setfRecepcionRecurso(fRecepcionRecurso);
                rzip.setcOrigenTransferencia(cOrigenTransferencia);
                rzip.setcNombreGestion(cNombreGestion);
                rzip.setNesExtranjero(nesExtranjero);
                rzip.setCesDonativo(cesDonativo);
                rzip.setcComprobanteFiscal(cComprobanteFiscal);
                if ("11".equals(cPrograma) || "6".equals(cPrograma) || "14".equals(cPrograma) || "15".equals(cPrograma) || "16".equals(cPrograma)) {
                    mensaje = registroIngreso.insertaRegistroRazonSolicalIP(rzip, cPrograma, u);
                }
            }
        } catch (Exception exc) {
            log.error(exc);
            throw new Exception(exc);
        }
        if (mensaje.equals("error")) {
            mensaje = "Ocurrio un error al insertar el registro de ingreso.";
        } else {
            mensaje = "Registro de ingreso insertado correctamente.";
        }
        return mensaje;
    }
}
