package com.syc.contable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import com.syc.contable.core.AdecuacionManager;
import com.syc.contable.core.EstructuraProgramaticaManager;
import com.syc.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class EstructuraProgramaticaBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(EstructuraProgramaticaBusinessLogic.class);

    public EstructuraProgramaticaBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public static ArrayList validaEstructuraProgramatica(Connection conn, String cEPValidar, String aEjercicioFiscal, String cSubcuenta, int iNumRenglon) throws SQLException {
        ArrayList<String> arrMResult = new ArrayList<String>();
        String[] arrayComponentes = cEPValidar.split("\\.");
        int nBloquesEP = arrayComponentes.length;
        int nBloquesCP = 16;
        int i = 0;
        int iExisteAP = 0;
        try {
            i = 1;
            nBloquesCP = EstructuraProgramaticaManager.maxNumEP(conn, aEjercicioFiscal, cSubcuenta);
            if (nBloquesEP == nBloquesCP) {
                while (i - 1 < nBloquesEP) {
                    arrMResult.addAll(EstructuraProgramaticaManager.validaEPDetalle(conn, aEjercicioFiscal, i, arrayComponentes[i - 1], iNumRenglon, cSubcuenta));
                    i++;
                }
                // verifica que exista la EP solo para reducciones
                iExisteAP = EstructuraProgramaticaManager.buscaEPCatalogo(conn, cEPValidar, aEjercicioFiscal, cSubcuenta);
                //if (iExisteAP == 0) {
                //	arrMResult.add("Error: la EP ("+cEPValidar+")secuencia: (" + iNumRenglon + ") Clave Presupuestal No Existe.");
                //}
                arrMResult.add(EstructuraProgramaticaManager.validaRMPOG(conn, cEPValidar, aEjercicioFiscal, cSubcuenta));
                arrMResult.add(EstructuraProgramaticaManager.validaROGTGFF(conn, cEPValidar, aEjercicioFiscal, cSubcuenta));
                arrMResult.add(EstructuraProgramaticaManager.validaRUGFFSFPGAIPP(conn, cEPValidar, aEjercicioFiscal, cSubcuenta));
            } else {
                arrMResult.add("Error: Clave Presupuestal con estructura incorrecta de la secuencia: (" + iNumRenglon + ").");
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return arrMResult;
    }

    public static String validaRMPOG(Connection conn, String cEPValidar, String aEjercicioFiscal, String cSubcuenta) throws SQLException {
        String cMensajeError = "";
        cMensajeError = EstructuraProgramaticaManager.validaRMPOG(conn, cEPValidar, aEjercicioFiscal, cSubcuenta);
        return cMensajeError;
    }

    public static String validaROGTGFF(Connection conn, String cEPValidar, String aEjercicioFiscal, String cSubcuenta) throws SQLException {
        String cMensajeError = "";
        cMensajeError = EstructuraProgramaticaManager.validaROGTGFF(conn, cEPValidar, aEjercicioFiscal, cSubcuenta);
        return cMensajeError;
    }

    public static String validaRUGFFSFPGAIPP(Connection conn, String cEPValidar, String aEjercicioFiscal, String cSubcuenta) throws SQLException {
        String cMensajeError = "";
        cMensajeError = EstructuraProgramaticaManager.validaRUGFFSFPGAIPP(conn, cEPValidar, aEjercicioFiscal, cSubcuenta);
        return cMensajeError;
    }

    public ArrayList validaClaveEP(String cClaveEP, int IterRegElx, String cSubCuenta) throws Exception {
        //String cErrorEP="";
        ArrayList arrMResult = new ArrayList<String>();
        String cEjercicioFiscal = "";
        int iEjercicioFiscal = 0;
        Connection conn = null;
        try {
            conn = getConnection();
            cEjercicioFiscal = AdecuacionManager.obtenEjercicioFiscal(conn);
            iEjercicioFiscal = new Integer(cEjercicioFiscal).intValue();
            iEjercicioFiscal++;
            cEjercicioFiscal = Integer.toString(iEjercicioFiscal);
            //AdecuacionBusinessLogic   adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
            arrMResult.add(validaEstructuraProgramatica(conn, cClaveEP, cEjercicioFiscal, cSubCuenta, 1));
        } finally {
        }
        return arrMResult;
    }

    public String InsertaEP(String cEP, String aEjercicioFiscal, String cSubClave) throws Exception {
        String cMensaje = "";
        Connection conn = null;
        try {
            conn = getConnection();
            EstructuraProgramaticaManager.insertEP(conn, cEP, cSubClave, aEjercicioFiscal);
        } finally {
            if (conn != null) {
                conn.commit();
                conn.close();
            }
            conn = null;
        }
        return cMensaje;
    }

    public String obtenEjercicioFiscal() throws SQLException {
        String cEjercicioFiscal = "";
        Connection conn = null;
        try {
            conn = getConnection();
            cEjercicioFiscal = EstructuraProgramaticaManager.obtenEjercicioFiscal(conn);
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return cEjercicioFiscal;
    }

    public ArrayList buscaERROR() throws SQLException {
        ArrayList buscaERROR = new ArrayList();
        Connection conn = null;
        try {
            conn = getConnection();
            buscaERROR = EstructuraProgramaticaManager.buscaERROR(conn);
        } finally {
            if (conn != null) {
                conn.commit();
                conn.close();
            }
            conn = null;
        }
        return buscaERROR;
    }

    public int InsertaERROREP(ArrayList arrMResult) throws SQLException {
        int vReturn = 0;
        Connection conn = null;
        try {
            conn = getConnection();
            vReturn = EstructuraProgramaticaManager.InsertaERROR(conn, arrMResult);
        } finally {
            if (conn != null) {
                conn.commit();
                conn.close();
            }
            conn = null;
        }
        return vReturn;
    }

    public int iNumeroConfSubcuenta(String aEjercicioFiscal, String cSubcuenta) throws SQLException {
        int nBloquesCP = 0;
        Connection conn = null;
        try {
            conn = getConnection();
            nBloquesCP = EstructuraProgramaticaManager.maxNumEP(conn, aEjercicioFiscal, cSubcuenta);
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return nBloquesCP;
    }
}
