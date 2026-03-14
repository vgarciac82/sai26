package com.axtel.sai.sicove.services.impl;

import java.sql.Connection;
import com.axtel.sai.sicove.entities.FuelAccountWalletRefund;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.axtel.sai.sicove.repositories.FuelAccountWalletRefundRepository;
import com.axtel.sai.sicove.services.FuelAccountWalletRefundService;
import com.syc.cfdi.db.CloseObject;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import java.util.Base64;

public class JDBCFuelAccountWalletRefundService extends DataSourceManager implements FuelAccountWalletRefundService {

    private final FuelAccountWalletRefundRepository fuelAccountWalletRefundRepository;

    public JDBCFuelAccountWalletRefundService(String jniName, FuelAccountWalletRefundRepository fuelAccountWalletRefundRepository) {
        super.init(jniName);
        this.fuelAccountWalletRefundRepository = fuelAccountWalletRefundRepository;
    }

    @Override
    public FuelAccountWalletRefund create(FuelAccountWalletRefund fuelAccountWalletRefund) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            fuelAccountWalletRefund = fuelAccountWalletRefundRepository.create(conn, fuelAccountWalletRefund);
            conn.commit();
            return fuelAccountWalletRefund;
        } catch (Exception e) {
            Util.rollback(conn);
            throw new SicoveException("Problemas insertando FuelAccountWalletRefund: " + e.toString(), e.getCause());
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public FuelAccountWalletRefund read(Integer idRefund) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            FuelAccountWalletRefund fuelAccountWalletRefund = fuelAccountWalletRefundRepository.read(conn, idRefund);
            return fuelAccountWalletRefund;
        } catch (Exception e) {
            throw new SicoveException("Problemas buscando FuelAccountWalletRefund: " + e.toString(), e.getCause());
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public FuelAccountWalletRefund update(Integer idRefund, FuelAccountWalletRefund fuelAccountWalletRefund) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            fuelAccountWalletRefund = fuelAccountWalletRefundRepository.update(conn, idRefund, fuelAccountWalletRefund);
            conn.commit();
            return fuelAccountWalletRefund;
        } catch (Exception e) {
            Util.rollback(conn);
            throw new SicoveException("Problemas actualizando FuelAccountWalletRefund: " + e.toString(), e.getCause());
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public void delete(Integer idRefund) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            fuelAccountWalletRefundRepository.delete(conn, idRefund);
            conn.commit();
        } catch (Exception e) {
            Util.rollback(conn);
            throw new SicoveException("Problemas borrando FuelAccountWalletRefund: " + e.toString(), e.getCause());
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
