package com.syc.sai.procesos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.axtel.egresos.viaticos.core.TransporteDAO;
import com.syc.cfdi.core.FacturaManager;
import com.syc.cfdi.db.CloseObject;
import com.syc.contable.AccountingEngine;
import com.syc.contable.caja.core.CajaManager;
import com.syc.contable.core.RelacionGastosManager;
import com.syc.contable.core.SolicitudPagoFirmaElectronica;
import com.syc.egresos.core.RelacionGastosDetalle;
import com.syc.egresos.core.RelacionGastosEncabezado;
import com.syc.ejercido.pagado.LogCancelaDevengadoManager;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.TipoCasoInterface;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.BitacoraManager;
import com.syc.gestion.core.CFSequenceManager;
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
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.PasivoDiferidoManager;
import com.syc.sai.firmaElectronica.core.FirmaElectronicaManager;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CambiaEPRelacionGastos {

    private static final Logger log = LoggerFactory.getLogger(CambiaEPRelacionGastos.class);

    public static void main(String[] args) throws Exception {
        String integracion = args[0];
        String reportPath = args[1];
        List<Integer> folios = cargaFolios(integracion);
        log.info("Iniciando proceso.");
        Map<String, List<RelacionGastosEncabezado[]>> usuarioTramite = new HashMap<String, List<RelacionGastosEncabezado[]>>();
        for (Integer folio : folios) {
            Connection conn = null;
            try {
                conn = Util.getStandAloneConnection();
                log.info("===========================================================================================");
                log.info("Object: {}", "Procesando Folio: " + folio);
                if (esRelacionReemplazada(conn, folio)) {
                    log.info("Object: {}", "La relacion de gastos con folio: " + folio + " ya ha sido reemplazada.");
                    continue;
                }
                RelacionGastosEncabezado encabezado = RelacionGastosManager.clonaRelacionGastos(conn, folio);
                RelacionGastosEncabezado original = RelacionGastosManager.seleccionaRG(conn, folio);
                if (encabezado == null)
                    throw new Exception("No se encontro la RG con folio: " + folio);
                List<RelacionGastosDetalle> detalleOriginal = RelacionGastosManager.getRelacionGastosDetalle(conn, folio);
                if (detalleOriginal.size() == 0)
                    throw new Exception("La RG con folio: " + folio + " no tiene detalle");
                Map<String, BigDecimal> detalleGenealNuevo = generaMontosEgreso(detalleOriginal);
                List<RelacionGastosDetalle> detalleNuevo = RelacionGastosManager.generaCalendario(conn, detalleGenealNuevo);
                detalleNuevo = complementaDetalle(conn, encabezado, detalleNuevo, detalleOriginal);
                Caso c = cargaCaso(conn, String.valueOf(folio));
                Usuario u = cargaUsuario(conn, encabezado.getcIdUsuarioCaptura());
                Caso casoNuevo = generaCasoNuevo(conn, c, u);
                insertaExpediente(conn, c.getIdGabinete(), casoNuevo.getIdGabinete());
                String contrarecibo = RelacionGastosManager.generaContrarecibo(conn, u);
                encabezado.setCaNoContrarrecibo(contrarecibo);
                int nFolioRelacionGastos = Integer.parseInt(casoNuevo.getFolio().substring(casoNuevo.getFolio().lastIndexOf('-') + 1));
                encabezado.setnFolioRELACIONGASTOS(nFolioRelacionGastos);
                actualizaFolioRGDet(detalleNuevo, nFolioRelacionGastos);
                int insertados = RelacionGastosManager.insertRelacionGastosEncabezado(conn, encabezado);
                insertados += RelacionGastosManager.insertaRelacionGastosDetalle(conn, detalleNuevo);
                log.info("Object: {}", "Insertados " + insertados + " registros");
                int nFolioPagoApartado = RelacionGastosManager.insertaPagoApartado(conn, encabezado);
                int facturas = copiaFacturas(conn, folio, nFolioRelacionGastos);
                log.info("Object: {}", "Se recuperaron " + facturas + " elementos de CFDI (Facturas, Retenciones e impuestos) ");
                int facturasBorradas = limpiaFacturasOrigen(conn, folio);
                log.info("Object: {}", "Se borraron " + facturasBorradas + " elementos de CFDI (Facturas, Retenciones e impuestos) ");
                int vuelosActualizados = actualizaVuelos(conn, folio, nFolioRelacionGastos);
                log.info("Object: {}", "Se actualizaron " + vuelosActualizados + " vuelos) ");
                int contrarreciboInsertado = insertaContrarrecibo(conn, original.getCaNoContrarrecibo(), contrarecibo);
                log.info("Object: {}", "Se insertaron " + contrarreciboInsertado + " contrarecibo ");
                int insertaDocComp = insertaDocComp(conn, original.getCaNoContrarrecibo(), contrarecibo);
                log.info("Object: {}", "Se insertaron " + insertaDocComp + " documentacion comprobatoria ");
                aplicaApartado(conn, nFolioPagoApartado);
                avanzaCasoAutorizacion(conn, casoNuevo, u);
                aplicaDevengado(conn, encabezado);
                avanzaCasoConsulta(conn, casoNuevo, u);
                insertaRelacionReemplazo(conn, folio, nFolioRelacionGastos);
                cancelaDevengado(conn, u, folio, u.getPropiedad("CCENTROCONTABLE").getValor());
                notificaFirma(conn, encabezado, u, reportPath);
                if (usuarioTramite.get(u.getLogin()) == null)
                    usuarioTramite.put(u.getLogin(), new ArrayList<RelacionGastosEncabezado[]>());
                usuarioTramite.get(u.getLogin()).add(new RelacionGastosEncabezado[] { original, encabezado });
                conn.commit();
            } catch (Exception e) {
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e2) {
                        log.warn("Error occurred", "Error en rollback: " + e2);
                    }
                log.error(e.getMessage(), e);
            } finally {
                CloseObject.closeObject(conn);
            }
        }
        Connection conn = null;
        try {
            conn = Util.getStandAloneConnection();
            notificaCapturista(conn, usuarioTramite);
            notificaOperador(conn, usuarioTramite);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    private static void notificaCapturista(Connection conn, Map<String, List<RelacionGastosEncabezado[]>> usuarioTramite) throws Exception {
        for (Iterator<String> iter = usuarioTramite.keySet().iterator(); iter.hasNext(); ) {
            String uLogin = iter.next();
            Usuario u = new Usuario();
            u.setLogin(uLogin);
            u = UsuarioManager.select(conn, u);
            List<RelacionGastosEncabezado[]> r = usuarioTramite.get(uLogin);
            String correo = "<html>" + "<head>" + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">" + "<style type=\"text/css\"> " + "	body { " + "			font-family: verdana, arial, sans-serif; " + "			font-size: 12px; " + "	} " + "	table { " + "		font-size: 12px; " + "		color: #333333; " + "		border-width: 1px; " + "		border-color: #666666; " + "		border-collapse: collapse; " + "	} " + "	table th { " + "		border-width: 1px; " + "		padding: 8px; " + "		border-style: solid; " + "		border-color: #666666; " + "		background-color: #dedede; " + "	} " + "	table td { " + "		border-width: 1px; " + "		padding: 8px; " + "		border-style: solid; " + "		border-color: #666666; " + "		background-color: #ffffff; " + "	} " + "	</style>" + "</head>" + "<body>" + "<br>" + "<div>" + "<form id=\"Form\" name=\"FormViaticos\"><b>C." + u.getNombre() + " </b> <br>" + "<b>Atencion!</b> <br>" + "<p>Se hace de su conocimiento que debido al <b>ambiente controlado </b> aplicado por la SHCP sus solicitudes de pago fueron reemplazadas <b>de gasto fiscal a recurso propio</b>. En la tabla siguiente encontrará el pago con su respectivo remplazo el cual tendrá que revisar y firmar electrónica en Visto Bueno y Autorización. . </p>" + "<table>" + "<thead>" + "<tr>" + "<th>Cuenta Por Pagar Original</th>" + "<th>Cuenta Por Pagar Nueva</th>" + "</tr>" + "</thead>" + "<tbody>";
            for (int i = 0; i < r.size(); i++) {
                RelacionGastosEncabezado original = r.get(i)[0];
                RelacionGastosEncabezado nueva = r.get(i)[1];
                correo = correo + "<tr>" + "<td>" + original.getCaNoContrarrecibo() + "</td>" + "<td>" + nueva.getCaNoContrarrecibo() + "</td>" + "</tr>";
            }
            correo = correo + "</tbody>" + "</table>" + "<br>" + "<br>" + "<b>Por favor realizar seguimiento a los tramites.</b> <br>" + "<br>" + "<p>Notificaciones Automaticas<br>" + "Sistema de Administracion Integral<br>" + Util.getTodayESMX() + "</p>" + "</form>" + "</body>" + "</html>";
            AlarmaManager.procesaAlarmaCNF(conn, "", null, null, "Notificacion de pagos reemplazados", u.getU_email(), correo);
        }
    }

    public static Caso avanzaCasoAutorizacion(Connection conn, Caso caso, Usuario u) throws Exception {
        return avanzaCaso(conn, caso, u.getLogin(), "", new String[] { "AUTORIZA_RELACIONGASTOS" }, new String[] { "autoriza_factura" }, Util.readValuesCasoDato(caso.getCasoDato()), null);
    }

    public static Caso avanzaCasoConsulta(Connection conn, Caso caso, Usuario u) throws Exception {
        return avanzaCaso(conn, caso, u.getLogin(), "", new String[] { "CONSULTA_RELACIONGASTOS" }, new String[] { "consulta_factura" }, Util.readValuesCasoDato(caso.getCasoDato()), null);
    }

    public static void aplicaDevengado(Connection conn, RelacionGastosEncabezado encabezado) throws Exception {
        AccountingEngine ae = new AccountingEngine();
        ae.setValidaInsuficienciaDeSaldo(true);
        ae.makeAccountingApplication(conn, "RELACIONGASTOS", String.valueOf(encabezado.getnFolioRELACIONGASTOS()), "tRELACIONGASTOSEncabezado", "tRELACIONGASTOSDetalle", "nFolioRELACIONGASTOS");
    }

    public static void aplicaApartado(Connection conn, int nFolioPagoApartado) throws Exception {
        AccountingEngine ae = new AccountingEngine();
        ae.setValidaInsuficienciaDeSaldo(true);
        ae.makeAccountingApplication(conn, "PAGOAPARTADO", String.valueOf(nFolioPagoApartado), "tPagoApartadoEncabezado", "tPagoApartadoDetalle", "nFolioPagoApartado");
    }

    private static void actualizaFolioRGDet(List<RelacionGastosDetalle> detalle, int nFolioRelacionGastos) {
        for (RelacionGastosDetalle renglon : detalle) {
            renglon.setnFolioRELACIONGASTOS(nFolioRelacionGastos);
        }
    }

    private static void insertaExpediente(Connection conn, int idGabineteOrigen, int idGabineteDestino) throws Exception {
        int insertados = 0;
        insertados += insertaCarpetas(conn, idGabineteOrigen, idGabineteDestino);
        insertados += insertaOrgCarpetas(conn, idGabineteOrigen, idGabineteDestino);
        insertados += insertaDocumentos(conn, idGabineteOrigen, idGabineteDestino);
        insertados += insertaPaginas(conn, idGabineteOrigen, idGabineteDestino);
        log.info("Object: {}", "Se insertaron " + insertados + " elementos en expediente");
    }

    private static Usuario cargaUsuario(Connection conn, String u_LOGIN) throws Exception {
        Usuario u = new Usuario();
        u.setLogin(u_LOGIN);
        u = UsuarioManager.select(conn, u);
        return u;
    }

    private static int insertaPaginas(Connection conn, int idGabineteOrigen, int idGabineteDestino) throws Exception {
        String query = "INSERT INTO imx_pagina(TITULO_APLICACION, ID_CARPETA_PADRE, ID_DOCUMENTO, NUMERO_PAGINA, VOLUMEN, TIPO_VOLUMEN, NOM_ARCHIVO_VOL, NOM_ARCHIVO_ORG, TIPO_PAGINA, ANOTACIONES, ESTADO_PAGINA, TAMANO_BYTES, ID_GABINETE, FECHA_CREACION, HORA_CREACION, ROWID)" + " SELECT	pagina.TITULO_APLICACION ," + "         pagina.ID_CARPETA_PADRE ," + "         pagina.ID_DOCUMENTO ," + "         pagina.NUMERO_PAGINA ," + "         pagina.VOLUMEN ," + "         pagina.TIPO_VOLUMEN ," + "         pagina.NOM_ARCHIVO_VOL ," + "         pagina.NOM_ARCHIVO_ORG ," + "         pagina.TIPO_PAGINA ," + "         pagina.ANOTACIONES ," + "         pagina.ESTADO_PAGINA ," + "         pagina.TAMANO_BYTES ," + "         " + idGabineteDestino + " AS ID_GABINETE ," + "         pagina.FECHA_CREACION ," + "         pagina.HORA_CREACION ," + "         pagina.ROWID" + "  FROM	imx_pagina pagina WITH(NOLOCK) " + "		INNER JOIN " + "		IMX_DOCUMENTO documento WITH(NOLOCK)" + "		ON pagina.TITULO_APLICACION = documento.TITULO_APLICACION " + "		AND pagina.ID_GABINETE = documento.ID_GABINETE " + "		AND pagina.ID_CARPETA_PADRE = documento.ID_CARPETA_PADRE " + "		AND pagina.ID_DOCUMENTO = documento.ID_DOCUMENTO" + "		INNER JOIN " + "		IMX_CARPETA carpeta" + "		ON documento.TITULO_APLICACION = carpeta.TITULO_APLICACION " + "		AND documento.ID_GABINETE = carpeta.ID_GABINETE " + "		AND documento.ID_CARPETA_PADRE = carpeta.ID_CARPETA" + "		INNER JOIN dbo.IMX_ORG_CARPETA orgCarpeta" + "		ON carpeta.TITULO_APLICACION = orgCarpeta.TITULO_APLICACION " + "		AND carpeta.ID_GABINETE = orgCarpeta.ID_GABINETE " + "		AND carpeta.ID_CARPETA = orgCarpeta.ID_CARPETA_HIJA" + " WHERE	pagina.TITULO_APLICACION = 'RELACIONGASTOS' " + "   AND	pagina.ID_GABINETE = " + idGabineteOrigen + "   AND	documento.ID_CARPETA_PADRE <> 0" + "   AND NOMBRE_CARPETA <> 'SOLICITUD DE PAGO'";
        PreparedStatement ps = null;
        try {
            log.debug("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            int afectados = ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    private static List<RelacionGastosDetalle> complementaDetalle(Connection conn, RelacionGastosEncabezado encabezado, List<RelacionGastosDetalle> detalleNuevo, List<RelacionGastosDetalle> detalleOriginal) throws Exception {
        RelacionGastosDetalle muestra = detalleOriginal.get(0);
        for (RelacionGastosDetalle renglon : detalleNuevo) {
            renglon.setnMes(muestra.getnMes());
            renglon.setcEjercicio(muestra.getcEjercicio());
            renglon.setcIdEntidadContable(muestra.getcIdEntidadContable());
            renglon.setcIdRelacion(muestra.getcIdRelacion());
            renglon.setcIdCuentaContable(muestra.getcIdCuentaContable());
            renglon.setmComprometido(renglon.getmImporteMasIva());
            renglon.setnPoliza(1);
            renglon.setID_TIPO_MOVIMIENTO(muestra.getID_TIPO_MOVIMIENTO());
            renglon.setID_TIPO_CONCEPTO(muestra.getID_TIPO_CONCEPTO());
            renglon.setaEjercicioFiscal(muestra.getaEjercicioFiscal());
            renglon.setcCentroContable(muestra.getcCentroContable());
            renglon.setRFC(muestra.getRFC());
            renglon.setnCapitulo(String.valueOf(Integer.parseInt(renglon.getEP().substring(31, 32)) * 10000));
            renglon.setAltaAlmacen(muestra.getAltaAlmacen());
            renglon.setOBGT(muestra.getOBGT());
            renglon.setCTAB(muestra.getCTAB());
            renglon.setRFC(muestra.getRFC());
            calculaMontos(renglon, muestra);
            renglon.setcEvento(calculaEvento(conn, renglon, encabezado));
        }
        return detalleNuevo;
    }

    private static void calculaMontos(RelacionGastosDetalle renglon, RelacionGastosDetalle muestra) {
        if (muestra.getmImporteNeto().equals(renglon.getmImporteBruto())) {
            renglon.setmImporteNeto(renglon.getmImporteMasIva());
            renglon.setmImporteBruto(renglon.getmImporteMasIva());
            renglon.setmImporteIva(renglon.getmImporteMasIva());
        } else {
            BigDecimal porcentajeImpuesto = muestra.getmImporteMasIva().subtract(muestra.getmImporteBruto()).divide(muestra.getmImporteBruto());
            porcentajeImpuesto = porcentajeImpuesto.add(new BigDecimal(1.0d));
            renglon.setmImporteNeto(renglon.getmImporteMasIva());
            renglon.setmImporteBruto(renglon.getmImporteMasIva().divide(porcentajeImpuesto));
            renglon.setmImporteIva(renglon.getmImporteMasIva());
        }
    }

    private static String calculaEvento(Connection conn, RelacionGastosDetalle renglon, RelacionGastosEncabezado encabezado) throws Exception {
        String query = "SELECT cevto " + "FROM   teventoconcepto " + "WHERE  id_destino_gasto = ? " + "       AND cobgini = ? " + "       AND cfuentefinanciamiento = ? " + "       AND ctconc = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        String cevento = null;
        String destinoGasto = encabezado.getID_DESTINO_GASTO();
        String obgt = renglon.getOBGT();
        String fuenteFinanciamiento = renglon.getEP().substring(39, 40);
        String concepto = renglon.getID_TIPO_CONCEPTO();
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, destinoGasto);
            ps.setString(2, obgt);
            ps.setString(3, fuenteFinanciamiento);
            ps.setString(4, concepto);
            rs = ps.executeQuery();
            if (rs.next())
                cevento = "APD_" + rs.getString(1);
            else
                throw new Exception("No se encontro evento para la combinacion: Destino Gasto[" + destinoGasto + "] OBGT[" + obgt + "] FF[" + fuenteFinanciamiento + "] Concepto[" + concepto + "]");
            return cevento;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    private static Map<String, BigDecimal> generaMontosEgreso(List<RelacionGastosDetalle> detalle) {
        Map<String, BigDecimal> montos = new HashMap<String, BigDecimal>();
        for (RelacionGastosDetalle renglon : detalle) {
            // Cambia la fuente de financiamiento
            String epNueva = renglon.getEP().substring(0, 39) + "4.14" + renglon.getEP().substring(43);
            log.debug("Object: {}", "Se cambia la EP original [" + renglon.getEP() + "] por la EP [" + epNueva + "]");
            if (montos.get(epNueva) == null)
                montos.put(epNueva, renglon.getmImporteMasIva());
            else
                montos.put(epNueva, montos.get(epNueva).add(renglon.getmImporteMasIva()));
        }
        return montos;
    }

    private static List<Integer> cargaFolios(String integracion) throws Exception {
        FileReader fr = new FileReader(integracion);
        BufferedReader br = new BufferedReader(fr);
        List<Integer> folios = new ArrayList<Integer>();
        String linea = "";
        while ((linea = br.readLine()) != null) {
            if (!StringUtils.isBlank(linea))
                folios.add(new Integer(StringUtils.trim(linea)));
        }
        fr.close();
        br.close();
        return folios;
    }

    public static Caso cargaCaso(Connection conn, String folioRG) throws Exception {
        String query = "SELECT id_caso " + "FROM   cg_caso " + "WHERE  c_folio LIKE '%-%-' + ? " + "       AND id_tc = 11 ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, folioRG);
            rs = ps.executeQuery();
            int idCaso = -1;
            if (rs.next())
                idCaso = rs.getInt(1);
            else
                throw new Exception("No se encontro caso de RG para el folio: " + folioRG);
            Caso c = new Caso();
            c.setIdCaso(idCaso);
            c = CasoManager.select(conn, c);
            return c;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static Caso generaCasoNuevo(Connection conn, Caso casoOrigen, Usuario u) throws Exception {
        FolioGeneratorInterface fg = null;
        ClassLoader cl = CambiaEPRelacionGastos.class.getClassLoader();
        Class<?> clase = cl.loadClass(GestionInterface.FOLIO_GENERATOR);
        fg = (FolioGeneratorInterface) clase.newInstance();
        Caso c = CasoManager.nuevoCaso(conn, u, 11, fg);
        c.setCasoDato(casoOrigen.getCasoDato());
        c.getCasoDato("FOLIO").setValor(c.getFolio());
        Map<String, String> m = new HashMap<String, String>();
        m.put("FOLIO", c.getFolio());
        m.put("OPERADOR", u.getLogin());
        m.put("FECHA_DOCUMENTO", c.getCasoDato("FECHA_DOCUMENTO").getValor());
        m.put("EJERCICIO_FISCAL", c.getCasoDato("EJERCICIO_FISCAL").getValor());
        Aplicacion app = AplicacionManager.select(conn, c.getTipoCaso().getGavetaAsociada());
        int id_gabinete = AplicacionManager.createExpediente(conn, u.getLogin(), c, app);
        c.setIdGabinete(id_gabinete);
        CasoManager.update(conn, c);
        TipoCasoInterface tci = null;
        if (c.getTipoCaso().tieneInterface()) {
            tci = Util.instanceTipoCasoInterface(c.getTipoCaso().getInterface());
            tci.onCreateExpediente(conn, u.getLogin(), c, app);
        }
        CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), m);
        return c;
    }

    private static int insertaCarpetas(Connection conn, int idGabineteOrigen, int idGabineteDestino) throws Exception {
        String query = "INSERT INTO imx_carpeta(TITULO_APLICACION, ID_GABINETE, ID_CARPETA, NOMBRE_CARPETA, NOMBRE_USUARIO, BANDERA_RAIZ, FH_CREACION, FH_MODIFICACION, NUMERO_ACCESOS, NUMERO_CARPETAS, NUMERO_DOCUMENTOS, DESCRIPCION, PASSWORD)" + "SELECT	DISTINCT carpeta.TITULO_APLICACION ," + "        " + idGabineteDestino + " AS  ID_GABINETE ," + "        carpeta.ID_CARPETA ," + "        carpeta.NOMBRE_CARPETA ," + "        carpeta.NOMBRE_USUARIO ," + "        carpeta.BANDERA_RAIZ ," + "        carpeta.FH_CREACION ," + "        carpeta.FH_MODIFICACION ," + "        carpeta.NUMERO_ACCESOS ," + "        carpeta.NUMERO_CARPETAS ," + "        carpeta.NUMERO_DOCUMENTOS ," + "        carpeta.DESCRIPCION ," + "        carpeta.PASSWORD " + "  FROM	imx_pagina pagina WITH(NOLOCK) " + "		INNER JOIN " + "		IMX_DOCUMENTO documento WITH(NOLOCK) " + "		ON pagina.TITULO_APLICACION = documento.TITULO_APLICACION " + "		AND pagina.ID_GABINETE = documento.ID_GABINETE " + "		AND pagina.ID_CARPETA_PADRE = documento.ID_CARPETA_PADRE " + "		AND pagina.ID_DOCUMENTO = documento.ID_DOCUMENTO " + "		INNER JOIN " + "		IMX_CARPETA carpeta" + "		ON documento.TITULO_APLICACION = carpeta.TITULO_APLICACION " + "		AND documento.ID_GABINETE = carpeta.ID_GABINETE " + "		AND documento.ID_CARPETA_PADRE = carpeta.ID_CARPETA " + "		INNER JOIN dbo.IMX_ORG_CARPETA orgCarpeta " + "		ON carpeta.TITULO_APLICACION = orgCarpeta.TITULO_APLICACION " + "		AND carpeta.ID_GABINETE = orgCarpeta.ID_GABINETE " + "		AND carpeta.ID_CARPETA = orgCarpeta.ID_CARPETA_HIJA " + " WHERE	pagina.TITULO_APLICACION = 'RELACIONGASTOS' " + "   AND	pagina.ID_GABINETE =  " + idGabineteOrigen + "   AND	documento.ID_CARPETA_PADRE <> 0 " + "   AND NOMBRE_CARPETA <> 'SOLICITUD DE PAGO' " + "   AND carpeta.NOMBRE_CARPETA NOT IN ( " + "   		SELECT NOMBRE_CARPETA FROM IMX_CARPETA WHERE TITULO_APLICACION = 'RELACIONGASTOS' AND ID_GABINETE = " + idGabineteDestino + ")";
        PreparedStatement ps = null;
        try {
            log.debug("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            int afectados = ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    private static int insertaDocumentos(Connection conn, int idGabineteOrigen, int idGabineteDestino) throws Exception {
        String query = "INSERT INTO dbo.IMX_DOCUMENTO (TITULO_APLICACION, ID_GABINETE, ID_CARPETA_PADRE, ID_DOCUMENTO, NOMBRE_DOCUMENTO, NOMBRE_USUARIO, PRIORIDAD, ID_TIPO_DOCTO, FH_CREACION, FH_MODIFICACION, NUMERO_ACCESOS, NUMERO_PAGINAS, TITULO, AUTOR, MATERIA, DESCRIPCION, CLASE_DOCUMENTO, ESTADO_DOCUMENTO, TAMANO_BYTES, COMPARTIR, TOKEN_COMPARTIR, FH_VIGENCIA, iEsVersion)" + " SELECT	DISTINCT documento.TITULO_APLICACION ," + "        " + idGabineteDestino + " AS ID_GABINETE ," + "        documento.ID_CARPETA_PADRE ," + "        documento.ID_DOCUMENTO ," + "        documento.NOMBRE_DOCUMENTO ," + "        documento.NOMBRE_USUARIO ," + "        documento.PRIORIDAD ," + "        documento.ID_TIPO_DOCTO ," + "        documento.FH_CREACION ," + "        documento.FH_MODIFICACION ," + "        documento.NUMERO_ACCESOS ," + "        documento.NUMERO_PAGINAS ," + "        documento.TITULO ," + "        documento.AUTOR ," + "        documento.MATERIA ," + "        documento.DESCRIPCION ," + "        documento.CLASE_DOCUMENTO ," + "        documento.ESTADO_DOCUMENTO ," + "        documento.TAMANO_BYTES ," + "        documento.COMPARTIR ," + "        documento.TOKEN_COMPARTIR ," + "        documento.FH_VIGENCIA ," + "        documento.iEsVersion" + "  FROM	imx_pagina pagina WITH(NOLOCK) " + "		INNER JOIN " + "		IMX_DOCUMENTO documento WITH(NOLOCK)" + "		ON pagina.TITULO_APLICACION = documento.TITULO_APLICACION " + "		AND pagina.ID_GABINETE = documento.ID_GABINETE " + "		AND pagina.ID_CARPETA_PADRE = documento.ID_CARPETA_PADRE " + "		AND pagina.ID_DOCUMENTO = documento.ID_DOCUMENTO" + "		INNER JOIN " + "		IMX_CARPETA carpeta" + "		ON documento.TITULO_APLICACION = carpeta.TITULO_APLICACION " + "		AND documento.ID_GABINETE = carpeta.ID_GABINETE " + "		AND documento.ID_CARPETA_PADRE = carpeta.ID_CARPETA" + "		INNER JOIN dbo.IMX_ORG_CARPETA orgCarpeta" + "		ON carpeta.TITULO_APLICACION = orgCarpeta.TITULO_APLICACION " + "		AND carpeta.ID_GABINETE = orgCarpeta.ID_GABINETE " + "		AND carpeta.ID_CARPETA = orgCarpeta.ID_CARPETA_HIJA" + " WHERE	pagina.TITULO_APLICACION = 'RELACIONGASTOS' " + "   AND	pagina.ID_GABINETE = " + idGabineteOrigen + "   AND	documento.ID_CARPETA_PADRE <> 0" + "   AND NOMBRE_CARPETA <> 'SOLICITUD DE PAGO' " + "   AND documento.NOMBRE_DOCUMENTO NOT IN( SELECT NOMBRE_DOCUMENTO FROM imx_documento d " + "                                           WHERE d.TITULO_APLICACION = 'RELACIONGASTOS' " + "                                             AND d.ID_GABINETE = " + idGabineteDestino + "                                             AND d.ID_CARPETA_PADRE = documento.ID_CARPETA_PADRE)";
        PreparedStatement ps = null;
        try {
            log.debug("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            int afectados = ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    private static int insertaOrgCarpetas(Connection conn, int idGabineteOrigen, int idGabineteDestino) throws Exception {
        String query = "insert into imx_org_carpeta(TITULO_APLICACION, ID_GABINETE, ID_CARPETA_HIJA, ID_CARPETA_PADRE, NOMBRE_HIJA)" + "SELECT  DISTINCT" + "        orgCarpeta.TITULO_APLICACION ," + "        ? ID_GABINETE ," + "        orgCarpeta.ID_CARPETA_HIJA ," + "        orgCarpeta.ID_CARPETA_PADRE ," + "        orgCarpeta.NOMBRE_HIJA" + "  FROM	imx_pagina pagina WITH(NOLOCK) " + "		INNER JOIN " + "		IMX_DOCUMENTO documento WITH(NOLOCK)" + "		ON pagina.TITULO_APLICACION = documento.TITULO_APLICACION " + "		AND pagina.ID_GABINETE = documento.ID_GABINETE " + "		AND pagina.ID_CARPETA_PADRE = documento.ID_CARPETA_PADRE " + "		AND pagina.ID_DOCUMENTO = documento.ID_DOCUMENTO" + "		INNER JOIN " + "		IMX_CARPETA carpeta" + "		ON documento.TITULO_APLICACION = carpeta.TITULO_APLICACION " + "		AND documento.ID_GABINETE = carpeta.ID_GABINETE " + "		AND documento.ID_CARPETA_PADRE = carpeta.ID_CARPETA" + "		INNER JOIN dbo.IMX_ORG_CARPETA orgCarpeta" + "		ON carpeta.TITULO_APLICACION = orgCarpeta.TITULO_APLICACION " + "		AND carpeta.ID_GABINETE = orgCarpeta.ID_GABINETE " + "		AND carpeta.ID_CARPETA = orgCarpeta.ID_CARPETA_HIJA" + " WHERE	pagina.TITULO_APLICACION = 'RELACIONGASTOS' " + "   AND	pagina.ID_GABINETE = ?" + "   AND	documento.ID_CARPETA_PADRE <> 0" + "   AND NOMBRE_CARPETA <> 'SOLICITUD DE PAGO' " + "   AND carpeta.NOMBRE_CARPETA NOT IN ( " + "   		SELECT NOMBRE_HIJA FROM IMX_ORG_CARPETA WHERE TITULO_APLICACION = 'RELACIONGASTOS' AND ID_GABINETE = " + idGabineteDestino + ")";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, idGabineteDestino);
            ps.setInt(2, idGabineteOrigen);
            int afectados = ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static Caso avanzaCaso(Connection conn, Caso c, String u_login, String observ, String[] resp, String[] oper, Map<String, String> data, String pathPrefix) throws Exception {
        Caso rco = null;
        boolean delete = true;
        if (resp.length != oper.length) {
            log.error("Object: {}", "Las dimensiones no coinciden rep[" + resp.length + "] vs oper[" + oper.length + "]");
            throw new GestionException("Las dimensiones no coinciden rep[" + resp.length + "] vs oper[" + oper.length + "]");
        }
        int[] idCasoOperSgte = new int[resp.length];
        CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), data);
        for (int i = 0; i < resp.length; i++) {
            if (CasoBusinessLogic.OPER_CONTINUE.equalsIgnoreCase(oper[i].trim())) {
                c.setStatus(100);
                CasoManager.update(conn, c);
                CasoOperacionManager.updateObservacion(conn, c, observ);
                delete = false;
                continue;
            } else if (CasoBusinessLogic.CASO_END.equalsIgnoreCase(oper[i].trim())) {
                delete = false;
                for (int j = i + 1; j < idCasoOperSgte.length; j++) idCasoOperSgte[j] = -1;
                CasoManager.terminaCaso(conn, c, resp, oper);
                TipoCasoInterface tci = null;
                if (c.getTipoCaso().tieneInterface()) {
                    tci = Util.instanceTipoCasoInterface(c.getTipoCaso().getInterface());
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
                tci = Util.instanceTipoCasoInterface(c.getTipoCaso().getInterface());
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

    private static void notificaFirma(Connection conn, RelacionGastosEncabezado encabezado, Usuario u, String reportPath) throws Exception {
        SolicitudFirmaElectronica solicitudPagoPrinter = new SolicitudPagoFirmaElectronica();
        solicitudPagoPrinter.setDetail("tRelacionGastosDetalle");
        solicitudPagoPrinter.setDocument("RELACIONGASTOS");
        solicitudPagoPrinter.setField("nFolioRelacionGastos");
        solicitudPagoPrinter.setFileExtension("pdf");
        solicitudPagoPrinter.setHeader("tRelacionGastosEncabezado");
        solicitudPagoPrinter.setIdField(encabezado.getnFolioRELACIONGASTOS());
        solicitudPagoPrinter.setReportPath(reportPath);
        solicitudPagoPrinter.setUsuario(u);
        String lastDocName = solicitudPagoPrinter.getDocName();
        try {
            solicitudPagoPrinter.setDocName("Solicitud de Pago Firmada");
            FirmaElectronicaManager.generaArchivoFirma(conn, solicitudPagoPrinter, "Solicitud de Pago", false);
            solicitudPagoPrinter.notificaOperacionPendiente(conn, SolicitudFirmaElectronica.VO_BO);
        } finally {
            solicitudPagoPrinter.setDocName(lastDocName);
        }
        FirmaElectronicaManager.actualizaMetodoAutorizacion(conn, "tRelacionGastosEncabezado", "nFolioRelacionGastos", String.valueOf(encabezado.getnFolioRELACIONGASTOS()), true);
    }

    private static int copiaFacturas(Connection conn, int nFolioRGOrigen, int nFolioRGDestino) throws Exception {
        String queryFacturas = "INSERT INTO tPagoFactura" + "        ( cTipoPago ," + "          nFolioPago ," + "          cfactura ," + "          mImporteBruto ," + "          mimporteconiva ," + "          mimporteiva ," + "          cRFCFactura ," + "          mOtrosImpuestos ," + "          cEsNotaCredito ," + "          mImporteDescuento ," + "          cMetodoPago" + "        )" + "SELECT	cTipoPago ," + "        ? AS nFolioPago ," + "        cfactura ," + "        mImporteBruto ," + "        mimporteconiva ," + "        mimporteiva ," + "        cRFCFactura ," + "        mOtrosImpuestos ," + "        cEsNotaCredito ," + "        mImporteDescuento ," + "        cMetodoPago " + "  FROM	tPagoFactura" + " WHERE	cTipoPago = 'RELACIONGASTOS' " + "   AND	nFolioPago = ?";
        String queryRetenciones = "INSERT INTO tPagoFacturaRetencion(" + "			cTipoPago ," + "			nFolioPago ," + "			UUID ," + "			cNombreRetencion ," + "			mImporteRetencion)" + "SELECT	cTipoPago ," + "        ? AS nFolioPago ," + "        UUID ," + "        cNombreRetencion ," + "        mImporteRetencion" + "  FROM	tPagoFacturaRetencion" + " WHERE	cTipoPago = 'relaciongastos' " + "   AND	nFolioPago = ?";
        String queryImpuestos = "INSERT INTO tPagoFacturaImpuestos" + "        ( cTipoPago ," + "          nFolioPago ," + "          UUID ," + "          cNombreImpuesto ," + "          mImporteImpuesto ," + "          nTazaImpuesto" + "        )" + "SELECT	cTipoPago ," + "        ? AS nFolioPago ," + "        UUID ," + "        cNombreImpuesto ," + "        mImporteImpuesto ," + "        nTazaImpuesto " + "  FROM tPagoFacturaImpuestos" + " WHERE	cTipoPago = 'relaciongastos' " + "   AND	nFolioPago = ?";
        PreparedStatement psFactura = null;
        PreparedStatement psRetenciones = null;
        PreparedStatement psImpuestos = null;
        int afectados = 0;
        try {
            psFactura = conn.prepareStatement(queryFacturas);
            psRetenciones = conn.prepareStatement(queryRetenciones);
            psImpuestos = conn.prepareStatement(queryImpuestos);
            psFactura.setInt(1, nFolioRGDestino);
            psFactura.setInt(2, nFolioRGOrigen);
            psRetenciones.setInt(1, nFolioRGDestino);
            psRetenciones.setInt(2, nFolioRGOrigen);
            psImpuestos.setInt(1, nFolioRGDestino);
            psImpuestos.setInt(2, nFolioRGOrigen);
            afectados += psFactura.executeUpdate();
            afectados += psRetenciones.executeUpdate();
            afectados += psImpuestos.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(psFactura);
            CloseObject.closeObject(psRetenciones);
            CloseObject.closeObject(psImpuestos);
        }
    }

    private static int actualizaVuelos(Connection conn, int nFolioRGOrigen, int nFolioRGDestino) throws Exception {
        String queryActualizaVuelos = "UPDATE tInfoBoleto SET nFolioRelacionGastos = ? WHERE nFolioRelacionGastos = ?";
        PreparedStatement psActualizaVuelos = null;
        int afectados = 0;
        try {
            psActualizaVuelos = conn.prepareStatement(queryActualizaVuelos);
            psActualizaVuelos.setInt(1, nFolioRGDestino);
            psActualizaVuelos.setInt(2, nFolioRGOrigen);
            afectados += psActualizaVuelos.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(psActualizaVuelos);
        }
    }

    private static int limpiaFacturasOrigen(Connection conn, int nFolioRGOrigen) throws Exception {
        String queryFacturas = "DELETE FROM tPagoFactura WHERE	cTipoPago = 'RELACIONGASTOS' " + "   AND	nFolioPago = ?";
        String queryRetenciones = "DELETE FROM tPagoFacturaRetencion WHERE	cTipoPago = 'RELACIONGASTOS' " + "   AND	nFolioPago = ?";
        String queryImpuestos = "DELETE FROM tPagoFacturaImpuestos WHERE	cTipoPago = 'RELACIONGASTOS' " + "   AND	nFolioPago = ?";
        PreparedStatement psFactura = null;
        PreparedStatement psRetenciones = null;
        PreparedStatement psImpuestos = null;
        int afectados = 0;
        try {
            psFactura = conn.prepareStatement(queryFacturas);
            psRetenciones = conn.prepareStatement(queryRetenciones);
            psImpuestos = conn.prepareStatement(queryImpuestos);
            psFactura.setInt(1, nFolioRGOrigen);
            psRetenciones.setInt(1, nFolioRGOrigen);
            psImpuestos.setInt(1, nFolioRGOrigen);
            afectados += psFactura.executeUpdate();
            afectados += psRetenciones.executeUpdate();
            afectados += psImpuestos.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(psFactura);
            CloseObject.closeObject(psRetenciones);
            CloseObject.closeObject(psImpuestos);
        }
    }

    private static final boolean esRelacionReemplazada(Connection conn, int nFolioRGOriginal) throws Exception {
        String query = "SELECT nFolioRelacionGastosNuevo FROM tRelacionGastosReemplazo WITH(NOLOCK) WHERE nFolioRelacionGastosOriginal = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, nFolioRGOriginal);
            rs = ps.executeQuery();
            if (rs.next())
                return true;
            else
                return false;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    private static final int insertaRelacionReemplazo(Connection conn, int nFolioRGOriginal, int nFolioRGNuevo) throws Exception {
        String query = "INSERT INTO tRelacionGastosReemplazo( nFolioRelacionGastosOriginal ,nFolioRelacionGastosNuevo)VALUES  ( ?, ? )";
        PreparedStatement ps = null;
        int afectados = 0;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, nFolioRGOriginal);
            ps.setInt(2, nFolioRGNuevo);
            afectados = ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    private static int insertaContrarrecibo(Connection conn, String cxpOrigen, String cxpDestino) throws Exception {
        String query = "INSERT INTO dbo.tContrarrecibo( aEjercicioFiscal, cIdEntidadContable, caNoContrarrecibo, cIdDocumento, " + " cIdTipoDocumento, cIdSubtipoDocumento, cIdTipoOperacion, caNoAP, nPolizaAP, cIdRFC, cIdTipoMoneda, " + " nTipoCambio, mImporteBruto, mImporteSancion, mImporteDevolucion, mAmortizacionAnticipo, mImporteIVA, " + " mImporteRetencion, mImportePenalizacion, mImporteNeto, fProgramadaPago, fValidacionDisponible, " + " lEsCandidatoCP, lHaySubpartidaExcluidaCP, cIdTipoCLCSiaff, cIdLeyendaSiaff, cReferencia1, nEsSubsidio, " + " fRegistro, cIdEstadoReintegro, cIdTipoReclasificacion, fAplicacionSiaff, fPagoSiaff, " + " cIdEstadoContrarrecibo, mOtrosImpuestos )" + "SELECT	aEjercicioFiscal ," + "		cIdEntidadContable ," + "		? AS caNoContrarrecibo ," + "		cIdDocumento ," + "		cIdTipoDocumento ," + "		cIdSubtipoDocumento ," + "		cIdTipoOperacion ," + "		caNoAP ," + "		nPolizaAP ," + "		cIdRFC ," + "		cIdTipoMoneda ," + "		nTipoCambio ," + "		mImporteBruto ," + "		mImporteSancion ," + "		mImporteDevolucion ," + "		mAmortizacionAnticipo ," + "		mImporteIVA ," + "		mImporteRetencion ," + "		mImportePenalizacion ," + "		mImporteNeto ," + "		fProgramadaPago ," + "		fValidacionDisponible ," + "		lEsCandidatoCP ," + "		lHaySubpartidaExcluidaCP ," + "		cIdTipoCLCSiaff ," + "		cIdLeyendaSiaff ," + "		cReferencia1 ," + "		nEsSubsidio ," + "		fRegistro ," + "		cIdEstadoReintegro ," + "		cIdTipoReclasificacion ," + "		fAplicacionSiaff ," + "		fPagoSiaff ," + "		cIdEstadoContrarrecibo ," + "		mOtrosImpuestos " + "  FROM	tContrarrecibo" + " WHERE	caNoContrarrecibo = ?";
        PreparedStatement ps = null;
        int afectados = 0;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, cxpDestino);
            ps.setString(2, cxpOrigen);
            afectados = ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    private static int insertaDocComp(Connection conn, String cxpOrigen, String cxpDestino) throws Exception {
        String query = "INSERT INTO tDocumentacionComprobatoriaDet( cEjercicio, cIdEntidadContable, caNoContrarrecibo, " + "nConsecutivo, ID_RAMO, DCD_FACTURA, DCD_FECHA_FACTURA, DCD_FECHA_RECEPCION, DCD_CBEN, DCD_TBEN, " + "DCD_TIPO_OPE, DCD_TIVA, DCD_VALOR, DCD_IMP_BRUTO, DCD_IVADES, DCD_ISR, DCD_IVA, DCD_MIL5, " + "DCD_MIL2, DCD_OTRAS_RET, DCD_PENALIZACION, DCD_CONTRIBUCION, DCD_CTOEXT, DCD_CONCEPTO, DCD_TODAY)" + "SELECT	cEjercicio ," + "		cIdEntidadContable ," + "		? caNoContrarrecibo ," + "		nConsecutivo ," + "		ID_RAMO ," + "		DCD_FACTURA ," + "		fAplicacion ," + "		fRecepcion ," + "		DCD_CBEN ," + "		DCD_TBEN ," + "		DCD_TIPO_OPE ," + "		DCD_TIVA ," + "		DCD_VALOR ," + "		DCD_IMP_BRUTO ," + "		DCD_IVADES ," + "		DCD_ISR ," + "		DCD_IVA ," + "		DCD_MIL5 ," + "		DCD_MIL2 ," + "		DCD_OTRAS_RET ," + "		DCD_PENALIZACION ," + "		DCD_CONTRIBUCION ," + "		DCD_CTOEXT ," + "		cConcepto ," + "		DCD_TODAY " + "  FROM	v_pagosDocComprobatoria" + " WHERE	caNoContrarrecibo = ?";
        PreparedStatement ps = null;
        int insertados = 0;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, cxpDestino);
            ps.setString(2, cxpOrigen);
            insertados = ps.executeUpdate();
            return insertados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    private static void cancelaDevengado(Connection conn, Usuario usuario, int nFolioDocumento, String centroContable) throws Exception {
        PreparedStatement pstmnEnc = null;
        PreparedStatement pstmDoc = null;
        PreparedStatement pstmnInse = null;
        PreparedStatement pstmnCaja = null;
        PreparedStatement pstmn = null;
        ResultSet rsEnc = null;
        ResultSet rsDoc = null;
        ResultSet rsCaja = null;
        ResultSet rs = null;
        AccountingEngine accEng = new AccountingEngine();
        try {
            String importeNeto = "mImporteNeto";
            String tipoPago = "RELACIONGASTOS";
            if (accEng.cancelAccountingApplication(conn, tipoPago, String.valueOf(nFolioDocumento), "tRelacionGastosEncabezado", "tRelacionGastosDetalle", "nFolioRelacionGastos")) {
                PasivoDiferidoManager.cancelarPasivoDiferido(conn, tipoPago, String.valueOf(nFolioDocumento));
                LogCancelaDevengadoManager.registraLog(conn, tipoPago, Integer.parseInt(String.valueOf(nFolioDocumento), 10), usuario.getLogin());
                FacturaManager.eliminaFacturas(conn, tipoPago, String.valueOf(nFolioDocumento));
                TransporteDAO.updateEstatusBoletos(conn, nFolioDocumento);
                TransporteDAO.borrarTaxi(conn, nFolioDocumento, tipoPago);
                String tipoDoc = "RELG";
                String idRFC = "cIdRFC";
                String sqlstmnt = "";
                sqlstmnt = "SELECT '" + tipoDoc + "-'+RTRIM(cUnidadResponsable)+'-" + String.valueOf(nFolioDocumento) + "' AS NumeroFolio," + "      	caNoContrarrecibo, " + "      	" + idRFC + " AS RFC, " + "      	dNombre AS nomRFC, " + "      	fAplicacion, " + "      	" + importeNeto + " AS mImporteNeto " + "  FROM	t" + tipoPago + "Encabezado with(nolock), " + "      	tBeneficiario tb with(nolock) " + " WHERE " + idRFC + " = tb.dRFC AND nFolio" + tipoPago + " = ? ";
                pstmDoc = conn.prepareStatement(sqlstmnt);
                pstmDoc.setString(1, String.valueOf(nFolioDocumento));
                rsDoc = pstmDoc.executeQuery();
                if (rsDoc.next()) {
                    java.util.Date utilDate = new java.util.Date();
                    long lnMilisegundos = utilDate.getTime();
                    java.sql.Date sqlDate = new java.sql.Date(lnMilisegundos);
                    String[] fp = String.valueOf(sqlDate).split("-");
                    String FechaRegistro = fp[2] + "-" + fp[1] + "-" + fp[0];
                    String NumeroFolio = rsDoc.getString("NumeroFolio");
                    String caNoContrarrecibo = rsDoc.getString("caNoContrarrecibo");
                    String nomRFC = rsDoc.getString("nomRFC");
                    String fAplicacion = rsDoc.getString("fAplicacion");
                    String mImporte = rsDoc.getString("mImporteNeto");
                    CFSequenceManager seq = CFSequenceManager.getInstance();
                    int nFolioD = seq.nextVal("DEV" + centroContable);
                    pstmnInse = conn.prepareStatement("INSERT INTO tVolante_Devolucion(folioDevolucion,NumeroFolio,FechaRegistro,Documento,noContrarrecibo,Folio,RFC,nomRFC,fAplicacion,importeNeto, MotivoDevolucion, DescripcionDevolucion)VALUES " + "('" + nFolioD + "','" + NumeroFolio + "','" + FechaRegistro + "','" + FechaRegistro + "','" + caNoContrarrecibo + "','" + String.valueOf(nFolioDocumento) + "','" + idRFC + "','" + nomRFC.replace("'", "''") + "','" + fAplicacion + "'," + mImporte + ", 1, 'Cierre Presupuestal Devengado' )");
                    pstmnInse.executeUpdate();
                }
                pstmnEnc = conn.prepareStatement("SELECT nFolioPagoApartado FROM tPagoApartadoEncabezado WITH(NOLOCK) WHERE cTipoPago = ? and nFolioPago = ? and cDocumentoHaplicado = 'S' ");
                pstmnEnc.setString(1, tipoPago);
                pstmnEnc.setString(2, String.valueOf(nFolioDocumento));
                rsEnc = pstmnEnc.executeQuery();
                if (rsEnc.next()) {
                    Integer FolioComprobacion = Integer.parseInt(String.valueOf(nFolioDocumento), 10);
                    nFolioDocumento = rsEnc.getInt("nFolioPagoApartado");
                    tipoPago = "PagoApartado";
                    if (accEng.cancelAccountingApplication(conn, tipoPago, String.valueOf(nFolioDocumento), "t" + tipoPago + "Encabezado", "t" + tipoPago + "Detalle", "nFolio" + tipoPago)) {
                        if (tipoPago.equalsIgnoreCase("RelacionGastos")) {
                            updateEliminaInfoVuelosRG(conn, FolioComprobacion);
                            pstmnCaja = conn.prepareStatement("SELECT nFolioCaja FROM dbo.tRELACIONGASTOSEncabezado WITH (NOLOCK) WHERE nFolioRELACIONGASTOS = ?");
                            pstmnCaja.setInt(1, FolioComprobacion);
                            rsCaja = pstmnCaja.executeQuery();
                            String us = usuario.getLogin();
                            if (rsCaja.next()) {
                                Integer FolioCaja = rsCaja.getInt("nFolioCaja");
                                CajaManager.borraDetalleViaticos(conn, FolioCaja, FolioComprobacion, us);
                            }
                        }
                    }
                }
            }
        } finally {
            CloseObject.closeObject(pstmDoc);
            CloseObject.closeObject(pstmnInse);
            CloseObject.closeObject(pstmnEnc);
            CloseObject.closeObject(pstmn);
            CloseObject.closeObject(rsDoc);
            CloseObject.closeObject(rsEnc);
            CloseObject.closeObject(rs);
        }
    }

    private static boolean updateEliminaInfoVuelosRG(Connection conn, int folio) throws SQLException {
        boolean respuesta = true;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        PreparedStatement psUpdateLayoutVuelos = null;
        PreparedStatement psDeleteInfoVuelos = null;
        try {
            String query = " SELECT CASE WHEN COUNT(*) > 0 THEN '1' ELSE '0' END AS existe FROM tInfoBoleto WITH (NOLOCK) WHERE nFolioRelacionGastos = ? ";
            pstm = conn.prepareStatement(query);
            pstm.setInt(1, folio);
            rs = pstm.executeQuery();
            if (rs.next()) {
                String existe = rs.getString("existe");
                if ("1".equalsIgnoreCase(existe)) {
                    String sUpdateLayoutVuelos = " UPDATE	vuelosdet SET vuelosdet.Status = 'A' " + " FROM		tLayoutVuelosDet vuelosdet WITH (NOLOCK) " + " INNER JOIN tInfoBoleto infoboleto WITH(NOLOCK) " + " ON " + " ( " + "	infoboleto.cNumeroBoleto = vuelosdet.cReferencia " + "	AND infoboleto.RFCVuelo = vuelosdet.RFC " + "	AND infoboleto.cNombreRFC = vuelosdet.cNombre " + "	AND infoboleto.mImporteBoleto = vuelosdet.mTotal " + " ) " + " WHERE infoboleto.nFolioRelacionGastos = ? ";
                    String sDeleteInfoVuelos = " DELETE FROM tInfoBoleto WHERE nFolioRelacionGastos = ? ";
                    psUpdateLayoutVuelos = conn.prepareStatement(sUpdateLayoutVuelos);
                    psUpdateLayoutVuelos.setInt(1, folio);
                    psDeleteInfoVuelos = conn.prepareStatement(sDeleteInfoVuelos);
                    psDeleteInfoVuelos.setInt(1, folio);
                    int updateVuelos = psUpdateLayoutVuelos.executeUpdate();
                    log.info("Object: {}", "Se Actualizaron: " + updateVuelos + " Vuelos.");
                    int deleteVuelos = psDeleteInfoVuelos.executeUpdate();
                    log.info("Object: {}", "Se eliminaron: " + deleteVuelos + " Vuelos.");
                }
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstm);
            CloseObject.closeObject(psUpdateLayoutVuelos);
            CloseObject.closeObject(psDeleteInfoVuelos);
        }
        return respuesta;
    }

    private static void notificaOperador(Connection conn, Map<String, List<RelacionGastosEncabezado[]>> usuarioTramite) throws Exception {
        String correo = "<html>" + "<head>" + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">" + "<style type=\"text/css\"> " + "	body { " + "			font-family: verdana, arial, sans-serif; " + "			font-size: 12px; " + "	} " + "	table { " + "		font-size: 12px; " + "		color: #333333; " + "		border-width: 1px; " + "		border-color: #666666; " + "		border-collapse: collapse; " + "	} " + "	table th { " + "		border-width: 1px; " + "		padding: 8px; " + "		border-style: solid; " + "		border-color: #666666; " + "		background-color: #dedede; " + "	} " + "	table td { " + "		border-width: 1px; " + "		padding: 8px; " + "		border-style: solid; " + "		border-color: #666666; " + "		background-color: #ffffff; " + "	} " + "	</style>" + "</head>" + "<body>" + "<br>" + "<div>" + "<form id=\"Form\" name=\"FormViaticos\">" + "<b>Atencion!</b> <br>" + "<p>Se hace de su conocimiento que debido al <b>ambiente controlado </b> aplicado por la SHCP sus solicitudes de pago fueron reemplazadas <b>de gasto fiscal a recurso propio</b>. En la tabla siguiente encontrará el pago con su respectivo remplazo el cual tendrá que revisar y firmar electrónica en Visto Bueno y Autorización. . </p>" + "<table>" + "<thead>" + "<tr>" + "<th>Cuenta Por Pagar Original</th>" + "<th>Cuenta Por Pagar Nueva</th>" + "</tr>" + "</thead>" + "<tbody>";
        for (Iterator<String> iter = usuarioTramite.keySet().iterator(); iter.hasNext(); ) {
            String uLogin = iter.next();
            List<RelacionGastosEncabezado[]> r = usuarioTramite.get(uLogin);
            for (int i = 0; i < r.size(); i++) {
                RelacionGastosEncabezado original = r.get(i)[0];
                RelacionGastosEncabezado nueva = r.get(i)[1];
                correo = correo + "<tr>" + "<td>" + original.getCaNoContrarrecibo() + "</td>" + "<td>" + nueva.getCaNoContrarrecibo() + "</td>" + "</tr>";
            }
        }
        correo = correo + "</tbody>" + "</table>" + "<br>" + "<br>" + "<b>Por favor realizar seguimiento a los tramites.</b> <br>" + "<br>" + "<p>Notificaciones Automaticas<br>" + "Sistema de Administracion Integral<br>" + Util.getTodayESMX() + "</p>" + "</form>" + "</body>" + "</html>";
        AlarmaManager.procesaAlarmaCNF(conn, "", null, null, "Notificacion de pagos reemplazados", "bherreraa@conafor.gob.mx;earellano@conafor.gob.mx;vgarciac@axtel.com.mx", correo);
    }
}
