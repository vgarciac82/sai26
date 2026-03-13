package com.syc.sai.procesos;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;

import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class ExportarFotografiasRF {

	public static void main(String[] args) throws Exception {
		String directorioSalida = args[0];
		String archivoEntrada = args[1];

		List<String> folios = cargaFolios(archivoEntrada);
		List<String> paths = exporSeveraltExpedients(folios, directorioSalida, true);
		for (String path : paths)
			System.out.println(path);
	}

	public static List<String> exporSeveraltExpedients(List<String> exportList, String directorioSalida, boolean standAlone) throws Exception {

		Connection conn = null;
		String zipExportNameRaiz = directorioSalida + File.separatorChar + "export_" + System.currentTimeMillis() + "_" + (Math.random() * 1000);
		List<String> rutas = new ArrayList<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String query = "SELECT	archivo  FROM	v_ArchivosEmpleadoFoto  WHERE	NUMERO_EMPLEADO = ?";

		try {
			if (standAlone)
				conn = Util.getStandAloneConnection();

			FileOutputStream fos = null;
			ZipOutputStream zos = null;

			ps = conn.prepareStatement(query);

			int i = 0;
			int cntFile = 1;

			for (String numeroEmpleado : exportList) {

				if (i % 1000 == 0) {
					if (fos != null) {
						zos.flush();
						zos.closeEntry();
						zos.close();

					}

					String zipExportName = zipExportNameRaiz + "_" + cntFile + ".zip";

					fos = new FileOutputStream(zipExportName);
					zos = new ZipOutputStream(fos);
					cntFile++;
					rutas.add(zipExportName);
				}

				System.out.println("Exportando Foto " + (i + 1) + " de " + exportList.size() + " Numero de Empleado: " + numeroEmpleado);

				StringBuilder rutaArchivo = null;
				ps.setString(1, numeroEmpleado);
				rs = ps.executeQuery();

				if (rs.next()) {
					rutaArchivo = new StringBuilder(rs.getString(1));
					Util.addToZip(zos, String.valueOf(numeroEmpleado), rutaArchivo.toString());
					i++;
				}
			}
			try {
				zos.flush();
				zos.closeEntry();
				zos.close();
			} catch (Exception e) {
				System.err.println("Problemas cerradno flujo: " + e);
			}

			return rutas;

		} finally {
			CloseObject.closeObject(conn);
		}
	}

	private static List<String> cargaFolios(String inputFile) throws Exception {

		File f = new File(inputFile);
		BufferedReader entrada = null;
		List<String> l = new ArrayList<String>();

		try {

			entrada = new BufferedReader(new FileReader(f));

			while (entrada.ready()) {
				l.add(entrada.readLine());
			}

			return l;

		} finally {
			if (entrada != null)
				try {
					entrada.close();
					entrada = null;
				} catch (Exception e) {

				}
		}

	}
}
