package com.syc.sai.contabilidad.polizamanual.controller;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.syc.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.polizamanual.Cuentas;
import com.syc.sai.contabilidad.polizamanual.CuentasEngineException;
import com.syc.sai.contabilidad.polizamanual.model.CuentasManager;

public class CuentasBusinessLogic extends DataSourceManager {

	Logger	log	= Logger.getLogger(CuentasBusinessLogic.class);

	public Hashtable<String,List<Cuentas>> getCuentasByPartida(HttpServletRequest req) throws CuentasEngineException {
		Hashtable<String, List<Cuentas>> result = new Hashtable<String, List<Cuentas>>();
		Connection conn = null;		
		try {
			conn = getConnection();
			Cuentas cuenta = new Cuentas();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			
			//cIdGrupoEvento=100 AND cIdSubGrupoEvento=10 AND cIdeventoManual=10 AND cPartida=21101
			String cIdGrupoEvento = req.getParameter("cIdGrupoEvento");
			String cIdSubGrupoEvento = req.getParameter("cIdSubGrupoEvento");
			String cIdeventoManual = req.getParameter("cIdeventoManual");
			String cPartida = req.getParameter("cPartida");
			
			String parametros=" cIdGrupoEvento="+cIdGrupoEvento+" AND " +
				"cIdSubGrupoEvento="+cIdSubGrupoEvento+" AND " +
				"cIdeventoManual="+cIdeventoManual;
			 String resultadoPartida=CuentasManager.readEventoBy(conn, parametros);
			 
			 if (resultadoPartida.equals("0")){
				 
				 String restrictions = 	" inner join (select nCuenta from tEventoManual with (nolock) where "+
						"cIdGrupoEvento="+cIdGrupoEvento+" AND " +
						"cIdSubGrupoEvento="+cIdSubGrupoEvento+" AND " +
						"cIdeventoManual="+cIdeventoManual+" AND " +
						"cEvento='ABONO')t on c.nCuenta=t.nCuenta";


				 result.put("CARGO", readCuentasBy(restrictions));

				 restrictions =" inner join (select nCuenta from tEventoManual with (nolock) where "+
						"cIdGrupoEvento="+cIdGrupoEvento+" AND " +
						"cIdSubGrupoEvento="+cIdSubGrupoEvento+" AND " +
						"cIdeventoManual="+cIdeventoManual+" AND " +
						"cEvento='CARGO' )t on c.nCuenta=t.nCuenta";
				 result.put("ABONO", readCuentasBy(restrictions));
				 
			 }
			 else
			 {
			 
			
			String restrictions = 	" inner join (select nCuenta from tEventoManual with (nolock) where "+
									"cIdGrupoEvento="+cIdGrupoEvento+" AND " +
									"cIdSubGrupoEvento="+cIdSubGrupoEvento+" AND " +
									"cIdeventoManual="+cIdeventoManual+" AND " +
									"cEvento='ABONO' AND " +
									"cPartida='"+cPartida+"')t on c.nCuenta=t.nCuenta";
			
			
			result.put("CARGO", readCuentasBy(restrictions));
			
			restrictions = 			" inner join (select nCuenta from tEventoManual with (nolock) where "+
									"cIdGrupoEvento="+cIdGrupoEvento+" AND " +
									"cIdSubGrupoEvento="+cIdSubGrupoEvento+" AND " +
									"cIdeventoManual="+cIdeventoManual+" AND " +
									"cEvento='CARGO' AND " +
									"cPartida='"+cPartida+"')t on c.nCuenta=t.nCuenta";
			result.put("ABONO", readCuentasBy(restrictions));
			
			 }
			return 	result;
		} catch (Exception e) {
			throw new CuentasEngineException(e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
					log.error("Error cerrando la base de datos" + e, e);
				}
		}

	}
	
	public List<Cuentas> readCuentasBy(String restrictions) throws CuentasEngineException {
		Connection conn = null;
		try {
			conn = getConnection();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			return CuentasManager.readCuentasBy(conn, restrictions);
		} catch (Exception e) {
			throw new CuentasEngineException(e);
		} finally {
			
		}
	}
		
	public int saveOrUpdateCuentas(HttpServletRequest req) throws CuentasEngineException {
		Connection conn = null;
		try {
			conn = getConnection();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Cuentas cuenta = new Cuentas();
			cuenta.setNcuenta(req.getParameter("ncuenta"));
			cuenta.setDcuenta(req.getParameter("dcuenta"));
			cuenta.setTipoCuenta(req.getParameter("tipoCuenta"));
			cuenta.setNcuentaPadre(req.getParameter("ncuentaPadre"));
			cuenta.setTipoBalance(req.getParameter("tipoBalance"));
			cuenta.setVerificaSaldo(req.getParameter("verificaSaldo"));
			cuenta.setNaturalezaCuenta(req.getParameter("naturalezaCuenta"));
			cuenta.setNivelCuenta(new Short(req.getParameter("nivelCuenta")));
			cuenta.setAplicacionCuenta(req.getParameter("aplicacionCuenta"));
			cuenta.setNcuentaLike(req.getParameter("ncuentaLike"));
			cuenta.setNordenBalanza(new Integer(req.getParameter("nordenBalanza")));
			cuenta.setNnivelBalanza(new Integer(req.getParameter("nnivelBalanza")));
			cuenta.setCbloqueaAbonos(req.getParameter("cbloqueaAbonos"));
			cuenta.setCbloqueaCargos(req.getParameter("cbloqueaCargos"));
			cuenta.setCnivelBloqueo(req.getParameter("cnivelBloqueo"));
			cuenta.setCuentaBloqueada(req.getParameter("cuentaBloqueada"));
			cuenta.setcSubcuenta(req.getParameter("cSubcuenta"));
			
			req.getSession().setAttribute("cuenta",cuenta);			
			return CuentasManager.saveOrUpdateCuentas(conn, cuenta);
		} catch (Exception e) {
			throw new CuentasEngineException(e);
		} finally {
			
		}

	}

	public String checkEvent(String parametros)throws CuentasEngineException {
		// TODO Auto-generated method stub
		String result;
		Connection conn = null;		
		try {
			conn = getConnection();
		result=CuentasManager.readEventoBy(conn, parametros);
		
		return result;
		} catch (Exception e) {
			throw new CuentasEngineException(e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
					log.error("Error cerrando la base de datos" + e, e);
				}
		}
	}	
}

