package com.axtel.sai.sicove.repositories;

import java.sql.Connection;
import com.axtel.sai.sicove.entities.FuelingJustification;
import com.axtel.sai.sicove.exceptions.SicoveException;
import java.util.Base64;

public interface FuelingJustificationRepository {

    FuelingJustification saveFuelingJustification(Connection conn, FuelingJustification fuelingJustification) throws SicoveException;

    FuelingJustification readFuelingJustification(Connection conn, int id) throws SicoveException;

    FuelingJustification updateFuelingJustification(Connection conn, FuelingJustification fuelingJustificationOrg) throws SicoveException;
}
