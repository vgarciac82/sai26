//Este es con el jar cos
package com.syc.admin.servlet;

import java.io.File;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.fileupload2.core.FileUploadException;
import com.syc.crud.dsmngr.DataSourceManager;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import java.nio.file.Paths;

@WebServlet(name = "CatalogoLPCE", urlPatterns = { "/servlet/CatalogoLPCE" })
public class CatalogoLPCE extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static String jniName = null;

    private static Logger log = LoggerFactory.getLogger(CatalogoLPCE.class);

    private String tempDir = null;

    private Connection conn = null;

    private CallableStatement cmst = null;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        tempDir = config.getInitParameter("tempDir");
        if (tempDir == null) {
            tempDir = config.getServletContext().getRealPath("/") + ".." + File.separator + "upload" + File.separator;
            File fDir = new File(tempDir);
            if (!fDir.exists())
                if (!fDir.mkdirs())
                    throw new ServletException("No se pudo crear el directorio " + tempDir);
        }
    }

    private List parseRequest(HttpServletRequest req) throws ServletException {
        DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
        JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
        // Si el archivo excede este tama?o, ocurre un excepcion FileUploadException
        //con -1 le indicamos que acepte archivos de cualquier tamaño
        // -1 sin limite
        upload.setFileSizeMax(-1);
        try {
            return upload.parseRequest(req);
        } catch (FileUploadException fe) {
            fe.printStackTrace();
            throw new ServletException("Error de recepcion " + fe.getMessage());
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession();
        List fileItems = null;
        String NombreArchivo = null;
        int count = 0;
        int outputValue = 0;
        int id = 0;
        int maximo = 0;
        File fichero = null;
        FileItem actual = null;
        FileItem actualAjax = null;
        File ficheroAjax = null;
        String CarpetaAjax = null;
        //Urls
        String urlServicios = null;
        String urlBienes = null;
        String rutaAjax = null;
        //Datos de conecction
        Statement stmt = null;
        ResultSet rs = null;
        ResultSet rsMaximo = null;
        String query = null;
        String queryMaximo = null;
        //Obtiene si es bienes o servicios
        String obtienenombreCarpeta = request.getParameter("choice");
        String RutaContex = getServletContext().getRealPath("/") + "docs" + File.separator + "lpce" + File.separator;
        //RutaContex=RutaContex.replaceAll("\\\\", "\\");
        log.info("Object: {}", "Nombre de la RutaContex: " + RutaContex);
        log.info("Object: {}", "Nombre de la carpeta: " + obtienenombreCarpeta);
        log.info("estamos en la iteración");
        try {
            conn = DataSourceManager.getConnection(jniName);
            ////////REgreso via ajax/////////////////
            if (request.getParameter("operacion") != null && "4".equalsIgnoreCase(request.getParameter("operacion"))) {
                log.info("Object: {}", "operacion****+" + request.getParameter("operacion"));
                String name = (String) sesion.getAttribute("ejmploNombre");
                log.info("Object: {}", "name****" + name);
                actualAjax = (FileItem) sesion.getAttribute("itemActual");
                CarpetaAjax = (String) sesion.getAttribute("carpeta");
                rutaAjax = (String) sesion.getAttribute("ruta");
                //
                //ficheroAjax = new  File("C:\\upload\\"+CarpetaAjax+"\\"+name);
                ficheroAjax = new File(RutaContex + CarpetaAjax.toLowerCase() + "\\" + name);
                // escribimos el fichero colgando del nuevo path
                actualAjax.write(ficheroAjax.toPath());
                return;
            } else //****************
            {
                //obtener ruta
                query = "select cValor from msistema where cParametro='" + obtienenombreCarpeta + "lpce'";
                log.info("Object: {}", "query:::::::" + query);
                stmt = conn.createStatement();
                rs = stmt.executeQuery(query);
                //se obtiene la url de la tabla
                while (rs.next()) {
                    urlServicios = rs.getString(1);
                    log.info("Object: {}", "Que trae el query? " + rs.getString(1));
                }
                //para obtener el nombre del archivo a subir
                fileItems = parseRequest(request);
                Iterator i = fileItems.iterator();
                while (i.hasNext() && count == 0) {
                    actual = (FileItem) i.next();
                    String fileName = actual.getName();
                    fichero = new File(fileName);
                    //Se obtiene el nombre del archivo
                    NombreArchivo = fichero.getName();
                    NombreArchivo = NombreArchivo.replace(".docx", ".doc");
                    log.info("Object: {}", "NombreArchivo .- " + NombreArchivo);
                    count++;
                }
                cmst = conn.prepareCall("{?= call pa_mvalidaExixteArchivoLPCE (?,?)}");
                cmst.registerOutParameter(1, Types.INTEGER);
                cmst.setString(2, NombreArchivo);
                cmst.setString(3, obtienenombreCarpeta);
                cmst.execute();
                outputValue = cmst.getInt(1);
                if (outputValue != 0) {
                    log.info("Ya existe el Documento.......");
                    //sesion.setAttribute("msg", "El archivo ya exite desea remplazarlo");
                    sesion.setAttribute("itemActual", actual);
                    sesion.setAttribute("ejmploNombre", NombreArchivo);
                    sesion.setAttribute("carpeta", obtienenombreCarpeta);
                    sesion.setAttribute("ruta", urlServicios);
                    response.sendRedirect("../Generador/SAICYS/mCatalogoLicitacionPublicaCreditoExterno.jsp?tab=1&msg=2");
                    return;
                } else {
                    log.info("Object: {}", "obtienenombreCarpeta: " + obtienenombreCarpeta);
                    log.info("Object: {}", "NombreArchivo: " + NombreArchivo);
                    id = Integer.parseInt(NombreArchivo.substring(0, NombreArchivo.indexOf('.')));
                    //obtener ruta
                    queryMaximo = "select MAX(nIdLicitacion) as maximo from mCatalogoLicitacionPublicacreditoExterno";
                    log.info("Object: {}", "queryMaximo:::::::" + queryMaximo);
                    stmt = conn.createStatement();
                    rsMaximo = stmt.executeQuery(queryMaximo);
                    while (rsMaximo.next()) {
                        maximo = Integer.parseInt(rsMaximo.getString(1));
                        log.info("Object: {}", "Que trae el query maximo? " + rsMaximo.getString(1));
                    }
                    if (id <= maximo) {
                        if (obtienenombreCarpeta != null && obtienenombreCarpeta.equalsIgnoreCase("Servicios")) {
                            query = "update mCatalogoLicitacionPublicaCreditoExterno set mNombreServicios='" + NombreArchivo + "' where nIdLicitacion=" + "'" + id + "'";
                            log.info("Object: {}", "query:::::::" + query);
                            stmt = conn.createStatement();
                            stmt.execute(query);
                            conn.commit();
                        } else {
                            query = "update mCatalogoLicitacionPublicaCreditoExterno set mNombreBienes='" + NombreArchivo + "' where nIdLicitacion=" + "'" + id + "'";
                            log.info("Object: {}", "query:::::::" + query);
                            stmt = conn.createStatement();
                            stmt.execute(query);
                            conn.commit();
                        }
                        //  urlServicios "..\\"+"172.29.150.118\\SAICyS\\Docs\\bases\\bienes"
                        //fichero = new  File("C:\\upload"+"\\"+obtienenombreCarpeta+"\\"+NombreArchivo);
                        fichero = new File(RutaContex + "\\" + obtienenombreCarpeta.toLowerCase() + "\\" + NombreArchivo);
                        // escribimos el fichero colgando del nuevo path
                        actual.write(fichero.toPath());
                        response.sendRedirect("../Generador/SAICYS/mCatalogoLicitacionPublicaCreditoExterno.jsp?tab=1&accion=2");
                        return;
                    } else {
                        response.sendRedirect("../Generador/SAICYS/mCatalogoLicitacionPublicaCreditoExterno.jsp?tab=1&registro=1");
                        return;
                    }
                }
            }
        } catch (Exception e) {
            log.info("Error occurred", "Error de Aplicación " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (stmt != null)
                    stmt.close();
                if (rs != null)
                    rs.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            if (cmst != null)
                try {
                    cmst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        cmst = null;
        conn = null;
        stmt = null;
        rs = null;
    }
}
