package com.syc.adquisiciones.businessLogic;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.syc.adquisiciones.manager.ProcessAgreementManager;
import com.syc.adquisiciones.util.Util;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoOperacion;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ProcessAgreement extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(ProcessAgreement.class);

    public boolean sendEmailAgreement() throws Exception {
        Connection conn = null;
        boolean resp = false;
        ProcessAgreementManager manager = null;
        // indica la hora a la que se realiza la
        int horaVerificacion = 0;
        // verificacion (en formato de 24 horas)
        Calendar hoy = Calendar.getInstance();
        int ncantidadDays = 0;
        ArrayList<List<String>> tabla = null;
        String cEjercicioAct = "2015";
        String cEmairCoordinator = "";
        String cEmairManager = "";
        String cEmairPurchasingDepartment = "";
        String cUECoordination = "";
        List<String> fila = null;
        Iterator<List<String>> itr = null;
        String subject = "";
        String cObservation = "";
        String token = "";
        int isEmailSend = 0;
        boolean isDebuger = false;
        String to = "";
        String token2 = "";
        StringBuilder bodymail = null;
        String cEmailManagerAdquisicion = "cromo@conafor.gob.mx";
        try {
            conn = com.syc.gestion.util.Util.getStandAloneConnection(true);
            manager = new ProcessAgreementManager();
            bodymail = new StringBuilder();
            // obtener el ejercicio actual
            cEjercicioAct = Util.obtieneEjercicioFiscalActivo(conn);
            // Validar si está habilitada la ejecución
            if (manager.isAvailableProcess(conn, "sendEmailAgreement")) {
                // validar fecha de ejecución
                horaVerificacion = manager.sendEmail(conn, "hourSendEmailAgreement");
                if (horaVerificacion == hoy.get(Calendar.HOUR_OF_DAY) || isDebuger) {
                    // Cantidad de días de anticipación para la validación de
                    // contratos que estan por finalizar su vigencia
                    ncantidadDays = manager.numbersDaysBeforeFinishingAgreement(conn);
                    // Obtener el listado de contratos que estan por terminar su
                    // vigencia con 60 días de ancipación.
                    tabla = manager.getListAgreements(conn, ncantidadDays);
                    if (tabla.isEmpty()) {
                        log.info("No hay contratos que venzan en 60 d\u00edas");
                    } else {
                        fila = new ArrayList<String>();
                        itr = tabla.iterator();
                        // Obtener email del jefe de adquisiciones.
                        cEmairPurchasingDepartment = manager.getEmailPurchasingDepartment(conn, cEjercicioAct);
                        while (itr.hasNext()) {
                            fila = itr.next();
                            // Obtener los emails del coordinador y Gerente
                            cUECoordination = manager.getCoordination(conn, fila.get(0));
                            cEmairCoordinator = manager.getEmailCoordinator(conn, cUECoordination, cEjercicioAct);
                            cEmairManager = manager.getEmailManager(conn, fila.get(0), cEjercicioAct);
                            if (null == cUECoordination || "null".equalsIgnoreCase(cUECoordination) || "".equalsIgnoreCase(cUECoordination)) {
                                cObservation = "No se encontro la unidad ejecutora de la coorninación para la unidad ejecutora " + fila.get(0) + " del contrato SAI '" + fila.get(1) + "'.";
                                token = "\r";
                            }
                            if ("".equalsIgnoreCase(cEmairCoordinator)) {
                                cObservation = cObservation + token + "No se encontro el email del Coordinador en el sistema SAI.";
                                token = "\r";
                            } else {
                                to = cEmairCoordinator;
                                token2 = ";";
                            }
                            if ("".equalsIgnoreCase(cEmairManager)) {
                                cObservation = cObservation + token + "No se encontro el email del Gerente para la unidad ejecutora \"" + fila.get(0) + " - " + fila.get(4) + "\" en el sistema SAI.";
                                token = "\r";
                            } else {
                                to = to + token2 + cEmairManager;
                                token2 = ";";
                            }
                            if ("".equalsIgnoreCase(cEmairPurchasingDepartment)) {
                                cObservation = cObservation + token + "No se encontro el email del jefe de adquisiciones en el sistema SAI.";
                            } else {
                                to = to + token2 + cEmairPurchasingDepartment;
                                token2 = ";";
                            }
                            if (isDebuger) {
                                cObservation = "Lista de correos " + cEmairManager + ";" + cEmairCoordinator + ";" + cEmairPurchasingDepartment + ";" + cEmailManagerAdquisicion;
                            }
                            // send email
                            subject = "Terminación de vigencia para el contrato SAI " + fila.get(1);
                            bodymail.append("<html>");
                            bodymail.append("<body>");
                            bodymail.append("<p>");
                            bodymail.append("<b> El contrato ").append("<u>").append(fila.get(2)).append(" \"").append(fila.get(3)).append("\" </u>");
                            bodymail.append(" ésta próximo a  concluir su vigencia, por lo que se le solicita atentamente revisar la necesidad de renovación, con el objeto de iniciar el trámite correspondiente.");
                            bodymail.append("</b></p><br>");
                            bodymail.append("<br>");
                            bodymail.append("<br>");
                            if (!"".equalsIgnoreCase(cObservation)) {
                                bodymail.append("<p><b>");
                                bodymail.append("<u> Nota. </u>").append(cObservation);
                                bodymail.append("</b></p><br>");
                            }
                            bodymail.append("<br>");
                            bodymail.append("<br>");
                            bodymail.append("Notificaciones Automaticas SAI \"Sistema de Administración Integral\"");
                            bodymail.append("<br>");
                            bodymail.append("</body>");
                            bodymail.append("</html>");
                            if (!"".equalsIgnoreCase(cEmairPurchasingDepartment) || !"".equalsIgnoreCase(cEmairManager) || !"".equalsIgnoreCase(cEmairCoordinator)) {
                                if (isDebuger) {
                                    AlarmaManager.procesaAlarmaCNF(conn, "", null, null, subject, cEmailManagerAdquisicion, "hfariasr@axtel.com.mx", null, bodymail.toString(), true);
                                } else {
                                    AlarmaManager.procesaAlarmaCNF(conn, "", null, null, subject, cEmairManager + ";" + cEmairCoordinator, cEmairPurchasingDepartment + ";" + cEmailManagerAdquisicion, null, bodymail.toString(), true);
                                }
                                isEmailSend = 1;
                            }
                            // Guardar bitacora de los contratos notificados.
                            manager.saveBinnacleSendEmailAgrements(fila.get(1), cObservation, cEmairPurchasingDepartment, cEmairManager, cEmairCoordinator, isEmailSend);
                            cObservation = "";
                            isEmailSend = 0;
                            fila = null;
                            fila = new ArrayList<String>();
                            if (bodymail.length() > 0)
                                bodymail.delete(0, bodymail.length());
                        }
                    }
                } else {
                    log.info("Object: {}", "No es la hora programada de envio de emails para el proceso de contratos que está por teminar su vigencia; hora programada de ejecución=" + horaVerificacion + ", Hora en que se verifica= " + hoy.get(Calendar.HOUR_OF_DAY));
                }
            } else {
                log.info("No está habilitado el servicio de envio de correos para el proceso de contratos que está por teminar su vigencia");
            }
            conn.commit();
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException e2) {
                log.error("Error occurred", "Error en el rollback: " + e2);
            }
            log.error("Error occurred", "Error: " + e);
        } finally {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
            conn = null;
            tabla = null;
            fila = null;
            itr = null;
            bodymail = null;
        }
        return resp;
    }

    public boolean sendEmailPAAASPresupuesto() throws Exception {
        Connection conn = null;
        boolean resp = false;
        ProcessAgreementManager manager = null;
        // indica la hora a la que se realiza la
        int horaVerificacion = 0;
        // verificacion (en formato de 24 horas)
        int diaVerificacion = 20;
        Calendar hoy = Calendar.getInstance();
        ArrayList<List<String>> tabla = null;
        StringBuilder cEjercicioAct = null;
        String cEjercicio = "2021";
        String cEmairManager = "";
        String cEmairPurchasingDepartment = "";
        String cEmailManagerAdquisicion = "cromo@conafor.gob.mx";
        List<String> fila = null;
        Iterator<List<String>> itr = null;
        String subject = "";
        StringBuilder cObservation = new StringBuilder();
        int isEmailSend = 0;
        boolean isDebuger = false;
        StringBuilder bodymail = null;
        File file = null;
        boolean esAmbienteDesarrollo = false;
        String urlReportes = null;
        try {
            // conn=getConnection( jniName );
            conn = com.syc.gestion.util.Util.getStandAloneConnection(true);
            urlReportes = com.syc.gestion.util.Util.urlFilePathReporte;
            manager = new ProcessAgreementManager();
            bodymail = new StringBuilder();
            cEjercicioAct = new StringBuilder();
            esAmbienteDesarrollo = "TRUE".equalsIgnoreCase(ConfiguraAplicativoManager.getSystemSetting(conn, "AMBIENTE_DESARROLLO"));
            // obtener el ejercicio actual
            cEjercicioAct.append(Util.obtieneEjercicioFiscalActivo(conn));
            if (esAmbienteDesarrollo) {
                cEjercicioAct.append("_desa");
            }
            cEjercicio = cEjercicioAct.toString();
            // Validar si está habilitada la ejecución
            if (manager.isAvailableProcess(conn, "sendEmailPAAASPresupuesto")) {
                // validar fecha de ejecución
                diaVerificacion = manager.sendEmail(conn, "daySendEmailPAAASPresupuesto");
                if (diaVerificacion == hoy.get(Calendar.DATE) || isDebuger) {
                    horaVerificacion = manager.sendEmail(conn, "hourSendEmailPAAASPresupuesto");
                    if (horaVerificacion == hoy.get(Calendar.HOUR_OF_DAY) || isDebuger) {
                        // Obtener email del jefe de adquisiciones.
                        cEmairPurchasingDepartment = manager.getEmailPurchasingDepartment(conn, cEjercicioAct.toString());
                        if ("".equalsIgnoreCase(cEmairPurchasingDepartment)) {
                            cObservation.append("No se encontro el email del jefe de adquisiciones en el sistema SAI.");
                        }
                        // Obtener la lista de unidades ejecutoras del
                        // presupuesto
                        tabla = manager.getListAreasPresupuestoPAAAS(conn, cEjercicioAct.toString());
                        if (tabla == null || tabla.isEmpty()) {
                            log.info("No hay diferencias del PAAAS vs Presupuesto");
                        } else {
                            fila = new ArrayList<String>();
                            itr = tabla.iterator();
                            subject = "REQUERIMIENTO DEL PROGRAMA ANUAL DE ADQUISICIONES";
                            while (itr.hasNext()) {
                                fila = itr.next();
                                bodymail.append("<html>");
                                bodymail.append("<body>");
                                bodymail.append("<p style=\"text-align: justify;\">");
                                bodymail.append("<b>");
                                bodymail.append("C." + fila.get(9));
                                bodymail.append("<br>");
                                bodymail.append(fila.get(6));
                                bodymail.append("</b>");
                                bodymail.append("<br>");
                                bodymail.append("<br>");
                                bodymail.append("Se solicita de la manera mas atenta lo siguiente:");
                                bodymail.append("<br>");
                                bodymail.append("<br>");
                                bodymail.append("En cumplimiento a lo establecido en los artículos 20 y 21 de la Ley de Adquisiciones, ");
                                bodymail.append("Arrendamientos y Servicios del Sector Público así como 16 y 17 de su Reglamento, se le notifica que la Unidad a su cargo cuenta con presupuesto asignado ");
                                bodymail.append("sin programación en compras para el cumplimiento de los objetivos institucionales, por lo cual se le solicita atentamente lleve a cabo la revisión y en su caso la");
                                bodymail.append("actualización y/o modificación del Programa Anual de Adquisiciones, Arrendamientos y Servicios de la Entidad a través del Sistema de Administración Integral (SAI).");
                                bodymail.append("</p><br>");
                                bodymail.append("<br>");
                                bodymail.append("<br>");
                                bodymail.append("<br>");
                                bodymail.append("Notificaciones Automaticas SAI \"Sistema de Administración Integral\"");
                                bodymail.append("<br>");
                                bodymail.append("</body>");
                                bodymail.append("</html>");
                                cEmairManager = fila.get(5);
                                // Generar el archivo con la información
                                file = manager.generaReportePAAS_Presup(conn, urlReportes + "Plantilla_Presupuesto_PAAAS.xlsx", fila.get(4), cEjercicio, false);
                                // Enviar el correo
                                if (!"".equalsIgnoreCase(cEmairPurchasingDepartment) || !"".equalsIgnoreCase(fila.get(5))) {
                                    if (isDebuger) {
                                        cEmairManager = "cromo@conafor.gob.mx";
                                        cEmairPurchasingDepartment = "hfariasr@axtel.com.mx";
                                        cEmailManagerAdquisicion = "hfariasr@alestra.com.mx";
                                    }
                                    AlarmaManager.procesaAlarmaAttachmentCNF(conn, null, null, null, subject, cEmairManager + ";", cEmairPurchasingDepartment + ";" + cEmailManagerAdquisicion + ";", null, bodymail.toString(), file, true);
                                    isEmailSend = 1;
                                }
                                if (!file.delete())
                                    file.deleteOnExit();
                                file = null;
                                // Guardar bitacora
                                manager.saveBinnacleSendEmailPAAASPresup(fila.get(4), cObservation.toString(), cEmairPurchasingDepartment, cEmairManager, isEmailSend);
                                if (cObservation.length() > 0)
                                    cObservation.delete(0, cObservation.length());
                                isEmailSend = 0;
                                fila = null;
                                fila = new ArrayList<String>();
                                if (bodymail.length() > 0)
                                    bodymail.delete(0, bodymail.length());
                                if (isDebuger)
                                    break;
                            }
                        }
                    } else {
                        log.info("Object: {}", "No es la hora programada de envio de emails para el proceso de PAAAS vs Presupuesto; hora programada de ejecución=" + horaVerificacion + ", Hora en que se verifica= " + hoy.get(Calendar.HOUR_OF_DAY));
                    }
                } else {
                    log.info("No es el día 20 de cada mes; día de ejecución=");
                }
            } else {
                log.info("No está habilitado el servicio de envio de correos para el proceso de PAAAS vs Presupuesto.");
            }
            conn.commit();
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException e2) {
                log.error("Error occurred", "Error en el rollback: " + e2);
            }
            log.error("Error occurred", "Error: " + e);
        } finally {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
            conn = null;
            tabla = null;
            fila = null;
            itr = null;
            bodymail = null;
            cObservation = null;
            urlReportes = null;
        }
        return resp;
    }

    public void borraPAAASPresupuestoCero() throws Exception {
        /*
		 * Borra el paaas disponible cuando el presupuesto modificado de una
		 * partida es cero Se ejecuta cada 20 de cada mes a las 3 de la mañana.
		 */
        Connection conn = null;
        ProcessAgreementManager manager = null;
        ArrayList<List<String>> tabla = null;
        ArrayList<List<String>> tablaPartidas = null;
        StringBuilder cEjercicioAct = null;
        List<String> fila = null;
        List<String> filaPartidas = null;
        Iterator<List<String>> itr = null;
        Iterator<List<String>> itrPartidas = null;
        try {
            conn = com.syc.gestion.util.Util.getStandAloneConnection(true);
            manager = new ProcessAgreementManager();
            cEjercicioAct = new StringBuilder();
            cEjercicioAct.append(Util.obtieneEjercicioFiscalActivo(conn));
            // Validar si hay paaas que borrar de las unidades ejecutoras
            tabla = manager.getListAreasBorrarPAAAS(conn, cEjercicioAct.toString());
            if (tabla == null || tabla.isEmpty()) {
                log.info("No hay diferencias del PAAAS vs Presupuesto");
            } else {
                fila = new ArrayList<String>();
                itr = tabla.iterator();
                while (itr.hasNext()) {
                    fila = (List<java.lang.String>) itr.next();
                    // Obtener las partidas a borrar del paas
                    tablaPartidas = manager.getListAreasPAAASPartidas(conn, fila.get(4));
                    if (tablaPartidas == null || tablaPartidas.isEmpty()) {
                        log.info("Object: {}", "No hay partidas con disponibilidad en el PAAAS para borrar de la unidade ejecutora : " + fila.get(4));
                    } else {
                        filaPartidas = new ArrayList<String>();
                        itrPartidas = tablaPartidas.iterator();
                        while (itrPartidas.hasNext()) {
                            filaPartidas = (List<java.lang.String>) itrPartidas.next();
                            // Eliminar el paas disponible del presupuesto
                            // modificado en 0
                            manager.borraCucopPAAASDisponible(conn, fila.get(4), filaPartidas.get(5));
                            // Guardar bitacora
                            manager.saveBinnacleBorraPAAAS(conn, fila.get(4), filaPartidas.get(5), "Proceso automático para el borrado de PAAAS.");
                        }
                    }
                }
            }
            conn.commit();
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException e2) {
                log.error("Error occurred", "Error en el rollback: " + e2);
            }
            log.error("Error occurred", "Error: " + e);
        } finally {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
            conn = null;
            manager = null;
            tabla = null;
            fila = null;
            itr = null;
            filaPartidas = null;
        }
    }

    public void sendEmailRequisicionesFirmaFIEL() throws Exception {
        // Proceso de recordatorio de requis que no fueron firmadas
        Connection conn = null;
        ProcessAgreementManager manager = null;
        HashMap<Integer, ArrayList<List<String>>> infoRequis = null;
        ArrayList<List<String>> table = null;
        String afterDays = "5";
        StringBuilder bodymail = null;
        String subject = null;
        Iterator<List<String>> itr = null;
        List<String> fila = null;
        int i = 0;
        String urlAutorizacion = null;
        try {
            conn = com.syc.gestion.util.Util.getStandAloneConnection(true);
            urlAutorizacion = ConfiguraAplicativoManager.getSystemSetting(conn, "URL_SAI") + "/egresos/AutRequisicion";
            manager = new ProcessAgreementManager();
            // String urlAutorizacion = cabl.getSystemSetting("URL_SAI") +
            // "/egresos/AutRequisicion";
            // Obtención de requisiciones en estatus espera de firma electronica
            afterDays = Util.getSystemSetting(conn, "After_Days_Requi");
            infoRequis = manager.getListRequisPendientesFirma(conn, Integer.parseInt(afterDays));
            // Envio de correo
            bodymail = new StringBuilder();
            subject = "REQUISICIONES PENDIENTES DE FIRMA ELECTRÓNICA ";
            if (infoRequis != null & !infoRequis.isEmpty()) {
                for (int clave : infoRequis.keySet()) {
                    i = 0;
                    log.info("Object: {}", "Se enviara correo al empleado " + clave);
                    table = infoRequis.get(clave);
                    itr = table.iterator();
                    bodymail.append(" <html>");
                    bodymail.append(" \n\t<head>");
                    bodymail.append(" \n\t<meta charset=\"UTF-8\">");
                    bodymail.append(" \n\t<style type=\"text/css\">");
                    bodymail.append(" \n\tbody {");
                    bodymail.append(" \n\tfont-family: verdana, arial, sans-serif;");
                    bodymail.append(" \n\tfont-size: 12px;");
                    bodymail.append(" \n\t }");
                    bodymail.append(" \n\ttable {");
                    bodymail.append(" \n\tfont-size: 12px;");
                    bodymail.append(" \n\tcolor: #333333;");
                    bodymail.append(" \n\tborder-width: 1px;");
                    bodymail.append(" \n\tborder-color: #666666;");
                    bodymail.append(" \n\tborder-collapse: collapse;");
                    bodymail.append(" \n\t }");
                    bodymail.append(" \n\t table th {");
                    bodymail.append(" \n\t border-width: 1px;");
                    bodymail.append(" \n\t padding: 8px;");
                    bodymail.append(" \n\t border-style: solid;");
                    bodymail.append(" \n\t border-color: #666666;");
                    bodymail.append(" \n\tbackground-color: #dedede;");
                    bodymail.append(" \n\t }");
                    bodymail.append(" \n\t table td {");
                    bodymail.append(" \n\t border-width: 1px;");
                    bodymail.append(" \n\t padding: 8px;");
                    bodymail.append(" \n\t border-style: solid;");
                    bodymail.append(" \n\t border-color: #666666;");
                    bodymail.append(" \n\t background-color: #ffffff;");
                    bodymail.append(" \n\t }");
                    bodymail.append(" \n\t </style>");
                    bodymail.append(" \n\t</head>");
                    bodymail.append(" \n\t <body> ");
                    bodymail.append(" \n\t <form id=\"FormRequis\" name=\"FormRequis\" >");
                    bodymail.append("\n\t <b>");
                    while (itr.hasNext()) {
                        fila = itr.next();
                        if (i == 0) {
                            bodymail.append("\n\t C." + fila.get(7));
                            bodymail.append("\n\t <br>");
                            bodymail.append(fila.get(8));
                            bodymail.append("\n\t </b> ");
                            bodymail.append("\n\t <br> ");
                            bodymail.append("\n\t <br> ");
                            bodymail.append("\n\t <p> ");
                            bodymail.append("De la manera mas atenta se solicita firmar lo siguiente: ");
                            bodymail.append("\n\t </p> ");
                            bodymail.append("\n\t <br> ");
                            bodymail.append("\n\t <br> ");
                            bodymail.append("\n\t <table > ");
                            bodymail.append("\n\t <thead> ");
                            bodymail.append(" \n\t <tr>");
                            bodymail.append(" \n\t <th>Requisición</th>");
                            bodymail.append(" \n\t <th>Partida presupuestal</th>");
                            bodymail.append(" \n\t <th>Descripción</th>");
                            bodymail.append(" \n\t <th>Para Autorizar/Rechazar</th>");
                            bodymail.append(" \n\t </tr>");
                            bodymail.append(" \n\t </thead>");
                            bodymail.append(" \n\t <tbody>");
                        }
                        bodymail.append(" \n\t <tr>");
                        for (int j = 0; j < 3; j++) {
                            bodymail.append(" \n\t <td>");
                            bodymail.append(fila.get(j));
                            bodymail.append(" \n\t </td>");
                        }
                        bodymail.append(" \n\t <td>");
                        bodymail.append("Clic <a href=\"");
                        bodymail.append(urlAutorizacion);
                        bodymail.append(Util.generaAccessoAutToken(Integer.parseInt(fila.get(5)), "APARTADO", Integer.parseInt(fila.get(9))));
                        bodymail.append(" \" > aquí.</a>");
                        bodymail.append(" \n\t </td> ");
                        bodymail.append(" \n\t </tr> ");
                        i++;
                    }
                    bodymail.append(" \n\t </tbody>");
                    bodymail.append(" \n\t </table> ");
                    bodymail.append(" \n\t <br /> ");
                    bodymail.append(" \n\t <br /> ");
                    bodymail.append(" \n\t <p> ");
                    bodymail.append(" Notificaciones Automaticas<br />Sistema de Administración Integral<br /> ");
                    bodymail.append(" \n\t </p> ");
                    bodymail.append(" \n\t </form> ");
                    bodymail.append(" \n\t</body> ");
                    bodymail.append(" \n\t</html> ");
                    // enviar correo
                    AlarmaManager.procesaAlarmaCNF(conn, "", null, null, subject, fila.get(6), bodymail.toString());
                    bodymail.delete(0, bodymail.length() - 1);
                }
            }
            conn.commit();
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException e2) {
                log.error("Error occurred", "Error en el rollback: " + e2);
            }
            log.error("Error occurred", "Error: " + e);
        } finally {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
            if (infoRequis != null) {
                infoRequis.clear();
            }
            if (bodymail != null) {
                bodymail.delete(0, bodymail.length() - 1);
            }
            conn = null;
            manager = null;
            infoRequis = null;
            table = null;
            bodymail = null;
            subject = null;
            itr = null;
            fila = null;
            urlAutorizacion = null;
        }
    }
}
