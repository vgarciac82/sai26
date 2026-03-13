package com.axtel.egresos.viaticos;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.axtel.egresos.viaticos.core.AgendaDAO;
import com.axtel.egresos.viaticos.core.ComisionDAO;
import com.axtel.egresos.viaticos.core.GeneraSolicitudViaticos;
import com.axtel.egresos.viaticos.core.TransporteDAO;
import com.axtel.egresos.viaticos.core.ViaticosDAO;
import com.syc.cfdi.core.FacturaManager;
import com.syc.dsmngr.DataSourceManager;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.core.impl.EgresoRELACIONGASTOSEncabezado;
import com.syc.egresos.firmante.FirmanteBussinessLogic;
import com.syc.egresos.firmante.core.FirmanteManager;
import com.syc.egresos.firmante.servlet.Firmante;
import com.syc.egresos.firmante.servlet.FirmanteSuplente;
import com.syc.ejercido.pagado.EgresosBusinessLogic;
import com.syc.fortimax.core.Carpeta;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionServlet;
import com.syc.gestion.util.Util;
import com.syc.implementacion.tesoreria.ComisionSinViaticos;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.sai.contabilidad.caja.CajaBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViaticosBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(EgresosBusinessLogic.class);

    private String jniName;

    private FirmanteBussinessLogic fbl = null;

    public static Agenda instanciaAgenda(String name) throws Exception {
        Agenda agenda = null;
        ClassLoader cl = Util.class.getClassLoader();
        Class<?> clase = cl.loadClass(name);
        agenda = (Agenda) clase.newInstance();
        return agenda;
    }

    public ViaticosBusinessLogic(String jniName) {
        super.init(jniName);
        this.jniName = jniName;
        this.fbl = new FirmanteBussinessLogic(jniName);
    }

    public ViaticosBusinessLogic() {
    }

    public Agenda cargaInstanciaAgenda(HttpServletRequest request, int folioAgenda) throws Exception {
        Agenda agenda = new Agenda();
        if ("EDITAR".equals(request.getParameter("tipoOperacion"))) {
            agenda.setIdComision(Integer.parseInt(request.getParameter("id")));
            agenda.setMotivoComision(request.getParameter("motivoComision"));
        } else if ("EDICION_SIN_FIRMAS".equals(request.getParameter("tipoOperacion"))) {
            agenda.setIdComision(Integer.parseInt(request.getParameter("cComision")));
            agenda.setMotivoComision(request.getParameter("motivoComision"));
        } else {
            agenda.setIdComision(Integer.parseInt(request.getParameter("idComision")));
            agenda.setMotivoComision(request.getParameter("concepto"));
        }
        agenda.setIdAgenda(folioAgenda);
        agenda.setFechaInicio(Util.stringToDate(request.getParameter("fInicio"), "dd/MM/yyyy"));
        agenda.setFechaFin(Util.stringToDate(request.getParameter("fFin"), "dd/MM/yyyy"));
        agenda.setIdPais(Integer.parseInt(request.getParameter("cPais")));
        agenda.setIdEstado(Integer.parseInt(request.getParameter("estadocombo")));
        agenda.setIdMunicipio(Integer.parseInt(request.getParameter("municipiocombo")));
        agenda.setLocalidad(request.getParameter("cLocalidad"));
        agenda.setActividades(request.getParameter("actividades"));
        return agenda;
    }

    public Comision cargaInstanciaComision(HttpServletRequest req, Usuario u) throws Exception {
        Comision comision = new Comision();
        comision.setIdComision(Integer.parseInt(req.getParameter("idComision")));
        comision.setCTAB(req.getParameter("ctaBancaria"));
        comision.setUsuarioCaptura(u.getLogin());
        comision.setUnidadResponsable(req.getParameter("cUejecutora"));
        comision.setTotalAgenda(new BigDecimal((req.getParameter("totalAgenda").equals("") ? "0" : req.getParameter("totalAgenda"))));
        comision.setTotalDias(new BigDecimal(req.getParameter("totalDias").equals("") ? "0" : req.getParameter("totalDias")));
        comision.setTotalTransporte(new BigDecimal(req.getParameter("totalTransporte").equals("") ? "0" : req.getParameter("totalTransporte")));
        comision.setNombreComision(req.getParameter("nombreComision"));
        comision.setCuentaBancariaCNF(req.getParameter("ctaBancariaCNF"));
        comision.setRFC(req.getParameter("campoRFC"));
        comision.setIdNombre(Integer.parseInt(req.getParameter("idNombre")));
        Empleado emp = new Empleado();
        emp.setNoEmpleado(Integer.parseInt(req.getParameter("nEmpleado")));
        comision.setEmpleado(emp);
        return comision;
    }

    public Comision cargaInstanciaComisionEdicion(HttpServletRequest req, Usuario u) throws Exception {
        Comision comision = new Comision();
        comision.setIdComision(Integer.parseInt(req.getParameter("id")));
        comision.setNombreComision(req.getParameter("cNombreComision"));
        comision.setCuentaBancariaCNF(req.getParameter("ctaBancariaCNF"));
        Empleado emp = new Empleado();
        emp.setNoEmpleado(Integer.parseInt(req.getParameter("numEmpleado")));
        comision.setEmpleado(emp);
        return comision;
    }

    public Comision cargaInstanciaEdicionSinFirmas(HttpServletRequest req, Usuario u) throws Exception {
        Comision comision = new Comision();
        comision.setIdComision(Integer.parseInt(req.getParameter("cComision")));
        return comision;
    }

    public TransporteAereo cargaInstanciaTransporteAereo(HttpServletRequest req) throws Exception {
        TransporteAereo avion = new TransporteAereo();
        avion.setIdComision(Integer.parseInt(req.getParameter("idComision")));
        avion.setcNumeroBoleto(req.getParameter("boletoAsignado"));
        avion.setmImporteBoleto(new BigDecimal(req.getParameter("importeBoleto").equals("") ? "0" : req.getParameter("importeBoleto")));
        avion.setPartida(req.getParameter("partidaBoleto"));
        avion.setRuta(req.getParameter("rutaBoleto"));
        avion.setRFCVuelo(req.getParameter("cIdRFC"));
        avion.setNombreVuelo(req.getParameter("cnombre"));
        avion.setTipoPago(req.getParameter("cDocumento"));
        // avion.setFolioPago( Integer.parseInt( req.getParameter( "folioPago"
        // )==null ? "0" : req.getParameter( "folioPago" ) ));
        return avion;
    }

    public Viaticos cargaInstanciaViaticos(HttpServletRequest req, int folio) throws Exception {
        Viaticos viatico = new Viaticos();
        viatico.setidAgenda(folio);
        viatico.setCuotaPorDia(new BigDecimal(req.getParameter("cCuota")));
        viatico.setMoneda(req.getParameter("cMoneda"));
        viatico.setTipoCambio(new BigDecimal(req.getParameter("tipoCambio")));
        viatico.setTieneHomologacion(Integer.parseInt(req.getParameter("hasSameRate")));
        viatico.setNivelHomologar(Integer.parseInt(req.getParameter("nivelHomologa") == null ? "0" : req.getParameter("nivelHomologa")));
        viatico.setPlazaHomologar(req.getParameter("plazaHomologa"));
        viatico.setJustificacion(req.getParameter("plazaJustifica"));
        viatico.setTienePaquete(Integer.parseInt(req.getParameter("hasPackage")));
        viatico.setIdPaquete(Integer.parseInt(req.getParameter("paqueteCombo")));
        return viatico;
    }

    private Comision instanciaComprobacionViaticos(HttpServletRequest req, Comision comision) throws Exception {
        comision.setPasaje(new BigDecimal(req.getParameter("mPasaje")));
        comision.setTaxi(new BigDecimal(req.getParameter("mTaxi")));
        comision.setPeaje(new BigDecimal(req.getParameter("mPeaje")));
        comision.setHotel(new BigDecimal(req.getParameter("mHotel")));
        comision.setConsumos(new BigDecimal(req.getParameter("mConsumos")));
        comision.setOtros(new BigDecimal(req.getParameter("mOtros")));
        comision.setPasajeLocal(new BigDecimal(req.getParameter("mPasajeLocal")));
        comision.setTaxiLocal(new BigDecimal(req.getParameter("mTaxiLocal")));
        comision.setGasLocal(new BigDecimal(req.getParameter("mGasolinaLocal")));
        comision.setPeajeLocal(new BigDecimal(req.getParameter("mPeajeLocal")));
        comision.setMaritimoLocal(new BigDecimal(req.getParameter("mMaritimoLocal")));
        comision.setAereoLocal(new BigDecimal(req.getParameter("mAereoLocal")));
        comision.setEvento(req.getParameter("cEvento"));
        comision.setTotalAgenda(new BigDecimal((req.getParameter("mTotal1") == null ? "0" : req.getParameter("mTotal1"))));
        comision.setTotalTransporte(new BigDecimal(req.getParameter("mTotalLocal") == null ? "0" : req.getParameter("mTotalLocal")));
        return comision;
    }

    public TransporteOficial cargaInstanciaTransporte(HttpServletRequest req) throws Exception {
        TransporteOficial transporteOficial = new TransporteOficial();
        if ("EDITAR".equals(req.getParameter("tipoOperacion"))) {
            transporteOficial.setIdComision(Integer.parseInt(req.getParameter("id")));
        } else if ("EDICION_SIN_FIRMAS".equals(req.getParameter("tipoOperacion"))) {
            transporteOficial.setIdComision(Integer.parseInt(req.getParameter("cComision")));
        } else {
            transporteOficial.setIdComision(Integer.parseInt(req.getParameter("idComision")));
        }
        transporteOficial.setIdTransporte(Integer.parseInt(req.getParameter("idTransporte")));
        transporteOficial.setIdTipo(Integer.parseInt(req.getParameter("transporteCombo")));
        transporteOficial.setOrigen(req.getParameter("descripcion"));
        transporteOficial.setMonto(new BigDecimal(req.getParameter("importeT")));
        transporteOficial.setKm(Integer.parseInt(req.getParameter("km")));
        transporteOficial.setTieneVales(Integer.parseInt(req.getParameter("tieneVales").equals("") ? "0" : req.getParameter("tieneVales")));
        transporteOficial.setNumEconomico(req.getParameter("nEconomico"));
        return transporteOficial;
    }

    public int lastId(String tipoPago) throws Exception {
        int folio = 0;
        PreparedStatement pst = null;
        ResultSet rs = null;
        Connection conn = getConnection();
        try {
            pst = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE (NOLOCK) WHERE seq_name = ?");
            pst.setString(1, tipoPago);
            rs = pst.executeQuery();
            if (rs.next()) {
                folio = rs.getInt(1);
            }
            return folio;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(pst);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(conn);
        }
    }

    public Agenda consultaAgenda(HttpServletRequest request, int idComision) throws Exception {
        Connection conn = null;
        Agenda agenda = null;
        try {
            conn = getConnection();
            agenda = AgendaDAO.consultarAgenda(conn, idComision);
            return agenda;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public Comision consultaComision(HttpServletRequest req, int idComision) throws Exception {
        Connection conn = null;
        Comision comision = null;
        try {
            conn = getConnection();
            comision = ComisionDAO.consultarComision(conn, idComision);
            comision = instanciaComprobacionViaticos(req, comision);
            return comision;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public boolean guardarAgenda(Agenda agenda, Viaticos viatico, Comision comision, int tieneExcepcionMesesAnteriores) throws Exception {
        Connection conn = null;
        String mensaje = "";
        try {
            conn = getConnection();
            //Revisa si no existen viaticos en esas fechas
            mensaje = GeneraSolicitudViaticos.consultaFechasCA(conn, agenda, comision.getEmpleado().getNoEmpleado());
            if (mensaje.isEmpty()) {
                boolean esComisionMA = GeneraSolicitudViaticos.comisionMesesAnteriores(conn, agenda);
                boolean cuotaCorrecta = ViaticosDAO.validaImporteCuota(conn, viatico, comision.getEmpleado().getNoEmpleado(), agenda.getIdPais());
                if (cuotaCorrecta) {
                    if (!esComisionMA || esComisionMA && tieneExcepcionMesesAnteriores == 1) {
                        if (!GeneraSolicitudViaticos.comisionExiste(conn, comision.getIdComision())) {
                            ComisionDAO.insertarComision(conn, comision);
                            ComisionDAO.actualizaComisionBitacora(conn, comision.getIdComision(), comision.getUsuarioCaptura());
                        }
                        AgendaDAO.insertarAgenda(conn, agenda);
                        AgendaDAO.actualizarFolio(conn);
                        ViaticosDAO.insertarViaticos(conn, viatico);
                        int certificado = GeneraSolicitudViaticos.tieneCertificado(conn, agenda.getIdAgenda());
                        boolean certificadoGuardado = GeneraSolicitudViaticos.certificadoGuardado(conn, comision.getIdComision());
                        if (certificado > 0 && certificadoGuardado == false) {
                            //Calcular el id y guardar en la tabla de tComision
                            GeneraSolicitudViaticos.generarFolioCertificado(conn, comision.getIdComision());
                        }
                        conn.commit();
                    } else {
                        throw new Exception("No se puede hacer una comision con una antiguedad mayor a 15 días. Consulte con el área de Tesoreria");
                    }
                } else {
                    throw new Exception("El importe de la cuota no es correcta, borre la agenda y trate de agregarla de nuevo o consulte al administrador.");
                }
            } else {
                throw new Exception(mensaje);
            }
            return true;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
            release();
        }
    }

    public boolean actualizarAgenda(Agenda agenda, Viaticos viatico, Comision comision, String login) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            //Valida fechas en los formatos de control de asistencia
            //Revisa si no existen viaticos en esas fechas
            String mensaje = GeneraSolicitudViaticos.consultaFechasCA(conn, agenda, comision.getEmpleado().getNoEmpleado());
            if (mensaje.isEmpty()) {
                boolean cuotaCorrecta = ViaticosDAO.validaImporteCuota(conn, viatico, comision.getEmpleado().getNoEmpleado(), agenda.getIdPais());
                if (cuotaCorrecta) {
                    AgendaDAO.insertarAgenda(conn, agenda);
                    AgendaDAO.actualizarFolio(conn);
                    ViaticosDAO.insertarViaticos(conn, viatico);
                    GeneraSolicitudViaticos.actualizaTotales(conn, comision.getIdComision(), "EDITADO");
                    int idComisionViaticos = existeComision(conn, comision.getIdComision(), agenda.getFechaInicio(), agenda.getIdMunicipio(), agenda.getFechaFin());
                    if (idComisionViaticos != 0) {
                        GeneraSolicitudViaticos.actualizarComisionViaticos(conn, agenda, comision.getNombreComision(), idComisionViaticos);
                    } else {
                        idComisionViaticos = GeneraSolicitudViaticos.insertComisionViaticos(conn, agenda, comision.getNombreComision());
                        actualizarComisionPagos(conn, idComisionViaticos, comision.getIdComision());
                    }
                    Agenda agendaAcum = AgendaDAO.consultaFechaAgendaAcumulada(conn, comision.getIdComision());
                    //Validar si es Certificado de transito y si ya esta guardado
                    int certificado = GeneraSolicitudViaticos.tieneCertificado(conn, agenda.getIdAgenda());
                    boolean folioGuardado = GeneraSolicitudViaticos.certificadoGuardado(conn, comision.getIdComision());
                    if (certificado > 0 && folioGuardado == false) {
                        //Calcular el id y guardar en la tabla de tComision
                        GeneraSolicitudViaticos.generarFolioCertificado(conn, comision.getIdComision());
                    }
                    AgendaDAO.actualizaAgendaBitacora(conn, agenda.getIdAgenda(), login);
                    ComisionDAO.actualizarBitacoraComision(conn, comision.getIdComision(), login);
                    ComisionDAO.insertarBitacoraRevertir(conn, comision.getIdComision(), agendaAcum, comision.getEmpleado().getNoEmpleado(), "Asistencia - Actualizar Agenda");
                    //Actualiza control de asistencia
                    GeneraSolicitudViaticos.actualizarAsistencia(conn, agendaAcum, comision.getEmpleado().getNoEmpleado(), "Comprobando");
                    conn.commit();
                } else {
                    throw new Exception("La importe de la cuota no es correcta, borre la agenda y trate de agregarla de nuevo o consulte al administrador.");
                }
            } else {
                throw new Exception(mensaje);
            }
            return true;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    private void actualizarComisionPagos(Connection conn, int idComisionViaticos, int idComisionModulo) throws Exception {
        PreparedStatement pst = null;
        try {
            //Se actualizan todas las RG exceptuando las del tren maya
            pst = conn.prepareStatement("UPDATE tRelacionComprobacionComisiones SET nIdComision = ? WHERE nidComisionModulo = ? AND cTipoTramite = 'RELACIONGASTOS' " + "AND nFolioTramite IN (select nFolioRELACIONGASTOS from tRELACIONGASTOSEncabezado WHERE cDocumentoHaplicado = 'S' AND cDescripcionPoliza NOT LIKE '%TREN MAYA%')");
            pst.setInt(1, idComisionViaticos);
            pst.setInt(2, idComisionModulo);
            pst.executeUpdate();
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public int agregaBoleto(TransporteAereo transporte) throws Exception {
        Connection conn = null;
        int registros = 0;
        try {
            conn = getConnection();
            int insertados = TransporteDAO.insertarTransporteAereo(conn, transporte);
            if (insertados > 0)
                registros = TransporteDAO.actualizarLayoutPorBoleto(conn, transporte, "MV");
            conn.commit();
            return registros;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public int eliminarAgenda(int folio, String u_login, String tipoOperacion, Agenda agenda, int idEmpleado) throws Exception {
        Connection conn = null;
        int guardados = 0;
        try {
            conn = getConnection();
            ViaticosDAO.borrarViaticos(conn, folio);
            guardados = AgendaDAO.borrarAgenda(conn, folio, u_login);
            ComisionDAO.actualizarComision(conn, folio, u_login);
            if ("EDITAR".equals(tipoOperacion) || "EDICION_SIN_FIRMAS".equals(tipoOperacion)) {
                GeneraSolicitudViaticos.revertirAsistencia(conn, agenda, idEmpleado);
            }
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
        return guardados;
    }

    public int eliminarAgendaEdicion(int folio, int idComision, String login) throws Exception {
        Connection conn = null;
        int guardados = 0;
        try {
            conn = getConnection();
            ViaticosDAO.borrarViaticos(conn, folio);
            guardados = AgendaDAO.borrarAgenda(conn, folio, login);
            GeneraSolicitudViaticos.actualizaTotales(conn, idComision, "EDITADO");
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
        return guardados;
    }

    public int eliminarAgendaSinFirmas(int folio, int idComision, String login) throws Exception {
        Connection conn = null;
        int guardados = 0;
        try {
            conn = getConnection();
            ViaticosDAO.borrarViaticos(conn, folio);
            guardados = AgendaDAO.borrarAgenda(conn, folio, login);
            GeneraSolicitudViaticos.actualizaTotales(conn, idComision, "ORIGINAL");
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
        return guardados;
    }

    public int eliminarTramite(int folio, String login) throws Exception {
        Connection conn = null;
        int guardados = 0;
        try {
            conn = getConnection();
            int idAgenda = AgendaDAO.existeAgenda(conn, folio);
            ViaticosDAO.borrarViaticos(conn, idAgenda);
            AgendaDAO.borrarAgenda(conn, idAgenda, login);
            TransporteDAO.borrarTransporteTodos(conn, folio);
            ComisionDAO.borrarComision(conn, folio, login);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
        return guardados;
    }

    public boolean guardarTransporte(TransporteOficial transporte) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            TransporteDAO.insertarTransporteOficial(conn, transporte);
            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public int eliminarTransporte(int folio, int idComision) throws Exception {
        Connection conn = null;
        int guardados = 0;
        try {
            conn = getConnection();
            TransporteDAO.borrarTransporte(conn, folio, idComision);
            GeneraSolicitudViaticos.consultaTransporteLocal(conn, idComision);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
        return guardados;
    }

    public String consultaEvento() throws Exception {
        Connection conn = null;
        String evento = "";
        try {
            conn = getConnection();
            evento = ComisionDAO.consultaEventoViaticos(conn, "RELACIONGASTOS", "D");
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
        return evento;
    }

    public Caso generaTramite(Comision comision, Agenda agenda, HttpServletRequest req, String reportPath, Usuario u) throws Exception, ViaticosException {
        Connection conn = null;
        int idComisionViaticos = 0;
        Caso casoGenerado = null;
        try {
            conn = getConnection();
            int tipoSolicitud = Integer.parseInt(req.getParameter("nidTipo"));
            int firmanteVobo = Integer.parseInt(req.getParameter("nombreVoBo"));
            int firmanteAut = Integer.parseInt(req.getParameter("nombreAut"));
            int firmanteElab = Integer.parseInt(req.getParameter("nEmpleadoElabora"));
            int folio = Integer.parseInt(req.getParameter("nFolioPago"));
            String cesFiel = req.getParameter("esFirmaElectronica");
            int folioCaja = Integer.parseInt(req.getParameter("nFolioCaja") == "" ? "0" : req.getParameter("nFolioCaja"));
            String informeComision = req.getParameter("informeComision");
            BigDecimal retencion = new BigDecimal(req.getParameter("mImporteRetencion"));
            BigDecimal neto = new BigDecimal(req.getParameter("importeNeto"));
            String ejercicioFiscal = "";
            idComisionViaticos = existeComision(conn, comision.getIdComision(), agenda.getFechaInicio(), agenda.getIdMunicipio(), agenda.getFechaFin());
            if (idComisionViaticos == 0)
                idComisionViaticos = GeneraSolicitudViaticos.insertComisionViaticos(conn, agenda, comision.getNombreComision());
            if (idComisionViaticos > 0) {
                try {
                    ejercicioFiscal = EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal();
                } catch (Exception e) {
                    throw new ViaticosException("Problemas encontrando el ejercicio fiscal: " + e.toString());
                }
                if (tipoSolicitud == 1) {
                    casoGenerado = GeneraSolicitudViaticos.generaSolicitudCaja(conn, comision, agenda, u, idComisionViaticos, firmanteElab, firmanteVobo, firmanteAut, cesFiel, reportPath, folio, informeComision, retencion, neto, ejercicioFiscal, folioCaja);
                    insertaDelegatorios(conn, casoGenerado, req, cesFiel.equals("S"));
                    if (cesFiel.equals("S")) {
                        // Se genera el formato y se envia a firma.
                        CajaBusinessLogic cbl = new CajaBusinessLogic();
                        cbl.solicitaFirmaElectronica(conn, casoGenerado, u, reportPath);
                    }
                } else if (tipoSolicitud == 2) {
                    EgresoEncabezado encabezado = new EgresoRELACIONGASTOSEncabezado();
                    encabezado = EgresoRELACIONGASTOSEncabezado.setInstanceRelacionGastos(req, comision, agenda, u, ejercicioFiscal);
                    agenda.setMotivoComision(req.getParameter("concepto"));
                    String mensaje = GeneraSolicitudViaticos.validaPartidasvsComprobacion(conn, folio);
                    if (mensaje.equals("")) {
                        casoGenerado = GeneraSolicitudViaticos.generaRelacionGastos(conn, comision, agenda, u, reportPath, folio, retencion, ejercicioFiscal, encabezado, idComisionViaticos, folioCaja);
                        insertaDelegatorios(conn, casoGenerado, req, cesFiel.equals("S"));
                        if ("S".equalsIgnoreCase(cesFiel)) {
                            EgresosBusinessLogic ebl = new EgresosBusinessLogic();
                            ebl.procesFirmaElectronica(conn, encabezado, u, reportPath);
                            ebl.generaInformeComision(conn, encabezado, u, reportPath);
                        }
                    } else
                        throw new ViaticosException("No cuadra el importe de las Partidas - Retenciones con lo que se capturo en el Detalle del Viático. " + mensaje);
                } else if (tipoSolicitud == 3) {
                    agenda.setActividades(req.getParameter("informeComisionCSV"));
                    casoGenerado = GeneraSolicitudViaticos.generaComisionSinViaticos(conn, comision, agenda, u, idComisionViaticos, firmanteElab, firmanteVobo, firmanteAut, cesFiel, reportPath, folio, ejercicioFiscal);
                    insertaDelegatorios(conn, casoGenerado, req, cesFiel.equals("S"));
                    ComisionSinViaticos csviat = new ComisionSinViaticos();
                    csviat.onAvanzaCaso(conn, u.getLogin(), casoGenerado, 5);
                } else {
                    throw new ViaticosException("La opcion de tipo solicitud recibida " + tipoSolicitud + " No es valida.");
                }
            } else
                throw new ViaticosException("No se pudo generar la  comision. Revise con el administrador");
            conn.commit();
            return casoGenerado;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2, e2);
                }
            throw (e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    private void insertaDelegatorios(Connection conn, Caso c, HttpServletRequest request, boolean esFirmaElectronica) throws Exception {
        List<Firmante> suplentes = new ArrayList<>();
        boolean suplenteVoBo = Boolean.parseBoolean(request.getParameter("voboSuplencia"));
        boolean suplenteAut = Boolean.parseBoolean(request.getParameter("autSuplencia"));
        if (suplenteVoBo)
            suplentes.add(generateSuplenteVoBo(conn, c, request));
        if (suplenteAut)
            suplentes.add(generateSuplenteAut(conn, c, request));
        fbl.saveFirmantes(conn, suplentes, c.getTipoCaso().getGavetaAsociada(), Util.folio(c.getFolio()), esFirmaElectronica);
    }

    private FirmanteSuplente generateSuplenteVoBo(Connection conn, Caso c, HttpServletRequest request) throws Exception {
        String noOficio = request.getParameter("voboNumeroOficio");
        Date fechaOficio = Util.stringToDate(request.getParameter("voboFechaOficio"), "dd/MM/yyyy");
        String tipoSuplencia = request.getParameter("voboSuplenciaMotivo");
        int numeroEmpleadoFirmante = Integer.parseInt(request.getParameter("voboEmpleadoSuplente"));
        FirmanteSuplente suplenteVobo = FirmanteManager.cargaFirmanteSuplente(conn, numeroEmpleadoFirmante, "SUPVOBO");
        suplenteVobo.setFechaOficio(fechaOficio);
        suplenteVobo.setFolioOficio(noOficio);
        suplenteVobo.setTipoSuplencia(tipoSuplencia);
        return suplenteVobo;
    }

    private FirmanteSuplente generateSuplenteAut(Connection conn, Caso c, HttpServletRequest request) throws Exception {
        String noOficio = request.getParameter("autNumroOficio");
        Date fechaOficio = Util.stringToDate(request.getParameter("autFechaOficio"), "dd/MM/yyyy");
        String tipoSuplencia = request.getParameter("autSuplenciaMotivo");
        int numeroEmpleadoFirmante = Integer.parseInt(request.getParameter("autEmpleadoSuplente"));
        FirmanteSuplente suplenteAut = FirmanteManager.cargaFirmanteSuplente(conn, numeroEmpleadoFirmante, "SUPAUT");
        suplenteAut.setFechaOficio(fechaOficio);
        suplenteAut.setFolioOficio(noOficio);
        suplenteAut.setTipoSuplencia(tipoSuplencia);
        return suplenteAut;
    }

    public Caso generaCasoRG(Comision comision, Agenda agenda, int tipoSolicitud, Usuario u, String idEvento) throws Exception, ViaticosException {
        Connection conn = null;
        int idComisionViaticos = 0;
        Caso casoGenerado = null;
        try {
            conn = getConnection();
            idComisionViaticos = existeComision(conn, comision.getIdComision(), agenda.getFechaInicio(), agenda.getIdMunicipio(), agenda.getFechaFin());
            if (idComisionViaticos == 0)
                idComisionViaticos = GeneraSolicitudViaticos.insertComisionViaticos(conn, agenda, comision.getNombreComision());
            if (idComisionViaticos > 0)
                casoGenerado = GeneraSolicitudViaticos.iniciaRg(conn, comision, agenda, u, idEvento, idComisionViaticos);
            else
                throw new ViaticosException("No se pudo generar la  comision. Revise con el administrador");
            conn.commit();
            return casoGenerado;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2, e2);
                }
            throw new ViaticosException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public int existeComision(Connection conn, int idComision, Date fechaInicio, int municipio, Date fechaFin) throws Exception {
        int folio = 0;
        PreparedStatement pst = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append(" SELECT TOP 1 RCC.nIdComision , fInicio, ID_MUNICIPIO ");
        query.append("	FROM tRelacionComprobacionComisiones RCC WITH (NOLOCK) ");
        query.append("	INNER JOIN tViaticosComisiones COM WITH (NOLOCK) ");
        query.append("		ON RCC.nIdComision = COM.nIdComision ");
        query.append("	WHERE nidcomisionModulo = ? AND ID_MUNICIPIO = ? AND fInicio = ? and fFin = ? ");
        query.append("	ORDER BY RCC.nIdComision DESC ");
        try {
            java.sql.Date sqlfechaInicio = new java.sql.Date(fechaInicio.getTime());
            java.sql.Date sqlfechaFin = new java.sql.Date(fechaFin.getTime());
            pst = conn.prepareStatement(query.toString());
            pst.setInt(1, idComision);
            pst.setInt(2, municipio);
            pst.setDate(3, sqlfechaInicio);
            pst.setDate(4, sqlfechaFin);
            rs = pst.executeQuery();
            log.debug(query);
            if (rs.next()) {
                folio = rs.getInt("nIdComision");
            }
            return folio;
        } finally {
            CloseObject.closeObject(pst);
            CloseObject.closeObject(rs);
        }
    }

    public String validaInicidencia(Comision comision, Agenda agenda) throws Exception {
        Connection conn = null;
        String mensaje = "";
        try {
            conn = getConnection();
            if (GeneraSolicitudViaticos.validaIncidenciasNomina(conn, comision, agenda))
                mensaje = "La comision tiene una incidencia capturada en nomina por lo que no se puede guardar.";
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2, e2);
                }
        } finally {
            CloseObject.closeObject(conn);
        }
        return mensaje;
    }

    public int guardarFirmantes(int folio, Comision comision, Agenda agenda) throws Exception {
        Connection conn = null;
        int firmantes = 0;
        try {
            conn = getConnection();
            int esInternacional = ComisionDAO.esComisionInternacional(conn, folio);
            firmantes = ComisionDAO.guardarFirmantes(conn, comision, esInternacional);
            ComisionDAO.insertarBitacoraRevertir(conn, folio, agenda, comision.getEmpleado().getNoEmpleado(), "Asistencia - Solicitud Viaticos");
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2, e2);
                }
        } finally {
            CloseObject.closeObject(conn);
        }
        return firmantes;
    }

    public boolean existenMasFirmantes(int folioComision) throws Exception {
        boolean existe = false;
        Connection conn = null;
        try {
            conn = getConnection();
            existe = GeneraSolicitudViaticos.existenMasFirmantes(conn, folioComision);
            return existe;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public String actualizaEstatusMasivo(int folioComision, int idEmpleado, int idEstatus) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            GeneraSolicitudViaticos.ActualizaBitacoraFirmantes(conn, folioComision, idEmpleado);
            GeneraSolicitudViaticos.actualizaEstatus(conn, folioComision);
            String origen = GeneraSolicitudViaticos.consultaOrigen(conn, folioComision);
            GeneraSolicitudViaticos.enviarCorreoIni(conn, folioComision, idEstatus, origen, 0, "");
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
        return "Se firmó correctamente";
    }

    public String actualizaEstatus(int folioComision, int idEmpleado) throws Exception {
        Connection conn = null;
        String estatus = "";
        try {
            conn = getConnection();
            estatus = AgendaDAO.consultaEstatusAgenda(conn, folioComision);
            if (estatus.equals("C")) {
                estatus = "La solicitud se encuentra cancelada";
            } else {
                GeneraSolicitudViaticos.ActualizaBitacoraFirmantes(conn, folioComision, idEmpleado);
                GeneraSolicitudViaticos.actualizaEstatus(conn, folioComision);
                GeneraSolicitudViaticos.enviarCorreoAut(conn, folioComision, idEmpleado);
                estatus = "Se firmó correctamente.";
            }
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
        return estatus;
    }

    public String rechazaComision(int folioComision, String login) throws Exception {
        Connection conn = null;
        Agenda agenda = new Agenda();
        try {
            conn = getConnection();
            GeneraSolicitudViaticos.RechazaBitacoraFirmantes(conn, folioComision);
            ComisionDAO.cancelaComision(conn, folioComision);
            agenda = AgendaDAO.consultaFechaAgendaAcumulada(conn, folioComision);
            GeneraSolicitudViaticos.enviarCorreoCancela(conn, folioComision);
            ComisionDAO.insertarBitacoraRevertir(conn, folioComision, agenda, agenda.getIdEmpleado(), "Rechaza comision");
            ComisionDAO.actualizarBitacoraComision(conn, folioComision, login);
            GeneraSolicitudViaticos.revertirAsistencia(conn, agenda, agenda.getIdEmpleado());
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
        return "Se canceló correctamente";
    }

    public String rechazaComisionConMotivo(int folioComision, String motivo, String login) throws Exception {
        Connection conn = null;
        Agenda agenda = new Agenda();
        try {
            conn = getConnection();
            int idEmpleado = ComisionDAO.consultaNoEmpleado(conn, folioComision);
            GeneraSolicitudViaticos.RechazaBitacoraFirmantes(conn, folioComision);
            ComisionDAO.cancelaComisionConMotivo(conn, folioComision, motivo);
            agenda = AgendaDAO.consultaFechaAgendaAcumulada(conn, folioComision);
            GeneraSolicitudViaticos.enviarCorreoCancela(conn, folioComision);
            ComisionDAO.insertarBitacoraRevertir(conn, folioComision, agenda, idEmpleado, "Rechaza comision con motivo");
            ComisionDAO.actualizarBitacoraComision(conn, folioComision, login);
            // Quitar leyenda en control de asistencia
            GeneraSolicitudViaticos.revertirAsistencia(conn, agenda, idEmpleado);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
        return "Se canceló correctamente";
    }

    public String finalizaTramite(int folioComision, int idEstatus, int idEmpleado, String u_login) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            Agenda agendas = AgendaDAO.consultaFechaAgendaAcumulada(conn, folioComision);
            GeneraSolicitudViaticos.ActualizaBitacoraFirmantes(conn, folioComision, idEmpleado);
            GeneraSolicitudViaticos.avanzarCaso(conn, folioComision, u_login, "VIATICOS", "CONSULTA_VIATICOS");
            GeneraSolicitudViaticos.enviarCorreo(conn, folioComision);
            int tieneBol = GeneraSolicitudViaticos.consutaTieneBoleto(conn, folioComision);
            if (tieneBol > 0) {
                GeneraSolicitudViaticos.enviarCorreoAgencia(conn, folioComision);
            }
            ComisionDAO.actualizarBitacoraComision(conn, folioComision, u_login);
            if (!ComisionDAO.estaFinalizada(conn, folioComision)) {
                ComisionDAO.actualizaEstatusAplicado(conn, folioComision, idEstatus);
                int numEmpComision = ComisionDAO.consultaNoEmpleado(conn, folioComision);
                ComisionDAO.insertarBitacoraRevertir(conn, folioComision, agendas, numEmpComision, "Asistencia - Comision autorizada");
                GeneraSolicitudViaticos.actualizarAsistencia(conn, agendas, numEmpComision, "Comprobando");
            }
            conn.commit();
            return "Se firmo correctamente.";
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            release();
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public int estatusSiguiente(int folio, int idEmpleado) throws Exception {
        int idEstatus = 0;
        Connection conn = null;
        try {
            conn = getConnection();
            idEstatus = GeneraSolicitudViaticos.estatusSiguiente(conn, folio, idEmpleado);
            return idEstatus;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public int eliminaBoletoAvion(TransporteAereo avion) throws Exception {
        Connection conn = null;
        int registros = 0;
        try {
            conn = getConnection();
            TransporteDAO.actualizarLayoutPorBoleto(conn, avion, "A");
            registros = TransporteDAO.borrarBoletoAvion(conn, avion);
            conn.commit();
            return registros;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void consultaActualizaAsistencia(Agenda agenda, int idEmpleado, String concepto) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            GeneraSolicitudViaticos.actualizarAsistencia(conn, agenda, idEmpleado, concepto);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
            release();
        }
    }

    public Caso getRelacionGastosAsociada(int folioViaticos) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            int folioRG = GeneraSolicitudViaticos.getFolioRG(conn, folioViaticos);
            Caso c = CasoManager.findByFolioLike(conn, "RELACIONGASTOS", String.valueOf(folioRG));
            return c;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void actualizarComision(int idComision) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            // Consulta transporte Local y actualiza la comision
            GeneraSolicitudViaticos.consultaTransporteLocal(conn, idComision);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public int consultaEmpleado(int idAgenda) throws Exception {
        Connection conn = null;
        int idEmpleado = 0;
        try {
            conn = getConnection();
            idEmpleado = ComisionDAO.consultaEmpleado(conn, idAgenda);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
        return idEmpleado;
    }

    public Agenda cargaInstanciaAgendaBorrar(HttpServletRequest request, int folioAgenda, int idEmpleado) throws Exception {
        Connection conn = null;
        Agenda agenda = new Agenda();
        try {
            conn = getConnection();
            agenda = AgendaDAO.consultaAgenda(conn, folioAgenda);
            ComisionDAO.insertarBitacoraRevertir(conn, agenda.getIdComision(), agenda, idEmpleado, "Elimina renglon agenda");
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
        return agenda;
    }

    public void editarAgenda(Comision comision, Agenda agenda, Viaticos viatico, String login) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            //Edita sobre el registro que ya existe de la agenda
            //Traerme los datos de la agenda antes de actualizar
            Agenda agendaBitacora = new Agenda();
            agendaBitacora = AgendaDAO.consultaAgenda(conn, agenda.getIdAgenda());
            AgendaDAO.actualizarDatosAgenda(conn, agenda);
            ComisionDAO.actualizarComision(conn, comision.getIdComision(), login);
            ViaticosDAO.actualizarDatosViaticos(conn, viatico);
            agenda = AgendaDAO.consultaAgenda(conn, agenda.getIdAgenda());
            int idComisionViaticos = existeComision(conn, comision.getIdComision(), agenda.getFechaInicio(), agenda.getIdMunicipio(), agenda.getFechaFin());
            if (idComisionViaticos != 0) {
                GeneraSolicitudViaticos.actualizarComisionViaticos(conn, agenda, comision.getNombreComision(), idComisionViaticos);
            }
            int firmas = GeneraSolicitudViaticos.estaFirmado(conn, comision.getIdComision());
            if (firmas > 0)
                GeneraSolicitudViaticos.borraFirmantes(conn, comision.getIdComision(), comision.getEmpleado().getNoEmpleado());
            //Validar si es Certificado de transito y si ya esta guardado
            int certificado = GeneraSolicitudViaticos.tieneCertificado(conn, agenda.getIdAgenda());
            boolean folioGuardado = GeneraSolicitudViaticos.certificadoGuardado(conn, comision.getIdComision());
            if (certificado > 0 && folioGuardado == false) {
                //Calcular el id y guardar en la tabla de tComision
                GeneraSolicitudViaticos.generarFolioCertificado(conn, comision.getIdComision());
            }
            ComisionDAO.insertarBitacoraRevertir(conn, comision.getIdComision(), agendaBitacora, comision.getEmpleado().getNoEmpleado(), "Revertir - Editar Agenda");
            ComisionDAO.insertarBitacoraRevertir(conn, comision.getIdComision(), agenda, comision.getEmpleado().getNoEmpleado(), "Aplicar - Editar Agenda");
            AgendaDAO.actualizaAgendaBitacora(conn, agenda.getIdAgenda(), login);
            ComisionDAO.actualizarBitacoraComision(conn, comision.getIdComision(), login);
            // Quitar y poner la leyenda en control de asistencia
            GeneraSolicitudViaticos.revertirAsistencia(conn, agendaBitacora, comision.getEmpleado().getNoEmpleado());
            GeneraSolicitudViaticos.actualizarAsistencia(conn, agenda, comision.getEmpleado().getNoEmpleado(), "Comprobando");
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void editarNombreAgenda(int idComision, String NombreNuevo, int idNombre) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            GeneraSolicitudViaticos.actualizaNombreComision(conn, idComision, NombreNuevo, idNombre);
            int idComisionViaticos = GeneraSolicitudViaticos.existeComisionGral(conn, idComision);
            if (idComisionViaticos != 0) {
                GeneraSolicitudViaticos.actualizarNombreComisionViaticos(conn, idComisionViaticos, NombreNuevo);
            }
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void editarFirmarAgenda(int idComision, int noEmpleado, String tipoOper, int pais) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            // Consultar agenda y registrar en el reloj de asistencia sus dias de viaticos
            Agenda agenda = AgendaDAO.consultaFechaAgendaAcumulada(conn, idComision);
            // Verifica si esta firmado para reiniciar firmantes
            int firmas = GeneraSolicitudViaticos.estaFirmado(conn, idComision);
            if (firmas > 0)
                GeneraSolicitudViaticos.borraFirmantes(conn, idComision, noEmpleado);
            ComisionDAO.insertarBitacoraRevertir(conn, idComision, agenda, noEmpleado, "Asistencia - Editar Enviar a Firmar Agenda");
            GeneraSolicitudViaticos.enviarCorreoIni(conn, idComision, 1, tipoOper, 0, "");
            String mensaje = GeneraSolicitudViaticos.consultaFechasCA(conn, agenda, noEmpleado);
            if (mensaje.isEmpty()) {
                GeneraSolicitudViaticos.actualizarAsistencia(conn, agenda, noEmpleado, "Comprobando");
            } else {
                throw new Exception(mensaje);
            }
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            release();
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void editarSinFirmar(int idComision, int noEmpleado, int pais, int tieneBoletos, String justBoletos) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            // Verifica si tiene alguna firma para reiniciar firmantes
            int firmas = GeneraSolicitudViaticos.estaFirmado(conn, idComision);
            if (firmas > 0) {
                GeneraSolicitudViaticos.borraFirmantes(conn, idComision, noEmpleado);
                GeneraSolicitudViaticos.enviarCorreoIni(conn, idComision, 1, "EDITAR", tieneBoletos, justBoletos);
            } else {
                GeneraSolicitudViaticos.enviarCorreoIni(conn, idComision, 1, "ORIGINAL", tieneBoletos, justBoletos);
            }
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void actualizarTransporte(TransporteOficial transporte, int folioComision) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            TransporteDAO.actualizarTransporteLocal(conn, transporte, folioComision);
            // Consulta el transporte local y actualiza la comision
            GeneraSolicitudViaticos.consultaTransporteLocal(conn, folioComision);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void eliminarSolicitud(int idComision, int folioPago) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            int tienePoliza = GeneraSolicitudViaticos.consultaPolizaTramite(conn, folioPago);
            if (tienePoliza == 0)
                GeneraSolicitudViaticos.eliminarSolicitud(conn, idComision, folioPago);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void agregarRetenciones(HttpServletRequest req) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            int folioPago = Integer.parseInt(req.getParameter("folioPago"));
            BigDecimal iva = new BigDecimal(req.getParameter("mIvaFactura"));
            BigDecimal isr = new BigDecimal(req.getParameter("mISRFactura"));
            int ejercicio = Integer.parseInt(req.getParameter("aejercicioFiscal"));
            String tipoPago = req.getParameter("cTipoPago");
            int tipoRetiva = Integer.parseInt(req.getParameter("tipoRetIVA"));
            int tipoRetisr = Integer.parseInt(req.getParameter("tipoRetISR"));
            int idConcepto = Integer.parseInt(req.getParameter("concepto"));
            GeneraSolicitudViaticos.agregarRetenciones(conn, folioPago, ejercicio, iva, isr, tipoPago, tipoRetiva, tipoRetisr, idConcepto);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void finalizaComision(int folio, int noEmpleado, String login) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            Agenda agenda = AgendaDAO.consultaFechaAgendaAcumulada(conn, folio);
            int tienePagos = GeneraSolicitudViaticos.revisarPagos(conn, folio);
            ComisionDAO.insertarBitacoraRevertir(conn, folio, agenda, noEmpleado, "Asistencia - Finaliza comisión");
            ComisionDAO.actualizarBitacoraComision(conn, folio, login);
            ComisionDAO.finalizaComision(conn, folio);
            if (tienePagos == 0) {
                GeneraSolicitudViaticos.actualizarAsistencia(conn, agenda, noEmpleado, "Comisión");
            } else {
                GeneraSolicitudViaticos.actualizarAsistencia(conn, agenda, noEmpleado, "Viáticos");
            }
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            release();
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public String envioCorreosTramitesRetraso(boolean standalone) throws Exception {
        Connection conn = null;
        try {
            if (standalone)
                conn = Util.getStandAloneConnection();
            else
                conn = getConnection();
            ArrayList<Integer> foliosPendientes = new ArrayList<Integer>();
            foliosPendientes = GeneraSolicitudViaticos.viaticosConSaldo(conn);
            GeneraSolicitudViaticos.enviarCorreoTramitesRetraso(conn, foliosPendientes, "vencida con retraso.");
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
        return "Se enviaron los correos correctamente.";
    }

    public String cancelaComision(int folio, String login) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            if (folio == 0) {
                throw new Exception("Folio nulo, error de conexión. Intentelo de nuevo mas tarde.");
            }
            Agenda agenda = AgendaDAO.consultaFechaAgendaAcumulada(conn, folio);
            GeneraSolicitudViaticos.RechazaBitacoraFirmantes(conn, folio);
            GeneraSolicitudViaticos.enviarCorreoCancela(conn, folio);
            if (agenda.getFechaInicio() == null || agenda.getFechaFin() == null) {
                throw new Exception("Las fechas estan vacias, error de conexión. Intentelo de nuevo mas tarde.");
            }
            //Actualiza Bitacoras
            ComisionDAO.insertarBitacoraRevertir(conn, folio, agenda, agenda.getIdEmpleado(), "Cancela comision");
            ComisionDAO.actualizarBitacoraComision(conn, folio, login);
            // Quita la leyenda el control de asistencia
            GeneraSolicitudViaticos.revertirAsistencia(conn, agenda, agenda.getIdEmpleado());
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
            release();
            throw e;
        } finally {
            CloseObject.closeObject(conn);
            release();
        }
        return "Se cancelo la comisión correctamente";
    }

    public void cargaJustificacion(Usuario u, Map<String, String> datosCarga) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            if (conn.getAutoCommit())
                conn.setAutoCommit(false);
            Caso c = CasoManager.findByFolioLike(conn, "RELACIONGASTOS", datosCarga.get("folio"));
            Carpeta archivoJustif = FacturaManager.obtenCarpetaDestino(conn, c, "Justificacion", u.getLogin());
            FacturaManager.insertaOficioDeTransito(conn, datosCarga.get("fileJustificacion"), archivoJustif, c, u.getLogin());
            GeneraSolicitudViaticos.actualizarJustificacionTicket(conn, datosCarga.get("folio"), "Se adjuntaron los tickets de las facturas correctamente que especifican que la compra o el servicio se realizó dentro de las fechas de la comisión.", "1", "0");
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.error("Problemas realizando rollback: " + e2, e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public void cargaPasesAbordar(Usuario u, Map<String, String> datosCarga) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            if (conn.getAutoCommit())
                conn.setAutoCommit(false);
            Caso c = CasoManager.findByFolioLike(conn, "RELACIONGASTOS", datosCarga.get("folio"));
            Carpeta archivoJustif = FacturaManager.obtenCarpetaDestino(conn, c, "Justificacion", u.getLogin());
            FacturaManager.insertaOficioDeTransito(conn, datosCarga.get("fileJustificaBoletos"), archivoJustif, c, u.getLogin());
            GeneraSolicitudViaticos.actualizarJustificacionBoleto(conn, datosCarga.get("folio"), "Se adjuntaron pases de abordar correctamente", "0", "1");
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.error("Problemas realizando rollback: " + e2, e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public void actualizarJustificacionBoletos(String folio, String mensaje) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            GeneraSolicitudViaticos.actualizarJustificacionBoleto(conn, folio, mensaje, "0", "0");
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.error("Problemas realizando rollback: " + e2, e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public void actualizarJustificacionTicket(String folio, String mensaje) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            GeneraSolicitudViaticos.actualizarJustificacionTicket(conn, folio, mensaje, "0", "0");
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.error("Problemas realizando rollback: " + e2, e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public void actualizarOtraJustificacion(String folio, String mensaje) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            GeneraSolicitudViaticos.actualizarOtraJustificacion(conn, folio, mensaje);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.error("Problemas realizando rollback: " + e2, e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public void validaPartidasComprobacion(int folio) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            String mensaje = GeneraSolicitudViaticos.validaPartidasvsComprobacion(conn, folio);
            if (!mensaje.equals(""))
                throw new ViaticosException("No cuadra el importe de las Partidas - Retenciones con lo que se capturo en el Detalle del Viático. " + mensaje);
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.error("Problemas realizando rollback: " + e2, e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public void actualizarJustificacionGasolina(HttpServletRequest req) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            String folio = req.getParameter("folio");
            int kmIni = Integer.parseInt(req.getParameter("kmIni"));
            int kmFin = Integer.parseInt(req.getParameter("kmFin"));
            String litros = req.getParameter("litros");
            String rendimiento = req.getParameter("rendimiento");
            String mensaje = req.getParameter("msjJustificacionGasolina");
            GeneraSolicitudViaticos.actualizarJustificacionGasolina(conn, folio, kmIni, kmFin, litros, rendimiento, mensaje);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.error("Problemas realizando rollback: " + e2, e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public static boolean enviarCorreoini(Connection connSQL, int folioComision, int estatus, int tieneBoleto, String justificaBoletos) throws Exception {
        boolean exito = false;
        String origen = GeneraSolicitudViaticos.consultaOrigen(connSQL, folioComision);
        GeneraSolicitudViaticos.enviarCorreoIni(connSQL, folioComision, estatus, origen, tieneBoleto, justificaBoletos);
        if (tieneBoleto == 1)
            ComisionDAO.guardarDatosBoletos(connSQL, folioComision, tieneBoleto, justificaBoletos);
        exito = true;
        return exito;
    }

    public String guardarComision(int folioComision, Comision comision, int tieneBoleto, String justificaBoletos) throws Exception {
        Connection connSQL = null;
        String mensaje = "";
        boolean exito = false;
        int estatus = 1;
        try {
            connSQL = getConnection();
            Agenda agenda = AgendaDAO.consultaFechaAgendaAcumulada(connSQL, folioComision);
            //Validar incidencia
            if (GeneraSolicitudViaticos.validaIncidenciasNomina(connSQL, comision, agenda)) {
                throw new Exception("La comisión tiene una incidencia capturada en nómina por lo que no se puede guardar.");
            }
            //Validar formatos control de asistencia
            String msgFechas = GeneraSolicitudViaticos.consultaFechasCA(connSQL, agenda, comision.getEmpleado().getNoEmpleado());
            if (msgFechas != null && !msgFechas.trim().isEmpty()) {
                throw new Exception(msgFechas);
            }
            //Guardar Firmantes
            int esInternacional = ComisionDAO.esComisionInternacional(connSQL, folioComision);
            int firmantes = ComisionDAO.guardarFirmantes(connSQL, comision, esInternacional);
            ComisionDAO.insertarBitacoraRevertir(connSQL, folioComision, agenda, comision.getEmpleado().getNoEmpleado(), "Asistencia - Solicitud Viaticos");
            if (firmantes <= 0) {
                throw new Exception("No se guardaron los firmantes, verifique con el administrador.");
            }
            if (firmantes > 1) {
                int idEmpleado = GeneraSolicitudViaticos.consultaEmpleadoFirmante(connSQL, folioComision);
                boolean vacaciones = GeneraSolicitudViaticos.validaVacaciones(connSQL, idEmpleado);
                if (vacaciones) {
                    GeneraSolicitudViaticos.ActualizaFirmantesVacaciones(connSQL, folioComision, idEmpleado);
                    estatus++;
                    connSQL.commit();
                }
            }
            exito = enviarCorreoini(connSQL, folioComision, estatus, tieneBoleto, justificaBoletos);
            if (!exito) {
                throw new Exception("No se envió el correo. Verifique el correo de los firmantes.");
            }
            GeneraSolicitudViaticos.actualizarAsistencia(connSQL, agenda, comision.getEmpleado().getNoEmpleado(), "Solicitud Viáticos");
            connSQL.commit();
            return mensaje;
        } catch (Exception e) {
            if (connSQL != null) {
                try {
                    connSQL.rollback();
                } catch (Exception e2) {
                    log.warn("Rollback SQL falló: " + e2);
                }
            }
            throw e;
        } finally {
            CloseObject.closeObject(connSQL);
        }
    }
}
