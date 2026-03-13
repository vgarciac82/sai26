package com.axtel.contabilidad.notification;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;


public class NotificaRetencionLaudos {

	public static void main( String[] args ) throws Exception {
		Connection conn = null;

		try {
			conn = Util.getStandAloneConnection();

			String notificaionPara = getNotificacionLAUDOS( conn );
			String[] cuerpoCorreo = generaNotificacion( conn );
			String[] cheques = getCheques( conn );
			int total = cuerpoCorreo.length;

			for ( int i = 0; i < total; i++ ) {
				AlarmaManager.procesaAlarmaCNF( conn, null, null, null, "Alerta LAUDOS Cheque #" + cheques[i], notificaionPara, cuerpoCorreo[i] );
			}

		} finally {
			CloseObject.closeObject( conn );
		}

	}

	private static String getNotificacionLAUDOS( Connection conn ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String correo = null;

		String querySelect = "SELECT GP_VALOR FROM CG_GRUPO_PROPIEDADES WHERE GP_NOMBRE = 'CORREO_ALERTA_CHEQUE_LAUDO'";

		try {
			ps = conn.prepareStatement( querySelect );

			rs = ps.executeQuery();

			if ( rs.next() )
				correo = rs.getString( 1 );
			else
				throw new Exception( "No se encontro correo destinatario. " );

			if ( StringUtils.isBlank( correo ) )
				throw new Exception( "No se encontro correo destinatario. " );

			return correo;

		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	private static String[] generaNotificacion( Connection conn ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> mensaje = new ArrayList<String>();

		String querySelect = "SELECT cuerpoCorreo FROM v_alertaLaudos WITH(NOLOCK)";

		try {
			ps = conn.prepareStatement( querySelect );

			rs = ps.executeQuery();

			while ( rs.next() ) {
				mensaje.add( rs.getString( 1 ) );
			}

			return mensaje.toArray( new String [mensaje.size()] );

		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	private static String[] getCheques( Connection conn ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> nNumCheque = new ArrayList<String>();

		String querySelect = "SELECT nNumCheque FROM v_alertaLaudos WITH(NOLOCK)";

		try {
			ps = conn.prepareStatement( querySelect );

			rs = ps.executeQuery();

			while ( rs.next() ) {
				nNumCheque.add( rs.getString( 1 ) );
			}

			return nNumCheque.toArray( new String [nNumCheque.size()] );

		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

}
