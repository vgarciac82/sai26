package com.syc.implementacion.tesoreria;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.syc.contable.core.SolicitudTramiteFirmaElectronica;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.Documento;
import com.syc.gestion.TipoCasoInterface;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioManager;
import com.syc.gestion.servlet.GestionServlet;
import com.syc.sai.firmaElectronica.core.FirmaElectronicaManager;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import com.syc.solicitudviaticos.core.ComisionSinViaticosManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ComisionSinViaticos implements TipoCasoInterface {

    private static final Logger log = LoggerFactory.getLogger(ComisionSinViaticos.class);

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
    public void onAvanzaCaso(Connection conn, String u_login, Caso c, int id_caso_oper) throws SQLException {
        if (id_caso_oper == 5)
            try {
                log.info("Object: {}", String.format("Ejecutando proceso Avanza Caso. Login[%s] Caso[%d]  Operacion[%d]", u_login, c.getIdCaso(), id_caso_oper));
                String document = c.getTipoCaso().getGavetaAsociada();
                String detail = StringUtils.isBlank(SolicitudFirmaElectronica.RELACION_TRAMITE_TABLA_D.get(document)) ? ("t" + document + "Detalle") : SolicitudFirmaElectronica.RELACION_TRAMITE_TABLA_D.get(document);
                String header = StringUtils.isBlank(SolicitudFirmaElectronica.RELACION_TRAMITE_TABLA_E.get(document)) ? ("t" + document + "Encabezado") : SolicitudFirmaElectronica.RELACION_TRAMITE_TABLA_E.get(document);
                String field = StringUtils.isBlank(SolicitudFirmaElectronica.RELACION_TRAMITE_KEY.get(document)) ? ("nFolio" + document) : SolicitudFirmaElectronica.RELACION_TRAMITE_KEY.get(document);
                int folio = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
                SolicitudFirmaElectronica solicitud = new SolicitudTramiteFirmaElectronica();
                solicitud.setDetail(detail);
                solicitud.setDocument(document);
                solicitud.setField(field);
                solicitud.setFileExtension("pdf");
                solicitud.setHeader(header);
                solicitud.setIdField(folio);
                solicitud.setReportPath(GestionServlet.reportPath);
                if (solicitud.esFirmaElectronica(conn)) {
                    String lastDocName = solicitud.getDocName();
                    solicitud.setUsuario(UsuarioManager.select(conn, new Usuario(u_login)));
                    solicitud.setDocName("Solicitud Firmada");
                    FirmaElectronicaManager.generaArchivoFirma(conn, solicitud, solicitud.getFolder(), false);
                    solicitud.notificaOperacionPendiente(conn, SolicitudFirmaElectronica.VO_BO);
                    solicitud.setDocName(lastDocName);
                } else {
                    FirmaElectronicaManager.avanzaEstatusSICOP(conn, solicitud, 0);
                    ComisionSinViaticosManager.actualizaAplicacion(conn, "S", solicitud.getIdField());
                }
            } catch (Exception e) {
                throw new SQLException(e);
            }
    }

    @Override
    public void onTerminaCaso(Connection conn, String u_login, Caso c, String observ, String[] resp, String[] oper, Map data) throws SQLException {
        int nFolioComision = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
        try {
            ComisionSinViaticosManager.liberaVuelos(conn, nFolioComision);
            ComisionSinViaticosManager.actualizaAplicacion(conn, "N", nFolioComision);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void onVenceCaso(Connection conn, String folio, int id_caso, int id_tc, int id_oper, int porc) throws SQLException {
    }

    @Override
    public void onSolicitaFirmaElectronica(Caso c, Usuario u, String reportPath) throws Exception {
    }
}
