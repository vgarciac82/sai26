package com.syc.sai.fonden.model;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.dsmngr.DataSourceManager;
import com.syc.ejercido.pagado.LayoutGeneralBussinessLogic;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.fonden.Fonden;
import com.syc.sai.fonden.FondenEngineException;
import com.syc.sai.fonden.FondenMovimiento;
import com.syc.sai.fonden.FondenMovimientoEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class FondenMovimientoBusinessLogic extends DataSourceManager {

    Logger log = LoggerFactory.getLogger(FondenMovimientoBusinessLogic.class);

    public List<FondenMovimiento> readFondenMovimiento(Integer cidFonde, Integer nIdFondenMovimiento) throws FondenMovimientoEngineException {
        Connection conn = null;
        try {
            conn = getConnection();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return FondenMovimientoManager.readFondenMovimiento(conn, cidFonde, nIdFondenMovimiento);
        } catch (Exception e) {
            throw new FondenMovimientoEngineException(e);
        } finally {
        }
    }

    public List<FondenMovimiento> readFondenMovimientoBy(String restrictions) throws FondenMovimientoEngineException {
        Connection conn = null;
        try {
            conn = getConnection();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return FondenMovimientoManager.readFondenMovimiento(conn, restrictions);
        } catch (Exception e) {
            throw new FondenMovimientoEngineException(e);
        } finally {
        }
    }

    public boolean validateNoGreaterThanImporteAnual(HttpServletRequest req) throws FondenMovimientoEngineException {
        Connection conn = null;
        try {
            conn = getConnection();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Double nTotal = new Double(req.getParameter("ntotal").replace("$", "").replace(",", ""));
            Integer cidFonden = new Integer(req.getParameter("cidFonden"));
            return FondenMovimientoManager.validateNoGreaterThanImporteAnual(conn, cidFonden, nTotal);
        } catch (Exception e) {
            throw new FondenMovimientoEngineException(e);
        } finally {
        }
    }

    public int saveOrUpdateFondenMovimiento(HttpServletRequest req) throws FondenMovimientoEngineException {
        Connection conn = null;
        try {
            conn = getConnection();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            FondenMovimiento fondenMovimiento = new FondenMovimiento();
            fondenMovimiento.setCconcepto(req.getParameter("cconcepto"));
            fondenMovimiento.setNprecioUnitario(new Double(req.getParameter("nprecioUnitario").replace("$", "").replace(",", "")));
            fondenMovimiento.setNcantidad(new Integer(req.getParameter("ncantidad")));
            fondenMovimiento.setNimporte(new Double(req.getParameter("nimporte").replace("$", "").replace(",", "")));
            fondenMovimiento.setCrfc(req.getParameter("crfc"));
            fondenMovimiento.setCnumPedido(req.getParameter("cnumPedido"));
            fondenMovimiento.setNnetoPedido(new Double(req.getParameter("nnetoPedido").replace("$", "").replace(",", "")));
            fondenMovimiento.setNprecio(new Double(req.getParameter("nprecio").replace("$", "").replace(",", "")));
            fondenMovimiento.setNtotal(new Double(req.getParameter("ntotal").replace("$", "").replace(",", "")));
            fondenMovimiento.setCidFonden(new Integer(req.getParameter("cidFonden")));
            fondenMovimiento.setNidFondenMovimiento(new Integer(req.getParameter("nidFondenMovimiento")));
            fondenMovimiento.setCproveedor(req.getParameter("cproveedor"));
            fondenMovimiento.setActivo(req.getParameter("activo"));
            fondenMovimiento.setIdGabinete(new Integer(req.getParameter("idGabinete")));
            fondenMovimiento.setcFolio(req.getParameter("cFolio"));
            fondenMovimiento.setcCentroContable(req.getParameter("cCentroContable"));
            fondenMovimiento.setcUnidadResponsable(req.getParameter("cUnidadResponsable"));
            fondenMovimiento.setnNumCaso(new Integer(req.getParameter("nNumCaso")));
            fondenMovimiento.setnCantidadTotal(new Integer(req.getParameter("nCantidadTotal")));
            fondenMovimiento.setnTechoDef(new Double(req.getParameter("nTechoDef").replace("$", "").replace(",", "")));
            fondenMovimiento.setnTipoCambio(new Double(req.getParameter("nTipoCambio").replace("$", "").replace(",", "")));
            fondenMovimiento.setcIdMoneda(req.getParameter("cIdMoneda"));
            fondenMovimiento.setnIdFondenMovEstatus(new Integer(req.getParameter("nIdFondenMovEstatus")));
            req.getSession().setAttribute("fondenMovimiento", fondenMovimiento);
            return FondenMovimientoManager.saveOrUpdateFondenMovimiento(conn, fondenMovimiento);
        } catch (Exception e) {
            throw new FondenMovimientoEngineException(e);
        } finally {
        }
    }

    public void getFondenReporteCSV(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws ServletException, IOException {
        try {
            String cRFC = request.getParameter("crfc");
            String cIdFonden = request.getParameter("cIdFonden");
            String restrictions = "cRFC = '" + cRFC + "'";
            restrictions += " AND cIdFonden=" + cIdFonden;
            List<FondenMovimiento> movs = readFondenMovimientoBy(restrictions);
            String layoutGeneral = "fondenReporteGeneral.csv";
            String layoutGeneralZip = "fondenReporteGeneral.zip";
            BufferedWriter out = new BufferedWriter(new FileWriter(layoutGeneral));
            StringBuffer archivoCSV = new StringBuffer();
            for (int i = 0; i < movs.size(); i++) {
                FondenMovimiento fondenMovimiento = movs.get(i);
                archivoCSV.append(fondenMovimiento.getnNumCaso());
                archivoCSV.append(",");
                archivoCSV.append(fondenMovimiento.getNprecioUnitario());
                archivoCSV.append(",");
                archivoCSV.append(fondenMovimiento.getNcantidad());
                archivoCSV.append(",");
                archivoCSV.append(fondenMovimiento.getNimporte());
                archivoCSV.append(",");
                archivoCSV.append(fondenMovimiento.getCproveedor());
                archivoCSV.append(",");
                archivoCSV.append(fondenMovimiento.getCnumPedido());
                archivoCSV.append(",");
                archivoCSV.append(fondenMovimiento.getNnetoPedido());
                archivoCSV.append(",");
                archivoCSV.append(fondenMovimiento.getNprecio());
                archivoCSV.append(",");
                archivoCSV.append(fondenMovimiento.getnCantidadTotal());
                archivoCSV.append(",");
                archivoCSV.append(fondenMovimiento.getNtotal());
                archivoCSV.append(",");
                //				archivoCSV.append(fondenMovimiento.getCconcepto());
                //				archivoCSV.append(",");
                //				archivoCSV.append(fondenMovimiento.getCrfc());
                //				archivoCSV.append(",");
                //				archivoCSV.append(fondenMovimiento.getActivo());
                //				archivoCSV.append(",");
                //				archivoCSV.append(fondenMovimiento.getIdGabinete());
                //				archivoCSV.append(",");
                //				archivoCSV.append(fondenMovimiento.getcFolio());
                //				archivoCSV.append(",");
                //				archivoCSV.append(fondenMovimiento.getcCentroContable());
                //				archivoCSV.append(",");
                //				archivoCSV.append(fondenMovimiento.getcUnidadResponsable());
                //				archivoCSV.append(",");
                //				archivoCSV.append(fondenMovimiento.getnTechoDef());
                //				archivoCSV.append(",");
                //				archivoCSV.append(fondenMovimiento.getnTipoCambio());
                //				archivoCSV.append(",");
                //				archivoCSV.append(fondenMovimiento.getcIdMoneda());
                //				archivoCSV.append(",");
                //				archivoCSV.append(fondenMovimiento.getnIdFondenMovEstatus());
                archivoCSV.append("\n");
            }
            String outTextPago = archivoCSV.toString();
            out.write(outTextPago);
            out.close();
            byte[] bufPag = new byte[2048];
            try {
                ZipOutputStream outPag = new ZipOutputStream(new FileOutputStream(layoutGeneralZip));
                FileInputStream inPag = new FileInputStream(layoutGeneral);
                outPag.putNextEntry(new ZipEntry(layoutGeneral));
                int lenPag;
                while ((lenPag = inPag.read(bufPag)) > 0) {
                    outPag.write(bufPag, 0, lenPag);
                }
                outPag.closeEntry();
                inPag.close();
                outPag.close();
            } catch (IOException e) {
            }
            doDownload(response, layoutGeneral, layoutGeneral, servletContext);
            File ficheroPag = new File(layoutGeneral);
            ficheroPag.delete();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doDownload(HttpServletResponse resp, String filename, String original_filename, ServletContext servletContext) throws IOException {
        int length = 0;
        File f = new File(filename);
        ServletOutputStream out = resp.getOutputStream();
        ServletContext context = servletContext;
        String mimetype = context.getMimeType(original_filename);
        resp.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
        resp.setContentLength((int) f.length());
        //resp.addHeader("Content-Disposition", "attachment; filename=\"" + original_filename + "\";");
        //resp.addHeader("Content-Disposition", "attachement; filename=\"" + original_filename + "\";");
        resp.addHeader("Content-Disposition", "inline; filename=\"" + original_filename + "\";");
        // 5K buffer
        byte[] bbuf = new byte[5 * 1024];
        DataInputStream in = new DataInputStream(new FileInputStream(f));
        while ((in != null) && ((length = in.read(bbuf)) != -1)) {
            out.write(bbuf, 0, length);
        }
        in.close();
        out.flush();
        out.close();
    }
}
