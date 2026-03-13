package com.syc.contable;


import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.contable.core.ReintegroDetalle;
import com.syc.contable.core.ReintegroDetalleMil;
import com.syc.contable.core.ReintegroEncabezado;
import com.syc.contable.core.ReintegroEncabezadoMil;
import com.syc.contable.core.ReintegrosManager;
import com.syc.contable.core.ReintegrosMilManager;
import com.syc.dsmngr.DataSourceManager;
import com.syc.fortimax.core.DocumentoBussinessLogic;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;


public class ReintegrosMilBusinessLogic extends DataSourceManager {

	private static Logger	log					= Logger.getLogger( ReintegrosMilBusinessLogic.class );
	public boolean			correoProduccion	= false;

	public ReintegrosMilBusinessLogic( String jniName ) {

		super.init( jniName );
	}

	AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic( GestionInterface.ATT_CONEXION ); // para
																										// obtener
																										// el
																										// ejercicio
																										// fiscal
																										// en
																										// diferentes
																										// funciones

	public List<String> leeArchivoExcel( String archivo, Caso c, Usuario usuario, int folio ) throws Exception {
		Connection conn = null;
		DocumentoBussinessLogic dbl = new DocumentoBussinessLogic();
		try {
			conn = getConnection();
			/*
			 * se pone true por si hay remanente para la ep, mes y cxp que
			 * cumplan aunque sea un renglon de muchos, en ese hay remanente
			 */
			ReintegroEncabezadoMil reinE = new ReintegroEncabezadoMil();
			ReintegroDetalleMil reinD = new ReintegroDetalleMil();
			ArrayList<ReintegroDetalleMil> reinDetalles = new ArrayList<ReintegroDetalleMil>();

			generaEncabezadoDetalleReintegro( archivo, c, usuario, reinE, reinD, reinDetalles, folio );
			List<String> lstErrores = validaArchivoExcelReintegro( c, reinDetalles, reinE, reinD );

			if ( lstErrores.size() > 0 )
				return lstErrores;
			else {
				ReintegrosMilManager.insertaReintegro( conn, reinE, reinDetalles, folio, c.getFolio(), usuario, c );
				List<String> l = new ArrayList<String>();
				l.add( "EXITO" );
				return l;
			}
		} catch ( Exception e ) {
			dbl.limpiaDocumento( c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1 );
			throw e;
		}

	}

	public List<String> validaArchivoExcelReintegro( Caso c, ArrayList<ReintegroDetalleMil> reinDetalles, ReintegroEncabezadoMil recE, ReintegroDetalleMil recD ) throws Exception {
		List<String> mensajes = new ArrayList<String>();
		boolean banderaRemanente = false;
		int folio = Integer.parseInt( c.getFolio().split( "-" )[2] );
		double remanente = 0;
		Connection conn = null;

		try {
			conn = getConnection();
			double importe = 0;
			for ( int i = 0; i < reinDetalles.size(); i++ ) {
				ReintegroDetalleMil rd = reinDetalles.get( i );
				importe += rd.getmImporteCLC();
				if ( !getEPCatalogo( rd.getEP() ) ) {
					mensajes.add( "La EP " + rd.getEP() + " no existe o se encuentra mal escrita \\n" );
					log.warn( "La EP " + rd.getEP() + " no existe o se encuentra mal escrita \n" );
					throw new Exception( "La EP " + rd.getEP() + " no existe o se encuentra mal escrita \\n" );
				} else {
					String str = clcPagada( Integer.parseInt( rd.getnSIAFF().replace( ".0", "" ) ), rd.getCLCSAI() );
					if ( str != null && !"".equals( str ) ) {
						mensajes.add( str );
						throw new Exception( str );
					} else if ( !"2012".equals( adecProy.obtenEjercicioFiscal() ) ) { // para
																						// dejar
																						// pasar
																						// el
																						// reintegro
																						// sin
																						// validación
																						// de
																						// remanentes
																						// en
																						// caso
																						// de
																						// ser
																						// 2012

						// ******************************************************************************//
						ReintegroDetalleMil temp = new ReintegroDetalleMil();
						temp = datosSICOPMil( String.valueOf( rd.getnSIAFF() ).replace( ".0", "" ), rd.getEP(), rd.getTipoMovimiento(), rd.getTipoConcepto() );
						rd.setSecCLC( temp.getSecCLC() );
						rd.setFolioDependenciaSicop( temp.getFolioDependenciaSicop() );
						rd.setRfc( getRFC( rd.getCLCSAI(), rd.getEP() ) );
						// *****************************************************************************//

						// int secuenciaCLC = 0; //TEMPORALMENTE PARA
						// REGULARIZACION!!!!!

						if ( "-1".equals( rd.getSecCLC() ) ) {
							mensajes.add( "No se puede obtener el valor de la secuencia para la EP " + rd.getEP() + " porque no se encuentra en la tabla CLC_SICOP o porque no se pudo enlazar.\\n" );
							log.warn( "No se puede obtener el valor de la secuencia para la EP " + rd.getEP() + " porque no se encuentra en la tabla CLC_SICOP o porque no se pudo enlazar.\n" );
							throw new Exception( "No se puede obtener el valor de la secuencia para la EP " + rd.getEP() + " porque no se encuentra en la tabla CLC_SICOP o porque no se pudo enlazar.\\n" );
						} else {
							if ( "-1".equals( rd.getFolioDependenciaSicop() ) ) {
								mensajes.add( "No se puede obtener el folio de dependencia para la EP " + rd.getEP() + " porque no se encuentra en la tabla CLC_SICOP o porque no se pudo enlazar.\\n" );
								log.warn( "No se puede obtener el folio de dependencia para la EP " + rd.getEP() + " porque no se encuentra en la tabla CLC_SICOP o porque no se pudo enlazar.\n" );
								throw new Exception( "No se puede obtener el folio de dependencia para la EP " + rd.getEP() + " porque no se encuentra en la tabla CLC_SICOP o porque no se pudo enlazar.\\n" );
							}
							banderaRemanente = false;
							int docRenglon = getNDocRenglon( rd.getCLCSAI(), rd.getEP(), rd.getMes(), rd.getCxp(), rd.getTipoMovimiento(), rd.getTipoConcepto() );
							if ( docRenglon == -1 ) {
								mensajes.add( "Favor de revisar el mes para la EP " + rd.getEP() + " con CXP " + rd.getCLCSAI() + "\\n" );
								log.warn( "Favor de revisar el mes para la EP " + rd.getEP() + " con CXP " + rd.getCLCSAI() + "\n" );
								throw new Exception( "Favor de revisar el mes para la EP " + rd.getEP() + " con CXP " + rd.getCLCSAI() + "\\n" );
							} else {
								remanente = getRemanente( rd.getEP(), rd.getCLCSAI(), docRenglon, rd.getCxp() );
								if ( remanente >= rd.getmImporteCLC() ) {
									banderaRemanente = true;
								}
								rd.setRenglonPagado( String.valueOf( docRenglon ) );
								rd.setcCentroContable( getCCNormal( rd.getCLCSAI(), rd.getEP(), rd.getMes(), folio, rd.getCxp() ) );
								if ( !banderaRemanente ) {
									mensajes.add( "No hay suficiente remanente para poder aplicar el reintegro para " + rd.getEP() + " renglon " + rd.getSecCLC() + " mes " + rd.getMes() + " cxp " + rd.getCLCSAI() + " remanente " + remanente + " importe a reintegrar " + rd.getmImporteCLC() + "\\n" );
									mensajes.add( ReintegrosMilManager.getMesesImportes( conn, rd.getEP(), rd.getCLCSAI() ) );
									log.warn( "No hay suficiente remanente para poder aplicar el reintegro para " + rd.getEP() + " renglon " + rd.getSecCLC() + " mes " + rd.getMes() + " cxp " + rd.getCLCSAI() + " remanente " + remanente + " importe a reintegrar " + rd.getmImporteCLC() + "\n" );
									throw new Exception( "No hay suficiente remanente para poder aplicar el reintegro para " + rd.getEP() + " renglon " + rd.getSecCLC() + " mes " + rd.getMes() + " cxp " + rd.getCLCSAI() + " remanente " + remanente + " importe a reintegrar " + rd.getmImporteCLC() + "\\n" + ReintegrosMilManager.getMesesImportes( conn, rd.getEP(), rd.getCLCSAI() ) );
								} else {
									String partida = getcPartida( rd.getEP() );
									if ( partida.trim().length() > 0 && partida != null && !"".equals( partida ) ) {
										rd.setcPartida( partida );
									}
									rd.setAlm( getALM( rd.getCLCSAI(), rd.getEP(), rd.getMes(), rd.getCxp() ) );
								}
							}
						}
					}
				}
			}
			if ( com.syc.contable.util.Math.truncate( importe, 2 ) != com.syc.contable.util.Math.truncate( Double.parseDouble( recE.getImporteLC() ), 2 ) ) {
				log.warn( "El importe del encabezado y la suma de los importes de los detalles no coinciden \n" );
				throw new Exception( "El importe del encabezado y la suma de los importes de los detalles no coinciden \\n" );
			}
		} catch ( Exception e ) {
			throw e;
		} finally {
			CloseObject.closeObject( conn, false );
		}

		return mensajes;
	}

	private void generaEncabezadoDetalleReintegro( String archivo, Caso c, Usuario usuario, ReintegroEncabezadoMil reinE, ReintegroDetalleMil reinD, ArrayList<ReintegroDetalleMil> reinDetalles, int folio ) throws Exception {
		String DATE_FORMAT = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat( DATE_FORMAT );
		Calendar c1 = Calendar.getInstance(); // today
		String today = sdf.format( c1.getTime() );
		reinE.setcCentroContable( usuario.getPropiedad( "CCENTROCONTABLE" ).getValor() );
		reinE.setnFolioReintegro( folio );
		reinE.setcUnidadResponsableContable( "RHQ" );
		reinE.setcTipoPoliza( "EG" );
		reinE.setfExpedicion( today );
		reinE.setcDocumentoHaplicado( "N" );
		reinE.setaEjercicioFiscal( adecProy.obtenEjercicioFiscal() );

		int renglon = 1;

		InputStream inp = new FileInputStream( archivo );
		HSSFWorkbook wb = new HSSFWorkbook( inp );
		HSSFSheet sheet = wb.getSheetAt( 0 );

		Iterator<Row> rowIterator = sheet.rowIterator();
		int fila = 0;

		while ( rowIterator.hasNext() ) {
			fila++;
			int celda = 0;
			String ep = "";
			HSSFRow hssfRow = ( HSSFRow ) rowIterator.next();
			Iterator<Cell> cellIterator = hssfRow.cellIterator();
			while ( cellIterator.hasNext() ) {
				try {
					String valor = valorCelda( cellIterator );
					if ( valor != null ) {
						if ( fila == 2 ) {
							if ( celda == 0 )
								reinE.setaEjercicioFiscal( valor.replace( ".0", "" ) );
							if ( celda == 1 )
								reinE.setcRamo( valor.replace( ".0", "" ) );
							if ( celda == 2 )
								reinE.setcUnidadResponsable( valor );
							if ( celda == 3 )
								reinE.setImporteLC( valor );
							if ( celda == 4 )
								reinE.setAviso( valor.replace( ".0", "" ) );
							if ( celda == 5 ) {
								if ( "N/A".equals( valor ) )
									reinE.setfSolicitud( today );
								else
									reinE.setfSolicitud( valor );
							}
							if ( celda == 6 ) {
								if ( validaCatMovimiento( valor ) ) {
									reinE.setMovimiento( valor );
								} else {
									log.error( "El movimiento no se encuentra en catalogos." );
									throw new Exception( "El movimiento no se encuentra en catalogos." );
								}
							}
							if ( celda == 7 ) {
								if ( validaCatTipoAviso( valor.replace( ".0", "" ) ) ) {
									reinE.setTipoAviso( valor.replace( ".0", "" ) );
								} else {
									log.error( "El tipo aviso no se encuentra en catalogos." );
									throw new Exception( "El tipo aviso no se encuentra en catalogos." );
								}
							}
							if ( celda == 8 ) {
								if ( validaCatFormaPago( valor.replace( ".0", "" ) ) ) {
									reinE.setFormaDePago( valor.replace( ".0", "" ) );
								} else {
									log.error( "La forma pago no se encuentra en catalogos." );
									throw new Exception( "La forma pago no se encuentra en catalogos." );
								}
							}
							if ( celda == 9 ) {
								if ( validaCatCausaAviso( valor.replace( ".0", "" ) ) ) {
									reinE.setCausaAviso( valor.replace( ".0", "" ) );
								} else {
									log.error( "La causa de aviso no se encuentra en catalogos." );
									throw new Exception( "La causa de aviso no se encuentra en catalogos." );
								}
							}
							if ( celda == 10 ) {
								if ( "N/A".equals( valor ) )
									reinE.setfAplicacion( today );
								else
									reinE.setfAplicacion( valor );
							}
							if ( celda == 11 ) {
								if ( "N/A".equals( valor ) )
									reinE.setfAcreditacion( today );
								else
									reinE.setfAcreditacion( valor );
							}
							if ( celda == 12 )
								reinE.setClvRastreo( valor.replace( ".0", "" ) );
							if ( celda == 13 )
								reinE.setFichaDeposito( valor.replace( ".0", "" ) );
							if ( celda == 14 )
								reinE.setLc( valor.replace( ".0", "" ) );
							if ( celda == 15 )
								reinE.setClvBanco( valor.replace( ".0", "" ) );
							if ( celda == 16 )
								reinE.setCuentaBancaria( valor.replace( ".0", "" ) );
							if ( celda == 17 )
								reinE.setFolioDependencia( valor.replace( ".0", "" ) );
						} else if ( fila == 4 ) {
							if ( celda == 0 )
								reinE.setObservaciones( valor );
							if ( celda == 1 )
								reinE.setConcepto( valor );
						}
						if ( fila > 5 ) {
							if ( celda == 0 ) {
								reinD = new ReintegroDetalleMil();
								reinD.setnSIAFF( valor.replace( ".0", "" ) ); // viene
																				// en
																				// el
																				// archivo
																				// como
																				// CLC
																				// indicando
																				// que
																				// es
																				// el
																				// número
																				// de
																				// CLC,
																				// el
																				// que
																				// proporcionan
																				// es
																				// el
								// SIAFF, de ahí el nombre para no confundirnos
								// con el nCLCSicop
							}
							if ( celda == 1 ) {
								// reinD.setnDocRenglon(valor.replace(".0",
								// ""));
								reinD.setnDocRenglon( String.valueOf( renglon ) );
								renglon++;
							}
							if ( celda == 2 )
								ep = valor;
							if ( celda == 3 ) {
								reinD.setEP( ep + "." + valor );
								reinD.setObgt( reinD.getEP().substring( 31, 36 ) );
							}
							if ( celda == 4 )
								reinD.setMes( Integer.parseInt( valor.replace( ".0", "" ) ) );
							if ( celda == 5 )
								reinD.setmImporteCLC( Double.parseDouble( valor ) );
							if ( celda == 6 )
								reinD.setCLCSAI( valor );
							if ( celda == 7 )
								reinD.setCxp( valor );
							if ( celda == 8 )
								reinD.setTipoConcepto( valor );
							if ( celda == 9 ) {
								reinD.setTipoMovimiento( valor );
								reinD.setcEvento( "REIN_TRAM" );
								reinDetalles.add( reinD );
							}
						}
					}
				} catch ( NumberFormatException e ) {
					throw new Exception( "Hubo un error de formato en la fila " + fila + " celda " + celda + "." + e.toString(), e );
				} catch ( IllegalStateException exc ) {
					throw new Exception( "Hubo un error de formato de formula en la fila " + fila + " celda " + celda + "." + exc.toString(), exc );
				} catch ( IndexOutOfBoundsException exc ) {
					throw new Exception( "Hubo un error de formato posiblemente de espacios en blanco en la fila " + fila + " celda " + celda + "." + exc.toString(), exc );
				} catch ( Exception e ) {
					throw e;
				}
				celda++;
			}
		}
	}

	private String valorCelda( Iterator<Cell> cellIterator ) {
		String valor = null;
		HSSFCell hssfCell = ( HSSFCell ) cellIterator.next();
		if ( hssfCell.getCellType() == CellType.NUMERIC ) {
			if ( HSSFDateUtil.isCellDateFormatted( hssfCell ) ) {
				Date date = HSSFDateUtil.getJavaDate( hssfCell.getNumericCellValue() );
				DateFormat dateFormatter = new SimpleDateFormat( "dd/MM/yyyy" );
				valor = dateFormatter.format( date );
			} else {
				if ( !String.valueOf( hssfCell.getNumericCellValue() ).equals( "" ) && String.valueOf( hssfCell.getNumericCellValue() ) != null ) {
					valor = String.valueOf( hssfCell.getNumericCellValue() );
				}
			}
		} else {
			if ( !hssfCell.getStringCellValue().equals( "" ) && hssfCell.getStringCellValue() != null ) {
				valor = hssfCell.getStringCellValue();
			}
		}
		return valor;
	}

	public void buscaReintegro( Caso c ) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
	}

	public boolean insertaDetReintegroPaso( Connection conn, int folio, ReintegroDetalle datos, String clc, String cCentro, int consecutivo, String folioDep ) throws Exception {
		boolean inserta = false;
		try {
			String evento = "REIN_TRAM"; // Cuando está en trámite todos los
											// reintegros van a tener este
											// evento, excepto SPEI
			if ( "REIN_TRAM_SPEI".equals( clc ) )
				evento = "REIN_TRAM_SPEI"; // Evento en trámite SPEI
			inserta = ReintegrosMilManager.insertaDetReintegroPaso( conn, folio, datos, evento, cCentro, consecutivo, folioDep );
		} catch ( Exception exc ) {
			log.error( exc );
			throw new Exception( exc );
		} finally {
		}
		return inserta;
	}

	public boolean borraDetReintegro( int folio ) throws Exception {
		Connection conn = null;
		boolean borra = false;
		try {
			conn = getConnection();
			borra = ReintegrosMilManager.borraDetReintegro( conn, folio );
			conn.commit();
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new Exception( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}
		return borra;
	}

	public boolean borraDetReintegroPaso( int folio ) throws Exception {
		Connection conn = null;
		boolean borra = false;
		try {
			conn = getConnection();
			if ( getDetallePasoTotal( folio ) > 0 ) {
				borra = ReintegrosMilManager.borraDetReintegroPaso( conn, folio );
				conn.commit();
			}
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new Exception( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}
		return borra;
	}

	public String getEvento( String cOBGINI, String cCTGA, String tipoCLC ) throws Exception {
		Connection conn = null;
		String res = "";
		try {
			conn = getConnection();
			res = ReintegrosMilManager.getEvento( conn, cOBGINI, cCTGA, tipoCLC );
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public ArrayList<ReintegroDetalle> getReintegroDetalle( int folio ) throws SQLException {
		Connection conn = null;
		ArrayList<ReintegroDetalle> detalles;
		try {
			conn = getConnection();
			detalles = ReintegrosMilManager.getReintegroDetalle( conn, folio );
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return detalles;
	}

	public ArrayList<ReintegroDetalle> getDetallesInsert( String tablaDatos, int folio ) throws Exception {
		ArrayList<ReintegroDetalle> reintegrosDetalle = new ArrayList<ReintegroDetalle>();
		String pagado = "";
		if ( !tablaDatos.equals( "" ) ) {
			String[] tablaDatosArreglo = tablaDatos.split( "!" );
			for ( int i = 0; i < tablaDatosArreglo.length; i += 7 ) {
				ReintegroDetalle reintegro = new ReintegroDetalle();
				if ( !tablaDatosArreglo[i].equals( "" ) ) {
					reintegro.setNoCLC( new Double( tablaDatosArreglo[i] ).intValue() );
					reintegro.setCxp( tablaDatosArreglo[i + 5] );
					pagado = clcPagada( reintegro.getNoCLC(), reintegro.getCxp() );
					if ( pagado.trim().length() > 0 && pagado != null && !"".equals( pagado ) ) {
						reintegrosDetalle.clear();
						break;
					}
				}
				if ( !tablaDatosArreglo[i + 1].equals( "" ) )
					reintegro.setSecCLC( secCLC( tablaDatosArreglo[i + 5].trim(), tablaDatosArreglo[i + 2].trim(), folio ) );
				if ( !tablaDatosArreglo[i + 2].equals( "" ) )
					reintegro.setEP( tablaDatosArreglo[i + 2].trim() );
				if ( !tablaDatosArreglo[i + 3].equals( "" ) )
					reintegro.setMes( new Double( tablaDatosArreglo[i + 3] ).intValue() );
				if ( !tablaDatosArreglo[i + 4].equals( "" ) )
					reintegro.setmImporteCLC( Double.valueOf( tablaDatosArreglo[i + 4] ) );
				if ( !tablaDatosArreglo[i + 5].equals( "" ) )
					reintegro.setCxp( tablaDatosArreglo[i + 5].trim() );
				if ( !tablaDatosArreglo[i + 6].equals( "" ) )
					reintegro.setnDocRenglon( Integer.parseInt( tablaDatosArreglo[i + 6].trim() ) );
				String partida = "";
				try {
					partida = getcPartida( reintegro.getEP() );
					if ( partida.trim().length() > 0 && partida != null && !"".equals( partida ) ) {
						reintegro.setnPartida( partida );
					} else {
						reintegrosDetalle.clear();
						break;
					}
				} catch ( SQLException sqle ) {
					sqle.printStackTrace();
				}
				if ( "".equals( pagado.trim() ) && pagado.trim().length() == 0 )
					reintegrosDetalle.add( reintegro );
				else {
					reintegrosDetalle = null;
				}
			}
		}
		return reintegrosDetalle;
	}

	public int getDetallePasoTotal( int folio ) throws Exception {
		Connection conn = null;
		int total = 0;
		try {
			conn = getConnection();
			total = ReintegrosMilManager.getDetallePasoTotal( conn, folio );
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return total;
	}

	public String clcPagada( int nCLC, String cxp ) throws Exception {
		Connection conn = null;
		String res = "";
		try {
			conn = getConnection();
			res = ReintegrosMilManager.getCLCPagada( conn, nCLC, cxp );
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public String AutorizaReintegroNuevo( Caso c, String nNumSicop, String cRecMotivSicop, String nNumMAP, String cRecMotivMAP, Map<?, ?> m, String prefixPath, String uLogin, Usuario usuario, String fAcredit, String fin_anio ) throws SQLException {

		// List<String> arrLResult = null;
		ArrayList<String> arrLResult = new ArrayList<String>();
		Connection conn = null;
		String cMensaje = "";
		String validaMes = "";
		CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
		try {
			conn = getConnection();
			int folio = new Integer( c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 ) ).intValue();

			validaMes = ReintegrosMilManager.validaMes( conn, c, folio, fAcredit );

			if ( "S".equals( validaMes ) ) {

				ReintegrosMilManager.autorizaReintegro( conn, c, nNumSicop, cRecMotivSicop, nNumMAP, cRecMotivMAP, uLogin, prefixPath, fAcredit, adecProy.obtenEjercicioFiscal() );
				ReintegroEncabezadoMil re = ReintegrosMilManager.getReintegroEncabezadoNuevo( conn, folio );

				/*
				 * Si el año actual es diferente el ejercicio fiscal se
				 * aplicaran los reintegros de Fin de Año
				 */
				if ( "S".equals( fin_anio ) ) {
					ReintegrosMilManager.updateReintegroMilFA( conn, folio );
				}

				ContableInterface conInt = new AplicacionContable();
				log.debug( "Inicia Autorización aplicacion contable " + new Timestamp( System.currentTimeMillis() ) );

				AplicarContableReturn acr;
				if ( !"2012".equals( adecProy.obtenEjercicioFiscal() ) )
					acr = conInt.aplicarContableNuevo( conn, c, "tReintegroAutEncabezadoMil", "tReintegroAutDetalleMil", "nFolioReintegroMilaut", new Integer( c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 ) ).intValue(), "REINTEGROAUTMIL", m, prefixPath, uLogin, "" );
				else
					acr = conInt.aplicarContableNuevo( conn, c, "tReintegroAutEncabezadoMil", "tReintegroAutDetalleMil", "nFolioReintegroMilaut", new Integer( c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 ) ).intValue(), "REINTEGROAUTMIL", m, prefixPath, uLogin, "SI" );

				arrLResult = ( ArrayList<String> ) acr.getMessageList();

				log.debug( "Termina Autorización Aplicacion contable " + new Timestamp( System.currentTimeMillis() ) );

				Calendar cal = new GregorianCalendar();
				String mesActual = Util.NOMBRE_MESES_MX[cal.get( Calendar.MONTH )];
				Caso cReloaded = new Caso();
				cReloaded.setIdCaso( c.getIdCaso() );
				cReloaded = CasoManager.select( conn, cReloaded );
				if ( acr.isSuccess() ) {
					conn.commit();

					String usuarioRevisor = ReintegrosManager.getCorreoRevisor( conn, c );
					String usuariosBitacora = ReintegrosManager.getListaCorreos( conn, c );
					String to = usuarioRevisor;
					String cc = "";
					// if(!"".equals(to) && to!=null)
					// cc=";";
					cc += usuariosBitacora;
					String bcc = "";
					String from = "";
					String fromName = "Avisos de Reintegro";
					String asuntoCorreo = "Aviso de Reintegro " + c.getFolio() + " (Ejercicio " + adecProy.obtenEjercicioFiscal() + ")";

					String body = ( !correoProduccion ? "CORREO DE PRUEBA <br>" : "" ) + ( !correoProduccion ? "este correo le hubiera llegado a: " + to + cc + "<br> <br>" : "" ) + "Cierre de " + mesActual + " de " + adecProy.obtenEjercicioFiscal() + "<br><br>" + "Para su conocimiento y efectos correspondientes, se le informa que ha sido autorizado en SIAFF y SICOP el reintegro " + "por $ " + re.getImporteLC() + "  con el folio siguiente: " + c.getFolio() + "<br>" + "Mismo que ya se encuentra con estatus de autorizado en el SAI con el No. " + c.getFolio() + ", " + "para su consulta de los reportes correspondientes.";
					try {
						if ( !correoProduccion ) {
							to = "arlopeza@axtel.com.mx;";
							cc = "vgarciac@axtel.com.mx";
						}
						AlarmaManager.procesaAlarmaCNF( conn, prefixPath, c.getCasoOperacion( 0 ), c, asuntoCorreo, to, cc, bcc, body );
					} catch ( Exception exmail ) {
						log.error( "No se logro enviar el correo de autorizacion de reintegros: " + exmail );
					}
					cbl.avanzaCaso( cReloaded, uLogin, "", new String [] { "CONSULTA_REINTEGRO_MIL" }, new String [] { "consulta_reintegro_m" }, m, prefixPath );
				} else {
					conn.rollback();
				}
			} else {
				arrLResult.add( "El mes de aplicacion esta cerrado contablemente, favor de notificar a contabilidad o cambiar la fecha de aplicacion." );
			}
		} catch ( Exception exc ) {
			conn.rollback();
			log.error( exc );
			arrLResult.add( exc.getLocalizedMessage() );

			try {
				conn.rollback();
			} catch ( Exception ex ) {
				log.warn( "En Rollback", ex );
			}
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		Iterator<String> iteraMensajes = arrLResult.iterator();
		while ( iteraMensajes.hasNext() ) {
			cMensaje += iteraMensajes.next();
		}
		return cMensaje;
	}

	@SuppressWarnings( { "unchecked" } )
	public ArrayList AutorizaReintegro( Caso c, String nNumSicop, String cRecMotivSicop, Map m, String prefixPath, String uLogin, String cSuperReduccion ) throws SQLException {

		ArrayList arrLResult = new ArrayList();
		Connection conn = null;

		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			ContableInterface conInt = new AplicacionContable();
			log.debug( "Inicia Autorización aplicacion contable " + new Timestamp( System.currentTimeMillis() ) );

			AplicarContableReturn acr;
			if ( !"2012".equals( adecProy.obtenEjercicioFiscal() ) )
				acr = conInt.aplicarContableNuevo( conn, c, "tReintegroAutEncabezadoMil", "tReintegroAutDetalleMil", "nFolioReintegroMilaut", new Integer( c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 ) ).intValue(), "REINTEGROAUTMIL", m, prefixPath, uLogin, "" );
			else
				acr = conInt.aplicarContableNuevo( conn, c, "tReintegroAutEncabezadoMil", "tReintegroAutDetalleMil", "nFolioReintegroMilaut", new Integer( c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 ) ).intValue(), "REINTEGROAUTMIL", m, prefixPath, uLogin, "SI" );

			arrLResult = ( ArrayList<String> ) acr.getMessageList();

			log.debug( "Termina Autorización Aplicacion contable " + new Timestamp( System.currentTimeMillis() ) );

			Caso cReloaded = new Caso();
			cReloaded.setIdCaso( c.getIdCaso() );
			cReloaded = CasoManager.select( conn, cReloaded );
			if ( acr.isSuccess() ) {
				conn.commit();
				cbl.avanzaCaso( cReloaded, uLogin, "", new String [] { "CONSULTA_REINTEGRO_MIL" }, new String [] { "consulta_reintegro_m" }, m, prefixPath );
			} else {
				conn.rollback();
				// cbl.avanzaCaso(cReloaded, uLogin, "", new String[] {
				// "AUTORIZADOR_REINTEGRO" }, new String[] {
				// "autoriza_reintegro" }, m, prefixPath);
			}
		} catch ( Exception exc ) {
			conn.rollback();
			log.error( exc );
			arrLResult.add( exc.getLocalizedMessage() );

			try {
				conn.rollback();
			} catch ( Exception ex ) {
				log.warn( "En Rollback", ex );
			}
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return arrLResult;
	}

	public String cancelarAppContableNuevo( Caso c, Map<?, ?> m, String prefixPath, String uLogin, String cFecha, Usuario usuario ) throws Exception {
		String retVal = null;
		Connection conn = null;

		try {
			ContableInterface ci = new AplicacionContable();
			// conn = getConnection();
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();

			AplicarContableReturn acr = ci.cancelarAppContableNueva( conn, c, "tReintegroEncabezadoMil", "tReintegroDetalleMil", "nFolioReintegroMil", new Integer( c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 ) ).intValue(), "REINTEGROMIL", m, prefixPath, uLogin, cFecha );

			Caso cReloaded = new Caso();
			cReloaded.setIdCaso( c.getIdCaso() );
			cReloaded = CasoManager.select( conn, cReloaded );
			if ( acr.isSuccess() ) {
				conn.commit();
				String to = usuario.getU_email();
				String body = ( !correoProduccion ? "CORREO DE PRUEBA <br>" : "" ) + ( !correoProduccion ? "este correo le hubiera llegado a: " + to + "<br> <br>" : "" ) + "Para su conocimiento y efectos correspondientes, se le informa que <b>fue cancelado</b> el folio siguiente:<br>" + c.getTipoCaso().getDescripcion() + " No. SAI: <b>" + c.getFolio() + "</b><br>" + "Mismo que ya cuenta con estatus de cancelado en el SAI.";
				try {
					if ( !correoProduccion )
						to = "vgarciac@axtel.com.mx;";
					AlarmaManager.procesaAlarmaCNF( conn, prefixPath, c.getCasoOperacion( 0 ), c, "", to, body );
				} catch ( Exception exmail ) {
					log.error( "No se logro enviar el correo de cancelacion de reintegros: " + exmail );
				}
				cbl.avanzaCaso( cReloaded, uLogin, "", new String [] { "CONSULTA_REINTEGRO_MIL" }, new String [] { "consulta_reintegro_mil" }, m, prefixPath );
			} else {
				conn.rollback();
			}
			retVal = acr.getMessageList().get( acr.getMessageList().size() - 1 );

			cbl.avanzaCaso( cReloaded, uLogin, "", new String [] { "CONSULTA_REINTEGROMIL" }, new String [] { "consulta_reintegro_m" }, m, prefixPath );
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;
		}

		return retVal;
	}

	public String ValidaReintegro( int nIdCaso, Usuario usuario, String aEjercicioFiscal, String cRamo, String cUR, Caso c, String cCentroContable, String cFechaAplica, Map<?, ?> m, String prefixPath, String uLogin ) throws Exception {
		List<String> arrLResult = null;
		Connection conn = null;
		String cMensaje = "";
		try {
			String DATE_FORMAT = "dd/MM/yyyy";
			SimpleDateFormat sdf = new SimpleDateFormat( DATE_FORMAT );
			Calendar c1 = Calendar.getInstance(); // today
			String today = sdf.format( c1.getTime() );
			final int folio = new Integer( c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 ) ).intValue();
			actualizaFechaAplicacionMil( folio, today );
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			ContableInterface conInt = new AplicacionContable();
			log.debug( "Inicia Autorización aplicacion contable " + new Timestamp( System.currentTimeMillis() ) );

			AplicarContableReturn acr;
			if ( !"2012".equals( adecProy.obtenEjercicioFiscal() ) )
				acr = conInt.aplicarContableNuevo( conn, c, "tReintegroEncabezadoMil", "tReintegroDetalleMil", "nFolioReintegroMil", new Integer( c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 ) ).intValue(), "REINTEGROMIL", m, prefixPath, uLogin, "" );
			else
				acr = conInt.aplicarContableNuevo( conn, c, "tReintegroEncabezadoMil", "tReintegroDetalleMil", "nFolioReintegroMil", new Integer( c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 ) ).intValue(), "REINTEGROMIL", m, prefixPath, uLogin, "SI" );

			arrLResult = acr.getMessageList();

			log.debug( "Termina Autorización Aplicacion contable " + new Timestamp( System.currentTimeMillis() ) );

			Caso cReloaded = new Caso();
			cReloaded.setIdCaso( c.getIdCaso() );
			cReloaded = CasoManager.select( conn, cReloaded );
			if ( acr.isSuccess() ) {
				conn.commit();
				cbl.avanzaCaso( cReloaded, uLogin, "", new String [] { "AUTORIZADOR_REINTEGRO_MIL" }, new String [] { "genera_layout_mil" }, m, prefixPath );
			} else {
				conn.rollback();
				// cbl.avanzaCaso(cReloaded, uLogin, "", new String[] {
				// "REVISOR_REINTEGRO" }, new String[] { "revisa_reintegro" },
				// m, prefixPath);
			}
		} catch ( Exception exc ) {
			conn.rollback();
			log.error( exc );
			arrLResult.add( exc.getLocalizedMessage() );

			try {
				conn.rollback();
			} catch ( Exception ex ) {
				log.warn( "En Rollback", ex );
			}
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		Iterator<String> iteraMensajes = arrLResult.iterator();
		while ( iteraMensajes.hasNext() ) {
			cMensaje += iteraMensajes.next();
		}
		return cMensaje;
	}

	public String getTipoPoliza( String tipoPoliza ) throws SQLException {
		Connection conn = null;
		String res = "";
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();

			res = ReintegrosMilManager.getTipoPoliza( conn, tipoPoliza );
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public double getRemanente( String EP, String CXP, int docRenglon, String cxpnomina ) throws SQLException {
		Connection conn = null;
		double res = 0.00;
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();

			res = ReintegrosMilManager.getRemanente( conn, EP, CXP.trim(), docRenglon, cxpnomina );
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public String getcPartida( String EP ) throws SQLException {
		Connection conn = null;
		String res = "";
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();

			res = ReintegrosMilManager.getcPartida( conn, EP );
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public String getEntidadFederativa( String EP ) throws SQLException {
		Connection conn = null;
		String res = "";
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();

			res = ReintegrosMilManager.getEntidadFederativa( conn, EP );
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public String getTipoPago( String cxp ) throws SQLException {
		Connection conn = null;
		String res = "";
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			res = ReintegrosMilManager.getTipoPago( conn, cxp );
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public ReintegroEncabezado getReintegroEncabezado( int nFolio ) throws SQLException {
		Connection conn = null;
		ReintegroEncabezado re = new ReintegroEncabezado();
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			re = ReintegrosMilManager.getReintegroEncabezado( conn, nFolio );
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return re;
	}

	public ReintegroEncabezadoMil getReintegroEncabezadoNuevo( int nFolio ) throws SQLException {
		Connection conn = null;
		ReintegroEncabezadoMil re = new ReintegroEncabezadoMil();
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			re = ReintegrosMilManager.getReintegroEncabezadoNuevo( conn, nFolio );
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return re;
	}

	public boolean getReintegroHApartado( int nFolio ) throws SQLException { // SIRVE
																				// PARA
																				// SABER
																				// SI
																				// YA
																				// HICIERON
																				// LA
																				// PRIMERA
																				// APLICACION
																				// CONTABLE
		// Y QUE NO LA VUELVAN A APLICAR
		Connection conn = null;
		boolean res = false;
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			res = ReintegrosMilManager.getReintegroHApartado( conn, nFolio );
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public ArrayList<ReintegroDetalle> getReintegroLayout( int folio ) throws SQLException {
		Connection conn = null;
		ArrayList<ReintegroDetalle> detalles;
		try {
			conn = getConnection();
			detalles = ReintegrosMilManager.getReintegroLayout( conn, folio );
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return detalles;
	}

	public boolean getEPCatalogo( String EP ) throws SQLException {
		boolean existe = false;
		Connection conn = null;
		try {
			conn = getConnection();
			existe = ReintegrosMilManager.getEPCatalogo( conn, EP );
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return existe;
	}

	public boolean actualizaInfoPagos( int nFolio, String clvRastreo, String lc, String ficha, String clvBanco, String cuenta, String fechaAcredit, String ctab ) throws SQLException {
		Connection conn = null;
		boolean res = false;
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			res = ReintegrosMilManager.actualizaInfoPagos( conn, nFolio, clvRastreo, lc, ficha, clvBanco, cuenta, fechaAcredit, ctab );
			conn.commit();
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public int secCLC( String caNoContrarrecibo, String EP, int folio ) throws SQLException {
		Connection conn = null;
		int res = 0;
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			res = ReintegrosMilManager.secCLC( conn, caNoContrarrecibo, EP, folio );
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public int secCLCRect( String caNoContrarrecibo, String EP ) throws SQLException {
		Connection conn = null;
		int res = 0;
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			res = ReintegrosMilManager.secCLCRect( conn, caNoContrarrecibo, EP );
			conn.commit();
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public String tipoConcepto( String caNoContrarrecibo, String EP ) throws SQLException {
		Connection conn = null;
		String res = "";
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			res = ReintegrosMilManager.tipoConcepto( conn, caNoContrarrecibo, EP );
			conn.commit();
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public String folioDependencia( String caNoContrarrecibo, String EP, int folio ) throws SQLException {
		Connection conn = null;
		String res = "";
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			res = ReintegrosMilManager.folioDependencia( conn, caNoContrarrecibo, EP, folio );
			conn.commit();
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public String getRFC( String caNoContrarrecibo, String ep ) throws SQLException {
		Connection conn = null;
		String res = "";
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			res = ReintegrosMilManager.getRFC( conn, caNoContrarrecibo, ep );
			conn.commit();
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public int getNDocRenglon( String caNoContrarrecibo, String EP, int cMes, String cxp, String movimiento, String concepto ) throws SQLException {
		Connection conn = null;
		int res = 0;
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			res = ReintegrosMilManager.getNDocRenglon( conn, caNoContrarrecibo, EP, cMes, cxp, movimiento, concepto );
			conn.commit();
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public boolean borraReintegro( int folio ) throws Exception {
		Connection conn = null;
		boolean borra = false;
		try {
			conn = getConnection();
			borra = ReintegrosMilManager.borraReintegro( conn, folio );
			conn.commit();
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new Exception( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}
		return borra;
	}

	public boolean validaCatMovimiento( String texto ) throws SQLException {
		Connection conn = null;
		boolean res = false;
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			res = ReintegrosMilManager.validaCatMovimiento( conn, texto );
			conn.commit();
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public boolean validaCatTipoAviso( String texto ) throws SQLException {
		Connection conn = null;
		boolean res = false;
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			res = ReintegrosMilManager.validaCatTipoAviso( conn, texto );
			conn.commit();
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public boolean validaCatFormaPago( String texto ) throws SQLException {
		Connection conn = null;
		boolean res = false;
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			res = ReintegrosMilManager.validaCatFormaPago( conn, texto );
			conn.commit();
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public boolean validaCatCausaAviso( String texto ) throws SQLException {
		Connection conn = null;
		boolean res = false;
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			res = ReintegrosMilManager.validaCatCausaAviso( conn, texto );
			conn.commit();
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public ReintegroDetalleMil datosSICOPMil( String caNoContrarrecibo, String EP, String movimiento, String concepto ) throws SQLException {
		Connection conn = null;
		ReintegroDetalleMil res = new ReintegroDetalleMil();
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			res = ReintegrosMilManager.datosSICOPMil( conn, caNoContrarrecibo, EP, movimiento, concepto );
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public int getRenglonPagadoMil( String caNoContrarrecibo, String EP, int cMes, int folio, String concepto, int movimiento ) throws SQLException {
		Connection conn = null;
		int res = -1;
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			res = ReintegrosMilManager.getRenglonPagadoMil( conn, caNoContrarrecibo, EP, cMes, folio, concepto, movimiento );
			conn.commit();
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public String getALM( String caNoContrarrecibo, String EP, int cMes, String cxp ) throws SQLException {
		Connection conn = null;
		String res = "";
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			res = ReintegrosMilManager.getALM( conn, caNoContrarrecibo, EP, cMes, cxp );
			conn.commit();
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public boolean getTipoCLC( int folioRein ) throws SQLException {
		Connection conn = null;
		boolean res = false;
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			res = ReintegrosManager.getTipoCLC( conn, folioRein );
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public String getCCNormal( String caNoContrarrecibo, String EP, int cMes, int folio, String cxpsai ) throws SQLException {
		Connection conn = null;
		String res = "";
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			res = ReintegrosMilManager.getCCNormal( conn, caNoContrarrecibo, EP, cMes, cxpsai );
			conn.commit();
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public void insertaReintegro( ReintegroEncabezadoMil reinE, ArrayList<ReintegroDetalleMil> reinDetalles, int folio, String folioCompleto, Usuario usuario, Caso c ) throws Exception, SQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			ReintegrosMilManager.insertaReintegro( conn, reinE, reinDetalles, folio, folioCompleto, usuario, c );
			ReintegrosMilManager.insertaReintegroPasivo( conn, folio );
			conn.commit();
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new Exception( exc );
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
	}

	public boolean actualizaFechaAplicacionMil( int folio, String fAplicacion ) throws SQLException, ParseException {
		Connection conn = null;
		boolean res = false;
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			res = ReintegrosMilManager.actualizaFechaAplicacionMil( conn, folio, fAplicacion );
			conn.commit();
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public String getCTAB( String caNoContrarrecibo, String ep ) throws SQLException, ParseException {
		Connection conn = null;
		String res = "";
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			res = ReintegrosMilManager.getCTAB( conn, caNoContrarrecibo, ep );
			conn.commit();
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return res;
	}

	public String eventoReintegroMil( String caNoContrarrecibo, String EP ) throws SQLException {
		Connection conn = null;
		String evento = "";
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			evento = ReintegrosMilManager.eventoReintegroMil( conn, caNoContrarrecibo, EP );
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}

		return evento;
	}

}
