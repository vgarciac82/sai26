package com.syc.gestion.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class ListadoCorreosPendientesManager {

    public static int insert(Connection conn, CorreosPendientesBean cpb) throws Exception {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("INSERT INTO tCorreosPendientes (destinatarios, mensaje, subject, error) " + "VALUES (?,?,?,?)");
            pstmnt.setString(1, cpb.getDestinatario());
            pstmnt.setString(2, cpb.getMensaje());
            pstmnt.setString(3, cpb.getSubject());
            pstmnt.setString(4, cpb.getError());
            retval = pstmnt.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmnt, false);
        }
        return retval;
    }

    public static int delete(Connection conn, CorreosPendientesBean cpb) throws Exception {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("DELETE from tCorreosPendientes where id = ?");
            pstmnt.setInt(1, cpb.getId());
            retval = pstmnt.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmnt, false);
        }
        return retval;
    }

    public static CorreosPendientesBean select(Connection conn, CorreosPendientesBean cpb) throws Exception {
        CorreosPendientesBean rg = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            StringBuffer where = new StringBuffer();
            String token = " WHERE ";
            if (cpb.getId() >= 0) {
                where.append(token + "id = ?");
                token = " AND ";
            }
            if (cpb.getDestinatario() != null) {
                where.append(token + "destinatario = ?");
                token = " AND ";
            }
            if (cpb.getMensaje() != null) {
                where.append(token + "mensaje = ?");
                token = " AND ";
            }
            if (cpb.getDestinatario() != null) {
                where.append(token + "subject = ?");
                token = " AND ";
            }
            if (cpb.getMensaje() != null) {
                where.append(token + "error = ?");
                token = " AND ";
            }
            pstmnt = conn.prepareStatement("SELECT * FROM tCorreosPendientes " + where.toString());
            int i = 1;
            if (cpb.getId() >= 0)
                pstmnt.setInt(i++, cpb.getId());
            if (cpb.getDestinatario() != null)
                pstmnt.setString(i++, cpb.getDestinatario());
            if (cpb.getMensaje() != null)
                pstmnt.setString(i++, cpb.getMensaje());
            if (cpb.getSubject() != null)
                pstmnt.setString(i++, cpb.getSubject());
            if (cpb.getError() != null)
                pstmnt.setString(i++, cpb.getError());
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                rg = new CorreosPendientesBean();
                rg.setId(rs.getInt("id"));
                rg.setDestinatario(rs.getString("destinatario"));
                rg.setMensaje(rs.getString("mensaje"));
                rg.setDestinatario(rs.getString("subject"));
                rg.setMensaje(rs.getString("error"));
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pstmnt, false);
        }
        return rg;
    }

    public static List<CorreosPendientesBean> selectAll(Connection conn) throws Exception {
        List<CorreosPendientesBean> grpList = new ArrayList<CorreosPendientesBean>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT * FROM tCorreosPendientes WITH(NOLOCK) ORDER BY id");
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                CorreosPendientesBean cpb = new CorreosPendientesBean();
                cpb.setId(rs.getInt("id"));
                cpb.setDestinatario(rs.getString("destinatarios"));
                cpb.setMensaje(rs.getString("mensaje"));
                cpb.setSubject(rs.getString("subject"));
                cpb.setError(rs.getString("error"));
                grpList.add(cpb);
            }
            return grpList;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pstmnt, false);
        }
    }
}
