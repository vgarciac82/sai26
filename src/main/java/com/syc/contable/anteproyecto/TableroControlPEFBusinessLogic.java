package com.syc.contable.anteproyecto;

import java.sql.Connection;
import java.util.Map;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

/**
 * Logica de negocios del tablero de control. Mediante mapas muestra la relacion
 * UE/UN de aquellas unidades que han sido capturadas.
 * 
 * @author Vicente Garcia Carrillo
 * 
 */
public class TableroControlPEFBusinessLogic extends DataSourceManager {

	

	/**
	 * Crea una nueva instancia del objeto.
	 */
	public TableroControlPEFBusinessLogic() {
		super();
	}

	/**
	 * Devuelve una matriz en la que se encuentra la relacion de calendarios
	 * cargados. Las columnas de la matriz son la unidad normativa y los
	 * reglones la unidad ejecutora
	 * 
	 * @param incluirCapitulosRestringidos
	 *            Si se desea incluir los capitulos incluidos.
	 * @return Mapa con la relacion UE/UN y los totales capturados.
	 */
	public Map<String, Map<String, TableroControlBean>> getRelacionCalendariosCargados(boolean incluirCapitulosRestringidos) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			Map<String, Map<String, TableroControlBean>> tablero = TableroControlPEFManager.cargaInicialPEF(conn);
			TableroControlPEFManager.obtenEstatusCalendario(conn, tablero);
			return tablero;
		} finally {
			CloseObject.closeObject(conn, false);
		}
	}
}
