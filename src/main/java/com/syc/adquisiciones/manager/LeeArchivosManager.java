package com.syc.adquisiciones.manager;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.axtel.contratos.Requisition;
import com.syc.adquisiciones.core.DatosArchivo;
import com.syc.adquisiciones.util.Util;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class LeeArchivosManager {

    private final Logger log = LoggerFactory.getLogger(LeeArchivosManager.class);

    public int bullkinsertEFOS(Connection conn, DatosArchivo datosArchivo) throws SQLException {
        int rows = 0;
        PreparedStatement pstm = null;
        String query = "BULK INSERT mHistoriaEFOSSAT FROM '" + datosArchivo.getcDominio() + datosArchivo.getcRuta()[3] + "\\" + datosArchivo.getcNombreArchivoDestino() + "' WITH (FORMAT = 'CSV', FIRSTROW=4,FIELDTERMINATOR = ',',ROWTERMINATOR = '\\n')";
        try {
            query = "BULK INSERT mHistoriaEFOSSAT FROM '' WITH (FORMAT = 'CSV', FIRSTROW=4,FIELDTERMINATOR = ',',ROWTERMINATOR = '\\n')";
            pstm = conn.prepareStatement(query);
            rows = pstm.executeUpdate();
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
        }
        return rows;
    }

    public void readFileExcelAndInsertDB(Connection conn, DatosArchivo datArchivo) throws Exception {
        XSSFWorkbook workbook = null;
        Row row = null;
        Cell cell = null;
        JSONObject jsonObj = null;
        try {
            workbook = new XSSFWorkbook(datArchivo.getArchivoStream());
            XSSFSheet firstSheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = firstSheet.rowIterator();
            int initRow = 4, k = 0;
            boolean insertar = true;
            while (rowIterator.hasNext()) {
                k++;
                row = rowIterator.next();
                if (k < initRow)
                    continue;
                jsonObj = new JSONObject();
                for (int c = row.getFirstCellNum(); c < 20; c++) {
                    cell = row.getCell(c);
                    if (cell == null) {
                        jsonObj.put("" + c, "");
                    } else {
                        if (c == 1 && "XXXXXXXXXXXX".equalsIgnoreCase(cell.getRichStringCellValue().getString())) {
                            insertar = false;
                            break;
                        }
                        fillObject(cell, "" + c, jsonObj);
                    }
                }
                if (insertar) {
                    insertEFOSTMP(conn, jsonObj);
                }
                row = null;
                jsonObj = null;
            }
        } finally {
            if (workbook != null) {
                workbook.close();
            }
            workbook = null;
        }
    }

    public boolean insertEFOSTMP(Connection conn, JSONObject jsonObj) throws SQLException, JSONException {
        boolean resp = false;
        PreparedStatement pstmnt = null;
        String query = null;
        try {
            query = "insert into mHistoriaEFOSSAT (num 	,cIdRFC 	,cRazonSocial  	,cSituacion 	,cOficioPresuncion 	,fPublicacionSATPresunto 	,cOficioPresuncionGobal " + ",fPublicacionDOFPresunto 	,cOficioDesvirtuado,fPublicacionSATDesvirtuado, cOficioGlobalDesvirtuado	,fPublicacionDOFDesvirtuado 	,cOficioDeFinitivo 	" + ",fPublicacionSATDefinitivo,cOficioGlobalDOFDeFinitivo 	" + ",fPublicacionDOFDefinitivo 	,cOficioSentFaovorable 	,fPublicacionSATSentFaovorable 	,cOficioSentFaovorableGlobal 	,fPublicacionDOFSentFaovorable )" + "VALUES(?,?,?,?,?,CONVERT(DATE,?),?,CONVERT(DATE,?),?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmnt = conn.prepareStatement(query);
            for (int i = 0; i < jsonObj.length(); i++) {
                if (i == 0) {
                    pstmnt.setInt(i + 1, jsonObj.getInt("" + i));
                } else {
                    pstmnt.setString(i + 1, jsonObj.getString("" + i));
                }
            }
            resp = pstmnt.execute();
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
        }
        return resp;
    }

    public int insertEFOS(Connection conn) throws SQLException {
        int resp = 0;
        PreparedStatement pstmnt = null;
        String query = null;
        try {
            query = "insert into mCatalogoEFOS  (cIdRFC,cRazonSocial,nSituacion,cOficioPresuncion,fPublicacionSATPresunto, " + "fPublicacionDOFPresunto,fPublicacionSATDesvirtuado,cOficioDesvirtuado, " + "fPublicacionDOFDesvirtuado,cOficioDeFinitivo,fPublicacionSATDefinitivo, " + "fPublicacionDOFDefinitivo,cOficioSentFaovorable,fPublicacionSATSentFaovorable, " + "fPublicacionDOFSentFaovorable,fFechaCarga,fFechaActualiza,cOficioGlobalDesvirtuado,cOficioGlobalDOFDeFinitivo) " + "select cat.cIdRFC ,cat.cRazonSocial,sit.cSituacion,cat.cOficioPresuncion,cat.fPublicacionSATPresunto,cat.fPublicacionDOFPresunto,cat.fPublicacionSATDesvirtuado " + ",cat.cOficioDesvirtuado,cat.fPublicacionDOFDesvirtuado,cat.cOficioDeFinitivo,cat.fPublicacionSATDefinitivo,cat.fPublicacionDOFDefinitivo,cat.cOficioSentFaovorable " + ",cat.fPublicacionSATSentFaovorable,cat.fPublicacionDOFSentFaovorable,getdate(),null,cOficioGlobalDesvirtuado,cOficioGlobalDOFDeFinitivo from mHistoriaEFOSSAT cat with(nolock) " + "inner join( select 	cIdRFC	,MAX(fPublicacionSATPresunto) fPublicacionSATPresunto 	from mHistoriaEFOSSAT with(nolock) 	group by cIdRFC )sinRep " + "on cat.cIdRFC=sinRep.cIdRFC and sinRep.fPublicacionSATPresunto=cat.fPublicacionSATPresunto " + "inner join tArticulo69B_Situacion as sit with(Nolock) on sit.dSituacion=cat.cSituacion " + "where cat.cIdRFC not in(select cIdRFC from mCatalogoEFOS with(Nolock))";
            pstmnt = conn.prepareStatement(query);
            resp = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
        }
        return resp;
    }

    public int updateEFOSExisting(Connection conn) throws SQLException {
        int resp = 0;
        PreparedStatement pstmnt = null;
        String query = null;
        try {
            query = "Update mCatalogoEFOS" + " set mCatalogoEFOS.cRazonSocial = temp.cRazonSocial" + " ,mCatalogoEFOS.nSituacion=temp.cSituacion" + " ,mCatalogoEFOS.cOficioPresuncion = temp.cOficioPresuncion" + " ,mCatalogoEFOS.fPublicacionSATPresunto = temp.fPublicacionSATPresunto" + " ,mCatalogoEFOS.fPublicacionDOFPresunto = temp.fPublicacionDOFPresunto" + " ,mCatalogoEFOS.fPublicacionSATDesvirtuado = temp.fPublicacionSATDesvirtuado" + " ,mCatalogoEFOS.cOficioDesvirtuado = temp.cOficioDesvirtuado" + " ,mCatalogoEFOS.fPublicacionDOFDesvirtuado = temp.fPublicacionDOFDesvirtuado" + " ,mCatalogoEFOS.cOficioDeFinitivo = temp.cOficioDeFinitivo" + " ,mCatalogoEFOS.fPublicacionSATDefinitivo = temp.fPublicacionSATDefinitivo" + " ,mCatalogoEFOS.fPublicacionDOFDefinitivo = temp.fPublicacionDOFDefinitivo" + " ,mCatalogoEFOS.cOficioSentFaovorable = temp.cOficioSentFaovorable" + " ,mCatalogoEFOS.fPublicacionSATSentFaovorable = temp.fPublicacionSATSentFaovorable" + " ,mCatalogoEFOS.fPublicacionDOFSentFaovorable = temp.fPublicacionDOFSentFaovorable" + " ,mCatalogoEFOS.fFechaActualiza=GETDATE() " + " ,mCatalogoEFOS.cOficioGlobalDesvirtuado = temp.cOficioGlobalDesvirtuado" + " ,mCatalogoEFOS.cOficioGlobalDOFDeFinitivo = temp.cOficioGlobalDOFDeFinitivo" + " from mCatalogoEFOS efos with(nolock)," + " (	select cat.cIdRFC ,cat.cRazonSocial,sit.cSituacion,cat.cOficioPresuncion,cat.fPublicacionSATPresunto,cat.fPublicacionDOFPresunto,cat.fPublicacionSATDesvirtuado" + "	,cat.cOficioDesvirtuado,cat.fPublicacionDOFDesvirtuado,cat.cOficioDeFinitivo,cat.fPublicacionSATDefinitivo,cat.fPublicacionDOFDefinitivo,cat.cOficioSentFaovorable" + "	,cat.fPublicacionSATSentFaovorable,cat.fPublicacionDOFSentFaovorable " + "  ,cat.cOficioGlobalDesvirtuado,cat.cOficioGlobalDOFDeFinitivo " + "  from mHistoriaEFOSSAT cat with(nolock) " + "	inner join( select 	cIdRFC	,MAX(fPublicacionSATPresunto) fPublicacionSATPresunto 	from mHistoriaEFOSSAT with(nolock) 	group by cIdRFC )sinRep " + "	on cat.cIdRFC=sinRep.cIdRFC and sinRep.fPublicacionSATPresunto=cat.fPublicacionSATPresunto" + "	inner join tArticulo69B_Situacion as sit with(Nolock) on sit.dSituacion=cat.cSituacion" + " )temp " + " where temp.cIdRFC=efos.cIdRFC";
            pstmnt = conn.prepareStatement(query);
            resp = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
        }
        return resp;
    }

    public int deleteEFOSTEMP(Connection conn) throws SQLException {
        int resp = 0;
        PreparedStatement pstmnt = null;
        String query = null;
        try {
            query = "delete from mHistoriaEFOSSAT";
            pstmnt = conn.prepareStatement(query);
            resp = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
        }
        return resp;
    }

    @SuppressWarnings("deprecation")
    public void fillObject(Cell cell, String i, JSONObject jsonObj) throws Exception {
        DateFormat df = DateFormat.getDateInstance();
        switch(cell.getCellTypeEnum()) {
            case BOOLEAN:
                jsonObj.put(i, cell.getBooleanCellValue());
                break;
            case STRING:
                jsonObj.put(i, cell.getRichStringCellValue().getString());
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    jsonObj.put(i, df.format(cell.getDateCellValue()));
                } else {
                    jsonObj.put(i, cell.getNumericCellValue());
                }
                break;
            case FORMULA:
                jsonObj.put(i, cell.getCellFormula());
                break;
            case BLANK:
                jsonObj.put(i, "");
                break;
            default:
                jsonObj.put(i, "");
        }
    }

    @SuppressWarnings("deprecation")
    public boolean validateCell(Cell cell) throws Exception {
        boolean error = false;
        switch(cell.getCellTypeEnum()) {
            case BLANK:
                error = true;
                break;
            case NUMERIC:
                if (cell.getNumericCellValue() < 0) {
                    error = true;
                }
                break;
            case STRING:
                if (StringUtils.isBlank(cell.getStringCellValue())) {
                    error = true;
                }
                break;
        }
        return error;
    }

    @SuppressWarnings("deprecation")
    public void printCellValue(Cell cell) throws Exception {
        DateFormat df = DateFormat.getDateInstance();
        switch(cell.getCellTypeEnum()) {
            case BOOLEAN:
                System.out.print(cell.getBooleanCellValue());
                break;
            case STRING:
                System.out.print(cell.getRichStringCellValue().getString());
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    System.out.print(df.format(cell.getDateCellValue()));
                } else {
                    System.out.print(cell.getNumericCellValue());
                }
                break;
            case FORMULA:
                System.out.print(cell.getCellFormula());
                break;
            case BLANK:
                System.out.print("");
                break;
            default:
                System.out.print("");
        }
        System.out.print("\t");
    }

    public boolean uploadStreamServerBD(String nameFile, DataInputStream archivoCargaStream, Connection conn) throws Exception, ArrayIndexOutOfBoundsException {
        String dominio;
        String usuarioRemoto;
        String passwordRemoto;
        String rutaRemoto;
        dominio = ConfiguraAplicativoManager.obtenDominioRemoto(conn);
        usuarioRemoto = ConfiguraAplicativoManager.obtenUsuarioRemotoFurrt(conn);
        passwordRemoto = ConfiguraAplicativoManager.obtenPasswordRemoto(conn);
        rutaRemoto = ConfiguraAplicativoManager.obtenRutaRemoto(conn);
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(dominio, usuarioRemoto, passwordRemoto);
        String sharepath = rutaRemoto + nameFile;
        SmbFile sFile = new SmbFile(sharepath, auth);
        SmbFileOutputStream out = new SmbFileOutputStream(sFile, true);
        byte[] buffer = null;
        String line = "";
        int length;
        buffer = new byte[1024];
        while ((length = archivoCargaStream.read(buffer)) > 0) {
            line = new String(buffer, "ISO-8859-1");
            line = line.replace(", S.", "; S.");
            buffer = line.getBytes(StandardCharsets.ISO_8859_1);
            System.out.println(line);
            System.out.println("length=" + length);
            out.write(buffer, 0, length);
        }
        out.flush();
        out.close();
        return true;
    }

    public void sendEmailLoadEFOS(Connection conn, File file) {
        String recipients = "";
        String token = "";
        String body = "";
        String subject = "Carga de layout EFOS en SAI";
        String queryEmails = "";
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            queryEmails = "select U_EMAIL from mListaUsuariosSendEmailEFOS list with(Nolock)" + " inner join CG_USUARIO as usuario with(nolock)" + " on usuario.U_LOGIN=list.cLogin";
            ps = conn.prepareStatement(queryEmails);
            rs = ps.executeQuery();
            while (rs.next()) {
                recipients = recipients + token + rs.getString("U_EMAIL");
                token = ";";
            }
            body = "<B>Atenci\u00f3n.</b></br>" + "Se notifica que fue cargado el layout EFOS del SAT en el sistema SAI.<br><br>" + "Por lo anterior es necesario que se verifique y se valide el archivo adjunto. <br><br>" + "Gracias y reciban un cordial saludo.<br> ";
            try {
                AlarmaManager.procesaAlarmaAttachmentCNF(conn, "", null, null, subject, recipients, body, file, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public void writeSheet1ProveedoresEFOS(Connection conn, XSSFSheet firstSheet, XSSFCellStyle estiloTabla) throws Exception {
        String query = "select catEFOS.cIdRFC,catProv.cRazonSocial,sit.dSituacion estatus_SAT,case when catProv.lHabilitado=1 then 'HABILITADO' ELSE 'DESHABILITADO' END estatus_SAI" + " ,catEFOS.cOficioPresuncion,convert(varchar,catEFOS.fPublicacionSATPresunto,103)fPublicacionSATPresunto,convert(varchar,catEFOS.fPublicacionDOFPresunto,103)fPublicacionDOFPresunto" + " ,catEFOS.fPublicacionSATDesvirtuado,catEFOS.cOficioDesvirtuado,catEFOS.fPublicacionDOFDesvirtuado,catEFOS.cOficioDeFinitivo" + " ,catEFOS.fPublicacionSATDefinitivo,catEFOS.fPublicacionDOFDefinitivo,catEFOS.cOficioSentFaovorable,catEFOS.fPublicacionSATSentFaovorable" + " ,catEFOS.fPublicacionDOFSentFaovorable,catProv.cGiro,catProv.cRepresentanteLegal,catProv.cEmail from mCatalogoEFOS as catEFOS with(Nolock)" + " inner join mCatalogoProveedor as catProv with(Nolock)" + " on replace(catProv.cIdRFC,'-','')=catEFOS.cIdRFC" + " inner join tArticulo69B_Situacion as sit with(nolock)" + " on sit.cSituacion=catEFOS.nSituacion order by catProv.lHabilitado desc";
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        XSSFRow rw = null;
        try {
            ps = conn.prepareStatement(query);
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            //Escribe el detalle
            int renglonInicio = 4;
            int rows = 0;
            int j = 0;
            int cnt = 0;
            int cantRowsFinal = 1;
            while (rst.next()) {
                rows = renglonInicio + cnt;
                if (j > 7) {
                    firstSheet.shiftRows(rows, rows + cantRowsFinal, 1);
                    rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                } else {
                    rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                }
                j++;
                for (int i = 0; i < totalcolumnas; i++) {
                    com.syc.gestion.util.Util.createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
                cnt++;
            }
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
        }
    }

    private String creaCadenaConsultaContratos(Connection conn) throws Exception {
        String cadena = "";
        String token = "";
        String query = "";
        PreparedStatement ps = null;
        ResultSet rst = null;
        try {
            query = "SELECT *FROM  tEjercicioFiscal with(Nolock)";
            ps = conn.prepareStatement(query);
            rst = ps.executeQuery();
            while (rst.next()) {
                cadena = cadena + token + " select cEjercicio,cIdRFC, cIdContratoDefinitivo cIdContrato,cNoProcedimientoCNET,nCodContratoCNET,nCodExpedienteCNET,cConceptoContrato from " + rst.getString("cNombreBD") + "..v_mContratosAdqObra with(Nolock) ";
                token = " union  ";
            }
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rst = null;
            ps = null;
            query = null;
            token = null;
        }
        return cadena;
    }

    public void writeSheet2ProveedoresEFOS(Connection conn, XSSFSheet secondSheet, XSSFCellStyle estiloTabla) throws Exception {
        String query = "";
        String cadenaQuery = "";
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        XSSFRow rw = null;
        try {
            cadenaQuery = creaCadenaConsultaContratos(conn);
            if (null == cadenaQuery || "".equalsIgnoreCase(cadenaQuery)) {
                throw new Exception("Error al consultar contratos de ejercicios anteriores.");
            }
            query = "select cEjercicio,isnull(cont.cIdRFC,'')cIdRFC " + ",isnull(cont.cIdContrato,'')cIdContrato " + ",isnull(cont.cNoProcedimientoCNET,'')cNoProcedimientoCNET " + ",isnull(cont.nCodContratoCNET,'')nCodContratoCNET " + ",isnull(cont.nCodExpedienteCNET,'')nCodExpedienteCNET " + ",isnull(cont.cConceptoContrato,'')cConceptoContrato " + "from mCatalogoEFOS as catEFOS with(Nolock)  " + "inner join mCatalogoProveedor as catProv with(Nolock)  " + "on replace(catProv.cIdRFC,'-','')=catEFOS.cIdRFC  " + "inner join tArticulo69B_Situacion as sit with(nolock)  " + "on sit.cSituacion=catEFOS.nSituacion " + "inner join( " + cadenaQuery + ")cont on cont.cIdRFC=catEFOS.cIdRFC " + "order by cEjercicio desc";
            ps = conn.prepareStatement(query);
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            //Escribe el detalle
            int renglonInicio = 4;
            int rows = 0;
            int j = 0;
            int cnt = 0;
            int cantRowsFinal = 1;
            while (rst.next()) {
                rows = renglonInicio + cnt;
                if (j > 7) {
                    secondSheet.shiftRows(rows, rows + cantRowsFinal, 1);
                    rw = (secondSheet.getRow(rows) == null ? secondSheet.createRow(rows) : secondSheet.getRow(rows));
                } else {
                    rw = (secondSheet.getRow(rows) == null ? secondSheet.createRow(rows) : secondSheet.getRow(rows));
                }
                j++;
                for (int i = 0; i < totalcolumnas; i++) {
                    com.syc.gestion.util.Util.createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
                cnt++;
            }
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
        }
    }

    public boolean updatePhysicalContractUpload(Connection conn, String cIdContratoDefinitivo, int lArchivoContCargado) throws SQLException {
        boolean resp = false;
        PreparedStatement pstmnt = null;
        String query = null;
        try {
            query = "update mContrato set lArchivoContCargado=" + lArchivoContCargado + " where cIdContratoDefinitivo='" + cIdContratoDefinitivo + "'";
            pstmnt = conn.prepareStatement(query);
            resp = pstmnt.executeUpdate() > 0;
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
            query = null;
        }
        return resp;
    }

    public boolean existDoctoContract(Connection conn, String cIdContratoDefinitivo, int nIdDocumento) throws SQLException {
        String query = "";
        boolean resp = false;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            query = "select *from mDocumentosContrato with(nolock) where cIdContratoDefinitivo='" + cIdContratoDefinitivo + "' and nIdDocumento=" + nIdDocumento;
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
        return resp;
    }

    public boolean insertDoctoContrato(Connection conn, String cIdContratoDefinitivo, int nIdDocumento, String cLogin) throws SQLException {
        boolean resp = false;
        PreparedStatement pstmnt = null;
        String query = null;
        try {
            query = "insert into mDocumentosContrato (cIdContratoDefinitivo,nIdDocumento,fFechaCarga,cUsuarioCarga) " + "values('" + cIdContratoDefinitivo + "'," + nIdDocumento + ",GETDATE(),'" + cLogin + "') ";
            pstmnt = conn.prepareStatement(query);
            resp = pstmnt.executeUpdate() > 0;
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
            query = null;
        }
        return resp;
    }

    public boolean updateDoctoContrato(Connection conn, String cIdContratoDefinitivo, int nIdDocumento, String cLogin) throws SQLException {
        boolean resp = false;
        PreparedStatement pstmnt = null;
        String query = null;
        try {
            query = "update mDocumentosContrato set fFechaActualiza=GETDATE(),cUsuarioActualiza='" + cLogin + "' where cIdContratoDefinitivo='" + cIdContratoDefinitivo + "' and nIdDocumento=" + nIdDocumento;
            pstmnt = conn.prepareStatement(query);
            resp = pstmnt.executeUpdate() > 0;
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
            query = null;
        }
        return resp;
    }

    public boolean existTerminacionAnti(Connection conn, String cIdContratoDefinitivo) throws SQLException {
        String query = "";
        boolean resp = false;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            query = "select *from mContratoTerminacionAnticipada with(nolock) where cIdContratoDefinitivo='" + cIdContratoDefinitivo + "' ";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
        return resp;
    }

    public boolean contratoAut(Connection conn, String cIdContratoDefinitivo) throws SQLException {
        String query = "";
        boolean resp = false;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            query = "select cIdContratoDefinitivo from mContrato with(nolock) where cIdContratoDefinitivo='" + cIdContratoDefinitivo + "' and nidEstado=4 " + " union select cIdContratoDefinitivo from mPlurianualidadContrato with(Nolock) where nIdEstado=4 and cIdContratoDefinitivo='" + cIdContratoDefinitivo + "'";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
        return resp;
    }

    public boolean insertTerminacionAnti(Connection conn, DatosArchivo datos, String cLogin) throws SQLException {
        boolean resp = false;
        PreparedStatement pstmnt = null;
        String query = null;
        try {
            query = "insert into mContratoTerminacionAnticipada (cIdContratoDefinitivo,nTipoTerminacion,cCausa,fFechaTermino,fFechaCaptura,cLogin,fFechaLimitePagoPendiente, fFechaNotificacionUAF) " + "values(?,?,?,convert(date,?),GETDATE(),?,convert(date,?),convert(date,?)) ";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, datos.getcIdContratoDefinitivo());
            pstmnt.setInt(2, datos.getnTipoTerminacionCont());
            pstmnt.setString(3, datos.getcCausa());
            pstmnt.setString(4, datos.getfFechaTermino());
            pstmnt.setString(5, cLogin);
            if (StringUtils.isBlank(datos.getfFechaLimitePagoPendiente()))
                pstmnt.setNull(6, Types.VARCHAR);
            else
                pstmnt.setString(6, datos.getfFechaLimitePagoPendiente());
            if (StringUtils.isBlank(datos.getFechaNotificacionUAF()))
                pstmnt.setNull(7, Types.VARCHAR);
            else
                pstmnt.setString(7, datos.getFechaNotificacionUAF());
            resp = pstmnt.executeUpdate() > 0;
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
            query = null;
        }
        return resp;
    }

    public boolean updateTerminacionAnti(Connection conn, DatosArchivo datos, String cLogin) throws SQLException {
        boolean resp = false;
        PreparedStatement pstmnt = null;
        String query = null;
        try {
            query = "update mContratoTerminacionAnticipada ";
            query += "       set fFechaActualiza=GETDATE(),cLogin='" + cLogin + "', ";
            query += "       	 nTipoTerminacion=" + datos.getnTipoTerminacionCont() + ", ";
            query += "           cCausa=?, ";
            query += "           fFechaTermino=convert(date,?) ";
            query += " where cIdContratoDefinitivo='" + datos.getcIdContratoDefinitivo() + "'";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, datos.getcCausa());
            pstmnt.setString(2, datos.getfFechaTermino());
            resp = pstmnt.executeUpdate() > 0;
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
            query = null;
        }
        return resp;
    }

    public List<String[]> readFileCSV(InputStream in) throws Exception {
        boolean primeraLinea = true;
        List<String[]> lista = new ArrayList<String[]>();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String renglon = "";
        try {
            while ((renglon = br.readLine()) != null) {
                String[] llaves = renglon.split(",");
                if (primeraLinea) {
                    primeraLinea = !primeraLinea;
                } else {
                    if (llaves[1].equalsIgnoreCase("RHQ") && "WF_SUFI_PROCURA_1".equalsIgnoreCase(llaves[25]))
                        lista.add(llaves);
                }
            }
        } finally {
            br.close();
        }
        return lista;
    }

    public List<String> obtienDatosLayout(Connection conn, String cFolioIntegrada) throws SQLException, JSONException {
        String query = "";
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<String> lista = null;
        try {
            query = "select \r\n" + "integra.nIdIntegraRequi\r\n" + ",tipoI.cTipoIntegracion+'-'+integra.cUnidadEjecutora+'-'+convert(varchar,integra.nConsecutivo) integrada\r\n" + ",layout.nIdLayoutRequi\r\n" + "from mLayoutRequis as layout with(Nolock)\r\n" + "inner join mIntegraRequis as integra with(Nolock)\r\n" + "on integra.nIdIntegraRequi=layout.nIdIntegraRequi\r\n" + "inner join mCatalogoTipoIntegracion tipoI with(Nolock)\r\n" + "on integra.nIdTipoIntegracion=tipoI.nIdTipoIntegracion\r\n" + "where layout.nEnviadoSICOP=1\r\n" + "and integra.nEstatus=2 and integra.nEnviadoSICOP=1\r\n" + "and tipoI.cTipoIntegracion+'-'+integra.cUnidadEjecutora+'-'+convert(varchar,integra.nConsecutivo)='" + cFolioIntegrada + "'";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                lista = new ArrayList<String>();
                lista.add(rs.getString("nIdIntegraRequi"));
                lista.add(rs.getString("integrada"));
                lista.add(rs.getString("nIdLayoutRequi"));
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
        return lista;
    }

    public JSONArray obtieneRequisIntegradas(Connection conn, int nIdIntegraRequi) throws SQLException, JSONException {
        String query = "";
        ResultSet rs = null;
        PreparedStatement ps = null;
        JSONObject object = null;
        JSONArray arrayObj = new JSONArray();
        try {
            query = "select \r\n" + "	integradas.cIdSolicitud\r\n" + "	,sol.C_FOLIO_APA,nFolioApartado,sol.cEjercicio\r\n" + "	from mRequisIntegradas integradas with(Nolock) \r\n" + "	inner join mSolicitud as sol with(Nolock)\r\n" + "	on sol.cIdSolicitud=integradas.cIdSolicitud\r\n" + "	where nIdIntegraRequi=?";
            ps = conn.prepareStatement(query);
            ps.setInt(1, nIdIntegraRequi);
            rs = ps.executeQuery();
            while (rs.next()) {
                object = new JSONObject();
                object.put("cIdSolicitud", rs.getString("cIdSolicitud"));
                object.put("cFolioCASO", rs.getString("C_FOLIO_APA"));
                object.put("nFolioApartado", rs.getInt("nFolioApartado"));
                object.put("cEjercicio", rs.getString("cEjercicio"));
                arrayObj.put(object);
                object = null;
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
            object = null;
        }
        return arrayObj;
    }

    public boolean updateStatusLayout(Connection conn, String cFecha, String cFolioSICOP, int nIdLayoutRequi) throws SQLException {
        boolean resp = false;
        PreparedStatement pstmnt = null;
        String query = null;
        try {
            query = "update mLayoutRequis set fFechaAutorizado=convert(date,?),cFolioSICOP=?,nEnviadoSICOP=2 where nIdLayoutRequi=?";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, cFecha);
            pstmnt.setString(2, cFolioSICOP);
            pstmnt.setInt(3, nIdLayoutRequi);
            resp = pstmnt.executeUpdate() > 0;
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
            query = null;
        }
        return resp;
    }

    public boolean updateStatusIntegrada(Connection conn, String cFolioSICOP, int nIdIntegraRequi) throws SQLException {
        boolean resp = false;
        PreparedStatement pstmnt = null;
        String query = null;
        try {
            query = "update mIntegraRequis set nEnviadoSICOP=2,cFolioSICOP=? where nIdIntegraRequi=?";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, cFolioSICOP);
            pstmnt.setInt(2, nIdIntegraRequi);
            resp = pstmnt.executeUpdate() > 0;
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
            query = null;
        }
        return resp;
    }

    public boolean updateStatusRequisicion(Connection conn, String cIdSolicitud, String cEjercicio) throws SQLException {
        boolean resp = false;
        PreparedStatement pstmnt = null;
        String query = null;
        try {
            query = "UPDATE msolicitud SET nIdEstado = 3 , nIdEstadoPrecomprometido=3  WHERE cIdSolicitud = ?  and cEjercicio=?";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, cIdSolicitud);
            pstmnt.setString(2, cEjercicio);
            resp = pstmnt.executeUpdate() > 0;
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
            query = null;
        }
        return resp;
    }

    public int saveBitacoraRetonoLayout(Connection conn, int nIdLayoutRequi, int nIdIntegraRequi, String cIdSolicitud, String cObservaciones, String cLogin) throws SQLException {
        int resp = 0;
        PreparedStatement pstmnt = null;
        String query = null;
        try {
            query = "insert into mBitacoraRetornoLayout (nIdLayoutRequi,nIdIntegraRequi,cIdSolicitud,cObservaciones,fFechaBitacora,cLogin) values(?,?,?,?,GETDATE(),?)";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setInt(1, nIdLayoutRequi);
            pstmnt.setInt(2, nIdIntegraRequi);
            pstmnt.setString(3, cIdSolicitud);
            pstmnt.setString(4, cObservaciones);
            pstmnt.setString(5, cLogin);
            resp = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
        }
        return resp;
    }

    public Requisition obtieneRequi(Connection conn, String cIdSolicitud) throws SQLException, JSONException {
        StringBuilder query = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Requisition requi = null;
        try {
            query = new StringBuilder();
            query.append(" select *from mSolicitud sol with(Nolock)  ");
            query.append(" inner join mCatalogoTipoSolicitud tipoSol with(Nolock) ");
            query.append(" on tipoSol.cIdTipoSolicitud=sol.cIdTipoSolicitud ");
            query.append(" inner join mCatalogoTipoConsolidado tipoCon with(Nolock) ");
            query.append(" on tipoCon.nTipoConsolidado=tipoSol.nTipoSolicitud ");
            query.append(" inner join tApartadoEncabezado apartado with(Nolock) ");
            query.append(" on apartado.cIdSolicitud=sol.cIdSolicitud and apartado.cDocumentoHaplicado='S' ");
            query.append(" where sol.cIdSolicitud=? ");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cIdSolicitud);
            log.info("Object: {}", query.toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                requi = new Requisition();
                requi.setCuentaDisp(rs.getString("cNumCuentaDisp"));
                requi.setIdSolicitud(rs.getString("cIdSolicitud"));
                requi.setIdEstado(rs.getInt("nIdEstado"));
                requi.setEjercicio(rs.getString("cEjercicio"));
                requi.setIdTipoSolicitud(rs.getString("cIdTipoSolicitud"));
                requi.setIdUnidadEjecutora(rs.getString("cIdUnidadEjecutora"));
                requi.setIdConsecutivo(rs.getInt("nIdConsecutivo"));
                requi.setIdAlcance(rs.getInt("nIdAlcance"));
                requi.setTipoConsolidado(rs.getString("cIdTipoConsolidado"));
                requi.setIdEntidadContable(rs.getString("cCentroContable"));
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
        }
        return requi;
    }

    public boolean areaRespPerteneceAreaRequirente(Connection conn, String aAreaReq, String cAreaResp) throws SQLException {
        StringBuilder query = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        ConfiguraAplicativoBusinessLogic configApp = null;
        String cEjercicio = "2021";
        String desa = "";
        boolean resp = false;
        try {
            configApp = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
            boolean esSAIAlterno = "true".equalsIgnoreCase(configApp.getSystemSetting("AMBIENTE_DESARROLLO"));
            if (esSAIAlterno) {
                desa = "_desa";
            }
            cEjercicio = Util.obtieneEjercicioFiscalActivo(conn);
            query = new StringBuilder();
            query.append("SELECT * ");
            query.append("FROM nomina_");
            query.append(cEjercicio);
            query.append(desa);
            query.append(".dbo.nom_Unidad_Ejecutora WITH(nOLOCK) WHERE c_coordinacion=? and cUejecutora=? ");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, aAreaReq);
            ps.setString(2, cAreaResp);
            log.info("Object: {}", query.toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
            }
            return resp;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
            configApp = null;
            cEjercicio = null;
            desa = null;
        }
    }

    public JSONObject obtieneConsolidado(Connection conn, String cIdSolicitud) throws SQLException, JSONException {
        StringBuilder query = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        JSONObject object = null;
        try {
            query = new StringBuilder();
            query.append(" Select c.cIdConsolidado,c.nIdEstado FROM mConsolidadoPreseleccionSolicitudes cps with(nolock) ");
            query.append(" INNER JOIN mConsolidado c with(nolock) ON cps.cEjercicio = c.cEjercicio ");
            query.append(" AND cps.cIdTipoConsolidado = c.cIdTipoConsolidado ");
            query.append(" AND cps.cIdUnidadEjecutora = c.cIdUnidadEjecutora ");
            query.append(" AND cps.nIdConsecutivo = c.nIdConsecutivo ");
            query.append(" WHERE nIdEstado IN(1,2) and cps.cIdSolicitud=? ");
            query.append(" group by c.cIdConsolidado,c.nIdEstado ");
            query.append(" union ");
            query.append(" select c.cIdConsolidado,c.nIdEstado FROM mConsolidadoSolicitud cps with(nolock) ");
            query.append(" INNER JOIN mConsolidado c with(nolock) ON cps.cEjercicio = c.cEjercicio ");
            query.append(" AND cps.cIdTipoConsolidado = c.cIdTipoConsolidado ");
            query.append(" AND cps.cIdUnidadEjecutora = c.cIdUnidadEjecutora ");
            query.append(" AND cps.nIdConsecutivo = c.nIdConsecutivo ");
            query.append(" WHERE nIdEstado IN(1,2) and cps.cIdSolicitud=? ");
            query.append(" group by c.cIdConsolidado,c.nIdEstado ");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cIdSolicitud);
            ps.setString(2, cIdSolicitud);
            log.info("Object: {}", query.toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                object = new JSONObject();
                object.put("cIdConsolidado", rs.getString("cIdConsolidado"));
                object.put("nIdEstado", rs.getInt("nIdEstado"));
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
        }
        return object;
    }

    public boolean apartadoSolicitud(Connection conn, String cIdSolicitud) throws SQLException, JSONException {
        StringBuilder query = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        boolean resp = false;
        try {
            query = new StringBuilder();
            query.append(" select cDocumentoHaplicado as documentoAplicado from tApartadoEncabezado WITH(NOLOCK) where cIdSolicitud=rtrim(?) and cDocumentoHaplicado='S' ");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cIdSolicitud);
            log.info("Object: {}", query.toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
        }
        return resp;
    }

    public int generateConsolidado(Connection conn, Requisition requi) throws SQLException, JSONException {
        CallableStatement cmst = null;
        int outputValue = -1;
        try {
            cmst = conn.prepareCall("{call pa_mConsolidadoAutomaticoPrecompromiso (?,?,?,?,?,?,?,?,?,?,?,?)}");
            cmst.setString(1, requi.getEjercicio());
            cmst.setString(2, requi.getTipoConsolidado());
            cmst.setString(3, requi.getIdUnidadEjecutora());
            cmst.setString(4, requi.getDescripcion());
            cmst.setInt(5, requi.getIdAlcance());
            //usuario
            cmst.setString(6, requi.getIdUsuarioCreacion());
            cmst.setString(7, requi.getIdTipoSolicitud());
            cmst.setString(8, requi.getIdUnidadEjecutora());
            cmst.setInt(9, requi.getIdConsecutivo());
            cmst.setString(10, "");
            cmst.registerOutParameter(11, Types.INTEGER);
            cmst.registerOutParameter(12, Types.VARCHAR);
            cmst.execute();
            outputValue = cmst.getInt(11);
            requi.setcIdConsolidado(cmst.getString(12));
        } finally {
            CloseObject.closeObject(cmst);
        }
        return outputValue;
    }

    public void readLayoutPresupuestoRequi(Connection conn, DatosArchivo datArchivo) throws Exception {
        XSSFWorkbook workbook = null;
        Row row = null;
        Cell cell = null;
        JSONObject jsonObj = null;
        StringBuilder errores = new StringBuilder();
        String[] encabezado = { "Ejercicio Fiscal", "Unidad Ejecutora", "Número de Requisición", "Clave SIAF o MAP", "Clave Interna", "Línea de la Requisición", "CUCOP", "Partida Presupuestal", "Descripción", "Descripción Adicional", "mes01", "mes02", "mes03", "mes04", "mes05", "mes06", "mes07", "mes08", "mes09", "mes10", "mes11", "mes12", "Costo Total por Línea" };
        XSSFSheet firstSheet = null;
        XSSFTable table = null;
        try {
            workbook = new XSSFWorkbook(datArchivo.getArchivoStream());
            firstSheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = firstSheet.rowIterator();
            int initRow = 6, k = 0;
            boolean insertar = true;
            table = firstSheet.getTables().get(0);
            int cantRows = table.getRowCount();
            int j = 0;
            while (rowIterator.hasNext()) {
                k++;
                j = 0;
                row = rowIterator.next();
                if (k < initRow)
                    continue;
                if (k >= (initRow + cantRows - 1))
                    break;
                jsonObj = new JSONObject();
                insertar = true;
                //System.out.println( "Renglon "+(k) );
                for (int c = row.getFirstCellNum(); c < 24; c++) {
                    cell = row.getCell(c);
                    if (cell == null) {
                        insertar = false;
                        break;
                    } else {
                        if ((c > 1 && c < 6) || (c > 9 && c < 22)) {
                            if (validateCell(cell)) {
                                insertar = false;
                                errores.append("Renglon " + (k) + " columna " + encabezado[c] + " dato no permitido.\n");
                            } else {
                                fillObject(cell, "" + j, jsonObj);
                            }
                            j++;
                        }
                    }
                }
                if (insertar) {
                    insertLayoutPresupuestoRequi(conn, jsonObj);
                }
                row = null;
                jsonObj = null;
            }
            if (errores.length() > 0) {
                throw new Exception(errores.toString());
            }
        } finally {
            if (workbook != null) {
                workbook.close();
            }
            workbook = null;
            table = null;
            firstSheet = null;
            jsonObj = null;
        }
    }

    public boolean insertLayoutPresupuestoRequi(Connection conn, JSONObject jsonObj) throws SQLException, JSONException {
        boolean resp = false;
        PreparedStatement pstmnt = null;
        String query = null;
        try {
            query = "insert into mLayoutPresupuestoRequi (cidSolicitud,cClaveEgresos,cClaveInterna,nIdLineaSol" + " ,mes01,mes02,mes03,mes04,mes05,mes06,mes07,mes08,mes09,mes10,mes11,mes12) " + " values(rtrim(ltrim(?)),rtrim(ltrim(?)),rtrim(ltrim(?)),?, ?,?,?,?, ?,?,?,?, ?,?,?,?)";
            pstmnt = conn.prepareStatement(query);
            for (int i = 0; i < jsonObj.length(); i++) {
                if (i == 3) {
                    pstmnt.setInt(i + 1, jsonObj.getInt("" + i));
                } else if (i > 3) {
                    pstmnt.setDouble(i + 1, jsonObj.getDouble("" + i));
                } else {
                    pstmnt.setString(i + 1, jsonObj.getString("" + i));
                }
            }
            resp = pstmnt.execute();
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
            query = null;
        }
        return resp;
    }

    public void validaPresupuestoCalendarizadoRequi(Connection conn, String cIdSolicitud) throws SQLException, Exception {
        StringBuilder query = null;
        StringBuilder msg = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            query = new StringBuilder();
            query.append(" select   nIdLineaSolicitud   ");
            query.append(" 		,case  ");
            query.append(" 			when (totalLineaRequi<(mes01+mes02+mes03+mes04+mes05+mes06+mes07+mes08+mes09+mes10+mes11+mes12)   ) ");
            query.append(" 				then 'Se calendarizo mas recurso del necesario en la línea '+convert(varchar,nIdLineaSolicitud)+' valor de la linea = '+ CONVERT(VARCHAR,totalLineaRequi)   ");
            query.append(" 				+' presupuesto calendarizado ='+ CONVERT(VARCHAR,(mes01+mes02+mes03+mes04+mes05+mes06+mes07+mes08+mes09+mes10+mes11+mes12)) ");
            query.append(" 			when (totalLineaRequi>(mes01+mes02+mes03+mes04+mes05+mes06+mes07+mes08+mes09+mes10+mes11+mes12) and capitulo<>1  ) ");
            query.append(" 				then 'Falta '+convert(varchar,(totalLineaRequi-(mes01+mes02+mes03+mes04+mes05+mes06+mes07+mes08+mes09+mes10+mes11+mes12)),103) ");
            query.append(" 				+' de presupuesto por calendarizar en la línea '+convert(varchar,nIdLineaSolicitud) ");
            query.append(" 			else '' ");
            query.append(" 		end validacion   ");
            query.append(" from	  	 ");
            query.append(" 	(select   	lineas.cIdSolicitud,lineas.nIdLineaSolicitud,lineas.mMontoNeto totalLineaRequi, SUBSTRING( lineas.cIdSubPartida,1,1)capitulo ");
            query.append(" 		,sum(layout.mes01)mes01,sum(layout.mes02)mes02,sum(layout.mes03)mes03  	 ");
            query.append(" 		,sum(layout.mes04)mes04,sum(layout.mes05)mes05,sum(layout.mes06)mes06  	 ");
            query.append(" 		,sum(layout.mes07)mes07,sum(layout.mes08)mes08,sum(layout.mes09)mes09  	 ");
            query.append(" 		,sum(layout.mes10)mes10,sum(layout.mes11)mes11,sum(layout.mes12)mes12  	 ");
            query.append(" 	from mLayoutPresupuestoRequi layout with(Nolock)  	 ");
            query.append(" 	inner join mSolicitudLineas lineas with(Nolock)  	on lineas.cIdSolicitud=layout.cidSolicitud  	 ");
            query.append(" 	and lineas.nIdLineaSolicitud=layout.nIdLineaSol  	 ");
            query.append(" 	where layout.cidSolicitud=? 	 ");
            query.append(" 	group by lineas.cIdSolicitud,lineas.nIdLineaSolicitud,lineas.mMontoNeto ");
            query.append(" 	,SUBSTRING( lineas.cIdSubPartida,1,1) ");
            query.append(" 	having (lineas.mMontoNeto!=(sum(layout.mes01)+sum(layout.mes02)+sum(layout.mes03)+sum(layout.mes04)+sum(layout.mes05)+sum(layout.mes06) +sum(layout.mes07)+sum(layout.mes08)+sum(layout.mes09)+sum(layout.mes10)+sum(layout.mes11)+sum(layout.mes12)) ) ");
            query.append(" 	)sub  order by nIdLineaSolicitud ");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cIdSolicitud);
            log.info("Object: {}", query.toString());
            rs = ps.executeQuery();
            msg = new StringBuilder();
            while (rs.next()) {
                if (rs.getString("validacion").length() > 0)
                    msg.append(rs.getString("validacion") + "\n");
            }
            if (msg.length() > 0) {
                throw new Exception(msg.toString());
            }
        } finally {
            if (query != null && query.length() > 0) {
                query.delete(0, query.length());
            }
            query = null;
            rs = null;
            ps = null;
        }
    }

    public void validaPresupuestoDisponibleRequi(Connection conn, String cIdSolicitud, int nNumCuentaDisp) throws SQLException, Exception {
        StringBuilder query = null;
        StringBuilder msg = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        ResultSetMetaData rsMetadata = null;
        try {
            query = new StringBuilder();
            query.append("select *from fn_mValidaPresupuestoDisponibleRequi(?,?)");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cIdSolicitud);
            ps.setInt(2, nNumCuentaDisp);
            log.info("Object: {}", query.toString());
            rs = ps.executeQuery();
            msg = new StringBuilder();
            rsMetadata = rs.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            while (rs.next()) {
                for (int i = 3; i <= totalcolumnas; i++) {
                    if (!"0".equals(rs.getString(i))) {
                        msg.append(rs.getString(i) + "\n");
                    }
                }
            }
            if (msg.length() > 0) {
                throw new Exception(msg.toString());
            }
        } finally {
            if (query != null && query.length() > 0) {
                query.delete(0, query.length());
            }
            query = null;
            rs = null;
            ps = null;
            rsMetadata = null;
        }
    }

    public void validaUEAndPArtidaRequi(Connection conn, String cIdSolicitud) throws SQLException, Exception {
        StringBuilder query = null;
        StringBuilder msg = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            query = new StringBuilder();
            query.append(" 	select   ");
            query.append(" 	case  ");
            query.append(" 		when sol.cIdUnidadEjecutora<>substring(layout.cClaveInterna,1,3) and SUBSTRING( layout.cClaveEgresos,32,1)<>'1' then 'No puedes agregar la clave interna '+layout.cClaveInterna  ");
            query.append(" 		+', ya que no empieza con la ue de la requisición '+sol.cIdUnidadEjecutora ");
            query.append(" 		when sol.cIdSubPartida<>SUBSTRING( layout.cClaveEgresos,32,5) then  'No puedes agregar la clave de egresos '+layout.cClaveEgresos+' porque trae la partida presupuestal '  ");
            query.append(" 	+SUBSTRING( layout.cClaveEgresos,32,5) +' y es diferente a la partida '+sol.cIdSubPartida+' de la requisición ' ");
            query.append(" 	else'' end validaUEAndPartida  ");
            query.append(" from mSolicitudLineas sol with(Nolock)  ");
            query.append(" inner join mLayoutPresupuestoRequi as layout with(Nolock)  ");
            query.append(" on layout.cidSolicitud=sol.cIdSolicitud  ");
            query.append(" and layout.nIdLineaSol=sol.nIdLineaSolicitud  ");
            query.append(" where sol.cIdSolicitud=?   ");
            query.append(" and (sol.cIdUnidadEjecutora<>substring(layout.cClaveInterna,1,3) or sol.cIdSubPartida<>SUBSTRING( layout.cClaveEgresos,32,5) ) ");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cIdSolicitud);
            log.info("Object: {}", query.toString());
            rs = ps.executeQuery();
            msg = new StringBuilder();
            while (rs.next()) {
                if (rs.getString("validaUEAndPartida").length() > 0)
                    msg.append(rs.getString("validaUEAndPartida"));
            }
            if (msg.length() > 0) {
                throw new Exception(msg.toString());
            }
        } finally {
            if (query != null && query.length() > 0) {
                query.delete(0, query.length());
            }
            query = null;
            rs = null;
            ps = null;
        }
    }

    public boolean insertPresupuestoRequi(Connection conn, String cIdSolicitud) throws SQLException, JSONException {
        boolean resp = false;
        PreparedStatement pstmnt = null;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" insert into mSolicitudLineasApartado (cEjercicio,cIdUnidadEjecutora ");
            query.append(" ,cIdSolicitud,nIdClaveEgresos,ClaveInterna,nIdLineaSolicitud,cIdCABM ");
            query.append(" ,cIdSubPartida,cDescripcion,mes01,mes02,mes03,mes04,mes05,mes06 ");
            query.append(" ,mes07,mes08,mes09,mes10,mes11,mes12) ");
            query.append(" select  ");
            query.append(" 	sol.cEjercicio,sol.cIdUnidadEjecutora,sol.cIdSolicitud ");
            query.append(" 	,layout.cClaveEgresos,layout.cClaveInterna ");
            query.append(" 	,sol.nIdLineaSolicitud,sol.cIdCABM,sol.cIdSubPartida ");
            query.append(" 	,sol.cDescripcion,layout.mes01,layout.mes02,layout.mes03 ");
            query.append(" 	,layout.mes04,layout.mes05,layout.mes06,layout.mes07 ");
            query.append(" 	,layout.mes08,layout.mes09,layout.mes10,layout.mes11 ");
            query.append(" 	,layout.mes12 ");
            query.append(" from mSolicitudLineas sol with(Nolock) ");
            query.append(" inner join mLayoutPresupuestoRequi as layout with(Nolock) ");
            query.append(" on layout.cidSolicitud=sol.cIdSolicitud ");
            query.append(" and layout.nIdLineaSol=sol.nIdLineaSolicitud ");
            query.append(" where sol.cIdSolicitud=? ");
            pstmnt = conn.prepareStatement(query.toString());
            pstmnt.setString(1, cIdSolicitud);
            resp = pstmnt.execute();
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            if (query != null && query.length() > 0) {
                query.delete(0, query.length());
            }
            query = null;
            pstmnt = null;
        }
        return resp;
    }

    public int deletePresupuestoRequi(Connection conn, String cIdSolicitud) throws SQLException {
        int resp = 0;
        PreparedStatement pstmnt = null;
        String query = null;
        try {
            query = "delete from mSolicitudLineasApartado where cIdSolicitud=?";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, cIdSolicitud);
            resp = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
        }
        return resp;
    }

    public int deleteLayoutRequi(Connection conn, String cIdSolicitud) throws SQLException {
        int resp = 0;
        PreparedStatement pstmnt = null;
        String query = null;
        try {
            query = "delete from mLayoutPresupuestoRequi where cidSolicitud=?";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, cIdSolicitud);
            resp = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
        }
        return resp;
    }

    public void validaEstatusRequi(Connection conn, String cIdSolicitud) throws SQLException, Exception {
        StringBuilder query = null;
        StringBuilder msg = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            query = new StringBuilder();
            query.append(" select *from mSolicitud with(Nolock) where cIdSolicitud=? and nIdEstado<>1  ");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cIdSolicitud);
            log.info("Object: {}", query.toString());
            rs = ps.executeQuery();
            msg = new StringBuilder();
            if (rs.next()) {
                msg.append("Para la carga de layout solo es cuando la requisición está en estatus de captura\n");
            }
            if (msg.length() > 0) {
                throw new Exception(msg.toString());
            }
        } finally {
            if (query != null && query.length() > 0) {
                query.delete(0, query.length());
            }
            query = null;
            rs = null;
            ps = null;
        }
    }
}
