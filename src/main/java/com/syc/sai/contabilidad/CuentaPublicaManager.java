package com.syc.sai.contabilidad;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.admin.servlet.ReportsException;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.contabilidad.utils.db.RSToTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class CuentaPublicaManager {

    private static final Logger log = LoggerFactory.getLogger(CuentaPublicaManager.class);

    public static String reportC32AP390ToTable(Connection conn) {
        ResultSet rs = null;
        CallableStatement cs1 = null;
        try {
            String[][] pregRows = CuentaPublicaCuerpoReportes.preGenC32AP390();
            int[] indices = { 16, 19, 22, 25, 28, 31, 34, 37, 40, 43 };
            String spCll = "{call sp_C32AP390_syc()}";
            cs1 = conn.prepareCall(spCll);
            log.debug("Object: {}", spCll);
            rs = cs1.executeQuery();
            List<List<String>> l = RSToTable.rsToList(rs);
            // Encabezado
            List<String> enc = l.get(0);
            int j = 0;
            for (int i = 5; i < enc.size(); i++) {
                String val = enc.get(i);
                if (val.indexOf("G") >= 0) {
                    String GF = val.substring(val.indexOf("G") + 1, val.indexOf("F"));
                    String F = val.substring(val.indexOf("F") + 1);
                    pregRows[0][indices[j]] = GF;
                    pregRows[1][indices[j]] = F;
                    j++;
                }
            }
            String encRep = "";
            for (int i = 0; i < pregRows.length; i++) {
                encRep += "<tr>";
                for (j = 0; j < pregRows[i].length; j++) encRep += pregRows[i][j];
                encRep += "</tr>";
            }
            String tr = new String("");
            boolean resultado = false;
            for (int idx = 1; idx < l.size(); idx++) {
                List<String> vals = l.get(idx);
                tr += "<tr>\n";
                for (int idxVal = 0; idxVal < vals.size(); idxVal++) {
                    resultado = true;
                    String td = "<td align=\"center\" class=\"" + (idxVal == (vals.size() - 1) ? "encabezadoLeftRight" : "encabezadoLeft") + "\" id=\"" + enc.get(idxVal) + "\">";
                    String val = vals.get(idxVal);
                    td += (null == val || "".equals(val) ? "&nbsp;" : val.replaceAll(" ", "&nbsp;")) + "</td>";
                    tr += td;
                }
                tr += "\n</tr>";
            }
            if (resultado) {
                tr += "\n<tr>";
                for (int idxVal = 0; idxVal < enc.size(); idxVal++) {
                    String td = "<td class=\"" + (idxVal == (enc.size() - 1) ? "encabezadoBottomLeftRight" : "encabezadoBottomLeft") + "\">";
                    td += "&nbsp;</td>";
                    tr += td;
                }
                tr += "\n</tr>";
            }
            return encRep + "\n" + tr;
        } catch (Exception e) {
            throw new ReportsException(e);
        } finally {
            try {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(cs1, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    public static String reportC11IF085SToTable(Connection conn) {
        ResultSet rs = null;
        CallableStatement cs1 = null;
        try {
            String spCll = "{call sp_C11IF085_syc()}";
            cs1 = conn.prepareCall(spCll);
            log.debug("Object: {}", spCll);
            rs = cs1.executeQuery();
            return RSToTable.rsToTable(rs, true);
        } catch (Exception e) {
            throw new ReportsException(e);
        } finally {
            try {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(cs1, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    public static String reportC11IF085IToTable(Connection conn) {
        ResultSet rs = null;
        CallableStatement cs1 = null;
        try {
            Map<String, Integer> anchor = new HashMap<String, Integer>();
            anchor.put("Cuentas", 1);
            anchor.put("MovimientoDeudor", 3);
            anchor.put("Corriente", 3);
            anchor.put("Inversion", 3);
            anchor.put("ObraPublica", 2);
            String spCll = "{call sp_C11IF085I_syc()}";
            cs1 = conn.prepareCall(spCll);
            log.debug("Object: {}", spCll);
            rs = cs1.executeQuery();
            return RSToTable.rsToTable(rs, anchor, true);
        } catch (Exception e) {
            throw new ReportsException(e);
        } finally {
            try {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(cs1, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    public static String reportC32AP400ToTable(Connection conn) {
        ResultSet rs = null;
        CallableStatement cs1 = null;
        try {
            String[][] pregRows = CuentaPublicaCuerpoReportes.preGenC32AP390();
            int[] indices = { 16, 19, 22, 25, 28, 31, 34, 37, 40, 43 };
            String spCll = "{call sp_C32AP400_syc()}";
            cs1 = conn.prepareCall(spCll);
            log.debug("Object: {}", spCll);
            rs = cs1.executeQuery();
            List<List<String>> l = RSToTable.rsToList(rs);
            // Encabezado
            List<String> enc = l.get(0);
            int j = 0;
            for (int i = 5; i < enc.size(); i++) {
                String val = enc.get(i);
                if (val.indexOf("G") >= 0) {
                    String GF = val.substring(val.indexOf("G") + 1, val.indexOf("F"));
                    String F = val.substring(val.indexOf("F") + 1);
                    pregRows[0][indices[j]] = GF;
                    pregRows[1][indices[j]] = F;
                    j++;
                }
            }
            String encRep = "";
            for (int i = 0; i < pregRows.length; i++) {
                encRep += "<tr>";
                for (j = 0; j < pregRows[i].length; j++) encRep += pregRows[i][j];
                encRep += "</tr>";
            }
            String tr = new String("");
            boolean resultado = false;
            for (int idx = 1; idx < l.size(); idx++) {
                List<String> vals = l.get(idx);
                tr += "<tr>\n";
                for (int idxVal = 0; idxVal < vals.size(); idxVal++) {
                    resultado = true;
                    String td = "<td align=\"center\" class=\"" + (idxVal == (vals.size() - 1) ? "encabezadoLeftRight" : "encabezadoLeft") + "\" id=\"" + enc.get(idxVal) + "\">";
                    String val = vals.get(idxVal);
                    td += (null == val || "".equals(val) ? "&nbsp;" : val.replaceAll(" ", "&nbsp;")) + "</td>";
                    tr += td;
                }
                tr += "\n</tr>";
            }
            if (resultado) {
                tr += "\n<tr>";
                for (int idxVal = 0; idxVal < enc.size(); idxVal++) {
                    String td = "<td class=\"" + (idxVal == (enc.size() - 1) ? "encabezadoBottomLeftRight" : "encabezadoBottomLeft") + "\">";
                    td += "&nbsp;</td>";
                    tr += td;
                }
                tr += "\n</tr>";
            }
            return encRep + "\n" + tr;
        } catch (Exception e) {
            throw new ReportsException(e);
        } finally {
            try {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(cs1, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    public static String reportC32AP405ToTable(Connection conn) {
        ResultSet rs = null;
        CallableStatement cs1 = null;
        try {
            String[][] pregRows = CuentaPublicaCuerpoReportes.preGenC32AP390();
            int[] indices = { 16, 19, 22, 25, 28, 31, 34, 37, 40, 43 };
            String spCll = "{call sp_C32AP405_syc()}";
            cs1 = conn.prepareCall(spCll);
            log.debug("Object: {}", spCll);
            rs = cs1.executeQuery();
            List<List<String>> l = RSToTable.rsToList(rs);
            // Encabezado
            List<String> enc = l.get(0);
            int j = 0;
            for (int i = 5; i < enc.size(); i++) {
                String val = enc.get(i);
                if (val.indexOf("G") >= 0) {
                    String GF = val.substring(val.indexOf("G") + 1, val.indexOf("F"));
                    String F = val.substring(val.indexOf("F") + 1);
                    pregRows[0][indices[j]] = GF;
                    pregRows[1][indices[j]] = F;
                    j++;
                }
            }
            String encRep = "";
            for (int i = 0; i < pregRows.length; i++) {
                encRep += "<tr>";
                for (j = 0; j < pregRows[i].length; j++) encRep += pregRows[i][j];
                encRep += "</tr>";
            }
            String tr = new String("");
            boolean resultado = false;
            for (int idx = 1; idx < l.size(); idx++) {
                List<String> vals = l.get(idx);
                tr += "<tr>\n";
                for (int idxVal = 0; idxVal < vals.size(); idxVal++) {
                    resultado = true;
                    String td = "<td align=\"center\" class=\"" + (idxVal == (vals.size() - 1) ? "encabezadoLeftRight" : "encabezadoLeft") + "\" id=\"" + enc.get(idxVal) + "\">";
                    String val = vals.get(idxVal);
                    td += (null == val || "".equals(val) ? "&nbsp;" : val.replaceAll(" ", "&nbsp;")) + "</td>";
                    tr += td;
                }
                tr += "\n</tr>";
            }
            if (resultado) {
                tr += "\n<tr>";
                for (int idxVal = 0; idxVal < enc.size(); idxVal++) {
                    String td = "<td class=\"" + (idxVal == (enc.size() - 1) ? "encabezadoBottomLeftRight" : "encabezadoBottomLeft") + "\">";
                    td += "&nbsp;</td>";
                    tr += td;
                }
                tr += "\n</tr>";
            }
            return encRep + "\n" + tr;
        } catch (Exception e) {
            throw new ReportsException(e);
        } finally {
            try {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(cs1, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    public static String reportFromSP(Connection conn, String repName, String[] condiciones, boolean includeClasses) {
        ResultSet rs = null;
        CallableStatement cs1 = null;
        try {
            String spName = "sp_" + repName + "_syc";
            String spCll = "{call " + spName + "(";
            String token = "";
            if (condiciones != null) {
                for (int i = 0; i < condiciones.length; i++) {
                    spCll += token + "?";
                    token = ", ";
                }
            }
            spCll += ")}";
            cs1 = conn.prepareCall(spCll);
            log.debug("Object: {}", spCll);
            if (condiciones != null) {
                for (int i = 0; i < condiciones.length; i++) {
                    cs1.setString(i + 1, condiciones[i]);
                    log.debug("Object: {}", i + " " + condiciones[i]);
                }
            }
            rs = cs1.executeQuery();
            conn.commit();
            return RSToTable.rsToTable(rs, includeClasses);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ReportsException(e);
        } finally {
            try {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(cs1, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    public static void reportFromSP(Connection conn, String repName, String[] condiciones, boolean includeClasses, HttpServletResponse resp) {
        ResultSet rs = null;
        CallableStatement cs1 = null;
        try {
            String spName = "sp_" + repName + "_syc";
            String spCll = "{call " + spName;
            /*
				if(repName.equals("BalanzaDetalle"))
					spCll=spCll +" @Salida OUTPUT"+ " (";
				else
				spCll= spCll + "(";
				*/
            spCll = spCll + " (";
            String token = "";
            if (condiciones != null) {
                for (int i = 0; i < condiciones.length; i++) {
                    spCll += token + "?";
                    token = ", ";
                }
            }
            spCll += ")}";
            cs1 = conn.prepareCall(spCll);
            log.debug("Object: {}", spCll);
            if (condiciones != null) {
                for (int i = 0; i < condiciones.length; i++) {
                    cs1.setString(i + 1, condiciones[i]);
                    log.debug("Object: {}", i + " " + condiciones[i]);
                }
            }
            //spCll="{call sp_BalanzaDet_syc   ('2014','10', 'BalanzaDet', '1', '7')}";
            //cs1 = conn.prepareCall(spCll);
            rs = cs1.executeQuery();
            conn.commit();
            RSToTable.rsToTable(rs, includeClasses, resp, repName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ReportsException(e);
        } finally {
            try {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(cs1, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }
}
