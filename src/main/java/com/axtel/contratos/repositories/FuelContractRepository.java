package com.axtel.contratos.repositories;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import com.axtel.contratos.entities.FuelContract;
import com.axtel.contratos.entities.FuelContractAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FuelContractRepository {

    private static final Logger log = LoggerFactory.getLogger(FuelContractRepository.class);

    private final QueryRunner runner = new QueryRunner();

    private final ScalarHandler<BigDecimal> scalarHandler = new ScalarHandler<>();

    private final ResultSetHandler<FuelContract> fuelContractResultHandler = new BeanHandler<FuelContract>(FuelContract.class);

    private final ResultSetHandler<FuelContractAccount> fuelContractAccountResultHandler = new BeanHandler<FuelContractAccount>(FuelContractAccount.class);

    private final ResultSetHandler<List<FuelContractAccount>> fuelContractListHandler = new BeanListHandler<FuelContractAccount>(FuelContractAccount.class);

    private static final String CONTRACT_ACCOUNT_HEADER_SQL;

    static {
        CONTRACT_ACCOUNT_HEADER_SQL = (new StringBuilder("SELECT id_account AS idAccount ").append("      ,id_contract AS idContract ").append("      ,employee_responsible AS employeeResponsible ").append("      ,id_unit AS idUnit ").append("      ,account_number AS accountNumber ").append("      ,registration_date AS registrationDate ").append("      ,user_registration AS userRegistration ").append("      ,monthly_asignation AS monthlyAsignation ").append("  FROM fuel_contract_account WITH(NOLOCK) ")).toString();
    }

    public FuelContract create(Connection conn, FuelContract contract) throws SQLException {
        StringBuilder queryInsert = new StringBuilder();
        queryInsert.append("INSERT INTO fuel_contract(contract_number ,total_max_amount ,active ,employee_registration ,is_by_liters, employee_administrator )");
        queryInsert.append("VALUES (?, ?, ?, ?, ?, ?)");
        if (contract.getId() > 0)
            throw new RuntimeException("El contrato ya cuenta con ID. No puede guardarse");
        BigDecimal newId = runner.insert(conn, queryInsert.toString(), scalarHandler, contract.getContractNumber(), contract.getTotalMaxAmount(), contract.isActive(), contract.getEmployeeRegistration(), contract.isByLiters(), contract.getEmployeeAdministrator());
        contract = readContract(conn, newId.intValue());
        return contract;
    }

    public FuelContract readContract(Connection conn, int id) throws SQLException {
        StringBuilder queryInsert = new StringBuilder();
        queryInsert.append("SELECT id AS id, contract_number AS contractNumber, total_max_amount AS totalMaxAmount,");
        queryInsert.append(" active AS active, registration_date AS registrationDate, employee_registration AS employeeRegistration,");
        queryInsert.append(" is_by_liters AS byLiters FROM fuel_contract WHERE id = ?");
        FuelContract contract = runner.query(conn, queryInsert.toString(), fuelContractResultHandler, id);
        log.debug("Se encontro: " + contract);
        return contract;
    }

    public FuelContract updateContract(Connection conn, FuelContract contract) throws SQLException {
        StringBuilder queryUpdate = new StringBuilder();
        queryUpdate.append("UPDATE fuel_contract  ");
        queryUpdate.append("SET contract_number = ? ");
        queryUpdate.append(",total_max_amount =  ? ");
        queryUpdate.append(",active =  ? ");
        queryUpdate.append(",is_by_liters = ? ");
        queryUpdate.append("WHERE id = ? ");
        int numRowsUpdated = runner.update(conn, queryUpdate.toString(), contract.getContractNumber(), contract.getTotalMaxAmount(), contract.isActive(), contract.isByLiters(), contract.getId());
        log.info("Se actualizo " + numRowsUpdated + " registros de contrato: \n" + contract);
        contract = readContract(conn, contract.getId());
        return contract;
    }

    public boolean delete(Connection conn, FuelContract contract) throws SQLException {
        StringBuilder queryDelete = new StringBuilder("DELETE FROM fuel_contract where id = ?");
        int numRowsDeleted = runner.update(conn, queryDelete.toString(), contract.getId());
        log.info("Se elimino " + numRowsDeleted + " registros del contrato: \n" + contract);
        return true;
    }

    public FuelContractAccount create(Connection conn, FuelContractAccount fuelContractAccount) throws SQLException {
        StringBuilder queryInsert = new StringBuilder();
        queryInsert.append("INSERT INTO fuel_contract_account ");
        queryInsert.append("           (id_contract ");
        queryInsert.append("           ,employee_responsible ");
        queryInsert.append("           ,id_unit ");
        queryInsert.append("           ,account_number ");
        queryInsert.append("           ,user_registration ");
        queryInsert.append("           ,monthly_asignation) ");
        queryInsert.append("     VALUES( ");
        queryInsert.append("           ?, ");
        queryInsert.append("           ?, ");
        queryInsert.append("           ?, ");
        queryInsert.append("           ?, ");
        queryInsert.append("           ?, ");
        queryInsert.append("           ?) ");
        if (fuelContractAccount.getIdAccount() > 0)
            throw new RuntimeException("La cuenta ya existe. No puede guardarse");
        BigDecimal newId = runner.insert(conn, queryInsert.toString(), scalarHandler, fuelContractAccount.getIdContract(), fuelContractAccount.getEmployeeResponsible(), fuelContractAccount.getIdUnit(), fuelContractAccount.getAccountNumber(), fuelContractAccount.getUserRegistration(), fuelContractAccount.getMonthlyAsignation());
        fuelContractAccount = readContractAccount(conn, newId.intValue());
        return fuelContractAccount;
    }

    private FuelContractAccount readContractAccount(Connection conn, int id) throws SQLException {
        StringBuilder query = new StringBuilder(CONTRACT_ACCOUNT_HEADER_SQL);
        query.append(" WHERE  id_account = ?");
        FuelContractAccount contractAccount = runner.query(conn, query.toString(), fuelContractAccountResultHandler, id);
        log.debug("Se encontro: " + contractAccount);
        return contractAccount;
    }

    public List<FuelContractAccount> getExecutiveUnitAccounts(Connection conn, int unitId) throws SQLException {
        StringBuilder query = new StringBuilder(CONTRACT_ACCOUNT_HEADER_SQL);
        query.append(" WHERE  id_unit = ?");
        List<FuelContractAccount> accounts = runner.query(conn, query.toString(), fuelContractListHandler, unitId);
        log.debug("Se encontro: " + accounts);
        return accounts;
    }
}
