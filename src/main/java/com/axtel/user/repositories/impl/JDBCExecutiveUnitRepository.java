package com.axtel.user.repositories.impl;

import java.sql.Connection;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import com.axtel.user.UserException;
import com.axtel.user.entities.ExecutiveUnit;
import com.axtel.user.repositories.ExecutiveUnitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCExecutiveUnitRepository implements ExecutiveUnitRepository {

    private static final Logger log = LoggerFactory.getLogger(JDBCExecutiveUnitRepository.class);

    private final QueryRunner run = new QueryRunner();

    private final ResultSetHandler<ExecutiveUnit> resultHandler = new BeanHandler<ExecutiveUnit>(ExecutiveUnit.class);

    @Override
    public ExecutiveUnit getExecutiveUnitByAU(Connection conn, String administrativeUnit) throws UserException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	executiveUnit.nidUnidadAdmin AS idExecutiveUnit,  ");
        query.append("		executiveUnit.c_Empresa AS idCompany,  ");
        query.append("		executiveUnit.nActivo AS active,  ");
        query.append("		executiveUnit.cUejecutora AS executiveUnit,  ");
        query.append("		executiveUnit.cUadministrativa AS administrativeUnit, ");
        query.append("		executiveUnit.cDescripcion AS executiveUnitName,  ");
        query.append("		executiveUnit.cUresponsable AS responsibleUnit,  ");
        query.append("		executiveUnit.c_coordinacion AS coordinatorUnit,  ");
        query.append("		executiveUnit.cDescCoortaCordinacion AS executiveUnitStands,  ");
        query.append("		executiveUnit.c_centrotrab AS workplace,  ");
        query.append("		executiveUnit.c_entidad AS state,  ");
        query.append("		executiveUnit.cPrograma AS budgetProgram, ");
        query.append("		executiveUnit.dUadministrativa AS administrativeUnitName,  ");
        query.append("		executiveUnit.c_municipio AS town, ");
        query.append("		executiveUnit.nCC_Control AS areaControl,  ");
        query.append("		executiveUnit.cCentroResp AS responsibleCenter  ");
        query.append("  FROM	nom_unidad_ejecutora AS executiveUnit ");
        query.append(" WHERE executiveUnit.cUejecutora = ? ");
        log.debug("Object: {}", "Buscando unidad ejecutora." + administrativeUnit);
        log.trace("Object: {}", "Ejecutando: \n" + query + "\n[" + administrativeUnit + "]");
        ExecutiveUnit eu;
        try {
            eu = run.query(conn, query.toString(), resultHandler, administrativeUnit);
            return eu;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new UserException("Error buscando unidad ejecutora: " + e.toString(), e.getCause());
        }
    }
}
