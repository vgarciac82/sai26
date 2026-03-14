package com.syc.adquisiciones.manager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Arrays;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.servlet.GestionInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ReportesManager implements GestionInterface {

    private static Logger log = LoggerFactory.getLogger(ReportesManager.class);

    public String generaApartadoPrecomCompromiso(Connection conn, String plantillaPath, String file_name) throws Exception {
        Workbook workbook = null;
        String query = "";
        file_name = file_name + ".xls";
        ResultSet rs = null;
        PreparedStatement pstm = null;
        InputStream fs = null;
        InputStream fsArchivo = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        int columnaInicio = 0;
        int renglonInicio = 2;
        BigDecimal[] totales = null;
        BigDecimal[] resumenTotal = new BigDecimal[4];
        try {
            //Apartado
            query = queryApartado();
            log.info("Object: {}", query.toString());
            pstm = conn.prepareStatement(query);
            rs = pstm.executeQuery();
            //workbook=creaWorkbookXLS( file_name, plantillaPath);
            File cFileExcelPlantilla = new File(plantillaPath);
            fs = new FileInputStream(cFileExcelPlantilla);
            com.syc.gestion.util.Util.copiaArchivo(fs, file_name);
            fsArchivo = new FileInputStream(cFileExcelPlantilla);
            workbook = new HSSFWorkbook(fsArchivo);
            CellStyle estiloTabla = workbook.createCellStyle();
            estiloTabla.setBorderRight(BorderStyle.THIN);
            estiloTabla.setBorderLeft(BorderStyle.THIN);
            estiloTabla.setBorderTop(BorderStyle.THIN);
            estiloTabla.setBorderBottom(BorderStyle.THIN);
            totales = writeWorkbookXLS(workbook, rs, renglonInicio, columnaInicio, estiloTabla, APARTADO);
            renglonInicio = 5;
            columnaInicio = 1;
            resumenTotal[0] = totales[1];
            resumenTotal[3] = totales[1];
            writeTotales(workbook, renglonInicio, columnaInicio, totales, estiloTabla);
            totales = null;
            //Precompromiso
            query = queryPrecompromiso();
            log.info("Object: {}", query.toString());
            if (pstm != null)
                pstm.close();
            pstm = null;
            pstm = conn.prepareStatement(query);
            rs = pstm.executeQuery();
            columnaInicio = 4;
            renglonInicio = 2;
            totales = writeWorkbookXLS(workbook, rs, renglonInicio, columnaInicio, estiloTabla, PRECOMPROMISO);
            columnaInicio = 5;
            renglonInicio = 5;
            resumenTotal[1] = totales[1];
            resumenTotal[3] = resumenTotal[3].add(totales[1]);
            writeTotales(workbook, renglonInicio, columnaInicio, totales, estiloTabla);
            totales = null;
            //Compromiso
            query = queryCompromiso();
            log.info("Object: {}", query.toString());
            if (pstm != null)
                pstm.close();
            pstm = null;
            pstm = conn.prepareStatement(query);
            rs = pstm.executeQuery();
            columnaInicio = 0;
            renglonInicio = 9;
            totales = writeWorkbookXLS(workbook, rs, renglonInicio, columnaInicio, estiloTabla, COMPROMISO);
            columnaInicio = 2;
            renglonInicio = 22;
            writeTotales(workbook, renglonInicio, columnaInicio, totales, estiloTabla);
            totales = null;
            //Disponible
            query = queryDisponible();
            log.info("Object: {}", query.toString());
            if (pstm != null)
                pstm.close();
            pstm = null;
            pstm = conn.prepareStatement(query);
            rs = pstm.executeQuery();
            columnaInicio = 0;
            renglonInicio = 27;
            totales = writeWorkbookXLS(workbook, rs, renglonInicio, columnaInicio, estiloTabla, DISPONIBLE);
            columnaInicio = 1;
            renglonInicio = 30;
            resumenTotal[2] = totales[0];
            resumenTotal[3] = resumenTotal[3].add(totales[0]);
            writeTotales(workbook, renglonInicio, columnaInicio, totales, estiloTabla);
            columnaInicio = 5;
            renglonInicio = 27;
            writeResumen(workbook, renglonInicio, columnaInicio, resumenTotal, estiloTabla);
            File fsalida = new File(file_name);
            fos = new FileOutputStream(fsalida);
            bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos);
            bos.flush();
        } catch (Exception e) {
            // TODO: handle exception
            log.error("Object: {}", e.getMessage());
            throw (e);
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (fs != null) {
                fs.close();
            }
            if (fsArchivo != null) {
                fsArchivo.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fos != null) {
                fos.close();
            }
            rs = null;
            pstm = null;
            fsArchivo = null;
            fs = null;
            bos = null;
            fos = null;
            totales = null;
            resumenTotal = null;
        }
        return file_name;
    }

    public BigDecimal[] writeWorkbookXLS(Workbook wb, ResultSet rs, int renglonInicio, int columnaInicio, CellStyle estiloTabla, int nCuenta) throws Exception {
        ResultSetMetaData rsMetadata = null;
        Sheet sheet0 = wb.getSheetAt(0);
        Row rw = null;
        int cnt = 0;
        BigDecimal[] totales = null;
        rsMetadata = rs.getMetaData();
        if (nCuenta <= 82102 || nCuenta == 82106) {
            totales = new BigDecimal[rsMetadata.getColumnCount() - 1];
            Arrays.fill(totales, BigDecimal.ZERO);
        } else if (nCuenta == 82103) {
            totales = new BigDecimal[rsMetadata.getColumnCount() - 2];
            Arrays.fill(totales, BigDecimal.ZERO);
        }
        while (rs.next()) {
            rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                com.syc.gestion.util.Util.createExcelCellRep((i + columnaInicio), rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                if ((nCuenta <= 82102 || nCuenta == 82106) && i > 0) {
                    totales[i - 1] = totales[i - 1].add(rs.getBigDecimal(rsMetadata.getColumnName(i + 1)));
                } else if (nCuenta == 82103 && i > 1) {
                    totales[i - 2] = totales[i - 2].add(rs.getBigDecimal(rsMetadata.getColumnName(i + 1)));
                }
            }
            cnt++;
        }
        return totales;
    }

    public Workbook creaWorkbookXLS(String file_name, String plantillaPath) throws IOException {
        File cFileExcelPlantilla = new File(plantillaPath);
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        com.syc.gestion.util.Util.copiaArchivo(fs, file_name);
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook wb = new HSSFWorkbook(fsArchivo);
        return wb;
    }

    private String queryApartado() {
        return "SELECT sub.capitulo,saldos.totalApartadoSaldos,sub.totalApartadoDocumentos " + " FROM (SELECT SUM(mImporte) totalApartadoDocumentos,SUBSTRING(det.EP,32,1)capitulo " + " FROM dbo.mSolicitud sol WITH(NOLOCK)  " + " INNER JOIN dbo.tApartadoEncabezado WITH(NOLOCK) " + " ON sol.cIdSolicitud = dbo.tApartadoEncabezado.cIdSolicitud " + " INNER JOIN dbo.tApartadoDetalle AS det WITH(NOLOCK) " + " ON dbo.tApartadoEncabezado.nFolioApartado = det.nFolioApartado " + " AND dbo.tApartadoEncabezado.cCentroContable = det.cCentroContable " + " WHERE nIdEstado=3 AND tApartadoEncabezado.cDocumentoHaplicado='S' " + " AND sol.cIdSolicitud NOT IN( " + " SELECT DISTINCT presel.cIdSolicitud FROM dbo.mConsolidadoSolicitud conSol WITH(NOLOCK) " + " INNER JOIN dbo.mConsolidado cons WITH(NOLOCK) ON conSol.cEjercicio = cons.cEjercicio " + " AND conSol.cIdConsolidado = cons.cIdConsolidado " + " AND conSol.cIdTipoConsolidado = cons.cIdTipoConsolidado " + " AND conSol.cIdUnidadEjecutora		 = cons.cIdUnidadEjecutora " + " AND conSol.nIdConsecutivo = cons.nIdConsecutivo " + " INNER JOIN dbo.mConsolidadoPreseleccionSolicitudes AS presel WITH(NOLOCK) " + " ON cons.cEjercicio = presel.cEjercicio  " + " AND cons.cIdTipoConsolidado = presel.cIdTipoConsolidado  " + " AND cons.cIdUnidadEjecutora = presel.cIdUnidadEjecutora " + " AND cons.nIdConsecutivo = presel.nIdConsecutivo " + " WHERE cons.nIdEstado=2 " + " UNION  " + " SELECT DISTINCT contModPart.cIdSolicitud FROM dbo.mContratoModificado AS conMod WITH(NOLOCK) " + " INNER JOIN dbo.mContratoModificadoPartida AS contModPart WITH(NOLOCK) " + " ON conMod.cEjercicio = contModPart.cEjercicio " + " AND conMod.cIdContrato = contModPart.cIdContrato " + " AND conMod.nConsecutivoModificacion = contModPart.nConsecutivoModificacion " + " WHERE conMod.nEstado IN(3,4) " + " ) " + " GROUP BY SUBSTRING(det.EP,32,1) " + " )sub " + " LEFT JOIN( " + " SELECT  " + " SUM(mSaldoArrastre) totalApartadoSaldos " + " ,SUBSTRING(cSubCuenta,32,1) capitulo " + " FROM dbo.tSaldosVista WITH(NOLOCK) WHERE nCuenta LIKE'82101%' " + " AND SUBSTRING(cSubCuenta,32,1) IN(2,3,5) " + " GROUP BY SUBSTRING(cSubCuenta,32,1) " + " )saldos ON saldos.capitulo=sub.capitulo " + " ORDER BY sub.capitulo ";
    }

    private String queryPrecompromiso() {
        return "SELECT query.capitulo,saldo.totalPrecomprometido totalPrecomprometido " + "  ,query.totalPrecomDocumentos " + "   FROM  " + "  (SELECT SUM(totalPrecom) totalPrecomDocumentos,capitulo FROM ( " + /* Precompromiso de consolidad y procedimiento*/
        "  SELECT  SUM(mImporte)totalPrecom " + "  ,SUBSTRING(EP,32,1)capitulo " + "  FROM dbo.tPrecomMaterialesEncabezado precom WITH(NOLOCK)  " + "  INNER JOIN dbo.tPrecomMaterialesDetalle AS det WITH(NOLOCK)  " + "  ON precom.nFolioPrecomMateriales = det.nFolioPrecomMateriales " + "  WHERE  " + "  precom.cDocumentoHaplicado='S' " + "  AND det.cEvento IN('PRECOM_MAT','DISP_PRECOMMAT') " + "  AND precom.cIdConsolidado NOT IN( " + "  	SELECT pedCont.cIdProcedimiento FROM dbo.tPreCompromisoEncabezado AS precom WITH(NOLOCK) " + "  	INNER JOIN( " + "  		SELECT cIdContratoDefinitivo,cIdProcedimiento FROM dbo.mContrato AS cont WITH(NOLOCK) " + "  		UNION " + "  		SELECT cIdPedidoDefinitivo,cIdProcedimiento FROM dbo.mPedido AS ped WITH(NOLOCK) " + "  	)pedCont ON pedCont.cIdContratoDefinitivo=precom.cIdContrato " + "  	WHERE precom.cDocumentoHaplicado='S' " + "  	) " + "  AND precom.cIdConsolidado NOT IN( " + "  	SELECT proced.cIdConsolidado FROM dbo.tPreCompromisoEncabezado AS precom WITH(NOLOCK) " + "  	INNER JOIN( " + "  		SELECT cIdContratoDefinitivo,cIdProcedimiento FROM dbo.mContrato AS cont WITH(NOLOCK) " + "  		UNION " + "  		SELECT cIdPedidoDefinitivo,cIdProcedimiento FROM dbo.mPedido AS ped WITH(NOLOCK) " + "  	)pedCont ON pedCont.cIdContratoDefinitivo=precom.cIdContrato " + "  	INNER JOIN dbo.mProcedimiento AS proced WITH(NOLOCK) ON proced.cIdProcedimiento=pedCont.cIdProcedimiento " + "  	WHERE precom.cDocumentoHaplicado='S'  AND proced.nIdEstado=2 " + "  ) " + "  GROUP BY SUBSTRING(EP,32,1) " + /*Procedimientos que generan mas de un pedido o contrato y que un pedido o contrato no se avanza */
        "  UNION " + "  SELECT  " + "  SUM(precomdetalle.mImporte)-SUM(ISNULL(procedPartidas.totalProced,0)) remanentePrecom " + "  ,SUBSTRING(precomdetalle.EP,32,1)capitulo " + "   FROM  " + "  (SELECT cIdContratoDefinitivo,cIdProcedimiento,nIdEstado,ConsecutivoPRECOMP,cIdRFC,nIdconsecutivoAdj FROM dbo.mContrato AS contrato WITH(NOLOCK)  " + "  UNION " + "  SELECT cIdPedidoDefinitivo,cIdProcedimiento,nIdEstado,ConsecutivoPRECOMP,cIdRFC,nIdconsecutivoAdj FROM dbo.mPedido pedido WITH(NOLOCK) " + "  )contratos " + "  INNER JOIN ( " + "  SELECT COUNT(cIdProcedimiento)repetido,cIdProcedimiento FROM dbo.mProcedimientoAdjudicacion WITH(NOLOCK) " + "  GROUP BY cIdProcedimiento " + "  HAVING COUNT(cIdProcedimiento)>1)procedimientoAdjudicado " + "  ON contratos.cIdProcedimiento = procedimientoAdjudicado.cIdProcedimiento " + "  INNER JOIN( " + "  	SELECT SUM(mMontoNetoLinea)totalProced,cIdProcedimiento,cIdRFC,nIdconsecutivoAdj FROM dbo.mProcedimientoAdjudicacionPartidas WITH(NOLOCK) " + "  	GROUP BY cIdProcedimiento,cIdRFC,nIdconsecutivoAdj " + "  )procedPartidas ON procedimientoAdjudicado.cIdProcedimiento = procedPartidas.cIdProcedimiento " + "  AND contratos.cIdRFC<>procedPartidas.cIdRFC AND contratos.nIdconsecutivoAdj<>procedPartidas.nIdconsecutivoAdj " + "  INNER JOIN dbo.mProcedimiento AS proced WITH(NOLOCK) ON procedimientoAdjudicado.cIdProcedimiento = proced.cIdProcedimiento " + "  INNER JOIN dbo.tPrecomMaterialesEncabezado AS precomMat WITH(NOLOCK) " + "  ON (proced.cIdConsolidado = precomMat.cIdConsolidado OR proced.cIdProcedimiento=precomMat.cIdConsolidado) " + "  INNER JOIN dbo.tPrecomMaterialesDetalle AS precomdetalle WITH(NOLOCK) ON precomMat.nFolioPrecomMateriales = precomdetalle.nFolioPrecomMateriales " + "  WHERE contratos.nIdEstado<3 AND contratos.ConsecutivoPRECOMP IS NULL " + "  GROUP BY SUBSTRING(precomdetalle.EP,32,1) " + /*Precompromiso de documentos en estatus 3 */
        "  UNION " + "  SELECT   " + "  SUM(mImporte)totalPrecom " + "  ,SUBSTRING(det.EP,32,1)ep " + "  FROM dbo.tPreCompromisoEncabezado precom WITH(NOLOCK)  " + "  INNER JOIN dbo.tPreCompromisoDetalle AS det WITH(NOLOCK)  " + "  ON precom.nFolioPreCompromiso = det.nFolioPreCompromiso " + "  INNER JOIN( " + "  	SELECT cIdContratoDefinitivo FROM dbo.mContrato AS cont WITH(NOLOCK) WHERE nIdEstado<=3 " + "  	UNION " + "  	SELECT cIdPedidoDefinitivo FROM dbo.mPedido AS ped WITH(NOLOCK) WHERE nIdEstado<=3 " + "  	UNION " + "  	SELECT cIdContratoDefinitivo FROM dbo.mPlurianualidadContrato WITH(NOLOCK) WHERE nIdEstado<=3 " + "  	UNION " + "  	SELECT  cContratoDefinitivo FROM dbo.mContratoModificado WITH(NOLOCK) WHERE nEstado<=3 AND tipoMod=0 " + "  	UNION " + "  	SELECT cIdContratoDefinitivo+'-AMP-'+CONVERT(varchar,nIdConsecutivoAmpliacion)contrato FROM dbo.mContratoAmpliacion WITH(NOLOCK) WHERE nIdEstado<=3 " + "  	UNION " + "  	SELECT cIdContratoDefinitivo+'-AMP-'+CONVERT(varchar,nIdConsecutivoAmpliacion)contrato FROM dbo.mContratoPluAmpliacion WITH(NOLOCK) WHERE nIdEstado<=3 " + "  )pedCont ON pedCont.cIdContratoDefinitivo=precom.cIdContrato " + "  WHERE  " + "  precom.cDocumentoHaplicado='S' " + "  AND det.cEvento IN('COMP_MAT','PRECOM','APTDPRCP') " + "  GROUP BY SUBSTRING(det.EP,32,1) " + /*Precompromiso en estatus 3 que se va a liberar al disponible */
        "  UNION " + "  SELECT   " + "  SUM(mImporte)totalPrecom " + "  ,SUBSTRING(det.EP,32,1) " + "  FROM dbo.tPreCompromisoEncabezado precom WITH(NOLOCK)  " + "  INNER JOIN dbo.tPreCompromisoDetalle AS det WITH(NOLOCK)  " + "  ON precom.nFolioPreCompromiso = det.nFolioPreCompromiso " + "  INNER JOIN( " + "  	SELECT cIdContratoDefinitivo FROM dbo.mContrato AS cont WITH(NOLOCK) WHERE nIdEstado<=3 " + "  	UNION " + "  	SELECT cIdPedidoDefinitivo FROM dbo.mPedido AS ped WITH(NOLOCK) WHERE nIdEstado<=3 " + "  	UNION " + "  	SELECT cIdContratoDefinitivo FROM dbo.mPlurianualidadContrato WITH(NOLOCK) WHERE nIdEstado<=3 " + "  	UNION " + "  	SELECT  cContratoDefinitivo FROM dbo.mContratoModificado WITH(NOLOCK) WHERE nEstado<=3 AND tipoMod=0 " + "  	UNION " + "  	SELECT cIdContratoDefinitivo+'-AMP-'+CONVERT(varchar,nIdConsecutivoAmpliacion)contrato FROM dbo.mContratoAmpliacion WITH(NOLOCK) WHERE nIdEstado<=3 " + "  	UNION " + "  	SELECT cIdContratoDefinitivo+'-AMP-'+CONVERT(varchar,nIdConsecutivoAmpliacion)contrato FROM dbo.mContratoPluAmpliacion WITH(NOLOCK) WHERE nIdEstado<=3 " + "  )pedCont ON pedCont.cIdContratoDefinitivo=precom.cIdContrato " + "  WHERE  " + "  precom.cDocumentoHaplicado IS NULL " + "  AND det.cEvento IN('PRECOMMAT_DISP') " + "  GROUP BY SUBSTRING(det.EP,32,1) " + /*precompromiso en espera de autorizar */
        "  UNION " + "  SELECT  " + "  SUM(compDet.mImporte)as Precompromiso " + "  ,SUBSTRING(compDet.EP,32,1)capitulo " + "   FROM dbo.tCompromisoEncabezado AS compEnc WITH(NOLOCK) " + "  INNER JOIN dbo.tCompromisoDetalle AS compDet WITH(NOLOCK) " + "  ON compEnc.nFolioCompromiso = compDet.nFolioCompromiso " + "  INNER JOIN dbo.tPreCompromisoEncabezado AS precomEnc WITH(NOLOCK) " + "  ON precomEnc.ConsecutivoCOMP=compEnc.nFolioCompromiso " + "  INNER JOIN dbo.tPreCompromisoDetalle AS precomDet WITH(NOLOCK) " + "  ON precomDet.nFolioPreCompromiso=precomEnc.nFolioPreCompromiso " + "  AND precomDet.EP=compDet.EP " + "  AND precomDet.nDocRenglon=compDet.nDocRenglon " + "  AND precomDet.cMes=compDet.cMes " + "  INNER JOIN(  " + "  	SELECT cIdContratoDefinitivo,nIdEstado,ConsecutivoPRECOMP FROM dbo.mContrato AS cont WITH(NOLOCK)  WHERE nIdEstado=4 " + "  	UNION " + "  	SELECT cIdPedidoDefinitivo,nIdEstado,ConsecutivoPRECOMP FROM dbo.mPedido AS ped WITH(NOLOCK)  WHERE nIdEstado=4 " + "  	UNION " + "  	SELECT cIdContratoDefinitivo,nIdEstado,ConsecutivoPRECOMP FROM dbo.mPlurianualidadContrato WITH(NOLOCK) WHERE nIdEstado=4 " + "  	UNION " + "  	SELECT  cContratoDefinitivo,nEstado,ConsecutivoPRECOMP FROM dbo.mContratoModificado WITH(NOLOCK) WHERE nEstado=4 AND tipoMod=0 " + "  	UNION " + "  	SELECT cIdContratoDefinitivo+'-AMP-'+CONVERT(varchar,nIdConsecutivoAmpliacion)contrato,nIdEstado,ConsecutivoPRECOMP FROM dbo.mContratoAmpliacion WITH(NOLOCK) WHERE nIdEstado=4 " + "  	UNION " + "  	SELECT cIdContratoDefinitivo+'-AMP-'+CONVERT(varchar,nIdConsecutivoAmpliacion)contrato,nIdEstado,ConsecutivoPRECOMP FROM dbo.mContratoPluAmpliacion WITH(NOLOCK) WHERE nIdEstado=4 " + "  )cont ON cont.cIdContratoDefinitivo=precomEnc.cIdContrato AND cont.ConsecutivoPRECOMP=precomEnc.nFolioPreCompromiso " + "  WHERE precomEnc.cDocumentoHaplicado='S' " + "  AND compEnc.cDocumentoHaplicado IS NULL " + "  GROUP BY SUBSTRING(compDet.EP,32,1) " + /*---precom financiero--- */
        "  UNION " + "  SELECT  " + "  	SUM(CASE WHEN precomFinDet.cEvento='CMP003' THEN precomFinDet.mImporteNegativo ELSE precomFinDet.mImporte END) precompromiso " + "  	,SUBSTRING(precomFinDet.EP,32,1)capitulo " + "  	 FROM  dbo.tCompromisoEncabezado AS compEnc WITH(NOLOCK) " + "  INNER JOIN dbo.tCompromisoDetalle AS compDet WITH(NOLOCK) " + "  ON compEnc.nFolioCompromiso = compDet.nFolioCompromiso " + "  INNER JOIN dbo.tPrecomFinancieroEncabezado precomFin WITH(NOLOCK) " + "  ON precomFin.nFolioPrecomFinanciero=compEnc.nFolioCompromiso " + "  INNER JOIN dbo.tPrecomFinancieroDetalle AS precomFinDet WITH(NOLOCK) " + "  ON precomFinDet.nFolioPrecomFinanciero=precomFin.nFolioPrecomFinanciero " + "  AND precomFinDet.cMes=compDet.cMes " + "  AND compDet.EP=precomFinDet.EP " + "  WHERE compEnc.cDocumentoHaplicado IS null AND precomFin.cDocumentoHaplicado='S' " + "  AND SUBSTRING(compDet.EP,32,1) IN(2,3,5) " + "  GROUP BY SUBSTRING(precomFinDet.EP,32,1) " + "  )sub " + "  group BY sub.capitulo " + "  )query " + "  RIGHT JOIN( " + "  	SELECT  " + "  	SUM(ISNULL(mSaldoArrastre,0)) totalPrecomprometido " + "  	,SUBSTRING(cSubCuenta,32,1)capitulo " + "  	FROM dbo.tSaldosVista WITH(NOLOCK) WHERE nCuenta LIKE'82102%' " + "  	AND  SUBSTRING(cSubCuenta,32,1) IN(2,3,5) " + "  	GROUP BY SUBSTRING(cSubCuenta,32,1) " + "  )saldo ON saldo.capitulo=query.capitulo " + "  WHERE saldo.totalPrecomprometido>0 ";
    }

    private String queryCompromiso() {
        return "SELECT  " + "  cFundamentoLegal " + "  ,capitulo " + "  ,SUM(sub.compromisoOrig) compromisoOrig " + "  ,SUM(sub.totalMinimo) totalMinimo " + "  ,SUM(sub.totalMaximo) totalMaximo " + "  ,SUM(sub.ampliadoDelMax) ampliadoDelMax " + "  ,SUM(sub.montoNetoConv) montoNetoConv " + "   FROM  " + "  	(select  " + "  		catFund.cFundamentoLegal " + "  		,SUBSTRING(sol.cIdSubPartida,1,1)capitulo " + "  		,orig.totalCompromiso AS compromisoOrig " + "  		,SUM(isnull(pap.mMontoNetoMinimo,0)) as totalMinimo " + "  		,case when pa.lContratoAbierto=1 then round(SUM(isnull(pap.mMontoNetoLineaMax,ISNULL(pap.mMontoNetoLinea,0))),2) " + "  		else round(SUM(isnull(pap.mMontoNetoMinimo,0)),2) " + "  		end totalMaximo " + "  		,ISNULL(totalAmpliado,0) ampliadoDelMax " + "  		,ISNULL(conv.totalConvenio,0) montoNetoConv " + "  		,cont.cIdContratoDefinitivo " + "  	from mProcedimientoAdjudicacionPartidas as pap with(nolock) " + "  	inner join mProcedimientoAdjudicacion as pa with(nolock) on pap.cIdProcedimiento=pa.cIdProcedimiento " + "  	and pa.cIdRFC=pap.cIdRFC and pa.nIdconsecutivoAdj=pap.nIdconsecutivoAdj" + "  	INNER JOIN dbo.mProcedimiento AS pro WITH(NOLOCK) ON pap.cEjercicio = pro.cEjercicio " + "  	AND pap.cIdConsolidado = pro.cIdConsolidado " + "  	AND pap.cIdProcedimiento = pro.cIdProcedimiento " + "  	INNER JOIN (SELECT cIdConsolidado,nIdLineaConsolidado,cIdSolicitud FROM dbo.mConsolidadoSolicitud WITH(NOLOCK) " + "  		GROUP BY cIdConsolidado,nIdLineaConsolidado,cIdSolicitud " + "  	) AS  cons  ON cons.cIdConsolidado=pro.cIdConsolidado " + "  	AND pap.nIdLineaConsolidado=cons.nIdLineaConsolidado " + "  	INNER JOIN dbo.mSolicitud AS sol WITH(NOLOCK) ON sol.cIdSolicitud=cons.cIdSolicitud " + "  	INNER JOIN dbo.mContrato AS cont WITH(NOLOCK) ON cont.cIdProcedimiento=pro.cIdProcedimiento " + "  	AND cont.cIdRFC=pa.cIdRFC AND cont.nIdconsecutivoAdj=pa.nIdconsecutivoAdj " + "  	INNER JOIN dbo.mCatalogoFundamentoLegal AS catFund WITH(NOLOCK) " + "  	ON pa.nIdFundamentoLeg = catFund.nIdFundamentoLeg " + "  	AND catFund.nIdCategoria=pro.nIdCategoria " + "  	INNER JOIN( " + "  		SELECT  " + "  			SUM(mImporte)totalCompromiso " + "  			,compE.cIdContrato " + "  			,SUBSTRING(compD.EP,32,1)capitulo " + "  			 FROM dbo.tCompromisoEncabezado compE WITH(NOLOCK) " + "  			INNER JOIN dbo.tCompromisoDetalle AS compD WITH(NOLOCK) " + "  			ON compE.nFolioCompromiso = compD.nFolioCompromiso " + "  			INNER JOIN dbo.tPreCompromisoEncabezado AS precom WITH(NOLOCK) " + "  			ON precom.cIdContrato=compE.cIdContrato AND precom.ConsecutivoCOMP=compE.nFolioCompromiso " + "  			WHERE compE.cDocumentoHaplicado='S'  " + "  			GROUP BY compE.cIdContrato " + "  			,SUBSTRING(compD.EP,32,1) " + "  	)orig ON orig.cIdContrato=cont.cIdContratoDefinitivo " + "  	AND orig.capitulo=SUBSTRING(sol.cIdSubPartida,1,1) " + "  	LEFT JOIN( " + "  		select  " + "  			cIdContratoDefinitivo " + "  			,round(sum(det.mImporte),2)totalAmpliado " + "  			,SUBSTRING(EP,32,1)capitulo " + "  			from mContratoAmpliacion as ca with(Nolock) " + "  			inner join tPreCompromisoEncabezado as enc with(Nolock)  " + "  			on ca.cIdContratoDefinitivo+'-AMP-'+convert(varchar,ca.nIdConsecutivoAmpliacion)=enc.cIdContrato " + "  			and enc.cDocumentoHaplicado='S' " + "  			inner join tPreCompromisoDetalle as det with(Nolock) on enc.nFolioPreCompromiso=det.nFolioPreCompromiso " + "  			INNER JOIN dbo.tCompromisoEncabezado AS comp WITH(NOLOCK) ON comp.nFolioCompromiso=enc.ConsecutivoCOMP " + "  			where ca.nIdEstado=4 AND comp.cDocumentoHaplicado='S' " + "  			group by SUBSTRING(EP,32,1),cIdContratoDefinitivo " + "  	)ampliado ON cont.cIdContratoDefinitivo = ampliado.cIdContratoDefinitivo " + "  	AND ampliado.capitulo=SUBSTRING(sol.cIdSubPartida,1,1) " + "  	LEFT JOIN( " + "  		select  " + "  			cm.cIdContratoDefinitivo	 " + "  			,SUM(isnull(det.mImporte,0))totalConvenio " + "  			,SUBSTRING(det.EP,32,1)capitulo " + "  			from mContratoModificado as cm with(nolock) " + "  			inner join tPreCompromisoEncabezado as enc with(nolock) on cm.cContratoDefinitivo=enc.cIdContrato  " + "  			and enc.cDocumentoHaplicado='S' " + "  			inner join tPreCompromisoDetalle as det with(nolock) on det.nFolioPreCompromiso=enc.nFolioPreCompromiso " + "  			INNER JOIN dbo.tCompromisoEncabezado AS comp WITH(NOLOCK) ON comp.nFolioCompromiso=enc.ConsecutivoCOMP " + "  			where cm.nEstado=4 AND comp.cDocumentoHaplicado='S' " + "  			group by SUBSTRING(det.EP,32,1),cm.cIdContratoDefinitivo " + "  	)conv ON conv.cIdContratoDefinitivo=cont.cIdContratoDefinitivo " + "  	AND conv.capitulo=SUBSTRING(sol.cIdSubPartida,1,1) " + "  	WHERE pro.nIdEstado=2 AND cont.nIdEstado=4 " + "  	group by catFund.cFundamentoLegal " + "  	,SUBSTRING(sol.cIdSubPartida,1,1) " + "  	,cont.cIdContratoDefinitivo " + "  	,orig.totalCompromiso " + "  	,ampliado.totalAmpliado " + "  	,conv.totalConvenio " + "  	,pa.lContratoAbierto " + "  	UNION " + /*Plurianuales*/
        "  	SELECT  " + "  	catleg.cFundamentoLegal " + "  	,SUBSTRING(part.cIdSubPartida,1,1)capitulo " + "  	,orig.totalCompromiso montonetoOrig " + "  	,SUM(part.mMontoNetoMinimo)montoNetoMinimo " + "  	,SUM(part.mMontoNetoLineaMaximo)montonetoMaximo " + "  	,ISNULL(totalAmpliado,0) ampliadoDelMax " + "  	,ISNULL(conv.totalConvenio,0) montoNetoConv " + "  	,pluCont.cIdContratoDefinitivo " + "  	 FROM dbo.mPlurianualidadContrato AS pluCont WITH(NOLOCK) " + "  	INNER JOIN dbo.mPartidasContratoPlurianual AS part WITH(NOLOCK) " + "  	ON pluCont.cIdContratoDefinitivo = part.cIdContratoDefinitivo " + "  	INNER JOIN dbo.mCatalogoFundamentoLegal AS catleg WITH(NOLOCK) " + "  	ON catleg.nIdCategoria=pluCont.nIdCategoria " + "  	AND catleg.nIdFundamentoLeg=pluCont.nIdFundamentoLeg " + "  	INNER JOIN( " + "  		SELECT  " + "  			SUM(mImporte)totalCompromiso " + "  			,compE.cIdContrato " + "  			,SUBSTRING(compD.EP,32,1)capitulo " + "  			 FROM dbo.tCompromisoEncabezado compE WITH(NOLOCK) " + "  			INNER JOIN dbo.tCompromisoDetalle AS compD WITH(NOLOCK) " + "  			ON compE.nFolioCompromiso = compD.nFolioCompromiso " + "  			INNER JOIN dbo.tPreCompromisoEncabezado AS precom WITH(NOLOCK) " + "  			ON precom.cIdContrato=compE.cIdContrato AND precom.ConsecutivoCOMP=compE.nFolioCompromiso " + "  			WHERE compE.cDocumentoHaplicado='S'  " + "  			GROUP BY compE.cIdContrato " + "  			,SUBSTRING(compD.EP,32,1) " + "  	)orig ON orig.cIdContrato=pluCont.cIdContratoDefinitivo " + "  	AND orig.capitulo=SUBSTRING(part.cIdSubPartida,1,1) " + "  	LEFT JOIN( " + "  		select  " + "  			cIdContratoDefinitivo " + "  			,round(sum(det.mImporte),2)totalAmpliado " + "  			,SUBSTRING(EP,32,1)capitulo " + "  			from mContratoAmpliacion as ca with(Nolock) " + "  			inner join tPreCompromisoEncabezado as enc with(Nolock)  " + "  			on ca.cIdContratoDefinitivo+'-AMP-'+convert(varchar,ca.nIdConsecutivoAmpliacion)=enc.cIdContrato " + "  			and enc.cDocumentoHaplicado='S' " + "  			inner join tPreCompromisoDetalle as det with(Nolock) on enc.nFolioPreCompromiso=det.nFolioPreCompromiso " + "  			INNER JOIN dbo.tCompromisoEncabezado AS comp WITH(NOLOCK) ON comp.nFolioCompromiso=enc.ConsecutivoCOMP " + "  			where ca.nIdEstado=4 AND comp.cDocumentoHaplicado='S' " + "  			group by SUBSTRING(EP,32,1),cIdContratoDefinitivo " + "  	)ampliado ON pluCont.cIdContratoDefinitivo = ampliado.cIdContratoDefinitivo " + "  	AND ampliado.capitulo=SUBSTRING(part.cIdSubPartida,1,1) " + "  	LEFT JOIN( " + "  		select  " + "  			cm.cIdContratoDefinitivo	 " + "  			,SUM(isnull(det.mImporte,0))totalConvenio " + "  			,SUBSTRING(det.EP,32,1)capitulo " + "  			from mContratoModificado as cm with(nolock) " + "  			inner join tPreCompromisoEncabezado as enc with(nolock) on cm.cContratoDefinitivo=enc.cIdContrato  " + "  			and enc.cDocumentoHaplicado='S' " + "  			inner join tPreCompromisoDetalle as det with(nolock) on det.nFolioPreCompromiso=enc.nFolioPreCompromiso " + "  			INNER JOIN dbo.tCompromisoEncabezado AS comp WITH(NOLOCK) ON comp.nFolioCompromiso=enc.ConsecutivoCOMP " + "  			where cm.nEstado=4 AND comp.cDocumentoHaplicado='S' " + "  			group by SUBSTRING(det.EP,32,1),cm.cIdContratoDefinitivo " + "  	)conv ON conv.cIdContratoDefinitivo=pluCont.cIdContratoDefinitivo " + "  	AND conv.capitulo=SUBSTRING(part.cIdSubPartida,1,1) " + "  	WHERE pluCont.nIdEstado=4  " + "  	GROUP BY catleg.cFundamentoLegal " + "  	,SUBSTRING(part.cIdSubPartida,1,1) " + "  	,pluCont.cIdContratoDefinitivo " + "  	,orig.totalCompromiso " + "  	,ampliado.totalAmpliado " + "  	,conv.totalConvenio " + "  )sub " + "  GROUP BY " + "  sub.cFundamentoLegal " + "  ,sub.capitulo ";
    }

    private String queryDisponible() {
        return "SELECT " + "  SUBSTRING(cSubCuenta,32,1) capitulo " + "  ,SUM(mSaldoArrastre) totalApartadoSaldos " + "  FROM dbo.tSaldosVista WITH(NOLOCK) WHERE nCuenta LIKE'82106%' " + "  AND SUBSTRING(cSubCuenta,32,1) IN(2,3,5) " + "  GROUP BY SUBSTRING(cSubCuenta,32,1) " + "  ORDER BY SUBSTRING(cSubCuenta,32,1) ";
    }

    private void writeTotales(Workbook wb, int renglonInicio, int columnaInicio, BigDecimal[] totales, CellStyle estiloTabla) throws Exception {
        Sheet sheet0 = null;
        Row rw = null;
        int j = 0;
        int cnt = 0;
        sheet0 = wb.getSheetAt(0);
        CellStyle estiloTbl = estiloTabla;
        estiloTbl.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        estiloTbl.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        if (totales != null) {
            rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            while (j < totales.length) {
                com.syc.gestion.util.Util.createExcelCellReportePresupuestal((columnaInicio + j), rw, totales[j], estiloTbl);
                System.out.println(totales[j]);
                j++;
            }
        }
    }

    private void writeResumen(Workbook wb, int renglonInicio, int columnaInicio, BigDecimal[] totales, CellStyle estiloTabla) throws Exception {
        Sheet sheet0 = null;
        Row rw = null;
        int j = 0;
        sheet0 = wb.getSheetAt(0);
        if (totales != null) {
            while (j < totales.length) {
                rw = (sheet0.getRow(renglonInicio + j) == null ? sheet0.createRow(renglonInicio + j) : sheet0.getRow(renglonInicio + j));
                com.syc.gestion.util.Util.createExcelCellReportePresupuestal(columnaInicio, rw, totales[j], estiloTabla);
                j++;
            }
        }
    }
}
