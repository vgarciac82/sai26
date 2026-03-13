package com.syc.obrapublica.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfiguraAplicativoManager extends DataSourceManager {

    private static Map<String, String> SYSTEM_PROPERTIES = null;

    private static final Logger log = LoggerFactory.getLogger(ConfiguraAplicativoManager.class);

    public ConfiguraAplicativoManager() {
        super();
        synchronized (this) {
            if (SYSTEM_PROPERTIES == null) {
                inicializaPropiedades();
            }
        }
    }

    public static int agregaValidaDocto(Connection conn, int id_tc, String cRamo, String cUnidadResponsable, String cEjercicioFiscal, String cLLave, String cValor) throws SQLException {
        int iAgregado = 0;
        PreparedStatement pstmnt = null;
        boolean retval = false;
        try {
            pstmnt = conn.prepareStatement("insert into ConfiguraDocumento (ID_TC ,cRamo,cUnidadResponsable, CD_NOMBRE ,CD_VALOR,cEjercicioFiscal) values (?,?,?,?,?,?)");
            pstmnt.setInt(1, id_tc);
            pstmnt.setString(2, cRamo);
            pstmnt.setString(3, cUnidadResponsable);
            pstmnt.setString(4, cLLave);
            pstmnt.setString(5, cValor);
            pstmnt.setString(6, cEjercicioFiscal);
            retval = pstmnt.execute();
            iAgregado++;
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
        }
        return iAgregado;
    }

    public static ArrayList layoutHeader(Connection conn, int cLayout) throws SQLException {
        ArrayList arrLHeader = new ArrayList();
        String cQuery = "select c_tipolayout, d_layout, d_tabla, d_tablatrabajo, d_rutaarchivoorigen, d_Delimitador, d_Operacion, d_Sql, d_descripcion, d_email, d_observacion, d_username, n_titulos, D_POSTSQL, d_nombrereporte, tipoCaso from t_layout l where l.c_layout = " + cLayout + " ";
        arrLHeader = execQuery(conn, cQuery);
        return arrLHeader;
    }

    public static ArrayList layoutDetalle(Connection conn, int cLayout) throws SQLException {
        String cQuery = "select [c_layout], [b_pk], [b_autoincrementa], [d_nombre], [d_alias], [d_tipo], [n_longitud], [d_formato], [b_visible], [d_constante], [d_campodestino], [d_sql], [d_param_in], [d_tipo_gen], [b_Ignorar], [n_longitudfija] from t_layoutcont l where l.c_layout = " + cLayout + " order by c_layoutCont";
        ArrayList arrLDetalle = new ArrayList();
        arrLDetalle = execQuery(conn, cQuery);
        return arrLDetalle;
    }

    public static int actTablaLayout(Connection conn, String elQuery) throws SQLException {
        int iCreaRegistro = 0;
        PreparedStatement pstmnu = null;
        boolean retval = false;
        int iExiste = 0;
        int iCreaSuficiencia = 0;
        String cUnidad = "";
        try {
            ConfiguraAplicativoBusinessLogic conAPP = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
            pstmnu = conn.prepareStatement(elQuery);
            retval = pstmnu.execute();
        } finally {
            if (pstmnu != null) {
                pstmnu.close();
            }
            pstmnu = null;
        }
        return iCreaRegistro;
    }

    public static String obtenPasswordRemoto(Connection conn) throws Exception {
        String ruta = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT gp_valor FROM CG_GRUPO_PROPIEDADES WHERE G_NOMBRE = 'PREFERENCIAS_CLIENTE' AND GP_NOMBRE = 'p_usuario_remoto'";
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next())
                ruta = rs.getString("gp_valor");
            return ruta;
        } finally {
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rs, false);
        }
    }

    public static String obtenDominioRemoto(Connection conn) throws Exception {
        String ruta = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT gp_valor FROM CG_GRUPO_PROPIEDADES WHERE G_NOMBRE = 'PREFERENCIAS_CLIENTE' AND GP_NOMBRE = 'dominio_remoto'";
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next())
                ruta = rs.getString("gp_valor");
            return ruta;
        } finally {
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rs, false);
        }
    }

    public static String obtenUsuarioRemotoFurrt(Connection conn) throws Exception {
        String ruta = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT gp_valor FROM CG_GRUPO_PROPIEDADES WHERE G_NOMBRE = 'PREFERENCIAS_CLIENTE' AND GP_NOMBRE = 'usuario_remoto'";
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next())
                ruta = rs.getString("gp_valor");
            return ruta;
        } finally {
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rs, false);
        }
    }

    public static String obtenRutaArchivosFurrt(Connection conn) throws Exception {
        String ruta = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT gp_valor FROM CG_GRUPO_PROPIEDADES WHERE G_NOMBRE = 'PREFERENCIAS_CLIENTE' AND GP_NOMBRE = 'ruta_archivos_flujos'";
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next())
                ruta = rs.getString("gp_valor");
            return ruta;
        } finally {
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rs, false);
        }
    }

    public static String replicaCataPresup(Connection conn, String aEjercicioFiscal, String cNewEjercicioFiscal, String cReplicaCatalogos, String cCierreAnual) throws SQLException {
        String cMensaje = "Operación realizada con éxito.";
        String cQuerySubCuenta = "select NombreCatalogo, cSubcuenta from tTipoSubcuentaConf where aEjercicioFiscal = ? order by cSubcuenta";
        String cQueryCampos = "select a.name from sys.all_columns a, sys.all_objects o where a.object_id = o.object_id AND o.name = ? ";
        String cTablaReplicar = "";
        String cCampos = "";
        String cCamposValues = "";
        String cCamposInsert = "";
        String cDesactivaEjercicioFiscal = "";
        String cCamposIn = "";
        String cQueryReplica = "";
        String cToken = "";
        String cTokenIn = "";
        HashMap<String, String> map = new HashMap<String, String>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        PreparedStatement psc = null;
        ResultSet rsc = null;
        PreparedStatement psa = null;
        PreparedStatement psb = null;
        PreparedStatement psj = null;
        PreparedStatement psd = null;
        PreparedStatement pse = null;
        PreparedStatement psf = null;
        PreparedStatement psg = null;
        PreparedStatement psi = null;
        boolean retval = false;
        try {
            if ("SI".equals(cReplicaCatalogos)) {
                // Catalogos Configurados en Tabla tTipoSubcuentaConf
                ps = conn.prepareStatement(cQuerySubCuenta);
                ps.setString(1, aEjercicioFiscal);
                rs = ps.executeQuery();
                while (rs.next()) {
                    cTablaReplicar = rs.getString("NombreCatalogo");
                    if (!map.containsKey(cTablaReplicar)) {
                        try {
                            psc = conn.prepareStatement(cQueryCampos);
                            psc.setString(1, cTablaReplicar);
                            rsc = psc.executeQuery();
                            while (rsc.next()) {
                                cCampos = rsc.getString("name");
                                if (!"aEjercicioFiscal".equals(cCampos)) {
                                    cCamposValues += cToken + cCampos;
                                } else {
                                    cCamposValues += cToken + "'" + cNewEjercicioFiscal + "'";
                                    cCamposIn += cTokenIn + cCampos;
                                    cTokenIn = "+";
                                }
                                cCamposInsert += cToken + cCampos;
                                cToken = ",";
                            }
                            cQueryReplica = "INSERT INTO " + cTablaReplicar + "(" + cCamposInsert + ") " + " SELECT " + cCamposValues + " FROM " + cTablaReplicar + " WHERE aEjercicioFiscal = '" + aEjercicioFiscal + "'" + " AND " + cCamposIn + " not in (SELECT " + cCamposIn + " FROM " + cTablaReplicar + " WHERE aEjercicioFiscal = '" + cNewEjercicioFiscal + "' )";
                            psi = conn.prepareStatement(cQueryReplica);
                            retval = psi.execute();
                        } finally {
                            if (psc != null)
                                psc.close();
                            if (rsc != null)
                                rsc.close();
                            psc = null;
                            rsc = null;
                            if (psi != null)
                                psi.close();
                            psi = null;
                        }
                    }
                }
                // Catalogos de Configuracion del
                String cQuerySubCuentaConf = "INSERT INTO tTipoSubcuentaConf (cSubcuenta, aEjercicioFiscal, nOrden, Interno, NombreCatalogo, NombreCampo, EtiquetaCampo, CentroCostos) " + " SELECT cSubcuenta, '" + cNewEjercicioFiscal + "', nOrden, Interno, NombreCatalogo, NombreCampo, EtiquetaCampo, CentroCostos FROM  tTipoSubcuentaConf WHERE aEjercicioFiscal = '" + aEjercicioFiscal + "'";
                String cQueryEventoConf = "INSERT INTO tEventoConfiguracion ( cEvento, nCuenta, aEjercicioFiscal, nDocRenglon, TipoCuenta, dComponente) " + " SELECT cEvento, nCuenta, '" + cNewEjercicioFiscal + "', nDocRenglon, TipoCuenta, dComponente FROM tEventoConfiguracion WHERE aEjercicioFiscal =  '" + aEjercicioFiscal + "' " + "    AND cEvento+nCuenta+convert(varchar,nDocRenglon)+TipoCuenta+dComponente not in (SELECT cEvento+nCuenta+convert(varchar,nDocRenglon)+TipoCuenta+dComponente FROM tEventoConfiguracion WHERE aEjercicioFiscal = '" + cNewEjercicioFiscal + "' )";
                String cQueryEventoConfDet = "INSERT INTO tEventoConfiguraDetalle (cEvento, nCuenta, aEjercicioFiscal, nDocRenglon, nOrden, dDetalleCuenta ) " + "SELECT cEvento, nCuenta, '" + cNewEjercicioFiscal + "', nDocRenglon, nOrden, dDetalleCuenta FROM tEventoConfiguraDetalle WHERE aEjercicioFiscal = '" + aEjercicioFiscal + "' " + "  AND cEvento+nCuenta+convert(varchar,nDocRenglon)+convert(varchar,nOrden)+dDetalleCuenta not in (SELECT cEvento+nCuenta+convert(varchar,nDocRenglon)+convert(varchar,nOrden)+dDetalleCuenta FROM tEventoConfiguraDetalle WHERE aEjercicioFiscal = '" + cNewEjercicioFiscal + "')";
                String cQueryCatUnidadResp = "INSERT INTO tCatUnidadResponsable ( aEjercicioFiscal, cUnidadResponsable, D_DESCRIPCION, nAlcance) " + " SELECT '" + cNewEjercicioFiscal + "', cUnidadResponsable, D_DESCRIPCION, nAlcance FROM tCatUnidadResponsable WHERE aEjercicioFiscal = '" + aEjercicioFiscal + "'" + "   AND cUnidadResponsable+D_DESCRIPCION+convert(varchar,nAlcance) not in ( SELECT cUnidadResponsable+D_DESCRIPCION+convert(varchar,nAlcance) FROM tCatUnidadResponsable WHERE aEjercicioFiscal = '" + cNewEjercicioFiscal + "') ";
                String cQueryCatalogoURCC = " INSERT INTO tCatalogoURCC (aEjercicioFiscal, cUnidadResponsable, cCentroContable, nConsecutivo) " + " SELECT  '" + cNewEjercicioFiscal + "', cUnidadResponsable, cCentroContable, nConsecutivo FROM tCatalogoURCC WHERE aEjercicioFiscal = '" + aEjercicioFiscal + "'" + "    AND cUnidadResponsable+cCentroContable+convert(varchar,nConsecutivo) not in (SELECT cUnidadResponsable+cCentroContable+convert(varchar,nConsecutivo) FROM tCatalogoURCC WHERE aEjercicioFiscal = '" + cNewEjercicioFiscal + "')";
                String cQueryPartidaCuenta = "INSERT INTO  tCatalogoPartidaCuenta ( cPartida, aEjercicioFiscal, nCuenta) " + " SELECT cPartida, '" + cNewEjercicioFiscal + "', nCuenta FROM tCatalogoPartidaCuenta WHERE aEjercicioFiscal = '" + aEjercicioFiscal + "' " + "    AND cPartida+nCuenta not in ( SELECT cPartida+nCuenta FROM tCatalogoPartidaCuenta WHERE aEjercicioFiscal = '" + cNewEjercicioFiscal + "' )";
                String cQueryCaratulaCuenta = "INSERT INTO tCatalogoCaratulaCuenta ( ccaratula, aEjercicioFiscal, nCuenta) " + " SELECT ccaratula, '" + cNewEjercicioFiscal + "', nCuenta FROM tCatalogoCaratulaCuenta WHERE aEjercicioFiscal = '" + aEjercicioFiscal + "'" + "    AND ccaratula+nCuenta not in (SELECT ccaratula+nCuenta  FROM tCatalogoCaratulaCuenta WHERE aEjercicioFiscal = '" + cNewEjercicioFiscal + "') ";
                psa = conn.prepareStatement(cQuerySubCuentaConf);
                retval = psa.execute();
                psb = conn.prepareStatement(cQueryEventoConf);
                retval = psb.execute();
                psj = conn.prepareStatement(cQueryEventoConfDet);
                retval = psj.execute();
                psd = conn.prepareStatement(cQueryCatUnidadResp);
                retval = psd.execute();
                pse = conn.prepareStatement(cQueryCatalogoURCC);
                retval = pse.execute();
                psf = conn.prepareStatement(cQueryPartidaCuenta);
                retval = psf.execute();
                psg = conn.prepareStatement(cQueryCaratulaCuenta);
                retval = psg.execute();
            }
            if ("SI".equals(cCierreAnual)) {
                cDesactivaEjercicioFiscal = "UPDATE tEjercicioFiscal SET cActivo = 1 WHERE aEjercicioFiscal = '" + cNewEjercicioFiscal + "' ";
                psc = conn.prepareStatement(cDesactivaEjercicioFiscal);
                retval = psc.execute();
                cDesactivaEjercicioFiscal = "UPDATE tEjercicioFiscal SET cActivo = 0 WHERE aEjercicioFiscal = '" + aEjercicioFiscal + "' ";
                psi = conn.prepareStatement(cDesactivaEjercicioFiscal);
                retval = psi.execute();
            }
        } finally {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
            ps = null;
            rs = null;
            if (psc != null)
                psc.close();
            if (psi != null)
                psi.close();
            psc = null;
            psi = null;
            if (psa != null)
                psa.close();
            psa = null;
            if (psb != null)
                psb.close();
            psb = null;
            if (psd != null)
                psd.close();
            psd = null;
            if (pse != null)
                pse.close();
            pse = null;
            if (psf != null)
                psf.close();
            psf = null;
            if (psg != null)
                psg.close();
            psg = null;
        }
        return cMensaje;
    }

    public static ArrayList execQuery(Connection conn, String cQuery) throws SQLException {
        Map<String, String> datarecord = null;
        ArrayList arrReturnQuery = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String[] cColQuery = null;
        int[] cColType = null;
        try {
            pstmnt = conn.prepareStatement(cQuery);
            rs = pstmnt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();
            int i = 0;
            arrReturnQuery.add(numberOfColumns);
            cColQuery = new String[numberOfColumns];
            cColType = new int[numberOfColumns];
            while (i < numberOfColumns) {
                cColQuery[i] = rsmd.getColumnName(i + 1);
                cColType[i] = rsmd.getColumnType(i + 1);
                i++;
            }
            arrReturnQuery.add(cColQuery);
            arrReturnQuery.add(cColType);
            boolean enviar = false;
            i = 0;
            int j = 0;
            while (rs.next()) {
                ArrayList arrDataQuery = new ArrayList();
                while (i < numberOfColumns) {
                    arrDataQuery.add(rs.getString(cColQuery[i]));
                    i++;
                }
                // System.out.println("Renglon:"+j);
                arrReturnQuery.add(arrDataQuery);
                i = 0;
                j++;
            }
            // System.out.println("termina lectura con un total de registros:"+j);
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            if (rs != null)
                rs.close();
            pstmnt = null;
            rs = null;
        }
        return arrReturnQuery;
    }

    public String getPropiedadSistema(String propiedad) {
        if (SYSTEM_PROPERTIES == null)
            inicializaPropiedades();
        return SYSTEM_PROPERTIES == null ? "" : SYSTEM_PROPERTIES.get(propiedad);
    }

    private void inicializaPropiedades() {
        Connection conn = null;
        String query = "SELECT * FROM CG_GRUPO_PROPIEDADES WITH(nolock) WHERE G_NOMBRE = 'PREFERENCIAS_CLIENTE'";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection(GestionInterface.ATT_CONEXION);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (SYSTEM_PROPERTIES == null)
                    SYSTEM_PROPERTIES = new HashMap<String, String>();
                SYSTEM_PROPERTIES.put(rs.getString("GP_NOMBRE"), rs.getString("GP_VALOR"));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
            CloseObject.closeObject(conn);
        }
    }

    public static String obtenRutaRemoto(Connection conn) throws Exception {
        String ruta = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT gp_valor FROM CG_GRUPO_PROPIEDADES WHERE G_NOMBRE = 'PREFERENCIAS_CLIENTE' AND GP_NOMBRE = 'ruta_remoto'";
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next())
                ruta = rs.getString("gp_valor");
            return ruta;
        } finally {
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rs, false);
        }
    }

    public static String getSystemSetting(Connection conn, String settingName) throws Exception {
        String query = "select gp_valor from CG_GRUPO_PROPIEDADES with(nolock) where G_NOMBRE = 'PREFERENCIAS_CLIENTE' and GP_NOMBRE = ?";
        ResultSet rs = null;
        PreparedStatement ps = null;
        String val = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, settingName);
            rs = ps.executeQuery();
            if (rs.next())
                val = rs.getString(1);
            return val;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static String getSystemSetting(Connection conn, String settingGName, String settingGPName) throws Exception {
        String query = "select gp_valor from CG_GRUPO_PROPIEDADES with(nolock) where G_NOMBRE = ? and GP_NOMBRE = ?";
        ResultSet rs = null;
        PreparedStatement ps = null;
        String val = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, settingGName);
            ps.setString(2, settingGPName);
            rs = ps.executeQuery();
            if (rs.next())
                val = rs.getString(1);
            return val;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }
}
