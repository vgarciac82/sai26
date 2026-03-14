package com.syc.cfdi;

import java.io.File;
import java.util.Base64;

public final class MassPaymentInvoiceComponents extends ComponentesFactura {

    private File activityReportFile;

    private String activityReportFileName;

    private boolean activityReportLoaded = false;

    public File getActivityReportFile() {
        return activityReportFile;
    }

    public String getActivityReportFileName() {
        return activityReportFileName;
    }

    public boolean isActivityReportLoaded() {
        return activityReportLoaded;
    }

    public void setActivityReportFile(File activityReportFile) {
        this.activityReportFile = activityReportFile;
    }

    public void setActivityReportFileName(String activityReportFileName) {
        this.activityReportFileName = activityReportFileName;
    }

    public void setActivityReportLoaded(boolean activityReportLoaded) {
        this.activityReportLoaded = activityReportLoaded;
    }
}
