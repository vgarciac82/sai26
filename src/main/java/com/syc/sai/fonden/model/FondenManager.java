package com.syc.sai.fonden.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.syc.sai.fonden.Fonden;
import com.syc.sai.fonden.FondenEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class FondenManager {

    private static Logger log = LoggerFactory.getLogger(FondenManager.class);

    public static List<Fonden> readFonden(Connection conn, Integer cIdFonden) throws FondenEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        List<Fonden> l = new ArrayList<Fonden>();
        try {
            String qry = "select cidFonden, cusuarioCreador, dfechaCaptura, cdescripcion, convert(decimal(16,2), nimporteAnual) as nimporteAnual from tFonden where cIdFonden=" + cIdFonden;
            pStatement = conn.prepareStatement(qry);
            //int cnt = 1;
            rs = pStatement.executeQuery();
            while (rs.next()) {
                l.add(extraeFonden(rs));
            }
            return l;
        } catch (Exception e) {
            throw new FondenEngineException(e);
        } finally {
            if (pStatement != null)
                try {
                    pStatement.close();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas cerrando PreparedStatement " + e2.toString());
                }
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas cerrando ResultSet " + e2.toString());
                }
        }
    }

    public static int saveOrUpdateFonden(Connection conn, Fonden fonden) throws FondenEngineException {
        if (readFonden(conn, fonden.getCidFonden()).isEmpty()) {
            return saveFonden(conn, fonden);
        } else {
            return updateFonden(conn, fonden);
        }
    }

    public static int saveFonden(Connection conn, Fonden fonden) throws FondenEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        List<Fonden> l = new ArrayList<Fonden>();
        int numRows;
        try {
            String qry = "INSERT INTO tFonden (cidFonden, cusuarioCreador, dfechaCaptura, cdescripcion, nimporteAnual) values( ?,?,?,?,? );";
            pStatement = conn.prepareStatement(qry);
            //int cnt = 1;
            pStatement.setInt(1, fonden.getCidFonden());
            pStatement.setString(2, fonden.getCusuarioCreador());
            pStatement.setDate(3, fonden.getDfechaCaptura());
            pStatement.setString(4, fonden.getCdescripcion());
            pStatement.setDouble(5, fonden.getNimporteAnual());
            numRows = pStatement.executeUpdate();
            conn.commit();
            return numRows;
        } catch (Exception e) {
            throw new FondenEngineException(e);
        } finally {
            if (pStatement != null)
                try {
                    pStatement.close();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas cerrando PreparedStatement " + e2.toString());
                }
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas cerrando ResultSet " + e2.toString());
                }
        }
    }

    public static int updateFonden(Connection conn, Fonden fonden) throws FondenEngineException {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        List<Fonden> l = new ArrayList<Fonden>();
        int numRows;
        try {
            String qry = "UPDATE tFonden SET cusuarioCreador=?, dfechaCaptura=?, cdescripcion=?, nimporteAnual=? where cidFonden=?;";
            pStatement = conn.prepareStatement(qry);
            //int cnt = 1;
            pStatement.setString(1, fonden.getCusuarioCreador());
            pStatement.setDate(2, fonden.getDfechaCaptura());
            pStatement.setString(3, fonden.getCdescripcion());
            pStatement.setDouble(4, fonden.getNimporteAnual());
            pStatement.setInt(5, fonden.getCidFonden());
            numRows = pStatement.executeUpdate();
            conn.commit();
            return numRows;
        } catch (Exception e) {
            throw new FondenEngineException(e);
        } finally {
            if (pStatement != null)
                try {
                    pStatement.close();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas cerrando PreparedStatement " + e2.toString());
                }
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas cerrando ResultSet " + e2.toString());
                }
        }
    }

    private static Fonden extraeFonden(ResultSet rs) throws FondenEngineException {
        try {
            Fonden pojo = new Fonden();
            pojo.setCidFonden(rs.getInt("cidFonden"));
            pojo.setCusuarioCreador(rs.getString("cusuarioCreador"));
            pojo.setDfechaCaptura(rs.getDate("dfechaCaptura"));
            pojo.setCdescripcion(rs.getString("cdescripcion"));
            pojo.setNimporteAnual(rs.getDouble("nimporteAnual"));
            return pojo;
        } catch (Exception e) {
            throw new FondenEngineException(e);
        }
    }
}
