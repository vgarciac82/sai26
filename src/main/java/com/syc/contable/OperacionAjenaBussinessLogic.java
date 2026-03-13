package com.syc.contable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.axtel.egresos.CLC_SOLXPAGAR;
import com.syc.contable.core.OperacionAjenaManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.GestionException;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class OperacionAjenaBussinessLogic extends DataSourceManager {

	private static Logger log = Logger.getLogger(AdecuacionBusinessLogic.class);

	public OperacionAjenaBussinessLogic(String jniName) {

		super.init(jniName);
	}

	public ArrayList<String> buscaCompromisos(String listaFolios, String listaCuentaBancaria, String listaFechas, String ListaLeyendas, String sUsuario) throws Exception {
		ArrayList<String> arrListaComp = null;

		Connection conn = null;

		try {
			conn = getConnection();
			arrListaComp = OperacionAjenaManager.BuscaCompromisos(conn, listaFolios, listaCuentaBancaria, listaFechas, ListaLeyendas, sUsuario);

		} catch (SQLException e) {
			log.error(e,e);
			if (conn != null) {
				try{
					conn.rollback();
				}catch(Exception e2){
					log.warn(e2,e2);
				}
			}
			throw new GestionException(e);
		} finally {
			CloseObject.closeObject( conn );
		}
		return arrListaComp;
	}

	public boolean ActualizaStatus(String listaFolios) throws Exception {
		Connection conn = null;

		try {
			conn = getConnection();
			OperacionAjenaManager.UpdateStatus(conn, listaFolios);
			conn.commit();
		} catch (SQLException e) {
			if (conn != null) {
				conn.rollback();
				throw new GestionException(e.getMessage());
			}
		} finally {
			CloseObject.closeObject( conn );
		}
		return true;
	}

	public ArrayList<String> ArmaDocumentoComprobatorio(String listaIds) throws Exception {
		ArrayList<String> arrListaComp = null;

		Connection conn = null;

		try {
			conn = getConnection();
			
			OperacionAjenaManager.updateHeaderCompromisos(conn, listaIds);
			conn.commit();
		} catch (SQLException e) {
			if (conn != null) {
				conn.rollback();
				throw new GestionException(e.getMessage());
			}
		} finally {
			CloseObject.closeObject( conn );
		}
		return arrListaComp;
	}

	public boolean actualizaCompromisos(Integer nEnviadoSICOP, String caNoCompromiso) throws Exception {
		boolean regActualizado = false;

		Connection conn = null;

		try {
			conn = getConnection();
			regActualizado = OperacionAjenaManager.updateHeaderCompromisosRealimentacion(conn, nEnviadoSICOP, caNoCompromiso);
			
			conn.commit();
		} catch (SQLException e) {
			if (conn != null) {
				conn.rollback();
				throw new GestionException(e.getMessage());
			}
		} finally {
			CloseObject.closeObject( conn );
		}
		return regActualizado;
	}

	public boolean insertaLineaLayout(String clave, String cRamo, String cUnidadResponsable, String folioSICOP, String idProceso, String cCentroContable, java.sql.Date fExpedicion, float total, String cTipoPoliza, String nFolioPoliza, String nPolizaCancelacion, String tipoMovimiento,
		String origenPresupuesto, String cuentaBancaria, String noSolicitud, String tCambio, String tMoneda, String tSolicitud, String volante, String rfc, String caNoCompromiso, String codSemarnat2, String estatus, java.sql.Date fAplicacion, String documento, String nDocumento, String descripcion)
		throws SQLException {
		boolean regInsertado = false;

		Connection conn = null;

		try {
			conn = getConnection();
			regInsertado = OperacionAjenaManager.insertRegisterLayout(conn, clave, cRamo, cUnidadResponsable, folioSICOP, idProceso, cCentroContable, fExpedicion, total, cTipoPoliza, nFolioPoliza, nPolizaCancelacion, tipoMovimiento, origenPresupuesto, cuentaBancaria, noSolicitud, tCambio, tMoneda,
				tSolicitud, volante, rfc, caNoCompromiso, codSemarnat2, estatus, fAplicacion, documento, nDocumento, descripcion);
			
			conn.commit();
		} catch (SQLException e) {
			if (conn != null) {
				e.printStackTrace();
				conn.rollback();
			}
		} finally {
			CloseObject.closeObject( conn );
		}
		return regInsertado;
	}

	public ArrayList<String> buscaAjenasIntegradas(String cxpAI, String fecha, String cBancaria, String Leyenda, String nFolio, String cBEN, int tipo) throws Exception {
		ArrayList<String> arrListaComp = null;

		Connection conn = null;

		try {
			conn = getConnection();
			arrListaComp = OperacionAjenaManager.buscaAjenasIntegradas(conn, cxpAI, fecha, cBancaria, Leyenda, nFolio, cBEN, tipo);

		} catch (SQLException e) {
			if (conn != null) {
				conn.rollback();
				throw new GestionException(e.getMessage());
			}
		} finally {
			CloseObject.closeObject( conn );
		}
		return arrListaComp;
	}

	
	public JSONArray buscaSolicitudesJSON(String finicio, String fFin, int tipo, Boolean esIP, Boolean esRGconOC, int cc, int ejercido) throws Exception {

		Connection conn = null;
		JSONArray jsonArray = new JSONArray();
		String[][] res = null;
		try {
			conn = getConnection();
		
			res = OperacionAjenaManager.buscaSolicitudes(conn, finicio, fFin, tipo, esIP, esRGconOC, cc, ejercido);
		
			for (int i = 0; i < res.length; i++) {
				JSONObject jsonObj = new JSONObject(new LinkedHashMap<String, String>());
				for (int j = 0; j < res[i].length; j++) {
					String strDatos = res[i][j];
					jsonObj.put("Col" + j, strDatos);
				}
				jsonArray.put(jsonObj);
			}
			return jsonArray;

		} finally {
			CloseObject.closeObject(conn);
		}
	}


	public List<String> procesaLayoutAjenas(File nombreDestino) throws Exception {
		List<String> resultado = null;
		Connection conn = null;
		InputStream is = new FileInputStream(nombreDestino);
		conn = getConnection();
		
		try {
			OperacionAjenaManager.borraTabla( conn );
			List<CLC_SOLXPAGAR> renglonesArchivo = leerArchivoAjena(is);
			conn.setAutoCommit(false);
			
			OperacionAjenaManager.insertarDatosCLC (conn, renglonesArchivo) ;
			
		} catch (Exception e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.warn("Problemas en rollback: " + e2);
				}

			if (resultado == null)
				resultado = new ArrayList<String>();

			resultado.add( e.toString());
			log.error(e, e);
		} finally {
			
			CloseObject.closeObject(conn);
		}
	
		return resultado;
	}
	

	public List<CLC_SOLXPAGAR> leerArchivoAjena(InputStream in) throws Exception {
		boolean primeraLinea = true;

		List<CLC_SOLXPAGAR> lista = new ArrayList<>();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String renglon = "";
		
		try {
			while ((renglon = br.readLine()) != null) {
				String[] llaves = renglon.split(",");
				if (primeraLinea) {
					primeraLinea = false;
				} else {
					if (llaves[3].equalsIgnoreCase("RHQ") ) {
						CLC_SOLXPAGAR item = new CLC_SOLXPAGAR ();
	                    int i = 0;
	                    item.setIdEvento(Integer.valueOf(llaves[i++]));
	                    item.setEvento(llaves[i++]);
	                    item.setIdRamoMl(llaves[i++]);
	                    item.setIdUnidadMl(llaves[i++]);
	                    item.setCani(llaves[i++]);
	                    item.setCgfu(llaves[i++]);
	                    item.setCfun(llaves[i++]);
	                    item.setCsfu(llaves[i++]);
	                    item.setCprg(llaves[i++]);
	                    item.setCain(llaves[i++]);
	                    item.setCppt(llaves[i++]);
	                    item.setCcap(llaves[i++]);
	                    item.setCcon(llaves[i++]);
	                    item.setCparg(llaves[i++]);
	                    item.setCpar(llaves[i++]);
	                    item.setCtga(llaves[i++]);
	                    item.setCfin(llaves[i++]);
	                    item.setCcau(llaves[i++]);
	                    item.setCcop(llaves[i++]);
	                    item.setCgeo(llaves[i++]);
	                    item.setCpla(llaves[i++]);
	                    item.setCppi(llaves[i++]); 
	                    item.setOfin(llaves[i++]);
	                    item.setAux1(llaves[i++]);
	                    item.setAux2(llaves[i++]);
	                    item.setAux3(llaves[i++]);
	                    item.setImporte148(new BigDecimal(llaves[i++].trim()));
	                    item.setMes149(llaves[i++]);
	                    item.setNcom15(llaves[i++]);
	                    item.setCben16(llaves[i++]);
	                    item.setNres17(llaves[i++]);
	                    item.setNoif18(llaves[i++]);
	                    item.setRemaIsr192(new BigDecimal(llaves[i++].trim()));
	                    item.setRemIva193(new BigDecimal(llaves[i++].trim()));
	                    item.setRem5mil194(new BigDecimal(llaves[i++].trim()));
	                    item.setImpNetneg200(new BigDecimal(llaves[i++].trim()));
	                    item.setRemaContrib202(new BigDecimal(llaves[i++].trim()));
	                    item.setPpag177(llaves[i++]);
	                    item.setTnom178(llaves[i++]);
	                    item.setTconc49(llaves[i++]);
	                    item.setConcMov50(llaves[i++]);
	                    item.setSpag176(llaves[i++]);
	                    item.setProceso(llaves[i++]);
	                    item.setIdUnidadCr(llaves[i++]);
	                    item.setRem2mil315(new BigDecimal(llaves[i++].trim()));
	                    item.setRemOtret316(new BigDecimal(llaves[i++].trim()));
	                    item.setRemPena317(new BigDecimal(llaves[i++].trim()));

	                    lista.add(item);
					}
				} 
			} 
			
			return lista;
			
		} finally {
			br.close();

		}
	}
}
