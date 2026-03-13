package com.syc.contable.servlet;

import java.io.IOException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.contable.PolizaBussinessLogic;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "EditaPolizaServlet", urlPatterns = { "/poliza/EditaPoliza" })
public class EditaPolizaServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = -2826694655683478567L;

    public static final Logger log = LoggerFactory.getLogger(EditaPolizaServlet.class);

    private String[] htmlResp = { " <!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"> " + " <HTML> " + "   <HEAD><TITLE>A Servlet</TITLE></HEAD> " + " <script type=\"text/javascript\" for=\"window\" event=\"onunload\"> " + " 	if (!bClicBtn) { " + " 		regresar(); " + " 	} " + " </script> " + " <SCRIPT languaje=\"javascript\"> " + " 	var bClicBtn = false; " + "   		   function regresar(){ " + "   					opener.parent.document.getElementById(\"pb_send\").disabled = false; " + "   					opener.comprobacionPoliza(); " + "   					opener.parent.document.getElementById(\"pb_send\").click(); " + "   				window.close(); " + "   		   } " + " </SCRIPT> " + "   <BODY> ", "", "</BODY> " + " </HTML>" };

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect("../index.jsp");
        }
        String action = req.getParameter("action");
        if ("EDITAR".equals(action)) {
            try {
                String nFolioDocumento = req.getParameter("nFolioDocumento");
                String nFolioPoliza = req.getParameter("nFolioPoliza");
                String cTipoDocumento = req.getParameter("cTipoDocumento");
                String cCentroContable = req.getParameter("cCentroContable");
                String cTipoPoliza = req.getParameter("cTipoPoliza");
                String aEjercicioFiscal = req.getParameter("aEjercicioFiscal");
                PolizaBussinessLogic pbl = new PolizaBussinessLogic();
                pbl.reEditaPoliza(nFolioDocumento, nFolioPoliza, cTipoDocumento, cCentroContable, cTipoPoliza, aEjercicioFiscal);
                RequestDispatcher dispatch = req.getRequestDispatcher("../gstnmngr/gestion?cmd=1");
                dispatch.forward(req, resp);
                // resp.sendRedirect("../gstnmngr/gestion?cmd=1");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else if ("ACTUALIZAR".equals(action)) {
            PolizaBussinessLogic pbl = new PolizaBussinessLogic();
            try {
                String nFolioDocPoliza = req.getParameter("nFolioDocPoliza");
                String cCentroContable = req.getParameter("cCentroContable");
                String cTipoPoliza = req.getParameter("cTipoPoliza");
                String aEjercicioFiscal = req.getParameter("aEjercicioFiscal");
                pbl.terminaEditaPoliza(Long.parseLong(nFolioDocPoliza), cCentroContable, cTipoPoliza, aEjercicioFiscal);
                htmlResp[1] = "DOCUMENTO DE POLIZA APLICADO CONTABLEMENTE<br><input type=\"button\" value=\"Regresar\" onclick=\"bClicBtn = true;regresar();\"/>";
                ServletOutputStream out = resp.getOutputStream();
                for (int i = 0; i < htmlResp.length; i++) out.println(htmlResp[i]);
                out.flush();
                out.close();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else if (("CAMBIA_ESTATUS").equals(action)) {
            PolizaBussinessLogic pbl = new PolizaBussinessLogic();
            try {
                String nFolioPoliza = req.getParameter("nFolioPoliza");
                String cCentroContable = req.getParameter("cCentroContable");
                String cTipoPoliza = req.getParameter("cTipoPoliza");
                String aEjercicioFiscal = req.getParameter("aEjercicioFiscal");
                String polizaEstatus = req.getParameter("polizaEstatus");
                int r = pbl.cambiaEstatusPoliza(nFolioPoliza, cCentroContable, cTipoPoliza, aEjercicioFiscal, polizaEstatus);
                String res = "{\"success\":\"" + (r > 0) + "\"}";
                resp.setContentType("application/json");
                ServletOutputStream out = resp.getOutputStream();
                out.print(res);
                out.flush();
                out.close();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else if ("ACTUALIZA_DETALLE".equals(action)) {
            PolizaBussinessLogic pbl = new PolizaBussinessLogic();
            try {
                String nFolioDocPoliza = req.getParameter("nFolioDocPoliza");
                String cCentroContable = req.getParameter("cCentroContable");
                String cTipoPoliza = req.getParameter("cTipoPoliza");
                String aEjercicioFiscal = req.getParameter("aEjercicioFiscal");
                pbl.actualizaMovimientosPoliza(Long.parseLong(nFolioDocPoliza), cCentroContable, cTipoPoliza, aEjercicioFiscal);
                String res = "{\"success\":\"true\"}";
                resp.setContentType("application/json");
                ServletOutputStream out = resp.getOutputStream();
                out.print(res);
                out.flush();
                out.close();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
