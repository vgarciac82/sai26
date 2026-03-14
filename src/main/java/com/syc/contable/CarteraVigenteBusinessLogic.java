package com.syc.contable;

import java.sql.Connection;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Base64;

public class CarteraVigenteBusinessLogic {

    private static DataSource ds = null;

    public CarteraVigenteBusinessLogic() {
        if (ds != null)
            return;
        Context initContext;
        try {
            initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            ds = (DataSource) envContext.lookup("jdbc/proinpro");
        } catch (NamingException ne) {
            throw new RuntimeException("No se encontro la fuente 'jdbc/proinpro'");
        }
    }

    public double validaCartera(String ep) throws Exception {
        Connection conn = null;
        double cantidad = 0.0d;
        String respuesta = "";
        conn = ds.getConnection();
        respuesta = CarteraVigenteManager.validaExisteCartera(conn, ep);
        if ("OK".equals(respuesta)) {
            respuesta = CarteraVigenteManager.validaVigenciaCartera(conn, ep);
            if ("OK".equals(respuesta)) {
                respuesta = CarteraVigenteManager.validaEntidadFederativa(conn, ep);
                if ("OK".equals(respuesta)) {
                    cantidad = CarteraVigenteManager.getMontoTotalCartera(conn, ep);
                    return cantidad;
                } else {
                    throw new Exception("La relación cartera/entidad federativa, no es válida. Cartera: " + ep.substring(45, 11) + "Entidad Federativa:" + ep.substring(42, 2));
                }
            } else {
                throw new Exception("Cartera no vigente: " + ep.substring(45, 11));
            }
        } else {
            throw new Exception("Cartera inexistente " + ep.substring(45, 11));
        }
    }
}
