package com.axtel.cfdi.stamp.core;


import java.sql.Connection;
import java.sql.SQLException;


public interface VolumenRepositoryInterface {

	public static final StringBuilder VOLUMEN_FIELDS = new StringBuilder( " volumen, drive, base_path AS basePath, directory_path AS directoryPath, capacity, volume_type AS volumeType " );

	Volumen select( Connection conn, String volumen ) throws SQLException;

	Volumen insert( Connection conn, Volumen volumen ) throws SQLException;

	Volumen getActive( Connection conn ) throws SQLException;

	Volumen getActive( Connection conn, Drive drive ) throws SQLException;

	Integer countByVolumen( Connection connection, Volumen volumen ) throws SQLException;

	void closeVolumen( Connection connection, String volumen, Volumen parentVolumen ) throws SQLException;
}
