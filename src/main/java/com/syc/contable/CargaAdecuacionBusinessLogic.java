package com.syc.contable;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.syc.contable.core.CargaAdecuacionManager;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class CargaAdecuacionBusinessLogic extends DataSourceManager {

	private static Logger	log					= Logger.getLogger(CargaAdecuacionBusinessLogic.class);
	public boolean			correoProduccion	= false;
	private String			jniName				= "";

	public CargaAdecuacionBusinessLogic(String jniName) {
		this.jniName = jniName;
		super.init(jniName);
	}

	public String procesaLayoutAdecuacionesMAP( String archivoOrigen, DataInputStream archivoCargaStream, HttpServletRequest req, HttpServletResponse resp, int mes, String cEsIP, Map<String, String> plantillas) throws Exception {
		String resultado = null;
		Connection conn = null;
		String file = null;
		Boolean error = true;
		
		conn = getConnection();
		
		try {
			if ("N".equals(cEsIP)){
				CargaAdecuacionManager.borrarMes(conn, mes);
				 error = CargaAdecuacionManager.copiaArchivoRemoto(conn, archivoOrigen, archivoCargaStream, mes);
					if (!error)
						throw new Exception("Error al copiar el archivo al servidor") ;
					
				conn.commit();
			}
			
			file = CargaAdecuacionManager.insertRegisterLayout(conn, mes, cEsIP, plantillas);
			conn.commit();
			
			resp.setContentType("application/vnd.ms-excel");
			resp.addHeader("Content-Disposition", "inline; filename=\"" + file + "\"; ");

			ServletOutputStream out = resp.getOutputStream();

			Util.doDownload(out, file, file, "");

			out.flush();
			out.close();

		} catch (Exception e3) {
			if( conn != null )
				try{
					conn.rollback();
				}catch(Exception e4){
					log.warn("Error en rollback: " + e4);
				}
			
			log.warn("No se proceso el archivo" + e3);
			resultado = "No se pudo procesar el archivo en sql " + e3;
			
			
		}
		
		finally {

			CloseObject.closeObject(conn);
			if (file != null) {
				File f = new File(file);
				if (!f.delete())
					f.deleteOnExit();
			}
		}
		return resultado;
	}

	public String cargaAdecuacionesMAP(String ruta, String nombreArchivo) throws Exception {
		String cargaCompleta = null;
		Connection conn = null;

		conn = getConnection();

		try {

			CargaAdecuacionManager.cargaArchivo(conn, ruta);
		} catch (Exception e) {
			log.warn("No se proceso el archivo" + e);
			cargaCompleta = "No se pudo cargar el archivo " + e;
		} finally {
			CloseObject.closeObject(conn);
		}
		return cargaCompleta;
	}
	
	public String consultaLayoutAdecuacionesMAP( String archivoOrigen, DataInputStream archivoCargaStream, HttpServletRequest req, HttpServletResponse resp, int mes, int version, Map<String, String> plantillas) throws Exception {
		
		String resultado = null;
		Connection conn = null;
		String file = null;
		conn = getConnection();
		
		
		try {
			file = CargaAdecuacionManager.consultaRegisterLayout(conn, mes, version, plantillas);
			
			resp.setContentType("application/vnd.ms-excel");
			resp.addHeader("Content-Disposition", "inline; filename=\"" + file + "\"; ");

			ServletOutputStream out = resp.getOutputStream();
			Util.doDownload(out, file, file, "");

			out.flush();
			out.close();
		}
		finally {
			CloseObject.closeObject(conn);
			if (file != null) {
				File f = new File(file);
				if (!f.delete())
					f.deleteOnExit();
			}
		}
		return resultado;
	}
	public void borrarMes(int mes) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();

			CargaAdecuacionManager.borrarMes(conn, mes);

			conn.commit();

		} catch (Exception e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.warn("Problemas en rollback: " + e2);
				}
			throw e;
		} finally {
			CloseObject.closeObject(conn);
		}

	}

}
