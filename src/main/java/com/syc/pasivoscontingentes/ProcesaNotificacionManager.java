package com.syc.pasivoscontingentes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import org.apache.log4j.Logger;

import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.registroingresos.RegistroIngresosManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class ProcesaNotificacionManager {

	public static final Logger log = Logger.getLogger(RegistroIngresosManager.class);
	
	public ProcesaNotificacionManager() {
		super();
	}
	
	public int validaInfo( Connection conn ) throws Exception {
		ResultSet rs = null;
		PreparedStatement ps = null;
		StringBuilder query = new StringBuilder();
		int total = 0;
		
		query.append( "SELECT COUNT(*) AS total FROM v_alertaLaudos WITH (NOLOCK) " );
		
		ps = conn.prepareStatement(query.toString());		

		rs = ps.executeQuery();
		
		if (rs.next()) {
			total = rs.getInt(1);
		}
		
		return total;
		
	}
	
	public void envioAlertas( Connection conn) throws Exception {

		ResultSet rs = null;
		PreparedStatement ps = null;
		ResultSet rs2 = null;
		PreparedStatement ps2 = null;				
		String subject = "Notificacion de Cheque de LAUDOS";

		log.info( "Correo: " + subject );

		try {
			ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic( GestionInterface.ATT_CONEXION );

			String body = getCuerpoCorreo(conn);						
			String dest = cabl.getSystemSetting( "CORREO_ALERTA_CHEQUE_LAUDO" );
			
			AlarmaManager.procesaAlarmaCNF( conn, "", null, null, subject, dest, body );

		} catch ( Exception e ) {
			log.warn( e, e );
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
			CloseObject.closeObject( rs2 );
			CloseObject.closeObject( ps2 );
		}
	}
	
	static String getCuerpoCorreo(Connection conn) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuilder query = new StringBuilder();
		String cuerpoCorreo = null;
		int anticipo = 0;
		String CXP = null;
		String CXP_OA = null;
		
		try {
			
			query.append( "SELECT cuerpoCorreo " );
			query.append( " , nFoliocaja AS anticipo " );
			query.append( " , ISNULL(caNoContrarrecibo,'') AS CXP " );
			query.append( " , ISNULL(caNoContrarreciboOA,'') AS CXP_OA " );			
			query.append( "FROM v_alertaLaudos WITH (NOLOCK) " );
						
			ps = conn.prepareStatement(query.toString());
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				cuerpoCorreo = rs.getString(1);
				anticipo = rs.getInt(2);
				CXP = rs.getString(3);
				CXP_OA = rs.getString(4);				
			}
			else
				throw new Exception("No fue posible encontrar el cConcepto para el folio ");

			
			String mailBody = "<html>";
				mailBody += "\n\t<head>";
				mailBody += "\n\t<meta charset=\"UTF-8\">";
				mailBody += "\n\t<style type=\"text/css\">";
				mailBody += "\n\tbody {";
				mailBody += "\n\t\t	font-family: verdana, arial, sans-serif;";
				mailBody += "\n\t\t	font-size: 12px;";
				mailBody += "\n\t}";
	
				mailBody += "\n\ttable {";
				mailBody += "\n\t\tfont-size: 12px;";
				mailBody += "\n\t\tcolor: #333333;";
				mailBody += "\n\t\tborder-width: 1px;";
				mailBody += "\n\t\tborder-color: #666666;";
				mailBody += "\n\t\tborder-collapse: collapse;";
				mailBody += "\n\t}";
	
				mailBody += "\n\ttable th {";
				mailBody += "\n\t\tborder-width: 1px;";
				mailBody += "\n\t\tpadding: 8px;";
				mailBody += "\n\t\tborder-style: solid;";
				mailBody += "\n\t\tborder-color: #666666;";
				mailBody += "\n\t\tbackground-color: #dedede;";
				mailBody += "\n\t}";
	
				mailBody += "\n\ttable td {";
				mailBody += "\n\t\tborder-width: 1px;";
				mailBody += "\n\t\tpadding: 8px;";
				mailBody += "\n\t\tborder-style: solid;";
				mailBody += "\n\t\tborder-color: #666666;";
				mailBody += "\n\t\tbackground-color: #ffffff;";
				mailBody += "\n\t}";
				mailBody += "\n\t</style>";
				mailBody += "</head>";
				mailBody += "\n\t<body>";
				mailBody += "\n\t\t<form id=\"Form\" name=\"FormNotificacionChequeLaudo\" >";
				mailBody += "	<br />";
				mailBody += "	<p>";
				mailBody += "		Se informa que estos cheques de LAUDOS fueron cobrados favoy de continuar con los procesos correspondientes a dicho tramite.";
				mailBody += "	</p>";
				mailBody += "	<p>";
				mailBody += "		<b>  </b>";
				mailBody += "	</p>";
				mailBody += "	<br />";
	
				mailBody += "	<table>";
				mailBody += "		<thead>";
				mailBody += "			<tr>";
				mailBody += "				<th>Concepto</th>";
				mailBody += "				<th>Anticipo</th>";
				mailBody += "				<th>CXP</th>";
				mailBody += "				<th>CXP OA</th>";				
				mailBody += "			</tr>";
				mailBody += "		</thead>";
				mailBody += "		<tbody>";
	
				mailBody += "<tr>";
				mailBody += "\n<td>" + cuerpoCorreo + "</td>";
				mailBody += "\n<td>" + anticipo + "</td>";
				mailBody += "\n<td>" + CXP + "</td>";
				mailBody += "\n<td>" + CXP_OA + "</td>";				
				mailBody += "</tr>";
				mailBody += "		</tbody>";
				mailBody += "	</table>";
				mailBody += "	<br />";
				mailBody += "	<br />";
				mailBody += "	<p>";
				mailBody += "		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday();
				mailBody += "	</p>";
				mailBody += "	</form>";
				mailBody += "</body>";
				mailBody += "</html>";

			return mailBody;

		} finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(ps);
		}

	}
			
}