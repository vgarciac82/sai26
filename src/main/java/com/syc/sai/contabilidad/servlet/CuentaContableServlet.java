package com.syc.sai.contabilidad.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
//import org.json.JSONArray;
import com.syc.sai.contabilidad.CuentaContable;
import com.syc.sai.contabilidad.CuentaContableBusinessLogic;
import com.syc.sai.contabilidad.polizamanual.CatalogoCabms;
import com.syc.sai.contabilidad.polizamanual.EventoRelacion;
import com.syc.sai.contabilidad.polizamanual.GrupoEvento;
import com.syc.sai.contabilidad.polizamanual.SubGrupoEvento;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "CuentaContableServlet", urlPatterns = { "/CuentaContable/AutoCompletaCuenta", "/CuentaContable/OperacionesCuenta", "/CuentaContable/VerificaBloqueo", "/CuentaContable/autoCompletaGrupoEvento", "/CuentaContable/autoCompletaSubGrupoEvento", "/CuentaContable/selected", "/CuentaContable/autoCompletaEventoRelacion", "/CuentaContable/autoCompletaCABMS", "/CuentaContable/selectCAMBS" })
public class CuentaContableServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(CuentaContableServlet.class);

    private static final long serialVersionUID = -2302925421291178822L;

    private String actualizaCuentas(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        CuentaContable cuenta = readAccountFromRequest(req);
        CuentaContableBusinessLogic cbl = new CuentaContableBusinessLogic();
        String r = "";
        if ("true".equalsIgnoreCase(req.getParameter("onlyDetail")))
            r = cbl.actualizaCuenta(cuenta, true);
        else
            r = cbl.actualizaCuenta(cuenta);
        return r;
    }

    private String altaCuenta(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        CuentaContable cuenta = readAccountFromRequest(req);
        CuentaContableBusinessLogic ccbl = new CuentaContableBusinessLogic();
        String r = ccbl.insertaCuenta(cuenta);
        return r;
    }

    private void autoCompletaCuentas(String term, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        CuentaContable c = new CuentaContable();
        c.setnCuenta(term);
        c.setAplicacionCuenta("S");
        CuentaContableBusinessLogic ccbl = new CuentaContableBusinessLogic();
        List<CuentaContable> l = ccbl.buscaCuentasDeAplicacion(c);
        String arr = "";
        String token = "";
        for (int i = 0; i < l.size(); i++) {
            arr += token + "{\"value\":\"" + l.get(i).getnCuenta() + "\",\"label\":\"" + l.get(i).getnCuenta() + "(" + l.get(i).getdCuenta() + ")" + "\"}";
            token = ",";
        }
        arr = "[" + arr + "]";
        log.debug("Object: {}", arr);
        resp.setContentType("text/html");
        PrintWriter o = resp.getWriter();
        o.print(arr);
        o.flush();
        o.close();
    }

    //*********************** Polizas Eventos Manuales 2014 *********************
    // Ing J. Luis DR
    private void autoCompletaGrupoEvento(String term, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        GrupoEvento aux = new GrupoEvento();
        CuentaContableBusinessLogic bl = new CuentaContableBusinessLogic();
        List<GrupoEvento> l = bl.autoCompletaGrupoEvento(term);
        String arr = "";
        String token = "";
        String json = "{\"value\":\"%s\",\"label\":\"%s\",\"descripcion\":\"%s\"}";
        for (int i = 0; i < l.size(); i++) {
            arr += token + String.format(json, l.get(i).getNidGrupoEvento(), l.get(i).getNidGrupoEvento() + "(" + l.get(i).getCnombreGrupo() + ")", l.get(i).getCnombreGrupo());
            token = ",";
        }
        arr = "[" + arr + "]";
        log.debug("Object: {}", arr);
        req.getSession().setAttribute("nGrupo", term);
        req.getSession().removeAttribute("nSubGrupo");
        req.getSession().removeAttribute("nEvento");
        resp.setContentType("text/html");
        PrintWriter o = resp.getWriter();
        arr = new String(arr.getBytes("UTF-8"), "ISO-8859-1");
        o.print(arr);
        o.flush();
        o.close();
    }

    private void autoCompletaSubGrupoEvento(String term, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        SubGrupoEvento aux = new SubGrupoEvento();
        String json = "{\"value\":\"%s\",\"label\":\"%s\",\"descripcion\":\"%s\"}";
        String arr = "";
        String token = "";
        String nGrupo = (String) req.getSession().getAttribute("nGrupo");
        if (nGrupo != null) {
            CuentaContableBusinessLogic bl = new CuentaContableBusinessLogic();
            List<SubGrupoEvento> l = bl.autoCompletaSubGrupoEvento(nGrupo, term);
            for (int i = 0; i < l.size(); i++) {
                arr += token + String.format(json, l.get(i).getNidSubGrupoEvento(), l.get(i).getNidSubGrupoEvento() + "(" + l.get(i).getCnombreSubGrupo() + ")", l.get(i).getCnombreSubGrupo());
                token = ",";
            }
        } else {
            arr += token + String.format(json, "0", "Debe seleccionar un grupo", "Debe seleccionar un grupo");
        }
        arr = "[" + arr + "]";
        log.debug("Object: {}", arr);
        resp.setContentType("text/html");
        PrintWriter o = resp.getWriter();
        arr = new String(arr.getBytes("UTF-8"), "ISO-8859-1");
        o.print(arr);
        o.flush();
        o.close();
    }

    private void autoCompletaEventoRelacion(String term, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        EventoRelacion aux = new EventoRelacion();
        CuentaContableBusinessLogic bl = new CuentaContableBusinessLogic();
        String nGrupo = (String) req.getSession().getAttribute("nGrupo");
        String nSubGrupo = (String) req.getSession().getAttribute("nSubGrupo");
        String nEvento = term;
        List<EventoRelacion> l = bl.autoCompletaEventoRelacion(nGrupo, nSubGrupo, nEvento);
        String arr = "";
        String token = "";
        String json = "{\"value\":\"%s\",\"label\":\"%s\",\"descripcion\":\"%s\"}";
        if (nGrupo != null) {
            if (nSubGrupo != null) {
                for (int i = 0; i < l.size(); i++) {
                    arr += token + String.format(json, l.get(i).getCevento(), l.get(i).getCevento() + " (" + l.get(i).getDevento() + ")", l.get(i).getDevento());
                    token = ",";
                }
            } else {
                arr += token + String.format(json, "0", "Debe seleccionar un SubGrupo", "Debe seleccionar un SubGrupo");
            }
        } else {
            arr += token + String.format(json, "0", "Debe seleccionar un grupo", "Debe seleccionar un grupo");
        }
        arr = "[" + arr + "]";
        log.debug("Object: {}", arr);
        resp.setContentType("text/html");
        PrintWriter o = resp.getWriter();
        arr = new String(arr.getBytes("UTF-8"), "ISO-8859-1");
        o.print(arr);
        o.flush();
        o.close();
    }

    private void autoCompletaCABMS(String term, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String nGrupo = (String) req.getSession().getAttribute("nGrupo");
        String nSubGrupo = (String) req.getSession().getAttribute("nSubGrupo");
        String nEvento = (String) req.getSession().getAttribute("nEvento");
        String nCABMS = term;
        String arr = "";
        String token = "";
        String json = "{\"value\":\"%s\",\"label\":\"%s\",\"descripcion\":\"%s\"}";
        if (nGrupo != null) {
            if (nSubGrupo != null) {
                if (nEvento != null) {
                    CatalogoCabms aux = new CatalogoCabms();
                    CuentaContableBusinessLogic bl = new CuentaContableBusinessLogic();
                    List<CatalogoCabms> l = bl.autoCompletaCABMS(nGrupo, nSubGrupo, nEvento, nCABMS);
                    for (int i = 0; i < l.size(); i++) {
                        arr += token + String.format(json, l.get(i).getCcabms(), l.get(i).getCcabms() + " (" + l.get(i).getCdescripcion() + ")", l.get(i).getCdescripcion());
                        token = ",";
                    }
                    arr = "[" + arr + "]";
                } else {
                    arr += token + String.format(json, "0", "Debe seleccionar un Evento", "Debe seleccionar un Evento");
                }
            } else {
                arr += token + String.format(json, "0", "Debe seleccionar un SubGrupo", "Debe seleccionar un SubGrupo");
            }
        } else {
            arr += token + String.format(json, "0", "Debe seleccionar un grupo", "Debe seleccionar un grupo");
        }
        log.debug("Object: {}", arr);
        resp.setContentType("text/html");
        PrintWriter o = resp.getWriter();
        o.print(arr);
        o.flush();
        o.close();
    }

    private void selectCABMS(String type, String term, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String id = term;
        String arr = "";
        String token = "";
        String json = "{\"value\":\"%s\",\"label\":\"%s\",\"descripcion\":\"%s\",\"CUCOP\":\"%s\",\"COG\":\"%s\"}";
        CatalogoCabms aux = new CatalogoCabms();
        CuentaContableBusinessLogic bl = new CuentaContableBusinessLogic();
        List<CatalogoCabms> l = bl.selectCABMS(type, id);
        for (int i = 0; i < l.size(); i++) {
            arr += token + String.format(json, l.get(i).getCcabms(), l.get(i).getCcabms() + " (" + l.get(i).getCdescripcion() + ")", l.get(i).getCdescripcion(), l.get(i).getCcucop().toString(), l.get(i).getCpartida());
            token = ",";
        }
        arr = "[" + arr + "]";
        log.debug("Object: {}", arr);
        resp.setContentType("text/html");
        PrintWriter o = resp.getWriter();
        o.print(arr);
        o.flush();
        o.close();
    }

    //**********************************************************************************
    private void buscaCuentas(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        CuentaContable cuenta = readAccountFromRequest(req);
        CuentaContableBusinessLogic ccbl = new CuentaContableBusinessLogic();
        List<CuentaContable> l = ccbl.buscaCuentas(cuenta);
        ResponseSender.sendDatatable(req, resp, l);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            ResponseSender.sendError(resp, "Sin session. Por favor reingrese al sistema.");
            return;
        }
        String accion = req.getParameter("accion");
        if ("".equals(accion) || null == accion) {
            try {
                accion = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1);
            } catch (Exception e) {
                log.warn("Error occurred", "Error obteniendo substring " + e);
            }
            if ("".equals(accion) || null == accion) {
                ResponseSender.sendError(resp, "No se recibio el parametro \"accion\" reporte al administrador");
                return;
            }
        }
        CuentaContableBusinessLogic ccbl = new CuentaContableBusinessLogic();
        if ("VericaBloqueoCuenta".equals(accion)) {
            boolean cBloqueada = false;
            String nCuenta = req.getParameter("nCuenta");
            String nMes = req.getParameter("nMes");
            String cCentroContable = req.getParameter("cCentroContable");
            String operacion = req.getParameter("operacion");
            try {
                cBloqueada = ccbl.cuentaBloqueada(nCuenta, Integer.parseInt(nMes), cCentroContable, operacion);
                ResponseSender.sendResult(resp, String.valueOf(cBloqueada));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                ResponseSender.sendError(resp, "Error mientras se verificaba la cuenta " + nCuenta + "\n" + e.toString());
            }
        } else if ("VerificaCuentas".equals(accion)) {
            String[] cuentasBloqueadas = null;
            try {
                String cuentasCargo = req.getParameter("cuentasCargo");
                String cuentasAbono = req.getParameter("cuentasAbono");
                String cCentroContable = req.getParameter("cCentroContable");
                String nMes = req.getParameter("nMes");
                cuentasBloqueadas = ccbl.cuentasBloqueadas(cuentasCargo, cuentasAbono, cCentroContable, Integer.parseInt(nMes));
                ResponseSender.sendResult(resp, cuentasBloqueadas, "cuenta");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                ResponseSender.sendError(resp, "Error mientras se verificaban las cuentas \n" + e.toString());
            }
        } else if ("AltaCuentas".equals(accion)) {
            try {
                String msg = altaCuenta(req, resp);
                ResponseSender.sendArrayMessages(resp, true, new String[] { "Atenci&oacute;n", msg }, "msg");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                ResponseSender.sendError(resp, "Error mientras se agregaba la cuenta \n" + e.toString());
            }
        } else if ("BuscaCuentas".equals(accion)) {
            try {
                buscaCuentas(req, resp);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                ResponseSender.sendError(resp, e.toString());
            }
        } else if ("ActualizaCuentas".equals(accion)) {
            try {
                String msg = actualizaCuentas(req, resp);
                ResponseSender.sendArrayMessages(resp, true, new String[] { "Atenci&oacute;n", msg }, "msg");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                ResponseSender.sendError(resp, e.toString());
            }
        } else if ("EliminaCuenta".equals(accion)) {
            try {
                String msg = eliminaCuentas(req, resp);
                ResponseSender.sendArrayMessages(resp, true, new String[] { "Atenci&oacute;n", msg }, "msg");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                ResponseSender.sendArrayMessages(resp, true, new String[] { "Atenci&oacute;n", e.toString() }, "msg");
            }
        } else if ("AutoCompletaCuenta".equals(accion)) {
            String toFind = req.getParameter("term");
            if (toFind != null) {
                try {
                    autoCompletaCuentas(toFind, req, resp);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            //*********************** Polizas Eventos Manuales 2014 *********************
            // Ing J. Luis DR
        } else if ("autoCompletaGrupoEvento".equals(accion)) {
            String toFind = req.getParameter("term");
            if (toFind != null) {
                try {
                    autoCompletaGrupoEvento(toFind, req, resp);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        } else if ("autoCompletaSubGrupoEvento".equals(accion)) {
            String toFind = req.getParameter("term");
            if (toFind != null) {
                try {
                    autoCompletaSubGrupoEvento(toFind, req, resp);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        } else if ("autoCompletaEventoRelacion".equals(accion)) {
            String toFind = req.getParameter("term");
            if (toFind != null) {
                try {
                    autoCompletaEventoRelacion(toFind, req, resp);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        } else if ("autoCompletaCABMS".equals(accion)) {
            String toFind = req.getParameter("term");
            if (toFind != null) {
                try {
                    autoCompletaCABMS(toFind, req, resp);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        } else if ("selectCAMBS".equals(accion)) {
            String key = req.getParameter("key");
            String value = req.getParameter("value");
            req.getSession().setAttribute(key, value);
            if (value != null) {
                try {
                    selectCABMS(key, value, req, resp);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        } else if ("selected".equals(accion)) {
            String key = req.getParameter("key");
            String value = req.getParameter("value");
            req.getSession().setAttribute(key, value);
        }
        //**************************************************************************
    }

    private String eliminaCuentas(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        CuentaContable cuenta = readAccountFromRequest(req);
        CuentaContableBusinessLogic cbl = new CuentaContableBusinessLogic();
        String r = cbl.eliminaCuenta(cuenta);
        return r;
    }

    private CuentaContable readAccountFromRequest(HttpServletRequest req) {
        CuentaContable cuenta = new CuentaContable();
        String nCuenta = req.getParameter("nCuenta");
        String dCuenta = req.getParameter("dCuenta");
        String TipoCuenta = req.getParameter("TipoCuenta");
        String TipoBalance = req.getParameter("TipoBalance");
        String VerificaSaldo = req.getParameter("VerificaSaldo");
        String NaturalezaCuenta = req.getParameter("NaturalezaCuenta");
        String NivelCuenta = req.getParameter("NivelCuenta");
        String AplicacionCuenta = req.getParameter("AplicacionCuenta");
        String cSubcuenta = req.getParameter("cSubcuenta");
        String nCuentaLike = req.getParameter("nCuentaLike");
        String nOrdenBalanza = req.getParameter("nOrdenBalanza");
        String nNivelBalanza = req.getParameter("nNivelBalanza");
        cuenta.setnCuenta(nCuenta);
        cuenta.setdCuenta(dCuenta);
        cuenta.setTipoCuenta(TipoCuenta);
        cuenta.setTipoBalance(TipoBalance);
        cuenta.setVerificaSaldo(VerificaSaldo);
        cuenta.setNaturalezaCuenta(NaturalezaCuenta);
        cuenta.setNivelCuenta(NivelCuenta != null ? Integer.parseInt(NivelCuenta) : -1);
        cuenta.setAplicacionCuenta(AplicacionCuenta);
        cuenta.setcSubcuenta(cSubcuenta);
        cuenta.setnCuentaLike(nCuentaLike);
        cuenta.setnOrdenBalanza(nOrdenBalanza != null && !"".equals(nOrdenBalanza) ? Integer.parseInt(nOrdenBalanza) : -1);
        cuenta.setnNivelBalanza(nNivelBalanza != null && !"".equals(nNivelBalanza) ? Integer.parseInt(nNivelBalanza) : -1);
        return cuenta;
    }
}
