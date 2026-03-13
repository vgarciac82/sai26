package com.syc.contable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.scottlogic.util.SortedList;

public class AccountingEventProcessor implements AccountingEventProcessorInterface {

	public static final String GROUP_NAME = "PREFERENCIAS_CLIENTE";
	public static final String GP_LONGITUD_BLOQUE = "longitud_bloque";
	public static final String GP_BLOQUES_POR_CUENTA = "no_bloques_cuenta";
	public static final String GP_SEPARADOR_DE_BLOQUE = "-";
	public static final String FIELD_CENTRO_CONTABLE = "cCentroContable";
	public static final String FIELD_EJERCICIO_FISCAL = "aEjercicioFiscal";
	public static final String FIELD_DOC_RENGLON = "nDocRenglon";

	private static final Logger log = Logger.getLogger(AccountingEventProcessor.class);

	private Map<String, List<AccountingEvent>> eventos;
	private Map<String, Account> cuentas;
	private int bloquesPorCta;
	private int longitudDeBloque;
	private String separadorBloque;

	public AccountingEventProcessor() {
		this.eventos = new Hashtable<String, List<AccountingEvent>>();
		this.cuentas = new Hashtable<String, Account>();
	}

	public List<AccountingMovement> getMovements(Connection conn, String event, Map<String, String> values) throws AccountingEventProcessorException {

		List<AccountingMovement> movements = new SortedList<AccountingMovement>(new AccountingMovementComparator());

		assignStructureAccount(conn);

		try {
			List<AccountingEvent> ec = getEvents(conn, event, values.get(FIELD_EJERCICIO_FISCAL));

			movements = generateMovements(conn, ec, values);
		} catch (Exception exc) {
			if (exc instanceof AccountingEventProcessorException)
				throw (AccountingEventProcessorException) exc;
			else
				throw new AccountingEventProcessorException(exc);
		}

		return movements;
	}

	// METODOS PRIVADOS

	private void assignStructureAccount(Connection conn) {
		separadorBloque = GP_SEPARADOR_DE_BLOQUE;
		bloquesPorCta = Integer.valueOf(getStructureAccount(conn, GROUP_NAME, GP_BLOQUES_POR_CUENTA));
		longitudDeBloque = Integer.valueOf(getStructureAccount(conn, GROUP_NAME, GP_LONGITUD_BLOQUE));
	}

	private String getStructureAccount(Connection conn, String groupName, String propName) {

		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			pstmnt = conn.prepareStatement("SELECT gp_valor FROM cg_grupo_propiedades WITH (NOLOCK) WHERE g_nombre = ? AND gp_nombre = ?");

			pstmnt.setString(1, groupName);
			pstmnt.setString(2, propName);

			rs = pstmnt.executeQuery();
			if (rs.next())
				return rs.getString(1);
		} catch (Exception exc) {
			throw new RuntimeException(exc);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception exc) {
				log.warn("Cerrando ResultSet", exc);
			}

			try {
				if (pstmnt != null)
					pstmnt.close();
			} catch (Exception exc) {
				log.warn("Cerrando PreparedStatement", exc);
			}
		}

		return null;
	}

	private List<AccountingEvent> getEvents(Connection conn, String evento, String ejercicioFiscal) throws AccountingEventProcessorException {

		if ((evento == null) || (evento.trim().isEmpty()))
			throw new AccountingEventProcessorException("evento no debe ser nulo o vacio");

		if ((ejercicioFiscal == null) || (ejercicioFiscal.trim().isEmpty()))
			throw new AccountingEventProcessorException("ejercicioFiscal no debe ser nulo o vacio");

		List<AccountingEvent> ec = eventos.get(evento + ejercicioFiscal);

		if (ec == null) {
			ec = getEventsFromDatabase(conn, evento, ejercicioFiscal);
			eventos.put(evento + ejercicioFiscal, ec);
		}

		return ec;
	}

	private List<AccountingEvent> getEventsFromDatabase(Connection conn, String event, String ejercicioFiscal) throws AccountingEventProcessorException {

		List<AccountingEvent> ecList = new ArrayList<AccountingEvent>();
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			pstmnt = conn.prepareStatement("SELECT m.cEvento, m.nCuenta, m.TipoCuenta, m.nDocRenglon, d.nOrden, m.dComponente, dDetalleCuenta FROM tEventoConfiguracion AS m "
			        + "WITH (NOLOCK) LEFT OUTER JOIN tEventoConfiguraDetalle AS d WITH (NOLOCK) ON (d.cEvento = m.cEvento AND d.aEjercicioFiscal = m.aEjercicioFiscal "
			        + "AND d.nCuenta = m.nCuenta AND d.nDocRenglon = m.nDocRenglon) WHERE m.cEvento = ? AND m.aEjercicioFiscal = ? ORDER BY m.cEvento, m.nCuenta, "
			        + "nDocRenglon, d.nOrden");

			pstmnt.setString(1, event);
			pstmnt.setString(2, ejercicioFiscal);

			rs = pstmnt.executeQuery();

			AccountingEvent ec = null;
			String prevAccount = new String();

			while (rs.next()) {
				if (!prevAccount.equals(rs.getString("nDocRenglon") + rs.getString("nCuenta"))) {
					ec = new AccountingEvent(rs.getString("cEvento"), rs.getString("nCuenta"), rs.getString("TipoCuenta"), rs.getString("dComponente"));
					ecList.add(ec);
				}

				if (rs.getString("dDetalleCuenta") != null)
					ec.getDetalle().add(rs.getString("dDetalleCuenta"));

				prevAccount = rs.getString("nDocRenglon") + ec.getCuenta();
			}
		} catch (Exception exc) {
			if (exc instanceof AccountingEventProcessorException)
				throw (AccountingEventProcessorException) exc;
			else
				throw new AccountingEventProcessorException(exc);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception exc) {
				log.warn("Cerrando ResultSet", exc);
			}

			try {
				if (pstmnt != null)
					pstmnt.close();
			} catch (Exception exc) {
				log.warn("Cerrando PreparedStatement", exc);
			}
		}

		return ecList;
	}

	private List<AccountingMovement> generateMovements(Connection conn, List<AccountingEvent> ecList, Map<String, String> values) throws AccountingEventProcessorException {

		Account cta;
		List<AccountingMovement> movtos = new SortedList<AccountingMovement>(new AccountingMovementComparator());

		for (AccountingEvent ec : ecList) {
			try {
				if( Double.parseDouble(values.get(ec.getComponente())) != 0){
					cta = buildAccount(conn, ec, values);

					if (cta.isSubCuentaRequerida() && ((values.get(cta.getSubCuenta()) == null) || (values.get(cta.getSubCuenta()).trim().length() == 0)))
						throw new AccountingEventProcessorException(String.format("La cuenta %s requiere la clave %s", cta.getCuenta(), cta.getSubCuenta()));

					movtos.add(new AccountingMovement(cta.getCuenta(),
					        cta.isSubCuentaRequerida() ? values.get(cta.getSubCuenta()).trim() : null,
					        Double.parseDouble(values.get(ec.getComponente())),
					        ec.esCargo(),
					        cta.esAcreedora(),
					        cta.verificaSaldo(),
					        cta.getCentroContable(),
					        cta.getEjercicioFiscal(),
					        Integer.parseInt(values.get(FIELD_DOC_RENGLON)),
					        cta.getDescCuenta(), cta.isPresupuestal(),
					        values.get( "cUnidadResponsable" )
					        )
							
							);					
				}
			} catch (NumberFormatException exc) {
				String msg = String.format("En componente %s = %s", ec.getComponente(), values.get(ec.getComponente()));
				log.error(msg, exc);
				throw new AccountingEventProcessorException(msg, exc);
			} catch (Exception exc) {
				if (exc instanceof AccountingEventProcessorException)
					throw (AccountingEventProcessorException) exc;
				else
					throw new AccountingEventProcessorException(exc);
			}
		}

		return movtos;
	}

	private Account buildAccount(Connection conn, AccountingEvent ec, Map<String, String> values) throws AccountingEventProcessorException {

		Account cta = null;
		StringBuffer cuenta = new StringBuffer();
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			String[] bloques = ec.getCuenta().split(separadorBloque);
			if (bloques.length != bloquesPorCta)
				throw new AccountingEventProcessorException("El total de bloques de la estructura de cuenta debe ser de " + bloquesPorCta + " y tiene " + bloques.length);

			if (!ec.getDetalle().isEmpty() && (bloques.length != ec.getDetalle().size()))
				throw new AccountingEventProcessorException("El detalle del evento (" + ec.getEvento() + ") no coincide con los bloques de la cuenta '" + ec.getCuenta() + "' ("
				        + bloquesPorCta + " vs " + ec.getDetalle().size() + ")");

			// Arma la cuenta a partir del evento
			String value, token = "";
			if (ec.getDetalle().isEmpty()) {
				cuenta.append(ec.getCuenta());
			} else {
				int i = 0;
				for (String block : ec.getDetalle()) {
					value = values.get(block);
					if (value != null)
						cuenta.append(token).append(leftPad(bloques[i++], value));
					else
						cuenta.append(token).append(leftPad(bloques[i++], block));
					token = separadorBloque;
				}
			}

			// Valida que la cuenta exista
			if (!cuentas.containsKey(cuenta.toString())) {
				pstmnt = conn.prepareStatement("SELECT nCuenta, cSubcuenta, VerificaSaldo, NaturalezaCuenta, AplicacionCuenta, dCuenta, TipoBalance, TipoCuenta  FROM tCuentas WITH (NOLOCK) WHERE nCuenta = ?");

				pstmnt.setString(1, cuenta.toString());

				rs = pstmnt.executeQuery();
				if (!rs.next())
					throw new AccountingEventProcessorException("No se localiz\u00F3 la cuenta '" + cuenta + "'");

				if ("N".equalsIgnoreCase(rs.getString("AplicacionCuenta")))
					throw new AccountingEventProcessorException("La cuenta '" + cuenta + "' no es de aplicaci\u00F3n");

				if ((rs.getString("cSubcuenta") != null) && (values.get(rs.getString("cSubcuenta")) == null))
					throw new AccountingEventProcessorException("No se recibi\u00F3 el componente '" + rs.getString("cSubcuenta") + "' de subcuenta para cuenta '" + cuenta + "'");

				String centroContable;
				if ((centroContable = values.get(FIELD_CENTRO_CONTABLE)) == null)
					throw new AccountingEventProcessorException("No se recibi\u00F3 el centro contable para cuenta '" + cuenta + "'");

				String ejercicoFiscal;
				if ((ejercicoFiscal = values.get(FIELD_EJERCICIO_FISCAL)) == null)
					throw new AccountingEventProcessorException("No se recibi\u00F3 el ejercicio fiscal");

				cta = new Account(rs.getString("nCuenta"),
				        rs.getString("cSubcuenta"),
				        "S".equalsIgnoreCase(rs.getString("VerificaSaldo")),
				        "D".equalsIgnoreCase(rs.getString("NaturalezaCuenta")),
				        centroContable,
				        ejercicoFiscal,
				        rs.getString("dCuenta"),
				        rs.getString("TipoBalance"),
				        rs.getString("TipoCuenta") );

				cuentas.put(rs.getString("nCuenta"), cta);
			} else {
				cta = cuentas.get(cuenta.toString());
				if (cta == null)
					throw new AccountingEventProcessorException("No se localiz\u00F3 la cuenta '" + cuenta + "'");

				String centroContable;
				if ((centroContable = values.get(FIELD_CENTRO_CONTABLE)) == null)
					throw new AccountingEventProcessorException("No se recibi\u00F3 el centro contable para cuenta '" + cuenta + "'");

				String ejercicoFiscal;
				if ((ejercicoFiscal = values.get(FIELD_EJERCICIO_FISCAL)) == null)
					throw new AccountingEventProcessorException("No se recibi\u00F3 el ejercicio fiscal");

				if (!centroContable.equals(cta.getCentroContable()))
					cta.setCentroContable(centroContable);

				if (!ejercicoFiscal.equals(cta.getEjercicioFiscal()))
					cta.setEjercicioFiscal(ejercicoFiscal);
			}
		} catch (Exception exc) {
			if (exc instanceof AccountingEventProcessorException)
				throw (AccountingEventProcessorException) exc;
			else
				throw new AccountingEventProcessorException(exc);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception exc) {
				log.warn("Cerrando ResultSet", exc);
			}

			try {
				if (pstmnt != null)
					pstmnt.close();
			} catch (Exception exc) {
				log.warn("Cerrando PreparedStatement", exc);
			}
		}

		return cta;
	}

	private String leftPad(String padding, String value) throws AccountingEventProcessorException {

		if (padding == null)
			throw new AccountingEventProcessorException("padding no debe ser nulo");

		if (value == null)
			throw new AccountingEventProcessorException("value no debe ser nulo");

		return padding.substring(0, longitudDeBloque - value.length()) + value;
	}

	public static class Account {

		private String cuenta;
		private String subCuenta;
		private String centroContable;
		private boolean verificaSaldo;
		private boolean naturaleza;
		private String ejercicioFiscal;
		private String descCuenta;
		private String TipoCuenta;
		private String TipoBalance;

		public Account(String cuenta, String subCuenta, boolean verificaSaldo, boolean naturaleza, String centroContable, String ejercicioFiscal, String descCuenta, String TipoCuenta, String TipoBalance) {
			this.cuenta = cuenta;
			this.subCuenta = subCuenta;
			this.verificaSaldo = verificaSaldo;
			this.naturaleza = naturaleza;
			this.centroContable = centroContable;
			this.ejercicioFiscal = ejercicioFiscal;
			this.descCuenta = descCuenta;
			this.TipoCuenta = TipoCuenta;
			this.TipoBalance = TipoBalance;
		}

		public String getCuenta() {
			return cuenta;
		}

		public String getSubCuenta() {
			return subCuenta;
		}

		public boolean isSubCuentaRequerida() {
			return (subCuenta != null);
		}

		public boolean verificaSaldo() {
			return verificaSaldo;
		}

		public boolean esAcreedora() {
			return naturaleza;
		}

		public String getCentroContable() {
			return centroContable;
		}

		public void setCentroContable(String centroContable) {
			this.centroContable = centroContable;
		}

		public String getEjercicioFiscal() {
			return ejercicioFiscal;
		}

		public void setEjercicioFiscal(String ejercicioFiscal) {
			this.ejercicioFiscal = ejercicioFiscal;
		}

		public String getDescCuenta() {
			return descCuenta;
		}

		public String getTipoCuenta() {
			return TipoCuenta;
		}

		public String getTipoBalance() {
			return TipoBalance;
		}

		public boolean isPresupuestal() {
			return ("P".equals(TipoCuenta) && "P".equals(TipoBalance));
		}

		
		
	}
}