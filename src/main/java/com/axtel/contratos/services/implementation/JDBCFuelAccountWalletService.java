package com.axtel.contratos.services.implementation;

import java.sql.Connection;
import java.sql.SQLException;
import com.axtel.contratos.entities.FuelAccountWallet;
import com.axtel.contratos.exception.ContratoException;
import com.axtel.contratos.repositories.FuelAccountWalletRepository;
import com.axtel.contratos.services.FuelAccountWalletService;
import com.syc.cfdi.db.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCFuelAccountWalletService extends DataSourceManager implements FuelAccountWalletService {

    private static final Logger log = LoggerFactory.getLogger(JDBCFuelAccountWalletService.class);

    private FuelAccountWalletRepository accountWalletRepository;

    public JDBCFuelAccountWalletService(FuelAccountWalletRepository accountWalletRepository, String jniName) {
        super.init(jniName);
        this.accountWalletRepository = accountWalletRepository;
    }

    @Override
    public FuelAccountWallet insert(FuelAccountWallet fuelAccountWallet) throws ContratoException {
        Connection conn = null;
        try {
            conn = getConnection();
            accountWalletRepository.insert(conn, fuelAccountWallet);
            conn.commit();
            return fuelAccountWallet;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn(e2.getMessage(), e2);
                }
            throw new ContratoException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public FuelAccountWallet readAccountWallet(int id) throws ContratoException {
        Connection conn = null;
        try {
            conn = getConnection();
            return accountWalletRepository.readAccountWallet(conn, id);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new ContratoException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public FuelAccountWallet update(FuelAccountWallet fuelAccountWallet) throws ContratoException {
        Connection conn = null;
        try {
            conn = getConnection();
            accountWalletRepository.update(conn, fuelAccountWallet);
            conn.commit();
            return fuelAccountWallet;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn(e2.getMessage(), e2);
                }
            throw new ContratoException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
