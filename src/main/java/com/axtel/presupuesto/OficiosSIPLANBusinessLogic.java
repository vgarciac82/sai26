package com.axtel.presupuesto;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OficiosSIPLANBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(OficiosSIPLANBusinessLogic.class);

    public OficiosSIPLANBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public int buscaOficio(int nOficio, String coordinacion) throws Exception {
        Connection conn = null;
        int cont = 0;
        String msg = "";
        try {
            conn = getConnection();
            cont = OficiosSIPLANManager.buscaOficio(conn, nOficio, coordinacion);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            msg += "Ocurrio el siguiente error al buscar: " + e.getMessage();
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.error("Ocurrio el siguiente error al hacer rollback: " + e2, e2);
                }
        } finally {
            CloseObject.closeObject(conn, false);
        }
        return cont;
    }

    public String guardaOficio(HttpServletRequest req, HttpServletResponse resp, int mes, String qwhere) throws Exception {
        Connection conn = null;
        int cont = 0;
        String msg = "";
        try {
            conn = getConnection();
            int nOficio = Integer.parseInt(req.getParameter("nOficio"));
            String fechaOficio = req.getParameter("fecha_aplicacion");
            String coordinacion = req.getParameter("cCoordinacion");
            String usuario = req.getParameter("usuario");
            cont = OficiosSIPLANManager.InsertaOficio(conn, fechaOficio, nOficio, usuario, coordinacion, mes, qwhere);
            if (cont > 0) {
                conn.commit();
                msg += "Oficio guardado correctamente";
            } else {
                msg += "Ocurrio un error, notifica al administrador";
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            msg += "Ocurrio el siguiente error al cancelar: " + e.getMessage();
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.error("Ocurrio el siguiente error al hacer rollback: " + e2, e2);
                }
        } finally {
            CloseObject.closeObject(conn, false);
        }
        return msg;
    }

    public void consultaOficio(HttpServletRequest req, HttpServletResponse resp, String ruta, int nOficio) throws Exception {
        Connection conn = null;
        String msg = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        int mes = 0;
        String qwhere = "";
        String coordinacion = "";
        String reportPath = "";
        String nombre1 = new String(req.getParameter("nombre1"));
        String puesto1 = new String(req.getParameter("cargo1"));
        try {
            conn = getConnection();
            String query = "SELECT nOficio, nMesModificado, cCondicion, cCoordinacion FROM tOficiosSIPLAN WHERE nOficio = " + nOficio;
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                //oficio = (rs.getInt(1));
                mes = (rs.getInt(2));
                qwhere = (rs.getString(3));
                coordinacion = (rs.getString(4));
            }
            Map<String, Object> parms = new LinkedHashMap<String, Object>();
            parms.put("whereUE", qwhere);
            parms.put("nOficio", nOficio);
            parms.put("SUBREPORT_DIR", ruta + File.separator);
            parms.put("mes", mes);
            parms.put("nombre1", nombre1);
            parms.put("puesto1", puesto1);
            parms.put("coordinacion", coordinacion);
            if ("A01".equals(coordinacion)) {
                reportPath = ruta + File.separator + "OficioMetasK.jasper";
            } else {
                reportPath = ruta + File.separator + "OficioMetas.jasper";
            }
            Util.generaFormatoJasper(conn, resp, ruta, parms, reportPath);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            msg += "Ocurrio el siguiente error al cancelar: " + e.getMessage();
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.error("Ocurrio el siguiente error al hacer rollback: " + e2, e2);
                }
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public void extraeModificado(HttpServletRequest req, HttpServletResponse resp, int mes, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            file = OficiosSIPLANManager.ReporteModificado(conn, mes, plantilla);
            File f = new File(file);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
            f = null;
        } finally {
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
    }
}
