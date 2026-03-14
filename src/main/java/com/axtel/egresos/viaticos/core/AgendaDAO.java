package com.axtel.egresos.viaticos.core;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import com.axtel.egresos.viaticos.Agenda;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class AgendaDAO {

    private static final Logger log = LoggerFactory.getLogger(AgendaDAO.class);

    public static int insertarAgenda(Connection conn, Agenda agenda) throws Exception {
        StringBuilder queryInsert = new StringBuilder();
        queryInsert.append("INSERT INTO tAgenda");
        queryInsert.append("   (nidAgenda,  nidComision, fInicio,fFin ,nidTipo ,nidPais ,nidEstado ,nidMunicipio,clocalidad,cMotivoComision ,cActividades )");
        queryInsert.append("	VALUES (?, ?, ?, ?, ? , ?, ? , ?, ?, ?, ? )");
        PreparedStatement ps = null;
        int insertados = 0;
        try {
            int cnt = 1;
            ps = conn.prepareStatement(queryInsert.toString());
            ps.setInt(cnt++, agenda.getIdAgenda());
            ps.setInt(cnt++, agenda.getIdComision());
            ps.setDate(cnt++, new java.sql.Date(agenda.getFechaInicio().getTime()));
            ps.setDate(cnt++, new java.sql.Date(agenda.getFechaFin().getTime()));
            ps.setInt(cnt++, agenda.getIdTipo());
            ps.setInt(cnt++, agenda.getIdPais());
            ps.setInt(cnt++, agenda.getIdEstado());
            ps.setInt(cnt++, agenda.getIdMunicipio());
            ps.setString(cnt++, agenda.getLocalidad());
            ps.setString(cnt++, agenda.getMotivoComision());
            ps.setString(cnt++, agenda.getActividades());
            log.debug("Object: {}", queryInsert.toString() + " con el folio" + agenda.getIdAgenda());
            insertados = ps.executeUpdate();
            return insertados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int borrarAgenda(Connection conn, int folio, String login) throws Exception {
        String query = "DELETE tAgenda where nidAgenda = ?";
        int borrados = 0;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, folio);
            borrados = ps.executeUpdate();
            actualizaAgendaBitacora(conn, folio, login);
            return borrados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static void actualizaAgendaBitacora(Connection conn, int folio, String login) throws Exception {
        String query = "UPDATE tAgendaBitacora SET cUsuario = ? where nidAgenda = ? AND Id = ?";
        PreparedStatement ps = null;
        try {
            int id = consultarIdAgendaBitacora(conn, folio);
            ps = conn.prepareStatement(query);
            ps.setString(1, login);
            ps.setInt(2, folio);
            ps.setInt(3, id);
            ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int consultarIdAgendaBitacora(Connection conn, int folio) throws Exception {
        String query = "SELECT MAX(id) Id FROM tAgendaBitacora WITH (NOLOCK) WHERE  nidAgenda = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        int id = 0;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, folio);
            rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
            return id;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int borrarAgendaComision(Connection conn, int idcomision, String login) throws Exception {
        String query = "DELETE tAgenda where nidComision = ?";
        int borrados = 0;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, idcomision);
            borrados = ps.executeUpdate();
            actualizaAgendaBitacora(conn, idcomision, login);
            return borrados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int existeAgenda(Connection conn, int folio) throws Exception {
        String query = "SELECT COUNT(*) FROM tAgenda WITH (NOLOCK) WHERE nIdComision = ?";
        int idAgenda = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, folio);
            rs = ps.executeQuery();
            if (rs.next()) {
                idAgenda = rs.getInt(1);
            }
            return idAgenda;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }

    public static Agenda consultarAgenda(Connection conn, int folio) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT nidTipo, nidPais, nidEstado, nidMunicipio, cLocalidad, cMotivoComision, cActividades, ");
        query.append("			(SELECT MIN(FINICIO) FROM tAgenda WITH (NOLOCK) WHERE nIdComision = A.nIdComision) FINICIO,");
        query.append(" 		(SELECT MAX(fFin) FROM tAgenda WITH (NOLOCK) WHERE nIdComision = A.nIdComision) FFIN, nidAgenda");
        query.append(" 	FROM tAgenda A WITH (NOLOCK)");
        query.append(" 	WHERE nIdComision = ? and nidPais <> 146 ");
        query.append(" UNION ");
        query.append(" SELECT nidTipo, nidPais, nidEstado, nidMunicipio, cLocalidad, cMotivoComision, cActividades, ");
        query.append("			(SELECT MIN(FINICIO) FROM tAgenda WITH (NOLOCK) WHERE nIdComision = A.nIdComision) FINICIO, ");
        query.append("			(SELECT MAX(fFin) FROM tAgenda WITH (NOLOCK) WHERE nIdComision = A.nIdComision) FFIN, nidAgenda ");
        query.append("		FROM tAgenda A WITH (NOLOCK) ");
        query.append("		WHERE nIdComision =? and nidPais = 146 ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        Agenda agenda = new Agenda();
        log.debug("Object: {}", "Consultando agenda del empleado: " + agenda.getIdAgenda());
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, folio);
            ps.setInt(2, folio);
            rs = ps.executeQuery();
            if (rs.next()) {
                agenda.setFechaInicio(Date.valueOf(rs.getString("fInicio")));
                agenda.setFechaFin(Date.valueOf(rs.getString("fFin")));
                agenda.setIdTipo(Integer.parseInt(rs.getString("nidTipo")));
                agenda.setIdPais(Integer.parseInt(rs.getString("nidPais")));
                agenda.setIdEstado(Integer.parseInt(rs.getString("nidEstado")));
                agenda.setIdMunicipio(Integer.parseInt(rs.getString("nidMunicipio")));
                agenda.setLocalidad(rs.getString("cLocalidad"));
                agenda.setMotivoComision(rs.getString("cMotivoComision"));
                agenda.setActividades(rs.getString("cActividades"));
                agenda.setIdAgenda(Integer.parseInt(rs.getString("nidAgenda")));
            }
            return agenda;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }

    public static void actualizarFolio(Connection conn) throws Exception {
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement("UPDATE CF_SEQUENCE WITH (ROWLOCK) SET seq_value = seq_value + 1 WHERE seq_name = 'agenda'");
            pst.executeUpdate();
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static ArrayList<Agenda> consultaListaAgendas(Connection conn, int idComision) throws Exception {
        String query = "SELECT nidAgenda, fInicio, fFin, nidTipo, nidPais, nidEstado,nidMunicipio, cLocalidad, cMotivoComision, cActividades " + " FROM tAgenda WITH (NOLOCK) WHERE nIdComision = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Agenda> formato = new ArrayList<Agenda>();
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, idComision);
            rs = ps.executeQuery();
            while (rs.next()) {
                Agenda agenda = new Agenda();
                agenda.setFechaInicio(Date.valueOf(rs.getString("fInicio")));
                agenda.setFechaFin(Date.valueOf(rs.getString("fFin")));
                agenda.setIdAgenda(Integer.parseInt(rs.getString("nidAgenda")));
                agenda.setIdPais(Integer.parseInt(rs.getString("nidPais")));
                agenda.setIdEstado(Integer.parseInt(rs.getString("nidEstado")));
                agenda.setIdMunicipio(Integer.parseInt(rs.getString("nidMunicipio")));
                agenda.setLocalidad(rs.getString("cLocalidad"));
                agenda.setMotivoComision(rs.getString("cMotivoComision"));
                agenda.setActividades(rs.getString("cActividades"));
                formato.add(agenda);
            }
            return formato;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }

    public static int actualizarDatosAgenda(Connection conn, Agenda agenda) throws Exception {
        PreparedStatement pst = null;
        String query = "UPDATE tAgenda SET fInicio = ?, fFin = ?, nidTipo = ?, nidPais = ?, nidEstado =?, nidMunicipio = ?, cLocalidad = ?, cMotivoComision = ? WHERE nidAgenda = ?";
        try {
            pst = conn.prepareStatement(query);
            pst.setDate(1, Util.toSQLDate(agenda.getFechaInicio()));
            pst.setDate(2, Util.toSQLDate(agenda.getFechaFin()));
            pst.setInt(3, agenda.getIdTipo());
            pst.setInt(4, agenda.getIdPais());
            pst.setInt(5, agenda.getIdEstado());
            pst.setInt(6, agenda.getIdMunicipio());
            pst.setString(7, agenda.getLocalidad());
            pst.setString(8, agenda.getMotivoComision());
            pst.setInt(9, agenda.getIdAgenda());
            int actualizados = pst.executeUpdate();
            return actualizados;
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static Agenda consultaFechaAgendaAcumulada(Connection conn, int idComision) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Agenda agenda = new Agenda();
        String query = "SELECT nIdComision, FINICIO, FFIN, nIdEmpleado FROM v_AgendaDiasAcumulados where nIdComision =  ? ";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, idComision);
            rs = ps.executeQuery();
            if (rs.next()) {
                agenda.setFechaInicio(Date.valueOf(rs.getString("FINICIO")));
                agenda.setFechaFin(Date.valueOf(rs.getString("FFIN")));
                agenda.setIdComision(idComision);
                agenda.setIdEmpleado(Integer.parseInt(rs.getString("nIdEmpleado")));
            }
            return agenda;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }

    public static Agenda consultaFechaAgendaBitacora(Connection conn, int idComision) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Agenda agenda = new Agenda();
        String query = "SELECT nidcomision, MIN(finicio) fInicio, MAX(ffin) fFin FROM tAgendaBitacora WITH (NOLOCK) WHERE nIdComision = ? " + "GROUP BY nIdComision";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, idComision);
            rs = ps.executeQuery();
            if (rs.next()) {
                agenda.setFechaInicio(Date.valueOf(rs.getString("fInicio")));
                agenda.setFechaFin(Date.valueOf(rs.getString("fFin")));
                agenda.setIdComision(rs.getInt("nidcomision"));
            }
            return agenda;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }

    public static Agenda consultaAgenda(Connection conn, int idAgenda) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Agenda agenda = new Agenda();
        String query = "SELECT nidcomision, finicio , ffin  FROM tAgenda WITH (NOLOCK) WHERE nIdAgenda = ? ";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, idAgenda);
            rs = ps.executeQuery();
            if (rs.next()) {
                agenda.setFechaInicio(Date.valueOf(rs.getString("finicio")));
                agenda.setFechaFin(Date.valueOf(rs.getString("ffin")));
                agenda.setIdComision(Integer.parseInt(rs.getString("nidcomision")));
                agenda.setIdAgenda(idAgenda);
            }
            return agenda;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }

    public static String consultaEstatusAgenda(Connection conn, int idComision) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String estatus = "";
        String query = "SELECT ISNULL(cDocHaplicado, '')  cDocHaplicado FROM tComision WITH (NOLOCK) WHERE nIdComision = ? ";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, idComision);
            rs = ps.executeQuery();
            if (rs.next()) {
                estatus = rs.getString(1);
            }
            return estatus;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }
}
