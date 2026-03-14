package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ReporteSaldosManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class ConsultaSaldosBusinessLogic extends DataSourceManager {

    public ConsultaSaldosBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public void generaReporteSaldos(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            int mIni = Integer.parseInt(req.getParameter("mesInicio"));
            int mFin = Integer.parseInt(req.getParameter("mesFin"));
            String nCuenta = req.getParameter("cuenta");
            String nSubCuenta = req.getParameter("subcuenta");
            String cCC = req.getParameter("cCC");
            file = ReporteSaldosManager.generaReporteSaldosManager(conn, mIni, mFin, nCuenta, nSubCuenta, cCC, plantilla);
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

    public void generaReporteMovimientos(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            int mIni = Integer.parseInt(req.getParameter("movMini"));
            int mFin = Integer.parseInt(req.getParameter("movMfin"));
            String nCuenta = req.getParameter("scCuenta");
            String nSubCuenta = req.getParameter("scSubcuenta");
            String nCC = req.getParameter("scCc");
            String SI = req.getParameter("scSi");
            String C = req.getParameter("scCargos");
            String A = req.getParameter("scAbonos");
            String SF = req.getParameter("scSf");
            file = ReporteSaldosManager.generaReporteMovimientosManager(conn, mIni, mFin, nCuenta, nSubCuenta, nCC, SI, C, A, SF, plantilla);
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
    }

    public void generaReportePolizaAux(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            int nFolio = Integer.parseInt(req.getParameter("nFolio"));
            String cTipo = req.getParameter("cTipo");
            int nDocto = Integer.parseInt(req.getParameter("nFolioDocumento"));
            String cDocto = req.getParameter("cTipoDocumento");
            int estatus = Integer.parseInt(req.getParameter("nEstatus"));
            file = ReporteSaldosManager.generaReportePolizaAuxManager(conn, nFolio, cTipo, nDocto, cDocto, estatus, plantilla);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + file + "\"; ");
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
    }
}
