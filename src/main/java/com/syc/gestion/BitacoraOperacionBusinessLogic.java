package com.syc.gestion;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.jenkov.prizetags.tree.impl.Tree;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.BitacoraOperacion;
import com.syc.gestion.core.BitacoraOperacionManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.TreeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitacoraOperacionBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(BitacoraOperacionBusinessLogic.class);

    public BitacoraOperacionBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public int getIdBitacora(int idTipoCaso, int idCaso, int idGabinete, int secuencialOperacion) throws GestionException {
        int retVal = -1;
        BitacoraOperacion bitoper = null;
        BitacoraOperacion bo = new BitacoraOperacion();
        Connection conn = null;
        try {
            conn = getConnection();
            bo.setIdTipoCaso(idTipoCaso);
            bo.setIdCaso(idCaso);
            bo.setIdGabinete(idGabinete);
            bo.setSecuencialOperacion(secuencialOperacion);
            bitoper = BitacoraOperacionManager.select(conn, bo);
            retVal = bitoper.getIdBitacora();
        } catch (SQLException exc) {
            log.warn("Obteniendo Bitacora Caso", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public BitacoraOperacion getBitacoraOperacion(int idCaso, int idSeq) throws GestionException {
        BitacoraOperacion retVal = new BitacoraOperacion();
        retVal.setIdCaso(idCaso);
        retVal.setSecuencialOperacion(idSeq);
        return getBitacoraOperacion(retVal);
    }

    public BitacoraOperacion getBitacoraOperacion(BitacoraOperacion bo) throws GestionException {
        BitacoraOperacion retVal = null;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = BitacoraOperacionManager.select(conn, bo);
        } catch (SQLException exc) {
            log.warn("Obteniendo Bitacora Caso", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public BitacoraOperacion[] getBitacoraOperaciones(int idCaso) throws GestionException {
        BitacoraOperacion[] retVal = null;
        Connection conn = null;
        try {
            conn = getConnection();
            BitacoraOperacion bo = new BitacoraOperacion();
            bo.setIdCaso(idCaso);
            retVal = BitacoraOperacionManager.selectAll(conn, bo);
        } catch (SQLException exc) {
            log.warn("Obteniendo Bitacora Caso", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int updateBitacoraOperacion(BitacoraOperacion bo) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = BitacoraOperacionManager.update(conn, bo);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
            log.warn("Actualizando Bitacora Operacion ", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int updateBitacoraOperacionUser(String oldUser, String newUser) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = BitacoraOperacionManager.updateUser(conn, oldUser, newUser);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
                log.warn("Actualizando Bitacora Operacion Usuario ", exc);
                throw new GestionException(exc);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public List getBitacoraOperacionPorUsuario(String u_login, int id_tc, int id_oper) throws GestionException {
        List l = new ArrayList();
        Connection conn = null;
        try {
            conn = getConnection();
            l = BitacoraOperacionManager.selectBitacoraOperacion(conn, u_login, id_tc, id_oper);
        } catch (SQLException exc) {
            log.warn("Obteniendo Caso Operacion por Usuario", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return l;
    }

    public ITree getArbolBitacoraOperacion(//BitacoraCaso bc,
    BitacoraOperacion bo) throws GestionException {
        Connection conn = null;
        ITree tree = new Tree();
        try {
            conn = getConnection();
            tree = TreeManager.getTree(conn, bo);
        } catch (SQLException exc) {
            log.error("Creando el Arbol del Caso", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Error cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return tree;
    }

    public String getGestionXML(String tituloAplicacion, String folio, String realPath) throws GestionException {
        String retVal = null;
        Connection conn = null;
        try {
            conn = getConnection();
            //GAF 2009-02-24
            retVal = BitacoraOperacionManager.getGestionXml(conn, tituloAplicacion, folio, realPath);
        } catch (SQLException exc) {
            log.warn("Actualizando Bitacora Caso Usuario ", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }
}
