package com.syc.reportes;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.reportes.core.GuiasContablesManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;
import java.nio.file.Paths;

public class GuiasContablesBusinessLogic extends DataSourceManager {

    public GuiasContablesBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public int guardaVersion(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Connection conn = null;
        int cont = 0;
        try {
            conn = getConnection();
            int id_guia = 0;
            int id_ManejoCuentas = 0;
            int id_Tipo = 0;
            String fechaInicio = req.getParameter("fecha_aplicacion");
            //String Observacion = new String((req.getParameter("observacion")).getBytes("ISO-8859-1"),"UTF-8");
            int idversion_sig = Integer.parseInt(req.getParameter("idversion_sig"));
            String usuario = req.getParameter("usuario");
            String id_archivo = req.getParameter("id_archivoV");
            if ("GuiasContabilizadoras.jasper".equals(id_archivo)) {
                id_guia = Integer.parseInt(req.getParameter("id_GuiaV"));
                id_Tipo = 1;
                if (id_guia == 0) {
                    cont = GuiasContablesManager.InsertaVersionMasivo(conn, fechaInicio, usuario, id_Tipo);
                } else {
                    cont = GuiasContablesManager.InsertaVersion(conn, fechaInicio, idversion_sig, usuario, id_Tipo, id_guia);
                }
            } else if ("ManejoDeCuentas.jasper".equals(id_archivo)) {
                id_ManejoCuentas = Integer.parseInt(req.getParameter("id_ManejoCuentasV"));
                id_Tipo = 2;
                if (id_ManejoCuentas == 0)
                    cont = GuiasContablesManager.InsertaVersionMasivo(conn, fechaInicio, usuario, id_Tipo);
                else {
                    cont = GuiasContablesManager.InsertaVersion(conn, fechaInicio, idversion_sig, usuario, id_Tipo, id_ManejoCuentas);
                }
            } else if ("PlanDecuentas.jasper".equals(id_archivo)) {
                id_Tipo = 3;
                cont = GuiasContablesManager.InsertaVersion(conn, fechaInicio, idversion_sig, usuario, id_Tipo, 1);
            }
            List<Integer> NoNivel = GuiasContablesManager.CatalogoNivel(conn);
            for (int i : NoNivel) {
                if ("GuiasContabilizadoras.jasper".equals(id_archivo))
                    GuiasContablesManager.GuiasContablesManager(conn, fechaInicio, idversion_sig, i, id_guia, usuario);
                else if ("ManejoDeCuentas.jasper".equals(id_archivo))
                    GuiasContablesManager.ManejoCuentas(conn, fechaInicio, idversion_sig, i, id_ManejoCuentas);
                else if ("PlanDecuentas.jasper".equals(id_archivo))
                    GuiasContablesManager.PlanCuentas(conn, fechaInicio, idversion_sig, i);
            }
            conn.commit();
        } finally {
            CloseObject.closeObject(conn, false);
        }
        return cont;
    }

    public void consultaVersiones(HttpServletRequest req, HttpServletResponse resp, String reportPath, String ruta, int tipoManual, String sinFirmas, int nivel, int idGuia, int idInstructivo) throws Exception {
        Connection conn = null;
        try {
            //version a consultar
            int idversion = Integer.parseInt(req.getParameter("id_version"));
            int sinFirma = Integer.parseInt(sinFirmas);
            String numfirmas = (req.getParameter("chk_firmas") == null ? "3" : req.getParameter("chk_firmas"));
            String nombre1 = new String(req.getParameter("nombre1").getBytes("ISO-8859-1"), "UTF-8");
            String puesto1 = new String(req.getParameter("cargo1").getBytes("ISO-8859-1"), "UTF-8");
            String nombre2 = new String(req.getParameter("nombre2").getBytes("ISO-8859-1"), "UTF-8");
            String puesto2 = new String(req.getParameter("cargo2").getBytes("ISO-8859-1"), "UTF-8");
            String nombre3 = new String(req.getParameter("nombre3").getBytes("ISO-8859-1"), "UTF-8");
            String puesto3 = new String(req.getParameter("cargo3").getBytes("ISO-8859-1"), "UTF-8");
            conn = getConnection();
            Map<String, Object> parms = new LinkedHashMap<String, Object>();
            parms.put("idVersion", idversion);
            parms.put("final", 1);
            parms.put("consulta", 1);
            parms.put("SUBREPORT_DIR", ruta + File.separator);
            parms.put("nfirmas", Integer.parseInt(numfirmas, 10));
            parms.put("nombre1", nombre1);
            parms.put("puesto1", puesto1);
            parms.put("nombre2", nombre2);
            parms.put("puesto2", puesto2);
            parms.put("nombre3", nombre3);
            parms.put("puesto3", puesto3);
            parms.put("tipoManual", tipoManual);
            parms.put("sinFirmas", sinFirma);
            parms.put("nivel", nivel);
            parms.put("idGuia", idGuia);
            parms.put("idInstructivo", idInstructivo);
            GuiasContablesManager.cosultaVersiones(conn, resp, reportPath, ruta, parms);
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public void cosultaTemporal(HttpServletRequest req, HttpServletResponse resp, String ruta, String tipoReporte, int tipoManual, String sinFirmas, int nivel, int idGuia, int idInstructivo) throws Exception {
        Connection conn = null;
        try {
            String reportPath = "";
            String numfirmas = (req.getParameter("chk_firmas") == null ? "3" : req.getParameter("chk_firmas"));
            int sinFirma = Integer.parseInt(sinFirmas);
            String nombre1 = new String(req.getParameter("nombre1").getBytes("ISO-8859-1"), "UTF-8");
            String puesto1 = new String(req.getParameter("cargo1").getBytes("ISO-8859-1"), "UTF-8");
            String nombre2 = new String(req.getParameter("nombre2").getBytes("ISO-8859-1"), "UTF-8");
            String puesto2 = new String(req.getParameter("cargo2").getBytes("ISO-8859-1"), "UTF-8");
            String nombre3 = new String(req.getParameter("nombre3").getBytes("ISO-8859-1"), "UTF-8");
            String puesto3 = new String(req.getParameter("cargo3").getBytes("ISO-8859-1"), "UTF-8");
            conn = getConnection();
            Map<String, Object> parms = new LinkedHashMap<String, Object>();
            parms.put("idVersion", 0);
            parms.put("final", 0);
            parms.put("consulta", 0);
            parms.put("SUBREPORT_DIR", ruta + File.separator);
            parms.put("nfirmas", Integer.parseInt(numfirmas, 10));
            parms.put("nombre1", nombre1);
            parms.put("puesto1", puesto1);
            parms.put("nombre2", nombre2);
            parms.put("puesto2", puesto2);
            parms.put("nombre3", nombre3);
            parms.put("puesto3", puesto3);
            parms.put("tipoManual", tipoManual);
            parms.put("sinFirmas", sinFirma);
            parms.put("nivel", nivel);
            parms.put("idGuia", idGuia);
            parms.put("idInstructivo", idInstructivo);
            reportPath = ruta + File.separator + tipoReporte;
            GuiasContablesManager.cosultaTemporal(conn, resp, ruta, parms, reportPath);
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public void cosultaFormato(HttpServletRequest req, HttpServletResponse resp, String ruta, String nomjsaper) throws Exception {
        Connection conn = null;
        try {
            System.out.println("Archivo a Extraer: " + ruta + File.separator + nomjsaper);
            File file = new File(ruta + File.separator + nomjsaper);
            conn = getConnection();
            resp.setContentType("application/pdf");
            resp.addHeader("Content-Disposition", "attachment; filename=" + nomjsaper);
            resp.setContentLength((int) file.length());
            FileInputStream fileInputStream = new FileInputStream(file);
            OutputStream responseOutputStream = resp.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes.toPath());
            }
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public int guardaVersionComplementos(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Connection conn = null;
        int cont = 0;
        try {
            conn = getConnection();
            String fechaInicio = req.getParameter("fecha_aplicacion");
            int idversion_complemento = Integer.parseInt(req.getParameter("idversion_complemento"));
            String usuario = req.getParameter("usuario");
            int id_complemento = Integer.parseInt(req.getParameter("id_ComplementoV"));
            cont = GuiasContablesManager.Complementos(conn, fechaInicio, idversion_complemento, id_complemento, usuario);
            conn.commit();
        } finally {
            CloseObject.closeObject(conn, false);
        }
        return cont;
    }
}
