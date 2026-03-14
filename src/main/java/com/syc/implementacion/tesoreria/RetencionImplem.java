package com.syc.implementacion.tesoreria;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.Documento;
import com.syc.gestion.TipoCasoInterface;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.contable.core.RetencionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class RetencionImplem implements TipoCasoInterface {

    private static final Logger log = LoggerFactory.getLogger(RetencionImplem.class);

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
    }

    @Override
    public void onTerminaCaso(Connection conn, String u_login, Caso c, String observ, String[] resp, String[] oper, Map data) throws SQLException {
        try {
            RetencionManager.descartaRete(conn, u_login, c);
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
