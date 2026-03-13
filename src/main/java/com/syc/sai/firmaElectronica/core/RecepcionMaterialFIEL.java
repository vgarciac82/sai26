package com.syc.sai.firmaElectronica.core;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import com.syc.adquisiciones.core.DatosRecepcionFIEL;
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
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.contratos.RecepcionMaterialManager;
import com.syc.sai.firmaElectronica.exceptions.AutRecepcionMaterialException;
import com.syc.sai.firmaElectronica.exceptions.FirmaElectronicaException;
import com.syc.sai.firmaElectronica.exceptions.NotEmptyDocumentException;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecepcionMaterialFIEL extends SolicitudFirmaElectronica {

    private static final Logger log = LogManager.getLogger(RecepcionMaterialFIEL.class);

    private DataSourceManager ds = null;

    private String motivoRechazo;

    private DatosRecepcionFIEL recepcionMaterial;

    /**
     */
    public RecepcionMaterialFIEL() {
        super();
        ds = new DataSourceManager() {
        };
    }

    /**
     * Crea el expediente de un proceso de contrato diverso
     *
     * @param conn
     * @throws AutRecepcionMaterialException
     */
    private int creaExpediente(Connection conn) throws AutRecepcionMaterialException {
        try {
            int idCaso = findIDCaso(conn);
            Caso rc = new Caso();
            rc.setIdCaso(idCaso);
            rc = CasoManager.select(conn, rc);
            Aplicacion app = AplicacionManager.select(conn, rc.getTipoCaso().getGavetaAsociada());
            int idGabiente = AplicacionManager.createExpediente(conn, getUsuario().getLogin(), rc, app);
            rc.setIdGabinete(idGabiente);
            CasoManager.update(conn, rc);
            return idGabiente;
        } catch (SQLException e) {
            throw new AutRecepcionMaterialException("Error de base de datos al crear expediente: " + e, e);
        }
    }

    /**
     * Busca el caso de contrato diverso dueño de la recepcion de material.
     *
     * @param conn
     * @return ID Caso.
     */
    private int findIDCaso(Connection conn) throws AutRecepcionMaterialException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	id_caso ");
        query.append("  FROM	cg_caso WITH(NOLOCK) ");
        query.append(" WHERE	id_tc = 9 ");
        query.append("   AND	c_folio LIKE '%-%-' + ? ");
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, String.valueOf(getIdField()));
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
            else
                throw new AutRecepcionMaterialException("No se encontro caso con id: " + getIdField() + " id_tc = 9 ");
        } catch (SQLException e) {
            throw new AutRecepcionMaterialException("Error de base de datos buscando caso para el id: " + getIdField() + " : " + e, e);
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }

    private void findIDField(Connection conn) throws SQLException, AutRecepcionMaterialException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	id_caso ");
        query.append("  FROM   pcontratodiverso WITH(nolock) ");
        query.append(" WHERE	cidcontrato = ?  ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, getRecepcionMaterial().getcIdPedContDef());
            rs = ps.executeQuery();
            if (rs.next())
                setIdField(rs.getInt(1));
            else
                throw new AutRecepcionMaterialException("No se encontro folio SAI para el contrato: " + getRecepcionMaterial().getcIdPedContDef());
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    @Override
    public String generaArchivoFirma(Connection conn, Volumen vol, Carpeta folder, boolean isSignedCopy) throws FirmaElectronicaException {
        Map<String, Object> parametrosReporte = new HashMap<String, Object>();
        parametrosReporte.put("nIDNota", new Integer(getRecepcionMaterial().getIdNota()));
        parametrosReporte.put("SUBREPORT_DIR", getReportPath() + File.separatorChar + "FIEL");
        String filename = null;
        try {
            filename = generaArchivoFirma(conn, vol, folder, parametrosReporte, "FIEL", "rptNotaMateriales.jasper", isSignedCopy);
        } catch (NotEmptyDocumentException nede) {
            log.warn("El documento no esta vacio. Se ignora" + nede);
        } catch (Exception e) {
            throw new FirmaElectronicaException(e);
        }
        return filename;
    }

    private String generaArchivoRecepcionMaterial(Connection conn, String folderName) throws AutRecepcionMaterialException {
        String filename = "";
        try {
            Volumen vol = VolumenManager.getVolumen(conn);
            int idCabinet = -1;
            idCabinet = getIDGabineteRM(conn);
            if (idCabinet <= 0)
                idCabinet = creaExpediente(conn);
            Carpeta folder = CarpetaManager.getCarpetaByName(conn, getDocument(), idCabinet, folderName);
            if (folder == null)
                folder = createFolder(conn, idCabinet, folderName);
            if (DocumentoManager.existeDocumentoCapturado(conn, folder.getTituloAplicacion(), folder.getIdGabinete(), folder.getIdCarpeta(), getDocName()))
                return DocumentoManager.getDocumento(conn, folder.getTituloAplicacion(), folder.getIdGabinete(), folder.getIdCarpeta(), getDocName()).getFullPathFilesNames()[0];
            filename = generaArchivoFirma(conn, vol, folder, false);
            return filename;
        } catch (SQLException e) {
            throw new AutRecepcionMaterialException("Error de base de datos al generar el documento: " + e.toString(), e);
        } catch (Exception e) {
            throw new AutRecepcionMaterialException("Error al generar el documento: " + e.toString(), e);
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
        expedientBody.append("				<a href=\"" + urlDescarga + generaRutaArchivo(conn, numeroEmpleado, getDocument(), getIdField()) + "\" > Atenta Nota </a>");
        expedientBody.append("				</td>");
        expedientBody.append("				<td>");
        expedientBody.append("				Firma de " + operacion + " de la recepci&oacute;n de material.");
        expedientBody.append("				</td>");
        expedientBody.append("			</tr>");
        expedientBody.append("</table>");
        expedientBody.append("	<br />");
        expedientBody.append("	<br />");
        return expedientBody;
    }

    private String generaRutaArchivo(Connection conn, int numeroEmpleado, String document, int idField) throws Exception {
        Caso c = CasoManager.findByFolioLike(conn, document, String.valueOf(idField));
        Documento documento = DocumentoManager.buscaDocumento(conn, document, c.getIdGabinete(), getDocName());
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
    public String getAutLegend(Connection conn) throws Exception {
        throw new Exception("getAutLegend No implementado.");
    }

    @Override
    public String getAutNombre(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	U_NOMBRE  ");
        query.append("   FROM	CG_USUARIO WITH(NOLOCK)  ");
        query.append("  WHERE	cNumeroEmpleado =  ?  ");
        query.append("    AND	U_ESTATUS = 'A'");
        PreparedStatement psNombre = null;
        ResultSet rsNombre = null;
        try {
            int numeroEmpleado = getRecepcionMaterial().getNumeroEmpleado();
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

    @Override
    public String getAutPuesto(Connection conn) throws AutRecepcionMaterialException {
        StringBuilder queryPuesto = new StringBuilder("SELECT	CARGO ");
        queryPuesto.append("  FROM	v_empleados_giro WITH(NOLOCK) ");
        queryPuesto.append(" WHERE	CLAVE = ? ");
        PreparedStatement psPuesto = null;
        ResultSet rsPuesto = null;
        try {
            int numeroEmpleado = getRecepcionMaterial().getNumeroEmpleado();
            psPuesto = conn.prepareStatement(queryPuesto.toString());
            psPuesto.setInt(1, numeroEmpleado);
            rsPuesto = psPuesto.executeQuery();
            if (rsPuesto.next()) {
                if (StringUtils.isBlank(rsPuesto.getString("CARGO")))
                    throw new AutRecepcionMaterialException("El empleado con Numero: " + numeroEmpleado + " no tiene puesto asignado. Notifique al administrador");
                return rsPuesto.getString("CARGO");
            } else
                throw new AutRecepcionMaterialException("No se encontro empleado con numero: " + numeroEmpleado);
        } catch (SQLException e) {
            throw new AutRecepcionMaterialException("Error de base de datos buscando empleado: " + e, e);
        } finally {
            CloseObject.closeObject(psPuesto);
            CloseObject.closeObject(rsPuesto);
        }
    }

    public String getConceptoContrato(Connection conn) throws AutRecepcionMaterialException {
        String objetoContrato = null;
        StringBuilder query = new StringBuilder();
        query.append("SELECT cidcontrato + ' A favor de: ' + cidrfc ");
        query.append("       + ' por el concepto de <br><p><i>' ");
        query.append("       + cconceptocontrato + '</i></p><br>' ");
        query.append("FROM   vlistacontratos ");
        query.append("WHERE  cidcontrato = ?   ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, getRecepcionMaterial().getcIdPedContDef());
            rs = ps.executeQuery();
            if (rs.next()) {
                objetoContrato = rs.getString(1);
            } else
                throw new AutRecepcionMaterialException("No se encontro informacion para el contrato.");
            return objetoContrato;
        } catch (SQLException e) {
            throw new AutRecepcionMaterialException("Error de base de datos mientras se buscaba el objeto del contrato: " + e, e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    @Override
    public String getCorreoAutoriza(Connection conn) throws AutRecepcionMaterialException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	U_EMAIL  ");
        query.append("   FROM	CG_USUARIO WITH(NOLOCK)  ");
        query.append("  WHERE	cNumeroEmpleado =  ?  ");
        query.append("    AND	U_ESTATUS = 'A'");
        PreparedStatement psMail = null;
        ResultSet rsMail = null;
        try {
            int numeroEmpleado = getRecepcionMaterial().getNumeroEmpleado();
            psMail = conn.prepareStatement(query.toString());
            psMail.setInt(1, numeroEmpleado);
            rsMail = psMail.executeQuery();
            if (rsMail.next()) {
                if (StringUtils.isBlank(rsMail.getString("U_EMAIL")))
                    throw new AutRecepcionMaterialException("El empleado con Numero: " + numeroEmpleado + " no tiene correo asignado. Notifique al administrador");
                return rsMail.getString("U_EMAIL");
            } else
                throw new AutRecepcionMaterialException("No se encontro empleado con numero: " + numeroEmpleado);
        } catch (SQLException e) {
            throw new AutRecepcionMaterialException("Error de base de datos buscando correo de autorizador. Notifique al administrador " + e, e);
        } finally {
            CloseObject.closeObject(psMail);
            CloseObject.closeObject(rsMail);
        }
    }

    private String[] getCorreoElabora(Connection conn) throws SQLException, AutRecepcionMaterialException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT u_nombre, u_email ");
        query.append("FROM   tnotaautorizarm rm WITH(nolock) ");
        query.append("       INNER JOIN cg_usuario u WITH(nolock) ");
        query.append("               ON rm.cidusuariocaptura = u.u_login ");
        query.append("WHERE  nidnota = ?  ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, getRecepcionMaterial().getIdNota());
            rs = ps.executeQuery();
            if (rs.next())
                return new String[] { rs.getString(1), rs.getString(2) };
            else
                throw new AutRecepcionMaterialException("No se encontro informacion del usuario que registro la solicitud: " + getRecepcionMaterial().getcIdRecepcionMat());
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    @Override
    public String getCuerpoCorreoAutoriza(Connection conn) throws Exception {
        ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
        String urlAutorizacion = cabl.getSystemSetting("URL_SAI") + "/egresos/AutRM";
        // ConfiguraAplicativoManager.getSystemSetting(
        String urlDescarga = generaURLDescarga(conn);
        // conn, "URL_SAI" ) +
        // "/muestraDocumento";
        String nombreCompleto = getAutNombre(conn);
        String puesto = getAutPuesto(conn);
        String concepto = getConceptoContrato(conn);
        String[] importe = getImporteContratoStr(conn);
        int numeroEmpleado = getRecepcionMaterial().getNumeroEmpleado();
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
        mailBody.append("	<b> C." + nombreCompleto + "</b> ");
        mailBody.append("	<br> ");
        mailBody.append("	<b>" + puesto + "</b> ");
        mailBody.append("	<br /> ");
        mailBody.append("		Se solicita de su autorización de la Recepcion de Material: ");
        mailBody.append(getRecepcionMaterial().getcIdRecepcionMat());
        mailBody.append(" del contrato SAI " + concepto);
        mailBody.append("	<table> ");
        mailBody.append("		<thead> ");
        mailBody.append("			<tr> ");
        mailBody.append("				<th>RM</th> ");
        mailBody.append("				<th>Importe Bruto</th> ");
        mailBody.append("				<th>Impuestos</th> ");
        mailBody.append("				<th>Total</th> ");
        mailBody.append("			</tr> ");
        mailBody.append("		</thead> ");
        mailBody.append("		<tbody> ");
        mailBody.append("<tr> ");
        mailBody.append("\n<td>" + getRecepcionMaterial().getcIdRecepcionMat() + "</td> ");
        mailBody.append("\n<td>" + importe[0] + "</td> ");
        mailBody.append("\n<td>" + importe[2] + "</td> ");
        mailBody.append("\n<td>" + importe[1] + "</td> ");
        mailBody.append("</tr> ");
        mailBody.append("		</tbody> ");
        mailBody.append("	</table> ");
        mailBody.append("	<br /> ");
        mailBody.append("	<br /> ");
        mailBody.append(generaLigaDoctos(conn, urlDescarga, numeroEmpleado, "Autorizacion").toString());
        mailBody.append("\n<b>Para autorizar o rechazar esta solicitud por favor de click <a href=\"");
        mailBody.append(urlAutorizacion);
        mailBody.append(generaAccessoAutToken(numeroEmpleado, getDocument(), getRecepcionMaterial().getIdNota()));
        mailBody.append("\" > aquí </a>.</b> ");
        mailBody.append("	<br /> ");
        mailBody.append("	<br /> ");
        mailBody.append("	<p> ");
        mailBody.append("		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday());
        mailBody.append("	</p> ");
        mailBody.append("	</form> ");
        mailBody.append("</body> ");
        mailBody.append("</html> ");
        return mailBody.toString();
    }

    @Override
    public String getCuerpoCorreoVistoBueno(Connection conn) throws Exception {
        ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
        String urlAutorizacion = cabl.getSystemSetting("URL_SAI") + "/egresos/AutRM";
        String nombreCompleto = getAutNombre(conn);
        String puesto = getAutPuesto(conn);
        String concepto = getConceptoContrato(conn);
        String[] importe = getImporteContratoStr(conn);
        int numeroEmpleado = getRecepcionMaterial().getNumeroEmpleadoVoBo();
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
        mailBody.append("	<b> C." + nombreCompleto + "</b> ");
        mailBody.append("	<br> ");
        mailBody.append("	<b>" + puesto + "</b> ");
        mailBody.append("	<br /> ");
        mailBody.append("		Se solicita el visto bueno de la Recepción de Material: ");
        mailBody.append(getRecepcionMaterial().getcIdRecepcionMat());
        mailBody.append(" del contrato SAI " + concepto);
        mailBody.append("	<table> ");
        mailBody.append("		<thead> ");
        mailBody.append("			<tr> ");
        mailBody.append("				<th>RM</th> ");
        mailBody.append("				<th>Importe Bruto</th> ");
        mailBody.append("				<th>Impuestos</th> ");
        mailBody.append("				<th>Total</th> ");
        mailBody.append("			</tr> ");
        mailBody.append("		</thead> ");
        mailBody.append("		<tbody> ");
        mailBody.append("<tr> ");
        mailBody.append("\n<td>" + getRecepcionMaterial().getcIdRecepcionMat() + "</td> ");
        mailBody.append("\n<td>" + importe[0] + "</td> ");
        mailBody.append("\n<td>" + importe[2] + "</td> ");
        mailBody.append("\n<td>" + importe[1] + "</td> ");
        mailBody.append("</tr> ");
        mailBody.append("		</tbody> ");
        mailBody.append("	</table> ");
        mailBody.append("	<br /> ");
        mailBody.append("	<br /> ");
        mailBody.append("\n<b>Para visto bueno o rechazar esta solicitud por favor de click <a href=\"");
        mailBody.append(urlAutorizacion);
        mailBody.append(generaAccessoAutToken(numeroEmpleado, getDocument(), getRecepcionMaterial().getIdNota()));
        mailBody.append("\" > aquí </a>.</b> ");
        mailBody.append("	<br /> ");
        mailBody.append("	<br /> ");
        mailBody.append("	<p> ");
        mailBody.append("		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday());
        mailBody.append("	</p> ");
        mailBody.append("	</form> ");
        mailBody.append("</body> ");
        mailBody.append("</html> ");
        return mailBody.toString();
    }

    private Documento getDocumentoFirma(Connection conn) throws SQLException, AutRecepcionMaterialException {
        setDocName("Atenta Nota ".concat(getRecepcionMaterial().getcIdRecepcionMat()));
        int idGabinete = getIDGabineteRM(conn);
        return DocumentoManager.buscaDocumento(conn, getDocument(), idGabinete, getDocName());
    }

    public void getFolioContrato(Connection conn) throws SQLException, AutRecepcionMaterialException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT id_caso AS folioContrato ");
        query.append("FROM   vlistacontratos ");
        query.append("WHERE  cidcontrato = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, getRecepcionMaterial().getcIdPedContDef());
            rs = ps.executeQuery();
            if (rs.next())
                setIdField(rs.getInt(1));
            else
                throw new AutRecepcionMaterialException("No se encontro folio para el contrato: " + getRecepcionMaterial().getcIdPedContDef());
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public int getIDGabineteRM(Connection conn) throws SQLException, AutRecepcionMaterialException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append("SELECT ID_GABINETE FROM imx");
        query.append(getDocument());
        query.append(" WITH(NOLOCK) WHERE folio LIKE '%-%-' + ?");
        findIDField(conn);
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, String.valueOf(getIdField()));
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

    /**
     * Devuelve los importes de la RM. Antes de IVA, IVA y Total.
     *
     * @param conn
     *            Conexion activa a la DB
     * @return Arreglo con 3 valores: [Monto Antes de IVA, Monto Total, IVA ]
     * @throws AutRecepcionMaterialException
     */
    private String[] getImporteContratoStr(Connection conn) throws AutRecepcionMaterialException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT CONVERT(VARCHAR(32), mmontosiniva, 103) AS mMontoSinIVA, ");
        query.append("       CONVERT(VARCHAR(32), mmontoconiva, 103) AS mMontoConIVA, ");
        query.append("       CONVERT(VARCHAR(32), mmontoiva, 103)    AS mMontoIVA ");
        query.append("FROM   mrecepcionpmat WITH(nolock) ");
        query.append("WHERE  cidpedcontdef = ? ");
        query.append("       AND cidrecepmat = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, getRecepcionMaterial().getcIdPedContDef());
            ps.setString(2, getRecepcionMaterial().getcIdRecepcionMat());
            rs = ps.executeQuery();
            if (rs.next())
                return new String[] { rs.getString("mMontoSinIVA"), rs.getString("mMontoConIVA"), rs.getString("mMontoIVA") };
            else
                throw new AutRecepcionMaterialException("No se encontro informacion de la RM ");
        } catch (SQLException e) {
            throw new AutRecepcionMaterialException("Error de DB mientras se buscaba informacion de la RM: " + e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    @Override
    public String getImporteStr(Connection conn) throws Exception {
        throw new Exception("getImporteStr No implementado.");
    }

    public String getMotivoRechazo() {
        return this.motivoRechazo;
    }

    public DatosRecepcionFIEL getRecepcionMaterial() {
        return recepcionMaterial;
    }

    public String getRutaReporteImpreso(Connection conn) throws SQLException, AutRecepcionMaterialException {
        return getDocumentoFirma(conn).getFullPathFilesNames()[0];
    }

    @Override
    public String getVoBoLegend(Connection conn) throws Exception {
        throw new Exception(" getVoBoLegend No implementado.");
    }

    public void notificaCancelacion(Connection conn) throws Exception {
        String subject = "Recepcion de material: " + getRecepcionMaterial().getcIdRecepcionMat() + " del contrato: " + getRecepcionMaterial().getcIdPedContDef() + " rechazado.";
        String[] datosUsuario = getCorreoElabora(conn);
        String nombreCompleto = datosUsuario[0];
        String correo = datosUsuario[1];
        String[] importe = getImporteContratoStr(conn);
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
        mailBody.append("		Se notifica la recepci&oacute;n de material ");
        mailBody.append(getRecepcionMaterial().getcIdRecepcionMat());
        mailBody.append(" del contrato: ");
        mailBody.append(getRecepcionMaterial().getcIdPedContDef());
        mailBody.append(" fue  <b>RECHAZADA</b>:");
        mailBody.append("	</p>");
        mailBody.append("	<table>");
        mailBody.append("		<thead>");
        mailBody.append("			<tr>");
        mailBody.append("				<th>RM</th>");
        mailBody.append("				<th>Importe Bruto</th>");
        mailBody.append("				<th>Impuestos</th>");
        mailBody.append("				<th>Total</th>");
        mailBody.append("			</tr>");
        mailBody.append("		</thead>");
        mailBody.append("		<tbody>");
        mailBody.append("<tr>");
        mailBody.append("\n<td>" + getRecepcionMaterial().getcIdRecepcionMat() + "</td>");
        mailBody.append("\n<td>" + importe[0] + "</td> ");
        mailBody.append("\n<td>" + importe[2] + "</td> ");
        mailBody.append("\n<td>" + importe[1] + "</td> ");
        mailBody.append("</tr>");
        mailBody.append("		</tbody>");
        mailBody.append("	</table>");
        mailBody.append("	<br />");
        mailBody.append("	<p>");
        mailBody.append("		Debido a:<br>");
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
        if (getRecepcionMaterial().getnIdEntraAlmacen() == SolicitudFirmaElectronica.ID_ALMACEN_VIRTUAL) {
            AlarmaManager.procesaAlarmaCNF(conn, "", null, null, subject, correo, null, com.syc.adquisiciones.util.Util.getSystemSetting(conn, SolicitudFirmaElectronica.GP_BCC_ATENTA_NOTA), mailBody.toString());
        } else {
            AlarmaManager.procesaAlarmaCNF(conn, "", null, null, subject, correo, mailBody.toString());
        }
    }

    public void notificaAutorizacionRM(Connection conn, File file) throws Exception {
        String subject = "Recepcion de material: " + getRecepcionMaterial().getcIdRecepcionMat() + " del contrato: " + getRecepcionMaterial().getcIdPedContDef() + " autorizada.";
        String[] datosUsuario = getCorreoElabora(conn);
        String nombreCompleto = datosUsuario[0];
        String correo = datosUsuario[1];
        String[] importe = getImporteContratoStr(conn);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "AtentaNota_" + getRecepcionMaterial().getIdNota() + ".pdf";
        File fsalida = null;
        StringBuilder mailBody = null;
        try {
            Util.copiaArchivo(new FileInputStream(file), file_name);
            fsalida = new File(file_name);
            mailBody = new StringBuilder();
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
            mailBody.append("		Se notifica la recepci&oacute;n de material ");
            mailBody.append(getRecepcionMaterial().getcIdRecepcionMat());
            mailBody.append(" del contrato: ");
            mailBody.append(getRecepcionMaterial().getcIdPedContDef());
            mailBody.append(" fue  <b>AUTORIZADA</b>:");
            mailBody.append("	</p>");
            mailBody.append("	<table>");
            mailBody.append("		<thead>");
            mailBody.append("			<tr>");
            mailBody.append("				<th>RM</th>");
            mailBody.append("				<th>Importe Bruto</th>");
            mailBody.append("				<th>Impuestos</th>");
            mailBody.append("				<th>Total</th>");
            mailBody.append("			</tr>");
            mailBody.append("		</thead>");
            mailBody.append("		<tbody>");
            mailBody.append("<tr>");
            mailBody.append("\n<td>" + getRecepcionMaterial().getcIdRecepcionMat() + "</td>");
            mailBody.append("\n<td>" + importe[0] + "</td> ");
            mailBody.append("\n<td>" + importe[2] + "</td> ");
            mailBody.append("\n<td>" + importe[1] + "</td> ");
            mailBody.append("</tr>");
            mailBody.append("		</tbody>");
            mailBody.append("	</table>");
            mailBody.append("	<br />");
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
            if (getRecepcionMaterial().getnIdEntraAlmacen() == SolicitudFirmaElectronica.ID_ALMACEN_VIRTUAL) {
                AlarmaManager.procesaAlarmaAttachmentCNF(conn, null, null, null, subject, correo, com.syc.adquisiciones.util.Util.getSystemSetting(conn, SolicitudFirmaElectronica.GP_BCC_ATENTA_NOTA), null, mailBody.toString(), fsalida, true);
            } else {
                AlarmaManager.procesaAlarmaAttachmentCNF(conn, null, null, null, subject, correo, null, null, mailBody.toString(), fsalida, true);
            }
        } finally {
            subject = null;
            nombreCompleto = null;
            datosUsuario = null;
            correo = null;
            importe = null;
            file_name = null;
            if (mailBody != null)
                mailBody.delete(0, mailBody.length());
            if (fsalida.exists())
                fsalida.delete();
            mailBody = null;
        }
    }

    public void notificaAutorizacionVoBoRM(Connection conn) throws Exception {
        String subject = "Recepcion de material: " + getRecepcionMaterial().getcIdRecepcionMat() + " del contrato: " + getRecepcionMaterial().getcIdPedContDef() + ", Autorizada por la SCDB.";
        String[] datosUsuario = getCorreoElabora(conn);
        String nombreCompleto = datosUsuario[0];
        String correo = datosUsuario[1];
        String[] importe = getImporteContratoStr(conn);
        StringBuilder mailBody = null;
        try {
            mailBody = new StringBuilder();
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
            mailBody.append("		Se notifica la recepci&oacute;n de material ");
            mailBody.append(getRecepcionMaterial().getcIdRecepcionMat());
            mailBody.append(" del contrato: ");
            mailBody.append(getRecepcionMaterial().getcIdPedContDef());
            mailBody.append(" fue  <b>AUTORIZADA</b>:");
            mailBody.append("	</p>");
            mailBody.append("	<table>");
            mailBody.append("		<thead>");
            mailBody.append("			<tr>");
            mailBody.append("				<th>RM</th>");
            mailBody.append("				<th>Importe Bruto</th>");
            mailBody.append("				<th>Impuestos</th>");
            mailBody.append("				<th>Total</th>");
            mailBody.append("			</tr>");
            mailBody.append("		</thead>");
            mailBody.append("		<tbody>");
            mailBody.append("<tr>");
            mailBody.append("\n<td>" + getRecepcionMaterial().getcIdRecepcionMat() + "</td>");
            mailBody.append("\n<td>" + importe[0] + "</td> ");
            mailBody.append("\n<td>" + importe[2] + "</td> ");
            mailBody.append("\n<td>" + importe[1] + "</td> ");
            mailBody.append("</tr>");
            mailBody.append("		</tbody>");
            mailBody.append("	</table>");
            mailBody.append("	<br />");
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
            AlarmaManager.procesaAlarmaCNF(conn, "", null, null, subject, correo, null, com.syc.adquisiciones.util.Util.getSystemSetting(conn, SolicitudFirmaElectronica.GP_BCC_ATENTA_NOTA), mailBody.toString());
        } finally {
            subject = null;
            nombreCompleto = null;
            datosUsuario = null;
            correo = null;
            importe = null;
            if (mailBody != null)
                mailBody.delete(0, mailBody.length());
            mailBody = null;
        }
    }

    @Override
    public void notificaOperacionMasivaPendiente(Connection conn, String operacion) throws Exception {
        throw new Exception("notificaOperacionMasivaPendiente No implementado.");
    }

    @Override
    public String notificaOperacionPendiente(Connection conn, String tipoAutorizacion) throws AutRecepcionMaterialException {
        try {
            int status = 0;
            if (SolicitudFirmaElectronica.AUT_RM.equals(tipoAutorizacion)) {
                status = SolicitudFirmaElectronica.ESTATUS_RM_AUT;
                if (getRecepcionMaterial().getnIdEntraAlmacen() == SolicitudFirmaElectronica.ID_ALMACEN_VIRTUAL) {
                    status = SolicitudFirmaElectronica.ESTATUS_RM_EMITIDA;
                    AlarmaManager.procesaAlarmaCNF(conn, "", null, null, "Solicitud de autorizacion de ".concat(getRecepcionMaterial().getcIdRecepcionMat()).concat(" del contrato ").concat(getRecepcionMaterial().getcIdPedContDef()), getCorreoAutoriza(conn), null, com.syc.adquisiciones.util.Util.getSystemSetting(conn, SolicitudFirmaElectronica.GP_BCC_ATENTA_NOTA), getCuerpoCorreoAutoriza(conn), false);
                } else {
                    AlarmaManager.procesaAlarmaCNF(conn, "", null, null, "Solicitud de autorizacion de ".concat(getRecepcionMaterial().getcIdRecepcionMat()).concat(" del contrato ").concat(getRecepcionMaterial().getcIdPedContDef()), getCorreoAutoriza(conn), getCuerpoCorreoAutoriza(conn));
                }
            }
            RecepcionMaterialManager.avanzaEstatus(conn, this.getRecepcionMaterial(), status);
            return "success";
        } catch (AutRecepcionMaterialException e) {
            throw e;
        } catch (Exception e) {
            throw new AutRecepcionMaterialException("Error notificando operacion pendiente: " + e, e);
        }
    }

    public String notificaVoBoRM(Connection conn, String tipoAutorizacion) throws AutRecepcionMaterialException {
        try {
            int status = 0;
            int numEmp = getRecepcionMaterial().getNumeroEmpleado();
            if (SolicitudFirmaElectronica.AUT_RM.equals(tipoAutorizacion)) {
                status = SolicitudFirmaElectronica.ESTATUS_RM_VoBo;
                getRecepcionMaterial().setNumeroEmpleado(getRecepcionMaterial().getNumeroEmpleadoVoBo());
                AlarmaManager.procesaAlarmaCNF(conn, "", null, null, "Solicitud de visto bueno de ".concat(getRecepcionMaterial().getcIdRecepcionMat()).concat(" del contrato ").concat(getRecepcionMaterial().getcIdPedContDef()), com.syc.adquisiciones.util.Util.getSystemSetting(conn, SolicitudFirmaElectronica.GP_BCC_ATENTA_NOTA), null, null, getCuerpoCorreoVistoBueno(conn));
                getRecepcionMaterial().setNumeroEmpleado(numEmp);
            }
            RecepcionMaterialManager.avanzaEstatus(conn, this.getRecepcionMaterial(), status);
            return "success";
        } catch (AutRecepcionMaterialException e) {
            throw e;
        } catch (Exception e) {
            throw new AutRecepcionMaterialException("Error notificando operacion pendiente: " + e, e);
        }
    }

    @Override
    public void onCancelaTramite(Connection conn, String reason) throws Exception {
        throw new Exception("onCancelaTramite No implementado.");
    }

    @Override
    public void onFinishAut(Connection conn) throws Exception {
        throw new Exception("onFinishAut No implementado.");
    }

    @Override
    public void onFinishVoBo(Connection conn) throws Exception {
        throw new Exception("onFinishVoBo No implementado.");
    }

    public void saveRecepcionMaterial(Connection conn) throws SQLException {
        RecepcionMaterialManager.saveAutorizacionRM(conn, getRecepcionMaterial());
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public void setRecepcionMaterial(DatosRecepcionFIEL recepcionMaterial) {
        this.recepcionMaterial = recepcionMaterial;
    }

    /**
     * Genera conexion a base de datos para resgistrar la solicitud de
     * autorizacion de RM
     *
     * @throws AutRecepcionMaterialException
     */
    public void solicitaAutorizacionRM() throws AutRecepcionMaterialException {
        boolean error = true;
        Connection conn = null;
        try {
            conn = ds.getConnection();
            saveRecepcionMaterial(conn);
            if (getRecepcionMaterial().getnIdEntraAlmacen() == SolicitudFirmaElectronica.ID_ALMACEN) {
                solicitaVoBoRM(conn);
            } else {
                solicitaAutorizacionRM(conn);
            }
            conn.commit();
            error = false;
        } catch (SQLException e) {
            error = true;
            throw new AutRecepcionMaterialException("Error de base de datos al solicitar autorizacion de RM: " + e.toString(), e);
        } finally {
            if (error) {
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e2) {
                        log.warn("Problemas en rollback: " + e2);
                    }
            }
            CloseObject.closeObject(conn);
        }
    }

    public void reenviaEmailRM() throws AutRecepcionMaterialException {
        boolean error = true;
        Connection conn = null;
        try {
            conn = ds.getConnection();
            findIDField(conn);
            if (getRecepcionMaterial().getnIdEntraAlmacen() == SolicitudFirmaElectronica.ID_ALMACEN_VIRTUAL) {
                AlarmaManager.procesaAlarmaCNF(conn, "", null, null, "Solicitud de autorizacion de ".concat(getRecepcionMaterial().getcIdRecepcionMat()).concat(" del contrato ").concat(getRecepcionMaterial().getcIdPedContDef()), getCorreoAutoriza(conn), null, com.syc.adquisiciones.util.Util.getSystemSetting(conn, SolicitudFirmaElectronica.GP_BCC_ATENTA_NOTA), getCuerpoCorreoAutoriza(conn));
            } else {
                AlarmaManager.procesaAlarmaCNF(conn, "", null, null, "Solicitud de autorizacion de ".concat(getRecepcionMaterial().getcIdRecepcionMat()).concat(" del contrato ").concat(getRecepcionMaterial().getcIdPedContDef()), getCorreoAutoriza(conn), getCuerpoCorreoAutoriza(conn));
            }
            error = false;
            conn.commit();
        } catch (Exception e) {
            error = true;
            throw new AutRecepcionMaterialException("Error al reenviar Email de RM: " + e.toString(), e);
        } finally {
            if (error) {
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e2) {
                        log.warn("Problemas en rollback: " + e2);
                    }
            }
            CloseObject.closeObject(conn);
        }
    }

    /**
     * Resgistra la solicitud de autorizacion de RM
     *
     * @param conn
     *            Conexion activa a la DB
     * @throws AutRecepcionMaterialException
     */
    public void solicitaAutorizacionRM(Connection conn) throws AutRecepcionMaterialException {
        try {
            generaArchivoRecepcionMaterial(conn, "Recepciones de Material");
            notificaOperacionPendiente(conn, SolicitudFirmaElectronica.AUT_RM);
        } catch (Exception e) {
            throw new AutRecepcionMaterialException("Error solicitando autorizacion de Recepcion de Material: " + e.toString(), e);
        }
    }

    public void solicitaVoBoRM(Connection conn) throws AutRecepcionMaterialException {
        try {
            if (getRecepcionMaterial().getRequiereAtentaNota() == 1) {
                generaArchivoRecepcionMaterial(conn, "Recepciones de Material");
            }
            getRecepcionMaterial().setNumeroEmpleadoVoBo(Integer.parseInt(com.syc.adquisiciones.util.Util.getSystemSetting(conn, SolicitudFirmaElectronica.GP_VOBO_ATENTA_NOTA)));
            notificaVoBoRM(conn, SolicitudFirmaElectronica.AUT_RM);
        } catch (Exception e) {
            throw new AutRecepcionMaterialException("Error solicitando el visto bueno de Recepcion de Material: " + e.toString(), e);
        }
    }

    public void updateSignedDocto(Connection conn, File signedFile) throws SQLException, AutRecepcionMaterialException {
        Documento d = getDocumentoFirma(conn);
        if (d == null)
            throw new AutRecepcionMaterialException("No se encontro el documento firmado para actualizar.");
        DocumentoManager.respaldaPagina(conn, d);
        Pagina pagina = d.getPaginaDocumento(0);
        String nomArchivo = Util.getFileWithoutExtencion(signedFile.getName());
        pagina.setNomArchivoOrg(nomArchivo + ".pdf");
        pagina.setNomArchivoVol(nomArchivo + ".tif");
        d.setPaginasDocumento(new Pagina[] { pagina });
        DocumentoManager.actualizaRutaPagina(conn, d);
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
            log.info(voLegend);
        }
        return voLegend;
    }

    @Override
    public String getAutLegendWithName(Connection conn, String nombre, String puesto) {
        try {
            String autLegend = AUT_LEGEND_PREFIX.concat(" Firmado por: ").concat(nombre).concat(" | ").concat(puesto);
            if (tieneDelegatorioAut(conn)) {
                autLegend = AUT_LEGEND_PREFIX + ". Firma " + getTipoSuplenciaAut() + " de " + getNombreEmpleadoSuplidoAut() + " con fundamento en el oficio: " + getFolioOficioAut() + " de fecha: " + getFechaOficioAut();
                log.info(autLegend);
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
