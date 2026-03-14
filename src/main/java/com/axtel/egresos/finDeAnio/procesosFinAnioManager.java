package com.axtel.egresos.finDeAnio;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

public class procesosFinAnioManager {

    public static SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");

    public static String fechaEfectiva = fecha.format(new Date(System.currentTimeMillis()));

    private static final Logger log = LoggerFactory.getLogger(procesosFinAnioManager.class);

    public procesosFinAnioManager() {
        // TODO Auto-generated constructor stub
    }

    //Datos de layout para ben_
    public static String BuscaPagos(Connection conn, String listaEvento, String listaFolios, String listaPagos, Map<String, String> plantillas) throws Exception {
        String fileName = "";
        PreparedStatement pstmntPagos = null;
        PreparedStatement pstmntPagosDet = null;
        //Encabezado
        ResultSet rs = null;
        //Detalle
        ResultSet rs2 = null;
        try {
            //Query para genera el encabezado del layout de la solicitud de pago
            StringBuilder querySelectPagos = genQueryPagos(listaFolios, listaPagos);
            pstmntPagos = conn.prepareStatement(querySelectPagos.toString());
            log.trace("Object: {}", "Ejecutando \n[" + querySelectPagos + "]");
            rs = pstmntPagos.executeQuery();
            //Query para generar el detalle del layouts de solicitudes de pago
            StringBuilder queryPagosDet = generaQueryPagosDet(listaFolios, listaPagos);
            pstmntPagosDet = conn.prepareStatement(queryPagosDet.toString());
            log.trace("Object: {}", "Ejecutando \n[" + queryPagosDet + "]");
            rs2 = pstmntPagosDet.executeQuery();
            fileName = generaLayOut(rs, rs2, plantillas.get("LAYOUTFINANIO"));
            return fileName;
        } finally {
            CloseObject.closeObject(pstmntPagos);
            CloseObject.closeObject(pstmntPagosDet);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rs2);
        }
    }

    private static final StringBuilder genQueryPagos(String listaIds, String listaPagos) {
        StringBuilder querySelectPagos = new StringBuilder();
        querySelectPagos.append("SELECT 'H' AS Header ");
        querySelectPagos.append(" , cTipoPoliza ");
        querySelectPagos.append(" , CASE WHEN cTipoPago IN('REINTEGRO') THEN 'En atención al reintegro No. ' + CONVERT(VARCHAR(15), nFolioPagado) ");
        querySelectPagos.append(" 		  WHEN cTipoPago IN('REINTEGROMIL','REINTEGROAUTMIL') THEN 'En atención al reintegro CAP Mil No. ' + CONVERT(VARCHAR(15), nFolioPagado) ");
        querySelectPagos.append(" 		  WHEN cTipoPago IN('RELACIONGASTOS') AND cEvento NOT IN ('27_13_3') THEN 'Pago ' + (SELECT aEjercicioFiscal FROM tEjercicioFiscal WHERE cActivo = 1) + ' RG No. ' + caNoContrarrecibo ");
        querySelectPagos.append(" 		  WHEN cTipoPago IN('RELACIONGASTOS') and cEvento IN ('27_13_3') THEN 'Pago ISN del mes de Diciembre ' + (SELECT aEjercicioFiscal FROM tEjercicioFiscal WHERE cActivo = 1) + ' RG No. ' + caNoContrarrecibo ");
        querySelectPagos.append(" 		  WHEN cTipoPago IN('PAGODIVERSO') AND cEvento NOT IN ('27_13_3') THEN 'Pago ' + (SELECT aEjercicioFiscal FROM tEjercicioFiscal WHERE cActivo = 1) + ' RG con OC No. ' + caNoContrarrecibo ");
        querySelectPagos.append(" 		  WHEN cTipoPago IN('AJENAS') THEN 'Pago ' + (SELECT aEjercicioFiscal FROM tEjercicioFiscal WHERE cActivo = 1) + ' ' + cDescripcionPoliza + ' OA No. ' + caNoContrarrecibo ");
        querySelectPagos.append(" 		  WHEN cTipoPago IN('INTEGRACION') THEN 'Registro CLC SIAFF ' + CONVERT(VARCHAR(15), FOLIO_CLC) + ' pagada por TESOFE INT No. ' + caNoContrarrecibo ");
        querySelectPagos.append(" 		  WHEN cTipoPago IN('PAGOPENASCONV') THEN cDescripcionPoliza");
        querySelectPagos.append(" 		ELSE cDescripcionPoliza");
        querySelectPagos.append(" END AS cDescripcion ");
        querySelectPagos.append(" , CASE WHEN cTipoPago IN ('REINTEGROMIL') OR cEvento = '27_13_3' THEN 'ERICK LUNA BENITEZ' ");
        querySelectPagos.append(" 		ELSE 'MAYRA LETICIA VAZQUEZ BARRAGAN' END AS nombreVoBo");
        querySelectPagos.append(" , CASE WHEN cTipoPago IN ('REINTEGROMIL') OR cEvento = '27_13_3' THEN 'SUBGERENCIA DE REMUNERACIONES' ");
        querySelectPagos.append(" 		ELSE 'SUBGERENCIA DE CONTROL FINANCIERO' END AS puestoVoBo ");
        querySelectPagos.append(" , CASE WHEN cTipoPago IN ('REINTEGROMIL') OR cEvento = '27_13_3' THEN 'ALMA ROSA MORENO CAMACHO' ");
        querySelectPagos.append(" 		ELSE 'TANIA ANANI LIMON MAGAÑA' END AS nombreAut ");
        querySelectPagos.append(" , CASE WHEN cTipoPago IN ('REINTEGROMIL') OR cEvento = '27_13_3' THEN 'GERENCIA DE RECURSOS HUMANOS' ");
        querySelectPagos.append(" 		ELSE 'GERENCIA DE PROGRAMACION Y PRESUPUESTO' END AS puestoAut ");
        querySelectPagos.append(" , importe ");
        querySelectPagos.append(" FROM vListaFinDeAnio ");
        querySelectPagos.append(" LEFT JOIN (SELECT DISTINCT NCTR_47, FOLIO_SIAFF_112 FROM CLC_SICOP ) CLC ON caNoContrarrecibo = NCTR_47 ");
        querySelectPagos.append(" LEFT JOIN CLC_SIAFF_ENC ON FOLIO_SIAFF_112 = FOLIO_CLC ");
        querySelectPagos.append(" WHERE nFolioPagado IN (");
        querySelectPagos.append(listaIds);
        querySelectPagos.append(") ");
        querySelectPagos.append(" AND cTipoPago IN (");
        querySelectPagos.append(listaPagos);
        querySelectPagos.append(") ");
        querySelectPagos.append(" ORDER BY nFolioPagado, cTipoPago ");
        return querySelectPagos;
    }

    private static StringBuilder generaQueryPagosDet(String listaIds, String listaPagos) {
        StringBuilder queryDC = new StringBuilder();
        queryDC.append("SELECT 'D' AS Header ");
        queryDC.append("	, 1 AS nDocRenglon ");
        queryDC.append("	, cEvento ");
        queryDC.append("	, importe ");
        queryDC.append("	, ctab ");
        queryDC.append("	, RFC ");
        queryDC.append("	, CASE WHEN ctaBenef = 'NA' THEN ctaBenef ELSE SUBSTRING(ctaBenef,7,11) END AS ctaBenef ");
        queryDC.append("FROM vListaFinDeAnio ");
        queryDC.append(" WHERE nFolioPagado IN (");
        queryDC.append(listaIds);
        queryDC.append(") ");
        queryDC.append(" AND cTipoPago IN (");
        queryDC.append(listaPagos);
        queryDC.append(") ");
        queryDC.append("ORDER BY nFolioPagado, cTipoPago");
        return queryDC;
    }

    @SuppressWarnings("resource")
    private static String generaLayOut(ResultSet rs, ResultSet rs2, String plantillaPath) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "LayOutSNPFinAnio" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        ResultSetMetaData rsMetadata = rs.getMetaData();
        ResultSetMetaData rs2Metadata = rs2.getMetaData();
        int renglonInicio = 0;
        while (rs.next()) {
            int rows = renglonInicio;
            sheet0.shiftRows(rows, renglonInicio, 1);
            Row rw = (sheet0.getRow(renglonInicio) == null ? sheet0.createRow(renglonInicio) : sheet0.getRow(renglonInicio));
            for (int i = 0; i < 8; i++) {
                Util.createExcelCell(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
            }
            renglonInicio++;
            if (rs2.next()) {
                int rows2 = renglonInicio;
                sheet0.shiftRows(rows2, renglonInicio, 1);
                Row rw2 = (sheet0.getRow(renglonInicio) == null ? sheet0.createRow(renglonInicio) : sheet0.getRow(renglonInicio));
                for (int i = 0; i < 7; i++) {
                    Util.createExcelCell(i, rw2, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1));
                }
            }
            renglonInicio++;
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static void bitacora(Connection conn, String listaEvento, String listaFolios, String listaPagos, String sUsuario) throws SQLException {
        PreparedStatement ps = null;
        try {
            StringBuilder insertaBitacora = new StringBuilder();
            insertaBitacora.append("INSERT INTO tBitacoraFinAnio ");
            insertaBitacora.append("SELECT ? ");
            insertaBitacora.append("	, GETDATE() ");
            insertaBitacora.append("	, cTipoPago ");
            insertaBitacora.append("	, nFolioPagado ");
            insertaBitacora.append("	, cEvento ");
            insertaBitacora.append("FROM vListaFinDeAnio ");
            insertaBitacora.append("WHERE nFolioPagado IN (" + listaFolios + ") ");
            insertaBitacora.append("AND cTipoPago IN (" + listaPagos + ")");
            insertaBitacora.append("ORDER BY nFolioPagado, cTipoPago");
            log.debug("Object: {}", "Query para insertar Bitacora: " + insertaBitacora);
            ps = conn.prepareStatement(insertaBitacora.toString());
            ps.setString(1, sUsuario);
            ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }
}
