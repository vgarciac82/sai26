package com.syc.contable.servlet;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.syc.contable.core.OperacionesAjenasIntDetalle;
import com.syc.contable.core.OperacionesAjenasIntEncabezado;
import com.syc.contable.core.OperacionesAjenasIntManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class OperacionesAjenasIntBusinessLogic extends DataSourceManager {

	private static final Logger					log	= Logger.getLogger(OperacionesAjenasIntBusinessLogic.class);
	private OperacionesAjenasIntEncabezado		encabezado;
	private List<OperacionesAjenasIntDetalle>	detalle;

	public OperacionesAjenasIntBusinessLogic(String jniName) {
		super.init(jniName);
	}

	public static OperacionesAjenasIntEncabezado instanceHeaderFromRequest(HttpServletRequest req) throws Exception {
		OperacionesAjenasIntEncabezado encabezado = new OperacionesAjenasIntEncabezado();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		encabezado.setaEjercicioFiscal(req.getParameter("cEjercicio"));
		encabezado.setCaNoContrarrecibo(req.getParameter("caNoContrarreciboInt"));
		encabezado.setcBeneficiario(req.getParameter("cBeneficiario"));
		encabezado.setcCentroContable(req.getParameter("cCentroContable"));
		encabezado.setcIDRFC(null);
		//encabezado.setcIdTipoDocumento(null);
		encabezado.setcRamo(req.getParameter("cRamo"));
		encabezado.setcUnidadResponsable(req.getParameter("cUnidadResponsable"));
		encabezado.setfCancelacion(null);
		encabezado.setfCaptura(formatter.parse(req.getParameter("fCaptura")));
		encabezado.setfDesde(formatter.parse(req.getParameter("fBusquedaDe")));
		encabezado.setFHasta(formatter.parse(req.getParameter("fBusquedaHasta")));
		encabezado.setIdDestinoGasto(null);
		encabezado.setmImportes(Double.parseDouble(req.getParameter("mImporteconAjuste")));
		encabezado.setnEnviadoSICOP("1");
		encabezado.setnFolioOperAjenasInt(Integer.parseInt(req.getParameter("nFolioOperAjenasInt"),10));
		encabezado.setU_LOGIN(req.getParameter("U_LOGIN"));
		encabezado.setcIDRFC(req.getParameter("cIDRFC")); 

		return encabezado;
	}

	public static List<OperacionesAjenasIntDetalle> instanceDetailFromRequest(HttpServletRequest req) throws Exception {
		List<OperacionesAjenasIntDetalle> detalle = new ArrayList<OperacionesAjenasIntDetalle>();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String[] eps = req.getParameterValues("Ep");
		String[] caNoContrarreciboOA = req.getParameterValues("caNoContrarreciboOA");
		String[] nFolioOperAjenas = req.getParameterValues("nFolioOperAjenas");
		String[] caNoContrarrecibo = req.getParameterValues("caNoContrarrecibo");
		String[] cTipoDoc = req.getParameterValues("cTipoDoc");
		String[] mTotal = req.getParameterValues("mTotal");
		String[] nDocRenglon = req.getParameterValues("nDocRenglon");
		String[] cIDRFC = req.getParameterValues("cIDRFC");
		String[] nFolioDoc = req.getParameterValues("nFolioDoc");
		String[] nDocRenglonInt = req.getParameterValues("nDocRenglonInt");
		String[] mAjuste = req.getParameterValues("mAjuste"); 
		String[] cMes = req.getParameterValues("cMes"); 

		for (int i = 0; i < eps.length; i++) {

			OperacionesAjenasIntDetalle tmpDet = new OperacionesAjenasIntDetalle();

			tmpDet.setaEjercicioFiscal(req.getParameter("cEjercicio"));
			tmpDet.setCaNoContrarrecibo(caNoContrarrecibo[i]);
			tmpDet.setCaNoContrarreciboOA(caNoContrarreciboOA[i]);
			tmpDet.setnFolioDoc(Integer.parseInt(nFolioDoc[i],10));
			tmpDet.setcCentroContable(req.getParameter("cCentroContable"));
			tmpDet.setcIDRFC(cIDRFC[i]);
			tmpDet.setcRamo(req.getParameter("cRamo"));
			tmpDet.setcTipoDoc(cTipoDoc[i]);
			tmpDet.setcUnidadResponsable(req.getParameter("cUnidadResponsable"));
			tmpDet.setEp(eps[i]);
			tmpDet.setfCaptura(formatter.parse(req.getParameter("fCaptura")));
			tmpDet.setmTotal(Double.parseDouble(mTotal[i]));
			tmpDet.setnDocRenglon(Integer.parseInt(nDocRenglon[i],10));
			tmpDet.setnDocRenglonInt(Integer.parseInt(nDocRenglonInt[i],10));
			tmpDet.setnFolioDoc(Integer.parseInt(nFolioDoc[i],10));
			tmpDet.setnFolioOperAjenas(Integer.parseInt(nFolioOperAjenas[i],10));
			tmpDet.setnFolioOperAjenasInt(Integer.parseInt(req.getParameter("nFolioOperAjenasInt"),10));
			tmpDet.setmAjuste(Double.parseDouble(mAjuste[i]));
			tmpDet.setcMes(cMes[i]);

			detalle.add(tmpDet);

		}

		return detalle;
	}

	public void setEncabezado(OperacionesAjenasIntEncabezado encabezado) {
		this.encabezado = encabezado;
	}

	public OperacionesAjenasIntEncabezado getEncabezado() {
		return encabezado;
	}

	public List<OperacionesAjenasIntDetalle> getDetalle() {
		return detalle;
	}

	public void setDetalle(List<OperacionesAjenasIntDetalle> detalle) {
		this.detalle = detalle;
	}

	public int insert() throws Exception {
		Connection conn = null;
		int insertados = 0;
		try {
			conn = getConnection();
			insertados = OperacionesAjenasIntManager.insertEncabezado(conn, getEncabezado());
			insertados += OperacionesAjenasIntManager.insertDetalle(conn, getDetalle());
			conn.commit();
			return insertados;
		} catch (Exception e) {
			log.error(e, e);
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.warn("No se pudo realizar rollback. Causa: " + e2, e2);
				}
			throw e;
		} finally {
			CloseObject.closeObject(conn, false);
		}
	}
}
