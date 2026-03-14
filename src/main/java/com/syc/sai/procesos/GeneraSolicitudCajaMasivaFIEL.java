package com.syc.sai.procesos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.fortimax.core.Volumen;
import com.syc.fortimax.core.VolumenManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import net.sf.jasperreports.engine.JasperRunManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneraSolicitudCajaMasivaFIEL {

    private static final Logger log = LoggerFactory.getLogger(GeneraSolicitudCajaMasivaFIEL.class);

    private StringBuilder query = new StringBuilder();

    private String directorioReportes;

    private String archivoEntrada;

    private int firmanteElab;

    private int firmanteVoBo;

    private int firmanteAuth;

    public StringBuilder getQuery() {
        return query;
    }

    public void setQuery(StringBuilder query) {
        this.query = query;
    }

    public String getDirectorioReportes() {
        return directorioReportes;
    }

    public void setDirectorioReportes(String directorioReportes) {
        this.directorioReportes = directorioReportes;
    }

    public String getArchivoEntrada() {
        return archivoEntrada;
    }

    public void setArchivoEntrada(String archivoEntrada) {
        this.archivoEntrada = archivoEntrada;
    }

    public int getFirmanteElab() {
        return firmanteElab;
    }

    public void setFirmanteElab(int firmanteElab) {
        this.firmanteElab = firmanteElab;
    }

    public int getFirmanteVoBo() {
        return firmanteVoBo;
    }

    public void setFirmanteVoBo(int firmanteVoBo) {
        this.firmanteVoBo = firmanteVoBo;
    }

    public int getFirmanteAuth() {
        return firmanteAuth;
    }

    public void setFirmanteAuth(int firmanteAuth) {
        this.firmanteAuth = firmanteAuth;
    }

    public GeneraSolicitudCajaMasivaFIEL() {
        query.append("UPDATE	tcajaencabezado  ");
        query.append("   SET	cEsFirmaElectronica = 'S', ");
        query.append("		nEnviadoSICOP = -2, ");
        query.append("		nNumEmpleadoVoBo = ?, ");
        query.append("		nNumEmpleadoAut = ?, ");
        query.append("		nNumEmpleadoElab = ?, ");
        query.append("		id_caso = ? ");
        query.append(" WHERE	nfoliocaja  = ? ");
    }

    public static void main(String[] args) {
        GeneraSolicitudCajaMasivaFIEL gscmf = new GeneraSolicitudCajaMasivaFIEL();
        gscmf.setDirectorioReportes(args[0]);
        gscmf.setArchivoEntrada(args[1]);
        gscmf.setFirmanteElab(Integer.parseInt(args[2]));
        gscmf.setFirmanteVoBo(Integer.parseInt(args[3]));
        gscmf.setFirmanteAuth(Integer.parseInt(args[4]));
        gscmf.generaSolicitudes();
    }

    private void generaSolicitudes() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            List<String> folios = cargaFolios(getArchivoEntrada());
            conn = Util.getStandAloneConnection();
            int numero = 0;
            ps = conn.prepareStatement(getQuery().toString());
            for (String folio : folios) {
                log.info("Object: {}", "Procesando folio:" + folio);
                if (StringUtils.isBlank(folio))
                    continue;
                Caso c = new Caso();
                c.setFolio(folio);
                c = CasoManager.select(conn, c);
                int nFolioCaja = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf("-") + 1));
                actualizaInformacion(ps, c);
                generaSolicitudAutorizacion(conn, c, nFolioCaja, getDirectorioReportes());
                System.out.println("Proceado " + numero++);
            }
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    // e2.printStackTrace();
                    log.warn(e2.getMessage(), e2);
                }
            log.error(e.getMessage(), e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    private void actualizaInformacion(PreparedStatement ps, Caso c) throws SQLException {
        int nFolioCaja = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf("-") + 1));
        ps.setInt(1, getFirmanteVoBo());
        ps.setInt(2, getFirmanteAuth());
        ps.setInt(3, getFirmanteElab());
        ps.setInt(4, c.getIdCaso());
        ps.setInt(5, nFolioCaja);
        ps.executeUpdate();
    }

    private File generaSolicitudAutorizacion(Connection conn, Caso casoCaja, int nFolioCaja, String directorioReportes) throws Exception {
        File reportParentDir = new File(directorioReportes);
        String fileName = generaRutaSalida(conn, casoCaja, "Solicitud Firmada", "pdf");
        log.info("Object: {}", "El archivo se guardara en:" + fileName);
        String reportName = "PolizaCaja_FIEL.jasper";
        File solicitud = new File(fileName);
        File reporteIn = new File(reportParentDir, reportName);
        String whereFolio = "and ce.nfoliocaja=" + nFolioCaja;
        OutputStream out = null;
        InputStream in = null;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("whereFolio", whereFolio);
            params.put("SUBREPORT_DIR", directorioReportes);
            in = new FileInputStream(reporteIn);
            out = new FileOutputStream(solicitud);
            JasperRunManager.runReportToPdfStream(in, out, params, conn);
            out.flush();
            return solicitud;
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (Exception e) {
                    log.warn("Object: {}", "Problemas cerrando flujo: " + e);
                }
            if (out != null)
                try {
                    out.close();
                } catch (Exception e) {
                    log.warn("Object: {}", "Problemas cerrando flujo: " + e);
                }
        }
    }

    private List<String> cargaFolios(String inputFile) throws Exception {
        File f = new File(inputFile);
        BufferedReader entrada = null;
        List<String> l = new ArrayList<String>();
        try {
            entrada = new BufferedReader(new FileReader(f));
            while (entrada.ready()) {
                l.add(entrada.readLine());
            }
            return l;
        } finally {
            if (entrada != null)
                try {
                    entrada.close();
                    entrada = null;
                } catch (Exception e) {
                }
        }
    }

    public synchronized String generaRutaSalida(Connection conn, Caso c, String nombreDocumento, String ext) throws Exception {
        Volumen vol = VolumenManager.getVolumen(conn);
        Documento d = DocumentoManager.buscaDocumento(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), nombreDocumento);
        if (d == null)
            throw new Exception(" No se encontro el documento: " + nombreDocumento + " en el expediente: " + c.getTipoCaso().getGavetaAsociada() + " " + c.getIdCaso() + " " + c.getIdGabinete());
        d.setExtension(ext);
        DocumentoManager.insertPaginaDocumento(conn, vol, d, "A", 0);
        // Lee nuevamente el documento para actualizar las paginas
        d = DocumentoManager.getDocumento(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), d.getIdCarpetaPadre(), nombreDocumento);
        String filename = d.getFullPathFilesNames()[d.getFullPathFilesNames().length - 1];
        return filename;
    }
}
