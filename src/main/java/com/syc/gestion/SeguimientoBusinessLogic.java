package com.syc.gestion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Seguimiento;
import com.syc.gestion.core.SeguimientoConsulta;
import com.syc.gestion.core.SeguimientoManager;
import com.syc.gestion.core.TipoCasoManager;
import com.syc.gestion.util.PaginaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class SeguimientoBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(SeguimientoBusinessLogic.class);

    public SeguimientoBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public List selectTipoCasos(String u_login) throws GestionException {
        List lTC = new ArrayList();
        Connection conn = null;
        try {
            conn = getConnection();
            Map m = TipoCasoManager.selectAllTipoCasos(conn, u_login);
            Iterator iter = m.values().iterator();
            while (iter.hasNext()) lTC.add(iter.next());
        } catch (Exception exc) {
            log.error("Recuperando Tipo Casos", exc);
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
        return lTC;
    }

    public PaginaData selectSeguimiento(int id_tc, int id_caso, String u_login, PaginaData param_pd) throws GestionException {
        PaginaData pd = null;
        //List listSeg = new ArrayList();
        Connection conn = null;
        try {
            conn = getConnection();
            Seguimiento seg = new Seguimiento();
            seg.setIdTC(id_tc);
            seg.setIdCaso(id_caso);
            /*
			 * if (u_login != null) seg.setResponsableEjec(u_login);
			 */
            pd = SeguimientoManager.select(conn, seg, u_login, param_pd);
        } catch (Exception exc) {
            log.error("Recuperando seguimiento", exc);
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
        return pd;
    }

    public PaginaData selectAgrupado(SeguimientoConsulta sc, String u_login, PaginaData in_pd) throws GestionException {
        PaginaData pdSeg = null;
        //List listSeg = new ArrayList();
        Connection conn = null;
        try {
            conn = getConnection();
            pdSeg = SeguimientoManager.selectAgrupado(conn, sc, u_login, in_pd);
        } catch (Exception exc) {
            log.error("Recuperando seguimiento", exc);
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
        return pdSeg;
    }

    public String activo_turno(String idResponsable, String idcaso, String estado_asunto) throws GestionException {
        Connection conn = null;
        String activo = "Activo";
        int conteo;
        ResultSet rs = null;
        PreparedStatement p = null;
        try {
            //si todo el asunto esta cerrado ya para que checamos esto regresamos cerrado
            if (estado_asunto.equals("S"))
                return activo = "Cerrado";
            conn = getConnection();
            String query = "SELECT COUNT(*) as conteo FROM CG_BITACORA_OPERACION " + " WHERE  ID_CASO =" + idcaso + " AND  ID_OPERACION =10 AND RESPONSABLE_ID ='" + idResponsable + "'" + " AND TERMINADA ='N'";
            String query_res = "select count(*) as conteo from cg_bitacora_operacion where " + " id_caso =" + idcaso + " and id_operacion in(4,5,12) and remitente_id = '" + idResponsable + "'";
            //System.out.println("query1 = "+query);
            //System.out.println("query2 = " +query_res);
            /* RDMB:  Vamos a checar si tiene respuestas rechazadas, que se encuentren como terminada ='N',si es que tiene 
	         * su estatus del turno es Abierto, si no tiene ninguna rechazada es Cerrado.
	         * */
            if (conn != null) {
                p = conn.prepareStatement(query);
                rs = p.executeQuery();
                if (rs != null) {
                    conteo = getRsValor(rs, "conteo");
                    activo = conteo == 0 ? "Cerrado" : "Activo";
                }
                if (activo.equals("Cerrado")) {
                    //como no tiene respuestas rechazadas solo validaremos que efectivamente haya enviado por lo menos una respuesta
                    p = conn.prepareStatement(query_res);
                    rs = p.executeQuery();
                    if (rs != null) {
                        conteo = getRsValor(rs, "conteo");
                        activo = conteo > 0 ? "Cerrado" : "Activo";
                    }
                }
            }
        } catch (Exception exc) {
            log.error("Recuperando seguimiento", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (rs != null)
                    rs.close();
                if (p != null)
                    p.close();
                rs = null;
                p = null;
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return activo;
    }

    private int getRsValor(ResultSet rs, String campo) throws Exception {
        int valor = 0;
        if (rs.next()) {
            valor = rs.getInt(campo);
        }
        return valor;
    }
}
