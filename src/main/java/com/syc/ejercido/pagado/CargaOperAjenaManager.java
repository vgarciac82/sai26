package com.syc.ejercido.pagado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.jfree.util.Log;

import com.syc.contable.core.AplicacionContable;
import com.syc.dsmngr.DataSourceManager;

public class CargaOperAjenaManager extends DataSourceManager {
	private static Logger log = Logger.getLogger(AplicacionContable.class);
	
	public int folioTemp(Connection conn) throws SQLException{
		
		int folio = 0;
		PreparedStatement ps = null;
		ResultSet rs = null;
	
		try{						
			
			ps = conn.prepareStatement("SELECT ISNULL(MAX(DISTINCT folioTempGral)+1,1) AS folio FROM tpasivosContingentesLaborales_temp");
			rs = ps.executeQuery();
			
			if(rs.next()){				
				folio = Integer.parseInt(rs.getString("folio"),10);				
			}
			
		}catch(Exception e){			
			log.error("Error: "+e);
			e.printStackTrace();
			folio = 0;			
		}finally {
			
			try{
				
				if(ps != null){ ps.close(); }
				if(rs != null){ rs.close(); }
				
				
			}catch(Exception pst){
				
				Log.warn("Cerrando PreparedStatement");
			}						
			
			ps = null;			
			rs = null;
			
		}
		
		return folio;		
	}		
	
	
	public String validaRfc(Connection conn, String rfc, String pc) throws SQLException{
		
		String valor = "noExiste";
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try{
			
			ps = conn.prepareStatement("SELECT * FROM tpasivosContingentes WITH (NOLOCK) WHERE cRFC = ? AND cSubcuenta = ? AND esPatrimonial = 0 ");
			ps.setString(1, rfc);
			ps.setString(2, pc);
			rs = ps.executeQuery();
			
			if(rs.next()){				
				valor = "existe";
			}
		
		}catch(Exception e){
			
			log.error("Error al Validar RFC : "+ e);
		}finally {
			
			try{
				
				if(ps != null){ ps.close(); }
				if(rs != null){ rs.close(); }
				
				
			}catch(Exception pst){
				
				Log.warn("Cerrando PreparedStatement");
			}						
			
			ps = null;			
			rs = null;
			
			conn = null;
		}
	
	
		return valor;
	}
	
	public String validaMes(Connection conn, String rfc, String pc, String mesP) throws SQLException{	
		
		String valor = "noExiste";
		PreparedStatement ps1 = null, ps2 = null;
		ResultSet rs1 = null, rs2 = null;
		int folio = 0;
		
		try{
			
			ps1 = conn.prepareStatement("SELECT idPasivoContingente FROM tpasivosContingentes WITH (NOLOCK) WHERE cRFC = ? AND cSubcuenta = ? AND esPatrimonial = 0 ");
			ps1.setString(1, rfc);
			ps1.setString(2, pc);
			rs1 = ps1.executeQuery();
			
			if(rs1.next()){				
				folio = Integer.parseInt(rs1.getString("idPasivoContingente"),10);				
			}
			
			ps2 = conn.prepareStatement(" SELECT cmes FROM tpasivosContingentesLaborales WITH (NOLOCK) where idPasivoContingente = " + folio + " AND cMes = ?");
			ps2.setString(1, mesP);
			rs2 = ps2.executeQuery();
			
			if(rs2.next()){				
				valor = "existe";
			}
		
		}catch(Exception e){
			
			log.error("Error al Validar mes de captura : "+ e);
		}finally {
			
			try{
				
				if(ps1 != null){ ps1.close(); }
				if(rs1 != null){ rs1.close(); }
				if(ps2 != null){ ps2.close(); }
				if(rs2 != null){ rs2.close(); }
				
				
			}catch(Exception pst){
				
				Log.warn("Cerrando PreparedStatement");
			}						
			
			ps1 = null;			
			rs1 = null;
			ps2 = null;			
			rs2 = null;
			
			conn = null;
		}
		
		return valor;
	}
	
	public String insertaTemp(Connection conn, String rfc, String pc, String mes, double mImporteNetoEnc, String fAplicacion, String ur, String cEjecicicioFiscal, String login, String estatus, int fTempGral) throws SQLException {
		
		String valor = "noGuardado";
		int IDPasivoContingente = 0; 
		
		PreparedStatement psID = null, psEnc;
		ResultSet rsID = null;
		
		try{
			
			psID = conn.prepareStatement("SELECT idPasivoContingente FROM tpasivosContingentes where cRFC = ? AND cSubcuenta = ? AND esPatrimonial = 0");
			psID.setString(1, rfc);
			psID.setString(2, pc);
			rsID = psID.executeQuery();
			
			if(rsID.next()){
				
				IDPasivoContingente = rsID.getInt("idPasivoContingente");
			}
			
			System.out.println("Insertando en tabla temporal " + fTempGral + "," + IDPasivoContingente + "," + rfc + "," + mes + "," + mImporteNetoEnc + "," + estatus + "," + fAplicacion);
			psEnc = conn.prepareStatement(" INSERT INTO tpasivosContingentesLaborales_temp " +
										  "		  ( folioTempGral,  idPasivoContingente, 		cRFC, 	 cMes, 		mImporte, 			  estatus, 	 	ur, 		usuario,	  fechaCaptura ) " +
										  " VALUES( "+fTempGral+",  "+IDPasivoContingente+", '"+rfc+"', "+mes+", '"+mImporteNetoEnc+"','"+estatus+"', '"+ur+"' , '"+login+"' , '"+fAplicacion+"' )");
			psEnc.executeUpdate();
			valor = "guardado";
			
		}catch(Exception e){
			
			log.error("Error al guardar el dato "+e);
		}
			
		return valor;
	}
	
	public String insertaPasivosLaborales(Connection conn, String fTempGral) throws SQLException {
		
		String valor = "noGuardado"; 
		
		PreparedStatement psEnc;
		
		try{
			
			System.out.println("Insertando en tabla final");
			psEnc = conn.prepareStatement(" INSERT INTO tpasivosContingentesLaborales " +
										  "	SELECT folioTempGral, idPasivoContingente, cMes, mImporte FROM tpasivosContingentesLaborales_temp WHERE folioTempGral =  " + fTempGral);										  
						
			psEnc.execute();
			valor = "guardado";
			
		}catch(Exception e){
			
			log.error("Error al guardar el dato " + e);
		}
			
		return valor;
	}

}
