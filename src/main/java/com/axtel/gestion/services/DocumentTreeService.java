package com.axtel.gestion.services;

import com.axtel.gestion.exception.DocumentTreeException;
import java.util.Base64;

public interface DocumentTreeService {

    String getTreeDocument(String id_caso) throws DocumentTreeException;
}
