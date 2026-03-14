package com.axtel.cfdi.stamp.service;

import java.sql.Connection;
import com.axtel.cfdi.stamp.core.VirtualFile;
import com.axtel.cfdi.stamp.core.Volumen;
import java.util.Base64;

public interface VolumenServiceInterface {

    Volumen getVolumen(Connection conn) throws com.axtel.cfdi.exceptions.FileManagmentException;

    VirtualFile generateFileLocation(Connection conn, String extension) throws com.axtel.cfdi.exceptions.FileManagmentException;
}
