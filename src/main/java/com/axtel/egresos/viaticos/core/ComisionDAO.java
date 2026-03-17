package com.axtel.egresos.viaticos.core;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.axtel.egresos.viaticos.Agenda;
import com.axtel.egresos.viaticos.Comision;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ComisionDAO {

    private static final Logger log = LoggerFactory.getLogger(ComisionDAO.class);

    public static int insertarComision(Connection conn, Comision comision) throws Exception {
        StringBuilder queryInsert = new StringBuilder();
        queryInsert.append(" INSERT INTO tComision  (nIdEmpleado,  nIdComision, CTAB, nIdEstatus, cIdUsuarioCaptura, cUnidadResponsable, cNombreComision, nCuentaBancariaCNF, cRFC, nOrigen, fAplicacion, nIdNombre)");
        queryInsert.append(" VALUES (?, ?, ? , 0 , ? , ?, ?, ? ,?, 'ORIGINAL' , getDate(), ?)");
        PreparedStatement ps = null;
        int cnt = 1, insertados = 0;
        try {
            ps = conn.prepareStatement(queryInsert.toString());
            ps.setInt(cnt++, comision.getEmpleado().getNoEmpleado());
            ps.setInt(cnt++, comision.getIdComision());
            ps.setString(cnt++, comision.getCTAB());
            ps.setString(cnt++, comision.getUsuarioCaptura());
            ps.setString(cnt++, comision.getUnidadResponsable());
            ps.setString(cnt++, comision.getNombreComision());
            ps.setString(cnt++, comision.getCuentaBancariaCNF());
            ps.setString(cnt++, comision.getRFC());
            ps.setInt(cnt++, comision.getIdNombre());
            log.debug("Object: " + String.valueOf(queryInsert.toString()));
            insertados = ps.executeUpdate();
            return insertados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int insertaComprobacionComision(Connection conn, Comision comision, int nFolio, String tipoPago) throws Exception {
        StringBuilder queryInsert = new StringBuilder();
        queryInsert.append(" INSERT INTO tComisionComprobacion  (nIdComision, nFolioRelacion, cTipoPago, mPasaje, mTaxi, mPeaje, mHotel, ");
        queryInsert.append(" mConsumos, mOtros, mTotal, mPasajeLocal, mTaxiLocal, mGasolinaLocal, mPeajeLocal,mMaritimoLocal, mAereoLocal, cEvento )");
        queryInsert.append(" VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ? ,? , ?, ?, ?, ?)");
        PreparedStatement ps = null;
        int cnt = 1, insertados = 0;
        try {
            ps = conn.prepareStatement(queryInsert.toString());
            ps.setInt(cnt++, comision.getIdComision());
            ps.setInt(cnt++, nFolio);
            ps.setString(cnt++, tipoPago);
            ps.setBigDecimal(cnt++, comision.getPasaje());
            ps.setBigDecimal(cnt++, comision.getTaxi());
            ps.setBigDecimal(cnt++, comision.getPeaje());
            ps.setBigDecimal(cnt++, comision.getHotel());
            ps.setBigDecimal(cnt++, comision.getConsumos());
            ps.setBigDecimal(cnt++, comision.getOtros());
            ps.setBigDecimal(cnt++, comision.getTotalAgenda());
            ps.setBigDecimal(cnt++, comision.getPasajeLocal());
            ps.setBigDecimal(cnt++, comision.getTaxiLocal());
            ps.setBigDecimal(cnt++, comision.getGasLocal());
            ps.setBigDecimal(cnt++, comision.getPeajeLocal());
            ps.setBigDecimal(cnt++, comision.getMaritimoLocal());
            ps.setBigDecimal(cnt++, comision.getAereoLocal());
            ps.setString(cnt++, comision.getEvento());
            log.debug("Object: " + String.valueOf(queryInsert.toString()));
            insertados = ps.executeUpdate();
            return insertados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int borrarComision(Connection conn, int comision, String login) throws Exception {
        String query = "DELETE tComision where nIdComision = ?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, comision);
            int borrados = ps.executeUpdate();
            actualizarBitacoraComision(conn, comision, login);
            return borrados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static void actualizarBitacoraComision(Connection conn, int comision, String login) throws Exception {
        int id = consultaIdBitacora(conn, comision);
        String query = "UPDATE tComisionBitacora set cUsuario = ? where id = ?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, login);
            ps.setInt(2, id);
            ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int consultaIdBitacora(Connection conn, int comision) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT MAX(id) Id FROM tComisionBitacora WITH (NOLOCK) WHERE  nIdComision = ?";
        int id = 0;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, comision);
            rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
            return id;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int borrarFirmantes(Connection conn, int comision) throws Exception {
        String query = "DELETE tFirmantesViaticos where nFolio = ?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, comision);
            int borrados = ps.executeUpdate();
            return borrados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int esComisionInternacional(Connection conn, int folio) throws Exception {
        PreparedStatement ps = null;
        String query = "SELECT COUNT(*) FROM tAgenda WITH (NOLOCK) WHERE nIdComision = ? and nidPais <> 146";
        ResultSet rs = null;
        int esInternacional = 0;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, folio);
            rs = ps.executeQuery();
            if (rs.next()) {
                esInternacional = rs.getInt(1);
                if (esInternacional > 0) {
                    esInternacional = 1;
                }
            }
            return esInternacional;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }

    public static int guardarFirmantes(Connection conn, Comision comision, int esInternacional) throws Exception {
        CallableStatement call = null;
        int firmantes = 0;
        ResultSet rs = null;
        PreparedStatement pst = null;
        try {
            call = conn.prepareCall("EXEC sp_cadenaFirmantes ?, ?, ?");
            call.setInt(1, comision.getIdComision());
            call.setInt(2, comision.getEmpleado().getNoEmpleado());
            call.setInt(3, esInternacional);
            rs = call.executeQuery();
            if (rs.next())
                firmantes = rs.getInt(1);
            //Actualizar correos cuando es ambiente de desarrollo
            ConfiguraAplicativoBusinessLogic configApp = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
            boolean esSAIAlterno = "true".equalsIgnoreCase(configApp.getSystemSetting("AMBIENTE_DESARROLLO"));
            if (esSAIAlterno) {
                String correo = ConfiguraAplicativoManager.getSystemSetting(conn, "CORREO_ALERTAS_DESARROLLO");
                pst = conn.prepareStatement("UPDATE tFirmantesViaticos SET d_email = ? WHERE nFolio = ? ");
                pst.setString(1, correo);
                pst.setInt(2, comision.getIdComision());
                pst.executeUpdate();
            }
            return firmantes;
        } finally {
            CloseObject.closeObject(pst);
            CloseObject.closeObject(call);
            CloseObject.closeObject(rs);
        }
    }

    public static Comision consultarComision(Connection conn, int folio) throws Exception {
        String query = ("SELECT * FROM tComision WITH (NOLOCK) WHERE nIdComision = ?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        Comision comision = new Comision();
        log.debug("Object: " + String.valueOf("Consultando folio de comision: " + folio));
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, folio);
            rs = ps.executeQuery();
            if (rs.next()) {
                comision.setTotalAgenda(rs.getBigDecimal("mTotalAgenda"));
                comision.setTotalTransporte(rs.getBigDecimal("mTotalTransporte"));
                comision.setIdEstatus(rs.getInt("nIdEstatus"));
                comision.setCTAB(rs.getString("CTAB"));
                comision.setTotalDias(rs.getBigDecimal("diasComision"));
                comision.setUsuarioCaptura(rs.getString("cIdUsuarioCaptura"));
                comision.setUnidadResponsable(rs.getString("cUnidadResponsable"));
                comision.setNombreComision(rs.getString("cNombreComision"));
                comision.setRFC(rs.getString("cRFC"));
                comision.setIdComision(folio);
            }
            String cuentaBancaria = getCuentaBancariaConafor(conn);
            comision.setCuentaBancariaCNF(cuentaBancaria);
            return comision;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }

    private static String getCuentaBancariaConafor(Connection conn) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        String cuenta = "";
        try {
            pst = conn.prepareStatement("SELECT GP_VALOR FROM CG_GRUPO_PROPIEDADES WITH (NOLOCK) WHERE GP_NOMBRE = 'cuenta_viaticos'");
            rs = pst.executeQuery();
            if (rs.next()) {
                cuenta = rs.getString(1);
            }
        } finally {
            CloseObject.closeObject(pst);
            CloseObject.closeObject(rs);
        }
        return cuenta;
    }

    public static int actualizarComision(Connection conn, int idComision, String login) throws Exception {
        int actualizados = 0;
        PreparedStatement ps = null;
        BigDecimal dias = new BigDecimal("0.00");
        BigDecimal total = new BigDecimal("0.00");
        try {
            dias = consultaDiasComision(conn, idComision);
            total = consultaTotalComision(conn, idComision);
            ps = conn.prepareStatement("UPDATE tComision SET mTotalAgenda = ?, diasComision = ? WHERE nIdComision = ?");
            ps.setBigDecimal(1, total);
            ps.setBigDecimal(2, dias);
            ps.setInt(3, idComision);
            actualizados = ps.executeUpdate();
            actualizarBitacoraComision(conn, idComision, login);
        } finally {
            CloseObject.closeObject(ps);
        }
        return actualizados;
    }

    public static int actualizarDiasComision(Connection conn, int idComision) throws Exception {
        int actualizados = 0;
        PreparedStatement ps = null;
        BigDecimal dias = new BigDecimal("0.00");
        try {
            dias = consultaDiasComision(conn, idComision);
            ps = conn.prepareStatement("UPDATE tComision SET diasComision = ? WHERE nIdComision = ?");
            ps.setBigDecimal(1, dias);
            ps.setInt(2, idComision);
            actualizados = ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
        return actualizados;
    }

    private static BigDecimal consultaDiasComision(Connection conn, int idComision) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        BigDecimal dias = new BigDecimal("0.00");
        try {
            ps = conn.prepareStatement("SELECT SUM(dias) FROM v_AgendaViaticos WHERE nIdComision = ?");
            ps.setInt(1, idComision);
            rs = ps.executeQuery();
            if (rs.next()) {
                dias = rs.getBigDecimal(1);
            }
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
        return dias;
    }

    public static BigDecimal consultaTotalComision(Connection conn, int idComision) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        BigDecimal importe = new BigDecimal("0.00");
        String query = "SELECT SUM(cuotaSinFormato) FROM v_AgendaViaticos WHERE nIdComision = ? ";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, idComision);
            rs = ps.executeQuery();
            if (rs.next()) {
                importe = rs.getBigDecimal(1);
            }
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
        return importe;
    }

    public static int cancelaComision(Connection conn, int idComision) throws Exception {
        PreparedStatement ps = null;
        int cancelado = 0;
        try {
            ps = conn.prepareStatement("UPDATE  tComision SET cDocHaplicado = 'C' WHERE nIdComision =  ? ");
            ps.setInt(1, idComision);
            cancelado = ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
        return cancelado;
    }

    public static int cancelaComisionConMotivo(Connection conn, int idComision, String motivo) throws Exception {
        PreparedStatement ps = null;
        int cancelado = 0;
        try {
            ps = conn.prepareStatement("UPDATE  tComision SET cDocHaplicado = 'C', cObservaciones= ? WHERE nIdComision =  ? ");
            ps.setString(1, motivo);
            ps.setInt(2, idComision);
            cancelado = ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
        return cancelado;
    }

    public static String consultaEventoViaticos(Connection conn, String documento, String tipo) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String evento = "";
        try {
            ps = conn.prepareStatement("SELECT cEvento FROM tEventoViaticos WITH (NOLOCK) WHERE cDocumento = ? AND tipo = ?");
            ps.setString(1, documento);
            ps.setString(2, tipo);
            rs = ps.executeQuery();
            if (rs.next()) {
                evento = rs.getString(1);
            }
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
        return evento;
    }

    public static String guardarDatosBoletos(Connection conn, int folio, int tieneBoleto, String justBoletos) throws Exception {
        PreparedStatement ps = null;
        String evento = "";
        try {
            ps = conn.prepareStatement("UPDATE tcomision SET cTieneBoleto = ? ,  cJustificaBoleto = ? WHERE nIdComision = ?");
            ps.setInt(1, tieneBoleto);
            ps.setString(2, justBoletos);
            ps.setInt(3, folio);
            ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
        return evento;
    }

    public static int consultaEmpleado(Connection conn, int idAgenda) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int idEmpleado = 0;
        try {
            ps = conn.prepareStatement("SELECT nIdEmpleado FROM tComision c WITH (NOLOCK) INNER JOIN tAgenda a WITH (NOLOCK) ON c.nIdComision = a.nIdComision WHERE nidAgenda = ?");
            ps.setInt(1, idAgenda);
            rs = ps.executeQuery();
            if (rs.next()) {
                idEmpleado = rs.getInt(1);
            }
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
        return idEmpleado;
    }

    public static int tieneFirmantes(Connection conn, int folio) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int tieneFirmas = 0;
        try {
            ps = conn.prepareStatement("SELECT COUNT(*) tieneFirmantes FROM tFirmantesViaticos WITH (NOLOCK) WHERE nFolio = ?");
            ps.setInt(1, folio);
            rs = ps.executeQuery();
            if (rs.next()) {
                tieneFirmas = rs.getInt(1);
            }
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
        return tieneFirmas;
    }

    public static int consultaNoEmpleado(Connection conn, int folio) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int idEmpleado = 0;
        try {
            ps = conn.prepareStatement("SELECT nIdEmpleado FROM tComision WITH (NOLOCK) WHERE nIdComision = ?");
            ps.setInt(1, folio);
            rs = ps.executeQuery();
            if (rs.next()) {
                idEmpleado = rs.getInt(1);
            }
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
        return idEmpleado;
    }

    public static void actualizaComisionBitacora(Connection conn, int folio, String login) throws Exception {
        String query = "UPDATE tComisionBitacora SET cUsuario = ? where nIdComision = ?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, login);
            ps.setInt(2, folio);
            ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static void insertarBitacoraRevertir(Connection conn, int folio, Agenda agenda, int idEmpleado, String proceso) throws Exception {
        String query = "INSERT INTO tRevertirAsistenciaBitacora ( nidComision, nidEmpleado, finicio, ffin, cProceso, fAplicacion) values ( ?,?,?,?,?, GETDATE())";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, folio);
            ps.setInt(2, idEmpleado);
            ps.setDate(3, (Date) agenda.getFechaInicio());
            ps.setDate(4, (Date) agenda.getFechaFin());
            ps.setString(5, proceso);
            ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static boolean estaFinalizada(Connection conn, int folio) throws Exception {
        boolean ap = false;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String query = "SELECT ISNULL(cDocHaplicado, 0) cDocHaplicado FROM tComision WITH (NOLOCK) WHERE nIdComision = ?";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, folio);
            rs = pst.executeQuery();
            if (rs.next()) {
                String aplicado = rs.getString(1);
                if (aplicado.equalsIgnoreCase("F")) {
                    ap = true;
                }
            }
        } finally {
            CloseObject.closeObject(pst, false);
        }
        return ap;
    }

    public static boolean actualizaEstatusAplicado(Connection conn, int folioComision, int idEstatus) throws Exception {
        boolean existe = false;
        PreparedStatement pst = null;
        String query = "UPDATE tComision SET cDocHaplicado = 'S', nIdEstatus = ? WHERE nIdComision = ?";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, idEstatus);
            pst.setInt(2, folioComision);
            int act = pst.executeUpdate();
            if (act > 0)
                existe = true;
        } finally {
            CloseObject.closeObject(pst, false);
        }
        return existe;
    }

    public static void finalizaComision(Connection conn, int folioComision) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("UPDATE tComision set cDocHaplicado = 'F' WHERE nIdComision = ?");
            ps.setInt(1, folioComision);
            ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static String consultaEstatusComision(Connection conn, int folio) throws Exception {
        String estatus = "";
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement("SELECT ISNULL(cDocHaplicado, '') cDocHaplicado FROM tComision WITH (NOLOCK) WHERE nIdComision = ?");
            pst.setInt(1, folio);
            rs = pst.executeQuery();
            if (rs.next()) {
                estatus = rs.getString(1);
            }
        } finally {
            CloseObject.closeObject(pst, false);
        }
        return estatus;
    }
}
