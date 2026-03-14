package com.syc.sai.procesos;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.BitacoraManager;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.CasoOperacionManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Operacion;
import com.syc.gestion.core.OperacionManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneraCasosViaticosMigracion {

    private static String OPER_CONTINUE = "CONTINUAR";

    private static String CASO_END = "TERMINAR";

    private static final Logger log = LoggerFactory.getLogger(GeneraCasosViaticosMigracion.class);

    public static void main(String[] args) throws Exception {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String url = "jdbc:sqlserver://10.0.0.194;database=sai_2023;autoReconnect=false";
        String usr = "sai";
        String pwd = "S412020admin";
        String queryInsertDato = "INSERT INTO cg_Caso_dato(ID_CASO, ID_CD, ID_TC, CD_VALOR) " + "SELECT ?, 1, 73, ? " + " UNION " + "SELECT ?, 2, 73, CONVERT(VARCHAR(32), GETDATE(), 103) " + "UNION " + "SELECT ?, 3, 73, '2023' " + "UNION " + "SELECT ?, 4, 73, 'SAI SAI SAI' " + "UNION " + "SELECT ?, 5, 73, 'Documentación Comisión Viaticos' " + "UNION " + "SELECT ?, 6, 73, 'MXP' " + "UNION " + "SELECT ?, 7, 73, CONVERT(VARCHAR(32), GETDATE(), 103) " + "UNION " + "SELECT ?, 8, 73, 'false' " + "UNION " + "SELECT ?, 9, 73, '' " + "UNION " + "SELECT ?, 10, 73, '' " + "UNION " + "SELECT ?, 11, 73, '' ";
        String query = "select * from tComision WHERE nIdComision BETWEEN 2183   AND 7765";
        String queryNextGav = "select MAX(id_gabinete) + 1 from IMXVIATICOS";
        String queryInsertCaso = "INSERT INTO CG_CASO(ID_CASO ,C_FOLIO ,ID_TC ,C_FECHA_INI ,C_TIEMPO_LIMITE ,C_ID_GABINETE ,C_STATUS)" + "VALUES (? ,? ,73,GETDATE() ,-1 ,? ,3)";
        String queryInsertGaveta = "INSERT INTO IMXVIATICOS(ID_GABINETE, ACTIVO, FOLIO, FECHA_DOCUMENTO, EJERCICIO_FISCAL, OPERADOR, UNIDADEJECUTORA, USUARIO_CAPTURA)" + "VALUES(?, 'N', ?, GETDATE(), '2023', 'SAI SAI SAI', ?, 'SAI')";
        String queryInsertOperacion = "INSERT INTO CG_CASO_OPERACION(ID_CASO, ID_CASO_OPER, ID_TC, ID_OPER, CO_FECHA_INI, CO_TIEMPO_LIMITE, CO_RESPONSABLE, CO_STATUS)" + "VALUES (? ,1 ,73 ,4 ,getdate() ,-1, 'CONSULTA_VIATICOS', 1)";
        PreparedStatement pstmntFolder = null;
        PreparedStatement ps = null;
        PreparedStatement psInsertCaso = null;
        PreparedStatement psInsertGaveta = null;
        PreparedStatement psNextGav = null;
        PreparedStatement psInsertOperacion = null;
        PreparedStatement psInsertDato = null;
        ResultSet rs = null;
        ResultSet rsNextVal = null;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, usr, pwd);
            conn.setAutoCommit(false);
            psInsertGaveta = conn.prepareStatement(queryInsertGaveta);
            psInsertCaso = conn.prepareStatement(queryInsertCaso);
            psInsertOperacion = conn.prepareStatement(queryInsertOperacion);
            psNextGav = conn.prepareStatement(queryNextGav);
            psInsertDato = conn.prepareStatement(queryInsertDato);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            CFSequenceManager sequence = CFSequenceManager.getInstance(null);
            pstmntFolder = conn.prepareStatement("INSERT INTO imx_carpeta " + "(titulo_aplicacion, id_gabinete, id_carpeta, nombre_carpeta, nombre_usuario, " + "bandera_raiz, fh_creacion, fh_modificacion, numero_accesos, numero_carpetas, " + "numero_documentos, descripcion, password) " + "VALUES (?,?,0,?,?,'S',?,?,0,0,0,'Módulo de Control de Gestion','-1')");
            while (rs.next()) {
                String ue = rs.getString("cUnidadResponsable");
                int idCaso = sequence.nextVal(conn, "ID_CASO");
                rsNextVal = psNextGav.executeQuery();
                rsNextVal.next();
                int idGabinete = rsNextVal.getInt(1);
                rsNextVal.close();
                String folioCaso = "VIAT-" + ue + "-" + rs.getInt("nIdComision");
                psInsertGaveta.setInt(1, idGabinete);
                psInsertGaveta.setString(2, folioCaso);
                psInsertGaveta.setString(3, ue);
                psInsertGaveta.executeUpdate();
                psInsertCaso.setInt(1, idCaso);
                psInsertCaso.setString(2, folioCaso);
                psInsertCaso.setInt(3, idGabinete);
                psInsertCaso.executeUpdate();
                psInsertOperacion.setInt(1, idCaso);
                psInsertOperacion.executeUpdate();
                psInsertDato.setInt(1, idCaso);
                psInsertDato.setString(2, folioCaso);
                psInsertDato.setInt(3, idCaso);
                psInsertDato.setInt(4, idCaso);
                psInsertDato.setInt(5, idCaso);
                psInsertDato.setInt(6, idCaso);
                psInsertDato.setInt(7, idCaso);
                psInsertDato.setInt(8, idCaso);
                psInsertDato.setInt(9, idCaso);
                psInsertDato.setInt(10, idCaso);
                psInsertDato.setInt(11, idCaso);
                psInsertDato.setInt(12, idCaso);
                psInsertDato.executeUpdate();
                pstmntFolder.setString(1, "VIATICOS");
                pstmntFolder.setInt(2, idGabinete);
                pstmntFolder.setString(3, folioCaso);
                pstmntFolder.setString(4, "sai");
                Timestamp t = new Timestamp(System.currentTimeMillis());
                pstmntFolder.setTimestamp(5, t);
                pstmntFolder.setTimestamp(6, t);
                pstmntFolder.executeUpdate();
                AplicacionManager.creaEstructuraCarpeta(conn, "VIATICOS", idGabinete, "sai", folioCaso);
            }
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (conn != null)
                Util.rollback(conn);
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(psInsertCaso);
            CloseObject.closeObject(psInsertGaveta);
            CloseObject.closeObject(psNextGav);
            CloseObject.closeObject(psInsertOperacion);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rsNextVal);
        }
    }

    private Caso iniciaCaso(Connection conn, Usuario u, String tCaso) throws GestionException, SQLException {
        int idTC = Integer.parseInt(tCaso);
        Caso c = null;
        FolioGeneratorInterface fg = null;
        try {
            ClassLoader cl = getClass().getClassLoader();
            Class clase = cl.loadClass("com.syc.gestion.custom.DefaultFolioGenerator");
            fg = (FolioGeneratorInterface) clase.newInstance();
        } catch (ClassNotFoundException exc) {
            System.out.println("Generador de folios" + exc);
            throw new GestionException(exc);
        } catch (InstantiationException exc) {
            System.out.println("Generador de folios" + exc);
            throw new GestionException(exc);
        } catch (IllegalAccessException exc) {
            System.out.println("Generador de folios" + exc);
            throw new GestionException(exc);
        }
        c = CasoManager.nuevoCaso(conn, u, idTC, fg);
        if (c == null) {
            System.out.println("No se logro crear el caso");
            throw new GestionException("No se logró crear el caso");
        }
        return c;
    }

    public synchronized Caso avanzaCaso(Caso c, String u_login, String observ, String[] resp, String[] oper, Map data, String pathPrefix) throws GestionException, ClassNotFoundException {
        Caso rco = null;
        Connection conn = null;
        boolean delete = true;
        if (resp.length != oper.length) {
            System.out.println("Las dimensiones no coinciden rep[" + resp.length + "] vs oper[" + oper.length + "]");
            throw new GestionException("Las dimensiones no coinciden rep[" + resp.length + "] vs oper[" + oper.length + "]");
        }
        try {
            int[] idCasoOperSgte = new int[resp.length];
            Class.forName("net.sourceforge.jtds.jdbcx.JtdsDataSource");
            String url = "jdbc:jtds:sqlserver://10.0.0.194:1433/sai_2015;autoReconnect=false";
            String usr = "sai";
            String pwd = "S41produccion";
            conn = DriverManager.getConnection(url, usr, pwd);
            conn.setAutoCommit(false);
            CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), data);
            for (int i = 0; i < resp.length; i++) {
                if (OPER_CONTINUE.equalsIgnoreCase(oper[i].trim())) {
                    // Ethiel, (mas abajo hace algunas
                    c.setStatus(100);
                    // operaciones y lo deja como 98) le
                    // ponemos un estatus muy alto para
                    // reconocerlo en el inbox.
                    CasoManager.update(conn, c);
                    // Ethiel,
                    CasoOperacionManager.updateObservacion(conn, c, observ);
                    // se
                    // creo
                    // este
                    // metodo
                    // porque
                    // cuando
                    // es
                    // la
                    // ultima
                    // operacion
                    // nunca
                    // guardaba
                    // observaciones
                    // Ethiel, simpre lo borraba aunque fuera
                    delete = false;
                    // CONTINUAR
                    continue;
                } else if (CASO_END.equalsIgnoreCase(oper[i].trim())) {
                    delete = false;
                    for (int j = i + 1; j < idCasoOperSgte.length; j++) idCasoOperSgte[j] = -1;
                    BitacoraManager.registraCasoOperacion(conn, u_login, c, idCasoOperSgte, resp, oper, true);
                    CasoManager.terminaCaso(conn, c, resp, oper);
                    // TipoCasoInterface tci = null;
                    if (c.getTipoCaso().tieneInterface()) {
                        // tci =
                        // instanceTipoCasoInterface(c.getTipoCaso().getInterface());
                        // tci.onTerminaCaso(conn, u_login, c, "", resp, oper,
                        // data);
                    }
                    break;
                }
                Operacion o = new Operacion();
                o.setIdTC(c.getIdTC());
                o.setNombre(oper[i].trim());
                o = OperacionManager.select(conn, o);
                if (o == null) {
                    System.out.println("No se localizo la Operacion \"" + oper[i] + "\"");
                    throw new GestionException("No se localizo la Operación \"" + oper[i] + "\"");
                }
                CasoOperacion co = CasoOperacionManager.nuevoCasoOperacion(conn, resp[i].trim(), observ, c, o);
                // Se comentan esta linea para evitar los duplicados en el inbox
                CasoOperacionManager.insert(conn, co);
                /*
				 * //Intento para evitar repetidos idCasoOperSgte[i] =
				 * co.getIdCasoOper(); //Se usara mejor el co actual y le
				 * cambiaremos los datos que queremos
				 * co.setIdCasoOper(c.getCasoOperacion(0).getIdCasoOper()); //En
				 * lugar de hacer insert se hara update
				 * CasoOperacionManager.update(conn, co);
				 */
                idCasoOperSgte[i] = co.getIdCasoOper();
            }
            if (delete) {
                BitacoraManager.registraCasoOperacion(conn, u_login, c, idCasoOperSgte, resp, oper);
                // Se comenta el delete ya que para evitar duplicados en inbox
                // se cambio el inser por un update
                CasoOperacionManager.delete(conn, c.getIdCaso(), c.getCasoOperacion(0).getIdCasoOper());
            }
            if ((c.getStatus() & Caso.MSG_SENDED) == Caso.MSG_SENDED)
                c.setStatus(c.getStatus() ^ Caso.MSG_SENDED);
            c.setStatus(c.getStatus() ^ Caso.EXECUTED);
            CasoManager.update(conn, c);
            conn.commit();
        } catch (SQLException exc) {
            log.error(exc.getMessage(), exc);
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println("En rollback" + ex);
            }
            System.out.println("No se logro avanzar el caso (" + c.getIdCaso() + ", " + c.getCasoOperacion(0).getIdCasoOper() + ")" + exc);
            throw new GestionException("No se logro avanzar el caso (" + c.getIdCaso() + ", " + c.getCasoOperacion(0).getIdCasoOper() + ")", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                System.out.println("Cerrando conexion a base de datos" + exc);
            }
            conn = null;
        }
        return rco;
    }
}
