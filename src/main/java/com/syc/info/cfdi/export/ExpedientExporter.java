package com.syc.info.cfdi.export;

import com.syc.info.cfdi.export.config.DBConfigurator;
import com.syc.info.cfdi.export.config.ReportConfig;
import java.util.Base64;

public class ExpedientExporter {

    private String dbPropertiesFilePath;

    private String reportFilePath;

    private ExporterTool exporterTool;

    public ExpedientExporter(String dbPropertiesFilePath, String reportFilePath) {
        this.dbPropertiesFilePath = dbPropertiesFilePath;
        this.reportFilePath = reportFilePath;
        this.exporterTool = new ExporterTool();
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        if (args == null || args.length < 3) {
            showUsage();
            return;
        }
        String dbPropertiesFilePath = args[0];
        String reportPropertiesFilePath = args[1];
        String reportFilePath = args[2];
        ReportConfig.initConfig(reportPropertiesFilePath);
        ExpedientExporter exporter = new ExpedientExporter(dbPropertiesFilePath, reportFilePath);
        exporter.makeExport();
    }

    private void makeExport() throws Exception {
        DBConfigurator dbConfigurator = DBConfigurator.instance(dbPropertiesFilePath);
        String[] paths = exporterTool.makeExport(dbConfigurator, reportFilePath);
    }

    public static void showUsage() {
        System.out.println("====================================================================================");
        System.out.println("Modo de uso: java -jar ExpedienteExporter.jar RUTA_CONFIG_CONN RUTA_ARCHIVO_AUXILIAR");
        System.out.println("Donde:");
        System.out.println("\tRUTA_CONFIG_CONN Es la ruta del archivo de configuracion a la base de datos.");
        System.out.println("\tRUTA_CONFIG_REPORT Es la ruta del archivo de configuracion del reporte.");
        System.out.println("\tRUTA_ARCHIVO_AUXILIAR Es el auxiliar del que se extraeran los archivos \n\tadjuntos.");
        System.out.println("====================================================================================");
    }
}
