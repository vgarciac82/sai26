package com.syc.adquisiciones.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import com.syc.crud.dsmngr.DataSourceManager;

public class ObtienePrecioUnitarioPAA extends HttpServlet {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private static String jndiName = null;
	private static Logger log = Logger.getLogger(ObtienePrecioUnitarioPAA.class);
	private Connection conn = null;
	private Statement stm = null;
	private ResultSet rs=null;	
	private String mensaje="";

	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			InitialContext ic = new InitialContext();
			jndiName = (String) ic.lookup("java:comp/env/dataSourceRefName");
			if (jndiName == null) {
				jndiName = "jdbc/gestion";
				log.info("Environment Entry \"dataSourceRefName\" nula usando default \""+ jndiName + "\"");
			} else
				log.info("dataSourceRefName=" + jndiName);
		} catch (NamingException exc) {
			mensaje = "Error: al obtener dataSource.";
			jndiName = "jdbc/gestion";
			log.info("Environment Entry \"dataSourceRefName\" no definida usando default \""+ jndiName + "\"");
		}
	}
	
	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String param[]= request.getParameter("Param").toString().split(",");
		BigDecimal precioUnitarioPromedio = new BigDecimal("0.00");
		precioUnitarioPromedio.setScale(2);
		PrintWriter out = response.getWriter(); 
		
		try{
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			List<DatosConexion> datosConexion = getEjerciciosFiscales(Integer.parseInt(param[2])); 
			
			for(DatosConexion dc : datosConexion){
				try{
					conn = DriverManager.getConnection("jdbc:sqlserver://"+dc.getDireccionServer()+
						";databaseName="+dc.getNombreBD()+";user="+dc.getUserBD()+";password="+dc.getPassBD()+";");
					stm = conn.createStatement();
					rs =stm.executeQuery("select (case when total=0 then 0 else total/meses end) as pu from "+
										"(select COUNT(*) as meses, (case when SUM(mPrecioUnitario) is null then 0 else SUM(mPrecioUnitario) end) as total "+ 
										"from mProgramaAnualDetallePeriodo with(nolock)"+ 
										"where cIdCABM='"+param[0]+"' and cIdUnidadEjecutora='"+param[1]+"' AND mPrecioUnitario>0) as tab");
					
					while (rs.next())
						precioUnitarioPromedio = rs.getBigDecimal(1);
						
					try {
						conn.close();
						stm.close();
						rs.close();
					} catch (SQLException e1) {
						mensaje = "Error: No se logro cerrar conexion.";
						e1.printStackTrace();
					}
					
					if(precioUnitarioPromedio.compareTo(new BigDecimal("0.00")) > 0){
						mensaje = precioUnitarioPromedio.toString();
						mensaje = mensaje.substring(0, mensaje.length()-2);
						break;
					}
				}	
				catch(Exception e){
					log.error("Error: No se logro consultar el precio unitario.");
					mensaje = "Error: No se logro consultar el precio unitario.";
					try {
						conn.close();
						stm.close();
						rs.close();
					} catch (SQLException e1) {
						log.error("Error: No se logro cerrar conexion.");
						mensaje = "Error: No se logro cerrar conexion.";
						e1.printStackTrace();
					}
				}
			}
			
			if(precioUnitarioPromedio.compareTo(new BigDecimal("0.00")) == 0)
				mensaje = "Error: No se encontro registro historico.";
			
		}
		catch(ClassNotFoundException ce){
			log.error("Error: no se encontro la clase com.microsoft.sqlserver.jdbc.SQLServerDriver");
			mensaje = "Error: no se encontro la clase com.microsoft.sqlserver.jdbc.SQLServerDriver";
		}
		
		log.info(mensaje);
		out.println(mensaje);
		
	}
	
	private List<DatosConexion> getEjerciciosFiscales(int ejercicioFiscalActual){
		List<DatosConexion> datosConexion = new ArrayList<DatosConexion>();
		DatosConexion dc = new DatosConexion();
		
		try{
			conn = DataSourceManager.getConnection(jndiName);
			stm = conn.createStatement();
			log.info("select * from tEjercicioFiscal with(nolock) where aEjercicioFiscal between "+(ejercicioFiscalActual-3)+" and "+(ejercicioFiscalActual-1)+" order by aEjercicioFiscal desc");
			rs = stm.executeQuery("select * from tEjercicioFiscal with(nolock) where aEjercicioFiscal between "+(ejercicioFiscalActual-3)+" and "+(ejercicioFiscalActual-1)+" order by aEjercicioFiscal desc");
			
			while(rs.next()){
				dc = new DatosConexion();
				dc.setAnio(Integer.parseInt(rs.getString(1)));
				dc.setActivo(Integer.parseInt(rs.getString(2)));
				dc.setNombreBD(rs.getString(3));
				dc.setUserBD(rs.getString(4));
				dc.setPassBD(rs.getString(5));
				dc.setDireccionServer(rs.getString(6));
				datosConexion.add(dc);
			}
			
			try {
				conn.close();
				stm.close();
				rs.close();
			} catch (SQLException e1) {
				mensaje = "Error: al consultar en el ejercicio fiscal "+ejercicioFiscalActual;
				e1.printStackTrace();
			}
			
		}
		catch(Exception e){
			mensaje = "Error: al crear la conexion a la base de datos del ejercicio fiscal "+ejercicioFiscalActual;
			try {
				conn.close();
				stm.close();
				rs.close();
			} catch (SQLException e1) {
				mensaje = "Error: al cerrar la conexion al ejercicio fiscal "+ejercicioFiscalActual;
				e1.printStackTrace();
			}
		}
		
		conn = null;
		stm = null;
		rs = null;
		return datosConexion;
	}
	
	public class DatosConexion{
		
		private int anio;
		private int activo;
		private String nombreBD;
		private String userBD;
		private String passBD;
		private String direccionServer;
		
		
		public DatosConexion() {
			// TODO Auto-generated constructor stub
		}		
		public int getAnio() {
			return anio;
		}
		public void setAnio(int anio) {
			this.anio = anio;
		}
		public int getActivo() {
			return activo;
		}
		public void setActivo(int activo) {
			this.activo = activo;
		}
		public String getNombreBD() {
			return nombreBD;
		}
		public void setNombreBD(String nombreBD) {
			this.nombreBD = nombreBD;
		}
		public String getUserBD() {
			return userBD;
		}
		public void setUserBD(String userBD) {
			this.userBD = userBD;
		}
		public String getPassBD() {
			return passBD;
		}
		public void setPassBD(String passBD) {
			this.passBD = passBD;
		}
		public String getDireccionServer() {
			return direccionServer;
		}
		public void setDireccionServer(String direccionServer) {
			this.direccionServer = direccionServer;
		}		
	}

}


