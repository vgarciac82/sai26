package com.syc.adquisiciones.businessLogic;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCacheDefinition;
import com.axtel.contratos.Requisition;
import com.syc.adquisiciones.core.DatosPedidoContrato;
import com.syc.adquisiciones.core.DatosReportesGRM;
import com.syc.adquisiciones.core.ObjectCelda;
import com.syc.adquisiciones.core.ProcedimientoSAC;
import com.syc.adquisiciones.manager.ReportesGRMManager;
import com.syc.cfdi.db.CloseObject;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportesGRMBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(ReportesGRMBusinessLogic.class);

    public File reporteContratosFisicos(DatosPedidoContrato datos) throws Exception {
        Connection conn = null;
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteContratos_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        File cFileExcelPlantilla = null;
        InputStream fs = null;
        InputStream fsArchivo = null;
        ReportesGRMManager manager = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        XSSFWorkbook workbook = null;
        File fsalida = null;
        try {
            conn = getConnection();
            manager = new ReportesGRMManager();
            cFileExcelPlantilla = new File(datos.getcNamePlantilla());
            fs = new FileInputStream(cFileExcelPlantilla);
            Util.copiaArchivo(fs, file_name);
            fsArchivo = new FileInputStream(cFileExcelPlantilla);
            workbook = new XSSFWorkbook(fsArchivo);
            XSSFSheet firstSheet = workbook.getSheetAt(0);
            XSSFCellStyle estiloFont = workbook.createCellStyle();
            XSSFCellStyle estiloFontRed = workbook.createCellStyle();
            XSSFFont fontRed = workbook.createFont();
            XSSFFont fontBlack = workbook.createFont();
            fontBlack.setColor(IndexedColors.BLACK.getIndex());
            fontRed.setColor(IndexedColors.RED.getIndex());
            estiloFont.setFont(fontBlack);
            estiloFontRed.setFont(fontRed);
            // Escribe en la hoja 1
            manager.writeSheet1ContratosFisicos(conn, firstSheet, datos, estiloFont, estiloFontRed);
            fsalida = new File(file_name);
            fos = new FileOutputStream(fsalida);
            bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos);
            bos.flush();
            conn.commit();
        } catch (SQLException e) {
            log.error(e.getMessage().toString());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn(e2.getMessage(), e2);
                }
            }
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fs != null) {
                fs.close();
            }
            if (workbook != null) {
                workbook.close();
            }
            fs = null;
            bos = null;
            workbook = null;
            fos = null;
            conn = null;
            cFileExcelPlantilla = null;
            fs = null;
            manager = null;
        }
        return fsalida;
    }

    public File generaFormato1120(DatosReportesGRM datos) throws Exception {
        Connection conn = null;
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteFormato1120_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        File cFileExcelPlantilla = null;
        InputStream fs = null;
        InputStream fsArchivo = null;
        ReportesGRMManager manager = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        XSSFWorkbook workbook = null;
        File fsalida = null;
        try {
            conn = getConnection();
            manager = new ReportesGRMManager();
            String cEjercicioActivo = com.syc.adquisiciones.util.Util.obtieneEjercicioFiscalActivo(conn);
            cFileExcelPlantilla = new File(datos.getcNamePlantilla());
            fs = new FileInputStream(cFileExcelPlantilla);
            Util.copiaArchivo(fs, file_name);
            fsArchivo = new FileInputStream(cFileExcelPlantilla);
            workbook = new XSSFWorkbook(fsArchivo);
            XSSFSheet firstSheet = workbook.getSheetAt(0);
            XSSFSheet secondSheet = workbook.getSheetAt(1);
            XSSFCellStyle estiloTabla = workbook.createCellStyle();
            estiloTabla.setBorderRight(BorderStyle.THIN);
            estiloTabla.setBorderBottom(BorderStyle.DOTTED);
            // Escribe en la hoja 1
            manager.writeSheet1Formato1120(conn, firstSheet, estiloTabla, cEjercicioActivo);
            // Escribe en la hoja 2
            manager.writeSheet2Formato1120(conn, secondSheet, estiloTabla);
            fsalida = new File(file_name);
            fos = new FileOutputStream(fsalida);
            bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos);
            bos.flush();
            conn.commit();
        } catch (SQLException e) {
            log.error(e.getMessage().toString());
            if (conn != null) {
                conn.rollback();
            }
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fs != null) {
                fs.close();
            }
            if (workbook != null) {
                workbook.close();
            }
            fs = null;
            bos = null;
            workbook = null;
            fos = null;
            conn = null;
            cFileExcelPlantilla = null;
            fs = null;
            manager = null;
        }
        return fsalida;
    }

    public File generaReporteCOCODI(DatosPedidoContrato datos) throws Exception {
        Connection conn = null;
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Reporte_Gestion_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        File cFileExcelPlantilla = null;
        InputStream fs = null;
        InputStream fsArchivo = null;
        ReportesGRMManager manager = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        XSSFWorkbook workbook = null;
        File fsalida = null;
        XSSFCellStyle estiloCell = null;
        XSSFCellStyle estiloCellFecha = null;
        XSSFDataFormat format = null;
        String[] arreglofechaIni = null;
        try {
            conn = getConnection();
            manager = new ReportesGRMManager();
            String cEjercicioActivo = com.syc.adquisiciones.util.Util.obtieneEjercicioFiscalActivo(conn);
            file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Reporte_Gestion_" + cEjercicioActivo + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
            cFileExcelPlantilla = new File(datos.getcNamePlantilla());
            fs = new FileInputStream(cFileExcelPlantilla);
            Util.copiaArchivo(fs, file_name);
            fsArchivo = new FileInputStream(cFileExcelPlantilla);
            //Está validación es porque cargaron contratos en 2023 con fecha de formalización de diciembre 2022 "Contratos consolidados de hacienda art 25 laassp"
            arreglofechaIni = datos.getcFechaInicio().split("/");
            if ("2023".equalsIgnoreCase(arreglofechaIni[2]) && "01".equalsIgnoreCase(arreglofechaIni[1]) && ("2023".equalsIgnoreCase(cEjercicioActivo))) {
                datos.setcFechaInicio("29/11/2022");
            }
            workbook = new XSSFWorkbook(fsArchivo);
            estiloCell = workbook.createCellStyle();
            estiloCellFecha = workbook.createCellStyle();
            format = workbook.createDataFormat();
            estiloCell.setDataFormat(format.getFormat("#,##0.00"));
            estiloCellFecha.setDataFormat(format.getFormat("dd/mm/yyyy"));
            XSSFSheet firstSheet = workbook.getSheetAt(0);
            XSSFSheet secondSheet = workbook.getSheetAt(1);
            XSSFSheet fourthSheet = workbook.getSheetAt(3);
            XSSFSheet fifthSheet = workbook.getSheetAt(4);
            XSSFSheet sixthSheet = workbook.getSheetAt(5);
            // Escribe en la hoja 1
            manager.writeSheet1COCODI(conn, firstSheet, cEjercicioActivo, datos, (float) 1.0);
            // Escribe en la hoja 2
            manager.writeSheet1COCODI(conn, secondSheet, cEjercicioActivo, datos, (float) 1000.0);
            // Escribe en la hoja 4
            manager.writeSheet3COCODI(conn, fourthSheet, datos, estiloCell, estiloCellFecha);
            // Escribe en la hoja 5 convenios
            manager.writeSheet4COCODI(conn, fifthSheet, datos, estiloCell);
            // Escribe en la hoja 6 pagos directos
            manager.writeSheet5COCODI(conn, sixthSheet, datos, estiloCell, estiloCellFecha);
            fsalida = new File(file_name);
            fos = new FileOutputStream(fsalida);
            bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos);
            bos.flush();
            conn.commit();
        } catch (SQLException e) {
            log.error(e.getMessage().toString());
            if (conn != null) {
                conn.rollback();
            }
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fs != null) {
                fs.close();
            }
            if (workbook != null) {
                workbook.close();
            }
            fs = null;
            bos = null;
            workbook = null;
            fos = null;
            conn = null;
            cFileExcelPlantilla = null;
            fs = null;
            manager = null;
            estiloCell = null;
            estiloCellFecha = null;
            format = null;
            arreglofechaIni = null;
        }
        return fsalida;
    }

    public File generaReportePresionGasto(DatosPedidoContrato datos) throws Exception {
        Connection conn = null;
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReportePresionGasto_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        File cFileExcelPlantilla = null;
        InputStream fs = null;
        InputStream fsArchivo = null;
        ReportesGRMManager manager = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        XSSFWorkbook workbook = null;
        File fsalida = null;
        try {
            Date d = new Date();
            Calendar c = new GregorianCalendar();
            c.setTime(d);
            conn = getConnection();
            manager = new ReportesGRMManager();
            cFileExcelPlantilla = new File(datos.getcNamePlantilla());
            fs = new FileInputStream(cFileExcelPlantilla);
            Util.copiaArchivo(fs, file_name);
            fsArchivo = new FileInputStream(cFileExcelPlantilla);
            workbook = new XSSFWorkbook(fsArchivo);
            XSSFSheet firstSheet = workbook.getSheetAt(0);
            String cEjercicioActivo = com.syc.adquisiciones.util.Util.obtieneEjercicioFiscalActivo(conn);
            datos.setcNameMes(com.syc.adquisiciones.util.Util.getNameMonth(c.get(Calendar.MONTH) + 1));
            // Escribe en la hoja 1
            manager.writeSheet1PresionGasto(conn, firstSheet, datos, cEjercicioActivo);
            fsalida = new File(file_name);
            fos = new FileOutputStream(fsalida);
            bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos);
            bos.flush();
            conn.commit();
        } catch (SQLException e) {
            log.error(e.getMessage().toString());
            if (conn != null) {
                conn.rollback();
            }
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fs != null) {
                fs.close();
            }
            if (fsArchivo != null) {
                fsArchivo.close();
            }
            if (workbook != null) {
                workbook.close();
            }
            fs = null;
            bos = null;
            workbook = null;
            fos = null;
            conn = null;
            cFileExcelPlantilla = null;
            fs = null;
            manager = null;
        }
        return fsalida;
    }

    public File generaReporte7030(DatosPedidoContrato datos) throws Exception {
        Connection conn = null;
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Reporte7030.xlsx";
        String query = "";
        String[] arreglofechaIni = null;
        String cEjercicioActivo = null;
        File cFileExcelPlantilla = null;
        InputStream fs = null;
        InputStream fsArchivo = null;
        ReportesGRMManager manager = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        XSSFWorkbook workbook = null;
        File fsalida = null;
        XSSFSheet firstSheet = null;
        XSSFSheet contratoSheet = null;
        XSSFSheet pedidoSheet = null;
        XSSFSheet pagosDirectosSheet = null;
        try {
            conn = getConnection();
            cEjercicioActivo = com.syc.adquisiciones.util.Util.obtieneEjercicioFiscalActivo(conn);
            //Está validación es porque cargaron contratos en 2023 con fecha de formalización de diciembre 2022 "Contratos consolidados de hacienda"
            arreglofechaIni = datos.getcFechaInicio().split("/");
            if ("2023".equalsIgnoreCase(arreglofechaIni[2]) && "01".equalsIgnoreCase(arreglofechaIni[1]) && ("2023".equalsIgnoreCase(cEjercicioActivo))) {
                datos.setcFechaInicio("29/11/2022");
            }
            if (datos.getnTipoIngreso() == 1) {
                file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Reporte7030_Fiscal.xlsx";
                query = "SELECT *FROM fn_mReporte7030Fiscal('" + datos.getcFechaInicio() + "','" + datos.getcFechaFin() + "'," + datos.getnTipoIngreso() + ") order by capitulo";
            } else if (datos.getnTipoIngreso() == 4) {
                file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Reporte7030_IP.xlsx";
                query = "SELECT *FROM fn_mReporte7030IP('" + datos.getcFechaInicio() + "','" + datos.getcFechaFin() + "'," + datos.getnTipoIngreso() + ") order by capitulo";
            } else if (datos.getnTipoIngreso() == 0) {
                file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Reporte7030_Fiscal_IP.xlsx";
                query = "SELECT *FROM fn_mReporte7030('" + datos.getcFechaInicio() + "','" + datos.getcFechaFin() + "') order by capitulo";
            } else {
                throw new Exception("Tipo de ingreso deconocido");
            }
            manager = new ReportesGRMManager();
            cFileExcelPlantilla = new File(datos.getcNamePlantilla());
            fs = new FileInputStream(cFileExcelPlantilla);
            Util.copiaArchivo(fs, file_name);
            fsArchivo = new FileInputStream(cFileExcelPlantilla);
            workbook = new XSSFWorkbook(fsArchivo);
            firstSheet = workbook.getSheetAt(0);
            contratoSheet = workbook.getSheetAt(2);
            pedidoSheet = workbook.getSheetAt(3);
            pagosDirectosSheet = workbook.getSheetAt(4);
            // Escribe en la hoja 1
            manager.writeSheet1Formato7030(conn, firstSheet, datos, cEjercicioActivo, query);
            //Escribe el detalle de contratos
            manager.writeSheetWorboock(conn, contratoSheet, datos, queryContratos7030(datos), 1);
            //Escribe el detalle de pedidos
            manager.writeSheetWorboock(conn, pedidoSheet, datos, queryPedidos7030(datos), 1);
            //Escribe el detalle de pagos directos
            manager.writeSheetWorboock(conn, pagosDirectosSheet, datos, queryPagosDirectos7030(datos), 1);
            HSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
            fsalida = new File(file_name);
            fos = new FileOutputStream(fsalida);
            bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos);
            bos.flush();
            conn.commit();
        } catch (SQLException e) {
            log.error(e.getMessage().toString());
            if (conn != null) {
                conn.rollback();
            }
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fs != null) {
                fs.close();
            }
            if (fsArchivo != null) {
                fsArchivo.close();
            }
            if (workbook != null) {
                workbook.close();
            }
            fs = null;
            bos = null;
            workbook = null;
            fos = null;
            conn = null;
            cFileExcelPlantilla = null;
            fs = null;
            manager = null;
            arreglofechaIni = null;
            cEjercicioActivo = null;
            firstSheet = null;
        }
        return fsalida;
    }

    private StringBuilder queryContratos7030(DatosPedidoContrato dato) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("	select  ");
        query.append("	cIdContratoDefinitivo,cFundamentoLegal,convert(int,rtrim(ltrim(cIdSubPartida)))cIdSubPartida ");
        query.append("	,cNoContratoCNET,convert(varchar,(convert(date,fInicio)),103)fInicio ");
        query.append("	,convert(varchar,(convert(date,fFin)),103) fFin ");
        query.append("	,convert(varchar,(convert(date,fFormalizacion)),103) fFormalizacion,cIdRFC ");
        query.append("	,cRazonSocial ,cConceptoContrato,nCodContratoCNET ");
        query.append("	,nCodExpedienteCNET ");
        query.append("	,sum(montoTotalContratado)montoTotalContratado ");
        query.append("from ");
        query.append("	(select cont.cIdContratoDefinitivo,adj.nIdFundamentoLeg ");
        query.append("		,adjPart.nIdLineaConsolidado,consLineas.cIdCABM ");
        query.append("		,cucop.cIdSubPartida,cucop.cIdCapitulo ");
        query.append("		,cont.cNoContratoCNET,cont.fInicio,cont.fFin ");
        query.append("		,cont.cIdRFC,cont.cConceptoContrato ");
        query.append("		,cont.fFormalizacion,cont.nCodContratoCNET ");
        query.append("		,cont.nCodExpedienteCNET,cont.cOficioDG,cont.cFolioMASCP ");
        query.append("		,adjPart.cIdProcedimiento,adjPart.mMontoNetoLinea ");
        query.append("		,adjPart.mMontoNetoMinimo,adjPart.mMontoNetoLineaMax ");
        query.append("		,adjPart.mMontoNetoPluri,flg.cFundamentoLegal,prov.cRazonSocial ");
        query.append("		,case when proced.isPlurianual=1 then adjPart.mMontoNetoPluri  ");
        query.append("			else case when adj.lContratoAbierto=1 then adjPart.mMontoNetoLineaMax else adjPart.mMontoNetoMinimo end end montoNetoInicialContratado ");
        query.append("		,isnull(conv.mMontoNetoLinea,0) montoConvenio ");
        query.append("		,(case when proced.isPlurianual=1 then adjPart.mMontoNetoPluri  ");
        query.append("			else case when adj.lContratoAbierto=1 then adjPart.mMontoNetoLineaMax else adjPart.mMontoNetoMinimo end end)+isnull(conv.mMontoNetoLinea,0) montoTotalContratado ");
        query.append("	from mContrato cont with(Nolock) ");
        query.append("	inner join mProcedimientoAdjudicacionPartidas as adjPart with(Nolock) ");
        query.append("	on adjPart.cIdProcedimiento=cont.cIdProcedimiento ");
        query.append("	and adjPart.nIdconsecutivoAdj=cont.nIdconsecutivoAdj ");
        query.append("	and adjPart.cIdRFC=cont.cIdRFC ");
        query.append("	inner join mProcedimientoAdjudicacion as adj with(Nolock) ");
        query.append("	on adj.cIdProcedimiento=adjPart.cIdProcedimiento ");
        query.append("	and adj.cIdRFC=adjPart.cIdRFC ");
        query.append("	and adj.nIdconsecutivoAdj=adjPart.nIdconsecutivoAdj ");
        query.append("	inner join mProcedimiento as proced with(Nolock) ");
        query.append("	on proced.cIdProcedimiento=cont.cIdProcedimiento ");
        query.append("	inner join mConsolidadoLineas as consLineas with(Nolock) ");
        query.append("	on consLineas.cIdConsolidado=adjPart.cIdConsolidado ");
        query.append("	and consLineas.nIdLineaConsolidado=adjPart.nIdLineaConsolidado ");
        query.append("	inner join mCatalogoCABM as cucop with(Nolock) ");
        query.append("	on cucop.cIdCABM=consLineas.cIdCABM ");
        if (dato.getnTipoIngreso() != 0) {
            query.append("	inner join ( ");
            query.append("	select  ");
            query.append("	consol.cIdConsolidado ");
            query.append("	,consolSol.nIdLineaConsolidado,consolSol.cIdSolicitud ");
            query.append("	,(select dbo.fn_mTipoRescursoRequi(consolSol.cIdSolicitud))tipoIng ");
            query.append("	from mConsolidado as consol with(Nolock) ");
            query.append("	inner join mConsolidadoSolicitud as consolSol with(Nolock) ");
            query.append("	on consol.cIdConsolidado=consolSol.cIdConsolidado ");
            query.append("	inner join mConsolidadoLineas as consolLine with(Nolock) ");
            query.append("	on consolLine.cIdConsolidado=consol.cIdConsolidado ");
            query.append("	and consolLine.nIdLineaConsolidado=consolSol.nIdLineaConsolidado ");
            query.append("	where consol.nIdEstado=2 ");
            query.append("	group by consol.cIdConsolidado ");
            query.append("	,consolSol.nIdLineaConsolidado,consolSol.cIdSolicitud ");
            query.append(")requi on requi.cIdConsolidado=proced.cIdConsolidado ");
            query.append(" and requi.nIdLineaConsolidado=adjPart.nIdLineaConsolidado ");
            query.append(" and requi.tipoIng= " + dato.getnTipoIngreso());
        }
        query.append("	inner join mCatalogoFundamentoLegal as flg with(Nolock) ");
        query.append("	on flg.nIdCategoria=proced.nIdCategoria ");
        query.append("	and flg.nIdFundamentoLeg=adj.nIdFundamentoLeg ");
        query.append("	inner join mCatalogoProveedor as prov with(Nolock) ");
        query.append("	on prov.cIdRFC=cont.cIdRFC ");
        query.append("	left join ( ");
        query.append("		select  ");
        query.append("			contMod.cIdContratoDefinitivo cIdContratoDefinitivo ");
        query.append("			,contModPart.nIdLineaConsolidado,sum(contModPart.mMontoNeto)mMontoNetoLinea ");
        query.append("		from mContratoModificado as contMod with(nolock) ");
        query.append("		inner join mContratoModificadoPartida as contModPart with(nolock) ");
        query.append("		on contMod.cIdContratoDefinitivo=contModPart.cIdContratoDefinitivo ");
        query.append("		and contMod.nConsecutivoModificacion=contModPart.nConsecutivoModificacion ");
        query.append("		where contMod.nEstado=4 and contMod.isConvEjercicioAnt=0 and tipoMod=0 ");
        query.append("		group by contMod.cIdContratoDefinitivo ");
        query.append("		,contModPart.nIdLineaConsolidado ");
        query.append("	)conv on conv.cIdContratoDefinitivo=cont.cIdContratoDefinitivo ");
        query.append("	and conv.nIdLineaConsolidado=adjPart.nIdLineaConsolidado ");
        query.append("	where cont.nIdEstado=4  ");
        query.append("	and convert(date,cont.fFormalizacion)>=convert(date,'" + dato.getcFechaInicio() + "') ");
        query.append("	and convert(date,cont.fFormalizacion)<=convert(date,'" + dato.getcFechaFin() + "') ");
        query.append("	and SUBSTRING(cucop.cIdSubPartida,1,3) not  in(select cPartidaExcepcion from mCatalogoPartidasExcepcion7030 with(Nolock)) ");
        query.append(")sub ");
        query.append("group by  ");
        query.append("cIdContratoDefinitivo,nIdFundamentoLeg,cIdSubPartida,cNoContratoCNET ");
        query.append(",fInicio,fFin,fFormalizacion,cIdRFC,cConceptoContrato,nCodContratoCNET ");
        query.append(",nCodExpedienteCNET,cFundamentoLegal,cRazonSocial ");
        return query;
    }

    private StringBuilder queryPedidos7030(DatosPedidoContrato dato) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("	select  ");
        query.append("	cIdContratoDefinitivo,cFundamentoLegal,convert(int,rtrim(ltrim(cIdSubPartida)))cIdSubPartida ");
        query.append("	,cNoContratoCNET,convert(varchar,(convert(date,fInicio)),103)fInicio ");
        query.append("	,convert(varchar,(convert(date,fFin)),103) fFin ");
        query.append("	,convert(varchar,(convert(date,fFormalizacion)),103) fFormalizacion,cIdRFC ");
        query.append("	,cRazonSocial ,cConceptoContrato,nCodContratoCNET ");
        query.append("	,nCodExpedienteCNET ");
        query.append("	,sum(montoTotalContratado)montoTotalContratado ");
        query.append("from ");
        query.append("	(select cont.cIdContratoDefinitivo,adj.nIdFundamentoLeg ");
        query.append("		,adjPart.nIdLineaConsolidado,consLineas.cIdCABM ");
        query.append("		,cucop.cIdSubPartida,cucop.cIdCapitulo ");
        query.append("		,cont.cNoContratoCNET,cont.fInicio,cont.fFin ");
        query.append("		,cont.cIdRFC,cont.cConceptoContrato ");
        query.append("		,cont.fFormalizacion,cont.nCodContratoCNET ");
        query.append("		,cont.nCodExpedienteCNET,cont.cOficioDG,cont.cFolioMASCP ");
        query.append("		,adjPart.cIdProcedimiento,adjPart.mMontoNetoLinea ");
        query.append("		,adjPart.mMontoNetoMinimo,adjPart.mMontoNetoLineaMax ");
        query.append("		,adjPart.mMontoNetoPluri,flg.cFundamentoLegal,prov.cRazonSocial ");
        query.append("		,case when proced.isPlurianual=1 then adjPart.mMontoNetoPluri  ");
        query.append("			else case when adj.lContratoAbierto=1 then adjPart.mMontoNetoLineaMax else adjPart.mMontoNetoMinimo end end montoNetoInicialContratado ");
        query.append("		,isnull(conv.mMontoNetoLinea,0) montoConvenio ");
        query.append("		,(case when proced.isPlurianual=1 then adjPart.mMontoNetoPluri  ");
        query.append("			else case when adj.lContratoAbierto=1 then adjPart.mMontoNetoLineaMax else adjPart.mMontoNetoMinimo end end)+isnull(conv.mMontoNetoLinea,0) montoTotalContratado ");
        query.append("	from( ");
        query.append("		select  ");
        query.append("			ped.cIdPedidoDefinitivo cIdContratoDefinitivo,cIdProcedimiento,nIdconsecutivoAdj,cIdRFC,fFormalizacion ");
        query.append("			,cConceptoPedido cConceptoContrato,''nCodContratoCNET,''nCodExpedienteCNET,''cOficioDG,''cFolioMASCP ");
        query.append("			,cIdPedidoDefinitivo as cNoContratoCNET,isnull(fInicio,fEntrega)fInicio,isnull(fFin,fEntrega)fFin ");
        query.append("			,nIdEstado ");
        query.append("		from mPedido as ped with(Nolock) where nIdEstado=4 ");
        query.append("	) cont  ");
        query.append("	inner join mProcedimientoAdjudicacionPartidas as adjPart with(Nolock) ");
        query.append("	on adjPart.cIdProcedimiento=cont.cIdProcedimiento ");
        query.append("	and adjPart.nIdconsecutivoAdj=cont.nIdconsecutivoAdj ");
        query.append("	and adjPart.cIdRFC=cont.cIdRFC ");
        query.append("	inner join mProcedimientoAdjudicacion as adj with(Nolock) ");
        query.append("	on adj.cIdProcedimiento=adjPart.cIdProcedimiento ");
        query.append("	and adj.cIdRFC=adjPart.cIdRFC ");
        query.append("	and adj.nIdconsecutivoAdj=adjPart.nIdconsecutivoAdj ");
        query.append("	inner join mProcedimiento as proced with(Nolock) ");
        query.append("	on proced.cIdProcedimiento=cont.cIdProcedimiento ");
        query.append("	inner join mConsolidadoLineas as consLineas with(Nolock) ");
        query.append("	on consLineas.cIdConsolidado=adjPart.cIdConsolidado ");
        query.append("	and consLineas.nIdLineaConsolidado=adjPart.nIdLineaConsolidado ");
        query.append("	inner join mCatalogoCABM as cucop with(Nolock) ");
        query.append("	on cucop.cIdCABM=consLineas.cIdCABM ");
        if (dato.getnTipoIngreso() != 0) {
            query.append("	inner join ( ");
            query.append("	select  ");
            query.append("	consol.cIdConsolidado ");
            query.append("	,consolSol.nIdLineaConsolidado,consolSol.cIdSolicitud ");
            query.append("	,(select dbo.fn_mTipoRescursoRequi(consolSol.cIdSolicitud))tipoIng ");
            query.append("	from mConsolidado as consol with(Nolock) ");
            query.append("	inner join mConsolidadoSolicitud as consolSol with(Nolock) ");
            query.append("	on consol.cIdConsolidado=consolSol.cIdConsolidado ");
            query.append("	inner join mConsolidadoLineas as consolLine with(Nolock) ");
            query.append("	on consolLine.cIdConsolidado=consol.cIdConsolidado ");
            query.append("	and consolLine.nIdLineaConsolidado=consolSol.nIdLineaConsolidado ");
            query.append("	where consol.nIdEstado=2 ");
            query.append("	group by consol.cIdConsolidado ");
            query.append("	,consolSol.nIdLineaConsolidado,consolSol.cIdSolicitud ");
            query.append(")requi on requi.cIdConsolidado=proced.cIdConsolidado ");
            query.append(" and requi.nIdLineaConsolidado=adjPart.nIdLineaConsolidado ");
            query.append(" and requi.tipoIng= " + dato.getnTipoIngreso());
        }
        query.append("	inner join mCatalogoFundamentoLegal as flg with(Nolock) ");
        query.append("	on flg.nIdCategoria=proced.nIdCategoria ");
        query.append("	and flg.nIdFundamentoLeg=adj.nIdFundamentoLeg ");
        query.append("	inner join mCatalogoProveedor as prov with(Nolock) ");
        query.append("	on prov.cIdRFC=cont.cIdRFC ");
        query.append("	left join ( ");
        query.append("		select  ");
        query.append("			contMod.cIdContratoDefinitivo cIdContratoDefinitivo ");
        query.append("			,contModPart.nIdLineaConsolidado,sum(contModPart.mMontoNeto)mMontoNetoLinea ");
        query.append("		from mContratoModificado as contMod with(nolock) ");
        query.append("		inner join mContratoModificadoPartida as contModPart with(nolock) ");
        query.append("		on contMod.cIdContratoDefinitivo=contModPart.cIdContratoDefinitivo ");
        query.append("		and contMod.nConsecutivoModificacion=contModPart.nConsecutivoModificacion ");
        query.append("		where contMod.nEstado=4 and contMod.isConvEjercicioAnt=0 and tipoMod=0 ");
        query.append("		group by contMod.cIdContratoDefinitivo ");
        query.append("		,contModPart.nIdLineaConsolidado ");
        query.append("	)conv on conv.cIdContratoDefinitivo=cont.cIdContratoDefinitivo ");
        query.append("	and conv.nIdLineaConsolidado=adjPart.nIdLineaConsolidado ");
        query.append("	where cont.nIdEstado=4  ");
        query.append("	and convert(date,cont.fFormalizacion)>=convert(date,'" + dato.getcFechaInicio() + "') ");
        query.append("	and convert(date,cont.fFormalizacion)<=convert(date,'" + dato.getcFechaFin() + "') ");
        query.append("	and SUBSTRING(cucop.cIdSubPartida,1,3) not  in(select cPartidaExcepcion from mCatalogoPartidasExcepcion7030 with(Nolock)) ");
        query.append(")sub ");
        query.append("group by  ");
        query.append("cIdContratoDefinitivo,nIdFundamentoLeg,cIdSubPartida,cNoContratoCNET ");
        query.append(",fInicio,fFin,fFormalizacion,cIdRFC,cConceptoContrato,nCodContratoCNET ");
        query.append(",nCodExpedienteCNET,cFundamentoLegal,cRazonSocial ");
        return query;
    }

    private StringBuilder queryPagosDirectos7030(DatosPedidoContrato dato) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("	select   ");
        query.append("	enc.nFolioPagoDirecto ,enc.caNoContrarrecibo ,enc.cConcepto  ");
        query.append("	,ue.D_DESCRIPCION areaRequirente ,enc.cIdRFC rfc,prov.cRazonSocial RazonSocial ");
        query.append("	,convert(int,rtrim(ltrim(paas.cIdSubPartida)))cIdSubPartida ,sum(paas.mMontoNeto)montoNeto  ");
        query.append("from tPagoDirectoEncabezado as enc with(Nolock)  ");
        if (dato.getnTipoIngreso() != 0) {
            query.append("	inner join( ");
            query.append("	select nFolioPagoDirecto from tPagoDirectoDetalle with(Nolock) ");
            query.append("	where substring(Ep, 40, 1)= " + dato.getnTipoIngreso());
            query.append("	group by nFolioPagoDirecto ");
            query.append(")tipoRecurso on tipoRecurso.nFolioPagoDirecto=enc.nFolioPagoDirecto ");
        }
        query.append("inner join tPagoDirectoPAAS as paas with(Nolock)  ");
        query.append("on paas.nFolioPagoDirecto=enc.nFolioPagoDirecto  ");
        query.append("inner join tCatUnidadEjecutora as ue with(Nolock)  ");
        query.append("on ue.cUnidadEjecutora=enc.cUnidadResponsable  ");
        query.append("inner join mCatalogoProveedor as prov with(Nolock)  ");
        query.append("on replace(prov.cIdRFC,'-','')=enc.cIdRFC  ");
        query.append("where enc.cDocumentoHaplicado='S' ");
        query.append("	and SUBSTRING(paas.cIdSubPartida,1,1) in(2,3,5) ");
        query.append("	and SUBSTRING(paas.cIdSubPartida,1,3) not  in(select cPartidaExcepcion from mCatalogoPartidasExcepcion7030 with(Nolock) union select cPartidaExcepcion from mCatalogoPartidasExcepcionPagoDirecto7030 with(Nolock)) ");
        query.append("	and convert(date,enc.fAplicacion)>=convert(date,'" + dato.getcFechaInicio() + "') ");
        query.append("	and convert(date,enc.fAplicacion)<=convert(date,'" + dato.getcFechaFin() + "') ");
        query.append("group by enc.nFolioPagoDirecto,enc.caNoContrarrecibo  ");
        query.append("	,enc.cConcepto,ue.D_DESCRIPCION ,enc.cIdRFC  ");
        query.append("	,paas.cIdSubPartida	,prov.cRazonSocial ,enc.cUnidadResponsable ");
        return query;
    }

    public File generaReportePagoDirecto(DatosPedidoContrato datos) throws Exception {
        Connection conn = null;
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Reporte_PagosDirectos.xlsx";
        String cadena = "";
        File cFileExcelPlantilla = null;
        InputStream fs = null;
        InputStream fsArchivo = null;
        ReportesGRMManager manager = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        XSSFWorkbook workbook = null;
        File fsalida = null;
        Calendar c = null;
        try {
            conn = getConnection();
            c = Calendar.getInstance();
            manager = new ReportesGRMManager();
            cFileExcelPlantilla = new File(datos.getcNamePlantilla());
            fs = new FileInputStream(cFileExcelPlantilla);
            Util.copiaArchivo(fs, file_name);
            fsArchivo = new FileInputStream(cFileExcelPlantilla);
            cadena = "A " + c.get(Calendar.DATE) + " de " + com.syc.adquisiciones.util.Util.getNameMonth(c.get(Calendar.MONTH) + 1) + " del " + c.get(Calendar.YEAR);
            workbook = new XSSFWorkbook(fsArchivo);
            XSSFSheet firstSheet = workbook.getSheetAt(0);
            XSSFCellStyle estiloTabla = workbook.createCellStyle();
            estiloTabla.setBorderRight(BorderStyle.THIN);
            estiloTabla.setBorderBottom(BorderStyle.DOTTED);
            // Escribe en la hoja 1
            manager.writeSheet1PagoDirecto(conn, firstSheet, estiloTabla, cadena, datos);
            ;
            fsalida = new File(file_name);
            fos = new FileOutputStream(fsalida);
            bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos);
            bos.flush();
            conn.commit();
        } catch (SQLException e) {
            log.error(e.getMessage().toString());
            if (conn != null) {
                conn.rollback();
            }
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fs != null) {
                fs.close();
            }
            if (fsArchivo != null) {
                fsArchivo.close();
            }
            if (workbook != null) {
                workbook.close();
            }
            fs = null;
            bos = null;
            workbook = null;
            fos = null;
            conn = null;
            cFileExcelPlantilla = null;
            fs = null;
            manager = null;
        }
        return fsalida;
    }

    public File generaReportePAASCNET(DatosPedidoContrato datos) throws Exception {
        Connection conn = null;
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Reporte_PASOP_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        File cFileExcelPlantilla = null;
        InputStream fs = null;
        InputStream fsArchivo = null;
        ReportesGRMManager manager = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        XSSFWorkbook workbook = null;
        File fsalida = null;
        try {
            conn = getConnection();
            manager = new ReportesGRMManager();
            cFileExcelPlantilla = new File(datos.getcNamePlantilla());
            fs = new FileInputStream(cFileExcelPlantilla);
            Util.copiaArchivo(fs, file_name);
            fsArchivo = new FileInputStream(cFileExcelPlantilla);
            workbook = new XSSFWorkbook(fsArchivo);
            XSSFSheet firstSheet = workbook.getSheetAt(1);
            // Escribe en la hoja 1
            manager.writeSheet1PASOP(conn, firstSheet, datos);
            fsalida = new File(file_name);
            fos = new FileOutputStream(fsalida);
            bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos);
            bos.flush();
            conn.commit();
        } catch (SQLException e) {
            log.error(e.getMessage().toString());
            if (conn != null) {
                conn.rollback();
            }
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fs != null) {
                fs.close();
            }
            if (workbook != null) {
                workbook.close();
            }
            fs = null;
            bos = null;
            workbook = null;
            fos = null;
            conn = null;
            cFileExcelPlantilla = null;
            fs = null;
            manager = null;
        }
        return fsalida;
    }

    public File generaReporteIndicadoresCNET(DatosPedidoContrato datos) throws Exception {
        Connection conn = null;
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "INDICADORES_COMPRANET_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        File cFileExcelPlantilla = null;
        InputStream fs = null;
        InputStream fsArchivo = null;
        ReportesGRMManager manager = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        XSSFWorkbook workbook = null;
        File fsalida = null;
        try {
            conn = getConnection();
            manager = new ReportesGRMManager();
            ConfiguraAplicativoBusinessLogic configApp = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
            boolean esSAIAlterno = "true".equalsIgnoreCase(configApp.getSystemSetting("SAI_AMBIENTAL")) || "true".equalsIgnoreCase(configApp.getSystemSetting("SAI_FONDEN")) || "S".equalsIgnoreCase(configApp.getSystemSetting("AMBIENTE_LOCAL")) || "true".equalsIgnoreCase(configApp.getSystemSetting("AMBIENTE_DESARROLLO"));
            String cEjercicioActivo = com.syc.adquisiciones.util.Util.obtieneEjercicioFiscalActivo(conn);
            cFileExcelPlantilla = new File(datos.getcNamePlantilla());
            fs = new FileInputStream(cFileExcelPlantilla);
            Util.copiaArchivo(fs, file_name);
            fsArchivo = new FileInputStream(cFileExcelPlantilla);
            workbook = new XSSFWorkbook(fsArchivo);
            XSSFSheet firstSheet = workbook.getSheetAt(5);
            XSSFSheet secondSheet = workbook.getSheetAt(6);
            XSSFSheet thirdSheet = workbook.getSheetAt(7);
            XSSFSheet fourthSheet = workbook.getSheetAt(8);
            XSSFSheet fivethSheet = workbook.getSheetAt(4);
            // Escribe en la hoja "Contratos ADQ SAI"
            manager.writeSheet6IndicadoresCNET(conn, firstSheet, datos);
            // Escribe en la hoja "Contratos de Obra SAI"
            manager.writeSheet7IndicadoresCNET(conn, secondSheet, datos);
            if (!esSAIAlterno) {
                // Escribe en la hoja "Contratos ADQ SAI Ambiental"
                manager.writeSheet8IndicadoresCNET(conn, thirdSheet, datos, cEjercicioActivo.substring(2, cEjercicioActivo.length()));
                // Escribe en la hoja "Contratos de Obra SAI Fonden"
                if (Integer.parseInt(cEjercicioActivo) < 2022) {
                    //De 2022 en adelante se quito FONDEN
                    manager.writeSheet9IndicadoresCNET(conn, fourthSheet, datos, cEjercicioActivo);
                }
            }
            // Escribe en la hoja "Concentrado_Contrataciones"
            manager.cantidadProcedIndicadoresCNET(conn, fivethSheet, datos, cEjercicioActivo.substring(2, cEjercicioActivo.length()), esSAIAlterno);
            // Actualiza formulas del libro
            HSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
            fsalida = new File(file_name);
            fos = new FileOutputStream(fsalida);
            bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos);
            bos.flush();
            conn.commit();
        } catch (SQLException e) {
            log.error(e.getMessage().toString());
            if (conn != null) {
                conn.rollback();
            }
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fs != null) {
                fs.close();
            }
            if (workbook != null) {
                workbook.close();
            }
            fs = null;
            bos = null;
            workbook = null;
            fos = null;
            conn = null;
            cFileExcelPlantilla = null;
            fs = null;
            manager = null;
        }
        return fsalida;
    }

    public File generaReporteTotalizado(DatosPedidoContrato datos) throws Exception {
        Connection conn = null;
        String fileName = System.getProperty("java.io.tmpdir") + File.separatorChar + "REPORTE_TOTALIZADO_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        File cFileExcelPlantilla = null;
        InputStream fs = null;
        InputStream fsArchivo = null;
        ReportesGRMManager manager = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        XSSFWorkbook workbook = null;
        File fsalida = null;
        try {
            conn = getConnection();
            manager = new ReportesGRMManager();
            String cEjercicioActivo = com.syc.adquisiciones.util.Util.obtieneEjercicioFiscalActivo(conn);
            cFileExcelPlantilla = new File(datos.getcNamePlantilla());
            fs = new FileInputStream(cFileExcelPlantilla);
            Util.copiaArchivo(fs, fileName);
            fsArchivo = new FileInputStream(cFileExcelPlantilla);
            workbook = new XSSFWorkbook(fsArchivo);
            XSSFSheet firstSheet = workbook.getSheetAt(0);
            // Escribe en la hoja
            manager.writeSheetReporteTotalizado(conn, firstSheet, datos, cEjercicioActivo);
            fsalida = new File(fileName);
            fos = new FileOutputStream(fsalida);
            bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos);
            bos.flush();
            conn.commit();
            return fsalida;
        } catch (SQLException e) {
            log.error(e.getMessage().toString());
            if (conn != null) {
                conn.rollback();
            }
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fs != null) {
                fs.close();
            }
            if (workbook != null) {
                workbook.close();
            }
            fs = null;
            bos = null;
            workbook = null;
            fos = null;
            conn = null;
            cFileExcelPlantilla = null;
            fs = null;
            manager = null;
        }
    }

    public File generaReporteArrendamiento(String plantilla, String where) throws Exception {
        Connection conn = null;
        ReportesGRMManager manager = null;
        String fileName = System.getProperty("java.io.tmpdir") + File.separatorChar + "REPORTE_ARRENDAMIENTO_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File cFileExcelPlantilla = null;
        InputStream fs = null;
        InputStream fsArchivo = null;
        XSSFWorkbook workbook = null;
        File file = null;
        try {
            conn = getConnection();
            manager = new ReportesGRMManager();
            cFileExcelPlantilla = new File(plantilla);
            fs = new FileInputStream(cFileExcelPlantilla);
            Util.copiaArchivo(fs, fileName);
            fsArchivo = new FileInputStream(cFileExcelPlantilla);
            workbook = new XSSFWorkbook(fsArchivo);
            XSSFSheet firstSheet = workbook.getSheetAt(0);
            XSSFCellStyle estiloTabla = workbook.createCellStyle();
            estiloTabla.setBorderRight(BorderStyle.THIN);
            estiloTabla.setBorderBottom(BorderStyle.DOTTED);
            manager.generaReporteArrendamiento(conn, plantilla, where, firstSheet, estiloTabla);
            file = new File(fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos);
            bos.flush();
            return file;
        } finally {
            CloseObject.closeObject(conn);
            workbook.close();
        }
    }

    public File generaReporteContratosCap4(DatosPedidoContrato datos) throws Exception {
        Connection conn = null;
        String fileName = System.getProperty("java.io.tmpdir") + File.separatorChar + "REPORTE_CONTRATOS_CAP4_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        File cFileExcelPlantilla = null;
        InputStream fs = null;
        InputStream fsArchivo = null;
        ReportesGRMManager manager = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        XSSFWorkbook workbook = null;
        File fsalida = null;
        try {
            conn = getConnection();
            manager = new ReportesGRMManager();
            String cEjercicioActivo = com.syc.adquisiciones.util.Util.obtieneEjercicioFiscalActivo(conn);
            cFileExcelPlantilla = new File(datos.getcNamePlantilla());
            fs = new FileInputStream(cFileExcelPlantilla);
            Util.copiaArchivo(fs, fileName);
            fsArchivo = new FileInputStream(cFileExcelPlantilla);
            workbook = new XSSFWorkbook(fsArchivo);
            XSSFSheet firstSheet = workbook.getSheetAt(0);
            // Escribe en la hoja
            manager.writeSheetReporteContCap4(conn, firstSheet, datos, cEjercicioActivo);
            fsalida = new File(fileName);
            fos = new FileOutputStream(fsalida);
            bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos);
            bos.flush();
            conn.commit();
            return fsalida;
        } catch (SQLException e) {
            log.error(e.getMessage().toString());
            if (conn != null) {
                conn.rollback();
            }
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fs != null) {
                fs.close();
            }
            if (workbook != null) {
                workbook.close();
            }
            fs = null;
            bos = null;
            workbook = null;
            fos = null;
            conn = null;
            cFileExcelPlantilla = null;
            fs = null;
            manager = null;
        }
    }

    public File generaReportePSP(DatosPedidoContrato datos) throws Exception {
        Connection conn = null;
        String fileName = System.getProperty("java.io.tmpdir") + File.separatorChar + "REPORTE_DE_PSP_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        File cFileExcelPlantilla = null;
        InputStream fs = null;
        InputStream fsArchivo = null;
        ReportesGRMManager manager = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        XSSFWorkbook workbook = null;
        File fsalida = null;
        String cEjercicioActivo = "2021";
        ConfiguraAplicativoBusinessLogic configApp = null;
        String desa = "";
        try {
            conn = getConnection();
            manager = new ReportesGRMManager();
            configApp = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
            boolean esSAIDesarrollo = "true".equalsIgnoreCase(configApp.getSystemSetting("AMBIENTE_DESARROLLO"));
            boolean esSAIAlterno = "true".equalsIgnoreCase(configApp.getSystemSetting("AMBIENTE_DESARROLLO")) || "true".equalsIgnoreCase(configApp.getSystemSetting("SAI_AMBIENTAL")) || "true".equalsIgnoreCase(configApp.getSystemSetting("SAI_FONDEN"));
            if (esSAIDesarrollo) {
                desa = "_desa";
            }
            cEjercicioActivo = com.syc.adquisiciones.util.Util.obtieneEjercicioFiscalActivo(conn);
            cFileExcelPlantilla = new File(datos.getcNamePlantilla());
            fs = new FileInputStream(cFileExcelPlantilla);
            Util.copiaArchivo(fs, fileName);
            fsArchivo = new FileInputStream(cFileExcelPlantilla);
            workbook = new XSSFWorkbook(fsArchivo);
            XSSFSheet firstSheet = workbook.getSheetAt(1);
            // Escribe en la hoja 1
            manager.writeSheetReportePSP(conn, firstSheet, datos, cEjercicioActivo, desa, esSAIAlterno);
            HSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
            CTPivotCacheDefinition.Factory.newInstance().setRefreshOnLoad(true);
            fsalida = new File(fileName);
            fos = new FileOutputStream(fsalida);
            bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos);
            bos.flush();
            conn.commit();
            return fsalida;
        } catch (SQLException e) {
            log.error(e.getMessage().toString());
            if (conn != null) {
                conn.rollback();
            }
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fs != null) {
                fs.close();
            }
            if (workbook != null) {
                workbook.close();
            }
            fs = null;
            bos = null;
            workbook = null;
            fos = null;
            conn = null;
            cFileExcelPlantilla = null;
            fs = null;
            manager = null;
            configApp = null;
        }
    }

    public File generaSemaforoSAC(ProcedimientoSAC datos) throws Exception {
        Connection conn = null;
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Semaforo_SAC_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        File cFileExcelPlantilla = null;
        InputStream fs = null;
        InputStream fsArchivo = null;
        ReportesGRMManager manager = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        XSSFWorkbook workbook = null;
        File fsalida = null;
        XSSFSheet firstSheet = null;
        XSSFSheet secondSheet = null;
        try {
            conn = getConnection();
            manager = new ReportesGRMManager();
            cFileExcelPlantilla = new File(datos.getcNamePlantilla());
            fs = new FileInputStream(cFileExcelPlantilla);
            Util.copiaArchivo(fs, file_name);
            fsArchivo = new FileInputStream(cFileExcelPlantilla);
            workbook = new XSSFWorkbook(fsArchivo);
            firstSheet = workbook.getSheetAt(0);
            secondSheet = workbook.getSheetAt(1);
            // Escribe en la hoja 1
            manager.writeSheet1SemaforoSAC(conn, firstSheet, datos);
            // Escribe en la hoja 2
            manager.writeSheet2SemaforoSAC(conn, secondSheet, datos);
            HSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
            fsalida = new File(file_name);
            fos = new FileOutputStream(fsalida);
            bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos);
            bos.flush();
            conn.commit();
        } catch (SQLException e) {
            log.error(e.getMessage().toString());
            if (conn != null) {
                conn.rollback();
            }
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fs != null) {
                fs.close();
            }
            if (workbook != null) {
                workbook.close();
            }
            fs = null;
            bos = null;
            workbook = null;
            firstSheet = null;
            secondSheet = null;
            fos = null;
            conn = null;
            cFileExcelPlantilla = null;
            fs = null;
            manager = null;
        }
        return fsalida;
    }

    public File generaReportePenasConvencionales(DatosPedidoContrato datos) throws Exception {
        Connection conn = null;
        String fileName = System.getProperty("java.io.tmpdir") + File.separatorChar + "REPORTE_PenasYDeducciones_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        File cFileExcelPlantilla = null;
        InputStream fs = null;
        InputStream fsArchivo = null;
        ReportesGRMManager manager = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        XSSFWorkbook workbook = null;
        XSSFSheet firstSheet = null;
        XSSFSheet secondSheet = null;
        XSSFSheet thirdSheet = null;
        XSSFCellStyle estiloCell = null;
        XSSFDataFormat format = null;
        File fsalida = null;
        boolean error = true;
        String cEjercicioActivo = "2022";
        try {
            conn = getConnection();
            manager = new ReportesGRMManager();
            cFileExcelPlantilla = new File(datos.getcNamePlantilla());
            fs = new FileInputStream(cFileExcelPlantilla);
            Util.copiaArchivo(fs, fileName);
            fsArchivo = new FileInputStream(cFileExcelPlantilla);
            workbook = new XSSFWorkbook(fsArchivo);
            firstSheet = workbook.getSheetAt(0);
            secondSheet = workbook.getSheetAt(1);
            thirdSheet = workbook.getSheetAt(2);
            estiloCell = workbook.createCellStyle();
            format = workbook.createDataFormat();
            estiloCell.setDataFormat(format.getFormat("#,##0.00"));
            //Writte in first sheet
            cEjercicioActivo = com.syc.adquisiciones.util.Util.obtieneEjercicioFiscalActivo(conn);
            manager.writeSheetReportePenas(conn, firstSheet, datos, cEjercicioActivo, estiloCell, false);
            //writte in second sheet
            manager.writeSheetReportePenas(conn, secondSheet, datos, cEjercicioActivo, estiloCell, true);
            //writte in third sheet
            manager.writeSheetthirdReportePenas(conn, thirdSheet, datos, cEjercicioActivo, estiloCell);
            fsalida = new File(fileName);
            fos = new FileOutputStream(fsalida);
            bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos);
            bos.flush();
            conn.commit();
            error = false;
        } catch (Exception e) {
            log.error(e.getMessage().toString());
            if (error && conn != null) {
                conn.rollback();
            }
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fs != null) {
                fs.close();
            }
            if (workbook != null) {
                workbook.close();
            }
            fs = null;
            bos = null;
            workbook = null;
            fos = null;
            conn = null;
            cFileExcelPlantilla = null;
            fs = null;
            manager = null;
            estiloCell = null;
            format = null;
        }
        return fsalida;
    }

    public File generaReporteProveedoresSancionados(DatosPedidoContrato datos) throws Exception {
        Connection conn = null;
        String fileName = System.getProperty("java.io.tmpdir") + File.separatorChar + "Proveedores_Sancionados_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        File cFileExcelPlantilla = null;
        InputStream fs = null;
        InputStream fsArchivo = null;
        ReportesGRMManager manager = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        XSSFWorkbook workbook = null;
        File fsalida = null;
        try {
            conn = getConnection();
            manager = new ReportesGRMManager();
            cFileExcelPlantilla = new File(datos.getcNamePlantilla());
            fs = new FileInputStream(cFileExcelPlantilla);
            Util.copiaArchivo(fs, fileName);
            fsArchivo = new FileInputStream(cFileExcelPlantilla);
            workbook = new XSSFWorkbook(fsArchivo);
            XSSFSheet firstSheet = workbook.getSheetAt(0);
            // Escribe en la hoja
            manager.writeSheetReporteProvIncump(conn, firstSheet, datos);
            // Escribe en la hoja
            XSSFSheet secondtSheet = workbook.getSheetAt(1);
            manager.writeSheetReporteRecisionContratos(conn, secondtSheet, datos);
            fsalida = new File(fileName);
            fos = new FileOutputStream(fsalida);
            bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos);
            bos.flush();
            conn.commit();
            return fsalida;
        } catch (SQLException e) {
            log.error(e.getMessage().toString());
            if (conn != null) {
                conn.rollback();
            }
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fs != null) {
                fs.close();
            }
            if (workbook != null) {
                workbook.close();
            }
            fs = null;
            bos = null;
            workbook = null;
            fos = null;
            conn = null;
            cFileExcelPlantilla = null;
            fs = null;
            manager = null;
        }
    }

    public File generaReporteGarantias(DatosPedidoContrato datos) throws Exception {
        Connection conn = null;
        String fileName = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteGarantias" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        File cFileExcelPlantilla = null;
        InputStream fs = null;
        InputStream fsArchivo = null;
        ReportesGRMManager manager = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        XSSFWorkbook workbook = null;
        File fsalida = null;
        XSSFCellStyle estiloCell = null;
        XSSFDataFormat format = null;
        try {
            conn = getConnection();
            manager = new ReportesGRMManager();
            cFileExcelPlantilla = new File(datos.getcNamePlantilla());
            fs = new FileInputStream(cFileExcelPlantilla);
            Util.copiaArchivo(fs, fileName);
            fsArchivo = new FileInputStream(cFileExcelPlantilla);
            workbook = new XSSFWorkbook(fsArchivo);
            XSSFSheet firstSheet = workbook.getSheetAt(0);
            estiloCell = workbook.createCellStyle();
            format = workbook.createDataFormat();
            estiloCell.setDataFormat(format.getFormat("#,##0.00"));
            // Escribe en la hoja
            manager.writeSheetReporteGarantia(conn, firstSheet, datos, estiloCell);
            fsalida = new File(fileName);
            fos = new FileOutputStream(fsalida);
            bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos);
            bos.flush();
            conn.commit();
            return fsalida;
        } catch (SQLException e) {
            log.error(e.getMessage().toString());
            if (conn != null) {
                conn.rollback();
            }
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fs != null) {
                fs.close();
            }
            if (workbook != null) {
                workbook.close();
            }
            fs = null;
            bos = null;
            workbook = null;
            fos = null;
            conn = null;
            cFileExcelPlantilla = null;
            fs = null;
            manager = null;
            estiloCell = null;
            format = null;
        }
    }

    public File generaContratosConCompromiso(DatosPedidoContrato datos) throws Exception {
        Connection conn = null;
        String fileName = System.getProperty("java.io.tmpdir") + File.separatorChar + "ContratosConCompromiso" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        File cFileExcelPlantilla = null;
        InputStream fs = null;
        InputStream fsArchivo = null;
        ReportesGRMManager manager = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        XSSFWorkbook workbook = null;
        File fsalida = null;
        XSSFCellStyle estiloCell = null;
        XSSFDataFormat format = null;
        try {
            conn = getConnection();
            manager = new ReportesGRMManager();
            cFileExcelPlantilla = new File(datos.getcNamePlantilla());
            fs = new FileInputStream(cFileExcelPlantilla);
            Util.copiaArchivo(fs, fileName);
            fsArchivo = new FileInputStream(cFileExcelPlantilla);
            workbook = new XSSFWorkbook(fsArchivo);
            XSSFSheet firstSheet = workbook.getSheetAt(0);
            estiloCell = workbook.createCellStyle();
            format = workbook.createDataFormat();
            estiloCell.setDataFormat(format.getFormat("#,##0.00"));
            // Escribe en la hoja
            manager.writeSheetReporteContratosCompromiso(conn, firstSheet, datos, estiloCell);
            fsalida = new File(fileName);
            fos = new FileOutputStream(fsalida);
            bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos);
            bos.flush();
            conn.commit();
            return fsalida;
        } catch (SQLException e) {
            log.error(e.getMessage().toString());
            if (conn != null) {
                conn.rollback();
            }
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fs != null) {
                fs.close();
            }
            if (workbook != null) {
                workbook.close();
            }
            fs = null;
            bos = null;
            workbook = null;
            fos = null;
            conn = null;
            cFileExcelPlantilla = null;
            fs = null;
            manager = null;
            estiloCell = null;
            format = null;
        }
    }

    public File generaReporteENSA(DatosPedidoContrato datos) throws Exception {
        Connection conn = null;
        String fileName = System.getProperty("java.io.tmpdir") + File.separatorChar + "Reporte_ENSA" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        File cFileExcelPlantilla = null;
        InputStream fs = null;
        InputStream fsArchivo = null;
        ReportesGRMManager manager = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        XSSFWorkbook workbook = null;
        File fsalida = null;
        try {
            conn = getConnection();
            manager = new ReportesGRMManager();
            cFileExcelPlantilla = new File(datos.getcNamePlantilla());
            fs = new FileInputStream(cFileExcelPlantilla);
            Util.copiaArchivo(fs, fileName);
            fsArchivo = new FileInputStream(cFileExcelPlantilla);
            workbook = new XSSFWorkbook(fsArchivo);
            XSSFSheet firstSheet = workbook.getSheetAt(0);
            // Escribe en la hoja
            manager.writeSheetReporteENSA(conn, firstSheet, datos);
            fsalida = new File(fileName);
            fos = new FileOutputStream(fsalida);
            bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos);
            bos.flush();
            conn.commit();
            return fsalida;
        } catch (SQLException e) {
            log.error(e.getMessage().toString());
            if (conn != null) {
                conn.rollback();
            }
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fs != null) {
                fs.close();
            }
            if (workbook != null) {
                workbook.close();
            }
            fs = null;
            bos = null;
            workbook = null;
            fos = null;
            conn = null;
            cFileExcelPlantilla = null;
            fs = null;
            manager = null;
        }
    }

    public File generaFormatoUCACP(DatosPedidoContrato datos) throws Exception {
        log.info("Inicia la logica de negocio para el reporte UCACP");
        Connection conn = null;
        String fileName = System.getProperty("java.io.tmpdir") + File.separatorChar + "Formato_UCACP" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        File cFileExcelPlantilla = null;
        InputStream fs = null;
        InputStream fsArchivo = null;
        ReportesGRMManager manager = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        XSSFWorkbook workbook = null;
        File fsalida = null;
        XSSFSheet firstSheet = null;
        XSSFSheet secondSheet = null;
        XSSFSheet thirdSheet = null;
        XSSFCellStyle estiloCelda = null;
        XSSFCellStyle estiloCeldaMoneda = null;
        XSSFCellStyle estiloCeldaFecha = null;
        XSSFCellStyle estiloCeldaPorcentaje = null;
        XSSFDataFormat format = null;
        try {
            manager = new ReportesGRMManager();
            cFileExcelPlantilla = new File(datos.getcNamePlantilla());
            fs = new FileInputStream(cFileExcelPlantilla);
            Util.copiaArchivo(fs, fileName);
            fsArchivo = new FileInputStream(cFileExcelPlantilla);
            workbook = new XSSFWorkbook(fsArchivo);
            firstSheet = workbook.getSheetAt(0);
            secondSheet = workbook.getSheetAt(2);
            thirdSheet = workbook.getSheetAt(3);
            format = workbook.createDataFormat();
            estiloCelda = workbook.createCellStyle();
            estiloCeldaMoneda = workbook.createCellStyle();
            estiloCeldaFecha = workbook.createCellStyle();
            estiloCeldaPorcentaje = workbook.createCellStyle();
            estiloCelda.setBorderRight(BorderStyle.THIN);
            estiloCelda.setBorderLeft(BorderStyle.THIN);
            estiloCelda.setBorderBottom(BorderStyle.DOTTED);
            estiloCelda.setBorderTop(BorderStyle.DOTTED);
            estiloCelda.setAlignment(HorizontalAlignment.CENTER);
            estiloCeldaMoneda.setBorderRight(BorderStyle.THIN);
            estiloCeldaMoneda.setBorderLeft(BorderStyle.THIN);
            estiloCeldaMoneda.setBorderBottom(BorderStyle.DOTTED);
            estiloCeldaMoneda.setBorderTop(BorderStyle.DOTTED);
            estiloCeldaMoneda.setAlignment(HorizontalAlignment.RIGHT);
            estiloCeldaFecha.setBorderRight(BorderStyle.THIN);
            estiloCeldaFecha.setBorderLeft(BorderStyle.THIN);
            estiloCeldaFecha.setBorderBottom(BorderStyle.DOTTED);
            estiloCeldaFecha.setBorderTop(BorderStyle.DOTTED);
            estiloCeldaFecha.setAlignment(HorizontalAlignment.CENTER);
            estiloCeldaPorcentaje.setBorderRight(BorderStyle.THIN);
            estiloCeldaPorcentaje.setBorderLeft(BorderStyle.THIN);
            estiloCeldaPorcentaje.setBorderBottom(BorderStyle.DOTTED);
            estiloCeldaPorcentaje.setBorderTop(BorderStyle.DOTTED);
            estiloCeldaPorcentaje.setAlignment(HorizontalAlignment.CENTER);
            estiloCeldaMoneda.setDataFormat(format.getFormat("#,##0.00"));
            estiloCeldaFecha.setDataFormat(format.getFormat("dd/mm/yyyy"));
            estiloCeldaPorcentaje.setDataFormat(format.getFormat("0.00%"));
            conn = getConnection(datos.getJniName());
            Map<Integer, List<ObjectCelda>> contratos = manager.resulsetContratos(conn, datos);
            Map<Integer, List<ObjectCelda>> partidasContrato = manager.resulsetPartidasContrato(conn, datos);
            Map<Integer, List<ObjectCelda>> facturasContrato = manager.resulsetFacturasContrato(conn, datos);
            conn.close();
            // Escribe en la hoja 1
            manager.writeSheet1UCAP(contratos, firstSheet, estiloCelda, estiloCeldaMoneda, estiloCeldaFecha, estiloCeldaPorcentaje);
            // Escribe en la hoja 2
            manager.writeSheet2UCAP(partidasContrato, secondSheet, estiloCelda, estiloCeldaMoneda);
            // Escribe en la hoja 3
            manager.writeSheet3UCAP(facturasContrato, thirdSheet, estiloCelda, estiloCeldaMoneda);
            fsalida = new File(fileName);
            fos = new FileOutputStream(fsalida);
            bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos);
            bos.flush();
            return fsalida;
        } catch (SQLException e) {
            log.error(e.getMessage().toString());
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fs != null) {
                fs.close();
            }
            if (workbook != null) {
                workbook.close();
            }
            fs = null;
            bos = null;
            workbook = null;
            fos = null;
            conn = null;
            cFileExcelPlantilla = null;
            fs = null;
            manager = null;
            estiloCelda = null;
            estiloCeldaMoneda = null;
            estiloCeldaFecha = null;
            format = null;
            estiloCeldaPorcentaje = null;
            log.info("Finaliza la logica de negocio para el reporte UCACP");
        }
    }

    public File generaLAyoutRequi(Requisition requi) throws Exception {
        Connection conn = null;
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Pre_Layout_Requi_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        File cFileExcelPlantilla = null;
        InputStream fs = null;
        InputStream fsArchivo = null;
        ReportesGRMManager manager = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        XSSFWorkbook workbook = null;
        File fsalida = null;
        XSSFSheet firstSheet = null;
        XSSFCellStyle estiloCeldaMoneda = null;
        XSSFDataFormat format = null;
        try {
            conn = getConnection();
            manager = new ReportesGRMManager();
            cFileExcelPlantilla = new File(requi.getcNamePlantilla());
            requi.setEjercicio(com.syc.adquisiciones.util.Util.obtieneEjercicioFiscalActivo(conn));
            fs = new FileInputStream(cFileExcelPlantilla);
            Util.copiaArchivo(fs, file_name);
            fsArchivo = new FileInputStream(cFileExcelPlantilla);
            workbook = new XSSFWorkbook(fsArchivo);
            firstSheet = workbook.getSheetAt(0);
            estiloCeldaMoneda = workbook.createCellStyle();
            format = workbook.createDataFormat();
            estiloCeldaMoneda.setDataFormat(format.getFormat("#,##0.00"));
            // Escribe en la hoja 1
            manager.writeSheetPreLayoutRequi(conn, firstSheet, requi, estiloCeldaMoneda);
            //HSSFFormulaEvaluator.evaluateAllFormulaCells( workbook );
            fsalida = new File(file_name);
            fos = new FileOutputStream(fsalida);
            bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos);
            bos.flush();
            conn.commit();
        } catch (SQLException e) {
            log.error(e.getMessage().toString());
            if (conn != null) {
                conn.rollback();
            }
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fs != null) {
                fs.close();
            }
            if (workbook != null) {
                workbook.close();
            }
            fs = null;
            bos = null;
            workbook = null;
            firstSheet = null;
            fos = null;
            conn = null;
            cFileExcelPlantilla = null;
            fs = null;
            manager = null;
            estiloCeldaMoneda = null;
            format = null;
        }
        return fsalida;
    }
}
