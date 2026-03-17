package com.syc.contable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.contable.core.Retencion;
import com.syc.contable.core.RetencionDetalle;
import com.syc.contable.core.RetencionEncabezado;
import com.syc.contable.core.RetencionManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.Caso;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

/**
 * @author Martha Aurora Sánchez Valdivieso para SYC Constructores de Sistemas
 *         SA de CV desarrollo gestion_conagua_sif México D.F. 19/07/2012
 */
public class RetencionBusinessLogic extends DataSourceManager {

    public Retencion retencion = null;

    private static Logger log = LoggerFactory.getLogger(ReintegrosBusinessLogic.class);

    public RetencionBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public Retencion filtrar(String CXP, String esIP) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            if (esIP.equals("N"))
                retencion = RetencionManager.filtraFolioSAI(conn, CXP);
            else
                retencion = RetencionManager.filtraFolioIP(conn, CXP);
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                throw new Exception("No tiene conexion");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            CloseObject.closeObject(conn);
        }
        return retencion;
    }

    public boolean insertarRetencion(RetencionEncabezado encabezado, List<RetencionDetalle> detalle) {
        Connection conn = null;
        boolean inserto = false;
        try {
            conn = getConnection();
            RetencionManager.insertarEncabezado(conn, encabezado);
            RetencionManager.insertarDetalle(conn, detalle);
            conn.commit();
            inserto = true;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
        } finally {
            CloseObject.closeObject(conn);
        }
        return inserto;
    }

    public String actualizarEvento(String cxp) throws Exception {
        Connection conn = null;
        String evento = "";
        try {
            conn = getConnection();
            cxp = cxp.trim();
            evento = RetencionManager.actualizaEvento(conn, cxp);
            if ("".equals(evento)) {
                throw new Exception("No hay evento para ese tipo de Retencion");
            }
            return evento;
        } catch (Exception exc) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
            throw exc;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public String aplicaRetencion(Caso c, String fApl) throws Exception {
        String mensaje = "";
        String folio = "";
        AccountingEngine ae = new AccountingEngine();
        Connection conn = null;
        String validaMes = "";
        try {
            conn = getConnection();
            validaMes = RetencionManager.validaMes(conn, c, fApl);
            if ("S".equals(validaMes)) {
                folio = c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1);
                //Aplicación contable de la retencion
                log.debug("Object: " + String.valueOf("Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()) + " Para  Folio:" + folio));
                ae.makeAccountingApplication(conn, "RETENCION", folio, "tRetencionEncabezado", "tRetencionDetalle", "nFolioRetencion");
                log.debug("Object: " + String.valueOf("Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()) + " Para  Folio:" + folio));
                //ARLA SE QUITA LA APLICACION DEL PASIVO DIFERIDO, SE APLICARA EN LA AJENA
                /*PasivoDiferidoManager.aplicarPasivoDiferido( conn, "RETENCION", folio, "tRetencionEncabezado", "tRetencionDetalle", "nfolioRetencion");
				log.debug( "Termina Aplicacion pasivo Diferido del folio " + folio );*/
                conn.commit();
            } else {
                mensaje = "El mes contable esta cerrado. Notifique al administrador";
                throw new Exception(mensaje);
            }
        } catch (Exception exc) {
            mensaje = exc.getMessage();
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.warn("En Rollback", ex);
            }
            throw exc;
        } finally {
            CloseObject.closeObject(conn);
        }
        return mensaje;
    }

    public String cancelarAppContableNuevo(Caso c, String cFecha) throws Exception {
        String retVal = null;
        Connection conn = null;
        AccountingEngine ae = new AccountingEngine();
        try {
            conn = getConnection();
            String folio = c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1);
            ae.cancelAccountingApplication(conn, "RETENCION", folio, "tRetencionEncabezado", "tRetencionDetalle", "nFolioRetencion");
            log.debug("Se cancelo la retencion correctamente");
            /*
			Caso cReloaded = new Caso();
			cReloaded.setIdCaso( c.getIdCaso() );
			cReloaded = CasoManager.select( conn, cReloaded );
*/
            conn.commit();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            conn.rollback();
            throw new Exception(exc);
        } finally {
            CloseObject.closeObject(conn);
        }
        return retVal;
    }

    public RetencionEncabezado getRetencionEncabezado(int folio) throws Exception {
        Connection conn = null;
        RetencionEncabezado res = null;
        try {
            conn = getConnection();
            res = RetencionManager.getRetencionEncabezado(conn, folio);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            throw new Exception(exc);
        } finally {
            CloseObject.closeObject(conn);
        }
        return res;
    }

    public List<RetencionDetalle> getRetencionDetalle(int folio) throws Exception {
        Connection conn = null;
        List<RetencionDetalle> res = new ArrayList<RetencionDetalle>();
        try {
            conn = getConnection();
            res = RetencionManager.getRetencionDetalle(conn, folio);
            return res;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public File creaSicop(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int folio = Integer.parseInt(request.getParameter("folioRete"));
        ArrayList<String> arrListRete = null;
        Connection conn = null;
        try {
            conn = getConnection();
            //Devuelve la generación del archivo SICOP
            arrListRete = RetencionManager.generaArchivoSICOP(conn, folio);
            //Se genera el archivo y el nombre del archivo
            File strlayoutSicop = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "LayoutRetenciones" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".csv");
            BufferedWriter out = new BufferedWriter(new FileWriter(strlayoutSicop));
            StringBuffer archivoSICOP = new StringBuffer();
            String token = "";
            //Se genera el formato del archivo
            for (int i = 0; i < arrListRete.size(); i++) {
                archivoSICOP.append(token).append(arrListRete.get(i));
                token = "\n";
            }
            String outTextArchivo = archivoSICOP.toString();
            out.write(outTextArchivo);
            out.flush();
            out.close();
            return strlayoutSicop;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public File compromisoSicop(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int folio = Integer.parseInt(request.getParameter("folioRete"));
        ArrayList<String> arrListRete = null;
        Connection conn = null;
        String folioSicop = "";
        try {
            conn = getConnection();
            //Devuelve la generación del archivo SICOP
            arrListRete = RetencionManager.generaCompromisoSICOP(conn, folio);
            //Devuelve el folio de compromiso
            folioSicop = RetencionManager.buscaCompromiso(conn, folio);
            //Se genera el archivo y el nombre del archivo
            File strlayoutSicop = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "LayoutCompromisoRete" + "_NoComp" + folioSicop + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".csv");
            BufferedWriter out = new BufferedWriter(new FileWriter(strlayoutSicop));
            StringBuffer archivoSICOP = new StringBuffer();
            String token = "";
            //Se genera el formato del archivo
            for (int i = 0; i < arrListRete.size(); i++) {
                archivoSICOP.append(token).append(arrListRete.get(i));
                token = "\n";
            }
            String outTextArchivo = archivoSICOP.toString();
            out.write(outTextArchivo);
            out.flush();
            out.close();
            //Se regresa el archivo
            return strlayoutSicop;
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
