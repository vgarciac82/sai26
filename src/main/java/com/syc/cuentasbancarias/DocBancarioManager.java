package com.syc.cuentasbancarias;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.syc.cfdi.core.FacturaManager;
import com.syc.fortimax.core.Carpeta;
import com.syc.fortimax.core.CarpetaManager;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.fortimax.core.OrgCarpeta;
import com.syc.fortimax.core.OrgCarpetaManager;
import com.syc.fortimax.core.Pagina;
import com.syc.fortimax.core.PaginaManager;
import com.syc.fortimax.core.Volumen;
import com.syc.fortimax.core.VolumenManager;
import com.syc.gestion.core.Caso;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.contabilidad.utils.db.RSToTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocBancarioManager {

    private static final Logger log = LoggerFactory.getLogger(FacturaManager.class);

    public static Carpeta obtenCarpetaDestino(Connection conn, Caso c, String nombreCarpeta, String uLogin) throws Exception {
        log.trace("Object: {}", "Inicia busqueda de carpeta [" + nombreCarpeta + "]");
        long start = System.currentTimeMillis();
        Carpeta carpeta = CarpetaManager.getCarpetaByName(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), nombreCarpeta);
        if (carpeta == null) {
            log.trace("Object: {}", "No existe la carpeta [" + nombreCarpeta + "] se creara.");
            Carpeta modelo = new Carpeta();
            modelo.setTituloAplicacion(c.getTipoCaso().getGavetaAsociada());
            modelo.setIdGabinete(c.getIdGabinete());
            modelo.setIdCarpeta(CarpetaManager.getNextIdCarpeta(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete()));
            modelo.setNombreCarpeta(nombreCarpeta);
            modelo.setNombreUsuario(uLogin);
            modelo.setBanderaRaiz("N");
            modelo.setDescripcion("Carpeta que contiene las facturas que ampara el pago");
            modelo.setPassword("-1");
            carpeta = CarpetaManager.insertaCarpeta(conn, modelo);
            OrgCarpeta oc = new OrgCarpeta();
            oc.setIdCarpetaHija(carpeta.getIdCarpeta());
            oc.setIdCarpetaPadre(0);
            oc.setIdGabinete(c.getIdGabinete());
            oc.setNombreHija(carpeta.getNombreCarpeta());
            oc.setTituloAplicacion(carpeta.getTituloAplicacion());
            OrgCarpetaManager.insert(conn, oc);
            log.trace("Object: {}", "Carpeta [" + nombreCarpeta + "] creada con exito.");
        }
        long stop = System.currentTimeMillis();
        log.trace("Object: {}", "Finaliza busqueda de carpeta en [" + ((stop - start) / 1000) + "] s.");
        return carpeta;
    }

    public static int insertaComprobante(Connection conn, String rutaArchivo, Carpeta c, Caso caso, String uLogin, String cuenta, CuentaBancaria cuentaBancaria) throws Exception {
        log.trace("Iniciando la insercion de oficio");
        int total = 0;
        long start = System.currentTimeMillis();
        log.trace("Insertando archivo");
        File origen = new File(rutaArchivo);
        log.trace("Object: {}", "Insertando el archivo: " + origen.getName());
        String tmpFile = origen.getName();
        int pos = tmpFile.lastIndexOf('.') + 1;
        String ext = pos != -1 ? tmpFile.substring(pos) : "";
        DocBancarioManager.insertaDocumento(conn, c.getTituloAplicacion(), c.getIdGabinete(), c.getIdCarpeta(), cuenta, ext, uLogin, origen.getAbsolutePath(), cuentaBancaria);
        log.trace("Archivo insertado exitosamente");
        total++;
        long stop = System.currentTimeMillis();
        log.trace("Object: {}", "Finalizado insercion en expediene de la factura en [" + ((stop - start) / 1000) + "] s.");
        return total;
    }

    private static final void insertaDocumento(Connection conn, String tituloAplicacion, int idGabinete, int idCarpeta, String nombreDocumento, String ext, String nombreUsuario, String archivo, CuentaBancaria cuentaBancaria) throws Exception {
        OutputStream fos = null;
        InputStream in = null;
        try {
            Volumen vol = VolumenManager.getVolumen(conn);
            Documento d = DocumentoManager.getDocumento(conn, tituloAplicacion, idGabinete, idCarpeta, nombreDocumento);
            if (d == null) {
                d = new Documento();
                d.setTituloAplicacion(tituloAplicacion);
                d.setIdGabinete(idGabinete);
                d.setIdCarpetaPadre(idCarpeta);
                if ("imx".equals(ext))
                    d.setNombreTipoDocto("IMAX_FILE");
                else
                    d.setNombreTipoDocto("EXTERNO");
                d.setNombreDocumento(nombreDocumento);
                d.setNombreUsuario(nombreUsuario);
                d.setExtension(ext);
                DocumentoManager.insertDocumento(conn, d);
                DocumentoManager.insertPaginaDocumento(conn, vol, d, "A", 0);
            }
            String filename = PaginaManager.getFilenamePath(conn, d.getTituloAplicacion(), d.getIdGabinete(), d.getIdCarpetaPadre(), d.getIdDocumento());
            if ((d.getExtension() == null) || ("".equals(d.getExtension())))
                d.setExtension(ext);
            fos = new FileOutputStream(filename);
            in = new FileInputStream(new File(archivo));
            int fileLength = 0, length = 0;
            byte[] buffer = new byte[4 * 1024];
            while ((in != null) && ((length = in.read(buffer)) != -1)) {
                fos.write(buffer, 0, length);
                fileLength += length;
            }
            fos.flush();
            String lowerFilename = nombreDocumento.toLowerCase() + ".xml";
            String[] filenames = d.getFilesNames();
            for (int i = 0; i < filenames.length; i++) {
                if (lowerFilename.equals(filenames[i].toLowerCase())) {
                    Pagina p = d.getPaginaDocumento(i);
                    p.setTamanoBytes(fileLength);
                    PaginaManager.updatePagina(conn, p);
                    break;
                }
            }
            insertCuenta(conn, cuentaBancaria, nombreUsuario);
        } finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (Exception e) {
                    log.warn("Problemas intentando cerrar el flujo de salida: " + e, e);
                }
            if (in != null)
                try {
                    in.close();
                } catch (Exception e) {
                    log.warn("Problemas intentando cerrar el flujo de salida: " + e, e);
                }
            fos = null;
            in = null;
        }
    }

    public static void insertCuenta(Connection conn, CuentaBancaria cuentaBancaria, String usuario) throws SQLException {
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("INSERT INTO dbo.tBeneficiarioCuentasBancariasTmp " + "(dRFC,cBanco,cPlaza,dCuentaBancaria,dDigitoVerificador,cStatusCuenta,dBanco,dSucursal,cUsuarioModifico,fCuentaModifico,nBCBEnviadoSICOP,cFolio)" + "VALUES(?,?,?,?,?,?,?,?,?,getdate(),?,?)");
            pstmnt.setString(1, cuentaBancaria.getRFC());
            pstmnt.setString(2, cuentaBancaria.getIdBanco());
            pstmnt.setString(3, cuentaBancaria.getPlaza());
            pstmnt.setString(4, cuentaBancaria.getCuentaBancaria());
            pstmnt.setString(5, cuentaBancaria.getDigVerificador());
            pstmnt.setInt(6, cuentaBancaria.getStatusCuenta());
            pstmnt.setString(7, cuentaBancaria.getBanco());
            pstmnt.setString(8, cuentaBancaria.getSucursal());
            pstmnt.setString(9, usuario);
            pstmnt.setInt(10, cuentaBancaria.getStatusSICOP());
            pstmnt.setString(11, cuentaBancaria.getFolio());
            pstmnt.executeUpdate();
        } catch (Exception e) {
            log.warn("Problemas intentando agregar en Base de Datos: " + e, e);
            throw e;
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
    }

    public static List<Map<String, String>> getCuentasTemporales(Connection conn, String folio) throws SQLException {
        List<Map<String, String>> cuentas = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT * FROM vcuentas_bancarias_temp WHERE cfolio = ?");
            ps.setString(1, folio);
            rs = ps.executeQuery();
            Map<String, String> obj;
            do {
                obj = RSToTable.rsToMap(rs);
                if (obj != null)
                    cuentas.add(obj);
            } while (obj != null);
            return cuentas;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public static List<CuentaBancaria> selectActiveByRFC(Connection conn, String rfc) throws SQLException {
        String query = "SELECT * FROM tBeneficiarioCuentasBancarias WITH(NOLOCK) " + " WHERE dRFC = ? AND cStatusCuenta = 1 AND nBCBEnviadoSICOP = 1";
        List<CuentaBancaria> cuentas = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, rfc);
            log.trace("Object: {}", "Retrieving active bank accounts for RFC [" + rfc + "]");
            rs = ps.executeQuery();
            while (rs.next()) {
                CuentaBancaria cuenta = new CuentaBancaria(rs.getString("subCuentaBancaria"), rs.getString("dRFC"));
                cuenta.setStatusCuenta(rs.getInt("cStatusCuenta"));
                cuenta.setBanco(rs.getString("dBanco"));
                cuenta.setSucursal(rs.getString("dSucursal"));
                cuenta.setStatusSICOP(rs.getInt("nBCBEnviadoSICOP"));
                cuentas.add(cuenta);
            }
        } catch (SQLException e) {
            log.error("Error retrieving active bank accounts for RFC [" + rfc + "]: " + e, e);
            throw e;
        }
        return cuentas;
    }

    public static List<Map<String, String>> getCuentasBeneficiario(Connection conn, String rfc) throws SQLException {
        List<Map<String, String>> cuentas = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append("SELECT cbanco               AS cBanco,  ");
        query.append("        dbanco               AS cNameBanco,  ");
        query.append("        cplaza               AS cPlaza,  ");
        query.append("        ddigitoverificador   AS dDigitoVerificador,  ");
        query.append("        dsucursal            AS dSucursal,  ");
        query.append("        dcuentabancaria      AS dCuentaBancaria,  ");
        query.append("        cbanco + cplaza + dcuentabancaria  ");
        query.append("        + ddigitoverificador AS cClabeInterbancaria,  ");
        query.append("        nbcbenviadosicop     AS nBCBEnviadoSICOP,  ");
        query.append("        0                    AS nEstatusCta,  ");
        query.append("        cusuariomodifico     AS cUsuarioModifico,  ");
        query.append("        ''                   AS cMotivoEliminaCta,  ");
        query.append("        dRFC                 AS rfc,  ");
        query.append("        ''               AS folio  ");
        query.append(" FROM  tbeneficiariocuentasbancarias WITH(nolock)  ");
        query.append(" WHERE REPLACE( drfc, '-', '' ) = REPLACE( ?, '-', '' )  ");
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, rfc);
            rs = ps.executeQuery();
            Map<String, String> obj;
            do {
                obj = RSToTable.rsToMap(rs);
                if (obj != null)
                    cuentas.add(obj);
            } while (obj != null);
            return cuentas;
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
