package com.syc.contable.adecuaciones;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import com.syc.contable.adecuaciones.Exception.IncompletRowException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;

/**
 * Clase encargada de la validacion de las adecuaciones presupuestales para
 * conagua.
 * 
 * @author Vicente Garcia Carrillo
 * 
 */
public class ValidacionAdecuacionesManager {

	/**
	 * Encabezado del detalle de las adecuaciones
	 */
	public static final String[]	ENCABEZADO_DETALLE_ADECUACION_ARCHIVO	= { "SECUENCIA", "CLAVE SIAFF O MAP", "CLAVE INTERNA", "CODIGO SAF", "TIPO", "MONTO ANUAL", "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE" };
	/**
	 * Log de la clase.
	 */
	private static final Logger		log										= Logger.getLogger(ValidacionAdecuacionesManager.class);

	/**
	 * Inserta un archivo Excel de adecuaciones para su validacion.
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos.
	 * @param hoja
	 *            Hoja abierta de excel de adecuaciones
	 * @param nFolio
	 *            Numero de folio del tramite.
	 * @param nRenglonCuerpo
	 *            Numero de renglon donde inicia el cuerpo
	 * @return Mensajes. Si es vacia se insertaron completamente sin error los
	 *         renglones. Si no es vacia contiene los errores ocurridos en la
	 *         validacion del archivo.
	 * @throws Exception
	 *             Si ocurre un error no controlado
	 */
	public static String insertaArchivoValidacionCuerpo(Connection conn, Workbook wb, HSSFSheet hoja, int nFolio, int nRenglonCuerpo) throws Exception {
		log.info(" Iniciando carga de archivo excel de adecuaciones para su validacion Folio[" + nFolio + "]");

		String query = "INSERT INTO tValida_Adecuacion(Folio, Secuencia, EP, MAP, funcion, programa_general, programa, partida, movimiento, anual, enero, febrero, marzo, abril, mayo, junio, julio, agosto, septiembre, octubre, noviembre, diciembre) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String mensajes = "";

		int totalInsertados = 0;
		int contadorRenglones = -1;

		PreparedStatement ps = null;

		try {

			ps = conn.prepareStatement(query);
			int secuencia = 0;
			for (Iterator<Row> i = hoja.iterator(); i.hasNext();) {
				try {
					contadorRenglones++;
					Row renglon = i.next();
					log.trace("Procesando renglon " + contadorRenglones + " del archivo excel");

					if (contadorRenglones >= nRenglonCuerpo) {
						if (Util.renglonVacio(renglon))
							continue;
						/* Insercion de informacion requerida para la validacion */
						secuencia++;
						ps.setString(1, "A" + nFolio);
						ps.setInt(2, secuencia);

						String cveSIAFF = validaColumnaCadenaNoVacia(renglon, 1);
						String funcion = cveSIAFF.substring(12, 15);
						String programaGeneral = cveSIAFF.substring(26, 27);
						String programa = cveSIAFF.substring(26, 30);
						String partida = cveSIAFF.substring(31, 40);

						ps.setString(3, cveSIAFF);
						ps.setString(4, validaColumnaCadenaNoVacia(renglon, 2));

						ps.setString(5, funcion);
						ps.setString(6, programaGeneral);
						ps.setString(7, programa);
						ps.setString(8, partida);
						ps.setString(9, validaColumnaCadenaNoVacia(renglon, 4));

						/* Insercion de montos */
						for (int n = 0; n < 13; n++) {
							ps.setDouble(10 + n, validaColumnaNumericaNoVacia(wb, renglon, 5 + n));
						}

						totalInsertados += ps.executeUpdate();
					}
				} catch (IncompletRowException e) {
					mensajes += "\n" + e.toString();
				} catch (Exception e) {
					mensajes += "\nERROR PROCESANDO RENGLON " + contadorRenglones + ": " + e.toString();
				}
			}
			log.info("Finalizando carga de archivo excel de adecuaciones para su validacion Folio[" + nFolio + "] Se insertaron " + totalInsertados);
			return mensajes;
		} finally {
			CloseObject.closeObject(ps, false);
		}

	}

	/**
	 * Devuelve el valor de una celda en entero.
	 * 
	 * @param renglon
	 *            Renglon no vacio
	 * @param indiceCelda
	 *            Numero de la celda a validar
	 * @return Contenido de la celda. Numero entero.
	 * @throws IncompletRowException
	 *             Si la celda esta vacia o no es un numero entero.
	 */
	public static int validaColumnaEnteroNoVacia(Row renglon, int indiceCelda) throws IncompletRowException {
		/* Asume que previamente se valido que el renglon no sea nulo. */
		Cell celda = renglon.getCell(indiceCelda);
		int val = 0;

		/* Si la celda esta vacia y no se permiten vacios lo reporta como error */
		if ((celda == null || celda.getCellType() == CellType.BLANK))
			throw new IncompletRowException("[Renglon: " + renglon.getRowNum() + "] La celda " + ValidacionAdecuacionesManager.ENCABEZADO_DETALLE_ADECUACION_ARCHIVO[indiceCelda] + " esta vacia.");
		/*
		 * Si viene como cadena de caracteres intenta convertirla a entero. Si
		 * es decimal arrojara la excepcion
		 */
		else if (celda.getCellType() == CellType.STRING)
			try {
				val = Integer.parseInt(celda.getStringCellValue());
			} catch (NumberFormatException e) {
				throw new IncompletRowException("[Renglon: " + renglon.getRowNum() + "] La celda " + ValidacionAdecuacionesManager.ENCABEZADO_DETALLE_ADECUACION_ARCHIVO[indiceCelda] + " esta en formato TEXTO y no se puede convertir a numero");
			}
		/*
		 * Si es una celda numerica, intenta convertirla a entero. Si falla lo
		 * reporta
		 */
		else if (celda.getCellType() == CellType.NUMERIC)
			try {
				val = Integer.parseInt(String.valueOf(celda.getNumericCellValue()));
			} catch (NumberFormatException e) {
				if (Util.esEntero(celda.getNumericCellValue()))
					try {
						val = Math.round((float) celda.getNumericCellValue());
					} catch (Exception e2) {
						throw new IncompletRowException("[Renglon: " + renglon.getRowNum() + "] La celda " + ValidacionAdecuacionesManager.ENCABEZADO_DETALLE_ADECUACION_ARCHIVO[indiceCelda] + " esta en formato NUMERICO pero no es un ENTERO (Sin decimales)");
					}
				else
					throw new IncompletRowException("[Renglon: " + renglon.getRowNum() + "] La celda " + ValidacionAdecuacionesManager.ENCABEZADO_DETALLE_ADECUACION_ARCHIVO[indiceCelda] + " esta en formato NUMERICO pero no es un ENTERO (Sin decimales)");
			}
		/*
		 * Si es otro tipo de dato (Formula o Boleano o Texto Rico no se puede
		 * procesar
		 */
		else
			throw new IncompletRowException("[Renglon: " + renglon.getRowNum() + "] La celda " + ValidacionAdecuacionesManager.ENCABEZADO_DETALLE_ADECUACION_ARCHIVO[indiceCelda] + " esta en formato no permitido )");
		/*
		 * Si llega a esta linea es porque no hubo error y se pudo convertir a
		 * entero.
		 */
		return val;
	}

	/**
	 * Devuelve el valor de una celda como String.
	 * 
	 * @param renglon
	 *            Renglon no vacio
	 * @param indiceCelda
	 *            Numero de la celda a validar
	 * @return Contenido de la celda.
	 * @throws IncompletRowException
	 *             Si la celda esta vacia o es de un tipo de datos incompatible.
	 */
	public static String validaColumnaCadenaNoVacia(Row renglon, int indiceCelda) throws IncompletRowException {
		/* Asume que previamente se valido que el renglon no sea nulo. */
		Cell celda = renglon.getCell(indiceCelda);

		/* Si la celda esta vacia y no se permiten vacios lo reporta como error */
		if ((celda == null || celda.getCellType() == CellType.BLANK))
			throw new IncompletRowException("[Renglon: " + renglon.getRowNum() + "] La celda " + ValidacionAdecuacionesManager.ENCABEZADO_DETALLE_ADECUACION_ARCHIVO[indiceCelda] + " esta vacia.");
		/*
		 * Si viene como cadena de caracteres retorna el valor, siempre que no
		 * este vacio.
		 */
		if (celda.getCellType() == CellType.STRING && !"".equals(celda.getStringCellValue()) && celda.getStringCellValue() != null)
			return celda.getStringCellValue();
		else if ("".equals(celda.getStringCellValue()) || celda.getStringCellValue() == null)
			throw new IncompletRowException("[Renglon: " + renglon.getRowNum() + "] La celda " + ValidacionAdecuacionesManager.ENCABEZADO_DETALLE_ADECUACION_ARCHIVO[indiceCelda] + " esta vacia.");

		/*
		 * Si es una celda numerica, devuelve su valor como cadena. Si falla lo
		 * reporta
		 */
		if (celda.getCellType() == CellType.NUMERIC)
			return String.valueOf(celda.getNumericCellValue());

		/*
		 * Si es otro tipo de dato (Formula o Boleano o Texto Rico no se puede
		 * procesar
		 */
		else
			throw new IncompletRowException("[Renglon: " + renglon.getRowNum() + "] La celda " + ValidacionAdecuacionesManager.ENCABEZADO_DETALLE_ADECUACION_ARCHIVO[indiceCelda] + " esta en formato no permitido )");

	}

	/**
	 * Devuelve el valor de una celda como numero de punto flotante (Double).
	 * 
	 * @param renglon
	 *            Renglon no vacio
	 * @param indiceCelda
	 *            Numero de la celda a validar
	 * @return Contenido de la celda. Numero entero.
	 * @throws IncompletRowException
	 *             Si la celda esta vacia o no es un numero.
	 */
	public static double validaColumnaNumericaNoVacia(Workbook wb, Row renglon, int indiceCelda) throws IncompletRowException {
		/* Asume que previamente se valido que el renglon no sea nulo. */
		Cell celda = renglon.getCell(indiceCelda);
		double val = 0;

		/* Si la celda esta vacia lo reporta como error */
		if ((celda == null || celda.getCellType() == CellType.BLANK))
			throw new IncompletRowException("[Renglon: " + renglon.getRowNum() + "] La celda " + ValidacionAdecuacionesManager.ENCABEZADO_DETALLE_ADECUACION_ARCHIVO[indiceCelda] + " esta vacia.");
		/*
		 * Si viene como cadena de caracteres intenta convertirla a entero. Si
		 * es decimal arrojara la excepcion
		 */
		else if (celda.getCellType() == CellType.STRING)
			try {
				val = Double.parseDouble(celda.getStringCellValue());
			} catch (NumberFormatException e) {
				throw new IncompletRowException("[Renglon: " + renglon.getRowNum() + "] La celda " + ValidacionAdecuacionesManager.ENCABEZADO_DETALLE_ADECUACION_ARCHIVO[indiceCelda] + " esta en formato TEXTO y no se puede convertir a numero");
			}
		/*
		 * Si es una celda numerica, intenta convertirla a double. Si falla lo
		 * reporta
		 */
		else if (celda.getCellType() == CellType.NUMERIC)
			try {
				val = celda.getNumericCellValue();
			} catch (Exception e) {
				throw new IncompletRowException("[Renglon: " + renglon.getRowNum() + "] La celda " + ValidacionAdecuacionesManager.ENCABEZADO_DETALLE_ADECUACION_ARCHIVO[indiceCelda] + " esta en formato NUMERICO pero no fue posible extraer su valor");
			}
		else if (celda.getCellType() == CellType.FORMULA) {
			FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
			CellValue cellValue = evaluator.evaluate(celda);
			if (cellValue.getCellType() == CellType.NUMERIC)
				try {
					val = celda.getNumericCellValue();
				} catch (Exception e) {
					throw new IncompletRowException("[Renglon: " + renglon.getRowNum() + "] La celda " + ValidacionAdecuacionesManager.ENCABEZADO_DETALLE_ADECUACION_ARCHIVO[indiceCelda] + " es una formula sin embargo su evaluacion no fue posible: " + e.toString());
				}
			else
				throw new IncompletRowException("[Renglon: " + renglon.getRowNum() + "] La celda " + ValidacionAdecuacionesManager.ENCABEZADO_DETALLE_ADECUACION_ARCHIVO[indiceCelda] + " es una formula, sin embargo su resultado no fue  NUMERICO ");
			/*
			 * Si es otro tipo de dato (Boleano o Texto Rico no se puede
			 * procesar
			 */
		} else
			throw new IncompletRowException("[Renglon: " + renglon.getRowNum() + "] La celda " + ValidacionAdecuacionesManager.ENCABEZADO_DETALLE_ADECUACION_ARCHIVO[indiceCelda] + " esta en formato no permitido )");
		/*
		 * Si llega a esta linea es porque no hubo error y se pudo convertir a
		 * double.
		 */
		return val;
	}

	/**
	 * Devuelve la clasificacion de una adecuacion. Se llama al SP
	 * sp_calcula_tipo_nivel_adecuacion. Esta clasificacion es exclusiva de
	 * adecuaciones. Si se requiere clasificar una FIAF se debe llamar el metodo
	 * clasificaFiaf, para clasificar una IADE se debe utilizar el metodo
	 * clasificaIADE
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos
	 * @param folioAdecuacion
	 *            Folio de la adecuacion a clasificar.
	 * @return Nivel y Tipo de adecuacion.
	 * @throws Exception
	 */
	public static ClasificacionAdecuacion clasificaAdecuacion(Connection conn, String folioAdecuacion) throws Exception {

		CallableStatement cstmt = null;
		ClasificacionAdecuacion ca = null;

		try {

			cstmt = conn.prepareCall("{call sp_calcula_tipo_nivel_adecuacion( ?, ?, ? ) }");

			cstmt.setString(1, folioAdecuacion);
			cstmt.registerOutParameter(2, java.sql.Types.INTEGER);
			cstmt.registerOutParameter(3, java.sql.Types.VARCHAR);

			cstmt.execute();

			ca = new ClasificacionAdecuacion(cstmt.getInt(2), cstmt.getString(3));

			return ca;
		} finally {
			CloseObject.closeObject(cstmt, false);
		}

	}

	/**
	 * Devuelve la clasificacion de una adecuacion. Se llama al SP
	 * sp_calcula_tipo_nivel_adecuacion. Esta clasificacion es exclusiva de
	 * adecuaciones. Si se requiere clasificar una FIAF se debe llamar el metodo
	 * clasificaFiaf, para clasificar una IADE se debe utilizar el metodo
	 * clasificaIADE
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos
	 * @param folioAdecuacion
	 *            Folio de la adecuacion a clasificar.
	 * @return Nivel y Tipo de adecuacion.
	 * @throws Exception
	 */
	public static ClasificacionAdecuacion clasificaIADE(Connection conn, int folioAdecuacion) throws Exception {

		CallableStatement cstmt = null;
		ClasificacionAdecuacion ca = null;

		try {

			cstmt = conn.prepareCall("{call sp_calcula_tipo_nivel_IADE( ?, ?, ? ) }");

			cstmt.setInt(1, folioAdecuacion);
			cstmt.registerOutParameter(2, java.sql.Types.INTEGER);
			cstmt.registerOutParameter(3, java.sql.Types.VARCHAR);

			cstmt.execute();

			ca = new ClasificacionAdecuacion(cstmt.getInt(2), cstmt.getString(3));

			return ca;
		} finally {
			CloseObject.closeObject(cstmt, false);
		}

	}

	/**
	 * Devuelve la clasificacion de una adecuacion. Se llama al SP
	 * sp_calcula_tipo_nivel_adecuacion. Esta clasificacion es exclusiva de
	 * adecuaciones. Si se requiere clasificar una FIAF se debe llamar el metodo
	 * clasificaFiaf, para clasificar una IADE se debe utilizar el metodo
	 * clasificaIADE
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos
	 * @param folioAdecuacion
	 *            Folio de la adecuacion a clasificar.
	 * @return Nivel y Tipo de adecuacion.
	 * @throws Exception
	 */
	public static ClasificacionAdecuacion clasificaFIAF(Connection conn, int folioAdecuacion) throws Exception {

		CallableStatement cstmt = null;
		ClasificacionAdecuacion ca = null;

		try {

			cstmt = conn.prepareCall("{call sp_calcula_tipo_nivel_FIAF( ?, ?, ? ) }");

			cstmt.setInt(1, folioAdecuacion);
			cstmt.registerOutParameter(2, java.sql.Types.INTEGER);
			cstmt.registerOutParameter(3, java.sql.Types.VARCHAR);

			cstmt.execute();

			ca = new ClasificacionAdecuacion(cstmt.getInt(2), cstmt.getString(3));

			return ca;
		} finally {
			CloseObject.closeObject(cstmt, false);
		}

	}

	public static void insertaArchivoValidacionCuerpo(Connection conn, Adecuacion adecuacion, int nFolio, String cSuperReduccion, String cSRInterna) throws Exception {
		log.info(" Iniciando carga de archivo excel de adecuaciones para su validacion Folio[" + nFolio + "]");

		String query = "INSERT INTO tValida_Adecuacion(Folio, Secuencia, EP, MAP, funcion, programa_general, programa, partida, movimiento, anual, enero, febrero, marzo, abril, mayo, junio, julio, agosto, septiembre, octubre, noviembre, diciembre) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String mensajes = "";

		int totalInsertados = 0;
		int contadorRenglones = -1;

		PreparedStatement ps = null;

		try {

			ps = conn.prepareStatement(query);
			for (Iterator<AdecuacionDetalle> i = adecuacion.getDetalle().iterator(); i.hasNext();) {
				try {
					contadorRenglones++;
					AdecuacionDetalle detalle = i.next();

					log.trace("Procesando renglon " + contadorRenglones + " del archivo excel");

					/* Insercion de informacion requerida para la validacion */
					ps.setString(1, "A" + nFolio);
					ps.setInt(2, detalle.getSecuencia());

					String cveSIAFF = detalle.getClaveSIAFF();
					String funcion = cveSIAFF.substring(12, 15);
					String programaGeneral = cveSIAFF.substring(26, 27);
					String programa = cveSIAFF.substring(26, 30);
					String partida = cveSIAFF.substring(31, 40);

					ps.setString(3, cveSIAFF);
					ps.setString(4, detalle.getClaveInterna().substring(0, 7));

					ps.setString(5, funcion);
					ps.setString(6, programaGeneral);
					ps.setString(7, programa);
					ps.setString(8, partida);
					ps.setString(9, detalle.getTipo());

					/* Insercion de montos */
					for (int n = 0; n < 13; n++) {
						ps.setDouble(10 + n, detalle.getMontos().get(n));
					}
					totalInsertados++;
					ps.addBatch();
				} catch (Exception e) {
					mensajes += "\nERROR PROCESANDO RENGLON " + contadorRenglones + ": " + e.toString();
				}
			}
			if (mensajes.length() > 0)
				throw new Exception(mensajes);
			ps.executeBatch();
			log.info("Finalizando carga de archivo excel de adecuaciones para su validacion Folio[" + nFolio + "] Se insertaron " + totalInsertados);
		} finally {
			CloseObject.closeObject(ps, false);
		}

	}

	public static int liberaArchivoValidacionAdecuacion(Connection conn, Adecuacion adecuacion, int nFolio) throws Exception {
		String query = "DELETE FROM tValida_Adecuacion WHERE folio = 'A" + nFolio + "'";
		int borrados = 0;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(query);
			borrados = ps.executeUpdate();
			return borrados;
		} finally {
			CloseObject.closeObject(ps, false);
		}

	}

	/**
	 * Valida que el usuario que subio el excel de la adecuacion pertenesca a la
	 * unidad ejecutora que viene en el excel.
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos
	 * @param usuario
	 *            Usuario que sube la adecuacion
	 * @param adecuacion
	 *            Adecuacion recibida
	 * @return Mensaje con el error.
	 * @throws Exception
	 */

	public static String validaUnidadUsuarioAdecuacion(Connection conn, Usuario usuario, Adecuacion adecuacion) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		String mensajes = new String("");
		String query = "SELECT	1 " + "  FROM	CG_CAT_EMPLEADO AS EM WITH (NOLOCK) " + "        JOIN tCatalogoUnidadResponsable AS UR WITH (NOLOCK) ON " + "        EM.ID_AREA = UR.ID_AREA " + " WHERE	CE_OS_RESPONSABLE = ? " + "   AND	ur.cUnidadResponsable = ?";
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, usuario.getLogin());
			ps.setString(2, adecuacion.getEncabezado().getUnidadEjecutora());

			rs = ps.executeQuery();

			if (!rs.next()) {
				mensajes = ("El usuario " + usuario.getLogin() + " no pertenece a la unidad ejecutora :" + adecuacion.getEncabezado().getUnidadEjecutora());
			}
			return mensajes;

		} finally {
			CloseObject.closeObject(ps, false);
			CloseObject.closeObject(rs, false);
		}
	}

	/**
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos
	 * @param adecuacion
	 *            Adecuacion recibida
	 * @return Mensaje con el error.
	 * @throws Exception
	 */

	public static String validaRamoAdecuacion(Connection conn, Adecuacion adecuacion) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		String mensajes = new String("");
		String query = "SELECT cRamo FROM dbo.tRamo WHERE cRamo = ?";
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, adecuacion.getEncabezado().getRamo());

			rs = ps.executeQuery();

			if (!rs.next()) {
				mensajes = ("El Ramo " + adecuacion.getEncabezado().getRamo() + " capturado en el archivo no es valido para CONAFOR.");
			}
			return mensajes;

		} finally {
			CloseObject.closeObject(ps, false);
			CloseObject.closeObject(rs, false);
		}
	}

	/**
	 * LLama al SP que se encarga de realizar las validaciones generales de la
	 * adecuacion<br>
	 * <ul>
	 * <li>Compensacion Anual</li>
	 * <li>Compensacion Mensual</li>
	 * <li>El usuario pertence a la UE en el archivo</li>
	 * <li>La suma de los meses debe ser igual al anual</li>
	 * </ul>
	 * 
	 * @param conn
	 *            Conexion activa a la base de datos.
	 * @param nFolioAdecuacion
	 *            Folio de la adecuacion a validar.
	 * @return Lista con los errores encontrados. Si la lista es vacia no se
	 *         encontraron errores.
	 * @throws Exception
	 *             En caso de error
	 */
	public static List<String> validacionGeneralAdecuacion(Connection conn, String nFolioAdecuacion) throws Exception {

		List<String> mensajes = new ArrayList<String>();
		CallableStatement cs = null;
		ResultSet rs = null;

		String query = "{call dbo.fn_valida_adecuacion_general( ? ) }";

		try {
			cs = conn.prepareCall(query);
			cs.setString(1, nFolioAdecuacion);

			rs = cs.executeQuery();

			while (rs.next()) {
				String ret = rs.getString(1);
				if (ret != null && !"".equals(ret))
					mensajes.add(ret);
			}

			return mensajes;
		} finally {
			CloseObject.closeObject(rs, false);
			CloseObject.closeObject(cs, false);
		}
	}

	/**
	 * 
	 * @param conn
	 * @param nFolioAdecuacion
	 * @return
	 * @throws Exception
	 */
	public static List<String> validaUsuarioExterno(Connection conn, String nFolioAdecuacion, String usuarioLogin) throws Exception {

		List<String> mensajes = new ArrayList<String>();
		CallableStatement cs = null;
		ResultSet rs = null;

		String query = "{call fn_valida_adecuacion_esForaneo( ?,? ) }";

		try {
			cs = conn.prepareCall(query);
			cs.setString(1, nFolioAdecuacion);
			cs.setString(2, usuarioLogin);

			rs = cs.executeQuery();

			while (rs.next()) {
				mensajes.add(rs.getString(1));
			}

			return mensajes;
		} finally {
			CloseObject.closeObject(rs, false);
			CloseObject.closeObject(cs, false);
		}

	}

	/**
	 * 
	 * @param conn
	 * @param nFolioAdecuacion
	 * @return
	 * @throws Exception
	 */
	public static String validaEsCalendario(Connection conn, String nFolioAdecuacion) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		String mensaje = "";

		String query = "SELECT dbo.fn_valida_adecuacion_define_tipo_adecuacion( '" + nFolioAdecuacion + "' ) ";

		try {
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();

			if (rs.next())
				mensaje = rs.getString(1);

			return mensaje;

		} finally {
			CloseObject.closeObject(rs, false);
			CloseObject.closeObject(ps, false);
		}
	}

	public static ArrayList<String> validaSaldos(Connection conn, String nFolioAdecuacion) throws Exception {

		ArrayList<String> mensajes = new ArrayList<String>();
		CallableStatement cs = null;
		ResultSet rs = null;

		String query = "{call fn_valida_adecuacion_saldos( ? ) }";

		try {
			cs = conn.prepareCall(query);
			cs.setString(1, nFolioAdecuacion);

			rs = cs.executeQuery();

			while (rs.next()) {
				mensajes.add(rs.getString(1));
			}

			return mensajes;
		} finally {
			CloseObject.closeObject(rs, false);
			CloseObject.closeObject(cs, false);
		}

	}
	public static ArrayList<String> validaPrograma(Connection conn, String nFolioAdecuacion) throws Exception {

		ArrayList<String> mensajes = new ArrayList<String>();
		CallableStatement cs = null;
		ResultSet rs = null;

		String query = "{call sp_valida_programa( ? ) }";

		try {
			cs = conn.prepareCall(query);
			cs.setString(1, nFolioAdecuacion);

			rs = cs.executeQuery();

			while (rs.next()) {
				mensajes.add(rs.getString(1));
			}

			return mensajes;
		} finally {
			CloseObject.closeObject(rs, false);
			CloseObject.closeObject(cs, false);
		}

	}
	public static String clasificaAdecuacion() throws Exception {

		return "";
	}

	public static String validaMontoTotal(Connection conn, Adecuacion adecuacion, String folio, String tipo) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		String mensajes = new String("");
		//double monto;
		String query = "SELECT * FROM (SELECT SUM(anual) AS suma FROM dbo.tValida_Adecuacion WITH(NOLOCK) WHERE movimiento = ? and folio = ?) AS suma WHERE suma <> ?";
		try {
			//String pattern = "#.00";
			//DecimalFormat myFormatter = new DecimalFormat(pattern);

			ps = conn.prepareStatement(query);
			ps.setString(1, tipo);
			ps.setString(2, folio);
			// monto = adecuacion.getEncabezado().getMontoTotal();
			// monto = (Double) ( myFormatter.parse(
			// String.valueOf(adecuacion.getEncabezado().getMontoTotal()) ) );
			// System.out.println("Monto: [" + monto + "]");
			ps.setDouble(3, adecuacion.getEncabezado().getMontoTotal());

			rs = ps.executeQuery();

			if (rs.next()) {
				// mensajes =
				// ("El Monto Total capturado en el encabezado del Lay Out no corresponde a la suma de Reducciones.");
			}
			return mensajes;

		} finally {
			CloseObject.closeObject(ps, false);
			CloseObject.closeObject(rs, false);
		}
	}
	
}
