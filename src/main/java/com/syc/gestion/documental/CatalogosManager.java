package com.syc.gestion.documental;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.apache.openjpa.lib.log.Log;

import com.syc.adquisiciones.manager.BaseSACManager;
import com.syc.adquisiciones.vo.ConexionesBD;

public class CatalogosManager {
	private static Logger log = Logger.getLogger(CatalogosManager.class);
	public static String[][] getSelectQuery(Connection conn, String query) throws SQLException, ServletException
	{

		String[][] retVal = null;
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		
		try 
		{			
			pstmnt = conn.prepareStatement(query);

			rs = pstmnt.executeQuery();
			List MyList = new ArrayList();
			int columnas = rs.getMetaData().getColumnCount();
			
			while (rs.next())
			{
				String[] arrRow = new String[columnas];
				for (int i = 0; i < columnas; i++)
				{
					if (rs.getString(i+1) == null)
						arrRow[i] = " ";
					else
						arrRow[i] = rs.getString(i+1);
				}
				MyList.add(arrRow);
			}
			retVal = new String[MyList.size()][columnas];
			MyList.toArray(retVal);

		}
		catch(SQLException ex)
		{
			throw new ServletException(ex);
		}
		finally 
		{
			if (rs != null)
				rs.close();

			if (pstmnt != null)
				pstmnt.close();

			rs = null;
			pstmnt = null;
		}

		return retVal;
	}

	public static void setInsertQuery(Connection conn, String query) throws ServletException, SQLException 
	{
		PreparedStatement pstmnt = null;
		try 
		{			
			pstmnt = conn.prepareStatement(query);
			log.info( query );
			pstmnt.executeUpdate();
			//GAF 2010-04-19
			//En los manager no debe haber commits!
			//solamente en los businesslogic o servlets
			//conn.commit();
		}
		catch (SQLException se)
		{
			/*
			try {
				conn.rollback();
			} catch (SQLException e) {
				System.err.println("[" + this.getClass().getName() + "] Error SQL rollback");
			}
			*/
			throw new ServletException(se);
		} 
		catch (Exception e)
		{
			throw new ServletException(e);
		}		
		finally 
		{
			try
			{
				pstmnt.close();
			}
			catch (SQLException se) 
			{
				conn.rollback();
				throw new ServletException(se);
			}
			pstmnt = null;
		}
	}
	
	
	public static List<ConexionesBD> getBasesDeDatos(Connection conn, String query) throws SQLException, ServletException
	{

		
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		ConexionesBD cbd = null;
		List<ConexionesBD> MyList = new ArrayList<ConexionesBD>();
		
		
		try 
		{			
			pstmnt = conn.prepareStatement(query);

			rs = pstmnt.executeQuery();
			
			
			while (rs.next())
			{
				cbd = new ConexionesBD();
				cbd.setEjercicioFiscal(rs.getString("AEJERCICIOFISCAL"));
				cbd.setcActivo(rs.getString("CACTIVO"));
				cbd.setNombreBD(rs.getString("CNOMBREBD"));
				cbd.setUsuarioBD(rs.getString("CUSERBD"));
				cbd.setPassBD(rs.getString("CPASSBD"));
				cbd.setServidor(rs.getString("CDIRECCIONSERVER"));
				cbd.setPuerto(rs.getString("CPUERTOBD"));
				MyList.add(cbd);
			}
			
		}
		catch(SQLException ex)
		{
			throw new ServletException(ex);
		}
		finally 
		{
			if (rs != null)
				rs.close();

			if (pstmnt != null)
				pstmnt.close();

			rs = null;
			pstmnt = null;
		}

		return MyList;
	}

}
