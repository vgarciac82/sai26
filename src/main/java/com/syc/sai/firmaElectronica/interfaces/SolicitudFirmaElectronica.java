package com.syc.sai.firmaElectronica.interfaces;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.axtel.reports.exceptions.ReportException;
import com.syc.fortimax.core.Carpeta;
import com.syc.fortimax.core.CarpetaManager;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.fortimax.core.OrgCarpeta;
import com.syc.fortimax.core.OrgCarpetaManager;
import com.syc.fortimax.core.Volumen;
import com.syc.fortimax.exceptions.FortimaxException;
import com.syc.gestion.core.Usuario;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.firmaElectronica.core.FirmaElectronicaManager;
import com.syc.sai.firmaElectronica.exceptions.FirmaElectronicaException;
import com.syc.sai.firmaElectronica.exceptions.NotEmptyDocumentException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public abstract class SolicitudFirmaElectronica {

    public static final int AUT_LAYOUT = 0;

    protected static final String AUT_LEGEND_PREFIX = "Autorizo";

    public static final String AUT_RM = "AUT_RM";

    public static final String AUT_RM_VOBO = "AUT_RM_VOBO";

    public static final int AUT_SICOP = -1;

    public static final String AUTORIZA = "AUT";

    public static final String UE_GRM = "A04";

    public static final String CORREOS_GRM_REQUI = "CORREOS_GRM_REQUI";

    public static final int ARCHIVOS_MASIVO_PENDIENTES = -8;

    public static final int ESPERA_CANCELACION = -4;

    public static final int ESTATUS_RM_AUT = 6;

    public static final int ESTATUS_RM_CANCELADA = 4;

    public static final int ESTATUS_RM_EMITIDA = 2;

    public static final int ESTATUS_RM_VoBo = 7;

    public static final int ID_ALMACEN_VIRTUAL = 2;

    public static final int ID_ALMACEN = 1;

    public static final int ESTATUS_ATENTA_NOTA_FIRMADA = 1;

    public static final String AUT_EST = "AUTORIZA_ESTIMACION";

    public static final String AUT_EST_JEFE = "AUTORIZA_ESTIMACION_JEFE";

    public static final String AUT_EST_SUBGERENTE = "AUTORIZA_ESTIMACION_SUBGERENTE";

    public static final int ESTATUS_ESTIMACION_FIRMA = 2;

    public static final int ESTATUS_ESTIMACION_FIRMA2 = 5;

    public static final int ESTATUS_ESTIMACION_FIRMA3 = 6;

    public static final int ESTATUS_ESTIMACION_AUT = 3;

    public static final int ESTATUS_ESTIMACION_CANCELADA = 4;

    public static final String FIRMA_ENSA = "FIRMA_ENSA";

    public static final int ESTATUS_ENSA_CAPTURA = 1;

    public static final int ESTATUS_ENSA_VALIDA = 2;

    public static final int ESTATUS_ENSA_FIRMA = 3;

    public static final int ESTATUS_ENSA_TESTIGO1 = 4;

    public static final int ESTATUS_ENSA_TESTIGO2 = 5;

    public static final int ESTATUS_ENSA_CONSULTA = 6;

    public static final int SERVICIO_NO_PRESTADO_ENSA = 2;

    public static final int GENERA_LAYOUT = 2;

    public static final int LAYOUT_GENERADO = 1;

    public static final int WAIT_FOR_MANAGER_AUTH = 4;

    public static final int LAYOUT_GENERADO_LAUDOS_IF = 1;

    public static final String LOCATION = "Comision Nacional Forestal";

    private static final Logger log = LoggerFactory.getLogger(SolicitudFirmaElectronica.class);

    public static final int POLIZA_AUTORIZADA = 1;

    public static final String R_AUT = "R_AUT";

    public static final int R_AUT_LAYOUT = 3;

    public static final int R_AUT_SICOP = -6;

    public static final String R_AUTORIZA = "R_AUT";

    public static final String GP_BCC_ATENTA_NOTA = "CORREO_BCC_ATENTA_NOTA";

    public static final String GP_VOBO_ATENTA_NOTA = "NUM_EMPLEADO_VOBO_ATENTA_NOTA";

    public static final String R_VO_BO = "R_VOBO";

    public static final int R_VO_BO_SICOP = -7;

    public static final String RECHAZA_RM = "RECHAZA_RM";

    public static final String RECHAZA_ESTIMACION = "RECHAZA_ESTIMACION";

    public static final Map<String, String> RELACION_TRAMITE_KEY = new HashMap<String, String>();

    public static final Map<String, String> RELACION_TRAMITE_TABLA_D = new HashMap<String, String>();

    public static final Map<String, String> RELACION_TRAMITE_TABLA_E = new HashMap<String, String>();

    public static final int SOLICITUD_CANCELADA = -3;

    public static final String PRE_AUTH = "PREAUTH";

    public static final String VO_BO = "VOBO";

    protected static final String VO_BO_LEGEND_PREFIX = "Responsable de la recepción del bien o servicio";

    public static final int VO_BO_SICOP = -2;

    public static final String TITULO_APLICACION_OBRA = "OBRAPUBLICA";

    public static final String ESTIMACION_CANCELADA = "CANCELADA";

    static {
        RELACION_TRAMITE_TABLA_E.put("COMSINVIATICOS", "tComisionesSinComprobacionEnc");
        RELACION_TRAMITE_TABLA_E.put("POLIZA", "tDocPolizaEncabezado");
        RELACION_TRAMITE_TABLA_D.put("COMSINVIATICOS", "tComisionesSinComprobacionDet");
        RELACION_TRAMITE_TABLA_D.put("POLIZA", "tDocPolizaDetalle");
        RELACION_TRAMITE_KEY.put("COMSINVIATICOS", "nFolioComision");
        RELACION_TRAMITE_KEY.put("POLIZA", "nFolioDocPoliza");
    }

    private boolean cargaMasiva = false;

    private String detail;

    private String docName;

    private String docNameJasper;

    private String document;

    private String fechaOficioAut;

    private String fechaOficioVoBo;

    private String field;

    private String fileExtension;

    private String folder = "Solicitud de Pago";

    private String folioOficioAut;

    private String folioOficioVoBo;

    private String folios;

    private boolean formato15D;

    private String header;

    private int idField;

    private String masiveDetail;

    private String masiveField;

    private String masiveHeader;

    private int masiveID;

    private String nombreEmpleadoSuplido;

    private String nombreEmpleadoSuplidoAut;

    private String passwordLlave;

    private boolean refirma;

    private String reportPath;

    private String rfcFirma;

    private String tipoAutorizacion;

    private String tipoSuplencia;

    private String tipoSuplenciaAut;

    private Usuario usuario;

    public Carpeta createFolder(Connection conn, int idCabinet) throws Exception {
        return createFolder(conn, idCabinet, getFolder());
    }

    public Carpeta createFolder(Connection conn, int idCabinet, String folderName) throws Exception {
        Carpeta modelo = new Carpeta();
        modelo.setTituloAplicacion(getDocument());
        modelo.setIdGabinete(idCabinet);
        modelo.setIdCarpeta(CarpetaManager.getNextIdCarpeta(conn, getDocument(), idCabinet));
        modelo.setNombreCarpeta(folderName);
        modelo.setNombreUsuario(getUsuario().getLogin());
        modelo.setBanderaRaiz("N");
        modelo.setDescripcion("Carpeta que contiene documentos firmados electronicamente.");
        modelo.setPassword("-1");
        modelo = CarpetaManager.insertaCarpeta(conn, modelo);
        OrgCarpeta oc = new OrgCarpeta();
        oc.setIdCarpetaHija(modelo.getIdCarpeta());
        oc.setIdCarpetaPadre(0);
        oc.setIdGabinete(idCabinet);
        oc.setNombreHija(folderName);
        oc.setTituloAplicacion(modelo.getTituloAplicacion());
        OrgCarpetaManager.insert(conn, oc);
        log.trace("Object: {}", "Carpeta [" + folderName + "] creada con exito.");
        return modelo;
    }

    public boolean esFirmaElectronica(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT cEsFirmaElectronica FROM ");
        query.append(getHeader());
        query.append(" WHERE ").append(getField()).append("= ?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean esFirmaElectronica = false;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, getIdField());
            rs = ps.executeQuery();
            if (rs.next()) {
                esFirmaElectronica = "S".equalsIgnoreCase(rs.getString(1));
            }
            return esFirmaElectronica;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public String generaAccessoAutToken(int numeroEmpleado, String document, int folio) {
        StringBuffer parametrosReales = null;
        /*
		 * Concatena los parametros. El separador sera el caracter | (pipe)
		 */
        parametrosReales = new StringBuffer("?");
        parametrosReales.append("u=").append(StringUtils.reverse(String.valueOf(numeroEmpleado)));
        parametrosReales.append("&");
        parametrosReales.append("d=").append(String.valueOf(document));
        parametrosReales.append("&");
        parametrosReales.append("f=").append(StringUtils.reverse(String.valueOf(folio)));
        log.debug("Object: " + String.valueOf("Cadena generada: " + parametrosReales));
        return parametrosReales.toString();
    }

    public abstract String generaArchivoFirma(Connection conn, Volumen vol, Carpeta folder, boolean isSignedCopy) throws FirmaElectronicaException;

    public abstract String generaArchivoInformeComision(Connection conn, Volumen vol, Carpeta folder, boolean isSignedCopy) throws FirmaElectronicaException;

    public String generaArchivoFirma(Connection conn, Volumen vol, Carpeta folder, Map<String, Object> reportParams, String subFolder, String reportName, boolean isSignedCopy) throws Exception {
        String filename = null;
        Documento d = getOrCreateDocument(conn, vol, folder, getDocName(), getFileExtension(), getUsuario().getLogin());
        filename = d.getFullPathFilesNames()[0];
        runReport(conn, getReportPath() + (StringUtils.isEmpty(subFolder) ? "" : File.separatorChar + subFolder), reportName, "", filename, reportParams);
        return filename;
    }

    public String generaURLDescarga(Connection conn) throws Exception {
        String ef = EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal();
        String saiName = ConfiguraAplicativoManager.getSystemSetting(conn, "URL_SAI") + "_" + ef + "/muestraDocumento";
        return saiName;
    }

    public abstract String getAutLegend(Connection conn) throws Exception;

    public abstract String getAutLegendWithName(Connection conn, String getVoBoNombre, String getVoBoPuesto) throws Exception;

    public String getAutNombre(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	U_NOMBRE  ");
        query.append("   FROM	CG_USUARIO WITH(NOLOCK)  ");
        query.append("  WHERE	cNumeroEmpleado =  ?  ");
        query.append("    AND	U_ESTATUS = 'A'");
        PreparedStatement psNombre = null;
        ResultSet rsNombre = null;
        try {
            int numeroEmpleadoAut = getNumEmpleadoAutSuplencia(conn);
            int numeroEmpleado = (numeroEmpleadoAut > 0 ? numeroEmpleadoAut : getAutNumEmpleado(conn));
            psNombre = conn.prepareStatement(query.toString());
            psNombre.setInt(1, numeroEmpleado);
            rsNombre = psNombre.executeQuery();
            if (rsNombre.next()) {
                if (StringUtils.isBlank(rsNombre.getString("U_NOMBRE")))
                    throw new Exception("El empleado con Numero: " + numeroEmpleado + " no tiene nombre asignado. Notifique al administrador");
                return rsNombre.getString("U_NOMBRE");
            } else
                throw new Exception("No se encontro empleado con numero: " + numeroEmpleado);
        } finally {
            CloseObject.closeObject(psNombre);
            CloseObject.closeObject(rsNombre);
        }
    }

    public String getAutNombreOriginal(Connection conn) throws Exception {
        String queryNombre = "SELECT U_NOMBRE FROM	CG_USUARIO WITH(NOLOCK) WHERE	cNumeroEmpleado =  ? AND	U_ESTATUS = 'A'";
        PreparedStatement psNombre = null;
        ResultSet rsNombre = null;
        try {
            int numeroEmpleado = getAutNumEmpleado(conn);
            psNombre = conn.prepareStatement(queryNombre);
            psNombre.setInt(1, numeroEmpleado);
            rsNombre = psNombre.executeQuery();
            if (rsNombre.next()) {
                if (StringUtils.isBlank(rsNombre.getString("U_NOMBRE")))
                    throw new Exception("El empleado con Numero: " + numeroEmpleado + " no tiene nombre asignado. Notifique al administrador");
                return rsNombre.getString("U_NOMBRE");
            } else
                throw new Exception("No se encontro empleado con numero: " + numeroEmpleado);
        } finally {
            CloseObject.closeObject(psNombre);
            CloseObject.closeObject(rsNombre);
        }
    }

    public int getAutNumEmpleado(Connection conn) throws Exception {
        String queryPago = "SELECT	nNumEmpleadoAut FROM " + getHeader() + " WITH(NOLOCK)  WHERE " + getField() + " = ? ";
        PreparedStatement psPago = null;
        ResultSet rsPago = null;
        try {
            int numeroEmpleado = -1;
            psPago = conn.prepareStatement(queryPago);
            psPago.setInt(1, getIdField());
            rsPago = psPago.executeQuery();
            if (rsPago.next()) {
                if (StringUtils.isBlank(rsPago.getString("nNumEmpleadoAut")))
                    throw new Exception("El campo No. Empleado para Aut esta vacio. Reporte al administrador");
                numeroEmpleado = Integer.parseInt(rsPago.getString("nNumEmpleadoAut"));
            } else {
                throw new Exception("No se encontro el pago con folio " + getIdField() + " en la tabla " + getHeader());
            }
            return numeroEmpleado;
        } finally {
            CloseObject.closeObject(psPago);
            CloseObject.closeObject(rsPago);
        }
    }

    public String getAutPuesto(Connection conn) throws Exception {
        String queryPuesto = "SELECT CARGO  FROM v_empleados_giro WITH(NOLOCK)  WHERE	CLAVE = ?";
        PreparedStatement psPuesto = null;
        ResultSet rsPuesto = null;
        try {
            int numeroEmpleadoAut = getNumEmpleadoAutSuplencia(conn);
            int numeroEmpleado = (numeroEmpleadoAut > 0 ? numeroEmpleadoAut : getAutNumEmpleado(conn));
            psPuesto = conn.prepareStatement(queryPuesto);
            psPuesto.setInt(1, numeroEmpleado);
            rsPuesto = psPuesto.executeQuery();
            if (rsPuesto.next()) {
                if (StringUtils.isBlank(rsPuesto.getString("CARGO")))
                    throw new Exception("El empleado con Numero: " + numeroEmpleado + " no tiene puesto asignado. Notifique al administrador");
                return rsPuesto.getString("CARGO");
            } else
                throw new Exception("No se encontro empleado con numero: " + numeroEmpleado);
        } finally {
            CloseObject.closeObject(psPuesto);
            CloseObject.closeObject(rsPuesto);
        }
    }

    public String getConcepto(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	cConcepto FROM vPagoProveedor WITH(NOLOCK) WHERE ctipoPago = ? AND nFolio = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, getDocument());
            ps.setInt(2, getIdField());
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getString(1);
            else
                throw new Exception("No fue posible encontrar el cConcepto para el folio " + getIdField() + " en el documento" + getDocument());
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public String getCorreoAutoriza(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	U_EMAIL  ");
        query.append("   FROM	CG_USUARIO WITH(NOLOCK)  ");
        query.append("  WHERE	cNumeroEmpleado =  ?  ");
        query.append("    AND	U_ESTATUS = 'A'");
        PreparedStatement psMail = null;
        ResultSet rsMail = null;
        try {
            int numeroEmpleadoAut = getNumEmpleadoAutSuplencia(conn);
            int numeroEmpleado = (numeroEmpleadoAut > 0 ? numeroEmpleadoAut : getAutNumEmpleado(conn));
            psMail = conn.prepareStatement(query.toString());
            psMail.setInt(1, numeroEmpleado);
            rsMail = psMail.executeQuery();
            if (rsMail.next()) {
                if (StringUtils.isBlank(rsMail.getString("U_EMAIL")))
                    throw new Exception("El empleado con Numero: " + numeroEmpleado + " no tiene correo asignado. Notifique al administrador");
                return rsMail.getString("U_EMAIL");
            } else
                throw new Exception("No se encontro empleado con numero: " + numeroEmpleado);
        } finally {
            CloseObject.closeObject(psMail);
            CloseObject.closeObject(rsMail);
        }
    }

    public String getCorreoVistoBueno(Connection conn) throws Exception {
        StringBuilder queryMail = new StringBuilder();
        queryMail.append("SELECT	U_EMAIL ");
        queryMail.append("  FROM	CG_USUARIO WITH(NOLOCK) ");
        queryMail.append(" WHERE	cNumeroEmpleado =  ? ");
        queryMail.append("   AND	U_ESTATUS = 'A'");
        PreparedStatement psMail = null;
        ResultSet rsMail = null;
        try {
            int numeroEmpleadoVoBo = getNumEmpleadoVistoBueno(conn);
            int numeroEmpleado = numeroEmpleadoVoBo > 0 ? numeroEmpleadoVoBo : getVoBoNumEmpleado(conn);
            psMail = conn.prepareStatement(queryMail.toString());
            psMail.setInt(1, numeroEmpleado);
            rsMail = psMail.executeQuery();
            if (rsMail.next()) {
                if (StringUtils.isBlank(rsMail.getString("U_EMAIL")))
                    throw new Exception("El empleado con Numero: " + numeroEmpleado + " no tiene correo asignado. Notifique al administrador");
                return rsMail.getString("U_EMAIL");
            } else
                throw new Exception("No se encontro empleado con numero: " + numeroEmpleado);
        } finally {
            CloseObject.closeObject(psMail);
            CloseObject.closeObject(rsMail);
        }
    }

    public abstract String getCuerpoCorreoAutoriza(Connection conn) throws Exception;

    public abstract String getCuerpoCorreoVistoBueno(Connection conn) throws Exception;

    /**
     * @return the detail
     */
    public String getDetail() {
        return detail;
    }

    /**
     * @return the docName
     */
    public String getDocName() {
        return docName;
    }

    /**
     * @return the document
     */
    public String getDocument() {
        return document;
    }

    /**
     * @return the fechaOficioAut
     */
    public String getFechaOficioAut() {
        return fechaOficioAut;
    }

    /**
     * @return the fechaOficio
     */
    public String getFechaOficioVoBo() {
        return fechaOficioVoBo;
    }

    /**
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * @return the fileExtension
     */
    public String getFileExtension() {
        return fileExtension;
    }

    public String getFolder() {
        return folder;
    }

    /**
     * @return the folioOficioAut
     */
    public String getFolioOficioAut() {
        return folioOficioAut;
    }

    /**
     * @return the folioOficioVoBo
     */
    public String getFolioOficioVoBo() {
        return folioOficioVoBo;
    }

    public String getFolios() {
        return this.folios;
    }

    public String getFolioSAI(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT FOLIO ");
        query.append("  FROM IMX").append(getDocument());
        query.append(" WHERE CONVERT( INT, substring(FOLIO, 10, 10) ) = ?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, getIdField());
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            } else
                throw new Exception("No se encontro folio SAI para el documento: " + getDocument() + " Con folio: " + getIdField());
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @return the idField
     */
    public int getIdField() {
        return idField;
    }

    public abstract String getImporteStr(Connection conn) throws Exception;

    public String getMasiveDetail() {
        return masiveDetail;
    }

    public String getMasiveField() {
        return masiveField;
    }

    public String getMasiveHeader() {
        return masiveHeader;
    }

    public int getMasiveID() {
        return masiveID;
    }

    /**
     * @return the nombreEmpleadoSuplido
     */
    public String getNombreEmpleadoSuplido() {
        return nombreEmpleadoSuplido;
    }

    /**
     * @return the nombreEmpleadoSuplidoAut
     */
    public String getNombreEmpleadoSuplidoAut() {
        return nombreEmpleadoSuplidoAut;
    }

    public int getNumEmpleadoAutSuplencia(Connection conn) throws Exception {
        String query = "SELECT	cnumeroEmpleado FROM tPagoFirmanteDelagatorio WITH(NOLOCK) WHERE	cTipoPago = ? AND	nFolioPago = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        int numeroEmpleado = -1;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, getDocument());
            ps.setInt(2, getIdField());
            rs = ps.executeQuery();
            if (rs.next())
                numeroEmpleado = rs.getInt(1);
            return numeroEmpleado;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public int getNumEmpleadoVistoBueno(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	cNumeroEmpleado ");
        query.append("  FROM	tPagoFirmanteDelegatorioVoBo  ");
        query.append(" WHERE	cTipoPago = ?  ");
        query.append("   AND	nFolioPago = ?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        int numeroEmpleado = -1;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, getDocument());
            ps.setInt(2, getIdField());
            rs = ps.executeQuery();
            if (rs.next())
                numeroEmpleado = rs.getInt(1);
            return numeroEmpleado;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public Documento getOrCreateDocument(Connection conn, Volumen vol, Carpeta parentFolder, String documentName, String fileExtension, String user) throws NotEmptyDocumentException, FortimaxException {
        Documento d = null;
        if (DocumentoManager.existeDocumento(conn, parentFolder.getTituloAplicacion(), parentFolder.getIdGabinete(), parentFolder.getIdCarpeta(), documentName)) {
            d = DocumentoManager.getDocumento(conn, parentFolder.getTituloAplicacion(), parentFolder.getIdGabinete(), parentFolder.getIdCarpeta(), documentName);
            if (DocumentoManager.existeDocumentoCapturado(conn, d.getTituloAplicacion(), d.getIdGabinete(), d.getIdCarpetaPadre(), d.getIdDocumento()))
                throw new NotEmptyDocumentException("El documento: " + parentFolder.getTituloAplicacion() + "_G" + parentFolder.getIdGabinete() + "C" + parentFolder.getIdCarpeta() + "D" + d.getIdDocumento() + " Existe y no esta vacio.");
        } else
            d = DocumentoManager.creaDocumento(conn, parentFolder, documentName, fileExtension, user);
        d.setExtension(fileExtension);
        DocumentoManager.insertPaginaDocumento(conn, vol, d, "A", 0);
        d = DocumentoManager.getDocumento(conn, parentFolder.getTituloAplicacion(), parentFolder.getIdGabinete(), parentFolder.getIdCarpeta(), documentName);
        d.setExtension(fileExtension);
        return d;
    }

    public Documento getDocument(Connection conn, Volumen vol, Carpeta parentFolder, String documentName, String fileExtension, String user) throws NotEmptyDocumentException, FortimaxException {
        Documento d = null;
        if (DocumentoManager.existeDocumento(conn, parentFolder.getTituloAplicacion(), parentFolder.getIdGabinete(), parentFolder.getIdCarpeta(), documentName)) {
            d = DocumentoManager.getDocumento(conn, parentFolder.getTituloAplicacion(), parentFolder.getIdGabinete(), parentFolder.getIdCarpeta(), documentName);
        } else {
            d = DocumentoManager.creaDocumento(conn, parentFolder, documentName, fileExtension, user);
            d.setExtension(fileExtension);
            DocumentoManager.insertPaginaDocumento(conn, vol, d, "A", 0);
        }
        d = DocumentoManager.getDocumento(conn, parentFolder.getTituloAplicacion(), parentFolder.getIdGabinete(), parentFolder.getIdCarpeta(), documentName);
        d.setExtension(fileExtension);
        return d;
    }

    public String getPasswordLlave() {
        return this.passwordLlave;
    }

    /**
     * @return the reportPath
     */
    public String getReportPath() {
        return reportPath;
    }

    public String getRfcFirma() {
        return this.rfcFirma;
    }

    public String getTipoAutorizacion() {
        return this.tipoAutorizacion;
    }

    /**
     * @return the tipoSuplencia
     */
    public String getTipoSuplencia() {
        return tipoSuplencia;
    }

    /**
     * @return the tipoSuplenciaAut
     */
    public String getTipoSuplenciaAut() {
        return tipoSuplenciaAut;
    }

    /**
     * @return the usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    public abstract String getVoBoLegend(Connection conn) throws Exception;

    public abstract String getVoBoLegendWithName(Connection conn, String getVoBoNombre, String getVoBoPuesto) throws Exception;

    public String getVoBoNombre(Connection conn) throws Exception {
        StringBuilder queryNombre = new StringBuilder();
        queryNombre.append("SELECT	U_NOMBRE ");
        queryNombre.append("  FROM	CG_USUARIO WITH(NOLOCK) ");
        queryNombre.append(" WHERE	cNumeroEmpleado =  ? ");
        queryNombre.append("   AND	U_ESTATUS = 'A'");
        PreparedStatement psNombre = null;
        ResultSet rsNombre = null;
        try {
            int numeroEmpleadoVoBo = getNumEmpleadoVistoBueno(conn);
            int numeroEmpleado = numeroEmpleadoVoBo > 0 ? numeroEmpleadoVoBo : getVoBoNumEmpleado(conn);
            psNombre = conn.prepareStatement(queryNombre.toString());
            psNombre.setInt(1, numeroEmpleado);
            rsNombre = psNombre.executeQuery();
            if (rsNombre.next()) {
                if (StringUtils.isBlank(rsNombre.getString("U_NOMBRE")))
                    throw new Exception("El empleado con Numero: " + numeroEmpleado + " no tiene nombre asignado. Notifique al administrador");
                return rsNombre.getString("U_NOMBRE");
            } else
                throw new Exception("No se encontro empleado con numero: " + numeroEmpleado);
        } finally {
            CloseObject.closeObject(psNombre);
            CloseObject.closeObject(rsNombre);
        }
    }

    public String getVoBoNombreOriginal(Connection conn) throws Exception {
        String queryNombre = "SELECT	U_NOMBRE FROM	CG_USUARIO WITH(NOLOCK) WHERE	cNumeroEmpleado =  ?   AND	U_ESTATUS = 'A'";
        PreparedStatement psNombre = null;
        ResultSet rsNombre = null;
        try {
            int numeroEmpleado = getVoBoNumEmpleado(conn);
            psNombre = conn.prepareStatement(queryNombre);
            psNombre.setInt(1, numeroEmpleado);
            rsNombre = psNombre.executeQuery();
            if (rsNombre.next()) {
                if (StringUtils.isBlank(rsNombre.getString("U_NOMBRE")))
                    throw new Exception("El empleado con Numero: " + numeroEmpleado + " no tiene nombre asignado. Notifique al administrador");
                return rsNombre.getString("U_NOMBRE");
            } else
                throw new Exception("No se encontro empleado con numero: " + numeroEmpleado);
        } finally {
            CloseObject.closeObject(psNombre);
            CloseObject.closeObject(rsNombre);
        }
    }

    public int getVoBoNumEmpleado(Connection conn) throws Exception {
        StringBuilder queryPago = new StringBuilder();
        queryPago.append("SELECT	nNumEmpleadoVoBo ");
        queryPago.append("  FROM	").append(getHeader()).append(" WITH(NOLOCK) ");
        queryPago.append(" WHERE	").append(getField()).append(" = ? ");
        PreparedStatement psPago = null;
        ResultSet rsPago = null;
        try {
            int numeroEmpleado = -1;
            psPago = conn.prepareStatement(queryPago.toString());
            psPago.setInt(1, getIdField());
            rsPago = psPago.executeQuery();
            if (rsPago.next()) {
                if (StringUtils.isBlank(rsPago.getString("nNumEmpleadoVoBo")))
                    throw new Exception("El campo No. Empleado para Vo Bo esta vacio. Reporte al administrador");
                numeroEmpleado = Integer.parseInt(rsPago.getString("nNumEmpleadoVoBo"));
            } else {
                throw new Exception("No se encontro el pago con folio " + getIdField() + " en la tabla " + getHeader());
            }
            return numeroEmpleado;
        } finally {
            CloseObject.closeObject(psPago);
            CloseObject.closeObject(rsPago);
        }
    }

    public String getVoBoPuesto(Connection conn) throws Exception {
        String queryPuesto = "SELECT CARGO FROM	v_empleados_giro WITH(NOLOCK) WHERE	CLAVE = ?";
        PreparedStatement psPuesto = null;
        ResultSet rsPuesto = null;
        try {
            int numeroEmpleadoVoBo = getNumEmpleadoVistoBueno(conn);
            int numeroEmpleado = numeroEmpleadoVoBo > 0 ? numeroEmpleadoVoBo : getVoBoNumEmpleado(conn);
            psPuesto = conn.prepareStatement(queryPuesto);
            psPuesto.setInt(1, numeroEmpleado);
            rsPuesto = psPuesto.executeQuery();
            if (rsPuesto.next()) {
                if (StringUtils.isBlank(rsPuesto.getString("CARGO")))
                    throw new Exception("El empleado con Numero: " + numeroEmpleado + " no tiene puesto asignado. Notifique al administrador");
                return rsPuesto.getString("CARGO");
            } else
                throw new Exception("No se encontro empleado con numero: " + numeroEmpleado);
        } finally {
            CloseObject.closeObject(psPuesto);
            CloseObject.closeObject(rsPuesto);
        }
    }

    public boolean isCargaMasiva() {
        return cargaMasiva;
    }

    public boolean isFormato15D() {
        return formato15D;
    }

    public boolean isQuestionaireAnswered(Connection conn) throws FirmaElectronicaException {
        return FirmaElectronicaManager.isQuestionnaireAnswered(conn, this);
    }

    public boolean isRefirma() {
        return refirma;
    }

    public abstract void notificaOperacionMasivaPendiente(Connection conn, String operacion) throws Exception;

    public abstract String notificaOperacionPendiente(Connection conn, String tipoAutorizacion) throws Exception;

    public abstract void onCancelaTramite(Connection conn, String reason) throws Exception;

    public abstract void onFinishAut(Connection conn) throws Exception;

    public abstract void onFinishVoBo(Connection conn) throws Exception;

    public void runReport(Connection conn, String reportPath, String reportName, String canoContrarecibo, String outputPath, Map<String, Object> parametrosReporte) throws ReportException {
        InputStream in = null;
        OutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(outputPath));
            in = new FileInputStream(reportPath + File.separatorChar + reportName);
            JasperRunManager.runReportToPdfStream(in, fos, parametrosReporte, conn);
            fos.flush();
        } catch (IOException | JRException e) {
            throw new ReportException(e);
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (Exception e) {
                    log.warn("Problemas cerrando flujo: " + e, e);
                }
            if (fos != null)
                try {
                    fos.close();
                } catch (Exception e) {
                    log.warn("Problemas cerrando flujo: " + e, e);
                }
            in = null;
            fos = null;
        }
    }

    public void setCargaMasiva(boolean cargaMasiva) {
        this.cargaMasiva = cargaMasiva;
    }

    /**
     * @param detail
     *            the detail to set
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     * @param docName
     *            the docName to set
     */
    public void setDocName(String docName) {
        this.docName = docName;
    }

    /**
     * @param document
     *            the document to set
     */
    public void setDocument(String document) {
        this.document = document;
    }

    /**
     * @param fechaOficioAut
     *            the fechaOficioAut to set
     */
    public void setFechaOficioAut(String fechaOficioAut) {
        this.fechaOficioAut = fechaOficioAut;
    }

    /**
     * @param fechaOficio
     *            the fechaOficio to set
     */
    public void setFechaOficioVoBo(String fechaOficio) {
        this.fechaOficioVoBo = fechaOficio;
    }

    /**
     * @param field
     *            the field to set
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * @param fileExtension
     *            the fileExtension to set
     */
    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    /**
     * @param folioOficioAut
     *            the folioOficioAut to set
     */
    public void setFolioOficioAut(String folioOficioAut) {
        this.folioOficioAut = folioOficioAut;
    }

    /**
     * @param folioOficioVoBo
     *            the folioOficioVoBo to set
     */
    public void setFolioOficioVoBo(String folioOficioVoBo) {
        this.folioOficioVoBo = folioOficioVoBo;
    }

    /**
     * @param folios
     *            the folios to set
     */
    public void setFolios(String folios) {
        this.folios = folios;
    }

    public void setFormato15D(boolean formato15d) {
        formato15D = formato15d;
    }

    /**
     * @param header
     *            the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @param idField
     *            the idField to set
     */
    public void setIdField(int idField) {
        this.idField = idField;
    }

    public void setMasiveDetail(String masiveDetail) {
        this.masiveDetail = masiveDetail;
    }

    public void setMasiveField(String masiveField) {
        this.masiveField = masiveField;
    }

    public void setMasiveHeader(String masiveHeader) {
        this.masiveHeader = masiveHeader;
    }

    public void setMasiveID(int masiveID) {
        this.masiveID = masiveID;
    }

    /**
     * @param nombreEmpleadoSuplido
     *            the nombreEmpleadoSuplido to set
     */
    public void setNombreEmpleadoSuplido(String nombreEmpleadoSuplido) {
        this.nombreEmpleadoSuplido = nombreEmpleadoSuplido;
    }

    /**
     * @param nombreEmpleadoSuplidoAut
     *            the nombreEmpleadoSuplidoAut to set
     */
    public void setNombreEmpleadoSuplidoAut(String nombreEmpleadoSuplidoAut) {
        this.nombreEmpleadoSuplidoAut = nombreEmpleadoSuplidoAut;
    }

    /**
     * @param passwordLlave
     *            the passwordLlave to set
     */
    public void setPasswordLlave(String passwordLlave) {
        this.passwordLlave = passwordLlave;
    }

    public void setRefirma(boolean refirma) {
        this.refirma = refirma;
    }

    /**
     * @param reportPath
     *            the reportPath to set
     */
    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    /**
     * @param rfcFirma
     *            the rfcFirma to set
     */
    public void setRfcFirma(String rfcFirma) {
        this.rfcFirma = rfcFirma;
    }

    /**
     * @param tipoAutorizacion
     *            the tipoAutorizacion to set
     */
    public void setTipoAutorizacion(String tipoAutorizacion) {
        this.tipoAutorizacion = tipoAutorizacion;
    }

    /**
     * @param tipoSuplencia
     *            the tipoSuplencia to set
     */
    public void setTipoSuplencia(String tipoSuplencia) {
        this.tipoSuplencia = tipoSuplencia;
    }

    /**
     * @param tipoSuplenciaAut
     *            the tipoSuplenciaAut to set
     */
    public void setTipoSuplenciaAut(String tipoSuplenciaAut) {
        this.tipoSuplenciaAut = tipoSuplenciaAut;
    }

    public String getDocNameJasper() {
        return docNameJasper;
    }

    public void setDocNameJasper(String docNameJasper) {
        this.docNameJasper = docNameJasper;
    }

    /**
     * @param usuario
     *            the usuario to set
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Verfica si tiene oficio delegatorio de firma
     *
     * @param conn
     * @return true si se capturo oficio delegatorio de autorizacion
     * @throws Exception
     */
    public boolean tieneDelegatorioAut(Connection conn) throws Exception {
        return FirmaElectronicaManager.cargaInformacionDelegatorioAut(conn, this);
    }

    /**
     * Verfica si tiene oficio delegatorio de firma de Vo Bo
     *
     * @param conn
     * @return true si se capturo oficio delegatorio de Vo Bo
     * @throws Exception
     */
    public boolean tieneDelegatorioVoBO(Connection conn) throws Exception {
        return FirmaElectronicaManager.cargaInformacionDelegatorioVoBo(conn, this);
    }

    public abstract void onGeneraArchivosMasivo(Connection conn);

    public abstract String notificaPrefirmante(Connection conn) throws Exception;
}
