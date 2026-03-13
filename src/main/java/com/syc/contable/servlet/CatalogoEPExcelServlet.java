package com.syc.contable.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
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
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Row;
import com.syc.contable.EstructuraProgramaticaBusinessLogic;
import com.syc.gestion.servlet.GestionFileReceiverServlet;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "CatalogoEPExcelServlet", urlPatterns = { "/gstnmngr/CatalogoEPExcel" })
public class CatalogoEPExcelServlet extends HttpServlet {

    /**
     * Constructor of the object.
     */
    public CatalogoEPExcelServlet() {
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

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String a;
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
        HttpSession session = request.getSession(true);
        if (request.getParameter("accion") == null) {
            String pathURL = request.getContextPath();
            String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + pathURL + "/";
            String path = "";
            DiskFileUpload fu = new DiskFileUpload();
            fu.setSizeMax(-1);
            Long date = System.currentTimeMillis();
            SimpleDateFormat fecha = new SimpleDateFormat("yyyyMMddhhmmssS");
            String sufijo = fecha.format(date);
            path = request.getRealPath("/upload/");
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            fu.setRepositoryPath(path);
            List<FileItem> fileItems = new ArrayList<FileItem>();
            Map<String, String> fieldMap = new Hashtable<String, String>();
            List<FileItem> fileList = new ArrayList<FileItem>();
            String pestana = "";
            try {
                fileItems = fu.parseRequest(request);
                if (fileItems == null) {
                    return;
                }
                for (FileItem item : fileItems) {
                    if (item.isFormField()) {
                        pestana = item.getString();
                    } else {
                        fileList.add(item);
                    }
                }
                Iterator i = fileList.iterator();
                FileItem actual = null;
                while (i.hasNext()) {
                    actual = (FileItem) i.next();
                    String fileName = "CatalogoEP.xls";
                    File archivo = new File(fu.getRepositoryPath() + "\\" + fileName);
                    actual.write(archivo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String tipo = request.getParameter("tipo");
            ArrayList<String> un = null;
            PrintWriter out = response.getWriter();
            try {
                String archivo = request.getParameter("archivoE");
                un = cargaExcel(archivo, tipo);
                session.setAttribute("objcArrayEP", un);
                pestana = "SI";
                response.sendRedirect(basePath + "plantillasCasos/catalogoEstructuraProgramatica.jsp?epError=" + pestana);
            } catch (SQLException se) {
                se.printStackTrace();
            } catch (OfficeXmlFileException offex) {
                response.sendRedirect(basePath + "plantillasCasos/catalogoEstructuraProgramatica.jsp?epError=SI&cErrorFile=Se requiere Archivo excel 97-2003");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }
    }

    private String replaceAccentsUTF8(String cadena) {
        String ret = "";
        if (cadena.contains("á") || cadena.contains("é") || cadena.contains("í") || cadena.contains("ó") || cadena.contains("ú")) {
            cadena = cadena.replace("á", "&#225;");
            cadena = cadena.replace("é", "&#233;");
            cadena = cadena.replace("í", "&#237;");
            cadena = cadena.replace("ó", "&#243;");
            cadena = cadena.replace("ú", "&#250;");
        }
        ret = cadena;
        return ret;
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException
     *             if an error occurs
     */
    public void init() throws ServletException {
        // Put your code here
    }

    private static Logger log = Logger.getLogger(GestionFileReceiverServlet.class);

    private String jniName = null;

    private String tempDir = null;

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
        tempDir = config.getInitParameter("tempDir");
        if (tempDir == null) {
            tempDir = config.getServletContext().getRealPath("/") + File.separator + "upload" + File.separator;
            File fDir = new File(tempDir);
            if (!fDir.exists())
                if (!fDir.mkdirs())
                    throw new ServletException("No se pudo crear el directorio " + tempDir);
        }
    }

    public ArrayList<String> cargaExcel(String archivo, String tipo) throws Exception {
        DiskFileUpload upload = new DiskFileUpload();
        upload.setRepositoryPath(tempDir);
        String cFileExcel = upload.getRepositoryPath() + "/CatalogoEP.xls";
        ArrayList<String> validaEP = new ArrayList<String>();
        ArrayList<String> inValidaEP = new ArrayList<String>();
        ArrayList arrMResult = new ArrayList();
        EstructuraProgramaticaBusinessLogic estrProgaBinss = new EstructuraProgramaticaBusinessLogic(jniName);
        //AnteProyectoBusinessLogic apBL = new AnteProyectoBusinessLogic("");
        try {
            InputStream inp = new FileInputStream(cFileExcel);
            HSSFWorkbook wb = new HSSFWorkbook(inp);
            HSSFSheet sheet = wb.getSheetAt(0);
            Iterator<Row> rowIter = sheet.rowIterator();
            HSSFRow row = (HSSFRow) rowIter.next();
            String cSubCuenta = "EPA";
            String aEjercicioFiscal = "";
            String cEjercicoFiscal = estrProgaBinss.obtenEjercicioFiscal();
            int nEjercisioFiscal = new Integer(cEjercicoFiscal).intValue();
            if ("EPA".equals(cSubCuenta)) {
                nEjercisioFiscal++;
            }
            int i = 1;
            while (rowIter.hasNext()) {
                row = (HSSFRow) rowIter.next();
                HSSFCell cell0 = (HSSFCell) row.getCell(0);
                HSSFCell cell1 = (HSSFCell) row.getCell(1);
                String valcell1 = "";
                valcell1 = cell0.getStringCellValue();
                arrMResult = estrProgaBinss.validaClaveEP(valcell1, i, cSubCuenta);
                i++;
                if (arrMResult.get(0).toString().length() < 10) {
                    log.debug("Sin Error la EP:" + valcell1 + " de la Secuancia del ARchiv XLS:" + i);
                    validaEP.add(valcell1);
                    estrProgaBinss.InsertaEP(valcell1, aEjercicioFiscal, cSubCuenta);
                } else {
                    //estrProgaBinss.InsertaERROREP(arrMResult);
                    log.debug("Con ERROR la EP:" + valcell1 + " de la Secuancia del ARchiv XLS:" + i);
                    inValidaEP.addAll(arrMResult);
                }
                arrMResult = null;
            }
            wb.close();
            // Hacemos los deletes e inserts a la base de datos preguntando
            // si ya existe el registro
            // *****************************************
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inValidaEP;
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
            szPath = getServletContext().getRealPath("/upload/ante");
            File file = new File(szPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            fu.setRepositoryPath(szPath);
            fileItems = fu.parseRequest(request);
        } catch (Exception e) {
        }
        return fileItems;
    }
}
