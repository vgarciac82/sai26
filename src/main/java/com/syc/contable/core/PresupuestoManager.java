package com.syc.contable.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import com.syc.contable.ContableInterface;
import com.syc.gestion.core.Caso;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class PresupuestoManager {

    private static final Logger log = LoggerFactory.getLogger(PresupuestoManager.class);

    /**
     * Constructor of the object.
     */
    public PresupuestoManager() {
        super();
    }

    public static ArrayList<String> validaEPDetalle(Connection conn, String ejercicio_fiscal, int IterCelElx, String cValor, int IterRegElx) throws SQLException {
        String subQuery = null;
        String tablaVariable = null;
        String descripcionTabla = null;
        //se agrega para tomar la etiqueta del catalogo desde tTipoSubcuentaConf en lugar de imx_catalogo ya que esta mal llenado para los presupuestales
        String etiquetaCampo = null;
        int nTotalReg = 0;
        ArrayList<String> arrLResult = new ArrayList<String>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT " + "		'SELECT count(' + NombreCampo + ') as nTotal FROM ' + NombreCatalogo + ' WITH (NOLOCK) " + "		WHERE '+ NombreCampo + '= ? '+ ISNULL(condicion,'') as subQuery, " + "		NombreCatalogo, EtiquetaCampo " + " FROM tTipoSubcuentaConf  WITH (NOLOCK) " + " WHERE aEjercicioFiscal =  ?" + " AND cSubcuenta = 'EP' " + " AND nOrden     = ? ");
            pstmnt.setString(1, ejercicio_fiscal);
            pstmnt.setInt(2, IterCelElx);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                subQuery = rs.getString("subQuery");
                tablaVariable = rs.getString("NombreCatalogo");
                etiquetaCampo = rs.getString("EtiquetaCampo");
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
                //pstmnt = conn.prepareStatement("SELECT 'La Clave "+ cValor + "'+'  del Catalogo '+ tbl_catalogo+' no Existe. De la secuencia:" + IterRegElx +"' FROM imx_catalogo WITH (NOLOCK)  WHERE Nombre_catalogo = ?" );
                //pstmnt.setString(1, tablaVariable);
                //rs = pstmnt.executeQuery();
                //if (rs.next())
                //descripcionTabla = rs.getString(1);
                descripcionTabla = "Error: En la secuencia " + IterRegElx + " del archivo Excel. La Clave " + cValor + " del Catálogo " + etiquetaCampo + " no Existe.";
                log.trace("Object: {}", subQuery + "[" + cValor + "]");
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

    public static int insertaDetPresupuesto(Connection conn, String ejercicio_fiscal, double sTotalMes, String cClaveEP, int nIdCaso, int iMes, int iDetallePresup, String cCentroContable, String cUserID) throws SQLException {
        PreparedStatement pstmnt = null;
        Boolean retval;
        String separador = Pattern.quote(".");
        String evento = "";
        String CRI = "";
        try {
            String[] parts = cClaveEP.split(separador);
            String FF = parts[11];
            if ("4".equals(FF)) {
                evento = "CARGA_IP";
                CRI = "79900/CNF010405EG1";
            } else {
                evento = "CARGA";
            }
            pstmnt = conn.prepareStatement("insert into tPresupuestoDetalle (nFolioPresupuesto, nDocRenglon,EP,mImporte,cEvento,cMes, cCentroContable,CRI ) values (?,?,?,?,?,?,?,?)");
            pstmnt.setInt(1, nIdCaso);
            pstmnt.setInt(2, iDetallePresup);
            pstmnt.setString(3, cClaveEP.trim());
            pstmnt.setDouble(4, sTotalMes);
            pstmnt.setString(5, evento);
            pstmnt.setInt(6, iMes);
            pstmnt.setString(7, cCentroContable);
            pstmnt.setString(8, CRI);
            retval = pstmnt.execute();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        // En este momento tenemos un array en el que cada elemento es un color.
        return 0;
    }

    public static int insertaPresupuesto(Connection conn, String ejercicio_fiscal, int nIdCaso, String sUsuario, String cCentroContable, String cUserID, String fFechaPaso) throws SQLException {
        PreparedStatement pstmnt = null;
        Boolean retval;
        ResultSet rs = null;
        String cRamoId = "16";
        String cUnidadResponsable = "RHQ";
        try {
            pstmnt = conn.prepareStatement("SELECT rtrim(ltrim(u.cUnidadResponsable)) AS cUnidadResponsable, rtrim(ltrim(u.cRamo)) AS cRamo " + "  FROM tCatalogoURCC  c " + "     , tCatalogoUnidadResponsable u " + "     , CG_CAT_EMPLEADO e " + " WHERE cCentroContable = ? " + "   AND c.cUnidadResponsable  = u.cUnidadResponsable " + "   AND u.ID_AREA = e.ID_AREA " + "   AND e.CE_OS_RESPONSABLE = ?");
            pstmnt.setString(1, cCentroContable);
            pstmnt.setString(2, cUserID);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                cUnidadResponsable = rs.getString("cUnidadResponsable");
                cRamoId = rs.getString("cRamo");
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
            pstmnt = conn.prepareStatement("insert into tPresupuestoEncabezado (nFolioPresupuesto, fCarga, cRamo, cUnidadResponsable,U_LOGIN,cdescripcionpoliza, fAplicacion,aEjercicioFiscal,cTipoPoliza) values (?,?,?,?,?,'CARGA DEL PRESUPUESTO AUTORIZADO FOLIO " + nIdCaso + "',?,?,?)");
            pstmnt.setInt(1, nIdCaso);
            pstmnt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            pstmnt.setString(3, cRamoId);
            pstmnt.setString(4, cUnidadResponsable);
            pstmnt.setString(5, sUsuario);
            pstmnt.setString(6, fFechaPaso);
            pstmnt.setString(7, ejercicio_fiscal);
            pstmnt.setString(8, "DI");
            retval = pstmnt.execute();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return 0;
    }

    public static int maxNumEP(Connection conn, String ejercicio_fiscal) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        int nNumCel = 0;
        try {
            pstmnt = conn.prepareStatement("SELECT max(nOrden) as NumCel FROM tTipoSubcuentaConf where aEjercicioFiscal = ? AND cSubcuenta = 'EP' ");
            pstmnt.setString(1, ejercicio_fiscal);
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

    public static int insertaCatalogoEP(Connection conn, String cClaveEP, String cCentroContable, String cUserID, String aEjercicio) throws SQLException {
        String[] arrayComponentes = cClaveEP.split("\\.");
        String ClaveInterna = "";
        String ClaveSIAFF = "";
        ResultSet rs = null;
        String cRamoId = "16";
        String cUnidadResponsable = "RHQ";
        PreparedStatement pstmnt = null;
        int iExiste = 0;
        boolean retval;
        try {
            pstmnt = conn.prepareStatement("select count(*) as total " + "  FROM tCatalogoEP  c " + " WHERE ep = ? ");
            pstmnt.setString(1, cClaveEP);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                iExiste = rs.getInt("total");
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        if (iExiste == 0) {
            //se valida Consistencia de Clave EP
            //
            // si no hay error en clave se da de alta
            try {
                pstmnt = conn.prepareStatement("select NombreCampo, Interno from tTipoSubcuentaConf where cSubcuenta = 'EP' and aEjercicioFiscal = '" + AdecuacionManager.obtenEjercicioFiscal(conn) + "' order by nOrden");
                rs = pstmnt.executeQuery();
                String cArmaValores = "'" + cRamoId + "','" + cClaveEP + "'";
                String cArmaCampos = "cRamoEP,EP";
                String cArmaWere = " WHERE cRamoEP = '" + cRamoId + "' ";
                int i = 0;
                int j = 0;
                while (rs.next()) {
                    if (cArmaCampos.length() > 0) {
                        cArmaCampos = cArmaCampos + ",";
                        cArmaValores = cArmaValores + ",";
                    }
                    if (i == 2) {
                        cArmaCampos += "cUnidadResponsableEP";
                        cArmaWere += " AND cUnidadResponsableEP = 'rtrim(ltrim('" + arrayComponentes[i] + "')) ";
                    } else if (i == 15) {
                        cArmaCampos += "cUnidadNorativa";
                    } else if (i == 14) {
                        cArmaCampos += "cUnidadEjecutora";
                    } else {
                        cArmaWere += " AND " + rs.getString("NombreCampo") + " = " + " rtrim(ltrim('" + arrayComponentes[i] + "'))";
                        cArmaCampos += rs.getString("NombreCampo");
                    }
                    cArmaValores += " rtrim(ltrim('" + arrayComponentes[i] + "'))";
                    if (rs.getString("Interno").equals("S")) {
                        if (ClaveInterna.length() > 0) {
                            ClaveInterna = ClaveInterna.trim() + ".";
                        }
                        ClaveInterna = ClaveInterna + arrayComponentes[i].trim();
                    } else {
                        if (ClaveSIAFF.length() > 0) {
                            ClaveSIAFF = ClaveSIAFF.trim() + ".";
                        }
                        ClaveSIAFF = ClaveSIAFF + arrayComponentes[i].trim();
                    }
                    i++;
                }
                cArmaCampos += ",ClaveSIAFF,ClaveInterna";
                cArmaValores += ",'" + ClaveSIAFF + "','" + ClaveInterna + "'";
                pstmnt = conn.prepareStatement("insert into tCatalogoEP (" + cArmaCampos + ") values (" + cArmaValores + ")");
                retval = pstmnt.execute();
            } finally {
                if (pstmnt != null)
                    pstmnt.close();
                pstmnt = null;
            }
        }
        return 0;
    }

    public static String buscaCentroContable(Connection conn, String cUnidadEjecutora) throws SQLException {
        String cCentroContable = "";
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT cCentroContable FROM tCatalogoURCC WHERE (cUnidadResponsable = ?) AND (nConsecutivo = 1)");
            pstmnt.setString(1, cUnidadEjecutora);
            rs = pstmnt.executeQuery();
            if (rs.next())
                cCentroContable = rs.getString("cCentroContable");
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return cCentroContable;
    }

    public static String reAplicaDoctos(Connection conn) throws SQLException {
        ArrayList<String> arrLResult = new ArrayList<String>();
        ArrayList<String> arrLResultREAP = new ArrayList<String>();
        String cRetValue = "";
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String aEjercicioFiscal;
        String cRamo;
        String cUnidadResponsable;
        String fAplicacion;
        String nFolioDocto;
        String cTipoDocto;
        String cTablaPadre = "";
        String cTablaHija = "";
        String cFolio = "";
        int nFolioDocumento = 0;
        System.out.println("Busco Doctos a reaplicar");
        //AND a.nFolioAdecuacion = 9999 " +
        String //AND a.nFolioAdecuacion = 9999 " +
        cQueryConstructor = //AND au.nFolioAdecuacionaut = 9999 " +
        "select aEjercicioFiscal, cRamo, cUnidadResponsable, fAplicacion, nFolioDocto, cTipoDocto, Folio " + " from ( select isnull(p.aEjercicioFiscal,'2012') as aEjercicioFiscal, p.cRamo, p.cUnidadResponsable, p.fAplicacion, p.nFolioPresupuesto as Folio, e.FOLIO as nFolioDocto, 'PRESUPUESTO' as cTipoDocto from tPresupuestoEncabezado p with (nolock), IMXPRESUPUESTO e with (nolock) where e.FOLIO = 'CP-'+convert(varchar,p.nFolioPresupuesto ) AND p.cDocumentoHaplicado = 'R'" + " union " + "        select a.aEjercicioFiscal,  a.cRamo, a.cUnidadResponsable,  a.fAplicacion,   a.nFolioAdecuacion as Folio,    e.FOLIO as nFolioDocto, 'Adecuacion' as cTipoDocto     from tAdecuacionEncabezado a with (nolock), IMXADECUACION e with (nolock) where e.FOLIO like '2012-%'+convert(varchar,a.nFolioAdecuacion) AND cDocumentoHaplicado = 'R' " + " union " + //AND pd.nFolioPagoDirecto = 9999 " +
        "        select au.aEjercicioFiscal, au.cRamo, au.cUnidadResponsable, au.fAplicacion, au.nFolioAdecuacionaut as Folio, e.FOLIO as nFolioDocto, 'ADECUACIONAUT' as cTipoDocto  from tADECUACIONAUTEncabezado au with (nolock) , IMXADECUACION e with (nolock)  where e.FOLIO like '2012-%'+convert(varchar,au.nFolioAdecuacionaut) AND cDocumentoHaplicado = 'R' " + " union " + //AND PV.nFolioPagoDiverso = 9999 " +
        "        select pd.aEjercicioFiscal, pd.cRamo, pd.cUnidadResponsable, pd.fAplicacion, pd.nFolioPagoDirecto as Folio, e.FOLIO as nFolioDocto, 'PagoDirecto' as cTipoDocto    from tPagoDirectoEncabezado pd with (nolock), IMXPAGODIRECTO e with (nolock) where e.FOLIO like 'PDIR-%-'+convert(varchar,pd.nFolioPagoDirecto) AND cDocumentoHaplicado = 'R' " + " union " + //AND rg.nFolioRELACIONGASTOS = 9999 " +
        "        select pv.aEjercicioFiscal, pv.cRamo, pv.cUnidadResponsable, pv.fAplicacion, PV.nFolioPagoDiverso as Folio, e.FOLIO as nFolioDocto, 'PAGODIVERSO' as cTipoDocto    from tPAGODIVERSOEncabezado pv with (nolock), IMXPAGODIVERSO e with (nolock) where e.FOLIO like 'PDIV-%'+ CONVERT(VARCHAR,PV.nFolioPagoDiverso)  AND cDocumentoHaplicado = 'R' " + " union " + //AND n.nFolioNOMINA = 9999 " +
        "        select rg.aEjercicioFiscal, rg.cRamo, rg.cUnidadResponsable, rg.fAplicacion, rg.nFolioRELACIONGASTOS as Folio, e.FOLIO as nFolioDocto, 'RELACIONGASTOS' as cTipoDocto from tRELACIONGASTOSEncabezado rg with (nolock) , IMXRELACIONGASTOS e with (nolock) where e.FOLIO like 'RELG-%-'+convert(varchar,rg.nFolioRELACIONGASTOS) AND cDocumentoHaplicado = 'R' " + " union  " + "        select n.aEjercicioFiscal,   n.cRamo, n.cUnidadResponsable,   n.fAplicacion, n.nFolioNOMINA as Folio, e.FOLIO as nFolioDocto, 'NOMINA' as cTipoDocto         from tNOMINAEncabezado n with (nolock) , IMXNOMINA e with (nolock)  where e.FOLIO  like 'NOM-%-' + convert(varchar,n.nFolioNOMINA) AND cDocumentoHaplicado = 'R' " + " ) a " + " order by a.fAplicacion ";
        try {
            ContableInterface conInt = new AplicacionContable();
            pstmnt = conn.prepareStatement(cQueryConstructor);
            rs = pstmnt.executeQuery();
            int i = 0;
            arrLResultREAP.add("Inica Re-Aplicaicon de todo a las :" + new Timestamp(System.currentTimeMillis()));
            while (rs.next()) {
                aEjercicioFiscal = rs.getString("aEjercicioFiscal");
                cRamo = rs.getString("cRamo");
                cUnidadResponsable = rs.getString("cUnidadResponsable");
                fAplicacion = rs.getString("fAplicacion");
                nFolioDocto = rs.getString("nFolioDocto");
                cTipoDocto = rs.getString("cTipoDocto");
                Caso c = null;
                if (c == null) {
                    cTablaPadre = "t" + cTipoDocto + "Encabezado";
                    /*if ("ADECUACIONAUTE".equals(cTipoDocto)){
						cTablaPadre = "t" + cTipoDocto + "ncabezado"; 
						cTablaHija = "t" + cTipoDocto + "Detalle"; 
						cFolio = "nFolioADECUACIONAUT"; 
					}
					else{*/
                    cTablaHija = "t" + cTipoDocto + "Detalle";
                    cFolio = "nFolio" + cTipoDocto;
                    //}
                    nFolioDocumento = rs.getInt("Folio");
                }
                System.out.println("Busco Docto del tipo:|" + cTipoDocto + "| con No.Folio:|" + nFolioDocto + "| a reaplicar inicia:|" + new Timestamp(System.currentTimeMillis()));
                //el commit se hace aqui adentro
                arrLResult.addAll(conInt.aplicarContable(conn, c, cTablaPadre, cTablaHija, cFolio, nFolioDocumento, cTipoDocto));
                while (i < arrLResult.size()) {
                    System.out.println(arrLResult.get(i));
                    i++;
                }
                System.out.println("Busco Docto del tipo:|" + cTipoDocto + "| con No.Folio:|" + nFolioDocto + "| a reaplicar Finaliza:|" + new Timestamp(System.currentTimeMillis()));
            }
            arrLResultREAP.add("Finaliza Re-Aplicaicon de todo a las :" + new Timestamp(System.currentTimeMillis()));
            while (i < arrLResult.size()) {
                System.out.println(arrLResultREAP.get(i));
                i++;
            }
            conn.commit();
            System.out.println("termina while ");
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return cRetValue;
    }

    public static List<String> getFiltroPartidasModulo(Connection conn, String modulo) throws Exception {
        String query = "SELECT cpartida FROM tPartidaFiltraPago WITH( NOLOCK ) WHERE ctipopago = ?";
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<String> partidasFiltro = new ArrayList<String>();
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, modulo);
            rs = ps.executeQuery();
            while (rs.next()) {
                partidasFiltro.add(rs.getString(1));
            }
            return partidasFiltro;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }
}
