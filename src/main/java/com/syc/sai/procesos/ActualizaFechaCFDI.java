package com.syc.sai.procesos;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.apache.commons.io.input.BOMInputStream;
import com.syc.cfdi.db.CloseObject;
import com.syc.cfdi.utils.FacturaUtils;
import com.syc.cfdi.v3332.Comprobante.Comprobante;
import com.syc.gestion.util.Util;
import mx.grupocorasa.sat.cfdi.v3.CFDv33;
import mx.grupocorasa.sat.cfdi.v3.CFDv3Factory;
import mx.grupocorasa.sat.cfdi.v4.CFDv40;
import java.util.Base64;

public class ActualizaFechaCFDI {

    private static StringBuilder query = new StringBuilder();

    private static StringBuilder queryUpdate = new StringBuilder("UPDATE tPagoFactura SET dFechaFactura=? WHERE cfactura = ? ");

    private static StringBuilder queryUpdateSerieFolio = new StringBuilder("UPDATE tPagoFactura SET folio = ?, serie = ? WHERE cfactura = ?");

    private static Connection conn = null;

    private static PreparedStatement psPagina = null;

    private static PreparedStatement psUpdateCFDI = null;

    static {
        query.append("SELECT d.nombre_documento, ");
        query.append("       v.unidad_disco + v.ruta_base ");
        query.append("       + v.ruta_directorio + p.nom_archivo_vol AS rurtaCFDI ");
        query.append("FROM   imx_carpeta c ");
        query.append("       INNER JOIN imx_documento d ");
        query.append("               ON c.titulo_aplicacion = d.titulo_aplicacion ");
        query.append("                  AND c.id_gabinete = d.id_gabinete ");
        query.append("                  AND c.id_carpeta = d.id_carpeta_padre ");
        query.append("       INNER JOIN imx_pagina p ");
        query.append("               ON p.titulo_aplicacion = d.titulo_aplicacion ");
        query.append("                  AND p.id_gabinete = d.id_gabinete ");
        query.append("                  AND p.id_carpeta_padre = d.id_carpeta_padre ");
        query.append("                  AND p.id_documento = d.id_documento ");
        query.append("       INNER JOIN imx_volumen v ");
        query.append("               ON p.volumen = v.volumen ");
        query.append("WHERE  c.titulo_aplicacion = ? ");
        query.append("       AND c.nombre_carpeta = 'CFDI' ");
        query.append("       AND c.id_gabinete = ? ");
        query.append("       AND d.nombre_documento LIKE '%.xml'  ");
    }

    public static void main(String[] args) throws Exception {
        String type = args[0];
        switch(type) {
            case "1":
                extractInfo(args);
                break;
            case "2":
                updateInfo(args);
                break;
            case "3":
                updateSerieFolio(args);
                break;
            default:
                masiveUpdate(args);
        }
    }

    private static void updateSerieFolio(String[] args) throws Exception {
        Path filePath = Paths.get(args[1]);
        String process = args[2];
        System.out.println("Openning Data base connection and creating statements");
        initDBObjects();
        try {
            psUpdateCFDI = conn.prepareStatement(queryUpdateSerieFolio.toString());
            System.out.println("Reading File");
            Files.lines(filePath).forEach(line -> {
                try {
                    System.out.println("Procesing : " + line);
                    psPagina.setString(1, process);
                    psPagina.setInt(2, Integer.parseInt(line));
                    ResultSet rsPagina = psPagina.executeQuery();
                    while (rsPagina.next()) {
                        String rutaPagina = rsPagina.getString(2);
                        String nombreDocumento = rsPagina.getString(1);
                        System.out.println("Procesing: " + nombreDocumento + " Ruta: " + rutaPagina);
                        File f = new File(rutaPagina);
                        DateCFDI info = new DateCFDI(f);
                        System.out.println("UUID: " + info.getUuid());
                        System.out.println("Fecha: " + info.getFechaExpedicion());
                        System.out.println("Folio: " + info.getFolio());
                        System.out.println("Serie: " + info.getSerie());
                        int afectados = updateSerieFolio(info);
                        System.out.println("Documento: " + nombreDocumento + " UUID:" + info.getUuid() + " Folio: " + info.getFolio() + " Serie " + info.getSerie() + " Con " + afectados + " registros actualizados ");
                    }
                    psPagina.clearParameters();
                    rsPagina.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Util.rollback(conn);
                    throw new RuntimeException("No fue posible actualizar el registro: " + e.toString());
                }
            });
            conn.commit();
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        } finally {
            closeDBObjects();
        }
    }

    private static int updateSerieFolio(DateCFDI info) throws SQLException {
        psUpdateCFDI.setString(1, info.getFolio());
        psUpdateCFDI.setString(2, info.getSerie());
        psUpdateCFDI.setString(3, info.getUuid());
        int afectados = psUpdateCFDI.executeUpdate();
        psUpdateCFDI.clearParameters();
        return afectados;
    }

    private static void updateInfo(String[] args) throws Exception {
        File file = new File(args[1]);
        DateCFDI info = new DateCFDI(file);
        System.out.println("UUID: " + info.getUuid());
        System.out.println("Fecha: " + info.getFechaExpedicion());
        System.out.println("Fecha Milisegundos: " + info.getFechaExpedicionMillis());
        System.out.println("Fecha transformada: " + new Timestamp(info.getFechaExpedicionMillis()));
        System.out.println("Openning Data base connection and creating statements");
        initDBObjects();
        System.out.println("Updating info");
        int affected = updateCertDate(info);
        conn.commit();
        System.out.println("Info updated: " + affected + " rows affected");
        System.out.println("About to close object");
        closeDBObjects();
        System.out.println("Finish");
    }

    private static void extractInfo(String[] args) throws Exception {
        File file = new File(args[1]);
        DateCFDI info = new DateCFDI(file);
        System.out.println("UUID: " + info.getUuid());
        System.out.println("Fecha: " + info.getFechaExpedicion());
        System.out.println("Fecha Milisegundos: " + info.getFechaExpedicionMillis());
        System.out.println("Fecha transformada: " + new Timestamp(info.getFechaExpedicionMillis()));
    }

    private static final void initDBObjects() throws Exception {
        conn = Util.getStandAloneConnection();
        psPagina = conn.prepareStatement(query.toString());
        psUpdateCFDI = conn.prepareStatement(queryUpdate.toString());
    }

    private static int updateCertDate(DateCFDI info) throws SQLException {
        psUpdateCFDI.setTimestamp(1, new Timestamp(info.getFechaExpedicionMillis()));
        psUpdateCFDI.setString(2, info.getUuid());
        int afectados = psUpdateCFDI.executeUpdate();
        psUpdateCFDI.clearParameters();
        return afectados;
    }

    public static void masiveUpdate(String[] args) {
        if (args.length < 3)
            throw new RuntimeException("Se debe proporcionar el mes y al menos un tramite");
        int mes = Integer.parseInt(args[1]);
        if (mes <= 0 || mes > 12)
            throw new RuntimeException("El mes debe ser entero entre 1 y 12");
        String[] procesos = Arrays.copyOfRange(args, 2, args.length);
        PreparedStatement ps = null;
        PreparedStatement psGabinete = null;
        try {
            initDBObjects();
            for (String proceso : procesos) {
                StringBuilder tableName = new StringBuilder("t").append(proceso).append("Encabezado");
                StringBuilder gavetaName = new StringBuilder("IMX").append(proceso);
                System.out.println("Actualizando facturas en tramite: " + tableName + ", " + gavetaName);
                ps = conn.prepareStatement("SELECT nFolio" + proceso + " FROM " + tableName + " WHERE cdocumentohaplicado = 'S' AND MONTH(faplicacion) = ?");
                ps.setInt(1, mes);
                ResultSet rsTramites = ps.executeQuery();
                psGabinete = conn.prepareStatement("SELECT * FROM " + gavetaName + " WHERE FOLIO LIKE '%-%-' + ?");
                while (rsTramites.next()) {
                    int folio = rsTramites.getInt(1);
                    System.out.println("Procesando:  " + proceso + " Folio " + folio);
                    psGabinete.setString(1, String.valueOf(folio));
                    ResultSet rsGabinete = psGabinete.executeQuery();
                    if (rsGabinete.next()) {
                        int idGabinete = rsGabinete.getInt(1);
                        System.out.println("ID Gabinete: " + idGabinete);
                        psPagina.setString(1, proceso);
                        psPagina.setInt(2, idGabinete);
                        ResultSet rsPagina = psPagina.executeQuery();
                        while (rsPagina.next()) {
                            try {
                                String rutaPagina = rsPagina.getString(2);
                                String nombreDocumento = rsPagina.getString(1);
                                System.out.println("Procesando: " + nombreDocumento + " Ruta: " + rutaPagina);
                                File f = new File(rutaPagina);
                                DateCFDI info = new DateCFDI(f);
                                LocalDateTime fechaExpedicion = info.getFechaExpedicion();
                                String uuid = info.getUuid();
                                int afectados = updateCertDate(info);
                                System.out.println("Documento: " + nombreDocumento + " UUID:" + uuid + " Fecha de Expedicion: " + fechaExpedicion + " Afectados " + afectados);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        psPagina.clearParameters();
                        rsPagina.close();
                    }
                    rsGabinete.close();
                    rsGabinete = null;
                }
                rsTramites.close();
                rsTramites = null;
                psGabinete.close();
                psGabinete = null;
            }
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(psGabinete);
            closeDBObjects();
        }
    }

    private static void closeDBObjects() {
        CloseObject.closeObject(conn);
        CloseObject.closeObject(psPagina);
        CloseObject.closeObject(psUpdateCFDI);
    }
}

class DateCFDI {

    private LocalDateTime fechaExpedicion;

    private String uuid;

    private long fechaExpedicionMillis;

    private String folio;

    private String serie;

    public DateCFDI(File f) throws Exception {
        String version = FacturaUtils.readVersion(f);
        InputStream in = new FileInputStream(f);
        InputStream inBOM = new BOMInputStream(in);
        Comprobante comprobante = null;
        if ("3.3".equals(version)) {
            CFDv33 cfd = (CFDv33) CFDv3Factory.load(inBOM);
            comprobante = new Comprobante((mx.grupocorasa.sat.cfd._33.Comprobante) ((CFDv33) cfd).getComprobanteDocument());
        } else if ("4.0".equals(version)) {
            CFDv40 cfd = new CFDv40(inBOM);
            comprobante = new Comprobante((mx.grupocorasa.sat.cfd._40.Comprobante) ((CFDv40) cfd).getComprobanteDocument());
        }
        this.fechaExpedicion = comprobante.getFechaExpedicion();
        this.setUuid(comprobante.getUUID());
        this.setFechaExpedicionMillis(comprobante.getFechaExpedicionMillis());
        this.setSerie(comprobante.getFolioSerie());
        this.setFolio(comprobante.getFolio());
    }

    public LocalDateTime getFechaExpedicion() {
        return fechaExpedicion;
    }

    public String getUuid() {
        return uuid;
    }

    public void setFechaExpedicion(LocalDateTime fechaExpedicion) {
        this.fechaExpedicion = fechaExpedicion;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getFechaExpedicionMillis() {
        return fechaExpedicionMillis;
    }

    public void setFechaExpedicionMillis(long fechaExpedicionMillis) {
        this.fechaExpedicionMillis = fechaExpedicionMillis;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }
}
