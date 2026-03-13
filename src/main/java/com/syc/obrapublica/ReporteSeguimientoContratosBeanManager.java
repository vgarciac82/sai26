package com.syc.obrapublica;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class ReporteSeguimientoContratosBeanManager {
	private static final Logger	log	= Logger.getLogger(ReporteSeguimientoContratosBeanManager.class);

	public static ReporteSeguimientoContratosBean instanceFromRS(ResultSet rs) throws Exception {
		ReporteSeguimientoContratosBean bean = new ReporteSeguimientoContratosBean();
		ResultSetMetaData metaData = rs.getMetaData();

		String[] beanInfo = new String[metaData.getColumnCount()];
		Map<String, Double> totales = new HashMap<String, Double>();

		bean.setUR(rs.getString("cu_ur"));
		bean.setIdArea(rs.getString("id_area"));

		for (int i = 0; i < beanInfo.length; i++) {
			log.trace("Procesando columna " + metaData.getColumnName(i + 1));
			String val = rs.getString(metaData.getColumnName(i + 1));

			if (i == 4 || i == 9 || i == 10) {

				if (totales.get(metaData.getColumnName(i + 1)) == null)
					totales.put(metaData.getColumnName(i + 1), 0.0d);

				totales.put(metaData.getColumnName(i + 1), totales.get(metaData.getColumnName(i + 1)) + (val == null || "".equals(val) ? 0.0d : Double.parseDouble(val)));
			}

			beanInfo[i] = val;
		}

		bean.setInfo(beanInfo);
		bean.setTotales(totales);
		return bean;
	}

	public static ReporteSeguimientoContratosBean updateInstanceFromRS(ResultSet rs, ReporteSeguimientoContratosBean ccBean) throws Exception {
		ResultSetMetaData metaData = rs.getMetaData();

		String[] beanInfo = new String[metaData.getColumnCount()];
		Map<String, Double> totales = ccBean.getTotales();

		for (int i = 0; i < beanInfo.length; i++) {
			log.trace("Procesando columna " + metaData.getColumnName(i + 1));
			String val = rs.getString(metaData.getColumnName(i + 1));

			if (i == 4 || i == 9 || i == 10)  {
				if (totales.get(metaData.getColumnName(i + 1)) == null)
					totales.put(metaData.getColumnName(i + 1), 0.0d);

				totales.put(metaData.getColumnName(i + 1), totales.get(metaData.getColumnName(i + 1)) + (val == null || "".equals(val) ? 0.0d : Double.parseDouble(val)));
			}
			beanInfo[i] = val;
		}

		ccBean.setInfo(beanInfo);
		ccBean.setTotales(totales);
		return ccBean;

	}
}
