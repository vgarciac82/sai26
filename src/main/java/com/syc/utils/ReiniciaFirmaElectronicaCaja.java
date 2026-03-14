package com.syc.utils;

import static com.syc.gestion.CasoBusinessLogic.CASO_END;
import static com.syc.gestion.CasoBusinessLogic.OPER_CONTINUE;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Map;
import com.syc.contable.core.SolicitudCajaFirmaElectronica;
import com.syc.gestion.TipoCasoInterface;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.BitacoraManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.CasoOperacionManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Operacion;
import com.syc.gestion.core.OperacionManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioManager;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.firmaElectronica.core.FirmaElectronicaManager;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReiniciaFirmaElectronicaCaja {

    private static final Logger log = LoggerFactory.getLogger(ReiniciaFirmaElectronicaCaja.class);

    private String folderName = "Documentacion Comprobatoria";

    private Map<String, TipoCasoInterface> tciMap;

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public static void main(String[] args) throws Exception {
        int idCaso = Integer.parseInt(args[0]);
        String login = args[1];
        String reportPath = args[2];
        String motivoRevision = args[3];
        ReiniciaFirmaElectronicaCaja rfec = new ReiniciaFirmaElectronicaCaja();
        rfec.reiniciaFirma(idCaso, login, reportPath, motivoRevision);
    }

    public synchronized Caso avanzaCaso(Connection conn, Caso c, String u_login, String observ, String[] resp, String[] oper, Map<String, String> data, String pathPrefix) throws Exception {
        Caso rco = null;
        boolean delete = true;
        if (resp.length != oper.length) {
            log.error("Object: {}", "Las dimensiones no coinciden rep[" + resp.length + "] vs oper[" + oper.length + "]");
            throw new GestionException("Las dimensiones no coinciden rep[" + resp.length + "] vs oper[" + oper.length + "]");
        }
        int[] idCasoOperSgte = new int[resp.length];
        //		if ( c.getCasoOperacion( 0 ).getOperacion().getAlarma() != null )
        //			AlarmaManager.procesaAlarma( conn, pathPrefix, c.getCasoOperacion( 0 ).getOperacion().getAlarma() );
        CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), data);
        for (int i = 0; i < resp.length; i++) {
            if (OPER_CONTINUE.equalsIgnoreCase(oper[i].trim())) {
                c.setStatus(100);
                CasoManager.update(conn, c);
                CasoOperacionManager.updateObservacion(conn, c, observ);
                delete = false;
                continue;
            } else if (CASO_END.equalsIgnoreCase(oper[i].trim())) {
                delete = false;
                for (int j = i + 1; j < idCasoOperSgte.length; j++) idCasoOperSgte[j] = -1;
                CasoManager.terminaCaso(conn, c, resp, oper);
                TipoCasoInterface tci = null;
                if (c.getTipoCaso().tieneInterface()) {
                    tci = instanceTipoCasoInterface(c.getTipoCaso().getInterface());
                    tci.onTerminaCaso(conn, u_login, c, "", resp, oper, data);
                }
                break;
            }
            Operacion o = new Operacion();
            o.setIdTC(c.getIdTC());
            o.setNombre(oper[i].trim());
            o = OperacionManager.select(conn, o);
            if (o == null) {
                log.error("Object: {}", "No se localizo la Operacion \"" + oper[i] + "\"");
                throw new GestionException("No se localizo la Operación \"" + oper[i] + "\"");
            }
            CasoOperacion co = CasoOperacionManager.nuevoCasoOperacion(conn, resp[i].trim(), observ, c, o);
            CasoOperacionManager.insert(conn, co);
            idCasoOperSgte[i] = co.getIdCasoOper();
            TipoCasoInterface tci = null;
            if (c.getTipoCaso().tieneInterface()) {
                tci = instanceTipoCasoInterface(c.getTipoCaso().getInterface());
                tci.onAvanzaCaso(conn, u_login, c, co.getIdOperacion());
            }
        }
        if (delete) {
            BitacoraManager.registraCasoOperacion(conn, u_login, c, idCasoOperSgte, resp, oper);
            CasoOperacionManager.delete(conn, c.getIdCaso(), c.getCasoOperacion(0).getIdCasoOper());
        }
        if ((c.getStatus() & Caso.MSG_SENDED) == Caso.MSG_SENDED)
            c.setStatus(c.getStatus() ^ Caso.MSG_SENDED);
        c.setStatus(c.getStatus() ^ Caso.EXECUTED);
        CasoManager.update(conn, c);
        return rco;
    }

    private void limpiaCarpeta(Connection conn, int nFolioCaja, String motivoRevision) throws Exception {
        String query = "{call sp_limpia_DocComp(?,?)}";
        CallableStatement cs = null;
        try {
            cs = conn.prepareCall(query);
            cs.setString(1, String.valueOf(nFolioCaja));
            cs.executeUpdate();
        } finally {
            CloseObject.closeObject(cs);
        }
    }

    private void reiniciaFirma(int idCaso, String uLogin, String reportPath, String motivoRevision) throws Exception {
        Connection conn = null;
        try {
            conn = Util.getStandAloneConnection();
            Caso c = instanciaCaso(conn, idCaso);
            Usuario u = instanciaUsuario(conn, uLogin);
            int nFolioCaja = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
            limpiaCarpeta(conn, nFolioCaja, motivoRevision);
            SolicitudFirmaElectronica printer = instanciaPrinter(nFolioCaja);
            printer.setReportPath(reportPath);
            printer.setUsuario(u);
            FirmaElectronicaManager.generaArchivoFirma(conn, printer, getFolderName(), false);
            Map<String, String> datos = (Map<String, String>) Util.readValuesCasoDato(c.getCasoDato());
            avanzaCaso(conn, c, u.getLogin(), "", new String[] { "VO_BO_CAJA_FIEL" }, new String[] { "vo_bo_fiel" }, datos, null);
            FirmaElectronicaManager.avanzaEstatusSICOP(conn, printer, SolicitudFirmaElectronica.VO_BO_SICOP);
            FirmaElectronicaManager.actualizaMetodoAutorizacion(conn, printer.getHeader(), printer.getField(), String.valueOf(printer.getIdField()), true);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn(e2.getMessage(), e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    private SolicitudFirmaElectronica instanciaPrinter(int nFolioCaja) {
        SolicitudFirmaElectronica printer = new SolicitudCajaFirmaElectronica();
        printer.setDetail("tCajaDetalle");
        printer.setDocName("Solicitud Firmada");
        printer.setDocument("CAJA");
        printer.setField("nFolioCaja");
        printer.setFileExtension("pdf");
        printer.setHeader("tCajaEncabezado");
        printer.setIdField(nFolioCaja);
        return printer;
    }

    private Usuario instanciaUsuario(Connection conn, String uLogin) throws Exception {
        Usuario u = new Usuario();
        u.setLogin(uLogin);
        u = UsuarioManager.select(conn, u);
        return u;
    }

    private Caso instanciaCaso(Connection conn, int idCaso) throws Exception {
        Caso c = new Caso();
        c.setIdCaso(idCaso);
        c = CasoManager.select(conn, c);
        return c;
    }

    private TipoCasoInterface instanceTipoCasoInterface(String name) throws GestionException {
        TipoCasoInterface tci = null;
        if (tciMap == null)
            tciMap = new Hashtable<>();
        if (name == null)
            throw new GestionException("'name' no debe ser nulo");
        tci = tciMap.get(name);
        if (tci != null)
            return tci;
        try {
            ClassLoader cl = getClass().getClassLoader();
            Class<?> clase = cl.loadClass(name);
            tci = (TipoCasoInterface) clase.newInstance();
            tciMap.put(name, tci);
        } catch (ClassNotFoundException exc) {
            log.error("TipoCasoInterface " + name, exc);
            throw new GestionException(exc);
        } catch (InstantiationException exc) {
            log.error("TipoCasoInterface " + name, exc);
            throw new GestionException(exc);
        } catch (IllegalAccessException exc) {
            log.error("TipoCasoInterface " + name, exc);
            throw new GestionException(exc);
        }
        return tci;
    }
}
