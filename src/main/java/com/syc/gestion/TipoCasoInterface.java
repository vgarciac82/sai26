package com.syc.gestion;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.Documento;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;

public interface TipoCasoInterface {


	/**
	 * Este metodo es llamado cuando se busca un expediente y es creado si no se
	 * encuentra la idea es poder controlar si se busca y crea en caso de no
	 * encontrarlo o si siempre se crea
	 * 
	 * @return true si se busca, false siempre crea uno nuevo
	 */
	public boolean buscaPorExpediente(Caso c, String u_login);

	/**
	 * Este metodo es llamado cuando se inicia un caso
	 * 
	 * @param conn
	 *            Conexion a base de datos. NOTA: No se debe de cerrar ni
	 *            ejecutar commit en esta conexion.
	 * @param u_login
	 *            Usuario que provoco la llamada
	 * @param c
	 *            Objeto com.syc.gestion.core.Caso
	 * 
	 * @throws SQLException
	 */
	public void onIniciaCaso(Connection conn, String u_login, Caso c) throws SQLException;

	/**
	 * Este metodo es llamado cuando se crea el expediente del caso
	 * 
	 * @param conn
	 *            Conexion a base de datos. NOTA: No se debe de cerrar ni
	 *            ejecutar commit en esta conexion.
	 * @param u_login
	 *            Usuario que provoco la llamada
	 * @param c
	 *            Objeto com.syc.gestion.core.Caso
	 * @param app
	 *            Objeto com.syc.fortimax.core.Aplicacion
	 * 
	 * @throws SQLException
	 */
	public void onCreateExpediente(Connection conn, String u_login, Caso c, Aplicacion app) throws SQLException;

	/**
	 * Este metodo es llamado cuando se recibe un documento o pagina del caso
	 * 
	 * @param conn
	 *            Conexion a base de datos. NOTA: No se debe de cerrar ni
	 *            ejecutar commit en esta conexion.
	 * @param c
	 *            Objeto com.syc.gestion.core.Caso
	 * @param d
	 *            Objeto com.syc.fortimax.core.Documento
	 * @throws SQLException
	 */
	public void onRecibeDocumento(Connection conn, Caso c, Documento d, boolean isSaveEvent) throws SQLException;

	/**
	 * Este metodo es llamado cuando es ejecutado un caso
	 * 
	 * @param conn
	 *            Conexion a base de datos. NOTA: No se debe de cerrar ni
	 *            ejecutar commit en esta conexion.
	 * @param u_login
	 *            Usuario que provoco la llamada
	 * @param c
	 *            Objeto com.syc.gestion.core.Caso
	 * @param id_caso_oper
	 *            Identificador de operacion ejecutada
	 * 
	 * @throws SQLException
	 */
	public void onEjecutaCaso(Connection conn, String u_login, Caso c, int id_caso_oper) throws SQLException;

	/**
	 * Este metodo es llamado cuando se avanza el caso
	 * 
	 * @param conn
	 *            Conexion a base de datos. NOTA: No se debe de cerrar ni
	 *            ejecutar commit en esta conexion.
	 * @param u_login
	 *            Usuario que provoco la llamada
	 * @param c
	 *            Objeto com.syc.gestion.core.Caso
	 * @param id_caso_oper
	 *            Identificador de operacion ejecutada
	 */
	public void onAvanzaCaso(Connection conn, String u_login, Caso c, int id_caso_oper) throws SQLException;

	/**
	 * Este metodo es llamado cuando es terminado un caso
	 * 
	 * @param conn
	 *            Conexion a base de datos. NOTA: No se debe de cerrar ni
	 *            ejecutar commit en esta conexion.
	 * @param u_login
	 *            Usuario que provoco la llamada
	 * @param c
	 *            Objeto com.syc.gestion.core.Caso
	 * @param observ
	 *            Observaciones realizadas
	 * @param resp
	 *            Responsable(s) siguiente(s)
	 * @param oper
	 *            Operacion(es) siguiente(s)
	 * @param data
	 *            Map de variables de caso (nombre/valor)
	 * 
	 * @throws SQLException
	 */
	public void onTerminaCaso(Connection conn, String u_login, Caso c, String observ, String[] resp, String[] oper, Map data) throws SQLException;

	/**
	 * Este metodo es llamado cuando se agota el tiempo configurado de un caso.
	 * 
	 * @param conn
	 *            Conexion a base de datos. NOTA: No se debe de cerrar ni
	 *            ejecutar commit en esta conexion.
	 * @param u_login
	 *            Usuario que provoco la llamada
	 * @param c
	 *            Objeto com.syc.gestion.core.Caso
	 * @param observ
	 *            Observaciones realizadas
	 * @param resp
	 *            Responsable(s) siguiente(s)
	 * @param oper
	 *            Operacion(es) siguiente(s)
	 * @param data
	 *            Map de variables de caso (nombre/valor)
	 * 
	 * @throws SQLException
	 */
	public void onVenceCaso(Connection conn, String folio, int id_caso, int id_tc, int id_oper, int porc) throws SQLException;

	public void onSolicitaFirmaElectronica(Caso c,  Usuario u, String reportPath) throws Exception;

}
