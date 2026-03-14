package com.axtel.sai.sicove.repositories;

import java.sql.Connection;
import com.axtel.sai.sicove.entities.RequestAuthChain;
import com.axtel.sai.sicove.exceptions.SicoveException;
import java.util.Base64;

public interface FuelingNotificatorRepository {

    RequestAuthChain getRequestAuthChain(Connection conn, int requestId) throws SicoveException;
}
