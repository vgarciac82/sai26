package com.syc.ejercido.pagado.core;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.ejercido.pagado.core.BoletajeAereoManager.Boletos;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoletaAereoBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(BoletaAereoBusinessLogic.class);

    public BoletaAereoBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public int insertaBoletos(List<Boletos> info) throws Exception {
        Connection conn = null;
        int insertados = 0;
        try {
            conn = getConnection();
            insertados = BoletajeAereoManager.insertaBoletos(conn, info);
            conn.commit();
            return insertados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas realizando Rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public List<Map<String, String>> leePagos(int nFolioPago) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            return BoletajeAereoManager.leePagos(conn, nFolioPago);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public int insertaBoletosTemporal(String cTipoPago, int nFolioPago, int nFolioVuelos, String[] nDocRenglonVuelos) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            int insertados = BoletajeAereoManager.insertaBoletosTemporal(conn, cTipoPago, nFolioPago, nFolioVuelos, nDocRenglonVuelos);
            conn.commit();
            return insertados;
        } catch (Exception e) {
            try {
                if (conn != null)
                    conn.rollback();
            } catch (Exception e2) {
                log.warn("Error en rollback: " + e2);
            }
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public int generaExcelVuelos(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantillas) throws Exception {
        Connection conn = null;
        String file = null;
        int iRegistros = 0;
        try {
            conn = getConnection();
            file = BoletajeAereoManager.generaExcelVuelos(req, resp, plantillas.get("FmtoRpteVuelos"));
            File f = new File(file);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
        } finally {
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
        return iRegistros;
    }
}
