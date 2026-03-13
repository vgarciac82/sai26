package com.syc.adquisiciones.businessLogic;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.syc.adquisiciones.OperacionesAmpliaciones;
import com.syc.adquisiciones.OperacionesCRUD;
import com.syc.adquisiciones.core.DatosContrato;
import com.syc.adquisiciones.manager.AmpliacionesContratosManager;
import com.syc.adquisiciones.manager.ProcedimientoManager;
import com.syc.dsmngr.DataSourceManager;

public class ContratoAmpImpl extends DataSourceManager implements OperacionesAmpliaciones {
	private static Logger log = Logger.getLogger(ContratoAmpImpl.class);
	public String AjustaCentavos(DatosContrato contrato) throws Exception {
		String msg="";
		Connection conn=null;
		OperacionesCRUD operCrud=null;
		ProcedimientoManager manager=null;
		Map<String, String> map=null;
		try {
			conn=getConnection();
			operCrud=new AmpliacionesContratosManager();
			map=new  HashMap<String, String>();
			manager=new ProcedimientoManager();
			map.put("mMontoNetoMod", ""+contrato.getAmpliacion().get(0).getmMontoConIVA());
			map.put("cEjercicio", contrato.getcEjercicio());
			map.put("cIdTipoContrato", contrato.getcTipoContrato());
			map.put("cIdUnidadEjecutora", contrato.getcUE());
			map.put("nIdConsecutivo", ""+contrato.getnIdConsecutivo());
			map.put("nIdConsecutivoAmpliacion", ""+contrato.getAmpliacion().get(0).getnConsecutivoAmpliacion());
			map.put("cIdContratoDef", contrato.getcIdContratoDef());
			//validar que no puedan modificar mas de 50 centavos
			if(manager.validaMax50CentavosContAbierto(conn, map)){
				operCrud.Update(map, conn);
				msg="Actualizaci\u00f3n de centavos correctamente.";
			}else{
				msg="No se puede modificar mas de 50 centavos.";
				log.warn("No se puede modificar los centavos por que no se cumple la condici\u00f3n de m\u00e1ximo 50 centavos.");
			}
			conn.commit();
		} catch (Exception e) {
			// TODO: handle exception
			log.error(e);
			e.printStackTrace();
			msg=e.getMessage();
			conn.rollback();
		}finally{
			if(conn!=null){
				conn.close();
			}
			conn=null;
			map=null;
			operCrud=null;
			manager=null;
		}
		return msg;
	}
}
