package com.axtel.sai.sicove.repositories;


import java.sql.Connection;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;

import com.axtel.sai.sicove.entities.MonthlyAssinationSummary;
import com.axtel.sai.sicove.entities.MonthlyDetailAssinationSummary;
import com.axtel.sai.sicove.exceptions.SicoveException;


public interface MonthlyAssinationRepository {

	public MonthlyAssinationSummary getMonthlySummary( Connection conn, int accountId, int month ) throws SicoveException;

	public List<MonthlyDetailAssinationSummary> getMonthlyDetail( Connection conn, int accountId, int month ) throws SicoveException;

	public HSSFSheet getMonthlyAsignationsSheet( Connection conn, HSSFSheet sheet, int account, int month ) throws SicoveException;

	public HSSFSheet getMonthlyFuellingSheet( Connection conn, HSSFSheet sheetAt, int account, int month ) throws SicoveException;

	public HSSFSheet getMonthlyVerificationSheet( Connection conn, HSSFSheet sheet, int account, int month ) throws SicoveException;

	HSSFSheet getMonthlySummarySheet( Connection conn, HSSFSheet sheet, int account, int month ) throws SicoveException;

}
