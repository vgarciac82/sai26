package com.syc.ejercido.pagado;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import com.syc.cfdi.utils.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.sai.procesosAutomaticos.AttachResult;
import com.syc.sai.procesosAutomaticos.core.ProcesoAdjunta;
import com.syc.sai.procesosAutomaticos.core.ProcesoAdjuntaBusinessLogic;
import com.syc.utils.pdf.PDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CLCAttachmentBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(CLCAttachmentBusinessLogic.class);

    private int standAlone = 0;

    private CallableStatement csBuscaGabinete = null;

    private CallableStatement csBuscaCLC = null;

    private CallableStatement csBuscaCxP = null;

    private PreparedStatement psBuscaPago = null;

    private PreparedStatement psInsertFolderHierarchy = null;

    private PreparedStatement psInsertFolder = null;

    private PreparedStatement psNexIDFolder = null;

    private PreparedStatement psSearchFolder = null;

    private PreparedStatement psSearchVolumenUnit = null;

    private PreparedStatement psSearchVolumen = null;

    private PreparedStatement psSearchVolPath = null;

    private PreparedStatement psBuscaSNP = null;

    private PreparedStatement psBuscaGabineteSNP = null;

    private static final String queryInsertFolder = "INSERT INTO imx_carpeta " + "            (titulo_aplicacion, " + "             id_gabinete, " + "             id_carpeta, " + "             nombre_carpeta, " + "             nombre_usuario, " + "             bandera_raiz, " + "             fh_creacion, " + "             fh_modificacion, " + "             numero_accesos, " + "             numero_carpetas, " + "             numero_documentos, " + "             descripcion, " + "             password) " + "VALUES     ( ?, " + "             ?, " + "             ?, " + "             ?, " + "             ?, " + "             ?, " + "             Getdate(), " + "             Getdate(), " + "             0, " + "             0, " + "             0, " + "             ?, " + "             ? ) ";

    private static final String queryInsertFolderHierarchy = "INSERT INTO imx_org_carpeta (titulo_aplicacion, id_gabinete, " + "id_carpeta_hija, id_carpeta_padre, nombre_hija) VALUES (?, ?, ?, ?, ?)";

    private static final String queryNexFolderID = "SELECT MAX(id_carpeta) FROM imx_carpeta WITH (NOLOCK) WHERE titulo_aplicacion = ? " + "AND id_gabinete = ?";

    private static final String querySearchFilePath = "SELECT ruta_directorio FROM imx_volumen WITH (NOLOCK) " + " WHERE volumen = ? AND unidad_disco = ? AND tipo_volumen = ?";

    private static final String querySearchFolder = "SELECT titulo_aplicacion, " + "       id_gabinete, " + "       id_carpeta, " + "       nombre_carpeta, " + "       nombre_usuario, " + "       bandera_raiz, " + "       fh_creacion, " + "       fh_modificacion, " + "       numero_accesos, " + "       numero_carpetas, " + "       numero_documentos, " + "       descripcion, " + "       password " + "FROM   imx_carpeta WITH (NOLOCK) " + "WHERE  bandera_raiz <> 'S' " + "       AND nombre_carpeta = ? " + "       AND id_gabinete = ? " + "       AND titulo_aplicacion = ?";

    private static final String querySearchVolumen = "SELECT volumen " + "FROM   imx_volumen WITH (NOLOCK)" + "WHERE  unidad_disco = ?  " + "        AND tipo_volumen = ? AND capacidad = ?";

    private static final String querySearchVolumenUnit = "SELECT unidad, " + "       tipo_dispositivo, " + "       ruta_base " + "FROM   imx_unidad_volumen WITH (NOLOCK)" + "WHERE  estado_unidad = ?";

    private static final String queryBuscaPago = "SELECT  caso.c_id_gabinete, " + "	       tipoCaso.tc_gaveta_asociada " + "	FROM   cg_caso caso WITH(nolock) " + "	       INNER JOIN dbo.cg_tipo_caso tipoCaso WITH(nolock) " + "	               ON caso.id_tc = tipoCaso.id_tc " + "	WHERE  c_folio = ? ";

    private static final String queryBuscaSNP = "SELECT 'CAJA'     AS cTipoPago, " + "       id_caso AS nFolioPAGO " + "FROM   tcajaencabezado with(nolock)  " + "WHERE  nfoliocaja = ? ";

    private static final String queryBuscaGabineteSNP = "SELECT  c_id_gabinete, " + "       c_folio  " + "FROM   cg_caso WITH(nolock) " + "WHERE  id_caso = ?";

    private ProcesoAdjuntaBusinessLogic pabl;

    private ProcesoAdjunta pa = null;

    public CLCAttachmentBusinessLogic() {
        standAlone = 1;
    }

    public CLCAttachmentBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public void attachCLCs(String zipFilePath, String outputDir, boolean ignorarExistentes) {
        log.info("Iniciando extraccion de archivo con CLCs a adjuntar.");
        log.trace("Iniciando Adjuntar archivos CLC.");
        long start = System.currentTimeMillis();
        List<File> arrList = new ArrayList<File>();
        try {
            Connection conn = null;
            File dirCLC = new File(outputDir);
            if (!dirCLC.exists() && !dirCLC.mkdirs())
                throw new Exception("El directorio [" + outputDir + "] no existe. Reporte al administrador del sistema.");
            if (!dirCLC.isDirectory())
                throw new Exception("La ruta[" + outputDir + "] no corresponde a un directorio. Reporte al administrador del sistema.");
            int extraidos = CLCAttachmentManager.extractZipCLC(zipFilePath, outputDir, arrList);
            log.debug("El archivo [" + zipFilePath + "] contiene [" + extraidos + "] archivos");
            log.trace("Iniciando adjuntamiento de archivos.");
            File[] files = arrList.toArray(new File[arrList.size()]);
            for (int i = 0; i < files.length; i++) {
                try {
                    if (standAlone == 0)
                        conn = getConnection();
                    else
                        conn = Util.getStandAloneConnection();
                    if (conn.getAutoCommit())
                        conn.setAutoCommit(false);
                    String clcFilePath = files[i].getAbsolutePath();
                    createStatements(conn);
                    int adjuntados = CLCAttachmentManager.attachCLC(conn, ignorarExistentes, clcFilePath, csBuscaCLC, csBuscaGabinete, psInsertFolderHierarchy, psInsertFolder, psNexIDFolder, psSearchFolder, psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, getProcesoAdjunta());
                    log.info("Se adjuntaron [" + adjuntados + "]");
                    conn.commit();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    if (conn != null)
                        try {
                            conn.rollback();
                        } catch (Exception e2) {
                            log.warn("Problemas realizando ROLLBACK " + e2, e2);
                        }
                } finally {
                    closeStatements();
                    CloseObject.closeObject(conn, false);
                    if (!files[i].delete())
                        files[i].deleteOnExit();
                }
            }
            getProcesoAdjunta().setIdEstatus(1);
            getProcesoAdjunta().setResultadoProceso("Proceso terminado.");
            getProcesoAdjuntaBL().actualizaResultado(getProcesoAdjunta());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        long stop = System.currentTimeMillis();
        log.trace("Terminado Adjuntar archivos CLC en (" + (stop - start) / 1000 + " s.])");
    }

    public void attachFactura(String zipFilePath, String outputDir, boolean ignorarExistentes) {
        log.info("Iniciando extraccion de archivo con Facturas a adjuntar.");
        log.trace("Iniciando Adjuntar archivos Facturas.");
        long start = System.currentTimeMillis();
        List<File> arrList = new ArrayList<File>();
        try {
            Connection conn = null;
            File dirCLC = new File(outputDir);
            if (!dirCLC.exists() && !dirCLC.mkdirs())
                throw new Exception("El directorio [" + outputDir + "] no existe. Reporte al administrador del sistema.");
            if (!dirCLC.isDirectory())
                throw new Exception("La ruta[" + outputDir + "] no corresponde a un directorio. Reporte al administrador del sistema.");
            int extraidos = CLCAttachmentManager.extractZipCLC(zipFilePath, outputDir, arrList);
            log.debug("El archivo [" + zipFilePath + "] contiene [" + extraidos + "] archivos");
            log.trace("Iniciando adjuntamiento de archivos.");
            File[] files = arrList.toArray(new File[arrList.size()]);
            for (int i = 0; i < files.length; i++) {
                try {
                    if (standAlone == 0)
                        conn = getConnection();
                    else
                        conn = Util.getStandAloneConnection();
                    if (conn.getAutoCommit())
                        conn.setAutoCommit(false);
                    String clcFilePath = files[i].getAbsolutePath();
                    createStatements(conn);
                    int adjuntados = CLCAttachmentManager.attachFactura(conn, ignorarExistentes, clcFilePath, csBuscaGabinete, psInsertFolderHierarchy, psInsertFolder, psNexIDFolder, psSearchFolder, psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, getProcesoAdjunta());
                    log.info("Se adjuntaron [" + adjuntados + "]");
                    conn.commit();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    if (conn != null)
                        try {
                            conn.rollback();
                        } catch (Exception e2) {
                            log.warn("Problemas realizando ROLLBACK " + e2, e2);
                        }
                } finally {
                    closeStatements();
                    CloseObject.closeObject(conn, false);
                    if (!files[i].delete())
                        files[i].deleteOnExit();
                }
            }
            getProcesoAdjunta().setIdEstatus(1);
            getProcesoAdjunta().setResultadoProceso("Proceso terminado.");
            getProcesoAdjuntaBL().actualizaResultado(getProcesoAdjunta());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        long stop = System.currentTimeMillis();
        log.trace("Terminado Adjuntar archivos CLC en (" + (stop - start) / 1000 + " s.])");
    }

    private void createStatements(Connection conn) throws Exception {
        psInsertFolderHierarchy = conn.prepareStatement(queryInsertFolderHierarchy);
        psInsertFolder = conn.prepareStatement(queryInsertFolder);
        psNexIDFolder = conn.prepareStatement(queryNexFolderID);
        psSearchFolder = conn.prepareStatement(querySearchFolder);
        psSearchVolumenUnit = conn.prepareStatement(querySearchVolumenUnit);
        psSearchVolumen = conn.prepareStatement(querySearchVolumen);
        psSearchVolPath = conn.prepareStatement(querySearchFilePath);
        csBuscaGabinete = conn.prepareCall(" {CALL dbo.fnBuscaGabinetePago(?,?) } ");
        csBuscaCLC = conn.prepareCall(" { CALL dbo.spListadoAdjuntarCLC(?) } ");
        csBuscaCxP = conn.prepareCall(" { CALL dbo.spbuscacxp(?, ?) } ");
        psBuscaPago = conn.prepareStatement(queryBuscaPago);
        psBuscaGabineteSNP = conn.prepareStatement(queryBuscaGabineteSNP);
        psBuscaSNP = conn.prepareStatement(queryBuscaSNP);
    }

    private void closeStatements() {
        try {
            CloseObject.closeObject(csBuscaGabinete, false);
            CloseObject.closeObject(csBuscaCLC, false);
            CloseObject.closeObject(csBuscaCxP, false);
            CloseObject.closeObject(psInsertFolderHierarchy, false);
            CloseObject.closeObject(psInsertFolder, false);
            CloseObject.closeObject(psNexIDFolder, false);
            CloseObject.closeObject(psSearchFolder, false);
            CloseObject.closeObject(psSearchVolumenUnit, false);
            CloseObject.closeObject(psSearchVolumen, false);
            CloseObject.closeObject(psSearchVolPath, false);
            CloseObject.closeObject(psBuscaPago, false);
            CloseObject.closeObject(psBuscaSNP, false);
            CloseObject.closeObject(psBuscaGabineteSNP, false);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public List<AttachResult> attachComprobantesBancarios(String centroContable, String zipFilePath, String outputDir, boolean ignorarExistentes, String tipoPago) {
        log.info("Iniciando extraccion de archivo con Comporbantes Bancarios a adjuntar.");
        log.trace("Iniciando Adjuntar archivos CLC.");
        long start = System.currentTimeMillis();
        List<File> arrList = new ArrayList<File>();
        List<AttachResult> result = new ArrayList<AttachResult>();
        try {
            Connection conn = null;
            File dirCLC = new File(outputDir);
            if (!dirCLC.exists() && !dirCLC.mkdirs()) {
                throw new Exception("El directorio [" + outputDir + "] no existe y no se pudo crear. Reporte al administrador del sistema.");
            } else if (!dirCLC.isDirectory()) {
                throw new Exception("La ruta[" + outputDir + "] no corresponde a un directorio. Reporte al administrador del sistema.");
            }
            int extraidos = CLCAttachmentManager.extractZipCLC(zipFilePath, outputDir, arrList);
            log.debug("El archivo [" + zipFilePath + "] contiene [" + extraidos + "] archivos");
            log.trace("Iniciando adjuntamiento de archivos.");
            File[] files = arrList.toArray(new File[arrList.size()]);
            for (int i = 0; i < files.length; i++) {
                try {
                    if (standAlone == 0)
                        conn = getConnection();
                    else
                        conn = Util.getStandAloneConnection();
                    if (conn.getAutoCommit())
                        conn.setAutoCommit(false);
                    String cbFilePath = files[i].getAbsolutePath();
                    createStatements(conn);
                    List<AttachResult> resultProc = CLCAttachmentManager.attachComprobanteBancario(conn, centroContable, ignorarExistentes, cbFilePath, csBuscaCxP, csBuscaGabinete, psInsertFolderHierarchy, psInsertFolder, psNexIDFolder, psSearchFolder, psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, tipoPago);
                    result.addAll(resultProc);
                    conn.commit();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    if (conn != null)
                        try {
                            conn.rollback();
                        } catch (Exception e2) {
                            log.warn("Problemas realizando ROLLBACK " + e2, e2);
                        }
                } finally {
                    if (files[i] != null)
                        if (!files[i].delete())
                            files[i].deleteOnExit();
                    closeStatements();
                    CloseObject.closeObject(conn, false);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        long stop = System.currentTimeMillis();
        log.trace("Terminado Adjuntar archivos CLC en (" + (stop - start) / 1000 + " s.])");
        return result;
    }

    public void attachDocumento(String zipFilePath, String carpetaExtraccion, String nombreCarpeta, String nombreDocumento, boolean ignorarExistentes) {
        log.info("Iniciando extraccion de archivo con Solicitudes de Pago a adjuntar.");
        log.trace("Iniciando Adjuntar archivo a documento.");
        long start = System.currentTimeMillis();
        try {
            List<File> filesList = new ArrayList<File>();
            Connection conn = null;
            File dirCLC = new File(carpetaExtraccion);
            if (!dirCLC.exists() && !dirCLC.mkdirs()) {
                throw new Exception("El directorio [" + carpetaExtraccion + "] no existe y no se pudo crear. Reporte al administrador del sistema.");
            } else if (!dirCLC.isDirectory()) {
                throw new Exception("La ruta[" + carpetaExtraccion + "] no corresponde a un directorio. Reporte al administrador del sistema.");
            }
            int extraidos = CLCAttachmentManager.extractZipCLC(zipFilePath, carpetaExtraccion, filesList);
            log.debug("El archivo [" + zipFilePath + "] contiene [" + extraidos + "] archivos");
            log.trace("Iniciando adjuntamiento de archivos.");
            File[] files = filesList.toArray(new File[filesList.size()]);
            for (int i = 0; i < files.length; i++) {
                try {
                    if (standAlone == 0)
                        conn = getConnection();
                    else
                        conn = Util.getStandAloneConnection();
                    if (conn.getAutoCommit())
                        conn.setAutoCommit(false);
                    String cbFilePath = files[i].getAbsolutePath();
                    createStatements(conn);
                    int adjuntados = CLCAttachmentManager.attachDocumento(conn, ignorarExistentes, cbFilePath, psBuscaPago, psInsertFolderHierarchy, psInsertFolder, psNexIDFolder, psSearchFolder, psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, nombreCarpeta, nombreDocumento);
                    log.info("Se adjuntaron [" + adjuntados + "]");
                    conn.commit();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    if (conn != null)
                        try {
                            conn.rollback();
                        } catch (Exception e2) {
                            log.warn("Problemas realizando ROLLBACK " + e2, e2);
                        }
                } finally {
                    closeStatements();
                    CloseObject.closeObject(conn, false);
                    try {
                        File fZip = new File(zipFilePath);
                        if (!fZip.delete())
                            fZip.deleteOnExit();
                        fZip = null;
                    } catch (Exception e2) {
                        log.warn("Problemas eliminado ZIP origen");
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        long stop = System.currentTimeMillis();
        log.trace("Terminado Adjuntar archivos CLC en (" + (stop - start) / 1000 + " s.])");
    }

    public void attachDocumentoConciliacion(Connection conn, String filePath, String nombreCarpeta, String nombreDocumento, String folioSAI, boolean ignorarExistentes) throws Exception {
        log.trace("Iniciando Adjuntar archivo a documento.");
        long start = System.currentTimeMillis();
        try {
            createStatements(conn);
            int adjuntados = CLCAttachmentManager.attachDocumento(conn, ignorarExistentes, filePath, psBuscaPago, psInsertFolderHierarchy, psInsertFolder, psNexIDFolder, psSearchFolder, psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, nombreCarpeta, nombreDocumento, folioSAI);
            log.info("Se adjuntaron [" + adjuntados + "]");
        } finally {
            closeStatements();
            try {
                File fZip = new File(filePath);
                if (!fZip.delete())
                    fZip.deleteOnExit();
                fZip = null;
            } catch (Exception e2) {
                log.warn("Problemas eliminado ZIP origen");
            }
        }
        long stop = System.currentTimeMillis();
        log.trace("Terminado Adjuntar archivos CLC en (" + (stop - start) / 1000 + " s.])");
    }

    public ProcesoAdjuntaBusinessLogic getProcesoAdjuntaBL() {
        return pabl;
    }

    public void setProcesoAdjuntaBL(ProcesoAdjuntaBusinessLogic pabl) {
        this.pabl = pabl;
    }

    public ProcesoAdjunta getProcesoAdjunta() {
        return pa;
    }

    public void setProcesoAdjunta(ProcesoAdjunta pa) {
        this.pa = pa;
    }

    public List<AttachResult> attachComprobantesBancariosCaja(String centroContable, String zipFilePath, String outputDir, boolean ignorarExistentes) {
        log.info("Iniciando extraccion de archivo con Comporbantes Bancarios para SNP a adjuntar.");
        log.trace("Iniciando Adjuntar comprobantes bancarios.");
        long start = System.currentTimeMillis();
        List<File> arrList = new ArrayList<File>();
        List<AttachResult> result = new ArrayList<AttachResult>();
        try {
            Connection conn = null;
            File dirCLC = new File(outputDir);
            if (!dirCLC.exists() && !dirCLC.mkdirs()) {
                throw new Exception("El directorio [" + outputDir + "] no existe y no se pudo crear. Reporte al administrador del sistema.");
            } else if (!dirCLC.isDirectory()) {
                throw new Exception("La ruta[" + outputDir + "] no corresponde a un directorio. Reporte al administrador del sistema.");
            }
            int extraidos = CLCAttachmentManager.extractZipCLC(zipFilePath, outputDir, arrList);
            log.debug("El archivo [" + zipFilePath + "] contiene [" + extraidos + "] archivos");
            log.trace("Iniciando adjuntamiento de archivos.");
            File[] files = arrList.toArray(new File[arrList.size()]);
            for (int i = 0; i < files.length; i++) {
                try {
                    if (standAlone == 0)
                        conn = getConnection();
                    else
                        conn = Util.getStandAloneConnection();
                    if (conn.getAutoCommit())
                        conn.setAutoCommit(false);
                    String cbFilePath = files[i].getAbsolutePath();
                    createStatements(conn);
                    List<AttachResult> resultProc = CLCAttachmentManager.attachComprobanteBancarioSNP(conn, centroContable, ignorarExistentes, cbFilePath, psBuscaSNP, psBuscaGabineteSNP, psInsertFolderHierarchy, psInsertFolder, psNexIDFolder, psSearchFolder, psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, getProcesoAdjunta());
                    result.addAll(resultProc);
                    conn.commit();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    if (conn != null)
                        try {
                            conn.rollback();
                        } catch (Exception e2) {
                            log.warn("Problemas realizando ROLLBACK " + e2, e2);
                        }
                } finally {
                    if (files[i] != null)
                        if (!files[i].delete())
                            files[i].deleteOnExit();
                    closeStatements();
                    CloseObject.closeObject(conn, false);
                }
            }
            getProcesoAdjunta().setIdEstatus(1);
            getProcesoAdjunta().setResultadoProceso("Proceso terminado.");
            getProcesoAdjuntaBL().actualizaResultado(getProcesoAdjunta());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        long stop = System.currentTimeMillis();
        log.trace("Terminado Adjuntar archivos CLC en (" + (stop - start) / 1000 + " s.])");
        return result;
    }

    public List<AttachResult> attachDocumentoCaja(String centroContable, String zipFilePath, String outputDir, boolean ignorarExistentes, String carpeta, String documento) {
        log.info(String.format("Iniciando extraccion de archivo con documento; %s para anexar a la carpeta: %s en CAJA", documento, carpeta));
        long start = System.currentTimeMillis();
        List<File> arrList = new ArrayList<File>();
        List<AttachResult> result = new ArrayList<AttachResult>();
        try {
            Connection conn = null;
            File dirCLC = new File(outputDir);
            if (!dirCLC.exists() && !dirCLC.mkdirs()) {
                throw new Exception("El directorio [" + outputDir + "] no existe y no se pudo crear. Reporte al administrador del sistema.");
            } else if (!dirCLC.isDirectory()) {
                throw new Exception("La ruta[" + outputDir + "] no corresponde a un directorio. Reporte al administrador del sistema.");
            }
            int extraidos = CLCAttachmentManager.extractZipCLC(zipFilePath, outputDir, arrList);
            log.debug("El archivo [" + zipFilePath + "] contiene [" + extraidos + "] archivos");
            log.trace("Iniciando adjuntamiento de archivos.");
            File[] files = arrList.toArray(new File[arrList.size()]);
            for (int i = 0; i < files.length; i++) {
                try {
                    if (standAlone == 0)
                        conn = getConnection();
                    else
                        conn = Util.getStandAloneConnection();
                    if (conn.getAutoCommit())
                        conn.setAutoCommit(false);
                    String cbFilePath = files[i].getAbsolutePath();
                    createStatements(conn);
                    List<AttachResult> resultProc = CLCAttachmentManager.attachDocumentoSNP(conn, centroContable, ignorarExistentes, cbFilePath, psBuscaSNP, psBuscaGabineteSNP, psInsertFolderHierarchy, psInsertFolder, psNexIDFolder, psSearchFolder, psSearchVolumenUnit, psSearchVolumen, psSearchVolPath, getProcesoAdjunta(), carpeta, documento);
                    result.addAll(resultProc);
                    conn.commit();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    if (conn != null)
                        try {
                            conn.rollback();
                        } catch (Exception e2) {
                            log.warn("Problemas realizando ROLLBACK " + e2, e2);
                        }
                } finally {
                    if (files[i] != null)
                        if (!files[i].delete())
                            files[i].deleteOnExit();
                    closeStatements();
                    CloseObject.closeObject(conn, false);
                }
            }
            getProcesoAdjunta().setIdEstatus(1);
            getProcesoAdjunta().setResultadoProceso("Proceso terminado.");
            getProcesoAdjuntaBL().actualizaResultado(getProcesoAdjunta());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        long stop = System.currentTimeMillis();
        log.trace("Terminado Adjuntar archivos CLC en (" + (stop - start) / 1000 + " s.])");
        return result;
    }
}
