package com.axtel.cfdi.stamp.core;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Base64;

public class VirtualFile {

    private int cfdiId;

    private LocalDateTime dateFile;

    private String fileName;

    private Path filePath;

    private String fileType;

    private String volumen;

    public int getCfdiId() {
        return cfdiId;
    }

    public LocalDateTime getDateFile() {
        return dateFile;
    }

    public String getFileName() {
        return fileName;
    }

    public Path getFilePath() {
        return filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public String getVolumen() {
        return volumen;
    }

    public void setCfdiId(int cfdiId) {
        this.cfdiId = cfdiId;
    }

    public void setDateFile(LocalDateTime dateFile) {
        this.dateFile = dateFile;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setVolumen(String volumen) {
        this.volumen = volumen;
    }

    @Override
    public String toString() {
        return "VirtualFile [cfdiId=" + cfdiId + ", volumen=" + volumen + ", fileName=" + fileName + ", filePath=" + filePath + ", dateFile=" + dateFile + ", fileType=" + fileType + "]";
    }
}
