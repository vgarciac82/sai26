package com.syc.egresos.core.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import com.axtel.egresos.entities.EgresoExcedeUMA;
import com.axtel.egresos.exceptions.EgresoException;
import com.axtel.egresos.viaticos.Agenda;
import com.axtel.egresos.viaticos.Comision;
import com.syc.cfdi.core.FacturaManager;
import com.syc.cfdi.db.CloseObject;
import com.syc.egresos.core.Amortizacion;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class EgresoRELACIONGASTOSEncabezado extends EgresoEncabezado {

    private static final Logger log = LoggerFactory.getLogger(EgresoRELACIONGASTOSEncabezado.class);

    private String folioRelacionGastos;

    StringBuilder queryIns = new StringBuilder();

    private String referenciaBancaria;

    private String IdDistritoRiego;

    private int IdEstadoRelacion;

    private String IdGEstatal;

    private String IdGRegional;

    private String IdRelacion;

    private String IdTipoRelacion;

    private String IdUnidadAdministrativa;

    private String InformeComision;

    private String ContrarreciboImpreso;

    private String SuficienciaAnualValidada;

    private String SuficienciaMensualValidada;

    private BigDecimal MontoBoleto;

    private int Acompanantes;

    private int ContieneFacturas;

    private int EsAlimentacionBrigadistas;

    private int EsCertificadoTransito;

    private int EsComisionExtranjero;

    private int EsComisionNacional;

    private String Evento;

    private String IdTipoLimiteDlls;

    private String cIdTipoMontoDesembolso;

    private String AplicaImpuestoCedular;

    private int FolioCaja;

    private int FolioCargaMasiva;

    private int IdComision;

    private int IdComisionReloj;

    {
        queryIns.append("INSERT INTO tRelacionGastosEncabezado(");
        queryIns.append("cEjercicio,cEsFirmaElectronica,cIdDistritoRiego,cIdEntidadContable,");
        queryIns.append("cIdEstadoRelacion,cIdGEstatal,cIdGRegional,cIdRelacion,cIdRFC,");
        queryIns.append("cIdTipoDocumento,cIdTipoRelacion,cIdUnidadAdministrativa,cInformeComision,");
        queryIns.append("cUnidadResponsableContable,fRecepcion,lContrarreciboImpreso,lSuficienciaAnualValidada,");
        queryIns.append("lSuficienciaMensualValidada,mMontoBoleto,nAcompanantes,nContieneFacturas,nEsAlimentacionBrigadistas,");
        queryIns.append("nEsCertificadoTransito,nEsComisionExtranjero,nEsComisionNacional,nFolioRELACIONGASTOS,sFirmanteEla,");
        queryIns.append("sPuestoEla,aEjercicioFiscal,ALM,caNoContrarrecibo,");
        queryIns.append("capitulo,cCentroContable,cDescripcionPoliza,cDocumentoHaplicado,cEvento,cIdTipoFondo,cIdTipoLimiteDlls,");
        queryIns.append("cIdTipoMontoDesembolso,cIdUsuarioAprobacion,cIdUsuarioCaptura,cIdUsuarioImpresion,cIdUsuarioRechazo,cIdUsuarioRevision,");
        queryIns.append("cRamo,ID_DESTINO_GASTO,ID_TIPO_CONCEPTO,ID_TIPO_MOVIMIENTO,nNumEmpleadoAut,nNumEmpleadoElab,nNumEmpleadoVoBo,");
        queryIns.append("lAplicaImpuestoCedular,mImporteBruto,mImporteMasIva,mImporteNeto,mImporteRetencion,");
        queryIns.append("nEnviadoSICOP,cMes,cnombre,cRadicado,CTAB,cTipoPoliza,cUnidadResponsable,");
        queryIns.append("nFolioCaja,nFolioCargaMasiva,nFolioPoliza,nFolioPolizaCancelacion,nIdComision,nIdComisionReloj,");
        queryIns.append("fAplicacion,fCancelacion,fProgramadaPago,fRevision,");
        queryIns.append("RFC,sFirmanteAut,sFirmanteVoBo,sPuestoAut,sPuestoVoBo,");
        queryIns.append("TIPO_OPERACION,U_LOGIN,nIdConcepto,cConcepto)");
        queryIns.append(" VALUES(");
        queryIns.append("?,?,?,?,?,?,");
        queryIns.append("?,?,?,?,?,");
        queryIns.append("?,?,?,?,?,");
        queryIns.append("?,?,?,?,?,");
        queryIns.append("?,?,?,?,?,");
        queryIns.append("?,?,?,?,?,?,");
        queryIns.append("?,?,?,?,?,?,?,?,?,?,?,");
        queryIns.append("?,?,?,?,?,?,?,?,?,?,?,");
        queryIns.append("?,?,?,?,?,?,?,?,");
        queryIns.append("?,?,?,?,?,?,");
        queryIns.append("?,?,?,?,");
        queryIns.append("?,?,?,?,?,");
        queryIns.append("?,?,?,?,?)");
    }

    public EgresoRELACIONGASTOSEncabezado() {
        setTipoPago("RELACIONGASTOS");
    }

    @Override
    public int actualizaMontosRetencion(Connection conn) throws Exception {
        throw new Exception("Funcionalidad actualizaMontosRetencion no implementada.");
    }

    @Override
    public int actualizaRetencion(Connection conn, int idTipoRetencion, BigDecimal valorRetencion) throws Exception {
        throw new Exception("Funcionalidad actualizaRetencion no implementada.");
    }

    @Override
    public int avanzaEstatus(Connection conn) throws Exception {
        String query = "";
        query += "UPDATE	tRELACIONGASTOSEncabezado ";
        query += "   SET	nIDEstatus = nIDEstatus + 1 ";
        query += " WHERE	nFolioRELACIONGASTOS = ? ";
        PreparedStatement ps = null;
        int actualizados = 0;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, getFolioPago());
            actualizados = ps.executeUpdate();
            return actualizados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    @Override
    public EgresoEncabezado cargaEncabezado(HttpServletRequest req) throws Exception {
        return instanceFromRequest(req);
    }

    @Override
    public EgresoEncabezado cargaEncabezado(int folioEgreso) throws Exception {
        super.init(getJniName());
        EgresoRELACIONGASTOSEncabezado epde = null;
        Connection conn = null;
        try {
            conn = getConnection();
            //epde = PagosFederalizadoManager.cargaEncabezado( conn, folioEgreso );
            return epde;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public int delete(Connection conn) throws Exception {
        String query = "DELETE   FROM	tRELACIONGASTOSEncabezado  WHERE	nFolioRELACIONGASTOS = ? ";
        PreparedStatement ps = null;
        int afectados = 0;
        try {
            log.trace("Object: {}", "Iniciando eliminacion de la Relacion de gastos: " + getFolioPago());
            ps = conn.prepareStatement(query);
            ps.setInt(1, getFolioPago());
            afectados = ps.executeUpdate();
            log.trace("Object: {}", "Se eliminaron : " + afectados + " pagos con el folio: " + getFolioPago());
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    @Override
    public int eliminaRetencion(Connection conn, int idTipoRetencion) throws Exception {
        String query = "DELETE FROM tPagoRetencion WHERE nFolioPago = ? AND cIdTipoRetencion = ? and cTipoDocumento =? ";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, getFolioRelacionGastos());
            ps.setInt(2, idTipoRetencion);
            ps.setString(3, getTipoPago());
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    @Override
    public int generaRetenciones(Connection conn) throws Exception {
        throw new Exception("Funcionalidad generaRetenciones no implementada.");
    }

    @Override
    public String getNombreAnexo() {
        return "Anexo1.jasper";
    }

    @Override
    public String getNombreSolicitudPago() {
        return "PolizaPago.jasper";
    }

    @Override
    public String getPrefijoCR(Connection conn) throws Exception {
        String cxpPrefijo = ConfiguraAplicativoManager.getSystemSetting(conn, "CXP_PREFIJO");
        return cxpPrefijo;
    }

    public String getReferenciaBancaria() {
        return referenciaBancaria;
    }

    @Override
    public void rechazaPago(Connection conn, String motivoRechazo) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE t").append(getTipoPago()).append("Encabezado ");
        query.append("   SET cDocumentoHAplicado = 'C',");
        query.append("       nIDEstatus = '-1' ");
        query.append(" WHERE nFolio").append(getTipoPago()).append(" = ").append("?");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, getFolioPago());
            ps.executeUpdate();
            FacturaManager.eliminaFacturas(conn, getTipoPago(), String.valueOf(getFolioPago()));
            notificaRechazo(conn, motivoRechazo);
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    @Override
    public int save(Connection conn) throws EgresoException {
        PreparedStatement ps = null;
        try {
            int cnt = 1;
            ps = conn.prepareStatement(queryIns.toString());
            ps.setString(cnt++, getEjercicioFiscal());
            ps.setString(cnt++, String.valueOf(getEsFirmaElectronica()));
            ps.setString(cnt++, getIdDistritoRiego());
            ps.setString(cnt++, getIdGEstatal());
            ps.setInt(cnt++, getIdEstadoRelacion());
            ps.setString(cnt++, getIdGEstatal());
            ps.setString(cnt++, getIdGRegional());
            ps.setString(cnt++, getIdRelacion());
            ps.setString(cnt++, getRfc());
            ps.setString(cnt++, getIdTipoDocumento());
            ps.setString(cnt++, getIdTipoRelacion());
            ps.setString(cnt++, getIdUnidadAdministrativa());
            ps.setString(cnt++, getInformeComision());
            ps.setString(cnt++, getUnidadResponsableContable());
            ps.setDate(cnt++, java.sql.Date.valueOf(LocalDate.now()));
            ps.setString(cnt++, String.valueOf(getContrarreciboImpreso()));
            ps.setString(cnt++, String.valueOf(getSuficienciaAnualValidada()));
            ps.setString(cnt++, String.valueOf(getSuficienciaMensualValidada()));
            ps.setBigDecimal(cnt++, getMontoBoleto());
            ps.setInt(cnt++, getAcompanantes());
            ps.setInt(cnt++, getContieneFacturas());
            ps.setInt(cnt++, getEsAlimentacionBrigadistas());
            ps.setInt(cnt++, getEsCertificadoTransito());
            ps.setInt(cnt++, getEsComisionExtranjero());
            ps.setInt(cnt++, getEsComisionNacional());
            ps.setString(cnt++, getFolioRelacionGastos());
            ps.setString(cnt++, getFirmanteEla());
            ps.setString(cnt++, getPuestoEla());
            ps.setString(cnt++, getEjercicioFiscal());
            ps.setString(cnt++, getAlm());
            ps.setString(cnt++, getContrarecibo());
            ps.setString(cnt++, getCapitulo());
            ps.setString(cnt++, getCentroContable());
            ps.setString(cnt++, getDescripcionPoliza());
            ps.setString(cnt++, String.valueOf(getDocumentoAplicado()));
            ps.setString(cnt++, getEvento());
            ps.setString(cnt++, getIdTipoFondo());
            ps.setString(cnt++, getIdTipoLimiteDlls());
            ps.setString(cnt++, String.valueOf(getcIdTipoMontoDesembolso()));
            ps.setString(cnt++, getIdUsuarioAprobacion());
            ps.setString(cnt++, getIdUsuarioCaptura());
            ps.setString(cnt++, getIdUsuarioImpresion());
            ps.setString(cnt++, getIdUsuarioRechazo());
            ps.setString(cnt++, getIdUsuarioRevision());
            ps.setString(cnt++, getRamo());
            ps.setString(cnt++, getIdDestinoGasto());
            ps.setString(cnt++, getIdTipoConcepto());
            ps.setString(cnt++, getIdTipoMovimiento());
            ps.setInt(cnt++, getNumEmpleadoAut());
            ps.setInt(cnt++, getNumEmpleadoElab());
            ps.setInt(cnt++, getNumEmpleadoVoBo());
            ps.setString(cnt++, getAplicaImpuestoCedular());
            ps.setBigDecimal(cnt++, getImporteBruto());
            ps.setBigDecimal(cnt++, getImporteMasIva());
            ps.setBigDecimal(cnt++, getImporteNeto());
            ps.setBigDecimal(cnt++, getImporteRetencion());
            ps.setInt(cnt++, getEnviadoSICOP());
            ps.setString(cnt++, getMes());
            ps.setString(cnt++, getNombre());
            ps.setString(cnt++, String.valueOf('N'));
            ps.setString(cnt++, getCTAB());
            ps.setString(cnt++, getTipoPoliza());
            ps.setString(cnt++, getUnidadResponsable());
            ps.setInt(cnt++, getFolioCaja());
            ps.setInt(cnt++, getFolioCargaMasiva());
            ps.setInt(cnt++, getFolioPoliza());
            ps.setInt(cnt++, getFolioPolizaCancelacion());
            ps.setInt(cnt++, getIdComision());
            ps.setInt(cnt++, getIdComisionReloj());
            ps.setDate(cnt++, java.sql.Date.valueOf(LocalDate.now()));
            if (getFechaCancelacion() != null)
                ps.setDate(cnt++, new java.sql.Date(getFechaCancelacion().getTime()));
            else
                ps.setNull(cnt++, Types.DATE);
            if (getFechaProgramadaPago() != null)
                ps.setDate(cnt++, new java.sql.Date(getFechaProgramadaPago().getTime()));
            else
                ps.setNull(cnt++, Types.DATE);
            if (getFechaRevision() != null)
                ps.setDate(cnt++, new java.sql.Date(getFechaRevision().getTime()));
            else
                ps.setNull(cnt++, Types.DATE);
            ps.setString(cnt++, getRfc());
            ps.setString(cnt++, getFirmanteAut());
            ps.setString(cnt++, getFirmanteVoBo());
            ps.setString(cnt++, getPuestoAut());
            ps.setString(cnt++, getPuestoVoBo());
            ps.setString(cnt++, getIdTipoOperacion());
            ps.setString(cnt++, getLogin());
            ps.setString(cnt++, getIdConcepto());
            ps.setString(cnt++, getConcepto());
            log.debug("Object: " + String.valueOf(queryIns.toString()));
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new EgresoException(e);
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public String getInformeComision() {
        return InformeComision;
    }

    public void setInformeComision(String informeComision) {
        InformeComision = informeComision;
    }

    public String getContrarreciboImpreso() {
        return ContrarreciboImpreso;
    }

    public void setContrarreciboImpreso(String contrarreciboImpreso) {
        ContrarreciboImpreso = contrarreciboImpreso;
    }

    public BigDecimal getMontoBoleto() {
        return MontoBoleto;
    }

    public void setMontoBoleto(BigDecimal montoBoleto) {
        MontoBoleto = montoBoleto;
    }

    public int getAcompanantes() {
        return Acompanantes;
    }

    public void setAcompanantes(int acompanantes) {
        Acompanantes = acompanantes;
    }

    public int getContieneFacturas() {
        return ContieneFacturas;
    }

    public void setContieneFacturas(int contieneFacturas) {
        ContieneFacturas = contieneFacturas;
    }

    public int getEsAlimentacionBrigadistas() {
        return EsAlimentacionBrigadistas;
    }

    public void setEsAlimentacionBrigadistas(int esAlimentacionBrigadistas) {
        EsAlimentacionBrigadistas = esAlimentacionBrigadistas;
    }

    public String getEvento() {
        return Evento;
    }

    public void setEvento(String evento) {
        Evento = evento;
    }

    public String getIdTipoLimiteDlls() {
        return IdTipoLimiteDlls;
    }

    public void setIdTipoLimiteDlls(String idTipoLimiteDlls) {
        IdTipoLimiteDlls = idTipoLimiteDlls;
    }

    public String getcIdTipoMontoDesembolso() {
        return cIdTipoMontoDesembolso;
    }

    public void setcIdTipoMontoDesembolso(String cIdTipoMontoDesembolso) {
        this.cIdTipoMontoDesembolso = cIdTipoMontoDesembolso;
    }

    public String getAplicaImpuestoCedular() {
        return AplicaImpuestoCedular;
    }

    public void setAplicaImpuestoCedular(String aplicaImpuestoCedular) {
        AplicaImpuestoCedular = aplicaImpuestoCedular;
    }

    public int getFolioCaja() {
        return FolioCaja;
    }

    public void setFolioCaja(int folioCaja) {
        FolioCaja = folioCaja;
    }

    public int getFolioCargaMasiva() {
        return FolioCargaMasiva;
    }

    public void setFolioCargaMasiva(int folioCargaMasiva) {
        FolioCargaMasiva = folioCargaMasiva;
    }

    public int getIdComision() {
        return IdComision;
    }

    public void setIdComision(int idComision) {
        IdComision = idComision;
    }

    public int getIdComisionReloj() {
        return IdComisionReloj;
    }

    public void setIdComisionReloj(int idComisionReloj) {
        IdComisionReloj = idComisionReloj;
    }

    public int getEsCertificadoTransito() {
        return EsCertificadoTransito;
    }

    public void setEsCertificadoTransito(int esCertificadoTransito) {
        EsCertificadoTransito = esCertificadoTransito;
    }

    public int getEsComisionExtranjero() {
        return EsComisionExtranjero;
    }

    public void setEsComisionExtranjero(int esComisionExtranjero) {
        EsComisionExtranjero = esComisionExtranjero;
    }

    public int getEsComisionNacional() {
        return EsComisionNacional;
    }

    public void setEsComisionNacional(int esComisionNacional) {
        EsComisionNacional = esComisionNacional;
    }

    private EgresoEncabezado instanceFromRequest(HttpServletRequest request) throws Exception {
        EgresoEncabezado encabezado = new EgresoRELACIONGASTOSEncabezado();
        encabezado = super.readFromRequest(request, encabezado);
        ((EgresoRELACIONGASTOSEncabezado) encabezado).setFolioPago(Integer.parseInt(request.getParameter("nFolioPago")));
        ((EgresoRELACIONGASTOSEncabezado) encabezado).setRfc(request.getParameter("cIDRFC"));
        ((EgresoRELACIONGASTOSEncabezado) encabezado).setNombre(request.getParameter("cnombre"));
        ((EgresoRELACIONGASTOSEncabezado) encabezado).setFechaAplicacion(Util.stringToDate(request.getParameter("fechaAplicacion"), "dd/MM/yyyy"));
        ((EgresoRELACIONGASTOSEncabezado) encabezado).setCTAB(request.getParameter("CTAB"));
        ((EgresoRELACIONGASTOSEncabezado) encabezado).setIdTipoOperacion(request.getParameter("TIPO_OPERACION"));
        ((EgresoRELACIONGASTOSEncabezado) encabezado).setIdDestinoGasto(request.getParameter("DESTINO_GASTO"));
        ((EgresoRELACIONGASTOSEncabezado) encabezado).setImporteMasIva(new BigDecimal(request.getParameter("totalFactura")));
        ((EgresoRELACIONGASTOSEncabezado) encabezado).setImporteBruto(new BigDecimal(request.getParameter("mTotalFacturaV")));
        ((EgresoRELACIONGASTOSEncabezado) encabezado).setImporteIVA(new BigDecimal(request.getParameter("mImporteIVA")));
        Date fechaAp = new Date();
        fechaAp = ((EgresoRELACIONGASTOSEncabezado) encabezado).getFechaAplicacion();
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaAp);
        ((EgresoRELACIONGASTOSEncabezado) encabezado).setMes(String.valueOf(cal.get(Calendar.MONTH)));
        return encabezado;
    }

    public static EgresoEncabezado setInstanceRelacionGastos(HttpServletRequest req, Comision comision, Agenda agenda, Usuario u, String ejercicioFiscal) throws Exception {
        EgresoRELACIONGASTOSEncabezado encabezado = new EgresoRELACIONGASTOSEncabezado();
        BigDecimal importeEdicion = new BigDecimal(req.getParameter("importeEdicion") == "" || req.getParameter("importeEdicion") == null ? "0" : req.getParameter("importeEdicion"));
        BigDecimal noComprobables = new BigDecimal(req.getParameter("importenoComprobable") == "" || req.getParameter("importeEdicion") == null ? "0" : req.getParameter("importenoComprobable"));
        BigDecimal neto = new BigDecimal(req.getParameter("importeNeto"));
        if (importeEdicion.compareTo(BigDecimal.ZERO) > 0) {
            neto = noComprobables.add(importeEdicion);
        }
        encabezado.setImporteBruto(new BigDecimal(req.getParameter("mImporteSinIVA")));
        encabezado.setImporteMasIva(new BigDecimal(req.getParameter("mImporteBruto")));
        encabezado.setImporteNeto(neto);
        encabezado.setCTAB(comision.getCTAB());
        encabezado.setCentroContable(u.getPropiedad("CCENTROCONTABLE").getValor());
        encabezado.setConcepto(req.getParameter("concepto"));
        encabezado.setDescripcionPoliza(req.getParameter("concepto"));
        encabezado.setDocumentoAplicado("");
        encabezado.setEvento("CD_AL01");
        encabezado.setIdDistritoRiego("00");
        encabezado.setIdEstadoRelacion(0);
        encabezado.setIdGEstatal("00");
        encabezado.setIdGRegional("00");
        encabezado.setRfc(comision.getRFC());
        encabezado.setIdTipoDocumento("3");
        encabezado.setIdTipoFondo("0");
        encabezado.setIdTipoLimiteDlls("0");
        encabezado.setcIdTipoMontoDesembolso("0");
        encabezado.setIdTipoRelacion("T");
        encabezado.setIdUnidadAdministrativa("000");
        encabezado.setIdUsuarioAprobacion("0");
        encabezado.setIdUsuarioCaptura(req.getParameter("login"));
        encabezado.setIdUsuarioImpresion("0");
        encabezado.setIdUsuarioRechazo("0");
        encabezado.setIdUsuarioRevision("0");
        encabezado.setRadicado(false);
        encabezado.setRamo("16");
        encabezado.setUnidadResponsableContable("RHQ");
        encabezado.setUnidadResponsable(comision.getUnidadResponsable());
        encabezado.setIdTipoConcepto("0");
        encabezado.setIdTipoMovimiento("000");
        encabezado.setContrarreciboImpreso("0");
        encabezado.setSuficienciaAnualValidada("0");
        encabezado.setSuficienciaMensualValidada("0");
        encabezado.setMontoBoleto(new BigDecimal(0));
        encabezado.setAcompanantes(0);
        encabezado.setEnviadoSICOP(0);
        encabezado.setEsAlimentacionBrigadistas(0);
        encabezado.setEsCertificadoTransito(0);
        encabezado.setEsComisionExtranjero(0);
        encabezado.setEsComisionNacional(0);
        encabezado.setFolioCargaMasiva(0);
        encabezado.setFolioPoliza(0);
        encabezado.setIdConcepto("0");
        encabezado.setTipoCambio(new BigDecimal("1.00"));
        encabezado.setRfc(comision.getRFC());
        encabezado.setIdTipoOperacion("1");
        encabezado.setLogin(u.getLogin());
        encabezado.setIdComisionReloj(comision.getIdComision());
        encabezado.setEsFirmaElectronica((req.getParameter("esFirmaElectronica").charAt(0)));
        encabezado.setInformeComision(req.getParameter("informeComision"));
        encabezado.setNumEmpleadoAut(Integer.parseInt(req.getParameter("nombreAut")));
        encabezado.setNumEmpleadoElab(Integer.parseInt(req.getParameter("nEmpleadoElabora")));
        encabezado.setNumEmpleadoVoBo(Integer.parseInt(req.getParameter("nombreVoBo")));
        encabezado.setFirmanteEla(u.getNombre());
        encabezado.setPuestoEla("");
        encabezado.setImporteRetencion(new BigDecimal(req.getParameter("mImporteRetencion")));
        encabezado.setFolioRelacionGastos(req.getParameter("nFolioPago"));
        encabezado.setIdRelacion("VIATICOS" + (req.getParameter("nFolioPago")));
        encabezado.setEjercicioFiscal(ejercicioFiscal);
        encabezado.setFolioPago(Integer.parseInt(req.getParameter("nFolioPago")));
        encabezado.setIdTipoOperacion(req.getParameter("TIPO_OPERACION"));
        encabezado.setIdDestinoGasto(req.getParameter("DESTINO_GASTO"));
        return encabezado;
    }

    public void setReferenciaBancaria(String referenciaBancaria) {
        this.referenciaBancaria = referenciaBancaria;
    }

    @Override
    public Amortizacion getAmortizacion(Connection conn) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String> resumenConcepto(Connection conn) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String> resumenPago(Connection conn) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, String>> resumenRetenciones(Connection conn) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean retencionEliminable(Connection conn, int idRetencion) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    public String getFolioRelacionGastos() {
        return folioRelacionGastos;
    }

    public void setFolioRelacionGastos(String folioRelacionGastos) {
        this.folioRelacionGastos = folioRelacionGastos;
    }

    public String getIdDistritoRiego() {
        return IdDistritoRiego;
    }

    public void setIdDistritoRiego(String idDistritoRiego) {
        IdDistritoRiego = idDistritoRiego;
    }

    public int getIdEstadoRelacion() {
        return IdEstadoRelacion;
    }

    public void setIdEstadoRelacion(int idEstadoRelacion) {
        IdEstadoRelacion = idEstadoRelacion;
    }

    public String getIdGEstatal() {
        return IdGEstatal;
    }

    public void setIdGEstatal(String idGEstatal) {
        IdGEstatal = idGEstatal;
    }

    public String getIdGRegional() {
        return IdGRegional;
    }

    public void setIdGRegional(String idGRegional) {
        IdGRegional = idGRegional;
    }

    public String getIdRelacion() {
        return IdRelacion;
    }

    public void setIdRelacion(String idRelacion) {
        IdRelacion = idRelacion;
    }

    public String getIdTipoRelacion() {
        return IdTipoRelacion;
    }

    public void setIdTipoRelacion(String idTipoRelacion) {
        IdTipoRelacion = idTipoRelacion;
    }

    public String getIdUnidadAdministrativa() {
        return IdUnidadAdministrativa;
    }

    public void setIdUnidadAdministrativa(String idUnidadAdministrativa) {
        IdUnidadAdministrativa = idUnidadAdministrativa;
    }

    public String getSuficienciaAnualValidada() {
        return SuficienciaAnualValidada;
    }

    public void setSuficienciaAnualValidada(String suficienciaAnualValidada) {
        SuficienciaAnualValidada = suficienciaAnualValidada;
    }

    public String getSuficienciaMensualValidada() {
        return SuficienciaMensualValidada;
    }

    public void setSuficienciaMensualValidada(String suficienciaMensualValidada) {
        SuficienciaMensualValidada = suficienciaMensualValidada;
    }

    @Override
    public List<EgresoExcedeUMA> validaTopeUMASUnidad(Connection conn, String rfc2) {
        return new ArrayList<EgresoExcedeUMA>();
    }
}
