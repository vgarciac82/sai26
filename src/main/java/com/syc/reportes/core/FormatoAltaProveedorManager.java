package com.syc.reportes.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.altaproveedor.AltaProveedorBusinessLogic;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

public class FormatoAltaProveedorManager {

    private static final Logger log = LoggerFactory.getLogger(FormatoAltaProveedorManager.class);

    public static String generaReporteFormatoAltaProveedor(Connection conn, Map<String, String> plantillas, String folio, boolean altaRapida) throws Exception {
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSet rst2 = null;
        ResultSet rst3 = null;
        ResultSet rst4 = null;
        try {
            String query = "" + "SELECT CASE " + "         WHEN alt.cactualizacion = 1 THEN 'MODIFICACION' " + "         ELSE 'ALTA' " + "       END                        AS TipoMovimiento, " + "       tipoRFC.ctipopersonarfc    AS TipoBeneficiario, " + "       alt.cidrfc                 AS RFC, " + "       alt.ccurp                  AS CURP, " + "       capellidopaterno           AS ApellidoPaterno, " + "       capellidomaterno           AS ApellidoMaterno, " + "       cnombre                    AS Nombre, " + "       CASE " + "         WHEN alt.crazonsocial = '' THEN " + "         capellidopaterno + ' ' + capellidomaterno + " + "         ' ' " + "         + cnombre " + "         ELSE alt.crazonsocial " + "       END                        AS Beneficiario, " + "       ccalle                     AS Calle, " + "       cnumeroexterno             AS NumeroExterno, " + "       cnumerointerno             AS NumeroInterno, " + "       ccolonia                   AS Colonia, " + "       mun.mpo_nombre             AS Localidad, " + "       estados.dEntidadFederativa AS Estado, " + "       ccodigopostal              AS CP, " + "       ctelefono                  AS Telefono, " + "       cemail                     AS Email " + "FROM   taltaproveedor alt WITH(nolock) " + "       INNER JOIN pcatalogotipopersonarfc tipoRFC WITH (nolock) " + "               ON tipoRFC.cidtipopersonarfc = alt.cidtipopersona " + "       INNER JOIN tCatalogoEntidadFederativa estados WITH (nolock) " + "               ON CAST (estados.cEntidadFederativa AS INT) = alt.cidentidadfederativa " + "       INNER JOIN cat_municipio mun WITH (nolock) " + "               ON mun.id_municipio = alt.cidmunicipio " + "WHERE  alt.cfolio = ?";
            ps = conn.prepareStatement(query);
            ps.setString(1, folio);
            rst = ps.executeQuery();
            log.debug("Object: " + String.valueOf(ps.toString()));
            String query2 = "" + "SELECT dcuentabancaria as cuenta, " + "       subcuentabancaria as clabe, " + "       dbanco as banco, " + "       CASE " + "         WHEN alt.crazonsocial = '' THEN " + "         alt.capellidopaterno + ' ' " + "         + alt.capellidomaterno + ' ' + alt.cnombre " + "         ELSE alt.crazonsocial " + "       END AS nombre " + "FROM   tbeneficiariocuentasbancarias banc WITH (nolock) " + "       INNER JOIN taltaproveedor alt  WITH (nolock) " + "               ON Replace (alt.cidrfc, '-', '') = banc.drfc " + "WHERE  alt.cfolio = ?";
            ps = conn.prepareStatement(query2);
            ps.setString(1, folio);
            rst2 = ps.executeQuery();
            log.debug("Object: " + String.valueOf(ps.toString()));
            String query3 = "" + "SELECT dcuentabancaria as cuenta, " + "       cBanco+cPlaza+dCuentaBancaria+dDigitoVerificador as clabe, " + "       dbanco as banco, " + "       CASE " + "         WHEN alt.crazonsocial = '' THEN " + "         alt.capellidopaterno + ' ' " + "         + alt.capellidomaterno + ' ' + alt.cnombre " + "         ELSE alt.crazonsocial " + "       END AS nombre " + "FROM   tBeneficiarioCuentasBancariasTmp banc WITH (nolock) " + "       INNER JOIN taltaproveedor alt  WITH (nolock) " + "               ON Replace (alt.cidrfc, '-', '') = banc.drfc " + "WHERE  alt.cfolio = ?";
            ps = conn.prepareStatement(query3);
            ps.setString(1, folio);
            rst3 = ps.executeQuery();
            log.debug("Object: " + String.valueOf(ps.toString()));
            String query4 = "";
            if (altaRapida)
                query4 = "SELECT ccben AS CBEN " + "FROM   tcbenaltaempleado cben WITH(NOLOCK) " + "       INNER JOIN taltaproveedor prov WITH(NOLOCK) " + "               ON Replace(cben.crfc, '-', '') = Replace(prov.cidrfc, '-', '') " + "WHERE  prov.cfolio = ? " + "       AND cben.ntipocben = " + AltaProveedorBusinessLogic.CBEN_PROVEEDOR;
            else
                query4 = "SELECT CBEN FROM tBeneficiario INNER JOIN tAltaProveedor ON dRFC =  REPLACE(cIdRFC,'-','') WHERE cFolio=?";
            ps = conn.prepareStatement(query4);
            ps.setString(1, folio);
            rst4 = ps.executeQuery();
            log.debug("Object: " + String.valueOf(ps.toString()));
            String fileName = generaReporteFormatoAltaProveedor(rst, rst2, rst3, rst4, plantillas.get("FmtoAltaProveedor"));
            return fileName;
        } finally {
            CloseObject.closeObject(rst, false);
            CloseObject.closeObject(rst2, false);
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rst3, false);
            CloseObject.closeObject(rst4, false);
        }
    }

    public static String generaReporteFormatoAltaProveedor(Connection conn, Map<String, String> plantillas, String folio) throws Exception {
        return generaReporteFormatoAltaProveedor(conn, plantillas, folio, false);
    }

    public static String generaReporteFormatoAltaProveedor(ResultSet rs, ResultSet rst2, ResultSet rst3, ResultSet rst4, String plantillaPath) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "FormatoAltaProveedor" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.MEDIUM);
        estiloTabla.setBorderLeft(BorderStyle.MEDIUM);
        estiloTabla.setBorderTop(BorderStyle.MEDIUM);
        estiloTabla.setBorderBottom(BorderStyle.MEDIUM);
        while (rs.next()) {
            Row rowtipomovimiento = (sheet0.getRow(1) == null ? sheet0.createRow(1) : sheet0.getRow(1));
            Cell celdatipomovimiento = (rowtipomovimiento.getCell(1) == null ? rowtipomovimiento.createCell(1) : rowtipomovimiento.getCell(1));
            celdatipomovimiento.setCellValue(rs.getString("TipoMovimiento"));
            Row rowtipobeneficiario = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
            Cell celdatipobeneficiario = (rowtipobeneficiario.getCell(1) == null ? rowtipobeneficiario.createCell(1) : rowtipobeneficiario.getCell(1));
            celdatipobeneficiario.setCellValue(rs.getString("TipoBeneficiario"));
            Row rowrfc = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
            Cell celdarfc = (rowrfc.getCell(1) == null ? rowrfc.createCell(1) : rowrfc.getCell(1));
            celdarfc.setCellValue(rs.getString("RFC"));
            Row rowcurp = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
            Cell celdacurp = (rowcurp.getCell(6) == null ? rowcurp.createCell(6) : rowcurp.getCell(6));
            celdacurp.setCellValue(rs.getString("CURP"));
            Row rowapellidopaterno = (sheet0.getRow(7) == null ? sheet0.createRow(7) : sheet0.getRow(7));
            Cell celdaapellidopaterno = (rowapellidopaterno.getCell(1) == null ? rowapellidopaterno.createCell(1) : rowapellidopaterno.getCell(1));
            celdaapellidopaterno.setCellValue(rs.getString("ApellidoPaterno"));
            Row rowapellidomaterno = (sheet0.getRow(7) == null ? sheet0.createRow(7) : sheet0.getRow(7));
            Cell celdaapellidomaterno = (rowapellidomaterno.getCell(6) == null ? rowapellidomaterno.createCell(6) : rowapellidomaterno.getCell(6));
            celdaapellidomaterno.setCellValue(rs.getString("ApellidoMaterno"));
            Row rownombre = (sheet0.getRow(9) == null ? sheet0.createRow(9) : sheet0.getRow(9));
            Cell celdanombre = (rownombre.getCell(1) == null ? rownombre.createCell(1) : rownombre.getCell(1));
            celdanombre.setCellValue(rs.getString("Nombre"));
            Row rowbeneficiario = (sheet0.getRow(11) == null ? sheet0.createRow(11) : sheet0.getRow(11));
            Cell celdabeneficiario = (rowbeneficiario.getCell(1) == null ? rowbeneficiario.createCell(1) : rowbeneficiario.getCell(1));
            celdabeneficiario.setCellValue(rs.getString("Beneficiario"));
            Row rowcalle = (sheet0.getRow(15) == null ? sheet0.createRow(15) : sheet0.getRow(15));
            Cell celdacalle = (rowcalle.getCell(1) == null ? rowcalle.createCell(1) : rowcalle.getCell(1));
            celdacalle.setCellValue(rs.getString("Calle"));
            Row rownumeroexterno = (sheet0.getRow(15) == null ? sheet0.createRow(15) : sheet0.getRow(15));
            Cell celdanumeroexterno = (rownumeroexterno.getCell(6) == null ? rownumeroexterno.createCell(6) : rownumeroexterno.getCell(6));
            celdanumeroexterno.setCellValue(rs.getString("NumeroExterno"));
            Row rownumerointerno = (sheet0.getRow(17) == null ? sheet0.createRow(17) : sheet0.getRow(17));
            Cell celdanumerointerno = (rownumerointerno.getCell(1) == null ? rownumerointerno.createCell(1) : rownumerointerno.getCell(1));
            celdanumerointerno.setCellValue(rs.getString("NumeroInterno"));
            Row rowcolonia = (sheet0.getRow(17) == null ? sheet0.createRow(17) : sheet0.getRow(17));
            Cell celdacolonia = (rowcolonia.getCell(6) == null ? rowcolonia.createCell(6) : rowcolonia.getCell(6));
            celdacolonia.setCellValue(rs.getString("Colonia"));
            Row rowlocalidad = (sheet0.getRow(19) == null ? sheet0.createRow(19) : sheet0.getRow(19));
            Cell celdalocalidad = (rowlocalidad.getCell(1) == null ? rowlocalidad.createCell(1) : rowlocalidad.getCell(1));
            celdalocalidad.setCellValue(rs.getString("Localidad"));
            Row rowestado = (sheet0.getRow(19) == null ? sheet0.createRow(19) : sheet0.getRow(19));
            Cell celdaestado = (rowestado.getCell(6) == null ? rowestado.createCell(6) : rowestado.getCell(6));
            celdaestado.setCellValue(rs.getString("Estado"));
            Row rowcp = (sheet0.getRow(21) == null ? sheet0.createRow(21) : sheet0.getRow(21));
            Cell celdacp = (rowcp.getCell(1) == null ? rowcp.createCell(1) : rowcp.getCell(1));
            celdacp.setCellValue(rs.getString("CP"));
            Row rowtelefono = (sheet0.getRow(21) == null ? sheet0.createRow(21) : sheet0.getRow(21));
            Cell celdatelefono = (rowtelefono.getCell(6) == null ? rowtelefono.createCell(6) : rowtelefono.getCell(6));
            celdatelefono.setCellValue(rs.getString("Telefono"));
            Row rowemail = (sheet0.getRow(23) == null ? sheet0.createRow(23) : sheet0.getRow(23));
            Cell celdaemail = (rowemail.getCell(1) == null ? rowemail.createCell(1) : rowemail.getCell(1));
            celdaemail.setCellValue(rs.getString("Email"));
        }
        while (rst4.next()) {
            Row rowcben = (sheet0.getRow(1) == null ? sheet0.createRow(1) : sheet0.getRow(1));
            Cell celdacben = (rowcben.getCell(6) == null ? rowcben.createCell(6) : rowcben.getCell(6));
            celdacben.setCellValue(rst4.getString("CBEN"));
        }
        int rowinc = 27;
        while (rst2.next()) {
            Row rowcuenta = (sheet0.getRow(rowinc) == null ? sheet0.createRow(rowinc) : sheet0.getRow(rowinc));
            Cell celdacuenta = (rowcuenta.getCell(0) == null ? rowcuenta.createCell(0) : rowcuenta.getCell(0));
            celdacuenta.setCellStyle(estiloTabla);
            celdacuenta.setCellValue(rst2.getString("cuenta"));
            Row rowclabe = (sheet0.getRow(rowinc) == null ? sheet0.createRow(rowinc) : sheet0.getRow(rowinc));
            Cell celdaclabe = (rowclabe.getCell(1) == null ? rowclabe.createCell(1) : rowclabe.getCell(1));
            Cell celdaclabe2 = (rowclabe.getCell(2) == null ? rowclabe.createCell(2) : rowclabe.getCell(2));
            celdaclabe.setCellStyle(estiloTabla);
            celdaclabe2.setCellStyle(estiloTabla);
            celdaclabe.setCellValue(rst2.getString("clabe"));
            Row rownombreben = (sheet0.getRow(rowinc) == null ? sheet0.createRow(rowinc) : sheet0.getRow(rowinc));
            Cell celdanombreben = (rownombreben.getCell(3) == null ? rownombreben.createCell(3) : rownombreben.getCell(3));
            Cell celdanombreben2 = (rownombreben.getCell(4) == null ? rownombreben.createCell(4) : rownombreben.getCell(4));
            Cell celdanombreben3 = (rownombreben.getCell(5) == null ? rownombreben.createCell(5) : rownombreben.getCell(5));
            celdanombreben.setCellStyle(estiloTabla);
            celdanombreben2.setCellStyle(estiloTabla);
            celdanombreben3.setCellStyle(estiloTabla);
            celdanombreben.setCellValue(rst2.getString("nombre"));
            Row rowbanco = (sheet0.getRow(rowinc) == null ? sheet0.createRow(rowinc) : sheet0.getRow(rowinc));
            Cell celdabanco = (rowbanco.getCell(6) == null ? rowbanco.createCell(6) : rowbanco.getCell(6));
            Cell celdabanco2 = (rowbanco.getCell(7) == null ? rowbanco.createCell(7) : rowbanco.getCell(7));
            Cell celdabanco3 = (rowbanco.getCell(8) == null ? rowbanco.createCell(8) : rowbanco.getCell(8));
            celdabanco.setCellStyle(estiloTabla);
            celdabanco2.setCellStyle(estiloTabla);
            celdabanco3.setCellStyle(estiloTabla);
            celdabanco.setCellValue(rst2.getString("banco"));
            rowinc++;
        }
        while (rst3.next()) {
            Row rowcuenta = (sheet0.getRow(rowinc) == null ? sheet0.createRow(rowinc) : sheet0.getRow(rowinc));
            Cell celdacuenta = (rowcuenta.getCell(0) == null ? rowcuenta.createCell(0) : rowcuenta.getCell(0));
            celdacuenta.setCellStyle(estiloTabla);
            celdacuenta.setCellValue(rst3.getString("cuenta"));
            Row rowclabe = (sheet0.getRow(rowinc) == null ? sheet0.createRow(rowinc) : sheet0.getRow(rowinc));
            Cell celdaclabe = (rowclabe.getCell(1) == null ? rowclabe.createCell(1) : rowclabe.getCell(1));
            Cell celdaclabe2 = (rowclabe.getCell(2) == null ? rowclabe.createCell(2) : rowclabe.getCell(2));
            celdaclabe.setCellStyle(estiloTabla);
            celdaclabe2.setCellStyle(estiloTabla);
            celdaclabe.setCellValue(rst3.getString("clabe"));
            Row rownombreben = (sheet0.getRow(rowinc) == null ? sheet0.createRow(rowinc) : sheet0.getRow(rowinc));
            Cell celdanombreben = (rownombreben.getCell(3) == null ? rownombreben.createCell(3) : rownombreben.getCell(3));
            Cell celdanombreben2 = (rownombreben.getCell(4) == null ? rownombreben.createCell(4) : rownombreben.getCell(4));
            Cell celdanombreben3 = (rownombreben.getCell(5) == null ? rownombreben.createCell(5) : rownombreben.getCell(5));
            celdanombreben.setCellStyle(estiloTabla);
            celdanombreben2.setCellStyle(estiloTabla);
            celdanombreben3.setCellStyle(estiloTabla);
            celdanombreben.setCellValue(rst3.getString("nombre"));
            Row rowbanco = (sheet0.getRow(rowinc) == null ? sheet0.createRow(rowinc) : sheet0.getRow(rowinc));
            Cell celdabanco = (rowbanco.getCell(6) == null ? rowbanco.createCell(6) : rowbanco.getCell(6));
            Cell celdabanco2 = (rowbanco.getCell(7) == null ? rowbanco.createCell(7) : rowbanco.getCell(7));
            Cell celdabanco3 = (rowbanco.getCell(8) == null ? rowbanco.createCell(8) : rowbanco.getCell(8));
            celdabanco.setCellStyle(estiloTabla);
            celdabanco2.setCellStyle(estiloTabla);
            celdabanco3.setCellStyle(estiloTabla);
            celdabanco.setCellValue(rst3.getString("banco"));
            Row rownueva = (sheet0.getRow(rowinc) == null ? sheet0.createRow(rowinc) : sheet0.getRow(rowinc));
            Cell celdaNueva = (rownueva.getCell(9) == null ? rownueva.createCell(9) : rownueva.getCell(9));
            celdaNueva.setCellStyle(estiloTabla);
            celdaNueva.setCellValue("NUEVA");
            rowinc++;
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos);
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }
}
