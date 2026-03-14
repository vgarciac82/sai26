package com.axtel.contratos.services.implementation;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.axtel.contratos.entities.FuelContract;
import com.axtel.contratos.entities.FuelContractAccount;
import com.axtel.contratos.repositories.FuelContractRepository;
import com.axtel.contratos.services.FuelContractService;
import com.syc.cfdi.db.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class FuelContractServiceImplementation extends DataSourceManager implements FuelContractService {

    private static final Logger log = LoggerFactory.getLogger(FuelContractServiceImplementation.class);

    private final FuelContractRepository fuelContractRepository = new FuelContractRepository();

    public FuelContractServiceImplementation(String jniName) {
        super.init(jniName);
    }

    @Override
    public FuelContract createContract(FuelContract fuelContract) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            fuelContract = fuelContractRepository.create(conn, fuelContract);
            conn.commit();
            return fuelContract;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public FuelContract readContract(int idContract) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            FuelContract fuelContract = fuelContractRepository.readContract(conn, idContract);
            return fuelContract;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public FuelContract updateContract(int idContract, FuelContract fuelContract) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            fuelContract = fuelContractRepository.updateContract(conn, fuelContract);
            conn.commit();
            return fuelContract;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public boolean deleteContract(FuelContract fuelContract) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            boolean deleted = fuelContractRepository.delete(conn, fuelContract);
            conn.commit();
            return deleted;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public FuelContractAccount createContractAccount(FuelContractAccount fuelContractAccount) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            fuelContractAccount = fuelContractRepository.create(conn, fuelContractAccount);
            conn.commit();
            return fuelContractAccount;
        } catch (SQLException e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public List<FuelContractAccount> getAccounts(int unitId) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            List<FuelContractAccount> accounts = fuelContractRepository.getExecutiveUnitAccounts(conn, unitId);
            return accounts;
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
