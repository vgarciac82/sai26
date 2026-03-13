package com.syc.contable.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.axtel.egresos.core.CargaMasivaRG;
import com.axtel.egresos.exceptions.EgresoException;
import com.axtel.reports.exceptions.ReportException;
import com.syc.egresos.core.RelacionGastosEncabezado;
import com.syc.egresos.core.impl.EgresoPAGODIVERSOEncabezado;
import com.syc.ejercido.pagado.core.EgresosManager;
import com.syc.fortimax.core.Carpeta;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.fortimax.core.Volumen;
import com.syc.fortimax.exceptions.FortimaxException;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.firmaElectronica.FirmaElectronicaBusinessLogic;
import com.syc.sai.firmaElectronica.core.FirmaElectronicaManager;
import com.syc.sai.firmaElectronica.exceptions.FirmaElectronicaException;
import com.syc.sai.firmaElectronica.exceptions.NotEmptyDocumentException;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SolicitudPagoFirmaElectronica extends SolicitudFirmaElectronica {

    private static final Logger log = LoggerFactory.getLogger(SolicitudPagoFirmaElectronica.class);

    public SolicitudPagoFirmaElectronica() {
        super();
        setFolder("Solicitud de Pago");
    }

    @Override
    public String generaArchivoFirma(Connection conn, Volumen vol, Carpeta folder, boolean isSignedCopy) throws FirmaElectronicaException {
        PreparedStatement psCxP = null;
        ResultSet rsCxP = null;
        String queryCxP = "SELECT canocontrarrecibo FROM " + getHeader() + " WITH(NOLOCK) WHERE " + getField() + " = ? ";
        String filename = null;
        String reportName = FirmaElectronicaManager.REPORTES_SOL_PAGO.get((isSignedCopy ? "ACUSE_" : "") + getDocument() + ((isQuestionaireAnswered(conn)) ? "_15D" : "") + "_FIEL");
        try {
            psCxP = conn.prepareStatement(queryCxP);
            psCxP.setInt(1, getIdField());
            rsCxP = psCxP.executeQuery();
            String canocontrarrecibo = null;
            if (rsCxP.next())
                canocontrarrecibo = StringUtils.trimToEmpty(rsCxP.getString(1));
            else
                throw new FirmaElectronicaException(new StringBuilder("No se encontro cuenta por pagar para el folio ").append(getIdField()).append(" en la tabla ").append(getHeader()).toString());
            Map<String, Object> parametrosReporte = new HashMap<>();
            parametrosReporte.put("SUBREPORT_DIR", getReportPath());
            if ("RELACIONGASTOS".equalsIgnoreCase(getDocument()))
                parametrosReporte.put("whereFolio", " AND CR.caNoContrarrecibo = '" + canocontrarrecibo + "'");
            else
                parametrosReporte.put("whereFolio", " CR.canocontrarrecibo='" + canocontrarrecibo + "'");
            try {
                Documento d = getOrCreateDocument(conn, vol, folder, getDocName(), getFileExtension(), getUsuario().getLogin());
                filename = d.getFullPathFilesNames()[0];
                runReport(conn, getReportPath(), reportName, canocontrarrecibo, filename, parametrosReporte);
            } catch (NotEmptyDocumentException nede) {
                log.warn("El documento no esta vacio. Se ignora" + nede);
            } catch (Exception e) {
                throw new FirmaElectronicaException(e);
            }
            int totalInvoices = EgresosManager.getTotalInvoices(conn, getDocument(), getIdField());
            int totalEP = EgresosManager.getTotalEP(conn, getDetail(), getField(), getIdField());
            if (!isSignedCopy && (totalInvoices > 7 || totalEP > 7)) {
                String anexoName = "Anexo1.jasper";
                Documento anexo = null;
                try {
                    anexo = getOrCreateDocument(conn, vol, folder, "Anexo Solicitud de Pago", getFileExtension(), getUsuario().getLogin());
                } catch (NotEmptyDocumentException e) {
                    log.warn("El archivo ya existia. Se ignora" + e, e);
                }
                String filenameAnexo = anexo.getFullPathFilesNames()[0];
                parametrosReporte.put("swhere", " and caNoContrarrecibo = '" + canocontrarrecibo + "'");
                runReport(conn, getReportPath(), anexoName, canocontrarrecibo, filenameAnexo, parametrosReporte);
            }
            int totalJust = EgresosManager.getJustification(conn, getIdField());
            if ("RELACIONGASTOS".equalsIgnoreCase(getDocument()) && totalJust > 0) {
                String justName = "ReporteJustificaciones.jasper";
                Documento docJustification = null;
                try {
                    docJustification = getDocument(conn, vol, folder, "Firma de Justificaciones", getFileExtension(), getUsuario().getLogin());
                } catch (NotEmptyDocumentException e) {
                    log.warn("El archivo ya existia. Se ignora" + e, e);
                }
                String filenameJustif = docJustification.getFullPathFilesNames()[0];
                parametrosReporte.put("where2", getIdField());
                runReport(conn, getReportPath(), justName, canocontrarrecibo, filenameJustif, parametrosReporte);
            }
            return filename;
        } catch (SQLException | EgresoException | FortimaxException | ReportException e1) {
            throw new FirmaElectronicaException(e1);
        } finally {
            CloseObject.closeObject(rsCxP);
            CloseObject.closeObject(psCxP);
        }
    }

    public String generaArchivoInformeComision(Connection conn, Volumen vol, Carpeta folder, boolean isSignedCopy) throws FirmaElectronicaException {
        PreparedStatement psCxP = null;
        ResultSet rsCxP = null;
        String queryCxP = "SELECT  caNoContrarrecibo FROM tRELACIONGASTOSEncabezado WITH (NOLOCK) WHERE  nFolioRELACIONGASTOS = ? ";
        String filename = null;
        String reportName = "PolizaInformeComisionRG_FIEL.jasper";
        try {
            psCxP = conn.prepareStatement(queryCxP);
            psCxP.setInt(1, getIdField());
            rsCxP = psCxP.executeQuery();
            String canocontrarrecibo = null;
            if (rsCxP.next())
                canocontrarrecibo = StringUtils.trimToEmpty(rsCxP.getString(1));
            else
                throw new FirmaElectronicaException(new StringBuilder("No se encontro cuenta por pagar para el folio ").append(getIdField()).append(" en la tabla ").append(getHeader()).toString());
            Map<String, Object> parametrosReporte = new HashMap<>();
            parametrosReporte.put("SUBREPORT_DIR", getReportPath());
            parametrosReporte.put("whereFolio", canocontrarrecibo);
            try {
                Documento d = getOrCreateDocument(conn, vol, folder, getDocName(), getFileExtension(), getUsuario().getLogin());
                filename = d.getFullPathFilesNames()[0];
                runReport(conn, getReportPath(), reportName, canocontrarrecibo, filename, parametrosReporte);
            } catch (NotEmptyDocumentException nede) {
                log.warn("El documento no esta vacio. Se ignora" + nede);
            } catch (Exception e) {
                throw new FirmaElectronicaException(e);
            }
            return filename;
        } catch (Exception e1) {
            throw new FirmaElectronicaException(e1);
        } finally {
            CloseObject.closeObject(rsCxP);
            CloseObject.closeObject(psCxP);
        }
    }

    private StringBuilder generaLigaDoctos(Connection conn, String urlDescarga, int numeroEmpleado, String operacion) throws Exception {
        StringBuilder expedientBody = new StringBuilder();
        expedientBody.append("	<br />");
        expedientBody.append("	<p>");
        expedientBody.append("	En la tabla siguiente puede revisar el documento que sera firmado.");
        expedientBody.append("	</p>");
        expedientBody.append("<table id=\"docsTbl\">");
        expedientBody.append("		<thead>");
        expedientBody.append("			<tr>");
        expedientBody.append("				<th>Archivo a Firmar</th>");
        expedientBody.append("				<th>Motivo</th>");
        expedientBody.append("			</tr>");
        expedientBody.append("		</thead>");
        expedientBody.append("		<tbody>");
        expedientBody.append("			<tr>");
        expedientBody.append("				<td>");
        expedientBody.append("				<a href=\"" + urlDescarga + generaRutaArchivo(conn, numeroEmpleado, getDocument(), getIdField()) + "\" > Solicitud de Pago </a>");
        expedientBody.append("				</td>");
        expedientBody.append("				<td>");
        expedientBody.append("				Firma de " + operacion + " al tramite.");
        expedientBody.append("				</td>");
        expedientBody.append("			</tr>");
        expedientBody.append("</table>");
        expedientBody.append("	<br />");
        expedientBody.append("	<br />");
        return expedientBody;
    }

    private String generaRutaArchivo(Connection conn, int numeroEmpleado, String document, int idField) throws Exception {
        FirmaElectronicaBusinessLogic febl = new FirmaElectronicaBusinessLogic(GestionInterface.ATT_CONEXION);
        febl.cargaInformacionTramite(document);
        Caso c = CasoManager.findByFolioLike(conn, document, String.valueOf(idField));
        Documento documento = DocumentoManager.buscaDocumento(conn, document, c.getIdGabinete(), febl.getTramiteSolicitud().getDocumentoFirmar());
        StringBuilder fortimaxNode = new StringBuilder(document).append("_").append("G").append(c.getIdGabinete()).append("C").append(documento.getIdCarpetaPadre()).append("D").append(documento.getIdDocumento());
        StringBuilder parametrosReales = null;
        /*
		 * Concatena los parametros. El separador sera el caracter | (pipe)
		 */
        parametrosReales = new StringBuilder("?");
        parametrosReales.append("fortimax=").append(fortimaxNode);
        log.debug("Cadena generada: " + parametrosReales.toString());
        return parametrosReales.toString();
    }

    @Override
    public String getAutLegend(Connection conn) {
        try {
            if (tieneDelegatorioAut(conn)) {
                String voLegend = AUT_LEGEND_PREFIX + ". Firma " + getTipoSuplenciaAut() + " de " + getNombreEmpleadoSuplidoAut() + " con fundamento en el oficio: " + getFolioOficioAut() + " de fecha: " + getFechaOficioAut();
                log.info(voLegend);
                return voLegend;
            } else
                return AUT_LEGEND_PREFIX;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getAutLegendWithName(Connection conn, String nombre, String puesto) {
        try {
            String autLegend = AUT_LEGEND_PREFIX.concat(". Firmado por: ").concat(nombre).concat(" | ").concat(puesto);
            if (tieneDelegatorioAut(conn)) {
                autLegend = AUT_LEGEND_PREFIX + ". Firma " + getTipoSuplenciaAut() + " de " + getNombreEmpleadoSuplidoAut() + " con fundamento en el oficio: " + getFolioOficioAut() + " de fecha: " + getFechaOficioAut();
                log.info(autLegend);
            }
            return autLegend;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getAutNombreMasivo(Connection conn) throws Exception {
        StringBuilder queryNombre = new StringBuilder();
        queryNombre.append("SELECT	U_NOMBRE ");
        queryNombre.append("  FROM	CG_USUARIO WITH(NOLOCK) ");
        queryNombre.append(" WHERE	cNumeroEmpleado =  ? ");
        queryNombre.append("   AND	U_ESTATUS = 'A'");
        PreparedStatement psNombre = null;
        ResultSet rsNombre = null;
        try {
            int numeroEmpleadoAut = getNumEmpleadoAutMasivo(conn);
            int numeroEmpleado = numeroEmpleadoAut > 0 ? numeroEmpleadoAut : getAutNumEmpleadoMasivo(conn);
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

    public int getAutNumEmpleadoMasivo(Connection conn) throws Exception {
        StringBuilder queryPago = new StringBuilder();
        queryPago.append("SELECT	TOP 1 nNumEmpleadoAut ");
        queryPago.append("  FROM	").append(getHeader()).append(" WITH(NOLOCK) ");
        queryPago.append(" WHERE	nFolioCargaMasiva = ? ");
        PreparedStatement psPago = null;
        ResultSet rsPago = null;
        try {
            int numeroEmpleado = -1;
            psPago = conn.prepareStatement(queryPago.toString());
            psPago.setInt(1, getMasiveID());
            rsPago = psPago.executeQuery();
            if (rsPago.next()) {
                if (StringUtils.isBlank(rsPago.getString("nNumEmpleadoAut")))
                    throw new Exception("El campo No. Empleado para Autoriza esta vacio. Reporte al administrador");
                numeroEmpleado = Integer.parseInt(rsPago.getString("nNumEmpleadoAut"));
            } else {
                throw new Exception("No se encontraron pagos en la carga masiva con folio " + getIdField() + " en la tabla " + getHeader());
            }
            return numeroEmpleado;
        } finally {
            CloseObject.closeObject(psPago);
            CloseObject.closeObject(rsPago);
        }
    }

    public String getAutPuestoMasivo(Connection conn) throws Exception {
        StringBuilder queryPuesto = new StringBuilder();
        queryPuesto.append("SELECT	CARGO ");
        queryPuesto.append("  FROM	v_empleados_giro WITH(NOLOCK) ");
        queryPuesto.append(" WHERE	CLAVE = ?");
        PreparedStatement psPuesto = null;
        ResultSet rsPuesto = null;
        try {
            int numeroEmpleadoAut = getNumEmpleadoAutMasivo(conn);
            int numeroEmpleado = numeroEmpleadoAut > 0 ? numeroEmpleadoAut : getAutNumEmpleadoMasivo(conn);
            psPuesto = conn.prepareStatement(queryPuesto.toString());
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

    private String getBeneficiario(Connection conn) throws Exception {
        String query = "SELECT	ltrim(rtrim(RFC)) + ' - ' + NOMBRE AS rfcBeneficiario " + "  FROM	vPagoProveedor WITH(NOLOCK) " + " WHERE	ctipoPago = ? " + " AND nFolio = ? ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, getDocument());
            ps.setInt(2, getIdField());
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getString(1);
            else
                throw new Exception("No fue posible encontrar el beneficiario para el folio " + getIdField() + " en el documento" + getDocument());
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    private String getContrarecibo(Connection conn) throws Exception {
        String query = "SELECT canocontrarrecibo FROM " + getHeader() + " WITH(NOLOCK) WHERE " + getField() + " = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, getIdField());
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getString(1);
            else
                throw new Exception("No fue posible encontrar el contrarecibo para el folio " + getIdField() + " en la table " + getHeader());
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public String getCorreoAutorizaMasivo(Connection conn) throws Exception {
        StringBuilder queryMail = new StringBuilder();
        queryMail.append("SELECT	U_EMAIL ");
        queryMail.append("  FROM	CG_USUARIO WITH(NOLOCK) ");
        queryMail.append(" WHERE	cNumeroEmpleado =  ? ");
        queryMail.append("   AND	U_ESTATUS = 'A'");
        PreparedStatement psMail = null;
        ResultSet rsMail = null;
        try {
            int numeroEmpleadoAut = getNumEmpleadoAutMasivo(conn);
            int numeroEmpleado = numeroEmpleadoAut > 0 ? numeroEmpleadoAut : getAutNumEmpleadoMasivo(conn);
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

    public String getCorreoVistoBuenoMasivo(Connection conn) throws Exception {
        StringBuilder queryMail = new StringBuilder();
        queryMail.append("SELECT	U_EMAIL ");
        queryMail.append("  FROM	CG_USUARIO WITH(NOLOCK) ");
        queryMail.append(" WHERE	cNumeroEmpleado =  ? ");
        queryMail.append("   AND	U_ESTATUS = 'A'");
        PreparedStatement psMail = null;
        ResultSet rsMail = null;
        try {
            int numeroEmpleadoVoBo = getNumEmpleadoVistoBuenoMasivo(conn);
            int numeroEmpleado = numeroEmpleadoVoBo > 0 ? numeroEmpleadoVoBo : getVoBoNumEmpleadoMasivo(conn);
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

    public String getCorreoPrefirmaProfoem(Connection conn) throws Exception {
        PreparedStatement psMail = null;
        ResultSet rsMail = null;
        String queryMail = "select GP_VALOR U_EMAIL from CG_GRUPO_PROPIEDADES where GP_NOMBRE = 'CORREO_PREFIRMANTE' ";
        try {
            psMail = conn.prepareStatement(queryMail);
            rsMail = psMail.executeQuery();
            if (rsMail.next()) {
                if (StringUtils.isBlank(rsMail.getString("U_EMAIL")))
                    throw new Exception("No se ha definido correo de prefirma profoem. Notifique al administrador");
                return rsMail.getString("U_EMAIL");
            } else
                throw new Exception("No se encontro en la tabla CG_GRUPO_PROPIEDADES el valor para el CORREO_PREFIRMANTE");
        } finally {
            CloseObject.closeObject(psMail);
            CloseObject.closeObject(rsMail);
        }
    }

    public String getNombrePrefirmaProfoem(Connection conn) throws Exception {
        PreparedStatement psMail = null;
        ResultSet rsMail = null;
        String queryMail = "select GP_VALOR from CG_GRUPO_PROPIEDADES where GP_NOMBRE = 'NOMBRE_PREFIRMANTE_PROFOEM' ";
        try {
            psMail = conn.prepareStatement(queryMail);
            rsMail = psMail.executeQuery();
            if (rsMail.next()) {
                return rsMail.getString("GP_VALOR");
            } else
                throw new Exception("No se encontro en la tabla CG_GRUPO_PROPIEDADES el valor para el NOMBRE_PREFIRMANTE_PROFOEM");
        } finally {
            CloseObject.closeObject(psMail);
            CloseObject.closeObject(rsMail);
        }
    }

    @Override
    public String getCuerpoCorreoAutoriza(Connection conn) throws Exception {
        ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
        String urlAutorizacion = cabl.getSystemSetting("URL_SAI") + (isRefirma() ? "/egresos/RAutPago" : "/egresos/AutPago");
        String urlDescarga = generaURLDescarga(conn);
        String nombreCompleto = getAutNombre(conn);
        String puesto = getAutPuesto(conn);
        String cxp = getContrarecibo(conn);
        String beneficiario = getBeneficiario(conn);
        String concepto = getConcepto(conn);
        String importe = getImporteStr(conn);
        int numeroEmpleadoAut = getNumEmpleadoAutSuplencia(conn);
        int numeroEmpleado = (numeroEmpleadoAut > 0 ? numeroEmpleadoAut : getAutNumEmpleado(conn));
        String mailBody = "<html>";
        mailBody += "\n\t<head>";
        mailBody += "\n\t<meta charset=\"UTF-8\">";
        mailBody += "\n\t<style type=\"text/css\">";
        mailBody += "\n\tbody {";
        mailBody += "\n\t\t	font-family: verdana, arial, sans-serif;";
        mailBody += "\n\t\t	font-size: 12px;";
        mailBody += "\n\t}";
        mailBody += "\n\ttable {";
        mailBody += "\n\t\tfont-size: 12px;";
        mailBody += "\n\t\tcolor: #333333;";
        mailBody += "\n\t\tborder-width: 1px;";
        mailBody += "\n\t\tborder-color: #666666;";
        mailBody += "\n\t\tborder-collapse: collapse;";
        mailBody += "\n\t}";
        mailBody += "\n\ttable th {";
        mailBody += "\n\t\tborder-width: 1px;";
        mailBody += "\n\t\tpadding: 8px;";
        mailBody += "\n\t\tborder-style: solid;";
        mailBody += "\n\t\tborder-color: #666666;";
        mailBody += "\n\t\tbackground-color: #dedede;";
        mailBody += "\n\t}";
        mailBody += "\n\ttable td {";
        mailBody += "\n\t\tborder-width: 1px;";
        mailBody += "\n\t\tpadding: 8px;";
        mailBody += "\n\t\tborder-style: solid;";
        mailBody += "\n\t\tborder-color: #666666;";
        mailBody += "\n\t\tbackground-color: #ffffff;";
        mailBody += "\n\t}";
        mailBody += "\n\t</style>";
        mailBody += "</head>";
        mailBody += "\n\t<body>";
        mailBody += "\n\t\t<form id=\"Form\" name=\"FormViaticos\" >";
        mailBody += "	<b> C." + nombreCompleto + "</b>";
        mailBody += "	<br>";
        mailBody += "	<b>" + puesto + "</b>";
        mailBody += "	<br />";
        mailBody += "	<p>";
        mailBody += "		Se solicita de " + (isRefirma() ? "su firma de autorizacion nuevamente " : "su firma de autorizacion ") + "para el siguiente pago:";
        mailBody += "	</p>";
        mailBody += "	<table>";
        mailBody += "		<thead>";
        mailBody += "			<tr>";
        mailBody += "				<th>Cuenta Por Pagar</th>";
        mailBody += "				<th>Beneficiario</th>";
        mailBody += "				<th>Concepto</th>";
        mailBody += "				<th>Monto</th>";
        mailBody += "			</tr>";
        mailBody += "		</thead>";
        mailBody += "		<tbody>";
        mailBody += "<tr>";
        mailBody += "\n<td>" + cxp + "</td>";
        mailBody += "\n<td>" + beneficiario + "</td>";
        mailBody += "\n<td>" + concepto + "</td>";
        mailBody += "\n<td>" + importe + "</td>";
        mailBody += "</tr>";
        mailBody += "		</tbody>";
        mailBody += "	</table>";
        mailBody += "	<br />";
        mailBody += "	<br />";
        mailBody += generaLigaDoctos(conn, urlDescarga, numeroEmpleado, "Autorizacion").toString();
        mailBody += "\n<b>Para autorizar o rechazar esta solicitud por favor de click <a href=\"" + urlAutorizacion + generaAccessoAutToken(numeroEmpleado, getDocument(), getIdField()) + "\" > aquí </a>.</b>";
        mailBody += "	<br />";
        mailBody += "	<br />";
        mailBody += "	<p>";
        mailBody += "		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday();
        mailBody += "	</p>";
        mailBody += "	</form>";
        mailBody += "</body>";
        mailBody += "</html>";
        return mailBody;
    }

    public String getCuerpoCorreoAutorizaMasivo(Connection conn) throws Exception {
        String urlAutorizacion = ConfiguraAplicativoManager.getSystemSetting(conn, "URL_SAI") + "/egresos/AutPagoMasivo";
        String nombreCompleto = getAutNombreMasivo(conn);
        String puesto = getAutPuestoMasivo(conn);
        List<RelacionGastosEncabezado> pagos = getIntegradasCargaMasiva(conn);
        int numeroEmpleadoAut = getNumEmpleadoAutMasivo(conn);
        int numeroEmpleado = numeroEmpleadoAut > 0 ? numeroEmpleadoAut : getAutNumEmpleadoMasivo(conn);
        String mailBody = "<html>";
        mailBody += "\n\t<head>";
        mailBody += "\n\t<meta charset=\"UTF-8\">";
        mailBody += "\n\t<style type=\"text/css\">";
        mailBody += "\n\tbody {";
        mailBody += "\n\t\t	font-family: verdana, arial, sans-serif;";
        mailBody += "\n\t\t	font-size: 12px;";
        mailBody += "\n\t}";
        mailBody += "\n\ttable {";
        mailBody += "\n\t\tfont-size: 12px;";
        mailBody += "\n\t\tcolor: #333333;";
        mailBody += "\n\t\tborder-width: 1px;";
        mailBody += "\n\t\tborder-color: #666666;";
        mailBody += "\n\t\tborder-collapse: collapse;";
        mailBody += "\n\t}";
        mailBody += "\n\ttable th {";
        mailBody += "\n\t\tborder-width: 1px;";
        mailBody += "\n\t\tpadding: 8px;";
        mailBody += "\n\t\tborder-style: solid;";
        mailBody += "\n\t\tborder-color: #666666;";
        mailBody += "\n\t\tbackground-color: #dedede;";
        mailBody += "\n\t}";
        mailBody += "\n\ttable td {";
        mailBody += "\n\t\tborder-width: 1px;";
        mailBody += "\n\t\tpadding: 8px;";
        mailBody += "\n\t\tborder-style: solid;";
        mailBody += "\n\t\tborder-color: #666666;";
        mailBody += "\n\t\tbackground-color: #ffffff;";
        mailBody += "\n\t}";
        mailBody += "\n\t</style>";
        mailBody += "</head>";
        mailBody += "\n\t<body>";
        mailBody += "\n\t\t<form id=\"Form\" name=\"FormViaticos\" >";
        mailBody += "	<b> C." + nombreCompleto + "</b>";
        mailBody += "	<br>";
        mailBody += "	<b>" + puesto + "</b>";
        mailBody += "	<br />";
        mailBody += "	<p>";
        mailBody += "		Se solicita de su autorización de la carga masiva de pagos que contiene:";
        mailBody += "	</p>";
        mailBody += "	<table>";
        mailBody += "		<thead>";
        mailBody += "			<tr>";
        mailBody += "				<th>Cuenta Por Pagar</th>";
        mailBody += "				<th>Beneficiario</th>";
        mailBody += "				<th>Concepto</th>";
        mailBody += "				<th>Monto</th>";
        mailBody += "			</tr>";
        mailBody += "		</thead>";
        mailBody += "		<tbody>";
        for (RelacionGastosEncabezado rgEnc : pagos) {
            mailBody += "<tr>";
            mailBody += "\n<td>" + rgEnc.getCaNoContrarrecibo() + "</td>";
            mailBody += "\n<td>" + rgEnc.getcIdRFC() + "-" + rgEnc.getCnombre() + "</td>";
            mailBody += "\n<td>" + rgEnc.getcConcepto() + "</td>";
            mailBody += "\n<td>" + rgEnc.getmImporteMasIva() + "</td>";
            mailBody += "</tr>";
        }
        mailBody += "		</tbody>";
        mailBody += "	</table>";
        mailBody += "	<br />";
        mailBody += "	<br />";
        mailBody += "\n<b>Para autorizar o rechazar esta solicitud por favor de click <a href=\"" + urlAutorizacion + generaAccessoAutToken(numeroEmpleado, getDocument(), getMasiveID()) + "\" > aquí </a>.</b>";
        mailBody += "	<br />";
        mailBody += "	<br />";
        mailBody += "	<p>";
        mailBody += "		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday();
        mailBody += "	</p>";
        mailBody += "	</form>";
        mailBody += "</body>";
        mailBody += "</html>";
        return mailBody;
    }

    public String getCuerpoCorreoVistoBueno(Connection conn) throws Exception {
        String urlAutorizacion = ConfiguraAplicativoManager.getSystemSetting(conn, "URL_SAI") + (isRefirma() ? "/egresos/RVoBoPago" : "/egresos/VoBoPago");
        String urlDescarga = generaURLDescarga(conn);
        String nombreCompleto = getVoBoNombre(conn);
        String puesto = getVoBoPuesto(conn);
        String cxp = getContrarecibo(conn);
        String beneficiario = getBeneficiario(conn);
        String concepto = getConcepto(conn);
        String importe = getImporteStr(conn);
        int numeroEmpleadoVoBo = getNumEmpleadoVistoBueno(conn);
        int numeroEmpleado = numeroEmpleadoVoBo > 0 ? numeroEmpleadoVoBo : getVoBoNumEmpleado(conn);
        StringBuilder mailBody = new StringBuilder();
        mailBody.append("<html>");
        mailBody.append("\n\t<head>");
        mailBody.append("\n\t<meta charset=\"UTF-8\">");
        mailBody.append("\n\t<style type=\"text/css\">");
        mailBody.append("\n\tbody {");
        mailBody.append("\n\t\t	font-family: verdana, arial, sans-serif;");
        mailBody.append("\n\t\t	font-size: 12px;");
        mailBody.append("\n\t}");
        mailBody.append("\n\ttable {");
        mailBody.append("\n\t\tfont-size: 12px;");
        mailBody.append("\n\t\tcolor: #333333;");
        mailBody.append("\n\t\tborder-width: 1px;");
        mailBody.append("\n\t\tborder-color: #666666;");
        mailBody.append("\n\t\tborder-collapse: collapse;");
        mailBody.append("\n\t}");
        mailBody.append("\n\ttable th {");
        mailBody.append("\n\t\tborder-width: 1px;");
        mailBody.append("\n\t\tpadding: 8px;");
        mailBody.append("\n\t\tborder-style: solid;");
        mailBody.append("\n\t\tborder-color: #666666;");
        mailBody.append("\n\t\tbackground-color: #dedede;");
        mailBody.append("\n\t}");
        mailBody.append("\n\ttable td {");
        mailBody.append("\n\t\tborder-width: 1px;");
        mailBody.append("\n\t\tpadding: 8px;");
        mailBody.append("\n\t\tborder-style: solid;");
        mailBody.append("\n\t\tborder-color: #666666;");
        mailBody.append("\n\t\tbackground-color: #ffffff;");
        mailBody.append("\n\t}");
        mailBody.append("\n\t</style>");
        mailBody.append("</head>");
        mailBody.append("\n\t<body>");
        mailBody.append("\n\t\t<form id=\"Form\" name=\"FormViaticos\" >");
        mailBody.append("	<b> C." + nombreCompleto + "</b>");
        mailBody.append("	<br>");
        mailBody.append("	<b>" + puesto + "</b>");
        mailBody.append("	<br />");
        mailBody.append("	<p>");
        mailBody.append("		Se solicita de " + (isRefirma() ? "su firma de visto bueno nuevamente " : "su firma de visto bueno ") + "para el siguiente pago:");
        mailBody.append("	</p>");
        mailBody.append("	<table>");
        mailBody.append("		<thead>");
        mailBody.append("			<tr>");
        mailBody.append("				<th>Cuenta Por Pagar</th>");
        mailBody.append("				<th>Beneficiario</th>");
        mailBody.append("				<th>Concepto</th>");
        mailBody.append("				<th>Monto</th>");
        mailBody.append("			</tr>");
        mailBody.append("		</thead>");
        mailBody.append("		<tbody>");
        mailBody.append("<tr>");
        mailBody.append("\n<td>" + cxp + "</td>");
        mailBody.append("\n<td>" + beneficiario + "</td>");
        mailBody.append("\n<td>" + concepto + "</td>");
        mailBody.append("\n<td>" + importe + "</td>");
        mailBody.append("</tr>");
        mailBody.append("		</tbody>");
        mailBody.append("	</table>");
        mailBody.append(generaLigaDoctos(conn, urlDescarga, numeroEmpleado, "Visto Bueno"));
        mailBody.append("\n<b>Para autorizar o rechazar esta solicitud por favor de click <a href=\"" + urlAutorizacion + generaAccessoAutToken(numeroEmpleado, getDocument(), getIdField()) + "\" > aquí </a>.</b>");
        mailBody.append("	<br />");
        mailBody.append("	<br />");
        mailBody.append("	<p>");
        mailBody.append("		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday());
        mailBody.append("	</p>");
        mailBody.append("	</form>");
        mailBody.append("</body>");
        mailBody.append("</html>");
        return mailBody.toString();
    }

    public String getCuerpoCorreoPrefirma(Connection conn) throws Exception {
        String nombreCompleto = getNombrePrefirmaProfoem(conn);
        String cxp = getContrarecibo(conn);
        String beneficiario = getBeneficiario(conn);
        String concepto = getConcepto(conn);
        String importe = getImporteStr(conn);
        StringBuilder mailBody = new StringBuilder();
        mailBody.append("<html>");
        mailBody.append("\n\t<head>");
        mailBody.append("\n\t<meta charset=\"UTF-8\">");
        mailBody.append("\n\t<style type=\"text/css\">");
        mailBody.append("\n\tbody {");
        mailBody.append("\n\t\t	font-family: verdana, arial, sans-serif;");
        mailBody.append("\n\t\t	font-size: 12px;");
        mailBody.append("\n\t}");
        mailBody.append("\n\ttable {");
        mailBody.append("\n\t\tfont-size: 12px;");
        mailBody.append("\n\t\tcolor: #333333;");
        mailBody.append("\n\t\tborder-width: 1px;");
        mailBody.append("\n\t\tborder-color: #666666;");
        mailBody.append("\n\t\tborder-collapse: collapse;");
        mailBody.append("\n\t}");
        mailBody.append("\n\ttable th {");
        mailBody.append("\n\t\tborder-width: 1px;");
        mailBody.append("\n\t\tpadding: 8px;");
        mailBody.append("\n\t\tborder-style: solid;");
        mailBody.append("\n\t\tborder-color: #666666;");
        mailBody.append("\n\t\tbackground-color: #dedede;");
        mailBody.append("\n\t}");
        mailBody.append("\n\ttable td {");
        mailBody.append("\n\t\tborder-width: 1px;");
        mailBody.append("\n\t\tpadding: 8px;");
        mailBody.append("\n\t\tborder-style: solid;");
        mailBody.append("\n\t\tborder-color: #666666;");
        mailBody.append("\n\t\tbackground-color: #ffffff;");
        mailBody.append("\n\t}");
        mailBody.append("\n\t</style>");
        mailBody.append("</head>");
        mailBody.append("\n\t<body>");
        mailBody.append("\n\t\t<form id=\"Form\" name=\"FormViaticos\" >");
        mailBody.append("	<b> C." + nombreCompleto + "</b>");
        mailBody.append("	<br />");
        mailBody.append("	<p>");
        mailBody.append("		Se solicita de " + (isRefirma() ? "su firma de revisión nuevamente " : "su firma de revisión ") + "para el siguiente pago:");
        mailBody.append("	</p>");
        mailBody.append("	<table>");
        mailBody.append("		<thead>");
        mailBody.append("			<tr>");
        mailBody.append("				<th>Cuenta Por Pagar</th>");
        mailBody.append("				<th>Beneficiario</th>");
        mailBody.append("				<th>Concepto</th>");
        mailBody.append("				<th>Monto</th>");
        mailBody.append("			</tr>");
        mailBody.append("		</thead>");
        mailBody.append("		<tbody>");
        mailBody.append("<tr>");
        mailBody.append("\n<td>" + cxp + "</td>");
        mailBody.append("\n<td>" + beneficiario + "</td>");
        mailBody.append("\n<td>" + concepto + "</td>");
        mailBody.append("\n<td>" + importe + "</td>");
        mailBody.append("</tr>");
        mailBody.append("		</tbody>");
        mailBody.append("	</table>");
        mailBody.append("	<br />");
        mailBody.append("\n<b>Para autorizar o rechazar entrar en el menú de Documentos / Revisión de Pagos Masivos.</b>");
        mailBody.append("	<br />");
        mailBody.append("	<br />");
        mailBody.append("	<p>");
        mailBody.append("		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday());
        mailBody.append("	</p>");
        mailBody.append("	</form>");
        mailBody.append("</body>");
        mailBody.append("</html>");
        return mailBody.toString();
    }

    public String getCuerpoCorreoVistoBuenoMasivo(Connection conn) throws Exception {
        String urlAutorizacion = ConfiguraAplicativoManager.getSystemSetting(conn, "URL_SAI") + "/egresos/VoBoPagoMasivo";
        String nombreCompleto = getVoBoNombreMasivo(conn);
        String puesto = getVoBoPuestoMasivo(conn);
        List<RelacionGastosEncabezado> pagos = getIntegradasCargaMasiva(conn);
        int numeroEmpleadoVoBo = getNumEmpleadoVistoBuenoMasivo(conn);
        int numeroEmpleado = numeroEmpleadoVoBo > 0 ? numeroEmpleadoVoBo : getVoBoNumEmpleadoMasivo(conn);
        String mailBody = "<html>";
        mailBody += "\n\t<head>";
        mailBody += "\n\t<meta charset=\"UTF-8\">";
        mailBody += "\n\t<style type=\"text/css\">";
        mailBody += "\n\tbody {";
        mailBody += "\n\t\t	font-family: verdana, arial, sans-serif;";
        mailBody += "\n\t\t	font-size: 12px;";
        mailBody += "\n\t}";
        mailBody += "\n\ttable {";
        mailBody += "\n\t\tfont-size: 12px;";
        mailBody += "\n\t\tcolor: #333333;";
        mailBody += "\n\t\tborder-width: 1px;";
        mailBody += "\n\t\tborder-color: #666666;";
        mailBody += "\n\t\tborder-collapse: collapse;";
        mailBody += "\n\t}";
        mailBody += "\n\ttable th {";
        mailBody += "\n\t\tborder-width: 1px;";
        mailBody += "\n\t\tpadding: 8px;";
        mailBody += "\n\t\tborder-style: solid;";
        mailBody += "\n\t\tborder-color: #666666;";
        mailBody += "\n\t\tbackground-color: #dedede;";
        mailBody += "\n\t}";
        mailBody += "\n\ttable td {";
        mailBody += "\n\t\tborder-width: 1px;";
        mailBody += "\n\t\tpadding: 8px;";
        mailBody += "\n\t\tborder-style: solid;";
        mailBody += "\n\t\tborder-color: #666666;";
        mailBody += "\n\t\tbackground-color: #ffffff;";
        mailBody += "\n\t}";
        mailBody += "\n\t</style>";
        mailBody += "</head>";
        mailBody += "\n\t<body>";
        mailBody += "\n\t\t<form id=\"Form\" name=\"FormViaticos\" >";
        mailBody += "	<b> C." + nombreCompleto + "</b>";
        mailBody += "	<br>";
        mailBody += "	<b>" + puesto + "</b>";
        mailBody += "	<br />";
        mailBody += "	<p>";
        mailBody += "		Se solicita de su Visto Bueno de la carga masiva de pagos que contiene:";
        mailBody += "	</p>";
        mailBody += "	<table>";
        mailBody += "		<thead>";
        mailBody += "			<tr>";
        mailBody += "				<th>Cuenta Por Pagar</th>";
        mailBody += "				<th>Beneficiario</th>";
        mailBody += "				<th>Concepto</th>";
        mailBody += "				<th>Monto</th>";
        mailBody += "			</tr>";
        mailBody += "		</thead>";
        mailBody += "		<tbody>";
        for (RelacionGastosEncabezado rgEnc : pagos) {
            mailBody += "<tr>";
            mailBody += "\n<td>" + rgEnc.getCaNoContrarrecibo() + "</td>";
            mailBody += "\n<td>" + rgEnc.getcIdRFC() + "-" + rgEnc.getCnombre() + "</td>";
            mailBody += "\n<td>" + rgEnc.getcConcepto() + "</td>";
            mailBody += "\n<td>" + rgEnc.getmImporteMasIva() + "</td>";
            mailBody += "</tr>";
        }
        mailBody += "		</tbody>";
        mailBody += "	</table>";
        mailBody += "	<br />";
        mailBody += "	<br />";
        mailBody += "\n<b>Para autorizar o rechazar esta solicitud por favor de click <a href=\"" + urlAutorizacion + generaAccessoAutToken(numeroEmpleado, getDocument(), getMasiveID()) + "\" > aquí </a>.</b>";
        mailBody += "	<br />";
        mailBody += "	<br />";
        mailBody += "	<p>";
        mailBody += "		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday();
        mailBody += "	</p>";
        mailBody += "	</form>";
        mailBody += "</body>";
        mailBody += "</html>";
        return mailBody;
    }

    @Override
    public String getImporteStr(Connection conn) throws Exception {
        String query = "SELECT	mImporteMasIva " + "  FROM	vPagoProveedor WITH(NOLOCK) " + " WHERE	ctipoPago = ? " + " AND nFolio = ? ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, getDocument());
            ps.setInt(2, getIdField());
            rs = ps.executeQuery();
            if (rs.next())
                return Util.formatNumber(rs.getBigDecimal(1));
            else
                throw new Exception("No fue posible encontrar el beneficiario para el folio " + getIdField() + " en el documento" + getDocument());
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    private List<RelacionGastosEncabezado> getIntegradasCargaMasiva(Connection conn) throws Exception {
        List<RelacionGastosEncabezado> rg = new ArrayList<RelacionGastosEncabezado>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT	caNoContrarrecibo,");
        query.append("      	cIdRFC, ");
        query.append("      	cnombre, ");
        query.append("      	cConcepto, ");
        query.append("      	mImporteMasIva ");
        query.append("  FROM	tRELACIONGASTOSEncabezado WITH(nolock) ");
        query.append(" WHERE	nFolioCargaMasiva = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, getMasiveID());
            rs = ps.executeQuery();
            while (rs.next()) {
                RelacionGastosEncabezado rgAux = new RelacionGastosEncabezado();
                rgAux.setCaNoContrarrecibo(rs.getString("caNoContrarrecibo"));
                rgAux.setcIdRFC(rs.getString("cIdRFC"));
                rgAux.setCnombre(rs.getString("cnombre"));
                rgAux.setcConcepto(rs.getString("cConcepto"));
                rgAux.setmImporteMasIva(rs.getBigDecimal("mImporteMasIva"));
                rg.add(rgAux);
            }
            return rg;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public int getNumEmpleadoAutMasivo(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	TOP 1 cNumeroEmpleado ");
        query.append("  FROM	tPagoFirmanteDelegatorioAut  ");
        query.append(" WHERE	cTipoPago = ?  ");
        query.append("   AND	nFolioPago IN (");
        query.append("     	SELECT	nFolioRelacionGastos  ");
        query.append("     	  FROM	tRELACIONGASTOSEncabezado WITH(nolock) ");
        query.append("		 	 WHERE	nFolioCargaMasiva = ?");
        query.append("   )");
        PreparedStatement ps = null;
        ResultSet rs = null;
        int numeroEmpleado = -1;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, getDocument());
            ps.setInt(2, getMasiveID());
            rs = ps.executeQuery();
            if (rs.next())
                numeroEmpleado = rs.getInt(1);
            return numeroEmpleado;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public int getNumEmpleadoVistoBuenoMasivo(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	TOP 1 cNumeroEmpleado ");
        query.append("  FROM	tPagoFirmanteDelegatorioVoBo  ");
        query.append(" WHERE	cTipoPago = ?  ");
        query.append("   AND	nFolioPago IN (");
        query.append("     	SELECT	nFolioRelacionGastos  ");
        query.append("     	  FROM	tRELACIONGASTOSEncabezado WITH(nolock) ");
        query.append("		 	 WHERE	nFolioCargaMasiva = ?");
        query.append("   )");
        PreparedStatement ps = null;
        ResultSet rs = null;
        int numeroEmpleado = -1;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, getDocument());
            ps.setInt(2, getMasiveID());
            rs = ps.executeQuery();
            if (rs.next())
                numeroEmpleado = rs.getInt(1);
            return numeroEmpleado;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    @Override
    public String getVoBoLegend(Connection conn) throws Exception {
        if (tieneDelegatorioVoBO(conn)) {
            String voLegend = VO_BO_LEGEND_PREFIX + ". Firma " + getTipoSuplencia() + " de " + getNombreEmpleadoSuplido() + " con fundamento en el oficio: " + getFolioOficioVoBo() + " de fecha: " + getFechaOficioVoBo();
            log.info(voLegend);
            return voLegend;
        } else
            return VO_BO_LEGEND_PREFIX;
    }

    public String getVoBoLegendWithName(Connection conn, String nombre, String puesto) throws Exception {
        String voLegend = VO_BO_LEGEND_PREFIX.concat(". Firmado por: ").concat(nombre).concat(" | ").concat(puesto);
        if (tieneDelegatorioVoBO(conn)) {
            voLegend = VO_BO_LEGEND_PREFIX + " Firma " + getTipoSuplencia() + " de " + getNombreEmpleadoSuplido() + " con fundamento en el oficio: " + getFolioOficioVoBo() + " de fecha: " + getFechaOficioVoBo();
            log.info(voLegend);
        }
        return voLegend;
    }

    public String getVoBoNombreMasivo(Connection conn) throws Exception {
        StringBuilder queryNombre = new StringBuilder();
        queryNombre.append("SELECT	U_NOMBRE ");
        queryNombre.append("  FROM	CG_USUARIO WITH(NOLOCK) ");
        queryNombre.append(" WHERE	cNumeroEmpleado =  ? ");
        queryNombre.append("   AND	U_ESTATUS = 'A'");
        PreparedStatement psNombre = null;
        ResultSet rsNombre = null;
        try {
            int numeroEmpleadoVoBo = getNumEmpleadoVistoBuenoMasivo(conn);
            int numeroEmpleado = numeroEmpleadoVoBo > 0 ? numeroEmpleadoVoBo : getVoBoNumEmpleadoMasivo(conn);
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

    public int getVoBoNumEmpleadoMasivo(Connection conn) throws Exception {
        StringBuilder queryPago = new StringBuilder();
        queryPago.append("SELECT	TOP 1 nNumEmpleadoVoBo ");
        queryPago.append("  FROM	").append(getHeader()).append(" WITH(NOLOCK) ");
        queryPago.append(" WHERE	nFolioCargaMasiva = ? ");
        PreparedStatement psPago = null;
        ResultSet rsPago = null;
        try {
            int numeroEmpleado = -1;
            psPago = conn.prepareStatement(queryPago.toString());
            psPago.setInt(1, getMasiveID());
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

    public String getVoBoPuestoMasivo(Connection conn) throws Exception {
        StringBuilder queryPuesto = new StringBuilder();
        queryPuesto.append("SELECT	CARGO ");
        queryPuesto.append("  FROM	v_empleados_giro WITH(NOLOCK) ");
        queryPuesto.append(" WHERE	CLAVE = ?");
        PreparedStatement psPuesto = null;
        ResultSet rsPuesto = null;
        try {
            int numeroEmpleadoVoBo = getNumEmpleadoVistoBuenoMasivo(conn);
            int numeroEmpleado = numeroEmpleadoVoBo > 0 ? numeroEmpleadoVoBo : getVoBoNumEmpleadoMasivo(conn);
            psPuesto = conn.prepareStatement(queryPuesto.toString());
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

    @Override
    public void notificaOperacionMasivaPendiente(Connection conn, String tipoAutorizacion) throws Exception {
        if (VO_BO.equals(tipoAutorizacion)) {
            AlarmaManager.procesaAlarmaCNF(conn, "", null, null, "Solicitud de vistobueno de Carga Masiva", getCorreoVistoBuenoMasivo(conn), getCuerpoCorreoVistoBuenoMasivo(conn));
        }
        if (AUTORIZA.equals(tipoAutorizacion)) {
            AlarmaManager.procesaAlarmaCNF(conn, "", null, null, "Solicitud de Autorizaci\u00F3n de Carga Masiva", getCorreoAutorizaMasivo(conn), getCuerpoCorreoAutorizaMasivo(conn));
        }
    }

    public String notificaOperacionPendiente(Connection conn, String tipoAutorizacion) throws Exception {
        String subjet = "", subjetA = "";
        if (VO_BO.equals(tipoAutorizacion) || R_VO_BO.equals(tipoAutorizacion)) {
            subjet = "Solicitud de " + (isRefirma() ? "refirma de " : "") + "visto bueno de pago";
            AlarmaManager.procesaAlarmaCNF(conn, "", null, null, subjet, getCorreoVistoBueno(conn), getCuerpoCorreoVistoBueno(conn));
            FirmaElectronicaManager.avanzaEstatusSICOP(conn, this, (isRefirma() ? SolicitudFirmaElectronica.R_VO_BO_SICOP : SolicitudFirmaElectronica.VO_BO_SICOP));
        }
        if (AUTORIZA.equals(tipoAutorizacion) || R_AUT.equals(tipoAutorizacion)) {
            subjetA = "Solicitud de " + (isRefirma() ? "refirma de " : "") + "autorizacion de pago";
            AlarmaManager.procesaAlarmaCNF(conn, "", null, null, subjetA, getCorreoAutoriza(conn), getCuerpoCorreoAutoriza(conn));
        }
        return "success";
    }

    public String notificaPrefirmante(Connection conn) throws Exception {
        String subjet = "Solicitud de Validacion de Pago PROFOEM.";
        AlarmaManager.procesaAlarmaCNF(conn, "", null, null, subjet, getCorreoPrefirmaProfoem(conn), getCuerpoCorreoPrefirma(conn));
        FirmaElectronicaManager.avanzaEstatusSICOP(conn, this, (isRefirma() ? SolicitudFirmaElectronica.WAIT_FOR_MANAGER_AUTH : SolicitudFirmaElectronica.WAIT_FOR_MANAGER_AUTH));
        return "success";
    }

    @Override
    public void onCancelaTramite(Connection conn, String reason) throws Exception {
        FirmaElectronicaManager.registraBitacoraCancelacion(conn, reason, this);
    }

    @Override
    public void onFinishAut(Connection conn) {
        if ("OPERAJENAS".equalsIgnoreCase(getDocument()))
            try {
                FirmaElectronicaManager.avanzaEstatusSICOP(conn, this, SolicitudFirmaElectronica.LAYOUT_GENERADO);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        else if ("RELACIONGASTOS".equals(getDocument())) {
            if (isCargaMasiva())
                try {
                    CargaMasivaRG cargaMasivaRG = new CargaMasivaRG(getMasiveID(), CargaMasivaRG.CARGA_AUT);
                    RelacionGastosManager.updateCargaMasivaRG(conn, cargaMasivaRG);
                } catch (SQLException e) {
                    throw new RuntimeException(e.toString(), e.getCause());
                }
        } else if ("PAGODIVERSO".equals(getDocument())) {
            try {
                boolean isAlternativeSite = "true".equalsIgnoreCase(ConfiguraAplicativoManager.getSystemSetting(conn, "SAI_AMBIENTAL"));
                EgresoPAGODIVERSOEncabezado header = PagosDiversosManager.cargaEncabezado(conn, getIdField());
                if (header.isCargaMasiva() && isAlternativeSite) {
                    FirmaElectronicaManager.avanzaEstatusSICOP(conn, this, SolicitudFirmaElectronica.WAIT_FOR_MANAGER_AUTH);
                }
            } catch (Exception e) {
                throw new RuntimeException("Problemas determinando pago diverso masivo y autorizadores." + e.toString(), e);
            }
        }
    }

    @Override
    public void onFinishVoBo(Connection conn) {
        if (isCargaMasiva())
            if ("RELACIONGASTOS".equals(getDocument())) {
                try {
                    CargaMasivaRG cargaMasivaRG = new CargaMasivaRG(getMasiveID(), CargaMasivaRG.CARGA_WAIT_AUT);
                    RelacionGastosManager.updateCargaMasivaRG(conn, cargaMasivaRG);
                } catch (SQLException e) {
                    throw new RuntimeException(e.toString(), e.getCause());
                }
            }
    }

    @Override
    public void onGeneraArchivosMasivo(Connection conn) {
        if ("RELACIONGASTOS".equals(getDocument())) {
            try {
                CargaMasivaRG cargaMasivaRG = new CargaMasivaRG(getMasiveID(), CargaMasivaRG.CARGA_WAIT_VO_BO);
                RelacionGastosManager.updateCargaMasivaRG(conn, cargaMasivaRG);
            } catch (SQLException e) {
                throw new RuntimeException(e.toString(), e.getCause());
            }
        }
    }
}
