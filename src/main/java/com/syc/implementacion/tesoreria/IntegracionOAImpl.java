/**
 */
package com.syc.implementacion.tesoreria;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.syc.contable.core.OperacionAjenaManager;
import com.syc.contable.core.OperacionesAjenasIntManager;
import com.syc.contable.core.SolicitudPagoFirmaElectronica;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.core.impl.EgresoOPERAJENASEncabezado;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.Documento;
import com.syc.gestion.TipoCasoInterface;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioManager;
import com.syc.gestion.servlet.GestionServlet;
import com.syc.sai.firmaElectronica.core.FirmaElectronicaManager;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vicente.garcia
 */
public class IntegracionOAImpl implements TipoCasoInterface {

    private static final Logger log = LoggerFactory.getLogger(IntegracionOAImpl.class);

    /**
     */
    public IntegracionOAImpl() {
    }

    @Override
    public boolean buscaPorExpediente(Caso c, String u_login) {
        return false;
    }

    @Override
    public void onIniciaCaso(Connection conn, String u_login, Caso c) throws SQLException {
    }

    @Override
    public void onCreateExpediente(Connection conn, String u_login, Caso c, Aplicacion app) throws SQLException {
    }

    @Override
    public void onRecibeDocumento(Connection conn, Caso c, Documento d, boolean isSaveEvent) throws SQLException {
    }

    @Override
    public void onEjecutaCaso(Connection conn, String u_login, Caso c, int id_caso_oper) throws SQLException {
    }

    @Override
    public void onAvanzaCaso(Connection conn, String u_login, Caso casoIntegracion, int id_caso_oper) throws SQLException {
        log.trace("Avanzando tramite de Operaciones ajenas");
        log.info("Object: {}", "onAvanzaCaso ---> u_login " + u_login);
        log.info("Object: {}", "onAvanzaCaso ---> c " + casoIntegracion);
        log.info("Object: {}", "onAvanzaCaso ---> id_caso_oper " + id_caso_oper);
        CasoOperacion operacion = casoIntegracion.getCasoOperacion(0);
        log.info("Object: {}", "onAvanzaCaso ---> getIdOperacion " + operacion.getIdOperacion());
        log.info("Object: {}", "onAvanzaCaso ---> getOperacion().getIdOperacion() " + operacion.getOperacion().getIdOperacion());
        log.info("Object: {}", "onAvanzaCaso ---> getOperacion().getResponsable() " + operacion.getOperacion().getResponsable());
        log.info("Object: {}", "onAvanzaCaso ---> Tipo Pago: " + casoIntegracion.getTipoCaso().getGavetaAsociada());
        /*
		 * Se carga el encabezado para saber si fue firma electronica. Si asi
		 * fue se procesa la firma.
		 */
        if (id_caso_oper == 2) {
            try {
                List<Integer> foliosIntegrados = OperacionesAjenasIntManager.getFoliosIntegrados(conn, casoIntegracion.getFolio());
                for (int folioIntegrado : foliosIntegrados) {
                    Caso caso = CasoManager.findByFolioLike(conn, "OPERAJENAS", String.valueOf(folioIntegrado));
                    OperacionAjenaManager.validaGabineteCaso(conn, caso);
                    EgresoEncabezado oaEncabezado = new EgresoOPERAJENASEncabezado();
                    oaEncabezado = oaEncabezado.cargaEncabezado(folioIntegrado);
                    SolicitudFirmaElectronica solicitudPagoPrinter = new SolicitudPagoFirmaElectronica();
                    solicitudPagoPrinter.setDetail("tOperAjenasDetalle");
                    solicitudPagoPrinter.setDocument(caso.getTipoCaso().getGavetaAsociada());
                    solicitudPagoPrinter.setField("nFolioOperAjenas");
                    solicitudPagoPrinter.setFileExtension("pdf");
                    solicitudPagoPrinter.setHeader("tOperAjenasEncabezado");
                    solicitudPagoPrinter.setIdField(folioIntegrado);
                    solicitudPagoPrinter.setReportPath(GestionServlet.reportPath);
                    solicitudPagoPrinter.setDocName("Solicitud Firmada");
                    if ("S".equalsIgnoreCase(StringUtils.trimToEmpty("" + oaEncabezado.getEsFirmaElectronica()))) {
                        Usuario u = new Usuario();
                        u.setLogin(u_login);
                        u = UsuarioManager.select(conn, u);
                        solicitudPagoPrinter.setUsuario(u);
                        FirmaElectronicaManager.generaArchivoFirma(conn, solicitudPagoPrinter, "Solicitud Firmada", false);
                        solicitudPagoPrinter.notificaOperacionPendiente(conn, SolicitudFirmaElectronica.VO_BO);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new SQLException(e);
            }
        }
    }

    @Override
    public void onTerminaCaso(Connection conn, String u_login, Caso casoIntegracion, String observ, String[] resp, String[] oper, @SuppressWarnings("rawtypes") Map data) throws SQLException {
    }

    @Override
    public void onVenceCaso(Connection conn, String folio, int id_caso, int id_tc, int id_oper, int porc) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSolicitaFirmaElectronica(Caso c, Usuario u, String reportPath) throws Exception {
        // TODO Auto-generated method stub
    }
}
