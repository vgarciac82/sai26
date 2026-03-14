package com.syc.contable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import com.axtel.egresos.core.MasiveOperation;
import com.syc.contable.core.RelacionGastosManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.ejercido.pagado.CierrePresupuestal;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelacionGastosBussinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(AdecuacionBusinessLogic.class);

    private String folioGenerator;

    private static String jniName = "jdbc/gestion";

    private CierrePresupuestal cpAMF;

    public RelacionGastosBussinessLogic(String jniName, String folioGenerator) {
        super.init(jniName);
        this.folioGenerator = folioGenerator;
        cpAMF = new CierrePresupuestal(jniName);
    }

    public String getUE(String sCB) throws Exception {
        PreparedStatement pstmntH = null;
        Connection conn = null;
        ResultSet rs3 = null;
        try {
            conn = getConnection();
            String sRet = "";
            String Sql3 = " select strUnidadEjecutora from [tUECuentasBancarias] where strclabe = '" + sCB + "'";
            pstmntH = conn.prepareStatement(Sql3);
            rs3 = pstmntH.executeQuery();
            if (rs3.next()) {
                sRet = rs3.getString(1);
            }
            return sRet;
        } finally {
            CloseObject.closeObject(rs3);
            CloseObject.closeObject(conn);
        }
    }

    public ArrayList<String> buscaCompromisos(String listaFolios, String listaCuentaBancaria, String listaFechas, String ListaLeyendas, String sUsuario) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaComp = RelacionGastosManager.BuscaCompromisos(conn, listaFolios, listaCuentaBancaria, listaFechas, ListaLeyendas, sUsuario);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                // e.printStackTrace();
                conn.rollback();
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return arrListaComp;
    }

    public ArrayList<String> buscaRelacionGastosIntegrados(String listaFolios, String listaCuentaBancaria, String sLeyenda, Usuario usuario, String pTimeStamp) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaComp = RelacionGastosManager.buscaRelacionGastosIntegrados(conn, listaFolios, listaCuentaBancaria, sLeyenda, usuario, pTimeStamp, folioGenerator);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                log.error(e.getMessage(), e);
                try {
                    conn.rollback();
                } catch (Exception e3) {
                    log.error("Problemas haciendo rollback " + e3, e3);
                }
                throw new GestionException(e.getMessage());
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            CloseObject.closeObject(conn);
        }
        return arrListaComp;
    }

    public boolean ActualizaStatus(String listaFolios) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            RelacionGastosManager.UpdateStatus(conn, listaFolios);
        } catch (SQLException e) {
            if (conn != null) {
                // e.printStackTrace();
                conn.rollback();
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return true;
    }

    public boolean ActualizaStatusPagoDiversoRelGastos(String listaFolios) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            RelacionGastosManager.UpdateStatusPagoDiversoRelGastos(conn, listaFolios);
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return true;
    }

    public ArrayList<String> ArmaDocumentoComprobatorio(String listaIds, String sTimeStamp, boolean bIntegra) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaComp = RelacionGastosManager.CreaDocumentacionComprobatoria(conn, listaIds, sTimeStamp, bIntegra);
            RelacionGastosManager.updateHeaderCompromisos(conn, listaIds);
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return arrListaComp;
    }

    public boolean actualizaCompromisos(Integer nEnviadoSICOP, String caNoCompromiso) throws Exception {
        boolean regActualizado = false;
        Connection conn = null;
        try {
            conn = getConnection();
            regActualizado = RelacionGastosManager.updateHeaderCompromisosRealimentacion(conn, nEnviadoSICOP, caNoCompromiso);
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                throw new GestionException(e.getMessage());
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return regActualizado;
    }

    public boolean insertaLineaLayout(String clave, String cRamo, String cUnidadResponsable, String folioSICOP, String idProceso, String cCentroContable, java.sql.Date fExpedicion, float total, String cTipoPoliza, String nFolioPoliza, String nPolizaCancelacion, String tipoMovimiento, String origenPresupuesto, String cuentaBancaria, String noSolicitud, String tCambio, String tMoneda, String tSolicitud, String volante, String rfc, String caNoCompromiso, String codSemarnat2, String estatus, java.sql.Date fAplicacion, String documento, String nDocumento, String descripcion) throws SQLException {
        boolean regInsertado = false;
        Connection conn = null;
        try {
            conn = getConnection();
            regInsertado = RelacionGastosManager.insertRegisterLayout(conn, clave, cRamo, cUnidadResponsable, folioSICOP, idProceso, cCentroContable, fExpedicion, total, cTipoPoliza, nFolioPoliza, nPolizaCancelacion, tipoMovimiento, origenPresupuesto, cuentaBancaria, noSolicitud, tCambio, tMoneda, tSolicitud, volante, rfc, caNoCompromiso, codSemarnat2, estatus, fAplicacion, documento, nDocumento, descripcion);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return regInsertado;
    }

    public List<Integer> obtenFoliosCargaMasiva(int folioCargaMasiva) throws Exception {
        List<Integer> result = null;
        Connection conn = null;
        try {
            conn = getConnection();
            result = RelacionGastosManager.obtenFoliosCargaMasiva(conn, folioCargaMasiva);
            return result;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public int actualizaFirmantesCargaMasiva(int nFolioCargaMasiva, String[] firmanteElabora, String[] firmanteVoBo, String[] firmanteAutoriza, String[] firmanteDelegatorio) throws Exception {
        int actualizados = 0;
        Connection conn = null;
        try {
            List<Integer> foliosCargados = obtenFoliosCargaMasiva(nFolioCargaMasiva);
            if (foliosCargados != null) {
                conn = getConnection();
                actualizados = RelacionGastosManager.actualizaFirmantesCargaMasiva(conn, foliosCargados, firmanteElabora, firmanteVoBo, firmanteAutoriza, firmanteDelegatorio);
                conn.commit();
            }
            return actualizados;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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

    public void exportaSolicitudesMasiva(String reportPath, String fileName, int nFolioCargaMasiva, OutputStream out) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            RelacionGastosManager.exportaSolicitudesMasiva(conn, reportPath, fileName, nFolioCargaMasiva, out);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public File generaLayoutRelacionGastosCompromiso(Usuario usuario, String caNoCompromiso) throws Exception {
        Connection conn = null;
        File layout = null;
        try {
            conn = getConnection();
            layout = RelacionGastosManager.generaLayoutCompromisoRG(conn, usuario, caNoCompromiso, folioGenerator);
            conn.commit();
            return layout;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Error occurred", "Error realizando rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void insertaTablasRelacionGastosCompromiso(Usuario usuario, String caNoCompromiso) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            RelacionGastosManager.insertaTablasCompromisoRG(conn, usuario, caNoCompromiso, folioGenerator);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Error occurred", "Error realizando rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public File generaDocComprobatoria(Usuario usuario, String caNoCompromiso) throws Exception {
        Connection conn = null;
        File layout = null;
        try {
            conn = getConnection();
            String integradora = RelacionGastosManager.buscaIntegradora(conn, caNoCompromiso);
            layout = RelacionGastosManager.generaDocComp(conn, integradora);
            conn.commit();
            return layout;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Error occurred", "Error realizando rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void exportaSolicitudesMasivaPendientesSICOP(HttpServletResponse resp, int nFolioCargaMasiva, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            file = RelacionGastosManager.ReporteAlimentacion(conn, nFolioCargaMasiva, plantilla);
            conn.commit();
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

    public String enviarSICOPSolicitudesMasiva(int nFolioCargaMasiva) throws Exception {
        int actualizados = 0;
        String msgResult = null;
        Connection conn = null;
        try {
            conn = getConnection();
            actualizados = RelacionGastosManager.actualizaSICOPCargaMasiva(conn, nFolioCargaMasiva);
            msgResult = "Se actualizaron exitosamente " + actualizados + " solicitudes de pago de la carga masiva " + nFolioCargaMasiva;
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas con rollback: " + e2);
                }
        } finally {
            CloseObject.closeObject(conn);
        }
        return msgResult;
    }

    public String rechazoSICOPSolicitudesMasiva(int nFolioCargaMasiva, String tipoDoc, String nFolio, Usuario usuario) throws Exception {
        int actualizados = 0;
        String msgResult = null;
        Connection conn = null;
        boolean autorizadoPorFiel = false;
        try {
            conn = getConnection();
            JSONObject resJson = cpAMF.cancelaDevengado(conn, tipoDoc, nFolio, usuario, autorizadoPorFiel);
            actualizados = RelacionGastosManager.actualizaSICOPRechazo(conn, nFolioCargaMasiva);
            msgResult = "Se rechazaron exitosamente " + actualizados + " solicitudes de pago de la carga masiva " + nFolioCargaMasiva;
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas con rollback: " + e2);
                }
        } finally {
            CloseObject.closeObject(conn);
        }
        return msgResult;
    }

    public String actualizaFirmantes(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws Exception {
        String msgResult = "";
        try {
            RelacionGastosBussinessLogic rgbl = new RelacionGastosBussinessLogic(jniName, null);
            int nFolioCargaMasiva = Integer.parseInt(req.getParameter("nFolioCargaMasiva"));
            String nombreVo = new String((StringUtils.isEmpty(req.getParameter("cNombreVo")) ? "" : req.getParameter("cNombreVo")).getBytes("ISO-8859-1"), "UTF-8");
            String paternoVo = new String((StringUtils.isEmpty(req.getParameter("cPaternoVo")) ? "" : req.getParameter("cPaternoVo")).getBytes("ISO-8859-1"), "UTF-8");
            String maternoVo = new String((StringUtils.isEmpty(req.getParameter("cMaternoVo")) ? "" : req.getParameter("cMaternoVo")).getBytes("ISO-8859-1"), "UTF-8");
            String firmanteVoBoStr = nombreVo + " " + paternoVo + " " + maternoVo;
            String nombreA = new String((StringUtils.isEmpty(req.getParameter("cNombreA")) ? "" : req.getParameter("cNombreA")).getBytes("ISO-8859-1"), "UTF-8");
            String paternoA = new String((StringUtils.isEmpty(req.getParameter("cPaternoA")) ? "" : req.getParameter("cPaternoA")).getBytes("ISO-8859-1"), "UTF-8");
            String maternoA = new String((StringUtils.isEmpty(req.getParameter("cMaternoA")) ? "" : req.getParameter("cMaternoA")).getBytes("ISO-8859-1"), "UTF-8");
            String firmanteAutorizaStr = nombreA + " " + paternoA + " " + maternoA;
            String nombreE = new String((StringUtils.isEmpty(req.getParameter("cNombreE")) ? "" : req.getParameter("cNombreE")).getBytes("ISO-8859-1"), "UTF-8");
            String paternoE = new String((StringUtils.isEmpty(req.getParameter("cPaternoE")) ? "" : req.getParameter("cPaternoE")).getBytes("ISO-8859-1"), "UTF-8");
            String maternoE = new String((StringUtils.isEmpty(req.getParameter("cMaternoE")) ? "" : req.getParameter("cMaternoE")).getBytes("ISO-8859-1"), "UTF-8");
            String firmanteElaboraStr = nombreE + " " + paternoE + " " + maternoE;
            String puestoVoBo = new String((StringUtils.isEmpty(req.getParameter("cPuestoVo")) ? "" : req.getParameter("cPuestoVo")).getBytes("ISO-8859-1"), "UTF-8");
            String puestoAuto = new String((StringUtils.isEmpty(req.getParameter("cPuestoA")) ? "" : req.getParameter("cPuestoA")).getBytes("ISO-8859-1"), "UTF-8");
            String puestoElab = new String((StringUtils.isEmpty(req.getParameter("cPuestoE")) ? "" : req.getParameter("cPuestoE")).getBytes("ISO-8859-1"), "UTF-8");
            String[] firmanteVoBo = new String[] { firmanteVoBoStr, puestoVoBo };
            String[] firmanteAutoriza = new String[] { firmanteAutorizaStr, puestoAuto };
            String[] firmanteElabora = new String[] { firmanteElaboraStr, puestoElab };
            String[] firmanteDelegatorio = null;
            boolean contieneOficioDelegatorio = "true".equals(req.getParameter("esOficioDelegatorio"));
            if (contieneOficioDelegatorio) {
                String nombreFirmanteDelegatorioStr = new String(StringUtils.trimToEmpty(req.getParameter("cNombreTitularAux")).getBytes(), "UTF-8");
                String aPaternoFirmanteDelegatorioStr = new String(StringUtils.trimToEmpty(req.getParameter("cApellidoPaternoTitularAux")).getBytes(), "UTF-8");
                String aMaternoFirmanteDelegatorioStr = new String(StringUtils.trimToEmpty(req.getParameter("cApellidoMaternoTitularAux")).getBytes(), "UTF-8");
                String folioDocto = new String(StringUtils.trimToEmpty(req.getParameter("cFolioOficioAux")).getBytes(), "UTF-8");
                String fechaDocto = new String(StringUtils.trimToEmpty(req.getParameter("dFechaOficioAux")).getBytes(), "UTF-8");
                String puestoFirmante = new String(StringUtils.trimToEmpty(req.getParameter("cPuestoTitularAux")).getBytes(), "UTF-8");
                String tipoSuplencia = req.getParameter("tipoSuplenciaAux");
                firmanteDelegatorio = new String[] { folioDocto, fechaDocto, nombreFirmanteDelegatorioStr, aPaternoFirmanteDelegatorioStr, aMaternoFirmanteDelegatorioStr, puestoFirmante, tipoSuplencia };
            }
            int actualizados = rgbl.actualizaFirmantesCargaMasiva(nFolioCargaMasiva, firmanteElabora, firmanteVoBo, firmanteAutoriza, firmanteDelegatorio);
            msgResult = "Se actualizaron exitosamente " + actualizados + " solicitudes de pago de la carga masiva " + nFolioCargaMasiva;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            msgResult = "No fue posible actualizar la informacion. Ocurrio el siguiente error: " + e;
        }
        return msgResult;
    }

    public List<MasiveOperation> readRGMasivaTemp(int folioGeneral) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            return RelacionGastosManager.readRGMasiva(conn, folioGeneral);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public List<?> readRGMasiva(int folioGeneral) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            return RelacionGastosManager.readRGMasivaAplicada(conn, folioGeneral);
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
