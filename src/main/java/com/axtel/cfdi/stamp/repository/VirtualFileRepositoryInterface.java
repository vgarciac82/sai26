package com.axtel.cfdi.stamp.repository;


import java.sql.Connection;

import com.axtel.cfdi.exceptions.FileManagmentException;
import com.axtel.cfdi.stamp.core.VirtualFile;


public interface VirtualFileRepositoryInterface {

	VirtualFile insert( Connection connection, VirtualFile virtualFile ) throws FileManagmentException;

	boolean existsFile( Connection filesConnection, int cfdiId, String string );

	VirtualFile select( Connection connection, int cfdiId, String type ) throws FileManagmentException;
}
