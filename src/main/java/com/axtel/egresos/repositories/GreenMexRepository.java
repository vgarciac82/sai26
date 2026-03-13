package com.axtel.egresos.repositories;

import java.math.BigDecimal;
import java.sql.Connection;

public interface GreenMexRepository {

	boolean insertaComprobacion( Connection conn, int folioComprobacion, BigDecimal impEjercer, String folioING, String remanenteING, String fecha ) throws Exception;

}

