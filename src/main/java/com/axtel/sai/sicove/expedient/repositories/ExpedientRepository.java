package com.axtel.sai.sicove.expedient.repositories;


import java.io.File;
import java.sql.Connection;
import java.util.List;

import com.axtel.sai.sicove.exceptions.SicoveException;
import com.axtel.sai.sicove.expedient.entities.DocumentFortimax;
import com.syc.fortimax.core.Fortimax;
import com.syc.fortimax.exceptions.FortimaxException;


public interface ExpedientRepository {

	List<DocumentFortimax> getExpedientDocuments( Connection conn, int idProcess, String folderExclude ) throws SicoveException;

	List<DocumentFortimax> getExpedientCapturedDocuments( Connection conn, int idProcess, String folderExclude ) throws SicoveException;

	Fortimax saveDocument( Connection conn, int idProcess, String folderName, String userName, String documentName, File file ) throws FortimaxException;

	Fortimax deleteDocument( Connection conn, int idProcess, String folderName, String userName, String documentName ) throws FortimaxException;

}
