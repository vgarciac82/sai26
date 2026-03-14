package com.syc.contable.core;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
//import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import com.axtel.presupuesto.catalogo.ProgramaPresupuestario;
import com.syc.contable.adecuaciones.Adecuacion;
import com.syc.contable.adecuaciones.AdecuacionDetalle;
import com.syc.contable.adecuaciones.AdecuacionDetalleManager;
import com.syc.contable.adecuaciones.AdecuacionEncabezado;
import com.syc.contable.adecuaciones.AdecuacionEncabezadoManager;
import com.syc.contable.adecuaciones.ClasificacionAdecuacion;
import com.syc.contable.adecuaciones.ValidacionAdecuacionesManager;
//import com.syc.contable.adecuaciones.Exception.IADEClavesNeteadasException;
import com.syc.contable.adecuaciones.core.Fap01;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class AdecuacionManager {

    private static final Logger log = LoggerFactory.getLogger(AdecuacionManager.class);

    public static List<String> getInvolucradosAdecuacion(Connection conn, Caso c) throws Exception {
        String query = "SELECT b_co_responsable_ejec " + " FROM   cg_bitacora WITH(nolock)  " + " WHERE  b_id_caso = ? ";
        List<String> involucrados = new ArrayList<String>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, c.getIdCaso());
            rs = ps.executeQuery();
            while (rs.next()) {
                involucrados.add(rs.getString(1));
            }
            return involucrados;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static String getListaCorreos(Connection conn, Set<String> involucrados) throws Exception {
        String to = "";
        String cadenaBusqueda = Util.joinSQL(involucrados.toArray(new String[involucrados.size()]));
        String query = "SELECT u_email " + "FROM   cg_usuario WITH(nolock) " + "WHERE  u_login IN ( " + cadenaBusqueda + " ) AND U_EMAIL IS NOT NULL";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            String token = "";
            while (rs.next()) {
                to += token + rs.getString(1);
                token = ";";
            }
            return to;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static String getListaCorreos(Connection conn, Caso c) throws SQLException {
        String to = "";
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String sSQL = "SELECT DISTINCT U_EMAIL " + " FROM dbo.CG_BITACORA AS B WITH (NOLOCK) " + " JOIN dbo.CG_USUARIO AS U WITH (NOLOCK) ON B_CO_RESPONSABLE_EJEC = U_LOGIN " + " WHERE B_CO_RESPONSABLE_SIGTE = 'JEFATURA_ADECUACIONES' " + " AND B_C_FOLIO = ? " + " AND B_ID_CASO = ? ";
        pstmnt = conn.prepareStatement(sSQL);
        if (c != null && c.getFolio() != null) {
            pstmnt.setString(1, c.getFolio());
            pstmnt.setInt(2, c.getIdCaso());
        } else
            // no encontrara nada, solo es para que
            pstmnt.setString(1, "BXX");
        // corra el qry
        rs = pstmnt.executeQuery();
        boolean enviar = false;
        while (rs.next()) {
            to += (enviar ? ";" : "") + rs.getString(1);
            enviar = true;
        }
        return to;
    }

    public static int updateHederAdecuacion(Connection conn, int nIdCaso, String cNivel, String cRamo, String cUnidadResponsable, String cRevisado, String aEjercicioFiscal, String cJustificacion, String cCentroContable, String cUserID, Date cFechaAplica, String cTipoAdecuacion, String tipoPoliza) throws SQLException {
        PreparedStatement pstmnt = null;
        ArrayList<String> arrResult = new ArrayList<String>();
        int retval;
        int i = 4;
        int j = 4;
        String QueryUpdate = "";
        if ("".equals(cNivel)) {
            arrResult = AdecuacionManager.validaInvercionaGastoCorr(conn, nIdCaso);
            if (arrResult.size() > 0) {
                cNivel = (String) arrResult.get(0);
            }
        }
        try {
            QueryUpdate = "UPDATE tAdecuacionEncabezado set cRevisado = ?, cJustificacion=replace(?,'%',' porciento '), fAplicacion=?, cTipoPoliza=?";
            if (!cNivel.isEmpty()) {
                QueryUpdate += ", nNivel = ? ";
                i++;
                j++;
            }
            if (!cTipoAdecuacion.isEmpty()) {
                QueryUpdate += ", cTipoAdecuacion = ? ";
                j++;
            }
            QueryUpdate += " WHERE  nFolioAdecuacion = ?";
            pstmnt = conn.prepareStatement(QueryUpdate);
            pstmnt.setString(1, cRevisado);
            pstmnt.setString(2, cJustificacion);
            pstmnt.setDate(3, cFechaAplica);
            pstmnt.setString(4, tipoPoliza);
            if (!cNivel.isEmpty()) {
                pstmnt.setString(i, cNivel);
            }
            if (!cTipoAdecuacion.isEmpty()) {
                pstmnt.setString(j, cTipoAdecuacion);
            }
            pstmnt.setInt(j + 1, nIdCaso);
            retval = pstmnt.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return retval;
    }

    private static String getTipoPoliza(Connection conn, String cDocumento) throws SQLException {
        String retTipoPoliza = "";
        ResultSet rs = null;
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("select cTipoPoliza from tDocumentoTipoPoliza with (nolock) where cDocumento=?");
            pstm.setString(1, cDocumento);
            rs = pstm.executeQuery();
            if (rs.next()) {
                retTipoPoliza = rs.getString(1);
            }
        } finally {
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(rs);
        }
        return retTipoPoliza;
    }

    // TODO Mejorar este metodo
    public static ArrayList seleccionaAdecuacion(Connection conn, int nFolioAdecuacion) throws SQLException {
        ArrayList<Object> arrmMontosCalendario = new ArrayList<>();
        // ArrayList<String> arrMResult = new ArrayList<String>();
        String cNivel = "3";
        String cTipoAdecuacion = "";
        String cRamo;
        String cUnidadResponsable;
        String aEjercicioFiscal;
        String cJustificacion;
        String cTipoMovDet = "";
        String cUserID = "";
        double mTotREnero = 0;
        double mTotRFebrero = 0;
        double mTotRMarzo = 0;
        double mTotRAbril = 0;
        double mTotRMayo = 0;
        double mTotRJunio = 0;
        double mTotRJulio = 0;
        double mTotRAgosto = 0;
        double mTotRSeptiembre = 0;
        double mTotROctubre = 0;
        double mTotRNoviembre = 0;
        double mTotRDiciembre = 0;
        double mTotREneroNegativo = 0;
        double mTotRFebreroNegativo = 0;
        double mTotRMarzoNegativo = 0;
        double mTotRAbrilNegativo = 0;
        double mTotRMayoNegativo = 0;
        double mTotRJunioNegativo = 0;
        double mTotRJulioNegativo = 0;
        double mTotRAgostoNegativo = 0;
        double mTotRSeptiembreNegativo = 0;
        double mTotROctubreNegativo = 0;
        double mTotRNoviembreNegativo = 0;
        double mTotRDiciembreNegativo = 0;
        double mTotAEnero = 0;
        double mTotAFebrero = 0;
        double mTotAMarzo = 0;
        double mTotAAbril = 0;
        double mTotAMayo = 0;
        double mTotAJunio = 0;
        double mTotAJulio = 0;
        double mTotAAgosto = 0;
        double mTotASeptiembre = 0;
        double mTotAOctubre = 0;
        double mTotANoviembre = 0;
        double mTotADiciembre = 0;
        double iTotAbonos = 0;
        double iTotCargos = 0;
        int nDocREnglon = 0;
        String cMensajeSecuencia = "";
        String cMensajeAdecSaldos = "";
        String cMensajeAdec = "";
        // ArrayList<String> arrLResult = new ArrayList<String>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String sqlStament = "";
        String cDocumentoHaplicado = "";
        int i = 0;
        // System.out.println("En Adecuacion Manager al inicio ");
        try {
            pstmnt = conn.prepareStatement("SELECT   cTipoAdecuacion,  cRamo, cUnidadResponsable, aEjercicioFiscal,  replace(replace(cJustificacion,'\"',''),'''','') as cJustificacion, U_LOGIN, cDocumentoHaplicado, nNivel from tAdecuacionEncabezado with (nolock) where nFolioAdecuacion = ?");
            pstmnt.setInt(1, nFolioAdecuacion);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                cNivel = rs.getString("nNivel");
                arrmMontosCalendario.add(cNivel);
                cRamo = rs.getString("cRamo");
                arrmMontosCalendario.add(cRamo);
                cUnidadResponsable = rs.getString("cUnidadResponsable");
                arrmMontosCalendario.add(cUnidadResponsable);
                aEjercicioFiscal = rs.getString("aEjercicioFiscal");
                arrmMontosCalendario.add(aEjercicioFiscal);
                cUserID = rs.getString("U_LOGIN");
                arrmMontosCalendario.add(cUserID);
                cJustificacion = rs.getString("cJustificacion");
                arrmMontosCalendario.add(cJustificacion);
                cDocumentoHaplicado = rs.getString("cDocumentoHaplicado");
                cTipoAdecuacion = rs.getString("cTipoAdecuacion");
                arrmMontosCalendario.add(cTipoAdecuacion);
            }
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
        try {
            sqlStament = "SELECT ROW_NUMBER() OVER(order by cEvento desc)  AS nDocREnglon, substring(cEvento,1,1) as cEvento, ISNULL(z.ClaveSIAFF, substring(a.EP,1,55)) as ClaveSIAFF, isnull(z.ClaveInterna,substring(a.ep,57,64)) as ClaveInterna,z.nClaveCNA " + "     ,  sum(mEnero) as mEnero, sum(mFebrero) as mFebrero, sum(mMarzo) as mMarzo, sum(mAbril) as mAbril, sum(mMayo) as mMayo, sum(mJunio) as mJunio, sum(mJulio) as mJulio, sum(mAgosto) as mAgosto, sum(mSeptiembre) as mSeptiembre, sum(mOctubre) as mOctubre, sum(mNoviembre) as mNoviembre, sum(mDiciembre) as mDiciembre " + "     ,  sum(mEneroNegativo) as mEneroNegativo, sum(mFebreroNegativo) as mFebreroNegativo, sum(mMarzoNegativo) as mMarzoNegativo, sum(mAbrilNegativo) as mAbrilNegativo " + "     ,  sum(mMayoNegativo) as mMayoNegativo, sum(mJunioNegativo) as mJunioNegativo, sum(mJulioNegativo) as mJulioNegativo, sum(mAgostoNegativo) as mAgostoNegativo " + "     ,  sum(mSeptiembreNegativo) as mSeptiembreNegativo, sum(mOctubreNegativo) as mOctubreNegativo, sum(mNoviembreNegativo) as mNoviembreNegativo, sum(mDiciembreNegativo) as mDiciembreNegativo " + "     , sum(mEnero) + sum(mFebrero) + sum(mMarzo) + sum(mAbril) + sum(mMayo) + sum(mJunio) + sum(mJulio) + sum(mAgosto) + sum(mSeptiembre) + sum(mOctubre) + sum(mNoviembre) + sum(mDiciembre) as mAnual " + "  FROM ( SELECT d.ep, d.cEvento, case d.cMes when 1 then sum(d.mImporte) else 0 end as mEnero " + "     	 , case d.cMes when 2 then sum(d.mImporte) else 0 end as mFebrero  " + "     	 , case d.cMes when 3 then sum(d.mImporte) else 0 end as mMarzo  " + "     	 , case d.cMes when 4 then sum(d.mImporte) else 0 end as mAbril  " + "     	 , case d.cMes when 5 then sum(d.mImporte) else 0 end as mMayo  " + "     	 , case d.cMes when 6 then sum(d.mImporte) else 0 end as mJunio  " + "     	 , case d.cMes when 7 then sum(d.mImporte) else 0 end as mJulio  " + "     	 , case d.cMes when 8 then sum(d.mImporte) else 0 end as mAgosto  " + "     	 , case d.cMes when 9 then sum(d.mImporte) else 0 end as mSeptiembre  " + "     	 , case d.cMes when 10 then sum(d.mImporte) else 0 end as mOctubre  " + "     	 , case d.cMes when 11 then sum(d.mImporte) else 0 end as mNoviembre  " + "     	 , case d.cMes when 12 then sum(d.mImporte) else 0 end as mDiciembre  " + "     	 , case d.cMes when 1 then sum(d.NewSaldo) else 0 end as mEneroNegativo  " + "     	 , case d.cMes when 2 then sum(d.NewSaldo) else 0 end as mFebreroNegativo  " + "     	 , case d.cMes when 3 then sum(d.NewSaldo) else 0 end as mMarzoNegativo  " + "     	 , case d.cMes when 4 then sum(d.NewSaldo) else 0 end as mAbrilNegativo  " + "     	 , case d.cMes when 5 then sum(d.NewSaldo) else 0 end as mMayoNegativo  " + "     	 , case d.cMes when 6 then sum(d.NewSaldo) else 0 end as mJunioNegativo  " + "     	 , case d.cMes when 7 then sum(d.NewSaldo) else 0 end as mJulioNegativo  " + "     	 , case d.cMes when 8 then sum(d.NewSaldo) else 0 end as mAgostoNegativo  " + "     	 , case d.cMes when 9 then sum(d.NewSaldo) else 0 end as mSeptiembreNegativo  " + "     	 , case d.cMes when 10 then sum(d.NewSaldo) else 0 end as mOctubreNegativo  " + "     	 , case d.cMes when 11 then sum(d.NewSaldo) else 0 end as mNoviembreNegativo  " + "     	 , case d.cMes when 12 then sum(d.NewSaldo) else 0 end as mDiciembreNegativo  ";
            if (!"S".equals(cDocumentoHaplicado)) {
                sqlStament += "     FROM (select d.EP, d.cEvento, d.cMes, d.mImporte, nFolioAdecuacion " + "	 , case substring(d.cEvento,1,1) when 'R' then case d.mImporte when 0 then 0 else (select s.mSaldoArrastre - a.mImporte from tAdecuacionDetalle a with (nolock), tSaldos s with (nolock) " + "	      where a.nFolioAdecuacion = d.nFolioAdecuacion AND a.cEvento = d.cEvento and a.cMes = d.cMes and a.EP = d.EP and d.nDocRenglon =  a.nDocRenglon " + "		    AND a.cEvento like 'R%'  AND a.mImporte > 0  and a.EP = s.cSubCuenta AND s.nCuenta like '82106%' AND convert(int,substring(s.nCuenta,7,5)) = a.cMes )  end " + "			else '0' end as NewSaldo  " + "  from tAdecuacionDetalle d with (nolock) " + " WHERE d.nFolioAdecuacion =  " + nFolioAdecuacion + ") d  " + "	 group by d.ep, d.cEvento, d.cMes ) a LEFT OUTER JOIN ";
            } else {
                sqlStament += "     FROM ( select d.EP, d.cEvento, d.cMes, d.mImporte, nFolioAdecuacion " + "		     	 		  , case substring(cEvento,1,1) when 'R' then (d.mImporte )   " + "		     	 		                                WHEN 'A' then (d.mImporte) end as NewSaldo from tAdecuacionDetalle d  with (nolock) " + "		  			  WHERE d.nFolioAdecuacion = " + nFolioAdecuacion + " ) d  group by d.ep, d.cEvento, d.cMes ) a  LEFT OUTER JOIN  ";
            }
            sqlStament += "           tCatalogoEP z   with (nolock)  ON a.EP =  z.EP  group by a.ep, a.cEvento, z.ClaveSIAFF, z.ClaveInterna,z.nClaveCNA ";
            pstmnt = conn.prepareStatement(sqlStament);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                Saldo epSaldo = new Saldo();
                i++;
                System.out.println("Secuencia:" + i + " de la Adecuacion:" + nFolioAdecuacion);
                nDocREnglon = rs.getInt("nDocREnglon");
                cMensajeSecuencia = "";
                cTipoMovDet = rs.getString("cEvento");
                epSaldo.setEp(cTipoMovDet);
                if ("R".equals(cTipoMovDet)) {
                    mTotREnero = mTotREnero + new Double(rs.getString("mEnero"));
                    mTotRFebrero = mTotRFebrero + new Double(rs.getString("mFebrero"));
                    mTotRMarzo = mTotRMarzo + new Double(rs.getString("mMarzo"));
                    mTotRAbril = mTotRAbril + new Double(rs.getString("mAbril"));
                    mTotRMayo = mTotRMayo + new Double(rs.getString("mMayo"));
                    mTotRJunio = mTotRJunio + new Double(rs.getString("mJunio"));
                    mTotRJulio = mTotRJulio + new Double(rs.getString("mJulio"));
                    mTotRAgosto = mTotRAgosto + new Double(rs.getString("mAgosto"));
                    mTotRSeptiembre = mTotRSeptiembre + new Double(rs.getString("mSeptiembre"));
                    mTotROctubre = mTotROctubre + new Double(rs.getString("mOctubre"));
                    mTotRNoviembre = mTotRNoviembre + new Double(rs.getString("mNoviembre"));
                    mTotRDiciembre = mTotRDiciembre + new Double(rs.getString("mDiciembre"));
                    if (!"S".equals(cDocumentoHaplicado) && "R".equals(cTipoMovDet)) {
                        mTotREneroNegativo = new Double(rs.getString("mEneroNegativo"));
                        mTotRFebreroNegativo = new Double(rs.getString("mFebreroNegativo"));
                        mTotRMarzoNegativo = new Double(rs.getString("mMarzoNegativo"));
                        mTotRAbrilNegativo = new Double(rs.getString("mAbrilNegativo"));
                        mTotRMayoNegativo = new Double(rs.getString("mMayoNegativo"));
                        mTotRJunioNegativo = new Double(rs.getString("mJunioNegativo"));
                        mTotRJulioNegativo = new Double(rs.getString("mJulioNegativo"));
                        mTotRAgostoNegativo = new Double(rs.getString("mAgostoNegativo"));
                        mTotRSeptiembreNegativo = new Double(rs.getString("mSeptiembreNegativo"));
                        mTotROctubreNegativo = new Double(rs.getString("mOctubreNegativo"));
                        mTotRNoviembreNegativo = new Double(rs.getString("mNoviembreNegativo"));
                        mTotRDiciembreNegativo = new Double(rs.getString("mDiciembreNegativo"));
                    } else {
                        mTotREneroNegativo = 0;
                        mTotRFebreroNegativo = 0;
                        mTotRMarzoNegativo = 0;
                        mTotRAbrilNegativo = 0;
                        mTotRMayoNegativo = 0;
                        mTotRJunioNegativo = 0;
                        mTotRJulioNegativo = 0;
                        mTotRAgostoNegativo = 0;
                        mTotRSeptiembreNegativo = 0;
                        mTotROctubreNegativo = 0;
                        mTotRNoviembreNegativo = 0;
                        mTotRDiciembreNegativo = 0;
                    }
                } else {
                    mTotREneroNegativo = 0;
                    mTotRFebreroNegativo = 0;
                    mTotRMarzoNegativo = 0;
                    mTotRAbrilNegativo = 0;
                    mTotRMayoNegativo = 0;
                    mTotRJunioNegativo = 0;
                    mTotRJulioNegativo = 0;
                    mTotRAgostoNegativo = 0;
                    mTotRSeptiembreNegativo = 0;
                    mTotROctubreNegativo = 0;
                    mTotRNoviembreNegativo = 0;
                    mTotRDiciembreNegativo = 0;
                    mTotAEnero = mTotAEnero + new Double(rs.getString("mEnero"));
                    mTotAFebrero = mTotAFebrero + new Double(rs.getString("mFebrero"));
                    mTotAMarzo = mTotAMarzo + new Double(rs.getString("mMarzo"));
                    mTotAAbril = mTotAAbril + new Double(rs.getString("mAbril"));
                    mTotAMayo = mTotAMayo + new Double(rs.getString("mMayo"));
                    mTotAJunio = mTotAJunio + new Double(rs.getString("mJunio"));
                    mTotAJulio = mTotAJulio + new Double(rs.getString("mJulio"));
                    mTotAAgosto = mTotAAgosto + new Double(rs.getString("mAgosto"));
                    mTotASeptiembre = mTotASeptiembre + new Double(rs.getString("mSeptiembre"));
                    mTotAOctubre = mTotAOctubre + new Double(rs.getString("mOctubre"));
                    mTotANoviembre = mTotANoviembre + new Double(rs.getString("mNoviembre"));
                    mTotADiciembre = mTotADiciembre + new Double(rs.getString("mDiciembre"));
                }
                String cEP = rs.getString("ClaveInterna") + "." + rs.getString("nClaveCNA");
                epSaldo.setMontoAbril(rs.getString("mAbril"));
                epSaldo.setMontoAgosto(rs.getString("mAgosto"));
                epSaldo.setMontoAnual(rs.getString("mAnual"));
                epSaldo.setMontoDiciembre(rs.getString("mDiciembre"));
                epSaldo.setMontoEnero(rs.getString("mEnero"));
                epSaldo.setMontoFebrero(rs.getString("mFebrero"));
                epSaldo.setMontoJulio(rs.getString("mJulio"));
                epSaldo.setMontoJunio(rs.getString("mJunio"));
                epSaldo.setMontoMarzo(rs.getString("mMarzo"));
                epSaldo.setMontoMayo(rs.getString("mMayo"));
                epSaldo.setMontoNoviembre(rs.getString("mNoviembre"));
                epSaldo.setMontoOctubre(rs.getString("mOctubre"));
                epSaldo.setMontoSeptiembre(rs.getString("mSeptiembre"));
                if (mTotREneroNegativo < 0) {
                    cMensajeSecuencia = "Enero";
                }
                if (mTotRFebreroNegativo < 0) {
                    cMensajeSecuencia += ", Febrero";
                }
                if (mTotRMarzoNegativo < 0) {
                    cMensajeSecuencia += ", Marzo";
                }
                if (mTotRAbrilNegativo < 0) {
                    cMensajeSecuencia += ", Abril";
                }
                if (mTotRMayoNegativo < 0) {
                    cMensajeSecuencia += ", Mayo";
                }
                if (mTotRJunioNegativo < 0) {
                    cMensajeSecuencia += ", Junio";
                }
                if (mTotRJulioNegativo < 0) {
                    cMensajeSecuencia += ", Julio";
                }
                if (mTotRAgostoNegativo < 0) {
                    cMensajeSecuencia += ", Agosto";
                }
                if (mTotRSeptiembreNegativo < 0) {
                    cMensajeSecuencia += ", Septiembre";
                }
                if (mTotROctubreNegativo < 0) {
                    cMensajeSecuencia += ", Octubre";
                }
                if (mTotRNoviembreNegativo < 0) {
                    cMensajeSecuencia += ", Noviembre";
                }
                if (mTotRDiciembreNegativo < 0) {
                    cMensajeSecuencia += ", Diciembre";
                }
                if (cMensajeSecuencia.length() > 0) {
                    cMensajeSecuencia = "Saldo Insuficiente en la clave: " + rs.getString("ClaveSIAFF").trim() + "." + rs.getString("ClaveInterna").trim() + "  para los Meses:\\n" + cMensajeSecuencia + "\\n";
                }
                epSaldo.setClaveSIAFF(rs.getString("ClaveSIAFF"));
                epSaldo.setClaveInterna(rs.getString("ClaveInterna"));
                epSaldo.setClaveCNA(rs.getString("nClaveCNA"));
                epSaldo.setMensajeSecuencia(cMensajeSecuencia);
                cMensajeAdecSaldos += cMensajeSecuencia;
                arrmMontosCalendario.add(epSaldo);
            }
            iTotAbonos = mTotAEnero + mTotAFebrero + mTotAMarzo + mTotAAbril + mTotAMayo + mTotAJunio + mTotAJulio + mTotAAgosto + mTotASeptiembre + mTotAOctubre + mTotANoviembre + mTotADiciembre;
            iTotCargos = mTotREnero + mTotRFebrero + mTotRMarzo + mTotRAbril + mTotRMayo + mTotRJunio + mTotRJulio + mTotRAgosto + mTotRSeptiembre + mTotROctubre + mTotRNoviembre + mTotRDiciembre;
            String pattern = "############.##";
            DecimalFormat myFormatter = new DecimalFormat(pattern);
            double iDiferencia = 0;
            if (iTotAbonos != 0 && iTotCargos != 0) {
                iDiferencia = com.syc.contable.util.Math.truncate(iTotAbonos, 2) - com.syc.contable.util.Math.truncate(iTotCargos, 2);
                if ((com.syc.contable.util.Math.truncate(mTotAEnero, 2) - com.syc.contable.util.Math.truncate(mTotREnero, 2)) != 0) {
                    // cTipoAdecuacion="5";
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Enero." + "\\n";
                }
                if ((com.syc.contable.util.Math.truncate(mTotAFebrero, 2) - com.syc.contable.util.Math.truncate(mTotRFebrero, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Febrero." + "\\n";
                }
                if ((com.syc.contable.util.Math.truncate(mTotAMarzo, 2) - com.syc.contable.util.Math.truncate(mTotRMarzo, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Marzo." + "\\n";
                }
                if ((com.syc.contable.util.Math.truncate(mTotAAbril, 2) - com.syc.contable.util.Math.truncate(mTotRAbril, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Marzo." + "\\n";
                }
                if ((com.syc.contable.util.Math.truncate(mTotAMayo, 2) - com.syc.contable.util.Math.truncate(mTotRMayo, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Mayo." + "\\n";
                }
                if ((com.syc.contable.util.Math.truncate(mTotAJunio, 2) - com.syc.contable.util.Math.truncate(mTotRJunio, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Junio." + "\\n";
                }
                if ((com.syc.contable.util.Math.truncate(mTotAJulio, 2) - com.syc.contable.util.Math.truncate(mTotRJulio, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Julio." + "\\n";
                }
                if ((com.syc.contable.util.Math.truncate(mTotAAgosto, 2) - com.syc.contable.util.Math.truncate(mTotRAgosto, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Agosto." + "\\n";
                }
                if ((com.syc.contable.util.Math.truncate(mTotASeptiembre, 2) - com.syc.contable.util.Math.truncate(mTotRSeptiembre, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Septiembre." + "\\n";
                }
                if ((com.syc.contable.util.Math.truncate(mTotAOctubre, 2) - com.syc.contable.util.Math.truncate(mTotROctubre, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Octubre." + "\\n";
                }
                if ((com.syc.contable.util.Math.truncate(mTotANoviembre, 2) - com.syc.contable.util.Math.truncate(mTotRNoviembre, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Noviembre." + "\\n";
                }
                if ((com.syc.contable.util.Math.truncate(mTotADiciembre, 2) - com.syc.contable.util.Math.truncate(mTotRDiciembre, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Diciembre." + "\\n";
                }
            }
            String cTotAbonos = new String(myFormatter.format(iTotAbonos));
            String cTotCargos = new String(myFormatter.format(iTotCargos));
            if (iDiferencia != 0) {
                cMensajeAdec += "Calendario Anual no Compensado." + "\\n";
            }
            if ("S".equals(cDocumentoHaplicado)) {
                cMensajeAdec = "";
                cMensajeAdecSaldos = "";
            }
            if ("0".equals(cTotAbonos) && !"0".equals(cTotCargos)) {
                // REduccion liquida todos pueden hcerla
                cMensajeAdec = "";
            } else if (!"0".equals(cTotAbonos) && "0".equals(cTotCargos)) {
                // ampleaciones Liquidas todos pueden hacerla
                cMensajeAdec = "";
            }
            // Valida para Cambio de Invercion a Gasto Corriente
            if (cMensajeAdecSaldos.length() > 0)
                cMensajeAdec = cMensajeAdecSaldos;
            // fin de Validacion
            arrmMontosCalendario.add(cTotCargos);
            arrmMontosCalendario.add(cTotAbonos);
            arrmMontosCalendario.add(new String(myFormatter.format(iDiferencia)));
            arrmMontosCalendario.add(cNivel);
            arrmMontosCalendario.add(cMensajeAdec);
            arrmMontosCalendario.add("");
            System.out.println("Termina la carga Generar datos de base de datos.");
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
        return arrmMontosCalendario;
    }

    public static ArrayList<Object> buscaEP(String cPPC, String cOGTOC, String cTGC, String cFFC, String cUEC, Connection conn, String cnCodigo, String aEjercicioFiscal) throws SQLException {
        ArrayList<Object> arrmEPsSaldos = new ArrayList<>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String cQueryConstructor = "SELECT distinct s.ClaveSIAFF, s.ClaveInterna, s.nClaveCNA, s.MontoAbril,s.MontoAgosto,s.MontoAnual,s.MontoDiciembre " + ", s.MontoEnero, s.MontoFebrero, s.MontoJulio, s.MontoJunio, s.MontoMarzo, s.MontoMayo, s.MontoNoviembre, s.MontoOctubre, s.MontoSeptiembre";
        cQueryConstructor += "  FROM vSaldosAnuales s WITH (nolock) ";
        cQueryConstructor += " WHERE aEjercicioFiscal='" + aEjercicioFiscal + "' ";
        cQueryConstructor += "   AND nCuentaP = '82106' ";
        if (!cPPC.equals("")) {
            cQueryConstructor += "   AND cProgramaPresupuestario = '" + cPPC + "'";
        }
        if (!cOGTOC.equals("")) {
            cQueryConstructor += "   AND cPartida = '" + cOGTOC + "'";
        }
        if (!cTGC.equals("")) {
            cQueryConstructor += "   AND cTipoGasto =  '" + cTGC + "'";
        }
        if (!cFFC.equals("")) {
            cQueryConstructor += "   AND cFuenteFinanciamiento = '" + cFFC + "'";
        }
        try {
            pstmnt = conn.prepareStatement(cQueryConstructor);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                Saldo epSaldoDis = new Saldo();
                epSaldoDis.setClaveSIAFF(rs.getString("ClaveSIAFF"));
                epSaldoDis.setClaveInterna(rs.getString("ClaveInterna"));
                epSaldoDis.setClaveCNA(rs.getString("nClaveCNA"));
                epSaldoDis.setMontoAbril(rs.getString("MontoAbril"));
                epSaldoDis.setMontoAgosto(rs.getString("MontoAgosto"));
                epSaldoDis.setMontoAnual(rs.getString("MontoAnual"));
                epSaldoDis.setMontoDiciembre(rs.getString("MontoDiciembre"));
                epSaldoDis.setMontoEnero(rs.getString("MontoEnero"));
                epSaldoDis.setMontoFebrero(rs.getString("MontoFebrero"));
                epSaldoDis.setMontoJulio(rs.getString("MontoJulio"));
                epSaldoDis.setMontoJunio(rs.getString("MontoJunio"));
                epSaldoDis.setMontoMarzo(rs.getString("MontoMarzo"));
                epSaldoDis.setMontoMayo(rs.getString("MontoMayo"));
                epSaldoDis.setMontoNoviembre(rs.getString("MontoNoviembre"));
                epSaldoDis.setMontoOctubre(rs.getString("MontoOctubre"));
                epSaldoDis.setMontoSeptiembre(rs.getString("MontoSeptiembre"));
                arrmEPsSaldos.add(epSaldoDis);
            }
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
        return arrmEPsSaldos;
    }

    public static void autorizaAdecuacion(Connection conn, Caso c, String nNumSicop, String fSicop, String nNumMAP, String fMAP, String cSuperReduccion, String prefixPath, String uLogin, String cCentroContable) throws SQLException, Exception {
        PreparedStatement pstmnt = null;
        int nIdCaso;
        String cSQLString = "";
        ResultSet rs = null;
        int nExiste = 0;
        String cQueryDetalle = "";
        String cQueryUpdate = " ";
        try {
            nIdCaso = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
            cQueryDetalle = "INSERT INTO tADECUACIONAUTDetalle(nDocRenglon, EP, cEvento, mImporte, mImporteNegativo, cMes, nFolioAdecuacionaut, cCentroContable) ";
            if (!"SI".equals(cSuperReduccion)) {
                cQueryDetalle += " SELECT nDocRenglon, EP, " + " CASE WHEN SUBSTRING(EP,40,1) <> '4' " + " 		THEN CASE WHEN SUBSTRING(cEvento,1,3) = 'AMP' THEN 'AMP005' " + " 				  WHEN SUBSTRING(cEvento,1,3) = 'RED' THEN 'RED005' END " + " 	WHEN SUBSTRING(EP,40,1) = '4' " + "			THEN CASE WHEN SUBSTRING(cEvento,1,3) = 'AMP' THEN 'AMP006' " + "				 	  WHEN SUBSTRING(cEvento,1,3) = 'RED' THEN 'RED006'END END AS cEvento " + " , mImporte, mImporteNegativo, det.cMes, det.nFolioAdecuacion, cCentroContable " + " FROM tAdecuacionDetalle det WITH (NOLOCK) INNER JOIN dbo.tAdecuacionEncabezado enc  WITH (NOLOCK) " + " ON det.nFolioAdecuacion = enc.nFolioAdecuacion WHERE det.nFolioAdecuacion = " + nIdCaso;
            } else {
                cQueryDetalle += "SELECT ROW_NUMBER() OVER(order by d.EP desc)  AS nDocREnglon, d.EP, 'REDPRES002' as cEvento " + "   , sum(case substring(d.cEvento,1,1)  when 'R' then d.mImporte when 'L' then d.mImporte*-1  end) as mImporte " + "   , sum(case substring(d.cEvento,1,1)  when 'R' then d.mImporte when 'L' then d.mImporte*-1  end)*-1 as mImporteNegativo " + "   , d.cMes, " + nIdCaso + " as nFolioAdecuacionaut, '" + cCentroContable + "' as cCentroContable " + "  FROM tAdecuacionEncabezado a with (nolock) " + "     , tAdecuacionDetalle d with (nolock) " + " WHERE a.nFolioAdecuacion = d.nFolioAdecuacion " + "   AND a.cSuperAdecuacion = 'SI' " + "   and a.cDocumentoHaplicado = 'S' " + "   AND substring(d.cEvento,1,1)  in ('R','L') " + "group by d.cMes, d.EP " + "order by d.EP, d.cMes";
                cQueryUpdate = "update tAdecuacionEncabezado  set nFolioConsolidacion = " + nIdCaso + "  where cSuperAdecuacion = 'SI' AND cDocumentoHaplicado = 'S' and nFolioConsolidacion is null and nFolioAdecuacion =" + nIdCaso;
            }
            cSQLString = "select COUNT(*) from tADECUACIONAUTEncabezado with (nolock) WHERE nFolioAdecuacionaut = " + nIdCaso;
            pstmnt = conn.prepareStatement(cSQLString);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                nExiste = rs.getInt(1);
            }
            if (nExiste == 0) {
                pstmnt = conn.prepareStatement("INSERT INTO tADECUACIONAUTEncabezado (nFolioAdecuacionaut, fCarga, cTipoAdecuacion, cRamo, cUnidadResponsable, aEjercicioFiscal, u_login, " + "nNumSicop, nNumMAP, cTipoPoliza, fAplicacion,cdescripcionpoliza, fSicop, fMAP) SELECT nFolioAdecuacion, fCarga, cTipoAdecuacion, cRamo,cUnidadResponsable, aEjercicioFiscal, " + "u_login, ?, ?, 'DI', CONVERT(DATE, ? ,103),'AUTORIZACION DE ADECUACION PRESUPUESTAL FOLIO ' + CAST(nFolioAdecuacion as varchar),CONVERT(DATE, ? ,103),CONVERT(DATE, ? ,103)  FROM tAdecuacionEncabezado WITH (NOLOCK) WHERE nFolioAdecuacion = ?");
                pstmnt.setString(1, nNumSicop);
                pstmnt.setString(2, nNumMAP);
                pstmnt.setString(3, fMAP);
                pstmnt.setString(4, fSicop);
                pstmnt.setString(5, fMAP);
                pstmnt.setInt(6, nIdCaso);
                pstmnt.execute();
                pstmnt.close();
                pstmnt = conn.prepareStatement(cQueryDetalle);
                // pstmnt.setInt(1, nIdCaso);
                pstmnt.execute();
                if ("SI".equals(cSuperReduccion)) {
                    if (pstmnt != null)
                        pstmnt.close();
                    pstmnt = null;
                    pstmnt = conn.prepareStatement(cQueryUpdate);
                    pstmnt.execute();
                    CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
                    // avanza casos
                    String cQueryAndec = "SELECT id_caso " + "   FROM tAdecuacionEncabezado a " + "  where cSuperAdecuacion      = 'SI' " + "    AND a.cDocumentoHaplicado = 'S' " + "    AND nFolioConsolidacion   = " + nIdCaso;
                    if (pstmnt != null)
                        pstmnt.close();
                    pstmnt = null;
                    if (rs != null)
                        rs.close();
                    rs = null;
                    int id_caso_super = 0;
                    pstmnt = conn.prepareStatement(cQueryAndec);
                    rs = pstmnt.executeQuery();
                    while (rs.next()) {
                        id_caso_super = rs.getInt(1);
                        Caso cReloaded = cbl.getCaso(id_caso_super);
                        cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "REVISORES_ADECUACIONES" }, new String[] { "revision_adecuacion" }, new HashMap(), prefixPath);
                    }
                }
            }
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
    }

    public static ArrayList<Integer> consultaxIntegrar(Connection conn, String cCasos, String cTipoAdecuacion) throws SQLException {
        ArrayList<Integer> arrResult = new ArrayList<>();
        String cQueryConstructor = "";
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        int nFolioAnterior = 0;
        try {
            cQueryConstructor = "SELECT h.nFolioAdecuacion, h.fCarga, h.fAplicacion, h.cRamo, h.cUnidadResponsable, h.cJustificacion, cEvento, SUM(mImporte) AS mMontoAdecua ";
            cQueryConstructor += " FROM tAdecuacionEncabezado h WITH (nolock) ";
            cQueryConstructor += "    , tAdecuacionDetalle d WITH (nolock) ";
            cQueryConstructor += " WHERE h.nFolioAdecuacion = d.nFolioAdecuacion ";
            cQueryConstructor += "   AND (h.nFolioAdecuacion in ( ";
            cQueryConstructor += "       SELECT SUBSTRING(CD_VALOR, 10, len(CD_VALOR)) ";
            cQueryConstructor += "         FROM CG_CASO_DATO WITH (nolock) ";
            cQueryConstructor += "        WHERE (ID_TC = 3) AND (ID_CASO in (" + cCasos + ")) ";
            cQueryConstructor += "          AND (ID_CD = 1) and CD_VALOR is not null )) ";
            cQueryConstructor += "   AND h.cDocumentoHaplicado ='S' ";
            cQueryConstructor += "   AND h.cTipoAdecuacion = '" + cTipoAdecuacion + "' ";
            cQueryConstructor += "GROUP BY h.nFolioAdecuacion, h.fCarga, h.fAplicacion, h.cRamo, h.cUnidadResponsable, h.cJustificacion, cEvento ";
            cQueryConstructor += " ORDER BY h.nFolioAdecuacion ";
            pstmnt = conn.prepareStatement(cQueryConstructor);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                // Adecuacion adecuaIntegra = new Adecuacion();
                if (nFolioAnterior == rs.getInt("nFolioAdecuacion")) {
                    continue;
                } else {
                    nFolioAnterior = rs.getInt("nFolioAdecuacion");
                }
            }
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
        return arrResult;
    }

    public static int buscaEPCatalogo(Connection conn, String cEP) throws SQLException {
        int iexiste = 0;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String cQueryConstructor = "SELECT 1 FROM tCatalogoEP WITH (NOLOCK) WHERE ep = ?";
        try {
            pstmnt = conn.prepareStatement(cQueryConstructor);
            pstmnt.setString(1, cEP);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                iexiste = rs.getInt(1);
            }
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
        return iexiste;
    }

    public static int buscaBorraAdecuacion(Connection conn, int nIdCaso) throws SQLException {
        PreparedStatement pstmnt = null;
        int iExiste = 0;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT count(*) AS Total from tAdecuacionEncabezado with (nolock) WHERE nFolioAdecuacion = ? AND cDocumentoHaplicado is null");
            pstmnt.setInt(1, nIdCaso);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                iExiste = rs.getInt("Total");
                if (iExiste > 0) {
                    borraAdecuaciondDet(conn, nIdCaso);
                    // borraAdecuacion(conn, nIdCaso);
                    iExiste = 1;
                }
            }
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
        return iExiste;
    }

    public static int borraAdecuacion(Connection conn, int nIdCaso) throws SQLException {
        PreparedStatement pstmnt = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("DELETE FROM tAdecuacionEncabezado WHERE nFolioAdecuacion=? ");
            pstmnt.setInt(1, nIdCaso);
            retval = pstmnt.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return retval;
    }

    public static int borraAdecuaciondDet(Connection conn, int nIdCaso) throws SQLException {
        PreparedStatement pstmnt = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("DELETE FROM tAdecuacionDetalle WHERE nFolioAdecuacion=? ");
            pstmnt.setInt(1, nIdCaso);
            retval = pstmnt.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return retval;
    }

    public static ArrayList<String> validaEstructuraProgramatica(Connection conn, String cEPValidar, String aEjercicioFiscal, String cSubcuenta, int iNumRenglon, String cTipoRenglon) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        ArrayList<String> arrMResult = new ArrayList<String>();
        String[] arrayComponentes = cEPValidar.split("\\.");
        int nBloquesEP = arrayComponentes.length;
        int nBloquesCP = 0;
        int i = 0;
        int iExisteAP = 0;
        try {
            i = 1;
            nBloquesCP = PresupuestoManager.maxNumEP(conn, aEjercicioFiscal);
            if (nBloquesEP == nBloquesCP) {
                while (i - 1 < nBloquesEP) {
                    arrMResult.addAll(PresupuestoManager.validaEPDetalle(conn, aEjercicioFiscal, i, arrayComponentes[i - 1], iNumRenglon));
                    // verifica que exista la EP solo para reducciones
                    if ("R".equals(cTipoRenglon)) {
                        iExisteAP = buscaEPCatalogo(conn, cEPValidar);
                        if (iExisteAP == 0) {
                            arrMResult.add("Error: la Reducción de la secuencia: (" + iNumRenglon + ") Clave Presupuestal No Existe. Por este motivo también se reportara como no compensada.");
                        }
                    }
                    i++;
                }
            } else {
                arrMResult.add("Error: Clave Presupuestal con estructura incorrecta de la secuencia: (" + iNumRenglon + ").Por este motivo también se reportara como no compensada.");
            }
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
        return arrMResult;
    }

    public static int modificaAdecuacion(Connection conn, int nFolioAdecuacion, String nFolioTramiteSicop, String nFolioTramiteMAP) throws SQLException {
        PreparedStatement pstmnt = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("update tAdecuacionEncabezado SET nFolioTramiteSicop=?, nFolioTramiteMAP=? WHERE nFolioAdecuacion=? ");
            pstmnt.setString(1, nFolioTramiteSicop);
            pstmnt.setString(2, nFolioTramiteMAP);
            pstmnt.setInt(3, nFolioAdecuacion);
            retval = pstmnt.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return retval;
    }

    public static String BuscaEPDuplicada(Connection conn, int nFolioAdecuacion, String cEP) throws SQLException {
        String cMesnaje = "";
        int iexiste = 0;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String cQueryConstructor = " select cMes, count(*) from tAdecuacionDetalle d  WITH (NOLOCK) where d.nFolioAdecuacion = ? and ep = ? and d.mImporte <> 0  GROUP BY  cMes HAVING COUNT(*)>1";
        try {
            pstmnt = conn.prepareStatement(cQueryConstructor);
            pstmnt.setInt(1, nFolioAdecuacion);
            pstmnt.setString(2, cEP);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                iexiste = rs.getInt(1);
            }
            if (iexiste > 0) {
                cMesnaje = "Error: EP Duplicada en el Archivo de EXCEL." + cEP;
            }
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
        return cMesnaje;
    }

    public static String validaMezclaNivel(Connection conn, int nFolioAdecuacion) throws SQLException {
        String cMensaje = "";
        int iexistenvII = 0;
        int iexistenvSup = 0;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        PreparedStatement pstmntii = null;
        ResultSet rsii = null;
        String cQueryConstructor = "select COUNT(*) ";
        cQueryConstructor += "  from (select count(*) as total, e.ClaveSIAFF, case substring(d.cEvento,1,1) WHEN 'R' THEN  sum(d.mImporte) end as Reduccion ";
        cQueryConstructor += "	         , case substring(d.cEvento,1,1) WHEN 'A' THEN  sum(d.mImporte) end as Adicion ";
        cQueryConstructor += "	      from tAdecuacionDetalle d  with (nolock) ";
        cQueryConstructor += "		     , tCatalogoEP e with (nolock) ";
        cQueryConstructor += "		 where d.nFolioAdecuacion = " + nFolioAdecuacion;
        cQueryConstructor += "		   AND d.EP = e.EP ";
        cQueryConstructor += "		group by e.ClaveSIAFF, substring(d.cEvento,1,1)) a ";
        cQueryConstructor += " group by a.ClaveSIAFF ";
        cQueryConstructor += " having count(*) > 1 ";
        String cQueryConstructorII = "select COUNT(*) ";
        cQueryConstructorII += "  from (select count(*) as total, e.ClaveSIAFF, case substring(d.cEvento,1,1) WHEN 'R' THEN  sum(d.mImporte) end as Reduccion ";
        cQueryConstructorII += "	         , case substring(d.cEvento,1,1) WHEN 'A' THEN  sum(d.mImporte) end as Adicion ";
        cQueryConstructorII += "	      from tAdecuacionDetalle d  with (nolock) ";
        cQueryConstructorII += "		     , tCatalogoEP e with (nolock) ";
        cQueryConstructorII += "		 where d.nFolioAdecuacion = " + nFolioAdecuacion;
        cQueryConstructorII += "		   AND d.EP = e.EP ";
        cQueryConstructorII += "		group by e.ClaveSIAFF, substring(d.cEvento,1,1)) a ";
        cQueryConstructorII += " group by a.ClaveSIAFF ";
        cQueryConstructorII += " having count(*) = 1 ";
        try {
            pstmnt = conn.prepareStatement(cQueryConstructor);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                iexistenvII = rs.getInt(1);
                log.debug("Object: {}", "tiene: " + iexistenvII + " de nivel II");
            }
            pstmntii = conn.prepareStatement(cQueryConstructorII);
            rsii = pstmntii.executeQuery();
            if (rsii.next()) {
                iexistenvSup = rsii.getInt(1);
                log.debug("Object: {}", "tiene: " + iexistenvSup + " de nivel superior a II");
            }
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(pstmntii);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rsii);
        }
        if (iexistenvII > 0 && iexistenvSup > 0) {
            cMensaje = "Error: En su afectación esta mezclando movimientos de nivel 2 con movimientos de nivel 3, 4, o 5." + "\\r\\n";
        }
        return cMensaje;
    }

    public static String verificaSaldos(Connection conn, String EP, int nFolioDocto, double mImporte, String cMes) throws SQLException {
        String cRetVal = "";
        ResultSet rs = null;
        PreparedStatement pstm = null;
        int nExiste = 0;
        try {
            pstm = conn.prepareStatement("SELECT count(*) FROM tSaldos s with (nolock) WHERE s.cSubCuenta = ?  AND s.nCuenta like '82106%' and convert(int,substring(s.nCuenta,7,5)) = ? and (s.mSaldoArrastre - ? ) < 0 AND ? > 0 ");
            pstm.setString(1, EP);
            pstm.setString(2, cMes);
            pstm.setDouble(3, com.syc.contable.util.Math.truncate(mImporte, 2));
            pstm.setDouble(4, com.syc.contable.util.Math.truncate(mImporte, 2));
            rs = pstm.executeQuery();
            if (rs.next()) {
                nExiste = rs.getInt(1);
            } else {
                nExiste = -1;
            }
        } finally {
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(rs);
        }
        if (nExiste != 0) {
            // cRetVal = "Saldo Insuficiente para la EP:"+EP+" en el Mes:"+cMes;
            cRetVal = "Saldo Insuficiente para el Mes:" + cMes + " EP: " + EP;
        }
        return cRetVal;
    }

    public static int verificaModificacionMeta(Connection conn, String subCuenta) throws SQLException {
        // obtengo
        ResultSet rs = null;
        PreparedStatement pstm = null;
        int cuenta = 0;
        try {
            pstm = conn.prepareStatement("SELECT COUNT(*) FROM tSaldos s With(NOLOCK) WHERE S.nCuenta LIKE '81102%' AND SUBSTRING(s.cSubCuenta, 1, 44) + '00000000000' = ?");
            pstm.setString(1, subCuenta);
            rs = pstm.executeQuery();
            if (rs.next()) {
                cuenta = rs.getInt(1);
            }
        } finally {
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(rs);
        }
        return cuenta;
    }

    public static int verificaModificacion(Connection conn, String subCuenta) throws SQLException {
        ResultSet rs = null;
        PreparedStatement pstm = null;
        int cuenta = 0;
        try {
            pstm = conn.prepareStatement("SELECT COUNT(*) FROM tSaldos s With(NOLOCK) WHERE S.nCuenta LIKE '81102%' AND SUBSTRING(s.cSubCuenta, 1, 55) = ?");
            pstm.setString(1, subCuenta);
            rs = pstm.executeQuery();
            if (rs.next()) {
                cuenta = rs.getInt(1);
            }
        } finally {
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(rs);
        }
        return cuenta;
    }

    public static String obtenClavesSicop(Connection conn, int clave) throws SQLException {
        // Obtiene
        // claves
        // SICOP
        // para
        // generar
        // el
        // layout
        // dependiendo
        // reduccion,
        // ampliacion
        // o
        // adecuacion
        ResultSet rs = null;
        PreparedStatement pstm = null;
        String h = "", fecha1 = "", fecha2 = "", t16 = "";
        try {
            pstm = conn.prepareStatement("SELECT * FROM tAdecuacionLayout al With(NOLOCK) WHERE al.idClave=?");
            pstm.setInt(1, clave);
            rs = pstm.executeQuery();
            if (rs.next()) {
                h = rs.getString("H");
                fecha1 = rs.getString("fecha1");
                fecha2 = rs.getString("fecha2");
                t16 = rs.getString("16");
            }
        } finally {
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(rs);
        }
        return h + "." + fecha1 + "." + fecha2 + "." + t16;
    }

    public static ArrayList<String> validaInvercionaGastoCorr(Connection conn, int nFolioAdecuacion) throws SQLException {
        CallableStatement pstmnt = null;
        ResultSet rs = null;
        ArrayList<String> arrMResult = new ArrayList<String>();
        String cNivel = "3";
        String cMensaje = "";
        String cCapituloOri = "";
        String cCapituloDest = "";
        String cTipoGastoOri = "";
        String cTipoGastoDest = "";
        String cSQlsentence = "{call fn_valida_adecuacion_validaInvercionaGastoCorr(?)}";
        try {
            pstmnt = conn.prepareCall(cSQlsentence);
            pstmnt.setInt(1, nFolioAdecuacion);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                cNivel = rs.getString("iNivel");
                cMensaje = rs.getString("cMensaje");
                cCapituloOri = rs.getString("cCaptuloOri");
                cCapituloDest = rs.getString("cCapituloDest");
                cTipoGastoOri = rs.getString("cTipoGastoOri");
                cTipoGastoDest = rs.getString("cTipoGastoDest");
            }
            if (!"3".equals(cNivel)) {
                arrMResult.add(cNivel);
                arrMResult.add(cMensaje);
                arrMResult.add(cCapituloOri);
                arrMResult.add(cCapituloDest);
                arrMResult.add(cTipoGastoOri);
                arrMResult.add(cTipoGastoDest);
            }
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
        return arrMResult;
    }

    public static int obtenFolioSicop(Connection conn, boolean aumenta) throws SQLException {
        String sqlUpdSeq = "UPDATE cf_sequence  WITH (ROWLOCK) SET seq_value = seq_value + 1 WHERE seq_name = 'SEQ_SICOPADEC'";
        String sqlInsSeq = "INSERT INTO cf_sequence (seq_name, seq_value) values ('SEQ_SICOPADEC', 1)";
        String sqlSelSeq = "SELECT seq_value FROM cf_sequence WITH (NOLOCK) WHERE seq_name = 'SEQ_SICOPADEC'";
        PreparedStatement psUpdate = null;
        PreparedStatement psInsert = null;
        PreparedStatement psSelect = null;
        ResultSet rs = null;
        int retVal = 1;
        try {
            if (aumenta) {
                psUpdate = conn.prepareStatement(sqlUpdSeq);
                if (psUpdate.executeUpdate() == 0) {
                    // Si no existe (no
                    // actualizo nada) se
                    // crea el registro
                    psInsert = conn.prepareStatement(sqlInsSeq);
                    psInsert.executeUpdate();
                }
            }
            psSelect = conn.prepareStatement(sqlSelSeq);
            rs = psSelect.executeQuery();
            if (rs.next())
                retVal = rs.getInt(1);
        } finally {
            CloseObject.closeObject(psUpdate);
            CloseObject.closeObject(psInsert);
            CloseObject.closeObject(psSelect);
            CloseObject.closeObject(rs);
        }
        return retVal;
    }

    public static boolean actualizaFolioSicopEncabezado(Connection conn, String folioSicop, int folioAdecuacion) throws SQLException {
        boolean retVal = false;
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("UPDATE tAdecuacionEncabezado SET nConsecutivoSicop=? WHERE nFolioAdecuacion=?");
            if ("0".equals(folioSicop))
                pstm.setNull(1, Types.INTEGER);
            else
                pstm.setString(1, folioSicop);
            pstm.setInt(2, folioAdecuacion);
            retVal = pstm.execute();
        } finally {
            CloseObject.closeObject(pstm);
        }
        // regresa true si hizo el update, false si falló
        return retVal;
    }

    public static int consultaConsecutivoSicop(Connection conn, int folioAdecuacion) throws SQLException {
        ResultSet rs = null;
        PreparedStatement pstm = null;
        int consecutivo = -1;
        try {
            pstm = conn.prepareStatement("SELECT * FROM tAdecuacionEncabezado WITH (NOLOCK) WHERE nFolioAdecuacion=?");
            pstm.setInt(1, folioAdecuacion);
            rs = pstm.executeQuery();
            if (rs.next()) {
                consecutivo = rs.getInt("nConsecutivoSicop");
            }
            // conn.commit();
        } finally {
            CloseObject.closeObject(pstm);
        }
        // regresa el consecutivo de SICOP
        return consecutivo;
    }

    public static ArrayList<String> validaPNRGP(Connection conn, int nFolioAdecuacion) throws SQLException {
        CallableStatement pstmnt = null;
        ResultSet rs = null;
        ArrayList<String> arrMResult = new ArrayList<String>();
        String cNivel = "3";
        String cMensaje = "";
        String cSQlsentence = "{call fn_valida_adecuacion_validaPNRGP(" + nFolioAdecuacion + ")}";
        try {
            pstmnt = conn.prepareCall(cSQlsentence);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                cMensaje = rs.getString("cmensaje");
                if (!"".equals(cMensaje)) {
                    cNivel = rs.getString("nnivel");
                }
            }
            if (!"3".equals(cNivel)) {
                arrMResult.add(cNivel);
                arrMResult.add(cMensaje);
            }
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
        return arrMResult;
    }

    public static String validaCalendario(Connection conn, int nFolioAdecuacion) throws SQLException {
        // Nunca se cambiaba el valor de cTipoAdecuacion, siempre era vacío. Se
        // regresa y luego se pregunta por él en la jsp
        ResultSet rsI = null;
        ResultSet rsII = null;
        ResultSet rsIII = null;
        PreparedStatement pstmI = null;
        PreparedStatement pstmII = null;
        PreparedStatement pstmIII = null;
        String cTipoAdecuacion = "";
        String cMes = "";
        String ClaveSIAFF = "";
        // mimporte relamente no es un importe, es un uno que
        int mImporte = 0;
        // viene en el query si hay resultados...
        // este booleano lo voy a usar para saber si
        boolean condicion = true;
        // alguna condicion falla, entonces no va a
        // ser calendario
        // este booleano lo voy a usar para
        boolean compensadoAnual = true;
        // saber si está compensado a nivel
        // anual por que ese lo usa tmb la
        // transferencia
        // verifica si esta compensado a nivel Mensual
        String cQueryI = "SELECT cMes, sum(mImporte) FROM (SELECT d.cMes, case d.cEvento when 'AMP001' then  sum(d.mImporte) else sum(d.mImporte) *-1 end  as mImporte " + "  FROM tAdecuacionDetalle d with (nolock) " + " WHERE d.nFolioAdecuacion =  " + nFolioAdecuacion + " and d.mImporte <> 0  " + " group BY d.cMes, d.cEvento) a " + " group BY cMes " + " HAVING sum(mImporte) <> 0 ";
        // verifica si esta compensado a nivel Anual
        String cQueryII = "SELECT 1, sum(mImporte) from ( " + "  select case d.cEvento when 'AMP001' then  sum(d.mImporte) else sum(d.mImporte) *-1 end  as mImporte " + "    from tAdecuacionDetalle d with (nolock) " + "   where d.nFolioAdecuacion = " + nFolioAdecuacion + " and d.mImporte <> 0 " + "  group by d.cEvento) a " + "  having sum(mImporte) <> 0";
        // verifica que no tenga Claves Duplicadas para el mismo mes A y R
        String cQueryIII = "select e.ClaveSIAFF, d.cMes, count(*) " + "   from tAdecuacionDetalle d  with (nolock) " + "      , tCatalogoEP e  with (nolock) " + "  WHERE nFolioAdecuacion = " + nFolioAdecuacion + " AND d.EP = e.EP and d.mImporte <> 0 " + " group by e.ClaveSIAFF, d.cMes " + " having count(*) > 1";
        // Verifica que aparezca la misma EP mas de 2 veces, esto es para
        // verificar que tiene una ampliación y una reducción, según entendí es
        // requisito para que sea calendario
        // String cQueryIV = "SELECT d.EP, COUNT(*) AS totalVeces " + " FROM
        // tAdecuacionDetalle d with (nolock) " + " WHERE nFolioAdecuacion = " +
        // nFolioAdecuacion + " group by d.EP having COUNT(*)>1";
        try {
            pstmI = conn.prepareStatement(cQueryI);
            rsI = pstmI.executeQuery();
            if (rsI.next()) {
                cMes = rsI.getString(1);
                condicion = false;
            }
            if ("".equals(cMes)) {
                pstmII = conn.prepareStatement(cQueryII);
                rsII = pstmII.executeQuery();
                if (rsII.next()) {
                    mImporte = rsII.getInt(1);
                    condicion = false;
                    compensadoAnual = false;
                }
            }
            if (mImporte == 0) {
                pstmIII = conn.prepareStatement(cQueryIII);
                rsIII = pstmIII.executeQuery();
                if (rsIII.next()) {
                    ClaveSIAFF = rsIII.getString(1);
                    condicion = false;
                }
            }
            if (condicion) {
                cTipoAdecuacion = "Calendario";
            } else if (compensadoAnual)
                cTipoAdecuacion = "Transferencia";
            else
                cTipoAdecuacion = "";
        } finally {
            CloseObject.closeObject(pstmI);
            CloseObject.closeObject(pstmII);
            CloseObject.closeObject(pstmIII);
            CloseObject.closeObject(rsI);
            CloseObject.closeObject(rsII);
            CloseObject.closeObject(rsIII);
        }
        return cTipoAdecuacion;
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
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(rs);
        }
        return cEjercicioFiscal;
    }

    public static int agregaFechaAplicacion(Connection conn, int nIdCaso) throws SQLException {
        PreparedStatement pstmnt = null;
        String QueryUpdate = "";
        int nResultHeder = 0;
        try {
            QueryUpdate = "UPDATE tAdecuacionEncabezado set fAplicacion = case aejerciciofiscal when YEAR(GETDATE()) then getdate() else CONVERT(date,'31/12/'+convert(varchar,YEAR(GETDATE())-1),105) end ";
            QueryUpdate += " WHERE  nFolioAdecuacion = ?";
            pstmnt = conn.prepareStatement(QueryUpdate);
            pstmnt.setInt(1, nIdCaso);
            nResultHeder = pstmnt.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return nResultHeder;
    }

    public static int updateAdecuacionDetalle(Connection conn, int iSecuencia, String cClaveEP, String cEvento, String sTotalMes, int nIdCaso, int iMes, String cCentroContable) throws SQLException {
        int iReturn = 0;
        PreparedStatement pstmnt = null;
        // Boolean retval = false;
        Double mImporteNegativo;
        try {
            mImporteNegativo = new Double(sTotalMes);
            mImporteNegativo = (mImporteNegativo * -1);
            pstmnt = conn.prepareStatement("UPDATE tAdecuacionDetalle SET mImporte=?, cMes=?, mImporteNegativo=?, cCentroContable=? " + " where nDocRenglon = ? AND EP = ? AND cEvento = ? AND nFolioAdecuacion = ?  ");
            pstmnt.setDouble(1, new Double(sTotalMes));
            pstmnt.setInt(2, iMes);
            pstmnt.setDouble(3, mImporteNegativo);
            pstmnt.setString(4, cCentroContable);
            pstmnt.setInt(5, iSecuencia);
            pstmnt.setString(6, cClaveEP);
            pstmnt.setString(7, cEvento);
            pstmnt.setInt(8, nIdCaso);
            iReturn = pstmnt.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return iReturn;
    }

    public static ArrayList<String> getResponsableArea(Connection conn, String login) throws SQLException {
        PreparedStatement pstmnt = null;
        ArrayList<String> res = new ArrayList<String>();
        ResultSet rs = null;
        // "select
        String // "select
        // d.*,
        cSQlsentence = // u.U_NOMBRE,
        "select u.U_NOMBRE+'><'+a.D_DESCRIPCION as RESPONSABLE_AREA " + // e.CARGO,
        // e.ID_AREA,
        // a.D_DESCRIPCION"
        " from CG_USUARIO_PROPIEDADES d" + ", CG_USUARIO_ROLE h" + ", CG_USUARIO_GRUPO g" + ", CG_USUARIO u" + ", CG_CAT_EMPLEADO e" + ", CG_CAT_AREAS a" + " where up_valor in (" + " SELECT UP_VALOR " + " FROM CG_USUARIO_PROPIEDADES " + " where U_LOGIN = upper('" + login + "') and" + " UP_NOMBRE = 'CCENTROCONTABLE')" + " and h.U_LOGIN = d.U_LOGIN" + " and h.R_NOMBRE = 'ADECUACIONES'" + " and d.U_LOGIN = g.U_LOGIN" + " and (g.G_NOMBRE = 'REVISORES_ADECUACIONES'" + " or g.G_NOMBRE = 'xREVISORES_ADECUACIONESx')" + " and d.U_LOGIN = u.U_LOGIN" + " and e.CE_OS_RESPONSABLE = u.U_LOGIN" + " and e.ID_AREA =  a.ID_AREA";
        try {
            pstmnt = conn.prepareStatement(cSQlsentence);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                res.add(rs.getString("RESPONSABLE_AREA"));
            }
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
        return res;
    }

    public static ArrayList<String> getResponsableIntegrador(Connection conn, String tipo) throws SQLException {
        PreparedStatement pstmnt = null;
        ArrayList<String> res = new ArrayList<String>();
        ResultSet rs = null;
        String cSQlsentence = "SELECT     u.U_NOMBRE as U_NOMBRE, u.U_EMAIL, u.U_ESTATUS, u.U_DESCRIPCION, up.U_LOGIN, up.UP_NOMBRE, up.UP_VALOR, a.D_DESCRIPCION, e.SALUTACION" + " FROM         CG_USUARIO_PROPIEDADES up, CG_USUARIO u, CG_CAT_EMPLEADO e, CG_CAT_AREAS a" + " WHERE     up.UP_NOMBRE = '" + tipo + "' AND up.U_LOGIN = u.U_LOGIN AND e.CE_OS_RESPONSABLE = u.U_LOGIN AND e.ID_AREA =  a.ID_AREA";
        try {
            pstmnt = conn.prepareStatement(cSQlsentence);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                String temp = rs.getString("SALUTACION") + " " + rs.getString("U_NOMBRE") + "><" + rs.getString("D_DESCRIPCION");
                res.add(temp);
            }
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
        return res;
    }

    public static String ValidaAdecuaciones(Connection conn, int nFolio, String aEjercicio, String login, String cUnidadResponsable, String cRamo, ArrayList arrmMontosCalendario, String cSuperReduccion, String cSRInterna) throws Exception {
        // ArrayList<String> arrValidaEP = new ArrayList<String>();
        String queryValidaVistas = "SELECT DISTINCT 'La EP [' + encabezado.ep + " + "       + " + "'] no se encuentra en su alcance. Solicite al administrador se agregue la unidad a su vista o eliminela'" + "FROM   dbo.tadecuaciondetalle encabezado WITH(nolock) " + "WHERE  nfolioadecuacion = ? " + "       AND Substring(encabezado.ep, 57, 3) NOT IN(SELECT ur " + "                                                  FROM " + "           dbo.v_vistasur vistas WITH(nolock) " + "                                                  WHERE " + "           vistas.usuario = ? " + "           AND vistas.modulo = " + "               'ADECUACIONES' " + "                                                     ) ";
        String cValidaAdicion = "NO";
        String cMensajeError = "";
        String cQueryAdminAdec = "select count(*) from CG_USUARIO_GRUPO u with (nolock) where u.U_LOGIN = '" + login + "' and u.G_NOMBRE = 'JEFATURA_ADECUACIONES'";
        String cQueryEPnExite = "select no, case substring(cEvento,1,1) when 'R' then ' Reducción ' else ' Ampliación ' end  as tipo " + " from (select isnull(e.ep,'error') as epcat, a.EP as no, a.cEvento " + "                 from tCatalogoEP e with (nolock)  RIGHT OUTER JOIN       tAdecuacionDetalle a with (nolock) on  e.ep = a.ep ";
        if ("SI".equals(cValidaAdicion)) {
            cQueryEPnExite += "				where a.nFolioAdecuacion = " + nFolio + " group by e.ep, a.EP, a.cEvento) d  ";
        } else {
            cQueryEPnExite += "				where a.nFolioAdecuacion = " + nFolio + " AND a.cEvento = 'R%' group by e.ep, a.EP, a.cEvento) d  ";
        }
        cQueryEPnExite += " where d.epcat like 'error'";
        String cQueryvSaldo = "SELECT ISNULL('Error: Saldo insuficiente en Disponible Neto, mes '+ CONVERT(VARCHAR,d.cMes) +' para la clave:'+d.ep+' Saldo Actual:'+ CONVERT(VARCHAR,s.mSaldoArrastre) +' Importe Requerido:'+ CONVERT(VARCHAR,d.mImporte) +' Saldo Faltante:'+ CONVERT(VARCHAR,(s.mSaldoArrastre - d.mImporte)*-1) " + "		,'Error: Saldo inexistente en Disponible Neto, mes '+ CONVERT(VARCHAR,d.cMes) +' para la clave:'+d.EP) " + " FROM  tSaldos s WITH (NOLOCK)  RIGHT OUTER JOIN tAdecuacionDetalle d WITH (NOLOCK)  ON  d.EP = s.cSubCuenta  AND s.nCuenta  = '82106-' +RIGHT(REPLICATE('0', 5)+CONVERT(VARCHAR(5), cMes), 5)+ '-00000-00000' " + " WHERE d.nFolioAdecuacion = " + nFolio + " AND d.cEvento like 'R%'  " + "   AND d.mImporte > 0 AND ISNULL(s.mSaldoArrastre,-1) - d.mImporte < 0 ";
        String cQueryvSaldoBruto = "SELECT ISNULL('Error: Saldo insuficiente en Disponible Bruto, mes '+ CONVERT(VARCHAR,d.cMes) +' para la clave:'+d.ep+' Saldo Actual:'+ CONVERT(VARCHAR,s.mSaldoArrastre) +' Importe Requerido:'+ CONVERT(VARCHAR,d.mImporte) +' Saldo Faltante:'+ CONVERT(VARCHAR,(s.mSaldoArrastre - d.mImporte)*-1) " + "		,'Error: Saldo inexistente en Disponible Bruto, mes '+ CONVERT(VARCHAR,d.cMes) +' para la clave:'+d.EP) " + " FROM  tSaldos s WITH (NOLOCK)  RIGHT OUTER JOIN tAdecuacionDetalle d WITH (NOLOCK)  ON  d.EP = s.cSubCuenta  AND s.nCuenta  = '82107-' +RIGHT(REPLICATE('0', 5)+CONVERT(VARCHAR(5), cMes), 5)+ '-00000-00000' " + " WHERE d.nFolioAdecuacion = " + nFolio + " AND  " + "   d.cEvento like 'R%'   " + "  AND d.mImporte > 0  " + "  AND ISNULL(s.mSaldoArrastre,-1) - d.mImporte < 0 ";
        String cQueryImporteNEgativo = "SELECT 'Error:El Importe Para la '+case substring(cEvento,1,1) when 'R' then ' Reducción ' else ' Ampliación ' end +' No puede ser Negativo('+CONVERT(VARCHAR,mImporte)+') en la clave '+EP +' ' " + " FROM tAdecuacionDetalle a with (nolock) WHERE a.mImporte < 0  and a.nFolioAdecuacion = " + nFolio + " ";
        String cQuerySaldoResSHCP = "select 'Error: en la Liberación a la Cuenta 81109 REDUCCION SHCP EN TRAMITE de la clave '+d.ep+ ' Del mes:'+ convert(varchar,d.cMes) +' Saldo Actual:'+ convert(varchar,s.mSaldoArrastre) +' Importe Requerido:'+ convert(varchar,d.mImporte) +' Saldo Faltante:'+ convert(varchar,(s.mSaldoArrastre - d.mImporte)*-1)  " + "  from tAdecuacionDetalle d with (nolock)  " + "     , tSaldos s with (nolock)  " + " where d.nFolioAdecuacion = " + nFolio + "   AND d.cEvento like 'L%' " + "   AND d.mImporte > 0  " + "   and d.EP = s.cSubCuenta  " + "   AND s.nCuenta  = '81109-' +Right(replicate('0', 5)+convert(varchar(5), cMes), 5)+ '-00000-00000'  " + "   and s.mSaldoArrastre - d.mImporte < 0 ";
        String cQueryRamoOBGTO = "select 'La '+case cEvento when 'R' then 'Reducción' when 'A' then 'Ampliación' else 'Liberación' end+ ' de la clave:'+ltrim(rtrim(EP))+' no es posible ya que esta no cumple con la relación entre fuente de financiamiento, partida y objeto de gasto' " + "  from (select r.aEjercicioFiscal, d.ep, d.nFolioAdecuacion, d.cMes, d.mImporte, substring(d.cEvento,1,1) as cEvento " + "	      from tCatalogoRamoOGTO r with (nolock) RIGHT OUTER JOIN tAdecuacionDetalle d with (nolock)  " + "	        on d.EP like  aEjercicioFiscal+'.'+ltrim(rtrim(cRamo))+'.%.'+cPartida+'.'+cTipoGasto+'.'+cFuenteFinanciamiento+'.%' " + "	     WHERE nFolioAdecuacion = " + nFolio + "  ) b " + " where aEjercicioFiscal is null ";
        String cQueryEstPRogAit = "select 'La '+case cEvento when 'R' then 'Reducción' when 'A' then 'Ampliación' else 'Liberación' end+ ' de la clave:'+ltrim(rtrim(EP))+' no es posible ya que esta no cumple con la Estructura Programática Autorizada' " + " from ( select distinct e.aEjercicioFiscal, d.ep, d.nFolioAdecuacion, d.cMes, d.mImporte, substring(d.cEvento,1,1) as cEvento " + "  from  tCatalogoEstProgAut e  with (nolock) RIGHT OUTER JOIN tAdecuacionDetalle d with (nolock)  " + "    on d.EP like  e.aEjercicioFiscal+'.'+ltrim(rtrim(cRamo))+'.'+cUnidadResponsable+'.'+cGrupoFuncional+'.'+cFuncion+'.'+cSubFuncion+'.%.'+cActividadInstitucional+'.'+cProgramaPresupuestario+'.%' " + "WHERE nFolioAdecuacion = " + nFolio + " ) a " + "WHERE a.aEjercicioFiscal is null ";
        String cBuscaEPDuplicada = "select cMes, count(*), ep " + " from tAdecuacionDetalle d  WITH (NOLOCK)  " + " where d.nFolioAdecuacion = " + nFolio + " and " + "	d.mImporte <> 0   " + "GROUP BY  cMes, ep HAVING COUNT(*)>1 ";
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        PreparedStatement pstme = null;
        ResultSet rse = null;
        PreparedStatement pstmne = null;
        ResultSet rsne = null;
        PreparedStatement pstmvs = null;
        ResultSet rsvs = null;
        PreparedStatement pstmiNeg = null;
        ResultSet rsiNeg = null;
        int iEsAdmin = 0;
        PreparedStatement pstmrogto = null;
        ResultSet rsrogto = null;
        PreparedStatement pstmOGTValPP = null;
        ResultSet rsiOGTValPP = null;
        PreparedStatement pstmDupliEP = null;
        ResultSet rsDupliEP = null;
        PreparedStatement psValidaVistas = null;
        ResultSet rsValidaVistas = null;
        try {
            log.debug("Object: {}", "Verifica si es Administrador de Adecuaciones " + new Timestamp(System.currentTimeMillis()));
            pstmnt = conn.prepareStatement(cQueryAdminAdec);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                iEsAdmin = rs.getInt(1);
            }
            if (iEsAdmin == 0) {
                psValidaVistas = conn.prepareStatement(queryValidaVistas);
                psValidaVistas.setInt(1, nFolio);
                psValidaVistas.setString(2, login);
                rsValidaVistas = psValidaVistas.executeQuery();
                while (rsValidaVistas.next()) {
                    cMensajeError += rsValidaVistas.getString(1);
                    cMensajeError += "\\r\\n";
                }
            }
            log.debug("Object: {}", "Adecuacion:" + nFolio + " Valida EP's para ver si existen " + new Timestamp(System.currentTimeMillis()));
            pstmne = conn.prepareStatement(cQueryEPnExite);
            rsne = pstmne.executeQuery();
            while (rsne.next()) {
                cMensajeError += "Error: En la " + rsne.getString(2) + " de la clave  (" + rsne.getString(1) + ") No Existe.";
                cMensajeError += "\\r\\n";
            }
            log.debug("Object: {}", "Adecuacion:" + nFolio + " Inicia Verificadion de EP Duplicadas: " + new Timestamp(System.currentTimeMillis()));
            pstmDupliEP = conn.prepareStatement(cBuscaEPDuplicada);
            rsDupliEP = pstmDupliEP.executeQuery();
            while (rsDupliEP.next()) {
                cMensajeError += "Error: Mes " + rsDupliEP.getString(1) + " afectado " + rsDupliEP.getString(2) + " veces por la clave " + rsDupliEP.getString(3) + ". Se debe netear el calendario del mes.";
                cMensajeError += "\\r\\n";
            }
            log.debug("Object: {}", "Adecuacion:" + nFolio + " Finaliza Verificadion de EP Duplicadas: " + new Timestamp(System.currentTimeMillis()));
            // valida Suficiencia de saldo
            // if (!"SI".equals(cSuperReduccion) || !"SI".equals(cSRInterna)){
            if (!"SI".equals(cSuperReduccion) && !"SI".equals(cSRInterna)) {
                log.debug("Object: {}", "Adecuacion:" + nFolio + " Inicia Suficiencia de saldo en Cuenta Disponible Neto: " + new Timestamp(System.currentTimeMillis()));
                pstmvs = conn.prepareStatement(cQueryvSaldo);
                rsvs = pstmvs.executeQuery();
                while (rsvs.next()) {
                    cMensajeError += rsvs.getString(1);
                    cMensajeError += "\\r\\n";
                }
                log.debug("Object: {}", "Adecuacion:" + nFolio + " Finaliza Suficiencia de saldo en Cuenta Disponible Neto: " + new Timestamp(System.currentTimeMillis()));
            } else {
                // int g = 0;
                if (!"SI".equals(cSRInterna)) {
                    log.debug("Object: {}", "Adecuacion:" + nFolio + " Inicia Suficiencia de saldo  en Cuenta Disponible Bruto: " + new Timestamp(System.currentTimeMillis()));
                    pstmvs = conn.prepareStatement(cQueryvSaldoBruto);
                    rsvs = pstmvs.executeQuery();
                    log.debug("Error occurred", "Se ha obtenido todos los errores he inicia el formateo de estos:" + new Timestamp(System.currentTimeMillis()));
                    while (rsvs.next()) {
                        cMensajeError += rsvs.getString(1);
                        cMensajeError += "\\r\\n";
                        // g++;
                    }
                }
                if (pstmvs != null)
                    pstmvs.close();
                pstmvs = null;
                if (rsvs != null)
                    rsvs.close();
                rsvs = null;
                // busca para liveracion de recursos
                pstmvs = conn.prepareStatement(cQuerySaldoResSHCP);
                rsvs = pstmvs.executeQuery();
                // g = 0;
                log.debug("Error occurred", "Se ha obtenido todos los errores he inicia el formateo de estos:" + new Timestamp(System.currentTimeMillis()));
                while (rsvs.next()) {
                    cMensajeError += rsvs.getString(1);
                    cMensajeError += "\\r\\n";
                    // System.out.println( g );
                    // g++;
                }
                log.debug("Object: {}", "Adecuacion:" + nFolio + " Finaliza Suficiencia de saldo en Cuenta Disponible Bruto: " + new Timestamp(System.currentTimeMillis()));
            }
            // Valida Montos Negativos en EXCEL
            System.out.println("Adecuacion:" + nFolio + " Importes en Negativo: " + new Timestamp(System.currentTimeMillis()));
            pstmiNeg = conn.prepareStatement(cQueryImporteNEgativo);
            rsiNeg = pstmiNeg.executeQuery();
            // int g = 0;
            while (rsiNeg.next()) {
                cMensajeError += rsiNeg.getString(1);
                cMensajeError += "\\r\\n";
                // g++;
            }
            System.out.println("Adecuacion:" + nFolio + " Finaliza Importes en Negativo: " + new Timestamp(System.currentTimeMillis()));
            // Validaciones de Normatividad
            pstmrogto = conn.prepareStatement(cQueryRamoOBGTO);
            rsrogto = pstmrogto.executeQuery();
            // g = 0;
            while (rsrogto.next()) {
                cMensajeError += rsrogto.getString(1);
                cMensajeError += "\\r\\n";
                // g++;
            }
            pstmOGTValPP = conn.prepareStatement(cQueryEstPRogAit);
            rsiOGTValPP = pstmOGTValPP.executeQuery();
            // g = 0;
            while (rsiOGTValPP.next()) {
                cMensajeError += rsiOGTValPP.getString(1);
                cMensajeError += "\\r\\n";
                // g++;
            }
            System.out.println("Adecuacion:" + nFolio + " Finaliza Importes en Negativo: " + new Timestamp(System.currentTimeMillis()));
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
            if (rs != null)
                rs.close();
            rs = null;
            if (pstme != null)
                pstme.close();
            pstme = null;
            if (rse != null)
                rse.close();
            rse = null;
            if (pstmne != null)
                pstmne.close();
            pstmne = null;
            if (rsne != null)
                rsne.close();
            rsne = null;
            if (pstmvs != null)
                pstmvs.close();
            pstmvs = null;
            if (rsvs != null)
                rsvs.close();
            rsvs = null;
            if (pstmrogto != null)
                pstmrogto.close();
            if (rsrogto != null)
                rsrogto.close();
            if (pstmOGTValPP != null)
                pstmOGTValPP.close();
            if (rsiOGTValPP != null)
                rsiOGTValPP.close();
            pstmrogto = null;
            rsrogto = null;
            pstmOGTValPP = null;
            rsiOGTValPP = null;
            if (pstmDupliEP != null)
                pstmDupliEP.close();
            if (rsDupliEP != null)
                rsDupliEP.close();
            pstmDupliEP = null;
            rsDupliEP = null;
            CloseObject.closeObject(rsValidaVistas, false);
            CloseObject.closeObject(psValidaVistas, false);
        }
        return cMensajeError;
    }

    public static String ObtenSiperAdecuacion(Connection conn, int nFolio, int id_Caso) throws SQLException {
        String cSuperAdecuacion = "NO";
        // and
        String cQuery = "SELECT cSuperAdecuacion FROM tAdecuacionEncabezado  WITH (NOLOCK) WHERE nFolioAdecuacion = ? ";
        // id_caso
        // =
        // ?
        // ";
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            pstm = conn.prepareStatement(cQuery);
            pstm.setInt(1, nFolio);
            rs = pstm.executeQuery();
            if (rs.next()) {
                cSuperAdecuacion = rs.getString(1);
            }
        } finally {
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(rs);
        }
        return cSuperAdecuacion;
    }

    public static String PartidasValidporPP(Connection conn, String[] arrayComponentes, String cEP) throws SQLException {
        String cMensaje = "";
        int nExiste = 0;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String cQueryPVxPP = "SELECT count(*) as nExiste FROM tCatalogoPartidaValidaPP p with (nolock) " + " WHERE p.aEjercicioFiscal = '" + arrayComponentes[0] + "' " + "   AND p.cRamo = '" + arrayComponentes[2] + "' " + "   AND p.cProgramaPresupuestario = '" + arrayComponentes[8] + "' " + "   AND p.cPartida = '" + arrayComponentes[9] + "'";
        try {
            pstm = conn.prepareStatement(cQueryPVxPP);
            rs = pstm.executeQuery();
            if (rs.next()) {
                nExiste = rs.getInt(1);
            }
            if (nExiste == 0) {
                cMensaje = "Error: La clave " + cEP + " no cumple Con Partidas Validas por Programa Presupuestario";
            }
        } finally {
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(rs);
        }
        return cMensaje;
    }

    public static ArrayList<String> getUnidades(Connection conn) throws SQLException {
        ArrayList<String> arrUnidades = new ArrayList<String>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String cSqlSelect = "SELECT '<option value=\"'+u.cUnidadResponsable+'\">'+ u.cUnidadResponsable+' '+u.D_DESCRIPCION+'</option>' FROM tCatUnidadResponsable u with (nolock) ";
        try {
            pstmnt = conn.prepareStatement(cSqlSelect);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                String temp = rs.getString(1);
                arrUnidades.add(temp);
            }
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
        return arrUnidades;
    }

    public static String integraAdecuaciones(Connection conn, int nFolio, String[] arrFolios, Usuario u) throws Exception {
        String cVretorno = "";
        String cUnidadUsuario = u.getU_UR();
        String cNFolioAdecuacion = "";
        int nFolioAdecuacion = 0;
        String[] arrnFolioAdecuacion = null;
        boolean encabezadoExiste = false;
        int i = 0;
        PreparedStatement pstmen = null;
        PreparedStatement pstsel = null;
        PreparedStatement pstmdt = null;
        PreparedStatement pstmdtu = null;
        String esCalendario = "";
        int trn = 0;
        ResultSet rs = null;
        String selectHeader = "SELECT * FROM tConsolidacionEncabezado where nFolioConsolidacion=?";
        String cInsertHeder = "INSERT INTO tConsolidacionEncabezado (nFolioCONSOLIDACION, fCreacion, cUnidadResponsableContable, cDescripcionPoliza, cJustificacion) VALUES (?,?,?,?,?)";
        String cInsertDetalle = "INSERT INTO tConsolidacionDetalle ( nFolioCONSOLIDACION, nFolioAdecuacion ) VALUES ( ?, ? )";
        try {
            pstsel = conn.prepareStatement(selectHeader);
            pstsel.setInt(1, nFolio);
            rs = pstsel.executeQuery();
            if (rs.next()) {
                encabezadoExiste = true;
            }
            if (!encabezadoExiste) {
                pstmen = conn.prepareStatement(cInsertHeder);
                pstmen.setInt(1, nFolio);
                pstmen.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                pstmen.setString(3, cUnidadUsuario);
                pstmen.setString(4, "Integracion de Adecuaciones para Capturarlas en SICOP");
                pstmen.setString(5, "");
                pstmen.execute();
            }
            while (arrFolios.length > i) {
                arrnFolioAdecuacion = arrFolios[i].split(",");
                cNFolioAdecuacion = arrnFolioAdecuacion[0];
                try {
                    nFolioAdecuacion = new Integer(cNFolioAdecuacion).intValue();
                } catch (NumberFormatException e) {
                    cNFolioAdecuacion = arrnFolioAdecuacion[0].substring(arrnFolioAdecuacion[0].lastIndexOf('-') + 1);
                    nFolioAdecuacion = new Integer(cNFolioAdecuacion).intValue();
                }
                // inserta detalle de Consolidado (Integracion de Adecuaciones)
                pstmdt = conn.prepareStatement(cInsertDetalle);
                pstmdt.setInt(1, nFolio);
                pstmdt.setInt(2, nFolioAdecuacion);
                if (!arrFolios[i].contains("FIAF"))
                    pstmdt.execute();
                // Actualiza Encavezado de Adecuacion
                String cUpdateAdecuacion = "UPDATE tAdecuacionEncabezado SET nFolioCONSOLIDACION= ? WHERE nFolioAdecuacion = ? ";
                if (arrFolios[i].contains("FIAF"))
                    cUpdateAdecuacion = "UPDATE tFIAFEncabezado SET nFolioCONSOLIDACION= ? WHERE nFolioFIAF = ? ";
                pstmdtu = conn.prepareStatement(cUpdateAdecuacion);
                pstmdtu.setInt(1, nFolio);
                pstmdtu.setInt(2, nFolioAdecuacion);
                pstmdtu.execute();
                esCalendario = AdecuacionManager.obtieneTipoAdec(conn, nFolioAdecuacion);
                if (!"Calendario".equals(esCalendario)) {
                    trn++;
                }
                i++;
            }
            // conn.commit();
        } finally {
            CloseObject.closeObject(pstmen);
            CloseObject.closeObject(pstmdt);
            CloseObject.closeObject(pstmdtu);
            CloseObject.closeObject(rs);
            // conn.rollback();
        }
        return cVretorno;
    }

    public static ArrayList<ArrayList<String>> buscaIntegrados(Connection conn, int nFolio) throws SQLException {
        ArrayList<ArrayList<String>> arrIntegracion = new ArrayList<>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        StringBuilder cSqlSelect = new StringBuilder();
        cSqlSelect.append("select distinct ca.c_folio nFolioAdecuacion ,ae.cTipoAdecuacion,ae.nNivel, ");
        cSqlSelect.append(" (SELECT case sum(case substring(d.cEvento,1,1) when 'R' then  d.mImporte * -1 else d.mImporte end) when 0 then sum(d.mImporte)/2 else sum(case substring(d.cEvento,1,1) when 'R' then  d.mImporte * -1 else d.mImporte end) end as mImporte ");
        cSqlSelect.append(" 	FROM tAdecuacionDetalle d with (nolock) ");
        cSqlSelect.append(" 	WHERE d.nFolioAdecuacion = ae.nFolioAdecuacion) as mImporte, " + " (select cd_valor from CG_CASO_DATO where id_caso=ca.ID_CASO and ID_CD=4 and ID_TC=3) as cUsuarioCreador, ");
        cSqlSelect.append(" ae.fAplicacion,substring(ca.C_FOLIO,6,3) as cUnidadResponsable, ");
        cSqlSelect.append(" '<input type=\\\"checkbox\\\" id=\\\"' + CONVERT(varchar, nFolioAdecuacion) + '\\\"/>' as cDescartar ");
        cSqlSelect.append(" from cg_caso ca (NOLOCK), cg_caso_operacion co (NOLOCK), tAdecuacionEncabezado ae (NOLOCK)");
        cSqlSelect.append(" where ca.id_caso=co.id_caso and ca.ID_TC=3");
        cSqlSelect.append(" and ae.nFolioAdecuacion=substring(ca.C_FOLIO,10,20)  and ae.nFolioConsolidacion =  " + nFolio + " ");
        cSqlSelect.append(" UNION ");
        cSqlSelect.append(" select distinct 'FIAF-'+RTRIM(ae.cUnidadResponsable)+'-'+CONVERT(varchar,ae.nFolioFIAF) nFolioAdecuacion,ae.cTipoAdecuacion,ae.nNivel,");
        cSqlSelect.append(" (SELECT case sum(case substring(d.cEvento,1,1) when 'R' then  d.mImporte * -1 else d.mImporte end) when 0 then sum(d.mImporte)/2 else sum(case substring(d.cEvento,1,1) when 'R' then  d.mImporte * -1 else d.mImporte end) end as mImporte ");
        cSqlSelect.append(" FROM tAdecuacionDetalle d with (nolock), tAdecuacionEncabezado tae with(nolock) ");
        cSqlSelect.append(" WHERE ae.nFolioFIAF=tae.nFolioFIAF and tae.nFolioAdecuacion=d.nFolioAdecuacion) as mImporte, ");
        cSqlSelect.append(" U_LOGIN as cUsuarioCreador, ae.fAplicacion, cUnidadResponsable as cUnidadResponsable, '<input type=\\\"checkbox\\\" id=\\\"F' + CONVERT(varchar, nFolioFIAF) + '\\\"/>' as cDescartar ");
        cSqlSelect.append(" from  tFIAFEncabezado ae (NOLOCK) where ae.nFolioConsolidacion=" + nFolio);
        try {
            pstmnt = conn.prepareStatement(cSqlSelect.toString());
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                ArrayList<String> arrDastosIntegracion = new ArrayList<String>();
                String tmpFolio = rs.getString(1);
                arrDastosIntegracion.add(tmpFolio);
                String tmpTipo = rs.getString(2);
                arrDastosIntegracion.add(tmpTipo);
                String tmpNivel = rs.getString(3);
                arrDastosIntegracion.add(tmpNivel);
                String tmpImporte = rs.getString(4);
                arrDastosIntegracion.add(tmpImporte);
                String tmpUsuario = rs.getString(5);
                arrDastosIntegracion.add(tmpUsuario);
                String tmpAplicado = rs.getString(6);
                arrDastosIntegracion.add(tmpAplicado);
                String tmpUnidad = rs.getString(7);
                arrDastosIntegracion.add(tmpUnidad);
                String tmpDescartar = rs.getString(8);
                arrDastosIntegracion.add(tmpDescartar);
                arrIntegracion.add(arrDastosIntegracion);
                arrDastosIntegracion = null;
            }
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
        return arrIntegracion;
    }

    public static ArrayList seleccionaIntegradaAdecCveCorta(Connection conn, int nFolioConsolidado, String cTipoAdecuacion, String U_LOGIN, String nNivel, String cRamo, String aEjercicioFiscal) throws Exception {
        ArrayList<Object> arrmMontosCalendario = new ArrayList<>();
        String cNivel = "3";
        String cUnidadResponsable;
        String cJustificacion;
        String cTipoMovDet = "";
        String cUserID = "";
        double mTotREnero = 0;
        double mTotRFebrero = 0;
        double mTotRMarzo = 0;
        double mTotRAbril = 0;
        double mTotRMayo = 0;
        double mTotRJunio = 0;
        double mTotRJulio = 0;
        double mTotRAgosto = 0;
        double mTotRSeptiembre = 0;
        double mTotROctubre = 0;
        double mTotRNoviembre = 0;
        double mTotRDiciembre = 0;
        double mTotREneroNegativo = 0;
        double mTotRFebreroNegativo = 0;
        double mTotRMarzoNegativo = 0;
        double mTotRAbrilNegativo = 0;
        double mTotRMayoNegativo = 0;
        double mTotRJunioNegativo = 0;
        double mTotRJulioNegativo = 0;
        double mTotRAgostoNegativo = 0;
        double mTotRSeptiembreNegativo = 0;
        double mTotROctubreNegativo = 0;
        double mTotRNoviembreNegativo = 0;
        double mTotRDiciembreNegativo = 0;
        double mTotAEnero = 0;
        double mTotAFebrero = 0;
        double mTotAMarzo = 0;
        double mTotAAbril = 0;
        double mTotAMayo = 0;
        double mTotAJunio = 0;
        double mTotAJulio = 0;
        double mTotAAgosto = 0;
        double mTotASeptiembre = 0;
        double mTotAOctubre = 0;
        double mTotANoviembre = 0;
        double mTotADiciembre = 0;
        double iTotAbonos = 0;
        double iTotCargos = 0;
        int nDocREnglon = 0;
        String cMensajeSecuencia = "";
        String cMensajeAdec = "";
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String sqlStament = "";
        String cDocumentoHaplicado = "";
        int i = 0;
        try {
            // NIVEL IADE
            ClasificacionAdecuacion ca;
            ca = ValidacionAdecuacionesManager.clasificaIADE(conn, nFolioConsolidado);
            pstmnt = conn.prepareStatement("SELECT ? as cTipoAdecuacion,  ? as cRamo, a.cUnidadResponsableContable, ? as aEjercicioFiscal, replace(replace(cJustificacion,'\"',''),'''','') as cJustificacion, ? as U_LOGIN, 'S' as cDocumentoHaplicado, ? as nNivel from tConsolidacionEncabezado a with (nolock) where a.nFolioCONSOLIDACION = ? ");
            pstmnt.setString(1, ca.getTipoAdecuacion());
            pstmnt.setString(2, cRamo);
            pstmnt.setString(3, aEjercicioFiscal);
            pstmnt.setString(4, U_LOGIN);
            pstmnt.setString(5, String.valueOf(ca.getNivel() == 0 ? "3" : ca.getNivel()));
            pstmnt.setInt(6, nFolioConsolidado);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                cNivel = rs.getString("nNivel");
                arrmMontosCalendario.add(cNivel);
                cRamo = rs.getString("cRamo");
                arrmMontosCalendario.add(cRamo);
                cUnidadResponsable = rs.getString("cUnidadResponsableContable");
                arrmMontosCalendario.add(cUnidadResponsable);
                aEjercicioFiscal = rs.getString("aEjercicioFiscal");
                arrmMontosCalendario.add(aEjercicioFiscal);
                cUserID = rs.getString("U_LOGIN");
                arrmMontosCalendario.add(cUserID);
                cJustificacion = rs.getString("cJustificacion");
                arrmMontosCalendario.add(cJustificacion);
                cDocumentoHaplicado = rs.getString("cDocumentoHaplicado");
                cTipoAdecuacion = rs.getString("cTipoAdecuacion");
                arrmMontosCalendario.add(cTipoAdecuacion);
            }
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
        try {
            sqlStament = " SELECT Row_number() " + "         OVER( " + "           ORDER BY cevento DESC)                   AS nDocREnglon, " + "       Substring(cevento, 1, 1)                     AS cEvento, " + "       Isnull(z.clavesiaff, Substring(a.ep, 1, 55)) AS ClaveSIAFF, " + "       z.nclavecna, " + "       Sum(menero)                                  AS mEnero, " + "       Sum(mfebrero)                                AS mFebrero, " + "       Sum(mmarzo)                                  AS mMarzo, " + "       Sum(mabril)                                  AS mAbril, " + "       Sum(mmayo)                                   AS mMayo, " + "       Sum(mjunio)                                  AS mJunio, " + "       Sum(mjulio)                                  AS mJulio, " + "       Sum(magosto)                                 AS mAgosto, " + "       Sum(mseptiembre)                             AS mSeptiembre, " + "       Sum(moctubre)                                AS mOctubre, " + "       Sum(mnoviembre)                              AS mNoviembre, " + "       Sum(mdiciembre)                              AS mDiciembre, " + "       Sum(meneronegativo)                          AS mEneroNegativo, " + "       Sum(mfebreronegativo)                        AS mFebreroNegativo, " + "       Sum(mmarzonegativo)                          AS mMarzoNegativo, " + "       Sum(mabrilnegativo)                          AS mAbrilNegativo, " + "       Sum(mmayonegativo)                           AS mMayoNegativo, " + "       Sum(mjunionegativo)                          AS mJunioNegativo, " + "       Sum(mjulionegativo)                          AS mJulioNegativo, " + "       Sum(magostonegativo)                         AS mAgostoNegativo, " + "       Sum(mseptiembrenegativo)                     AS mSeptiembreNegativo, " + "       Sum(moctubrenegativo)                        AS mOctubreNegativo, " + "       Sum(mnoviembrenegativo)                      AS mNoviembreNegativo, " + "       Sum(mdiciembrenegativo)                      AS mDiciembreNegativo, " + "       Sum(menero) + Sum(mfebrero) + Sum(mmarzo) " + "       + Sum(mabril) + Sum(mmayo) + Sum(mjunio) " + "       + Sum(mjulio) + Sum(magosto) + Sum(mseptiembre) " + "       + Sum(moctubre) + Sum(mnoviembre) " + "       + Sum(mdiciembre)                            AS mAnual," + "       proyecto " + "FROM   (SELECT Substring(d.ep, 1, 55) AS ep, " + "               d.cevento, " + "               CASE d.cmes " + "                 WHEN 1 THEN Sum(d.mimporte) " + "                 ELSE 0 " + "               END                    AS mEnero, " + "               CASE d.cmes " + "                 WHEN 2 THEN Sum(d.mimporte) " + "                 ELSE 0 " + "               END                    AS mFebrero, " + "               CASE d.cmes " + "                 WHEN 3 THEN Sum(d.mimporte) " + "                 ELSE 0 " + "               END                    AS mMarzo, " + "               CASE d.cmes " + "                 WHEN 4 THEN Sum(d.mimporte) " + "                 ELSE 0 " + "               END                    AS mAbril, " + "               CASE d.cmes " + "                 WHEN 5 THEN Sum(d.mimporte) " + "                 ELSE 0 " + "               END                    AS mMayo, " + "               CASE d.cmes " + "                 WHEN 6 THEN Sum(d.mimporte) " + "                 ELSE 0 " + "               END                    AS mJunio, " + "               CASE d.cmes " + "                 WHEN 7 THEN Sum(d.mimporte) " + "                 ELSE 0 " + "               END                    AS mJulio, " + "               CASE d.cmes " + "                 WHEN 8 THEN Sum(d.mimporte) " + "                 ELSE 0 " + "               END                    AS mAgosto, " + "               CASE d.cmes " + "                 WHEN 9 THEN Sum(d.mimporte) " + "                 ELSE 0 " + "               END                    AS mSeptiembre, " + "               CASE d.cmes " + "                 WHEN 10 THEN Sum(d.mimporte) " + "                 ELSE 0 " + "               END                    AS mOctubre, " + "               CASE d.cmes " + "                 WHEN 11 THEN Sum(d.mimporte) " + "                 ELSE 0 " + "               END                    AS mNoviembre, " + "               CASE d.cmes " + "                 WHEN 12 THEN Sum(d.mimporte) " + "                 ELSE 0 " + "               END                    AS mDiciembre, " + "               CASE d.cmes " + "                 WHEN 1 THEN Sum(d.newsaldo) " + "                 ELSE 0 " + "               END                    AS mEneroNegativo, " + "               CASE d.cmes " + "                 WHEN 2 THEN Sum(d.newsaldo) " + "                 ELSE 0 " + "               END                    AS mFebreroNegativo, " + "               CASE d.cmes " + "                 WHEN 3 THEN Sum(d.newsaldo) " + "                 ELSE 0 " + "               END                    AS mMarzoNegativo, " + "               CASE d.cmes " + "                 WHEN 4 THEN Sum(d.newsaldo) " + "                 ELSE 0 " + "               END                    AS mAbrilNegativo, " + "               CASE d.cmes " + "                 WHEN 5 THEN Sum(d.newsaldo) " + "                 ELSE 0 " + "               END                    AS mMayoNegativo, " + "               CASE d.cmes " + "                 WHEN 6 THEN Sum(d.newsaldo) " + "                 ELSE 0 " + "               END                    AS mJunioNegativo, " + "               CASE d.cmes " + "                 WHEN 7 THEN Sum(d.newsaldo) " + "                 ELSE 0 " + "               END                    AS mJulioNegativo, " + "               CASE d.cmes " + "                 WHEN 8 THEN Sum(d.newsaldo) " + "                 ELSE 0 " + "               END                    AS mAgostoNegativo, " + "               CASE d.cmes " + "                 WHEN 9 THEN Sum(d.newsaldo) " + "                 ELSE 0 " + "               END                    AS mSeptiembreNegativo, " + "               CASE d.cmes " + "                 WHEN 10 THEN Sum(d.newsaldo) " + "                 ELSE 0 " + "               END                    AS mOctubreNegativo, " + "               CASE d.cmes " + "                 WHEN 11 THEN Sum(d.newsaldo) " + "                 ELSE 0 " + "               END                    AS mNoviembreNegativo, " + "               CASE d.cmes " + "                 WHEN 12 THEN Sum(d.newsaldo) " + "                 ELSE 0 " + "               END                    AS mDiciembreNegativo ," + "               proyecto " + "        FROM   (SELECT dbo.CambiaEPPlurianual(d.ep) AS ep, " + "                       d.cevento, " + "                       d.cmes, " + "                       d.mimporte, " + "                       " + nFolioConsolidado + " AS nFolioAdecuacion, " + "                       CASE Substring(cevento, 1, 1) " + "                         WHEN 'R' THEN ( d.mimporte ) " + "                         WHEN 'A' THEN ( d.mimporte ) " + "                       END AS NewSaldo," + "                       ISNULL( CONVERT(VARCHAR(32), unidadProyecto.nIdProyecto ), '00000' ) as proyecto " + "                FROM   tadecuaciondetalle d WITH (nolock) " + "					   LEFT OUTER JOIN " + "					   tUnidadProyecto unidadProyecto WITH(nolock)" + "					   ON substring( d.ep, 61,3 ) = unidadProyecto.cUnidadResponsable" + "                WHERE  d.nfolioadecuacion IN (SELECT nfolioadecuacion " + "                                              FROM " + "                       tconsolidaciondetalle d WITH ( " + "                       nolock) " + "                                              WHERE  d.nfolioconsolidacion =  " + nFolioConsolidado + "                                             )) d " + "        GROUP  BY Substring(d.ep, 1, 55), " + "                  d.cevento, " + "                  d.cmes," + "                  d.proyecto) a " + "       LEFT OUTER JOIN tcatalogoep z WITH (nolock) " + "                    ON a.ep = z.ep " + "GROUP  BY Substring(a.ep, 1, 55), " + "          a.cevento, " + "          z.clavesiaff, " + "          z.claveinterna, " + "          z.nclavecna,   " + "		  a.proyecto";
            pstmnt = conn.prepareStatement(sqlStament);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                Saldo epSaldo = new Saldo();
                i++;
                System.out.println("Secuencia:" + i + " de la Adecuacion:" + nFolioConsolidado);
                nDocREnglon = rs.getInt("nDocREnglon");
                cMensajeSecuencia = "";
                cTipoMovDet = rs.getString("cEvento");
                epSaldo.setEp(cTipoMovDet);
                if ("R".equals(cTipoMovDet)) {
                    mTotREnero = mTotREnero + new Double(rs.getString("mEnero"));
                    mTotRFebrero = mTotRFebrero + new Double(rs.getString("mFebrero"));
                    mTotRMarzo = mTotRMarzo + new Double(rs.getString("mMarzo"));
                    mTotRAbril = mTotRAbril + new Double(rs.getString("mAbril"));
                    mTotRMayo = mTotRMayo + new Double(rs.getString("mMayo"));
                    mTotRJunio = mTotRJunio + new Double(rs.getString("mJunio"));
                    mTotRJulio = mTotRJulio + new Double(rs.getString("mJulio"));
                    mTotRAgosto = mTotRAgosto + new Double(rs.getString("mAgosto"));
                    mTotRSeptiembre = mTotRSeptiembre + new Double(rs.getString("mSeptiembre"));
                    mTotROctubre = mTotROctubre + new Double(rs.getString("mOctubre"));
                    mTotRNoviembre = mTotRNoviembre + new Double(rs.getString("mNoviembre"));
                    mTotRDiciembre = mTotRDiciembre + new Double(rs.getString("mDiciembre"));
                    if (!"S".equals(cDocumentoHaplicado) && "R".equals(cTipoMovDet)) {
                        mTotREneroNegativo = new Double(rs.getString("mEneroNegativo"));
                        mTotRFebreroNegativo = new Double(rs.getString("mFebreroNegativo"));
                        mTotRMarzoNegativo = new Double(rs.getString("mMarzoNegativo"));
                        mTotRAbrilNegativo = new Double(rs.getString("mAbrilNegativo"));
                        mTotRMayoNegativo = new Double(rs.getString("mMayoNegativo"));
                        mTotRJunioNegativo = new Double(rs.getString("mJunioNegativo"));
                        mTotRJulioNegativo = new Double(rs.getString("mJulioNegativo"));
                        mTotRAgostoNegativo = new Double(rs.getString("mAgostoNegativo"));
                        mTotRSeptiembreNegativo = new Double(rs.getString("mSeptiembreNegativo"));
                        mTotROctubreNegativo = new Double(rs.getString("mOctubreNegativo"));
                        mTotRNoviembreNegativo = new Double(rs.getString("mNoviembreNegativo"));
                        mTotRDiciembreNegativo = new Double(rs.getString("mDiciembreNegativo"));
                    } else {
                        mTotREneroNegativo = 0;
                        mTotRFebreroNegativo = 0;
                        mTotRMarzoNegativo = 0;
                        mTotRAbrilNegativo = 0;
                        mTotRMayoNegativo = 0;
                        mTotRJunioNegativo = 0;
                        mTotRJulioNegativo = 0;
                        mTotRAgostoNegativo = 0;
                        mTotRSeptiembreNegativo = 0;
                        mTotROctubreNegativo = 0;
                        mTotRNoviembreNegativo = 0;
                        mTotRDiciembreNegativo = 0;
                    }
                } else {
                    mTotREneroNegativo = 0;
                    mTotRFebreroNegativo = 0;
                    mTotRMarzoNegativo = 0;
                    mTotRAbrilNegativo = 0;
                    mTotRMayoNegativo = 0;
                    mTotRJunioNegativo = 0;
                    mTotRJulioNegativo = 0;
                    mTotRAgostoNegativo = 0;
                    mTotRSeptiembreNegativo = 0;
                    mTotROctubreNegativo = 0;
                    mTotRNoviembreNegativo = 0;
                    mTotRDiciembreNegativo = 0;
                    mTotAEnero = mTotAEnero + new Double(rs.getString("mEnero"));
                    mTotAFebrero = mTotAFebrero + new Double(rs.getString("mFebrero"));
                    mTotAMarzo = mTotAMarzo + new Double(rs.getString("mMarzo"));
                    mTotAAbril = mTotAAbril + new Double(rs.getString("mAbril"));
                    mTotAMayo = mTotAMayo + new Double(rs.getString("mMayo"));
                    mTotAJunio = mTotAJunio + new Double(rs.getString("mJunio"));
                    mTotAJulio = mTotAJulio + new Double(rs.getString("mJulio"));
                    mTotAAgosto = mTotAAgosto + new Double(rs.getString("mAgosto"));
                    mTotASeptiembre = mTotASeptiembre + new Double(rs.getString("mSeptiembre"));
                    mTotAOctubre = mTotAOctubre + new Double(rs.getString("mOctubre"));
                    mTotANoviembre = mTotANoviembre + new Double(rs.getString("mNoviembre"));
                    mTotADiciembre = mTotADiciembre + new Double(rs.getString("mDiciembre"));
                }
                epSaldo.setMontoAbril(rs.getString("mAbril"));
                epSaldo.setMontoAgosto(rs.getString("mAgosto"));
                epSaldo.setMontoAnual(rs.getString("mAnual"));
                epSaldo.setMontoDiciembre(rs.getString("mDiciembre"));
                epSaldo.setMontoEnero(rs.getString("mEnero"));
                epSaldo.setMontoFebrero(rs.getString("mFebrero"));
                epSaldo.setMontoJulio(rs.getString("mJulio"));
                epSaldo.setMontoJunio(rs.getString("mJunio"));
                epSaldo.setMontoMarzo(rs.getString("mMarzo"));
                epSaldo.setMontoMayo(rs.getString("mMayo"));
                epSaldo.setMontoNoviembre(rs.getString("mNoviembre"));
                epSaldo.setMontoOctubre(rs.getString("mOctubre"));
                epSaldo.setMontoSeptiembre(rs.getString("mSeptiembre"));
                if (mTotREneroNegativo < 0) {
                    cMensajeSecuencia = "Enero";
                }
                if (mTotRFebreroNegativo < 0) {
                    cMensajeSecuencia += ", Febrero";
                }
                if (mTotRMarzoNegativo < 0) {
                    cMensajeSecuencia += ", Marzo";
                }
                if (mTotRAbrilNegativo < 0) {
                    cMensajeSecuencia += ", Abril";
                }
                if (mTotRMayoNegativo < 0) {
                    cMensajeSecuencia += ", Mayo";
                }
                if (mTotRJunioNegativo < 0) {
                    cMensajeSecuencia += ", Junio";
                }
                if (mTotRJulioNegativo < 0) {
                    cMensajeSecuencia += ", Julio";
                }
                if (mTotRAgostoNegativo < 0) {
                    cMensajeSecuencia += ", Agosto";
                }
                if (mTotRSeptiembreNegativo < 0) {
                    cMensajeSecuencia += ", Septiembre";
                }
                if (mTotROctubreNegativo < 0) {
                    cMensajeSecuencia += ", Octubre";
                }
                if (mTotRNoviembreNegativo < 0) {
                    cMensajeSecuencia += ", Noviembre";
                }
                if (mTotRDiciembreNegativo < 0) {
                    cMensajeSecuencia += ", Diciembre";
                }
                if (cMensajeSecuencia.length() > 0) {
                    cMensajeSecuencia = "Saldo Insuficiente en el renglon: " + nDocREnglon + " para los Meses:\\n" + cMensajeSecuencia;
                }
                epSaldo.setProyecto(rs.getString("proyecto"));
                epSaldo.setClaveSIAFF(rs.getString("ClaveSIAFF"));
                epSaldo.setClaveInterna("00");
                epSaldo.setClaveCNA(rs.getString("nClaveCNA"));
                epSaldo.setMensajeSecuencia(cMensajeSecuencia);
                arrmMontosCalendario.add(epSaldo);
            }
            iTotAbonos = mTotAEnero + mTotAFebrero + mTotAMarzo + mTotAAbril + mTotAMayo + mTotAJunio + mTotAJulio + mTotAAgosto + mTotASeptiembre + mTotAOctubre + mTotANoviembre + mTotADiciembre;
            iTotCargos = mTotREnero + mTotRFebrero + mTotRMarzo + mTotRAbril + mTotRMayo + mTotRJunio + mTotRJulio + mTotRAgosto + mTotRSeptiembre + mTotROctubre + mTotRNoviembre + mTotRDiciembre;
            String pattern = "############.##";
            DecimalFormat myFormatter = new DecimalFormat(pattern);
            double iDiferencia = 0;
            if (iTotAbonos != 0 && iTotCargos != 0) {
                iDiferencia = com.syc.contable.util.Math.truncate(iTotAbonos, 2) - com.syc.contable.util.Math.truncate(iTotCargos, 2);
                if ((com.syc.contable.util.Math.truncate(mTotAEnero, 2) - com.syc.contable.util.Math.truncate(mTotREnero, 2)) != 0) {
                    // cTipoAdecuacion="5";
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Enero.";
                }
                if ((com.syc.contable.util.Math.truncate(mTotAFebrero, 2) - com.syc.contable.util.Math.truncate(mTotRFebrero, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Febrero.";
                }
                if ((com.syc.contable.util.Math.truncate(mTotAMarzo, 2) - com.syc.contable.util.Math.truncate(mTotRMarzo, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Marzo.";
                }
                if ((com.syc.contable.util.Math.truncate(mTotAAbril, 2) - com.syc.contable.util.Math.truncate(mTotRAbril, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Marzo.";
                }
                if ((com.syc.contable.util.Math.truncate(mTotAMayo, 2) - com.syc.contable.util.Math.truncate(mTotRMayo, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Mayo.";
                }
                if ((com.syc.contable.util.Math.truncate(mTotAJunio, 2) - com.syc.contable.util.Math.truncate(mTotRJunio, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Junio.";
                }
                if ((com.syc.contable.util.Math.truncate(mTotAJulio, 2) - com.syc.contable.util.Math.truncate(mTotRJulio, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Julio.";
                }
                if ((com.syc.contable.util.Math.truncate(mTotAAgosto, 2) - com.syc.contable.util.Math.truncate(mTotRAgosto, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Agosto.";
                }
                if ((com.syc.contable.util.Math.truncate(mTotASeptiembre, 2) - com.syc.contable.util.Math.truncate(mTotRSeptiembre, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Septiembre.";
                }
                if ((com.syc.contable.util.Math.truncate(mTotAOctubre, 2) - com.syc.contable.util.Math.truncate(mTotROctubre, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Octubre.";
                }
                if ((com.syc.contable.util.Math.truncate(mTotANoviembre, 2) - com.syc.contable.util.Math.truncate(mTotRNoviembre, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Noviembre.";
                }
                if ((com.syc.contable.util.Math.truncate(mTotADiciembre, 2) - com.syc.contable.util.Math.truncate(mTotRDiciembre, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Diciembre.";
                }
            }
            String cTotAbonos = new String(myFormatter.format(iTotAbonos));
            String cTotCargos = new String(myFormatter.format(iTotCargos));
            if (iDiferencia != 0) {
                cMensajeAdec += "Calendario Anual no Compensado.";
            }
            if ("S".equals(cDocumentoHaplicado)) {
                cMensajeAdec = "";
            }
            if ("0".equals(cTotAbonos) && !"0".equals(cTotCargos)) {
                // REduccion liquida todos pueden hcerla
                cMensajeAdec = "";
            } else if (!"0".equals(cTotAbonos) && "0".equals(cTotCargos)) {
                // ampleaciones Liquidas todos pueden hacerla
                cMensajeAdec = "";
            }
            // Valida para Cambio de Invercion a Gasto Corriente
            // fin de Validacion
            arrmMontosCalendario.add(cTotCargos);
            arrmMontosCalendario.add(cTotAbonos);
            arrmMontosCalendario.add(new String(myFormatter.format(iDiferencia)));
            arrmMontosCalendario.add(cNivel);
            arrmMontosCalendario.add(cMensajeAdec);
            System.out.println("Termina la carga Generar datos de base de datos.");
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
        return arrmMontosCalendario;
    }

    public static ArrayList<Object> seleccionaIntegradaAdec(Connection conn, int nFolioConsolidado, String cTipoAdecuacion, String U_LOGIN, String nNivel, String cRamo, String aEjercicioFiscal) throws Exception {
        ArrayList<Object> arrmMontosCalendario = new ArrayList<>();
        // ArrayList<String> arrMResult = new ArrayList<String>();
        String cNivel = "3";
        // String cTipoAdecuacion = "";
        // String cRamo;
        String cUnidadResponsable;
        // String aEjercicioFiscal;
        String cJustificacion;
        String cTipoMovDet = "";
        String cUserID = "";
        double mTotREnero = 0;
        double mTotRFebrero = 0;
        double mTotRMarzo = 0;
        double mTotRAbril = 0;
        double mTotRMayo = 0;
        double mTotRJunio = 0;
        double mTotRJulio = 0;
        double mTotRAgosto = 0;
        double mTotRSeptiembre = 0;
        double mTotROctubre = 0;
        double mTotRNoviembre = 0;
        double mTotRDiciembre = 0;
        double mTotREneroNegativo = 0;
        double mTotRFebreroNegativo = 0;
        double mTotRMarzoNegativo = 0;
        double mTotRAbrilNegativo = 0;
        double mTotRMayoNegativo = 0;
        double mTotRJunioNegativo = 0;
        double mTotRJulioNegativo = 0;
        double mTotRAgostoNegativo = 0;
        double mTotRSeptiembreNegativo = 0;
        double mTotROctubreNegativo = 0;
        double mTotRNoviembreNegativo = 0;
        double mTotRDiciembreNegativo = 0;
        double mTotAEnero = 0;
        double mTotAFebrero = 0;
        double mTotAMarzo = 0;
        double mTotAAbril = 0;
        double mTotAMayo = 0;
        double mTotAJunio = 0;
        double mTotAJulio = 0;
        double mTotAAgosto = 0;
        double mTotASeptiembre = 0;
        double mTotAOctubre = 0;
        double mTotANoviembre = 0;
        double mTotADiciembre = 0;
        double iTotAbonos = 0;
        double iTotCargos = 0;
        int nDocREnglon = 0;
        String cMensajeSecuencia = "";
        String cMensajeAdec = "";
        // ArrayList<String> arrLResult = new ArrayList<String>();
        PreparedStatement pstmnt = null;
        // PreparedStatement pstmntu = null;
        ResultSet rs = null;
        String sqlStament = "";
        String cDocumentoHaplicado = "";
        int i = 0;
        // System.out.println("En Adecuacion Manager al inicio ");
        try {
            // NIVEL IADE
            ClasificacionAdecuacion ca;
            ca = ValidacionAdecuacionesManager.clasificaIADE(conn, nFolioConsolidado);
            pstmnt = conn.prepareStatement("SELECT ? as cTipoAdecuacion,  ? as cRamo, a.cUnidadResponsableContable, ? as aEjercicioFiscal, replace(replace(cJustificacion,'\"',''),'''','') as cJustificacion, ? as U_LOGIN, 'S' as cDocumentoHaplicado, ? as nNivel from tConsolidacionEncabezado a with (nolock) where a.nFolioCONSOLIDACION = ? ");
            pstmnt.setString(1, ca.getTipoAdecuacion());
            pstmnt.setString(2, cRamo);
            pstmnt.setString(3, aEjercicioFiscal);
            pstmnt.setString(4, U_LOGIN);
            pstmnt.setString(5, String.valueOf(ca.getNivel() == 0 ? "3" : ca.getNivel()));
            pstmnt.setInt(6, nFolioConsolidado);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                cNivel = rs.getString("nNivel");
                arrmMontosCalendario.add(cNivel);
                cRamo = rs.getString("cRamo");
                arrmMontosCalendario.add(cRamo);
                cUnidadResponsable = rs.getString("cUnidadResponsableContable");
                arrmMontosCalendario.add(cUnidadResponsable);
                aEjercicioFiscal = rs.getString("aEjercicioFiscal");
                arrmMontosCalendario.add(aEjercicioFiscal);
                cUserID = rs.getString("U_LOGIN");
                arrmMontosCalendario.add(cUserID);
                cJustificacion = rs.getString("cJustificacion");
                arrmMontosCalendario.add(cJustificacion);
                cDocumentoHaplicado = rs.getString("cDocumentoHaplicado");
                cTipoAdecuacion = rs.getString("cTipoAdecuacion");
                arrmMontosCalendario.add(cTipoAdecuacion);
            }
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
        try {
            sqlStament = "SELECT ROW_NUMBER() OVER(order by cEvento desc)  AS nDocREnglon, substring(cEvento,1,1) as cEvento, ISNULL(z.ClaveSIAFF, substring(a.EP,1,55)) as ClaveSIAFF, isnull(z.ClaveInterna,substring(a.ep,57,64)) as ClaveInterna,z.nClaveCNA " + "     ,  sum(mEnero) as mEnero, sum(mFebrero) as mFebrero, sum(mMarzo) as mMarzo, sum(mAbril) as mAbril, sum(mMayo) as mMayo, sum(mJunio) as mJunio, sum(mJulio) as mJulio, sum(mAgosto) as mAgosto, sum(mSeptiembre) as mSeptiembre, sum(mOctubre) as mOctubre, sum(mNoviembre) as mNoviembre, sum(mDiciembre) as mDiciembre " + "     ,  sum(mEneroNegativo) as mEneroNegativo, sum(mFebreroNegativo) as mFebreroNegativo, sum(mMarzoNegativo) as mMarzoNegativo, sum(mAbrilNegativo) as mAbrilNegativo " + "     ,  sum(mMayoNegativo) as mMayoNegativo, sum(mJunioNegativo) as mJunioNegativo, sum(mJulioNegativo) as mJulioNegativo, sum(mAgostoNegativo) as mAgostoNegativo " + "     ,  sum(mSeptiembreNegativo) as mSeptiembreNegativo, sum(mOctubreNegativo) as mOctubreNegativo, sum(mNoviembreNegativo) as mNoviembreNegativo, sum(mDiciembreNegativo) as mDiciembreNegativo " + "     , sum(mEnero) + sum(mFebrero) + sum(mMarzo) + sum(mAbril) + sum(mMayo) + sum(mJunio) + sum(mJulio) + sum(mAgosto) + sum(mSeptiembre) + sum(mOctubre) + sum(mNoviembre) + sum(mDiciembre) as mAnual " + "  FROM ( SELECT d.ep, d.cEvento, case d.cMes when 1 then sum(d.mImporte) else 0 end as mEnero " + "     	 , case d.cMes when 2 then sum(d.mImporte) else 0 end as mFebrero     	 , case d.cMes when 3 then sum(d.mImporte) else 0 end as mMarzo  " + "     	 , case d.cMes when 4 then sum(d.mImporte) else 0 end as mAbril       	 , case d.cMes when 5 then sum(d.mImporte) else 0 end as mMayo  " + "     	 , case d.cMes when 6 then sum(d.mImporte) else 0 end as mJunio       	 , case d.cMes when 7 then sum(d.mImporte) else 0 end as mJulio  " + "     	 , case d.cMes when 8 then sum(d.mImporte) else 0 end as mAgosto      	 , case d.cMes when 9 then sum(d.mImporte) else 0 end as mSeptiembre  " + "     	 , case d.cMes when 10 then sum(d.mImporte) else 0 end as mOctubre     	 , case d.cMes when 11 then sum(d.mImporte) else 0 end as mNoviembre  " + "     	 , case d.cMes when 12 then sum(d.mImporte) else 0 end as mDiciembre  " + "     	 , case d.cMes when 1 then sum(d.NewSaldo) else 0 end as mEneroNegativo  " + "     	 , case d.cMes when 2 then sum(d.NewSaldo) else 0 end as mFebreroNegativo  " + "     	 , case d.cMes when 3 then sum(d.NewSaldo) else 0 end as mMarzoNegativo  " + "     	 , case d.cMes when 4 then sum(d.NewSaldo) else 0 end as mAbrilNegativo  " + "     	 , case d.cMes when 5 then sum(d.NewSaldo) else 0 end as mMayoNegativo  " + "     	 , case d.cMes when 6 then sum(d.NewSaldo) else 0 end as mJunioNegativo  " + "     	 , case d.cMes when 7 then sum(d.NewSaldo) else 0 end as mJulioNegativo  " + "     	 , case d.cMes when 8 then sum(d.NewSaldo) else 0 end as mAgostoNegativo  " + "     	 , case d.cMes when 9 then sum(d.NewSaldo) else 0 end as mSeptiembreNegativo  " + "     	 , case d.cMes when 10 then sum(d.NewSaldo) else 0 end as mOctubreNegativo  " + "     	 , case d.cMes when 11 then sum(d.NewSaldo) else 0 end as mNoviembreNegativo  " + "     	 , case d.cMes when 12 then sum(d.NewSaldo) else 0 end as mDiciembreNegativo  " + "     FROM ( select d.EP, d.cEvento, d.cMes, d.mImporte, " + nFolioConsolidado + " as nFolioAdecuacion " + "		     	 		  , case substring(cEvento,1,1) when 'R' then (d.mImporte )   " + "                        WHEN 'A' then (d.mImporte) end as NewSaldo from tAdecuacionDetalle d  with (nolock) " + "		  			  WHERE d.nFolioAdecuacion IN (select nFolioAdecuacion from tConsolidacionDetalle d with (nolock) WHERE d.nFolioConsolidacion = " + nFolioConsolidado + " ) ) d  group by d.ep, d.cEvento, d.cMes ) a  LEFT OUTER JOIN  " + "           tCatalogoEP z   with (nolock)  ON a.EP =  z.EP  group by a.ep, a.cEvento, z.ClaveSIAFF, z.ClaveInterna,z.nClaveCNA ";
            pstmnt = conn.prepareStatement(sqlStament);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                Saldo epSaldo = new Saldo();
                i++;
                System.out.println("Secuencia:" + i + " de la Adecuacion:" + nFolioConsolidado);
                nDocREnglon = rs.getInt("nDocREnglon");
                cMensajeSecuencia = "";
                cTipoMovDet = rs.getString("cEvento");
                epSaldo.setEp(cTipoMovDet);
                if ("R".equals(cTipoMovDet)) {
                    mTotREnero = mTotREnero + new Double(rs.getString("mEnero"));
                    mTotRFebrero = mTotRFebrero + new Double(rs.getString("mFebrero"));
                    mTotRMarzo = mTotRMarzo + new Double(rs.getString("mMarzo"));
                    mTotRAbril = mTotRAbril + new Double(rs.getString("mAbril"));
                    mTotRMayo = mTotRMayo + new Double(rs.getString("mMayo"));
                    mTotRJunio = mTotRJunio + new Double(rs.getString("mJunio"));
                    mTotRJulio = mTotRJulio + new Double(rs.getString("mJulio"));
                    mTotRAgosto = mTotRAgosto + new Double(rs.getString("mAgosto"));
                    mTotRSeptiembre = mTotRSeptiembre + new Double(rs.getString("mSeptiembre"));
                    mTotROctubre = mTotROctubre + new Double(rs.getString("mOctubre"));
                    mTotRNoviembre = mTotRNoviembre + new Double(rs.getString("mNoviembre"));
                    mTotRDiciembre = mTotRDiciembre + new Double(rs.getString("mDiciembre"));
                    if (!"S".equals(cDocumentoHaplicado) && "R".equals(cTipoMovDet)) {
                        mTotREneroNegativo = new Double(rs.getString("mEneroNegativo"));
                        mTotRFebreroNegativo = new Double(rs.getString("mFebreroNegativo"));
                        mTotRMarzoNegativo = new Double(rs.getString("mMarzoNegativo"));
                        mTotRAbrilNegativo = new Double(rs.getString("mAbrilNegativo"));
                        mTotRMayoNegativo = new Double(rs.getString("mMayoNegativo"));
                        mTotRJunioNegativo = new Double(rs.getString("mJunioNegativo"));
                        mTotRJulioNegativo = new Double(rs.getString("mJulioNegativo"));
                        mTotRAgostoNegativo = new Double(rs.getString("mAgostoNegativo"));
                        mTotRSeptiembreNegativo = new Double(rs.getString("mSeptiembreNegativo"));
                        mTotROctubreNegativo = new Double(rs.getString("mOctubreNegativo"));
                        mTotRNoviembreNegativo = new Double(rs.getString("mNoviembreNegativo"));
                        mTotRDiciembreNegativo = new Double(rs.getString("mDiciembreNegativo"));
                    } else {
                        mTotREneroNegativo = 0;
                        mTotRFebreroNegativo = 0;
                        mTotRMarzoNegativo = 0;
                        mTotRAbrilNegativo = 0;
                        mTotRMayoNegativo = 0;
                        mTotRJunioNegativo = 0;
                        mTotRJulioNegativo = 0;
                        mTotRAgostoNegativo = 0;
                        mTotRSeptiembreNegativo = 0;
                        mTotROctubreNegativo = 0;
                        mTotRNoviembreNegativo = 0;
                        mTotRDiciembreNegativo = 0;
                    }
                } else {
                    mTotREneroNegativo = 0;
                    mTotRFebreroNegativo = 0;
                    mTotRMarzoNegativo = 0;
                    mTotRAbrilNegativo = 0;
                    mTotRMayoNegativo = 0;
                    mTotRJunioNegativo = 0;
                    mTotRJulioNegativo = 0;
                    mTotRAgostoNegativo = 0;
                    mTotRSeptiembreNegativo = 0;
                    mTotROctubreNegativo = 0;
                    mTotRNoviembreNegativo = 0;
                    mTotRDiciembreNegativo = 0;
                    mTotAEnero = mTotAEnero + new Double(rs.getString("mEnero"));
                    mTotAFebrero = mTotAFebrero + new Double(rs.getString("mFebrero"));
                    mTotAMarzo = mTotAMarzo + new Double(rs.getString("mMarzo"));
                    mTotAAbril = mTotAAbril + new Double(rs.getString("mAbril"));
                    mTotAMayo = mTotAMayo + new Double(rs.getString("mMayo"));
                    mTotAJunio = mTotAJunio + new Double(rs.getString("mJunio"));
                    mTotAJulio = mTotAJulio + new Double(rs.getString("mJulio"));
                    mTotAAgosto = mTotAAgosto + new Double(rs.getString("mAgosto"));
                    mTotASeptiembre = mTotASeptiembre + new Double(rs.getString("mSeptiembre"));
                    mTotAOctubre = mTotAOctubre + new Double(rs.getString("mOctubre"));
                    mTotANoviembre = mTotANoviembre + new Double(rs.getString("mNoviembre"));
                    mTotADiciembre = mTotADiciembre + new Double(rs.getString("mDiciembre"));
                }
                String cEP = rs.getString("ClaveInterna") + "." + rs.getString("nClaveCNA");
                epSaldo.setMontoAbril(rs.getString("mAbril"));
                epSaldo.setMontoAgosto(rs.getString("mAgosto"));
                epSaldo.setMontoAnual(rs.getString("mAnual"));
                epSaldo.setMontoDiciembre(rs.getString("mDiciembre"));
                epSaldo.setMontoEnero(rs.getString("mEnero"));
                epSaldo.setMontoFebrero(rs.getString("mFebrero"));
                epSaldo.setMontoJulio(rs.getString("mJulio"));
                epSaldo.setMontoJunio(rs.getString("mJunio"));
                epSaldo.setMontoMarzo(rs.getString("mMarzo"));
                epSaldo.setMontoMayo(rs.getString("mMayo"));
                epSaldo.setMontoNoviembre(rs.getString("mNoviembre"));
                epSaldo.setMontoOctubre(rs.getString("mOctubre"));
                epSaldo.setMontoSeptiembre(rs.getString("mSeptiembre"));
                if (mTotREneroNegativo < 0) {
                    cMensajeSecuencia = "Enero";
                }
                if (mTotRFebreroNegativo < 0) {
                    cMensajeSecuencia += ", Febrero";
                }
                if (mTotRMarzoNegativo < 0) {
                    cMensajeSecuencia += ", Marzo";
                }
                if (mTotRAbrilNegativo < 0) {
                    cMensajeSecuencia += ", Abril";
                }
                if (mTotRMayoNegativo < 0) {
                    cMensajeSecuencia += ", Mayo";
                }
                if (mTotRJunioNegativo < 0) {
                    cMensajeSecuencia += ", Junio";
                }
                if (mTotRJulioNegativo < 0) {
                    cMensajeSecuencia += ", Julio";
                }
                if (mTotRAgostoNegativo < 0) {
                    cMensajeSecuencia += ", Agosto";
                }
                if (mTotRSeptiembreNegativo < 0) {
                    cMensajeSecuencia += ", Septiembre";
                }
                if (mTotROctubreNegativo < 0) {
                    cMensajeSecuencia += ", Octubre";
                }
                if (mTotRNoviembreNegativo < 0) {
                    cMensajeSecuencia += ", Noviembre";
                }
                if (mTotRDiciembreNegativo < 0) {
                    cMensajeSecuencia += ", Diciembre";
                }
                if (cMensajeSecuencia.length() > 0) {
                    cMensajeSecuencia = "Saldo Insuficiente en el renglon: " + nDocREnglon + " para los Meses:\\n" + cMensajeSecuencia;
                }
                epSaldo.setClaveSIAFF(rs.getString("ClaveSIAFF"));
                epSaldo.setClaveInterna(rs.getString("ClaveInterna"));
                epSaldo.setClaveCNA(rs.getString("nClaveCNA"));
                epSaldo.setMensajeSecuencia(cMensajeSecuencia);
                arrmMontosCalendario.add(epSaldo);
            }
            iTotAbonos = mTotAEnero + mTotAFebrero + mTotAMarzo + mTotAAbril + mTotAMayo + mTotAJunio + mTotAJulio + mTotAAgosto + mTotASeptiembre + mTotAOctubre + mTotANoviembre + mTotADiciembre;
            iTotCargos = mTotREnero + mTotRFebrero + mTotRMarzo + mTotRAbril + mTotRMayo + mTotRJunio + mTotRJulio + mTotRAgosto + mTotRSeptiembre + mTotROctubre + mTotRNoviembre + mTotRDiciembre;
            String pattern = "############.##";
            DecimalFormat myFormatter = new DecimalFormat(pattern);
            double iDiferencia = 0;
            if (iTotAbonos != 0 && iTotCargos != 0) {
                iDiferencia = com.syc.contable.util.Math.truncate(iTotAbonos, 2) - com.syc.contable.util.Math.truncate(iTotCargos, 2);
                if ((com.syc.contable.util.Math.truncate(mTotAEnero, 2) - com.syc.contable.util.Math.truncate(mTotREnero, 2)) != 0) {
                    // cTipoAdecuacion="5";
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Enero.";
                }
                if ((com.syc.contable.util.Math.truncate(mTotAFebrero, 2) - com.syc.contable.util.Math.truncate(mTotRFebrero, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Febrero.";
                }
                if ((com.syc.contable.util.Math.truncate(mTotAMarzo, 2) - com.syc.contable.util.Math.truncate(mTotRMarzo, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Marzo.";
                }
                if ((com.syc.contable.util.Math.truncate(mTotAAbril, 2) - com.syc.contable.util.Math.truncate(mTotRAbril, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Marzo.";
                }
                if ((com.syc.contable.util.Math.truncate(mTotAMayo, 2) - com.syc.contable.util.Math.truncate(mTotRMayo, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Mayo.";
                }
                if ((com.syc.contable.util.Math.truncate(mTotAJunio, 2) - com.syc.contable.util.Math.truncate(mTotRJunio, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Junio.";
                }
                if ((com.syc.contable.util.Math.truncate(mTotAJulio, 2) - com.syc.contable.util.Math.truncate(mTotRJulio, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Julio.";
                }
                if ((com.syc.contable.util.Math.truncate(mTotAAgosto, 2) - com.syc.contable.util.Math.truncate(mTotRAgosto, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Agosto.";
                }
                if ((com.syc.contable.util.Math.truncate(mTotASeptiembre, 2) - com.syc.contable.util.Math.truncate(mTotRSeptiembre, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Septiembre.";
                }
                if ((com.syc.contable.util.Math.truncate(mTotAOctubre, 2) - com.syc.contable.util.Math.truncate(mTotROctubre, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Octubre.";
                }
                if ((com.syc.contable.util.Math.truncate(mTotANoviembre, 2) - com.syc.contable.util.Math.truncate(mTotRNoviembre, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Noviembre.";
                }
                if ((com.syc.contable.util.Math.truncate(mTotADiciembre, 2) - com.syc.contable.util.Math.truncate(mTotRDiciembre, 2)) != 0) {
                    cNivel = "5";
                    cMensajeAdec += "Calendario no Compensado en el Mes de Diciembre.";
                }
            }
            String cTotAbonos = new String(myFormatter.format(iTotAbonos));
            String cTotCargos = new String(myFormatter.format(iTotCargos));
            if (iDiferencia != 0) {
                cMensajeAdec += "Calendario Anual no Compensado.";
            }
            if ("S".equals(cDocumentoHaplicado)) {
                cMensajeAdec = "";
            }
            if ("0".equals(cTotAbonos) && !"0".equals(cTotCargos)) {
                // REduccion liquida todos pueden hcerla
                cMensajeAdec = "";
            } else if (!"0".equals(cTotAbonos) && "0".equals(cTotCargos)) {
                // ampleaciones Liquidas todos pueden hacerla
                cMensajeAdec = "";
            }
            // Valida para Cambio de Invercion a Gasto Corriente
            // fin de Validacion
            arrmMontosCalendario.add(cTotCargos);
            arrmMontosCalendario.add(cTotAbonos);
            arrmMontosCalendario.add(new String(myFormatter.format(iDiferencia)));
            arrmMontosCalendario.add(cNivel);
            arrmMontosCalendario.add(cMensajeAdec);
            System.out.println("Termina la carga Generar datos de base de datos.");
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
        return arrmMontosCalendario;
    }

    public static int desIntegra(Connection conn, int nFolio) throws SQLException {
        int nDesintegrado = 0;
        PreparedStatement pstmen = null;
        String cQueryUpdate = "UPDATE tAdecuacionEncabezado SET nFolioConsolidacion = NULL WHERE nFolioConsolidacion = ? ";
        // String cQueryUpdateFIAF = "UPDATE tFIAFEncabezado SET
        // nFolioConsolidacion = NULL WHERE nFolioConsolidacion = ? ";
        try {
            pstmen = conn.prepareStatement(cQueryUpdate);
            pstmen.setInt(1, nFolio);
            pstmen.execute();
            nDesintegrado = 1;
        } finally {
            CloseObject.closeObject(pstmen);
        }
        return nDesintegrado;
    }

    public static int updateIntegracion(Connection conn, int nFolio, String nNumSicop, String nNumMAP, String fFechaSicop, String fFechaMAP, String cMotSicop, String cMotMAP) throws SQLException {
        int retval = 0;
        PreparedStatement pstmen = null;
        String cQueryUpdate = "UPDATE tAdecuacionEncabezado SET fMAP = ?, nAutorizacionMAP=?, cMotivoRechazo=?, nFolioTramiteSicop=?, fSicop=?, cMotivoRechazoSicop=? WHERE nFolioConsolidacion = ? ";
        try {
            pstmen = conn.prepareStatement(cQueryUpdate);
            pstmen.setString(1, fFechaMAP);
            pstmen.setString(2, nNumMAP);
            pstmen.setString(3, cMotMAP);
            pstmen.setString(4, nNumSicop);
            pstmen.setString(5, fFechaSicop);
            pstmen.setString(6, cMotSicop);
            pstmen.setInt(7, nFolio);
            retval = pstmen.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmen);
        }
        return retval;
    }

    public static int consultaConsecutivoSicopIntegrado(Connection conn, int folioAdecuacion) throws SQLException {
        ResultSet rs = null;
        PreparedStatement pstm = null;
        int consecutivo = -1;
        try {
            pstm = conn.prepareStatement("SELECT *  FROM tConsolidacionEncabezado WITH (NOLOCK) where nFolioCONSOLIDACION = ?");
            pstm.setInt(1, folioAdecuacion);
            rs = pstm.executeQuery();
            if (rs.next()) {
                consecutivo = rs.getInt("nConsecutivoSicop");
            }
        } finally {
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(rs);
        }
        // regresa el consecutivo de SICOP
        return consecutivo;
    }

    public static boolean actualizaFolioSicopEncabezadoInt(Connection conn, String folioSicop, int folioAdecuacion) throws SQLException {
        int rs = 0;
        PreparedStatement pstm = null;
        int newFolio = 0;
        boolean resp = false;
        try {
            if ("0".equals(folioSicop)) {
                newFolio = obtenFolioSicop(conn, true);
            }
            pstm = conn.prepareStatement("UPDATE tConsolidacionEncabezado SET nConsecutivoSicop=? WHERE nFolioCONSOLIDACION=?");
            pstm.setInt(1, newFolio);
            pstm.setInt(2, folioAdecuacion);
            rs = pstm.executeUpdate();
            if (rs != 0) {
                resp = true;
            }
            conn.commit();
        } finally {
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(rs);
        }
        // regresa true si hizo el update, false si falló
        return resp;
    }

    public static String ValidaNcuatro(Connection conn, int nFolio) throws SQLException {
        String nNivel = "3";
        PreparedStatement pstm = null;
        ResultSet rs = null;
        PreparedStatement pstma = null;
        ResultSet rsa = null;
        PreparedStatement pstmb = null;
        ResultSet rsb = null;
        PreparedStatement pstmc = null;
        ResultSet rsc = null;
        PreparedStatement pstmh = null;
        ResultSet rsh = null;
        PreparedStatement pstmi = null;
        ResultSet rsi = null;
        PreparedStatement pstmj = null;
        ResultSet rsj = null;
        String cExiste = "";
        String cExistea = "";
        PreparedStatement pstmk = null;
        ResultSet rsk = null;
        PreparedStatement pstml = null;
        ResultSet rsl = null;
        PreparedStatement pstmm = null;
        ResultSet rsm = null;
        String cQueryA = "select e.cPartida from tAdecuacionDetalle a with (nolock), tCatalogoNivelcuatro c with (nolock) " + "	 , tCatalogoEP e with (nolock)  " + " where nFolioAdecuacion = " + nFolio + " " + "   AND a.EP = e.EP " + "   AND e.cPartida = c.cPartida";
        String cQuery = "select * from (select e.cGrupoFuncional, e.cFuncion, sum(case substring(a.cEvento,1,1) when 'R' then a.mImporte *-1 else a.mImporte end) as mImporte " + "  from tCatalogoEP e with (nolock)  " + "     , tAdecuacionDetalle a with (nolock) " + " WHERE a.nFolioAdecuacion =  " + nFolio + " " + "  AND a.EP = e.ep " + "  group by e.cGrupoFuncional, e.cFuncion ) a " + "where a.mImporte <> 0";
        // incremento a Grupo Funcional 1 (Finalidad)
        String cQueryB = " select * from (select e.ClaveSIAFF, sum(case substring(d.cEvento,1,1) when 'R' then d.mImporte *-1 else d.mImporte end) as mImporte " + "		  from tAdecuacionDetalle d with (nolock) " + "		     , tCatalogoEP e with (nolock) " + "		 WHERE d.nFolioAdecuacion =  " + nFolio + " " + "		   AND d.EP = e.ep " + "		   AND e.cGrupoFuncional = 1 " + "		 group by e.ClaveSIAFF ) a " + "		 where mImporte > 0 ";
        // reduccion a Grupo funcional 2 (finalidad)
        String cQueryC = "select * from (select e.cGrupoFuncional, sum(case substring(d.cEvento,1,1) when 'R' then d.mImporte *-1 else d.mImporte end) as mImporte " + "		  from tAdecuacionDetalle d with (nolock) " + "		     , tCatalogoEP e with (nolock) " + "		 WHERE d.nFolioAdecuacion = " + nFolio + " " + "		   AND d.EP = e.ep " + "		   AND e.cGrupoFuncional = 2 " + "		 group by e.cGrupoFuncional ) a " + "		 where mImporte < 0 ";
        String cQueryH = "select count(*) as total from (select sum(case substring(d.cEvento,1,1)  when 'R' then d.mImporte * -1 else d.mImporte end) as importe " + "   FROM tAdecuacionDetalle d with (nolock), tCatalogoEP e with (nolock)   " + "  WHERE d.nFolioAdecuacion = " + nFolio + " " + " 	 AND   d.EP = e.ep  " + " 	 AND substring(e.cPartida,1,2) = '43' ) i " + " 	where i.importe <> 0 ";
        String cQueryI = "select count(*) as total from (select e.ClaveSIAFF, sum(case substring(d.cEvento,1,1)  when 'R' then d.mImporte * -1 else d.mImporte end) as importe " + "    FROM tAdecuacionDetalle d with (nolock), tCatalogoEP e with (nolock)   " + "   WHERE d.nFolioAdecuacion = " + nFolio + " " + "     AND d.EP = e.ep  " + "     AND e.cPartida = '36101' " + "   group by e.ClaveSIAFF) i " + "   where i.importe > 0 ";
        String cQueryJ = "select count(*) from (select e.cTipoGasto, sum(case substring(d.cEvento,1,1)  when 'R' then d.mImporte * -1 else d.mImporte end) as importe " + "   FROM tAdecuacionDetalle d with (nolock), tCatalogoEP e with (nolock)   " + "  WHERE d.nFolioAdecuacion = " + nFolio + " " + "    AND d.EP = e.ep  " + "    AND e.cTipoGasto in ('3','1') " + "  group by e.cTipoGasto ) i WHERE importe <> 0 ";
        String cQueryK = "select count(*) from (SELECT e.cEntidadFederativa " + "		  FROM tAdecuacionDetalle d with (nolock) " + "	     , tCatalogoEP e with (nolock)  " + "	 WHERE nFolioAdecuacion = " + nFolio + " " + "	   AND d.EP = e.EP " + "	 group by e.cEntidadFederativa ) a ";
        String cQueryL = "select count(*) from (SELECT e.cGrupoFuncional,e.cFuncion,e.cSubFuncion,e.cProgramaGeneral,e.cActividadInstitucional,e.cProgramaPresupuestario,e.cPartida,e.cTipoGasto,e.cFuenteFinanciamiento " + "	     , SUM(case substring(cEvento,1,1) when 'A' then d.mImporte else d.mImporte *-1 end ) AS mImporte " + "	  FROM tAdecuacionDetalle d with (nolock)  " + "	     , tCatalogoEP e with (nolock)  " + "	 WHERE nFolioAdecuacion = " + nFolio + " " + "	   AND d.EP = e.EP " + "	 group by e.cGrupoFuncional,e.cFuncion,e.cSubFuncion,e.cProgramaGeneral,e.cActividadInstitucional,e.cProgramaPresupuestario,e.cPartida,e.cTipoGasto,e.cFuenteFinanciamiento ) a " + "	 where a.mImporte <> 0 ";
        String cQueryApyVol = "select sum(mImporte) AS mImporte FROM (select sum(mImporte) as mImporte, h.cPartida from ( " + "select sum(case substring(d.cEvento,1,1) when 'A' then mImporte else mImporte * -1 end) as mImporte, e.cPartida " + "  from tAdecuacionDetalle d with (nolock) " + "     , tCatalogoEP e with (nolock) " + " where nFolioAdecuacion =  " + nFolio + " " + "   AND d.EP = e.EP  " + "   AND e.cPartida in ('44101','44105')  " + "   and mImporte <> 0 " + "   group by substring(d.cEvento,1,1), e.cPartida ) h " + "   group by h.cPartida ) F";
        try {
            pstm = conn.prepareStatement(cQuery);
            rs = pstm.executeQuery();
            while (rs.next()) {
                cExiste = rs.getString("cGrupoFuncional");
                if ("3".equals(cExiste)) {
                    cExiste = "";
                    nNivel = "3";
                    break;
                } else {
                    nNivel = "4";
                }
            }
            if (!"".equals(cExiste)) {
                nNivel = "4";
            }
            pstma = conn.prepareStatement(cQueryA);
            rsa = pstma.executeQuery();
            if (rsa.next()) {
                cExistea = rsa.getString(1);
            }
            if (!"".equals(cExistea)) {
                nNivel = "4";
            }
            cExistea = "";
            pstmb = conn.prepareStatement(cQueryB);
            rsb = pstmb.executeQuery();
            if (rsb.next()) {
                cExistea = rsb.getString(1);
            }
            if (!"".equals(cExistea)) {
                nNivel = "4";
            }
            cExistea = "";
            pstmc = conn.prepareStatement(cQueryC);
            rsc = pstmc.executeQuery();
            if (rsc.next()) {
                cExistea = rsc.getString(1);
            }
            if (!"".equals(cExistea)) {
                nNivel = "4";
            }
            // ----------------------------------------------------------------------
            cExistea = "";
            pstmh = conn.prepareStatement(cQueryH);
            rsh = pstmh.executeQuery();
            if (rsh.next()) {
                cExistea = rsh.getString(1);
            }
            if (!"0".equals(cExistea)) {
                nNivel = "4";
            }
            cExistea = "";
            pstmi = conn.prepareStatement(cQueryI);
            rsi = pstmi.executeQuery();
            if (rsi.next()) {
                cExistea = rsi.getString(1);
            }
            if (!"0".equals(cExistea)) {
                nNivel = "4";
            }
            cExistea = "";
            pstmj = conn.prepareStatement(cQueryJ);
            rsj = pstmj.executeQuery();
            if (rsj.next()) {
                cExistea = rsj.getString(1);
            }
            if (Integer.parseInt(cExistea) > 1) {
                nNivel = "4";
            }
            /*
			 * Si es nivel 4 se valida si lo que cmabia es la entidad Federativo
			 * y el resto de las variables son iguales entonces se regresa a
			 * nivel 3
			 */
            if (nNivel == "4") {
                cExistea = "";
                pstmk = conn.prepareStatement(cQueryK);
                rsk = pstmk.executeQuery();
                if (rsk.next()) {
                    cExistea = rsk.getString(1);
                }
                /*
				 * Si tiene mas de un estado se verifica las demas varibales
				 */
                if (Integer.parseInt(cExistea) > 1) {
                    cExistea = "";
                    pstml = conn.prepareStatement(cQueryL);
                    rsl = pstml.executeQuery();
                    if (rsl.next()) {
                        cExistea = rsl.getString(1);
                    }
                    if (Integer.parseInt(cExistea) == 0) {
                        nNivel = "3";
                    }
                }
            }
            // se valida Apoyo A Voluntarios
            cExistea = "";
            pstmm = conn.prepareStatement(cQueryApyVol);
            rsm = pstmm.executeQuery();
            Double mImporte = 0.0;
            if (rsm.next()) {
                mImporte = rsm.getDouble(1);
            }
            if (mImporte > 0) {
                nNivel = "4";
            }
        } finally {
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(pstma);
            CloseObject.closeObject(pstmb);
            CloseObject.closeObject(pstmc);
            CloseObject.closeObject(pstmh);
            CloseObject.closeObject(pstmi);
            CloseObject.closeObject(pstmj);
            CloseObject.closeObject(pstmk);
            CloseObject.closeObject(pstml);
            CloseObject.closeObject(pstmm);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rsa);
            CloseObject.closeObject(rsb);
            CloseObject.closeObject(rsc);
            CloseObject.closeObject(rsh);
            CloseObject.closeObject(rsk);
            CloseObject.closeObject(rsl);
            CloseObject.closeObject(rsm);
            CloseObject.closeObject(rsi);
            CloseObject.closeObject(rsj);
        }
        return nNivel;
    }

    public static ArrayList validaAdecIntegrada(Connection conn, int nFolio) throws SQLException {
        // boolean rVar = false;
        ArrayList<Object> arrConsolidado = new ArrayList<>();
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int nConsecutivoSicop = 0;
        int nFolioCONSOLIDACION = 0;
        String cSuperAdecuacion = "";
        String cSRInterna = "";
        // ISNULL(h.nFolioTramiteSicop,''),
        String // ISNULL(h.nFolioTramiteSicop,''),
        // ISNULL(h.fSicop,''),ISNULL(h.nAutorizacionMAP,''),
        cQueryA = // ISNULL(h.fMAP,'')
        "select isnull(h.nConsecutivoSicop,0), isnull(h.nFolioCONSOLIDACION,0), isnull(cSuperAdecuacion,''), isnull(cSRInterna,'') " + "  FROM tConsolidacionEncabezado h with (nolock)   RIGHT OUTER JOIN  tAdecuacionEncabezado a with (nolock) on a.nFolioConsolidacion = h.nFolioCONSOLIDACION  " + " where a.nFolioAdecuacion = " + nFolio + " ";
        try {
            pstm = conn.prepareStatement(cQueryA);
            rs = pstm.executeQuery();
            if (rs.next()) {
                nConsecutivoSicop = rs.getInt(1);
                nFolioCONSOLIDACION = rs.getInt(2);
                cSuperAdecuacion = rs.getString(3);
                cSRInterna = rs.getString(4);
                // *****METI ESTAS 4 LINEAS PARA QUE NO SIEMPRE TUVIERA VALORES
                // EL ARRAY*****************//
                arrConsolidado.add(nConsecutivoSicop);
                arrConsolidado.add(nFolioCONSOLIDACION);
                arrConsolidado.add(cSuperAdecuacion);
                arrConsolidado.add(cSRInterna);
                // //***********************************************************
            }
        } catch (SQLException sql) {
            sql.printStackTrace();
        } finally {
            if (pstm != null)
                pstm.close();
            pstm = null;
            if (rs != null)
                rs.close();
            rs = null;
        }
        return arrConsolidado;
    }

    public static int validaAdecSicop(Connection conn, int nFolio) throws SQLException {
        // boolean rVar = false;
        PreparedStatement pstm = null;
        ArrayList<Integer> arrConsolidado = new ArrayList<>();
        ResultSet rs = null;
        int nConsecutivoSicop = 0;
        String cQueryA = "select isnull (nConsecutivoSicop,0) from tAdecuacionEncabezado a with (nolock) " + " where nFolioAdecuacion = " + nFolio + " AND  nConsecutivoSicop is not null ";
        try {
            pstm = conn.prepareStatement(cQueryA);
            rs = pstm.executeQuery();
            if (rs.next()) {
                nConsecutivoSicop = rs.getInt(1);
            }
            arrConsolidado.add(nConsecutivoSicop);
        } finally {
            if (pstm != null)
                pstm.close();
            pstm = null;
            if (rs != null)
                rs.close();
            rs = null;
        }
        return nConsecutivoSicop;
    }

    public static String obtienenNumMAP(Connection conn, int nFolio) throws SQLException {
        // boolean rVar = false;
        PreparedStatement pstm = null;
        ArrayList<String> arrnNumMAP = new ArrayList<>();
        ResultSet rs = null;
        String nNumMAP = "";
        String cQueryA = " select nNumMAP from tADECUACIONAUTEncabezado where nFolioAdecuacionaut= " + nFolio;
        try {
            pstm = conn.prepareStatement(cQueryA);
            rs = pstm.executeQuery();
            if (rs.next()) {
                nNumMAP = rs.getString(1);
            }
            arrnNumMAP.add(nNumMAP);
        } finally {
            if (pstm != null)
                pstm.close();
            pstm = null;
            if (rs != null)
                rs.close();
            rs = null;
        }
        return nNumMAP;
    }

    public static String obtienenNumSicop(Connection conn, int nFolio) throws SQLException {
        // boolean rVar = false;
        PreparedStatement pstm = null;
        ArrayList<String> arrnNumSicop = new ArrayList<>();
        ResultSet rs = null;
        String nNumSicop = "";
        String cQueryA = " select nNumSicop from tADECUACIONAUTEncabezado where nFolioAdecuacionaut= " + nFolio;
        try {
            pstm = conn.prepareStatement(cQueryA);
            rs = pstm.executeQuery();
            if (rs.next()) {
                nNumSicop = rs.getString(1);
            }
            arrnNumSicop.add(nNumSicop);
        } finally {
            if (pstm != null)
                pstm.close();
            pstm = null;
            if (rs != null)
                rs.close();
            rs = null;
        }
        return nNumSicop;
    }

    public static String validaNivelDos(Connection conn, int nFolio) throws SQLException {
        String cNivel = "3";
        String cQueryA = "select count(*) from ( select e.ClaveSIAFF, sum(case substring(d.cEvento,1,1) when 'R' then  d.mImporte*-1 when 'A' then d.mImporte end) as mImporte " + "	  from tAdecuacionDetalle d with (nolock), tCatalogoEP e with (nolock) " + "	 where nFolioAdecuacion =  " + nFolio + " " + "	   AND d.ep = e.EP  " + "	   group by e.ClaveSIAFF) a  " + "	   where a.mImporte <> 0 ";
        String cQueryB = "select count(*) from (select e.cUnidadEjecutora from tAdecuacionDetalle d with (nolock)  " + "	     , tCatalogoEP e with (nolock)  " + "	 where nFolioAdecuacion = " + nFolio + " " + "	   AND d.ep = e.EP  " + "	group by e.cUnidadEjecutora ) a ";
        PreparedStatement pstm = null;
        ResultSet rs = null;
        PreparedStatement pstmB = null;
        ResultSet rsB = null;
        PreparedStatement pstmC = null;
        ResultSet rsC = null;
        int nExisteA = 0;
        int nExisteB = 0;
        // int nExisteC = 0;
        try {
            pstm = conn.prepareStatement(cQueryA);
            rs = pstm.executeQuery();
            if (rs.next()) {
                nExisteA = rs.getInt(1);
            }
            if (nExisteA == 0) {
                pstmB = conn.prepareStatement(cQueryB);
                rsB = pstmB.executeQuery();
                if (rsB.next()) {
                    nExisteB = rsB.getInt(1);
                }
                if (nExisteB > 1) {
                    /*
					 * pstmC= conn.prepareStatement(cQueryC); rsC =
					 * pstmC.executeQuery(); if (rsC.next()) { nExisteC =
					 * rsC.getInt(1); } if (nExisteC == 1 ){
					 */
                    cNivel = "2";
                    // }
                }
            }
        } finally {
            if (pstm != null)
                pstm.close();
            pstm = null;
            if (rs != null)
                rs.close();
            rs = null;
            if (pstmB != null)
                pstmB.close();
            pstmB = null;
            if (rsB != null)
                rsB.close();
            rsB = null;
            if (pstmC != null)
                pstmC.close();
            pstmC = null;
            if (rsC != null)
                rsC.close();
            rsC = null;
        }
        return cNivel;
    }

    public static boolean validaRestrictivas(Connection conn, int nFolio) throws SQLException {
        boolean inibeAPL = false;
        CallableStatement pstmd = null;
        ResultSet rsd = null;
        String cExistea = "";
        // MODALIDAD ('E','G','M','N','O','P') CON cAPITULO 5000,6000
        String cQueryD = "{call fn_valida_adecuacion_validaRestrictivas(" + nFolio + ")}";
        try {
            cExistea = "";
            pstmd = conn.prepareCall(cQueryD);
            rsd = pstmd.executeQuery();
            if (rsd.next()) {
                // Jesús, porque en un
                cExistea = rsd.getString("existe");
                // string
                // si es
                // el resultado de un count
            }
            if (!"0".equals(cExistea)) {
                inibeAPL = true;
            }
        } finally {
            if (pstmd != null)
                pstmd.close();
            pstmd = null;
            if (rsd != null)
                rsd.close();
            rsd = null;
        }
        return inibeAPL;
    }

    public static String ValidaCinco(Connection conn, int nFolio, String cNivel) throws SQLException {
        String cNewNivel = "";
        String cQuery = "select count(*) from (select  sum(case d.cEvento when 'A' then d.mImporte else d.mImporte *-1 end ) as mImporte " + " from tAdecuacionDetalle d with (nolock)  " + "    , tCatalogoEP e with (nolock)  " + " where d.nFolioAdecuacion =  " + nFolio + "  AND " + "      e.EP =  d.EP " + " AND substring (e.cProgramaPresupuestario,1,1) = 'M' ) a " + " WHERE a.mImporte > 0";
        PreparedStatement pstmd = null;
        ResultSet rsd = null;
        String cExistea = "";
        try {
            cExistea = "";
            pstmd = conn.prepareStatement(cQuery);
            rsd = pstmd.executeQuery();
            if (rsd.next()) {
                cExistea = rsd.getString(1);
            }
            if (!"0".equals(cExistea)) {
                cNewNivel = "5";
            } else {
                cNewNivel = cNivel;
            }
        } finally {
            if (pstmd != null)
                pstmd.close();
            pstmd = null;
            if (rsd != null)
                rsd.close();
            rsd = null;
        }
        return cNewNivel;
    }

    public static int buscaConsecutivoSICOP(Connection conn, int nFolio) throws SQLException {
        int nConsecutivoSICOP = 0;
        PreparedStatement pstmd = null;
        ResultSet rsd = null;
        String cQuery = "select c.nConsecutivoSicop from tConsolidacionEncabezado c with (nolock) where c.nFolioCONSOLIDACION = " + nFolio + " ";
        try {
            pstmd = conn.prepareStatement(cQuery);
            rsd = pstmd.executeQuery();
            if (rsd.next()) {
                nConsecutivoSICOP = rsd.getInt(1);
            }
        } finally {
            if (pstmd != null)
                pstmd.close();
            pstmd = null;
            if (rsd != null)
                rsd.close();
            rsd = null;
        }
        return nConsecutivoSICOP;
    }

    public static ArrayList<Object> consultaIntegradas(Connection conn, int nFolio) throws SQLException {
        ArrayList<Object> arrIntegradas = new ArrayList<>();
        PreparedStatement pstmd = null;
        ResultSet rsd = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String cUnidadCreadora = "";
        String cFolioAdecuacion = "";
        String cMontoAdecuacion = "";
        String cEjercicioFiscal = "";
        String cFechaAdecuacion = "";
        String cJustificacionAdecua = "";
        String cNivelAdecua = "";
        String cTipoAdecuacion = "";
        String cFechaIntegra = "";
        double mTotalReducciones = 0;
        double mTotalAmpliaciones = 0;
        double mTotalLiberacion = 0;
        String cTipoMovimiento = "";
        int i = 0;
        String cQuery = "SELECT a.aEjercicioFiscal,a.cTipoAdecuacion, a.nNivel,a.cUnidadResponsable, a.aEjercicioFiscal+'-'+ltrim(rtrim(a.cUnidadResponsable))+'-'+convert(varchar,a.nFolioAdecuacion) " + "     , (select top 1 sum(case substring(cEvento,1,1) when 'A' then mImporte else mImporte *-1 end ) from tAdecuacionDetalle c where c.nFolioAdecuacion = a.nFolioAdecuacion group by  substring(cEvento,1,1) ) " + "	 , a.fAplicacion, a.cJustificacion " + "  FROM tConsolidacionDetalle D WITH (nolock) " + "     , tAdecuacionEncabezado a WITH (nolock)  " + " where D.nFolioCONSOLIDACION =  " + nFolio + " " + "   AND d.nFolioAdecuacion = a.nFolioAdecuacion";
        String cQueryTotales = "SELECT h.fCreacion, substring(cEvento,1,1), sum(case substring(cEvento,1,1) when 'A' then mImporte else mImporte  end ) " + "  FROM tConsolidacionDetalle D WITH (nolock)  " + "     , tConsolidacionEncabezado h WITH (nolock)   " + "     , tAdecuacionDetalle  a WITH (nolock)  " + " where D.nFolioCONSOLIDACION = " + nFolio + " " + "   AND d.nFolioAdecuacion = a.nFolioAdecuacion " + "   AND d.nFolioCONSOLIDACION = h.nFolioCONSOLIDACION " + " group by  h.fCreacion, substring(cEvento,1,1) ";
        try {
            // SACA TOTALES POR TIMPO DE mOVIMIENTO rEDUCCION AMPLIACION
            pstm = conn.prepareStatement(cQueryTotales);
            rs = pstm.executeQuery();
            while (rs.next()) {
                cFechaIntegra = rs.getString(1);
                cTipoMovimiento = rs.getString(2);
                if ("R".equals(cTipoMovimiento)) {
                    mTotalReducciones = rs.getDouble(3);
                } else if ("A".equals(cTipoMovimiento)) {
                    mTotalAmpliaciones = rs.getDouble(3);
                } else {
                    mTotalLiberacion = rs.getDouble(3);
                }
            }
            pstmd = conn.prepareStatement(cQuery);
            rsd = pstmd.executeQuery();
            while (rsd.next()) {
                ArrayList<String> arrDetIntegd = new ArrayList<>();
                if (i == 0) {
                    ArrayList<Object> arrDetInteg = new ArrayList<>();
                    cEjercicioFiscal = rsd.getString(1);
                    arrDetInteg.add(cEjercicioFiscal);
                    cTipoAdecuacion = rsd.getString(2);
                    arrDetInteg.add(cTipoAdecuacion);
                    cNivelAdecua = rsd.getString(3);
                    arrDetInteg.add(cNivelAdecua);
                    arrDetInteg.add(mTotalAmpliaciones);
                    arrDetInteg.add(mTotalReducciones);
                    arrDetInteg.add(cFechaIntegra);
                    arrIntegradas.add(arrDetInteg);
                    arrDetInteg = null;
                }
                cUnidadCreadora = rsd.getString(4);
                arrDetIntegd.add(cUnidadCreadora);
                cFolioAdecuacion = rsd.getString(5);
                arrDetIntegd.add(cFolioAdecuacion);
                cMontoAdecuacion = rsd.getString(6);
                arrDetIntegd.add(cMontoAdecuacion);
                cFechaAdecuacion = rsd.getString(7);
                arrDetIntegd.add(cFechaAdecuacion);
                cJustificacionAdecua = rsd.getString(8);
                arrDetIntegd.add(cJustificacionAdecua);
                arrIntegradas.add(arrDetIntegd);
                arrDetIntegd = null;
                i++;
            }
        } finally {
            if (pstmd != null)
                pstmd.close();
            pstmd = null;
            if (rsd != null)
                rsd.close();
            rsd = null;
            if (pstm != null)
                pstm.close();
            pstm = null;
            if (rs != null)
                rs.close();
            rs = null;
        }
        return arrIntegradas;
    }

    public static ArrayList<ArrayList<String>> consultaFap02AmpliaFIAFExcel(Connection conn, int nFolio) throws SQLException {
        ArrayList<ArrayList<String>> arrAdecuaciones = new ArrayList<>();
        ResultSet rsd = null;
        String nfolioadec = "";
        String programa = "";
        String importe = "";
        String justificacionA = "";
        String justificacionR = "";
        String justificacionN = "";
        String suma = "0.0";
        String cQuery = "{call sp_fap02_ampl_FIAF (" + nFolio + ") }";
        CallableStatement cs = null;
        try {
            cs = conn.prepareCall(cQuery);
            rsd = cs.executeQuery();
            while (rsd.next()) {
                ArrayList<String> arrAdecuad = new ArrayList<>();
                nfolioadec = rsd.getString(1);
                arrAdecuad.add(nfolioadec);
                programa = rsd.getString(2);
                arrAdecuad.add(programa);
                importe = rsd.getString(3);
                arrAdecuad.add(importe);
                // encabezado
                justificacionA = rsd.getString(4);
                arrAdecuad.add(justificacionA);
                justificacionR = rsd.getString(5);
                arrAdecuad.add(justificacionR);
                justificacionN = rsd.getString(6);
                arrAdecuad.add(justificacionN);
                suma = rsd.getString(7);
                arrAdecuad.add(suma);
                arrAdecuaciones.add(arrAdecuad);
            }
        } finally {
            CloseObject.closeObject(cs);
            CloseObject.closeObject(rsd);
        }
        return arrAdecuaciones;
    }

    public static ArrayList<ArrayList<String>> consultaFap02AmpliaExcel(Connection conn, int nFolio) throws SQLException {
        ArrayList<ArrayList<String>> arrAdecuaciones = new ArrayList<>();
        ResultSet rsd = null;
        String nfolioadec = "";
        String programa = "";
        String importe = "";
        String justificacionA = "";
        String justificacionR = "";
        String justificacionN = "";
        String suma = "0.0";
        //int i = 0;
        String cQuery = "{call sp_fap02_ampl (" + nFolio + ") }";
        CallableStatement cs = null;
        try {
            cs = conn.prepareCall(cQuery);
            rsd = cs.executeQuery();
            while (rsd.next()) {
                ArrayList<String> arrAdecuad = new ArrayList<>();
                nfolioadec = rsd.getString(1);
                arrAdecuad.add(nfolioadec);
                programa = rsd.getString(2);
                arrAdecuad.add(programa);
                importe = rsd.getString(3);
                arrAdecuad.add(importe);
                // encabezado
                justificacionA = rsd.getString(4);
                arrAdecuad.add(justificacionA);
                justificacionR = rsd.getString(5);
                arrAdecuad.add(justificacionR);
                justificacionN = rsd.getString(6);
                arrAdecuad.add(justificacionN);
                suma = rsd.getString(7);
                arrAdecuad.add(suma);
                arrAdecuaciones.add(arrAdecuad);
            }
        } finally {
            CloseObject.closeObject(cs);
            CloseObject.closeObject(rsd);
        }
        return arrAdecuaciones;
    }

    public static ArrayList<ArrayList<String>> consultaFap02ReduceExcel(Connection conn, int nFolio) throws SQLException {
        ArrayList<ArrayList<String>> arrAdecuaciones = new ArrayList<>();
        ResultSet rsd = null;
        String nfolioadec = "";
        String programa = "";
        String importe = "";
        String justificacionA = "";
        String justificacionR = "";
        String justificacionN = "";
        String suma = "0.0";
        String cQuery = "{call sp_fap02_reduc (" + nFolio + ") }";
        CallableStatement cs = null;
        try {
            cs = conn.prepareCall(cQuery);
            rsd = cs.executeQuery();
            while (rsd.next()) {
                ArrayList<String> arrAdecuad = new ArrayList<>();
                nfolioadec = rsd.getString(1);
                arrAdecuad.add(nfolioadec);
                programa = rsd.getString(2);
                arrAdecuad.add(programa);
                importe = rsd.getString(3);
                arrAdecuad.add(importe);
                // encabezado
                justificacionA = rsd.getString(4);
                arrAdecuad.add(justificacionA);
                justificacionR = rsd.getString(5);
                arrAdecuad.add(justificacionR);
                justificacionN = rsd.getString(6);
                arrAdecuad.add(justificacionN);
                suma = rsd.getString(7);
                arrAdecuad.add(suma);
                arrAdecuaciones.add(arrAdecuad);
            }
        } finally {
            CloseObject.closeObject(cs);
            CloseObject.closeObject(rsd);
        }
        return arrAdecuaciones;
    }

    public static String[] consultaFirmantePuesto(Connection conn, String ur, int nFolio) throws SQLException {
        String[] arrFirmante = new String[2];
        ResultSet rsd = null;
        String Nombre = "";
        String Puesto = "";
        String cQuery = "{call fn_define_firmante_adecuacion ('" + ur + "'," + nFolio + ") }";
        CallableStatement cs = null;
        cs = conn.prepareCall(cQuery);
        try {
            rsd = cs.executeQuery();
            while (rsd.next()) {
                Nombre = rsd.getString(1);
                arrFirmante[0] = Nombre;
                Puesto = rsd.getString(2);
                arrFirmante[1] = Puesto;
            }
        } finally {
            CloseObject.closeObject(cs);
            CloseObject.closeObject(rsd);
        }
        return arrFirmante;
    }

    public static String[] consultaFirmanteFIAFPuesto(Connection conn, String ur, int nFolio) throws SQLException {
        String[] arrFirmante = new String[2];
        ResultSet rsd = null;
        String Nombre = "";
        String Puesto = "";
        String cQuery = "{call fn_define_firmante_adecuacion ('" + ur + "'," + nFolio + ") }";
        CallableStatement cs = null;
        cs = conn.prepareCall(cQuery);
        try {
            rsd = cs.executeQuery();
            while (rsd.next()) {
                Nombre = rsd.getString(1);
                arrFirmante[0] = Nombre;
                Puesto = rsd.getString(2);
                arrFirmante[1] = Puesto;
            }
        } finally {
            CloseObject.closeObject(cs);
            CloseObject.closeObject(rsd);
        }
        return arrFirmante;
    }

    public static ArrayList consultaFap02ReduceFIAFExcel(Connection conn, int nFolio) throws SQLException {
        ArrayList<ArrayList<String>> arrAdecuaciones = new ArrayList<>();
        ResultSet rsd = null;
        String nfolioadec = "";
        String programa = "";
        String importe = "";
        String justificacionA = "";
        String justificacionR = "";
        String justificacionN = "";
        String suma = "0.0";
        String cQuery = "{call sp_fap02_reduc_FIAF (" + nFolio + ") }";
        CallableStatement cs = null;
        cs = conn.prepareCall(cQuery);
        try {
            rsd = cs.executeQuery();
            while (rsd.next()) {
                ArrayList<String> arrAdecuad = new ArrayList<>();
                nfolioadec = rsd.getString(1);
                arrAdecuad.add(nfolioadec);
                programa = rsd.getString(2);
                arrAdecuad.add(programa);
                importe = rsd.getString(3);
                arrAdecuad.add(importe);
                // encabezado
                justificacionA = rsd.getString(4);
                arrAdecuad.add(justificacionA);
                justificacionR = rsd.getString(5);
                arrAdecuad.add(justificacionR);
                justificacionN = rsd.getString(6);
                arrAdecuad.add(justificacionN);
                suma = rsd.getString(7);
                arrAdecuad.add(suma);
                arrAdecuaciones.add(arrAdecuad);
            }
        } finally {
            CloseObject.closeObject(cs);
            CloseObject.closeObject(rsd);
        }
        return arrAdecuaciones;
    }

    public static ArrayList<ArrayList<String>> consultaIntegradasExcel(Connection conn, int nFolio) throws SQLException {
        ArrayList<ArrayList<String>> arrIntegradas = new ArrayList<>();
        ResultSet rsd = null;
        String UE = "";
        String VALOR = "";
        String NO_SAI = "";
        String F_APLICACION = "";
        String CONCEPTO = "";
        String IADE = "";
        String NIVEL = "";
        String TIPO = "";
        String NO_AFECTACIONES = "";
        String AMPLIACIONES = "";
        String REDUCCIONES = "";
        String DIFERENCIA = "";
        String cQuery = "{call sp_integra_adecua (" + nFolio + ") }";
        CallableStatement cs = null;
        cs = conn.prepareCall(cQuery);
        try {
            rsd = cs.executeQuery();
            while (rsd.next()) {
                // ArrayList<String> arrDetIntegd = new ArrayList<>();
                ArrayList<String> arrDetInteg = new ArrayList<>();
                UE = rsd.getString(2);
                arrDetInteg.add(UE);
                NO_SAI = rsd.getString(3);
                arrDetInteg.add(NO_SAI);
                VALOR = rsd.getString(4);
                arrDetInteg.add(VALOR);
                F_APLICACION = rsd.getString(5);
                arrDetInteg.add(F_APLICACION);
                CONCEPTO = rsd.getString(6);
                arrDetInteg.add(CONCEPTO);
                // RESUMEN
                IADE = rsd.getString(7);
                arrDetInteg.add(IADE);
                NIVEL = rsd.getString(8);
                arrDetInteg.add(NIVEL);
                TIPO = rsd.getString(9);
                arrDetInteg.add(TIPO);
                NO_AFECTACIONES = rsd.getString(10);
                arrDetInteg.add(NO_AFECTACIONES);
                AMPLIACIONES = rsd.getString(11);
                arrDetInteg.add(AMPLIACIONES);
                REDUCCIONES = rsd.getString(12);
                arrDetInteg.add(REDUCCIONES);
                DIFERENCIA = rsd.getString(13);
                arrDetInteg.add(DIFERENCIA);
                arrIntegradas.add(arrDetInteg);
            }
        } finally {
            CloseObject.closeObject(cs);
            CloseObject.closeObject(rsd);
        }
        return arrIntegradas;
    }

    public static ArrayList<ArrayList<String>> consultaIntegradasFIAFExcel(Connection conn, int nFolio) throws SQLException {
        ArrayList<ArrayList<String>> arrIntegradas = new ArrayList<>();
        ResultSet rsd = null;
        String UE = "";
        String VALOR = "";
        String NO_SAI = "";
        String F_APLICACION = "";
        String CONCEPTO = "";
        String IADE = "";
        String NIVEL = "";
        String TIPO = "";
        String NO_AFECTACIONES = "";
        String AMPLIACIONES = "";
        String REDUCCIONES = "";
        String DIFERENCIA = "";
        String cQuery = "{call sp_integra_adecua_FIAF (" + nFolio + ") }";
        CallableStatement cs = null;
        cs = conn.prepareCall(cQuery);
        try {
            rsd = cs.executeQuery();
            while (rsd.next()) {
                ArrayList<String> arrDetInteg = new ArrayList<>();
                UE = rsd.getString(2);
                arrDetInteg.add(UE);
                NO_SAI = rsd.getString(3);
                arrDetInteg.add(NO_SAI);
                VALOR = rsd.getString(4);
                arrDetInteg.add(VALOR);
                F_APLICACION = rsd.getString(5);
                arrDetInteg.add(F_APLICACION);
                CONCEPTO = rsd.getString(6);
                arrDetInteg.add(CONCEPTO);
                // RESUMEN
                IADE = rsd.getString(7);
                arrDetInteg.add(IADE);
                NIVEL = rsd.getString(8);
                arrDetInteg.add(NIVEL);
                TIPO = rsd.getString(9);
                arrDetInteg.add(TIPO);
                NO_AFECTACIONES = rsd.getString(10);
                arrDetInteg.add(NO_AFECTACIONES);
                AMPLIACIONES = rsd.getString(11);
                arrDetInteg.add(AMPLIACIONES);
                REDUCCIONES = rsd.getString(12);
                arrDetInteg.add(REDUCCIONES);
                DIFERENCIA = rsd.getString(13);
                arrDetInteg.add(DIFERENCIA);
                arrIntegradas.add(arrDetInteg);
            }
        } finally {
            CloseObject.closeObject(cs);
            CloseObject.closeObject(rsd);
        }
        return arrIntegradas;
    }

    public static String validaCapituloUsuario(Connection conn, int nFolio) throws SQLException {
        String cMensajeError = "";
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String cExiste = "";
        String cQuery = "	SELECT distinct left(u.cCapitulo+replicate('0', 5),5) FROM tAdecuacionDetalle d with (nolock), tAdecuacionEncabezado h with (nolock) " + "	     , tCatalogoEP e with (nolock), tCatalogoCapituloUsuario u with (nolock) " + "	 where d.nFolioAdecuacion = h.nFolioAdecuacion " + "	   AND d.EP  = e.ep " + "	   AND d.nFolioAdecuacion =  " + nFolio + " " + "	   and h.U_LOGIN <> u.u_logion " + "	   AND substring(e.cPartida,1,1) = cCapitulo";
        try {
            pstm = conn.prepareStatement(cQuery);
            rs = pstm.executeQuery();
            if (rs.next()) {
                cExiste = rs.getString(1);
            }
            if (!"".equals(cExiste)) {
                cMensajeError = "Error Usted no tiene alcance para el Capitulo " + cExiste;
            }
        } finally {
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(rs);
        }
        return cMensajeError;
    }

    public static boolean validaAplicado(Caso c, Connection conn) throws SQLException {
        boolean bAutorizado = false;
        // String cFolioIntegra = c.getFolio();
        int id_caso = c.getIdCaso();
        String cQueryCaso = "select * from CG_CASO_DATO d where d.ID_CASO =  " + id_caso + " and ID_TC = 3 and ID_CD = 10 and " + " CD_VALOR NOT IN ('DOCUMENTO APLICADO CONTABLEMENTE','DOCUMENTO DE ADECUACIONAUTE APLICADO CONTABLEMENTE|', 'DOCUMENTO DE ADECUACION APLICADO CONTABLEMENTE|','ADECUACION APLICADO CONTABLEMENTE CANCELADO') ";
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int iExiste = 0;
        try {
            pstm = conn.prepareStatement(cQueryCaso);
            rs = pstm.executeQuery();
            if (rs.next()) {
                iExiste = rs.getInt(1);
            }
            if (iExiste > 0) {
                bAutorizado = true;
            }
        } finally {
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(rs);
        }
        return bAutorizado;
    }

    public static boolean regresaCasoIntegra(Caso c, Connection conn) throws SQLException {
        boolean bRegrezado = false;
        int id_caso = c.getIdCaso();
        PreparedStatement pstmnt = null;
        String cUpdateCasoDato = "UPDATE CG_CASO_OPERACION set ID_OPER = 1 where ID_CASO = " + id_caso;
        try {
            pstmnt = conn.prepareStatement(cUpdateCasoDato);
            bRegrezado = pstmnt.execute();
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
        }
        return bRegrezado;
    }

    public static String validaIntegracionConsolidada(Connection conn, int nFolio) throws SQLException {
        String cMensajeError = "";
        String cQueryDuplicados = "select count(*), cMes,ep from (select cMes, count(*) as total, ep, cEvento, sum(mImporte) as mImporte " + " from tAdecuacionDetalle d  WITH (NOLOCK) " + " where d.nFolioAdecuacion in (select cd.nFolioAdecuacion from tConsolidacionDetalle cd where cd.nFolioCONSOLIDACION = " + nFolio + ") and " + "	d.mImporte <> 0    " + " GROUP BY  cMes, ep, cEvento ) a " + " group by cMes,ep " + " HAVING COUNT(*)>1 ";
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int iExiste = 0;
        try {
            pstm = conn.prepareStatement(cQueryDuplicados);
            rs = pstm.executeQuery();
            if (rs.next()) {
                iExiste = rs.getInt(1);
            }
            if (iExiste > 0) {
                cMensajeError = "EP Duplicada no es posible realizar la integración.";
            }
        } finally {
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(rs);
        }
        return cMensajeError;
    }

    public static ArrayList validaNivMensaje(Connection conn, int nFolio) throws SQLException {
        String cMesnaje = "";
        int iNivel = 0;
        ArrayList<ArrayList<Object>> arrNivel = new ArrayList<>();
        ArrayList<Object> arrNivelMensaje = new ArrayList<>();
        String cQuery = "select distinct c.nNivel, isnull(c.cMensaje,'') from tAdecuacionDetalle a with (nolock), tCatalogoNivelcuatro c with (nolock) " + "	 , tCatalogoEP e with (nolock)  " + " where nFolioAdecuacion = " + nFolio + " AND " + "    a.EP = e.EP  " + "   AND e.cPartida = c.cPartida " + "   and c.cMensaje is not null";
        PreparedStatement pstm = null;
        ResultSet rs = null;
        // int iExiste = 0;
        try {
            pstm = conn.prepareStatement(cQuery);
            rs = pstm.executeQuery();
            while (rs.next()) {
                iNivel = rs.getInt(1);
                cMesnaje = rs.getString(2);
                arrNivelMensaje.add(iNivel);
                arrNivelMensaje.add(cMesnaje);
                arrNivel.add(arrNivelMensaje);
            }
        } finally {
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(rs);
        }
        return arrNivel;
    }

    public static ArrayList buscaIntegradosFIAF(Connection conn, int nFolio) throws SQLException {
        ArrayList<ArrayList<String>> arrIntegracion = new ArrayList<>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String cSqlSelect = "select distinct " + " ca.c_folio nFolioAdecuacion ,ae.cTipoAdecuacion,ae.nNivel, " + " (SELECT case sum(case substring(d.cEvento,1,1) when 'R' then  d.mImporte * -1 else d.mImporte end) when 0 then sum(d.mImporte)/2 else sum(case substring(d.cEvento,1,1) when 'R' then  d.mImporte * -1 else d.mImporte end) end as mImporte " + " 	FROM tAdecuacionDetalle d with (nolock) " + " 	WHERE d.nFolioAdecuacion = ae.nFolioAdecuacion) as mImporte, " + " (select cd_valor from CG_CASO_DATO where id_caso=ca.ID_CASO and ID_CD=4 and ID_TC=3) as cUsuarioCreador, " + " ae.fAplicacion,substring(ca.C_FOLIO,6,3) as cUnidadResponsable, " + " '<input type=\\\"checkbox\\\" id=\\\"' + CONVERT(varchar, nFolioAdecuacion) + '\\\"/>' as cDescartar " + " from cg_caso ca, cg_caso_operacion co, tAdecuacionEncabezado ae " + " where ca.id_caso=co.id_caso and ca.ID_TC=3" + " and ae.nFolioAdecuacion=substring(ca.C_FOLIO,10,20) " + "  and ae.nFolioFIAF =  " + nFolio + " ";
        try {
            pstmnt = conn.prepareStatement(cSqlSelect);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                ArrayList<String> arrDastosIntegracion = new ArrayList<String>();
                String tmpFolio = rs.getString(1);
                arrDastosIntegracion.add(tmpFolio);
                String tmpTipo = rs.getString(2);
                arrDastosIntegracion.add(tmpTipo);
                String tmpNivel = rs.getString(3);
                arrDastosIntegracion.add(tmpNivel);
                String tmpImporte = rs.getString(4);
                arrDastosIntegracion.add(tmpImporte);
                String tmpUsuario = rs.getString(5);
                arrDastosIntegracion.add(tmpUsuario);
                String tmpAplicado = rs.getString(6);
                arrDastosIntegracion.add(tmpAplicado);
                String tmpUnidad = rs.getString(7);
                arrDastosIntegracion.add(tmpUnidad);
                String tmpDescartar = rs.getString(8);
                arrDastosIntegracion.add(tmpDescartar);
                arrIntegracion.add(arrDastosIntegracion);
                arrDastosIntegracion = null;
            }
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
        return arrIntegracion;
    }

    public static String integraAdecuacionesFIAF(Connection conn, int nFolio, String[] foliosAdec, String cJustificacionA, String cJustificacionR, Usuario u, String cJustificacionNormativa, String aEjercicioFiscal, String cTipoAdecuacion, String nNivel) throws SQLException {
        int i = 0;
        String mensaje = "";
        PreparedStatement pstselect = null;
        PreparedStatement pstmen = null;
        PreparedStatement pstmdtu = null;
        ResultSet rs = null;
        String selectFIAFEncabezado = "SELECT * FROM tFIAFEncabezado WHERE nFolioFIAF=?";
        String insertFIAFEncabezado = "INSERT INTO tFIAFEncabezado (nFolioFIAF, fCreacion, cUnidadResponsableContable, cDescripcionPoliza, cJustificacionA, cJustificacionR, cJustificacionNormativa,cTipoPoliza,cRamo,aEjercicioFiscal,U_LOGIN,cUnidadResponsable,cCentroContable,cTipoAdecuacion,nNivel) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        String cUpdateAdecuacion = "UPDATE tAdecuacionEncabezado SET nFolioFIAF= ? WHERE nFolioAdecuacion = ? ";
        String cUpdateCgOperacion = "update CG_CASO_OPERACION set co_responsable='FIAF', id_oper=7 WHERE id_caso=(select ae.id_caso from tAdecuacionEncabezado ae with(nolock), CG_CASO_OPERACION cg with(nolock) where ae.id_caso=cg.ID_CASO and nFolioAdecuacion=?) ";
        try {
            pstselect = conn.prepareStatement(selectFIAFEncabezado);
            pstselect.setInt(1, nFolio);
            rs = pstselect.executeQuery();
            if (!rs.next()) {
                pstmen = conn.prepareStatement(insertFIAFEncabezado);
                pstmen.setInt(1, nFolio);
                pstmen.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                pstmen.setString(3, "RHQ");
                pstmen.setString(4, "Integracion de Adecuaciones FIAF");
                pstmen.setString(5, cJustificacionA);
                pstmen.setString(6, cJustificacionR);
                pstmen.setString(7, cJustificacionNormativa);
                pstmen.setString(8, "DI");
                pstmen.setString(9, u.getU_Ramo());
                pstmen.setString(10, aEjercicioFiscal);
                pstmen.setString(11, u.getNombre());
                pstmen.setString(12, u.getU_UR());
                pstmen.setString(13, u.getPropiedad("CCENTROCONTABLE").getValor());
                pstmen.setString(14, cTipoAdecuacion);
                pstmen.setString(15, nNivel);
                pstmen.execute();
            }
            // Actualiza EncaBezado de Adecuacion
            pstmdtu = conn.prepareStatement(cUpdateAdecuacion);
            while (foliosAdec.length > i) {
                pstmdtu.setInt(1, nFolio);
                pstmdtu.setInt(2, new Integer(foliosAdec[i].substring(foliosAdec[i].lastIndexOf('-') + 1)).intValue());
                pstmdtu.addBatch();
                i++;
            }
            pstmdtu.executeBatch();
            pstmdtu.close();
            // Actualiza responsable en cg_Caso_operacion para que ya no lo vean
            // en la bandeja
            i = 0;
            pstmdtu = conn.prepareStatement(cUpdateCgOperacion);
            while (foliosAdec.length > i) {
                pstmdtu.setInt(1, new Integer(foliosAdec[i].substring(foliosAdec[i].lastIndexOf('-') + 1)).intValue());
                pstmdtu.addBatch();
                i++;
            }
            pstmdtu.executeBatch();
            conn.commit();
            mensaje = "La integración se realizó correctamente";
        } catch (SQLException e) {
            e.printStackTrace();
            mensaje = e.getMessage();
        } finally {
            CloseObject.closeObject(pstmen);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmdtu);
            CloseObject.closeObject(pstselect);
        }
        return mensaje;
    }

    public static String desIntegraFIAF(Connection conn, int nFolio, String[] foliosAdec) throws SQLException {
        String mensaje = "";
        PreparedStatement pstmen = null;
        String cQueryUpdate = "UPDATE tAdecuacionEncabezado SET nFolioFIAF = NULL WHERE nFolioFIAF = ? ";
        String cUpdateCgOperacion = "update CG_CASO_OPERACION set co_responsable='REVISORES_ADECUACIONES', id_oper=5 WHERE id_caso=(select ae.id_caso from tAdecuacionEncabezado ae with(nolock), CG_CASO_OPERACION cg with(nolock) where ae.id_caso=cg.ID_CASO and nFolioAdecuacion=?) ";
        PreparedStatement pstmdtu = null;
        try {
            pstmen = conn.prepareStatement(cQueryUpdate);
            pstmen.setInt(1, nFolio);
            pstmen.execute();
            // Actualiza responsable en cg_Caso_operacion para que ya no lo vean
            // en la bandeja
            int i = 0;
            pstmdtu = conn.prepareStatement(cUpdateCgOperacion);
            while (foliosAdec.length > i) {
                pstmdtu.setInt(1, new Integer(foliosAdec[i].substring(foliosAdec[i].lastIndexOf('-') + 1)).intValue());
                pstmdtu.addBatch();
                i++;
            }
            pstmdtu.executeBatch();
            mensaje = "La desintegración se realizó correctamente";
        } catch (SQLException e) {
            e.printStackTrace();
            mensaje = e.getMessage();
            log.warn("Object: {}", e.getMessage());
        } finally {
            CloseObject.closeObject(pstmdtu);
            CloseObject.closeObject(pstmen);
        }
        return mensaje;
    }

    public static String avanzaAdecFIAF(Connection conn, int nFolio, String[] foliosAdec) throws SQLException {
        String mensaje = "";
        PreparedStatement pstmen = null;
        String cUpdateCgOperacion = "update CG_CASO_OPERACION set co_responsable='CONSULTA_ADECUACIONES', id_oper=6 WHERE id_caso=(select ae.id_caso from tAdecuacionEncabezado ae with(nolock), CG_CASO_OPERACION cg with(nolock) where ae.id_caso=cg.ID_CASO and nFolioAdecuacion=?) ";
        PreparedStatement pstmdtu = null;
        PreparedStatement pstmnt = null;
        try {
            int j = 0;
            pstmnt = conn.prepareStatement("INSERT INTO tADECUACIONAUTEncabezado (nFolioAdecuacionaut, cDocumentoHaplicado, fCarga, cTipoAdecuacion, cRamo, cUnidadResponsable, aEjercicioFiscal, u_login, " + "cTipoPoliza, fAplicacion,cdescripcionpoliza) SELECT nFolioAdecuacion, 'S',fCarga, cTipoAdecuacion, cRamo,cUnidadResponsable, aEjercicioFiscal, " + "u_login, cTipoPoliza, fAplicacion,'AUTORIZACION DE ADECUACION PRESUPUESTAL FOLIO ' + CAST(nFolioAdecuacion as varchar) FROM tAdecuacionEncabezado WITH (NOLOCK) WHERE nFolioAdecuacion = ?");
            while (foliosAdec.length > j) {
                pstmnt.setInt(1, new Integer(foliosAdec[j].substring(foliosAdec[j].lastIndexOf('-') + 1)).intValue());
                pstmnt.addBatch();
                j++;
            }
            pstmnt.executeBatch();
            // Actualiza responsable en cg_Caso_operacion para que ya no lo vean
            // en la bandeja de consulta
            int i = 0;
            pstmdtu = conn.prepareStatement(cUpdateCgOperacion);
            while (foliosAdec.length > i) {
                pstmdtu.setInt(1, new Integer(foliosAdec[i].substring(foliosAdec[i].lastIndexOf('-') + 1)).intValue());
                pstmdtu.addBatch();
                i++;
            }
            pstmdtu.executeBatch();
            mensaje = "";
        } catch (SQLException e) {
            e.printStackTrace();
            mensaje = e.getMessage();
            log.warn("Object: {}", e.getMessage());
        } finally {
            CloseObject.closeObject(pstmdtu);
            CloseObject.closeObject(pstmen);
        }
        return mensaje;
    }

    public static String agregaJustificaciones(Connection conn, int nFolio, String justificacionA, String justificacionR, String justificacionNormativa) throws SQLException {
        String mensaje = "";
        PreparedStatement pstmen = null;
        String cQueryUpdate = "UPDATE tFIAFEncabezado SET cJustificacionA = ? , cJustificacionR = ?, cJustificacionNormativa = ? WHERE nFolioFIAF = ? ";
        try {
            pstmen = conn.prepareStatement(cQueryUpdate);
            pstmen.setString(1, justificacionA);
            pstmen.setString(2, justificacionR);
            pstmen.setString(3, justificacionNormativa);
            pstmen.setInt(4, nFolio);
            pstmen.execute();
            mensaje = "Se actualizaron las justificaciones correctamente";
        } catch (SQLException e) {
            e.printStackTrace();
            mensaje = e.getMessage();
            log.warn("Object: {}", e.getMessage());
        } finally {
            CloseObject.closeObject(pstmen);
        }
        return mensaje;
    }

    public static void agregaFechaAplicacionFIAF(Connection conn, int nIdCaso) throws SQLException {
        PreparedStatement pstmnt = null;
        String queryUpdate = "";
        try {
            queryUpdate = "UPDATE tFIAFEncabezado set fAplicacion = case aejerciciofiscal when YEAR(GETDATE()) then getdate() else CONVERT(date,'31/12/'+convert(varchar,YEAR(GETDATE())-1),105) end ";
            queryUpdate += " WHERE  nFolioFIAF = ?";
            pstmnt = conn.prepareStatement(queryUpdate);
            pstmnt.setInt(1, nIdCaso);
            pstmnt.execute();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
    }

    public static void autorizaFIAF(Connection conn, Caso c, String nNumSicop, String cRecMotivSicop, String nNumMAP, String cRecMotivMAP, String ulogin, String prefixPath) throws SQLException, GestionException {
        PreparedStatement pstmnt = null;
        int nIdCaso;
        ResultSet rs = null;
        try {
            nIdCaso = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
            pstmnt = conn.prepareStatement("INSERT INTO tFIAFAutEncabezado (nFolioFIAFaut, fCreacion, cUnidadResponsableContable, cDescripcionPoliza, cJustificacionA, cJustificacionR, cJustificacionNormativa,cTipoPoliza,cRamo,aEjercicioFiscal,U_LOGIN,cUnidadResponsable,fAplicacion,cCentroContable)" + " SELECT nFolioFIAF, fCreacion, cUnidadResponsableContable, 'AUTORIZACION DE INTEGRACION FIAF FOLIO ' + CAST(nFolioFIAF as varchar), cJustificacionA, cJustificacionR, cJustificacionNormativa,cTipoPoliza,cRamo,aEjercicioFiscal,U_LOGIN,cUnidadResponsable,GETDATE(),cCentroContable FROM tFIAFEncabezado WITH (NOLOCK) WHERE nFolioFIAF = ?");
            pstmnt.setInt(1, nIdCaso);
            pstmnt.execute();
            conn.commit();
        } catch (SQLException s) {
            conn.rollback();
            s.printStackTrace();
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
    }

    public static int consecutivoSICOPFIAF(Connection conn, int nFolio) throws SQLException {
        int nConsecutivoSICOP = 0;
        PreparedStatement pstmd = null;
        ResultSet rsd = null;
        String cQuery = "select c.nConsecutivoSicop from tFIAFEncabezado c with (nolock) where c.nFolioFIAF = " + nFolio + " ";
        try {
            pstmd = conn.prepareStatement(cQuery);
            rsd = pstmd.executeQuery();
            if (rsd.next()) {
                nConsecutivoSICOP = rsd.getInt(1);
            }
        } finally {
            CloseObject.closeObject(pstmd);
            CloseObject.closeObject(rsd);
        }
        return nConsecutivoSICOP;
    }

    public static int nivelFIAF(Connection conn, int nFolio) throws SQLException {
        int nNivel = 0;
        PreparedStatement pstmd = null;
        ResultSet rsd = null;
        String cQuery = "select c.nNivel from tFIAFEncabezado c with (nolock) where c.nFolioFIAF = " + nFolio + " ";
        try {
            pstmd = conn.prepareStatement(cQuery);
            rsd = pstmd.executeQuery();
            if (rsd.next()) {
                nNivel = rsd.getInt(1);
            }
        } finally {
            CloseObject.closeObject(pstmd);
            CloseObject.closeObject(rsd);
        }
        return nNivel;
    }

    public static boolean actualizaFolioSicopFIAFEncabezado(Connection conn, int folioSicop, int folioFIAF) throws SQLException {
        int rs = 0;
        PreparedStatement pstm = null;
        boolean resp = false;
        try {
            pstm = conn.prepareStatement("UPDATE tFIAFEncabezado SET nConsecutivoSicop=? WHERE nFolioFIAF=?");
            if (folioSicop == 0)
                pstm.setNull(1, Types.INTEGER);
            else
                pstm.setInt(1, folioSicop);
            pstm.setInt(2, folioFIAF);
            rs = pstm.executeUpdate();
            if (rs != 0) {
                resp = true;
            }
            conn.commit();
        } finally {
            CloseObject.closeObject(pstm);
        }
        // regresa true si hizo el update, false si falló
        return resp;
    }

    public static AdecuacionCalendario adecuacionCalendarioFIAF(Connection conn, int nFolioFIAF) throws SQLException {
        AdecuacionCalendario ac = new AdecuacionCalendario();
        String query = "";
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            String justificacion = "";
            pstmnt = conn.prepareStatement("SELECT replace(replace(cJustificacionNormativa,'\"',''),'''','') as justificacion from tFIAFEncabezado f with (nolock) where f.nFolioFIAF = ? ");
            pstmnt.setInt(1, nFolioFIAF);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                justificacion = rs.getString("justificacion");
                ac.setJustificacion(justificacion);
            }
            query = "SELECT ROW_NUMBER() OVER(order by cEvento desc)  AS nDocREnglon, substring(cEvento,1,1) as cEvento, ISNULL(z.ClaveSIAFF, substring(a.EP,1,55)) as ClaveSIAFF, isnull(z.ClaveInterna,substring(a.ep,57,64)) as ClaveInterna,z.nClaveCNA " + "     ,  sum(mEnero) as mEnero, sum(mFebrero) as mFebrero, sum(mMarzo) as mMarzo, sum(mAbril) as mAbril, sum(mMayo) as mMayo, sum(mJunio) as mJunio, sum(mJulio) as mJulio, sum(mAgosto) as mAgosto, sum(mSeptiembre) as mSeptiembre, sum(mOctubre) as mOctubre, sum(mNoviembre) as mNoviembre, sum(mDiciembre) as mDiciembre " + "     ,  sum(mEneroNegativo) as mEneroNegativo, sum(mFebreroNegativo) as mFebreroNegativo, sum(mMarzoNegativo) as mMarzoNegativo, sum(mAbrilNegativo) as mAbrilNegativo " + "     ,  sum(mMayoNegativo) as mMayoNegativo, sum(mJunioNegativo) as mJunioNegativo, sum(mJulioNegativo) as mJulioNegativo, sum(mAgostoNegativo) as mAgostoNegativo " + "     ,  sum(mSeptiembreNegativo) as mSeptiembreNegativo, sum(mOctubreNegativo) as mOctubreNegativo, sum(mNoviembreNegativo) as mNoviembreNegativo, sum(mDiciembreNegativo) as mDiciembreNegativo " + "     , sum(mEnero) + sum(mFebrero) + sum(mMarzo) + sum(mAbril) + sum(mMayo) + sum(mJunio) + sum(mJulio) + sum(mAgosto) + sum(mSeptiembre) + sum(mOctubre) + sum(mNoviembre) + sum(mDiciembre) as mAnual " + "  FROM ( SELECT d.ep, d.cEvento, case d.cMes when 1 then sum(d.mImporte) else 0 end as mEnero " + "     	 , case d.cMes when 2 then sum(d.mImporte) else 0 end as mFebrero     	 , case d.cMes when 3 then sum(d.mImporte) else 0 end as mMarzo  " + "     	 , case d.cMes when 4 then sum(d.mImporte) else 0 end as mAbril       	 , case d.cMes when 5 then sum(d.mImporte) else 0 end as mMayo  " + "     	 , case d.cMes when 6 then sum(d.mImporte) else 0 end as mJunio       	 , case d.cMes when 7 then sum(d.mImporte) else 0 end as mJulio  " + "     	 , case d.cMes when 8 then sum(d.mImporte) else 0 end as mAgosto      	 , case d.cMes when 9 then sum(d.mImporte) else 0 end as mSeptiembre  " + "     	 , case d.cMes when 10 then sum(d.mImporte) else 0 end as mOctubre     	 , case d.cMes when 11 then sum(d.mImporte) else 0 end as mNoviembre  " + "     	 , case d.cMes when 12 then sum(d.mImporte) else 0 end as mDiciembre  " + "     	 , case d.cMes when 1 then sum(d.NewSaldo) else 0 end as mEneroNegativo  " + "     	 , case d.cMes when 2 then sum(d.NewSaldo) else 0 end as mFebreroNegativo  " + "     	 , case d.cMes when 3 then sum(d.NewSaldo) else 0 end as mMarzoNegativo  " + "     	 , case d.cMes when 4 then sum(d.NewSaldo) else 0 end as mAbrilNegativo  " + "     	 , case d.cMes when 5 then sum(d.NewSaldo) else 0 end as mMayoNegativo  " + "     	 , case d.cMes when 6 then sum(d.NewSaldo) else 0 end as mJunioNegativo  " + "     	 , case d.cMes when 7 then sum(d.NewSaldo) else 0 end as mJulioNegativo  " + "     	 , case d.cMes when 8 then sum(d.NewSaldo) else 0 end as mAgostoNegativo  " + "     	 , case d.cMes when 9 then sum(d.NewSaldo) else 0 end as mSeptiembreNegativo  " + "     	 , case d.cMes when 10 then sum(d.NewSaldo) else 0 end as mOctubreNegativo  " + "     	 , case d.cMes when 11 then sum(d.NewSaldo) else 0 end as mNoviembreNegativo  " + "     	 , case d.cMes when 12 then sum(d.NewSaldo) else 0 end as mDiciembreNegativo  " + "     FROM ( select d.EP, d.cEvento, d.cMes, d.mImporte, " + nFolioFIAF + " as nFolioAdecuacion " + "		     	 		  , case substring(cEvento,1,1) when 'R' then (d.mImporte )   " + "                        WHEN 'A' then (d.mImporte) end as NewSaldo from tAdecuacionDetalle d  with (nolock) " + "		  			  WHERE d.nFolioAdecuacion IN (select d.nFolioAdecuacion from v_AdecuacionDetFIAF  d with (nolock) WHERE d.nFolioFIAF =  " + nFolioFIAF + " ) ) d  group by d.ep, d.cEvento, d.cMes ) a  LEFT OUTER JOIN  " + "           tCatalogoEP z   with (nolock)  ON a.EP =  z.EP  group by a.ep, a.cEvento, z.ClaveSIAFF, z.ClaveInterna,z.nClaveCNA ";
            pstmnt = conn.prepareStatement(query);
            rs = pstmnt.executeQuery();
            int i = 0;
            while (rs.next()) {
                Saldo epSaldo = new Saldo();
                i++;
                ac.addRenglon(rs.getInt("nDocREnglon"));
                ac.addMovimiento(rs.getString("cEvento"));
                // epSaldo.setEp(ac.getTipoMov());
                if ("R".equals(ac.getMovimiento(i - 1))) {
                    ac.setmTotREnero(ac.getmTotREnero() + new Double(rs.getString("mEnero")));
                    ac.setmTotRFebrero(ac.getmTotRFebrero() + new Double(rs.getString("mFebrero")));
                    ac.setmTotRMarzo(ac.getmTotRMarzo() + new Double(rs.getString("mMarzo")));
                    ac.setmTotRAbril(ac.getmTotRAbril() + new Double(rs.getString("mAbril")));
                    ac.setmTotRMayo(ac.getmTotRMayo() + new Double(rs.getString("mMayo")));
                    ac.setmTotRJunio(ac.getmTotRJunio() + new Double(rs.getString("mJunio")));
                    ac.setmTotRJulio(ac.getmTotRJulio() + new Double(rs.getString("mJulio")));
                    ac.setmTotRAgosto(ac.getmTotRAgosto() + new Double(rs.getString("mAgosto")));
                    ac.setmTotRSeptiembre(ac.getmTotRSeptiembre() + new Double(rs.getString("mSeptiembre")));
                    ac.setmTotROctubre(ac.getmTotROctubre() + new Double(rs.getString("mOctubre")));
                    ac.setmTotRNoviembre(ac.getmTotRNoviembre() + new Double(rs.getString("mNoviembre")));
                    ac.setmTotRDiciembre(ac.getmTotRDiciembre() + new Double(rs.getString("mDiciembre")));
                } else {
                    ac.setmTotAEnero(ac.getmTotAEnero() + new Double(rs.getString("mEnero")));
                    ac.setmTotAFebrero(ac.getmTotAFebrero() + new Double(rs.getString("mFebrero")));
                    ac.setmTotAMarzo(ac.getmTotAMarzo() + new Double(rs.getString("mMarzo")));
                    ac.setmTotAAbril(ac.getmTotAAbril() + new Double(rs.getString("mAbril")));
                    ac.setmTotAMayo(ac.getmTotAMayo() + new Double(rs.getString("mMayo")));
                    ac.setmTotAJunio(ac.getmTotAJunio() + new Double(rs.getString("mJunio")));
                    ac.setmTotAJulio(ac.getmTotAJulio() + new Double(rs.getString("mJulio")));
                    ac.setmTotAAgosto(ac.getmTotAAgosto() + new Double(rs.getString("mAgosto")));
                    ac.setmTotASeptiembre(ac.getmTotASeptiembre() + new Double(rs.getString("mSeptiembre")));
                    ac.setmTotAOctubre(ac.getmTotAOctubre() + new Double(rs.getString("mOctubre")));
                    ac.setmTotANoviembre(ac.getmTotANoviembre() + new Double(rs.getString("mNoviembre")));
                    ac.setmTotADiciembre(ac.getmTotADiciembre() + new Double(rs.getString("mDiciembre")));
                }
                String cEP = rs.getString("ClaveInterna") + "." + rs.getString("nClaveCNA");
                epSaldo.setMontoAbril(rs.getString("mAbril"));
                epSaldo.setMontoAgosto(rs.getString("mAgosto"));
                epSaldo.setMontoAnual(rs.getString("mAnual"));
                epSaldo.setMontoDiciembre(rs.getString("mDiciembre"));
                epSaldo.setMontoEnero(rs.getString("mEnero"));
                epSaldo.setMontoFebrero(rs.getString("mFebrero"));
                epSaldo.setMontoJulio(rs.getString("mJulio"));
                epSaldo.setMontoJunio(rs.getString("mJunio"));
                epSaldo.setMontoMarzo(rs.getString("mMarzo"));
                epSaldo.setMontoMayo(rs.getString("mMayo"));
                epSaldo.setMontoNoviembre(rs.getString("mNoviembre"));
                epSaldo.setMontoOctubre(rs.getString("mOctubre"));
                epSaldo.setMontoSeptiembre(rs.getString("mSeptiembre"));
                epSaldo.setClaveSIAFF(rs.getString("ClaveSIAFF"));
                epSaldo.setClaveInterna(rs.getString("ClaveInterna"));
                epSaldo.setClaveCNA(rs.getString("nClaveCNA"));
                epSaldo.setEp(epSaldo.getClaveSIAFF().trim() + "." + epSaldo.getClaveInterna().trim());
                ac.addSaldo(epSaldo);
            }
            ac.setiTotAbonos(ac.getmTotAEnero() + ac.getmTotAFebrero() + ac.getmTotAMarzo() + ac.getmTotAAbril() + ac.getmTotAMayo() + ac.getmTotAJunio() + ac.getmTotAJulio() + ac.getmTotAAgosto() + ac.getmTotASeptiembre() + ac.getmTotAOctubre() + ac.getmTotANoviembre() + ac.getmTotADiciembre());
            ac.setiTotCargos(ac.getmTotREnero() + ac.getmTotRFebrero() + ac.getmTotRMarzo() + ac.getmTotRAbril() + ac.getmTotRMayo() + ac.getmTotRJunio() + ac.getmTotRJulio() + ac.getmTotRAgosto() + ac.getmTotRSeptiembre() + ac.getmTotROctubre() + ac.getmTotRNoviembre() + ac.getmTotRDiciembre());
            // String pattern = "############.##";
            // DecimalFormat myFormatter = new DecimalFormat( pattern );
            String cMensajeAdec = "";
            double iDiferencia = 0;
            if (ac.getiTotAbonos() != 0 && ac.getiTotCargos() != 0) {
                iDiferencia = com.syc.contable.util.Math.truncate(ac.getiTotAbonos(), 2) - com.syc.contable.util.Math.truncate(ac.getiTotCargos(), 2);
                if ((com.syc.contable.util.Math.truncate(ac.getmTotAEnero(), 2) - com.syc.contable.util.Math.truncate(ac.getmTotREnero(), 2)) != 0) {
                    cMensajeAdec += "Calendario no Compensado en el Mes de Enero.";
                }
                if ((com.syc.contable.util.Math.truncate(ac.getmTotAFebrero(), 2) - com.syc.contable.util.Math.truncate(ac.getmTotRFebrero(), 2)) != 0) {
                    cMensajeAdec += "Calendario no Compensado en el Mes de Febrero.";
                }
                if ((com.syc.contable.util.Math.truncate(ac.getmTotAMarzo(), 2) - com.syc.contable.util.Math.truncate(ac.getmTotRMarzo(), 2)) != 0) {
                    cMensajeAdec += "Calendario no Compensado en el Mes de Marzo.";
                }
                if ((com.syc.contable.util.Math.truncate(ac.getmTotAAbril(), 2) - com.syc.contable.util.Math.truncate(ac.getmTotRAbril(), 2)) != 0) {
                    cMensajeAdec += "Calendario no Compensado en el Mes de Abril.";
                }
                if ((com.syc.contable.util.Math.truncate(ac.getmTotAMayo(), 2) - com.syc.contable.util.Math.truncate(ac.getmTotRMayo(), 2)) != 0) {
                    cMensajeAdec += "Calendario no Compensado en el Mes de Mayo.";
                }
                if ((com.syc.contable.util.Math.truncate(ac.getmTotAJunio(), 2) - com.syc.contable.util.Math.truncate(ac.getmTotRJunio(), 2)) != 0) {
                    cMensajeAdec += "Calendario no Compensado en el Mes de Junio.";
                }
                if ((com.syc.contable.util.Math.truncate(ac.getmTotAJulio(), 2) - com.syc.contable.util.Math.truncate(ac.getmTotRJulio(), 2)) != 0) {
                    cMensajeAdec += "Calendario no Compensado en el Mes de Julio.";
                }
                if ((com.syc.contable.util.Math.truncate(ac.getmTotAAgosto(), 2) - com.syc.contable.util.Math.truncate(ac.getmTotRAgosto(), 2)) != 0) {
                    cMensajeAdec += "Calendario no Compensado en el Mes de Agosto.";
                }
                if ((com.syc.contable.util.Math.truncate(ac.getmTotASeptiembre(), 2) - com.syc.contable.util.Math.truncate(ac.getmTotRSeptiembre(), 2)) != 0) {
                    cMensajeAdec += "Calendario no Compensado en el Mes de Septiembre.";
                }
                if ((com.syc.contable.util.Math.truncate(ac.getmTotAOctubre(), 2) - com.syc.contable.util.Math.truncate(ac.getmTotROctubre(), 2)) != 0) {
                    cMensajeAdec += "Calendario no Compensado en el Mes de Octubre.";
                }
                if ((com.syc.contable.util.Math.truncate(ac.getmTotANoviembre(), 2) - com.syc.contable.util.Math.truncate(ac.getmTotRNoviembre(), 2)) != 0) {
                    cMensajeAdec += "Calendario no Compensado en el Mes de Noviembre.";
                }
                if ((com.syc.contable.util.Math.truncate(ac.getmTotADiciembre(), 2) - com.syc.contable.util.Math.truncate(ac.getmTotRDiciembre(), 2)) != 0) {
                    cMensajeAdec += "Calendario no Compensado en el Mes de Diciembre.";
                }
            }
            if (iDiferencia != 0) {
                cMensajeAdec += "Calendario Anual no Compensado.";
            }
            if (ac.getiTotAbonos() == 0 && ac.getiTotCargos() != 0) {
                // reducción líquida todos pueden hacerla
                cMensajeAdec = "";
            } else if (ac.getiTotAbonos() != 0 && ac.getiTotCargos() == 0) {
                // ampliaciones líquidas todos pueden hacerla
                cMensajeAdec = "";
            }
            ac.setMensaje(cMensajeAdec);
            System.out.println("Termina la carga Generar datos de base de datos.");
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return ac;
    }

    public static FIAFEncabezado getFIAFEncabezado(Connection conn, int nFolio) throws SQLException {
        PreparedStatement pstmd = null;
        ResultSet rsd = null;
        String cQuery = "select * from tFIAFEncabezado with (nolock) where nFolioFIAF = " + nFolio + " ";
        FIAFEncabezado fe = new FIAFEncabezado();
        try {
            pstmd = conn.prepareStatement(cQuery);
            rsd = pstmd.executeQuery();
            if (rsd.next()) {
                fe.setnFolioFIAF(rsd.getInt("nFolioFIAF"));
                fe.setfCreacion(rsd.getDate("fCreacion"));
                fe.setnAutorizacionMAP(rsd.getString("nAutorizacionMAP"));
                fe.setfMAP(rsd.getDate("fMAP"));
                fe.setcMotivoRechazo(rsd.getString("cMotivoRechazo"));
                fe.setcUnidadResponsableContable(rsd.getString("cUnidadResponsableContable"));
                fe.setnFolioPolizaCancelacion(rsd.getInt("nFolioPolizaCancelacion"));
                fe.setfCancelacion(rsd.getDate("fCancelacion"));
                fe.setcDescripcionPoliza(rsd.getString("cDescripcionPoliza"));
                fe.setcJustificacionA(rsd.getString("cJustificacionA"));
                fe.setcJustificacionR(rsd.getString("cJustificacionR"));
                fe.setcJustificacionNormativa(rsd.getString("cJustificacionNormativa"));
                fe.setnFolioTramiteSicop(rsd.getString("nFolioTramiteSicop"));
                fe.setfSicop(rsd.getDate("fSicop"));
                fe.setcMotivoRechazoSicop(rsd.getString("cMotivoRechazoSicop"));
                fe.setnConsecutivoSicop(rsd.getInt("nConsecutivoSicop"));
                fe.setcDocumentoHaplicado(rsd.getString("cDocumentoHaplicado"));
                fe.setnFolioPoliza(rsd.getInt("nFolioPoliza"));
                fe.setcTipoPoliza(rsd.getString("cTipoPoliza"));
                fe.setcRamo(rsd.getString("cRamo"));
                fe.setfAplicacion(rsd.getDate("fAplicacion"));
                fe.setaEjercicioFiscal(rsd.getString("aEjercicioFiscal"));
                fe.setU_LOGIN(rsd.getString("U_LOGIN"));
                fe.setcUnidadResponsable(rsd.getString("cUnidadResponsable"));
                fe.setcCentroContable(rsd.getString("cCentroContable"));
            }
        } finally {
            if (pstmd != null)
                pstmd.close();
            pstmd = null;
            if (rsd != null)
                rsd.close();
            rsd = null;
        }
        return fe;
    }

    public static void actualizaJustificaciones(Connection conn, String justificacionA, String justificacionR, String justificacionN, int folioAdecuacion) throws SQLException {
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("UPDATE tAdecuacionEncabezado SET justificacionA=?,justificacionR=?,justificacionN=? WHERE nFolioAdecuacion=?");
            pstm.setString(1, justificacionA);
            pstm.setString(2, justificacionR);
            pstm.setString(3, justificacionN);
            pstm.setInt(4, folioAdecuacion);
            pstm.executeUpdate();
            conn.commit();
        } finally {
            if (pstm != null)
                pstm.close();
            pstm = null;
        }
    }

    public static void actualizaNivelFIAF(Connection conn, int nFolioFIAF, int nNivel, String cTipoAdecuacion) throws SQLException {
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("UPDATE tFIAFEncabezado SET nNivel=?,cTipoAdecuacion=? WHERE nFolioFIAF=?");
            pstm.setInt(1, nNivel);
            pstm.setString(2, cTipoAdecuacion);
            pstm.setInt(3, nFolioFIAF);
            pstm.executeUpdate();
            conn.commit();
        } finally {
            if (pstm != null)
                pstm.close();
            pstm = null;
        }
    }

    public static ArrayList<Integer> getIdCasoAdecuacionFIAFCorreo(Connection conn, int folio) throws SQLException {
        ArrayList<Integer> folios = new ArrayList<Integer>();
        ResultSet res = null;
        PreparedStatement pstm = null;
        pstm = conn.prepareStatement("SELECT * FROM tAdecuacionEncabezado with(nolock) WHERE nFolioFIAF = ?");
        pstm.setInt(1, folio);
        res = pstm.executeQuery();
        while (res.next()) {
            folios.add(res.getInt("id_caso"));
        }
        if (pstm != null) {
            pstm.close();
        }
        if (res != null) {
            res.close();
        }
        return folios;
    }

    public static ArrayList<Integer> getFoliosFIAFConsolidacion(Connection conn, int folioFIAF) throws SQLException {
        ArrayList<Integer> folios = new ArrayList<Integer>();
        ResultSet res = null;
        PreparedStatement pstm = null;
        pstm = conn.prepareStatement("SELECT nFolioFIAF FROM tFIAFEncabezado with(nolock) WHERE nFolioConsolidacion = ?");
        pstm.setInt(1, folioFIAF);
        res = pstm.executeQuery();
        while (res.next()) {
            folios.add(res.getInt("nFolioFIAF"));
        }
        if (pstm != null) {
            pstm.close();
        }
        if (res != null) {
            res.close();
        }
        return folios;
    }

    public static Map<String, String> getCorreoUsuarioAdecuacionCincoDias(Connection conn, int id_oper, int dias) throws SQLException {
        Map<String, String> map = new TreeMap<String, String>();
        ResultSet res = null;
        PreparedStatement pstm = null;
        pstm = conn.prepareStatement("SELECT usuario.U_LOGIN, usuario.U_EMAIL as revisor_email, unidad_responsable.cUnidadResponsable " + " FROM VIMX_USUARIO usuario, tCatalogoUnidadResponsable unidad_responsable, CG_USUARIO_GRUPO usuario_grupo" + " WHERE usuario.ID_AREA = unidad_responsable.ID_AREA AND usuario.U_LOGIN = usuario_grupo.U_LOGIN AND usuario_grupo.G_NOMBRE = 'JEFATURA_ADECUACIONES'");
        res = pstm.executeQuery();
        while (res.next()) {
            map.put(res.getString("U_LOGIN"), res.getString("revisor_email"));
        }
        if (pstm != null) {
            pstm.close();
        }
        if (res != null) {
            res.close();
        }
        return map;
    }

    public static Map<String, CorreoAdecuacion> getCorreoUsuarioAdecuacion(Connection conn, int id_oper, int dias) throws SQLException {
        Map<String, CorreoAdecuacion> map = new TreeMap<String, CorreoAdecuacion>();
        ResultSet res = null;
        PreparedStatement pstm = null;
        StringBuilder query = new StringBuilder();
        query.append("SELECT DISTINCT * ");
        query.append("	FROM ( SELECT usuario.U_LOGIN, usuario.U_EMAIL as revisor_email, unidad_responsable.cUnidadResponsable");
        query.append("		FROM VIMX_USUARIO usuario With(NOLOCK), tCatalogoUnidadResponsable unidad_responsable With(NOLOCK), CG_USUARIO_GRUPO usuario_grupo With(NOLOCK) ");
        query.append("	WHERE usuario.ID_AREA = unidad_responsable.ID_AREA AND usuario.U_LOGIN = usuario_grupo.U_LOGIN ");
        query.append(" AND (usuario_grupo.G_NOMBRE = 'REVISORES_ADECUACIONES' or usuario_grupo.G_NOMBRE = 'xREVISORES_ADECUACIONESx')");
        query.append("	) AS revisores,( SELECT DISTINCT caso.ID_CASO, caso.co_responsable, usuario.U_EMAIL, caso.unidad_responsable, caso.folio, caso.C_ALARMA");
        query.append("	FROM ( SELECT DISTINCT operacion.ID_CASO, CASE WHEN bitacora.co_responsable IS NULL THEN operacion.CO_RESPONSABLE ELSE bitacora.co_responsable END as co_responsable,");
        query.append("			SUBSTRING(caso.c_folio, 6,3 ) AS unidad_responsable, caso.c_folio as folio, caso.C_ALARMA");
        query.append("	FROM CG_CASO_OPERACION operacion with(nolock)");
        query.append("	LEFT OUTER JOIN( SELECT B_ID_CASO AS id_caso, B_CO_RESPONSABLE_EJEC AS co_responsable");
        query.append("				FROM CG_BITACORA bitacora WITH(NOLOCK)");
        query.append("				WHERE bitacora.B_ID_TC = 3 AND bitacora.ID_BITACORA = (SELECT MIN(id_bitacora) ");
        query.append("																		FROM CG_BITACORA WITH(NOLOCK)");
        query.append("																		WHERE B_ID_CASO = bitacora.B_ID_CASO");
        query.append("																		AND B_ID_TC = 3)" + "	) bitacora");
        query.append("	ON operacion.ID_CASO = bitacora.id_caso");
        query.append("	LEFT OUTER JOIN CG_CASO caso with(nolock)	ON caso.ID_CASO = operacion.ID_CASO	");
        query.append("	WHERE operacion.ID_TC = 3 AND operacion.ID_OPER = ? AND dbo.SUMA_DIAS_HABILES(co_fecha_ini,?) < GETDATE()) AS caso");
        query.append("	LEFT OUTER JOIN cg_usuario usuario with(nolock)	ON caso.co_responsable = usuario.U_LOGIN OR caso.co_responsable = usuario.U_NOMBRE");
        query.append("	WHERE U_EMAIL IS NOT NULL) AS CASOS");
        query.append("	WHERE revisores.cUnidadResponsable = CASOS.unidad_responsable");
        query.append("	ORDER BY ID_CASO,unidad_responsable");
        pstm = conn.prepareStatement(query.toString());
        pstm.setInt(1, id_oper);
        pstm.setInt(2, dias);
        res = pstm.executeQuery();
        while (res.next()) {
            CorreoAdecuacion adtemp = new CorreoAdecuacion();
            if (map.containsKey(res.getString("folio"))) {
                adtemp = map.get(res.getString("folio"));
                adtemp.setCorreos(adtemp.getCorreos() + ";" + res.getString("revisor_email"));
                map.put(res.getString("folio"), adtemp);
            } else {
                adtemp.setCorreos(res.getString("U_EMAIL") + ";" + res.getString("revisor_email"));
                adtemp.setIdCaso(res.getInt("ID_CASO"));
                adtemp.setAlarma(res.getString("C_ALARMA"));
                map.put(res.getString("folio"), adtemp);
            }
        }
        if (pstm != null) {
            pstm.close();
        }
        if (res != null) {
            res.close();
        }
        return map;
    }

    public static List<CorreosJefatura> getMensaje(Connection conn, int id_oper, int dias) throws SQLException {
        List<CorreosJefatura> correosJef = new ArrayList<CorreosJefatura>();
        ResultSet res = null;
        PreparedStatement pstm = null;
        pstm = conn.prepareStatement("select operacion.nFolioAdecuacion, operacion.fCarga, operacion.fAplicacion, operacion.alertaCorreo,dbo.SUMA_DIAS_HABILES( fAplicacion, 5),GETDATE() from tADECUACIONEncabezado operacion With(NOLOCK) inner join cg_caso caso With(NOLOCK)" + " on operacion.id_caso = caso.ID_CASO inner join CG_CASO_OPERACION co With(NOLOCK) on operacion.id_caso = co.id_caso and co.ID_CASO=caso.ID_CASO" + " where caso.ID_TC = 3 and co.ID_OPER <> 6 and dbo.SUMA_DIAS_HABILES( fAplicacion, 5) < getDate()		and (operacion.alertaCorreo<>1 or operacion.alertaCorreo is null)");
        res = pstm.executeQuery();
        while (res.next()) {
            CorreosJefatura correosJefatura = new CorreosJefatura();
            correosJefatura.setAlertaCorreo(res.getInt("alertaCorreo"));
            correosJefatura.setnFolio(res.getInt("nFolioAdecuacion"));
            correosJefatura.setfAplicacion(res.getDate("fAplicacion"));
            correosJef.add(correosJefatura);
        }
        if (pstm != null) {
            pstm.close();
        }
        if (res != null) {
            res.close();
        }
        return correosJef;
    }

    public static void actualizaAlarma(Connection conn, String valor, int idCaso) throws SQLException {
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("UPDATE CG_CASO set C_ALARMA=? where ID_CASO=?");
            pstm.setString(1, valor);
            pstm.setInt(2, idCaso);
            pstm.executeUpdate();
            conn.commit();
        } finally {
            if (pstm != null)
                pstm.close();
            pstm = null;
        }
    }

    public static void actualizaAlarmaCincoDias(Connection conn, int valor, int folio) throws SQLException {
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("UPDATE tADECUACIONEncabezado set alertaCorreo=? where nFolioAdecuacion=?");
            pstm.setInt(1, valor);
            pstm.setInt(2, folio);
            pstm.executeUpdate();
            conn.commit();
        } finally {
            if (pstm != null)
                pstm.close();
            pstm = null;
        }
    }

    public static void actualizaMotivoCancelacion(Connection conn, String motivo, int folio) throws SQLException {
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("UPDATE tADECUACIONEncabezado set cMotivoCancelacion=? where nFolioAdecuacion=?");
            pstm.setString(1, motivo);
            pstm.setInt(2, folio);
            pstm.executeUpdate();
            conn.commit();
        } finally {
            if (pstm != null)
                pstm.close();
            pstm = null;
        }
    }

    public static int actualizaEventoAdecuacionLiquida(Connection conn, String cEvento, int nFolioAdecuacion) throws Exception {
        PreparedStatement ps = null;
        String query = "UPDATE tAdecuacionDetalle SET cevento = ? WHERE nFolioAdecuacion = ?";
        int r = 0;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, cEvento);
            ps.setInt(2, nFolioAdecuacion);
            r = ps.executeUpdate();
            return r;
        } finally {
            if (ps != null)
                CloseObject.closeObject(ps, false);
        }
    }

    public static int actualizaPolizaFIAF(Connection conn, int folio, String aut) throws Exception {
        PreparedStatement ps = null;
        String query = "update tAdecuacionEncabezado set cDocumentoHaplicado='S', nFolioPoliza=(SELECT nFolioPoliza from tFIAFEncabezado where nFolioFIAF=?) where nFolioFIAF=?";
        if ("true".equals(aut))
            query = "update tAdecuacionAutEncabezado set cDocumentoHaplicado='S', nFolioPoliza=(SELECT nFolioPoliza from tFIAFAutEncabezado where nFolioFIAF=?) where nFolioFIAF=?";
        int r = 0;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, folio);
            ps.setInt(2, folio);
            r = ps.executeUpdate();
            return r;
        } finally {
            if (ps != null)
                CloseObject.closeObject(ps, false);
        }
    }

    public static String validaMezclaNivel(Connection conn, String folio) throws Exception {
        CallableStatement cstmt = null;
        ResultSet rs = null;
        String result = "";
        try {
            cstmt = conn.prepareCall("{call dbo.fn_valida_adecuacion_mezcla_nivel( ? ) }");
            cstmt.setString(1, folio);
            rs = cstmt.executeQuery();
            if (rs.next())
                result = rs.getString(1);
            if ("true".equalsIgnoreCase(result))
                return "\\nLa afectacion contiene movimientos tanto de calendario como de transferencia por lo que no es procedente.";
            else
                return "";
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cstmt, false);
        }
    }

    /**
     * Validacion de calendario. Ejecuta el SP fn_valida_adecuacion_calendario.
     * NOTA: Previamente se debio validar que la adecuacion sea un calendario.
     * De otra manera el resultado que regrese este procedimiento no sera real.
     *
     * @param conn
     *            Conexion activa a la base de datos
     * @param folio
     *            Folio de la adecuacion
     * @return Lista con los errores encontrados en el calendario. Si regresa
     *         una cadena vacia no se encontraron errores.
     * @throws Exception
     */
    public static String validaCalendario(Connection conn, String folio) throws Exception {
        CallableStatement cstmt = null;
        // ClasificacionAdecuacion ca = null;
        ResultSet rs = null;
        String result = "";
        try {
            cstmt = conn.prepareCall("{call fn_valida_adecuacion_calendario( ? ) }");
            cstmt.setString(1, folio);
            rs = cstmt.executeQuery();
            while (rs.next()) result += "\\n" + rs.getString(1);
            return result;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cstmt, false);
        }
    }

    public static int borraOperacionesRepetidas(Connection conn) throws Exception {
        String querySelect = "SELECT id_caso, Max(id_oper) id_oper, Max(id_caso_oper) id_caso_oper FROM   cg_caso_operacion WITH( nolock ) WHERE  id_tc = 3 AND id_oper <> 6 GROUP  BY id_caso HAVING Count(*) > 1";
        String queryUpdate = "UPDATE tbl_bloqueo WITH (rowlock) " + "SET    nbloqueo = 1 WHERE nbloqueo = 1";
        String queryDelete = "DELETE FROM cg_caso_operacion WITH (ROWLOCK) WHERE id_caso = ? AND id_oper = ? AND id_caso_oper = ?";
        int eliminados = 0;
        PreparedStatement psSelect = null;
        PreparedStatement psUpdate = null;
        PreparedStatement psDelete = null;
        ResultSet rs = null;
        try {
            psUpdate = conn.prepareStatement(queryUpdate);
            psUpdate.executeUpdate();
            psDelete = conn.prepareStatement(queryDelete);
            psSelect = conn.prepareStatement(querySelect);
            rs = psSelect.executeQuery();
            while (rs.next()) {
                psDelete.setInt(1, rs.getInt("id_caso"));
                psDelete.setInt(2, rs.getInt("id_oper"));
                psDelete.setInt(3, rs.getInt("id_caso_oper"));
                eliminados += psDelete.executeUpdate();
            }
            conn.commit();
            return eliminados;
        } catch (Exception e) {
            log.error("Error mientras se eliminaban operaciones duplicadas. " + e, e);
            try {
                if (conn != null)
                    conn.rollback();
            } catch (Exception e2) {
                log.error("Error realizando rollback " + e2, e2);
            }
            throw e;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(psUpdate, false);
            CloseObject.closeObject(psSelect, false);
            CloseObject.closeObject(psDelete, false);
        }
    }

    /**
     * Inserta una adecuacion validada a la base de datos.
     *
     * @param conn
     *            Conexion activa a la base de datos
     * @param adecuacion
     *            Adecuacion valida
     * @return Numero de registros insertados
     * @throws Exception
     */
    public static int insertaAdecuacion(Connection conn, Adecuacion adecuacion) throws Exception {
        int retval = 0;
        int borrados = 0;
        log.info("Object: {}", "Insertando encabezado y detalle de la adecuacion con el numero de folio " + adecuacion.getEncabezado().getnFolioAdecuacion());
        borrados += AdecuacionDetalleManager.deleteAdecuacionDetalle(conn, adecuacion.getEncabezado().getnFolioAdecuacion());
        borrados += AdecuacionEncabezadoManager.deleteAdecuacionEncabezado(conn, adecuacion.getEncabezado().getnFolioAdecuacion());
        log.info("Object: {}", "Se eliminaron " + borrados + " registros previos ");
        retval += AdecuacionEncabezadoManager.insertaAdecuacionEncabezado(conn, adecuacion.getEncabezado());
        retval += AdecuacionDetalleManager.insertaAdecuacionDetalle(conn, adecuacion.getDetalle(), adecuacion.getEncabezado().getnFolioAdecuacion(), adecuacion.getEncabezado().getCentroContable());
        return retval;
    }

    /**
     * Validacion de partidas especiales. Ejecuta el SP
     * fn_valida_adecuacion_partidas_especiales. NOTA: Previamente se debio
     * validar que la adecuacion sea una transferencia. De otra manera el
     * resultado que regrese este procedimiento no sera real.
     *
     * @param conn
     *            Conexion activa a la base de datos
     * @param folio
     *            Folio de la adecuacion
     * @return En estas partidas se tienen casos en los que no debe continuar y
     *         casos donde solo arroja advertencias. Si encuentra una partida
     *         que no permite continuar entonces arroja una excepcion en otro
     *         caso regresa una lista concatenada de advertencias.
     * @throws Exception
     */
    public static ArrayList<String> validaPartidasEspeciales(Connection conn, String folio) throws Exception {
        CallableStatement cstmt = null;
        ResultSet rs = null;
        ArrayList<String> mensajes = new ArrayList<String>();
        try {
            cstmt = conn.prepareCall("{call fn_valida_adecuacion_partidas_especiales( ? ) }");
            cstmt.setString(1, folio);
            rs = cstmt.executeQuery();
            while (rs.next()) {
                mensajes.add(rs.getString(1));
            }
            return mensajes;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cstmt, false);
        }
    }

    /**
     * Regresa una adecuacion almacenada en la base de datos.
     *
     * @param conn
     *            Conexion abierta a la base de datos
     * @param folio
     *            Folio de la adecuacion
     * @return Adecuacion si existe.
     */
    public static Adecuacion cargaAdecuacion(Connection conn, int folio) throws Exception {
        Adecuacion adecuacion = null;
        AdecuacionEncabezado encabezado = AdecuacionEncabezadoManager.readAdecuacionEncabezado(conn, folio);
        List<AdecuacionDetalle> detalle = AdecuacionDetalleManager.readAdecuacionDetalle(conn, folio);
        adecuacion = new Adecuacion(encabezado, detalle);
        return adecuacion;
    }

    /**
     * Regresa una adecuacion almacenada en la base de datos incluyendo el
     * proyecto al que le corresponde.
     *
     * @param conn
     *            Conexion abierta a la base de datos
     * @param folio
     *            Folio de la adecuacion
     * @return Adecuacion si existe.
     */
    public static Adecuacion cargaAdecuacionProyecto(Connection conn, int folio) throws Exception {
        Adecuacion adecuacion = null;
        AdecuacionEncabezado encabezado = AdecuacionEncabezadoManager.readAdecuacionEncabezado(conn, folio);
        List<AdecuacionDetalle> detalle = AdecuacionDetalleManager.readAdecuacionDetalleProyecto(conn, folio);
        adecuacion = new Adecuacion(encabezado, detalle);
        return adecuacion;
    }

    public static int insertaEpsNuevas(Connection conn, String folio) throws Exception {
        int retval = 0;
        log.info("Insertando EP nueva en el catalogo");
        retval += AdecuacionDetalleManager.insertaEPCatalogo(conn, folio);
        return retval;
    }

    public static int buscaFolioIntegracion(Connection conn) throws SQLException {
        int nFolioIntegra = 0;
        PreparedStatement pstmd = null;
        ResultSet rsd = null;
        String cQuery = "SELECT (MAX(nFolioCONSOLIDACION) + 1) AS FolioIntegra FROM dbo.tConsolidacionEncabezado WITH (NOLOCK)";
        try {
            pstmd = conn.prepareStatement(cQuery);
            rsd = pstmd.executeQuery();
            if (rsd.next()) {
                nFolioIntegra = rsd.getInt(1);
            }
        } finally {
            if (pstmd != null)
                pstmd.close();
            pstmd = null;
            if (rsd != null)
                rsd.close();
            rsd = null;
        }
        return nFolioIntegra;
    }

    public static String integraAdecuaciones2(Connection conn, int nFolio, String[] arrFolios, Usuario u) throws SQLException {
        String cVretorno = "";
        String cUnidadUsuario = u.getU_UR();
        String cNFolioAdecuacion = "";
        int nFolioAdecuacion = 0;
        String[] arrnFolioAdecuacion = null;
        boolean encabezadoExiste = false;
        int i = 0;
        PreparedStatement pstmen = null;
        PreparedStatement pstsel = null;
        PreparedStatement pstmdt = null;
        PreparedStatement pstmdtu = null;
        ResultSet rs = null;
        String selectHeader = "SELECT * FROM tConsolidacionEncabezado where nFolioConsolidacion=?";
        String cInsertHeder = "INSERT INTO tConsolidacionEncabezado (nFolioCONSOLIDACION, fCreacion, cUnidadResponsableContable, cDescripcionPoliza, cJustificacion) VALUES (?,?,?,?,?)";
        String cInsertDetalle = "INSERT INTO tConsolidacionDetalle ( nFolioCONSOLIDACION, nFolioAdecuacion ) VALUES ( ?, ? )";
        try {
            pstsel = conn.prepareStatement(selectHeader);
            pstsel.setInt(1, nFolio);
            rs = pstsel.executeQuery();
            if (rs.next()) {
                encabezadoExiste = true;
            }
            if (!encabezadoExiste) {
                pstmen = conn.prepareStatement(cInsertHeder);
                pstmen.setInt(1, nFolio);
                pstmen.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                pstmen.setString(3, cUnidadUsuario);
                pstmen.setString(4, "Integracion de Adecuaciones para Capturarlas en SICOP");
                pstmen.setString(5, "");
                pstmen.execute();
            }
            while (arrFolios.length > i) {
                arrnFolioAdecuacion = arrFolios[i].split(",");
                cNFolioAdecuacion = arrnFolioAdecuacion[0];
                nFolioAdecuacion = new Integer(cNFolioAdecuacion).intValue();
                // inserta detalle de Consolidado (Integracion de Adecuaciones)
                pstmdt = conn.prepareStatement(cInsertDetalle);
                pstmdt.setInt(1, nFolio);
                pstmdt.setInt(2, nFolioAdecuacion);
                if (!arrFolios[i].contains("FIAF"))
                    pstmdt.execute();
                // Actualiza Encavezado de Adecuacion
                String cUpdateAdecuacion = "UPDATE tAdecuacionEncabezado SET nFolioCONSOLIDACION= ? WHERE nFolioAdecuacion = ? ";
                if (arrFolios[i].contains("FIAF"))
                    cUpdateAdecuacion = "UPDATE tFIAFEncabezado SET nFolioCONSOLIDACION= ? WHERE nFolioFIAF = ? ";
                pstmdtu = conn.prepareStatement(cUpdateAdecuacion);
                pstmdtu.setInt(1, nFolio);
                pstmdtu.setInt(2, nFolioAdecuacion);
                pstmdtu.execute();
                i++;
            }
            conn.commit();
        } finally {
            if (pstmen != null)
                pstmen.close();
            pstmen = null;
            if (pstmdt != null)
                pstmdt.close();
            pstmdt = null;
            if (pstmdtu != null)
                pstmdtu.close();
            pstmdtu = null;
        }
        return cVretorno;
    }

    public static ArrayList<String> validaIntegracionNeteo(Connection conn, int nFolioIADE) throws Exception {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        ArrayList<String> arrMResult = new ArrayList<String>();
        String query = "SELECT MSG FROM Fn_valida_adecuacion_iade_neteo(?)";
        log.trace("Object: {}", "Validando neteo de integracion [" + nFolioIADE + "]");
        log.trace("Object: {}", "Ejecutando [" + query + "][" + nFolioIADE + "]");
        try {
            pstmnt = conn.prepareStatement(query);
            pstmnt.setInt(1, nFolioIADE);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                arrMResult.add(rs.getString(1));
            }
            log.info("Object: {}", "Validacion de neto de integracion[" + nFolioIADE + "] terminada");
            return arrMResult;
        } finally {
            CloseObject.closeObject(pstmnt, false);
            CloseObject.closeObject(rs, false);
        }
    }

    public static ArrayList consultaFap02IADEAmpliaExcel(Connection conn, int nFolio) throws SQLException {
        ArrayList<ArrayList<String>> arrAdecuaciones = new ArrayList<>();
        ResultSet rsd = null;
        String nfolioadec = "";
        String programa = "";
        String importe = "";
        String cQuery = "{call sp_fap02_iade_ampl (" + nFolio + ") }";
        CallableStatement cs = null;
        cs = conn.prepareCall(cQuery);
        try {
            rsd = cs.executeQuery();
            while (rsd.next()) {
                ArrayList<String> arrAdecuad = new ArrayList<>();
                nfolioadec = rsd.getString(1);
                arrAdecuad.add(nfolioadec);
                programa = rsd.getString(2);
                arrAdecuad.add(programa);
                importe = rsd.getString(3);
                arrAdecuad.add(importe);
                arrAdecuaciones.add(arrAdecuad);
            }
        } finally {
            CloseObject.closeObject(cs);
            CloseObject.closeObject(rsd);
        }
        return arrAdecuaciones;
    }

    public static ArrayList<ArrayList<String>> consultaFap02IADEReduceExcel(Connection conn, int nFolio) throws SQLException {
        ArrayList<ArrayList<String>> arrAdecuaciones = new ArrayList<>();
        ResultSet rsd = null;
        String nfolioadec = "";
        String programa = "";
        String importe = "";
        String cQuery = "{call sp_fap02_IADE_reduc (" + nFolio + ") }";
        CallableStatement cs = null;
        cs = conn.prepareCall(cQuery);
        try {
            rsd = cs.executeQuery();
            while (rsd.next()) {
                ArrayList<String> arrAdecuad = new ArrayList<>();
                nfolioadec = rsd.getString(1);
                arrAdecuad.add(nfolioadec);
                programa = rsd.getString(2);
                arrAdecuad.add(programa);
                importe = rsd.getString(3);
                arrAdecuad.add(importe);
                // encabezado
                arrAdecuaciones.add(arrAdecuad);
            }
        } finally {
            CloseObject.closeObject(cs);
            CloseObject.closeObject(rsd);
        }
        return arrAdecuaciones;
    }

    public static List<Fap01> cargaFAP01(Connection conn, int nFolioAdecuacion) throws Exception {
        String query = "SELECT nFolioAdecuacion ," + "        ep ," + "        movimiento ," + "        enero ," + "        febrero ," + "        marzo ," + "        abril ," + "        mayo ," + "        junio ," + "        julio ," + "        agosto ," + "        septiembre ," + "        octubre ," + "        noviembre ," + "        diciembre ," + "        anual ," + "        ejercicio ," + "        ramo ," + "        unidad_responsable ," + "        gf ," + "        f ," + "        sf ," + "        PG ," + "        AI ," + "        PP ," + "        OG ," + "        tg ," + "        ff ," + "        ef ," + "        cartera ," + "        AE," + "        nFolioAdecuacion" + " FROM  vFAP01_ADEC WITH(NOLOCK) " + "WHERE nfolioAdecuacion = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Fap01> result = new ArrayList<Fap01>();
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, nFolioAdecuacion);
            rs = ps.executeQuery();
            while (rs.next()) {
                Fap01 tmp = new Fap01();
                tmp.setAE(rs.getString("AE"));
                tmp.setAI(rs.getString("AI"));
                tmp.setCartera(rs.getString("cartera"));
                tmp.setEf(rs.getString("ef"));
                tmp.setEjercicio(rs.getString("ejercicio"));
                tmp.setEp(rs.getString("ep"));
                tmp.setF(rs.getString("f"));
                tmp.setFf(rs.getString("ff"));
                tmp.setGf(rs.getString("gf"));
                tmp.setMovimiento(rs.getString("movimiento"));
                tmp.setNfolioconsolidacion(rs.getInt("nfolioAdecuacion"));
                tmp.setOG(rs.getString("OG"));
                tmp.setPG(rs.getString("PG"));
                tmp.setPP(rs.getString("PP"));
                tmp.setRamo(rs.getString("ramo"));
                tmp.setSf(rs.getString("sf"));
                tmp.setTg(rs.getString("tg"));
                tmp.setUnidadResponsable(rs.getString("unidad_responsable"));
                double[] montos = new double[13];
                for (int i = 0; i < Util.NOMBRE_MESES_ADECUACIONES.length; i++) {
                    montos[i] = rs.getDouble(Util.NOMBRE_MESES_ADECUACIONES[i]);
                }
                tmp.setMontos(montos);
                result.add(tmp);
            }
            return result;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static String[] obtienenNumCAL(Connection conn, int nFolio) throws SQLException {
        // boolean rVar = false;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String[] nNumCAL = new String[2];
        String cQueryA = " select nNumCAL, CONVERT( VARCHAR(32), fCal, 103 ) from tADECUACIONAUTEncabezado WTIH(NOLOCK) where nFolioAdecuacionaut= " + nFolio;
        try {
            pstm = conn.prepareStatement(cQueryA);
            rs = pstm.executeQuery();
            if (rs.next()) {
                nNumCAL[0] = rs.getString(1);
                nNumCAL[1] = rs.getString(2);
            }
        } finally {
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(rs);
        }
        return nNumCAL;
    }

    public static String obtieneTipoAdec(Connection conn, int nFolio) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String tipoAdec = "";
        String cQueryA = " SELECT cTipoAdecuacion FROM dbo.tAdecuacionEncabezado WITH (NOLOCK) WHERE nFolioAdecuacion = ? ";
        try {
            pstm = conn.prepareStatement(cQueryA);
            pstm.setInt(1, nFolio);
            rs = pstm.executeQuery();
            if (rs.next()) {
                tipoAdec = rs.getString(1);
            }
        } finally {
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(rs);
        }
        return tipoAdec;
    }

    public static boolean esControlAmbiental(Connection conn) throws Exception {
        String query = "SELECT GP_VALOR FROM CG_GRUPO_PROPIEDADES WITH(nolock) WHERE G_NOMBRE = 'PREFERENCIAS_CLIENTE' AND GP_NOMBRE = 'SAI_AMBIENTAL'";
        Statement stmnt = null;
        ResultSet rs = null;
        boolean esControlAmbiental = false;
        try {
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery(query);
            if (rs.next()) {
                esControlAmbiental = "true".equalsIgnoreCase(rs.getString(1));
            }
            return esControlAmbiental;
        } finally {
            CloseObject.closeObject(stmnt, false);
            CloseObject.closeObject(rs, false);
        }
    }

    public static boolean esControlFonden(Connection conn) throws Exception {
        String query = "SELECT GP_VALOR FROM CG_GRUPO_PROPIEDADES WITH(nolock) WHERE G_NOMBRE = 'PREFERENCIAS_CLIENTE' AND GP_NOMBRE = 'SAI_FONDEN'";
        Statement stmnt = null;
        ResultSet rs = null;
        boolean esControlFonden = false;
        try {
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery(query);
            if (rs.next()) {
                esControlFonden = "true".equalsIgnoreCase(rs.getString(1));
            }
            return esControlFonden;
        } finally {
            CloseObject.closeObject(stmnt, false);
            CloseObject.closeObject(rs, false);
        }
    }

    public static ArrayList<String> validaSaldoDispobible(Connection conn, int nFolioAdecuacion) throws Exception {
        ArrayList<String> mensajes = new ArrayList<String>();
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_validaSaldos_enRevision( ? ) }";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, nFolioAdecuacion);
            rs = cs.executeQuery();
            while (rs.next()) {
                mensajes.add(rs.getString(1));
            }
            return mensajes;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    public static ArrayList<String> validaPlurianueles(Connection conn, int nFolioAdecuacion) throws Exception {
        ArrayList<String> mensajes = new ArrayList<String>();
        CallableStatement cs = null;
        ResultSet rs = null;
        String cFolio = "A" + String.valueOf(nFolioAdecuacion);
        ;
        String query = "{call sp_valida_EP_plurianual( ? ) }";
        try {
            cs = conn.prepareCall(query);
            cs.setString(1, cFolio);
            rs = cs.executeQuery();
            while (rs.next()) {
                mensajes.add(rs.getString(1));
            }
            return mensajes;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    public static ArrayList<Object> buscaDatosSIAFFSICOP(Connection conn, String lineaCaptura, int folio) throws SQLException {
        boolean rVar = false;
        ArrayList<Object> arrResultado = new ArrayList<>();
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int FOLIO_REINTEGRO = 0;
        int FOLIO_REINTEGRO_DEP = 0;
        String fechaAplicacion = "";
        int REGISTROS_REINTEGRO_SIAFF = 0;
        int REGISTROS_REINTEGRO_DETALLE = 0;
        PreparedStatement pstm1 = null;
        ResultSet rs1 = null;
        String cQuery2 = "select COUNT(distinct FOLIO_CLC) AS c_rs , isnull(FOLIO_REINTEGRO,0), isnull(FOLIO_REINTEGRO_DEP,0), isnull(FECHA_APLICACION,'') , (SELECT COUNT ( distinct noCLC) FROM tReintegroDetalle with(nolock) WHERE nFolioReintegro = " + folio + " ) as c_rd " + "  from REINTEGRO_SIAFF where FOLIO_CLC in ( SELECT distinct noCLC FROM tReintegroDetalle with(nolock) WHERE nFolioReintegro = " + folio + ") " + " and FOLIO_CLC_DEPENDENCIA in ( SELECT distinct nFolioDependencia FROM tReintegroDetalle with(nolock) WHERE nFolioReintegro = " + folio + ") " + " and LINEA_CAPTURA = '" + lineaCaptura + "'  group by FOLIO_REINTEGRO, FOLIO_REINTEGRO_DEP, FECHA_APLICACION";
        try {
            pstm1 = conn.prepareStatement(cQuery2);
            rs1 = pstm1.executeQuery();
            if (rs1.next()) {
                REGISTROS_REINTEGRO_SIAFF = rs1.getInt(1);
                REGISTROS_REINTEGRO_DETALLE = rs1.getInt(5);
            }
            if ((REGISTROS_REINTEGRO_SIAFF == REGISTROS_REINTEGRO_DETALLE) && (REGISTROS_REINTEGRO_SIAFF != 0 && REGISTROS_REINTEGRO_DETALLE != 0)) {
                String cQueryA = " select distinct isnull(FOLIO_REINTEGRO,0), isnull(FOLIO_REINTEGRO_DEP,0), isnull(FECHA_APLICACION,'')" + "  from REINTEGRO_SIAFF where FOLIO_CLC in ( SELECT distinct noCLC FROM tReintegroDetalle with(nolock) WHERE nFolioReintegro = " + folio + ") " + " and FOLIO_CLC_DEPENDENCIA in ( SELECT distinct nFolioDependencia FROM tReintegroDetalle with(nolock) WHERE nFolioReintegro = " + folio + ") " + " and LINEA_CAPTURA = '" + lineaCaptura + "'";
                try {
                    pstm = conn.prepareStatement(cQueryA);
                    rs = pstm.executeQuery();
                    if (rs.next()) {
                        FOLIO_REINTEGRO = rs.getInt(1);
                        FOLIO_REINTEGRO_DEP = rs.getInt(2);
                        fechaAplicacion = rs.getString(3);
                        arrResultado.add(FOLIO_REINTEGRO);
                        arrResultado.add(FOLIO_REINTEGRO_DEP);
                        arrResultado.add(fechaAplicacion);
                    }
                    if (FOLIO_REINTEGRO > 0) {
                        rVar = true;
                    }
                } catch (SQLException sql) {
                    sql.printStackTrace();
                } finally {
                    CloseObject.closeObject(pstm);
                    CloseObject.closeObject(rs);
                }
            } else {
                arrResultado.add("ERROR");
                arrResultado.add("");
                arrResultado.add("");
            }
        } catch (SQLException sql) {
            sql.printStackTrace();
        } finally {
            CloseObject.closeObject(pstm1);
            CloseObject.closeObject(rs1);
        }
        return arrResultado;
    }

    /**
     * Validacion de EPs plurianuales. Ejecuta el SP
     * fn_valida_adecuacion_plurianuales. <br>
     * NOTA: Previamente se debio validar que la adecuacion sea de oficinas
     * centrales, de otra manera el resultado que regrese este procedimiento
     * puede sobreescribir al de error.
     *
     * @param conn
     *            Conexion activa a la base de datos
     * @param folioAdecuacion
     *            Folio de la adecuacion
     * @return Lista de advertencias con aquellas EPs plurianuales en la
     *         adecuacion.
     * @throws Exception
     */
    public static ArrayList<String> validaPartidasPlurianuales(Connection conn, String folioAdecuacion) throws Exception {
        CallableStatement cstmt = null;
        ResultSet rs = null;
        ArrayList<String> mensajes = new ArrayList<String>();
        try {
            cstmt = conn.prepareCall("{call fn_valida_adecuacion_plurianuales( ? ) }");
            cstmt.setString(1, folioAdecuacion);
            rs = cstmt.executeQuery();
            while (rs.next()) {
                mensajes.add(rs.getString(1));
            }
            return mensajes;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cstmt, false);
        }
    }

    /**
     * Validacion de EPs con METAS fn_valida_adecuacion_metas. <br>
     * NOTA: Previamente se debio validar que la adecuacion sea de oficinas
     * centrales, de otra manera el resultado que regrese este procedimiento
     * puede sobreescribir al de error.
     *
     * @param conn
     *            Conexion activa a la base de datos
     * @param folioAdecuacion
     *            Folio de la adecuacion
     * @return Lista de advertencias con aquellas EPs con METAS en la
     *         adecuacion.
     * @throws Exception
     */
    public static ArrayList<String> validaEPMetas(Connection conn, String folioAdecuacion) throws Exception {
        CallableStatement cstmt = null;
        ResultSet rs = null;
        ArrayList<String> mensajes = new ArrayList<String>();
        try {
            cstmt = conn.prepareCall("{call fn_valida_adecuacion_metas( ? ) }");
            cstmt.setString(1, folioAdecuacion);
            rs = cstmt.executeQuery();
            while (rs.next()) {
                mensajes.add(rs.getString(1));
            }
            return mensajes;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cstmt, false);
        }
    }

    // TODO Leer de la base de datos la info.
    public static List<ProgramaPresupuestario> leeProgramaMetas(Connection conn) {
        return new ArrayList<ProgramaPresupuestario>(Arrays.asList(new ProgramaPresupuestario("S219", "Apoyos para el Desarrollo Forestal Sustentable"), new ProgramaPresupuestario("E014", "Protección Forestal")));
    }

    public static ArrayList<ArrayList<String>> consultaActividadInstitucional(Connection conn, int nFolio) throws Exception {
        ArrayList<ArrayList<String>> arrActividadInstitucional = new ArrayList<>();
        CallableStatement cs = null;
        ResultSet rs = null;
        String GR = "";
        String FN = "";
        String SF = "";
        String AI = "";
        String dActividadInstitucional = "";
        String cQuery = "{call sp_META_AI (" + nFolio + ") }";
        cs = conn.prepareCall(cQuery);
        try {
            rs = cs.executeQuery();
            while (rs.next()) {
                ArrayList<String> arrAI = new ArrayList<>();
                GR = rs.getString(2);
                arrAI.add(GR);
                FN = rs.getString(3);
                arrAI.add(FN);
                SF = rs.getString(4);
                arrAI.add(SF);
                AI = rs.getString(5);
                arrAI.add(AI);
                dActividadInstitucional = rs.getString(6);
                arrAI.add(dActividadInstitucional);
                arrActividadInstitucional.add(arrAI);
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
        return arrActividadInstitucional;
    }

    public static ArrayList<ArrayList<String>> consultaActividadInstitucionalIADE(Connection conn, int nFolio) throws Exception {
        ArrayList<ArrayList<String>> arrActividadInstitucional = new ArrayList<>();
        CallableStatement cs = null;
        ResultSet rs = null;
        String GR = "";
        String FN = "";
        String SF = "";
        String AI = "";
        String dActividadInstitucional = "";
        String cQuery = "{call sp_META_AI (" + nFolio + ") }";
        cs = conn.prepareCall(cQuery);
        try {
            rs = cs.executeQuery();
            while (rs.next()) {
                ArrayList<String> arrAI = new ArrayList<>();
                GR = rs.getString(2);
                arrAI.add(GR);
                FN = rs.getString(3);
                arrAI.add(FN);
                SF = rs.getString(4);
                arrAI.add(SF);
                AI = rs.getString(5);
                arrAI.add(AI);
                dActividadInstitucional = rs.getString(6);
                arrAI.add(dActividadInstitucional);
                arrActividadInstitucional.add(arrAI);
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
        return arrActividadInstitucional;
    }

    public static boolean esApartadoAplicado(Connection conn, int nIdCaso) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM tAdecuacionEncabezado WITH(NOLOCK) WHERE cDocumentoHAplicado = 'S' AND nFolioAdecuacion = ?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, nIdCaso);
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1) > 0;
            return false;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static void esReserva(Connection conn, int nFolio, String adecuacionReserva) throws SQLException {
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("UPDATE tADECUACIONEncabezado set esReserva = ? where nFolioAdecuacion = ?");
            pstm.setString(1, adecuacionReserva);
            pstm.setInt(2, nFolio);
            pstm.executeUpdate();
        } finally {
            CloseObject.closeObject(pstm);
        }
    }

    public static String esAdecuacionReserva(Connection conn, int nIdCaso) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT ISNULL(esReserva,'NO') AS esReserva FROM tAdecuacionEncabezado WITH(NOLOCK) WHERE nFolioAdecuacion = ?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, nIdCaso);
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getString(1);
            return "NO";
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static String justificacionAmpIADE(Connection conn, int folio) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT ISNULL(cJustificacionAmp,'') AS cJustificacionAmp FROM tConsolidacionEncabezado WHERE nFolioCONSOLIDACION = ?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, folio);
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getString(1);
            return "";
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static String justificacionRedIADE(Connection conn, int folio) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT ISNULL(cJustificacionRed,'') AS cJustificacionRed FROM tConsolidacionEncabezado WHERE nFolioCONSOLIDACION = ?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, folio);
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getString(1);
            return "";
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }
}
