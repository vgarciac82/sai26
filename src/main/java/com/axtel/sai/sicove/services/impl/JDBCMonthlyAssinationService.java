package com.axtel.sai.sicove.services.impl;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import com.axtel.sai.sicove.entities.MonthlyAssinationSummary;
import com.axtel.sai.sicove.entities.MonthlyDetailAssinationSummary;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.axtel.sai.sicove.repositories.MonthlyAssinationRepository;
import com.axtel.sai.sicove.services.MonthlyAssinationService;
import com.syc.cfdi.db.CloseObject;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import java.util.Base64;
import java.nio.file.Paths;

public class JDBCMonthlyAssinationService extends DataSourceManager implements MonthlyAssinationService {

    private MonthlyAssinationRepository monthlyAssinationRepository;

    public JDBCMonthlyAssinationService(String jniName, MonthlyAssinationRepository monthlyAssinationRepository) {
        super.init(jniName);
        this.monthlyAssinationRepository = monthlyAssinationRepository;
    }

    @Override
    public MonthlyAssinationSummary getMonthlySummary(int accountId, int month) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            return monthlyAssinationRepository.getMonthlySummary(conn, accountId, month);
        } catch (Exception e) {
            throw new SicoveException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public List<MonthlyDetailAssinationSummary> getMonthlyDetailSummary(int accountId, int month) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            return monthlyAssinationRepository.getMonthlyDetail(conn, accountId, month);
        } catch (Exception e) {
            throw new SicoveException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public void generateAssginationReport(File workBookSource, int account, int month) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            HSSFWorkbook workBook = Util.openExcel(workBookSource);
            monthlyAssinationRepository.getMonthlyAsignationsSheet(conn, workBook.getSheetAt(0), account, month);
            monthlyAssinationRepository.getMonthlyFuellingSheet(conn, workBook.getSheetAt(1), account, month);
            monthlyAssinationRepository.getMonthlyVerificationSheet(conn, workBook.getSheetAt(2), account, month);
            monthlyAssinationRepository.getMonthlySummarySheet(conn, workBook.getSheetAt(3), account, month);
            workBook.write(workBookSource.toPath());
        } catch (Exception e) {
            throw new SicoveException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
