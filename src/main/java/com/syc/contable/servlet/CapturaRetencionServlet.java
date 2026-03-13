package com.syc.contable.servlet;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.contable.RetencionBusinessLogic;
import com.syc.contable.core.RetencionDetalle;
import com.syc.contable.core.RetencionEncabezado;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;


public class CapturaRetencionServlet extends HttpServlet {

	private static final long	serialVersionUID	= 1L;
	private static Logger		log					= Logger.getLogger( RetencionBusinessLogic.class );

	public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

		request.setCharacterEncoding( "UTF-8" );
		HttpSession session = request.getSession( false );

		if ( session == null ) {
			response.sendRedirect( "../index.jsp" );
			return;
		}
		
		RetencionEncabezado ret = new RetencionEncabezado();
		RetencionBusinessLogic recPresBL = new RetencionBusinessLogic( GestionInterface.ATT_CONEXION );
		String DATE_FORMAT = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat( DATE_FORMAT );
		Calendar c1 = Calendar.getInstance();
		String today = sdf.format( c1.getTime() );
		boolean val = false;

		try {
			String folio = request.getParameter( "folio" );
			java.sql.Date fExp;
			java.sql.Date fApl;
			try {
				fExp = java.sql.Date.valueOf( request.getParameter( "fExp" ) );
				fApl = java.sql.Date.valueOf( request.getParameter( "fApl" ) );
			} catch ( IllegalArgumentException e ) {
				fExp = java.sql.Date.valueOf( today );
				fApl = java.sql.Date.valueOf( today );
			}

			String concepto = request.getParameter( "cDescripcionPolizaH" ).replaceAll( "[\n\r]", "" );
			String cCentroContable = request.getParameter( "cCentroContable_C" );
			String mImporteRetencion = request.getParameter( "mImporteRetencion" );
			int folioPoliza = 0;
			int folioR = new Integer( folio.substring( folio.lastIndexOf( '-' ) + 1 ) ).intValue();
			String cxp = StringUtils.trimToEmpty( request.getParameter( "cxp_c" ) );

			ret.setnFolioRetencion( folioR );
			ret.setnIdCaso( new Integer( request.getParameter( "ID_Caso_C" ) ).intValue() );
			ret.setcUnidadResponsable( request.getParameter( "cUnidad_C" ) );
			ret.setfCarga( fExp );
			ret.setfAplicacion( fApl );
			ret.setcRamo( request.getParameter( "cRamo_C" ) );
			ret.setnOrigenPPTO( new Integer( request.getParameter( "nOrigenPPTO" ) ).intValue() );
			ret.setaEjercicioFiscal( request.getParameter( "cEjercicio_C" ) );
			ret.setcCentroContable( cCentroContable );
			ret.setcIdRFC( request.getParameter( "idrfc" ) );
			ret.setCTAB( request.getParameter( "CTAB" ) );
			ret.setNOMBRE( request.getParameter( "nombre" ) );
			ret.setcConcepto( concepto );
			ret.setTipoPago( request.getParameter( "tipoPago" ) );
			ret.setCaNoContrarrecibo( cxp );
			ret.setcIdUsuarioCaptura( request.getParameter( "u_login_C" ) );
			ret.setnFolioPoliza( folioPoliza );
			ret.setcTipoPoliza( "EG" );
			ret.setnCompromisoSICOP( request.getParameter( "compromisoSICOP" ) );
			ret.setcUnidadResponsableContable( "RHQ" );
			ret.setEsIP( request.getParameter( "cEsIP" ) );
			ret.setmImporteRetencion( new Double( mImporteRetencion ).doubleValue() );
			ret.setnFolioPago( new Integer (request.getParameter( "nFolioPago" ) ));
			
			String detalle[] = request.getParameter( "info" ).split( "!" );

			String evento = recPresBL.actualizarEvento( cxp );
			ArrayList<RetencionDetalle> retenciones = new ArrayList<RetencionDetalle>();
			
			
			for ( int i = 0; i < detalle.length; i += 17) {
				RetencionDetalle rd = new RetencionDetalle();
				rd.setnFolioRetencion( folioR );
				rd.setnDocRenglon( Integer.parseInt( detalle[i].trim() ) );
				rd.setcMes( ( detalle[i + 1].trim() ) );
				rd.setEP( detalle[i + 2].trim() );
				rd.setmImporte( Double.parseDouble( detalle[i + 3].trim() ) );
				rd.setM2Millar( Double.parseDouble( detalle[i + 4].trim() ) );
				rd.setmObra5( Double.parseDouble( detalle[i + 5].trim() ) );
				rd.setmImporteFlete4( Double.parseDouble( detalle[i + 6].trim() ) );
				rd.setmISRHonorarios( Double.parseDouble( detalle[i + 7].trim() ) );
				rd.setmISRArrenda( Double.parseDouble( detalle[i + 8].trim() ) );
				rd.setmImporteIvaHonorarios( Double.parseDouble( detalle[i + 9].trim() ) );
				rd.setmImporteIvaArrenda( Double.parseDouble( detalle[i + 10].trim() ) );
				rd.setmRetImpuestoCedular( Double.parseDouble( detalle[i + 11].trim() ) );
				rd.setmImporteISRLaudos( Double.parseDouble( detalle[i + 12].trim() ) );
				rd.setmISROtros( Double.parseDouble( detalle[i + 13].trim() ) );
				rd.setmImporteIva6( Double.parseDouble( detalle[i + 14].trim() ) );
				rd.setcEvento( evento );
				rd.setcUnidadResponsable( request.getParameter( "cUnidad_C" ) );
				rd.setRfc( detalle[i + 15].trim() );
				rd.setObgt( rd.getEP().substring( 31, 36 ) );
				rd.setmPasivoDiferido( Double.parseDouble( detalle[i + 3].trim() ) );
				rd.setcPasivo( request.getParameter( "cPasivo_C" ) );
				rd.setcCentroContable( Integer.parseInt( cCentroContable ) );
				rd.setcEjercicio( request.getParameter( "cEjercicio_C" ) );
				retenciones.add( rd );
			}

			// RetencionEncabezado res =
			// recPresBL.getRetencionEncabezado(folioR);
			boolean retorno = false;
			log.debug( "Se instancia el encabezado y el detalle de la retencion" );
			if ( ret != null ) {

				 retorno = recPresBL.insertarRetencion( ret, retenciones );
			}

			if (retorno)
				ResponseSender.sendClientSimpleMessage( response, true, "Correcto" );
			else
				throw new Exception("Ocurrio un error al guardar la solicitud, revise el presupuesto o consulte al administrador");

		} catch ( Exception ex ) {
			
			ResponseSender.sendClientSimpleMessage( response, false, "Ocurrio un error:" + ex);
		}
	}
}
