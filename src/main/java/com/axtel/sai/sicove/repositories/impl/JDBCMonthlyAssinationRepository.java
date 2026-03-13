package com.axtel.sai.sicove.repositories.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import com.axtel.sai.sicove.entities.MonthlyAssinationSummary;
import com.axtel.sai.sicove.entities.MonthlyDetailAssinationSummary;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.axtel.sai.sicove.repositories.MonthlyAssinationRepository;
import com.syc.cfdi.db.CloseObject;
import com.syc.gestion.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCMonthlyAssinationRepository implements MonthlyAssinationRepository {

    private static final Logger log = LoggerFactory.getLogger(JDBCMonthlyAssinationRepository.class);

    private final QueryRunner runner = new QueryRunner();

    private final ResultSetHandler<MonthlyAssinationSummary> assignationSummaryRequestHandler = new BeanHandler<MonthlyAssinationSummary>(MonthlyAssinationSummary.class);

    private final ResultSetHandler<List<MonthlyDetailAssinationSummary>> assignationDetailRequestHandler = new BeanListHandler<MonthlyDetailAssinationSummary>(MonthlyDetailAssinationSummary.class);

    @Override
    public MonthlyAssinationSummary getMonthlySummary(Connection conn, int accountId, int month) throws SicoveException {
        log.info("Getting Assignation Sumary for month " + month + " id account: " + month);
        StringBuilder querySelect = new StringBuilder();
        querySelect.append(" SELECT	asignation_id_account AS asignationIdAccount, ");
        querySelect.append(" 		asignation_request_month AS asignationRequestMonth, ");
        querySelect.append(" 		asignation_autorized_amount AS asignationAutorizedAmount, ");
        querySelect.append(" 		(ISNULL(fueling_authorized_amount, 0.00) - ISNULL(fueling_refund_amount,0.00) ) AS fuelingAuthorizedAmount, ");
        querySelect.append(" 		ISNULL(fueling_authorized_amount, 0.00)  AS totalFuelingAuthorizedAmount, ");
        querySelect.append(" 		ISNULL( fueling_refund_amount, 0.00) AS fuelingRefundAmount ");
        querySelect.append("   FROM	( ");
        querySelect.append(" 		SELECT	request_month AS asignation_request_month,  ");
        querySelect.append(" 				id_account AS asignation_id_account, ");
        querySelect.append(" 				SUM( autorized_amount) AS asignation_autorized_amount ");
        querySelect.append(" 			FROM	vFuelingRequest vfr WITH(NOLOCK) ");
        querySelect.append(" 			WHERE	vfr.request_status = 4 ");
        querySelect.append(" 			AND	vfr.request_month = ? ");
        querySelect.append(" 			AND	vfr.id_account = ? ");
        querySelect.append(" 		GROUP BY vfr.request_month,  ");
        querySelect.append(" 					vfr.id_account ");
        querySelect.append(" 		) AS asignations  ");
        querySelect.append(" 		LEFT OUTER JOIN ");
        querySelect.append(" 		( ");
        querySelect.append(" 		SELECT	vfw.request_month AS fueling_request_month, ");
        querySelect.append(" 				vfw.id_account AS fueling_id_account, ");
        querySelect.append(" 				SUM(vfw.authorized_amount) AS fueling_authorized_amount ");
        querySelect.append(" 		  FROM	vFuelingWallet vfw  ");
        querySelect.append(" 		 WHERE	vfw.id_status IN (4, 7, 8, 9) ");
        querySelect.append(" 		   AND	vfw.request_month = ? ");
        querySelect.append(" 		   AND	vfw.id_account = ? ");
        querySelect.append(" 		GROUP By vfw.request_month,  ");
        querySelect.append(" 				 vfw.id_account ");
        querySelect.append(" 		) AS fuelling ");
        querySelect.append(" 		ON ");
        querySelect.append(" 			asignations.asignation_id_account = fuelling.fueling_id_account ");
        querySelect.append(" 		LEFT OUTER JOIN ");
        querySelect.append(" 		( ");
        querySelect.append(" 		 ");
        querySelect.append(" 		SELECT	Month(fawr.registration_date) AS refund_month,  ");
        querySelect.append(" 				faw.id_contract_account       AS refund_id_account,  ");
        querySelect.append(" 				Sum(fawr.refund_amount)       AS fueling_refund_amount ");
        querySelect.append(" 		  FROM  fuel_account_wallet_refund fawr WITH(NOLOCK) ");
        querySelect.append(" 		  INNER JOIN ");
        querySelect.append(" 		  fuelaccountwallets faw WITH(NOLOCK) ");
        querySelect.append(" 		  ON ");
        querySelect.append(" 		  fawr.id_fuel_account_wallets = faw.id_fuel_account_wallets ");
        querySelect.append(" 		 WHERE  Month(fawr.registration_date) = ? ");
        querySelect.append(" 		   AND faw.id_contract_account = ? ");
        querySelect.append(" 		GROUP BY  ");
        querySelect.append(" 				MONTH(fawr.registration_date), ");
        querySelect.append(" 				faw.id_contract_account  ");
        querySelect.append(" 		) as refunds ");
        querySelect.append(" 		ON  ");
        querySelect.append(" 			fuelling.fueling_id_account = refund_id_account ");
        MonthlyAssinationSummary monthlyAssinationSummary;
        try {
            log.trace("Executing Query: \n" + querySelect + "\n[" + month + "]\n[" + accountId + "]" + "\n[" + month + "]\n[" + accountId + "]" + "\n[" + month + "]\n[" + accountId + "]");
            monthlyAssinationSummary = runner.query(conn, querySelect.toString(), assignationSummaryRequestHandler, month, accountId, month, accountId, month, accountId);
            log.debug("Founded: " + monthlyAssinationSummary);
            return monthlyAssinationSummary;
        } catch (SQLException e) {
            throw new SicoveException(e);
        }
    }

    @Override
    public List<MonthlyDetailAssinationSummary> getMonthlyDetail(Connection conn, int accountId, int month) throws SicoveException {
        log.info("Getting Assignation Sumary for month " + month + " id account: " + month);
        StringBuilder querySelect = new StringBuilder();
        querySelect.append(" SELECT	request_date AS requestDate, ");
        querySelect.append("      	    request_amount AS requestedAmount, ");
        querySelect.append("       	ISNULL(autorized_amount,0.00) authorizedAmount, ");
        querySelect.append("       	request_month month,");
        querySelect.append("       	id_account AS asignationIdAccount");
        querySelect.append("  FROM  vfuelingrequest ");
        querySelect.append(" WHERE  request_month =  ? ");
        querySelect.append("   AND	id_account =  ?");
        List<MonthlyDetailAssinationSummary> monthlyDetailAssinationSummary;
        try {
            log.trace("Executing Query: \n" + querySelect + "\n[" + month + "]\n[" + accountId + "]");
            monthlyDetailAssinationSummary = runner.query(conn, querySelect.toString(), assignationDetailRequestHandler, month, accountId);
            log.debug("Founded: " + monthlyDetailAssinationSummary);
            return monthlyDetailAssinationSummary;
        } catch (SQLException e) {
            throw new SicoveException(e);
        }
    }

    @Override
    public HSSFSheet getMonthlyAsignationsSheet(Connection conn, HSSFSheet sheet, int account, int month) throws SicoveException {
        StringBuilder querySelect = new StringBuilder();
        querySelect.append("SELECT	id_fuel_provisioning_request, request_date, employee_responsible_name, request_amount,");
        querySelect.append("     	autorized_amount, request_justification  ");
        querySelect.append("  FROM	vfuelingrequest ");
        querySelect.append(" WHERE	id_account = ? ");
        querySelect.append("   AND	request_month = ?");
        ResultSet rsAsignation = null;
        PreparedStatement psAsignation = null;
        try {
            psAsignation = conn.prepareStatement(querySelect.toString());
            psAsignation.setInt(1, account);
            psAsignation.setInt(2, month);
            rsAsignation = psAsignation.executeQuery();
            Util.resultSetToExcel(rsAsignation, sheet, 1, false);
            return sheet;
        } catch (Exception e) {
            throw new SicoveException("Error generando hoja de asignacion mensual: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(rsAsignation);
            CloseObject.closeObject(psAsignation);
        }
    }

    @Override
    public HSSFSheet getMonthlyFuellingSheet(Connection conn, HSSFSheet sheet, int account, int month) throws SicoveException {
        StringBuilder querySelect = new StringBuilder();
        querySelect.append("SELECT request_month           AS mes, ");
        querySelect.append("       administrativeunitname  AS unidad, ");
        querySelect.append("       fueling_request_id      AS folio, ");
        querySelect.append("       request_date            AS fecha_solicitud, ");
        querySelect.append("       end_date                AS fin_comision, ");
        querySelect.append("       justification_text      AS jutificacion, ");
        querySelect.append("       employeeresponsiblename AS solicitado_por, ");
        querySelect.append("       state_name              AS lugar_de_comision, ");
        querySelect.append("       liscence_plate          AS placa, ");
        querySelect.append("       wallet_number           AS tarjeta, ");
        querySelect.append("       sub_brand               AS datos_vehiculares, ");
        querySelect.append("       fueling_amount          AS litros_solicitados, ");
        querySelect.append("       authorized_amount       AS litros_asignados, ");
        querySelect.append("       id_status               AS id_status, ");
        querySelect.append("       fueling_status          AS estatus, ");
        querySelect.append("       id_account ");
        querySelect.append("FROM   vfuelingwallet ");
        querySelect.append("WHERE  id_account = ? ");
        querySelect.append("       AND request_month = ?  ");
        ResultSet rsFuelling = null;
        PreparedStatement psFuelling = null;
        try {
            psFuelling = conn.prepareStatement(querySelect.toString());
            psFuelling.setInt(1, account);
            psFuelling.setInt(2, month);
            rsFuelling = psFuelling.executeQuery();
            Util.resultSetToExcel(rsFuelling, sheet, 1, false);
            return sheet;
        } catch (Exception e) {
            throw new SicoveException("Error generando hoja de asignacion mensual: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(rsFuelling);
            CloseObject.closeObject(psFuelling);
        }
    }

    @Override
    public HSSFSheet getMonthlyVerificationSheet(Connection conn, HSSFSheet sheet, int account, int month) throws SicoveException {
        StringBuilder querySelect = new StringBuilder();
        querySelect.append("  SELECT fuelling.id_account,  ");
        querySelect.append("         fuelling.request_month,  ");
        querySelect.append("         reqVerif.id_verification,  ");
        querySelect.append("         fuelling.fueling_request_id,  ");
        querySelect.append("         reqVerif.current_wallet_balance,  ");
        querySelect.append("         reqVerif.initial_vehicle_kilometers,  ");
        querySelect.append("         reqVerif.current_vehicle_kilometers,  ");
        querySelect.append("         fuelling.liscence_plate,  ");
        querySelect.append("         fuelling.wallet_number,  ");
        querySelect.append("         fuelling.contract_number,  ");
        querySelect.append("         fuelling.authorized_amount,  ");
        querySelect.append("         ISNULL( vdt.validated_amount, 0.00 ) AS validated_amount, ");
        querySelect.append("         fuelling.fueling_status  ");
        querySelect.append("  FROM   wallet_fuel_request_verification reqVerif  ");
        querySelect.append("         INNER JOIN vfuelingwallet fuelling  ");
        querySelect.append("                 ON reqVerif.fueling_request_id = fuelling.fueling_request_id  ");
        querySelect.append("         LEFT OUTER JOIN v_verification_detail_total vdt ");
        querySelect.append("                 ON reqVerif.id_verification = vdt.id_verification ");
        querySelect.append("WHERE  id_account = ? ");
        querySelect.append("       AND request_month = ?  ");
        ResultSet rsFuelling = null;
        PreparedStatement psFuelling = null;
        try {
            psFuelling = conn.prepareStatement(querySelect.toString());
            psFuelling.setInt(1, account);
            psFuelling.setInt(2, month);
            rsFuelling = psFuelling.executeQuery();
            Util.resultSetToExcel(rsFuelling, sheet, 1, false);
            return sheet;
        } catch (Exception e) {
            throw new SicoveException("Error generando hoja de asignacion mensual: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(rsFuelling);
            CloseObject.closeObject(psFuelling);
        }
    }

    @Override
    public HSSFSheet getMonthlySummarySheet(Connection conn, HSSFSheet sheet, int account, int month) throws SicoveException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT fuelling.liscence_plate, ");
        query.append("       fuelling.wallet_number, ");
        query.append("       fuelling.description, ");
        query.append("       fuelling.total_amount_allocated, ");
        query.append("       Isnull(verification.validation_amount, 0) AS validation_amount, ");
        query.append("       Isnull(refund.validation_amount, 0)       AS refund_amount, ");
        query.append("       fuelling.total_amount_allocated - Isnull(verification.validation_amount, ");
        query.append("                                         0) ");
        query.append("       - Isnull(refund.validation_amount, 0)     AS amount_pending ");
        query.append("FROM   (SELECT liscence_plate, ");
        query.append("               wallet_number, ");
        query.append("               description, ");
        query.append("               Sum(authorized_amount) AS total_amount_allocated ");
        query.append("        FROM   vfuelingwallet fuelling ");
        query.append("        WHERE  fuelling.id_status IN ( 4, 7, 8, 9 ) ");
        query.append("               AND id_account = ? ");
        query.append("               AND request_month = ? ");
        query.append("        GROUP  BY liscence_plate, ");
        query.append("                  wallet_number, ");
        query.append("                  description) AS fuelling ");
        query.append("       LEFT OUTER JOIN (SELECT fuelling.liscence_plate, ");
        query.append("                               fuelling.wallet_number, ");
        query.append("                               Sum(detverif.validated_amount) AS ");
        query.append("                               validation_amount ");
        query.append("                        FROM   wallet_fuel_request_verification reqRefun ");
        query.append("                               INNER JOIN vfuelingwallet fuelling ");
        query.append("                                       ON reqrefun.fueling_request_id = ");
        query.append("                                          fuelling.fueling_request_id ");
        query.append("                               INNER JOIN v_verification_detail_total detVerif ");
        query.append("                                       ON reqrefun.id_verification = ");
        query.append("                                          detverif.id_verification ");
        query.append("                        WHERE  fuelling.id_status = 9 ");
        query.append("                               AND fuelling.id_account = ? ");
        query.append("                               AND fuelling.request_month = ? ");
        query.append("                        GROUP  BY fuelling.liscence_plate, ");
        query.append("                                  fuelling.wallet_number) AS verification ");
        query.append("                    ON fuelling.liscence_plate = verification.liscence_plate ");
        query.append("                       AND fuelling.wallet_number = verification.wallet_number ");
        query.append("       LEFT OUTER JOIN ");
        query.append("       (SELECT Isnull(v.liscence_plate, 'TARJETA NO ASIGNADA') ");
        query.append("               AS ");
        query.append("               liscence_plate, ");
        query.append("               reqrefun.wallet_number, ");
        query.append("               Sum(reqrefun.refund_amount) ");
        query.append("               AS ");
        query.append("              validation_amount ");
        query.append("                        FROM   fuel_account_wallet_refund reqRefun ");
        query.append("                               INNER JOIN fuelaccountwallets accountWallet ");
        query.append("                                       ON reqrefun.wallet_number = ");
        query.append("                                          accountwallet.wallet_number ");
        query.append("                               LEFT OUTER JOIN vehicle v ");
        query.append("                                            ON ");
        query.append("                               accountwallet.vehicle_inventory_id = ");
        query.append("                               v.id_inventory ");
        query.append("                        WHERE  accountwallet.id_contract_account = ? ");
        query.append("                               AND Month(reqrefun.registration_date) = ? ");
        query.append("                        GROUP  BY ");
        query.append("       Isnull(v.liscence_plate, 'TARJETA NO ASIGNADA'), ");
        query.append("       Month(reqrefun.registration_date), ");
        query.append("       reqrefun.wallet_number) AS refund ");
        query.append("                    ON fuelling.wallet_number = refund.wallet_number  ");
        ResultSet rsFuelling = null;
        PreparedStatement psFuelling = null;
        try {
            log.trace("Ejecutando:  " + query);
            psFuelling = conn.prepareStatement(query.toString());
            psFuelling.setInt(1, account);
            psFuelling.setInt(2, month);
            psFuelling.setInt(3, account);
            psFuelling.setInt(4, month);
            psFuelling.setInt(5, account);
            psFuelling.setInt(6, month);
            rsFuelling = psFuelling.executeQuery();
            Util.resultSetToExcel(rsFuelling, sheet, 2, false);
            return sheet;
        } catch (Exception e) {
            throw new SicoveException("Error generando hoja de asignacion mensual: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(rsFuelling);
            CloseObject.closeObject(psFuelling);
        }
    }
}
