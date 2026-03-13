package com.syc.gestion;


import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.naming.AuthenticationException;

import org.apache.log4j.Logger;
import org.jdom.Element;

import com.axtel.user.UserException;
import com.axtel.user.entities.ExecutiveUnit;
import com.axtel.user.repositories.ExecutiveUnitRepository;
import com.axtel.user.repositories.impl.JDBCExecutiveUnitRepository;
import com.axtel.user.services.ExecutiveUnitService;
import com.axtel.user.services.impl.JDBCExecutiveUnitService;
import com.syc.adquisiciones.vo.ConexionesBD;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Grupo;
import com.syc.gestion.core.GrupoManager;
import com.syc.gestion.core.OperacionManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioManager;
import com.syc.gestion.custom.XmlGeneratorInterface;
import com.syc.gestion.documental.CatalogosManager;
import com.syc.ldap.UsuarioLDAP;
import com.syc.ldap.UsuarioLDAPException;
import com.syc.sai.contabilidad.utils.db.CloseObject;


public class UsuarioBusinessLogic extends DataSourceManager {

	private static final Logger		log	= Logger.getLogger( UsuarioBusinessLogic.class );
	private String					jniName;

	private ExecutiveUnitRepository	executiveUnitRepository;
	private ExecutiveUnitService	executiveUnitService;

	public UsuarioBusinessLogic( String jniName ) {
		super.init( jniName );
		this.jniName = jniName;
		executiveUnitRepository = new JDBCExecutiveUnitRepository();
		executiveUnitService = new JDBCExecutiveUnitService( jniName, executiveUnitRepository );
	}

	public Usuario validaCredenciales( String u_login, String u_password ) throws GestionException {

		Usuario ru = null;
		Connection conn = null;

		try {
			conn = getConnection();
			Usuario u = new Usuario();
			u.setLogin( u_login );
			if ( u_password != null )
				u.setPassword( u_password.trim() );

			log.debug( "Buscando Usuario:" + u_login + " password:" + u_password );
			ru = UsuarioManager.select( conn, u );

		} catch ( SQLException exc ) {
			log.error( "Validando credenciales de usuario " + u_login, exc );
			throw new GestionException( exc );
		} finally {
			try {
				if ( conn != null )
					conn.close();
			} catch ( SQLException exc ) {
				log.warn( "Cerrando conexion a base de datos", exc );
			}

			conn = null;
		}

		return ru;
	}

	public Usuario setUserUR( Usuario u ) throws GestionException {
		Connection conn = null;
		try {
			conn = getConnection();
			if ( u != null ) {
				u = UsuarioManager.getRamoUR( conn, u );
				log.debug( "Usuario encontrado:" + u.getLogin() + " UR:" + u.getU_UR() );
			}
			return u;
		} catch ( Exception exc1 ) {
			log.error( "Leyendo UR y Ramo del usuario ", exc1 );
			throw new GestionException( exc1 );
		} finally {
			CloseObject.closeObject( conn );
		}

	}

	public Usuario validaCredenciales( String u_login, String u_password, byte[] bPk ) throws GestionException {

		Usuario ru = null;
		Connection conn = null;
		System.out.println( "VALIDANDO CREDENCIALES" );
		try {
			conn = getConnection();
			Usuario u = new Usuario();

			u.setLogin( u_login );
			u.setPassword( u_password );
			u.setBPk( bPk );

			// ru = UsuarioManager.selectUITAM(conn, u);
			ru = UsuarioManager.select( conn, u );
		} catch ( SQLException exc ) {
			log.error( "Validando credenciales de usuario " + u_login, exc );
			throw new GestionException( exc );
		} finally {
			try {
				if ( conn != null )
					conn.close();
			} catch ( SQLException exc ) {
				log.warn( "Cerrando conexion a base de datos", exc );
			}

			conn = null;
		}

		return ru;
	}

	public Usuario validaCredencialesActivo( String u_login, String u_password ) throws GestionException {

		Usuario ru = null;
		Connection conn = null;

		try {
			conn = getConnection();
			Usuario u = new Usuario();

			u.setLogin( u_login );
			u.setPassword( u_password );
			u.setEstatus( "A" );
			log.debug( "Buscando Usuario:" + u_login + " password:" + u_password );
			ru = UsuarioManager.select( conn, u );
			try {
				if ( ru != null ) {
					ru = UsuarioManager.getRamoUR( conn, ru );// para
																// complementar
																// Unidad
																// responsable y
																// Ramo
					log.debug( "Usuario encontrado:" + ru.getLogin() + " UR:" + ru.getU_UR() );
				}
			} catch ( Exception exc1 ) {
				log.error( "Leyendo UR y Ramo del usuario " + u_login, exc1 );
				throw new GestionException( exc1 );
			}
		} catch ( SQLException exc ) {
			log.error( "Validando credenciales de usuario " + u_login, exc );
			throw new GestionException( exc );
		} finally {
			try {
				if ( conn != null )
					conn.close();
			} catch ( SQLException exc ) {
				log.warn( "Cerrando conexion a base de datos", exc );
			}

			conn = null;
		}

		return ru;
	}

	public int validaCredencialesLDAP( String u_login, String u_password ) throws Exception {

		int regresa = 5;
		Connection conn = null;
		try {
			conn = getConnection();
			UsuarioLDAP uldap = new UsuarioLDAP();

			try {
				if ( uldap.validaUsuario( conn, u_login, u_password ) )
					regresa = 0;
			} catch ( AuthenticationException e ) {
				e.printStackTrace( System.out );
				regresa = 5;
			} catch ( NullPointerException e ) {
				e.printStackTrace( System.out );
				regresa = 6;
			} catch ( UsuarioLDAPException e ) {
				e.getMessage();
				e.printStackTrace( System.out );
				regresa = 7;
			}
			// catch(Exception e){e.printStackTrace(System.out); return 7;}

			// else{
			// return 5;
			// }

		} finally {
			try {
				if ( conn != null )
					conn.close();
			} catch ( SQLException exc ) {
				log.warn( "Cerrando conexion a base de datos", exc );
			}
			conn = null;
		}
		return regresa;
	}

	public final int	MSG_TEXT	= 0;
	public final int	USR_OBJ		= 1;

	public Object[] cambiaPassword( String u_login, String oldpasswd, String newpasswd ) throws GestionException {

		Connection conn = null;
		Object[] returnValue = { "La contrasea actual es invalida", null };

		try {
			conn = getConnection();
			oldpasswd = convertCMD5( oldpasswd );
			Usuario u = validaCredenciales( u_login, oldpasswd );

			if ( u != null ) {
				/************************************************************************************/
				try {
					newpasswd = convertCMD5( newpasswd );
					System.out.println( "El password encriptado es:" + newpasswd );

				} catch ( Exception e ) {
					throw new GestionException( "Error en encriptacion de datos :" + e.getMessage() );
				}
				/***********************************************************************************/
				u.setPassword( newpasswd );

				UsuarioManager.update( conn, u );

				conn.commit();

				returnValue[MSG_TEXT] = "Cambio de contraseña exitosa";
				returnValue[USR_OBJ] = u;
			}
		} catch ( Exception exc ) {
			if ( conn != null )
				try {
					conn.rollback();
				} catch ( SQLException ex ) {
					log.warn( "Error en rollback", ex );
				}

			log.error( "Cambiando contraseña de usuario" + u_login, exc );
			throw new GestionException( exc );
		} finally {
			try {
				if ( conn != null )
					conn.close();
			} catch ( SQLException exc ) {
				log.warn( "Cerrando conexion a base de datos", exc );
			}

			conn = null;
		}

		return returnValue;
	}

	private Object[] changePSSW( List<ConexionesBD> conexiones, String u_login, String oldpasswd, String newpasswd ) throws Exception {
		ConexionesBD cbd = null;
		Connection conn = null;
		Usuario u = null;
		Object[] returnValue = { "", null };
		try {
			Iterator<ConexionesBD> ite = conexiones.iterator();
			oldpasswd = convertCMD5( oldpasswd );
			u = validaCredenciales( u_login, oldpasswd );
			String token = "";
			if ( u != null ) {
				newpasswd = convertCMD5( newpasswd );
				while ( ite.hasNext() ) {
					cbd = ite.next();
					try {
						log.info( "Inicia el cambio de Password para la base: " + cbd.getNombreBD() );
						conn = getConectionCatalogo( cbd.getServidor(), cbd.getPuerto(), cbd.getNombreBD(), cbd.getUsuarioBD(), cbd.getPassBD() );
						System.out.println( "El password encriptado es:" + newpasswd );

						u.setPassword( newpasswd );

						UsuarioManager.update( conn, u );

						conn.commit();
						returnValue[MSG_TEXT] = returnValue[MSG_TEXT] + token + "Cambio de contrase\u00f1a exitosa para el ejercicio: " + cbd.getEjercicioFiscal();
						returnValue[USR_OBJ] = u;
						token = "; ";
					} catch ( Exception ex ) {
						log.error( ex, ex );
						returnValue[MSG_TEXT] = returnValue[MSG_TEXT] + token + ex.getMessage() + cbd.getEjercicioFiscal();
						returnValue[USR_OBJ] = u;
						if ( conn != null ) {
							try {
								conn.rollback();
							} catch ( Exception e ) {
								log.warn( "Problema realizando rollback: " + e );
							}
						}
					} finally {
						if ( conn != null ) {
							conn.close();
						}
						conn = null;
					}
				}
			} else {
				returnValue[MSG_TEXT] = "Error al validar credenciales del usuario";
				returnValue[USR_OBJ] = u;
				log.warn( "Usuario null" );
			}
		} catch ( Exception e ) {
			returnValue[MSG_TEXT] = returnValue[MSG_TEXT] + e.getMessage() + cbd.getEjercicioFiscal();
			returnValue[USR_OBJ] = u;
			e.getMessage();
		}
		return returnValue;
	}

	public Object[] changePasswordNBases( String u_login, String oldpasswd, String newpasswd ) throws GestionException {
		Object[] returnValue = { "", null };
		Connection conn = null;
		List<ConexionesBD> conexiones = null;
		try {
			conn = getConnection();
			conexiones = CatalogosManager.getBasesDeDatos( conn, "SELECT * FROM TEJERCICIOFISCAL" );
			// Cambiar el password
			returnValue = changePSSW( conexiones, u_login, oldpasswd, newpasswd );
			conn.commit();
		} catch ( Exception e ) {
			// TODO: handle exception
			returnValue[MSG_TEXT] = e.getMessage();
			try {
				conn.rollback();
			} catch ( SQLException e1 ) {
				// TODO Auto-generated catch block
				log.warn( "Error en rollback", e1 );
			}
			log.error( "Cambiando contrase\u00f1a de usuario" + u_login, e );
		} finally {
			try {
				if ( conn != null )
					conn.close();
			} catch ( SQLException exc ) {
				log.warn( "Cerrando conexion a base de datos", exc );
			}

			conn = null;
		}
		return returnValue;
	}

	public Element toParaXML() throws GestionException {

		Connection conn = null;
		Element elPara = new Element( "para" );

		try {
			conn = getConnection();

			List usrList = UsuarioManager.selectAll( conn );
			Element elUsers = new Element( "usuarios" );
			for ( Iterator iter = usrList.iterator(); iter.hasNext(); )
				elUsers.addContent( ( ( Usuario ) iter.next() ).toXML() );

			elPara.addContent( elUsers );

			List grpList = GrupoManager.selectAll( conn );
			Element elGroup = new Element( "grupos" );
			for ( Iterator iter = grpList.iterator(); iter.hasNext(); )
				elGroup.addContent( ( ( Grupo ) iter.next() ).toXML() );

			elPara.addContent( elGroup );
		} catch ( SQLException exc ) {
			try {
				conn.rollback();
			} catch ( SQLException ex ) {
				log.warn( "Error en rollback", ex );
			}

			log.error( "", exc );
			throw new GestionException( exc );
		} finally {
			try {
				if ( conn != null )
					conn.close();
			} catch ( SQLException exc ) {
				log.warn( "Cerrando conexion a base de datos", exc );
			}

			conn = null;
		}

		return elPara;
	}

	public Element toUserGroupOperXML( int id_tc ) throws GestionException {

		Connection conn = null;
		Element elPara = new Element( "para" );

		try {
			conn = getConnection();

			List usrList = UsuarioManager.selectAll( conn );
			Element elUsers = new Element( "usuarios" );
			for ( Iterator iter = usrList.iterator(); iter.hasNext(); ) {
				Usuario u = ( Usuario ) iter.next();
				elUsers.addContent( u.toXML( OperacionManager.selectForUsuario( conn, id_tc, u.getLogin() ) ) );
			}

			elPara.addContent( elUsers );

			List grpList = GrupoManager.selectAll( conn );
			Element elGroup = new Element( "grupos" );
			for ( Iterator iter = grpList.iterator(); iter.hasNext(); ) {
				Grupo g = ( Grupo ) iter.next();
				elGroup.addContent( g.toXML( OperacionManager.selectForGrupo( conn, id_tc, g.getNombre() ) ) );
			}

			elPara.addContent( elGroup );
		} catch ( SQLException exc ) {
			try {
				conn.rollback();
			} catch ( SQLException ex ) {
				log.warn( "Error en rollback", ex );
			}

			log.error( "Generando XML usuario/grupo/operacion", exc );
			throw new GestionException( exc );
		} finally {
			try {
				if ( conn != null )
					conn.close();
			} catch ( SQLException exc ) {
				log.warn( "Cerrando conexion a base de datos", exc );
			}

			conn = null;
		}

		return elPara;
	}

	public void modifyXML( XmlGeneratorInterface xml, Element el ) throws GestionException {

		Connection conn = null;

		try {
			conn = getConnection();

			xml.usersProcess( conn, el );
			xml.groupsProcess( conn, el );
		} catch ( SQLException exc ) {
			log.error( "Modificando XML", exc );
			throw new GestionException( exc );
		} finally {
			try {
				if ( conn != null )
					conn.close();
			} catch ( SQLException exc ) {
				log.warn( "Cerrando conexion a base de datos", exc );
			}

			conn = null;
		}
	}

	public Usuario getUsuario( Usuario u ) throws GestionException {

		Connection conn = null;
		Usuario retval = null;

		try {
			conn = getConnection();
			retval = UsuarioManager.select( conn, u );
			retval = UsuarioManager.getRamoUR( conn, retval );
		} catch ( SQLException exc ) {
			log.error( "Buscando Usuario", exc );
			throw new GestionException( exc );
		} finally {
			try {
				if ( conn != null )
					conn.close();
			} catch ( SQLException exc ) {
				log.warn( "Cerrando conexion a base de datos", exc );
			}

			conn = null;
		}

		return retval;
	}

	/************************************************************************************************/
	public String convertCMD5( String pass ) throws Exception {
		String pwd = pass;
		String pwdCMD5 = "";
		try {
			MessageDigest digest = java.security.MessageDigest.getInstance( "MD5" );
			digest.update( pwd.getBytes() );
			byte[] hash = digest.digest();
			pwdCMD5 = convertToHex( hash );
			System.out.println( convertToHex( hash ) );
			System.out.println( digest.toString() );
		} catch ( Exception e ) {
			System.out.println( "Error al encriptar credenciales..." + e.getMessage() );

		}
		return pwdCMD5;
	}

	/************************************************************************************************/

	private String convertToHex( byte[] data ) {
		StringBuffer buf = new StringBuffer();
		for ( int i = 0; i < data.length; i++ ) {
			int halfbyte = ( data[i] >>> 4 ) & 0x0F;
			int two_halfs = 0;
			do {
				if ( ( 0 <= halfbyte ) && ( halfbyte <= 9 ) )
					buf.append( ( char ) ( '0' + halfbyte ) );
				else
					buf.append( ( char ) ( 'a' + ( halfbyte - 10 ) ) );
				halfbyte = data[i] & 0x0F;
			} while ( two_halfs++ < 1 );
		}
		return buf.toString();
	}

	/************************************************************************************************/
	private static Connection getConectionCatalogo( String server, String port, String bd, String user, String pass ) throws Exception {

		Connection conn = null;
		String url = null;

		url = "jdbc:jtds:sqlserver://" + server + ":" + port + "/" + bd;
		Class.forName( "net.sourceforge.jtds.jdbc.Driver" );
		conn = DriverManager.getConnection( url, user, pass );

		conn.setAutoCommit( false );

		return conn;
	}

	public ExecutiveUnit getExecutiveUnit( Usuario u ) throws UserException {
		return executiveUnitService.getExecutiveUnitByAU( u.getU_UR() );
	}
	
	public Usuario getUsuarioUR( Usuario u ) throws GestionException {

		Connection conn = null;
		Usuario retval = null;

		try {
			conn = getConnection();			
			retval = UsuarioManager.getRamoUR( conn, u );
		} catch ( SQLException exc ) {
			log.error( "Buscando Usuario", exc );
			throw new GestionException( exc );
		} finally {
			try {
				if ( conn != null )
					conn.close();
			} catch ( SQLException exc ) {
				log.warn( "Cerrando conexion a base de datos", exc );
			}

			conn = null;
		}

		return retval;
	}

}
