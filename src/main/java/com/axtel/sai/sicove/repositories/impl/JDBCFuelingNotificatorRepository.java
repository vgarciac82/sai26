package com.axtel.sai.sicove.repositories.impl;

import java.sql.Connection;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import com.axtel.sai.sicove.entities.RequestAuthChain;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.axtel.sai.sicove.repositories.FuelingNotificatorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCFuelingNotificatorRepository implements FuelingNotificatorRepository {

    private static final Logger log = LoggerFactory.getLogger(JDBCFuelingNotificatorRepository.class);

    private final QueryRunner runner = new QueryRunner();

    private final ResultSetHandler<RequestAuthChain> resultHandler = new BeanHandler<RequestAuthChain>(RequestAuthChain.class);

    @Override
    public RequestAuthChain getRequestAuthChain(Connection conn, int requestId) throws SicoveException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	wallet_number AS walletNumber, ");
        query.append("		employeeResponsibleName AS applicantName, ");
        query.append("		position AS applicantPosition, ");
        query.append("		employeeMail AS applicantMail, ");
        query.append("		accountResponsibleName AS authorizerName , ");
        query.append("		accountResponsiblePostition	AS authorizerPosition , ");
        query.append("		accountResponsibleMail AS authorizerMail, ");
        query.append("		employee_responsible AS authorizerEmployeeNumber ");
        query.append(" FROM	vFuelingWallet ");
        query.append(" WHERE fueling_request_id = ?");
        log.debug("Object: {}", "Looking for authorization chain for id " + requestId);
        log.trace("Object: {}", "Ejecutando: \n" + query + "\n[" + requestId + "]");
        try {
            RequestAuthChain requestAuthChain = runner.query(conn, query.toString(), resultHandler, requestId);
            return requestAuthChain;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new SicoveException("Error buscando unidad ejecutora: " + e.toString(), e.getCause());
        }
    }
}
