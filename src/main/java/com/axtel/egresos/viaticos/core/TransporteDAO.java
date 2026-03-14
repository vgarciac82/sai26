package com.axtel.egresos.viaticos.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import com.axtel.egresos.viaticos.Transporte;
import com.axtel.egresos.viaticos.TransporteAereo;
import com.axtel.egresos.viaticos.TransporteOficial;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.contabilidad.utils.db.RSToTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class TransporteDAO {

    private static final Logger log = LoggerFactory.getLogger(TransporteDAO.class);

    public static int insertarTransporteLocal(Connection conn, Transporte transporte) throws Exception {
        StringBuilder queryInsert = new StringBuilder();
        queryInsert.append("INSERT INTO tTransporteLocal");
        queryInsert.append("   (nIdComision, nIdTransporte,   nIdTipo,cOrigen ,mMonto ,mKm  )");
        queryInsert.append("	VALUES (?, ?, ?, ?, ? , ? )");
        PreparedStatement ps = null;
        int insertados = 0;
        try {
            int cnt = 1;
            ps = conn.prepareStatement(queryInsert.toString());
            ps.setInt(cnt++, transporte.getIdComision());
            ps.setInt(cnt++, transporte.getIdTransporte());
            ps.setInt(cnt++, transporte.getIdTipo());
            ps.setString(cnt++, transporte.getOrigen());
            ps.setBigDecimal(cnt++, transporte.getMonto());
            ps.setLong(cnt++, transporte.getKm());
            log.debug("Object: {}", queryInsert.toString());
            insertados = ps.executeUpdate();
            return insertados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int insertarTransporteOficial(Connection conn, TransporteOficial transporte) throws Exception {
        StringBuilder queryInsert = new StringBuilder();
        queryInsert.append("INSERT INTO tTransporteLocal");
        queryInsert.append("   (nIdComision, nIdTransporte,   nIdTipo,cOrigen ,mMonto ,mKm , cNumEconomico, cTieneVales)");
        queryInsert.append("	VALUES (?, ?, ?, ?, ? , ?, ?, ? )");
        PreparedStatement ps = null;
        int insertados = 0;
        try {
            int cnt = 1;
            ps = conn.prepareStatement(queryInsert.toString());
            ps.setInt(cnt++, transporte.getIdComision());
            ps.setInt(cnt++, transporte.getIdTransporte());
            ps.setInt(cnt++, transporte.getIdTipo());
            ps.setString(cnt++, transporte.getOrigen());
            ps.setBigDecimal(cnt++, transporte.getMonto());
            ps.setLong(cnt++, transporte.getKm());
            ps.setString(cnt++, transporte.getNumEconomico());
            ps.setInt(cnt++, transporte.getTieneVales());
            log.debug("Object: {}", queryInsert.toString());
            insertados = ps.executeUpdate();
            return insertados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int insertarTransporteAereo(Connection conn, TransporteAereo transporte) throws Exception {
        StringBuilder queryInsert = new StringBuilder();
        queryInsert.append("INSERT INTO tTransporteAereo (nidComision, cReferencia,mImporteBoleto , cPartida, cRuta, RFCVuelo, cNombre, cTipoPago )");
        queryInsert.append("	VALUES (?, ?, ? ,?, ?, ? , ?, ?)");
        PreparedStatement ps = null;
        int insertados = 0;
        int cnt = 1;
        try {
            ps = conn.prepareStatement(queryInsert.toString());
            ps.setInt(cnt++, transporte.getIdComision());
            ps.setString(cnt++, transporte.getcNumeroBoleto());
            ps.setBigDecimal(cnt++, transporte.getmImporteBoleto());
            ps.setString(cnt++, transporte.getPartida());
            ps.setString(cnt++, transporte.getRuta());
            ps.setString(cnt++, transporte.getRFCVuelo());
            ps.setString(cnt++, transporte.getNombreVuelo());
            ps.setString(cnt++, transporte.getTipoPago());
            log.debug("Object: {}", queryInsert.toString());
            insertados = ps.executeUpdate();
            return insertados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int borrarTransporte(Connection conn, int folio, int idComision) throws Exception {
        int borrados = 0;
        PreparedStatement ps = null;
        String queryDelete = "DELETE tTransporteLocal  where nIdTransporte = ? and nIdComision = ?";
        try {
            ps = conn.prepareStatement(queryDelete);
            ps.setInt(1, folio);
            ps.setInt(2, idComision);
            borrados = ps.executeUpdate();
            return borrados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int borrarTransporteTodos(Connection conn, int idComision) throws Exception {
        int borrados = 0;
        PreparedStatement ps = null;
        String queryDelete = "DELETE tTransporteLocal  where nIdComision = ?";
        try {
            ps = conn.prepareStatement(queryDelete);
            ps.setInt(1, idComision);
            borrados = ps.executeUpdate();
            return borrados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int borrarTransporteAereo(Connection conn, int idComision, int folioPago, String tipoPago) throws Exception {
        int borrados = 0;
        PreparedStatement ps = null;
        String queryDelete = "DELETE tTransporteAereo  where nidComision = ? and nfolioPago = ? and ctipoPago = ?";
        try {
            ps = conn.prepareStatement(queryDelete);
            ps.setInt(1, idComision);
            ps.setInt(2, folioPago);
            ps.setString(3, tipoPago);
            borrados = ps.executeUpdate();
            return borrados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int borrarBoletoAvion(Connection conn, TransporteAereo avion) throws Exception {
        int borrados = 0;
        PreparedStatement ps = null;
        String queryDelete = "DELETE tTransporteAereo WHERE cReferencia = ? AND mImporteBoleto = ? AND nidComision  = ? ";
        try {
            ps = conn.prepareStatement(queryDelete);
            ps.setString(1, avion.getcNumeroBoleto());
            ps.setBigDecimal(2, avion.getmImporteBoleto());
            ps.setInt(3, avion.getIdComision());
            borrados = ps.executeUpdate();
            log.debug("Object: {}", queryDelete.toString());
            return borrados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static List<Transporte> consultaTransporteLocal(Connection conn, int idAgenda) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Transporte> transportes = new ArrayList<Transporte>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT nidComision idComision, nIdTransporte idTransporte, nIdTipo idTipo, ");
        query.append(" cOrigen origen, mMonto monto, mKm km, cNumEconomico, cTieneVales");
        query.append(" FROM tTransporteLocal WITH (NOLOCK) WHERE nidComision = ?");
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, idAgenda);
            rs = ps.executeQuery();
            Map<String, String> resultObj = RSToTable.rsToMapCaseSensitive(rs);
            while (resultObj != null) {
                Transporte transportesLocal = new Transporte();
                BeanUtils.populate(transportesLocal, resultObj);
                TransporteOficial nwTransporte = new TransporteOficial();
                BeanUtils.populate(nwTransporte, resultObj);
                transportes.add(transportesLocal);
                resultObj = RSToTable.rsToMapCaseSensitive(rs);
            }
            return transportes;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static int actualizarLayout(Connection conn, int idComision, String estatus, int folioPago) throws Exception {
        PreparedStatement pst = null;
        String query = "UPDATE layout set Status = ? FROM tLayoutVuelosDet layout " + " INNER JOIN tTransporteAereo transporte " + " ON layout.RFC = transporte.RFCVuelo AND layout.mTotal = transporte.mImporteBoleto AND layout.cReferencia = transporte.cReferencia " + " WHERE transporte.nidComision = ? and transporte.nFolioPago = ? ";
        int actualizados = 0;
        try {
            pst = conn.prepareStatement(query);
            pst.setString(1, estatus);
            pst.setInt(2, idComision);
            pst.setInt(3, folioPago);
            actualizados = pst.executeUpdate();
            log.debug("Object: {}", query.toString());
            return actualizados;
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static int actualizarLayoutPorBoleto(Connection conn, TransporteAereo transporte, String estatus) throws Exception {
        PreparedStatement pst = null;
        String query = "UPDATE layout set Status = ? FROM tLayoutVuelosDet layout " + " INNER JOIN tTransporteAereo transporte " + " ON layout.RFC = transporte.RFCVuelo AND layout.mTotal = transporte.mImporteBoleto AND layout.cReferencia = transporte.cReferencia " + " WHERE transporte.nidComision = ? and transporte.cReferencia = ? ";
        int actualizados = 0;
        try {
            pst = conn.prepareStatement(query);
            pst.setString(1, estatus);
            pst.setInt(2, transporte.getIdComision());
            pst.setString(3, transporte.getcNumeroBoleto());
            actualizados = pst.executeUpdate();
            log.debug("Object: {}", query.toString());
            return actualizados;
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static int actualizarTransporteLocal(Connection conn, TransporteOficial transporte, int folioComision) throws Exception {
        PreparedStatement pst = null;
        String query = "UPDATE tTransporteLocal SET nIdTipo = ?, cOrigen = ?, mMonto = ?, mKm = ?, cNumEconomico = ?, cTieneVales = ? WHERE nIdComision = ?  and nIdTransporte = ?";
        int actualizados = 0;
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, transporte.getIdTipo());
            pst.setString(2, transporte.getOrigen());
            pst.setBigDecimal(3, transporte.getMonto());
            pst.setLong(4, transporte.getKm());
            pst.setString(5, transporte.getNumEconomico());
            pst.setInt(6, transporte.getTieneVales());
            pst.setInt(7, folioComision);
            pst.setInt(8, transporte.getIdTransporte());
            actualizados = pst.executeUpdate();
            log.debug("Object: {}", query.toString());
            return actualizados;
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static void actualizaFolioPago(Connection conn, int idComision, int folio) throws Exception {
        PreparedStatement pst = null;
        //int actualizado = 0;
        try {
            pst = conn.prepareStatement("UPDATE tTransporteAereo SET NFOLIOPAGO = ? where nidComision = ? and nfoliopago IS NULL");
            pst.setInt(1, folio);
            pst.setInt(2, idComision);
            pst.executeUpdate();
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static void actualizaEstatusFolioPago(Connection conn, int idComision, int folio) throws Exception {
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement("UPDATE tTransporteAereo SET NFOLIOPAGO = ? , cTienePago = 'S' where nidComision = ? and NFOLIOPAGO IS NULL");
            pst.setInt(1, folio);
            pst.setInt(2, idComision);
            pst.executeUpdate();
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static int buscaComision(Connection conn, int folioPago) throws Exception {
        int comision = 0;
        String query = "SELECT ISNULL(nidComisionModulo,0) nIdComision FROM tRelacionComprobacionComisiones WHERE cTipoTramite = 'RELACIONGASTOS' AND nFolioTramite = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, folioPago);
            rs = ps.executeQuery();
            if (rs.next()) {
                comision = rs.getInt("nIdComision");
            }
            return comision;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static void updateEstatusBoletos(Connection conn, int folioPago) throws Exception {
        PreparedStatement pst = null;
        //int actualizado = 0;
        try {
            pst = conn.prepareStatement("UPDATE tLayoutVuelosDet SET Status = 'A' WHERE cReferencia IN (SELECT cReferencia FROM tTransporteAereo WITH (NOLOCK) WHERE nFolioPago = ? AND RFC = (SELECT DISTINCT RFCVuelo FROM tTransporteAereo WITH (NOLOCK) WHERE nFolioPago = ?))");
            pst.setInt(1, folioPago);
            pst.setInt(2, folioPago);
            pst.executeUpdate();
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static boolean tieneJustificacionBoletos(Connection conn, int folioPago) throws Exception {
        String sql = "SELECT COUNT(*) tieneBoletos FROM tJustificacionRG where cJustificaBoletos != '' and nFolioRELACIONGASTOS = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, folioPago);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 1;
                }
            }
        }
        return false;
    }

    public static int insertarRelacionTaxi(Connection conn, int idComision, int folio, String tipoPago) throws Exception {
        PreparedStatement pst = null;
        int actualizado = 0;
        try {
            pst = conn.prepareStatement("INSERT INTO tRelacionTaxiPagos (nFolioRelaciongastos, cFolioTaxi, nidComision, mMonto, cTipoPago) " + " SELECT ?, cFolioTaxi, nidComision, mMonto, ? FROM v_TaxisViaticos WHERE nidComision = ? ");
            pst.setInt(1, folio);
            pst.setString(2, tipoPago);
            pst.setInt(3, idComision);
            actualizado = pst.executeUpdate();
            return actualizado;
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static int actualizarLayoutTaxis(Connection conn, int idComision, int tienePago) throws Exception {
        PreparedStatement pst = null;
        int actualizado = 0;
        try {
            pst = conn.prepareStatement("UPDATE tLayoutTaxis SET cTienePago = ? WHERE nIdComision = ?");
            pst.setInt(1, tienePago);
            pst.setInt(2, idComision);
            actualizado = pst.executeUpdate();
            return actualizado;
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static int borrarTaxi(Connection conn, int nFolioRelaciongastos, String cTipoPago) throws Exception {
        int borrados = 0;
        PreparedStatement ps = null;
        String queryDelete = "DELETE tRelacionTaxiPagos where nFolioRelaciongastos = ? and cTipoPago = ?";
        try {
            ps = conn.prepareStatement(queryDelete);
            ps.setInt(1, nFolioRelaciongastos);
            ps.setString(2, cTipoPago);
            borrados = ps.executeUpdate();
            log.debug("Object: {}", queryDelete.toString());
            return borrados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }
}
