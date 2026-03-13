package com.axtel.gestion.services;

import com.axtel.gestion.exception.DocumentTreeException;

public interface DocumentTreeService {

		String getTreeDocument( String id_caso ) throws DocumentTreeException;
	
}
