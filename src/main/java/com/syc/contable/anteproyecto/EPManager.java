package com.syc.contable.anteproyecto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.syc.contable.core.AdecuacionManager;
import com.syc.gestion.core.GrupoPropiedades;
import com.syc.gestion.core.GrupoPropiedadesManager;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;

/**
 * Clase administradora de la subcuenta tipo EP (Estructura Programatica)
 * 
 * @author Vicente Garcia Carrillo
 * @version 1.0
 * 
 */
public class EPManager {

	private static final Logger		log		= Logger.getLogger(EPManager.class);
	public static final String[]	meses	= { "Anual", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre ", "Noviembre", "Diciembre" };

	/**
	 * Desagrega la EP basado en la configuracion de la subcuenta para el
	 * ejercicio fiscal activo.
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos.
	 * @param EP
	 *            Estructura Programatica.
	 * @param incluyeEP
	 *            Indica si en el mapa de desagregacion se incluye la ep.
	 * @return Mapa con relacion uno a uno. La llave del mapa es el nombre del
	 *         elemento de la ep y el valor sera el elemento en la ep. El mapa
	 *         se entrega en el orden definido en la configuracion.
	 * @throws Exception
	 *             Si ocurre un error mientras se desagrega una estructura
	 *             programatica
	 */
	public static Map<String, String> desagregaEP(Connection conn, String EP, boolean incluyeEP) throws Exception {
		log.trace("Desagregando EP: " + EP);
		if ("".equals(EP) || null == EP)
			throw new Exception("Se ha recibido una EP vacia");

		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String, String> epDesagregada = null;

		String query = "SELECT	nOrden, nombreCampo, interno, aEjercicioFiscal " + "  FROM	tTipoSubcuentaConf " + " WHERE	cSubcuenta = 'EP' " + "   AND	aEjercicioFiscal = ? order by norden";

		String ejercicioFiscal = AdecuacionManager.obtenEjercicioFiscal(conn);
		String separadorEP = getSeparadorEP(conn);
		int totalElementosEP = getNumeroComponentesEP(conn);

		try {
			if (totalElementosEP <= 0)
				throw new Exception("No se encontro configuracion para el tipo de cuenta [EP] en el ejercicio " + ejercicioFiscal);

			if ("".equals(separadorEP) || null == separadorEP)
				throw new Exception("No se definio el separador de la EP en la tabla de configuracion del sistema");

			if (".".equals(separadorEP))
				separadorEP = "[" + separadorEP + "]";
			String[] elementosEP = EP.split(separadorEP);

			if (elementosEP.length != totalElementosEP)
				throw new Exception("El total de elementos en la EP [" + elementosEP + "] es diferente a los campos establecidos en la configuracion de la subcuenta [" + totalElementosEP + "]");

			ps = conn.prepareStatement(query);
			ps.setString(1, ejercicioFiscal);

			rs = ps.executeQuery();
			epDesagregada = new LinkedHashMap<String, String>();

			int cnt = 0;
			while (rs.next()) {
				epDesagregada.put(rs.getString("nombreCampo"), elementosEP[cnt]);
				cnt++;
			}
			if (incluyeEP)
				epDesagregada.put("EP", EP);

			log.trace("EP Desagregada exitosamente!!");
			return epDesagregada;
		} finally {

			CloseObject.closeObject(rs, false);
			CloseObject.closeObject(ps, false);

		}

	}

	/**
	 * Desagrega la EP basado en la configuracion de la subcuenta para el
	 * ejercicio fiscal activo.
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos.
	 * @param EP
	 *            Estructura Programatica.
	 * @return Mapa con relacion uno a uno. La llave del mapa es el nombre del
	 *         elemento de la ep y el valor sera el elemento en la ep. El mapa
	 *         se entrega en el orden definido en la configuracion.
	 * @throws Exception
	 *             Si ocurre un error mientras se desagrega una estructura
	 *             programatica
	 */
	public static Map<String, String> desagregaEP(Connection conn, String EP) throws Exception {
		return desagregaEP(conn, EP, false);

	}

	/**
	 * Devuelve el total de componentes esperados en la EP
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos
	 * @return Total de componentes de la EP
	 * @throws Exception
	 */
	public static int getNumeroComponentesEP(Connection conn) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT COUNT(*) as totalElementosEP FROM tTipoSubcuentaConf  WHERE	cSubcuenta = 'EP' AND aEjercicioFiscal = ?";
		String ejercicioFiscal = AdecuacionManager.obtenEjercicioFiscal(conn);

		int totalElementosEP = -1;
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, ejercicioFiscal);

			rs = ps.executeQuery();
			if (rs.next())
				totalElementosEP = rs.getInt("totalElementosEP");

			return totalElementosEP;
		} finally {
			CloseObject.closeObject(rs, false);
			CloseObject.closeObject(ps, false);
		}

	}

	/**
	 * Devuelve el caracter separador de la EP por default .
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos.
	 * @return Caracter separador de la EP
	 * @throws Exception
	 */
	public static String getSeparadorEP(Connection conn) throws Exception {
		GrupoPropiedades propiedadSeparadorEP = new GrupoPropiedades();
		propiedadSeparadorEP.setGrupo("PREFERENCIAS_CLIENTE");
		propiedadSeparadorEP.setNombre("sep_ep");
		propiedadSeparadorEP = GrupoPropiedadesManager.select(conn, propiedadSeparadorEP);

		if (propiedadSeparadorEP != null && propiedadSeparadorEP.getValor() != null)
			return propiedadSeparadorEP.getValor();
		else
			return "";
	}

	/**
	 * Valida la EP de calendario.<br>
	 * Validar 1 La EP existe en el calendario pre cargado.<br>
	 * Validar 2 Si la carga no viene de un usuario administrador, entonces
	 * debera ser exclusivamente de su unidad ejecutora.<br>
	 * Validar 2.1 Si la carga no viene de un usuario administrador, entonces no
	 * debera contener partidas restringidas.<br>
	 * Validar 3 Validacion de montos.<br>
	 * Validar 3.1 El primer monto debe se considera el anual, mayor a cero.<br>
	 * Validar 3.2 Todos los montos deberan ser enteros.<br>
	 * Validar 3.3 A excepcion del primer monto los demas deberan ser mayores o
	 * iguales a cero.<br>
	 * Validar 3.4 La suma de los montos mensuales debe ser exactamente igual al
	 * monto anual.<br>
	 * Todas las validaciones corren para obtener el listado de incumplimientos
	 * de la EP.
	 * 
	 * @param esAdmin
	 *            indica si es administrador.
	 * @param UE
	 *            Unidad Ejecutora del usuario
	 * @param capitulosRestringidos
	 *            Capitulos restringidos
	 * @param epMap
	 *            EP desagregada
	 * @param row
	 *            Columna del archivo excel
	 * @return
	 */
	public static List<String> validaEPCalendario(Connection conn, int ejercicioFiscal, boolean esAdmin, String UE, int[] capitulosRestringidos, Map<String, String> epMap, Row row) {
		String ep = epMap.get("EP");
		List<String> mensajes = new ArrayList<String>();

		try {
			if (!esEPCapturadaProyecto(conn, ep, ejercicioFiscal))
				mensajes.add("En la fila: " + row.getRowNum() + " La EP [" + ep + "] no fue capturada en la carga de proyecto.");
			if (!perteneceEPAUsuarioUE(UE, esAdmin, epMap))
				mensajes.add("En la fila: " + row.getRowNum() + " La EP [" + ep + "] no pertenece a su unidad ejecutora [" + UE + "]");
			if (!epSinCapitulosRestringidos(esAdmin, epMap, capitulosRestringidos))
				mensajes.add("En la fila: " + row.getRowNum() + " La EP [" + ep + "] contiene capitulos exclusivos para la carga desde la unidad central");
			List<String> mensajesValidacionDeSaldos = validaMontosCalendario(conn, epMap, row);
			if (mensajesValidacionDeSaldos.size() > 0)
				mensajes.addAll(mensajesValidacionDeSaldos);

		} catch (Exception e) {
			mensajes.add("Error validando renglon " + row.getRowNum() + " del archivo. Causa: " + e);
		}
		return mensajes;
	}

	/**
	 * Valida si la EP existe en la carga de proyecto.
	 * 
	 * @param EP
	 *            Estructura Programatica a validar.
	 * @return true si y solo si existe en la tabla de proyecto cargado.
	 */
	public static boolean esEPCapturadaProyecto(Connection conn, String EP, int ejercicioFiscal) throws Exception {
		String query = "SELECT	1 as Existe " + "  FROM	tProyecto_PF " + " WHERE	EP = ? " + "   AND	aEjercicioFiscal = ?";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, EP);
			ps.setInt(2, ejercicioFiscal);

			rs = ps.executeQuery();
			return rs.next();

		} finally {
			CloseObject.closeObject(rs, false);
			CloseObject.closeObject(ps, false);
		}

	}

	/**
	 * Valida que la EP pertenesca al usuario basado en su unidad
	 * normativa/ejecutora. Si es un usuario administrador, todas las EPs
	 * pertenecen a el. Si es un usuario de Unidad Normativa valida que la EP
	 * corresponda a su unidad normativa. Si es un usuario de unidad ejecutora,
	 * valida que la EP corresponda a su unidad ejecutora.
	 * 
	 * @param UE
	 *            Unidad Ejecutora/Normativa del usuario.
	 * @param esAdmin
	 *            Indica si es un usuario administrador
	 * @param ep
	 *            Estructura Programatica desagregada.
	 * @return true si y solo si el usuario tiene derecho a la estructura
	 *         programatica.
	 */
	public static boolean perteneceEPAUsuarioUN(String UE, boolean esAdmin, Map<String, String> ep) {
		if (esAdmin)
			return true;
		else {
			int numeroUE = Integer.parseInt(UE.substring(1));
			if (numeroUE >= 0 && numeroUE <= 15)
				return UE.equalsIgnoreCase(ep.get("UnidadNormativa"));
			else
				return UE.equalsIgnoreCase(ep.get("cUnidadEjecutora"));
		}
	}

	/**
	 * Valida que la EP pertenezca al usuario basado en su unidad ejecutora. Si
	 * es un usuario administrador, todas las EPs pertenecen a el. En otro caso
	 * valida que la EP pertenezca a su unidad ejecutora. Si es un usuario de
	 * Unidad Normativa, la asume como unidad ejecutora.
	 * 
	 * @param UE
	 *            Unidad Ejecutora del usuario.
	 * @param esAdmin
	 *            Inidca si es un usuario administrador.
	 * @param ep
	 *            Estructura Programatica desagregada
	 * @return true si y solo si el usuario tienen derecho a la estructura
	 *         programatica.
	 */
	public static boolean perteneceEPAUsuarioUE(String UE, boolean esAdmin, Map<String, String> ep) {
		if (esAdmin)
			return true;
		else
			return UE.equalsIgnoreCase(ep.get("cUnidadEjecutora"));
	}

	/**
	 * Valida que la EP no contiene capitulos restringidos. Si es un usuario
	 * administrador no toma en cuenta las partidas restringidas.
	 * 
	 * @param esAdmin
	 *            Indica si es un usuario administrador
	 * @param ep
	 *            Estructura Programatica desagregada
	 * @param capitulosRestringidos
	 *            Arreglo con los capitulos que no pueden ser calendarizados por
	 *            Unidades Ejecutoras
	 * @return true si la EP no contiene partidas restringidas
	 */
	public static boolean epSinCapitulosRestringidos(boolean esAdmin, Map<String, String> ep, int[] capitulosRestringidos) {
		if (esAdmin || capitulosRestringidos == null)
			return true;
		else {
			boolean contieneRestingida = true;

			String capitulo = ep.get("cPartida");
			for (int i = 0; i < capitulosRestringidos.length; i++)
				contieneRestingida = contieneRestingida && !capitulo.startsWith(String.valueOf(capitulosRestringidos[i]));

			return contieneRestingida;
		}
	}

	/**
	 * Validacion de montos en calendario. Los montos deberan ser enteros,
	 * mayores o iguales a cero y la suma de los montos de Enero a Diciembre
	 * debe ser exactamente igual al monto registrado en el PEF
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos.
	 * @param ep
	 *            Mapa con
	 * @param row
	 * @return
	 * @throws Exception
	 */
	public static List<String> validaMontosCalendario(Connection conn, Map<String, String> ep, Row row) throws Exception {
		String query = "SELECT Monto as MontoAnual FROM	tProyecto_PF  WHERE	EP = ?";
		List<String> errores = new ArrayList<String>();
		int cnt = -1;
		double suma = 0d;
		ResultSet rs = null;
		PreparedStatement ps = null;

		for (Iterator<Cell> i = row.cellIterator(); i.hasNext();) {
			try {
				cnt++;
				Cell cell = i.next();
				/* Ignora la primer columna que es la EP */
				if (cnt == 0)
					continue;

				double val = cell.getNumericCellValue();
				if (val < 0d)
					errores.add("En la fila: " + row.getRowNum() + " para el monto " + meses[cnt - 1] + " es negativo.");
				if (!Util.esEntero(val)) {
					errores.add("En la fila: " + row.getRowNum() + " para el monto " + meses[cnt - 1] + " NO es entero (Contiene decimales).");
				}
				if (cnt > 1)
					suma += val;
			} catch (Exception e) {
				errores.add("Error procesando fila: " + row.getRowNum() + " celda: " + cnt + " Causa: " + e);
			}
		}

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, ep.get("EP"));
			rs = ps.executeQuery();

			if (rs.next()) {
				double montoAnual = rs.getDouble(1);
				if (!(suma == montoAnual))
					errores.add("En la fila: " + row.getRowNum() + "  El monto anual [" + montoAnual + "] es diferente a la suma de los meses [" + suma + "] ");
			} else {
				errores.add("En la fila: " + row.getRowNum() + " No se encontro registro en el PEF para la EP[" + ep.get("EP") + "]");
			}
		} catch (Exception e) {
			errores.add("Error procesando el monto anual " + e);
		} finally {
			CloseObject.closeObject(rs, false);
			CloseObject.closeObject(ps, false);
		}

		return errores;
	}

	/**
	 * Agrega el calendario a una EP para su insercion.
	 * 
	 * @param epMap
	 *            EP Desagregada
	 * @param row
	 *            Informacion del calendario
	 * @return Mapa con el contenido completo.
	 */
	public static Map<String, String> agregaCalendario(Map<String, String> epMap, Row row) throws Exception {

		int cnt = -1;
		int j = 0;

		/* El monto anual esta al final en la tabla, sin embargo */
		for (Iterator<Cell> i = row.cellIterator(); i.hasNext();) {
			Cell cell = i.next();
			cnt++;
			if (cnt < 1)
				continue;

			double val = cell.getNumericCellValue();
			NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());

			String moneyVal = formatter.format(val);
			String nombreCampo = "mMonto_" + meses[j];
			epMap.put(nombreCampo, moneyVal);
			j++;
		}

		return epMap;
	}

	public static String getComponente(String ep, String componente) {
		if ("CAPITULO".equals(componente))
			return ep.substring(31, 36);
		else if ("FUENTE_FINANCIAMIENTO".equals(componente))
			return ep.substring(39, 40); 
		else
			throw new RuntimeException("No se encontro el componente: " + componente);
	}

}
