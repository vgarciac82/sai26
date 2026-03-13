package com.axtel.egresos.services;

import java.math.BigDecimal;

public interface GreenMexService {
	
	boolean insertaComprobacion( int folioComprobacion, BigDecimal impEjercer, String folioING, String remanenteING, String fecha ) throws Exception; 

}

