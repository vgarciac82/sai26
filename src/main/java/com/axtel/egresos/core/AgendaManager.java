package com.axtel.egresos.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import com.axtel.egresos.viaticos.Agenda;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class AgendaManager {

    public static List<Agenda> consultaAgendas(Connection conn, int idEmpleado) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT formato_viaticos_id idAgenda, formato.fecha_inicio fechaInicio, formato.fecha_fin fechaFin, agenda.cat_paises_id idPais ");
        query.append(" , agenda.cat_municipios_id idMunicipio, localidad, REPLACE(str_motivos, '?', '') motivoComision, str_actividades actividades");
        query.append(" , pais.nombre pais, municipio.entidad, municipio.municipio, municipio.entidad_id idEstado");
        query.append(" FROM formato ");
        query.append("  INNER JOIN formato_viaticos viat ON formato.id = viat.formato_id ");
        query.append("  INNER JOIN viaticos_agenda agenda ON viat.id = agenda.formato_viaticos_id ");
        query.append("  INNER JOIN cat_paises pais ON pais.id = agenda.cat_paises_id ");
        query.append("  INNER JOIN cat_municipios municipio ON agenda.cat_municipios_id = municipio.id ");
        query.append(" WHERE numero_empleado = ? AND (bAceptado IS NULL OR bAceptado = 0 or bautoriza IS NULL OR bautoriza = 0) ");
        query.append(" AND autorizado = 1  AND year(formato.fecha_inicio) >= YEAR(NOW())-1  ORDER BY formato_viaticos_id DESC");
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Agenda> formato = new ArrayList<Agenda>();
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, idEmpleado);
            rs = ps.executeQuery();
            while (rs.next()) {
                Agenda agenda = new Agenda();
                agenda.setFechaInicio(Date.valueOf(rs.getString("fechaInicio")));
                agenda.setFechaFin(Date.valueOf(rs.getString("fechaFin")));
                agenda.setIdAgenda(Integer.parseInt(rs.getString("idAgenda")));
                agenda.setIdPais(Integer.parseInt(rs.getString("idPais")));
                agenda.setIdEstado(Integer.parseInt(rs.getString("idEstado")));
                agenda.setEntidad(rs.getString("entidad"));
                agenda.setMunicipio(rs.getString("municipio"));
                agenda.setPais(rs.getString("pais"));
                agenda.setIdMunicipio(Integer.parseInt(rs.getString("idMunicipio")));
                agenda.setLocalidad(rs.getString("localidad"));
                agenda.setMotivoComision(rs.getString("motivoComision"));
                agenda.setActividades(rs.getString("actividades"));
                formato.add(agenda);
            }
            return formato;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static Agenda consultaAgenda(Connection conn, int idAgenda) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT formato_viaticos_id idAgenda, formato.fecha_inicio fechaInicio, formato.fecha_fin fechaFin, agenda.cat_paises_id idPais ");
        query.append(" , agenda.cat_municipios_id idMunicipio, localidad, str_motivos motivoComision, str_actividades actividades");
        query.append(" , pais.nombre pais, municipio.entidad, municipio.municipio, municipio.entidad_id idEstado");
        query.append(" FROM formato ");
        query.append("  INNER JOIN formato_viaticos viat ON formato.id = viat.formato_id ");
        query.append("  INNER JOIN viaticos_agenda agenda ON viat.id = agenda.formato_viaticos_id ");
        query.append("  INNER JOIN cat_paises pais ON pais.id = agenda.cat_paises_id ");
        query.append("  INNER JOIN cat_municipios municipio ON agenda.cat_municipios_id = municipio.id ");
        query.append(" WHERE formato_viaticos_id = ?  AND agenda.cat_paises_id <> 146");
        query.append(" UNION ");
        query.append("SELECT formato_viaticos_id idAgenda, formato.fecha_inicio fechaInicio, formato.fecha_fin fechaFin, agenda.cat_paises_id idPais ");
        query.append(" , agenda.cat_municipios_id idMunicipio, localidad, str_motivos motivoComision, str_actividades actividades");
        query.append(" , pais.nombre pais, municipio.entidad, municipio.municipio, municipio.entidad_id idEstado");
        query.append(" FROM formato ");
        query.append("  INNER JOIN formato_viaticos viat ON formato.id = viat.formato_id ");
        query.append("  INNER JOIN viaticos_agenda agenda ON viat.id = agenda.formato_viaticos_id ");
        query.append("  INNER JOIN cat_paises pais ON pais.id = agenda.cat_paises_id ");
        query.append("  INNER JOIN cat_municipios municipio ON agenda.cat_municipios_id = municipio.id ");
        query.append(" WHERE formato_viaticos_id = ?  AND agenda.cat_paises_id = 146");
        PreparedStatement ps = null;
        ResultSet rs = null;
        Agenda agenda = new Agenda();
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, idAgenda);
            ps.setInt(2, idAgenda);
            rs = ps.executeQuery();
            if (rs.next()) {
                agenda.setFechaInicio(Date.valueOf(rs.getString("fechaInicio")));
                agenda.setFechaFin(Date.valueOf(rs.getString("fechaFin")));
                agenda.setIdPais(Integer.parseInt(rs.getString("idPais")));
                agenda.setIdEstado(Integer.parseInt(rs.getString("idEstado")));
                agenda.setIdMunicipio(Integer.parseInt(rs.getString("idMunicipio")));
                agenda.setLocalidad(rs.getString("localidad"));
                agenda.setMotivoComision(rs.getString("motivoComision"));
                agenda.setActividades(rs.getString("actividades"));
            }
            return agenda;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static String guardaAgenda(Connection conn, Agenda agenda) throws Exception {
        PreparedStatement pst = null, pst2 = null;
        ResultSet rs = null;
        String folio = "";
        try {
            agenda = buscarMunicipioSAI(conn, agenda);
            if (agenda.getIdPais() != 146) {
                buscarEstado(conn, agenda);
                buscarMunicipioInternacional(conn, agenda);
            }
            int comisionExiste = existeAgenda(conn, agenda);
            if (comisionExiste == 0) {
                String query2 = "INSERT INTO tViaticosComisiones (fInicio,fFin,cConcepto_Comision,nIdPais,ID_ESTADO,ID_MUNICIPIO,cLocalidad,cEstatus) VALUES ( ?,?,?,?,?,?,?,'A')";
                pst2 = conn.prepareStatement(query2);
                pst2.setDate(1, (Date) agenda.getFechaInicio());
                pst2.setDate(2, (Date) agenda.getFechaFin());
                pst2.setString(3, agenda.getNombreComision());
                pst2.setInt(4, agenda.getIdPais());
                pst2.setInt(5, agenda.getIdEstado());
                pst2.setInt(6, agenda.getIdMunicipio());
                pst2.setString(7, agenda.getLocalidad());
                pst2.executeUpdate();
                String query = "SELECT	MAX(nIdComision) AS nIdComision FROM tViaticosComisiones WITH (NOLOCK)";
                pst = conn.prepareStatement(query);
                rs = pst.executeQuery();
                if (rs.next()) {
                    folio = rs.getString(1);
                }
            } else {
                folio = Integer.toString(comisionExiste);
            }
            return folio;
        } finally {
            CloseObject.closeObject(pst);
            CloseObject.closeObject(pst2);
            CloseObject.closeObject(rs);
        }
    }

    private static void buscarEstado(Connection conn, Agenda agenda) throws Exception {
        PreparedStatement pst = null, pst2 = null, pst3 = null;
        int existe = 0;
        ResultSet rs = null, rs2 = null;
        try {
            pst = conn.prepareStatement("SELECT COUNT(*) FROM CAT_ESTADOS (NOLOCK) WHERE EDO_NOMBRE LIKE '%" + agenda.getLocalidad() + "%' AND ID_PAIS = " + agenda.getIdPais());
            rs = pst.executeQuery();
            if (rs.next()) {
                existe = rs.getInt(1);
            }
            if (existe == 0) {
                pst2 = conn.prepareStatement("INSERT INTO CAT_ESTADOS (EDO_NOMBRE, EDO_ABREVIATURA, ID_PAIS) VALUES (?, NULL, ?)");
                pst2.setString(1, agenda.getLocalidad());
                pst2.setInt(2, agenda.getIdPais());
                pst2.executeUpdate();
                pst3 = conn.prepareStatement("SELECT MAX(ID_ESTADO) ID_ESTADO FROM CAT_ESTADOS (NOLOCK)");
                rs2 = pst3.executeQuery();
                if (rs2.next()) {
                    int idEdoNuevo = rs2.getInt(1);
                    agenda.setIdEstado(idEdoNuevo);
                }
            } else {
                pst2 = conn.prepareStatement("SELECT ID_ESTADO FROM CAT_ESTADOS (NOLOCK) WHERE EDO_NOMBRE LIKE '%" + agenda.getLocalidad() + "%' AND ID_PAIS = " + agenda.getIdPais());
                rs2 = pst2.executeQuery();
                if (rs2.next()) {
                    int idEdoNuevo = rs2.getInt(1);
                    agenda.setIdEstado(idEdoNuevo);
                }
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rs2);
            CloseObject.closeObject(pst);
            CloseObject.closeObject(pst2);
            CloseObject.closeObject(pst3);
        }
    }

    private static void buscarMunicipioInternacional(Connection conn, Agenda agenda) throws Exception {
        PreparedStatement pst = null, pst2 = null, pst3 = null;
        int existe = 0;
        ResultSet rs = null, rs2 = null;
        try {
            pst = conn.prepareStatement("SELECT COUNT(*) FROM CAT_MUNICIPIO (NOLOCK) WHERE MPO_NOMBRE LIKE '%" + agenda.getLocalidad() + "%' AND ID_ESTADO = " + agenda.getIdEstado());
            rs = pst.executeQuery();
            if (rs.next()) {
                existe = rs.getInt(1);
            }
            if (existe == 0) {
                pst2 = conn.prepareStatement("INSERT INTO CAT_MUNICIPIO (ID_ESTADO, MPO_NOMBRE) VALUES ( ?, ? )");
                pst2.setInt(1, agenda.getIdEstado());
                pst2.setString(2, agenda.getLocalidad());
                pst2.executeUpdate();
                pst3 = conn.prepareStatement("SELECT MAX(ID_MUNICIPIO) ID_MUNICIPIO FROM CAT_MUNICIPIO (NOLOCK)");
                rs2 = pst3.executeQuery();
                if (rs2.next()) {
                    int idNuevo = rs2.getInt(1);
                    agenda.setIdMunicipio(idNuevo);
                    ;
                }
            } else {
                pst2 = conn.prepareStatement("SELECT ID_MUNICIPIO FROM CAT_MUNICIPIO (NOLOCK) WHERE MPO_NOMBRE LIKE '%" + agenda.getLocalidad() + "%' AND ID_ESTADO = " + agenda.getIdEstado());
                rs2 = pst2.executeQuery();
                if (rs2.next()) {
                    int idNuevo = rs2.getInt(1);
                    agenda.setIdMunicipio(idNuevo);
                }
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rs2);
            CloseObject.closeObject(pst);
            CloseObject.closeObject(pst2);
            CloseObject.closeObject(pst3);
        }
    }

    private static int existeAgenda(Connection conn, Agenda agenda) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        String query = "SELECT nIdComision FROM tViaticosComisiones WHERE fInicio =  ? AND fFin = ? AND ID_MUNICIPIO = ? and cConcepto_Comision = ?";
        int folio = 0;
        pst = conn.prepareStatement(query);
        pst.setDate(1, (Date) agenda.getFechaInicio());
        pst.setDate(2, (Date) agenda.getFechaFin());
        pst.setInt(3, agenda.getIdMunicipio());
        pst.setString(4, agenda.getNombreComision());
        rs = pst.executeQuery();
        if (rs.next()) {
            folio = rs.getInt(1);
        }
        return folio;
    }

    public static Agenda buscarMunicipioSAI(Connection conn, Agenda agenda) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        String query = "SELECT TOP 1 ID_MUNICIPIO FROM CAT_MUNICIPIO_RELOJ_SAI (NOLOCK) WHERE MUNICIPIO_ID_RELOJ = ?";
        pst = conn.prepareStatement(query);
        pst.setInt(1, agenda.getIdMunicipio());
        rs = pst.executeQuery();
        if (rs.next()) {
            agenda.setIdMunicipio(Integer.parseInt(rs.getString("ID_MUNICIPIO")));
        }
        return agenda;
    }

    public static boolean actualizaAgenda(Connection conn, Agenda agenda, int idComision) throws Exception {
        PreparedStatement pst = null;
        boolean actualizo = false;
        String query = "UPDATE tViaticosComisiones SET fInicio = CAST(? AS date), fFin = CAST(? AS date), nIdPais = ?, ID_ESTADO = ?, ID_MUNICIPIO = ?, cLocalidad = ? where nIdComision = ? ";
        agenda = buscarMunicipioSAI(conn, agenda);
        pst = conn.prepareStatement(query);
        pst.setDate(1, (Date) agenda.getFechaInicio());
        pst.setDate(2, (Date) agenda.getFechaFin());
        pst.setInt(3, agenda.getIdPais());
        pst.setInt(4, agenda.getIdEstado());
        pst.setInt(5, agenda.getIdMunicipio());
        pst.setString(6, agenda.getLocalidad());
        pst.setInt(7, idComision);
        int actualizados = pst.executeUpdate();
        if (actualizados > 0) {
            actualizo = true;
        }
        return actualizo;
    }
}
