package com.syc.sai.procesos;

import java.sql.Connection;
import com.axtel.contratos.QuestionnaireBussinessLogic;
import com.axtel.contratos.core.ContractQuestionnaire;
import com.axtel.contratos.core.QuestionnaireManager;
import com.syc.egresos.firmante.core.FirmanteManager;
import com.syc.egresos.firmante.servlet.Firmante;
import com.syc.gestion.util.Util;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import java.util.Base64;

public class ResendQuestionnaireMail {

    public static void main(String[] args) throws Exception {
        ResendQuestionnaireMail rqm = new ResendQuestionnaireMail();
        rqm.sendNotificacion("RM-A02-1", "vgarciac@axtel.com.mx");
    }

    private void sendNotificacion(String id, String mail) throws Exception {
        Connection conn = Util.getStandAloneConnection();
        QuestionnaireBussinessLogic qbl = new QuestionnaireBussinessLogic(true);
        ContractQuestionnaire questionnaire = QuestionnaireManager.readContractQuestionnaireFIEL(conn, id);
        Firmante f = FirmanteManager.readRequestSignatory(conn, questionnaire);
        f.setCorreoEmpleado(mail);
        qbl.setSignatory(f);
        qbl.setDocument(ContractQuestionnaire.APPLICATION);
        qbl.setQuestionnaire(questionnaire);
        qbl.notificaOperacionPendiente(conn, SolicitudFirmaElectronica.AUTORIZA);
    }
}
