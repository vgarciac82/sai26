package com.syc.contable.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import com.syc.egresos.core.EgresoDetalle;
import com.syc.egresos.core.impl.EgresoPAGOFEDERALIZADODetalle;
import com.syc.egresos.core.impl.EgresoPAGOFEDERALIZADOEncabezado;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.contabilidad.utils.db.RSToTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PagosFederalizadoManager {

    public PagosFederalizadoManager() {
        super();
    }

    private static final Logger log = LoggerFactory.getLogger(PagosFederalizadoManager.class);

    public static EgresoPAGOFEDERALIZADOEncabezado cargaEncabezado(Connection conn, int folioPago) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	aEjercicioFiscal AS ejercicioFiscal, ");
        query.append("		ALM AS alm, ");
        query.append("		caNoContrarrecibo AS contrarecibo, ");
        query.append("		capitulo AS capitulo, ");
        query.append("		cCentroContable AS centroContable, ");
        query.append("		cConcepto AS concepto, ");
        query.append("		cDescripcionPoliza AS descripcionPoliza, ");
        query.append("		cDocumentoHaplicado AS documentoAplicado, ");
        query.append("		cEsFirmaElectronica AS esFirmaElectronica, ");
        query.append("		fperiodode AS fperiodode, ");
        query.append("		fperiodohasta AS fperiodohasta, ");
        query.append("		cFolioContratoObra AS idContrato, ");
        query.append("		cIdEstadoEstimacion AS idEstadoEstimacion, ");
        query.append("		cIdTipoDocumento AS idTipoDocumento, ");
        query.append("		cIdTipoOperacion AS idTipoOperacion, ");
        query.append("		cIdUsuarioAprobacion AS idUsuarioAprobacion, ");
        query.append("		cIdUsuarioCaptura AS idUsuarioCaptura, ");
        query.append("		cIdUsuarioImpresion AS idUsuarioImpresion, ");
        query.append("		cIdUsuarioRechazo AS idUsuarioRechazo, ");
        query.append("		cIdUsuarioRevision AS idUsuarioRevision, ");
        query.append("		cMes AS mes, ");
        query.append("		cNoEstimacion AS noEstimacion, ");
        query.append("		cNoFactura AS noFactura, ");
        query.append("		cOficioDiferenciaCambiaria AS oficioDiferenciaCambiaria, ");
        query.append("		nidprograma AS idPrograma, ");
        query.append("		cSubPrograma AS cSubPrograma, ");
        query.append("		cRamo AS ramo, ");
        query.append("		cReferenciaPRODDER AS referenciaPRODDER, ");
        query.append("		CTAB AS CTAB, ");
        query.append("		cTipoPoliza AS tipoPoliza, ");
        query.append("		cUnidadResponsable AS unidadResponsable, ");
        query.append("		cUnidadResponsableContable AS unidadResponsableContable, ");
        query.append("		CONVERT( DATE, fAplicacion, 103) AS fechaAplicacion, ");
        query.append("		fCancelacion AS fechaCancelacion, ");
        query.append("		fProgramadaPago AS fechaProgramadaPago, ");
        query.append("		ID_DESTINO_GASTO AS idDestinoGasto, ");
        query.append("		ID_TIPO_FONDO AS idTipoFondo, ");
        query.append("		ID_TIPO_MOVIMIENTO AS IdTipoMovimiento, ");
        query.append("		ID_TIPO_OPERACION AS idTipoOperacion, ");
        query.append("		lAmortizarAnticipoConEscalacion AS amortizarAnticipoConEscalacion, ");
        query.append("		lAplicaImpuestoCedular AS aplicaImpuestoCedular, ");
        query.append("		lContrarreciboImpreso AS contrarreciboImpreso, ");
        query.append("		mAcumuladoxpagar AS importeAcumuladoPagar, ");
        query.append("		mAmortizacion AS importeAmortizacion, ");
        query.append("		mAmortizacionAcumulado AS importeAmortizacionAcumulado, ");
        query.append("		mAmortizacionAnticipo AS importeAmortizacionAnticipo, ");
        query.append("		mImporteBruto AS importeBruto, ");
        query.append("		mImporteDevolucion AS importeDevolucion, ");
        query.append("		mImporteDevolucionAcumulado AS importeDevolucionAcumulado, ");
        query.append("		mImporteIVA AS importeIVA, ");
        query.append("		mImporteMasIva AS importeMasIva, ");
        query.append("		mImporteNeto AS importeNeto, ");
        query.append("		mImportePenalizacion AS importePenalizacion, ");
        query.append("		mImporteRetencion AS importeRetencion, ");
        query.append("		mImporteSancion AS importeSancion, ");
        query.append("		mImporteSancionAcumulado AS importeSancionAcumulad, ");
        query.append("		mOtrosImpuestos AS importeOtrosImpuestos, ");
        query.append("		mSaldoAnticipo AS importeSaldoAnticipo, ");
        query.append("		mSaldoCedula AS importeSaldoCedula, ");
        query.append("		nEnviadoSICOP AS enviadoSICOP, ");
        query.append("		nFolioPAGOFEDERALIZADO AS folioPagoFederalizado, ");
        query.append("		nFolioPoliza AS folioPoliza, ");
        query.append("		nFolioPolizaCancelacion AS folioPolizaCancelacion, ");
        query.append("		nIdConcepto AS idConcepto, ");
        query.append("		nNumEmpleadoAut AS numEmpleadoAut, ");
        query.append("		nNumEmpleadoElab AS numEmpleadoElab, ");
        query.append("		nNumEmpleadoVoBo AS numEmpleadoVoBo, ");
        query.append("		NOMBRE AS Nombre, ");
        query.append("		nPorcAmortizacion AS porcAmortizacion, ");
        query.append("		ISNULL( nPorcImpuestoCedular, 0) AS porcImpuestoCedular, ");
        query.append("		nTipoCambio AS tipoCambio, ");
        query.append("		NumPagoAMF AS numPagoAMF, ");
        query.append("		RFC AS rfc, ");
        query.append("		U_LOGIN AS login, nFolioPAGOFEDERALIZADO folioPago");
        query.append("  FROM	tPAGOFEDERALIZADOEncabezado WITH(NOLOCK)");
        query.append(" WHERE	nFolioPAGOFEDERALIZADO = ?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String query2 = query.toString();
            ps = conn.prepareStatement(query2);
            ps.setInt(1, folioPago);
            rs = ps.executeQuery();
            Map<String, String> resultObj = RSToTable.rsToMapCaseSensitive(rs);
            EgresoPAGOFEDERALIZADOEncabezado pdEncabezado = new EgresoPAGOFEDERALIZADOEncabezado();
            DateConverter converter = new DateConverter(null);
            converter.setPattern("yyyy-MM-dd");
            ConvertUtils.register(converter, Date.class);
            BeanUtils.populate(pdEncabezado, resultObj);
            return pdEncabezado;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static List<EgresoDetalle> cargaDetalle(Connection conn, int folioPago) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append("SELECT	ALM AS alm, ");
        query.append("		altaAlmacen AS altaAlmacen, ");
        query.append("		cCentroContable AS centroContable, ");
        query.append("		cEjercicio AS ejercicioFiscal, ");
        query.append("		cEvento AS evento, ");
        query.append("		cIdCuentaContable AS idCuentaContable, ");
        query.append("		cMes AS mesCalendario, ");
        query.append("		EP AS ep, ");
        query.append("		ID_TIPO_CONCEPTO AS idTipoConcepto, ");
        query.append("		ID_TIPO_MOVIMIENTO AS idTipoMovimiento, ");
        query.append("		m23IVA AS importe23IVA, ");
        query.append("		m2Millar AS importe2Millar, ");
        query.append("		mCNIC AS importeCNIC, ");
        query.append("		mComprometido AS importeComprometido, ");
        query.append("		mDevolucion AS importeDevolucion, ");
        query.append("		mIMDT AS importeIMDT, ");
        query.append("		mImporte AS importe, ");
        query.append("		mImporteAmortiza AS importeAmortiza, ");
        query.append("		mImporteBruto AS importeBruto, ");
        query.append("		mImporteFlete23 AS importeFlete23, ");
        query.append("		mImporteFlete4 AS importeFlete4, ");
        query.append("		mImporteISRLaudos AS importeISRLaudos, ");
        query.append("		mImporteIva AS importeIva, ");
        query.append("		mImporteIvaArrenda AS importeIvaArrenda, ");
        query.append("		mImporteIvaHonorarios + isnull(mImporteIva6,0) AS importeIvaHonorarios, ");
        query.append("		mImporteIvaProv AS importeIvaProv, ");
        query.append("		mImporteMasIva AS importeMasIva, ");
        query.append("		mImporteNeto AS importeNeto, ");
        query.append("		mImporteObra AS importeObra, ");
        query.append("		mISRArrenda AS importeISRArrenda, ");
        query.append("		mISRHonorarios AS importeISRHonorarios, ");
        query.append("		mISROtros AS importeISROtros, ");
        query.append("		mObra5 AS importeObra5, ");
        query.append("		mOtrosImpuestos AS importeOtrosImpuestos, ");
        query.append("		mPenalizacion AS importePenalizacion, ");
        query.append("		mRetencion AS importeRetencion, ");
        query.append("		mRetImpuestoCedular AS importeRetImpuestoCedular, ");
        query.append("		mSancion AS importeSancion, ");
        query.append("		mTesofe AS importeTesofe, ");
        query.append("		nCapitulo AS capitulo, ");
        query.append("		nDocRenglon AS numeroRenglon, ");
        query.append("		nPoliza AS numeroPoliza, ");
        query.append("		OBGT AS obgt, ");
        query.append("		RFC AS rfc, ");
        query.append("		aEjercicioFiscal AS ejercicioFiscal, ");
        query.append("		cDocumentoHaplicado AS documentoAplicado, ");
        query.append("		cIdEntidadContable AS cIdEntidadContable, ");
        query.append("		cIdRelacion AS cIdRelacion, ");
        query.append("		cTipoPoliza AS cTipoPoliza, ");
        query.append("		m5Millar AS importe5Millar, ");
        query.append("		mAmortizacionAnticipo AS importeAmortizacionAnticipo, ");
        query.append("		mBruto AS importeBruto, ");
        query.append("		mCedular AS importeCedular, ");
        query.append("		mFletes AS importeFletes, ");
        query.append("		mIVA AS importeIVA, ");
        query.append("		mNeto AS importeNeto, ");
        query.append("		nFolioPAGOFEDERALIZADO AS folioPagoFederalizado, ");
        query.append("		nFolioPoliza AS folioPoliza, ");
        query.append("		nMes AS numeroMes,  ");
        query.append("		mimporteISRResico AS importeISRResico, ");
        query.append("		FFM, cUnidadResponsable AS unidadResponsable ");
        query.append("  FROM	tPAGOFEDERALIZADODetalle WITH(NOLOCK)");
        query.append(" WHERE	nFolioPAGOFEDERALIZADO = ?");
        List<EgresoDetalle> detalle = new ArrayList<EgresoDetalle>();
        try {
            String query2 = query.toString();
            ps = conn.prepareStatement(query2);
            ps.setInt(1, folioPago);
            rs = ps.executeQuery();
            DateConverter converter = new DateConverter(null);
            Map<String, String> resultObj = RSToTable.rsToMapCaseSensitive(rs);
            converter.setPattern("yyyy-MM-dd");
            ConvertUtils.register(converter, Date.class);
            while (resultObj != null) {
                EgresoPAGOFEDERALIZADODetalle pdDetalle = new EgresoPAGOFEDERALIZADODetalle();
                BeanUtils.populate(pdDetalle, resultObj);
                detalle.add(pdDetalle);
                resultObj = RSToTable.rsToMapCaseSensitive(rs);
            }
            return detalle;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }
}
