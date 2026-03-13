package com.syc.sai.procesos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.syc.cfdi.core.FacturaManager;
import com.syc.cfdi.db.CloseObject;
import com.syc.cfdi.v3332.Comprobante.Comprobante;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActualizaCFDI {

    private static final Logger log = LoggerFactory.getLogger(ActualizaCFDI.class);

    private StringBuilder query;

    private StringBuilder queryUpdate;

    private StringBuilder queryApp;

    private List<StringBuilder> faltantes = new ArrayList<>();

    private String urlConn;

    private String driverName;

    private String user;

    private String pass;

    private StringBuilder queryReteInsert;

    public ActualizaCFDI(String urlConn, String driverName, String user, String pass) throws ClassNotFoundException, SQLException {
        log.debug("Iniciando Objetos.");
        this.urlConn = urlConn;
        this.driverName = driverName;
        this.user = user;
        this.pass = pass;
        createQuery();
    }

    private Connection createConn(String urlConn, String driverName, String user, String pass) throws ClassNotFoundException, SQLException {
        Connection conn = null;
        Class.forName(driverName);
        conn = DriverManager.getConnection(urlConn, user, pass);
        conn.setAutoCommit(false);
        return conn;
    }

    private void createQuery() {
        query = new StringBuilder();
        query.append("select v.UNIDAD_DISCO +  v.RUTA_BASE + v.RUTA_DIRECTORIO + p.NOM_ARCHIVO_VOL AS ruta_archivo,*  ");
        query.append("from IMX_CARPETA c ");
        query.append("inner join IMX_DOCUMENTO d ");
        query.append("on c.TITULO_APLICACION = d.TITULO_APLICACION ");
        query.append("and c.ID_GABINETE = d.ID_GABINETE ");
        query.append("and c.ID_CARPETA = d.ID_CARPETA_PADRE ");
        query.append("INNER JOIN imx_pagina p ");
        query.append("on  ");
        query.append("d.TITULO_APLICACION = p.TITULO_APLICACION ");
        query.append("and d.ID_GABINETE = p.ID_GABINETE ");
        query.append("and d.ID_CARPETA_PADRE = p.ID_CARPETA_PADRE ");
        query.append("and d.ID_DOCUMENTO = p.ID_DOCUMENTO ");
        query.append("inner join IMX_VOLUMEN v ");
        query.append("on p.VOLUMEN = v.VOLUMEN ");
        query.append("where c.NOMBRE_CARPETA = 'CFDI' ");
        query.append("and p.NOM_ARCHIVO_ORG like '%.xml' ");
        queryApp = new StringBuilder();
        queryApp.append("select v.UNIDAD_DISCO +  v.RUTA_BASE + v.RUTA_DIRECTORIO + p.NOM_ARCHIVO_VOL AS ruta_archivo,*  ");
        queryApp.append("from IMX_CARPETA c ");
        queryApp.append("inner join IMX_DOCUMENTO d ");
        queryApp.append("on c.TITULO_APLICACION = d.TITULO_APLICACION ");
        queryApp.append("and c.ID_GABINETE = d.ID_GABINETE ");
        queryApp.append("and c.ID_CARPETA = d.ID_CARPETA_PADRE ");
        queryApp.append("INNER JOIN imx_pagina p ");
        queryApp.append("on  ");
        queryApp.append("d.TITULO_APLICACION = p.TITULO_APLICACION ");
        queryApp.append("and d.ID_GABINETE = p.ID_GABINETE ");
        queryApp.append("and d.ID_CARPETA_PADRE = p.ID_CARPETA_PADRE ");
        queryApp.append("and d.ID_DOCUMENTO = p.ID_DOCUMENTO ");
        queryApp.append("inner join IMX_VOLUMEN v ");
        queryApp.append("on p.VOLUMEN = v.VOLUMEN ");
        queryApp.append("where c.NOMBRE_CARPETA IN ('CFDI','NC') ");
        queryApp.append("and p.NOM_ARCHIVO_ORG like '%.xml' ");
        queryApp.append("and c.TITULO_APLICACION = ? ");
        queryApp.append("and c.id_gabinete = ? ");
        queryUpdate = new StringBuilder("UPDATE tpagofactura SET dFechaTimbrado = ?, dFechaFactura = ?,cRegimenFiscal = ?,cMetodoPago = ?, cRazonSocial = ? WHERE cfactura = ?");
        queryReteInsert = new StringBuilder("INSERT INTO tPagoFacturaRetencion( cTipoPago, nFolioPago, UUID, cNombreRetencion, mImporteRetencion, cBase, cTipoFactor, cTasaCuota ) VALUES( ?, ?, ?, ?, ?, ?, ?, ? ) ");
    }

    public static void main(String[] args) throws Exception {
        String urlConn = args[0];
        String driverName = args[1];
        String user = args[2];
        String pass = args[3];
        ActualizaCFDI actualizador = null;
        if (args.length == 5) {
            File fileFolios = new File(args[4]);
            log.info("Iniciando proceso de archivo: " + fileFolios + "\nURL: " + urlConn + "\nDriver:" + driverName + "\nuser:" + user + "\npass:" + pass);
            actualizador = new ActualizaCFDI(urlConn, driverName, user, pass);
            actualizador.actualizaInformacionFechas(fileFolios);
        }
        log.info(actualizador.getFaltantes());
    }

    private void actualizaInformacionFechas(File fileFolios) throws SQLException {
        log.info("Procesano archivo: " + fileFolios);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileFolios));
            String line = reader.readLine();
            while (line != null) {
                Connection conn = null;
                ResultSet rsFolios = null;
                Statement stmntFolios = null;
                PreparedStatement psApp = null;
                ResultSet rs = null;
                try {
                    conn = createConn(this.urlConn, this.driverName, this.user, this.pass);
                    stmntFolios = conn.createStatement();
                    psApp = conn.prepareStatement(queryApp.toString());
                    String[] info = line.split(",");
                    log.info("Procesando entrada: " + line);
                    rsFolios = stmntFolios.executeQuery("SELECT id_gabinete FROM imx" + info[0] + " WHERE folio LIKE '%-%-" + info[1] + "'");
                    if (rsFolios.next()) {
                        int idCabinet = rsFolios.getInt(1);
                        log.info("Se encuentra folio de gabinete: " + idCabinet);
                        psApp.setString(1, info[0]);
                        psApp.setInt(2, idCabinet);
                        rs = psApp.executeQuery();
                        procesaRS(rs);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    CloseObject.closeObject(stmntFolios);
                    CloseObject.closeObject(psApp);
                    CloseObject.closeObject(rsFolios);
                    CloseObject.closeObject(rs);
                    CloseObject.closeObject(conn);
                    line = reader.readLine();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (Exception e2) {
                    log.warn(e2.getMessage(), e2);
                }
        }
    }

    private void procesaRS(ResultSet rs) throws SQLException {
        StringBuilder rutaArchivo = null;
        while (rs.next()) {
            Connection connUpdate = null;
            PreparedStatement psUpdate = null;
            PreparedStatement psInsert = null;
            try {
                connUpdate = createConn(this.urlConn, this.driverName, this.user, this.pass);
                psUpdate = connUpdate.prepareStatement(queryUpdate.toString());
                psInsert = connUpdate.prepareStatement(queryReteInsert.toString());
                rutaArchivo = new StringBuilder(rs.getString("ruta_archivo"));
                Comprobante comprobante = FacturaManager.cargaComprobante(new File(rutaArchivo.toString()));
                long fechaTimbradoMillis = comprobante.getFechaTimbradoMillis();
                long fechaEmitidoMillis = comprobante.getFechaExpedicionMillis();
                String uuid = comprobante.getUUID();
                String regimenFiscal = comprobante.getRegimenEmisor();
                String metodoPago = comprobante.getMetodoPago();
                log.info("Actualizando Archivo: " + rutaArchivo + " factura: " + uuid + " Fecha de Timbrado: " + fechaTimbradoMillis + " Metodo de Pago: " + metodoPago + " Razon Social: " + comprobante.getNombreEmisor());
                psUpdate.setTimestamp(1, new Timestamp(fechaTimbradoMillis));
                psUpdate.setTimestamp(2, new Timestamp(fechaEmitidoMillis));
                psUpdate.setString(3, regimenFiscal);
                psUpdate.setString(4, metodoPago);
                psUpdate.setString(5, comprobante.getNombreEmisor());
                psUpdate.setString(6, uuid);
                log.info("Ejecutando: " + queryUpdate.toString() + "\nActualizando Archivo: " + rutaArchivo + "\n( 1, " + new Timestamp(fechaTimbradoMillis) + ")" + "( 2, " + new Timestamp(fechaEmitidoMillis) + ")" + "( 3, " + regimenFiscal + " )" + "( 4, " + metodoPago + " )" + "( 5, " + comprobante.getNombreEmisor() + " )" + "( 6, " + uuid + " )");
                int actualizados = psUpdate.executeUpdate();
                log.info("Se actualizaron: " + actualizados + " facturas");
                connUpdate.commit();
            } catch (FileNotFoundException e) {
                log.error(e.getMessage(), e);
                if (connUpdate != null)
                    try {
                        connUpdate.rollback();
                    } catch (Exception e2) {
                        log.warn("Problemas en rollback ::procesaRS " + e2.toString());
                    }
                getFaltantes().add(rutaArchivo);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                if (connUpdate != null)
                    try {
                        connUpdate.rollback();
                    } catch (Exception e2) {
                        log.warn("Problemas en rollback ::procesaRS " + e2.toString());
                    }
            } finally {
                CloseObject.closeObject(psUpdate);
                CloseObject.closeObject(connUpdate);
            }
        }
    }

    public List<StringBuilder> getFaltantes() {
        return faltantes;
    }

    public void setFaltantes(List<StringBuilder> faltantes) {
        this.faltantes = faltantes;
    }
}
