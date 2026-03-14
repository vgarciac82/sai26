package com.syc.gestion.reportes;

//
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
//import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
//import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
//import com.syc.reportes.core.ReporteAcreedoresDeudoresManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import net.sf.jasperreports.engine.JasperRunManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class reportesBussinesObject extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(reportesBussinesObject.class);

    // parametro glonales
    // ingreso
    double monto8151;

    double monto43;

    double monto42;

    double monto31156;

    double monto311234;

    double total2;

    double total3;

    double total4;

    // egreso
    double monto8251P;

    double monto1241P;

    double monto1242P;

    double monto1243P;

    double monto1244P;

    double monto1245P;

    double monto1246P;

    double monto1248P;

    double monto123P;

    double monto125P;

    double monto1236P;

    double monto1213P;

    double monto11P;

    double monto551C;

    double monto1151C;

    double monto1161C;

    double monto5C;

    public reportesBussinesObject(String jniName) {
        super.init(jniName);
    }

    /**
     * Imprime el reporte de la adecuacion.
     *
     * @param request
     * @param response
     * @param reportPath
     *            Path al archivo JASPER
     * @param imgPath
     *            Path a la imagen del reporte
     * @throws Exception
     */
    public void reporteImprimeAdecuacionPDF(HttpServletRequest request, HttpServletResponse response, String reportPath, String imgPath) throws Exception {
        log.debug("Object: {}", "Imprimiendo reporte de adecuacion REPORT_PATH[" + reportPath + "] IMAGE_PATH[" + imgPath + "]");
        long init = System.currentTimeMillis();
        Connection conn = null;
        Map<String, Object> parms = null;
        InputStream reportStream = null;
        ServletOutputStream out = null;
        try {
            conn = getConnection();
            String nFolio = request.getParameter("adecFolio");
            String fileName = "Adecuacion" + nFolio + ".pdf";
            parms = new LinkedHashMap<String, Object>();
            parms.put("folio", Integer.parseInt(nFolio, 10));
            parms.put("SUBREPORT_DIR", imgPath);
            reportStream = new FileInputStream(reportPath);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
            out = response.getOutputStream();
            JasperRunManager.runReportToPdfStream(reportStream, out, parms, conn);
            out.flush();
            out.close();
            reportStream.close();
            log.info("Object: {}", "Reporte de adecuacion terminado en " + (System.currentTimeMillis() - init) / 100 + " segundos ");
        } finally {
            CloseObject.closeObject(conn, false);
            parms = null;
            out = null;
            reportStream = null;
        }
    }

    public void reporteChequeManual(HttpServletRequest req, HttpServletResponse resp, String strReport, String ruta, String CXP, String tipo, String Usuario, String BeneficiarioNuevo) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            Map<String, Object> parms = new LinkedHashMap<String, Object>();
            reportes objReporte = new reportes();
            parms.put("CXP", CXP);
            parms.put("tipo", tipo);
            parms.put("usuario", Usuario);
            parms.put("beneficiario_temp", BeneficiarioNuevo);
            objReporte.execute(conn, req, resp, strReport, parms);
            conn.commit();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
    }

    public void reporteConciliacion(HttpServletRequest req, HttpServletResponse resp, String strReport, String reportName, String cEsFiel) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            String folio = req.getParameter("nConciliacion");
            String rutaCompleta = strReport + "\\" + reportName;
            Map<String, Object> parms = new LinkedHashMap<String, Object>();
            reportes objReporte = new reportes();
            parms.put("nConciliacion", folio);
            parms.put("SUBREPORT_DIR", strReport);
            if (cEsFiel.equalsIgnoreCase("S")) {
                //				objReporte.executeConciliaFIEL( conn, strReport, reportName, parms );
            } else {
                objReporte.execute(conn, req, resp, rutaCompleta, parms);
            }
            conn.commit();
        } finally {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (SQLException exc) {
                    log.warn("Object: {}", "Realizando rollback: " + exc);
                }
            CloseObject.closeObject(conn);
        }
    }

    public void reporteEstadosFinancieros(HttpServletRequest req, HttpServletResponse resp, String strReport, String reportName, String subreporte, String reporteDetalle, boolean firmaElectronica) throws Exception {
        Connection conn = null;
        try {
            Integer numRep = Integer.parseInt(req.getParameter("nReporte"), 10);
            String moneda = req.getParameter("Moneda");
            String nivel = req.getParameter("Formato");
            String numfirmas = (req.getParameter("chk_firmas") == null ? "3" : req.getParameter("chk_firmas"));
            String fecha = req.getParameter("Fecha");
            String centroContable = req.getParameter("cCentroContable");
            String comentarios = new String(req.getParameter("comentarios").getBytes("ISO-8859-1"), "UTF-8");
            String caracteristicas = new String(req.getParameter("caracteristicas").getBytes("ISO-8859-1"), "UTF-8");
            String descripcionContable = req.getParameter("cdescripcion");
            String nombre1 = new String(req.getParameter("nombre1").getBytes("ISO-8859-1"), "UTF-8");
            String puesto1 = new String(req.getParameter("cargo1").getBytes("ISO-8859-1"), "UTF-8");
            String nombre2 = new String(req.getParameter("nombre2").getBytes("ISO-8859-1"), "UTF-8");
            String puesto2 = new String(req.getParameter("cargo2").getBytes("ISO-8859-1"), "UTF-8");
            String nombre3 = new String(req.getParameter("nombre3").getBytes("ISO-8859-1"), "UTF-8");
            String puesto3 = new String(req.getParameter("cargo3").getBytes("ISO-8859-1"), "UTF-8");
            String nombre4 = new String(req.getParameter("nombre4").getBytes("ISO-8859-1"), "UTF-8");
            String puesto4 = new String(req.getParameter("cargo4").getBytes("ISO-8859-1"), "UTF-8");
            String[] numeroEmpleado = new String[] { req.getParameter("numEmpleado1"), req.getParameter("numEmpleado2"), req.getParameter("numEmpleado3"), req.getParameter("numEmpleado4") };
            String nombreCompleto = strReport + "\\" + reportName;
            String cEsConac = req.getParameter("esConac");
            String mesAA = req.getParameter("esMesAA");
            conn = getConnection();
            Map<String, Object> parms = new LinkedHashMap<String, Object>();
            reportes objReporte = new reportes();
            parms.put("mes", Integer.parseInt(fecha.substring(3, 5), 10));
            parms.put("anio", Integer.parseInt(fecha.substring(6), 10));
            parms.put("miles", Integer.parseInt(moneda, 10));
            parms.put("centroContable", centroContable);
            parms.put("nombreCentro", descripcionContable);
            if (numRep == 2 || numRep == 3 || numRep == 4 || numRep == 5 || numRep == 6 || numRep == 8 || numRep == 9)
                parms.put("esConac", cEsConac);
            if (numRep == 2 || numRep == 3 || numRep == 5 || numRep == 9)
                parms.put("nivel", Integer.parseInt(nivel, 10));
            if (numRep == 2 || numRep == 3 || numRep == 9 || numRep == 8)
                parms.put("esMesAA", mesAA);
            if (numRep == 10) {
                parms.put("comentarios", comentarios);
                parms.put("caracteristicas", caracteristicas);
                parms.put("SUBREPORT_DIR", subreporte);
                parms.put("SUBREPORT_DIR", reporteDetalle);
            }
            if (numRep == 11) {
                datosConcIng(conn, Integer.parseInt(fecha.substring(3, 5), 10), Integer.parseInt(fecha.substring(6), 10), Integer.parseInt(moneda, 10));
                parms.put("monto8151", monto8151);
                parms.put("monto43", monto43);
                parms.put("monto42", monto42);
                parms.put("monto31156", monto31156);
                parms.put("monto311234", monto311234);
                parms.put("total2", total2);
                parms.put("total3", total3);
                parms.put("total4", total4);
            }
            parms.put("nfirmas", Integer.parseInt(numfirmas, 10));
            parms.put("nombre1", nombre1);
            parms.put("puesto1", puesto1);
            parms.put("nombre2", nombre2);
            parms.put("puesto2", puesto2);
            parms.put("nombre3", nombre3);
            parms.put("puesto3", puesto3);
            parms.put("nombre4", nombre4);
            parms.put("puesto4", puesto4);
            System.out.println(strReport);
            parms.put("SUBREPORT_DIR", strReport);
            if (firmaElectronica)
                objReporte.executeFIEL(conn, strReport, reportName, parms, numeroEmpleado, numRep);
            else
                objReporte.execute(conn, req, resp, nombreCompleto, parms);
            conn.commit();
        } finally {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (SQLException exc) {
                    log.warn("Object: {}", "Realizando rollback: " + exc);
                }
            CloseObject.closeObject(conn);
        }
    }

    private void datosConcIng(Connection conn, int mes, int anio, int miles) throws Exception {
        CallableStatement ctm = null;
        ResultSet rs = null;
        String cQueryD = "{call dbo.sp_conciliacion_ING (?, ?, ?)}";
        String cuenta = "";
        double monto;
        try {
            ctm = conn.prepareCall(cQueryD);
            ctm.setInt(1, mes);
            ctm.setInt(2, anio);
            ctm.setInt(3, miles);
            rs = ctm.executeQuery();
            while (rs.next()) {
                cuenta = rs.getString("cuenta");
                monto = rs.getDouble("monto");
                if (cuenta.equals("8151"))
                    monto8151 = monto;
                if (cuenta.equals("43"))
                    monto43 = monto;
                if (cuenta.equals("42"))
                    monto42 = monto;
                if (cuenta.equals("31131P56"))
                    monto31156 = monto;
                if (cuenta.equals("31131P234"))
                    monto311234 = monto;
                if (cuenta.equals("total2"))
                    total2 = monto;
                if (cuenta.equals("total3"))
                    total3 = monto;
                if (cuenta.equals("total4"))
                    total4 = monto;
            }
            //Ultimos cambios se suman todos los ingresos en no contables
            monto311234 = monto311234 + monto31156;
            monto31156 = 0;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ctm, false);
        }
        // TODO Auto-generated method stub
    }

    /*
	private void datosConcEgr( Connection conn, int mes, int anio, int miles ) throws Exception {
		CallableStatement ctm = null;
		ResultSet rs = null;
		String cQueryD = "{call dbo.sp_conciliacion_EG (?, ?, ?)}";
		String cuenta = "";
		double monto;

		try {
			ctm = conn.prepareCall( cQueryD );
			ctm.setInt( 1, mes );
			ctm.setInt( 2, anio );
			ctm.setInt( 3, miles );
			rs = ctm.executeQuery();

			while ( rs.next() ) {
				cuenta = rs.getString( "cuenta" );
				monto = rs.getDouble( "monto" );

				if ( cuenta.equals( "8251P" ) )
					monto8251P = monto;
				if ( cuenta.equals( "1241P" ) )
					monto1241P = monto;
				if ( cuenta.equals( "1242P" ) )
					monto1242P = monto;
				if ( cuenta.equals( "1243P" ) )
					monto1243P = monto;
				if ( cuenta.equals( "1244P" ) )
					monto1244P = monto;
				if ( cuenta.equals( "1245P" ) )
					monto1245P = monto;
				if ( cuenta.equals( "1246P" ) )
					monto1246P = monto;
				if ( cuenta.equals( "1248P" ) )
					monto1248P = monto;
				if ( cuenta.equals( "123P" ) )
					monto123P = monto;
				if ( cuenta.equals( "125P" ) )
					monto125P = monto;
				if ( cuenta.equals( "1236P" ) )
					monto1236P = monto;
				if ( cuenta.equals( "1213P" ) )
					monto1213P = monto;
				if ( cuenta.equals( "11P" ) )
					monto11P = monto;
				if ( cuenta.equals( "551C" ) )
					monto551C = monto;
				if ( cuenta.equals( "1151C" ) )
					monto1151C = monto;
				if ( cuenta.equals( "1161C" ) )
					monto1161C = monto;
				if ( cuenta.equals( "5C" ) )
					monto5C = monto;
				if ( cuenta.equals( "total2" ) )
					total2 = monto;
				if ( cuenta.equals( "total3" ) )
					total3 = monto;
				if ( cuenta.equals( "total4" ) )
					total4 = monto;
				
				
				
			}

		} finally {
			CloseObject.closeObject( rs, false );
			CloseObject.closeObject( ctm, false );
		}
	}
*/
    public void reporte1(HttpServletRequest req, HttpServletResponse resp, String strReport) {
        Connection conn = null;
        try {
            conn = getConnection();
            Map<String, String> parms = new HashMap<>();
            reportes objReporte = new reportes();
            // Parametros del reporte
            // p_area_rem == AND REMITENTE_AREA = '16016300'
            // if (!req.getParameter("situacion").equals(""))
            parms.put("p_area_rem", " AND REMITENTE_AREA = '" + req.getParameter("txtDirigidoA") + "' ");
            objReporte.execute(conn, req, resp, strReport, parms);
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                // log.warn("Error en rollback", ex);
            }
            log.error("Actualizando caso", exc);
            // throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
    }

    public void reporte2(HttpServletRequest req, HttpServletResponse resp, String strReport) {
        Connection conn = null;
        try {
            conn = getConnection();
            Map<String, String> parms = new HashMap<>();
            reportes objReporte = new reportes();
            // Parametros del reporte
            // p_area_rem == AND REMITENTE_AREA = '16016300'
            // if (!req.getParameter("situacion").equals("")).
            java.util.Enumeration en = req.getParameterNames();
            while (en.hasMoreElements()) {
                String parmName = (String) en.nextElement();
                System.out.println("Contiene el parametro=[" + parmName + "]");
            }
            String f_ini = req.getParameter("f_ini");
            String f_fin = req.getParameter("f_fin");
            parms.put("p_area_rem", " AND REMITENTE_AREA = '" + req.getParameter("txtDirigidoA") + "' ");
            parms.put("p_date_init", "'" + f_ini + "' ");
            parms.put("p_date_final", "'" + f_fin + "' ");
            objReporte.execute(conn, req, resp, strReport, parms);
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (Exception ex) {
                // log.warn("Error en rollback", ex);
            }
            log.error("Actualizando caso", exc);
            // throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
    }

    public void reporte3(HttpServletRequest req, HttpServletResponse resp, String strReport) {
        Connection conn = null;
        try {
            conn = getConnection();
            Map<String, String> parms = new HashMap<>();
            reportes objReporte = new reportes();
            // Parametros del reporte
            // p_area_rem == AND REMITENTE_AREA = '16016300'
            // if (!req.getParameter("situacion").equals(""))
            parms.put("p_area_resp", " AND RESPONSABLE_AREA = '" + req.getParameter("txtDirigidoA") + "' ");
            parms.put("p_date_ini", "'" + req.getParameter("f_ini") + "' ");
            parms.put("p_date_fin", "'" + req.getParameter("f_fin") + "' ");
            objReporte.execute(conn, req, resp, strReport, parms);
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (Exception ex) {
                // log.warn("Error en rollback", ex);
            }
            log.error("Actualizando caso", exc);
            // throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
    }

    public void reporte4(HttpServletRequest req, HttpServletResponse resp, String strReport) {
        Connection conn = null;
        try {
            conn = getConnection();
            Map<String, String> parms = new HashMap<>();
            reportes objReporte = new reportes();
            // Parametros del reporte
            // p_area_rem == AND REMITENTE_AREA = '16016300'
            // if (!req.getParameter("situacion").equals(""))
            parms.put("p_area_rem", " AND V2.REMITENTE_AREA = '" + req.getParameter("txtDirigidoA") + "' ");
            objReporte.execute(conn, req, resp, strReport, parms);
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (Exception ex) {
                // log.warn("Error en rollback", ex);
            }
            log.error("Actualizando caso", exc);
            // throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
    }

    public void reporteMovimientosRectificacion(HttpServletRequest req, HttpServletResponse resp, String strReport, String u_nombre) {
        Connection conn = null;
        try {
            conn = getConnection();
            Map<String, String> parms = new HashMap<>();
            reportes objReporte = new reportes();
            // Parametros del reporte
            String folio_rectificacion = req.getParameter("folio_rectificacion");
            String nfolio = req.getParameter("nfolio");
            // if (!req.getParameter("situacion").equals(""))
            parms.put("folio_rectificacion", folio_rectificacion);
            parms.put("nfolio", nfolio);
            parms.put("u_nombre", u_nombre);
            objReporte.execute(conn, req, resp, strReport, parms);
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.warn("Error en rollback", ex);
            }
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
    }

    public void reporteMovimientosReintegro(HttpServletRequest req, HttpServletResponse resp, String strReport, String u_nombre) {
        Connection conn = null;
        try {
            conn = getConnection();
            Map<String, String> parms = new HashMap<>();
            reportes objReporte = new reportes();
            // Parametros del reporte
            String folio_reintegro = req.getParameter("folio_reintegro");
            String nfolio = req.getParameter("nfolio");
            // if (!req.getParameter("situacion").equals(""))
            parms.put("folio_reintegro", folio_reintegro);
            parms.put("nfolio", nfolio);
            parms.put("u_nombre", u_nombre);
            objReporte.execute(conn, req, resp, strReport, parms);
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.warn("Error en rollback", ex);
            }
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
    }

    public void reporteIntegraAdecuaPdf(HttpServletRequest req, HttpServletResponse resp, String strReport) {
        Connection conn = null;
        try {
            conn = getConnection();
            Map parms = new HashMap<>();
            reportes objReporte = new reportes();
            // Parametros del reporte
            int nFolioIntegracion = new Integer(req.getParameter("nFolioIntegracion").substring(req.getParameter("nFolioIntegracion").lastIndexOf('-') + 1)).intValue();
            // String nFolioIntegracion=req.getParameter("nFolioIntegracion");
            String nConsecutivoSICOP = req.getParameter("nConsecutivoSICOP");
            parms.put("nFolioIntegracion", nFolioIntegracion);
            parms.put("nConsecutivoSICOP", nConsecutivoSICOP);
            objReporte.execute(conn, req, resp, strReport, parms);
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.warn("Error en rollback", ex);
            }
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
    }

    public void reporteIntegraAdecuaFIAFPdf(HttpServletRequest req, HttpServletResponse resp, String strReport) {
        Connection conn = null;
        try {
            conn = getConnection();
            Map<String, Integer> parms = new HashMap<>();
            reportes objReporte = new reportes();
            // Parametros del reporte
            int nFolioFIAF = new Integer(req.getParameter("nFolioFIAF").substring(req.getParameter("nFolioFIAF").lastIndexOf('-') + 1)).intValue();
            parms.put("nFolioFIAF", nFolioFIAF);
            objReporte.execute(conn, req, resp, strReport, parms);
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.warn("Error en rollback", ex);
            }
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
    }

    public void generaExcel(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantillas) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String fecha = req.getParameter("Fecha");
            // String fechaFin = req.getParameter("fecha_fin");
            String centroContable = req.getParameter("cCentroContable");
            String tipoCedula = req.getParameter("reporteTipo");
            String moneda = req.getParameter("Moneda");
            String nivel = req.getParameter("Formato");
            String comentarios = new String(req.getParameter("comentarios").getBytes("ISO-8859-1"), "UTF-8");
            String caracteristicas = new String(req.getParameter("caracteristicas").getBytes("ISO-8859-1"), "UTF-8");
            String desagregado = req.getParameter("desagregada");
            String cEsConac = req.getParameter("esConac");
            String esMesAA = req.getParameter("esMesAA");
            file = reportes.EstadosFinancierosExcel(conn, fecha, centroContable, tipoCedula, moneda, nivel, comentarios, caracteristicas, plantillas, desagregado, cEsConac, esMesAA);
            File f = new File(file);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
        } finally {
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
    }

    public String validaReporte(HttpServletRequest req, HttpServletResponse resp) {
        // Connection conn = null;
        String file = null;
        try {
            // conn = getConnection();
            String fecha = req.getParameter("Fecha");
            String reporte = req.getParameter("reporteTipo");
            String[] nomRep = reporte.split("[.]");
            String sDate1 = "30/11/2018";
            try {
                Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
                Date date2 = new SimpleDateFormat("dd/MM/yyyy").parse(fecha);
                if (date2.compareTo(date1) > 0) {
                    reporte = nomRep[0] + "2." + nomRep[1];
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return reporte;
        } finally {
            // CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
    }

    public String validaReporteFiel(HttpServletRequest req, HttpServletResponse resp) {
        // Connection conn = null;
        String file = null;
        try {
            // conn = getConnection();
            String reporte = req.getParameter("reporteTipo");
            String[] nomRep = reporte.split("[.]");
            try {
                reporte = nomRep[0] + "_fiel." + nomRep[1];
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return reporte;
        } finally {
            // CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
    }
}
