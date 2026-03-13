package com.axtel.cfdi.stamp.core;


public class Drive {

	private String	drive;
	private String	basePath;
	private int		driveStatus;
	private String	driveType;

	public String getDrive() {
		return drive;
	}

	public void setDrive( String drive ) {
		this.drive = drive;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath( String basePath ) {
		this.basePath = basePath;
	}

	public int getDriveStatus() {
		return driveStatus;
	}

	public void setDriveStatus( int driveStatus ) {
		this.driveStatus = driveStatus;
	}

	public String getDriveType() {
		return driveType;
	}

	public void setDriveType( String driveType ) {
		this.driveType = driveType;
	}

	@Override
	public String toString() {
		return "Drive [drive=" + drive + ", basePath=" + basePath + ", driveStatus=" + driveStatus + ", driveType=" + driveType + "]";
	}

}
