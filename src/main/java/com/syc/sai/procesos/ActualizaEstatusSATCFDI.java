package com.syc.sai.procesos;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.syc.cfdi.db.CloseObject;
import com.syc.gestion.util.Util;
import com.syc.ws.validacionSAT.Acuse;
import com.syc.ws.validacionSAT.impl.FacturaSATValidacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActualizaEstatusSATCFDI {

    private static final Logger log = LoggerFactory.getLogger(ActualizaCFDI.class);

    private StringBuilder queryUpdate;

    private StringBuilder querySel;

    private FacturaSATValidacion fsv = null;

    private String rfcReceptor = "CNF010405EG1";

    private String urlConn;

    private String driverName;

    private String user;

    private String pass;

    private List<CFDIConsulta> cfdiList = new ArrayList<>();

    public ActualizaEstatusSATCFDI(String urlConn, String driverName, String user, String pass) throws Exception {
        this.urlConn = urlConn;
        this.driverName = driverName;
        this.user = user;
        this.pass = pass;
        log.debug("Iniciando Objetos.");
        createQuery();
        fsv = new FacturaSATValidacion();
    }

    private Connection createConn(String urlConn, String driverName, String user, String pass) throws ClassNotFoundException, SQLException {
        Connection conn = null;
        Class.forName(driverName);
        conn = DriverManager.getConnection(urlConn, user, pass);
        conn.setAutoCommit(false);
        return conn;
    }

    private void createQuery() {
        querySel = new StringBuilder();
        querySel.append("SELECT	cfactura AS UUID, cRFCFactura AS emisor, mImporteConIVA AS total  ");
        querySel.append("  FROM	tpagofactura WITH(NOLOCK)  ");
        querySel.append(" WHERE	LEN(cfactura) = 36  ");
        querySel.append("   AND	( cRFCFactura <> 'OFICIOTRANSITO' AND cRFCFactura <> 'OFICIOCTOFED') ");
        querySel.append("   AND	cEstatusSAT IS NULL ");
        queryUpdate = new StringBuilder("UPDATE tpagofactura SET dFechaConsultaEstatus = GETDATE(), cEstatusSAT = ? WHERE cfactura = ?");
    }

    public static void main(String[] args) throws Exception {
        String urlConn = args[0];
        String driverName = args[1];
        String user = args[2];
        String pass = args[3];
        ActualizaEstatusSATCFDI actualizador = null;
        log.info("Object: {}", "Iniciando proceso: \nURL: " + urlConn + "\nDriver:" + driverName + "\nuser:" + user + "\npass:" + pass);
        actualizador = new ActualizaEstatusSATCFDI(urlConn, driverName, user, pass);
        actualizador.actualizaEstausSAT();
    }

    private void leeCFDIProcesar() throws SQLException {
        Connection conn = null;
        PreparedStatement psSelect = null;
        ResultSet rs = null;
        try {
            conn = createConn(urlConn, driverName, user, pass);
            psSelect = conn.prepareStatement(querySel.toString());
            rs = psSelect.executeQuery();
            while (rs.next()) {
                CFDIConsulta consulta = new CFDIConsulta(rs.getString("UUID"), rs.getString("emisor"), rfcReceptor, rs.getBigDecimal("total"));
                cfdiList.add(consulta);
            }
        } catch (Exception e) {
            throw new SQLException();
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(psSelect);
            CloseObject.closeObject(conn);
        }
    }

    private void actualizaEstausSAT() throws SQLException {
        leeCFDIProcesar();
        int i = 1;
        for (CFDIConsulta cfdi : cfdiList) {
            log.info("Object: {}", "Procesando CFDI " + i++ + " de " + cfdiList.size());
            Connection connUpdate = null;
            PreparedStatement psUpdate = null;
            try {
                connUpdate = createConn(urlConn, driverName, user, pass);
                psUpdate = connUpdate.prepareStatement(queryUpdate.toString());
                fsv.setRfcEmisor(cfdi.getRfcEmisor());
                fsv.setRfcReceptor(cfdi.getRfcReceptor());
                fsv.setTotalFactura(cfdi.getTotal());
                fsv.setUuid(cfdi.getUuid());
                Acuse acuse = null;
                try {
                    acuse = fsv.validaCFDI();
                } catch (Exception e) {
                    fsv.setTotalFactura(Util.ZERO);
                    acuse = fsv.validaCFDI();
                }
                psUpdate.setString(1, acuse.getEstado());
                psUpdate.setString(2, cfdi.getUuid());
                int afectados = psUpdate.executeUpdate();
                connUpdate.commit();
                log.info("Object: {}", "Se valido exitosamente la factura: " + cfdi.getUuid() + " Resultado: " + acuse.getEstado() + " Se afectaron:  " + afectados + " registros.");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                if (connUpdate != null)
                    try {
                        connUpdate.rollback();
                    } catch (Exception e2) {
                        log.warn("Object: {}", "Problemas en rollback ::actualizaEstausSAT() [" + e2.toString() + "]");
                    }
            } finally {
                CloseObject.closeObject(connUpdate);
                CloseObject.closeObject(psUpdate);
            }
        }
    }

    class CFDIConsulta {

        private String uuid;

        private String rfcEmisor;

        private String rfcReceptor;

        private BigDecimal total;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getRfcEmisor() {
            return rfcEmisor;
        }

        public void setRfcEmisor(String rfcEmisor) {
            this.rfcEmisor = rfcEmisor;
        }

        public String getRfcReceptor() {
            return rfcReceptor;
        }

        public void setRfcReceptor(String rfcReceptor) {
            this.rfcReceptor = rfcReceptor;
        }

        public BigDecimal getTotal() {
            return total;
        }

        public void setTotal(BigDecimal total) {
            this.total = total;
        }

        @Override
        public String toString() {
            return "CFDIConsulta [uuid=" + uuid + ", rfcEmisor=" + rfcEmisor + ", rfcReceptor=" + rfcReceptor + ", total=" + total + "]";
        }

        public CFDIConsulta(String uuid, String rfcEmisor, String rfcReceptor, BigDecimal total) {
            super();
            this.uuid = uuid;
            this.rfcEmisor = rfcEmisor;
            this.rfcReceptor = rfcReceptor;
            this.total = total;
        }
    }
}
