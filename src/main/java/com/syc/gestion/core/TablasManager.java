package com.syc.gestion.core;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import com.syc.admin.servlet.ReportsException;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class TablasManager extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(TablasManager.class);

    public static ArrayList<ArrayList<String>> ExecSp(Connection conn, String[] Campos) throws SQLException {
        Connection con = conn;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        CallableStatement cs = null;
        ArrayList<ArrayList<String>> Datos = new ArrayList<ArrayList<String>>();
        ArrayList<String> fila = null;
        try {
            cs = con.prepareCall("{call sp_BuscaDatoCamposTabla_syc ( ?, ?, ?, ?, ?, ?, ?, ?)}");
            /*cs.setString("@campo","%contrato%");
		cs.setString("@campo2","");
		cs.setString("@campo3","");
		cs.setString("@valor","%SGAPDS-OCLSP-JAL-09-127-rf-lp-PASIVO%");
		cs.setString("@campowhere","ccentrocontable");
		cs.setString("@campowhere2","entidadcontable");
		cs.setString("@campowhere3","");
		cs.setString("@valorwhere","%");*/
            cs.setString("@campo", Campos[0]);
            cs.setString("@campo2", Campos[1]);
            cs.setString("@campo3", Campos[2]);
            cs.setString("@valor", Campos[3]);
            cs.setString("@campowhere", Campos[4]);
            cs.setString("@campowhere2", Campos[5]);
            cs.setString("@campowhere3", Campos[6]);
            cs.setString("@valorwhere", Campos[7]);
            log.info("{call sp_BuscaDatoCamposTabla_syc ( ?, ?, ?, ?, ?, ?, ?, ?)}");
            for (int j = 0; j <= 7; j++) {
                log.info("Object: {}", Campos[j]);
            }
            cs.execute();
            // System.out.println("Hay Resultados>>>"+hayRes);
            rs = cs.getResultSet();
            int i = 0;
            if (rs == null) {
                return Datos;
            } else {
                rsmd = rs.getMetaData();
                while (rs.next()) {
                    fila = new ArrayList<String>();
                    for (i = 1; i <= rsmd.getColumnCount(); i++) {
                        fila.add(rs.getString(i));
                        // System.out.print(rs.getString(i));
                    }
                    Datos.add(fila);
                    //System.out.println();
                }
                //iterator for more resulsetessssssetes
                while (cs.getMoreResults()) {
                    rs = cs.getResultSet();
                    rsmd = rs.getMetaData();
                    while (rs.next()) {
                        fila = new ArrayList<String>();
                        for (i = 1; i <= rsmd.getColumnCount(); i++) {
                            fila.add(rs.getString(i));
                            //System.out.print(rs.getString(i));
                        }
                        Datos.add(fila);
                        //System.out.println();
                    }
                }
                //more resulsetesss
            }
            //else del si hay resultados
            return Datos;
        } catch (Exception e) {
            throw new ReportsException(e);
        } finally {
            try {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(cs, false);
            } catch (Exception e) {
                conn.rollback();
            }
        }
    }

    //metodo
    public static int insertaPolizaEventosDetalle(Connection conn, String Query) throws SQLException {
        int rows = 0;
        try {
            conn.setAutoCommit(false);
            PreparedStatement st = conn.prepareStatement(Query);
            rows = st.executeUpdate();
            return rows;
        } catch (Exception e) {
            throw new ReportsException(e);
        } finally {
            try {
                conn.commit();
                //conn.close();
            } catch (Exception e) {
                conn.rollback();
            }
        }
    }

    //metodo 2
    public static boolean ejecutaQuery(Connection conn, String Query) throws SQLException {
        try {
            conn.setAutoCommit(false);
            PreparedStatement st = conn.prepareStatement(Query);
            st.executeUpdate();
        } catch (Exception e) {
            System.out.println("Falló: " + Query);
            return false;
        }
        System.out.println(Query);
        return true;
    }

    public static String ejecutaQueryrS(Connection conn, String Query) throws SQLException {
        String dato = "";
        ResultSet rs = null;
        try {
            //conn.setAutoCommit(false);
            PreparedStatement st = conn.prepareStatement(Query);
            st.executeQuery();
            rs = st.getResultSet();
            while (rs.next()) {
                dato = rs.getString(1);
            }
        } catch (Exception e) {
            System.err.println("Error en query " + Query);
        } finally {
            try {
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
            }
        }
        return dato;
    }

    public static int actualizaEncabezado(Connection conn, String nFolioDocPoliza) throws SQLException {
        String Query = "update tDocPolizaEncabezado set mTotalCargos=(select SUM(case when cEvento='CARGO' then mImporte else 0 end) " + "from tDocPolizaDetalle where nFolioDocPoliza=" + nFolioDocPoliza + "), mTotalAbonos=(select SUM(case when cEvento='ABONO' then mImporte else 0 end)" + "from tDocPolizaDetalle where nFolioDocPoliza=" + nFolioDocPoliza + ") where nFolioDocPoliza=" + nFolioDocPoliza + "   ";
        int rows = 0;
        //System.out.println("Actualizando encabezados"+Query);
        try {
            conn.setAutoCommit(false);
            PreparedStatement st = conn.prepareStatement(Query);
            rows = st.executeUpdate();
            return rows;
        } catch (Exception e) {
            throw new ReportsException(e);
        } finally {
            try {
                conn.commit();
                conn.close();
            } catch (Exception e) {
                conn.rollback();
            }
        }
    }

    //metodo 2
    public static ArrayList<String> selectDetalleDocPoliza(Connection conn, String nFolioDocPoliza) throws SQLException {
        ArrayList<String> data = new ArrayList<String>();
        Statement st = conn.createStatement();
        ResultSet rs = null;
        final int COLUMNAS = 16;
        String Query = "SELECT ndocrenglon, " + "       SUBSTRING(td.ncuenta,1,22) + '0' AS ncuenta, " + "       dcuenta, " + "       nsubcuenta, " + "       cconcepto, " + "       CASE " + "         WHEN cevento = 'CARGO' THEN mimporte " + "         ELSE 0 " + "       END                 Cargos, " + "       CASE " + "         WHEN cevento = 'ABONO' THEN mimporte " + "         ELSE 0 " + "       END                 Abonos, " + "       parcial, " + "       nidgrupoevento, " + "       nidsubgrupoevento, " + "       cideventomanual, " + "       Isnull(cpartida, '')cPartida, " + "       Isnull(ccabms, '')  cCABMS, " + "       Isnull(ccucop, '')  cCUCOP, " + "       nnumeroevento, " + "       td.ncuenta AS nCuentaAplicacion " + "FROM   tdocpolizadetalle td " + "       INNER JOIN (SELECT ncuenta, " + "                          dcuenta " + "                   FROM   tcuentas)ct " + "               ON nfoliodocpoliza = " + nFolioDocPoliza + " " + "                  AND td.ncuenta = ct.ncuenta " + "ORDER  BY ndocrenglon, " + "          nnumeroevento, " + "          cevento DESC";
        try {
            conn.setAutoCommit(false);
            st.executeQuery(Query);
            rs = st.getResultSet();
            while (rs.next()) {
                for (int i = 1; i <= COLUMNAS; i++) data.add(rs.getString(i));
            }
        } catch (Exception e) {
            throw new ReportsException(e);
        } finally {
            try {
                conn.commit();
                conn.close();
            } catch (Exception e) {
                conn.rollback();
            }
        }
        return data;
    }

    public static String getNfolioDocumento(Connection conn, String nFolioPoliza, String cCentroContable, String cTipoPoliza) throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = null;
        String Query = "select nfolioDocPoliza from tDocPolizaEncabezado where nFolioPoliza=" + nFolioPoliza + " and cCentroContable='" + cCentroContable + "' and cTipoPoliza='" + cTipoPoliza + "'";
        String nFoliodocPoliza = "";
        try {
            conn.setAutoCommit(false);
            st.executeQuery(Query);
            rs = st.getResultSet();
            while (rs.next()) nFoliodocPoliza = rs.getString(1);
        } catch (Exception e) {
            throw new ReportsException(e);
        } finally {
            try {
                conn.commit();
                conn.close();
            } catch (Exception e) {
                conn.rollback();
            }
        }
        return nFoliodocPoliza;
    }

    public static String[] ExecSpDatosPoliza(Connection con, String nFolioDocPoliza) throws SQLException {
        final int COLUMNAS = 12;
        String[] data = new String[COLUMNAS];
        ResultSet rs = null;
        CallableStatement cs = null;
        try {
            cs = con.prepareCall("{call datospolizaManual (?)}");
            cs.setString("@folioDocPoliza", nFolioDocPoliza);
            cs.execute();
            rs = cs.getResultSet();
            int i = 0;
            while (rs.next()) {
                for (i = 1; i <= COLUMNAS; i++) data[i - 1] = rs.getString(i);
            }
            /*  System.out.println("***************************************************");
			System.out.println("Obteniendo datos de poliza Manual: "+nFolioDocPoliza);
			System.out.println("NFolioPoliza: "+data[0]);
			System.out.println("***************************************************");
*/
            return data;
        } catch (Exception e) {
            throw new ReportsException(e);
        } finally {
            try {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(cs, false);
            } catch (Exception e) {
                con.rollback();
            }
        }
    }

    public static String[] selectDatosDocPoliza(Connection conn, String nFolioDocPoliza) throws SQLException {
        String[] data = new String[9];
        Statement st = conn.createStatement();
        ResultSet rs = null;
        final int COLUMNAS = 9;
        String Query = "select isnull(nFolioPoliza,'-1'),SUM(case when cEvento='CARGO' then mImporte end)Cargos,SUM(case when cEvento='ABONO' then mImporte end) Abonos" + " ,MAX(nDocRenglon)ndocrenglon,max(nNumeroEvento)numEvento,nIdGrupoEvento,nIdSubGrupoEvento,cIdEventoManual,ID_OPER from tDocPolizaEncabezado te with(nolock)" + " inner join tDocPolizaDetalle td with(nolock)" + " on td.nFolioDocPoliza=te.nFolioDocPoliza" + " and  te.nFolioDocPoliza=" + nFolioDocPoliza + " INNER JOIN CG_CASO_OPERACION CO ON co.ID_CASO=te.nIdCasoOrigen" + " group by te.nFolioPoliza,nIdGrupoEvento,nIdSubGrupoEvento,cIdEventoManual,ID_OPER";
        System.out.println(">>>>>" + Query);
        try {
            conn.setAutoCommit(false);
            st.executeQuery(Query);
            rs = st.getResultSet();
            while (rs.next()) {
                for (int i = 1; i <= COLUMNAS; i++) data[i - 1] = rs.getString(i);
            }
        } catch (Exception e) {
            throw new ReportsException(e);
        } finally {
            try {
                conn.commit();
                conn.close();
            } catch (Exception e) {
                conn.rollback();
            }
        }
        return data;
    }
}
