package com.syc.info.cfdi.export;

import java.sql.Connection;
import java.util.List;
import com.syc.cfdi.db.CloseObject;
import com.syc.cfdi.util.CFDIUtils;
import com.syc.info.cfdi.export.config.DBConfigurator;
import java.util.Base64;

public class ExporterTool {

    public String[] makeExport(DBConfigurator dbConfigurator, String reportFilePath) throws Exception {
        Connection conn = null;
        try {
            conn = CFDIUtils.getConnection(dbConfigurator);
            //			List<String> foliosProc = ExportReportManager.readAndUpdateReport(conn, reportFilePath );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseObject.closeObject(conn);
        }
        return null;
    }
}
