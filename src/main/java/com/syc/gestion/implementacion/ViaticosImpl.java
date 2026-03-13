																																																																																																															package com.syc.gestion.implementacion;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import com.axtel.egresos.viaticos.Agenda;
import com.axtel.egresos.viaticos.ViaticosBusinessLogic;
import com.axtel.egresos.viaticos.core.AgendaDAO;
import com.axtel.egresos.viaticos.core.ComisionDAO;
import com.axtel.egresos.viaticos.core.GeneraSolicitudViaticos;
import com.axtel.egresos.viaticos.core.ViaticosDAO;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.Documento;
import com.syc.gestion.TipoCasoInterface;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionServlet;

public class ViaticosImpl implements TipoCasoInterface {

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
			int nFolio = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf("-") + 1));
			int idAgenda = AgendaDAO.existeAgenda( conn, nFolio );
			int tieneFirmantes = ComisionDAO.tieneFirmantes( conn, nFolio );
			String estatus = ComisionDAO.consultaEstatusComision(conn, nFolio);
			
			//VALIDAR SI ESTA CANCELADO YA NO ENVIA LEYENDA
			if (tieneFirmantes > 0 && !estatus.equalsIgnoreCase( "C" )) {
				Agenda agenda= AgendaDAO.consultaFechaAgendaAcumulada( conn, nFolio );
				int idEmpleado = ComisionDAO.consultaNoEmpleado( conn, nFolio );
				
				ComisionDAO.insertarBitacoraRevertir (conn, nFolio, agenda, idEmpleado, "Cancela comision OnTerminaCaso");
				GeneraSolicitudViaticos.revertirAsistencia( conn, agenda, idEmpleado );
			}
			
			if(idAgenda > 0) {
				ViaticosDAO.borrarViaticosComision( conn, nFolio );
				AgendaDAO.borrarAgendaComision( conn, nFolio, u_login );
			}
			
			ComisionDAO.borrarFirmantes( conn, nFolio );
			ComisionDAO.borrarComision( conn, nFolio, u_login );

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
