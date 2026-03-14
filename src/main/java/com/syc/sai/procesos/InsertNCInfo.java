package com.syc.sai.procesos;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.input.BOMInputStream;
import com.syc.cfdi.utils.FacturaUtils;
import com.syc.cfdi.v3332.Comprobante.Comprobante;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import mx.grupocorasa.sat.cfd._40.Comprobante.CfdiRelacionados;
import mx.grupocorasa.sat.cfd._40.Comprobante.CfdiRelacionados.CfdiRelacionado;
import mx.grupocorasa.sat.cfdi.v3.CFDv33;
import mx.grupocorasa.sat.cfdi.v3.CFDv3Factory;
import mx.grupocorasa.sat.cfdi.v4.CFDv40;
import java.util.Base64;

public class InsertNCInfo {

    class NcInfo {

        private int cabinet;

        private String documentName;

        private File filePath;

        private String folderName;

        private int idFolder;

        private String processFolio;

        private int processID;

        private String processType;

        private String receipt;

        public int getCabinet() {
            return cabinet;
        }

        public String getDocumentName() {
            return documentName;
        }

        public File getFilePath() {
            return filePath;
        }

        public String getFolderName() {
            return folderName;
        }

        public int getIdFolder() {
            return idFolder;
        }

        public String getProcessFolio() {
            return processFolio;
        }

        public int getProcessID() {
            return processID;
        }

        public String getProcessType() {
            return processType;
        }

        public String getReceipt() {
            return receipt;
        }

        public void setCabinet(int cabinet) {
            this.cabinet = cabinet;
        }

        public void setDocumentName(String documentName) {
            this.documentName = documentName;
        }

        public void setFilePath(File filePath) {
            this.filePath = filePath;
        }

        public void setFolderName(String folderName) {
            this.folderName = folderName;
        }

        public void setIdFolder(int idFolder) {
            this.idFolder = idFolder;
        }

        public void setProcessFolio(String processFolio) {
            this.processFolio = processFolio;
        }

        public void setProcessID(int processID) {
            this.processID = processID;
        }

        public void setProcessType(String processType) {
            this.processType = processType;
        }

        public void setReceipt(String receipt) {
            this.receipt = receipt;
        }

        @Override
        public String toString() {
            return "NcInfo [processType=" + processType + ", cabinet=" + cabinet + ", idFolder=" + idFolder + ", folderName=" + folderName + ", documentName=" + documentName + ", filePath=" + filePath + ", processFolio=" + processFolio + ", processID=" + processID + ", receipt=" + receipt + "]";
        }
    }

    public static void main(String[] args) throws Exception {
        String urlConn = args[0];
        String user = args[1];
        String pass = args[2];
        InsertNCInfo processor = new InsertNCInfo(urlConn, user, pass);
        processor.runProcess();
    }

    private Connection connection = null;

    private PreparedStatement psInfo = null;

    private PreparedStatement psInsertInfo = null;

    private PreparedStatement psSearchInfo = null;

    private final StringBuilder querySelectNCInfo = new StringBuilder("  SELECT	folder.TITULO_APLICACION AS processType, " + " 		folder.ID_GABINETE AS cabinet, " + " 		folder.ID_CARPETA AS idFolder, " + " 		folder.NOMBRE_CARPETA AS folderName, " + " 		document.NOMBRE_DOCUMENTO AS documentName, " + " 		vol.UNIDAD_DISCO + vol.RUTA_BASE + vol.RUTA_DIRECTORIO + page.NOM_ARCHIVO_VOL AS filePath, " + " 		processInfo.folio AS processFolio, " + " 		processInfo.nfolio AS processID, " + " 		processInfo.canocontrarrecibo AS receipt " + "   FROM	IMX_CARPETA AS folder with(nolock) " + " 		INNER JOIN " + " 		IMX_DOCUMENTO document with(nolock) " + " 		ON  " + " 			   folder.TITULO_APLICACION = document.TITULO_APLICACION " + " 		   AND folder.ID_GABINETE = document.ID_GABINETE " + " 		   AND folder.ID_CARPETA = document.ID_CARPETA_PADRE " + " 		INNER JOIN " + " 		IMX_PAGINA page WITH(NOLOCK) " + " 		ON " + " 			   document.TITULO_APLICACION = page.TITULO_APLICACION " + " 		   AND document.ID_GABINETE = page.ID_GABINETE " + " 		   AND document.ID_CARPETA_PADRE = page.ID_CARPETA_PADRE " + " 		   AND document.ID_DOCUMENTO = page.ID_DOCUMENTO " + " 		INNER JOIN " + " 		IMX_VOLUMEN vol WITH(NOLOCK) " + " 		ON " + " 				page.VOLUMEN = vol.VOLUMEN " + " 		LEFT OUTER JOIN  " + " 		vTramitesExportar processInfo " + " 		ON " + " 				processInfo.cdocumento = folder.TITULO_APLICACION " + " 		   AND	processInfo.id_gabinete = folder.ID_GABINETE " + "  WHERE	folder.NOMBRE_CARPETA = 'NC' " + "    AND	processInfo.cdocumentohaplicado = 'S' " + "    AND	NOM_ARCHIVO_ORG like '%.xml' " + "    AND canocontrarrecibo = '10CP2024117811'" + " ORDER BY	folder.TITULO_APLICACION, folder.ID_GABINETE, folder.ID_CARPETA, document.ID_DOCUMENTO ");

    private final StringBuilder queryInsertNCInfo = new StringBuilder("INSERT INTO tPagoNotaCredito (cTipoPago ,nFolioPago ,cUUID ,cUUIDRelacionado ,mimporteBruto, mimporteconiva ,mimporteiva ,cRFCFactura ,mOtrosImpuestos ,mImporteDescuento ,dFechaNC ,dFechaTimbrado)\r\n" + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

    private final StringBuilder querySearchRelaedInfo = new StringBuilder("SELECT COUNT(*) as total FROM tPagoNotaCredito WHERE cUUID = ? AND cUUIDRelacionado = ?");

    public InsertNCInfo(String url, String user, String password) throws ClassNotFoundException, SQLException {
        connection = openDBConnection(url, user, password);
        psInfo = connection.prepareStatement(querySelectNCInfo.toString());
        psInsertInfo = connection.prepareStatement(queryInsertNCInfo.toString());
        psSearchInfo = connection.prepareStatement(querySearchRelaedInfo.toString());
    }

    private List<NcInfo> loadNCInfo() throws SQLException {
        List<NcInfo> ncInfo = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = psInfo.executeQuery();
            while (rs.next()) {
                NcInfo infoRow = new NcInfo();
                infoRow.setCabinet(rs.getInt("cabinet"));
                infoRow.setDocumentName(rs.getString("documentName"));
                infoRow.setFilePath(new File(rs.getString("filePath")));
                infoRow.setFolderName(rs.getString("folderName"));
                infoRow.setIdFolder(rs.getInt("idFolder"));
                infoRow.setProcessFolio(rs.getString("processFolio"));
                infoRow.setProcessID(rs.getInt("processID"));
                infoRow.setProcessType(rs.getString("processType"));
                infoRow.setReceipt(rs.getString("receipt"));
                ncInfo.add(infoRow);
            }
            return ncInfo;
        } finally {
            CloseObject.closeObject(rs);
        }
    }

    private Connection openDBConnection(String urlConn, String user, String pass) throws ClassNotFoundException, SQLException {
        Connection conn = null;
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        conn = DriverManager.getConnection(urlConn, user, pass);
        conn.setAutoCommit(false);
        return conn;
    }

    private void runProcess() throws Exception {
        List<NcInfo> ncList = loadNCInfo();
        int i = 0;
        for (NcInfo info : ncList) {
            i++;
            try {
                System.out.println("Se procesa: " + i + " de " + ncList.size() + ": " + info);
                Comprobante nc = readFileInfo(info);
                insertInfo(info, nc);
            } catch (Exception e) {
                System.out.println("Problemas procesando: " + info + " \n Error: " + e.toString());
                e.printStackTrace();
            }
        }
        connection.commit();
        CloseObject.closeObject(psInfo);
        CloseObject.closeObject(psInsertInfo);
        CloseObject.closeObject(psSearchInfo);
        CloseObject.closeObject(connection);
    }

    private void insertInfo(NcInfo info, Comprobante nc) throws SQLException {
        int inserted = 0;
        if (nc.isCfd40()) {
            List<CfdiRelacionados> uuidRelatedList = nc.getComprobante40().getCfdiRelacionados();
            if (uuidRelatedList != null && uuidRelatedList.size() > 0)
                for (CfdiRelacionados relatedInvoices : uuidRelatedList) {
                    if ("01".equals(relatedInvoices.getTipoRelacion().value())) {
                        List<CfdiRelacionado> relatedInvoiceList = relatedInvoices.getCfdiRelacionado();
                        for (CfdiRelacionado invoice : relatedInvoiceList) {
                            if (existsRelationUUID(nc.getUUID(), invoice.getUUID()))
                                continue;
                            inserted += insertRelation(info, nc, invoice.getUUID());
                        }
                    }
                }
        } else if (nc.isCfd33()) {
            mx.grupocorasa.sat.cfd._33.Comprobante.CfdiRelacionados relatedInvoices = nc.getComprobante33().getCfdiRelacionados();
            List<mx.grupocorasa.sat.cfd._33.Comprobante.CfdiRelacionados.CfdiRelacionado> relatedInvoicesList;
            if (relatedInvoices != null && "01".equals(relatedInvoices.getTipoRelacion().value())) {
                relatedInvoicesList = relatedInvoices.getCfdiRelacionado();
                for (mx.grupocorasa.sat.cfd._33.Comprobante.CfdiRelacionados.CfdiRelacionado invoice : relatedInvoicesList) {
                    if (existsRelationUUID(nc.getUUID(), invoice.getUUID()))
                        continue;
                    inserted += insertRelation(info, nc, invoice.getUUID());
                }
            }
        }
        System.out.println("Se insertaron: " + inserted + " registros");
    }

    private int insertRelation(NcInfo info, Comprobante nc, String uuidParent) throws SQLException {
        String uuidNC = nc.getUUID();
        psInsertInfo.setString(1, info.getProcessType());
        psInsertInfo.setInt(2, info.getProcessID());
        psInsertInfo.setString(3, uuidNC);
        psInsertInfo.setString(4, uuidParent);
        psInsertInfo.setBigDecimal(5, nc.getSubTotal());
        psInsertInfo.setBigDecimal(6, nc.getTotal());
        psInsertInfo.setBigDecimal(7, nc.getTotal().subtract(nc.getSubTotal()));
        psInsertInfo.setString(8, nc.getRFCEmisor());
        psInsertInfo.setBigDecimal(9, Util.ZERO);
        psInsertInfo.setBigDecimal(10, Util.ZERO);
        psInsertInfo.setDate(11, new Date(nc.getFechaExpedicionMillis()));
        psInsertInfo.setDate(12, new Date(nc.getFechaTimbradoMillis()));
        System.out.println("Se insertara la relacion: " + info.getProcessType() + " - " + info.getProcessID() + "[" + uuidNC + "][" + uuidParent + "]");
        return psInsertInfo.executeUpdate();
    }

    private boolean existsRelationUUID(String uuidNC, String parentUUID) throws SQLException {
        ResultSet rs = null;
        boolean existe = false;
        try {
            psSearchInfo.setString(1, uuidNC);
            psSearchInfo.setString(2, parentUUID);
            rs = psSearchInfo.executeQuery();
            if (rs.next())
                existe = rs.getInt(1) > 0;
            return existe;
        } finally {
            CloseObject.closeObject(rs);
        }
    }

    private Comprobante readFileInfo(NcInfo info) throws Exception {
        String version = FacturaUtils.readVersion(info.getFilePath());
        InputStream in = new FileInputStream(info.getFilePath());
        InputStream inBOM = new BOMInputStream(in);
        Comprobante comprobante = null;
        if ("3.3".equals(version)) {
            CFDv33 cfd = (CFDv33) CFDv3Factory.load(inBOM);
            comprobante = new Comprobante((mx.grupocorasa.sat.cfd._33.Comprobante) ((CFDv33) cfd).getComprobanteDocument());
        } else if ("4.0".equals(version)) {
            CFDv40 cfd = new CFDv40(inBOM);
            comprobante = new Comprobante((mx.grupocorasa.sat.cfd._40.Comprobante) ((CFDv40) cfd).getComprobanteDocument());
        }
        in.close();
        inBOM.close();
        return comprobante;
    }
}
