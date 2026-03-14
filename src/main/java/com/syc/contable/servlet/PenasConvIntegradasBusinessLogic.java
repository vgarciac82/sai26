package com.syc.contable.servlet;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import com.syc.contable.core.PenasConvIntDetalle;
import com.syc.contable.core.PenasConvIntEncabezado;
import com.syc.contable.core.PenasConvIntegradasManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PenasConvIntegradasBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(PenasConvIntegradasBusinessLogic.class);

    private PenasConvIntEncabezado encabezado;

    private List<PenasConvIntDetalle> detalle;

    public PenasConvIntegradasBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public static PenasConvIntEncabezado instanceHeaderFromRequest(HttpServletRequest req) throws Exception {
        PenasConvIntEncabezado encabezado = new PenasConvIntEncabezado();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        encabezado.setaEjercicioFiscal(req.getParameter("cEjercicio"));
        encabezado.setCaNoContrarrecibo(req.getParameter("caNoContrarreciboInt"));
        encabezado.setcBeneficiario(req.getParameter("cBeneficiario"));
        encabezado.setcCentroContable(req.getParameter("cCentroContable"));
        encabezado.setcIDRFC(null);
        //encabezado.setcIdTipoDocumento(null);
        encabezado.setcRamo(req.getParameter("cRamo"));
        encabezado.setcUnidadResponsable(req.getParameter("cUnidadResponsable"));
        encabezado.setfCancelacion(null);
        encabezado.setfCaptura(formatter.parse(req.getParameter("fCaptura")));
        encabezado.setfDesde(formatter.parse(req.getParameter("fBusquedaDe")));
        encabezado.setFHasta(formatter.parse(req.getParameter("fBusquedaHasta")));
        encabezado.setmImportes(Double.parseDouble(req.getParameter("mImporteconAjuste")));
        encabezado.setnEnviadoSICOP("1");
        encabezado.setnFolioPagoPenasConvInt(Integer.parseInt(req.getParameter("nFolioPagoPenasConvInt"), 10));
        encabezado.setU_LOGIN(req.getParameter("U_LOGIN"));
        encabezado.setcIDRFC(req.getParameter("cIDRFC"));
        return encabezado;
    }

    public static List<PenasConvIntDetalle> instanceDetailFromRequest(HttpServletRequest req) throws Exception {
        List<PenasConvIntDetalle> detalle = new ArrayList<PenasConvIntDetalle>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String[] eps = req.getParameterValues("Ep");
        String[] caNoContrarreciboOA = req.getParameterValues("caNoContrarreciboPP");
        String[] nFolioOperAjenas = req.getParameterValues("nFolioPagoPenasConv");
        String[] caNoContrarrecibo = req.getParameterValues("caNoContrarrecibo");
        String[] cTipoDoc = req.getParameterValues("cTipoDoc");
        String[] mTotal = req.getParameterValues("mImporteMasIva");
        String[] nDocRenglon = req.getParameterValues("nDocRenglon");
        String[] cIDRFC = req.getParameterValues("cIDRFC");
        String[] nFolioDoc = req.getParameterValues("nFolioDoc");
        String[] nDocRenglonInt = req.getParameterValues("nDocRenglonInt");
        String[] mAjuste = req.getParameterValues("mAjuste");
        String[] cMes = req.getParameterValues("cMes");
        for (int i = 0; i < eps.length; i++) {
            PenasConvIntDetalle tmpDet = new PenasConvIntDetalle();
            tmpDet.setaEjercicioFiscal(req.getParameter("cEjercicio"));
            tmpDet.setCaNoContrarrecibo(caNoContrarrecibo[i]);
            tmpDet.setcaNoContrarreciboPP(caNoContrarreciboOA[i]);
            tmpDet.setnFolioDoc(Integer.parseInt(nFolioDoc[i], 10));
            tmpDet.setcCentroContable(req.getParameter("cCentroContable"));
            tmpDet.setcIDRFC(cIDRFC[i]);
            tmpDet.setcRamo(req.getParameter("cRamo"));
            tmpDet.setcTipoDoc(cTipoDoc[i]);
            tmpDet.setcUnidadResponsable(req.getParameter("cUnidadResponsable"));
            tmpDet.setEp(eps[i]);
            tmpDet.setfCaptura(formatter.parse(req.getParameter("fCaptura")));
            tmpDet.setmImporteMasIva(Double.parseDouble(mTotal[i]));
            tmpDet.setnDocRenglon(Integer.parseInt(nDocRenglon[i], 10));
            tmpDet.setnDocRenglonInt(Integer.parseInt(nDocRenglonInt[i], 10));
            tmpDet.setnFolioDoc(Integer.parseInt(nFolioDoc[i], 10));
            tmpDet.setnFolioPagoPenasConv(Integer.parseInt(nFolioOperAjenas[i], 10));
            tmpDet.setnFolioPagoPenasConvInt(Integer.parseInt(req.getParameter("nFolioPagoPenasConvInt"), 10));
            tmpDet.setmAjuste(Double.parseDouble(mAjuste[i]));
            tmpDet.setcMes(cMes[i]);
            detalle.add(tmpDet);
        }
        return detalle;
    }

    public void setEncabezado(PenasConvIntEncabezado encabezado) {
        this.encabezado = encabezado;
    }

    public PenasConvIntEncabezado getEncabezado() {
        return encabezado;
    }

    public List<PenasConvIntDetalle> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<PenasConvIntDetalle> detalle) {
        this.detalle = detalle;
    }

    public int insert() throws Exception {
        Connection conn = null;
        int insertados = 0;
        try {
            conn = getConnection();
            insertados = PenasConvIntegradasManager.insertEncabezado(conn, getEncabezado());
            insertados += PenasConvIntegradasManager.insertDetalle(conn, getDetalle());
            conn.commit();
            return insertados;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("No se pudo realizar rollback. Causa: " + e2, e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public ArrayList<String> buscaPenasConvIntegradas(String cxpAI, String fecha, String cBancaria, String Leyenda, String nFolio, String cBEN) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaComp = PenasConvIntegradasManager.buscaPenasConvIntegradas(conn, cxpAI, fecha, cBancaria, Leyenda, nFolio, cBEN);
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
                //throw  new GestionException(e.getMessage());
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return arrListaComp;
    }
}
