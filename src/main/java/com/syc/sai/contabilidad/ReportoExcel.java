package com.syc.sai.contabilidad;

import com.syc.sai.contabilidad.servlet.DtableToExcel;
import java.io.IOException;
//import java.sql.CallableStatement;
//import java.sql.Connection;
//import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.reportes.ReporteBussinesLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ReportetoExcel", urlPatterns = { "/reports/ReportetoExcel" })
public class ReportoExcel extends DtableToExcel {

    private static final long serialVersionUID = -5034769853645642993L;

    private static final Logger log = LoggerFactory.getLogger(ReportoExcel.class);

    public String getValor(String data) {
        return (data == null ? "" : data);
    }

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
        cCentroContable = u.getPropiedad("CCENTROCONTABLE").getValor();
        llamaReporte(request);
        String filtro = "";
        if (request.getParameter("bTipoReporte").contains("AuxiliarM")) {
            filtro = (request.getParameter("bFiltroReporte")).replace("de la cuenta", "");
        } else {
            filtro = request.getParameter("bFiltroReporte");
        }
        encabezado = new ArrayList<String>();
        encabezado.add("COMISION NACIONAL FORESTAL");
        encabezado.add("SISTEMA DE ADMINISTRACION INTEGRAL");
        encabezado.add("CONTABILIDAD");
        encabezado.add("" + request.getParameter("bhcCentroContable"));
        encabezado.add("");
        encabezado.add(filtro);
        encabezado.add("");
        INICIANDATOS = 7;
        fechaRight = true;
        try {
            generarExcel(response, request);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void llamaReporte(HttpServletRequest request) {
        if ("Balanza".equalsIgnoreCase(request.getParameter("bTipoReporte")))
            balanza(request);
        else if ("Auxiliares".equalsIgnoreCase(request.getParameter("bTipoReporte")))
            auxiliar(request);
        else if (request.getParameter("bTipoReporte").contains("Analitico"))
            analitico(request);
        else if (request.getParameter("bTipoReporte").contains("AuxiliarM"))
            auxiliarM(request);
    }

    private void balanza(HttpServletRequest request) {
        iniciar();
        String DepuraColumnas = getValor(request.getParameter("bDepuraColumnas"));
        String[] NombreCols;
        String[] FormatoCols;
        if ((DepuraColumnas.compareTo("S") == 0) || (DepuraColumnas.compareTo("") == 0)) {
            DepuraColumnas = "S";
            setnumCols(new int[] { 8 });
            NombreCols = new String[] { "CUENTA", "DESCRIPCION", "SALDO INICIAL DEUDOR", "SALDO INICIAL ACREEDOR", "MOVIMIENTOS ACUMULADOS DEUDOR", "MOVIMIENTOS ACUMULADOS ACREEDOR", "SALDO FINAL DEUDOR", "SALDO FINAL ACREEDOR" };
            FormatoCols = new String[] { "", "", "Double", "Double", "Double", "Double", "Double", "Double" };
            setTitulos(new String[] { "Balanza Corta" });
        } else {
            setnumCols(new int[] { 9 });
            NombreCols = new String[] { "CUENTA", "DESCRIPCION", "SALDO INICIAL", "MOVIMIENTOS ACUMULADOS DEBE", "MOVIMIENTOS ACUMULADOS HABER", "SALDO MES ANTERIOR", "MOVIMIENTOS DEBE DEL MES", "MOVIMIENTOS HABER DEL MES", "SALDO FINAL" };
            FormatoCols = new String[] { "", "", "Double", "Double", "Double", "Double", "Double", "Double", "Double" };
            setTitulos(new String[] { "Balanza Ampliada" });
        }
        Todo.add(new ArrayList<String>(Arrays.asList(NombreCols)));
        Todo.add(new ArrayList<String>(Arrays.asList(FormatoCols)));
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
        ArrayList<String> data = (ArrayList<String>) rbl.ReporteBalanzaList(buscaCuentaIni, buscaCuentaFin, aEjercicioFiscal, DepuraLineas, DepuraColumnas, cCentroContable, TipoReporte, mesIni, mesFin, FiltroSubcuenta);
        Todo.add(data);
    }

    private void auxiliar(HttpServletRequest request) {
        iniciar();
        String[] NombreCols = null;
        String[] FormatoCols = null;
        setnumCols(new int[] { 12 });
        NombreCols = new String[] { "TIPO", "NUM", "CxP", "FECHA", "CUENTA", "SUBCUENTA", "CHEQUE", "CONCEPTO", "REFERENCIA", "CARGOS", "ABONOS", "SALDO" };
        FormatoCols = new String[] { "", "", "", "", "", "", "", "", "", "Double", "Double", "Double" };
        setTitulos(new String[] { "Auxiliares" });
        Todo.add(new ArrayList<String>(Arrays.asList(NombreCols)));
        Todo.add(new ArrayList<String>(Arrays.asList(FormatoCols)));
        String swhere = getValor(request.getParameter("bswhere"));
        String cCentroContable = getValor(request.getParameter("bcCentroContable"));
        String buscaCuentaIni = getValor(request.getParameter("bbuscaCuentaIni"));
        String fAuxIni = getValor(request.getParameter("bfAuxIni"));
        String fAuxFin = getValor(request.getParameter("bfAuxFin"));
        ReporteBussinesLogic rbl = new ReporteBussinesLogic("jdbc/gestion");
        ArrayList<String> data = (ArrayList<String>) rbl.ReporteAuxiliaresList(swhere, cCentroContable, buscaCuentaIni, fAuxIni, fAuxFin);
        Todo.add(data);
    }

    private void auxiliarM(HttpServletRequest request) {
        iniciar();
        String[] NombreCols = null;
        String[] FormatoCols = null;
        setnumCols(new int[] { 15 });
        NombreCols = new String[] { "TIPO", "NUM", "CC", "CxP", "FECHA", "CUENTA", "SUBCUENTA", "RFC", "NOMBRE", "CONCEPTO", "DESCRIPCION", "CARGOS", "ABONOS", "SALDOS", "APLICADO" };
        FormatoCols = new String[] { "", "", "", "", "", "", "", "", "", "", "", "Double", "Double", "Double", "" };
        setTitulos(new String[] { "Auxiliar a Mayor" });
        Todo.add(new ArrayList<String>(Arrays.asList(NombreCols)));
        Todo.add(new ArrayList<String>(Arrays.asList(FormatoCols)));
        String buscaCtaMayor = getValor(request.getParameter("CuentaAuxMayor"));
        String buscaSubCtaMayor = getValor(request.getParameter("SubCuentaAuxMayor"));
        String cCentroContable = getValor(request.getParameter("CentroContAux"));
        String fAuxIni = getValor(request.getParameter("fAuxMayorIni"));
        String fAuxFin = getValor(request.getParameter("fAuxMayorFin"));
        String subtot = getValor(request.getParameter("cSubtotal"));
        ReporteBussinesLogic rbl = new ReporteBussinesLogic("jdbc/gestion");
        ArrayList<String> data = (ArrayList<String>) rbl.ReporteAuxiliarMayor(buscaCtaMayor, buscaSubCtaMayor, cCentroContable, fAuxIni, fAuxFin, subtot);
        Todo.add(data);
        ArrayList<String> dataSaldos = (ArrayList<String>) rbl.ReporteAuxiliarMayorSaldos(buscaCtaMayor, buscaSubCtaMayor, cCentroContable, fAuxIni, fAuxFin);
        encabezadoSaldos = new ArrayList<String>();
        if (dataSaldos.size() > 0) {
            encabezadoSaldos.add(dataSaldos.get(0));
            encabezadoSaldos.add(dataSaldos.get(1));
            encabezadoSaldos.add(dataSaldos.get(2));
            encabezadoSaldos.add(dataSaldos.get(3));
        }
    }

    private void analitico(HttpServletRequest request) {
        iniciar();
        String DepuraColumnas = getValor(request.getParameter("bDepuraColumnas"));
        String TipoSubCuentaC = request.getParameter("bTipoSubCuentaC") + "";
        String cCentroContable = getValor(request.getParameter("bcCentroContable"));
        String TipoReporte = getValor(request.getParameter("bTipoReporte"));
        String[] NombreCols;
        String[] FormatoCols;
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
        } else if (cCentroContable.equals("00") && TipoReporte.equals("AnaliticoEC")) {
            primerCol = "C.C.";
            segundaCol = "DESCRIPCION";
        } else {
            primerCol = "CUENTA";
            segundaCol = "DESCRIPCION";
        }
        if ((DepuraColumnas.compareTo("S") == 0) || (DepuraColumnas.compareTo("") == 0)) {
            DepuraColumnas = "S";
            setnumCols(new int[] { 8 });
            NombreCols = new String[] { primerCol, segundaCol, "SALDO INICIAL DEUDOR", "SALDO INICIAL ACREEDOR", "MOVIMIENTOS ACUMULADOS DEUDOR", "MOVIMIENTOS ACUMULADOS ACREEDOR", "SALDO FINAL DEUDOR", "SALDO FINAL ACREEDOR" };
            FormatoCols = new String[] { "", "", "Double", "Double", "Double", "Double", "Double", "Double" };
            setTitulos(new String[] { "Analitico" });
        } else {
            setnumCols(new int[] { 9 });
            NombreCols = new String[] { primerCol, segundaCol, "SALDO INICIAL", "MOVIMIENTOS ACUMULADOS DEBE", "MOVIMIENTOS ACUMULADOS HABER", "SALDO MES ANTERIOR", "MOVIMIENTOS DEBE DEL MES", "MOVIMIENTOS HABER DEL MES", "SALDO FINAL" };
            FormatoCols = new String[] { "", "", "Double", "Double", "Double", "Double", "Double", "Double", "Double" };
            setTitulos(new String[] { "Analitico" });
        }
        Todo.add(new ArrayList<String>(Arrays.asList(NombreCols)));
        Todo.add(new ArrayList<String>(Arrays.asList(FormatoCols)));
        String DepuraLineas = getValor(request.getParameter("bDepuraLineas"));
        String mesIni = getValor(request.getParameter("bmesIni"));
        String mesFin = getValor(request.getParameter("bmesFin"));
        String FiltroSubcuenta = getValor(request.getParameter("bFiltroSubcuenta"));
        String buscaCuentaIni = getValor(request.getParameter("bbuscaCuentaIni"));
        String buscaCuentaFin = getValor(request.getParameter("bbuscaCuentaFin"));
        String aEjercicioFiscal = getValor(request.getParameter("baEjercicioFiscal"));
        ReporteBussinesLogic rbl = new ReporteBussinesLogic("jdbc/gestion");
        ArrayList<String> data = (ArrayList<String>) rbl.ReporteBalanzaList(buscaCuentaIni, buscaCuentaFin, aEjercicioFiscal, DepuraLineas, DepuraColumnas, cCentroContable, TipoReporte, mesIni, mesFin, FiltroSubcuenta);
        Todo.add(data);
    }
}
