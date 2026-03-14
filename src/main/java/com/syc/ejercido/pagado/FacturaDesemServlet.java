package com.syc.ejercido.pagado;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "FacturaDesemServlet", urlPatterns = { "/gstnmngr/FacturaDesemServlet" })
public class FacturaDesemServlet extends HttpServlet {

    private static Logger log = LoggerFactory.getLogger(AplicacionContable.class);

    /**
     * Constructor of the object.
     */
    public FacturaDesemServlet() {
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
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
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
     * This method is called when a form has its tag value method equals to post.
     *
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
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
        String pathUrl = request.getContextPath();
        String pathBase = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + pathUrl + "/";
        String mensaje = "";
        String recibo = "";
        //	String valorSumar = "";
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
            //	java.util.Date utilDate = new java.util.Date();
            //	long lnMilisegundos = utilDate.getTime();
            //	java.sql.Date sqlDate = new java.sql.Date(lnMilisegundos);
            //	String [] fp = String.valueOf(sqlDate).split("-");
            //	String fCarga = fp[2]+"-"+fp[1]+"-"+fp[0];
            //String tipoArchivo = "cargaArchivo";
            String valor = "no_guardado";
            String tipoArchivo = fieldMap.get("tipoArchivo");
            InputStream in = fileList.get(0).getInputStream();
            //	InputStream ini = fileList.get(0).getInputStream();
            //CargaArchivosCapituloMil guardaArchivo = new CargaArchivosCapituloMil();
            FacturaCarga Carga = new FacturaCarga();
            mensaje = Carga.enviaRuta(in, tipoArchivo);
            if (valor.equals("aplicado") || valor.equals("guardado")) {
                mensaje = "Archivo Cargado Correctamente Con Numero: " + recibo;
            } else if (valor.equals("mayor")) {
                mensaje = "Importe Mayor Al Comprometido";
            } else if (valor.equals("cancelado")) {
                mensaje = "El Compromiso Esta Cancelado";
            } else if (valor.equals("sinFolioSicop")) {
                mensaje = "El Compromiso No Tiene Folio Sicop";
            } else if (valor.equals("sinAplicar")) {
                mensaje = "El Compromiso No Esta Aplicado";
            } else if (valor.equals("noExiste")) {
                mensaje = " No Existe Compromiso ";
            } else if (valor.equals("noExisteMovimiento")) {
                mensaje = mensajeError;
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error FacturaDesembolso: " + e);
            mensaje = "Error: No Se Guardaron Los Registros Correctamente";
        }
        response.sendRedirect(pathBase + "/Generador/FacturaCarga.jsp?mensaje=" + mensaje);
    }

    public List<FileItem> procesaArchivos(HttpServletRequest request) {
        String szPath;
        List<FileItem> fileItems = new ArrayList<FileItem>();
        try {
            // Se construye un objeto para que parsee la petición
            ServletFileUpload fu = new ServletFileUpload();
            // Tamaño máximo que aceptará el archivo
            // El tamaño no importa
            fu.setSizeMax(-1);
            // Si excede el 1 Gb en memoria lo
            fu.setSizeThreshold(1048576);
            // escribe a disco
            szPath = getServletContext().getRealPath("/upload/ejercidoPagado");
            File file = new File(szPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            fu.setRepositoryPath(szPath);
            fileItems = fu.parseRequest(request);
        } catch (Exception e) {
            log.error("Error occurred", "Error: " + e);
            System.out.println("Error de Aplicaci&oacute;n " + e.getMessage());
        }
        return fileItems;
    }

    public String compararTotales(InputStream inOrg) throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inOrg));
        String valorReturn = "menorIgual";
        String sCadenas = "";
        String caNoCompromiso = "";
        double importeDocTotalNomina = 0.00;
        double importeNetoTotalNominaComprometido = 0.00;
        double totalComprometido = 0.00;
        try {
            CargaArchivosCapituloMil guardaArchivo = new CargaArchivosCapituloMil();
            while ((sCadenas = br.readLine()) != null) {
                String[] celdas = sCadenas.split(",");
                caNoCompromiso = celdas[0].trim();
                String importeNeto = celdas[6].trim();
                /*
				valor = guardaArchivo.existeTipoMovimiento(tipoMovimiento);
				
				if(!valor.equals("existe")){ 
					break;
				}
				*/
                importeDocTotalNomina = Double.parseDouble(importeNeto) + importeDocTotalNomina;
            }
            //if(valor.equals("existe")){
            NumberFormat formatter = new DecimalFormat("###.##");
            importeNetoTotalNominaComprometido = guardaArchivo.sumarImporteNetoTotalCompromiso(caNoCompromiso);
            totalComprometido = guardaArchivo.sumarImporteTotalComprometido(caNoCompromiso);
            double totalNomina = importeNetoTotalNominaComprometido + importeDocTotalNomina;
            totalComprometido = Double.parseDouble(formatter.format(totalComprometido));
            totalNomina = Double.parseDouble(formatter.format(totalNomina));
            System.out.println("nomina: " + totalNomina + " / " + " compromiso: " + totalComprometido);
            if (totalNomina > totalComprometido) {
                valorReturn = "mayor";
            }
            /*}else{
				
				valorReturn = valor;
				
			}*/
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

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException if an error occurs
     */
    public void init() throws ServletException {
        // Put your code here
    }
}
