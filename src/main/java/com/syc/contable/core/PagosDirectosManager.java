package com.syc.contable.core;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang.StringUtils;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.core.impl.EgresoPAGODIRECTOEncabezado;
import com.syc.ejercido.pagado.core.EgresosManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.contabilidad.utils.db.RSToTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PagosDirectosManager {

    private static final Logger log = LoggerFactory.getLogger(PagosDirectosManager.class);

    public static int actualizaEstatus(Connection conn, EgresoPAGODIRECTOEncabezado pde) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static ArrayList<StringBuilder> generaLayoutPagos(Connection conn, String listaIds, String listaCuentaBancaria, String listaFechas, String listaLeyendas, String sUsuario) throws Exception {
        ArrayList<StringBuilder> arrListaComp = new ArrayList<StringBuilder>();
        PreparedStatement pstmntPagos = null;
        PreparedStatement pstmntHLayout = null;
        PreparedStatement pstmntHLayoutDet = null;
        PreparedStatement pstmntPagosDet = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        BigDecimal total = new BigDecimal(0);
        BigDecimal retenciones = new BigDecimal(0);
        BigDecimal revisarTotal = new BigDecimal(0);
        BigDecimal revisarRete = new BigDecimal(0);
        try {
            String[] arrFolios = listaIds.split(",");
            String[] arrCuentasBancarias = listaCuentaBancaria.split(",");
            String[] arrFechas = listaFechas.split(",");
            String[] arrLeyendas = listaLeyendas.split(",");
            int intIndice = -1;
            //Query para insertar en la tabla de tLayoutsCreadosHeader
            StringBuilder sqlInsertaLayoutGrabado = PagosDirectosManager.generaQueryInsertaLayout();
            log.debug("Query para insertar layouts: " + sqlInsertaLayoutGrabado);
            pstmntHLayout = conn.prepareStatement(sqlInsertaLayoutGrabado.toString());
            //Query para generar el detalle del layouts de solicitudes de pago
            StringBuilder queryPagosDet = generaQueryPagosDet();
            log.debug("Query SP Detalle: " + sqlInsertaLayoutGrabado);
            pstmntPagosDet = conn.prepareStatement(queryPagosDet.toString());
            //Query para Insertar en el detalle de tLayoutsCreadosDetalle
            StringBuilder sqlLayoutInsertaDet = generaQueryInsertDetalleLayout();
            log.debug("Query inserta Detalle Layout: " + sqlLayoutInsertaDet);
            pstmntHLayoutDet = conn.prepareStatement(sqlLayoutInsertaDet.toString());
            //Query para genera el encabezado del layout de la solicitud de pago
            StringBuilder querySelectPagos = genQueryPagosSICOP(listaIds);
            log.debug("Ejecutando query SP Encabezado: " + querySelectPagos.toString());
            pstmntPagos = conn.prepareStatement(querySelectPagos.toString());
            rs = pstmntPagos.executeQuery();
            log.trace("Consulta ejecutada. Iterando resultados");
            while (rs.next()) {
                log.debug("Procesando pago: " + rs.getString(1));
                String nFolio = "";
                String nFolioCompromiso = rs.getString(1);
                for (int i = 0; i < arrFolios.length; i++) {
                    nFolio = arrFolios[i].trim();
                    if (nFolio.equals(nFolioCompromiso)) {
                        intIndice = i;
                        break;
                    }
                }
                String vcReferencia1 = rs.getString(24).trim();
                String vcReferencia2 = rs.getString(25).trim();
                String vcCtaBancaria = arrCuentasBancarias[intIndice].trim().trim();
                String vcBenef = rs.getString(18).trim();
                String vcLeyenda = arrLeyendas[intIndice].trim().trim();
                String vRFC = rs.getString(21).trim();
                //Datos de cadenas productivas
                String vcEstatus = rs.getString(43).trim();
                String vIntermFin = rs.getString(44).trim();
                String vnClaveAMF = rs.getString(45).trim();
                if (!"".equals(vnClaveAMF)) {
                    vcBenef = "S24676";
                    vcCtaBancaria = "22800100000100";
                    vcLeyenda = "1";
                    vRFC = "6001";
                }
                if ("Operada Pagada".equals(vcEstatus)) {
                    vcCtaBancaria = vIntermFin;
                }
                if (!"".equals(vcReferencia1) && "".equals(vcReferencia2)) {
                    vcLeyenda = "0";
                }
                log.trace("Armando encabezado");
                StringBuilder encabezado = new StringBuilder();
                encabezado.append(rs.getString(2));
                encabezado.append(",");
                encabezado.append(arrFechas[intIndice].trim());
                encabezado.append(",");
                encabezado.append(rs.getString(4).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(5).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(6).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(7).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(8).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(9).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(10).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(11).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(12).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(13).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(14).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(15).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(16).trim());
                encabezado.append(",");
                encabezado.append(vcLeyenda);
                encabezado.append(",");
                encabezado.append(vcBenef);
                encabezado.append(",");
                encabezado.append(vcCtaBancaria);
                encabezado.append(",");
                encabezado.append(vRFC);
                encabezado.append(",");
                encabezado.append(rs.getString(22).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(23).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(24).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(25).trim().replaceAll("[\r\n]{2,}", " "));
                encabezado.append(",");
                encabezado.append(rs.getString(26).trim().replaceAll("[\r\n]{2,}", " "));
                encabezado.append(",");
                encabezado.append(rs.getString(27).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(28).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(29).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(30).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(31).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(32).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(33).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(34).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(35).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(36).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(37).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(38).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(39).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(40).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(41));
                encabezado.append(",");
                encabezado.append(rs.getString(42));
                encabezado.append("\r\n");
                log.trace("Encabezado Armado: \n" + encabezado);
                arrListaComp.add(encabezado);
                int insertados = 0;
                log.trace("Ejecutando query\n[" + sqlInsertaLayoutGrabado + "]\nValores:" + "[" + arrFechas[intIndice].trim() + "]" + "[" + arrLeyendas[intIndice].trim() + "]" + "[" + arrCuentasBancarias[intIndice].trim() + "]" + "[" + sUsuario + "]" + "[" + rs.getString(1) + "]");
                //Se insertan la información de tlayouts
                pstmntHLayout.setString(1, arrFechas[intIndice].trim());
                pstmntHLayout.setString(2, arrLeyendas[intIndice].trim());
                pstmntHLayout.setString(3, arrCuentasBancarias[intIndice].trim());
                pstmntHLayout.setString(4, sUsuario);
                pstmntHLayout.setString(5, rs.getString(1));
                insertados = pstmntHLayout.executeUpdate();
                log.debug("Se insertaron " + insertados + " registros");
                //Genera el detalle para layout de Solicitud de Pago SICOP
                pstmntPagosDet.setString(1, rs.getString(1));
                log.trace("Ejecutando \n[" + queryPagosDet + "]\n[" + rs.getString(1) + "]");
                rs2 = pstmntPagosDet.executeQuery();
                while (rs2.next()) {
                    String token = new String("");
                    StringBuilder detalle = new StringBuilder();
                    for (int i = 1; i < 40; i++) {
                        if (i == 27) {
                            revisarTotal = rs2.getBigDecimal(i);
                            total = total.add(revisarTotal);
                            if (revisarTotal.compareTo(BigDecimal.ZERO) < 0) {
                                throw new Exception("No se genero el layout ya que el importe de uno de los registros del layout es menor que cero. Revise los pagos: " + listaIds);
                            } else {
                                detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                                token = ",";
                            }
                        } else if (i >= 32 && i <= 37) {
                            //Suma el importe de las retenciones
                            revisarRete = rs2.getBigDecimal(i);
                            retenciones = retenciones.add(revisarRete);
                            detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                            token = ",";
                        } else {
                            detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                            token = ",";
                        }
                    }
                    token = "";
                    detalle.append("\r\n");
                    arrListaComp.add(detalle);
                }
                //Genera el detalle de tlayoutsCreadosDetalle
                pstmntHLayoutDet.setString(1, nFolioCompromiso);
                log.trace("Ejecutando \n[" + sqlLayoutInsertaDet + "]" + "[" + nFolioCompromiso + "]" + "[" + rs.getString(1) + "]");
                insertados = pstmntHLayoutDet.executeUpdate();
                log.debug("Se insertaron " + insertados + " registros en tLayoutsCreadosDetalle ");
            }
            //Valida que el total del Layout sea igual a los pagos
            validarTotalLayout(conn, total, listaIds);
            //Valida que el total de las Retenciones sea igual a los pagos
            validarTotalRetenciones(conn, retenciones, listaIds);
            return arrListaComp;
        } finally {
            CloseObject.closeObject(pstmntPagos);
            CloseObject.closeObject(pstmntHLayout);
            CloseObject.closeObject(pstmntHLayoutDet);
            CloseObject.closeObject(pstmntPagosDet);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rs2);
        }
    }

    private static final StringBuilder generaQueryInsertDetalleLayout() {
        StringBuilder sqlLayoutInsertaDet = new StringBuilder();
        sqlLayoutInsertaDet.append("INSERT INTO tLayoutsCreadosDetalle (nFolilo, sID_EVENTO, sEVENTO, sID_RAMO_ML, sUnidadResponsable, saEjercicioFiscal, sGrupoFuncional, sFuncion, sSubFuncion, sProgramaGeneral, ");
        sqlLayoutInsertaDet.append(" 		sActividadInstitucional, sProgramaPresupuestario, sCCAP_157, sCCON_158, sCPARG_300, sCPAR_159, sTipoGasto, sFuenteFinanciamiento, sEntidadFederativa, sCartera, sUnidadEjecutora2, sCCOP_163, sPL, sOFI, sAUX1, sAUX2, sAUX3, ");
        sqlLayoutInsertaDet.append(" 		mImporteNeto, nMES_149, sNRES, sTIPO_CONTRATO, sCONC_MOV, mISR, mIVA, mMil5, mMil2, mContribucion, mOtrasRet, mPenalizacion, sid_ctr_intdet) ");
        sqlLayoutInsertaDet.append("SELECT DISTINCT TPDD.nFolioPagoDirecto, ");
        sqlLayoutInsertaDet.append("      	'1' ID_EVENTO, ");
        sqlLayoutInsertaDet.append("      	'24.0.001' EVENTO, ");
        sqlLayoutInsertaDet.append("      	ltrim(TCEP.cRamo) ID_RAMO_ML, ");
        sqlLayoutInsertaDet.append("      	'RHQ',  ");
        sqlLayoutInsertaDet.append("      	TCEP.aEjercicioFiscal, ");
        sqlLayoutInsertaDet.append("      	TCEP.cGrupoFuncional, ");
        sqlLayoutInsertaDet.append("      	tCEP.cFuncion,  ");
        sqlLayoutInsertaDet.append("      	tCEP.cSubFuncion,  ");
        sqlLayoutInsertaDet.append("      	CASE  ");
        sqlLayoutInsertaDet.append("      			WHEN tCEP.cProgramaGeneral IN ( SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK) ) THEN '00'  ");
        sqlLayoutInsertaDet.append("      	        ELSE tCEP.cProgramaGeneral  ");
        sqlLayoutInsertaDet.append("      	END AS cProgramaGeneral,  ");
        sqlLayoutInsertaDet.append("      	tCEP.cActividadInstitucional,  ");
        sqlLayoutInsertaDet.append("      	tCEP.cProgramaPresupuestario,  ");
        sqlLayoutInsertaDet.append("      	ltrim(substring(cpartida,1,1)) CCAP_157,  ");
        sqlLayoutInsertaDet.append("      	substring(cpartida,2,1) CCON_158,  ");
        sqlLayoutInsertaDet.append("      	substring(cpartida,3,1) CPARG_300,  ");
        sqlLayoutInsertaDet.append("      	substring(cpartida,4,2) CPAR_159,  ");
        sqlLayoutInsertaDet.append("      	tCEP.cTipoGasto,  ");
        sqlLayoutInsertaDet.append("      	tCEP.cFuenteFinanciamiento,  ");
        sqlLayoutInsertaDet.append("      	tCEP.cEntidadFederativa,  ");
        sqlLayoutInsertaDet.append("      	SUBSTRING( dbo.CambiaEPCarteraMeta(TPDD.EP),45, 11), ");
        sqlLayoutInsertaDet.append("      	ltrim('0000000' + tCEP.cUnidadEjecutora),  ");
        sqlLayoutInsertaDet.append("      	substring(TCEP.cUnidadNorativa,2,2) CCOP_163,  ");
        sqlLayoutInsertaDet.append("      	'000' PL,  ");
        sqlLayoutInsertaDet.append("      	'000' OFI,  ");
        sqlLayoutInsertaDet.append("      	'00000' AUX1,  ");
        sqlLayoutInsertaDet.append("      	'00000' AUX2,  ");
        sqlLayoutInsertaDet.append("      	'0000000000' AUX3,  ");
        sqlLayoutInsertaDet.append("      	SUM(TPDD.mImporteNeto) AS mImporteNeto,  ");
        sqlLayoutInsertaDet.append("      	MONTH(GETDATE()) MES_149,  ");
        sqlLayoutInsertaDet.append("      	'0' NRES, ");
        sqlLayoutInsertaDet.append("      	ltrim('PN') TIPO_CONTRATO,  ");
        sqlLayoutInsertaDet.append("      	'000' CONC_MOV, ");
        sqlLayoutInsertaDet.append("      	 CONVERT(decimal(17, 2),DC.DCD_ISR), ");
        sqlLayoutInsertaDet.append("      	 CONVERT(decimal(17, 2),DCD_IVA),  ");
        sqlLayoutInsertaDet.append("      	CONVERT(decimal(17, 2),DC.DCD_MIL5),  ");
        sqlLayoutInsertaDet.append("      	CONVERT(decimal(17, 2),DCD_MIL2),  ");
        sqlLayoutInsertaDet.append("      	CONVERT(decimal(17, 2),DC.DCD_CONTRIBUCION),  ");
        sqlLayoutInsertaDet.append("      	CONVERT(decimal(17, 2),DC.DCD_OTRAS_RET),  ");
        sqlLayoutInsertaDet.append("      	CONVERT(decimal(17, 2),DC.DCD_PENALIZACION),  ");
        sqlLayoutInsertaDet.append("      	'' id_ctr_intdet  ");
        sqlLayoutInsertaDet.append("  FROM	tPagoDirectoDetalle TPDD  WITH(NOLOCK)  ");
        sqlLayoutInsertaDet.append("      	inner join tPagoDirectoEncabezado TPDE  WITH(NOLOCK)  ");
        sqlLayoutInsertaDet.append("      	 on TPDD.nFolioPagoDirecto = TPDE.nFolioPagoDirecto  ");
        sqlLayoutInsertaDet.append("      	 inner join tCatalogoEP TCEP  WITH(NOLOCK)  on TPDD.EP = TCEP.EP ");
        sqlLayoutInsertaDet.append("      	 INNER JOIN v_DCD_PAGO_DIRECTO DC  WITH(NOLOCK)  ");
        sqlLayoutInsertaDet.append("      	 ON DC.nFolioPagoDirecto = TPDE.nFolioPagoDirecto  ");
        sqlLayoutInsertaDet.append("      	 where TPDD.nFolioPagoDirecto = ? ");
        sqlLayoutInsertaDet.append("  GROUP BY TPDD.nFolioPagoDirecto, ltrim(TCEP.cRamo), TCEP.aEjercicioFiscal, TCEP.cGrupoFuncional, tCEP.cFuncion, tCEP.cSubFuncion, tCEP.cProgramaGeneral, tCEP.cActividadInstitucional, ");
        sqlLayoutInsertaDet.append("  		tCEP.cProgramaPresupuestario, ltrim(substring(cpartida,1,1)), substring(cpartida,2,1), substring(cpartida,3,1), substring(cpartida,4,2), tCEP.cTipoGasto, ");
        sqlLayoutInsertaDet.append("  		tCEP.cFuenteFinanciamiento, tCEP.cEntidadFederativa, SUBSTRING( dbo.CambiaEPCarteraMeta(TPDD.EP),45, 11), ltrim('0000000' + tCEP.cUnidadEjecutora),   ");
        sqlLayoutInsertaDet.append("  		substring(TCEP.cUnidadNorativa,2,2), DC.DCD_ISR, DCD_IVA, DC.DCD_MIL5, DCD_MIL2, DC.DCD_CONTRIBUCION, DC.DCD_OTRAS_RET, DC.DCD_PENALIZACION ");
        return sqlLayoutInsertaDet;
    }

    private static StringBuilder generaQueryPagosDet() {
        StringBuilder queryDC = new StringBuilder();
        queryDC.append("SELECT '1' ID_EVENTO, ");
        queryDC.append("      	'24.0.001' EVENTO,");
        queryDC.append("      	ltrim(TCEP.cRamo) ID_RAMO_ML,");
        queryDC.append("      	'RHQ', ");
        queryDC.append("      	TCEP.aEjercicioFiscal, ");
        queryDC.append("      	TCEP.cGrupoFuncional, ");
        queryDC.append("      	tCEP.cFuncion,	");
        queryDC.append("      	tCEP.cSubFuncion, ");
        queryDC.append("      	CASE WHEN tCEP.cProgramaGeneral IN ");
        queryDC.append("      		(SELECT cProgramaGeneral ");
        queryDC.append("      		   FROM tCat_ProGeneralPlurianual WITH (NOLOCK) )");
        queryDC.append("      	THEN '00'");
        queryDC.append("      	ELSE tCEP.cProgramaGeneral ");
        queryDC.append("      	END AS cProgramaGeneral, ");
        queryDC.append("      	 tCEP.cActividadInstitucional, ");
        queryDC.append("      	tCEP.cProgramaPresupuestario,	");
        queryDC.append("      	ltrim(substring(cpartida,1,1)) CCAP_157, ");
        queryDC.append("      	substring(cpartida,2,1) CCON_158, ");
        queryDC.append("      	substring(cpartida,3,1) CPARG_300, ");
        queryDC.append("      	substring(cpartida,4,2) CPAR_159, ");
        queryDC.append("      	tCEP.cTipoGasto,");
        queryDC.append("      	tCEP.cFuenteFinanciamiento,");
        queryDC.append("      	tCEP.cEntidadFederativa,");
        queryDC.append("      	SUBSTRING( dbo.CambiaEPCarteraMeta(TPDD.EP),45, 11) AS cCartera,");
        queryDC.append("      	'0000000000',");
        queryDC.append("      	'00' CCOP_163,");
        queryDC.append("      	'000' PL,");
        queryDC.append("      	'000' OFI,");
        queryDC.append("      	'00000' AUX1,");
        queryDC.append("      	'00000' AUX2,");
        queryDC.append("      	'0000000000' AUX3, ");
        queryDC.append("      	sum(CONVERT(decimal(17, 2), TPDD.mImporteNeto)) mImporteNeto,");
        queryDC.append("      	MONTH(GETDATE()) MES_149,");
        queryDC.append("      	'0' NRES, ");
        queryDC.append("      	CASE WHEN cpartida='35801' THEN 'GD' ELSE 'PN' END TIPO_CONTRATO,");
        queryDC.append("      	'000' CONC_MOV, ");
        queryDC.append("      	CONVERT(decimal(17, 2), sum(TPDD.mISRHonorarios + TPDD.mISRArrenda + TPDD.mimporteISRResico)) DCD_ISR,  ");
        queryDC.append("      	CONVERT(decimal(17, 2), sum(TPDD.m23Iva + mImporteIvaHonorarios + mImporteIvaArrenda  + TPDD.mImporteFlete23 + ISNULL(TPDD.mImporteIva6, 0) + ISNULL(TPDD.mImporteFlete4, 0))) DCD_IVA,  ");
        queryDC.append("      	CONVERT(decimal(17, 2), sum(TPDD.mObra5)) DCD_MIL5,  ");
        queryDC.append("      	CONVERT(decimal(17, 2), sum(TPDD.mCNIC + TPDD.mIMDT)) DCD_MIL2,  ");
        queryDC.append("      	CONVERT(decimal(17, 2), 0) DCD_CONTRIBUCION,  ");
        queryDC.append("      	CONVERT(decimal(17, 2), sum(ISNULL(TPDD.mRetImpuestoCedular, 0))) DCD_OTRAS_RET,  ");
        queryDC.append("      	CONVERT(decimal(17, 2), sum(TPDD.mPenalizacion + TPDD.mTesofe)) DCD_PENALIZACION, ");
        queryDC.append("      	'' id_ctr_intdet, ");
        queryDC.append("      	TPDD.nFolioPagoDirecto  ");
        queryDC.append("  FROM	tPagoDirectoDetalle TPDD  WITH(NOLOCK) ");
        queryDC.append("      	inner join tPagoDirectoEncabezado TPDE  WITH(NOLOCK)  ");
        queryDC.append("      		on TPDD.nFolioPagoDirecto = TPDE.nFolioPagoDirecto  ");
        queryDC.append("      	inner join tCatalogoEP TCEP  WITH(NOLOCK) ");
        queryDC.append("      		on rtrim(TPDD.EP) = rtrim(TCEP.EP)  ");
        queryDC.append(" where TPDD.nFolioPagoDirecto in (?)  ");
        queryDC.append(" group by TCEP.cRamo,  ");
        queryDC.append("      	TCEP.aEjercicioFiscal, ");
        queryDC.append("      	TCEP.cGrupoFuncional,  ");
        queryDC.append("      	tCEP.cFuncion,  ");
        queryDC.append("      	tCEP.cSubFuncion,  ");
        queryDC.append("      	cProgramaGeneral, ");
        queryDC.append("      	tCEP.cActividadInstitucional, ");
        queryDC.append("      	tCEP.cProgramaPresupuestario,  ");
        queryDC.append("      	cpartida,  ");
        queryDC.append("      	tCEP.cTipoGasto,  ");
        queryDC.append("       tCEP.cFuenteFinanciamiento,  ");
        queryDC.append("      	tCEP.cEntidadFederativa,  ");
        queryDC.append("      	SUBSTRING( dbo.CambiaEPCarteraMeta(TPDD.EP),45, 11), ");
        queryDC.append("      	ltrim(TPDD.ID_TIPO_CONCEPTO), ");
        queryDC.append("      	TPDD.nFolioPagoDirecto  ");
        queryDC.append("      	ORDER BY TPDD.nFolioPagoDirecto");
        return queryDC;
    }

    private static final StringBuilder genQueryPagosSICOP(String listaIds) {
        //Se genera el query para el encabezado del layout de SICOP SP
        // 02-02-2021 Se agrego el importe del IVA
        StringBuilder querySelectPagos = new StringBuilder();
        querySelectPagos.append("SELECT distinct tCE.nFolioPagoDirecto, ");
        querySelectPagos.append(" 'H' AS Header,");
        querySelectPagos.append(" CONVERT(nvarchar(10), tCE.fCarga,103), ");
        querySelectPagos.append(" CONVERT(nvarchar(10), tCE.fAplicacion,103), ");
        querySelectPagos.append(" tCE.cRamo,");
        querySelectPagos.append(" tCE.cRamo, ");
        querySelectPagos.append(" tCE.cRamo, ");
        querySelectPagos.append(" 'RHQ' UnidadResponsable,");
        querySelectPagos.append(" 'RHQ' UnidadResponsable,");
        querySelectPagos.append(" 'RHQ' UnidadResponsable, ");
        querySelectPagos.append(" 'N' ID_TIPO_MOVIMIENTO, ");
        querySelectPagos.append(" '5' AS OrigenPpto,");
        querySelectPagos.append(" '3' AS TipoSol, ");
        querySelectPagos.append(" 'MXN' TipoMoneda, ");
        querySelectPagos.append(" '1' TipoCambio, ");
        querySelectPagos.append(" '1' TIPO_PAGO, ");
        querySelectPagos.append(" 'PENDIENTE' AS CveLeyenda, ");
        querySelectPagos.append(" case cEstatus when 'Operada Pagada' then cpd.cCampoAdi3 else ISNULL(B.CBEN, '') end, ");
        querySelectPagos.append(" B.CBEN, ");
        querySelectPagos.append(" BCB.subCuentaBancaria CUENTA_BANCARIA, ");
        querySelectPagos.append(" case cEstatus when 'Operada Pagada' then cpd.cCampoAdi2 else rtrim(ISNULL(tce.cIdRFC, '')) end, ");
        querySelectPagos.append(" 'FAC', ");
        querySelectPagos.append(" '' FechaReferencia, ");
        querySelectPagos.append(" isnull(ta.nClaveAMF, isnull(nNumIdentificador,'')) Referencia1, ");
        querySelectPagos.append(" isnull(ta.numFolioAMF, '') Referencia2, ");
        querySelectPagos.append(" LEFT(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(LTRIM(RTRIM(tCE.cConcepto)), CHAR(160), ' '), '  ', ' ') , ',', ''), CHAR(10), ''),CHAR(9), ''),CHAR(13), ''), CHAR(34), '') , 70), ");
        querySelectPagos.append(" '' NotasReverso, ");
        querySelectPagos.append(" isnull(ta.nClaveAMF, '') AMF, ");
        querySelectPagos.append(" rtrim(tCE.caNoContrarrecibo) NO_ACMI, ");
        querySelectPagos.append(" rtrim(tCE.caNoContrarrecibo) AuxiliarComodin, ");
        querySelectPagos.append(" '' CTR , ");
        querySelectPagos.append(" CONVERT(decimal(17, 2), ISNULL(DC.DCD_ISR, 0)) ISR, ");
        querySelectPagos.append(" CONVERT(decimal(17, 2), ISNULL(DC.DCD_IVA, 0) + ISNULL(DC.DCD_CONTRIBUCION, 0)) RETIVA,");
        querySelectPagos.append(" CONVERT(decimal(17, 2), ISNULL(DC.DCD_MIL5, 0)) R5MILLAR, ");
        querySelectPagos.append(" CASE when DC.DCD_MIL2 = 0 or DC.DCD_MIL2 is null THEN '0' ELSE CONVERT(varchar(20), DC.DCD_MIL2) END R2MILLAS, ");
        querySelectPagos.append(" CASE when DC.DCD_OTRAS_RET = 0 or DC.DCD_OTRAS_RET is null THEN '0' ELSE CONVERT(varchar(20), DC.DCD_OTRAS_RET) end OTRASRET, ");
        querySelectPagos.append(" CASE when DC.DCD_PENALIZACION = 0 or DC.DCD_PENALIZACION is null THEN '0' else CONVERT(varchar (20),DC.DCD_PENALIZACION) end PENALIZA, ");
        querySelectPagos.append(" 0 CONTRIB, ");
        querySelectPagos.append(" CASE when DC.DCD_IVADES = 0 or DC.DCD_IVADES is null THEN '0' else CONVERT(varchar(20), DC.DCD_IVADES,0) end ivades, ");
        querySelectPagos.append(" '' IVAANT, ");
        querySelectPagos.append(" '' FOLIODC, ");
        querySelectPagos.append(" 'NA' ID_DESTINO_GASTO,  ");
        querySelectPagos.append(" isnull(cpd.cEstatus, ''), ");
        querySelectPagos.append(" isnull(cpd.cCampoAdi4, ''), ");
        querySelectPagos.append(" isnull(ta.nClaveAMF, '') ");
        querySelectPagos.append(" FROM tPagoDirectoEncabezado tCE ");
        querySelectPagos.append(" LEFT JOIN tBeneficiario B WITH (NOLOCK)  ON tce.cIdRFC = B.dRFC ");
        querySelectPagos.append(" LEFT JOIN tPagoAMF ta WITH (NOLOCK)  ON tCE.NumPagoAMF = ta.numPagoAMF ");
        querySelectPagos.append(" INNER JOIN tBeneficiarioCuentasBancarias BCB WITH (NOLOCK)  ON tCE.cIdRFC =	BCB.dRFC AND BCB.subCuentaBancaria = tCE.CTAB ");
        querySelectPagos.append(" LEFT JOIN pCatalogoTipoDocumento CTD WITH (NOLOCK)  ON tCE.cIdTipoDocumento = CTD.cIdTipoDocumento ");
        querySelectPagos.append(" INNER JOIN v_DCD_PAGO_DIRECTO DC WITH (NOLOCK) ON DC.nFolioPagoDirecto =tCE.nFolioPagoDirecto ");
        querySelectPagos.append(" LEFT JOIN tCadenasPDetalle cpd WITH (NOLOCK) ON cpd.caNoContrarrecibo = tCE.caNoContrarrecibo ");
        querySelectPagos.append(" WHERE tCE.nFolioPagoDirecto in (" + listaIds + ") ");
        querySelectPagos.append(" ORDER BY tCE.nFolioPagoDirecto ");
        return querySelectPagos;
    }

    public static EgresoEncabezado cargaEncabezado(Connection conn, int folioPagoDirecto) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT	cIdEntidadContable AS centroContable, ");
        query.append("       	cConcepto AS concepto, ");
        query.append("       	caNoContrarrecibo AS contrarecibo, ");
        query.append("       	CTAB AS CTAB, ");
        query.append("       	cDescripcionPoliza AS descripcionPoliza, ");
        query.append("       	cDocumentoHaplicado AS documentoAplicado, ");
        query.append("       	aEjercicioFiscal AS ejercicioFiscal, ");
        query.append("       	nEnviadoSICOP AS enviadoSICOP, ");
        query.append("       	cEsFirmaElectronica AS esFirmaElectronica, ");
        query.append("       	CONVERT( DATE, fAplicacion, 103) AS fechaAplicacion, ");
        query.append("       	CONVERT( DATE, fCancelacion, 103)  AS fechaCancelacion, ");
        query.append("       	CONVERT( DATE, fCarga, 103)  AS fechaCaptura, ");
        query.append("       	CONVERT( DATE, fCarga , 103) AS fechaCarga, ");
        query.append("       	CONVERT( DATE, fProgramadaPago, 103)  AS fechaProgramadaPago, ");
        query.append("       	CONVERT( DATE, fRevision, 103)  AS fechaRevision, ");
        query.append("       	sFirmanteAut AS firmanteAut, ");
        query.append("       	sFirmanteEla AS firmanteEla, ");
        query.append("       	sFirmanteVoBo AS firmanteVoBo, ");
        query.append("       	nFolioPagoDirecto AS folioPago, ");
        query.append("       	nFolioPoliza AS folioPoliza, ");
        query.append("       	nFolioPolizaCancelacion AS folioPolizaCancelacion, ");
        query.append("       	nIdConcepto AS idConcepto, ");
        query.append("       	ID_DESTINO_GASTO AS idDestinoGasto, ");
        query.append("       	nIDEstatus AS idEstatus, ");
        query.append("       	ID_TIPO_CONCEPTO AS idTipoConcepto, ");
        query.append("       	cIdTipoDocumento AS idTipoDocumento, ");
        query.append("       	cIdTipoFondo AS idTipoFondo, ");
        query.append("       	ID_TIPO_MOVIMIENTO AS idTipoMovimiento, ");
        query.append("       	mImporteBruto AS importeBruto, ");
        query.append("       	mImporteDescuento AS importeDescuento, ");
        query.append("       	mImporteIVA AS importeIVA, ");
        query.append("       	mImporteNeto AS importeNeto, ");
        query.append("       	mImporteRetencion AS importeRetencion, ");
        query.append("       	U_LOGIN AS login, ");
        query.append("       	nNumEmpleadoAut AS numEmpleadoAut, ");
        query.append("       	nNumEmpleadoElab AS numEmpleadoElab, ");
        query.append("       	nNumEmpleadoVoBo AS numEmpleadoVoBo, ");
        query.append("       	mOtrosImpuestos AS otrosImpuestos, ");
        query.append("       	sPuestoAut AS puestoAut, ");
        query.append("       	sPuestoEla AS puestoEla, ");
        query.append("       	sPuestoVoBo AS puestoVoBo, ");
        query.append("       	cRamo AS ramo, ");
        query.append("       	cIdRFC AS rfc, ");
        query.append("       	'PAGODIRECTO' AS tipoPago, ");
        query.append("       	cTipoPoliza AS tipoPoliza, ");
        query.append("       	cUnidadResponsable AS unidadResponsable, ");
        query.append("       	nFolioPagoDirecto AS folioPagoDirecto, ");
        query.append("       	cIdDocumento AS idDocumento, ");
        query.append("       	cIdTipoPagoDirecto AS idTipoPagoDirecto, ");
        query.append("       	mImportePenalizacion AS importePenalizacion ");
        query.append("   FROM	tPagoDirectoEncabezado WITH (NOLOCK) ");
        query.append("  WHERE	nFolioPagoDirecto = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //Ejecuta el query e instancia el Encabezado
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, folioPagoDirecto);
            rs = ps.executeQuery();
            Map<String, String> resultObj = RSToTable.rsToMapCaseSensitive(rs);
            EgresoPAGODIRECTOEncabezado encabezado = new EgresoPAGODIRECTOEncabezado();
            //Se convierte el formato de fecha
            DateConverter converter = new DateConverter(null);
            converter.setPatterns(new String[] { "dd/mm/yyyy", "yyyy-MM-dd" });
            ConvertUtils.register(converter, Date.class);
            BeanUtils.populate(encabezado, resultObj);
            EgresosManager.isAplica15D(conn, encabezado);
            return encabezado;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static ArrayList<StringBuilder> creaDocumentacionComprobatoria(Connection conn, String listaIds) throws Exception {
        ArrayList<StringBuilder> arrListaComp = new ArrayList<StringBuilder>();
        PreparedStatement pstmntH = null;
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        try {
            StringBuilder queryDet = PagosDirectosManager.generaQueryDCDet();
            log.debug("Query DC Detalle [" + queryDet + "]");
            pstmntD = conn.prepareStatement(queryDet.toString());
            StringBuilder query = new StringBuilder();
            query.append("SELECT ");
            query.append("      	nFolioPagoDirecto, ");
            query.append("      	'H' H,");
            query.append("      	cRamo,");
            query.append("      	'RHQ',");
            query.append("      	'' SOL_PAGO, ");
            query.append("      	'3', ");
            query.append("      	caNoContrarrecibo FOLIO_INTERNO, ");
            query.append("      	caNoContrarrecibo COMODIN ");
            query.append("  FROM	dbo.tPagoDirectoEncabezado (NOLOCK)");
            query.append(" WHERE	nFolioPagoDirecto in (").append(listaIds).append(" )");
            query.append("  ORDER BY nFolioPagoDirecto");
            pstmntH = conn.prepareStatement(query.toString());
            log.trace("Ejecutando\n[" + query.toString() + "]" + "\n[" + listaIds + "]");
            rs = pstmntH.executeQuery();
            while (rs.next()) {
                String nFolioCompromiso = rs.getString(1);
                StringBuilder encabezado = new StringBuilder();
                encabezado.append(rs.getString(2).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(3).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(4).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(5).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(6).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(7).trim());
                encabezado.append(",");
                encabezado.append(rs.getString(8).trim());
                encabezado.append("\r\n");
                arrListaComp.add(encabezado);
                log.trace("Ejecutando\n[" + queryDet + "]\n[" + nFolioCompromiso + "]");
                pstmntD.setString(1, nFolioCompromiso);
                rs2 = pstmntD.executeQuery();
                while (rs2.next()) {
                    String token = new String();
                    StringBuilder detalle = new StringBuilder();
                    for (int i = 1; i < 21; i++) {
                        detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                        token = ",";
                    }
                    token = "";
                    detalle.append("\r\n");
                    arrListaComp.add(detalle);
                }
                CloseObject.closeObject(rs2);
                pstmntD.clearParameters();
            }
            return arrListaComp;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rs2);
            CloseObject.closeObject(pstmntH);
            CloseObject.closeObject(pstmntD);
        }
    }

    private static StringBuilder generaQueryDCDet() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT DISTINCT PDE.cramo, ");
        query.append("                 Replace(Replace(pde.cIdDocumento, ',', ''), '\"', ''), ");
        query.append("                 CONVERT(NVARCHAR(10), pde.fAplicacion, 103), ");
        query.append("                 CONVERT(NVARCHAR(10), pde.fAplicacion, 103) ");
        query.append("                 + ' 12:00:00 a.m.', ");
        query.append("                 Replace(B.CBEN, ' ', '') DCD_CBEN, ");
        query.append("                 CASE ");
        query.append("                   WHEN B.cextranjero = 1 THEN '05' ");
        query.append("                   ELSE '04' ");
        query.append("                 END ");
        query.append("                 'TipoBen', ");
        query.append("                 '85' AS dcd_tipo_ope, ");
        query.append("                 '07' AS TIVA, ");
        query.append("                 CONVERT(DECIMAL(17, 2), 0.00), ");
        query.append("                 CONVERT(DECIMAL(17, 2), (pde.mImporteNeto + DCD_ISR + DCD_IVA + DCD_MIL5 + DCD_MIL2 + DCD_OTRAS_RET - DCD.dcd_ivades))  BRUTO, ");
        query.append("                 CONVERT(DECIMAL(17, 2), DCD.dcd_ivades)                     IVA, ");
        query.append("                 CONVERT(DECIMAL(17, 2), DCD.dcd_iva + DCD.dcd_contribucion) ");
        query.append("                 RETIVA, ");
        query.append("                 CONVERT(DECIMAL(17, 2), DCD.dcd_isr)                        ISR, ");
        query.append("                 CONVERT(DECIMAL(17, 2), DCD.dcd_mil5) ");
        query.append("                 R5MILLAR, ");
        query.append("                 CONVERT(DECIMAL(17, 2), DCD.dcd_mil2) ");
        query.append("                 R2MILLAS, ");
        query.append("                 CONVERT(DECIMAL(17, 2), DCD.dcd_otras_ret) ");
        query.append("                 OTRASRET, ");
        query.append("                 CONVERT(DECIMAL(17, 2), DCD.dcd_penalizacion) ");
        query.append("                 PENALIZA, ");
        query.append("                 CONVERT(DECIMAL(17, 2), 0) ");
        query.append("                 CONTRIB, ");
        query.append("                 0.00 AS dcd_ctoext, ");
        query.append("                 PDE.ciddocumento, ");
        query.append("                 Replace(Replace(pde.cConcepto, ',', ''), '\"', ''), ");
        query.append("                 pde.nfoliopagodirecto ");
        query.append(" FROM   dbo.tpagodirectoencabezado PDE (NOLOCK) ");
        query.append("        INNER JOIN dbo.v_DCD_PAGO_DIRECTO DCD  (NOLOCK)");
        query.append("                ON PDE.nFolioPagoDirecto = DCD.nFolioPagoDirecto ");
        query.append("        INNER JOIN [dbo].[tbeneficiario] B (NOLOCK) ");
        query.append("                ON PDE.cidrfc = B.drfc ");
        query.append(" WHERE  PDE.nfoliopagodirecto = ? ");
        query.append(" ORDER  BY PDE.nfoliopagodirecto ");
        return query;
    }

    public static boolean insertRegisterLayout(Connection conn, String clave, String cRamo, String cUnidadResponsable, String folioSICOP, String idProceso, String cCentroContable, java.sql.Date fExpedicion, float total, String cTipoPoliza, String nFolioPoliza, String nPolizaCancelacion, String tipoMovimiento, String origenPresupuesto, String cuentaBancaria, String noSolicitud, String tCambio, String tMoneda, String tSolicitud, String volante, String rfc, String caNoCompromiso, String codSemarnat2, String estatus, java.sql.Date fAplicacion, String documento, String nDocumento, String descripcion) throws SQLException {
        PreparedStatement pstmnt = null;
        boolean insertReg;
        StringBuilder queryInsert = new StringBuilder();
        queryInsert.append("INSERT INTO tLayoutCompromisos( cClave,cRamo,cUnidadCreadora,cFolioCompromisoSICOP,cIdProceso,");
        queryInsert.append("                              cCentroContable,fExpedicion,mImporte,cIdTipoPoliza,nPolizaSICOP,");
        queryInsert.append("                              nPolizaCancelacionSICOP,cIdTipoMovimiento,nOrigenPresupuesto,nCuentaBancaria,cNoSolicitud,");
        queryInsert.append("                              cTipoCambio,cMoneda,cTipoSolicitud,cVolante,dRFC,");
        queryInsert.append("                              caNoCompromiso,caNoSemarnat2,cEstatus,fAplicacionSICOP,tDocumento,nDocumento,cDescripcion)");
        queryInsert.append(" VALUES( '" + clave + "','" + cRamo + "','" + cUnidadResponsable + "','" + folioSICOP + "','" + idProceso + "'," + "" + "'" + cCentroContable + "','" + fExpedicion + "'," + total + ",'" + cTipoPoliza + "','" + nFolioPoliza + "'," + "" + "'" + nPolizaCancelacion + "','" + tipoMovimiento + "','" + origenPresupuesto + "','" + cuentaBancaria + "','" + noSolicitud + "'," + "" + "'" + tCambio + "','" + tMoneda + "','" + tSolicitud + "','" + volante + "','" + rfc + "'," + "" + "'" + caNoCompromiso + "','" + codSemarnat2 + "','" + estatus + "','" + fAplicacion + "','" + documento + "'," + "" + "'" + nDocumento + "','" + descripcion + "')");
        try {
            pstmnt = conn.prepareStatement(queryInsert.toString());
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

    public static int updateHeaderCompromisos(Connection conn, String listaIds) throws Exception {
        PreparedStatement pstmnt = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("UPDATE tPagoDirectoEncabezado SET nEnviadoSICOP = 1 WHERE nFolioPagoDirecto in (" + listaIds + ")");
            retval = pstmnt.executeUpdate();
            return retval;
        } finally {
            CloseObject.closeObject(pstmnt);
        }
    }

    public static boolean updateHeaderCompromisosRealimentacion(Connection conn, Integer nEnviadoSICOP, String caNoCompromiso) throws SQLException {
        PreparedStatement pstmntL = null;
        String queryUpdateEstatus = "UPDATE tCompromisoEncabezado SET nEnviadoSICOP = " + nEnviadoSICOP + "                             WHERE caNoCompromiso = '" + caNoCompromiso + "'";
        try {
            pstmntL = conn.prepareStatement(queryUpdateEstatus);
            pstmntL.executeUpdate();
            return true;
        } finally {
            CloseObject.closeObject(pstmntL);
        }
    }

    public static int UpdateStatus(Connection conn, String listaIds) throws SQLException {
        PreparedStatement pstmnt = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("UPDATE tPagoDirectoEncabezado SET nEnviadoSICOP = 2 WHERE nFolioPagoDirecto in (" + listaIds + ")");
            retval = pstmnt.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return retval;
    }

    private static final StringBuilder generaQueryInsertaLayout() {
        /* Aqui grabamos dentro de layouts creados encabezado */
        StringBuilder sqlInsertaLayoutGrabado = new StringBuilder();
        sqlInsertaLayoutGrabado.append("INSERT INTO tLayoutsCreadosHeader ");
        sqlInsertaLayoutGrabado.append("SELECT distinct getdate(),  ");
        sqlInsertaLayoutGrabado.append("tCE.nFolioPagoDirecto, ");
        sqlInsertaLayoutGrabado.append("'H' AS Header, ");
        sqlInsertaLayoutGrabado.append("CONVERT(nvarchar(10), tCE.fCarga,103), ");
        sqlInsertaLayoutGrabado.append("?,");
        sqlInsertaLayoutGrabado.append("tCE.cRamo,");
        sqlInsertaLayoutGrabado.append("tCE.cRamo,");
        sqlInsertaLayoutGrabado.append("tCE.cRamo, ");
        sqlInsertaLayoutGrabado.append("'RHQ' UnidadResponsable,");
        sqlInsertaLayoutGrabado.append("'RHQ' UnidadResponsable,");
        sqlInsertaLayoutGrabado.append("'RHQ' UnidadResponsable, ");
        sqlInsertaLayoutGrabado.append("'N' ID_TIPO_MOVIMIENTO, ");
        sqlInsertaLayoutGrabado.append("'1' AS OrigenPpto,");
        sqlInsertaLayoutGrabado.append("'3' AS TipoSol,");
        sqlInsertaLayoutGrabado.append("'MXN' TipoMoneda,");
        sqlInsertaLayoutGrabado.append("'1' TipoCambio,");
        sqlInsertaLayoutGrabado.append("'1' TIPO_PAGO,");
        sqlInsertaLayoutGrabado.append("?,");
        sqlInsertaLayoutGrabado.append("B.CBEN,");
        sqlInsertaLayoutGrabado.append("?,");
        sqlInsertaLayoutGrabado.append("rtrim(tCE.cIdRFC),");
        sqlInsertaLayoutGrabado.append("'FAC',");
        sqlInsertaLayoutGrabado.append("'' FechaReferencia, ");
        sqlInsertaLayoutGrabado.append("'' Referencia1, ");
        sqlInsertaLayoutGrabado.append("'' Referencia2, ");
        sqlInsertaLayoutGrabado.append("tCE.cConcepto, ");
        sqlInsertaLayoutGrabado.append("'' NotasReverso, ");
        sqlInsertaLayoutGrabado.append("'' AMF, ");
        sqlInsertaLayoutGrabado.append("rtrim(tCE.caNoContrarrecibo) NO_ACMI, ");
        sqlInsertaLayoutGrabado.append("rtrim(tCE.caNoContrarrecibo) AuxiliarComodin, ");
        sqlInsertaLayoutGrabado.append("'' CTR , ");
        sqlInsertaLayoutGrabado.append("'' FolioDC, ");
        sqlInsertaLayoutGrabado.append("CONVERT(decimal(17, 2), DC.DCD_ISR),");
        sqlInsertaLayoutGrabado.append("CONVERT(decimal(17, 2),DCD_IVA), ");
        sqlInsertaLayoutGrabado.append("CONVERT(decimal(17, 2),DC.DCD_MIL5), ");
        sqlInsertaLayoutGrabado.append("CONVERT(decimal(17, 2),DCD_MIL2), ");
        sqlInsertaLayoutGrabado.append("CONVERT(decimal(17, 2),DC.DCD_OTRAS_RET), ");
        sqlInsertaLayoutGrabado.append("CONVERT(decimal(17, 2),DC.DCD_PENALIZACION), ");
        sqlInsertaLayoutGrabado.append("CONVERT(decimal(17, 2),DC.DCD_CONTRIBUCION), ");
        sqlInsertaLayoutGrabado.append("CONVERT(decimal(17, 2),DC.DCD_IVADES),");
        sqlInsertaLayoutGrabado.append(" '0' IVAANT, ");
        sqlInsertaLayoutGrabado.append("tCE.ID_DESTINO_GASTO, ");
        sqlInsertaLayoutGrabado.append("?");
        sqlInsertaLayoutGrabado.append("  FROM tPagoDirectoEncabezado tCE WITH (NOLOCK)  ");
        sqlInsertaLayoutGrabado.append(" LEFT JOIN tBeneficiario B WITH (NOLOCK) ");
        sqlInsertaLayoutGrabado.append("    ON tce.cIdRFC = B.dRFC  ");
        sqlInsertaLayoutGrabado.append("INNER JOIN tBeneficiarioCuentasBancarias BCB WITH (NOLOCK)  ");
        sqlInsertaLayoutGrabado.append("    ON tCE.cIdRFC =	BCB.dRFC  and tce.cTAB = BCB.SUBCUENTABANCARIA");
        sqlInsertaLayoutGrabado.append(" LEFT JOIN pCatalogoTipoDocumento CTD WITH (NOLOCK)  ");
        sqlInsertaLayoutGrabado.append(" ON tCE.cIdTipoDocumento =	CTD.cIdTipoDocumento  ");
        sqlInsertaLayoutGrabado.append(" LEFT JOIN v_DCD_PAGO_DIRECTO DC WITH (NOLOCK)  ");
        sqlInsertaLayoutGrabado.append(" ON DC.nFolioPagoDirecto =tCE.nFolioPagoDirecto  ");
        sqlInsertaLayoutGrabado.append(" WHERE tCE.nFolioPagoDirecto = ?  ");
        return sqlInsertaLayoutGrabado;
    }

    public static void validarTotalLayout(Connection con, BigDecimal total, String listaIds) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        BigDecimal importe = new BigDecimal(0);
        String query = "SELECT SUM(mImporteNeto) FROM tPagoDirectoDetalle (NOLOCK) WHERE nFolioPagoDirecto IN (" + listaIds + ")";
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
        String query = "SELECT SUM(mISRHonorarios + mISRArrenda + mimporteISRResico + m23Iva + mImporteIvaHonorarios + mImporteIvaArrenda  + mImporteFlete23 + ISNULL(mImporteIva6, 0) + ISNULL(mImporteFlete4, 0) + mObra5 + mRetImpuestoCedular) totalRetenciones " + "        FROM tPagoDirectoDetalle (NOLOCK) WHERE nFolioPagoDirecto IN (" + listaIds + ")";
        try {
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();
            if (rs.next()) {
                importe = rs.getBigDecimal(1);
            }
            int resultado = total.compareTo(importe);
            if (resultado != 0) {
                throw new Exception("El importe de las Retenciones es diferente de la suma del detalle los pagos. Reporte al administrador.");
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pst);
        }
    }

    public static BigDecimal validarIvaPD(Connection con, BigDecimal total, int folio) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        BigDecimal importe = new BigDecimal(0).setScale(2, RoundingMode.HALF_UP);
        StringBuilder query = new StringBuilder();
        query.append(" SELECT SUM(importe) - (SELECT mImporteIVA FROM tPagoDirectoEncabezado WITH (NOLOCK) WHERE nFolioPagoDirecto = ? ) Importe  FROM ( ");
        query.append("	SELECT ROUND( mimportemasiva - (  mImporteMasIva /1.16 ), 2 ) importe ");
        query.append(" FROM tPagoDirectoDetalle WITH (NOLOCK) WHERE nFolioPagoDirecto = ? ) AS tbl");
        try {
            pst = con.prepareStatement(query.toString());
            pst.setInt(1, folio);
            pst.setInt(2, folio);
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

    public static File generaLayoutCompromisoPDNomina(Connection conn, Usuario usuario, String caNoCompromiso) throws Exception {
        String nombreArchivo = "PDRELGASTOS" + "_" + caNoCompromiso + ".csv";
        File archivoLayout = new File(new File(System.getProperty("java.io.tmpdir")), nombreArchivo);
        PrintWriter out = null;
        StringBuilder queryInformacionCompromiso = new StringBuilder();
        queryInformacionCompromiso.append("SELECT  PDCOmpromisoEnc.nFolioPDNominaCompromiso , PDCOmpromisoEnc.canocontrarrecibo , compromisoEnc.nFolioAutSICOP ,  PDCOmpromisoEnc.cCuentaBancaria , ");
        queryInformacionCompromiso.append("         PDCOmpromisoEnc.fProgramadaAutorizacion ,  PDCOmpromisoEnc.nLeyenda,   compromisoEnc.cIdContrato ");
        queryInformacionCompromiso.append("   FROM	tPDNominaCompromisoEncabezado PDCOmpromisoEnc WITH(NOLOCK) ");
        queryInformacionCompromiso.append(" 		INNER JOIN  tCompromisoEncabezado compromisoEnc WITH(NOLOCK) ");
        queryInformacionCompromiso.append(" 		ON PDCOmpromisoEnc.nFolioCompromiso = compromisoEnc.nFolioCompromiso ");
        queryInformacionCompromiso.append("  WHERE	canocontrarrecibo = ?");
        PreparedStatement psHeader = null;
        PreparedStatement psDetail = null;
        PreparedStatement psInformacionCompromiso = null;
        ResultSet rsInformacionCompromiso = null;
        int folioPDNominaCompromiso = -1;
        String folioAutSICOP = null;
        int leyenda = -1;
        String IDContratoCXP = null;
        String cuentaBancaria = null;
        try {
            psInformacionCompromiso = conn.prepareStatement(queryInformacionCompromiso.toString());
            psInformacionCompromiso.setString(1, caNoCompromiso);
            rsInformacionCompromiso = psInformacionCompromiso.executeQuery();
            if (rsInformacionCompromiso.next()) {
                folioPDNominaCompromiso = rsInformacionCompromiso.getInt("nFolioPDNominaCompromiso");
                folioAutSICOP = rsInformacionCompromiso.getString("nFolioAutSICOP");
                IDContratoCXP = rsInformacionCompromiso.getString("cIdContrato").trim();
                cuentaBancaria = rsInformacionCompromiso.getString("cCuentaBancaria");
                leyenda = rsInformacionCompromiso.getInt("nLeyenda");
            } else
                throw new Exception("No fue posible generar layout debido a que no se encontro informacion para el compromiso: " + caNoCompromiso);
            boolean layoutPrevioCreado = existeLayout(conn, IDContratoCXP);
            String CxPPDNomina = getCXPIntegradosCompromiso(conn, folioPDNominaCompromiso);
            String foliosPDNomina = getFoliosIntegradosCompromiso(conn, folioPDNominaCompromiso);
            String encabezadoLayout = generaEncabezadoLayoutCompPDNomina(conn, folioPDNominaCompromiso, cuentaBancaria, IDContratoCXP, CxPPDNomina, leyenda);
            String detalleLayout = generaDetalleLayoutCompPDNomina(conn, folioPDNominaCompromiso, folioAutSICOP);
            if (!layoutPrevioCreado) {
                insertaLayoutCompPDNominaEncabezado(conn, leyenda, cuentaBancaria, IDContratoCXP, CxPPDNomina, usuario.getLogin(), folioPDNominaCompromiso);
                insertaLayoutCompPDNominaDetalle(conn, foliosPDNomina, folioAutSICOP, folioPDNominaCompromiso);
            }
            out = new PrintWriter(archivoLayout);
            out.println(encabezadoLayout);
            out.print(detalleLayout);
            out.flush();
            out.close();
            out = null;
            return archivoLayout;
        } finally {
            CloseObject.closeObject(rsInformacionCompromiso);
            CloseObject.closeObject(psInformacionCompromiso);
            CloseObject.closeObject(psHeader);
            CloseObject.closeObject(psDetail);
        }
    }

    private static boolean existeLayout(Connection conn, String folioCxPPDCompromiso) throws Exception {
        String querySel = "SELECT	COUNT(*) AS Existe  FROM	tLayoutsCreadosHeader WITH(NOLOCK) WHERE	sAuxiliarComodin = ?";
        ResultSet rsSel = null;
        PreparedStatement psSel = null;
        try {
            psSel = conn.prepareStatement(querySel);
            psSel.setString(1, folioCxPPDCompromiso);
            rsSel = psSel.executeQuery();
            return rsSel.next() && rsSel.getInt(1) > 0;
        } finally {
            CloseObject.closeObject(rsSel);
            CloseObject.closeObject(psSel);
        }
    }

    private static boolean existeLayout(Connection conn, int folio) throws Exception {
        String querySel = "SELECT	COUNT(*) AS Existe  FROM	tLayoutsCreadosHeader WITH(NOLOCK) WHERE	nFolio = ?";
        ResultSet rsSel = null;
        PreparedStatement psSel = null;
        try {
            psSel = conn.prepareStatement(querySel);
            psSel.setInt(1, folio);
            rsSel = psSel.executeQuery();
            return rsSel.next() && rsSel.getInt(1) > 0;
        } finally {
            CloseObject.closeObject(rsSel);
            CloseObject.closeObject(psSel);
        }
    }

    private static String getCXPIntegradosCompromiso(Connection conn, int folioPDNominaCompromiso) throws Exception {
        String token = "";
        String cxp = "";
        String queryBuscaIntegradas = "SELECT	RTRIM( LTRIM( caNoContrarrecibo ) ) AS caNoContrarrecibo " + "  FROM	tPagoDirectoEncabezado WITH (NOLOCK)  " + "  WHERE	nFolioPagoDirecto IN ( SELECT	nFolioPagoDirecto  FROM	tPDNominaCompromisoDetalle WITH(NOLOCK) " + " 		 WHERE	nFolioPDNominaCompromiso = ? )";
        PreparedStatement psCXP = null;
        ResultSet rsCXP = null;
        try {
            psCXP = conn.prepareStatement(queryBuscaIntegradas);
            psCXP.setInt(1, folioPDNominaCompromiso);
            rsCXP = psCXP.executeQuery();
            while (rsCXP.next()) {
                cxp += token + rsCXP.getString("caNoContrarrecibo");
                token = " ";
            }
            return cxp;
        } finally {
            CloseObject.closeObject(rsCXP);
            CloseObject.closeObject(psCXP);
        }
    }

    private static String getFoliosIntegradosCompromiso(Connection conn, int folioPDNominaCompromiso) throws Exception {
        String token = "";
        String cxp = "";
        String queryBuscaIntegradas = "SELECT	nFolioPagoDirecto  FROM	tPagoDirectoEncabezado WITH (NOLOCK)  " + "  WHERE	nFolioPagoDirecto IN ( SELECT	nFolioPagoDirecto  FROM	tPDNominaCompromisoDetalle WITH(NOLOCK) " + " 		 WHERE	nFolioPDNominaCompromiso = ? )";
        PreparedStatement psCXP = null;
        ResultSet rsCXP = null;
        try {
            psCXP = conn.prepareStatement(queryBuscaIntegradas);
            psCXP.setInt(1, folioPDNominaCompromiso);
            rsCXP = psCXP.executeQuery();
            while (rsCXP.next()) {
                cxp += token + rsCXP.getString("nFolioPagoDirecto");
                token = " ";
            }
            return cxp;
        } finally {
            CloseObject.closeObject(rsCXP);
            CloseObject.closeObject(psCXP);
        }
    }

    private static String generaEncabezadoLayoutCompPDNomina(Connection conn, int folioRelacionGastosCompromiso, String ctaBancaria, String noIntegracion, String foliosPDNomina, int leyenda) throws Exception {
        StringBuilder queryEncabezadoLayout = new StringBuilder();
        // A
        queryEncabezadoLayout.append("SELECT top 1	1, " + "      	'H' AS Header, ");
        // B
        queryEncabezadoLayout.append("      	CONVERT(nvarchar(10), GETDATE(), 103) FECHA_EXP, ");
        // C
        queryEncabezadoLayout.append("      	CONVERT(nvarchar(10), GETDATE(), 103) FECHA_APL, ");
        // D
        queryEncabezadoLayout.append("      	tCE.cRamo ID_RAMO, ");
        // E
        queryEncabezadoLayout.append("      	tCE.cRamo ID_RAMO_CR, ");
        // F
        queryEncabezadoLayout.append("      	tCE.cRamo ID_RAMO_REC, ");
        // G
        queryEncabezadoLayout.append("      	'RHQ'  ID_UNIDAD, ");
        // H
        queryEncabezadoLayout.append("      	'RHQ'  ID_UNIDAD_CR, ");
        // I
        queryEncabezadoLayout.append("      	'RHQ'  ID_UNIDAD_REC, ");
        // J
        queryEncabezadoLayout.append("      	'N' ID_TIPO_MOVIMIENTO,  ");
        // K
        queryEncabezadoLayout.append("      	'4' AS OrigenPpto, ");
        // L
        queryEncabezadoLayout.append("      	'9' AS TipoSol, ");
        // M
        queryEncabezadoLayout.append("      	'MXN' TipoMoneda, ");
        // N
        queryEncabezadoLayout.append("      	'1' TipoCambio, ");
        // O
        queryEncabezadoLayout.append("      	'1' TIPO_PAGO, ");
        // P
        queryEncabezadoLayout.append("      	'" + leyenda + "' AS CVE_LEYENDA_66,");
        // Q /*CAMBIAR AL CBEN DEL PROVEEDOR*/
        queryEncabezadoLayout.append("      	CBEN, ");
        // R
        queryEncabezadoLayout.append("         '" + ctaBancaria.trim() + "' CUENTA_BANCARIA, ");
        // S
        queryEncabezadoLayout.append("      	tce.cIdRFC RFC_227, ");
        // T
        queryEncabezadoLayout.append("      	'FAC' TDOC_87, ");
        // U
        queryEncabezadoLayout.append("      	'' FechaReferencia, ");
        // V
        queryEncabezadoLayout.append("      	'' Referencia1, ");
        // W
        queryEncabezadoLayout.append("      	'' Referencia2, ");
        // X
        queryEncabezadoLayout.append("      	REPLACE(cDescripcionPoliza,',','') CPAG_76, ");
        // Y
        queryEncabezadoLayout.append("      	'' NotasReverso, ");
        // Z
        queryEncabezadoLayout.append("      	'' AMF, ");
        // AA
        queryEncabezadoLayout.append("      	rtrim(tCE.caNoContrarrecibo) NO_ACMI, ");
        // AB
        queryEncabezadoLayout.append("      	rtrim(tCE.caNoContrarrecibo) AuxiliarComodin, ");
        // AC
        queryEncabezadoLayout.append("      	'' CTR, ");
        // Ad
        queryEncabezadoLayout.append("      	CONVERT(decimal(17, 2), sum(DCD_ISR),0)  AS ISR_303,");
        // AE
        queryEncabezadoLayout.append("       	CONVERT(decimal(17, 2), sum(DCD_IVADES), 0) IVA_304, ");
        // AF
        queryEncabezadoLayout.append("       	CONVERT(decimal(17, 2), sum(DCD_MIL5),0) MIL5_305, ");
        // AG
        queryEncabezadoLayout.append("       	CONVERT(decimal(17, 2), sum(DCD_MIL2),0) MIL2_306, ");
        // AH
        queryEncabezadoLayout.append("       	CASE when DCD_OTRAS_RET = 0 or DCD_OTRAS_RET is null THEN '0' ELSE CONVERT(varchar(20), sum(DCD_OTRAS_RET)) end OTRASRET, ");
        // AI
        queryEncabezadoLayout.append("       	'0' PENALIZACION_308, ");
        // AJ
        queryEncabezadoLayout.append("       	'0' CONTRIBUCION_309, ");
        // AK
        queryEncabezadoLayout.append("       	CONVERT(decimal(17, 2), sum(DCD_IVA), 0) AS IVADES_310,	");
        // AL
        queryEncabezadoLayout.append("       	'' IVAANT_311, ");
        // AM
        queryEncabezadoLayout.append("      	'' FolioDC, ");
        // AN
        queryEncabezadoLayout.append("      	'NA' ID_DESTINO_GASTO,   ");
        // AO
        queryEncabezadoLayout.append("      	'' TTRANS_60, ");
        // AP
        queryEncabezadoLayout.append("      	'' ANT_CON_IVA_413");
        queryEncabezadoLayout.append("  FROM	tPagoDirectoEncabezado tCE WITH(NOLOCK) ");
        queryEncabezadoLayout.append(" 		LEFT JOIN tBeneficiario B WITH (NOLOCK)  ON tce.cIdRFC = B.dRFC ");
        queryEncabezadoLayout.append("      	inner join v_DCD_PAGO_DIRECTO dcd WITH (NOLOCK) ");
        queryEncabezadoLayout.append("      	on dcd.nFolioPagoDirecto = tce.nFolioPagoDirecto ");
        queryEncabezadoLayout.append(" WHERE	tCE.nFolioPagoDirecto IN (");
        queryEncabezadoLayout.append(" 		SELECT	nFolioPagoDirecto FROM	tPDNominaCompromisoDetalle WITH(NOLOCK)  WHERE	nFolioPDNominaCompromiso = ? )");
        queryEncabezadoLayout.append(" GROUP BY cRamo,  DCD_OTRAS_RET, CBEN, rtrim(tCE.caNoContrarrecibo), cDescripcionPoliza, tce.cIdRFC");
        PreparedStatement psHeader = null;
        ResultSet rsHeader = null;
        try {
            log.debug(queryEncabezadoLayout.toString());
            psHeader = conn.prepareStatement(queryEncabezadoLayout.toString());
            psHeader.setInt(1, folioRelacionGastosCompromiso);
            rsHeader = psHeader.executeQuery();
            if (rsHeader.next())
                return Util.resultSetToConcatenateString(rsHeader, ",", 1);
            else
                throw new Exception("No fue posible armar el encabezado del layout ya que no se retornaron registros de informacion");
        } finally {
            CloseObject.closeObject(rsHeader);
            CloseObject.closeObject(psHeader);
        }
    }

    @SuppressWarnings("unused")
    private static String generaDetalleLayoutCompPDNomina(Connection conn, int folioRelacionGastosCompromiso, String folioAutSICOP) throws Exception {
        StringBuilder queryLayoutDetalle = new StringBuilder();
        // A
        queryLayoutDetalle.append(" SELECT '1' ID_EVENTO,  	");
        // B
        queryLayoutDetalle.append(" '24.0.001' 	  EVENTO,  	");
        // C
        queryLayoutDetalle.append(" Substring(D.EP, 6, 2)  ID_RAMO_ML, ");
        // D
        queryLayoutDetalle.append(" 'RHQ',  					");
        // E
        queryLayoutDetalle.append(" Substring(D.EP, 1, 4)  aEjercicioFiscal,");
        // F
        queryLayoutDetalle.append(" Substring(D.EP, 13, 1) cGrupoFuncional, ");
        // G
        queryLayoutDetalle.append(" Substring(D.EP, 15, 1) cFuncion,  ");
        // H
        queryLayoutDetalle.append(" Substring(D.EP, 17, 2) cSubFuncion, ");
        // I
        queryLayoutDetalle.append(" CASE WHEN Substring(D.EP, 20, 2) IN (SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK)) THEN '00' ELSE Substring(D.EP, 20, 2) END AS cProgramaGeneral, ");
        // J
        queryLayoutDetalle.append(" Substring(D.EP, 23, 3) cActividadInstitucional, ");
        // K
        queryLayoutDetalle.append(" Substring(D.EP, 27, 4) cProgramaPresupuestario,");
        // L
        queryLayoutDetalle.append(" Substring(D.EP, 32, 1) CCAP_157,  ");
        // M
        queryLayoutDetalle.append(" Substring(D.EP, 33, 1) CCON_158,  ");
        // N
        queryLayoutDetalle.append(" Substring(D.EP, 34, 1) CPARG_300, ");
        // O
        queryLayoutDetalle.append(" Substring(D.EP, 35, 2) CPAR_159,  ");
        // P
        queryLayoutDetalle.append(" Substring(D.EP, 38, 1) cTipoGasto,");
        // Q
        queryLayoutDetalle.append(" Substring(D.EP, 40, 1) cFuenteFinanciamiento,");
        // R
        queryLayoutDetalle.append(" Substring(D.EP, 42, 2) cEntidadFederativa, ");
        // S
        queryLayoutDetalle.append(" SUBSTRING( dbo.CambiaEPCarteraMeta(D.EP),45, 11) AS cCartera,");
        // T
        queryLayoutDetalle.append(" '0000000000' AS CCAU_162,  		");
        // U
        queryLayoutDetalle.append(" '00'                   CCOP_163,  	");
        // V
        queryLayoutDetalle.append(" '000'                  PL,  		");
        // W
        queryLayoutDetalle.append(" '000'                  OFI,  		");
        // X
        queryLayoutDetalle.append(" '00000'                AUX1,  		");
        // Y
        queryLayoutDetalle.append(" '00000'                AUX2,  		");
        // Z
        queryLayoutDetalle.append(" '0000000000'           AUX3,  		");
        // AA
        queryLayoutDetalle.append(" '" + StringUtils.trimToEmpty(folioAutSICOP) + "' AS NCOM_35,");
        // AB
        queryLayoutDetalle.append(" Sum(D.mImporteNeto) AS MONTO,  ");
        // AC
        queryLayoutDetalle.append(" D.cmes                 MES_149,  	");
        // AD
        queryLayoutDetalle.append(" '0'                    NRES,  		");
        // AE
        queryLayoutDetalle.append(" CASE WHEN Substring(ep, 32, 5) = '35801' THEN 'GD' ELSE 'PN' END TIPO_CONTRATO, ");
        // AF
        queryLayoutDetalle.append(" '000'                  CONC_MOV,  	");
        // AG
        queryLayoutDetalle.append(" CONVERT(decimal(17, 2),sum(mimporteISRResico + mISRArrenda + mISRHonorarios+ mISROtros + mImporteISRLaudos),0)  AS RETENCIONES, ");
        // AH
        queryLayoutDetalle.append(" CONVERT(decimal(17, 2),sum(mImporteIvaArrenda + mImporteIvaHonorarios + mImporteFlete23+  mImporteFlete4),0) IVA_43,  	");
        // AI
        queryLayoutDetalle.append(" CONVERT(decimal(17, 2),sum(isnull(mObra5, 0) + isnull(mImporteObra,0))) MIL5_44,  						");
        // AJ
        queryLayoutDetalle.append(" CONVERT(decimal(17, 2),sum(isnull(m2Millar,0))) MIL2_312,  						");
        // AK
        queryLayoutDetalle.append(" 0 CONTRIBUCION_48,  				");
        // AL
        queryLayoutDetalle.append(" CONVERT(decimal(17, 2),sum(isnull(mRetImpuestoCedular,0))) OTRAS_RET_313, ");
        // AM
        queryLayoutDetalle.append(" 0 IVADES_45,  						");
        // AN
        queryLayoutDetalle.append(" 0 ANTICIPO_46,  					");
        // AO
        queryLayoutDetalle.append(" 0 PENALIZACION_314,  				");
        // AP
        queryLayoutDetalle.append(" 0 IVAANT_47,  						");
        // AQ
        queryLayoutDetalle.append(" ''  id_ctr_intdet  				");
        queryLayoutDetalle.append(" FROM   tPagoDirectoDetalle D WITH (NOLOCK)  ");
        queryLayoutDetalle.append(" WHERE  d.nFolioPagoDirecto in (SELECT	nFolioPagoDirecto FROM	tPDNominaCompromisoDetalle WITH(NOLOCK)  WHERE	nFolioPDNominaCompromiso = ? ) ");
        queryLayoutDetalle.append(" GROUP  BY Substring(D.EP, 61, 3),  D.EP, D.cmes,mISRHonorarios,  D.ID_TIPO_CONCEPTO, SUBSTRING( dbo.CambiaEPCarteraMeta(D.EP),45, 11)");
        PreparedStatement psLayoutDetalle = null;
        ResultSet rsLayoutDetalle = null;
        try {
            psLayoutDetalle = conn.prepareStatement(queryLayoutDetalle.toString());
            psLayoutDetalle.setInt(1, folioRelacionGastosCompromiso);
            rsLayoutDetalle = psLayoutDetalle.executeQuery();
            if (rsLayoutDetalle.next()) {
                String detalle = Util.resultSetToConcatenateString(rsLayoutDetalle, ",", 0);
                return detalle;
            } else
                throw new Exception("No se encontro informacion para armar el detalle con folio de compromiso :" + folioRelacionGastosCompromiso);
        } finally {
            CloseObject.closeObject(psLayoutDetalle);
            CloseObject.closeObject(rsLayoutDetalle);
        }
    }

    private static void insertaLayoutCompPDNominaEncabezado(Connection conn, int leyenda, String ctaBancaria, String noIntegracion, String foliosRGIntegrados, String usuario, int folioRelacionGastosCompromiso) throws Exception {
        StringBuilder queryInsertaLayoutEnc = new StringBuilder();
        queryInsertaLayoutEnc.append("INSERT INTO  tLayoutsCreadosHeader(fCreacionLayout, nFolio, Header, fCarga, fAplicacion, sRamo, sRamo1, sRamo2, sUnidadResponsable, sUnidadResponsable2, sUnidadResponsable3, sTipoMovimiento, sOrigenPpto, sTipoSol, sTipoMoneda, sTipoCambio, sTIPO_PAGO, sCveLeyenda, sCBEN, sCUENTA_BANCARIA, sIdRFC, sFAC, fFechaReferencia, sReferencia1, sReferencia2, sConcepto, sNotasReverso, sAMF, sNoContrarrecibo, sAuxiliarComodin, sCTR, sFolioDC, mISR, mIVA, mMil5, mMil2, mOtrasRet, mPenalizacion, mContribucion, mIvaDes, mIvaAnt, sDestinoGasto, sLogin)");
        // A
        queryInsertaLayoutEnc.append("SELECT   getdate(), tCE.nFolioPagoDirecto, 'H' AS Header, ");
        // B
        queryInsertaLayoutEnc.append("     	CONVERT(nvarchar(10), tCE.fAplicacion,103) FECHA_EXP, ");
        // C
        queryInsertaLayoutEnc.append("     	CONVERT(nvarchar(10), tCE.fAplicacion,103) FECHA_APL, ");
        // D
        queryInsertaLayoutEnc.append("     	tCE.cRamo ID_RAMO, ");
        // E
        queryInsertaLayoutEnc.append("     	tCE.cRamo ID_RAMO_CR, ");
        // F
        queryInsertaLayoutEnc.append("     	tCE.cRamo ID_RAMO_REC, ");
        // G
        queryInsertaLayoutEnc.append("     	'RHQ'  ID_UNIDAD, ");
        // H
        queryInsertaLayoutEnc.append("     	'RHQ'  ID_UNIDAD_CR, ");
        // I
        queryInsertaLayoutEnc.append("     	'RHQ'  ID_UNIDAD_REC, ");
        // J
        queryInsertaLayoutEnc.append("     	'N' ID_TIPO_MOVIMIENTO,  ");
        // K
        queryInsertaLayoutEnc.append("     	'1' AS OrigenPpto, ");
        // L
        queryInsertaLayoutEnc.append("     	'3' AS TipoSol, ");
        // M
        queryInsertaLayoutEnc.append("     	'MXN' TipoMoneda, ");
        // N
        queryInsertaLayoutEnc.append("     	'1' TipoCambio, ");
        // O
        queryInsertaLayoutEnc.append("     	'1' TIPO_PAGO, ");
        // P
        queryInsertaLayoutEnc.append("     	'" + leyenda + "' AS CVE_LEYENDA_66,");
        // Q
        queryInsertaLayoutEnc.append("     	CBEN, ");
        // R
        queryInsertaLayoutEnc.append("         '" + ctaBancaria.trim() + "' CUENTA_BANCARIA, ");
        // S
        queryInsertaLayoutEnc.append("     	dRFC RFC_227, ");
        // T
        queryInsertaLayoutEnc.append("     	'FAC' TDOC_87, ");
        // U
        queryInsertaLayoutEnc.append("     	'' FechaReferencia, ");
        // V
        queryInsertaLayoutEnc.append("     	'' Referencia1, ");
        // W
        queryInsertaLayoutEnc.append("     	'' Referencia2, ");
        // X
        queryInsertaLayoutEnc.append("     	LEFT(cConcepto,500) CPAG_76, ");
        // Y
        queryInsertaLayoutEnc.append("      	'' NotasReverso, ");
        // Z
        queryInsertaLayoutEnc.append("     	'' AMF, ");
        // AA
        queryInsertaLayoutEnc.append("      	tCE.canocontrarrecibo NO_ACMI, ");
        // AB
        queryInsertaLayoutEnc.append("      	'" + noIntegracion + "' AuxiliarComodin, ");
        // AC
        queryInsertaLayoutEnc.append("     	'' CTR, ");
        // AD
        queryInsertaLayoutEnc.append("     	'' FolioDC, ");
        // AE
        queryInsertaLayoutEnc.append("     	CONVERT(decimal(17, 2),SUM(DCD_ISR),0)  AS ISR_303, ");
        // AF
        queryInsertaLayoutEnc.append("      	CONVERT(decimal(17, 2), SUM(DCD_IVA), 0) IVA_304, ");
        // AG
        queryInsertaLayoutEnc.append("      	CONVERT(decimal(17, 2),0) MIL5_305, ");
        // AH
        queryInsertaLayoutEnc.append("      	CONVERT(decimal(17, 2),0) MIL2_306, ");
        // AI
        queryInsertaLayoutEnc.append("      	0 mImporteRetencion, ");
        // AJ
        queryInsertaLayoutEnc.append("      	CONVERT(decimal(17, 2),0) PENALIZACION_308, ");
        // AK
        queryInsertaLayoutEnc.append("      	CONVERT(decimal(17, 2), 0) CONTRIBUCION_309, ");
        // AL
        queryInsertaLayoutEnc.append("      	CONVERT(decimal(17, 2), SUM(DCD_IVADES), 0) AS IVADES_310,	");
        // AM
        queryInsertaLayoutEnc.append("      	0 IVAANT_311, ");
        // AN
        queryInsertaLayoutEnc.append("     	ID_DESTINO_GASTO,   ");
        queryInsertaLayoutEnc.append("      	'" + usuario + "'");
        queryInsertaLayoutEnc.append("  FROM	tPagoDirectoEncabezado tCE (NOLOCK)");
        queryInsertaLayoutEnc.append("  inner join v_DCD_PAGO_DIRECTO dcd WITH (NOLOCK) ");
        queryInsertaLayoutEnc.append("			on dcd.nFolioPagoDirecto  = tCE.nFolioPagoDirecto  ");
        queryInsertaLayoutEnc.append("  JOIN tBeneficiario BEN WITH (NOLOCK) ");
        queryInsertaLayoutEnc.append("			ON tCE.cIdRFC = BEN.dRFC ");
        queryInsertaLayoutEnc.append(" WHERE	tCE.nFolioPagoDirecto  in ( SELECT	nFolioPagoDirecto  ");
        queryInsertaLayoutEnc.append(" 		  FROM	tPDNominaCompromisoDetalle WITH(NOLOCK) ");
        queryInsertaLayoutEnc.append(" 		 WHERE	nFolioPDNominaCompromiso = ? )");
        queryInsertaLayoutEnc.append(" Group by tCE.nFolioPagoDirecto , tce.fAplicacion, tce.cramo, tCE.canocontrarrecibo, CBEN, dRFC, cConcepto, ID_DESTINO_GASTO ");
        PreparedStatement psInsertaEncabezadoLayout = null;
        try {
            psInsertaEncabezadoLayout = conn.prepareStatement(queryInsertaLayoutEnc.toString());
            psInsertaEncabezadoLayout.setInt(1, folioRelacionGastosCompromiso);
            psInsertaEncabezadoLayout.executeUpdate();
        } finally {
            CloseObject.closeObject(psInsertaEncabezadoLayout);
        }
    }

    private static void insertaLayoutCompPDNominaDetalle(Connection conn, String foliosPDNomina, String folioAutSICOP, int folioRelacionGastosCompromiso) throws Exception {
        StringBuilder queryInsertLayoutCompRG = new StringBuilder();
        queryInsertLayoutCompRG.append("INSERT INTO tLayoutsCreadosDetalle(nFolilo, sID_EVENTO, sEVENTO, sID_RAMO_ML, sUnidadResponsable, saEjercicioFiscal, sGrupoFuncional, sFuncion, sSubFuncion, sProgramaGeneral, ");
        queryInsertLayoutCompRG.append(" 		sActividadInstitucional, sProgramaPresupuestario, sCCAP_157, sCCON_158, sCPARG_300, sCPAR_159, sTipoGasto, sFuenteFinanciamiento, sEntidadFederativa, sCartera, sUnidadEjecutora2, sCCOP_163, sPL, sOFI, sAUX1, sAUX2, sAUX3, NCOM_35, ");
        queryInsertLayoutCompRG.append(" 		mImporteNeto, nMES_149, sNRES, sTIPO_CONTRATO, sCONC_MOV, mISR, mIVA, mMil5, mMil2, mContribucion, mOtrasRet, IVADES_45, ANTICIPO_46, mPenalizacion, IVAANT_47, sid_ctr_intdet)");
        // A
        queryInsertLayoutCompRG.append(" SELECT " + foliosPDNomina + " nFolio, '1'                    ID_EVENTO, ");
        // B
        queryInsertLayoutCompRG.append("       '24.0.001'             EVENTO, ");
        // C
        queryInsertLayoutCompRG.append("       Substring(D.EP, 6, 2)  ID_RAMO_ML, ");
        // D
        queryInsertLayoutCompRG.append("       'RHQ', ");
        // E
        queryInsertLayoutCompRG.append("       Substring(D.EP, 1, 4)  aEjercicioFiscal, ");
        // F
        queryInsertLayoutCompRG.append("       Substring(D.EP, 13, 1) cGrupoFuncional, ");
        // G
        queryInsertLayoutCompRG.append("       Substring(D.EP, 15, 1) cFuncion, ");
        // H
        queryInsertLayoutCompRG.append("       Substring(D.EP, 17, 2) cSubFuncion, ");
        // I
        queryInsertLayoutCompRG.append("       CASE WHEN Substring(D.EP, 20, 2) IN (SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK)) THEN '00' ELSE Substring(D.EP, 20, 2)  END   AS cProgramaGeneral, ");
        // J
        queryInsertLayoutCompRG.append("       Substring(D.EP, 23, 3) cActividadInstitucional, ");
        // K
        queryInsertLayoutCompRG.append("       Substring(D.EP, 27, 4) cProgramaPresupuestario, ");
        // L
        queryInsertLayoutCompRG.append("       Substring(D.EP, 32, 1) CCAP_157, ");
        // M
        queryInsertLayoutCompRG.append("       Substring(D.EP, 33, 1) CCON_158, ");
        // N
        queryInsertLayoutCompRG.append("       Substring(D.EP, 34, 1) CPARG_300, ");
        // O
        queryInsertLayoutCompRG.append("       Substring(D.EP, 35, 2) CPAR_159, ");
        // P
        queryInsertLayoutCompRG.append("       Substring(D.EP, 38, 1) cTipoGasto, ");
        // Q
        queryInsertLayoutCompRG.append("       Substring(D.EP, 40, 1) cFuenteFinanciamiento, ");
        // R
        queryInsertLayoutCompRG.append("       Substring(D.EP, 42, 2) cEntidadFederativa, ");
        // S
        queryInsertLayoutCompRG.append("       SUBSTRING( dbo.CambiaEPCarteraMeta(D.EP),45, 11) AS cCartera, ");
        // T
        queryInsertLayoutCompRG.append("       ltrim('0000000' + tCEP.cUnidadEjecutora) AS CCAU_162, ");
        // U
        queryInsertLayoutCompRG.append("       substring(TCEP.cUnidadNorativa,2,2) AS CCOP_163, ");
        // V
        queryInsertLayoutCompRG.append("       '000'                  PL, ");
        // W
        queryInsertLayoutCompRG.append("       '000'                  OFI, ");
        // X
        queryInsertLayoutCompRG.append("       '00000'                AUX1, ");
        // Y
        queryInsertLayoutCompRG.append("       '00000'                AUX2, ");
        // Z
        queryInsertLayoutCompRG.append("       '0000000000'           AUX3, ");
        // AA
        queryInsertLayoutCompRG.append("       '" + StringUtils.trimToEmpty(folioAutSICOP) + "' AS NCOM_35,");
        // AB
        queryInsertLayoutCompRG.append("       SUM(D.mImporteNeto) AS MONTO, ");
        // AC
        queryInsertLayoutCompRG.append("       D.cmes                 MES_149, ");
        // AD
        queryInsertLayoutCompRG.append("       '0'                    NRES, ");
        // AE
        queryInsertLayoutCompRG.append("       CASE WHEN Substring(d.ep, 32, 5) = '35801' THEN 'GD' ELSE 'PN' END  TIPO_CONTRATO, ");
        // AF
        queryInsertLayoutCompRG.append("       '000'                  CONC_MOV, ");
        // AG
        queryInsertLayoutCompRG.append("       CONVERT(decimal(17, 2),ISNULL(SUM(mimporteISRResico + mImporteISRLaudos + mISROtros + mISRArrenda + mISRHonorarios),0),0)    AS RETENCIONES, ");
        // AH
        queryInsertLayoutCompRG.append("       CONVERT(decimal(17, 2),SUM(mimporteivaArrenda + mimporteivahonorarios + mimporteflete23 + mImporteFlete4 ),0) IVA_43, ");
        // AI
        queryInsertLayoutCompRG.append("       0 MIL5_44, ");
        // AJ
        queryInsertLayoutCompRG.append("       0 MIL2_312, ");
        // AK
        queryInsertLayoutCompRG.append("       0 CONTRIBUCION_48, ");
        // AL
        queryInsertLayoutCompRG.append("       0 OTRAS_RET_313, ");
        // AM
        queryInsertLayoutCompRG.append("       0 IVADES_45, ");
        // AN
        queryInsertLayoutCompRG.append("       0 ANTICIPO_46, ");
        // AO
        queryInsertLayoutCompRG.append("       0 PENALIZACION_314, ");
        // AP
        queryInsertLayoutCompRG.append("       0 IVAANT_47, ");
        // AQ
        queryInsertLayoutCompRG.append("       ''  id_ctr_intdet ");
        queryInsertLayoutCompRG.append(" FROM  tPagoDirectoDetalle D WITH (NOLOCK) ");
        queryInsertLayoutCompRG.append(" inner join tCatalogoEP TCEP on D.EP = TCEP.EP ");
        queryInsertLayoutCompRG.append(" WHERE  d.nFolioPagoDirecto in ( SELECT	nFolioPagoDirecto FROM	tPDNominaCompromisoDetalle WITH(NOLOCK)  WHERE	nFolioPDNominaCompromiso = ? )");
        queryInsertLayoutCompRG.append(" GROUP  BY Substring(D.EP, 61, 3),  D.EP,  D.cmes,     D.ID_TIPO_CONCEPTO, SUBSTRING( dbo.CambiaEPCarteraMeta(D.EP),45, 11), substring(TCEP.cUnidadNorativa,2,2), tCEP.cUnidadEjecutora ");
        PreparedStatement psInsertLayoutCompRG = null;
        try {
            psInsertLayoutCompRG = conn.prepareStatement(queryInsertLayoutCompRG.toString());
            psInsertLayoutCompRG.setInt(1, folioRelacionGastosCompromiso);
            psInsertLayoutCompRG.executeUpdate();
        } finally {
            CloseObject.closeObject(psInsertLayoutCompRG);
        }
    }

    public static String buscaCXP(Connection conn, String caNoCompromiso) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String CXP = null;
        String query = "SELECT  cIdContrato FROM tCompromisoEncabezado (NOLOCK) WHERE caNoCompromiso = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, caNoCompromiso);
            rs = ps.executeQuery();
            if (rs.next())
                CXP = rs.getString(1);
            return CXP;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static File generaDocComp(Connection conn, String Integradora) throws Exception {
        String nombreArchivo = "DC" + "_" + Integradora.trim() + ".csv";
        File archivoLayout = new File(new File(System.getProperty("java.io.tmpdir")), nombreArchivo);
        String encabezadoLayout = null;
        PrintWriter out = null;
        String queryEncabezadoLayout = "SELECT nFolioPagoDirecto, 'H' H," + "cRamo, " + "'RHQ' UR, " + " '' SOL_PAGO," + " '3' NO_SOL, " + " caNoContrarrecibo FOLIO_INTERNO," + " caNoContrarrecibo COMODIN " + " FROM	dbo.tPagoDirectoEncabezado WITH(NOLOCK)" + "WHERE	caNoContrarrecibo = ? ";
        PreparedStatement psHeader = null;
        ResultSet rsHeader = null;
        try {
            log.debug(queryEncabezadoLayout);
            psHeader = conn.prepareStatement(queryEncabezadoLayout);
            psHeader.setString(1, Integradora.trim());
            rsHeader = psHeader.executeQuery();
            if (rsHeader.next())
                encabezadoLayout = Util.resultSetToConcatenateString(rsHeader, ",", 1);
            else
                throw new Exception("No fue posible armar el encabezado del layout ya que no se retornaron registros de informacion");
            String detalleLayout = generaDetalleDC(conn, Integradora.trim());
            out = new PrintWriter(archivoLayout);
            out.println(encabezadoLayout);
            out.print(detalleLayout);
            out.flush();
            out.close();
            out = null;
            return archivoLayout;
        } catch (Exception c) {
            throw new Exception("No se encontro informacion para armar el detalle con folio de compromiso :" + Integradora);
        } finally {
            CloseObject.closeObject(rsHeader);
            CloseObject.closeObject(psHeader);
        }
    }

    private static String generaDetalleDC(Connection conn, String Integradora) throws Exception {
        StringBuilder queryLayoutDetalle = new StringBuilder();
        queryLayoutDetalle.append("	SELECT DISTINCT PDE.cramo, ");
        queryLayoutDetalle.append(" Replace(Replace(pde.caNoContrarrecibo, ',', ''), '\"', '') INTEGRACION, ");
        queryLayoutDetalle.append(" CONVERT(NVARCHAR(10), pde.fAplicacion, 103) FECHA ,");
        queryLayoutDetalle.append(" CONVERT(NVARCHAR(10), pde.fAplicacion, 103) + ' 12:00:00 a.m.' FECHAAPL, ");
        queryLayoutDetalle.append(" CBEN DCD_CBEN,");
        queryLayoutDetalle.append(" '04'   'TipoBen',");
        queryLayoutDetalle.append(" '85' AS dcd_tipo_ope,");
        queryLayoutDetalle.append(" '07' AS TIVA,");
        queryLayoutDetalle.append(" CONVERT(DECIMAL(17, 2), 0.00) TASA,");
        queryLayoutDetalle.append(" CONVERT(DECIMAL(17, 2), (select sum(mimportemasiva) from tPagoDirectoDetalle");
        queryLayoutDetalle.append(" where nFolioPagoDirecto = pde.nFolioPagoDirecto) + sum(DCD.mISR) + sum(DCD.mIVA) - sum(DCD.mIVADes)) BRUTO,");
        queryLayoutDetalle.append(" CONVERT(DECIMAL(17, 2), sum(DCD.mIvaDes) )                     IVA,");
        queryLayoutDetalle.append(" CONVERT(DECIMAL(17, 2), sum(DCD.mIVA + DCD.mContribucion))  RETIVA,");
        queryLayoutDetalle.append(" CONVERT(DECIMAL(17, 2), sum(DCD.mISR))                ISR,");
        queryLayoutDetalle.append(" CONVERT(DECIMAL(17, 2), sum(DCD.mMil5))               R5MILLAR,");
        queryLayoutDetalle.append(" CONVERT(DECIMAL(17, 2), sum(DCD.mMil2))               R2MILLAS,");
        queryLayoutDetalle.append(" CONVERT(DECIMAL(17, 2), sum (DCD.mOtrasRet) )          OTRASRET,");
        queryLayoutDetalle.append(" CONVERT(DECIMAL(17, 2), sum(DCD.mPenalizacion))       PENALIZA,");
        queryLayoutDetalle.append(" CONVERT(DECIMAL(17, 2), 0)		                 CONTRIB,");
        queryLayoutDetalle.append(" 0.00 AS dcd_ctoext,");
        queryLayoutDetalle.append(" Replace(Replace(pde.caNoContrarrecibo, ',', ''), '\"', '') ");
        queryLayoutDetalle.append(" FROM   dbo.tPagoDirectoEncabezado PDE");
        queryLayoutDetalle.append("        INNER JOIN dbo.tLayoutsCreadosHeader DCD");
        queryLayoutDetalle.append("                ON sAuxiliarComodin = caNoContrarrecibo   ");
        queryLayoutDetalle.append("        LEFT JOIN tBeneficiario B WITH (NOLOCK)  ON PDE.cIdRFC = B.dRFC ");
        queryLayoutDetalle.append(" WHERE  caNoContrarrecibo = '" + Integradora.trim() + "'");
        queryLayoutDetalle.append(" group by PDE.cramo, pde.caNoContrarrecibo, pde.fAplicacion, pde.nFolioPagoDirecto, CBEN ");
        PreparedStatement psLayoutDetalle = null;
        ResultSet rsLayoutDetalle = null;
        try {
            log.debug("Generando Detalle Integracion (LAYOUT) Query: " + queryLayoutDetalle);
            psLayoutDetalle = conn.prepareStatement(queryLayoutDetalle.toString());
            rsLayoutDetalle = psLayoutDetalle.executeQuery();
            if (rsLayoutDetalle.next()) {
                String detalle = Util.resultSetToConcatenateString(rsLayoutDetalle, ",", 0);
                log.debug("Detalle para DC de la integracion " + Integradora + "\n" + detalle);
                return detalle;
            } else
                throw new Exception("No se encontro informacion para armar el detalle con folio de compromiso :" + Integradora);
        } finally {
            CloseObject.closeObject(psLayoutDetalle);
            CloseObject.closeObject(rsLayoutDetalle);
        }
    }

    public static ArrayList<StringBuilder> generaLayoutCompromisoPD(Connection conn, Usuario usuario, String folio, String cuentaBancaria, String fecha, String leyenda) throws Exception {
        ArrayList<StringBuilder> arrListaComp = new ArrayList<StringBuilder>();
        PreparedStatement psHeader = null;
        PreparedStatement psDetail = null;
        PreparedStatement pstmntHLayout = null;
        PreparedStatement pstmntHLayoutDet = null;
        ResultSet rs = null, rs2 = null;
        String[] arrFolios = folio.split(",");
        BigDecimal total = new BigDecimal(0);
        BigDecimal retenciones = new BigDecimal(0);
        BigDecimal revisarTotal = new BigDecimal(0);
        BigDecimal revisarRete = new BigDecimal(0);
        BigDecimal revisarIVA = new BigDecimal(0);
        BigDecimal diferencia = new BigDecimal("0.00");
        try {
            for (String folioActual : arrFolios) {
                psHeader = generaEncabezadoLayoutCompPD(conn, folioActual.trim());
                rs = psHeader.executeQuery();
                while (rs.next()) {
                    log.debug("Procesando pago: " + rs.getString(1));
                    String token = new String("");
                    StringBuilder encabezado = new StringBuilder();
                    for (int i = 2; i <= 42; i++) {
                        encabezado.append(token).append(rs.getString(i).trim().replaceAll("[\r\n]{2,}", " ").replace(",", ""));
                        token = ",";
                    }
                    token = "";
                    encabezado.append("\r\n");
                    log.debug("Encabezado: " + encabezado.toString());
                    arrListaComp.add(encabezado);
                    psDetail = generaDetalleLayoutCompPD(conn, folioActual.trim());
                    rs2 = psDetail.executeQuery();
                    int renglon = 1;
                    while (rs2.next()) {
                        token = new String("");
                        StringBuilder detalle = new StringBuilder();
                        for (int i = 2; i <= 43; i++) {
                            if (i == 29) {
                                revisarTotal = rs2.getBigDecimal(i);
                                total = total.add(revisarTotal);
                                if (revisarTotal.compareTo(BigDecimal.ZERO) < 0) {
                                    throw new Exception("No se genero el layout ya que el importe de uno de los registros del layout es menor que cero. Revise los pagos: " + folio);
                                } else {
                                    detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                                    token = ",";
                                }
                            } else if (i >= 34 && i <= 38) {
                                // Suma el importe de las retenciones
                                revisarRete = rs2.getBigDecimal(i);
                                retenciones = retenciones.add(revisarRete);
                                detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                                token = ",";
                            } else if (i == 41) {
                                revisarIVA = rs2.getBigDecimal(i);
                                //Validar y ajuste de IVA
                                if (renglon == 1) {
                                    diferencia = validarIvaPD(conn, revisarIVA, Integer.parseInt(folioActual.trim())).setScale(2, RoundingMode.HALF_UP);
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
                                detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                                token = ",";
                            }
                        }
                        renglon++;
                        token = "";
                        detalle.append("\r\n");
                        arrListaComp.add(detalle);
                        log.debug("Detalle: " + detalle.toString());
                    }
                    boolean layoutPrevioCreado = existeLayout(conn, folioActual.trim());
                    if (!layoutPrevioCreado) {
                        // Query para insertar en la tabla de
                        // tLayoutsCreadosHeader
                        StringBuilder sqlInsertaLayoutGrabado = PagosDirectosManager.generaQueryInsertaLayout();
                        log.debug("Query para insertar layouts: " + sqlInsertaLayoutGrabado);
                        pstmntHLayout = conn.prepareStatement(sqlInsertaLayoutGrabado.toString());
                        pstmntHLayout.setString(1, fecha);
                        pstmntHLayout.setString(2, leyenda);
                        pstmntHLayout.setString(3, cuentaBancaria);
                        pstmntHLayout.setString(4, usuario.getLogin());
                        pstmntHLayout.setString(5, rs.getString(1));
                        pstmntHLayout.executeUpdate();
                        // Query para Insertar en el detalle de
                        // tLayoutsCreadosDetalle
                        StringBuilder sqlLayoutInsertaDet = generaQueryInsertDetalleLayout();
                        log.debug("Query inserta Detalle Layout: " + sqlLayoutInsertaDet);
                        pstmntHLayoutDet = conn.prepareStatement(sqlLayoutInsertaDet.toString());
                        pstmntHLayoutDet.setString(1, folioActual.trim());
                        pstmntHLayoutDet.executeUpdate();
                    }
                }
            }
            // Valida que el total del Layout sea igual a los pagos
            validarTotalLayout(conn, total, folio);
            // Valida que el total de las Retenciones sea igual a los pagos
            validarTotalRetenciones(conn, retenciones, folio);
            return arrListaComp;
        } catch (Exception c) {
            log.error(c.getMessage(), c);
            throw new Exception(c.getMessage());
        } finally {
            CloseObject.closeObject(psHeader);
            CloseObject.closeObject(psDetail);
            CloseObject.closeObject(pstmntHLayout);
            CloseObject.closeObject(pstmntHLayoutDet);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rs2);
        }
    }

    private static PreparedStatement generaEncabezadoLayoutCompPD(Connection conn, String listaFolios) throws Exception {
        StringBuilder queryEncabezadoLayout = new StringBuilder();
        // A
        queryEncabezadoLayout.append("SELECT top 1	tCE.nFolioPagoDirecto, 	'H' AS Header, ");
        // B
        queryEncabezadoLayout.append("      	CONVERT(nvarchar(10), GETDATE(), 103) FECHA_EXP, ");
        // C
        queryEncabezadoLayout.append("      	CONVERT(nvarchar(10), GETDATE(), 103) FECHA_APL, ");
        // D
        queryEncabezadoLayout.append("      	tCE.cRamo ID_RAMO, ");
        // E
        queryEncabezadoLayout.append("      	tCE.cRamo ID_RAMO_CR, ");
        // F
        queryEncabezadoLayout.append("      	tCE.cRamo ID_RAMO_REC, ");
        // G
        queryEncabezadoLayout.append("      	'RHQ'  ID_UNIDAD, ");
        // H
        queryEncabezadoLayout.append("      	'RHQ'  ID_UNIDAD_CR, ");
        // I
        queryEncabezadoLayout.append("      	'RHQ'  ID_UNIDAD_REC, ");
        // J
        queryEncabezadoLayout.append("      	'N' ID_TIPO_MOVIMIENTO,  ");
        // K
        queryEncabezadoLayout.append("      	'5' AS OrigenPpto, ");
        // L
        queryEncabezadoLayout.append("      	'9' AS TipoSol, ");
        // M
        queryEncabezadoLayout.append("      	'MXN' TipoMoneda, ");
        // N
        queryEncabezadoLayout.append("      	'1' TipoCambio, ");
        // O
        queryEncabezadoLayout.append("      	'1' TIPO_PAGO, ");
        // P
        queryEncabezadoLayout.append("      	1 AS CVE_LEYENDA_66,");
        // Q
        queryEncabezadoLayout.append("      	CBEN, ");
        // R
        queryEncabezadoLayout.append("         CTAB CUENTA_BANCARIA, ");
        // S
        queryEncabezadoLayout.append("      	cIdRFC RFC_227, ");
        // T
        queryEncabezadoLayout.append("      	'FAC' TDOC_87, ");
        // U
        queryEncabezadoLayout.append("      	 '' FechaReferencia, ");
        // V
        queryEncabezadoLayout.append("      	'' Referencia1, ");
        // W
        queryEncabezadoLayout.append("      	'' Referencia2, ");
        // X
        queryEncabezadoLayout.append("      	LEFT(cDescripcionPoliza,500) CPAG_76, ");
        // Y
        queryEncabezadoLayout.append("      	'' NotasReverso, ");
        // Z
        queryEncabezadoLayout.append("      	'' AMF, ");
        // AA
        queryEncabezadoLayout.append("      	caNoContrarrecibo NO_ACMI, ");
        // AB
        queryEncabezadoLayout.append("      	caNoContrarrecibo AuxiliarComodin, ");
        // AC
        queryEncabezadoLayout.append("      	caNoContrarrecibo CTR, ");
        // Ad
        queryEncabezadoLayout.append("      	CONVERT(decimal(17, 2), sum(DCD_ISR),0)  AS ISR_303,");
        // AE
        queryEncabezadoLayout.append("       	CONVERT(decimal(17, 2), sum(DCD_IVA), 0) IVA_304, ");
        // AF
        queryEncabezadoLayout.append("       	CONVERT(decimal(17, 2), sum(DCD_MIL5),0) MIL5_305, ");
        // AG
        queryEncabezadoLayout.append("       	CONVERT(decimal(17, 2), sum(DCD_MIL2),0) MIL2_306, ");
        // AH
        queryEncabezadoLayout.append("       	ISNULL(CONVERT(varchar(20), sum(DCD_OTRAS_RET)),0) OTRASRET, ");
        // AI
        queryEncabezadoLayout.append("       	'0' PENALIZACION_308, ");
        // AJ
        queryEncabezadoLayout.append("       	'0' CONTRIBUCION_309, ");
        // AK
        queryEncabezadoLayout.append("       	CONVERT(decimal(17, 2), sum(DCD_IVADES), 0) AS IVADES_310,	");
        // AL
        queryEncabezadoLayout.append("       	'' IVAANT_311, ");
        // AM
        queryEncabezadoLayout.append("      	'' FolioDC, ");
        // AN
        queryEncabezadoLayout.append("      	'NA' ID_DESTINO_GASTO,   ");
        // AO
        queryEncabezadoLayout.append("      	'' TTRANS_60, ");
        // AP
        queryEncabezadoLayout.append("      	'' ANT_CON_IVA_413");
        queryEncabezadoLayout.append("  FROM	tPagoDirectoEncabezado tCE WITH(NOLOCK) ");
        queryEncabezadoLayout.append("      	inner join v_DCD_PAGO_DIRECTO dcd WITH (NOLOCK) ");
        queryEncabezadoLayout.append("      	on dcd.nFolioPagoDirecto = tce.nFolioPagoDirecto ");
        queryEncabezadoLayout.append("      	INNER JOIN tBeneficiario beneficiario WITH (NOLOCK)  ");
        queryEncabezadoLayout.append("      	ON beneficiario.dRFC = tCE.cIdRFC ");
        queryEncabezadoLayout.append(" WHERE	tCE.nFolioPagoDirecto in ( " + listaFolios + " ) ");
        queryEncabezadoLayout.append(" GROUP BY cRamo , cIdRFC, CTAB, caNoContrarrecibo, cDescripcionPoliza, tCE.nFolioPagoDirecto,CBEN ");
        queryEncabezadoLayout.append(" ORDER BY tCE.nFolioPagoDirecto ");
        PreparedStatement psHeader = null;
        psHeader = conn.prepareStatement(queryEncabezadoLayout.toString());
        return psHeader;
    }

    private static PreparedStatement generaDetalleLayoutCompPD(Connection conn, String folio) throws Exception {
        StringBuilder queryLayoutDetalle = new StringBuilder();
        // A
        queryLayoutDetalle.append(" SELECT d.nFolioPagoDirecto, '1' ID_EVENTO,  	");
        // B
        queryLayoutDetalle.append(" '24.0.001' 	  EVENTO,  	");
        // C
        queryLayoutDetalle.append(" Substring(D.EP, 6, 2)  ID_RAMO_ML, ");
        // D
        queryLayoutDetalle.append(" 'RHQ',  					");
        // E
        queryLayoutDetalle.append(" Substring(D.EP, 1, 4)  aEjercicioFiscal,");
        // F
        queryLayoutDetalle.append(" Substring(D.EP, 13, 1) cGrupoFuncional, ");
        // G
        queryLayoutDetalle.append(" Substring(D.EP, 15, 1) cFuncion,  ");
        // H
        queryLayoutDetalle.append(" Substring(D.EP, 17, 2) cSubFuncion, ");
        // I
        queryLayoutDetalle.append(" CASE WHEN Substring(D.EP, 20, 2) IN (SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK)) THEN '00' ELSE Substring(D.EP, 20, 2) END AS cProgramaGeneral, ");
        // J
        queryLayoutDetalle.append(" Substring(D.EP, 23, 3) cActividadInstitucional, ");
        // K
        queryLayoutDetalle.append(" Substring(D.EP, 27, 4) cProgramaPresupuestario,");
        // L
        queryLayoutDetalle.append(" Substring(D.EP, 32, 1) CCAP_157,  ");
        // M
        queryLayoutDetalle.append(" Substring(D.EP, 33, 1) CCON_158,  ");
        // N
        queryLayoutDetalle.append(" Substring(D.EP, 34, 1) CPARG_300, ");
        // O
        queryLayoutDetalle.append(" Substring(D.EP, 35, 2) CPAR_159,  ");
        // P
        queryLayoutDetalle.append(" Substring(D.EP, 38, 1) cTipoGasto,");
        // Q
        queryLayoutDetalle.append(" Substring(D.EP, 40, 1) cFuenteFinanciamiento,");
        // R
        queryLayoutDetalle.append(" Substring(D.EP, 42, 2) cEntidadFederativa, ");
        // S
        queryLayoutDetalle.append(" SUBSTRING( dbo.CambiaEPCarteraMeta(D.EP),45, 11) AS cCartera,");
        // T
        queryLayoutDetalle.append(" '0000000000' AS CCAU_162,  		");
        // U
        queryLayoutDetalle.append(" '00'                   CCOP_163,  	");
        // V
        queryLayoutDetalle.append(" '000'                  PL,  		");
        // W
        queryLayoutDetalle.append(" '000'                  OFI,  		");
        // X
        queryLayoutDetalle.append(" '00000'                AUX1,  		");
        // Y
        queryLayoutDetalle.append(" '00000'                AUX2,  		");
        // Z
        queryLayoutDetalle.append(" '0000000000'           AUX3,  		");
        // AA
        queryLayoutDetalle.append(" nFolioAutSICOP  AS NCOM_35,");
        // AB
        queryLayoutDetalle.append(" Sum(D.mImporteNeto)  AS MONTO,  ");
        // AC
        queryLayoutDetalle.append(" D.cmes                 MES_149,  	");
        // AD
        queryLayoutDetalle.append(" nFolioSuficiencia as NRES, ");
        // AE
        queryLayoutDetalle.append(" CASE WHEN Substring(ep, 32, 5) = '35801' THEN 'GD' ELSE 'PN' END TIPO_CONTRATO, ");
        // AF
        queryLayoutDetalle.append(" '000'                  CONC_MOV,  	");
        // AG
        queryLayoutDetalle.append(" CONVERT(decimal(17, 2),sum(mimporteISRResico + mISRArrenda + mISRHonorarios+ mISROtros + mImporteISRLaudos),0)  AS RETENCIONES, ");
        // AH
        queryLayoutDetalle.append(" CONVERT(decimal(17, 2),sum(mImporteIvaArrenda + mImporteIvaHonorarios + mImporteFlete23+  mImporteFlete4),0) IVA_43,  	");
        // AI
        queryLayoutDetalle.append(" CONVERT(decimal(17, 2),sum(isnull(mObra5, 0) + isnull(mImporteObra,0))) MIL5_44,  						");
        // AJ
        queryLayoutDetalle.append(" CONVERT(decimal(17, 2),sum(isnull(m2Millar,0))) MIL2_312,  						");
        // AK
        queryLayoutDetalle.append(" CONVERT(decimal(17, 2),sum(isnull(mRetImpuestoCedular,0))) OTRAS_RET_313, ");
        // AL
        queryLayoutDetalle.append(" 0 PENALIZACION_314,   						");
        // AM
        queryLayoutDetalle.append(" 0 CONTRIB,  				");
        // AN
        queryLayoutDetalle.append(" CONVERT(decimal(17, 2),ROUND( mimportemasiva - (  mImporteMasIva /1.16 ), 2 ))  IVADES_45,  ");
        // AO
        queryLayoutDetalle.append(" 0 ANTICIPO_46,				");
        // AP
        queryLayoutDetalle.append(" 0 IVAANT_47,  				");
        // AQ
        queryLayoutDetalle.append(" ''  id_ctr_intdet			");
        queryLayoutDetalle.append(" FROM   tPagoDirectoDetalle D WITH (NOLOCK)  ");
        queryLayoutDetalle.append(" INNER JOIN   tPagoDirectoEncabezado E WITH (NOLOCK)  ");
        queryLayoutDetalle.append(" 	on d.nFolioPagoDirecto = e.nFolioPagoDirecto  ");
        queryLayoutDetalle.append(" WHERE  d.nFolioPagoDirecto in ( " + folio + "  ) ");
        queryLayoutDetalle.append(" GROUP  BY Substring(D.EP, 61, 3),  D.EP, D.cmes,mISRHonorarios,  D.ID_TIPO_CONCEPTO,  SUBSTRING( dbo.CambiaEPCarteraMeta(D.EP),45, 11), d.nFolioPagoDirecto , mImporteMasIva, nFolioAutSICOP,nFolioSuficiencia");
        queryLayoutDetalle.append(" ORDER  BY  d.nFolioPagoDirecto, Sum(D.mImporteNeto) DESC ");
        PreparedStatement psLayoutDetalle = null;
        psLayoutDetalle = conn.prepareStatement(queryLayoutDetalle.toString());
        return psLayoutDetalle;
    }
}
