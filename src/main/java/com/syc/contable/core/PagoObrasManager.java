package com.syc.contable.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang.StringUtils;
import com.syc.egresos.core.EgresoDetalle;
import com.syc.egresos.core.impl.EgresoPAGOOBRADetalle;
import com.syc.egresos.core.impl.EgresoPAGOOBRAEncabezado;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.contabilidad.utils.db.RSToTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PagoObrasManager {

    private static final Logger log = LoggerFactory.getLogger(PagoObrasManager.class);

    public PagoObrasManager() {
        super();
    }

    public static ArrayList<String> BuscaCompromisos(Connection conn, String listaIds, String listaCuentaBancaria, String listaFechas, String listaLeyendas, String sUsuario) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        ResultSet rs = null;
        ResultSet rs2 = null;
        String[] arrFolios = listaIds.split(",");
        String[] arrCuentasBancarias = listaCuentaBancaria.split(",");
        String[] arrFechas = listaFechas.split(",");
        String[] arrLeyendas = listaLeyendas.split(",");
        int intIndice = -1;
        BigDecimal total = new BigDecimal(0);
        BigDecimal retenciones = new BigDecimal(0);
        BigDecimal revisarTotal = new BigDecimal(0);
        BigDecimal revisarRete = new BigDecimal(0);
        BigDecimal revisarIVA = new BigDecimal(0);
        BigDecimal diferencia = new BigDecimal("0.00");
        String nFolio = "";
        String nFolioCompromiso = "";
        try {
            rs = insertaEncabezadoObra(conn, listaIds);
            while (rs.next()) {
                System.out.println(arrFolios[0].trim());
                nFolio = rs.getString(1);
                nFolioCompromiso = nFolio;
                for (int i = 0; i < arrFolios.length; i++) {
                    nFolio = arrFolios[i].trim();
                    if (nFolio.equals(nFolioCompromiso)) {
                        intIndice = i;
                        break;
                    }
                }
                String vcReferencia1 = rs.getString(23).trim();
                String vcReferencia2 = rs.getString(24).trim();
                String vcCtaBancaria = arrCuentasBancarias[intIndice].trim().trim();
                String vcBenef = rs.getString(18).trim();
                String vcLeyenda = arrLeyendas[intIndice].trim().trim();
                String vRFC = rs.getString(20).trim();
                if (!"".equals(vcReferencia1) && "".equals(vcReferencia2)) {
                    vcLeyenda = "0";
                }
                String encabezado = rs.getString(2) + "," + arrFechas[intIndice].trim() + "," + rs.getString(4).trim() + "," + rs.getString(5).trim() + "," + rs.getString(6).trim() + "," + rs.getString(7).trim() + "," + rs.getString(8).trim() + "," + rs.getString(9).trim() + "," + rs.getString(10).trim() + "," + rs.getString(11).trim() + "," + rs.getString(12).trim() + "," + rs.getString(13).trim() + "," + rs.getString(14).trim() + "," + rs.getString(15).trim() + "," + rs.getString(16).trim() + "," + vcLeyenda + "," + vcBenef + "," + vcCtaBancaria + "," + vRFC + "," + rs.getString(21).trim() + "," + rs.getString(22).trim() + "," + rs.getString(23).trim() + "," + rs.getString(24).trim() + "," + rs.getString(25).trim() + "," + rs.getString(26).trim() + "," + rs.getString(27).trim() + "," + rs.getString(28).trim() + "," + rs.getString(29).trim() + "," + rs.getString(30).trim() + "," + rs.getString(31).trim() + "," + rs.getString(32).trim() + "," + rs.getString(33).trim() + "," + rs.getString(34).trim() + "," + rs.getString(35).trim() + "," + rs.getString(36).trim() + "," + rs.getString(37).trim() + "," + rs.getString(38).trim() + "," + rs.getString(39).trim() + "," + rs.getString(40).trim() + "," + rs.getString(41);
                encabezado = encabezado + "\r\n";
                arrListaComp.add(encabezado);
                // Aqui grabamos dentro de layouts creados encabezado
                insertaLayoutEncabezadoObra(conn, arrFechas[intIndice].trim(), arrLeyendas[intIndice].trim(), arrCuentasBancarias[intIndice].trim(), sUsuario, listaIds);
                rs2 = insertaDetalleObra(conn, nFolioCompromiso);
                int renglon = 1;
                while (rs2.next()) {
                    String token = new String();
                    StringBuffer detalle = new StringBuffer();
                    for (int i = 1; i < 43; i++) {
                        if (i == 28) {
                            revisarTotal = rs2.getBigDecimal(i);
                            total = total.add(revisarTotal);
                            if (revisarTotal.compareTo(BigDecimal.ZERO) < 0) {
                                throw new Exception("No se genero el layout ya que el importe de uno de los registros del layout es menor que cero. Revise los pagos: " + listaIds);
                            } else {
                                String valor = (StringUtils.isBlank(rs2.getString(i)) ? "" : rs2.getString(i).trim());
                                detalle.append(token).append(valor);
                                token = ",";
                            }
                        } else if (i >= 33 && i <= 37) {
                            //Suma el importe de las retenciones
                            revisarRete = rs2.getBigDecimal(i);
                            retenciones = retenciones.add(revisarRete);
                            String valor = (StringUtils.isBlank(rs2.getString(i)) ? "" : rs2.getString(i).trim());
                            detalle.append(token).append(valor);
                            token = ",";
                        } else if (i == 40) {
                            revisarIVA = rs2.getBigDecimal(i);
                            //Validar y ajuste de IVA
                            if (renglon == 1) {
                                diferencia = validarIvaPago(conn, Integer.parseInt(nFolio.trim())).setScale(2, RoundingMode.HALF_UP);
                                if (diferencia.compareTo(revisarIVA) < 0) {
                                    revisarIVA = revisarIVA.subtract(diferencia);
                                    diferencia = BigDecimal.ZERO;
                                } else {
                                    diferencia = diferencia.subtract(revisarIVA);
                                    revisarIVA = new BigDecimal("0.00");
                                }
                            } else if (renglon > 1 && diferencia.compareTo(BigDecimal.ZERO) != 0) {
                                if (diferencia.compareTo(revisarIVA) < 0) {
                                    revisarIVA = revisarIVA.subtract(diferencia);
                                    diferencia = BigDecimal.ZERO;
                                } else {
                                    diferencia = diferencia.subtract(revisarIVA);
                                    revisarIVA = new BigDecimal("0.00");
                                }
                            }
                            detalle.append(token).append(revisarIVA);
                            token = ",";
                        } else {
                            String valor = (StringUtils.isBlank(rs2.getString(i)) ? "" : rs2.getString(i).trim());
                            detalle.append(token).append(valor);
                            token = ",";
                        }
                    }
                    renglon++;
                    token = "";
                    detalle.append("\r\n");
                    arrListaComp.add(detalle.toString());
                }
                // Aqui grabamos dentro de layouts creados detalle
                insertaLayoutDetalleObra(conn, nFolioCompromiso, listaIds);
            }
            //Valida que el total del Layout sea igual a los pagos
            validarTotalLayout(conn, total, listaIds);
            //Valida que el total de las Retenciones sea igual a los pagos
            validarTotalRetenciones(conn, retenciones, listaIds);
            return arrListaComp;
        } finally {
            CloseObject.closeObject(rs2);
            CloseObject.closeObject(rs);
        }
    }

    public static BigDecimal validarIvaPago(Connection con, int folio) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        BigDecimal importe = new BigDecimal(0).setScale(2, RoundingMode.HALF_UP);
        StringBuilder query = new StringBuilder();
        query.append(" SELECT ISNULL(SUM(d.miva), 0) - ISNULL(h.mImporteIVA, 0) AS Importe ");
        query.append("	FROM tPAGOOBRADetalle d WITH (NOLOCK) INNER JOIN tPAGOOBRAEncabezado h WITH (NOLOCK) ");
        query.append(" ON h.nFolioPAGOOBRA = d.nFolioPAGOOBRA");
        query.append(" WHERE d.nFolioPAGOOBRA = ?  AND d.mImporteMasIva > 0");
        query.append(" GROUP BY d.nFolioPAGOOBRA, h.mImporteIVA");
        try {
            pst = con.prepareStatement(query.toString());
            pst.setInt(1, folio);
            rs = pst.executeQuery();
            if (rs.next()) {
                importe = rs.getBigDecimal(1);
            }
            return importe;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pst);
        }
    }

    public static ArrayList<String> CreaDocumentacionComprobatoria(Connection conn, String listaIds) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntH = null;
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        String Sql = " SELECT nFolioPAGOOBRA,  'H' H,  cRamo,  'RHQ',  '' SOL_PAGO,  '3',  caNoContrarrecibo FOLIO_INTERNO,  caNoContrarrecibo COMODIN  FROM tPAGOOBRAEncabezado  WHERE nFolioPAGOOBRA in (" + listaIds + ") ";
        try {
            pstmntH = conn.prepareStatement(Sql);
            log.debug(Sql);
            rs = pstmntH.executeQuery();
            String Sql2 = "select distinct PDE.cRamo, 'POBR-' + CAST(PDE.nFolioPAGOOBRA AS VARCHAR(7)), CONVERT(nvarchar(10), PDE.fAplicacion,103),  " + "			CONVERT(nvarchar(10), PDE.fAplicacion,103) + ' 12:00:00 a.m.',  " + "			ISNULL(BB.CBEN, '') cben, case when BB.cExtranjero = 1 then '05' else '04' end 'TipoBen',  " + "			'85', CASE WHEN CONVERT(int,PDE.mImporteIVA) = 0 THEN '05' else  '07' END  TIVA,  " + "			CONVERT(decimal(17,2), 0),  " + "			CONVERT(decimal(17,2), PDE.mImporteBruto) BRUTO,   " + "			CONVERT(decimal(17,2), PDE.mImporteIVA) IVA_DES, " + "			CONVERT(decimal(17,2), DCD.DCD_IVA) IVA,  " + "			CONVERT(decimal(17,2), DCD.DCD_ISR) ISR, " + "			CONVERT(decimal(17,2), DCD.DCD_MIL5) MIL5, " + "			CONVERT(decimal(17,2), DCD.DCD_MIL2) MIL2, " + "			CONVERT(decimal(17,2), DCD.DCD_OTRAS_RET) OTRASRET," + "			CONVERT(decimal(17,2), DCD.DCD_PENALIZACION) PEN," + "			CONVERT(decimal(17,2), DCD.DCD_CONTRIBUCION) CONT," + "			0,  REPLACE(REPLACE(pde.cConcepto,',',''),'\\',''), PDE.nFolioPAGOOBRA" + "		from dbo.tPAGOOBRAEncabezado PDE WITH (NOLOCK) " + "		inner join v_DCD_PAGO_OBRA DCD WITH (NOLOCK)  on pde.nFolioPAGOOBRA = dcd.nFolioPAGOOBRA " + "		LEFT JOIN pContratoOBRASesion sd WITH (NOLOCK)  ON PDE.cFolioContratoObra = sd.cIdContrato and bActivo = 1 AND sd.fSesionVigencia = (select convert(date, isnull(min(pcs.fSesionVigencia), '1900-01-01')) from pContratoObraSesion pcs where pcs.cIdContrato = sd.cIdContrato and bActivo = 1 and pcs.fSesionVigencia >= convert(date, GETDATE())) " + "		LEFT JOIN tBeneficiario BB WITH (NOLOCK) ON BB.dRFC = PDE.RFC " + " where PDE.nFolioPAGOOBRA =  ? ORDER BY PDE.nFolioPAGOOBRA";
            pstmntD = conn.prepareStatement(Sql2);
            while (rs.next()) {
                String nFolioCompromiso = rs.getString(1);
                String encabezado = rs.getString(2).trim() + "," + rs.getString(3).trim() + "," + rs.getString(4).trim() + "," + rs.getString(5).trim() + "," + rs.getString(6).trim() + "," + rs.getString(7).trim() + "," + rs.getString(8).trim();
                encabezado = encabezado + "\r\n";
                arrListaComp.add(encabezado);
                pstmntD.setString(1, nFolioCompromiso);
                log.debug(Sql2);
                rs2 = pstmntD.executeQuery();
                while (rs2.next()) {
                    String token = new String();
                    StringBuffer detalle = new StringBuffer();
                    for (int i = 1; i < 21; i++) {
                        detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                        token = ",";
                    }
                    token = "";
                    detalle.append("\r\n");
                    arrListaComp.add(detalle.toString());
                }
            }
        } finally {
            CloseObject.closeObject(rs2);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmntD);
            CloseObject.closeObject(pstmntH);
        }
        return arrListaComp;
    }

    public static int updateHeaderCompromisos(Connection conn, String listaIds) throws SQLException {
        PreparedStatement pstmnt = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("UPDATE tPAGOOBRAEncabezado SET nEnviadoSICOP = 1 WHERE nFolioPAGOOBRA in (" + listaIds + ")");
            retval = pstmnt.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return retval;
    }

    public static int UpdateStatus(Connection conn, String listaIds) throws SQLException {
        PreparedStatement pstmnt = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("UPDATE tPAGOOBRAEncabezado SET nEnviadoSICOP = 2 WHERE nFolioPAGOOBRA in (" + listaIds + ")");
            retval = pstmnt.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return retval;
    }

    public static boolean updateHeaderCompromisosRealimentacion(Connection conn, Integer nEnviadoSICOP, String caNoCompromiso) throws SQLException {
        PreparedStatement pstmntL = null;
        String queryUpdateEstatus = "UPDATE tCompromisoEncabezado SET nEnviadoSICOP = " + nEnviadoSICOP + "   WHERE caNoCompromiso = '" + caNoCompromiso + "'";
        try {
            pstmntL = conn.prepareStatement(queryUpdateEstatus);
            pstmntL.executeUpdate();
            return true;
        } finally {
            CloseObject.closeObject(pstmntL);
        }
    }

    public static boolean insertRegisterLayout(Connection conn, String clave, String cRamo, String cUnidadResponsable, String folioSICOP, String idProceso, String cCentroContable, java.sql.Date fExpedicion, float total, String cTipoPoliza, String nFolioPoliza, String nPolizaCancelacion, String tipoMovimiento, String origenPresupuesto, String cuentaBancaria, String noSolicitud, String tCambio, String tMoneda, String tSolicitud, String volante, String rfc, String caNoCompromiso, String codSemarnat2, String estatus, java.sql.Date fAplicacion, String documento, String nDocumento, String descripcion) throws SQLException {
        PreparedStatement pstmnt = null;
        boolean insertReg;
        String queryInsert = "INSERT INTO tLayoutCompromisos(cClave,cRamo,cUnidadCreadora,cFolioCompromisoSICOP,cIdProceso,cCentroContable,fExpedicion,mImporte,cIdTipoPoliza,nPolizaSICOP, nPolizaCancelacionSICOP,cIdTipoMovimiento,nOrigenPresupuesto,nCuentaBancaria,cNoSolicitud,                                               cTipoCambio,cMoneda,cTipoSolicitud,cVolante,dRFC,                                               caNoCompromiso,caNoSemarnat2,cEstatus,fAplicacionSICOP,tDocumento,                                               nDocumento,cDescripcion)             VALUES('" + clave + "','" + cRamo + "','" + cUnidadResponsable + "','" + folioSICOP + "','" + idProceso + "','" + cCentroContable + "','" + fExpedicion + "'," + total + ",'" + cTipoPoliza + "','" + nFolioPoliza + "','" + nPolizaCancelacion + "','" + tipoMovimiento + "','" + origenPresupuesto + "','" + cuentaBancaria + "','" + noSolicitud + "','" + tCambio + "','" + tMoneda + "','" + tSolicitud + "','" + volante + "','" + rfc + "','" + caNoCompromiso + "','" + codSemarnat2 + "','" + estatus + "','" + fAplicacion + "','" + documento + "','" + nDocumento + "','" + descripcion + "')";
        try {
            pstmnt = conn.prepareStatement(queryInsert);
            int reg = pstmnt.executeUpdate();
            if (reg == 1) {
                insertReg = true;
            } else {
                insertReg = false;
            }
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return insertReg;
    }

    public static EgresoPAGOOBRAEncabezado cargaEncabezado(Connection conn, int folioPagoObra) throws Exception {
        StringBuffer sbSel = new StringBuffer();
        sbSel.append("SELECT ");
        sbSel.append("      	aEjercicioFiscal AS ejercicioFiscal, ");
        sbSel.append("      	ALM AS alm, ");
        sbSel.append("      	caNoContrarrecibo AS contrarecibo, ");
        sbSel.append("      	capitulo AS capitulo, ");
        sbSel.append("      	cCentroContable AS centroContable, ");
        sbSel.append("      	cConcepto AS concepto, ");
        sbSel.append("      	cDescripcionPoliza AS descripcionPoliza, ");
        sbSel.append("      	cDocumentoHaplicado AS documentoAplicado, ");
        sbSel.append("      	cEsFirmaElectronica AS esFirmaElectronica, ");
        sbSel.append("      	cFolioContratoObra AS folioContratoObra, ");
        sbSel.append("      	cIdEstadoEstimacion AS idEstadoEstimacion, ");
        sbSel.append("      	cIdTipoDocumento AS idTipoDocumento, ");
        sbSel.append("      	cIdTipoOperacion AS idTipoOperacion, ");
        sbSel.append("      	cIdUsuarioAprobacion AS idUsuarioAprobacion, ");
        sbSel.append("      	cIdUsuarioCaptura AS idUsuarioCaptura, ");
        sbSel.append("      	cIdUsuarioImpresion AS idUsuarioImpresion, ");
        sbSel.append("      	cIdUsuarioRechazo AS idUsuarioRechazo, ");
        sbSel.append("      	cIdUsuarioRevision AS idUsuarioRevision, ");
        sbSel.append("      	cIngresosPropios AS ingresosPropios, ");
        sbSel.append("      	cMes AS mes, ");
        sbSel.append("      	cNoConvenio AS NumConvenio, ");
        sbSel.append("      	cNoEstimacion AS noEstimacion, ");
        sbSel.append("      	cNoFactura AS noFactura, ");
        sbSel.append("      	cOficioDiferenciaCambiaria AS oficioDiferenciaCambiaria, ");
        sbSel.append("      	cRadicado AS radicado, ");
        sbSel.append("      	cRamo AS ramo, ");
        sbSel.append("      	cReferenciaPRODDER AS referenciaPRODDER, ");
        sbSel.append("      	CTAB AS CTAB, ");
        sbSel.append("      	cTipoPoliza AS tipoPoliza, ");
        sbSel.append("      	cUnidadResponsable AS unidadResponsable, ");
        sbSel.append("      	cUnidadResponsableContable AS unidadResponsableContable, ");
        sbSel.append("      	fAplicacion AS fechaAplicacion, ");
        sbSel.append("      	fCancelacion AS fechaCancelacion, ");
        sbSel.append("      	CONVERT( DATE, fperiodode, 103) AS fechaPeriodoDesde, ");
        sbSel.append("      	CONVERT( DATE, fperiodohasta, 103) AS fechaPeriodoHasta, ");
        sbSel.append("      	CONVERT( DATE, fProgramadaPago, 103) AS fechaProgramadaPago, ");
        sbSel.append("      	ID_DESTINO_GASTO AS idDestinoGasto, ");
        sbSel.append("      	ID_TIPO_FONDO AS idTipoFondo, ");
        sbSel.append("      	ID_TIPO_MOVIMIENTO AS idTipoMovimiento, ");
        sbSel.append("      	ID_TIPO_OPERACION AS idTipoOperacion, ");
        sbSel.append("      	lAmortizarAnticipoConEscalacion AS amortizarAnticipoConEscalacion, ");
        sbSel.append("      	lAplicaImpuestoCedular AS aplicaImpuestoCedular, ");
        sbSel.append("      	lContrarreciboImpreso AS contrarreciboImpreso, ");
        sbSel.append("      	mAcumuladoxpagar AS importeAcumuladoPagar, ");
        sbSel.append("      	mAmortizacion AS importeAmortizacion, ");
        sbSel.append("      	mAmortizacionAcumulado AS importeAmortizacionAcumulado, ");
        sbSel.append("      	mAmortizacionAnticipo AS importeAmortizacionAnticipo, ");
        sbSel.append("      	mImporteBruto AS importeBruto, ");
        sbSel.append("      	mImporteDevolucion AS importeDevolucion, ");
        sbSel.append("      	mImporteDevolucionAcumulado AS importeDevolucionAcumulado, ");
        sbSel.append("      	mImporteIVA AS importeIVA, ");
        sbSel.append("      	mImporteMasIva AS importeMasIva, ");
        sbSel.append("      	mImporteNeto AS importeNeto, ");
        sbSel.append("      	mImportePenalizacion AS importePenalizacion, ");
        sbSel.append("      	mImporteRetencion AS importeRetencion, ");
        sbSel.append("      	mImporteSancion AS importeSancion, ");
        sbSel.append("      	mImporteSancionAcumulado AS importeSancionAcumulad, ");
        sbSel.append("      	mSaldoAnticipo AS importeSaldoAnticipo, ");
        sbSel.append("      	mSaldoCedula AS importeSaldoCedula, ");
        sbSel.append("      	nEnviadoSICOP AS enviadoSICOP, ");
        sbSel.append("      	nFolioPAGOOBRA AS folioPagoObra, ");
        sbSel.append("      	nFolioPoliza AS folioPoliza, ");
        sbSel.append("      	nFolioPolizaCancelacion AS folioPolizaCancelacion, ");
        sbSel.append("      	nIdConcepto AS idConcepto, ");
        sbSel.append("      	nNumEmpleadoAut AS numEmpleadoAut, ");
        sbSel.append("      	nNumEmpleadoElab AS numEmpleadoElab, ");
        sbSel.append("      	nNumEmpleadoVoBo AS numEmpleadoVoBo, ");
        sbSel.append("      	NOMBRE AS Nombre, ");
        sbSel.append("      	nPorcAmortizacion AS porcAmortizacion, ");
        sbSel.append("      	ISNULL( nPorcImpuestoCedular, 0 ) AS porcImpuestoCedular, ");
        sbSel.append("      	nTipoCambio AS tipoCambio, ");
        sbSel.append("      	NumPagoAMF AS numPagoAMF, ");
        sbSel.append("      	RFC AS rfc, ");
        sbSel.append("      	sFirmanteAut AS firmanteAut, ");
        sbSel.append("      	sFirmanteEla AS firmanteEla, ");
        sbSel.append("      	sFirmanteVoBo AS firmanteVoBo, ");
        sbSel.append("      	sPuestoAut AS puestoAut, ");
        sbSel.append("      	sPuestoEla AS puestoEla, ");
        sbSel.append("      	sPuestoVoBo AS puestoVoBo, ");
        sbSel.append("      	U_LOGIN AS login ");
        sbSel.append("  FROM	tPagoObraEncabezado WITH(NOLOCK) ");
        sbSel.append(" WHERE	nFolioPagoObra = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sbSel.toString());
            ps.setInt(1, folioPagoObra);
            rs = ps.executeQuery();
            Map<String, String> resultObj = RSToTable.rsToMapCaseSensitive(rs);
            EgresoPAGOOBRAEncabezado poEncabezado = new EgresoPAGOOBRAEncabezado();
            DateConverter converter = new DateConverter(null);
            converter.setPattern("yyyy-MM-dd");
            ConvertUtils.register(converter, Date.class);
            BeanUtils.populate(poEncabezado, resultObj);
            return poEncabezado;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static List<EgresoDetalle> cargaDetalle(Connection conn, int folioPAGOOBRA) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sbSel = new StringBuilder();
        sbSel.append("SELECT	ADEFAS AS adefas, ");
        sbSel.append("      	aEjercicioFiscal AS ejercicioFiscal, ");
        sbSel.append("      	ALM AS alm, ");
        sbSel.append("      	altaAlmacen AS altaAlmacen, ");
        sbSel.append("      	cCentroContable AS centroContable, ");
        sbSel.append("      	cDocumentoHaplicado AS documentoAplicado, ");
        sbSel.append("      	cEjercicio AS ejercicioFiscal, ");
        sbSel.append("      	cEvento AS evento, ");
        sbSel.append("      	cIdCuentaContable AS idCuentaContable, ");
        sbSel.append("      	cIdEntidadContable AS cIdEntidadContable, ");
        sbSel.append("      	cIdRelacion AS cIdRelacion, ");
        sbSel.append("      	cMes AS mesCalendario, ");
        sbSel.append("      	cTipoPoliza AS cTipoPoliza, ");
        sbSel.append("      	EP AS ep, ");
        sbSel.append("      	ID_TIPO_CONCEPTO AS idTipoConcepto, ");
        sbSel.append("      	ID_TIPO_MOVIMIENTO AS idTipoMovimiento, ");
        sbSel.append("      	m23IVA AS importe23IVA, ");
        sbSel.append("      	m2Millar AS importe2Millar, ");
        sbSel.append("      	m5Millar AS importe5Millar, ");
        sbSel.append("      	mAmortizacionAnticipo AS importeAmortizacionAnticipo, ");
        sbSel.append("      	mBruto AS importeBruto, ");
        sbSel.append("      	mCedular AS importeCedular, ");
        sbSel.append("      	mCNIC AS importeCNIC, ");
        sbSel.append("      	mComprometido AS importeComprometido, ");
        sbSel.append("      	mDevolucion AS importeDevolucion, ");
        sbSel.append("      	mFletes AS importeFletes, ");
        sbSel.append("      	mIMDT AS importeIMDT, ");
        sbSel.append("      	mImporte AS importe, ");
        sbSel.append("      	mImporteAmortiza AS importeAmortiza, ");
        sbSel.append("      	mImporteBruto AS importeBruto, ");
        sbSel.append("      	mImporteFlete23 AS importeFlete23, ");
        sbSel.append("      	mImporteFlete4 AS importeFlete4, ");
        sbSel.append("      	mImporteISRLaudos AS importeISRLaudos, ");
        sbSel.append("      	mImporteIva AS importeIva, ");
        sbSel.append("      	mImporteIva6 AS importeIva6, ");
        sbSel.append("      	mImporteIvaArrenda AS importeIvaArrenda, ");
        sbSel.append("      	mImporteIvaHonorarios AS importeIvaHonorarios, ");
        sbSel.append("      	mImporteIvaProv AS importeIvaProv, ");
        sbSel.append("      	mImporteMasIva AS importeMasIva, ");
        sbSel.append("      	mImporteNegativo AS importeImporteNegativo, ");
        sbSel.append("      	mImporteNeto AS importeNeto, ");
        sbSel.append("      	mImporteObra AS importeObra, ");
        sbSel.append("      	mISRArrenda AS importeISRArrenda, ");
        sbSel.append("      	mISRHonorarios AS importeISRHonorarios, ");
        sbSel.append("      	ISNULL( mISROtros, 0.00) AS importeISROtros, ");
        sbSel.append("      	mIVA AS importeIva, ");
        sbSel.append("      	mNeto AS importeNeto, ");
        sbSel.append("      	mObra5 AS importeObra5, ");
        sbSel.append("      	mPenalizacion AS importePenalizacion, ");
        sbSel.append("      	mRetencion AS importeRetencion, ");
        sbSel.append("      	mRetImpuestoCedular AS importeRetImpuestoCedular, ");
        sbSel.append("      	mSancion AS importeSancion, ");
        sbSel.append("      	mTesofe AS importeTesofe, ");
        sbSel.append("      	nCapitulo AS capitulo, ");
        sbSel.append("      	nDocRenglon AS numeroRenglon, ");
        sbSel.append("      	nFolioPAGOOBRA AS nFolioPAGOOBRA, ");
        sbSel.append("      	nFolioPoliza AS folioPoliza, ");
        sbSel.append("      	nMes AS numeroMes, ");
        sbSel.append("      	nPoliza AS numeroPoliza, ");
        sbSel.append("      	OBGT AS obgt, ");
        sbSel.append("      	Periodo13 AS periodo13, ");
        sbSel.append("      	RFC AS rfc ");
        sbSel.append("  FROM	tPagoObraDetalle WITH(NOLOCK) ");
        sbSel.append(" WHERE	nFolioPAGOOBRA = ? ");
        List<EgresoDetalle> detalle = new ArrayList<EgresoDetalle>();
        try {
            ps = conn.prepareStatement(sbSel.toString());
            ps.setInt(1, folioPAGOOBRA);
            rs = ps.executeQuery();
            DateConverter converter = new DateConverter(null);
            Map<String, String> resultObj = RSToTable.rsToMapCaseSensitive(rs);
            converter.setPattern("yyyy-MM-dd");
            ConvertUtils.register(converter, Date.class);
            while (resultObj != null) {
                EgresoPAGOOBRADetalle pdDetalle = new EgresoPAGOOBRADetalle();
                BeanUtils.populate(pdDetalle, resultObj);
                pdDetalle.setfolioPAGOOBRA(rs.getInt("nFolioPAGOOBRA"));
                detalle.add(pdDetalle);
                resultObj = RSToTable.rsToMapCaseSensitive(rs);
            }
            return detalle;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static void insertaLayoutEncabezadoObra(Connection conn, String fechas, String leyenda, String cuentaBancaria, String sUsuario, String listaIds) throws Exception {
        PreparedStatement pstmntHLayout = null;
        try {
            StringBuilder SqlLayoutGrabado = new StringBuilder();
            SqlLayoutGrabado.append("INSERT INTO tLayoutsCreadosPagoObrasHeader (	fCreacionLayout	,nFolio	,Header	,fCarga	,fAplicacion	,sRamo	,sRamo1	,sRamo2	,sUnidadResponsable	,sUnidadResponsable2	,sUnidadResponsable3	,sTipoMovimiento	,sOrigenPpto	,sTipoSol	,sTipoMoneda	,sTipoCambio	,sTIPO_PAGO	,sCveLeyenda	,sCBEN	,sCUENTA_BANCARIA	,sIdRFC	,sFAC	,fFechaReferencia	,sReferencia1	,sReferencia2	,sConcepto	,sNotasReverso	,sAMF	,sNoContrarrecibo	,sAuxiliarComodin	,sCTR	,sFolioDC	,mISR	,mIVA	,mMil5	,mMil2	,mOtrasRet	,mPenalizacion	,mContribucion	,mIvaDes	,mIvaAnt	,sDestinoGasto	,sLogin)");
            SqlLayoutGrabado.append("SELECT distinct getdate(),  tCE.nFolioPAGOOBRA, 'H' AS Header, '" + fechas);
            SqlLayoutGrabado.append("', CONVERT(nvarchar(10), tCE.fAplicacion,103) ,tCE.cRamo,tCE.cRamo,tCE.cRamo, ");
            SqlLayoutGrabado.append("'RHQ' UnidadResponsable,'RHQ' UnidadResponsable,'RHQ' UnidadResponsable, ");
            SqlLayoutGrabado.append("'N' ID_TIPO_MOVIMIENTO, '1' AS OrigenPpto,'3' AS TipoSol, 'MXN' TipoMoneda ,");
            SqlLayoutGrabado.append(" '1' TipoCambio, '1' TIPO_PAGO, '");
            SqlLayoutGrabado.append(leyenda + "', B.CBEN, '");
            SqlLayoutGrabado.append(cuentaBancaria + "', rtrim(tCE.RFC), 'FAC', ");
            SqlLayoutGrabado.append("'' FechaReferencia, '' Referencia1, '' Referencia2, tCE.cConcepto, '' NotasReverso, '' AMF, ");
            SqlLayoutGrabado.append("rtrim(DC.caNoContrarrecibo) NO_ACMI, rtrim(DC.caNoContrarrecibo) AuxiliarComodin, '' CTR , ");
            SqlLayoutGrabado.append("'' FolioDC, CONVERT(decimal(17,2), DC.DCD_ISR), CONVERT(decimal(17,2),DCD_IVA), ");
            SqlLayoutGrabado.append("CONVERT(decimal(17,2),DC.DCD_MIL5), CONVERT(decimal(17,2),DCD_MIL2), ");
            SqlLayoutGrabado.append("CONVERT(decimal(17,2),DC.DCD_OTRAS_RET), ");
            SqlLayoutGrabado.append("CONVERT(decimal(17,2),DC.DCD_PENALIZACION), ");
            SqlLayoutGrabado.append("CONVERT(decimal(17,2),DC.DCD_CONTRIBUCION), CONVERT(decimal(17,2),DC.DCD_IVADES), ");
            SqlLayoutGrabado.append("'0' IVAANT, ");
            SqlLayoutGrabado.append("tCE.ID_DESTINO_GASTO, '");
            SqlLayoutGrabado.append(sUsuario + "' ");
            SqlLayoutGrabado.append(" FROM tPAGOOBRAEncabezado tCE (NOLOCK)");
            SqlLayoutGrabado.append(" LEFT JOIN tBeneficiario B (NOLOCK) ON tce.RFC = B.dRFC ");
            SqlLayoutGrabado.append(" INNER JOIN tBeneficiarioCuentasBancarias BCB (NOLOCK) ON tCE.RFC =	BCB.dRFC ");
            SqlLayoutGrabado.append(" LEFT JOIN pCatalogoTipoDocumento CTD (NOLOCK) ON tCE.cIdTipoDocumento =	CTD.cIdTipoDocumento ");
            SqlLayoutGrabado.append(" LEFT JOIN v_pagosDocComprobatoria DC (NOLOCK) ON DC.caNoContrarrecibo =tCE.caNoContrarrecibo AND DC.cTipoPago = 'OBRA' ");
            SqlLayoutGrabado.append(" WHERE tCE.nFolioPAGOOBRA in (" + listaIds + ") ");
            log.debug(SqlLayoutGrabado.toString());
            pstmntHLayout = conn.prepareStatement(SqlLayoutGrabado.toString());
            pstmntHLayout.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmntHLayout);
        }
    }

    public static void insertaLayoutDetalleObra(Connection conn, String nFolioCompromiso, String listaIds) throws Exception {
        PreparedStatement pstmntHLayoutDet = null;
        try {
            StringBuilder SqlLayoutGrabadoDet = new StringBuilder();
            SqlLayoutGrabadoDet.append(" INSERT INTO tLayoutsCreadosPagoObrasDetalle SELECT DISTINCT ");
            SqlLayoutGrabadoDet.append(nFolioCompromiso + ", '1' ID_EVENTO,'24.0.001' EVENTO,ltrim(TCEP.cRamo) ID_RAMO_ML, rtrim(TPDE.cUnidadResponsable), ");
            SqlLayoutGrabadoDet.append(" TCEP.aEjercicioFiscal, TCEP.cGrupoFuncional, tCEP.cFuncion, tCEP.cSubFuncion, ");
            SqlLayoutGrabadoDet.append(" 	CASE WHEN tCEP.cProgramaGeneral IN ( SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK) ) THEN '00' ELSE tCEP.cProgramaGeneral END AS cProgramaGeneral, ");
            SqlLayoutGrabadoDet.append("		tCEP.cActividadInstitucional, tCEP.cProgramaPresupuestario, ltrim(substring(cpartida,1,1)) CCAP_157, substring(cpartida,2,1) CCON_158, substring(cpartida,3,1) CPARG_300, ");
            SqlLayoutGrabadoDet.append(" substring(cpartida,4,2) CPAR_159, tCEP.cTipoGasto, tCEP.cFuenteFinanciamiento, tCEP.cEntidadFederativa, tCEP.cCartera,ltrim('0000000' + tCEP.cUnidadEjecutora), ");
            SqlLayoutGrabadoDet.append(" substring(TCEP.cUnidadNorativa,2,2) CCOP_163, '000' PL, '000' OFI, '00000' AUX1, '00000' AUX2, '0000000000' AUX3, TPDE.mImporteNeto, TPDD.cMes AS MES_149, ");
            SqlLayoutGrabadoDet.append(" '0' NRES,ltrim('PN') TIPO_CONTRATO, '000' CONC_MOV, ");
            SqlLayoutGrabadoDet.append(" CONVERT(decimal(17,2),DC.DCD_ISR), ");
            SqlLayoutGrabadoDet.append(" CONVERT(decimal(17,2),m23IVA), ");
            SqlLayoutGrabadoDet.append(" CONVERT(decimal(17,2),DC.DCD_MIL5), ");
            SqlLayoutGrabadoDet.append(" CONVERT(decimal(17,2),DCD_MIL2), ");
            SqlLayoutGrabadoDet.append(" CONVERT(decimal(17,2),DC.DCD_CONTRIBUCION), ");
            SqlLayoutGrabadoDet.append(" CONVERT(decimal(17,2),DC.DCD_OTRAS_RET), ");
            SqlLayoutGrabadoDet.append(" CONVERT(decimal(17,2),DCD_IVA), ");
            SqlLayoutGrabadoDet.append(" CONVERT(decimal(17,2),DC.DCD_PENALIZACION), '' id_ctr_intdet ");
            SqlLayoutGrabadoDet.append(" FROM tPAGOOBRADetalle TPDD (NOLOCK) inner join tPAGOOBRAEncabezado TPDE (NOLOCK)");
            SqlLayoutGrabadoDet.append(" on TPDD.nFolioPAGOOBRA = TPDE.nFolioPAGOOBRA ");
            SqlLayoutGrabadoDet.append(" inner join tCatalogoEP TCEP (NOLOCK) on TPDD.EP = TCEP.EP ");
            SqlLayoutGrabadoDet.append(" LEFT JOIN v_pagosDocComprobatoria DC ON DC.caNoContrarrecibo = TPDE.caNoContrarrecibo AND DC.cTipoPago = 'OBRA' ");
            SqlLayoutGrabadoDet.append(" where TPDD.nFolioPAGOOBRA in (" + listaIds + ") ");
            pstmntHLayoutDet = conn.prepareStatement(SqlLayoutGrabadoDet.toString());
            pstmntHLayoutDet.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmntHLayoutDet);
        }
    }

    public static ResultSet insertaEncabezadoObra(Connection conn, String listaIds) throws Exception {
        PreparedStatement pstmntH = null;
        ResultSet rs = null;
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT distinct tCE.nFolioPAGOOBRA,  'H' AS Header, ");
        sql.append(" CONVERT(nvarchar(10), tCE.fAplicacion ,103), ");
        sql.append(" CONVERT(nvarchar(10), tCE.fAplicacion,103), ");
        sql.append(" tCE.cRamo, tCE.cRamo,  tCE.cRamo, ");
        sql.append(" 'RHQ' Responsable,  'RHQ' UnidadResponsable,  'RHQ' UnidadResponsable, ");
        sql.append(" 'N' ID_TIPO_MOVIMIENTO,  '5' AS OrigenPpto,  '3' AS TipoSol,  'MXN' TipoMoneda , ");
        sql.append(" '1' TipoCambio,  '1' TIPO_PAGO,  'PENDIENTE' CveLeyenda, ");
        sql.append(" ISNULL(BB.CBEN, isnull(B.CBEN,'')), ");
        sql.append(" 'Cuenta' CUENTA_BANCARIA, rtrim(ISNULL(SD.cIdRFCSesion, tCE.RFC)), ");
        sql.append(" 'FAC', ");
        sql.append(" '' FechaReferencia, ");
        sql.append(" isnull(ta.nClaveAMF, '') Referencia1, ");
        sql.append(" isnull(ta.numFolioAMF, '') Referencia2,  LEFT(REPLACE(REPLACE(tCE.cConcepto,',',''),'\"',''), 70), ");
        sql.append(" '' NotasReverso,  isnull(ta.nClaveAMF, '') AMF, ");
        sql.append(" rtrim(TCE.caNoContrarrecibo) NO_ACMI, ");
        sql.append(" rtrim(TCE.caNoContrarrecibo) AuxiliarComodin, ");
        sql.append(" '' CTR , ");
        sql.append(" CONVERT(decimal(17, 2), ISNULL(DC.DCD_ISR, 0)) ISR, ");
        sql.append(" CONVERT(decimal(17, 2), isNull(DC.DCD_IVA,0)) RETIVA, ");
        sql.append(" CONVERT(decimal(17, 2), ISNULL(DC.DCD_MIL5, 0)) R5MILLAR, ");
        sql.append(" CONVERT(decimal(17, 2), ISNULL(DC.DCD_MIL2, 0)) R2MILLAS, ");
        sql.append(" CONVERT(decimal(17, 2), ISNULL(DC.DCD_OTRAS_RET, 0)) OTRASRET, ");
        sql.append(" CONVERT(decimal(17, 2), ISNULL(DC.DCD_PENALIZACION, 0)) PENALIZA, ");
        sql.append(" CONVERT(decimal(17, 2), ISNULL(DC.DCD_CONTRIBUCION, 0)) CONTRIB, ");
        sql.append(" CONVERT(decimal(17, 2), ISNULL(tCE.mimporteIVA, 0)) IVA, ");
        sql.append(" '0.00' IVAANT, ");
        sql.append(" '' FolioDC, ");
        sql.append(" 'NA' ID_DESTINO_GASTO, ");
        sql.append(" '', ");
        sql.append(" '', ");
        sql.append(" isnull(ta.nClaveAMF, '') ");
        sql.append(" FROM tPAGOOBRAEncabezado tCE (NOLOCK) ");
        sql.append("	INNER JOIN pContratoObra contrato (NOLOCK) ON contrato.cIdContrato = tCE.cFolioContratoObra");
        sql.append(" LEFT JOIN pContratoOBRASesion sd (NOLOCK) ON tCE.cFolioContratoObra = sd.cIdContrato and bActivo = 1 AND sd.fSesionVigencia = (select convert(date, isnull(min(pcs.fSesionVigencia), '1900-01-01')) from pContratoObraSesion pcs where pcs.cIdContrato = sd.cIdContrato and bActivo = 1 and pcs.fSesionVigencia >= convert(date, GETDATE())) ");
        sql.append(" LEFT JOIN tBeneficiario B (NOLOCK) ON B.dRFC = tCE.RFC ");
        sql.append(" LEFT JOIN tBeneficiario BB (NOLOCK) ON BB.dRFC = sd.cIdRFCSesion ");
        sql.append(" LEFT JOIN tPagoAMF ta (NOLOCK) ON tCE.NumPagoAMF = ta.numPagoAMF ");
        sql.append(" LEFT JOIN v_DCD_PAGO_OBRA DC (NOLOCK) ON DC.nFolioPAGOOBRA =tCE.nFolioPAGOOBRA ");
        sql.append(" WHERE tCE.nFolioPAGOOBRA in ( " + listaIds + ") ");
        sql.append(" ORDER BY tCE.nFolioPAGOOBRA ");
        try {
            pstmntH = conn.prepareStatement(sql.toString());
            log.debug(sql.toString());
            rs = pstmntH.executeQuery();
        } finally {
        }
        return rs;
    }

    public static ResultSet insertaDetalleObra(Connection conn, String folios) throws Exception {
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        StringBuilder sql2 = new StringBuilder();
        sql2.append(" SELECT   '1' ID_EVENTO,  '24.0.001' EVENTO,  ltrim(TCEP.cRamo) ID_RAMO_ML,   'RHQ',   TCEP.aEjercicioFiscal,   TCEP.cGrupoFuncional,   tCEP.cFuncion,   tCEP.cSubFuncion,   ");
        sql2.append("			CASE WHEN tCEP.cProgramaGeneral IN ( SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK) ) THEN '00' ELSE tCEP.cProgramaGeneral END AS cProgramaGeneral ");
        sql2.append("			,  tCEP.cActividadInstitucional,   tCEP.cProgramaPresupuestario,   ");
        sql2.append("			ltrim(substring(cpartida,1,1)) CCAP_157,   substring(cpartida,2,1) CCON_158 ");
        sql2.append("			,   substring(cpartida,3,1) CPARG_300,   substring(cpartida,4,2) CPAR_159 ");
        sql2.append("			,   tCEP.cTipoGasto,   tCEP.cFuenteFinanciamiento,   tCEP.cEntidadFederativa,  ");
        sql2.append("			 SUBSTRING( dbo.CambiaEPCarteraMeta(TPDD.EP),45, 11) cCartera,  '0000000000',  '00' CCOP_163, ");
        sql2.append("			 '000' PL,   '000' OFI,   '00000' AUX1,   '00000' AUX2,   '0000000000' AUX3,  ");
        sql2.append("				 (SELECT top 1 nFolioAutSICOP FROM tCompromisoEncabezado CompEnc WITH (NOLOCK) WHERE nFolioCompromiso = ( SELECT MAX(nFolioCompromiso) FROM tCompromisoEncabezado WITH (NOLOCK) WHERE cIdContrato = TPDE.cFolioContratoObra and cDocumentoHaplicado = 'S' AND nFolioAutSICOP <> '-1' AND nFolioAutSICOP IS NOT NULL) ) AS NCOM_35,  ");
        sql2.append("			 sum(CONVERT(decimal(17, 2), TPDD.mImporteNeto)) Monto, ");
        sql2.append("			 TPDD.cMes AS MES_149,   ");
        sql2.append("     		 (SELECT TOP 1 nFolioSuficiencia FROM tCompromisoEncabezado WITH (NOLOCK) WHERE cIdContrato = cFolioContratoObra AND nFolioSuficiencia IS NOT NULL AND nFolioSuficiencia != -1  ORDER BY nFolioCompromiso DESC) NRES,");
        sql2.append("			  CASE WHEN cpartida='35801' THEN 'GD' ELSE 'PN' END TIPO_CONTRATO,  ");
        sql2.append("			 '000' CONC_MOV ");
        sql2.append("			 , CONVERT(decimal(17, 2), sum(TPDD.mISRHonorarios + TPDD.mISRArrenda)) DCD_ISR ");
        sql2.append("			 , CONVERT(decimal(17, 2), sum(TPDD.m23Iva + TPDD.mImporteFlete23 + TPDD.mImporteIva6)) DCD_IVA ");
        sql2.append("			 , CONVERT(decimal(17, 2), sum(TPDD.mObra5)) DCD_MIL5 ");
        sql2.append("			 , CONVERT(decimal(17, 2), sum(TPDD.mCNIC + TPDD.mIMDT)) DCD_MIL2 ");
        sql2.append("			 , CONVERT(decimal(17, 2), sum(ISNULL(TPDD.mRetImpuestoCedular, 0))) DCD_OTRAS_RET ");
        sql2.append("			 , CONVERT( DECIMAL( 17,2), sum(mPenalizacion) )  DCD_PENALIZACION ");
        sql2.append("			 , CONVERT(decimal(17, 2), sum(ISNULL(TPDD.mImporteFlete4, 0))) DCD_CONTRIBUCION ");
        sql2.append("			 , CONVERT(decimal(17, 2), sum(TPDD.mIVA) ) IVADES_45 ");
        sql2.append("			 , CONVERT(decimal(17, 2), 0) ANTICIPO_46 ");
        sql2.append("			 , CONVERT(decimal(17, 2), 0) IVAANT_47,  '' id_ctr_intdet  , TPDD.nFolioPAGOOBRA ");
        sql2.append("		FROM  tPAGOOBRADetalle TPDD  WITH (NOLOCK) ");
        sql2.append("			 INNER JOIN tPAGOOBRAEncabezado TPDE   WITH (NOLOCK) ON TPDD.nFolioPAGOOBRA = TPDE.nFolioPAGOOBRA    ");
        sql2.append("			 INNER JOIN tCatalogoEP TCEP   WITH (NOLOCK) ON TPDD.EP = TCEP.EP    ");
        sql2.append("		WHERE TPDE.cDocumentoHaplicado = 'S' and TPDD.nFolioPAGOOBRA in (" + folios + ") AND cEvento <> 'ANTICIPO'   ");
        sql2.append("			 GROUP BY TCEP.cRamo, 	TCEP.aEjercicioFiscal,	TCEP.cGrupoFuncional, 	tCEP.cFuncion, 	tCEP.cSubFuncion, 	cProgramaGeneral,");
        sql2.append("				tCEP.cActividadInstitucional, tCEP.cProgramaPresupuestario, cpartida, tCEP.cTipoGasto, tCEP.cFuenteFinanciamiento, tCEP.cEntidadFederativa, SUBSTRING( dbo.CambiaEPCarteraMeta(TPDD.EP),45, 11) ");
        sql2.append("			   ,ltrim(TPDD.ID_TIPO_CONCEPTO), TPDE.cFolioContratoObra, TPDE.cCentroContable,  TPDD.cMes , TPDD.nFolioPAGOOBRA ");
        sql2.append("		HAVING SUM(TPDD.mImporteNeto) >0 ");
        sql2.append("		ORDER BY TPDD.nFolioPAGOOBRA");
        try {
            String Sql2 = sql2.toString();
            pstmntD = conn.prepareStatement(Sql2);
            rs = pstmntD.executeQuery();
        } finally {
        }
        return rs;
    }

    public static void validarTotalLayout(Connection con, BigDecimal total, String listaIds) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        BigDecimal importe = new BigDecimal(0);
        String query = "SELECT SUM(mImporteNeto) FROM tPAGOOBRADetalle (NOLOCK) WHERE nFolioPAGOOBRA IN (" + listaIds + ")";
        try {
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();
            if (rs.next()) {
                importe = rs.getBigDecimal(1);
            }
            int resultado = total.compareTo(importe);
            if (resultado != 0) {
                throw new Exception("El importe del Layout es diferente de la suma de los pagos. Reporte al administrador.");
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pst);
        }
    }

    public static void validarTotalRetenciones(Connection con, BigDecimal total, String listaIds) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        BigDecimal importe = new BigDecimal(0);
        String query = "SELECT SUM(mImporteIvaArrenda + mImporteIvaHonorarios + mImporteObra +mImporteFlete23 +mISRHonorarios +mISRArrenda + mImporteFlete4  + mObra5 +mImporteISRLaudos + mISROtros+mRetImpuestoCedular + isnull(mImporteIva6,0) +  isnull(mImporteIsrResico,0) ) totalRetenciones " + " FROM tPAGOOBRADetalle (NOLOCK) WHERE nFolioPAGOOBRA IN (" + listaIds + ")";
        try {
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();
            if (rs.next()) {
                importe = rs.getBigDecimal(1);
            }
            int resultado = total.compareTo(importe);
            if (resultado != 0) {
                throw new Exception("No se genero el layout. El importe de las Retenciones es diferente de la suma de los pagos. Reporte al administrador.");
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pst);
        }
    }
}
