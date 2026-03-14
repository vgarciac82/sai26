package com.syc.contable.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;

public class ReporteEjercicioAnteriorManager {

    public ReporteEjercicioAnteriorManager() {
        super();
    }

    public static List<String> obtenerUnidades(List<String> capitulo, String unidad, String fuente, String partida, String ejercicio, Connection conn) throws SQLException {
        ArrayList<String> arrLResult = new ArrayList<String>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            String query = "select DISTINCT cUnidadEjecutora " + " from ( select a1.cUnidadEjecutora , a1.cProgramaPresupuestario , substring(a1.nCuenta,1,5) Cuenta , sum(a1.mSaldoArrastre) sai_2012 , " + " sum(a2.mSaldoArrastre) sai_2013 from ( select cUnidadEjecutora , cProgramaPresupuestario , nCuenta , sum(mSaldoArrastre) mSaldoArrastre " + " from sai.dbo.tSaldos s1 with(NOLOCK) inner join sai.dbo.tCatalogoEP cEP1 with(NOLOCK) on s1.cSubCuenta=cEP1.EP ";
            if (fuente != null && !"".equals(fuente)) {
                query += " and cEP1.cFuenteFinanciamiento ='" + fuente + "'";
            }
            //			if(unidad!=null && !"".equals(unidad))
            //			{
            //				query+=" AND   t1.cunidadejecutora ='"+unidad+"'";
            //			}
            if (capitulo != null && capitulo.size() != 0) {
                String cadena = "";
                for (int i = 0; i < capitulo.size(); i++) {
                    if (i == 0) {
                        //cadena=capitulo.get(i);
                        query += " and ( substring(LTRIM(cEP1.cPartida),1,1)=" + capitulo.get(i);
                    } else if (i == (capitulo.size() - 1)) {
                        //cadena+=", "+capitulo.get(i);
                        query += " or substring(LTRIM(cEP1.cPartida),1,1)=" + capitulo.get(i) + ") ";
                    } else {
                        //cadena+=", "+capitulo.get(i);
                        query += " or substring(LTRIM(cEP1.cPartida),1,1)=" + capitulo.get(i);
                    }
                }
            }
            if (partida != null && !"".equals(partida)) {
                query += " AND  cEP1.cPartida='" + partida + "'";
            }
            //and cEP1.cFuenteFinanciamiento=1 and cEP1.cPartida=11301 and (cEP1.cPartida like'1%' or cEP1.cPartida like'2%')
            query += "group by nCuenta,cUnidadEjecutora,cEP1.cProgramaPresupuestario) " + " a1 inner join ( select cUnidadEjecutora ,cProgramaPresupuestario ,nCuenta ,sum(mSaldoArrastre) mSaldoArrastre " + " from sai_2013.dbo.tSaldos s2 with(NOLOCK) inner join sai_2013.dbo.tCatalogoEP cEP2 with(NOLOCK) on s2.cSubCuenta=cEP2.EP ";
            if (fuente != null && !"".equals(fuente)) {
                query += " and cEP2.cFuenteFinanciamiento ='" + fuente + "'";
            }
            //			if(unidad!=null && !"".equals(unidad))
            //			{
            //				query+=" AND   t1.cunidadejecutora ='"+unidad+"'";
            //			}
            if (capitulo != null && capitulo.size() != 0) {
                String cadena = "";
                for (int i = 0; i < capitulo.size(); i++) {
                    if (i == 0) {
                        //cadena=capitulo.get(i);
                        query += " and ( substring(LTRIM(cEP2.cPartida),1,1)=" + capitulo.get(i);
                    } else if (i == (capitulo.size() - 1)) {
                        //cadena+=", "+capitulo.get(i);
                        query += " or substring(LTRIM(cEP2.cPartida),1,1)=" + capitulo.get(i) + ") ";
                    } else {
                        //cadena+=", "+capitulo.get(i);
                        query += " or substring(LTRIM(cEP2.cPartida),1,1)=" + capitulo.get(i);
                    }
                }
            }
            if (partida != null && !"".equals(partida)) {
                query += " AND  cEP2.cPartida='" + partida + "'";
                System.out.println("partida " + partida);
            }
            query += " group by nCuenta,cUnidadEjecutora,cEP2.cProgramaPresupuestario) " + " a2 on a1.cProgramaPresupuestario =a2.cProgramaPresupuestario and a1.cUnidadEjecutora=a2.cUnidadEjecutora and a1.nCuenta=a2.nCuenta where a1.nCuenta like '81101%' or a1.nCuenta like '81102%' " + " or a1.nCuenta like '82108%' group by a1.cUnidadEjecutora,a1.cProgramaPresupuestario,substring (a1.nCuenta,1,5)) " + " as tbl ";
            if (unidad != null && !"".equals(unidad)) {
                query += "WHERE cUnidadEjecutora ='" + unidad + "' ";
            }
            query += " group by cUnidadEjecutora, cProgramaPresupuestario ";
            pstmnt = conn.prepareStatement(query);
            //pstmnt.setString(1, ejercicio_fiscal);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                arrLResult.add(rs.getString("cUnidadEjecutora"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                //rs.close();
                if (pstmnt != null)
                    //pstmnt.close();
                    rs = null;
            pstmnt = null;
        }
        return arrLResult;
    }

    public static ArrayList<ReporteEjercicioAnterior> validaEPDetalle2013(List<String> capitulo, String unidad, String fuente, String partida, Connection conn, String ejercicio_fiscal) throws SQLException {
        //borrarTablas(conn);
        //		prueba(conn);
        //		prueba2(conn);
        //		prueba3(conn);
        ArrayList<ReporteEjercicioAnterior> arrLResult13 = new ArrayList<ReporteEjercicioAnterior>();
        Statement stmt = null;
        ResultSet rs = null;
        String query = "select cUnidadEjecutora AS Unidad, cProgramaPresupuestario, " + " sum( case when Cuenta = 81101 then sai_2013 else 0 end )as original_2013, " + " sum( case when Cuenta = 81102 then sai_2013 else 0 end )as modificado_2013, sum( case when Cuenta = 82108 then sai_2013 else 0 end )as ejercido_2013 " + " from ( select a1.cUnidadEjecutora , a1.cProgramaPresupuestario , substring(a1.nCuenta,1,5) Cuenta , sum(a1.mSaldoArrastre) sai_2012 , " + " sum(a2.mSaldoArrastre) sai_2013 from ( select cUnidadEjecutora , cProgramaPresupuestario , nCuenta , sum(mSaldoArrastre) mSaldoArrastre " + " from sai.dbo.tSaldos s1 with(NOLOCK) inner join sai.dbo.tCatalogoEP cEP1 with(NOLOCK) on s1.cSubCuenta=cEP1.EP ";
        if (fuente != null && !"".equals(fuente)) {
            query += " and cEP1.cFuenteFinanciamiento ='" + fuente + "'";
        }
        //			if(unidad!=null && !"".equals(unidad))
        //			{
        //				query+=" AND   t1.cunidadejecutora ='"+unidad+"'";
        //			}
        if (capitulo != null && capitulo.size() != 0) {
            String cadena = "";
            for (int i = 0; i < capitulo.size(); i++) {
                if (i == 0) {
                    //cadena=capitulo.get(i);
                    query += " and ( substring(LTRIM(cEP1.cPartida),1,1)=" + capitulo.get(i);
                } else if (i == (capitulo.size() - 1)) {
                    //cadena+=", "+capitulo.get(i);
                    query += " or substring(LTRIM(cEP1.cPartida),1,1)=" + capitulo.get(i) + ") ";
                } else {
                    //cadena+=", "+capitulo.get(i);
                    query += " or substring(LTRIM(cEP1.cPartida),1,1)=" + capitulo.get(i);
                }
            }
        }
        if (partida != null && !"".equals(partida)) {
            query += " AND  cEP1.cPartida='" + partida + "'";
            System.out.println("partida " + partida);
        }
        //and cEP1.cFuenteFinanciamiento=1 and cEP1.cPartida=11301 and (cEP1.cPartida like'1%' or cEP1.cPartida like'2%')
        query += " group by nCuenta,cUnidadEjecutora,cEP1.cProgramaPresupuestario) " + " a1 inner join ( select cUnidadEjecutora ,cProgramaPresupuestario ,nCuenta ,sum(mSaldoArrastre) mSaldoArrastre " + " from sai_2013.dbo.tSaldos s2 with(NOLOCK) inner join sai_2013.dbo.tCatalogoEP cEP2 with(NOLOCK) on s2.cSubCuenta=cEP2.EP ";
        if (fuente != null && !"".equals(fuente)) {
            query += " and cEP2.cFuenteFinanciamiento ='" + fuente + "'";
        }
        //			if(unidad!=null && !"".equals(unidad))
        //			{
        //				query+=" AND   t1.cunidadejecutora ='"+unidad+"'";
        //			}
        if (capitulo != null && capitulo.size() != 0) {
            String cadena = "";
            for (int i = 0; i < capitulo.size(); i++) {
                if (i == 0) {
                    //cadena=capitulo.get(i);
                    query += " and ( substring(LTRIM(cEP2.cPartida),1,1)=" + capitulo.get(i);
                } else if (i == (capitulo.size() - 1)) {
                    //cadena+=", "+capitulo.get(i);
                    query += " or substring(LTRIM(cEP2.cPartida),1,1)=" + capitulo.get(i) + ") ";
                } else {
                    //cadena+=", "+capitulo.get(i);
                    query += " or substring(LTRIM(cEP2.cPartida),1,1)=" + capitulo.get(i);
                }
            }
        }
        if (partida != null && !"".equals(partida)) {
            query += " AND  cEP2.cPartida='" + partida + "'";
            System.out.println("partida " + partida);
        }
        query += " group by nCuenta,cUnidadEjecutora,cEP2.cProgramaPresupuestario) " + " a2 on a1.cProgramaPresupuestario =a2.cProgramaPresupuestario and a1.cUnidadEjecutora=a2.cUnidadEjecutora and a1.nCuenta=a2.nCuenta where a1.nCuenta like '81101%' or a1.nCuenta like '81102%' " + " or a1.nCuenta like '82108%' group by a1.cUnidadEjecutora,a1.cProgramaPresupuestario,substring (a1.nCuenta,1,5)) " + " as tbl ";
        if (unidad != null && !"".equals(unidad)) {
            query += "WHERE cUnidadEjecutora ='" + unidad + "' ";
        }
        query += " group by cUnidadEjecutora, cProgramaPresupuestario ";
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                ReporteEjercicioAnterior reporteEjercicioAnterior = new ReporteEjercicioAnterior();
                reporteEjercicioAnterior.setCprograma_presupuestario(rs.getString("cProgramaPresupuestario"));
                reporteEjercicioAnterior.setsaldoModificado(rs.getString("modificado_2013"));
                reporteEjercicioAnterior.setSaldoOriginal(rs.getString("original_2013"));
                reporteEjercicioAnterior.setsaldoEjercido(rs.getString("ejercido_2013"));
                //reporteEjercicioAnterior.setcFuenteFinanciamiento(rs.getString("FuenteFinanciamiento"));
                reporteEjercicioAnterior.setEjercicio("2013");
                reporteEjercicioAnterior.setUnidadEjecutora(rs.getString("Unidad"));
                arrLResult13.add(reporteEjercicioAnterior);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                rs.close();
            rs = null;
            stmt = null;
        }
        return arrLResult13;
    }

    public static ArrayList<ReporteEjercicioAnterior> validaEPDetalle(List<String> capitulo, String unidad, String fuente, String partida, Connection conn, String ejercicio_fiscal) throws SQLException {
        ArrayList<ReporteEjercicioAnterior> arrLResult12 = new ArrayList<ReporteEjercicioAnterior>();
        Statement stmt = null;
        ResultSet rs = null;
        String query = "select cUnidadEjecutora AS Unidad, cProgramaPresupuestario, " + " sum( case when Cuenta = 81101 then sai_2012 else 0 end )as original_2012, " + " sum( case when Cuenta = 81102 then sai_2012 else 0 end )as modificado_2012, sum( case when Cuenta = 82108 then sai_2012 else 0 end )as ejercido_2012 " + " from ( select a1.cUnidadEjecutora , a1.cProgramaPresupuestario , substring(a1.nCuenta,1,5) Cuenta , sum(a1.mSaldoArrastre) sai_2012 , " + " sum(a2.mSaldoArrastre) sai_2013 from ( select cUnidadEjecutora , cProgramaPresupuestario , nCuenta , sum(mSaldoArrastre) mSaldoArrastre " + " from sai.dbo.tSaldos s1 with(NOLOCK) inner join sai.dbo.tCatalogoEP cEP1 with(NOLOCK) on s1.cSubCuenta=cEP1.EP ";
        if (fuente != null && !"".equals(fuente)) {
            query += " and cEP1.cFuenteFinanciamiento ='" + fuente + "'";
        }
        //			if(unidad!=null && !"".equals(unidad))
        //			{
        //				query+=" AND   t1.cunidadejecutora ='"+unidad+"'";
        //			}
        if (capitulo != null && capitulo.size() != 0) {
            String cadena = "";
            for (int i = 0; i < capitulo.size(); i++) {
                if (i == 0) {
                    //cadena=capitulo.get(i);
                    query += " and ( substring(LTRIM(cEP1.cPartida),1,1)=" + capitulo.get(i);
                } else if (i == (capitulo.size() - 1)) {
                    //cadena+=", "+capitulo.get(i);
                    query += " or substring(LTRIM(cEP1.cPartida),1,1)=" + capitulo.get(i) + ") ";
                } else {
                    //cadena+=", "+capitulo.get(i);
                    query += " or substring(LTRIM(cEP1.cPartida),1,1)=" + capitulo.get(i);
                }
            }
        }
        if (partida != null && !"".equals(partida)) {
            query += " AND  cEP1.cPartida='" + partida + "'";
        }
        //and cEP1.cFuenteFinanciamiento=1 and cEP1.cPartida=11301 and (cEP1.cPartida like'1%' or cEP1.cPartida like'2%')
        query += " group by nCuenta,cUnidadEjecutora,cEP1.cProgramaPresupuestario) " + " a1 inner join ( select cUnidadEjecutora ,cProgramaPresupuestario ,nCuenta ,sum(mSaldoArrastre) mSaldoArrastre " + " from sai_2013.dbo.tSaldos s2 with(NOLOCK) inner join sai_2013.dbo.tCatalogoEP cEP2 with(NOLOCK) on s2.cSubCuenta=cEP2.EP ";
        if (fuente != null && !"".equals(fuente)) {
            query += " and cEP2.cFuenteFinanciamiento ='" + fuente + "'";
        }
        //			if(unidad!=null && !"".equals(unidad))
        //			{
        //				query+=" AND   t1.cunidadejecutora ='"+unidad+"'";
        //			}
        if (capitulo != null && capitulo.size() != 0) {
            String cadena = "";
            for (int i = 0; i < capitulo.size(); i++) {
                if (i == 0) {
                    //cadena=capitulo.get(i);
                    query += " and ( substring(LTRIM(cEP2.cPartida),1,1)=" + capitulo.get(i);
                } else if (i == (capitulo.size() - 1)) {
                    //cadena+=", "+capitulo.get(i);
                    query += " or substring(LTRIM(cEP2.cPartida),1,1)=" + capitulo.get(i) + ") ";
                } else {
                    //cadena+=", "+capitulo.get(i);
                    query += " or substring(LTRIM(cEP2.cPartida),1,1)=" + capitulo.get(i);
                }
            }
        }
        if (partida != null && !"".equals(partida)) {
            query += " AND  cEP2.cPartida='" + partida + "'";
            System.out.println("partida " + partida);
        }
        query += " group by nCuenta,cUnidadEjecutora,cEP2.cProgramaPresupuestario) " + " a2 on a1.cProgramaPresupuestario =a2.cProgramaPresupuestario and a1.cUnidadEjecutora=a2.cUnidadEjecutora and a1.nCuenta=a2.nCuenta where a1.nCuenta like '81101%' or a1.nCuenta like '81102%' " + " or a1.nCuenta like '82108%' group by a1.cUnidadEjecutora,a1.cProgramaPresupuestario,substring (a1.nCuenta,1,5)) " + " as tbl ";
        if (unidad != null && !"".equals(unidad)) {
            query += "WHERE cUnidadEjecutora ='" + unidad + "' ";
        }
        query += " group by cUnidadEjecutora, cProgramaPresupuestario ";
        System.out.println("q " + query);
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                ReporteEjercicioAnterior reporteEjercicioAnterior = new ReporteEjercicioAnterior();
                reporteEjercicioAnterior.setCprograma_presupuestario(rs.getString("cProgramaPresupuestario"));
                reporteEjercicioAnterior.setsaldoModificado(rs.getString("modificado_2012"));
                reporteEjercicioAnterior.setSaldoOriginal(rs.getString("original_2012"));
                reporteEjercicioAnterior.setsaldoEjercido(rs.getString("ejercido_2012"));
                //reporteEjercicioAnterior.setcFuenteFinanciamiento(rs.getString("FuenteFinanciamiento"));
                reporteEjercicioAnterior.setEjercicio("2012");
                reporteEjercicioAnterior.setUnidadEjecutora(rs.getString("Unidad"));
                arrLResult12.add(reporteEjercicioAnterior);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                rs.close();
            rs = null;
            stmt = null;
        }
        return arrLResult12;
    }

    public static boolean borrarTablas(Connection conn) {
        boolean revisa = false;
        try {
            String queryT1 = "drop table t1";
            conn.prepareStatement(queryT1);
            String queryT2 = "drop table t2";
            conn.prepareStatement(queryT2);
            String queryT3 = "drop table t3";
            conn.prepareStatement(queryT3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return revisa;
    }

    public static ArrayList<ReporteEjercicioAnterior> prueba(Connection conn) throws SQLException {
        ArrayList<ReporteEjercicioAnterior> arrLResult = new ArrayList<ReporteEjercicioAnterior>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String query = "SELECT  cDescripcion_Cuenta, cunidadejecutora, cprograma_presupuestario, cFuente_Financiamiento, cPartida,  SUM(mMonto_Anual) AS saldoComprometido " + " INTO t1 FROM	tsaldo_congelado  WHERE (cdescripcion_cuenta = 'COMPROMETIDO'   ) group by cDescripcion_Cuenta, cunidadejecutora,cprograma_presupuestario,cFuente_Financiamiento, cPartida  order by cDescripcion_Cuenta, cunidadejecutora,cprograma_presupuestario,cFuente_Financiamiento";
        try {
            pstmnt = conn.prepareStatement(query);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                //	rs.close();
                if (pstmnt != null)
                    //	pstmnt.close();
                    rs = null;
            pstmnt = null;
        }
        return arrLResult;
    }

    public static ArrayList<ReporteEjercicioAnterior> prueba2(Connection conn) throws SQLException {
        ArrayList<ReporteEjercicioAnterior> arrLResult = new ArrayList<ReporteEjercicioAnterior>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String query2 = "SELECT  cDescripcion_Cuenta, cunidadejecutora, cprograma_presupuestario, cFuente_Financiamiento,  SUM(mMonto_Anual) AS saldoDevengado INTO t2 " + "FROM	tsaldo_congelado  WHERE (cDescripcion_Cuenta = 'DEVENGADO') group by cDescripcion_Cuenta, cunidadejecutora,cprograma_presupuestario,cFuente_Financiamiento  order by cDescripcion_Cuenta, cunidadejecutora,cprograma_presupuestario,cFuente_Financiamiento";
        try {
            pstmnt = conn.prepareStatement(query2);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                //	rs.close();
                if (pstmnt != null)
                    //	pstmnt.close();
                    rs = null;
            pstmnt = null;
        }
        return arrLResult;
    }

    public static ArrayList<ReporteEjercicioAnterior> prueba3(Connection conn) throws SQLException {
        ArrayList<ReporteEjercicioAnterior> arrLResult = new ArrayList<ReporteEjercicioAnterior>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String query3 = "SELECT  cDescripcion_Cuenta, cunidadejecutora, cprograma_presupuestario, cFuente_Financiamiento,  SUM(mMonto_Anual) AS saldoOriginal INTO t3 " + " FROM	tsaldo_congelado  WHERE (cDescripcion_Cuenta = 'ORIGINAL') group by cDescripcion_Cuenta, cunidadejecutora,cprograma_presupuestario,cFuente_Financiamiento  order by cDescripcion_Cuenta, cunidadejecutora,cprograma_presupuestario,cFuente_Financiamiento";
        try {
            pstmnt = conn.prepareStatement(query3);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                // rs.close();
                if (pstmnt != null)
                    // pstmnt.close();
                    rs = null;
            pstmnt = null;
        }
        return arrLResult;
    }
}
