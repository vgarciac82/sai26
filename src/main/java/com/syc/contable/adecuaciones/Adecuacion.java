package com.syc.contable.adecuaciones;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Almacena informacion de adecuacion.
 *
 * @author Vicente Garcia C.
 * @version 1.0
 */
public class Adecuacion implements Serializable {

    private static Logger log = LoggerFactory.getLogger(Adecuacion.class);

    private static final long serialVersionUID = -8582425949198119780L;

    private List<AdecuacionDetalle> detalle;

    private AdecuacionEncabezado encabezado;

    private double totalAmpliaciones;

    private double totalReducciones;

    private double diferencia;

    public double getTotalAmpliaciones() {
        return totalAmpliaciones;
    }

    public void setTotalAmpliaciones(double totalAmpliaciones) {
        this.totalAmpliaciones = totalAmpliaciones;
    }

    public double getTotalReducciones() {
        return totalReducciones;
    }

    public void setTotalReducciones(double totalReducciones) {
        this.totalReducciones = totalReducciones;
    }

    public double getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(double diferencia) {
        this.diferencia = diferencia;
    }

    /**
     * Instancia un nuevo objeto adecuacion leyendo la informacion desde el
     * archivo excel
     *
     * @param archivo
     *            Ruta hacia el archivo
     * @param c
     *            Caso
     * @param usuario
     *            Usuario de carga
     * @param cSuperReduccion
     *            Indica si es super reduccion
     * @param cSRInterna
     *            Indica si es interna
     * @return Objeto con la informacion leida desde el archivo excel.
     * @throws Exception
     *             Si no se cuenta con la informacion correcta o si ocurre un
     *             error de E/S.
     */
    public static Adecuacion instanceFromExcel(int aEjercicio, String archivo, Caso c, Usuario usuario, String cSuperReduccion, String cSRInterna) throws Exception {
        File f = null;
        FileInputStream stream = null;
        HSSFWorkbook workbook = null;
        HSSFSheet sheet = null;
        String cCentroContable = null;
        try {
            f = new File(archivo);
            if (!f.exists())
                throw new FileNotFoundException("No se encontro el archivo de carga[ " + archivo + "]");
            if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE"))
                cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
            else
                throw new Exception("No se localiza centro contable en el usuario. Reporte al administrador");
            /* Abre el flujo hacia el archivo excel */
            stream = new FileInputStream(f);
            workbook = new HSSFWorkbook(stream);
            sheet = workbook.getSheetAt(0);
            /* Lectura de variables que llegan desde la JSP */
            int nFolio = Integer.parseInt(c.getFolio().substring(9));
            boolean superReduccion = "SI".equalsIgnoreCase(cSuperReduccion);
            boolean SRInterna = "SI".equalsIgnoreCase(cSRInterna);
            // Instancia un objeto Adecuacion desde un Excel
            Adecuacion adecuacion = Adecuacion.instanceFromExcel(aEjercicio, workbook, sheet, usuario.getLogin(), nFolio, superReduccion, SRInterna, 1, 5);
            adecuacion.getEncabezado().setId_caso(c.getIdCaso());
            adecuacion.getEncabezado().setCentroContable(cCentroContable);
            return adecuacion;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (stream != null)
                try {
                    stream.close();
                } catch (Exception e2) {
                    log.warn("Object: {}", "No se pudo cerrar el flujo hacia el archivo de adecuacion." + e2.toString());
                } finally {
                    stream = null;
                }
            throw e;
        } finally {
            workbook = null;
            sheet = null;
        }
    }

    /**
     * *********************************************************************************************************************************************************************************************
     */
    /**
     * Obtiene Adecuaciones
     *
     * @param nFolioAdecuacion
     *            Ruta hacia el archivo
     * @param conn
     *            Caso
     *
     * @return Objeto con la informacion leida desde el archivo excel.
     * @throws SQLException
     * @throws Exception
     *             Si no se cuenta con la informacion correcta o si ocurre un
     *             error de E/S.
     */
    public static AdecuacionEncabezado getAdeacuacionEncabezado(String nFolioAdecuacion, Connection conn) throws Exception {
        AdecuacionEncabezado AdeEn = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            String sSQL = "select aEjercicioFiscal,cUnidadresponsable,cRamo,cJustificacion from tadecuacionencabezado where nFolioAdecuacion=" + nFolioAdecuacion;
            pstmnt = conn.prepareStatement(sSQL);
            rs = pstmnt.executeQuery();
            AdeEn = new AdecuacionEncabezado();
            if (rs.next()) {
                AdeEn.setEjercicioFiscal(rs.getInt("aEjercicioFiscal"));
                AdeEn.setUnidadEjecutora(rs.getString("cUnidadresponsable"));
                AdeEn.setRamo(rs.getString("cRamo"));
                AdeEn.setJustificacion(rs.getString("cJustificacion"));
            } else {
                System.out.println("Error al obtener el encabezado de una adecuación");
                throw new Exception("Error al obtener el encabezado de una adecuación");
            }
            sSQL = "select " + "SUM( case when substring(cevento, 1,1) = 'R' THEN mImporte ELSE 0  END)  as total_reducciones " + ",SUM( case when substring(cevento, 1,1) = 'A' THEN mImporte ELSE 0  END)  as total_ampliaciones " + "from tAdecuacionDetalle " + " where nFolioAdecuacion =" + nFolioAdecuacion;
            pstmnt = conn.prepareStatement(sSQL);
            double total_reducciones = 0.00;
            double total_ampliaciones = 0.00;
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                total_reducciones = rs.getDouble("total_reducciones");
                total_ampliaciones = rs.getDouble("total_ampliaciones");
                if (total_reducciones > 0 && total_ampliaciones > 0)
                    AdeEn.setMontoTotal(total_reducciones);
                else
                    AdeEn.setMontoTotal(total_ampliaciones);
            } else {
                System.out.println("Error al obtener el detalle de una adecuación");
                throw new Exception("Error al obtener el detalle de una adecuación");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            CloseObject.closeObject(pstmnt, false);
            CloseObject.closeObject(rs, false);
        }
        return AdeEn;
    }

    /**
     * *********************************************************************************************************************************************************************************************
     */
    /**
     * Instancia un nuevo objeto adecuacion leyendo la informacion desde un
     * objeto excel.
     *
     * @param wb
     *            Excel
     * @param hoja
     *            Hoja activa
     * @param nFolio
     *            Folio de la adecuacion
     * @param nRenglonEncabezado
     *            renglon a partir del cual inicia el encabezado
     * @param nRenglonDetalle
     *            renglon a partir del cual inicia el detalle
     * @return Objeto con la informacion leida desde el archivo excel.
     * @throws Exception
     *             Si no se cuenta con la informacion correcta o si ocurre un
     *             error de E/S.
     */
    public static Adecuacion instanceFromExcel(int aEjercicio, Workbook wb, HSSFSheet hoja, String usuario, int nFolio, boolean superReduccion, boolean SRInterna, int nRenglonEncabezado, int nRenglonCuerpo) throws Exception {
        AdecuacionEncabezado encabezado = null;
        List<AdecuacionDetalle> detalle = null;
        String mensajes = "";
        try {
            log.debug("Iniciando carga de adecuacion desde Excel.");
            try {
                encabezado = AdecuacionEncabezadoManager.readFromExcel(aEjercicio, wb, hoja, usuario, nFolio, superReduccion, SRInterna, nRenglonEncabezado);
            } catch (Exception e) {
                mensajes += e.toString();
            }
            try {
                detalle = AdecuacionDetalleManager.readFromExcel(wb, hoja, usuario, nFolio, superReduccion, SRInterna, nRenglonCuerpo);
            } catch (Exception e) {
                mensajes += e.toString();
            }
            if (!"".equals(mensajes))
                throw new Exception(mensajes);
            Adecuacion adecuacion = new Adecuacion();
            adecuacion.setEncabezado(encabezado);
            adecuacion.setDetalle(detalle);
            return adecuacion;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Construye una nueva instancia del objeto con el encabezado y el detalle
     * vacios (nulos).
     */
    public Adecuacion() {
        super();
    }

    /**
     * Construye una nueva instancia del objeto estableciendo el encabezado y el
     * detalle.
     *
     * @param encabezado
     * @param detalle
     */
    public Adecuacion(AdecuacionEncabezado encabezado, List<AdecuacionDetalle> detalle) {
        super();
        this.encabezado = encabezado;
        this.detalle = detalle;
        calculaMontos();
    }

    /**
     * @return the detalle
     */
    public List<AdecuacionDetalle> getDetalle() {
        return detalle;
    }

    /**
     * @return the encabezado
     */
    public AdecuacionEncabezado getEncabezado() {
        return encabezado;
    }

    /**
     * @param detalle
     *            the detalle to set
     */
    public void setDetalle(List<AdecuacionDetalle> detalle) {
        this.detalle = detalle;
        calculaMontos();
    }

    /**
     * @param encabezado
     *            the encabezado to set
     */
    public void setEncabezado(AdecuacionEncabezado encabezado) {
        this.encabezado = encabezado;
    }

    private void calculaMontos() {
        double totalRed = 0.0d;
        double totalAmp = 0.0d;
        List<AdecuacionDetalle> detalleSumar = getDetalle();
        if (detalleSumar != null)
            for (Iterator<AdecuacionDetalle> it = detalleSumar.iterator(); it.hasNext(); ) {
                AdecuacionDetalle renglon = it.next();
                if (renglon.getMontos() != null && "A".equalsIgnoreCase(renglon.getTipo()))
                    totalAmp += renglon.getMontos().get(0);
                else if (renglon.getMontos() != null && "R".equalsIgnoreCase(renglon.getTipo()))
                    totalRed += renglon.getMontos().get(0);
            }
        setTotalAmpliaciones(totalAmp);
        setTotalReducciones(totalRed);
        setDiferencia(totalAmp - totalRed);
    }
}
