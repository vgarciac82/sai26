package com.syc.sai.contabilidad.utils.db;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class RSToTable {

    private static final Logger log = LoggerFactory.getLogger(RSToTable.class);

    private static final String OPEN_TR = "<tr>";

    private static final String CLOSE_TR = "</tr>";

    private static final String OPEN_TD = "<td class=\"centradoVertical\">";

    private static final String OPEN_TD_COLSPAN = "<td class=\"centradoVertical\" colspan=\"%s\">";

    private static final String CLOSE_TD = "</td>";

    private static final String EMPTY_ESPACE = "&nbsp;";

    private static final String EMPTY_TD = OPEN_TD + EMPTY_ESPACE + CLOSE_TD;

    private static final String htmlBodyI = "<HTML>\n " + "\t<HEAD>\n " + "\t\t<TITLE>\n</TITLE>\n " + "\t\t<style type=\"text/css\">\n " + "\t\t\tbody {\n " + "\t\t\t\tfont-family: Arial, \"Helvetica Neue\", Helvetica, sans-serif;\n " + "\t\t\t\tfont-size: 18;\n " + "\t\t\t}\n " + ".encabezadoTopBottomRight { " + "	border-top-style: solid; " + "	border-top-color: black; " + "	border-top-width: thin; " + "	border-bottom-style: solid; " + "	border-bottom-color: black; " + "	border-bottom-width: thin; " + "	border-right-style: solid; " + "	border-right-color: black; " + "	border-right-width: thin; " + "}" + "\t\t\t.encabezadoTopBottomLeft { " + "\t\t\t\t	border-top-style: solid;\n " + "\t\t\t\t	border-top-color: black;\n " + "\t\t\t\t	border-top-width: thin;\n " + "\t\t\t\t	border-bottom-style: solid;\n " + "\t\t\t\t	border-bottom-color: black;\n " + "\t\t\t\t	border-bottom-width: thin;\n " + "\t\t\t\t	border-left-style: solid;\n " + "\t\t\t\t	border-left-color: black;\n " + "\t\t\t\t	border-left-width: thin;\n " + "\t\t\t\t} " + "\t\t\t.encabezadoTop {\n " + "\t\t\t\tborder-top-style: solid;\n " + "\t\t\t\tborder-top-color: black;\n " + "\t\t\t\tborder-top-width: thin;\n " + "\t\t\t}\n " + "\n" + "\t\t\t.encabezadoTopBottom{\n " + "\t\t\t\tborder-top-style: solid;\n " + "\t\t\t\tborder-top-color: black;\n " + "\t\t\t\tborder-top-width: thin;\n " + "\t\t\t\tborder-bottom-style: solid;\n " + "\t\t\t\tborder-bottom-color: black;\n " + "\t\t\t\tborder-bottom-width: thin;\n " + "\t\t\t}\n " + "\t\t\t.encabezadoTopLeft {\n " + "\t\t\t\tborder-top-style: solid;\n " + "\t\t\t\tborder-top-color: black;\n " + "\t\t\t\tborder-top-width: thin;\n " + "\t\t\t\tborder-left-style: solid;\n " + "\t\t\t\tborder-left-color: black;\n " + "\t\t\t\tborder-left-width: thin;\n " + "\t\t\t}\n " + "\n" + "\t\t\t.encabezadoTopRight {\n " + "\t\t\t\tborder-top-style: solid;\n " + "\t\t\t\tborder-top-color: black;\n " + "\t\t\t\tborder-top-width: thin;\n " + "\t\t\t\tborder-right-style: solid;\n " + "\t\t\t\tborder-right-color: black;\n " + "\t\t\t\tborder-right-width: thin;\n " + "\t\t\t}\n " + "\t\t\t.encabezadoTopLeftRight{\n " + "\t\t\t\tborder-top-style: solid;\n " + "\t\t\t\tborder-top-color: black;\n " + "\t\t\t\tborder-top-width: thin;\n " + "\t\t\t\tborder-left-style: solid;\n " + "\t\t\t\tborder-left-color: black;\n " + "\t\t\t\tborder-left-width: thin;\n " + "\t\t\t\tborder-right-style: solid;\n " + "\t\t\t\tborder-right-color: black;\n " + "\t\t\t\tborder-right-width: thin;\n " + "\t\t\t}\n " + "\t\t\t.encabezadoTopLeftBottom{\n " + "\t\t\t\tborder-top-style: solid;\n " + "\t\t\t\tborder-top-color: black;\n " + "\t\t\t\tborder-top-width: thin;\n " + "\t\t\t\tborder-left-style: solid;\n " + "\t\t\t\tborder-left-color: black;\n " + "\t\t\t\tborder-left-width: thin;\n " + "\t\t\t\tborder-bottom-style: solid;\n " + "\t\t\t\tborder-bottom-color: black;\n " + "\t\t\t\tborder-bottom-width: thin;\n " + "\t\t\t}\n " + "\n" + "\t\t\t.encabezadoTopRightBottom{\n " + "\t\t\t\tborder-top-style: solid;\n " + "\t\t\t\tborder-top-color: black;\n " + "\t\t\t\tborder-top-width: thin;\n " + "\t\t\t\tborder-right-style: solid;\n " + "\t\t\t\tborder-right-color: black;\n " + "\t\t\t\tborder-right-width: thin;\n " + "\t\t\t\tborder-bottom-style: solid;\n " + "\t\t\t\tborder-bottom-color: black;\n " + "\t\t\t\tborder-bottom-width: thin;\n " + "\t\t\t}\n " + "\n" + "\t\t\t.encabezadoLeftRight {\n " + "\t\t\t\tborder-left-style: solid;\n " + "\t\t\t\tborder-left-color: black;\n " + "\t\t\t\tborder-left-width: thin;\n " + "\t\t\t\tborder-right-style: solid;\n " + "\t\t\t\tborder-right-color: black;\n " + "\t\t\t\tborder-right-width: thin;\n " + "\t\t\t}\n " + "\n" + "\t\t\t.encabezadoBottomLeft {\n " + "\t\t\t\tborder-left-style: solid;\n " + "\t\t\t\tborder-left-color: black;\n " + "\t\t\t\tborder-left-width: thin;\n " + "\t\t\t\tborder-bottom-style: solid;\n " + "\t\t\t\tborder-bottom-color: black;\n " + "\t\t\t\tborder-bottom-width: thin;\n " + "\t\t\t}\n " + "\t\t\t.encabezadoBottomLeftRight {\n " + "\t\t\t\tborder-left-style: solid;\n " + "\t\t\t\tborder-left-color: black;\n " + "\t\t\t\tborder-left-width: thin;\n " + "\t\t\t\tborder-bottom-style: solid;\n " + "\t\t\t\tborder-bottom-color: black;\n " + "\t\t\t\tborder-bottom-width: thin;\n " + "\t\t\t\tborder-right-style: solid;\n " + "\t\t\t\tborder-right-color: black;\n " + "\t\t\t\tborder-right-width: thin;\n " + "\t\t\t}\n " + "\n" + "\t\t\t.encabezadoBottomRight {\n " + "\t\t\t\tborder-bottom-style: solid;\n " + "\t\t\t\tborder-bottom-color: black;\n " + "\t\t\t\tborder-bottom-width: thin;\n " + "\t\t\t\tborder-right-style: solid;\n " + "\t\t\t\tborder-right-color: black;\n " + "\t\t\t\tborder-right-width: thin;\n " + "\t\t\t}\n " + "\n" + "\t\t\t.encabezadoLeft {\n " + "\t\t\t\tborder-left-style: solid;\n " + "\t\t\t\tborder-left-color: black;\n " + "\t\t\t\tborder-left-width: thin;\n " + "\t\t\t}\n " + "\n" + "\t\t\t.encabezadoRight {\n " + "\t\t\t\tborder-right-style: solid;\n " + "\t\t\t\tborder-right-color: black;\n " + "\t\t\t\tborder-right-width: thin;\n " + "\t\t\t}\n " + "\n" + "\t\t\t.encabezadoBottom {\n " + "\t\t\t\tborder-bottom-style: solid;\n " + "\t\t\t\tborder-bottom-color: black;\n " + "\t\t\t\tborder-bottom-width: thin;\n " + "\t\t\t}\n " + ".encabezadoAll {\n " + "\tborder-top-style: solid;\n " + "\tborder-top-color: black;\n " + "\tborder-top-width: thin;\n " + "\tborder-left-style: solid;\n " + "\tborder-left-color: black;\n " + "\tborder-left-width: thin;\n " + "\tborder-right-style: solid;\n " + "\tborder-right-color: black;\n " + "\tborder-right-width: thin;\n " + "\tborder-bottom-style: solid;\n " + "\tborder-bottom-color: black;\n " + "\tborder-bottom-width: thin;\n " + "}\n" + "\t\t</style>\n " + "\t</HEAD>\n " + "\t<BODY>\n";

    private static final String htmlBodyF = "\n\t</BODY>\n" + "</HTML>";

    public static Map<String, String> rsToMap(ResultSet rs) throws SQLException {
        Map<String, String> resultado = null;
        ResultSetMetaData rsmd = rs.getMetaData();
        if (rs.next()) {
            resultado = new HashMap<String, String>();
            for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
                resultado.put(rsmd.getColumnName(i).toUpperCase(), rs.getString(rsmd.getColumnName(i)));
            }
        }
        return resultado;
    }

    public static Map<String, String> rsToMapCaseSensitive(ResultSet rs) throws SQLException {
        Map<String, String> resultado = null;
        ResultSetMetaData rsmd = rs.getMetaData();
        if (rs.next()) {
            resultado = new LinkedHashMap<String, String>();
            for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
                int type = rsmd.getColumnType(i);
                String nombreColumna = rsmd.getColumnName(i);
                String valor = StringUtils.trimToEmpty(rs.getString(rsmd.getColumnName(i)));
                log.debug("Object: {}", nombreColumna + "," + type + "," + rs.getString(rsmd.getColumnName(i)));
                resultado.put(nombreColumna, valor);
            }
        }
        return resultado;
    }

    public static List<List<String>> rsToList(ResultSet rs) throws Exception {
        List<List<String>> result = new ArrayList<List<String>>();
        List<String> encabezado = new ArrayList<String>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int numColumns = rsmd.getColumnCount();
        for (int i = 1; i < numColumns + 1; i++) encabezado.add(rsmd.getColumnName(i));
        result.add(encabezado);
        while (rs.next()) {
            List<String> col = new ArrayList<String>();
            for (int i = 1; i < numColumns + 1; i++) {
                if ("Total".equals(rsmd.getColumnName(i)) || "G2F1".equals(rsmd.getColumnName(i)) || "G2F2".equals(rsmd.getColumnName(i)) || "G3F2".equals(rsmd.getColumnName(i))) {
                    DecimalFormat formato = new DecimalFormat("###,###.00");
                    String salida = formato.format(Double.parseDouble(rs.getString(rsmd.getColumnName(i))));
                    col.add(salida);
                } else {
                    col.add(rs.getString(rsmd.getColumnName(i)));
                }
            }
            result.add(col);
        }
        return result;
    }

    public static String rsToTable(ResultSet rs) throws Exception {
        return rsToTable(rs, null, false);
    }

    public static String rsToTable(ResultSet rs, boolean includeClasses) throws Exception {
        return rsToTable(rs, null, includeClasses);
    }

    public static void rsToTable(ResultSet rs, boolean includeClasses, HttpServletResponse resp, String repName) throws Exception {
        rsToTable(rs, null, includeClasses, resp, repName);
    }

    public static String rsToTable(ResultSet rs, Map<String, Integer> anchor) throws Exception {
        return rsToTable(rs, anchor, false);
    }

    public static String rsToTable(ResultSet rs, Map<String, Integer> anchor, boolean includeClasses) throws Exception {
        StringBuffer result = new StringBuffer();
        ResultSetMetaData rsmd = rs.getMetaData();
        boolean results = false;
        int numColumns = rsmd.getColumnCount();
        if (!includeClasses) {
            String tr = "<tr>\n";
            for (int i = 1; i < numColumns + 1; i++) {
                String td = "<td align=\"center\"><b>";
                String column_name = rsmd.getColumnName(i);
                td += column_name;
                td += "</b></td>";
                tr += td;
            }
            tr += "\n</tr>";
            result.append(tr);
        }
        while (rs.next()) {
            StringBuffer tr = new StringBuffer("<tr>\n\t");
            results = true;
            for (int i = 1; i < numColumns + 1; i++) {
                String column_name = rsmd.getColumnName(i);
                int colspan = (anchor != null && anchor.get(column_name) != null && anchor.get(column_name) > 0) ? anchor.get(column_name) : 1;
                String className = (includeClasses ? " class=\"" + (i == numColumns ? "encabezadoLeftRight" : "encabezadoLeft") + "\"" : "");
                String td = "<td align=\"left\" id=\"" + column_name + "\" colspan=\"" + colspan + "\"" + className;
                String val = rs.getString(column_name);
                if (// Hoja
                // de
                // Trabajo
                // Variaciones
                "APLICADO".equals(column_name) || "REINTEGRO".equals(column_name) || "EJERCIDO".equals(column_name) || "Cargos".equals(column_name) || "Abonos".equals(column_name) || "mMovimiento".equals(column_name) || "Total".equals(column_name) || "SaldoInicialDeudor".equals(column_name) || "SaldoInicialAcreedor".equals(column_name) || "MovimientosAcumuladosDebe".equals(column_name) || "MovimientosAcumuladosHaber".equals(column_name) || "SaldoAntesAjusteDeudor".equals(column_name) || "SaldoAntesAjusteAcreedor".equals(column_name) || "AjustePrevDeudor".equals(column_name) || "AjustePrevAcreedor".equals(column_name) || "SaldoPreviosDeudor".equals(column_name) || "SaldoPreviosAcreedor".equals(column_name) || "AjustePresupDeudor".equals(column_name) || "AjustePresupAcreedor".equals(column_name) || "SaldoAjustadosDeudor".equals(column_name) || "SaldoAjustadosAcreedor".equals(column_name) || "AjusteResulDeudor".equals(column_name) || "AjusteResulAcreedor".equals(column_name) || "SaldoFinalDeudor".equals(column_name) || "SaldoFinalAcreedor".equals(column_name) || "DeudorAnterior".equals(column_name) || "AcreedorAnterior".equals(column_name) || "DeudorActual".equals(column_name) || // Cuentas
                "AcreedorActual".equals(column_name) || // Enlace
                "variaciondeudor".equals(column_name) || // Rectificaciones
                "variacionacreedor".equals(column_name) || // al
                "ImporteDeudor".equals(column_name) || // Ejercicio
                "ImporteAcreedor".equals(column_name) || // Afectaciones
                "MovimientoDeudor".equals(column_name) || // Afectaciones
                "MovimientoAcreedor".// a
                equals(// cuentas
                column_name) || // Analisis
                "MovimientoDebe".equals(column_name) || // Analisis
                "MovimientoHaber".// de
                equals(// la
                column_name) || // Incidencia
                "TG1_Gasto_Corriente".equals(column_name) || "TG2_Gasto_de_Inversión".equals(column_name) || "TG3_Gasto_de_Obra_Pública".equals(column_name) || "TG4".equals(column_name) || "TG5".equals(column_name) || "TG6".equals(column_name) || "TG7_Otro_Corriente".equals(column_name) || "TG8_Otro_Inversión".equals(column_name) || "TG9_Gasto_de_Inversión_a_Fideicomi".equals(column_name) || "TG0_Gasto_Corriente_a_Fideicomisos".equals(column_name) || "Corriente".equals(column_name) || // Integracion
                "Inversion".equals(column_name) || // del
                "ObraPublica".equals(column_name) || // Costo
                "Cuentas".equals(column_name) || // Presupuestal
                "ImportePresupuestarios".equals(column_name) || // Presupuestal
                "ImporteNoPresupuestarios".equals(column_name) || "Original".equals(column_name) || "AmplPresupLiquidas".equals(column_name) || "ReduPresupLiquidas".equals(column_name) || "AmplReduCompensadas".equals(column_name) || "ModifAutorizado".equals(column_name) || "Devengado".equals(column_name) || "Ejercido".equals(column_name) || // Analisis
                "DevengadoEjercido".equals(column_name) || // del
                "Pagado".equals(column_name) || // gasto
                "Economias".equals(column_name) || // por
                "TotalDevengadoEjercidoAnterior".equals(column_name) || // Funcion
                "OriginalAutorizado".equals(column_name) || "TotalDevengadoEjercido".equals(column_name) || "DiferenciaCB".equals(column_name) || // Analisis
                "DiferenciaCA".equals(column_name) || // del
                "ReferenciaCB".equals(column_name) || // Gatso
                "ReferenciaCA".equals(column_name) || // Federalizado
                "Diferencia".equals(column_name) || // Gasto
                // Gasto
                "Referencia".// por
                equals(// Entidad
                column_name) || // Federativa
                "ServPersonalesOTG".equals(column_name) || "ServPersonalesTG3".equals(column_name) || "MatSuministrosOTG".equals(column_name) || "MatSuministrosTG3".equals(column_name) || "ServGeneralesOTG".equals(column_name) || "ServGeneralesTG3".equals(column_name) || "TransfAsigSubsiOtrsAyudasOTG".equals(column_name) || "TransfAsigSubsiOtrsAyudasTG3".equals(column_name) || "BienMueInmueIntanOTG".equals(column_name) || "BienMueInmueIntanTG3".equals(column_name) || "InverPúblicaOTG".equals(column_name) || "InverFinaOtrasProvOTG".equals(column_name) || // Recursos
                "InverFinaOtrasProvTG3".equals(column_name) || // Federal
                "PartAportacionesOTG".equals(column_name) || "PartAportacionesTG3".equals(column_name) || "OriginalGastoCorriente".equals(column_name) || "OriginalGastoCapital".equals(column_name) || "OriginalTotal".equals(column_name) || "DevengadoEjercidoGastoCorriente".equals(column_name) || "DevengadoEjercidoGastoCapital".equals(column_name) || "DevengadoEjercidoTotal".equals(column_name)) {
                    td = td.replaceAll("left", "right");
                    td = td + " style='mso-number-format:\"Standard\"' ";
                }
                td = td + " >";
                tr.append(td + (null == val || "".equals(val) ? "&nbsp;" : val.replaceAll(" ", "&nbsp;")) + "</td>");
            }
            tr.append("\n</tr>");
            System.out.println("tr:" + tr);
            result.append(tr);
        }
        if (results && includeClasses) {
            String tr = "\n<tr>\n\t";
            for (int i = 1; i < numColumns + 1; i++) {
                String column_name = rsmd.getColumnName(i);
                int colspan = (anchor != null && anchor.get(column_name) != null && anchor.get(column_name) > 0) ? anchor.get(column_name) : 1;
                String td = "<td id=\"" + column_name + "\" class=\"" + (i == numColumns ? "encabezadoBottomLeftRight" : "encabezadoBottomLeft") + "\" colspan=\"" + colspan + "\" >&nbsp;</td>";
                tr += td;
            }
            tr += "\n</tr>";
            result.append(tr);
        }
        return result.toString();
    }

    public static String ArrayToRow(String[] array, int[] exclude) {
        String rw = OPEN_TR;
        DecimalFormat df = new DecimalFormat("$#,###.#");
        for (int i = 0; i < array.length; i++) {
            boolean excluir = false;
            for (int j = 0; j < exclude.length; j++) if (i == exclude[j])
                excluir = true;
            if (!excluir) {
                if (i == 9 || i == 13 || i == 14) {
                    rw += OPEN_TD + ("".equals(array[i]) || null == array[i] ? EMPTY_ESPACE : df.format(Double.parseDouble(array[i]))) + CLOSE_TD;
                } else {
                    rw += OPEN_TD + ("".equals(array[i]) || null == array[i] ? EMPTY_ESPACE : array[i]) + CLOSE_TD;
                }
            }
        }
        return rw + CLOSE_TR;
    }

    public static String ArrayToRow(String[] array, int[] exclude, int totalColumnas) {
        String rw = OPEN_TR;
        int max = Math.max(totalColumnas, array.length);
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < exclude.length; j++) if (i == exclude[j])
                continue;
            if (i < array.length)
                rw += OPEN_TD + ("".equals(array[i]) ? EMPTY_ESPACE : array[i]) + CLOSE_TD;
            else
                rw += EMPTY_TD;
        }
        return rw + CLOSE_TR;
    }

    public static String ConvertArrayToRow(String[] array, int[] colspan) {
        String rw = OPEN_TR;
        for (int i = 0; i < array.length; i++) {
            int colSpan = 1;
            if (i < colspan.length) {
                colSpan = colspan[i];
            }
            rw += String.format(OPEN_TD_COLSPAN, colSpan) + ("".equals(array[i]) || null == array[i] ? EMPTY_ESPACE : array[i]) + CLOSE_TD;
        }
        return rw + CLOSE_TR;
    }

    public static String ArrayToRowSpan(String[] array, int[] colspan, int totalColumnas) {
        String rw = OPEN_TR;
        int totalSpan = 0;
        for (int i = 0; i < colspan.length; i++) {
            totalSpan += colspan[i];
        }
        int max = Math.max(totalColumnas, totalSpan);
        for (int i = 0; i < array.length; i++) {
            int colSpan = 1;
            if (i < colspan.length) {
                colSpan = colspan[i];
            }
            rw += String.format(OPEN_TD_COLSPAN, colSpan) + ("".equals(array[i]) || null == array[i] ? EMPTY_ESPACE : array[i]) + CLOSE_TD;
        }
        for (int i = array.length; i < max; i++) rw += EMPTY_TD;
        return rw + CLOSE_TR;
    }

    public static String StringToRow(String val, int colspan, int totalColumnas) {
        String rw = OPEN_TR + String.format(OPEN_TD_COLSPAN, String.valueOf(colspan)) + ("".equals(val) ? EMPTY_ESPACE : val) + CLOSE_TD;
        for (int i = 0; i < totalColumnas - colspan; i++) {
            rw += EMPTY_TD;
        }
        return rw + CLOSE_TR;
    }

    public static String MatrixToRow(String[][] m, int[] colspan, int completar) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < m.length; i++) {
            result.append(ArrayToRowSpan(m[i], colspan, completar) + "\n");
        }
        return result.toString();
    }

    public static String[] ht(int tipoEtiqueta) {
        String[] dt = new String[9];
        if (tipoEtiqueta == 2) {
            dt[0] = "application/octet-stream";
            dt[1] = "txt";
            dt[2] = "";
            dt[3] = "|";
            dt[4] = "";
            dt[5] = "";
            dt[6] = "";
            dt[7] = "|\n";
            dt[8] = "\n";
        } else if (tipoEtiqueta == 1) {
            dt[0] = "text/xls";
            dt[1] = "xls";
            dt[2] = htmlBodyI.concat("<table>");
            dt[3] = "<td>";
            dt[4] = "</td>";
            dt[5] = "</table colspan=\"0\">".concat(htmlBodyF);
            dt[6] = "<tr>";
            dt[7] = "</tr>";
            dt[8] = "<td  style='mso-number-format:\"Standard\"'>";
        } else {
            dt[0] = "text/csv";
            dt[1] = "csv";
            dt[2] = "";
            dt[3] = "";
            dt[4] = ",";
            dt[5] = "";
            dt[6] = "";
            dt[7] = "\n";
        }
        return dt;
    }

    public static void rsToTable(ResultSet rs, Map<String, Integer> anchor, boolean includeClasses, HttpServletResponse resp, String reportName) throws Exception {
        ResultSetMetaData rsmd = rs.getMetaData();
        int numColumns = rsmd.getColumnCount();
        int tipoReporte = 0;
        int tipoEtiqueta = 0;
        if (reportName.equals("ContraCuentas")) {
            tipoReporte = 1;
            tipoEtiqueta = 1;
        } else if (reportName.equals("ConciliacionCostoOperacion")) {
            tipoReporte = 2;
            tipoEtiqueta = 1;
        } else if (reportName.equals("ReporteCapitulo1000concepto")) {
            tipoReporte = 3;
            tipoEtiqueta = 1;
        } else if ((reportName.equals("DIOT")) || (reportName.equals("DIM")) || (reportName.equals("DIOT_v2"))) {
            tipoReporte = 4;
            tipoEtiqueta = 2;
        } else if (reportName.equals("ReporteCxPCLC")) {
            tipoReporte = 5;
            tipoEtiqueta = 1;
        }
        String[] etiqueta = ht(tipoEtiqueta);
        resp.setContentType(etiqueta[0]);
        resp.addHeader("Content-Disposition", "inline; filename=\"reporte" + reportName + "_" + System.currentTimeMillis() + "." + etiqueta[1] + "\";");
        PrintWriter out = resp.getWriter();
        String ultimaColumna = "N";
        try {
            if (tipoEtiqueta != 2) {
                out.write(etiqueta[2]);
                out.write(etiqueta[6]);
                for (int i = 1; i <= numColumns; i++) {
                    out.write(etiqueta[3]);
                    String column_name = rsmd.getColumnName(i);
                    out.write(column_name);
                    out.write(etiqueta[4]);
                }
                out.write(etiqueta[7]);
            }
            DecimalFormat formato = new DecimalFormat("###,###,##0.00");
            String val;
            while (rs.next()) {
                out.write(etiqueta[6]);
                for (int i = 1; i <= numColumns; i++) {
                    val = rs.getString(i);
                    if (val == null) {
                        out.write(etiqueta[3]);
                        val = " ";
                    } else if (tipoReporte == 0 && (i == 32 || i == 33 || i == 34)) {
                        out.write(etiqueta[3]);
                        val = formato.format(Double.parseDouble(val));
                        out.write("\"".concat(val).concat("\""));
                    } else if (tipoReporte == 1 && (i == 9)) {
                        out.write(etiqueta[8]);
                        out.write(val);
                    } else if (tipoReporte == 2 && (i == 31 || i == 32 || i == 33)) {
                        out.write(etiqueta[8]);
                        out.write(val);
                    } else if (tipoReporte == 3 && (i == 10 || i == 11 || i == 15)) {
                        out.write(etiqueta[8]);
                        out.write(val);
                    } else if (tipoReporte == 5 && (i == 12)) {
                        out.write(etiqueta[8]);
                        out.write(val);
                    } else if (tipoReporte == 4 && ((i == 22) || (i == 23))) {
                        out.write(etiqueta[3]);
                        out.write(val.replace(".00", ""));
                    } else {
                        if (tipoReporte == 4 && i == 1) {
                            System.out.println("Primer col " + i);
                            // out.write("");
                        } else if (tipoReporte == 4 && i == numColumns) {
                            System.out.println("Ultima col " + numColumns);
                            ultimaColumna = "S";
                            out.write(etiqueta[3]);
                        } else
                            out.write(etiqueta[3]);
                        if (tipoReporte == 0)
                            out.write("\"".concat(val).concat("\""));
                        else
                            out.write(val);
                    }
                    out.write(etiqueta[4]);
                }
                // for
                if (tipoReporte == 4 && "S".equals(ultimaColumna))
                    out.write(etiqueta[8]);
                else
                    out.write(etiqueta[7]);
            }
            // while
            out.write(etiqueta[5]);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            out.flush();
            out.close();
            System.gc();
        }
        // finally
    }
}
