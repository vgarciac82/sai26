package com.syc.ejercido.pagado;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.jfree.util.Log;
import com.syc.contable.core.AplicacionContable;
import com.syc.dsmngr.DataSourceManager;

public class FacturaCarga  extends DataSourceManager{
	private static Logger log = Logger.getLogger(AplicacionContable.class);
	
	public String enviaRuta(InputStream in, String tipoArchivo)	throws FileNotFoundException {

		
		
		BufferedReader brr = new BufferedReader(new InputStreamReader(in));
		
		String sCadenas = "";
		
		String valorReturn = "";
		String szError = "";
		Connection conn = null;
		
		int nRegistro = 0;

		try {
			
			int nRegistrosCorrectos = 0;
			String noGuardado = "0";
			
			conn = getConnection();
			
									
			while ((sCadenas = brr.readLine()) != null) {
				nRegistro = nRegistro + 1;
				
				if (nRegistro > 1){
					
					
				
					String[] celdas = sCadenas.split(",");
					
					if(tipoArchivo.equals("cargaArchivo")){
																		
						String NoContrato = celdas[0].trim(); 			// Debe existir en la Tabla dContrato
						String Prestamo = celdas[1].trim();				// Debe existir en la Tabla dContrato y estar en el mismo registro del id_Contrato					
						String NoFactura = celdas[2].trim();  			// Debe tener cuando mucho 20 caracteres
						String EjercicioFiscal = celdas[3].trim();
						String FechaFactura = celdas[4].trim();
						String FechaPoliza = celdas[5].trim();
						String ImporteFederal = celdas[6].trim();
						String ImporteEstatal = celdas[7].trim();
						String ImporteMunicipal = celdas[8].trim();
						String ImporteOtros = celdas[9].trim();
						String Estimacion = celdas[10].trim();  		// Debe tener cuando mucho 10 caracteres
						String Concepto = celdas[11].trim();  			// Debe tener cuando mucho 40 caracteres

						
						// Validaciones para Carga
						String existe = buscaNoContrato( conn, NoContrato, Prestamo );
						PreparedStatement id_Contrato = conn.prepareStatement("SELECT c.id_Contrato  FROM dContrato AS c WITH(NOLOCK) WHERE c.NoContrato = ? ");
						id_Contrato.setString(1, NoContrato);
						ResultSet rs1 = id_Contrato.executeQuery();
						String idC = "";
						if(rs1.next()){
							idC = rs1.getString("id_Contrato");
							}
						String existeNoFactura = existeNoFactura(conn, NoFactura, idC);
						String longitudNoFactura = "";
						if( NoContrato.length() > 50){
							longitudNoFactura = "Longitud de la Factura Mayor a 50";
							System.out.println(longitudNoFactura);
						}
						String longitudEstimacion = "";
						if( Estimacion.length() > 10){
							longitudEstimacion = "Longitud de la Estimacion Mayor a 10";
							System.out.println(longitudEstimacion);
						}
						String longitudConcepto = "";
						if( Concepto.length() > 40){
							longitudConcepto = "Longitud del Concepto Mayor a 40";
							System.out.println(longitudConcepto);
						}
						
						if("S&iacute; Existe Pr&eacute;stamo-Contrato".equals(existe) && "".equals(longitudNoFactura) && "".equals(longitudEstimacion) && "".equals(longitudConcepto) && "No Existe Factura".equals(existeNoFactura)){

							String status = insertaLineaComprometidoEnc( conn, NoContrato,Prestamo,NoFactura,EjercicioFiscal,FechaFactura,FechaPoliza,ImporteFederal,ImporteEstatal,ImporteMunicipal,ImporteOtros,Estimacion,Concepto);

							nRegistrosCorrectos = nRegistrosCorrectos + 1;
							if(status.equals("noGuardado")){
								
								noGuardado += nRegistro;
								noGuardado += ",";
							}
						
						}
						else
						{
							szError += "Error en el renglon " + (nRegistro) + " ";
							if(!"S&iacute; Existe Pr&eacute;stamo-Contrato".equals(existe)) szError = szError + existe;
							if(!"No Existe Factura".equals(existeNoFactura)) szError = szError + ", " + existeNoFactura;
							if(!"".equals(longitudNoFactura)) szError = szError + ", " + longitudNoFactura;
							if(!"".equals(longitudEstimacion)) szError = szError + ", " + longitudEstimacion;
							if(!"".equals(longitudConcepto)) szError = szError + ", " + longitudConcepto + "\n";
							szError += "; ";
							
						}
					}
				}	
				
				
			}

			nRegistro = nRegistro - 1;
			int diferencia = nRegistro  - nRegistrosCorrectos;
			valorReturn += "Registros Totales: = " + nRegistro + " \n";
			valorReturn += "Registros Correctos: = " + nRegistrosCorrectos + " \n";
			valorReturn += "Registros con Error: = " + diferencia  + " \n";
			//+ "Error en linea(s): = " + noGuardado 
			valorReturn += " \n" + szError;
			
			conn.commit();
		} catch (Exception se) {
			
				log.error("Error: " + se);
				se.printStackTrace();
				valorReturn = " error en el renglon " + nRegistro + ", datos incompletos.";
			
			try {
				
				conn.rollback();
				
			} catch (Exception exc) {
				
				Log.warn("Error: cerrando rollback enviaRuta " +exc);
				
			}	
				
		} finally {
			
				try {
					if (brr != null)
						brr.close();
				} catch (Exception exc) {
					Log.warn("Cerrando BufferedReader", exc);
				}
				try {
					
					if (conn != null){ conn.close(); }
						
				} catch (Exception exc) {
					Log.warn("Cerrando BufferedReader", exc);
				}
				
				brr = null;
				conn = null;
		}
		
		return valorReturn;
	}
	
	public String insertaLineaComprometidoEnc(Connection conn,String NoContrato,String Prestamo,String NoFactura,String EjercicioFiscal,String FechaFactura,String FechaPoliza,String ImporteFederal,String ImporteEstatal,String ImporteMunicipal,String ImporteOtros,String Estimacion,String Concepto){
		
		String respuesta = "noGuardado", idP = "", idC = "";
		PreparedStatement ps = null, pscaso = null, psInserta = null, id_contrato = null;
		ResultSet rs = null, rs1 = null;
		
		try{
			
			id_contrato = conn.prepareStatement("SELECT c.id_Contrato, p.id_prestamo  FROM dContrato AS c WITH(NOLOCK), dPrestamo AS p WITH(NOLOCK) WHERE c.NoContrato = ? AND p.NumeroPrestamo = ? ");
			id_contrato.setString(1, NoContrato);
			id_contrato.setString(2, Prestamo);
			rs1 = id_contrato.executeQuery();
			
			if(rs1.next()){
				idC = rs1.getString("id_contrato");
				idP = rs1.getString("id_prestamo");
				}
			
			String n = "INSERT INTO dFactura (NoContrato,Prestamo,NoFactura,EjercicioFiscal,FechaFactura,FechaPoliza,ImporteFederal,ImporteEstatal,ImporteMunicipal,ImporteOtros,Estimacion,Concepto,FechaUltimoMovimiento,TipoCarga ) VALUES ('"+idC+"','"+idP+"','"+NoFactura+"','"+EjercicioFiscal+"','"+FechaFactura+"','"+FechaPoliza+"','"+ImporteFederal+"','"+ImporteEstatal+"','"+ImporteMunicipal+"','"+ImporteOtros+"','"+Estimacion+"','"+Concepto+"',getdate(), 'A')";
			System.out.println(n);
			
			ps = conn.prepareStatement("INSERT INTO dFactura (id_Contrato,id_prestamo,NumFactura,EjercicioFiscal,FechaFactura,FechaPoliza, ImporteFederal,ImporteEstatal,ImporteMunicipal,ImporteOtros,Estimacion,Concepto,FechaUltimoMovimiento,TipoCarga ) VALUES ('"+idC+"','"+idP+"','"+NoFactura+"','"+EjercicioFiscal+"','"+FechaFactura+"','"+FechaPoliza+"','"+ImporteFederal+"','"+ImporteEstatal+"','"+ImporteMunicipal+"','"+ImporteOtros+"','"+Estimacion+"','"+Concepto+"',getdate(), 'A')");
			ps.executeUpdate();
			
			
			
			respuesta = "guardado";

			
		}catch(Exception e){
			
			log.error("Error Guardar Compromiso Encabezado: "+e);
			
		}finally{
			
			try{
				
				if(ps != null){ ps.close(); }
				
				if(pscaso != null ){ pscaso.close(); }
				if(psInserta != null) {psInserta.close();}
				if(rs != null) {rs.close();}
				if(rs1 != null) {rs1.close();}
				
			}catch(Exception ef){
				
				log.warn("Error: cerrando statement: "+ef);
				
			}
			
			ps = null;
			pscaso = null;
			psInserta = null;
			rs = null;
			rs1 = null;
		}
		
		return respuesta;
	}
	
	public String buscaNoContrato( Connection conn ,String NoContrato,String Prestamo ) throws SQLException {
		
		String respuesta = "No Existe Prestamo-Contrato";
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try{
			
			ps = conn.prepareStatement("SELECT c.NoContrato, p.NumeroPrestamo FROM dContrato AS c WITH(NOLOCK), dPrestamo AS p WITH(NOLOCK) WHERE c.NoContrato = ? AND NumeroPrestamo = ?");
			ps.setString(1, NoContrato);
			ps.setString(2, Prestamo);
			rs = ps.executeQuery();
			
			if(rs.next()){
				
				respuesta = "S&iacute; Existe Pr&eacute;stamo-Contrato";
				}
			
			System.out.println("RES: "+respuesta);
		}catch(Exception e){
			
			log.warn("Error: Buscar Compromiso Aplicado: " +e);
			
		}finally{
			
			try{
				
				if(ps != null){ ps.close(); }
				if(rs != null){ rs.close(); }
				
			}catch(Exception e){
				
				log.warn("Error: cerrando statement: " +e);
				
			}
			
			ps = null;
			rs = null;
		}

		return respuesta;
	}
		public String existeNoFactura ( Connection conn,String NoFactura, String idC ) throws SQLException {
			
			String respuestaNoFactura = "No Existe Factura";
			PreparedStatement ps = null;
			ResultSet rs = null;
			
			try{
				
				ps = conn.prepareStatement("SELECT * FROM dFactura WITH(NOLOCK) WHERE NumFactura = ? AND id_Contrato = ?");
				ps.setString(1, NoFactura);
				ps.setString(2, idC);
				rs = ps.executeQuery();
				
				if(rs.next()){
					
					respuestaNoFactura = "Ya Existe ese Numero de Factura asociado a ese Contrato";
					}
				
				System.out.println("RES: "+respuestaNoFactura+": "+NoFactura);
			}catch(Exception e){
				
				log.warn("Error: Buscar Compromiso Aplicado: " +e);
				
			}finally{
				
				try{
					
					if(ps != null){ ps.close(); }
					if(rs != null){ rs.close(); }
					
				}catch(Exception e){
					
					log.warn("Error: cerrando statement: " +e);
					
				}
				
				ps = null;
				rs = null;
			}
	
			return 	respuestaNoFactura;
		}
}
