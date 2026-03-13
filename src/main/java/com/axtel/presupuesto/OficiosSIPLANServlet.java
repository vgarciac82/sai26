package com.axtel.presupuesto;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "OficiosSIPLAN", urlPatterns = { "/presupuesto/OficiosSIPLAN" })
public class OficiosSIPLANServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 6723127616859732249L;

    private static final Logger log = LoggerFactory.getLogger(OficiosSIPLANServlet.class);

    private static String jniName = "jdbc/gestion";

    private static Map<String, String> plantillas = null;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        OficiosSIPLANBusinessLogic osp = new OficiosSIPLANBusinessLogic(jniName);
        //ruta del jasper
        String ruta = getServletContext().getRealPath("Reportes" + File.separator);
        int nOficio = Integer.parseInt(req.getParameter("nOficioC"));
        String consulta = req.getParameter("consulta");
        String ext = req.getParameter("ext");
        int mes = Integer.parseInt(req.getParameter("mes"));
        try {
            if ("S".equals(consulta))
                osp.consultaOficio(req, resp, ruta, nOficio);
            if ("xls".equals(ext))
                osp.extraeModificado(req, resp, mes, plantillas);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String msg = "";
        log.info("Iniciando la creación del Oficio.");
        HttpSession session = req.getSession(false);
        String responseType = "";
        boolean success = false;
        //ruta del jasper
        String ruta = getServletContext().getRealPath("Reportes" + File.separator);
        String qwhere = " AND ";
        int existe = 0;
        try {
            if (session == null)
                throw new Exception("Su sesion ha terminado. Ingrese nuevamente al sistema.");
            Usuario u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null)
                msg = "Su sesion ha terminado. Ingrese nuevamente al sistema.";
            int todasUnidades = Integer.parseInt(req.getParameter("todas"));
            String[] unidades = req.getParameterValues("chk_UnidadEjecutora");
            responseType = StringUtils.isBlank(req.getParameter("responseType")) ? "REDIRECT" : req.getParameter("responseType");
            String coordinacion = req.getParameter("cCoordinacion");
            int mes = Integer.parseInt(req.getParameter("mes"));
            int nOficio = Integer.parseInt(req.getParameter("nOficio"));
            OficiosSIPLANBusinessLogic osp = new OficiosSIPLANBusinessLogic(jniName);
            if ((unidades == null || unidades.length == 0) && todasUnidades == 0)
                msg = "No se recibio ninguna unidad.";
            if (StringUtils.isEmpty(msg)) {
                //BUSCA SI YA EXISTE EL OFICIO CON LA COORDINACION
                existe = osp.buscaOficio(nOficio, coordinacion);
                if (existe == 0) {
                    if (todasUnidades == 1) {
                        qwhere = qwhere + "cCoordinacion = '" + coordinacion + "'";
                    } else {
                        qwhere = qwhere + "cUnidadEjecutora IN (";
                        for (int i = 0; i < unidades.length; i++) {
                            if (i == unidades.length - 1)
                                qwhere = qwhere + "'" + unidades[i] + "'";
                            else
                                qwhere = qwhere + "'" + unidades[i] + "',";
                        }
                        qwhere = qwhere + ")";
                    }
                    osp.guardaOficio(req, resp, mes, qwhere);
                    msg = "Se dio de alta exitosamente el oficio " + nOficio + " favor de consultarlo en la pestaña 'Oficios Existentes'";
                    session.setAttribute("RESULT", msg);
                    success = true;
                } else {
                    msg = "El No. oficio " + nOficio + " ya existe, no puedes repetir números.";
                    session.setAttribute("RESULT", msg);
                }
            } else {
                msg = "No se creo la version correctamente, favor de contactar al administrador.";
                session.setAttribute("RESULT", msg);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            msg += e.toString();
        }
        session.setAttribute("MSG_RESP", msg);
        resp.sendRedirect("../Generador/oficiosSIPLAN.jsp");
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        synchronized (this) {
            if (plantillas == null) {
                plantillas = new HashMap<String, String>();
                plantillas.put("ModificadoSIPLAN", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ModificadoSIPLAN.xls"));
            }
        }
    }
}
