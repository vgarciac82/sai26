package com.syc.obrapublica;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReporteF10BeanManager {

    private static final Logger log = LoggerFactory.getLogger(ReporteF10BeanManager.class);

    public static ReporteF10Bean instanceFromRS(ResultSet rs) throws SQLException {
        ReporteF10Bean bean = new ReporteF10Bean();
        ResultSetMetaData metaData = rs.getMetaData();
        String[] beanInfo = new String[metaData.getColumnCount()];
        double[][] resumen = new double[4][2];
        Map<String, Double> totales = new HashMap<String, Double>();
        bean.setUR(rs.getString("cu_ur"));
        bean.setCC(rs.getString("cu_cc"));
        for (int i = 0; i < beanInfo.length; i++) {
            log.trace("Procesando columna " + metaData.getColumnName(i + 1));
            String val = rs.getString(metaData.getColumnName(i + 1));
            if ((i > 0 && i < 5) || (i >= 10 && i <= 20) || (i == 24)) {
                if (totales.get(metaData.getColumnName(i + 1)) == null)
                    totales.put(metaData.getColumnName(i + 1), 0.0d);
                totales.put(metaData.getColumnName(i + 1), totales.get(metaData.getColumnName(i + 1)) + (val == null || "".equals(val) ? 0.0d : Double.parseDouble(val)));
            }
            if ("LP".equalsIgnoreCase(metaData.getColumnName(i + 1)) && !(val == null || "".equals(val))) {
                resumen[0][0] = resumen[0][0] + (val == null || "".equals(val) ? 0.0d : Double.parseDouble(val));
                resumen[0][1] = resumen[0][1] + rs.getDouble("totalcontratosiniva");
            } else if ("I3P".equalsIgnoreCase(metaData.getColumnName(i + 1)) && !(val == null || "".equals(val))) {
                resumen[1][0] = resumen[1][0] + (val == null || "".equals(val) ? 0.0d : Double.parseDouble(val));
                resumen[1][1] = resumen[1][1] + rs.getDouble("totalcontratosiniva");
            } else if ("AD".equalsIgnoreCase(metaData.getColumnName(i + 1)) && !(val == null || "".equals(val))) {
                resumen[2][0] = resumen[2][0] + (val == null || "".equals(val) ? 0.0d : Double.parseDouble(val));
                resumen[2][1] = resumen[2][1] + rs.getDouble("totalcontratosiniva");
            } else if ("CC".equalsIgnoreCase(metaData.getColumnName(i + 1)) && !(val == null || "".equals(val))) {
                resumen[3][0] = resumen[3][0] + (val == null || "".equals(val) ? 0.0d : Double.parseDouble(val));
                resumen[3][1] = resumen[3][1] + rs.getDouble("totalcontratosiniva");
            }
            beanInfo[i] = val;
        }
        bean.setInfo(beanInfo);
        bean.setTotales(totales);
        bean.setResumen(resumen);
        return bean;
    }

    public static ReporteF10Bean updateInstanceFromRS(ResultSet rs, ReporteF10Bean ccBean) throws Exception {
        ResultSetMetaData metaData = rs.getMetaData();
        String[] beanInfo = new String[metaData.getColumnCount()];
        Map<String, Double> totales = ccBean.getTotales();
        double[][] resumen = ccBean.getResumen();
        for (int i = 0; i < beanInfo.length; i++) {
            log.trace("Procesando columna " + metaData.getColumnName(i + 1));
            String val = rs.getString(metaData.getColumnName(i + 1));
            if ((i > 0 && i < 5) || (i >= 10 && i <= 20) || (i == 24)) {
                if (totales.get(metaData.getColumnName(i + 1)) == null)
                    totales.put(metaData.getColumnName(i + 1), 0.0d);
                totales.put(metaData.getColumnName(i + 1), totales.get(metaData.getColumnName(i + 1)) + (val == null || "".equals(val) ? 0.0d : Double.parseDouble(val)));
            }
            if ("LP".equalsIgnoreCase(metaData.getColumnName(i + 1)) && !(val == null || "".equals(val))) {
                resumen[0][0] = resumen[0][0] + (val == null || "".equals(val) ? 0.0d : Double.parseDouble(val));
                resumen[0][1] = resumen[0][1] + rs.getDouble("totalcontratosiniva");
            } else if ("I3P".equalsIgnoreCase(metaData.getColumnName(i + 1)) && !(val == null || "".equals(val))) {
                resumen[1][0] = resumen[1][0] + (val == null || "".equals(val) ? 0.0d : Double.parseDouble(val));
                resumen[1][1] = resumen[1][1] + rs.getDouble("totalcontratosiniva");
            } else if ("AD".equalsIgnoreCase(metaData.getColumnName(i + 1)) && !(val == null || "".equals(val))) {
                resumen[2][0] = resumen[2][0] + (val == null || "".equals(val) ? 0.0d : Double.parseDouble(val));
                resumen[2][1] = resumen[2][1] + rs.getDouble("totalcontratosiniva");
            } else if ("CC".equalsIgnoreCase(metaData.getColumnName(i + 1)) && !(val == null || "".equals(val))) {
                resumen[3][0] = resumen[3][0] + (val == null || "".equals(val) ? 0.0d : Double.parseDouble(val));
                resumen[3][1] = resumen[3][1] + rs.getDouble("totalcontratosiniva");
            }
            beanInfo[i] = val;
        }
        ccBean.setInfo(beanInfo);
        ccBean.setTotales(totales);
        ccBean.setResumen(resumen);
        return ccBean;
    }
}
