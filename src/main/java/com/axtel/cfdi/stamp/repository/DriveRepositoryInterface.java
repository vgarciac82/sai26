package com.axtel.cfdi.stamp.repository;


import java.sql.Connection;
import java.sql.SQLException;

import com.axtel.cfdi.stamp.core.Drive;


public interface DriveRepositoryInterface {

	public static final StringBuilder DRIVE_FIELDS = new StringBuilder( "drive, basePath, drive_Status AS driveStatus, driveType" );

	Drive findById( Connection connection, String drive ) throws SQLException;

	Drive selectActive( Connection connection ) throws SQLException;

}
