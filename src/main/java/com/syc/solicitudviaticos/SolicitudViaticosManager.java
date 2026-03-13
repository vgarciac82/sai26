package com.syc.solicitudviaticos;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.gestion.TipoCasoInterface;
import com.syc.gestion.core.BitacoraManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.CasoOperacionManager;
import com.syc.gestion.core.Operacion;
import com.syc.gestion.core.OperacionManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioManager;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.solicitudviaticos.NotificacionSolicitudViaticos.AgendaNotificacion;

import common.Logger;

public class SolicitudViaticosManager {
	public static String		msgRetorno	= "";
	private static final Logger	log			= Logger.getLogger(SolicitudViaticosManager.class);

	public static void creaViaticoTransporte(Connection conn, BigDecimal monto, int tipoTransporte, int folioSolicitud, int noKm, String origen, String placas, String tieneVales) throws Exception {
		PreparedStatement ps = null;

		String query = "INSERT INTO dbo.tSolicitudViaticosTransporte " + "( nFolioSolicitudViaticos, nIdTransporte, cOrigen, nKmRecorrer, cPlaca, nHayVales, mMonto) " + "VALUES  ( ?, ?, ?, ?, ?, ?, ? )";

		try {

			ps = conn.prepareStatement(query);
			ps.setInt(1, folioSolicitud);
			ps.setInt(2, tipoTransporte);
			ps.setString(3, origen);
			ps.setInt(4, noKm);
			ps.setString(5, placas);
			ps.setString(6, tieneVales);
			ps.setBigDecimal(7, monto);

			int afectados = ps.executeUpdate();

			log.info("Se insertaron " + afectados + " para la solicituda de viaticos: " + folioSolicitud);
		} finally {
			CloseObject.closeObject(ps, false);
		}

	}

	public static void creaViatico(Connection conn, int folioViatico, String fechaCaptura, String loginCaptura, String numeroEmpleadoBeneficiario, String esSolicitudPropia, String cUnidadEjecutora, String eMail, String cuentaBanco) throws Exception {

		log.info("Insertando informacion viaticos: " + folioViatico + "," + fechaCaptura + "," + loginCaptura + "," + numeroEmpleadoBeneficiario + "," + esSolicitudPropia + "," + cUnidadEjecutora);
		String queryInsert = "INSERT INTO tSolicitudViaticosEncabezado " 
		                   + "        ( nFolioSolicitudViaticos , " 
		                   + "          dFechaCaptura , " 
		                   + "          cLoginCaptura , " 
		                   + "          nNumEmpleadoBeneficiario , " 
		                   + "          lEsSolicitudPropia , " 
		                   + "          cUnidadEjecutora, "
		                   + "         cCorreoEmpleadoBeneficiario, "
		                   + "         subCuentasBancaria "
		                   + "        )" 
		                   + "VALUES  ( ?," 
		                   + "          ?," 
		                   + "          ?," 
		                   + "          ?," 
		                   + "          ?," 
		                   + "          ?," 
		                   + "          ?,"
		                   + "          ?" 
		                   + "        )";

		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(queryInsert);

			ps.setInt(1, folioViatico);
			ps.setDate(2, new Date(Util.dateLong(fechaCaptura, "dd/MM/yyyy")));
			ps.setString(3, loginCaptura);
			ps.setString(4, numeroEmpleadoBeneficiario);
			ps.setInt(5, "S".equalsIgnoreCase(esSolicitudPropia) ? 1 : 0);
			ps.setString(6, cUnidadEjecutora);
			ps.setString(7, eMail);
			ps.setString(8, cuentaBanco);
			
			int afectados = ps.executeUpdate();
			log.info("Se insertaron " + afectados + " para la solicituda de viaticos: " + folioViatico);
			
		} finally {
			CloseObject.closeObject(ps);
		}

	}

	public static void creaFirmantes(Connection conn, int folioViatico) throws Exception {
		CallableStatement cs = null;

		try {
			String query = "{call sp_firmantesSolicitudViaticos (?)}";

			cs = conn.prepareCall(query);
			cs.setInt(1, folioViatico);

			cs.executeUpdate();

		} finally {
			CloseObject.closeObject(cs, false);
		}

	}

	public static void creaViaticoAgenda(Connection conn, int folioSolicitud, String fInicio, String fFin, String lEsAnticipado, String lEsNacional, int nIdPais, int ID_ESTADO, int ID_MUNICIPIO, String cLocalidad, String cMotivo, BigDecimal mCuotaPorDia, int nIDPaquete, int nIDHomologacion,
		String cIDTipoMoneda, String cActividadesAgenda) throws Exception {
		PreparedStatement ps = null;
		PreparedStatement psRow = null;
		ResultSet rs = null;

		int nDocRenglon = 0;

		String query = "INSERT INTO dbo.tSolicitudViaticosAgenda " + "( nFolioSolicitudViaticos, nDocRenglon, fInicio, fFin, lEsAnticipado, lEsNacional, nIdPais, ID_ESTADO, ID_MUNICIPIO, cLocalidad, cMotivo, mCuotaPorDia, nIDPaquete, nIDHomologacion, cIDTipoMoneda, cActividadesAgenda ) "
			+ "VALUES  ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

		try {

			psRow = conn.prepareStatement(" SELECT ISNULL(MAX(nDocRenglon), 0) + 1 AS renglon FROM tSolicitudViaticosAgenda WITH (NOLOCK) WHERE nFolioSolicitudViaticos = ? ");
			psRow.setInt(1, folioSolicitud);
			rs = psRow.executeQuery();

			if (rs.next()) {
				nDocRenglon = rs.getInt("renglon");
			}

			if (nDocRenglon > 0) {
				ps = conn.prepareStatement(query);
				ps.setInt(1, folioSolicitud);
				ps.setInt(2, nDocRenglon);
				ps.setDate(3, new Date(Util.dateLong(fInicio, "dd/MM/yyyy")));
				ps.setDate(4, new Date(Util.dateLong(fFin, "dd/MM/yyyy")));
				ps.setString(5, lEsAnticipado);
				ps.setString(6, lEsNacional);
				ps.setInt(7, nIdPais);
				ps.setInt(8, ID_ESTADO);
				ps.setInt(9, ID_MUNICIPIO);
				ps.setString(10, cLocalidad);
				ps.setString(11, cMotivo);
				ps.setBigDecimal(12, mCuotaPorDia);
				ps.setInt(13, nIDPaquete);
				ps.setInt(14, nIDHomologacion);
				ps.setString(15, cIDTipoMoneda);
				ps.setString(16, cActividadesAgenda);

				int afectados = ps.executeUpdate();

				log.info("Se insertaron " + afectados + " para la solicitud de viaticos agenda: " + folioSolicitud);
			}

		} finally {
			CloseObject.closeObject(ps, false);
		}

	}

	public static NotificacionSolicitudViaticos getPrimerAutorizacion(Connection conn, int folioSolicitud) throws Exception {
		String querySel = "SELECT	solicitud.nfoliosolicitudviaticos,  " 
						+ "			solicitud.nDocRenglon,  " 
						+ "			solicitud.noempleado,  " 
						+ "			solicitud.idplaza,   " 
						+ "			solicitud.puesto,   " 
						+ "			solicitud.nombre,   "
						+ "			solicitud.nautorizado,  " 
						+ "			empleados.d_email " 
						+ "  FROM	tsolicitudviaticosfirmantes solicitud WITH(NOLOCK) " 
						+ "			LEFT OUTER JOIN " 
						+ "			vEmpleadoViaticante empleados WITH(NOLOCK) "
						+ "			ON solicitud.noempleado = empleados.c_empleado " 
						+ " WHERE	solicitud.ndocrenglon = ? " 
						+ "			AND solicitud.nFolioSolicitudViaticos = ? ";
		PreparedStatement ps = null;
		ResultSet rsSolicitante = null;
		ResultSet rsAutoriza = null;
		NotificacionSolicitudViaticos nsv = null;

		try {

			ps = conn.prepareStatement(querySel);
			ps.setInt(1, 1);
			ps.setInt(2, folioSolicitud);

			rsSolicitante = ps.executeQuery();

			if (rsSolicitante.next()) {
				nsv = new NotificacionSolicitudViaticos();
				nsv.setSolicitanteNumEmpleado(String.valueOf(rsSolicitante.getInt("noempleado")));
				nsv.setSolicitantePuesto(rsSolicitante.getString("puesto"));
				nsv.setSolicitanteNombre(rsSolicitante.getString("nombre"));
				nsv.setIdRenglonSolicitante(rsSolicitante.getInt("nDocRenglon"));
			} else
				throw new Exception("No se encontraron firmantes para el tramite: " + folioSolicitud);

			ps.setInt(1, 2);
			rsAutoriza = ps.executeQuery();

			if (rsAutoriza.next()) {
				nsv.setAutorizadorNumEmpleado(String.valueOf(rsAutoriza.getInt("noempleado")));
				nsv.setAutorizadorPuesto(rsAutoriza.getString("puesto"));
				nsv.setAutorizadorNombre(rsAutoriza.getString("nombre"));
				nsv.setIdRenglonAutorizador(rsAutoriza.getInt("nDocRenglon"));
			} else {
				nsv.setAutorizadorNumEmpleado(nsv.getSolicitanteNumEmpleado());
				nsv.setAutorizadorPuesto(nsv.getSolicitantePuesto());
				nsv.setAutorizadorNombre(nsv.getSolicitanteNombre());
				nsv.setIdRenglonAutorizador(nsv.getIdRenglonSolicitante());
			}

			nsv.setAgenda(leeAgendaSolicitud(conn, folioSolicitud));
			return nsv;

		} finally {

			CloseObject.closeObject(ps);
			CloseObject.closeObject(rsAutoriza);
			CloseObject.closeObject(rsSolicitante);

		}

	}

	private static List<AgendaNotificacion> leeAgendaSolicitud(Connection conn, int folioSolicitud) throws Exception {

		String query = "SELECT clocalidad + ' en ' + municipio + ', ' + estado AS Destino, " 
		             + "       cmotivo, " 
		             + "       CONVERT(VARCHAR(32), finicio, 103)              AS fechaInicio, " 
		             + "       CONVERT(VARCHAR(32), ffin, 103)                 AS fechaFin, "
		             + "       Datediff( day, finicio, ffin ) + 1              AS dias, " 
		             + "       totalimporte " 
		             + "FROM   vsolicitudviaticosagenda WITH(nolock) " 
		             + "WHERE Folio = ?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<AgendaNotificacion> agenda = new ArrayList<NotificacionSolicitudViaticos.AgendaNotificacion>();
		NotificacionSolicitudViaticos nsv = new NotificacionSolicitudViaticos();

		try {

			ps = conn.prepareStatement(query);
			ps.setInt(1, folioSolicitud);

			rs = ps.executeQuery();

			while (rs.next()) {

				AgendaNotificacion agendaRenglon = nsv.new AgendaNotificacion();

				agendaRenglon.setDestino(rs.getString("Destino"));
				agendaRenglon.setMotivo(rs.getString("cmotivo"));
				agendaRenglon.setFechaInicio(Util.stringToDate(rs.getString("fechaInicio"), "dd/MM/yyyy"));
				agendaRenglon.setFechaFin(Util.stringToDate(rs.getString("fechaFin"), "dd/MM/yyyy"));
				agendaRenglon.setDias(rs.getInt("dias"));
				agendaRenglon.setTotal(rs.getBigDecimal("totalimporte"));

				agenda.add(agendaRenglon);

			}

			return agenda;
		} finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(ps);
		}

	}

	public static NotificacionSolicitudViaticos selectNotificacionSolicitudViaticos(Connection conn, int folioSolicitud, int idRenglonAutorizador) throws Exception {
		String querySel = "SELECT	solicitud.nfoliosolicitudviaticos,  " 
						+ "			solicitud.nDocRenglon,  "
						+ "			solicitud.noempleado,  " 
						+ "			solicitud.idplaza,   " 
						+ "			solicitud.puesto,   " 
						+ "			solicitud.nombre,   "
						+ "			solicitud.nautorizado,  " 
						+ "			empleados.d_email " 
						+ "  FROM	tsolicitudviaticosfirmantes solicitud WITH(NOLOCK) " 
						+ "			LEFT OUTER JOIN " 
						+ "			vEmpleadoViaticante empleados WITH(NOLOCK) "
						+ "			ON solicitud.noempleado = empleados.c_empleado " 
						+ " WHERE	solicitud.ndocrenglon = ? " 
						+ "			AND solicitud.nFolioSolicitudViaticos = ? ";

		PreparedStatement ps = null;
		ResultSet rsSolicitud = null;
		ResultSet rsAutoriza = null;
		NotificacionSolicitudViaticos nsv = null;

		try {

			ps = conn.prepareStatement(querySel);
			ps.setInt(1, 1);
			ps.setInt(2, folioSolicitud);

			rsSolicitud = ps.executeQuery();

			if (rsSolicitud.next()) {

				nsv = new NotificacionSolicitudViaticos();
				nsv.setFolioSolicitudViaticos(rsSolicitud.getInt("nfoliosolicitudviaticos"));
				nsv.setSolicitanteNumEmpleado(String.valueOf(rsSolicitud.getInt("noempleado")));
				nsv.setSolicitantePuesto(rsSolicitud.getString("puesto"));
				nsv.setSolicitanteNombre(rsSolicitud.getString("nombre"));
				nsv.setIdRenglonSolicitante(rsSolicitud.getInt("nDocRenglon"));
				nsv.setSolicitanteCorreo( rsSolicitud.getString("d_email") );

			} else
				throw new Exception("No se encontraron firmantes para el tramite: " + folioSolicitud);

			ps.setInt(1, idRenglonAutorizador);
			rsAutoriza = ps.executeQuery();

			if (rsAutoriza.next()) {
				
				nsv.setAutorizadorNumEmpleado(String.valueOf(rsAutoriza.getInt("noempleado")));
				nsv.setAutorizadorPuesto(rsAutoriza.getString("puesto"));
				nsv.setAutorizadorNombre( rsAutoriza.getString("nombre") );
				nsv.setIdRenglonAutorizador( rsAutoriza.getInt("nDocRenglon") );
				nsv.setAutorizadorCorreo( rsAutoriza.getString("d_email") );
				
			}

			nsv.setAgenda(leeAgendaSolicitud(conn, folioSolicitud));
			return nsv;

		} finally {

			CloseObject.closeObject(ps);
			CloseObject.closeObject(rsAutoriza);
			CloseObject.closeObject(rsSolicitud);

		}
	}

	public static int actualizaEstausAutorizacion(Connection conn, NotificacionSolicitudViaticos autorizacionObj) throws Exception {

		int afectados = 0;
		String queryUpdate = "UPDATE	tSolicitudViaticosFirmantes " 
		                   + "   SET	nAutorizado = ?, "
		                   + "          fAutorizacion = GETDATE() "
		                   + ( 
		                	   StringUtils.isBlank( autorizacionObj.getMotivoRechazo() )  ? "": 
		                	   "          ,cMotivoRechazo = ? "
		                	 )
		                   + " WHERE	nFolioSolicitudViaticos = ? " 
		                   + "   AND	nDocRenglon = ? ";
		PreparedStatement ps = null;

		try {
			int parmConuter = 1;
			ps = conn.prepareStatement(queryUpdate);

			ps.setInt(parmConuter++, autorizacionObj.getAutoriza());
			if( !StringUtils.isBlank( autorizacionObj.getMotivoRechazo() ) )
				ps.setString(parmConuter++, autorizacionObj.getMotivoRechazo() );
			
			ps.setInt(parmConuter++, autorizacionObj.getFolioSolicitudViaticos());
			ps.setInt(parmConuter++, autorizacionObj.getIdRenglonAutorizador());

			afectados = ps.executeUpdate();

			return afectados;
		} finally {
			CloseObject.closeObject(ps);
		}

	}

	public static Caso buscaCaso(Connection conn, int folioSolicitud) throws Exception {

		String query = "SELECT id_caso FROM cg_caso caso WITH(NOLOCK) WHERE c_folio LIKE 'VIAT-%-' + ?";
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, String.valueOf(folioSolicitud));

			rs = ps.executeQuery();

			if (rs.next()) {

				int idCaso = rs.getInt(1);

				Caso c = new Caso();
				c.setIdCaso(idCaso);
				c = CasoManager.select(conn, c);

				if (c == null)
					throw new Exception("No se encontro caso para el id de caso: " + idCaso);

				return c;

			} else
				throw new Exception("No se encontro ID de Caso para el folio : " + folioSolicitud);

		} finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(ps);
		}
	}

	public static void finalizaSolicitud(Connection conn, Usuario u, Caso c) throws Exception {

		Map<String, String>  m = Util.readValuesCasoDato(c.getCasoDato());
		CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), m);

		Operacion o = new Operacion();
		o.setIdTC(c.getIdTC());
		o.setNombre("consulta_sol_viaticos");
		o = OperacionManager.select(conn, o);

		CasoOperacion co = CasoOperacionManager.nuevoCasoOperacion(conn, "CONSULTA_SOL_VIATICOS", "", c, o);
		CasoOperacionManager.insert(conn, co);

		int idCasoOperSgte = co.getIdCasoOper();

		BitacoraManager.registraCasoOperacion(conn, u.getLogin(), c, new int[] { idCasoOperSgte }, new String[] { "CONSULTA_SOL_VIATICOS" }, new String[] { "consulta_sol_viaticos" });
		CasoOperacionManager.delete(conn, c.getIdCaso(), c.getCasoOperacion(0).getIdCasoOper());

		if ((c.getStatus() & Caso.MSG_SENDED) == Caso.MSG_SENDED)
			c.setStatus(c.getStatus() ^ Caso.MSG_SENDED);

		c.setStatus(c.getStatus() ^ Caso.EXECUTED);

		CasoManager.update(conn, c);

	}

	public static int creaComision(Connection conn, int folioSolicitud) throws Exception {
		String queryCrea = "{ call dbo.sp_inserta_viaticosComision ? } ";
		String querySelID = "SELECT nIdComision FROM tViaticosComisiones WITH(NOLOCK) WHERE nFolioViaticos = ?";

		CallableStatement cs = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			cs = conn.prepareCall(queryCrea);
			ps = conn.prepareStatement(querySelID);

			cs.setInt(1, folioSolicitud);
			cs.executeUpdate();

			ps.setInt(1, folioSolicitud);
			rs = ps.executeQuery();

			if (rs.next())
				return rs.getInt(1);
			else
				throw new Exception("No se encontro ID de comison para el folio: " + folioSolicitud);

		} finally {
			CloseObject.closeObject(cs);
			CloseObject.closeObject(ps);
			CloseObject.closeObject(rs);
		}

	}

	public static Caso creaSolicitudCaja(Connection conn, int folioSolicitud, int idComision, Usuario u) throws Exception {
		
		Caso c = generaCasoCaja(conn, u, folioSolicitud );
		int nFolioCaja = Integer.parseInt(  c.getFolio().substring( c.getFolio().lastIndexOf('-') + 1) );
		String callStr = "{ call dbo.sp_insertaViaticoCaja @nfolioCaja = ?,  @folio = ?, @idcaso = ? }";
		CallableStatement cs = null;
		try{
			cs = conn.prepareCall( callStr );
			cs.setInt(1, nFolioCaja);
			cs.setInt(2, folioSolicitud);
			cs.setInt(3, c.getIdCaso() );
			
			cs.executeUpdate();

			return c;
		}finally {
			CloseObject.closeObject(cs);
		}
	}
	
	public static Caso generaCasoCaja( Connection conn, Usuario u, int folioSolicitud ) throws Exception{
		
		FolioGeneratorInterface fg = null;
		ClassLoader cl = SolicitudViaticosManager.class.getClassLoader();
		Class<?> clase = cl.loadClass(GestionInterface.FOLIO_GENERATOR);
		fg = (FolioGeneratorInterface) clase.newInstance();

		Caso c = CasoManager.nuevoCaso(conn, u, 42, fg);
		String fecha = Util.getTodayESMX();
		String ejercicioFiscal = EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal();

		c.getCasoDato("FOLIO").setValor(c.getFolio());
		c.getCasoDato("OPERADOR").setValor(u.getLogin());
		c.getCasoDato("FECHA_DOCUMENTO").setValor(fecha);
		c.getCasoDato("EJERCICIO_FISCAL").setValor(ejercicioFiscal);

		Map<String, String> m = new HashMap<String, String>();
		m.put("FOLIO", c.getFolio());
		m.put("OPERADOR", u.getLogin());
		m.put("FECHA_DOCUMENTO", fecha);
		m.put("EJERCICIO_FISCAL", ejercicioFiscal);

		Aplicacion app = AplicacionManager.select(conn, c.getTipoCaso().getGavetaAsociada());
		int id_gabinete = AplicacionManager.createExpediente(conn, u.getLogin(), c, app);
		c.setIdGabinete(id_gabinete);
		CasoManager.update(conn, c);
		TipoCasoInterface tci = null;

		if (c.getTipoCaso().tieneInterface()) {
			tci = instanceTipoCasoInterface(c.getTipoCaso().getInterface());
			tci.onCreateExpediente(conn, u.getLogin(), c, app);
		}

		CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), m);
		return c;
	}

	public static Usuario getUsuarioCaptura(Connection conn, int folioSolicitud) throws Exception {
		String query = "SELECT cLoginCaptura FROM tSolicitudViaticosEncabezado WIHT(NOLOCK) WHERE nFolioSolicitudViaticos = ?";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, folioSolicitud);
			rs = ps.executeQuery();
			String uLogin = "";
			if (rs.next())
				uLogin = rs.getString(1);
			else
				throw new Exception("No se encontro el usuario creador del folio: " + folioSolicitud);

			Usuario u = new Usuario();
			u.setLogin(uLogin);
			u = UsuarioManager.select(conn, u);
			u = UsuarioManager.getRamoUR(conn, u); 
			return u;

		} finally {
			CloseObject.closeObject(ps);
			CloseObject.closeObject(rs);
		}
	}

	private static TipoCasoInterface instanceTipoCasoInterface(String name) throws Exception {

		TipoCasoInterface tci = null;

		ClassLoader cl = SolicitudViaticosManager.class.getClassLoader();
		Class<?> clase = cl.loadClass(name);
		tci = (TipoCasoInterface) clase.newInstance();

		return tci;
		
	}

	public static void avanzaTramiteCaja(Connection conn, Usuario u, Caso c) throws Exception {
		Map<String, String> m = Util.readValuesCasoDato(c.getCasoDato());
		CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), m);

		Operacion o = new Operacion();
		o.setIdTC(c.getIdTC());
		o.setNombre("autoriza_caja");
		o = OperacionManager.select(conn, o);

		CasoOperacion co = CasoOperacionManager.nuevoCasoOperacion(conn, "AUTORIZA_CAJA", "", c, o);
		CasoOperacionManager.insert(conn, co);

		int idCasoOperSgte = co.getIdCasoOper();

		BitacoraManager.registraCasoOperacion(conn, u.getLogin(), c, new int[] { idCasoOperSgte }, new String[] { "AUTORIZA_CAJA" }, new String[] { "autoriza_caja" });
		CasoOperacionManager.delete(conn, c.getIdCaso(), c.getCasoOperacion(0).getIdCasoOper());

		if ((c.getStatus() & Caso.MSG_SENDED) == Caso.MSG_SENDED)
			c.setStatus(c.getStatus() ^ Caso.MSG_SENDED);

		c.setStatus(c.getStatus() ^ Caso.EXECUTED);

		CasoManager.update(conn, c);

		
	}

	public static void autorizaTramiteCaja(Connection conn, Usuario u, Caso c) throws Exception{
		Map<String, String>  m = Util.readValuesCasoDato(c.getCasoDato());
		CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), m);

		Operacion o = new Operacion();
		o.setIdTC(c.getIdTC());
		o.setNombre("consulta_caja");
		o = OperacionManager.select(conn, o);

		CasoOperacion co = CasoOperacionManager.nuevoCasoOperacion(conn, "CONSULTA_CAJA", "", c, o);
		CasoOperacionManager.insert(conn, co);

		int idCasoOperSgte = co.getIdCasoOper();

		BitacoraManager.registraCasoOperacion(conn, u.getLogin(), c, new int[] { idCasoOperSgte }, new String[] { "CONSULTA_CAJA" }, new String[] { "consulta_caja" });
		CasoOperacionManager.delete(conn, c.getIdCaso(), c.getCasoOperacion(0).getIdCasoOper());

		if ((c.getStatus() & Caso.MSG_SENDED) == Caso.MSG_SENDED)
			c.setStatus(c.getStatus() ^ Caso.MSG_SENDED);

		c.setStatus(c.getStatus() ^ Caso.EXECUTED);

		CasoManager.update(conn, c);
	}

	public static void cambiaEstatusSolicitud(Connection conn, int folioSolicitud, int estatus) throws Exception {
		String query = "UPDATE tSolicitudViaticosEncabezado SET nIDEstatus = ? WHERE nFolioSolicitudViaticos=?";
		PreparedStatement ps = null;
		try{
			ps = conn.prepareStatement(query);
			ps.setInt(1, estatus);
			ps.setInt(2, folioSolicitud);
			ps.executeUpdate();
		}finally {
			CloseObject.closeObject(ps);
		}
	}

	public static boolean notifiacionAtendida(Connection conn, Map<String, String> datosToken) throws Exception {
		
		int folio = Integer.parseInt( datosToken.get("folio") );
		int IdRenglonAutorizador  = Integer.parseInt( datosToken.get("IdRenglonAutorizador") );
		String query = 	 "SELECT	nAutorizado "
						+"  FROM	tsolicitudviaticosfirmantes solicitud WITH(NOLOCK) "
						+" WHERE	nFolioSolicitudViaticos = ? " 
						+"   AND nDocRenglon= ?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean notificacionAtendida = false;
		try{
			ps = conn.prepareStatement(query);
			ps.setInt(1, folio );
			ps.setInt(2, IdRenglonAutorizador );
			
			rs = ps.executeQuery();
			
			if( rs.next() ){
				int autorizado = rs.getInt(1);
				notificacionAtendida = ( autorizado != SolicitudViaticosBusinessLogic.FIRMANTE_CAPTURA );
			}
			
			return notificacionAtendida;
		}finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(ps);
		}
		
	}
}
