package com.syc.sai.procesosAutomaticos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.tesoreria.estadoDeCuenta.EstadoDeCuentaSimple;
import com.syc.utils.zip.ZipManager;

public class EstadoDeCuentaBusinessLogic extends DataSourceManager {

	private static final Logger	log	= Logger.getLogger(EstadoDeCuentaBusinessLogic.class);

	public EstadoDeCuentaBusinessLogic(String jniName) {
		super.init(jniName);
	}

	public int adjuntaEstadoDeCuentaMasivo(File archivoZip, Usuario u, int idTCaso, FolioGeneratorInterface fg, String opResponsable, String jniName, String directorioTemporal) throws Exception {
		directorioTemporal = (directorioTemporal + (directorioTemporal.endsWith(String.valueOf(File.separatorChar)) ? "" : File.separatorChar) + String.valueOf(System.currentTimeMillis()));
		List<File> procesar = ZipManager.extraeArchivosMemoria(archivoZip.getAbsolutePath(), directorioTemporal);
		Connection conn = null;

		try {

			conn = getConnection();

			for (Iterator<File> iterator = procesar.iterator(); iterator.hasNext();) {
				File f = iterator.next();
				String ext = Util.getFileExtencion(f.getName());

				if ("TXT".equalsIgnoreCase(ext)) {
					EstadoDeCuentaSimple edoCta = procesaTextoEdoCta(conn, f);
					log.info(edoCta);
				} else if ("PDF".equalsIgnoreCase(ext)) {
					EstadoDeCuentaSimple edoCta = procesaPDFEdoCta(conn, f);
					log.info(edoCta);
				} else {
					throw new Exception("No se permiten archivo del tipo [" + ext + "]. Eliminelo del zip e intente de nuevo");
				}

				log.info(f.getAbsolutePath());
			}

			conn.commit();
			return 0;
		} catch (Exception e) {
			try {

			} catch (Exception e2) {
				log.warn(e2);
			}
			throw e;
		} finally {
			CloseObject.closeObject(conn);
		}
	}

	private EstadoDeCuentaSimple procesaPDFEdoCta(Connection conn, File f) throws Exception {

		String nombre = Util.getFileWithoutExtencion(f.getName());
		String[] datos = nombre.split("_");
		
		if (datos != null && datos.length < 3)
			throw new Exception("El nombre del archivo es incorrecto. Valide que sea de la forma: DD_MM_YYYY_#####.pdf ");
		
		EstadoDeCuentaSimple edoCta = new EstadoDeCuentaSimple(datos[3], datos[0] + "/" + datos[1] + "/" + datos[2], 0.0d, 0.0d);
		return edoCta;
		
	}

	private EstadoDeCuentaSimple procesaTextoEdoCta(Connection conn, File f) throws Exception {

		FileInputStream fstream = null;
		BufferedReader br = null;

		try {
				
			// Abrir archivo
			fstream = new FileInputStream(f);
			br = new BufferedReader(new InputStreamReader(fstream));
			String strLine = "";
			EstadoDeCuentaSimple edoCta = null;

			int renglon = 0;

			// Procesar archivo (Extraer Informacion)
			while ((strLine = br.readLine()) != null) {
				if (renglon++ == 0)
					continue;
				else {
					String[] str = strLine.split("[|]");
					if (str != null && str.length <= 3)
						throw new Exception("El primer renglon de informacion esta vacio o incompleto. Validar");
					// Crear objeto
					edoCta = new EstadoDeCuentaSimple(str[0], str[2], 0.0d, 0.0d);
					break;
				}
			}

			return edoCta;
		} catch (Exception e) {
			throw e;
		} finally {
			// Cerrar Archivo
			if (fstream != null)
				fstream.close();
			if (br != null)
				br.close();
		}
	}
}
