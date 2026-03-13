package com.syc.obrapublica.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ObraPublicaBusinessLogic;
import com.syc.obrapublica.ObraPublicaContractBusinessLogic;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "ObraPublicaServlet", urlPatterns = { "/AplicaContableObraPublica", "/GeneraInformacionContrato", "/ConvenioModificatorio", "/CapturaEstimacion", "/NoContratoGenerador", "/NoConvenioGenerador" })
public class ObraPublicaServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 960968562779591000L;

    private String jniName = null;

    private static final Logger log = Logger.getLogger(ObraPublicaServlet.class);

    private static String folioGenerator = null;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            ResponseSender.sendError(resp, "Sin session. Por favor reingrese al sistema.");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            ResponseSender.sendError(resp, "Sin usuario en session. Por favor reingrese al sistema.");
            return;
        }
        String accion = req.getParameter("accion");
        if ("APPLY_CONT".equals(accion)) {
            String folioSAI = req.getParameter("FolioSAI");
            ObraPublicaContractBusinessLogic opbl = new ObraPublicaContractBusinessLogic(jniName);
            String[] msg = null;
            try {
                String aEjercicioFiscal = req.getParameter("aEjercicioFiscal");
                msg = opbl.ApplyCompromiso(folioSAI, aEjercicioFiscal, folioGenerator, u);
                sendFinishMessage(resp, "true".equalsIgnoreCase(msg[0]), "DOCUMENTO " + ("true".equalsIgnoreCase(msg[0]) ? "" : "NO") + " APLICADO", msg[1]);
            } catch (Exception e) {
                log.error(e, e);
                sendFinishMessage(resp, false, "DOCUMENTO NO APLICADO" + (msg != null ? "<br>" + msg[1] : ""), "Causa del error: " + e.toString());
            }
        } else if ("CONVENIO_MODIFICATORIO".equals(accion)) {
            ObraPublicaContractBusinessLogic obl = new ObraPublicaContractBusinessLogic(jniName);
            String folioSAI = req.getParameter("folioSAI");
            try {
                Caso c = obl.generaConvenioMoficatorio(u.getLogin(), folioSAI);
                CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
                ITree tree = casoTx.getArbolCaso(c);
                session.setAttribute(ATT_TREE, tree);
                session.setAttribute(ATT_CASE, c);
                resp.sendRedirect("caso/exec-container.jsp");
            } catch (GestionException e) {
                log.error(e, e);
                ResponseSender.sendError(resp, e.toString());
            }
        } else if ("PAGO_PASIVO".equals(accion)) {
            // IRD 20131121 RO-0009 copia
            // tablas del año anterior e
            // inicia el caso
            ObraPublicaContractBusinessLogic obl = new ObraPublicaContractBusinessLogic(jniName);
            String folioSAI = req.getParameter("folioSAI");
            try {
                FolioGeneratorInterface fg = null;
                ClassLoader cl = getClass().getClassLoader();
                Class<?> clase = cl.loadClass(folioGenerator);
                fg = (FolioGeneratorInterface) clase.newInstance();
                Caso c = obl.generaCaso(u, 23, fg, u.getLogin());
                CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String fecha = sdf.format(date);
                Map<String, String> data = new HashMap<String, String>();
                data.put("FOLIO", c.getFolio());
                data.put("OPERADOR", u.getLogin());
                data.put("FECHA_DOCUMENTO", fecha);
                data.put("EJERCICIO_FISCAL", "2015");
                casoTx.avanzaCaso(c, u.getLogin(), "", new String[] { "CAPTURA_OBRAPUBLICA" }, new String[] { "pago_pasivo" }, data, "");
                c = casoTx.ejecutaCaso(c.getIdCaso(), -1, u.getNombre());
                obl.copiaInformacionContratoAnioAnterior(folioSAI, c.getFolio());
                ITree tree = casoTx.getArbolCaso(c);
                session.setAttribute(ATT_TREE, tree);
                session.setAttribute(ATT_CASE, c);
                resp.sendRedirect("caso/exec-container.jsp");
            } catch (GestionException e) {
                log.error(e, e);
                ResponseSender.sendError(resp, e.toString());
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if ("CONTRATO_PLURIANUAL".equals(accion)) {
            // MLR 20131121
            // RO-0010
            ObraPublicaContractBusinessLogic obl = new ObraPublicaContractBusinessLogic(jniName);
            String folioSAI = req.getParameter("folioSAI");
            try {
                FolioGeneratorInterface fg = null;
                ClassLoader cl = getClass().getClassLoader();
                Class<?> clase = cl.loadClass(folioGenerator);
                fg = (FolioGeneratorInterface) clase.newInstance();
                Caso c = obl.generaCaso(u, 23, fg, u.getLogin());
                CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String fecha = sdf.format(date);
                Map<String, String> data = new HashMap<String, String>();
                data.put("FOLIO", c.getFolio());
                data.put("OPERADOR", u.getLogin());
                data.put("FECHA_DOCUMENTO", fecha);
                // data.put("EJERCICIO_FISCAL", aEjercicioFiscal);
                data.put("EJERCICIO_FISCAL", "2015");
                casoTx.avanzaCaso(c, u.getLogin(), "", new String[] { "CAPTURA_OBRAPUBLICA" }, new String[] { "OBRAPUBLICA_PLURI" }, data, "");
                c = casoTx.ejecutaCaso(c.getIdCaso(), -1, u.getNombre());
                obl.copiaInformacionContratoAnioAnteriorPlurianual(folioSAI, c.getFolio());
                ITree tree = casoTx.getArbolCaso(c);
                session.setAttribute(ATT_TREE, tree);
                session.setAttribute(ATT_CASE, c);
                resp.sendRedirect("caso/exec-container.jsp");
            } catch (GestionException e) {
                log.error(e, e);
                ResponseSender.sendError(resp, e.toString());
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if ("APPLY_CONV_MOD".equals(accion)) {
            String[] msg = null;
            try {
                String folioSAI = req.getParameter("FolioSAI");
                String nFolioOPConvHeader = req.getParameter("nFolioOPConvHeader");
                String aEjercicioFiscal = req.getParameter("cEjercicio");
                String cContable = req.getParameter("cCentroContable");
                String cveContrato = req.getParameter("ccvecontrato");
                String noConvenio = req.getParameter("cNoConvenio");
                String fInicio = req.getParameter("fInicioConv");
                String fFin = req.getParameter("fFinConv");
                double montoModificado = Double.parseDouble(req.getParameter("mTotalConv"));
                ObraPublicaContractBusinessLogic opbl = new ObraPublicaContractBusinessLogic(jniName);
                msg = opbl.applyConvenioModificatorioCompromiso(u, folioGenerator, nFolioOPConvHeader, folioSAI, aEjercicioFiscal, cContable, cveContrato, noConvenio, fInicio, fFin, montoModificado);
                sendFinishMessage(resp, "true".equalsIgnoreCase(msg[0]), "DOCUMENTO " + ("true".equalsIgnoreCase(msg[0]) ? "" : "NO") + " APLICADO", msg[1]);
            } catch (Exception e) {
                log.error(e, e);
                sendFinishMessage(resp, false, "DOCUMENTO NO APLICADO" + (msg != null ? "<br>" + msg[1] : ""), "Causa del error: " + e.toString());
            }
        } else if ("APPLY_PAGOPASIVO_PRECOMP".equals(accion)) {
            // IRD 20131121
            // RO-0009
            // autoriza en
            // ventanilla
            String[] msg = null;
            try {
                String folioSAI = req.getParameter("FolioSAI");
                String nfoliooppagpasheader = req.getParameter("nfoliooppagpasheader");
                String aEjercicioFiscal = req.getParameter("cEjercicio");
                String cContable = req.getParameter("cCentroContable");
                String cveContrato = req.getParameter("ccvecontrato");
                String noConvenio = req.getParameter("cNoConvenio");
                /*
				 * String fInicio = req.getParameter("fInicioConv"); String fFin
				 * = req.getParameter("fFinConv");
				 */
                String fInicio = "";
                String fFin = "";
                double montoModificado = 0.0;
                ObraPublicaContractBusinessLogic opbl = new ObraPublicaContractBusinessLogic(jniName);
                msg = opbl.applyPagoPasivo(u, folioGenerator, nfoliooppagpasheader, folioSAI, aEjercicioFiscal, cContable, cveContrato, noConvenio, fInicio, fFin, montoModificado);
                sendFinishMessage(resp, "true".equalsIgnoreCase(msg[0]), "DOCUMENTO " + ("true".equalsIgnoreCase(msg[0]) ? "" : "NO") + " APLICADO", msg[1]);
            } catch (Exception e) {
                log.error(e, e);
                sendFinishMessage(resp, false, "DOCUMENTO NO APLICADO" + (msg != null ? "<br>" + msg[1] : ""), "Causa del error: " + e.toString());
            }
        } else if ("APPLY_PLURIANUAL_PRECOMP".equals(accion)) {
            // IRD 20131121
            // RO-0009
            // autoriza en
            // ventanilla
            String[] msg = null;
            try {
                String folioSAI = req.getParameter("FolioSAI");
                String nfolioopplurianualheader = req.getParameter("nfolioopplurianualheader");
                String aEjercicioFiscal = req.getParameter("cEjercicio");
                String cContable = req.getParameter("cCentroContable");
                String cveContrato = req.getParameter("ccvecontrato");
                String noConvenio = req.getParameter("cNoConvenio");
                String fInicio = req.getParameter("fInicioConv");
                String fFin = req.getParameter("fFinConv");
                double montoModificado = 0.0;
                ObraPublicaContractBusinessLogic opbl = new ObraPublicaContractBusinessLogic(jniName);
                msg = opbl.applyPlurianual(u, folioGenerator, nfolioopplurianualheader, folioSAI, aEjercicioFiscal, cContable, cveContrato, noConvenio, fInicio, fFin, montoModificado);
                sendFinishMessage(resp, "true".equalsIgnoreCase(msg[0]), "DOCUMENTO " + ("true".equalsIgnoreCase(msg[0]) ? "" : "NO") + " APLICADO", msg[1]);
            } catch (Exception e) {
                log.error(e, e);
                sendFinishMessage(resp, false, "DOCUMENTO NO APLICADO" + (msg != null ? "<br>" + msg[1] : ""), "Causa del error: " + e.toString());
            }
        } else if ("GEN_FOLIO".equals(accion)) {
            String cUR = u.getU_UR();
            String tRecurso = req.getParameter("tRecurso") == null ? "" : req.getParameter("tRecurso");
            String tAdjudicacion = req.getParameter("tAdjudicacion") == null ? "" : req.getParameter("tAdjudicacion");
            ObraPublicaContractBusinessLogic opbl = new ObraPublicaContractBusinessLogic(jniName);
            try {
                String folioCont = opbl.generaFolioContrato(cUR, tRecurso, tAdjudicacion);
                ResponseSender.sendResult(resp, folioCont);
            } catch (Exception e) {
                log.error(e);
                ResponseSender.sendError(resp, e.getMessage());
            }
        } else if ("GEN_FOLIO_CONV".equals(accion)) {
            String cUR = u.getU_UR();
            String tRecurso = req.getParameter("tRecurso") == null ? "" : req.getParameter("tRecurso");
            String tAdjudicacion = req.getParameter("tAdjudicacion") == null ? "" : req.getParameter("tAdjudicacion");
            ObraPublicaContractBusinessLogic opbl = new ObraPublicaContractBusinessLogic(jniName);
            try {
                String folioConv = opbl.generaFolioConvenio(cUR, tRecurso, tAdjudicacion);
                ResponseSender.sendResult(resp, folioConv);
            } catch (Exception e) {
                log.error(e);
                ResponseSender.sendError(resp, e.getMessage());
            }
        } else if ("CAPTURA_ESTIMACION".equals(accion)) {
            ObraPublicaContractBusinessLogic obl = new ObraPublicaContractBusinessLogic(jniName);
            ObraPublicaBusinessLogic opbl = new ObraPublicaBusinessLogic();
            String folioSAI = req.getParameter("folioSAI");
            try {
                Caso c = obl.generaCasoCapturaEstimacion(u.getLogin(), folioSAI);
                CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
                opbl.changeStatus(folioSAI, 20);
                ITree tree = casoTx.getArbolCaso(c);
                session.setAttribute(ATT_TREE, tree);
                session.setAttribute(ATT_CASE, c);
                session.setAttribute("OP_CAPTURA_ESTIMACION", "true");
                resp.sendRedirect("caso/exec-container.jsp");
            } catch (GestionException e) {
                log.error(e, e);
                ResponseSender.sendError(resp, e.toString());
            }
        } else if ("ACTUALIZACION_DATOS".equals(accion)) {
            ObraPublicaContractBusinessLogic obl = new ObraPublicaContractBusinessLogic(jniName);
            ObraPublicaBusinessLogic opbl = new ObraPublicaBusinessLogic();
            String folioSAI = req.getParameter("folioSAI");
            try {
                Caso c = obl.generaCasoCapturaEstimacion(u.getLogin(), folioSAI);
                CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
                opbl.changeStatus(folioSAI, 20);
                ITree tree = casoTx.getArbolCaso(c);
                session.setAttribute(ATT_TREE, tree);
                session.setAttribute(ATT_CASE, c);
                session.setAttribute("OP_ACTUALIZACION_DATOS", "true");
                resp.sendRedirect("caso/exec-container.jsp");
            } catch (GestionException e) {
                log.error(e, e);
                ResponseSender.sendError(resp, e.toString());
            }
        } else if ("APLICA_APARTADO".equals(accion)) {
            String folioSAI = req.getParameter("FolioSAI");
            ObraPublicaContractBusinessLogic opbl = new ObraPublicaContractBusinessLogic(jniName);
            String[] msg = null;
            try {
                msg = opbl.ApplyApartado(folioSAI);
                sendFinishMessage(resp, "true".equalsIgnoreCase(msg[0]), "DOCUMENTO " + ("true".equalsIgnoreCase(msg[0]) ? "" : "NO") + " APLICADO", msg[1]);
            } catch (Exception e) {
                log.error(e, e);
                sendFinishMessage(resp, false, "DOCUMENTO NO APLICADO" + (msg != null ? "<br>" + msg[1] : ""), "Causa del error: " + e.toString());
            }
        } else if ("APLICA_PRECOMPROMISO".equals(accion)) {
            String folioSAI = req.getParameter("FolioSAI");
            ObraPublicaContractBusinessLogic opbl = new ObraPublicaContractBusinessLogic(jniName);
            String[] msg = null;
            try {
                msg = opbl.ApplyPreCompr(folioSAI);
                sendFinishMessage(resp, "true".equalsIgnoreCase(msg[0]), "DOCUMENTO " + ("true".equalsIgnoreCase(msg[0]) ? "" : "NO") + " APLICADO", msg[1]);
            } catch (Exception e) {
                log.error(e, e);
                sendFinishMessage(resp, false, "DOCUMENTO NO APLICADO" + (msg != null ? "<br>" + msg[1] : ""), "Causa del error: " + e.toString());
            }
        } else if ("APLICA_CONVENIO_PRECOMP".equals(accion)) {
            String nFolioOPConvHeader = req.getParameter("nFolioOPConvHeader");
            ObraPublicaContractBusinessLogic opbl = new ObraPublicaContractBusinessLogic(jniName);
            String[] msg = null;
            try {
                msg = opbl.applyConvenioModificatorioPrecompromiso(nFolioOPConvHeader);
                sendFinishMessage(resp, "true".equalsIgnoreCase(msg[0]), "DOCUMENTO " + ("true".equalsIgnoreCase(msg[0]) ? "" : "NO") + " APLICADO", msg[1]);
            } catch (Exception e) {
                log.error(e, e);
                sendFinishMessage(resp, false, "DOCUMENTO NO APLICADO" + (msg != null ? "<br>" + msg[1] : ""), "Causa del error: " + e.toString());
            }
        } else if ("APLICA_PAGO_PASIVO_PRECOMP".equals(accion)) {
            // IRD 20131121
            // RO-0009
            // aplica
            // precompromiso
            // y envia a
            // ventanilla
            String nFolioOPPagPasHeader = req.getParameter("nFolioOPPagPasHeader");
            ObraPublicaContractBusinessLogic opbl = new ObraPublicaContractBusinessLogic(jniName);
            String[] msg = null;
            try {
                msg = opbl.applyPagoPasivoPrecompromiso(nFolioOPPagPasHeader);
                sendFinishMessage(resp, "true".equalsIgnoreCase(msg[0]), "DOCUMENTO " + ("true".equalsIgnoreCase(msg[0]) ? "" : "NO") + " APLICADO", msg[1]);
            } catch (Exception e) {
                log.error(e, e);
                sendFinishMessage(resp, false, "DOCUMENTO NO APLICADO" + (msg != null ? "<br>" + msg[1] : ""), "Causa del error: " + e.toString());
            }
        } else if ("APLICA_PLURIANUAL_PRECOMP".equals(accion)) {
            // MLR 20131121
            // RO-0010
            // aplica
            // precompromiso
            // y envia a
            // ventanilla
            String nfolioopplurianualheader = req.getParameter("nfolioopplurianualheader");
            ObraPublicaContractBusinessLogic opbl = new ObraPublicaContractBusinessLogic(jniName);
            String[] msg = null;
            try {
                msg = opbl.applyPlurianualPrecompromiso(nfolioopplurianualheader);
                sendFinishMessage(resp, "true".equalsIgnoreCase(msg[0]), "DOCUMENTO " + ("true".equalsIgnoreCase(msg[0]) ? "" : "NO") + " APLICADO", msg[1]);
            } catch (Exception e) {
                log.error(e, e);
                sendFinishMessage(resp, false, "DOCUMENTO NO APLICADO" + (msg != null ? "<br>" + msg[1] : ""), "Causa del error: " + e.toString());
            }
        } else if ("ACTUALIZA_CM".equals(accion)) {
            try {
                String fFechaIniContr = req.getParameter("fInicioConv");
                String fFechaFinContr = (req.getParameter("fFinConv") == null ? "01/01/1900" : req.getParameter("fFinConv"));
                double mMonto = Double.parseDouble(req.getParameter("mConv"));
                double nPorceIVA = Double.parseDouble(req.getParameter("ivaConv"));
                double mMontoConIVA = Double.parseDouble(req.getParameter("mTotalConv"));
                String cDescripcionConvenio = req.getParameter("cMotivoConv");
                double mMontoIncremento = Double.parseDouble(req.getParameter("mIncremento"));
                double mMontoIncrementoIVA = Double.parseDouble(req.getParameter("mMontoIncrementoIVA"));
                int nFolioOPConvHeader = ("".equals(req.getParameter("nFolioOPConvHeader")) ? 0 : Integer.parseInt(req.getParameter("nFolioOPConvHeader")));
                String folioConvenio = req.getParameter("cNoConvenio");
                ObraPublicaBusinessLogic opbl = new ObraPublicaBusinessLogic();
                nFolioOPConvHeader = opbl.actualizaEncabezadoConvModificatorio(folioConvenio, fFechaIniContr, fFechaFinContr, mMonto, nPorceIVA, mMontoConIVA, cDescripcionConvenio, mMontoIncremento, mMontoIncrementoIVA, nFolioOPConvHeader);
                ResponseSender.sendClientSimpleMessage(resp, true, String.valueOf(nFolioOPConvHeader));
            } catch (Exception e) {
                log.error("Error actualizando encabezado el CM. " + e.toString(), e);
                ResponseSender.sendClientSimpleMessage(resp, false, e.toString());
            }
        } else if ("ACTUALIZA_PAGO_PASIVO".equals(accion)) {
            // IRD 20131121
            // RO-0009
            try {
                double mPagoPasivo = Double.parseDouble(req.getParameter("mPagoPasivo"));
                double ivaPagPasF = Double.parseDouble(req.getParameter("ivaPagPasF"));
                double mTotalPagoPasivo = Double.parseDouble(req.getParameter("mTotalPagoPasivo"));
                String cMotivoPagoPasivo = req.getParameter("cMotivoPagoPasivo");
                String nFolioOPPagPasHeader = req.getParameter("nFolioOPPagPasHeader");
                String folioSAI = req.getParameter("folioSAI");
                String fFechaAplicacion = req.getParameter("fRecepcion");
                String aEjercicioFiscal = req.getParameter("cEjercicio");
                ObraPublicaBusinessLogic opbl = new ObraPublicaBusinessLogic();
                nFolioOPPagPasHeader = opbl.actualizaEncabezadoPagoPasivo(mPagoPasivo, ivaPagPasF, mTotalPagoPasivo, cMotivoPagoPasivo, nFolioOPPagPasHeader, folioSAI, fFechaAplicacion, aEjercicioFiscal);
                ResponseSender.sendClientSimpleMessage(resp, true, String.valueOf(nFolioOPPagPasHeader));
            } catch (Exception e) {
                log.error("Error actualizando encabezado el CM. " + e.toString(), e);
                ResponseSender.sendClientSimpleMessage(resp, false, e.toString());
            }
        } else if ("ACTUALIZA_PLURIANUAL".equals(accion)) {
            // MLR 20131217
            // RO-0010
            try {
                double mPlurianual = Double.parseDouble(req.getParameter("mPlurianual"));
                double ivaPlurianual = Double.parseDouble(req.getParameter("ivaPlurianual"));
                double mTotalPlurianual = Double.parseDouble(req.getParameter("mTotalPlurianual"));
                // String cMotivoPagoPasivo =
                // req.getParameter("cMotivoPagoPasivo");
                String nFolioOPPlurianualHeader = req.getParameter("nFolioOPPlurianualHeader");
                String folioSAI = req.getParameter("folioSAI");
                String fFechaAplicacion = req.getParameter("fRecepcion");
                String aEjercicioFiscal = req.getParameter("cEjercicio");
                ObraPublicaBusinessLogic opbl = new ObraPublicaBusinessLogic();
                nFolioOPPlurianualHeader = opbl.actualizaEncabezadoPlurianual(mPlurianual, ivaPlurianual, mTotalPlurianual, nFolioOPPlurianualHeader, folioSAI, fFechaAplicacion, aEjercicioFiscal);
                ResponseSender.sendClientSimpleMessage(resp, true, String.valueOf(nFolioOPPlurianualHeader));
            } catch (Exception e) {
                log.error("Error actualizando encabezado el CM. " + e.toString(), e);
                ResponseSender.sendClientSimpleMessage(resp, false, e.toString());
            }
        } else if ("CANCEL_PRECOM_PASIVO".equals(accion)) {
            ObraPublicaContractBusinessLogic opbl = new ObraPublicaContractBusinessLogic(jniName);
            String folioSAI = req.getParameter("FolioSAI");
            String[] retVal = opbl.CancelPasivo(folioSAI, "PAGOPASIVO_PRECOM");
            if ("false".equalsIgnoreCase(retVal[0])) {
                log.error("No se pudo cancelar el documento.");
                ResponseSender.sendClientSimpleMessage(resp, false, "No se pudo cancelar el documento.");
            }
            ResponseSender.sendClientSimpleMessage(resp, true, "");
        } else if ("CANCEL_PRECOM_PLURIANUAL".equals(accion)) {
            ObraPublicaContractBusinessLogic opbl = new ObraPublicaContractBusinessLogic(jniName);
            String folioSAI = req.getParameter("FolioSAI");
            String[] retVal = opbl.CancelPlurianual(folioSAI, "PLURIANUAL_PRECOM");
            if ("false".equalsIgnoreCase(retVal[0])) {
                log.error("No se pudo cancelar el documento.");
                ResponseSender.sendClientSimpleMessage(resp, false, "No se pudo cancelar el documento.");
            }
            ResponseSender.sendClientSimpleMessage(resp, true, "");
        } else if ("CONTRATO_CON_FACTURA".equals(accion)) {
            try {
                ObraPublicaContractBusinessLogic opbl = new ObraPublicaContractBusinessLogic(jniName);
                Caso c = (Caso) session.getAttribute(ATT_CASE);
                int nTipoFacturaGlobal = (null == req.getParameter("tipoFactGlobal") || "".equalsIgnoreCase(req.getParameter("tipoFactGlobal")) ? 1 : Integer.parseInt(req.getParameter("tipoFactGlobal")));
                int cfdiCapurados = opbl.getTotalCFDICapturdos(c, (null == req.getParameter("foliosai") ? "" : req.getParameter("foliosai")), nTipoFacturaGlobal);
                ResponseSender.sendClientSimpleMessage(resp, true, String.valueOf(cfdiCapurados));
            } catch (Exception e) {
                log.error(e, e);
                ResponseSender.sendClientSimpleMessage(resp, false, e.toString());
            }
        }
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        try {
            InitialContext ic = new InitialContext();
            folioGenerator = (String) ic.lookup("java:comp/env/folioGeneratorInterface");
            if (folioGenerator == null) {
                folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
                log.info("Environment Entry \"folioGeneratorInterface\" nula usando default \"" + folioGenerator + "\"");
            } else
                log.info("folioGeneratorInterface=" + folioGenerator);
        } catch (NamingException exc) {
            folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
            log.info("Environment Entry \"folioGeneratorInterface\" no definida usando default \"" + folioGenerator + "\"");
        }
    }

    private void sendFinishMessage(HttpServletResponse resp, boolean success, String tituloMsg, String message) throws IOException {
        PrintWriter out = resp.getWriter();
        String strResp = "<HTML>";
        strResp += "\n\t<BODY";
        strResp += "\n\t\tstyle=\"font-family: 'Lucida Grande', Verdana, Arial, Helvetica, sans-serif; font-size: 12px; font-weight: bold;\">";
        strResp += "\n\t\t<table align=\"center\" width=\"100%\">";
        strResp += "\n\t\t\t<tr>";
        strResp += "\n\t\t\t\t<td align=\"center\">";
        strResp += "\n\t\t\t\t\t" + tituloMsg;
        strResp += "\n\t\t\t\t</td>";
        strResp += "\n\t\t\t</tr>";
        strResp += "\n\t\t\t<tr>";
        strResp += "\n\t\t\t\t<td align=\"center\">";
        strResp += "\n\t\t\t\t\t<table>";
        strResp += "\n\t\t\t\t\t\t<tr>";
        strResp += "\n\t\t\t\t\t\t\t<td align=\"center\" style=\"font-size: 11px;\">";
        strResp += "\n\t\t\t\t\t\t\t\t<br>";
        strResp += "\n\t\t\t\t\t\t\t\t" + message;
        strResp += "\n\t\t\t\t\t\t\t\t<br>";
        strResp += "\n\t\t\t\t\t\t\t</td>";
        strResp += "\n\t\t\t\t\t\t</tr>";
        strResp += "\n\t\t\t\t\t\t<tr>";
        strResp += "\n\t\t\t\t\t\t\t<td align=\"right\">";
        strResp += "\n\t\t\t\t\t\t\t\t<input type=\"button\" value=\"Aceptar\"";
        strResp += "\n\t\t\t\t\t\t\t\t\tonclick=\"parent.terminaAppCont(" + String.valueOf(success) + ")\">";
        strResp += "\n\t\t\t\t\t\t\t</td>";
        strResp += "\n\t\t\t\t\t\t</tr>";
        strResp += "\n\t\t\t\t\t</table>";
        strResp += "\n\t\t\t\t</td>";
        strResp += "\n\t\t\t</tr>";
        strResp += "\n\t\t</table>";
        strResp += "\n\t</BODY>";
        strResp += "\n</HTML>";
        strResp += "\n";
        out.println(strResp);
        out.flush();
        out.close();
    }
}
