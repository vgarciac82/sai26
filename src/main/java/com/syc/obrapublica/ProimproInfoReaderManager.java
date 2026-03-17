package com.syc.obrapublica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ProimproInfoReaderManager {

    private static final Logger log = LoggerFactory.getLogger(ProimproInfoReaderManager.class);

    public static List<String> getEPs(Connection conn, String ef, String cartera, String oli, String ur, String ue) throws Exception {
        ResultSet rsOli = null;
        PreparedStatement psOli = null;
        List<String> eps = new ArrayList<String>();
        try {
            String queryOli = "SELECT CASE  " + "         WHEN epproinpro <> '-1' THEN epproinpro  " + "         ELSE ''  " + "       END        AS descEP,  " + "       epproinpro AS idEP  " + "FROM   (SELECT '-1' AS EPProInpro  " + "        UNION  " + "        SELECT ( CONVERT(VARCHAR," + ef + ") " + "                 + '.16.' + Substring(idcvepres, 15, 3) + '.'  " + "                 + Substring(idcvepres, 1, 13) + '.'  " + "                 + Substring(idcvepres, 19, 14) + '.' + CASE Len(CONVERT(VARCHAR  " + "                 ,  " + "                 idestado))  " + "                        WHEN 1 THEN '0' + CONVERT(VARCHAR, idestado) ELSE  " + "                 CONVERT(  " + "                 VARCHAR,  " + "                        idestado) END + '.' + ? ) AS EPProInpro  " + "        FROM   cnsolis  " + "        WHERE  monto > 0  " + "               AND idproyecto = ?  " + "               AND idoliof = ?)AS a ";
            psOli = conn.prepareStatement(queryOli);
            psOli.setString(1, cartera);
            psOli.setString(2, cartera);
            psOli.setString(3, oli);
            rsOli = psOli.executeQuery();
            while (rsOli.next()) {
                eps.add(rsOli.getString("idEP"));
            }
            return eps;
        } finally {
            try {
                CloseObject.closeObject(rsOli, false);
                CloseObject.closeObject(psOli, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            } finally {
                rsOli = null;
                psOli = null;
            }
        }
    }

    public static List<String> getOlis(Connection conn, String cartera, String ur, String ue) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<String> olis = new ArrayList<String>();
        String query = "SELECT DISTINCT idoliof " + " FROM   cnsolis " + " WHERE  idproyecto = ? ";
        String cond = "";
        try {
            if (ur != null && !"".equals(ur))
                cond += " AND iduan = ? ";
            if (ue != null && !"".equals(ue))
                cond += " AND iduae = ? ";
            ps = conn.prepareStatement(query + cond);
            int i = 1;
            ps.setString(i++, cartera);
            if (ur != null && !"".equals(ur))
                ps.setString(i++, ur);
            if (ue != null && !"".equals(ue))
                ps.setString(i++, ue);
            rs = ps.executeQuery();
            while (rs.next()) {
                olis.add(rs.getString("idoliof"));
            }
            return olis;
        } finally {
            try {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(ps, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            } finally {
                rs = null;
                ps = null;
            }
        }
    }

    public static String getImporteOliCarteraURUE(Connection conn, String cartera, String capitulo, String oli, String ur, String ue) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String importeOli = "0";
        String query = "SELECT DISTINCT monto " + " FROM   cnsolis " + " WHERE  idproyecto = ? ";
        query = "select isnull(sum(Monto), 0) monto from cnsolis where idCvePres like ? and idProyecto in (" + "select distinct idProyecto from cnsolis " + "where idCvePres like ? and idOLIOf = ?)";
        String cond = "";
        try {
            ps = conn.prepareStatement(query + cond);
            int i = 1;
            //ps.setString(i++, ue);
            ps.setString(i++, capitulo);
            //ps.setString(i++, cartera);
            //ps.setString(i++, ue);
            ps.setString(i++, capitulo);
            ps.setString(i++, oli);
            rs = ps.executeQuery();
            if (rs.next()) {
                importeOli = rs.getString("monto");
            }
            return importeOli;
        } finally {
            try {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(ps, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            } finally {
                rs = null;
                ps = null;
            }
        }
    }

    public static List<String> getCarterasProyecto(Connection conn, String cartera, String ur, String ue) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<String> carteras = new ArrayList<String>();
        String sqlBase = "SELECT distinct idproyecto " + " FROM   cnsolis ";
        String token = " WHERE ";
        String cond = "";
        try {
            if (cartera != null && !"".equals(cartera)) {
                cond += token + "       idproyecto = ? ";
                token = " AND ";
            }
            if (ur != null && !"".equals(ur)) {
                cond += token + "      iduan = ? ";
                token = " AND ";
            }
            if (ue != null && !"".equals(ue)) {
                cond += token + "      iduae = ? ";
                token = " AND ";
            }
            log.debug("Object: " + String.valueOf("Buscando carteras [" + sqlBase + cond + "]"));
            ps = conn.prepareStatement(sqlBase + cond);
            int i = 1;
            if (cartera != null && !"".equals(cartera)) {
                ps.setString(i++, cartera);
            }
            if (ur != null && !"".equals(ur)) {
                ps.setString(i++, ur);
            }
            if (ue != null && !"".equals(ue)) {
                ps.setString(i++, ue);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                carteras.add(rs.getString("idproyecto"));
            }
            return carteras;
        } finally {
            try {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(ps, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            } finally {
                rs = null;
                ps = null;
            }
        }
    }

    public static List<String> getEPFromCarteras(Connection conn, String ef, String cartera, String oli, String ur, String ue) throws Exception {
        List<String> clavesPresupuestales;
        List<String> eps = new ArrayList<String>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT ep AS epID FROM vsaldosanuales WHERE ncuentap = 82106";
        String claves = "";
        String tokenClaves = "";
        String condicion = "";
        try {
            clavesPresupuestales = getEPs(conn, ef, cartera, oli, ur, ue);
            if (clavesPresupuestales != null && clavesPresupuestales.size() > 0) {
                for (Iterator<String> i = clavesPresupuestales.iterator(); i.hasNext(); ) {
                    claves = tokenClaves + " ep LIKE '" + i.next() + "%" + ue + "' ";
                    tokenClaves = "OR";
                }
                condicion = " AND (" + claves + ")";
                query += condicion;
                log.debug("Object: " + String.valueOf(query.toString()));
                ps = conn.prepareStatement(query);
                rs = ps.executeQuery();
                while (rs.next()) {
                    eps.add(rs.getString("epID"));
                }
            }
            return eps;
        } finally {
            try {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(ps, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }
}
