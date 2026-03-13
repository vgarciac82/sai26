package com.syc.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.syc.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

/**
 * Almacen de propiedades del modulo. Sirve para parametrizar algunos aspectos
 * de cada modulo, como configuraciones de correo, cuentas contables, etc. Cada
 * modulo del SAI debe definir su unico y propio prefijo. Por ejemplo obra
 * publica utiliza [MOP]. Esta clase cargara de la tabla
 * <i>cg_grupo_propiedades</i> los parametros que inicien con el prefijo del
 * modulo. Los nombres de las propiedades se deberan separar por punto. Por
 * ejemplo: MOP.mail.stmpserver. En caso de existir mas de una propiedad con el
 * mismo nombre, la clase la concatenara con el caracter | de modo que los
 * objetos que lo utilizen puedan despues separarlos mediante el metodo
 * {@link String#split(String)}
 * 
 * @author Vicente Garcia C.
 * 
 */
public class ModuleProperties extends DataSourceManager {
	private static final Logger	log	= Logger.getLogger(ModuleProperties.class);
	private Properties			moduleProperties;
	private String				modulePrefix;

	/**
	 * Crea un nuevo objeto especificando el prefijo del modulo y carga sus
	 * propiedades.
	 * 
	 * @param modulePrefix
	 */
	public ModuleProperties(String modulePrefix) throws Exception {
		Connection conn = null;
		String query = "SELECT GP_NOMBRE, GP_VALOR " + "FROM   cg_grupo_propiedades " + "WHERE  g_nombre = 'PREFERENCIAS_CLIENTE' " + "       AND gp_nombre LIKE ? + '%' ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		moduleProperties = new Properties();
		this.modulePrefix = modulePrefix;

		try {
			conn = getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, modulePrefix);
			rs = ps.executeQuery();
			while (rs.next()) {
				String propName = rs.getString("GP_NOMBRE");
				String propValue = rs.getString("GP_VALOR");
				if (moduleProperties.contains(propName))
					moduleProperties.setProperty(propName, moduleProperties.getProperty(propName) + "|" + propValue);
				else
					moduleProperties.setProperty(propName, propValue);

			}
		} finally {
			try {
				CloseObject.closeObject(rs, false);
				CloseObject.closeObject(ps, false);
				CloseObject.closeObject(conn, false);
			} catch (Exception e) {
				log.warn(e);
			}
		}
	}
	
	/**
	 * Devuelve un arreglo con el contenido de una propiedad compuesta. Por
	 * ejemplo si se tiene la propiedad <code>mop.mail.destinatario</code> en la
	 * que se especifica cada destinatario de correo, al cargar la propiedad, se
	 * encuentra de la forma destino1@mail.com|destino2@mail.com. Al llamar este
	 * metodo, devolvera un arreglo con el contenido
	 * <code>{destino1@mail.com,destino2@mail.com}</code>
	 * 
	 * @param propName
	 *            Nombre de la propiedad. (Se antepone automaticamente el
	 *            prefijo del modulo)
	 * @return Arreglo con los valores que contiene la propiedad.
	 */
	public String[] getCompositeProperty(String propName) {
		propName = modulePrefix + "." + propName;
		String property = this.moduleProperties.getProperty(propName);
		
		if (property == null || "".equals(property))
			return new String[] {};
		else
			return property.split("[|]");
	}

	/**
	 * Devuelve el valor de una propiedad. Antepone el nombre del modulo para
	 * buscar la propiedad, por lo que no debe especificarse en el parametro.
	 * 
	 * @param propName
	 * @return Valor de la propiedad.
	 */
	public String getProperty(String propName) {
		return this.moduleProperties.getProperty(this.modulePrefix + "." + propName);
	}
}
