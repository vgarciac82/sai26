package com.syc.egresos.core.impl;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.log4j.Logger;

import com.axtel.egresos.entities.EgresoExcedeUMA;
import com.axtel.egresos.exceptions.EgresoException;
import com.syc.cfdi.core.FacturaManager;
import com.syc.cfdi.db.CloseObject;
import com.syc.contable.core.PagosFederalizadoManager;
import com.syc.egresos.core.Amortizacion;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.core.EgresoRetencion;
import com.syc.egresos.core.EgresoRetencionFederalizadoManager;
import com.syc.ejercido.pagado.core.EgresosManager;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.utils.db.RSToTable;


public class EgresoPAGOFEDERALIZADOEncabezado extends EgresoEncabezado {

	private static final Logger	log					= Logger.getLogger( EgresoPAGOFEDERALIZADOEncabezado.class );
	private String				folioPagoFederalizado;
	private Date 				fPeriodode;
	private Date 				fPeriodoHasta;	
	private String				idContrato;
	private String				idPrograma;
	private String				cSubPrograma;
	StringBuilder				queryIns			= new StringBuilder();
	private String				referenciaBancaria;
	{
		queryIns.append( "INSERT INTO	tPAGOFEDERALIZADOEncabezado(" );
		queryIns.append( "			cFolioContratoObra," );
		queryIns.append( "			nFolioPAGOFEDERALIZADO," );
		queryIns.append( "			caNoContrarrecibo," );
		queryIns.append( "			rfc," );
		queryIns.append( "			nombre," );
		queryIns.append( "			cNoEstimacion," );
		queryIns.append( "			cIdTipoOperacion," );
		queryIns.append( "			fAplicacion," );
		queryIns.append( "			cNoFactura," );
		queryIns.append( "			mImporteBruto," );
		queryIns.append( "			mImporteSancion," );
		queryIns.append( "			mImporteDevolucion," );
		queryIns.append( "			mAmortizacionAnticipo," );
		queryIns.append( "			mImporteIVA," );
		queryIns.append( "			mImporteRetencion," );
		queryIns.append( "			mImportePenalizacion," );
		queryIns.append( "			mImporteNeto," );
		queryIns.append( "			nPorcAmortizacion," );
		queryIns.append( "			cIdEstadoEstimacion," );
		queryIns.append( "			lContrarreciboImpreso," );
		queryIns.append( "			cConcepto," );
		queryIns.append( "			lAmortizarAnticipoConEscalacion," );
		queryIns.append( "			nIdConcepto," );
		queryIns.append( "			fProgramadaPago," );
		queryIns.append( "			nTipoCambio," );
		queryIns.append( "			cIdUsuarioCaptura," );
		queryIns.append( "			cIdUsuarioImpresion," );
		queryIns.append( "			cIdUsuarioRevision," );
		queryIns.append( "			cIdUsuarioAprobacion," );
		queryIns.append( "			cIdUsuarioRechazo," );
		queryIns.append( "			ID_DESTINO_GASTO," );
		queryIns.append( "			ID_TIPO_OPERACION," );
		queryIns.append( "			ID_TIPO_MOVIMIENTO," );
		queryIns.append( "			ID_TIPO_FONDO," );
		queryIns.append( "			fperiodode," );
		queryIns.append( "			fperiodohasta," );
		queryIns.append( "			cUnidadResponsable," );
		queryIns.append( "			cRamo," );
		queryIns.append( "			cIdTipoDocumento," );
		queryIns.append( "			mImporteMasIva," );
		queryIns.append( "			mAmortizacion," );
		queryIns.append( "			mSaldoCedula," );
		queryIns.append( "			mAcumuladoxpagar," );
		queryIns.append( "			mSaldoAnticipo," );
		queryIns.append( "			cCentroContable," );
		queryIns.append( "			cMes," );
		queryIns.append( "			aEjercicioFiscal," );
		queryIns.append( "			mAmortizacionAcumulado," );
		queryIns.append( "			mImporteSancionAcumulado," );
		queryIns.append( "			mImporteDevolucionAcumulado," );
		queryIns.append( "			cTipoPoliza," );
		queryIns.append( "			NumPagoAMF," );
		queryIns.append( "			CTAB," );
		queryIns.append( "			mOtrosImpuestos," );
		queryIns.append( "			nidprograma," );
		queryIns.append( "			cSubPrograma," );
		queryIns.append( "			nNumEmpleadoElab," );
		queryIns.append( "			nNumEmpleadoVoBo," );
		queryIns.append( "			nNumEmpleadoAut," );
		queryIns.append( "			cEsFirmaElectronica, " );
		queryIns.append( "			cDescripcionPoliza," );
		queryIns.append( "			nIDEstatus" );
		queryIns.append( "		)" );
		queryIns.append( " VALUES	(" );
		queryIns.append( "		?," ); //cFolioContratoObra
		queryIns.append( "		?," ); //nFolioPAGOFEDERALIZADO
		queryIns.append( "		?," ); //caNoContrarrecibo
		queryIns.append( "		LTRIM(RTRIM(?))," ); //RFC
		queryIns.append( "		?," ); //NOMBRE
		queryIns.append( "		1," ); //cNoEstimacion
		queryIns.append( "		null," ); //cIdTipoOperacion
		queryIns.append( "		?," ); //fAplicacion
		queryIns.append( "		?," ); //cNoFactura
		queryIns.append( "		?," ); //mImporteBruto
		queryIns.append( "		?," ); //mImporteSancion
		queryIns.append( "		?," ); //mImporteDevolucion
		queryIns.append( "		?," ); //mAmortizacionAnticipo
		queryIns.append( "		?," ); //mImporteIVA
		queryIns.append( "		?," ); //mImporteRetencion
		queryIns.append( "		0," ); //mImportePenalizacion
		queryIns.append( "		?," ); //mImporteNeto
		queryIns.append( "		0," );
		queryIns.append( "		0," );
		queryIns.append( "		0," );
		queryIns.append( "		?," ); //cConcepto
		queryIns.append( "		0," );
		queryIns.append( "		?," ); //nIdConcepto
		queryIns.append( "		?," ); //fProgramadaPago
		queryIns.append( "		0," );
		queryIns.append( "		?," ); //cIdUsuarioCaptura
		queryIns.append( "		0," );
		queryIns.append( "		0," );
		queryIns.append( "		0," );
		queryIns.append( "		0," );
		queryIns.append( "		?," ); //ID_DESTINO_GASTO
		queryIns.append( "		?," ); //ID_TIPO_OPERACION
		queryIns.append( "		0," ); //ID_TIPO_MOVIMIENTO
		queryIns.append( "		0," ); //ID_TIPO_FONDO
		queryIns.append( "		?," ); //fperiodode
		queryIns.append( "		?," ); //fperiodohasta
		queryIns.append( "		?," ); //cUnidadResponsable
		queryIns.append( "		?," ); //cRamo
		queryIns.append( "		?," ); //cIdTipoDocumento
		queryIns.append( "		?," ); //mImporteMasIva
		queryIns.append( "		?," ); //mAmortizacion
		queryIns.append( "		?," ); //mSaldoCedula
		queryIns.append( "		?," ); //mAcumuladoxpagar
		queryIns.append( "		?," ); //mSaldoAnticipo
		queryIns.append( "		?," ); //cCentroContable
		queryIns.append( "		?," ); //cMes
		queryIns.append( "		?," ); //aEjercicioFiscal
		queryIns.append( "		?," ); //mAmortizacionAcumulado
		queryIns.append( "		?," ); //mImporteSancionAcumulado
		queryIns.append( "		?," ); //mImporteDevolucionAcumulado
		queryIns.append( "		?," ); //cTipoPoliza
		queryIns.append( "		'***'," ); //NumPagoAMF
		queryIns.append( "		?," ); //CTAB
		queryIns.append( "		?," ); //mOtrosImpuestos
		queryIns.append( "		?," ); //nidprograma
		queryIns.append( "		?," ); //cSubPrograma
		queryIns.append( "		?," ); //nNumEmpleadoElab
		queryIns.append( "		?," ); //nNumEmpleadoVoBo
		queryIns.append( "		?," ); //nNumEmpleadoAut
		queryIns.append( "		?," ); //cEsFirmaElectronica
		queryIns.append( "		?," ); //cDescripcionPoliza
		queryIns.append( "		?)" ); //nIDEstatus
	}

	public EgresoPAGOFEDERALIZADOEncabezado( ) {
		setTipoPago( "PAGOFEDERALIZADO" );
	}

	@Override
	public int actualizaMontosRetencion( Connection conn ) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append( " UPDATE	tpagoFederalizadoEncabezado"); 
		query.append( "   SET	mImporteNeto = tbl.nuevoImporteNeto, ");
		query.append( "		mImporteRetencion = tbl.retenciones"); 
		query.append( " FROM	( ");
		query.append( "		SELECT	pagoFederalizado.mImporteBruto + pagoFederalizado.mImporteIVA + pagoFederalizado.mOtrosImpuestos - SUM(isnull( retenciones.mImporteRetencion,0) ) AS nuevoImporteNeto,"); 
		query.append( "				SUM(isnull( retenciones.mImporteRetencion,0) ) AS retenciones ");
		query.append( "		  FROM	tPAGOFEDERALIZADOEncabezado pagoFederalizado WITH(NOLOCK) ");
		query.append( "				LEFT OUTER JOIN  tpagoRetencion retenciones WITH(NOLOCK)	ON "); 
		query.append( "		pagoFederalizado.nFoliopagoFederalizado = retenciones.nFoliopago ");
		query.append( " WHERE	pagoFederalizado.nFoliopagoFederalizado = ? ");
		query.append( "	GROUP BY pagoFederalizado.mImporteBruto, pagoFederalizado.mImporteIVA , pagoFederalizado.mOtrosImpuestos"); 
		query.append( "	) AS tbl ");
		query.append( " WHERE nFoliopagoFederalizado = ? ");
		
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setInt( 1, getFolioPago() );
			ps.setInt( 2, getFolioPago() );

			return ps.executeUpdate();

		} finally {
			CloseObject.closeObject( ps );
		}
	}

	@Override
	public int actualizaRetencion( Connection conn, int idTipoRetencion, BigDecimal valorRetencion ) throws Exception {
		throw new Exception( "Funcionalidad actualizaRetencion no implementada." );
	}

	@Override
	public int avanzaEstatus( Connection conn ) throws Exception {
		String query = "";
		query += "UPDATE	tPAGOFEDERALIZADOEncabezado ";
		query += "   SET	nIDEstatus = nIDEstatus + 1 ";
		query += " WHERE	nFolioPAGOFEDERALIZADO = ? ";

		PreparedStatement ps = null;
		int actualizados = 0;

		try {

			ps = conn.prepareStatement( query );
			ps.setInt( 1, getFolioPago() );

			actualizados = ps.executeUpdate();

			return actualizados;
		} finally {
			CloseObject.closeObject( ps );
		}
	}

	@Override
	public EgresoEncabezado cargaEncabezado( HttpServletRequest req ) throws Exception {
		return instanceFromRequest( req );
	}

	@Override
	public EgresoEncabezado cargaEncabezado( int folioEgreso ) throws Exception {
		super.init( getJniName() );
		EgresoPAGOFEDERALIZADOEncabezado epde = null;
		Connection conn = null;
		try {
			conn = getConnection();
			epde = PagosFederalizadoManager.cargaEncabezado( conn, folioEgreso );
			return epde;
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	@Override
	public int delete( Connection conn ) throws Exception {
		String query = "DELETE   FROM	tPAGOFEDERALIZADOEncabezado  WHERE	nfolioPagoFederalizado = ? " ;

		PreparedStatement ps = null;
		int afectados = 0;
		try {
			log.trace( "Iniciando eliminacion del pago directo: " + getFolioPago() );
			ps = conn.prepareStatement( query );
			ps.setInt( 1, getFolioPago() );

			afectados = ps.executeUpdate();
			log.trace( "Se eliminaron : " + afectados + " pagos con el folio: " + getFolioPago() );

			return afectados;
		} finally {
			CloseObject.closeObject( ps );
		}
	}

	@Override
	public int eliminaRetencion( Connection conn, int idTipoRetencion ) throws Exception {
		String query =  "DELETE FROM tPagoRetencion WHERE nFolioPago = ? AND cIdTipoRetencion = ? and cTipoDocumento =? ";

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement( query );
			ps.setString( 1, getfolioPagoFederalizado() );
			ps.setInt( 2, idTipoRetencion );
			ps.setString( 3, getTipoPago() );

			return ps.executeUpdate();
		} finally {
			CloseObject.closeObject( ps );
		}
	}

	@Override
	public int generaRetenciones( Connection conn ) throws Exception {
		List<EgresoRetencion> retenciones = EgresoRetencionFederalizadoManager.generaRetencionesEgresoFederalizado( conn, this );
		int insertados = 0;

		for ( EgresoRetencion retencion : retenciones ) {
			insertados += EgresoRetencionFederalizadoManager.insertaRetencionEgresoFederalizado( conn, this, retencion );
		}
		log.info( "Se insertaron: " + insertados + " retenciones para el pago directo folio " + getfolioPagoFederalizado() );
		return insertados;

	}

	@Override
	public Amortizacion getAmortizacion( Connection conn ) throws Exception {
		return EgresosManager.getAmortizacion( conn, this );
	}

	public String getfolioPagoFederalizado() {
		return folioPagoFederalizado;
	}

	public String getIdContrato() {
		return idContrato;
	}

	@Override
	public String getNombreAnexo() {
		return "AnexoFed.jasper";
	}

	@Override
	public String getNombreSolicitudPago() {
		return "PolizaPagoN.jasper";
	}

	@Override
	public String getPrefijoCR( Connection conn ) throws Exception {

		String cxpPrefijo = ConfiguraAplicativoManager.getSystemSetting( conn, "CXP_PREFIJO" );
		return cxpPrefijo;
	}

	public String getReferenciaBancaria() {
		return referenciaBancaria;
	}

	@Override
	public void rechazaPago( Connection conn, String motivoRechazo ) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append( "UPDATE t" ).append( getTipoPago() ).append( "Encabezado " );
		query.append( "   SET cDocumentoHAplicado = 'C'," );
		query.append( "       nIDEstatus = '-1' " );
		query.append( " WHERE nFolio" ).append( getTipoPago() ).append( " = " ).append( "?" );

		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setInt( 1, getFolioPago() );
			ps.executeUpdate();

			FacturaManager.eliminaFacturas( conn, getTipoPago(), String.valueOf( getFolioPago() ) );

			notificaRechazo( conn, motivoRechazo );

		} finally {
			CloseObject.closeObject( ps );
		}
	}

	@Override
	public Map<String, String> resumenConcepto( Connection conn ) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append( " SELECT	operacion.TIPO_OPERACION AS idTipoDocumentoLbl, ");
		query.append( "	   UPPER( destinoGasto.DESTINO_GASTO ) AS idDestinoGastoLbl, ");
		query.append( "	   federalizadoEncabezado.cConcepto AS conceptoLbl, ");
		query.append( "	   beneficiario.dRFC + ' - '+ beneficiario.dNombre + ISNULL(' ' + beneficiario.dApellidoPaterno, '') + ISNULL(' ' + beneficiario.dApellidoMaterno, '') AS rfcLbl, ");
		query.append( "	   dBanco + ' - ' + subCuentaBancaria AS CTABLbl, ");
		query.append( "	   FID.cprograma ProgramaLbl, ");
		query.append( "	   SUB.cdescsubprograma subProgramaLbl ");
		query.append( " FROM	tpagofederalizadoEncabezado federalizadoEncabezado  (NOLOCK) ");
		query.append( "	   INNER JOIN  CAT_TIPO_OPERACION operacion  (NOLOCK)  ");
		query.append( "	   ON federalizadoEncabezado.id_tipo_operacion = operacion.ID_TIPO_OPER ");
		query.append( "	   AND operacion.TO_TIPO_DOCTO = 'FEDERALIZADO' ");
		query.append( "	   INNER JOIN CAT_DESTINO_GASTO AS destinoGasto  (NOLOCK)  ");
		query.append( "	   ON  federalizadoEncabezado.ID_DESTINO_GASTO = destinoGasto.ID_DESTINO_GASTO ");
		query.append( "	   INNER JOIN tBeneficiario beneficiario  (NOLOCK)   ");
		query.append( "	   ON federalizadoEncabezado.RFC = beneficiario.dRFC ");
		query.append( "	   INNER JOIN tBeneficiarioCuentasBancarias beneficiarioCB  (NOLOCK)  ");
		query.append( "	   ON federalizadoEncabezado.RFC = beneficiarioCB.dRFC ");
		query.append( "	   AND federalizadoEncabezado.CTAB  = beneficiarioCB.subCuentaBancaria ");
		query.append( "	   LEFT JOIN tfideicomiso (NOLOCK) FID ");
		query.append( "	   ON FID.id = federalizadoEncabezado.nidprograma ");
		query.append( "	   LEFT JOIN tprogramafideicomiso (NOLOCK) SUB ");
		query.append( "	   ON SUB.csubprograma = federalizadoEncabezado.cSubPrograma  ");
		query.append( " WHERE	nFoliopagofederalizado = ?" );
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			ps = conn.prepareStatement( query.toString() );
			ps.setInt( 1, getFolioPago() );

			rs = ps.executeQuery();
			Map<String, String> result = RSToTable.rsToMapCaseSensitive( rs );
			return result;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	@Override
	public Map<String, String> resumenPago( Connection conn ) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append( " SELECT	pagoFederalizadoEncabezado.caNoContrarrecibo AS caNoContrarreciboResumen,");
		query.append( "	'PAGOFEDERALIZADO' AS tipoDocumentoResumen, ");
		query.append( "	CONVERT( VARCHAR(16), pagoFederalizadoEncabezado.fAplicacion, 103) AS fechaAplicacionResumen, ");
		query.append( "	pagoFederalizadoEncabezado.ID_DESTINO_GASTO + ' - ' + destinoGasto.DESTINO_GASTO AS destinoGastoResumen, ");
		query.append( "	pagoFederalizadoEncabezado.nFoliopagoFederalizado AS nFolioResumen, ");
		query.append( "	centroContable.cDescripcion AS centroContableResumen, ");
		query.append( "	ur.D_DESCRIPCION AS unidadResponsableResumen, ");
		query.append( "	tipoOperacion.TIPO_OPERACION AS tipopagoFederalizadoResumen, ");
		query.append( "	convert(varchar(32), CONVERT(MONEY, pagoFederalizadoEncabezado.mImporteNeto) , 103) AS importePagarResumen, ");
		query.append( "	dbo.fnCantidadLetra(pagoFederalizadoEncabezado.mImporteNeto) AS importeLetraResumen,");
		query.append( "	pagoFederalizadoEncabezado.RFC AS rfcResumen,");
		query.append( "	beneficiario.dNombre + ISNULL( ' ' + dApellidoPaterno , '' ) + ISNULL( ' ' + dApellidoMaterno, '') AS cNombreResumen,");
		query.append( "	pagoFederalizadoEncabezado.CTAB AS ctaBanResumen,");
		query.append( "	beneficiarioCB.dBanco AS ctaBanBancoResumen,");
		query.append( "	pagoFederalizadoEncabezado.cConcepto AS conceptoResumen,");
		query.append( "	CONVERT(VARCHAR(32), CONVERT(MONEY, pagoFederalizadoEncabezado.mImporteBruto), 103 ) AS mImporteBrutoResumen,");
		query.append( "	CONVERT(VARCHAR(32), CONVERT(MONEY, pagoFederalizadoEncabezado.mImportePenalizacion ), 103 ) AS importePenasResumen,");
		query.append( "	CONVERT(VARCHAR(32), CONVERT(MONEY, pagoFederalizadoEncabezado.mImporteIVA ), 103 ) AS importeIVAResumen,");
		query.append( "	CONVERT(VARCHAR(32), CONVERT(MONEY, pagoFederalizadoEncabezado.mOtrosImpuestos ), 103 ) AS importeOtrosImpResumen,");
		query.append( "	CONVERT(VARCHAR(32), CONVERT(MONEY, pagoFederalizadoEncabezado.mImporteRetencion ), 103 ) AS importeRetResumen,");
		query.append( "	CONVERT(VARCHAR(32), CONVERT(MONEY, pagoFederalizadoEncabezado.mImporteNeto ), 103 ) AS importeNetoResumen,");
		query.append( " cprograma programaResumen, cdescsubprograma  subprogramaResumen" );
		query.append( " FROM	tpagoFederalizadoEncabezado pagoFederalizadoEncabezado WITH(NOLOCK)");
		query.append( "	INNER join	tBeneficiario beneficiario WITH(NOLOCK)");
		query.append( "	ON 	pagoFederalizadoEncabezado.RFC = beneficiario.dRFC");
		query.append( "	INNER JOIN	CAT_DESTINO_GASTO destinoGasto WITH(NOLOCK) ");
		query.append( "	ON	pagoFederalizadoEncabezado.ID_DESTINO_GASTO = destinoGasto.ID_DESTINO_GASTO ");
		query.append( "	INNER JOIN 	tCatalogoCentroContable centroContable WITH(NOLOCK) ");
		query.append( "	ON	pagoFederalizadoEncabezado.cCentroContable = centroContable.cCentroContable");
		query.append( "	INNER JOIN tCatUnidadResponsable ur WITH(NOLOCK) ");
		query.append( "	ON 	pagoFederalizadoEncabezado.cUnidadResponsable = ur.cUnidadResponsable");
		query.append( "	LEFT OUTER JOIN CAT_TIPO_OPERACION tipoOperacion WITH(NOLOCK) ");
		query.append( "	ON	pagoFederalizadoEncabezado.ID_TIPO_OPERACION = tipoOperacion.ID_TIPO_OPER");
		query.append( "	AND tipoOperacion.TO_TIPO_DOCTO = 'FEDERALIZADO'");
		query.append( "	LEFT OUTER JOIN tBeneficiarioCuentasBancarias beneficiarioCB WITH(NOLOCK) ");
		query.append( "	ON	pagoFederalizadoEncabezado.CTAB = beneficiarioCB.subCuentaBancaria");
		query.append( "	AND pagoFederalizadoEncabezado.RFC = beneficiarioCB.dRFC");
		query.append( " LEFT JOIN  tfideicomiso prog (nolock) " );
		query.append( " ON pagoFederalizadoEncabezado.nidprograma = prog.id" );
		query.append( " LEFT JOIN tprogramafideicomiso subprog (nolock) " );
		query.append( " ON subprog.csubprograma = pagoFederalizadoEncabezado.cSubPrograma AND subprog.id = pagoFederalizadoEncabezado.nidprograma" );
		query.append( " WHERE	nFoliopagoFederalizado = ?");
		
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setInt( 1, getFolioPago() );
			rs = ps.executeQuery();
			Map<String, String> result = RSToTable.rsToMapCaseSensitive( rs );
			return result;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	@Override
	public List<Map<String, String>> resumenRetenciones( Connection conn ) throws Exception {
		List<Map<String, String>> detalle = new ArrayList<Map<String, String>>();
		StringBuilder sb = new StringBuilder();
		sb.append( "SELECT	cTipoRetencion, mImporteBruto, nPorcRetencion, importeRetencion  " );
		sb.append( "  FROM	vPagoRetencion " );
		sb.append( " WHERE	nFoliopago = ? and cTipoPago = ? " );

		PreparedStatement ps = null;
		ResultSet rs = null;
	
		try {
			ps = conn.prepareStatement( sb.toString() );
			ps.setInt( 1, getFolioPago() );
			ps.setString( 2, getTipoPago() );
			rs = ps.executeQuery();

			DateConverter converter = new DateConverter( null );
			Map<String, String> resultObj = RSToTable.rsToMapCaseSensitive( rs );
			converter.setPattern( "yyyy-MM-dd" );
			ConvertUtils.register( converter, Date.class );

			while ( resultObj != null ) {

				detalle.add( resultObj );
				resultObj = RSToTable.rsToMapCaseSensitive( rs );

			}

			return detalle;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	@Override
	public boolean retencionEliminable( Connection conn, int idRetencion ) throws Exception {
		throw new RuntimeException( "retencionEliminable No Implementado." );
	}

	@Override
	public int save( Connection conn ) throws EgresoException {

		PreparedStatement ps = null;
		try {
			int param = 1;
			ps = conn.prepareStatement( queryIns.toString() );

			ps.setString( param++, getIdContrato() );
			ps.setInt( param++, getFolioPago() );
			ps.setString( param++, getContrarecibo() );
			ps.setString( param++, getRfc() );
			ps.setString( param++, getNombre() );
			ps.setDate( param++, Util.toSQLDate( getFechaAplicacion() ) );
			ps.setInt( param++, getFolioPago()  );
			ps.setBigDecimal( param++, getImporteBruto() );
			ps.setBigDecimal( param++, getImporteSancion() );
			ps.setBigDecimal( param++, getImporteDevolucion() );
			ps.setBigDecimal( param++, getImporteAmortizacionAnticipo() );
			ps.setBigDecimal( param++, getImporteIVA() );
			ps.setBigDecimal( param++, getImporteRetencion() );
			ps.setBigDecimal( param++, getImporteMasIva() );
			ps.setString( param++, getConcepto() );
			ps.setString( param++, getIdConcepto() );
			ps.setDate( param++, Util.toSQLDate( getFechaProgramadaPago() ) );
			ps.setString( param++, getIdUsuarioCaptura() );
			ps.setString( param++, getIdDestinoGasto() );
			ps.setString( param++, getIdTipoOperacion() );
			ps.setDate( param++, Util.toSQLDate( getfPeriodode() ) );
			ps.setDate( param++, Util.toSQLDate( getfPeriodoHasta() ) );
			ps.setString( param++, getUnidadResponsable() );
			ps.setString( param++, getRamo() );
			ps.setString( param++, getIdTipoDocumento() );
			ps.setBigDecimal( param++, getImporteMasIva() );
			ps.setBigDecimal( param++, getImporteAmortizacion() );
			ps.setBigDecimal( param++, getImporteMasIva() );
			ps.setBigDecimal( param++, getImporteAcumuladoPagar() );
			ps.setBigDecimal( param++, getImporteSaldoAnticipo() );
			ps.setString( param++, getCentroContable() );
			ps.setString( param++, getMes() );
			ps.setString( param++, getEjercicioFiscal() );
			ps.setBigDecimal( param++, getImporteAmortizacionAcumulado() );
			ps.setBigDecimal( param++, getImporteSancionAcumulad() );
			ps.setBigDecimal( param++, getImporteDevolucionAcumulado() );
			ps.setString( param++, getTipoPoliza() );
			ps.setString( param++, getCTAB() );
			ps.setBigDecimal( param++, getOtrosImpuestos() );
			ps.setString( param++, getIdPrograma() );
			ps.setString( param++, getcSubPrograma() );
			ps.setInt( param++, getNumEmpleadoElab() );
			ps.setInt( param++, getNumEmpleadoVoBo() );
			ps.setInt( param++, getNumEmpleadoAut() );
			ps.setString( param++, String.valueOf( getEsFirmaElectronica() ) );
			ps.setString( param++, getConcepto() );
			ps.setInt( param++, getIdEstatus());

			log.debug( queryIns.toString() );
			log.debug( "Informacion:" + getIdContrato() + ", "+ getFolioPago()+ ", " + getContrarecibo()+ ", "+ getRfc()+ ", "+ getNombre()+ ", "+ Util.toSQLDate( getFechaAplicacion() ) + ", "+ getFolioPago() 
			 + ", "+ getImporteBruto() + ", "+ getImporteSancion() + ", "+  getImporteDevolucion() + ", "+ getImporteAmortizacionAnticipo() + ", "+ getImporteIVA() + ", "+  getImporteRetencion() + ", "+  getImporteMasIva() + ", "+  getConcepto() 
			 + ", "+  getIdConcepto() + ", "+ Util.toSQLDate( getFechaProgramadaPago() ) + ", "+   getIdUsuarioCaptura()+ ", "+ getIdDestinoGasto() + ", "+ getIdTipoOperacion()+ ", " + Util.toSQLDate( getfPeriodode() )
			 + ", " + Util.toSQLDate( getfPeriodoHasta() )+ ", " + getUnidadResponsable() +", "+getRamo() +", "+ getIdTipoDocumento() +", "+ getImporteMasIva() +", "+ getImporteAmortizacion() +", "+ getImporteAcumuladoPagar() 
			 +", "+ getImporteSaldoAnticipo() +", "+ getCentroContable() +", "+ getMes() +", "+ getEjercicioFiscal() +", "+ getImporteAmortizacionAcumulado() +", "+ getImporteSancionAcumulad() +", "+ getImporteDevolucionAcumulado() 
			 +", "+ getTipoPoliza() +", "+ getCTAB() +", "+ getOtrosImpuestos() +", "+ getIdPrograma() +", "+ getcSubPrograma() +", "+ getNumEmpleadoElab() +", "+ getNumEmpleadoVoBo() +", "+ getNumEmpleadoAut() +", "+ getEsFirmaElectronica() +", "+ getConcepto()+", "+ getIdEstatus());
			
			return ps.executeUpdate();
		} catch ( SQLException e ) {
			throw new EgresoException( e );
		} finally {
			CloseObject.closeObject( ps );
		}
	}

	private EgresoEncabezado instanceFromRequest( HttpServletRequest request ) throws Exception {

		EgresoEncabezado encabezado = new EgresoPAGOFEDERALIZADOEncabezado();
		encabezado = super.readFromRequest( request, encabezado );

		( ( EgresoPAGOFEDERALIZADOEncabezado ) encabezado ).setFolioPago( Integer.parseInt( request.getParameter( "nFolioPago" ) ) );
		( ( EgresoPAGOFEDERALIZADOEncabezado ) encabezado ).setIdContrato( request.getParameter( "cIDContratoFed" ) );
		( ( EgresoPAGOFEDERALIZADOEncabezado ) encabezado ).setRfc( request.getParameter( "cIDRFC" ) );
		( ( EgresoPAGOFEDERALIZADOEncabezado ) encabezado ).setNombre( request.getParameter( "cnombre" ) );
		( ( EgresoPAGOFEDERALIZADOEncabezado ) encabezado ).setFechaAplicacion( Util.stringToDate( request.getParameter( "fechaAplicacion" ), "dd/MM/yyyy" ) );
		( ( EgresoPAGOFEDERALIZADOEncabezado ) encabezado ).setfPeriodode( Util.stringToDate( request.getParameter( "fDesde" ), "dd/MM/yyyy" ) );
		( ( EgresoPAGOFEDERALIZADOEncabezado ) encabezado ).setfPeriodoHasta( Util.stringToDate( request.getParameter( "fHasta" ), "dd/MM/yyyy" ) );
		( ( EgresoPAGOFEDERALIZADOEncabezado ) encabezado ).setcSubPrograma(  request.getParameter( "nIdSubPrograma" ) );
		( ( EgresoPAGOFEDERALIZADOEncabezado ) encabezado ).setIdPrograma( request.getParameter( "nIDPrograma" ) );
		( ( EgresoPAGOFEDERALIZADOEncabezado ) encabezado ).setCTAB( request.getParameter( "CTAB" ) );
		( ( EgresoPAGOFEDERALIZADOEncabezado ) encabezado ).setIdTipoOperacion( request.getParameter( "TIPO_OPERACION" ) );
		( ( EgresoPAGOFEDERALIZADOEncabezado ) encabezado ).setIdDestinoGasto( request.getParameter( "DESTINO_GASTO" ) );
		( ( EgresoPAGOFEDERALIZADOEncabezado ) encabezado ).setImporteMasIva( new BigDecimal(request.getParameter( "totalFactura") ));
		( ( EgresoPAGOFEDERALIZADOEncabezado ) encabezado ).setImporteBruto( new BigDecimal(request.getParameter( "mTotalFacturaV") )); 
		( ( EgresoPAGOFEDERALIZADOEncabezado ) encabezado ).setImporteIVA( new BigDecimal(request.getParameter( "mImporteIVA") ));

		Date fechaAp = new Date(); 
		fechaAp = ( ( EgresoPAGOFEDERALIZADOEncabezado ) encabezado ).getFechaAplicacion();
		Calendar cal = Calendar.getInstance();
		cal.setTime(fechaAp);
		( ( EgresoPAGOFEDERALIZADOEncabezado ) encabezado ).setMes( String.valueOf( cal.get(Calendar.MONTH) ));
		
		return encabezado;
	}

	public void setfolioPagoFederalizado( String folioPagoFederalizado ) {
		this.folioPagoFederalizado = folioPagoFederalizado;
	}

	public void setIdContrato( String idContrato ) {
		this.idContrato = idContrato;
	}

	public void setReferenciaBancaria( String referenciaBancaria ) {
		this.referenciaBancaria = referenciaBancaria;
	}

	public Date getfPeriodoHasta() {
		return fPeriodoHasta;
	}

	public void setfPeriodoHasta( Date fPeriodoHasta ) {
		this.fPeriodoHasta = fPeriodoHasta;
	}

	public Date getfPeriodode() {
		return fPeriodode;
	}

	public void setfPeriodode( Date fPeriodode ) {
		this.fPeriodode = fPeriodode;
	}

	public String getIdPrograma() {
		return idPrograma;
	}

	public void setIdPrograma( String idPrograma ) {
		this.idPrograma = idPrograma;
	}

	public String getcSubPrograma() {
		return cSubPrograma;
	}

	public void setcSubPrograma( String cSubPrograma ) {
		this.cSubPrograma = cSubPrograma;
	}
	
	@Override
	public List<EgresoExcedeUMA> validaTopeUMASUnidad( Connection conn, String rfc2 ) {
		return new ArrayList<EgresoExcedeUMA>();
	}
}
