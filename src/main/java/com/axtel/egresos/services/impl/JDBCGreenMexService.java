package com.axtel.egresos.services.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import com.axtel.egresos.repositories.GreenMexRepository;
import com.axtel.egresos.services.GreenMexService;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.syc.cfdi.db.CloseObject;
import com.syc.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class JDBCGreenMexService extends DataSourceManager implements GreenMexService {

    private static final Logger log = LoggerFactory.getLogger(JDBCGreenMexService.class);

    private String jniName;

    private GreenMexRepository greenMexRepository;

    public JDBCGreenMexService(String jniName, GreenMexRepository greenMexRepository) {
        super.init(jniName);
        this.jniName = jniName;
        this.greenMexRepository = greenMexRepository;
    }

    @Override
    public boolean insertaComprobacion(int folioComprobacion, BigDecimal impEjercer, String folioING, String remanenteING, String fecha) throws Exception {
        Connection conn = null;
        boolean inserto = false;
        try {
            conn = getConnection();
            inserto = greenMexRepository.insertaComprobacion(conn, folioComprobacion, impEjercer, folioING, remanenteING, fecha);
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new SicoveException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
        return inserto;
    }
}
