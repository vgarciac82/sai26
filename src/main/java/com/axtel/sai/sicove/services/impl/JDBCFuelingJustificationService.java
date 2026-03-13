package com.axtel.sai.sicove.services.impl;

import java.sql.Connection;
import com.axtel.sai.sicove.entities.FuelingJustification;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.axtel.sai.sicove.repositories.FuelingJustificationRepository;
import com.axtel.sai.sicove.services.FuelingJustificationService;
import com.syc.cfdi.db.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCFuelingJustificationService extends DataSourceManager implements FuelingJustificationService {

    private static final Logger log = LoggerFactory.getLogger(JDBCFuelingJustificationService.class);

    public FuelingJustificationRepository fuelingJustificationRepository;

    public JDBCFuelingJustificationService(String jniName, FuelingJustificationRepository fuelingJustificationRepository) {
        super.init(jniName);
        this.fuelingJustificationRepository = fuelingJustificationRepository;
    }

    @Override
    public FuelingJustification saveFuelingJustification(FuelingJustification fuelingJustification) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            fuelingJustificationRepository.saveFuelingJustification(conn, fuelingJustification);
            conn.commit();
            return fuelingJustification;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn(e2.getMessage(), e2);
                }
            throw new SicoveException(e.getMessage(), e.getCause());
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public FuelingJustification readFuelingJustification(int id) throws SicoveException {
        return null;
    }

    @Override
    public FuelingJustification updateFuelingJustification(FuelingJustification fuelingJustificationOrg) throws SicoveException {
        return null;
    }
}
