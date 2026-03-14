package com.syc.gestion.reportes;

//
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperRunManager;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@SuppressWarnings("unused")
public class reporteSIIWebDisFinancierasBussinesLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(reporteSIIWebDisFinancierasBussinesLogic.class);

    //parametro globales
    public reporteSIIWebDisFinancierasBussinesLogic(String jniName) {
        super.init(jniName);
    }

    public File generaCSV(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantillas) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String fecha = req.getParameter("Fecha");
            String tipo = req.getParameter("reporte");
            String nfolioeCs = req.getParameter("nFolioeCs");
            return reporteSIIWebDisFinancierasManager.DisponibilidadesFinancierasCSV(conn, tipo, nfolioeCs);
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public void generaPlantillaExcel(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantillas) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String existe = "0";
            String existe1112 = "0";
            String mes = req.getParameter("nMes");
            String tipoPlantilla = req.getParameter("reporte");
            String nfolioeCs = req.getParameter("nFolioeCs");
            if ("210".equals(tipoPlantilla) || "221".equals(tipoPlantilla) || "222".equals(tipoPlantilla)) {
                if ("0".equals(existe) && "0".equals(existe1112)) {
                    file = reporteSIIWebDisFinancierasManager.DispFinExcel(conn, nfolioeCs, tipoPlantilla, plantillas);
                } else {
                    file = reporteSIIWebDisFinancierasManager.DispFinExcelSelect(conn, nfolioeCs, tipoPlantilla, plantillas);
                }
            }
            File f = new File(file);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
        } finally {
            conn.commit();
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
    }
}
