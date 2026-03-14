package com.syc.gestion.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.apache.commons.fileupload2.core.FileUploadException;
import com.jenkov.prizetags.tree.itf.ITree;
import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.syc.fortimax.core.Fortimax;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.viewer.servlet.ViewerParametersInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "GestionFileReceiverServlet", urlPatterns = { "/upload" })
public class GestionFileReceiverServlet extends HttpServlet implements GestionInterface, ViewerParametersInterface {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(GestionFileReceiverServlet.class);

    private int count = 0;

    private String jniName = null;

    private String tempDir = null;

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

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean updToAlbum = "true".equalsIgnoreCase(req.getParameter("upd"));
        boolean closeWin = "true".equalsIgnoreCase(req.getParameter("close"));
        boolean current = "true".equalsIgnoreCase(req.getParameter("current"));
        if ((!FileUpload.isMultipartContent(req)) && (!updToAlbum)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No es una peticion multipart/form-data");
            return;
        }
        HttpSession session = req.getSession(false);
        if (session == null) {
            log.warn("No hay sesion");
            resp.sendRedirect("../index.jsp");
            // <script language="javascript">self.top.location.href = "../index.jsp";</script>
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            log.warn("No hay Usuario en sesion");
            session.invalidate();
            resp.sendRedirect("../index.jsp");
            return;
        }
        Caso c = (Caso) session.getAttribute(ATT_CASE);
        if (c == null) {
            log.error("Llamada invalida, sin Caso seleccionado");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Llamada inválida, sin Caso seleccionado");
        }
        String select = req.getParameter("select");
        if (select == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sin nodo seleccionado");
            return;
        }
        ITree tree = (ITree) session.getAttribute(ATT_TREE);
        if (tree == null) {
            log.error("Llamada invalida, sin Arbol");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Llamada invalida, sin Arbol");
        }
        ITreeNode node = tree.findNode(select);
        if (node == null) {
            log.error("Llamada invalida, nodo invalido");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Llamada invalida, nodo invalido");
        }
        CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);
        if (updToAlbum) {
            try {
                cbl.actualizaDocumentoGestion(new Fortimax(select));
                tree = cbl.getArbolCaso(c);
            } catch (GestionException e) {
                throw new ServletException(e);
            }
            session.setAttribute(ATT_TREE, tree);
            PrintWriter out = resp.getWriter();
            if (closeWin)
                windowClose(out, select, 0, current);
            else {
                out.print("<script type=\"text/javascript\">");
                out.print("parent.frames[\"doctree\"].location.reload(1);");
                out.print("parent.frames[\"main\"].location.href=\"imgmng/image-viewer.jsp?select=" + select + "\";");
                out.println("</script>");
            }
            out.flush();
            out.close();
            return;
        }
        int idx = 0;
        count = 0;
        String tmpFile = null;
        List fileItems = parseRequest(req);
        Iterator i = fileItems.iterator();
        while (i.hasNext()) {
            FileItem item = (FileItem) i.next();
            if (item.isFormField())
                continue;
            try {
                tmpFile = (new File(item.getName())).getName();
                int pos = tmpFile.lastIndexOf('.') + 1;
                String ext = pos != -1 ? tmpFile.substring(pos) : "";
                Fortimax fimx = new Fortimax(select);
                cbl.recibeDocumentoGestion(c, fimx.getIdCarpeta(), node.getName(), ext, new DataInputStream(item.getInputStream()), closeWin);
                item.delete();
                Fortimax f = new Fortimax(select);
                idx = cbl.getArchivosDocumento(f.getTituloAplicacion(), f.getIdGabinete(), f.getIdCarpeta(), f.getIdDocumento()).length - 1;
                tree = cbl.getArbolCaso(c);
                session.setAttribute(ATT_TREE, tree);
            } catch (Exception e) {
                throw new ServletException(e);
            }
            count++;
        }
        PrintWriter out = resp.getWriter();
        if (closeWin)
            windowClose(out, select, idx, current);
        else {
            out.print("<script type=\"text/javascript\">");
            out.print("parent.frames[\"doctree\"].location.reload(1);");
            out.print("parent.frames[\"main\"].location.href=\"" + "filestore?select=" + select + "\";");
            out.println("</script>");
        }
        out.flush();
        out.close();
    }

    private List parseRequest(HttpServletRequest req) throws ServletException {
        ServletFileUpload upload = new ServletFileUpload();
        upload.setRepositoryPath(tempDir);
        // Directorio temporal de carga de archivos
        // -1 sin limite
        upload.setSizeMax(-1);
        try {
            return upload.parseRequest(req);
        } catch (FileUploadException fe) {
            fe.printStackTrace();
            throw new ServletException("Error de recepcion " + fe.getMessage());
        }
    }

    private void windowClose(PrintWriter out, String select, int index, boolean current) throws IOException {
        out.print("<html><header><script type=\"text/javascript\">");
        out.print("function winClose() {");
        out.print("opener.parent.parent.frames[\"doctree\"].location.reload(1);");
        if (current)
            out.print("opener.parent.parent.frames[\"main\"].location.href=\"" + "imgmng/image-viewer.jsp?select=" + select + "&" + INDEX_KEY + "=" + index + "\";");
        else
            out.print("opener.parent.parent.frames[\"main\"].location.href=\"" + "image-viewer.jsp?select=" + select + "&" + INDEX_KEY + "=" + index + "\";");
        out.print("self.close();");
        out.print("}");
        out.println("</script></header><body onload=\"winClose()\"></body></html>");
    }
}
