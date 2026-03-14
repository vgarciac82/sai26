package com.syc.contable.anteproyecto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Sheet;
import com.syc.contable.core.AdecuacionManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComparaSaiSicopBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(ComparaSaiSicopBusinessLogic.class);

    private String uLoginCarga = "";

    /**
     * Construye una nueva instancia del objeto.
     */
    public ComparaSaiSicopBusinessLogic(String uLogin) {
        super.init();
        this.uLoginCarga = uLogin;
    }

    /**
     * Carga la base de datos SICOP desde un flujo de datos abierto y lo
     * almacena en la base de datos.
     */
    public synchronized List<String> cargaExcelComparaSaiSicop(InputStream stream) throws Exception {
        List<String> errores = new ArrayList<String>();
        PreparedStatement psInsertaMomento = null;
        Connection conn = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        try {
            conn = getConnection();
            ComparaSaiSicopManager.limpiaProyecto(conn);
            int renglon = 0;
            String linea = "";
            String query = "INSERT INTO tcomparacion_sai_sicop(ep,momento,imp_anual,imp_ene,imp_feb,imp_mar,imp_abr,imp_may, imp_jun,imp_jul,imp_ago,imp_sep,imp_oct,imp_nov,imp_dic ) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            psInsertaMomento = conn.prepareStatement(query);
            while ((linea = br.readLine()) != null) {
                String[] renglonInfo = linea.split(";");
                if (renglonInfo == null || renglonInfo.length == 1)
                    renglonInfo = linea.split(",");
                if (renglon == 0) {
                    renglon++;
                    continue;
                }
                if ("RHQ".equalsIgnoreCase(renglonInfo[1])) {
                    log.debug("Object: {}", "renglon leido " + renglon);
                    ComparaSaiSicopManager.obtieneDatosEP(conn, renglonInfo, psInsertaMomento);
                }
                renglon++;
            }
            ComparaSaiSicopManager.insertaVersionProyecto(conn, Integer.parseInt(AdecuacionManager.obtenEjercicioFiscal(conn)), uLoginCarga);
            if (errores.size() == 0)
                conn.commit();
            else
                conn.rollback();
            return errores;
        } catch (Exception e) {
            log.error("Error insertando comparación " + e, e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "No se pudo realizar rollback en conexion" + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(psInsertaMomento, false);
            CloseObject.closeObject(conn, false);
        }
    }

    /**
     * Carga la base SICOP dada la ruta de un archivo excel y lo almacena en la
     * base de datos. <br>
     */
    public List<String> cargaExcelComparaSaiSicop(String file) throws Exception {
        File f = null;
        FileInputStream stream = null;
        try {
            f = new File(file);
            if (!f.exists())
                throw new FileNotFoundException("No se encontro el archivo de carga[ " + file + "]");
            stream = new FileInputStream(f);
            return cargaExcelComparaSaiSicop(stream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            if (stream != null)
                try {
                    stream.close();
                } catch (Exception e2) {
                    log.warn("Object: {}", "No se pudo cerrar el archivo de carga." + e2);
                }
            stream = null;
        }
    }

    public List<List<String>> consultaReporte(String momento) throws SQLException {
        List<List<String>> arrDiferencias = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrDiferencias = (ComparaSaiSicopManager.consultaExportaReporte(conn, momento));
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return arrDiferencias;
    }

    public Sheet consultaReporteExcel(Sheet hoja, String momento) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            hoja = ComparaSaiSicopManager.consultaExportaReporteExcel(conn, hoja, momento);
        } finally {
            CloseObject.closeObject(conn, false);
        }
        return hoja;
    }
}
