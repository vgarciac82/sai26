package com.syc.contable.adecuaciones;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.sai.contabilidad.utils.db.CloseObject;

import common.Logger;

public class ValidacionAdecuacionesBusinessLogic extends DataSourceManager {

	private static final Logger	log	= Logger.getLogger(ValidacionAdecuacionesBusinessLogic.class);

	/**
	 * Construye una nueva instancia del objeto.
	 * 
	 * @param jniName
	 *            Cadena para buscar el DataSource
	 */
	public ValidacionAdecuacionesBusinessLogic(String jniName) {
		super.init(jniName);
	}

	/**
	 * Inserta valida y tipifica la adecuacion.
	 * 
	 * @param archivo
	 *            Archivo con la adecuacion
	 * @param c
	 *            Caso abierto de adecuacion
	 * @param usuario
	 *            Usaurio que genera la adecuacion
	 * @param cSuperReduccion
	 *            Indica si es una super reduccion
	 * @param cSRInterna
	 *            Indica si es una reduccion interna.
	 * @return Cadena con mensajes de error. Si es vacio, todo fue correcto.
	 */
	public String insertaAdecuacion(String archivo, Caso c, Usuario usuario, String cSuperReduccion, String cSRInterna) throws Exception {
		Connection conn = null;
		String errores = "";

		File f = null;
		FileInputStream stream = null;

		HSSFWorkbook workbook = null;
		HSSFSheet sheet = null;

		try {
			conn = getConnection();
			f = new File(archivo);

			if (!f.exists())
				throw new FileNotFoundException("No se encontro el archivo de carga[ " + archivo + "]");

			stream = new FileInputStream(f);
			workbook = new HSSFWorkbook(stream);
			sheet = workbook.getSheetAt(0);

			int nFolio = Integer.parseInt(c.getFolio().substring(9));

			errores += ValidacionAdecuacionesManager.insertaArchivoValidacionCuerpo(conn, workbook, sheet, nFolio, 5);

			if (errores.length() == 0)
				conn.commit();
			else
				conn.rollback();

		} catch (Exception e) {
			log.error(e, e);
			errores = e.toString();
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.warn("Problemas con rollback: " + e2);
				}
			if (stream != null)
				try {
					stream.close();
				} catch (Exception e2) {
					log.warn("No se pudo cerrar el flujo hacia el archivo de adecuacion." + e2.toString());
				} finally {
					stream = null;
				}

		} finally {
			CloseObject.closeObject(conn, false);
			workbook = null;
			sheet = null;
		}

		return errores;
	}

	/**
	 * Devuelve la clasificacion de una adecuacion. <br>
	 * Existen los siguientes tipos de adecuacion: <br>
	 * <b> Calendario</b> <br>
	 * Nivel 3 si esta compensado. <br>
	 * Nivel 5 si no esta compensado. <br>
	 * <b> Ampliacion</b> <br>
	 * Nivel 5 <br>
	 * <b> Reduccion</b> <br>
	 * Nivel 5 <br>
	 * <br>
	 * <b> Transferencia</b> <br>
	 * Nivel 3 <br>
	 * Nivel 4 <br>
	 * Nivel 5
	 * 
	 * @param folioAdecuacion
	 *            Folio de la adecuacion a validar.
	 * @return Nivel y tipo de adecuacion encapsulada en el objeto
	 *         ClasificacionAdecuacion.
	 * @throws Exception
	 */
	public ClasificacionAdecuacion clasificaAdecuacion(String folioAdecuacion) throws Exception {
		Connection conn = null;
		ClasificacionAdecuacion ca = null;

		try {
			conn = getConnection();
			ca = ValidacionAdecuacionesManager.clasificaAdecuacion(conn, folioAdecuacion);
			return ca;
		} finally {
			CloseObject.closeObject(conn, false);
		}

	}

	/**
	 * Devuelve la clasificacion de una FIAF tomando en cuenta el detalle de
	 * todas las adecuaciones que la integran como una sola. <br>
	 * Se clasificara como una adecuacion siguiendo el siguiente esquema:<br>
	 * <b> Calendario</b> <br>
	 * Nivel 3 si esta compensado. <br>
	 * Nivel 5 si no esta compensado. <br>
	 * <b> Ampliacion</b> <br>
	 * Nivel 5 <br>
	 * <b> Reduccion</b> <br>
	 * Nivel 5 <br>
	 * <br>
	 * <b> Transferencia</b> <br>
	 * Nivel 3 <br>
	 * Nivel 4 <br>
	 * Nivel 5
	 * 
	 * @param folioAdecuacion
	 *            Folio de la adecuacion a validar.
	 * @return Nivel y tipo de adecuacion encapsulada en el objeto
	 *         ClasificacionAdecuacion.
	 * @throws Exception
	 */
	public ClasificacionAdecuacion clasificaFIAF(int folioAdecuacion) throws Exception {
		Connection conn = null;
		ClasificacionAdecuacion ca = null;

		try {
			conn = getConnection();
			ca = ValidacionAdecuacionesManager.clasificaFIAF(conn, folioAdecuacion);
			return ca;
		} finally {
			CloseObject.closeObject(conn, false);
		}

	}

	/**
	 * Devuelve la clasificacion de una IADE tomando en cuenta el detalle de
	 * todas las adecuaciones e integraciones FIAF que la integran como una
	 * sola. <br>
	 * Se clasificara como una adecuacion siguiendo el siguiente esquema:<br>
	 * <b> Calendario</b> <br>
	 * Nivel 3 si esta compensado. <br>
	 * Nivel 5 si no esta compensado. <br>
	 * <b> Ampliacion</b> <br>
	 * Nivel 5 <br>
	 * <b> Reduccion</b> <br>
	 * Nivel 5 <br>
	 * <br>
	 * <b> Transferencia</b> <br>
	 * Nivel 3 <br>
	 * Nivel 4 <br>
	 * Nivel 5
	 * 
	 * @param folioAdecuacion
	 *            Folio de la adecuacion a validar.
	 * @return Nivel y tipo de adecuacion encapsulada en el objeto
	 *         ClasificacionAdecuacion.
	 * @throws Exception
	 */
	public ClasificacionAdecuacion clasificaIADE(int folioAdecuacion) throws Exception {
		Connection conn = null;
		ClasificacionAdecuacion ca = null;

		try {
			conn = getConnection();
			ca = ValidacionAdecuacionesManager.clasificaIADE(conn, folioAdecuacion);
			return ca;
		} finally {
			CloseObject.closeObject(conn, false);
		}

	}

	public void insertaArchivoValidacion(Adecuacion adecuacion, Caso c, Usuario usuario, String cSuperReduccion, String cSRInterna) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			int nFolio = Integer.parseInt(c.getFolio().substring(9));
			ValidacionAdecuacionesManager.insertaArchivoValidacionCuerpo(conn, adecuacion, nFolio, cSuperReduccion, cSRInterna);
			conn.commit();
		} catch (Exception e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.warn("Problemas realizando rollback: " + e2, e2);
				}
			throw e;
		} finally {
			CloseObject.closeObject(conn, false);
		}
	}

	public void liberaArchivoValidacionAdecuacion(Adecuacion adecuacion, Caso c, Usuario usuario, String cSuperReduccion, String cSRInterna) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			int nFolio = Integer.parseInt(c.getFolio().substring(9));
			ValidacionAdecuacionesManager.liberaArchivoValidacionAdecuacion(conn, adecuacion, nFolio);
			conn.commit();
		} catch (Exception e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.warn("Problemas realizando rollback: " + e2, e2);
				}
			throw e;
		} finally {
			CloseObject.closeObject(conn, false);
		}
	}

	/**
	 * Ejecut
	 * @param nFolioAdc
	 * @return
	 * @throws Exception
	 */
	public List<String> validacionGeneralAdecuacion(String ejercicioFiscal, String nFolioAdc) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			return ValidacionAdecuacionesManager.validacionGeneralAdecuacion(conn, nFolioAdc);
		} finally {
			CloseObject.closeObject(conn, false);
		}
	}
	
}
