package com.syc.reportes.servlet;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class conciliaBancosBusinessLogic extends DataSourceManager {

    public conciliaBancosBusinessLogic(String jndiName) {
        super.init(jndiName);
    }

    public static void GeneraConciliaAut(Connection conn, int idConciliacion, String strCuenta, int nMes) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_l_ConciliaAuto( ?, ?, ?)}";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, idConciliacion);
            cs.setString(2, strCuenta);
            cs.setInt(3, nMes);
            rs = cs.executeQuery();
            if (rs.next()) {
                System.out.println("Se proceso la conciliacion automatica.");
            } else {
                throw new Exception("No se proceso la conciliacion automatica. Notifique al administrador");
            }
            conn.commit();
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(cs);
            CloseObject.closeObject(conn);
            System.out.println("Cerramos Objetos conciliacion automatica...");
        }
    }

    public static int generaEstadoCuenta(Connection conn, String strCuenta, int nMes, String login, String rutaArch) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_l_tmpEdoCta( ?, ?, ?, ? )}";
        String strEdoCta;
        int idEdoCta = 0;
        try {
            cs = conn.prepareCall(query);
            cs.setString(1, strCuenta);
            cs.setInt(2, nMes);
            cs.setString(3, login);
            cs.setString(4, rutaArch);
            rs = cs.executeQuery();
            if (rs.next()) {
                strEdoCta = rs.getString(1);
                idEdoCta = rs.getInt(2);
            } else {
                throw new Exception("Error al importar el estado de cuenta. Notifique al administrador");
            }
            if ("S".equals(strEdoCta)) {
                System.out.println("Se importo Estado de Cuenta.");
            } else {
                System.out.println("No importo Estado de Cuenta, Saldos diferentes.");
                throw new Exception("Error al Importar el Estado de Cuenta. Validar el Archivo TXT");
            }
            return idEdoCta;
        } finally {
            CloseObject.closeObject(cs);
            CloseObject.closeObject(rs);
        }
    }

    public static int generaAuxiliar(Connection conn, String strCuenta, int nMes, String login, boolean esControlFonden) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_l_tmpAuxiliar( ?, ?, ?)}";
        int idAux = 0;
        try {
            cs = conn.prepareCall(query);
            cs.setString(1, strCuenta);
            cs.setInt(2, nMes);
            cs.setString(3, login);
            rs = cs.executeQuery();
            if (rs.next()) {
                if (esControlFonden)
                    idAux = rs.getInt(2);
                else
                    idAux = rs.getInt(2);
            } else if (!esControlFonden) {
                throw new Exception("Fallo al generar el auxiliar contable. Notifique al administrador");
            }
            System.out.println("Auxilar Generado.");
            return idAux;
        } finally {
            CloseObject.closeObject(cs);
            CloseObject.closeObject(rs);
        }
    }

    public static boolean creaConciliacion(Connection conn, String Cuenta, Integer Mes, Integer idEdoCta, Integer idAux, String cCC, Integer SIIWEB, String strUsuario, boolean esControlFonden) throws Exception {
        PreparedStatement pstm_cta = null;
        ResultSet nConciliacion = null;
        Integer idConciliacion = 0, nConc = 0;
        String cCentroContable = "";
        String cDescripcion = "Conciliacion de la Cuenta " + Cuenta + " del Mes " + Mes.toString();
        if (esControlFonden) {
            cCentroContable = "00";
        } else
            cCentroContable = cCC;
        AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        String ef = adbl.obtenEjercicioFiscal();
        pstm_cta = conn.prepareStatement("INSERT INTO dbo.tConciliacion(nEdocta,nAuxiliar,nCban,nMes,fConciliacion,mSaldoFinal,cUsuario,cDescripcion,nFinal, cCentroContable, mIntereses, mSaldoLibros, mSaldoPDF, pdfOriginal, pdfFirmas, nModalidad, nFolioCuenta, nEjercicio, mSumaAbonos) " + "VALUES ( " + idEdoCta + "," + idAux + " ,'" + Cuenta + "'," + Mes + " ,GETDATE(),0,SUBSTRING('" + strUsuario + "',1,10) ,'" + cDescripcion + "' ,0,'" + cCentroContable + "',0,0,0,0,0,0," + SIIWEB + "," + ef + ",0 )");
        nConc = pstm_cta.executeUpdate();
        if (nConc.equals(1)) {
            pstm_cta = conn.prepareStatement("Select MAX(nConciliacion) as nConciliacion From dbo.tConciliacion WITH (NOLOCK) Where nEdoCta=" + idEdoCta + " AND nAuxiliar=" + idAux + " AND nCban ='" + Cuenta + "' AND nMes=" + Mes + " AND nFinal = 0;");
            nConciliacion = pstm_cta.executeQuery();
            if (nConciliacion.next()) {
                idConciliacion = nConciliacion.getInt("nConciliacion");
                System.out.println("Conciliacion Creada. ID: " + idConciliacion.toString());
                return true;
            } else {
                return false;
            }
        } else {
            throw new Exception("No se genero la conciliacion. Notifique al administrador");
        }
    }

    public static boolean validaConciliacion(Connection conn, String Cuenta, Integer Mes) throws Exception {
        PreparedStatement pstm_valida = null;
        ResultSet valida = null;
        String strResultado = "";
        pstm_valida = conn.prepareStatement("SELECT CASE WHEN COUNT(*) = 1 THEN 'S' ELSE 'N' END RESP FROM dbo.tConciliacion WITH (NOLOCK) WHERE nMes = " + Mes + " AND nCban LIKE '%" + Cuenta + "%' AND nFinal = 1");
        valida = pstm_valida.executeQuery();
        if (valida.next()) {
            strResultado = valida.getString("RESP");
        } else {
            throw new Exception("Error al buscar conciliación finalizada del mes " + Mes + " para la cuenta " + Cuenta + ". Notifique al Administrador");
        }
        System.out.println("Conciliacion procedente: " + strResultado);
        if ("S".equals(strResultado)) {
            return true;
        } else {
            return false;
        }
    }

    public static String CLABE(Connection conn, String Cuenta) throws Exception {
        PreparedStatement pstm_cuenta = null;
        ResultSet cuenta = null;
        String strCta = "";
        pstm_cuenta = conn.prepareStatement("SELECT DISTINCT strClabe FROM dbo.tUECuentasBancarias WITH (NOLOCK) WHERE strClabe LIKE '%" + Cuenta + "%'");
        cuenta = pstm_cuenta.executeQuery();
        if (cuenta.next()) {
            strCta = cuenta.getString("strClabe");
        } else {
            throw new Exception("No se encontro la cuenta bancaria en la base de datos. Notifique al administrador");
        }
        return strCta;
    }

    public static Integer SIIWEB(Connection conn, String Cuenta) throws Exception {
        PreparedStatement pstm_ctaSIIWEB = null;
        ResultSet nCuentaSIIWEB = null;
        Integer nSIIWEB = 0;
        pstm_ctaSIIWEB = conn.prepareStatement("SELECT nFolioCuenta FROM dbo.tSIIWEBCuentas (NOLOCK) WHERE cCLABE LIKE '%" + Cuenta + "%' AND cInstrumento = 'IE009' AND cTipoAct = 'DF'");
        nCuentaSIIWEB = pstm_ctaSIIWEB.executeQuery();
        if (nCuentaSIIWEB.next()) {
            nSIIWEB = nCuentaSIIWEB.getInt("nFolioCuenta");
        } else {
            throw new Exception("No se encontro la cuenta bancaria en las cuentas de SIIWEB. Notifique al administrador");
        }
        return nSIIWEB;
    }

    public static String ctaCC(Connection conn, String Cuenta) throws Exception {
        PreparedStatement pstm_cc = null;
        ResultSet nCC = null;
        String strCC = "";
        pstm_cc = conn.prepareStatement("SELECT DISTINCT strCentroContable FROM dbo.tUECuentasBancarias WITH (NOLOCK) WHERE strClabe LIKE '%" + Cuenta + "%'");
        nCC = pstm_cc.executeQuery();
        if (nCC.next()) {
            strCC = nCC.getString("strCentroContable");
        } else {
            throw new Exception("No se encontro el centro contable para la cuenta bancaria " + Cuenta + " en la base de datos. Notifique al administrador");
        }
        return strCC;
    }
}
