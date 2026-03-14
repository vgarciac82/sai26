package com.syc.gestion.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Base64;

public class CoberturaManager {

    // CUIDADO: Reserva para uso excluisivos los alias:
    // "cg_caso                     c"
    // "cg_cat areas                a"
    // "cg_cat_empleado             e"
    // "cg_caso_operacion           co"
    // "cg_cat_areas    (cobertura) ac"
    // "cg_cat_empleado (cobertura) ec"
    public static Cobertura selectByIdUsuario(Connection conn, String u_login, int id_tabla, int id_producto, String titulo_aplicacion) throws SQLException {
        //System.out.println("u_login,id_tabla,id_producto,titulo_aplicacion=" + u_login + ", " + id_tabla + ", " + id_producto + ", " + titulo_aplicacion);
        //Map m = new Hashtable();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        Cobertura c;
        try {
            // 1. Leer el registro de CBO asociada al usuario
            pstmnt = conn.prepareStatement("SELECT " + "    cb.* " + "FROM " + "    cg_usuario_cobertura uc " + ",   cg_cobertura         cb " + "WHERE " + "    uc.u_login      = ? " + "AND uc.id_tabla     = cb.id_tabla " + "AND uc.id_cobertura = cb.id_cobertura " + "AND cb.id_tabla     = ? " + "AND cb.id_producto  = ?");
            pstmnt.setString(1, u_login);
            pstmnt.setInt(2, id_tabla);
            pstmnt.setInt(3, id_producto);
            rs = pstmnt.executeQuery();
            // FIXME: El diseño en la BD permite varias CBO por usuario-sesion, esto es incorrecto solo debe permitir una CBO.
            if (rs.next()) {
                c = new Cobertura();
                c.setIdCobertura(rs.getInt("id_cobertura"));
                c.setCoDescripcion((rs.getString("co_descripcion") == null ? "" : rs.getString("co_descripcion")));
                c.setCoWhereClause((rs.getString("co_where_clause") == null ? "" : rs.getString("co_where_clause")));
                c.setCoFromClause((rs.getString("co_from_clause") == null ? "" : rs.getString("co_from_clause")));
                c.setIdTabla(rs.getInt("id_tabla"));
                c.setIdProducto(rs.getInt("id_producto"));
                //m.put(c.getCoDescripcion(), c);
                // 2. Ligar CBO con caso.
                //    La liga entre CBO y casos se establece como sigue:
                //    Se consideran los casos cuyos involucrados pertenecen a alguna area de la CBO del usuario en sesion
                String whereCBO = c.getCoWhereClause();
                String fromCBO = c.getCoFromClause();
                fromCBO += ",cg_caso c (NOLOCK),cg_caso_operacion co (NOLOCK),cg_cat_empleado e (NOLOCK) ";
                whereCBO = whereCBO.replaceAll("\\@u_login", "'" + u_login + "'");
                whereCBO += " AND co.id_caso          = c.id_caso";
                whereCBO += " AND e.ce_os_responsable = co.co_responsable";
                whereCBO += " AND a.id_area           = e.id_area";
                if (titulo_aplicacion != null && !titulo_aplicacion.equals(""))
                    whereCBO += " AND c.c_id_gabinete     = imx" + titulo_aplicacion.toLowerCase() + ".id_gabinete";
                c.setCoFromClause(fromCBO);
                c.setCoWhereClause(whereCBO);
            } else
                c = null;
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return c;
    }

    public static boolean isCasoVisible(Connection conn, Cobertura c, String id_caso) throws SQLException {
        boolean retVal = false;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            //Aqui aplicamos la cobertura
            String query = "SELECT DISTINCT count(c.id_caso) " + " from " + c.getCoFromClause() + " WHERE c.id_caso = ? " + " AND c_folio not like 'TMP-%' " + c.getCoWhereClause();
            //System.out.println("query=["+query+"] id_caso=["+id_caso+"]");
            // 1. Leer el registro de CBO asociada al usuario
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, id_caso);
            rs = pstmnt.executeQuery();
            int cuantos = -1;
            if (rs.next()) {
                cuantos = rs.getInt(1);
                retVal = cuantos > 0;
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return retVal;
    }

    public static HashMap casosFiltrados(Connection conn, FiltroCasos fc) throws SQLException {
        HashMap retVal = new HashMap();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        //ARMA LA CLAUSULA WHERE
        StringBuffer where = new StringBuffer();
        String token = " WHERE ";
        if (fc.getTipoAsunto() != null && fc.getTipoAsunto().length() > 0) {
            where.append(token + "TIPOASUNTO = '" + fc.getTipoAsunto() + "' ");
            token = " AND ";
            if (fc.getTipoAsunto().equals("I")) {
                if (fc.getRemitenteInternoLogin() != null && fc.getRemitenteInternoLogin().length() > 0) {
                    where.append(token + " REMINULOGIN = '" + fc.getRemitenteInternoLogin() + "' ");
                    token = " AND ";
                }
                if (fc.getRemitenteInternoNombre() != null && fc.getRemitenteInternoNombre().length() > 0) {
                    where.append(token + " REMINUNOMBRE = '" + fc.getRemitenteInternoNombre() + "' ");
                    token = " AND ";
                }
                if (fc.getRemitenteInternoPuesto() != null && fc.getRemitenteInternoPuesto().length() > 0) {
                    where.append(token + " REMINPTONOM = '" + fc.getRemitenteInternoPuesto() + "' ");
                    token = " AND ";
                }
                if (fc.getRemitenteInternoArea() != null && fc.getRemitenteInternoArea().length() > 0) {
                    where.append(token + " REMINDDESC = '" + fc.getRemitenteInternoArea() + "' ");
                    token = " AND ";
                }
            } else if (fc.getTipoAsunto().equals("E")) {
                if (fc.getRemitenteExternoNombre() != null && fc.getRemitenteExternoNombre().length() > 0) {
                    where.append(token + " RENOMBRE = '" + fc.getRemitenteExternoNombre() + "' ");
                    token = " AND ";
                }
                if (fc.getRemitenteExternoCargo() != null && fc.getRemitenteExternoCargo().length() > 0) {
                    where.append(token + " RECARGO = '" + fc.getRemitenteExternoCargo() + "' ");
                    token = " AND ";
                }
                if (fc.getRemitenteExternoProcedencia() != null && fc.getRemitenteExternoProcedencia().length() > 0) {
                    where.append(token + " REPROCEDENCIA = '" + fc.getRemitenteExternoProcedencia() + "' ");
                    token = " AND ";
                }
                if (fc.getRemitenteExternoEstado() != null && fc.getRemitenteExternoEstado().length() > 0) {
                    where.append(token + " REESTADO = '" + fc.getRemitenteExternoEstado() + "' ");
                    token = " AND ";
                }
                if (fc.getRemitenteExternoMunicipio() != null && fc.getRemitenteExternoMunicipio().length() > 0) {
                    where.append(token + " REMUNICIPIO = '" + fc.getRemitenteExternoMunicipio() + "' ");
                    token = " AND ";
                }
                if (fc.getRemitenteExternoLocalidad() != null && fc.getRemitenteExternoLocalidad().length() > 0) {
                    where.append(token + " RELOCALIDAD = '" + fc.getRemitenteExternoLocalidad() + "' ");
                    token = " AND ";
                }
            }
        }
        //obtenemos la consulta de fx_FiltroCasos
        String query = "SELECT id_caso from " + "fx_FiltroCasos2('" + fc.getFechaInicial() + "','" + fc.getFechaFinal() + "') " + where.toString();
        System.out.println("query fx_FiltroCasos2=[" + query + "]");
        try {
            pstmnt = conn.prepareStatement(query);
            rs = pstmnt.executeQuery();
            //int cuantos = -1;
            while (rs.next()) {
                //de momento, podemos solo meter la clave...
                //aunque quizas sea mejor meter todos los
                //campos de la consulta en un array de strings
                retVal.put(Integer.toString(rs.getInt(1)), "");
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return retVal;
    }
}
