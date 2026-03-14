package com.axtel.proveedores;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.Documento;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.TipoCasoInterface;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.EjercicioFiscalManager;
import java.util.Base64;

public class AltaProveedorCorta implements TipoCasoInterface {

    CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);

    @Override
    public boolean buscaPorExpediente(Caso c, String u_login) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onIniciaCaso(Connection conn, String u_login, Caso c) throws SQLException {
        try {
            c.getCasoDato("FOLIO").setValor(c.getFolio());
            c.getCasoDato("DOCUMENT_DATE").setValor(Util.getTodayESMX());
            c.getCasoDato("FISCAL_YEAR").setValor(EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal());
            cbl.actualizaCasoDato(conn, c, Util.readValuesCasoDato(c.getCasoDato()));
            int idGabinete = cbl.creaExpediente(conn, u_login, c);
            c.setIdGabinete(idGabinete);
            CasoManager.update(conn, c);
        } catch (Exception e) {
            throw new SQLException(e.toString(), e);
        }
    }

    @Override
    public void onCreateExpediente(Connection conn, String u_login, Caso c, Aplicacion app) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void onRecibeDocumento(Connection conn, Caso c, Documento d, boolean isSaveEvent) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void onEjecutaCaso(Connection conn, String u_login, Caso c, int id_caso_oper) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void onAvanzaCaso(Connection conn, String u_login, Caso c, int id_caso_oper) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void onTerminaCaso(Connection conn, String u_login, Caso c, String observ, String[] resp, String[] oper, Map data) throws SQLException {
        // TODO Auto-generated method stub
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
