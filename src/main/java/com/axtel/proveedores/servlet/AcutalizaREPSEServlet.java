/**
 */
package com.axtel.proveedores.servlet;

import java.io.IOException;
import java.util.Iterator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import com.axtel.proveedores.ProveedorService;
import com.axtel.proveedores.model.Proveedor;
import com.axtel.request.MultipartObject;
import com.axtel.request.utils.RequestUtils;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vicente.garcia
 */
@WebServlet(name = "AcutalizaREPSEServlet", urlPatterns = { "/proveedores/actualizaREPSE" })
public class AcutalizaREPSEServlet extends HttpServlet implements GestionInterface {

    /**
     */
    private static final long serialVersionUID = -129610500485493735L;

    private static final Logger log = LogManager.getLogger(AcutalizaREPSEServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Usuario u = null;
        HttpSession session = req.getSession(false);
        String msg = "";
        if (session == null) {
            msg = "Su sesión ha caducado. Ingrese nuevamente al sistema y repita la operacion.";
            session = req.getSession(true);
        }
        u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            msg = "Su sesión ha caducado. Ingrese nuevamente al sistema y repita la operacion.";
        }
        MultipartObject mo = null;
        try {
            if (StringUtils.isBlank(msg)) {
                mo = RequestUtils.processMultiPart(req, System.getProperty("java.io.tmpdir"), -1);
                ProveedorService proveedorService = new ProveedorService(ATT_CONEXION);
                Proveedor proveedor = proveedorService.selectByRfc(mo.getParams().get("rfc"));
                proveedor.setNumeroREPSE(mo.getParams().get("noRepse"));
                proveedorService.updateREPSEProveedor(proveedor, u, mo.getFiles().get("archivoEvidencia"));
                msg = "Proveedor actualizado con exito!";
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            msg = "Error insertando evidencia del proveedor. " + e;
        } finally {
            if (mo != null && mo.getFiles() != null) {
                try {
                    for (Iterator<String> i = mo.getFiles().keySet().iterator(); i.hasNext(); ) {
                        String file = i.next();
                        if (!mo.getFiles().get(file).delete())
                            mo.getFiles().get(file).deleteOnExit();
                    }
                } catch (Exception e) {
                    log.warn("Problemas eliminando temporales: " + e, e);
                }
            }
        }
        session.setAttribute("msg", msg);
        resp.sendRedirect("../proveedores/CapturaREPSE.jsp");
    }
}
