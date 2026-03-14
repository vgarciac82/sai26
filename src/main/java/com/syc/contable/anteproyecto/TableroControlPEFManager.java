package com.syc.contable.anteproyecto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager para el tablero de control.
 *
 * @author Vicente Garcia Carrillo
 */
public class TableroControlPEFManager {

    private static final Logger log = LoggerFactory.getLogger(TableroControlPEFManager.class);

    public static Map<String, Map<String, TableroControlBean>> cargaInicialPEF(Connection conn) throws Exception {
        Map<String, Map<String, TableroControlBean>> tablero = new LinkedHashMap<String, Map<String, TableroControlBean>>();
        String queryUN = "SELECT cunidadresponsable " + "FROM   tcatunidadresponsable " + "WHERE  nalcance = 1  " + "       AND cunidadresponsable LIKE 'B%' " + "ORDER  BY cunidadresponsable ";
        String queryUE = "SELECT cunidadresponsable " + "FROM   tcatunidadresponsable " + "WHERE  cunidadresponsable LIKE 'B%' " + "ORDER  BY cunidadresponsable ";
        PreparedStatement psUN = null;
        PreparedStatement psUE = null;
        ResultSet rsUN = null;
        ResultSet rsUE = null;
        List<String> ue = new ArrayList<String>();
        List<String> un = new ArrayList<String>();
        try {
            psUE = conn.prepareStatement(queryUE);
            rsUE = psUE.executeQuery();
            while (rsUE.next()) ue.add(rsUE.getString(1));
            psUN = conn.prepareStatement(queryUN);
            rsUN = psUN.executeQuery();
            while (rsUN.next()) un.add(rsUN.getString(1));
            for (Iterator<String> i = ue.iterator(); i.hasNext(); ) {
                String ueStr = i.next();
                Map<String, TableroControlBean> unidadNormativa = new LinkedHashMap<String, TableroControlBean>();
                for (Iterator<String> j = un.iterator(); j.hasNext(); ) {
                    String unStr = j.next();
                    TableroControlBean bean = new TableroControlBean(ueStr, unStr, 0, 0);
                    unidadNormativa.put(unStr, bean);
                }
                tablero.put(ueStr, unidadNormativa);
            }
            return tablero;
        } finally {
            CloseObject.closeObject(rsUN, false);
            CloseObject.closeObject(rsUE, false);
            CloseObject.closeObject(psUN, false);
            CloseObject.closeObject(psUE, false);
        }
    }

    /**
     * Revisa el estado de la captura del calendario.
     *
     * @param conn
     *            Conexion activa a la base de datos.
     * @param tablero
     *            Tablero de control de calendario
     * @return Tablero de control actualizado.
     * @throws Exception
     */
    public static Map<String, Map<String, TableroControlBean>> obtenEstatusCalendario(Connection conn, Map<String, Map<String, TableroControlBean>> tablero) throws Exception {
        String query = "SELECT cunidadejecutorapef, " + "       cunidadnormativapef, " + "       total,  " + "       total_capturados " + "FROM   vtablero_control_pef WITH(nolock) " + "ORDER  BY cunidadejecutorapef,  " + "          cunidadnormativapef ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                String un = rs.getString("cunidadnormativapef");
                String ue = rs.getString("cunidadejecutorapef");
                int total = rs.getInt("total");
                int totalCapturados = rs.getInt("total_capturados");
                log.debug("Object: {}", "Actualizando Unidad Ejecutora: " + ue + " Unidad Normativa: " + un);
                Map<String, TableroControlBean> ueMap = tablero.get(ue.trim());
                TableroControlBean bean = ueMap.get(un.trim());
                bean.setTotalCalendario(total);
                bean.setTotalCapturado(totalCapturados);
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return tablero;
    }
}
