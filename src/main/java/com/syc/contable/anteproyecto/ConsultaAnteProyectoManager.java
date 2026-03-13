package com.syc.contable.anteproyecto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;

import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class ConsultaAnteProyectoManager {

	private static Logger	log	= Logger.getLogger(ConsultaAnteProyectoManager.class);

	public static Sheet consultaExportaReporteExcel(Connection conn, Sheet hoja, String cUE, String cUN, String cEP, String mMontoCalculado, String mMontoOptimo, String mMontoIrreductible) throws Exception {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sQuery = "";
		String sQueryWhere ="";
		String token = "";
		if (cUE != "" )
		{
			token =" AND ";
			sQueryWhere += token +  " cUE = '" + cUE + "' ";
		}

		if (cUN != "" )
		{
			token =" AND ";
			sQueryWhere += token +  " cUN = '" + cUN + "' ";
		}
		if (cEP != "" )
		{
			token =" AND ";
			sQueryWhere += token + " cEP = '" + cEP + "' ";
		}
		if (mMontoCalculado != "" )
		{
			token =" AND ";
			sQueryWhere += token +  " mMontoCalculado = '" + mMontoCalculado + "' ";
		}
		if (mMontoOptimo != "" )
		{
			token =" AND ";
			sQueryWhere += token +  " mMontoOptimo = '" + mMontoOptimo + "' ";
		}
		if (mMontoIrreductible != "" )
		{
			token =" AND ";
			sQueryWhere += token +  " mMontoIrreductible = '" + mMontoIrreductible + "' ";
		}

		sQuery = "SELECT * FROM vtanteproyecto_autorizadodt with(nolock) WHERE 1=1  " + sQueryWhere;
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
