package com.axtel.contratos;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import com.axtel.contratos.core.ProcesoEnteraSatisfaccionManager;
import com.axtel.contratos.entities.DatEnteraSatisfaccion;
import com.syc.adquisiciones.util.Util;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.fortimax.core.Carpeta;
import com.syc.fortimax.core.CarpetaManager;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.fortimax.core.Pagina;
import com.syc.fortimax.core.Volumen;
import com.syc.fortimax.core.VolumenManager;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.firmaElectronica.exceptions.AutRecepcionMaterialException;
import com.syc.sai.firmaElectronica.exceptions.FirmaElectronicaException;
import com.syc.sai.firmaElectronica.exceptions.NotEmptyDocumentException;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcesoEnteraSatisfaccionBusinessLogic extends SolicitudFirmaElectronica {

    private static Logger log = LoggerFactory.getLogger(ProcesoEnteraSatisfaccionBusinessLogic.class);

    private DataSourceManager ds = null;

    DatEnteraSatisfaccion datEnteraSatisfaccion;

    public ProcesoEnteraSatisfaccionBusinessLogic() {
        super();
        ds = new DataSourceManager() {
        };
    }

    public JSONObject getData(String cFolio) throws Exception {
        Connection conn = null;
        JSONArray arrayObj = null;
        JSONObject jsonObj = null;
        StringBuilder query = null;
        boolean error = true;
        String cIdContratoSAI = null;
        try {
            query = new StringBuilder();
            jsonObj = new JSONObject();
            conn = ds.getConnection();
            //Codificar
            query.append("Select *from fn_procesoEnteraSatisfaccion('" + cFolio + "')  ");
            log.info("Object: {}", query.toString());
            arrayObj = Util.datGuardados(conn, query.toString());
            jsonObj.put("infoGuardada", arrayObj);
            if (arrayObj.getJSONObject(0).getBoolean("HAYINFO")) {
                cIdContratoSAI = arrayObj.getJSONObject(0).getString("pedidoContratoCompromiso");
                arrayObj = null;
                query.delete(0, query.length());
                //Catalogo partidas de contrato
                query.append("select '0. Seleccionar partida' as descrip, 0 nIdLineaConsolidado union select convert(varchar,nIdLineaConsolidado)+'. '+descrip as descrip,nIdLineaConsolidado from fn_partidasDeContrato('" + cIdContratoSAI + "')");
                arrayObj = Util.obtieneDatQuery(conn, query.toString());
                jsonObj.put("catPartContrato", arrayObj);
                arrayObj = null;
                query.delete(0, query.length());
                //Catalogo lugar de prestación del servicio
                query.append("select '0. Seleccionar lugar de entrega del servicio' descrip, 0 nCentroTrabajo union SELECT convert(varchar,nCentroTrabajo)+'. '+cDescripcion as descrip,nCentroTrabajo FROM mContratoServicioPrestado with (nolock) WHERE cIdcontratoDefinitivo='" + cIdContratoSAI + "' order by nCentroTrabajo ");
                arrayObj = Util.obtieneDatQuery(conn, query.toString());
                jsonObj.put("catLugarPrestServicio", arrayObj);
            }
            arrayObj = null;
            query.delete(0, query.length());
            //Catalogo periodo
            query.append("select 'Favor de seleccionar el mes de pago'cPeriodo,0 nIdPeriodo union select cPeriodo,nIdPeriodo from mCatalogoPeriodo with(Nolock)");
            arrayObj = Util.obtieneDatQuery(conn, query.toString());
            jsonObj.put("catMesPago", arrayObj);
            conn.commit();
            error = false;
            return jsonObj;
        } catch (Exception e) {
            log.error("Object: {}", e.getMessage());
            throw new Exception(e);
        } finally {
            if (error) {
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e) {
                        log.error("Object: {}", "Bug, Rollback: " + e);
                        throw new Exception("Bug, Rollback: " + e.toString(), e);
                    }
            }
            CloseObject.closeObject(conn);
            query.delete(0, query.length());
            query = null;
            arrayObj = null;
            cIdContratoSAI = null;
            ds = null;
        }
    }

    public void saveInformation(DatEnteraSatisfaccion dat) throws Exception {
        Connection conn = null;
        boolean error = true;
        ProcesoEnteraSatisfaccionManager manager = null;
        try {
            conn = ds.getConnection();
            manager = new ProcesoEnteraSatisfaccionManager();
            //Validar información
            StringBuilder msg = validateInfo(dat);
            if (msg.length() > 0) {
                throw new Exception(msg.toString());
            }
            //validar si ya existe la información
            if (manager.existInfoEnteraSatisfaccion(conn, dat.getcFolio())) {
                manager.updateServicioEnteraSatisfacccion(conn, dat);
                manager.updateAnexo1A(conn, dat);
            } else {
                manager.saveServicioEnteraSatisfacccion(conn, dat);
                manager.saveAnexo1A(conn, dat);
            }
            //Validar si el servicio no se presto a entera satisfacción
            manager.deleteActaHechos(conn, dat.getcFolio());
            if (dat.getnServPrestEnteraSatisfaccion() == SolicitudFirmaElectronica.SERVICIO_NO_PRESTADO_ENSA) {
                manager.saveActaHechos(conn, dat);
            }
            conn.commit();
            error = false;
        } catch (Exception e) {
            log.error("Object: {}", e.getMessage());
            throw new Exception(e);
        } finally {
            if (error) {
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e) {
                        log.error("Object: {}", "Bug, Rollback: " + e);
                        throw new Exception("Bug, Rollback: " + e.toString(), e);
                    }
            }
            CloseObject.closeObject(conn);
            manager = null;
            ds = null;
        }
    }

    private StringBuilder validateInfo(DatEnteraSatisfaccion dat) {
        StringBuilder msg = new StringBuilder();
        if (dat.getcIdContratoDefinitivo() == null || "".equalsIgnoreCase(dat.getcIdContratoDefinitivo())) {
            msg.append("Favor de seleccionar un contrato.");
        }
        if (dat.getnNumEmpFirmante() == 0) {
            msg.append("\nFavor de seleccionar un firmante.");
        }
        if (dat.getnServPrestEnteraSatisfaccion() == 0) {
            msg.append("\nFavor de seleccionar si el servicio se presto a entera satisfacción.");
        }
        return msg;
    }

    @Override
    public String generaArchivoFirma(Connection conn, Volumen vol, Carpeta folder, boolean isSignedCopy) throws FirmaElectronicaException {
        Map<String, Object> parametrosReporte = new HashMap<String, Object>();
        parametrosReporte.put("cFolio", getDatEnteraSatisfaccion().getcFolio());
        parametrosReporte.put("SUBREPORT_DIR", getReportPath() + File.separatorChar + "FIEL");
        String filename = null;
        try {
            filename = generaArchivoFirma(conn, vol, folder, parametrosReporte, "FIEL", getDocNameJasper(), isSignedCopy);
        } catch (NotEmptyDocumentException nede) {
            log.warn("Object: {}", "El documento no esta vacio. Se ignora" + nede);
        } catch (Exception e) {
            throw new FirmaElectronicaException(e);
        }
        return filename;
    }

    @Override
    public String generaArchivoInformeComision(Connection conn, Volumen vol, Carpeta folder, boolean isSignedCopy) throws FirmaElectronicaException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getAutLegend(Connection conn) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getAutLegendWithName(Connection conn, String getVoBoNombre, String getVoBoPuesto) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getCuerpoCorreoAutoriza(Connection conn) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getCuerpoCorreoVistoBueno(Connection conn) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getImporteStr(Connection conn) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getVoBoLegend(Connection conn) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getVoBoLegendWithName(Connection conn, String getVoBoNombre, String getVoBoPuesto) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void notificaOperacionMasivaPendiente(Connection conn, String operacion) throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public String notificaOperacionPendiente(Connection conn, String tipoAutorizacion) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCancelaTramite(Connection conn, String reason) throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public void onFinishAut(Connection conn) throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public void onFinishVoBo(Connection conn) throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public void onGeneraArchivosMasivo(Connection conn) {
        // TODO Auto-generated method stub
    }

    public DatEnteraSatisfaccion getDatEnteraSatisfaccion() {
        return datEnteraSatisfaccion;
    }

    public void setDatEnteraSatisfaccion(DatEnteraSatisfaccion datEnteraSatisfaccion) {
        this.datEnteraSatisfaccion = datEnteraSatisfaccion;
    }

    public void getDocumento() throws Exception {
        boolean error = true;
        Connection conn = null;
        try {
            conn = ds.getConnection();
            generaArchivo(conn, "Documentos");
            conn.commit();
            error = false;
        } catch (SQLException e) {
            error = true;
            throw new Exception("Error de base de datos al solicitar documento " + getDocName() + ": " + e.toString(), e);
        } finally {
            if (error) {
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e2) {
                        log.warn("Object: {}", "Problemas en rollback: " + e2);
                    }
            }
            CloseObject.closeObject(conn);
        }
    }

    private String generaArchivo(Connection conn, String folderName) throws AutRecepcionMaterialException {
        String filename = "";
        Documento doc = null;
        try {
            Volumen vol = VolumenManager.getVolumen(conn);
            int idCabinet = -1;
            idCabinet = getIDGabinete(conn);
            if (idCabinet <= 0)
                idCabinet = creaExpediente(conn);
            Carpeta folder = CarpetaManager.getCarpetaByName(conn, getDocument(), idCabinet, folderName);
            if (folder == null)
                folder = createFolder(conn, idCabinet, folderName);
            if (DocumentoManager.existeDocumentoCapturado(conn, folder.getTituloAplicacion(), folder.getIdGabinete(), folder.getIdCarpeta(), getDocName())) {
                doc = DocumentoManager.getDocumento(conn, folder.getTituloAplicacion(), folder.getIdGabinete(), folder.getIdCarpeta(), getDocName());
                DocumentoManager.limpiaDocumento(conn, folder.getTituloAplicacion(), folder.getIdGabinete(), folder.getIdCarpeta(), doc.getIdDocumento());
            }
            filename = generaArchivoFirma(conn, vol, folder, false);
            return filename;
        } catch (SQLException e) {
            throw new AutRecepcionMaterialException("Error de base de datos al generar el documento: " + e.toString(), e);
        } catch (Exception e) {
            throw new AutRecepcionMaterialException("Error al generar el documento: " + e.toString(), e);
        }
    }

    public int getIDGabinete(Connection conn) throws SQLException, AutRecepcionMaterialException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        //IdField() es el id_caso
        StringBuilder query = new StringBuilder();
        query.append("SELECT ID_GABINETE FROM imx");
        query.append(getDocument());
        query.append(" WITH(NOLOCK) WHERE folio = + ?");
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, getDatEnteraSatisfaccion().getcFolio());
            rs = ps.executeQuery();
            int idCabinet = -1;
            if (rs.next())
                idCabinet = rs.getInt("ID_GABINETE");
            return idCabinet;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    private int creaExpediente(Connection conn) throws AutRecepcionMaterialException {
        Caso rc = null;
        Aplicacion app = null;
        try {
            int idCaso = findIDCaso(conn);
            rc = new Caso();
            rc.setIdCaso(idCaso);
            rc = CasoManager.select(conn, rc);
            app = AplicacionManager.select(conn, rc.getTipoCaso().getGavetaAsociada());
            int idGabiente = AplicacionManager.createExpediente(conn, getUsuario().getLogin(), rc, app);
            rc.setIdGabinete(idGabiente);
            CasoManager.update(conn, rc);
            return idGabiente;
        } catch (SQLException e) {
            throw new AutRecepcionMaterialException("Error de base de datos al crear expediente: " + e, e);
        } finally {
            rc = null;
            app = null;
        }
    }

    private int findIDCaso(Connection conn) throws AutRecepcionMaterialException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	id_caso ");
        query.append("  FROM	cg_caso WITH(NOLOCK) ");
        query.append(" WHERE	id_tc = 101 ");
        query.append("   AND	c_folio = ? ");
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, getDatEnteraSatisfaccion().getcFolio());
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
            else
                throw new AutRecepcionMaterialException("No se encontro caso con id: " + getIdField() + " id_tc = 101 ");
        } catch (SQLException e) {
            throw new AutRecepcionMaterialException("Error de base de datos buscando caso para el id: " + getIdField() + " : " + e, e);
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }

    public void saveValidate(DatEnteraSatisfaccion dat) throws Exception {
        Connection conn = null;
        boolean error = true;
        ProcesoEnteraSatisfaccionManager manager = null;
        try {
            conn = ds.getConnection();
            manager = new ProcesoEnteraSatisfaccionManager();
            manager.updateValidateEnteraSatisfaccion(conn, dat);
            conn.commit();
            error = false;
        } catch (Exception e) {
            log.error("Object: {}", e.getMessage());
            throw new Exception(e);
        } finally {
            if (error) {
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e) {
                        log.error("Object: {}", "Bug, Rollback: " + e);
                        throw new Exception("Bug, Rollback: " + e.toString(), e);
                    }
            }
            CloseObject.closeObject(conn);
            manager = null;
            ds = null;
        }
    }

    public void sendProcess() throws Exception {
        Connection conn = null;
        boolean error = true;
        int id_tc = 101;
        try {
            conn = ds.getConnection();
            ProcesoEnteraSatisfaccionManager.updateStateEnteraSatisfaccion(conn, getDatEnteraSatisfaccion());
            //Validar el id_oper=
            if (getDatEnteraSatisfaccion().getnIdEstatus() == SolicitudFirmaElectronica.ESTATUS_ENSA_FIRMA) {
                //mandar correo al firmante
                sendEmail(conn, false);
                //Atrapar el caso con el nombre del firmante
                ProcesoEnteraSatisfaccionManager.asignedCase(conn, getDatEnteraSatisfaccion().getResp().toString(), getDatEnteraSatisfaccion().getnIdCaso(), id_tc);
            } else if (getDatEnteraSatisfaccion().getnIdEstatus() == SolicitudFirmaElectronica.ESTATUS_ENSA_CAPTURA) {
                //mandar correo al capturista y al firmante
                sendEmail(conn, true);
            }
            conn.commit();
            error = false;
        } catch (Exception e) {
            log.error("Object: {}", e.getMessage());
            throw new Exception(e);
        } finally {
            if (error) {
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e) {
                        log.error("Object: {}", "Bug, Rollback: " + e);
                        throw new Exception("Bug, Rollback: " + e.toString(), e);
                    }
            }
            CloseObject.closeObject(conn);
            ds = null;
        }
    }

    public void sendEmailProcess(Connection conn, boolean isReject) throws Exception {
        sendEmail(conn, isReject);
    }

    private void sendEmail(Connection conn, boolean isReject) throws Exception {
        StringBuilder subject = null;
        StringBuilder bodymail = null;
        try {
            subject = new StringBuilder();
            subject.append("Proceso a entera satisfacción con folio " + getDatEnteraSatisfaccion().getcFolio());
            //obtener email firmante
            Map<String, String> datUsers = ProcesoEnteraSatisfaccionManager.getSignatoryData(conn, getDatEnteraSatisfaccion().getcFolio());
            if (datUsers == null || datUsers.isEmpty()) {
                throw new Exception("No se cuenta con la información del firmante para el folio=" + getDatEnteraSatisfaccion().getcFolio());
            }
            setIdField(getDatEnteraSatisfaccion().getnIdCaso());
            //Send email
            setNextEmailEstate(datUsers, isReject);
            if (isReject) {
                bodymail = bodyEmailReject(conn, datUsers);
            } else {
                bodymail = bodyEmail(conn, datUsers);
            }
            if (getDatEnteraSatisfaccion().getEmail() == null || "".equals(getDatEnteraSatisfaccion().getEmail())) {
                throw new Exception("No se cuenta con una direcci\u00f3n de correo el\u00e9ctronico.");
            }
            //AlarmaManager.procesaAlarma( conn, "", null, null, subject.toString(), getDatEnteraSatisfaccion().getEmail()+";", bodymail.toString() );
            AlarmaManager.procesaAlarmaCNF(conn, null, null, null, subject.toString(), getDatEnteraSatisfaccion().getEmail() + ";", bodymail.toString());
            //AlarmaManager.procesaAlarmaCNF( conn, "",null, null, subject.toString(), getDatEnteraSatisfaccion().getEmail()+";", "", bodymail.toString(), false );
        } finally {
            if (bodymail != null && bodymail.length() > 0)
                bodymail.delete(0, bodymail.length());
            if (subject != null && subject.length() > 0)
                subject.delete(0, subject.length());
            subject = null;
            bodymail = null;
        }
    }

    private void setNextEmailEstate(Map<String, String> datUsers, boolean isReject) {
        if (isReject) {
            getDatEnteraSatisfaccion().setEmail(datUsers.get("emailUserCapture").concat(";").concat(datUsers.get("emailSignatory")));
            getDatEnteraSatisfaccion().setResp(new String[] { "CAPTURA_ENTERASATISFACCION" });
            getDatEnteraSatisfaccion().setOper(new String[] { "captura_enterasatisfaccion" });
        } else {
            if (getDatEnteraSatisfaccion().getnIdEstatus() == SolicitudFirmaElectronica.ESTATUS_ENSA_FIRMA) {
                getDatEnteraSatisfaccion().setEmail(datUsers.get("emailSignatory"));
                getDatEnteraSatisfaccion().setResp(new String[] { datUsers.get("nameSignatory") });
                getDatEnteraSatisfaccion().setOper(new String[] { "firma_enterasatisfaccion" });
            } else if (getDatEnteraSatisfaccion().getnIdEstatus() == SolicitudFirmaElectronica.ESTATUS_ENSA_TESTIGO1) {
                getDatEnteraSatisfaccion().setEmail(datUsers.get("emailWitness1"));
                getDatEnteraSatisfaccion().setResp(new String[] { datUsers.get("nameWitness1") });
                getDatEnteraSatisfaccion().setOper(new String[] { "testigo1_enterasatisfaccion" });
            } else if (getDatEnteraSatisfaccion().getnIdEstatus() == SolicitudFirmaElectronica.ESTATUS_ENSA_TESTIGO2) {
                getDatEnteraSatisfaccion().setEmail(datUsers.get("emailWitness2"));
                getDatEnteraSatisfaccion().setResp(new String[] { datUsers.get("nameWitness2") });
                getDatEnteraSatisfaccion().setOper(new String[] { "testigo2_enterasatisfaccion" });
            } else {
                getDatEnteraSatisfaccion().setEmail(datUsers.get("emailUserCapture").concat(";").concat(datUsers.get("emailUserValidate")));
                getDatEnteraSatisfaccion().setResp(new String[] { "CONSULTA_ENTERASATISFACCION" });
                getDatEnteraSatisfaccion().setOper(new String[] { "consulta_enterasatisfaccion" });
            }
        }
    }

    private StringBuilder bodyEmailReject(Connection conn, Map<String, String> datUser) throws Exception {
        String cEntregaEnteraSatisfaccion = null;
        StringBuilder mailBody = null;
        try {
            cEntregaEnteraSatisfaccion = Integer.parseInt(datUser.get("serviceRendered")) == SolicitudFirmaElectronica.SERVICIO_NO_PRESTADO_ENSA ? "NO" : "SI";
            mailBody = new StringBuilder("<html>");
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
            mailBody.append("\n\t\t<form id=\"Form\" name=\"FormEnteraSatisfacsion\" > ");
            mailBody.append("	<b> C." + datUser.get("nameUserCapture") + "</b> ");
            mailBody.append("	<br><br> ");
            mailBody.append("Fue rechazado el proceso de entera satisfacción con folio ");
            mailBody.append(getDatEnteraSatisfaccion().getcFolio());
            mailBody.append(" del contrato SAI " + getDatEnteraSatisfaccion().getcIdContratoDefinitivo());
            mailBody.append(" a favor de " + datUser.get("rfc") + " " + datUser.get("supplier"));
            mailBody.append("	<br> <br> ");
            mailBody.append("	<table> ");
            mailBody.append("		<thead> ");
            mailBody.append("			<tr> ");
            mailBody.append("				<th>Folio</th> ");
            mailBody.append("				<th>Mes de Pago</th> ");
            mailBody.append("				<th>Firmante</th> ");
            mailBody.append("				<th>¿El servicio fue entregado a entera satisfacción?</th> ");
            mailBody.append("				<th>Testigo 1</th> ");
            mailBody.append("				<th>Testigo 2</th> ");
            mailBody.append("			</tr> ");
            mailBody.append("		</thead> ");
            mailBody.append("		<tbody> ");
            mailBody.append("<tr> ");
            mailBody.append("\n<td>" + getDatEnteraSatisfaccion().getcFolio() + "</td> ");
            mailBody.append("\n<td>" + datUser.get("paymentMonth") + "</td> ");
            mailBody.append("\n<td>" + datUser.get("nameSignatory") + "</td> ");
            mailBody.append("\n<td>" + cEntregaEnteraSatisfaccion + "</td> ");
            mailBody.append("\n<td>" + datUser.get("nameWitness1") + "</td> ");
            mailBody.append("\n<td>" + datUser.get("nameWitness2") + "</td> ");
            mailBody.append("</tr> ");
            mailBody.append("		</tbody> ");
            mailBody.append("	</table> ");
            mailBody.append("	<br /> ");
            mailBody.append("	<br /> ");
            mailBody.append("	<p> ");
            mailBody.append("		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + com.syc.gestion.util.Util.getToday());
            mailBody.append("	</p> ");
            mailBody.append("	</form> ");
            mailBody.append("</body> ");
            mailBody.append("</html> ");
        } finally {
            cEntregaEnteraSatisfaccion = null;
        }
        return mailBody;
    }

    private StringBuilder bodyEmail(Connection conn, Map<String, String> datUser) throws Exception {
        ConfiguraAplicativoBusinessLogic cabl = null;
        String urlAutorizacion = null;
        String urlDescarga = null;
        String cEntregaEnteraSatisfaccion = null;
        StringBuilder mailBody = null;
        try {
            cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
            urlAutorizacion = cabl.getSystemSetting("URL_SAI") + "/egresos/AutENSA";
            urlDescarga = generaURLDescarga(conn);
            cEntregaEnteraSatisfaccion = Integer.parseInt(datUser.get("serviceRendered")) == SolicitudFirmaElectronica.SERVICIO_NO_PRESTADO_ENSA ? "NO" : "SI";
            mailBody = new StringBuilder("<html>");
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
            mailBody.append("\n\t\t<form id=\"Form\" name=\"FormEnteraSatisfacsion\" > ");
            if (getDatEnteraSatisfaccion().getnIdEstatus() == SolicitudFirmaElectronica.ESTATUS_ENSA_FIRMA) {
                mailBody.append("	<b> C." + datUser.get("nameSignatory") + "</b> ");
                mailBody.append("	<br> ");
                mailBody.append("	<b>" + datUser.get("positionSignatory") + "</b> ");
                mailBody.append("	<br><br> ");
                mailBody.append("Se solicita de su firma al proceso de entera satisfacción con folio ");
            } else {
                if (getDatEnteraSatisfaccion().getnIdEstatus() == SolicitudFirmaElectronica.ESTATUS_ENSA_TESTIGO1) {
                    //Testigo 1
                    mailBody.append("	<b> C." + datUser.get("nameWitness1") + "</b> ");
                    mailBody.append("	<br> ");
                    mailBody.append("	<b>" + datUser.get("positionWitness1") + "</b> ");
                } else if (getDatEnteraSatisfaccion().getnIdEstatus() == SolicitudFirmaElectronica.ESTATUS_ENSA_TESTIGO2) {
                    //Testigo 2
                    mailBody.append("	<b> C." + datUser.get("nameWitness2") + "</b> ");
                    mailBody.append("	<br> ");
                    mailBody.append("	<b>" + datUser.get("positionWitness2") + "</b> ");
                }
                mailBody.append("	<br><br> ");
                if (getDatEnteraSatisfaccion().getnIdEstatus() == SolicitudFirmaElectronica.ESTATUS_ENSA_CONSULTA) {
                    mailBody.append("Se notifica que ha finalizado el proceso de entera satisfacción con folio ");
                } else {
                    mailBody.append("Se solicita aceptar al proceso de entera satisfacción con folio ");
                }
            }
            mailBody.append(getDatEnteraSatisfaccion().getcFolio());
            mailBody.append(" del contrato SAI " + getDatEnteraSatisfaccion().getcIdContratoDefinitivo());
            mailBody.append(" a favor de " + datUser.get("rfc") + " " + datUser.get("supplier"));
            mailBody.append("	<br> <br> ");
            mailBody.append("	<table> ");
            mailBody.append("		<thead> ");
            mailBody.append("			<tr> ");
            mailBody.append("				<th>Folio</th> ");
            mailBody.append("				<th>Mes de Pago</th> ");
            mailBody.append("				<th>Firmante</th> ");
            mailBody.append("				<th>¿El servicio fue entregado a entera satisfacción?</th> ");
            mailBody.append("				<th>Testigo 1</th> ");
            mailBody.append("				<th>Testigo 2</th> ");
            mailBody.append("			</tr> ");
            mailBody.append("		</thead> ");
            mailBody.append("		<tbody> ");
            mailBody.append("<tr> ");
            mailBody.append("\n<td>" + getDatEnteraSatisfaccion().getcFolio() + "</td> ");
            mailBody.append("\n<td>" + datUser.get("paymentMonth") + "</td> ");
            mailBody.append("\n<td>" + datUser.get("nameSignatory") + "</td> ");
            mailBody.append("\n<td>" + cEntregaEnteraSatisfaccion + "</td> ");
            mailBody.append("\n<td>" + datUser.get("nameWitness1") + "</td> ");
            mailBody.append("\n<td>" + datUser.get("nameWitness2") + "</td> ");
            mailBody.append("</tr> ");
            mailBody.append("		</tbody> ");
            mailBody.append("	</table> ");
            mailBody.append("	<br /> ");
            mailBody.append("	<br /> ");
            mailBody.append(generaLigaDoctos(conn, urlDescarga));
            if (getDatEnteraSatisfaccion().getnIdEstatus() < SolicitudFirmaElectronica.ESTATUS_ENSA_CONSULTA) {
                if (getDatEnteraSatisfaccion().getnIdEstatus() == SolicitudFirmaElectronica.ESTATUS_ENSA_FIRMA)
                    mailBody.append("\n<b>Para autorizar o rechazar esta solicitud por favor de click <a href=\"");
                else
                    mailBody.append("\n<b>Para acptar esta solicitud por favor de click <a href=\"");
                mailBody.append(urlAutorizacion);
                if (getDatEnteraSatisfaccion().getnIdEstatus() == SolicitudFirmaElectronica.ESTATUS_ENSA_FIRMA) {
                    mailBody.append(generaAccessoAutToken(Integer.parseInt(datUser.get("numberSignatory")), getDocument(), getDatEnteraSatisfaccion().getnServicioEnteraSatisfaccion()));
                } else if (getDatEnteraSatisfaccion().getnIdEstatus() == SolicitudFirmaElectronica.ESTATUS_ENSA_TESTIGO1) {
                    mailBody.append(generaAccessoAutToken(Integer.parseInt(datUser.get("numberWitness1")), getDocument(), getDatEnteraSatisfaccion().getnServicioEnteraSatisfaccion()));
                } else if (getDatEnteraSatisfaccion().getnIdEstatus() == SolicitudFirmaElectronica.ESTATUS_ENSA_TESTIGO2) {
                    mailBody.append(generaAccessoAutToken(Integer.parseInt(datUser.get("numberWitness2")), getDocument(), getDatEnteraSatisfaccion().getnServicioEnteraSatisfaccion()));
                }
                mailBody.append("\" > aquí </a>.</b> ");
            }
            mailBody.append("	<br /> ");
            mailBody.append("	<br /> ");
            mailBody.append("	<p> ");
            mailBody.append("		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + com.syc.gestion.util.Util.getToday());
            mailBody.append("	</p> ");
            mailBody.append("	</form> ");
            mailBody.append("</body> ");
            mailBody.append("</html> ");
        } finally {
            cabl = null;
            urlAutorizacion = null;
            urlDescarga = null;
            cEntregaEnteraSatisfaccion = null;
        }
        return mailBody;
    }

    private StringBuilder generaLigaDoctos(Connection conn, String urlDescarga) throws Exception {
        StringBuilder expedientBody = new StringBuilder();
        expedientBody.append("	<br />");
        expedientBody.append("	<p>");
        if (getDatEnteraSatisfaccion().getnIdEstatus() == SolicitudFirmaElectronica.ESTATUS_ENSA_FIRMA) {
            expedientBody.append("	En la tabla siguiente puede revisar el(los) documento(s) que sera(n) firmado(s).");
        } else {
            expedientBody.append("	En la tabla siguiente puede revisar el(los) documento(s) firmado(s).");
        }
        expedientBody.append("	</p>");
        expedientBody.append("<table id=\"docsTbl\">");
        expedientBody.append("		<thead>");
        expedientBody.append("			<tr>");
        expedientBody.append("				<th>Link del Archivo</th>");
        expedientBody.append("				<th>Nombre del Archivo</th>");
        expedientBody.append("			</tr>");
        expedientBody.append("		</thead>");
        expedientBody.append("		<tbody>");
        expedientBody.append("			<tr>");
        expedientBody.append("				<td>");
        expedientBody.append("				<a href=\"" + urlDescarga + generaRutaArchivo(conn, getDocument(), getIdField()) + "\" > Anexo 1A </a>");
        expedientBody.append("				</td>");
        expedientBody.append("				<td>");
        expedientBody.append("				Anexo 1A.");
        expedientBody.append("				</td>");
        expedientBody.append("			</tr>");
        if (getDatEnteraSatisfaccion().getnServPrestEnteraSatisfaccion() == SolicitudFirmaElectronica.SERVICIO_NO_PRESTADO_ENSA) {
            setDocName("Acta_Hechos");
            expedientBody.append("			<tr>");
            expedientBody.append("				<td>");
            expedientBody.append("				<a href=\"" + urlDescarga + generaRutaArchivo(conn, getDocument(), getIdField()) + "\" > Acta de Hechos </a>");
            expedientBody.append("				</td>");
            expedientBody.append("				<td>");
            expedientBody.append("				Acta Circunstanciada de Hechos.");
            expedientBody.append("				</td>");
            expedientBody.append("			</tr>");
        }
        expedientBody.append("</table>");
        expedientBody.append("	<br />");
        expedientBody.append("	<br />");
        return expedientBody;
    }

    private String generaRutaArchivo(Connection conn, String document, int idField) throws Exception {
        Caso c = CasoManager.select(conn, idField);
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

    public String getRutaReporteImpreso(Connection conn) throws SQLException, AutRecepcionMaterialException {
        return getDocumentoFirma(conn).getFullPathFilesNames()[0];
    }

    private Documento getDocumentoFirma(Connection conn) throws SQLException, AutRecepcionMaterialException {
        int idGabinete = getIDGabinete(conn);
        return DocumentoManager.buscaDocumento(conn, getDocument(), idGabinete, getDocName());
    }

    public void updateSignedDocto(Connection conn, File signedFile) throws SQLException, AutRecepcionMaterialException {
        Documento d = getDocumentoFirma(conn);
        if (d == null)
            throw new AutRecepcionMaterialException("No se encontro el documento firmado para actualizar.");
        DocumentoManager.respaldaPagina(conn, d);
        Pagina pagina = d.getPaginaDocumento(0);
        String nomArchivo = com.syc.gestion.util.Util.getFileWithoutExtencion(signedFile.getName());
        pagina.setNomArchivoOrg(nomArchivo + ".pdf");
        pagina.setNomArchivoVol(nomArchivo + ".tif");
        d.setPaginasDocumento(new Pagina[] { pagina });
        DocumentoManager.actualizaRutaPagina(conn, d);
    }

    public void registrarBitacora(Connection conn, String estatus) throws Exception {
        ProcesoEnteraSatisfaccionManager.registraBitacora(conn, estatus, getDocument(), getDatEnteraSatisfaccion().getnServicioEnteraSatisfaccion(), getUsuario().getLogin());
    }

    @Override
    public String notificaPrefirmante(Connection conn) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
