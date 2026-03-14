package com.syc.gestion.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Manifest;
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
import com.syc.gestion.core.FortimaxFile;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.NodeInformation;
import com.syc.gestion.core.Usuario;
import com.syc.jar.JarEntry;
import com.syc.jar.JarOutputStream;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "GestionSendFolderServlet", urlPatterns = { "/filedownload" })
public class GestionSendFolderServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(GestionSendFolderServlet.class);

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
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect("index.jsp");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            resp.sendRedirect("index.jsp");
            return;
        }
        ITree tree = (ITree) session.getAttribute(ATT_TREE);
        if (tree == null) {
            resp.sendRedirect("index.jsp");
            return;
        }
        String selectId = req.getParameter("select");
        if (selectId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sin nodo seleccionado");
            return;
        }
        boolean opcionVersiones = !"true".equals(req.getParameter("ver"));
        boolean asZip = "true".equals(req.getParameter("zip"));
        ITreeNode treeNode = tree.findNode(selectId);
        String tmpPath = getServletContext().getRealPath("WEB-INF/temp");
        File fzip = new File(tmpPath + "/" + u.getLogin() + System.currentTimeMillis() + (asZip ? ".zip" : ".jar"));
        JarOutputStream jout = null;
        if (!asZip) {
            String preDir = "/jars/selfextractor/";
            String[] classPath = { getServletContext().getRealPath(preDir + "SelfExtractorFile.class"), getServletContext().getRealPath(preDir + "AsiExtraField.class"), getServletContext().getRealPath(preDir + "ExtraFieldUtils.class"), getServletContext().getRealPath(preDir + "JarMarker.class"), getServletContext().getRealPath(preDir + "UnixStat.class"), getServletContext().getRealPath(preDir + "UnrecognizedExtraField.class"), getServletContext().getRealPath(preDir + "ZipEntry.class"), getServletContext().getRealPath(preDir + "ZipExtraField.class"), getServletContext().getRealPath(preDir + "ZipFile$BoundedInputStream.class"), getServletContext().getRealPath(preDir + "ZipFile.class"), getServletContext().getRealPath(preDir + "ZipLong.class"), getServletContext().getRealPath(preDir + "ZipOutputStream.class"), getServletContext().getRealPath(preDir + "ZipShort.class") };
            String rutaPackage = "org/apache/tools/zip/";
            // Esto debe contener el archivo Manifest (jarmanifest)
            // Manifest-Version: 1.0\n
            // Main-Class: SelfExtractorFile\n
            String manifestPath = getServletContext().getRealPath("/jars/selfextractor/jarmanifest");
            File fmnfst = new File(manifestPath);
            jout = new JarOutputStream(new FileOutputStream(fzip), new Manifest(new FileInputStream(fmnfst)), "CP1252");
            String token = "";
            for (int i = 0; i < classPath.length; i++) {
                File fslfext = new File(classPath[i]);
                jout.putNextEntry(new JarEntry(token + fslfext.getName()));
                FileInputStream fis = new FileInputStream(fslfext);
                int len;
                // 4K buffer
                byte[] buf = new byte[4 * 1024];
                while ((len = fis.read(buf)) > 0) {
                    jout.write(buf, 0, len);
                }
                fis.close();
                jout.closeEntry();
                token = rutaPackage;
            }
        } else {
            jout = new JarOutputStream(new FileOutputStream(fzip), "CP1252");
        }
        String nom_docto = null;
        if (treeNode.getType().startsWith("docto")) {
            NodeInformation inf = (NodeInformation) treeNode.getObject();
            // FIXME ver si extension trae ~
            nom_docto = (treeNode.getType().endsWith("frtimx") ? "imx/" : inf.getNameWithExtension());
        } else {
            nom_docto = treeNode.getName() + "/";
        }
        appendToZipFile(jout, (NodeInformation) treeNode.getObject(), new Fortimax(treeNode.getId()), nom_docto);
        processChildren(jout, tree, treeNode, treeNode.getName() + "/", opcionVersiones);
        jout.finish();
        jout.close();
        doDownload(resp, fzip.getAbsolutePath(), fzip.getName());
        fzip.delete();
    }

    private void processChildren(JarOutputStream out, ITree t, ITreeNode p, String parent, boolean opcionVersiones) throws IOException {
        List l = p.getChildren();
        for (Iterator iter = l.iterator(); iter.hasNext(); ) {
            ITreeNode n = (ITreeNode) iter.next();
            String type = n.getType();
            NodeInformation inf = (NodeInformation) n.getObject();
            // if (type.startsWith("carpeta")) {
            // Carpeta c = (Carpeta) obj;
            // if (c.isProtected() && !c.isOpen())
            // continue;
            // }
            if (opcionVersiones) {
                if (isNotLastNode(t, n))
                    continue;
            }
            String entryName = parent + n.getName() + "/";
            if (File.separatorChar != '/') {
                entryName = entryName.replace(File.separatorChar, '/');
            }
            if (type.startsWith("docto")) {
                // FIXME ver si extension trae ~
                entryName = parent + (type.endsWith("frtimx") ? "imx/" : inf.getNameWithExtension());
            }
            appendToZipFile(out, (NodeInformation) n.getObject(), new Fortimax(n.getId()), entryName);
            if (n.hasChildren())
                processChildren(out, t, n, parent + n.getName() + "/", opcionVersiones);
        }
    }

    private boolean isNotLastNode(ITree t, ITreeNode n) {
        NodeInformation inf = (NodeInformation) n.getObject();
        String maxId = n.getId();
        ITreeNode pn = n.getParent();
        String nameToFind = inf.getNameWithExtension();
        List l = pn.getChildren();
        for (Iterator iter = l.iterator(); iter.hasNext(); ) {
            ITreeNode in = (ITreeNode) iter.next();
            NodeInformation infIn = (NodeInformation) n.getObject();
            if (infIn.getNameWithExtension().equals(nameToFind)) {
                String currId = in.getId();
                if (currId.compareTo(maxId) > 0)
                    maxId = currId;
            }
        }
        return (maxId.compareTo(n.getId()) == 0 ? false : true);
    }

    private void appendToZipFile(JarOutputStream out, NodeInformation inf, Fortimax f, String parent) throws IOException {
        String path = parent;
        if (File.separatorChar != '/') {
            path = path.replace(File.separatorChar, '/');
        }
        JarEntry entry = new JarEntry(path);
        path = setJarEntry(out, entry, 1);
        if (f.isDocumento()) {
            FortimaxFile[] fn = getFilesList(f);
            // File fn[] = getFilesList(f);
            for (int i = 0; i < fn.length; i++) {
                if (fn.length > 1) {
                    String zeros = "0000000";
                    String pagName = path + "p" + zeros.substring(0, zeros.length() - String.valueOf(i + 1).length()) + (i + 1) + ((fn[i].withExtension()) ? "." + fn[i].getExtension() : "");
                    out.closeEntry();
                    if (File.separatorChar != '/') {
                        pagName = pagName.replace(File.separatorChar, '/');
                    }
                    entry = new JarEntry(pagName);
                    setJarEntry(out, entry, 1);
                }
                int length = 0;
                // 4K buffer
                byte[] bbuf = new byte[4 * 1024];
                DataInputStream in = new DataInputStream(new FileInputStream(fn[i].getFile()));
                while ((in != null) && ((length = in.read(bbuf)) != -1)) {
                    out.write(bbuf, 0, length);
                }
                in.close();
            }
        }
        out.closeEntry();
        out.flush();
    }

    private String setJarEntry(JarOutputStream out, JarEntry entry, int seq) {
        String retval = entry.getName();
        try {
            out.putNextEntry(entry);
        } catch (Exception ze) {
            if (ze.getMessage().startsWith("duplicate entry")) {
                String newName = entry.getName();
                newName = newName.replaceAll("\\(\\d\\)", "");
                int dot = newName.lastIndexOf(".");
                if (dot == -1)
                    newName = newName + "(" + seq + ")";
                else
                    newName = newName.substring(0, dot) + "(" + seq + ")" + newName.substring(dot);
                JarEntry e = new JarEntry(newName);
                retval = setJarEntry(out, e, seq + 1);
            }
        }
        return retval;
    }

    private FortimaxFile[] getFilesList(Fortimax f) {
        FortimaxFile[] files = new FortimaxFile[0];
        CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);
        try {
            files = cbl.getArchivosDeDocumento(f.getTituloAplicacion(), f.getIdGabinete(), f.getIdCarpeta(), f.getIdDocumento());
        } catch (GestionException exc) {
            log.warn("Recuperando Archivos", exc);
            files = new FortimaxFile[0];
        }
        return files;
    }

    private void doDownload(HttpServletResponse resp, String filename, String original_filename) throws IOException {
        File f = new File(filename);
        int length = 0;
        ServletOutputStream op = resp.getOutputStream();
        ServletContext context = getServletConfig().getServletContext();
        String mimetype = context.getMimeType(original_filename);
        resp.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
        resp.setContentLength((int) f.length());
        resp.addHeader("Content-Disposition", "inline; filename=\"" + original_filename + "\";");
        // 4K buffer
        byte[] bbuf = new byte[4 * 1024];
        DataInputStream in = new DataInputStream(new FileInputStream(f));
        while ((in != null) && ((length = in.read(bbuf)) != -1)) {
            op.write(bbuf, 0, length);
        }
        in.close();
        op.flush();
        op.close();
    }
}
