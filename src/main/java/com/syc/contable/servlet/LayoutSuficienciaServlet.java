package com.syc.contable.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import com.syc.contable.CompromisoBussinessLogic;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "LayoutSuficienciaServlet", urlPatterns = { "/gstnmngr/generaLayoutSuficiencia", "/compromiso/cancelaSuficiencia" })
public class LayoutSuficienciaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger log = Logger.getLogger(LayoutSuficienciaServlet.class);

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String msgRetorno = "";
        ArrayList<String> arrListPago = null;
        File filename = null;
        String nombre = "";
        try {
            String tipoLayout = (request.getParameter("tipoLayout") != null) ? request.getParameter("tipoLayout").trim() : "";
            String reimprimir = (request.getParameter("reimprimir") != null) ? request.getParameter("reimprimir").trim() : "";
            DateFormat fecha = new SimpleDateFormat("yyyyMMddhhmmsss");
            String sufijo = fecha.format(new Date(System.currentTimeMillis()));
            CompromisoBussinessLogic cmpbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
            String cxpIntegrada = (request.getParameter("coIntegrada") != null) ? request.getParameter("coIntegrada").trim() : "";
            String es = request.getParameter("esIntegrado");
            int esIntegrada = es.isEmpty() ? 0 : Integer.parseInt(request.getParameter("esIntegrado"));
            String esCalendario = request.getParameter("esCalendario");
            if (tipoLayout.equals("1")) {
                // Folios suficiencia
                String foliosSuf = "";
                if (es.equals("0")) {
                    String folios = (request.getParameter("foliosSuficiencia") != null) ? request.getParameter("foliosSuficiencia").trim() : "";
                    int valor = folios.length() - 1;
                    foliosSuf = folios.substring(0, valor);
                }
                nombre = "LayoutSuficiencia";
                filename = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + nombre + sufijo.trim() + ".csv");
                arrListPago = cmpbl.layoutSuficiencia(foliosSuf, reimprimir, esIntegrada, cxpIntegrada, esCalendario);
            } else if (tipoLayout.equals("2")) {
                String foliosComp = "";
                // Folios compromiso
                if (es.equals("0")) {
                    String foliosC = (request.getParameter("foliosCompromisos") != null) ? request.getParameter("foliosCompromisos").trim() : "";
                    int valor2 = foliosC.length() - 1;
                    foliosComp = foliosC.substring(0, valor2);
                }
                nombre = "LayoutCompromiso";
                filename = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + nombre + sufijo.trim() + ".csv");
                arrListPago = cmpbl.layoutCompromisos(foliosComp, reimprimir, esIntegrada, cxpIntegrada);
            } else if (tipoLayout.equals("3")) {
                //extracción de solicitudes de pago
                String cxp = (request.getParameter("cxp") != null) ? request.getParameter("cxp").trim() : "";
                nombre = "ExtraccionExcel";
                filename = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + nombre + sufijo.trim() + ".csv");
                arrListPago = cmpbl.extraerExcel(cxp);
            } else if (tipoLayout.equals("4")) {
                // Folios compromiso
                String foliosC = (request.getParameter("foliosCalendario") != null) ? request.getParameter("foliosCalendario").trim() : "";
                int valor2 = foliosC.length() - 1;
                String foliosComp = foliosC.substring(0, valor2);
                nombre = "LayoutCalendario";
                filename = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + nombre + sufijo.trim() + ".csv");
                arrListPago = cmpbl.layoutCompromisosCalendario(foliosComp, reimprimir);
            } else if (tipoLayout.equals("5")) {
                // Folios compromiso
                String folio = (request.getParameter("foliosSuficiencia") != null) ? request.getParameter("foliosSuficiencia").trim() : "";
                nombre = "LayoutSuficienciaRed";
                filename = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + nombre + sufijo.trim() + ".csv");
                arrListPago = cmpbl.layoutSuficienciaCalendario(folio, reimprimir);
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            StringBuffer archivoPago = new StringBuffer();
            for (int i = 0; i < arrListPago.size(); i++) {
                archivoPago.append(arrListPago.get(i));
            }
            String outTextPago = archivoPago.toString();
            out.write(outTextPago);
            out.close();
            try (ServletOutputStream outputStream = response.getOutputStream();
                FileInputStream fileInputStream = new FileInputStream(filename)) {
                response.setContentType("text/csv; charset=UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + filename.getName() + "\"");
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            } catch (IOException e) {
                log.error(e, e);
                throw e;
            }
            filename.delete();
            return;
        } catch (Exception e) {
            log.error(e, e);
            msgRetorno = "Ocurrio el siguiente error al generar el layout: " + e.getMessage();
        }
        if (msgRetorno != null) {
            session.setAttribute("RESULT", msgRetorno);
            response.sendRedirect(request.getContextPath() + "/Generador/IntegraLayoutSuficiencia.jsp");
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String folio = (request.getParameter("folio") != null) ? request.getParameter("folio").trim() : "";
            String tipo = (request.getParameter("tipo") != null) ? request.getParameter("tipo").trim() : "";
            String folioSICOP = (request.getParameter("folioSICOP") != null) ? request.getParameter("folioSICOP").trim() : "";
            CompromisoBussinessLogic cmpbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
            if (tipo.equalsIgnoreCase("cancelacion")) {
                cmpbl.cancelarPrecompSuficiencia(folio);
            } else if (tipo.equalsIgnoreCase("aplicacion")) {
                cmpbl.aplicarCompromisoDirecto(folio, folioSICOP);
            }
        } catch (Exception e) {
            log.error(e, e);
            String msgRetorno = "Ocurrió un error al cancelar la suficiencia: " + e.getMessage();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/plain");
            response.getWriter().write(msgRetorno);
        }
    }
}
