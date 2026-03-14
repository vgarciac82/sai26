package com.axtel.sai.sicove.expedient.repositories.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang.StringUtils;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.axtel.sai.sicove.expedient.entities.DocumentFortimax;
import com.axtel.sai.sicove.expedient.repositories.ExpedientRepository;
import com.syc.fortimax.core.Carpeta;
import com.syc.fortimax.core.CarpetaManager;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.fortimax.core.Fortimax;
import com.syc.fortimax.core.FortimaxManager;
import com.syc.fortimax.core.OrgCarpeta;
import com.syc.fortimax.core.OrgCarpetaManager;
import com.syc.fortimax.core.PaginaManager;
import com.syc.fortimax.exceptions.FortimaxException;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class JDBCExpedientRepostory implements ExpedientRepository {

    private static final Logger log = LoggerFactory.getLogger(JDBCExpedientRepostory.class);

    private final QueryRunner run = new QueryRunner();

    private final ResultSetHandler<List<DocumentFortimax>> resultListHandler = new BeanListHandler<DocumentFortimax>(DocumentFortimax.class);

    private final StringBuilder queryExpedientBase = new StringBuilder();

    public JDBCExpedientRepostory() {
        initQueries();
    }

    private void initQueries() {
        queryExpedientBase.append("SELECT tc.tc_gaveta_asociada + '_G' ");
        queryExpedientBase.append("       + CONVERT(VARCHAR(32), c.c_id_gabinete) + 'C' ");
        queryExpedientBase.append("       + CONVERT(VARCHAR(32), folder.id_carpeta) ");
        queryExpedientBase.append("       + 'D' + CONVERT(VARCHAR(32), doc.id_documento) AS fortimax, ");
        queryExpedientBase.append("       folder.nombre_carpeta + '/' + nombre_documento AS documentPath, ");
        queryExpedientBase.append("       nombre_documento AS documentName, ");
        queryExpedientBase.append("       CASE ");
        queryExpedientBase.append("         WHEN pag.numero_pagina IS NULL THEN '0' ");
        queryExpedientBase.append("         ELSE '1' ");
        queryExpedientBase.append("       END                                            AS attached ");
        queryExpedientBase.append("FROM   cg_caso c ");
        queryExpedientBase.append("       INNER JOIN cg_tipo_caso tc ");
        queryExpedientBase.append("               ON c.id_tc = tc.id_tc ");
        queryExpedientBase.append("       INNER JOIN imx_carpeta folder ");
        queryExpedientBase.append("               ON folder.titulo_aplicacion = tc.tc_gaveta_asociada ");
        queryExpedientBase.append("                  AND folder.id_gabinete = c.c_id_gabinete ");
        queryExpedientBase.append("       INNER JOIN imx_documento doc ");
        queryExpedientBase.append("               ON folder.titulo_aplicacion = doc.titulo_aplicacion ");
        queryExpedientBase.append("                  AND folder.id_gabinete = doc.id_gabinete ");
        queryExpedientBase.append("                  AND folder.id_carpeta = doc.id_carpeta_padre ");
    }

    @Override
    public List<DocumentFortimax> getExpedientDocuments(Connection conn, int idProcess, String folderExclude) throws SicoveException {
        log.info("Object: {}", "Searching expedient for process " + idProcess);
        StringBuilder query = new StringBuilder(queryExpedientBase);
        query.append("       LEFT JOIN imx_pagina pag ");
        query.append("              ON doc.titulo_aplicacion = pag.titulo_aplicacion ");
        query.append("                 AND doc.id_gabinete = pag.id_gabinete ");
        query.append("                 AND doc.id_carpeta_padre = pag.id_carpeta_padre ");
        query.append("                 AND doc.id_documento = pag.id_documento ");
        query.append("WHERE  folder.bandera_raiz <> 'S' ");
        query.append("       AND c.id_caso = ? ");
        query.append("       AND doc.iEsVersion = 0 ");
        if (!StringUtils.isEmpty(folderExclude))
            query.append("       AND folder.nombre_carpeta <> ? ");
        List<DocumentFortimax> documents;
        try {
            log.trace("Object: {}", "Executing: \n" + query + "\n[" + idProcess + "]" + "\n[" + StringUtils.trimToEmpty(folderExclude) + "]");
            if (!StringUtils.isEmpty(folderExclude))
                documents = run.query(conn, query.toString(), resultListHandler, idProcess, folderExclude);
            else
                documents = run.query(conn, query.toString(), resultListHandler, idProcess);
            log.debug("Object: {}", "Retriving " + documents);
            return documents;
        } catch (SQLException e) {
            throw new SicoveException(e);
        }
    }

    @Override
    public List<DocumentFortimax> getExpedientCapturedDocuments(Connection conn, int idProcess, String folderExclude) throws SicoveException {
        log.info("Object: {}", "Searching expedient for process " + idProcess);
        StringBuilder query = new StringBuilder(queryExpedientBase);
        query.append("       INNER JOIN imx_pagina pag ");
        query.append("              ON doc.titulo_aplicacion = pag.titulo_aplicacion ");
        query.append("                 AND doc.id_gabinete = pag.id_gabinete ");
        query.append("                 AND doc.id_carpeta_padre = pag.id_carpeta_padre ");
        query.append("                 AND doc.id_documento = pag.id_documento ");
        query.append("WHERE  folder.bandera_raiz <> 'S' ");
        query.append("       AND c.id_caso = ? ");
        if (!StringUtils.isEmpty(folderExclude))
            query.append("       AND folder.nombre_carpeta <> ? ");
        List<DocumentFortimax> documents;
        try {
            log.trace("Object: {}", "Executing: \n" + query + "\n[" + idProcess + "]");
            if (!StringUtils.isEmpty(folderExclude))
                documents = run.query(conn, query.toString(), resultListHandler, idProcess, folderExclude);
            else
                documents = run.query(conn, query.toString(), resultListHandler, idProcess);
            log.debug("Object: {}", "Retriving " + documents);
            return documents;
        } catch (SQLException e) {
            throw new SicoveException(e);
        }
    }

    @Override
    public Fortimax saveDocument(Connection conn, int idProcess, String folderName, String userName, String documentName, File file) throws FortimaxException {
        try {
            Fortimax fmx = null;
            Caso process = CasoManager.select(conn, idProcess);
            Carpeta parentFolder = CarpetaManager.getCarpetaByName(conn, process.getTipoCaso().getGavetaAsociada(), process.getIdGabinete(), folderName);
            if (parentFolder == null)
                parentFolder = createFolder(conn, process, folderName, userName);
            Documento document = DocumentoManager.buscaDocumento(conn, process.getTipoCaso().getGavetaAsociada(), process.getIdGabinete(), parentFolder.getIdCarpeta(), documentName);
            if (document == null) {
                document = DocumentoManager.creaDocumento(conn, parentFolder, documentName, Util.getFileExtencion(file.getName()), userName);
                document.setExtension(Util.getFileExtencion(file.getName()));
                fmx = FortimaxManager.saveFile(conn, document, file);
            } else
                fmx = new Fortimax(document.getTituloAplicacion(), document.getIdGabinete(), document.getIdCarpetaPadre(), document.getIdDocumento());
            return fmx;
        } catch (Exception e) {
            throw new FortimaxException(e);
        }
    }

    @Override
    public Fortimax deleteDocument(Connection conn, int idProcess, String folderName, String userName, String documentName) throws FortimaxException {
        try {
            Fortimax fmx = null;
            Caso process = CasoManager.select(conn, idProcess);
            Carpeta parentFolder = CarpetaManager.getCarpetaByName(conn, process.getTipoCaso().getGavetaAsociada(), process.getIdGabinete(), folderName);
            if (parentFolder == null)
                throw new FortimaxException("No se encontro folder con el nombre: " + folderName);
            parentFolder = createFolder(conn, process, folderName, userName);
            Documento document = DocumentoManager.buscaDocumento(conn, process.getTipoCaso().getGavetaAsociada(), process.getIdGabinete(), parentFolder.getIdCarpeta(), documentName);
            if (document != null) {
                File page = null;
                if (document.getPaginasDocumento() != null && document.getFullPathFilesNames().length > 0)
                    page = new File(document.getFullPathFilesNames()[0]);
                PaginaManager.delete(conn, process.getTipoCaso().getGavetaAsociada(), process.getIdGabinete(), parentFolder.getIdCarpeta(), document.getIdDocumento());
                DocumentoManager.delete(conn, process.getTipoCaso().getGavetaAsociada(), process.getIdGabinete(), parentFolder.getIdCarpeta(), document.getIdDocumento());
                if (page != null && !page.delete())
                    page.deleteOnExit();
            } else
                throw new FortimaxException("No se encontro documento a eliminar con nomber : " + documentName + " en el folder: " + folderName + " En el proceso: " + idProcess);
            return fmx;
        } catch (Exception e) {
            throw new FortimaxException(e);
        }
    }

    private Carpeta createFolder(Connection conn, Caso process, String folderName, String userName) throws SQLException, FortimaxException {
        String processName = process.getTipoCaso().getGavetaAsociada();
        Carpeta folder = new Carpeta();
        folder.setTituloAplicacion(processName);
        folder.setIdGabinete(process.getIdGabinete());
        folder.setIdCarpeta(CarpetaManager.getNextIdCarpeta(conn, processName, process.getIdGabinete()));
        folder.setNombreCarpeta(folderName);
        folder.setNombreUsuario(userName);
        folder.setBanderaRaiz("N");
        folder.setDescripcion(folderName);
        folder.setPassword("-1");
        folder = CarpetaManager.insertaCarpeta(conn, folder);
        OrgCarpeta oc = new OrgCarpeta();
        oc.setIdCarpetaHija(folder.getIdCarpeta());
        oc.setIdCarpetaPadre(0);
        oc.setIdGabinete(process.getIdGabinete());
        oc.setNombreHija(folderName);
        oc.setTituloAplicacion(processName);
        OrgCarpetaManager.insert(conn, oc);
        log.trace("Object: {}", "Folder [" + folderName + "] created successfully");
        return folder;
    }
}
