package com.syc.sai.contabilidad.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.reportes.ReporteBussinesLogic;
import com.syc.gestion.servlet.GestionInterface;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "ReportetoHtml", urlPatterns = { "/reports/ReportetoHtml" })
public class RptHtml extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = -5034769853645642993L;

    protected String html = new String();

    protected int numCols = 0;

    protected String[] nombreCols;

    protected String[] formatoCols;

    protected ArrayList<String> datos = new ArrayList<String>();

    protected String titulo = "";

    protected String centroContable = "";

    protected String filtroReporte = "";

    protected SimpleDateFormat fecha = new SimpleDateFormat("EEEE d' de 'MMMM' del 'yyyy", new Locale("es", "MX"));

    protected Calendar gc = GregorianCalendar.getInstance();

    protected String fechaFormateada = fecha.format(gc.getTime()).toUpperCase();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            sendError(response, "Session Terminada. Ingrese nuevamente al sistema");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            sendError(response, "No hay usuario en session. Ingrese nuevamente al sistema");
            return;
        }
        llamaReporte(request);
        printHtml(response);
    }

    public String getValor(String data) {
        return (data == null ? "" : data);
    }

    private void llamaReporte(HttpServletRequest request) {
        centroContable = request.getParameter("bhcCentroContable");
        filtroReporte = request.getParameter("bFiltroReporte");
        if (request.getParameter("bTipoReporte").equalsIgnoreCase("Balanza"))
            balanza(request);
        else if (request.getParameter("bTipoReporte").equalsIgnoreCase("Auxiliares"))
            auxiliar(request);
        else if (request.getParameter("bTipoReporte").contains("Analitico"))
            analitico(request);
        else if (request.getParameter("bTipoReporte").contains("AuxiliarM"))
            auxiliarM(request);
    }

    protected void printHtml(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.addHeader("Content-Disposition", "inline; filename=\"reporte" + "rpt" + "_" + System.currentTimeMillis() + ".html\";");
        PrintWriter pw = response.getWriter();
        pw.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
        pw.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        pw.println("<head>");
        pw.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
        pw.println("<link rel=\"stylesheet\" type=\"text/css\" 	href=\"../css/vistaPrevia.css\"></link>");
        pw.println("<title>Vista Previa</title>");
        pw.println("</head>");
        pw.println("<body>");
        pw.println("<table border=\"0\">");
        pw.println("<tr>");
        pw.println("<td ><img src=\"../imagenes/logotipo-usuario.png\"></td>");
        pw.println("<td ><div align=\"center\">");
        pw.println("COMISION NACIONAL FORESTAL ");
        pw.println("<br>");
        pw.println("SISTEMA DE ADMINISTRACI&Oacute;N INTEGRAL <br>");
        pw.println("CONTABILIDAD");
        pw.println("<br><br>");
        pw.println(centroContable);
        pw.println("<br><br>");
        pw.println(new String(filtroReporte.getBytes("ISO-8859-1"), "UTF-8") + "</div></td>");
        pw.println("<td >" + fechaFormateada + "</td>");
        pw.println("</tr>");
        pw.println("</table>");
        pw.println("<table  border=\"0\">");
        pw.println("<tr id=\"title\">");
        for (int i = 0; i < numCols; i++) pw.println("<th>" + nombreCols[i] + "</th>");
        pw.println("</tr>");
        int colNow = 1;
        int par = 0;
        DecimalFormat formateador = new DecimalFormat("###,##0.00");
        for (int j = 0; j < datos.size(); j++) {
            if (colNow == 1 && verificarCorteSubtotal(j))
                pw.println("<tr class=\"subtotal\">");
            else if (colNow == 1 && verificarCorteTotal(j))
                pw.println("<tr class=\"total\">");
            else if (colNow == 1 && ((par % 2) == 0))
                pw.println("<tr class=\"lineaImpar\">");
            else if (colNow == 1)
                pw.println("<tr>");
            pw.println(formatoCols[colNow - 1].equalsIgnoreCase("Double") ? "<td style='mso-number-format:\"Standard\"'>" + formateador.format(Double.parseDouble(datos.get(j).equals("") ? "0.0" : datos.get(j))) + "</td>" : "<td>" + datos.get(j) + "</td>");
            if ((colNow) == numCols) {
                pw.println("</tr>");
                colNow = 0;
                par++;
            }
            colNow++;
        }
        pw.println("</table>");
        pw.println("<p>&nbsp;</p>");
        pw.println("</body>");
        pw.println("</html>");
    }

    private boolean verificarCorteSubtotal(int inicio) {
        for (int i = 0; i < numCols; i++) if (datos.get(inicio + i).equalsIgnoreCase("SUBTOTAL"))
            return true;
        return false;
    }

    private boolean verificarCorteTotal(int inicio) {
        for (int i = 0; i < numCols; i++) if (datos.get(inicio + i).contains("TOTAL"))
            return true;
        return false;
    }

    private void balanza(HttpServletRequest request) {
        String DepuraColumnas = getValor(request.getParameter("bDepuraColumnas"));
        if ((DepuraColumnas.compareTo("S") == 0) || (DepuraColumnas.compareTo("") == 0)) {
            DepuraColumnas = "S";
            numCols = 8;
            nombreCols = new String[] { "CUENTA", "DESCRIPCION", "SALDO INICIAL DEUDOR", "SALDO INICIAL ACREEDOR", "MOVIMIENTOS ACUMULADOS DEUDOR", "MOVIMIENTOS ACUMULADOS ACREEDOR", "SALDO FINAL DEUDOR", "SALDO FINAL ACREEDOR" };
            formatoCols = new String[] { "", "", "Double", "Double", "Double", "Double", "Double", "Double" };
            titulo = "Balanza Corta";
        } else {
            numCols = 9;
            nombreCols = new String[] { "CUENTA", "DESCRIPCION", "SALDO INICIAL", "MOVIMIENTOS ACUMULADOS DEBE", "MOVIMIENTOS ACUMULADOS HABER", "SALDO MES ANTERIOR", "MOVIMIENTOS DEBE DEL MES", "MOVIMIENTOS HABER DEL MES", "SALDO FINAL" };
            formatoCols = new String[] { "", "", "Double", "Double", "Double", "Double", "Double", "Double", "Double" };
            titulo = "Balanza Ampliada";
        }
        String DepuraLineas = getValor(request.getParameter("bDepuraLineas"));
        String mesIni = getValor(request.getParameter("bmesIni"));
        String mesFin = getValor(request.getParameter("bmesFin"));
        String FiltroSubcuenta = getValor(request.getParameter("bFiltroSubcuenta"));
        String buscaCuentaIni = getValor(request.getParameter("bbuscaCuentaIni"));
        String buscaCuentaFin = getValor(request.getParameter("bbuscaCuentaFin"));
        String aEjercicioFiscal = getValor(request.getParameter("baEjercicioFiscal"));
        String TipoReporte = getValor(request.getParameter("bTipoReporte"));
        String cCentroContable = getValor(request.getParameter("bcCentroContable"));
        ReporteBussinesLogic rbl = new ReporteBussinesLogic("jdbc/gestion");
        datos = (ArrayList<String>) rbl.ReporteBalanzaList(buscaCuentaIni, buscaCuentaFin, aEjercicioFiscal, DepuraLineas, DepuraColumnas, cCentroContable, TipoReporte, mesIni, mesFin, FiltroSubcuenta);
    }

    private void auxiliar(HttpServletRequest request) {
        numCols = 12;
        nombreCols = new String[] { "TIPO", "NUM", "CxP", "FECHA", "CUENTA", "SUBCUENTA", "CHEQUE", "CONCEPTO", "REFERENCIA", "CARGOS", "ABONOS", "SALDO" };
        formatoCols = new String[] { "", "", "", "", "", "", "", "", "", "Double", "Double", "Double" };
        titulo = "Auxiliares";
        String swhere = getValor(request.getParameter("bswhere"));
        String cCentroContable = getValor(request.getParameter("bcCentroContable"));
        String buscaCuentaIni = getValor(request.getParameter("bbuscaCuentaIni"));
        String fAuxIni = getValor(request.getParameter("bfAuxIni"));
        String fAuxFin = getValor(request.getParameter("bfAuxFin"));
        ReporteBussinesLogic rbl = new ReporteBussinesLogic("jdbc/gestion");
        datos = (ArrayList<String>) rbl.ReporteAuxiliaresList(swhere, cCentroContable, buscaCuentaIni, fAuxIni, fAuxFin);
    }

    private void auxiliarM(HttpServletRequest request) {
        numCols = 14;
        nombreCols = new String[] { "TIPO", "NUM", "CC", "CxP", "FECHA", "CUENTA", "SUBCUENTA", "RFC", "NOMBRE", "CONCEPTO", "DESCRIPCION", "CARGOS", "ABONOS", "APLICADO" };
        formatoCols = new String[] { "", "", "", "", "", "", "", "", "", "", "", "Double", "Double", "" };
        titulo = "Auxiliar a Mayor";
        String buscaCtaMayor = getValor(request.getParameter("CuentaAuxMayor"));
        String buscaSubCtaMayor = getValor(request.getParameter("SubCuentaAuxMayor"));
        String cCentroContable = getValor(request.getParameter("CentroContAux"));
        String fAuxIni = getValor(request.getParameter("fAuxMayorIni"));
        String fAuxFin = getValor(request.getParameter("fAuxMayorFin"));
        String subtot = getValor(request.getParameter("cSubtotal"));
        ReporteBussinesLogic rbl = new ReporteBussinesLogic("jdbc/gestion");
        datos = (ArrayList<String>) rbl.ReporteAuxiliarMayor(buscaCtaMayor, buscaSubCtaMayor, cCentroContable, fAuxIni, fAuxFin, subtot);
    }

    private void analitico(HttpServletRequest request) {
        String DepuraColumnas = getValor(request.getParameter("bDepuraColumnas"));
        String TipoSubCuentaC = request.getParameter("bTipoSubCuentaC") + "";
        System.out.println(TipoSubCuentaC);
        String primerCol = "";
        String segundaCol = "";
        if (TipoSubCuentaC.equalsIgnoreCase("ALM")) {
            primerCol = "NUMERO";
            segundaCol = "ALMACEN";
        } else if (TipoSubCuentaC.equalsIgnoreCase("CTAB")) {
            primerCol = "CUENTA";
            segundaCol = "CTA. BANCARIA";
        } else if (TipoSubCuentaC.equalsIgnoreCase("RFC")) {
            primerCol = "RFC";
            segundaCol = "NOMBRE";
        } else {
            primerCol = "CUENTA";
            segundaCol = "DESCRIPCION";
        }
        if ((DepuraColumnas.compareTo("S") == 0) || (DepuraColumnas.compareTo("") == 0)) {
            DepuraColumnas = "S";
            numCols = 8;
            nombreCols = new String[] { primerCol, segundaCol, "SALDO INICIAL DEUDOR", "SALDO INICIAL ACREEDOR", "MOVIMIENTOS ACUMULADOS DEUDOR", "MOVIMIENTOS ACUMULADOS ACREEDOR", "SALDO FINAL DEUDOR", "SALDO FINAL ACREEDOR" };
            formatoCols = new String[] { "", "", "Double", "Double", "Double", "Double", "Double", "Double" };
            titulo = "Analitico";
        } else {
            numCols = 9;
            nombreCols = new String[] { primerCol, segundaCol, "SALDO INICIAL", "MOVIMIENTOS ACUMULADOS DEBE", "MOVIMIENTOS ACUMULADOS HABER", "SALDO MES ANTERIOR", "MOVIMIENTOS DEBE DEL MES", "MOVIMIENTOS HABER DEL MES", "SALDO FINAL" };
            formatoCols = new String[] { "", "", "Double", "Double", "Double", "Double", "Double", "Double", "Double" };
            titulo = "Analitico";
        }
        String DepuraLineas = getValor(request.getParameter("bDepuraLineas"));
        String mesIni = getValor(request.getParameter("bmesIni"));
        String mesFin = getValor(request.getParameter("bmesFin"));
        String FiltroSubcuenta = getValor(request.getParameter("bFiltroSubcuenta"));
        String buscaCuentaIni = getValor(request.getParameter("bbuscaCuentaIni"));
        String buscaCuentaFin = getValor(request.getParameter("bbuscaCuentaFin"));
        String aEjercicioFiscal = getValor(request.getParameter("baEjercicioFiscal"));
        String TipoReporte = getValor(request.getParameter("bTipoReporte"));
        String cCentroContable = getValor(request.getParameter("bcCentroContable"));
        ReporteBussinesLogic rbl = new ReporteBussinesLogic("jdbc/gestion");
        datos = (ArrayList<String>) rbl.ReporteBalanzaList(buscaCuentaIni, buscaCuentaFin, aEjercicioFiscal, DepuraLineas, DepuraColumnas, cCentroContable, TipoReporte, mesIni, mesFin, FiltroSubcuenta);
    }

    protected static void sendError(HttpServletResponse resp, String msg) throws IOException {
        PrintWriter out = resp.getWriter();
        out.println("<html>");
        out.println("\t<body>");
        out.println("\t\t<h1>Se presento el siguiente problema mientras se llenaba el reporte</h1><br>");
        out.println("\t\t<br>" + msg + "<br>");
        out.println("\t</body>");
        out.println("</html>");
        out.flush();
        out.close();
    }
}
