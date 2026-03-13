package com.syc.reportes.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.syc.contable.AccountingEngine;
import com.syc.contable.core.CompromisoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class CargaArchivoManager {
				
	private static final Logger	log	= Logger.getLogger( CompromisoManager.class );
	
	public static boolean insertArchivo( Connection conn,java.sql.Date fExpTmp, java.sql.Date fAppTmp, int folio, String descripcion, String codTran, String sucursal, String deposito, String retiro, String saldo ) throws Exception {
		PreparedStatement pstmnt = null;
		boolean insertReg = false;

		try {
			// falta cambiar la tabla del insert
			String queryInsert = "INSERT INTO tArchivoEjercidoPagado (dfechaOperacion, dfecha, nreferencia, cdescripcion, ncodtransac, nsucursal, mdepositos, mretiros, msaldo ) values('" 
						+ fExpTmp  + "' ,'" + fAppTmp +  "', " + folio + " ,'" + descripcion + "','" + codTran  + "','" + sucursal + "','" + deposito + "','" + retiro + "','" + saldo + "')";

			pstmnt = conn.prepareStatement( queryInsert );

			int reg = pstmnt.executeUpdate();

			if ( reg == 1 ) {
				insertReg = true;
			} else {
				insertReg = false;
			}
			
		} finally {

			CloseObject.closeObject( pstmnt );
		}
		return insertReg;
	}

	public static boolean insertArchivoTaxis( Connection conn, java.sql.Date fAplicacion, String idEmpleado, String destino, String ur, String idComision, String folioTaxi, String monto) throws Exception {
		PreparedStatement pstmnt = null;
		boolean insertReg = false;

		try {
			// falta cambiar la tabla del insert
			String queryInsert = "INSERT INTO tLayoutTaxis (fAplicacion, nIdEmpleado, cDestino, cUnidadResponsable, nidComision, cFolioTaxi, mMonto ) VALUES('" 
						+ fAplicacion  + "' ," + idEmpleado +  ",'" + destino + "','" + ur + "','" + idComision  + "','" + folioTaxi + "','" + monto.trim() + "')";

			pstmnt = conn.prepareStatement( queryInsert );

			int reg = pstmnt.executeUpdate();

			if ( reg == 1 ) {
				insertReg = true;
			} else {
				insertReg = false;
			}
			
		} finally {

			CloseObject.closeObject( pstmnt );
		}
		return insertReg;
	}


	public static boolean borraTabla( Connection conn ) throws Exception {
		boolean borrado = false;
		PreparedStatement pstmnt = null;

		try {
			String querySelect = "DELETE tArchivoEjercidoPagado";
			pstmnt = conn.prepareStatement( querySelect );
			pstmnt.executeUpdate();
			borrado = true;
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			CloseObject.closeObject( pstmnt );
		}
		return borrado;
	}

	public static String insertaPagado( Connection conn, int folio, Date fAplicacion ) throws Exception {
		PreparedStatement pstmnt = null,  pstmUpPagado = null, pstmSeqPagado = null, pstInsertar = null ;
		ResultSet rs = null,  rs3 = null;
		String strFolioPagado= "";
		SimpleDateFormat fechaApp = new SimpleDateFormat("yyyy/MM/dd"); 
		String fecha = fechaApp.format(fAplicacion);
		String queryEjercido = "SELECT  * FROM v_aplicarejercidopagadoEncabezado WITH(NOLOCK) WHERE cTipoPago = 'PAGODIVERSO' and nfolio = ?";
		try {
				pstmnt = conn.prepareStatement( queryEjercido );
				pstmnt.setInt( 1, folio );
				rs = pstmnt.executeQuery();
			
				if(rs.next()){
					String tipoPago = rs.getString( "cTipoPago" );
					String canoContrarrecibo = rs.getString( "caNoContrarrecibo" );
					String descripcion = rs.getString( "cDescripcionPoliza" );
					String unidad = rs.getString( "cUnidadResponsableContable" );
					
			
						pstmUpPagado = conn.prepareStatement("UPDATE CF_SEQUENCE WITH (ROWLOCK) SET seq_value = seq_value + 1 WHERE seq_name = 'PAGADO' ");
						pstmUpPagado.executeUpdate();
							
						pstmSeqPagado = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'PAGADO' ");
						rs3 = pstmSeqPagado.executeQuery();
							
						if(rs3.next()){					
								
							strFolioPagado = rs3.getString("seq_value");
										
						}
						
						String queryEjer = "INSERT INTO tPagadoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza, "
								+ " U_LOGIN, fAplicacion, nFolioPolizaCancelacion, cDescripcionPoliza, cUnidadResponsableContable, nFolioSICOP, cRamo, "
								+ " cIdUsuarioCaptura, FechaAplicacionSicop, FechaPagoSicop, SolicitudPago,NumeroProceso,nFolioSIAFF,FechaPagado, nFolioPagado)"
								+ " VALUES ('" + tipoPago + "', " + folio + ", RTRIM(LTRIM('" + canoContrarrecibo + "')), 0, 'EG', 'administrador', CAST ('"+ fecha + "' AS DATE), 0, '" 
								+ descripcion + "','" + unidad + "','-1', '16', 'administrador', getdate(), getdate(), 9999, 9999, 9999, getdate()," + strFolioPagado + ")" ;
					
					pstInsertar = conn.prepareStatement( queryEjer );
					int pagado = pstInsertar.executeUpdate();
					 
					if (pagado == 1){
						int detalle = 	insertaDetalle ( conn, folio, strFolioPagado, "PAGADO");
						
						if (!(detalle == 1)){
							strFolioPagado = "0";
						}
					} else
						strFolioPagado = "0";
				}	
						
						
		} finally {

			CloseObject.closeObject( rs );
			CloseObject.closeObject( rs3 ); 
			CloseObject.closeObject( pstmUpPagado );
			CloseObject.closeObject( pstmSeqPagado );
			CloseObject.closeObject( pstInsertar );
			CloseObject.closeObject( pstmnt );
		}
		return strFolioPagado;
	}

	public static int insertaDetalle (Connection conn, int folio, String strFolioEjPag, String tipo) throws Exception {
		PreparedStatement ps = null, psInserta = null;
		ResultSet rs = null;
		String query = "";
		String cEvento= "";
		int inserto = 0;
		try {
			
			if ("EJERCIDO".equals( tipo )) {
				 query = "select * from v_aplicarejercidopagadoDetalle where cTipoPago = 'PAGODIVERSO' and nfolio = ? and mPasivoDiferido = 0";
			} else {
				query = "select * from v_aplicarejercidopagadoDetalle where cTipoPago = 'PAGODIVERSO' and nfolio = ?";
			}
				
			ps = conn.prepareStatement( query );
			ps.setInt( 1, folio );
			
			rs = ps.executeQuery();
			
			while(rs.next()){
				String cTipoPago = rs.getString("cTipoPago");
				int nDocRenglon = rs.getInt("nDocRenglon");
				int strcEjercicio = rs.getInt("cEjercicio");
				int cMes = rs.getInt("cMes");
				String cIdEntidadContable = rs.getString("cIdEntidadContable");
				String EP = rs.getString("EP");
				String nCapitulo = rs.getString("nCapitulo");
				String cIdRelacion = rs.getString("cIdRelacion");
				String idTipoMovimiento = rs.getString("ID_TIPO_MOVIMIENTO");
				String idTipoConcepto = rs.getString("ID_TIPO_CONCEPTO");
				String cCentroContable = rs.getString("cCentroContable");
				String Rfc =  rs.getString("RFC");
				String alm = rs.getString("ALM");
				String cIdCuentaContable = rs.getString("cIdCuentaContable");
				String nPoliza = rs.getString("nPoliza");
				String altaAlmacen = rs.getString( "altaAlmacen" );
								
				double mComprometido =  rs.getDouble("mComprometido");
				double mImporteBruto = rs.getDouble("mImporteBruto");
				double mImporteIva = rs.getDouble("mImporteIva");
				double mImporteNeto = rs.getDouble("mImporteNeto");
				double mImporteMasIva = rs.getDouble("mImporteMasIva");
				double mSancion = rs.getDouble("mSancion");
				double mDevolucion = rs.getDouble("mDevolucion");
				double mImporteAmortiza = rs.getDouble("mImporteAmortiza");
				double mRetencion = rs.getDouble("mRetencion");
				double mPenalizacion = rs.getDouble("mPenalizacion");
				double m2Millar = rs.getDouble("m2Millar");
				double m23IVA = rs.getDouble("m23IVA");
				double mISRHonorarios = rs.getDouble( "mISRHonorarios" );
				double mImporteIvaHonorarios = rs.getDouble("mImporteIvaHonorarios");
				double mImporteIvaProv = rs.getDouble("mImporteIvaProv");
				double mImporteObra = rs.getDouble("mImporteObra");
				double mObra5 = rs.getDouble("mObra5");
				double mImporteIvaArrenda = rs.getDouble("mImporteIvaArrenda");
				double mImporteISRLaudos = rs.getDouble( "mImporteISRLaudos" );
				double mIva6 = rs.getDouble("mImporteIva6");
				double mRetImpuestoCedular = rs.getDouble("mRetImpuestoCedular");
				double mImporteFlete4 = rs.getDouble("mImporteFlete4");
				double mImporteFlete23 = rs.getDouble("mImporteFlete23");
				double mISRArrenda = rs.getDouble("mISRArrenda");
				double mCNIC = rs.getDouble("mCNIC");
				double mTesofe = rs.getDouble("mTesofe");
				double mPasivoDiferido = rs.getDouble("mPasivoDiferido");
				String cTAB = rs.getString("CTAB");
				String cOBGT = EP.substring(31, 36);
				String ur = rs.getString("cUnidadResponsable");
				
				String cEventoCa[]= rs.getString("cEvento").split("_");
				StringBuilder queryApp = new StringBuilder();
				cEvento = "P";
				for(int i = 1; i < cEventoCa.length; i++){
					cEvento += "_";
					cEvento += cEventoCa[i];
				}	
				
				String cEventoPagado = cEvento;
						
				if (("EJERCIDO").equals(tipo)) {
					queryApp.append( "INSERT INTO tEjercidoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO"); 
					queryApp.append( ",ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios");
					queryApp.append( ",mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioEjercido, mImporteISRLaudos, mimporteiva6, cUnidadResponsable)");
					queryApp.append( " VALUES('"+cTipoPago+"',"+ folio +","+nDocRenglon+","+cMes+",'"+strcEjercicio+"','"+cIdEntidadContable+"','"+cIdRelacion+"','"+ EP );
					queryApp.append( "','"+cIdCuentaContable+"',"+mComprometido+","+nPoliza+",'"+idTipoMovimiento+"','"+idTipoConcepto+"','EJERCIDO','"+cMes+"','");
					queryApp.append( cCentroContable+"','"+Rfc+"',"+mImporteNeto+",'"+alm+"',"+mImporteBruto+","+mImporteMasIva+","+mImporteIva+",'"+nCapitulo+"',"+mSancion+","+mDevolucion+",");
					queryApp.append( mImporteAmortiza+" ,"+mRetencion+","+mPenalizacion+","+m2Millar+","+m23IVA+","+mISRHonorarios);
					queryApp.append( ","+mObra5+", "+mImporteFlete4+", "+mISRArrenda+", "+mRetImpuestoCedular+", "+mImporteIvaArrenda);
					queryApp.append( ", "+mImporteIvaHonorarios+", "+mImporteFlete23+","+mImporteIvaProv+","+mImporteObra+", "+mCNIC+","+mTesofe+",'"+altaAlmacen+"'," );
					queryApp.append(" '"+strcEjercicio+"',"+mCNIC+","+mImporteNeto+","+strFolioEjPag+ ", " + mImporteISRLaudos+ ", " + mIva6 +  ", '" + ur +"')" );
					
					psInserta = conn.prepareStatement(queryApp.toString());
					inserto = psInserta.executeUpdate();
					log.debug( queryApp.toString() );
				
				} else if (("PAGADO").equals(tipo)) { 
					queryApp.append( "INSERT INTO tPagadoDetalle ( cTipoPago, nFolioPAGO, nDocRenglon," );
					queryApp.append( "cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido, nPoliza, ID_TIPO_MOVIMIENTO, ID_TIPO_CONCEPTO," );
					queryApp.append( "cEvento, nMes, cCentroContable, RFC,  mImporteNeto, ALM ,mImporteBruto, mImporteMasIva," );
					queryApp.append(" mImporteIva, nCapitulo, mSancion, mDevolucion, mImporteAmortiza, mRetencion, mPenalizacion, m2Millar," );
					queryApp.append(" m23IVA, mISRHonorarios, mObra5    , mImporteFlete4, mISRArrenda, mRetImpuestoCedular, mImporteIvaArrenda, mImporteIvaHonorarios ," );
					queryApp.append(" mImporteFlete23, mImporteIvaProv , mImporteObra, mCNIC, mTesofe, altaAlmacen, aEjercicioFiscal," );
					queryApp.append(" mIMDT, mImporte,nFolioPagado,OBGT, CTAB, mImporteISRLaudos, mPasivoDiferido, mImporteIva6,cUnidadResponsable) VALUES('" );
					queryApp.append( cTipoPago );
					queryApp.append( "',"+ folio);
					queryApp.append( ","+nDocRenglon);
					queryApp.append( ","+cMes);
					queryApp.append( ",'"+strcEjercicio);
					queryApp.append( "','"+cIdEntidadContable);
					queryApp.append( "','"+cIdRelacion);
					queryApp.append( "','"+EP);
					queryApp.append( "','"+cIdCuentaContable);
					queryApp.append( "',"+mComprometido);
					queryApp.append( ","+nPoliza);
					queryApp.append( ",'"+idTipoMovimiento);
					queryApp.append( "','"+idTipoConcepto);
					queryApp.append( "','"+cEventoPagado);
					queryApp.append( "','"+cMes);
					queryApp.append( "','"+cCentroContable);
					queryApp.append( "','"+Rfc);
					queryApp.append( "',"+mImporteNeto);
					queryApp.append( ",'"+alm);
					queryApp.append( "',"+mImporteBruto);
					queryApp.append( ","+mImporteMasIva);
					queryApp.append( ","+mImporteIva);
					queryApp.append( ",'"+nCapitulo);
					queryApp.append( "',"+mSancion);
					queryApp.append( ","+mDevolucion);
					queryApp.append( ","+mImporteAmortiza);
					queryApp.append( " ,"+mRetencion);
					queryApp.append( ","+mPenalizacion);
					queryApp.append( ","+m2Millar);
					queryApp.append( ","+m23IVA);
					queryApp.append( ","+mISRHonorarios);
					queryApp.append( ","+mObra5);
					queryApp.append( " ,"+mImporteFlete4);
					queryApp.append( ","+mISRArrenda);
					queryApp.append( ","+mRetImpuestoCedular);
					queryApp.append( ","+mImporteIvaArrenda);
					queryApp.append( ","+mImporteIvaHonorarios);
					queryApp.append( ","+mImporteFlete23);
					queryApp.append( ","+mImporteIvaProv);
					queryApp.append( ","+mImporteObra);
					queryApp.append( ","+mCNIC);
					queryApp.append( ","+mTesofe);
					queryApp.append( ",'"+altaAlmacen);
					queryApp.append( "','"+ strcEjercicio);
					queryApp.append( "',"+mCNIC);
					queryApp.append( ","+mImporteNeto);
					queryApp.append( ","+strFolioEjPag);
					queryApp.append( ",'"+cOBGT);
					queryApp.append( "', '"+cTAB+"'," + mImporteISRLaudos + "," + mPasivoDiferido+ "," +  mIva6+ ",'" +  ur +"')"); 
					
					psInserta = conn.prepareStatement(queryApp.toString());
				    inserto = psInserta.executeUpdate();
				    log.debug( queryApp.toString() );
				}	
			}	
			return inserto;
		}  finally {
			CloseObject.closeObject( ps );
			CloseObject.closeObject( psInserta );
			CloseObject.closeObject( rs );
		}
		
	}

	public static boolean existeFolio( Connection conn, int folio, String importe ) throws Exception {
		PreparedStatement pstmnt = null, pst2 = null;
		ResultSet rs = null;
		int solicitudExiste = 0;
		float total = 0;
		boolean existe = false;
		float importeBanco = Float.parseFloat(importe);
		
		try {
				pstmnt = conn.prepareStatement( "select count(*) idExiste, sum(mImporteNeto) mImporteNeto from tPAGODIVERSOEncabezado (NOLOCK) where nFolioPAGODIVERSO = ?");
				pstmnt.setInt( 1, folio );
				rs = pstmnt.executeQuery();
			
				if(rs.next()){
					solicitudExiste = rs.getInt("idExiste");
					total = rs.getFloat("mImporteNeto");
				}	 
				
				if ( solicitudExiste > 0 ) { //La solicitud Bancaria existe en la base
					
					if (importeBanco ==  total ) { //el total es el mismo
						existe = true;
					} else {
						pst2 = conn.prepareStatement( "update tArchivoEjercidoPagado set cDescripcion  ='EL IMPORTE DE LA SOLICITUD ES DIFERENTE AL DEL BANCO'  where nReferencia = ?" );
						pst2.setInt(1, folio);
						pst2.executeUpdate();
					}
				}
		
				
		}finally {
				CloseObject.closeObject( rs );
				CloseObject.closeObject( pst2 );
				CloseObject.closeObject( pstmnt );	
		}
			return existe;
	}
	
	public static boolean existeFolioTaxi( Connection conn, String folio ) throws Exception {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		boolean existe = false;
		
		try {
				pstmnt = conn.prepareStatement( "select count(*)  from tLayoutTaxis WITH (NOLOCK) where cFolioTaxi = ?");
				pstmnt.setString( 1, folio );
				rs = pstmnt.executeQuery();
			
				if(rs.next()){
					int i = rs.getInt( 1 );
					
					if( i > 0) 
						existe = true;
				}	 
						
		}finally {
				CloseObject.closeObject( rs );
				CloseObject.closeObject( pstmnt );	
		}
			return existe;
	}
	
	public static boolean borrarFolioTaxi( Connection conn, String folio ) throws Exception {
		PreparedStatement pstmnt = null;
		boolean existe = false;
		
		try {
				pstmnt = conn.prepareStatement( "DELETE tLayoutTaxis where cFolioTaxi = ?");
				pstmnt.setString( 1, folio );
				pstmnt.execute();
				
		}finally {
				CloseObject.closeObject( pstmnt );
		}
			return existe;
	}
	
	public static boolean estaAplicado( Connection conn, int folio ) throws Exception {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String ejercido = "";
		boolean aplicado = false;
		String queryEjercido = "SELECT  CASE when ejercido = 'EJERCIDO' THEN 'S' ELSE 'N' END aplicadoEjercido FROM v_aplicarejercidopagadoEncabezado WITH(NOLOCK) WHERE cTipoPago = 'PAGODIVERSO' and nfolio = ?";
		try {
				pstmnt = conn.prepareStatement( queryEjercido );
				pstmnt.setInt( 1, folio );
				rs = pstmnt.executeQuery();
			
				if(rs.next()){
					 ejercido = rs.getString("aplicadoEjercido");
				}	 
				
				if ("S".equals( ejercido ))
					aplicado = true;
				
		}finally {
				CloseObject.closeObject( rs );		
				CloseObject.closeObject( pstmnt );	
		}
			return aplicado;
	}
	
	public static boolean estaAplicadoPagado( Connection conn, int folio ) throws Exception {
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String pagado = "";
		boolean aplicado = false;
		String queryEjercido = "SELECT  CASE when pagado = 'PAGADO' THEN 'S' ELSE 'N' END aplicado FROM v_aplicarejercidopagadoEncabezado WITH(NOLOCK) WHERE cTipoPago = 'PAGODIVERSO' and nfolio = ?";
		try {
				pstmnt = conn.prepareStatement( queryEjercido );
				pstmnt.setInt( 1, folio );
				rs = pstmnt.executeQuery();
			
				if(rs.next()){
					pagado = rs.getString("aplicado");
				}	 
				
				if ("S".equals( pagado ))
					aplicado = true;
				
		}finally {
				CloseObject.closeObject( rs );		
				CloseObject.closeObject( pstmnt );	
		}
			return aplicado;
	}
	
	
	public static String insertaEjercido (Connection conn, int folio, Date fAplicacion) throws Exception {
		PreparedStatement pstmUpEjercido = null,  pstmSeqEjercido= null, pstEjercido = null, pstInsertar = null;
		ResultSet rs = null, rs2 = null;
		String strFolioEjercido = "0";
		SimpleDateFormat fechaApp = new SimpleDateFormat("yyyy/MM/dd"); 
		String fecha = fechaApp.format(fAplicacion);
		try {
			/*** Seleccionar SEQUENCE Ejercido ***/
			pstmUpEjercido = conn.prepareStatement("UPDATE CF_SEQUENCE WITH (ROWLOCK) SET seq_value = seq_value + 1  WHERE seq_name = 'EJERCIDO' ");
			pstmUpEjercido.executeUpdate();
			
			pstmSeqEjercido = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'EJERCIDO' ");
			rs = pstmSeqEjercido.executeQuery();
			
				if(rs.next()){
					
					strFolioEjercido = rs.getString("seq_value");
					
				}
			
			/*** Inserta Ejercido ***/
			String query = "SELECT  * FROM v_aplicarejercidopagadoEncabezado WITH(NOLOCK) WHERE cTipoPago = 'PAGODIVERSO' and nfolio = ?";
				
			pstEjercido = conn.prepareStatement( query );
			pstEjercido.setInt( 1, folio );
			
			rs2 = pstEjercido.executeQuery();
			
			if(rs2.next()){
				String tipoPago = rs2.getString( "cTipoPago" );
				String canoContrarrecibo = rs2.getString( "caNoContrarrecibo" );
				String descripcion = rs2.getString( "cDescripcionPoliza" );
				String unidad = rs2.getString( "cUnidadResponsableContable" );
				
				
				String queryEjer = "INSERT INTO tEjercidoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza, "
							+ " U_LOGIN, fAplicacion, nFolioPolizaCancelacion, cDescripcionPoliza, cUnidadResponsableContable, nFolioSICOP, cRamo, "
							+ " cIdUsuarioCaptura, FechaAplicacionSicop, FechaPagoSicop, SolicitudPago,NumeroProceso,nFolioSIAFF,nFolioEjercido)"
							+ " VALUES ('" + tipoPago + "', " + folio + ", RTRIM(LTRIM('" + canoContrarrecibo + "')), 0, 'DI', 'administrador', CAST ('"+ fecha + "' AS DATE), 0, '" 
							+ descripcion + "','" + unidad + "','-1', '16', 'administrador', getdate(), getdate(), 9999, 9999, 9999, " + strFolioEjercido + ")" ;
				
				pstInsertar = conn.prepareStatement( queryEjer );
				int ejecuto =  pstInsertar.executeUpdate();
				
				if (ejecuto == 1 ) {
					/* inserta detalle */
					int detalle = insertaDetalle(conn, folio, strFolioEjercido, "EJERCIDO");	
					
					if (!(detalle == 1)) {
						
						strFolioEjercido = "0";
					} 
				}else {  
						strFolioEjercido = "0";	
					
				}
			}
			
			
			return strFolioEjercido;
			
		} finally {
			CloseObject.closeObject( pstmSeqEjercido );
			CloseObject.closeObject( pstmUpEjercido );
			CloseObject.closeObject( pstEjercido );
			CloseObject.closeObject( pstInsertar );
			CloseObject.closeObject( rs );
			CloseObject.closeObject( rs2 );
		}
	}
	
	public static void aplicaEjercido (Connection conn, String folioEjercido) throws Exception {

		AccountingEngine ae = new AccountingEngine();
		ae.setValidaInsuficienciaDeSaldo( true );
		
		ae.makeAccountingApplication( conn, "EJERCIDO", folioEjercido, "tEjercidoEncabezado", "tEjercidoDetalle", "nFolioEjercido" );
	}

	public static void aplicaPagado (Connection conn, String folioPagado) throws Exception {

		AccountingEngine ae = new AccountingEngine();
		ae.setValidaInsuficienciaDeSaldo( true );
		
		ae.makeAccountingApplication( conn, "PAGADO", folioPagado, "tPagadoEncabezado", "tPagadoDetalle", "nFolioPagado" );
	}
} 
