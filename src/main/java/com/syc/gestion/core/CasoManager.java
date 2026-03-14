package com.syc.gestion.core;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import com.syc.dbms.DBMS;
import com.syc.gestion.custom.DefaultFolioGenerator;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class CasoManager {

    private static Logger log = LoggerFactory.getLogger(CasoManager.class);

    public static int delete(Connection conn, int id_caso) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("DELETE FROM cg_caso with(rowlock) WHERE id_caso = ?");
            pstmnt.setInt(1, id_caso);
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static Caso findByFolioLike(Connection conn, String tituloAplicacion, String folio) throws Exception {
        Caso c = null;
        String query = "SELECT folio, id_gabinete " + "  FROM IMX" + tituloAplicacion + " WITH(NOLOCK) " + " WHERE folio LIKE '%-%-' + ?";
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, folio);
            rs = ps.executeQuery();
            int encontrados = 0;
            while (rs.next()) {
                encontrados++;
                String folioCompleto = rs.getString("folio");
                int idGabinete = rs.getInt("id_gabinete");
                c = new Caso();
                c.setFolio(folioCompleto);
                c.setIdGabinete(idGabinete);
                actualizarGabinete(conn, idGabinete, folioCompleto);
                c = select(conn, c);
            }
            if (encontrados > 1 || c == null)
                throw new Exception("No se pudo encontrar caso con el folio terminacion " + folio + " en la aplicacion " + tituloAplicacion);
            return c;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }

    public static void actualizarGabinete(Connection conn, int idGabinete, String folioCaso) throws Exception {
        PreparedStatement pst = null;
        String query = "UPDATE CG_CASO SET C_ID_GABINETE = ? WHERE C_FOLIO = ?";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, idGabinete);
            pst.setString(2, folioCaso);
            pst.executeUpdate();
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static int findIdCasoByFolio(Connection conn, String tituloAplicacion, int folio) throws SQLException {
        PreparedStatement psBuscaCaso = null;
        ResultSet rsBuscaCaso = null;
        TipoCaso[] tc = TipoCasoManager.selectByApp(conn, tituloAplicacion);
        String condicion = "";
        String tokenCond = "";
        int idCaso = -1;
        for (int cntTC = 0; cntTC < tc.length; cntTC++) {
            condicion = condicion + tokenCond + tc[cntTC].getIdTC();
            tokenCond = ",";
        }
        condicion = "(" + condicion + ")";
        try {
            StringBuilder queryFindCaso = new StringBuilder();
            queryFindCaso.append("SELECT	ID_CASO ");
            queryFindCaso.append("  FROM	cg_caso WITH(nolock) ");
            queryFindCaso.append(" WHERE	id_tc IN").append(condicion).append(" ");
            queryFindCaso.append("   AND	c_folio LIKE '%-%-' + ? ");
            psBuscaCaso = conn.prepareStatement(queryFindCaso.toString());
            psBuscaCaso.setString(1, String.valueOf(folio));
            rsBuscaCaso = psBuscaCaso.executeQuery();
            if (rsBuscaCaso.next())
                idCaso = rsBuscaCaso.getInt(1);
            return idCaso;
        } finally {
            CloseObject.closeObject(rsBuscaCaso);
            CloseObject.closeObject(psBuscaCaso);
        }
    }

    private static int getNextIdCaso(Connection conn) throws SQLException {
        CFSequenceManager sm = CFSequenceManager.getInstance();
        return sm.nextVal(conn, "ID_CASO");
    }

    public static int insert(Connection conn, Caso c) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("INSERT INTO cg_caso " + "(id_caso, c_folio, id_tc, c_fecha_ini, c_tiempo_limite, c_alarma, c_id_gabinete, c_status) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            pstmnt.setInt(1, c.getIdCaso());
            pstmnt.setString(2, c.getFolio());
            pstmnt.setInt(3, c.getIdTC());
            pstmnt.setTimestamp(4, c.getFechaInicio());
            pstmnt.setInt(5, c.getTiempoLimite());
            pstmnt.setString(6, c.getAlarma());
            pstmnt.setInt(7, c.getIdGabinete());
            pstmnt.setInt(8, c.getStatus());
            retval = pstmnt.executeUpdate();
            // Ethiel, para conservar concecutivo del folio, deberia ser un
            // trigger si hay tiempo hacerlo
            // pstmnt = conn.prepareStatement("INSERT INTO cg_caso_folio (id_tc)
            // values (?)");
            // pstmnt.setInt(1, c.getIdTC());
            // retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static Caso nuevoCaso(Connection conn, String username, String ur, int id_tc) throws SQLException, GestionException {
        Caso c = null;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("SELECT * FROM cg_tipo_caso with(nolock) WHERE id_tc = ?");
            pstmnt.setInt(1, id_tc);
            ResultSet rs = pstmnt.executeQuery();
            if (rs.next()) {
                c = new Caso();
                c.setIdCaso(getNextIdCaso(conn));
                c.setIdTC(rs.getInt("id_tc"));
                c.setFechaInicio(new Timestamp(System.currentTimeMillis()));
                c.setTiempoLimite(rs.getInt("tc_tiempo_limite"));
                c.setAlarma(rs.getString("tc_alarma"));
                c.setIdGabinete(-1);
                c.setStatus(Caso.CREATED | Caso.EXECUTED);
                TipoCaso tc = new TipoCaso();
                tc.setIdTC(rs.getInt("id_tc"));
                c.setTipoCaso(TipoCasoManager.select(conn, tc));
                if (insert(conn, c) <= 0)
                    throw new SQLException("No se pudo salvar el caso iniciado (" + id_tc + ")");
                Operacion o = OperacionManager.primeraOperacion(conn, id_tc);
                if (o == null)
                    throw new NullPointerException("No se encontro la primera operacion del tipo caso (" + id_tc + ")");
                CasoOperacion co = CasoOperacionManager.nuevoCasoOperacion(conn, username, null, c, o);
                if (co == null)
                    throw new SQLException("No se logro crear el caso operacion del caso iniciado (" + id_tc + ")");
                if (CasoOperacionManager.insert(conn, co) <= 0)
                    throw new SQLException("No se pudo salvar el caso operacion iniciado (" + id_tc + ")");
                c.setCasoOperacion(co);
                c.setCasoDato(CasoDatoManager.createCasoDato(conn, id_tc, c.getIdCaso()));
                c.setFolio(DefaultFolioGenerator.getNextFolioDes(conn, ur, c));
                update(conn, c);
            }
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return c;
    }

    public static Caso nuevoCaso(Connection conn, Usuario u, int id_tc, FolioGeneratorInterface fg) throws SQLException, GestionException {
        Caso c = null;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("SELECT * FROM cg_tipo_caso with(nolock) WHERE id_tc = ?");
            pstmnt.setInt(1, id_tc);
            ResultSet rs = pstmnt.executeQuery();
            if (rs.next()) {
                c = new Caso();
                c.setIdCaso(getNextIdCaso(conn));
                c.setIdTC(rs.getInt("id_tc"));
                c.setFechaInicio(new Timestamp(System.currentTimeMillis()));
                c.setTiempoLimite(rs.getInt("tc_tiempo_limite"));
                c.setAlarma(rs.getString("tc_alarma"));
                c.setIdGabinete(-1);
                c.setStatus(Caso.CREATED | Caso.EXECUTED);
                TipoCaso tc = new TipoCaso();
                tc.setIdTC(rs.getInt("id_tc"));
                c.setTipoCaso(TipoCasoManager.select(conn, tc));
                if (insert(conn, c) <= 0)
                    throw new SQLException("No se pudo salvar el caso iniciado (" + id_tc + ")");
                Operacion o = OperacionManager.primeraOperacion(conn, id_tc);
                if (o == null)
                    throw new NullPointerException("No se encontro la primera operacion del tipo caso (" + id_tc + ")");
                CasoOperacion co = CasoOperacionManager.nuevoCasoOperacion(conn, u.getNombre(), null, c, o);
                if (co == null)
                    throw new SQLException("No se logro crear el caso operacion del caso iniciado (" + id_tc + ")");
                if (CasoOperacionManager.insert(conn, co) <= 0)
                    throw new SQLException("No se pudo salvar el caso operacion iniciado (" + id_tc + ")");
                c.setCasoOperacion(co);
                c.setCasoDato(CasoDatoManager.createCasoDato(conn, id_tc, c.getIdCaso()));
                // c.setFolio(fg.getNextFolio(conn, u.getLogin(), c));
                c.setFolio(fg.getNextFolioUsr(conn, u, c));
                update(conn, c);
            }
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return c;
    }

    public static int obtieneIdCasoContDiv(Connection conn, String cIdcontrato) throws Exception {
        Caso c = null;
        String query = "select caso.ID_CASO from cg_caso caso with(nolock) " + " where c_folio like'%CDIV-%-'+convert(varchar,isnull((select id_caso from pContratoDiverso with(nolock)  where cIdContrato=?),-1)) ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            int idCaso = -1;
            ps = conn.prepareStatement(query);
            ps.setString(1, cIdcontrato);
            rs = ps.executeQuery();
            if (rs.next())
                idCaso = rs.getInt(1);
            else
                throw new Exception("No se encontro el idCaso para el contrato " + cIdcontrato);
            return idCaso;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static void revisaTiempoLimiteDeCasos(Connection conn, String prefixPath) throws IOException, SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        if (log.isDebugEnabled())
            log.debug("Entrando...");
        try {
            String whereStatement;
            String dbmsName = conn.getMetaData().getDatabaseProductName();
            int currentDbms = DBMS.searchDBMSByName(dbmsName);
            switch(currentDbms) {
                case DBMS.DB2:
                    whereStatement = "WHERE (c_fecha_ini + c_tiempo_limite HOURS) <= ?";
                    break;
                case DBMS.ORACLE:
                    whereStatement = "WHERE (c_fecha_ini + (c_tiempo_limite/24)) <= ?";
                    break;
                case DBMS.ANTS:
                case DBMS.INFORMIX:
                case DBMS.ISERIES:
                case DBMS.SQLANYWHERE:
                case DBMS.SQLSERVER:
                    whereStatement = "WHERE (c_fecha_ini + (c_tiempo_limite/24)) <= ?";
                    break;
                case DBMS.SYBASE:
                default:
                    throw new RuntimeException("JDBC Driver \"" + dbmsName + "\" no implementado");
            }
            pstmnt = conn.prepareStatement("SELECT id_caso FROM cg_caso with(nolock) " + whereStatement);
            switch(currentDbms) {
                case DBMS.DB2:
                case DBMS.ORACLE:
                    pstmnt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    break;
                case DBMS.ANTS:
                case DBMS.INFORMIX:
                case DBMS.ISERIES:
                case DBMS.SQLANYWHERE:
                case DBMS.SQLSERVER:
                    pstmnt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    break;
                case DBMS.SYBASE:
                default:
                    throw new RuntimeException("JDBC Driver \"" + dbmsName + "\" no implementado");
            }
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                Caso c = new Caso();
                c.setIdCaso(rs.getInt(1));
                c = CasoManager.select(conn, c);
                log.debug("Object: {}", "Procesando alarma del Id Caso = (" + c.getIdCaso() + ")");
                if ((c.getStatus() & Caso.MSG_SENDED) == Caso.MSG_SENDED)
                    continue;
                c.setStatus(c.getStatus() | Caso.MSG_SENDED);
                CasoManager.update(conn, c);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        if (log.isDebugEnabled())
            log.debug("Saliendo");
    }

    public static Caso select(Connection conn, int idCaso) throws SQLException {
        Caso c = new Caso();
        c.setIdCaso(idCaso);
        return select(conn, c);
    }

    public static Caso select(Connection conn, Caso c) throws SQLException {
        Caso rc = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            StringBuffer where = new StringBuffer();
            String token = " WHERE ";
            if (c.getIdCaso() > 0) {
                where.append(token + "id_caso = ?");
                token = " AND ";
            }
            if (c.getFolio() != null) {
                where.append(token + "c_folio = ?");
                token = " AND ";
            }
            if (c.getIdTC() > 0) {
                where.append(token + "id_tc = ?");
                token = " AND ";
            }
            // if (c.getFechaInicio() != null) {
            // where.append(token + "c_fecha_ini = ?");
            // token = " AND ";
            // }
            //
            // if (c.getTiempoLimite() > 0) {
            // where.append(token + "c_tiempo_limite = ?");
            // token = " AND ";
            // }
            // if (c.getAlarma() != null) {
            // where.append(token + "c_alarma = ?");
            // token = " AND ";
            // }
            if (c.getIdGabinete() > 0) {
                where.append(token + "c_id_gabinete = ?");
                token = " AND ";
            }
            // if (c.getStatus() > 0) {
            // where.append(token + "c_status = ?");
            // token = " AND ";
            // }
            pstmnt = conn.prepareStatement("SELECT * FROM cg_caso with(nolock) " + where.toString());
            int i = 1;
            if (c.getIdCaso() > 0)
                pstmnt.setInt(i++, c.getIdCaso());
            if (c.getFolio() != null)
                pstmnt.setString(i++, c.getFolio());
            if (c.getIdTC() > 0)
                pstmnt.setInt(i++, c.getIdTC());
            // if (c.getFechaInicio() != null)
            // pstmnt.setTimestamp(i++, c.getFechaInicio());
            // if (c.getTiempoLimite() > 0)
            // pstmnt.setInt(i++, c.getTiempoLimite());
            //
            // if (c.getAlarma() != null)
            // pstmnt.setString(i++, c.getAlarma());
            if (c.getIdGabinete() > 0)
                pstmnt.setInt(i++, c.getIdGabinete());
            // if (c.getStatus() > 0)
            // pstmnt.setInt(i++, c.getStatus());
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                rc = new Caso();
                rc.setIdCaso(rs.getInt("id_caso"));
                rc.setFolio(rs.getString("c_folio"));
                rc.setIdTC(rs.getInt("id_tc"));
                rc.setFechaInicio(rs.getTimestamp("c_fecha_ini"));
                rc.setTiempoLimite(rs.getInt("c_tiempo_limite"));
                rc.setAlarma(rs.getString("c_alarma"));
                rc.setIdGabinete(rs.getInt("c_id_gabinete"));
                rc.setStatus(rs.getInt("c_status"));
                TipoCaso tc = new TipoCaso();
                tc.setIdTC(rc.getIdTC());
                rc.setTipoCaso(TipoCasoManager.select(conn, tc));
                CasoDato cd = new CasoDato();
                cd.setIdCaso(rc.getIdCaso());
                rc.setCasoDato(CasoDatoManager.select(conn, cd));
                CasoOperacion co = new CasoOperacion();
                co.setIdCaso(rc.getIdCaso());
                rc.setCasoOperacion(CasoOperacionManager.select(conn, co));
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return rc;
    }

    public static Caso select(Connection conn, Caso c, int id_caso_oper) throws SQLException {
        Caso rc = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            StringBuffer where = new StringBuffer();
            String token = " WHERE ";
            if (c.getIdCaso() > 0) {
                where.append(token + "id_caso = ?");
                token = " AND ";
            }
            if (c.getFolio() != null) {
                where.append(token + "c_folio = ?");
                token = " AND ";
            }
            if (c.getIdTC() > 0) {
                where.append(token + "id_tc = ?");
                token = " AND ";
            }
            if (c.getFechaInicio() != null) {
                where.append(token + "c_fecha_ini = ?");
                token = " AND ";
            }
            if (c.getTiempoLimite() > 0) {
                where.append(token + "c_tiempo_limite = ?");
                token = " AND ";
            }
            if (c.getAlarma() != null) {
                where.append(token + "c_alarma = ?");
                token = " AND ";
            }
            if (c.getIdGabinete() > 0) {
                where.append(token + "c_id_gabinete = ?");
                token = " AND ";
            }
            if (c.getStatus() > 0) {
                where.append(token + "c_status = ?");
                token = " AND ";
            }
            pstmnt = conn.prepareStatement("SELECT * FROM cg_caso with(nolock) " + where.toString());
            int i = 1;
            if (c.getIdCaso() > 0)
                pstmnt.setInt(i++, c.getIdCaso());
            if (c.getFolio() != null)
                pstmnt.setString(i++, c.getFolio());
            if (c.getIdTC() > 0)
                pstmnt.setInt(i++, c.getIdTC());
            if (c.getFechaInicio() != null)
                pstmnt.setTimestamp(i++, c.getFechaInicio());
            if (c.getTiempoLimite() > 0)
                pstmnt.setInt(i++, c.getTiempoLimite());
            if (c.getAlarma() != null)
                pstmnt.setString(i++, c.getAlarma());
            if (c.getIdGabinete() > 0)
                pstmnt.setInt(i++, c.getIdGabinete());
            if (c.getStatus() > 0)
                pstmnt.setInt(i++, c.getStatus());
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                rc = new Caso();
                rc.setIdCaso(rs.getInt("id_caso"));
                rc.setFolio(rs.getString("c_folio"));
                rc.setIdTC(rs.getInt("id_tc"));
                rc.setFechaInicio(rs.getTimestamp("c_fecha_ini"));
                rc.setTiempoLimite(rs.getInt("c_tiempo_limite"));
                rc.setAlarma(rs.getString("c_alarma"));
                rc.setIdGabinete(rs.getInt("c_id_gabinete"));
                rc.setStatus(rs.getInt("c_status"));
                TipoCaso tc = new TipoCaso();
                tc.setIdTC(rc.getIdTC());
                rc.setTipoCaso(TipoCasoManager.select(conn, tc));
                CasoDato cd = new CasoDato();
                cd.setIdCaso(rc.getIdCaso());
                rc.setCasoDato(CasoDatoManager.select(conn, cd));
                CasoOperacion co = new CasoOperacion();
                co.setIdCaso(rc.getIdCaso());
                co.setIdCasoOper(id_caso_oper);
                rc.setCasoOperacion(CasoOperacionManager.select(conn, co));
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return rc;
    }

    public static Caso[] select(Connection conn, int id_oper, String co_responsable, Date co_fecha_ini) throws SQLException {
        Caso[] rc = new Caso[0];
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        ArrayList casos = new ArrayList();
        try {
            String sSQL = "select cc.* from cg_caso cc with(nolock), cg_caso_operacion cco with(nolock) " + " where cco.co_responsable = ?" + " and cco.id_oper = ?" + " and cco.co_fecha_ini <= ?" + " and cco.id_caso = cc.id_caso";
            pstmnt = conn.prepareStatement(sSQL);
            pstmnt.setString(1, co_responsable);
            pstmnt.setInt(2, id_oper);
            pstmnt.setDate(3, new java.sql.Date(co_fecha_ini.getTime()));
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                Caso c = new Caso();
                c.setIdCaso(rs.getInt("id_caso"));
                c.setFolio(rs.getString("c_folio"));
                c.setIdTC(rs.getInt("id_tc"));
                c.setFechaInicio(rs.getTimestamp("c_fecha_ini"));
                c.setTiempoLimite(rs.getInt("c_tiempo_limite"));
                c.setAlarma(rs.getString("c_alarma"));
                c.setIdGabinete(rs.getInt("c_id_gabinete"));
                c.setStatus(rs.getInt("c_status"));
                TipoCaso tc = new TipoCaso();
                tc.setIdTC(c.getIdTC());
                c.setTipoCaso(TipoCasoManager.select(conn, tc));
                CasoDato cd = new CasoDato();
                cd.setIdCaso(c.getIdCaso());
                c.setCasoDato(CasoDatoManager.select(conn, cd));
                CasoOperacion co = new CasoOperacion();
                co.setIdCaso(c.getIdCaso());
                c.setCasoOperacion(CasoOperacionManager.select(conn, co));
                casos.add(c);
            }
            rc = new Caso[casos.size()];
            casos.toArray(rc);
        } catch (Exception exc) {
            exc.printStackTrace(System.out);
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return rc;
    }

    public static Caso select(Connection conn, String tituloAplicacion, int idGabinete) throws Exception {
        Caso c = null;
        String query = "SELECT folio FROM imx" + tituloAplicacion + " WITH(nolock) WHERE id_gabinete = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String folio;
            ps = conn.prepareStatement(query);
            ps.setInt(1, idGabinete);
            rs = ps.executeQuery();
            if (rs.next())
                folio = rs.getString(1);
            else
                throw new Exception("No se encontro informacion para el tramite [" + tituloAplicacion + "] con gabinete [" + idGabinete + "]");
            c = new Caso();
            c.setFolio(folio);
            return select(conn, c);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static void terminaCaso(Connection conn, Caso c, String[] resp, String[] oper) throws SQLException {
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("DELETE FROM cg_caso_dato with(rowlock) WHERE id_caso = ?");
            pstmnt.setInt(1, c.getIdCaso());
            pstmnt.executeUpdate();
            System.out.println("LONGITUD CASO OPERACION: " + c.getCasoOperacion().size());
            for (int i = 0; i < c.getCasoOperacion().size(); i++) {
                CasoOperacion o = c.getCasoOperacion(i);
                CasoOperacionManager.delete(conn, o.getIdCaso(), o.getIdCasoOper());
            }
            pstmnt = conn.prepareStatement("DELETE FROM cg_caso with(rowlock) WHERE id_caso = ?");
            pstmnt.setInt(1, c.getIdCaso());
            pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
    }

    public static int update(Connection conn, Caso c) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("UPDATE cg_caso with(rowlock) SET c_folio = ?, id_tc = ?, c_fecha_ini = ?" + ", c_tiempo_limite = ?, c_alarma = ?, c_id_gabinete = ?, c_status = ? WHERE id_caso = ?");
            pstmnt.setString(1, c.getFolio());
            pstmnt.setInt(2, c.getIdTC());
            pstmnt.setTimestamp(3, c.getFechaInicio());
            pstmnt.setInt(4, c.getTiempoLimite());
            pstmnt.setString(5, c.getAlarma());
            pstmnt.setInt(6, c.getIdGabinete());
            pstmnt.setInt(7, c.getStatus());
            pstmnt.setInt(8, c.getIdCaso());
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }
}
