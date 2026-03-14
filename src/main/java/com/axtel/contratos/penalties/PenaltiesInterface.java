package com.axtel.contratos.penalties;

import org.json.JSONException;
import org.json.JSONObject;
import com.axtel.contratos.penalties.core.PenaltyAndDeduction;
import com.axtel.contratos.penalties.exception.PenaltiesExceptions;
import com.syc.adquisiciones.core.Respuesta;
import java.util.Base64;

public interface PenaltiesInterface {

    public JSONObject queryPenalties(PenaltyAndDeduction penalty) throws PenaltiesExceptions, JSONException;

    public void savePenalties(PenaltyAndDeduction penalty) throws PenaltiesExceptions;

    public void deletePenalties(PenaltyAndDeduction penalty) throws PenaltiesExceptions;

    public Respuesta areThereDocuments(PenaltyAndDeduction penalty) throws PenaltiesExceptions;

    public void sendPenalty(PenaltyAndDeduction penalty) throws PenaltiesExceptions;

    public void sendEmail(PenaltyAndDeduction penalty) throws PenaltiesExceptions;

    public void validatePenalty(PenaltyAndDeduction penalty) throws PenaltiesExceptions;

    public void addRowPenaltyItem(PenaltyAndDeduction penalty) throws PenaltiesExceptions;

    public void addRowDeductionItem(PenaltyAndDeduction penalty) throws PenaltiesExceptions;
}
