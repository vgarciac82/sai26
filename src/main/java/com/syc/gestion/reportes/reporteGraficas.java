package com.syc.gestion.reportes;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.syc.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class reporteGraficas extends DataSourceManager {

    public String NombreReporte = "";

    private static Logger log = LoggerFactory.getLogger(reporteGraficas.class);

    public reporteGraficas(String jniName) {
        super.init(jniName);
    }

    public String graficaPresupuesto(int tipo) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        CallableStatement cs1 = null;
        String cadena = "";
        conn = getConnection();
        try {
            if (tipo == 1) {
                //programa anual general
                pstmnt = conn.prepareStatement("SELECT dbo.fn_mGraficas (?) as cadena");
                pstmnt.setInt(1, tipo);
            } else {
                if (tipo == 3) {
                    //tabla programa anual
                    pstmnt = conn.prepareStatement("SELECT * from fn_mGraficaProgAnualTabla(1)");
                    //	rs=pstmnt.executeQuery();
                } else {
                    if (tipo == 4) {
                        //requsicion general
                        pstmnt = conn.prepareStatement("SELECT dbo.fn_mGraficas (?) as cadena");
                        pstmnt.setInt(1, tipo);
                        //	rs=pstmnt.executeQuery();
                    } else {
                        if (tipo == 5) {
                            //requsicion tabla
                            pstmnt = conn.prepareStatement("SELECT * from fn_mGraficaRequisicionesTabla()");
                            //	rs=pstmnt.executeQuery();
                        } else {
                            if (tipo == 6) {
                                //consolidado por capitulo
                                pstmnt = conn.prepareStatement("SELECT dbo.fn_mGraficas (?) as cadena");
                                pstmnt.setInt(1, tipo);
                                //	rs=pstmnt.executeQuery();
                            } else {
                                if (tipo == 7) {
                                    //consolidado alcane
                                    pstmnt = conn.prepareStatement("SELECT dbo.fn_mGraficas (?) as cadena");
                                    pstmnt.setInt(1, tipo);
                                    //	rs=pstmnt.executeQuery();
                                } else {
                                    if (tipo == 8) {
                                        //tabla consolidado
                                        pstmnt = conn.prepareStatement("SELECT * from fn_mGraficaConsolidadoTabla()");
                                        //		rs=pstmnt.executeQuery();
                                    } else {
                                        if (tipo == 9) {
                                            //procedimientoGeneral
                                            pstmnt = conn.prepareStatement("SELECT dbo.fn_mGraficas (?) as cadena");
                                            pstmnt.setInt(1, tipo);
                                            //	rs=pstmnt.executeQuery();
                                        } else {
                                            if (tipo == 10) {
                                                //tabla procedimiento
                                                pstmnt = conn.prepareStatement("SELECT * from fn_mGraficaProcedimientoTabla()");
                                                //		rs=pstmnt.executeQuery();
                                            } else {
                                                if (tipo == 11) {
                                                    //programa anual por capitulo
                                                    pstmnt = conn.prepareStatement("SELECT dbo.fn_mGraficas (?) as cadena");
                                                    pstmnt.setInt(1, tipo);
                                                } else {
                                                    if (tipo == 12) {
                                                        //pedidos -- rfc mas adjudicados
                                                        pstmnt = conn.prepareStatement("SELECT dbo.fn_mGraficas (?) as cadena");
                                                        pstmnt.setInt(1, tipo);
                                                        //		rs=pstmnt.executeQuery();
                                                    } else {
                                                        if (tipo == 13) {
                                                            //tabla de pedido
                                                            pstmnt = conn.prepareStatement("SELECT * from fn_mGraficaPedidoTabla()");
                                                        } else {
                                                            if (tipo == 14) {
                                                                //pedido por capitulo
                                                                pstmnt = conn.prepareStatement("SELECT dbo.fn_mGraficas (?) as cadena");
                                                                pstmnt.setInt(1, tipo);
                                                            } else {
                                                                if (tipo == 15) {
                                                                    //pedido por capitulo
                                                                    pstmnt = conn.prepareStatement("SELECT dbo.fn_mGraficas (?) as cadena");
                                                                    pstmnt.setInt(1, tipo);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                cadena += rs.getString("cadena") + "\t";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            if (cs1 != null)
                cs1.close();
            if (conn != null)
                conn.close();
            cs1 = null;
            rs = null;
            pstmnt = null;
            conn = null;
        }
        return cadena;
    }

    public String graficaProgramado() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        CallableStatement cs1 = null;
        String cadena = "";
        conn = getConnection();
        try {
            pstmnt = conn.prepareStatement("SELECT dbo.fn_mGraficas (2) as cadena");
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                cadena += rs.getString("cadena");
                //	cadena+=",";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            if (cs1 != null)
                cs1.close();
            if (conn != null)
                conn.close();
            cs1 = null;
            rs = null;
            pstmnt = null;
            conn = null;
        }
        return cadena;
    }
}
