package com.axtel.contratos;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.axtel.contratos.core.ContractQuestionnaire;
import com.axtel.contratos.core.QuestionnaireManager;
import com.axtel.contratos.core.RequisitionManager;
import com.axtel.contratos.exception.ContratoException;
import com.syc.cfdi.db.CloseObject;
import com.syc.contable.AccountingEngine;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.firmante.core.FirmanteManager;
import com.syc.egresos.firmante.servlet.Firmante;
import com.syc.fortimax.core.Carpeta;
import com.syc.fortimax.core.CarpetaManager;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.fortimax.core.Pagina;
import com.syc.fortimax.core.Volumen;
import com.syc.fortimax.core.VolumenManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.firmaElectronica.exceptions.AutRecepcionMaterialException;
import com.syc.sai.firmaElectronica.exceptions.FirmaElectronicaException;
import com.syc.sai.firmaElectronica.exceptions.NotEmptyDocumentException;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class QuestionnaireBussinessLogic extends SolicitudFirmaElectronica {

    class RequestComponents {

        private int consecutive;

        private String type;

        private String unit;

        public RequestComponents(String idRequest) {
            String[] components = idRequest.split("-");
            this.type = components[0];
            this.unit = components[1];
            this.consecutive = Integer.parseInt(components[2]);
        }

        /**
         * @return the consecutive
         */
        public int getConsecutive() {
            return consecutive;
        }

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * @return the unit
         */
        public String getUnit() {
            return unit;
        }

        /**
         * @param consecutive
         *            the consecutive to set
         */
        public void setConsecutive(int consecutive) {
            this.consecutive = consecutive;
        }

        /**
         * @param type
         *            the type to set
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * @param unit
         *            the unit to set
         */
        public void setUnit(String unit) {
            this.unit = unit;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(QuestionnaireBussinessLogic.class);

    private DataSourceManager ds = null;

    private String motivoRechazo;

    private ContractQuestionnaire questionnaire;

    private Requisition contractRequisition;

    private Map<String, String> reportNames;

    private Map<String, Object> reportParams;

    private String reportTemplate;

    private Firmante signatory;

    private boolean standAlone;

    public QuestionnaireBussinessLogic() {
        setReportNames();
        ds = new DataSourceManager() {
        };
        ds.init(GestionInterface.ATT_CONEXION);
    }

    public QuestionnaireBussinessLogic(String jniName, ContractQuestionnaire questionnaire) {
        ds = new DataSourceManager() {
        };
        ds.init(jniName);
        this.questionnaire = questionnaire;
        if (questionnaire.isApplyQuestionnaire()) {
            setReportNames();
        } else {
            setReportName();
        }
    }

    public QuestionnaireBussinessLogic(boolean standAlone) {
        this.standAlone = standAlone;
        setReportNames();
    }

    @Override
    public String generaArchivoFirma(Connection conn, Volumen vol, Carpeta folder, boolean isSignedCopy) throws FirmaElectronicaException {
        String filename = null;
        try {
            filename = generaArchivoFirma(conn, vol, folder, getReportParams(), "FIEL", getReportTemplate(), false);
        } catch (NotEmptyDocumentException nede) {
            log.warn("Object: {}", "El documento no esta vacio. Se ignora" + nede);
        } catch (Exception e) {
            throw new FirmaElectronicaException(e);
        }
        return filename;
    }

    public List<String> generaArchivos(Connection conn) throws AutRecepcionMaterialException {
        List<String> fileNames = new ArrayList<>();
        try {
            Volumen vol = VolumenManager.getVolumen(conn);
            Carpeta folder = CarpetaManager.getCarpetaByName(conn, getDocument(), getQuestionnaire().getCabinetId(), getFolder());
            if (folder == null)
                folder = createFolder(conn, getQuestionnaire().getCabinetId());
            String docFile = "";
            for (Iterator<String> i = getReportNames().keySet().iterator(); i.hasNext(); ) {
                setDocName(i.next());
                if (DocumentoManager.existeDocumentoCapturado(conn, folder.getTituloAplicacion(), folder.getIdGabinete(), folder.getIdCarpeta(), getDocName())) {
                    docFile = DocumentoManager.getDocumento(conn, folder.getTituloAplicacion(), folder.getIdGabinete(), folder.getIdCarpeta(), getDocName()).getFullPathFilesNames()[0];
                } else {
                    setReportTemplate(getReportNames().get(getDocName()));
                    if ("Comprobante".equals(getDocName()))
                        docFile = genRequestFile(conn, vol, folder);
                    else
                        docFile = genQuestionnaireFile(conn, vol, folder);
                }
                fileNames.add(docFile);
            }
            return fileNames;
        } catch (SQLException e) {
            throw new AutRecepcionMaterialException("Error de base de datos al generar el documento: " + e.toString(), e);
        } catch (Exception e) {
            throw new AutRecepcionMaterialException("Error al generar el documento: " + e.toString(), e);
        }
    }

    private StringBuilder generaLigaDoctos(Connection conn, String urlDescarga, String msgTable) throws Exception {
        StringBuilder expedientBody = new StringBuilder();
        expedientBody.append("	<br />");
        expedientBody.append("	<p>");
        //"	En la tabla siguiente puede revisar el documento que sera firmado."
        expedientBody.append(msgTable);
        expedientBody.append("	</p>");
        expedientBody.append("<table id=\"docsTbl\">");
        expedientBody.append("		<thead>");
        expedientBody.append("			<tr>");
        expedientBody.append("				<th>Archivo a Firmar</th>");
        expedientBody.append("				<th>Motivo</th>");
        expedientBody.append("			</tr>");
        expedientBody.append("		</thead>");
        expedientBody.append("		<tbody>");
        for (Iterator<String> i = getReportNames().keySet().iterator(); i.hasNext(); ) {
            setDocName(i.next());
            expedientBody.append("			<tr>");
            expedientBody.append("				<td>");
            expedientBody.append("				<a href=\"" + urlDescarga + generaRutaArchivo(conn, getDocument(), getIdField()) + "\" > ").append(getDocName()).append("</a>");
            expedientBody.append("				</td>");
            expedientBody.append("				<td>");
            expedientBody.append("				Firma de Autorizaci&oacute;n");
            expedientBody.append("				</td>");
            expedientBody.append("			</tr>");
        }
        expedientBody.append("</table>");
        expedientBody.append("	<br />");
        expedientBody.append("	<br />");
        return expedientBody;
    }

    private String generaRutaArchivo(Connection conn, String document, int idField) throws Exception {
        Caso c = CasoManager.findByFolioLike(conn, document, String.valueOf(idField));
        Documento documento = DocumentoManager.buscaDocumento(conn, document, c.getIdGabinete(), getDocName());
        StringBuilder fortimaxNode = new StringBuilder(document).append("_").append("G").append(c.getIdGabinete()).append("C").append(documento.getIdCarpetaPadre()).append("D").append(documento.getIdDocumento());
        StringBuilder parametrosReales = null;
        /*
		 * Concatena los parametros. El separador sera el caracter | (pipe)
		 */
        parametrosReales = new StringBuilder("?");
        parametrosReales.append("fortimax=").append(fortimaxNode);
        log.debug("Object: {}", "Cadena generada: " + parametrosReales.toString());
        return parametrosReales.toString();
    }

    private String genQuestionnaireFile(Connection conn, Volumen vol, Carpeta folder) throws ContratoException {
        try {
            Map<String, Object> questionnaireParams = new HashMap<String, Object>();
            questionnaireParams.put("cIdSolicitud", "'" + getQuestionnaire().getIdRequest() + "'");
            questionnaireParams.put("SUBREPORT_DIR", getReportPath());
            setReportParams(questionnaireParams);
            return generaArchivoFirma(conn, vol, folder, false);
        } catch (Exception e) {
            throw new ContratoException(e);
        }
    }

    private String genRequestFile(Connection conn, Volumen vol, Carpeta folder) throws ContratoException {
        try {
            RequestComponents components = new RequestComponents(getQuestionnaire().getIdRequest());
            String fiscalYear = EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal();
            Map<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("cEjercicio", fiscalYear);
            requestParams.put("cIdUnidadEjecutora", components.getUnit());
            requestParams.put("nIdConsecutivo", String.valueOf(components.getConsecutive()));
            requestParams.put("cIdTipoSolicitud", components.getType());
            requestParams.put("formato", ContractQuestionnaire.FORMAT);
            requestParams.put("SUBREPORT_DIR", getReportPath());
            setReportParams(requestParams);
            return generaArchivoFirma(conn, vol, folder, false);
        } catch (Exception e) {
            throw new ContratoException(e);
        }
    }

    @Override
    public String getAutLegend(Connection conn) throws Exception {
        return null;
    }

    private Connection getConnection() throws SQLException {
        return this.ds.getConnection();
    }

    @Override
    public String getCuerpoCorreoAutoriza(Connection conn) throws Exception {
        String urlAutorizacion = ConfiguraAplicativoManager.getSystemSetting(conn, "URL_SAI") + "/egresos/AutRequisicion";
        String urlDescarga = generaURLDescarga(conn);
        int folioRequisition = Util.readNumericID(getContractRequisition().getFolioApartado());
        setIdField(folioRequisition);
        StringBuilder mailBody = new StringBuilder("<html>");
        mailBody.append("\n\t<head> ");
        mailBody.append("\n\t<meta charset=\"UTF-8\"> ");
        mailBody.append("\n\t<style type=\"text/css\"> ");
        mailBody.append("\n\tbody { ");
        mailBody.append("\n\t\t	font-family: verdana, arial, sans-serif; ");
        mailBody.append("\n\t\t	font-size: 12px; ");
        mailBody.append("\n\t} ");
        mailBody.append("\n\ttable { ");
        mailBody.append("\n\t\tfont-size: 12px; ");
        mailBody.append("\n\t\tcolor: #333333; ");
        mailBody.append("\n\t\tborder-width: 1px; ");
        mailBody.append("\n\t\tborder-color: #666666; ");
        mailBody.append("\n\t\tborder-collapse: collapse; ");
        mailBody.append("\n\t} ");
        mailBody.append("\n\ttable th { ");
        mailBody.append("\n\t\tborder-width: 1px; ");
        mailBody.append("\n\t\tpadding: 8px; ");
        mailBody.append("\n\t\tborder-style: solid; ");
        mailBody.append("\n\t\tborder-color: #666666; ");
        mailBody.append("\n\t\tbackground-color: #dedede; ");
        mailBody.append("\n\t} ");
        mailBody.append("\n\ttable td { ");
        mailBody.append("\n\t\tborder-width: 1px; ");
        mailBody.append("\n\t\tpadding: 8px; ");
        mailBody.append("\n\t\tborder-style: solid; ");
        mailBody.append("\n\t\tborder-color: #666666; ");
        mailBody.append("\n\t\tbackground-color: #ffffff; ");
        mailBody.append("\n\t} ");
        mailBody.append("\n\t</style> ");
        mailBody.append("</head> ");
        mailBody.append("\n\t<body> ");
        mailBody.append("\n\t\t<form id=\"Form\" name=\"FormViaticos\" > ");
        mailBody.append("	<b> C." + getSignatory().getNombreEmpleado() + "</b> ");
        mailBody.append("	<br> ");
        mailBody.append("	<b>" + getSignatory().getPuestoEmpleado() + "</b> ");
        mailBody.append("	<br /> ");
        if (getQuestionnaire().isApplyQuestionnaire()) {
            if (getQuestionnaire().isApply15D()) {
                mailBody.append("Se ha iniciado un proceso de contratacion en el que, segun sus respuestas al cuestionario de determinaci&oacute;n, <b> aplican las obligaciones del articulo 15D del C&oacute;digo Fiscal de la Federaci&oacute;n.</b><br>");
                mailBody.append("\n\t<br>");
                mailBody.append("Se le recuerda que para realizar el pago correspondiente deber&aacute; contar con:<br>");
                mailBody.append("<ul>");
                mailBody.append("<li>Registro a que se refiere el artículo 15 de la Ley federal del trabajo</li>");
                mailBody.append("<li>Copia de los comprobantes fiscales por concepto de pago de salarios de los trabajadores con los que le hayan proporcionado el servicio o ejecutado la obra correspondiente</li>");
                mailBody.append("<li>Recibo de pago expedido por institución bancaria por la declaración de entero de las retenciones de impuestos efectuadas a dichos trabajadores</li>");
                mailBody.append("<li>pago de las cuotas obrero patronales al Instituto Mexicano del Seguro Social, así como del pago de las aportaciones al Instituto del Fondo Nacional de la Vivienda para los Trabajadores</li>");
                mailBody.append("<li>Copia de la declaración del impuesto al valor agregado y del acuse de recibo del pago correspondiente al periodo en que el contratante efectuó el pago de la contraprestación y del impuesto al valor agregado que le fue trasladado.</li>");
                mailBody.append("</ul>");
            } else {
                mailBody.append("Se ha iniciado un proceso de contratacion en el que, segun sus respuestas al cuestionario de determinaci&oacute;n, <b>No</b> aplican las obligaciones del articulo 15D del C&oacute;digo Fiscal de la Federaci&oacute;n.<br>");
                mailBody.append("<br>");
                mailBody.append("Se le recuerda que si en algun momento su contrato requiere que personal preste servicios a favor de CONAFOR debera reportarlo a la Gerencia de Recursos Materiales para recibir instrucciones respecto a las obligaciones que puede estar adquiriendo.<br>");
            }
            mailBody.append("Se solicita de su firma electronica para la solicitud de requisicion y del cuestionario para continuar con el tramite.");
        } else {
            mailBody.append("Se ha iniciado un proceso de contratacion en el que,<b>No</b> aplica cuestionario del articulo 15D del C&oacute;digo Fiscal de la Federaci&oacute;n.<br>");
            mailBody.append("<br>");
            mailBody.append("Se le recuerda que si en algun momento su contrato requiere que personal preste servicios a favor de CONAFOR debera reportarlo a la Gerencia de Recursos Materiales para recibir instrucciones respecto a las obligaciones que puede estar adquiriendo.<br>");
            mailBody.append("Se solicita de su firma electronica para la solicitud de requisición para continuar con el tramite.");
        }
        mailBody.append("	<table> ");
        mailBody.append("		<thead> ");
        mailBody.append("			<tr> ");
        mailBody.append("				<th>Solicitud</th> ");
        mailBody.append("				<th>Partida</th> ");
        mailBody.append("				<th>Concepto</th> ");
        mailBody.append("			</tr> ");
        mailBody.append("		</thead> ");
        mailBody.append("		<tbody> ");
        mailBody.append("<tr> ");
        mailBody.append("\n<td>" + getContractRequisition().getIdSolicitud() + "</td> ");
        mailBody.append("\n<td>" + getContractRequisition().getIdSubPartida() + "</td> ");
        mailBody.append("\n<td>" + getContractRequisition().getDescripcion() + "</td> ");
        mailBody.append("</tr> ");
        mailBody.append("		</tbody> ");
        mailBody.append("	</table> ");
        mailBody.append("	<br /> ");
        mailBody.append("	<br /> ");
        mailBody.append(generaLigaDoctos(conn, urlDescarga, "En la tabla siguiente puede revisar el documento que sera firmado.").toString());
        mailBody.append("\n<b>Para autorizar o rechazar esta solicitud por favor de click <a href=\"");
        mailBody.append(urlAutorizacion);
        mailBody.append(generaAccessoAutToken(Integer.parseInt(getQuestionnaire().getEmployeeNumber()), getDocument(), folioRequisition));
        mailBody.append("\" > aquí </a>.</b> ");
        mailBody.append("	<br /> ");
        mailBody.append("	<br /> ");
        mailBody.append("	<p> ");
        mailBody.append("		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday());
        mailBody.append("	</p> ");
        mailBody.append("	</form> ");
        mailBody.append("</body> ");
        mailBody.append("</html> ");
        log.debug("Object: {}", mailBody);
        return mailBody.toString();
    }

    @Override
    public String getCuerpoCorreoVistoBueno(Connection conn) throws Exception {
        return null;
    }

    @Override
    public String getImporteStr(Connection conn) throws Exception {
        return null;
    }

    /**
     * @return the motivoRechazo
     */
    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public String getPathFile(int requestId, int cabinetId, String docName) throws SQLException {
        Connection conn = null;
        String path = null;
        Documento d = null;
        try {
            conn = getConnection();
            path = "";
            if ("Comprobante".equalsIgnoreCase(docName) || (!"Comprobante".equalsIgnoreCase(docName) && RequisitionManager.isAplidCuestionary(conn, requestId))) {
                d = DocumentoManager.buscaDocumento(conn, ContractQuestionnaire.APPLICATION, cabinetId, docName);
                if (d == null || d.getPaginasDocumento().length == 0)
                    throw new RuntimeException("No se encontro documento con los parametros: " + requestId + "," + cabinetId + "," + docName);
                path = d.getFullPathFilesNames()[0];
            }
            return path;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public ContractQuestionnaire getQuestionnaire() {
        return questionnaire;
    }

    /**
     * @return the reportNames
     */
    public Map<String, String> getReportNames() {
        return reportNames;
    }

    public Map<String, Object> getReportParams() {
        return reportParams;
    }

    /**
     * @return the reportTemplate
     */
    public String getReportTemplate() {
        return reportTemplate;
    }

    public Firmante getSignatory() {
        return signatory;
    }

    @Override
    public String getVoBoLegend(Connection conn) throws Exception {
        return null;
    }

    public int insertQuestionnaire() throws ContratoException {
        Connection conn = null;
        try {
            conn = getConnection();
            int insertados = insertQuestionnaire(conn);
            conn.commit();
            return insertados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2, e2);
                }
            throw new ContratoException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    private int insertQuestionnaire(Connection conn) throws ContratoException {
        int afectados = 0;
        try {
            afectados += QuestionnaireManager.insertQuestionnaire(conn, getQuestionnaire());
            afectados += QuestionnaireManager.insertAnswers(conn, getQuestionnaire(), getUsuario());
            return afectados;
        } catch (Exception e) {
            throw new ContratoException(e);
        }
    }

    public void notificaCancelacion(Connection conn, Requisition requisition) throws Exception {
        ContractQuestionnaire questionnaire = QuestionnaireManager.readContractQuestionnaireFIEL(conn, requisition.getIdSolicitud());
        Usuario u = new Usuario();
        u.setLogin(questionnaire.getCaptureEmployeeLogin());
        u = UsuarioManager.select(conn, u);
        StringBuilder subject = new StringBuilder("Rechazo de la requisicion ").append(requisition.getIdSolicitud());
        String nombreCompleto = u.getNombre();
        String correo = u.getU_email();
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
        mailBody.append("	<b> C.").append(nombreCompleto).append("</b>");
        mailBody.append("	<br>");
        mailBody.append("	<p>");
        mailBody.append("		Se notifica que la requisicion: ");
        mailBody.append("<b> ").append(requisition.getIdSolicitud()).append("</b>");
        mailBody.append("<br>");
        mailBody.append("&quot<i>").append(requisition.getDescripcion()).append("</i>&quot");
        mailBody.append("<br>");
        mailBody.append(" fue  <b>RECHAZADA</b>");
        mailBody.append("	</p>");
        mailBody.append("	<p>");
        mailBody.append("		El motivo de rechazo es:<br>");
        mailBody.append("	</p>");
        mailBody.append("	<pre>");
        mailBody.append(getMotivoRechazo());
        mailBody.append("	</pre>");
        mailBody.append("	<br />");
        mailBody.append("	<br />");
        mailBody.append("\n<b>Para los fines que considere convenientes.</b>");
        mailBody.append("	<br />");
        mailBody.append("	<br />");
        mailBody.append("	<p>");
        mailBody.append("		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday());
        mailBody.append("	</p>");
        mailBody.append("	</form>");
        mailBody.append("</body>");
        mailBody.append("</html>");
        log.debug("Object: {}", mailBody);
        AlarmaManager.procesaAlarmaCNF(conn, "", null, null, subject.toString(), correo, mailBody.toString());
    }

    @Override
    public void notificaOperacionMasivaPendiente(Connection conn, String operacion) throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public String notificaOperacionPendiente(Connection conn, String tipoAutorizacion) throws Exception {
        try {
            String subget = "Solicitud de firma de Cuestionario y Requisición ";
            if (AUTORIZA.equals(tipoAutorizacion)) {
                if (!getQuestionnaire().isApplyQuestionnaire()) {
                    subget = "Solicitud de firma de Requisición ";
                }
                AlarmaManager.procesaAlarmaCNF(conn, "", null, null, subget.concat(getQuestionnaire().getIdRequest()), getSignatory().getCorreoEmpleado(), getCuerpoCorreoAutoriza(conn));
            }
            return "success";
        } catch (AutRecepcionMaterialException e) {
            throw e;
        } catch (Exception e) {
            throw new AutRecepcionMaterialException("Error notificando operacion pendiente: " + e, e);
        }
    }

    public void notificaAutOperacion(Connection conn) throws Exception {
        Usuario u = null;
        StringBuilder emailsGRM = new StringBuilder();
        try {
            //Usuario que captura
            u = new Usuario();
            u.setLogin(getContractRequisition().getIdUsuarioCreacion());
            u = UsuarioManager.select(conn, u);
            //La requi es de la A04
            if (UE_GRM.equalsIgnoreCase(getContractRequisition().getIdUnidadEjecutora())) {
                //Correo del jefe de adquisiciones
                emailsGRM.append(com.syc.adquisiciones.util.Util.getSystemSetting(conn, SolicitudFirmaElectronica.CORREOS_GRM_REQUI));
            }
            StringBuilder subject = new StringBuilder("Requisición Autorizada ").append(getContractRequisition().getIdSolicitud());
            String urlDescarga = generaURLDescarga(conn);
            StringBuilder mailBody = new StringBuilder();
            mailBody.append("<html>");
            mailBody.append("\n\t<head> ");
            mailBody.append("\n\t<meta charset=\"UTF-8\"> ");
            mailBody.append("\n\t<style type=\"text/css\"> ");
            mailBody.append("\n\tbody { ");
            mailBody.append("\n\t\t	font-family: verdana, arial, sans-serif; ");
            mailBody.append("\n\t\t	font-size: 12px; ");
            mailBody.append("\n\t} ");
            mailBody.append("\n\ttable { ");
            mailBody.append("\n\t\tfont-size: 12px; ");
            mailBody.append("\n\t\tcolor: #333333; ");
            mailBody.append("\n\t\tborder-width: 1px; ");
            mailBody.append("\n\t\tborder-color: #666666; ");
            mailBody.append("\n\t\tborder-collapse: collapse; ");
            mailBody.append("\n\t} ");
            mailBody.append("\n\ttable th { ");
            mailBody.append("\n\t\tborder-width: 1px; ");
            mailBody.append("\n\t\tpadding: 8px; ");
            mailBody.append("\n\t\tborder-style: solid; ");
            mailBody.append("\n\t\tborder-color: #666666; ");
            mailBody.append("\n\t\tbackground-color: #dedede; ");
            mailBody.append("\n\t} ");
            mailBody.append("\n\ttable td { ");
            mailBody.append("\n\t\tborder-width: 1px; ");
            mailBody.append("\n\t\tpadding: 8px; ");
            mailBody.append("\n\t\tborder-style: solid; ");
            mailBody.append("\n\t\tborder-color: #666666; ");
            mailBody.append("\n\t\tbackground-color: #ffffff; ");
            mailBody.append("\n\t} ");
            mailBody.append("\n\t</style> ");
            mailBody.append("</head> ");
            mailBody.append("\n\t<body> ");
            mailBody.append("\n\t\t<form id=\"Form\" name=\"FormEmail\" > ");
            mailBody.append("	<b> C." + u.getNombre() + "</b> ");
            mailBody.append("Se ha autorizado la requisición");
            mailBody.append("	<br /> ");
            mailBody.append("	<table> ");
            mailBody.append("		<thead> ");
            mailBody.append("			<tr> ");
            mailBody.append("				<th>Solicitud</th> ");
            mailBody.append("				<th>Partida</th> ");
            mailBody.append("				<th>Concepto</th> ");
            mailBody.append("			</tr> ");
            mailBody.append("		</thead> ");
            mailBody.append("		<tbody> ");
            mailBody.append("<tr> ");
            mailBody.append("\n<td>" + getContractRequisition().getIdSolicitud() + "</td> ");
            mailBody.append("\n<td>" + getContractRequisition().getIdSubPartida() + "</td> ");
            mailBody.append("\n<td>" + getContractRequisition().getDescripcion() + "</td> ");
            mailBody.append("</tr> ");
            mailBody.append("		</tbody> ");
            mailBody.append("	</table> ");
            mailBody.append("	<br /> ");
            mailBody.append("	<br /> ");
            mailBody.append(generaLigaDoctos(conn, urlDescarga, "En la tabla siguiente puede revisar el documento que fue firmado.").toString());
            mailBody.append("	<br /> ");
            mailBody.append("	<br /> ");
            mailBody.append("	<p> ");
            mailBody.append("		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday());
            mailBody.append("	</p> ");
            mailBody.append("	</form> ");
            mailBody.append("</body> ");
            mailBody.append("</html> ");
            log.debug("Object: {}", mailBody);
            AlarmaManager.procesaAlarmaCNF(conn, "", null, null, subject.toString(), u.getU_email(), emailsGRM.toString(), "", mailBody.toString(), false);
        } catch (AutRecepcionMaterialException e) {
            throw e;
        } catch (Exception e) {
            throw new AutRecepcionMaterialException("Error notificando operacion pendiente: " + e, e);
        } finally {
            u = null;
        }
    }

    @Override
    public void onCancelaTramite(Connection conn, String reason) throws Exception {
    }

    @Override
    public void onFinishAut(Connection conn) throws Exception {
    }

    @Override
    public void onFinishVoBo(Connection conn) throws Exception {
    }

    private Firmante readSignatory(Connection conn) throws IllegalAccessException, InvocationTargetException, Exception {
        return FirmanteManager.readRequestSignatory(conn, getQuestionnaire());
    }

    public void sendQuestionnaireSign() throws ContratoException {
        Connection conn = null;
        try {
            conn = getConnection();
            setSignatory(readSignatory(conn));
            if (getQuestionnaire().isApplyQuestionnaire()) {
                sendQuestionnaireSign(conn);
            } else {
                sendRequiSign(conn);
            }
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas con rollback: " + e2.toString(), e2);
                }
            throw new ContratoException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void reSendEmail() throws ContratoException {
        Connection conn = null;
        try {
            conn = getConnection();
            setSignatory(readSignatory(conn));
            reSendEmail(conn);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas con rollback: " + e2.toString(), e2);
                }
            throw new ContratoException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    private void reSendEmail(Connection conn) throws ContratoException {
        Requisition requisition = null;
        try {
            requisition = RequisitionManager.readRequisition(conn, getQuestionnaire().getIdRequest());
            setContractRequisition(requisition);
            getQuestionnaire().setApply15D(false);
            getQuestionnaire().setApplyQuestionnaire(false);
            setReportNames(null);
            if ("S".equalsIgnoreCase(requisition.getApplyQuestionnaire())) {
                getQuestionnaire().setApplyQuestionnaire(true);
                setReportNames();
            } else {
                setReportName();
            }
            if ("S".equalsIgnoreCase(requisition.getcAplica15D())) {
                getQuestionnaire().setApply15D(true);
            }
            notificaOperacionPendiente(conn, AUTORIZA);
        } catch (Exception e) {
            throw new ContratoException(e);
        } finally {
            requisition = null;
        }
    }

    private void sendQuestionnaireSign(Connection conn) throws ContratoException {
        try {
            Requisition requisition = RequisitionManager.readRequisition(conn, getQuestionnaire().getIdRequest());
            setContractRequisition(requisition);
            QuestionnaireManager.findCabinet(conn, getQuestionnaire());
            insertQuestionnaire(conn);
            generaArchivos(conn);
            applySecluded(conn);
            RequisitionManager.setRequisitionStatus(conn, requisition, RequisitionStatus.WAIT_FOR_SIGNATURE, PrecomprometidoStatus.REQUESTED);
            notificaOperacionPendiente(conn, AUTORIZA);
        } catch (Exception e) {
            throw new ContratoException(e);
        }
    }

    private void sendRequiSign(Connection conn) throws ContratoException {
        try {
            Requisition requisition = RequisitionManager.readRequisition(conn, getQuestionnaire().getIdRequest());
            setContractRequisition(requisition);
            QuestionnaireManager.findCabinet(conn, getQuestionnaire());
            QuestionnaireManager.insertQuestionnaire(conn, getQuestionnaire());
            generaArchivos(conn);
            if ("RT".equalsIgnoreCase(requisition.getIdTipoSolicitud())) {
                //Validar las requis de tineda
                applySecludedTD(conn);
            } else {
                applySecluded(conn);
            }
            RequisitionManager.setRequisitionStatus(conn, requisition, RequisitionStatus.WAIT_FOR_SIGNATURE, PrecomprometidoStatus.REQUESTED);
            notificaOperacionPendiente(conn, AUTORIZA);
        } catch (Exception e) {
            throw new ContratoException(e);
        }
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public void setQuestionnaire(ContractQuestionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    public void setReportNames() {
        reportNames = new HashMap<String, String>();
        reportNames.put("Comprobante", "rptRequisiciones_Fiel.jasper");
        reportNames.put("Cuestionario Firmado", "ReporteCuestionario.jasper");
    }

    public void setReportName() {
        reportNames = new HashMap<String, String>();
        reportNames.put("Comprobante", "rptRequisiciones_Fiel.jasper");
    }

    /**
     * @param reportNames
     *            the reportNames to set
     */
    public void setReportNames(Map<String, String> reportNames) {
        this.reportNames = reportNames;
    }

    public void setReportParams(Map<String, Object> reportParams) {
        this.reportParams = reportParams;
    }

    /**
     * @param reportTemplate
     *            the reportTemplate to set
     */
    public void setReportTemplate(String reportTemplate) {
        this.reportTemplate = reportTemplate;
    }

    public void setSignatory(Firmante signatory) {
        this.signatory = signatory;
    }

    public void avanzaEstatusRequisicion(Connection conn, Requisition requisition) throws SQLException {
        RequisitionManager.authRequisition(conn, requisition);
    }

    public List<Documento> getRutaDocumentos(Connection conn, Requisition requisition) throws Exception {
        List<Documento> doctos = new ArrayList<>();
        Caso c = CasoManager.findByFolioLike(conn, getDocument(), String.valueOf(getIdField()));
        Documento d = null;
        for (Iterator<String> i = getReportNames().keySet().iterator(); i.hasNext(); ) {
            String documentName = i.next();
            d = DocumentoManager.buscaDocumento(conn, ContractQuestionnaire.APPLICATION, c.getIdGabinete(), documentName);
            if (d != null)
                doctos.add(d);
        }
        return doctos;
    }

    public void updateSignedDocto(Connection conn, File signedFile, Documento d) throws SQLException, AutRecepcionMaterialException {
        DocumentoManager.respaldaPagina(conn, d);
        Pagina pagina = d.getPaginaDocumento(0);
        String nomArchivo = Util.getFileWithoutExtencion(signedFile.getName());
        pagina.setNomArchivoOrg(nomArchivo + ".pdf");
        pagina.setNomArchivoVol(nomArchivo + ".tif");
        d.setPaginasDocumento(new Pagina[] { pagina });
        DocumentoManager.actualizaRutaPagina(conn, d);
    }

    /**
     * @return the contractRequisition
     */
    public Requisition getContractRequisition() {
        return contractRequisition;
    }

    /**
     * @param contractRequisition
     *            the contractRequisition to set
     */
    public void setContractRequisition(Requisition contractRequisition) {
        this.contractRequisition = contractRequisition;
    }

    public void applySecluded(Connection conn) throws Exception {
        CallableStatement cmst = null, cmst1 = null;
        String result = "";
        CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
        try {
            AccountingEngine ae = new AccountingEngine();
            ae.setValidaInsuficienciaDeSaldo(true);
            RequisitionManager.insertApartado(conn, getContractRequisition(), getUsuario());
            Caso c = CasoManager.findByFolioLike(conn, "APARTADO", String.valueOf(getContractRequisition().getConsecutivoApartado()));
            log.debug("Object: {}", "Inicia aplicacion presupuestal " + new Timestamp(System.currentTimeMillis()));
            Map<String, String> m = Util.readValuesCasoDato(c.getCasoDato());
            ae.makeAccountingApplication(conn, "APARTADO", String.valueOf(getContractRequisition().getConsecutivoApartado()), "tApartadoEncabezado", "tApartadoDetalle", "nFolioApartado");
            cmst = conn.prepareCall("{?= call pa_validaAplicacionContable (?,?,?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setInt(2, getContractRequisition().getConsecutivoApartado());
            cmst.setString(3, getContractRequisition().getEjercicio());
            cmst.setString(4, "S");
            cmst.setString(5, "PR");
            cmst.setString(6, "APARTADO");
            cmst.execute();
            int outputValue = cmst.getInt(1);
            if (outputValue == 0) {
                // actualiza vigencia de las requisiciones
                cmst1 = conn.prepareCall("{ call sp_mEstadoVigenciaApartado (?,?,?,?)}");
                cmst1.setString(1, getContractRequisition().getEjercicio());
                cmst1.setString(2, getContractRequisition().getIdUnidadEjecutora());
                cmst1.setString(3, getContractRequisition().getIdSolicitud());
                cmst1.setString(4, c.getFolio());
                cmst1.execute();
                // Avanza el cazo
                cbl.avanzaCaso(conn, c, getUsuario().getLogin(), "", new String[] { "CONSULTA_APARTADO" }, new String[] { "CONSULTA_APTD" }, m, null);
                // Bitacora
                com.syc.adquisiciones.util.Util.bitacoraMovimientos(getContractRequisition().getIdSolicitud(), "Apartado Aplicado", getUsuario().getLogin(), conn);
                log.debug("Object: {}", c.getCasoDato("APLICADO_CONT").getValor());
                result = "DOCUMENTO DE APARTADO APLICADO PRESUPUESTALMENTE.";
                log.debug("Object: {}", "Termina la autorización del apartado. " + new Timestamp(System.currentTimeMillis()));
            } else {
                switch(outputValue) {
                    case 1:
                        result = "EL DOCUMENTO NO SE APLICO PRESUPUESTALMENTE (NO SE ENCONTRO EL REGISTRO DEL APARTADO).";
                        break;
                    case 2:
                        result = "EL DOCUMENTO NO SE APLICO PRESUPUESTALMENTE (NO EXISTE NINGUN REGISTRO DE POLIZA DEL DOCUMENTO).";
                        break;
                    case 3:
                        result = "EL DOCUMENTO NO SE APLICO PRESUPUESTALMENTE (LOS MOVIMIENTOS DE LA AFECTACION PRESUPUESTAL NO ESTAN COMPLETOS).";
                        break;
                    default:
                        result = "ERROR INESPERADO EN LA CONFIRMACION DE LA APLICACION PRESUPUESTAL.";
                }
                throw new Exception(result);
            }
        } finally {
            CloseObject.closeObject(cmst);
            CloseObject.closeObject(cmst1);
        }
    }

    public void applySecludedTD(Connection conn) throws Exception {
        CallableStatement cmst = null, cmst1 = null;
        CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
        try {
            RequisitionManager.insertApartado(conn, getContractRequisition(), getUsuario());
            Caso c = CasoManager.findByFolioLike(conn, "APARTADO", String.valueOf(getContractRequisition().getConsecutivoApartado()));
            Map<String, String> m = Util.readValuesCasoDato(c.getCasoDato());
            // Avanza el cazo
            cbl.avanzaCaso(conn, c, getUsuario().getLogin(), "", new String[] { "ADJUNTO_APARTADO" }, new String[] { "ADJUNTO_APTD" }, m, null);
        } finally {
            CloseObject.closeObject(cmst);
            CloseObject.closeObject(cmst1);
        }
    }

    public void canceledSecluded(Connection conn) throws Exception {
        CallableStatement cmst = null, cmst1 = null;
        String result = "";
        CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
        try {
            AccountingEngine ae = new AccountingEngine();
            ae.setValidaInsuficienciaDeSaldo(true);
            // RequisitionManager.insertApartado(conn, getContractRequisition(),
            // getUsuario());
            Caso c = CasoManager.findByFolioLike(conn, "APARTADO", String.valueOf(getContractRequisition().getConsecutivoApartado()));
            log.debug("Object: {}", "Inicia aplicacion presupuestal " + new Timestamp(System.currentTimeMillis()));
            Map<String, String> m = Util.readValuesCasoDato(c.getCasoDato());
            ae.cancelAccountingApplication(conn, "APARTADO", String.valueOf(getContractRequisition().getConsecutivoApartado()), "tApartadoEncabezado", "tApartadoDetalle", "nFolioApartado");
            cmst = conn.prepareCall("{?= call pa_validaAplicacionContable (?,?,?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setInt(2, getContractRequisition().getConsecutivoApartado());
            cmst.setString(3, getContractRequisition().getEjercicio());
            cmst.setString(4, "C");
            cmst.setString(5, "PR");
            cmst.setString(6, "APARTADO");
            cmst.execute();
            int outputValue = cmst.getInt(1);
            if (outputValue == 0) {
                // Avanza el cazo
                cbl.avanzaCaso(conn, c, getUsuario().getLogin(), "", new String[] { "CONSULTA_APARTADO" }, new String[] { "CONSULTA_APTD" }, m, null);
                // Bitacora
                com.syc.adquisiciones.util.Util.bitacoraMovimientos(getContractRequisition().getIdSolicitud(), "Apartado Cancelado", getUsuario().getLogin(), conn);
                log.debug("Object: {}", c.getCasoDato("APLICADO_CONT").getValor());
                result = "DOCUMENTO DE APARTADO APLICADO PRESUPUESTALMENTE.";
                log.debug("Object: {}", "Termina la autorización del apartado. " + new Timestamp(System.currentTimeMillis()));
            } else {
                switch(outputValue) {
                    case 1:
                        result = "EL DOCUMENTO NO SE APLICO PRESUPUESTALMENTE (NO SE ENCONTRO EL REGISTRO DEL APARTADO).";
                        break;
                    case 2:
                        result = "EL DOCUMENTO NO SE APLICO PRESUPUESTALMENTE (NO EXISTE NINGUN REGISTRO DE POLIZA DEL DOCUMENTO).";
                        break;
                    case 3:
                        result = "EL DOCUMENTO NO SE APLICO PRESUPUESTALMENTE (LOS MOVIMIENTOS DE LA AFECTACION PRESUPUESTAL NO ESTAN COMPLETOS).";
                        break;
                    default:
                        result = "ERROR INESPERADO EN LA CONFIRMACION DE LA APLICACION PRESUPUESTAL.";
                }
                throw new Exception(result);
            }
        } finally {
            CloseObject.closeObject(cmst);
            CloseObject.closeObject(cmst1);
        }
    }

    public void readAnswersPayments(EgresoEncabezado header) throws SQLException {
        Connection conn = null;
        try {
            conn = ds.getConnection();
            QuestionnaireManager.readAnswersPayments(conn, header);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public void onGeneraArchivosMasivo(Connection conn) {
        // TODO Auto-generated method stub
    }

    @Override
    public String generaArchivoInformeComision(Connection conn, Volumen vol, Carpeta folder, boolean isSignedCopy) throws FirmaElectronicaException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getVoBoLegendWithName(Connection conn, String nombre, String puesto) throws Exception {
        String voLegend = VO_BO_LEGEND_PREFIX.concat(" Firmado por: ").concat(nombre).concat(" | ").concat(puesto);
        if (tieneDelegatorioVoBO(conn)) {
            voLegend = VO_BO_LEGEND_PREFIX + " Firma " + getTipoSuplencia() + " de " + getNombreEmpleadoSuplido() + " con fundamento en el oficio: " + getFolioOficioVoBo() + " de fecha: " + getFechaOficioVoBo();
            log.info("Object: {}", voLegend);
        }
        return voLegend;
    }

    @Override
    public String getAutLegendWithName(Connection conn, String nombre, String puesto) {
        try {
            String autLegend = AUT_LEGEND_PREFIX.concat(" Firmado por: ").concat(nombre).concat(" | ").concat(puesto);
            if (tieneDelegatorioAut(conn)) {
                autLegend = AUT_LEGEND_PREFIX + ". Firma " + getTipoSuplenciaAut() + " de " + getNombreEmpleadoSuplidoAut() + " con fundamento en el oficio: " + getFolioOficioAut() + " de fecha: " + getFechaOficioAut();
                log.info("Object: {}", autLegend);
            }
            return autLegend;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String notificaPrefirmante(Connection conn) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
