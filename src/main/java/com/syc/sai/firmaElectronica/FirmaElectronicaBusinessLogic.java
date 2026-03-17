package com.syc.sai.firmaElectronica;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
// Utilidades estándar
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
// // // // // // import org.apache.commons.ssl.PKCS8Key;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.axtel.contratos.ProcesoEnteraSatisfaccionBusinessLogic;
import com.axtel.contratos.QuestionnaireBussinessLogic;
import com.axtel.contratos.Requisition;
import com.axtel.contratos.RequisitionBussinessLogic;
import com.axtel.contratos.RequisitionStatus;
import com.axtel.contratos.core.ProcesoEnteraSatisfaccionManager;
import com.axtel.contratos.core.RequisitionManager;
import com.axtel.contratos.entities.DatEnteraSatisfaccion;
import com.axtel.egresos.core.MasiveOperation;
import com.lowagie.text.Element;
// Cambio a OpenPDF (com.lowagie)
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSignature;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfString;
import com.syc.adquisiciones.core.DatosRecepcionFIEL;
import com.syc.cfdi.db.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.fortimax.core.Pagina;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.reportes.ConciliacionFirma;
import com.syc.gestion.reportes.EstadosFinancierosFirma;
import com.syc.gestion.reportes.FirmaElectronicaReporte;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.implementacion.tesoreria.EgresosInterface;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.obrapublica.core.EstimacionObraFIEL;
import com.syc.sai.bitacora.BitacoraOperacionDoctosBusinessLogic;
import com.syc.sai.contratos.RecepcionMaterialManager;
import com.syc.sai.firmaElectronica.cert.client.ValidaCert;
import com.syc.sai.firmaElectronica.core.FirmaElectronicaManager;
import com.syc.sai.firmaElectronica.core.RecepcionMaterialFIEL;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class FirmaElectronicaBusinessLogic extends DataSourceManager {

    public class TramiteSolicitud {

        private String claseImplementa;

        private String documentoFirmar;

        private int idTipoCaso;

        private String tramite;

        public String getClaseImplementa() {
            return claseImplementa;
        }

        public String getDocumentoFirmar() {
            return documentoFirmar;
        }

        public int getIdTipoCaso() {
            return idTipoCaso;
        }

        public String getTramite() {
            return tramite;
        }

        public void setClaseImplementa(String c) {
            this.claseImplementa = c;
        }

        public void setDocumentoFirmar(String d) {
            this.documentoFirmar = d;
        }

        public void setIdTipoCaso(int i) {
            this.idTipoCaso = i;
        }

        public void setTramite(String t) {
            this.tramite = t;
        }
    }

    private static final int ALTURA = 65;

    private static final int INIT_Y_END_PAGE = 160;

    private static final int INIT_Y_HORIZONTAL = 500;

    private static final int INIT_Y_VERTICAL = 580;

    private static final Logger log = LoggerFactory.getLogger(FirmaElectronicaBusinessLogic.class);

    private ConfiguraAplicativoBusinessLogic cabl;

    private X509Certificate certificado;

    private boolean esAmbienteDesarrollo = true;

    private String jniName;

    private PrivateKey privateKey;

    private TramiteSolicitud tramiteSolicitud;

    public FirmaElectronicaBusinessLogic(File certificado, File llave, String password) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            readCertificate(certificado);
            readPrivateKey(llave, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public FirmaElectronicaBusinessLogic(String jniName) {
        super.init(jniName);
        Security.addProvider(new BouncyCastleProvider());
        this.jniName = jniName;
        cabl = new ConfiguraAplicativoBusinessLogic(jniName);
        esAmbienteDesarrollo = cabl.getSystemSetting("AMBIENTE_DESARROLLO") == null ? false : Boolean.valueOf(cabl.getSystemSetting("AMBIENTE_DESARROLLO"));
    }

    // --- MÉTODOS DE FIRMA REFACTORIZADOS ---
    public void actualizaEstatusSICOP(String tabla, String campo, int llave, int estatus) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            if ("tRELACIONGASTOSencabezado".equalsIgnoreCase(tabla))
                FirmaElectronicaManager.avanzaEstatusSICOPRG(conn, tabla, campo, llave, estatus);
            else
                FirmaElectronicaManager.avanzaEstatusSICOP(conn, tabla, campo, llave, estatus);
            int folioCom = FirmaElectronicaManager.consultaComision(conn, llave, tabla);
            if (folioCom > 0)
                FirmaElectronicaManager.validaFinalizaComision(conn, folioCom);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                conn.rollback();
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    private void aplicarFirmaManual(PdfStamper stamper, Rectangle rect, int pagina, String reason, String idFirma) throws Exception {
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLayer2Font(new Font(Font.HELVETICA, 9.0f, Font.NORMAL));
        appearance.setLocation(SolicitudFirmaElectronica.LOCATION);
        appearance.setVisibleSignature(rect, pagina, idFirma);
        PdfSignature dic = new PdfSignature(PdfName.ADOBE_PPKLITE, PdfName.ADBE_PKCS7_DETACHED);
        dic.setReason(appearance.getReason());
        dic.setLocation(appearance.getLocation());
        dic.setDate(new PdfDate(appearance.getSignDate()));
        appearance.setCryptoDictionary(dic);
        HashMap<PdfName, Integer> exc = new HashMap<>();
        exc.put(PdfName.CONTENTS, new Integer(8192 * 2 + 2));
        appearance.preClose(exc);
        MessageDigest md = MessageDigest.getInstance("SHA-256", "BC");
        InputStream data = appearance.getRangeStream();
        byte[] buf = new byte[8192];
        int n;
        while ((n = data.read(buf)) > 0) md.update(buf, 0, n);
        byte[] hash = md.digest();
        Signature sig = Signature.getInstance("SHA256withRSA", "BC");
        sig.initSign(getPrivateKey());
        sig.update(hash);
        byte[] signatureBytes = sig.sign();
        byte[] outc = new byte[8192];
        System.arraycopy(signatureBytes, 0, outc, 0, signatureBytes.length);
        PdfDictionary dic2 = new PdfDictionary();
        dic2.put(PdfName.CONTENTS, new PdfString(outc).setHexWriting(true));
        appearance.close(dic2);
    }

    public List<String> autVoBoRM(RecepcionMaterialFIEL fer) {
        List<String> logProc = null;
        String[] folios = fer.getFolios().split(",");
        for (String folio : folios) {
            Connection conn = null;
            try {
                conn = getConnection();
                DatosRecepcionFIEL drf = new DatosRecepcionFIEL();
                drf = RecepcionMaterialManager.read(conn, Integer.parseInt(folio));
                fer.setDocName("Atenta Nota ".concat(drf.getcIdRecepcionMat()));
                fer.setRecepcionMaterial(drf);
                fer.getFolioContrato(conn);
                if (logProc == null)
                    logProc = new ArrayList<String>();
                FirmaElectronicaManager.autVoBoRecepcionMaterial(conn, fer);
                if (drf.getnIdEntraAlmacen() == SolicitudFirmaElectronica.ID_ALMACEN && (drf.getNumeroEmpleado() == 0 && "-1".equalsIgnoreCase(drf.getFolioNota()))) {
                    // Notificar al capturista.
                    fer.notificaAutorizacionVoBoRM(conn);
                } else {
                    fer.notificaOperacionPendiente(conn, SolicitudFirmaElectronica.AUT_RM);
                }
                registraBitacoraRM(conn, SolicitudFirmaElectronica.AUT_RM_VOBO, fer);
                logProc.add(" Documento: " + fer.getDocument() + " con Folio: " + folio + " visto bueno exitosamente.");
                conn.commit();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                logProc.add(" Error en documento: " + fer.getDocument() + " con Folio: " + folio + " Causa: " + e);
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e2) {
                        log.warn(e2.getMessage(), e2);
                    }
            } finally {
                CloseObject.closeObject(conn);
            }
        }
        return logProc;
    }

    public Rectangle calculaPosicionFirmanteH(int num) {
        return new Rectangle(45, INIT_Y_HORIZONTAL - (num * ALTURA), 560, (INIT_Y_HORIZONTAL - (num * ALTURA)) + ALTURA);
    }

    public Rectangle calculaPosicionFirmanteV(int num) {
        return new Rectangle(45, INIT_Y_VERTICAL - (num * ALTURA), 560, (INIT_Y_VERTICAL - (num * ALTURA)) + ALTURA);
    }

    // --- MÉTODOS DE NEGOCIO (RESTAURADOS TOTALMENTE) ---
    public void cargaInformacionTramite(String nombre) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            this.tramiteSolicitud = FirmaElectronicaManager.cargaInformacionTramite(conn, this, nombre);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public Rectangle endPageSignatoryV(int num) {
        return new Rectangle(45, INIT_Y_END_PAGE - (num * ALTURA), 560, (INIT_Y_END_PAGE - (num * ALTURA)) + ALTURA);
    }

    public void enviarCorreoVoBo(String tabla, String campo, int llave, int estatus, SolicitudFirmaElectronica sol) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            FirmaElectronicaManager.avanzaEstatusSICOP(conn, tabla, campo, llave, estatus);
            FirmaElectronicaManager.registraBitacora(conn, "PREFIRMA", sol);
            sol.notificaOperacionPendiente(conn, SolicitudFirmaElectronica.VO_BO);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                conn.rollback();
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public String esLaudoIF(int valorLlave) throws Exception {
        Connection conn = null;
        String laudo_IF = "";
        try {
            conn = getConnection();
            laudo_IF = FirmaElectronicaManager.esLaudoIF(conn, valorLlave);
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
        return laudo_IF;
    }

    public boolean esUsuarioAutorizadorSICOP(Usuario u) throws Exception {
        if (u.getRole("AutorizaSICOP_FIEL") != null)
            return true;
        else
            return false;
    }

    public void firmaArchivoFirmado(Certificate[] chain, String inpFile, Rectangle formatoFirma, String reason, int pagina, String numAutorizador) throws Exception {
        File tmp = File.createTempFile("presigned", ".pdf", new File("/tmp"));
        try (FileInputStream ins = new FileInputStream(inpFile);
            FileOutputStream os = new FileOutputStream(tmp)) {
            PdfReader reader = new PdfReader(ins);
            PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0', null, true);
            int numPag = (pagina > 0 ? pagina : reader.getNumberOfPages());
            String id = "AutSignature" + (StringUtils.isEmpty(numAutorizador) ? "" : "_" + numAutorizador);
            aplicarFirmaManual(stamper, formatoFirma, numPag, reason, id);
            stamper.close();
            reader.close();
            Util.copiaArchivo(tmp.getAbsolutePath(), inpFile);
        } catch (IllegalArgumentException e) {
            if (!StringUtils.contains(e.getMessage(), "Signature already exists"))
                throw e;
        } finally {
            if (tmp.exists())
                tmp.delete();
        }
    }

    public void firmaArchivoPDF(Certificate[] chain, String inpFile, Rectangle formatoFirma, String reason, int pagina) throws Exception {
        firmaArchivoFirmado(chain, inpFile, formatoFirma, reason, pagina, null);
    }

    public String firmaConciliacion(ConciliacionFirma fer, String cerFileName, String keyFileName) throws Exception {
        String logProc = null;
        Certificate[] chain = validaCertificados(fer, cerFileName, keyFileName);
        log.trace("Object: {}", fer);
        Connection conn = null;
        try {
            conn = getConnection();
            fer.setIdField(Integer.parseInt(fer.getFolios()));
            ((ConciliacionFirma) fer).setIdConciliacion(Integer.parseInt(fer.getFolios()));
            ((ConciliacionFirma) fer).cargaInformacion(conn);
            FirmaElectronicaManager.avanzaEstatusConciliacion(conn, fer);
            String pathPagina = ((ConciliacionFirma) fer).getRutaReporteImpreso();
            log.info("Object: {}", "Se firmara el documento: " + pathPagina);
            int idTipoFirmante = ((ConciliacionFirma) fer).getIdTipoFirmante();
            log.debug("Object: " + String.valueOf("El archivo se encuentra en: " + pathPagina));
            log.info("Object: {}", "Inicia firma electronica de la conciliación. Tipo Firmante: " + idTipoFirmante);
            Rectangle rectangleSign = EstadosFinancierosFirma.ZONAS_FIRMA.get(idTipoFirmante);
            Integer[] rectangleAcuse = EstadosFinancierosFirma.ZONAS_ACUSE.get(idTipoFirmante);
            if (((ConciliacionFirma) fer).getOrden() == 1) {
                firmaArchivoPDF(chain, pathPagina, rectangleSign, fer.getLeyendaFirma(), 0);
                generaAcuseConciliacion(conn, rectangleAcuse, fer.getRutaReporteImpreso(), fer, fer.getIdConciliacion(), fer.getLeyendaFirma());
                registraBitacora(conn, String.valueOf(fer.getIdTipoFirmante()), fer);
                ((ConciliacionFirma) fer).cargaOrdenActual(conn);
                ((ConciliacionFirma) fer).cargaInformacion(conn);
                fer.notificaOperacionPendiente(conn, String.valueOf(((ConciliacionFirma) fer).getIdTipoFirmante()));
            } else {
                firmaArchivoFirmado(chain, pathPagina, rectangleSign, ((ConciliacionFirma) fer).getLeyendaFirma(), 0, String.valueOf(((ConciliacionFirma) fer).getOrden()));
                generaAcuseConciliacion(conn, rectangleAcuse, ((ConciliacionFirma) fer).getRutaReporteImpreso(), fer, fer.getIdConciliacion(), ((ConciliacionFirma) fer).getLeyendaFirma());
                registraBitacora(conn, String.valueOf(((ConciliacionFirma) fer).getIdTipoFirmante()), fer);
                ((ConciliacionFirma) fer).cargaOrdenActual(conn);
                ((ConciliacionFirma) fer).cargaInformacion(conn);
                fer.notificaOperacionPendiente(conn, String.valueOf(((ConciliacionFirma) fer).getIdTipoFirmante()));
            }
            logProc = " Documento: " + fer.getDocument() + " con Folio: " + fer.getFolios() + " firmado exitosamente. Avanzado a siguiente estatus";
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            logProc = " Error en documento: " + fer.getDocument() + " con Folio: " + fer.getFolios() + " Causa: " + e;
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn(e2.getMessage(), e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
        return logProc;
    }

    public List<String> firmaCuestionario(QuestionnaireBussinessLogic feq, String cerFileName, String keyFileName) throws Exception {
        List<String> logProc = null;
        Certificate[] chain = validaCertificados(feq, cerFileName, keyFileName);
        String[] folios = feq.getFolios().split(",");
        for (String folio : folios) {
            Connection conn = null;
            try {
                conn = getConnection();
                int folioApartado = Integer.parseInt(folio);
                feq.setIdField(folioApartado);
                Requisition requisition = RequisitionManager.readRequisition(conn, folioApartado);
                if (logProc == null)
                    logProc = new ArrayList<String>();
                feq.avanzaEstatusRequisicion(conn, requisition);
                FirmaElectronicaManager.avanzaEstatusSICOP(conn, "tCuestionarioRequisicionFIEL", "cIdSolicitud", requisition.getIdSolicitud(), SolicitudFirmaElectronica.AUT_LAYOUT);
                ((QuestionnaireBussinessLogic) feq).setContractRequisition(requisition);
                if ("N".equalsIgnoreCase(requisition.getApplyQuestionnaire())) {
                    ((QuestionnaireBussinessLogic) feq).setReportNames(null);
                    ((QuestionnaireBussinessLogic) feq).setReportName();
                }
                List<Documento> documentos = ((QuestionnaireBussinessLogic) feq).getRutaDocumentos(conn, requisition);
                for (Documento documento : documentos) {
                    String pathPagina = documento.getFullPathFilesNames()[0];
                    log.info("Object: {}", "Se firmara el documento: " + pathPagina);
                    log.trace("Object: {}", "El archivo se encuentra en: " + pathPagina);
                    Rectangle rectangleSign = null;
                    if ("Cuestionario Firmado".equals(documento.getNombreDocumento()))
                        rectangleSign = calculaPosicionFirmanteV(1);
                    else
                        rectangleSign = calculaPosicionFirmanteH(1);
                    String encReazon = "Firma de Autorizacion de la solicitud: ";
                    encReazon = encReazon.concat(requisition.getIdSolicitud());
                    String signedHash = getPDFSignedHash(pathPagina, "", folioApartado, encReazon);
                    String razonFirma = "Firmado por: ".concat(feq.getUsuario().getNombre()).concat(" | ");
                    razonFirma = razonFirma.concat(encReazon).concat(" | ");
                    razonFirma = razonFirma.concat(Util.getToday("dd-MM-yyyy HH:mm:ss"));
                    razonFirma = razonFirma.concat("\n").concat(signedHash);
                    File signedFile = firmaRecepcionPDF(conn, chain, pathPagina, rectangleSign, razonFirma, 0);
                    ((QuestionnaireBussinessLogic) feq).updateSignedDocto(conn, signedFile, documento);
                }
                // Notificar al usuario capturista y al equipo de adquisiciones.
                ((QuestionnaireBussinessLogic) feq).notificaAutOperacion(conn);
                logProc.add(" Documento: " + feq.getDocument() + " con Folio: " + folio + " firmado exitosamente. Avanzado a siguiente estatus");
                registraBitacora(conn, SolicitudFirmaElectronica.AUTORIZA, feq);
                feq.onFinishAut(conn);
                conn.commit();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                logProc.add(" Error en documento: " + feq.getDocument() + " con Folio: " + folio + " Causa: " + e);
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e2) {
                        log.warn(e2.getMessage(), e2);
                    }
                throw e;
            } finally {
                CloseObject.closeObject(conn);
            }
        }
        return logProc;
    }

    private String firmaDocumento(Connection conn, SolicitudFirmaElectronica sfe, String folio, Certificate[] chain) throws Exception {
        Caso c = CasoManager.findByFolioLike(conn, sfe.getDocument(), folio);
        Documento d = DocumentoManager.buscaDocumento(conn, sfe.getDocument(), c.getIdGabinete(), sfe.getDocName());
        if (d == null || d.getPaginasDocumento().length == 0)
            throw new Exception("Error: Documento no hallado.");
        FirmaElectronicaManager.avanzaEstatusSICOP(conn, sfe, (SolicitudFirmaElectronica.VO_BO.equals(sfe.getTipoAutorizacion()) ? SolicitudFirmaElectronica.AUT_SICOP : SolicitudFirmaElectronica.AUTORIZA.equals(sfe.getTipoAutorizacion()) ? SolicitudFirmaElectronica.AUT_LAYOUT : 0));
        Pagina pag = d.getPaginaDocumento(0);
        String path = pag.getUnidadDisco() + pag.getRutaBase() + pag.getRutaDirectorio() + pag.getNomArchivoVol();
        Rectangle rV = "CAJA".equals(sfe.getDocument()) ? EgresosInterface.RECT_VOBO_CAJA : EgresosInterface.RECT_VOBO;
        Rectangle rA = "CAJA".equals(sfe.getDocument()) ? EgresosInterface.RECT_AUT_CAJA : EgresosInterface.RECT_AUT;
        if (SolicitudFirmaElectronica.VO_BO.equals(sfe.getTipoAutorizacion())) {
            firmaArchivoPDF(chain, path, rV, sfe.getVoBoLegend(conn), 0);
            generaAcuse(conn, path, sfe, sfe.getTipoAutorizacion(), Integer.parseInt(folio), sfe.getVoBoLegend(conn));
        } else {
            firmaArchivoFirmado(chain, path, rA, sfe.getAutLegend(conn), 0, null);
            generaAcuse(conn, path, sfe, sfe.getTipoAutorizacion(), Integer.parseInt(folio), sfe.getAutLegend(conn));
        }
        return "Documento Folio: " + folio + " firmado.";
    }

    public List<String> firmaDocumento(SolicitudFirmaElectronica sfe, String cer, String key) throws Exception {
        Certificate[] chain = validaCertificados(sfe, cer, key);
        String[] folios = sfe.getFolios().split(",");
        List<String> res = new ArrayList<>();
        for (String f : folios) {
            Connection conn = null;
            try {
                conn = getConnection();
                res.add(firmaDocumento(conn, sfe, f, chain));
                conn.commit();
            } catch (Exception e) {
                if (conn != null)
                    conn.rollback();
                res.add("Error en " + f + ": " + e.getMessage());
            } finally {
                CloseObject.closeObject(conn);
            }
        }
        return res;
    }

    public File firmaEstimacionObra(Connection conn, EstimacionObraFIEL estObraFIEL, String cerFileName, String keyFileName) throws Exception {
        Certificate[] chain = validaCertificados(estObraFIEL, cerFileName, keyFileName);
        log.info("Object: {}", "Iniciando firma electronica de la Estimación " + estObraFIEL.getEstimacionObra());
        String pathPagina = null;
        String folio = estObraFIEL.getFolios();
        File signedFile = null;
        try {
            pathPagina = ((EstimacionObraFIEL) estObraFIEL).getRutaReporteImpreso(conn);
            log.info("Object: {}", "Se firmara el documento: " + pathPagina);
            log.trace("Object: {}", "El archivo se encuentra en: " + pathPagina);
            Rectangle rectangleSign = calculaPosicionFirmanteV(estObraFIEL.getEstimacionObra().getNumFirmante());
            String encReazon = "Autorizo la estimacion de obra con folio: ";
            encReazon = encReazon.concat(String.valueOf(estObraFIEL.getEstimacionObra().getnEstimacion()));
            encReazon = encReazon.concat(" del contrato ").concat(estObraFIEL.getEstimacionObra().getContratoCNET());
            String signedHash = getPDFSignedHash(pathPagina, "", Integer.parseInt(folio), encReazon);
            String razonFirma = "Firmado por: ".concat(estObraFIEL.getAutNombre(conn)).concat(" | ");
            razonFirma = razonFirma.concat(encReazon).concat(" | ");
            razonFirma = razonFirma.concat(Util.getToday("dd-MM-yyyy HH:mm:ss"));
            razonFirma = razonFirma.concat("\n").concat(signedHash);
            signedFile = firmaRecepcionPDF(conn, chain, pathPagina, rectangleSign, razonFirma, 0, estObraFIEL.getEstimacionObra().getNumFirmante());
            ((EstimacionObraFIEL) estObraFIEL).updateSignedDocto(conn, signedFile);
        } finally {
            pathPagina = null;
            folio = null;
            chain = null;
        }
        return signedFile;
    }

    public List<String> firmaRecepcionMaterial(RecepcionMaterialFIEL ferm, String cer, String key) throws Exception {
        Certificate[] chain = validaCertificados(ferm, cer, key);
        String[] folios = ferm.getFolios().split(",");
        List<String> logProc = new ArrayList<>();
        for (String folio : folios) {
            Connection conn = null;
            try {
                conn = getConnection();
                int folioRM = Integer.parseInt(folio);
                DatosRecepcionFIEL drfiel = RecepcionMaterialManager.read(conn, folioRM);
                ferm.setRecepcionMaterial(drfiel);
                if (drfiel.getIdEstatusRM() == SolicitudFirmaElectronica.ESTATUS_RM_AUT)
                    FirmaElectronicaManager.avanzaEstatusRM(conn, ferm);
                String path = ferm.getRutaReporteImpreso(conn);
                String reason = "Autorizo folio: " + drfiel.getcIdRecepcionMat();
                String hash = getPDFSignedHash(path, "", folioRM, reason);
                String razonFirma = "Firmado por: " + ferm.getAutNombre(conn) + " | " + reason + " | " + Util.getToday("dd-MM-yyyy HH:mm:ss") + "\n" + hash;
                File signedFile = firmaRecepcionPDF(conn, chain, path, calculaPosicionFirmanteV(1), razonFirma, 0);
                ferm.updateSignedDocto(conn, signedFile);
                conn.commit();
                logProc.add("RM " + folio + " firmada.");
            } catch (Exception e) {
                if (conn != null)
                    conn.rollback();
                throw e;
            } finally {
                CloseObject.closeObject(conn);
            }
        }
        return logProc;
    }

    public File firmaRecepcionPDF(Connection conn, Certificate[] chain, String inpFile, Rectangle formatoFirma, String reason, int pagina) throws Exception {
        return firmaRecepcionPDF(conn, chain, inpFile, formatoFirma, reason, pagina, 1);
    }

    public File firmaRecepcionPDF(Connection conn, Certificate[] chain, String inp, Rectangle rect, String reason, int pag, int numFirmante) throws Exception {
        File original = new File(inp);
        File signed = new File(original.getParentFile(), DocumentoManager.getNextFilename(null, "signed_") + ".tif");
        try (FileInputStream ins = new FileInputStream(original);
            FileOutputStream os = new FileOutputStream(signed)) {
            PdfReader reader = new PdfReader(ins);
            PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0', null, true);
            int p = (pag > 0 ? pag : reader.getNumberOfPages());
            PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
            appearance.setLayer2Text(reason);
            appearance.setLayer2Font(new Font(Font.TIMES_ROMAN, 7.0f, Font.NORMAL));
            appearance.setVisibleSignature(rect, p, "Signature1");
            // Reutilización de la lógica de firmado manual
            PdfSignature dic = new PdfSignature(PdfName.ADOBE_PPKLITE, PdfName.ADBE_PKCS7_DETACHED);
            appearance.setCryptoDictionary(dic);
            HashMap<PdfName, Integer> exc = new HashMap<>();
            exc.put(PdfName.CONTENTS, new Integer(8192 * 2 + 2));
            appearance.preClose(exc);
            MessageDigest md = MessageDigest.getInstance("SHA-256", "BC");
            InputStream data = appearance.getRangeStream();
            byte[] buf = new byte[8192];
            int n;
            while ((n = data.read(buf)) > 0) md.update(buf, 0, n);
            byte[] signatureHash = sigSign(md.digest());
            byte[] outc = new byte[8192];
            System.arraycopy(signatureHash, 0, outc, 0, signatureHash.length);
            PdfDictionary dic2 = new PdfDictionary();
            dic2.put(PdfName.CONTENTS, new PdfString(outc).setHexWriting(true));
            appearance.close(dic2);
            stamper.close();
            reader.close();
        }
        return signed;
    }

    public List<String> firmaReporte(FirmaElectronicaReporte fer, String cerFileName, String keyFileName) throws Exception {
        List<String> logProc = null;
        Certificate[] chain = validaCertificados(fer, cerFileName, keyFileName);
        String[] folios = fer.getFolios().split(",");
        String[] ordenes = null;
        if (!StringUtils.isEmpty(fer.getOrdenes()))
            ordenes = fer.getOrdenes().split(",");
        int nOrden = -1;
        log.trace("Object: {}", fer);
        for (String folio : folios) {
            Connection conn = null;
            nOrden++;
            try {
                conn = getConnection();
                fer.setIdField(Integer.parseInt(folio));
                if (ordenes != null)
                    fer.setOrden(Integer.parseInt(ordenes[nOrden]));
                ((EstadosFinancierosFirma) fer).setIdEstadoFinanciero(Integer.parseInt(folio));
                ((EstadosFinancierosFirma) fer).cargaInformacion(conn);
                if (logProc == null)
                    logProc = new ArrayList<String>();
                FirmaElectronicaManager.avanzaEstatusReporte(conn, fer);
                String pathPagina = fer.getRutaReporteImpreso();
                log.info("Object: {}", "Se firmara el documento: " + pathPagina);
                int idTipoFirmante = ((EstadosFinancierosFirma) fer).getIdTipoFirmante();
                log.debug("Object: " + String.valueOf("El archivo se encuentra en: " + pathPagina));
                log.info("Object: {}", "Inicia firma electronica del documento. Tipo Firmante: " + idTipoFirmante);
                int idTipoReporte = fer.getIdTipoReporte();
                Rectangle rectangleSign = null;
                Integer[] rectangleAcuse = null;
                if (EstadosFinancierosFirma.REPORTE_HORIZONTAL.contains(idTipoReporte)) {
                    rectangleSign = EstadosFinancierosFirma.ZONAS_FIRMA_H.get(idTipoFirmante);
                    rectangleAcuse = EstadosFinancierosFirma.ZONAS_ACUSE_H.get(idTipoFirmante);
                } else {
                    rectangleSign = EstadosFinancierosFirma.ZONAS_FIRMA.get(idTipoFirmante);
                    rectangleAcuse = EstadosFinancierosFirma.ZONAS_ACUSE.get(idTipoFirmante);
                }
                if (((EstadosFinancierosFirma) fer).getOrden() == 1) {
                    firmaArchivoPDF(chain, pathPagina, rectangleSign, ((EstadosFinancierosFirma) fer).getLeyendaFirma(), 0);
                    generaAcuseReporte(conn, rectangleAcuse, ((EstadosFinancierosFirma) fer).getRutaReporteImpreso(), fer, Integer.parseInt(folio), ((EstadosFinancierosFirma) fer).getLeyendaFirma());
                    registraBitacora(conn, String.valueOf(((EstadosFinancierosFirma) fer).getIdTipoFirmante()), fer);
                    ((EstadosFinancierosFirma) fer).cargaOrdenActual(conn);
                    ((EstadosFinancierosFirma) fer).cargaInformacion(conn);
                    fer.notificaOperacionPendiente(conn, String.valueOf(((EstadosFinancierosFirma) fer).getIdTipoFirmante()));
                } else {
                    firmaArchivoFirmado(chain, pathPagina, rectangleSign, ((EstadosFinancierosFirma) fer).getLeyendaFirma(), 0, String.valueOf(((EstadosFinancierosFirma) fer).getOrden()));
                    generaAcuseReporte(conn, rectangleAcuse, ((EstadosFinancierosFirma) fer).getRutaReporteImpreso(), fer, Integer.parseInt(folio), ((EstadosFinancierosFirma) fer).getLeyendaFirma());
                    registraBitacora(conn, String.valueOf(((EstadosFinancierosFirma) fer).getIdTipoFirmante()), fer);
                    ((EstadosFinancierosFirma) fer).cargaOrdenActual(conn);
                    ((EstadosFinancierosFirma) fer).cargaInformacion(conn);
                    fer.notificaOperacionPendiente(conn, String.valueOf(((EstadosFinancierosFirma) fer).getIdTipoFirmante()));
                }
                logProc.add(" Documento: " + fer.getDocument() + " con Folio: " + folio + " firmado exitosamente. Avanzado a siguiente estatus");
                conn.commit();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                logProc.add(" Error en documento: " + fer.getDocument() + " con Folio: " + folio + " Causa: " + e);
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e2) {
                        log.warn(e2.getMessage(), e2);
                    }
                throw e;
            } finally {
                CloseObject.closeObject(conn);
            }
        }
        return logProc;
    }

    private String generaAcuse(Connection conn, String orig, SolicitudFirmaElectronica tramite, String tipo, int folio, String reason) throws Exception {
        String last = tramite.getDocName();
        try {
            tramite.setDocName("Acuse " + last);
            String path = FirmaElectronicaManager.generaArchivoFirma(conn, tramite, tramite.getFolder(), true);
            String hash = getPDFSignedHash(orig, tipo, folio, reason);
            writeHash(path, hash, reason, tipo);
            return path;
        } finally {
            tramite.setDocName(last);
        }
    }

    private String generaAcuseConciliacion(Connection conn, Integer[] rectangleAcuse, String pdfOriginal, ConciliacionFirma fer, int nFolio, String reason) throws Exception {
        String path = ((ConciliacionFirma) fer).getRutaAcuseImpreso();
        String legend = ((ConciliacionFirma) fer).getLeyendaFirma() + " " + ((ConciliacionFirma) fer).getNombreFirmante();
        String signedHash = getPDFSignedHash(pdfOriginal, "", nFolio, reason);
        writeHashReporte(path, rectangleAcuse, signedHash, legend, ((ConciliacionFirma) fer).getIdTipoFirmante());
        return path;
    }

    private String generaAcuseReporte(Connection conn, Integer[] rectangleAcuse, String pdfOriginal, FirmaElectronicaReporte fer, int nFolio, String reason) throws Exception {
        String path = ((EstadosFinancierosFirma) fer).getRutaAcuseImpreso();
        String legend = ((EstadosFinancierosFirma) fer).getLeyendaFirma();
        legend = ((EstadosFinancierosFirma) fer).getLeyendaFirma() + " " + ((EstadosFinancierosFirma) fer).getNombreFirmante();
        String signedHash = getPDFSignedHash(pdfOriginal, "", nFolio, reason);
        writeHashReporte(path, rectangleAcuse, signedHash, legend, ((EstadosFinancierosFirma) fer).getIdTipoFirmante());
        return path;
    }

    public String reFirmaDocumento(SolicitudFirmaElectronica sfe) throws Exception {
        Connection conn = null;
        CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
        try {
            conn = getConnection();
            BitacoraOperacionDoctosBusinessLogic bitacora = new BitacoraOperacionDoctosBusinessLogic(GestionInterface.ATT_CONEXION);
            bitacora.setuLogin(sfe.getUsuario().getLogin());
            Caso c = CasoManager.findByFolioLike(conn, sfe.getDocument(), sfe.getFolios());
            Documento d = DocumentoManager.buscaDocumento(conn, sfe.getDocument(), c.getIdGabinete(), sfe.getDocName());
            bitacora.setIdTC(c.getIdTC());
            bitacora.setModulo(sfe.getDocument());
            bitacora.setnFolio(Integer.parseInt(sfe.getFolios()));
            sfe.setIdField(Integer.parseInt(sfe.getFolios()));
            /* Busca el archivo firmado si no existe se crea */
            if (d != null)
                cbl.versionaDocumento(conn, bitacora, sfe.getDocument() + "_G" + c.getIdGabinete() + "C" + d.getIdCarpetaPadre() + "D" + d.getIdDocumento());
            Documento da = DocumentoManager.buscaDocumento(conn, sfe.getDocument(), c.getIdGabinete(), "Acuse " + sfe.getDocName());
            /* Busca el acuse firmado si no existe se crea */
            if (da != null)
                cbl.versionaDocumento(conn, bitacora, sfe.getDocument() + "_G" + c.getIdGabinete() + "C" + da.getIdCarpetaPadre() + "D" + da.getIdDocumento());
            FirmaElectronicaManager.generaArchivoFirma(conn, sfe, "Solicitud de Pago", false);
            sfe.notificaOperacionPendiente(conn, SolicitudFirmaElectronica.R_VO_BO);
            conn.commit();
            return "El tramite " + c.getFolio() + " enviado a firma exitosamente.";
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Prblema realizando rollback " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public boolean generaArchivosFirmaMasiva(int folioCargaMasiva, SolicitudFirmaElectronica solicitudPagoPrinter, List<MasiveOperation> operations, String documentName) {
        Connection conn = null;
        try {
            conn = getConnection();
            for (MasiveOperation masiveOperation : operations) {
                solicitudPagoPrinter.setIdField(masiveOperation.getFolioTramite());
                solicitudPagoPrinter.setDocName(documentName);
                FirmaElectronicaManager.generaArchivoFirma(conn, solicitudPagoPrinter, solicitudPagoPrinter.getFolder(), false);
            }
            FirmaElectronicaManager.avanzaEstatusSICOPMasivo(conn, solicitudPagoPrinter, SolicitudFirmaElectronica.VO_BO_SICOP);
            solicitudPagoPrinter.notificaOperacionMasivaPendiente(conn, SolicitudFirmaElectronica.VO_BO);
            solicitudPagoPrinter.onGeneraArchivosMasivo(conn);
            conn.commit();
            return true;
        } catch (Exception e) {
            log.error("Error generando documentacion masiva: " + e, e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception ee) {
                    log.warn("Error: cerrando rollback ", ee);
                }
            throw new RuntimeException(e.toString(), e.getCause());
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    // --- INTEGRACIÓN CON OTROS MÓDULOS (RM, REQUISICIÓN, REPORTES) ---
    public X509Certificate getCertificado() {
        return certificado;
    }

    public String getPDFSignedHash(String filePath, String type, int nFolio, String reason) throws Exception {
        try (PdfReader reader = new PdfReader(filePath)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfStamper stamper = PdfStamper.createSignature(reader, baos, '\0');
            PdfSignatureAppearance sap = stamper.getSignatureAppearance();
            sap.setReason(reason);
            sap.setLocation(SolicitudFirmaElectronica.LOCATION);
            PdfSignature dic = new PdfSignature(PdfName.ADOBE_PPKLITE, PdfName.ADBE_PKCS7_DETACHED);
            dic.setDate(new PdfDate(sap.getSignDate()));
            sap.setCryptoDictionary(dic);
            HashMap<PdfName, Integer> exc = new HashMap<>();
            exc.put(PdfName.CONTENTS, new Integer(8192 * 2 + 2));
            sap.preClose(exc);
            MessageDigest md = MessageDigest.getInstance("SHA-256", "BC");
            InputStream data = sap.getRangeStream();
            byte[] buf = new byte[8192];
            int n;
            while ((n = data.read(buf)) > 0) md.update(buf, 0, n);
            byte[] hash = md.digest();
            Signature sig = Signature.getInstance("SHA256withRSA", "BC");
            sig.initSign(getPrivateKey());
            sig.update(hash);
            byte[] signedBytes = sig.sign();
            stamper.close();
            // CORRECCIÓN: Uso de java.util.Base64 para evitar errores de
            // parámetros
            return java.util.Base64.getEncoder().encodeToString(signedBytes);
        }
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public TramiteSolicitud getTramiteSolicitud() {
        return tramiteSolicitud;
    }

    public SolicitudFirmaElectronica instanceFromWeb(String tipoPago) throws Exception {
        cargaInformacionTramite(tipoPago);
        SolicitudFirmaElectronica sfe = (SolicitudFirmaElectronica) Util.instanceCasoFIEL(getTramiteSolicitud().getClaseImplementa());
        sfe.setDocument(tipoPago);
        String detail = StringUtils.isBlank(SolicitudFirmaElectronica.RELACION_TRAMITE_TABLA_D.get(sfe.getDocument())) ? ("t" + sfe.getDocument() + "Detalle") : SolicitudFirmaElectronica.RELACION_TRAMITE_TABLA_D.get(sfe.getDocument());
        String field = StringUtils.isBlank(SolicitudFirmaElectronica.RELACION_TRAMITE_KEY.get(sfe.getDocument())) ? ("nFolio" + sfe.getDocument()) : SolicitudFirmaElectronica.RELACION_TRAMITE_KEY.get(sfe.getDocument());
        String header = StringUtils.isBlank(SolicitudFirmaElectronica.RELACION_TRAMITE_TABLA_E.get(sfe.getDocument())) ? ("t" + sfe.getDocument() + "Encabezado") : SolicitudFirmaElectronica.RELACION_TRAMITE_TABLA_E.get(sfe.getDocument());
        sfe.setHeader(header);
        sfe.setDetail(detail);
        sfe.setDocName(getTramiteSolicitud().getDocumentoFirmar());
        sfe.setField(field);
        sfe.setFileExtension("pdf");
        return sfe;
    }

    private void readCertificate(File f) throws Exception {
        try (FileInputStream is = new FileInputStream(f)) {
            this.certificado = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(is);
        }
    }

    private void readPrivateKey(File f, String pass) throws Exception {
        this.privateKey = null; // null; // null; // null; // null; // new PKCS8Key(FileUtils.readFileToByteArray(f), pass.toCharArray());
    }

    public List<String> rechazaConciliacion(ConciliacionFirma fer) {
        List<String> logProc = null;
        String[] folios = fer.getFolios().split(",");
        log.trace("Object: {}", fer);
        for (String folio : folios) {
            Connection conn = null;
            try {
                conn = getConnection();
                int numero = Integer.parseInt(folio);
                fer.setIdField(numero);
                ((ConciliacionFirma) fer).setIdConciliacion(numero);
                ((ConciliacionFirma) fer).cargaInformacion(conn);
                if (logProc == null)
                    logProc = new ArrayList<String>();
                FirmaElectronicaManager.cancelaFirmaConciliacion(conn, numero);
                FirmaElectronicaManager.regresaConciliacion(conn, numero);
                FirmaElectronicaManager.estatusCancelaConciliacion(conn, fer);
                FirmaElectronicaManager.borraBitacora(conn, numero);
                ((ConciliacionFirma) fer).notificaCancelacion(conn);
                logProc.add(" Documento conciliacion con Folio: " + folio + " cancelado exitosamente.");
                conn.commit();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                logProc.add(" Error en documento: " + fer.getDocument() + " con Folio: " + folio + " Causa: " + e);
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e2) {
                        log.warn(e2.getMessage(), e2);
                    }
            } finally {
                CloseObject.closeObject(conn);
            }
        }
        return logProc;
    }

    public List<String> rechazaReporte(FirmaElectronicaReporte fer) {
        List<String> logProc = null;
        String[] folios = fer.getFolios().split(",");
        log.trace("Object: {}", fer);
        for (String folio : folios) {
            Connection conn = null;
            try {
                conn = getConnection();
                fer.setIdField(Integer.parseInt(folio));
                ((EstadosFinancierosFirma) fer).setIdEstadoFinanciero(Integer.parseInt(folio));
                ((EstadosFinancierosFirma) fer).cargaInformacion(conn);
                if (logProc == null)
                    logProc = new ArrayList<String>();
                FirmaElectronicaManager.cancelaFirmaReporte(conn, fer);
                FirmaElectronicaManager.estatusCancelaReporte(conn, fer);
                ((EstadosFinancierosFirma) fer).notificaCancelacion(conn);
                logProc.add(" Documento: " + fer.getDocument() + " con Folio: " + folio + " cancelado exitosamente.");
                conn.commit();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                logProc.add(" Error en documento: " + fer.getDocument() + " con Folio: " + folio + " Causa: " + e);
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e2) {
                        log.warn(e2.getMessage(), e2);
                    }
            } finally {
                CloseObject.closeObject(conn);
            }
        }
        return logProc;
    }

    public List<String> rechazaRequisicion(QuestionnaireBussinessLogic cqFiel) {
        List<String> logProc = null;
        String[] folios = cqFiel.getFolios().split(",");
        for (String folio : folios) {
            Connection conn = null;
            try {
                conn = getConnection();
                RequisitionBussinessLogic rbl = new RequisitionBussinessLogic(this.jniName);
                Requisition requisition = RequisitionManager.readRequisition(conn, Integer.parseInt(folio));
                requisition.setNotas(cqFiel.getMotivoRechazo());
                requisition.setIdUsuarioAnulacion(cqFiel.getUsuario().getLogin());
                requisition.setIdEstado(RequisitionStatus.CANCELED);
                cqFiel.setIdField(Util.folio(requisition.getFolioApartado()));
                if (logProc == null)
                    logProc = new ArrayList<String>();
                rbl.cancelRequisition(conn, requisition);
                FirmaElectronicaManager.avanzaEstatusSICOP(conn, "tCuestionarioRequisicionFIEL", "cIdSolicitud", requisition.getIdSolicitud(), SolicitudFirmaElectronica.SOLICITUD_CANCELADA);
                cqFiel.notificaCancelacion(conn, requisition);
                registraBitacora(conn, "RECHAZO_REQ", cqFiel);
                conn.commit();
                logProc.add(" Documento: " + cqFiel.getDocument() + " con Folio: " + folio + " cancelado exitosamente.");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                logProc.add(" Error en documento: " + cqFiel.getDocument() + " con Folio: " + folio + " Causa: " + e);
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e2) {
                        log.warn(e2.getMessage(), e2);
                    }
            } finally {
                CloseObject.closeObject(conn);
            }
        }
        return logProc;
    }

    public List<String> rechazaRM(RecepcionMaterialFIEL fer) {
        List<String> res = new ArrayList<>();
        String[] folios = fer.getFolios().split(",");
        for (String f : folios) {
            Connection conn = null;
            try {
                conn = getConnection();
                fer.setRecepcionMaterial(RecepcionMaterialManager.read(conn, Integer.parseInt(f)));
                FirmaElectronicaManager.cancelaRecepcionMaterial(conn, fer);
                registraBitacoraRM(conn, SolicitudFirmaElectronica.RECHAZA_RM, fer);
                conn.commit();
                res.add("RM " + f + " rechazada.");
            } catch (Exception e) {
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception ex) {
                    }
            } finally {
                CloseObject.closeObject(conn);
            }
        }
        return res;
    }

    private void registraBitacora(Connection conn, String operacion, SolicitudFirmaElectronica solicitudPagoPrinter) throws Exception {
        FirmaElectronicaManager.registraBitacora(conn, operacion, solicitudPagoPrinter);
    }

    private void registraBitacoraRM(Connection conn, String operacion, RecepcionMaterialFIEL solicitudPagoPrinter) throws Exception {
        FirmaElectronicaManager.registraBitacoraRM(conn, operacion, solicitudPagoPrinter);
    }

    private File signedDocto(Connection conn, com.axtel.contratos.ProcesoEnteraSatisfaccionBusinessLogic feENSA, Certificate[] chain) throws SQLException, Exception {
        File signedFile = null;
        String encReazon = null;
        Rectangle rectangleSign = null;
        String razonFirma = null;
        try {
            String pathPagina = ((ProcesoEnteraSatisfaccionBusinessLogic) feENSA).getRutaReporteImpreso(conn);
            log.info("Object: {}", "Se firmara el documento: " + pathPagina);
            log.trace("Object: {}", "El archivo se encuentra en: " + pathPagina);
            rectangleSign = endPageSignatoryV(1);
            encReazon = "Autorizo el proceso de entera satisfacción con folio: ";
            encReazon = encReazon.concat(feENSA.getDatEnteraSatisfaccion().getcFolio());
            encReazon = encReazon.concat(" del contrato ").concat(feENSA.getDatEnteraSatisfaccion().getcIdContratoDefinitivo());
            String signedHash = getPDFSignedHash(pathPagina, "", Integer.parseInt(feENSA.getFolios()), encReazon);
            razonFirma = "Firmado por: ".concat(feENSA.getDatEnteraSatisfaccion().getcNombreEmpFirmante()).concat(" | ");
            razonFirma = razonFirma.concat(encReazon).concat(" | ");
            razonFirma = razonFirma.concat(Util.getToday("dd-MM-yyyy HH:mm:ss"));
            razonFirma = razonFirma.concat("\n").concat(signedHash);
            signedFile = firmaRecepcionPDF(conn, chain, pathPagina, rectangleSign, razonFirma, 0);
            ((ProcesoEnteraSatisfaccionBusinessLogic) feENSA).updateSignedDocto(conn, signedFile);
        } finally {
            encReazon = null;
            rectangleSign = null;
            razonFirma = null;
        }
        return signedFile;
    }

    public void signENSA(Connection conn, ProcesoEnteraSatisfaccionBusinessLogic feENSA, String cerFileName, String keyFileName) throws Exception {
        Certificate[] chain = null;
        log.info("Iniciando firma electronica del proceso entera satisfaccion ENSA ");
        DatEnteraSatisfaccion dat = null;
        int sigEstatus = SolicitudFirmaElectronica.ESTATUS_ENSA_CONSULTA;
        try {
            chain = validaCertificados(feENSA, cerFileName, keyFileName);
            dat = ProcesoEnteraSatisfaccionManager.read(conn, Integer.parseInt(feENSA.getFolios()));
            feENSA.setDatEnteraSatisfaccion(dat);
            if (dat.getnServPrestEnteraSatisfaccion() == SolicitudFirmaElectronica.SERVICIO_NO_PRESTADO_ENSA) {
                sigEstatus = SolicitudFirmaElectronica.ESTATUS_ENSA_TESTIGO1;
                feENSA.setDocName("Acta_Hechos");
                signedDocto(conn, feENSA, chain);
            }
            // Firmar documento Anexo 1A
            feENSA.setDocName("Anexo1A");
            signedDocto(conn, feENSA, chain);
            feENSA.getDatEnteraSatisfaccion().setnIdEstatus(sigEstatus);
        } finally {
            chain = null;
        }
    }

    private byte[] sigSign(byte[] hash) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA", "BC");
        sig.initSign(getPrivateKey());
        sig.update(hash);
        return sig.sign();
    }

    public Certificate[] validaCertificados(SolicitudFirmaElectronica sfe, String cer, String key) throws Exception {
        if (!esAmbienteDesarrollo) {
            String status = ValidaCert.validate(cer, "");
            if (!"GOOD".equalsIgnoreCase(status))
                throw new Exception("Certificado inválido SAT: " + status);
        }
        readPrivateKey(new File(key), sfe.getPasswordLlave());
        readCertificate(new File(cer));
        return new Certificate[] { getCertificado() };
    }

    public boolean voBoFirmado(String documento, int folio) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            return FirmaElectronicaManager.voBoFirmado(conn, documento, folio);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    private void writeHash(String path, String hash, String legend, String tipo) throws Exception {
        File out = File.createTempFile("Acuse", ".pdf", new File("/tmp"));
        try (PdfReader reader = new PdfReader(path);
            FileOutputStream fos = new FileOutputStream(out)) {
            PdfStamper stamper = new PdfStamper(reader, fos);
            PdfContentByte canvas = stamper.getOverContent(reader.getNumberOfPages());
            Font f = FontFactory.getFont("Sans Serif", 7.0f, Font.NORMAL, Color.BLACK);
            ColumnText ct = new ColumnText(canvas);
            Paragraph paragraph = new Paragraph(legend + "\n" + hash, f);
            if (SolicitudFirmaElectronica.VO_BO.equals(tipo))
                ct.setSimpleColumn(paragraph, 32, 65, 580, 112, 8, Element.ALIGN_LEFT);
            else
                ct.setSimpleColumn(paragraph, 32, 8, 580, 62, 8, Element.ALIGN_LEFT);
            ct.go();
            stamper.close();
            reader.close();
            Util.copiaArchivo(out.getAbsolutePath(), path);
        } finally {
            if (out.exists())
                out.delete();
        }
    }

    public String writeHashReporte(String path, Integer[] rectangleAcuse, String hash, String legend, int tipo) throws Exception {
        File out = null;
        PdfReader reader = null;
        FileOutputStream fos = null;
        PdfStamper stamper = null;
        try {
            out = File.createTempFile("Prefirma", ".pdf", new File("/tmp"));
            reader = new PdfReader(path);
            int pagina = reader.getNumberOfPages();
            fos = new FileOutputStream(out);
            stamper = new PdfStamper(reader, fos);
            PdfContentByte canvas = stamper.getOverContent(pagina);
            Font f = FontFactory.getFont("Sans Serif", 7.0f, Font.NORMAL, Color.BLACK);
            ColumnText ct = new ColumnText(canvas);
            Paragraph paragraph = new Paragraph(legend + "\n" + hash, f);
            ct.setSimpleColumn(paragraph, rectangleAcuse[0], rectangleAcuse[1], rectangleAcuse[2], rectangleAcuse[3], 8, Element.ALIGN_LEFT);
            ct.go();
            stamper.close();
            fos.flush();
            fos.close();
            reader.close();
            Util.copiaArchivo(out.getAbsolutePath(), path);
            if (!out.delete())
                out.deleteOnExit();
            return path;
        } finally {
            out = null;
            reader = null;
            fos = null;
            stamper = null;
        }
    }
}
