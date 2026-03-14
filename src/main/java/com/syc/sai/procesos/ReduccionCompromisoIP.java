package com.syc.sai.procesos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.axtel.egresos.compromiso.Compromiso;
import com.axtel.egresos.compromiso.CompromisoDetalle;
import com.axtel.egresos.compromiso.CompromisoEncabezado;
import com.axtel.egresos.compromiso.PrecompromisoFinancieroDetalle;
import com.axtel.egresos.compromiso.PrecompromisoFinancieroEncabezado;
import com.syc.contable.AccountingEngine;
import com.syc.contable.core.CompromisoManager;
import com.syc.contable.core.PrecompromisoFinanciero;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.CasoOperacionManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioManager;
import com.syc.gestion.custom.DefaultFolioGenerator;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ReduccionCompromisoIP {

    private static final Logger log = LoggerFactory.getLogger(ReduccionCompromisoIP.class);

    private static final AccountingEngine ae = new AccountingEngine();

    public static Caso generaCaso(Connection conn, Usuario u, int idTCaso, FolioGeneratorInterface fg, String opResponsable, Map<String, String> m) throws Exception {
        Caso c = CasoManager.nuevoCaso(conn, u, idTCaso, fg);
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        m.put("FOLIO", c.getFolio());
        m.put("FECHA_DOCUMENTO", fecha);
        for (Iterator<String> keyIterator = m.keySet().iterator(); keyIterator.hasNext(); ) {
            String keyStr = keyIterator.next();
            c.getCasoDato(keyStr).setValor(m.get(keyStr));
        }
        Aplicacion app = AplicacionManager.select(conn, c.getTipoCaso().getGavetaAsociada());
        int id_gabinete = AplicacionManager.createExpediente(conn, u.getLogin(), c, app);
        if (id_gabinete < 0) {
            log.error("Identificador de Gabiente inválido (< 0)");
            throw new SQLException("Identificador de Gabiente inválido (< 0)");
        }
        c.setIdGabinete(id_gabinete);
        CasoManager.update(conn, c);
        CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), m);
        AplicacionManager.updateExpediente(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), m);
        CasoOperacion co = ((CasoOperacion) c.getCasoOperacion().get(0));
        CasoOperacionManager.update(conn, co, opResponsable);
        c = CasoManager.select(conn, c);
        return c;
    }

    public static void main(String[] args) throws Exception {
        Connection conn = null;
        if (args.length != 2) {
            log.error("Uso: java LeerArchivoCSV <ruta_del_archivo_csv>");
            System.exit(1);
        }
        ae.setValidaInsuficienciaDeSaldo(true);
        String archivoCSV = args[0];
        String usuario = args[1];
        Usuario u = new Usuario();
        conn = Util.getStandAloneConnection();
        u.setLogin(usuario);
        u = UsuarioManager.select(conn, u);
        String ef = EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("EJERCICIO_FISCAL", ef);
        map.put("OPERADOR", u.getNombre());
        map.put("CONCEPTO_MOV", "");
        map.put("MONEDA", "MXN");
        map.put("FECHA_AP_CONT", Util.getTodayESMX());
        map.put("APLICADO_CONT", "true");
        map.put("CANCELADO_CONT", "false");
        map.put("MENSAJE", "");
        map.put("AUTORIZADO_CONT", "true");
        FolioGeneratorInterface fg = new DefaultFolioGenerator();
        try (BufferedReader br = new BufferedReader(new FileReader(archivoCSV))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] valores = linea.split(",");
                String contrato = valores[0];
                String ep = valores[1];
                String ue = extreaUnidad(ep);
                u.setU_UR(ue);
                Caso c = generaCaso(conn, u, 7, fg, "consulta_compromiso", map);
                PrecompromisoFinanciero pc = insertaPrecompromiso(conn, c, ef, u, ep, valores);
                if (pc != null) {
                    Compromiso compromiso = insertaCompromiso(conn, contrato, c, ef, u, pc);
                    log.info("Object: {}", "================================   APLICANDO COMPROMISO " + c.getFolio() + "   ================================");
                    ae.makeAccountingApplication(conn, "PRECOMFINANCIERO", String.valueOf(pc.getEncabezado().getFolioPrecomFinanciero()), "tPrecomFinancieroEncabezado", "tPrecomFinancierodetalle", "nFolioPrecomFinanciero");
                    ae.makeAccountingApplication(conn, "COMPROMISO", String.valueOf(compromiso.getEncabezado().getnFolioCompromiso()), "tcompromisoencabezado", "tcompromisodetalle", "nFolioCompromiso");
                    log.info("===============================================================================================================");
                }
            }
            conn.commit();
        } catch (Exception e) {
            log.error("Error occurred", "Error al leer el archivo CSV: " + e.getMessage());
            e.printStackTrace();
            Util.rollback(conn);
            throw new Exception("No se logro crear el compromiso.");
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    private static Compromiso insertaCompromiso(Connection conn, String contrato, Caso c, String ef, Usuario u, PrecompromisoFinanciero pc) throws Exception {
        CompromisoEncabezado cEnc = new CompromisoEncabezado();
        cEnc.setaEjercicioFiscal(Integer.valueOf(ef));
        cEnc.setCaNoCompromiso(generaCaNoCompromiso(conn, "10", Integer.valueOf(ef)));
        cEnc.setcCentroContable("10");
        cEnc.setcDescripcionPoliza("Disminucion de compromiso por liberacion de recursos propios");
        cEnc.setcIdContrato(contrato);
        cEnc.setcRadicado("N");
        cEnc.setcTipoContrato("DI");
        cEnc.setcTipoPoliza("CO");
        cEnc.setcUnidadResponsable(pc.getEncabezado().getUnidadResponsable());
        cEnc.setcUnidadResponsableContable("RHQ");
        cEnc.setfAplicacion(new Date());
        cEnc.setfCarga(new Date());
        cEnc.setnEnviadoSICOP(1);
        cEnc.setnFolioCompromiso(Util.folio(c));
        cEnc.setnMes(Util.getCurrentMonth(conn));
        cEnc.setUsuario(u.getLogin());
        cEnc.setEsCalendario("N");
        List<CompromisoDetalle> cDet = new ArrayList<>();
        for (PrecompromisoFinancieroDetalle pDet : pc.getDetalle()) {
            CompromisoDetalle renglon = new CompromisoDetalle();
            renglon.setcEvento("CMP004");
            renglon.setcCentroContable("10");
            renglon.setcMes(pDet.getMes());
            renglon.setEP(pDet.getEP());
            renglon.setmImporte(pDet.getImporte());
            renglon.setmImporteNegativo(pDet.getImporteNegativo());
            renglon.setnDocRenglon(pDet.getRenglon());
            renglon.setnFolioCompromiso(cEnc.getnFolioCompromiso());
            cDet.add(renglon);
        }
        Compromiso compromiso = new Compromiso();
        compromiso.setEncabezado(cEnc);
        compromiso.setDetalle(cDet);
        CompromisoManager.insertaCompromiso(conn, compromiso);
        return compromiso;
    }

    private static final CFSequenceManager sequence = CFSequenceManager.getInstance(null);

    private static String generaCaNoCompromiso(Connection conn, String cc, int ef) throws SQLException {
        int folio = 100000 + sequence.nextVal(conn, "CO-" + cc);
        String seqValue = cc + "CO" + ef + folio;
        return seqValue;
    }

    private static PrecompromisoFinanciero insertaPrecompromiso(Connection conn, Caso c, String ef, Usuario u, String ep, String[] valores) throws SQLException {
        PrecompromisoFinanciero pc = null;
        PrecompromisoFinancieroEncabezado preEnc = new PrecompromisoFinancieroEncabezado();
        preEnc.setCentroContable("10");
        preEnc.setDescripcionPoliza("Disminucion de compromiso por liberacion de recursos propios");
        preEnc.setEjercicioFiscal(Integer.valueOf(ef));
        preEnc.setFechaAplicacion(LocalDate.now());
        preEnc.setFechaCarga(LocalDate.now());
        preEnc.setFolioPrecomFinanciero(Util.folio(c));
        preEnc.setLogin(u.getLogin());
        preEnc.setRadicado(false);
        preEnc.setRamo("16");
        preEnc.setTipoPoliza("PR");
        preEnc.setUnidadResponsable(extreaUnidad(ep));
        preEnc.setUnidadResponsableContable("RHQ");
        List<PrecompromisoFinancieroDetalle> preDet = new ArrayList<>();
        int nRenglon = 1;
        for (int i = 0; i < 12; i++) {
            if (StringUtils.isBlank(valores[2 + i]))
                continue;
            PrecompromisoFinancieroDetalle renglon = new PrecompromisoFinancieroDetalle();
            BigDecimal importe = new BigDecimal(valores[2 + i]);
            if (Util.ZERO.compareTo(importe) == 0)
                continue;
            else {
                renglon.setFolioPrecomFinanciero(preEnc.getFolioPrecomFinanciero());
                renglon.setCentroContable("10");
                renglon.setcEvento("CMP003");
                renglon.setEP(ep);
                renglon.setImporte(importe.multiply(new BigDecimal(-1)));
                renglon.setImporteNegativo(importe);
                renglon.setMes(i + 1);
                renglon.setRenglon(nRenglon);
                renglon.setUnidadResponsable(preEnc.getUnidadResponsable());
                nRenglon++;
                preDet.add(renglon);
            }
        }
        if (preDet.size() > 0) {
            pc = new PrecompromisoFinanciero();
            pc.setEncabezado(preEnc);
            pc.setDetalle(preDet);
            CompromisoManager.insertaPrecomFinanciero(conn, pc);
            log.info("Object: {}", "Precompromiso " + pc.getEncabezado().getFolioPrecomFinanciero() + " insertado.");
        }
        return pc;
    }

    private static String extreaUnidad(String ep) {
        return ep.substring(56, 56 + 3);
    }
}
