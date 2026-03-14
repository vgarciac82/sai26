package com.axtel.egresos.compromiso;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import com.axtel.contratos.core.ConvenioColaboracion;
import com.axtel.contratos.core.ConvenioColaboracionDetalle;
import java.util.Base64;

public class CompromisoDetalle {

    private String cCentroContable;

    private String cEvento;

    private int cMes;

    private String EP;

    private BigDecimal mImporte;

    private BigDecimal mImporteNegativo;

    private int nDocRenglon;

    private int nFolioCompromiso;

    /**
     * @return the cCentroContable
     */
    public String getcCentroContable() {
        return cCentroContable;
    }

    /**
     * @return the cEvento
     */
    public String getcEvento() {
        return cEvento;
    }

    /**
     * @return the cMes
     */
    public int getcMes() {
        return cMes;
    }

    /**
     * @return the eP
     */
    public String getEP() {
        return EP;
    }

    /**
     * @return the mImporte
     */
    public BigDecimal getmImporte() {
        return mImporte;
    }

    /**
     * @return the mImporteNegativo
     */
    public BigDecimal getmImporteNegativo() {
        return mImporteNegativo;
    }

    /**
     * @return the nDocRenglon
     */
    public int getnDocRenglon() {
        return nDocRenglon;
    }

    /**
     * @return the nFolioCompromiso
     */
    public int getnFolioCompromiso() {
        return nFolioCompromiso;
    }

    /**
     * @param cCentroContable
     *            the cCentroContable to set
     */
    public void setcCentroContable(String cCentroContable) {
        this.cCentroContable = cCentroContable;
    }

    /**
     * @param cEvento
     *            the cEvento to set
     */
    public void setcEvento(String cEvento) {
        this.cEvento = cEvento;
    }

    /**
     * @param cMes
     *            the cMes to set
     */
    public void setcMes(int cMes) {
        this.cMes = cMes;
    }

    /**
     * @param eP
     *            the eP to set
     */
    public void setEP(String eP) {
        EP = eP;
    }

    /**
     * @param mImporte
     *            the mImporte to set
     */
    public void setmImporte(BigDecimal mImporte) {
        this.mImporte = mImporte;
    }

    /**
     * @param mImporteNegativo
     *            the mImporteNegativo to set
     */
    public void setmImporteNegativo(BigDecimal mImporteNegativo) {
        this.mImporteNegativo = mImporteNegativo;
    }

    /**
     * @param nDocRenglon
     *            the nDocRenglon to set
     */
    public void setnDocRenglon(int nDocRenglon) {
        this.nDocRenglon = nDocRenglon;
    }

    /**
     * @param nFolioCompromiso
     *            the nFolioCompromiso to set
     */
    public void setnFolioCompromiso(int nFolioCompromiso) {
        this.nFolioCompromiso = nFolioCompromiso;
    }

    @Override
    public String toString() {
        return "CompromisoDetalle [cCentroContable=" + cCentroContable + ", cEvento=" + cEvento + ", cMes=" + cMes + ", EP=" + EP + ", mImporte=" + mImporte + ", mImporteNegativo=" + mImporteNegativo + ", nDocRenglon=" + nDocRenglon + ", nFolioCompromiso=" + nFolioCompromiso + "]";
    }

    public static List<CompromisoDetalle> instanceFrom(Connection conn, ConvenioColaboracion convenio) {
        List<CompromisoDetalle> detalle = new ArrayList<>();
        int nrenglon = 1;
        for (ConvenioColaboracionDetalle renglon : convenio.getDetalle()) {
            CompromisoDetalle cDetalle = new CompromisoDetalle();
            cDetalle.setcCentroContable(convenio.getEncabezado().getCentroContable());
            cDetalle.setcEvento("CMP002");
            cDetalle.setcMes(renglon.getMes());
            cDetalle.setEP(renglon.getEp());
            cDetalle.setmImporte(renglon.getImporte());
            cDetalle.setmImporteNegativo(renglon.getImporte().multiply(new BigDecimal(-1.0)));
            cDetalle.setnDocRenglon(nrenglon++);
            detalle.add(cDetalle);
        }
        return detalle;
    }
}
