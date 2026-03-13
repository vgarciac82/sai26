package com.axtel.sai.sicove.repositories.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import com.axtel.sai.sicove.entities.FuelingJustification;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.axtel.sai.sicove.repositories.FuelingJustificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCFuelingJustificationRepository implements FuelingJustificationRepository {

    private static final Logger log = LoggerFactory.getLogger(JDBCFuelingJustificationRepository.class);

    private final QueryRunner runner = new QueryRunner();

    private final ResultSetHandler<FuelingJustification> fuelingJustificationRepositoryHandler = new BeanHandler<FuelingJustification>(FuelingJustification.class);

    private final ScalarHandler<BigDecimal> scalarHandler = new ScalarHandler<>();

    @Override
    public FuelingJustification saveFuelingJustification(Connection conn, FuelingJustification fuelingJustification) throws SicoveException {
        log.info("Saving: " + fuelingJustification);
        StringBuilder insertJustificationSql = new StringBuilder("INSERT INTO justification (is_justification, id_commision, justification_text, initial_date, end_date, country_id, state_name, municipality_name)");
        insertJustificationSql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        if (fuelingJustification.getJustificationId() > 0)
            throw new RuntimeException("La justificacion ya cuenta con ID. No puede guardarse");
        BigDecimal newId;
        try {
            newId = runner.insert(conn, insertJustificationSql.toString(), scalarHandler, fuelingJustification.isWithJustification(), fuelingJustification.getIdCommision(), fuelingJustification.getJustification(), fuelingJustification.getInitialDate(), fuelingJustification.getEndDate(), fuelingJustification.getCountryId(), fuelingJustification.getStateName(), fuelingJustification.getMunicipalityName());
        } catch (SQLException e) {
            throw new SicoveException(e);
        }
        fuelingJustification = readFuelingJustification(conn, newId.intValue());
        return fuelingJustification;
    }

    @Override
    public FuelingJustification readFuelingJustification(Connection conn, int id) throws SicoveException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT justification_id AS justificationId ");
        query.append("      ,is_justification AS withJustification ");
        query.append("      ,id_commision AS idCommision ");
        query.append("      ,justification_text AS justification ");
        query.append("      ,initial_date AS initialDate ");
        query.append("      ,end_date AS endDate ");
        query.append("      ,country_id AS countryId ");
        query.append("      ,state_name AS stateName ");
        query.append("      ,municipality_name AS municipalityName ");
        query.append("  FROM justification WITH(NOLOCK) ");
        query.append(" WHERE justification_id = ? ");
        FuelingJustification fuelingJustification;
        try {
            fuelingJustification = runner.query(conn, query.toString(), fuelingJustificationRepositoryHandler, id);
            log.debug("Se encontro: " + fuelingJustification);
            return fuelingJustification;
        } catch (SQLException e) {
            throw new SicoveException(e);
        }
    }

    @Override
    public FuelingJustification updateFuelingJustification(Connection conn, FuelingJustification fuelingJustification) throws SicoveException {
        log.info("Updating: " + fuelingJustification);
        StringBuilder query = new StringBuilder();
        query.append("UPDATE	justification ");
        query.append("   SET	is_justification = ? ");
        query.append("		,country_id = ? ");
        query.append("		,end_date = ? ");
        query.append("		,id_commision = ? ");
        query.append("		,initial_date = ? ");
        query.append("		,justification_text = ? ");
        query.append("		,municipality_name = ? ");
        query.append("		,state_name = ? ");
        query.append(" WHERE	justification_id = ? ");
        try {
            runner.update(conn, query.toString(), fuelingJustification.isWithJustification(), fuelingJustification.getCountryId(), fuelingJustification.getEndDate(), fuelingJustification.getIdCommision(), fuelingJustification.getInitialDate(), fuelingJustification.getJustification(), fuelingJustification.getMunicipalityName(), fuelingJustification.getStateName(), fuelingJustification.getJustificationId());
        } catch (SQLException e) {
            throw new SicoveException(e);
        }
        fuelingJustification = readFuelingJustification(conn, fuelingJustification.getJustificationId());
        return fuelingJustification;
    }
}
