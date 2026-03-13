package com.syc.contable.servlet;

import java.io.IOException;
import java.util.ArrayList;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.contable.ReintegrosBusinessLogic;
import com.syc.contable.core.ReintegroBoletos;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "CapturaBoletos", urlPatterns = { "/gstnmngr/CapturaBoletos" })
public class CapturaBoletosServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(ReintegrosBusinessLogic.class);

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        try {
            int nFolio = Integer.parseInt(request.getParameter("nFolio"));
            String[] detalle = request.getParameter("info").split("!");
            ArrayList<ReintegroBoletos> boletos = new ArrayList<ReintegroBoletos>();
            for (int i = 0; i < detalle.length; i += 6) {
                ReintegroBoletos rb = new ReintegroBoletos();
                rb.setNfolioReintegro(nFolio);
                rb.setnFolioPagoDiverso(Integer.parseInt(detalle[i].trim()));
                rb.setRFC((detalle[i + 1].trim()));
                rb.setcNombre(detalle[i + 2].trim());
                rb.setcReferencia(detalle[i + 3].trim());
                rb.setmTotal(Double.parseDouble(detalle[i + 4].trim()));
                rb.setcPartida(detalle[i + 5].trim());
                boletos.add(rb);
            }
            boolean retorno = false;
            log.debug("Se instancia los datos para insertar los Boletos");
            ReintegrosBusinessLogic reintBL = new ReintegrosBusinessLogic(GestionInterface.ATT_CONEXION);
            if (boletos != null) {
                retorno = reintBL.insertarBoletos(boletos);
            }
            if (retorno)
                ResponseSender.sendClientSimpleMessage(response, true, "Correcto");
            else
                throw new Exception("Ocurrion un error al guardar la solicitud, revise el presupuesto o consulte al administrador");
        } catch (Exception ex) {
            ResponseSender.sendClientSimpleMessage(response, false, "Ocurrio un error insertando la informacion. Favor de avisar al administrador");
        }
    }

    public void put(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        try {
            int nFolio = Integer.parseInt(request.getParameter("nFolio"));
            String cxp = request.getParameter("cxp");
            boolean retorno = false;
            ReintegrosBusinessLogic reintBL = new ReintegrosBusinessLogic(GestionInterface.ATT_CONEXION);
            retorno = reintBL.insertarTodosBoletos(cxp, nFolio);
            if (retorno)
                ResponseSender.sendClientSimpleMessage(response, true, "Correcto");
            else
                throw new Exception("Ocurrion un error al guardar la solicitud, revise el presupuesto o consulte al administrador");
        } catch (Exception ex) {
            ResponseSender.sendClientSimpleMessage(response, false, "Ocurrio un error insertando la informacion. Favor de avisar al administrador");
        }
    }
}
