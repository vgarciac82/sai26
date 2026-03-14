package com.syc.obrapublica;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import java.util.Base64;

public class ReporteF10ExcelBean {

    int numReg;

    int numCol;

    int numAgupa;

    String[][] valCelda;

    CellStyle[][] arrStyle;

    int[][] tipoCelda;

    int[][] arrayAgrupa;

    public ReporteF10ExcelBean(int numReg, int numCol, Sheet sheet, int regInicio, int numAgupa) {
        setNumReg(numReg);
        setNumCol(numCol);
        this.valCelda = new String[numReg][numCol];
        this.arrStyle = new CellStyle[numReg][numCol];
        this.tipoCelda = new int[numReg][numCol];
        if (numAgupa > 0) {
            this.arrayAgrupa = new int[numAgupa][4];
            setNumAgupa(numAgupa);
        }
        Row RowDetIntegra = null;
        //int nCol=0;
        Cell celda = null;
        CellStyle cellStyle6 = null;
        int nRow = 0;
        while (nRow < numReg) {
            try {
                RowDetIntegra = sheet.getRow(regInicio);
            } catch (Exception hazNada) {
                RowDetIntegra = sheet.createRow(regInicio);
            }
            if (RowDetIntegra == null) {
                RowDetIntegra = sheet.createRow(regInicio);
            }
            regInicio++;
            for (int nCol = 0; nCol < numCol; nCol++) {
                try {
                    celda = RowDetIntegra.getCell(nCol);
                } catch (Exception hazNada) {
                    celda = RowDetIntegra.createCell(nCol);
                }
                if (celda == null) {
                    celda = RowDetIntegra.createCell(nCol);
                }
                cellStyle6 = celda.getCellStyle();
                arrStyle[nRow][nCol] = cellStyle6;
                //tipoCelda[nRow][nCol].getCellType() = celda.getCellType();
                switch(// 2 = formula, 0 = numerico, 1 = string
                celda.getCellType()) {
                    case NUMERIC:
                        valCelda[nRow][nCol] = String.valueOf(celda.getNumericCellValue());
                        break;
                    case STRING:
                        valCelda[nRow][nCol] = celda.getStringCellValue();
                        break;
                    case FORMULA:
                        valCelda[nRow][nCol] = celda.getCellFormula();
                        break;
                    case BOOLEAN:
                        valCelda[nRow][nCol] = celda.getStringCellValue();
                        break;
                    default:
                        valCelda[nRow][nCol] = "";
                        break;
                }
            }
            nRow++;
        }
    }

    public int getNumAgupa() {
        return numAgupa;
    }

    public void setNumAgupa(int numAgupa) {
        this.numAgupa = numAgupa;
    }

    public int[][] getTipoCelda() {
        return tipoCelda;
    }

    public void setTipoCelda(int[][] tipoCelda) {
        this.tipoCelda = tipoCelda;
    }

    public int[][] getArrayAgrupa() {
        return arrayAgrupa;
    }

    public void setArrayAgrupa(int[][] arrayAgrupa) {
        this.arrayAgrupa = arrayAgrupa;
    }

    public String[][] getValCelda() {
        return valCelda;
    }

    public void setValCelda(String[][] valCelda) {
        this.valCelda = valCelda;
    }

    public CellStyle[][] getArrStyle() {
        return arrStyle;
    }

    public void setArrStyle(CellStyle[][] arrStyle) {
        this.arrStyle = arrStyle;
    }

    public int[][] gettipoCelda() {
        return tipoCelda;
    }

    public void settipoCelda(int[][] tipoCelda) {
        this.tipoCelda = tipoCelda;
    }

    public int getNumReg() {
        return numReg;
    }

    public void setNumReg(int numReg) {
        this.numReg = numReg;
    }

    public int getNumCol() {
        return numCol;
    }

    public void setNumCol(int numCol) {
        this.numCol = numCol;
    }

    public void acumula(String[][] pValCelda) {
        for (int nCol = 0; nCol < this.numCol; nCol++) {
            switch(// 2 = formula, 0 = numerico, 1 = string
            this.tipoCelda[0][nCol]) {
                case 0:
                    try {
                        this.valCelda[0][nCol] = Double.toString(Double.parseDouble(this.valCelda[0][nCol]) + Double.parseDouble(pValCelda[0][nCol]));
                    } catch (Exception hazNada) {
                    }
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                default:
                    break;
            }
        }
    }

    public void totResumen(String[][] pValCelda) {
        try {
            this.valCelda[1][5] = Double.toString(Double.parseDouble(pValCelda[0][1]));
            this.valCelda[2][5] = Double.toString(Double.parseDouble(pValCelda[0][2]));
            this.valCelda[3][5] = Double.toString(Double.parseDouble(pValCelda[0][3]));
            this.valCelda[4][5] = Double.toString(Double.parseDouble(pValCelda[0][4]));
            this.valCelda[5][5] = Double.toString(Double.parseDouble(pValCelda[0][1]) + Double.parseDouble(pValCelda[0][2]) + Double.parseDouble(pValCelda[0][3]) + Double.parseDouble(pValCelda[0][4]));
            this.valCelda[5][7] = Double.toString(Double.parseDouble(this.valCelda[1][7]) + Double.parseDouble(this.valCelda[2][7]) + Double.parseDouble(this.valCelda[3][7]) + Double.parseDouble(this.valCelda[4][7]));
        } catch (Exception hazNada) {
        }
    }

    public void acumResumen(String[][] pValCelda) {
        try {
            if ("1".equalsIgnoreCase(pValCelda[0][1]))
                this.valCelda[1][7] = Double.toString(Double.parseDouble(this.valCelda[1][7]) + Double.parseDouble(pValCelda[0][13]));
            if ("1".equalsIgnoreCase(pValCelda[0][2]))
                this.valCelda[2][7] = Double.toString(Double.parseDouble(this.valCelda[2][7]) + Double.parseDouble(pValCelda[0][13]));
            if ("1".equalsIgnoreCase(pValCelda[0][3]))
                this.valCelda[3][7] = Double.toString(Double.parseDouble(this.valCelda[3][7]) + Double.parseDouble(pValCelda[0][13]));
            if ("1".equalsIgnoreCase(pValCelda[0][4]))
                this.valCelda[4][7] = Double.toString(Double.parseDouble(this.valCelda[4][7]) + Double.parseDouble(pValCelda[0][13]));
            //this.valCelda[5][6] = Double.toString(Double.parseDouble(pValCelda[0][1])+Double.parseDouble(pValCelda[0][2])+Double.parseDouble(pValCelda[0][3])+Double.parseDouble(pValCelda[0][4]));
        } catch (Exception hazNada) {
        }
    }

    public void reinicia() {
        int nRow = 0;
        while (nRow < this.numReg) {
            for (int nCol = 0; nCol < this.numCol; nCol++) {
                switch(// 2 = formula, 0 = numerico, 1 = string
                this.tipoCelda[nRow][nCol]) {
                    case 0:
                        try {
                            this.valCelda[nRow][nCol] = Double.toString(Double.parseDouble("0"));
                        } catch (Exception hazNada) {
                        }
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    default:
                        break;
                }
            }
            nRow++;
        }
    }

    public void print(Sheet sheet, int regInicio) {
        int nRow = 0;
        Row rowDetIntegra = null;
        //int nCol=0;
        Cell celda = null;
        CellStyle cellStyle6 = null;
        int regActual = regInicio;
        while (nRow < this.numReg) {
            rowDetIntegra = sheet.createRow(regActual++);
            for (int nCol = 0; nCol < this.numCol; nCol++) {
                celda = rowDetIntegra.createCell(nCol);
                //cellStyle6 = this.arrStyle[nRow][nCol];
                //arrStyle[nRow][nCol] = cellStyle6;
                //tipoCelda[nRow][nCol] = celda.getCellType();
                celda.setCellStyle(arrStyle[nRow][nCol]);
                //				celda.setCellType(tipoCelda[nRow][nCol]);
                switch(// 2 = formula, 0 = numerico, 1 = string
                tipoCelda[nRow][nCol]) {
                    case 0:
                        try {
                            celda.setCellValue(Double.parseDouble(valCelda[nRow][nCol]));
                        } catch (Exception hazNada) {
                            celda.setCellValue(valCelda[nRow][nCol]);
                        }
                        break;
                    case 1:
                        //celda.setCellValue(valCelda[nRow][nCol]);
                        celda.setCellValue(valCelda[nRow][nCol]);
                        break;
                    case 2:
                        //celda.setCellFormula(valCelda[nRow][nCol]);
                        break;
                    case 3:
                        celda.setCellValue(valCelda[nRow][nCol]);
                        break;
                    default:
                        celda.setCellValue(valCelda[nRow][nCol]);
                        break;
                }
            }
            nRow++;
        }
        for (nRow = 0; nRow < this.numAgupa; nRow++) {
            sheet.addMergedRegion(new CellRangeAddress(// mention first row here
            regInicio + arrayAgrupa[nRow][0], //mention last row here, it is 1 as we are doing a column wise merging
            regInicio + arrayAgrupa[nRow][1], //mention first column of merging
            arrayAgrupa[nRow][2], //mention last column to include in merge
            arrayAgrupa[nRow][3]));
        }
    }
}
