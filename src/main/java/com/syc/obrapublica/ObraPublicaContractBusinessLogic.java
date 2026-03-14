package com.syc.obrapublica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletException;
import com.syc.contable.AccountingEngine;
import com.syc.contable.AccountingEngineException;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.CasoOperacionBusinessLogic;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.core.ObraPublicaContract;
import com.syc.obrapublica.core.ObraPublicaContractManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.ws.inventario.RespuestaWS;
import com.syc.ws.inventario.WSManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ObraPublicaContractBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(ObraPublicaContractBusinessLogic.class);

    private String jniName = null;

    private static final int ID_TIPO_CASO_OBRA = 10;

    private static final int ID_TIPO_CASO_COMP = 7;

    public boolean correoProduccion = true;

    public ObraPublicaContractBusinessLogic(String jniName) {
        super.init(jniName);
        this.jniName = jniName;
    }

    public byte SaveApartado(ObraPublicaContract opc, String[] listHeader, String[][] listDetail) throws GestionException {
        Connection conn = null;
        byte retVal;
        try {
            conn = getConnection();
            retVal = ObraPublicaContractManager.SaveApartado(conn, opc, listHeader, listDetail);
            ;
            if (retVal == 0) {
                if (opc.getApplyAccountingEngine() == 1) {
                    retVal = ObraPublicaContractManager.ApplyApartado(conn, opc);
                }
                if (retVal == 0) {
                    conn.commit();
                }
            }
        } catch (SQLException exc) {
            log.error("Actualizando Mensaje", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public byte SavePreCompromiso(ObraPublicaContract opc, String[] listHeader, String[][] listDetail) throws GestionException {
        Connection conn = null;
        byte retVal;
        try {
            conn = getConnection();
            retVal = ObraPublicaContractManager.SavePreCompromiso(conn, opc, listHeader, listDetail);
            if (retVal == 0) {
                if (opc.getApplyAccountingEngine() == 1) {
                    retVal = ObraPublicaContractManager.ApplyPreCompr(conn, opc);
                }
                if (retVal == 0) {
                    conn.commit();
                }
            }
        } catch (SQLException exc) {
            log.error("Actualizando Mensaje", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public byte SaveCompromiso(ObraPublicaContract opc, String[] listHeader, String[][] listDetail) throws GestionException {
        Connection conn = null;
        byte retVal;
        try {
            conn = getConnection();
            retVal = ObraPublicaContractManager.SaveCompromiso(conn, opc, listHeader, listDetail);
            if (retVal == 0) {
                if (opc.getApplyAccountingEngine() == 1) {
                    retVal = ObraPublicaContractManager.ApplyCompromiso(conn, opc);
                }
                if (retVal == 0) {
                    conn.commit();
                }
            }
        } catch (SQLException exc) {
            log.error("Actualizando Mensaje", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public String[] ApplyApartado(String folioSAI) throws GestionException {
        Connection conn = null;
        String[] retVal = null;
        String msg = null;
        boolean appSuccess = false;
        try {
            conn = getConnection();
            ObraPublicaContract opc = new ObraPublicaContract();
            opc.setFolioSAI(folioSAI);
            ObraPublicaContractManager.SelectIdApartadoHeaderAndEventos(conn, opc);
            if (opc == null || opc.getIdApartadoHeader() <= 0)
                throw new Exception("No se encontro el documento de Obra Publica con el folio: " + folioSAI);
            else {
                AccountingEngine accEng = new AccountingEngine();
                appSuccess = accEng.makeAccountingApplication(conn, "APARTADO_OPC", String.valueOf(opc.getIdApartadoHeader()), "tObraPublicaApartadoEncabezado", "tObraPublicaApartadoDetalle", "nFolioOPAHeader");
                ObraPublicaContractManager.SetStatusApartado(conn, opc.getIdApartadoHeader(), (byte) 1);
                if (appSuccess) {
                    msg = "DOCUMENTO DE OBRA PUBLICA [APARTADO]: <i>" + folioSAI + "</i> APLICADO CONTABLEMENTE.";
                } else {
                    msg = "NO SE APLICO EL DOCUMENTO [APARTADO]: <i>" + folioSAI + "</i>";
                }
                conn.commit();
            }
        } catch (Exception exc) {
            log.error("Error aplicando contablemente apartado obra publica[" + folioSAI + "] " + exc, exc);
            msg = exc.toString();
            appSuccess = false;
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e) {
                    log.error("No fue posible aplicar rollback " + e, e);
                }
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        retVal = new String[] { String.valueOf(appSuccess), msg };
        return retVal;
    }

    public String[] ApplyPreCompr(String folioSAI) throws GestionException {
        Connection conn = null;
        String[] retVal = null;
        String msg = "";
        boolean appSuccess = false;
        try {
            conn = getConnection();
            ObraPublicaContract opc = new ObraPublicaContract();
            opc.setFolioSAI(folioSAI);
            ObraPublicaContractManager.SelectIdPreCompromisoHeaderAndEventos(conn, opc);
            if (opc == null || opc.getIdPreCompromisoHeader() <= 0)
                throw new Exception("No se encontro el documento de Obra Publica con el folio: " + folioSAI);
            AccountingEngine accEng = new AccountingEngine();
            appSuccess = accEng.makeAccountingApplication(conn, "PRECOM_OPC", String.valueOf(opc.getIdPreCompromisoHeader()), "tObraPublicaPreCompromisoEncabezado", "tObraPublicaPreCompromisoDetalle", "nFolioOPPreComHeader");
            ObraPublicaContractManager.SetStatusPreCompromiso(conn, opc.getIdPreCompromisoHeader(), (byte) 2);
            if (appSuccess) {
                msg = "DOCUMENTO DE OBRA PUBLICA[PRECOMPROMISO]: " + folioSAI + " APLICADO CONTABLEMENTE";
            } else {
                msg = "NO SE APLICO EL DOCUMENTO [PRECOMPROMISO]: <i>" + folioSAI + "</i>";
            }
            conn.commit();
        } catch (Exception exc) {
            appSuccess = false;
            log.error("Error aplicando contablemente precompromiso obra publica[" + folioSAI + "] " + exc, exc);
            msg = exc.toString();
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e) {
                    log.error("No fue posible aplicar rollback " + e, e);
                }
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        retVal = new String[] { String.valueOf(appSuccess), msg };
        return retVal;
    }

    public String[] ApplyCompromiso(String folioSAI, String aEjercicioFiscal, String folioGenerator, Usuario u) throws GestionException {
        Connection conn = null;
        String[] retVal = null;
        boolean appSuccess = false;
        String msg = "";
        try {
            ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(jniName);
            boolean esSAIAlterno = "true".equalsIgnoreCase(cabl.getSystemSetting("SAI_AMBIENTAL")) || "true".equalsIgnoreCase(cabl.getSystemSetting("SAI_FONDEN")) || "true".equalsIgnoreCase(cabl.getSystemSetting("AMBIENTE_DESARROLLO"));
            conn = getConnection();
            ObraPublicaContract opc = new ObraPublicaContract();
            opc.setFolioSAI(folioSAI);
            ObraPublicaContractManager.SelectIdCompromisoHeaderAndEventos(conn, opc);
            int esFonden = ObraPublicaManager.esFONDEN(conn, folioSAI);
            if (opc == null || opc.getIdCompromisoHeader() <= 0) {
                throw new Exception("No se encontro el documento de Obra Publica con el folio: " + folioSAI);
            }
            AccountingEngine accEng = new AccountingEngine();
            appSuccess = accEng.makeAccountingApplication(conn, "COMP_OPC", String.valueOf(opc.getIdCompromisoHeader()), "tObraPublicaCompromisoEncabezado", "tObraPublicaCompromisoDetalle", "nFolioOPComHeader");
            ObraPublicaContractManager.SetStatusCompromiso(conn, opc.getIdCompromisoHeader(), (byte) 7);
            if (appSuccess) {
                /*
				 * VGC.20190102 Se llama el servicio web de registro de nueva
				 * obra. Si la respuesta es < 0 quieres decir que el WS esta
				 * apagado por lo que no se valida el resultado. De otro modo se
				 * valida que el resultado sea "success" para guardar el ID de
				 * obra generado en el sistema de inmuebles. Si el resultado es
				 * "Error" se lanza la excepcion con el codigo y el mensaje
				 * recibido y se realiza rollback.
				 */
                //if(!esSAIAlterno){
                if (esFonden != 1 && !esSAIAlterno) {
                    //ARLA 08102021 si el contrato esta marcado con FONDEN no notifica al sistema de inmuebles
                    boolean activaWS = "true".equalsIgnoreCase(cabl.getSystemSetting("WS_OBRAPUBLICA", "Activa_WS_ObraPublica"));
                    RespuestaWS respuesta = null;
                    if (activaWS)
                        respuesta = WSManager.generatePublicWork(conn, String.valueOf(opc.getIdCompromisoHeader()), u);
                    if (activaWS && respuesta.getCode() >= 0) {
                        if (WSManager.EXITO.equalsIgnoreCase(respuesta.getEstatus()) && respuesta.getIdRePublicWork() > 0) {
                            ObraPublicaContractManager.actualizaInformacionInmuebles(conn, folioSAI, respuesta.getIdRePublicWork());
                        } else {
                            throw new Exception("Error mientras se notificaba la obra publica a inmuebles. Codigo: [" + respuesta.getCode() + "] , IdRePublicWork: [" + respuesta.getIdRePublicWork() + " ]");
                        }
                    } else {
                        if (respuesta == null || respuesta.getCode() < 0) {
                            throw new Exception("Error mientras se notificaba la obra publica a inmuebles");
                        }
                    }
                }
                Caso cObra = null, cCompromiso = null;
                FolioGeneratorInterface fg = null;
                try {
                    ClassLoader cl = getClass().getClassLoader();
                    Class<?> clase = cl.loadClass(folioGenerator);
                    fg = (FolioGeneratorInterface) clase.newInstance();
                } catch (ClassNotFoundException exc) {
                    log.error("Generador de folios", exc);
                    throw new ServletException(exc);
                } catch (InstantiationException exc) {
                    log.error("Generador de folios", exc);
                    throw new ServletException(exc);
                } catch (IllegalAccessException exc) {
                    log.error("Generador de folios", exc);
                    throw new ServletException(exc);
                }
                cObra = generaCaso(u, ID_TIPO_CASO_OBRA, fg, "VENTANILLA_CONTRATO");
                String idCasoContratoStr = cObra.getFolio().substring(cObra.getFolio().lastIndexOf('-') + 1);
                String folioCasoContrato = cObra.getFolio();
                cCompromiso = generaCasoCompromiso(u, fg);
                int idCasoCompromisoStr = Integer.parseInt(cCompromiso.getFolio().substring(cCompromiso.getFolio().lastIndexOf('-') + 1));
                int nMes = GregorianCalendar.getInstance().get(Calendar.MONTH) + 1;
                String caNoCompromiso = generateCaNoCompromiso(u.getPropiedad("CCENTROCONTABLE").getValor(), aEjercicioFiscal);
                ObraPublicaContractManager.generaInformacionContrato(conn, folioSAI, Integer.parseInt(idCasoContratoStr));
                ObraPublicaContractManager.generaInformacionCompromiso(conn, folioSAI, idCasoCompromisoStr, nMes, caNoCompromiso);
                String etiqutaSAI = "sai_" + (Integer.parseInt(aEjercicioFiscal) - 1) + "..";
                if (esSAIAlterno) {
                    /*Ya no se aplica en automatico. Se guardan en la aplicacion y la respuesta de SICOP lo debe aplicar.*/
                    appSuccess = appSuccess && accEng.makeAccountingApplication(conn, "COMPROMISO", String.valueOf(idCasoCompromisoStr), "tCompromisoEncabezado", "tCompromisoDetalle", "nFolioCompromiso");
                    etiqutaSAI = "sai_" + (Integer.parseInt(aEjercicioFiscal) - 1) + "_desa..";
                } else {
                    if (ObraPublicaContractManager.esContratoPluriEjercicioAnt(conn, opc.getcIdContrato())) {
                        //es plurianual del ejercicio anterior
                        //Cargar el anticipo no amortizado de contratos plurianuales de ejercicios anteriosres
                        ObraPublicaContractManager.addAnticipoObraNoamortizado(conn, opc.getcIdContrato(), etiqutaSAI);
                    }
                    //validar el recurso
                    if (!ObraPublicaContractManager.traeRecursoFiscal(conn, folioSAI)) {
                        appSuccess = appSuccess && accEng.makeAccountingApplication(conn, "COMPROMISO", String.valueOf(idCasoCompromisoStr), "tCompromisoEncabezado", "tCompromisoDetalle", "nFolioCompromiso");
                    }
                }
                msg = "DOCUMENTO DE OBRA PUBLICA[COMPROMISO]: " + folioSAI + " APLICADO CONTABLEMENTE";
                msg += "<br><br>Se genero exitosamente el tramite de Contrato Obra. El folio del tramite es: <i>" + folioCasoContrato + "</i>";
                msg += "Se genero exitosamente el tramite de Compromiso. El folio del tramite es: <i>" + cCompromiso.getFolio() + "</i>";
            } else {
                appSuccess = false;
                msg = "ATENCION! No se aplico contablemente el documento " + folioSAI;
            }
            conn.commit();
        } catch (Exception exc) {
            log.error("Error aplicando contablemente compromiso obra publica[" + folioSAI + "] " + exc, exc);
            appSuccess = false;
            msg = exc.toString();
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e) {
                    log.error("No fue posible aplicar rollback " + e, e);
                }
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        retVal = new String[] { String.valueOf(appSuccess), msg };
        return retVal;
    }

    public String[] applyConvenioModificatorioPrecompromiso(String folioSAI) throws Exception {
        Connection conn = null;
        String[] retVal = null;
        boolean appSuccess = true;
        String msg = "";
        try {
            conn = getConnection();
            ObraPublicaBusinessLogic opbl = new ObraPublicaBusinessLogic();
            Map<String, String> m = opbl.loadCMInfoMap(Integer.parseInt(folioSAI));
            try {
                if (cmTieneIncrementoMonto(folioSAI)) {
                    AccountingEngine accEng = new AccountingEngine();
                    if (m.get("nFolioOPConvHeaderCancel".toUpperCase()) != null) {
                        String toCancel = m.get("nFolioOPConvHeaderCancel".toUpperCase());
                        appSuccess = accEng.cancelAccountingApplication(conn, "CONVMOD_PRECOM", toCancel, "tObraPublicaConvModifEncabezado", "tObraPublicaConvModifDetalle", "nFolioOPConvHeader");
                    }
                    appSuccess = appSuccess && accEng.makeAccountingApplication(conn, "CONVMOD_PRECOM", folioSAI, "tObraPublicaConvModifEncabezado", "tObraPublicaConvModifDetalle", "nFolioOPConvHeader");
                    if (appSuccess) {
                        msg = "DOCUMENTO DE CONVENIO MODIFICATORIO: " + folioSAI + " APLICADO CONTABLEMENTE";
                    } else
                        msg = "ATENCION! NO SE APLICO EL PRECOMPROMISO: " + folioSAI;
                } else {
                    ObraPublicaManager.setConvenioModificatorioAplicado(conn, Integer.parseInt(folioSAI));
                }
                conn.commit();
            } catch (AccountingEngineException e) {
                log.error(e.getMessage(), e);
                appSuccess = false;
                msg = e.getMessage();
                conn.rollback();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                appSuccess = false;
                msg = e.getMessage();
                conn.rollback();
            }
        } catch (SQLException exc) {
            log.error("Actualizando Mensaje", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        retVal = new String[] { String.valueOf(appSuccess), msg };
        return retVal;
    }

    // IRD 20131121 RO-0009
    public String[] applyPagoPasivoPrecompromiso(String folioSAI) throws Exception {
        Connection conn = null;
        String[] retVal = null;
        boolean appSuccess = true;
        String msg = "";
        try {
            conn = getConnection();
            ObraPublicaBusinessLogic opbl = new ObraPublicaBusinessLogic();
            Map<String, String> m = opbl.loadCMInfoMap(Integer.parseInt(folioSAI));
            try {
                // if (cmTieneIncrementoMonto(folioSAI)) {
                AccountingEngine accEng = new AccountingEngine();
                /*
				 * if (m.get("nFolioOPPagPasHeader".toUpperCase()) != null) {
				 * String toCancel =
				 * m.get("nFolioOPPagPasHeader".toUpperCase()); appSuccess =
				 * accEng.cancelAccountingApplication(conn, "PAGOPASIVO_PRECOM",
				 * toCancel, "tObraPublicaPagoPasivoEncabezado",
				 * "tObraPublicaPagoPasivoDetalle", "nFolioOPPagPasivoHeader");
				 * }
				 */
                appSuccess = appSuccess && accEng.makeAccountingApplication(conn, "PAGOPASIVO_PRECOM", folioSAI, "tObraPublicaPagoPasivoEncabezado", "tObraPublicaPagoPasivoDetalle", "nFolioOPPagPasHeader");
                if (appSuccess) {
                    msg = "DOCUMENTO DE PAGO DE PASIVO: " + folioSAI + " APLICADO CONTABLEMENTE";
                } else
                    msg = "ATENCION! NO SE APLICO EL PRECOMPROMISO: " + folioSAI;
                // } else {
                // ObraPublicaManager.setConvenioModificatorioAplicado(conn,
                // Integer.parseInt(folioSAI));
                // }
                conn.commit();
            } catch (AccountingEngineException e) {
                log.error(e.getMessage(), e);
                appSuccess = false;
                msg = e.getMessage();
                conn.rollback();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                appSuccess = false;
                msg = e.getMessage();
                conn.rollback();
            }
        } catch (SQLException exc) {
            log.error("Actualizando Mensaje", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        retVal = new String[] { String.valueOf(appSuccess), msg };
        return retVal;
    }

    // IRD 20131121 RO-0010
    public String[] applyPlurianualPrecompromiso(String folioSAI) throws Exception {
        Connection conn = null;
        String[] retVal = null;
        boolean appSuccess = true;
        String msg = "";
        try {
            conn = getConnection();
            ObraPublicaBusinessLogic opbl = new ObraPublicaBusinessLogic();
            Map<String, String> m = opbl.loadCMInfoMap(Integer.parseInt(folioSAI));
            try {
                // if (cmTieneIncrementoMonto(folioSAI)) {
                AccountingEngine accEng = new AccountingEngine();
                /*
				 * if (m.get("nFolioOPPagPasHeader".toUpperCase()) != null) {
				 * String toCancel =
				 * m.get("nFolioOPPagPasHeader".toUpperCase()); appSuccess =
				 * accEng.cancelAccountingApplication(conn, "PAGOPASIVO_PRECOM",
				 * toCancel, "tObraPublicaPagoPasivoEncabezado",
				 * "tObraPublicaPagoPasivoDetalle", "nFolioOPPagPasivoHeader");
				 * }
				 */
                appSuccess = appSuccess && accEng.makeAccountingApplication(conn, "PLURIANUAL_PRECOM", folioSAI, "tObraPublicaPlurianualEncabezado", "tObraPublicaPlurianualDetalle", "nFolioOPPlurianualHeader");
                if (appSuccess) {
                    msg = "DOCUMENTO DE PLURIANUAL: " + folioSAI + " APLICADO CONTABLEMENTE";
                } else
                    msg = "ATENCION! NO SE APLICO EL PRECOMPROMISO: " + folioSAI;
                // } else {
                // ObraPublicaManager.setConvenioModificatorioAplicado(conn,
                // Integer.parseInt(folioSAI));
                // }
                conn.commit();
            } catch (AccountingEngineException e) {
                log.error(e.getMessage(), e);
                appSuccess = false;
                msg = e.getMessage();
                conn.rollback();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                appSuccess = false;
                msg = e.getMessage();
                conn.rollback();
            }
        } catch (SQLException exc) {
            log.error("Actualizando Mensaje", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        retVal = new String[] { String.valueOf(appSuccess), msg };
        return retVal;
    }

    public byte MoveToAuthorize(String folioSAI) throws GestionException {
        Connection conn = null;
        byte retVal;
        try {
            conn = getConnection();
            ObraPublicaContract opc = new ObraPublicaContract();
            opc.setFolioSAI(folioSAI);
            retVal = ObraPublicaContractManager.SelectIdPreCompromisoHeaderAndEventos(conn, opc);
            if (retVal == 0) {
                retVal = ObraPublicaContractManager.ApplyPreCompr(conn, opc);
                if (retVal == 0) {
                    retVal = ObraPublicaContractManager.SetStatusPreCompromiso(conn, opc.getIdPreCompromisoHeader(), (byte) 3);
                    if (retVal == 0)
                        conn.commit();
                }
            } else {
                if (retVal == -1) {
                    opc.setMessage("Error enc base de datos al aplicar el motor contable");
                } else if (retVal == -2)
                    opc.setMessage("El documento compromiso con el folio SAI no fue encontrado en la base de datos");
            }
        } catch (SQLException exc) {
            log.error("Actualizando Mensaje", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    // IRD RO-0003 Se método CancelContractMultiple para cancelaciones masivas
    // IRD 20140901 SE incluye cancelación de convenios modificatorios
    public String[][] CancelContractMultiple(String folioSAI, String motivoCancela) {
        String[] response = new String[2];
        byte retVal;
        Connection conn = null;
        String[][] mensajes = null;
        String[] fields = folioSAI.split("\\|");
        String folioCancelado = "";
        String tipoDocumento = "";
        if (fields.length > 0) {
            try {
                conn = getConnection();
                String token = "";
                mensajes = new String[fields.length + 1][2];
                mensajes[0][0] = "";
                mensajes[0][1] = "";
                int numFolio = 1;
                for (int i = 0; i < fields.length; i++) {
                    if (!"".equalsIgnoreCase(fields[i])) {
                        String folSai = fields[i].split(":")[0];
                        if (!"".equalsIgnoreCase(folSai)) {
                            mensajes[numFolio][0] = "";
                            mensajes[numFolio][1] = "";
                            String tipFolio = fields[i].split(":")[1];
                            if ("CONVENIO".equalsIgnoreCase(tipFolio))
                                tipoDocumento = "CONVENIO";
                            else if ("PROINPRO".equalsIgnoreCase(tipFolio))
                                tipoDocumento = "PROINPRO";
                            else
                                tipoDocumento = ObraPublicaContractManager.obtenTipoDocumentoOP(conn, folSai);
                            String[] respuesta = CancelContract(folSai, tipoDocumento, true);
                            if ("true".equalsIgnoreCase(respuesta[0])) {
                                mensajes[numFolio][0] = "true";
                                mensajes[numFolio][1] = "Documento " + folSai + " cancelado.";
                                folioCancelado += token + "'" + folSai + "'";
                                token = ",";
                            } else {
                                mensajes[numFolio][0] = "false";
                                mensajes[numFolio][1] = "No se pudo cancelar el documento " + folSai + ".";
                            }
                            numFolio++;
                        }
                    }
                }
                AutoCancelacionApartadoBusinessLogic pabl = new AutoCancelacionApartadoBusinessLogic(jniName);
                pabl.correoProduccion = this.correoProduccion;
                pabl.enviaAdvertenciaResponsableProximaCancelacion(folioCancelado, motivoCancela);
            } catch (Exception exc) {
                log.error("Actualizando Mensaje", exc);
                response[0] = "false";
                response[1] = "Error cancelando documento: " + exc;
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e) {
                        log.warn("Error occurred", "Error realizando rollback: " + e);
                    }
            } finally {
                try {
                    if (conn != null)
                        conn.close();
                } catch (SQLException exc) {
                    log.warn("Cerrando conexion a base de datos", exc);
                }
                conn = null;
            }
        }
        return mensajes;
    }

    public String[] CancelContract(String folioSAI, String tipoDocumento, boolean esMasiva) {
        Connection conn = null;
        String[] response = new String[2];
        byte retVal;
        try {
            conn = getConnection();
            // IRD 20140806 solo se hara comit cuando se terminen las
            // cancelaciones correspondientes de apartado, pre y compromiso
            ObraPublicaContract opc = new ObraPublicaContract();
            opc.setFolioSAI(folioSAI);
            retVal = ObraPublicaContractManager.GetSituacionContratoObraPublica(conn, opc);
            if ("5".equalsIgnoreCase(tipoDocumento) || "4".equalsIgnoreCase(tipoDocumento)) {
                // IRD
                // 20140806
                // son
                // Pago
                // pasivo
                // y
                // plurianuales
                retVal = ObraPublicaContractManager.SetDocApliApartado(conn, folioSAI, "C");
                if ("4".equalsIgnoreCase(tipoDocumento)) {
                    int retVal2 = 0;
                    retVal2 = ObraPublicaContractManager.SelectIdPagoPasivoHeaderAndEventos(conn, opc);
                    if (retVal2 == 0) {
                        if ("S".equalsIgnoreCase(opc.getcDocumentoHaplicado()))
                            retVal2 = ObraPublicaContractManager.CancelPagoPasivo(conn, opc);
                    }
                    // if (retVal2 == 0) {
                    retVal2 = ObraPublicaContractManager.SetDocApliPagoPasivo(conn, folioSAI, "C");
                    // }
                }
                if ("5".equalsIgnoreCase(tipoDocumento)) {
                    int retVal2 = 0;
                    retVal2 = ObraPublicaContractManager.SelectIdPlurianualHeaderAndEventos(conn, opc);
                    if (retVal2 == 0) {
                        if ("S".equalsIgnoreCase(opc.getcDocumentoHaplicado()))
                            retVal2 = ObraPublicaContractManager.CancelPlurianual(conn, opc);
                    }
                    // if (retVal2 == 0) {
                    retVal2 = ObraPublicaContractManager.SetDocApliPlurianual(conn, folioSAI, "C");
                    // }
                }
                if (esMasiva)
                    retVal = ObraPublicaContractManager.updateCancelacionMas(conn, folioSAI);
            } else if ("CONVENIO".equalsIgnoreCase(tipoDocumento) || "6".equalsIgnoreCase(tipoDocumento)) {
                // IRD
                // 20140901
                // se
                // incluye
                // cancelación
                // de
                // convenios
                int retConvMod = 0;
                retVal = -1;
                retConvMod = ObraPublicaContractManager.SelectIdConvModifHeaderAndEventos(conn, opc);
                if (retConvMod == 0) {
                    if ("S".equalsIgnoreCase(opc.getcDocumentoHaplicado()) && "S".equalsIgnoreCase(opc.getcConvenioEnCaptura()))
                        retConvMod = ObraPublicaContractManager.CancelConvModif(conn, opc);
                }
                if (retConvMod == 0) {
                    if ("S".equalsIgnoreCase(opc.getcConvenioEnCaptura()))
                        retVal = ObraPublicaContractManager.SetDocApliConvModif(conn, opc.getidConvModHeader(), "C");
                    if (retVal == 0) {
                        if (esMasiva)
                            retVal = ObraPublicaContractManager.updateCancelacionMas(conn, folioSAI);
                    }
                }
                if (// MLR Se agrega para
                // enviar a consulta
                // un convenio
                "6".equalsIgnoreCase(tipoDocumento)) {
                    retVal = ObraPublicaContractManager.updateCancelacionConv(conn, folioSAI);
                    System.out.println("Entra retval2");
                }
            } else if ("PROINPRO".equalsIgnoreCase(tipoDocumento)) {
                // IRD
                // 20140912
                // se
                // incluye
                // cancelación
                // de
                // precompromisos
                // desde
                // PROINPRO
                int retProin = 0;
                retVal = -1;
                retProin = ObraPublicaContractManager.SelectIdProinproHeaderAndEventos(conn, opc);
                if (retProin == 0) {
                    retProin = ObraPublicaContractManager.CancelProinpro(conn, opc);
                }
                if (retProin == 0) {
                    retVal = ObraPublicaContractManager.SetDocApliProinpro(conn, opc.getidProinproHeader(), "C");
                }
            } else {
                if (retVal == 0) {
                    // Fue autorizado por ventanilla
                    if (opc.getWhereIsContract() == 7) {
                        retVal = ObraPublicaContractManager.SelectIdCompromisoHeaderAndEventos(conn, opc);
                        if (retVal == 0) {
                            retVal = ObraPublicaContractManager.CancelCompromiso(conn, opc);
                            if (retVal == 0) {
                                retVal = ObraPublicaContractManager.SetStatusCompromiso(conn, opc.getIdCompromisoHeader(), (byte) 9);
                            }
                        }
                    }
                    if ((opc.getWhereIsContract() == 10) && (retVal == 0)) {
                        // MLR
                        // 20140620
                        // Se
                        // marca
                        // como
                        // 'C'
                        // la
                        // captura
                        // de
                        // compromiso
                        // (cancelacion
                        // masiva)
                        retVal = ObraPublicaContractManager.SelectIdCompromisoSinAceptarVentanilla(conn, opc);
                        if (retVal == 0) {
                            retVal = ObraPublicaContractManager.SetDocCancelComp(conn, folioSAI, "C");
                            if (retVal == 0) {
                                // conn.commit();
                                /*
								 * if (retVal == 0) conn.commit();
								 */
                            }
                        }
                    }
                    if ((opc.getWhereIsContract() > 3) && retVal == 0) {
                        retVal = ObraPublicaContractManager.SelectIdPreCompromisoHeaderAndEventos(conn, opc);
                        if (retVal == 0) {
                            retVal = ObraPublicaContractManager.CancelPreCompromiso(conn, opc);
                            if (retVal == 0) {
                                retVal = ObraPublicaContractManager.SetStatusPreCompromiso(conn, opc.getIdPreCompromisoHeader(), (byte) 9);
                            }
                        }
                    }
                    if ((opc.getWhereIsContract() > 1) && (retVal == 0)) {
                        retVal = ObraPublicaContractManager.SelectIdApartadoHeaderAndEventos(conn, opc);
                        if (retVal == 0) {
                            retVal = ObraPublicaContractManager.CancelApartado(conn, opc);
                            if (retVal == 0) {
                                retVal = ObraPublicaContractManager.SetStatusApartado(conn, opc.getIdApartadoHeader(), (byte) 9);
                                /*
								 * if (retVal == 0) conn.commit();
								 */
                            }
                        }
                    }
                    if ((opc.getWhereIsContract() == 0) && (retVal == 0)) {
                        // IRD
                        // 20131018
                        // Se
                        // marca
                        // como
                        // 'C'
                        // si
                        // es
                        // que
                        // no
                        // esta
                        // aplicado
                        // el
                        // apartado
                        retVal = ObraPublicaContractManager.SelectIdApartadoHeaderAndEventos(conn, opc);
                        if (retVal == 0) {
                            retVal = ObraPublicaContractManager.SetDocApliApartado(conn, folioSAI, "C");
                            /*
							 * if (retVal == 0) conn.commit();
							 */
                        }
                    }
                    if (esMasiva)
                        retVal = ObraPublicaContractManager.updateCancelacionMas(conn, folioSAI);
                }
                /// if ret val 0
            }
            // if pasivo
            if (retVal == 0) {
                conn.commit();
                response[0] = "true";
                response[1] = "Documento " + folioSAI + " cancelado.";
            } else {
                conn.rollback();
                response[0] = "false";
                response[1] = "Error cancelando documento: ";
            }
        } catch (Exception exc) {
            log.error("Actualizando Mensaje", exc);
            response[0] = "false";
            response[1] = "Error cancelando documento: " + exc;
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e) {
                    log.warn("Error occurred", "Error realizando rollback: " + e);
                }
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return response;
    }

    public String[] CancelPasivo(String folioSAI, String tipoDocumento) {
        Connection conn = null;
        String[] response = new String[2];
        byte retVal;
        try {
            conn = getConnection();
            ObraPublicaContract opc = new ObraPublicaContract();
            opc.setFolioSAI(folioSAI);
            retVal = ObraPublicaContractManager.GetSituacionContratoObraPublica(conn, opc);
            int retVal2 = 0;
            retVal2 = ObraPublicaContractManager.SelectIdPagoPasivoHeaderAndEventos(conn, opc);
            if (retVal2 == 0) {
                if ("S".equalsIgnoreCase(opc.getcDocumentoHaplicado()))
                    retVal2 = ObraPublicaContractManager.CancelPagoPasivo(conn, opc);
                if (retVal2 == 0) {
                    retVal2 = ObraPublicaContractManager.SetDocApliPagoPasivo(conn, folioSAI, "");
                }
            }
            response[0] = "true";
            response[1] = "Documento " + folioSAI + " cancelado.";
            conn.commit();
        } catch (Exception exc) {
            log.error("Actualizando Mensaje", exc);
            response[0] = "false";
            response[1] = "Error cancelando documento: " + exc;
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e) {
                    log.warn("Error occurred", "Error realizando rollback: " + e);
                }
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return response;
    }

    public String[] CancelPlurianual(String folioSAI, String tipoDocumento) {
        Connection conn = null;
        String[] response = new String[2];
        byte retVal;
        try {
            conn = getConnection();
            ObraPublicaContract opc = new ObraPublicaContract();
            opc.setFolioSAI(folioSAI);
            retVal = ObraPublicaContractManager.GetSituacionContratoObraPublica(conn, opc);
            int retVal2 = 0;
            retVal2 = ObraPublicaContractManager.SelectIdPlurianualHeaderAndEventos(conn, opc);
            if (retVal2 == 0) {
                if ("S".equalsIgnoreCase(opc.getcDocumentoHaplicado()))
                    retVal2 = ObraPublicaContractManager.CancelPlurianual(conn, opc);
                if (retVal2 == 0) {
                    retVal2 = ObraPublicaContractManager.SetDocApliPlurianual(conn, folioSAI, "");
                }
            }
            response[0] = "true";
            response[1] = "Documento " + folioSAI + " cancelado.";
            conn.commit();
        } catch (Exception exc) {
            log.error("Actualizando Mensaje", exc);
            response[0] = "false";
            response[1] = "Error cancelando documento: " + exc;
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e) {
                    log.warn("Error occurred", "Error realizando rollback: " + e);
                }
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return response;
    }

    public int generaInformacionCompromiso(String folioSAI, int idCasoCompromisoStr, int nMes, String caNoCompromiso) throws GestionException {
        Connection conn = null;
        try {
            conn = getConnection();
            int result = ObraPublicaContractManager.generaInformacionCompromiso(conn, folioSAI, idCasoCompromisoStr, nMes, caNoCompromiso);
            conn.commit();
            return result;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("No se pudo realizar el rollback debido al siguiente error:" + e2, e2);
                }
            throw new GestionException(e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e) {
                    log.warn("No fue posible cerrar la conexion a la base de datos debido al siguiente error: " + e, e);
                }
            conn = null;
        }
    }

    public int generaInformacionContrato(String folioSAI, int idCaso) throws GestionException {
        Connection conn = null;
        try {
            conn = getConnection();
            int result = ObraPublicaContractManager.generaInformacionContrato(conn, folioSAI, idCaso);
            conn.commit();
            return result;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("No se pudo realizar el rollback debido al siguiente error:" + e2, e2);
                }
            throw new GestionException(e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e) {
                    log.warn("No fue posible cerrar la conexion a la base de datos debido al siguiente error: " + e, e);
                }
            conn = null;
        }
    }

    // IRD 20131121 RO-0009
    public int copiaInformacionContratoAnioAnterior(String folioSAIAnterior, String FolioSAI) throws GestionException {
        Connection conn = null;
        try {
            conn = getConnection();
            int result = ObraPublicaContractManager.copiaInformacionContratoAnioAnterior(conn, folioSAIAnterior, FolioSAI);
            conn.commit();
            return result;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("No se pudo realizar el rollback debido al siguiente error:" + e2, e2);
                }
            throw new GestionException(e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e) {
                    log.warn("No fue posible cerrar la conexion a la base de datos debido al siguiente error: " + e, e);
                }
            conn = null;
        }
    }

    // MLR 20131204 RO-0010
    public int copiaInformacionContratoAnioAnteriorPlurianual(String folioSAIAnterior, String FolioSAI) throws GestionException {
        Connection conn = null;
        try {
            conn = getConnection();
            int result = ObraPublicaContractManager.copiaInformacionContratoAnioAnteriorPl(conn, folioSAIAnterior, FolioSAI);
            conn.commit();
            return result;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("No se pudo realizar el rollback debido al siguiente error:" + e2, e2);
                }
            throw new GestionException(e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e) {
                    log.warn("No fue posible cerrar la conexion a la base de datos debido al siguiente error: " + e, e);
                }
            conn = null;
        }
    }

    public Caso generaConvenioMoficatorio(String uLogin, String folioSAI) throws GestionException {
        Connection conn = null;
        Caso c = null;
        try {
            conn = getConnection();
            c = new Caso();
            c.setFolio(folioSAI);
            c = CasoManager.select(conn, c);
            if (c != null) {
                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String fecha = sdf.format(date);
                Map<String, String> data = new HashMap<String, String>();
                data.put("FOLIO", c.getFolio());
                data.put("OPERADOR", uLogin);
                data.put("FECHA_DOCUMENTO", fecha);
                data.put("EJERCICIO_FISCAL", obtieneEjecicioFiscal());
                // data.put("USURIO", u);
                CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
                casoTx.avanzaCaso(c, uLogin, "", new String[] { "CAPTURA_OBRAPUBLICA" }, new String[] { "conv_modificatorio" }, data, "");
                c = casoTx.ejecutaCaso(c.getIdCaso(), -1, uLogin);
            } else
                throw new GestionException("No se encontro el tramite con folio: " + folioSAI);
            conn.commit();
            return c;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("No se pudo realizar el rollback debido al siguiente error:" + e2, e2);
                }
            throw new GestionException(e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e) {
                    log.warn("No fue posible cerrar la conexion a la base de datos debido al siguiente error: " + e, e);
                }
            conn = null;
        }
    }

    // IRD 20131121 RO-0009
    public Caso generaPagoPasivo(String uLogin, String folioSAI) throws GestionException {
        Connection conn = null;
        Caso c = null;
        try {
            conn = getConnection();
            c = new Caso();
            c.setFolio(folioSAI);
            c = CasoManager.select(conn, c);
            if (c != null) {
                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String fecha = sdf.format(date);
                Map<String, String> data = new HashMap<String, String>();
                data.put("FOLIO", c.getFolio());
                data.put("OPERADOR", uLogin);
                data.put("FECHA_DOCUMENTO", fecha);
                data.put("EJERCICIO_FISCAL", obtieneEjecicioFiscal());
                CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
                casoTx.avanzaCaso(c, uLogin, "", new String[] { "CAPTURA_OBRAPUBLICA" }, new String[] { "conv_modificatorio" }, data, "");
                c = casoTx.ejecutaCaso(c.getIdCaso(), -1, uLogin);
            } else
                throw new GestionException("No se encontro el tramite con folio: " + folioSAI);
            conn.commit();
            return c;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("No se pudo realizar el rollback debido al siguiente error:" + e2, e2);
                }
            throw new GestionException(e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e) {
                    log.warn("No fue posible cerrar la conexion a la base de datos debido al siguiente error: " + e, e);
                }
            conn = null;
        }
    }

    public String generaFolioContrato(String cUR, String tRecurso, String tAdjudicacion) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            String folio = ObraPublicaContractManager.generaFolioContratoOP(conn, cUR, tRecurso, tAdjudicacion);
            return folio;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas cerrando conexion a DB " + e2);
                }
        }
    }

    public String generaFolioConvenio(String cUR, String tRecurso, String tAdjudicacion) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            String folio = ObraPublicaContractManager.generaFolioConvenioOP(conn, cUR, tRecurso, tAdjudicacion);
            return folio;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas cerrando conexion a DB " + e2);
                }
        }
    }

    public boolean cmTieneIncrementoMonto(String folioSAI) throws Exception {
        Connection conn = null;
        boolean retVal = false;
        try {
            conn = getConnection();
            retVal = ObraPublicaManager.cmTieneIncrementoMonto(conn, folioSAI);
            return retVal;
        } finally {
            if (conn != null) {
                try {
                    CloseObject.closeObject(conn, false);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    public Caso generaCasoCapturaEstimacion(String uLogin, String folioSAI) throws GestionException {
        Connection conn = null;
        Caso c = null;
        try {
            conn = getConnection();
            c = new Caso();
            c.setFolio(folioSAI);
            c = CasoManager.select(conn, c);
            return c;
        } catch (Exception e) {
            throw new GestionException(e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e) {
                    log.warn("No fue posible cerrar la conexion a la base de datos debido al siguiente error: " + e, e);
                }
            conn = null;
        }
    }

    // IRD 20131121 RO-0009 Se cambia de private a public
    public Caso generaCaso(Usuario u, int idTCaso, FolioGeneratorInterface fg, String opResponsable) throws GestionException {
        CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
        Caso c = casoTx.IniciaCaso(u, idTCaso, fg);
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        // Variables del caso
        c.getCasoDato("FOLIO").setValor(c.getFolio());
        c.getCasoDato("OPERADOR").setValor(u.getLogin());
        c.getCasoDato("FECHA_DOCUMENTO").setValor(fecha);
        try {
            c.getCasoDato("EJERCICIO_FISCAL").setValor(obtieneEjecicioFiscal());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> m = new HashMap<String, String>();
        m.put("FOLIO", c.getFolio());
        m.put("OPERADOR", u.getLogin());
        m.put("FECHA_DOCUMENTO", fecha);
        try {
            m.put("EJERCICIO_FISCAL", obtieneEjecicioFiscal());
        } catch (Exception e) {
            e.printStackTrace();
        }
        c.setIdGabinete(casoTx.creaExpediente(u.getLogin(), c));
        // Guarda las variables de caso.
        c = casoTx.actualizaCasoDato(c, m);
        // Cambia el usuario al grupo ventanilla para que aparezca en el inbox.
        CasoOperacion co = ((CasoOperacion) c.getCasoOperacion().get(0));
        CasoOperacionBusinessLogic cobl = new CasoOperacionBusinessLogic(jniName);
        cobl.updateCasoResponsable(co, opResponsable);
        return c;
    }

    private Caso generaCasoCompromiso(Usuario u, FolioGeneratorInterface fg) throws GestionException {
        CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
        Caso c = casoTx.IniciaCaso(u, ID_TIPO_CASO_COMP, fg);
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        // Variables del caso
        c.getCasoDato("FOLIO").setValor(c.getFolio());
        c.getCasoDato("OPERADOR").setValor(u.getLogin());
        c.getCasoDato("FECHA_DOCUMENTO").setValor(fecha);
        try {
            c.getCasoDato("EJERCICIO_FISCAL").setValor(obtieneEjecicioFiscal());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> m = new HashMap<String, String>();
        m.put("FOLIO", c.getFolio());
        m.put("OPERADOR", u.getLogin());
        m.put("FECHA_DOCUMENTO", fecha);
        try {
            m.put("EJERCICIO_FISCAL", obtieneEjecicioFiscal());
        } catch (Exception e) {
            e.printStackTrace();
        }
        c.setIdGabinete(casoTx.creaExpediente(u.getLogin(), c));
        // Guarda las variables de caso.
        c = casoTx.actualizaCasoDato(c, m);
        // Cambia el usuario al grupo ventanilla para que aparezca en el inbox.
        CasoOperacion co = ((CasoOperacion) c.getCasoOperacion().get(0));
        CasoOperacionBusinessLogic cobl = new CasoOperacionBusinessLogic(jniName);
        co.setIdOperacion(2);
        co.setResponsable("CONSULTA_PAGOS");
        cobl.updateCasoOperacion(co);
        return c;
    }

    // IRD 20131121 RO-0009
    private Caso generaCasoPagoPasivo(Usuario u, FolioGeneratorInterface fg) throws GestionException {
        CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
        Caso c = casoTx.IniciaCaso(u, ID_TIPO_CASO_COMP, fg);
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        // Variables del caso
        c.getCasoDato("FOLIO").setValor(c.getFolio());
        c.getCasoDato("OPERADOR").setValor(u.getLogin());
        c.getCasoDato("FECHA_DOCUMENTO").setValor(fecha);
        try {
            c.getCasoDato("EJERCICIO_FISCAL").setValor(obtieneEjecicioFiscal());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> m = new HashMap<String, String>();
        m.put("FOLIO", c.getFolio());
        m.put("OPERADOR", u.getLogin());
        m.put("FECHA_DOCUMENTO", fecha);
        try {
            m.put("EJERCICIO_FISCAL", obtieneEjecicioFiscal());
        } catch (Exception e) {
            e.printStackTrace();
        }
        c.setIdGabinete(casoTx.creaExpediente(u.getLogin(), c));
        // Guarda las variables de caso.
        c = casoTx.actualizaCasoDato(c, m);
        // Cambia el usuario al grupo ventanilla para que aparezca en el inbox.
        CasoOperacion co = ((CasoOperacion) c.getCasoOperacion().get(0));
        CasoOperacionBusinessLogic cobl = new CasoOperacionBusinessLogic(jniName);
        co.setIdOperacion(2);
        co.setResponsable("CONSULTA_PAGOS");
        cobl.updateCasoOperacion(co);
        return c;
    }

    // MLR 20131220 RO-0010
    private Caso generaCasoPlurianual(Usuario u, FolioGeneratorInterface fg) throws GestionException {
        CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
        Caso c = casoTx.IniciaCaso(u, ID_TIPO_CASO_COMP, fg);
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        // Variables del caso
        c.getCasoDato("FOLIO").setValor(c.getFolio());
        c.getCasoDato("OPERADOR").setValor(u.getLogin());
        c.getCasoDato("FECHA_DOCUMENTO").setValor(fecha);
        try {
            c.getCasoDato("EJERCICIO_FISCAL").setValor(obtieneEjecicioFiscal());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> m = new HashMap<String, String>();
        m.put("FOLIO", c.getFolio());
        m.put("OPERADOR", u.getLogin());
        m.put("FECHA_DOCUMENTO", fecha);
        try {
            m.put("EJERCICIO_FISCAL", obtieneEjecicioFiscal());
        } catch (Exception e) {
            e.printStackTrace();
        }
        c.setIdGabinete(casoTx.creaExpediente(u.getLogin(), c));
        // Guarda las variables de caso.
        c = casoTx.actualizaCasoDato(c, m);
        // Cambia el usuario al grupo ventanilla para que aparezca en el inbox.
        CasoOperacion co = ((CasoOperacion) c.getCasoOperacion().get(0));
        CasoOperacionBusinessLogic cobl = new CasoOperacionBusinessLogic(jniName);
        co.setIdOperacion(2);
        co.setResponsable("CONSULTA_PLURIANUAL");
        cobl.updateCasoOperacion(co);
        return c;
    }

    private String generateCaNoCompromiso(String cCentroContable, String aEjercicioFiscal) throws SQLException {
        CFSequenceManager seqMngr = CFSequenceManager.getInstance(jniName);
        String seqValue = String.valueOf(seqMngr.nextVal("CO-" + cCentroContable));
        seqValue = 100000 + Integer.parseInt(seqValue, 10) + "";
        // seqValue = seqValue.substring(seqValue.length() - 6);
        seqValue = cCentroContable + "CO" + aEjercicioFiscal + seqValue;
        return seqValue;
    }

    // IRD 20131121 RO-0009
    private String generateCaNoPAgoPAsivo(String cCentroContable, String aEjercicioFiscal) throws SQLException {
        CFSequenceManager seqMngr = CFSequenceManager.getInstance(jniName);
        String seqValue = String.valueOf(seqMngr.nextVal("CO-" + cCentroContable));
        seqValue = "000000" + seqValue;
        seqValue = seqValue.substring(seqValue.length() - 6);
        seqValue = cCentroContable + "CO" + aEjercicioFiscal + seqValue;
        return seqValue;
    }

    // MLR 20131220 RO-0010
    private String generateCaNoPlurianual(String cCentroContable, String aEjercicioFiscal) throws SQLException {
        CFSequenceManager seqMngr = CFSequenceManager.getInstance(jniName);
        String seqValue = String.valueOf(seqMngr.nextVal("CO-" + cCentroContable));
        seqValue = "000000" + seqValue;
        seqValue = seqValue.substring(seqValue.length() - 6);
        seqValue = cCentroContable + "CO" + aEjercicioFiscal + seqValue;
        return seqValue;
    }

    public String[] applyConvenioModificatorioCompromiso(Usuario u, String folioGenerator, String nFolioConvModif, String folioSAI, String aEjercicioFiscal, String cContable, String cveContrato, String noConvenio, String fInicio, String fFin, double montoModificado) throws Exception {
        Connection conn = null;
        String msg = "";
        String[] ret = null;
        boolean success = false;
        ConfiguraAplicativoBusinessLogic cabl = null;
        Caso cCompromiso = null;
        FolioGeneratorInterface fg = null;
        try {
            cabl = new ConfiguraAplicativoBusinessLogic(jniName);
            boolean esSAIAlterno = "true".equalsIgnoreCase(cabl.getSystemSetting("SAI_AMBIENTAL")) || "true".equalsIgnoreCase(cabl.getSystemSetting("SAI_FONDEN")) || "true".equalsIgnoreCase(cabl.getSystemSetting("AMBIENTE_DESARROLLO"));
            conn = getConnection();
            boolean hayMontoModificado = ObraPublicaManager.cmTieneIncrementoMonto(conn, nFolioConvModif);
            if (hayMontoModificado) {
                try {
                    ClassLoader cl = getClass().getClassLoader();
                    Class<?> clase = cl.loadClass(folioGenerator);
                    fg = (FolioGeneratorInterface) clase.newInstance();
                } catch (ClassNotFoundException exc) {
                    log.error("Generador de folios", exc);
                    throw new ServletException(exc);
                } catch (InstantiationException exc) {
                    log.error("Generador de folios", exc);
                    throw new ServletException(exc);
                } catch (IllegalAccessException exc) {
                    log.error("Generador de folios", exc);
                    throw new ServletException(exc);
                }
                cCompromiso = generaCasoCompromiso(u, fg);
                int idCasoCompromisoStr = Integer.parseInt(cCompromiso.getFolio().substring(cCompromiso.getFolio().lastIndexOf('-') + 1));
                int nMes = GregorianCalendar.getInstance().get(Calendar.MONTH) + 1;
                String caNoCompromiso = generateCaNoCompromiso(u.getPropiedad("CCENTROCONTABLE").getValor(), aEjercicioFiscal);
                ObraPublicaContractManager.generaInformacionConvenioCompromiso(conn, nFolioConvModif, folioSAI, idCasoCompromisoStr, nMes, caNoCompromiso);
                //validar el recurso
                if (esSAIAlterno || !ObraPublicaContractManager.convTraeRecursoFiscal(conn, Integer.parseInt(nFolioConvModif))) {
                    AccountingEngine accEng = new AccountingEngine();
                    success = accEng.makeAccountingApplication(conn, "COMPROMISO", String.valueOf(idCasoCompromisoStr), "tCompromisoEncabezado", "tCompromisoDetalle", "nFolioCompromiso");
                }
                msg = "DOCUMENTO DE OBRA PUBLICA[CONVENIO MODIFICATORIO]: " + nFolioConvModif + " APLICADO CONTABLEMENTE";
                msg += "Se genero exitosamente el tramite de Compromiso. El folio del tramite es: <i>" + cCompromiso.getFolio() + "</i>";
            }
            ObraPublicaManager.insertaInformacionContratoConvenioModificatorio(conn, aEjercicioFiscal, cContable, cveContrato, noConvenio, fInicio, fFin, montoModificado);
            msg += "<br>Convenio modificatorio registrado con exito.";
            ObraPublicaManager.setConvenioModificatorioEnCaptura(conn, "N", Integer.parseInt(nFolioConvModif));
            conn.commit();
            ret = new String[] { String.valueOf(success), msg };
            return ret;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas aplicando rollback " + e, e);
                }
            throw e;
        } finally {
            if (conn != null)
                CloseObject.closeObject(conn, false);
            cabl = null;
            cCompromiso = null;
            fg = null;
        }
    }

    // IRD 20131121 RO-0009
    public String[] applyPagoPasivo(Usuario u, String folioGenerator, String nFolioOPPagPasHeader, String folioSAI, String aEjercicioFiscal, String cContable, String cveContrato, String noConvenio, String fInicio, String fFin, double montoModificado) throws Exception {
        Connection conn = null;
        String msg = "";
        String[] ret = null;
        boolean success = false;
        try {
            conn = getConnection();
            Caso cPagoObra = null, cContratoPagPas = null;
            FolioGeneratorInterface fg = null;
            try {
                ClassLoader cl = getClass().getClassLoader();
                Class<?> clase = cl.loadClass(folioGenerator);
                fg = (FolioGeneratorInterface) clase.newInstance();
            } catch (ClassNotFoundException exc) {
                log.error("Generador de folios", exc);
                throw new ServletException(exc);
            } catch (InstantiationException exc) {
                log.error("Generador de folios", exc);
                throw new ServletException(exc);
            } catch (IllegalAccessException exc) {
                log.error("Generador de folios", exc);
                throw new ServletException(exc);
            }
            cContratoPagPas = generaCaso(u, ID_TIPO_CASO_OBRA, fg, "VENTANILLA_CONTRATO");
            String idCasoContratoPagPasStr = cContratoPagPas.getFolio().substring(cContratoPagPas.getFolio().lastIndexOf('-') + 1);
            // String folioCasoContratoPagPas = cContratoPagPas.getFolio();
            cPagoObra = generaCasoPagoPasivo(u, fg);
            int idCasoCompromisoStr = Integer.parseInt(cPagoObra.getFolio().substring(cPagoObra.getFolio().lastIndexOf('-') + 1));
            int nMes = GregorianCalendar.getInstance().get(Calendar.MONTH) + 1;
            String caNoPagoPasivo = generateCaNoPAgoPAsivo(u.getPropiedad("CCENTROCONTABLE").getValor(), aEjercicioFiscal);
            ObraPublicaContractManager.generaInformacionContrato(conn, folioSAI, Integer.parseInt(idCasoContratoPagPasStr));
            ObraPublicaContractManager.generaInformacionPagoPasivoCompromiso(conn, nFolioOPPagPasHeader, folioSAI, idCasoCompromisoStr, nMes, caNoPagoPasivo);
            AccountingEngine accEng = new AccountingEngine();
            success = accEng.makeAccountingApplication(conn, "COMPROMISO", String.valueOf(idCasoCompromisoStr), "tCompromisoEncabezado", "tCompromisoDetalle", "nFolioCompromiso");
            msg = "DOCUMENTO DE OBRA PUBLICA[PAGO PASIVO]: " + nFolioOPPagPasHeader + " APLICADO CONTABLEMENTE";
            msg += "Se genero exitosamente el tramite de Compromiso. El folio del tramite es: <i>" + cPagoObra.getFolio() + "</i>";
            ObraPublicaManager.insertaInformacionContratoConvenioModificatorio(conn, aEjercicioFiscal, cContable, cveContrato, noConvenio, fInicio, fFin, montoModificado);
            msg += "<br>Pago de Pasivo registrado con exito.";
            ObraPublicaManager.setPagoPasivoEnCaptura(conn, "N", Integer.parseInt(nFolioOPPagPasHeader));
            conn.commit();
            ret = new String[] { String.valueOf(success), msg };
            return ret;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas aplicando rollback " + e, e);
                }
            throw e;
        } finally {
            if (conn != null)
                CloseObject.closeObject(conn, false);
        }
    }

    // mlr 20131220 RO-0010
    public String[] applyPlurianual(Usuario u, String folioGenerator, String nFolioOPPlurianualHeader, String folioSAI, String aEjercicioFiscal, String cContable, String cveContrato, String noConvenio, String fInicio, String fFin, double montoModificado) throws Exception {
        Connection conn = null;
        String msg = "";
        String[] ret = null;
        boolean success = false;
        try {
            conn = getConnection();
            Caso cPagoObraPluri = null, cContratoPlurianual = null;
            FolioGeneratorInterface fg = null;
            try {
                ClassLoader cl = getClass().getClassLoader();
                Class<?> clase = cl.loadClass(folioGenerator);
                fg = (FolioGeneratorInterface) clase.newInstance();
            } catch (ClassNotFoundException exc) {
                log.error("Generador de folios", exc);
                throw new ServletException(exc);
            } catch (InstantiationException exc) {
                log.error("Generador de folios", exc);
                throw new ServletException(exc);
            } catch (IllegalAccessException exc) {
                log.error("Generador de folios", exc);
                throw new ServletException(exc);
            }
            cContratoPlurianual = generaCaso(u, ID_TIPO_CASO_OBRA, fg, "VENTANILLA_CONTRATO");
            String idCasoContratoPlurianualStr = cContratoPlurianual.getFolio().substring(cContratoPlurianual.getFolio().lastIndexOf('-') + 1);
            cPagoObraPluri = generaCasoPlurianual(u, fg);
            int idCasoCompromisoPlurianualStr = Integer.parseInt(cPagoObraPluri.getFolio().substring(cPagoObraPluri.getFolio().lastIndexOf('-') + 1));
            int nMes = GregorianCalendar.getInstance().get(Calendar.MONTH) + 1;
            String caNoPlurianual = generateCaNoPlurianual(u.getPropiedad("CCENTROCONTABLE").getValor(), aEjercicioFiscal);
            ObraPublicaContractManager.generaInformacionContrato(conn, folioSAI, Integer.parseInt(idCasoContratoPlurianualStr));
            ObraPublicaContractManager.generaInformacionPlurianualCompromiso(conn, nFolioOPPlurianualHeader, folioSAI, idCasoCompromisoPlurianualStr, nMes, caNoPlurianual);
            AccountingEngine accEng = new AccountingEngine();
            success = accEng.makeAccountingApplication(conn, "COMPROMISO", String.valueOf(idCasoCompromisoPlurianualStr), "tCompromisoEncabezado", "tCompromisoDetalle", "nFolioCompromiso");
            msg = "DOCUMENTO DE OBRA PUBLICA[PLURIANUAL]: " + nFolioOPPlurianualHeader + " APLICADO CONTABLEMENTE";
            msg += "Se genero exitosamente el tramite de Compromiso. El folio del tramite es: <i>" + cPagoObraPluri.getFolio() + "</i>";
            ObraPublicaManager.insertaInformacionContratoConvenioModificatorio(conn, aEjercicioFiscal, cContable, cveContrato, noConvenio, fInicio, fFin, montoModificado);
            msg += "<br>Plurianual registrado con exito.";
            ObraPublicaManager.setPlurianualEnCaptura(conn, "N", Integer.parseInt(nFolioOPPlurianualHeader));
            conn.commit();
            ret = new String[] { String.valueOf(success), msg };
            return ret;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas aplicando rollback " + e, e);
                }
            throw e;
        } finally {
            if (conn != null)
                CloseObject.closeObject(conn, false);
        }
    }

    public String obtieneEjecicioFiscal() throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String EjercicioFiscal = "";
        try {
            conn = getConnection();
            String queryEf = "select aEjercicioFiscal from tEjercicioFiscal where cActivo = 1 ";
            ps = conn.prepareStatement(queryEf);
            rs = ps.executeQuery();
            if (rs.next()) {
                EjercicioFiscal = rs.getString("aEjercicioFiscal");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return EjercicioFiscal;
    }

    public int getTotalCFDICapturdos(Caso c, String cFolioSAI) throws Exception {
        return getTotalCFDICapturdos(c, cFolioSAI, 1);
    }

    public int getTotalCFDICapturdos(Caso c, String cFolioSAI, int nTipoFacturaGlobal) throws Exception {
        int cfdiCapturados = 0;
        Connection conn = null;
        try {
            conn = getConnection();
            if (c == null) {
                c = new Caso();
                c.setFolio(cFolioSAI);
                c.setIdTC(GestionInterface.IDTC_OBRAPUBLICA);
                c = CasoManager.select(conn, c);
            }
            String tituloAplicacion = c.getTipoCaso().getGavetaAsociada();
            int idGabinete = c.getIdGabinete();
            cfdiCapturados = ObraPublicaContractManager.getTotalCFDICapturdos(conn, tituloAplicacion, idGabinete, nTipoFacturaGlobal);
            return cfdiCapturados;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public Caso cargaCasoContrato(String folioContrato) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            return ObraPublicaContractManager.cargaCasoContrato(conn, folioContrato);
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
