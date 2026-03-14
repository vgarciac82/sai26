package com.syc.ejercido.pagado;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import com.syc.cfdi.utils.CloseObject;
import com.syc.fortimax.core.Carpeta;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.fortimax.core.OrgCarpeta;
import com.syc.fortimax.core.Pagina;
import com.syc.fortimax.core.PaginaManager;
import com.syc.fortimax.core.Volumen;
import com.syc.gestion.util.Util;
import com.syc.sai.procesosAutomaticos.AttachResult;
import com.syc.sai.procesosAutomaticos.core.ProcesoAdjunta;
import com.syc.utils.TiffFilenameFilter;
import com.syc.utils.pdf.PDF;
import com.syc.utils.zip.ZipManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CLCAttachmentManager {

    private static final Logger log = LoggerFactory.getLogger(CLCAttachmentManager.class);

    private static Object sync = new Object();

    /*
	 * 3 - 600 - 999
	 */
    public static int VOL_MAX_DIR = 999;

    /*
	 * 5 - 3072
	 */
    public static int VOL_MAX_ARCH = 3072;

    private static String administraVolumen(Volumen vol) throws Exception {
        File file = new File(vol.getUnidad() + vol.getRutaBase() + vol.getRutaDirectorio() + vol.getVolumen());
        String[] lista = file.list(new TiffFilenameFilter());
        int totFiles = (lista != null) ? lista.length : 0;
        String volPrefix = vol.getVolumen().substring(0, 3);
        String volumen = vol.getVolumen().substring(3);
        if (totFiles >= VOL_MAX_ARCH)
            return (volPrefix + nextVolumenSequence(volumen));
        return vol.getVolumen();
    }

    public static int attachCLC(Connection conn, boolean ignorarExistentes, String clcFilePath, CallableStatement csBuscaCLC, CallableStatement csBuscaGabinete, PreparedStatement psInsertFolderHierarchy, PreparedStatement psInsertFolder, PreparedStatement psNexIDFolder, PreparedStatement psSearchFolder, PreparedStatement psSearchVolumenUnit, PreparedStatement psSearchVolumen, PreparedStatement psSearchVolPath, ProcesoAdjunta pa) throws Exception {
        int procesados = 0;
        ResultSet rsGabinetes = null;
        ResultSet rsCLCs = null;
        try {
            int clc = 0;
            if (clcFilePath.toUpperCase().indexOf("CLC") >= 0) {
                clc = Integer.parseInt(clcFilePath.substring(clcFilePath.lastIndexOf("_") + 1, clcFilePath.lastIndexOf(".")), 10);
                log.info("Object: {}", "Adjuntando archivo [" + clcFilePath + "] a la CLC " + clc);
                csBuscaCLC.setInt(1, clc);
                rsCLCs = csBuscaCLC.executeQuery();
                while (rsCLCs.next()) {
                    String tipoPago = rsCLCs.getString("cTipoPago");
                    if ("FEDERALIZADO".equals(tipoPago))
                        tipoPago = "pago" + tipoPago;
                    String nFolioPago = rsCLCs.getString("nFolioPAGO");
                    if (StringUtils.isEmpty(nFolioPago)) {
                        CLCAttachmentLogManager.insertLogCLC(conn, new File(clcFilePath).getName(), 0, "", -1, tipoPago, '0', "No se encontro pago para la CLC: " + clc, "CLC", pa.getIdProceso());
                        continue;
                    }
                    csBuscaGabinete.setString(1, tipoPago);
                    csBuscaGabinete.setInt(2, Integer.parseInt(nFolioPago, 10));
                    rsGabinetes = csBuscaGabinete.executeQuery();
                    if (rsGabinetes.next()) {
                        try {
                            int idGabinete = rsGabinetes.getInt(1);
                            if ((ignorarExistentes && !DocumentoManager.existeDocumento(conn, tipoPago, idGabinete, new File(clcFilePath).getName())) || !ignorarExistentes) {
                                System.out.println(" Adjuntando archivo al pago " + tipoPago + " con folio " + nFolioPago);
                                CLCAttachmentManager.attchToExpedient(psInsertFolderHierarchy, psInsertFolder, psNexIDFolder, psSearchFolder, tipoPago, idGabinete, clcFilePath, psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, conn, "CLC");
                                CLCAttachmentLogManager.insertLogCLC(conn, new File(clcFilePath).getName(), 0, "", Integer.parseInt(nFolioPago, 10), tipoPago, '9', "Se adjunto exitosamente el archivo", "CLC", pa.getIdProceso());
                            } else {
                                log.info("Object: {}", "El archivo [" + clcFilePath + "] ya existe. Se ignora");
                                CLCAttachmentLogManager.insertLogCLC(conn, new File(clcFilePath).getName(), 0, "", Integer.parseInt(nFolioPago, 10), tipoPago, '3', " El archivo ya existe. Se ignora.", "CLC", pa.getIdProceso());
                            }
                        } catch (Exception e1) {
                            try {
                                CLCAttachmentLogManager.insertLogCLC(conn, new File(clcFilePath).getName(), 0, "", Integer.parseInt(nFolioPago, 10), tipoPago, '2', "Ocurrio el siguiente error al adjuntar: " + e1, "CLC", pa.getIdProceso());
                            } catch (Exception e2) {
                                log.error(e2.getMessage(), e2);
                            }
                            throw e1;
                        }
                    } else {
                        CLCAttachmentLogManager.insertLogCLC(conn, new File(clcFilePath).getName(), 0, "", Integer.parseInt(nFolioPago, 10), tipoPago, '1', "No se encontro gabinete en  IMX" + tipoPago + " con el folio [" + nFolioPago + "]", "CLC", pa.getIdProceso());
                    }
                }
            } else {
                log.info("Object: {}", "El archivo " + clcFilePath + " no esta bien formado. Debe nombrarse [CLC_###.pdf] Donde ### es el numero de CLC");
                CLCAttachmentLogManager.insertLogCLC(conn, new File(clcFilePath).getName(), 0, "", -1, "", '4', "El archivo " + clcFilePath + " no esta bien formado. Debe nombrarse [CLC_###.pdf] Donde ### es el numero de CLC", "CLC", pa.getIdProceso());
            }
            return procesados;
        } catch (Exception e) {
            CLCAttachmentLogManager.insertLogCLC(conn, new File(clcFilePath).getName(), 0, "", -1, "", '2', "Ocurrio un error al adjuntar el archivo: " + clcFilePath + e, "CLC", pa.getIdProceso());
            throw e;
        } finally {
            CloseObject.closeObject(rsGabinetes, false);
            CloseObject.closeObject(rsCLCs, false);
        }
    }

    public static int attachFactura(Connection conn, boolean ignorarExistentes, String clcFilePath, CallableStatement csBuscaGabinete, PreparedStatement psInsertFolderHierarchy, PreparedStatement psInsertFolder, PreparedStatement psNexIDFolder, PreparedStatement psSearchFolder, PreparedStatement psSearchVolumenUnit, PreparedStatement psSearchVolumen, PreparedStatement psSearchVolPath, ProcesoAdjunta pa) throws Exception {
        int procesados = 0;
        ResultSet rsGabinetes = null;
        ResultSet rsCLCs = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            String nombre = leerNombreArchivo(clcFilePath);
            String numBoleto = "";
            pst = conn.prepareStatement("SELECT * FROM vBoletosAvion WITH (NOLOCK) WHERE cNumeroBoleto = ?");
            pst.setString(1, nombre);
            rs = pst.executeQuery();
            if (rs.next()) {
                String tipoPago = rs.getString("ctipoPago");
                String nFolioPago = rs.getString("nFolioRelacionGastos");
                numBoleto = rs.getString("cNumeroBoleto");
                if (StringUtils.isEmpty(nFolioPago)) {
                    CLCAttachmentLogManager.insertLogCLC(conn, new File(clcFilePath).getName(), 0, "", -1, tipoPago, '0', "No se encontro pago para el numero de boleto: " + numBoleto, "Factura", pa.getIdProceso());
                }
                csBuscaGabinete.setString(1, tipoPago);
                csBuscaGabinete.setInt(2, Integer.parseInt(nFolioPago, 10));
                rsGabinetes = csBuscaGabinete.executeQuery();
                if (rsGabinetes.next()) {
                    try {
                        int idGabinete = rsGabinetes.getInt(1);
                        if ((ignorarExistentes && !DocumentoManager.existeDocumento(conn, tipoPago, idGabinete, new File(clcFilePath).getName())) || !ignorarExistentes) {
                            System.out.println(" Adjuntando archivo al pago " + tipoPago + " con folio " + nFolioPago);
                            CLCAttachmentManager.attchToExpedient(psInsertFolderHierarchy, psInsertFolder, psNexIDFolder, psSearchFolder, tipoPago, idGabinete, clcFilePath, psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, conn, "Otros");
                            CLCAttachmentLogManager.insertLogCLC(conn, new File(clcFilePath).getName(), 0, "", Integer.parseInt(nFolioPago, 10), tipoPago, '9', "Se adjunto exitosamente el archivo", "Otros", pa.getIdProceso());
                        } else {
                            log.info("Object: {}", "El archivo [" + clcFilePath + "] ya existe. Se ignora");
                            CLCAttachmentLogManager.insertLogCLC(conn, new File(clcFilePath).getName(), 0, "", Integer.parseInt(nFolioPago, 10), tipoPago, '3', " El archivo ya existe. Se ignora.", "Otros", pa.getIdProceso());
                        }
                    } catch (Exception e1) {
                        try {
                            CLCAttachmentLogManager.insertLogCLC(conn, new File(clcFilePath).getName(), 0, "", Integer.parseInt(nFolioPago, 10), tipoPago, '2', "Ocurrio el siguiente error al adjuntar: " + e1, "Otros", pa.getIdProceso());
                        } catch (Exception e2) {
                            log.error(e2.getMessage(), e2);
                        }
                        throw e1;
                    }
                } else {
                    CLCAttachmentLogManager.insertLogCLC(conn, new File(clcFilePath).getName(), 0, "", Integer.parseInt(nFolioPago, 10), tipoPago, '1', "No se encontro gabinete en  IMX" + tipoPago + " con el folio [" + nFolioPago + "]", "Otros", pa.getIdProceso());
                }
            }
            return procesados;
        } catch (Exception e) {
            CLCAttachmentLogManager.insertLogCLC(conn, new File(clcFilePath).getName(), 0, "", -1, "", '2', "Ocurrio un error al adjuntar el archivo: " + clcFilePath + e, "Otros", pa.getIdProceso());
            throw e;
        } finally {
            CloseObject.closeObject(rsGabinetes, false);
            CloseObject.closeObject(rsCLCs, false);
        }
    }

    public static String leerNombreArchivo(String pdfPath) throws Exception {
        PDF pdf = new PDF();
        String nombre = "";
        try {
            String text = pdf.load(pdfPath).extractText(1, 1);
            String textoABuscar = "B_";
            int intIndex = text.indexOf(textoABuscar);
            int intIndex2 = text.substring(intIndex, intIndex + 25).indexOf("\r\n");
            //intIndex2  = intIndex2 ;
            boolean resultado = text.contains(textoABuscar);
            if (resultado) {
                nombre = text.substring(intIndex + 2, intIndex + intIndex2);
                nombre = nombre.replaceAll("01SERVICIO", "");
            }
            return nombre;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            pdf.close();
        }
    }

    public static int attchToExpedient(PreparedStatement psInsertFolderHierarchy, PreparedStatement psInsertFolder, PreparedStatement psNexIDFolder, PreparedStatement psSearchFolder, String tituloAplicacion, int idGabinete, String clcPathFile, PreparedStatement psSearchVolumenUnit, PreparedStatement psSearchVolumen, PreparedStatement psSearchVolPath, Connection conn, String nombreCarpeta) throws Exception {
        return attchToExpedient(psInsertFolderHierarchy, psInsertFolder, psNexIDFolder, psSearchFolder, tituloAplicacion, idGabinete, clcPathFile, psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, conn, nombreCarpeta, null);
    }

    public static int attchToExpedient(PreparedStatement psInsertFolderHierarchy, PreparedStatement psInsertFolder, PreparedStatement psNexIDFolder, PreparedStatement psSearchFolder, String tituloAplicacion, int idGabinete, String clcPathFile, PreparedStatement psSearchVolumenUnit, PreparedStatement psSearchVolumen, PreparedStatement psSearchVolPath, Connection conn, String nombreCarpeta, String nombreDocumento) throws Exception {
        int insertados = 0;
        Carpeta cfdi = obtenCarpetaDestino(psInsertFolderHierarchy, psInsertFolder, psNexIDFolder, psSearchFolder, tituloAplicacion, idGabinete, nombreCarpeta);
        insertados = insertaArchivosCLC(cfdi, "CLC_CARGA", clcPathFile, conn, psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, nombreDocumento);
        return insertados;
    }

    public static String creaDirectorio(String unidad_disco, String ruta_base, String ruta_directorio, int level) throws SQLException {
        String newRutaDirectorio = ruta_directorio;
        if (ruta_directorio == null)
            throw new SQLException("La ruta_directorio no debe ser nula");
        if (((null + File.separator).equals(ruta_directorio)) && (level > 0))
            throw new SQLException("Se alcanzo el limite maximo de directorios");
        File pathDir = new File(newRutaDirectorio);
        File realDir = new File(unidad_disco + ruta_base + newRutaDirectorio);
        String[] dirList = realDir.list(new FilenameFilter() {

            public boolean accept(File f, String s) {
                return f.isDirectory();
            }
        });
        int totDir = (dirList == null) ? 0 : dirList.length;
        if (totDir >= VOL_MAX_DIR) {
            newRutaDirectorio = creaDirectorio(unidad_disco, ruta_base, pathDir.getParent() + File.separator, level + 1);
        } else {
            if (level > 0) {
                // Se ordena, puede venir en desorden
                Arrays.sort(dirList);
                String[] dirSuffix = { null, null, File.separator + "000", File.separator + "000" + File.separator + "000" };
                String newDirname = null;
                try {
                    newDirname = String.valueOf(Integer.parseInt(realDir.list()[realDir.list().length - 1], 10) + 1);
                } catch (NumberFormatException ne) {
                    newDirname = "";
                }
                newDirname = "000".substring(0, 3 - newDirname.length()) + newDirname;
                File d = new File(realDir.getPath() + File.separator + newDirname + ((dirSuffix[level] != null) ? dirSuffix[level] + File.separator : File.separator));
                // if (!d.mkdirs())
                if (!makeDirectories(d))
                    throw new SQLException("No se logro crear directorio " + d.getPath());
                newRutaDirectorio = pathDir.getPath() + File.separator + newDirname + ((dirSuffix[level] != null) ? dirSuffix[level] + File.separator : File.separator);
            } else if (!realDir.exists()) {
                if (!realDir.mkdirs())
                    throw new SQLException("No se logro crear directorio " + realDir.getPath());
            }
        }
        return newRutaDirectorio;
    }

    public static int extractZipCLC(String zipFilePath, String outputDir, List<File> extractedFiles) throws Exception {
        return ZipManager.extraeArchivos(zipFilePath, outputDir, extractedFiles);
    }

    public static int extractZipCLC(String zipFilePath, String outputDir) throws Exception {
        return ZipManager.extraeArchivos(zipFilePath, outputDir, null);
    }

    private static Carpeta getCarpetaByName(PreparedStatement psSearFolder, String gavetaAsociada, int idGabinete, String nombreCarpeta) throws Exception {
        ResultSet rs = null;
        Carpeta c = null;
        try {
            psSearFolder.setString(1, nombreCarpeta);
            psSearFolder.setInt(2, idGabinete);
            psSearFolder.setString(3, gavetaAsociada);
            rs = psSearFolder.executeQuery();
            if (rs.next()) {
                c = new Carpeta();
                c.setTituloAplicacion(rs.getString("titulo_aplicacion"));
                c.setIdGabinete(rs.getInt("id_gabinete"));
                c.setIdCarpeta(rs.getInt("id_carpeta"));
                c.setNombreCarpeta(rs.getString("nombre_carpeta"));
                c.setNombreUsuario(rs.getString("nombre_usuario"));
                c.setBanderaRaiz(rs.getString("bandera_raiz"));
                c.setFechaCreacion(rs.getTimestamp("fh_creacion"));
                c.setFechaModificacion(rs.getTimestamp("fh_modificacion"));
                c.setNumeroAccesos(rs.getInt("numero_accesos"));
                c.setNumeroCarpetas(rs.getInt("numero_carpetas"));
                c.setNumeroDocumentos(rs.getInt("numero_documentos"));
                c.setDescripcion(rs.getString("descripcion"));
                c.setPassword(rs.getString("password"));
            }
            return c;
        } finally {
            CloseObject.closeObject(rs, false);
        }
    }

    private static synchronized int getNextIdCarpeta(PreparedStatement psNexIDFolder, String titulo_aplicacion, int id_gabinete) throws Exception {
        int retval = -1;
        ResultSet rs = null;
        try {
            psNexIDFolder.setString(1, titulo_aplicacion);
            psNexIDFolder.setInt(2, id_gabinete);
            rs = psNexIDFolder.executeQuery();
            if (rs.next())
                retval = rs.getInt(1) + 1;
            return retval;
        } finally {
            CloseObject.closeObject(rs, false);
        }
    }

    public static Volumen getVolumen(Connection conn, PreparedStatement psSearchVolumenUnit, PreparedStatement psSearchVolumen, PreparedStatement psSearchVolPath) throws Exception {
        Volumen vol = new Volumen(null);
        PreparedStatement stmnt = null, pstmntInsert = null;
        ResultSet rs = null;
        synchronized (sync) {
            try {
                psSearchVolumenUnit.setInt(1, vol.getEstadoUnidad());
                rs = psSearchVolumenUnit.executeQuery();
                if (!rs.next())
                    throw new SQLException("No se encontro unidad activa");
                vol.setUnidad(rs.getString("unidad"));
                vol.setTipoDispositivo(rs.getString("tipo_dispositivo"));
                vol.setRutaBase(rs.getString("ruta_base"));
                psSearchVolumen.setString(1, vol.getUnidad());
                psSearchVolumen.setString(2, vol.getTipoVolumen());
                psSearchVolumen.setString(3, vol.getCapacidad());
                rs = psSearchVolumen.executeQuery();
                if (!rs.next())
                    throw new Exception("No se encontro un volumen activo");
                vol.setVolumen(rs.getString(1));
                psSearchVolPath.setString(1, vol.getVolumen());
                psSearchVolPath.setString(2, vol.getUnidad());
                psSearchVolPath.setString(3, vol.getTipoVolumen());
                rs = psSearchVolPath.executeQuery();
                if (!rs.next())
                    throw new Exception("No se encontro un volumen activo");
                vol.setRutaDirectorio(rs.getString("ruta_directorio"));
                String volumenAnterior = vol.getVolumen();
                vol.setVolumen(administraVolumen(vol));
                // boolean createDirectory = false;
                if (!volumenAnterior.equals(vol.getVolumen())) {
                    stmnt = conn.prepareStatement("UPDATE imx_volumen SET capacidad = '0' WHERE volumen = ? AND unidad_disco = ? AND tipo_volumen = ? AND capacidad = ?");
                    stmnt.setString(1, volumenAnterior);
                    stmnt.setString(2, vol.getUnidad());
                    stmnt.setString(3, vol.getTipoVolumen());
                    stmnt.setString(4, "1");
                    // Si no actualiza se asume que ya fue actualizado por otro
                    // servidor (e. un cluster de servidores de aplicaciones)
                    vol.setRutaDirectorio(creaDirectorio(vol.getUnidad(), vol.getRutaBase(), vol.getRutaDirectorio(), 0));
                    if (stmnt.executeUpdate() != 0) {
                        pstmntInsert = conn.prepareStatement("INSERT INTO imx_volumen (volumen, unidad_disco, ruta_base, ruta_directorio, capacidad, tipo_volumen) VALUES (?, ?, ?, ?, ?, ?)");
                        pstmntInsert.setString(1, vol.getVolumen());
                        pstmntInsert.setString(2, vol.getUnidad());
                        pstmntInsert.setString(3, vol.getRutaBase());
                        pstmntInsert.setString(4, vol.getRutaDirectorio() + vol.getVolumen() + File.separator);
                        pstmntInsert.setString(5, "1");
                        pstmntInsert.setString(6, vol.getTipoVolumen());
                        pstmntInsert.executeUpdate();
                    }
                    /* RMN - Fin correccion */
                    // vol.setRutaDirectorio(creaRutaDirectorio(vol.getUnidad(),
                    // vol.getRutaBase(), vol.getRutaDirectorio(), 0));
                    File v = new File(vol.getUnidad() + vol.getRutaBase() + vol.getRutaDirectorio() + vol.getVolumen());
                    /*
					 * Cuando existen 2 o más filesystems que se sincronizan a
					 * un tiempo determinado, se valida que exista el
					 * directorio, de lo contrario se crea.
					 */
                    try {
                        if (!v.exists())
                            if (!makeDirectories(v))
                                throw new SQLException("No se logro crear directorio " + v.getPath());
                    } catch (Exception exc) {
                        log.error(exc.getMessage(), exc);
                        throw new SQLException(exc);
                    }
                } else {
                    /*
					 * EJRV Para cuando existan 2 o más filesystems que se
					 * sincronizan a un tiempo determinado, se valida que exista
					 * el directorio, de lo contrario se crea.
					 */
                    File v = new File(vol.getUnidad() + vol.getRutaBase() + vol.getRutaDirectorio() + vol.getVolumen());
                    try {
                        if (!v.exists())
                            if (!makeDirectories(v))
                                throw new SQLException("No se logro crear directorio " + v.getPath());
                    } catch (Exception exc) {
                        log.error(exc.getMessage(), exc);
                        throw new SQLException(exc);
                    }
                }
            } finally {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(pstmntInsert, false);
                CloseObject.closeObject(stmnt, false);
            }
            return vol;
        }
    }

    public static int insertaArchivosCLC(Carpeta c, String uLogin, String clcPathFile, Connection conn, PreparedStatement psSearchVolumenUnit, PreparedStatement psSearchVolumen, PreparedStatement psSearchVolPath) throws Exception {
        return insertaArchivosCLC(c, uLogin, clcPathFile, conn, psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, null);
    }

    public static int insertaArchivosCLC(Carpeta c, String uLogin, String clcPathFile, Connection conn, PreparedStatement psSearchVolumenUnit, PreparedStatement psSearchVolumen, PreparedStatement psSearchVolPath, String nombreDocumento) throws Exception {
        log.trace("Iniciando la insercion en expediente de la CLC");
        int total = 0;
        long start = System.currentTimeMillis();
        log.trace("Insertando archivo");
        File origen = new File(clcPathFile);
        log.trace("Object: {}", "Insertando el archivo: " + origen.getName());
        String tmpFile = origen.getName();
        int pos = tmpFile.lastIndexOf('.') + 1;
        String ext = pos != -1 ? tmpFile.substring(pos) : "";
        insertaDocumento(conn, c.getTituloAplicacion(), c.getIdGabinete(), c.getIdCarpeta(), (nombreDocumento == null ? tmpFile : nombreDocumento), ext, "DOC_CARGA", clcPathFile, psSearchVolumenUnit, psSearchVolumen, psSearchVolPath);
        log.trace("Archivo insertado exitosamente");
        total++;
        long stop = System.currentTimeMillis();
        log.trace("Object: {}", "Finalizado insercion en expediene de la factura en [" + ((stop - start) / 1000) + "] s.");
        return total;
    }

    private static Carpeta insertaCarpeta(PreparedStatement psSearchFolder, PreparedStatement psInsertFolder, Carpeta c) throws Exception {
        psInsertFolder.setString(1, c.getTituloAplicacion());
        psInsertFolder.setInt(2, c.getIdGabinete());
        psInsertFolder.setInt(3, c.getIdCarpeta());
        psInsertFolder.setString(4, c.getNombreCarpeta());
        psInsertFolder.setString(5, c.getNombreUsuario());
        psInsertFolder.setString(6, c.getBanderaRaiz());
        psInsertFolder.setString(7, c.getDescripcion());
        psInsertFolder.setString(8, c.getPassword());
        psInsertFolder.executeUpdate();
        return getCarpetaByName(psSearchFolder, c.getTituloAplicacion(), c.getIdGabinete(), c.getNombreCarpeta());
    }

    public static final void insertaDocumento(Connection conn, String tituloAplicacion, int idGabinete, int idCarpeta, String nombreDocumento, String ext, String nombreUsuario, String archivo, PreparedStatement psSearchVolumenUnit, PreparedStatement psSearchVolumen, PreparedStatement psSearchVolPath) throws Exception {
        insertaDocumento(conn, tituloAplicacion, idGabinete, idCarpeta, nombreDocumento, ext, nombreUsuario, archivo, psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, true);
    }

    public static final void insertaDocumento(Connection conn, String tituloAplicacion, int idGabinete, int idCarpeta, String nombreDocumento, String ext, String nombreUsuario, String archivo, PreparedStatement psSearchVolumenUnit, PreparedStatement psSearchVolumen, PreparedStatement psSearchVolPath, boolean eliminaTemporal) throws Exception {
        OutputStream fos = null;
        InputStream in = null;
        try {
            Volumen vol = getVolumen(conn, psSearchVolumenUnit, psSearchVolumen, psSearchVolPath);
            Documento d = DocumentoManager.getDocumento(conn, tituloAplicacion, idGabinete, idCarpeta, nombreDocumento);
            if (d == null) {
                d = new Documento();
                d.setTituloAplicacion(tituloAplicacion);
                d.setIdGabinete(idGabinete);
                d.setIdCarpetaPadre(idCarpeta);
                if ("imx".equals(ext))
                    d.setNombreTipoDocto("IMAX_FILE");
                else
                    d.setNombreTipoDocto("EXTERNO");
                d.setNombreDocumento(nombreDocumento);
                d.setNombreUsuario(nombreUsuario);
                d.setExtension(ext);
                DocumentoManager.insertDocumento(conn, d);
            }
            if (StringUtils.isEmpty(d.getExtension()))
                d.setExtension(ext);
            DocumentoManager.insertPaginaDocumento(conn, vol, d, "A", 0);
            String filename = PaginaManager.getFilenamePath(conn, d.getTituloAplicacion(), d.getIdGabinete(), d.getIdCarpetaPadre(), d.getIdDocumento());
            if ((d.getExtension() == null) || ("".equals(d.getExtension())))
                d.setExtension(ext);
            fos = new FileOutputStream(filename);
            in = new FileInputStream(new File(archivo));
            int fileLength = 0, length = 0;
            byte[] buffer = new byte[10 * 1024];
            while ((in != null) && ((length = in.read(buffer)) != -1)) {
                fos.write(buffer, 0, length);
                fileLength += length;
            }
            fos.flush();
            String lowerFilename = nombreDocumento.toLowerCase() + ".xml";
            String[] filenames = d.getFilesNames();
            for (int i = 0; i < filenames.length; i++) {
                if (lowerFilename.equals(filenames[i].toLowerCase())) {
                    Pagina p = d.getPaginaDocumento(i);
                    p.setTamanoBytes(fileLength);
                    PaginaManager.updatePagina(conn, p);
                    break;
                }
            }
        } finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (Exception e) {
                    log.warn("Problemas intentando cerrar el flujo de salida: " + e, e);
                }
            if (in != null)
                try {
                    in.close();
                } catch (Exception e) {
                    log.warn("Problemas intentando cerrar el flujo de salida: " + e, e);
                }
            fos = null;
            in = null;
            if (eliminaTemporal) {
                try {
                    File delFile = new File(archivo);
                    if (!delFile.delete())
                        delFile.deleteOnExit();
                } catch (Exception e) {
                    log.warn("Problemas borrando archivo. " + e, e);
                }
            }
        }
    }

    private static int insertOrgCarpeta(PreparedStatement psInsertFolderHierarchy, OrgCarpeta oc) throws Exception {
        int retval = -1;
        psInsertFolderHierarchy.setString(1, oc.getTituloAplicacion());
        psInsertFolderHierarchy.setInt(2, oc.getIdGabinete());
        psInsertFolderHierarchy.setInt(3, oc.getIdCarpetaHija());
        psInsertFolderHierarchy.setInt(4, oc.getIdCarpetaPadre());
        psInsertFolderHierarchy.setString(5, oc.getNombreHija());
        retval = psInsertFolderHierarchy.executeUpdate();
        return retval;
    }

    private static String nextVolumenSequence(String volumen) throws Exception {
        String value = volumen;
        String lastChar = null;
        if (("zzzzz".equals(value)) || ("".equals(value)) || (value == null))
            throw new Exception("Numero maximo de volumenes alcanzado");
        lastChar = value.substring(value.length() - 1).toLowerCase();
        if (lastChar.equals("z"))
            value = nextVolumenSequence(value.substring(0, value.length() - 1));
        else if (lastChar.equals("9"))
            value = value.substring(0, value.length() - 1) + "a";
        else
            value = value.substring(0, value.length() - 1) + siguienteCaracter(lastChar);
        return (value + "00000".substring(0, 5 - value.length()));
    }

    private static Carpeta obtenCarpetaDestino(PreparedStatement psInsertFolderHierarchy, PreparedStatement psInsertFolder, PreparedStatement psNexIDFolder, PreparedStatement psSearchFolder, String tituloAplicacion, int idGabinete, String nombreCarpeta) throws Exception {
        log.trace("Object: {}", "Inicia busqueda de carpeta [" + nombreCarpeta + "]");
        long start = System.currentTimeMillis();
        Carpeta cfdiCarpeta = getCarpetaByName(psSearchFolder, tituloAplicacion, idGabinete, nombreCarpeta);
        if (cfdiCarpeta == null) {
            log.trace("Object: {}", "No existe la carpeta [" + nombreCarpeta + "] se creara.");
            Carpeta modelo = new Carpeta();
            modelo.setTituloAplicacion(tituloAplicacion);
            modelo.setIdGabinete(idGabinete);
            modelo.setIdCarpeta(getNextIdCarpeta(psNexIDFolder, tituloAplicacion, idGabinete));
            modelo.setNombreCarpeta(nombreCarpeta);
            modelo.setNombreUsuario("CLC_AUTO_JOB");
            modelo.setBanderaRaiz("N");
            modelo.setDescripcion("Carpeta que contiene la CLC");
            modelo.setPassword("-1");
            cfdiCarpeta = insertaCarpeta(psSearchFolder, psInsertFolder, modelo);
            OrgCarpeta oc = new OrgCarpeta();
            oc.setIdCarpetaHija(cfdiCarpeta.getIdCarpeta());
            oc.setIdCarpetaPadre(0);
            oc.setIdGabinete(idGabinete);
            oc.setNombreHija(cfdiCarpeta.getNombreCarpeta());
            oc.setTituloAplicacion(cfdiCarpeta.getTituloAplicacion());
            insertOrgCarpeta(psInsertFolderHierarchy, oc);
            log.trace("Object: {}", "Carpeta [" + nombreCarpeta + "] creada con exito.");
        }
        long stop = System.currentTimeMillis();
        log.trace("Object: {}", "Finaliza busqueda de carpeta en [" + ((stop - start) / 1000) + "] s.");
        return cfdiCarpeta;
    }

    private static String siguienteCaracter(String ch) {
        try {
            return String.valueOf(Integer.parseInt(ch, 10) + 1);
        } catch (NumberFormatException e) {
            String[] letters = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
            for (int i = 0; i < letters.length; i++) {
                if (ch.equals(letters[i])) {
                    return letters[i + 1];
                }
            }
        }
        return null;
    }

    public static List<AttachResult> attachComprobanteBancario(Connection conn, String centroContable, boolean ignorarExistentes, String cbFilePath, CallableStatement csBuscaCxP, CallableStatement csBuscaGabinete, PreparedStatement psInsertFolderHierarchy, PreparedStatement psInsertFolder, PreparedStatement psNexIDFolder, PreparedStatement psSearchFolder, PreparedStatement psSearchVolumenUnit, PreparedStatement psSearchVolumen, PreparedStatement psSearchVolPath, String cTipoPago) throws Exception {
        int procesados = 0;
        ResultSet rsGabinetes = null;
        ResultSet rsCLCs = null;
        List<AttachResult> resultList = new ArrayList<AttachResult>();
        log.trace("Object: {}", "Procesando archivo [" + cbFilePath + "]");
        try {
            /*ARLA20190911 Se hace la busqueda por numero de folio y no por cxp*/
            String nombreArchivo = new File(cbFilePath).getName();
            String contraRecibo = new File(cbFilePath).getName().substring(0, nombreArchivo.lastIndexOf("."));
            /*FAV20171019 Se guarda en base el prefijo de CxP*/
            /*
			ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic( GestionInterface.ATT_CONEXION );
			String cxpPrefijo = cabl.getSystemSetting("CXP_PREFIJO") == null?"CP":cabl.getSystemSetting("CXP_PREFIJO");
			
			String ef = String.valueOf(EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal());
			int numContraRecibo = 0;
			String contraRecibo = "";
			String ceros = "000000";
			String nombreArchivo = new File(cbFilePath).getName();

			if ( nombreArchivo.toUpperCase().indexOf(cxpPrefijo) < 0) {
				numContraRecibo = Integer.parseInt(new File(cbFilePath).getName().substring(0, nombreArchivo.lastIndexOf(".")), 10);
				contraRecibo = centroContable + cxpPrefijo + ef + (ceros.substring(String.valueOf(numContraRecibo).length())) + numContraRecibo;
			} else {
				contraRecibo = Util.getFileWithoutExtencion(nombreArchivo);
			}
			*/
            log.info("Object: {}", "Adjuntando archivo [" + cbFilePath + "] al  Contrarecibo " + contraRecibo);
            csBuscaCxP.setString(1, contraRecibo);
            csBuscaCxP.setString(2, cTipoPago);
            rsCLCs = csBuscaCxP.executeQuery();
            boolean encontrados = false;
            while (rsCLCs.next()) {
                encontrados = true;
                String tipoPago = rsCLCs.getString("cTipoPago");
                String nFolioPago = rsCLCs.getString("nFolioPAGO");
                if (StringUtils.isEmpty(nFolioPago) || "-1".equals(nFolioPago)) {
                    CLCAttachmentLogManager.insertLog(conn, new File(cbFilePath).getName(), 0, contraRecibo, -1, tipoPago, '0', "No se encontro pago con CxP " + contraRecibo);
                    resultList.add(new AttachResult(nombreArchivo, "No se encontro pago con CxP " + contraRecibo, false));
                    continue;
                }
                csBuscaGabinete.setString(1, tipoPago);
                csBuscaGabinete.setInt(2, Integer.parseInt(nFolioPago, 10));
                rsGabinetes = csBuscaGabinete.executeQuery();
                if (rsGabinetes.next()) {
                    int idGabinete = rsGabinetes.getInt(1);
                    if ((ignorarExistentes && !DocumentoManager.existeDocumentoCapturado(conn, tipoPago, idGabinete, "Comprobante Banco")) || !ignorarExistentes) {
                        log.debug("Object: {}", " Adjuntando archivo al pago " + tipoPago + " con folio " + nFolioPago);
                        try {
                            procesados += CLCAttachmentManager.attchToExpedient(psInsertFolderHierarchy, psInsertFolder, psNexIDFolder, psSearchFolder, tipoPago, idGabinete, cbFilePath, psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, conn, "Comprobante Banco", "Comprobante Banco");
                            CLCAttachmentLogManager.insertLog(conn, new File(cbFilePath).getName(), 0, contraRecibo, Integer.parseInt(nFolioPago, 10), tipoPago, '9', "Se adjunto exitosamente el archivo");
                            resultList.add(new AttachResult(nombreArchivo, "Se adjunto exitosamente el archivo " + contraRecibo, true));
                        } catch (Exception e) {
                            try {
                                CLCAttachmentLogManager.insertLog(conn, new File(cbFilePath).getName(), 0, contraRecibo, Integer.parseInt(nFolioPago, 10), tipoPago, '2', "Ocurrio el siguiente error al adjuntar: " + e);
                            } catch (Exception e2) {
                                log.error(e2.getMessage(), e2);
                            }
                            throw e;
                        }
                    } else {
                        CLCAttachmentLogManager.insertLog(conn, new File(cbFilePath).getName(), 0, contraRecibo, Integer.parseInt(nFolioPago, 10), tipoPago, '3', " El archivo ya existe. Se ignora.");
                        resultList.add(new AttachResult(nombreArchivo, "El archivo ya existe. Se ignora. ", false));
                    }
                } else {
                    CLCAttachmentLogManager.insertLog(conn, new File(cbFilePath).getName(), 0, contraRecibo, Integer.parseInt(nFolioPago, 10), tipoPago, '1', "No se encontro gabinete en  IMX" + tipoPago + " con el folio [" + nFolioPago + "]");
                    resultList.add(new AttachResult(nombreArchivo, "No se encontro gabinete en  IMX" + tipoPago + " con el folio [" + nFolioPago + "]", false));
                }
            }
            if (!encontrados) {
                CLCAttachmentLogManager.insertLog(conn, new File(cbFilePath).getName(), 0, contraRecibo, -1, "RELACIONGASTOS", '0', "No se encontro pago con CxP " + contraRecibo);
                resultList.add(new AttachResult(nombreArchivo, "No se encontro pago con CxP " + contraRecibo, false));
            }
            log.info("Object: {}", "Se procesaron " + procesados + " archivos.");
        } catch (Exception e) {
            resultList.add(new AttachResult(cbFilePath, "Ocurrio el siguiente error al adjuntar: " + e, false));
            throw e;
        } finally {
            CloseObject.closeObject(rsGabinetes, false);
            CloseObject.closeObject(rsCLCs, false);
        }
        return resultList;
    }

    private synchronized static boolean makeDirectories(File path) {
        if (path == null)
            throw new NullPointerException("path no debe ser nulo");
        if (path.exists())
            return true;
        if (path.mkdir())
            return true;
        File canonPath = null;
        try {
            canonPath = path.getCanonicalFile();
        } catch (IOException e) {
            return false;
        }
        String parent = canonPath.getParent();
        return (parent != null) && (makeDirectories(new File(parent)) && canonPath.mkdir());
    }

    public static int attachDocumento(Connection conn, boolean ignorarExistentes, String cbFilePath, PreparedStatement psBuscaPago, PreparedStatement psInsertFolderHierarchy, PreparedStatement psInsertFolder, PreparedStatement psNexIDFolder, PreparedStatement psSearchFolder, PreparedStatement psSearchVolumenUnit, PreparedStatement psSearchVolumen, PreparedStatement psSearchVolPath, String nombreCarpeta, String nombreDocumento) throws Exception {
        int procesados = 0;
        ResultSet rsGabinetes = null;
        log.trace("Object: {}", "Procesando archivo [" + cbFilePath + "]");
        try {
            String cFolioPago = new File(cbFilePath).getName().substring(0, new File(cbFilePath).getName().lastIndexOf("."));
            log.info("Object: {}", "Adjuntando archivo [" + cbFilePath + "] al pago con folio [" + cFolioPago + "]");
            psBuscaPago.setString(1, cFolioPago);
            rsGabinetes = psBuscaPago.executeQuery();
            if (rsGabinetes.next()) {
                int idGabinete = rsGabinetes.getInt("c_id_gabinete");
                if (idGabinete < 0) {
                    CLCAttachmentLogManager.insertLog(conn, new File(cbFilePath).getName(), 0, cFolioPago, -1, cFolioPago, '0', "No se encontr\u00F3 expediente creado para el pago " + cFolioPago + " por lo que no se adjunto", "SOLPAGO");
                } else {
                    String tipoPago = rsGabinetes.getString("tc_gaveta_asociada");
                    if ((ignorarExistentes && !DocumentoManager.existeDocumentoCapturado(conn, tipoPago, idGabinete, nombreDocumento)) || !ignorarExistentes) {
                        log.debug("Object: {}", " Adjuntando archivo al pago " + tipoPago + " con folio " + cFolioPago);
                        try {
                            procesados = CLCAttachmentManager.attchToExpedient(psInsertFolderHierarchy, psInsertFolder, psNexIDFolder, psSearchFolder, tipoPago, idGabinete, cbFilePath, psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, conn, nombreCarpeta, nombreDocumento);
                            CLCAttachmentLogManager.insertLog(conn, new File(cbFilePath).getName(), 0, cFolioPago, idGabinete, tipoPago, '9', "Se adjunto exitosamente el archivo", "SOLPAGO");
                        } catch (Exception e) {
                            try {
                                CLCAttachmentLogManager.insertLog(conn, new File(cbFilePath).getName(), 0, cFolioPago, idGabinete, tipoPago, '2', "Ocurrio el siguiente error al adjuntar: " + e, "SOLPAGO");
                            } catch (Exception e2) {
                                log.error(e2.getMessage(), e2);
                            }
                            throw e;
                        }
                    } else {
                        CLCAttachmentLogManager.insertLog(conn, new File(cbFilePath).getName(), 0, cFolioPago, idGabinete, tipoPago, '3', " El archivo ya existe. Se ignora.", "SOLPAGO");
                    }
                }
            } else {
                CLCAttachmentLogManager.insertLog(conn, new File(cbFilePath).getName(), 0, cFolioPago, -1, "Desconocido", '1', "No se encontro gabinete en  cg_caso con el folio [" + cFolioPago + "]");
            }
            return procesados;
        } finally {
            CloseObject.closeObject(rsGabinetes, false);
        }
    }

    public static int attachDocumento(Connection conn, boolean ignorarExistentes, String cbFilePath, PreparedStatement psBuscaPago, PreparedStatement psInsertFolderHierarchy, PreparedStatement psInsertFolder, PreparedStatement psNexIDFolder, PreparedStatement psSearchFolder, PreparedStatement psSearchVolumenUnit, PreparedStatement psSearchVolumen, PreparedStatement psSearchVolPath, String nombreCarpeta, String nombreDocumento, String cFolioPago) throws Exception {
        int procesados = 0;
        ResultSet rsGabinetes = null;
        log.trace("Object: {}", "Procesando archivo [" + cbFilePath + "]");
        try {
            log.info("Object: {}", "Adjuntando archivo [" + cbFilePath + "] al pago con folio [" + cFolioPago + "]");
            psBuscaPago.setString(1, cFolioPago);
            rsGabinetes = psBuscaPago.executeQuery();
            if (rsGabinetes.next()) {
                int idGabinete = rsGabinetes.getInt("c_id_gabinete");
                if (idGabinete < 0) {
                    CLCAttachmentLogManager.insertLog(conn, new File(cbFilePath).getName(), 0, cFolioPago, -1, cFolioPago, '0', "No se encontr\u00F3 expediente creado para el pago " + cFolioPago + " por lo que no se adjunto", "SOLPAGO");
                } else {
                    String tipoPago = rsGabinetes.getString("tc_gaveta_asociada");
                    if ((ignorarExistentes && !DocumentoManager.existeDocumentoCapturado(conn, tipoPago, idGabinete, nombreCarpeta, nombreDocumento)) || !ignorarExistentes) {
                        log.debug("Object: {}", " Adjuntando archivo al pago " + tipoPago + " con folio " + cFolioPago);
                        try {
                            procesados = CLCAttachmentManager.attchToExpedient(psInsertFolderHierarchy, psInsertFolder, psNexIDFolder, psSearchFolder, tipoPago, idGabinete, cbFilePath, psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, conn, nombreCarpeta, nombreDocumento);
                            CLCAttachmentLogManager.insertLog(conn, new File(cbFilePath).getName(), 0, cFolioPago, idGabinete, tipoPago, '9', "Se adjunto exitosamente el archivo", "CONBANCOFIRMADA");
                        } catch (Exception e) {
                            try {
                                CLCAttachmentLogManager.insertLog(conn, new File(cbFilePath).getName(), 0, cFolioPago, idGabinete, tipoPago, '2', "Ocurrio el siguiente error al adjuntar: " + e, "CONBANCOFIRMADA");
                            } catch (Exception e2) {
                                log.error(e2.getMessage(), e2);
                            }
                            throw e;
                        }
                    } else {
                        CLCAttachmentLogManager.insertLog(conn, new File(cbFilePath).getName(), 0, cFolioPago, idGabinete, tipoPago, '3', " El archivo ya existe. Se ignora.", "SOLPAGO");
                    }
                }
            } else {
                CLCAttachmentLogManager.insertLog(conn, new File(cbFilePath).getName(), 0, cFolioPago, -1, "Desconocido", '1', "No se encontro gabinete en  cg_caso con el folio [" + cFolioPago + "]");
            }
            return procesados;
        } finally {
            CloseObject.closeObject(rsGabinetes, false);
        }
    }

    public static List<AttachResult> attachComprobanteBancarioSNP(Connection conn, String centroContable, boolean ignorarExistentes, String cbFilePath, PreparedStatement csBuscaCxP, PreparedStatement csBuscaGabinete, PreparedStatement psInsertFolderHierarchy, PreparedStatement psInsertFolder, PreparedStatement psNexIDFolder, PreparedStatement psSearchFolder, PreparedStatement psSearchVolumenUnit, PreparedStatement psSearchVolumen, PreparedStatement psSearchVolPath, ProcesoAdjunta procesoAdjunta) throws Exception {
        int procesados = 0;
        ResultSet rsGabinetes = null;
        ResultSet rsCLCs = null;
        List<AttachResult> resultList = new ArrayList<AttachResult>();
        log.trace("Object: {}", "Procesando archivo [" + cbFilePath + "]");
        try {
            String nombreArchivo = new File(cbFilePath).getName();
            int nFolioCaja = Integer.parseInt(Util.getFileWithoutExtencion(nombreArchivo));
            log.info("Object: {}", "Adjuntando archivo [" + cbFilePath + "] al folio de Caja " + nFolioCaja);
            csBuscaCxP.setInt(1, nFolioCaja);
            rsCLCs = csBuscaCxP.executeQuery();
            boolean encontrados = false;
            while (rsCLCs.next()) {
                encontrados = true;
                String tipoPago = rsCLCs.getString("cTipoPago");
                String nFolioPago = rsCLCs.getString("nFolioPAGO");
                if (StringUtils.isEmpty(nFolioPago) || "-1".equals(nFolioPago)) {
                    CLCAttachmentLogManager.insertLogCLC(conn, new File(cbFilePath).getName(), 0, String.valueOf(nFolioCaja), -1, tipoPago, '0', "No se encontro Solictud No Presupuestal con folio " + nFolioCaja, "COMPROBANTE_SNP", procesoAdjunta.getIdProceso());
                    resultList.add(new AttachResult(nombreArchivo, "No se encontro SNP con folio " + nFolioCaja, false));
                    continue;
                }
                csBuscaGabinete.setInt(1, Integer.parseInt(nFolioPago, 10));
                rsGabinetes = csBuscaGabinete.executeQuery();
                if (rsGabinetes.next()) {
                    int idGabinete = rsGabinetes.getInt(1);
                    if ((ignorarExistentes && !DocumentoManager.existeDocumentoCapturado(conn, tipoPago, idGabinete, "Comprobante Banco")) || !ignorarExistentes) {
                        log.debug("Object: {}", " Adjuntando archivo al pago " + tipoPago + " con folio " + nFolioPago);
                        try {
                            procesados += CLCAttachmentManager.attchToExpedient(psInsertFolderHierarchy, psInsertFolder, psNexIDFolder, psSearchFolder, tipoPago, idGabinete, cbFilePath, psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, conn, "Comprobante Banco", "Comprobante Banco");
                            CLCAttachmentLogManager.insertLogCLC(conn, new File(cbFilePath).getName(), 0, String.valueOf(nFolioCaja), Integer.parseInt(nFolioPago, 10), tipoPago, '9', "Se adjunto exitosamente el archivo", "COMPROBANTE_SNP", procesoAdjunta.getIdProceso());
                            resultList.add(new AttachResult(nombreArchivo, "Se adjunto exitosamente el archivo " + String.valueOf(nFolioCaja), true));
                        } catch (Exception e) {
                            try {
                                CLCAttachmentLogManager.insertLogCLC(conn, new File(cbFilePath).getName(), 0, String.valueOf(nFolioCaja), Integer.parseInt(nFolioPago, 10), tipoPago, '2', "Ocurrio el siguiente error al adjuntar: " + e, "COMPROBANTE_SNP", procesoAdjunta.getIdProceso());
                            } catch (Exception e2) {
                                log.error(e2.getMessage(), e2);
                            }
                            throw e;
                        }
                    } else {
                        CLCAttachmentLogManager.insertLogCLC(conn, new File(cbFilePath).getName(), 0, String.valueOf(nFolioCaja), Integer.parseInt(nFolioPago, 10), tipoPago, '3', " El archivo ya existe. Se ignora.", "COMPROBANTE_SNP", procesoAdjunta.getIdProceso());
                        resultList.add(new AttachResult(nombreArchivo, "El archivo ya existe. Se ignora. ", false));
                    }
                } else {
                    CLCAttachmentLogManager.insertLogCLC(conn, new File(cbFilePath).getName(), 0, String.valueOf(nFolioCaja), Integer.parseInt(nFolioPago, 10), tipoPago, '1', "No se encontro gabinete en  IMX" + tipoPago + " con el folio [" + nFolioPago + "]", "COMPROBANTE_SNP", procesoAdjunta.getIdProceso());
                    resultList.add(new AttachResult(nombreArchivo, "No se encontro gabinete en  IMX" + tipoPago + " con el folio [" + nFolioPago + "]", false));
                }
            }
            if (!encontrados) {
                CLCAttachmentLogManager.insertLogCLC(conn, new File(cbFilePath).getName(), 0, String.valueOf(nFolioCaja), -1, "RELACIONGASTOS", '0', "No se encontro pago con CxP " + String.valueOf(nFolioCaja), "COMPROBANTE_SNP", procesoAdjunta.getIdProceso());
                resultList.add(new AttachResult(nombreArchivo, "No se encontro pago con CxP " + String.valueOf(nFolioCaja), false));
            }
            log.info("Object: {}", "Se procesaron " + procesados + " archivos.");
        } catch (Exception e) {
            resultList.add(new AttachResult(cbFilePath, "Ocurrio el siguiente error al adjuntar: " + e, false));
            throw e;
        } finally {
            CloseObject.closeObject(rsGabinetes, false);
            CloseObject.closeObject(rsCLCs, false);
        }
        return resultList;
    }

    public static List<AttachResult> attachDocumentoSNP(Connection conn, String centroContable, boolean ignorarExistentes, String cbFilePath, PreparedStatement csBuscaCxP, PreparedStatement csBuscaGabinete, PreparedStatement psInsertFolderHierarchy, PreparedStatement psInsertFolder, PreparedStatement psNexIDFolder, PreparedStatement psSearchFolder, PreparedStatement psSearchVolumenUnit, PreparedStatement psSearchVolumen, PreparedStatement psSearchVolPath, ProcesoAdjunta procesoAdjunta, String nombreCarpeta, String nombreDocumento) throws Exception {
        int procesados = 0;
        ResultSet rsGabinetes = null;
        ResultSet rsCLCs = null;
        List<AttachResult> resultList = new ArrayList<AttachResult>();
        log.trace("Object: {}", "Procesando archivo [" + cbFilePath + "]");
        try {
            String nombreArchivo = new File(cbFilePath).getName();
            int nFolioCaja = Integer.parseInt(Util.getFileWithoutExtencion(nombreArchivo));
            log.info("Object: {}", "Adjuntando archivo [" + cbFilePath + "] al folio de Caja " + nFolioCaja);
            csBuscaCxP.setInt(1, nFolioCaja);
            rsCLCs = csBuscaCxP.executeQuery();
            boolean encontrados = false;
            while (rsCLCs.next()) {
                encontrados = true;
                String tipoPago = rsCLCs.getString("cTipoPago");
                String nFolioPago = rsCLCs.getString("nFolioPAGO");
                if (StringUtils.isEmpty(nFolioPago) || "-1".equals(nFolioPago)) {
                    CLCAttachmentLogManager.insertLogCLC(conn, new File(cbFilePath).getName(), 0, String.valueOf(nFolioCaja), -1, tipoPago, '0', "No se encontro Solictud No Presupuestal con folio " + nFolioCaja, "COMPROBANTE_SNP", procesoAdjunta.getIdProceso());
                    resultList.add(new AttachResult(nombreArchivo, "No se encontro SNP con folio " + nFolioCaja, false));
                    continue;
                }
                csBuscaGabinete.setInt(1, Integer.parseInt(nFolioPago, 10));
                rsGabinetes = csBuscaGabinete.executeQuery();
                if (rsGabinetes.next()) {
                    int idGabinete = rsGabinetes.getInt(1);
                    if ((ignorarExistentes && !DocumentoManager.existeDocumentoCapturado(conn, tipoPago, idGabinete, nombreCarpeta, nombreDocumento)) || !ignorarExistentes) {
                        log.debug("Object: {}", " Adjuntando archivo al pago " + tipoPago + " con folio " + nFolioPago);
                        try {
                            procesados += CLCAttachmentManager.attchToExpedient(psInsertFolderHierarchy, psInsertFolder, psNexIDFolder, psSearchFolder, tipoPago, idGabinete, cbFilePath, psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, conn, nombreCarpeta, nombreDocumento);
                            CLCAttachmentLogManager.insertLogCLC(conn, new File(cbFilePath).getName(), 0, String.valueOf(nFolioCaja), Integer.parseInt(nFolioPago, 10), tipoPago, '9', "Se adjunto exitosamente el archivo", "COMPROBANTE_SNP", procesoAdjunta.getIdProceso());
                            resultList.add(new AttachResult(nombreArchivo, "Se adjunto exitosamente el archivo " + String.valueOf(nFolioCaja), true));
                        } catch (Exception e) {
                            try {
                                CLCAttachmentLogManager.insertLogCLC(conn, new File(cbFilePath).getName(), 0, String.valueOf(nFolioCaja), Integer.parseInt(nFolioPago, 10), tipoPago, '2', "Ocurrio el siguiente error al adjuntar: " + e, "COMPROBANTE_SNP", procesoAdjunta.getIdProceso());
                            } catch (Exception e2) {
                                log.error(e2.getMessage(), e2);
                            }
                            throw e;
                        }
                    } else {
                        CLCAttachmentLogManager.insertLogCLC(conn, new File(cbFilePath).getName(), 0, String.valueOf(nFolioCaja), Integer.parseInt(nFolioPago, 10), tipoPago, '3', " El archivo ya existe. Se ignora.", "COMPROBANTE_SNP", procesoAdjunta.getIdProceso());
                        resultList.add(new AttachResult(nombreArchivo, "El archivo ya existe. Se ignora. ", false));
                    }
                } else {
                    CLCAttachmentLogManager.insertLogCLC(conn, new File(cbFilePath).getName(), 0, String.valueOf(nFolioCaja), Integer.parseInt(nFolioPago, 10), tipoPago, '1', "No se encontro gabinete en  IMX" + tipoPago + " con el folio [" + nFolioPago + "]", "COMPROBANTE_SNP", procesoAdjunta.getIdProceso());
                    resultList.add(new AttachResult(nombreArchivo, "No se encontro gabinete en  IMX" + tipoPago + " con el folio [" + nFolioPago + "]", false));
                }
            }
            if (!encontrados) {
                CLCAttachmentLogManager.insertLogCLC(conn, new File(cbFilePath).getName(), 0, String.valueOf(nFolioCaja), -1, "RELACIONGASTOS", '0', "No se encontro pago con CxP " + String.valueOf(nFolioCaja), "COMPROBANTE_SNP", procesoAdjunta.getIdProceso());
                resultList.add(new AttachResult(nombreArchivo, "No se encontro pago con CxP " + String.valueOf(nFolioCaja), false));
            }
            log.info("Object: {}", "Se procesaron " + procesados + " archivos.");
        } catch (Exception e) {
            resultList.add(new AttachResult(cbFilePath, "Ocurrio el siguiente error al adjuntar: " + e, false));
            throw e;
        } finally {
            CloseObject.closeObject(rsGabinetes, false);
            CloseObject.closeObject(rsCLCs, false);
        }
        return resultList;
    }
}
