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
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import com.syc.contable.AnteProyectoBusinessLogic;
import com.syc.gestion.servlet.GestionFileReceiverServlet;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "TechosAnteProyectoExcel", urlPatterns = { "/gstnmngr/TechosExcel" })
public class TechosAnteProyectoExcel extends HttpServlet {

    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor of the object.
     */
    public TechosAnteProyectoExcel() {
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
        HttpSession session = request.getSession(false);
        if (request.getParameter("accion") == null) {
            String pathURL = request.getContextPath();
            String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + pathURL + "/";
            String path = "";
            DiskFileUpload fu = new DiskFileUpload();
            // tamaño máximo que aceptará el archivo
            fu.setSizeMax(-1);
            Long date = System.currentTimeMillis();
            SimpleDateFormat fecha = new SimpleDateFormat("yyyyMMddhhmmssS");
            String sufijo = fecha.format(date);
            // String path = req.getRealPath("/upload/"+
            // req.getSession().getAttribute(empleado.getClaveUsuario()));
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
                    String fileName = "TechosAnteProyecto.xls";
                    File archivo = new File(fu.getRepositoryPath() + "\\" + fileName);
                    actual.write(archivo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.sendRedirect(basePath + "plantillasCasos/TechosAnteProyecto.jsp?pes=" + pestana);
        } else {
            AnteProyectoBusinessLogic apBL = new AnteProyectoBusinessLogic("");
            if (request.getParameter("accion").equals("deletePP")) {
                try {
                    apBL.borraTecho(request.getParameter("filtro"), request.getParameter("tabla"), "cProgramaPresupuestario", null, null, null, null);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (request.getParameter("accion").equals("deleteEn")) {
                try {
                    apBL.borraTecho(request.getParameter("filtro"), request.getParameter("tabla"), "cEntidadFederativa", null, null, null, null);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (request.getParameter("accion").equals("deleteUE")) {
                try {
                    apBL.borraTecho(request.getParameter("filtro"), request.getParameter("tabla"), "cUnidadResponsable", null, null, null, null);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (request.getParameter("accion").equals("deleteUN")) {
                try {
                    apBL.borraTecho(request.getParameter("filtro"), request.getParameter("tabla"), "cUnidadNormativa", null, null, null, null);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (request.getParameter("accion").equals("deleteRet")) {
                try {
                    apBL.borraTecho(request.getParameter("filtro"), request.getParameter("tabla"), "cPartida", "cTipoGasto", request.getParameter("filtro2"), null, null);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (request.getParameter("accion").equals("deletePUE")) {
                try {
                    apBL.borraTecho(request.getParameter("filtro"), request.getParameter("tabla"), "cPartida", "cTipoGasto", request.getParameter("filtro2"), "cUnidadResponsable", request.getParameter("filtro3"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (request.getParameter("accion").equals("deletePPUE")) {
                try {
                    apBL.borraTecho(request.getParameter("filtro"), request.getParameter("tabla"), "cProgramaPresupuestario", "cUnidadResponsable", request.getParameter("filtro2"), null, null);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (request.getParameter("accion").equals("deletePUN")) {
                try {
                    apBL.borraTechoUN(request.getParameter("filtro"), request.getParameter("tabla"), "cPartida", "cTipoGasto", request.getParameter("filtro2"), "cUnidadNormativa", request.getParameter("filtro3"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                String tipo = request.getParameter("tipo");
                ArrayList<String> un = null;
                PrintWriter out = response.getWriter();
                try {
                    String archivo = request.getParameter("archivoE");
                    un = cargaExcel(archivo, tipo);
                } catch (SQLException se) {
                    se.printStackTrace();
                }
                if (un != null && !tipo.equals("ret") && !tipo.equals("partidaue") && !tipo.equals("progprue") && !tipo.equals("partidaun")) {
                    String mensaje = "";
                    String val = "";
                    if (tipo.equals("un")) {
                        if (un.get(0) != null && un.get(0).equals("error")) {
                            mensaje = un.get(1);
                        }
                    }
                    if (mensaje.trim().length() > 1) {
                        val = "{\"techos\":[{\"mensaje\":\"" + mensaje + "\"}]}";
                    } else {
                        Iterator<String> iterator = un.iterator();
                        val = "{\"techos\": [";
                        int i = 0;
                        while (iterator.hasNext()) {
                            val += "{\"clave\":" + "\"" + iterator.next() + "\",";
                            val += "\"descripcion\":" + "\"" + replaceAccentsUTF8(iterator.next().trim()) + "\",";
                            val += "\"techo" + "\":" + iterator.next() + "},";
                            i++;
                        }
                        val = val.substring(0, val.length() - 1);
                        val += "]}";
                    }
                    out.println(val);
                } else if (tipo.equals("partidaue")) {
                    Iterator<String> iterator = un.iterator();
                    String val = "{\"techos\": [";
                    int i = 0;
                    while (iterator.hasNext()) {
                        val += "{\"clave1\":" + "\"" + iterator.next() + "\",";
                        val += "\"descripcion1\":" + "\"" + replaceAccentsUTF8(iterator.next().trim()) + "\",";
                        val += "\"clave2\":" + "\"" + iterator.next() + "\",";
                        val += "\"descripcion2\":" + "\"" + replaceAccentsUTF8(iterator.next().trim()) + "\",";
                        val += "\"clave3\":" + "\"" + iterator.next() + "\",";
                        val += "\"descripcion3\":" + "\"" + replaceAccentsUTF8(iterator.next().trim()) + "\",";
                        val += "\"techo" + "\":" + iterator.next() + "},";
                        i++;
                    }
                    val = val.substring(0, val.length() - 1);
                    val += "]}";
                    out.println(val);
                } else if (tipo.equals("partidaun")) {
                    Iterator<String> iterator = un.iterator();
                    String val = "{\"techos\": [";
                    int i = 0;
                    while (iterator.hasNext()) {
                        val += "{\"clave1\":" + "\"" + iterator.next() + "\",";
                        val += "\"descripcion1\":" + "\"" + replaceAccentsUTF8(iterator.next().trim()) + "\",";
                        val += "\"clave2\":" + "\"" + iterator.next() + "\",";
                        val += "\"descripcion2\":" + "\"" + replaceAccentsUTF8(iterator.next().trim()) + "\",";
                        val += "\"clave3\":" + "\"" + iterator.next() + "\",";
                        val += "\"descripcion3\":" + "\"" + replaceAccentsUTF8(iterator.next().trim()) + "\",";
                        val += "\"techo" + "\":" + iterator.next() + "},";
                        i++;
                    }
                    val = val.substring(0, val.length() - 1);
                    val += "]}";
                    out.println(val);
                } else {
                    Iterator<String> iterator = un.iterator();
                    String val = "{\"techos\": [";
                    int i = 0;
                    while (iterator.hasNext()) {
                        val += "{\"clave1\":" + "\"" + iterator.next() + "\",";
                        val += "\"descripcion1\":" + "\"" + replaceAccentsUTF8(iterator.next().trim()) + "\",";
                        val += "\"clave2\":" + "\"" + iterator.next() + "\",";
                        val += "\"descripcion2\":" + "\"" + replaceAccentsUTF8(iterator.next().trim()) + "\",";
                        val += "\"techo" + "\":" + iterator.next() + "},";
                        i++;
                    }
                    val = val.substring(0, val.length() - 1);
                    val += "]}";
                    out.println(val);
                }
                out.flush();
                out.close();
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

    public ArrayList<String> cargaExcel(String archivo, String tipo) throws SQLException {
        DiskFileUpload upload = new DiskFileUpload();
        upload.setRepositoryPath(tempDir);
        String cFileExcel = upload.getRepositoryPath() + "/TechosAnteProyecto.xls";
        ArrayList<String> un = new ArrayList<String>();
        AnteProyectoBusinessLogic apBL = new AnteProyectoBusinessLogic("");
        try {
            InputStream inp = new FileInputStream(cFileExcel);
            HSSFWorkbook wb = new HSSFWorkbook(inp);
            HSSFSheet sheet = wb.getSheetAt(0);
            Iterator<Row> rowIter = sheet.rowIterator();
            HSSFRow row = (HSSFRow) rowIter.next();
            while (rowIter.hasNext()) {
                row = (HSSFRow) rowIter.next();
                HSSFCell cell0 = (HSSFCell) row.getCell(0);
                HSSFCell cell1 = (HSSFCell) row.getCell(1);
                String valcell1 = "";
                double valcell2 = 0;
                // Se guarda el primer valor preguntando de que tipo es y se
                // checa si es unidadNormativa que efectivamente sea Normativa y
                // no ejecutora
                if (cell0.getCellType() == CellType.NUMERIC)
                    valcell1 = String.valueOf(new Double(cell0.getNumericCellValue()).intValue());
                else
                    valcell1 = cell0.getStringCellValue();
                if (tipo.equals("un")) {
                    boolean esNormativa = apBL.isUNormativa(valcell1);
                    if (!esNormativa) {
                        un.clear();
                        un.add("error");
                        un.add("El archivo contenia una unidad normativa no valida, se insertaron las que estaban correctas");
                        break;
                    }
                }
                un.add(valcell1);
                // *************************************************
                // Obtenemos las descripciones, en la normativa y ejecutra se
                // obtiene igual, por eso el "or"
                if (tipo.equals("un") || tipo.equals("ue")) {
                    un.add(apBL.getDescripcionClave(cell0.getStringCellValue()));
                    // agregamos el 3er valor después de haber obtenido la
                    // descripción
                    un.add(String.valueOf(cell1.getNumericCellValue()));
                } else // *************************************
                // Obtenemos la entidad federativa dada una clave
                if (tipo.equals("entidad")) {
                    if (cell0.getCellType() == CellType.NUMERIC)
                        un.add(apBL.getEntidadFederativaClave(String.valueOf(new Double(cell0.getNumericCellValue()).intValue())));
                    else
                        un.add(apBL.getEntidadFederativaClave(cell0.getStringCellValue()));
                    // agregamos el 3er valor después de haber obtenido la
                    // descripción
                    un.add(String.valueOf(cell1.getNumericCellValue()));
                } else // ****************************************
                // Obtenemos la descripcion del programaPresupuestario
                if (tipo.equals("programap")) {
                    un.add(apBL.getProgramaPresupuestario(cell0.getStringCellValue()));
                    // agregamos el 3er valor después de haber obtenido la
                    // descripción
                    un.add(String.valueOf(cell1.getNumericCellValue()));
                } else // *****************************************
                if (tipo.equals("ret")) {
                    HSSFCell cell2 = (HSSFCell) row.getCell(2);
                    valcell2 = cell2.getNumericCellValue();
                    if (cell0.getCellType() == CellType.NUMERIC)
                        un.add(apBL.getPartidaCatalogo(String.valueOf(new Double(cell0.getNumericCellValue()).intValue())));
                    else
                        un.add(apBL.getPartidaCatalogo(cell0.getStringCellValue()));
                    // Insertamos el segundo valor del excel y buscamos su
                    // descripcion
                    if (cell1.getCellType() == CellType.NUMERIC) {
                        un.add(String.valueOf(new Double(cell1.getNumericCellValue()).intValue()));
                        un.add(apBL.getTipoGasto(String.valueOf(new Double(cell1.getNumericCellValue()).intValue())));
                    } else {
                        un.add(cell1.getStringCellValue());
                        un.add(apBL.getTipoGasto(cell1.getStringCellValue()));
                    }
                    // Insertamos el último que sería el monto
                    un.add(String.valueOf(cell2.getNumericCellValue()));
                } else if (tipo.equals("progprue")) {
                    HSSFCell cell2 = (HSSFCell) row.getCell(2);
                    valcell2 = cell2.getNumericCellValue();
                    if (cell0.getCellType() == CellType.NUMERIC)
                        un.add(apBL.getProgramaPresupuestario(String.valueOf(new Double(cell0.getNumericCellValue()).intValue())));
                    else
                        un.add(apBL.getProgramaPresupuestario(cell0.getStringCellValue()));
                    // Insertamos el segundo valor del excel y buscamos su
                    // descripcion
                    if (cell1.getCellType() == CellType.NUMERIC) {
                        un.add(String.valueOf(new Double(cell1.getNumericCellValue()).intValue()));
                        un.add(apBL.getUnidadResponsable(String.valueOf(new Double(cell1.getNumericCellValue()).intValue())));
                    } else {
                        un.add(cell1.getStringCellValue());
                        un.add(apBL.getUnidadResponsable(cell1.getStringCellValue()));
                    }
                    // Insertamos el último que sería el monto
                    un.add(String.valueOf(cell2.getNumericCellValue()));
                } else if (tipo.equals("partidaue")) {
                    HSSFCell cell2 = (HSSFCell) row.getCell(2);
                    HSSFCell cell3 = (HSSFCell) row.getCell(3);
                    valcell2 = cell3.getNumericCellValue();
                    if (cell0.getCellType() == CellType.NUMERIC)
                        un.add(apBL.getPartidaCatalogo(String.valueOf(new Double(cell0.getNumericCellValue()).intValue())));
                    else
                        un.add(apBL.getPartidaCatalogo(cell0.getStringCellValue()));
                    // Insertamos el segundo valor del excel y buscamos su
                    // descripcion
                    if (cell1.getCellType() == CellType.NUMERIC) {
                        un.add(String.valueOf(new Double(cell1.getNumericCellValue()).intValue()));
                        un.add(apBL.getTipoGasto(String.valueOf(new Double(cell1.getNumericCellValue()).intValue())));
                    } else {
                        un.add(cell1.getStringCellValue());
                        un.add(apBL.getTipoGasto(cell1.getStringCellValue()));
                    }
                    // Insertamos el tercer valor del excel y buscamos su
                    // descripcion
                    if (cell2.getCellType() == CellType.NUMERIC) {
                        un.add(String.valueOf(new Double(cell2.getNumericCellValue()).intValue()));
                        un.add(apBL.getUnidadResponsable(String.valueOf(new Double(cell2.getNumericCellValue()).intValue())));
                    } else {
                        un.add(cell2.getStringCellValue());
                        un.add(apBL.getUnidadResponsable(cell2.getStringCellValue()));
                    }
                    // Insertamos el último que sería el monto
                    un.add(String.valueOf(valcell2));
                }
                // Hacemos los deletes e inserts a la base de datos preguntando
                // si ya existe el registro
                if (tipo.equals("un")) {
                    int i = apBL.getTechoExiste(cell0.getStringCellValue(), "tTechoUNormativa", "cUnidadNormativa", null, null, null, null);
                    if (i > 0) {
                        apBL.borraTecho(cell0.getStringCellValue(), "tTechoUNormativa", "cUnidadNormativa", null, null, null, null);
                        apBL.insertaTechoUN(cell0.getStringCellValue(), cell1.getNumericCellValue());
                    } else
                        apBL.insertaTechoUN(cell0.getStringCellValue(), cell1.getNumericCellValue());
                } else if (tipo.equals("ue")) {
                    int i = apBL.getTechoExiste(cell0.getStringCellValue(), "tTechoUEjecutora", "cUnidadResponsable", null, null, null, null);
                    if (i > 0) {
                        apBL.borraTecho(cell0.getStringCellValue(), "tTechoUEjecutora", "cUnidadResponsable", null, null, null, null);
                        apBL.insertaTechoUE(cell0.getStringCellValue(), cell1.getNumericCellValue());
                    } else
                        apBL.insertaTechoUE(cell0.getStringCellValue(), cell1.getNumericCellValue());
                } else if (tipo.equals("entidad")) {
                    if (cell0.getCellType() == CellType.NUMERIC) {
                        int i = apBL.getTechoExiste(String.valueOf(new Double(cell0.getNumericCellValue()).intValue()), "tTechoEntidadFederativa", "cEntidadFederativa", null, null, null, null);
                        if (i > 0) {
                            apBL.borraTecho(String.valueOf(new Double(cell0.getNumericCellValue()).intValue()), "tTechoEntidadFederativa", "cEntidadFederativa", null, null, null, null);
                            apBL.insertaTechoEn(String.valueOf(new Double(cell0.getNumericCellValue()).intValue()), cell1.getNumericCellValue());
                        } else
                            apBL.insertaTechoEn(String.valueOf(new Double(cell0.getNumericCellValue()).intValue()), cell1.getNumericCellValue());
                    } else {
                        int i = apBL.getTechoExiste(cell0.getStringCellValue(), "tTechoEntidadFederativa", "cEntidadFederativa", null, null, null, null);
                        if (i > 0) {
                            apBL.borraTecho(cell0.getStringCellValue(), "tTechoEntidadFederativa", "cEntidadFederativa", null, null, null, null);
                            apBL.insertaTechoEn(cell0.getStringCellValue(), cell1.getNumericCellValue());
                        } else
                            apBL.insertaTechoEn(cell0.getStringCellValue(), cell1.getNumericCellValue());
                    }
                } else if (tipo.equals("programap")) {
                    int i = apBL.getTechoExiste(cell0.getStringCellValue(), "tTechoProgramaPresupuestario", "cProgramaPresupuestario", null, null, null, null);
                    if (i > 0) {
                        apBL.borraTecho(cell0.getStringCellValue(), "tTechoProgramaPresupuestario", "cProgramaPresupuestario", null, null, null, null);
                        apBL.insertaTechoPP(cell0.getStringCellValue(), cell1.getNumericCellValue());
                    } else
                        apBL.insertaTechoPP(cell0.getStringCellValue(), cell1.getNumericCellValue());
                } else if (tipo.equals("ret")) {
                    String valor;
                    String valor2;
                    if (cell0.getCellType() == CellType.NUMERIC)
                        valor = String.valueOf(new Double(cell0.getNumericCellValue()).intValue());
                    else
                        valor = cell0.getStringCellValue();
                    if (cell1.getCellType() == CellType.NUMERIC)
                        valor2 = String.valueOf(new Double(cell1.getNumericCellValue()).intValue());
                    else
                        valor2 = cell1.getStringCellValue();
                    int i = apBL.getTechoExiste(valor, "tTechosPartida", "cPartida", "cTipoGasto", valor2, null, null);
                    if (i > 0) {
                        apBL.borraTecho(valor, "tTechosPartida", "cPartida", "cTipoGasto", valor2, null, null);
                        apBL.insertaTechoPartida(valor, valor2, valcell2);
                    } else
                        apBL.insertaTechoPartida(valor, valor2, valcell2);
                } else if (tipo.equals("progprue")) {
                    String valor;
                    String valor2;
                    if (cell0.getCellType() == CellType.NUMERIC)
                        valor = String.valueOf(new Double(cell0.getNumericCellValue()).intValue());
                    else
                        valor = cell0.getStringCellValue();
                    if (cell1.getCellType() == CellType.NUMERIC)
                        valor2 = String.valueOf(new Double(cell1.getNumericCellValue()).intValue());
                    else
                        valor2 = cell1.getStringCellValue();
                    int i = apBL.getTechoExiste(valor, "tTechoProgPresupUniEjec", "cProgramaPresupuestario", "cUnidadResponsable", valor2, null, null);
                    if (i > 0) {
                        apBL.borraTecho(valor, "tTechoProgPresupUniEjec", "cProgramaPresupuestario", "cUnidadResponsable", valor2, null, null);
                        apBL.insertaTechoPPUE(valor, valor2, valcell2);
                    } else
                        apBL.insertaTechoPPUE(valor, valor2, valcell2);
                } else if (tipo.equals("partidaue")) {
                    String valor;
                    String valor2;
                    String valor3;
                    HSSFCell cell2 = (HSSFCell) row.getCell(2);
                    HSSFCell cell3 = (HSSFCell) row.getCell(3);
                    double montoUE = cell3.getNumericCellValue();
                    if (cell0.getCellType() == CellType.NUMERIC)
                        valor = String.valueOf(new Double(cell0.getNumericCellValue()).intValue());
                    else
                        valor = cell0.getStringCellValue();
                    if (cell1.getCellType() == CellType.NUMERIC)
                        valor2 = String.valueOf(new Double(cell1.getNumericCellValue()).intValue());
                    else
                        valor2 = cell1.getStringCellValue();
                    if (cell2.getCellType() == CellType.NUMERIC)
                        valor3 = String.valueOf(new Double(cell2.getNumericCellValue()).intValue());
                    else
                        valor3 = cell2.getStringCellValue();
                    int i = apBL.getTechoExiste(valor, "tTechosPartidaUniEjec", "cPartida", "cTipoGasto", valor2, "cUnidadResponsable", valor3);
                    if (i > 0) {
                        apBL.borraTecho(valor, "tTechosPartidaUniEjec", "cPartida", "cTipoGasto", valor2, "cUnidadResponsable", valor3);
                        apBL.insertaTechoPartidaUE(valor, valor2, valor3, montoUE);
                    } else
                        apBL.insertaTechoPartidaUE(valor, valor2, valor3, montoUE);
                } else if (tipo.equals("partidaun")) {
                    String valor;
                    String valor2;
                    String valor3;
                    HSSFCell cell2 = (HSSFCell) row.getCell(2);
                    HSSFCell cell3 = (HSSFCell) row.getCell(3);
                    double montoUE = cell3.getNumericCellValue();
                    if (cell0.getCellType() == CellType.NUMERIC)
                        valor = String.valueOf(new Double(cell0.getNumericCellValue()).intValue());
                    else
                        valor = cell0.getStringCellValue();
                    if (cell1.getCellType() == CellType.NUMERIC)
                        valor2 = String.valueOf(new Double(cell1.getNumericCellValue()).intValue());
                    else
                        valor2 = cell1.getStringCellValue();
                    if (cell2.getCellType() == CellType.NUMERIC)
                        valor3 = String.valueOf(new Double(cell2.getNumericCellValue()).intValue());
                    else
                        valor3 = cell2.getStringCellValue();
                    int i = apBL.getTechoExiste(valor, "tTechosPartidaUniNorm", "cPartida", "cTipoGasto", valor2, "cUnidadNormativa", valor3);
                    if (i > 0) {
                        apBL.borraTechoUN(valor, "tTechosPartidaUniNorm", "cPartida", "cTipoGasto", valor2, "cUnidadNormativa", valor3);
                        apBL.insertaTechoPartidaUN(valor, valor2, valor3, montoUE);
                    } else
                        apBL.insertaTechoPartidaUN(valor, valor2, valor3, montoUE);
                }
                // *****************************************
            }
            wb.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return un;
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
