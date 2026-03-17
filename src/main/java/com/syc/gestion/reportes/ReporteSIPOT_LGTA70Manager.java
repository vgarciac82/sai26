package com.syc.gestion.reportes;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.contabilidad.utils.db.RSToTable;
import java.util.Base64;
import java.nio.file.Paths;

@SuppressWarnings("unused")
public class ReporteSIPOT_LGTA70Manager implements Serializable {

    private final static long serialVersionUID = 1;

    public static String ReportesExcel(Connection conn, int mes, int anio, String tipo, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        PreparedStatement ps = null, ps2 = null;
        ResultSet rs = null, rs2 = null, rs3 = null, rs4 = null, rs5 = null;
        String query = null, query2 = null, query3 = null, esDesarrollo = null, baseName = "nomina_" + anio;
        int mesIni = 0;
        if (anio >= 2018) {
            if (tipo.equals("43A"))
                query = "{call sp_SIPOT_43A_IngresosRecibidos_v2( ?, ? )}";
            else if (tipo.equals("43B")) {
                if (mes == 3)
                    mesIni = 1;
                else if (mes == 6)
                    mesIni = 4;
                else if (mes == 9)
                    mesIni = 7;
                else if (mes == 12)
                    mesIni = 10;
                query = "{call sp_SIPOT_43B_FirmantesResponsables( ?, ?, ? )}";
                if (anio <= 2023 && mes <= 3) {
                    query3 = "SELECT GP_VALOR FROM CG_GRUPO_PROPIEDADES WHERE GP_NOMBRE = 'AMBIENTE_DESARROLLO'";
                    ps2 = conn.prepareStatement(query3);
                    rs5 = ps2.executeQuery();
                    rs5 = ps2.executeQuery();
                    if (rs5.next()) {
                        esDesarrollo = rs5.getString("GP_VALOR");
                    }
                    if ("TRUE".equals(esDesarrollo))
                        baseName = baseName + "_desa";
                    query2 = "SELECT ROW_NUMBER ( )   \r\n" + "		OVER (  ORDER BY d_emplnombre, d_emplap, d_emplam, d_plaza ) AS row1\r\n" + "		, d_emplnombre\r\n" + "		, d_emplap\r\n" + "		, d_emplam\r\n" + "		, d_plaza\r\n" + "		, NOTA\r\n" + "	FROM ( SELECT DISTINCT empleados.d_emplnombre\r\n" + "				, empleados.d_emplap\r\n" + "				, empleados.d_emplam\r\n" + "				, empleados.d_plaza\r\n" + "				, CASE WHEN cTipoSuplencia IS NULL THEN ''\r\n" + "					ELSE (SELECT 'Firma ' + cTipoSuplencia + ' de ' + e.d_emplnombre + ' ' + e.d_emplap + ' ' + e.d_emplam\r\n" + "								+ ' con fundamento en el oficio ' + cFolioOficio + ' de fecha ' + CONVERT(VARCHAR(15), dFechaOficio)\r\n" + "						FROM " + baseName + "..nom_empleado e (NOLOCK)\r\n" + "						WHERE e.c_empleado = PF.nNumEmpleadoAutP)  \r\n" + "				END NOTA					\r\n" + "		FROM v_PagosFirmantes AS PF\r\n" + "		INNER JOIN (SELECT e.c_empleado,e.d_emplnombre,e.d_emplap,e.d_emplam,MAX(pm.fInicio)fInicio,MAX(pm.fFin) fFin,vHist.d_plaza\r\n" + "					FROM " + baseName + "..nom_empleado e (NOLOCK)\r\n" + "					INNER JOIN " + baseName + "..nom_Plaza_Movto pm (NOLOCK)\r\n" + "						ON e.c_empleado = pm.c_empleado AND cDocumento <> 'BAJA'\r\n" + "					INNER JOIN " + baseName + "..v_Plazas_Historia_rep vHist (NOLOCK)\r\n" + "						ON vHist.c_plaza = pm.c_plaza\r\n" + "						AND pm.fInicio BETWEEN vHist.fInicio AND vHist.fFin\r\n" + "					GROUP BY e.c_empleado,e.d_emplnombre,e.d_emplap,e.d_emplam,vHist.d_plaza\r\n" + "					) empleados\r\n" + "			ON PF.fPagado BETWEEN empleados.fInicio AND empleados.fFin\r\n" + "				AND empleados.c_empleado = PF.nNumEmpleadoAut\r\n" + "		WHERE YEAR(FPAGADO) = " + anio + " \r\n" + "			AND MONTH(PF.fPagado) BETWEEN " + mesIni + " AND " + mes + " \r\n" + "		) TBL \r\n" + "	ORDER BY d_emplnombre, d_emplap, d_emplam, d_plaza	";
                } else if (anio >= 2023 && mes >= 3) {
                    query3 = "SELECT GP_VALOR FROM CG_GRUPO_PROPIEDADES WHERE GP_NOMBRE = 'AMBIENTE_DESARROLLO'";
                    ps2 = conn.prepareStatement(query3);
                    rs5 = ps2.executeQuery();
                    rs5 = ps2.executeQuery();
                    if (rs5.next()) {
                        esDesarrollo = rs5.getString("GP_VALOR");
                    }
                    if ("TRUE".equals(esDesarrollo))
                        baseName = baseName + "_desa";
                    query2 = "SELECT ROW_NUMBER ( )   \r\n" + "		OVER (  ORDER BY d_emplnombre, d_emplap, d_emplam, d_plaza ) AS row1\r\n" + "		, d_emplnombre\r\n" + "		, d_emplap\r\n" + "		, d_emplam\r\n" + "		, CASE Sexo \r\n" + "			WHEN 1 THEN 'Mujer'\r\n" + "			WHEN 2 THEN 'Hombre'\r\n" + "		END AS Sexo" + "		, d_plaza\r\n" + "		, NOTA\r\n" + "	FROM ( SELECT DISTINCT empleados.d_emplnombre\r\n" + "				, empleados.d_emplap\r\n" + "				, empleados.d_emplam\r\n" + "				, empleados.Sexo\r\n" + "				, empleados.d_plaza\r\n" + "				, CASE WHEN cTipoSuplencia IS NULL THEN ''\r\n" + "					ELSE (SELECT 'Firma ' + cTipoSuplencia + ' de ' + e.d_emplnombre + ' ' + e.d_emplap + ' ' + e.d_emplam\r\n" + "								+ ' con fundamento en el oficio ' + cFolioOficio + ' de fecha ' + CONVERT(VARCHAR(15), dFechaOficio)\r\n" + "						FROM " + baseName + "..nom_empleado e (NOLOCK)\r\n" + "						WHERE e.c_empleado = PF.nNumEmpleadoAutP)  \r\n" + "				END NOTA					\r\n" + "		FROM v_PagosFirmantes AS PF\r\n" + "		INNER JOIN (SELECT e.c_empleado,e.d_emplnombre,e.d_emplap,e.d_emplam, e.Sexo, MAX(pm.fInicio)fInicio,MAX(pm.fFin) fFin,vHist.d_plaza\r\n" + "					FROM " + baseName + "..nom_empleado e (NOLOCK)\r\n" + "					INNER JOIN " + baseName + "..nom_Plaza_Movto pm (NOLOCK)\r\n" + "						ON e.c_empleado = pm.c_empleado AND cDocumento <> 'BAJA'\r\n" + "					INNER JOIN " + baseName + "..v_Plazas_Historia_rep vHist (NOLOCK)\r\n" + "						ON vHist.c_plaza = pm.c_plaza\r\n" + "						AND pm.fInicio BETWEEN vHist.fInicio AND vHist.fFin\r\n" + "					GROUP BY e.c_empleado,e.d_emplnombre,e.d_emplap,e.d_emplam, e.Sexo, vHist.d_plaza\r\n" + "					) empleados\r\n" + "			ON PF.fPagado BETWEEN empleados.fInicio AND empleados.fFin\r\n" + "				AND empleados.c_empleado = PF.nNumEmpleadoAut\r\n" + "		WHERE YEAR(FPAGADO) = " + anio + " \r\n" + "			AND MONTH(PF.fPagado) BETWEEN " + mesIni + " AND " + mes + " \r\n" + "		) TBL \r\n" + "	ORDER BY d_emplnombre, d_emplap, d_emplam, d_plaza	";
                }
            } else if (tipo.equals("31"))
                query = "{call sp_SIPOT_31_IngresosPresupuestales_v2( ?, ?)}";
        } else {
            if (tipo.equals("43A"))
                query = "{call sp_SIPOT_43A_IngresosRecibidos( ?, ? )}";
            else if (tipo.equals("31"))
                query = "{call sp_SIPOT_31_IngresosPresupuestales( ?, ?)}";
        }
        String fileName = "";
        try {
            if (tipo.equals("43B")) {
                cs = conn.prepareCall(query);
                cs.setInt(1, anio);
                cs.setInt(2, mesIni);
                cs.setInt(3, mes);
                rs = cs.executeQuery();
                ps = conn.prepareStatement(query2);
                rs2 = ps.executeQuery();
            } else {
                cs = conn.prepareCall(query);
                cs.setInt(1, anio);
                cs.setInt(2, mes);
                rs = cs.executeQuery();
            }
            if (anio >= 2018) {
                if (tipo.equals("43A")) {
                    fileName = generaReporteExcel(rs, tipo, plantillas.get("F43A_v2"), mes, anio);
                } else if (tipo.equals("43B") && anio >= 2020) {
                    if (anio <= 2023 && mes <= 3) {
                        fileName = generaReporteExcel43B(rs, rs2, tipo, plantillas.get("F43B"), mes, anio);
                    } else if (anio >= 2023 && mes >= 3) {
                        fileName = generaReporteExcel43B(rs, rs2, tipo, plantillas.get("F43B_v2"), mes, anio);
                    }
                } else if (tipo.equals("31")) {
                    if (anio <= 2023 && mes <= 3) {
                        fileName = generaReporteExcel(rs, tipo, plantillas.get("F31_v2"), mes, anio);
                    } else if (anio >= 2023 && mes >= 3) {
                        fileName = generaReporteExcel(rs, tipo, plantillas.get("F31_v3"), mes, anio);
                    }
                }
            } else {
                if (tipo.equals("43A")) {
                    fileName = generaReporteExcel(rs, tipo, plantillas.get("F43A"), mes, anio);
                } else if (tipo.equals("31")) {
                    fileName = generaReporteExcel(rs, tipo, plantillas.get("F31"), mes, anio);
                }
            }
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReporteExcel(ResultSet rs, String tipoFormato, String plantillaPath, int mes, int anio) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteSIPOT" + "_" + tipoFormato + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        sheet0 = Util.resultSetToExcel(rs, sheet0, 7, 0, false);
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos);
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    private static String generaReporteExcel43B(ResultSet rs, ResultSet rs2, String tipoFormato, String plantillaPath, int mes, int anio) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteSIPOT_43B" + "_" + tipoFormato + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        int cnt = 0;
        int renglonInicio = 7;
        Sheet sheet0 = workbook.getSheetAt(0);
        Sheet sheet1 = workbook.getSheetAt(1);
        Sheet sheet2 = workbook.getSheetAt(2);
        Sheet sheet3 = workbook.getSheetAt(3);
        ResultSetMetaData rsMetadata1 = rs.getMetaData();
        ResultSetMetaData rsMetadata2 = rs2.getMetaData();
        Row rw = null;
        while (rs.next()) {
            rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata1.getColumnCount(); i++) {
                Util.createExcelCell(i, rw, rs, rsMetadata1.getColumnName(i + 1), rsMetadata1.getColumnType(i + 1));
            }
            cnt++;
        }
        renglonInicio = 3;
        cnt = 0;
        while (rs2.next()) {
            rw = (sheet1.getRow(renglonInicio + cnt) == null ? sheet1.createRow(renglonInicio + cnt) : sheet1.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata2.getColumnCount() - 1; i++) {
                Util.createExcelCell(i, rw, rs2, rsMetadata2.getColumnName(i + 1), rsMetadata2.getColumnType(i + 1));
            }
            rw = (sheet2.getRow(renglonInicio + cnt) == null ? sheet2.createRow(renglonInicio + cnt) : sheet2.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata2.getColumnCount() - 1; i++) {
                Util.createExcelCell(i, rw, rs2, rsMetadata2.getColumnName(i + 1), rsMetadata2.getColumnType(i + 1));
            }
            rw = (sheet3.getRow(renglonInicio + cnt) == null ? sheet3.createRow(renglonInicio + cnt) : sheet3.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata2.getColumnCount() - 1; i++) {
                Util.createExcelCell(i, rw, rs2, rsMetadata2.getColumnName(i + 1), rsMetadata2.getColumnType(i + 1));
            }
            cnt++;
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos);
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }
}
