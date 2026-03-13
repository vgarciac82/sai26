package com.syc.contable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.gestion.core.Caso;

public interface ContableInterface {

	//public ArrayList aplicarContable(Connection conn, Caso c, String cTablaPadre, String cTablaHija, String cFolio, int nFolioDocumento, String cTipoDocumento, String cFechaAplica);
	public ArrayList aplicarContable(Connection conn, Caso c, String cTablaPadre, String cTablaHija, String cFolio, int nFolioDocumento, String cTipoDocumento);

	//public ArrayList aplicarContableNuevo(Connection conn, Caso c, String cTablaPadre, String cTablaHija, String cFolio, int nFolioDocumento, String cTipoDocumento,
	//        Map m, String prefixPath, String uLogin);

	public AplicarContableReturn aplicarContableNuevo(Connection conn, Caso c, String cTablaPadre, String cTablaHija, String cFolio, int nFolioDocumento, String cTipoDocumento,
	        Map m, String prefixPath, String uLogin, String validaSaldo);

	public ArrayList aplicarContable(Connection conn, Caso c, String cTablaPadre, String cTablaHija, String cFolio, int nFolioDocumento, String cTipoDocumento, Map m,
	        String prefixPath, String uLogin);

	public String cancelarAppContable(Connection conn, Caso c, String cTablaPadre, String cTablaHija, String cFolio, int nFolioDocumento, String cTipoDocumento, String cFecha)
	        throws SQLException;

	public String cancelarAppContable(Connection conn, Caso c, String cTablaPadre, String cTablaHija, String cFolio, int nFolioDocumento, String cTipoDocumento, Map m,
	        String prefixPath, String uLogin, String cFecha) throws SQLException;

	public AplicarContableReturn cancelarAppContableNueva(Connection conn, Caso c, String cTablaPadre, String cTablaHija, String cFolio, int nFolioDocumento, String cTipoDocumento, Map m,
	        String prefixPath, String uLogin, String cFecha) throws SQLException;
}
