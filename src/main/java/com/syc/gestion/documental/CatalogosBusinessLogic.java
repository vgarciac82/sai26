package com.syc.gestion.documental;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.syc.adquisiciones.vo.ConexionesBD;
import com.syc.auditoria.AuditoriaBusinessLogic;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Empleado;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatalogosBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(CatalogosBusinessLogic.class);

    public CatalogosBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public JSONArray readCatalogos(String query) throws ServletException, JSONException {
        return readCatalogos(query, false);
    }

    public JSONArray readCatalogos(String query, boolean makeCommit) throws ServletException, JSONException {
        String[][] retVal = null;
        Connection conn = null;
        JSONArray jsonArray = new JSONArray();
        String strDatos = "";
        try {
            conn = getConnection();
            retVal = CatalogosManager.getSelectQuery(conn, query);
            for (int i = 0; i < retVal.length; i++) {
                JSONObject jsonObj = new JSONObject(new LinkedHashMap<String, String>());
                for (int j = 0; j < retVal[i].length; j++) {
                    strDatos = retVal[i][j];
                    jsonObj.put("Col" + j, strDatos);
                }
                // Add to the array
                jsonArray.put(jsonObj);
            }
            if (makeCommit)
                conn.commit();
        } catch (SQLException exc) {
            log.error(exc);
            throw new ServletException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            conn = null;
        }
        return jsonArray;
    }

    public JSONArray readCatalogosManto(String query, boolean makeCommit, String baseDatos) throws ServletException, JSONException {
        String[][] retVal = null;
        Connection conn = null;
        JSONArray jsonArray = new JSONArray();
        String strDatos = "";
        List<ConexionesBD> conexiones = null;
        ConexionesBD cbd = null;
        String queryBase = null;
        String queryOrig = query;
        try {
            conn = getConnection();
            queryBase = (baseDatos.indexOf("*") != -1) ? "SELECT * FROM TEJERCICIOFISCAL" : " SELECT * FROM TEJERCICIOFISCAL WHERE AEJERCICIOFISCAL = '" + baseDatos + "'";
            conexiones = CatalogosManager.getBasesDeDatos(conn, queryBase);
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                //strAgregado = e.getMessage();
                e.printStackTrace();
            }
            conn = null;
        }
        Iterator<ConexionesBD> ite = conexiones.iterator();
        while (ite.hasNext()) {
            cbd = ite.next();
            try {
                if (cbd.getEjercicioFiscal().trim().length() == 3)
                    queryOrig = query.replaceAll("''", "'2" + cbd.getEjercicioFiscal().trim() + "-PRUEBAS' ");
                else
                    queryOrig = query.replaceAll("''", "'" + cbd.getEjercicioFiscal() + "' ");
                conn = getConectionCatalogo(cbd.getServidor(), cbd.getPuerto(), cbd.getNombreBD(), cbd.getUsuarioBD(), cbd.getPassBD());
                retVal = CatalogosManager.getSelectQuery(conn, queryOrig);
                for (int i = 0; i < retVal.length; i++) {
                    JSONObject jsonObj = new JSONObject(new LinkedHashMap<String, String>());
                    for (int j = 0; j < retVal[i].length; j++) {
                        strDatos = retVal[i][j];
                        jsonObj.put("Col" + j, strDatos);
                    }
                    // Add to the array
                    jsonArray.put(jsonObj);
                }
            } catch (SQLException exc) {
                log.error(exc);
                throw new ServletException(exc);
            } finally {
                try {
                    if (conn != null)
                        conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                conn = null;
            }
        }
        return jsonArray;
    }

    public synchronized String InsertMantoCatalogos(String query, String base, Object[] param, int accion) throws ServletException {
        String strResult = "N";
        String strAgregadoFinal = null;
        Connection conn = null;
        List<ConexionesBD> conexiones = null;
        ConexionesBD cbd = null;
        String queryBase = null;
        try {
            conn = getConnection();
            //Para cuando es la base de pruebas, hay que quitarle el primer numero
            if (base.length() > 4) {
                String[] baseaux = new String[2];
                baseaux = base.split("-");
                base = baseaux[0].substring(1, baseaux[0].length());
            }
            queryBase = (base.indexOf("*") != -1) ? "SELECT * FROM TEJERCICIOFISCAL" : " SELECT * FROM TEJERCICIOFISCAL WHERE AEJERCICIOFISCAL = '" + base + "'";
            conexiones = CatalogosManager.getBasesDeDatos(conn, queryBase);
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            conn = null;
        }
        Iterator<ConexionesBD> ite = conexiones.iterator();
        String jndiName = "jdbc/gestion";
        HttpSession sesion = (HttpSession) param[2];
        while (ite.hasNext()) {
            cbd = ite.next();
            try {
                conn = getConectionCatalogo(cbd.getServidor(), cbd.getPuerto(), cbd.getNombreBD(), cbd.getUsuarioBD(), cbd.getPassBD());
                CatalogosManager.setInsertQuery(conn, query);
                try {
                    AuditoriaBusinessLogic ABL = new AuditoriaBusinessLogic(jndiName);
                    if (accion == GestionInterface.OPER_INS) {
                        //|| accion == GestionInterface.OPER_DEL
                        String valores_insert = ABL.getValores_insert(query, (String) param[0]);
                        Usuario objUsuario = (Usuario) sesion.getAttribute(GestionInterface.ATT_USER);
                        String usuario = objUsuario.getLogin();
                        Empleado emp = (Empleado) sesion.getAttribute(GestionInterface.ATT_EMPLEADO);
                        String area = emp.getClaveArea();
                        ABL.agregaAuditoriaManto(conn, usuario, area, jndiName.substring(jndiName.indexOf("/") + 1), (String) param[1], "Agregar", "", valores_insert, query, "");
                    } else if (accion == GestionInterface.OPER_UPD) {
                        ArrayList valores_origen_destino = (ArrayList) param[3];
                        Usuario objUsuario = (Usuario) sesion.getAttribute(GestionInterface.ATT_USER);
                        String usuario = objUsuario.getLogin();
                        Empleado emp = (Empleado) sesion.getAttribute(GestionInterface.ATT_EMPLEADO);
                        String area = emp.getClaveArea();
                        String llave = (String) param[0];
                        llave = llave.toUpperCase();
                        System.out.println("Usuario:" + usuario + "  emp:" + emp + "   area:" + area + "   llave:" + llave);
                        if (valores_origen_destino.size() > 0 && !"".equals(valores_origen_destino.get(0).toString()) && !"".equals(valores_origen_destino.get(0).toString()))
                            ABL.agregaAuditoriaManto(conn, usuario, area, jndiName.substring(jndiName.indexOf("/") + 1), (String) param[1], "Actualizar", "Para " + llave + " ten&iacute;a:<br>" + valores_origen_destino.get(0).toString(), "Para " + llave + " tiene:<br>" + valores_origen_destino.get(1).toString(), query, llave);
                    } else if (accion == GestionInterface.OPER_DEL) {
                        //String valores_insert=ABL.getValores_insert(strQuery,strParam);
                        Usuario objUsuario = (Usuario) sesion.getAttribute(GestionInterface.ATT_USER);
                        String usuario = objUsuario.getLogin();
                        Empleado emp = (Empleado) sesion.getAttribute(GestionInterface.ATT_EMPLEADO);
                        String area = emp.getClaveArea();
                        //ABL.agregaAuditoria(usuario,area,jndiName.substring(jndiName.indexOf("/")+1),strModulo,"Agregar","",valores_insert,strQuery,"");
                        ABL.agregaAuditoriaManto(conn, usuario, area, jndiName.substring(jndiName.indexOf("/") + 1), (String) param[1], "Borrar", (String) param[0], "", query, (String) param[0]);
                    } else //Para  avisos
                    if (accion == GestionInterface.OPER_AVI) {
                        //String valores_insert=ABL.getValores_insert(strQuery,strParam);
                        Usuario objUsuario = (Usuario) sesion.getAttribute(GestionInterface.ATT_USER);
                        String usuario = objUsuario.getLogin();
                        Empleado emp = (Empleado) sesion.getAttribute(GestionInterface.ATT_EMPLEADO);
                        String area = emp.getClaveArea();
                        //ABL.agregaAuditoria(usuario,area,jndiName.substring(jndiName.indexOf("/")+1),strModulo,"Agregar","",valores_insert,strQuery,"");
                        ABL.agregaAuditoriaManto(conn, usuario, area, jndiName.substring(jndiName.indexOf("/") + 1), (String) param[1], "GuardarAviso", (String) param[0], "", query, (String) param[0]);
                    }
                } catch (GestionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                conn.commit();
                strResult = "S";
            } catch (SQLException exc) {
                log.error(exc);
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    System.err.println("[" + this.getClass().getName() + "] Error SQL rollback");
                }
                strAgregadoFinal = exc.getMessage();
            } catch (ServletException exc) {
                log.error(exc);
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    System.err.println("[" + this.getClass().getName() + "] Error SQL rollback");
                }
                strAgregadoFinal = exc.getMessage();
            } finally {
                try {
                    if (conn != null)
                        conn.close();
                } catch (SQLException e) {
                }
                conn = null;
                try {
                    if (accion == GestionInterface.OPER_INS) {
                        conn = getConectionCatalogo(cbd.getServidor(), cbd.getPuerto(), cbd.getNombreBD(), cbd.getUsuarioBD(), cbd.getPassBD());
                        AuditoriaBusinessLogic ABL = new AuditoriaBusinessLogic(jndiName);
                        String valores_insert = "No logró insertar";
                        Usuario objUsuario = (Usuario) sesion.getAttribute(GestionInterface.ATT_USER);
                        String usuario = objUsuario.getLogin();
                        Empleado emp = (Empleado) sesion.getAttribute(GestionInterface.ATT_EMPLEADO);
                        String area = emp.getClaveArea();
                        ABL.agregaAuditoria(usuario, area, jndiName.substring(jndiName.indexOf("/") + 1), (String) param[1], "Agregar", "", valores_insert, query, "");
                        conn.commit();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (conn != null)
                            conn.close();
                    } catch (SQLException e) {
                    }
                    conn = null;
                }
            }
        }
        return strResult;
    }

    public String InsertCatalogos(String query) throws ServletException {
        String strAgregado = "N";
        Connection conn = null;
        try {
            conn = getConnection();
            CatalogosManager.setInsertQuery(conn, query);
            conn.commit();
            strAgregado = "S";
        } catch (SQLException exc) {
            log.error(exc);
            try {
                conn.rollback();
            } catch (SQLException e) {
                System.err.println("[" + this.getClass().getName() + "] Error SQL rollback");
            }
            strAgregado = exc.getMessage();
            throw new ServletException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                strAgregado = e.getMessage();
                e.printStackTrace();
            }
            conn = null;
        }
        return strAgregado;
    }

    public String[][] ArrCatalogos(String query) throws ServletException {
        String[][] retVal = null;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = CatalogosManager.getSelectQuery(conn, query);
        } catch (SQLException exc) {
            log.error(exc);
            throw new ServletException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            conn = null;
        }
        return retVal;
    }

    public Map<String, String> ArrCatalogosManto(String query, String base) throws ServletException {
        String[][] retVal = null;
        Map<String, String> map = new HashMap<String, String>();
        Connection conn = null;
        List<ConexionesBD> conexiones = null;
        ConexionesBD cbd = null;
        String queryBase = null;
        try {
            conn = getConnection();
            queryBase = (base.indexOf("*") != -1) ? "SELECT * FROM TEJERCICIOFISCAL" : " SELECT * FROM TEJERCICIOFISCAL WHERE AEJERCICIOFISCAL = '" + base + "'";
            conexiones = CatalogosManager.getBasesDeDatos(conn, queryBase);
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                //strAgregado = e.getMessage();
                e.printStackTrace();
            }
            conn = null;
        }
        Iterator<ConexionesBD> ite = conexiones.iterator();
        while (ite.hasNext()) {
            cbd = ite.next();
            try {
                conn = getConectionCatalogo(cbd.getServidor(), cbd.getPuerto(), cbd.getNombreBD(), cbd.getUsuarioBD(), cbd.getPassBD());
                retVal = CatalogosManager.getSelectQuery(conn, query);
                if (retVal.length > 0)
                    map.put((cbd.getEjercicioFiscal().trim().length() == 3) ? "2" + cbd.getEjercicioFiscal().trim() + "-PRUEBAS" : cbd.getEjercicioFiscal(), "" + retVal.length);
            } catch (SQLException exc) {
                log.error(exc);
                throw new ServletException(exc);
            } finally {
                try {
                    if (conn != null)
                        conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                conn = null;
            }
        }
        return map;
    }

    private Connection getConectionCatalogo(String server, String port, String bd, String user, String pass) {
        Connection conn = null;
        String url = null;
        try {
            url = "jdbc:jtds:sqlserver://" + server + ":" + port + "/" + bd;
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, pass);
            conn.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public String selectAvisoUsuario(String query, String base) throws ServletException {
        String strAgregado = "N";
        Connection conn = null;
        List<ConexionesBD> conexiones = null;
        ConexionesBD cbd = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String queryBase = null;
        try {
            conn = getConnection();
            //queryBase = " SELECT * FROM TEJERCICIOFISCAL WHERE AEJERCICIOFISCAL = '"+base+"'";
            queryBase = (base.indexOf("*") != -1) ? "SELECT * FROM TEJERCICIOFISCAL" : " SELECT * FROM TEJERCICIOFISCAL WHERE AEJERCICIOFISCAL = '" + base + "'";
            conexiones = CatalogosManager.getBasesDeDatos(conn, queryBase);
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                strAgregado = e.getMessage();
                e.printStackTrace();
            }
            conn = null;
        }
        //Iterator<ConexionesBD> ite = conexiones.iterator();
        cbd = conexiones.get(0);
        try {
            conn = getConectionCatalogo(cbd.getServidor(), cbd.getPuerto(), cbd.getNombreBD(), cbd.getUsuarioBD(), cbd.getPassBD());
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getString("cValor");
        } catch (SQLException exc) {
            strAgregado = exc.getMessage();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
            }
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                strAgregado = e.getMessage();
                e.printStackTrace();
            }
            ps = null;
            rs = null;
            conn = null;
        }
        return strAgregado;
    }
}
