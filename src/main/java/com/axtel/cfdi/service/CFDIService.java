package com.axtel.cfdi.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import com.axtel.cfdi.CFDI;
import com.axtel.cfdi.CFDIDetalle;
import com.axtel.cfdi.CFDIEncabezado;
import com.axtel.cfdi.core.CFDIDetalleManager;
import com.axtel.cfdi.core.CFDIEncabezadoManager;
import com.axtel.cfdi.exceptions.FileManagmentException;
import com.axtel.cfdi.stamp.core.DigitalSignature;
import com.axtel.cfdi.stamp.core.PacInfo;
import com.axtel.cfdi.stamp.core.UtilSecurity;
import com.axtel.cfdi.stamp.core.VirtualFile;
import com.axtel.cfdi.stamp.core.VolumenRepository;
import com.axtel.cfdi.stamp.core.VolumenRepositoryInterface;
import com.axtel.cfdi.stamp.repository.DriveRepository;
import com.axtel.cfdi.stamp.repository.DriveRepositoryInterface;
import com.axtel.cfdi.stamp.repository.StampInvoiceRepository;
import com.axtel.cfdi.stamp.repository.VirtualFileRepository;
import com.axtel.cfdi.stamp.repository.VirtualFileRepositoryInterface;
import com.axtel.cfdi.stamp.service.VolumenService;
import com.axtel.cfdi.stamp.service.VolumenServiceInterface;
import com.syc.cfdi.util.CFDIUtils;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import mx.com.sw.services.stamp.Stamp;
import mx.com.sw.services.stamp.responses.StampResponseV2;
import mx.grupocorasa.sat.cfd._40.Comprobante;
import mx.grupocorasa.sat.cfdi.v4.CFDv40;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class CFDIService extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(CFDIService.class);

    private DriveRepositoryInterface driveRepository;

    private VirtualFileRepositoryInterface virtualFileRepository;

    private VolumenRepositoryInterface volumenRepository;

    private VolumenServiceInterface volumenService;

    private InvoicePDFService pdfService;

    public CFDIService(String jniName, File reporthPath) {
        super.init(jniName);
        virtualFileRepository = new VirtualFileRepository();
        volumenRepository = new VolumenRepository();
        driveRepository = new DriveRepository();
        volumenService = new VolumenService();
        pdfService = new InvoicePDFService(reporthPath);
        ((VolumenService) volumenService).setDriveRepository(driveRepository);
        ((VolumenService) volumenService).setVolumenRepository(volumenRepository);
    }

    public CFDI autoriza(int idInvoice) {
        Connection conn = null;
        try {
            conn = getConnection();
            CFDI invoice = getCFDI(idInvoice);
            if (StringUtils.trimToNull(invoice.getEncabezado().getFolio()) != null)
                return invoice;
            int nextVal = CFSequenceManager.getInstance().nextVal(conn, invoice.getEncabezado().getSerie().getSerie());
            invoice.getEncabezado().setEstatusId(CFDI.AUTORIZADO);
            invoice.getEncabezado().setFolio(String.valueOf(nextVal));
            CFDIEncabezadoManager.actualizarCFDIEncabezado(conn, invoice.getEncabezado());
            conn.commit();
            return getCFDI(idInvoice);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Util.rollback(conn);
            throw new RuntimeException("Error autorizando cfdi con folio: " + idInvoice + " Causa: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void deleteDetailRow(int idDetalle) {
        Connection conn = null;
        try {
            conn = getConnection();
            CFDIDetalleManager.eliminarCFDIDetalle(conn, idDetalle);
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Util.rollback(conn);
            throw new RuntimeException("Error eliminando renglon: " + idDetalle + " Causa: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public StampResponseV2 firmaCFDI(Connection conn, CFDv40 cfdi) throws Exception {
        Stamp sdk = null;
        StampResponseV2 response = null;
        OutputStream byteOS = new ByteArrayOutputStream(2048);
        cfdi.guardar(byteOS, true);
        String xml = ((ByteArrayOutputStream) byteOS).toString("UTF-8");
        byteOS.flush();
        log.info("Object: {}", "XML GENERADO:\n===================================\n" + xml + "\n===================================");
        PacInfo pacInfo = new PacInfo(conn);
        String usuario = UtilSecurity.decrypt(pacInfo.getUser());
        String password = UtilSecurity.decrypt(pacInfo.getPassword());
        log.info("Object: {}", pacInfo.getUrl());
        sdk = new Stamp(pacInfo.getUrl(), usuario, password, null, 0);
        response = (StampResponseV2) sdk.timbrarV2(xml, false);
        log.debug("Object: {}", response.getStatus());
        return response;
    }

    public CFDI generateInvoiceFiles(CFDI invoice) {
        Connection filesConnection = null;
        VirtualFile filePDF = null;
        VirtualFile fileXML = null;
        try {
            filesConnection = getConnection();
            filePDF = virtualFileRepository.select(filesConnection, invoice.getEncabezado().getCfdiId(), "PDF");
            fileXML = virtualFileRepository.select(filesConnection, invoice.getEncabezado().getCfdiId(), "XML");
            if (filePDF != null) {
                filePDF = generatePDF(filesConnection, invoice);
                filePDF.setCfdiId(invoice.getEncabezado().getCfdiId());
                filePDF = virtualFileRepository.insert(filesConnection, filePDF);
            }
            if (fileXML != null) {
                fileXML = generateXML(filesConnection, invoice.getXmlInvoice());
                fileXML.setFileName(invoice.getEncabezado().getUuid() + ".xml");
                fileXML.setFileType("XML");
                fileXML.setCfdiId(invoice.getEncabezado().getCfdiId());
                fileXML = virtualFileRepository.insert(filesConnection, fileXML);
            }
            filesConnection.commit();
            invoice.setFilePDF(filePDF);
            invoice.setFileXML(fileXML);
            return invoice;
        } catch (Exception e) {
            Util.rollback(filesConnection);
            log.error("Error generando archivos: " + e.toString(), e);
            throw new RuntimeException(e);
        } finally {
            CloseObject.closeObject(filesConnection);
        }
    }

    public VirtualFile generateXML(Connection conn, String cfdiXml) throws FileManagmentException {
        try {
            VirtualFile volumenFile = volumenService.generateFileLocation(conn, "xml");
            log.debug("Object: {}", "Escribiendo : =======================================================\n\n" + cfdiXml + "\n\n===================================================================");
            File f = volumenFile.getFilePath().toFile();
            boolean created = f.createNewFile();
            log.info("Object: {}", "Archivo " + (created ? "creado" : "no se pudo crear") + " en " + f.getAbsolutePath());
            FileUtils.writeStringToFile(f, cfdiXml, StandardCharsets.UTF_8);
            return volumenFile;
        } catch (Exception e) {
            throw new FileManagmentException(e.toString(), e.getCause());
        }
    }

    private VirtualFile generatePDF(Connection conn, CFDI cfdi) throws Exception {
        VirtualFile volumenFile = volumenService.generateFileLocation(conn, "pdf");
        volumenFile.setFileName(cfdi.getEncabezado().getUuid() + ".pdf");
        volumenFile.setFileType("PDF");
        pdfService.generateInvoicePDF(cfdi, volumenFile.getFilePath().toFile().getAbsolutePath());
        return volumenFile;
    }

    public CFDI getCFDI(Connection conn, int cfdiId) {
        try {
            log.info("Object: {}", "Fetching CFDI with ID: " + cfdiId);
            CFDI cfdi = new CFDI();
            cfdi.setEncabezado(CFDIEncabezadoManager.obtenerCFDIEncabezado(conn, cfdiId));
            cfdi.setDetalles(CFDIDetalleManager.obtenerTodosCFDIDetalles(conn, cfdiId));
            return cfdi;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CFDI getCFDI(int cfdiId) {
        Connection conn = null;
        try {
            log.info("Object: {}", "Fetching CFDI with ID: " + cfdiId);
            conn = getConnection();
            return getCFDI(conn, cfdiId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public CFDI insertCFDI(CFDI cfdi) {
        Connection conn = null;
        try {
            log.info("Object: {}", "Creating CFDI: " + cfdi);
            conn = getConnection();
            CFDIEncabezado encabezado = CFDIEncabezadoManager.guardarCFDIEncabezado(conn, cfdi.getEncabezado());
            cfdi.setEncabezado(encabezado);
            for (CFDIDetalle detalle : cfdi.getDetalles()) {
                detalle.setCfdiId(encabezado.getCfdiId());
                CFDIDetalleManager.guardarCFDIDetalle(conn, detalle);
            }
            conn.commit();
            return cfdi;
        } catch (Exception e) {
            Util.rollback(conn);
            throw new RuntimeException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public CFDIDetalle insertCFDIDetail(CFDIDetalle detailRow) {
        Connection conn = null;
        try {
            log.info("Object: {}", "Creating CFDI Detail: " + detailRow);
            conn = getConnection();
            detailRow = CFDIDetalleManager.guardarCFDIDetalle(conn, detailRow);
            CFDIEncabezadoManager.actualizaMonto(conn, detailRow.getCfdiId());
            conn.commit();
            return detailRow;
        } catch (Exception e) {
            Util.rollback(conn);
            throw new RuntimeException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public CFDIEncabezado insertCFDIHeader(CFDIEncabezado header) {
        Connection conn = null;
        try {
            log.info("Object: {}", "Creating CFDI: " + header);
            conn = getConnection();
            header = CFDIEncabezadoManager.guardarCFDIEncabezado(conn, header);
            conn.commit();
            return header;
        } catch (Exception e) {
            Util.rollback(conn);
            throw new RuntimeException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public CFDI stampInvoice(CFDI invoice, DigitalSignature digitalSignature) {
        Connection conn = null;
        String xml = null;
        try {
            conn = getConnection();
            CFDv40 toStamp = StampInvoiceRepository.instanceFromDB(conn, invoice.getEncabezado().getCfdiId());
            Comprobante comprobante = (Comprobante) toStamp.getComprobanteDocument();
            if (comprobante.getReceptor().getRegimenFiscalReceptor() == null)
                throw new RuntimeException("El receptor: " + comprobante.getReceptor().getRfc() + " No tiene registrado regimen fiscal.");
            if (comprobante.getReceptor().getDomicilioFiscalReceptor() == null)
                throw new RuntimeException("El receptor: " + comprobante.getReceptor().getRfc() + " No tiene registrado Domiciclio FIscal (CP)");
            comprobante.getReceptor().setNombre(CFDIUtils.plainName(comprobante.getReceptor().getNombre()));
            toStamp.sellar(digitalSignature.getKey(), digitalSignature.getCert());
            StampResponseV2 response = firmaCFDI(conn, toStamp);
            if ("success".equals(String.valueOf(response.getStatus()))) {
                log.debug("Object: {}", response.getData().getTFD());
                log.debug("Object: {}", response.getData().getCFDI());
                xml = response.getMessageDetail();
            } else {
                log.debug("Object: {}", response.getMessage());
                log.debug("Object: {}", response.getMessageDetail());
                if ("307. El comprobante contiene un timbre previo.".equalsIgnoreCase(response.getMessage())) {
                    xml = response.getData().getCFDI();
                } else {
                    Exception e = new Exception(response.getMessage() + " / " + response.getMessageDetail());
                    throw e;
                }
            }
            invoice.setXmlInvoice(xml);
            CFDIEncabezadoManager.saveStampedInvoice(conn, invoice);
            invoice = getCFDI(conn, invoice.getEncabezado().getCfdiId());
            invoice.setXmlInvoice(xml);
            conn.commit();
            return invoice;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Util.rollback(conn);
            throw new RuntimeException("No fue posible timbrar el CFDI debido al error: " + e, e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public CFDI updateCFDI(CFDI cfdi) {
        Connection conn = null;
        try {
            log.info("Object: {}", "Updating CFDI: " + cfdi);
            conn = getConnection();
            CFDIEncabezadoManager.actualizarCFDIEncabezado(conn, cfdi.getEncabezado());
            cfdi.setEncabezado(CFDIEncabezadoManager.obtenerCFDIEncabezado(conn, cfdi.getEncabezado().getCfdiId()));
            conn.commit();
            return cfdi;
        } catch (Exception e) {
            Util.rollback(conn);
            throw new RuntimeException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public CFDI finaliza(CFDI invoice) {
        Connection conn = null;
        try {
            conn = getConnection();
            invoice.getEncabezado().setEstatusId(6);
            CFDIEncabezadoManager.actualizarCFDIEncabezado(conn, invoice.getEncabezado());
            invoice.setEncabezado(CFDIEncabezadoManager.obtenerCFDIEncabezado(conn, invoice.getEncabezado().getCfdiId()));
            conn.commit();
            return invoice;
        } catch (Exception e) {
            Util.rollback(conn);
            throw new RuntimeException("Error finalizando emision de CFDI: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void sendInvoice(CFDI invoice) {
        Connection conn = null;
        try {
            String ccInvoice = ConfiguraAplicativoManager.getSystemSetting(conn, "MAIL_CC_INVOICE");
            invoice.getEncabezado().getReceptor().getEmail();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            CloseObject.closeObject(conn);
        }
    }
    /*
	 * public void deleteCFDI( int cfdiId ) { Connection conn = null; try {
	 * log.info( "Deleting CFDI with ID: " + cfdiId ); conn = getConnection();
	 * CFDIManager.eliminarCFDI( conn, cfdiId ); conn.commit(); } catch (
	 * Exception e ) { Util.rollback( conn ); throw new RuntimeException( e ); }
	 * finally { CloseObject.closeObject( conn ); } }
	 * 
	 * public List<CFDI> getAllCFDIs() { Connection conn = null; try { log.info(
	 * "Fetching all CFDIs" ); conn = getConnection(); return
	 * CFDIManager.obtenerTodosCFDIs( conn ); } catch ( Exception e ) { throw
	 * new RuntimeException( e ); } finally { CloseObject.closeObject( conn ); }
	 * }
	 */
}
