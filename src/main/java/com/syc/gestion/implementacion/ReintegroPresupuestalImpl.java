package com.syc.gestion.implementacion;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.Documento;
import com.syc.gestion.TipoCasoInterface;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.contable.core.ReintegrosManager;

public class ReintegroPresupuestalImpl implements TipoCasoInterface {

	public boolean buscaPorExpediente(Caso c, String u_login) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onIniciaCaso(Connection conn, String u_login, Caso c) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void onCreateExpediente(Connection conn, String u_login, Caso c, Aplicacion app) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void onRecibeDocumento(Connection conn, Caso c, Documento d, boolean isSaveEvent) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void onEjecutaCaso(Connection conn, String u_login, Caso c, int id_caso_oper) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void onAvanzaCaso(Connection conn, String u_login, Caso c, int id_caso_oper) throws SQLException {
		// TODO Auto-generated method stub
		try {
			//ComprobacionLaudosManager.avanzaCaso(conn, u_login, c, id_caso_oper);

		} catch (Exception e) {
			throw new SQLException(e);
		}

	}

	public void onTerminaCaso(Connection conn, String u_login, Caso c, String observ, String[] resp, String[] oper, Map data) throws SQLException {
		try {
			int nFolioReintegro = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf("-") + 1));
			ReintegrosManager.descartaReintegro(conn, u_login, nFolioReintegro);
			ReintegrosManager.descartaReintegroComision(conn, u_login, nFolioReintegro);

		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	public void onVenceCaso(Connection conn, String folio, int id_caso, int id_tc, int id_oper, int porc) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void onSolicitaFirmaElectronica(Caso c,  Usuario u, String reportPath) throws SQLException {
		return;
	}
}
