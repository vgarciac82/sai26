package com.axtel.egresos.repositories;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Base64;

public interface GreenMexRepository {

    boolean insertaComprobacion(Connection conn, int folioComprobacion, BigDecimal impEjercer, String folioING, String remanenteING, String fecha) throws Exception;
}
