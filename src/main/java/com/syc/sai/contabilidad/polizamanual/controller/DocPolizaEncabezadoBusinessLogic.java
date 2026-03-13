package com.syc.sai.contabilidad.polizamanual.controller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.polizamanual.DocPolizaEncabezado;
import com.syc.sai.contabilidad.polizamanual.DocPolizaEncabezadoEngineException;
import com.syc.sai.contabilidad.polizamanual.model.DocPolizaEncabezadoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class DocPolizaEncabezadoBusinessLogic extends DataSourceManager {

	Logger	log	= Logger.getLogger(DocPolizaEncabezadoBusinessLogic.class);

	public DocPolizaEncabezadoBusinessLogic(){
		super.init( GestionInterface.ATT_CONEXION );
	}
	public DocPolizaEncabezadoBusinessLogic(String jniName ){
		super.init( jniName );
	}
	
	public List<DocPolizaEncabezado> readDocPolizaEncabezadoBy(String restrictions) throws DocPolizaEncabezadoEngineException {
		Connection conn = null;
		try {
			conn = getConnection();
			return DocPolizaEncabezadoManager.readDocPolizaEncabezadoBy(conn, restrictions);
		} catch (Exception e) {
			throw new DocPolizaEncabezadoEngineException(e);
		} finally {
			
		}
	}
		
	public int saveOrUpdateDocPolizaEncabezado(HttpServletRequest req) throws DocPolizaEncabezadoEngineException {
		Connection conn = null;
		try {
			conn = getConnection();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			DocPolizaEncabezado docPolizaEncabezado = new DocPolizaEncabezado();
			docPolizaEncabezado.setFcarga(req.getParameter("fcarga"));
			docPolizaEncabezado.setFaplicacion(req.getParameter("faplicacion"));
			docPolizaEncabezado.setCramo(req.getParameter("cramo"));
			docPolizaEncabezado.setCunidadResponsable(req.getParameter("cunidadResponsable"));
			docPolizaEncabezado.setCdocumentoHaplicado(req.getParameter("cdocumentoHaplicado"));
			docPolizaEncabezado.setNfolioPoliza(new Integer(req.getParameter("nfolioPoliza")));
			docPolizaEncabezado.setNmes(new Short(req.getParameter("nmes")));
			docPolizaEncabezado.setCrevisado(req.getParameter("crevisado"));
			docPolizaEncabezado.setCunidadResponsableContable(req.getParameter("cunidadResponsableContable"));
			docPolizaEncabezado.setNfolioPolizaCancelacion(new Integer(req.getParameter("nfolioPolizaCancelacion")));
			docPolizaEncabezado.setFcancelacion(req.getParameter("fcancelacion"));
			docPolizaEncabezado.setCdescripcionPoliza(req.getParameter("cdescripcionPoliza"));
			docPolizaEncabezado.setCconcepto(req.getParameter("cconcepto"));
			docPolizaEncabezado.setCidUsuarioCaptura(req.getParameter("cidUsuarioCaptura"));
			docPolizaEncabezado.setCidUsuarioRevision(req.getParameter("cidUsuarioRevision"));
			docPolizaEncabezado.setCidUsuarioAprobacion(req.getParameter("cidUsuarioAprobacion"));
			docPolizaEncabezado.setCidOrigen(req.getParameter("cidOrigen"));
			docPolizaEncabezado.setMtotalCargos(new Double(req.getParameter("mtotalCargos")));
			docPolizaEncabezado.setMtotalAbonos(new Double(req.getParameter("mtotalAbonos")));
			docPolizaEncabezado.setCtipoDocumento(req.getParameter("ctipoDocumento"));
			docPolizaEncabezado.setCcomentarios(req.getParameter("ccomentarios"));
			docPolizaEncabezado.setNcambio(new Integer(req.getParameter("ncambio")));
			docPolizaEncabezado.setNidCasoOrigen(new BigDecimal(req.getParameter("nidCasoOrigen")));
			docPolizaEncabezado.setPeriodo13(req.getParameter("periodo13"));
			docPolizaEncabezado.setAdefas(req.getParameter("adefas"));
			docPolizaEncabezado.setNtipoAjuste(new Integer(req.getParameter("ntipoAjuste")));
			docPolizaEncabezado.setAejercicioFiscal(req.getParameter("aejercicioFiscal"));
			docPolizaEncabezado.setCcentroContable(req.getParameter("ccentroContable"));
			docPolizaEncabezado.setNfolioDocPoliza(new Integer(req.getParameter("nfolioDocPoliza")));
			docPolizaEncabezado.setCtipoPoliza(req.getParameter("ctipoPoliza"));
			docPolizaEncabezado.setnFormatoPoliza(new Integer(req.getParameter("nFormatoPoliza")));
			req.getSession().setAttribute("docPolizaEncabezado",docPolizaEncabezado);			
			return DocPolizaEncabezadoManager.saveOrUpdateDocPolizaEncabezado(conn, docPolizaEncabezado);
		} catch (Exception e) {
			throw new DocPolizaEncabezadoEngineException(e);
		} finally {
			
		}

	}
	
	public void imprimePoliza(HttpServletRequest req, HttpServletResponse resp, String tipoReporte, String tipoReporte2, String ruta, int nFolioPoliza, String cCentroContable) throws Exception {
		Connection conn = null;
		try {			
			conn = getConnection();
			
			Map<String, Object> parms = new LinkedHashMap<String, Object>();
			
			parms.put("poliza",nFolioPoliza);			
			parms.put("cc", cCentroContable);
			parms.put("SUBREPORT_DIR", ruta);
						
			DocPolizaEncabezadoManager.ImprimirPoliza(conn, resp, tipoReporte, ruta, parms);			
						
		}finally{
			CloseObject.closeObject(conn, false);
			}
	}
}

