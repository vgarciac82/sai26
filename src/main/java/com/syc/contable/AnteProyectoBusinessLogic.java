package com.syc.contable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.syc.cfdi.utils.CloseObject;
import com.syc.contable.core.AdecuacionManager;
import com.syc.contable.core.AnteProyectoAut;
import com.syc.contable.core.AnteProyectoAutCalendario;
import com.syc.contable.core.AnteProyectoManager;
import com.syc.contable.core.CatEPAnteP;
import com.syc.contable.core.EstructuraProgramaticaManager;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDato;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;

public class AnteProyectoBusinessLogic  extends DataSourceManager {
	
	private static Logger log = Logger.getLogger(AnteProyectoBusinessLogic.class);

	public AnteProyectoBusinessLogic(String jniName) {
		super.init(jniName);
	}

	public ArrayList<ArrayList<String>> cargaArchivoXLS(String archivo, Caso c, Usuario usuario, String cUnidadEjecutora, String aEjercicioFiscal) throws Exception {
		ArrayList arrmMontosCalendario = new ArrayList<>();
		ArrayList<String> arrmMontosErrore = new ArrayList<String>();
		Connection conn = null;
		String cFileExcel;
		int IterRegElx=0;
		int IterCelElx=0;
		// campos para lectura
		int nConsecutivo=0;
		String cConsecutivo="";
		String cEP="";
		Double mCalculado=0.0;
		String cCalculado="";
		String cOptimo="";
		String cIreductible="";
		String cErrorFile="";
		Double mOptimo=0.0;
		Double mIreductible=0.0;
		//String cReduccion="";
		//String cIncremento="";
		int iReduccion=0;
		int iIncremento=0;
		String cUnidadResponsable="";
		String cEjercicioFiscal="";
		try{
			cUnidadResponsable=usuario.getU_UR();
			conn = getConnection();
			int nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();

			cFileExcel = archivo.substring(0, archivo.length() - 3) + "xls";

			File fFileExcel = new File(archivo.substring(0, archivo.length() - 3) + "xls");
			File fFileOrig = new File(archivo);

			FileChannel in = (new FileInputStream(fFileOrig)).getChannel();
			FileChannel out = (new FileOutputStream(fFileExcel)).getChannel();
			in.transferTo(0, fFileOrig.length(), out);
			in.close();
			out.close();
			FileInputStream fileInputStream = new FileInputStream(cFileExcel);
			try{
				POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);
				HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
				HSSFSheet hssfSheet = workBook.getSheetAt(0);
				IterRegElx++;
				
				AnteProyectoManager.borraDetalleAnteproyecto(conn, cUnidadEjecutora, nFolio);
				Iterator<?> rowIterator = hssfSheet.rowIterator();
				while (rowIterator.hasNext()) {
					//System.out.println("Importando excel de ante proyecto rnglon:"+IterRegElx+ " Numero de Folio:"+nFolio);
					HSSFRow hssfRow = (HSSFRow) rowIterator.next();
					Iterator<?> iterator = hssfRow.cellIterator();
					IterCelElx=0;
					if (IterRegElx >= 2) {
						ArrayList<Object> arrmCalendario = new ArrayList<>();
						while (iterator.hasNext()) {
							IterCelElx++;
							HSSFCell hssfCell = (HSSFCell) iterator.next();
							if (nConsecutivo == 0 && IterCelElx > 1){
								break;
							}
							if (IterCelElx == 1) {
								try{
									nConsecutivo = (int) hssfCell.getNumericCellValue();
								} catch (Exception ex) {
									try{
										cConsecutivo = new String(hssfCell.getStringCellValue());
										nConsecutivo= new Integer(cConsecutivo.trim()).intValue();
									}catch (Exception e){
										arrmCalendario.add("ERROR");
										cErrorFile+="ERROR:Archivo fuera de estandar en la linea:"+IterRegElx+ " Numero de Folio:"+nFolio+" del Consecutivo."+ e.getMessage();
										break;
									}
								}
								arrmCalendario.add(nConsecutivo);
							} else if (IterCelElx == 2) {
								try{
									cEP = (String) hssfCell.getStringCellValue();
									cEjercicioFiscal=cEP.substring(0, 4);
									arrmCalendario.add(cEP);
									arrmCalendario.add(cEjercicioFiscal);
									
								} catch (Exception ex) {
									arrmCalendario.add("ERROR");
									arrmCalendario.add("ERROR");
									cErrorFile+="ERROR:Archivo fuera de estandar en la linea:"+IterRegElx+ " Numero de Folio:"+nFolio+" de la EP.";
								}
							} else if (IterCelElx == 3) {
								try{
									mCalculado =  new Double(hssfCell.getNumericCellValue());
									arrmCalendario.add(mCalculado);
								} catch (Exception ex) {
									try{
										cCalculado = (String) hssfCell.getStringCellValue();
										mCalculado = new Double (cCalculado.replace(",", ""));
										arrmCalendario.add(mCalculado);
									}catch (Exception e) {
										arrmCalendario.add("ERROR");
										cErrorFile+="ERROR:Archivo fuera de estandar en la linea:"+IterRegElx+ " Numero de Folio:"+nFolio+" del Calculado."+e.getMessage();
									}
								}
							} else if (IterCelElx == 4) {
								try{
									mOptimo = new Double(hssfCell.getNumericCellValue());
									arrmCalendario.add(mOptimo);
								} catch (Exception ex) {
									try{
										cOptimo = (String) hssfCell.getStringCellValue();
										mOptimo = new Double (cOptimo.replace(",", ""));
										arrmCalendario.add(mOptimo);
									}catch (Exception e) {
										arrmCalendario.add("ERROR");
										cErrorFile+="ERROR:Archivo fuera de estandar en la linea:"+IterRegElx+ " Numero de Folio:"+nFolio+" del Optimo.";
									}
								}
							} else if (IterCelElx == 5) {
								try{
									mIreductible = new Double(hssfCell.getNumericCellValue());
									arrmCalendario.add(mIreductible);
								} catch (Exception ex) {
									try{
										cIreductible = (String) hssfCell.getStringCellValue();
										mIreductible = new Double (cIreductible.replace(",", ""));
										arrmCalendario.add(mIreductible);
									}catch (Exception e) {
										arrmCalendario.add("ERROR");
										cErrorFile+="ERROR:Archivo fuera de estandar en la linea:"+IterRegElx+ " Numero de Folio:"+nFolio+" del Ireductible."+e.getMessage();
									}
								}
							} else if (IterCelElx == 6) {
								try{
									iReduccion = new  Integer(hssfCell.getStringCellValue().trim()).intValue();
									arrmCalendario.add(iReduccion);
								} catch (Exception ex) {
									try{
										iReduccion = (int) hssfCell.getNumericCellValue();
										arrmCalendario.add(iReduccion);
									}catch (Exception exi){
										arrmCalendario.add("ERROR");
										cErrorFile+="ERROR:Archivo fuera de estandar en la linea:"+IterRegElx+ " Numero de Folio:"+nFolio+" del Reduccion."+exi.getMessage();
									}
								}
							} else if (IterCelElx == 7) {
								try{
									iIncremento = new  Integer(hssfCell.getStringCellValue().trim()).intValue();
									arrmCalendario.add(iIncremento);
								} catch (Exception ex) {
									try{
										iIncremento = (int) hssfCell.getNumericCellValue();
										arrmCalendario.add(iIncremento);
									}catch (Exception exi){
										arrmCalendario.add("ERROR");
										cErrorFile+="ERROR:Archivo fuera de estandar en la linea:"+IterRegElx+ " Numero de Folio:"+nFolio+" del Incremento."+exi.getMessage();
									}
								}
							}
							//inserta o actualiza registro validando estructura de le EP
							if (IterCelElx == 5 ){//&& bErrorArchivo == false) {
								System.out.println("nFolio:"+ nFolio + ", nConsecutivo:"+nConsecutivo );
								arrmCalendario.add(EstructuraProgramaticaManager.validaRMPOG(conn, cEP, aEjercicioFiscal, "EPA"));
								//ArrayList arrmObtenDatos = new ArrayList();
								String cSIAFF = cEP;
								String cInterna = "";
								arrmCalendario.add(cErrorFile);
								AnteProyectoManager.insertaDetalleAnteProyecto(conn, nFolio, nConsecutivo, cSIAFF,cInterna, mCalculado, mOptimo, mIreductible, iReduccion, iIncremento, cUnidadEjecutora, cEjercicioFiscal);
								//arrmCalendario.add(AnteProyectoManager.ValidaDetalleAnte(conn, cUnidadResponsable, nFolio, nConsecutivo, cEP, mCalculado, mOptimo, mIreductible, iReduccion, iIncremento, cEjercicioFiscal, c, true));
								//arrmCalendario.add(arrmObtenDatos); 
								arrmMontosCalendario.add(arrmCalendario);
								nConsecutivo=0;
								break;
							}/*else if (bErrorArchivo == true){
								break;
							}*/
						}
					}
					IterRegElx++;
				}
				//se valida el archivo completo
				workBook.close();
				conn.commit();
				arrmMontosErrore=AnteProyectoManager.ValidaAnteProyectoEP(conn, cUnidadResponsable, nFolio, aEjercicioFiscal);
				arrmMontosCalendario.add(arrmMontosErrore);	

				CasoDato cd = new CasoDato();
				cd.setIdCaso(c.getIdCaso());
				cd.setIdCD(9);
				cd.setIdTC(c.getIdTC());
				cd.setValor("NULL");
				 
				CasoDatoManager.update(conn, cd);
				cd.setIdCD(8);
				cd.setIdTC(c.getIdTC());
				cd.setValor("NULL");
				//CasoDatoManager cdm = new CasoDatoManager();
				CasoDatoManager.update(conn, cd);
				cd.setIdCD(11);
				cd.setIdTC(c.getIdTC());
				cd.setValor("NULL");
				//CasoDatoManager cdm = new CasoDatoManager();
				CasoDatoManager.update(conn, cd);
				conn.commit();
			}
			catch(OfficeXmlFileException e){
				throw new Exception("El archivo de excel debe estar en formato .xls (1997-2003)\\n");
			}

		} finally {
			if (conn != null) {
				conn.close();
			}
			conn = null;
		}
		return arrmMontosCalendario;
	}
	
	public boolean cargaArchivoXLSR(String archivo, Caso c, Usuario usuario) throws Exception {
		ArrayList<Object> arrmMontosCalendario = new ArrayList<>();
		ArrayList arrmEstatusRechazo = new ArrayList<>();
		Connection conn = null;
		String cFileExcel;
		int IterRegElx=0;
		int IterCelElx=0;
		// campos para lectura
		int nConsecutivo=0;
		String cConsecutivo="";
		String cEP="";
		Double mCalculado=0.0;
		String cCalculado="";
		Double mOptimo=0.0;
		Double mIreductible=0.0;
		int iReduccion=0;
		int iIncremento=0;
		String cUnidadResponsable="";
		String cEjercicioFiscal="";
		boolean bRechazoUN=false;
		//boolean bActualizado=false;
		String cErrorFile="";
		String cOptimo="";
		String cIreductible="";

		try{
			cUnidadResponsable=usuario.getU_UR();
			conn = getConnection();
			int nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();

			cFileExcel = archivo.substring(0, archivo.length() - 3) + "xls";

			File fFileExcel = new File(archivo.substring(0, archivo.length() - 3) + "xls");
			File fFileOrig = new File(archivo);

			FileChannel in = (new FileInputStream(fFileOrig)).getChannel();
			FileChannel out = (new FileOutputStream(fFileExcel)).getChannel();
			in.transferTo(0, fFileOrig.length(), out);
			in.close();
			out.close();
			FileInputStream fileInputStream = new FileInputStream(cFileExcel);
			POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);

			HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
			HSSFSheet hssfSheet = workBook.getSheetAt(0);
			IterRegElx++;

			Iterator<?> rowIterator = hssfSheet.rowIterator();
			while (rowIterator.hasNext()) {
				System.out.println("Importando excel de ante proyecto rnglon:"+IterRegElx+ " Numero de Folio:"+nFolio);
				HSSFRow hssfRow = (HSSFRow) rowIterator.next();
				Iterator<?> iterator = hssfRow.cellIterator();
				IterCelElx=0;
				if (IterRegElx >= 2) {
					while (iterator.hasNext()) {
						ArrayList<Object> arrmCalendario = new ArrayList<>();
						IterCelElx++;
						HSSFCell hssfCell = (HSSFCell) iterator.next();
						if (IterCelElx == 1) {
							try{
								nConsecutivo = (int) hssfCell.getNumericCellValue();
							} catch (Exception ex) {
								try{
									cConsecutivo = new String(hssfCell.getStringCellValue());
									nConsecutivo= new Integer(cConsecutivo.trim()).intValue();
								}catch (Exception e){
									arrmCalendario.add("ERROR");
									//cErrorFile="ERROR:Archivo fuera de estandar en la linea:"+IterRegElx+ " Numero de Folio:"+nFolio+" del Consecutivo."+ e.getMessage();
									cErrorFile="ERROR:Archivo fuera de estandar en la linea:"+IterRegElx+ " Numero de Folio:"+nFolio+" del Consecutivo.";
									break;
								}
							}
							arrmCalendario.add(nConsecutivo);
						} else if (IterCelElx == 2) {
							try{
								cEP = (String) hssfCell.getStringCellValue();
								cEjercicioFiscal=cEP.substring(0, 4);
								arrmCalendario.add(cEP);
								arrmCalendario.add(cEjercicioFiscal);
							} catch (Exception ex) {
								arrmCalendario.add("ERROR");
								arrmCalendario.add("ERROR");
								cErrorFile+="ERROR:Archivo fuera de estandar en la linea:"+IterRegElx+ " Numero de Folio:"+nFolio+" de la EP.";
							}
						} else if (IterCelElx == 3) {
							try{
								mCalculado =  new Double(hssfCell.getNumericCellValue());
								arrmCalendario.add(mCalculado);
							} catch (Exception ex) {
								try{
									cCalculado = (String) hssfCell.getStringCellValue();
									mCalculado = new Double (cCalculado);
									arrmCalendario.add(mCalculado);
								}catch (Exception e) {
									arrmCalendario.add("ERROR");
									//cErrorFile+="ERROR:Archivo fuera de estandar en la linea:"+IterRegElx+ " Numero de Folio:"+nFolio+" del Calculado."+e.getMessage();
									cErrorFile+="ERROR:Archivo fuera de estandar en la linea:"+IterRegElx+ " Numero de Folio:"+nFolio+" del Calculado.";
								}
							}
						} else if (IterCelElx == 4) {
							try{
								mOptimo = new Double(hssfCell.getNumericCellValue());
								arrmCalendario.add(mOptimo);
							} catch (Exception ex) {
								try{
									cOptimo = (String) hssfCell.getStringCellValue();
									mOptimo = new Double (cCalculado);
									arrmCalendario.add(mOptimo);
								}catch (Exception e) {
									arrmCalendario.add("ERROR");
									//cErrorFile+="ERROR:Archivo fuera de estandar en la linea:"+IterRegElx+ " Numero de Folio:"+nFolio+" del Optimo.";
									cErrorFile+="ERROR:Archivo fuera de estandar en la linea:"+IterRegElx+ " Numero de Folio:"+nFolio+" del Optimo.";
								}
							}
						} else if (IterCelElx == 5) {
							try{
								mIreductible = new Double(hssfCell.getNumericCellValue());
								arrmCalendario.add(mIreductible);
							} catch (Exception ex) {
								try{
									cIreductible = (String) hssfCell.getStringCellValue();
									mIreductible = new Double (cCalculado);
									arrmCalendario.add(mIreductible);
								}catch (Exception e) {
									arrmCalendario.add("ERROR");
									//cErrorFile+="ERROR:Archivo fuera de estandar en la linea:"+IterRegElx+ " Numero de Folio:"+nFolio+" del Ireductible."+e.getMessage();
									cErrorFile+="ERROR:Archivo fuera de estandar en la linea:"+IterRegElx+ " Numero de Folio:"+nFolio+" del Ireductible.";
								}
						}
						} else if (IterCelElx == 6) {
							try{
								iReduccion = new  Integer(hssfCell.getStringCellValue().trim()).intValue();
								arrmCalendario.add(iReduccion);
							} catch (Exception ex) {
								try{
									iReduccion = (int) hssfCell.getNumericCellValue();
									arrmCalendario.add(iReduccion);
								}catch (Exception exi){
									arrmCalendario.add("ERROR");
									//cErrorFile+="ERROR:Archivo fuera de estandar en la linea:"+IterRegElx+ " Numero de Folio:"+nFolio+" del Reduccion."+exi.getMessage();
									cErrorFile+="ERROR:Archivo fuera de estandar en la linea:"+IterRegElx+ " Numero de Folio:"+nFolio+" del Reduccion.";
								}
							}
						} else if (IterCelElx == 7) {
							try{
								iIncremento = new  Integer(hssfCell.getStringCellValue().trim()).intValue();
								arrmCalendario.add(iIncremento);
							} catch (Exception ex) {
								try{
									iIncremento = (int) hssfCell.getNumericCellValue();
									arrmCalendario.add(iIncremento);
								}catch (Exception exi){
									arrmCalendario.add("ERROR");
									//cErrorFile+="ERROR:Archivo fuera de estandar en la linea:"+IterRegElx+ " Numero de Folio:"+nFolio+" del Incremento."+exi.getMessage();
									cErrorFile+="ERROR:Archivo fuera de estandar en la linea:"+IterRegElx+ " Numero de Folio:"+nFolio+" del Incremento.";
								}
							}
						}
						//inserta o actualiza registro validando estructura de le EP
						if (IterCelElx == 7 && !"".equals(cEP) ) {
							System.out.println("nFolio:"+ nFolio + ", nConsecutivo:"+nConsecutivo );
							arrmEstatusRechazo=AnteProyectoManager.ValidaDetalleAnte(conn, cUnidadResponsable, nFolio, nConsecutivo, cEP, mCalculado, mOptimo, mIreductible, iReduccion, iIncremento, cEjercicioFiscal, c, true );
							arrmCalendario.add(arrmEstatusRechazo);
							arrmMontosCalendario.add(arrmCalendario);
							bRechazoUN=true;
							break;
						}
					}
				}
				IterRegElx++;
			}
			//se invoca la actualizACION DEL REGISTRO PARA INDICAR QUE FE RECHAZADO
			AnteProyectoManager.MarcaEstatus(conn, cUnidadResponsable, nFolio, "Revisor", "R");
			workBook.close();
			conn.commit();
		} finally {
			
			if (conn != null) {
				conn.close();
			}
			conn = null;
		}
		//arrmEstatusRechazo.add(bRechazoUN);
		//arrmEstatusRechazo.add(arrmMontosCalendario);
		return true;
	}

	public  int CreaAntePRoyecto(Caso c, Usuario usuario, String nCuenta, int nFolioAnteProyecto, int nPorcentajeReduccion, int nPorcentajeAmpliacion, String cUnidadEjecutora, String cAutorizado, String cDescripcion, String cCampo, String cUnidadNormativa) throws SQLException{
		Connection conn = null;
		try{
			conn = getConnection();
			String aEjercicioFiscal = AdecuacionManager.obtenEjercicioFiscal(conn);
			String aEjercicio = AdecuacionManager.obtenEjercicioFiscal(conn);
			
			int nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
			AnteProyectoManager.creaHederAnteProyecto(c, usuario, conn, nFolio, aEjercicioFiscal, aEjercicio, nCuenta, nPorcentajeAmpliacion, nPorcentajeReduccion, cAutorizado, cDescripcion, cUnidadEjecutora, cCampo, cUnidadNormativa);
			AnteProyectoManager.creaDetalleAnteProyecto(conn, nFolio, nCuenta, nPorcentajeAmpliacion, nPorcentajeReduccion, cUnidadEjecutora, cUnidadNormativa, nFolioAnteProyecto);
			conn.commit();
		}catch (SQLException e) {
			e.printStackTrace();
			conn.rollback();
		}finally {
			if (conn != null) {
				conn.close();
			}
			conn = null;
		}
		return 1; 
	}

	public  ArrayList<ArrayList<String>> CreaAntePRoyectoXLS(Caso c, Usuario usuario, String nCuenta, int nFolioAnteProyecto, int nPorcentajeReduccion, int nPorcentajeAmpliacion, String cUnidadEjecutora, String cAutorizado, String cDescripcion, String cCampo, String cUnidadNormativa, String archivo) throws Exception{
		ArrayList<ArrayList<String>> arrmDatosAnteProyecto = new ArrayList<ArrayList<String>>();
		Connection conn = null;
		try{
			conn = getConnection();
			String aEjercicioFiscal = AdecuacionManager.obtenEjercicioFiscal(conn);
			String aEjercicio = AdecuacionManager.obtenEjercicioFiscal(conn);
			
			int nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
			AnteProyectoManager.creaHederAnteProyecto(c, usuario, conn, nFolio, aEjercicioFiscal, aEjercicio, nCuenta, nPorcentajeAmpliacion, nPorcentajeReduccion, cAutorizado, cDescripcion, cUnidadEjecutora, cCampo, cUnidadNormativa);
			//AnteProyectoManager.creaDetalleAnteProyecto(conn, nFolio, nCuenta, nPorcentajeAmpliacion, nPorcentajeReduccion, cUnidadEjecutora, cUnidadNormativa, nFolioAnteProyecto);
			arrmDatosAnteProyecto=cargaArchivoXLS(archivo, c, usuario, cUnidadEjecutora, aEjercicioFiscal);
			conn.commit();			
		}catch (SQLException e) {
			e.printStackTrace();
			conn.rollback();
		}catch(Exception ex){
			throw new Exception(ex.getMessage());
		}finally {
			CloseObject.closeObject( conn, false );
		}
		return arrmDatosAnteProyecto; 
	}
	
	public ArrayList<ArrayList<String>> RecuperaTechos(String cUnidadResponsable, String cRolUsuario) throws SQLException {
		ArrayList<ArrayList<String>> arrmDatosAnteProyecto = new ArrayList<ArrayList<String>>();
		ArrayList<String> arrmTechoUN = new ArrayList<String>();
		ArrayList<String> arrmTechoUE = new ArrayList<String>();
		ArrayList<String> arrmTechoEF = new ArrayList<String>();
		ArrayList<String> arrmTechoPP = new ArrayList<String>();
		ArrayList<String> arrmTechoPA = new ArrayList<String>();
		ArrayList<String> arrmVerciones = new ArrayList<String>();
		ArrayList arrmUniNorma = new ArrayList<>();
		ArrayList<String> arrmTechoPPUE = new ArrayList<String>();
		ArrayList<String> arrmTechoPAUE = new ArrayList<String>();
		Connection conn = null;
		
		try{
			conn = getConnection();
			arrmTechoUN=AnteProyectoManager.obtenTechoUN(conn, cUnidadResponsable, cRolUsuario);
			arrmDatosAnteProyecto.add(arrmTechoUN);
			arrmTechoEF=AnteProyectoManager.obtenTechoEF(conn, cRolUsuario);
			arrmDatosAnteProyecto.add(arrmTechoEF);
			arrmTechoUE=AnteProyectoManager.obtenTechoUR(conn, cUnidadResponsable, cRolUsuario);
			arrmDatosAnteProyecto.add(arrmTechoUE);
			arrmTechoPP=AnteProyectoManager.obtenTechoPP(conn, cRolUsuario);
			arrmDatosAnteProyecto.add(arrmTechoPP);
			arrmTechoPA=AnteProyectoManager.obtenTechoPA(conn, cUnidadResponsable, cRolUsuario);
			arrmDatosAnteProyecto.add(arrmTechoPA);
			arrmVerciones=AnteProyectoManager.VercionesAnteproyecto(conn, cUnidadResponsable, cRolUsuario);
			arrmDatosAnteProyecto.add(arrmVerciones);
			arrmUniNorma=AnteProyectoManager.UnidadesNormativas(conn);
			arrmDatosAnteProyecto.add(arrmUniNorma);
			arrmUniNorma=AnteProyectoManager.UnidadesEjecutoras(conn);
			arrmDatosAnteProyecto.add(arrmUniNorma);
			arrmTechoPPUE=AnteProyectoManager.obtenTechoUEPP(conn, cRolUsuario, cUnidadResponsable);
			arrmDatosAnteProyecto.add(arrmTechoPPUE);
			arrmTechoPAUE=AnteProyectoManager.obtenTechoPAUE(conn, cUnidadResponsable, cRolUsuario);
			arrmDatosAnteProyecto.add(arrmTechoPAUE);
			
		}finally{
			if (conn != null) {
				conn.close();
			}
			conn = null;
		}
		
		return arrmDatosAnteProyecto;
	}

	public ArrayList<ArrayList<String>> RecuperaAnteProyecto(String cUnidadResponsable, Caso c) throws SQLException {
		ArrayList<ArrayList<String>> arrmDatosAnteProyecto = new ArrayList<>();
		Connection conn = null;
		
		try{
			conn = getConnection();
			int nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
			String aEjercicioFiscal = AdecuacionManager.obtenEjercicioFiscal(conn);

			arrmDatosAnteProyecto=AnteProyectoManager.obtenAnteProyecto(conn, cUnidadResponsable, nFolio, aEjercicioFiscal, c);
			
		}finally{
			if (conn != null) {
				conn.close();
			}
			conn = null;
		}
		
		return arrmDatosAnteProyecto;
		
	}

	public ArrayList<String> ValidaTechos(Caso c, Usuario usuario, String cUnidadResponsable) throws SQLException{
		//String cErrorValida="";
		ArrayList<String> arrmMensajeTechos = new ArrayList<>();
		int nFolio =0;
		Connection conn = null;
		try{
			conn = getConnection();
			nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
			arrmMensajeTechos=AnteProyectoManager.ValidaTechoUN(conn, nFolio, cUnidadResponsable);
		}finally{
			if (conn != null) {
				conn.close();
			}
			conn = null;
		}
		return arrmMensajeTechos;
	}

	public ArrayList<String> techoPAUEXLS(String archivo, Caso c, Usuario usuario) throws Exception{
		ArrayList arrmMontosCalendario = new ArrayList<>();
		Connection conn = null;
		String cCentroContable = "0";
		String cFileExcel;
		int IterRegElx=0;
		int IterCelElx=0;
		String cUnidadResponsable="";
		int nConsecutivo=0;
		String cPartida="";
		String cTipoGasto="";
		String cUnidadEjecutora="";
		Double mTecho=0.0;
		try{
			cUnidadResponsable=usuario.getU_UR();
			conn = getConnection();
			if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
				cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
			}
			int nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();

			cFileExcel = archivo.substring(0, archivo.length() - 3) + "xls";

			File fFileExcel = new File(archivo.substring(0, archivo.length() - 3) + "xls");
			File fFileOrig = new File(archivo);

			FileChannel in = (new FileInputStream(fFileOrig)).getChannel();
			FileChannel out = (new FileOutputStream(fFileExcel)).getChannel();
			in.transferTo(0, fFileOrig.length(), out);
			in.close();
			out.close();
			FileInputStream fileInputStream = new FileInputStream(cFileExcel);
			POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);

			HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
			HSSFSheet hssfSheet = workBook.getSheetAt(0);
			IterRegElx++;

			Iterator<?> rowIterator = hssfSheet.rowIterator();
			while (rowIterator.hasNext()) {
				System.out.println("Importando excel de ante proyecto rnglon:"+IterRegElx+ " Numero de Folio:"+nFolio);
				HSSFRow hssfRow = (HSSFRow) rowIterator.next();
				Iterator<?> iterator = hssfRow.cellIterator();
				//List cellTempList = new ArrayList<>();
				IterCelElx=0;
				if (IterRegElx >= 2) {
					while (iterator.hasNext()) {
						ArrayList<Object> arrmCalendario = new ArrayList<>();
						IterCelElx++;
						HSSFCell hssfCell = (HSSFCell) iterator.next();
						if (IterCelElx == 1) {
							cPartida = (String) hssfCell.getStringCellValue();
							arrmCalendario.add(cPartida);
						} else if (IterCelElx == 2) {
							cTipoGasto = (String) hssfCell.getStringCellValue();
							arrmCalendario.add(cTipoGasto);
						} else if (IterCelElx == 3){
							cUnidadEjecutora= (String) hssfCell.getStringCellValue();
							arrmCalendario.add(cUnidadEjecutora);
						} else if (IterCelElx == 4){
							mTecho = (Double) hssfCell.getNumericCellValue();
							arrmCalendario.add(mTecho);
						}
						//inserta o actualiza registro validando estructura de le EP
						if (IterCelElx == 7) {
							System.out.println("nFolio:"+ nFolio + ", nConsecutivo:"+nConsecutivo );
							AnteProyectoManager.InsertaTechoPAUE(conn, cPartida, cTipoGasto,cUnidadEjecutora, mTecho);
							arrmMontosCalendario.add(arrmCalendario);
							break;
						}
					}
				}
				IterRegElx++;
			}
			conn.commit();
			workBook.close();
		} finally {
			if (conn != null) {
				conn.close();
			}
			conn = null;
		}
		return arrmMontosCalendario;
	}

	public ArrayList<String> techoPPUEXLS(String archivo, Caso c, Usuario usuario) throws Exception{
		ArrayList arrmMontosCalendario = new ArrayList<>();
		Connection conn = null;
		String cCentroContable = "0";
		String cFileExcel;
		int IterRegElx=0;
		int IterCelElx=0;
		String cUnidadResponsable="";
		int nConsecutivo=0;
		String cProgramaPresupuestario="";
		String cUnidadEjecutora="";
		Double mTecho=0.0;
		try{
			cUnidadResponsable=usuario.getU_UR();
			conn = getConnection();
			if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
				cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
			}
			int nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();

			cFileExcel = archivo.substring(0, archivo.length() - 3) + "xls";

			File fFileExcel = new File(archivo.substring(0, archivo.length() - 3) + "xls");
			File fFileOrig = new File(archivo);

			FileChannel in = (new FileInputStream(fFileOrig)).getChannel();
			FileChannel out = (new FileOutputStream(fFileExcel)).getChannel();
			in.transferTo(0, fFileOrig.length(), out);
			in.close();
			out.close();
			FileInputStream fileInputStream = new FileInputStream(cFileExcel);
			POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);

			HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
			HSSFSheet hssfSheet = workBook.getSheetAt(0);
			IterRegElx++;

			Iterator<?> rowIterator = hssfSheet.rowIterator();
			while (rowIterator.hasNext()) {
				System.out.println("Importando excel de ante proyecto rnglon:"+IterRegElx+ " Numero de Folio:"+nFolio);
				HSSFRow hssfRow = (HSSFRow) rowIterator.next();
				Iterator<?> iterator = hssfRow.cellIterator();
				//List cellTempList = new ArrayList();
				IterCelElx=0;
				if (IterRegElx >= 2) {
					while (iterator.hasNext()) {
						ArrayList<Object> arrmCalendario = new ArrayList<>();
						IterCelElx++;
						HSSFCell hssfCell = (HSSFCell) iterator.next();
						if (IterCelElx == 1) {
								cProgramaPresupuestario = (String) hssfCell.getStringCellValue();
							arrmCalendario.add(cProgramaPresupuestario);
						} else if (IterCelElx == 2) {
							cUnidadEjecutora=(String) hssfCell.getStringCellValue();
							arrmCalendario.add(cUnidadEjecutora);
						}  else if (IterCelElx == 3) {
							mTecho=(Double) hssfCell.getNumericCellValue();
							arrmCalendario.add(mTecho);
						}
						//inserta o actualiza registro validando estructura de le EP
						if (IterCelElx == 7) {
							System.out.println("nFolio:"+ nFolio + ", nConsecutivo:"+nConsecutivo );
							//AnteProyectoManager.ValidaDetalleAnte(conn, cUnidadResponsable, nFolio, nConsecutivo, cEP, mCalculado, mOptimo, mIreductible, iReduccion, iIncremento, cEjercicioFiscal);
							arrmMontosCalendario.add(arrmCalendario);
							break;
						}
					}
				}
				IterRegElx++;
			}
			conn.commit();
			workBook.close();
		} finally {
			if (conn != null) {
				conn.close();
			}
			conn = null;
		}
		return arrmMontosCalendario;
		
	}

	public ArrayList<String> techoEFXLS(String archivo, Caso c, Usuario usuario) throws Exception{
		ArrayList arrmMontosCalendario = new ArrayList<>();
		Connection conn = null;
		String cFileExcel;
		int IterRegElx=0;
		int IterCelElx=0;
		int nConsecutivo=0;
		String cEntidadFederativa="";
		Double mTecho=0.0;;
		try{
			conn = getConnection();
			int nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();

			cFileExcel = archivo.substring(0, archivo.length() - 3) + "xls";

			File fFileExcel = new File(archivo.substring(0, archivo.length() - 3) + "xls");
			File fFileOrig = new File(archivo);

			FileChannel in = (new FileInputStream(fFileOrig)).getChannel();
			FileChannel out = (new FileOutputStream(fFileExcel)).getChannel();
			in.transferTo(0, fFileOrig.length(), out);
			in.close();
			out.close();
			FileInputStream fileInputStream = new FileInputStream(cFileExcel);
			POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);

			HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
			HSSFSheet hssfSheet = workBook.getSheetAt(0);
			IterRegElx++;

			Iterator<?> rowIterator = hssfSheet.rowIterator();
			while (rowIterator.hasNext()) {
				System.out.println("Importando excel de ante proyecto rnglon:"+IterRegElx+ " Numero de Folio:"+nFolio);
				HSSFRow hssfRow = (HSSFRow) rowIterator.next();
				Iterator<?> iterator = hssfRow.cellIterator();
				//List cellTempList = new ArrayList();
				IterCelElx=0;
				if (IterRegElx >= 2) {
					while (iterator.hasNext()) {
						ArrayList<Object> arrmCalendario = new ArrayList<>();
						IterCelElx++;
						HSSFCell hssfCell = (HSSFCell) iterator.next();
						if (IterCelElx == 1) {
							cEntidadFederativa = (String) hssfCell.getStringCellValue();
							arrmCalendario.add(cEntidadFederativa);
						} else if (IterCelElx == 2) {
							mTecho = (Double) hssfCell.getNumericCellValue();
							arrmCalendario.add(mTecho);
						} 
						//inserta o actualiza registro validando estructura de le EP
						if (IterCelElx == 7) {
							System.out.println("nFolio:"+ nFolio + ", nConsecutivo:"+nConsecutivo );
							//AnteProyectoManager.ValidaDetalleAnte(conn, cUnidadResponsable, nFolio, nConsecutivo, cEP, mCalculado, mOptimo, mIreductible, iReduccion, iIncremento, cEjercicioFiscal);
							arrmMontosCalendario.add(arrmCalendario);
							break;
						}
					}
				}
				IterRegElx++;
			}
			conn.commit();
			workBook.close();
		} finally {
			if (conn != null) {
				conn.close();
			}
			conn = null;
		}
		return arrmMontosCalendario;
		
	}

	public ArrayList<String> techoPAXLS(String archivo, Caso c, Usuario usuario) throws Exception{
		ArrayList arrmMontosCalendario = new ArrayList<>();
		Connection conn = null;
		String cCentroContable = "0";
		String cFileExcel;
		int IterRegElx=0;
		int IterCelElx=0;
		String cUnidadResponsable="";
		int nConsecutivo=0;
		String cPartida="";
		String cTipoGasto="";
		Double mTecho=0.0;
		try{
			cUnidadResponsable=usuario.getU_UR();
			conn = getConnection();
			if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
				cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
			}
			int nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();

			cFileExcel = archivo.substring(0, archivo.length() - 3) + "xls";

			File fFileExcel = new File(archivo.substring(0, archivo.length() - 3) + "xls");
			File fFileOrig = new File(archivo);

			FileChannel in = (new FileInputStream(fFileOrig)).getChannel();
			FileChannel out = (new FileOutputStream(fFileExcel)).getChannel();
			in.transferTo(0, fFileOrig.length(), out);
			in.close();
			out.close();
			FileInputStream fileInputStream = new FileInputStream(cFileExcel);
			POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);

			HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
			HSSFSheet hssfSheet = workBook.getSheetAt(0);
			IterRegElx++;

			Iterator<?> rowIterator = hssfSheet.rowIterator();
			while (rowIterator.hasNext()) {
				System.out.println("Importando excel de ante proyecto rnglon:"+IterRegElx+ " Numero de Folio:"+nFolio);
				HSSFRow hssfRow = (HSSFRow) rowIterator.next();
				Iterator<?> iterator = hssfRow.cellIterator();
				//List cellTempList = new ArrayList();
				IterCelElx=0;
				if (IterRegElx >= 2) {
					while (iterator.hasNext()) {
						ArrayList<Object> arrmCalendario = new ArrayList<>();
						IterCelElx++;
						HSSFCell hssfCell = (HSSFCell) iterator.next();
						if (IterCelElx == 1) {
							cPartida = (String) hssfCell.getStringCellValue();
							arrmCalendario.add(cPartida);
						} else if (IterCelElx == 2) {
							cTipoGasto = (String) hssfCell.getStringCellValue();
							arrmCalendario.add(cTipoGasto);
						} else if (IterCelElx == 3) {
							mTecho = (Double) hssfCell.getNumericCellValue();
							arrmCalendario.add(mTecho);
						} 
						//inserta o actualiza registro validando estructura de le EP
						if (IterCelElx == 7) {
							System.out.println("nFolio:"+ nFolio + ", nConsecutivo:"+nConsecutivo );
							//AnteProyectoManager.ValidaDetalleAnte(conn, cUnidadResponsable, nFolio, nConsecutivo, cEP, mCalculado, mOptimo, mIreductible, iReduccion, iIncremento, cEjercicioFiscal);
							arrmMontosCalendario.add(arrmCalendario);
							break;
						}
					}
				}
				IterRegElx++;
			}
			conn.commit();
			workBook.close();
		} finally {
			if (conn != null) {
				conn.close();
			}
			conn = null;
		}
		return arrmMontosCalendario;
		
	}

	public ArrayList<String> techoPPXLS(String archivo, Caso c, Usuario usuario) throws Exception{
		ArrayList arrmMontosCalendario = new ArrayList<>();
		Connection conn = null;
		String cFileExcel;
		int IterRegElx=0;
		int IterCelElx=0;
		int nConsecutivo=0;
		String cPRogramaPresupuestario="";
		Double mTecho=0.0;
		try{
			conn = getConnection();
			int nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();

			cFileExcel = archivo.substring(0, archivo.length() - 3) + "xls";

			File fFileExcel = new File(archivo.substring(0, archivo.length() - 3) + "xls");
			File fFileOrig = new File(archivo);

			FileChannel in = (new FileInputStream(fFileOrig)).getChannel();
			FileChannel out = (new FileOutputStream(fFileExcel)).getChannel();
			in.transferTo(0, fFileOrig.length(), out);
			in.close();
			out.close();
			FileInputStream fileInputStream = new FileInputStream(cFileExcel);
			POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);

			HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
			HSSFSheet hssfSheet = workBook.getSheetAt(0);
			IterRegElx++;

			Iterator<?> rowIterator = hssfSheet.rowIterator();
			while (rowIterator.hasNext()) {
				System.out.println("Importando excel de ante proyecto rnglon:"+IterRegElx+ " Numero de Folio:"+nFolio);
				HSSFRow hssfRow = (HSSFRow) rowIterator.next();
				Iterator<?> iterator = hssfRow.cellIterator();
				//List cellTempList = new ArrayList();
				IterCelElx=0;
				if (IterRegElx >= 2) {
					while (iterator.hasNext()) {
						ArrayList<Object> arrmCalendario = new ArrayList<>();
						IterCelElx++;
						HSSFCell hssfCell = (HSSFCell) iterator.next();
						if (IterCelElx == 1) {
							cPRogramaPresupuestario = (String) hssfCell.getStringCellValue();
							arrmCalendario.add(cPRogramaPresupuestario);
						} else if (IterCelElx == 2) {
							mTecho = (Double) hssfCell.getNumericCellValue();
							arrmCalendario.add(mTecho);
						}  
						//inserta o actualiza registro validando estructura de le EP
						if (IterCelElx == 3) {
							System.out.println("nFolio:"+ nFolio + ", nConsecutivo:"+nConsecutivo );
							//AnteProyectoManager.ValidaDetalleAnte(conn, cUnidadResponsable, nFolio, nConsecutivo, cEP, mCalculado, mOptimo, mIreductible, iReduccion, iIncremento, cEjercicioFiscal);
							arrmMontosCalendario.add(arrmCalendario);
							break;
						}
					}
				}
				IterRegElx++;
			}
			conn.commit();
			workBook.close();
		} finally {
			if (conn != null) {
				conn.close();
			}
			conn = null;
		}
		return arrmMontosCalendario;
		
	}
	
	public ArrayList<String> techoUEXLS(String archivo, Caso c, Usuario usuario) throws SQLException, IOException{
		ArrayList arrmMontosCalendario = new ArrayList<>();
		Connection conn = null;
		String cCentroContable = "0";
		String cFileExcel;
		int IterRegElx=0;
		int IterCelElx=0;
		String cUnidadResponsable="";
		int nConsecutivo=0;
		String cUnidadEjecutora="";
		Double mTecho=0.0;
		try{
			cUnidadResponsable=usuario.getU_UR();
			conn = getConnection();
			if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
				cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
			}
			int nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();

			cFileExcel = archivo.substring(0, archivo.length() - 3) + "xls";

			File fFileExcel = new File(archivo.substring(0, archivo.length() - 3) + "xls");
			File fFileOrig = new File(archivo);

			FileChannel in = (new FileInputStream(fFileOrig)).getChannel();
			FileChannel out = (new FileOutputStream(fFileExcel)).getChannel();
			in.transferTo(0, fFileOrig.length(), out);
			in.close();
			out.close();
			FileInputStream fileInputStream = new FileInputStream(cFileExcel);
			POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);

			HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
			HSSFSheet hssfSheet = workBook.getSheetAt(0);
			IterRegElx++;

			Iterator<?> rowIterator = hssfSheet.rowIterator();
			while (rowIterator.hasNext()) {
				System.out.println("Importando excel de ante proyecto rnglon:"+IterRegElx+ " Numero de Folio:"+nFolio);
				HSSFRow hssfRow = (HSSFRow) rowIterator.next();
				Iterator<?> iterator = hssfRow.cellIterator();
				//List cellTempList = new ArrayList();
				IterCelElx=0;
				if (IterRegElx >= 2) {
					while (iterator.hasNext()) {
						ArrayList<Object> arrmCalendario = new ArrayList<>();
						IterCelElx++;
						HSSFCell hssfCell = (HSSFCell) iterator.next();
						if (IterCelElx == 1) {
							cUnidadEjecutora = (String) hssfCell.getStringCellValue();
							arrmCalendario.add(cUnidadEjecutora);
						} else if (IterCelElx == 2) {
							mTecho = (Double) hssfCell.getNumericCellValue();
							arrmCalendario.add(mTecho);
						} 						//inserta o actualiza registro validando estructura de le EP
						if (IterCelElx == 7) {
							System.out.println("nFolio:"+ nFolio + ", nConsecutivo:"+nConsecutivo );
							//AnteProyectoManager.ValidaDetalleAnte(conn, cUnidadResponsable, nFolio, nConsecutivo, cEP, mCalculado, mOptimo, mIreductible, iReduccion, iIncremento, cEjercicioFiscal);
							arrmMontosCalendario.add(arrmCalendario);
							break;
						}
					}
				}
				IterRegElx++;
			}
			conn.commit();
			workBook.close();
		} finally {
			if (conn != null) {
				conn.close();
			}
			conn = null;
		}
		return arrmMontosCalendario;
		
	}
	
	public ArrayList<String> techoUNXLS(String archivo, Caso c, Usuario usuario) throws SQLException, IOException{
		ArrayList arrmMontosCalendario = new ArrayList<>();
		Connection conn = null;
		String cCentroContable = "0";
		String cFileExcel;
		int IterRegElx=0;
		int IterCelElx=0;
		String cUnidadResponsable="";
		int nConsecutivo=0;
		String cUnidadNormativa="";
		Double mTecho=0.0;
		try{
			cUnidadResponsable=usuario.getU_UR();
			conn = getConnection();
			if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
				cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
			}
			int nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();

			cFileExcel = archivo.substring(0, archivo.length() - 3) + "xls";

			File fFileExcel = new File(archivo.substring(0, archivo.length() - 3) + "xls");
			File fFileOrig = new File(archivo);

			FileChannel in = (new FileInputStream(fFileOrig)).getChannel();
			FileChannel out = (new FileOutputStream(fFileExcel)).getChannel();
			in.transferTo(0, fFileOrig.length(), out);
			in.close();
			out.close();
			FileInputStream fileInputStream = new FileInputStream(cFileExcel);
			POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);

			HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
			HSSFSheet hssfSheet = workBook.getSheetAt(0);
			IterRegElx++;

			Iterator<?> rowIterator = hssfSheet.rowIterator();
			while (rowIterator.hasNext()) {
				System.out.println("Importando excel de ante proyecto rnglon:"+IterRegElx+ " Numero de Folio:"+nFolio);
				HSSFRow hssfRow = (HSSFRow) rowIterator.next();
				Iterator<?> iterator = hssfRow.cellIterator();
				//List cellTempList = new ArrayList();
				IterCelElx=0;
				if (IterRegElx >= 2) {
					while (iterator.hasNext()) {
						ArrayList<Object> arrmCalendario = new ArrayList<>();
						IterCelElx++;
						HSSFCell hssfCell = (HSSFCell) iterator.next();
						if (IterCelElx == 1) {
							cUnidadNormativa = (String) hssfCell.getStringCellValue();
							arrmCalendario.add(nConsecutivo);
						} else if (IterCelElx == 2) {
							mTecho = (Double) hssfCell.getNumericCellValue();
							arrmCalendario.add(mTecho);
						}
						if (IterCelElx == 7) {
							System.out.println("nFolio:"+ nFolio + ", nConsecutivo:"+nConsecutivo );
							//AnteProyectoManager.ValidaDetalleAnte(conn, cUnidadResponsable, nFolio, nConsecutivo, cEP, mCalculado, mOptimo, mIreductible, iReduccion, iIncremento, cEjercicioFiscal);
							arrmMontosCalendario.add(arrmCalendario);
							break;
						}
					}
				}
				IterRegElx++;
			}
			conn.commit();
			workBook.close();
		} finally {
			if (conn != null) {
				conn.close();
			}
			conn = null;
		}
		return arrmMontosCalendario;
		
	}

	public int verificaUNUE(String cUnidadNormativa, String cUnidadEjecutora) throws SQLException{
		int ivReturn=0;
		Connection conn = null;
		try{
			conn = getConnection();
			ivReturn=AnteProyectoManager.ValidaUnUe(conn,cUnidadNormativa,cUnidadEjecutora);
		}finally{
			if (conn != null){
				conn.close();
			}
			conn = null;
		}
		return ivReturn;
	}

	public int cambiaEstatus(String cUnidadEjecutora, int nFolio, String cEstatus, String cQuien) throws SQLException{
		int iVerReturn=0;
		Connection conn = null;
		try{
			conn = getConnection();
			AnteProyectoManager.MarcaEstatus(conn, cUnidadEjecutora, nFolio, cQuien, cEstatus);
			if (!"Revisor".equals(cQuien)){
				AnteProyectoManager.ConsolidaAnteProyecto(conn,nFolio);
			}
			conn.commit();
		}finally{
			if (conn != null)
				conn.close();
			conn = null;
		}
		return iVerReturn;
	}

	public String getDescripcionClave(String cUnidadResponsable) throws SQLException{
		String descripcion="";
		Connection conn = null;
		try{
			conn = getConnection();
			descripcion=AnteProyectoManager.getDescripcionCUResponsable(conn,cUnidadResponsable);
		}finally{
			if (conn != null){
				conn.close();
			}
			conn = null;
		}
		return descripcion;
	}
	
	public String getEntidadFederativaClave(String cEntidadFederativa) throws SQLException{
		String descripcion="";
		Connection conn = null;
		try{
			conn = getConnection();
			descripcion=AnteProyectoManager.getEntidadFederativaClave(conn,cEntidadFederativa);
		}finally{
			if (conn != null){
				conn.close();
			}
			conn = null;
		}
		return descripcion;
	}

	public String getProgramaPresupuestario(String cProgramaPresupuestario) throws SQLException{
		String descripcion="";
		Connection conn = null;
		try{
			conn = getConnection();
			descripcion=AnteProyectoManager.getProgramaPresupuestario(conn,cProgramaPresupuestario);
		}finally{
			if (conn != null){
				conn.close();
			}
			conn = null;
		}
		return descripcion;
	}
	
	public String getTipoGasto(String cTipoGasto) throws SQLException{
		String descripcion="";
		Connection conn = null;
		try{
			conn = getConnection();
			descripcion=AnteProyectoManager.getTipoGasto(conn, cTipoGasto);
		}finally{
			if (conn != null){
				conn.close();
			}
			conn = null;
		}
		return descripcion;
	}

	public String getPartidaCatalogo(String cPartida) throws SQLException{
		String descripcion="";
		Connection conn = null;
		try{
			conn = getConnection();
			descripcion=AnteProyectoManager.getPartidaCatalogo(conn, cPartida);
		}finally{
			if (conn != null){
				conn.close();
			}
			conn = null;
		}
		return descripcion;
	}

	public String getUnidadResponsable(String cUR) throws SQLException{
		String descripcion="";
		Connection conn = null;
		try{
			conn = getConnection();
			descripcion=AnteProyectoManager.getUnidadResponsable(conn, cUR);
		}finally{
			if (conn != null){
				conn.close();
			}
			conn = null;
		}
		return descripcion;
	}

	public void insertaTechoUE(String c, double monto) throws NumberFormatException, SQLException{
	    Connection conn = null;
		try{
			conn = getConnection();
			AnteProyectoManager.InsertaTechoUE(conn, c, monto);
		}finally{
		    conn.commit();
			if (conn != null){
				conn.close();
			}
			conn = null;
		}
	}
	
	public void insertaTechoUN(String c, double monto) throws NumberFormatException, SQLException{
	    Connection conn = null;
		try{
			conn = getConnection();
			AnteProyectoManager.InsertaTechoUN(conn, c, monto);
		}finally{
		    conn.commit();
			if (conn != null){
				conn.close();
			}
			conn = null;
		}
	}
	
	public void insertaTechoEn(String c, double monto) throws NumberFormatException, SQLException{
	    Connection conn = null;
		try{
			conn = getConnection();
			AnteProyectoManager.InsertaTechoEn(conn, c, monto);
		}finally{
		    conn.commit();
			if (conn != null){
				conn.close();
			}
			conn = null;
		}
	}

	
	public void insertaTechoPP(String c, double monto) throws NumberFormatException, SQLException{
	    Connection conn = null;
		try{
			conn = getConnection();
			AnteProyectoManager.InsertaTechoPP(conn, c, monto);
		}finally{
		    conn.commit();
			if (conn != null){
				conn.close();
			}
			conn = null;
		}
	}
	
	public void insertaTechoPartida(String c, String c2, double monto) throws NumberFormatException, SQLException{
	    Connection conn = null;
		try{
			conn = getConnection();
			AnteProyectoManager.InsertaTechoPA(conn, c, c2, monto);
		}finally{
		    conn.commit();
			if (conn != null){
				conn.close();
			}
			conn = null;
		}
	}
	
	public void insertaTechoPPUE(String c, String c2, double monto) throws NumberFormatException, SQLException{
	    Connection conn = null;
		try{
			conn = getConnection();
			AnteProyectoManager.InsertaTechoPPUE(conn, c, c2, monto);
		}finally{
		    conn.commit();
			if (conn != null){
				conn.close();
			}
			conn = null;
		}
	}
	
	public void insertaTechoPartidaUE(String c, String c2, String c3, double monto) throws NumberFormatException, SQLException{
	    Connection conn = null;
		try{
			conn = getConnection();
			AnteProyectoManager.InsertaTechoPartidaUE(conn, c, c2, c3, monto);
		}finally{
		    conn.commit();
			if (conn != null){
				conn.close();
			}
			conn = null;
		}
	}


	public void insertaTechoPartidaUN(String c, String c2, String c3, double monto) throws NumberFormatException, SQLException{
	    Connection conn = null;
		try{
			conn = getConnection();
			AnteProyectoManager.InsertaTechoPartidaUN(conn, c, c2, c3, monto);
		}finally{
		    conn.commit();
			if (conn != null){
				conn.close();
			}
			conn = null;
		}
	}

	public void borraTecho(String cUnidad, String tabla, String campo, String campo2, String valor2, String campo3, String valor3) throws NumberFormatException, SQLException{
	    Connection conn = null;
		try{
			conn = getConnection();
			AnteProyectoManager.borraTecho(conn, cUnidad, tabla, campo,campo2,valor2,campo3,valor3);
		}finally{
		    conn.commit();
			if (conn != null){
				conn.close();
			}
			conn = null;
		}
	}

	public void borraTechoUN(String cUnidad, String tabla, String campo, String campo2, String valor2, String campo3, String valor3) throws NumberFormatException, SQLException{
	    Connection conn = null;
		try{
			conn = getConnection();
			AnteProyectoManager.borraTechoUN(conn, cUnidad, tabla, campo,campo2,valor2,campo3,valor3);
		}finally{
		    conn.commit();
			if (conn != null){
				conn.close();
			}
			conn = null;
		}
	}

	public int getTechoExiste(String cUnidad, String tabla, String campo, String campo2, String valor2, String campo3, String valor3) throws NumberFormatException, SQLException{
	    Connection conn = null;
	    int i=0;
		try{
			conn = getConnection();
			i = AnteProyectoManager.getTechoExiste(conn, cUnidad, tabla, campo, campo2, valor2, campo3, valor3);
		}finally{
			if (conn != null){
				conn.close();
			}
			conn = null;
		}
		return i;
	}
	
	public HashMap getTechos(String tabla) throws NumberFormatException, SQLException{
	    Connection conn = null;
	    HashMap contenedor = new HashMap();
		try{
			conn = getConnection();
			contenedor = AnteProyectoManager.getTechos(conn,tabla);
		}finally{
			if (conn != null){
				conn.close();
			}
			conn = null;
		}
		return contenedor;
	}

	public ArrayList<String> cargaArchivoXLSi(String archivo, Caso c, Usuario usuario) throws Exception {
		ArrayList arrmMontosCalendario = new ArrayList<>();
		Connection conn = null;
		String cCentroContable = "0";
		String cFileExcel;
		int IterRegElx=0;
		int IterCelElx=0;
		int i=0;
		// campos para lectura
		int nConsecutivo=0;
		String cConsecutivo="";
		String cEP="";
		Double mCalculado=0.0;
		//String cCalculado="";
		Double mOptimo=0.0;
		Double mIreductible=0.0;
		int iReduccion=0;
		int iIncremento=0;
		String cUnidadResponsable="";
		String cEjercicioFiscal="";

		try{
			cUnidadResponsable=usuario.getU_UR();
			conn = getConnection();
			if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
				cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
			}
			int nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();

			cFileExcel = archivo.substring(0, archivo.length() - 3) + "xls";

			File fFileExcel = new File(archivo.substring(0, archivo.length() - 3) + "xls");
			File fFileOrig = new File(archivo);

			FileChannel in = (new FileInputStream(fFileOrig)).getChannel();
			FileChannel out = (new FileOutputStream(fFileExcel)).getChannel();
			in.transferTo(0, fFileOrig.length(), out);
			in.close();
			out.close();
			FileInputStream fileInputStream = new FileInputStream(cFileExcel);
			POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);

			HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
			HSSFSheet hssfSheet = workBook.getSheetAt(0);
			IterRegElx++;

			Iterator<?> rowIterator = hssfSheet.rowIterator();
			while (rowIterator.hasNext()) {
				System.out.println("Importando excel de ante proyecto rnglon:"+IterRegElx+ " Numero de Folio:"+nFolio);
				HSSFRow hssfRow = (HSSFRow) rowIterator.next();
				Iterator<?> iterator = hssfRow.cellIterator();
				//List cellTempList = new ArrayList();
				IterCelElx=0;
				if (IterRegElx >= 2) {
					while (iterator.hasNext()) {
						ArrayList<Object> arrmCalendario = new ArrayList<>();
						IterCelElx++;
						HSSFCell hssfCell = (HSSFCell) iterator.next();
						if (IterCelElx == 1) {
							try{
								nConsecutivo = (int) hssfCell.getNumericCellValue();
							} catch (Exception ex) {
								cConsecutivo = new String(hssfCell.getStringCellValue());
								nConsecutivo= new Integer(cConsecutivo.trim()).intValue();
							}
							arrmCalendario.add(nConsecutivo);
						} else if (IterCelElx == 2) {
							cEP = (String) hssfCell.getStringCellValue();
							cEjercicioFiscal=cEP.substring(0, 4);
							arrmCalendario.add(cEP);
							arrmCalendario.add(cEjercicioFiscal);
						} else if (IterCelElx == 3) {
							try{
								mCalculado =  new Double(hssfCell.getNumericCellValue());
								arrmCalendario.add(mCalculado);
							} catch (Exception ex) {
							}
						} else if (IterCelElx == 4) {
							try{
								mOptimo = new Double(hssfCell.getNumericCellValue());
								arrmCalendario.add(mOptimo);
							} catch (Exception ex) {
							}
						} else if (IterCelElx == 5) {
							try{
								mIreductible = new Double(hssfCell.getNumericCellValue());
								arrmCalendario.add(mIreductible);
							} catch (Exception ex) {
							}
						} else if (IterCelElx == 6) {
							try{
								iReduccion = new  Integer(hssfCell.getStringCellValue().trim()).intValue();
								arrmCalendario.add(iReduccion);
							} catch (Exception ex) {
							}
						} else if (IterCelElx == 7) {
							try{
								iIncremento = new  Integer(hssfCell.getStringCellValue().trim()).intValue();
								arrmCalendario.add(iIncremento);
							} catch (Exception ex) {
							}

						} 
						//inserta o actualiza registro validando estructura de le EP
						if (IterCelElx == 7) {
							System.out.println("nFolio:"+ nFolio + ", nConsecutivo:"+nConsecutivo );
							arrmCalendario.add(AnteProyectoManager.ValidaDetalleAnte(conn, cUnidadResponsable, nFolio, nConsecutivo, cEP, mCalculado, mOptimo, mIreductible, iReduccion, iIncremento, cEjercicioFiscal, c, true));
							arrmMontosCalendario.add(arrmCalendario);
							break;
						}
					}
				}
				IterRegElx++;
			}
			conn.commit();
			workBook.close();
		} finally {
			conn.close();
			conn = null;
		}
		return arrmMontosCalendario;
	}
	
	public boolean isUNormativa(String cUnidadResponsable) throws SQLException{
	    Connection conn = null;
	    boolean normativa=false;
		try{
			conn = getConnection();
			normativa = AnteProyectoManager.isUNormativa(conn, cUnidadResponsable);
		}finally{
			if (conn != null){
				conn.close();
			}
			conn = null;
		}
		return normativa;
	}

	public ArrayList<String> getConfSubcuenta(String cSubCuenta, int nEjercicioFiscal, String cEP, Map<String,String> mapValues ) throws SQLException{
		//ArrayList arrmDatosJSP = new ArrayList<>();
		ArrayList<String> arrTablasSubCuenta = new ArrayList<>();
		Connection conn = null;
		//int i=0;
		try{
			if (cEP == null)
				cEP="";
			conn = getConnection();
			arrTablasSubCuenta=AnteProyectoManager.ObtenTablaSubcuenta(conn, cSubCuenta, nEjercicioFiscal, cEP, mapValues );
			conn.commit();
		} finally {
			if (conn != null){
				conn.close();
			}
			conn = null;
		}
		return arrTablasSubCuenta;		
	}

	public String validaClaveEP(String cClaveEP, int IterRegElx) throws Exception{
		String cErrorEP="";
		String cEjercicioFiscal="";
		int iEjercicioFiscal=0;
		Connection conn=null;
		try{
			conn = getConnection();
			cEjercicioFiscal = AdecuacionManager.obtenEjercicioFiscal(conn);
			iEjercicioFiscal= new Integer (cEjercicioFiscal).intValue();
			iEjercicioFiscal++;
			cEjercicioFiscal=Integer.toString(iEjercicioFiscal);
			AdecuacionBusinessLogic   adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
			cErrorEP=adecProy.validaClaveEP(conn, cClaveEP, cEjercicioFiscal, 1);
			
		}finally{
			
		}
		return cErrorEP;
	}
	
	public ArrayList<AnteProyectoAut> getCAnteProyectoAut(String cUnidadResponsable) throws SQLException{
		Connection conn=null;
		ArrayList<AnteProyectoAut> res;
		try{
			conn = getConnection();
			res = AnteProyectoManager.getCAnteProyectoAut(conn, cUnidadResponsable);
		}finally {
			if (conn != null) {
				conn.close();
			}
			conn = null;
		}
		return res;
	}
	
	public boolean insertaCAnteProyectoAut(ArrayList<AnteProyectoAutCalendario> apa, String cCentroContable) throws SQLException{
		Connection conn=null;
		boolean folio=false;
		try{
			conn = getConnection();
			folio = AnteProyectoManager.insertaCAnteProyectoAut(conn, apa, cCentroContable);
			conn.commit();
		}catch (SQLException e) {
			if(conn != null){
				e.printStackTrace();
				conn.rollback();
			}
		}finally {
			if (conn != null) {
				conn.close();
			}
			conn = null;
		}
		return folio;
	}
	
	public boolean getExisteDetalleAut(AnteProyectoAutCalendario apac) throws SQLException{
		boolean existe = false;
		Connection conn = null;
		try{
			conn = getConnection();
			existe = AnteProyectoManager.getExisteDetalleAut(conn, apac);
		}finally {
			if (conn != null) {
				conn.close();
			}
			conn = null;
		}
		return existe;
	}
	
	public ArrayList<CatEPAnteP> getCatEPAnteProy(String query) throws SQLException{
		ArrayList<CatEPAnteP> cateps;
		Connection conn = null;
		try{
			conn = getConnection();
			cateps = AnteProyectoManager.getCatEPAnteProy(conn, query);
		}finally {
			if (conn != null) {
				conn.close();
			}
			conn = null;
		}
		return cateps;
	}
	
	public boolean getRepetidoCalendario(AnteProyectoAutCalendario apac) throws SQLException{
		boolean repetido = false;
		Connection conn = null;
		try{
			conn = getConnection();
			repetido = AnteProyectoManager.getRepetidoCalendario(conn, apac);
			conn.commit();
		}catch (SQLException e) {
			if(conn != null){
				e.printStackTrace();
				conn.rollback();
			}
		}finally {
			if (conn != null) {
				conn.close();
			}
			conn = null;
		}
		return repetido;
	}
	
	public ArrayList<ArrayList<String>> getUnidadesEjecutoras() throws SQLException{
		ArrayList<ArrayList<String>> unidades;
		Connection conn = null;
		try{
			conn = getConnection();
			unidades = AnteProyectoManager.UnidadesEjecutoras(conn);
		}finally {
			if (conn != null) {
				conn.close();
			}
			conn = null;
		}
		return unidades;
	}


	public int marcaAutoImport(Caso c) throws SQLException{
		Connection conn = null;
		int retVar=0;
		try{
			conn = getConnection();
			CasoDato cd = new CasoDato();
			cd.setIdCaso(c.getIdCaso());
			cd.setIdCD(9);
			cd.setIdTC(c.getIdTC());
			cd.setValor("true");
			
			CasoDatoManager.update(conn, cd);
			conn.commit();
			
		}finally{
			if (conn != null) {
				conn.close();
			}
			conn = null;
		}
		
		return retVar;
	}
}


