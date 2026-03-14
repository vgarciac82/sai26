package com.syc.contable.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.fileupload2.core.FileItem;
import com.syc.contable.CompromisoBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Martha Aurora Sánchez Valdivieso para SYC Constructores de Sistemas
 *         SA de CV desarrollo gestion_conagua_sif México D.F. 07/02/2012
 */
@WebServlet(name = "RespuestaCompromisosServlet", urlPatterns = { "/gstnmngr/RespuestaCompromisosSICOP" })
public class RespuestaCompromisosServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(RespuestaCompromisosServlet.class);

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        try {
            if (session == null) {
                response.sendRedirect("index.jsp");
                return;
            }
            Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            if (usuario == null) {
                response.sendRedirect("index.jsp");
                return;
            }
            String tipoLayout = "";
            List<?> fileItems = null;
            Iterator<?> iter = null;
            DataInputStream archivoCargaStream = null;
            File nombreDestino = null;
            fileItems = Util.parseRequest(request, System.getProperty("java.io.tmpdir"), -1);
            iter = fileItems.iterator();
            List<String> errores = null;
            boolean archivoRecibido = false;
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                if (item.isFormField()) {
                    if ("tipoLayout".equals(item.getFieldName())) {
                        tipoLayout = item.getString();
                    }
                } else {
                    archivoCargaStream = new DataInputStream(item.getInputStream());
                    String nombreArchivo = item.getName();
                    log.info("Object: {}", "Copiando archivo :" + nombreArchivo);
                    nombreDestino = File.createTempFile("CARGA_COMPROMISO", ".csv", new File(System.getProperty("java.io.tmpdir")));
                    Util.copiaArchivo(archivoCargaStream, nombreDestino.getAbsolutePath());
                    archivoRecibido = true;
                }
            }
            if (!archivoRecibido)
                throw new Exception("No se recibio archivo.");
            CompromisoBussinessLogic cmpBL = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
            if ("COMPROMISO".equalsIgnoreCase(tipoLayout)) {
                errores = cmpBL.procesaLayoutCompromiso(nombreDestino);
                cmpBL.actualizaFolios();
            } else if (("SUFICIENCIA".equalsIgnoreCase(tipoLayout))) {
                errores = cmpBL.procesaLayoutSuficiencia(nombreDestino);
            } else {
                errores = cmpBL.procesaLayoutAplicarCompromisos(nombreDestino);
            }
            if (errores == null)
                session.setAttribute("msg", "Archivo cargado exitosamente.");
            else
                session.setAttribute("msg", "El archivo cargo con errores.\n" + Util.join(errores, '\n'));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            session.setAttribute("msg", "Ocurrio el siguiente error: " + e.getMessage());
        }
        response.sendRedirect("../Generador/CompromisosDevueltos.jsp");
    }
}
