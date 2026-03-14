package com.syc.obrapublica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObraPublicaEPManager {

    private static final Logger log = LoggerFactory.getLogger(ObraPublicaEPManager.class);

    public static List<String> getEPsSinOli(Connection conn, String cartera, String ur, String ue, String folioSAI) throws Exception {
        ResultSet rsOli = null;
        PreparedStatement psOli = null;
        List<String> eps = new ArrayList<String>();
        try {
            //String queryOli = "SELECT SUBSTRING(vsa.ep, 1, 55) descEP, SUBSTRING(vsa.ep, 1, 55) idEP FROM vsaldosanuales vsa WITH(NOLOCK) WHERE vsa.ccartera = ? and ncuentap = 82106";
            String queryOli = "SELECT SUBSTRING(vsa.ep, 1, 55) descEP, SUBSTRING(vsa.ep, 1, 55) idEP FROM vsaldosanuales vsa WITH(NOLOCK), tCatalogoEP cep WITH(NOLOCK) WHERE vsa.EP = cep.EP and  vsa.ccartera = ? and ncuentap = 82106  and cep.cPartida like '6%'  and MontoAnual != 0 ";
            queryOli += " and vsa.EP in (select distinct (select ef.aEjercicioFiscal from tEjercicioFiscal ef where ef.cActivo  = 1) + SUBSTRING(ep,5,100) from tObraPublicaCompromisoDetalle d, tObraPublicaCompromisoEncabezado e ";
            queryOli += " where e.nFolioOPComHeader = d.nFolioOPComHeader and e.FolioSAI = ?) ";
            queryOli = " SELECT SUBSTRING(vsa.ep, 1, 55) descEP, SUBSTRING(vsa.ep, 1, 55) idEP ";
            queryOli += "  FROM vsaldosanuales vsa WITH(NOLOCK), tCatalogoEP cep WITH(NOLOCK) WHERE vsa.EP = cep.EP  ";
            queryOli += "   and  vsa.ccartera = ?  ";
            queryOli += "   and ncuentap = 82106  and cep.cPartida like '6%'  and MontoAnual != 0  ";
            queryOli += "    and vsa.EP in ( ";
            queryOli += "       select distinct (select ef.aEjercicioFiscal from tEjercicioFiscal ef with (nolock) where ef.cActivo  = 1)  ";
            queryOli += "         + SUBSTRING(ep,5,100) from tObraPublicaCompromisoDetalle d with (nolock) , tObraPublicaCompromisoEncabezado e with (nolock)  ";
            queryOli += "         where e.nFolioOPComHeader = d.nFolioOPComHeader  ";
            queryOli += "         and e.FolioSAI = ? ";
            queryOli += "       union  ";
            queryOli += "       select distinct mapeo.EP from tObraPublicaCompromisoDetalle d with (nolock) ";
            queryOli += "        , tObraPublicaCompromisoEncabezado e with (nolock)  ";
            queryOli += "        , (select distinct cep.ep, replace(cep.EP,'.'+cProgramaNuevo+'.', '.'+cProgramaAnterior+'.'  ) epAnterior ";
            queryOli += "            from tCatalogoEP cep with (nolock), tOPMapeoCambioPrograma cp with (nolock) ";
            queryOli += "           where cp.aEjercicioFiscal = (select ef.aEjercicioFiscal from tEjercicioFiscal ef with (nolock) where ef.cActivo  = 1) and cp.cProgramaNuevo = cep.cProgramaPresupuestario ";
            queryOli += "          ) mapeo ";
            queryOli += "        where e.nFolioOPComHeader = d.nFolioOPComHeader  ";
            queryOli += "         and mapeo.epAnterior =  (select ef.aEjercicioFiscal from tEjercicioFiscal ef with (nolock) where ef.cActivo  = 1)  ";
            queryOli += "         + SUBSTRING(d.ep,5,100) ";
            queryOli += "         and e.FolioSAI = ? ";
            queryOli += "    ) ";
            psOli = conn.prepareStatement(queryOli);
            psOli.setString(1, cartera);
            psOli.setString(2, folioSAI);
            psOli.setString(3, folioSAI);
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

    public static List<String> getCarterasSinOli(Connection conn, String ur, String ue) throws Exception {
        ResultSet rsOli = null;
        PreparedStatement psOli = null;
        List<String> eps = new ArrayList<String>();
        try {
            String queryOli = "SELECT distinct cep.cCartera  descEP, cep.cCartera idEP FROM vsaldosanuales vsa WITH(NOLOCK), tCatalogoEP cep WITH(NOLOCK) WHERE vsa.EP = cep.EP and ncuentap = 82106  and cep.cPartida like '6%'  and MontoAnual != 0";
            psOli = conn.prepareStatement(queryOli);
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

    public static List<String> getEPFromCarteras(Connection conn, List<String> clavesPresupuestales, String ef, String cartera, String oli, String ur, String ue, String SinOli) throws Exception {
        List<String> eps = new ArrayList<String>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT ep AS epID FROM vsaldosanuales WITH(NOLOCK) WHERE ncuentap = 82106";
        String claves = "";
        String tokenClaves = "";
        String condicion = "";
        try {
            if (clavesPresupuestales != null && clavesPresupuestales.size() > 0) {
                for (Iterator<String> i = clavesPresupuestales.iterator(); i.hasNext(); ) {
                    claves += tokenClaves + " ep LIKE '" + i.next() + "." + ue + "%' ";
                    tokenClaves = "OR";
                }
                condicion = " AND (" + claves + ")";
                query += condicion;
                log.debug("Object: {}", query.toString());
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

    public static List<String> getEPFromVistas(Connection conn, String usuario, String ef, String cartera, String oli, String ur, String ue) throws Exception {
        List<String> eps = new ArrayList<String>();
        PreparedStatement ps = null;
        ResultSet rsEPS = null;
        String query = "SELECT ep AS epID " + "FROM   vsaldosanuales WITH(nolock) " + "WHERE  ncuentap = '82106' " + "       AND cpartida IN (SELECT cpartida " + "                        FROM   dbo.toppartidasinoli partidas WITH( nolock ) " + "                               INNER JOIN dbo.tvistasur vistas WITH( nolock ) " + "                                       ON partidas.cunidadejecutora = vistas.ur " + "                        WHERE  vistas.modulo = 'OBRAPUBLICA' " + "                               AND vistas.usuario = ?) " + "       AND cunidadejecutora IN (SELECT ur " + "                                FROM   dbo.tvistasur " + "                                WHERE  modulo = 'OBRAPUBLICA' " + "                                       AND usuario = ?) ";
        try {
            log.debug("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, usuario);
            ps.setString(2, usuario);
            rsEPS = ps.executeQuery();
            while (rsEPS.next()) {
                eps.add(rsEPS.getString("epID"));
            }
            return eps;
        } finally {
            try {
                CloseObject.closeObject(rsEPS, false);
                CloseObject.closeObject(ps, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    public static List<String> getEPFromCarteras(Connection conn, List<String> clavesPresupuestales, String ef, String cartera, String oli, String ur, String ue) throws Exception {
        List<String> eps = new ArrayList<String>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT ep AS epID FROM vsaldosanuales WITH(NOLOCK) WHERE ncuentap = 82106";
        String claves = "";
        String tokenClaves = "";
        String condicion = "";
        try {
            if (clavesPresupuestales != null && clavesPresupuestales.size() > 0) {
                for (Iterator<String> i = clavesPresupuestales.iterator(); i.hasNext(); ) {
                    claves += tokenClaves + " ep LIKE '" + i.next() + "." + ue + "%' ";
                    tokenClaves = "OR";
                }
                condicion = " AND (" + claves + ")";
                query += condicion;
                log.debug("Object: {}", query.toString());
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

    public static List<String> getEPFromCarteras(Connection conn, String usuario, String ef, String cartera, String oli, String ur, String ue) throws Exception {
        List<String> eps = new ArrayList<String>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT ep AS epID FROM vsaldosanuales WITH(NOLOCK) WHERE ncuentap = '82106' and cPartida LIKE '6%' ";
        String claves = "";
        String tokenClaves = "";
        String condicion = "";
        try {
            claves += tokenClaves + " ep LIKE '" + ef + ".%" + cartera + "%.%'";
            condicion = " AND (" + claves + ")";
            query += condicion;
            log.debug("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                eps.add(rs.getString("epID"));
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

    public static boolean ueSinOLIS(Connection conn, String ue) throws Exception {
        String query = "SELECT Count(*) AS PartidasSinOLI " + "FROM   dbo.toppartidasinoli WITH(nolock) " + "WHERE  cunidadejecutora = ? ";
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, ue);
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1) > 0;
            else
                return false;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }
}
