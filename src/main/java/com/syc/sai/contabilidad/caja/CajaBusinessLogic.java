package com.syc.sai.contabilidad.caja;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.contable.core.SolicitudCajaFirmaElectronica;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.firmaElectronica.core.FirmaElectronicaManager;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CajaBusinessLogic extends DataSourceManager {

    static Logger log = LoggerFactory.getLogger(CajaBusinessLogic.class);

    private String header;

    private String detail;

    private String document;

    private String reportPath;

    private String field;

    private String folderName;

    private String documentName;

    /**
     * @return the folderName
     */
    private String getFolderName() {
        return folderName;
    }

    /**
     * @param folderName
     *            the folderName to set
     */
    private void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    /**
     * @return the documentName
     */
    private String getDocumentName() {
        return documentName;
    }

    /**
     * @param documentName
     *            the documentName to set
     */
    private void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    /**
     * @return the detail
     */
    private String getDetail() {
        return detail;
    }

    /**
     * @param detail
     *            the detail to set
     */
    private void setDetail(String detail) {
        this.detail = detail;
    }

    public CajaBusinessLogic() {
        init(null);
    }

    public CajaBusinessLogic(String jniName) {
        init(jniName);
    }

    public void init(String jniName) {
        if (StringUtils.isBlank(jniName))
            super.init();
        else
            super.init(jniName);
        setHeader("tCajaEncabezado");
        setDetail("tCajaDetalle");
        setDocument("CAJA");
        setFolderName("Documentacion Comprobatoria");
        setDocumentName("Solicitud Firmada");
        setField("nFolioCaja");
    }

    private void setDocument(String document) {
        this.document = document;
    }

    public static String[] readfAplicacion(String nFolioCaja, String cCentroContable, String cUR) {
        // 0=fAplicacion,1=minDate,2=MaxDate
        String[] fechas = new String[3];
        int APLICACION = 0;
        int MINIMA = 1;
        int MAXIMA = 2;
        int mesAbierto = -1;
        try {
            // VGC20160104 Calcula la fecha de captura para el cambio de
            // ejercicio fiscal. Si el EF Activo es diferente al EF actual => la
            // fecha de captura es 31/12/EFA en otro caso sera el dia
            AdecuacionBusinessLogic abl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
            int efa = Integer.parseInt(abl.obtenEjercicioFiscal());
            fechas[APLICACION] = CajaManager.ejecutaQueryRS("select convert(varchar, fAplicacion,103) fAplicacion from tcajaencabezado with(nolock) where nfoliocaja=" + nFolioCaja);
            mesAbierto = CajaManager.ejecutaQueryRI("select nMes from tMesesContables with(nolock) where mesAbierto='S' and cCentroContable='" + cCentroContable + "' AND cUnidadResponsable='" + cUR + "'");
            String DATE_FORMAT = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            Calendar c1 = Calendar.getInstance();
            if (efa == c1.get(Calendar.YEAR))
                fechas[MAXIMA] = c1.get(Calendar.YEAR) + "," + (c1.get(Calendar.MONTH)) + "," + c1.getActualMaximum(Calendar.DAY_OF_MONTH);
            else
                fechas[MAXIMA] = efa + "," + (11) + "," + c1.getActualMaximum(Calendar.DAY_OF_MONTH);
            if (fechas[APLICACION].equals("")) {
                if (efa == c1.get(Calendar.YEAR))
                    fechas[APLICACION] = sdf.format(c1.getTime());
                else
                    fechas[APLICACION] = "31/12/" + efa;
                int mesActual = c1.get(Calendar.MONTH) + 1;
                if (mesAbierto != mesActual) {
                    c1.set(Calendar.MONTH, mesAbierto - 1);
                    c1.set(Calendar.DAY_OF_MONTH, c1.getActualMaximum(Calendar.DAY_OF_MONTH));
                    if (efa != c1.get(Calendar.YEAR))
                        c1.set(Calendar.YEAR, efa);
                }
            } else
                c1.setTime(sdf.parse(fechas[APLICACION]));
            fechas[MINIMA] = c1.get(Calendar.YEAR) + "," + (mesAbierto - 1) + "," + c1.getActualMinimum(Calendar.DAY_OF_MONTH);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return fechas;
    }

    public static String[] readfAplicacion(String cCentroContable, String cUR) {
        // 0=fAplicacion,1=minDate,2=MaxDate
        String[] fechas = new String[3];
        int APLICACION = 0;
        int MINIMA = 1;
        int MAXIMA = 2;
        int mesAbierto = -1;
        try {
            // VGC20160104 Calcula la fecha de captura para el cambio de
            // ejercicio fiscal. Si el EF Activo es diferente al EF actual => la
            // fecha de captura es 31/12/EFA en otro caso sera el dia
            AdecuacionBusinessLogic abl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
            int efa = Integer.parseInt(abl.obtenEjercicioFiscal());
            boolean activaEventosFA = "S".equalsIgnoreCase((new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION)).getSystemSetting("ACTIVA_EVENTOS_FA"));
            mesAbierto = CajaManager.ejecutaQueryRI("select nMes from tMesesContables with(nolock) where mesAbierto='S' and cCentroContable='" + cCentroContable + "' AND cUnidadResponsable='" + cUR + "'");
            String DATE_FORMAT = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            Calendar c1 = Calendar.getInstance();
            if (efa != c1.get(Calendar.YEAR) && !activaEventosFA) {
                c1.set(Calendar.DAY_OF_MONTH, 31);
                c1.set(Calendar.MONTH, 11);
                c1.set(Calendar.YEAR, efa);
            }
            fechas[MAXIMA] = c1.get(Calendar.YEAR) + "," + (c1.get(Calendar.MONTH)) + "," + c1.getActualMaximum(Calendar.DAY_OF_MONTH);
            int mesActual = c1.get(Calendar.MONTH) + 1;
            if (efa != c1.get(Calendar.YEAR)) {
                c1.set(Calendar.DAY_OF_MONTH, 31);
                c1.set(Calendar.MONTH, 11);
                c1.set(Calendar.YEAR, efa);
            }
            fechas[APLICACION] = sdf.format(c1.getTime());
            if (mesAbierto != mesActual) {
                c1.set(Calendar.MONTH, mesAbierto - 1);
                c1.set(Calendar.DAY_OF_MONTH, c1.getActualMaximum(Calendar.DAY_OF_MONTH));
            }
            fechas[MINIMA] = c1.get(Calendar.YEAR) + "," + (c1.get(Calendar.MONTH)) + "," + c1.getActualMinimum(Calendar.DAY_OF_MONTH);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return fechas;
    }

    public static String readfCaptura(String nFolioCaja, String cCentroContable) {
        String fCaptura = "";
        try {
            fCaptura = CajaManager.ejecutaQueryRS("select convert(varchar, fcreacion,103) fcreacion from tcajaencabezado with(nolock) where nfoliocaja=" + nFolioCaja);
            // VGC20160104 Calcula la fecha de captura para el cambio de
            // ejercicio fiscal. Si el EF Activo es diferente al EF actual => la
            // fecha de captura es 31/12/EFA en otro caso sera el dia
            if (fCaptura.equals("")) {
                String DATE_FORMAT = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                Calendar c1 = Calendar.getInstance();
                fCaptura = sdf.format(c1.getTime());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return fCaptura;
    }

    public String readfCancelacion(String nFolioCaja) throws Exception {
        String fCaptura = "";
        String[] fecha = new String[3];
        try {
            fCaptura = CajaManager.ejecutaQueryRS("select convert(varchar, fcancelacion,103) fcreacion from tcajaencabezado with(nolock) where nfoliocaja=" + nFolioCaja);
            fecha = fCaptura.split("/");
            fCaptura = fecha[2] + "/" + fecha[1] + "/" + fecha[0];
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        return fCaptura;
    }

    public String[] PolizaCancelacion(Connection conn, String nFolioCaja) throws Exception {
        // 0: Folio de Poliza de Cancelacion, 1:Tipo de Poliza, 2:Centro
        // Contable ,3:Fecha de Aplicacion de la Cancelacion
        String[] datos = new String[5];
        datos[0] = CajaManager.ejecutaQueryRS(conn, "select  nfoliopolizacancelacion poliza from tcajaencabezado with(nolock) where nfoliocaja=" + nFolioCaja);
        datos[1] = CajaManager.ejecutaQueryRS(conn, "select  ctipopoliza tipopoliza  from tcajaencabezado with(nolock) where nfoliocaja=" + nFolioCaja);
        datos[2] = CajaManager.ejecutaQueryRS(conn, "select  cCentroContable   from tcajaencabezado with(nolock) where nfoliocaja=" + nFolioCaja);
        datos[3] = CajaManager.ejecutaQueryRS(conn, "select  convert(varchar, fAplicacion,103) faplicacion from tpoliza with(nolock) where nfolioPoliza=" + datos[0] + " and ctipopoliza='" + datos[1] + "' and cCentroContable='" + datos[2] + "'");
        datos[4] = CajaManager.ejecutaQueryRS(conn, "select nNumCheque from tChequeEncabezado WITH(nolock) where caNoContrarrecibo='" + nFolioCaja + "' and cDocumentoHaplicado ='S'");
        if (datos[4].equalsIgnoreCase(""))
            datos[4] = "No se cancela ningun cheque";
        else {
            CajaManager.cancelaCheque(conn, "update tChequeEncabezado set cDocumentoHaplicado ='C',fCancelacion =GETDATE(),cDescripcionPoliza='Se Cancela Solicitud No Presupuestal Origen', nFolioPolizaCancelacion=" + datos[0] + "where caNoContrarrecibo='" + nFolioCaja + "' and cDocumentoHaplicado ='S'");
            datos[4] = "Se Cancela el cheque asociado numero: " + datos[4];
        }
        String evento = CajaManager.ejecutaQueryRS(conn, "SELECT cEvento FROM tCajaDetalle WITH(nolock) WHERE nFOlioCaja = " + nFolioCaja);
        String saldoInicial = CajaManager.ejecutaQueryRS(conn, "SELECT ISNULL(esSaldoInicial,'0') FROM tcajaencabezado (NOLOCK) WHERE nFoliocaja= " + nFolioCaja);
        String[] componentesEvento = evento.split("_");
        if (("35".equals(componentesEvento[0]) && "1".equals(componentesEvento[1]) && "2".equals(componentesEvento[2]) && "0".equals(saldoInicial)) || ("35".equals(componentesEvento[0]) && "2".equals(componentesEvento[1]) && "7".equals(componentesEvento[2]) && "0".equals(saldoInicial))) {
            CajaManager.actualizaRemanentesComprobacion(conn, nFolioCaja);
            CajaManager.updateQuery(conn, "UPDATE tcajaencabezado SET comprobado=NULL, nFolioComprobacion=0 WHERE nfoliocaja IN (SELECT nfoliocaja  FROM tBonificacion_Comision WITH(nolock) WHERE nFolioComprobacion=" + nFolioCaja + ")");
            CajaManager.updateQuery(conn, "DELETE FROM tBonificacion_Comision WHERE nFolioComprobacion=" + nFolioCaja);
        }
        String eventoSI = CajaManager.ejecutaQueryRS(conn, "SELECT count(*) FROM dbo.tSaldoAntiguedadEventos WITH(nolock) WHERE cEvento = '" + evento + "'");
        if ("1".equals(eventoSI) && "1".equals(saldoInicial)) {
            CajaManager.updateQuery(conn, "UPDATE tcajaencabezado SET esSaldoInicial = NULL WHERE nfoliocaja =" + nFolioCaja);
            CajaManager.updateQuery(conn, "UPDATE tSaldoAntiguedadEncabezado SET cEstatus = 'Cancelado' WHERE nfoliocaja =" + nFolioCaja);
            // TODO: Regresar mSaldo - tSaldosAntiguedad - segun
            // tSaldoAntiguedadDetalle
            CajaManager.updateQuery(conn, "UPDATE dbo.tSaldosAntiguedad " + "SET mSaldo = mSaldo + (SELECT D.mImporte " + "FROM dbo.tSaldoAntiguedadEncabezado E (NOLOCK) " + "INNER JOIN dbo.tSaldoAntiguedadDetalle D (NOLOCK) " + "ON E.nFolioSAEnc = D.nFolioSAEnc " + "AND E.nFolioCaja = " + nFolioCaja + " AND D.nFolioSA = dbo.tSaldosAntiguedad.nFolioSA) " + "WHERE nFolioSA IN (SELECT D.nFolioSA " + "FROM dbo.tSaldoAntiguedadEncabezado E (NOLOCK) " + "INNER JOIN dbo.tSaldoAntiguedadDetalle D (NOLOCK) " + "ON E.nFolioSAEnc = D.nFolioSAEnc " + "AND E.nFolioCaja = " + nFolioCaja + ")");
        }
        return datos;
    }

    public static String LeerVistas(String usuario) {
        String vistas = " ";
        String query = "Select ur from tvistasUR (NOLOCK) where modulo='TESORERIA' and usuario='" + usuario + "'";
        // log.info(query);
        try {
            vistas = CajaManager.ejecutaQueryCadena(query, "ur");
            // log.info(vistas);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return vistas;
    }

    protected static void sendError(HttpServletResponse resp, String msg) throws IOException {
        PrintWriter out = resp.getWriter();
        out.println("<html>");
        out.println("\t<body>");
        out.println("\t\t<h1>Se presento el siguiente problema mientras se llenaba el reporte</h1><br>");
        out.println("\t\t<br>" + msg + "<br>");
        out.println("\t</body>");
        out.println("</html>");
        out.flush();
        out.close();
    }

    public void solicitaFirmaElectronica(Caso c, Usuario u) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            solicitaFirmaElectronica(conn, c, u, "");
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn(e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void solicitaFirmaElectronica(Connection conn, Caso c, Usuario u, String reportPath) throws Exception {
        int nFolioCaja = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
        SolicitudFirmaElectronica printer = new SolicitudCajaFirmaElectronica();
        printer.setDetail(getDetail());
        printer.setDocName(getDocumentName());
        printer.setDocument(getDocument());
        printer.setField(getField());
        printer.setFileExtension("pdf");
        printer.setHeader(getHeader());
        printer.setIdField(nFolioCaja);
        printer.setReportPath(reportPath.equals("") ? getReportPath() : reportPath);
        printer.setUsuario(u);
        FirmaElectronicaManager.generaArchivoFirma(conn, printer, getFolderName(), false);
        CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
        Map<String, String> datos = Util.readValuesCasoDato(c.getCasoDato());
        cbl.avanzaCaso(conn, c, u.getLogin(), "", new String[] { "VO_BO_CAJA_FIEL" }, new String[] { "vo_bo_fiel" }, datos, null);
        printer.notificaOperacionPendiente(conn, SolicitudFirmaElectronica.VO_BO);
        FirmaElectronicaManager.avanzaEstatusSICOP(conn, printer, SolicitudFirmaElectronica.VO_BO_SICOP);
        FirmaElectronicaManager.actualizaMetodoAutorizacion(conn, printer.getHeader(), printer.getField(), String.valueOf(printer.getIdField()), true);
    }

    private String getDocument() {
        return document;
    }

    private String getHeader() {
        return this.header;
    }

    private void setHeader(String header) {
        this.header = header;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    public String getReportPath() {
        return this.reportPath;
    }

    private String getField() {
        return this.field;
    }

    private void setField(String field) {
        this.field = field;
    }
}
