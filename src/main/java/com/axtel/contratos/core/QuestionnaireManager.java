package com.axtel.contratos.core;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.LogManager;
import com.syc.cfdi.db.CloseObject;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.gestion.core.Usuario;
import com.syc.sai.contabilidad.utils.db.RSToTable;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuestionnaireManager {

    private static final String REQUEST_APPLICATION = "APARTADO";

    private static final Logger log = LogManager.getLogger(QuestionnaireManager.class);

    /**
     * Lee las respuestas ingresadas al cuestionario de pago.
     *
     * @param conn Conexion activa a la base de datos.
     * @param header
     * @return
     * @throws SQLException
     */
    public static void readAnswersPayments(Connection conn, EgresoEncabezado header) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT questionId, ");
        query.append("       answer, ");
        query.append("       question ");
        query.append("  FROM vquestionnairepayments ");
        query.append(" WHERE ctipopago = ? ");
        query.append("   AND nfoliopago = ?   ");
        log.debug("Buscando respuestas al cuestionario: " + header.getTipoPago() + ", " + header.getFolioPago());
        QueryRunner run = new QueryRunner();
        ResultSetHandler<List<QuestionnaireAnswer>> h = new BeanListHandler<QuestionnaireAnswer>(QuestionnaireAnswer.class);
        List<QuestionnaireAnswer> answers = run.query(conn, query.toString(), h, header.getTipoPago(), header.getFolioPago());
        header.setQuestionnaireAnswers(answers);
    }

    public static int insertPaymentsQuestionnaire(Connection conn, EgresoEncabezado header) throws SQLException {
        int afectados = 0;
        StringBuilder queryInsert = new StringBuilder();
        queryInsert.append("INSERT INTO tCuestionarioPagosFIEL");
        queryInsert.append("           (cTipoPago");
        queryInsert.append("           ,nFolioPago");
        queryInsert.append("           ,nEnviadoSICOP");
        if (header.getFirmanteAut() != null)
            queryInsert.append("           ,cNumeroEmpleado");
        queryInsert.append("           ,cIdUsuarioCaptura");
        queryInsert.append("           ,cAplica15D)");
        queryInsert.append("     VALUES");
        queryInsert.append("           (?");
        queryInsert.append("           ,?");
        queryInsert.append("           ,?");
        if (header.getFirmanteAut() != null)
            queryInsert.append("           ,?");
        queryInsert.append("           ,?");
        queryInsert.append("           ,?)");
        PreparedStatement ps = null;
        try {
            int paramCnt = 1;
            ps = conn.prepareStatement(queryInsert.toString());
            ps.setString(paramCnt++, header.getTipoPago());
            ps.setInt(paramCnt++, header.getFolioPago());
            ps.setInt(paramCnt++, SolicitudFirmaElectronica.VO_BO_SICOP);
            if (header.getFirmanteAut() != null)
                ps.setString(paramCnt++, header.getFirmanteAut());
            ps.setString(paramCnt++, header.getLogin());
            ps.setString(paramCnt++, header.isAplica15D() ? "S" : "N");
            afectados += ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int insertAnswersPayments(Connection conn, EgresoEncabezado header) throws SQLException {
        int afectados = 0;
        StringBuilder queryInsert = new StringBuilder();
        queryInsert.append("INSERT INTO tCuestionarioPagos");
        queryInsert.append("           (cTipoPago");
        queryInsert.append("           ,nFolioPago");
        queryInsert.append("           ,nIdPregunta");
        queryInsert.append("           ,cRespuesta");
        queryInsert.append("           ,uLogin)");
        queryInsert.append("     VALUES(");
        queryInsert.append("           ?,");
        queryInsert.append("           ?,");
        queryInsert.append("           ?,");
        queryInsert.append("           ?,");
        queryInsert.append("           ?)");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(queryInsert.toString());
            for (QuestionnaireAnswer answer : header.getQuestionnaireAnswers()) {
                ps.setString(1, header.getTipoPago());
                ps.setInt(2, header.getFolioPago());
                ps.setInt(3, answer.getQuestionId());
                ps.setString(4, answer.getAnswer());
                ps.setString(5, header.getLogin());
                afectados += ps.executeUpdate();
            }
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int insertAnswers(Connection conn, ContractQuestionnaire contractQuestionnaire, Usuario usuario) throws SQLException {
        int afectados = 0;
        StringBuilder queryInsert = new StringBuilder();
        queryInsert.append("INSERT INTO tCuestionarioRequisicion");
        queryInsert.append("           (cIdSolicitud");
        queryInsert.append("           ,nIdPregunta");
        queryInsert.append("           ,cRespuesta");
        queryInsert.append("           ,uLogin)");
        queryInsert.append("     VALUES(");
        queryInsert.append("           ?,");
        queryInsert.append("           ?,");
        queryInsert.append("           ?,");
        queryInsert.append("           ?)");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(queryInsert.toString());
            for (QuestionnaireAnswer answer : contractQuestionnaire.getAnswers()) {
                ps.setString(1, contractQuestionnaire.getIdRequest());
                ps.setInt(2, answer.getQuestionId());
                ps.setString(3, answer.getAnswer());
                ps.setString(4, usuario.getLogin());
                afectados += ps.executeUpdate();
            }
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int insertQuestionnaire(Connection conn, ContractQuestionnaire contractQuestionnaire) throws SQLException {
        int afectados = 0;
        StringBuilder queryInsert = new StringBuilder();
        queryInsert.append("INSERT INTO tCuestionarioRequisicionFIEL");
        queryInsert.append("           (cIdSolicitud");
        queryInsert.append("           ,nEnviadoSICOP");
        queryInsert.append("           ,cNumeroEmpleado");
        queryInsert.append("           ,cIdUsuarioCaptura ");
        queryInsert.append("           ,cAplica15D)");
        queryInsert.append("     VALUES(");
        queryInsert.append("           ?,");
        queryInsert.append("           ?,");
        queryInsert.append("           ?,");
        queryInsert.append("           ?,");
        queryInsert.append("           ?)");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(queryInsert.toString());
            ps.setString(1, contractQuestionnaire.getIdRequest());
            ps.setInt(2, contractQuestionnaire.getStatus());
            ps.setString(3, contractQuestionnaire.getEmployeeNumber());
            ps.setString(4, contractQuestionnaire.getCaptureEmployeeLogin());
            ps.setString(5, contractQuestionnaire.isApply15D() ? "S" : "N");
            afectados += ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static void findCabinet(Connection conn, ContractQuestionnaire questionnaire) throws SQLException {
        String reservID = findeReservID(conn, questionnaire);
        StringBuilder querySel = new StringBuilder();
        querySel.append("SELECT	ID_GABINETE ");
        querySel.append("  FROM	IMX").append(REQUEST_APPLICATION);
        querySel.append(" WHERE	folio = ?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(querySel.toString());
            ps.setString(1, reservID);
            rs = ps.executeQuery();
            if (rs.next())
                questionnaire.setCabinetId(rs.getInt(1));
            else
                throw new SQLException("No se encontro gabinete para el apartado: " + reservID);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    private static String findeReservID(Connection conn, ContractQuestionnaire questionnaire) throws SQLException {
        StringBuilder querySel = new StringBuilder("select C_FOLIO_APA from mSolicitud where cIdSolicitud = ?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(querySel.toString());
            ps.setString(1, questionnaire.getIdRequest());
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getString(1);
            else
                throw new SQLException("No se encontro folio de apartado para la requisicion: " + questionnaire.getIdRequest());
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }

    public static ContractQuestionnaire readContractQuestionnaireFIEL(Connection conn, String requestID) throws SQLException, IllegalAccessException, InvocationTargetException {
        StringBuilder querySel = new StringBuilder();
        querySel.append("SELECT cIdSolicitud AS idRequest ");
        querySel.append("      ,nEnviadoSICOP AS status ");
        querySel.append("      ,cNumeroEmpleado AS employeeNumber ");
        querySel.append("      ,cIdUsuarioCaptura AS captureEmployeeLogin ");
        querySel.append("      ,CASE cAplica15D WHEN 'S' then 'true' else 'false' end AS apply15D ");
        querySel.append("  FROM dbo.tCuestionarioRequisicionFIEL ");
        querySel.append("WHERE cIdSolicitud = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(querySel.toString());
            ps.setString(1, requestID);
            rs = ps.executeQuery();
            Map<String, String> values = RSToTable.rsToMapCaseSensitive(rs);
            ContractQuestionnaire questionnaire = new ContractQuestionnaire();
            BeanUtils.populate(questionnaire, values);
            return questionnaire;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }

    public static int deleteQuestionnaireFIEL(Connection conn, String cIdRequest) throws SQLException {
        int afectados = 0;
        StringBuilder query = null;
        PreparedStatement ps = null;
        try {
            query = new StringBuilder();
            query.append("delete tCuestionarioRequisicionFIEL where cIdSolicitud=?");
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cIdRequest);
            afectados += ps.executeUpdate();
            return afectados;
        } finally {
            query = null;
            CloseObject.closeObject(ps);
        }
    }

    public static int deleteQuestionnaire(Connection conn, String cIdRequest) throws SQLException {
        int afectados = 0;
        StringBuilder query = null;
        PreparedStatement ps = null;
        try {
            query = new StringBuilder();
            query.append("delete tCuestionarioRequisicion where cIdSolicitud=?");
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cIdRequest);
            afectados += ps.executeUpdate();
            return afectados;
        } finally {
            query = null;
            CloseObject.closeObject(ps);
        }
    }

    public static int deleteIMXPAgina(Connection conn, int nCabinetId) throws SQLException {
        int afectados = 0;
        StringBuilder query = null;
        PreparedStatement ps = null;
        try {
            query = new StringBuilder();
            query.append("delete from IMX_PAGINA where TITULO_APLICACION = 'APARTADO' and ID_GABINETE = ? and ID_CARPETA_PADRE =  2  and ID_DOCUMENTO IN (1,	2) ");
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, nCabinetId);
            afectados += ps.executeUpdate();
            return afectados;
        } finally {
            query = null;
            CloseObject.closeObject(ps);
        }
    }
}
