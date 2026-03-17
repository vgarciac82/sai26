package com.syc.ejercido.pagado.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;
import java.nio.file.Paths;

public class BoletajeAereoManager {

    public class Boletos {

        private String cBoleto;

        private BigDecimal mMontoBoleto;

        private int nFolioRelacionGastos;

        /**
         * @param cBoleto
         * @param mMontoBoleto
         * @param nFolioRelacionGastos
         */
        public Boletos(String cBoleto, BigDecimal mMontoBoleto, int nFolioRelacionGastos) {
            this.cBoleto = cBoleto;
            this.mMontoBoleto = mMontoBoleto;
            this.nFolioRelacionGastos = nFolioRelacionGastos;
        }

        public String getcBoleto() {
            return cBoleto;
        }

        public BigDecimal getmMontoBoleto() {
            return mMontoBoleto;
        }

        /**
         * @return the nFolioRelacionGastos
         */
        public int getnFolioRelacionGastos() {
            return nFolioRelacionGastos;
        }

        public void setcBoleto(String cboleto) {
            cBoleto = cboleto;
        }

        public void setmMontoBoleto(BigDecimal mMontoBoleto) {
            this.mMontoBoleto = mMontoBoleto;
        }

        /**
         * @param nFolioRelacionGastos
         *            the nFolioRelacionGastos to set
         */
        public void setnFolioRelacionGastos(int nFolioRelacionGastos) {
            this.nFolioRelacionGastos = nFolioRelacionGastos;
        }

        /*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
        @Override
        public String toString() {
            return "Boletos [nFolioRelacionGastos=" + nFolioRelacionGastos + ", cBoleto=" + cBoleto + ", mMontoBoleto=" + mMontoBoleto + "]";
        }
    }

    public static boolean existeBoleto(Connection conn, Boletos boleto) throws Exception {
        boolean existe = false;
        Statement stmnt = null;
        String queryExiste = "SELECT	COUNT(*) AS existe " + "  FROM	tInfoBoleto WITH(NOLOCK) " + " WHERE	nFolioRelacionGastos = " + boleto.getnFolioRelacionGastos() + " AND cNumeroBoleto = '" + boleto.getcBoleto() + "'";
        ResultSet rs = null;
        try {
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery(queryExiste);
            if (rs.next())
                existe = rs.getInt(1) > 0;
            return existe;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(stmnt);
        }
    }

    public static int insertaBoletos(Connection conn, List<Boletos> info) throws Exception {
        PreparedStatement psInsert = null;
        String queryInsert = "INSERT INTO tInfoBoleto(nFolioRelacionGastos, cNumeroBoleto, mImporteBoleto )VALUES(?,?,?)";
        int insertados = 0;
        try {
            psInsert = conn.prepareStatement(queryInsert);
            for (int i = 0; i < info.size(); i++) {
                Boletos b = info.get(i);
                if (!existeBoleto(conn, b)) {
                    psInsert.setInt(1, b.getnFolioRelacionGastos());
                    psInsert.setString(2, b.getcBoleto());
                    psInsert.setBigDecimal(3, b.getmMontoBoleto());
                    insertados += psInsert.executeUpdate();
                }
            }
            return insertados;
        } finally {
            CloseObject.closeObject(psInsert);
        }
    }

    public static List<Boletos> parseBoletajeAereo(HttpServletRequest req) throws Exception {
        String[] numBoletos = req.getParameterValues("numeroBoleto");
        String[] importeBoletos = req.getParameterValues("importeBoleto");
        List<Boletos> boletos = new ArrayList<Boletos>();
        if (numBoletos.length != importeBoletos.length)
            throw new Exception("No coincide le numero de importes con el numero de boletos.");
        BoletajeAereoManager bam = new BoletajeAereoManager();
        for (int i = 0; i < numBoletos.length; i++) {
            boletos.add(bam.new Boletos(numBoletos[i], (new BigDecimal(importeBoletos[i])).setScale(2), Integer.parseInt(req.getParameter("folioRG"))));
        }
        return boletos;
    }

    public static List<Map<String, String>> leePagos(Connection conn, int nFolioPago) throws Exception {
        String query = "SELECT	nFolioRelacionGastos, cNumeroBoleto, mImporteBoleto " + "	FROM	tInfoBoleto WITH(nolock) " + " WHERE	nFolioRelacionGastos = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Map<String, String>> resultado = new ArrayList<Map<String, String>>();
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, nFolioPago);
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, String> obj = new HashMap<String, String>();
                obj.put("cNumeroBoleto", rs.getString("cNumeroBoleto"));
                obj.put("mImporteBoleto", Util.formatNumber(rs.getBigDecimal("mImporteBoleto")));
                obj.put("nFolioRelacionGastos", rs.getString("nFolioRelacionGastos"));
                resultado.add(obj);
            }
            return resultado;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static int insertaBoletosTemporal(Connection conn, String cTipoPago, int nFolioPago, int nFolioVuelos, String[] nDocRenglonVuelos) throws Exception {
        if (nDocRenglonVuelos != null) {
            String queryInsert = "INSERT INTO tTempVuelosEnCaptura(cTipoPago, nFolioPago, nFolioVuelos, nDocRenglon) VALUES (?,?,?,?)";
            PreparedStatement psInsert = null;
            int insertados = 0;
            try {
                psInsert = conn.prepareStatement(queryInsert);
                for (int i = 0; i < nDocRenglonVuelos.length; i++) {
                    psInsert.setString(1, cTipoPago);
                    psInsert.setInt(2, nFolioPago);
                    psInsert.setInt(3, nFolioVuelos);
                    psInsert.setInt(4, Integer.parseInt(StringUtils.trim(nDocRenglonVuelos[i])));
                    insertados += psInsert.executeUpdate();
                    psInsert.clearParameters();
                }
                return insertados;
            } finally {
                CloseObject.closeObject(psInsert);
            }
        } else
            return 0;
    }

    public static String generaExcelVuelos(HttpServletRequest req, HttpServletResponse resp, String plantillaPath) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "FormatoVuelos" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
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
        String[] boleto = req.getParameterValues("boleto");
        String[] rfc = req.getParameterValues("rfc");
        String[] nombre = req.getParameterValues("nombre");
        String[] ruta = req.getParameterValues("ruta");
        String[] fsalida = req.getParameterValues("fsalida");
        String[] fregreso = req.getParameterValues("fregreso");
        String[] status = req.getParameterValues("status");
        String[] pagado = req.getParameterValues("pagado");
        String[] folio = req.getParameterValues("folio");
        String[] partida = req.getParameterValues("partida");
        String[] total = req.getParameterValues("total");
        String[] ue = req.getParameterValues("ue");
        String[] ur = req.getParameterValues("urE");
        for (int i = 0; i < boleto.length; i++) {
            Row rowboleto = (sheet0.getRow(i + 1) == null ? sheet0.createRow(i + 1) : sheet0.getRow(i + 1));
            Cell celdaboleto = (rowboleto.getCell(0) == null ? rowboleto.createCell(0) : rowboleto.getCell(0));
            celdaboleto.setCellValue(boleto[i]);
            Row rowrfc = (sheet0.getRow(i + 1) == null ? sheet0.createRow(i + 1) : sheet0.getRow(i + 1));
            Cell celdarfc = (rowrfc.getCell(1) == null ? rowrfc.createCell(1) : rowrfc.getCell(1));
            celdarfc.setCellValue(rfc[i]);
            Row rownombre = (sheet0.getRow(i + 1) == null ? sheet0.createRow(i + 1) : sheet0.getRow(i + 1));
            Cell celdanombre = (rownombre.getCell(2) == null ? rownombre.createCell(2) : rownombre.getCell(2));
            celdanombre.setCellValue(nombre[i]);
            Row rowruta = (sheet0.getRow(i + 1) == null ? sheet0.createRow(i + 1) : sheet0.getRow(i + 1));
            Cell celdaruta = (rowruta.getCell(3) == null ? rowruta.createCell(3) : rowruta.getCell(3));
            celdaruta.setCellValue(ruta[i]);
            Row rowfsalida = (sheet0.getRow(i + 1) == null ? sheet0.createRow(i + 1) : sheet0.getRow(i + 1));
            Cell celdafsalida = (rowfsalida.getCell(4) == null ? rowfsalida.createCell(4) : rowfsalida.getCell(4));
            celdafsalida.setCellValue(fsalida[i]);
            Row rowfregreso = (sheet0.getRow(i + 1) == null ? sheet0.createRow(i + 1) : sheet0.getRow(i + 1));
            Cell celdafregreso = (rowfregreso.getCell(5) == null ? rowfregreso.createCell(5) : rowfregreso.getCell(5));
            celdafregreso.setCellValue(fregreso[i]);
            Row rowstatus = (sheet0.getRow(i + 1) == null ? sheet0.createRow(i + 1) : sheet0.getRow(i + 1));
            Cell celdastatus = (rowstatus.getCell(6) == null ? rowstatus.createCell(6) : rowstatus.getCell(6));
            celdastatus.setCellValue(status[i]);
            Row rowpagado = (sheet0.getRow(i + 1) == null ? sheet0.createRow(i + 1) : sheet0.getRow(i + 1));
            Cell celdapagado = (rowpagado.getCell(7) == null ? rowpagado.createCell(7) : rowpagado.getCell(7));
            celdapagado.setCellValue(pagado[i]);
            Row rowfolio = (sheet0.getRow(i + 1) == null ? sheet0.createRow(i + 1) : sheet0.getRow(i + 1));
            Cell celdafolio = (rowfolio.getCell(8) == null ? rowfolio.createCell(8) : rowfolio.getCell(8));
            celdafolio.setCellValue(folio[i]);
            Row rowpartida = (sheet0.getRow(i + 1) == null ? sheet0.createRow(i + 1) : sheet0.getRow(i + 1));
            Cell celdapartida = (rowpartida.getCell(9) == null ? rowpartida.createCell(9) : rowpartida.getCell(9));
            celdapartida.setCellValue(partida[i]);
            Row rowtotal = (sheet0.getRow(i + 1) == null ? sheet0.createRow(i + 1) : sheet0.getRow(i + 1));
            Cell celdatotal = (rowtotal.getCell(10) == null ? rowtotal.createCell(10) : rowtotal.getCell(10));
            celdatotal.setCellValue(total[i]);
            Row rowUE = (sheet0.getRow(i + 1) == null ? sheet0.createRow(i + 1) : sheet0.getRow(i + 1));
            Cell celdaUE = (rowUE.getCell(11) == null ? rowUE.createCell(11) : rowUE.getCell(11));
            celdaUE.setCellValue(ue[i]);
            Row rowUR = (sheet0.getRow(i + 1) == null ? sheet0.createRow(i + 1) : sheet0.getRow(i + 1));
            Cell celdaUR = (rowUR.getCell(12) == null ? rowUR.createCell(12) : rowUR.getCell(12));
            celdaUR.setCellValue(ur[i]);
        }
        File filesalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(filesalida);
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
