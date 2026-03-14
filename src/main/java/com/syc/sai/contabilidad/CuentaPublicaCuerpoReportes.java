package com.syc.sai.contabilidad;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import com.syc.admin.servlet.ReportsException;
import java.util.Base64;

public class CuentaPublicaCuerpoReportes {

    public static Map<String, String> CuerpoReportes = new HashMap<String, String>();

    public static String getReportBody(String repName) {
        String retVal = CuentaPublicaCuerpoReportes.CuerpoReportes.get(repName);
        return retVal;
    }

    public static String getReportBody(String repName, InputStream is) {
        if (is != null) {
            try {
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String read = br.readLine();
                while (read != null) {
                    sb.append(read);
                    read = br.readLine();
                }
                CuentaPublicaCuerpoReportes.CuerpoReportes.put(repName, sb.toString());
            } catch (Exception e) {
                throw new ReportsException("Error cargando el encabezado del reporte " + repName, e);
            }
        } else {
            CuentaPublicaCuerpoReportes.CuerpoReportes.put(repName, "");
        }
        return CuentaPublicaCuerpoReportes.CuerpoReportes.get(repName);
    }

    public static String getReportBody(String repName, InputStream is, String mesOtrimestre) {
        //Modificar esta funcion
        if (is != null) {
            try {
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String read = br.readLine();
                while (read != null) {
                    read = read.replace("#", mesOtrimestre);
                    sb.append(read);
                    read = br.readLine();
                }
                CuentaPublicaCuerpoReportes.CuerpoReportes.put(repName, sb.toString());
            } catch (Exception e) {
                throw new ReportsException("Error cargando el encabezado del reporte " + repName, e);
            }
        } else {
            CuentaPublicaCuerpoReportes.CuerpoReportes.put(repName, "");
        }
        return CuentaPublicaCuerpoReportes.CuerpoReportes.get(repName);
    }

    public static String[][] preGenC32AP390() {
        String[][] rw = new String[2][45];
        int j = 0;
        // 0
        rw[0][j++] = "<td class=\"encabezadoTopLeft\" align=\"center\">\n";
        rw[0][j++] = "&nbsp;";
        rw[0][j++] = "</td>\n";
        // 1
        rw[0][j++] = "<td class=\"encabezadoTopLeft\" align=\"center\">\n";
        rw[0][j++] = "&nbsp;";
        rw[0][j++] = "</td>\n";
        // 2
        rw[0][j++] = "<td class=\"encabezadoTopLeft\" align=\"center\">\n";
        rw[0][j++] = "&nbsp;";
        rw[0][j++] = "</td>\n";
        // 3
        rw[0][j++] = "<td class=\"encabezadoTopLeft\" align=\"center\">\n";
        rw[0][j++] = "&nbsp;";
        rw[0][j++] = "</td>\n";
        // 4
        rw[0][j++] = "<td class=\"encabezadoTopLeft\" align=\"center\">\n";
        rw[0][j++] = "&nbsp;";
        rw[0][j++] = "</td>\n";
        // 5
        rw[0][j++] = "<td class=\"encabezadoTopLeft\" align=\"center\">\n";
        rw[0][j++] = "&nbsp;";
        rw[0][j++] = "</td>\n";
        // 6
        rw[0][j++] = "<td class=\"encabezadoTopLeft\" align=\"center\">\n";
        rw[0][j++] = "&nbsp;";
        rw[0][j++] = "</td>\n";
        // 7
        rw[0][j++] = "<td class=\"encabezadoTopLeft\" align=\"center\">\n";
        rw[0][j++] = "&nbsp;";
        rw[0][j++] = "</td>\n";
        // 8
        rw[0][j++] = "<td class=\"encabezadoTopLeft\" align=\"center\">\n";
        rw[0][j++] = "&nbsp;";
        rw[0][j++] = "</td>\n";
        // 9
        rw[0][j++] = "<td class=\"encabezadoTopLeft\" align=\"center\">\n";
        rw[0][j++] = "&nbsp;";
        rw[0][j++] = "</td>\n";
        // 10
        rw[0][j++] = "<td class=\"encabezadoTopLeft\" align=\"center\">\n";
        rw[0][j++] = "&nbsp;";
        rw[0][j++] = "</td>\n";
        // 11
        rw[0][j++] = "<td class=\"encabezadoTopLeft\" align=\"center\">\n";
        rw[0][j++] = "&nbsp;";
        rw[0][j++] = "</td>\n";
        // 12
        rw[0][j++] = "<td class=\"encabezadoTopLeft\" align=\"center\">\n";
        rw[0][j++] = "&nbsp;";
        rw[0][j++] = "</td>\n";
        // 13
        rw[0][j++] = "<td class=\"encabezadoTopLeft\" align=\"center\">\n";
        rw[0][j++] = "&nbsp;";
        rw[0][j++] = "</td>\n";
        // 14
        rw[0][j++] = "<td class=\"encabezadoTopLeftRight\" align=\"center\">\n";
        rw[0][j++] = "&nbsp;";
        rw[0][j++] = "</td>\n";
        j = 0;
        rw[1][j++] = "<td class=\"encabezadoBottomLeft\" align=\"center\">\n";
        rw[1][j++] = "CONCEPTO";
        rw[1][j++] = "</td>\n";
        rw[1][j++] = "<td class=\"encabezadoBottomLeft\" align=\"center\">\n";
        rw[1][j++] = "INVERSIÓN 2011";
        rw[1][j++] = "</td>\n";
        rw[1][j++] = "<td class=\"encabezadoBottomLeft\" align=\"center\">\n";
        rw[1][j++] = "CLAVE";
        rw[1][j++] = "</td>\n";
        rw[1][j++] = "<td class=\"encabezadoBottomLeft\" align=\"center\">\n";
        rw[1][j++] = "DENOMINACIÓN";
        rw[1][j++] = "</td>\n";
        rw[1][j++] = "<td class=\"encabezadoBottomLeft\" align=\"center\">\n";
        rw[1][j++] = "TOTAL";
        rw[1][j++] = "</td>\n";
        rw[1][j++] = "<td class=\"encabezadoTopBottomLeft\" align=\"center\">\n";
        rw[1][j++] = "&nbsp;";
        rw[1][j++] = "</td>\n";
        rw[1][j++] = "<td class=\"encabezadoTopBottomLeft\" align=\"center\">\n";
        rw[1][j++] = "&nbsp;";
        rw[1][j++] = "</td>\n";
        rw[1][j++] = "<td class=\"encabezadoTopBottomLeft\" align=\"center\">\n";
        rw[1][j++] = "&nbsp;";
        rw[1][j++] = "</td>\n";
        rw[1][j++] = "<td class=\"encabezadoTopBottomLeft\" align=\"center\">\n";
        rw[1][j++] = "&nbsp;";
        rw[1][j++] = "</td>\n";
        rw[1][j++] = "<td class=\"encabezadoTopBottomLeft\" align=\"center\">\n";
        rw[1][j++] = "&nbsp;";
        rw[1][j++] = "</td>\n";
        rw[1][j++] = "<td class=\"encabezadoTopBottomLeft\" align=\"center\">\n";
        rw[1][j++] = "&nbsp;";
        rw[1][j++] = "</td>\n";
        rw[1][j++] = "<td class=\"encabezadoTopBottomLeft\" align=\"center\">\n";
        rw[1][j++] = "&nbsp;";
        rw[1][j++] = "</td>\n";
        rw[1][j++] = "<td class=\"encabezadoTopBottomLeft\" align=\"center\">\n";
        rw[1][j++] = "&nbsp;";
        rw[1][j++] = "</td>\n";
        rw[1][j++] = "<td class=\"encabezadoTopBottomLeft\" align=\"center\">\n";
        rw[1][j++] = "&nbsp;";
        rw[1][j++] = "</td>\n";
        rw[1][j++] = "<td class=\"encabezadoAll\" align=\"center\">\n";
        rw[1][j++] = "&nbsp;";
        rw[1][j++] = "</td>\n";
        return rw;
    }
}
