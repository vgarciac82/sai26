package com.syc.contable.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.contable.AccountingEngine;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "RectificacionesServlet", urlPatterns = { "/servlet/RectificacionesServlet" })
public class RectificacionesServlet extends HttpServlet {

    private static final long serialVersionUID = 3830246252504144684L;

    private static final Logger log = LoggerFactory.getLogger(RectificacionesServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String mensaje = "";
        String cancelaDocumento = request.getParameter("cancelaDocumento");
        String cancelaDocumentoMil = request.getParameter("cancelaDocumentoMil");
        if (cancelaDocumento != null && !"".equals(cancelaDocumento) && "true".equals(cancelaDocumento)) {
            try {
                String folio = request.getParameter("folio");
                if (folio != null && !"".equals(folio)) {
                    mensaje += cancelaRectificacion(folio);
                } else {
                    mensaje += "Todos los campos deben llenarse";
                }
            } catch (Exception ex) {
                mensaje += ex.toString();
            } catch (IllegalAccessError e) {
                mensaje += e.toString();
            } finally {
                response.sendRedirect("../plantillasCasos/cancelaDocumentoManual.jsp?mensaje=" + mensaje);
            }
        }
        if (cancelaDocumentoMil != null && !"".equals(cancelaDocumentoMil) && "true".equals(cancelaDocumentoMil)) {
            try {
                String folio = request.getParameter("folio");
                if (folio != null && !"".equals(folio)) {
                    mensaje += cancelaRectificacionMil(folio);
                } else {
                    mensaje += "Todos los campos deben llenarse";
                }
            } catch (Exception ex) {
                mensaje += ex.toString();
            } catch (IllegalAccessError e) {
                mensaje += e.toString();
            } finally {
                response.sendRedirect("../plantillasCasos/cancelaDocumentoManual.jsp?mensaje=" + mensaje);
            }
        }
    }

    public String cancelaRectificacion(String folio) throws SQLException {
        Connection conn = null;
        String mensaje = "";
        try {
            AccountingEngine ae = new AccountingEngine();
            conn = ae.getConnection();
            //CANCELAR RECTIFICACIONAUT
            boolean rectificacionAutResultado = ae.cancelAccountingApplication(conn, "RECTIFICACIONAUT", folio, "tRectificacionAutEncabezado", "tRectificacionAutDetalle", "nFolioRectificacionAut");
            mensaje += "Cancela rectificacionAut Resultado:" + new Boolean(rectificacionAutResultado).toString();
            //CANCELAR RECTIFICACION
            boolean rectificacionResultado = ae.cancelAccountingApplication(conn, "RECTIFICACION", folio, "tRectificacionEncabezado", "tRectificacionDetalle", "nFolioRectificacion");
            mensaje += ", Cancela rectificacion Resultado:" + new Boolean(rectificacionResultado).toString();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            mensaje += "No ha sido posible realizar la cancelación <br> <br> " + e.toString();
            conn.rollback();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return mensaje;
    }

    public String cancelaRectificacionMil(String folio) throws SQLException {
        Connection conn = null;
        String mensaje = "";
        try {
            AccountingEngine ae = new AccountingEngine();
            conn = ae.getConnection();
            //CANCELAR RECTIFICACIONAUT
            boolean rectificacionAutResultado = ae.cancelAccountingApplication(conn, "RECTIFICACIONAUTMIL", folio, "tRectificacionAutEncabezadoMil", "tRectificacionAutDetalleMil", "nFolioRectificacionMilAut");
            mensaje += "Cancela rectificacionAut Resultado:" + new Boolean(rectificacionAutResultado).toString();
            //CANCELAR RECTIFICACION
            boolean rectificacionResultado = ae.cancelAccountingApplication(conn, "RECTIFICACIONMIL", folio, "tRectificacionEncabezadoMil", "tRectificacionDetalleMil", "nFolioRectificacionMil");
            mensaje += ", Cancela rectificacion Resultado:" + new Boolean(rectificacionResultado).toString();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            mensaje += "No ha sido posible realizar la cancelación <br> <br> " + e.toString();
            conn.rollback();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return mensaje;
    }
}
