package com.axtel.sai.sicove.expedient.services.impl;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.axtel.sai.sicove.expedient.entities.DocumentFortimax;
import com.axtel.sai.sicove.expedient.repositories.ExpedientRepository;
import com.axtel.sai.sicove.expedient.services.ExpedientService;
import com.jenkov.prizetags.tree.itf.ITree;
import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.syc.cfdi.db.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.fortimax.core.Fortimax;
import com.syc.fortimax.core.Pagina;
import com.syc.fortimax.core.PaginaManager;
import com.syc.fortimax.exceptions.FortimaxException;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCExpedientService extends DataSourceManager implements ExpedientService {

    private static final Logger log = LoggerFactory.getLogger(JDBCExpedientService.class);

    private ExpedientRepository expedientRepository;

    private CasoBusinessLogic processService;

    public JDBCExpedientService(String jniName, ExpedientRepository expedientRepository) {
        super.init(jniName);
        this.expedientRepository = expedientRepository;
        this.processService = new CasoBusinessLogic(jniName);
    }

    @Override
    public List<DocumentFortimax> getExpedientDocuments(int idProcess, String folderExclude) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            return expedientRepository.getExpedientDocuments(conn, idProcess, folderExclude);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new SicoveException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public void saveDocument(File file, String documentSelect, int idProcess) throws SicoveException {
        try {
            Fortimax fimx = new Fortimax(documentSelect);
            Caso process = processService.getCaso(idProcess);
            ITree tree = processService.getArbolCaso(process);
            ITreeNode node = tree.findNode(documentSelect);
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            processService.recibeDocumentoGestion(process, fimx.getIdCarpeta(), node.getName(), Util.getFileExtencion(file.getName()), dis, false);
            dis.close();
        } catch (GestionException | IOException e) {
            throw new SicoveException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public List<DocumentFortimax> getExpedientCapturedDocuments(int idProcess, String folderExclude) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            return expedientRepository.getExpedientCapturedDocuments(conn, idProcess, folderExclude);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new SicoveException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public void deleteDocument(Fortimax fortimax, boolean cleanOnly) throws FortimaxException {
        Connection conn = null;
        try {
            conn = getConnection();
            Documento document = DocumentoManager.selectDocumento(conn, fortimax.getTituloAplicacion(), fortimax.getIdGabinete(), fortimax.getIdCarpeta(), fortimax.getIdDocumento());
            for (Pagina pagina : document.getPaginasDocumento()) {
                PaginaManager.deletePagina(conn, pagina);
            }
            if (!cleanOnly)
                DocumentoManager.delete(conn, fortimax.getTituloAplicacion(), fortimax.getIdGabinete(), fortimax.getIdCarpeta(), fortimax.getIdDocumento());
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new FortimaxException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
