package com.syc.reportes;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ConciliacionGastoDevengadoManager;
import com.syc.reportes.core.ReporteConciliaEgresosManager;
import com.syc.reportes.core.ReporteConciliacion11225Manager;
//import com.syc.reportes.core.ReporteMomentosManager;
import com.syc.reportes.core.ReportePolizasDetManager;
import com.syc.reportes.core.ReportePolizasManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ReportePolizasBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(AdecuacionBusinessLogic.class);

    public ReportePolizasBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public void generaReportePolizas(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String fechaInicio = req.getParameter("fecha_inicio");
            String fechaFin = req.getParameter("fecha_fin");
            String cContable = req.getParameter("cCentroContable");
            String tPoliza = req.getParameter("tipoPoliza");
            String tipoReporte = req.getParameter("TIPO_REPORTE");
            String tipo = req.getParameter("Detalle");
            String cUR = req.getParameter("cUnidadResponsable");
            if ("*".equals(cUR)) {
                cUR = "%";
            }
            if (tipoReporte == null) {
                tipoReporte = "0";
            } else if ("false".equals(tipo)) {
                tipoReporte = "1";
            }
            file = ReportePolizasManager.generaReportePolizasManager(conn, fechaInicio, fechaFin, cContable, tPoliza, tipoReporte, cUR, plantilla);
            File f = new File(file);
            resp.setContentType("application/octet-stream");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
        } finally {
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
    }

    public void generaReportePolizasDet(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String fechaInicio = req.getParameter("fecha_inicio");
            String fechaFin = req.getParameter("fecha_fin");
            String cContable = req.getParameter("cCentroContable");
            String tPoliza = req.getParameter("tipoPoliza");
            String cUR = req.getParameter("cUnidadResponsable");
            file = ReportePolizasDetManager.generaReportePolizasDetManager(conn, fechaInicio, fechaFin, cContable, tPoliza, cUR, plantilla);
            File f = new File(file);
            resp.setContentType("application/octet-stream");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
        } finally {
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
    }

    public void generaReporteCOMSOC(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String fechaFin = req.getParameter("fecha_fin");
            int mesFin = Integer.parseInt(fechaFin.substring(5, 7));
            int anio = Integer.parseInt(fechaFin.substring(0, 4));
            file = ReportePolizasDetManager.generaReporteCOMSOCManager(conn, mesFin, anio, plantilla);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + "ReporteCOMSOC.xls" + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
        } finally {
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
    }

    public void generaConciliacionRad(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String reporte = req.getParameter("TIPO_REPORTE");
            String fechaFin = req.getParameter("fecha_fin");
            int mesFin = Integer.parseInt(fechaFin.split("/")[1]);
            if ("CONCILIARADPAGADO".equals(reporte)) {
                file = ReportePolizasDetManager.generaReporteConciliaRadPagManager(conn, mesFin, plantilla);
            } else if ("CONCILIARADINGRESO".equals(reporte)) {
                file = ReportePolizasDetManager.generaReporteConciliaRadIngManager(conn, mesFin, plantilla);
            }
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + "ReporteConciliaRadicado.xls" + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
        } finally {
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
    }

    public void generaConciliacionIngresoGasto(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String fechaFin = req.getParameter("fecha_fin");
            int mesFin = Integer.parseInt(fechaFin.split("/")[1]);
            file = ReportePolizasDetManager.generaReporteConciliaIngGtoManager(conn, mesFin, plantilla);
            File f = new File(file);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
            f = null;
        } finally {
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
    }

    public String generaReporteConciliacion11225(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String fechaFin = req.getParameter("fecha_fin");
            String saldo = req.getParameter("SaldoSIAFF");
            String cMes = String.valueOf(Integer.parseInt(fechaFin.split("/")[1]));
            file = ReporteConciliacion11225Manager.generaReporteConciliacion(conn, cMes, saldo, plantilla);
            File f = new File(file);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, ".xls");
            out.flush();
            out.close();
            f = null;
        } finally {
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
        return file;
    }

    public void generaReporteGastoDevengado(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String fechaInicio = req.getParameter("fecha_fin");
            file = ConciliacionGastoDevengadoManager.GastoDevengadoManager(conn, fechaInicio, plantilla);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + "ConciliaGastoDevengado.xls" + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
        } finally {
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
    }

    public void generaCedulaPatrimonio(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String fechaFin = req.getParameter("fecha_fin");
            int mesFin = Integer.parseInt(fechaFin.split("/")[1]);
            file = ReportePolizasDetManager.generaCedulaPatrimonioManager(conn, mesFin, plantilla);
            File f = new File(file);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
        } finally {
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
    }

    public void generaReporteIP(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String fechaFin = req.getParameter("fecha_fin");
            int mesFin = Integer.parseInt(fechaFin.split("/")[1]);
            file = ReportePolizasDetManager.generaReporteIPManager(conn, mesFin, plantilla);
            File f = new File(file);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
        } finally {
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
    }

    public void generaReporteConciliaMomentos(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String reporte = req.getParameter("TIPO_REPORTE");
            String fechaFin = req.getParameter("fecha_fin");
            int mesFin = 0;
            if ("FFM".equals(reporte))
                mesFin = Integer.parseInt(fechaFin.split("-")[1]);
            else
                mesFin = Integer.parseInt(fechaFin.split("/")[1]);
            if ("ConciliaCompromiso".equals(reporte)) {
                file = ReportePolizasDetManager.conciliaCompromisoManager(conn, mesFin, plantilla);
            } else if ("ConciliaDevengado".equals(reporte)) {
                file = ReportePolizasDetManager.conciliaDevengadoManager(conn, mesFin, plantilla);
            } else if ("ConciliaEjercido".equals(reporte)) {
                file = ReportePolizasDetManager.conciliaEjercidoManager(conn, mesFin, plantilla);
            } else if ("CONCILIAMOD".equals(reporte)) {
                file = ReportePolizasDetManager.generaReporteConciliacionManager(conn, mesFin, plantilla);
            } else if ("ConciliaMomentos".equals(reporte)) {
                file = ReportePolizasDetManager.conciliaMomentosManager(conn, mesFin, plantilla);
            } else if ("ConciliaIngreso".equals(reporte)) {
                file = ReportePolizasDetManager.conciliaIngresosManager(conn, mesFin, fechaFin, plantilla);
            } else if ("FFM".equals(reporte)) {
                file = ReportePolizasDetManager.reporteFFM(conn, mesFin, plantilla);
            } else if ("ConciliaDevengadoIng".equals(reporte)) {
                file = ReportePolizasDetManager.conciliaDevengadoIngManager(conn, mesFin, plantilla);
            } else if ("ConciliaOrgModDispEjeIng".equals(reporte)) {
                file = ReportePolizasDetManager.conciliaOrgDispModEjeIngManager(conn, mesFin, plantilla);
            } else if ("ConciliaEgresos".equals(reporte)) {
                file = ReporteConciliaEgresosManager.generaReporteConciliaEgresos(conn, mesFin, fechaFin, plantilla);
            } else if ("ConciliaRadicadoPagado".equals(reporte)) {
                file = ReportePolizasDetManager.conciliaRadicadoPagado(conn, mesFin, plantilla);
            }
            File f = new File(file);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
            conn.commit();
        } finally {
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
    }

    public void actualizarSaldos(Integer mes) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            ReportePolizasDetManager.actualizaSaldosManager(conn, mes);
            conn.commit();
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public void generaReporteREP(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String unidad = (req.getParameter("UR") != null) ? req.getParameter("UR").trim() : "";
            String rfc = (req.getParameter("cIdRFC") != null) ? req.getParameter("cIdRFC").trim() : "";
            if ("".equals(unidad)) {
                unidad = "%";
            }
            if ("".equals(rfc)) {
                rfc = "%";
            }
            file = ReportePolizasManager.generaReporteREP(conn, unidad, rfc, plantilla);
            File f = new File(file);
            resp.setContentType("application/octet-stream");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
        } finally {
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
    }

    public void generaReporteBoletos(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String tipo = (req.getParameter("tipo") != null) ? req.getParameter("tipo").trim() : "";
            if ("2".equals(tipo)) {
                file = ReportePolizasManager.generaReporteBoletosAdjuntos(conn, plantilla);
            } else {
                file = ReportePolizasManager.generaReporteBoletosPendientes(conn, plantilla);
            }
            File f = new File(file);
            resp.setContentType("application/octet-stream");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
        } finally {
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
    }

    public void generaReporteCapacitacion(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantillas) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String mes = req.getParameter("mes");
            String ejercicioFiscal = req.getParameter("ejercicioFiscal");
            int mesIni = Integer.parseInt(mes);
            int anio = Integer.parseInt(ejercicioFiscal);
            file = ReportePolizasDetManager.generaReporteCapacitacionManager(conn, mesIni, anio, plantillas);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + "ReporteCapacitacion.xls" + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
        } finally {
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
    }

    public List<String> cargaArchivoCFDI(File nombreDestino) throws Exception {
        List<String> resultado = null;
        Connection conn = null;
        InputStream is = new FileInputStream(nombreDestino);
        conn = getConnection();
        try {
            ReportePolizasManager.borraTabla(conn);
            List<String[]> renglonesArchivo = leerArchivo(is);
            conn.setAutoCommit(false);
            ReportePolizasManager.insertarDatosCfdi(conn, renglonesArchivo);
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
            if (resultado == null)
                resultado = new ArrayList<String>();
            resultado.add(e.toString());
            log.error(e.getMessage(), e);
        } finally {
            CloseObject.closeObject(conn);
        }
        return resultado;
    }

    public List<String[]> leerArchivo(InputStream in) throws Exception {
        List<String[]> lista = new ArrayList<>();
        // solo para .xlsx
        Workbook workbook = new XSSFWorkbook(in);
        // primera hoja
        Sheet sheet = workbook.getSheetAt(0);
        int lineaInicial = 3;
        for (Row row : sheet) {
            if (row.getRowNum() >= lineaInicial) {
                List<String> columnas = new ArrayList<>();
                int totalColumnas = row.getLastCellNum();
                for (int cn = 0; cn < totalColumnas; cn++) {
                    Cell cell = row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String valor = "";
                    switch(cell.getCellTypeEnum()) {
                        case STRING:
                            valor = cell.getStringCellValue();
                            break;
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                valor = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cell.getDateCellValue());
                            } else {
                                valor = BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
                            }
                            break;
                        case BOOLEAN:
                            valor = Boolean.toString(cell.getBooleanCellValue());
                            break;
                        case FORMULA:
                            try {
                                valor = cell.getStringCellValue();
                            } catch (IllegalStateException e) {
                                valor = BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
                            }
                            break;
                        default:
                            valor = "";
                    }
                    // Limpieza general
                    // sin caracteres invisibles
                    valor = valor.trim().replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
                    // longitud máxima de respaldo
                    valor = valor.length() > 255 ? valor.substring(0, 255) : valor;
                    columnas.add(valor);
                }
                lista.add(columnas.toArray(new String[0]));
            }
        }
        workbook.close();
        return lista;
    }
}
