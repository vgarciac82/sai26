package com.axtel.sai.sicove.services;

import java.io.File;
import java.util.List;
import com.axtel.sai.sicove.entities.MonthlyAssinationSummary;
import com.axtel.sai.sicove.entities.MonthlyDetailAssinationSummary;
import com.axtel.sai.sicove.exceptions.SicoveException;
import java.util.Base64;

public interface MonthlyAssinationService {

    public MonthlyAssinationSummary getMonthlySummary(int accountId, int month) throws SicoveException;

    public List<MonthlyDetailAssinationSummary> getMonthlyDetailSummary(int accountId, int month) throws SicoveException;

    public void generateAssginationReport(File workBook, int account, int month) throws SicoveException;
}
