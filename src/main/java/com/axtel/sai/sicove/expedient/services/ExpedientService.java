package com.axtel.sai.sicove.expedient.services;

import java.io.File;
import java.util.List;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.axtel.sai.sicove.expedient.entities.DocumentFortimax;
import com.syc.fortimax.core.Fortimax;
import com.syc.fortimax.exceptions.FortimaxException;
import java.util.Base64;

public interface ExpedientService {

    List<DocumentFortimax> getExpedientDocuments(int idProcess, String folderExclude) throws SicoveException;

    public void saveDocument(File file, String documentSelect, int idProcess) throws SicoveException;

    List<DocumentFortimax> getExpedientCapturedDocuments(int idProcess, String folderExclude) throws SicoveException;

    void deleteDocument(Fortimax document, boolean cleanOnly) throws FortimaxException;
}
