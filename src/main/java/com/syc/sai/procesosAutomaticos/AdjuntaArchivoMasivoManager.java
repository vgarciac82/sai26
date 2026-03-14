package com.syc.sai.procesosAutomaticos;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import com.syc.ejercido.pagado.CLCAttachmentManager;
import com.syc.fortimax.core.Carpeta;
import com.syc.fortimax.core.CarpetaManager;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.fortimax.core.OrgCarpeta;
import com.syc.fortimax.core.OrgCarpetaManager;
import com.syc.fortimax.core.Volumen;
import com.syc.fortimax.core.VolumenManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.utils.zip.ZipManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class AdjuntaArchivoMasivoManager {

    private static final Logger log = LoggerFactory.getLogger(AdjuntaArchivoMasivoManager.class);

    private static final String ceros = "000000";

    public static int adjuntMasivo(Connection conn, String tituloAplicacion, int idCarpeta, int idDocumento, String cCentroContable, int aEjercicioFiscal, int desde, int hasta, String nombreDestino) throws Exception {
        /* FAV20171019 Se guarda en base el prefijo de CxP */
        ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
        String cxpPrefijo = cabl.getSystemSetting("CXP_PREFIJO") == null ? "CP" : cabl.getSystemSetting("CXP_PREFIJO");
        String tablaEncabezado = "t" + tituloAplicacion + "encabezado";
        String cxpBase = String.valueOf(cCentroContable) + cxpPrefijo + String.valueOf(aEjercicioFiscal);
        String queryBuscaNombres = "SELECT NOMBRE_ELEMENTO FROM IMX_ESTRUC_DOCTOS WITH(NOLOCK) WHERE TITULO_APLICACION = ? AND POSICION_ELEMENTO = ?";
        String queryDocumento = "SELECT nFolio" + tituloAplicacion + " FROM " + tablaEncabezado + " WITH( nolock) WHERE canocontrarrecibo = ?";
        String queryExpediente = "SELECT id_gabinete FROM IMX" + tituloAplicacion + " WITH( nolock) WHERE FOLIO LIKE '%-%-' + CONVERT(VARCHAR(32), ?)";
        String querySearchVolumenUnit = "SELECT unidad, " + "       tipo_dispositivo, " + "       ruta_base " + "FROM   imx_unidad_volumen WITH (NOLOCK)" + "WHERE  estado_unidad = ?";
        String querySearchVolumen = "SELECT volumen " + "FROM   imx_volumen WITH (NOLOCK)" + "WHERE  unidad_disco = ?  " + "        AND tipo_volumen = ? AND capacidad = ?";
        String querySearchFilePath = "SELECT ruta_directorio FROM imx_volumen WITH (NOLOCK) " + " WHERE volumen = ? AND unidad_disco = ? AND tipo_volumen = ?";
        String nombreCarpeta = "";
        String nombreDocumento = "";
        int nFolioDocumento;
        int idGabinete = -1;
        PreparedStatement psNombreElemento = null;
        PreparedStatement psDocumento = null;
        PreparedStatement psExpediente = null;
        PreparedStatement psSearchVolumenUnit = null;
        PreparedStatement psSearchVolumen = null;
        PreparedStatement psSearchVolPath = null;
        ResultSet rsDocumento = null;
        ResultSet rsExpediente = null;
        ResultSet rsElemento = null;
        int cnt = 0;
        try {
            psNombreElemento = conn.prepareStatement(queryBuscaNombres);
            psDocumento = conn.prepareStatement(queryDocumento);
            psExpediente = conn.prepareStatement(queryExpediente);
            psSearchVolumenUnit = conn.prepareStatement(querySearchVolumenUnit);
            psSearchVolumen = conn.prepareStatement(querySearchVolumen);
            psSearchVolPath = conn.prepareStatement(querySearchFilePath);
            for (int i = desde; i <= hasta; i++) {
                String numero = ceros.substring(0, ceros.length() - String.valueOf(i).length()) + String.valueOf(i);
                String cxp = cxpBase + numero;
                log.info("Object: {}", "Contrarecibo generado [" + cxp + "]");
                psDocumento.setString(1, cxp);
                rsDocumento = psDocumento.executeQuery();
                if (rsDocumento.next()) {
                    nFolioDocumento = rsDocumento.getInt(1);
                    psExpediente.setInt(1, nFolioDocumento);
                    rsExpediente = psExpediente.executeQuery();
                    if (rsExpediente.next()) {
                        psNombreElemento.setString(1, tituloAplicacion);
                        psNombreElemento.setInt(2, idCarpeta);
                        rsElemento = psNombreElemento.executeQuery();
                        if (rsElemento.next())
                            nombreCarpeta = rsElemento.getString(1);
                        psNombreElemento.setString(1, tituloAplicacion);
                        psNombreElemento.setInt(2, idDocumento);
                        rsElemento = psNombreElemento.executeQuery();
                        if (rsElemento.next()) {
                            nombreDocumento = rsElemento.getString(1);
                        }
                        idGabinete = rsExpediente.getInt("id_gabinete");
                        if ("RELACIONGASTOS".equals(tituloAplicacion)) {
                            String cNombreDocto = "";
                            cNombreDocto = validaEsAlimentacionBrigadistas(conn, nFolioDocumento);
                            File origenDocto = new File(nombreDestino);
                            if (!"".equals(cNombreDocto)) {
                                if (!"".equals(crearCarpetaRG(conn, tituloAplicacion, idGabinete, cNombreDocto, "admin", psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, origenDocto, nombreDestino, (i == hasta), cxp, nFolioDocumento))) {
                                    nombreDocumento = cNombreDocto;
                                    cnt++;
                                }
                            }
                        }
                        if (DocumentoManager.existeDocumento(conn, tituloAplicacion, idGabinete, nombreDocumento) && !DocumentoManager.existeDocumentoCapturado(conn, tituloAplicacion, idGabinete, nombreDocumento)) {
                            Documento d = DocumentoManager.buscaDocumento(conn, tituloAplicacion, idGabinete, nombreDocumento);
                            File origen = new File(nombreDestino);
                            log.trace("Object: {}", "Insertando el archivo: " + origen.getName());
                            String tmpFile = origen.getName();
                            int pos = tmpFile.lastIndexOf('.') + 1;
                            String ext = pos != -1 ? tmpFile.substring(pos) : "";
                            CLCAttachmentManager.insertaDocumento(conn, d.getTituloAplicacion(), d.getIdGabinete(), d.getIdCarpetaPadre(), nombreDocumento, ext, "CARGA_MASIVA", nombreDestino, psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, (i == hasta));
                            log.trace("Archivo insertado exitosamente");
                            cnt++;
                            log.info("Object: {}", "Documento adjuntado exitosamente en el CxP[" + cxp + "]");
                        }
                    }
                }
            }
        } finally {
            CloseObject.closeObject(psNombreElemento);
            CloseObject.closeObject(psDocumento);
            CloseObject.closeObject(psExpediente);
            CloseObject.closeObject(psSearchVolumenUnit);
            CloseObject.closeObject(psSearchVolumen);
            CloseObject.closeObject(psSearchVolPath);
            CloseObject.closeObject(rsDocumento);
            CloseObject.closeObject(rsExpediente);
            CloseObject.closeObject(rsElemento);
        }
        return cnt;
    }

    public static int adjuntaArchivo(Connection conn, String usuario, String tituloAplicacion, int idGabinete, String nombreCarpeta, String nombreDocumento, File archivo) throws Exception {
        Carpeta carpeta = CarpetaManager.getCarpetaByName(conn, tituloAplicacion, idGabinete, nombreCarpeta);
        if (carpeta == null) {
            carpeta = new Carpeta();
            carpeta.setBanderaRaiz("N");
            carpeta.setDescripcion(nombreCarpeta);
            Timestamp fecha = new Timestamp(System.currentTimeMillis());
            carpeta.setFechaCreacion(fecha);
            carpeta.setFechaModificacion(fecha);
            carpeta.setIdCarpeta(CarpetaManager.getNextIdCarpeta(conn, tituloAplicacion, idGabinete));
            carpeta.setNombreCarpeta(nombreCarpeta);
            carpeta.setNombreUsuario(usuario);
            carpeta.setNumeroAccesos(0);
            carpeta.setNumeroCarpetas(0);
            carpeta.setNumeroDocumentos(1);
            carpeta.setPassword("-1");
            carpeta.setTituloAplicacion(tituloAplicacion);
            carpeta.setIdGabinete(idGabinete);
            CarpetaManager.insertaCarpeta(conn, carpeta);
            OrgCarpeta orgcarpeta = new OrgCarpeta();
            orgcarpeta.setIdCarpetaHija(carpeta.getIdCarpeta());
            orgcarpeta.setIdCarpetaPadre(1);
            orgcarpeta.setIdGabinete(idGabinete);
            orgcarpeta.setNombreHija(carpeta.getNombreCarpeta());
            orgcarpeta.setTituloAplicacion(tituloAplicacion);
            OrgCarpetaManager.insert(conn, orgcarpeta);
        }
        Documento doc = DocumentoManager.getDocumento(conn, tituloAplicacion, idGabinete, carpeta.getIdCarpeta(), nombreDocumento);
        if (doc == null) {
            doc = new Documento();
            doc.setAutor(null);
            doc.setClaseDocumento(0);
            doc.setCompartir("N");
            doc.setDescripcion(nombreDocumento);
            doc.setEstadoDocumento("V");
            doc.setEsVersion(0);
            doc.setExtension(Util.getFileExtencion(archivo.getName()));
            Date fecha = new Date(System.currentTimeMillis());
            doc.setFechaCreacion(fecha);
            doc.setFechaModificacion(fecha);
            doc.setFh_vigencia(null);
            doc.setIdCarpetaPadre(carpeta.getIdCarpeta());
            doc.setIdGabinete(idGabinete);
            doc.setIdTipoDocto(1);
            doc.setMateria("ORIGINAL");
            doc.setNombreDocumento(nombreDocumento);
            doc.setNombreUsuario(usuario);
            doc.setNumeroAccesos(0);
            doc.setNumeroPaginas(1);
            doc.setPrioridad(3);
            doc.setTamanoBytes(0);
            doc.setTitulo(null);
            doc.setTituloAplicacion(tituloAplicacion);
            doc.setTokenCompartir(null);
            doc.setIdDocumento(DocumentoManager.getNextIdDocumento(conn, tituloAplicacion, idGabinete, carpeta.getIdCarpeta()));
            doc.setNombreTipoDocto("EXTERNO");
            DocumentoManager.insertDocumento(conn, doc);
        }
        if (doc.getPaginasDocumento() != null && doc.getPaginasDocumento().length > 0) {
            return 0;
        }
        Volumen vol = VolumenManager.getVolumen(conn);
        DocumentoManager.insertPaginaDocumento(conn, vol, doc, "A", 0);
        doc = DocumentoManager.getDocumento(conn, tituloAplicacion, idGabinete, carpeta.getIdCarpeta(), nombreDocumento);
        String filename = doc.getFullPathFilesNames()[doc.getFullPathFilesNames().length - 1];
        Util.copiaArchivo(archivo.getAbsolutePath(), filename);
        return 1;
    }

    public static int adjuntMasivoDocPoliza(Connection conn, String tituloAplicacion, int idCarpeta, int idDocumento, String cCentroContable, int aEjercicioFiscal, List<Rango> rangos, String nombreDestino) throws Exception {
        String queryBuscaNombres = "SELECT	NOMBRE_ELEMENTO " + "  FROM	IMX_ESTRUC_DOCTOS WITH(NOLOCK) " + " WHERE	TITULO_APLICACION = ? " + "   AND	POSICION_ELEMENTO = ? ";
        String queryExpediente = "SELECT	id_gabinete " + "  FROM	IMXPOLIZA WITH( nolock) " + " WHERE	FOLIO LIKE '%-%-' + CONVERT(VARCHAR(32), ?)";
        String querySearchVolumenUnit = "SELECT unidad, " + "       tipo_dispositivo, " + "       ruta_base " + "FROM   imx_unidad_volumen WITH (NOLOCK)" + "WHERE  estado_unidad = ?";
        String querySearchVolumen = "SELECT volumen " + "  FROM   imx_volumen WITH (NOLOCK)" + " WHERE	unidad_disco = ?  " + "   AND	tipo_volumen = ? " + "   AND	capacidad = ?";
        String querySearchFilePath = "SELECT	ruta_directorio " + "  FROM	imx_volumen WITH (NOLOCK) " + " WHERE	volumen = ? " + "   AND	unidad_disco = ? " + "   AND	tipo_volumen = ?";
        String nombreDocumento = "";
        int idGabinete = -1;
        PreparedStatement psNombreElemento = null;
        PreparedStatement psDocumento = null;
        PreparedStatement psExpediente = null;
        PreparedStatement psSearchVolumenUnit = null;
        PreparedStatement psSearchVolumen = null;
        PreparedStatement psSearchVolPath = null;
        ResultSet rsExpediente = null;
        ResultSet rsElemento = null;
        int cnt = 0;
        try {
            psNombreElemento = conn.prepareStatement(queryBuscaNombres);
            psExpediente = conn.prepareStatement(queryExpediente);
            psSearchVolumenUnit = conn.prepareStatement(querySearchVolumenUnit);
            psSearchVolumen = conn.prepareStatement(querySearchVolumen);
            psSearchVolPath = conn.prepareStatement(querySearchFilePath);
            for (Iterator<Rango> iterator = rangos.iterator(); iterator.hasNext(); ) {
                Rango rango = iterator.next();
                int hasta = Math.max(rango.getInicio(), rango.getFin());
                int desde = Math.min(rango.getInicio(), rango.getFin());
                for (int i = desde; i <= hasta; i++) {
                    int nFolioDocPoliza = i;
                    log.info("Object: {}", "Folio de documento Poliza [" + nFolioDocPoliza + "]");
                    psExpediente.setInt(1, nFolioDocPoliza);
                    rsExpediente = psExpediente.executeQuery();
                    if (rsExpediente.next()) {
                        psNombreElemento.setString(1, tituloAplicacion);
                        psNombreElemento.setInt(2, idDocumento);
                        rsElemento = psNombreElemento.executeQuery();
                        if (rsElemento.next()) {
                            nombreDocumento = rsElemento.getString(1);
                        }
                        idGabinete = rsExpediente.getInt("id_gabinete");
                        if (DocumentoManager.existeDocumento(conn, tituloAplicacion, idGabinete, nombreDocumento) && !DocumentoManager.existeDocumentoCapturado(conn, tituloAplicacion, idGabinete, nombreDocumento)) {
                            Documento d = DocumentoManager.buscaDocumento(conn, tituloAplicacion, idGabinete, nombreDocumento);
                            File origen = new File(nombreDestino);
                            log.trace("Object: {}", "Insertando el archivo: " + origen.getName());
                            String tmpFile = origen.getName();
                            int pos = tmpFile.lastIndexOf('.') + 1;
                            String ext = pos != -1 ? tmpFile.substring(pos) : "";
                            CLCAttachmentManager.insertaDocumento(conn, d.getTituloAplicacion(), d.getIdGabinete(), d.getIdCarpetaPadre(), nombreDocumento, ext, "CARGA_MASIVA", nombreDestino, psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, (i == hasta && !iterator.hasNext()));
                            log.trace("Archivo insertado exitosamente");
                            cnt++;
                            log.info("Object: {}", "Documento adjuntado exitosamente en folio[" + nFolioDocPoliza + "]");
                        }
                    }
                }
            }
        } finally {
            CloseObject.closeObject(psNombreElemento);
            CloseObject.closeObject(psDocumento);
            CloseObject.closeObject(psExpediente);
            CloseObject.closeObject(psSearchVolumenUnit);
            CloseObject.closeObject(psSearchVolumen);
            CloseObject.closeObject(psSearchVolPath);
            CloseObject.closeObject(rsExpediente);
            CloseObject.closeObject(rsElemento);
        }
        return cnt;
    }

    public static int adjuntMasivoDocPoliza(Connection conn, String tituloAplicacion, int idCarpeta, int idDocumento, String cCentroContable, int aEjercicioFiscal, Rango rango, String nombreDestino) throws Exception {
        String queryBuscaNombres = "SELECT	NOMBRE_ELEMENTO " + "  FROM	IMX_ESTRUC_DOCTOS WITH(NOLOCK) " + " WHERE	TITULO_APLICACION = ? " + "   AND	POSICION_ELEMENTO = ? ";
        String queryExpediente = "SELECT	id_gabinete " + "  FROM	IMXPOLIZA WITH( nolock) " + " WHERE	FOLIO LIKE '%-%-' + CONVERT(VARCHAR(32), ?)";
        String querySearchVolumenUnit = "SELECT unidad, " + "       tipo_dispositivo, " + "       ruta_base " + "FROM   imx_unidad_volumen WITH (NOLOCK) " + " WHERE  estado_unidad = ?";
        String querySearchVolumen = "SELECT volumen " + "  FROM   imx_volumen WITH (NOLOCK)" + " WHERE	unidad_disco = ?  " + "   AND	tipo_volumen = ? " + "   AND	capacidad = ?";
        String querySearchFilePath = "SELECT	ruta_directorio " + "  FROM	imx_volumen WITH (NOLOCK) " + " WHERE	volumen = ? " + "   AND	unidad_disco = ? " + "   AND	tipo_volumen = ?";
        String nombreDocumento = "";
        int idGabinete = -1;
        PreparedStatement psNombreElemento = null;
        PreparedStatement psDocumento = null;
        PreparedStatement psExpediente = null;
        PreparedStatement psSearchVolumenUnit = null;
        PreparedStatement psSearchVolumen = null;
        PreparedStatement psSearchVolPath = null;
        ResultSet rsExpediente = null;
        ResultSet rsElemento = null;
        int cnt = 0;
        try {
            psNombreElemento = conn.prepareStatement(queryBuscaNombres);
            psExpediente = conn.prepareStatement(queryExpediente);
            psSearchVolumenUnit = conn.prepareStatement(querySearchVolumenUnit);
            psSearchVolumen = conn.prepareStatement(querySearchVolumen);
            psSearchVolPath = conn.prepareStatement(querySearchFilePath);
            int hasta = Math.max(rango.getInicio(), rango.getFin());
            int desde = Math.min(rango.getInicio(), rango.getFin());
            log.info("Object: {}", String.format("Adjuntando archivo %s al expediente tipo %s en la carpeta %d en el documento %d centro contable %s del EF %d desde el folio inicial %d al folio final %d", nombreDestino, tituloAplicacion, idCarpeta, idDocumento, cCentroContable, aEjercicioFiscal, desde, hasta));
            for (int i = desde; i <= hasta; i++) {
                int nFolioDocPoliza = i;
                log.info("Object: {}", "Folio de documento Poliza [" + nFolioDocPoliza + "]");
                psExpediente.setInt(1, nFolioDocPoliza);
                rsExpediente = psExpediente.executeQuery();
                if (rsExpediente.next()) {
                    psNombreElemento.setString(1, tituloAplicacion);
                    psNombreElemento.setInt(2, idDocumento);
                    rsElemento = psNombreElemento.executeQuery();
                    if (rsElemento.next()) {
                        nombreDocumento = rsElemento.getString(1);
                    } else {
                        log.error("Object: {}", "No se encontro documento para el idDocumento " + idDocumento);
                        continue;
                    }
                    idGabinete = rsExpediente.getInt("id_gabinete");
                    if (DocumentoManager.existeDocumento(conn, tituloAplicacion, idGabinete, nombreDocumento) && !DocumentoManager.existeDocumentoCapturado(conn, tituloAplicacion, idGabinete, nombreDocumento)) {
                        Documento d = DocumentoManager.buscaDocumento(conn, tituloAplicacion, idGabinete, nombreDocumento);
                        File origen = new File(nombreDestino);
                        log.trace("Object: {}", "Insertando el archivo: " + origen.getName());
                        String tmpFile = origen.getName();
                        int pos = tmpFile.lastIndexOf('.') + 1;
                        String ext = pos != -1 ? tmpFile.substring(pos) : "";
                        CLCAttachmentManager.insertaDocumento(conn, d.getTituloAplicacion(), d.getIdGabinete(), d.getIdCarpetaPadre(), nombreDocumento, ext, "CARGA_MASIVA", nombreDestino, psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, false);
                        log.trace("Archivo insertado exitosamente");
                        cnt++;
                        log.info("Object: {}", "Documento adjuntado exitosamente en folio[" + nFolioDocPoliza + "]");
                    } else {
                        log.info("Object: {}", String.format("El documento %s en el gabinete %d de la aplicacion %s en la carpeta %d no se encontro o ya contiene informacion. ", nombreDocumento, idGabinete, tituloAplicacion, idCarpeta));
                    }
                } else {
                    log.error("Object: {}", "No se encontro expediente con el folio " + nFolioDocPoliza);
                }
            }
        } finally {
            CloseObject.closeObject(psNombreElemento);
            CloseObject.closeObject(psDocumento);
            CloseObject.closeObject(psExpediente);
            CloseObject.closeObject(psSearchVolumenUnit);
            CloseObject.closeObject(psSearchVolumen);
            CloseObject.closeObject(psSearchVolPath);
            CloseObject.closeObject(rsExpediente);
            CloseObject.closeObject(rsElemento);
        }
        return cnt;
    }

    public static String adjuntMasivo(Connection conn, String tituloAplicacion, String archivoZip, String directorioTemporal, Usuario u) throws Exception {
        log.trace("Inicio de proceso de adjuntar masivamente solicitudes de pago.");
        StringBuffer logAdjuntos = new StringBuffer();
        String tablaEncabezado = "t" + tituloAplicacion + "encabezado";
        StringBuilder queryDocumento = new StringBuilder();
        queryDocumento.append("SELECT nFolio");
        queryDocumento.append(tituloAplicacion);
        queryDocumento.append(" FROM ");
        queryDocumento.append(tablaEncabezado);
        queryDocumento.append(" WITH( nolock) WHERE ");
        if ("CAJA".equalsIgnoreCase(tituloAplicacion))
            queryDocumento.append(" nFolioCaja = ?");
        else
            queryDocumento.append(" canocontrarrecibo = ?");
        String queryExpediente = "SELECT id_gabinete FROM IMX" + tituloAplicacion + " WITH( nolock) WHERE FOLIO LIKE '%-%-' + CONVERT(VARCHAR(32), ?)";
        String querySearchVolumenUnit = "SELECT unidad, " + "       tipo_dispositivo, " + "       ruta_base " + "FROM   imx_unidad_volumen WITH (NOLOCK)" + "WHERE  estado_unidad = ?";
        String querySearchVolumen = "SELECT volumen " + "FROM   imx_volumen WITH (NOLOCK)" + "WHERE  unidad_disco = ?  " + "        AND tipo_volumen = ? AND capacidad = ?";
        String querySearchFilePath = "SELECT ruta_directorio FROM imx_volumen WITH (NOLOCK) " + " WHERE volumen = ? AND unidad_disco = ? AND tipo_volumen = ?";
        String nombreDocumento = "Solicitud de Pago Firmada";
        if ("CAJA".equalsIgnoreCase(tituloAplicacion))
            nombreDocumento = "Solicitud Firmada";
        int nFolioDocumento;
        int idGabinete = -1;
        PreparedStatement psNombreElemento = null;
        PreparedStatement psDocumento = null;
        PreparedStatement psExpediente = null;
        PreparedStatement psSearchVolumenUnit = null;
        PreparedStatement psSearchVolumen = null;
        PreparedStatement psSearchVolPath = null;
        ResultSet rsDocumento = null;
        ResultSet rsExpediente = null;
        ResultSet rsElemento = null;
        String token = "";
        directorioTemporal = (directorioTemporal + (directorioTemporal.endsWith(String.valueOf(File.separatorChar)) ? "" : File.separatorChar) + String.valueOf(System.currentTimeMillis()));
        try {
            psDocumento = conn.prepareStatement(queryDocumento.toString());
            psExpediente = conn.prepareStatement(queryExpediente);
            psSearchVolumenUnit = conn.prepareStatement(querySearchVolumenUnit);
            psSearchVolumen = conn.prepareStatement(querySearchVolumen);
            psSearchVolPath = conn.prepareStatement(querySearchFilePath);
            List<File> procesar = ZipManager.extraeArchivosMemoria(archivoZip, directorioTemporal);
            for (int i = 0; i < procesar.size(); i++) {
                try {
                    String cxp = Util.getFileWithoutExtencion(procesar.get(i).getName());
                    log.debug("Object: {}", "Contrarecibo generado [" + cxp + "]");
                    psDocumento.setString(1, cxp);
                    rsDocumento = psDocumento.executeQuery();
                    if (rsDocumento.next()) {
                        nFolioDocumento = rsDocumento.getInt(1);
                        psExpediente.setInt(1, nFolioDocumento);
                        rsExpediente = psExpediente.executeQuery();
                        if (rsExpediente.next()) {
                            idGabinete = rsExpediente.getInt("id_gabinete");
                            if (DocumentoManager.existeDocumento(conn, tituloAplicacion, idGabinete, nombreDocumento)) {
                                if (!DocumentoManager.existeDocumentoCapturado(conn, tituloAplicacion, idGabinete, nombreDocumento)) {
                                    Documento d = DocumentoManager.buscaDocumento(conn, tituloAplicacion, idGabinete, nombreDocumento);
                                    File origen = procesar.get(i);
                                    log.trace("Object: {}", "Insertando el archivo: " + origen.getName());
                                    String tmpFile = origen.getName();
                                    int pos = tmpFile.lastIndexOf('.') + 1;
                                    String ext = pos != -1 ? tmpFile.substring(pos) : "";
                                    CLCAttachmentManager.insertaDocumento(conn, d.getTituloAplicacion(), d.getIdGabinete(), d.getIdCarpetaPadre(), nombreDocumento, ext, u.getLogin(), procesar.get(i).getAbsolutePath(), psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, true);
                                    log.trace("Archivo insertado exitosamente");
                                    log.info("Object: {}", "Documento adjuntado exitosamente en el CxP[" + cxp + "]");
                                    logAdjuntos.append(token + "[" + cxp + "] Documento adjuntado exitosamente  en el expediente [" + idGabinete + "] del tramite [" + tituloAplicacion + "] con folio [" + nFolioDocumento + "]");
                                } else {
                                    logAdjuntos.append(token + "[" + cxp + "] El documento [" + nombreDocumento + "] en el expediente [" + idGabinete + "] del tramite [" + tituloAplicacion + "] con folio " + nFolioDocumento + " NO ESTA VACIO. Se ignora");
                                }
                            } else {
                                logAdjuntos.append(token + "[" + cxp + "] No se encontro el documento [" + nombreDocumento + "] en el expediente [" + idGabinete + "] del tramite [" + tituloAplicacion + "] con folio " + nFolioDocumento + " Notifique a soporte tecnico.");
                            }
                        } else {
                            logAdjuntos.append(token + "[" + cxp + "] No se encontro expediente creado en el tramite [" + tituloAplicacion + "] con folio " + nFolioDocumento + " Notifique a soporte tecnico.");
                        }
                    } else {
                        logAdjuntos.append(token + "[" + cxp + "] No se encontro informacion para en el tramite [" + tituloAplicacion + "] Valide que la CxP sea correcta.");
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    logAdjuntos.append(token + "[" + procesar.get(i) + "] Ocurrio el siguiente error: " + e.toString());
                } finally {
                    if (!procesar.get(i).delete()) {
                        log.warn("No se pudo eliminar al momento. Se marca para eliminar en el proximo reset");
                        procesar.get(i).deleteOnExit();
                    }
                }
                token = "\n";
            }
        } finally {
            CloseObject.closeObject(psNombreElemento);
            CloseObject.closeObject(psDocumento);
            CloseObject.closeObject(psExpediente);
            CloseObject.closeObject(psSearchVolumenUnit);
            CloseObject.closeObject(psSearchVolumen);
            CloseObject.closeObject(psSearchVolPath);
            CloseObject.closeObject(rsDocumento);
            CloseObject.closeObject(rsExpediente);
            CloseObject.closeObject(rsElemento);
            File fTemp = new File(directorioTemporal);
            if (!fTemp.delete())
                fTemp.deleteOnExit();
        }
        return logAdjuntos.toString();
    }

    public static String validaEsAlimentacionBrigadistas(Connection conn, int nFolioDocumento) throws Exception {
        String cNombreDocto = "";
        String sSql = "SELECT	CASE WHEN nEsAlimentacionBrigadistas = 1 THEN 'Alimentacion a Brigadistas' " + "WHEN nEsBoxLunch = 1 THEN 'Juegos Deportivos'" + " ELSE '' END cNomDocto " + " FROM tRELACIONGASTOSEncabezado WITH (NOLOCK) WHERE nFolioRELACIONGASTOS = ?";
        PreparedStatement psDocto = null;
        ResultSet rsDocto = null;
        try {
            psDocto = conn.prepareStatement(sSql);
            psDocto.setInt(1, nFolioDocumento);
            rsDocto = psDocto.executeQuery();
            if (rsDocto.next()) {
                cNombreDocto = rsDocto.getString("cNomDocto");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            CloseObject.closeObject(psDocto);
            CloseObject.closeObject(rsDocto);
        }
        return cNombreDocto;
    }

    public static String crearCarpetaRG(Connection conn, String gavetaAsociada, int idGabinete, String nombreCarpeta, String uLogin, PreparedStatement psSearchVolumenUnit, PreparedStatement psSearchVolumen, PreparedStatement psSearchVolPath, File origen, String nombreDestino, boolean bValor, String cxp, int nFolioDocumento) throws Exception {
        StringBuffer logAdjuntos = new StringBuffer();
        log.trace("Object: {}", "Inicia busqueda de carpeta [" + nombreCarpeta + "]");
        long start = System.currentTimeMillis();
        Carpeta carpeta = CarpetaManager.getCarpetaByName(conn, gavetaAsociada, idGabinete, nombreCarpeta);
        if (carpeta == null) {
            log.trace("Object: {}", "No existe la carpeta [" + nombreCarpeta + "] se creara.");
            Carpeta modelo = new Carpeta();
            modelo.setTituloAplicacion(gavetaAsociada);
            modelo.setIdGabinete(idGabinete);
            modelo.setIdCarpeta(CarpetaManager.getNextIdCarpeta(conn, gavetaAsociada, idGabinete));
            modelo.setNombreCarpeta(nombreCarpeta);
            modelo.setNombreUsuario(uLogin);
            modelo.setBanderaRaiz("N");
            modelo.setDescripcion("Carpeta que contiene los Documentos de la Solicitud");
            modelo.setPassword("-1");
            carpeta = CarpetaManager.insertaCarpeta(conn, modelo);
            OrgCarpeta oc = new OrgCarpeta();
            oc.setIdCarpetaHija(carpeta.getIdCarpeta());
            oc.setIdCarpetaPadre(0);
            oc.setIdGabinete(idGabinete);
            oc.setNombreHija(carpeta.getNombreCarpeta());
            oc.setTituloAplicacion(carpeta.getTituloAplicacion());
            OrgCarpetaManager.insert(conn, oc);
            log.trace("Object: {}", "Carpeta [" + nombreCarpeta + "] creada con exito.");
        }
        if (!DocumentoManager.existeDocumento(conn, gavetaAsociada, idGabinete, nombreCarpeta)) {
            log.trace("Object: {}", "Insertando el archivo: " + origen.getName());
            String tmpFile = origen.getName();
            int pos = tmpFile.lastIndexOf('.') + 1;
            String ext = pos != -1 ? tmpFile.substring(pos) : "";
            CLCAttachmentManager.insertaDocumento(conn, carpeta.getTituloAplicacion(), carpeta.getIdGabinete(), carpeta.getIdCarpeta(), nombreCarpeta, ext, "CARGA_MASIVA", nombreDestino, psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, bValor);
            log.trace("Archivo insertado exitosamente");
            log.info("Documento adjuntado exitosamente.");
            logAdjuntos.append("[" + cxp + "] Documento adjuntado exitosamente  en el expediente [" + idGabinete + "] del tramite [" + gavetaAsociada + "] con folio [" + nFolioDocumento + "]");
        } else {
            logAdjuntos.append("[" + cxp + "] El documento [" + nombreCarpeta + "] en el expediente [" + idGabinete + "] del tramite [" + gavetaAsociada + "] con folio " + nFolioDocumento + " NO ESTA VACIO. Se ignora");
        }
        long stop = System.currentTimeMillis();
        log.trace("Object: {}", "Finaliza busqueda de carpeta en [" + ((stop - start) / 1000) + "] s.");
        return logAdjuntos.toString();
    }

    public static String adjuntaCertificadoTransitoMasivo(Connection conn, String tituloAplicacion, String archivoZip, String directorioTemporal, Usuario u) throws Exception {
        log.trace("Inicio de proceso de adjuntar masivamente Certificado de Transito.");
        StringBuffer logAdjuntos = new StringBuffer();
        String tablaEncabezado = "t" + tituloAplicacion + "encabezado";
        String queryDocumento = "SELECT nFolio" + tituloAplicacion + " FROM " + tablaEncabezado + " WITH( nolock) WHERE canocontrarrecibo = ?";
        String queryExpediente = "SELECT id_gabinete FROM IMX" + tituloAplicacion + " WITH( nolock) WHERE FOLIO LIKE '%-%-' + CONVERT(VARCHAR(32), ?)";
        String querySearchVolumenUnit = "SELECT unidad, " + "       tipo_dispositivo, " + "       ruta_base " + "FROM   imx_unidad_volumen WITH (NOLOCK)" + "WHERE  estado_unidad = ?";
        String querySearchVolumen = "SELECT volumen " + "FROM   imx_volumen WITH (NOLOCK)" + "WHERE  unidad_disco = ?  " + "        AND tipo_volumen = ? AND capacidad = ?";
        String querySearchFilePath = "SELECT ruta_directorio FROM imx_volumen WITH (NOLOCK) " + " WHERE volumen = ? AND unidad_disco = ? AND tipo_volumen = ?";
        String nombreDocumento = "Certificado de Transito";
        int nFolioDocumento;
        int idGabinete = -1;
        PreparedStatement psNombreElemento = null;
        PreparedStatement psDocumento = null;
        PreparedStatement psExpediente = null;
        PreparedStatement psSearchVolumenUnit = null;
        PreparedStatement psSearchVolumen = null;
        PreparedStatement psSearchVolPath = null;
        ResultSet rsDocumento = null;
        ResultSet rsExpediente = null;
        ResultSet rsElemento = null;
        String token = "";
        directorioTemporal = (directorioTemporal + (directorioTemporal.endsWith(String.valueOf(File.separatorChar)) ? "" : File.separatorChar) + String.valueOf(System.currentTimeMillis()));
        try {
            psDocumento = conn.prepareStatement(queryDocumento);
            psExpediente = conn.prepareStatement(queryExpediente);
            psSearchVolumenUnit = conn.prepareStatement(querySearchVolumenUnit);
            psSearchVolumen = conn.prepareStatement(querySearchVolumen);
            psSearchVolPath = conn.prepareStatement(querySearchFilePath);
            List<File> procesar = ZipManager.extraeArchivosMemoria(archivoZip, directorioTemporal);
            for (int i = 0; i < procesar.size(); i++) {
                try {
                    String cxp = Util.getFileWithoutExtencion(procesar.get(i).getName());
                    log.debug("Object: {}", "Contrarecibo generado [" + cxp + "]");
                    psDocumento.setString(1, cxp);
                    rsDocumento = psDocumento.executeQuery();
                    if (rsDocumento.next()) {
                        nFolioDocumento = rsDocumento.getInt(1);
                        psExpediente.setInt(1, nFolioDocumento);
                        rsExpediente = psExpediente.executeQuery();
                        if (rsExpediente.next()) {
                            idGabinete = rsExpediente.getInt("id_gabinete");
                            String cNombreDocto = "";
                            cNombreDocto = validaEsCertificadoTransito(conn, nFolioDocumento);
                            File origenDocto = procesar.get(i);
                            if (!"".equals(cNombreDocto)) {
                                logAdjuntos.append(crearCarpetaRG(conn, tituloAplicacion, idGabinete, nombreDocumento, u.getLogin(), psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, origenDocto, procesar.get(i).getAbsolutePath(), true, cxp, nFolioDocumento));
                            }
                        } else {
                            logAdjuntos.append(token + "[" + cxp + "] No se encontro expediente creado en el tramite [" + tituloAplicacion + "] con folio " + nFolioDocumento + " Notifique a soporte tecnico.");
                        }
                    } else {
                        logAdjuntos.append(token + "[" + cxp + "] No se encontro informacion para en el tramite [" + tituloAplicacion + "] Valide que la CxP sea correcta.");
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    logAdjuntos.append(token + "[" + procesar.get(i) + "] Ocurrio el siguiente error: " + e.toString());
                } finally {
                    if (!procesar.get(i).delete()) {
                        log.warn("No se pudo eliminar al momento. Se marca para eliminar en el proximo reset");
                        procesar.get(i).deleteOnExit();
                    }
                }
                token = "\n";
            }
        } finally {
            CloseObject.closeObject(psNombreElemento);
            CloseObject.closeObject(psDocumento);
            CloseObject.closeObject(psExpediente);
            CloseObject.closeObject(psSearchVolumenUnit);
            CloseObject.closeObject(psSearchVolumen);
            CloseObject.closeObject(psSearchVolPath);
            CloseObject.closeObject(rsDocumento);
            CloseObject.closeObject(rsExpediente);
            CloseObject.closeObject(rsElemento);
            File fTemp = new File(directorioTemporal);
            if (!fTemp.delete())
                fTemp.deleteOnExit();
        }
        return logAdjuntos.toString();
    }

    public static String validaEsCertificadoTransito(Connection conn, int nFolioDocumento) throws Exception {
        String cNombreDocto = "";
        String sSql = "SELECT	CASE WHEN nEsCertificadoTransito = 1 THEN 'Certificado de Transito' " + " ELSE '' END cNomDocto " + " FROM tRELACIONGASTOSEncabezado WITH (NOLOCK) WHERE nFolioRELACIONGASTOS = ?";
        PreparedStatement psDocto = null;
        ResultSet rsDocto = null;
        try {
            psDocto = conn.prepareStatement(sSql);
            psDocto.setInt(1, nFolioDocumento);
            rsDocto = psDocto.executeQuery();
            if (rsDocto.next()) {
                cNombreDocto = rsDocto.getString("cNomDocto");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            CloseObject.closeObject(psDocto);
            CloseObject.closeObject(rsDocto);
        }
        return cNombreDocto;
    }
}
