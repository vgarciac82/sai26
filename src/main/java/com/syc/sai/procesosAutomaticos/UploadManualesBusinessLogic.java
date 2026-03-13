package com.syc.sai.procesosAutomaticos;

import java.sql.Connection;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.contable.core.ManualContable;
import com.syc.contable.core.ManualContableDetalle;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.ejercido.pagado.CLCAttachmentBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class UploadManualesBusinessLogic extends DataSourceManager {

	private static final Logger						log		= Logger.getLogger(UploadManualesBusinessLogic.class);
	private static ConfiguraAplicativoBusinessLogic	cabl	= null;
	private static FolioGeneratorInterface			fg		= null;
	private static CLCAttachmentBusinessLogic		clcabl	= null;

	public UploadManualesBusinessLogic(String jniName, String folioGenerator) throws Exception {

		super.init(jniName);

		synchronized (this) {
			if (cabl == null)
				cabl = new ConfiguraAplicativoBusinessLogic(jniName);
			if (clcabl == null)
				clcabl = new CLCAttachmentBusinessLogic(jniName);
			if (fg == null) {
				if (!StringUtils.isEmpty(folioGenerator)) {
					ClassLoader cl = getClass().getClassLoader();
					Class<?> clase = cl.loadClass(folioGenerator);
					fg = (FolioGeneratorInterface) clase.newInstance();
				}
			}
		}
	}
	
	public String uploadManual(Usuario u, String nombreCarpeta, String nombreDestino, int nIDversion) {
		Connection conn = null;
		Caso c = null;
		log.info("Iniciando el proceso de ajuntar el manual contable en la carpeta[" + nombreCarpeta + "] Archivo[" + nombreDestino + "]");
		String msg = "";
		int idManual = -1;
		
		try {
			
			conn = getConnection();
			
			idManual = UploadManualesManager.ObtenerIDManual(conn, nombreCarpeta);//OK
			
			String centroContable = u.getPropiedad("CCENTROCONTABLE").getValor();
			boolean esEdoCta = "Estado de Cuenta".equalsIgnoreCase(nombreCarpeta);

			if (!UploadManualesManager.existeManualContableCC(conn, centroContable, idManual, nIDversion)) {//OK
				c = UploadManualesManager.creaManualContable(conn, u, Integer.parseInt(cabl.getSystemSetting("CARGA_MC_IDTC")), centroContable, fg);//OK
			} else {
				c = UploadManualesManager.readCasoOrigenManualContable(conn, centroContable, idManual);//OK
			}

			ManualContable conCon = UploadManualesManager.readManualContable(conn, c.getFolio());//OK

			if (!UploadManualesManager.existeVersionManual(conCon, idManual)) {//OK

				ManualContableDetalle detalle = new ManualContableDetalle(conCon.getEncabezado().getfolioManualContable(), u.getLogin(), idManual, nIDversion);
				UploadManualesManager.insertaDetalle(conn, detalle);//OK
				conCon.getDetalle().add(detalle);

				clcabl.attachDocumentoConciliacion(conn, nombreDestino, nombreCarpeta, nombreCarpeta, c.getFolio(), true);
				msg = "Archivo cargado exitosamente.";

			} else {
				msg = "El archivo ya existe. Se ignora";
			}

			conn.commit();

		} catch (Exception e) {
			log.error(e, e);
			msg = "Error adjuntando conciliacion bancaria firmada: " + e.toString();
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.warn("Problemas en rollback: " + e2);
				}
		} finally {
			CloseObject.closeObject(conn);
		}
		return msg;
	}
}
