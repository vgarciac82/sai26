package com.syc.sai.contabilidad.servlet;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.syc.sai.contabilidad.CuentaContable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseSender {

    private static final Logger log = LoggerFactory.getLogger(ResponseSender.class);

    public static void sendError(HttpServletResponse resp, String errorMsg) throws IOException {
        sendClientSimpleMessage(resp, false, errorMsg);
    }

    public static void sendError(HttpServletResponse resp, Exception e) {
        try {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            ServletOutputStream out = resp.getOutputStream();
            out.println("{\"errCause\":\"" + e.toString() + "\"}");
            out.flush();
            out.close();
        } catch (Exception e2) {
            throw new RuntimeException(e2);
        }
    }

    public static void sendResult(HttpServletResponse resp, String[] result, String encabezado) throws IOException {
        sendArrayMessages(resp, true, result, encabezado);
    }

    public static void sendResult(HttpServletResponse resp, String result) throws IOException {
        sendClientSimpleMessage(resp, true, result);
    }

    public static void sendClientSimpleMessage(HttpServletResponse resp, boolean success, String msg) throws IOException {
        String res = "{\"success\":\"" + success + "\"," + "\"data_1\":{\"result\":\"" + msg + "\"}" + "}";
        resp.setContentType("application/json");
        ServletOutputStream out = resp.getOutputStream();
        out.print(res);
        out.flush();
        out.close();
    }

    public static void sendArrayMessages(HttpServletResponse resp, boolean success, String[] result, String enc) throws IOException {
        String res = "{\"success\":\"" + success + "\",";
        if (result == null || result.length == 0)
            res += "\"data_1\":[]" + "}";
        else {
            String token = "";
            res += "\"data_1\":[";
            for (int i = 0; i < result.length; i++) {
                res += token + "{\"" + enc + "\":\"" + result[i] + "\"}";
                token = ", ";
            }
            res += "]}";
        }
        resp.setContentType("application/json");
        ServletOutputStream out = resp.getOutputStream();
        log.debug(res);
        out.print(res);
        out.flush();
        out.close();
    }

    public static void sendMultiArrayMessages(HttpServletResponse resp, boolean success, String[][] result, String enc) throws IOException {
        String res = "{\"success\":\"" + success + "\",";
        if (result == null || result.length == 0)
            res += "\"data_1\":[]" + "}";
        else {
            String token = "";
            String tokenP = "";
            res += "\"data_1\":";
            for (int j = 0; j < result.length; j++) {
                res += tokenP + "[";
                for (int i = 0; i < result.length; i++) {
                    res += token + "{\"" + enc + "\":\"" + result[i] + "\"}";
                    token = ", ";
                }
                res += "]";
            }
            tokenP = ",";
        }
        res += "}";
        resp.setContentType("application/json");
        ServletOutputStream out = resp.getOutputStream();
        log.debug(res);
        out.print(res);
        out.flush();
        out.close();
    }

    public static void sendDatatable(HttpServletRequest req, HttpServletResponse resp, List<CuentaContable> l) throws Exception {
        String encT = "{\"aaData\":[";
        StringBuffer buffer = new StringBuffer();
        String endT = "]}";
        String token = "";
        for (Iterator<CuentaContable> i = l.iterator(); i.hasNext(); ) {
            CuentaContable c = i.next();
            String dcuenta = new String(c.getdCuenta().toUpperCase().getBytes("UTF-8"), "ISO-8859-1");
            buffer.append(token + "[");
            buffer.append("\"" + c.getnCuenta() + "\",");
            buffer.append("\"" + dcuenta + "\",");
            buffer.append("\"" + c.getTipoCuenta() + "\",");
            buffer.append("\"" + c.getnCuentaPadre() + "\",");
            buffer.append("\"" + c.getTipoBalance() + "\",");
            buffer.append("\"" + c.getNaturalezaCuenta() + "\",");
            buffer.append("\"" + c.getNivelCuenta() + "\",");
            buffer.append("\"" + c.getAplicacionCuenta() + "\",");
            buffer.append("\"" + c.getcSubcuenta() + "\",");
            buffer.append("\"" + c.getnCuentaLike() + "\"");
            buffer.append("]\n");
            token = ",";
        }
        String respuesta = encT + buffer.toString() + endT;
        resp.setContentType("application/json");
        ServletOutputStream out = resp.getOutputStream();
        log.debug(respuesta);
        out.print(respuesta);
        out.flush();
        out.close();
    }

    public static void sendGenericDatatable(HttpServletRequest req, HttpServletResponse resp, List<List<String>> l, Integer iTotalDisplayRecords) throws Exception {
        int sEcho = req.getParameter("sEcho") == null ? 1 : Integer.parseInt(req.getParameter("sEcho"));
        String encT = "{\"sEcho\":\"" + sEcho + "\", \"iTotalRecords\":\"" + iTotalDisplayRecords + "\", \"iTotalDisplayRecords\":\"" + iTotalDisplayRecords + "\", \"aaData\":[";
        StringBuffer buffer = new StringBuffer();
        String endT = "]}";
        String token = "";
        for (Iterator<List<String>> i = l.iterator(); i.hasNext(); ) {
            List<String> lAux = i.next();
            buffer.append(token + "[");
            String tokenArr = "";
            for (int j = 0; j < lAux.size(); j++) {
                buffer.append(tokenArr + "\"" + lAux.get(j) + "\"");
                tokenArr = ",";
            }
            buffer.append("]\n");
            token = ",";
        }
        String respuesta = new String((encT + buffer.toString() + endT).getBytes("UTF-8"), "ISO-8859-1");
        // String respuesta = encT + buffer.toString() + endT;
        resp.setContentType("application/json; charset=UTF-8");
        ServletOutputStream out = resp.getOutputStream();
        log.debug(respuesta);
        out.print(respuesta);
        out.flush();
        out.close();
    }
}
