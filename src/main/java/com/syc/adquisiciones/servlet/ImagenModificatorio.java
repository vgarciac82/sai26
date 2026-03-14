package com.syc.adquisiciones.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import java.util.Base64;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import java.nio.file.Paths;

@WebServlet(name = "imagenModificatorio", urlPatterns = { "/servlet/imagenModificatorio" })
public class ImagenModificatorio extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static String jniName = null;

    private String tempDir = null;

    private Connection conn = null;

    private PreparedStatement pstm = null;

    public ImagenModificatorio() {
        super();
    }

    public void destroy() {
        super.destroy();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.flush();
        out.close();
    }

    @SuppressWarnings("unchecked")
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String tipoArchivo = session.getAttribute(GestionInterface.ATT_PedidoModificatorioTipoArchivo).toString();
        String pedidoDefinitivo = session.getAttribute(GestionInterface.ATT_PedidoModificatorioDefinitivo).toString();
        String consecutivoMod = session.getAttribute(GestionInterface.ATT_PedidoModificatorioConsecutivo).toString();
        String ruta = getServletContext().getRealPath("/") + "docs" + File.separator + tipoArchivo + File.separator + pedidoDefinitivo.replace("/", "-") + "-" + consecutivoMod;
        String contentType = request.getContentType();
        if ((contentType != null) && (contentType.indexOf("multipart/form-data") >= 0)) {
            try {
                conn = DataSourceManager.getConnection(jniName);
                List fileItems = parseRequest(request);
                Iterator i = fileItems.iterator();
                if (i.hasNext()) {
                    FileItem actual = (FileItem) i.next();
                    String fileName = actual.getName();
                    String ext = fileName.substring(fileName.lastIndexOf('.')).toLowerCase();
                    File file = new File(ruta + ext);
                    actual.write(file.toPath());
                    String query = "update mPedidoModificado set cExtImagen = ? where cIdPedidoDefinitivo = ? and nConsecutivoModificacion = ?";
                    pstm = conn.prepareStatement(query);
                    pstm.setString(1, ext.replace(".", ""));
                    pstm.setString(2, pedidoDefinitivo);
                    pstm.setInt(3, Integer.parseInt(consecutivoMod));
                    pstm.executeUpdate();
                    conn.commit();
                }
                response.sendRedirect("../Generador/SAICYS/PedidoModificatorio.jsp?tab=3&cMensaje=Imagen+almacenada+correctamente");
                return;
                //response.sendRedirect("../Generador/SAICYS/PedidoModificatorioObservaciones.jsp?pDefinitivo="+ pedidoDefinitivo + "&mod=" + consecutivoMod + "&tipoArchivo=" + tipoArchivo);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (conn != null)
                        conn.close();
                    if (pstm != null)
                        pstm.close();
                } catch (SQLException exc) {
                    exc.printStackTrace();
                }
                if (pstm != null) {
                    try {
                        pstm.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            pstm = null;
            conn = null;
        }
        response.sendRedirect("../Generador/SAICYS/PedidoModificatorio.jsp?tab=3&cMensaje=Ocurrio+un+error+al+cargar+la+imagen");
        return;
    }

    @SuppressWarnings("unchecked")
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

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
            }
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
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
}
