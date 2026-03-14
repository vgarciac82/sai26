package com.syc.contable;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import com.syc.contable.core.ReporteEjercicioAnterior;
import com.syc.contable.core.ReporteEjercicioAnteriorManager;
import com.syc.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ReporteEjercicioAnteriorBussinessLogic extends DataSourceManager {

    private final String MODIFICADO = "MODIFICADO";

    private final String ORIGINAL = "ORIGINAL";

    private final String COMPROMETIDO = "COMPROMETIDO";

    List<ReporteEjercicioAnterior> modificado = new ArrayList<ReporteEjercicioAnterior>();

    List<ReporteEjercicioAnterior> original = new ArrayList<ReporteEjercicioAnterior>();

    List<ReporteEjercicioAnterior> comprometido = new ArrayList<ReporteEjercicioAnterior>();

    private static Logger log = LoggerFactory.getLogger(PresupuestoBusinessLogic.class);

    public ReporteEjercicioAnteriorBussinessLogic(String jniName) {
        super.init(jniName);
    }

    public ReporteEjercicioAnteriorBussinessLogic() {
    }

    public void prueba(List<String> capitulo, String unidad, String fuente, String partida, String ejercicio, HttpServletResponse response, HttpServletRequest request) {
        ArrayList<String> arrLResult = new ArrayList<String>();
        //no puede llevar el &aacute porque se corta
        arrLResult.add("Reporte de inconsistencias de EPs vs Catálogos<br>");
        Connection conn = null;
        try {
            conn = getConnection();
            List<ReporteEjercicioAnterior> lista = ReporteEjercicioAnteriorManager.validaEPDetalle(capitulo, unidad, fuente, partida, conn, ejercicio);
            List<ReporteEjercicioAnterior> lista13 = ReporteEjercicioAnteriorManager.validaEPDetalle2013(capitulo, unidad, fuente, partida, conn, ejercicio);
            List<String> unidades = ReporteEjercicioAnteriorManager.obtenerUnidades(capitulo, unidad, fuente, partida, ejercicio, conn);
            new ReporteEjercicioAnteriorBussinessLogic().creaExcelEjercicio(lista, lista13, unidades, response, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void creaExcelEjercicio(List<ReporteEjercicioAnterior> lista, List<ReporteEjercicioAnterior> lista13, List<String> unidades, HttpServletResponse response, HttpServletRequest request) throws IOException, SQLException {
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"" + "prueba" + "Carga.xls\";");
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet hs = wb.createSheet();
        HSSFRow fila = hs.createRow(0);
        Sheet sheet = wb.getSheetAt(0);
        //ajusta el ancho de la primera columna
        sheet.autoSizeColumn((short) 0);
        //ajusta el ancho de la segunda columna
        sheet.autoSizeColumn((short) 1);
        //ajusta el ancho de la tercera columna
        sheet.autoSizeColumn((short) 2);
        //ajusta el ancho de la cuarta columna
        sheet.autoSizeColumn((short) 3);
        sheet.setColumnWidth((short) 0, (short) (8 / ((double) 1 / 256)));
        sheet.setColumnWidth((short) 1, (short) (18 / ((double) 1 / 256)));
        sheet.setColumnWidth((short) 2, (short) (18 / ((double) 1 / 256)));
        sheet.setColumnWidth((short) 3, (short) (18 / ((double) 1 / 256)));
        sheet.setColumnWidth((short) 4, (short) (18 / ((double) 1 / 256)));
        sheet.setColumnWidth((short) 5, (short) (18 / ((double) 1 / 256)));
        sheet.setColumnWidth((short) 6, (short) (18 / ((double) 1 / 256)));
        //estiloNegrita
        HSSFCellStyle estiloNegrita = wb.createCellStyle();
        HSSFFont negrita = wb.createFont();
        negrita.setBold(true);
        estiloNegrita.setFont(negrita);
        //estiloCentrado
        HSSFCellStyle estilocentrado = wb.createCellStyle();
        estilocentrado.setAlignment(HorizontalAlignment.CENTER);
        //unidades
        if (unidades.size() > 0) {
            HSSFCell celda = null;
            int valorMaximo = 0;
            celda = fila.createCell(0);
            celda.setCellValue("2012");
            estiloNegrita.setAlignment(HorizontalAlignment.CENTER);
            celda.setCellStyle(estiloNegrita);
            hs.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
            celda = fila.createCell(4);
            celda.setCellValue("2013");
            estiloNegrita.setAlignment(HorizontalAlignment.CENTER);
            celda.setCellStyle(estiloNegrita);
            hs.addMergedRegion(new CellRangeAddress(0, 0, 4, 6));
            fila = hs.createRow(1);
            for (int i = 0; i < unidades.size(); i++) {
                celda = fila.createCell(0);
                celda.setCellValue(unidades.get(i));
                celda.setCellStyle(estiloNegrita);
                celda = fila.createCell(1);
                celda.setCellValue("ORIGINAL");
                celda.setCellStyle(estiloNegrita);
                celda = fila.createCell(2);
                celda.setCellValue("MODIFICADO");
                celda.setCellStyle(estiloNegrita);
                celda = fila.createCell(3);
                celda.setCellValue("EJERCIDO");
                celda.setCellStyle(estiloNegrita);
                celda = fila.createCell(4);
                celda.setCellValue("ORIGINAL");
                celda.setCellStyle(estiloNegrita);
                celda = fila.createCell(5);
                celda.setCellValue("MODIFICADO");
                celda.setCellStyle(estiloNegrita);
                celda = fila.createCell(6);
                celda.setCellValue("EJERCIDO");
                celda.setCellStyle(estiloNegrita);
                ++valorMaximo;
                ++valorMaximo;
                for (int j = 0; j < lista.size(); j++) {
                    if (lista.get(j).getUnidadEjecutora().equals(unidades.get(i))) {
                        HSSFCellStyle cellStyle = wb.createCellStyle();
                        fila = hs.createRow(valorMaximo);
                        celda = fila.createCell(0);
                        celda.setCellValue(lista.get(j).getCprograma_presupuestario());
                        celda.setCellStyle(estilocentrado);
                        celda = fila.createCell(1);
                        if (lista.get(j).getSaldoOriginal() != null) {
                            cellStyle.setDataFormat((short) 7);
                            celda.setCellValue(Double.valueOf(lista.get(j).getSaldoOriginal()));
                            celda.setCellStyle(cellStyle);
                        } else {
                            celda.setCellValue(" - - - ");
                        }
                        celda = fila.createCell(2);
                        if (lista.get(j).getsaldoModificado() != null) {
                            cellStyle.setDataFormat((short) 7);
                            celda.setCellValue(Double.valueOf(lista.get(j).getsaldoModificado()));
                            celda.setCellStyle(cellStyle);
                        } else {
                            celda.setCellValue(" - - - ");
                        }
                        celda = fila.createCell(3);
                        if (lista.get(j).getsaldoEjercido() != null) {
                            cellStyle.setDataFormat((short) 7);
                            celda.setCellValue(Double.valueOf(lista.get(j).getsaldoEjercido()));
                            celda.setCellStyle(cellStyle);
                        } else {
                            celda.setCellValue(" - - - ");
                        }
                        //2013
                        celda = fila.createCell(4);
                        if (lista13.get(j).getSaldoOriginal() != null) {
                            cellStyle.setDataFormat((short) 7);
                            celda.setCellValue(Double.valueOf(lista13.get(j).getSaldoOriginal()));
                            celda.setCellStyle(cellStyle);
                        } else {
                            celda.setCellValue(" - - - ");
                        }
                        celda = fila.createCell(5);
                        if (lista13.get(j).getsaldoModificado() != null) {
                            cellStyle.setDataFormat((short) 7);
                            celda.setCellValue(Double.valueOf(lista13.get(j).getsaldoModificado()));
                            celda.setCellStyle(cellStyle);
                        } else {
                            celda.setCellValue(" - - - ");
                        }
                        celda = fila.createCell(6);
                        if (lista13.get(j).getsaldoEjercido() != null) {
                            cellStyle.setDataFormat((short) 7);
                            celda.setCellValue(Double.valueOf(lista13.get(j).getsaldoEjercido()));
                            celda.setCellStyle(cellStyle);
                        } else {
                            celda.setCellValue(" - - - ");
                        }
                        ++valorMaximo;
                    }
                }
                fila = hs.createRow(++valorMaximo);
            }
        }
        wb.write(response.getOutputStream());
        wb.close();
    }
}
