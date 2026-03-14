package com.syc.sai.procesosAutomaticos;

import java.sql.Connection;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import com.syc.contable.core.ConciliacionBancoFirmada;
import com.syc.contable.core.ConciliacionBancoFirmadaDetalle;
import com.syc.contable.core.ConciliacionContable;
import com.syc.contable.core.ConciliacionContableDetalle;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.ejercido.pagado.CLCAttachmentBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.utils.pdf.PDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadConciliacionBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(UploadConciliacionBusinessLogic.class);

    private static ConfiguraAplicativoBusinessLogic cabl = null;

    private static FolioGeneratorInterface fg = null;

    private static CLCAttachmentBusinessLogic clcabl = null;

    public UploadConciliacionBusinessLogic(String jniName, String folioGenerator) throws Exception {
        super.init(jniName);
        synchronized (this) {
            if (cabl == null)
                cabl = new ConfiguraAplicativoBusinessLogic(jniName);
            if (clcabl == null)
                clcabl = new CLCAttachmentBusinessLogic(jniName);
            if (fg == null) {
                if (!StringUtils.isEmpty(folioGenerator)) {
                    ClassLoader cl = getClass().getClassLoader();
                    Class<?> clase = cl.loadClass(folioGenerator);
                    fg = (FolioGeneratorInterface) clase.newInstance();
                }
            }
        }
    }

    public String uploadCB(Usuario u, String nombreCarpeta, String nombreDestino, String ctaBan, int nMes) {
        Connection conn = null;
        Caso c = null;
        log.info("Object: {}", "Iniciando el proceso de ajuntar conciliacion bancaria en la carpeta[" + nombreCarpeta + "] Archivo[" + nombreDestino + "] Cuenta [" + ctaBan + "] Mes[" + nMes + "]");
        String msg = "";
        try {
            conn = getConnection();
            String centroContable = u.getPropiedad("CCENTROCONTABLE").getValor();
            boolean esEdoCta = "Estado de Cuenta".equalsIgnoreCase(nombreCarpeta);
            if (!UploadConciliacionManager.existeConcilicacion(conn, ctaBan, centroContable)) {
                c = UploadConciliacionManager.creaConciliacion(conn, u, Integer.parseInt(cabl.getSystemSetting("CARGA_CB_IDTC")), ctaBan, centroContable, fg);
            } else {
                c = UploadConciliacionManager.readCasoOrigen(conn, ctaBan, centroContable);
            }
            ConciliacionBancoFirmada cbf = UploadConciliacionManager.read(conn, c.getFolio());
            if (!UploadConciliacionManager.existeMesConciliacion(cbf, nMes, esEdoCta)) {
                ConciliacionBancoFirmadaDetalle detalle = new ConciliacionBancoFirmadaDetalle(cbf.getEncabezado().getFolioConciliacionFirmada(), nMes, u.getLogin(), esEdoCta);
                if (esEdoCta) {
                    if ("012320001204605677".equalsIgnoreCase(ctaBan)) {
                        SaldosEdoCta saldo = leeSaldosPDFBBVA(nombreDestino);
                        detalle.setSaldo(saldo);
                    } else {
                        SaldosEdoCta saldo = leeSaldosPDF(nombreDestino);
                        detalle.setSaldo(saldo);
                    }
                }
                UploadConciliacionManager.insertaDetalle(conn, detalle);
                cbf.getDetalle().add(detalle);
                String nombreMes = Util.NOMBRE_MESES_MX[nMes - 1];
                clcabl.attachDocumentoConciliacion(conn, nombreDestino, nombreCarpeta, nombreMes, c.getFolio(), true);
                msg = "Archivo cargado exitosamente.";
            } else {
                msg = (esEdoCta ? " El estado de cuenta " : " La conciliacion bancaria ") + "ya existe. Se ignora";
            }
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            msg = "Error adjuntando conciliacion bancaria firmada: " + e.toString();
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
        } finally {
            CloseObject.closeObject(conn);
        }
        return msg;
    }

    public boolean versionMes(int idGabinete, String nombreMes, String nombreCarpeta) throws Exception {
        Connection conn = null;
        int nMes = Util.numeroDeMes(nombreMes.trim().toUpperCase()) + 1;
        try {
            conn = getConnection();
            ConciliacionBancoFirmada cbf = UploadConciliacionManager.read(conn, idGabinete);
            boolean esEdoCta = "Estado de Cuenta".equalsIgnoreCase(nombreCarpeta);
            if (cbf != null && UploadConciliacionManager.existeMesConciliacion(cbf, nMes, esEdoCta))
                UploadConciliacionManager.borraConciliacionMes(conn, cbf, nMes, esEdoCta);
            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas con rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public static String limpiarCaracteres(String input) {
        String cadena = "";
        for (int i = 0; i < input.length(); i++) {
            char character = input.charAt(i);
            int ascii = (int) character;
            if (ascii < 128)
                cadena = cadena + character;
        }
        return cadena;
    }

    public SaldosEdoCta leeSaldosPDF(String pdfPath) throws Exception {
        PDF pdf = new PDF();
        SaldosEdoCta saldoEdoCta = null;
        try {
            String text = pdf.load(pdfPath).extractText(1, 1);
            String regexSaldo = "SALDO FINAL\\s*\\$\\s*([\\d,\\.]+)";
            Pattern pSaldo = Pattern.compile(regexSaldo);
            Matcher mSaldo = pSaldo.matcher(text);
            Locale locale = new Locale("es", "MX");
            NumberFormat nf = NumberFormat.getInstance(locale);
            if (mSaldo.find()) {
                String saldoStr = mSaldo.group(1);
                log.debug("Object: {}", "Saldo Final: " + saldoStr);
                double saldo = nf.parse(saldoStr).doubleValue();
                log.info("Object: {}", "Saldo Final: " + saldo);
                saldoEdoCta = new SaldosEdoCta(saldo, saldo);
            } else {
                throw new RuntimeException("No se encontro el saldo. Se espera la palabra SALDO FINAL en el pdf");
            }
            return saldoEdoCta;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            pdf.close();
        }
    }

    public SaldosEdoCta leeSaldosPDFBBVA(String pdfPath) throws Exception {
        PDF pdf = new PDF();
        SaldosEdoCta saldoEdoCta = null;
        try {
            String text = pdf.load(pdfPath).extractText(1, 1);
            String regexSaldo = "Saldo Final \\(\\+\\)\\s*([\\d,\\.]+)";
            Pattern pSaldo = Pattern.compile(regexSaldo);
            Matcher mSaldo = pSaldo.matcher(text);
            Locale locale = new Locale("es", "MX");
            NumberFormat nf = NumberFormat.getInstance(locale);
            if (mSaldo.find()) {
                String saldoStr = mSaldo.group(1);
                log.debug("Object: {}", "Saldo Final: " + saldoStr);
                double saldo = nf.parse(saldoStr).doubleValue();
                log.info("Object: {}", "Saldo Final: " + saldo);
                saldoEdoCta = new SaldosEdoCta(saldo, saldo);
            } else {
                throw new RuntimeException("No se encontro el saldo. Se espera la palabra SALDO FINAL en el pdf");
            }
            return saldoEdoCta;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            pdf.close();
        }
    }

    public String uploadConciliacion(Usuario u, String nombreCarpeta, String nombreDestino, int nMes, int conciliacion) {
        Connection conn = null;
        Caso c = null;
        log.info("Object: {}", "Iniciando el proceso de ajuntar conciliacion contable en la carpeta[" + nombreCarpeta + "] Archivo[" + nombreDestino + "] Mes[" + nMes + "] Tipo[" + conciliacion + "]");
        String msg = "";
        try {
            conn = getConnection();
            String centroContable = u.getPropiedad("CCENTROCONTABLE").getValor();
            boolean esEdoCta = "Estado de Cuenta".equalsIgnoreCase(nombreCarpeta);
            if (!UploadConciliacionManager.existeConcilicacionContableCC(conn, centroContable)) {
                c = UploadConciliacionManager.creaConciliacionContable(conn, u, Integer.parseInt(cabl.getSystemSetting("CARGA_CC_IDTC")), centroContable, fg);
            } else {
                c = UploadConciliacionManager.readCasoOrigenConciliacionContable(conn, centroContable);
            }
            ConciliacionContable conCon = UploadConciliacionManager.readConciliacionContable(conn, c.getFolio());
            if (!UploadConciliacionManager.existeMesConciliacion(conCon, nMes, conciliacion)) {
                ConciliacionContableDetalle detalle = new ConciliacionContableDetalle(conCon.getEncabezado().getfolioConciliacionContable(), nMes, u.getLogin(), conciliacion);
                UploadConciliacionManager.insertaDetalle(conn, detalle);
                conCon.getDetalle().add(detalle);
                String nombreMes = Util.NOMBRE_MESES_MX[nMes - 1];
                clcabl.attachDocumentoConciliacion(conn, nombreDestino, nombreCarpeta, nombreMes, c.getFolio(), true);
                msg = "Archivo cargado exitosamente.";
            } else {
                msg = (esEdoCta ? " El estado de cuenta " : " La conciliacion bancaria ") + "ya existe. Se ignora";
            }
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            msg = "Error adjuntando conciliacion bancaria firmada: " + e.toString();
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
        } finally {
            CloseObject.closeObject(conn);
        }
        return msg;
    }
}
