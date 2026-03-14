package com.syc.info.cfdi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import com.syc.cfdi.util.CFDIUtils;
import com.syc.fortimax.core.ExpedientExporteThread;
import com.syc.fortimax.core.ExpedientExporterBusinessLogic;
import com.syc.fortimax.core.ExportLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ExpedientExporterAuditoria {

    private static List<Integer> EXECUTION_DAYS;

    private static Logger log;

    private static final String PROPERTIES_FILE_NAME = "ExpExp.properties";

    private static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private static String[] START_HOUR;

    private static String[] STOP_HOUR;

    private static Properties SYS_PROPERTIES;

    private static Map<String, Integer> WEEK_DAYS_KEY;

    private boolean esTest = false;

    public static void main(String[] args) throws Exception {
        boolean test = false;
        if (args.length > 0)
            test = "true".equalsIgnoreCase(args[0]);
        ExpedientExporterAuditoria eea = new ExpedientExporterAuditoria();
        eea.esTest = test;
        eea.execExport();
    }

    private List<ExpedientExporteThread> threads;

    public ExpedientExporterAuditoria() throws Exception {
        log = LoggerFactory.getLogger(ExpedientExporterAuditoria.class);
        String sysPropsPATH = System.getProperty("user.home") + File.separatorChar + PROPERTIES_FILE_NAME;
        synchronized (this) {
            if (WEEK_DAYS_KEY == null) {
                WEEK_DAYS_KEY = new LinkedHashMap<String, Integer>();
                WEEK_DAYS_KEY.put("D", 1);
                WEEK_DAYS_KEY.put("L", 2);
                WEEK_DAYS_KEY.put("A", 3);
                WEEK_DAYS_KEY.put("M", 4);
                WEEK_DAYS_KEY.put("J", 5);
                WEEK_DAYS_KEY.put("V", 6);
                WEEK_DAYS_KEY.put("S", 7);
            }
            if (ExpedientExporterAuditoria.SYS_PROPERTIES == null) {
                SYS_PROPERTIES = new Properties();
                InputStream is = new FileInputStream(sysPropsPATH);
                SYS_PROPERTIES.load(is);
                is.close();
                configure();
            }
        }
    }

    private boolean allFinisehd() {
        boolean finished = true;
        for (int i = 0; i < threads.size(); i++) {
            finished = finished && threads.get(i).isFinished();
        }
        return finished;
    }

    private long calculaSiguienteEjecucion() {
        long retVal = 0l;
        Calendar currentDate = new GregorianCalendar();
        int currentDay = currentDate.get(Calendar.DAY_OF_WEEK);
        Calendar executionDate = new GregorianCalendar();
        executionDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(ExpedientExporterAuditoria.START_HOUR[0]));
        executionDate.set(Calendar.MINUTE, Integer.parseInt(ExpedientExporterAuditoria.START_HOUR[1]));
        executionDate.set(Calendar.SECOND, Integer.parseInt(ExpedientExporterAuditoria.START_HOUR[2]));
        /*
		 * Si el dia actual esta incluido en la programacion verificamos la
		 * hora.
		 */
        if (ExpedientExporterAuditoria.EXECUTION_DAYS.contains(currentDay)) {
            /*
			 * La hora actual es mayor a la hora planeada de ejecucion. Se
			 * calcula el dia siguiente
			 */
            if (currentDate.compareTo(executionDate) > 0) {
                executionDate = nexExecutionDay(executionDate);
                retVal = executionDate.getTimeInMillis() - currentDate.getTimeInMillis();
            } else if (currentDate.compareTo(executionDate) < 0) {
                /*
				 * La hora de ejecucion es mayor a la hora actual, se obtiene la
				 * diferencia y sera el numero de segundos a esperar.
				 */
                retVal = executionDate.getTimeInMillis() - currentDate.getTimeInMillis();
            } else
                retVal = 0;
            System.out.println("Siguiente ejecucion el : " + SDF_DATE.format(executionDate.getTime()));
        } else {
            executionDate = nexExecutionDay(executionDate);
            retVal = executionDate.getTimeInMillis() - currentDate.getTimeInMillis();
        }
        System.out.println(String.format("Tendran que esperar: %.2f minutos ", (retVal / 60000.00)));
        return retVal;
    }

    private void configure() throws Exception {
        if (StringUtils.isEmpty(ExpedientExporterAuditoria.SYS_PROPERTIES.getProperty("DAYS_EXECUTE")))
            throw new Exception("No se definio el atributo: DAYS_EXECUTE");
        String[] days = ExpedientExporterAuditoria.SYS_PROPERTIES.getProperty("DAYS_EXECUTE").split(",");
        ExpedientExporterAuditoria.EXECUTION_DAYS = new ArrayList<Integer>();
        for (int i = 0; i < days.length; i++) ExpedientExporterAuditoria.EXECUTION_DAYS.add(ExpedientExporterAuditoria.WEEK_DAYS_KEY.get(days[i]));
        ExpedientExporterAuditoria.START_HOUR = ExpedientExporterAuditoria.SYS_PROPERTIES.getProperty("START_HOUR").split(",");
        ExpedientExporterAuditoria.STOP_HOUR = ExpedientExporterAuditoria.SYS_PROPERTIES.getProperty("STOP_HOUR").split(",");
    }

    private void escribeLogs(List<ExportLog> exportLog, String nombre) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            String pathExport = System.getProperty("user.home") + File.separatorChar + nombre + "_" + CFDIUtils.getTodayFile() + ".csv";
            fw = new FileWriter(pathExport);
            bw = new BufferedWriter(fw);
            for (Iterator<ExportLog> i = exportLog.iterator(); i.hasNext(); ) {
                ExportLog renglon = i.next();
                bw.write(renglon.toCSV() + "\n");
            }
            bw.flush();
            bw.close();
        } catch (Exception e) {
            log.error("Problemas escribiendo el log del proceso: " + e, e);
        } finally {
            fw = null;
            bw = null;
            System.gc();
        }
    }

    public void execExport() throws Exception {
        try {
            while (true) {
                if (!esTest) {
                    Thread.sleep(calculaSiguienteEjecucion());
                }
                List<ExportLog> logGeneral = new ArrayList<ExportLog>();
                List<ExportLog> logDetallado = new ArrayList<ExportLog>();
                ExpedientExporterBusinessLogic eebl = new ExpedientExporterBusinessLogic();
                List<String> expedients = new LinkedList<String>(Arrays.asList(eebl.listaExportarAuditoria(true)));
                if (expedients == null || expedients.isEmpty())
                    break;
                threads = new ArrayList<ExpedientExporteThread>();
                String thName = "Proceso_";
                for (int i = 0; i < Integer.parseInt(ExpedientExporterAuditoria.SYS_PROPERTIES.getProperty("NUMBER_OF_THREADS")); i++) {
                    ExpedientExporteThread thAux = new ExpedientExporteThread(logDetallado, logGeneral, null, true, expedients, (thName + i));
                    threads.add(thAux);
                }
                /* Aqui se lanzan los procesos */
                boolean continuar = true;
                Calendar stop = new GregorianCalendar();
                stop.set(Calendar.HOUR_OF_DAY, Integer.parseInt(ExpedientExporterAuditoria.STOP_HOUR[0]));
                stop.set(Calendar.MINUTE, Integer.parseInt(ExpedientExporterAuditoria.STOP_HOUR[1]));
                stop.set(Calendar.SECOND, Integer.parseInt(ExpedientExporterAuditoria.STOP_HOUR[2]));
                if (stop.before(new GregorianCalendar())) {
                    // throw new
                    // Exception("La hora de termino es menor a la hora de inicio. Se termina");
                    stop.add(Calendar.DATE, 1);
                }
                while (continuar) {
                    System.out.println("Validando hora de detencion.");
                    if ((new GregorianCalendar()).compareTo(stop) >= 0 || allFinisehd()) {
                        System.out.println("Hora de termino alcanzada. Salir");
                        continuar = false;
                        ExpedientExporteThread.setContinueProc(false);
                        while (!allFinisehd()) {
                            log.info("Esperando que todos los procesos terminen para escribir el log.");
                            Thread.sleep(30l * 1000l);
                        }
                        escribeLogs(logGeneral, "logGeneral");
                        escribeLogs(logDetallado, "logDetallado");
                    } else {
                        System.out.println("Hora de termino NO alcanzada. Durmiendo. Se detendra el proceso el: " + SDF_DATE.format(stop.getTime()));
                        Thread.sleep(30000);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            ExpedientExporteThread.setContinueProc(false);
        }
        log.info("Proceso de migracion terminado completamente");
    }

    private Calendar nexExecutionDay(Calendar executionDate) {
        boolean encontrado = false;
        Calendar m = executionDate;
        for (int i = 0; i < 7 && !encontrado; i++) {
            m.add(Calendar.DATE, 1);
            int dow = m.get(Calendar.DAY_OF_WEEK);
            if (ExpedientExporterAuditoria.EXECUTION_DAYS.contains(dow))
                encontrado = true;
        }
        return m;
    }
}
