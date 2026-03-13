package com.syc.adquisiciones.businessLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import com.axtel.contratos.ContractStatus;
import com.axtel.contratos.EventosPresupuestales;
import com.syc.adquisiciones.core.ContratoModificado;
import com.syc.adquisiciones.manager.ContratoModificadoManager;
import com.syc.adquisiciones.util.Util;
import com.syc.contable.AccountingEngine;
import com.syc.contable.ContableInterface;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContratoModificadoBusiness extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(ContratoModificadoBusiness.class);

    public String savePartidas(String jndiName, ContratoModificado contMod, Usuario usuario) throws Exception {
        Connection conn = null;
        ContratoModificadoManager manager = null;
        StringBuilder msg = new StringBuilder();
        String[] arrayTabla = null;
        ArrayList<List<String>> tabla = null;
        List<String> fila = null;
        Iterator<List<String>> itr = null;
        double montoOriginal = 0;
        double montoModificado = 0;
        double montoModificadoTotal = 0;
        double pu = 0.0;
        int cantidadModificada = 0;
        int cantidadOriginal = 0;
        String token = "";
        boolean isServicio = false;
        try {
            if (null == contMod.getCadTabla() || "".equalsIgnoreCase(contMod.getCadTabla())) {
                throw new Exception("No se recibieron los datos de la tabla \"PARTIDAS MODIFICADAS CONVENIO DE REDUCCI\u00d3N\".");
            }
            arrayTabla = contMod.getCadTabla().split(",");
            tabla = Util.creaArray(arrayTabla);
            conn = getConnection(jndiName);
            manager = new ContratoModificadoManager();
            itr = tabla.iterator();
            /* Posiciones de la fila
			 * 0.- Linea de consolidado
			 * 1.- Monto con IVA original de la partida del contrato
			 * 2.- IVA
			 * 3.- Cantidad a Modificar
			 * 4.- Precio Unitario
			 * 5.- Monto con IVA capturado, monto a reducir
			 * 6.- Cantidad Original
			 * */
            if ("CV".equalsIgnoreCase(contMod.getcIdContrato().substring(0, 2))) {
                //Servicios
                isServicio = true;
            }
            while (itr.hasNext()) {
                fila = new ArrayList<String>();
                fila = itr.next();
                if (isServicio) {
                    //Servicios
                    montoOriginal = Double.parseDouble(fila.get(1).replace(",", ""));
                    montoModificado = Double.parseDouble((fila.get(5).replace(",", "")).replace("$", ""));
                    montoModificado = Math.abs(montoModificado);
                    pu = Double.parseDouble((fila.get(4).replace(",", "")).replace("$", ""));
                    fila.set(5, "" + montoModificado);
                    fila.set(4, "" + (Math.abs(pu)));
                    //Validar que no se redusca mas del 10% por partida de contrato
                    if (montoModificado > (montoOriginal * 10.0 / 100.0)) {
                        msg.append(token + "La partida de contrato número " + fila.get(0) + " no es posible guardar la modificación porque pasa del 10%");
                        token = "\n";
                        fila = null;
                        continue;
                    }
                } else {
                    //Bienes
                    cantidadModificada = Integer.parseInt(fila.get(3));
                    cantidadOriginal = Integer.parseInt(fila.get(6));
                    //Validar que no se redusca mas del 10% por partida de contrato
                    if (cantidadModificada > Math.floor(cantidadOriginal * 10.0 / 100.0)) {
                        msg.append(token + "La partida de contrato número " + fila.get(0) + " no es posible guardar la modificación porque pasa del 10%");
                        token = "\n";
                        fila = null;
                        continue;
                    }
                }
                montoModificadoTotal += montoModificado;
                manager.updatePartidaContMod(conn, contMod, fila);
                msg.append(token + "La partida de contrato número " + fila.get(0) + " fue actualizada.");
                token = "\n";
                fila = null;
            }
            contMod.setmTotalModificacion(montoModificadoTotal);
            manager.updateContMod(conn, contMod);
            Util.bitacoraMovimientos(contMod.getcIdContratoDefinitivo(), "Actualiza partidas.", usuario.getLogin(), conn);
            conn.commit();
            return msg.toString();
        } catch (SQLException e) {
            conn.rollback();
            log.error(e);
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            manager = null;
            conn = null;
            arrayTabla = null;
            tabla = null;
            fila = null;
            itr = null;
            token = null;
        }
    }

    public String deletePartida(String jndiName, ContratoModificado contMod, int nidlineaConsolidado, Usuario usuario) throws Exception {
        Connection conn = null;
        ContratoModificadoManager manager = null;
        String msg = null;
        double montoModPartida = 0.0;
        try {
            conn = getConnection(jndiName);
            manager = new ContratoModificadoManager();
            //Se consulta el monto modificado de la martida de contrato
            montoModPartida = manager.queryMontoModificadoLinea(conn, contMod, nidlineaConsolidado);
            //delete partida d contrato
            manager.deletePartidaContMod(conn, contMod, nidlineaConsolidado);
            //Se actualiza el totalModificado
            contMod.setmTotalModificacion(montoModPartida);
            manager.updateContModDeletePart(conn, contMod);
            //Se guarda el movimiento
            Util.bitacoraMovimientos(contMod.getcIdContratoDefinitivo(), "Partida \"+nidlineaConsolidado+\" de contrato eliminada", usuario.getLogin(), conn);
            conn.commit();
            msg = "Partida " + nidlineaConsolidado + " de contrato eliminada";
            return msg;
        } catch (SQLException e) {
            conn.rollback();
            log.error(e);
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            manager = null;
            conn = null;
        }
    }

    public String deleteAllPartidas(String jndiName, ContratoModificado contMod, Usuario usuario) throws Exception {
        Connection conn = null;
        ContratoModificadoManager manager = null;
        String msg = null;
        double montoModPartida = 0.0;
        try {
            conn = getConnection(jndiName);
            manager = new ContratoModificadoManager();
            //delete partida d contrato
            manager.deleteAllPartidasContMod(conn, contMod);
            //Se actualiza el totalModificado
            contMod.setmTotalModificacion(montoModPartida);
            manager.updateContMod(conn, contMod);
            //Se guarda el movimiento
            Util.bitacoraMovimientos(contMod.getcIdContratoDefinitivo(), "Partidas eliminadas", usuario.getLogin(), conn);
            conn.commit();
            msg = "Partidas eliminadas";
            return msg;
        } catch (SQLException e) {
            conn.rollback();
            log.error(e);
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            manager = null;
            conn = null;
        }
    }

    public String addPartidaDeContrato(String jndiName, ContratoModificado contMod, int nidlineaConsolidado, Usuario usuario) throws Exception {
        Connection conn = null;
        ContratoModificadoManager manager = null;
        String msg = null;
        String cejercicioAct = "";
        StringBuilder lineaConsolidado = null;
        try {
            lineaConsolidado = new StringBuilder();
            lineaConsolidado.append(" and nidlineaconsolidado =");
            if (nidlineaConsolidado <= 0) {
                throw new Exception("No se recibi\u00f3 la partida de contrato.");
            }
            lineaConsolidado.append(nidlineaConsolidado);
            conn = getConnection(jndiName);
            manager = new ContratoModificadoManager();
            cejercicioAct = Util.obtieneEjercicioFiscalActivo(conn);
            contMod.setcEjercicio(cejercicioAct);
            //Agregar la partida
            manager.addPartidasContrato(conn, contMod, lineaConsolidado.toString());
            msg = "Partida " + nidlineaConsolidado + " de contrato agregada";
            Util.bitacoraMovimientos(contMod.getcIdContratoDefinitivo(), msg, usuario.getLogin(), conn);
            conn.commit();
            return msg;
        } catch (SQLException e) {
            conn.rollback();
            log.error(e);
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            manager = null;
            conn = null;
            lineaConsolidado = null;
        }
    }

    public String addAllPartidas(String jndiName, ContratoModificado contMod, Usuario usuario) throws Exception {
        Connection conn = null;
        ContratoModificadoManager manager = null;
        String msg = null;
        String cejercicioAct = "";
        try {
            conn = getConnection(jndiName);
            manager = new ContratoModificadoManager();
            cejercicioAct = Util.obtieneEjercicioFiscalActivo(conn);
            contMod.setcEjercicio(cejercicioAct);
            //Agregar la partida
            manager.addAllPartidasContrato(conn, contMod);
            msg = "Partidas agregadas";
            Util.bitacoraMovimientos(contMod.getcIdContratoDefinitivo(), msg, usuario.getLogin(), conn);
            conn.commit();
            return msg;
        } catch (SQLException e) {
            conn.rollback();
            log.error(e);
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            manager = null;
            conn = null;
        }
    }

    public JSONArray queryContratoModificado(String jndiName, ContratoModificado contMod) throws Exception {
        Connection conn = null;
        JSONArray arrayObj = null;
        StringBuilder query = null;
        try {
            conn = getConnection(jndiName);
            query = new StringBuilder();
            //datos Guardados
            query.append("SELECT 'Total Anterior: $' + convert(varchar, mTotalAnterior, 1) as lblTotalAnterior ");
            query.append(", 'Total Nuevo: $' + convert(varchar, mTotalNuevo, 1) as lblTotal ");
            query.append(", 'Total Modificación: $' + convert(varchar, mTotalModificacion, 1) as lblTotalModificado ");
            query.append(", mTotalModificacion as cTotalModificacion , mTotalAnterior as totalAnterior ");
            query.append(", round(mTotalModificacion,2) as totalMod, mTotalNuevo as totalNuevo ");
            query.append(", nEstado as nIdEstado, 'ESTATUS: '+cEstado as lblEstadoMod ");
            query.append(",'Porcentaje Modificación Total: ' + convert(varchar,round(mTotalModificacion* 100 / (contMod.mTotalAnterior) , 13), 1) + '%' lblPorcentajeMod ");
            query.append(", tipoMod, 'Tipo de Modificación: ' + catTipoMod.cDescripcion as lblTipoMod ");
            query.append(", contMod.ConsecutivoPRECOMP as nFolioPreCompromiso	,contMod.C_FOLIO_PRE as folioCasoPreCompromiso ");
            query.append(",contMod.cNoConvenio,contMod.bEsXTotalPlu,contMod.cObjetoConvenio objConv ");
            query.append(",'Contrato Definitivo: [[ '+contMod.cIdContratoDefinitivo+' ]]' lblDefinitivo ");
            query.append(",'Total Contrato Original: $'+convert(varchar,mTotalAnterior )lblTotalContratoOriginal  ");
            query.append(", '$' + convert(varchar, mTotalModificacion, 1) as mImporteTotalReduccion ");
            query.append(", '$' + convert(varchar, isnull(presupCap.totalCapturado ,0.0), 1) as mPreComprometer ");
            query.append(", '$' + convert(varchar, isnull(presupCap.totalCapturado*-1 ,0.0)+mTotalModificacion, 1) as difPrecompromiso ");
            query.append("FROM mContratoModificado as contMod WITH (NOLOCK) ");
            query.append("inner join mCatalogoEstadoContrato as catEstCont WITH (NOLOCK) on nEstado = nIdEstado ");
            query.append("inner join mcatalogoTipoMod as catTipoMod WITH (NOLOCK) on contMod.tipoMod=catTipoMod.nIdTipoMod ");
            query.append("left join(select cIdcontratoDefinitivo,nIdconsecutivoMod ");
            query.append(",sum(mes01)+SUM(mes02)+SUM(mes03)+SUM(mes04)+SUM(mes05)+SUM(mes06)+SUM(mes07)+SUM(mes08)+SUM(mes09)+SUM(mes10)+SUM(mes11)+SUM(mes12)totalCapturado ");
            query.append(" from mContratoModificadoPresupuestoCapturado with(Nolock) ");
            query.append("group by cIdcontratoDefinitivo,nIdconsecutivoMod)presupCap ");
            query.append(" on presupCap.cIdcontratoDefinitivo=contMod.cIdcontratoDefinitivo and presupCap.nIdconsecutivoMod=contMod.nConsecutivoModificacion ");
            query.append("WHERE contMod.cIdContratoDefinitivo ='" + contMod.getcIdContratoDefinitivo() + "' and contMod.nConsecutivoModificacion = " + contMod.getnConsecutivoModificacion());
            arrayObj = Util.datGuardados(conn, query.toString());
            conn.commit();
            return arrayObj;
        } catch (SQLException e) {
            conn.rollback();
            log.error(e);
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
    }

    public String guardaCambioUEPartidasContMod(String cIdcontratoDefinitivo, int nConsecutivoMod, ArrayList<List<String>> tabla, Usuario usuario) throws Exception {
        //Validar datos
        String msg = "";
        String token = "";
        boolean resp = true;
        ContratoModificadoManager manager = null;
        List<String> fila = null;
        Iterator<List<String>> itr = null;
        Connection conn = null;
        try {
            if (null == cIdcontratoDefinitivo || "".equals(cIdcontratoDefinitivo)) {
                msg = "El n\u00famero de contrato es un dato requerido";
                token = "\n";
                resp = false;
            }
            if (nConsecutivoMod == 0) {
                msg = msg + token + "No se recibio el consecutivo de modificaci\u00f3n";
                token = "\n";
                resp = false;
            }
            if (tabla.size() == 0) {
                msg = msg + token + "El contrato no tiene partidas";
                resp = false;
            }
            //Guardar la nueva ue de las partidas
            fila = new ArrayList<String>();
            itr = tabla.iterator();
            manager = new ContratoModificadoManager();
            conn = getConnection();
            if (resp) {
                while (itr.hasNext()) {
                    fila = itr.next();
                    //validar si existe la partida
                    if (manager.existePartidaContratoMod(conn, cIdcontratoDefinitivo, nConsecutivoMod, Integer.parseInt(fila.get(5)))) {
                        //actualizar
                        resp = manager.updatePartidaContMod(conn, cIdcontratoDefinitivo, nConsecutivoMod, fila);
                    } else {
                        //insertar
                        resp = manager.insertPartidaContMod(conn, cIdcontratoDefinitivo, nConsecutivoMod, fila);
                    }
                    fila = null;
                    if (!resp) {
                        msg = "Error al guardar el detalle de las partidas de contrato.";
                        break;
                    }
                    fila = new ArrayList<String>();
                }
            }
            //Guardar bitacora
            Util.bitacoraMovimientos(cIdcontratoDefinitivo, "Guarda partidas convenio modificatorio UE", usuario.getLogin(), conn);
            if (resp) {
                msg = "Datos Guardados.";
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            conn.rollback();
            msg = e.getMessage();
            log.error(e);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            itr = null;
            fila = null;
            manager = null;
            conn = null;
        }
        return msg;
    }

    public String apruebaConvRed(String jndiName, ContratoModificado contMod, Usuario usuario) throws Exception, JSONException {
        Connection conn = null;
        ContratoModificadoManager manager = null;
        String msg = null;
        JSONArray jsonArray = null;
        JSONArray jsonArrayMeses = null;
        try {
            conn = getConnection(jndiName);
            manager = new ContratoModificadoManager();
            jsonArray = new JSONArray(contMod.getCadTabla());
            if (jsonArray == null || jsonArray.length() == 0) {
                throw new Exception("No se recibi\u00f3 el calendario del presupuesto a reduccir.");
            }
            // cambiar estatus
            contMod.setnEstado(ContractStatus.BUDGET);
            manager.updateStateConvMod(conn, contMod);
            //llenar detale del monto capturado
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonArrayMeses = (JSONArray) ((jsonArray.getJSONObject(i).get("datosMesesCap")));
                if (jsonArrayMeses == null || jsonArrayMeses.length() == 0) {
                    continue;
                }
                manager.addCalendarioCaptura(conn, contMod, jsonArrayMeses, jsonArray.getJSONObject(i).getString("claveSIAFF"), jsonArray.getJSONObject(i).getString("claveInterna"));
            }
            Util.bitacoraMovimientos(contMod.getcIdContratoDefinitivo(), "Aprueba Convenio de Reducción", usuario.getLogin(), conn);
            conn.commit();
            msg = "Convenio aprobado de forma correcta.";
            return msg;
        } catch (SQLException e) {
            conn.rollback();
            log.error(e);
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            manager = null;
            conn = null;
            jsonArray = null;
            jsonArrayMeses = null;
        }
    }

    public String devulveConvRed(String jndiName, ContratoModificado contMod, Usuario usuario) throws Exception {
        Connection conn = null;
        ContratoModificadoManager manager = null;
        String msg = null;
        try {
            conn = getConnection(jndiName);
            manager = new ContratoModificadoManager();
            // cambiar estatus
            contMod.setnEstado(ContractStatus.NO_BUDGET);
            manager.updateStateConvMod(conn, contMod);
            //Elimina detale del monto capturado
            manager.deleteCalendarioCaptura(conn, contMod);
            //Guarda bitacora
            Util.bitacoraMovimientos(contMod.getcIdContratoDefinitivo(), "Devuelve Convenio de Reducción", usuario.getLogin(), conn);
            conn.commit();
            msg = "Convenio devuelto de forma correcta.";
            return msg;
        } catch (SQLException e) {
            conn.rollback();
            log.error(e);
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            manager = null;
            conn = null;
        }
    }

    public String autorizaConvRed(String jndiName, ContratoModificado contMod, Usuario usuario) throws Exception {
        Connection conn = null;
        ContratoModificadoManager manager = null;
        StringBuilder msg = null;
        ConfiguraAplicativoBusinessLogic configApp = null;
        boolean esSAIAlterno = false;
        Caso c = null;
        String folioCaso = null;
        int indice = -1;
        int folio = -1;
        String DATE_FORMAT = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String today = sdf.format(c1.getTime());
        String cEjercicio = "";
        Map<String, String> m = null;
        AplicarContableReturn acr = null;
        ContableInterface conInt = null;
        ArrayList<String> arrLResult = new ArrayList<String>();
        AccountingEngine ae = null;
        CasoBusinessLogic cbl = null;
        Map<String, String> datosMap = null;
        ArrayList<Map<String, String>> registros = null;
        Map<String, String> datos = null;
        try {
            conn = getConnection(jndiName);
            msg = new StringBuilder();
            configApp = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
            manager = new ContratoModificadoManager();
            esSAIAlterno = "true".equals(configApp.getSystemSetting("SAI_AMBIENTAL")) || "true".equals(configApp.getSystemSetting("SAI_FONDEN"));
            cEjercicio = Util.obtieneEjercicioFiscalActivo(conn);
            //Se valida pagos y movimientos de compromiso pendientes
            if (!esSAIAlterno) {
                //Validar si hay compromisos por autorizar en sicop
                if ((contMod.getcIdContratoDefinitivo()).indexOf("PE") < 0 && Util.hayCompromisoPendienteAutSICOP(conn, contMod.getcIdContratoDefinitivo())) {
                    log.info("Hay compromisos pendientes por autorizar en SICOP.");
                    throw new Exception("No se puede autorizar el Convenio Modificatorio en SAI. El contrato tiene compromisos pendientes por autorizar en SICOP.");
                }
                //Validar si hay pagos por autorizar en sicop
                if ((contMod.getcIdContratoDefinitivo()).indexOf("PE") < 0 && Util.hayPagosPendienteAutSICOP(conn, contMod.getcIdContratoDefinitivo())) {
                    log.info("Hay pagos pendientes por autorizar en SICOP.");
                    throw new Exception("No se puede autorizar el Convenio Modificatorio en SAI. El contrato tiene pagos pendientes por autorizar en SICOP.");
                }
            }
            //Se valida si es un contrato descentralizado.
            registros = manager.queryUnidadesCont(conn, contMod);
            conInt = new AplicacionContable();
            msg.append("Convenio autorizado de forma correcta.");
            int i = 0;
            ae = new AccountingEngine();
            ae.setValidaInsuficienciaDeSaldo(true);
            cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            while (i < registros.size()) {
                datosMap = registros.get(i);
                contMod.setcCanoCompromiso(Util.generaCaNoContrarecibo(datosMap.get("cCentroContable"), cEjercicio, "CO"));
                //validar la unidad
                if (null == datosMap.get("cUnidadResponsable") || "".equalsIgnoreCase(datosMap.get("cUnidadResponsable"))) {
                    log.info("La tabla de partidas del contrato modificado no tiene la unidad ejecutora de cada l\u00ednea");
                    throw new Exception("La tabla de partidas del contrato modificado no tiene la unidad ejecutora de cada l\\u00ednea");
                }
                //Se genera el caso
                c = manager.iniciaCaso(usuario, datosMap.get("cUnidadResponsable"), (GestionInterface.IDTC_COMPROMISO + ""), jndiName);
                if (c == null) {
                    //no se genero correctamente folio
                    log.info("NO SE PUDO GENERAR EL FOLIO DE COMPROMISO INTENTE NUEVAMENTE");
                    throw new Exception("NO SE PUDO GENERAR EL FOLIO DE COMPROMISO INTENTE NUEVAMENTE");
                } else {
                    folioCaso = c.getFolio();
                    indice = folioCaso.lastIndexOf('-') + 1;
                    folio = Integer.parseInt(folioCaso.substring(indice));
                    datos = new HashMap<String, String>();
                    datos.put("FOLIO", folioCaso);
                    datos.put("FECHA_DOCUMENTO", today);
                    datos.put("EJERCICIO_FISCAL", cEjercicio);
                    datos.put("OPERADOR", usuario.getNombre());
                    datos.put("MONEDA", "MXP");
                    datos.put("APLICADO_CONT", "false");
                    datos.put("CONCEPTO_MOV", "Convenio Modificatorio de Reducción");
                    CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), datos);
                    //llena las tablas del compromiso
                    contMod.setnFolioCompromiso(folio);
                    contMod.setcUnidadEjecutoraLinea(datosMap.get("cUnidadResponsable"));
                    contMod.setcCentroContable(datosMap.get("cCentroContable"));
                    manager.createEncabezadoCompromisoRed(conn, contMod, usuario.getLogin());
                    manager.createDetalleCompromisoRed(conn, contMod, EventosPresupuestales.CMP004);
                    //llena el las tablas del precompromisoFinanciero
                    manager.createEncabezadoPrecomFinanciero(conn, contMod, usuario.getLogin());
                    manager.createDetallePrecomFinanciero(conn, contMod, EventosPresupuestales.CMP003);
                    //aplicación contable del precompromiso
                    m = com.syc.gestion.util.Util.readValuesCasoDato(c.getCasoDato());
                    ae.makeAccountingApplication(conn, "PRECOMFINANCIERO", String.valueOf(folio), "tPrecomFinancieroEncabezado", "tPrecomFinancieroDetalle", "nFolioPrecomFinanciero");
                    //Aplicar el compromiso si son ingresos propios
                    if (!Util.esRecursoFiscal(conn, contMod.getcIdContratoDefinitivo()) && !esSAIAlterno) {
                        acr = conInt.aplicarContableNuevo(conn, c, "", "", "", 0, "", m, contMod.getPrefixPath(), usuario.getLogin(), "");
                        arrLResult = (ArrayList<String>) acr.getMessageList();
                        if (!acr.isSuccess()) {
                            throw new Exception("Error al aplicar la reducción de compromiso.\n" + arrLResult.get(0));
                        }
                    }
                    //Avanza el caso
                    cbl.avanzaCaso(conn, c, usuario.getLogin(), "", new String[] { "CONSULTA_PAGOS" }, new String[] { "consulta_compromiso" }, m, null);
                    msg.append("\nFolio de compromiso :" + folioCaso);
                    c = null;
                    datos = null;
                }
                i++;
            }
            // cambiar estatus
            contMod.setnEstado(ContractStatus.APPROVED);
            manager.updateStateConvMod(conn, contMod);
            //Guarda la bitacora
            Util.bitacoraMovimientos(contMod.getcIdContratoDefinitivo(), "Convenio de Reducción Autorizado", usuario.getLogin(), conn);
            conn.commit();
            return msg.toString();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            log.error(e);
            throw (e);
        } finally {
            if (c1 != null) {
                c1.clear();
            }
            if (conn != null) {
                conn.close();
            }
            if (registros != null) {
                registros.clear();
            }
            if (datosMap != null) {
                datosMap.clear();
            }
            manager = null;
            conn = null;
            configApp = null;
            registros = null;
            folioCaso = null;
            c1 = null;
            sdf = null;
            m = null;
            cbl = null;
            arrLResult = null;
            datosMap = null;
            c = null;
        }
    }

    public String saveAndValidateDates(String jndiName, ContratoModificado contMod, Usuario usuario) throws Exception {
        Connection conn = null;
        ContratoModificadoManager manager = null;
        StringBuilder msg = new StringBuilder();
        boolean error = true;
        try {
            conn = getConnection(jndiName);
            manager = new ContratoModificadoManager();
            // Validar fechas
            manager.validateDates(conn, contMod);
            //Guardar fechas, núm de conv. y objeto del convenio
            manager.saveDates(conn, contMod);
            manager.saveNumConvenio(conn, contMod);
            //Guarda bitacora
            Util.bitacoraMovimientos(contMod.getcIdContratoDefinitivo(), "Guarda Fechas, Número y objeto de convenio", usuario.getLogin(), conn);
            msg.append("Datos Guardados.");
            conn.commit();
            error = false;
        } catch (SQLException e) {
            if (error && conn != null)
                conn.rollback();
            log.error(e);
            msg.append("Error: " + e.getMessage());
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            manager = null;
            conn = null;
        }
        return msg.toString();
    }
}
