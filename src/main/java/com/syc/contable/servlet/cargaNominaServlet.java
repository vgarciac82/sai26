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
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.apache.commons.fileupload2.core.FileItem;
import org.jfree.util.Log;
import com.syc.contable.CargaNominaBussinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import java.util.Base64;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;

@WebServlet(name = "cargaNominaServlet", urlPatterns = { "/gstnmngr/cargaNomina" })
public class cargaNominaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Caso caso = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        // String id_folioN= request.getParameter("id_caso");
        String folio = caso.getFolio();
        String id_folioN = folio.substring(folio.lastIndexOf("-") + 1);
        String pathURL = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + pathURL + "/";
        String mensaje;
        // String rutaTmpArchivo = cargarArchivoSCV(empleado, request,
        // response);
        InputStream in = cargarArchivoSCV(request, response);
        try {
            boolean actualizado = enviaRuta(in, caso, id_folioN);
            if (actualizado == true) {
                mensaje = "Archivo Cargado";
            } else {
                mensaje = "Registros no válidos. Favor de verificar...";
            }
        } finally {
            if (in != null)
                in.close();
            in = null;
        }
        response.sendRedirect(basePath + "Generador/FacturaNomina.jsp?mensaje=" + mensaje + "&id_folio=" + id_folioN);
    }

    @SuppressWarnings("unchecked")
    public InputStream cargarArchivoSCV(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String szPath = "";
        try {
            DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
            // Se construye un objeto para que parsee la petición
            JakartaServletFileUpload fu = new JakartaServletFileUpload(factory);
            // Tamaño máximo que aceptará el archivo
            // El tamaño no importa
            fu.setFileSizeMax(-1);
            // Si excede el 1 Gb en memoria lo
            fu.setSizeThreshold(1048576);
            // escribe a disco
            szPath = getServletContext().getRealPath("/upload/nomina");
            File file = new File(szPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            List<FileItem> fileItems = fu.parseRequest(request);
            Iterator<FileItem> i = fileItems.iterator();
            FileItem actual = null;
            while (i.hasNext()) {
                actual = i.next();
                if (actual.isFormField())
                    continue;
                return actual.getInputStream();
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
        throw new IOException("No se encontro archivo alguno en la peticion");
    }

    public static boolean enviaRuta(InputStream in, Caso caso, String id_folioN) throws FileNotFoundException {
        String claveCNA = "";
        boolean primeraLinea = true;
        List<String> mapa = new ArrayList<String>();
        mapa.add("H");
        CargaNominaBussinessLogic nominaBL = new CargaNominaBussinessLogic(GestionInterface.ATT_CONEXION);
        int id_caso = Integer.parseInt(id_folioN, 10);
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
                    // buscando en la tabla tCatalogoEP el campo nClaveCNA
                    claveCNA = nominaBL.buscaClaveCNA(EP);
                    if (!claveCNA.equals("")) {
                        // Insertando en tabla tNominaCargaArchivo
                        lineaInsert = nominaBL.insertaLineaNomina(id_caso, EP, claveInterna, concepto, movimiento, saldo, claveCNA);
                    }
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
