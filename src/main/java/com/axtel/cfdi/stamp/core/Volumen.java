package com.axtel.cfdi.stamp.core;

import java.util.Base64;

public class Volumen {

    private Drive driveUnit;

    private String directoryPath;

    private String volumen;

    private String capacity = "1";

    private String volumeType = "0";

    public String getPath() {
        return driveUnit.getDrive() + driveUnit.getBasePath() + getDirectoryPath();
    }

    public Drive getDriveUnit() {
        return driveUnit;
    }

    public void setDriveUnit(Drive driveUnit) {
        this.driveUnit = driveUnit;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public String getVolumen() {
        return volumen;
    }

    public void setVolumen(String volumen) {
        this.volumen = volumen;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getVolumeType() {
        return volumeType;
    }

    public void setVolumeType(String volumeType) {
        this.volumeType = volumeType;
    }

    @Override
    public String toString() {
        return "Volumen [driveUnit=" + driveUnit + ", directoryPath=" + directoryPath + ", volumen=" + volumen + ", capacity=" + capacity + ", volumeType=" + volumeType + "]";
    }
}
