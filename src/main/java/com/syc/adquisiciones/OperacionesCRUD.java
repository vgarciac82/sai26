package com.syc.adquisiciones;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public interface OperacionesCRUD {
	public boolean Creade(Map<String, String> map,Connection conn) throws SQLException;
	public boolean Reade(Map<String, String> map,Connection conn)throws SQLException;
	public boolean Update(Map<String, String> map,Connection conn)throws SQLException;
	public boolean Delete(Map<String, String> map,Connection conn)throws SQLException;
}
