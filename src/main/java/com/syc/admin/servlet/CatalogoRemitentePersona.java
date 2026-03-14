package com.syc.admin.servlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;

public class CatalogoRemitentePersona {

    public static int delete(Connection conn, int crp_id_persona) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("DELETE FROM cg_cat_remitente_persona WHERE crp_id_persona = ?");
            pstmnt.setInt(1, crp_id_persona);
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int insert(Connection conn, CatRemPersona crp) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            String valor2 = "SELECT Count(*) total2 FROM cg_cat_remitente_persona" + " WHERE crp_id_persona='" + crp.getCrp_id_persona() + "'" + " AND crp_nombres='" + crp.getCrp_nombres() + "'" + " AND crp_apaterno='" + crp.getCrp_apaterno() + "'" + " AND crp_amaterno='" + crp.getCrp_amaterno() + "'" + " AND crp_id_area='" + crp.getCrp_id_area() + "'" + " AND crp_puesto='" + crp.getCrp_puesto() + "'";
            System.out.println(valor2);
            pstmnt = conn.prepareStatement(valor2);
            rs = pstmnt.executeQuery();
            if (!rs.next())
                return -1;
            System.out.println(rs.getString("total2"));
            retval = Integer.parseInt(rs.getString("total2"));
            if (rs.getString("total2").equals("0")) {
                pstmnt = conn.prepareStatement("INSERT INTO cg_cat_remitente_persona (crp_id_persona, crp_nombres, crp_apaterno, crp_amaterno, crp_id_area, crp_puesto) VALUES (?, ?, ?, ?, ?, ?)");
                pstmnt.setInt(1, crp.getCrp_id_persona());
                pstmnt.setString(2, crp.getCrp_nombres());
                pstmnt.setString(3, crp.getCrp_apaterno());
                pstmnt.setString(4, crp.getCrp_amaterno());
                pstmnt.setString(5, crp.getCrp_id_area());
                pstmnt.setString(6, crp.getCrp_puesto());
                retval = pstmnt.executeUpdate();
            } else {
                System.out.println("La persona ya existe");
            }
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static String select(Connection conn, int crp_id_persona) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String ru_login = null;
        try {
            pstmnt = conn.prepareStatement("SELECT crp_id_persona FROM cg_cat_remitente_persona WHERE crp_id_persona = ?");
            pstmnt.setInt(1, crp_id_persona);
            rs = pstmnt.executeQuery();
            if (rs.next())
                ru_login = rs.getString(1);
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return ru_login;
    }

    public static CatRemPersona select(Connection conn, CatRemPersona crp) throws SQLException {
        CatRemPersona ru = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            StringBuffer where = new StringBuffer();
            String token = " WHERE ";
            if (crp.getCrp_id_persona() != -1) {
                where.append(token + "crp_id_persona = ?");
                token = " AND ";
            }
            if (crp.getCrp_nombres() != null) {
                where.append(token + "crp_nombres = ?");
                token = " AND ";
            }
            if (crp.getCrp_apaterno() != null) {
                where.append(token + "crp_apaterno = ?");
                token = " AND ";
            }
            if (crp.getCrp_amaterno() != null) {
                where.append(token + "crp_amaterno = ?");
                token = " AND ";
            }
            if (crp.getCrp_id_area() != null) {
                where.append(token + "crp_id_area = ?");
                token = " AND ";
            }
            if (crp.getCrp_puesto() != null) {
                where.append(token + "crp_puesto");
                token = " AND ";
            }
            pstmnt = conn.prepareStatement("SELECT * FROM cg_cat_remitente_persona " + where.toString());
            int i = 1;
            if (crp.getCrp_id_persona() != -1)
                pstmnt.setInt(i++, crp.getCrp_id_persona());
            if (crp.getCrp_nombres() != null)
                pstmnt.setString(i++, crp.getCrp_nombres());
            if (crp.getCrp_apaterno() != null)
                pstmnt.setString(i++, crp.getCrp_apaterno());
            if (crp.getCrp_amaterno() != null)
                pstmnt.setString(i++, crp.getCrp_amaterno());
            if (crp.getCrp_id_area() != null)
                pstmnt.setString(i++, crp.getCrp_id_area());
            if (crp.getCrp_puesto() != null)
                pstmnt.setString(i++, crp.getCrp_puesto());
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                ru = new CatRemPersona();
                ru.setCrp_id_persona(Integer.parseInt(rs.getString("crp_id_persona")));
                ru.setCrp_nombres(rs.getString("crp_nombres"));
                ru.setCrp_apaterno(rs.getString("crp_apaterno"));
                ru.setCrp_amaterno(rs.getString("crp_amaterno"));
                ru.setCrp_id_area(rs.getString("crp_id_area"));
                ru.setCrp_puesto(rs.getString("crp_puesto"));
                //ru.setPropiedades(CatalogoEmpleado.select(conn, ce.getLogin()));
                //ru.setGrupos(UsuarioGrupoManager.selectGrupos(conn, ru.getLogin()));
                //ru.setRoles(UsuarioRoleManager.selectRoles(conn, ru.getLogin()));
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return ru;
    }

    public static List selectAll(Connection conn) throws SQLException {
        List usrList = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT * FROM cg_cat_remitente_persona ORDER BY crp_nombres");
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                CatRemPersona crp = new CatRemPersona();
                crp.setCrp_id_persona(rs.getInt("crp_id_persona"));
                crp.setCrp_nombres(rs.getString("crp_nombres"));
                crp.setCrp_apaterno(rs.getString("crp_apaterno"));
                crp.setCrp_amaterno(rs.getString("crp_amaterno"));
                crp.setCrp_id_area(rs.getString("crp_id_area"));
                crp.setCrp_puesto(rs.getString("crp_puesto"));
                //ce.setPropiedades(UsuarioPropiedadesManager.select(conn, ce.getLogin()));
                //u.setGrupos(UsuarioGrupoManager.selectGrupos(conn, u.getLogin()));
                //u.setRoles(UsuarioRoleManager.selectRoles(conn, u.getLogin()));
                usrList.add(crp);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return usrList;
    }

    public static int update(Connection conn, CatRemPersona crp) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        //Statement st = null;
        try {
            pstmnt = conn.prepareStatement("UPDATE cg_cat_remitente_persona SET crp_nombres = ?, crp_apaterno = ?, crp_amaterno = ?, crp_id_area = ?, crp_puesto = ? WHERE crp_id_persona = ?");
            //pstmnt = conn.prepareStatement("UPDATE cg_cat_empleado SET ce_nombre_completo = ?"
            //+ ", ce_ap_paterno = ?, ce_ap_materno = ?, ce_os_responsable = ? WHERE id_ce = ?");
            pstmnt.setString(1, crp.getCrp_nombres());
            pstmnt.setString(2, crp.getCrp_apaterno());
            pstmnt.setString(3, crp.getCrp_amaterno());
            pstmnt.setString(4, crp.getCrp_id_area());
            pstmnt.setString(5, crp.getCrp_puesto());
            pstmnt.setInt(6, crp.getCrp_id_persona());
            retval = pstmnt.executeUpdate();
            //pstmnt.setString(1, "'"+ce.getCe_nombre_completo()+"'");
            //pstmnt.setString(2, "'"+ce.getCe_ap_paterno()+"'");
            //pstmnt.setString(3, "'"+ce.getCe_ap_materno()+"'");
            //pstmnt.setString(4, "'"+ce.getCe_os_responsable()+"'");
            //pstmnt.setInt(5, ce.getId_ce());
            //String id = Integer.toString(ce.getId_ce());
            //String nombre = ce.getCe_nombre_completo();
            //String ap = ce.getCe_ap_paterno();
            //String am = ce.getCe_ap_materno();
            //String respo = ce.getCe_os_responsable();
            //String qry =  "UPDATE cg_cat_empleado SET ce_nombre_completo = '"+nombre
            //+ "', ce_ap_paterno = '"+ap+"', ce_ap_materno = '"+am+"', ce_os_responsable = '"+respo+"' WHERE id_ce ="+id;
            //UsuarioPropiedadesManager.update(conn, ce.getPropiedades());
            // FIXME Se deben actualizar
            // UsuarioGrupoManager.update(conn, u.getGrupos());
            // UsuarioRoleManager.update(conn, u.getRoles());
            //boolean rs = st.execute(qry);
            //if(rs){conn.commit();
            //if(pstmnt.execute()){retval=1;}else{retval=0;}
            //}else{conn.rollback();}
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }
}
