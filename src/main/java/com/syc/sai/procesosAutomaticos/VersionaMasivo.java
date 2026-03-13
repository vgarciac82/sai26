package com.syc.sai.procesosAutomaticos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.fortimax.core.Carpeta;
import com.syc.fortimax.core.CarpetaManager;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.sai.bitacora.BitacoraOperacionDoctosBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class VersionaMasivo extends DataSourceManager {

	private String				jniName;
	private static final Logger	logger				= Logger.getLogger(VersionaMasivo.class);
	private static final String	NOMBRE_CARPETA		= "Solicitud de Pago";
	private static final String	NOMBRE_DOCUMENTO	= "Solicitud de Pago Firmada";

	public VersionaMasivo(String jniName) {
		super.init(jniName);
		this.jniName = jniName;
	}

	public String getJniName() {
		return jniName;
	}

	public void setJniName(String jniName) {
		this.jniName = jniName;
	}

	// http://localhost:8181/sai/filestore?select=PAGODIVERSO_G11069C2D1
	public List<String> generaVersiones() {

		List<String> log = new ArrayList<String>();

		try {

			BitacoraOperacionDoctosBusinessLogic bodbl = new BitacoraOperacionDoctosBusinessLogic(getJniName(), "ADMIN", "", -1, -1);
			CasoBusinessLogic cbl = new CasoBusinessLogic(getJniName());

			List<Tramite> tramites = listaTramites();

			for (Iterator<Tramite> i = tramites.iterator(); i.hasNext();) {
				String proc = "";
				try {

					Tramite t = i.next();
					proc += "Proceso. Tramite:" + t.getTituloAplicacion() + " Contrarecibo:" + t.getCxp() + " Folio SAI:" + t.getFolio() + " Folio:" + t.getNfolio();
					Documento d = buscaDocumentoVersionar(t);

					if (d != null) {

						String nodeID = d.getTituloAplicacion() + "_G" + d.getIdGabinete() + "C" + d.getIdCarpetaPadre() + "D" + d.getIdDocumento();

						bodbl.setModulo(d.getTituloAplicacion());
						bodbl.setnFolio(t.getNfolio());
						bodbl.insertaBitacora(10, "El usuario intenta limpiar un documento " + nodeID + " en el expediente");

						String docName = cbl.versionaDocumento(bodbl, nodeID);
						proc += "\nEl documento se versiono exitosamente. ID: " + docName;
					} else {
						proc += "\nNo se encontro en el tramite la carpeta " + VersionaMasivo.NOMBRE_CARPETA + " o el documento " + VersionaMasivo.NOMBRE_DOCUMENTO;
					}

				} catch (DocumentoVacioException e) {
					proc += "\n Error: " + e;
					logger.error(e, e);
				} catch (Exception e) {
					logger.error(e, e);
					proc += "\n Error: " + e;
				}
				log.add(proc);
			}

		} catch (Exception e) {
			log.add("Ocurrio el siguiente error: " + e);
		}
		return log;
	}

	private List<Tramite> listaTramites() throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = " SELECT DISTINCT cxp,  " 
		             + "        tipopago, " 
		             + "        folio,  " 
		             + "        nfolio,  " 
		             + "        id_gabinete " 
		             + " FROM   tmp_pagos_regularizacion AS pagos WITH(nolock) " 
		             + "        LEFT OUTER JOIN vtramitesexportar exportar WITH(nolock) "
		             + "                     ON pagos.tipopago = exportar.cdocumento  " 
		             + "                        AND pagos.cxp = exportar.canocontrarrecibo " 
		             + " WHERE  cxp <> '1'  " + " ORDER  BY folio";
		List<Tramite> tramites = new ArrayList<VersionaMasivo.Tramite>();

		try {
			conn = getConnection();
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();

			while (rs.next()) {
				Tramite t = new Tramite();

				t.setCxp(rs.getString("cxp"));
				t.setTituloAplicacion(rs.getString("tipopago"));
				t.setFolio(rs.getString("folio"));
				t.setNfolio(rs.getInt("nfolio"));
				t.setId_gabinete(rs.getInt("id_gabinete"));

				tramites.add(t);
			}

			return tramites;
		} finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(ps);
			CloseObject.closeObject(conn);
		}
	}

	private Documento buscaDocumentoVersionar(Tramite t) throws DocumentoVacioException, Exception {
		Documento d = null;
		Carpeta c = null;
		Connection conn = null;
		try {
			conn = getConnection();
			c = CarpetaManager.getCarpetaByName(conn, "FEDERALIZADO".equalsIgnoreCase( t.getTituloAplicacion() )?"PAGO"+t.getTituloAplicacion():t.getTituloAplicacion(), t.getId_gabinete(), VersionaMasivo.NOMBRE_CARPETA);
			if (c != null) {
				if (DocumentoManager.existeDocumentoCapturado(conn, c.getTituloAplicacion(), c.getIdGabinete(), c.getNombreCarpeta(), VersionaMasivo.NOMBRE_DOCUMENTO)) {
					d = DocumentoManager.buscaDocumento(conn, c.getTituloAplicacion(), c.getIdGabinete(), c.getIdCarpeta(), VersionaMasivo.NOMBRE_DOCUMENTO);
				} else
					throw new DocumentoVacioException("El documento " + VersionaMasivo.NOMBRE_DOCUMENTO + " del gabinete " + t.getId_gabinete() + " del tramite " + t.getTituloAplicacion() + " esta vacio. No es neceario versionar.");
			}
			return d;
		} finally {
			CloseObject.closeObject(conn);
		}

	}

	private class DocumentoVacioException extends Exception {

		private static final long	serialVersionUID	= 5980921315556384842L;

		public DocumentoVacioException(String message) {
			super(message);
		}

	}

	private class Tramite {
		private String	cxp;
		private String	tituloAplicacion;
		private String	folio;
		private int		nfolio;
		private int		id_gabinete;

		public String getCxp() {
			return cxp;
		}

		public void setCxp(String cxp) {
			this.cxp = cxp;
		}

		public String getTituloAplicacion() {
			return tituloAplicacion;
		}

		public void setTituloAplicacion(String tituloAplicacion) {
			this.tituloAplicacion = tituloAplicacion;
		}

		public String getFolio() {
			return folio;
		}

		public void setFolio(String folio) {
			this.folio = folio;
		}

		public int getNfolio() {
			return nfolio;
		}

		public void setNfolio(int nfolio) {
			this.nfolio = nfolio;
		}

		public int getId_gabinete() {
			return id_gabinete;
		}

		public void setId_gabinete(int id_gabinete) {
			this.id_gabinete = id_gabinete;
		}

	}
}