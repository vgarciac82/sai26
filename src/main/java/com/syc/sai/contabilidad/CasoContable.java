package com.syc.sai.contabilidad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.CasoOperacionManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Operacion;
import com.syc.gestion.core.OperacionManager;
import com.syc.gestion.core.TipoCaso;
import com.syc.gestion.core.TipoCasoManager;
import com.syc.gestion.core.Usuario;
import java.util.Base64;

public class CasoContable {

    public synchronized static Caso nuevoCaso(Connection conn, Usuario u, String cCentroContable, int id_tc) throws SQLException, GestionException {
        Caso c = null;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("SELECT * FROM cg_tipo_caso with(nolock) WHERE id_tc = ?");
            pstmnt.setInt(1, id_tc);
            ResultSet rs = pstmnt.executeQuery();
            if (rs.next()) {
                c = new Caso();
                c.setIdCaso(nextVal("ID_CASO", conn));
                c.setIdTC(rs.getInt("id_tc"));
                c.setFechaInicio(new Timestamp(System.currentTimeMillis()));
                c.setTiempoLimite(rs.getInt("tc_tiempo_limite"));
                c.setAlarma(rs.getString("tc_alarma"));
                c.setIdGabinete(-1);
                c.setStatus(Caso.CREATED | Caso.EXECUTED);
                TipoCaso tc = new TipoCaso();
                tc.setIdTC(rs.getInt("id_tc"));
                c.setTipoCaso(TipoCasoManager.select(conn, tc));
                c.setFolio(getNextFolioUsr(conn, cCentroContable, c));
                if (CasoManager.insert(conn, c) <= 0)
                    throw new SQLException("No se pudo salvar el caso iniciado (" + id_tc + ")");
                Operacion o = OperacionManager.primeraOperacion(conn, id_tc);
                if (o == null)
                    throw new NullPointerException("No se encontro la primera operacion del tipo caso (" + id_tc + ")");
                CasoOperacion co = CasoOperacionManager.nuevoCasoOperacion(conn, "", null, c, o);
                if (co == null)
                    throw new SQLException("No se logro crear el caso operacion del caso iniciado (" + id_tc + ")");
                if (CasoOperacionManager.insert(conn, co) <= 0)
                    throw new SQLException("No se pudo salvar el caso operacion iniciado (" + id_tc + ")");
                c.setCasoOperacion(co);
                c.setCasoDato(CasoDatoManager.createCasoDato(conn, id_tc, c.getIdCaso()));
                CasoManager.update(conn, c);
                conn.commit();
            }
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return c;
    }

    public synchronized static String getNextFolioUsr(Connection conn, String cCentroContable, Caso c) throws SQLException, GestionException {
        int id = -1;
        String prefijo = "";
        String retval = null;
        prefijo = "POLI-C" + cCentroContable + "-";
        id = nextVal(c.getTipoCaso().getGavetaAsociada(), conn);
        retval = prefijo + id;
        return retval;
    }

    public synchronized static int nextVal(String name, Connection conn) throws SQLException {
        int retVal = 1;
        PreparedStatement psUpdate = null, psSelect = null;
        ResultSet rs = null;
        try {
            // Bloqueamos el registro incrementando al nuevo valor
            psUpdate = conn.prepareStatement("UPDATE cf_sequence WITH (ROWLOCK) SET seq_value = seq_value + 1 WHERE seq_name = ?");
            psUpdate.setString(1, name);
            psUpdate.executeUpdate();
            // Recuperamos el nuevo valor
            psSelect = conn.prepareStatement("SELECT seq_value FROM cf_sequence WITH (NOLOCK) WHERE seq_name = ?");
            psSelect.setString(1, name);
            rs = psSelect.executeQuery();
            if (rs.next()) {
                retVal = rs.getInt("seq_value");
            } else {
                // Si no existe creamos el registro
                CFSequenceManager.insert(conn, name, retVal);
            }
            conn.commit();
        } catch (Exception exc) {
            conn.rollback();
            throw new SQLException(exc);
        } finally {
            if (psSelect != null)
                psSelect.close();
            if (psUpdate != null)
                psUpdate.close();
            psSelect = null;
            psUpdate = null;
        }
        return retVal;
    }

    @SuppressWarnings("finally")
    public synchronized static boolean finalizaPoliza(Caso caso, Usuario u, Connection conn) {
        int id = 0;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        boolean status = true;
        try {
            pstmnt = conn.prepareStatement("SELECT MAX(id_oper) FROM cg_operacion with(nolock) WHERE id_tc = ?");
            pstmnt.setInt(1, caso.getIdTC());
            rs = pstmnt.executeQuery();
            rs.next();
            id = rs.getInt(1);
            pstmnt = conn.prepareStatement(" update CG_CASO_OPERACION set ID_OPER=CO.ID_OPER, CO_RESPONSABLE=CO.O_RESPONSABLE  from CG_OPERACION CO with(nolock) " + " where CO.ID_OPER=? and  CO.ID_TC=?  and ID_CASO=?");
            pstmnt.setInt(1, id);
            pstmnt.setInt(2, caso.getIdTC());
            pstmnt.setInt(3, caso.getIdCaso());
            pstmnt.execute();
            conn.commit();
            pstmnt = conn.prepareStatement(" update CG_CASO_DATO set CD_VALOR=? where ID_CASO=? and ID_CD=? ");
            pstmnt.setString(1, caso.getFolio());
            pstmnt.setInt(2, caso.getIdCaso());
            pstmnt.setInt(3, 1);
            pstmnt.addBatch();
            pstmnt.setString(1, caso.getFormatFechaInicio("dd/MM/yyyy"));
            pstmnt.setInt(2, caso.getIdCaso());
            pstmnt.setInt(3, 2);
            pstmnt.addBatch();
            pstmnt.setString(1, u.getNombre());
            pstmnt.setInt(2, caso.getIdCaso());
            pstmnt.setInt(3, 4);
            pstmnt.addBatch();
            pstmnt.setString(1, "Aplicación Poliza");
            pstmnt.setInt(2, caso.getIdCaso());
            pstmnt.setInt(3, 5);
            pstmnt.addBatch();
            pstmnt.setString(1, "MXP");
            pstmnt.setInt(2, caso.getIdCaso());
            pstmnt.setInt(3, 6);
            pstmnt.addBatch();
            pstmnt.setString(1, "true");
            pstmnt.setInt(2, caso.getIdCaso());
            pstmnt.setInt(3, 8);
            pstmnt.addBatch();
            pstmnt.setString(1, "true");
            pstmnt.setInt(2, caso.getIdCaso());
            pstmnt.setInt(3, 11);
            pstmnt.addBatch();
            pstmnt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmnt != null)
                    pstmnt.close();
            } catch (Exception e) {
                ;
            }
            pstmnt = null;
            rs = null;
            return status;
        }
    }
}
