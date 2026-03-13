package com.syc.sai.contabilidad;

import java.sql.Connection;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import com.syc.admin.servlet.ReportsException;
import com.syc.crud.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CuentaPublicaBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(CuentaPublicaBusinessLogic.class);

    private String tblI = "<table colspan=\"0\">";

    private String tblF = "</table>";

    public String generateReportC11IF085() {
        Connection conn = null;
        StringBuffer sb1 = new StringBuffer(CuentaPublicaCuerpoReportes.getReportBody("C11IF085S"));
        StringBuffer sb2 = new StringBuffer(CuentaPublicaCuerpoReportes.getReportBody("C11IF085I"));
        try {
            conn = getConnection();
            sb1.append(CuentaPublicaManager.reportC11IF085SToTable(conn));
            sb2.append(CuentaPublicaManager.reportC11IF085IToTable(conn));
            return tblI + sb1.toString() + sb2.toString() + tblF;
        } catch (Exception e) {
            throw new ReportsException(e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e2) {
                    log.warn("Problemas cerrando la conexion a la base de datos: " + e2);
                }
            conn = null;
        }
    }

    public String generateReportC32AP400() {
        Connection conn = null;
        StringBuffer sb = new StringBuffer(CuentaPublicaCuerpoReportes.getReportBody("C32AP400"));
        try {
            conn = getConnection();
            sb.append(CuentaPublicaManager.reportC32AP400ToTable(conn));
            return tblI + sb.toString() + tblF;
        } catch (Exception e) {
            throw new ReportsException(e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e2) {
                    log.warn("Problemas cerrando la conexion a la base de datos: " + e2);
                }
            conn = null;
        }
    }

    public String generateReportC32AP405() {
        Connection conn = null;
        StringBuffer sb = new StringBuffer(CuentaPublicaCuerpoReportes.getReportBody("C32AP405"));
        try {
            conn = getConnection();
            sb.append(CuentaPublicaManager.reportC32AP405ToTable(conn));
            return tblI + sb.toString() + tblF;
        } catch (Exception e) {
            throw new ReportsException(e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e2) {
                    log.warn("Problemas cerrando la conexion a la base de datos: " + e2);
                }
            conn = null;
        }
    }

    public String generateReportC32AP390() {
        Connection conn = null;
        StringBuffer sb = new StringBuffer(CuentaPublicaCuerpoReportes.getReportBody("C32AP390"));
        try {
            conn = getConnection();
            sb.append(CuentaPublicaManager.reportC32AP390ToTable(conn));
            return tblI + sb.toString() + tblF;
        } catch (Exception e) {
            throw new ReportsException(e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e2) {
                    log.warn("Problemas cerrando la conexion a la base de datos: " + e2);
                }
            conn = null;
        }
    }

    public String generateReport(String reportName, boolean isSP, String[] condiciones) {
        StringBuffer sb = new StringBuffer();
        Connection conn = null;
        try {
            conn = getConnection();
            if (isSP) {
                String rpBdy = CuentaPublicaCuerpoReportes.getReportBody(reportName);
                boolean includeClasses = true;
                if (rpBdy != null && !"".equals(rpBdy))
                    sb.append(rpBdy);
                else
                    includeClasses = false;
                sb.append(CuentaPublicaManager.reportFromSP(conn, reportName, condiciones, includeClasses));
            }
            return tblI + sb.toString() + tblF;
        } catch (Exception e) {
            throw new ReportsException(e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e2) {
                    log.warn("Problemas cerrando la conexion a la base de datos: " + e2);
                }
            conn = null;
        }
    }

    public String generateReport(String[] reportNames, boolean isSP, List<List<String>> condiciones) {
        StringBuffer sb = new StringBuffer();
        try {
            for (int i = 0; i < reportNames.length; i++) {
                String[] cond = null;
                if (condiciones != null && i < condiciones.size())
                    cond = condiciones.get(i).toArray(new String[0]);
                sb.append(generateReport(reportNames[i], isSP, cond));
            }
            return tblI + sb.toString() + tblF;
        } catch (Exception e) {
            throw new ReportsException(e);
        }
    }

    public void generateReport(String reportName, boolean isSP, String[] condiciones, HttpServletResponse resp) {
        StringBuffer sb = new StringBuffer();
        Connection conn = null;
        try {
            conn = getConnection();
            if (isSP) {
                String rpBdy = CuentaPublicaCuerpoReportes.getReportBody(reportName);
                boolean includeClasses = true;
                if (rpBdy != null && !"".equals(rpBdy))
                    sb.append(rpBdy);
                else
                    includeClasses = false;
                CuentaPublicaManager.reportFromSP(conn, reportName, condiciones, includeClasses, resp);
            }
        } catch (Exception e) {
            throw new ReportsException(e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e2) {
                    log.warn("Problemas cerrando la conexion a la base de datos: " + e2);
                }
            conn = null;
        }
    }
}
