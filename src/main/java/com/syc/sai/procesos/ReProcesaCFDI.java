package com.syc.sai.procesos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import com.syc.cfdi.ComponentesFactura;
import com.syc.cfdi.FacturaBusinessLogic;
import com.syc.cfdi.core.ExtraccionFacturas;
import com.syc.cfdi.core.FacturaManager;
import com.syc.cfdi.db.CloseObject;
import com.syc.fortimax.core.Carpeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ReProcesaCFDI extends ProcesoSAI {

    private String tituloAplicacion;

    private int folio;

    private String login;

    public String getTituloAplicacion() {
        return tituloAplicacion;
    }

    public void setTituloAplicacion(String tituloAplicacion) {
        this.tituloAplicacion = tituloAplicacion;
    }

    public int getFolio() {
        return folio;
    }

    public void setFolio(int folio) {
        this.folio = folio;
    }

    private static final Logger log = LoggerFactory.getLogger(ReProcesaCFDI.class);

    public ReProcesaCFDI(String urlConn, String driverName, String user, String pass) throws ClassNotFoundException, SQLException {
        super(urlConn, driverName, user, pass);
        log.debug("Iniciando Objetos.");
    }

    public static void main(String[] args) throws Exception {
        String urlConn = args[0];
        String driverName = args[1];
        String user = args[2];
        String pass = args[3];
        String fileZip = args[4];
        String tApp = args[5];
        String folioStr = args[6];
        String loginStr = args[7];
        ReProcesaCFDI procesadorCFDI = new ReProcesaCFDI(urlConn, driverName, user, pass);
        procesadorCFDI.setFolio(Integer.parseInt(folioStr));
        procesadorCFDI.setTituloAplicacion(tApp);
        procesadorCFDI.setLogin(loginStr);
        procesadorCFDI.procesaCFDI(fileZip);
    }

    private void procesaCFDI(String zipFile) throws Exception {
        log.info("Object: {}", "Iniciando proceso: \nURL: " + getUrlConn() + "\nDriver:" + getDriverName() + "\nuser:" + getUser() + "\npass:" + getPass());
        log.info("Object: {}", "Procesano archivo: " + zipFile);
        FacturaBusinessLogic fbl = new FacturaBusinessLogic();
        Connection conn = createConn();
        fbl.setValidacionSAT(true);
        fbl.setNotificaErroresSAT(false);
        fbl.setNotificaErroresEFA(false);
        String tipoModulo = "";
        ExtraccionFacturas ef = fbl.extraeFacturas(conn, zipFile, getTituloAplicacion(), "CNF010405EG1", getFolio(), false, false, tipoModulo);
        if (ef.getErrores().size() == 0) {
            Map<String, ComponentesFactura> facturas = ef.getFacturas();
            Carpeta cfdi = FacturaManager.obtenCarpetaDestino(conn, getTituloAplicacion(), getIdGabinete(conn), "CFDI", getLogin());
            for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
                String facturaNombre = i.next();
                log.debug("Object: " + String.valueOf("Insertando factura [" + facturaNombre + "] "));
                ComponentesFactura cf = facturas.get(facturaNombre);
                FacturaManager.insertaArchivosFactura(conn, facturaNombre, cf, cfdi, getLogin());
            }
            FacturaManager.insertaInformacionFacturas(conn, getTituloAplicacion(), getFolio(), facturas, false);
            conn.commit();
        }
        conn.close();
    }

    private int getIdGabinete(Connection conn) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT id_gabinete FROM IMX" + getTituloAplicacion() + " WHERE folio LIKE '%-%-" + getFolio() + "'");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
            else
                throw new RuntimeException("No se encontro gabinete para " + getTituloAplicacion() + " con folio " + getFolio());
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
