package com.syc.ejercido.pagado;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import com.syc.ejercido.pagado.CargaPagosBoletaje.Vuelo;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class CargaPagosBoletajeManager {

    public static String msgRetorno = "";

    private static final Logger log = LoggerFactory.getLogger(CargaPagosBoletajeManager.class);

    public static int insertaVuelos(Connection conn, List<Vuelo> vuelos, int folioPago, String centroContable, String login, String nombreArchivo) throws Exception {
        String qureryInsert = "INSERT INTO	tPagoDiversoBoletajeAvion( nFolioPagoDiverso, RFC, cNombre, cReferencia, cRuta, cLineaAerea, mQ, mTarifa, mIVA, mTUA, mYR, mTotal, cPartida, cUnidadEjecutora, fFechaSalida, fFechaRegreso, cCentroContable, U_LOGIN, fRegistro, cNombreArchivo ) " + " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
        PreparedStatement ps = null;
        int insertados = 0;
        try {
            ps = conn.prepareStatement(qureryInsert);
            for (Iterator<Vuelo> i = vuelos.iterator(); i.hasNext(); ) {
                Vuelo v = i.next();
                ps.setInt(1, folioPago);
                ps.setString(2, v.getRFC());
                ps.setString(3, v.getcNombre());
                ps.setString(4, v.getcReferencia());
                ps.setString(5, v.getcRuta());
                ps.setString(6, v.getcLineaAerea());
                //ps.setString(7, v.getcClase());
                ps.setBigDecimal(7, v.getmQ());
                ps.setBigDecimal(8, v.getmTarifa());
                ps.setBigDecimal(9, v.getmIVA());
                ps.setBigDecimal(10, v.getmTUA());
                ps.setBigDecimal(11, v.getmYR());
                ps.setBigDecimal(12, v.getmTotal());
                ps.setString(13, v.getcPartida());
                ps.setString(14, v.getcUnidadEjecutora());
                if (v.getfFechaSalida() != null) {
                    ps.setDate(15, new Date(v.getfFechaSalida().getTime()));
                } else {
                    ps.setDate(15, null);
                }
                if (v.getfFechaRegreso() != null) {
                    ps.setDate(16, new Date(v.getfFechaRegreso().getTime()));
                } else {
                    ps.setDate(16, null);
                }
                //ps.setString(18, v.getcDFO());
                ps.setString(17, centroContable);
                ps.setString(18, login);
                ps.setDate(19, new Date(System.currentTimeMillis()));
                ps.setString(20, nombreArchivo);
                insertados += ps.executeUpdate();
            }
            return insertados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static boolean existeFolioPago(Connection conn, int nFolioPago) throws Exception {
        boolean bReturn = true;
        String sQueryRead = " SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END AS bExiste FROM tPagoDiversoBoletajeAvion WITH (NOLOCK) " + " WHERE nFolioPagoDiverso = ?	";
        ResultSet res = null;
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement(sQueryRead);
            pstm.setInt(1, nFolioPago);
            res = pstm.executeQuery();
            if (res.next()) {
                bReturn = res.getBoolean("bExiste");
            }
            return bReturn;
        } finally {
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(res);
        }
    }

    public static int obtenerFolioVuelo(Connection conn) throws Exception {
        int bReturn = 0;
        String sQueryRead = " SELECT (ISNULL(MAX(nFolioVuelos), 0) + 1) AS nFolioVuelo FROM tLayoutVuelosEnc WITH (NOLOCK) ";
        ResultSet res = null;
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement(sQueryRead);
            res = pstm.executeQuery();
            if (res.next()) {
                bReturn = res.getInt("nFolioVuelo");
            }
            return bReturn;
        } finally {
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(res);
        }
    }

    public static int insertaLayoutVuelos(Connection conn, List<Vuelo> vuelos, int folioPago, String centroContable, String login, String nombreArchivo) throws Exception {
        int insertados = 0;
        try {
            insertados = insertaLayoutEncabezado(conn, vuelos, folioPago, centroContable, login, nombreArchivo);
        } catch (Exception e) {
            throw e;
        }
        return insertados;
    }

    public static int insertaLayoutEncabezado(Connection conn, List<Vuelo> vuelos, int folioPago, String centroContable, String login, String nombreArchivo) throws Exception {
        String qureryInsert = "INSERT INTO	tLayoutVuelosEnc( nFolioVuelos, cFolioVuelos, cCentroContable, U_LOGIN, fRegistro, Status ) " + " SELECT ?, ?, ?, ?, GETDATE() AS fRegistro, 'A' AS Status  ";
        PreparedStatement ps = null;
        int insertados = 0;
        int nFolioVuelo = 0;
        try {
            if (folioPago == 0) {
                nFolioVuelo = obtenerFolioVuelo(conn);
                if (nFolioVuelo != 0) {
                    ps = conn.prepareStatement(qureryInsert);
                    ps.setInt(1, nFolioVuelo);
                    ps.setString(2, ("VUELOS-" + nFolioVuelo));
                    ps.setString(3, centroContable);
                    ps.setString(4, login);
                    insertados += ps.executeUpdate();
                    if (!validaBeneficiarios(conn, vuelos, nFolioVuelo)) {
                        insertados = 0;
                    }
                    if (insertados > 0) {
                        insertados = insertaLayoutDetalle(conn, vuelos, nFolioVuelo, centroContable, login, nombreArchivo);
                    }
                }
            } else {
                if (validaBeneficiarios(conn, vuelos, folioPago)) {
                    insertados = insertaLayoutDetalle(conn, vuelos, folioPago, centroContable, login, nombreArchivo);
                }
            }
            return insertados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int insertaLayoutDetalle(Connection conn, List<Vuelo> vuelos, int folioPago, String centroContable, String login, String nombreArchivo) throws Exception {
        String qureryInsert = "INSERT INTO	tLayoutVuelosDet( RFC, cNombre, cReferencia, cRuta, cLineaAerea, mQ, mTarifa, mIVA, mTUA, mYR, mTotal, cPartida, cUnidadEjecutora, fFechaSalida, fFechaRegreso, cCentroContable, U_LOGIN, fRegistro, cNombreArchivo, nFolioVuelos, cFolioVuelos, Status, nDocRenglon, mImporteServicio , mImporteIVAServicio, mGranTotal) " + " SELECT ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'A' AS Status, (SELECT (ISNULL(MAX(nDocRenglon), 0)) + 1 AS Renglon FROM tLayoutVuelosDet WITH (NOLOCK) WHERE nFolioVuelos = ?) , ?, ?, ? ";
        PreparedStatement ps = null;
        int insertados = 0;
        int nFolioVuelo = 0;
        nFolioVuelo = folioPago;
        try {
            if (nFolioVuelo != 0) {
                ps = conn.prepareStatement(qureryInsert);
                for (Iterator<Vuelo> i = vuelos.iterator(); i.hasNext(); ) {
                    Vuelo v = i.next();
                    log.debug("Object: " + String.valueOf("Insertando vuelo: " + v));
                    ps.setString(1, v.getRFC());
                    ps.setString(2, v.getcNombre());
                    ps.setString(3, v.getcReferencia());
                    ps.setString(4, v.getcRuta());
                    ps.setString(5, v.getcLineaAerea());
                    ps.setBigDecimal(6, v.getmQ());
                    ps.setBigDecimal(7, v.getmTarifa());
                    ps.setBigDecimal(8, v.getmIVA());
                    ps.setBigDecimal(9, v.getmTUA());
                    ps.setBigDecimal(10, v.getmYR());
                    ps.setBigDecimal(11, v.getmTotal());
                    ps.setString(12, v.getcPartida());
                    ps.setString(13, v.getcUnidadEjecutora());
                    if (v.getfFechaSalida() != null) {
                        ps.setDate(14, new Date(v.getfFechaSalida().getTime()));
                    } else {
                        ps.setDate(14, null);
                    }
                    if (v.getfFechaRegreso() != null) {
                        ps.setDate(15, new Date(v.getfFechaRegreso().getTime()));
                    } else {
                        ps.setDate(15, null);
                    }
                    ps.setString(16, centroContable);
                    ps.setString(17, login);
                    ps.setDate(18, new Date(System.currentTimeMillis()));
                    ps.setString(19, nombreArchivo);
                    ps.setInt(20, nFolioVuelo);
                    ps.setString(21, ("VUELOS-" + nFolioVuelo));
                    ps.setInt(22, nFolioVuelo);
                    ps.setBigDecimal(23, v.getMontoServicio());
                    ps.setBigDecimal(24, v.getMontoIVAServicio());
                    ps.setBigDecimal(25, v.getGranTotal());
                    insertados += ps.executeUpdate();
                }
            }
            return insertados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public int updateLayoutVuelosDet(Connection conn, int nFolio) throws Exception {
        PreparedStatement psUpdateLayoutVuelos = null;
        PreparedStatement psExisteVuelos = null;
        PreparedStatement psUpdateComisionEnc = null;
        ResultSet rs = null;
        int insertados = 0;
        try {
            String sSqlExisteVuelos = " SELECT CASE WHEN COUNT(*) > 0 THEN '1' ELSE '0' END AS existe FROM tComisionesSinComprobacionDet WITH (NOLOCK) WHERE nFolioComision = ? ";
            psExisteVuelos = conn.prepareStatement(sSqlExisteVuelos);
            psExisteVuelos.setInt(1, nFolio);
            rs = psExisteVuelos.executeQuery();
            if (rs.next()) {
                String existe = rs.getString("existe");
                if ("1".equalsIgnoreCase(existe)) {
                    String sUpdateLayoutVuelos = " UPDATE	vuelosdet SET vuelosdet.Status = 'A' " + " FROM		tLayoutVuelosDet vuelosdet WITH (NOLOCK) " + " INNER JOIN tComisionesSinComprobacionDet comisiondet WITH(NOLOCK) " + " ON " + " ( " + "	comisiondet.cBoleto = vuelosdet.cReferencia " + "	AND comisiondet.RFCVuelo = vuelosdet.RFC " + "	AND comisiondet.cNombreRFC = vuelosdet.cNombre " + "	AND comisiondet.mImporteBoleto = vuelosdet.mTotal " + " ) " + " WHERE comisiondet.nFolioComision = ? ";
                    psUpdateLayoutVuelos = conn.prepareStatement(sUpdateLayoutVuelos);
                    psUpdateLayoutVuelos.setInt(1, nFolio);
                    int updateVuelos = psUpdateLayoutVuelos.executeUpdate();
                    insertados = updateVuelos;
                    psUpdateComisionEnc = conn.prepareStatement("UPDATE tComisionesSinComprobacionEnc set cDocumentoHaplicado = 'C' where nFolioComision = ?");
                    psUpdateComisionEnc.setInt(1, nFolio);
                    psUpdateComisionEnc.executeUpdate();
                }
            }
            return insertados;
        } finally {
            CloseObject.closeObject(psUpdateLayoutVuelos);
            CloseObject.closeObject(psExisteVuelos);
            CloseObject.closeObject(rs);
        }
    }

    public void cancelaTramite(Connection conn, int folio) throws Exception {
        PreparedStatement pst = null;
        String query = "update dbo.tComisionesSinComprobacionEnc SET cDocumentoHAplicado = 'C' WHERE nFolioComision = ?";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, folio);
            pst.executeQuery();
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    private static boolean validaBeneficiarios(Connection conn, List<Vuelo> vuelos, int nFolio) throws Exception {
        boolean correcto = true;
        PreparedStatement ps = null;
        PreparedStatement psUpdate = null;
        String sSql = " INSERT INTO tLayoutVuelosDet_Tmp (nFolioVuelos, iRenglon, RFC, cNombre, cReferencia, cRuta, mTotal, cPartida) " + " SELECT ?, ?, ?, ?, ?, ?, ?, ? ";
        String sSqlUpdate = " UPDATE	L " + " SET		L.bExiste = '1' " + " FROM	tLayoutVuelosDet_Tmp L WITH (NOLOCK) " + " WHERE EXISTS ( SELECT * FROM tBeneficiario WITH (NOLOCK) WHERE dRFC = L.RFC ) " + " AND		L.nFolioVuelos = ?  ";
        int insertados = 0;
        int iRenglon = 0;
        try {
            ps = conn.prepareStatement(sSql);
            for (Iterator<Vuelo> i = vuelos.iterator(); i.hasNext(); ) {
                Vuelo v = i.next();
                iRenglon++;
                ps.setInt(1, nFolio);
                ps.setInt(2, iRenglon);
                ps.setString(3, v.getRFC());
                ps.setString(4, v.getcNombre());
                ps.setString(5, v.getcReferencia());
                ps.setString(6, v.getcRuta());
                ps.setBigDecimal(7, v.getmTotal());
                ps.setString(8, v.getcPartida());
                insertados += ps.executeUpdate();
            }
            if (insertados > 0) {
                psUpdate = conn.prepareStatement(sSqlUpdate);
                psUpdate.setInt(1, nFolio);
                psUpdate.executeUpdate();
                correcto = validaLayoutDetTmp(conn, nFolio);
            } else {
                correcto = false;
            }
        } catch (Exception e) {
            correcto = false;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(psUpdate);
        }
        return correcto;
    }

    private static boolean validaLayoutDetTmp(Connection conn, int nFolio) {
        boolean correcto = true;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PreparedStatement psDelete = null;
        msgRetorno = "";
        String sSql = " SELECT * FROM tLayoutVuelosDet_Tmp WITH (NOLOCK) " + " WHERE nFolioVuelos = ? AND RFC NOT IN ('INV38301000000', 'INV44102000000') AND bExiste IS NULL  " + " ORDER BY iRenglon ";
        String sSqlDelete = " DELETE FROM tLayoutVuelosDet_Tmp WHERE nFolioVuelos = ? ";
        int iCont = 0;
        String sRow = "";
        String sRFC = "";
        try {
            ps = conn.prepareStatement(sSql);
            ps.setInt(1, nFolio);
            rs = ps.executeQuery();
            while (rs.next()) {
                sRow = "";
                sRFC = "";
                sRow = rs.getString("iRenglon");
                sRFC = rs.getString("RFC");
                msgRetorno = msgRetorno + "\nRFC: " + sRFC + ", NO Existe. Linea # " + sRow;
                iCont++;
            }
            psDelete = conn.prepareStatement(sSqlDelete);
            psDelete.setInt(1, nFolio);
            psDelete.executeUpdate();
            log.info("Object: {}", sSql + " [" + nFolio + "]");
            log.info("Object: {}", sSqlDelete + " [" + nFolio + "]");
            if (iCont > 0) {
                correcto = false;
            }
        } catch (Exception e) {
            correcto = false;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(psDelete);
        }
        return correcto;
    }
}
