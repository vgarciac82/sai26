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
import com.syc.reportes.core.ReporteAcreedoresDeudoresManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@SuppressWarnings("unused")
public class reporteSIIWebFlujoEfectivoBussinesLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(reporteSIIWebFlujoEfectivoBussinesLogic.class);

    //parametro globales
    public reporteSIIWebFlujoEfectivoBussinesLogic(String jniName) {
        super.init(jniName);
    }

    public File generaCSV(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantillas) throws Exception {
        Connection conn = null;
        String file = null;
        String mes = req.getParameter("mes");
        int mesIni = Integer.parseInt(mes);
        String ejercicioFiscal = req.getParameter("ejercicioFiscal");
        int anio = Integer.parseInt(ejercicioFiscal);
        try {
            conn = getConnection();
            String fecha = req.getParameter("Fecha");
            String tipo = req.getParameter("reporte");
            String tipoPlantilla = req.getParameter("reporte");
            String nfolioeCs = req.getParameter("nFolioeCs");
            return reporteSIIWebFlujoEfectivoManager.FlujoEfectivoCSV(conn, mesIni, anio, tipo, nfolioeCs, tipoPlantilla);
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
            String mes = req.getParameter("mes");
            int dia = Integer.parseInt(req.getParameter("dia"));
            int mesIni = Integer.parseInt(mes);
            String tipoPlantilla = req.getParameter("reporte");
            String nfolioeCs = req.getParameter("nFolioeCs");
            String ejercicioFiscal = req.getParameter("ejercicioFiscal");
            int anio = Integer.parseInt(ejercicioFiscal);
            if ("Obs112".equals(tipoPlantilla)) {
                existe = req.getParameter("validaExiste");
            } else if ("Efe1112".equals(tipoPlantilla)) {
                existe1112 = req.getParameter("validaExiste1112");
            }
            if ("210".equals(tipoPlantilla) || "221".equals(tipoPlantilla) || "222".equals(tipoPlantilla) || "Obs112".equals(tipoPlantilla) || "Efe1112".equals(tipoPlantilla)) {
                if ("0".equals(existe) && "0".equals(existe1112)) {
                    file = reporteSIIWebFlujoEfectivoManager.DispFinExcel(conn, nfolioeCs, tipoPlantilla, plantillas);
                } else {
                    file = reporteSIIWebFlujoEfectivoManager.DispFinExcelSelect(conn, nfolioeCs, tipoPlantilla, plantillas);
                }
            } else if ("Ant114".equals(tipoPlantilla)) {
                file = reporteSIIWebFlujoEfectivoManager.FlujoEfectivoExcel(conn, mesIni, anio, tipoPlantilla, plantillas, dia);
            } else {
                file = reporteSIIWebFlujoEfectivoManager.FlujoEfectivoExcel(conn, mesIni, anio, tipoPlantilla, plantillas, dia);
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
