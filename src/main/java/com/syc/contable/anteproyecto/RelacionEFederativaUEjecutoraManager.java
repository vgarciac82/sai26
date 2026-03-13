package com.syc.contable.anteproyecto;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Map;

import org.apache.log4j.Logger;

import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class RelacionEFederativaUEjecutoraManager {

	private static Logger	log	= Logger.getLogger(RelacionEFederativaUEjecutoraManager.class);

	public static int insertaRenglonEFederativaCartera(Connection conn, Map<String, String> infoRenglon) throws Exception {
		Statement stmnt = null;
		int r = 0;
		try {
			log.trace("Iniciando insercion de renglon");
			stmnt = conn.createStatement();
			r = stmnt.executeUpdate(Util.genInsertFromMap("tCatalogoEFederativaUEjecutora", infoRenglon));
			log.trace("Se inserto " + r + "registros");
			return r;
		} finally {
			CloseObject.closeObject(stmnt, false);
		}

	}

}
