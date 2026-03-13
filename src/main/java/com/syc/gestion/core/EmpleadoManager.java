package com.syc.gestion.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.lang.StringUtils;
import com.syc.cfdi.utils.CloseObject;
import com.syc.dbms.DBMS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmpleadoManager {

    public static final Logger log = LoggerFactory.getLogger(EmpleadoManager.class);

    public static int delete(Connection conn, String id) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("DELETE FROM cg_cat_empleado WHERE ID_EMPLEADO = ?");
            pstmnt.setString(1, id);
            retval = pstmnt.executeUpdate();
            //GAF 2010-04-16
            //En los manager no debe haber commits!
            //solamente en los businesslogic o servlets
            //conn.commit();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int insert(Connection conn, Empleado e) throws SQLException {
        int retval = -1;
        //Si el id de empleado viene nulo, obtener el maximo
        if (e.getId() == null) {
            e.setId(Integer.toString(getMaxIdEmpleado(conn)));
        }
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("INSERT INTO cg_cat_empleado " + "(ID_AREA, ID_EMPLEADO, " + "CE_AP_PATERNO, CE_AP_MATERNO, " + "CE_NOMBRE_COMPLETO, CE_OS_RESPONSABLE, " + "SALUTACION, CARGO) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            pstmnt.setString(1, e.getClaveArea());
            pstmnt.setString(2, e.getId());
            pstmnt.setString(3, e.getApellidoPaterno());
            pstmnt.setString(4, e.getApellidoMaterno());
            pstmnt.setString(5, e.getNombre());
            pstmnt.setString(6, e.getClaveUsuario());
            pstmnt.setString(7, e.getSalutacion());
            pstmnt.setString(8, e.getCargo());
            retval = pstmnt.executeUpdate();
            //GAF 2010-04-16
            //En los manager no debe haber commits!
            //solamente en los businesslogic o servlets
            //conn.commit();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static Empleado select(Connection conn, Empleado e) throws SQLException {
        Empleado re = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            StringBuffer where = new StringBuffer();
            String token = " WHERE ";
            if (e.getClaveArea() != null) {
                where.append(token + "e.ID_AREA = ?");
                token = " AND ";
            }
            if (e.getId() != null) {
                where.append(token + "e.ID_EMPLEADO = ?");
                token = " AND ";
            }
            if (e.getApellidoPaterno() != null) {
                where.append(token + "e.CE_AP_PATERNO = ?");
                token = " AND ";
            }
            if (e.getApellidoMaterno() != null) {
                where.append(token + "e.CE_AP_MATERNO = ?");
                token = " AND ";
            }
            if (e.getNombre() != null) {
                where.append(token + "e.CE_NOMBRE_COMPLETO = ?");
                token = " AND ";
            }
            if (e.getClaveUsuario() != null) {
                where.append(token + "e.CE_OS_RESPONSABLE = ?");
                token = " AND ";
            }
            if (e.getSalutacion() != null) {
                where.append(token + "e.SALUTACION = ?");
                token = " AND ";
            }
            if (e.getSalutacion() != null) {
                where.append(token + "e.CARGO = ?");
                token = " AND ";
            }
            String query = "SELECT * FROM cg_cat_empleado e " + where.toString();
            //System.out.println("query=["+query+"] parametro e.getId()=["+e.getId()+"]");
            pstmnt = conn.prepareStatement(query);
            int i = 1;
            if (e.getClaveArea() != null)
                pstmnt.setString(i++, e.getClaveArea());
            if (e.getId() != null)
                pstmnt.setString(i++, e.getId());
            if (e.getApellidoPaterno() != null)
                pstmnt.setString(i++, e.getApellidoPaterno());
            if (e.getApellidoMaterno() != null)
                pstmnt.setString(i++, e.getApellidoMaterno());
            if (e.getNombre() != null)
                pstmnt.setString(i++, e.getNombre());
            if (e.getClaveUsuario() != null)
                pstmnt.setString(i++, e.getClaveUsuario());
            if (e.getSalutacion() != null)
                pstmnt.setString(i++, e.getSalutacion());
            if (e.getCargo() != null)
                pstmnt.setString(i++, e.getCargo());
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                re = new Empleado();
                re.setClaveArea(rs.getString("ID_AREA"));
                re.setId(rs.getString("ID_EMPLEADO"));
                re.setApellidoPaterno(rs.getString("CE_AP_PATERNO"));
                re.setApellidoMaterno(rs.getString("CE_AP_MATERNO"));
                re.setNombre(rs.getString("CE_NOMBRE_COMPLETO"));
                re.setClaveUsuario(rs.getString("CE_OS_RESPONSABLE"));
                re.setSalutacion(rs.getString("SALUTACION"));
                String cargo = null;
                try {
                    cargo = buscaCargoRH(conn, rs.getString("CE_OS_RESPONSABLE"));
                } catch (Exception e3) {
                    log.warn("Error buscando cargo en SAI RRHH " + e3);
                }
                if (StringUtils.isBlank(cargo))
                    re.setCargo(rs.getString("CARGO"));
                else
                    re.setCargo(cargo);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return re;
    }

    private static String buscaCargoRH(Connection conn, String uLogin) throws SQLException {
        String query = "SELECT descripcion_puesto " + "FROM   cg_usuario usuario WITH(nolock) " + "       INNER JOIN v_empleados_giro empleado WITH(nolock) " + "               ON usuario.cnumeroempleado = empleado.clave " + "WHERE  usuario.u_login = ? ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        String cargo = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, uLogin);
            rs = ps.executeQuery();
            if (rs.next())
                cargo = rs.getString(1);
            return cargo;
        } finally {
            try {
                CloseObject.closeObject(ps, false);
                CloseObject.closeObject(rs, false);
            } catch (Exception e) {
            }
        }
    }

    public static int update(Connection conn, Empleado e) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("UPDATE cg_cat_empleado " + "SET ID_AREA = ?, " + "CE_AP_PATERNO = ?, " + "CE_AP_MATERNO = ?, " + "CE_NOMBRE_COMPLETO = ?, " + "SALUTACION = ?, " + "CARGO = ? " + //OJO: Esto se pone como CE_OS_RESPONSABLE
            //Para salir del compromiso, pero hace falta
            //Revisarlo y cuando menos crear un indice
            //unico por CE_OS_RESPONSABLE
            "WHERE CE_OS_RESPONSABLE = ?");
            pstmnt.setString(1, e.getClaveArea());
            pstmnt.setString(2, e.getApellidoPaterno());
            pstmnt.setString(3, e.getApellidoMaterno());
            pstmnt.setString(4, e.getNombre());
            pstmnt.setString(5, e.getSalutacion());
            pstmnt.setString(6, e.getCargo());
            pstmnt.setString(7, e.getClaveUsuario());
            retval = pstmnt.executeUpdate();
            //GAF 2010-04-16
            //En los manager no debe haber commits!
            //solamente en los businesslogic o servlets
            //conn.commit();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int update(Connection conn, Empleado eFrom, Empleado eTo) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("UPDATE cg_cat_empleado " + "SET ID_AREA = ?, " + "CE_AP_PATERNO = ?, " + "CE_AP_MATERNO = ?, " + "CE_NOMBRE_COMPLETO = ?, " + "SALUTACION = ?, " + "CARGO = ?, " + "CE_OS_RESPONSABLE = ? " + //OJO: Esto se pone como CE_OS_RESPONSABLE
            //Para salir del compromiso, pero hace falta
            //Revisarlo y cuando menos crear un indice
            //unico por CE_OS_RESPONSABLE
            "WHERE CE_OS_RESPONSABLE = ?");
            pstmnt.setString(1, eTo.getClaveArea());
            pstmnt.setString(2, eTo.getApellidoPaterno());
            pstmnt.setString(3, eTo.getApellidoMaterno());
            pstmnt.setString(4, eTo.getNombre());
            pstmnt.setString(5, eTo.getSalutacion());
            pstmnt.setString(6, eTo.getCargo());
            pstmnt.setString(7, eTo.getClaveUsuario());
            pstmnt.setString(8, eFrom.getClaveUsuario());
            retval = pstmnt.executeUpdate();
            //GAF 2010-04-16
            //En los manager no debe haber commits!
            //solamente en los businesslogic o servlets
            //conn.commit();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int getMaxIdEmpleado(Connection conn) throws SQLException {
        DBMS myDbms = new DBMS(conn);
        int retVal = -1;
        PreparedStatement pstmnt = null;
        try {
            String query = "select MAX(" + myDbms.convertStringToInt("id_empleado") + ")+1 from cg_cat_empleado";
            pstmnt = conn.prepareStatement(query);
            ResultSet rs = pstmnt.executeQuery();
            if (rs.next()) {
                retVal = rs.getInt(1);
            }
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
        }
        return retVal;
    }
}
