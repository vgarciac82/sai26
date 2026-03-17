package com.axtel.egresos.viaticos.core;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import com.axtel.egresos.viaticos.Viaticos;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.contabilidad.utils.db.RSToTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ViaticosDAO {

    private static final Logger log = LoggerFactory.getLogger(ViaticosDAO.class);

    public static int insertarViaticos(Connection conn, Viaticos Viaticos) throws Exception {
        StringBuilder queryInsert = new StringBuilder();
        queryInsert.append(" INSERT INTO tViaticos  (nidAgenda, mcuotaPorDia, cmoneda , mtipoCambio, ctieneHomologacion, nnivelHomologar, cplazaHomologar, cjustificacion, ctienePaquete, nidPaquete )");
        queryInsert.append("	VALUES (?, ?, ?,?, ?,?, ?,?, ?,? )");
        PreparedStatement ps = null;
        int insertados = 0;
        try {
            int cnt = 1;
            ps = conn.prepareStatement(queryInsert.toString());
            ps.setInt(cnt++, Viaticos.getidAgenda());
            ps.setBigDecimal(cnt++, Viaticos.getCuotaPorDia());
            ps.setString(cnt++, Viaticos.getMoneda());
            ps.setBigDecimal(cnt++, Viaticos.getTipoCambio());
            ps.setInt(cnt++, Viaticos.getTieneHomologacion());
            ps.setInt(cnt++, Viaticos.getNivelHomologar());
            ps.setString(cnt++, Viaticos.getPlazaHomologar());
            ps.setString(cnt++, Viaticos.getJustificacion());
            ps.setInt(cnt++, Viaticos.getTienePaquete());
            ps.setInt(cnt++, Viaticos.getIdPaquete());
            log.debug("Object: " + String.valueOf(queryInsert.toString()));
            insertados = ps.executeUpdate();
            return insertados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static void consultarViaticos(Connection conn, int idAgenda) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT * FROM tViaticos WITH (NOLOCK)");
        query.append(" WHERE nidAgenda = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        Viaticos viatico = new Viaticos();
        log.debug("Object: " + String.valueOf("Consultando el viatico del empleado: " + idAgenda));
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, idAgenda);
            rs = ps.executeQuery();
            if (rs.next()) {
                viatico.setCuotaPorDia(new BigDecimal(rs.getString("mCuotaPorDia")));
                viatico.setMoneda(rs.getString("cMoneda"));
                viatico.setTipoCambio(new BigDecimal(rs.getString("mTipoCambio")));
                viatico.setTieneHomologacion(Integer.parseInt(rs.getString("cTieneHomologacion")));
                viatico.setNivelHomologar(Integer.parseInt(rs.getString("nNivelHomologar")));
                viatico.setPlazaHomologar(rs.getString("cPlazaHomologar"));
                viatico.setJustificacion(rs.getString("cJustificacion"));
                viatico.setTienePaquete(Integer.parseInt(rs.getString("cTienePaquete")));
                viatico.setIdPaquete(Integer.parseInt(rs.getString("nIdPaquete")));
            }
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }

    public static List<Viaticos> consultaViaticos(Connection conn, int idAgenda) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Viaticos> viaticos = new ArrayList<Viaticos>();
        StringBuilder query = new StringBuilder();
        query.append(" SELECT nidAgenda idAgenda, mCuotaPorDia cuotaPorDia, cMoneda moneda, mTipoCambio tipoCambio, cTieneHomologacion tieneHomologacion,");
        query.append(" nNivelHomologar nivelHomologar, cPlazaHomologar plazaHomologar, cJustificacion justificacion, cTienePaquete tienePaquete, nIdPaquete idPaquete ");
        query.append(" FROM tViaticos WITH (NOLOCK) ");
        query.append(" WHERE nidAgenda = ?");
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, idAgenda);
            rs = ps.executeQuery();
            Map<String, String> resultObj = RSToTable.rsToMapCaseSensitive(rs);
            while (resultObj != null) {
                Viaticos nwViatico = new Viaticos();
                BeanUtils.populate(nwViatico, resultObj);
                viaticos.add(nwViatico);
                resultObj = RSToTable.rsToMapCaseSensitive(rs);
            }
            return viaticos;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static int borrarViaticos(Connection conn, int folio) throws Exception {
        String query = "DELETE tViaticos where nidAgenda = ?";
        PreparedStatement ps = null;
        int borrados = 0;
        log.debug("Object: " + String.valueOf("Borrando el viatico del folio: " + folio));
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, folio);
            borrados = ps.executeUpdate();
            return borrados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int borrarViaticosComision(Connection conn, int idcomision) throws Exception {
        String query = "DELETE tViaticos where nidAgenda in (select nidAgenda from tAgenda (nolock) where nIdComision = ?)";
        PreparedStatement ps = null;
        int borrados = 0;
        log.debug("Object: " + String.valueOf("Borrando el viatico de la comision: " + idcomision));
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, idcomision);
            borrados = ps.executeUpdate();
            return borrados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int actualizarDatosViaticos(Connection conn, Viaticos viatico) throws Exception {
        PreparedStatement pst = null;
        String query = "UPDATE  tViaticos SET mCuotaPorDia = ?, cTieneHomologacion = ?,  cPlazaHomologar = ?, cJustificacion = ?, nNivelHomologar = ?, cTienePaquete =? , nIdPaquete = ? WHERE nidAgenda= ?";
        try {
            pst = conn.prepareStatement(query);
            pst.setBigDecimal(1, viatico.getCuotaPorDia());
            pst.setInt(2, viatico.getTieneHomologacion());
            pst.setString(3, viatico.getPlazaHomologar());
            pst.setString(4, viatico.getJustificacion());
            pst.setInt(5, viatico.getNivelHomologar());
            pst.setInt(6, viatico.getTienePaquete());
            pst.setInt(7, viatico.getIdPaquete());
            pst.setInt(8, viatico.getidAgenda());
            int actualizados = pst.executeUpdate();
            return actualizados;
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static boolean validaImporteCuota(Connection conn, Viaticos viatico, int idEmpleado, int idPais) throws Exception {
        PreparedStatement pst = null;
        boolean esCorrecto = true;
        BigDecimal cuota = null;
        try {
            //Extraer la cuota
            if (viatico.getTieneHomologacion() == 1) {
                cuota = extraeCuotaHomologa(conn, viatico.getNivelHomologar(), idPais);
            } else {
                cuota = extraeCuotaEmpleado(conn, idEmpleado, idPais);
            }
            if (viatico.getTienePaquete() != 0) {
                BigDecimal porcentaje = extraePorcentajeTarifa(conn, viatico.getIdPaquete());
                cuota = cuota.multiply(porcentaje);
            }
            if (viatico.getCuotaPorDia().compareTo(cuota) != 0) {
                esCorrecto = false;
            }
            return esCorrecto;
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static BigDecimal extraeCuotaEmpleado(Connection conn, int idEmpleado, int idPais) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        BigDecimal cuota = null;
        StringBuilder query = new StringBuilder();
        try {
            String columna_cuota = null;
            if (idPais == 146) {
                columna_cuota = "mCuota_pesos";
            } else {
                columna_cuota = "mCuota_dlls";
            }
            query.append("SELECT " + columna_cuota + " FROM v_empleados_giro WITH (NOLOCK) ");
            query.append(" INNER JOIN tviaticosCuotasDet WITH (NOLOCK) ON SUBSTRING(NIVEL,1,1) = cNivel ");
            query.append("	WHERE CLAVE = ?");
            pst = conn.prepareStatement(query.toString());
            pst.setInt(1, idEmpleado);
            rs = pst.executeQuery();
            if (rs.next()) {
                cuota = rs.getBigDecimal(1);
            }
            return cuota;
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static BigDecimal extraeCuotaHomologa(Connection conn, int nivelHomologa, int idPais) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        BigDecimal cuota = null;
        StringBuilder query = new StringBuilder();
        try {
            String columna_cuota = null;
            if (idPais == 146) {
                columna_cuota = "mImporteMX";
            } else {
                columna_cuota = "mImporteME";
            }
            query.append("SELECT " + columna_cuota + " FROM tCatHomologacionComision WITH (NOLOCK) ");
            query.append("	WHERE nIdHomologacion = ?");
            pst = conn.prepareStatement(query.toString());
            pst.setInt(1, nivelHomologa);
            rs = pst.executeQuery();
            if (rs.next()) {
                cuota = rs.getBigDecimal(1);
            }
            return cuota;
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static BigDecimal extraePorcentajeTarifa(Connection conn, int idPaquete) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        BigDecimal porcentaje = null;
        try {
            pst = conn.prepareStatement("SELECT nPorcentaje FROM tCatPaquetesComision WITH (NOLOCK) WHERE nIdPaquete =  ? ");
            pst.setInt(1, idPaquete);
            rs = pst.executeQuery();
            if (rs.next()) {
                porcentaje = rs.getBigDecimal(1);
            }
            return porcentaje;
        } finally {
            CloseObject.closeObject(pst);
        }
    }
}
