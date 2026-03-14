package com.axtel.sisecop.services;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.axtel.sai.sicove.expedient.repositories.ExpedientRepository;
import com.axtel.sisecop.entities.ProyectoServicioTDR;
import com.axtel.sisecop.repostories.ProjectProcurementProcessRepository;
import com.syc.cfdi.db.CloseObject;
import com.syc.dsmngr.DataSourceManager;
import com.syc.fortimax.core.Fortimax;
import com.syc.fortimax.exceptions.FortimaxException;
import com.syc.gestion.util.Util;
import java.util.Base64;

public class ProjectProcurementProcessService extends DataSourceManager {

    private ProjectProcurementProcessRepository projectPPRepository;

    private ExpedientRepository expedientRepository;

    public ProjectProcurementProcessService(String jniName) {
        super.init(jniName);
        this.projectPPRepository = new ProjectProcurementProcessRepository();
    }

    public ProyectoServicioTDR addProjectTDR(int idProcess, int servicioID, String fileDescription, String folderName, File file, String userName) throws SQLException, FortimaxException {
        Connection conn = null;
        Fortimax fmx = null;
        try {
            conn = getConnection();
            fmx = expedientRepository.saveDocument(conn, idProcess, folderName, userName, file.getName(), file);
            ProyectoServicioTDR projectTDR = new ProyectoServicioTDR();
            projectTDR.setTdrArchivo(fileDescription);
            projectTDR.setTdrRuta(fmx.toString());
            projectTDR = projectPPRepository.createServicioTermino(conn, servicioID, projectTDR);
            conn.commit();
            return projectTDR;
        } catch (SQLException e) {
            Util.rollback(conn);
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void deleteProjectTDR(int idTDR) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            projectPPRepository.deleteServicioTermino(connection, idTDR);
            connection.commit();
        } catch (SQLException e) {
            Util.rollback(connection);
            throw e;
        } finally {
            CloseObject.closeObject(connection);
        }
    }

    public List<ProyectoServicioTDR> readByProjectID(int projectID) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            return projectPPRepository.readByServicioId(connection, projectID);
        } finally {
            CloseObject.closeObject(connection);
        }
    }

    public ExpedientRepository getExpedientRepository() {
        return expedientRepository;
    }

    public void setExpedientRepository(ExpedientRepository expedientRepository) {
        this.expedientRepository = expedientRepository;
    }
}
