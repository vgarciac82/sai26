package com.syc.adquisiciones.manager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import com.axtel.contratos.Requisition;
import com.syc.adquisiciones.core.DatosPedidoContrato;
import com.syc.adquisiciones.core.ObjectCelda;
import com.syc.adquisiciones.core.ProcedimientoSAC;
import com.syc.adquisiciones.util.Util;
import com.syc.cfdi.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportesGRMManager {

    private static Logger log = LoggerFactory.getLogger(ReportesGRMManager.class);

    public void writeSheet1Formato1120(Connection conn, XSSFSheet firstSheet, XSSFCellStyle estiloTabla, String cEjercicio) throws Exception {
        String query = "Select *from v_mReporteFormato1120SIIWEB ";
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        String cadena = "FORMATO 1120 \"ESTAD\u00edSTICAS POR ACCCI\u00f3N DE COMPRA\" " + cEjercicio;
        try {
            ps = conn.prepareStatement(query);
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            XSSFRow rw = (firstSheet.getRow(2) == null ? firstSheet.createRow(2) : firstSheet.getRow(2));
            XSSFCell celdarsad = (rw.getCell(0) == null ? rw.createCell(0) : rw.getCell(0));
            celdarsad.setCellValue(cadena);
            //Escribe el detalle
            int renglonInicio = 6;
            int rows = 0;
            int j = 0;
            int cnt = 0;
            int cantRowsFinal = 17;
            double sumaMonto = 0.0;
            while (rst.next()) {
                rows = renglonInicio + cnt;
                if (j > 1) {
                    firstSheet.shiftRows(rows, rows + cantRowsFinal, 1);
                    rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                } else {
                    rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                }
                j++;
                for (int i = 0; i < totalcolumnas; i++) {
                    if (i == 4) {
                        sumaMonto = sumaMonto + rst.getDouble(i + 1);
                    }
                    com.syc.gestion.util.Util.createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
                cnt++;
            }
            //Footer
            rows = renglonInicio + cnt + 1;
            // sheet0.createRow(rows);
            rw = firstSheet.getRow(rows);
            celdarsad = rw.getCell(4);
            celdarsad.setCellValue(sumaMonto);
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
        }
    }

    public void writeSheet2Formato1120(Connection conn, XSSFSheet secondSheet, XSSFCellStyle estiloTabla) throws Exception {
        String query = "Select *from v_mReporteFormato1120SIIWEB ";
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        try {
            ps = conn.prepareStatement(query);
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            //Escribe el detalle
            int renglonInicio = 0;
            int rows = 0;
            int cnt = 0;
            XSSFRow rw = null;
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (secondSheet.getRow(rows) == null ? secondSheet.createRow(rows) : secondSheet.getRow(rows));
                for (int i = 0; i < totalcolumnas; i++) {
                    com.syc.gestion.util.Util.createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
                cnt++;
            }
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
        }
    }

    public void writeSheet1COCODI(Connection conn, XSSFSheet firstSheet, String cEjercicio, DatosPedidoContrato datos, float nCifras) throws Exception {
        String query = null;
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        StringBuilder cadena = new StringBuilder();
        //String cadena="Del 01 de enero al 31 de marzo de "+cEjercicio;
        try {
            String[] arreglofechaIni = datos.getcFechaInicio().split("/");
            String[] arreglofechaFin = datos.getcFechaFin().split("/");
            cadena.append("Del" + (arreglofechaIni[0].length() == 1 ? "0" + arreglofechaIni[0] : arreglofechaIni[0]) + " de " + Util.getNameMonth(Integer.parseInt(arreglofechaIni[1])));
            if (!arreglofechaIni[2].equalsIgnoreCase(cEjercicio)) {
                cadena.append(" de " + arreglofechaIni[2]);
            }
            cadena.append(" al " + (arreglofechaFin[0].length() == 1 ? "0" + arreglofechaFin[0] : arreglofechaFin[0]));
            cadena.append(" de " + Util.getNameMonth(Integer.parseInt(arreglofechaFin[1])) + " de " + cEjercicio);
            query = "select *from fn_mReporteCOCODI(?,?,?) ";
            ps = conn.prepareStatement(query);
            ps.setString(1, datos.getcFechaInicio());
            ps.setString(2, datos.getcFechaFin());
            ps.setFloat(3, nCifras);
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            XSSFRow rw = firstSheet.getRow(5);
            XSSFCell celdarsad = rw.getCell(0);
            celdarsad.setCellValue(cadena.toString());
            //Escribe el detalle
            int renglonInicio = 10;
            int rows = 0;
            int cnt = 0;
            double[] totales = new double[7];
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                for (int i = 0; i < totalcolumnas; i++) {
                    if (i > 0) {
                        totales[i] = totales[i] + rst.getDouble(i + 1);
                    }
                    createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                }
                cnt++;
            }
            //Write row total
            rows = renglonInicio + cnt;
            rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
            for (int k = 0; k < totales.length; k++) {
                Cell cell = rw.getCell(k);
                if (k == 0) {
                    cell.setCellValue("TOTAL");
                } else {
                    cell.setCellValue(totales[k]);
                }
            }
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
        }
    }

    public void writeSheet3COCODI(Connection conn, XSSFSheet thirdSheet, DatosPedidoContrato datos, XSSFCellStyle estiloCell, XSSFCellStyle estiloCellFecha) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append(" 	select 	sub.cIdProcedimiento	,sub.cIdRFC,sub.cRazonSocial	,sum(netoOriginalContrato)netoOriginalContrato  ");
        query.append(" 	,sum(montoConvenio)montoConvenio,sum(totalNetoContrato)totalNeto,sub.cIdContratoDefinitivo contratoSAI	  ");
        query.append(" 	,sub.cNoContratoCNET,sub.numProcedCNET,sub.cFundamentoLegal	,sub.fFormalizacion,fFallo  ");
        query.append(" 	,sub.fechaInicio,sub.fechaFin,cConceptoContrato    ");
        query.append(" from(   ");
        query.append(" 	select    ");
        query.append(" 		procedAdjPart.cIdProcedimiento,procedAdjPart.cIdRFC,procedAdjPart.cIdConsolidado   ");
        query.append(" 		,procedAdjPart.nIdLineaConsolidado   ");
        query.append(" 		,procedAdjPart.mMontoNetoLinea   ");
        query.append(" 		,isnull(conv.mMontoNetoLinea,0)montoConvenio   ");
        query.append(" 		,case when proced.isPlurianual=0 then procedAdjPart.mMontoNetoLineaMax+isnull(conv.mMontoNetoLinea,0) else procedAdjPart.mMontoNetoPluri+isnull(conv.mMontoNetoLinea,0) end totalNetoContrato  ");
        query.append(" 		,case when proced.isPlurianual=0 then procedAdjPart.mMontoNetoLineaMax else procedAdjPart.mMontoNetoPluri  end netoOriginalContrato    ");
        query.append(" 		,contrato.cIdContratoDefinitivo   ");
        query.append(" 		,procedAdj.nIdFundamentoLeg   ");
        query.append(" 		,convert(date,contrato.fCreacion)fCreacion   ");
        query.append(" 		,convert(date,contrato.fFallo)fFallo   ");
        query.append(" 		,convert(date,contrato.fFormalizacion)fFormalizacion   ");
        query.append(" 		,contrato.cNoContratoCNET   ");
        query.append(" 		,catFundLeg.cFundamentoLegal   ");
        query.append(" 		,contrato.cConceptoContrato   ");
        query.append(" 		,prov.cRazonSocial  ");
        query.append(" 		,proced.cOficio numProcedCNET  ");
        query.append(" 		,convert(date,contrato.fechaInicio)fechaInicio  ");
        query.append(" 		,convert(date,contrato.fechaFin)fechaFin  ");
        query.append(" 	from mProcedimientoAdjudicacionPartidas as procedAdjPart with(Nolock)   ");
        query.append(" 	inner join mProcedimientoAdjudicacion as procedAdj with(Nolock) on procedAdj.cIdProcedimiento=procedAdjPart.cIdProcedimiento   ");
        query.append(" 	and procedAdj.cIdRFC=procedAdjPart.cIdRFC and procedAdj.nIdconsecutivoAdj=procedAdjPart.nIdconsecutivoAdj   ");
        query.append(" 	inner join mProcedimiento as proced with(Nolock) on proced.cIdProcedimiento=procedAdjPart.cIdProcedimiento   ");
        query.append(" 	inner join mCatalogoFundamentoLegal as catFundLeg with(Nolock)   ");
        query.append(" 	on catFundLeg.nIdCategoria=proced.nIdCategoria and catFundLeg.nIdFundamentoLeg=procedAdj.nIdFundamentoLeg   ");
        query.append(" 	inner join(    ");
        query.append(" 		select cIdProcedimiento,cIdContratoDefinitivo,cIdRFC,fCreacion,fFormalizacion,fFallo,cNoContratoCNET,nIdconsecutivoAdj,cConceptoContrato  ");
        query.append(" 		,fInicio  fechaInicio  ");
        query.append(" 		,case when cIdTipoProcedimiento='PC' or cIdTipoProcedimiento='PT'  then fEntrega else fFin end fechaFin   ");
        query.append(" 		from mContrato as contrato with(Nolock) where contrato.nIdEstado=4 and cConceptoContrato not like'%ampliaci%' and cConceptoContrato not like'%convenio%'   ");
        query.append(" 		union   ");
        query.append(" 		select cIdProcedimiento,cIdPedidoDefinitivo,cIdRFC,fCreacion,fFormalizacion,fFallo,cNoContratoCNET,nIdconsecutivoAdj,cConceptoPedido  ");
        query.append(" 		,case when cIdTipoProcedimiento='PC' then fEntrega else fInicio end fechaInicio  ");
        query.append(" 		,case when cIdTipoProcedimiento='PC' then fEntrega else fFin end fechaFin   ");
        query.append(" 		from mPedido as pedido with(Nolock) where pedido.nIdEstado=4   ");
        query.append(" 	)contrato on contrato.cIdProcedimiento=proced.cIdProcedimiento   ");
        query.append(" 	and contrato.cIdRFC=procedAdjPart.cIdRFC and contrato.nIdconsecutivoAdj=procedAdjPart.nIdconsecutivoAdj  ");
        query.append(" 	inner join mCatalogoProveedor prov with(Nolock)  ");
        query.append(" 	on prov.cIdRFC=contrato.cIdRFC  ");
        query.append(" 	inner join (   ");
        query.append(" 		select    ");
        query.append(" 		consol.cIdConsolidado   ");
        query.append(" 		,consolSol.nIdLineaConsolidado,consolSol.cIdSolicitud   ");
        query.append(" 		from mConsolidado as consol with(Nolock)   ");
        query.append(" 		inner join mConsolidadoSolicitud as consolSol with(Nolock)   ");
        query.append(" 		on consol.cIdConsolidado=consolSol.cIdConsolidado   ");
        query.append(" 		inner join mConsolidadoLineas as consolLine with(Nolock)   ");
        query.append(" 		on consolLine.cIdConsolidado=consol.cIdConsolidado   ");
        query.append(" 		where consol.nIdEstado=2   ");
        query.append(" 		group by consol.cIdConsolidado   ");
        query.append(" 		,consolSol.nIdLineaConsolidado,consolSol.cIdSolicitud   ");
        query.append(" 	)requi on requi.cIdConsolidado=proced.cIdConsolidado   ");
        query.append(" 	and requi.nIdLineaConsolidado=procedAdjPart.nIdLineaConsolidado   ");
        query.append(" 	inner join mSolicitud as sol  with(Nolock)   ");
        query.append(" 	on sol.cIdSolicitud=requi.cIdSolicitud     ");
        query.append(" 	left join (   ");
        query.append(" 		select    ");
        query.append(" 			contMod.cIdContratoDefinitivo cIdContratoDefinitivo   ");
        query.append(" 			,contModPart.nIdLineaConsolidado,sum(contModPart.mMontoNeto)mMontoNetoLinea   ");
        query.append(" 		from mContratoModificado as contMod with(nolock) ");
        query.append(" 		inner join  pContratoDiversoConvenio convDiv with(Nolock)  ");
        query.append(" 		on convDiv.cIdContrato=contMod.cIdContratoDefinitivo ");
        query.append(" 		and convDiv.nConsecutivoModificacion=contMod.nConsecutivoModificacion ");
        query.append(" 		and contMod.cContratoDefinitivo=convDiv.cIdModificacion ");
        query.append(" 		inner join mContratoModificadoPartida as contModPart with(nolock)   ");
        query.append(" 		on contMod.cIdContratoDefinitivo=contModPart.cIdContratoDefinitivo   ");
        query.append(" 		and contMod.nConsecutivoModificacion=contModPart.nConsecutivoModificacion   ");
        query.append(" 		where contMod.nEstado=4 and contMod.isConvEjercicioAnt=0 and tipoMod=0  ");
        query.append(" 		and convert(date,convDiv.fFirmaContrato)>=convert(date,?) and convert(date,convDiv.fFirmaContrato)<=convert(date,?) ");
        query.append(" 		group by contMod.cIdContratoDefinitivo   ");
        query.append(" 		,contModPart.nIdLineaConsolidado   ");
        query.append(" 	)conv on conv.cIdContratoDefinitivo=contrato.cIdContratoDefinitivo   ");
        query.append(" 	and conv.nIdLineaConsolidado=procedAdjPart.nIdLineaConsolidado   ");
        query.append(" 	where proced.nIdEstado=2 and sol.nIdEstado=3   ");
        query.append(" 	and convert(date,contrato.fFormalizacion)>=convert(date,?) and convert(date,contrato.fFormalizacion)<=convert(date,?)  ");
        query.append(" 	group by procedAdjPart.cIdProcedimiento,procedAdjPart.cIdRFC,procedAdjPart.cIdConsolidado   ");
        query.append(" 	,procedAdjPart.nIdLineaConsolidado,procedAdjPart.mMontoNetoLinea   ");
        query.append(" 	,contrato.cIdContratoDefinitivo,procedAdj.nIdFundamentoLeg   ");
        query.append(" 	,contrato.fCreacion			,contrato.fFallo			,contrato.fFormalizacion   ");
        query.append(" 	,conv.mMontoNetoLinea			,procedAdjPart.mMontoNetoLineaMax			,proced.isPlurianual   ");
        query.append(" 	,procedAdjPart.mMontoNetoPluri			,contrato.cNoContratoCNET			,catFundLeg.cFundamentoLegal,contrato.cConceptoContrato   ");
        query.append(" 	,prov.cRazonSocial  ");
        query.append(" 	,proced.cOficio   ");
        query.append(" 	,contrato.fechaInicio  ");
        query.append(" 	,contrato.fechaFin  ");
        query.append(" 	)sub   ");
        query.append(" 	group by sub.cIdProcedimiento	,sub.cIdRFC	,sub.cIdContratoDefinitivo    ");
        query.append(" ,sub.cNoContratoCNET	,sub.cFundamentoLegal	,sub.fFormalizacion,fFallo,cConceptoContrato  ");
        query.append(" ,sub.cRazonSocial,sub.numProcedCNET,sub.fechaInicio,sub.fechaFin  ");
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, datos.getcFechaInicio());
            ps.setString(2, datos.getcFechaFin());
            ps.setString(3, datos.getcFechaInicio());
            ps.setString(4, datos.getcFechaFin());
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            XSSFRow rw = null;
            //Escribe el detalle
            int renglonInicio = 1;
            int rows = 0;
            int cnt = 0;
            XSSFTable table = thirdSheet.getTables().get(0);
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (thirdSheet.getRow(rows) == null ? thirdSheet.createRow(rows) : thirdSheet.getRow(rows));
                for (int i = 0; i < totalcolumnas; i++) {
                    if (i > 2 && i <= 5) {
                        createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloCell);
                    }
                    if (i > 9 && i <= 13) {
                        createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloCellFecha);
                    } else {
                        createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                    }
                }
                cnt++;
            }
            if (cnt > 0)
                updateSizeTable((cnt + renglonInicio - 1), table);
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (query != null) {
                query.delete(0, query.length());
            }
            rsMetadata = null;
            rst = null;
            ps = null;
            query = null;
        }
    }

    public void writeSheet4COCODI(Connection conn, XSSFSheet fourthSheet, DatosPedidoContrato datos, XSSFCellStyle estiloCell) throws Exception {
        String query = "select \r\n" + "contMod.cIdContratoDefinitivo\r\n" + ",contMod.cNoConvenio\r\n" + ",contMod.cObjetoConvenio\r\n" + ",contMod.mTotalModificacion\r\n" + "from mContratoModificado as contMod with(Nolock)\r\n" + "where contMod.nEstado=4 and isConvEjercicioAnt=0 ";
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        try {
            ps = conn.prepareStatement(query);
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            XSSFRow rw = null;
            XSSFTable table = fourthSheet.getTables().get(0);
            //Escribe el detalle
            int renglonInicio = 1;
            int rows = 0;
            int cnt = 0;
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (fourthSheet.getRow(rows) == null ? fourthSheet.createRow(rows) : fourthSheet.getRow(rows));
                for (int i = 0; i < totalcolumnas; i++) {
                    if (i == 3) {
                        createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloCell);
                    } else
                        createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                }
                cnt++;
            }
            if (cnt > 0)
                updateSizeTable((cnt + renglonInicio - 1), table);
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
        }
    }

    public void writeSheet5COCODI(Connection conn, XSSFSheet sixthSheet, DatosPedidoContrato datos, XSSFCellStyle estiloCell, XSSFCellStyle estiloCellFecha) throws Exception {
        StringBuilder query = null;
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        try {
            query = new StringBuilder();
            query.append(" 	select   ");
            query.append(" 	enc.nFolioPagoDirecto ,enc.caNoContrarrecibo ,enc.cConcepto  ");
            query.append(" 	,ue.D_DESCRIPCION areaRequirente ,enc.cIdRFC rfc,prov.cRazonSocial RazonSocial ");
            query.append(" 	,paas.cIdSubPartida ,sum(paas.mMontoNeto)montoNeto ,enc.cUnidadResponsable  ");
            query.append(" 	,enc.fAplicacion ");
            query.append(" from tPagoDirectoEncabezado as enc with(Nolock)  ");
            query.append(" inner join tPagoDirectoPAAS as paas with(Nolock)  ");
            query.append(" on paas.nFolioPagoDirecto=enc.nFolioPagoDirecto  ");
            query.append(" inner join tCatUnidadEjecutora as ue with(Nolock)  ");
            query.append(" on ue.cUnidadEjecutora=enc.cUnidadResponsable  ");
            query.append(" inner join mCatalogoProveedor as prov with(Nolock)  ");
            query.append(" on replace(prov.cIdRFC,'-','')=enc.cIdRFC  ");
            query.append(" where enc.cDocumentoHaplicado='S' ");
            query.append(" and SUBSTRING(paas.cIdSubPartida,1,1) in(2,3,5) ");
            query.append(" and SUBSTRING(paas.cIdSubPartida,1,3) not  in(select cPartidaExcepcion from mCatalogoPartidasExcepcionPagoDirecto7030 with(Nolock)) ");
            query.append(" and convert(date,enc.fAplicacion)>=convert(date,?) ");
            query.append(" and convert(date,enc.fAplicacion)<=convert(date,?) ");
            query.append(" group by enc.nFolioPagoDirecto,enc.caNoContrarrecibo  ");
            query.append(" ,enc.cConcepto,ue.D_DESCRIPCION ,enc.cIdRFC  ");
            query.append(" ,paas.cIdSubPartida	,prov.cRazonSocial ,enc.cUnidadResponsable ");
            query.append(" ,enc.fAplicacion ");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, datos.getcFechaInicio());
            ps.setString(2, datos.getcFechaFin());
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            XSSFRow rw = null;
            XSSFTable table = sixthSheet.getTables().get(0);
            //Escribe el detalle
            int renglonInicio = 1;
            int rows = 0;
            int cnt = 0;
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (sixthSheet.getRow(rows) == null ? sixthSheet.createRow(rows) : sixthSheet.getRow(rows));
                for (int i = 0; i < totalcolumnas; i++) {
                    if (i == 7) {
                        createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloCell);
                    } else if (i == 9) {
                        createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloCellFecha);
                    } else
                        createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                }
                cnt++;
            }
            if (cnt > 0)
                updateSizeTable((cnt + renglonInicio - 1), table);
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (query != null) {
                query.delete(0, query.length());
            }
            rsMetadata = null;
            rst = null;
            ps = null;
            query = null;
        }
    }

    public void writeSheet1PresionGasto(Connection conn, XSSFSheet firstSheet, DatosPedidoContrato datos, String cEjercicio) throws Exception {
        String query = "Select *from v_mReportePresionGasto";
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        XSSFRow rw = null;
        String cadena = "PRESIONES DE GASTO " + cEjercicio + " CON DATOS AL MES DE " + datos.getcNameMes().toUpperCase() + " DE " + cEjercicio;
        try {
            ps = conn.prepareStatement(query);
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            rw = firstSheet.getRow(4);
            XSSFCell celdarsad = rw.getCell(3);
            celdarsad.setCellValue(cadena);
            //Escribe el detalle
            int renglonInicio = 9;
            int rows = 0;
            int cnt = 0;
            double[] montos = { 0, 0, 0, 0 };
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                for (int i = 0; i < totalcolumnas - 1; i++) {
                    if (i >= 5 && i <= 8) {
                        montos[i - 5] = montos[i - 5] + rst.getDouble(i + 1);
                    }
                    createExcelCellRep(i + 1, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                }
                cnt++;
            }
            //Write row total
            rows = renglonInicio + cnt;
            rw = (firstSheet.getRow(renglonInicio - 1) == null ? firstSheet.createRow(renglonInicio - 1) : firstSheet.getRow(renglonInicio - 1));
            String formula = "";
            String[] letras = { "G", "H", "I", "J" };
            for (int k = 0; k < montos.length; k++) {
                formula = "SUM(" + letras[k] + (renglonInicio + 1) + ":" + letras[k] + rows + ")";
                Cell cell = rw.getCell(k + 6);
                cell.setCellFormula(formula);
                cell.setCellValue(montos[k]);
            }
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
        }
    }

    public void writeSheet1Formato7030(Connection conn, XSSFSheet firstSheet, DatosPedidoContrato datos, String cEjercicio, String query) throws Exception {
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        XSSFRow rw = null;
        String cadena = "Periodo: del 01 de enero al 31 de marzo 2020";
        try {
            String[] arreglofechaIni = datos.getcFechaInicio().split("/");
            String[] arreglofechaFin = datos.getcFechaFin().split("/");
            cadena = "Periodo: del " + arreglofechaIni[0] + " de " + Util.getNameMonth(Integer.parseInt(arreglofechaIni[1])) + " al " + arreglofechaFin[0] + " de " + Util.getNameMonth(Integer.parseInt(arreglofechaFin[1])) + " " + cEjercicio;
            ps = conn.prepareStatement(query);
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            rw = firstSheet.getRow(4);
            XSSFCell celdarsad = rw.getCell(3);
            celdarsad.setCellValue(cadena);
            //Escribe el detalle
            int renglonInicio = 16;
            int rows = 0;
            int cnt = 0;
            double[] montos2 = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            double[] montos3 = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            double[] montos5 = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            int cant2 = 0;
            int cant3 = 0;
            int cant5 = 0;
            while (rst.next()) {
                rows = renglonInicio + cnt;
                if (rst.getInt(1) == 2) {
                    cant2++;
                } else if (rst.getInt(1) == 3) {
                    cant3++;
                    rows = renglonInicio + cnt + 1;
                } else if (rst.getInt(1) == 5) {
                    cant5++;
                    rows = renglonInicio + cnt + 2;
                }
                rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                for (int i = 2; i < totalcolumnas; i++) {
                    if (i >= 4 && i <= 12 && cant2 > 0 && cant3 == 0 && cant5 == 0) {
                        montos2[i - 4] = montos2[i - 4] + rst.getDouble(i + 1);
                    } else if (i >= 4 && i <= 12 && cant2 > 0 && cant3 > 0 && cant5 == 0) {
                        montos3[i - 4] = montos3[i - 4] + rst.getDouble(i + 1);
                    } else if (i >= 4 && i <= 12 && cant2 > 0 && cant3 > 0 && cant5 > 0) {
                        montos5[i - 4] = montos5[i - 4] + rst.getDouble(i + 1);
                    }
                    createExcelCellRep(i - 2, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                }
                cnt++;
            }
            //Write row total
            rows = renglonInicio - 1;
            rw = (firstSheet.getRow(renglonInicio - 1) == null ? firstSheet.createRow(renglonInicio - 1) : firstSheet.getRow(renglonInicio - 1));
            String formula = "";
            String[] letras = { "C", "D", "E", "F", "G", "H", "I", "J", "K" };
            for (int k = 0; k < montos2.length; k++) {
                //Totales capitilo 2
                formula = "SUM(" + letras[k] + (renglonInicio + 1) + ":" + letras[k] + (renglonInicio + cant2) + ")";
                Cell cell = rw.getCell(k + 2);
                cell.setCellFormula(formula);
                cell.setCellValue(montos2[k]);
            }
            rw = (firstSheet.getRow(renglonInicio + cant2) == null ? firstSheet.createRow(renglonInicio + cant2) : firstSheet.getRow(renglonInicio + cant2));
            for (int k = 0; k < montos3.length; k++) {
                //Totales capitilo 3
                formula = "SUM(" + letras[k] + (renglonInicio + cant2 + 2) + ":" + letras[k] + (renglonInicio + cant2 + cant3 + 1) + ")";
                Cell cell = rw.getCell(k + 2);
                cell.setCellFormula(formula);
                cell.setCellValue(montos3[k]);
            }
            rw = (firstSheet.getRow(renglonInicio + cant2 + cant3 + 1) == null ? firstSheet.createRow(renglonInicio + cant2 + cant3 + 1) : firstSheet.getRow(renglonInicio + cant2 + cant3 + 1));
            for (int k = 0; k < montos5.length; k++) {
                //Totales capitilo 5
                formula = "SUM(" + letras[k] + (renglonInicio + cant2 + cant3 + 3) + ":" + letras[k] + (renglonInicio + cnt + 2) + ")";
                Cell cell = rw.getCell(k + 2);
                cell.setCellFormula(formula);
                cell.setCellValue(montos5[k]);
            }
            rw = (firstSheet.getRow(renglonInicio + cant2 + cant3 + cant5 + 2) == null ? firstSheet.createRow(renglonInicio + cant2 + cant3 + cant5 + 2) : firstSheet.getRow(renglonInicio + cant2 + cant3 + cant5 + 2));
            for (int k = 0; k < montos5.length; k++) {
                //Totales Total
                formula = "SUM(" + letras[k] + (renglonInicio) + "+" + letras[k] + (renglonInicio + cant2 + 1) + "+" + letras[k] + (renglonInicio + cant2 + cant3 + 2) + ")";
                Cell cell = rw.getCell(k + 2);
                cell.setCellFormula(formula);
                cell.setCellValue(montos5[k] + montos3[k] + montos2[k]);
            }
            //Periodo
            rw = (firstSheet.getRow(6) == null ? firstSheet.createRow(6) : firstSheet.getRow(6));
            Cell cell = rw.getCell(8);
            cell.setCellValue(cadena);
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
        }
    }

    public void writeSheetWorboock(Connection conn, XSSFSheet sheet, DatosPedidoContrato datos, StringBuilder query, int renglonInicio) throws Exception {
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        XSSFRow rw = null;
        XSSFTable table = null;
        try {
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            int rows = 0;
            int cnt = 0;
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (sheet.getRow(rows) == null ? sheet.createRow(rows) : sheet.getRow(rows));
                for (int i = 0; i < totalcolumnas; i++) {
                    createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                }
                cnt++;
            }
            if (cnt > 0) {
                table = sheet.getTables().get(0);
                updateSizeTable((cnt + renglonInicio - 1), table);
            }
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (query != null && query.length() > 0)
                query.delete(0, query.length());
            rsMetadata = null;
            rst = null;
            ps = null;
            query = null;
        }
    }

    public void writeSheet1PagoDirecto(Connection conn, XSSFSheet firstSheet, XSSFCellStyle estiloTabla, String cadena, DatosPedidoContrato datos) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        try {
            query = "select *from (select  " + "enc.nFolioPagoDirecto " + ",enc.caNoContrarrecibo " + ",enc.cConcepto " + ",ue.D_DESCRIPCION areaRequirente " + ",enc.cIdRFC rfc" + ",prov.cRazonSocial RazonSocial" + ",convert(int,paas.cIdSubPartida) partida " + ",sum(paas.mMontoNeto)montoNeto " + ",convert(varchar,enc.fAplicacion,103)fAplicacion " + ",enc.cUnidadResponsable cIdUnidadEjecutora " + "from tPagoDirectoEncabezado as enc with(Nolock) " + "inner join tPagoDirectoPAAS as paas with(Nolock) " + "on paas.nFolioPagoDirecto=enc.nFolioPagoDirecto " + "inner join tCatUnidadEjecutora as ue with(Nolock) " + "on ue.cUnidadEjecutora=enc.cUnidadResponsable " + "inner join mCatalogoProveedor as prov with(Nolock) " + "on replace(prov.cIdRFC,'-','')=enc.cIdRFC " + "where enc.cDocumentoHaplicado='S' " + "group by enc.nFolioPagoDirecto " + ",enc.caNoContrarrecibo " + ",enc.cConcepto " + ",ue.D_DESCRIPCION " + ",enc.cIdRFC " + ",paas.cIdSubPartida  " + ",prov.cRazonSocial " + ",enc.cUnidadResponsable,enc.fAplicacion ) sub" + " where 1=1 " + datos.getcWhere() + "order by sub.nFolioPagoDirecto";
            ps = conn.prepareStatement(query);
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            XSSFRow rw = (firstSheet.getRow(2) == null ? firstSheet.createRow(2) : firstSheet.getRow(2));
            XSSFCell celdarsad = (rw.getCell(0) == null ? rw.createCell(0) : rw.getCell(0));
            celdarsad.setCellValue(cadena);
            //Escribe el detalle
            int renglonInicio = 5;
            int rows = 0;
            int j = 0;
            int cnt = 0;
            int cantRowsFinal = 17;
            double sumaMonto = 0.0;
            while (rst.next()) {
                rows = renglonInicio + cnt;
                if (j > 1) {
                    firstSheet.shiftRows(rows, rows + cantRowsFinal, 1);
                    rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                } else {
                    rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                }
                j++;
                for (int i = 0; i < totalcolumnas - 1; i++) {
                    if (i == 7) {
                        sumaMonto = sumaMonto + rst.getDouble(i + 1);
                    }
                    com.syc.gestion.util.Util.createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
                cnt++;
            }
            //Footer
            rows = renglonInicio + cnt + 1;
            // sheet0.createRow(rows);
            rw = firstSheet.getRow(rows);
            celdarsad = rw.getCell(7);
            celdarsad.setCellValue(sumaMonto);
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
        }
    }

    public void writeSheet1PASOP(Connection conn, XSSFSheet firstSheet, DatosPedidoContrato datos) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        XSSFRow rw = null;
        try {
            query = "SELECT *FROM [rpt_CompraNETRedondeado_GERARDO]() where 1=1 " + datos.getcWhere();
            ps = conn.prepareStatement(query);
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            //Escribe el detalle
            int renglonInicio = 6;
            int rows = 0;
            int cnt = 0;
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                for (int i = 0; i < totalcolumnas; i++) {
                    createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                }
                cnt++;
            }
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
        }
    }

    public void writeSheet1ContratosFisicos(Connection conn, XSSFSheet firstSheet, DatosPedidoContrato datos, XSSFCellStyle estiloCell, XSSFCellStyle estiloCellRed) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        try {
            query = "select \r\n" + "cont.cIdUnidadEjecutora+' - '+upper(ue.D_DESCRIPCION) areaReq\r\n" + ",cont.cIdContratoDefinitivo\r\n" + ",cont.cidusuarioCreacion\r\n" + ",cont.cIdRFC\r\n" + ",prov.crazonSocial\r\n" + ",proced.cOficio cNumProcedCNET\r\n" + ",cont.cNoContratoCNET\r\n" + ",cont.nCodExpedienteCNET\r\n" + ",cont.nCodContratoCNET\r\n" + ",isnull(convert(varchar,doc.fFechaCarga,113 ),'') fFechaCarga\r\n" + ",isnull(doc.cUsuarioCarga,'')cUsuarioCargaDoc\r\n" + ",case when cont.lArchivoContCargado=1 then 'Archivo cargado' else 'Archivo no cargado' end estatusDoc\r\n" + ",convert(varchar,convert(date,cont.fFormalizacion),103)fFormalizacion\r\n" + ",cont.lArchivoContCargado\r\n" + "from mContrato as cont with(Nolock)\r\n" + "inner join mprocedimiento as proced with(Nolock) on cont.cIdProcedimiento=proced.cIdProcedimiento\r\n" + "inner join mCatalogoProveedor as prov with(Nolock) on cont.cIdRFC=prov.cIdRFC\r\n" + "inner join tCatUnidadEjecutora as ue with(Nolock)\r\n" + "on cont.cIdUnidadEjecutora=ue.cUnidadEjecutora\r\n" + "left join mDocumentosContrato doc with(Nolock)\r\n" + "on cont.cIdContratoDefinitivo=doc.cIdContratoDefinitivo\r\n" + "and doc.nIdDocumento=1\r\n" + "where cont.nIdEstado=4 " + datos.getcWhere();
            ps = conn.prepareStatement(query);
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            //Escribe el detalle
            int renglonInicio = 1;
            int rows = 0;
            int cnt = 0;
            XSSFRow rw = null;
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                for (int i = 0; i < totalcolumnas - 1; i++) {
                    if (i == 11) {
                        if (rst.getInt(totalcolumnas) == 0) {
                            com.syc.gestion.util.Util.createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloCellRed);
                        } else {
                            com.syc.gestion.util.Util.createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloCell);
                        }
                    } else {
                        createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                    }
                }
                cnt++;
            }
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
        }
    }

    public String[] selectRutaArchivo(Connection conn, String idCaso, int idGabinete, String tituloAplicacion, String nameDocto) throws SQLException {
        String[] resultado = new String[2];
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        try {
            query.append(" SELECT ");
            query.append(" v.UNIDAD_DISCO + v.RUTA_BASE + v.RUTA_DIRECTORIO + p.NOM_ARCHIVO_ORG as ruta_archivo_org, ");
            query.append(" v.UNIDAD_DISCO + v.RUTA_BASE + v.RUTA_DIRECTORIO + p.NOM_ARCHIVO_VOL as ruta_archivo_vol ");
            query.append(" FROM IMX_PAGINA p  WITH (NOLOCK), IMX_VOLUMEN v  WITH (NOLOCK), IMXCONTRATODIVERSO imxDiv  WITH (NOLOCK), CG_CASO c  WITH (NOLOCK),IMX_DOCUMENTO doc WITH (NOLOCK) WHERE ");
            query.append(" imxDiv.ID_GABINETE = p.ID_GABINETE ");
            query.append(" AND p.VOLUMEN = v.VOLUMEN ");
            query.append(" AND c.ID_CASO = ? ");
            query.append(" AND p.TITULO_APLICACION = ?  ");
            query.append(" AND doc.NOMBRE_DOCUMENTO=? ");
            query.append(" AND imxDiv.ID_GABINETE=C.C_ID_GABINETE  ");
            query.append(" AND  p.ID_GABINETE=doc.ID_GABINETE  ");
            query.append(" AND p.ID_CARPETA_PADRE=doc.ID_CARPETA_PADRE  ");
            query.append(" AND p.TITULO_APLICACION=doc.TITULO_APLICACION  ");
            query.append(" AND p.ID_DOCUMENTO=doc.ID_DOCUMENTO  ");
            query.append(" AND imxDiv.ID_GABINETE=?  ");
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, idCaso);
            ps.setString(2, tituloAplicacion);
            ps.setString(3, nameDocto);
            ps.setInt(4, idGabinete);
            rs = ps.executeQuery();
            if (rs.next()) {
                resultado[0] = rs.getString("ruta_archivo_org");
                resultado[1] = rs.getString("ruta_archivo_vol");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                rs.close();
            if (ps != null)
                ps.close();
            rs = null;
            ps = null;
        }
        return resultado;
    }

    public void writeSheet6IndicadoresCNET(Connection conn, XSSFSheet firstSheet, DatosPedidoContrato datos) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        XSSFRow rw = null;
        String cadena = "Contratos de Adquisiciones SAI (" + datos.getcFechaInicio() + " al " + datos.getcFechaFin() + ")";
        XSSFTable table = null;
        try {
            rw = (firstSheet.getRow(0) == null ? firstSheet.createRow(0) : firstSheet.getRow(0));
            XSSFCell celdarsad = (rw.getCell(0) == null ? rw.createCell(0) : rw.getCell(0));
            celdarsad.setCellValue(cadena);
            query = "select \r\n" + "cont.cIdContratoDefinitivo Contrato_SAI\r\n" + ",cont.cNoContratoCNET Contrato_CNET\r\n" + ",proced.cOficio Procedimiento_CNET\r\n" + ",cont.cIdRFC RFC,convert(varchar,cont.fFormalizacion,103) Fecha_Formalizacion\r\n" + ",leg.cFundamentoLegal Fundamento_Legal\r\n" + ",cat.cCategoria Tipo_Procedimiento\r\n" + ",cat.nIdCategoria nIdCategoria\r\n" + ",case when proced.isPlurianual=1 then part.totalPluri\r\n" + "else case when adj.lContratoAbierto=1 then part.totalConIVAmaximo else part.totalConIVAminimo end\r\n" + "end + isnull(conv.totalMod,0) as Total_Contrato\r\n" + "from mContrato as cont with(Nolock)\r\n" + "inner join mProcedimiento as proced with(Nolock)\r\n" + "on proced.cIdProcedimiento=cont.cIdProcedimiento\r\n" + "inner join mProcedimientoAdjudicacion adj with(Nolock)\r\n" + "on adj.cIdRFC=cont.cIdRFC and adj.nIdconsecutivoAdj=cont.nIdconsecutivoAdj\r\n" + "and cont.cIdProcedimiento=adj.cIdProcedimiento\r\n" + "inner join mCatalogoCategoriaProcedimiento as cat with(Nolock)\r\n" + "on cat.nIdCategoria=proced.nIdCategoria\r\n" + "inner join mCatalogoFundamentoLegal leg with(Nolock)\r\n" + "on leg.nIdCategoria=cat.nIdCategoria\r\n" + "and leg.nIdFundamentoLeg=adj.nIdFundamentoLeg\r\n" + "inner join (\r\n" + "	select \r\n" + "	cIdProcedimiento\r\n" + "	,cIdRFC\r\n" + "	,sum(mMontoNetoMinimo)totalConIVAminimo\r\n" + "	,sum(mMontoNetoLineaMax)totalConIVAmaximo\r\n" + "	,sum(mMontoNetoPluri)totalPluri,nIdconsecutivoAdj\r\n" + "	from mProcedimientoAdjudicacionPartidas as part with(Nolock)\r\n" + "	group by cIdProcedimiento,nIdconsecutivoAdj\r\n" + "	,cIdRFC\r\n" + ")part on part.cIdProcedimiento=cont.cIdProcedimiento\r\n" + "and part.cIdRFC=cont.cIdRFC and part.nIdconsecutivoAdj=cont.nIdconsecutivoAdj \r\n" + "left join(\r\n" + "	select contMod.cIdContratoDefinitivo\r\n" + "	,sum(isnull(contMod.mTotalModificacion,0))as totalMod\r\n" + "	From mContratoModificado  as contMod with(Nolock)\r\n" + "	where contMod.nEstado=4\r\n" + "	group by contMod.cIdContratoDefinitivo\r\n" + ")conv on conv.cIdContratoDefinitivo=cont.cIdContratoDefinitivo\r\n" + "where cont.nIdEstado=4\r\n" + "and convert(date,cont.fFormalizacion)>=convert(date,'" + datos.getcFechaInicio() + "')\r\n" + "and convert(date,cont.fFormalizacion)<=convert(date,'" + datos.getcFechaFin() + "')";
            ps = conn.prepareStatement(query);
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            //Escribe el detalle
            int renglonInicio = 2;
            int rows = 0;
            int cnt = 0;
            table = firstSheet.getTables().get(0);
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                for (int i = 0; i < totalcolumnas; i++) {
                    createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                }
                cnt++;
            }
            if (cnt > 0)
                updateSizeTable((cnt + renglonInicio - 1), table);
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
            table = null;
        }
    }

    public void writeSheet7IndicadoresCNET(Connection conn, XSSFSheet firstSheet, DatosPedidoContrato datos) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        XSSFRow rw = null;
        XSSFTable table = null;
        String cadena = "Contratos de Obra SAI (" + datos.getcFechaInicio() + " al " + datos.getcFechaFin() + ")";
        try {
            rw = (firstSheet.getRow(0) == null ? firstSheet.createRow(0) : firstSheet.getRow(0));
            XSSFCell celdarsad = (rw.getCell(0) == null ? rw.createCell(0) : rw.getCell(0));
            celdarsad.setCellValue(cadena);
            query = "select \r\n" + "apartado.foliosai Contrato_SAI\r\n" + ",contObra.cIdContrato Contrato_CNET\r\n" + ",contObra.cNoProcedimientoCNET Procedimiento_CNET\r\n" + ",contObra.cIdRFC RFC\r\n" + ",convert(varchar,convert(date,contObra.fFirmaContrato),103)Fecha_Formalizacion\r\n" + ",fleg.cdescarticulo Fundamento_Legal\r\n" + ",tipoAdj.cDescripcion Tipo_Procedimiento\r\n" + ",tipoAdj.cIdTAdjudicacion\r\n" + ",contObra.mTotal +isnull(convenios.incrementoConvenio,0)Total_Contrato\r\n" + "from tobrapublicaapartadoencabezado apartado WITH(nolock) \r\n" + "inner JOIN tobrapublicaprecompromisoencabezado precompromiso WITH(nolock) \r\n" + "ON apartado.foliosai = precompromiso.foliosai\r\n" + "inner JOIN tobrapublicacompromisoencabezado compromisoObra WITH(nolock)\r\n" + "ON apartado.foliosai = compromisoObra.foliosai\r\n" + "inner JOIN dbo.pContratoObra AS contObra WITH(NOLOCK)\r\n" + "ON contObra.cIdContrato=compromisoObra.cCveContrato \r\n" + "LEFT OUTER JOIN dbo.tCatalogoAdjudicacion tipoAdj WITH(NOLOCK)\r\n" + "ON tipoAdj.cIdTAdjudicacion=precompromiso.cTipoAdjudica\r\n" + "LEFT OUTER JOIN dbo.tCatalogoOPFundamentoLegal fleg WITH(NOLOCK)\r\n" + "ON fleg.cIdTAdjudicacion=precompromiso.cTipoAdjudica\r\n" + "and fleg.cArticulo=precompromiso.cArticulo\r\n" + "LEFT OUTER JOIN (SELECT ccvecontrato,\r\n" + "            Min(ffechainicontr)         AS\r\n" + "            InicioContratoModificado,\r\n" + "            Max(ffechafincontr)         AS\r\n" + "            FinContratoModificado,\r\n" + "            Sum( mmontoincremento)\r\n" + "            + Sum( mmontoincrementoiva) AS incrementoConvenio\r\n" + "    FROM   tobrapublicaconvmodifencabezado WITH(nolock)\r\n" + "    WHERE  cdocumentohaplicado = 'S'\r\n" + "    GROUP  BY ccvecontrato) AS convenios\r\n" + "ON compromisoObra.ccvecontrato = convenios.ccvecontrato\r\n" + "where contObra.nEstatus=2 \r\n" + "and convert(date,contObra.fFirmaContrato)>=convert(date,'" + datos.getcFechaInicio() + "')\r\n" + "and convert(date,contObra.fFirmaContrato)<=convert(date,'" + datos.getcFechaFin() + "')";
            ps = conn.prepareStatement(query);
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            //Escribe el detalle
            int renglonInicio = 2;
            int rows = 0;
            int cnt = 0;
            table = firstSheet.getTables().get(0);
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                for (int i = 0; i < totalcolumnas; i++) {
                    createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                }
                cnt++;
            }
            if (cnt > 0)
                updateSizeTable((cnt + renglonInicio - 1), table);
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
            table = null;
        }
    }

    public void writeSheet8IndicadoresCNET(Connection conn, XSSFSheet firstSheet, DatosPedidoContrato datos, String cEjercicio) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        XSSFRow rw = null;
        XSSFTable table = null;
        String cadena = "Contratos de Adquisiciones SAI Ambiental (" + datos.getcFechaInicio() + " al " + datos.getcFechaFin() + ")";
        try {
            rw = (firstSheet.getRow(0) == null ? firstSheet.createRow(0) : firstSheet.getRow(0));
            XSSFCell celdarsad = (rw.getCell(0) == null ? rw.createCell(0) : rw.getCell(0));
            celdarsad.setCellValue(cadena);
            query = "select \r\n" + "cont.cIdContratoDefinitivo Contrato_SAI\r\n" + ",cont.cNoContratoCNET Contrato_CNET\r\n" + ",proced.cOficio Procedimiento_CNET\r\n" + ",cont.cIdRFC RFC,convert(varchar,cont.fFormalizacion,103) Fecha_Formalizacion\r\n" + ",leg.cFundamentoLegal Fundamento_Legal\r\n" + ",cat.cCategoria Tipo_Procedimiento\r\n" + ",cat.nIdCategoria nIdCategoria\r\n" + ",case when proced.isPlurianual=1 then part.totalPluri\r\n" + "else case when adj.lContratoAbierto=1 then part.totalConIVAmaximo else part.totalConIVAminimo end\r\n" + "end + isnull(conv.totalMod,0) as Total_Contrato\r\n" + "from compensacion_ambiental_" + cEjercicio + ".dbo.mContrato as cont with(Nolock)\r\n" + "inner join compensacion_ambiental_" + cEjercicio + ".dbo.mProcedimiento as proced with(Nolock)\r\n" + "on proced.cIdProcedimiento=cont.cIdProcedimiento\r\n" + "inner join compensacion_ambiental_" + cEjercicio + ".dbo.mProcedimientoAdjudicacion adj with(Nolock)\r\n" + "on adj.cIdRFC=cont.cIdRFC and adj.nIdconsecutivoAdj=cont.nIdconsecutivoAdj\r\n" + "and cont.cIdProcedimiento=adj.cIdProcedimiento\r\n" + "inner join compensacion_ambiental_" + cEjercicio + ".dbo.mCatalogoCategoriaProcedimiento as cat with(Nolock)\r\n" + "on cat.nIdCategoria=proced.nIdCategoria\r\n" + "inner join compensacion_ambiental_" + cEjercicio + ".dbo.mCatalogoFundamentoLegal leg with(Nolock)\r\n" + "on leg.nIdCategoria=cat.nIdCategoria\r\n" + "and leg.nIdFundamentoLeg=adj.nIdFundamentoLeg\r\n" + "inner join (\r\n" + "	select \r\n" + "	cIdProcedimiento\r\n" + "	,cIdRFC\r\n" + "	,sum(mMontoNetoMinimo)totalConIVAminimo\r\n" + "	,sum(mMontoNetoLineaMax)totalConIVAmaximo\r\n" + "	,sum(mMontoNetoPluri)totalPluri,nIdconsecutivoAdj\r\n" + "	from compensacion_ambiental_" + cEjercicio + ".dbo.mProcedimientoAdjudicacionPartidas as part with(Nolock)\r\n" + "	group by cIdProcedimiento,nIdconsecutivoAdj\r\n" + "	,cIdRFC\r\n" + ")part on part.cIdProcedimiento=cont.cIdProcedimiento\r\n" + "and part.cIdRFC=cont.cIdRFC and part.nIdconsecutivoAdj=cont.nIdconsecutivoAdj\r\n" + "left join(\r\n" + "	select contMod.cIdContratoDefinitivo\r\n" + "	,sum(isnull(contMod.mTotalModificacion,0))as totalMod\r\n" + "	From compensacion_ambiental_" + cEjercicio + ".dbo.mContratoModificado  as contMod with(Nolock)\r\n" + "	where contMod.nEstado=4\r\n" + "	group by contMod.cIdContratoDefinitivo\r\n" + ")conv on conv.cIdContratoDefinitivo=cont.cIdContratoDefinitivo\r\n" + "where cont.nIdEstado=4\r\n" + "and convert(date,cont.fFormalizacion)>=convert(date,'" + datos.getcFechaInicio() + "')\r\n" + "and convert(date,cont.fFormalizacion)<=convert(date,'" + datos.getcFechaFin() + "')";
            ps = conn.prepareStatement(query);
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            //Escribe el detalle
            int renglonInicio = 2;
            int rows = 0;
            int cnt = 0;
            table = firstSheet.getTables().get(0);
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                for (int i = 0; i < totalcolumnas; i++) {
                    createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                }
                cnt++;
            }
            if (cnt > 0)
                updateSizeTable((cnt + renglonInicio - 1), table);
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
            table = null;
        }
    }

    public void writeSheet9IndicadoresCNET(Connection conn, XSSFSheet firstSheet, DatosPedidoContrato datos, String cEjercicio) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        XSSFRow rw = null;
        XSSFTable table = null;
        String cadena = "Contratos de Obra SAI Fonden (" + datos.getcFechaInicio() + " al " + datos.getcFechaFin() + ")";
        try {
            rw = (firstSheet.getRow(0) == null ? firstSheet.createRow(0) : firstSheet.getRow(0));
            XSSFCell celdarsad = (rw.getCell(0) == null ? rw.createCell(0) : rw.getCell(0));
            celdarsad.setCellValue(cadena);
            query = "select \r\n" + "apartado.foliosai Contrato_SAI\r\n" + ",contObra.cIdContrato Contrato_CNET\r\n" + ",contObra.cNoProcedimientoCNET Procedimiento_CNET\r\n" + ",contObra.cIdRFC RFC\r\n" + ",convert(varchar,convert(date,contObra.fFirmaContrato),103)Fecha_Formalizacion\r\n" + ",fleg.cdescarticulo Fundamento_Legal\r\n" + ",tipoAdj.cDescripcion Tipo_Procedimiento\r\n" + ",tipoAdj.cIdTAdjudicacion\r\n" + ",contObra.mTotal +isnull(convenios.incrementoConvenio,0)Total_Contrato\r\n" + "from fonden_" + cEjercicio + ".dbo.tobrapublicaapartadoencabezado apartado WITH(nolock) \r\n" + "inner JOIN fonden_" + cEjercicio + ".dbo.tobrapublicaprecompromisoencabezado precompromiso WITH(nolock) \r\n" + "ON apartado.foliosai = precompromiso.foliosai\r\n" + "inner JOIN fonden_" + cEjercicio + ".dbo.tobrapublicacompromisoencabezado compromisoObra WITH(nolock)\r\n" + "ON apartado.foliosai = compromisoObra.foliosai\r\n" + "inner JOIN fonden_" + cEjercicio + ".dbo.pContratoObra AS contObra WITH(NOLOCK)\r\n" + "ON contObra.cIdContrato=compromisoObra.cCveContrato \r\n" + "LEFT OUTER JOIN fonden_" + cEjercicio + ".dbo.tCatalogoAdjudicacion tipoAdj WITH(NOLOCK)\r\n" + "ON tipoAdj.cIdTAdjudicacion=precompromiso.cTipoAdjudica\r\n" + "LEFT OUTER JOIN fonden_" + cEjercicio + ".dbo.tCatalogoOPFundamentoLegal fleg WITH(NOLOCK)\r\n" + "ON fleg.cIdTAdjudicacion=precompromiso.cTipoAdjudica\r\n" + "and fleg.cArticulo=precompromiso.cArticulo\r\n" + "LEFT OUTER JOIN (SELECT ccvecontrato,\r\n" + "            Min(ffechainicontr)         AS\r\n" + "            InicioContratoModificado,\r\n" + "            Max(ffechafincontr)         AS\r\n" + "            FinContratoModificado,\r\n" + "            Sum( mmontoincremento)\r\n" + "            + Sum( mmontoincrementoiva) AS incrementoConvenio\r\n" + "    FROM   fonden_" + cEjercicio + ".dbo.tobrapublicaconvmodifencabezado WITH(nolock)\r\n" + "    WHERE  cdocumentohaplicado = 'S'\r\n" + "    GROUP  BY ccvecontrato) AS convenios\r\n" + "ON compromisoObra.ccvecontrato = convenios.ccvecontrato\r\n" + "where contObra.nEstatus=2 \r\n" + "and convert(date,contObra.fFirmaContrato)>=convert(date,'" + datos.getcFechaInicio() + "')\r\n" + "and convert(date,contObra.fFirmaContrato)<=convert(date,'" + datos.getcFechaFin() + "')";
            ps = conn.prepareStatement(query);
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            //Escribe el detalle
            int renglonInicio = 2;
            int rows = 0;
            int cnt = 0;
            table = firstSheet.getTables().get(0);
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                for (int i = 0; i < totalcolumnas; i++) {
                    createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                }
                cnt++;
            }
            if (cnt > 0)
                updateSizeTable((cnt + renglonInicio - 1), table);
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
            table = null;
        }
    }

    public void cantidadProcedIndicadoresCNET(Connection conn, XSSFSheet firstSheet, DatosPedidoContrato datos, String cEjercicio, boolean esSAIAlterno) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        ResultSet rst = null;
        XSSFRow rw = null;
        int[] arrayCantTipoProced = new int[3];
        try {
            query = "select \r\n" + "proced.cOficio Procedimiento_CNET\r\n" + ",cat.nIdCategoria nIdCategoria\r\n" + "from mContrato as cont with(Nolock)\r\n" + "inner join mProcedimiento as proced with(Nolock)\r\n" + "on proced.cIdProcedimiento=cont.cIdProcedimiento\r\n" + "inner join mCatalogoCategoriaProcedimiento as cat with(Nolock)\r\n" + "on cat.nIdCategoria=proced.nIdCategoria\r\n" + "where cont.nIdEstado=4\r\n" + "and convert(date,cont.fFormalizacion)>=convert(date,'" + datos.getcFechaInicio() + "')\r\n" + "and convert(date,cont.fFormalizacion)<=convert(date,'" + datos.getcFechaFin() + "')\r\n" + "group by proced.cOficio \r\n" + ",cat.nIdCategoria ";
            if (!esSAIAlterno) {
                query += " union select  \r\n" + "	proced.cOficio Procedimiento_CNET \r\n" + "	,cat.nIdCategoria nIdCategoria \r\n" + "	from compensacion_ambiental_" + cEjercicio + ".dbo.mContrato as cont with(Nolock) \r\n" + "	inner join compensacion_ambiental_" + cEjercicio + ".dbo.mProcedimiento as proced with(Nolock) \r\n" + "	on proced.cIdProcedimiento=cont.cIdProcedimiento \r\n" + "	inner join compensacion_ambiental_" + cEjercicio + ".dbo.mCatalogoCategoriaProcedimiento as cat with(Nolock) \r\n" + "	on cat.nIdCategoria=proced.nIdCategoria \r\n" + "	where cont.nIdEstado=4 \r\n" + "	and convert(date,cont.fFormalizacion)>=convert(date,'" + datos.getcFechaInicio() + "') \r\n" + "	and convert(date,cont.fFormalizacion)<=convert(date,'" + datos.getcFechaFin() + "') \r\n" + "	group by proced.cOficio  \r\n" + "	,cat.nIdCategoria";
            }
            ps = conn.prepareStatement(query);
            rst = ps.executeQuery();
            while (rst.next()) {
                if (rst.getInt("nIdCategoria") < 5) {
                    arrayCantTipoProced[0]++;
                } else if (rst.getInt("nIdCategoria") > 4 && rst.getInt("nIdCategoria") < 10) {
                    arrayCantTipoProced[1]++;
                } else {
                    arrayCantTipoProced[2]++;
                }
            }
            //write licitaciones
            for (int i = 0; i < arrayCantTipoProced.length; i++) {
                rw = (firstSheet.getRow(i + 1) == null ? firstSheet.createRow(i + 1) : firstSheet.getRow(i + 1));
                XSSFCell celdarsad = (rw.getCell(6) == null ? rw.createCell(6) : rw.getCell(6));
                celdarsad.setCellValue(arrayCantTipoProced[i]);
            }
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rst = null;
            ps = null;
        }
    }

    public void writeSheetReporteTotalizado(Connection conn, XSSFSheet firstSheet, DatosPedidoContrato datos, String cEjercicioActivo) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        XSSFRow rw = null;
        String cadena = "Ejercicio " + cEjercicioActivo;
        try {
            rw = (firstSheet.getRow(2) == null ? firstSheet.createRow(2) : firstSheet.getRow(2));
            XSSFCell celdarsad = (rw.getCell(0) == null ? rw.createCell(0) : rw.getCell(0));
            celdarsad.setCellValue(cadena);
            query = "select *from v_mReporteOCTotalizado with(Nolock) where 1=1 " + datos.getcWhere() + " union select *from v_mReporteOCTotalizadoPlu  with(Nolock) where 1=1 " + datos.getcWhere() + " union select *from v_mreporteObraTotalizado  with(Nolock) where 1=1 " + datos.getcWhere() + " union select *from v_mReporteOcTotalizadoConvEjerAnt  with(Nolock) where 1=1 " + datos.getcWhere() + " union select *from v_mReporteOcTotalizadoRemanente  with(Nolock) where 1=1 " + datos.getcWhere() + " union select *from v_mReporteOCTotalizadoContratoArt25  with(Nolock) where 1=1 " + datos.getcWhere();
            log.info(query);
            ps = conn.prepareStatement(query);
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            //Escribe el detalle
            int renglonInicio = 5;
            int rows = 0;
            int cnt = 0;
            XSSFTable table = firstSheet.getTables().get(0);
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                for (int i = 0; i < totalcolumnas - 3; i++) {
                    createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                }
                cnt++;
            }
            if (cnt > 0)
                updateSizeTable((cnt + renglonInicio - 1), table);
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
        }
    }

    private void updateSizeTable(int lastTableRow, XSSFTable table) {
        AreaReference newTableArea = new AreaReference(table.getStartCellReference(), new CellReference(lastTableRow, table.getEndCellReference().getCol()), SpreadsheetVersion.EXCEL2007);
        table.setArea(newTableArea);
        table.updateReferences();
    }

    public void writeSheetReporteContCap4(Connection conn, XSSFSheet firstSheet, DatosPedidoContrato datos, String cEjercicioActivo) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        XSSFRow rw = null;
        String cadena = "Ejercicio " + cEjercicioActivo;
        try {
            rw = (firstSheet.getRow(2) == null ? firstSheet.createRow(2) : firstSheet.getRow(2));
            XSSFCell celdarsad = (rw.getCell(0) == null ? rw.createCell(0) : rw.getCell(0));
            celdarsad.setCellValue(cadena);
            query = "select *from v_mreporteOCTotalizadoCap4 with(Nolock) where 1=1 " + datos.getcWhere();
            log.info(query);
            ps = conn.prepareStatement(query);
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            //Escribe el detalle
            int renglonInicio = 5;
            int rows = 0;
            int cnt = 0;
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                for (int i = 0; i < totalcolumnas - 3; i++) {
                    createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                }
                cnt++;
            }
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
        }
    }

    public void writeSheetReportePSP(Connection conn, XSSFSheet firstSheet, DatosPedidoContrato datos, String cEjercicio, String desa, boolean esSAIAlterno) throws Exception {
        StringBuilder queryProd = null;
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        XSSFRow rw = null;
        XSSFTable table = null;
        try {
            queryProd = queryProduccionPSP(cEjercicio, desa, esSAIAlterno);
            //obtener el query de ambiental
            if (!esSAIAlterno) {
                queryCompensacionAmbientalPSP(queryProd, cEjercicio);
            }
            log.info(queryProd.toString());
            ps = conn.prepareStatement(queryProd.toString());
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            //Escribe el detalle
            int renglonInicio = 1;
            int rows = 0;
            int cnt = 0;
            Cell cell = null;
            table = firstSheet.getTables().get(0);
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                cell = (rw.getCell(0) == null ? rw.createCell(0) : rw.getCell(0));
                cell.setCellValue(cnt + 1);
                for (int i = 1; i <= totalcolumnas; i++) {
                    createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i), rsMetadata.getColumnType(i));
                }
                cnt++;
            }
            if (cnt > 0)
                updateSizeTable((cnt + renglonInicio - 1), table);
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
            queryProd = null;
        }
    }

    private StringBuilder queryProduccionPSP(String cEjercicio, String desa, boolean esSAIAlterno) {
        StringBuilder query = new StringBuilder();
        query.append("select    ");
        query.append("		 		 case when datPSP.lEsMaestro=1 then 'MAESTROS' ELSE  upper(req.cDescCoortaCordinacion) END coordinacion  ");
        query.append("		 		 ,upper(resp.cDescripcion) areaResponsable  ");
        query.append("		 		 ,upper(proced.cOficio) numProced  ");
        query.append("		 		 ,upper(cont.cNoContratoCNET) cNoContratoCNET ");
        query.append("				 ,upper(isnull(datPSP.cDenominacionProyecto,''))cDenominacionProyecto ");
        query.append("		 		 ,upper(prov.cRazonSocial) cRazonSocial   ");
        query.append("		 		 ,upper(replace(cont.cIdRFC,'-',''))cIdRFC  ");
        query.append("		 		 ,case when datPSP.nCentroTrabajo=37 then 'OFC' else 'PDF' end ofc_pdf   ");
        query.append("		 		 ,case when datPSP.nCentroTrabajo=37 then upper(centTrab.d_centrotrab) else 'PDF '+upper(centTrab.d_centrotrab) end  centroTrabajo  ");
        query.append("		 		 ,case when termant.cIdContratoDefinitivo is not null    ");
        query.append("		 		 	then case when convert(date,termant.fFechaTermino)<=convert(date,GETDATE()) then 'BAJA' ELSE 'ACTIVO' END   ");
        query.append("		 		 else case when convert(date,cont.fFin)<=convert(date,GETDATE()) then 'BAJA' ELSE 'ACTIVO' END end estatus   ");
        query.append("		 		 ,CONVERT(VARCHAR,cont.fInicio,103)fechaInicio   ");
        query.append("		 		 ,CONVERT(VARCHAR,cont.fFin,103)fechaFin ");
        query.append("				 ,case when termant.fFechaTermino is null then '' else convert(varchar, termant.fFechaTermino,103)end fechaTerminacionAnticipada ");
        query.append("				 ,isnull(datPSP.mMontoMensual,0.00)mMontoMensual ");
        query.append("				 ,isnull(gastTraslado.totalMaximoContrato,0.00) montoTotalGastosTraslado ");
        query.append("		 		 ,case when termant.cIdContratoDefinitivo is not null then isnull(pagado.totalPagado,0) else maximo.totalMaximoContrato end montoMaximo ");
        query.append("				 ,isnull(pagado.totalPagado,0)-isnull(pagoGastosTraslado.totalPagadoGastosTraslado,0) montoPagadoHonorarios ");
        query.append("				 ,isnull(pagoGastosTraslado.totalPagadoGastosTraslado,0) montoPagadoGastosTraslado ");
        query.append("		 		 ,isnull(pagado.totalPagado,0) importePagado   ");
        query.append("		 		 ,'PRINCIPAL'tipoSAI  ");
        query.append("		 		 ,case when termant.cIdContratoDefinitivo is not null THEN 'SI' ELSE 'NO' END tieneTermAnticipada  ");
        query.append("		         ,cont.cIdContratoDefinitivo ");
        query.append("				 ,isnull(tieneCompFiscal.tieneCompromisoFiscal,'NO')tieneCompromisoFiscal ");
        query.append("				 ,isnull(tieneCompIngresosProp.tieneCompromisoIngresosPropios,'NO')tieneCompromisoIngresosPropios ");
        query.append("		 		 from mContrato as cont with(Nolock)   ");
        query.append("		 		 inner join mDatosContratoPSP as datPSP with(Nolock)   ");
        query.append("		 		 on datPSP.cIdcontratoDefinitivo=cont.cIdContratoDefinitivo   ");
        query.append("		 		 inner join mCatalogoProveedor prov with(Nolock)   ");
        query.append("		 		 on prov.cIdRFC=cont.cIdRFC   ");
        query.append("		 		 inner join nomina_" + cEjercicio + ".dbo.nom_Unidad_Ejecutora resp with(Nolock)   ");
        query.append("		 		 on resp.cUejecutora=datPSP.cAreaResponsable   ");
        query.append("		 		 inner join nomina_" + cEjercicio + ".dbo.nom_Unidad_Ejecutora req with(Nolock)   ");
        query.append("		 		 on req.cUejecutora=datPSP.cAreaRequirente   ");
        query.append("		 		 inner join nomina_" + cEjercicio + ".dbo.nom_centrotrabajo centTrab with(Nolock)   ");
        query.append("		 		 on centTrab.c_centrotrab=datPSP.nCentroTrabajo   ");
        query.append("		 		 and centTrab.c_empresa=1  ");
        query.append("		 		 inner join mProcedimiento as proced with(Nolock)  ");
        query.append("		 		 on proced.cIdProcedimiento=cont.cIdProcedimiento  ");
        query.append("		 		 inner join(select    ");
        query.append("		 		 	case when pa.lContratoAbierto=1 then round(SUM(isnull(pap.mMontoNetoLineaMax,ISNULL(pap.mMontoNetoLinea,0))),2)   ");
        query.append("		 		 	else round(SUM(isnull(pap.mMontoNetoMinimo,0)),2)   ");
        query.append("		 		 	end totalMaximoContrato   ");
        query.append("		 		 	,pap.cIdProcedimiento   ");
        query.append("		 		 	,pap.cIdRFC,isnull(pa.mMontoTotalPlurianual,0) mMontoTotalPedContPlurianual   ");
        query.append("		 		 	,SUM(isnull(pap.mMontoNetoMinimo,0)) as totalMinimoContrato   ");
        query.append("		 		 	,pap.nIdconsecutivoAdj   ");
        query.append("		 		 	from mProcedimientoAdjudicacionPartidas as pap with(nolock)   ");
        query.append("		 		 	inner join mProcedimientoAdjudicacion as pa with(nolock) on pap.cIdProcedimiento=pa.cIdProcedimiento   ");
        query.append("		 		 	and pa.cIdRFC=pap.cIdRFC and pa.nIdconsecutivoAdj=pap.nIdconsecutivoAdj   ");
        query.append("		 		 	group by pap.cIdProcedimiento,pap.cIdRFC,pa.lContratoAbierto,pa.mMontoTotalPlurianual,pap.nIdconsecutivoAdj   ");
        query.append("		 		 )maximo on cont.cIdProcedimiento=maximo.cIdProcedimiento and cont.cIdRFC=maximo.cIdRFC and maximo.nIdconsecutivoAdj=cont.nIdconsecutivoAdj ");
        query.append("				 left join( ");
        query.append("					select    ");
        query.append("						case when pa.lContratoAbierto=1 then round(SUM(isnull(pap.mMontoNetoLineaMax,ISNULL(pap.mMontoNetoLinea,0))),2)   ");
        query.append("						else round(SUM(isnull(pap.mMontoNetoMinimo,0)),2)   ");
        query.append("						end totalMaximoContrato   ");
        query.append("						,pap.cIdProcedimiento   ");
        query.append("						,pap.cIdRFC,isnull(pa.mMontoTotalPlurianual,0) mMontoTotalPedContPlurianual   ");
        query.append("						,SUM(isnull(pap.mMontoNetoMinimo,0)) as totalMinimoContrato   ");
        query.append("						,SUM(isnull(pap.mMontoNetoLinea,0)) as totalLineaContrato   ");
        query.append("						,pap.nIdconsecutivoAdj   ");
        query.append("						from mProcedimientoAdjudicacionPartidas as pap with(nolock)   ");
        query.append("						inner join mProcedimientoAdjudicacion as pa with(nolock) on pap.cIdProcedimiento=pa.cIdProcedimiento   ");
        query.append("						and pa.cIdRFC=pap.cIdRFC and pa.nIdconsecutivoAdj=pap.nIdconsecutivoAdj  ");
        query.append("						where pap.nIdLineaConsolidado=2 ");
        query.append("						group by pap.cIdProcedimiento,pap.cIdRFC,pa.lContratoAbierto,pa.mMontoTotalPlurianual,pap.nIdconsecutivoAdj ");
        query.append("				 )gastTraslado on gastTraslado.cIdProcedimiento=maximo.cIdProcedimiento ");
        query.append("				 and gastTraslado.cIdRFC=maximo.cIdRFC ");
        query.append("				 and gastTraslado.nIdconsecutivoAdj=maximo.nIdconsecutivoAdj ");
        query.append("		 		 left join mContratoTerminacionAnticipada as termant with(Nolock)   ");
        query.append("		 		 on termant.cIdContratoDefinitivo=cont.cIdContratoDefinitivo   ");
        query.append("		 		 left join(   ");
        query.append("		 		 	select    ");
        query.append("		 		 	sum(totalPagado) totalPagado   ");
        query.append("		 		 	,cFolioPAGODIVERSO   ");
        query.append("		 		 	from (   ");
        query.append("		 		 		SELECT    ");
        query.append("		 		 			sum(isnull(d.mImporteMasIva,0))+(ISNULL(dismDevengado.totalDism,0))-(ISNULL(reintegro.totalReintegro,0))totalPagado   ");
        query.append("		 		 			,e.cFolioPAGODIVERSO,month(pe.faplicacion) nMes   ");
        query.append("		 		 			,e.caNoContrarrecibo    ");
        query.append("		 		 			FROM dbo.tPAGODIVERSOEncabezado e WITH (NOLOCK)	   ");
        query.append("		 		 			INNER JOIN dbo.tPAGODIVERSODetalle d WITH (NOLOCK) ON e.nFolioPAGODIVERSO = d.nFolioPAGODIVERSO AND e.cDocumentoHaplicado='S'	   ");
        query.append("		 		 			inner join tPagadoEncabezado as pe with(Nolock) on pe.caNoContrarrecibo=e.caNoContrarrecibo and pe.cDocumentoHaplicado='S'   ");
        query.append("		 		 			and pe.cTipoPago='PAGODIVERSO'   ");
        query.append("		 		 			LEFT join (   ");
        query.append("		 		 					SELECT    ");
        query.append("		 		 					sum(mImporte) totalReintegro,cxp   ");
        query.append("		 		 					FROM  tReintegroEncabezado as reintEnc with(Nolock)   ");
        query.append("		 		 					inner join tReintegroDetalle as reintDet with(Nolock)    ");
        query.append("		 		 					on reintDet.nFolioReintegro=reintEnc.nFolioReintegro and reintEnc.cDocumentoHaplicado='S'   ");
        query.append("		 		 					GROUP BY cxp   ");
        query.append("		 		 			)reintegro ON reintegro.cxp=e.caNoContrarrecibo    ");
        query.append("		 		 			left join(   ");
        query.append("		 		 				select    ");
        query.append("		 		 				sum(mTotal)totalDism, caNoContrarreciboRef   ");
        query.append("		 		 					from tDisminucionDevEncabezado enc (nolock)   ");
        query.append("		 		 					inner join tDisminucionDevDetalle det (nolock)   ");
        query.append("		 		 					on enc.nFolioDisminucionDev = det.nFolioDisminucionDev   ");
        query.append("		 		 					where cTipoDoc='PAGODIVERSO'   ");
        query.append("		 		 					group by    ");
        query.append("		 		 					caNoContrarreciboRef   ");
        query.append("		 		 			)dismDevengado on dismDevengado.caNoContrarreciboRef=e.caNoContrarrecibo    ");
        query.append("		 		 			group by e.cFolioPAGODIVERSO,month(pe.faplicacion),reintegro.totalReintegro,dismDevengado.totalDism   ");
        query.append("		 		 			,e.caNoContrarrecibo   ");
        query.append("		 		 		)sub   ");
        query.append("		 		 	group by cFolioPAGODIVERSO   ");
        query.append("		 		 )pagado on pagado.cFolioPAGODIVERSO=cont.cIdContratoDefinitivo ");
        query.append("				 left join (select  ");
        query.append("					 recep.cIdpedContDef ");
        query.append("					 ,sum(lineas.mMontoConIVA)-SUM(recep.mDescuentoConIVA) as totalPagadoGastosTraslado ");
        query.append("					 from mRecepcionpMat as recep with(Nolock) ");
        query.append("					 inner join mRecepcionpMatLineas as lineas with(Nolock) ");
        query.append("					 on lineas.cIdpedContDef=recep.cIdpedContDef ");
        query.append("					 and lineas.cIdRecepMat=recep.cIdRecepMat ");
        query.append("					 and lineas.nIdConsecutivoRecepM=recep.nIdConsecutivoRecepM ");
        query.append("					 where recep.nIdEstadoRecepMat=3  ");
        query.append("					 and lineas.nIdLineaConsolidado=2 ");
        query.append("					 group by recep.cIdpedContDef ");
        query.append("				 )pagoGastosTraslado on pagoGastosTraslado.cIdpedContDef=cont.cIdContratoDefinitivo ");
        query.append("				 left join( ");
        query.append("					select  ");
        query.append("					 cIdContrato ");
        query.append("					 ,'SI'tieneCompromisoFiscal ");
        query.append("					 from tCompromisoEncabezado enc with(Nolock) ");
        query.append("					 inner join tCompromisoDetalle as det with(Nolock) ");
        query.append("					 on enc.nFolioCompromiso=det.nFolioCompromiso ");
        query.append("					 where enc.cDocumentoHaplicado='S' ");
        query.append("					 and substring(det.EP,40,1)='1' ");
        query.append("					 group by cIdContrato ");
        query.append("				 )tieneCompFiscal on tieneCompFiscal.cIdContrato=cont.cIdContratoDefinitivo ");
        query.append("		 		 left join( ");
        query.append("					select  ");
        query.append("					 cIdContrato ");
        query.append("					 ,'SI'tieneCompromisoIngresosPropios ");
        query.append("					 from tCompromisoEncabezado enc with(Nolock) ");
        query.append("					 inner join tCompromisoDetalle as det with(Nolock) ");
        query.append("					 on enc.nFolioCompromiso=det.nFolioCompromiso ");
        query.append("					 where enc.cDocumentoHaplicado='S' ");
        query.append("					 and substring(det.EP,40,1)='4' ");
        query.append("					 group by cIdContrato ");
        query.append("				 )tieneCompIngresosProp on tieneCompIngresosProp.cIdContrato=cont.cIdContratoDefinitivo ");
        query.append("				 where cont.nIdEstado=4   ");
        query.append("		 		 and cont.lEsPSP=1  ");
        /**
         * *************Plurianuales***********************
         */
        query.append(" union    select  ");
        query.append("		   case when datPSP.lEsMaestro=1 then 'MAESTROS' ELSE  upper(coor.cDescCoortaCordinacion) END coordinacion  ");
        query.append("		   ,upper(catUE.D_DESCRIPCION) areaResp ");
        query.append("		   ,upper(contPlu.cNumProcedCNET)cNumProcedCNET ");
        query.append("		   ,upper(contPlu.cNoContratoCNET) cNoContratoCNET ");
        query.append("		   ,upper(isnull(datPSP.cDenominacionProyecto,''))cDenominacionProyecto ");
        query.append("		   ,upper(prov.cRazonSocial)cRazonSocial ");
        query.append("		   ,upper(replace(prov.cIdRFC,'-',''))rfc ");
        query.append("		   ,case when coor.c_centrotrab=37 then 'OFC' else 'PDF' end ofc_pdf ");
        query.append("		   ,case when datPSP.nCentroTrabajo=37 then upper(centTrab.d_centrotrab) else 'PDF '+upper(centTrab.d_centrotrab) end  centroTrabajo  ");
        query.append("		   ,case when termant.cIdContratoDefinitivo is not null    ");
        query.append("		   then case when convert(date,termant.fFechaTermino)<=convert(date,GETDATE()) then 'BAJA' ELSE 'ACTIVO' END   ");
        query.append("		   else case when convert(date,contPlu.fFin)<=convert(date,GETDATE()) then 'BAJA' ELSE 'ACTIVO' END end estatus   ");
        query.append("		   ,convert(varchar,(convert(date,contPlu.fInicio)),103) fechaInicioCont ");
        query.append("		   ,convert(varchar,(convert(date,contPlu.fFin)),103)fechaFinCont ");
        query.append("		   ,case when termant.fFechaTermino is null then '' else convert(varchar, termant.fFechaTermino,103)end fechaTerminacionAnticipada ");
        query.append("		   ,isnull(datPSP.mMontoMensual,0.00)mMontoMensual ");
        query.append("		   ,isnull(gastTraslado.totalMaximoGastosTraslado,0.00) montoTotalGastosTraslado ");
        query.append("		   ,maximo.totalMaximo MontoMaximoContrato ");
        query.append("		   ,isnull(pagos.totalPagado,0)-isnull(pagoGastosTraslado.totalPagadoGastosTraslado,0) montoPagadoHonorarios ");
        query.append("		   ,isnull(pagoGastosTraslado.totalPagadoGastosTraslado,0) montoPagadoGastosTraslado ");
        query.append("		   ,pagos.totalPagado montoTotalPagado ");
        query.append("		   ,'PRINCIPAL'sai ");
        query.append("		   ,case when termant.cIdContratoDefinitivo is not null THEN 'SI' ELSE 'NO' END tieneTermAnticipada  ");
        query.append("		   ,contPlu.cIdContratoDefinitivo ");
        query.append("		   ,isnull(tieneCompFiscal.tieneCompromisoFiscal,'NO')tieneCompromisoFiscal ");
        query.append("		   ,isnull(tieneCompIngresosProp.tieneCompromisoIngresosPropios,'NO')tieneCompromisoIngresosPropios ");
        query.append("		   from mContratoPlurianualidad as plu with(Nolock) ");
        query.append("		   inner join mPlurianualidadContrato as contPlu with(Nolock) ");
        query.append("		   on plu.cIdContratoDefinitivo=contPlu.cIdContratoDefinitivo ");
        query.append("		   inner join mDatosContratoPSP as datPSP with(Nolock) ");
        query.append("		   on datPSP.cIdcontratoDefinitivo=contPlu.cIdContratoDefinitivo ");
        query.append("		   inner join mCatalogoProveedor as prov with(Nolock) ");
        query.append("		   on prov.cIdRFC=contPlu.cIdRFC ");
        query.append("		   inner join( ");
        query.append("		   	select  ");
        query.append("		   	cIdContratoDefinitivo ");
        query.append("		   	,SUM(mMontoNetoLineaMaximo)totalMaximo ");
        query.append("		   	,SUM(mMontoNetoMinimo)totalMinimo ");
        query.append("		   	from mPartidasContratoPlurianual with(Nolock) ");
        query.append("		   	where cIdSubPartida='33104' ");
        query.append("		   	group by cIdContratoDefinitivo ");
        query.append("		   )maximo ");
        query.append("		   on maximo.cIdContratoDefinitivo=plu.cIdContratoDefinitivo ");
        query.append("		   left join( ");
        query.append("		   	select  ");
        query.append("		   	cIdContratoDefinitivo ");
        query.append("		   	,SUM(mMontoNetoLineaMaximo)totalMaximoGastosTraslado ");
        query.append("		   	from mPartidasContratoPlurianual with(Nolock) ");
        query.append("		   	where cIdSubPartida='33104' and nIdLineaConsolidado=2 ");
        query.append("		   	group by cIdContratoDefinitivo ");
        query.append("		   )gastTraslado on gastTraslado.cIdContratoDefinitivo=plu.cIdContratoDefinitivo ");
        query.append("		   inner join nomina_" + cEjercicio + ".dbo.nom_Unidad_Ejecutora coor with(Nolock) ");
        query.append("		   on coor.cUejecutora=datPSP.cAreaRequirente ");
        query.append("		   inner join tCatUnidadEjecutora as catUE with(Nolock) ");
        query.append("		   on catUE.cUnidadEjecutora=datPSP.cAreaResponsable ");
        query.append("		   inner join nomina_" + cEjercicio + ".dbo.nom_centrotrabajo centTrab with(Nolock) ");
        query.append("		   on centTrab.c_centrotrab=datPSP.nCentroTrabajo ");
        query.append("		   left join mContratoTerminacionAnticipada as termant with(Nolock)   ");
        query.append("		   on termant.cIdContratoDefinitivo=contPlu.cIdContratoDefinitivo ");
        query.append("		   left JOIN (		 ");
        query.append("		   	select	cFolioPAGODIVERSO folioContPed ");
        query.append("		   		,ISNULL([1],0.00)Enero ");
        query.append("		   		,ISNULL([2],0.00)Febrero ");
        query.append("		   		,ISNULL([3],0.00)Marzo ");
        query.append("		   		,ISNULL([4],0.00)Abril ");
        query.append("		   		,ISNULL([5],0.00)Mayo ");
        query.append("		   		,ISNULL([6],0.00)Junio ");
        query.append("		   		,ISNULL([7],0.00)Julio ");
        query.append("		   		,ISNULL([8],0.00)Agosto ");
        query.append("		   		,ISNULL([9],0.00)Septiembre ");
        query.append("		   		,ISNULL([10],0.00)Octubre ");
        query.append("		   		,ISNULL([11],0.00)Noviembre ");
        query.append("		   		,ISNULL([12],0.00)Diciembre ");
        query.append("		   		,ISNULL([1],0.00)+ISNULL([2],0.00)+ISNULL([3],0.00)+ISNULL([4],0.00)+ISNULL([5],0.00)+ISNULL([6],0.00) ");
        query.append("		   			+ISNULL([7],0.00)+ISNULL([8],0.00)+ISNULL([9],0.00)+ISNULL([10],0.00)+ISNULL([11],0.00)+ISNULL([12],0.00) totalPagado ");
        query.append("		   		from ");
        query.append("		   		(SELECT  ");
        query.append("		   			sum(isnull(d.mImporteMasIva,0))+(ISNULL(dismDevengado.totalDism,0))-(ISNULL(reintegro.totalReintegro,0))totalPagado ");
        query.append("		   			,e.cFolioPAGODIVERSO,month(pe.faplicacion) nMes ");
        query.append("		   			FROM dbo.tPAGODIVERSOEncabezado e WITH (NOLOCK)	 ");
        query.append("		   			INNER JOIN dbo.tPAGODIVERSODetalle d WITH (NOLOCK) ON e.nFolioPAGODIVERSO = d.nFolioPAGODIVERSO AND e.cDocumentoHaplicado='S'	 ");
        query.append("		   			inner join tPagadoEncabezado as pe with(Nolock) on pe.caNoContrarrecibo=e.caNoContrarrecibo and pe.cDocumentoHaplicado='S' ");
        query.append("		   			and pe.cTipoPago='PAGODIVERSO' ");
        query.append("		   			LEFT join ( ");
        query.append("		   					SELECT  ");
        query.append("		   					sum(mImporte) totalReintegro,cxp ");
        query.append("		   					FROM  tReintegroEncabezado as reintEnc with(Nolock) ");
        query.append("		   					inner join tReintegroDetalle as reintDet with(Nolock)  ");
        query.append("		   					on reintDet.nFolioReintegro=reintEnc.nFolioReintegro and reintEnc.cDocumentoHaplicado='S' ");
        query.append("		   					GROUP BY cxp ");
        query.append("		   			)reintegro ON reintegro.cxp=e.caNoContrarrecibo  ");
        query.append("		   			left join( ");
        query.append("		   				select  sum(mTotal)totalDism, caNoContrarreciboRef ");
        query.append("		   					from tDisminucionDevEncabezado enc (nolock) ");
        query.append("		   					inner join tDisminucionDevDetalle det (nolock) ");
        query.append("		   					on enc.nFolioDisminucionDev = det.nFolioDisminucionDev ");
        query.append("		   					where cTipoDoc='PAGODIVERSO' ");
        query.append("		   					group by caNoContrarreciboRef ");
        query.append("		   			)dismDevengado on dismDevengado.caNoContrarreciboRef=e.caNoContrarrecibo  ");
        query.append("		   			group by e.cFolioPAGODIVERSO,month(pe.faplicacion),reintegro.totalReintegro,dismDevengado.totalDism ");
        query.append("		   		)sub ");
        query.append("		   		PIVOT(  SUM(totalPagado)  FOR nMes IN ([1],[2],[3],[4],[5], [6], [7], [8], [9],[10],[11],[12])  )AS Pivot1 ");
        query.append("		   ) AS pagos ON pagos.folioContPed=contPlu.cIdContratoDefinitivo ");
        query.append("		   left join (select  ");
        query.append("		   	recep.cIdpedContDef ");
        query.append("		   	,sum(lineas.mMontoConIVA)-SUM(recep.mDescuentoConIVA) as totalPagadoGastosTraslado ");
        query.append("		   	from mRecepcionpMat as recep with(Nolock) ");
        query.append("		   	inner join mRecepcionpMatLineas as lineas with(Nolock) ");
        query.append("		   	on lineas.cIdpedContDef=recep.cIdpedContDef ");
        query.append("		   	and lineas.cIdRecepMat=recep.cIdRecepMat ");
        query.append("		   	and lineas.nIdConsecutivoRecepM=recep.nIdConsecutivoRecepM ");
        query.append("			inner join dbo.tPAGODIVERSOEncabezado e WITH (NOLOCK) ");
        query.append("			on e.cFolioPAGODIVERSO=recep.cIdpedContDef and recep.cIdRecepMat=e.cIdRecepMat and e.cDocumentoHaplicado='S' ");
        query.append("			inner join tPagadoEncabezado as pe with(Nolock) on pe.caNoContrarrecibo=e.caNoContrarrecibo and pe.cDocumentoHaplicado='S' ");
        query.append("			and pe.cTipoPago='PAGODIVERSO' ");
        query.append("		   	where recep.nIdEstadoRecepMat=3  ");
        query.append("		   	and lineas.nIdLineaConsolidado=2 ");
        query.append("		   	group by recep.cIdpedContDef ");
        query.append("		   )pagoGastosTraslado on pagoGastosTraslado.cIdpedContDef=contPlu.cIdContratoDefinitivo ");
        query.append("		   left join( ");
        query.append("		   select  ");
        query.append("		   	cIdContrato ");
        query.append("		   	,'SI'tieneCompromisoFiscal ");
        query.append("		   	from tCompromisoEncabezado enc with(Nolock) ");
        query.append("		   	inner join tCompromisoDetalle as det with(Nolock) ");
        query.append("		   	on enc.nFolioCompromiso=det.nFolioCompromiso ");
        query.append("		   	where enc.cDocumentoHaplicado='S' ");
        query.append("		   	and substring(det.EP,40,1)='1' ");
        query.append("		   	group by cIdContrato ");
        query.append("		   )tieneCompFiscal on tieneCompFiscal.cIdContrato=contPlu.cIdContratoDefinitivo ");
        query.append("		   left join( ");
        query.append("		   select  ");
        query.append("		   	cIdContrato ");
        query.append("		   	,'SI'tieneCompromisoIngresosPropios ");
        query.append("		   	from tCompromisoEncabezado enc with(Nolock) ");
        query.append("		   	inner join tCompromisoDetalle as det with(Nolock) ");
        query.append("		   	on enc.nFolioCompromiso=det.nFolioCompromiso ");
        query.append("		   	where enc.cDocumentoHaplicado='S' ");
        query.append("		   	and substring(det.EP,40,1)='4' ");
        query.append("		   	group by cIdContrato ");
        query.append("		   )tieneCompIngresosProp on tieneCompIngresosProp.cIdContrato=contPlu.cIdContratoDefinitivo ");
        return query;
    }

    private void queryCompensacionAmbientalPSP(StringBuilder query, String cEjercicio) {
        query.append(" union select    ");
        query.append(" 		 		 case when datPSP.lEsMaestro=1 then 'MAESTROS' ELSE  upper(req.cDescCoortaCordinacion) END coordinacion  ");
        query.append(" 		 		 ,upper(resp.cDescripcion) areaResponsable  ");
        query.append(" 		 		 ,upper(proced.cOficio) numProced  ");
        query.append(" 		 		 ,upper(cont.cNoContratoCNET) cNoContratoCNET ");
        query.append(" 				 ,upper(isnull(datPSP.cDenominacionProyecto,''))cDenominacionProyecto ");
        query.append(" 		 		 ,upper(prov.cRazonSocial) cRazonSocial ");
        query.append(" 		 		 ,upper(replace(cont.cIdRFC,'-',''))cIdRFC  ");
        query.append(" 		 		 ,case when datPSP.nCentroTrabajo=37 then 'OFC' else 'PDF' end ofc_pdf   ");
        query.append(" 		 		 ,case when datPSP.nCentroTrabajo=37 then upper(centTrab.d_centrotrab) else 'PDF '+upper(centTrab.d_centrotrab) end  centroTrabajo  ");
        query.append(" 		 		 ,case when termant.cIdContratoDefinitivo is not null    ");
        query.append(" 		 		 	then case when convert(date,termant.fFechaTermino)<=convert(date,GETDATE()) then 'BAJA' ELSE 'ACTIVO' END   ");
        query.append(" 		 		 else case when convert(date,cont.fFin)<=convert(date,GETDATE()) then 'BAJA' ELSE 'ACTIVO' END end estatus   ");
        query.append(" 		 		 ,CONVERT(VARCHAR,cont.fInicio,103)fechaInicio   ");
        query.append(" 		 		 ,CONVERT(VARCHAR,cont.fFin,103)fechaFin ");
        query.append(" 				 ,case when termant.fFechaTermino is null then '' else convert(varchar, termant.fFechaTermino,103)end fechaTerminacionAnticipada ");
        query.append(" 				 ,isnull(datPSP.mMontoMensual,0.00)mMontoMensual ");
        query.append(" 				 ,isnull(gastTraslado.totalMaximoContrato,0.00) montoTotalGastosTraslado ");
        query.append(" 		 		 ,case when termant.cIdContratoDefinitivo is not null then isnull(pagado.totalPagado,0) else maximo.totalMaximoContrato end montoMaximo ");
        query.append(" 				 ,isnull(pagado.totalPagado,0)-isnull(pagoGastosTraslado.totalPagadoGastosTraslado,0) montoPagadoHonorarios ");
        query.append(" 				 ,isnull(pagoGastosTraslado.totalPagadoGastosTraslado,0) montoPagadoGastosTraslado ");
        query.append(" 		 		 ,isnull(pagado.totalPagado,0) importePagado   ");
        query.append(" 		 		 ,'AMBIENTAL'tipoSAI  ");
        query.append(" 		 		 ,case when termant.cIdContratoDefinitivo is not null THEN 'SI' ELSE 'NO' END tieneTermAnticipada  ");
        query.append(" 		         ,cont.cIdContratoDefinitivo ");
        query.append(" 				 ,isnull(tieneCompFiscal.tieneCompromisoFiscal,'NO')tieneCompromisoFiscal ");
        query.append(" 				 ,isnull(tieneCompIngresosProp.tieneCompromisoIngresosPropios,'NO')tieneCompromisoIngresosPropios ");
        query.append(" 		 		 from compensacion_ambiental_" + cEjercicio.substring(2) + "..mContrato as cont with(Nolock)   ");
        query.append(" 		 		 inner join compensacion_ambiental_" + cEjercicio.substring(2) + "..mDatosContratoPSP as datPSP with(Nolock)   ");
        query.append(" 		 		 on datPSP.cIdcontratoDefinitivo=cont.cIdContratoDefinitivo   ");
        query.append(" 		 		 inner join compensacion_ambiental_" + cEjercicio.substring(2) + "..mCatalogoProveedor prov with(Nolock)   ");
        query.append(" 		 		 on prov.cIdRFC=cont.cIdRFC   ");
        query.append(" 		 		 inner join nomina_" + cEjercicio + ".dbo.nom_Unidad_Ejecutora resp with(Nolock)   ");
        query.append(" 		 		 on resp.cUejecutora=datPSP.cAreaResponsable   ");
        query.append(" 		 		 inner join nomina_" + cEjercicio + ".dbo.nom_Unidad_Ejecutora req with(Nolock)   ");
        query.append(" 		 		 on req.cUejecutora=datPSP.cAreaRequirente   ");
        query.append(" 		 		 inner join nomina_" + cEjercicio + ".dbo.nom_centrotrabajo centTrab with(Nolock)   ");
        query.append(" 		 		 on centTrab.c_centrotrab=datPSP.nCentroTrabajo   ");
        query.append(" 		 		 and centTrab.c_empresa=1  ");
        query.append(" 		 		 inner join compensacion_ambiental_" + cEjercicio.substring(2) + "..mProcedimiento as proced with(Nolock)  ");
        query.append(" 		 		 on proced.cIdProcedimiento=cont.cIdProcedimiento  ");
        query.append(" 		 		 inner join(select    ");
        query.append(" 		 		 	case when pa.lContratoAbierto=1 then round(SUM(isnull(pap.mMontoNetoLineaMax,ISNULL(pap.mMontoNetoLinea,0))),2)   ");
        query.append(" 		 		 	else round(SUM(isnull(pap.mMontoNetoMinimo,0)),2)   ");
        query.append(" 		 		 	end totalMaximoContrato   ");
        query.append(" 		 		 	,pap.cIdProcedimiento   ");
        query.append(" 		 		 	,pap.cIdRFC,isnull(pa.mMontoTotalPlurianual,0) mMontoTotalPedContPlurianual   ");
        query.append(" 		 		 	,SUM(isnull(pap.mMontoNetoMinimo,0)) as totalMinimoContrato   ");
        query.append(" 		 		 	,pap.nIdconsecutivoAdj   ");
        query.append(" 		 		 	from compensacion_ambiental_" + cEjercicio.substring(2) + "..mProcedimientoAdjudicacionPartidas as pap with(nolock)   ");
        query.append(" 		 		 	inner join compensacion_ambiental_" + cEjercicio.substring(2) + "..mProcedimientoAdjudicacion as pa with(nolock) on pap.cIdProcedimiento=pa.cIdProcedimiento   ");
        query.append(" 		 		 	and pa.cIdRFC=pap.cIdRFC and pa.nIdconsecutivoAdj=pap.nIdconsecutivoAdj   ");
        query.append(" 		 		 	group by pap.cIdProcedimiento,pap.cIdRFC,pa.lContratoAbierto,pa.mMontoTotalPlurianual,pap.nIdconsecutivoAdj   ");
        query.append(" 		 		 )maximo on cont.cIdProcedimiento=maximo.cIdProcedimiento and cont.cIdRFC=maximo.cIdRFC and maximo.nIdconsecutivoAdj=cont.nIdconsecutivoAdj ");
        query.append(" 				 left join( ");
        query.append(" 					select    ");
        query.append(" 						case when pa.lContratoAbierto=1 then round(SUM(isnull(pap.mMontoNetoLineaMax,ISNULL(pap.mMontoNetoLinea,0))),2)   ");
        query.append(" 						else round(SUM(isnull(pap.mMontoNetoMinimo,0)),2)   ");
        query.append(" 						end totalMaximoContrato   ");
        query.append(" 						,pap.cIdProcedimiento   ");
        query.append(" 						,pap.cIdRFC,isnull(pa.mMontoTotalPlurianual,0) mMontoTotalPedContPlurianual   ");
        query.append(" 						,SUM(isnull(pap.mMontoNetoMinimo,0)) as totalMinimoContrato   ");
        query.append(" 						,SUM(isnull(pap.mMontoNetoLinea,0)) as totalLineaContrato   ");
        query.append(" 						,pap.nIdconsecutivoAdj   ");
        query.append(" 						from compensacion_ambiental_" + cEjercicio.substring(2) + "..mProcedimientoAdjudicacionPartidas as pap with(nolock)   ");
        query.append(" 						inner join compensacion_ambiental_" + cEjercicio.substring(2) + "..mProcedimientoAdjudicacion as pa with(nolock) on pap.cIdProcedimiento=pa.cIdProcedimiento   ");
        query.append(" 						and pa.cIdRFC=pap.cIdRFC and pa.nIdconsecutivoAdj=pap.nIdconsecutivoAdj  ");
        query.append(" 						where pap.nIdLineaConsolidado=2 ");
        query.append(" 						group by pap.cIdProcedimiento,pap.cIdRFC,pa.lContratoAbierto,pa.mMontoTotalPlurianual,pap.nIdconsecutivoAdj ");
        query.append(" 				 )gastTraslado on gastTraslado.cIdProcedimiento=maximo.cIdProcedimiento ");
        query.append(" 				 and gastTraslado.cIdRFC=maximo.cIdRFC ");
        query.append(" 				 and gastTraslado.nIdconsecutivoAdj=maximo.nIdconsecutivoAdj ");
        query.append(" 		 		 left join compensacion_ambiental_" + cEjercicio.substring(2) + "..mContratoTerminacionAnticipada as termant with(Nolock)   ");
        query.append(" 		 		 on termant.cIdContratoDefinitivo=cont.cIdContratoDefinitivo   ");
        query.append(" 		 		 left join(   ");
        query.append(" 		 		 	select    ");
        query.append(" 		 		 	sum(totalPagado) totalPagado   ");
        query.append(" 		 		 	,cFolioPAGODIVERSO   ");
        query.append(" 		 		 	from (   ");
        query.append(" 		 		 		SELECT    ");
        query.append(" 		 		 			sum(isnull(d.mImporteMasIva,0))+(ISNULL(dismDevengado.totalDism,0))-(ISNULL(reintegro.totalReintegro,0))totalPagado   ");
        query.append(" 		 		 			,e.cFolioPAGODIVERSO,month(pe.faplicacion) nMes   ");
        query.append(" 		 		 			,e.caNoContrarrecibo    ");
        query.append(" 		 		 			FROM compensacion_ambiental_" + cEjercicio.substring(2) + ".dbo.tPAGODIVERSOEncabezado e WITH (NOLOCK)	   ");
        query.append(" 		 		 			INNER JOIN compensacion_ambiental_" + cEjercicio.substring(2) + ".dbo.tPAGODIVERSODetalle d WITH (NOLOCK) ON e.nFolioPAGODIVERSO = d.nFolioPAGODIVERSO AND e.cDocumentoHaplicado='S'	   ");
        query.append(" 		 		 			inner join compensacion_ambiental_" + cEjercicio.substring(2) + "..tPagadoEncabezado as pe with(Nolock) on pe.caNoContrarrecibo=e.caNoContrarrecibo and pe.cDocumentoHaplicado='S'   ");
        query.append(" 		 		 			and pe.cTipoPago='PAGODIVERSO'   ");
        query.append(" 		 		 			LEFT join (   ");
        query.append(" 		 		 					SELECT    ");
        query.append(" 		 		 					sum(mImporte) totalReintegro,cxp   ");
        query.append(" 		 		 					FROM  compensacion_ambiental_" + cEjercicio.substring(2) + "..tReintegroEncabezado as reintEnc with(Nolock)   ");
        query.append(" 		 		 					inner join compensacion_ambiental_" + cEjercicio.substring(2) + "..tReintegroDetalle as reintDet with(Nolock)    ");
        query.append(" 		 		 					on reintDet.nFolioReintegro=reintEnc.nFolioReintegro and reintEnc.cDocumentoHaplicado='S'   ");
        query.append(" 		 		 					GROUP BY cxp   ");
        query.append(" 		 		 			)reintegro ON reintegro.cxp=e.caNoContrarrecibo    ");
        query.append(" 		 		 			left join(   ");
        query.append(" 		 		 				select    ");
        query.append(" 		 		 				sum(mTotal)totalDism, caNoContrarreciboRef   ");
        query.append(" 		 		 					from compensacion_ambiental_" + cEjercicio.substring(2) + "..tDisminucionDevEncabezado enc (nolock)   ");
        query.append(" 		 		 					inner join compensacion_ambiental_" + cEjercicio.substring(2) + "..tDisminucionDevDetalle det (nolock)   ");
        query.append(" 		 		 					on enc.nFolioDisminucionDev = det.nFolioDisminucionDev   ");
        query.append(" 		 		 					where cTipoDoc='PAGODIVERSO'   ");
        query.append(" 		 		 					group by    ");
        query.append(" 		 		 					caNoContrarreciboRef   ");
        query.append(" 		 		 			)dismDevengado on dismDevengado.caNoContrarreciboRef=e.caNoContrarrecibo    ");
        query.append(" 		 		 			group by e.cFolioPAGODIVERSO,month(pe.faplicacion),reintegro.totalReintegro,dismDevengado.totalDism   ");
        query.append(" 		 		 			,e.caNoContrarrecibo   ");
        query.append(" 		 		 		)sub   ");
        query.append(" 		 		 	group by cFolioPAGODIVERSO   ");
        query.append(" 		 		 )pagado on pagado.cFolioPAGODIVERSO=cont.cIdContratoDefinitivo ");
        query.append(" 				 left join (select  ");
        query.append(" 					 recep.cIdpedContDef ");
        query.append(" 					 ,sum(lineas.mMontoConIVA)-SUM(recep.mDescuentoConIVA) as totalPagadoGastosTraslado ");
        query.append(" 					 from compensacion_ambiental_" + cEjercicio.substring(2) + "..mRecepcionpMat as recep with(Nolock) ");
        query.append(" 					 inner join compensacion_ambiental_" + cEjercicio.substring(2) + "..mRecepcionpMatLineas as lineas with(Nolock) ");
        query.append(" 					 on lineas.cIdpedContDef=recep.cIdpedContDef ");
        query.append(" 					 and lineas.cIdRecepMat=recep.cIdRecepMat ");
        query.append(" 					 and lineas.nIdConsecutivoRecepM=recep.nIdConsecutivoRecepM ");
        query.append("						 inner join compensacion_ambiental_" + cEjercicio.substring(2) + ".dbo.tPAGODIVERSOEncabezado e WITH (NOLOCK) ");
        query.append("						 on e.cFolioPAGODIVERSO=recep.cIdpedContDef and recep.cIdRecepMat=e.cIdRecepMat and e.cDocumentoHaplicado='S' ");
        query.append("						 inner join compensacion_ambiental_" + cEjercicio.substring(2) + "..tPagadoEncabezado as pe with(Nolock) on pe.caNoContrarrecibo=e.caNoContrarrecibo and pe.cDocumentoHaplicado='S' ");
        query.append("						 and pe.cTipoPago='PAGODIVERSO' ");
        query.append(" 					 where recep.nIdEstadoRecepMat=3  ");
        query.append(" 					 and lineas.nIdLineaConsolidado=2 ");
        query.append(" 					 group by recep.cIdpedContDef ");
        query.append(" 				 )pagoGastosTraslado on pagoGastosTraslado.cIdpedContDef=cont.cIdContratoDefinitivo ");
        query.append(" 				 left join( ");
        query.append(" 					select  ");
        query.append(" 					 cIdContrato ");
        query.append(" 					 ,'SI'tieneCompromisoFiscal ");
        query.append(" 					 from compensacion_ambiental_" + cEjercicio.substring(2) + "..tCompromisoEncabezado enc with(Nolock) ");
        query.append(" 					 inner join compensacion_ambiental_" + cEjercicio.substring(2) + "..tCompromisoDetalle as det with(Nolock) ");
        query.append(" 					 on enc.nFolioCompromiso=det.nFolioCompromiso ");
        query.append(" 					 where enc.cDocumentoHaplicado='S' ");
        query.append(" 					 and substring(det.EP,40,1)='1' ");
        query.append(" 					 group by cIdContrato ");
        query.append(" 				 )tieneCompFiscal on tieneCompFiscal.cIdContrato=cont.cIdContratoDefinitivo ");
        query.append(" 		 		 left join( ");
        query.append(" 					select  ");
        query.append(" 					 cIdContrato ");
        query.append(" 					 ,'SI'tieneCompromisoIngresosPropios ");
        query.append(" 					 from compensacion_ambiental_" + cEjercicio.substring(2) + "..tCompromisoEncabezado enc with(Nolock) ");
        query.append(" 					 inner join compensacion_ambiental_" + cEjercicio.substring(2) + "..tCompromisoDetalle as det with(Nolock) ");
        query.append(" 					 on enc.nFolioCompromiso=det.nFolioCompromiso ");
        query.append(" 					 where enc.cDocumentoHaplicado='S' ");
        query.append(" 					 and substring(det.EP,40,1)='4' ");
        query.append(" 					 group by cIdContrato ");
        query.append(" 				 )tieneCompIngresosProp on tieneCompIngresosProp.cIdContrato=cont.cIdContratoDefinitivo ");
        query.append(" 				 where cont.nIdEstado=4   ");
        query.append(" 		 		 and cont.lEsPSP=1  ");
    }

    public void generaReporteArrendamiento(Connection conn, String plantilla, String where, XSSFSheet firstSheet, XSSFCellStyle estiloTabla) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        ResultSetMetaData rsMetadata = null;
        XSSFRow rw = null;
        Calendar c = null;
        String Encabezado = null;
        String query = " select \r\n" + "    div.cIdContrato\r\n" + "    ,div.cConceptoContrato\r\n" + "    ,div.cIdRFC\r\n" + "    ,prov.cRazonSocial\r\n" + "    ,comp.partida\r\n" + "    ,sub.cSubpartida\r\n" + "    ,convert(date,div.fAdjudicacion)fAdjudicacion\r\n" + "    ,convert(date,div.fFirmaContrato)fFirmaContrato\r\n" + "    ,convert(date,div.fContratoIni)fInicio\r\n" + "    ,convert(date,div.fContratoFin)fFin\r\n" + "    ,isnull(div.mImporteBruto,0)mImporteSinIVA\r\n" + "    ,isnull(div.mImporteIVA,0)mImporteIVA\r\n" + "    ,isnull(div.mImporteTotal,0)mImporteTotal\r\n" + "    ,isnull(pagado.totalPagado,0)totalPagado\r\n" + "    ,case when nFolioAutSICOP is null then '' else\r\n" + "    ''''+isnull(nFolioAutSICOP,' ') end nFolioAutSICOP\r\n" + "from pContratoDiverso as div with(Nolock)\r\n" + "inner join mCatalogoProveedor as prov with(Nolock)\r\n" + "on replace(prov.cIdRFC,'-','')=div.cIdRFC\r\n" + "inner join(\r\n" + "        select \r\n" + "        cIdContrato\r\n" + "        ,SUBSTRING(ep,32,5)partida\r\n" + "        ,SUM(mImporte)compromiso\r\n" + "        ,enc.nFolioAutSICOP\r\n" + "        from tCompromisoEncabezado as enc with(Nolock)\r\n" + "        inner join tCompromisoDetalle as det with(Nolock)\r\n" + "        on enc.nFolioCompromiso=det.nFolioCompromiso\r\n" + "        where enc.cIdContrato like'CD%'\r\n" + "        group by cIdContrato\r\n" + "        ,SUBSTRING(ep,32,5)\r\n" + "        ,enc.nFolioAutSICOP\r\n" + "        having SUM(mImporte)>0\r\n" + ")comp on div.cIdContrato=comp.cIdContrato\r\n" + "inner join mcatalogoSubpartida sub with(Nolock)\r\n" + "on sub.cIdSubPartida=comp.partida\r\n" + "left join(\r\n" + "        SELECT \r\n" + "        sum(isnull(d.mImporteMasIva,0))-(ISNULL(reintegro.totalReintegro,0))totalPagado\r\n" + "        ,e.cFolioPAGODIVERSO\r\n" + "        FROM dbo.tPAGODIVERSOEncabezado e WITH (NOLOCK) \r\n" + "        INNER JOIN dbo.tPAGODIVERSODetalle d WITH (NOLOCK) ON e.nFolioPAGODIVERSO = d.nFolioPAGODIVERSO AND e.cDocumentoHaplicado='S'     \r\n" + "        inner join tPagadoEncabezado as pe with(Nolock) on pe.caNoContrarrecibo=e.caNoContrarrecibo and pe.cDocumentoHaplicado='S'\r\n" + "        and pe.cTipoPago='PAGODIVERSO'\r\n" + "        LEFT join (\r\n" + "                    SELECT \r\n" + "                    sum(mImporte) totalReintegro,cxp,month(reintEnc.faplicacion) nMes\r\n" + "                    FROM  tReintegroEncabezado as reintEnc with(Nolock)\r\n" + "                    inner join tReintegroDetalle as reintDet with(Nolock) \r\n" + "                    on reintDet.nFolioReintegro=reintEnc.nFolioReintegro and reintEnc.cDocumentoHaplicado='S'\r\n" + "                    GROUP BY cxp,month(reintEnc.faplicacion)\r\n" + "        )reintegro ON reintegro.cxp=e.caNoContrarrecibo and reintegro.nMes=month(e.faplicacion)\r\n" + "        where e.cFolioPAGODIVERSO like'CD%'\r\n" + "        group by e.cFolioPAGODIVERSO,reintegro.totalReintegro\r\n" + ")pagado on div.cIdContrato=pagado.cFolioPAGODIVERSO\r\n" + "where div.cIdContrato like'CD%' " + where;
        try {
            c = Calendar.getInstance();
            Encabezado = "A " + c.get(Calendar.DATE) + " de " + com.syc.adquisiciones.util.Util.getNameMonth(c.get(Calendar.MONTH) + 1) + " del " + c.get(Calendar.YEAR);
            rw = (firstSheet.getRow(3) == null ? firstSheet.createRow(3) : firstSheet.getRow(3));
            XSSFCell celdarsad = (rw.getCell(2) == null ? rw.createCell(2) : rw.getCell(2));
            celdarsad.setCellValue(Encabezado);
            log.debug(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            rsMetadata = rs.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            int renglonInicio = 6;
            int rows = 0;
            int cnt = 0;
            while (rs.next()) {
                rows = renglonInicio + cnt;
                rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                for (int i = 0; i < totalcolumnas; i++) {
                    //createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                    com.syc.gestion.util.Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
                cnt++;
            }
        } finally {
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rs, false);
        }
    }

    public void writeSheet1SemaforoSAC(Connection conn, XSSFSheet firstSheet, ProcedimientoSAC datos) throws Exception {
        StringBuilder query = null;
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        try {
            query = new StringBuilder();
            query.append("select substring(cProcedimientoTurnado, patindex('%-%' , cProcedimientoTurnado)+2, LEN(cProcedimientoTurnado)) enlace ");
            query.append(",cDenominacionProced,cat.cProcedimiento tipoContrato ");
            query.append(",nIdProcedimientoSAC consecutivoProceso ");
            query.append(",day(proced.fSolicitud)dia ");
            query.append(",cat.nDuracion duracionProceso ");
            query.append(",case when proced.fAtencion is null or year(proced.fAtencion)<=1900 then 0 ");
            query.append("	else day (proced.fAtencion )end diaAtencion ");
            query.append(",case when proced.fAtencion is null or year(proced.fAtencion)<=1900 then 0 ");
            query.append("	else DATEDIFF (DAY, proced.fSolicitud , proced.fAtencion )end +cat.nDuracion duracionReal ");
            query.append(",convert(varchar,proced.fSolicitud,103) fSolicitud ");
            query.append(",case when proced.fAtencion is null or year(proced.fAtencion)<=1900 then ''  ");
            query.append("else convert(varchar,proced.fAtencion,103) end fechaAtencion ");
            query.append(",round((procesoCont.nPorcentaje/100.00),2) nPorcentaje ");
            query.append("from mProcedimientoSAC proced with(Nolock) ");
            query.append("inner join mCatalogocategoriaprocedimientoSAC as cat with(Nolock) ");
            query.append("on proced.nTipoProcedimiento=cat.nIdcategoria ");
            query.append("inner join mCatalogoProcesosContratacionSAC procesoCont with(Nolock) ");
            query.append("on procesoCont.nIdProcesoContratacion=proced.nIdProcesoContratacion ");
            query.append("inner join mRelacionCatProcedimiento_ProcesoContratacion rel with(Nolock) ");
            query.append("on rel.nIdCategoria=proced.nTipoProcedimiento ");
            query.append("and rel.nIdProcesoContratacion=procesoCont.nIdProcesoContratacion ");
            query.append("where proced.nEstatus not in(4,5) and proced.fSolicitud >= convert(date,'" + datos.getcFechaInicio() + "') ");
            query.append("and proced.fSolicitud <= convert(date,'" + datos.getcFechaFin() + "') ");
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            XSSFRow rw = null;
            //Escribe el detalle
            int renglonInicio = 4;
            int rows = 0;
            int cnt = 0;
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                for (int i = 0; i < totalcolumnas; i++) {
                    createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                }
                cnt++;
            }
        } catch (Exception e) {
            log.error(e);
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
            query = null;
        }
    }

    public void writeSheet2SemaforoSAC(Connection conn, XSSFSheet secondSheet, ProcedimientoSAC datos) throws Exception {
        StringBuilder query = null;
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        try {
            query = new StringBuilder();
            query.append("select  ");
            query.append("cProcedimientoTurnado ");
            query.append(",COUNT(cProcedimientoTurnado) procedEnTramite ");
            query.append("from mProcedimientoSAC proced with(Nolock) ");
            query.append("inner join mCatalogocategoriaprocedimientoSAC as cat with(Nolock) ");
            query.append("on proced.nTipoProcedimiento=cat.nIdcategoria ");
            query.append("inner join mCatalogoProcesosContratacionSAC procesoCont with(Nolock) ");
            query.append("on procesoCont.nIdProcesoContratacion=proced.nIdProcesoContratacion ");
            query.append("inner join mRelacionCatProcedimiento_ProcesoContratacion rel with(Nolock) ");
            query.append("on rel.nIdCategoria=proced.nTipoProcedimiento ");
            query.append("and rel.nIdProcesoContratacion=procesoCont.nIdProcesoContratacion ");
            query.append("where nPorcentaje<100 and proced.nEstatus not in(4,5) ");
            query.append("and  proced.fSolicitud >= convert(date,'" + datos.getcFechaInicio() + "') ");
            query.append("and proced.fSolicitud <= convert(date,'" + datos.getcFechaFin() + "') ");
            query.append("group by cProcedimientoTurnado");
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            XSSFRow rw = null;
            //Escribe el detalle
            int renglonInicio = 1;
            int columnaInicio = 4;
            int rows = 0;
            int cnt = 0;
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (secondSheet.getRow(rows) == null ? secondSheet.createRow(rows) : secondSheet.getRow(rows));
                for (int i = 0; i < totalcolumnas; i++) {
                    createExcelCellRep(columnaInicio, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                    columnaInicio++;
                }
                cnt++;
                columnaInicio = 4;
            }
        } catch (Exception e) {
            log.error(e);
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
            query = null;
        }
    }

    public static void setValueCellExcel(Cell cell, ObjectCelda obj) throws Exception {
        if (obj.getTipoDato() == Types.BIGINT || obj.getTipoDato() == Types.BIT || obj.getTipoDato() == Types.INTEGER || obj.getTipoDato() == Types.SMALLINT || obj.getTipoDato() == Types.TINYINT) {
            cell.setCellValue(Integer.parseInt(obj.getValor()));
        } else if (obj.getTipoDato() == Types.DECIMAL || obj.getTipoDato() == Types.DOUBLE || obj.getTipoDato() == Types.FLOAT || obj.getTipoDato() == Types.NUMERIC || obj.getTipoDato() == Types.REAL) {
            cell.setCellValue(Double.parseDouble(obj.getValor()));
        } else
            cell.setCellValue(obj.getValor());
    }

    public static void setStyleCell(Cell cell, XSSFCellStyle estiloCell) throws Exception {
        cell.setCellStyle(estiloCell);
    }

    public static Cell createExcelCellRep(int index, Row fila, ResultSet rs, String cellName, int tipoDato, XSSFCellStyle estiloTabla) throws Exception {
        Cell cell = fila.createCell(index);
        if (tipoDato == Types.BIGINT || tipoDato == Types.BIT || tipoDato == Types.INTEGER || tipoDato == Types.SMALLINT || tipoDato == Types.TINYINT) {
            // Tipos de dato enteros
            int val = rs.getInt(cellName);
            cell.setCellValue(val);
            cell.setCellStyle(estiloTabla);
            return cell;
        } else if (tipoDato == Types.DECIMAL || tipoDato == Types.DOUBLE || tipoDato == Types.FLOAT || tipoDato == Types.NUMERIC || tipoDato == Types.REAL) {
            // Tipos de dato reales
            double val = rs.getDouble(cellName);
            cell.setCellValue(val);
            cell.setCellStyle(estiloTabla);
            return cell;
        } else if (tipoDato == Types.DATE || tipoDato == Types.TIME || tipoDato == Types.TIMESTAMP) {
            if (rs.getDate(cellName) != null) {
                // Tipo de dato fecha
                Date d = new Date(rs.getDate(cellName).getTime());
                cell.setCellValue(d);
                cell.setCellStyle(estiloTabla);
            }
            return cell;
        } else {
            String val = rs.getString(cellName);
            cell.setCellValue(val);
            cell.setCellStyle(estiloTabla);
            return cell;
        }
    }

    public static Cell createExcelCellRep(int index, Row fila, ResultSet rs, String cellName, int tipoDato) throws Exception {
        Cell cell = (fila.getCell(index) == null ? fila.createCell(index) : fila.getCell(index));
        if (tipoDato == Types.BIGINT || tipoDato == Types.BIT || tipoDato == Types.INTEGER || tipoDato == Types.SMALLINT || tipoDato == Types.TINYINT) {
            // Tipos de dato enteros
            //int val = rs.getInt( cellName );
            //cell.setCellValue(val);
            if (tipoDato == Types.BIGINT) {
                BigDecimal val1 = rs.getBigDecimal(cellName);
                cell.setCellValue("" + val1);
            } else {
                int val = rs.getInt(cellName);
                cell.setCellValue(val);
            }
            return cell;
        } else if (tipoDato == Types.DECIMAL || tipoDato == Types.DOUBLE || tipoDato == Types.FLOAT || tipoDato == Types.NUMERIC || tipoDato == Types.REAL) {
            // Tipos de dato reales
            double val = rs.getDouble(cellName);
            cell.setCellValue(val);
            return cell;
        } else if (tipoDato == Types.DATE || tipoDato == Types.TIME || tipoDato == Types.TIMESTAMP) {
            if (rs.getDate(cellName) != null) {
                // Tipo de dato fecha
                Date d = new Date(rs.getDate(cellName).getTime());
                cell.setCellValue(d);
            }
            return cell;
        } else {
            String val = rs.getString(cellName);
            cell.setCellValue(val);
            return cell;
        }
    }

    public void writeSheetReportePenas(Connection conn, XSSFSheet sheet, DatosPedidoContrato datos, String cEjercicioActivo, XSSFCellStyle estiloCell, boolean pagadas) throws Exception {
        StringBuilder query = null;
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        XSSFRow rw = null;
        String cadena = "Ejercicio Fiscal " + cEjercicioActivo;
        try {
            rw = (sheet.getRow(2) == null ? sheet.createRow(2) : sheet.getRow(2));
            XSSFCell celdarsad = (rw.getCell(0) == null ? rw.createCell(0) : rw.getCell(0));
            celdarsad.setCellValue(cadena);
            query = new StringBuilder();
            if (pagadas) {
                query.append("select *from reportePenasConvencionalesPagadas('" + datos.getcUnidadEjecutora() + "')");
            } else {
                query.append("select *from reportePenasConvencionales('" + datos.getcUnidadEjecutora() + "')");
            }
            log.info(query);
            ps = conn.prepareStatement(query.toString());
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            //Escribe el detalle
            int renglonInicio = 5;
            int rows = 0;
            int cnt = 0;
            XSSFTable table = sheet.getTables().get(0);
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (sheet.getRow(rows) == null ? sheet.createRow(rows) : sheet.getRow(rows));
                for (int i = 0; i < totalcolumnas; i++) {
                    if (i >= 8) {
                        createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloCell);
                    } else {
                        createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                    }
                }
                cnt++;
            }
            if (cnt > 0)
                updateSizeTable((cnt + renglonInicio - 1), table);
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
        }
    }

    public void writeSheetthirdReportePenas(Connection conn, XSSFSheet sheet, DatosPedidoContrato datos, String cEjercicioActivo, XSSFCellStyle estiloCell) throws Exception {
        StringBuilder query = null;
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        XSSFRow rw = null;
        String cadena = "Ejercicio Fiscal " + cEjercicioActivo;
        try {
            rw = (sheet.getRow(2) == null ? sheet.createRow(2) : sheet.getRow(2));
            XSSFCell celdarsad = (rw.getCell(0) == null ? rw.createCell(0) : rw.getCell(0));
            celdarsad.setCellValue(cadena);
            query = new StringBuilder();
            query.append("select *from v_mreportePenasConvencionales ");
            log.info(query);
            ps = conn.prepareStatement(query.toString());
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            //Escribe el detalle
            int renglonInicio = 5;
            int rows = 0;
            int cnt = 0;
            XSSFTable table = sheet.getTables().get(0);
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (sheet.getRow(rows) == null ? sheet.createRow(rows) : sheet.getRow(rows));
                for (int i = 0; i < totalcolumnas; i++) {
                    if (i >= 7 && i < 9) {
                        createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloCell);
                    } else {
                        createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                    }
                }
                cnt++;
            }
            if (cnt > 0)
                updateSizeTable((cnt + renglonInicio - 1), table);
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
        }
    }

    public void writeSheetReporteProvIncump(Connection conn, XSSFSheet firstSheet, DatosPedidoContrato datos) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        XSSFRow rw = null;
        try {
            query = "select *from v_mCatalogoProveedoresIncumplidos with(Nolock) ";
            log.info(query);
            ps = conn.prepareStatement(query);
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            //Escribe el detalle
            int renglonInicio = 5;
            int rows = 0;
            int cnt = 0;
            XSSFTable table = firstSheet.getTables().get(0);
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                for (int i = 0; i < totalcolumnas; i++) {
                    createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                }
                cnt++;
            }
            if (cnt > 0)
                updateSizeTable((cnt + renglonInicio - 1), table);
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
        }
    }

    public void writeSheetReporteRecisionContratos(Connection conn, XSSFSheet firstSheet, DatosPedidoContrato datos) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        XSSFRow rw = null;
        try {
            query = "select *from v_mContratosRescindidos with(Nolock) ";
            log.info(query);
            ps = conn.prepareStatement(query);
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            //Escribe el detalle
            int renglonInicio = 5;
            int rows = 0;
            int cnt = 0;
            XSSFTable table = firstSheet.getTables().get(0);
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                for (int i = 0; i < totalcolumnas; i++) {
                    createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                }
                cnt++;
            }
            if (cnt > 0)
                updateSizeTable((cnt + renglonInicio - 1), table);
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
        }
    }

    public void writeSheetReporteGarantia(Connection conn, XSSFSheet firstSheet, DatosPedidoContrato datos, XSSFCellStyle estiloCell) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        XSSFRow rw = null;
        try {
            query = "select *from v_mReporteGarantias with(Nolock) where 1=1 " + datos.getcWhere();
            log.info(query);
            ps = conn.prepareStatement(query);
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            //Escribe el detalle
            int renglonInicio = 5;
            int rows = 0;
            int cnt = 0;
            XSSFTable table = firstSheet.getTables().get(0);
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                for (int i = 0; i < totalcolumnas; i++) {
                    if (i == 14 || i == 15 || i == 21 || i == 27 || i == 33 || i == 43 || i == 52 || i == 61) {
                        createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloCell);
                    } else {
                        createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                    }
                }
                cnt++;
            }
            if (cnt > 0)
                updateSizeTable((cnt + renglonInicio - 1), table);
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
        }
    }

    public void writeSheetReporteContratosCompromiso(Connection conn, XSSFSheet firstSheet, DatosPedidoContrato datos, XSSFCellStyle estiloCell) throws Exception {
        StringBuilder query = null;
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        XSSFRow rw = null;
        String cadena = "Ejercicio Fiscal 2024";
        try {
            cadena = "Ejercicio Fiscal " + Util.obtieneEjercicioFiscalActivo(conn);
            rw = (firstSheet.getRow(2) == null ? firstSheet.createRow(2) : firstSheet.getRow(2));
            XSSFCell celdarsad = (rw.getCell(0) == null ? rw.createCell(0) : rw.getCell(0));
            celdarsad.setCellValue(cadena);
            query = new StringBuilder();
            query.append("SELECT  ");
            query.append("cont.cIdContrato Contrato_SAI ");
            query.append(",cont.cIdRFC RFC ");
            query.append(",cont.cRazonSocial Razon_Social ");
            query.append(",comp.ClaveSIAFF Clave_SIAFF ");
            query.append(",comp.ClaveInterna Clave_Interna ");
            query.append(",MontoEnero,MontoFebrero ");
            query.append(",MontoMarzo,MontoAbril ");
            query.append(",MontoMayo,MontoJunio ");
            query.append(",MontoJulio,MontoAgosto ");
            query.append(",MontoSeptiembre,MontoOctubre ");
            query.append(",MontoNoviembre,MontoDiciembre ");
            query.append(",MontoAnual ");
            query.append("FROM vCompromisoAutContratoEP  comp  ");
            query.append("inner join( ");
            query.append("	select  ");
            query.append("	div.cIdContrato,prov.cIdRFC,prov.cRazonSocial,div.cIdUnidadAdministrativa ");
            query.append("	from pContratoDiverso as div with(Nolock) ");
            query.append("	inner join mCatalogoProveedor as prov with(Nolock) ");
            query.append("	on replace(prov.cIdRFC,'-','')=div.cIdRFC ");
            query.append(")cont on cont.cIdContrato=comp.cidcontrato ");
            if (!"".equalsIgnoreCase(datos.getcUnidadEjecutora())) {
                query.append("WHERE (substring(comp.ClaveInterna,1,3) =? or cont.cIdUnidadAdministrativa =?) ");
            }
            query.append("order by comp.cidcontrato ");
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            if (!"".equalsIgnoreCase(datos.getcUnidadEjecutora())) {
                ps.setString(1, datos.getcUnidadEjecutora());
                ps.setString(2, datos.getcUnidadEjecutora());
            }
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            //Escribe el detalle
            int renglonInicio = 5;
            int rows = 0;
            int cnt = 0;
            XSSFTable table = firstSheet.getTables().get(0);
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                for (int i = 0; i < totalcolumnas; i++) {
                    if (i > 4) {
                        createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloCell);
                    } else {
                        createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                    }
                }
                cnt++;
            }
            if (cnt > 0)
                updateSizeTable((cnt + renglonInicio - 1), table);
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
            query = null;
        }
    }

    public void writeSheetReporteENSA(Connection conn, XSSFSheet firstSheet, DatosPedidoContrato datos) throws Exception {
        StringBuilder query = null;
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        XSSFRow rw = null;
        String cadena = "Ejercicio Fiscal 2025";
        try {
            cadena = "Ejercicio Fiscal " + Util.obtieneEjercicioFiscalActivo(conn);
            rw = (firstSheet.getRow(2) == null ? firstSheet.createRow(2) : firstSheet.getRow(2));
            XSSFCell celdarsad = (rw.getCell(0) == null ? rw.createCell(0) : rw.getCell(0));
            celdarsad.setCellValue(cadena);
            query = new StringBuilder();
            query.append("SELECT  *from v_mReporteENSA where 1=1 ");
            if (!"".equalsIgnoreCase(datos.getcUnidadEjecutora())) {
                query.append("and cFolio like '%" + datos.getcUnidadEjecutora() + "%' ");
            }
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            //Escribe el detalle
            int renglonInicio = 5;
            int rows = 0;
            int cnt = 0;
            XSSFTable table = firstSheet.getTables().get(0);
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                for (int i = 0; i < totalcolumnas; i++) {
                    createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                }
                cnt++;
            }
            if (cnt > 0)
                updateSizeTable((cnt + renglonInicio - 1), table);
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
            query = null;
        }
    }

    public void writeSheet1UCAP(Map<Integer, List<ObjectCelda>> contratos, XSSFSheet firstSheet, XSSFCellStyle estiloCelda, XSSFCellStyle estiloCeldaMoneda, XSSFCellStyle estiloCeldaFecha, XSSFCellStyle estiloCeldaPorcentaje) throws Exception {
        log.info("Inicia la escritura de la hoja 1 del reporte UCACP.");
        XSSFRow rw = null;
        XSSFTable table = null;
        List<ObjectCelda> listObj = null;
        Cell cell = null;
        try {
            //cadena="Ejercicio Fiscal "+Util.obtieneEjercicioFiscalActivo( conn );
            rw = (firstSheet.getRow(2) == null ? firstSheet.createRow(2) : firstSheet.getRow(2));
            if (contratos.isEmpty()) {
                log.info("El mapa está vacío.");
                return;
            }
            //Escribe el detalle
            int renglonInicio = 8;
            int cantRowsFinal = 5;
            int rows = 0;
            int cnt = 0;
            int j = 0;
            for (Map.Entry<Integer, List<ObjectCelda>> entry : contratos.entrySet()) {
                listObj = entry.getValue();
                rows = renglonInicio + cnt;
                if (j > 1) {
                    firstSheet.shiftRows(rows, rows + cantRowsFinal, 1);
                    rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                } else {
                    rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                }
                j++;
                for (int i = 0; i < listObj.size(); i++) {
                    cell = rw.createCell(i);
                    setValueCellExcel(cell, listObj.get(i));
                    if ((i > 7 && i < 10)) {
                        setStyleCell(cell, estiloCeldaFecha);
                    } else if ((i > 12 && i < 17) || i == 18 || i == 19 || i == 25) {
                        setStyleCell(cell, estiloCeldaMoneda);
                    } else if (i == 26) {
                        setStyleCell(cell, estiloCeldaPorcentaje);
                    } else
                        setStyleCell(cell, estiloCelda);
                }
                cnt++;
            }
            if (cnt > 0) {
                table = firstSheet.getTables().get(0);
                updateSizeTable((cnt + renglonInicio - 1), table);
            }
        } catch (Exception e) {
            throw (e);
        } finally {
            if (listObj != null) {
                listObj.clear();
            }
            table = null;
            listObj = null;
            rw = null;
            log.info("Finaliza la escritura de la hoja 1 del reporte UCACP.");
        }
    }

    public void writeSheet2UCAP(Map<Integer, List<ObjectCelda>> partContratos, XSSFSheet secondSheet, XSSFCellStyle estiloCelda, XSSFCellStyle estiloCeldaMoneda) throws Exception {
        log.info("Inicia la escritura de la hoja 2 del reporte UCACP.");
        XSSFRow rw = null;
        XSSFTable table = null;
        List<ObjectCelda> listObj = null;
        Cell cell = null;
        try {
            if (partContratos.isEmpty()) {
                log.info("El mapa está vacío.");
                return;
            }
            int rows = 0;
            int renglonInicio = 4;
            int cantRowsFinal = 2;
            int cnt = 0;
            int j = 0;
            for (Map.Entry<Integer, List<ObjectCelda>> entry : partContratos.entrySet()) {
                listObj = entry.getValue();
                rows = renglonInicio + cnt;
                if (j > 1) {
                    secondSheet.shiftRows(rows, rows + cantRowsFinal, 1);
                    rw = (secondSheet.getRow(rows) == null ? secondSheet.createRow(rows) : secondSheet.getRow(rows));
                } else {
                    rw = (secondSheet.getRow(rows) == null ? secondSheet.createRow(rows) : secondSheet.getRow(rows));
                }
                j++;
                for (int i = 0; i < listObj.size(); i++) {
                    cell = rw.createCell(i);
                    setValueCellExcel(cell, listObj.get(i));
                    if ((i == 4 || i == 5)) {
                        setStyleCell(cell, estiloCeldaMoneda);
                    } else
                        setStyleCell(cell, estiloCelda);
                }
                cnt++;
            }
            if (cnt > 0) {
                table = secondSheet.getTables().get(0);
                updateSizeTable((cnt + renglonInicio - 1), table);
            }
        } catch (Exception e) {
            throw (e);
        } finally {
            if (listObj != null)
                listObj.clear();
            listObj = null;
            table = null;
            rw = null;
            log.info("Finaliza la escritura de la hoja 2 del reporte UCACP.");
        }
    }

    public void writeSheet3UCAP(Map<Integer, List<ObjectCelda>> facturasContrato, XSSFSheet thirdSheet, XSSFCellStyle estiloCelda, XSSFCellStyle estiloCeldaMoneda) throws Exception {
        log.info("Inicia la escritura de la hoja 3 del reporte UCACP.");
        XSSFRow rw = null;
        XSSFTable table = null;
        List<ObjectCelda> listObj = null;
        Cell cell = null;
        try {
            if (facturasContrato.isEmpty()) {
                log.info("El mapa está vacío.");
                return;
            }
            //Escribe el detalle
            int renglonInicio = 4;
            int cantRowsFinal = 2;
            int rows = 0;
            int cnt = 0;
            int j = 0;
            for (Map.Entry<Integer, List<ObjectCelda>> entry : facturasContrato.entrySet()) {
                listObj = entry.getValue();
                rows = renglonInicio + cnt;
                if (j > 1) {
                    thirdSheet.shiftRows(rows, rows + cantRowsFinal, 1);
                    rw = (thirdSheet.getRow(rows) == null ? thirdSheet.createRow(rows) : thirdSheet.getRow(rows));
                } else {
                    rw = (thirdSheet.getRow(rows) == null ? thirdSheet.createRow(rows) : thirdSheet.getRow(rows));
                }
                for (int i = 0; i < listObj.size(); i++) {
                    cell = rw.createCell(i);
                    setValueCellExcel(cell, listObj.get(i));
                    if ((i > 3))
                        setStyleCell(cell, estiloCeldaMoneda);
                    else
                        setStyleCell(cell, estiloCelda);
                }
                cnt++;
            }
            if (cnt > 0) {
                table = thirdSheet.getTables().get(0);
                updateSizeTable((cnt + renglonInicio - 1), table);
            }
        } catch (Exception e) {
            throw (e);
        } finally {
            if (listObj != null)
                listObj.clear();
            listObj = null;
            table = null;
            rw = null;
            log.info("Finaliza la escritura de la hoja 3 del reporte UCACP.");
        }
    }

    public void writeSheetPreLayoutRequi(Connection conn, XSSFSheet firstSheet, Requisition requi, XSSFCellStyle estiloCeldaMoneda) throws Exception {
        StringBuilder query = null;
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        XSSFTable table = null;
        XSSFCell celdarsad = null;
        try {
            query = new StringBuilder();
            query.append(" select  ");
            query.append(" convert(int,sol.cEjercicio)cEjercicio,sol.cIdUnidadEjecutora,sol.cIdSolicitud ");
            query.append(" ,rtrim(ltrim(isnull(apartado.nIdClaveEgresos,'')))nIdClaveEgresos ");
            query.append(" ,rtrim(ltrim(isnull(apartado.ClaveInterna,'')))ClaveInterna ");
            query.append(" ,sol.nIdLineaSolicitud,convert(int,sol.cIdCABM)cIdCABM,convert(int,sol.cIdSubPartida)cIdSubPartida ");
            query.append(" ,sol.cDescripcion,sol.cDescripcionAdicional ");
            query.append(" ,isnull(apartado.mes01,0.00) mes01,isnull(apartado.mes02,0.00) mes02 ");
            query.append(" ,isnull(apartado.mes03,0.00) mes03,isnull(apartado.mes04,0.00) mes04 ");
            query.append(" ,isnull(apartado.mes05,0.00) mes05,isnull(apartado.mes06,0.00) mes06 ");
            query.append(" ,isnull(apartado.mes07,0.00) mes07,isnull(apartado.mes08,0.00) mes08 ");
            query.append(" ,isnull(apartado.mes09,0.00) mes09,isnull(apartado.mes10,0.00) mes10 ");
            query.append(" ,isnull(apartado.mes11,0.00) mes11,isnull(apartado.mes12,0.00) mes12 ");
            query.append(" ,mMontoNeto ");
            query.append(" ,'=SI.CONJUNTO(([@[Costo Total por Línea]]<SUMA(Tabla1[@[mes01]:[mes12]]))");
            query.append(",\"No puedes calendarizar mas presupuesto del consto total de la línea\",");
            query.append("([@[Costo Total por Línea]]>SUMA(Tabla1[@[mes01]:[mes12]]))");
            query.append(",\"Hace falta presupuesto para cubrir el costo total de la línea\"");
            query.append(",([@[Costo Total por Línea]]=SUMA(Tabla1[@[mes01]:[mes12]]))");
            query.append(",\"Monto Correcto\")' formula ");
            query.append(" from mSolicitudLineas sol with(Nolock) ");
            query.append(" left join mSolicitudLineasApartado apartado with(Nolock) ");
            query.append(" on apartado.cIdSolicitud=sol.cIdSolicitud ");
            query.append(" and apartado.nIdLineaSolicitud=sol.nIdLineaSolicitud  ");
            query.append(" where sol.cIdSolicitud=? ");
            query.append(" order by sol.nIdLineaSolicitud ");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, requi.getIdSolicitud());
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            XSSFRow rw = null;
            rw = (firstSheet.getRow(2) == null ? firstSheet.createRow(2) : firstSheet.getRow(2));
            celdarsad = (rw.getCell(0) == null ? rw.createCell(0) : rw.getCell(0));
            celdarsad.setCellValue("Ejercicio Fiscal " + requi.getEjercicio());
            //Escribe el detalle
            int renglonInicio = 5;
            int rows = 0;
            int cnt = 0;
            while (rst.next()) {
                rows = renglonInicio + cnt;
                rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                for (int i = 0; i < totalcolumnas; i++) {
                    if ((i > 9 && i < totalcolumnas - 1))
                        createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloCeldaMoneda);
                    else
                        createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                }
                cnt++;
            }
            if (cnt > 0) {
                table = firstSheet.getTables().get(0);
                updateSizeTable((cnt + renglonInicio - 1), table);
            }
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (query != null && query.length() > 0) {
                query.delete(0, query.length());
            }
            rsMetadata = null;
            rst = null;
            ps = null;
            table = null;
            query = null;
            celdarsad = null;
        }
    }

    public Map<Integer, List<ObjectCelda>> setResulsetToMap(ResultSet rs) throws Exception {
        log.info("Inicia el Set del resulset en un MAP ");
        Map<Integer, List<ObjectCelda>> objectMap = null;
        ResultSetMetaData rsmd = null;
        List<ObjectCelda> listObj = null;
        ObjectCelda obj = null;
        try {
            if (rs == null)
                throw new Exception("Error resulset is null");
            listObj = new ArrayList<ObjectCelda>();
            objectMap = new LinkedHashMap<Integer, List<ObjectCelda>>();
            rsmd = rs.getMetaData();
            int i = 1;
            while (rs.next()) {
                listObj = new ArrayList<ObjectCelda>();
                for (int j = 1; j < rsmd.getColumnCount() + 1; j++) {
                    obj = new ObjectCelda();
                    obj.setNameColumn(rsmd.getColumnName(j));
                    obj.setTipoDato(rsmd.getColumnType(j));
                    obj.setValor(StringUtils.trimToEmpty(rs.getString(rsmd.getColumnName(j))));
                    listObj.add(obj);
                    obj = null;
                }
                objectMap.put(i, listObj);
                i++;
            }
            return objectMap;
        } finally {
            rsmd = null;
            obj = null;
            listObj = null;
            log.info("Finaliza el Set del resulset en un MAP ");
        }
    }

    public Map<Integer, List<ObjectCelda>> resulsetContratos(Connection conn, DatosPedidoContrato datos) throws Exception {
        StringBuilder query = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        log.info("Generando resulset de la hoja 1, reporte UCACP");
        try {
            query = new StringBuilder();
            query.append("select ");
            query.append("	orden,nCodExpedienteCNET,cNumProcedimientoCNET,cDescripcionCorta ");
            query.append("	,cFundamentoLegal,caracter,contratoMarco,cConceptoContrato ");
            query.append("	,fFallo,fFormalizacion,cNoContratoCNET,cucop,partida ");
            query.append("	,subtotalContratoCerrado,totalContratoCerrado ");
            query.append("	,subtotalContratoMax,totalContratoMax,moneda ");
            query.append("	,fInicio,fFin,ConvenioMod,fechaFinConv,cRazonSocial,rfc ");
            query.append("	,case when  isnull(montoEjercido,0)>0 then  'ANEXO B' else '' end Factura ");
            query.append("	,isnull(montoEjercido,0) montoEjercido ");
            query.append("	,cast(round(case when isnull(montoEjercido,0)=0.00 then 0.00 ");
            query.append("	else (isnull(montoEjercido,0)/ (case when isPlurianual=1 or lContratoAbierto=1  then totalContratoMax else  ");
            query.append("	totalContratoCerrado end )) ");
            query.append("	end,4) as money) porcentajeAvance ");
            query.append(",contratoSAI from ");
            query.append("	[fn_mFormatoUCAP] (convert(date,?),convert(date,?)) fun ");
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, datos.getcFechaInicio());
            ps.setString(2, datos.getcFechaFin());
            rs = ps.executeQuery();
            log.info("Finaliza resulset de la hoja 1, reporte UCACP");
            return setResulsetToMap(rs);
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (query != null && query.length() > 0) {
                query.delete(0, query.length());
            }
            query = null;
            ps = null;
            rs = null;
        }
    }

    public Map<Integer, List<ObjectCelda>> resulsetPartidasContrato(Connection conn, DatosPedidoContrato datos) throws Exception {
        StringBuilder query = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        log.info("Generando resulset del listado de partidas de contratos, reporte UCACP");
        try {
            query = new StringBuilder();
            query.append(" 	select  ");
            query.append(" 			fun.orden,fun.cNoContratoCNET  ");
            query.append(" 			,convert(int,rtrim(ltrim(cucop.cIdCABM)))cIdCABM,convert(int,rtrim(ltrim(cucop.cIdSubPartida)))cIdSubPartida  ");
            query.append(" 			,(case when fun.isPlurianual=1 then cucop.mSubtotalPluri else   ");
            query.append(" 				case when fun.lContratoAbierto=1 then cucop.mSubtotalMaximo else cucop.mSubtotalMinimo end   ");
            query.append(" 			end)+isnull(conv.subtotal,0) subtotalCucop  ");
            query.append(" 			,(case when fun.isPlurianual=1 then cucop.mMontoNetoPluri else   ");
            query.append(" 				case when fun.lContratoAbierto=1 then cucop.mMontoNetoLineaMax else cucop.mMontoNetoMinimo end   ");
            query.append(" 			end)+isnull(conv.mMontoNeto,0) totalCucop  ");
            query.append(" 			,fun.moneda  ");
            query.append(" 			,fun.contratoSAI  ");
            query.append(" 			from [fn_mContratosConMasDeUnCucop]() masUnCucop  ");
            query.append(" 			inner join fn_mFormatoUCAP_Orden (convert(date,?),convert(date,?)) fun  ");
            query.append(" 			on masUnCucop.cIdContratoDefinitivo=fun.contratoSAI  ");
            query.append(" 			inner join(  ");
            query.append(" 			SELECT   ");
            query.append(" 				cont.cIdContratoDefinitivo  ");
            query.append(" 				,sl.cIdCABM,sl.cIdSubPartida  ");
            query.append(" 				,sum(cast(round((mMontoNetoMinimo/(1+(0.01*nPocentajeIVA))),2)as money)) as mSubtotalMinimo  ");
            query.append(" 				,sum(mMontoNetoMinimo)mMontoNetoMinimo  ");
            query.append(" 				,sum(cast(round((mMontoNetoLineaMax/(1+(0.01*nPocentajeIVA))),2)as money)) as mSubtotalMaximo  ");
            query.append(" 				,sum(mMontoNetoLineaMax)mMontoNetoLineaMax  ");
            query.append(" 				,sum(cast(round((mMontoNetoPluri/(1+(0.01*nPocentajeIVA))),2)as money))mSubtotalPluri  ");
            query.append(" 				,sum(mMontoNetoPluri)mMontoNetoPluri  ");
            query.append(" 			from mProcedimientoAdjudicacionPartidas part with(Nolock)  ");
            query.append(" 			inner join mContrato as cont with(Nolock)   ");
            query.append(" 			on cont.cIdProcedimiento=part.cIdProcedimiento  ");
            query.append(" 			and cont.cIdRFC=part.cIdRFC  ");
            query.append(" 			and cont.nIdconsecutivoAdj=part.nIdconsecutivoAdj  ");
            query.append(" 			inner join mConsolidadoSolicitud AS conS WITH(nOLOCK)   ");
            query.append(" 			on part.cIdConsolidado=conS.cIdConsolidado  ");
            query.append(" 			and part.nIdLineaConsolidado=conS.nIdLineaConsolidado  ");
            query.append(" 			inner join mSolicitudLineas as sl with(nolock) on conS.cEjercicio=sl.cEjercicio  ");
            query.append(" 			and conS.cIdTipoSolicitud=sl.cIdTipoSolicitud  ");
            query.append(" 			and conS.cIdUnidadEjecutoraSolicitud=sl.cIdUnidadEjecutora  ");
            query.append(" 			and conS.nIdConsecutivoSolicitud=sl.nIdConsecutivo  ");
            query.append(" 			and sl.cIdSolicitud=conS.cIdSolicitud   ");
            query.append(" 			and cons.nIdLineaSolicitud=sl.nIdLineaSolicitud  ");
            query.append(" 			group by cont.cIdContratoDefinitivo  ");
            query.append(" 			,sl.cIdCABM,sl.cIdSubPartida  ");
            query.append(" 			  ");
            query.append(" 			union  ");
            query.append(" 			select  ");
            query.append(" 				cIdContratoDefinitivo  ");
            query.append(" 				,cIdCABM,cIdSubPartida  ");
            query.append(" 				,sum(cast(round((mMontoNetoMinimo/(1+(0.01*iva.VALOR))),2)as money)) as mSubtotalMinimo  ");
            query.append(" 				,sum(mMontoNetoMinimo)mMontoNetoMinimo  ");
            query.append(" 				,sum(cast(round((mMontoNetoMaximo/(1+(0.01*iva.VALOR))),2)as money)) as mSubtotalMaximo  ");
            query.append(" 				,sum(mMontoNetoMaximo)mMontoNetoMax  ");
            query.append(" 				,sum(cast(round((mMontoNetoTotalPluri/(1+(0.01*iva.VALOR))),2)as money))mSubtotalPluri  ");
            query.append(" 				,sum(mMontoNetoTotalPluri)mMontoNetoPluri  ");
            query.append(" 				  ");
            query.append(" 			from mContratoCap4Partidas part with(Nolock)  ");
            query.append(" 			inner join mCatalogoTipoIVA as iva with(Nolock)  ");
            query.append(" 			on iva.IDIVA=part.nIdIVA ");
            query.append(" 			group by cIdContratoDefinitivo,cIdCABM,cIdSubPartida  ");
            query.append(" 		)cucop on cucop.cIdContratoDefinitivo=fun.contratoSAI  ");
            query.append(" 		left join( ");
            query.append(" 				select  ");
            query.append(" 					contMod.cIdContratoDefinitivo ");
            query.append(" 					,lin.cIdCABM,lin.cIdSubPartida ");
            query.append(" 					,cast(round(sum(part.mPrecioUnitario*part.nCantidad),2)as money) subtotal ");
            query.append(" 					,sum(part.mMontoNeto)mMontoNeto ");
            query.append(" 				from mContratoModificado as contMod with(Nolock) ");
            query.append(" 				inner join  pContratoDiversoConvenio convDiv with(Nolock)  ");
            query.append(" 				on convDiv.cIdContrato=contMod.cIdContratoDefinitivo ");
            query.append(" 				and convDiv.nConsecutivoModificacion=contMod.nConsecutivoModificacion ");
            query.append(" 				and contMod.cContratoDefinitivo=convDiv.cIdModificacion ");
            query.append(" 				inner join mContratoModificadoPartida as part with(Nolock) ");
            query.append(" 				on contMod.cIdContratoDefinitivo=part.cIdContratoDefinitivo ");
            query.append(" 				and contMod.nConsecutivoModificacion=part.nConsecutivoModificacion ");
            query.append(" 				inner join mSolicitudLineas lin with(Nolock) ");
            query.append(" 				on lin.nIdLineaSolicitud=part.cIdLineaSolicitud ");
            query.append(" 				and lin.cIdSolicitud=part.cIdSolicitud  ");
            query.append(" 				where contMod.nEstado=4 and contMod.isConvEjercicioAnt=0 ");
            query.append(" 				and convert(date,convDiv.fFirmaContrato)>=convert(date,?) ");
            query.append(" 				and convert(date,convDiv.fFirmaContrato)<=convert(date,?) ");
            query.append(" 				and tipoMod=0 ");
            query.append(" 				group by contMod.cIdContratoDefinitivo ");
            query.append(" 				,lin.cIdCABM,lin.cIdSubPartida ");
            query.append(" 			)conv on conv.cIdContratoDefinitivo=cucop.cIdContratoDefinitivo ");
            query.append(" 			and conv.cIdCABM=cucop.cIdCABM ");
            query.append(" 			and conv.cIdSubPartida=cucop.cIdSubPartida ");
            query.append(" 		order by fun.orden ");
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, datos.getcFechaInicio());
            ps.setString(2, datos.getcFechaFin());
            ps.setString(3, datos.getcFechaInicio());
            ps.setString(4, datos.getcFechaFin());
            rs = ps.executeQuery();
            log.info("Finaliza resulset del listado de partidas de contratos, reporte UCACP");
            return setResulsetToMap(rs);
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (query != null && query.length() > 0) {
                query.delete(0, query.length());
            }
            query = null;
            ps = null;
            rs = null;
        }
    }

    public Map<Integer, List<ObjectCelda>> resulsetFacturasContrato(Connection conn, DatosPedidoContrato datos) throws Exception {
        StringBuilder query = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        log.info("Generando resulset del listado de contratos, reporte UCACP");
        try {
            query = new StringBuilder();
            query.append("select ");
            query.append("	fun.orden,fun.cNoContratoCNET ");
            query.append("	,convert(int,rtrim(ltrim(fun.partida))) partida ");
            query.append(" ,case when len(fact.cfactura)<5 then contFact.cFactura else fact.cfactura end cfactura ");
            query.append("	,(fact.mImporteBruto - isnull(fact.mImporteDescuento,0) ) as subtotal ");
            query.append("	,fact.mimporteconiva+isnull(retencion.totalretencion,0) totalConIVA ");
            query.append("from [fn_mFormatoUCAP_Orden] (convert(date,?),convert(date,?)) fun ");
            query.append("inner join tPAGODIVERSOEncabezado enc with(Nolock)  ");
            query.append("on enc.cFolioPAGODIVERSO=fun.contratoSAI ");
            query.append("INNER JOIN dbo.tPagadoEncabezado pag WITH (NOLOCK) ON pag.caNoContrarrecibo = enc.caNoContrarrecibo ");
            query.append("and convert(date,pag.fAplicacion)<=convert(date,?) ");
            query.append("inner join tPagoFactura fact with(Nolock) on fact.cTipoPago='PAGODIVERSO'  ");
            query.append("and fact.nFolioPago=enc.nFolioPAGODIVERSO ");
            query.append("left outer join( ");
            query.append("	select uuid, sum(mImporteRetencion) as totalretencion  ");
            query.append("	,nFolioPago ");
            query.append("	from ");
            query.append("    tPagoFacturaRetencion with(nolock) ");
            query.append("	where cTipoPago='PAGODIVERSO' ");
            query.append("    group by uuid,nFolioPago ");
            query.append(" )retencion on retencion.UUID=fact.cfactura ");
            query.append(" and retencion.nFolioPago=fact.nFolioPago ");
            query.append(" left join tContratoFactura contFact with(Nolock) ");
            query.append(" on contFact.cIDContrato=fun.contratoSAI ");
            query.append("where  ");
            query.append("(fact.cEsNotaCredito='N' OR mImportePenalizacion = 0 )  and enc.cDocumentoHaplicado='S' and pag.cDocumentoHaplicado='S' ");
            query.append("order by fun.orden ");
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, datos.getcFechaInicio());
            ps.setString(2, datos.getcFechaFin());
            ps.setString(3, datos.getcFechaFin());
            rs = ps.executeQuery();
            log.info("Finaliza resulset del listado de contratos, reporte UCACP");
            return setResulsetToMap(rs);
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (query != null && query.length() > 0) {
                query.delete(0, query.length());
            }
            query = null;
            ps = null;
            rs = null;
        }
    }
}
