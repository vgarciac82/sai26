package com.syc.ejercido.pagado.core;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.syc.contable.caja.core.CajaManager;
import com.syc.contable.core.AdecuacionManager;
import com.syc.contable.core.PagosDiversosRGManager;
import com.syc.ejercido.pagado.Anexo1Detalle;
import com.syc.ejercido.pagado.Anexo1Encabezado;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class Anexo1Manager {

    private static final Logger log = LoggerFactory.getLogger(CajaManager.class);

    public Anexo1Manager() {
        super();
    }

    public static int insertEncabezado(Connection conn, Anexo1Encabezado encabezado) throws Exception {
        String query = "INSERT INTO tAnexo1Encabezado ( " + "	nFolioAnexo1 , " + "	fCaptura , " + " 	fAplicacion , " + " 	cConcepto , " + " 	caNoContrarrecibo , " + " 	cTipoAnexo, " + " 	cIdContrato, " + " 	mImporte , " + " 	u_login , " + " 	cCentroContable , " + " 	aEjercicioFiscal , " + " 	cUnidadResponsable , " + " 	cTipoPoliza , " + " 	cUnidadResponsableContable, " + "	sFirmanteVoBo, " + "	sPuestoVoBo, " + "	sFirmanteAut, " + "	sPuestoAut) " + "VALUES  ( " + "	?, " + "	?, " + "	?, " + "	?, " + "	?, " + "	?, " + "	?, " + "	?, " + "	?, " + "	?, " + "	?, " + "	?, " + "	?, " + "	?, " + "	?, " + "	?, " + "	?, " + "	? )";
        int retVal = 0;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, encabezado.getnFolioAnexo());
            ps.setDate(2, encabezado.getfCaptura() == null ? null : new Date(encabezado.getfCaptura().getTime()));
            ps.setDate(3, encabezado.getfApliacion() == null ? null : new Date(encabezado.getfApliacion().getTime()));
            ps.setString(4, encabezado.getcConcepto());
            ps.setString(5, encabezado.getcaNoContrarrecibo());
            ps.setString(6, encabezado.getcTipoAnexo());
            ps.setString(7, encabezado.getcIdContrato());
            ps.setDouble(8, encabezado.getmImporte());
            ps.setString(9, encabezado.getu_login());
            ps.setString(10, encabezado.getcCentroContable());
            ps.setString(11, encabezado.getaEjercicioFiscal());
            ps.setString(12, encabezado.getcUnidadResponsable());
            ps.setString(13, encabezado.getcTipoPoliza());
            ps.setString(14, encabezado.getcUnidadResponsableContable());
            ps.setString(15, encabezado.getfirmanteVoBo());
            ps.setString(16, encabezado.getcPuestoVo());
            ps.setString(17, encabezado.getfirmanteAut());
            ps.setString(18, encabezado.getcPuestoA());
            retVal = ps.executeUpdate();
            return retVal;
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    public static int insertDetalle(Connection conn, List<Anexo1Detalle> detalle) throws Exception {
        String queryInsert = "INSERT INTO tAnexo1Detalle ( " + "	nFolioAnexo1 , " + "	nDocRenglon , " + "	Ep , " + "	cMes , " + "	cEvento , " + "	aEjercicioFiscal , " + "	cCentroContable , " + "	mImporte , " + "	mImporteNegativo , " + "	OBGT ) " + "VALUES  ( " + "	?, " + "	(SELECT COUNT(*)+1 FROM tAnexo1Detalle WITH(NOLOCK) WHERE nFolioAnexo1 = ?), " + "	?, " + "	?, " + "	?, " + "	?, " + "	?, " + "	?, " + "	?, " + "	SUBSTRING(?,32,5) )";
        PreparedStatement ps = null;
        int retVal = 0;
        try {
            ps = conn.prepareStatement(queryInsert);
            for (Iterator<Anexo1Detalle> it = detalle.iterator(); it.hasNext(); ) {
                Anexo1Detalle insertDetObj = it.next();
                ps.setInt(1, insertDetObj.getnFolioAnexo());
                ps.setInt(2, insertDetObj.getnFolioAnexo());
                ps.setString(3, insertDetObj.getEp());
                ps.setString(4, insertDetObj.getcMes());
                ps.setString(5, insertDetObj.getcEvento());
                ps.setString(6, insertDetObj.getaEjercicioFiscal());
                ps.setString(7, insertDetObj.getcCentroContable());
                ps.setDouble(8, insertDetObj.getmImporte());
                ps.setDouble(9, insertDetObj.getmImporteNegativo());
                ps.setString(10, insertDetObj.getEp());
                retVal += ps.executeUpdate();
            }
            return retVal;
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    public static ArrayList<String> buscaAnexo1Integrados(Connection conn, String listaIds, String listaCuentaBancaria, String listaFechas, String listaLeyendas, Usuario usuario, String sTimeStamp, String folioGenerator) throws Exception {
        String sUsuario = usuario.getLogin();
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntH = null;
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        ResultSet rs4 = null;
        PreparedStatement pstmntHLayout = null;
        PreparedStatement pstmntHLayoutDet = null;
        PreparedStatement pstmUpSeqLayout = null;
        PreparedStatement pstmSeqLayout = null;
        String strFolioLayout = "";
        try {
            String[] arrFolios = listaIds.split(",");
            String[] arrCuentasBancarias = listaCuentaBancaria.split(",");
            String[] arrFechas = listaFechas.split(",");
            String[] arrLeyendas = listaLeyendas.split(",");
            //String strRFC = "";
            // Obtiene Ejercicio Fiscal
            String ejercicioFiscal = AdecuacionManager.obtenEjercicioFiscal(conn);
            String sREFERENCIA1_107 = sTimeStamp;
            /*String Sql3 = " select RTRIM(LTRIM(REPLACE(strrfc,'-',''))) from [tUECuentasBancarias] where strclabe = '" + arrCuentasBancarias[0].trim() + "'";
			pstmntH = conn.prepareStatement(Sql3);
			rs3 = pstmntH.executeQuery();
			if (rs3.next()) {
				strRFC = rs3.getString(1);
			}
			rs3.close();*/
            //URVP SE ARMA EL "SP" ENCABEZADO
            //tCE.nFolioAnexo," +
            String //tCE.nFolioAnexo," +
            //"	REPLACE(LEFT(tCE.cConcepto, 70),',','')," +
            Sql = " SELECT TOP 1 " + "	SUBSTRING('" + sREFERENCIA1_107 + "',LEN('" + sREFERENCIA1_107 + "')-7,LEN('" + sREFERENCIA1_107 + "'))," + "	'H' AS Header," + "	CONVERT(nvarchar(10), GETDATE(),103)," + "	CONVERT(nvarchar(10), GETDATE(),103)," + "	tCE.cRamo," + "	tCE.cRamo," + "	tCE.cRamo," + "	'RHQ' UnidadResponsable," + "	'RHQ' UnidadResponsable," + "	'RHQ' UnidadResponsable," + "	'N' ID_TIPO_MOVIMIENTO," + "	'1' AS OrigenPpto," + "	'3' AS TipoSol," + "	'MXN' TipoMoneda," + "	'1' TipoCambio," + "	'1' TIPO_PAGO," + "	'PENDIENTE' AS CveLeyenda," + "	'S04929' CBEN," + "	'" + arrCuentasBancarias[0].trim() + "' CUENTA_BANCARIA," + "	'16RHQ'," + "	'FAC'," + "	'' FechaReferencia," + "	'' Referencia1," + "	'' Referencia2," + " 	'Integracion de Anexo 1 " + sREFERENCIA1_107 + "' Concepto," + "	'' NotasReverso," + "	'' AMF," + "	'" + sREFERENCIA1_107 + "' NO_ACMI," + "	'" + sREFERENCIA1_107 + "' AuxiliarComodin," + "	'' CTR," + "	'' FolioDC," + "	CONVERT(DECIMAL(17, 2), 0) DCD_ISR, " + "	CONVERT(DECIMAL(17, 2), 0) DCD_IVADES, " + "	CONVERT(DECIMAL(17, 2), 0) DCD_MIL5, " + "	CONVERT(DECIMAL(17, 2), 0) DCD_MIL2, " + "	CONVERT(DECIMAL(17, 2), 0) DCD_OTRAS_RET, " + "	CONVERT(DECIMAL(17, 2), 0) DCD_PENALIZACION, " + "	CONVERT(DECIMAL(17, 2), 0) DCD_CONTRIBUCION, " + "	CONVERT(DECIMAL(17, 2), 0) DCD_IVA, " + "	CONVERT(DECIMAL(17, 2), 0) IVAANT, " + "	'NA' ID_DESTINO_GASTO " + "FROM tAnexo1Encabezado tCE " + "WHERE tCE.nFolioAnexo1 IN (" + listaIds + ") " + "GROUP BY cRamo";
            pstmntH = conn.prepareStatement(Sql);
            log.debug("Object: {}", Sql.toString());
            rs = pstmntH.executeQuery();
            while (rs.next()) {
                //inserta encabezado
                log.debug("Object: {}", "Procesando folio[" + arrFolios[0].trim() + "]");
                //String nFolio, nFolioCompromiso = rs.getString(1);
                String encabezado = rs.getString(2) + "," + arrFechas[0].trim() + "," + rs.getString(4).trim() + "," + rs.getString(5).trim() + "," + rs.getString(6).trim() + "," + rs.getString(7).trim() + "," + rs.getString(8).trim() + "," + rs.getString(9).trim() + "," + rs.getString(10).trim() + "," + rs.getString(11).trim() + "," + rs.getString(12).trim() + "," + rs.getString(13).trim() + "," + rs.getString(14).trim() + "," + rs.getString(15).trim() + "," + rs.getString(16).trim() + "," + arrLeyendas[0].trim().trim() + "," + rs.getString(18).trim() + "," + rs.getString(19).trim() + "," + rs.getString(20).trim() + "," + rs.getString(21).trim() + "," + rs.getString(22).trim() + "," + rs.getString(23).trim() + "," + rs.getString(24).trim() + "," + rs.getString(25).trim().replaceAll("[\r\n]{2,}", " ") + "," + rs.getString(26).trim() + "," + rs.getString(27).trim() + "," + rs.getString(28).trim() + "," + rs.getString(29).trim() + "," + rs.getString(30).trim() + "," + rs.getString(31).trim() + "," + rs.getString(32).trim() + "," + rs.getString(33).trim() + "," + rs.getString(34).trim() + "," + rs.getString(35).trim() + "," + rs.getString(36).trim() + "," + rs.getString(37).trim() + "," + rs.getString(38).trim() + "," + rs.getString(39).trim() + "," + rs.getString(40).trim() + "," + rs.getString(41);
                encabezado = encabezado + "\r\n";
                arrListaComp.add(encabezado);
                //OBTENER SEQUENCE DE LAYOUT DE ANEXO1
                pstmUpSeqLayout = conn.prepareStatement("UPDATE CF_SEQUENCE  WITH (ROWLOCK) SET seq_value = seq_value + 1  WHERE seq_name = 'LAYOUTANEXO' ");
                pstmUpSeqLayout.executeUpdate();
                pstmSeqLayout = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'LAYOUTANEXO' ");
                rs4 = pstmSeqLayout.executeQuery();
                if (rs4.next()) {
                    strFolioLayout = rs4.getString("seq_value");
                } else {
                    //SI NO EXISTE LO INSERTA
                    String InsertSequenceLayout = "INSERT INTO CF_SEQUENCE " + "		SELECT 'LAYOUTANEXO', 1 ";
                    pstmUpSeqLayout = conn.prepareStatement(InsertSequenceLayout);
                    pstmUpSeqLayout.executeUpdate();
                    strFolioLayout = "1";
                }
                //int retval;
                // Aqui grabamos dentro de layouts creados encabezado
                String SqlLayoutGrabado = "" + "	INSERT INTO tLayoutsCreadosAnexo1Header " + "		SELECT " + strFolioLayout + "," + "			getdate(),  " + "			tCE.nFolioAnexo1, " + "			'H' AS Header, " + "			CONVERT(nvarchar(10), tCE.fAplicacion,103)," + "			CONVERT(nvarchar(10), tCE.fAplicacion,103)," + "			tCE.cRamo," + "			tCE.cRamo," + "			tCE.cRamo," + "			'RHQ' UnidadResponsable," + "			'RHQ' UnidadResponsable," + "			'RHQ' UnidadResponsable," + "			'N' ID_TIPO_MOVIMIENTO," + "			'1' AS OrigenPpto," + "			'2' AS TipoSol," + "			'MXN' TipoMoneda," + "			'1' TipoCambio," + "			'1' TIPO_PAGO," + "			'1'," + "			'S04929' CBEN," + "			'" + arrCuentasBancarias[0].trim() + "'," + "			'16RHQ'," + "			'FAC', " + "			'' FechaReferencia," + "			'' Referencia1," + "			'' Referencia2," + "			REPLACE(LEFT(tCE.cConcepto, 70),',','')," + "			'' NotasReverso," + "			'' AMF," + "			rtrim(tCE.caNoContrarrecibo) NO_ACMI," + "			'" + sREFERENCIA1_107 + "' AuxiliarComodin," + "			'' CTR," + "			'' FolioDC," + "			CONVERT(DECIMAL(17, 2), 0) DCD_ISR, " + "			CONVERT(DECIMAL(17, 2), 0) DCD_IVADES, " + "			CONVERT(DECIMAL(17, 2), 0) DCD_MIL5, " + "			CONVERT(DECIMAL(17, 2), 0) DCD_MIL2, " + "			CONVERT(DECIMAL(17, 2), 0) DCD_OTRAS_RET, " + "			CONVERT(DECIMAL(17, 2), 0) DCD_PENALIZACION, " + "			CONVERT(DECIMAL(17, 2), 0) DCD_CONTRIBUCION, " + "			CONVERT(DECIMAL(17, 2), 0) DCD_IVA, " + "			CONVERT(DECIMAL(17, 2), 0) IVAANT, " + "			'ANEXO1' AS ID_DESTINO_GASTO," + "			'" + sUsuario + "'," + "			'ACTIVO'," + "			NULL, " + "			NULL" + "		FROM tAnexo1Encabezado tCE (nolock) " + "		LEFT JOIN tBeneficiario B (nolock) " + "			ON B.dRFC = 'CNF010405EG1' " + "		WHERE tCE.nFolioAnexo1 IN (" + listaIds + ")";
                pstmntHLayout = conn.prepareStatement(SqlLayoutGrabado);
                pstmntHLayout.executeUpdate();
                //URVP SE ARMA EL "SP" DETALLE
                //"	SUBSTRING(D.EP,20,2) cProgramaGeneral, " +
                String //"	SUBSTRING(D.EP,20,2) cProgramaGeneral, " +
                //penalizaciones
                Sql2 = " SELECT '1' ID_EVENTO," + "	'24.0.001' EVENTO," + "	SUBSTRING(D.EP,6,2) ID_RAMO_ML," + "	'RHQ'," + "	SUBSTRING(D.EP,1,4) aEjercicioFiscal," + "	SUBSTRING(D.EP,13,1) cGrupoFuncional," + "	SUBSTRING(D.EP,15,1) cFuncion," + "	SUBSTRING(D.EP,17,2) cSubFuncion," + "	CASE WHEN SUBSTRING(D.EP,20,2) IN (SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK)) THEN '00' ELSE SUBSTRING(D.EP,20,2) END AS cProgramaGeneral, " + "	SUBSTRING(D.EP,23,3) cActividadInstitucional, " + "	SUBSTRING(D.EP,27,4) cProgramaPresupuestario, " + "	SUBSTRING(D.EP,32,1) CCAP_157, " + "	SUBSTRING(D.EP,33,1)CCON_158," + "	SUBSTRING(D.EP,34,1) CPARG_300, " + "	SUBSTRING(D.EP,35,2) CPAR_159, " + "	SUBSTRING(D.EP,38,1) cTipoGasto, " + "	SUBSTRING(D.EP,40,1) cFuenteFinanciamiento, " + "	SUBSTRING(D.EP,42,2) cEntidadFederativa, " + "	SUBSTRING(D.EP,45,11)cCartera, " + "	'0000000000'," + "	'00'CCOP_163," + "	'000' PL," + "	'000' OFI," + "	'00000' AUX1," + "	'00000' AUX2," + "	'0000000000' AUX3," + "	CONVERT(decimal(17, 2),SUM(D.mImporte)) MONTO," + "	cMes MES_149," + "	'0' NRES," + "	CASE WHEN SUBSTRING(ep,32,5)='35801' THEN 'GD' ELSE 'PN' END TIPO_CONTRATO," + "	'000' CONC_MOV," + " 	CONVERT(DECIMAL(17, 2),0) DCD_ISR," + " 	CONVERT(DECIMAL(17, 2),0) DCD_IVA," + " 	CONVERT(DECIMAL(17, 2),0) DCD_MIL5," + " 	CONVERT(DECIMAL(17, 2),0) DCD_MIL2," + " 	CONVERT(decimal(17,2), 0) DCD_CONTRIBUCION," + " 	CONVERT(DECIMAL(17, 2),0) DCD_OTRAS_RET," + "	CONVERT(DECIMAL(17, 2),0)," + "	'' id_ctr_intdet " + "FROM tAnexo1Detalle D " + "WHERE nFolioAnexo1 IN (" + listaIds + ") " + "GROUP BY SUBSTRING(D.EP,6,2), " + "	SUBSTRING(D.EP,1,4), " + "	SUBSTRING(D.EP,13,1), " + "	SUBSTRING(D.EP,15,1), " + "	SUBSTRING(D.EP,17,2), " + "	SUBSTRING(D.EP,20,2), " + "	SUBSTRING(D.EP,23,3), " + "	SUBSTRING(D.EP,27,4), " + "	SUBSTRING(D.EP,32,1), " + "	SUBSTRING(D.EP,33,1), " + "	SUBSTRING(D.EP,34,1), " + "	SUBSTRING(D.EP,35,2), " + "	SUBSTRING(D.EP,38,1), " + "	SUBSTRING(D.EP,40,1), " + "	SUBSTRING(D.EP,42,2), " + "	SUBSTRING(D.EP,45,11), " + "	cMes, " + "	SUBSTRING(ep,32,5)";
                pstmntD = conn.prepareStatement(Sql2);
                rs2 = pstmntD.executeQuery();
                while (rs2.next()) {
                    String token = new String();
                    StringBuffer detalle = new StringBuffer();
                    for (int i = 1; i < 40; i++) {
                        detalle.append(token).append(rs2.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                        token = ",";
                    }
                    token = "";
                    detalle.append("\r\n");
                    arrListaComp.add(detalle.toString());
                }
                if (rs2 != null) {
                    rs2.close();
                }
                if (pstmntD != null) {
                    pstmntD.close();
                }
                // Aqui grabamos dentro de layouts creados detalle
                //"				tCEP.cProgramaGeneral," +
                String //"				tCEP.cProgramaGeneral," +
                SqlLayoutGrabadoDet = "	DECLARE @lsUR varchar(3), @lsEP varchar(100), @lmSuma money " + "	DECLARE curRGIntegrado CURSOR LOCAL FOR " + "			SELECT E.cUnidadResponsable, D.EP, SUM(D.mImporte)" + "			FROM tAnexo1Encabezado E, tAnexo1Detalle D " + "			WHERE E.nFolioAnexo1 = D.nFolioAnexo1 AND E.nFolioAnexo1 IN (" + listaIds + ") " + "			GROUP BY E.cUnidadResponsable, D.EP " + " 	OPEN curRGIntegrado " + " 		FETCH NEXT FROM curRGIntegrado into @lsUR, @lsEP, @lmSuma WHILE @@FETCH_STATUS = 0 " + " 		BEGIN " + " 			INSERT INTO tLayoutsCreadosAnexo1Detalle " + " 			SELECT TOP 1 " + strFolioLayout + "," + "				'1' ID_EVENTO," + "				'24.0.001' EVENTO," + "				ltrim(TCEP.cRamo) ID_RAMO_ML," + "				'RHQ'," + "				TCEP.aEjercicioFiscal," + "				TCEP.cGrupoFuncional," + "				tCEP.cFuncion," + "				tCEP.cSubFuncion," + "				CASE WHEN tCEP.cProgramaGeneral IN (SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK)) THEN '00' ELSE tCEP.cProgramaGeneral END AS cProgramaGeneral," + "				tCEP.cActividadInstitucional," + "				tCEP.cProgramaPresupuestario," + "				ltrim(substring(cpartida,1,1)) CCAP_157," + "				substring(cpartida,2,1) CCON_158," + "				substring(cpartida,3,1) CPARG_300," + "				substring(cpartida,4,2) CPAR_159," + "				tCEP.cTipoGasto," + "				tCEP.cFuenteFinanciamiento," + "				tCEP.cEntidadFederativa," + "				tCEP.cCartera," + "				ltrim('0000000' + tCEP.cUnidadEjecutora)," + "				substring(TCEP.cUnidadNorativa,2,2) CCOP_163," + "				'000' PL," + "				'000' OFI," + "				'00000' AUX1," + "				'00000' AUX2," + "				'0000000000' AUX3," + "				@lmSuma MONTO," + "				MONTH(GETDATE()) MES_149," + "				'0' NRES," + "				ltrim('PN') TIPO_CONTRATO," + "				'000' CONC_MOV," + "				CONVERT(DECIMAL(17, 2), 0) DCD_ISR, " + "				CONVERT(DECIMAL(17, 2), 0) DCD_IVA, " + "				CONVERT(DECIMAL(17, 2), 0) DCD_MIL5, " + "				CONVERT(DECIMAL(17, 2), 0) DCD_MIL2, " + "				CONVERT(DECIMAL(17, 2), 0) DCD_CONTRIBUCION, " + "				CONVERT(DECIMAL(17, 2), 0) DCD_OTRAS_RET, " + "				CONVERT(DECIMAL(17, 2), 0) DCD_IVADES, " + "				CONVERT(DECIMAL(17, 2), 0) DCD_PENALIZACION, " + "				'' id_ctr_intdet," + "				" + strFolioLayout + " 			FROM tAnexo1Detalle TPDD (nolock)" + "			INNER JOIN tAnexo1Encabezado TPDE ON TPDD.nFolioAnexo1 = TPDE.nFolioAnexo1 " + "			INNER JOIN tCatalogoEP TCEP ON TPDD.EP = TCEP.EP " + "			WHERE TPDE.cUnidadResponsable = @lsUR" + "				AND TPDD.EP = @lsEP" + "			FETCH NEXT FROM curRGIntegrado into @lsUR, @lsEP, @lmSuma" + "		END" + "	CLOSE curRGIntegrado " + " 	DEALLOCATE curRGIntegrado  ";
                pstmntHLayoutDet = conn.prepareStatement(SqlLayoutGrabadoDet);
                pstmntHLayoutDet.executeUpdate();
                insertaConsolidacionAnexo1(conn, sTimeStamp, usuario, ejercicioFiscal, folioGenerator);
            }
            return arrListaComp;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(rs3, false);
            CloseObject.closeObject(rs4, false);
            CloseObject.closeObject(pstmntH, false);
            CloseObject.closeObject(pstmntD, false);
            CloseObject.closeObject(pstmntHLayout, false);
            CloseObject.closeObject(pstmntHLayoutDet, false);
            CloseObject.closeObject(pstmUpSeqLayout, false);
            CloseObject.closeObject(pstmSeqLayout, false);
        }
    }

    public static int insertaConsolidacionAnexo1(Connection conn, String sTimeStamp, Usuario sUsuario, String ejercicioFiscal, String folioGenerator) throws Exception {
        log.info("Object: {}", "Insertando consolidacion de Ingreso del Anexo 1. Folio Integracion[" + sTimeStamp + "] Usuario[" + sUsuario + "] Ejercicio Fiscal[" + ejercicioFiscal + "]");
        int insertados = 0;
        String sqlInsertConsolidacion = "INSERT INTO tconsolidacionrelaciongastosencabezado " + "            (nFolioConsolidacion," + "             nidintegracion, " + "             fcarga, " + "             faplicacion, " + "             ctipopoliza, " + "             u_login, " + "             cunidadresponsablecontable, " + "             cdescripcionpoliza, " + "             cramo, " + "             cunidadresponsable, " + "             aejerciciofiscal) " + "SELECT ?								AS nFolioConsolidacion," + "        ?                             AS nIdIntegracion, " + "       Getdate()                     AS fCarga, " + "       Getdate()                     AS fAplicacion, " + "       'IN'                          AS cTipoPoliza, " + "       ?                             AS U_LOGIN, " + "       'RHQ'                         AS cUnidadResponsableContable, " + "       'Poliza de Ingreso Devengado y Recaudado de la Integración " + sTimeStamp + "/ANEXO1' AS cDescripcionPoliza, " + "       '16'                          AS cRamo, " + "       ?                             AS cUnidadResponsable, " + "       ?                             AS aEjercicioFiscal ";
        //+"       CASE WHEN ENCABEZADO.rfc <> 'TESOFE' THEN '' ELSE ENCABEZADO.rfc END AS RFC, "
        String //+"       CASE WHEN ENCABEZADO.rfc <> 'TESOFE' THEN '' ELSE ENCABEZADO.rfc END AS RFC, "
        //+"       CASE WHEN ENCABEZADO.RFC <> 'TESOFE' THEN '' ELSE ENCABEZADO.RFC END , "
        sqlInsertConsolidacionDetalle = "INSERT INTO dbo.tconsolidacionrelaciongastosdetalle" + "        ( nDocRenglon ," + "          nFolioConsolidacion ," + "          ep ," + "          cevento ," + "          ccentrocontable ," + "          cmes ," + "          ID_destino_gasto ," + "          ID_TIPO_CONCEPTO ," + "          partida ," + "          tipogasto ," + "          mimportemasiva ," + "          mImporteNegativo ," + "          nidintegracion ," + "          CTAB ," + "          RFC ," + "          ALM ," + "          OBGT" + "        )" + "SELECT Row_number() OVER (ORDER BY nfolioconsolidacion) AS nDocRenglon, " + "       consolidacion_encabezado.nfolioconsolidacion AS nFolioConsolidacion, " + "       ep, " + "       dbo.fn_evento_integracion_Anexo1(ep) AS cevento, " + "       DETALLE.ccentrocontable, " + "       DETALLE.cmes, " + "       '' id_destino_gasto, " + "       '' id_tipo_concepto, " + "       Substring(detalle.ep, 32, 5)                 AS partida, " + "       Substring(detalle.ep, 38, 1)                 AS tipogasto, " + "       Sum(DETALLE.mImporte)                  AS mimportemasiva, " + "       Sum(DETALLE.mImporteNegativo)         AS mImporteNegativo, " + "       LAYOUT.sauxiliarcomodin                      AS nidintegracion, " + "       LAYOUT.scuenta_bancaria                      AS CTAB, " + "       '' RFC, " + "       '' alm, " + "       Substring(detalle.ep, 32, 5)                 AS OBGT " + "FROM   dbo.tAnexo1Encabezado ENCABEZADO WITH (nolock) " + "INNER JOIN dbo.tAnexo1Detalle DETALLE WITH (nolock) " + "		  ON ENCABEZADO.nFolioAnexo1 = DETALLE.nFolioAnexo1 " + "LEFT OUTER JOIN dbo.tLayoutsCreadosAnexo1Header LAYOUT WITH (nolock) " + "       ON ENCABEZADO.canocontrarrecibo = LAYOUT.snocontrarrecibo " + "INNER JOIN dbo.tconsolidacionrelaciongastosencabezado consolidacion_encabezado WITH (nolock) " + "       ON LAYOUT.sauxiliarcomodin = consolidacion_encabezado.nidintegracion " + "WHERE  LAYOUT.sauxiliarcomodin IS NOT NULL " + "       AND nfolioconsolidacion = ? " + "GROUP  BY ep, " + "       consolidacion_encabezado.nfolioconsolidacion, " + "       DETALLE.ccentrocontable, " + "       DETALLE.cmes, " + "       LAYOUT.sauxiliarcomodin, " + "       scuenta_bancaria, " + "       Substring(detalle.ep, 32, 5)";
        PreparedStatement psInsertaEncabezado = null;
        PreparedStatement psInsertaDetalle = null;
        ResultSet rsFolioConsolidacion = null;
        int nFolioConsolidacion = -1;
        try {
            Caso c = PagosDiversosRGManager.generaCaso(conn, sUsuario, folioGenerator);
            nFolioConsolidacion = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1), 10);
            log.debug("Object: {}", "Query Insert Encabezado[" + sqlInsertConsolidacion + "]");
            log.debug("Object: {}", "Query Insert Detalle[" + sqlInsertConsolidacionDetalle + "]");
            psInsertaEncabezado = conn.prepareStatement(sqlInsertConsolidacion, Statement.RETURN_GENERATED_KEYS);
            psInsertaDetalle = conn.prepareStatement(sqlInsertConsolidacionDetalle);
            psInsertaEncabezado.setInt(1, nFolioConsolidacion);
            psInsertaEncabezado.setString(2, sTimeStamp);
            psInsertaEncabezado.setString(3, sUsuario.getLogin());
            psInsertaEncabezado.setString(4, "");
            psInsertaEncabezado.setString(5, ejercicioFiscal);
            insertados += psInsertaEncabezado.executeUpdate();
            log.debug("Object: {}", "Insertados en encabezado: " + insertados + " registros ");
            rsFolioConsolidacion = psInsertaEncabezado.getGeneratedKeys();
            psInsertaDetalle.setInt(1, nFolioConsolidacion);
            insertados += psInsertaDetalle.executeUpdate();
            log.debug("Object: {}", "Insertados en detalle: " + insertados + " registros ");
            return insertados;
        } finally {
            CloseObject.closeObject(rsFolioConsolidacion, false);
            CloseObject.closeObject(psInsertaEncabezado, false);
            CloseObject.closeObject(psInsertaDetalle, false);
        }
    }

    public static ArrayList<String> CreaDocumentacionComprobatoria(Connection conn, String listaIds, String sTimeStamp, boolean bIntegra) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntH = null;
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        String sREFERENCIA1_107 = "'" + sTimeStamp + "'";
        if (bIntegra)
            sREFERENCIA1_107 = "DCD.caNoContrarrecibo";
        //URVP -- ENCABEZADO DEL DOCCOMP
        String Sql = "	select distinct " + "		PDE.nFolioAnexo1," + "		'H' H," + "		PDE.cRamo," + "		'RHQ'," + "		'' SOL_PAGO," + "		'3'," + "		" + sREFERENCIA1_107 + " FOLIO_INTERNO," + " 		" + sREFERENCIA1_107 + " COMODIN" + "	from  dbo.tAnexo1Encabezado PDE" + "	WHERE PDE.nFolioAnexo1 in (" + listaIds + ") ";
        pstmntH = conn.prepareStatement(Sql);
        System.out.println(Sql);
        rs = pstmntH.executeQuery();
        while (rs.next()) {
            //String nFolioCompromiso = rs.getString(1);
            String encabezado = rs.getString(2).trim() + "," + rs.getString(3).trim() + "," + rs.getString(4).trim() + "," + rs.getString(5).trim() + "," + rs.getString(6).trim() + "," + rs.getString(7).trim() + "," + rs.getString(8).trim();
            encabezado = encabezado + "\r\n";
            arrListaComp.add(encabezado);
            String sCampo = "'S04929'";
            //URVP DETALLE DEL DOCCOMP
            //07 ??
            String //07 ??
            Sql2 = " select distinct " + "		PDE.cRamo," + "		PDE.caNoContrarrecibo, " + "		CONVERT(nvarchar(10), PDE.fAplicacion,103)," + "		CONVERT(nvarchar(10), PDE.fAplicacion,103) + ' 12:00:00 a.m.', " + "		" + sCampo + "," + "		case when B.cExtranjero = 1 then '05' else '04' end 'TipoBen'," + "		'85' TIPO_OPE," + "		'05' TIVA," + "		'0' DCD_VALOR," + "		CONVERT(decimal(17, 2), PDE.mImporte) MONTO," + "		CONVERT(DECIMAL(17, 2), 0) DCD_IVA," + "		CONVERT(DECIMAL(17, 2), 0) DCD_IVADES," + "		CONVERT(DECIMAL(17, 2), 0) DCD_ISR," + "		CONVERT(DECIMAL(17, 2), 0) DCD_MIL5," + "		CONVERT(DECIMAL(17, 2), 0) DCD_MIL2," + "		CONVERT(DECIMAL(17, 2), 0) DCD_OTRAS_RET," + "		CONVERT(DECIMAL(17, 2), 0) DCD_PENALIZACION," + "		CONVERT(DECIMAL(17, 2), 0) DCD_CONTRIBUCION," + "		'0' DCD_CTOEXT," + "		PDE.caNoContrarrecibo DCD_FACTURA," + "		PDE.cConcepto," + "		PDE.caNoContrarrecibo" + "	from dbo.tAnexo1Encabezado PDE" + "	INNER JOIN dbo.tAnexo1Detalle RGD ON PDE.nFolioAnexo1 = RGD.nFolioAnexo1" + "	INNER JOIN tBeneficiario B " + "		ON B.dRFC = 'CNF010405EG1' " + "	where PDE.nFolioAnexo1 in (" + listaIds + ")";
            pstmntD = conn.prepareStatement(Sql2);
            System.out.println(Sql2);
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
            if (rs2 != null) {
                rs2.close();
            }
            if (pstmntD != null) {
                pstmntD.close();
            }
            if (!bIntegra)
                break;
        }
        if (rs != null) {
            rs.close();
        }
        if (pstmntH != null) {
            pstmntH.close();
        }
        return arrListaComp;
    }

    public static int updateHeaderAnexo1EnvioSICOP(Connection conn, String listaIds) throws SQLException {
        PreparedStatement pstmnt = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("UPDATE tAnexo1Encabezado SET nEnviadoSICOP = 1 WHERE nFolioAnexo1 IN (" + listaIds + ")");
            retval = pstmnt.executeUpdate();
            conn.commit();
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
        }
        return retval;
    }

    public static int UpdateStatus(Connection conn, String Integracion, String usuario) throws SQLException {
        PreparedStatement pstmnt = null, updateLayout = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("UPDATE tAnexo1Encabezado SET nEnviadoSICOP = 0 WHERE nFolioAnexo1 IN (SELECT Folio FROM vListaAnexo1ConLayout WHERE sAuxiliarComodin = '" + Integracion + "')");
            retval = pstmnt.executeUpdate();
            updateLayout = conn.prepareStatement("UPDATE tLayoutsCreadosAnexo1Header SET cEstatus = 'DEVUELTO', fDevolucionLayout = GETDATE(), sLoginDevolucion = '" + usuario + "' WHERE sAuxiliarComodin = '" + Integracion + "'");
            updateLayout.executeUpdate();
            conn.commit();
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            if (updateLayout != null) {
                updateLayout.close();
            }
            pstmnt = null;
            updateLayout = null;
        }
        return retval;
    }

    public static boolean exists(Connection conn, int nFolioAnexo) throws Exception {
        String query = "SELECT COUNT(*) AS existe FROM dbo.tAnexo1Encabezado WHERE nFolioAnexo1 = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean existe = false;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, nFolioAnexo);
            rs = ps.executeQuery();
            if (rs.next())
                existe = rs.getInt(1) > 0;
            return existe;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static int updateEncabezado(Connection conn, Anexo1Encabezado encabezado) throws Exception {
        String query = "UPDATE tanexo1encabezado " + "SET    fcaptura = CONVERT(DATE, ?, 103), " + "       faplicacion = CONVERT(DATE, ?, 103), " + "       cconcepto = ?, " + "       canocontrarrecibo = ?, " + "       ctipoanexo = ?, " + "       cidcontrato = ?, " + "       mimporte = ? " + "WHERE  nfolioanexo1 = ? ";
        PreparedStatement psUpdate = null;
        try {
            int afectados = 0;
            psUpdate = conn.prepareStatement(query);
            psUpdate.setDate(1, new Date(encabezado.getfApliacion().getTime()));
            psUpdate.setDate(2, new Date(encabezado.getfApliacion().getTime()));
            psUpdate.setString(3, encabezado.getcConcepto());
            psUpdate.setString(4, encabezado.getcaNoContrarrecibo());
            psUpdate.setString(5, encabezado.getcTipoAnexo());
            psUpdate.setString(6, encabezado.getcIdContrato());
            psUpdate.setDouble(7, encabezado.getmImporte());
            psUpdate.setInt(8, encabezado.getnFolioAnexo());
            afectados = psUpdate.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(psUpdate);
        }
    }

    public static void updateRenglones(Connection conn, Integer nFolio) throws Exception {
        String sqlDetalle = "SELECT nDocRenglon,Ep,cMes FROM dbo.tAnexo1Detalle WITH(NOLOCK) WHERE nFolioAnexo1 = ?";
        String sqlUpdate = "UPDATE dbo.tAnexo1Detalle SET nDocRenglon = ? WHERE nDocRenglon = ? AND Ep = ? AND cMes = ? AND nFolioAnexo1 = ?";
        String sqlDeletemImporteCero = "DELETE tAnexo1Detalle WHERE mImporte = 0 AND nFolioAnexo1 = ?";
        PreparedStatement psDetalle = null, psUpdate = null;
        ResultSet rsDetalle = null;
        Integer renglon, cont;
        String ep, cmes;
        try {
            //SE ELIMINAN LOS REGISTROS QUE SE HAYAN QUEDADO CON IMPORTE 0 EN EL UPDATE
            psDetalle = conn.prepareStatement(sqlDeletemImporteCero);
            psDetalle.setInt(1, nFolio);
            psDetalle.executeUpdate();
            psDetalle = conn.prepareStatement(sqlDetalle);
            psDetalle.setInt(1, nFolio);
            cont = 1;
            rsDetalle = psDetalle.executeQuery();
            while (rsDetalle.next()) {
                renglon = Integer.parseInt(rsDetalle.getString("nDocRenglon"), 10);
                ep = rsDetalle.getString("Ep");
                cmes = rsDetalle.getString("cMes");
                psUpdate = conn.prepareStatement(sqlUpdate);
                psUpdate.setInt(1, cont);
                psUpdate.setInt(2, renglon);
                psUpdate.setString(3, ep);
                psUpdate.setString(4, cmes);
                psUpdate.setInt(5, nFolio);
                log.info("Object: {}", "Update " + cont + ": [UPDATE dbo.tAnexo1Detalle SET nDocRenglon = " + cont + " WHERE nDocRenglon = " + renglon + " AND Ep = '" + ep + "' AND cMes = " + cmes + " AND nFolioAnexo1 = " + nFolio + "]");
                psUpdate.executeUpdate();
                cont++;
            }
        } finally {
            CloseObject.closeObject(psDetalle);
            CloseObject.closeObject(psUpdate);
            CloseObject.closeObject(rsDetalle);
        }
    }

    public static void insertUpdateApartado(Connection conn, Integer nFolio, String accion) throws Exception {
        String sqlInsertEncabezado = "INSERT INTO tPagoApartadoEncabezado " + "		SELECT ?, " + "			'ANEXO1'," + "			nFolioAnexo1," + "			fAplicacion," + "			cRamo," + "			caNoContrarrecibo," + "			aEjercicioFiscal," + "			NULL," + "			NULL," + "			cTipoPoliza," + "			cUnidadResponsableContable," + "			NULL," + "			NULL," + "			'Movimiento Comodin de Radicado: '+caNoContrarrecibo" + "		FROM tAnexo1Encabezado WITH(NOLOCK) " + "		WHERE nFolioAnexo1 = ?";
        String sqlInsertDetalle = "INSERT INTO tPagoApartadoDetalle " + "			SELECT ?," + "				nDocRenglon," + "				cMes," + "				'DIS_ANEXO1_T'," + "				aEjercicioFiscal," + "				cCentroContable," + "				Ep," + "				mImporte " + "			FROM tAnexo1Detalle WITH(NOLOCK) " + "			WHERE nFolioAnexo1 = ?";
        String nFolioPagoApartado = "-1";
        PreparedStatement pstmUpApartado = null, pstmSeqApartado = null, pstmInsert = null, pstmDelete = null;
        ResultSet rsSequence = null;
        try {
            if (accion.equals("INSERT")) {
                //VERIFICA SI EXISTE ENCABEZADO PARA EL MISMO DOCUMENTO
                pstmSeqApartado = conn.prepareStatement("SELECT nFolioPagoApartado FROM tPagoApartadoEncabezado WITH(NOLOCK) WHERE cTipoPago='ANEXO1' AND nFolioPago = ?");
                pstmSeqApartado.setInt(1, nFolio);
                rsSequence = pstmSeqApartado.executeQuery();
                if (rsSequence.next()) {
                    //SI EXISTE SE TOMA EL MISMO FOLIO DE APARTADO
                    nFolioPagoApartado = rsSequence.getString("nFolioPagoApartado");
                } else {
                    //SI NO EXISTE SE ACTUALIZA Y SE TOMA DE CF_SEQUENCE
                    pstmUpApartado = conn.prepareStatement("UPDATE CF_SEQUENCE  WITH (ROWLOCK) SET seq_value = seq_value + 1 WHERE seq_name = 'APARTADO'");
                    pstmUpApartado.executeUpdate();
                    pstmSeqApartado = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'APARTADO' ");
                    rsSequence = pstmSeqApartado.executeQuery();
                    if (rsSequence.next()) {
                        nFolioPagoApartado = rsSequence.getString("seq_value");
                    }
                    //SE INSERTA ENCABEZADO
                    log.info("Object: {}", "Insert Enzabezado Movimiento Comodin Radicado: [" + sqlInsertEncabezado + " (" + nFolioPagoApartado + ", " + nFolio + ")]");
                    pstmInsert = conn.prepareStatement(sqlInsertEncabezado);
                    pstmInsert.setInt(1, Integer.parseInt(nFolioPagoApartado, 10));
                    pstmInsert.setInt(2, nFolio);
                    pstmInsert.executeUpdate();
                }
                log.info("Object: {}", "Insert Detalle Movimiento Comodin Radicado: [" + sqlInsertDetalle + " (" + nFolioPagoApartado + ", " + nFolio + ")]");
                pstmInsert = conn.prepareStatement(sqlInsertDetalle);
                pstmInsert.setInt(1, Integer.parseInt(nFolioPagoApartado, 10));
                pstmInsert.setInt(2, nFolio);
                pstmInsert.executeUpdate();
            } else {
                //DELETE E INSERT EN TPAGOAPARTADODETALLE PARA LA APLICACION DEL MOVIMIENTO COMODIN AL RADICADO
                pstmSeqApartado = conn.prepareStatement("SELECT nFolioPagoApartado FROM tPagoApartadoEncabezado WITH(NOLOCK) WHERE cTipoPago='ANEXO1' AND nFolioPago = ?");
                pstmSeqApartado.setInt(1, nFolio);
                rsSequence = pstmSeqApartado.executeQuery();
                if (rsSequence.next()) {
                    nFolioPagoApartado = rsSequence.getString("nFolioPagoApartado");
                }
                log.info("Object: {}", "Delete Detalle Movimiento Comodin Radicado: [DELETE tPagoApartadoDetalle WHERE nFolioPagoApartado = " + nFolioPagoApartado + "]");
                pstmDelete = conn.prepareStatement("DELETE tPagoApartadoDetalle WHERE nFolioPagoApartado = ?");
                pstmDelete.setInt(1, Integer.parseInt(nFolioPagoApartado, 10));
                pstmDelete.executeUpdate();
                log.info("Object: {}", "Insert Detalle Movimiento Comodin Radicado: [" + sqlInsertDetalle + " (" + nFolioPagoApartado + ", " + nFolio + ")]");
                pstmInsert = conn.prepareStatement(sqlInsertDetalle);
                pstmInsert.setInt(1, Integer.parseInt(nFolioPagoApartado, 10));
                pstmInsert.setInt(2, nFolio);
                pstmInsert.executeUpdate();
            }
        } finally {
            CloseObject.closeObject(pstmUpApartado);
            CloseObject.closeObject(pstmSeqApartado);
            CloseObject.closeObject(pstmInsert);
            CloseObject.closeObject(pstmDelete);
            CloseObject.closeObject(rsSequence);
        }
    }

    public static void insertUpdateApartadoNuevo(Connection conn, Integer nFolio) throws Exception {
        String sqlInsertEncabezado = "INSERT INTO tPagoApartadoEncabezado " + "		SELECT ?, " + "			'ANEXO1'," + "			nFolioAnexo1," + "			fAplicacion," + "			cRamo," + "			caNoContrarrecibo," + "			aEjercicioFiscal," + "			NULL," + "			NULL," + "			cTipoPoliza," + "			cUnidadResponsableContable," + "			NULL," + "			NULL," + "			'Movimiento Comodin de Radicado: '+caNoContrarrecibo" + "		FROM tAnexo1Encabezado WITH(NOLOCK) " + "		WHERE nFolioAnexo1 = ?";
        String sqlInsertDetalle = "INSERT INTO tPagoApartadoDetalle " + "			SELECT ?," + "				nDocRenglon," + "				cMes," + "				'DIS_ANEXO1_T'," + "				aEjercicioFiscal," + "				cCentroContable," + "				Ep," + "				mImporte " + "			FROM tAnexo1Detalle WITH(NOLOCK) " + "			WHERE nFolioAnexo1 = ?";
        String nFolioPagoApartado = "-1";
        PreparedStatement pstmUpApartado = null, pstmSeqApartado = null, pstmInsert = null, pstmDelete = null;
        ResultSet rsSequence = null;
        try {
            //VERIFICA SI EXISTE ENCABEZADO PARA EL MISMO DOCUMENTO
            pstmSeqApartado = conn.prepareStatement("SELECT nFolioPagoApartado FROM tPagoApartadoEncabezado WITH(NOLOCK) WHERE cTipoPago='ANEXO1' AND nFolioPago = ?");
            pstmSeqApartado.setInt(1, nFolio);
            rsSequence = pstmSeqApartado.executeQuery();
            if (rsSequence.next()) {
                //SI EXISTE SE TOMA EL MISMO FOLIO DE APARTADO
                nFolioPagoApartado = rsSequence.getString("nFolioPagoApartado");
                log.info("Object: {}", "Delete Detalle Movimiento Comodin Radicado: [DELETE tPagoApartadoDetalle WHERE nFolioPagoApartado = " + nFolioPagoApartado + "]");
                pstmDelete = conn.prepareStatement("DELETE tPagoApartadoDetalle WHERE nFolioPagoApartado = ?");
                pstmDelete.setInt(1, Integer.parseInt(nFolioPagoApartado, 10));
                pstmDelete.executeUpdate();
            } else {
                //SI NO EXISTE SE ACTUALIZA Y SE TOMA DE CF_SEQUENCE
                pstmUpApartado = conn.prepareStatement("UPDATE CF_SEQUENCE  WITH (ROWLOCK) SET seq_value = seq_value + 1 WHERE seq_name = 'APARTADO'");
                pstmUpApartado.executeUpdate();
                pstmSeqApartado = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'APARTADO' ");
                rsSequence = pstmSeqApartado.executeQuery();
                if (rsSequence.next()) {
                    nFolioPagoApartado = rsSequence.getString("seq_value");
                }
                //SE INSERTA ENCABEZADO
                log.info("Object: {}", "Insert Enzabezado Movimiento Comodin Radicado: [" + sqlInsertEncabezado + " (" + nFolioPagoApartado + ", " + nFolio + ")]");
                pstmInsert = conn.prepareStatement(sqlInsertEncabezado);
                pstmInsert.setInt(1, Integer.parseInt(nFolioPagoApartado, 10));
                pstmInsert.setInt(2, nFolio);
                pstmInsert.executeUpdate();
            }
            log.info("Object: {}", "Insert Detalle Movimiento Comodin Radicado: [" + sqlInsertDetalle + " (" + nFolioPagoApartado + ", " + nFolio + ")]");
            pstmInsert = conn.prepareStatement(sqlInsertDetalle);
            pstmInsert.setInt(1, Integer.parseInt(nFolioPagoApartado, 10));
            pstmInsert.setInt(2, nFolio);
            pstmInsert.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmUpApartado);
            CloseObject.closeObject(pstmSeqApartado);
            CloseObject.closeObject(pstmInsert);
            CloseObject.closeObject(pstmDelete);
            CloseObject.closeObject(rsSequence);
        }
    }
}
