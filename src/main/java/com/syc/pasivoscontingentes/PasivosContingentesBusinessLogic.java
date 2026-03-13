package com.syc.pasivoscontingentes;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.syc.contable.core.SolicitudPOLIZAFirmaElectronica;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.polizamanual.controller.GeneradorPolizaManualManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.firmaElectronica.core.FirmaElectronicaManager;
import com.syc.sai.firmaElectronica.exceptions.FirmaElectronicaException;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasivosContingentesBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(PasivosContingentesBusinessLogic.class);

    private Usuario u;

    public PasivosContingentesBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public void generaCedula(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String mesS = req.getParameter("mes");
            String anio = req.getParameter("aEjercicioFiscal");
            file = PasivosContingentesManager.PasivoContingenteManager(conn, mesS, anio, plantilla);
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

    public int generaPoliza(String fAplicacion, String cUnidadEjecutora, String cCentroContable, String uLogin, String uNombre, String reportPath, Usuario u) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            int nFolioPoliza = GeneradorPolizaManualManager.generaPolizaPasivoCont(conn, fAplicacion, cUnidadEjecutora, cCentroContable, uLogin, uNombre);
            int mes = Integer.parseInt(fAplicacion.substring(3, 5));
            int anio = Integer.parseInt(fAplicacion.substring(6, 10));
            if (mes == 12) {
                int total = PasivosContingentesManager.ValidaExistencia(conn, anio);
                if (total != 0) {
                    PasivosContingentesManager.LimpiaPasivos(conn, anio);
                    PasivosContingentesManager.PasivosEjerciciosAnteriores(conn, anio);
                } else if (total == 0) {
                    PasivosContingentesManager.PasivosEjerciciosAnteriores(conn, anio);
                }
            }
            enviaFIEL(conn, nFolioPoliza, reportPath, u);
            conn.commit();
            return nFolioPoliza;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Throwable t) {
                    log.warn("Problemas realizando rollback: " + t);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public int generaPolizaAlta(String fAplicacion, String cUnidadEjecutora, String cCentroContable, String uLogin, String uNombre, String RFC, String PC, String reportPath, Usuario u) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            int nFolioPoliza = GeneradorPolizaManualManager.generaPolizaPasivoContAlta(conn, fAplicacion, cUnidadEjecutora, cCentroContable, uLogin, uNombre, RFC, PC);
            enviaFIEL(conn, nFolioPoliza, reportPath, u);
            conn.commit();
            return nFolioPoliza;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Throwable t) {
                    log.warn("Problemas realizando rollback: " + t);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public int generaPolizaBaja(String fAplicacion, String cUnidadEjecutora, String cCentroContable, String uLogin, String uNombre, String RFC, String PC, Usuario u, String reportPath) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            int nFolioPoliza = GeneradorPolizaManualManager.generaPolizaPasivoContBaja(conn, fAplicacion, cUnidadEjecutora, cCentroContable, uLogin, uNombre, RFC, PC);
            PasivosContingentesManager.bajaPasivo(conn, RFC, PC, fAplicacion);
            enviaFIEL(conn, nFolioPoliza, reportPath, u);
            conn.commit();
            return nFolioPoliza;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Throwable t) {
                    log.warn("Problemas realizando rollback: " + t);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void generaCedulaLaboral(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String mesS = req.getParameter("mes");
            String anio = req.getParameter("aEjercicioFiscal");
            file = PasivosContingentesManager.PasivosContingentesLaboral(conn, mesS, anio, plantilla);
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

    public int generaPolizaLaboral(String fAplicacion, String cUnidadEjecutora, String cCentroContable, String uLogin, String uNombre, String reportPath, Usuario u) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            int nFolioPoliza = GeneradorPolizaManualManager.generaPolizaPasivoContLaboral(conn, fAplicacion, cUnidadEjecutora, cCentroContable, uLogin, uNombre);
            enviaFIEL(conn, nFolioPoliza, reportPath, u);
            conn.commit();
            return nFolioPoliza;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Throwable t) {
                    log.warn("Problemas realizando rollback: " + t);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void generaCedualAntiguedad(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla, String unidad) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String mesS = req.getParameter("mes");
            String anio = req.getParameter("aEjercicioFiscal");
            file = PasivosContingentesManager.PasivosContingentesAntiguedad(conn, mesS, anio, plantilla, unidad);
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

    public void generaCedualTrimestral(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla, String unidad) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String mesS = req.getParameter("mes");
            String anio = req.getParameter("aEjercicioFiscal");
            file = PasivosContingentesManager.PasivosContingentesTrimestral(conn, mesS, anio, plantilla, unidad);
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

    public String readSubcuenta() throws SQLException {
        String subcuenta = "";
        Connection conn = null;
        try {
            conn = getConnection();
            subcuenta = PasivosContingentesManager.obtenSubcuentaPC(conn);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return subcuenta;
    }

    public double validaSaldo(String RFC, String PC, String mes, String tipoPasivo) throws Exception {
        Connection conn = null;
        int mesInt = Integer.parseInt(mes);
        double saldo;
        try {
            conn = getConnection();
            saldo = PasivosContingentesManager.validarSaldo(conn, RFC, PC, mesInt, tipoPasivo);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return saldo;
    }

    public void desactivarPasivo(String RFC, String PC, String fAplicacion) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            PasivosContingentesManager.bajaPasivo(conn, RFC, PC, fAplicacion);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Throwable t) {
                    log.warn("Problemas realizando el rollback: " + t);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void generaCedualAntiguedadExpediente(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla, String unidad) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String mesS = req.getParameter("mes");
            String anio = req.getParameter("aEjercicioFiscal");
            file = PasivosContingentesManager.PasivosContingentesAntiguedadExpediente(conn, mesS, anio, plantilla, unidad);
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

    public void generaCedualAntiguedadExpedienteConsolidada(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantillas, String unidad) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String mesS = req.getParameter("mes");
            String anio = req.getParameter("aEjercicioFiscal");
            file = PasivosContingentesManager.PasivosContingentesAntiguedadExpedienteConsolidado(conn, mesS, anio, plantillas, unidad);
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

    private void enviaFIEL(Connection conn, int nFolioPoliza, String reportPath, Usuario u) throws Exception {
        SolicitudFirmaElectronica solicitudPagoPrinter = new SolicitudPOLIZAFirmaElectronica();
        solicitudPagoPrinter.setDetail("tDocPolizaDetalle");
        solicitudPagoPrinter.setDocument("POLIZA");
        solicitudPagoPrinter.setField("nFolioDocPoliza");
        solicitudPagoPrinter.setFileExtension("pdf");
        solicitudPagoPrinter.setHeader("tDocPolizaEncabezado");
        solicitudPagoPrinter.setIdField(nFolioPoliza);
        solicitudPagoPrinter.setReportPath(reportPath);
        solicitudPagoPrinter.setUsuario(u);
        solicitudPagoPrinter.setDocName("Poliza Firmada");
        FirmaElectronicaManager.generaArchivoFirma(conn, solicitudPagoPrinter, "Poliza Firmada", false);
        solicitudPagoPrinter.notificaOperacionPendiente(conn, SolicitudFirmaElectronica.VO_BO);
        FirmaElectronicaManager.avanzaEstatusSICOP(conn, solicitudPagoPrinter, SolicitudFirmaElectronica.VO_BO_SICOP);
    }
}
