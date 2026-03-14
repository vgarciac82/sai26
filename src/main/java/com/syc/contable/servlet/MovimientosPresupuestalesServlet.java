package com.syc.contable.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.contable.MovimientosPresupuestalesBussinessLogic;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

/**
 * @author Martha Aurora Sánchez Valdivieso para SYC Constructores de Sistemas SA de CV
 *         desarrollo gestion_conagua_sif México D.F. 05/07/2012
 */
@WebServlet(name = "MovimientosPresupuestalesServlet", urlPatterns = { "/gstnmngr/MovimientosPresupuestalesServlet" })
public class MovimientosPresupuestalesServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(MovimientosPresupuestalesServlet.class);

    String mensaje = "";

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        String pathURL = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + pathURL + "/";
        List<StringBuffer> movimientos = new ArrayList<StringBuffer>();
        MovimientosPresupuestalesBussinessLogic movpresBL = new MovimientosPresupuestalesBussinessLogic(GestionInterface.ATT_CONEXION);
        try {
            String ep = request.getParameter("txtGridEP").toString();
            String tipoCuenta = request.getParameter("dTipoCuenta").toString();
            String strDesde = request.getParameter("fDesde").toString();
            String strHasta = request.getParameter("fHasta").toString();
            session.setAttribute("ep", ep);
            session.setAttribute("tipoCuenta", tipoCuenta);
            session.setAttribute("strDesde", strDesde);
            session.setAttribute("strHasta", strHasta);
            movimientos = movpresBL.filtraMovimientos(ep, tipoCuenta, strDesde, strHasta);
            session.setAttribute("movimientos", movimientos);
            if (movimientos == null || movimientos.size() == 0) {
                mensaje = "No hay resultados para esta b&uacute;squeda";
            } else {
                mensaje = "B&uacute;squeda completa";
            }
            session.setAttribute("mensaje", mensaje);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            response.sendRedirect(basePath + "plantillasCasos/movimientosPresupuestales.jsp");
        }
    }
}
