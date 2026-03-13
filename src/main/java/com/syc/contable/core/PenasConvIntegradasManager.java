package com.syc.contable.core;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.syc.sai.contabilidad.utils.db.CloseObject;

public class PenasConvIntegradasManager {	
	
	public static int insertEncabezado(Connection conn, PenasConvIntEncabezado encabezado) throws Exception {
		StringBuilder query = new StringBuilder();

		query.append("INSERT INTO tPagoPenasConvIntegradoEnc (")
		     .append(" nFolioPagoPenasConvInt,")
		     .append(" cBeneficiario,")
		     .append(" fCaptura,")
		     .append(" fDesde,")
		     .append(" fHasta,")
		     .append(" caNoContrarreciboInt,")
		     .append(" mImporteNeto,")
		     .append(" U_LOGIN,")
		     .append(" cCentroContable,")
		     .append(" aEjercicioFiscal,")
		     .append(" cUnidadResponsable,")
		     .append(" cRamo,")
		     .append(" fCancelacion,")
		     .append(" nEnviadoSICOP,")
		     .append(" cIDRFC")
		     .append(" ) VALUES (")
		     .append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ")
		     .append(" )");

		
		int retVal = 0;
		PreparedStatement ps = null;
		try{
			
			ps = conn.prepareStatement(query.toString());
			ps.setInt(1, encabezado.getnFolioPagoPenasConvInt() );
			ps.setString(2, encabezado.getcBeneficiario() );
			ps.setDate(3, encabezado.getfCaptura() == null ? null: new Date(encabezado.getfCaptura().getTime()) );
			ps.setDate(4, encabezado.getfDesde() == null ? null: new Date(encabezado.getfDesde().getTime()) );
			ps.setDate(5, encabezado.getFHasta() == null ? null: new Date(encabezado.getFHasta().getTime()) );
			ps.setString(6, encabezado.getCaNoContrarrecibo() );
			ps.setDouble(7, encabezado.getmImportes() );
			ps.setString(8, encabezado.getU_LOGIN() );
			ps.setString(9, encabezado.getcCentroContable() );
			ps.setString(10, encabezado.getaEjercicioFiscal() );
			ps.setString(11, encabezado.getcUnidadResponsable() );
			ps.setString(12, encabezado.getcRamo() );
			ps.setDate(13, null );
			ps.setString(14, encabezado.getnEnviadoSICOP() );
			ps.setString(15, encabezado.getcIDRFC() );
			
			retVal = ps.executeUpdate();

			return retVal;
		}finally{
			CloseObject.closeObject(ps, false);
		}
		
	}

	public static int insertDetalle(Connection conn, List<PenasConvIntDetalle> detalle) throws Exception {
		StringBuilder queryInsert = new StringBuilder();

			queryInsert.append("INSERT INTO tPagoPenasConvIntegradoDet (")
			           .append(" nFolioPagoPenasConvInt,")
			           .append(" nDocRenglon,")
			           .append(" nFolioPagoPenasConv,")
			           .append(" caNoContrarreciboPP,")
			           .append(" cUnidadResponsable,")
			           .append(" cIDRFC,")
			           .append(" cCentroContable,")
			           .append(" aEjercicioFiscal,")
			           .append(" Ep,")
			           .append(" mImporteMasIva,")
			           .append(" nFolioDoc,")
			           .append(" caNoContrarrecibo,")
			           .append(" cTipoDoc,")
			           .append(" cRamo,")
			           .append(" nDocRenglonInt,")
			           .append(" mAjuste,")
			           .append(" cMes")
			           .append(" ) VALUES (")
			           .append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?")
			           .append(" )");
		
			PreparedStatement ps = null;
			int retVal = 0;
			try{
				ps = conn.prepareStatement(queryInsert.toString());
				for( Iterator<PenasConvIntDetalle>it = detalle.iterator(); it.hasNext(); ){
					PenasConvIntDetalle insertDetObj = it.next();
					ps.setInt(1, insertDetObj.getnFolioPagoPenasConvInt());
					ps.setInt(2, insertDetObj.getnDocRenglon());
					ps.setInt(3, insertDetObj.getnFolioPagoPenasConv());
					ps.setString(4, insertDetObj.getcaNoContrarreciboPP() );
					ps.setString(5, insertDetObj.getcUnidadResponsable() );
					ps.setString(6, insertDetObj.getcIDRFC() );
					ps.setString(7, insertDetObj.getcCentroContable() );
					ps.setString(8, insertDetObj.getaEjercicioFiscal() );
					ps.setString(9, insertDetObj.getEp() );
					ps.setDouble(10, insertDetObj.getmImporteMasIva() );
					ps.setInt(11, insertDetObj.getnFolioDoc() );
					ps.setString(12, insertDetObj.getCaNoContrarrecibo() );
					ps.setString(13, insertDetObj.getcTipoDoc() );
					ps.setString(14, insertDetObj.getcRamo() );
					ps.setInt(15, insertDetObj.getnDocRenglonInt() );
					ps.setDouble(16, insertDetObj.getmAjuste() );
					ps.setString(17, insertDetObj.getcMes() );
					retVal += ps.executeUpdate();
					
				}
				return retVal;
			
			}finally{
				CloseObject.closeObject(ps, false);
			}		
			
	}
	
	public static ArrayList<String> buscaPenasConvIntegradas(Connection conn, String cxpAI, String fecha, String cBancaria, String Leyenda, String nFolio, String cBEN)throws Exception{
		
		ArrayList<String> arrListaComp = new ArrayList<String>();
		PreparedStatement pstmntH = null;
		
		PreparedStatement pstmntD = null;
		ResultSet rs = null;
		ResultSet rs2 = null;	
		
		try {
		//ENCABEZADO DE LAYOUT PENA CONVENCIONAL INTEGRADA
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT distinct tCE.nFolioPagoPenasConvInt, ");
			sql.append(" 'H' AS Header, ");
			sql.append(" CONVERT(nvarchar(10), tCE.fCaptura,103), ");
			sql.append(" CONVERT(nvarchar(10), tCE.fCaptura,103), ");
			sql.append(" tCE.cRamo, ");
			sql.append(" tCE.cRamo, ");
			sql.append(" tCE.cRamo, ");
			sql.append(" 'RHQ' UnidadResponsable, ");
			sql.append(" 'RHQ' UnidadResponsable, ");
			sql.append(" 'RHQ' UnidadResponsable, ");
			sql.append(" CASE WHEN opa.cIdTipoDocumento = 'L' THEN 'I' ELSE opa.cIdTipoDocumento END AS cIdTipoDocumento, ");
			sql.append(" '1' AS OrigenPpto, ");
			sql.append(" 'MXN' TipoMoneda, ");
			sql.append(" '1.00' TipoCambio, ");
			sql.append(" CASE WHEN opa.cIdTipodocumento='O' THEN '1' ELSE '2' END TIPO_PAGO, ");
			sql.append(" '").append(Leyenda).append("' CveLeyenda, ");
			sql.append(" '").append(cBEN).append("' CBEN, ");
			sql.append(" '").append(cBancaria).append("' CUENTA_BANCARIA, ");
			sql.append(" rtrim(tCE.cIdRFC), ");
			sql.append(" 'OTR', ");
			sql.append(" '' FechaReferencia, ");
			sql.append(" LEFT(opa.cConcepto, 40) Referencia1, ");
			sql.append(" '' Referencia2, ");
			sql.append(" LEFT(opa.cConcepto, 40), ");
			sql.append(" '' AMF, ");
			sql.append(" rtrim(tCE.caNoContrarreciboInt) NO_ACMI, ");
			sql.append(" rtrim(tCE.caNoContrarreciboInt) AuxiliarComodin, ");
			sql.append(" rtrim(tCE.caNoContrarreciboInt) ID_CTR_INT_302, ");
			sql.append(" '' CTR, ");
			sql.append(" '' FolioDC ");
			sql.append(" FROM tPagoPenasConvIntegradoEnc tCE WITH (NOLOCK) ");
			sql.append(" LEFT JOIN tBeneficiario B WITH (NOLOCK) ON tCE.cIdRFC = B.dRFC ");
			sql.append(" INNER JOIN tBeneficiarioCuentasBancarias BCB WITH (NOLOCK) ON tCE.cIdRFC = BCB.dRFC ");
			sql.append(" INNER JOIN tGrupoOpAjenas opa WITH (NOLOCK) ON opa.nIdGrupoOpAjena = tCE.cBeneficiario ");
			sql.append(" WHERE tCE.nFolioPagoPenasConvInt = ").append(nFolio);


		pstmntH = conn.prepareStatement(sql.toString());
		rs = pstmntH.executeQuery();

		if(rs.next()){
			
					String vreferencia = rs.getString(22).trim();
					   vreferencia = vreferencia.replaceAll(",", " ");
					   vreferencia = vreferencia.replaceAll(":", "");
					   vreferencia = vreferencia.replaceAll(";", "");
					   vreferencia = vreferencia.replaceAll("\\(", "");
					   vreferencia = vreferencia.replaceAll("/", "");
					   vreferencia = vreferencia.replaceAll("\\)", "");
					   vreferencia = vreferencia.replaceAll("%", "");
					
					   StringBuilder encabezado = new StringBuilder();
					   encabezado.append(rs.getString(2)).append(",")
					             .append(rs.getString(3)).append(",")
					             .append(rs.getString(4).trim()).append(",")
					             .append(rs.getString(5).trim()).append(",")
					             .append(rs.getString(6).trim()).append(",")
					             .append(rs.getString(7).trim()).append(",")
					             .append(rs.getString(8).trim()).append(",")
					             .append(rs.getString(9).trim()).append(",")
					             .append(rs.getString(10).trim()).append(",")
					             .append(rs.getString(11).trim()).append(",")
					             .append(rs.getString(12).trim()).append(",")
					             .append(rs.getString(13).trim()).append(",")
					             .append(rs.getString(14).trim()).append(",")
					             .append(rs.getString(15).trim()).append(",")
					             .append(Leyenda).append(",")
					             .append(rs.getString(17).trim()).append(",")
					             .append(cBancaria).append(",")
					             .append(rs.getString(19).trim()).append(",")
					             .append(rs.getString(20).trim()).append(",")
					             .append(rs.getString(21).trim()).append(",")
					             .append(vreferencia).append(",")
					             .append(rs.getString(23).trim()).append(",")
					             .append(vreferencia).append(",")
					             .append(rs.getString(25).trim()).append(",")
					             .append(rs.getString(26).trim()).append(",")
					             .append(rs.getString(27).trim()).append(",")
					             .append(rs.getString(29).trim()).append(",")
					             .append(rs.getString(30).trim())
					             .append( "\r\n" );

					arrListaComp.add(encabezado.toString());							
					
		}
		
			//DETALLE LAYOUT PENA CONVENCIONAL INTEGRADA
			StringBuilder sql2 = new StringBuilder();
			sql2.append(" SELECT '800' AS ID_EVENTO,  ");
			sql2.append(" '405_CLC_T' AS EVENTO, ");
			sql2.append(" ltrim(TCEP.cRamo) ID_RAMO_ML, ");
			sql2.append(" 'RHQ', ");
			sql2.append(" TCEP.aEjercicioFiscal, ");
			sql2.append(" TCEP.cGrupoFuncional, ");
			sql2.append(" tCEP.cFuncion, ");
			sql2.append(" tCEP.cSubFuncion, ");
			sql2.append(" CASE WHEN tCEP.cProgramaGeneral IN (SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK)) THEN '00' ELSE tCEP.cProgramaGeneral END AS cProgramaGeneral, ");
			sql2.append(" tCEP.cActividadInstitucional, ");
			sql2.append(" tCEP.cProgramaPresupuestario, ");
			sql2.append(" ltrim(substring(cpartida,1,1)) CCAP_157, ");
			sql2.append(" substring(cpartida,2,1) CCON_158, ");
			sql2.append(" substring(cpartida,3,1) CPARG_300, ");
			sql2.append(" substring(cpartida,4,2) CPAR_159, ");
			sql2.append(" tCEP.cTipoGasto, ");
			sql2.append(" tCEP.cFuenteFinanciamiento, ");
			sql2.append(" tCEP.cEntidadFederativa, ");
			sql2.append(" SUBSTRING( dbo.CambiaEPCarteraMeta(TCEP.EP),45, 11) as cCartera, ");
			sql2.append(" '0000000000', ");
			sql2.append(" '00' CCOP_163, ");
			sql2.append(" '000' PL, ");
			sql2.append(" '000' OFI, ");
			sql2.append(" '00000' AUX1, ");
			sql2.append(" '00000' AUX2, ");
			sql2.append(" '0000000000' AUX3, ");
			sql2.append(" CONVERT(decimal(17, 2), Sum(TPDD.mImporteMasIva)) mImporteNeto, ");
			sql2.append(" TPDD.cMes AS MES_149, ");
			sql2.append(" CONVERT(decimal(17, 2), Sum(TPDD.mImporteMasIva)) IMPORTE_148, ");
			sql2.append(" CASE WHEN ISNULL( CLCS.NCOM_15, '') = '' THEN '' ELSE RIGHT( REPLICATE('0', 6) + CLCS.NCOM_15, 6) END AS numcompromiso, ");
			sql2.append(" (SELECT top 1 nFolioSuficiencia FROM tCompromisoEncabezado WITH (NOLOCK) where CAST(CLCS.NCOM_15 AS INT) = CAST(tCompromisoEncabezado.nFolioAutSICOP AS INT)),   ");
			sql2.append(" '', ");
			sql2.append(" ltrim('PN') TIPO_CONTRATO, ");
			sql2.append(" '000' CONC_MOV, ");
			sql2.append(" ISNULL(TEE.solicitudPago, 0) AS nFolioSICOP, ");
			sql2.append(" CASE ISNULL( CLCS.SPAG_176, '') WHEN '' THEN '' ELSE ISNULL(CLCS.CBEN_16, '') END CBEN, ");
			sql2.append(" '' ");
			sql2.append(" FROM tPagoPenasConvIntegradoDet TPDD with(nolock) ");
			sql2.append(" INNER JOIN tPagoPenasConvIntegradoEnc TPDE with(nolock) ");
			sql2.append(" ON TPDD.nFolioPagoPenasConvInt  = TPDE.nFolioPagoPenasConvInt ");
			sql2.append(" INNER JOIN tCatalogoEP TCEP with(nolock) ");
			sql2.append(" ON rtrim(TCEP.EP) = rtrim(TPDD.EP) ");
			sql2.append(" INNER JOIN tEjercidoEncabezado TEE with(nolock) ");
			sql2.append(" ON TEE.canocontrarrecibo = TPDD.canocontrarrecibo ");
			sql2.append(" LEFT JOIN (select DISTINCT NCOM_15, CBEN_16, NCTR_47, SPAG_176  from CLC_SICOP with(nolock) ) CLCS ");
			sql2.append(" ON  CLCS.NCTR_47 = TPDD.canocontrarrecibo ");
			sql2.append(" AND CLCS.SPAG_176 = TEE.solicitudPago ");
			sql2.append(" WHERE TPDE.nFolioPagoPenasConvInt = ").append(nFolio);
			sql2.append(" group by TCEP.cRamo, ");
			sql2.append(" TCEP.aEjercicioFiscal, ");
			sql2.append(" TCEP.cGrupoFuncional, ");
			sql2.append(" tCEP.cFuncion, ");
			sql2.append(" tCEP.cSubFuncion, ");
			sql2.append(" tCEP.cProgramaGeneral, ");
			sql2.append(" tCEP.cActividadInstitucional, ");
			sql2.append(" tCEP.cProgramaPresupuestario, ");
			sql2.append(" cpartida, ");
			sql2.append(" tCEP.cTipoGasto, ");
			sql2.append(" tCEP.cFuenteFinanciamiento, ");
			sql2.append(" tCEP.cEntidadFederativa, ");
			sql2.append(" TCEP.EP, ");
			sql2.append(" TPDD.cMes, ");
			sql2.append(" TPDD.canocontrarrecibo, ");
			sql2.append(" CLCS.NCOM_15, ");
			sql2.append(" TEE.solicitudPago, ");
			sql2.append(" CLCS.SPAG_176, ");
			sql2.append(" CLCS.CBEN_16 ");						  
			
			pstmntD = conn.prepareStatement(sql2.toString());
			rs2 = pstmntD.executeQuery();

				while (rs2.next()){
					
					String token = new String();
					StringBuffer detalle = new StringBuffer();
					
					for (int i = 1; i < 38; i++) {
						detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
						token = ",";
					}
					
					token = "";
					detalle.append("\r\n");
					arrListaComp.add(detalle.toString());
				}
		} finally {
			
			CloseObject.closeObject( pstmntD );
			CloseObject.closeObject( pstmntH );
			CloseObject.closeObject( rs );
			CloseObject.closeObject( rs2 );
			
		}	
		return arrListaComp;
	}	
	
}
