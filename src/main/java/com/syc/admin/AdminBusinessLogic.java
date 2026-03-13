package com.syc.admin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;

import com.syc.admin.core.Usuario;
import com.syc.admin.core.UsuarioManager;
import com.syc.dsmngr.DataSourceManager;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.fortimax.core.Descripcion;
import com.syc.fortimax.core.DescripcionManager;
import com.syc.gestion.core.CasoOperacionManager;
import com.syc.gestion.core.GestionException;

public class AdminBusinessLogic extends DataSourceManager {

	private static Logger log = Logger.getLogger(AdminBusinessLogic.class);

	public AdminBusinessLogic(String jniName) {

		super.init(jniName);
	}

	
	public int servicio(String evento, Usuario u) throws AdminException {

		Connection conn = null;
		int retVal = -1;
		try {
			
			conn = getConnection();
			
			if (evento.endsWith("User")){
				
				if(evento!=null) {
					if(evento.equals("addUser")){
						retVal = UsuarioManager.insert(conn, u);
					} else if(evento.equals("updateUser")) {
						retVal = UsuarioManager.update(conn, u);
					} else if(evento.equals("deleteUser")) {
						retVal = UsuarioManager.delete(conn, u.getLogin());
					}
				}
			}

		} catch (SQLException exc) {
			log.error("Recuperando descripcion", exc);
			throw new AdminException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}
		return retVal;
	}

	
	
	public Descripcion[] getDescripcion(String titulo_aplicacion) throws GestionException {

		Descripcion[] desc = new Descripcion[0];
		Connection conn = null;

		try {
			conn = getConnection();

			Map m = DescripcionManager.select(conn, titulo_aplicacion);

			desc = (Descripcion[]) m.values().toArray(new Descripcion[m.values().size()]);
		} catch (SQLException exc) {
			log.error("Recuperando descripcion", exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}

		return desc;
	}

	
	public String[] insertAplicacion(String titulo_aplicacion, String u_login, String nombre_carpeta, Map map)
			throws GestionException {

		Connection conn = null;
		String[] retval = new String[2];

		try {
			conn = getConnection();

			retval = AplicacionManager.insert(conn, titulo_aplicacion, u_login, nombre_carpeta, map);

			conn.commit();
		} catch (SQLException exc) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				log.warn("Error en rollback", ex);
			}

			log.error("Agregando expediente", exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}

		return retval;
	}

	public boolean deleteAplicacion(String titulo_aplicacion, int id_gabinete) throws GestionException {

		Connection conn = null;
		boolean retval = false;

		try {
			conn = getConnection();

			retval = AplicacionManager.delete(conn, titulo_aplicacion, id_gabinete);

			conn.commit();
		} catch (SQLException exc) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				log.warn("Error en rollback", ex);
			}

			log.error("Borrando expediente", exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}

		return retval;
	}

	public boolean updateAplicacion(String titulo_aplicacion, int id_gabinete, Map map) throws GestionException {

		Connection conn = null;
		boolean retval = false;

		try {
			conn = getConnection();

			retval = AplicacionManager.update(conn, titulo_aplicacion, id_gabinete, map);

			conn.commit();
		} catch (SQLException exc) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				log.warn("Error en rollback", ex);
			}

			log.error("Actualizando expediente", exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}

		return retval;
	}

	public String[][] getQueryByExampleAplicacionData(String titulo_aplicacion, Map map) throws GestionException {

		Connection conn = null;
		String[][] retval = new String[0][0];

		try {
			conn = getConnection();

			// TODO: Considerar u_login
			retval = AplicacionManager.getQueryByExampleAplicacionData(conn, titulo_aplicacion, "", map);
		} catch (SQLException exc) {
			log.error("Consultando expediente", exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}

		return retval;
	}

	public Map consultaCasoOperacion(String titulo_aplicacion, int id_gabinete) throws GestionException {

		Connection conn = null;
		Map m = new Hashtable();

		try {
			conn = getConnection();

			m = CasoOperacionManager.consultaCasoOperacion(conn, titulo_aplicacion, id_gabinete);
		} catch (SQLException exc) {
			log.error("Obteniendo caso operacion", exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}

		return m;
	}

	public Map getCasoOperacion(String u_login, String titulo_aplicacion, int id_gabinete) throws GestionException {

		Connection conn = null;
		Map m = new Hashtable();

		try {
			conn = getConnection();

			m = CasoOperacionManager.selectCasoOperacion(conn, u_login, titulo_aplicacion, id_gabinete);
		} catch (SQLException exc) {
			log.error("Obteniendo caso operacion", exc);
			throw new GestionException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
		}

		return m;
	}
}
