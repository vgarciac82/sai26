package com.syc.ejercido.pagado;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.apache.commons.fileupload2.core.FileItem;
import org.jfree.util.Log;
import com.syc.contable.core.AplicacionContable;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;

@WebServlet(name = "SubirArchivosCapituloMilServlet", urlPatterns = { "/gstnmngr/SubirArchivosCapituloMil" })
public class SubirArchivosCapituloMilServlet extends HttpServlet implements GestionInterface {

    /**
     */
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(AplicacionContable.class);

    /**
     * Constructor of the object.
     */
    public SubirArchivosCapituloMilServlet() {
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
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        out.println("<HTML>");
        out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
        out.println("  <BODY>");
        out.print("    This is ");
        out.print(this.getClass());
        out.println(", using the GET method");
        out.println("  </BODY>");
        out.println("</HTML>");
        out.flush();
        out.close();
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
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        List<FileItem> fileItems = new ArrayList<FileItem>();
        Map<String, String> fieldMap = new Hashtable<String, String>();
        List<FileItem> fileList = new ArrayList<FileItem>();
        //String pathUrl = request.getContextPath();
        //String pathBase = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + pathUrl + "/";
        String mensaje = "";
        String recibo = "";
        String valorSumar = "";
        String mensajeError = "";
        try {
            fileItems = procesaArchivos(request);
            for (FileItem item : fileItems) {
                if (item.isFormField()) {
                    fieldMap.put(item.getFieldName(), item.getString());
                } else {
                    fileList.add(item);
                }
            }
            java.util.Date utilDate = new java.util.Date();
            long lnMilisegundos = utilDate.getTime();
            java.sql.Date sqlDate = new java.sql.Date(lnMilisegundos);
            String[] fp = String.valueOf(sqlDate).split("-");
            String fCarga = fp[2] + "-" + fp[1] + "-" + fp[0];
            String tipoArchivo = fieldMap.get("tipoArchivo");
            String valor = "no_guardado";
            InputStream in = fileList.get(0).getInputStream();
            InputStream ini = fileList.get(0).getInputStream();
            String fAplicacion = fieldMap.get("fAplicacion");
            String[] fA = fAplicacion.split("/");
            String fAplicacion2 = fA[2] + "-" + fA[1] + "-" + fA[0];
            String fRecepcion = fA[0] + "-" + fA[1] + "-" + fA[2];
            String cUnidadResponsable = fieldMap.get("cUnidadResponsable");
            String aEjercicioFiscal = fieldMap.get("aEjercicioFiscal");
            String login = fieldMap.get("login");
            String cCentroContable = fieldMap.get("cCentroContable");
            String nMesCxp = fA[1];
            String nMes = fp[1];
            String cRamo = fieldMap.get("cRamo");
            String cUnidadResponsableContable = "RHQ";
            String idCaso = fieldMap.get("idCaso");
            String nFolioNOMINA = "";
            String compromisoAmpliado = fieldMap.get("compromisoAmpliado");
            String esAmpliacionReduccion = fieldMap.get("tipoAmplRed");
            if ("reducir".equals(esAmpliacionReduccion))
                esAmpliacionReduccion = "R";
            else if ("ampliar".equals(esAmpliacionReduccion))
                esAmpliacionReduccion = "A";
            else
                esAmpliacionReduccion = "";
            CargaArchivosCapituloMil guardaArchivo = new CargaArchivosCapituloMil();
            /*FAV20171019 Se guarda en base el prefijo de CxP*/
            //ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic( GestionInterface.ATT_CONEXION );
            //String cxpPrefijo = cabl.getSystemSetting("CXP_PREFIJO") == null?"CP":cabl.getSystemSetting("CXP_PREFIJO");
            if (tipoArchivo.equals("docComprometido")) {
                String nFolioCompromisoNomina = fieldMap.get("nFolioCompromisoNomina");
                //Nombre Del Archivo
                String cIdContrat = fieldMap.get("cIdContrato");
                int count = cIdContrat.length();
                String cIdContrato = cIdContrat.substring(1, count);
                //String flComprometido = fieldMap.get("flComprometido");
                String cTipoContrato = "";
                String caNoCompromiso = fieldMap.get("caNoCompromiso");
                recibo = caNoCompromiso;
                String cDescripcionPoliza = "PAGO DEL COMPROMISO " + fAplicacion2;
                valor = guardaArchivo.enviaRuta(in, tipoArchivo, nFolioCompromisoNomina, fCarga, cIdContrato, cTipoContrato, fAplicacion2, cCentroContable, cRamo, cUnidadResponsable, caNoCompromiso, nMes, aEjercicioFiscal, cUnidadResponsableContable, cDescripcionPoliza, idCaso, login, "", "", compromisoAmpliado, esAmpliacionReduccion);
            } else {
                nFolioNOMINA = fieldMap.get("nFolioNOMINA");
                String cIdRelacion = "NOMI-" + cUnidadResponsable + "-" + nFolioNOMINA;
                String caNoContrarreciboNomina = fieldMap.get("caNoContrarreciboNomina");
                recibo = caNoContrarreciboNomina;
                int index = -1;
                int index2 = -1;
                int index3 = -1;
                valorSumar = compararTotales(in);
                if (valorSumar.equals("menorIgual")) {
                    String buscarSequence = "CR-" + cCentroContable;
                    String recibo_ini = cCentroContable + "CP" + aEjercicioFiscal;
                    valor = guardaArchivo.enviaRuta(ini, tipoArchivo, nFolioNOMINA, fCarga, cIdRelacion, "cTipoContrato", fAplicacion2, cCentroContable, cRamo, cUnidadResponsable, caNoContrarreciboNomina, nMesCxp, aEjercicioFiscal, cUnidadResponsableContable, idCaso, fRecepcion, login, recibo_ini, buscarSequence, compromisoAmpliado, esAmpliacionReduccion);
                    System.out.println("valor:: " + valor);
                    String[] cad = valor.split("/");
                    index = valor.indexOf("NO EXISTE MOVIMIENTO:");
                    index2 = valor.indexOf("ERROR");
                    index3 = valor.indexOf("NORFC");
                    if (index > -1) {
                        valor = "noExisteMovimiento";
                        mensajeError = cad[0] + "/" + cad[2];
                    } else if (index2 > -1) {
                        valor = "noSuficiencia";
                        mensajeError = "No Hay Saldo Suficiente:" + cad[1] + "/" + cad[2];
                    } else if (index3 > -1) {
                        valor = "noRFC";
                        mensajeError = "El RFC " + cad[1] + " no existe.";
                    }
                } else {
                    // mayor
                    valor = valorSumar;
                }
            }
            if (valor.equals("aplicado") || valor.equals("guardado")) {
                if (valor.equals("guardado")) {
                    recibo = guardaArchivo.muestraContraRecibo(nFolioNOMINA);
                }
                mensaje = "Archivo Cargado Correctamente Con Numero: " + recibo + "/";
            } else {
                // se mueve el codigo a una funcion.
                mensaje = regresaMensaje(valor, mensajeError);
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error: " + e);
            mensaje = "Error: No Se Guardaron Los Registros Correctamente / ";
        }
        response.sendRedirect("../Generador/capituloMil.jsp?mensaje=" + mensaje);
    }

    public List<FileItem> procesaArchivos(HttpServletRequest request) {
        String szPath;
        List<FileItem> fileItems = new ArrayList<FileItem>();
        try {
            DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
            // Se construye un objeto para que parsee la peticiÃ³n
            JakartaServletFileUpload fu = new JakartaServletFileUpload(factory);
            // Tamaño máximo que aceptará el archivo
            // El tamaño no importa
            fu.setFileSizeMax(-1);
            // Si excede el 1 Gb en memoria lo
            fu.setSizeThreshold(1048576);
            // escribe a disco
            szPath = getServletContext().getRealPath("/upload/ejercidoPagado");
            File file = new File(szPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            fileItems = fu.parseRequest(request);
        } catch (Exception e) {
            log.error("Error occurred", "Error: " + e);
            System.out.println("Error de Aplicación " + e.getMessage());
        }
        return fileItems;
    }

    public String compararTotales(InputStream inOrg) throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inOrg));
        String valorReturn = "menorIgual";
        String sCadenas = "";
        String caNoCompromiso = "";
        BigDecimal importeDocTotalNomina = new BigDecimal(0);
        BigDecimal importeNetoTotalNominaComprometido = new BigDecimal(0);
        BigDecimal totalComprometido = new BigDecimal(0);
        String valor = "";
        try {
            CargaArchivosCapituloMil guardaArchivo = new CargaArchivosCapituloMil();
            while ((sCadenas = br.readLine()) != null) {
                String[] celdas = sCadenas.split(",");
                caNoCompromiso = celdas[0].trim();
                //String tipoMovimiento = celdas[3].trim();
                String importeNeto = celdas[6].trim();
                importeDocTotalNomina = importeDocTotalNomina.add(new BigDecimal(importeNeto).setScale(2));
            }
            valor = guardaArchivo.existeCompromiso(caNoCompromiso);
            if (valor.equals("existe")) {
                importeNetoTotalNominaComprometido = guardaArchivo.sumarImporteNetoTotalCompromisoBig(caNoCompromiso);
                totalComprometido = guardaArchivo.sumarImporteTotalComprometidoBig(caNoCompromiso);
                BigDecimal totalNomina = new BigDecimal(0);
                totalNomina = (totalNomina.add(importeNetoTotalNominaComprometido)).setScale(2, RoundingMode.HALF_UP);
                totalNomina = (totalNomina.add(importeDocTotalNomina)).setScale(2, RoundingMode.HALF_UP);
                log.info("Object: {}", "nomina: " + totalNomina + " / " + " compromiso: " + totalComprometido);
                if ((totalNomina.compareTo(totalComprometido)) == 1) {
                    valorReturn = "mayor";
                }
            } else {
                valorReturn = valor;
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error: " + e);
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (Exception exc) {
                Log.warn("Cerrando BufferedReader", exc);
            }
            br = null;
        }
        return valorReturn;
    }

    public String regresaMensaje(String valor, String mensajeError) {
        String mensaje = "";
        if (valor.equals("mayor")) {
            mensaje = "Importe Mayor Al Comprometido /";
        } else if (valor.equals("cancelado")) {
            mensaje = "El Compromiso Esta Cancelado /";
        } else if (valor.equals("sinFolioSicop")) {
            mensaje = "El Compromiso No Tiene Folio Sicop /";
        } else if (valor.equals("sinAplicar")) {
            mensaje = "El Compromiso No Esta Aplicado /";
        } else if (valor.equals("noExiste")) {
            mensaje = " No Existe Compromiso /";
        } else if (valor.equals("noExisteMovimiento")) {
            mensaje = mensajeError;
        } else if (valor.equals("noSuficiencia")) {
            mensaje = mensajeError;
        } else if (valor.equals("noRFC")) {
            mensaje = mensajeError;
        } else {
            mensaje = " Error: No Se Guardaron Los Registros Correctamente\\n" + valor;
        }
        return mensaje;
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException if an error occurs
     */
    public void init() throws ServletException {
        // Put your code here
    }
}
