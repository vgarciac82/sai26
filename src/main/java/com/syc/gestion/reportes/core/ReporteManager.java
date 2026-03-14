package com.syc.gestion.reportes.core;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import com.syc.contable.core.AnteProyectoAut;
import com.syc.contable.core.AnteProyectoAutCalendario;
import com.syc.contable.core.Auxiliares;
import com.syc.contable.core.Balanza;
import com.syc.contable.core.PAOP;
import com.syc.contable.core.Poliza;
import com.syc.contable.core.ReporteAdecuacionesPorUN;
import com.syc.contable.core.ReportePrespuestal;
import com.syc.contable.core.Saldo;
import com.syc.gestion.reports.Reporte;
import com.syc.gestion.reports.ReporteParametro;
import com.syc.gestion.reports.ReporteParametroManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class ReporteManager {

    public static int delete(Connection conn, int id_reporte) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("DELETE FROM cg_reporte WHERE id_reporte = ?");
            pstmnt.setInt(1, id_reporte);
            //aqui se deberia borrar los parametros del reporte
            //GrupoPropiedadesManager.delete(conn, g_nombre);
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static List select(Connection conn, String query, String nameColLogin, String nameColDesc) throws SQLException {
        List acumuladoLst = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement(query);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                Acumulado a = new Acumulado();
                a.setId((nameColLogin != "" ? rs.getString(nameColLogin) : ""));
                //a.setId("Hello");
                a.setDescripcion(rs.getString(nameColDesc));
                int iVencidos = rs.getInt("pendientesVencidos");
                int iNoVencidos = rs.getInt("pendientesNoVencidos");
                int iCerrados = rs.getInt("cerrado");
                int iPendientes = iVencidos + iNoVencidos;
                int iCasos = iPendientes + iCerrados;
                a.setTotalTurnos(iCasos);
                a.setVencidosNumero(iVencidos);
                a.setNovencidosNumero(iNoVencidos);
                a.setConcluidos(iCerrados);
                a.setPendientes(iPendientes);
                if (iCasos == 0)
                    iCasos = 1;
                a.setVencidosPorcentaje(iVencidos / iCasos);
                a.setNovencidosPorcentaje(iNoVencidos / iCasos);
                a.setConcluidosPorcentaje(iCerrados / iCasos);
                acumuladoLst.add(a);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return acumuladoLst;
    }

    public static List selectHomoViati(Connection conn, String sql) throws SQLException {
        List hvList = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement(sql);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                HomoViati hv = new HomoViati();
                hv.setArea(rs.getString("area"));
                hv.setFecha(rs.getString("fecha"));
                hv.setNo_oficio(rs.getString("no_oficio"));
                hvList.add(hv);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return hvList;
    }

    public static List selectReportePresupuesto(Connection conn, String sql) throws SQLException {
        List saldoList = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement(sql);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                Saldo epSaldo = new Saldo();
                epSaldo.setMontoAbril(rs.getString("MontoAbril"));
                epSaldo.setMontoAgosto(rs.getString("MontoAgosto"));
                epSaldo.setMontoAnual(rs.getString("MontoAnual"));
                epSaldo.setMontoDiciembre(rs.getString("MontoDiciembre"));
                epSaldo.setMontoEnero(rs.getString("MontoEnero"));
                epSaldo.setMontoFebrero(rs.getString("MontoFebrero"));
                epSaldo.setMontoJulio(rs.getString("MontoJulio"));
                epSaldo.setMontoJunio(rs.getString("MontoJunio"));
                epSaldo.setMontoMarzo(rs.getString("MontoMarzo"));
                epSaldo.setMontoMayo(rs.getString("MontoMayo"));
                epSaldo.setMontoNoviembre(rs.getString("MontoNoviembre"));
                epSaldo.setMontoOctubre(rs.getString("MontoOctubre"));
                epSaldo.setMontoSeptiembre(rs.getString("MontoSeptiembre"));
                epSaldo.setClaveSIAFF(rs.getString("ClaveSIAFF"));
                epSaldo.setClaveInterna(rs.getString("ClaveInterna"));
                epSaldo.setClaveCNA(rs.getString("nClaveCNA"));
                saldoList.add(epSaldo);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return saldoList;
    }

    public static List selectConsultaPolizas(Connection conn, String sql) throws SQLException {
        List polizaList = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement(sql);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                Poliza cPoliza = new Poliza();
                cPoliza.setAejercicioFiscal(rs.getString("aEjercicioFiscal"));
                cPoliza.setCcentroContable(rs.getString("cCentroContable"));
                cPoliza.setCdescripcionCentroContable(rs.getString("cDescripcionCentroContable"));
                cPoliza.setCdescripcionPoliza(rs.getString("cDescripcionPoliza"));
                cPoliza.setCtipoDocumento(rs.getString("cTipoDocumento"));
                cPoliza.setCtipoPoliza(rs.getString("cTipoPoliza"));
                cPoliza.setCdescripcionTipoPoliza(rs.getString("cDescripcionTipoPoliza"));
                cPoliza.setFaplicacion(rs.getString("fAplicacion"));
                cPoliza.setFcreacion(rs.getString("fCreacion"));
                cPoliza.setMtotalAbono(rs.getString("mTotalAbono"));
                cPoliza.setMtotalCargo(rs.getString("mTotalCargo"));
                cPoliza.setNcuenta(rs.getString("nCuenta"));
                cPoliza.setNfolioDocumento(rs.getString("nFolioDocumento"));
                cPoliza.setNfolioPoliza(rs.getString("nFolioPoliza"));
                cPoliza.setNmes(rs.getString("nMes"));
                cPoliza.setNpolizaAutomatica(rs.getString("nPolizaAutomatica"));
                cPoliza.setDocHAplicado(rs.getString("cDocHAplicado"));
                cPoliza.setCusuarioAutorizo(rs.getString("cUsuarioAutorizo"));
                cPoliza.setIdOper(rs.getString("ID_OPER"));
                polizaList.add(cPoliza);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return polizaList;
    }

    public static List callMultiReportePresupuestal(Connection conn, String EPs, String ProgramaPresupuestario, String Partida, String UnidadResponsableEP, String UnidadEjecutora, String Cartera, String ChkReintegros, String ChkRectificaciones, String ChkAdecuaciones, String ChkPagosAnticipados, String ChkPlurianuales, String ChkOriginal, String ChkModificado, String ChkAmpAutorizada, String ChkRedAutorizada, String ChkAmpenTramite, String ChkRedenTramite, String ChkReienTramite, String ChkRectificacion, String ChkRedSHCPenTramite, String ChkRedSHCPAplicada, String ChkApartado, String ChkPrecomprometido, String ChkComprometido, String ChkDevengado, String ChkEjernoPagado, String ChkDisponibleNeto, String ChkDisponibleBruto, String ChkEjercidoPagado, String sULogin) throws SQLException {
        ResultSet rs = null;
        CallableStatement cs1 = null;
        double anual = 0;
        List ReportePrespuestalList = new ArrayList();
        try {
            cs1 = conn.prepareCall("{call dbo.sp_MultiReportePresupuestal(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
            cs1.setString(1, EPs);
            cs1.setString(2, ProgramaPresupuestario);
            cs1.setString(3, Partida);
            cs1.setString(4, UnidadResponsableEP);
            cs1.setString(5, UnidadEjecutora);
            cs1.setString(6, Cartera);
            cs1.setString(7, ChkReintegros);
            cs1.setString(8, ChkRectificaciones);
            cs1.setString(9, ChkAdecuaciones);
            cs1.setString(10, ChkPagosAnticipados);
            cs1.setString(11, ChkPlurianuales);
            cs1.setString(12, ChkOriginal);
            cs1.setString(13, ChkModificado);
            cs1.setString(14, ChkAmpAutorizada);
            cs1.setString(15, ChkRedAutorizada);
            cs1.setString(16, ChkAmpenTramite);
            cs1.setString(17, ChkRedenTramite);
            cs1.setString(18, ChkReienTramite);
            cs1.setString(19, ChkRectificacion);
            cs1.setString(20, ChkRedSHCPenTramite);
            cs1.setString(21, ChkRedSHCPAplicada);
            cs1.setString(22, ChkApartado);
            cs1.setString(23, ChkPrecomprometido);
            cs1.setString(24, ChkComprometido);
            cs1.setString(25, ChkDevengado);
            cs1.setString(26, ChkEjernoPagado);
            cs1.setString(27, ChkDisponibleNeto);
            cs1.setString(28, ChkDisponibleBruto);
            cs1.setString(29, ChkEjercidoPagado);
            cs1.setString(30, sULogin.trim());
            cs1.execute();
            PreparedStatement pstmnt = conn.prepareStatement("select * from tMultiReportePresupuestos where U_LOGIN='" + sULogin.trim() + "'");
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                ReportePrespuestal reportePrespuestal = new ReportePrespuestal();
                anual = Double.parseDouble(rs.getString("Enero")) + Double.parseDouble(rs.getString("Febrero")) + Double.parseDouble(rs.getString("Marzo")) + Double.parseDouble(rs.getString("Abril")) + Double.parseDouble(rs.getString("Mayo")) + Double.parseDouble(rs.getString("Junio")) + Double.parseDouble(rs.getString("Julio")) + Double.parseDouble(rs.getString("Agosto")) + Double.parseDouble(rs.getString("Septiembre")) + Double.parseDouble(rs.getString("Octubre")) + Double.parseDouble(rs.getString("Noviembre")) + Double.parseDouble(rs.getString("Diciembre"));
                reportePrespuestal.setcCuenta(rs.getString("Cuenta"));
                reportePrespuestal.setfFechaMov(rs.getString("FechaMov"));
                reportePrespuestal.setcTipoDoc(rs.getString("TipoDoc"));
                reportePrespuestal.setCfolio(rs.getString("Folio"));
                reportePrespuestal.setcEP(rs.getString("Ep"));
                //reportePrespuestal.setcCancelado(rs.getString("Cancelado"));
                reportePrespuestal.setcAnual(anual);
                reportePrespuestal.setcEnero(Double.parseDouble(rs.getString("Enero")));
                reportePrespuestal.setcFebrero(Double.parseDouble(rs.getString("Febrero")));
                reportePrespuestal.setcMarzo(Double.parseDouble(rs.getString("Marzo")));
                reportePrespuestal.setcAbril(Double.parseDouble(rs.getString("Abril")));
                reportePrespuestal.setcMayo(Double.parseDouble(rs.getString("Mayo")));
                reportePrespuestal.setcJunio(Double.parseDouble(rs.getString("Junio")));
                reportePrespuestal.setcJulio(Double.parseDouble(rs.getString("Julio")));
                reportePrespuestal.setcAgosto(Double.parseDouble(rs.getString("Agosto")));
                reportePrespuestal.setcSeptiembre(Double.parseDouble(rs.getString("Septiembre")));
                reportePrespuestal.setcOctubre(Double.parseDouble(rs.getString("Octubre")));
                reportePrespuestal.setcNoviembre(Double.parseDouble(rs.getString("Noviembre")));
                reportePrespuestal.setcDiciembre(Double.parseDouble(rs.getString("Diciembre")));
                ReportePrespuestalList.add(reportePrespuestal);
            }
            /**
             * *********************************Borramos la tabla temporal solo por el query del usuario consultante ********************************************************
             */
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM tMultiReportePresupuestos " + "WHERE u_login = '" + sULogin.trim() + "'";
            stmt.executeUpdate(sql);
            System.out.println("Ejecucion de borrado temporal completo");
            /**
             * ******************************************************************************************
             */
        } catch (SQLException sqlerr) {
            sqlerr.printStackTrace();
        } finally {
            if (rs != null)
                rs.close();
            if (cs1 != null)
                cs1.close();
            rs = null;
            cs1 = null;
        }
        return ReportePrespuestalList;
    }

    public static List ReporteAdecuacionesPorUN(Connection conn, String Ep, String UnidadResponsableEP, String Folio, String Estatus, String FechaIni, String FechaFin, String Monto, String FolioSICOP, String FolioMAP, String Login) throws SQLException {
        ResultSet rs = null;
        CallableStatement cs1 = null;
        List reporteAdecuacionesPorUNList = new ArrayList();
        try {
            cs1 = conn.prepareCall("{call dbo.sp_ReporteAdecuacionesPorUN(?,?,?,?,?,?,?,?,?,?)}");
            cs1.setString(1, Ep);
            cs1.setString(2, UnidadResponsableEP);
            cs1.setString(3, Folio);
            cs1.setString(4, Estatus);
            cs1.setString(5, FechaIni);
            cs1.setString(6, FechaFin);
            cs1.setString(7, Monto);
            cs1.setString(8, FolioSICOP);
            cs1.setString(9, FolioMAP);
            cs1.setString(10, Login);
            rs = cs1.executeQuery();
            while (rs.next()) {
                ReporteAdecuacionesPorUN reporteAdecuacionesPorUN = new ReporteAdecuacionesPorUN();
                reporteAdecuacionesPorUN.setcUnidad(rs.getString("Unidad"));
                reporteAdecuacionesPorUN.setCfolio(rs.getString("Folio"));
                reporteAdecuacionesPorUN.setEp(rs.getString("Ep"));
                reporteAdecuacionesPorUN.setcEstatus(rs.getString("Estatus"));
                reporteAdecuacionesPorUN.setfFecha(rs.getString("Fecha"));
                reporteAdecuacionesPorUN.setcMonto(rs.getString("Monto"));
                reporteAdecuacionesPorUN.setcFolioSICOP(rs.getString("FolioSICOP"));
                reporteAdecuacionesPorUN.setcFolioMAP(rs.getString("FolioMAP"));
                reporteAdecuacionesPorUN.setcUsuario(rs.getString("Usuario"));
                reporteAdecuacionesPorUNList.add(reporteAdecuacionesPorUN);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (cs1 != null)
                cs1.close();
            rs = null;
            cs1 = null;
        }
        return reporteAdecuacionesPorUNList;
    }

    public static List<Saldo> callMultiReporte(Connection conn, String sql, String Cuenta, String cOrddeBy, String cGroupBy, String InfoRegMes, String TipoReporte, String Usuario, String[] nCtasPresup, String[] dCtasPresup, String tipoEP) throws SQLException {
        List<Saldo> saldoList = new ArrayList<>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        CallableStatement cs1 = null;
        String Salida = "";
        String opcion = "";
        try {
            //			pstmnt = conn.prepareStatement(sql);
            //			rs = pstmnt.executeQuery();
            if ("EPTRABAJO".equals(tipoEP))
                cs1 = conn.prepareCall("{call sp_pMultiReporte_syc(?,?,?,?,?,?,?,?)}");
            else if ("EPORIGEN".equals(tipoEP))
                cs1 = conn.prepareCall("{call sp_pMultiReporte_claveORG(?,?,?,?,?,?,?,?)}");
            cs1.setString(1, sql);
            cs1.setString(2, Cuenta);
            cs1.setString(3, cOrddeBy);
            cs1.setString(4, cGroupBy);
            cs1.setString(5, InfoRegMes);
            cs1.setString(6, TipoReporte);
            cs1.setString(7, Usuario);
            cs1.registerOutParameter(8, Types.VARCHAR);
            rs = cs1.executeQuery();
            while (rs.next()) {
                Saldo epSaldo = new Saldo();
                if (cGroupBy.equals("aEjercicioFiscal_1") || cGroupBy.equals("cSubCuenta")) {
                    epSaldo.setEjercicioFiscal(rs.getString("aEjercicioFiscal_1"));
                }
                if (cGroupBy.equals("cRamo_2") || cGroupBy.equals("cSubCuenta")) {
                    epSaldo.setRamoEP(rs.getString("cRamo_2"));
                }
                if (cGroupBy.equals("cUnidadResponsable_3") || cGroupBy.equals("cSubCuenta")) {
                    epSaldo.setUnidadResponsableEP(rs.getString("cUnidadResponsable_3"));
                }
                if (cGroupBy.equals("cGrupoFuncional_4") || cGroupBy.equals("cSubCuenta")) {
                    epSaldo.setGrupoFuncional(rs.getString("cGrupoFuncional_4"));
                }
                if (cGroupBy.equals("cFuncion_5") || cGroupBy.equals("cSubCuenta")) {
                    epSaldo.setFuncion(rs.getString("cFuncion_5"));
                }
                if (cGroupBy.equals("cSubFuncion_6") || cGroupBy.equals("cSubCuenta")) {
                    epSaldo.setSubFuncion(rs.getString("cSubFuncion_6"));
                }
                if (cGroupBy.equals("cProgramaGeneral_7") || cGroupBy.equals("cSubCuenta")) {
                    epSaldo.setProgramaGeneral(rs.getString("cProgramaGeneral_7"));
                }
                if (cGroupBy.equals("cActividadInstitucional_8") || cGroupBy.equals("cSubCuenta")) {
                    epSaldo.setActividadInstitucional(rs.getString("cActividadInstitucional_8"));
                }
                if (cGroupBy.equals("cProgramaPresupuestario_9") || cGroupBy.equals("cSubCuenta")) {
                    epSaldo.setProgramaPresupuestario(rs.getString("cProgramaPresupuestario_9"));
                }
                if (cGroupBy.equals("cPartida_10") || cGroupBy.equals("cSubCuenta")) {
                    epSaldo.setPartida(rs.getString("cPartida_10"));
                }
                if (cGroupBy.equals("cTipoGasto_11") || cGroupBy.equals("cSubCuenta")) {
                    epSaldo.setTipoGasto(rs.getString("cTipoGasto_11"));
                }
                if (cGroupBy.equals("cFuenteFinanciamiento_12") || cGroupBy.equals("cSubCuenta")) {
                    epSaldo.setFuenteFinanciamiento(rs.getString("cFuenteFinanciamiento_12"));
                }
                if (cGroupBy.equals("cEntidadFederativa_13") || cGroupBy.equals("cSubCuenta")) {
                    epSaldo.setEntidadFederativa(rs.getString("cEntidadFederativa_13"));
                }
                if (cGroupBy.equals("cCartera_14") || cGroupBy.equals("cSubCuenta")) {
                    epSaldo.setCartera(rs.getString("cCartera_14"));
                }
                if (cGroupBy.equals("cUnidadResponsable_15") || cGroupBy.equals("cSubCuenta")) {
                    epSaldo.setUnidadEjecutora(rs.getString("cUnidadResponsable_15"));
                }
                if (cGroupBy.equals("cUnidadResponsable_16") || cGroupBy.equals("cSubCuenta")) {
                    epSaldo.setUnidadNorativa(rs.getString("cUnidadResponsable_16"));
                }
                if (cGroupBy.equals("cSubCuenta")) {
                    epSaldo.setEp(rs.getString("cSubCuenta"));
                }
                if (cGroupBy.equals("cSubCuenta")) {
                    epSaldo.setClaveCNA(rs.getString("nClaveCNA"));
                }
                for (int contador = 0; contador < nCtasPresup.length; contador++) {
                    if (Cuenta == "" || Cuenta.contains(nCtasPresup[contador])) {
                        opcion = dCtasPresup[contador].trim().replace(" ", "_");
                        if (opcion.equals("ORIGINAL")) {
                            epSaldo.setMontoOriginal(rs.getString(dCtasPresup[contador].replace(" ", "_")));
                        }
                        if (opcion.equals("MODIFICADO")) {
                            epSaldo.setMontoModificado(rs.getString(dCtasPresup[contador].replace(" ", "_")));
                        }
                        if (opcion.equals("AMPLIACION_AUTORIZADA")) {
                            epSaldo.setMontoAmpliacionAutorizada(rs.getString(dCtasPresup[contador].replace(" ", "_")));
                        }
                        if (opcion.equals("REDUCCION_AUTORIZADA")) {
                            epSaldo.setMontoReduccionAutorizada(rs.getString(dCtasPresup[contador].replace(" ", "_")));
                        }
                        if (opcion.equals("AMPLIACION_EN_TRAMITE")) {
                            epSaldo.setMontoAmpliacionTramite(rs.getString(dCtasPresup[contador].replace(" ", "_")));
                        }
                        if (opcion.equals("APARTADO")) {
                            epSaldo.setMontoApartado(rs.getString(dCtasPresup[contador].replace(" ", "_")));
                        }
                        if (opcion.equals("PRECOMPROMETIDO")) {
                            epSaldo.setMontoPrecomprometido(rs.getString(dCtasPresup[contador].replace(" ", "_")));
                        }
                        if (opcion.equals("COMPROMETIDO")) {
                            epSaldo.setMontoComprometido(rs.getString(dCtasPresup[contador].replace(" ", "_")));
                        }
                        if (opcion.equals("DEVENGADO")) {
                            epSaldo.setMontoDevengado(rs.getString(dCtasPresup[contador].replace(" ", "_")));
                        }
                        if (opcion.equals("EJERCIDO_NO_PAGADO")) {
                            epSaldo.setMontoEjercidoNoPagado(rs.getString(dCtasPresup[contador].replace(" ", "_")));
                        }
                        if (opcion.equals("DISPONIBLE_NETO")) {
                            epSaldo.setMontoDisponibleNeto(rs.getString(dCtasPresup[contador].replace(" ", "_")));
                        }
                        if (opcion.equals("DISPONIBLE_NETO_RADICADO")) {
                            epSaldo.setMontoDisponibleNetoRadicado(rs.getString(dCtasPresup[contador].replace(" ", "_")));
                        }
                        if (opcion.equals("DISPONIBLE_BRUTO")) {
                            epSaldo.setMontoDisponibleBruto(rs.getString(dCtasPresup[contador].replace(" ", "_")));
                        }
                        if (opcion.equals("EJERCIDO_PAGADO")) {
                            epSaldo.setMontoEjercidoPagado(rs.getString(dCtasPresup[contador].replace(" ", "_")));
                        }
                        if (opcion.equals("REDUCCION_EN_TRAMITE")) {
                            epSaldo.setMontoReduccionTramite(rs.getString(dCtasPresup[contador].replace(" ", "_")));
                        }
                        if (opcion.equals("REINTEGRO_EN_TRAMITE")) {
                            epSaldo.setMontoReintegroTramite(rs.getString(dCtasPresup[contador].replace(" ", "_")));
                        }
                        if (opcion.equals("RECTIFICACION")) {
                            epSaldo.setMontoRectificacion(rs.getString(dCtasPresup[contador].replace(" ", "_")));
                        }
                        if (opcion.equals("REDUCCION_SHCP_EN_TRAMITE")) {
                            epSaldo.setMontoReduccionSHCPTramite(rs.getString(dCtasPresup[contador].replace(" ", "_")));
                        }
                        if (opcion.equals("REDUCCION_SHCP_APLICADA")) {
                            epSaldo.setMontoReduccionSHCPAplicada(rs.getString(dCtasPresup[contador].replace(" ", "_")));
                        }
                        if (opcion.equals("PRECOMPROMISO_MATERIALES")) {
                            epSaldo.setMontoPrecompromisoMateriales(rs.getString(dCtasPresup[contador].replace(" ", "_")));
                        }
                    }
                    ;
                }
                if (!TipoReporte.equals("GENERAL")) {
                    epSaldo.setDcuenta(rs.getString("dCuenta"));
                    epSaldo.setMontoAbril(rs.getString("ABRIL"));
                    epSaldo.setMontoAgosto(rs.getString("AGOSTO"));
                    epSaldo.setMontoDiciembre(rs.getString("DICIEMBRE"));
                    epSaldo.setMontoEnero(rs.getString("ENERO"));
                    epSaldo.setMontoFebrero(rs.getString("FEBRERO"));
                    epSaldo.setMontoJulio(rs.getString("JULIO"));
                    epSaldo.setMontoJunio(rs.getString("JUNIO"));
                    epSaldo.setMontoMarzo(rs.getString("MARZO"));
                    epSaldo.setMontoMayo(rs.getString("MAYO"));
                    epSaldo.setMontoNoviembre(rs.getString("NOVIEMBRE"));
                    epSaldo.setMontoOctubre(rs.getString("OCTUBRE"));
                    epSaldo.setMontoSeptiembre(rs.getString("SEPTIEMBRE"));
                }
                saldoList.add(epSaldo);
            }
            Salida = cs1.getString(8);
            if (!Salida.contentEquals("OK"))
                throw new SQLException(Salida);
            //System.out.println("Salida: "+Salida);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            if (cs1 != null)
                cs1.close();
            cs1 = null;
            rs = null;
            pstmnt = null;
        }
        return saldoList;
    }

    public static ArrayList<ArrayList<String>> callMultiReporteContable(Connection conn, String sql, String Cuenta, String cOrddeBy, String cGroupBy, String InfoRegMes, String TipoReporte, String Usuario, String[] nCtasPresup, String cCentroContable, String Componentes) throws SQLException {
        List saldoList = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        CallableStatement cs1 = null;
        ResultSetMetaData rsmd = null;
        ArrayList<ArrayList<String>> Datos = new ArrayList<ArrayList<String>>();
        ArrayList<String> fila = null;
        //		String Salida="";
        //		String opcion="";
        try {
            //			pstmnt = conn.prepareStatement(sql);
            //			rs = pstmnt.executeQuery();
            cs1 = conn.prepareCall("{call sp_pMultiReporteContable_syc(?,?,?,?,?,?,?,?,?)}");
            cs1.setString(1, sql);
            cs1.setString(2, Cuenta);
            cs1.setString(3, cOrddeBy);
            cs1.setString(4, cGroupBy);
            cs1.setString(5, InfoRegMes);
            cs1.setString(6, TipoReporte);
            cs1.setString(7, Usuario);
            cs1.setString(8, cCentroContable);
            cs1.registerOutParameter(9, Types.VARCHAR);
            rs = cs1.executeQuery();
            rsmd = rs.getMetaData();
            while (rs.next()) {
                fila = new ArrayList<String>();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if ((TipoReporte.equals("GENERAL") && i >= 3 && i <= 18) || (!TipoReporte.equals("GENERAL") && i >= 5 && i <= 20)) {
                        if (Componentes.compareTo("INCLUIR") == 0)
                            if ((rsmd.getColumnName(i).equals("aEjercicioFiscal_1")) || (rsmd.getColumnName(i).equals("cRamo_2")) || (rsmd.getColumnName(i).equals("cUnidadResponsable_3")) || (rsmd.getColumnName(i).equals("cGrupoFuncional_4")) || (rsmd.getColumnName(i).equals("cFuncion_5")) || (rsmd.getColumnName(i).equals("cSubFuncion_6")) || (rsmd.getColumnName(i).equals("cProgramaGeneral_7")) || (rsmd.getColumnName(i).equals("cActividadInstitucional_8")) || (rsmd.getColumnName(i).equals("cProgramaPresupuestario_9")) || (rsmd.getColumnName(i).equals("cPartida_10")) || (rsmd.getColumnName(i).equals("cTipoGasto_11")) || (rsmd.getColumnName(i).equals("cFuenteFinanciamiento_12")) || (rsmd.getColumnName(i).equals("cEntidadFederativa_13")) || (rsmd.getColumnName(i).equals("cCartera_14")) || (rsmd.getColumnName(i).equals("cUnidadResponsable_15")) || (rsmd.getColumnName(i).equals("cUnidadResponsable_16")))
                                fila.add(rs.getString(i));
                    } else
                        fila.add(rs.getString(i));
                    // System.out.print(rs.getString(i));
                }
                Datos.add(fila);
                //System.out.println();
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
            cs1 = null;
            rs = null;
            pstmnt = null;
        }
        return Datos;
    }

    public static List callPAOP(Connection conn, String aEjercicioFiscal, String mesIni) throws SQLException {
        List PAOPList = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        CallableStatement cs1 = null;
        String Salida = "";
        try {
            // EXECUTE sp_tBalanzaAnalitico_syc @Salida OUTPUT, '21203-00000-00000-00000', '', '2012', 'N', 'N', 'undefined', 'AnaliticoCC', '1', '5', 'APA8002291D9';
            cs1 = conn.prepareCall("{call sp_PAOP_syc(?,?,?)}");
            cs1.registerOutParameter(1, Types.VARCHAR);
            cs1.setString(2, aEjercicioFiscal);
            cs1.setString(3, mesIni);
            rs = cs1.executeQuery();
            while (rs.next()) {
                PAOP rPAOP = new PAOP();
                rPAOP.setCejercicioFiscal(rs.getString("cEjercicioFiscal"));
                rPAOP.setNmes(rs.getString("nMes"));
                rPAOP.setNrenglon(rs.getString("nRenglon"));
                rPAOP.setCcVE_CUCOP(rs.getString("cCVE_CUCOP"));
                rPAOP.setCcONCEPTO(rs.getString("cCONCEPTO"));
                rPAOP.setMmultianualEstimado(rs.getString("mMultianualEstimado"));
                rPAOP.setMestimadoMipymes(rs.getString("mEstimadoMipymes"));
                rPAOP.setMestimadoNoCubiertasTLC(rs.getString("mEstimadoNoCubiertasTLC"));
                rPAOP.setNcantidad(rs.getString("nCantidad"));
                rPAOP.setNunidadMedida(rs.getString("nUnidadMedida"));
                rPAOP.setCtipoProcContratacion(rs.getString("cTipoProcContratacion"));
                rPAOP.setNentidadFederativa(rs.getString("nEntidadFederativa"));
                rPAOP.setNtrimestre1(rs.getString("nTrimestre1"));
                rPAOP.setNtrimestre2(rs.getString("nTrimestre2"));
                rPAOP.setNtrimestre3(rs.getString("nTrimestre3"));
                rPAOP.setNtrimestre4(rs.getString("nTrimestre4"));
                rPAOP.setFinicialContrato(rs.getString("fInicialContrato"));
                rPAOP.setNplurianual(rs.getString("nPlurianual"));
                rPAOP.setNejerciciosFicales(rs.getString("nEjerciciosFicales"));
                rPAOP.setManualEjercer(rs.getString("mAnualEjercer"));
                rPAOP.setCcomentario1(rs.getString("cComentario1"));
                rPAOP.setFfinalContrato(rs.getString("fFinalContrato"));
                rPAOP.setCcomentario3(rs.getString("cComentario3"));
                rPAOP.setCtipoProcedimiento(rs.getString("cTipoProcedimiento"));
                rPAOP.setCstatus(rs.getString("cStatus"));
                PAOPList.add(rPAOP);
            }
            Salida = cs1.getString(1);
            if (!Salida.contentEquals("OK"))
                throw new SQLException(Salida);
            //		System.out.println("Salida: "+Salida);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            if (cs1 != null)
                cs1.close();
            cs1 = null;
            rs = null;
            pstmnt = null;
        }
        return PAOPList;
    }

    public static List callBalanza(Connection conn, String buscaCuentaIni, String buscaCuentaFin, String aEjercicioFiscal, String DepuraLineas, String DepuraColumnas, String cCentroContable, String TipoReporte, String mesIni, String mesFin, String FiltroSubcuenta) throws SQLException {
        List balanzaList = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        CallableStatement cs1 = null;
        String Salida = "";
        try {
            // EXECUTE sp_tBalanzaAnalitico_syc @Salida OUTPUT, '21203-00000-00000-00000', '', '2012', 'N', 'N', 'undefined', 'AnaliticoCC', '1', '5', 'APA8002291D9';
            cs1 = conn.prepareCall("{call sp_tBalanzaAnalitico_syc(?,?,?,?,?,?,?,?,?,?,?)}");
            cs1.registerOutParameter(1, Types.VARCHAR);
            cs1.setString(2, buscaCuentaIni);
            cs1.setString(3, buscaCuentaFin);
            cs1.setString(4, aEjercicioFiscal);
            cs1.setString(5, DepuraLineas);
            cs1.setString(6, DepuraColumnas);
            cs1.setString(7, cCentroContable);
            cs1.setString(8, TipoReporte);
            cs1.setString(9, mesIni);
            cs1.setString(10, mesFin);
            cs1.setString(11, FiltroSubcuenta);
            System.out.println("Se pidio que se depuraran columnas? " + DepuraColumnas);
            rs = cs1.executeQuery();
            while (rs.next()) {
                Balanza rBalanza = new Balanza();
                rBalanza.setNordenBalanza(rs.getString("nOrdenBalanza"));
                rBalanza.setCsangria(rs.getString("cSangria"));
                rBalanza.setNcuenta(rs.getString("nCuenta"));
                rBalanza.setDcuenta(rs.getString("dCuenta"));
                rBalanza.setSaldoInicial(rs.getString("SaldoInicial"));
                rBalanza.setSaldoInicialAcreedor(rs.getString("SaldoInicialAcreedor"));
                rBalanza.setSaldoInicialDeudor(rs.getString("SaldoInicialDeudor"));
                rBalanza.setMovimientosAcumuladosDebe(rs.getString("MovimientosAcumuladosDebe"));
                rBalanza.setMovimientosAcumuladosHaber(rs.getString("MovimientosAcumuladosHaber"));
                rBalanza.setSaldoMesAnterior(rs.getString("SaldoMesAnterior"));
                rBalanza.setMovimientosMesDebe(rs.getString("MovimientosMesDebe"));
                rBalanza.setMovimientosMesHaber(rs.getString("MovimientosMesHaber"));
                rBalanza.setSaldoFinal(rs.getString("SaldoFinal"));
                rBalanza.setSaldoFinalAcreedor(rs.getString("SaldoFinalAcreedor"));
                rBalanza.setSaldoFinalDeudor(rs.getString("SaldoFinalDeudor"));
                rBalanza.setAplicacionCuentar(rs.getString("AplicacionCuenta"));
                balanzaList.add(rBalanza);
            }
            Salida = cs1.getString(1);
            if (!Salida.contentEquals("OK"))
                throw new SQLException(Salida);
            //System.out.println("Salida: "+Salida);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            if (cs1 != null)
                cs1.close();
            cs1 = null;
            rs = null;
            pstmnt = null;
        }
        return balanzaList;
    }

    public static ArrayList<String> generaAnalitico(Connection conn, String buscaCuentaIni, String buscaCuentaFin, String aEjercicioFiscal, String DepuraLineas, String DepuraColumnas, String cCentroContable, String TipoReporte, String mesIni, String mesFin, String FiltroSubcuenta) throws SQLException {
        ArrayList<String> balanzaList = new ArrayList<String>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        CallableStatement cs1 = null;
        String Salida = "";
        try {
            // EXECUTE sp_tBalanzaAnalitico_syc @Salida OUTPUT, '21203-00000-00000-00000', '', '2012', 'N', 'N', 'undefined', 'AnaliticoCC', '1', '5', 'APA8002291D9';
            cs1 = conn.prepareCall("{call sp_tBalanzaAnalitico_syc(?,?,?,?,?,?,?,?,?,?,?)}");
            cs1.registerOutParameter(1, Types.VARCHAR);
            cs1.setString(2, buscaCuentaIni);
            cs1.setString(3, buscaCuentaFin);
            cs1.setString(4, aEjercicioFiscal);
            cs1.setString(5, DepuraLineas);
            cs1.setString(6, DepuraColumnas);
            cs1.setString(7, cCentroContable);
            cs1.setString(8, TipoReporte);
            cs1.setString(9, mesIni);
            cs1.setString(10, mesFin);
            cs1.setString(11, FiltroSubcuenta);
            rs = cs1.executeQuery();
            int COLUMNAS = DepuraColumnas.equalsIgnoreCase("S") ? 6 : 7;
            double[] totales = new double[COLUMNAS];
            double[] subtotales = new double[COLUMNAS];
            boolean primerSubtotal = false;
            boolean ultimoSubtotal = false;
            double saldoInicial = 0;
            double saldoFinal = 0;
            String nOrdenBalanza = "";
            int naturalezaCta = 0;
            if (DepuraColumnas.equals("S")) {
                while (rs.next()) {
                    if (!primerSubtotal) {
                        nOrdenBalanza = rs.getString("nOrdenBalanza");
                        primerSubtotal = true;
                    }
                    if (!rs.getString("nOrdenBalanza").equals(nOrdenBalanza)) {
                        balanzaList.add("");
                        balanzaList.add("SUBTOTAL");
                        balanzaList.add(subtotales[0] + "");
                        balanzaList.add(subtotales[1] + "");
                        balanzaList.add(subtotales[2] + "");
                        balanzaList.add(subtotales[3] + "");
                        balanzaList.add(subtotales[4] + "");
                        balanzaList.add(subtotales[5] + "");
                        subtotales = new double[COLUMNAS];
                        ultimoSubtotal = true;
                    }
                    nOrdenBalanza = rs.getString("nOrdenBalanza");
                    naturalezaCta = rs.getString("NaturalezaCuenta").equalsIgnoreCase("D") ? 1 : 2;
                    if (!rs.getString("nCuenta").equals("") && rs.getString("AplicacionCuenta").equalsIgnoreCase("S")) {
                        saldoInicial = Double.parseDouble(rs.getString("SaldoInicial"));
                        saldoFinal = Double.parseDouble(rs.getString("SaldoFinal"));
                        totales[0] += (naturalezaCta == 1) ? saldoInicial : 0;
                        totales[1] += (naturalezaCta == 2) ? saldoInicial : 0;
                        totales[2] += Double.parseDouble(rs.getString("MovimientosMesDebe"));
                        totales[3] += Double.parseDouble(rs.getString("MovimientosMesHaber"));
                        totales[4] += (naturalezaCta == 1) ? saldoFinal : 0;
                        totales[5] += (naturalezaCta == 2) ? saldoFinal : 0;
                        subtotales[0] += (naturalezaCta == 1) ? saldoInicial : 0;
                        subtotales[1] += (naturalezaCta == 2) ? saldoInicial : 0;
                        subtotales[2] += Double.parseDouble(rs.getString("MovimientosMesDebe"));
                        subtotales[3] += Double.parseDouble(rs.getString("MovimientosMesHaber"));
                        subtotales[4] += (naturalezaCta == 1) ? saldoFinal : 0;
                        subtotales[5] += (naturalezaCta == 2) ? saldoFinal : 0;
                    }
                    balanzaList.add(rs.getString("nCuenta"));
                    balanzaList.add(rs.getString("dCuenta"));
                    balanzaList.add((naturalezaCta == 1) ? rs.getString("SaldoInicial") : "");
                    balanzaList.add((naturalezaCta == 2) ? rs.getString("SaldoInicial") : "");
                    balanzaList.add(rs.getString("MovimientosMesDebe"));
                    balanzaList.add(rs.getString("MovimientosMesHaber"));
                    balanzaList.add((naturalezaCta == 1) ? rs.getString("SaldoFinal") : "");
                    balanzaList.add((naturalezaCta == 2) ? rs.getString("SaldoFinal") : "");
                }
                if (ultimoSubtotal) {
                    balanzaList.add("");
                    balanzaList.add("SUBTOTAL");
                    balanzaList.add(subtotales[0] + "");
                    balanzaList.add(subtotales[1] + "");
                    balanzaList.add(subtotales[2] + "");
                    balanzaList.add(subtotales[3] + "");
                    balanzaList.add(subtotales[4] + "");
                    balanzaList.add(subtotales[5] + "");
                }
                balanzaList.add("");
                balanzaList.add("TOTALES");
                for (int i = 0; i < (COLUMNAS); i++) balanzaList.add("" + totales[i]);
            } else {
                while (rs.next()) {
                    naturalezaCta = rs.getString("NaturalezaCuenta").equalsIgnoreCase("C") ? 1 : 2;
                    if (!primerSubtotal) {
                        nOrdenBalanza = rs.getString("nOrdenBalanza");
                        primerSubtotal = true;
                    }
                    if (!rs.getString("nOrdenBalanza").equals(nOrdenBalanza)) {
                        balanzaList.add("");
                        balanzaList.add("SUBTOTAL");
                        balanzaList.add(subtotales[0] + "");
                        balanzaList.add(subtotales[1] + "");
                        balanzaList.add(subtotales[2] + "");
                        balanzaList.add(subtotales[3] + "");
                        balanzaList.add(subtotales[4] + "");
                        balanzaList.add(subtotales[5] + "");
                        balanzaList.add(subtotales[6] + "");
                        subtotales = new double[COLUMNAS];
                        ultimoSubtotal = true;
                    }
                    nOrdenBalanza = rs.getString("nOrdenBalanza");
                    if (!rs.getString("nCuenta").equals("") && rs.getString("AplicacionCuenta").equalsIgnoreCase("S")) {
                        System.out.println("Aquiii en ampliada");
                        totales[0] += Double.parseDouble(rs.getString("SaldoInicial"));
                        totales[1] += Double.parseDouble(rs.getString("MovimientosAcumuladosDebe"));
                        totales[2] += Double.parseDouble(rs.getString("MovimientosAcumuladosHaber"));
                        totales[3] += Double.parseDouble(rs.getString("SaldoMesAnterior"));
                        totales[4] += Double.parseDouble(rs.getString("MovimientosMesDebe"));
                        totales[5] += Double.parseDouble(rs.getString("MovimientosMesHaber"));
                        totales[6] += Double.parseDouble(rs.getString("SaldoFinal"));
                        subtotales[0] += Double.parseDouble(rs.getString("SaldoInicial"));
                        subtotales[1] += Double.parseDouble(rs.getString("MovimientosAcumuladosDebe"));
                        subtotales[2] += Double.parseDouble(rs.getString("MovimientosAcumuladosHaber"));
                        subtotales[3] += Double.parseDouble(rs.getString("SaldoMesAnterior"));
                        subtotales[4] += Double.parseDouble(rs.getString("MovimientosMesDebe"));
                        subtotales[5] += Double.parseDouble(rs.getString("MovimientosMesHaber"));
                        subtotales[6] += Double.parseDouble(rs.getString("SaldoFinal"));
                    }
                    balanzaList.add(rs.getString("nCuenta"));
                    balanzaList.add(rs.getString("dCuenta"));
                    balanzaList.add(rs.getString("SaldoInicial"));
                    balanzaList.add(rs.getString("MovimientosAcumuladosDebe"));
                    balanzaList.add(rs.getString("MovimientosAcumuladosHaber"));
                    balanzaList.add(rs.getString("SaldoMesAnterior"));
                    balanzaList.add(rs.getString("MovimientosMesDebe"));
                    balanzaList.add(rs.getString("MovimientosMesHaber"));
                    balanzaList.add(rs.getString("SaldoFinal"));
                }
                if (ultimoSubtotal) {
                    balanzaList.add("");
                    balanzaList.add("SUBTOTAL");
                    balanzaList.add(subtotales[0] + "");
                    balanzaList.add(subtotales[1] + "");
                    balanzaList.add(subtotales[2] + "");
                    balanzaList.add(subtotales[3] + "");
                    balanzaList.add(subtotales[4] + "");
                    balanzaList.add(subtotales[5] + "");
                    balanzaList.add(subtotales[6] + "");
                }
                balanzaList.add("");
                balanzaList.add("TOTALES");
                for (int i = 0; i < (COLUMNAS); i++) balanzaList.add("" + totales[i]);
            }
            Salida = cs1.getString(1);
            if (!Salida.contentEquals("OK"))
                throw new SQLException(Salida);
            //System.out.println("Salida: "+Salida);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            if (cs1 != null)
                cs1.close();
            cs1 = null;
            rs = null;
            pstmnt = null;
        }
        return balanzaList;
    }

    public static ArrayList<String> generaBalanza(Connection conn, String buscaCuentaIni, String buscaCuentaFin, String aEjercicioFiscal, String DepuraLineas, String DepuraColumnas, String cCentroContable, String TipoReporte, String mesIni, String mesFin, String FiltroSubcuenta) throws SQLException {
        ArrayList<String> balanzaList = new ArrayList<String>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        CallableStatement cs1 = null;
        String Salida = "";
        try {
            // EXECUTE sp_tBalanzaAnalitico_syc @Salida OUTPUT, '21203-00000-00000-00000', '', '2012', 'N', 'N', 'undefined', 'AnaliticoCC', '1', '5', 'APA8002291D9';
            cs1 = conn.prepareCall("{call sp_tBalanzaAnalitico_syc(?,?,?,?,?,?,?,?,?,?,?)}");
            cs1.registerOutParameter(1, Types.VARCHAR);
            cs1.setString(2, buscaCuentaIni);
            cs1.setString(3, buscaCuentaFin);
            cs1.setString(4, aEjercicioFiscal);
            cs1.setString(5, DepuraLineas);
            cs1.setString(6, DepuraColumnas);
            cs1.setString(7, cCentroContable);
            cs1.setString(8, TipoReporte);
            cs1.setString(9, mesIni);
            cs1.setString(10, mesFin);
            cs1.setString(11, FiltroSubcuenta);
            rs = cs1.executeQuery();
            double[] totales = new double[7];
            int tipoMovimiento = 0;
            if (DepuraColumnas.equals("S")) {
                while (rs.next()) {
                    balanzaList.add(rs.getString("nCuenta"));
                    balanzaList.add(rs.getString("dCuenta"));
                    balanzaList.add(rs.getString("SaldoInicialDeudor"));
                    balanzaList.add(rs.getString("SaldoInicialAcreedor"));
                    balanzaList.add(rs.getString("MovimientosMesDebe"));
                    balanzaList.add(rs.getString("MovimientosMesHaber"));
                    balanzaList.add(rs.getString("SaldoFinalDeudor"));
                    balanzaList.add(rs.getString("SaldoFinalAcreedor"));
                    if (rs.getString("nCuenta").compareTo("") != 0) {
                        totales[0] += Double.parseDouble(rs.getString("SaldoInicialDeudor"));
                        totales[1] += Double.parseDouble(rs.getString("SaldoInicialAcreedor"));
                        totales[2] += Double.parseDouble(rs.getString("MovimientosMesDebe"));
                        totales[3] += Double.parseDouble(rs.getString("MovimientosMesHaber"));
                        totales[4] += Double.parseDouble(rs.getString("SaldoFinalDeudor"));
                        totales[5] += Double.parseDouble(rs.getString("SaldoFinalAcreedor"));
                    }
                }
                balanzaList.add("");
                balanzaList.add("TOTALES");
                for (int i = 0; i < 6; i++) balanzaList.add("" + totales[i]);
            } else {
                while (rs.next()) {
                    tipoMovimiento = (rs.getString("NaturalezaCuenta").equals("A")) ? 1 : 2;
                    balanzaList.add(rs.getString("nCuenta"));
                    balanzaList.add(rs.getString("dCuenta"));
                    balanzaList.add(rs.getString("SaldoInicial"));
                    balanzaList.add(rs.getString("MovimientosAcumuladosDebe"));
                    balanzaList.add(rs.getString("MovimientosAcumuladosHaber"));
                    balanzaList.add(rs.getString("SaldoMesAnterior"));
                    balanzaList.add(rs.getString("MovimientosMesDebe"));
                    balanzaList.add(rs.getString("MovimientosMesHaber"));
                    balanzaList.add(rs.getString("SaldoFinal"));
                    if (rs.getString("nCuenta").compareTo("") != 0) {
                        totales[0] += Double.parseDouble(rs.getString("SaldoInicial"));
                        totales[1] += Double.parseDouble(rs.getString("MovimientosAcumuladosDebe"));
                        totales[2] += Double.parseDouble(rs.getString("MovimientosAcumuladosHaber"));
                        if (tipoMovimiento == 1)
                            totales[3] += Double.parseDouble(rs.getString("SaldoMesAnterior")) * (-1);
                        else
                            totales[3] += Double.parseDouble(rs.getString("SaldoMesAnterior"));
                        totales[4] += Double.parseDouble(rs.getString("MovimientosMesDebe"));
                        totales[5] += Double.parseDouble(rs.getString("MovimientosMesHaber"));
                        if (tipoMovimiento == 1)
                            totales[6] += Double.parseDouble(rs.getString("SaldoFinal")) * (-1);
                        else
                            totales[6] += Double.parseDouble(rs.getString("SaldoFinal"));
                    }
                }
                balanzaList.add("");
                balanzaList.add("TOTALES");
                for (int i = 0; i < 7; i++) balanzaList.add("" + totales[i]);
            }
            Salida = cs1.getString(1);
            if (!Salida.contentEquals("OK"))
                throw new SQLException(Salida);
            //System.out.println("Salida: "+Salida);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            if (cs1 != null)
                cs1.close();
            cs1 = null;
            rs = null;
            pstmnt = null;
        }
        return balanzaList;
    }

    public static ArrayList<String> generaMultiReporteOP(Connection conn, String cCentroContable, String fechaI, String fechaF, String idunidadresponsable, boolean generarVacio, String reportBody, String strUsuario, String strCondicion, String strCondMultiR, String columnasBorrar, String[] NomCols, String[] ForCols, ArrayList Todo) throws SQLException {
        ArrayList<String> balanzaList = new ArrayList<String>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        CallableStatement cs1 = null;
        //String Salida="";
        try {
            cs1 = conn.prepareCall("{call sp_pMultiReporte_syc_OP2(?,?,?,?,?,?,?,?,?)}");
            //		cs1.setString(1, strCondMultiR);
            cs1.setString(1, strCondMultiR);
            cs1.setString(2, "'81101','81102','81103','81104','81105','81106','81107','81108','81109','81110','82101','82102','82103','82104','82105','82106','82107','82108'");
            cs1.setString(3, "cSubCuenta");
            cs1.setString(4, "cSubCuenta");
            cs1.setString(5, "mSaldo12");
            cs1.setString(6, "GENERAL");
            cs1.setString(7, strUsuario);
            cs1.setString(8, strCondicion);
            cs1.registerOutParameter(9, Types.VARCHAR);
            rs = cs1.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();
            int i = 0;
            NomCols = new String[numberOfColumns + 1];
            ForCols = new String[numberOfColumns + 1];
            while (i < numberOfColumns) {
                NomCols[i] = rsmd.getColumnName(i + 1);
                switch(rsmd.getColumnType(i + 1)) {
                    case 3:
                        ForCols[i] = "Double";
                        break;
                    default:
                        ForCols[i] = "";
                        break;
                }
                i++;
            }
            Todo.add(new ArrayList<String>(Arrays.asList(NomCols)));
            Todo.add(new ArrayList<String>(Arrays.asList(ForCols)));
            while (rs.next()) {
                i = 0;
                String nada = "";
                while (i < numberOfColumns) {
                    try {
                        balanzaList.add(rs.getString(i + 1));
                    } catch (Exception es) {
                        nada = es.getMessage();
                    }
                    i++;
                }
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
            cs1 = null;
            rs = null;
            pstmnt = null;
        }
        return balanzaList;
    }

    public static List callAuxiliares(Connection conn, String swhere, String cCentroContable, String buscaCuentaIni, String fAuxIni, String fAuxFin) throws SQLException {
        List auxiliaresList = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        CallableStatement cs1 = null;
        String Salida = "";
        try {
            // EXECUTE sp_GeneraAuxiliarCNF @Salida OUTPUT, swhere;
            cs1 = conn.prepareCall("{call sp_GeneraAuxiliarCNF(?,?,?,?,?,?)}");
            cs1.registerOutParameter(1, Types.VARCHAR);
            cs1.setString(2, swhere);
            cs1.setString(3, cCentroContable);
            cs1.setString(4, buscaCuentaIni);
            cs1.setString(5, fAuxIni);
            cs1.setString(6, fAuxFin);
            rs = cs1.executeQuery();
            while (rs.next()) {
                Auxiliares rAuxiliares = new Auxiliares();
                rAuxiliares.setAuxTipo(rs.getString("cTipoPoliza"));
                rAuxiliares.setAuxNum(rs.getString("nFolioPoliza"));
                rAuxiliares.setAuxCxP(rs.getString("caNoContrarrecibo"));
                rAuxiliares.setAuxFecha(rs.getString("fMovimiento"));
                rAuxiliares.setAuxConcepto(rs.getString("cTipoDocumento"));
                rAuxiliares.setAuxReferencia(rs.getString("cDescripcionMovPol"));
                rAuxiliares.setCtipoMovimiento(rs.getString("cTipoMovimiento"));
                if (rAuxiliares.getCtipoMovimiento().equals("A")) {
                    rAuxiliares.setAuxCargos("0");
                    rAuxiliares.setAuxAbonos(rs.getString("mMovimiento"));
                } else {
                    rAuxiliares.setAuxCargos(rs.getString("mMovimiento"));
                    rAuxiliares.setAuxAbonos("0");
                }
                ;
                rAuxiliares.setAuxSaldo(rs.getString("mAcumulado"));
                auxiliaresList.add(rAuxiliares);
            }
            Salida = cs1.getString(1);
            if (!Salida.contentEquals("OK"))
                throw new SQLException(Salida);
            //System.out.println("Salida: "+Salida);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            if (cs1 != null)
                cs1.close();
            cs1 = null;
            rs = null;
            pstmnt = null;
        }
        return auxiliaresList;
    }

    public static ArrayList<String> generaAuxiliar(Connection conn, String swhere, String cCentroContable, String buscaCuentaIni, String fAuxIni, String fAuxFin) throws SQLException {
        ArrayList<String> auxiliaresList = new ArrayList<String>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        CallableStatement cs1 = null;
        String Salida = "";
        int total_SI = 0;
        String monto = "";
        double[] totales = new double[3];
        boolean inicial = false;
        try {
            cs1 = conn.prepareCall("{call sp_GeneraAuxiliarCNF(?,?,?,?,?,?)}");
            cs1.registerOutParameter(1, Types.VARCHAR);
            cs1.setString(2, swhere);
            cs1.setString(3, cCentroContable);
            cs1.setString(4, buscaCuentaIni);
            cs1.setString(5, fAuxIni);
            cs1.setString(6, fAuxFin);
            rs = cs1.executeQuery();
            int cTipoMovimiento = 0;
            while (rs.next()) {
                if (inicial && rs.getString("nFolioPoliza").equals("0")) {
                    for (// 5
                    int i = 0; // 5
                    i < 8; // 5
                    i++) auxiliaresList.add("");
                    auxiliaresList.add("TOTALES");
                    for (// 3
                    int i = 0; // 3
                    i < 3; // 3
                    i++) if (total_SI == 1 && i == 2) {
                        auxiliaresList.add("" + monto);
                        total_SI = 0;
                        monto = "";
                    } else {
                        auxiliaresList.add("" + totales[i]);
                    }
                    totales = new double[3];
                }
                if (!inicial)
                    inicial = true;
                if (rs.getString("cDescripcionMovPol").equalsIgnoreCase("SALDO INICIAL:")) {
                    auxiliaresList.add("");
                    auxiliaresList.add("");
                    auxiliaresList.add("");
                    auxiliaresList.add(rs.getString("fMovimiento").replace("00:00:00.0", ""));
                    auxiliaresList.add(rs.getString("nCuenta"));
                    auxiliaresList.add(rs.getString("nSubCuenta"));
                    auxiliaresList.add("");
                    auxiliaresList.add(rs.getString("nCuenta") + " " + rs.getString("dCuenta") + " " + rs.getString("cTipoDocumento") + " " + rs.getString("nSubCuenta") + " " + rs.getString("detalle"));
                    auxiliaresList.add(rs.getString("cDescripcionMovPol"));
                    auxiliaresList.add("");
                    auxiliaresList.add("");
                    auxiliaresList.add(rs.getString("mAcumulado"));
                    total_SI = 1;
                    monto = rs.getString("mAcumulado");
                    continue;
                }
                cTipoMovimiento = (rs.getString("cTipoMovimiento").equals("C")) ? 1 : 2;
                auxiliaresList.add(rs.getString("cTipoPoliza"));
                auxiliaresList.add(rs.getString("nFolioPoliza"));
                auxiliaresList.add(rs.getString("caNoContrarrecibo"));
                auxiliaresList.add(rs.getString("fMovimiento").replace("00:00:00.0", ""));
                auxiliaresList.add(rs.getString("nCuenta"));
                auxiliaresList.add(rs.getString("nSubCuenta"));
                auxiliaresList.add(rs.getString("cheque"));
                auxiliaresList.add(rs.getString("cTipoDocumento"));
                auxiliaresList.add(rs.getString("cDescripcionMovPol"));
                auxiliaresList.add(cTipoMovimiento == 1 ? rs.getString("mMovimiento") : "0");
                auxiliaresList.add(cTipoMovimiento == 2 ? rs.getString("mMovimiento") : "0");
                auxiliaresList.add(rs.getString("mAcumulado"));
                totales[0] += (Double.parseDouble(cTipoMovimiento == 1 ? rs.getString("mMovimiento") : "0"));
                totales[1] += (Double.parseDouble(cTipoMovimiento == 2 ? rs.getString("mMovimiento") : "0"));
                totales[2] = (Double.parseDouble(rs.getString("mAcumulado")));
                System.out.println(totales[2] + " Totaless");
                total_SI = 0;
            }
            for (// 5
            int i = 0; // 5
            i < 8; // 5
            i++) auxiliaresList.add("");
            auxiliaresList.add("TOTALES");
            for (// 3
            int i = 0; // 3
            i < 3; // 3
            i++) {
                if (total_SI == 1 && i == 2) {
                    auxiliaresList.add("" + monto);
                } else {
                    auxiliaresList.add("" + totales[i]);
                }
            }
            Salida = cs1.getString(1);
            if (!Salida.contentEquals("OK"))
                throw new SQLException(Salida);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            if (cs1 != null)
                cs1.close();
            cs1 = null;
            rs = null;
            pstmnt = null;
            System.out.println("Todo a null en auxiliares");
        }
        return auxiliaresList;
    }

    public static ArrayList<String> generaAuxiliarMayor(Connection conn, String buscaCuentaIni, String subCuenta, String cCentroContable, String fAuxIni, String fAuxFin, String subtot) throws SQLException {
        ArrayList<String> auxiliaresList = new ArrayList<String>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        CallableStatement cs1 = null;
        try {
            cs1 = conn.prepareCall("{call sp_l_GeneraAuxiliarMayor(?,?,?,?,?,?)}");
            cs1.setString(1, buscaCuentaIni);
            cs1.setString(2, subCuenta);
            cs1.setString(3, cCentroContable);
            cs1.setString(4, fAuxIni);
            cs1.setString(5, fAuxFin);
            cs1.setString(6, subtot);
            rs = cs1.executeQuery();
            while (rs.next()) {
                auxiliaresList.add(rs.getString("cTipoPoliza"));
                auxiliaresList.add(rs.getString("nFolioPoliza"));
                auxiliaresList.add(rs.getString("cCentroContable"));
                auxiliaresList.add(rs.getString("cCxP"));
                auxiliaresList.add(rs.getString("fMovimiento").replace("00:00:00.0", ""));
                auxiliaresList.add(rs.getString("nCuenta"));
                auxiliaresList.add(rs.getString("nSubCuenta"));
                auxiliaresList.add(rs.getString("cRFC"));
                auxiliaresList.add(rs.getString("cNOMBRE"));
                auxiliaresList.add(rs.getString("cConcepto"));
                auxiliaresList.add(rs.getString("Descripcion"));
                auxiliaresList.add(rs.getString("mCargo"));
                auxiliaresList.add(rs.getString("mAbono"));
                auxiliaresList.add(rs.getString("Saldo"));
                auxiliaresList.add(rs.getString("cAplicado"));
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
            cs1 = null;
            rs = null;
            pstmnt = null;
            System.out.println("Todo a null en auxiliares");
        }
        return auxiliaresList;
    }

    public static ArrayList<String> generaAuxiliarMayorSaldos(Connection conn, String buscaCuentaIni, String subCuenta, String cCentroContable, String fAuxIni, String fAuxFin) throws SQLException {
        ArrayList<String> auxiliaresList = new ArrayList<String>();
        ResultSet rs_Saldos = null;
        CallableStatement cs_Saldos = null;
        try {
            cs_Saldos = conn.prepareCall("{call sp_l_AuxiliarMayorSaldos(?,?,?,?,?)}");
            cs_Saldos.setString(1, buscaCuentaIni);
            cs_Saldos.setString(2, subCuenta);
            cs_Saldos.setString(3, cCentroContable);
            cs_Saldos.setString(4, fAuxIni);
            cs_Saldos.setString(5, fAuxFin);
            rs_Saldos = cs_Saldos.executeQuery();
            while (rs_Saldos.next()) {
                auxiliaresList.add(rs_Saldos.getString("SaldoInicial"));
                auxiliaresList.add(rs_Saldos.getString("Cargos"));
                auxiliaresList.add(rs_Saldos.getString("Abonos"));
                auxiliaresList.add(rs_Saldos.getString("SaldoFinal"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs_Saldos != null)
                rs_Saldos.close();
            if (cs_Saldos != null)
                cs_Saldos.close();
            cs_Saldos = null;
            rs_Saldos = null;
            System.out.println("Todo a null en auxiliares");
        }
        return auxiliaresList;
    }

    public static List selectAll(Connection conn, String sql, int opc) throws SQLException {
        List gralList = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement(sql);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                General g = new General();
                switch(opc) {
                    case 0:
                        /*Datos necesarios para el grid de detalle del Reporte Estadï¿½stico por Area*/
                        g.setArea("");
                        g.setFolio(rs.getString("folio"));
                        g.setReferencia(rs.getString("referencia"));
                        g.setNombreRemitente(rs.getString("nombre_rem"));
                        g.setAreaRemitente(rs.getString("desc_area_rem"));
                        g.setFechaAtencion(rs.getString("fechalimite"));
                        g.setCerrado(rs.getString("cerrado"));
                        g.setDestinatario("");
                        g.setPrioridad(rs.getString("prioridad"));
                        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
						 * Esteban Badillo. Fecha: 15/Oct/2009
						 * Descripcion:
						 * 	Se agregan tres columnas nuevas al grid de presentaciï¿½n de detalles
						 *  del Reporte Estadï¿½stico por Area.
						 * */
                        g.setResponsableAreaDesc(rs.getString("DESC_AREA_RESP"));
                        g.setResponsableNombre(rs.getString("NOMBRE_RESP"));
                        //Tambien se puede proyectar
                        g.setFechaEnvio(rs.getString("fecha_envio"));
                        //en el query el campo "fecha_envio" en lugar de "fecha_inicio", ya que
                        //dicho "fecha_envio" presenta una informaciï¿½n mas detallada sobre la hora
                        //en que se realizï¿½ el registro del envï¿½o.
                        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
                        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
						 * Esteban Badillo. Fecha: 19/Feb/2010
						 * Descripcion:
						 * 	Se agrega la columna "Asunto" al reporte.
						 * */
                        g.setAsunto(rs.getString("asunto"));
                        break;
                    case 1:
                        g.setFolio(rs.getString("folio"));
                        g.setReferencia(rs.getString("referencia"));
                        g.setFechaRegistro(rs.getString("fecharegistro"));
                        g.setResponsableArea(rs.getString("responsable_area"));
                        g.setResponsableAreaDesc(rs.getString("desc_area_resp"));
                        g.setResponsableId(rs.getString("responsable_id"));
                        g.setResponsableNombre(rs.getString("nombre_resp"));
                        g.setRemitenteId(rs.getString("remitente_id"));
                        g.setNombreRemitente(rs.getString("remitente"));
                        g.setTipoAsunto(rs.getString("tipoasunto"));
                        g.setEstatus(rs.getString("estatus"));
                        g.setPrioridad(rs.getString("prioridad"));
                        g.setUser01(rs.getString("user01"));
                        g.setTipoInstruccion(rs.getString("tipoinstruccion"));
                        break;
                    case 2:
                        g.setArea(rs.getString("desc_area_rem"));
                        g.setFolio(rs.getString("folio"));
                        g.setReferencia(rs.getString("referencia"));
                        g.setNombreRemitente(rs.getString("nombre_rem"));
                        g.setAreaRemitente(rs.getString("desc_area_rem"));
                        //MFR - Se agrega a peticiï¿½n de Conagua para el Reporte Estadistico por Empleado
                        g.setFechaEnvio(rs.getString("fecha_envio"));
                        g.setFechaAtencion(rs.getString("user03"));
                        //g.setFechaAtencion(rs.getString("fechalimite"));
                        g.setCerrado(rs.getString("cerrado"));
                        //g.setDestinatario(rs.getString("nombre_resp"));
                        g.setPrioridad(rs.getString("prioridad"));
                        //MFR - Se agrega a peticiï¿½n de Conagua para el Reporte Estadistico por Empleado
                        g.setAsunto(rs.getString("asunto"));
                        break;
                    case 3:
                        //Ricardo:se agrega metodo (depura) para que no este pintando valores null en la tabla
                        g.setFolio(depura(rs.getString("folio")));
                        g.setReferencia(depura(rs.getString("referencia")));
                        g.setArea(depura(rs.getString("desc_area_rem")));
                        g.setAreaRemitente(depura(rs.getString("remitente_area")));
                        g.setNombreRemitente(depura(rs.getString("nombre_rem")));
                        g.setCerrado(depura(rs.getString("cerrado")));
                        g.setTipoInstruccion(depura(rs.getString("tipo_instruccion")));
                        g.setPrioridad(depura(rs.getString("prioridad")));
                        g.setAsunto(depura(rs.getString("asunto")));
                        g.setResponsableAreaDesc(depura(rs.getString("desc_area_resp")));
                        g.setResponsableNombre(depura(rs.getString("nombre_resp")));
                        g.setFechaRegistro(depura(rs.getString("fecharegistro")));
                        /*
						 * Esteban Badillo. Fecha: 10/Sep/2009
						 * Descripcion:
						 * 	Se agregan los cmapos fechalimite y fecha_envio como parte de los cambios solicitados para el Reporte General
						 * */
                        g.setFechaLimite(depura(rs.getString("fechalimite")));
                        g.setFechaEnvio(depura(rs.getString("fechaenvio")));
                        break;
                }
                gralList.add(g);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return gralList;
    }

    protected static String depura(String valor) {
        //Ricardo:se declara este metodo de depura para que no este pintando valores null en la tabla
        String aux = "";
        if (valor != null)
            aux = valor;
        return aux;
    }

    public static int update(Connection conn, Reporte r) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("UPDATE cg_reporte SET r_nombre = ?, r_path = ?, r_descripcion = ? WHERE id_reporte = ?");
            pstmnt.setString(1, r.getNombre());
            pstmnt.setString(2, r.getRuta());
            pstmnt.setString(3, r.getDescripcion());
            pstmnt.setInt(4, r.getIdReporte());
            // FIXME Solo se actualizan o tambien se insertan?
            if (!r.getParametros().isEmpty()) {
                for (Iterator iter = r.getParametros().keySet().iterator(); iter.hasNext(); ) {
                    String name = (String) iter.next();
                    ReporteParametro rp = r.getParametro(name);
                    if (rp != null) {
                        ReporteParametroManager.update(conn, rp);
                    } else {
                        ReporteParametroManager.insert(conn, rp);
                    }
                }
            }
            retval = pstmnt.executeUpdate();
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return retval;
    }

    public static List<AnteProyectoAut> callReporteAnteproyecto(Connection conn, String ep, String ejercicioFiscal, String cOrderBy, String cGroupBy, String cuenta) throws SQLException {
        List<AnteProyectoAut> listReg = new ArrayList<AnteProyectoAut>();
        PreparedStatement pstmnt = null;
        PreparedStatement pstmntH = null;
        ResultSet rs = null;
        ResultSet rsH = null;
        String sqlCLAVESIAFF = "";
        String sqlCLAVEINTERNA = "";
        String sqlEPAnteproyecto = "";
        String sqlAnteproyectoDetalleAut = "";
        try {
            sqlCLAVESIAFF = "(SELECT CLAVESIAFF FROM tCatalogoEPAnteproy WHERE aEjercicioFiscal = " + ejercicioFiscal + " ";
            sqlCLAVEINTERNA = "(SELECT CLAVEINTERNA FROM tCatalogoEPAnteproy WHERE aEjercicioFiscal = " + ejercicioFiscal + " ";
            sqlEPAnteproyecto += (ep != "" && ep != null ? " AND EP = '" + ep + "'" : "");
            sqlCLAVESIAFF = sqlCLAVESIAFF + sqlEPAnteproyecto + ") ";
            sqlCLAVEINTERNA = sqlCLAVEINTERNA + sqlEPAnteproyecto + ") ";
            sqlAnteproyectoDetalleAut = "SELECT nFolioAnteProyectoAut, nConsecutivo, aEjercicioFiscal, cUnidadResponsable, " + "cUnidadEjecutora, cClaveSiaff, cClaveInterna, ISNULL(mCalculado,'') AS mCalculado, " + "ISNULL(mOptimo,'') AS mOptimo, ISNULL(mIreductible,'') AS mIreductible " + " FROM tAnteProyectoDetalleAut " + " WHERE CCLAVESIAFF = " + sqlCLAVESIAFF + " AND CCLAVEINTERNA = " + sqlCLAVEINTERNA;
            pstmnt = conn.prepareStatement(sqlAnteproyectoDetalleAut);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                String sqlAnteproyectoEncabezadoAut = " SELECT nFolioAnteProyectoAut, cRevisor, cAutorizado FROM tAnteProyectoEncabezadoAut " + " WHERE nFolioAnteProyectoAut = '" + rs.getString("nFolioAnteProyectoAut") + "' " + " AND aEjercicioFiscal = '" + rs.getString("aEjercicioFiscal") + "' ";
                pstmntH = conn.prepareStatement(sqlAnteproyectoEncabezadoAut);
                rsH = pstmntH.executeQuery();
                if (rsH.next()) {
                    String nFolioAnteProyectoAut = rsH.getString("nFolioAnteProyectoAut");
                    String revisor = (rsH.getString("cRevisor") != null || !("").equals(rsH.getString("cRevisor")) || !(" ").equals(rsH.getString("cRevisor"))) ? rsH.getString("cRevisor") : "";
                    String autorizador = (rsH.getString("cAutorizado") != null || !("").equals(rsH.getString("cAutorizado")) || !(" ").equals(rsH.getString("cAutorizado"))) ? rsH.getString("cAutorizado") : "";
                    if (((" ").equals(revisor) || ("A").equals(revisor) || (" ").equals(autorizador) || ("A").equals(autorizador)) && nFolioAnteProyectoAut != null) {
                        //FOLIOANTEPROYECTO||RENGLON||EJERCICIO FISCAL||UNIDAD RESPONSABLE||UNIDADEJECUTORA||CLAVESIAFF||CLAVEINTERNA||mCALCULADO||mOPTIMO||mIRREDUCTIBLE||
                        AnteProyectoAut anteproyAut = new AnteProyectoAut();
                        anteproyAut.setFolioAnteProyectoAut(rs.getString("nFolioAnteProyectoAut"));
                        anteproyAut.setD_NConsecutivo(rs.getString("nConsecutivo"));
                        if (cGroupBy.equals("aEjercicioFiscal_1") || cGroupBy.equals("cSubCuenta")) {
                            anteproyAut.setEjercicioFiscal(rs.getString("aEjercicioFiscal"));
                        }
                        if (cGroupBy.equals("cUnidadResponsable_3") || cGroupBy.equals("cSubCuenta")) {
                            anteproyAut.setUnidadResponsableEP(rs.getString("cUnidadResponsable"));
                        }
                        if (cGroupBy.equals("cUnidadResponsable_15") || cGroupBy.equals("cSubCuenta")) {
                            anteproyAut.setD_CUnidadEjecutora(rs.getString("cUnidadEjecutora"));
                        }
                        if (cGroupBy.equals("cSubCuenta")) {
                            anteproyAut.setD_CClaveSIAFF(rs.getString("cClaveSiaff"));
                        }
                        if (cGroupBy.equals("cSubCuenta")) {
                            anteproyAut.setD_CClaveInterna(rs.getString("cClaveInterna"));
                        }
                        if (cuenta == "" || cuenta.contains("calculado")) {
                            anteproyAut.setD_MCalculado(rs.getString("mCalculado"));
                        }
                        if (cuenta == "" || cuenta.contains("optimo")) {
                            anteproyAut.setD_MOptimo(rs.getString("mOptimo"));
                        }
                        if (cuenta == "" || cuenta.contains("irreductible")) {
                            anteproyAut.setD_MIreductible(rs.getString("mIreductible"));
                        }
                        if (cuenta == "" || cuenta.contains("calculado,optimo")) {
                            anteproyAut.setD_MCalculado(rs.getString("mCalculado"));
                            anteproyAut.setD_MOptimo(rs.getString("mOptimo"));
                        }
                        if (cuenta == "" || cuenta.contains("optimo,calculado")) {
                            anteproyAut.setD_MCalculado(rs.getString("mCalculado"));
                            anteproyAut.setD_MOptimo(rs.getString("mOptimo"));
                        }
                        if (cuenta == "" || cuenta.contains("calculado,irreductible")) {
                            anteproyAut.setD_MCalculado(rs.getString("mCalculado"));
                            anteproyAut.setD_MIreductible(rs.getString("mIreductible"));
                        }
                        if (cuenta == "" || cuenta.contains("irreductible,calculado")) {
                            anteproyAut.setD_MCalculado(rs.getString("mCalculado"));
                            anteproyAut.setD_MIreductible(rs.getString("mIreductible"));
                        }
                        if (cuenta == "" || cuenta.contains("optimo,irreductible")) {
                            anteproyAut.setD_MOptimo(rs.getString("mOptimo"));
                            anteproyAut.setD_MIreductible(rs.getString("mIreductible"));
                        }
                        if (cuenta == "" || cuenta.contains("irreductible,optimo")) {
                            anteproyAut.setD_MOptimo(rs.getString("mOptimo"));
                            anteproyAut.setD_MIreductible(rs.getString("mIreductible"));
                        }
                        if (cuenta == "" || cuenta.contains("calculado,optimo,irreductible")) {
                            anteproyAut.setD_MCalculado(rs.getString("mCalculado"));
                            anteproyAut.setD_MOptimo(rs.getString("mOptimo"));
                            anteproyAut.setD_MIreductible(rs.getString("mIreductible"));
                        }
                        if (cuenta == "" || cuenta.contains("calculado,irreductible,optimo")) {
                            anteproyAut.setD_MCalculado(rs.getString("mCalculado"));
                            anteproyAut.setD_MOptimo(rs.getString("mOptimo"));
                            anteproyAut.setD_MIreductible(rs.getString("mIreductible"));
                        }
                        if (cuenta == "" || cuenta.contains("optimo,calculado,irreductible")) {
                            anteproyAut.setD_MCalculado(rs.getString("mCalculado"));
                            anteproyAut.setD_MOptimo(rs.getString("mOptimo"));
                            anteproyAut.setD_MIreductible(rs.getString("mIreductible"));
                        }
                        if (cuenta == "" || cuenta.contains("optimo,irreductible,calculado")) {
                            anteproyAut.setD_MCalculado(rs.getString("mCalculado"));
                            anteproyAut.setD_MOptimo(rs.getString("mOptimo"));
                            anteproyAut.setD_MIreductible(rs.getString("mIreductible"));
                        }
                        if (cuenta == "" || cuenta.contains("irreductible,optimo,calculado")) {
                            anteproyAut.setD_MCalculado(rs.getString("mCalculado"));
                            anteproyAut.setD_MOptimo(rs.getString("mOptimo"));
                            anteproyAut.setD_MIreductible(rs.getString("mIreductible"));
                        }
                        if (cuenta == "" || cuenta.contains("irreductible,calculado,optimo")) {
                            anteproyAut.setD_MCalculado(rs.getString("mCalculado"));
                            anteproyAut.setD_MOptimo(rs.getString("mOptimo"));
                            anteproyAut.setD_MIreductible(rs.getString("mIreductible"));
                        }
                        listReg.add(anteproyAut);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmnt != null) {
                pstmnt.close();
            }
            rs = null;
            pstmnt = null;
        }
        return listReg;
    }

    public static List<AnteProyectoAut> callReporteAnteproyectoParametros(Connection conn, String ejercicioFiscal, String ramoEP, String unidadResponsableEP, String grupoFuncional, String funcion, String subFuncion, String programaGeneral, String programaPresupuestario, String actividadInstitucional, String partida, String tipoGasto, String fuenteFinanciamiento, String entidadFederativa, String cartera, String unidadEjecutora, String unidadNormativa, String cOrderBy, String cGroupBy, String cuenta, String usuario, String filtro) throws SQLException {
        List<AnteProyectoAut> listReg = new ArrayList<AnteProyectoAut>();
        PreparedStatement pstmntSubQuery = null;
        ResultSet rsSubQuery = null;
        String subQueryCLAVESIAFF = "";
        String subQueryCLAVEINTERNA = "";
        String subQuery = "";
        String sqlAnteproyectoDetalleAut = "";
        try {
            subQueryCLAVESIAFF = "(SELECT CLAVESIAFF FROM tCatalogoEPAnteproy WHERE aEjercicioFiscal = " + ejercicioFiscal;
            subQueryCLAVEINTERNA = "(SELECT CLAVEINTERNA FROM tCatalogoEPAnteproy WHERE aEjercicioFiscal = " + ejercicioFiscal;
            subQuery += (ramoEP != "" && ramoEP != null ? " AND cRamoEP = '" + ramoEP + "'" : "");
            subQuery += (unidadResponsableEP != "" && unidadResponsableEP != null ? " AND cUnidadResponsableEP = '" + unidadResponsableEP + "'" : "");
            subQuery += (grupoFuncional != "" && grupoFuncional != null ? " AND cGrupoFuncional = '" + grupoFuncional + "'" : "");
            subQuery += (funcion != "" && funcion != null ? " AND cFuncion = '" + funcion + "'" : "");
            subQuery += (subFuncion != "" && subFuncion != null ? " AND cSubFuncion = '" + subFuncion + "'" : "");
            subQuery += (programaGeneral != "" && programaGeneral != null ? " AND cProgramaGeneral = '" + programaGeneral + "'" : "");
            subQuery += (actividadInstitucional != "" && actividadInstitucional != null ? " AND cActividadInstitucional = '" + actividadInstitucional + "'" : "");
            subQuery += (programaPresupuestario != "" && programaPresupuestario != null ? " AND cProgramaPresupuestario = '" + programaPresupuestario + "'" : "");
            subQuery += (partida != "" && partida != null ? " AND cPartida = '" + partida + "'" : "");
            subQuery += (tipoGasto != "" && tipoGasto != null ? " AND cTipoGasto = '" + tipoGasto + "'" : "");
            subQuery += (fuenteFinanciamiento != "" && fuenteFinanciamiento != null ? " AND cFuenteFinanciamiento = '" + fuenteFinanciamiento + "'" : "");
            subQuery += (entidadFederativa != "" && entidadFederativa != null ? " AND cEntidadFederativa = '" + entidadFederativa + "'" : "");
            subQuery += (cartera != "" && cartera != null ? " AND cCartera = '" + cartera + "'" : "");
            subQuery += (unidadEjecutora != "" && unidadEjecutora != null ? " AND cUnidadEjecutora = '" + unidadEjecutora + "'" : "");
            subQuery += (unidadNormativa != "" && unidadNormativa != null ? " AND cUnidadNorativa = '" + unidadNormativa + "'" : "");
            subQueryCLAVESIAFF = subQueryCLAVESIAFF + subQuery + ")";
            subQueryCLAVEINTERNA = subQueryCLAVEINTERNA + subQuery + ")";
            sqlAnteproyectoDetalleAut = "SELECT header.cRevisor as cRevisor" + ",header.cAutorizado AS cAutorizado" + ",detail.nFolioAnteProyectoAut AS nFolioAnteProyectoAut" + ",detail.nConsecutivo AS nConsecutivo" + ",detail.aEjercicioFiscal AS aEjercicioFiscal" + ",detail.cUnidadResponsable AS cUnidadResponsable" + ",detail.cUnidadEjecutora AS cUnidadEjecutora" + ",detail.cClaveSiaff AS cClaveSiaff" + ",detail.cClaveInterna AS cClaveInterna" + ",ISNULL(detail.mCalculado,'') AS mCalculado" + ",ISNULL(detail.mOptimo,'') AS mOptimo" + ",ISNULL(detail.mIreductible,'') AS mIreductible " + " FROM tAnteProyectoEncabezadoAut header with(nolock) , tAnteProyectoDetalleAut detail with(nolock)" + " WHERE header.nFolioAnteProyectoAut = detail.nFolioAnteProyectoAut" + " AND detail.CCLAVESIAFF IN " + subQueryCLAVESIAFF + " AND detail.CCLAVEINTERNA IN " + subQueryCLAVEINTERNA + " ORDER BY nFolioAnteProyectoAut, nConsecutivo ASC";
            pstmntSubQuery = conn.prepareStatement(sqlAnteproyectoDetalleAut);
            rsSubQuery = pstmntSubQuery.executeQuery();
            while (rsSubQuery.next()) {
                String revisor = rsSubQuery.getString("cRevisor");
                String autorizador = rsSubQuery.getString("cAutorizado");
                if ((" ").equals(revisor) || ("A").equals(revisor) || (" ").equals(autorizador) || ("A").equals(autorizador)) {
                    //FOLIOANTEPROYECTO||RENGLON||EJERCICIO FISCAL||UNIDAD RESPONSABLE||UNIDADEJECUTORA||CLAVESIAFF||CLAVEINTERNA||mCALCULADO||mOPTIMO||mIRREDUCTIBLE||
                    AnteProyectoAut anteproyAut = new AnteProyectoAut();
                    anteproyAut.setFolioAnteProyectoAut(rsSubQuery.getString("nFolioAnteProyectoAut"));
                    anteproyAut.setD_NConsecutivo(rsSubQuery.getString("nConsecutivo"));
                    if (cGroupBy.equals("aEjercicioFiscal_1") || cGroupBy.equals("cSubCuenta")) {
                        anteproyAut.setEjercicioFiscal(rsSubQuery.getString("aEjercicioFiscal"));
                    }
                    if (cGroupBy.equals("cUnidadResponsable_3") || cGroupBy.equals("cSubCuenta")) {
                        anteproyAut.setUnidadResponsableEP(rsSubQuery.getString("cUnidadResponsable"));
                    }
                    if (cGroupBy.equals("cUnidadResponsable_15") || cGroupBy.equals("cSubCuenta")) {
                        anteproyAut.setD_CUnidadEjecutora(rsSubQuery.getString("cUnidadEjecutora"));
                    }
                    if (cGroupBy.equals("cSubCuenta")) {
                        anteproyAut.setD_CClaveSIAFF(rsSubQuery.getString("cClaveSiaff"));
                    }
                    if (cGroupBy.equals("cSubCuenta")) {
                        anteproyAut.setD_CClaveInterna(rsSubQuery.getString("cClaveInterna"));
                    }
                    if (cuenta == "" || cuenta.contains("calculado")) {
                        anteproyAut.setD_MCalculado(rsSubQuery.getString("mCalculado"));
                    }
                    if (cuenta == "" || cuenta.contains("optimo")) {
                        anteproyAut.setD_MOptimo(rsSubQuery.getString("mOptimo"));
                    }
                    if (cuenta == "" || cuenta.contains("irreductible")) {
                        anteproyAut.setD_MIreductible(rsSubQuery.getString("mIreductible"));
                    }
                    if (cuenta == "" || cuenta.contains("calculado,optimo")) {
                        anteproyAut.setD_MCalculado(rsSubQuery.getString("mCalculado"));
                        anteproyAut.setD_MOptimo(rsSubQuery.getString("mOptimo"));
                    }
                    if (cuenta == "" || cuenta.contains("optimo,calculado")) {
                        anteproyAut.setD_MCalculado(rsSubQuery.getString("mCalculado"));
                        anteproyAut.setD_MOptimo(rsSubQuery.getString("mOptimo"));
                    }
                    if (cuenta == "" || cuenta.contains("calculado,irreductible")) {
                        anteproyAut.setD_MCalculado(rsSubQuery.getString("mCalculado"));
                        anteproyAut.setD_MIreductible(rsSubQuery.getString("mIreductible"));
                    }
                    if (cuenta == "" || cuenta.contains("irreductible,calculado")) {
                        anteproyAut.setD_MCalculado(rsSubQuery.getString("mCalculado"));
                        anteproyAut.setD_MIreductible(rsSubQuery.getString("mIreductible"));
                    }
                    if (cuenta == "" || cuenta.contains("optimo,irreductible")) {
                        anteproyAut.setD_MOptimo(rsSubQuery.getString("mOptimo"));
                        anteproyAut.setD_MIreductible(rsSubQuery.getString("mIreductible"));
                    }
                    if (cuenta == "" || cuenta.contains("irreductible,optimo")) {
                        anteproyAut.setD_MOptimo(rsSubQuery.getString("mOptimo"));
                        anteproyAut.setD_MIreductible(rsSubQuery.getString("mIreductible"));
                    }
                    if (cuenta == "" || cuenta.contains("calculado,optimo,irreductible")) {
                        anteproyAut.setD_MCalculado(rsSubQuery.getString("mCalculado"));
                        anteproyAut.setD_MOptimo(rsSubQuery.getString("mOptimo"));
                        anteproyAut.setD_MIreductible(rsSubQuery.getString("mIreductible"));
                    }
                    if (cuenta == "" || cuenta.contains("calculado,irreductible,optimo")) {
                        anteproyAut.setD_MCalculado(rsSubQuery.getString("mCalculado"));
                        anteproyAut.setD_MOptimo(rsSubQuery.getString("mOptimo"));
                        anteproyAut.setD_MIreductible(rsSubQuery.getString("mIreductible"));
                    }
                    if (cuenta == "" || cuenta.contains("optimo,calculado,irreductible")) {
                        anteproyAut.setD_MCalculado(rsSubQuery.getString("mCalculado"));
                        anteproyAut.setD_MOptimo(rsSubQuery.getString("mOptimo"));
                        anteproyAut.setD_MIreductible(rsSubQuery.getString("mIreductible"));
                    }
                    if (cuenta == "" || cuenta.contains("optimo,irreductible,calculado")) {
                        anteproyAut.setD_MCalculado(rsSubQuery.getString("mCalculado"));
                        anteproyAut.setD_MOptimo(rsSubQuery.getString("mOptimo"));
                        anteproyAut.setD_MIreductible(rsSubQuery.getString("mIreductible"));
                    }
                    if (cuenta == "" || cuenta.contains("irreductible,optimo,calculado")) {
                        anteproyAut.setD_MCalculado(rsSubQuery.getString("mCalculado"));
                        anteproyAut.setD_MOptimo(rsSubQuery.getString("mOptimo"));
                        anteproyAut.setD_MIreductible(rsSubQuery.getString("mIreductible"));
                    }
                    if (cuenta == "" || cuenta.contains("irreductible,calculado,optimo")) {
                        anteproyAut.setD_MCalculado(rsSubQuery.getString("mCalculado"));
                        anteproyAut.setD_MOptimo(rsSubQuery.getString("mOptimo"));
                        anteproyAut.setD_MIreductible(rsSubQuery.getString("mIreductible"));
                    }
                    listReg.add(anteproyAut);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rsSubQuery != null) {
                rsSubQuery.close();
            }
            if (pstmntSubQuery != null) {
                pstmntSubQuery.close();
            }
            rsSubQuery = null;
            pstmntSubQuery = null;
        }
        return listReg;
    }

    public static List<AnteProyectoAutCalendario> callReporteAnteproyectoCalendarizado(Connection conn, String ep, String ejercicioFiscal, String cOrderBy, String cGroupBy) throws SQLException {
        List<AnteProyectoAutCalendario> listReg = new ArrayList<AnteProyectoAutCalendario>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String sqlCLAVESIAFF = "";
        String sqlCLAVEINTERNA = "";
        String sqlEPAnteproyecto = "";
        String sqlAnteproyectoCalendario = "";
        try {
            sqlCLAVESIAFF = "(SELECT CLAVESIAFF FROM tCatalogoEPAnteproy WHERE aEjercicioFiscal = " + ejercicioFiscal + " ";
            sqlCLAVEINTERNA = "(SELECT CLAVEINTERNA FROM tCatalogoEPAnteproy WHERE aEjercicioFiscal = " + ejercicioFiscal + " ";
            sqlEPAnteproyecto += (ep != "" && ep != null ? " AND EP = '" + ep + "'" : "");
            sqlCLAVESIAFF = sqlCLAVESIAFF + sqlEPAnteproyecto + ") ";
            sqlCLAVEINTERNA = sqlCLAVEINTERNA + sqlEPAnteproyecto + ") ";
            sqlAnteproyectoCalendario = " SELECT nConsecutivo" + "		,nFolioAnteProyectoAut" + "		,aEjercicioFiscal" + "		,cUnidadResponsable" + "		,cClaveSiaff" + "		,cClaveInterna" + "		,cCentroContable" + "		,nFolioAnteProyecto" + "		,ISNULL(mAnualAutorizado,'') AS mAnualAutorizado" + "		,ISNULL(mEnero,'') AS mEnero" + "		,ISNULL(mFebrero,'') AS mFebrero" + "		,ISNULL(mMarzo,'') AS mMarzo" + "		,ISNULL(mAbril,'') AS mAbril" + "		,ISNULL(mMayo,'') AS mMayo" + "		,ISNULL(mJunio,'') AS mJunio" + "		,ISNULL(mJulio,'') AS mJulio" + "		,ISNULL(mAgosto,'') AS mAgosto" + "		,ISNULL(mSeptiembre,'') AS mSeptiembre" + "		,ISNULL(mOctubre,'') AS mOctubre" + "		,ISNULL(mNoviembre,'') AS mNoviembre" + "		,ISNULL(mDiciembre,'') AS mDiciembre" + " FROM tAnteProyectoAUTCalendario with(nolock)" + " WHERE cClaveSiaff = " + sqlCLAVESIAFF + " AND cClaveInterna = " + sqlCLAVEINTERNA + " ORDER BY nFolioAnteProyecto ASC";
            pstmnt = conn.prepareStatement(sqlAnteproyectoCalendario);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                /*
				 * FOLIO ANTEPROYECTO||EJERCICIO FISCAL||UNIDAD RESPONSABLE||CCLAVESIAFF||CLAVE INTERNA||CENTRO CONTABLE
				 * MONTO ANUAL AUTORIZADO||MONTO ENERO||MONTO FEBRERO||MONTO MARZO||MONTO ABRIL||MONTO MAYO||MONTO JUNIO
				 * MONTO JULIO||MONTO AGOSTO||MONTO SEPTIEMBRE||MONTO OCTUBRE||MONTO NOVIEMBRE||MONTO DICIEMBRE
				 */
                AnteProyectoAutCalendario anteproyAutCal = new AnteProyectoAutCalendario();
                anteproyAutCal.setNConsecutivo(rs.getString("nConsecutivo"));
                anteproyAutCal.setNFolioAnteProyectoAut(rs.getString("nFolioAnteProyectoAut"));
                if (cGroupBy.equals("aEjercicioFiscal_1") || cGroupBy.equals("cSubCuenta")) {
                    anteproyAutCal.setAEjercicioFiscal(rs.getString("aEjercicioFiscal"));
                }
                if (cGroupBy.equals("cUnidadResponsable_3") || cGroupBy.equals("cSubCuenta")) {
                    anteproyAutCal.setCUnidadResponsable(rs.getString("cUnidadResponsable"));
                }
                if (cGroupBy.equals("cSubCuenta")) {
                    anteproyAutCal.setCClaveSiaff(rs.getString("cClaveSiaff"));
                }
                if (cGroupBy.equals("cSubCuenta")) {
                    anteproyAutCal.setCClaveInterna(rs.getString("cClaveInterna"));
                }
                anteproyAutCal.setCCentroContable(rs.getString("cCentroContable"));
                anteproyAutCal.setNFolioAnteProyecto(rs.getString("nFolioAnteProyecto"));
                anteproyAutCal.setMAnualAutorizado(rs.getString("mAnualAutorizado"));
                anteproyAutCal.setMEnero(rs.getString("mEnero"));
                anteproyAutCal.setMFebrero(rs.getString("mFebrero"));
                anteproyAutCal.setMMarzo(rs.getString("mMarzo"));
                anteproyAutCal.setMAbril(rs.getString("mAbril"));
                anteproyAutCal.setMMayo(rs.getString("mMayo"));
                anteproyAutCal.setMJunio(rs.getString("mJunio"));
                anteproyAutCal.setMJulio(rs.getString("mJulio"));
                anteproyAutCal.setMAgosto(rs.getString("mAgosto"));
                anteproyAutCal.setMSeptiembre(rs.getString("mSeptiembre"));
                anteproyAutCal.setMOctubre(rs.getString("mOctubre"));
                anteproyAutCal.setMNoviembre(rs.getString("mNoviembre"));
                anteproyAutCal.setMDiciembre(rs.getString("mDiciembre"));
                listReg.add(anteproyAutCal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmnt != null) {
                pstmnt.close();
            }
            rs = null;
            pstmnt = null;
        }
        return listReg;
    }

    public static List<AnteProyectoAutCalendario> callReporteAnteproyectoCalendarizadoParametros(Connection conn, String ejercicioFiscal, String ramoEP, String unidadResponsableEP, String grupoFuncional, String funcion, String subFuncion, String programaGeneral, String programaPresupuestario, String actividadInstitucional, String partida, String tipoGasto, String fuenteFinanciamiento, String entidadFederativa, String cartera, String unidadEjecutora, String unidadNormativa, String cOrderBy, String cGroupBy, String usuario, String filtro) throws SQLException {
        List<AnteProyectoAutCalendario> listReg = new ArrayList<AnteProyectoAutCalendario>();
        PreparedStatement pstmntSubQuery = null;
        ResultSet rsSubQuery = null;
        String subQueryCLAVESIAFF = "";
        String subQueryCLAVEINTERNA = "";
        String subQuery = "";
        String sqlAnteproyectoCal = "";
        try {
            subQueryCLAVESIAFF = "(SELECT CLAVESIAFF FROM tCatalogoEPAnteproy WHERE aEjercicioFiscal = " + ejercicioFiscal;
            subQueryCLAVEINTERNA = "(SELECT CLAVEINTERNA FROM tCatalogoEPAnteproy WHERE aEjercicioFiscal = " + ejercicioFiscal;
            subQuery += (ramoEP != "" && ramoEP != null ? " AND cRamoEP = '" + ramoEP + "'" : "");
            subQuery += (unidadResponsableEP != "" && unidadResponsableEP != null ? " AND cUnidadResponsableEP = '" + unidadResponsableEP + "'" : "");
            subQuery += (grupoFuncional != "" && grupoFuncional != null ? " AND cGrupoFuncional = '" + grupoFuncional + "'" : "");
            subQuery += (funcion != "" && funcion != null ? " AND cFuncion = '" + funcion + "'" : "");
            subQuery += (subFuncion != "" && subFuncion != null ? " AND cSubFuncion = '" + subFuncion + "'" : "");
            subQuery += (programaGeneral != "" && programaGeneral != null ? " AND cProgramaGeneral = '" + programaGeneral + "'" : "");
            subQuery += (actividadInstitucional != "" && actividadInstitucional != null ? " AND cActividadInstitucional = '" + actividadInstitucional + "'" : "");
            subQuery += (programaPresupuestario != "" && programaPresupuestario != null ? " AND cProgramaPresupuestario = '" + programaPresupuestario + "'" : "");
            subQuery += (partida != "" && partida != null ? " AND cPartida = '" + partida + "'" : "");
            subQuery += (tipoGasto != "" && tipoGasto != null ? " AND cTipoGasto = '" + tipoGasto + "'" : "");
            subQuery += (fuenteFinanciamiento != "" && fuenteFinanciamiento != null ? " AND cFuenteFinanciamiento = '" + fuenteFinanciamiento + "'" : "");
            subQuery += (entidadFederativa != "" && entidadFederativa != null ? " AND cEntidadFederativa = '" + entidadFederativa + "'" : "");
            subQuery += (cartera != "" && cartera != null ? " AND cCartera = '" + cartera + "'" : "");
            subQuery += (unidadEjecutora != "" && unidadEjecutora != null ? " AND cUnidadEjecutora = '" + unidadEjecutora + "'" : "");
            subQuery += (unidadNormativa != "" && unidadNormativa != null ? " AND cUnidadNorativa = '" + unidadNormativa + "'" : "");
            subQueryCLAVESIAFF = subQueryCLAVESIAFF + subQuery + ")";
            subQueryCLAVEINTERNA = subQueryCLAVEINTERNA + subQuery + ")";
            sqlAnteproyectoCal = "SELECT nConsecutivo" + "		,nFolioAnteProyectoAut" + "		,aEjercicioFiscal" + "		,cUnidadResponsable" + "		,cClaveSiaff" + "		,cClaveInterna" + "		,cCentroContable" + "		,nFolioAnteProyecto" + "		,ISNULL(mAnualAutorizado,'') AS mAnualAutorizado" + "		,ISNULL(mEnero,'') AS mEnero" + "		,ISNULL(mFebrero,'') AS mFebrero" + "		,ISNULL(mMarzo,'') AS mMarzo" + "		,ISNULL(mAbril,'') AS mAbril" + "		,ISNULL(mMayo,'') AS mMayo" + "		,ISNULL(mJunio,'') AS mJunio" + "		,ISNULL(mJulio,'') AS mJulio" + "		,ISNULL(mAgosto,'') AS mAgosto" + "		,ISNULL(mSeptiembre,'') AS mSeptiembre" + "		,ISNULL(mOctubre,'') AS mOctubre" + "		,ISNULL(mNoviembre,'') AS mNoviembre" + "		,ISNULL(mDiciembre,'') AS mDiciembre" + " FROM tAnteProyectoAUTCalendario with(nolock)" + " WHERE cClaveSiaff IN " + subQueryCLAVESIAFF + " AND cClaveInterna IN " + subQueryCLAVEINTERNA + " ORDER BY nFolioAnteProyecto ASC";
            pstmntSubQuery = conn.prepareStatement(sqlAnteproyectoCal);
            rsSubQuery = pstmntSubQuery.executeQuery();
            while (rsSubQuery.next()) {
                /*
				 * FOLIO ANTEPROYECTO||EJERCICIO FISCAL||UNIDAD RESPONSABLE||CCLAVESIAFF||CLAVE INTERNA||CENTRO CONTABLE
				 * MONTO ANUAL AUTORIZADO||MONTO ENERO||MONTO FEBRERO||MONTO MARZO||MONTO ABRIL||MONTO MAYO||MONTO JUNIO
				 * MONTO JULIO||MONTO AGOSTO||MONTO SEPTIEMBRE||MONTO OCTUBRE||MONTO NOVIEMBRE||MONTO DICIEMBRE
				 */
                AnteProyectoAutCalendario anteproyAutCal = new AnteProyectoAutCalendario();
                anteproyAutCal.setNFolioAnteProyectoAut(rsSubQuery.getString("nFolioAnteProyectoAut"));
                anteproyAutCal.setNConsecutivo(rsSubQuery.getString("nConsecutivo"));
                if (cGroupBy.equals("aEjercicioFiscal_1") || cGroupBy.equals("cSubCuenta")) {
                    anteproyAutCal.setAEjercicioFiscal(rsSubQuery.getString("aEjercicioFiscal"));
                }
                if (cGroupBy.equals("cUnidadResponsable_3") || cGroupBy.equals("cSubCuenta")) {
                    anteproyAutCal.setCUnidadResponsable(rsSubQuery.getString("cUnidadResponsable"));
                }
                if (cGroupBy.equals("cSubCuenta")) {
                    anteproyAutCal.setCClaveSiaff(rsSubQuery.getString("cClaveSiaff"));
                }
                if (cGroupBy.equals("cSubCuenta")) {
                    anteproyAutCal.setCClaveInterna(rsSubQuery.getString("cClaveInterna"));
                }
                anteproyAutCal.setCCentroContable(rsSubQuery.getString("cCentroContable"));
                anteproyAutCal.setNFolioAnteProyecto(rsSubQuery.getString("nFolioAnteProyecto"));
                anteproyAutCal.setMAnualAutorizado(rsSubQuery.getString("mAnualAutorizado"));
                anteproyAutCal.setMEnero(rsSubQuery.getString("mEnero"));
                anteproyAutCal.setMFebrero(rsSubQuery.getString("mFebrero"));
                anteproyAutCal.setMMarzo(rsSubQuery.getString("mMarzo"));
                anteproyAutCal.setMAbril(rsSubQuery.getString("mAbril"));
                anteproyAutCal.setMMayo(rsSubQuery.getString("mMayo"));
                anteproyAutCal.setMJunio(rsSubQuery.getString("mJunio"));
                anteproyAutCal.setMJulio(rsSubQuery.getString("mJulio"));
                anteproyAutCal.setMAgosto(rsSubQuery.getString("mAgosto"));
                anteproyAutCal.setMSeptiembre(rsSubQuery.getString("mSeptiembre"));
                anteproyAutCal.setMOctubre(rsSubQuery.getString("mOctubre"));
                anteproyAutCal.setMNoviembre(rsSubQuery.getString("mNoviembre"));
                anteproyAutCal.setMDiciembre(rsSubQuery.getString("mDiciembre"));
                listReg.add(anteproyAutCal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rsSubQuery != null) {
                rsSubQuery.close();
            }
            if (pstmntSubQuery != null) {
                pstmntSubQuery.close();
            }
            rsSubQuery = null;
            pstmntSubQuery = null;
        }
        return listReg;
    }

    public static String obtieneUR(Connection conn, String usuario) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String mensaje = "";
        String query = "SELECT cUnidadResponsable FROM dbo.CG_CAT_EMPLEADO AS emp WITH (NOLOCK) JOIN dbo.tCatalogoUnidadResponsable AS ur WITH (NOLOCK) ON emp.ID_AREA = ur.ID_AREA WHERE CE_OS_RESPONSABLE = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, usuario);
            rs = ps.executeQuery();
            while (rs.next()) {
                mensaje = rs.getString(1);
            }
            return mensaje;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static String obtieneVistas(Connection conn, String usuario, String Modulo) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String unidades = "";
        String URs = "";
        //String role = "SELECT COUNT(R_NOMBRE) FROM dbo.CG_USUARIO_ROLE WHERE R_NOMBRE IN ('ADMIN','ADMIN_RECMAT') AND U_LOGIN = ?";
        String query = "SELECT DISTINCT ur FROM dbo.tVistasUR WITH (NOLOCK) WHERE usuario = ? AND modulo = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, usuario);
            ps.setString(2, Modulo);
            rs = ps.executeQuery();
            while (rs.next()) {
                unidades += "'" + rs.getString(1) + "',";
            }
            URs = unidades.substring(1, unidades.length() - 2);
            return URs;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static int obtieneRole(Connection conn, String usuario) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int role = 0;
        String query = "SELECT COUNT(R_NOMBRE) FROM dbo.CG_USUARIO_ROLE WHERE R_NOMBRE IN ('ADMIN','ADMIN_RECMAT') AND U_LOGIN = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, usuario);
            rs = ps.executeQuery();
            while (rs.next()) {
                role = rs.getInt(1);
            }
            //URs = unidades.substring(1, unidades.length()-2);
            return role;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static int obtieneGrupo(Connection conn, String usuario) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int grupo = 0;
        String query = "SELECT COUNT(*) FROM CG_USUARIO_GRUPO WHERE U_LOGIN = ? AND G_NOMBRE = 'JEFATURA_ADECUACIONES'";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, usuario);
            rs = ps.executeQuery();
            while (rs.next()) {
                grupo = rs.getInt(1);
            }
            //URs = unidades.substring(1, unidades.length()-2);
            return grupo;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }
}
