package com.axtel.sisecop.services;

import java.sql.Connection;
import java.sql.SQLException;
import com.axtel.sisecop.dto.ProyectoServicioActividadDTO;
import com.axtel.sisecop.entities.ProyectoServicioActividad;
import com.axtel.sisecop.repostories.ProjectActivityRepository;
import com.syc.cfdi.db.CloseObject;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ProjectActivityService extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(ProjectActivityService.class);

    private ProjectActivityRepository projectActivityRepository = new ProjectActivityRepository();

    public ProjectActivityService(String jniName) {
        init(jniName);
    }

    public ProyectoServicioActividad createActivity(ProyectoServicioActividadDTO activityDTO) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            ProyectoServicioActividad activity = projectActivityRepository.insertProyectoServicioActividad(conn, activityDTO);
            conn.commit();
            return activity;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Util.rollback(conn);
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void deleteActivity(int id) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            projectActivityRepository.deleteProyectoServicioActividad(conn, id);
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Util.rollback(conn);
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
