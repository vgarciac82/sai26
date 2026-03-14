package com.axtel.egresos.services;

import java.math.BigDecimal;
import java.util.Base64;

public interface GreenMexService {

    boolean insertaComprobacion(int folioComprobacion, BigDecimal impEjercer, String folioING, String remanenteING, String fecha) throws Exception;
}
