package com.syc.contable.anteproyecto;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;

import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class ComparaSaiSicopManager {

	private static Logger	log	= Logger.getLogger(ComparaSaiSicopManager.class);

	public static File generaArchivoProyecto(Connection conn, boolean esAdministrador, String ur, int ejercicioFiscal, int[] capitulosExcluir, String nombreArchivo) throws Exception {
		String query = "SELECT	EP, " + "	MONTO AS monto_anual, " + "	CONVERT(int,'0') as monto_enero, " + "	CONVERT(int,'0') as monto_febrero, " + "	CONVERT(int,'0') as monto_marzo, " + "	CONVERT(int,'0') as monto_abril, " + "	CONVERT(int,'0') as monto_mayo, " + "	CONVERT(int,'0') as monto_junio, "
			+ "	CONVERT(int,'0') as monto_julio, " + "	CONVERT(int,'0') as monto_agosto, " + "	CONVERT(int,'0') as monto_septiembre, " + "	CONVERT(int,'0') as monto_octubre, " + "	CONVERT(int,'0') as monto_noviembre, " + "	CONVERT(int,'0') as monto_diciembre " + "  FROM	tProyecto_PF "
			+ " WHERE	aEjercicioFiscal = " + ejercicioFiscal + (esAdministrador ? "" : " AND cUnidadEjecutora = '" + ur + "'");
		ResultSet rs = null;
		Statement stmnt = null;

		try {
			stmnt = conn.createStatement();
			if (!esAdministrador && capitulosExcluir != null && capitulosExcluir.length > 0)
				for (int i = 0; i < capitulosExcluir.length; i++)
					query += " AND cPartida NOT LIKE '" + capitulosExcluir[i] + "%' ";

			rs = stmnt.executeQuery(query);
			return Util.ExcelFromRS(rs, nombreArchivo);
		} finally {
			CloseObject.closeObject(rs, false);
			CloseObject.closeObject(stmnt, false);
		}

	}

	/**
	 * Inserta la version de la comparación
	 */
	public static int insertaVersionProyecto(Connection conn, int ejercicioFiscal, String uLogin) throws Exception {
		String query = "INSERT INTO t_control_version_Comparacionsaisicop(nEjercicio_Fiscal, cUsuario_Carga) VALUES(?,?)";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, ejercicioFiscal);
			ps.setString(2, uLogin);
			return ps.executeUpdate();
		} finally {
			CloseObject.closeObject(ps, false);
		}
	}

	/**
	 * Elimina todo el contenido de la tabla de comparación.
	 */
	public static int limpiaProyecto(Connection conn) throws Exception {
		String query = "DELETE FROM tcomparacion_sai_sicop";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(query);
			return ps.executeUpdate();
		} finally {
			CloseObject.closeObject(ps, false);
		}
	}

	public static int obtieneDatosEP(Connection conn, String[] renglonInfo, PreparedStatement psInsertaMomento) throws Exception {

		String ep = "";
		int compPs = 15;
		ep = renglonInfo[2] + "." + renglonInfo[0] + "." + renglonInfo[1] + "." + renglonInfo[3] + "." + renglonInfo[4] + "." + ((renglonInfo[5].length() == 1 ? ("0" + renglonInfo[5]) : renglonInfo[5])) + "." + ((renglonInfo[6].length() == 1 ? ("0" + renglonInfo[6]) : renglonInfo[6])) + "." + ((renglonInfo[7].length() == 1 ? ("00" + renglonInfo[7]) : renglonInfo[7])) + "." + renglonInfo[8] + "." + renglonInfo[9] + renglonInfo[10] + renglonInfo[11]
			+ (renglonInfo[12].length() == 1 ? ("0" + renglonInfo[12]) : renglonInfo[12]) + "." + renglonInfo[13] + "." + renglonInfo[14] + "." + (renglonInfo[15].length() == 1 ? ("0" + renglonInfo[15]) : renglonInfo[15]) + "."
			+ (renglonInfo[16].equals("0") ? ("0000000000" + renglonInfo[16]) : renglonInfo[16]) + "." + (renglonInfo[17].substring(7)) + "." + ((renglonInfo[18]).equals("RE") ? ("0" + renglonInfo[18]) : (renglonInfo[18].length() == 1 ? ("B0" + renglonInfo[18]) : "B" + renglonInfo[18]));
		log.debug("EP: " + ep);

		List<PosicionArchivoSICOP> momentos = PosicionArchivoSICOPManager.cargaMomentos(conn);
		int insertados = 0;
		for (int c = 0; c < momentos.size(); c++) {
			PosicionArchivoSICOP momento = momentos.get(c);

			psInsertaMomento.setString(1, ep);
			psInsertaMomento.setString(2, momento.getcuentas_sai());

			int j = 0;
			for (int k = 3; k <= compPs; k++) {
				psInsertaMomento.setString(k, renglonInfo[momento.getcol_inicio() + j].replaceAll("[,]", "."));
				j++;

			}
			insertados = psInsertaMomento.executeUpdate();
		}

		return insertados;

	}

	public static List<List<String>> consultaExportaReporte(Connection conn, String momento) throws SQLException {
		List<List<String>> listado = new ArrayList<List<String>>();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sQueryWhere ="";
		if (null == momento || "".equals(momento) )
			sQueryWhere = "";
		else
			sQueryWhere = " where momento in (" + momento + ")" ;
		String sQuery = "";
		sQuery = "SELECT * FROM vcomparativo_sai_sicop with(nolock)" + sQueryWhere;
		try {
			pstm = conn.prepareStatement(sQuery);
			rs = pstm.executeQuery();
			while (rs.next()) {
				List<String> renglon = new ArrayList<String>();
				renglon.add(rs.getString("EP"));
				renglon.add(rs.getString("momento"));
				renglon.add(rs.getString("diferencia"));
				renglon.add(rs.getString("diferencia_ENERO"));
				renglon.add(rs.getString("diferencia_FEBRERO"));
				renglon.add(rs.getString("diferencia_MARZO"));
				renglon.add(rs.getString("diferencia_ABRIL"));
				renglon.add(rs.getString("diferencia_MAYO"));
				renglon.add(rs.getString("diferencia_JUNIO"));
				renglon.add(rs.getString("diferencia_JULIO"));
				renglon.add(rs.getString("diferencia_AGOSTO"));
				renglon.add(rs.getString("diferencia_SEPTIEMBRE"));
				renglon.add(rs.getString("diferencia_OCTUBRE"));
				renglon.add(rs.getString("diferencia_NOVIEMBRE"));
				renglon.add(rs.getString("diferencia_DICIEMBRE"));
				listado.add(renglon);
			}
		} finally {
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return listado;
	}

	public static Sheet consultaExportaReporteExcel(Connection conn, Sheet hoja, String momento) throws Exception {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sQueryWhere ="";
		if (null == momento || "".equals(momento))
			sQueryWhere = "";
		else
			sQueryWhere = " where momento in (" + momento + ")" ;
		String sQuery = "";
		sQuery = "SELECT * FROM vcomparativo_sai_sicop_excel with(nolock) " + sQueryWhere;
		try {
			pstm = conn.prepareStatement(sQuery);
			rs = pstm.executeQuery();
			return Util.resultSetToExcel(rs, hoja, 9);
		} finally {
			CloseObject.closeObject(rs, false);
			CloseObject.closeObject(pstm, false);
		}
	}
}
