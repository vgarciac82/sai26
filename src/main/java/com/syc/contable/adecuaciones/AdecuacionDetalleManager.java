package com.syc.contable.adecuaciones;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import com.axtel.presupuesto.AdecuacionDetalleResumen;
import com.axtel.presupuesto.catalogo.ProgramaPresupuestario;
import com.syc.contable.adecuaciones.Exception.IncompletRowException;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdecuacionDetalleManager {

    private static final Logger log = LoggerFactory.getLogger(AdecuacionDetalleManager.class);

    private static final String[] MESES_ADECUACION = { "Anual", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" };

    private static final StringBuilder DETALLE_SQL = new StringBuilder();

    static {
        DETALLE_SQL.append("SELECT nfolioadecuacion, ");
        DETALLE_SQL.append("       SUBSTRING(ep,1,55) as ep, ");
        DETALLE_SQL.append("       SUBSTRING(ep, 57, 64) AS claveInterna, ");
        DETALLE_SQL.append("       cevento, ");
        DETALLE_SQL.append("       Sum(mimporte) AS anual, ");
        DETALLE_SQL.append("       Sum(CASE ");
        DETALLE_SQL.append("             WHEN cmes = 1 THEN mimporte ");
        DETALLE_SQL.append("             ELSE 0 ");
        DETALLE_SQL.append("           END)      AS enero, ");
        DETALLE_SQL.append("       Sum(CASE ");
        DETALLE_SQL.append("             WHEN cmes = 2 THEN mimporte ");
        DETALLE_SQL.append("             ELSE 0 ");
        DETALLE_SQL.append("           END)      AS febrero, ");
        DETALLE_SQL.append("       Sum(CASE ");
        DETALLE_SQL.append("             WHEN cmes = 3 THEN mimporte ");
        DETALLE_SQL.append("             ELSE 0 ");
        DETALLE_SQL.append("           END)      AS marzo, ");
        DETALLE_SQL.append("       Sum(CASE ");
        DETALLE_SQL.append("             WHEN cmes = 4 THEN mimporte ");
        DETALLE_SQL.append("             ELSE 0 ");
        DETALLE_SQL.append("           END)      AS abril, ");
        DETALLE_SQL.append("       Sum(CASE ");
        DETALLE_SQL.append("             WHEN cmes = 5 THEN mimporte ");
        DETALLE_SQL.append("             ELSE 0 ");
        DETALLE_SQL.append("           END)      AS mayo, ");
        DETALLE_SQL.append("       Sum(CASE ");
        DETALLE_SQL.append("             WHEN cmes = 6 THEN mimporte ");
        DETALLE_SQL.append("             ELSE 0 ");
        DETALLE_SQL.append("           END)      AS junio, ");
        DETALLE_SQL.append("       Sum(CASE ");
        DETALLE_SQL.append("             WHEN cmes = 7 THEN mimporte ");
        DETALLE_SQL.append("             ELSE 0 ");
        DETALLE_SQL.append("           END)      AS julio, ");
        DETALLE_SQL.append("       Sum(CASE ");
        DETALLE_SQL.append("             WHEN cmes = 8 THEN mimporte ");
        DETALLE_SQL.append("             ELSE 0 ");
        DETALLE_SQL.append("           END)      AS agosto, ");
        DETALLE_SQL.append("       Sum(CASE ");
        DETALLE_SQL.append("             WHEN cmes = 9 THEN mimporte ");
        DETALLE_SQL.append("             ELSE 0 ");
        DETALLE_SQL.append("           END)      AS septiembre, ");
        DETALLE_SQL.append("       Sum(CASE ");
        DETALLE_SQL.append("             WHEN cmes = 10 THEN mimporte ");
        DETALLE_SQL.append("             ELSE 0 ");
        DETALLE_SQL.append("           END)      AS octubre, ");
        DETALLE_SQL.append("       Sum(CASE ");
        DETALLE_SQL.append("             WHEN cmes = 11 THEN mimporte ");
        DETALLE_SQL.append("             ELSE 0 ");
        DETALLE_SQL.append("           END)      AS noviembre, ");
        DETALLE_SQL.append("       Sum(CASE ");
        DETALLE_SQL.append("             WHEN cmes = 12 THEN mimporte ");
        DETALLE_SQL.append("             ELSE 0 ");
        DETALLE_SQL.append("           END)      AS diciembre ");
        DETALLE_SQL.append("FROM   tadecuaciondetalle WITH(NOLOCK) ");
    }

    /**
     * Calcula el evento que le corresponde al detalle. Se simplifico a: Cuando
     * es reduccion se llama a RED001. En caso de ampliacion se marca como
     * AMP001. Otro tipo no se puede manejar
     *
     * @param detalle
     *            Renglon de la adecuacion
     * @return Evento correspondiente al movimiento.
     * @throws Exception
     *             Si el movimiento no corresponde a un evento.
     */
    public static String calculaNombreEvento(AdecuacionDetalle detalle) throws Exception {
        String evto = null;
        if ("R".equalsIgnoreCase(detalle.getTipo())) {
            evto = "RED001";
        } else if ("A".equalsIgnoreCase(detalle.getTipo())) {
            evto = "AMP001";
        } else {
            throw new Exception("No existe evento para el movimiento tipo [" + detalle.getTipo() + "]");
        }
        return evto;
    }

    /**
     * Borra todo el detalle de una adecuacion.
     *
     * @param conn
     *            COnexion activa a la base de datos
     * @param nFolioAdecuacion
     *            Folio de la adecuacion.
     * @return Numero de registros eliminados.
     * @throws Exception
     */
    public static int deleteAdecuacionDetalle(Connection conn, int nFolioAdecuacion) throws Exception {
        String query = "DELETE FROM tAdecuacionDetalle WHERE nFolioAdecuacion = ?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, nFolioAdecuacion);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    /**
     * Inserta renglones del detalle de la adecuacion,.
     *
     * @param conn
     *            Conexion activa a la base de datos.
     * @param detalle
     *            Detalle de la adecuacion
     * @param nFolioAdecuacion
     *            Folio de la adecuacion
     * @param centroContable
     *            centro contable
     * @return Numero de renglones insertados
     * @throws Exception
     */
    public static int insertaAdecuacionDetalle(Connection conn, List<AdecuacionDetalle> detalle, int nFolioAdecuacion, String centroContable) throws Exception {
        PreparedStatement pstmnt = null;
        String queryInsert = "INSERT INTO tAdecuacionDetalle(nDocRenglon,EP,cEvento,mImporte,nFolioAdecuacion, cMes,mImporteNegativo, cCentroContable) VALUES (?,?,?,?,?,?,?,?)";
        int nDocRenglon = 0;
        log.info("Insertando detalle de la adecuacion ");
        try {
            pstmnt = conn.prepareStatement(queryInsert);
            /* Iteracion de los renglones de la adecuacion */
            for (Iterator<AdecuacionDetalle> i = detalle.iterator(); i.hasNext(); ) {
                AdecuacionDetalle detalleRenglon = i.next();
                int mes = 0;
                /* Iteracion de los meses en el renglon */
                for (Iterator<Double> j = detalleRenglon.getMontos().iterator(); j.hasNext(); ) {
                    Double monto = j.next();
                    /* El elemento 0 en el arreglo siempre es el anual */
                    if (monto != null && monto > 0 && mes > 0) {
                        nDocRenglon++;
                        pstmnt.setInt(1, nDocRenglon);
                        pstmnt.setString(2, detalleRenglon.getClaveSIAFF().substring(0, 55) + "." + detalleRenglon.getClaveInterna().substring(0, 7));
                        pstmnt.setString(3, AdecuacionDetalleManager.calculaNombreEvento(detalleRenglon));
                        pstmnt.setDouble(4, monto.doubleValue());
                        pstmnt.setDouble(5, nFolioAdecuacion);
                        pstmnt.setDouble(6, mes);
                        pstmnt.setDouble(7, monto.doubleValue() * -1);
                        pstmnt.setString(8, centroContable);
                        pstmnt.addBatch();
                    }
                    mes++;
                }
            }
            pstmnt.executeBatch();
            log.info("Se insertaron [" + nDocRenglon + "] renglondes de la adecuacion [" + nFolioAdecuacion + "]");
            return nDocRenglon;
        } finally {
            CloseObject.closeObject(pstmnt, false);
        }
    }

    /**
     * @param conn
     * @param folio
     * @return
     * @throws Exception
     */
    public static int insertaEPCatalogo(Connection conn, String folio) throws Exception {
        CallableStatement cstmt = null;
        ResultSet rs = null;
        int cont = 0;
        try {
            cstmt = conn.prepareCall("{call sp_insertaEPNuevas( ? ) }");
            cstmt.setString(1, folio);
            rs = cstmt.executeQuery();
            while (rs.next()) {
                cont = (rs.getInt(1));
            }
            return cont;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cstmt, false);
        }
    }

    /**
     * Devuelve el detalle de una adecuacion, siguiendo la estructura del excel
     * de carga pero leido de la base de datos.
     *
     * @param conn
     *            Conexion activa a la base de datos.
     * @param nFolioAdecuacion
     *            FOlio de la adecuacion a cargar
     * @return Detalle de la adecuacion.
     * @throws Exception
     */
    public static List<AdecuacionDetalle> readAdecuacionDetalle(Connection conn, int nFolioAdecuacion) throws Exception {
        StringBuilder query = new StringBuilder(DETALLE_SQL);
        query.append("WHERE  nfolioadecuacion = ? ");
        query.append("GROUP  BY nfolioadecuacion, ");
        query.append("          SUBSTRING(ep,1,55),  SUBSTRING(ep, 57, 64), ");
        query.append("          cevento ");
        query.append("ORDER  BY cevento DESC, ");
        query.append("          SUBSTRING(ep,1,55) ASC ");
        return readAdecuacionDetalle(conn, query.toString(), nFolioAdecuacion);
    }

    private static List<AdecuacionDetalle> readAdecuacionDetalle(Connection conn, String query, int nFolioAdecuacion) throws Exception {
        log.trace("Se ejecutara: " + query);
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<AdecuacionDetalle> detalle = new ArrayList<AdecuacionDetalle>();
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, nFolioAdecuacion);
            rs = ps.executeQuery();
            int secuencia = 1;
            while (rs.next()) {
                AdecuacionDetalle detalleRenglon = new AdecuacionDetalle();
                detalleRenglon.setnFolioAdecuacion(rs.getInt("nFolioAdecuacion"));
                detalleRenglon.setEP(rs.getString("ep"));
                detalleRenglon.setClaveSIAFF(rs.getString("ep"));
                detalleRenglon.setcEvento(rs.getString("cevento"));
                detalleRenglon.setClaveInterna(rs.getString("claveInterna"));
                List<Double> montos = new ArrayList<Double>();
                for (int i = 0; i < Util.NOMBRE_MESES_ADECUACIONES.length; i++) montos.add(rs.getDouble(Util.NOMBRE_MESES_ADECUACIONES[i]));
                detalleRenglon.setMontos(montos);
                detalleRenglon.setSecuencia(secuencia);
                detalleRenglon.setTipo(detalleRenglon.getcEvento() == null ? "" : detalleRenglon.getcEvento().substring(0, 1));
                secuencia++;
                detalle.add(detalleRenglon);
            }
            return detalle;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    /**
     * Devuelve el detalle de una adecuacion, incluyendo el proyecto, siguiendo
     * la estructura del excel de carga pero leido de la base de datos.
     *
     * @param conn
     *            Conexion activa a la base de datos.
     * @param nFolioAdecuacion
     *            FOlio de la adecuacion a cargar
     * @return Detalle de la adecuacion.
     * @throws Exception
     */
    public static List<AdecuacionDetalle> readAdecuacionDetalleProyecto(Connection conn, int nFolioAdecuacion) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT nfolioadecuacion, " + "       SUBSTRING( dbo.CambiaEPPlurianual(EP), 0,56) AS ep, " + "       cevento, " + "       Sum(mimporte)                                                     AS " + "       anual, " + "       Sum(CASE " + "             WHEN cmes = 1 THEN mimporte " + "             ELSE 0 " + "           END)                                                          AS " + "       enero, " + "       Sum(CASE " + "             WHEN cmes = 2 THEN mimporte " + "             ELSE 0 " + "           END)                                                          AS " + "       febrero, " + "       Sum(CASE " + "             WHEN cmes = 3 THEN mimporte " + "             ELSE 0 " + "           END)                                                          AS " + "       marzo, " + "       Sum(CASE " + "             WHEN cmes = 4 THEN mimporte " + "             ELSE 0 " + "           END)                                                          AS " + "       abril, " + "       Sum(CASE " + "             WHEN cmes = 5 THEN mimporte " + "             ELSE 0 " + "           END)                                                          AS mayo " + "       , " + "       Sum(CASE " + "             WHEN cmes = 6 THEN mimporte " + "             ELSE 0 " + "           END)                                                          AS " + "       junio, " + "       Sum(CASE " + "             WHEN cmes = 7 THEN mimporte " + "             ELSE 0 " + "           END)                                                          AS " + "       julio, " + "       Sum(CASE " + "             WHEN cmes = 8 THEN mimporte " + "             ELSE 0 " + "           END)                                                          AS " + "       agosto, " + "       Sum(CASE " + "             WHEN cmes = 9 THEN mimporte " + "             ELSE 0 " + "           END)                                                          AS " + "       septiembre, " + "       Sum(CASE " + "             WHEN cmes = 10 THEN mimporte " + "             ELSE 0 " + "           END)                                                          AS " + "       octubre, " + "       Sum(CASE " + "             WHEN cmes = 11 THEN mimporte " + "             ELSE 0 " + "           END)                                                          AS " + "       noviembre, " + "       Sum(CASE " + "             WHEN cmes = 12 THEN mimporte " + "             ELSE 0 " + "           END)                                                          AS " + "       diciembre, " + "       Isnull(CONVERT(VARCHAR(32), unidadProyecto.nidproyecto), '00000') AS " + "       proyecto " + "FROM   tadecuaciondetalle detalle WITH(nolock) " + "       LEFT OUTER JOIN tunidadproyecto unidadProyecto " + "                    ON Substring(detalle.ep, 61, 3) = " + "                       unidadProyecto.cunidadresponsable " + "WHERE  nfolioadecuacion = ? " + "GROUP  BY nfolioadecuacion, " + "          SUBSTRING( dbo.CambiaEPPlurianual(EP), 0,56), " + "          cevento, " + "          unidadProyecto.nidproyecto " + "ORDER  BY cevento DESC, " + "          SUBSTRING( dbo.CambiaEPPlurianual(EP), 0,56) ASC   ";
        List<AdecuacionDetalle> detalle = new ArrayList<AdecuacionDetalle>();
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, nFolioAdecuacion);
            rs = ps.executeQuery();
            int secuencia = 1;
            while (rs.next()) {
                AdecuacionDetalle detalleRenglon = new AdecuacionDetalle();
                detalleRenglon.setnFolioAdecuacion(rs.getInt("nFolioAdecuacion"));
                detalleRenglon.setEP(rs.getString("ep"));
                detalleRenglon.setcEvento(rs.getString("cevento"));
                detalleRenglon.setProyecto(rs.getString("proyecto"));
                List<Double> montos = new ArrayList<Double>();
                for (int i = 0; i < Util.NOMBRE_MESES_ADECUACIONES.length; i++) montos.add(rs.getDouble(Util.NOMBRE_MESES_ADECUACIONES[i]));
                detalleRenglon.setMontos(montos);
                detalleRenglon.setSecuencia(secuencia);
                detalleRenglon.setTipo(detalleRenglon.getcEvento() == null ? "" : detalleRenglon.getcEvento().substring(0, 1));
                secuencia++;
                detalle.add(detalleRenglon);
            }
            return detalle;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static List<AdecuacionDetalle> readFromExcel(Workbook wb, HSSFSheet hoja, String usuario, int nFolio, boolean superReduccion, boolean SRInterna, int nRenglonCuerpo) throws Exception {
        log.info(" Iniciando carga de archivo excel de adecuaciones para su validacion Folio[" + nFolio + "]");
        List<AdecuacionDetalle> detalle = new ArrayList<AdecuacionDetalle>();
        String mensajes = "";
        int contadorRenglones = -1;
        int secuencia = 0;
        for (Iterator<Row> i = hoja.iterator(); i.hasNext(); ) {
            try {
                contadorRenglones++;
                Row renglon = i.next();
                log.trace("Procesando renglon " + contadorRenglones + " del archivo excel");
                if (contadorRenglones >= nRenglonCuerpo) {
                    if (Util.renglonVacio(renglon))
                        continue;
                    String cveSIAFF = ValidacionAdecuacionesManager.validaColumnaCadenaNoVacia(renglon, 1);
                    String claveInterna = ValidacionAdecuacionesManager.validaColumnaCadenaNoVacia(renglon, 2);
                    String tipo = ValidacionAdecuacionesManager.validaColumnaCadenaNoVacia(renglon, 4);
                    List<Double> montos = new ArrayList<Double>();
                    /* Insercion de montos */
                    for (int n = 0; n < 13; n++) {
                        double val = ValidacionAdecuacionesManager.validaColumnaNumericaNoVacia(wb, renglon, 5 + n);
                        if (val < 0)
                            throw new Exception("\nEl valor para la columna " + MESES_ADECUACION[n] + " es menor a cero.");
                        montos.add(val);
                    }
                    AdecuacionDetalle aDet = new AdecuacionDetalle(claveInterna, cveSIAFF, montos, secuencia + 1, tipo);
                    detalle.add(aDet);
                    secuencia++;
                }
            } catch (IncompletRowException e) {
                mensajes += "\n" + e.getMessage();
            } catch (Exception e) {
                mensajes += "\nERROR PROCESANDO RENGLON " + (contadorRenglones + 1) + ": " + e.toString();
            }
        }
        if (secuencia == 0)
            mensajes += "\nEl archivo Excel no contiene detalle.";
        log.info("Finalizando carga de archivo excel de adecuaciones para su validacion Folio[" + nFolio + "] Se contaron " + secuencia + " renglones de detalle");
        if ("".equals(mensajes))
            return detalle;
        else
            throw new Exception(mensajes);
    }

    public static List<AdecuacionDetalleResumen> readAdecuacionDetalle(Connection conn, int folioAdecuacion, List<ProgramaPresupuestario> leeProgramaMetas) throws Exception {
        StringBuilder condicion = new StringBuilder();
        String token = "";
        for (ProgramaPresupuestario programa : leeProgramaMetas) {
            condicion.append(token).append(" substring(ep,27,4) = '").append(programa.getProgramaPresuestario()).append("'");
            token = " OR ";
        }
        StringBuilder query = new StringBuilder();
        query.append("SELECT Substring(ep, 27, 4)     AS programa, ");
        query.append("       Substring(ep, 57, 3)     AS unidadEjecutora, ");
        query.append("       Substring(ep, 42, 2)     AS entidadFederativa, ");
        query.append("       Substring(cevento, 1, 1) AS tipoMovimiento, ");
        query.append("       Sum(mimporte)            AS montoMovimiento, ");
        query.append("       cMes                     AS mes, ");
        query.append("       cMeta AS ordenIndicador ");
        query.append("FROM   tadecuaciondetalle detalle ");
        query.append("       INNER JOIN tCatalogoMetas carteras ");
        query.append("       ON substring(detalle.EP,45,11) = carteras.cCartera ");
        query.append("WHERE  ").append("(").append(condicion).append(")");
        query.append(" AND  nfolioadecuacion = ? ");
        query.append("GROUP  BY Substring(ep, 27, 4), ");
        query.append("          Substring(ep, 57, 3), ");
        query.append("          Substring(ep, 42, 2), ");
        query.append("          Substring(ep, 42, 2), ");
        query.append("          Substring(cevento, 1, 1), ");
        query.append("          cMes, ");
        query.append("          cMeta ");
        query.append("ORDER  BY Substring(cevento, 1, 1) DESC, ");
        query.append("          Substring(ep, 27, 4) ASC, ");
        query.append("          Substring(ep, 57, 3) ASC  ");
        QueryRunner run = new QueryRunner();
        ResultSetHandler<List<AdecuacionDetalleResumen>> h = new BeanListHandler<AdecuacionDetalleResumen>(AdecuacionDetalleResumen.class);
        List<AdecuacionDetalleResumen> detalle = run.query(conn, query.toString(), h, folioAdecuacion);
        return detalle;
    }
}
