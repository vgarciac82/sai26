package com.syc.contable.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.jfree.util.Log;
import com.syc.contable.CargaNominaBussinessLogic;
import com.syc.contable.SaldosBussinessLogic;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "SaldosInicialesServlet", urlPatterns = { "/gstnmngr/SaldosInicialesServlet" })
public class SaldosInicialesServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger log = Logger.getLogger(RespuestaCompromisosServlet.class);

    private static String mensaje = "";

    public static InputStream in;

    void depura(String cadena) {
        System.out.println("mensaje: " + cadena);
    }

    @SuppressWarnings("unchecked")
    public List<FileItem> procesaArchivos(HttpServletRequest request) {
        String szPath;
        List<FileItem> fileItems = new ArrayList<FileItem>();
        try {
            // Se construye un objeto para que parsee la petición
            DiskFileUpload fu = new DiskFileUpload();
            // Tamaño máximo que aceptará el archivo
            // El tamaño no importa
            fu.setSizeMax(-1);
            // Si excede el 1 Gb en memoria lo
            fu.setSizeThreshold(1048576);
            // escribe a disco
            szPath = getServletContext().getRealPath("/upload/compromisos");
            File file = new File(szPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            fu.setRepositoryPath(szPath);
            fileItems = fu.parseRequest(request);
        } catch (Exception e) {
            depura("Error de Aplicación " + e.getMessage());
        }
        return fileItems;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        List<FileItem> fileItems = new ArrayList<FileItem>();
        String pathURL = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + pathURL + "/";
        SaldosBussinessLogic saldos = new SaldosBussinessLogic(GestionInterface.ATT_CONEXION);
        Map<String, String> fieldMap = new Hashtable<String, String>();
        List<FileItem> fileList = new ArrayList<FileItem>();
        StringBuffer sb = new StringBuffer();
        try {
            fileItems = procesaArchivos(request);
            for (FileItem item : fileItems) {
                if (item.isFormField()) {
                    fieldMap.put(item.getFieldName(), item.getString());
                } else {
                    fileList.add(item);
                }
            }
            if (fileList.isEmpty())
                throw new ServletException("No se recibio ning\u00fan archivo");
            InputStream in = fileList.get(0).getInputStream();
            List<String> archivoL = saldos.archivoLeido(in);
            session.setAttribute("lista", archivoL);
            mensaje = "Desplegando archivo leido";
            response.sendRedirect(basePath + "Generador/CompromisosDevueltos.jsp?mensaje=" + mensaje);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e);
        }
        // Aqui se hace el armado del StringBufer de los datos a insertar
        sb = cargarArchivoXLS(request, response);
        if (sb.length() != 0) {
            try {
                boolean actualizado = saldos.insertaSaldos(sb);
                if (actualizado == true) {
                    mensaje = "Archivo validado";
                } else {
                    mensaje = "No se insertaron todos los registros. Favor de verificar...";
                }
                response.sendRedirect(basePath + "Generador/CompromisosDevueltos.jsp?mensaje=" + mensaje);
            } catch (Exception e) {
                e.printStackTrace();
                log.info(e);
            } finally {
                if (sb != null)
                    sb = null;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public StringBuffer cargarArchivoXLS(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String szPath = "";
        try {
            // Se construye un objeto para que parsee la petición
            DiskFileUpload fu = new DiskFileUpload();
            // Tamaño máximo que aceptará el archivo
            // El tamaño no importa
            fu.setSizeMax(-1);
            // Si excede el 1 Gb en memoria lo
            fu.setSizeThreshold(1048576);
            // escribe a disco
            szPath = getServletContext().getRealPath("/upload/saldos");
            File file = new File(szPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            fu.setRepositoryPath(szPath);
            List<FileItem> fileItems = fu.parseRequest(request);
            Iterator<FileItem> i = fileItems.iterator();
            FileItem actual = null;
            while (i.hasNext()) {
                actual = i.next();
                if (actual.isFormField())
                    continue;
                //				return actual.getInputStream();
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
        throw new IOException("No se encontro archivo alguno en la peticion");
    }

    public static boolean enviaRuta(InputStream in) throws FileNotFoundException {
        boolean primeraLinea = true;
        List<String> mapa = new ArrayList<String>();
        mapa.add("H");
        CargaNominaBussinessLogic nominaBL = new CargaNominaBussinessLogic(GestionInterface.ATT_CONEXION);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String sCadena = "";
        Hashtable<String, Integer> nomColumna = new Hashtable<String, Integer>();
        boolean lineaInsert = false;
        try {
            while ((sCadena = br.readLine()) != null) {
                String[] llaves = sCadena.split(",");
                if (primeraLinea) {
                    for (int j = 0; j < llaves.length; j++) {
                        if (mapa.contains(llaves[j])) {
                            nomColumna.put(new String(llaves[j]), new Integer(j));
                        }
                    }
                    primeraLinea = false;
                } else {
                    int nInicio = 0;
                    int nFin = 0;
                    if (sCadena.indexOf('"') > -1)
                        nInicio = sCadena.indexOf('"');
                    if (sCadena.indexOf('"', nInicio + 1) > -1)
                        nFin = sCadena.indexOf('"', nInicio + 1);
                    if ((nInicio == 0) && (nFin == 0)) {
                    } else {
                        String sParte1 = sCadena.substring(0, nInicio);
                        String sParte2 = sCadena.substring(nInicio + 1, nFin);
                        String sParte3 = sCadena.substring(nFin + 1);
                        sParte2 = sParte2.replace(",", "");
                        sCadena = sParte1 + sParte2 + sParte3;
                    }
                    String[] celdas = sCadena.split(",");
                    String tmp1 = celdas[19].trim();
                    tmp1 = tmp1.substring(7, tmp1.length());
                    String tmp2 = "B" + celdas[20];
                    String claveInterna = tmp1 + "." + tmp2;
                    String EP = celdas[4] + "." + celdas[2] + "." + celdas[3] + "." + celdas[5] + "." + celdas[6] + "." + celdas[7] + "." + celdas[8] + "." + celdas[9] + "." + celdas[10] + "." + celdas[11] + celdas[12] + celdas[13] + celdas[14] + "." + celdas[15] + "." + celdas[16] + "." + celdas[17] + "." + celdas[18] + "." + claveInterna;
                    String saldoStr = celdas[27];
                    Double saldo = Double.valueOf(saldoStr).doubleValue();
                    String concepto = celdas[29];
                    String movimiento = celdas[30];
                }
            }
        } catch (Exception se) {
            se.printStackTrace();
            return false;
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (Exception exc) {
                Log.warn("Cerrando BufferedReader", exc);
            }
            br = null;
        }
        return lineaInsert;
    }
}
