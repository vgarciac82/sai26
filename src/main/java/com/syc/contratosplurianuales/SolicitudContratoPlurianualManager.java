package com.syc.contratosplurianuales;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.syc.gestion.core.Caso;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import net.sf.jasperreports.engine.JasperRunManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

public class SolicitudContratoPlurianualManager {

    private static final Logger log = LoggerFactory.getLogger(SolicitudContratoPlurianualManager.class);

    public static String generaExcelSolicitud(Connection conn, HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        PreparedStatement psEnc = null;
        ResultSet rsEnc = null;
        PreparedStatement psDet = null;
        ResultSet rsDet = null;
        PreparedStatement psDetM = null;
        ResultSet rsDetM = null;
        String sFolio = req.getParameter("FOLIO");
        String sSqlEnc = " SELECT EncR1, EncR2, EncR3, cDescripcionProyecto,fInicio, fFin, TotalContrato , JustificacionBeneficios, sPuestoSol,sFirmanteSol " + " FROM vw_ContratosPlurianualEncabezado WITH (NOLOCK) WHERE Folio = " + sFolio;
        StringBuilder sqlDet = new StringBuilder();
        sqlDet.append(" SELECT Folio, aEjercicioFiscal, ROW_NUMBER ( )  over (order by aEjercicioFiscal) nDocrenglon, cast(iejercicioClave as varchar(4)) + substring(EPCorta,5,19)+  '00'  + SUBSTRING(epcorta,22,50) EPCorta, iEjercicioClave, RA, UR, FI, FN, SF,'00' RG, AI, PP, OG, TG,FF, EF, PPI ");
        sqlDet.append(" , sum(mMes1) mMes1, sum(mMes2) mMes2, sum(mMes3) mMes3, sum(mMes4) mMes4, sum(mmes5) mmes5 , sum(mMes6) mMes6, sum(mMes7) mMes7, sum(mMes8) mMes8, sum(mMes9) mMes9, sum(mmes10) mmes10, sum(mMes11) mMes11, sum(mMes12) mMes12, SUM(mimporteTotal) mimporteTotal ");
        sqlDet.append(" FROM vw_ContratosPlurianualDetalle WITH (NOLOCK) WHERE Folio =  " + sFolio);
        sqlDet.append(" group by Folio, aEjercicioFiscal,  cast(iejercicioClave as varchar(4)) + substring(EPCorta,5,19), SUBSTRING(epcorta,22,50)  , RA, UR, FI, FN, SF,  AI, PP, OG, TG,FF, EF,iEjercicioClave,PPI   ");
        sqlDet.append(" order by cast(iejercicioClave as varchar(4)) + substring(EPCorta,5,19)");
        String sSqlDetMontos = " SELECT aEjercicio, montoEjercicio FROM vw_ContratosPlurianual_MontosEjercicios WITH (NOLOCK) WHERE Folio = " + sFolio;
        String plantillaPath = "", file_name = "";
        int numAnios = consultaAniosPlurianual(conn, sFolio);
        try {
            psEnc = conn.prepareStatement(sSqlEnc);
            rsEnc = psEnc.executeQuery();
            psDet = conn.prepareStatement(sqlDet.toString());
            rsDet = psDet.executeQuery();
            psDetM = conn.prepareStatement(sSqlDetMontos);
            rsDetM = psDetM.executeQuery();
            if (numAnios <= 3) {
                plantillaPath = plantilla.get("FormatoSolContratoPLU");
            } else if (numAnios == 4) {
                plantillaPath = plantilla.get("FormatoSolContratoPLU4");
            } else if (numAnios == 5) {
                plantillaPath = plantilla.get("FormatoSolContratoPLU5");
            } else if (numAnios == 6) {
                plantillaPath = plantilla.get("FormatoSolContratoPLU6");
            } else if (numAnios == 7) {
                plantillaPath = plantilla.get("FormatoSolContratoPLU7");
            }
            file_name = ReporteExcelPlurianuales(rsDet, rsEnc, rsDetM, plantillaPath, numAnios);
            return file_name;
        } finally {
            CloseObject.closeObject(rsEnc);
            CloseObject.closeObject(psEnc);
            CloseObject.closeObject(rsDet);
            CloseObject.closeObject(psDet);
        }
    }

    public static String generaExcelSolicitudModificado(Connection conn, HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        PreparedStatement psEnc = null, psDet = null, psDetM = null, psDetDD = null, psDetE = null, psEncDD = null;
        ResultSet rsEnc = null, rsDet = null, rsDetM = null, rsDetDD = null, rsDetE = null, rsEncDD = null;
        String sSqlEnc = "", sSqlDet = "", sSqlDetMontos = "";
        String sFolio = req.getParameter("FOLIO");
        String esModificado = req.getParameter("cEsModificado");
        String folioOrigen = req.getParameter("nFolioContratoPlurianual");
        String folioUltimo = req.getParameter("folioAnterior");
        int numAnios = 0, nAniosMayorOriginal = 0, numAniosOrigen = 0, numAnioMod = 0;
        String queryEnc = " SELECT EncR1, EncR2, EncR3, cDescripcionProyecto,fInicio, fFin, TotalContrato , JustificacionBeneficios, sPuestoSol,sFirmanteSol, FolioOrigen " + " FROM vw_ContratosPlurianualEncabezadoMod WITH (NOLOCK) WHERE Folio = " + sFolio;
        String queryDet = " SELECT Folio, aEjercicioFiscal, ROW_NUMBER ( )  over (order by iEjercicioClave) nDocrenglon, substring(EPCorta,1,19)+  '00'  + SUBSTRING(epcorta,22,50) EPCorta , iEjercicioClave, RA, UR, FI, FN, SF, '00' RG, AI, PP, OG, TG,FF, EF, PPI,  " + " sum(mMes1) mMes1, sum(mMes2) mMes2, sum(mMes3) mMes3, sum(mMes4) mMes4, sum(mmes5) mmes5, sum(mMes6) mMes6, sum(mMes7) mMes7, sum(mMes8) mMes8, sum(mMes9) mMes9, sum(mmes10) mmes10, sum(mMes11) mMes11, sum(mMes12) mMes12, SUM(mimporteTotal) mimporteTotal " + " FROM vw_ContratosPlurianualDetalleMod WITH (NOLOCK) WHERE Folio = " + sFolio + " group by Folio, aEjercicioFiscal,  substring(EPCorta,1,19), SUBSTRING(epcorta,22,50), RA, UR, FI, FN, SF,  AI, PP, OG, TG,FF, EF,iEjercicioClave,PPI ";
        String queryMontos = " SELECT aEjercicio, montoEjercicio FROM vw_ContratosPlurianual_MontosEjerMod WITH (NOLOCK) WHERE Folio = " + sFolio;
        String plantillaPath = "", file_name = "";
        //Si es Original llamar a las tablas origen
        if ("0".equals(esModificado)) {
            sSqlEnc = " SELECT EncR1, EncR2, EncR3, cDescripcionProyecto,fInicio, fFin, TotalContrato , JustificacionBeneficios, sPuestoSol,sFirmanteSol, Folio  FolioOrigen" + " FROM vw_ContratosPlurianualEncabezado WITH (NOLOCK) WHERE Folio = " + folioOrigen;
            sSqlDet = " SELECT Folio, aEjercicioFiscal, ROW_NUMBER ( )  over (order by iEjercicioClave) nDocrenglon, substring(EPCorta,1,19)+  '00'  + SUBSTRING(epcorta,22,50) EPCorta , iEjercicioClave, RA, UR, FI, FN, SF,'00' RG, AI, PP, OG, TG,FF, EF, PPI,  " + " sum(mMes1) mMes1, sum(mMes2) mMes2, sum(mMes3) mMes3, sum(mMes4) mMes4, sum(mmes5) mmes5, sum(mMes6) mMes6, sum(mMes7) mMes7, sum(mMes8) mMes8, sum(mMes9) mMes9, sum(mmes10) mmes10, sum(mMes11) mMes11, sum(mMes12) mMes12, SUM(mimporteTotal) mimporteTotal " + " FROM vw_ContratosPlurianualDetalle WITH (NOLOCK) WHERE Folio = " + folioOrigen + " group by Folio, aEjercicioFiscal,  substring(EPCorta,1,19), SUBSTRING(epcorta,22,50), RA, UR, FI, FN, SF,  AI, PP, OG, TG,FF, EF,iEjercicioClave,PPI ";
            sSqlDetMontos = " SELECT aEjercicio, montoEjercicio FROM vw_ContratosPlurianual_MontosEjercicios WITH (NOLOCK) WHERE Folio = " + folioOrigen;
            numAniosOrigen = consultaAniosPlurianual(conn, folioOrigen);
            numAnioMod = consultaAniosPlurianualMod(conn, sFolio);
        } else {
            sSqlEnc = " SELECT EncR1, EncR2, EncR3, cDescripcionProyecto,fInicio, fFin, TotalContrato , JustificacionBeneficios, sPuestoSol,sFirmanteSol, FolioOrigen " + " FROM vw_ContratosPlurianualEncabezadoMod WITH (NOLOCK) WHERE Folio = " + folioUltimo;
            sSqlDet = " SELECT Folio, aEjercicioFiscal, ROW_NUMBER ( )  over (order by iEjercicioClave) nDocrenglon, substring(EPCorta,1,19)+  '00'  + SUBSTRING(epcorta,22,50) EPCorta , iEjercicioClave, RA, UR, FI, FN, SF,'00' RG, AI, PP, OG, TG,FF, EF, PPI,   " + " sum(mMes1) mMes1, sum(mMes2) mMes2, sum(mMes3) mMes3, sum(mMes4) mMes4, sum(mmes5) mmes5, sum(mMes6) mMes6, sum(mMes7) mMes7, sum(mMes8) mMes8, sum(mMes9) mMes9, sum(mmes10) mmes10, sum(mMes11) mMes11, sum(mMes12) mMes12, SUM(mimporteTotal) mimporteTotal " + " FROM vw_ContratosPlurianualDetalleMod WITH (NOLOCK) WHERE Folio = " + folioUltimo + " group by Folio, aEjercicioFiscal,  substring(EPCorta,1,19), SUBSTRING(epcorta,22,50), RA, UR, FI, FN, SF,  AI, PP, OG, TG,FF, EF,iEjercicioClave,PPI ";
            sSqlDetMontos = " SELECT aEjercicio, montoEjercicio FROM vw_ContratosPlurianual_MontosEjerMod WITH (NOLOCK) WHERE Folio = " + folioUltimo;
            numAniosOrigen = consultaAniosPlurianualMod(conn, sFolio);
            numAnioMod = consultaAniosPlurianualMod(conn, folioUltimo);
        }
        if (numAniosOrigen > numAnioMod) {
            numAnios = numAniosOrigen;
            nAniosMayorOriginal = 1;
        } else {
            numAnios = numAnioMod;
        }
        try {
            psEnc = conn.prepareStatement(sSqlEnc);
            rsEnc = psEnc.executeQuery();
            psDet = conn.prepareStatement(sSqlDet);
            rsDet = psDet.executeQuery();
            psDetM = conn.prepareStatement(sSqlDetMontos);
            rsDetM = psDetM.executeQuery();
            psDetDD = conn.prepareStatement(queryDet);
            rsDetDD = psDetDD.executeQuery();
            psDetE = conn.prepareStatement(queryMontos);
            rsDetE = psDetE.executeQuery();
            psEncDD = conn.prepareStatement(queryEnc);
            rsEncDD = psEncDD.executeQuery();
            if (numAnios <= 3) {
                plantillaPath = plantilla.get("FormatoSolContratoPLU");
            } else if (numAnios == 4) {
                plantillaPath = plantilla.get("FormatoSolContratoPLU4");
            } else if (numAnios == 5) {
                plantillaPath = plantilla.get("FormatoSolContratoPLU5");
            } else if (numAnios == 6) {
                plantillaPath = plantilla.get("FormatoSolContratoPLU6");
            } else if (numAnios == 7) {
                plantillaPath = plantilla.get("FormatoSolContratoPLU7");
            }
            file_name = ReporteExcelPlurianualesMod(rsDet, rsEnc, rsDetM, rsEncDD, rsDetDD, rsDetE, plantillaPath, numAnios, nAniosMayorOriginal);
            return file_name;
        } finally {
            CloseObject.closeObject(rsEnc);
            CloseObject.closeObject(psEnc);
            CloseObject.closeObject(rsDet);
            CloseObject.closeObject(psDet);
            CloseObject.closeObject(psDetDD);
            CloseObject.closeObject(psDetE);
            CloseObject.closeObject(rsDetDD);
            CloseObject.closeObject(rsDetE);
        }
    }

    public static void descartaModificacionContrato(Connection conn, String uLogin, Caso c) throws Exception {
        int nFolioContrato = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf("-") + 1));
        descartaModificacionContrato(conn, uLogin, nFolioContrato);
    }

    public static void descartaModificacionContrato(Connection conn, String uLogin, int nFolioContrato) throws Exception {
        String queryExiste = "SELECT COUNT(*) AS Existe FROM tContratosPlurianualEnc_Mod (NOLOCK) WHERE nFolioContratoPlurianual = ?";
        String queryContEnc = "DELETE dbo.tContratosPlurianualEnc_Mod WHERE nFolioContratoPlurianual = ?";
        String queryContDet = "DELETE dbo.tContratosPlurianualDet_Mod WHERE nFolioContratoPlurianual = ?";
        String queryMontoEjer = "DELETE tContratosPlurianual_EjercicioMod WHERE nFolioContratoPlurianual = ?";
        PreparedStatement psEncabezado = null, psDetalle = null, psMonto = null, psExiste = null;
        ResultSet rsExiste = null;
        int existe = 0;
        try {
            psExiste = conn.prepareStatement(queryExiste);
            psExiste.setInt(1, nFolioContrato);
            rsExiste = psExiste.executeQuery();
            if (rsExiste.next()) {
                existe = rsExiste.getInt("Existe");
            }
            if (existe > 0) {
                psMonto = conn.prepareStatement(queryMontoEjer);
                psMonto.setInt(1, nFolioContrato);
                psMonto.execute();
                psDetalle = conn.prepareStatement(queryContDet);
                psDetalle.setInt(1, nFolioContrato);
                psDetalle.execute();
                psEncabezado = conn.prepareStatement(queryContEnc);
                psEncabezado.setInt(1, nFolioContrato);
                psEncabezado.execute();
            }
        } finally {
            CloseObject.closeObject(rsExiste, false);
            CloseObject.closeObject(psEncabezado, false);
            CloseObject.closeObject(psDetalle, false);
            CloseObject.closeObject(psMonto, false);
            CloseObject.closeObject(psExiste, false);
        }
    }

    public static void descartaContrato(Connection conn, String uLogin, Caso c) throws Exception {
        int nFolioContrato = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf("-") + 1));
        descartaContrato(conn, uLogin, nFolioContrato);
    }

    public static void descartaContrato(Connection conn, String uLogin, int nFolioContrato) throws Exception {
        String queryExiste = "SELECT COUNT(*) AS Existe FROM tContratosPlurianualEncabezado (NOLOCK) WHERE nFolioContratoPlurianual = ?";
        String queryContEnc = "DELETE dbo.tContratosPlurianualEncabezado WHERE nFolioContratoPlurianual = ?";
        String queryContDet = "DELETE dbo.tContratosPlurianualDetalle WHERE nFolioContratoPlurianual = ?";
        String queryMontoEjer = "DELETE tContratosPlurianual_MontosEjercicios WHERE nFolioContratoPlurianual = ?";
        PreparedStatement psEncabezado = null, psDetalle = null, psMonto = null, psExiste = null;
        ResultSet rsExiste = null;
        int existe = 0;
        try {
            psExiste = conn.prepareStatement(queryExiste);
            psExiste.setInt(1, nFolioContrato);
            rsExiste = psExiste.executeQuery();
            if (rsExiste.next()) {
                existe = rsExiste.getInt("Existe");
            }
            if (existe > 0) {
                psMonto = conn.prepareStatement(queryMontoEjer);
                psMonto.setInt(1, nFolioContrato);
                psMonto.execute();
                psDetalle = conn.prepareStatement(queryContDet);
                psDetalle.setInt(1, nFolioContrato);
                psDetalle.execute();
                psEncabezado = conn.prepareStatement(queryContEnc);
                psEncabezado.setInt(1, nFolioContrato);
                psEncabezado.execute();
            }
        } finally {
            CloseObject.closeObject(rsExiste, false);
            CloseObject.closeObject(psEncabezado, false);
            CloseObject.closeObject(psDetalle, false);
            CloseObject.closeObject(psMonto, false);
            CloseObject.closeObject(psExiste, false);
        }
    }

    public static String ReporteExcelPlurianuales(ResultSet rsDet, ResultSet rsEnc, ResultSet rsDetM, String plantillaPath, int numAnios) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "FormatoSOLContratoPLU" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        XSSFWorkbook workbook = new XSSFWorkbook(fsArchivo);
        fsArchivo.close();
        XSSFSheet sheet0 = workbook.getSheetAt(0);
        ResultSetMetaData rsMetadata = rsDet.getMetaData();
        ResultSetMetaData rsMetadataEnc = rsEnc.getMetaData();
        CellStyle estiloMoney = Util.generaEstiloBordesAnchos(workbook, 12, false, true, true, false, true);
        CellStyle estiloMoneyDoble = Util.generaEstiloBordesAnchos(workbook, 12, false, true, true, false, true);
        estiloMoneyDoble.setBorderLeft(BorderStyle.DOUBLE);
        estiloMoneyDoble.setBorderRight(BorderStyle.DOUBLE);
        CellStyle esTitulo = Util.generaEstiloBordesAnchos(workbook, 12, true, true, true, false, false);
        esTitulo.setAlignment(HorizontalAlignment.CENTER);
        CellStyle esTexto = Util.generaEstiloBordesAnchos(workbook, 12, false, true, true, false, false);
        esTexto.setBorderLeft(BorderStyle.DOUBLE);
        esTexto.setBorderRight(BorderStyle.DOUBLE);
        esTexto.setVerticalAlignment(VerticalAlignment.TOP);
        CellStyle esTexto2 = Util.generaEstiloBordesAnchos(workbook, 12, false, true, true, false, false);
        CellStyle esTextoSB = Util.generaEstilo(workbook, 12, false, false, false, false, false);
        esTextoSB.setVerticalAlignment(VerticalAlignment.TOP);
        esTextoSB.setAlignment(HorizontalAlignment.CENTER);
        CellStyle esTextoFinal = workbook.createCellStyle();
        ;
        esTextoFinal.setBorderTop(BorderStyle.DOUBLE);
        int fin = 27;
        if (numAnios <= 3) {
            fin = 27;
        } else {
            fin = 29;
        }
        if (rsEnc.next()) {
            for (int i = 0; i < 3; i++) {
                Row rw = (sheet0.getRow(i) == null ? sheet0.createRow(i) : sheet0.getRow(i));
                Util.createExcelCellRep(0, rw, rsEnc, rsMetadataEnc.getColumnName(i + 1), rsMetadataEnc.getColumnType(i + 1), esTitulo);
            }
            Row nDescProy = (sheet0.getRow(11) == null ? sheet0.createRow(11) : sheet0.getRow(11));
            Cell celdaDescProy = (nDescProy.getCell(15) == null ? nDescProy.createCell(15) : nDescProy.getCell(15));
            celdaDescProy.setCellValue(rsEnc.getString("cDescripcionProyecto"));
            for (int i = 4; i <= 6; i++) {
                Row rw = (sheet0.getRow(11) == null ? sheet0.createRow(11) : sheet0.getRow(11));
                Util.createExcelCellRep(i + 15, rw, rsEnc, rsMetadataEnc.getColumnName(i + 1), rsMetadataEnc.getColumnType(i + 1), esTexto);
            }
            //Justificacion
            Row nJust = (sheet0.getRow(33) == null ? sheet0.createRow(33) : sheet0.getRow(33));
            Cell celdaJust = (nJust.getCell(0) == null ? nJust.createCell(0) : nJust.getCell(0));
            celdaJust.setCellValue(rsEnc.getString("JustificacionBeneficios"));
            //Datos del firmante
            Row nPuesto = (sheet0.getRow(59) == null ? sheet0.createRow(59) : sheet0.getRow(59));
            Util.createExcelCellRep(1, nPuesto, rsEnc, rsMetadataEnc.getColumnName(9), rsMetadataEnc.getColumnType(9), esTextoSB);
            sheet0.addMergedRegion(new CellRangeAddress(nPuesto.getRowNum(), nPuesto.getRowNum(), 1, 10));
            Row nFirmaSol = (sheet0.getRow(66) == null ? sheet0.createRow(66) : sheet0.getRow(66));
            Util.createExcelCellRep(1, nFirmaSol, rsEnc, rsMetadataEnc.getColumnName(10), rsMetadataEnc.getColumnType(10), esTextoSB);
            sheet0.addMergedRegion(new CellRangeAddress(nFirmaSol.getRowNum(), nFirmaSol.getRowNum(), 1, 10));
        }
        int iCol = 22;
        int num = 0;
        if (numAnios > 3)
            num = numAnios - 3;
        else if (numAnios <= 3)
            numAnios = 3;
        while (rsDetM.next()) {
            //Encabezado
            Row nAnio = (sheet0.getRow(9) == null ? sheet0.createRow(9) : sheet0.getRow(9));
            Cell celdaAnio = (nAnio.getCell(iCol) == null ? nAnio.createCell(iCol) : nAnio.getCell(iCol));
            celdaAnio.setCellValue(rsDetM.getString("aEjercicio"));
            Cell celdaAAF = (nAnio.getCell(iCol + numAnios) == null ? nAnio.createCell(iCol + numAnios) : nAnio.getCell(iCol + numAnios));
            celdaAAF.setCellValue(rsDetM.getString("aEjercicio"));
            iCol++;
        }
        int iRowSec = 11;
        int iRowEP = 38;
        int renglon = 0;
        iCol = 22;
        String ejercicio = "", ejercicioEncabezado = "";
        Row nAnio = (sheet0.getRow(9) == null ? sheet0.createRow(9) : sheet0.getRow(9));
        while (rsDet.next()) {
            Row rw = (sheet0.getRow(iRowSec) == null ? sheet0.createRow(iRowSec) : sheet0.getRow(iRowSec));
            //ndocrenglon
            Util.createExcelCellRep(0, rw, rsDet, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), esTexto);
            for (int i = 5; i <= 18; i++) {
                Util.createExcelCellRep(i - 4, rw, rsDet, rsMetadata.getColumnName(i), rsMetadata.getColumnType(i), esTexto);
            }
            //Compara el año con el año de la clave para poner el importe en la columna que le corresponda
            ejercicio = rsDet.getString("iEjercicioClave");
            Cell celdaAnio = (nAnio.getCell(iCol));
            ejercicioEncabezado = celdaAnio.getStringCellValue();
            if (ejercicio.equals(ejercicioEncabezado)) {
                Util.createExcelCellRep(iCol, rw, rsDet, rsMetadata.getColumnName(31), rsMetadata.getColumnType(31), estiloMoneyDoble);
            } else {
                ++iCol;
                Util.createExcelCellRep(iCol, rw, rsDet, rsMetadata.getColumnName(31), rsMetadata.getColumnType(31), estiloMoneyDoble);
            }
            Row rw2 = (sheet0.getRow(iRowEP) == null ? sheet0.createRow(iRowEP) : sheet0.getRow(iRowEP));
            ////   DETALLE DE LAS CLAVES CORTAS
            Util.createExcelCellRep(0, rw2, rsDet, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), esTexto2);
            Util.createExcelCellRep(9, rw2, rsDet, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), esTexto2);
            Util.createExcelCellRep(11, rw2, rsDet, rsMetadata.getColumnName(31), rsMetadata.getColumnType(31), estiloMoney);
            Util.createExcelCellRep(14, rw2, rsDet, rsMetadata.getColumnName(19), rsMetadata.getColumnType(19), estiloMoney);
            Util.createExcelCellRep(15, rw2, rsDet, rsMetadata.getColumnName(20), rsMetadata.getColumnType(20), estiloMoney);
            Util.createExcelCellRep(16, rw2, rsDet, rsMetadata.getColumnName(21), rsMetadata.getColumnType(21), estiloMoney);
            Util.createExcelCellRep(17, rw2, rsDet, rsMetadata.getColumnName(22), rsMetadata.getColumnType(22), estiloMoney);
            Util.createExcelCellRep(18, rw2, rsDet, rsMetadata.getColumnName(23), rsMetadata.getColumnType(23), estiloMoney);
            Util.createExcelCellRep(19, rw2, rsDet, rsMetadata.getColumnName(24), rsMetadata.getColumnType(24), estiloMoney);
            Util.createExcelCellRep(20, rw2, rsDet, rsMetadata.getColumnName(25), rsMetadata.getColumnType(25), estiloMoney);
            Util.createExcelCellRep(21, rw2, rsDet, rsMetadata.getColumnName(26), rsMetadata.getColumnType(26), estiloMoney);
            Util.createExcelCellRep(22, rw2, rsDet, rsMetadata.getColumnName(27), rsMetadata.getColumnType(27), estiloMoney);
            if (numAnios > 4) {
                Util.createExcelCellRep(24, rw2, rsDet, rsMetadata.getColumnName(28), rsMetadata.getColumnType(28), estiloMoney);
                Util.createExcelCellRep(26, rw2, rsDet, rsMetadata.getColumnName(29), rsMetadata.getColumnType(29), estiloMoney);
                Util.createExcelCellRep(26 + num, rw2, rsDet, rsMetadata.getColumnName(30), rsMetadata.getColumnType(30), estiloMoney);
            } else if (numAnios <= 3) {
                Util.createExcelCellRep(23, rw2, rsDet, rsMetadata.getColumnName(28), rsMetadata.getColumnType(28), estiloMoney);
                Util.createExcelCellRep(24, rw2, rsDet, rsMetadata.getColumnName(29), rsMetadata.getColumnType(29), estiloMoney);
                Util.createExcelCellRep(26, rw2, rsDet, rsMetadata.getColumnName(30), rsMetadata.getColumnType(30), estiloMoney);
            } else {
                Util.createExcelCellRep(23, rw2, rsDet, rsMetadata.getColumnName(28), rsMetadata.getColumnType(28), estiloMoney);
                Util.createExcelCellRep(24 + num, rw2, rsDet, rsMetadata.getColumnName(29), rsMetadata.getColumnType(29), estiloMoney);
                Util.createExcelCellRep(26 + num, rw2, rsDet, rsMetadata.getColumnName(30), rsMetadata.getColumnType(30), estiloMoney);
            }
            iRowSec++;
            iRowEP++;
            renglon++;
            //Agrega renglones cuando sobrepasan los renglones que ya tiene la plantilla
            if (renglon > 15) {
                Util.copyRow(workbook, sheet0, 17, iRowSec);
                //Darle formato a los renglones que se agregaron
                for (int i = 19; i <= fin; i++) {
                    Cell celdaMontoA = (rw.getCell(i) == null ? rw.createCell(i) : rw.getCell(i));
                    celdaMontoA.setCellStyle(estiloMoneyDoble);
                }
                iRowEP++;
                Util.copyRow(workbook, sheet0, iRowSec + 11, iRowEP);
                if (renglon > 15) {
                    sheet0.addMergedRegion(new CellRangeAddress(rw2.getRowNum() + 1, rw2.getRowNum() + 1, 9, 10));
                    sheet0.addMergedRegion(new CellRangeAddress(rw2.getRowNum() + 1, rw2.getRowNum() + 1, 11, 13));
                    sheet0.addMergedRegion(new CellRangeAddress(rw2.getRowNum() + 1, rw2.getRowNum() + 1, 0, 8));
                }
                if (numAnios == 4) {
                    sheet0.addMergedRegion(new CellRangeAddress(rw2.getRowNum() + 1, rw2.getRowNum() + 1, 24, 25));
                    sheet0.addMergedRegion(new CellRangeAddress(rw2.getRowNum() + 1, rw2.getRowNum() + 1, 26, 26 + num - 1));
                    sheet0.addMergedRegion(new CellRangeAddress(rw2.getRowNum() + 1, rw2.getRowNum() + 1, 26 + num, 26 + num + 3));
                } else if (numAnios <= 3) {
                    sheet0.addMergedRegion(new CellRangeAddress(rw2.getRowNum() + 1, rw2.getRowNum() + 1, 24, 25));
                    sheet0.addMergedRegion(new CellRangeAddress(rw2.getRowNum() + 1, rw2.getRowNum() + 1, 26, 26 + num + 1));
                    Cell celdaMontoA = (rw2.getCell(26 + num + 1) == null ? rw2.createCell(26 + num + 1) : rw2.getCell(26 + num + 1));
                    celdaMontoA.setCellStyle(estiloMoney);
                } else {
                    sheet0.addMergedRegion(new CellRangeAddress(rw2.getRowNum(), rw2.getRowNum(), 24 + num, 24 + num + 1));
                    sheet0.addMergedRegion(new CellRangeAddress(rw2.getRowNum(), rw2.getRowNum(), 26 + num, 26 + num + 2));
                    Cell celdaMontoA = (rw2.getCell(26 + num + 2) == null ? rw2.createCell(26 + num + 2) : rw2.getCell(26 + num + 2));
                    celdaMontoA.setCellStyle(estiloMoney);
                }
            }
        }
        if (renglon > 15) {
            //Borrar el ultimo renglon de la segunda tabla
            Row row = (sheet0.getRow(iRowEP) == null ? sheet0.createRow(iRowEP) : sheet0.getRow(iRowEP));
            //esTextoFinal
            deleteRow(row, sheet0);
            //Borrar el ultimo renglon de la primera tabla
            Row rw = (sheet0.getRow(iRowSec) == null ? sheet0.createRow(iRowSec) : sheet0.getRow(iRowSec));
            //esTextoFinal
            deleteRow(rw, sheet0);
            /* //Pone la linea de abajo en la primera tabla de las claves presupuestales
			for (int i = 0; i<= fin; i++){
				Cell celdaMontoA = (rw.getCell(i) == null? rw.createCell(i): rw.getCell(i));
				celdaMontoA.setCellStyle( esTextoFinal );						
			}
			*/
            Row conc = sheet0.getRow(iRowSec + 1);
            conc.setHeight((short) 450);
            Row just = sheet0.getRow(iRowSec + 6);
            just.setHeight((short) 2500);
        }
        File filesalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(filesalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        workbook.close();
        return file_name;
    }

    //Eliminar columna
    private static void deleteRow(Row row, Sheet sheet) {
        int lastRowNum = sheet.getLastRowNum();
        int rowIndex = row.getRowNum();
        if (rowIndex >= 0 && rowIndex < lastRowNum) {
            sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
        }
        if (rowIndex == lastRowNum) {
            Row removingRow = sheet.getRow(rowIndex);
            if (removingRow != null) {
                sheet.removeRow(removingRow);
            }
        }
    }

    //Dice cuantos años tiene ese el contrato plurianual
    public static int consultaAniosPlurianual(Connection conn, String folio) throws Exception {
        int anios = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT COUNT (* ) numero FROM vw_ContratosPlurianual_MontosEjercicios WITH (NOLOCK) where Folio = ? ";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, folio);
            rs = ps.executeQuery();
            if (rs.next()) {
                anios = rs.getInt(1);
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return anios;
    }

    //Dice cuantos años tiene ese el contrato plurianual modificado
    public static int consultaAniosPlurianualMod(Connection conn, String folio) throws Exception {
        int anios = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT COUNT (* ) numero FROM tContratosPlurianual_EjercicioMod WITH (NOLOCK) where nFolioContratoPlurianual = ? ";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, folio);
            rs = ps.executeQuery();
            if (rs.next()) {
                anios = rs.getInt(1);
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return anios;
    }

    public static String ReporteExcelPlurianualesMod(ResultSet rsDet, ResultSet rsEnc, ResultSet rsDetM, ResultSet rsEncDD, ResultSet rsDetDD, ResultSet rsDetE, String plantillaPath, int numAnios, int nAOrigenMayor) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "FormatoSOLContratoPLU" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new XSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        ResultSetMetaData rsMetadata = rsDet.getMetaData();
        ResultSetMetaData rsMetadataEnc = rsEnc.getMetaData();
        ResultSetMetaData rsMetadataDD = rsDetDD.getMetaData();
        ResultSetMetaData rsMetadataEncDD = rsEncDD.getMetaData();
        CellStyle estiloMoney = Util.generaEstiloBordesAnchos(workbook, 12, false, true, true, false, true);
        CellStyle esTitulo = Util.generaEstiloBordesAnchos(workbook, 12, true, true, true, false, false);
        esTitulo.setAlignment(HorizontalAlignment.CENTER);
        CellStyle esTexto = Util.generaEstiloBordesAnchos(workbook, 12, false, true, true, false, false);
        esTexto.setBorderLeft(BorderStyle.DOUBLE);
        esTexto.setBorderRight(BorderStyle.DOUBLE);
        esTexto.setVerticalAlignment(VerticalAlignment.TOP);
        CellStyle esTexto2 = Util.generaEstiloBordesAnchos(workbook, 12, false, true, true, false, false);
        CellStyle esTextoSB = Util.generaEstilo(workbook, 12, false, false, false, false, false);
        esTextoSB.setVerticalAlignment(VerticalAlignment.TOP);
        esTextoSB.setAlignment(HorizontalAlignment.CENTER);
        if (rsEnc.next()) {
            //Encabezados
            for (int i = 0; i < 3; i++) {
                Row rw = (sheet0.getRow(i) == null ? sheet0.createRow(i) : sheet0.getRow(i));
                Util.createExcelCellRep(0, rw, rsEnc, rsMetadataEnc.getColumnName(i + 1), rsMetadataEnc.getColumnType(i + 1), esTitulo);
            }
            Row nDescProy = (sheet0.getRow(10) == null ? sheet0.createRow(10) : sheet0.getRow(10));
            Cell celdaDescProy = (nDescProy.getCell(15) == null ? nDescProy.createCell(15) : nDescProy.getCell(15));
            celdaDescProy.setCellValue(rsEnc.getString("cDescripcionProyecto"));
            for (int i = 4; i <= 6; i++) {
                Row rw = (sheet0.getRow(11) == null ? sheet0.createRow(11) : sheet0.getRow(11));
                Util.createExcelCellRep(i + 15, rw, rsEnc, rsMetadataEnc.getColumnName(i + 1), rsMetadataEnc.getColumnType(i + 1), esTexto);
            }
            Row nJust = (sheet0.getRow(33) == null ? sheet0.createRow(33) : sheet0.getRow(33));
            Cell celdaJust = (nJust.getCell(0) == null ? nJust.createCell(0) : nJust.getCell(0));
            celdaJust.setCellValue(rsEnc.getString("JustificacionBeneficios"));
            Row nPuesto = (sheet0.getRow(61) == null ? sheet0.createRow(61) : sheet0.getRow(61));
            Util.createExcelCellRep(1, nPuesto, rsEnc, rsMetadataEnc.getColumnName(9), rsMetadataEnc.getColumnType(9), esTextoSB);
            Row nFirmaSol = (sheet0.getRow(67) == null ? sheet0.createRow(67) : sheet0.getRow(67));
            Util.createExcelCellRep(1, nFirmaSol, rsEnc, rsMetadataEnc.getColumnName(10), rsMetadataEnc.getColumnType(10), esTextoSB);
        }
        if (rsEncDD.next()) {
            Row nDescProy = (sheet0.getRow(19) == null ? sheet0.createRow(19) : sheet0.getRow(19));
            Cell celdaDescProy = (nDescProy.getCell(15) == null ? nDescProy.createCell(15) : nDescProy.getCell(15));
            celdaDescProy.setCellValue(rsEncDD.getString("cDescripcionProyecto"));
            for (int i = 4; i <= 6; i++) {
                Row rw = (sheet0.getRow(20) == null ? sheet0.createRow(20) : sheet0.getRow(20));
                Util.createExcelCellRep(i + 15, rw, rsEncDD, rsMetadataEncDD.getColumnName(i + 1), rsMetadataEncDD.getColumnType(i + 1), esTexto);
            }
        }
        int iCol = 22;
        int iRowM = 11;
        int num = 0;
        if (numAnios > 3)
            num = numAnios - 3;
        else if (numAnios < 3)
            numAnios = 3;
        Row rDICE = (sheet0.getRow(10) == null ? sheet0.createRow(10) : sheet0.getRow(10));
        Cell celdaDICE = (rDICE.getCell(0) == null ? rDICE.createCell(0) : rDICE.getCell(0));
        celdaDICE.setCellValue("DICE:");
        Row rDICE2 = (sheet0.getRow(37) == null ? sheet0.createRow(37) : sheet0.getRow(37));
        Cell celdaDICE2 = (rDICE2.getCell(0) == null ? rDICE2.createCell(0) : rDICE2.getCell(0));
        celdaDICE2.setCellValue("DICE:");
        while (rsDetM.next()) {
            //Encabezado Años
            if (nAOrigenMayor == 1) {
                Row nAnio = (sheet0.getRow(9) == null ? sheet0.createRow(9) : sheet0.getRow(9));
                Cell celdaAnio = (nAnio.getCell(iCol) == null ? nAnio.createCell(iCol) : nAnio.getCell(iCol));
                celdaAnio.setCellValue(rsDetM.getString("aEjercicio"));
                Cell celdaAAF = (nAnio.getCell(iCol + numAnios) == null ? nAnio.createCell(iCol + numAnios) : nAnio.getCell(iCol + numAnios));
                celdaAAF.setCellValue(rsDetM.getString("aEjercicio"));
            }
            //Importes
            Row nMontoA = (sheet0.getRow(iRowM) == null ? sheet0.createRow(iRowM) : sheet0.getRow(iRowM));
            Cell celdaMontoA = (nMontoA.getCell(iCol) == null ? nMontoA.createCell(iCol) : nMontoA.getCell(iCol));
            celdaMontoA.setCellValue(rsDetM.getString("montoEjercicio"));
            iCol++;
            iRowM++;
        }
        iRowM = 18;
        Row rDD = (sheet0.getRow(iRowM) == null ? sheet0.createRow(iRowM) : sheet0.getRow(iRowM));
        Cell celdadd = (rDD.getCell(0) == null ? rDD.createCell(0) : rDD.getCell(0));
        celdadd.setCellValue("DEBE DECIR:");
        Row rDD2 = (sheet0.getRow(46) == null ? sheet0.createRow(46) : sheet0.getRow(46));
        Cell celdadd2 = (rDD2.getCell(0) == null ? rDD2.createCell(0) : rDD2.getCell(0));
        celdadd2.setCellValue("DEBE DECIR:");
        celdadd2.setCellStyle(esTextoSB);
        iRowM = iRowM + 1;
        iCol = 22;
        //Importes del Debe decir
        while (rsDetE.next()) {
            //Encabezado Años
            if (nAOrigenMayor == 0) {
                Row nAnio = (sheet0.getRow(9) == null ? sheet0.createRow(9) : sheet0.getRow(9));
                Cell celdaAnio = (nAnio.getCell(iCol) == null ? nAnio.createCell(iCol) : nAnio.getCell(iCol));
                celdaAnio.setCellValue(rsDetE.getString("aEjercicio"));
                Cell celdaAAF = (nAnio.getCell(iCol + numAnios) == null ? nAnio.createCell(iCol + numAnios) : nAnio.getCell(iCol + numAnios));
                celdaAAF.setCellValue(rsDetE.getString("aEjercicio"));
            }
            //Importes
            Row nMontoA = (sheet0.getRow(iRowM) == null ? sheet0.createRow(iRowM) : sheet0.getRow(iRowM));
            Cell celdaMontoA = (nMontoA.getCell(iCol) == null ? nMontoA.createCell(iCol) : nMontoA.getCell(iCol));
            celdaMontoA.setCellValue(rsDetE.getString("montoEjercicio"));
            iCol++;
            iRowM++;
        }
        int iRowSec = 11;
        int iRowEP = 38;
        //Detalle de claves presupuestales
        while (rsDet.next()) {
            Row rw = (sheet0.getRow(iRowSec) == null ? sheet0.createRow(iRowSec) : sheet0.getRow(iRowSec));
            Row rw2 = (sheet0.getRow(iRowEP) == null ? sheet0.createRow(iRowEP) : sheet0.getRow(iRowEP));
            Util.createExcelCellRep(0, rw, rsDet, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), esTexto);
            for (int i = 5; i <= 18; i++) {
                if (i <= 15)
                    Util.createExcelCellRep(i - 4, rw, rsDet, rsMetadata.getColumnName(i), rsMetadata.getColumnType(i), esTexto2);
                else
                    Util.createExcelCellRep(i - 4, rw, rsDet, rsMetadata.getColumnName(i), rsMetadata.getColumnType(i), esTexto);
            }
            //Util.createExcelCellRep(14, rw, rsDet, rsMetadata.getColumnName(46), rsMetadata.getColumnType(46), esTexto);
            ////   DETALLE DE LAS CLAVES CORTAS
            Util.createExcelCellRep(0, rw2, rsDet, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), esTexto2);
            Util.createExcelCellRep(9, rw2, rsDet, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), esTexto2);
            Util.createExcelCellRep(11, rw2, rsDet, rsMetadata.getColumnName(31), rsMetadata.getColumnType(31), estiloMoney);
            Util.createExcelCellRep(14, rw2, rsDet, rsMetadata.getColumnName(19), rsMetadata.getColumnType(19), estiloMoney);
            Util.createExcelCellRep(15, rw2, rsDet, rsMetadata.getColumnName(20), rsMetadata.getColumnType(20), estiloMoney);
            Util.createExcelCellRep(16, rw2, rsDet, rsMetadata.getColumnName(21), rsMetadata.getColumnType(21), estiloMoney);
            Util.createExcelCellRep(17, rw2, rsDet, rsMetadata.getColumnName(22), rsMetadata.getColumnType(22), estiloMoney);
            Util.createExcelCellRep(18, rw2, rsDet, rsMetadata.getColumnName(23), rsMetadata.getColumnType(23), estiloMoney);
            Util.createExcelCellRep(19, rw2, rsDet, rsMetadata.getColumnName(24), rsMetadata.getColumnType(24), estiloMoney);
            Util.createExcelCellRep(20, rw2, rsDet, rsMetadata.getColumnName(25), rsMetadata.getColumnType(25), estiloMoney);
            Util.createExcelCellRep(21, rw2, rsDet, rsMetadata.getColumnName(26), rsMetadata.getColumnType(26), estiloMoney);
            Util.createExcelCellRep(22, rw2, rsDet, rsMetadata.getColumnName(27), rsMetadata.getColumnType(27), estiloMoney);
            if (numAnios > 4) {
                Util.createExcelCellRep(24, rw2, rsDet, rsMetadata.getColumnName(28), rsMetadata.getColumnType(28), estiloMoney);
                Util.createExcelCellRep(26, rw2, rsDet, rsMetadata.getColumnName(29), rsMetadata.getColumnType(29), estiloMoney);
                Util.createExcelCellRep(26 + num, rw2, rsDet, rsMetadata.getColumnName(30), rsMetadata.getColumnType(30), estiloMoney);
            } else if (numAnios <= 3) {
                Util.createExcelCellRep(23, rw2, rsDet, rsMetadata.getColumnName(28), rsMetadata.getColumnType(28), estiloMoney);
                Util.createExcelCellRep(24, rw2, rsDet, rsMetadata.getColumnName(29), rsMetadata.getColumnType(29), estiloMoney);
                Util.createExcelCellRep(26, rw2, rsDet, rsMetadata.getColumnName(30), rsMetadata.getColumnType(30), estiloMoney);
            } else {
                Util.createExcelCellRep(23, rw2, rsDet, rsMetadata.getColumnName(28), rsMetadata.getColumnType(28), estiloMoney);
                Util.createExcelCellRep(24 + num, rw2, rsDet, rsMetadata.getColumnName(29), rsMetadata.getColumnType(29), estiloMoney);
                Util.createExcelCellRep(26 + num, rw2, rsDet, rsMetadata.getColumnName(30), rsMetadata.getColumnType(30), estiloMoney);
            }
            iRowSec++;
            iRowEP++;
        }
        iRowSec = 19;
        iRowEP = 47;
        //Detalle de claves presupuestales Debe decir
        while (rsDetDD.next()) {
            Row rw = (sheet0.getRow(iRowSec) == null ? sheet0.createRow(iRowSec) : sheet0.getRow(iRowSec));
            Row rw2 = (sheet0.getRow(iRowEP) == null ? sheet0.createRow(iRowEP) : sheet0.getRow(iRowEP));
            Util.createExcelCellRep(0, rw, rsDetDD, rsMetadataDD.getColumnName(3), rsMetadataDD.getColumnType(3), esTexto);
            for (int i = 5; i <= 18; i++) {
                if (i <= 15)
                    Util.createExcelCellRep(i - 4, rw, rsDetDD, rsMetadataDD.getColumnName(i), rsMetadataDD.getColumnType(i), esTexto2);
                else
                    Util.createExcelCellRep(i - 4, rw, rsDetDD, rsMetadataDD.getColumnName(i), rsMetadataDD.getColumnType(i), esTexto);
            }
            //Util.createExcelCellRep(14, rw, rsDetDD, rsMetadataDD.getColumnName(46), rsMetadataDD.getColumnType(46), esTexto);
            ////   DETALLE DE LAS CLAVES CORTAS
            Util.createExcelCellRep(0, rw2, rsDetDD, rsMetadataDD.getColumnName(4), rsMetadataDD.getColumnType(4), esTexto2);
            Util.createExcelCellRep(9, rw2, rsDetDD, rsMetadataDD.getColumnName(5), rsMetadataDD.getColumnType(5), esTexto2);
            Util.createExcelCellRep(11, rw2, rsDetDD, rsMetadataDD.getColumnName(31), rsMetadataDD.getColumnType(31), estiloMoney);
            Util.createExcelCellRep(14, rw2, rsDetDD, rsMetadataDD.getColumnName(19), rsMetadataDD.getColumnType(19), estiloMoney);
            Util.createExcelCellRep(15, rw2, rsDetDD, rsMetadataDD.getColumnName(20), rsMetadataDD.getColumnType(20), estiloMoney);
            Util.createExcelCellRep(16, rw2, rsDetDD, rsMetadataDD.getColumnName(21), rsMetadataDD.getColumnType(21), estiloMoney);
            Util.createExcelCellRep(17, rw2, rsDetDD, rsMetadataDD.getColumnName(22), rsMetadataDD.getColumnType(22), estiloMoney);
            Util.createExcelCellRep(18, rw2, rsDetDD, rsMetadataDD.getColumnName(23), rsMetadataDD.getColumnType(23), estiloMoney);
            Util.createExcelCellRep(19, rw2, rsDetDD, rsMetadataDD.getColumnName(24), rsMetadataDD.getColumnType(24), estiloMoney);
            Util.createExcelCellRep(20, rw2, rsDetDD, rsMetadataDD.getColumnName(25), rsMetadataDD.getColumnType(25), estiloMoney);
            Util.createExcelCellRep(21, rw2, rsDetDD, rsMetadataDD.getColumnName(26), rsMetadataDD.getColumnType(26), estiloMoney);
            Util.createExcelCellRep(22, rw2, rsDetDD, rsMetadataDD.getColumnName(27), rsMetadataDD.getColumnType(27), estiloMoney);
            if (numAnios > 4) {
                Util.createExcelCellRep(24, rw2, rsDetDD, rsMetadataDD.getColumnName(28), rsMetadataDD.getColumnType(28), estiloMoney);
                Util.createExcelCellRep(26, rw2, rsDetDD, rsMetadataDD.getColumnName(29), rsMetadataDD.getColumnType(29), estiloMoney);
                Util.createExcelCellRep(26 + num, rw2, rsDetDD, rsMetadataDD.getColumnName(30), rsMetadataDD.getColumnType(30), estiloMoney);
            } else if (numAnios <= 3) {
                Util.createExcelCellRep(23, rw2, rsDetDD, rsMetadataDD.getColumnName(28), rsMetadataDD.getColumnType(28), estiloMoney);
                Util.createExcelCellRep(24, rw2, rsDetDD, rsMetadataDD.getColumnName(29), rsMetadataDD.getColumnType(29), estiloMoney);
                Util.createExcelCellRep(26, rw2, rsDetDD, rsMetadataDD.getColumnName(30), rsMetadataDD.getColumnType(30), estiloMoney);
            } else {
                Util.createExcelCellRep(23, rw2, rsDetDD, rsMetadataDD.getColumnName(28), rsMetadataDD.getColumnType(28), estiloMoney);
                Util.createExcelCellRep(24 + num, rw2, rsDetDD, rsMetadataDD.getColumnName(29), rsMetadataDD.getColumnType(29), estiloMoney);
                Util.createExcelCellRep(26 + num, rw2, rsDetDD, rsMetadataDD.getColumnName(30), rsMetadataDD.getColumnType(30), estiloMoney);
            }
            iRowSec++;
            iRowEP++;
        }
        File filesalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(filesalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        workbook.close();
        return file_name;
    }

    public static void exportaReportes(Connection conn, String reportPath, String fileName, String folio, int esModificado, int nFolioContratoPlurianual, int esOriginal, OutputStream out) throws Exception {
        String query = "select MAX(nFolioContratoPlurianual) folioAnterior from tContratosPlurianualEnc_Mod where nFolioContratoOrigen =  " + nFolioContratoPlurianual + "		and nFolioContratoPlurianual <> " + folio;
        ResultSet rs = null;
        Statement stmnt = null;
        boolean first = true;
        ZipOutputStream zos = null;
        InputStream in = null;
        String nombreReporte1 = "";
        String nombreReporte2 = "", pdfName = "";
        int folioAnterior = 0;
        try {
            Map<String, Object> parametrosReporte = new HashMap<String, Object>();
            parametrosReporte.put("SUBREPORT_DIR", reportPath);
            if (esOriginal == 0) {
                if (esModificado == 1) {
                    stmnt = conn.createStatement();
                    rs = stmnt.executeQuery(query);
                    if (rs.next()) {
                        folioAnterior = rs.getInt("folioAnterior");
                    }
                    nombreReporte1 = "ModFormatoEvaluacionMT.jasper";
                    nombreReporte2 = "ModSolContratoPlurianual.jasper";
                    parametrosReporte.put("folioUltimo", folioAnterior);
                    parametrosReporte.put("whereFolio", folio);
                } else {
                    nombreReporte1 = "ModFormatoEvaluacionOriginal.jasper";
                    nombreReporte2 = "ModSolContratoPlurianualOriginal.jasper";
                    parametrosReporte.put("whereFolio", folio);
                }
            } else {
                nombreReporte1 = "FormatoEvaluacionMT.jasper";
                nombreReporte2 = "SolicitudContratoPlurianual.jasper";
                parametrosReporte.put("whereFolio", folio);
            }
            for (int i = 1; i < 3; i++) {
                if (i == 1) {
                    in = new FileInputStream(reportPath + "\\" + nombreReporte1);
                    pdfName = nombreReporte1 + ".pdf";
                } else if (i == 2) {
                    in = new FileInputStream(reportPath + "\\" + nombreReporte2);
                    pdfName = nombreReporte2 + ".pdf";
                }
                if (first) {
                    zos = new ZipOutputStream(out);
                    first = false;
                }
                ZipEntry ze = new ZipEntry(pdfName);
                zos.putNextEntry(ze);
                JasperRunManager.runReportToPdfStream(in, zos, parametrosReporte, conn);
                log.debug("Object: {}", "Procesando reporte del folio" + folio + " y el contrato anterior fue " + folioAnterior);
                if (in != null)
                    try {
                        in.close();
                    } catch (Exception e) {
                        log.warn("Object: {}", "Problemas cerrando reporte. " + e);
                    }
            }
            if (zos != null) {
                zos.flush();
                zos.closeEntry();
                zos.close();
                out.flush();
                out.close();
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(stmnt);
        }
    }
}
