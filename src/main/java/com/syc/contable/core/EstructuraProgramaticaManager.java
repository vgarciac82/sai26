package com.syc.contable.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.StringTokenizer;
import com.syc.cfdi.db.CloseObject;
import com.syc.contable.ContableInterface;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import java.util.Base64;

public class EstructuraProgramaticaManager {

    public EstructuraProgramaticaManager() {
        super();
    }

    public static ArrayList<String> validaEPDetalle(Connection conn, String ejercicio_fiscal, int IterCelElx, String cValor, int IterRegElx, String cSubcuenta) throws SQLException {
        String subQuery = null;
        String tablaVariable = null;
        String descripcionTabla = null;
        int nTotalReg = 0;
        ArrayList<String> arrLResult = new ArrayList<String>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT 'SELECT count(' + NombreCampo + ') as nTotal FROM ' + NombreCatalogo + ' WITH (NOLOCK)  WHERE '+ NombreCampo + '= ? ' as subQuery, NombreCatalogo " + " FROM tTipoSubcuentaConf  WITH (NOLOCK) " + " WHERE aEjercicioFiscal =  ?" + "   AND cSubcuenta = '" + cSubcuenta + "' " + "  AND nOrden     = ? ");
            pstmnt.setString(1, ejercicio_fiscal);
            pstmnt.setInt(2, IterCelElx);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                subQuery = rs.getString("subQuery");
                tablaVariable = rs.getString("NombreCatalogo");
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        try {
            pstmnt = conn.prepareStatement(subQuery);
            pstmnt.setString(1, cValor);
            rs = pstmnt.executeQuery();
            if (rs.next())
                nTotalReg = rs.getInt("nTotal");
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        if (nTotalReg == 0) {
            try {
                pstmnt = conn.prepareStatement("SELECT 'La Clave " + cValor + "'+'  del Catalogo '+ tbl_catalogo+' no Existe. De la secuencia:" + IterRegElx + "' FROM imx_catalogo WITH (NOLOCK)  WHERE Nombre_catalogo = ?");
                pstmnt.setString(1, tablaVariable);
                rs = pstmnt.executeQuery();
                if (rs.next())
                    descripcionTabla = rs.getString(1);
            } finally {
                if (rs != null)
                    rs.close();
                if (pstmnt != null)
                    pstmnt.close();
                rs = null;
                pstmnt = null;
            }
            arrLResult.add(descripcionTabla);
        }
        return arrLResult;
    }

    public static int buscaEPCatalogo(Connection conn, String cEP, String ejercicio_fiscal, String cSubcuenta) throws SQLException {
        int iexiste = 0;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String cQueryConstructor = "";
        if ("EPA".equals(cSubcuenta)) {
            cQueryConstructor = "SELECT 1 FROM tCatalogoEPAnteProy  WITH (NOLOCK) WHERE ep = ?";
        } else {
            cQueryConstructor = "SELECT 1 FROM tCatalogoEP WITH (NOLOCK) WHERE ep = ?";
        }
        try {
            pstmnt = conn.prepareStatement(cQueryConstructor);
            pstmnt.setString(1, cEP);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                iexiste = rs.getInt(1);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return iexiste;
    }

    public static String validaRMPOG(Connection conn, String cEP, String ejercicio_fiscal, String cSubcuenta) throws SQLException {
        String cErrorMensaje = "";
        String cQueryValida = "";
        int iexiste = 0;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        if ("EPA".equals(cSubcuenta)) {
            cQueryValida = "SELECT COUNT(*) " + "  FROM tCatalogoEPAnteProy e WITH (NOLOCK)" + "     , tCatalogoRMPOG r WITH (NOLOCK)" + " WHERE e.EP = ? " + "   AND e.aEjercicioFiscal = r.aEjercicioFiscal " + "   AND e.cRamoEP = r.cRamo " + "   AND e.cProgramaPresupuestario = r.cProgramaPresupuestario " + "   AND e.cPartida = r.cPartida ";
        } else {
            cQueryValida = "SELECT COUNT(*) " + "  FROM tCatalogoEP e WITH (NOLOCK)" + "     , tCatalogoPartidaValidaPP r WITH (NOLOCK)" + " WHERE e.EP = ? " + "   AND e.aEjercicioFiscal = r.aEjercicioFiscal " + "   AND e.cRamoEP = r.cRamo " + "   AND e.cProgramaPresupuestario = r.cProgramaPresupuestario " + "   AND e.cPartida = r.cPartida";
        }
        try {
            pstmnt = conn.prepareStatement(cQueryValida);
            pstmnt.setString(1, cEP);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                iexiste = rs.getInt(1);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        if (iexiste == 0) {
            cErrorMensaje = "Error: La clave " + cEP + " no cumple con la relación Programa Presupuestario y Partida específica.";
        }
        return cErrorMensaje;
    }

    public static String validaRUGFFSFPGAIPP(Connection conn, String cEP, String ejercicio_fiscal, String cSubcuenta) throws SQLException {
        String cErrorMensaje = "";
        String cQueryValida = "";
        int iexiste = 0;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        if ("EPA".equals(cSubcuenta)) {
            cQueryValida = "select count(*)  as nExiste " + "  from tCatalogoEPAnteProy e WITH (NOLOCK)" + "     , tCatalogoRUGFFSFPGAIPP r WITH (NOLOCK)" + " WHERE e.EP = ? AND " + "   e.aEjercicioFiscal = r.aEjercicioFiscal " + "   AND e.cGrupoFuncional = r.cGrupoFuncional " + "   AND e.cFuncion = r.cFuncion " + "   AND e.cSubFuncion = r.cSubFuncion " + "   AND e.cActividadInstitucional = r.cActividadInstitucional " + "   AND e.cUnidadResponsableEP = r.cUnidadResponsable";
        } else {
            cQueryValida = "select count(*)  as nExiste " + "  from tCatalogoEP e WITH (NOLOCK)" + "     , tCatalogoEstProgAut r WITH (NOLOCK)" + " WHERE e.EP = ? AND " + "    e.aEjercicioFiscal = r.aEjercicioFiscal " + "   AND e.cGrupoFuncional = r.cGrupoFuncional " + "   AND e.cFuncion = r.cFuncion " + "   AND e.cSubFuncion = r.cSubFuncion " + "   AND e.cActividadInstitucional = r.cActividadInstitucional " + "   AND e.cUnidadResponsableEP = r.cUnidadResponsable";
        }
        try {
            pstmnt = conn.prepareStatement(cQueryValida);
            pstmnt.setString(1, cEP);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                iexiste = rs.getInt(1);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        if (iexiste == 0) {
            //cErrorMensaje="Error: La clave "+cEP+" no cumple con la relacion Ramo, Unidad Responsable, Finalidad, Función, Sub-Función, Reasignación, Actividad Institucional y Programa Presupuestario.";
            cErrorMensaje = "Error: La clave " + cEP + " no cumple con la Estructura Programática Autorizada.";
        }
        return cErrorMensaje;
    }

    public static String validaROGTGFF(Connection conn, String cEP, String ejercicio_fiscal, String cSubcuenta) throws SQLException {
        String cErrorMensaje = "";
        String cQueryValida = "";
        PreparedStatement pstmnt = null;
        int iexiste = 0;
        ResultSet rs = null;
        if (!"EPA".equals(cSubcuenta)) {
            cQueryValida = "SELECT count(*) as nExiste " + "  FROM tCatalogoEP e WITH (NOLOCK) " + "     , tCatalogoROGTGFF f WITH (NOLOCK) " + " WHERE e.ep = ? AND " + "    e.cRamoEP = f.cRamo " + "   AND e.aEjercicioFiscal = f.aEjercicioFiscal " + "   AND e.cPartida = f.cPartida " + "   AND e.cFuenteFinanciamiento =  f.cFuenteFinanciamiento " + "   AND e.cTipoGasto = f.cTipoGasto";
        } else {
            cQueryValida = "SELECT count(*) as nExiste " + "  FROM tCatalogoEPAnteProy e WITH (NOLOCK)" + "     , tCatalogoEstProgAut f WITH (NOLOCK)" + " WHERE e.ep = ? AND " + "    e.cRamoEP = f.cRamo " + "   AND e.aEjercicioFiscal = f.aEjercicioFiscal " + "   AND e.cPartida = f.cPartida " + "   AND e.cFuenteFinanciamiento =  f.cFuenteFinanciamiento " + "   AND e.cTipoGasto = f.cTipoGasto ";
        }
        try {
            pstmnt = conn.prepareStatement(cQueryValida);
            pstmnt.setString(1, cEP);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                iexiste = rs.getInt(1);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        if (iexiste == 0) {
            cErrorMensaje = "Error: La clave " + cEP + " no cumple con la relación Objeto del Gasto, Tipo de Gasto y Fuente de Financiamiento.";
        }
        return cErrorMensaje;
    }

    public static int maxNumEP(Connection conn, String ejercicio_fiscal, String cSubcuenta) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        int nNumCel = 0;
        try {
            pstmnt = conn.prepareStatement("SELECT max(nOrden) as NumCel FROM tTipoSubcuentaConf WITH (NOLOCK) where aEjercicioFiscal = ? AND cSubcuenta = ? ");
            pstmnt.setString(1, ejercicio_fiscal);
            pstmnt.setString(2, cSubcuenta);
            rs = pstmnt.executeQuery();
            if (rs.next())
                nNumCel = rs.getInt("NumCel");
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return nNumCel;
    }

    public static String insertEP(Connection conn, String cEP, String cSubCuenta, String aEjercicioFiscal) throws SQLException {
        String cMensaje = "";
        String[] arrayComponentes = cEP.split("\\.");
        int nBloquesEP = arrayComponentes.length;
        int nBloquesCP = 0;
        boolean retval = false;
        String cClaveSIAFF = "";
        String cClaveInterna = "";
        PreparedStatement pstmnt = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int iExiste = 0;
        String cQueryBuscaEP = "";
        if (!"EPA".equals(cSubCuenta)) {
            cQueryBuscaEP = "SELECT count(*) FROM tCatalogoEP e with (NOLOCK) WHERE ep = ? ";
        } else {
            cQueryBuscaEP = "SELECT count(*) FROM tCatalogoEPAnteProy e with (NOLOCK) WHERE ep = ? ";
        }
        try {
            nBloquesCP = EstructuraProgramaticaManager.maxNumEP(conn, aEjercicioFiscal, cSubCuenta);
            pstmnt = conn.prepareStatement(cQueryBuscaEP);
            pstmnt.setString(1, cEP);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                iExiste = rs.getInt(1);
            }
            if (iExiste > 0) {
                cMensaje = "Error: La clave " + cEP + " ya Existe.";
            } else {
                cClaveSIAFF = arrayComponentes[0] + "." + arrayComponentes[1] + "." + arrayComponentes[2] + "." + arrayComponentes[3] + "." + arrayComponentes[4] + "." + arrayComponentes[5] + "." + arrayComponentes[6] + "." + arrayComponentes[7] + "." + arrayComponentes[8] + "." + arrayComponentes[9] + "." + arrayComponentes[10] + "." + arrayComponentes[11] + "." + arrayComponentes[12] + "." + arrayComponentes[13];
                cClaveInterna = arrayComponentes[14] + "." + arrayComponentes[15];
                pstmnt = conn.prepareStatement("INSERT INTO [dbo].[tCatalogoEPAnteProy] " + "(aEjercicioFiscal,EP,ClaveSIAFF,ClaveInterna,cRamoEP,cUnidadResponsableEP,cGrupoFuncional,cFuncion,cSubFuncion, " + " cProgramaGeneral,cActividadInstitucional,cProgramaPresupuestario,cPartida,cTipoGasto,cFuenteFinanciamiento," + " cEntidadFederativa,cCartera,cUnidadNorativa,cUnidadEjecutora,cRamo )VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                pstmnt.setString(1, arrayComponentes[0]);
                pstmnt.setString(2, cEP);
                pstmnt.setString(3, cClaveSIAFF);
                pstmnt.setString(4, cClaveInterna);
                pstmnt.setString(5, arrayComponentes[1]);
                pstmnt.setString(6, arrayComponentes[2]);
                pstmnt.setString(7, arrayComponentes[3]);
                pstmnt.setString(8, arrayComponentes[4]);
                pstmnt.setString(9, arrayComponentes[5]);
                pstmnt.setString(10, arrayComponentes[6]);
                pstmnt.setString(11, arrayComponentes[7]);
                pstmnt.setString(12, arrayComponentes[8]);
                pstmnt.setString(13, arrayComponentes[9]);
                pstmnt.setString(14, arrayComponentes[10]);
                pstmnt.setString(15, arrayComponentes[11]);
                pstmnt.setString(16, arrayComponentes[12]);
                pstmnt.setString(17, arrayComponentes[13]);
                pstmnt.setString(18, arrayComponentes[14]);
                pstmnt.setString(19, arrayComponentes[15]);
                pstmnt.setString(20, arrayComponentes[1]);
                retval = pstmnt.execute();
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstm);
        }
        return cMensaje;
    }

    public static String obtenEjercicioFiscal(Connection conn) throws SQLException {
        String cEjercicioFiscal = "";
        ResultSet rs = null;
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("SELECT top (1) aEjercicioFiscal FROM tEjercicioFiscal e WITH (NOLOCK) WHERE cActivo = 1");
            rs = pstm.executeQuery();
            if (rs.next()) {
                cEjercicioFiscal = rs.getString(1);
            }
        } finally {
            if (pstm != null)
                pstm.close();
            pstm = null;
            if (rs != null)
                rs.close();
            rs = null;
        }
        return cEjercicioFiscal;
    }

    public static ArrayList<String> buscaERROR(Connection conn) throws SQLException {
        ArrayList<String> arrErrores = new ArrayList<String>();
        ResultSet rs = null;
        PreparedStatement pstm = null;
        PreparedStatement pstmnt = null;
        try {
            pstm = conn.prepareStatement("SELECT * FROM tErroresEP e WITH (NOLOCK) ");
            rs = pstm.executeQuery();
            while (rs.next()) {
                arrErrores.add(rs.getString(1));
            }
            pstmnt = conn.prepareStatement("delete from tErroresEP ");
        } finally {
            if (pstm != null)
                pstm.close();
            pstm = null;
            if (rs != null)
                rs.close();
            rs = null;
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return arrErrores;
    }

    public static int InsertaERROR(Connection conn, ArrayList arrMResult) throws SQLException {
        int vRecturn = 0;
        boolean retval = false;
        PreparedStatement pstmnt = null;
        String cError = "";
        int i = 0;
        try {
            pstmnt = conn.prepareStatement("INSERT INTO tErroresEP (cErrorEP )VALUES (?)");
            while (arrMResult.size() < i) {
                cError = (String) arrMResult.get(i);
                pstmnt.setString(1, cError);
                retval = pstmnt.execute();
                i++;
            }
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return vRecturn;
    }
}
