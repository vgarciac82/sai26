package com.axtel.cfdi.stamp.repository;

import java.sql.Connection;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.axtel.cfdi.exceptions.FileManagmentException;
import com.axtel.cfdi.stamp.core.VirtualFile;
import java.util.Base64;

public class VirtualFileRepository implements VirtualFileRepositoryInterface {

    private ResultSetHandler<VirtualFile> resultHandler = new BeanHandler<VirtualFile>(VirtualFile.class);

    private ScalarHandler<Integer> scalarHandler = new ScalarHandler<>();

    private QueryRunner runner = new QueryRunner();

    private static final Logger log = LoggerFactory.getLogger(VirtualFileRepository.class);

    @Override
    public VirtualFile insert(Connection connection, VirtualFile virtualFile) throws FileManagmentException {
        try {
            StringBuilder query = new StringBuilder();
            query.append("INSERT INTO CFDI_FILES(cfdi_id, volumen, nombre_archivo, ruta_archivo, file_type)");
            query.append("VALUES(?, ?, ?, ?, ?)");
            log.info("Object: {}", "Insertando: " + virtualFile);
            runner.insert(connection, query.toString(), scalarHandler, virtualFile.getCfdiId(), virtualFile.getVolumen(), virtualFile.getFileName(), virtualFile.getFilePath().toFile().getAbsolutePath(), virtualFile.getFileType());
            return virtualFile;
        } catch (Exception e) {
            throw new FileManagmentException(e.toString(), e.getCause());
        }
    }

    @Override
    public boolean existsFile(Connection filesConnection, int cfdiId, String fileType) {
        String query = "SELECT COUNT(*) FROM CFDI_FILES WHERE cfdi_id = ? AND file_type = ?";
        try {
            log.info("Verificando existencia del archivo para CFDI_ID: {} y ruta: {}", cfdiId, fileType);
            int count = runner.query(filesConnection, query, scalarHandler, cfdiId, fileType);
            boolean exists = count > 0;
            log.info("Object: {}", "El archivo " + fileType + " para CFDI_ID " + cfdiId + (exists ? "existe" : "no existe"));
            return exists;
        } catch (Exception e) {
            log.error("Error al verificar existencia del archivo: " + e.getMessage(), e);
            throw new RuntimeException("Error al verificar existencia del archivo: " + e.getMessage(), e);
        }
    }

    @Override
    public VirtualFile select(Connection connection, int cfdiId, String type) throws FileManagmentException {
        String query = "SELECT cfdi_id AS cfdiId, volumen, nombre_archivo AS fileName, ruta_archivo AS filePath, file_type AS fileType " + "FROM CFDI_FILES WHERE cfdi_id = ?";
        try {
            log.info("Buscando archivo para CFDI_ID: {}", cfdiId);
            VirtualFile virtualFile = runner.query(connection, query, resultHandler, cfdiId);
            return virtualFile;
        } catch (Exception e) {
            log.error("Error al buscar archivo para CFDI_ID: " + cfdiId, e);
            throw new FileManagmentException("Error al buscar archivo para CFDI_ID: " + cfdiId, e);
        }
    }
}
