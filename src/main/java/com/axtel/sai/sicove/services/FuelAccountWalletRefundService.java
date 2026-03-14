package com.axtel.sai.sicove.services;

import com.axtel.sai.sicove.entities.FuelAccountWalletRefund;
import com.axtel.sai.sicove.exceptions.SicoveException;
import java.util.Base64;

public interface FuelAccountWalletRefundService {

    FuelAccountWalletRefund create(FuelAccountWalletRefund fuelAccountWalletRefund) throws SicoveException;

    FuelAccountWalletRefund read(Integer idRefund) throws SicoveException;

    FuelAccountWalletRefund update(Integer idRefund, FuelAccountWalletRefund fuelAccountWalletRefund) throws SicoveException;

    void delete(Integer idRefund) throws SicoveException;
}
