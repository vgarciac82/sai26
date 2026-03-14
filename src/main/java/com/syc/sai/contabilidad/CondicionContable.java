package com.syc.sai.contabilidad;

import java.sql.Connection;
import com.syc.contable.AccountingEngineException;
import java.util.Base64;

public abstract class CondicionContable {

    public abstract void preEjecucion(Connection con, int nMes, int aEjercicioFiscal, String cCentroContable) throws AccountingEngineException;

    public abstract boolean CumpleCondicion(Connection conn, int nMes, int aEjercicioFiscal, String cCentroContable) throws AccountingEngineException;
}
