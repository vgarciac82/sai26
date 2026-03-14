package com.syc.sai.procesos;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.jenkov.prizetags.tree.itf.ITree;
import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.syc.fortimax.core.Fortimax;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.NodeInformation;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "SAIGetFileServlet", urlPatterns = { "/SAIFilestore" })
public class SAIGetFileServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(SAIGetFileServlet.class);

    private String jniName = null;

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
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
		HttpSession session = req.getSession(false);
		if (session == null) {
			log.warn("No hay sesion");
			setMainPage(resp);
			return;
		}

		Usuario u = (Usuario) session.getAttribute(ATT_USER);
		if (u == null) {
			log.warn("No hay Usuario en sesion");
			session.invalidate();
			setMainPage(resp);
			return;
		}
*/
        String select = req.getParameter("select");
        Fortimax nodoFortimax = new Fortimax(select);
        CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);
        Caso c = null;
        ITree tree = null;
        try {
            c = cbl.getCaso(nodoFortimax.getTituloAplicacion(), nodoFortimax.getIdGabinete());
            if (c == null) {
                throw new Exception("No fue posible generar un caso para el nodo [" + select + "]");
            }
            tree = cbl.getArbolCaso(c);
            if (tree == null) {
                log.error("No se encontro arbol en el caso ");
                throw new ServletException("No se encontro arbol en el caso");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
        ITreeNode node = tree.findNode(select);
        if (node == null) {
            log.error("Object: {}", "No se encontro nodo (" + select + ")");
            throw new ServletException("No se encontro nodo (" + select + ")");
        }
        NodeInformation nodeInf = (NodeInformation) node.getObject();
        if (nodeInf == null) {
            log.error("No hay informacion en el nodo");
            throw new ServletException("No hay informacion en el nodo");
        }
        if (nodeInf.getFisicalName() != null)
            doDownload(resp, nodeInf.getFisicalFilenamePath(), nodeInf.getNameWithExtension());
        else {
            resp.sendRedirect("caso/upload.jsp?select=" + select);
        }
    }

    private void setMainPage(HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        out.println("<script language=\"javascript\">self.top.location.href=\"../index.jsp\";</script>");
        out.flush();
        out.close();
    }

    private void doDownload(HttpServletResponse resp, String filename, String original_filename) throws IOException {
        int length = 0;
        File f = new File(filename);
        ServletOutputStream out = resp.getOutputStream();
        ServletContext context = getServletConfig().getServletContext();
        String mimetype = context.getMimeType(original_filename);
        resp.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
        resp.setContentLength((int) f.length());
        // resp.addHeader("Content-Disposition", "attachment; filename=\"" +
        // original_filename + "\";");
        // resp.addHeader("Content-Disposition", "attachement; filename=\"" +
        // original_filename + "\";");
        resp.addHeader("Content-Disposition", "inline; filename=\"" + original_filename + "\";");
        // 5K buffer
        byte[] bbuf = new byte[5 * 1024];
        DataInputStream in = new DataInputStream(new FileInputStream(f));
        while ((in != null) && ((length = in.read(bbuf)) != -1)) {
            out.write(bbuf, 0, length);
        }
        in.close();
        out.flush();
        out.close();
    }
}
