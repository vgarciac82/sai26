package com.syc.contable.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jfree.util.Log;

import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;

public class AnteProyectoManager {

	public static  boolean creaHederAnteProyecto(Caso c, Usuario usuario, Connection conn, int nFolio, String aEjercicioFiscal, String aEjercicio, String nCuenta, int nPorcentajeAmpliacion, int nPorcentajeReduccion, String cAutorizado, String cDescripcion, String cUnidadEjecutora, String cCampo, String cUnidadNormativa) throws SQLException {
		boolean retVar=false;
		PreparedStatement pstm = null;
		PreparedStatement pstms = null;
		ResultSet rs = null;
		int iExiste=0;
		int id_caso=0;
		id_caso=c.getIdCaso();
		String cQueryExiste="Select count(*) from tAnteProyectoEncabezado with (nolock) where nFolioAnteProyecto = ? AND aEjercicioFiscal = ? AND cUnidadResponsable = ? ";
		//String cQueryExiste="Select count(*) from tAnteProyectoEncabezado with (nolock) where nFolioAnteProyecto = ? AND aEjercicioFiscal = ? ";
		String cQueryCrea="INSERT INTO tAnteProyectoEncabezado (nFolioAnteProyecto ,aEjercicioFiscal, cUnidadResponsable " +
		" ,fCarga, nFolioAnteProyectoAnterior, aEjercicioFiscalAnterior, nCuenta, nIncremento " +
		" ,nDecremento, cCampoCalculo, cAutorizado, cDescripcion, cUnidadNormativa, id_caso) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,? )" ;

		try{
			pstms = conn.prepareStatement(cQueryExiste);
			pstms.setInt(1, nFolio);
			pstms.setString(2, aEjercicioFiscal);
			pstms.setString(3, cUnidadEjecutora);
			rs = pstms.executeQuery();
			if (rs.next()) {
				iExiste=rs.getInt(1);
			}
			
			if (iExiste==0){
				pstm = conn.prepareStatement(cQueryCrea);
				pstm.setInt(1, nFolio);
				pstm.setString(2, aEjercicioFiscal);
				pstm.setString(3, cUnidadEjecutora);
				pstm.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
				pstm.setInt(5, nFolio);
				pstm.setString(6, aEjercicio);
				pstm.setString(7, nCuenta);
				pstm.setInt(8, nPorcentajeAmpliacion);
				pstm.setInt(9, nPorcentajeReduccion);
				pstm.setString(10, cCampo);
				pstm.setString(11, cAutorizado);
				pstm.setString(12, cDescripcion);
				pstm.setString(13, cUnidadNormativa);
				pstm.setInt(14, id_caso);
				retVar = pstm.execute();
			}
			
		}catch(SQLException s){
			s.printStackTrace();
		}
		finally{
			if (pstm != null)
				pstm.close();
			pstm = null;
			if (pstms != null)
				pstms.close();
			pstms = null;
			if (rs != null)
				rs.close();
			rs = null;
		}
		return retVar;
	}
	
	public static boolean creaDetalleAnteProyecto(Connection conn, int nIdCaso, String nCuenta, int nIncremento, int nDecremento, String cUnidadResponsable, String cUnidadNormativa, int nFolioAnteProyecto ) throws SQLException {
		
		boolean retVar= false;
		PreparedStatement pstm = null;
		PreparedStatement pstmd = null;
		ResultSet rs = null;
		String cQueryCrea="";
		String cQueryBusca="";
		int aEjercicioFiscal = new Integer( AdecuacionManager.obtenEjercicioFiscal(conn)).intValue();
		int aEjercicioFiscalAnteProy = aEjercicioFiscal + 1;
		int nConsecutivo=0;
		try{
			pstm = conn.prepareStatement("set ANSI_NULLS off ");
			retVar = pstm.execute();
			cQueryCrea="INSERT INTO tAnteProyectoDetalle (nConsecutivo,nFolioAnteProyecto,aEjercicioFiscal,cUnidadResponsable,cClaveSiaff,cClaveInterna,mCalculado,mOptimo,mIreductible,nPorcentajeReduccion,nPorcentajeIncremento)";
			if (!"".equals(nCuenta)){
				
				cQueryCrea="INSERT INTO tAnteProyectoDetalle (nConsecutivo,nFolioAnteProyecto,aEjercicioFiscal,cUnidadResponsable,cClaveSiaff,cClaveInterna,mCalculado,mOptimo,mIreductible,nPorcentajeReduccion,nPorcentajeIncremento) "
					 + " SELECT ROW_NUMBER() OVER(order by e.cUnidadEjecutora desc)  AS nDocREnglon,"+nIdCaso+" as nFolio, (SELECT e.aEjercicioFiscal+1 FROM tEjercicioFiscal e with (nolock) WHERE cActivo = 1 ) as cEjercicioFiscal, e.cUnidadEjecutora as cUnidadResponsable "
					 + "	  , replace(e.ClaveSIAFF,'"+aEjercicioFiscal+"','"+aEjercicioFiscalAnteProy+"') as ClaveSIAFF, e.ClaveInterna ";
				if (0 != nIncremento ) {
					cQueryCrea+="     , sum(mSaldoArrastre) + sum((mSaldoArrastre) * ("+ nIncremento+"/100.00)) as mCalculado ";
				}else if (0 != nIncremento ) {
					cQueryCrea+="     , sum(mSaldoArrastre) - sum((mSaldoArrastre) * ("+ nDecremento+"/100.00)) as mCalculado  ";
				}else{
					cQueryCrea+="     , sum(mSaldoArrastre)  as mCalculado  ";
				}
				cQueryCrea+="	      , 0 as mOptimo, 0 as mIreductible, 0 as nPorcentajeReduccion, 0 as nPorcentajeIncremento "
					 + "		   FROM tSaldos s with (nolock)       , tCatalogoEPAnteProy e with (nolock)  "
					 + "		   WHERE s.cSubCuenta = REPLACE(e.EP,'"+aEjercicioFiscalAnteProy+"','"+aEjercicioFiscal+"')    "
					 + "		     AND s.nCuenta LIKE '"+nCuenta+"-%' "
					 + "		     AND e.cUnidadEjecutora = '"+cUnidadResponsable+"' "
					 + "		     AND e.cUnidadNorativa = '"+cUnidadNormativa+"' "
					 + "       GROUP BY e.cUnidadEjecutora, REPLACE(e.ClaveSIAFF,'"+aEjercicioFiscal+"','"+aEjercicioFiscalAnteProy+"'), e.ClaveInterna ";
				pstm.close();
				pstm = conn.prepareStatement(cQueryCrea);
				retVar = pstm.execute();

				cQueryBusca="SELECT max(nConsecutivo) as nConsecutivo FROM tAnteProyectoDetalle  WITH (NOLOCK) WHERE nFolioAnteProyecto = "+nIdCaso+" AND cUnidadResponsable = '"+cUnidadResponsable+"' ";
				
				pstmd = conn.prepareStatement(cQueryBusca);
				rs = pstmd.executeQuery();
				if (rs.next()) {
					nConsecutivo= rs.getInt(1);
				}

				cQueryCrea="INSERT INTO tAnteProyectoDetalle (nConsecutivo,nFolioAnteProyecto,aEjercicioFiscal,cUnidadResponsable,cClaveSiaff,cClaveInterna,mCalculado,mOptimo,mIreductible,nPorcentajeReduccion,nPorcentajeIncremento) "
					 + " SELECT ROW_NUMBER() OVER(order by e.cUnidadEjecutora desc)+ "+nConsecutivo+"  AS nDocREnglon,"+nIdCaso+"  as nFolio, (SELECT e.aEjercicioFiscal+1 FROM tEjercicioFiscal e with (nolock) WHERE cActivo = 1 ) as cEjercicioFiscal, e.cUnidadEjecutora as cUnidadResponsable "
					 + "      , e.ClaveSIAFF, e.ClaveInterna, 0 as mCalculado, 0 as mOptimo, 0 as mIreductible, 0 as nPorcentajeReduccion, 0 as nPorcentajeIncremento  	"
					 + "  FROM tCatalogoEPAnteProy e WITH (NOLOCK) "
					 + " WHERE e.ep not in (select e.ep from tCatalogoEPAnteProy e with (nolock) "
					 + "     , tAnteProyectoDetalle d with (nolock) WHERE e.ClaveSIAFF = d.cClaveSiaff "
					 + "    AND e.ClaveInterna = d.cClaveInterna AND d.nFolioAnteProyecto = "+nIdCaso+ " "
					 + "    AND d.cUnidadResponsable = e.cUnidadEjecutora "
					 + "	AND e.cUnidadEjecutora = '"+cUnidadResponsable+"'  AND e.cUnidadNorativa = '"+cUnidadNormativa+"' )"
					 + "    AND e.cUnidadEjecutora = '"+cUnidadResponsable+"'  AND e.cUnidadNorativa = '" + cUnidadNormativa + "' ";
				
				pstm = conn.prepareStatement(cQueryCrea);
				retVar = pstm.execute();
				
			}else {
				cQueryCrea+=" select nConsecutivo,"+nIdCaso+" as nFolioAnteProyecto, aEjercicioFiscal, cUnidadResponsable, cClaveSiaff, cClaveInterna ";
				if (0 != nDecremento ){
					cQueryCrea+="     , sum(mCalculado) - (sum(mCalculado) *  (" + nDecremento + "/100.00)) as mCalculado ";
				}
				else if (0 != nIncremento){
					cQueryCrea+="     , sum(mCalculado) + (sum(mCalculado) *  (" + nIncremento + "/100.00)) as mCalculado ";
				}
				else{
					cQueryCrea+="	 , sum(mCalculado) as mCalculado ";
				}
				cQueryCrea+="     , sum(mOptimo) as mOptimo ";
				cQueryCrea+="	 , sum(mIreductible) as mIreductible, nPorcentajeReduccion, nPorcentajeIncremento ";
				cQueryCrea+="  from tAnteProyectoDetalle d with (nolock)" ;
				cQueryCrea+=" where d.nFolioAnteProyecto = "+ nFolioAnteProyecto + " ";
				cQueryCrea+=" group by nConsecutivo, aEjercicioFiscal, cUnidadResponsable, cClaveSiaff, cClaveInterna, nPorcentajeReduccion, nPorcentajeIncremento ";
				pstm = conn.prepareStatement(cQueryCrea);
				retVar = pstm.execute();
			}

			
		}finally{
			if (pstm != null)
				pstm.close();
			if (pstmd != null)
				pstmd.close();
			if (rs != null)
				rs.close();
			rs = null;
			pstmd = null;
			pstm = null;
		}
		return retVar;
	}

	public static ArrayList<String> obtenTechoUN (Connection conn, String cUnidadResponsable, String cRoolUsuario) throws SQLException {
		ArrayList arrmObtenDatos = new ArrayList<>();
		PreparedStatement pstm = null;
		String uRTecho="";
		String uRDesc="";
		ResultSet rs = null;
		String sQuery="";
		String monto="";
		int i=0;
		try{
			sQuery="SELECT cUnidadNormativa, u.D_DESCRIPCION, mTechoMonto  FROM tTechoUNormativa t with (nolock), tCatUnidadResponsable u with (nolock) WHERE t.cUnidadNormativa = u.cUnidadResponsable AND u.nAlcance = 1 ";
			//if (!"".equals(cRoolUsuario) )
			//	sQuery+="AND u.cUnidadResponsable = ? ";
			pstm = conn.prepareStatement(sQuery);
			//pstm.setString(1, cUnidadResponsable);
			rs = pstm.executeQuery();
			while (rs.next()) {
				ArrayList<String> arrmODatos = new ArrayList<String>();
				uRTecho = rs.getString(1);
				uRDesc = rs.getString(2);
				monto = String.valueOf(new DecimalFormat("###,###.##").format(rs.getDouble(3)));
				//mTechoUN =  rs.getDouble(3);
				arrmODatos.add(uRTecho);
				arrmODatos.add(uRDesc);
				arrmODatos.add(monto);
				arrmObtenDatos.add(arrmODatos);
				arrmODatos=null;
				i++;
			}
		}finally{
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		if (i== 0){
			return null;
		}else{
			return arrmObtenDatos;
		}
	}
	
	public static ArrayList<String> obtenTechoEF (Connection conn, String cRoolUsuario) throws SQLException {
		ArrayList arrmObtenDatos = new ArrayList<>();
		PreparedStatement pstm = null;
		String eFTecho="";
		String eFDesc="";
		ResultSet rs = null;
		String sQuery="";
		String monto="";
		try{
			sQuery="SELECT t.cEntidadFederativa as txtGridEntFederalC, e.dEntidadFederativa as txtGridEntFederalCabr, mMontoTecho as mMontoTopeEntidad FROM tTechoEntidadFederativa t with (nolock), tCatalogoEntidadFederativa e with (nolock) WHERE t.cEntidadFederativa =  e.cEntidadFederativa";

			pstm = conn.prepareStatement(sQuery);
			
			rs = pstm.executeQuery();
			while (rs.next()) {
				ArrayList<Object> arrmODatos = new ArrayList<>();
				eFTecho = rs.getString(1);
				eFDesc = rs.getString(2);
				monto = String.valueOf(new DecimalFormat("###,###.##").format(rs.getDouble(3)));
				//mTechoEF =  rs.getDouble(3);
				arrmODatos.add(eFTecho);
				arrmODatos.add(eFDesc);
				arrmODatos.add(monto);
				arrmObtenDatos.add(arrmODatos);
				arrmODatos=null;
			}
		}finally{
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}			
		}
		return arrmObtenDatos;
	}

	public static ArrayList<String> obtenTechoUR (Connection conn, String cUnidadResponsable, String cRoolUsuario)  throws SQLException {
		ArrayList arrmObtenDatos = new ArrayList<>();
		PreparedStatement pstm = null;
		String uRTecho="";
		String uRDesc="";
		ResultSet rs = null;
		String sQuery="";
		String monto="";
		try{
			sQuery="SELECT u.cUnidadResponsable, u.D_DESCRIPCION, mTechoMonto FROM tTechoUEjecutora e with (nolock), tCatUnidadResponsable u with (nolock) WHERE e.cUnidadResponsable = u.cUnidadResponsable ";
			pstm = conn.prepareStatement(sQuery);
			
			rs = pstm.executeQuery();
			while (rs.next()) {
				ArrayList<Object> arrmODatos = new ArrayList<>();
				uRTecho = rs.getString(1);
				uRDesc = rs.getString(2);
				monto = String.valueOf(new DecimalFormat("###,###.##").format(rs.getDouble(3)));
				//mTechoUN =  rs.getDouble(3);
				arrmODatos.add(uRTecho);
				arrmODatos.add(uRDesc);
				arrmODatos.add(monto);
				arrmObtenDatos.add(arrmODatos);
				arrmODatos=null;
			}

		}finally{
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
			
		}
		return arrmObtenDatos;
	}

	public static ArrayList<String> obtenTechoPP (Connection conn, String cRoolUsuario)  throws SQLException {
		ArrayList arrmObtenDatos = new ArrayList<>();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String pPTecho="";
		String pPDesc="";
		String sQuery="";
		String monto="";
		try{
			sQuery=" SELECT p.cProgramaPresupuestario as txtGridtProgPresupC, p.dProgramaPresupuestario as txtGridtProgPresupD, mTechoMonto as mMontoTopeProgPres FROM tTechoProgramaPresupuestario t with (nolock), tCatalogoProgramaPresupuestario p with (nolock) WHERE t.cProgramaPresupuestario = p.cProgramaPresupuestario ";
			pstm = conn.prepareStatement(sQuery);
			rs = pstm.executeQuery();
			while (rs.next()) {
				ArrayList<Object> arrmODatos = new ArrayList<>();
				pPTecho = rs.getString(1);
				pPDesc = rs.getString(2);
				monto = String.valueOf(new DecimalFormat("###,###.##").format(rs.getDouble(3)));
				//mTechoPP =  rs.getDouble(3);
				arrmODatos.add(pPTecho);
				arrmODatos.add(pPDesc);
				arrmODatos.add(monto);
				arrmObtenDatos.add(arrmODatos);
				arrmODatos=null;
			}
		}finally{
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return arrmObtenDatos;
	}

	public static ArrayList<String> obtenTechoUEPP (Connection conn, String cRoolUsuario, String  cUnidadResponsable)  throws SQLException {
		ArrayList arrmObtenDatos = new ArrayList<>();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String pPTecho="";
		String pPDesc="";
		String pPTechoUE="";
		String pPDescUE="";
		String sQuery="";
		String monto="";
		try{
			//sQuery=" SELECT p.cProgramaPresupuestario as txtGridtProgPresupC, p.dProgramaPresupuestario as txtGridtProgPresupD, mTechoMonto as mMontoTopeProgPres FROM tTechoProgramaPresupuestario t with (nolock), tCatalogoProgramaPresupuestario p with (nolock) WHERE t.cProgramaPresupuestario = p.cProgramaPresupuestario ";
			sQuery="SELECT p.cProgramaPresupuestario as txtGridtProgPresupC " +
					"    , p.dProgramaPresupuestario as txtGridtProgPresupD " +
					"    , u.cUnidadResponsable " +
					"    , u.D_DESCRIPCION " +
					"    , mTechoMonto as mMontoTopeProgPres " + 
					" FROM tTechoProgPresupUniEjec t with (nolock) " +
					"    , tCatalogoProgramaPresupuestario p with (nolock) " + 
					"    , tCatUnidadResponsable u with (nolock)  " +
					"WHERE t.cProgramaPresupuestario = p.cProgramaPresupuestario " + 
					"  AND t.cUnidadResponsable      = u.cUnidadResponsable";
			pstm = conn.prepareStatement(sQuery);
			rs = pstm.executeQuery();
			while (rs.next()) {
				ArrayList<Object> arrmODatos = new ArrayList<>();
				pPTecho = rs.getString(1);
				pPDesc = rs.getString(2);
				pPTechoUE = rs.getString(3);
				pPDescUE = rs.getString(4);
				monto = String.valueOf(new DecimalFormat("###,###.##").format(rs.getDouble(5)));
				//mTechoPP =  rs.getDouble(3);
				arrmODatos.add(pPTecho);
				arrmODatos.add(pPDesc);
				arrmODatos.add(pPTechoUE);
				arrmODatos.add(pPDescUE);
				arrmODatos.add(monto);
				arrmObtenDatos.add(arrmODatos);
				arrmODatos=null;
			}
		}finally{
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return arrmObtenDatos;
	}

	public static ArrayList<String> obtenTechoPA (Connection conn, String cUnidadResponsable, String cRoolUsuario) throws SQLException {
		ArrayList arrmObtenDatos = new ArrayList<>();
		PreparedStatement pstm = null;
		String pPTecho="";
		String pPDesc="";
		String pPTechoTG="";
		String pPDescTG="";
		ResultSet rs = null;
		String sQuery="";
		String monto="";
		try{
			sQuery=" SELECT p.cPartida as txtGridOGTOC, p.dPartida as txtGridOGTOD, g.cTipoGasto as txtGridCTG, g.dTipoGasto as txtGridDTG, mTechoPartida as mTechoPartida FROM tTechosPartida t with (nolock), tCatalogoPartida p with (nolock), tCatalogoTipoGasto g with (nolock) WHERE t.cTipoGasto = g.cTipoGasto AND t.cPartida = p.cPartida ";
			pstm = conn.prepareStatement(sQuery);
			rs = pstm.executeQuery();
			while (rs.next()) {
				ArrayList<Object> arrmODatos = new ArrayList<>();
				pPTecho = rs.getString(1);
				pPDesc = rs.getString(2);
				pPTechoTG = rs.getString(3);
				pPDescTG = rs.getString(4);
				monto = String.valueOf(new DecimalFormat("###,###.##").format(rs.getDouble(5)));
				//mTechoPP =  rs.getDouble(5);
				arrmODatos.add(pPTecho);
				arrmODatos.add(pPDesc);
				arrmODatos.add(pPTechoTG);
				arrmODatos.add(pPDescTG);
				arrmODatos.add(monto);
				arrmObtenDatos.add(arrmODatos);
				arrmODatos=null;
			}
		}finally{
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return arrmObtenDatos;
	}

	public static ArrayList<String> obtenTechoPAUE (Connection conn, String cUnidadResponsable, String cRoolUsuario) throws SQLException {
		ArrayList arrmObtenDatos = new ArrayList<>();
		PreparedStatement pstm = null;
		String pPTecho="";
		String pPDesc="";
		String pPTechoTG="";
		String pPDescTG="";
		String pPTechoUE="";
		String pPDescUE="";
		ResultSet rs = null;
		String sQuery="";
		String monto="";
		try{
			//sQuery=" SELECT p.cPartida as txtGridOGTOC, p.dPartida as txtGridOGTOD, g.cTipoGasto as txtGridCTG, g.dTipoGasto as txtGridDTG, mTechoPartida as mTechoPartida FROM tTechosPartida t with (nolock), tCatalogoPartida p with (nolock), tCatalogoTipoGasto g with (nolock) WHERE t.cTipoGasto = g.cTipoGasto AND t.cPartida = p.cPartida ";
			sQuery = " SELECT p.cPartida as txtGridOGTOC " +
					"	    , p.dPartida as txtGridOGTOD " +
					"	    , g.cTipoGasto as txtGridCTG " +
					"	    , g.dTipoGasto as txtGridDTG " +
					"	    , t.cUnidadResponsable  " +
					"	    , u.D_DESCRIPCION " +
					"	    , mTechoPartida as mTechoPartida " + 
					"    FROM tTechosPartidaUniEjec t with (nolock) " +
					"	    , tCatalogoPartida p with (nolock) " +
					"	    , tCatalogoTipoGasto g with (nolock)  " +
					"	    , tCatUnidadResponsable u with (nolock)  " +
					"    WHERE t.cTipoGasto = g.cTipoGasto  " +
					"	   AND t.cPartida = p.cPartida  " +
					"	   AND t.cUnidadResponsable = u.cUnidadResponsable";
			pstm = conn.prepareStatement(sQuery);
			rs = pstm.executeQuery();
			while (rs.next()) {
				ArrayList<Object> arrmODatos = new ArrayList<>();
				pPTecho = rs.getString(1);
				pPDesc = rs.getString(2);
				pPTechoTG = rs.getString(3);
				pPDescTG = rs.getString(4);
				pPTechoUE = rs.getString(5);
				pPDescUE = rs.getString(6);
				monto = String.valueOf(new DecimalFormat("###,###.##").format(rs.getDouble(7)));
				//mTechoPP =  rs.getDouble(5);
				arrmODatos.add(pPTecho);
				arrmODatos.add(pPDesc);
				arrmODatos.add(pPTechoTG);
				arrmODatos.add(pPDescTG);
				arrmODatos.add(pPTechoUE);
				arrmODatos.add(pPDescUE);
				arrmODatos.add(monto);
				arrmObtenDatos.add(arrmODatos);
				arrmODatos=null;
			}
		}finally{
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return arrmObtenDatos;
	}
	
	public static ArrayList<ArrayList<String>> obtenAnteProyecto(Connection conn, String cUnidadResponsable, int nFolio, String cEjercicioFiscal, Caso c) throws SQLException {
		ArrayList arrmObtenDatos = new ArrayList<>();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sQuery="";
		int nConsec=0;
		String cClaveSiaff="";
		String cCalculado="";
		Double mCalculado =0.0;
		String cOptimo="";
		Double mOptimo =0.0;
		String cIreductible="";
		Double mIreductible =0.0;
		String cPorcentajeReduccion ="";
		String cPorcentajeIncremento ="";
		String cMotivoRechazo="";
		int id_caso=0;
		int iReduccion=0;
		int iIncremento=0;
		
		try{
			id_caso=c.getIdCaso();
			sQuery=" SELECT d.nConsecutivo, d.aEjercicioFiscal, d.cClaveSiaff + '.'+ d.cClaveInterna as Ep, d.mCalculado, d.mOptimo "+
				"	, d.mIreductible, d.nPorcentajeReduccion, d.nPorcentajeIncremento, isnull(d.cMotivoRechazo,'') " +
				"  from tAnteProyectoDetalle d with (nolock), " +
				"       tAnteproyectoEncabezado a with (nolock)"+
				" WHERE d.nFolioAnteproyecto = a.nFolioAnteProyecto "+
				"   AND d.aEjercicioFiscal   = a.aEjercicioFiscal+1 "+
				"   AND d.cUnidadResponsable = a.cUnidadResponsable " +
				"   AND a.nFolioAnteProyecto = " + nFolio  + " "+
				"   AND a.id_caso = "+id_caso + " ";
				//+"   AND cUnidadResponsable = '"+ cUnidadResponsable +"'";
				//"   AND aEjercicioFiscal = '"+cEjercicioFiscal+"'";
			pstm = conn.prepareStatement(sQuery);
			rs = pstm.executeQuery();
			while (rs.next()) {
				ArrayList<Object> arrmODatos = new ArrayList<>();
				nConsec = rs.getInt(1);
				arrmODatos.add(nConsec);
				cEjercicioFiscal = rs.getString(2);
				arrmODatos.add(cEjercicioFiscal);
				cClaveSiaff = rs.getString(3);
				arrmODatos.add(cClaveSiaff);
				cCalculado = String.valueOf(new DecimalFormat("###,###.##").format(rs.getDouble(4)));
				mCalculado =  rs.getDouble(4);
				arrmODatos.add(cCalculado);
				cOptimo = String.valueOf(new DecimalFormat("###,###.##").format(rs.getDouble(5)));
				mOptimo =  rs.getDouble(5);
				arrmODatos.add(cOptimo);
				cIreductible = String.valueOf(new DecimalFormat("###,###.##").format(rs.getDouble(6)));
				mIreductible =  rs.getDouble(6);
				arrmODatos.add(cIreductible);
				cPorcentajeReduccion = rs.getString(7);
				iReduccion= new Integer(cPorcentajeReduccion).intValue(); 
				arrmODatos.add(cPorcentajeReduccion);
				cPorcentajeIncremento = rs.getString(8);
				iIncremento= new Integer(cPorcentajeIncremento).intValue(); 
				arrmODatos.add(cPorcentajeIncremento);
				cMotivoRechazo=rs.getString(9);
				arrmODatos.add(cMotivoRechazo);
				arrmObtenDatos.add(arrmODatos);
				arrmODatos=null;
			}
		}finally{
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return arrmObtenDatos;
	}

	public static ArrayList<String> ValidaDetalleAnte(Connection conn, String cUnidadResponsable, int nFolio, int nConsecutivo, String cEP, Double mCalculado, Double mOptimo, Double mIreductible, int iReduccion, int iIncremento, String cEjercicioFiscal, Caso c, boolean bCRUD  )  throws SQLException {
		ArrayList<String> arrmObtenDatos = new ArrayList<String>();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		PreparedStatement pstmd = null;
		ResultSet rsd = null;
		int nConsec=0;
		String cSIAFF="";
		String cInterna="";
		String cUnidadEjecutora="";
		String sQuery="SELECT count(*) FROM tAnteProyectoDetalle d with (nolock), tAnteProyectoEncabezado a with (nolock), tCatalogoEPAnteProy e  with (nolock) " 
			+"	WHERE d.nFolioAnteproyecto = a.nFolioAnteProyecto AND d.aEjercicioFiscal   = a.aEjercicioFiscal+1 "   
			+"	  AND d.cUnidadResponsable = a.cUnidadResponsable  AND d.cClaveSiaff = e.ClaveSiaff "
			+"	  AND d.cClaveInterna = e.ClaveInterna AND e.ep = '"+cEP.trim()+"' AND a.nFolioAnteProyecto = " + nFolio+ " and a.id_caso= "+c.getIdCaso()+" ";
		try{
			cUnidadEjecutora= c.getCasoDato("MONEDA").getValor();
			pstm = conn.prepareStatement(sQuery);
			rs = pstm.executeQuery();
			if (rs.next()){
				nConsec = rs.getInt(1);				
			}
			sQuery=" SELECT ClaveSIAFF, ClaveInterna FROM tCatalogoEPAnteProy with (nolock) WHERE EP = rtrim(ltrim(?)) ";
			
			pstmd=conn.prepareStatement(sQuery);
			pstmd.setString(1, cEP);
			rsd = pstmd.executeQuery();
			if (rsd.next()){
				cSIAFF = rsd.getString(1);				
				cInterna = rsd.getString(2);
			}
			if (nConsec > 0 ){
				if (!"".equals(cSIAFF)){
					if (bCRUD){
						updateDetalleAnteProyecto(conn, nFolio, nConsecutivo, cSIAFF,cInterna, mCalculado, mOptimo, mIreductible, iReduccion, iIncremento, cUnidadEjecutora, cEjercicioFiscal);
					}
				}else{
					arrmObtenDatos.add("ERROR: La Clave EP no es Valida o no Existe para este Ejercicio Fiscal Consulte Con Gerencia de Presupuesto.");
				}
			}else{
				if (!"".equals(cSIAFF)){
					if (bCRUD){
						insertaDetalleAnteProyecto(conn, nFolio, nConsecutivo, cSIAFF,cInterna, mCalculado, mOptimo, mIreductible, iReduccion, iIncremento, cUnidadEjecutora, cEjercicioFiscal);
					}
				}else{
					arrmObtenDatos.add("ERROR: La Clave EP no es Valida o no Existe para este Ejercicio Fiscal Consulte Con Gerencia de Presupuesto.");
				}
			}
			
		}finally{
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (pstmd != null) {
				pstmd.close();
			}
			if (rsd != null) {
				rsd.close();
			}
			
		}
	
		return arrmObtenDatos;
	}

	public static int insertaDetalleAnteProyecto(Connection conn, int nFolio, int nConsecutivo, String cSIAFF, String cInterna, Double mCalculado, Double mOptimo, Double mIreductible, int iReduccion, int iIncremento, String cUnidadResponsable, String cEjercicioFiscal) throws SQLException{
		int iReturn=0;
		//boolean retVar;
		PreparedStatement pstm = null;
		PreparedStatement pstms = null;
		ResultSet rs = null;
		String clSIAFF="";
		String cQueryEP="select top (1)  e.ClaveSIAFF, e.ClaveInterna from tCatalogoEPAnteProy e with (nolock) where e.EP = '"+cSIAFF.trim()+"'" ;
		String cQueryCrea="INSERT INTO tAnteProyectoDetalle (nConsecutivo,nFolioAnteProyecto,aEjercicioFiscal,cUnidadResponsable,cClaveSiaff,cClaveInterna,mCalculado,mOptimo,mIreductible,nPorcentajeReduccion,nPorcentajeIncremento)";
		cQueryCrea+= " VALUES ( ?,?,?,?,?,?,?,?,?,?,?)";
		try{
			if ("".equals(cInterna)){
				pstms = conn.prepareStatement(cQueryEP);
				rs = pstms.executeQuery();
				if (rs.next()){
					clSIAFF = rs.getString(1);
					cInterna = rs.getString(2);
				}else{
					cInterna = cSIAFF.substring(56,63);
					clSIAFF = cSIAFF.substring(0, 55);
				}
			}
			Log.debug(cQueryCrea);
			Log.debug(nConsecutivo+','+nFolio+','+cEjercicioFiscal+','+cUnidadResponsable+','+ cSIAFF+','+ cInterna+','+mCalculado +','+ mOptimo+','+ mIreductible+','+iReduccion+','+iIncremento);
			pstm = conn.prepareStatement(cQueryCrea);
			pstm.setInt(1, nConsecutivo);
			pstm.setInt(2, nFolio);
			pstm.setString(3, cEjercicioFiscal);
			pstm.setString(4, cUnidadResponsable);
			pstm.setString(5, clSIAFF);
			pstm.setString(6, cInterna.trim());
			pstm.setDouble(7, mCalculado);
			pstm.setDouble(8, mOptimo);
			pstm.setDouble(9, mIreductible);
			pstm.setInt(10, iReduccion);
			pstm.setInt(11, iIncremento);
			iReturn = pstm.executeUpdate();
			
		}finally{
			if (pstm != null)
				pstm.close();
			pstm = null;
			if (pstms != null)
				pstms.close();
			pstms = null;
			if (rs != null)
				rs.close();
			rs = null;
		}
		
		return iReturn; 
	}

	public static int updateDetalleAnteProyecto(Connection conn, int nFolio, int nConsecutivo, String cSIAFF, String cInterna, Double mCalculado, Double mOptimo, Double mIreductible, int iReduccion, int iIncremento, String cUnidadResponsable, String cEjercicioFiscal) throws SQLException{
		int iReturn=0;
		PreparedStatement pstm = null;
		String cQueryCrea="UPDATE tAnteProyectoDetalle  SET mOptimo = ?, mIreductible= ?,nPorcentajeReduccion= ?,nPorcentajeIncremento= ?, mCalculado=? ";
		//cQueryCrea+="nFolioAnteProyecto,aEjercicioFiscal,cUnidadResponsable,cClaveSiaff,cClaveInterna,mCalculado,"
		cQueryCrea+= " WHERE  nFolioAnteProyecto = ? AND cClaveSiaff =? AND cClaveInterna =?";
		try{
			Log.debug(cQueryCrea);
			System.out.println(cQueryCrea);
			Log.debug(nConsecutivo+','+nFolio+','+cEjercicioFiscal+','+cUnidadResponsable+','+ cSIAFF+','+ cInterna+','+mCalculado +','+ mOptimo+','+ mIreductible+','+iReduccion+','+iIncremento);
			System.out.println(nConsecutivo+','+nFolio+','+cEjercicioFiscal+','+cUnidadResponsable+','+ cSIAFF+','+ cInterna+','+mCalculado +','+ mOptimo+','+ mIreductible+','+iReduccion+','+iIncremento);
			pstm = conn.prepareStatement(cQueryCrea);
			pstm.setDouble(1, mOptimo);
			pstm.setDouble(2, mIreductible);
			pstm.setInt(3, iReduccion);
			pstm.setInt(4, iIncremento);
			pstm.setDouble(5, mCalculado);
			pstm.setInt(6, nFolio);
			pstm.setString(7, cSIAFF);
			pstm.setString(8, cInterna);
			iReturn = pstm.executeUpdate();
			
		}finally{
			if (pstm != null)
				pstm.close();
			pstm = null;
		}
		
		return iReturn; 
		
	}

	public static String validaTechoPP(Connection conn, int nFolio) throws SQLException{
		String cQueryEF="SELECT e.mMontoTecho - a.mCalculado as mCalculado, e.mMontoTecho - mOptimo AS mOptimo, e.mMontoTecho - mIreductible AS mIreductible, cEntidadFederativa "
			+"  FROM (SELECT sum(d.mCalculado) as mCalculado, sum(d.mOptimo) as mOptimo, sum(d.mIreductible) as mIreductible, t.cEntidadFederativa "
			+"		  FROM tTechoEntidadFederativa t with (nolock)"
			+"		     , tCatalogoEntidadFederativa e with (nolock)" 
			+"			 , tCatalogoEP p with (nolock)"
			+"			 , tAnteProyectoDetalle d with (nolock)" 
			+"		WHERE e.cEntidadFederativa = p.cEntidadFederativa " 
			+"		  AND p.ClaveSIAFF = replace(d.cClaveSiaff,convert(varchar,convert(int, substring(d.cClaveSiaff,1,4))),convert(varchar,convert(int, substring(d.cClaveSiaff,1,4))-1)) " 
			+"		  AND p.ClaveInterna = p.ClaveInterna "
			+"		  AND d.nFolioAnteProyecto = " + nFolio
			+"		GROUP BY t.cEntidadFederativa ) AS a "
			+"	 , tTechoEntidadFederativa e "
			+" WHERE a.cEntidadFederativa = e.cEntidadFederativa";

		String cQueryPP="select p.mTechoMonto - a.mCalculado as mCalculado, p.mTechoMonto - mOptimo as mTechoMonto, p.mTechoMonto - mIreductible as mTechoMonto, cProgramaPresupuestario "
			+"  from (select sum(d.mCalculado) as mCalculado, sum(d.mOptimo) as mOptimo, sum(d.mIreductible) as mIreductible, t.cProgramaPresupuestario  "
			+"		  from tTechoProgramaPresupuestario t with (nolock) "
			+"		     , tCatalogoEP e with (nolock) "
			+"		     , tAnteProyectoDetalle d with (nolock)" 
			+"		  WHERE t.cProgramaPresupuestario = e.cProgramaPresupuestario " 
			+"		   AND e.ClaveInterna = d.cClaveInterna  "
			+"		   AND e.ClaveSIAFF   = replace(d.cClaveSiaff,convert(varchar,convert(int, substring(d.cClaveSiaff,1,4))),convert(varchar,convert(int, substring(d.cClaveSiaff,1,4))-1)) " 
			+"		   and d.nFolioAnteProyecto =  " + nFolio
			+"		 group by t.cProgramaPresupuestario ) as A "
			+"	 , tTechoProgramaPresupuestario p "
			+" where p.cProgramaPresupuestario = a.cProgramaPresupuestario ";

		String cQueryTPA= "select p.mTechoPartida - a.mCalculado as mCalculado, p.mTechoPartida - a.mOptimo as mOptimo, p.mTechoPartida - a.mIreductible as mIreductible, cPartida, cTipoGasto "
			+"  from (select sum(d.mCalculado) as mCalculado, sum(isnull(d.mOptimo,0)) as mOptimo, sum(isnull(d.mIreductible,0)) as mIreductible, t.cPartida, t.cTipoGasto  "
			+"		  from tTechosPartida t  with (nolock)"
			+"		     , tCatalogoEP e  with (nolock)"
			+"			 , tAnteProyectoDetalle d with (nolock)" 
			+"		 WHERE t.cPartida = e.cPartida  "
			+"		   AND t.cTipoGasto = e.cTipoGasto " 
			+"		   AND e.ClaveSIAFF = replace(d.cClaveSiaff,convert(varchar,convert(int, substring(d.cClaveSiaff,1,4))),convert(varchar,convert(int, substring(d.cClaveSiaff,1,4))-1)) " 
			+"		   AND e.ClaveInterna =  d.cClaveInterna  "
			+"		   and d.nFolioAnteProyecto = " + nFolio 
			+"		group by t.cPartida, t.cTipoGasto  ) as A "
			+"	, tTechosPartida p "
			+" WHERE p.cPartida = a.cPartida "
			+"  AND p.cTipoGasto = p.cTipoGasto ";

		String cQueryUE="select u.mTechoMonto - a.mCalculado as mCalculado, u.mTechoMonto - a.mOptimo as mOptimomOptimo , u.mTechoMonto -mIreductible as mIreductible, cUnidadResponsable "
			+"  from (select sum(d.mCalculado) as mCalculado, sum(d.mOptimo) as mOptimo, sum(d.mIreductible) as mIreductible, t.cUnidadResponsable  "
			+"		  from tTechoUEjecutora t  with (nolock)"
			+"		     , tCatUnidadResponsable u with (nolock)" 
			+"			 , tCatalogoEP e  with (nolock)"
			+"			 , tAnteProyectoDetalle d " 
			+"		 WHERE t.cUnidadResponsable = u.cUnidadResponsable " 
			+"		   AND e.cUnidadEjecutora = t.cUnidadResponsable  "
			+"		   AND t.cUnidadResponsable = d.cUnidadResponsable  "
			+"		   AND e.ClaveSIAFF = replace(d.cClaveSiaff,convert(varchar,convert(int, substring(d.cClaveSiaff,1,4))),convert(varchar,convert(int, substring(d.cClaveSiaff,1,4))-1)) " 
			+"		   AND e.ClaveInterna = d.cClaveInterna  "
			+"		  and d.nFolioAnteProyecto = " + nFolio
			+"		 group by t.cUnidadResponsable ) a "
			+"	 , tTechoUEjecutora  u "
			+" WHERE a.cUnidadResponsable = u.cUnidadResponsable ";

		String cQueryUN=" SELECT u.mTechoMonto - a.mCalculado as mCalculado, u.mTechoMonto - a.mOptimo as mOptimo, u.mTechoMonto - a.mIreductible as mIreductible, a.cUnidadNormativa "
			+"   FROM (SELECT sum(d.mCalculado) as mCalculado, sum(d.mOptimo) as mOptimo, sum(d.mIreductible) as mIreductible, t.cUnidadNormativa "
			+" 		  from tTechoUNormativa t  with (nolock)"
			+" 		     , tCatUnidadResponsable u with (nolock)" 
			+" 			 , tCatalogoEP e  with (nolock)"
			+" 			 , tAnteProyectoDetalle d with (nolock) " 
			+" 		 WHERE t.cUnidadNormativa = u.cUnidadResponsable "  
			+" 		   AND e.cUnidadNorativa = t.cUnidadNormativa  "
			+" 		   AND e.ClaveInterna = replace(d.cClaveSiaff,convert(varchar,convert(int, substring(d.cClaveSiaff,1,4))),convert(varchar,convert(int, substring(d.cClaveSiaff,1,4))-1)) " 
			+" 		   AND e.ClaveInterna = d.cClaveInterna  "
			+" 		   AND d.nFolioAnteProyecto = " + nFolio
			+" 		group by t.cUnidadNormativa  ) as A "
			+" 	 , tTechoUNormativa u "
			+"  WHERE a.cUnidadNormativa =  u.cUnidadNormativa ";

		//String cMensajeRegresa="";
		PreparedStatement pstmEF = null;
		ResultSet rsEF = null;
		PreparedStatement pstmPP = null;
		ResultSet rsPP = null;
		PreparedStatement pstmTPA = null;
		ResultSet rsTPA = null;
		PreparedStatement pstmUE = null;
		ResultSet rsUE = null;
		PreparedStatement pstmUN = null;
		ResultSet rsUN = null;

		Double mTechoOptimoEF=0.0;
		//Double mTechoCalaculadoEF=0.0;
		Double mTechoIReductibleEF = 0.0;
		String cEF="";

		Double mTechoOptimoPP=0.0;
		Double mTechoCalaculadoPP=0.0;
		Double mTechoIReductiblePP = 0.0;
		String cPP="";

		Double mTechoOptimoTPA=0.0;
		Double mTechoCalaculadoTPA=0.0;
		Double mTechoIReductibleTPA = 0.0;
		String cTPAPArtida="";
		String cTPATipoGasto="";

		Double mTechoOptimoUE=0.0;
		Double mTechoCalaculadoUE=0.0;
		Double mTechoIReductibleUE = 0.0;
		String cUE="";
		
		Double mTechoOptimoUN=0.0;
		Double mTechoCalaculadoUN=0.0;
		Double mTechoIReductibleUN = 0.0;
		String cUN="";
		
		String cErrorAnteProyecto="";
		//String monto=""; 

		try{
			pstmEF = conn.prepareStatement(cQueryEF);
			rsEF = pstmEF.executeQuery();
			while (rsEF.next()) {
				mTechoCalaculadoUN = rsEF.getDouble(1);
				mTechoOptimoEF =  rsEF.getDouble(2);
				mTechoIReductibleEF =  rsEF.getDouble(3);
				cEF = rsEF.getString(4);
				if (mTechoCalaculadoUN < 0 ){
					cErrorAnteProyecto="Error: Se exede el Techo Presupuestal para la entidad:"+cEF+" en el Apartado Calculado |";
				}
				if (mTechoOptimoEF < 0 ){
					cErrorAnteProyecto="Error: Se exede el Techo Presupuestal para la entidad:"+cEF+" en el Apartado Optimo |";
				}
				if (mTechoIReductibleEF < 0 ){
					cErrorAnteProyecto="Error: Se exede el Techo Presupuestal para la entidad:"+cEF+" en el Apartado Ireductible |";
				}
			}

			pstmPP = conn.prepareStatement(cQueryPP);
			rsPP = pstmPP.executeQuery();
			while (rsPP.next()) {
				mTechoCalaculadoPP =  rsPP.getDouble(1);
				mTechoOptimoPP =  rsPP.getDouble(2);
				mTechoIReductiblePP =  rsPP.getDouble(3);
				cPP = rsPP.getString(4);
				if (mTechoCalaculadoPP < 0 ){
					cErrorAnteProyecto="Error: Se exede el Techo Presupuestal para el Programa Presupuestario :"+cPP+" en el Apartado Calculado |";
				}
				if (mTechoOptimoPP < 0 ){
					cErrorAnteProyecto="Error: Se exede el Techo Presupuestal para el Programa Presupuestario :"+cPP+" en el Apartado Optimo |";
				}
				if (mTechoIReductiblePP < 0 ){
					cErrorAnteProyecto="Error: Se exede el Techo Presupuestal para el Programa Presupuestario :"+cPP+" en el Apartado Ireductible |";
				}
			}
			pstmTPA = conn.prepareStatement(cQueryTPA);
			rsTPA = pstmTPA.executeQuery();
			while (rsTPA.next()) {
				mTechoCalaculadoTPA =  rsTPA.getDouble(1);
				mTechoOptimoTPA =  rsTPA.getDouble(2);
				mTechoIReductibleTPA =  rsTPA.getDouble(3);
				cTPAPArtida = rsTPA.getString(4);
				cTPATipoGasto = rsTPA.getString(5);
				if (mTechoCalaculadoTPA < 0 ){
					cErrorAnteProyecto="Error: Se exede el Techo Presupuestal para la Prtida:"+cTPAPArtida+" Con el Tipo de Gasto:"+cTPATipoGasto+" en el Apartado Calculado |";
				}
				if (mTechoOptimoTPA < 0 ){
					cErrorAnteProyecto="Error: Se exede el Techo Presupuestal para la Prtida:"+cTPAPArtida+" Con el Tipo de Gasto:"+cTPATipoGasto+" en el Apartado Optimo |";
				}
				if (mTechoIReductibleTPA < 0 ){
					cErrorAnteProyecto="Error: Se exede el Techo Presupuestal para la Prtida:"+cTPAPArtida+" Con el Tipo de Gasto:"+cTPATipoGasto+" en el Apartado Ireductible |";
				}
			}
			pstmUE = conn.prepareStatement(cQueryUE);
			rsUE = pstmUE.executeQuery();
			while (rsUE.next()) {
				mTechoCalaculadoUE =  rsUE.getDouble(1);
				mTechoOptimoUE =  rsUE.getDouble(2);
				mTechoIReductibleUE = rsUE.getDouble(3);
				cUE = rsUE.getString(4);
				if (mTechoCalaculadoUE < 0 ){
					cErrorAnteProyecto="Error: Se exede el Techo Presupuestal para la Unidad Ejecutora:"+cUE+" en el Apartado Calculado |";
				}
				if (mTechoOptimoUE < 0 ){
					cErrorAnteProyecto="Error: Se exede el Techo Presupuestal para la Unidad Ejecutora:"+cUE+" en el Apartado Optimo |";
				}
				if (mTechoIReductibleUE < 0 ){
					cErrorAnteProyecto="Error: Se exede el Techo Presupuestal para la Unidad Ejecutora:"+cUE+" en el Apartado Ireductible |";
				}
			}
			pstmUN = conn.prepareStatement(cQueryUN);
			rsUN = pstmUN.executeQuery();
			while (rsUN.next()) {
				mTechoCalaculadoUN =  rsUN.getDouble(1);
				mTechoOptimoUN =  rsUN.getDouble(2);
				mTechoIReductibleUN =  rsUN.getDouble(3);
				cUN = rsUN.getString(4);
				if (mTechoCalaculadoUN < 0 ){
					cErrorAnteProyecto="Error: Se exede el Techo Presupuestal para la Unidad Normativa:"+cUN+" en el Apartado Calculado |";
				}
				if (mTechoOptimoUN < 0 ){
					cErrorAnteProyecto="Error: Se exede el Techo Presupuestal para la Unidad Normativa:"+cUN+" en el Apartado Optimo |";
				}
				if (mTechoIReductibleUN < 0 ){
					cErrorAnteProyecto="Error: Se exede el Techo Presupuestal para la Unidad Normativa:"+cUN+" en el Apartado Ireductible |";
				}
			}
			
		}finally{
			if (pstmEF != null) {
				pstmEF.close();
			}
			if (rsEF != null) {
				rsEF.close();
			}
			if (pstmPP != null) {
				pstmPP.close();
			}
			if (rsPP != null) {
				rsPP.close();
			}
			if (pstmTPA != null) {
				pstmTPA.close();
			}
			if (rsTPA != null) {
				rsTPA.close();
			}
			if (pstmUE != null) {
				pstmUE.close();
			}
			if (rsUE != null) {
				rsUE.close();
			}
			if (pstmUN != null) {
				pstmUN.close();
			}
			if (rsUN != null) {
				rsUN.close();
			}
			
		}
		return cErrorAnteProyecto;
	}

	public static ArrayList<String> VercionesAnteproyecto(Connection conn, String cUnidadResponsable, String cRoolUsuario) throws SQLException{
		ArrayList arrmObtenDatos = new ArrayList<>();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String cClaveFolio="";
		String nDescripcion="";
		String sQuery="select nFolioAnteProyecto, convert(varchar,nFolioAnteProyecto)+'   ' +upper(cDescripcion) as cDescripcion " +
			"  from tAnteProyectoEncabezado a with (nolock) " + 
			" where a.cUnidadResponsable = '"+cUnidadResponsable+"'";
	//	int nFolio=0;
	//	String cDescVercion="";
		try{
			pstm = conn.prepareStatement(sQuery);
			rs = pstm.executeQuery();
			while (rs.next()) {
				ArrayList<String> arrmODatos = new ArrayList<>();
				cClaveFolio = rs.getString(1);
				nDescripcion = rs.getString(2);
				arrmODatos.add(cClaveFolio);
				arrmODatos.add(nDescripcion);
				arrmObtenDatos.add(arrmODatos);
				arrmODatos=null;
			}
		}finally{
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
			
		}
		return arrmObtenDatos;
	}

	public static ArrayList<ArrayList<String>> UnidadesNormativas(Connection conn) throws SQLException{
		ArrayList<ArrayList<String>> arrmObtenDatos = new ArrayList<>();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String cClaveUnidad="";
		String cDescUnidad="";
		String sQuery="SELECT cUnidadResponsable,cUnidadResponsable+' '+D_DESCRIPCION FROM tCatUnidadResponsable with (nolock) WHERE nAlcance = 1";
	//	int i=0;
		try{
			pstm = conn.prepareStatement(sQuery);
			rs = pstm.executeQuery();
			while (rs.next()) {
				ArrayList<String> arrmODatos = new ArrayList<>();
				cClaveUnidad = rs.getString(1);
				cDescUnidad = rs.getString(2);
				arrmODatos.add(cClaveUnidad);
				arrmODatos.add(cDescUnidad);
				arrmObtenDatos.add(arrmODatos);
				arrmODatos=null;
			}
		}finally{
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return arrmObtenDatos;
	}

	public static ArrayList<ArrayList<String>> UnidadesEjecutoras(Connection conn) throws SQLException{
		ArrayList<ArrayList<String>> arrmObtenDatos = new ArrayList<>();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String cClaveUnidad="";
		String cDescUnidad="";
		String sQuery="SELECT cUnidadResponsable,cUnidadResponsable+' '+D_DESCRIPCION FROM tCatUnidadResponsable with (nolock)";
		
		try{
			pstm = conn.prepareStatement(sQuery);
			rs = pstm.executeQuery();
			while (rs.next()) {
				ArrayList<String> arrmODatos = new ArrayList<>();
				cClaveUnidad = rs.getString(1);
				cDescUnidad = rs.getString(2);
				arrmODatos.add(cClaveUnidad);
				arrmODatos.add(cDescUnidad);
				arrmObtenDatos.add(arrmODatos);
				arrmODatos=null;
			}
		}finally{
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
			pstm=null;
			rs=null;
		}
		return arrmObtenDatos;
	}
	
	public static int InsertaTechoPAUE(Connection conn, String cPartida, String cTipoGasto,String cUnidadEjecutora, Double mTecho) throws SQLException{
		int nVarReturn=0;
		
		PreparedStatement pstm = null;
		String cQueryCrea="INSERT INTO tTechosPartidaUniEjec (cTipoGasto, cPartida, cUnidadResponsable, mTechoPartida) VALUES (?, ?, ?, ? )"; 
		try{
			pstm = conn.prepareStatement(cQueryCrea);
			pstm.setString(1, cTipoGasto);
			pstm.setString(2, cPartida);
			pstm.setString(3, cUnidadEjecutora);
			pstm.setString(4, cUnidadEjecutora);
			pstm.setDouble(5, mTecho);
			nVarReturn = pstm.executeUpdate();
		}finally{
			if (pstm != null)
				pstm.close();
			pstm = null;
		}
		return nVarReturn;
	}

	public static ArrayList<String> ValidaTechoUN(Connection conn, int nFolio, String cUnidadResponsable) throws SQLException{
		ArrayList arrmValTecho = new ArrayList<>();
		PreparedStatement pstmsUN = null;
		ResultSet rsUN = null;
		PreparedStatement pstmsUE = null;
		ResultSet rsUE = null;
		PreparedStatement pstmsEF = null;
		ResultSet rsEF = null;
		PreparedStatement pstmsPP = null;
		ResultSet rsPP = null;
		PreparedStatement pstmsPA = null;
		ResultSet rsPA = null;
		PreparedStatement pstmsPPUE = null;
		ResultSet rsPPUE = null;
		PreparedStatement pstmsPAUE = null;
		ResultSet rsPAUE = null;
		double mTechoUN=0.0;
		double mTechoUE=0.0;
		double mTechoEF=0.0;
		double mTechoPP=0.0;
		double mTechoPA=0.0;
		double mTechoPPUE=0.0;
		double mTechoPAUE=0.0;
		String cUnidadNormativa="";
		String cUnidadEjecutora="";
		String cEntidadFederativa="";
		String cProgramaPresupuestario ="";
		String cTipoGasto ="";
		String cPartida ="";
		
		String cQueryUN="SELECT tn.mTechoMonto - a.mTecho as mTecho, a.cUnidadNorativa FROM (select sum(d.mCalculado) as mTecho, e.cUnidadNorativa from tAnteProyectoDetalle d with (nolock), tCatalogoEPAnteProy e with (nolock)"+
		"		WHERE d.cClaveSiaff = e.ClaveSIAFF AND ltrim(rtrim(d.cClaveInterna)) = ltrim(rtrim(e.ClaveInterna)) AND d.nFolioAnteProyecto = "+nFolio +" " +
		" 	group by e.cUnidadNorativa )  a "+
		" 	, tTechoUNormativa tn "+
		" where a.cUnidadNorativa = tn.cUnidadNormativa"+
		"  AND a.mTecho > tn.mTechoMonto";
		String cQueryUE="SELECT te.mTechoMonto - a.mTecho, a.cUnidadEjecutora "+
		" FROM (select sum(d.mCalculado) as mTecho, e.cUnidadEjecutora "+
		"	 from tAnteProyectoDetalle d with (nolock)"+
		"	    , tCatalogoEPAnteProy e with (nolock) "+
		"	WHERE d.cClaveSiaff = e.ClaveSIAFF "+
		"	  AND ltrim(rtrim(d.cClaveInterna)) = ltrim(rtrim(e.ClaveInterna)) "+
		"	  AND d.nFolioAnteProyecto = "+nFolio +" "+
		"	group by e.cUnidadEjecutora )  a "+
		"	, tTechoUEjecutora te "+
		" where a.cUnidadEjecutora = te.cUnidadResponsable "+
		"  AND a.mTecho > te.mTechoMonto";
		String cQueryEF="SELECT ted.mMontoTecho - a.mTecho, a.cEntidadFederativa "+
		" FROM (select sum(d.mCalculado) as mTecho, e.cEntidadFederativa "+
		"	 from tAnteProyectoDetalle d "+
		"	    , tCatalogoEPAnteProy e "+
		"	WHERE d.cClaveSiaff = e.ClaveSIAFF "+
		"	  AND ltrim(rtrim(d.cClaveInterna)) = ltrim(rtrim(e.ClaveInterna)) "+
		"	  AND d.nFolioAnteProyecto = "+nFolio +" "+
		"	group by e.cEntidadFederativa )  a "+
		"	, tTechoEntidadFederativa ted "+
		" where a.cEntidadFederativa = ted.cEntidadFederativa "+
		"  AND a.mTecho > ted.mMontoTecho";
		String cQueryPP="SELECT tpp.mTechoMonto - a.mTecho, a.cProgramaPresupuestario "+
		" FROM (select sum(d.mCalculado) as mTecho, e.cProgramaPresupuestario "+
		"		 from tAnteProyectoDetalle d with (nolock)"+
		"		    , tCatalogoEPAnteProy e with (nolock)"+
		"		WHERE d.cClaveSiaff = e.ClaveSIAFF "+
		"		  AND ltrim(rtrim(d.cClaveInterna)) = ltrim(rtrim(e.ClaveInterna)) "+
		"		  AND d.nFolioAnteProyecto = "+nFolio +" "+
		"	group by e.cProgramaPresupuestario )  a "+
		"	, tTechoProgramaPresupuestario tpp "+
		" where a.cProgramaPresupuestario = tpp.cProgramaPresupuestario "+
		"  AND a.mTecho > tpp.mTechoMonto ";
		String cQueryPA=" SELECT tpa.mTechoPartida - a.mTecho, a.cPartida, a.cTipoGasto "+
		" FROM (select sum(d.mCalculado) as mTecho, e.cPartida, e.cTipoGasto "+
		" 		 from tAnteProyectoDetalle d  with (nolock)"+
		" 		    , tCatalogoEPAnteProy e  with (nolock)"+
		" 		WHERE d.cClaveSiaff = e.ClaveSIAFF "+ 
		" 		  AND ltrim(rtrim(d.cClaveInterna)) = ltrim(rtrim(e.ClaveInterna)) "+ 
		" 		  AND d.nFolioAnteProyecto =  "+nFolio +" "+
		" 	group by e.cPartida, e.cTipoGasto )  a "+ 
		" 	, tTechosPartida tpa  "+
		"  where a.cPartida = tpa.cPartida "+ 
		"  AND  a.cTipoGasto = tpa.cTipoGasto  "+
		"  AND a.mTecho > tpa.mTechoPartida ";
		//Partida Restringuida por unidad Ejecutora
		String cQueryPAUE=" SELECT tpa.mTechoPartida - a.mTecho, a.cPartida, a.cTipoGasto, a.cUnidadEjecutora  "+
		"		 FROM (select sum(d.mCalculado) as mTecho, e.cPartida, e.cTipoGasto, e.cUnidadEjecutora  "+
		"				 from tAnteProyectoDetalle d   with (nolock)"+
		"				    , tCatalogoEPAnteProy e  with (nolock) "+
		"				WHERE d.cClaveSiaff = e.ClaveSIAFF  "+ 
		"				  AND ltrim(rtrim(d.cClaveInterna)) = ltrim(rtrim(e.ClaveInterna))  "+ 
		"				  AND d.nFolioAnteProyecto = "+nFolio +" "+
		"			group by e.cPartida, e.cTipoGasto, e.cUnidadEjecutora )  a  "+ 
		"			, tTechosPartida tpa   "+
		"		 where a.cPartida = tpa.cPartida  "+ 
		"		  AND  a.cTipoGasto = tpa.cTipoGasto   "+
		"		  AND a.cUnidadEjecutora = '"+cUnidadResponsable+"' " +
		"		  AND a.mTecho > tpa.mTechoPartida   ";
		// Programa Presupuestario por Unidad Ejecutora
		String cQueryPPUE=" SELECT tpp.mTechoMonto - a.mTecho, a.cProgramaPresupuestario, a.cUnidadEjecutora  "+ 
		"		 FROM (select sum(d.mCalculado) as mTecho, e.cProgramaPresupuestario, e.cUnidadEjecutora   "+
		"				 from tAnteProyectoDetalle d  with (nolock) "+
		"				    , tCatalogoEPAnteProy e  with (nolock) "+
		"				WHERE d.cClaveSiaff = e.ClaveSIAFF  "+ 
		"				  AND ltrim(rtrim(d.cClaveInterna)) = ltrim(rtrim(e.ClaveInterna))  "+ 
		"				  AND d.nFolioAnteProyecto = "+nFolio +" "+
		"			group by e.cProgramaPresupuestario, e.cUnidadEjecutora )  a   "+
		"			, tTechoProgramaPresupuestario tpp   "+
		"		 where a.cProgramaPresupuestario = tpp.cProgramaPresupuestario  "+ 
		"		  AND a.mTecho > tpp.mTechoMonto   "+
		"		  AND a.cUnidadEjecutora = '"+cUnidadResponsable+"' ";
		//
		String monto="";
		try{
			//Unidad Normativa
			pstmsUN = conn.prepareStatement(cQueryUN);
			rsUN = pstmsUN.executeQuery();
			while (rsUN.next()) {
				ArrayList<String> arrmODatos = new ArrayList<>();
				String cMensaje="";
				mTechoUN = rsUN.getDouble(1);
				cUnidadNormativa = rsUN.getString(2);
				if (!"".equals(cUnidadNormativa)){
					monto = String.valueOf(new DecimalFormat("###,###.##").format(mTechoUN));
					cMensaje="Excede el techo Presupuestal para La Unidad Normativa:"+cUnidadNormativa+ " Por la Cantidad:"+monto;
					arrmODatos.add(cMensaje);
					arrmValTecho.add(arrmODatos);
				}
				arrmODatos=null;
			}
			//Unidad Ejecutora
			pstmsUE = conn.prepareStatement(cQueryUE);
			rsUE = pstmsUE.executeQuery();
			while (rsUE.next()) {
				ArrayList<String> arrmODatos = new ArrayList<>();
				String cMensaje="";
				mTechoUE = rsUE.getDouble(1);
				cUnidadEjecutora = rsUE.getString(2);
				if (!"".equals(cUnidadEjecutora)){
					monto = String.valueOf(new DecimalFormat("###,###.##").format(mTechoUE));
					cMensaje="Excede el techo Presupuestal para La Unidad Ejecutora:"+cUnidadEjecutora+ " Por la Cantidad:"+monto;
					arrmODatos.add(cMensaje);
					arrmValTecho.add(arrmODatos);
				}
				arrmODatos=null;
			}
			//Entidad Federativa
			pstmsEF = conn.prepareStatement(cQueryEF);
			rsEF = pstmsEF.executeQuery();
			while (rsEF.next()) {
				ArrayList<String> arrmODatos = new ArrayList<>();
				String cMensaje="";
				mTechoEF = rsEF.getDouble(1);
				cEntidadFederativa = rsEF.getString(2);
				if (!"".equals(cEntidadFederativa)){
					monto = String.valueOf(new DecimalFormat("###,###.##").format(mTechoEF));
					cMensaje="Excede el techo Presupuestal para La Entidad Federativa:"+cEntidadFederativa+ " Por la Cantidad:"+monto;
					arrmODatos.add(cMensaje);
					arrmValTecho.add(arrmODatos);
				}
				arrmODatos=null;
			}
			//Programa Presupuestario
			pstmsPP = conn.prepareStatement(cQueryPP);
			rsPP = pstmsPP.executeQuery();
			while (rsPP.next()) {
				ArrayList<String> arrmODatos = new ArrayList<>();
				String cMensaje="";
				mTechoPP = rsPP.getDouble(1);
				cProgramaPresupuestario = rsPP.getString(2);
				if (!"".equals(cEntidadFederativa)){
					monto = String.valueOf(new DecimalFormat("###,###.##").format(mTechoPP));
					cMensaje="Excede el techo Presupuestal para El Programa Presupuestario:"+cProgramaPresupuestario+ " Por la Cantidad:"+monto;
					arrmODatos.add(cMensaje);
					arrmValTecho.add(arrmODatos);
				}
				arrmODatos=null;
			}
			//Partida Restringuidas
			pstmsPA = conn.prepareStatement(cQueryPA);
			rsPA = pstmsPA.executeQuery();
			while (rsPA.next()) {
				ArrayList<String> arrmODatos = new ArrayList<>();
				String cMensaje="";
				mTechoPA = rsPA.getDouble(1);
				cPartida = rsPA.getString(2);
				cTipoGasto = rsPA.getString(3);
				if (!"".equals(cEntidadFederativa)){
					monto = String.valueOf(new DecimalFormat("###,###.##").format(mTechoPA));
					cMensaje="Excede el techo Presupuestal para La Partida:"+cPartida+ ", con el Tipo de Gasto:"+cTipoGasto+" Por la Cantidad:"+monto;
					arrmODatos.add(cMensaje);
					arrmValTecho.add(arrmODatos);
				}
				arrmODatos=null;
			}

			//Programa Presupuestario para la Unidad Ejecutora
			pstmsPPUE = conn.prepareStatement(cQueryPPUE);
			rsPPUE = pstmsPPUE.executeQuery();
			while (rsPP.next()) {
				ArrayList<String> arrmODatos = new ArrayList<>();
				String cMensaje="";
				mTechoPPUE = rsPPUE.getDouble(1);
				cProgramaPresupuestario = rsPPUE.getString(2);
				if (!"".equals(cProgramaPresupuestario)){
					monto = String.valueOf(new DecimalFormat("###,###.##").format(mTechoPPUE));
					cMensaje="Excede el techo Presupuestal para El Programa Presupuestario:"+cProgramaPresupuestario+ ", Para la Unidad Ejecutora:"+cUnidadResponsable+" Por la Cantidad:"+monto;
					arrmODatos.add(cMensaje);
					arrmValTecho.add(arrmODatos);
				}
				arrmODatos=null;
			}
			//PRograma PResupuestario Para la Unidad Ejecutora 
			pstmsPAUE = conn.prepareStatement(cQueryPAUE);
			rsPAUE = pstmsPAUE.executeQuery();
			while (rsPAUE.next()) {
				ArrayList<String> arrmODatos = new ArrayList<>();
				String cMensaje="";
				mTechoPAUE = rsPAUE.getDouble(1);
				cPartida = rsPAUE.getString(2);
				cTipoGasto = rsPAUE.getString(3);
				if (!"".equals(cPartida)){
					monto = String.valueOf(new DecimalFormat("###,###.##").format(mTechoEF));
					cMensaje="Excede el techo Presupuestal para La Partida:"+cPartida+ ", con el Tipo de Gasto:"+cTipoGasto+" Por la Cantidad:"+monto;
					arrmODatos.add(cMensaje);
					arrmValTecho.add(arrmODatos);
				}
				arrmODatos=null;
			}
			
			
		}finally{
			if (pstmsUN != null) {
				pstmsUN.close();
			}
			if (rsUN != null) {
				rsUN.close();
			}
			pstmsUN = null;
			rsUN = null;
			if (pstmsUE != null) {
				pstmsUE.close();
			}
			if (rsUE != null) {
				rsUE.close();
			}
			pstmsUE = null;
			rsUE = null;
			if (pstmsEF != null) {
				pstmsEF.close();
			}
			if (rsEF != null) {
				rsEF.close();
			}
			pstmsEF = null;
			rsEF = null;
			if (pstmsPP != null) {
				pstmsPP.close();
			}
			if (rsPP != null) {
				rsPP.close();
			}
			pstmsPP = null;
			rsPP = null;
			if (pstmsPA != null) {
				pstmsPA.close();
			}
			if (rsPA != null) {
				rsPA.close();
			}
			pstmsPA = null;
			rsPA = null;
			if (pstmsPPUE != null) {
				pstmsPPUE.close();
			}
			if (rsPPUE != null) {
				rsPPUE.close();
			}
			pstmsPPUE = null;
			rsPPUE = null;
			if (pstmsPAUE != null) {
				pstmsPAUE.close();
			}
			if (rsPAUE != null) {
				rsPAUE.close();
			}
			pstmsPAUE = null;
			rsPAUE = null;
			
		}
		return arrmValTecho;
	}
	
 	public static int InsertaTechoPPUE(Connection conn, String cProgramaPresupuestario, String cUnidadEjecutora, Double mTecho) throws SQLException{
		int nVarReturn=0;
		PreparedStatement pstm = null;
		String cQueryCrea="INSERT INTO tTechoProgPresupUniEjec (cProgramaPresupuestario, cUnidadResponsable, mTechoMonto) VALUES (?, ?, ?)";
		try{
			pstm = conn.prepareStatement(cQueryCrea);
			pstm.setString(1, cProgramaPresupuestario);
			pstm.setString(2, cUnidadEjecutora);
			pstm.setDouble(3, mTecho);
			nVarReturn = pstm.executeUpdate();
			
		}finally{
			if (pstm != null)
				pstm.close();
			pstm = null;
		}
		return nVarReturn;
		
	}

	public static int InsertaTechoEF(Connection conn, String cEntidadFederativa, Double mTecho) throws SQLException{
		int nVarReturn=0;
		
		PreparedStatement pstm = null;
		String cQueryCrea="INSERT INTO tTechoEntidadFederativa (cEntidadFederativa, mMontoTecho) VALUES (?, ?)";
		try{
			pstm = conn.prepareStatement(cQueryCrea);
			pstm.setString(1, cEntidadFederativa);
			pstm.setDouble(2, mTecho);
			nVarReturn = pstm.executeUpdate();
			
		}finally{
			if (pstm != null)
				pstm.close();
			pstm = null;
		}
		return nVarReturn;
		
	}

	public static int InsertaTechoPP(Connection conn, String cProgramaPresupuestario, Double mTecho) throws SQLException{
		int nVarReturn=0;
		
		PreparedStatement pstm = null;
		String cQueryCrea="INSERT INTO tTechoProgramaPresupuestario (cProgramaPresupuestario, mTechoMonto) VALUES (?, ?)";
		try{
			pstm = conn.prepareStatement(cQueryCrea);
			pstm.setString(1, cProgramaPresupuestario);
			pstm.setDouble(2, mTecho);
			nVarReturn = pstm.executeUpdate();
			
		}finally{
			if (pstm != null)
				pstm.close();
			pstm = null;
		}
		return nVarReturn;
		
	}

	public static int InsertaTechoPA(Connection conn, String cPartida, String cTipoGasto, Double mTecho) throws SQLException{
		int nVarReturn=0;
		
		PreparedStatement pstm = null;
		String cQueryCrea="INSERT INTO tTechosPartida (cTipoGasto, cPartida, mTechoPartida) VALUES (?, ?, ?)"; 
		try{
			pstm = conn.prepareStatement(cQueryCrea);
			pstm.setString(1, cTipoGasto);
			pstm.setString(2, cPartida);
			pstm.setDouble(3, mTecho);
			nVarReturn = pstm.executeUpdate();
			
		}finally{
			if (pstm != null)
				pstm.close();
			pstm = null;
		}
		return nVarReturn;
		
	}

	public static int InsertaTechoPartidaUE(Connection conn, String cPartida, String cTipoGasto, String cUnidadResponsable, Double mTecho) throws SQLException{
		int nVarReturn=0;
		
		PreparedStatement pstm = null;
		String cQueryCrea="INSERT INTO tTechosPartidaUniEjec (cUnidadResponsable, cTipoGasto, cPartida, mTechoPartida) VALUES (?, ?, ?, ?)"; 
		try{
			pstm = conn.prepareStatement(cQueryCrea);
			pstm.setString(1, cUnidadResponsable);
			pstm.setString(2, cTipoGasto);
			pstm.setString(3, cPartida);
			pstm.setDouble(4, mTecho);
			nVarReturn = pstm.executeUpdate();
			
		}finally{
			if (pstm != null)
				pstm.close();
			pstm = null;
		}
		return nVarReturn;
		
	}

	public static int InsertaTechoPartidaUN(Connection conn, String cPartida, String cTipoGasto, String cUnidadNormativa, Double mTecho) throws SQLException{
		int nVarReturn=0;
		
		PreparedStatement pstm = null;
		String cQueryCrea="INSERT INTO tTechosPartidaUniNorm (cUnidadNormativa, cTipoGasto, cPartida, mTechoPartida) VALUES (?, ?, ?, ?)"; 
		try{
			pstm = conn.prepareStatement(cQueryCrea);
			pstm.setString(1, cUnidadNormativa);
			pstm.setString(2, cTipoGasto);
			pstm.setString(3, cPartida);
			pstm.setDouble(4, mTecho);
			nVarReturn = pstm.executeUpdate();
			
		}finally{
			if (pstm != null)
				pstm.close();
			pstm = null;
		}
		return nVarReturn;
		
	}

	public static int InsertaTechoUN(Connection conn, String cUnidadNormativa, Double mTecho) throws SQLException{
		int nVarReturn=0;
		
		PreparedStatement pstm = null;
		String cQueryCrea="INSERT INTO tTechoUNormativa (cUnidadNormativa, mTechoMonto) VALUES (?, ?)";
		try{
			pstm = conn.prepareStatement(cQueryCrea);
			pstm.setString(1, cUnidadNormativa);
			pstm.setDouble(2, mTecho);
			nVarReturn = pstm.executeUpdate();
			
		}finally{
			if (pstm != null)
				pstm.close();
			pstm = null;
		}
		return nVarReturn;
		
	}

	public static int InsertaTechoUE(Connection conn, String cUnidadEjecutora, Double mTecho) throws SQLException{
		int nVarReturn=0;
		
		PreparedStatement pstm = null;
		String cQueryCrea="INSERT INTO tTechoUEjecutora (cUnidadResponsable, mTechoMonto) VALUES (?, ?)"; 
		try{
			pstm = conn.prepareStatement(cQueryCrea);
			pstm.setString(1, cUnidadEjecutora);
			pstm.setDouble(2, mTecho);
			nVarReturn = pstm.executeUpdate();
			
		}finally{
			if (pstm != null)
				pstm.close();
			pstm = null;
		}
		return nVarReturn;
		
	}

	public static int InsertaTechoEn(Connection conn, String cEntidadFederativa, Double mTecho) throws SQLException{
		int nVarReturn=0;
		
		PreparedStatement pstm = null;
		String cQueryCrea="INSERT INTO tTechoEntidadFederativa (cEntidadFederativa, mMontoTecho) VALUES (?, ?)";
		try{
			pstm = conn.prepareStatement(cQueryCrea);
			pstm.setString(1, cEntidadFederativa);
			pstm.setDouble(2, mTecho);
			nVarReturn = pstm.executeUpdate();
			
		}finally{
			if (pstm != null)
				pstm.close();
			pstm = null;
		}
		return nVarReturn;
		
	}

	public static boolean ValidaDetalleAnteR(Connection conn, String cUnidadResponsable, int nFolio, int nConsecutivo, String cEP, String cMotivoRechazo) throws SQLException{
		PreparedStatement pstm = null;
		PreparedStatement pstms = null;
		boolean retVar=false;
		ResultSet rs = null;
		String cSIAFF="";
		String cInterna="";
		String cQueryEP="select e.ClaveSIAFF, e.ClaveInterna from tCatalogoEPAnteProy e with (nolock) where e.EP = '"+cEP.trim()+"'" ;
		String cQuery="UPDATE tAnteProyectoDetalle  SET cMotivoRechazo = ? ";
		cQuery+= " WHERE  nFolioAnteProyecto = ? AND cClaveSiaff =? AND cClaveInterna =?";
		try{
			pstms = conn.prepareStatement(cQueryEP);
			rs = pstms.executeQuery();
			if (rs.next()){
				cSIAFF = rs.getString(1);
				cInterna = rs.getString(2);
				Log.debug(cQuery);
				System.out.println(cQuery);
				Log.debug("Folio"+nFolio);
				System.out.println("Folio"+nFolio);
				Log.debug("cSIAFF:"+cSIAFF+" cInterna:"+cInterna.trim());
				System.out.println("cSIAFF:"+cSIAFF+" cInterna:"+cInterna.trim());
				pstm = conn.prepareStatement(cQuery);
				pstm.setString(1, cMotivoRechazo);
				pstm.setInt(2, nFolio);
				pstm.setString(3, cSIAFF);
				pstm.setString(4, cInterna.trim());
				retVar = pstm.execute();
			}
			
		}finally{
			if (pstm != null)
				pstm.close();
			if (pstms != null)
				pstms.close();
			if (rs != null){
				rs.close();
			}
			pstm = null;
			pstms = null;
			rs=null;
		}
		
		return retVar; 

	}
	
	public static int ValidaUnUe(Connection conn, String cUnidadResponsable, String cUnidadEjecutora) throws SQLException{
		int iVreturn=0;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String cQuery="SELECT COUNT(*) AS TOTAL FROM tCatalogoEPAnteProy E with (nolock) " ;
		if (!"".equals(cUnidadResponsable) && !"".equals(cUnidadEjecutora)) 
			cQuery+=" WHERE E.cUnidadNorativa = '"+cUnidadResponsable+"' AND E.cUnidadEjecutora = '"+cUnidadEjecutora+"'";
		try{
			pstm = conn.prepareStatement(cQuery);
			rs = pstm.executeQuery();
			if(rs.next()) {
				iVreturn = rs.getInt(1);
			}
		}finally{
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
			pstm=null;
			rs=null;
		}
		return iVreturn;
	}

	public static int MarcaEstatus(Connection conn, String cUnidadResponsable, int nFolio, String cQuien, String cEstatus) throws SQLException{
		int iRetVar=0;
		
		PreparedStatement pstm = null;
		String cQueryRev=" UPDATE tAnteProyectoEncabezado set cRevisor= '"+cEstatus+"' where nFolioAnteProyecto = ? ";//AND cUnidadResponsable = ? ";
		String cQueryAut=" UPDATE tAnteProyectoEncabezado set cAutorizado= '"+cEstatus+"' where nFolioAnteProyecto = ? ";//AND cUnidadResponsable = ? ";
		
		try{
			if ("Revisor".equals(cQuien)){
				pstm = conn.prepareStatement(cQueryRev);
			}else{
				pstm = conn.prepareStatement(cQueryAut);
			}
			pstm.setInt(1, nFolio);
			//pstm.setString(2, cUnidadResponsable);
			iRetVar = pstm.executeUpdate();
			
		}finally{
			if (pstm != null) {
				pstm.close();
			}
			pstm=null;
		}
		return iRetVar;
	}
	
	public static int ConsolidaAnteProyecto(Connection conn, int nFolio ) throws SQLException{
		int iVreturn=0;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		PreparedStatement pstmc = null;
		ResultSet rsc = null;
		//PreparedStatement pstmb = null;
		//ResultSet rsb = null;
		PreparedStatement pstmx = null;
		ResultSet rsx = null;
		PreparedStatement pstmi = null;
		//cRevisor not in ('R','0') ";
		int iMaxConsec=0;
		int iExiste=0;

		try{
			// verifica si existe el anteProyecto autorizado y obtiene su id
			String cQueryBusca = "select count(*) from tAnteProyectoEncabezadoAut with (nolock) WHERE cRevisor not in ('R','0') and aEjercicioFiscal ='"+String.valueOf(Integer.parseInt(AdecuacionManager.obtenEjercicioFiscal(conn))+1)+"'";
			pstmc = conn.prepareStatement(cQueryBusca);
			rsc = pstmc.executeQuery();
			if(rsc.next()) {
				iExiste = rsc.getInt(1);
			}
			if (iExiste == 0){
				//Calcula el Consecutivo 
				String cQueryBuscaFolio = "select max(nFolioAnteProyectoAut) from tAnteProyectoEncabezadoAut with (nolock) WHERE cRevisor not in ('R','0')";
				//String cQueryBuscaFolio = "select max(nFolioAnteProyectoAut) from tAnteProyectoEncabezado with (nolock) WHERE cRevisor not in ('R','0')";
				//pstmx = conn.prepareStatement(cQueryBusca); //estas haciendo un max a nFolioAnteProyectoAut en la tabla de tAnteProyectoEncabezado, ese folio NO Existe en esa tabla!!!
				//ponias el mismo cQueryBusca en lugar del cQueryBuscaFolio!!!!
				pstmx = conn.prepareStatement(cQueryBuscaFolio);
				rsx = pstmx.executeQuery();
				if(rsx.next()) {
					iExiste = rsx.getInt(1);
				}
				iExiste++;
				//se crea el heder del consolidado
				String cQueryInsertHeder="INSERT INTO tAnteProyectoEncabezadoAut (nFolioAnteProyectoAut,aEjercicioFiscal,cUnidadResponsable,fCarga,nFolioAnteProyectoAutAnterior,aEjercicioFiscalAnterior,nCuenta,nIncremento,nDecremento,cCampoCalculo,cRevisor,cAutorizado,cDescripcion,cUnidadNormativa )" +
						" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
				pstmi = conn.prepareStatement(cQueryInsertHeder);
				pstmi.setInt(1, iExiste); //el folio nuevo, pero mas abajo el anterior le pones el mismo que este. Si es un identity no hay necesidad de ponerlo. Para qué haces una consulta para obtenerlo, ese folio viene en el caso.
				pstmi.setString(2, String.valueOf(Integer.parseInt(AdecuacionManager.obtenEjercicioFiscal(conn))+1)); //quite tu hardcode de "2013"
				pstmi.setString(3, "RHQ");
				pstmi.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
				pstmi.setInt(5, iExiste); //el folio anterior le pones el mismo que el actual?? 
				pstmi.setString(6, AdecuacionManager.obtenEjercicioFiscal(conn));
				pstmi.setInt(7, 0);
				pstmi.setInt(8, 0);
				pstmi.setString(9, "");
				pstmi.setString(10, "");
				pstmi.setString(11, "");
				pstmi.setString(12, "");
				//pstmi.setString(13, "Version "+iExiste+1+" del Ante Proyecto."); //no lo habías sumado ya antes iExiste++? para que le sumas 1?
				pstmi.setString(13, "Version "+iExiste+" del Ante Proyecto.");
				pstmi.setString(14, "RHQ");
				
				iVreturn = pstmi.executeUpdate();
				
				// inisia actualizacion //se escribe inicia!!
				String cQueryMaximo="select max(nConsecutivo) FROM tAnteProyectoDetalleAut with (nolock) WHERE nFolioAnteProyectoAut = ? ";
				pstm = conn.prepareStatement(cQueryMaximo);
				pstm.setInt(1, iExiste);
				rs = pstm.executeQuery(); //esto es un select max a detalleaut el cual no ha sido insertado, se pregunta por iExiste, este iExiste tiene el folio del último , de que me sirve?
				if(rs.next()) {
					iMaxConsec = rs.getInt(1);
				}
				
				String cQueryInsert="INSERT INTO tAnteProyectoDetalleAut (nConsecutivo,nFolioAnteProyectoAut,aEjercicioFiscal,cUnidadResponsable,nFolioAnteProyecto,cUnidadEjecutora,cClaveSiaff,cClaveInterna,mCalculado,mOptimo,mIreductible,nPorcentajeReduccion,nPorcentajeIncremento,cMotivoRechazo ) ";
				cQueryInsert+="select d.nConsecutivo,"+iExiste+" as nFolioAnteProyectoAut, d.aEjercicioFiscal, d.cUnidadResponsable, d.nFolioAnteProyecto, d.cUnidadResponsable, cClaveSiaff, cClaveInterna, mCalculado, mOptimo ";
				cQueryInsert+="     , mIreductible, nPorcentajeReduccion, nPorcentajeIncremento, cMotivoRechazo ";
				cQueryInsert+="  from tAnteProyectoEncabezado a with (nolock) ";
				cQueryInsert+="     , tAnteProyectoDetalle d with (nolock) ";
				cQueryInsert+=" where a.nFolioAnteProyecto = "+nFolio;
				cQueryInsert+="   AND a.nFolioAnteProyecto  = d.nFolioAnteProyecto ";
				cQueryInsert+="   AND a.cAutorizado = 'A' ";
				pstmi = conn.prepareStatement(cQueryInsert);
				iVreturn = pstmi.executeUpdate();
				
			}
			

		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			conn.commit();
			if (pstmi != null)
				pstmi.close();
			pstmi = null;
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
			pstm=null;
			rs=null;
			if (pstmc != null) {
				pstmc.close();
			}
			if (rsc != null) {
				rsc.close();
			}
			pstmc=null;
			rsc=null;
			
		}
		return iVreturn;
	}

	public static String getDescripcionCUResponsable(Connection conn, String cUnidadResponsable) throws SQLException{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String descripcion="";
		String cQuery="select ur.D_DESCRIPCION from tCatUnidadResponsable ur with (nolock) where ur.cUnidadResponsable = '"+cUnidadResponsable.trim()+"'" ;
		try{
			pstm = conn.prepareStatement(cQuery);
			rs = pstm.executeQuery();
			if (rs.next()){
			    descripcion = rs.getString(1);
			}
		}finally{
			if (pstm != null)
				pstm.close();
			if (rs != null){
				rs.close();
			}
			pstm = null;
			rs=null;
		}
		
		return descripcion; 
	}
	
	public static String getEntidadFederativaClave(Connection conn, String cEntidadFederativa) throws SQLException{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String descripcion="";
		String cQuery="select ef.dEntidadFederativa from tCatalogoEntidadFederativa ef with (nolock) where ef.cEntidadFederativa = '"+cEntidadFederativa.trim()+"'" ;
		try{
			pstm = conn.prepareStatement(cQuery);
			rs = pstm.executeQuery();
			if (rs.next()){
			    descripcion = rs.getString(1);
			}
		}finally{
			if (pstm != null)
				pstm.close();
			if (rs != null){
				rs.close();
			}
			pstm = null;
			rs=null;
		}
		
		return descripcion.trim(); 
	}
	
	public static String getProgramaPresupuestario(Connection conn, String cProgramaPresupuestario) throws SQLException{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String descripcion="";
		String cQuery="select pp.dProgramaPresupuestario from tCatalogoProgramaPresupuestario pp with (nolock) where pp.cProgramaPresupuestario = '"+cProgramaPresupuestario.trim()+"'" ;
		try{
			pstm = conn.prepareStatement(cQuery);
			rs = pstm.executeQuery();
			if (rs.next()){
			    descripcion = rs.getString(1);
			}
		}finally{
			if (pstm != null)
				pstm.close();
			if (rs != null){
				rs.close();
			}
			pstm = null;
			rs=null;
		}
		
		return descripcion.trim(); 
	}

	public static int borraTecho(Connection conn, String cUnidad, String tabla, String campo, String campo2, String valorCampo2,String campo3, String valorCampo3) throws SQLException {
		PreparedStatement pstmnt = null;
		int iExiste = 0;
		//boolean retval;
		try {
		    if(campo2!=null){
			if(campo3!=null)
			    pstmnt = conn.prepareStatement("DELETE FROM "+tabla+" WHERE "+campo+"=? AND "+campo2+"=? AND "+campo3+"=?");
			else
			    pstmnt = conn.prepareStatement("DELETE FROM "+tabla+" WHERE "+campo+"=? AND "+campo2+"=?");
		    }
		    else
			pstmnt = conn.prepareStatement("DELETE FROM "+tabla+" WHERE "+campo+"=?");
			pstmnt.setString(1, cUnidad);
			
			if(campo2!=null)
			    pstmnt.setString(2, valorCampo2);
			
			if(campo3!=null)
			    pstmnt.setString(3, valorCampo3);
			
			iExiste = pstmnt.executeUpdate();

		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}
		return iExiste;
	}
	
	public static int borraTechoUN(Connection conn, String cUnidad, String tabla, String campo, String campo2, String valorCampo2,String campo3, String valorCampo3) throws SQLException {
		PreparedStatement pstmnt = null;
		int iExiste = 0;
		
		try {
		    if(campo2!=null){
			if(campo3!=null)
			    pstmnt = conn.prepareStatement("DELETE FROM "+tabla+" WHERE "+campo+"=? AND "+campo2+"=? AND "+campo3+"=?");
			else
			    pstmnt = conn.prepareStatement("DELETE FROM "+tabla+" WHERE "+campo+"=? AND "+campo2+"=?");
		    }
		    else
			pstmnt = conn.prepareStatement("DELETE FROM "+tabla+" WHERE "+campo+"=?");
			pstmnt.setString(1, valorCampo3);
			
			if(campo2!=null)
			    pstmnt.setString(2, valorCampo2);
			
			if(campo3!=null)
			    pstmnt.setString(3, cUnidad);
			
			iExiste = pstmnt.executeUpdate();

		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}
		return iExiste;
	}

	public static int getTechoExiste(Connection conn, String cUnidad, String tabla, String campo, String campo2, String valorCampo2, String campo3, String valorCampo3) throws SQLException{
	    	int iVreturn=0;
	    	PreparedStatement pstm = null;
		ResultSet rs = null;
		String cQuery;
		if(campo2!=null){
		    if(campo3!=null) cQuery="SELECT COUNT(*) AS TOTAL FROM "+tabla+" with (nolock) WHERE "+campo+"=? AND "+campo2+"=? AND "+campo3+"=?";
		    else cQuery="SELECT COUNT(*) AS TOTAL FROM "+tabla+" with (nolock) WHERE "+campo+"=? AND "+campo2+"=?";
		}
		else
		    cQuery="SELECT COUNT(*) AS TOTAL FROM "+tabla+" with (nolock) WHERE "+campo+"=? " ;
		try{
			pstm = conn.prepareStatement(cQuery);
			pstm.setString(1, cUnidad);
			if(campo2!=null)
			    pstm.setString(2, valorCampo2);
			if(campo3!=null)
			    pstm.setString(3, valorCampo3);
			rs = pstm.executeQuery();
			if(rs.next()) {
				iVreturn = rs.getInt(1);
			}
		}finally{
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
			pstm=null;
			rs=null;
		}
		return iVreturn;
	}

	public static HashMap<String, String> getTechos(Connection conn, String tabla) throws SQLException{
	    	HashMap<String, String> contenedor = new HashMap<>();
	    	String valor1;
	    	String valor2;
	    	String valor3;
	    	String descripcion;
	    	String descripcion2;
	    	String descripcion3;
	    	String monto;
	    	int key = 0;
	    	
	    	PreparedStatement pstm = null;
		ResultSet rs = null;
		String cQuery;
		cQuery="SELECT * FROM "+tabla+" with (nolock)" ;
		try{
			pstm = conn.prepareStatement(cQuery);
			rs = pstm.executeQuery();
			if(tabla.equals("tTechoUNormativa") || tabla.equals("tTechoUEjecutora") || tabla.equals("tTechoEntidadFederativa") || tabla.equals("tTechoProgramaPresupuestario")){
			    while(rs.next()) {
				valor1 = rs.getString(1);
				contenedor.put("c"+key, valor1);
				if(tabla.equals("tTechoUNormativa") || tabla.equals("tTechoUEjecutora"))
				    descripcion = getDescripcionCUResponsable(conn, valor1);
				else if (tabla.equals("tTechoEntidadFederativa"))
				    descripcion = getEntidadFederativaClave(conn, valor1);
				//else if (tabla.equals("tTechoProgramaPresupuestario"))
				else
				    descripcion = getProgramaPresupuestario(conn, valor1);
				contenedor.put("d"+key, descripcion);
				monto = String.valueOf(new DecimalFormat("###,###.##").format(rs.getDouble(2)));
				contenedor.put("techo"+key, monto);
				key++;
			    }
			}else if(tabla.equals("tTechoProgPresupUniEjec") || tabla.equals("tTechosPartida")){
			    while(rs.next()) {
				valor1 = rs.getString(1);
				contenedor.put("cp"+key, valor1);
				
				if(tabla.equals("tTechoProgPresupUniEjec"))
				    descripcion = getUnidadResponsable(conn, valor1);
				else
				    descripcion = getTipoGasto(conn, valor1);
				contenedor.put("d"+key, descripcion);
				
				valor2 = rs.getString(2);
				contenedor.put("cu"+key, valor2);
				
				if(tabla.equals("tTechoProgPresupUniEjec"))
				    descripcion2 = getProgramaPresupuestario(conn, valor2);
				else
				    descripcion2 = getPartidaCatalogo(conn, valor2);
				contenedor.put("d2"+key, descripcion2);
				
				monto = String.valueOf(new DecimalFormat("###,###.##").format(rs.getDouble(3)));
				contenedor.put("techo"+key, monto);
				key++;
			    }
			}else if(tabla.equals("tTechosPartidaUniEjec")){
			    while(rs.next()) {
				valor1 = rs.getString(1);
				contenedor.put("cp"+key, valor1);
				
				descripcion = getUnidadResponsable(conn, valor1);
				contenedor.put("d"+key, descripcion);
				
				valor2 = rs.getString(2);
				contenedor.put("ct"+key, valor2);
				
				descripcion2 = getTipoGasto(conn, valor2);
				contenedor.put("d2"+key, descripcion2);
				
				valor3 = rs.getString(3);
				contenedor.put("cu"+key, valor3);
				
				descripcion3 = getPartidaCatalogo(conn, valor3);
				contenedor.put("d3"+key, descripcion3);
				
				monto = String.valueOf(new DecimalFormat("###,###.##").format(rs.getDouble(4)));
				contenedor.put("techo"+key, monto);
				key++;
			    }
			}else if(tabla.equals("tTechosPartidaUniNorm")){
			    while(rs.next()) {
				valor1 = rs.getString(1);
				contenedor.put("cp"+key, valor1);
				
				descripcion = getUnidadResponsable(conn, valor1);
				contenedor.put("d"+key, descripcion);
				
				valor2 = rs.getString(2);
				contenedor.put("ct"+key, valor2);
				
				descripcion2 = getTipoGasto(conn, valor2);
				contenedor.put("d2"+key, descripcion2);
				
				valor3 = rs.getString(3);
				contenedor.put("cu"+key, valor3);
				
				descripcion3 = getPartidaCatalogo(conn, valor3);
				contenedor.put("d3"+key, descripcion3);
				
				monto = String.valueOf(new DecimalFormat("###,###.##").format(rs.getDouble(4)));
				contenedor.put("techo"+key, monto);
				key++;
			    }
			}
		}finally{
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
			pstm=null;
			rs=null;
		}
		return contenedor;
	}
	
	public static String getTipoGasto(Connection conn, String cTipoGasto) throws SQLException{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String descripcion="";
		String cQuery="select tg.dTipoGasto from tCatalogoTipoGasto tg with (nolock) where tg.cTipoGasto = '"+cTipoGasto.trim()+"'" ;
		try{
			pstm = conn.prepareStatement(cQuery);
			rs = pstm.executeQuery();
			if (rs.next()){
			    descripcion = rs.getString(1);
			}
		}finally{
			if (pstm != null)
				pstm.close();
			if (rs != null){
				rs.close();
			}
			pstm = null;
			rs=null;
		}
		
		return descripcion.trim(); 
	}

	public static String getPartidaCatalogo(Connection conn, String cPartida) throws SQLException{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String descripcion="";
		String cQuery="select cp.dPartida from tCatalogoPartida cp with (nolock) where cp.cPartida = '"+cPartida.trim()+"'" ;
		try{
			pstm = conn.prepareStatement(cQuery);
			rs = pstm.executeQuery();
			if (rs.next()){
			    descripcion = rs.getString(1);
			}
		}finally{
			if (pstm != null)
				pstm.close();
			if (rs != null){
				rs.close();
			}
			pstm = null;
			rs=null;
		}
		
		return descripcion.trim(); 
	}

	public static String getUnidadResponsable(Connection conn, String cUR) throws SQLException{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String descripcion="";
		String cQuery="select cur.D_DESCRIPCION from tCatUnidadResponsable cur with (nolock) where cur.cUnidadResponsable = '"+cUR.trim()+"'" ;
		try{
			pstm = conn.prepareStatement(cQuery);
			rs = pstm.executeQuery();
			if (rs.next()){
			    descripcion = rs.getString(1);
			}
		}finally{
			if (pstm != null)
				pstm.close();
			if (rs != null){
				rs.close();
			}
			pstm = null;
			rs=null;
		}
		
		return descripcion.trim(); 
	}
	
	public static boolean isUNormativa(Connection conn,String cUnidadResponsable) throws SQLException{
		//ArrayList arrmObtenDatos = new ArrayList();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String esNormativa="";
		boolean normativa = false;
		String sQuery="SELECT nAlcance FROM tCatUnidadResponsable with (nolock) WHERE cUnidadResponsable = ?";
		//int i=0;
		try{
			pstm = conn.prepareStatement(sQuery);
			pstm.setString(1, cUnidadResponsable);
			rs = pstm.executeQuery();
			if (rs.next()) {
			    esNormativa = rs.getString(1);
			    if(esNormativa.equals("1"))
			    	normativa = true;
			}
		}finally{
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return normativa;
	}

	public static boolean borraDetalleAnteproyecto(Connection conn, String cUnidadResponsable, int nFolio) throws SQLException{
		boolean bBorrado=false;
		//String cQuery="DELETE FROM tAnteProyectoDetalle where  nFolioAnteProyecto = "+nFolio+" AND cUnidadResponsable = '" + cUnidadResponsable + "' ";
		String cQuery="DELETE FROM tAnteProyectoDetalle where  nFolioAnteProyecto = "+nFolio;
		PreparedStatement pstm = null;
		try{
			pstm = conn.prepareStatement(cQuery);
			bBorrado = pstm.execute();
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			if (pstm != null)
				pstm.close();
			pstm = null;
		}
		return bBorrado;
	}

	public static ArrayList<String> ValidaAnteProyectoEP(Connection conn, String cUnidadResponsable, int nFolio, String aEjercicioFiscal )throws SQLException{
		ArrayList<String> arrmMontosCalendario = new ArrayList<String>();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String cMEnsajeErrorEP="";
		String aEjercicioFiscalMasUno = String.valueOf(Integer.parseInt(aEjercicioFiscal)+1);
		String cQueryValida="select 'La secuencia '+CONVERT(varchar,nConsecutivo)+' Contiene una EP no valida' from tAnteProyectoDetalle d with (nolock) " 
				+" where d.nFolioAnteProyecto = "+nFolio 
				+"   AND d.aEjercicioFiscal = '"+aEjercicioFiscalMasUno+"'"
				+"   AND d.cUnidadResponsable = '"+cUnidadResponsable+"'"
				+"  AND ltrim(d.cClaveSiaff+'.'+d.cClaveInterna) not in (select ep from tCatalogoEPAnteProy with (nolock) )";
		try{
			pstm = conn.prepareStatement(cQueryValida);
			rs = pstm.executeQuery();
			while (rs.next()) {
				cMEnsajeErrorEP = rs.getString(1);
			    if(!"".equals(cMEnsajeErrorEP)){
			    	arrmMontosCalendario.add(cMEnsajeErrorEP);
			    }
			}
		}catch(SQLException s){
			s.printStackTrace();
		}finally{
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return arrmMontosCalendario;
	}

	public static ArrayList<String> ObtenTablaSubcuenta(Connection conn, String cSubCuenta, int nEjercicioFiscal, String cEP, Map mapValues ) throws SQLException{
		ArrayList arrTablasSubCuentaE = new ArrayList<>();
		String[] arrayComponentes = cEP.split("\\.");
		PreparedStatement pstmt = null;
		ResultSet rst = null;
		PreparedStatement pstmc = null;
		ResultSet rsc = null;
		PreparedStatement pstmd = null;
		ResultSet rsd = null;
		String cDescTabla="";
		String cNombreTabla="";
		String cNameCampo="";
		String cNameCampo1="";
		String cNameCampo2="";
		String cNameCampo3="";
		String cNameCampo4="";
		String cNameCampo5="";
		String cCampoLlave="";
		String cCampoDescrip="";
		String cComboDefinition="";
		String cActivaSelected="";
		int object_id=0;
		int i=0;
		int nConsecEP=0;
		String cSTRQuery="";
		String cQueryTablas="select t.EtiquetaCampo, t.NombreCatalogo, t.NombreCampo, s.object_id "
						+   "  from IMX_CATALOGO c with (nolock) "
						+   "     , tTipoSubcuentaConf t with (nolock)" 
						+   "	 , sys.tables s with (nolock) "
						+   " WHERE c.NOMBRE_CATALOGO = t.NombreCatalogo "
						+   "   AND t.NombreCatalogo = s.name "
						+   "   AND t.cSubcuenta = '"+cSubCuenta+"'"
						+   "   AND t.aEjercicioFiscal = '"+nEjercicioFiscal+"' "
						+   " order by t.nOrden ";
		String cQueryCampos="select c.name, c.max_length "
						+   "   from sys.columns c with (nolock) where c.object_id = ? "
						+   "   order by c.column_id ";
		try{
			
			pstmt = conn.prepareStatement(cQueryTablas);
			rst = pstmt.executeQuery();
			while (rst.next()) {
				ArrayList arrTablasSubCuenta = new ArrayList();
				cDescTabla = rst.getString(1);
				arrTablasSubCuenta.add(cDescTabla);
				cNombreTabla = rst.getString(2);
				cNameCampo = rst.getString(3);

				if (mapValues.get(cNameCampo) != null )
					cActivaSelected=mapValues.get(cNameCampo).toString(); // obtiene el valor del Map
				else
					cActivaSelected="";
				
				object_id = rst.getInt(4);
			    if(!"".equals(cDescTabla)){
					ArrayList<ArrayList<String>> arrCamposTablasSubCuenta = new ArrayList<ArrayList<String>>();
					pstmc = conn.prepareStatement(cQueryCampos);
					pstmc.setInt(1, object_id);
					rsc = pstmc.executeQuery();
					i=0;
					while (rsc.next()) {
						i++;
						if (i == 1 ){
							cNameCampo1=rsc.getString(1);
							cSTRQuery="SELECT "+cNameCampo1 ;
						}else if (i==2){
							cNameCampo2=rsc.getString(1);
							cSTRQuery+=", "+cNameCampo2;
						}else if (i==3){
							cNameCampo3=rsc.getString(1);
							cSTRQuery+=", "+cNameCampo3;
						}else if (i==4){
							cNameCampo4=rsc.getString(1);
							cSTRQuery+=", "+cNameCampo4;
						}else if (i==5){
							cNameCampo5=rsc.getString(1);
							cSTRQuery+=", "+cNameCampo5;
						}else{
							break;
						}
					}
					cSTRQuery+=" FROM " + cNombreTabla + " ";
					if ("cUnidadResponsable".equals(cNameCampo) && "Unidad Normativa".equals(cDescTabla)){
						cSTRQuery+=" WHERE nAlcance = 1 ";
					}
					pstmd = conn.prepareStatement(cSTRQuery);
					rsd = pstmd.executeQuery();
					int d=0;
					ArrayList<String> arrDataTablas = new ArrayList<String>();
					while (rsd.next()) {
						if ("cUnidadResponsable".equals(cNameCampo)){
							cCampoLlave = rsd.getString("cUnidadResponsable");
							cCampoDescrip = cCampoLlave + " " + rsd.getString("D_DESCRIPCION");
						} else if ("aEjercicioFiscal".equals(cNameCampo)) {
							cCampoLlave = rsd.getString("aEjercicioFiscal");
							int nActivo = rsd.getInt(2);
							if (nActivo == 1)
								cCampoDescrip = cCampoLlave + " Activo" ;
							else
								cCampoDescrip = cCampoLlave + " Inactivo" ;
						}else{
							cCampoLlave = rsd.getString(cNameCampo).trim();
							if(i > 1){
								cCampoDescrip = cCampoLlave.trim() + " " + rsd.getString("d"+cNameCampo.substring(1));
							}
						}
						String cSelected = "";
						if (!cActivaSelected.isEmpty()){
							//if ( cCampoLlave.trim() == arrayComponentes[nConsecEP]){
							if (cActivaSelected.equals(cCampoLlave)){
								cSelected= " selected ";
							}else{
								cSelected= " ";
							}
						}
						if(i > 1){
							cComboDefinition="<option value=\""+cCampoLlave+"\" "+ cSelected +">"+cCampoDescrip+"</option>";
						}else{
							cComboDefinition="<option value=\""+cCampoLlave+"\" "+ cSelected +">"+cCampoLlave+"</option>";
						}
						arrDataTablas.add(cComboDefinition);
						d++;
					}
					arrCamposTablasSubCuenta.add(arrDataTablas);
					arrTablasSubCuenta.add(arrCamposTablasSubCuenta);
					arrCamposTablasSubCuenta=null;
					arrTablasSubCuentaE.add(arrTablasSubCuenta);
					arrTablasSubCuenta=null;
					nConsecEP++;
			    }
			}
			
		}finally{
			if (pstmt != null) {
				pstmt.close();
			}
			if (rst != null) {
				rst.close();
			}
			if (pstmc != null) {
				pstmc.close();
			}
			if (rsc != null) {
				rsc.close();
			}
			pstmc = null;
			rsc = null;
			if (pstmd != null) {
				pstmd.close();
			}
			if (rsd != null) {
				rsd.close();
			}
			pstmd = null;
			rsd = null;
		}
		return arrTablasSubCuentaE;
	}

	public static ArrayList<AnteProyectoAut> getCAnteProyectoAut(Connection conn,String cUnidadResponsable) throws SQLException{
		ArrayList<AnteProyectoAut> res = new ArrayList<AnteProyectoAut>();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		//boolean normativa = false;
		String sQuery="SELECT nConsecutivo,nFolioAnteProyecto,nFolioAnteProyectoAut,aEjercicioFiscal,cClaveSiaff,cClaveInterna,cUnidadResponsable,mCalculado as montoAnualAut FROM tAnteProyectoDetalleAut with(nolock) WHERE cUnidadEjecutora = ?";
		//int i=0;
		try{
			pstm = conn.prepareStatement(sQuery);
			pstm.setString(1, cUnidadResponsable);
			rs = pstm.executeQuery();
			AnteProyectoAut aut;
			while (rs.next()) {
				aut = new AnteProyectoAut();
				aut.setD_NConsecutivo(String.valueOf(rs.getInt("nConsecutivo")));
				aut.setD_NFolioAnteProyecto(String.valueOf(rs.getInt("nFolioAnteProyecto")));
				aut.setFolioAnteProyectoAut(String.valueOf(rs.getInt("nFolioAnteProyectoAut")));
				aut.setD_CClaveSIAFF(rs.getString("cClaveSiaff"));
				aut.setD_CClaveInterna(rs.getString("cClaveInterna"));
				aut.setD_CUnidadEjecutora(rs.getString("cUnidadResponsable"));
				aut.setD_MCalculado(rs.getString("montoAnualAut"));
				aut.setEjercicioFiscal(rs.getString("aEjercicioFiscal"));
				res.add(aut);
			}
		}finally{
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return res;
	}

	public static boolean insertaCAnteProyectoAut(Connection conn, ArrayList<AnteProyectoAutCalendario> ac, String cCentroContable) throws SQLException{
		boolean retVar=false;
		PreparedStatement pstm = null;
		//ResultSet rs = null;
		AnteProyectoAutCalendario apa;
		int i;
		String cQueryCrea="INSERT INTO tAnteProyectoAUTCalendario (nConsecutivo, nFolioAnteProyectoAut, aEjercicioFiscal, cUnidadResponsable, cClaveSiaff, cClaveInterna, cCentroContable, nFolioAnteProyecto, mAnualAutorizado, mEnero, mFebrero, mMarzo, mAbril, mMayo, mJunio, mJulio, mAgosto, mSeptiembre, mOctubre, mNoviembre, mDiciembre) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )"; 
		try{	
			pstm = conn.prepareStatement(cQueryCrea);
			for(i=0;i<ac.size();i++){
				apa = ac.get(i);
				pstm.setInt(1, Integer.parseInt(apa.getNConsecutivo()));
				pstm.setInt(2, Integer.parseInt(apa.getNFolioAnteProyectoAut()));
				pstm.setString(3, apa.getAEjercicioFiscal());
				pstm.setString(4, apa.getCUnidadResponsable());
				pstm.setString(5, apa.getCClaveSiaff());
				pstm.setString(6, apa.getCClaveInterna());
				pstm.setString(7, cCentroContable);
				pstm.setInt(8, Integer.parseInt(apa.getNFolioAnteProyecto())); 
				pstm.setDouble(9, Double.parseDouble(apa.getMAnualAutorizado()));
				pstm.setDouble(10, Double.parseDouble(apa.getMEnero()));
				pstm.setDouble(11, Double.parseDouble(apa.getMFebrero()));
				pstm.setDouble(12, Double.parseDouble(apa.getMMarzo()));
				pstm.setDouble(13, Double.parseDouble(apa.getMAbril()));
				pstm.setDouble(14, Double.parseDouble(apa.getMMayo()));
				pstm.setDouble(15, Double.parseDouble(apa.getMJunio()));
				pstm.setDouble(16, Double.parseDouble(apa.getMJulio()));
				pstm.setDouble(17, Double.parseDouble(apa.getMAgosto()));
				pstm.setDouble(18, Double.parseDouble(apa.getMSeptiembre()));
				pstm.setDouble(19, Double.parseDouble(apa.getMOctubre()));
				pstm.setDouble(20, Double.parseDouble(apa.getMNoviembre()));
				pstm.setDouble(21, Double.parseDouble(apa.getMDiciembre()));
				retVar = pstm.execute();
			}
		}finally{
			if (pstm != null)
				pstm.close();
			pstm = null;
		}
		return retVar;
	}
	
	public static boolean getExisteDetalleAut(Connection conn,AnteProyectoAutCalendario apac) throws SQLException{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		boolean existe = false;
		String sQuery="SELECT COUNT(*) FROM tAnteProyectoDetalleAut with(nolock) WHERE nConsecutivo = ? AND nFolioAnteProyectoAut = ? AND nFolioAnteProyecto = ? AND aEjercicioFiscal = ? AND cUnidadEjecutora = ? AND cClaveSiaff = ? AND cClaveInterna = ? AND mCalculado = ?";
		//int i=0;
		try{
			pstm = conn.prepareStatement(sQuery);
			pstm.setInt(1, new Double(apac.getNConsecutivo()).intValue());
			pstm.setString(2, apac.getNFolioAnteProyectoAut());
			pstm.setString(3, String.valueOf(new Double(apac.getNFolioAnteProyecto()).intValue()));
			pstm.setString(4, apac.getAEjercicioFiscal());
			pstm.setString(5, apac.getCUnidadResponsable());
			pstm.setString(6, apac.getCClaveSiaff());
			pstm.setString(7, apac.getCClaveInterna().trim());
			pstm.setString(8, apac.getMAnualAutorizado());
			rs = pstm.executeQuery();
			if (rs.next()) {
				existe = true;
			}
		}finally{
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return existe;
	}

	public static boolean getRepetidoCalendario(Connection conn,AnteProyectoAutCalendario apac) throws SQLException{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		boolean repetido = false;
		int cuenta=0;
		String sQuery="SELECT COUNT(*) FROM tAnteProyectoAUTCalendario with(nolock) WHERE nConsecutivo = ? AND nFolioAnteProyectoAut = ? AND nFolioAnteProyecto = ?";
		//int i=0;
		try{
			pstm = conn.prepareStatement(sQuery);
			pstm.setInt(1, new Double(apac.getNConsecutivo()).intValue());
			pstm.setString(2, apac.getNFolioAnteProyectoAut());
			pstm.setString(3, String.valueOf(new Double(apac.getNFolioAnteProyecto()).intValue()));
			rs = pstm.executeQuery();
			if (rs.next()) {
				cuenta = rs.getInt(1);
			}
			if(cuenta!=0)
				repetido = true;
			if(repetido){
				String uQuery="UPDATE tAnteProyectoAUTCalendario SET mEnero=?,mFebrero=?,mMarzo=?,mAbril=?,mMayo=?,mJunio=?,mJulio=?,mAgosto=?,mSeptiembre=?,mOctubre=?,mNoviembre=?,mDiciembre=? WHERE nConsecutivo = ? AND nFolioAnteProyecto = ? AND nFolioAnteProyectoAut = ? AND cClaveSiaff = ? AND cClaveInterna = ? AND mAnualAutorizado = ?";
				pstm = conn.prepareStatement(uQuery);
				pstm.setDouble(1, Double.parseDouble(apac.getMEnero()));
				pstm.setDouble(2, Double.parseDouble(apac.getMFebrero()));
				pstm.setDouble(3, Double.parseDouble(apac.getMMarzo()));
				pstm.setDouble(4, Double.parseDouble(apac.getMAbril()));
				pstm.setDouble(5, Double.parseDouble(apac.getMMayo()));
				pstm.setDouble(6, Double.parseDouble(apac.getMJunio()));
				pstm.setDouble(7, Double.parseDouble(apac.getMJulio()));
				pstm.setDouble(8, Double.parseDouble(apac.getMAgosto()));
				pstm.setDouble(9, Double.parseDouble(apac.getMSeptiembre()));
				pstm.setDouble(10, Double.parseDouble(apac.getMOctubre()));
				pstm.setDouble(11, Double.parseDouble(apac.getMNoviembre()));
				pstm.setDouble(12, Double.parseDouble(apac.getMDiciembre()));
				pstm.setInt(13, Integer.parseInt(apac.getNConsecutivo()));
				pstm.setInt(14, Integer.parseInt(apac.getNFolioAnteProyecto()));
				pstm.setInt(15, Integer.parseInt(apac.getNFolioAnteProyectoAut()));
				pstm.setString(16, apac.getCClaveSiaff());
				pstm.setString(17, apac.getCClaveInterna());
				pstm.setDouble(18, Double.parseDouble(apac.getMAnualAutorizado()));
				pstm.execute();
			}
		}finally{
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return repetido;
	}
	
	public static ArrayList<CatEPAnteP> getCatEPAnteProy(Connection conn,String query) throws SQLException{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		ArrayList<CatEPAnteP> cateps = new ArrayList<CatEPAnteP>();
		try{
			pstm = conn.prepareStatement(query);
			rs = pstm.executeQuery();
			while (rs.next()) {
				CatEPAnteP catep = new CatEPAnteP();
				catep.setaEjercicioFiscal(rs.getString("aEjercicioFiscal"));
				catep.setnClaveCNA(rs.getString("nClaveCNA"));
				catep.setEP(rs.getString("EP"));
				catep.setClaveSiaff(rs.getString("ClaveSIAFF"));
				catep.setClaveInterna(rs.getString("ClaveInterna"));
				catep.setcRamoEP(rs.getString("cRamoEP"));
				catep.setcUnidadResponsableEP(rs.getString("cUnidadResponsableEP"));
				catep.setcGrupoFuncional(rs.getString("cGrupoFuncional"));
				catep.setcFuncion(rs.getString("cFuncion"));
				catep.setcSubFuncion(rs.getString("cSubFuncion"));
				catep.setcProgramaGeneral(rs.getString("cProgramaGeneral"));
				catep.setcActividadInstitucional(rs.getString("cActividadInstitucional"));
				catep.setcProgramaPresupuestario(rs.getString("cProgramaPresupuestario"));
				catep.setcPartida(rs.getString("cPartida"));
				catep.setcTipoGasto(rs.getString("cTipoGasto"));
				catep.setcFuenteFinanciamiento(rs.getString("cFuenteFinanciamiento"));
				catep.setcEntidadFederativa(rs.getString("cEntidadFederativa"));
				catep.setcCartera(rs.getString("cCartera"));
				catep.setcUnidadNormativa(rs.getString("cUnidadNorativa"));
				catep.setcUnidadEjecutora(rs.getString("cUnidadEjecutora"));
				catep.setcRamo(rs.getString("cRamo"));
				cateps.add(catep);
			}
		}finally{
			if (pstm != null) {
				pstm.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return cateps;
	}

}
