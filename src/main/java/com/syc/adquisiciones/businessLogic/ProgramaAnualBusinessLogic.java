package com.syc.adquisiciones.businessLogic;

import com.syc.adquisiciones.core.DatosPAAS;
import com.syc.adquisiciones.core.Respuesta;
import com.syc.adquisiciones.manager.ConsumePAASManager;
import com.syc.adquisiciones.util.Util;
import com.syc.dsmngr.DataSourceManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgramaAnualBusinessLogic extends DataSourceManager {

    public static Logger log = LoggerFactory.getLogger(ProgramaAnualBusinessLogic.class);

    public Respuesta deleteCUCOP(String cIdUnidadEjecutora, String cCucop, String cPartida) throws Exception {
        Respuesta respuesta = null;
        Connection conn = null;
        ConsumePAASManager manager = null;
        try {
            conn = getConnection();
            manager = new ConsumePAASManager();
            respuesta = new Respuesta();
            respuesta.setResp(false);
            String ejercicioAct = Util.obtieneEjercicioFiscalActivo(conn);
            if (manager.hayCUCOPEnSolicitudes(conn, ejercicioAct, cIdUnidadEjecutora, cCucop, cPartida)) {
                throw new Exception("No se puede eliminar el CUCOP porque esta asociado a una o mas solicitudes");
            }
            manager.deleteCUCOPDetallePerioro(conn, ejercicioAct, cIdUnidadEjecutora, cCucop, cPartida);
            manager.deleteCUCOPDetalle(conn, ejercicioAct, cIdUnidadEjecutora, cCucop, cPartida);
            respuesta.setMsg("CUCOP Eliminado del PAAS.");
            respuesta.setResp(true);
            conn.commit();
        } catch (Exception e) {
            if (respuesta == null)
                respuesta = new Respuesta();
            conn.rollback();
            e.printStackTrace();
            respuesta.setMsg(e.getMessage());
            respuesta.setResp(false);
            throw e;
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
            manager = null;
        }
        return respuesta;
    }

    public Respuesta deleteCUCOPCapMil(DatosPAAS dat) throws Exception {
        Respuesta respuesta = null;
        Connection conn = null;
        ConsumePAASManager manager = null;
        try {
            conn = getConnection();
            manager = new ConsumePAASManager();
            respuesta = new Respuesta();
            respuesta.setResp(false);
            if (manager.deleteCUCOPDetallePerioroCapMil(conn, dat)) {
                if (manager.deleteCUCOPDetalleCapMil(conn, dat)) {
                    respuesta.setMsg("CUCOP Eliminado del PAAS.");
                    respuesta.setResp(true);
                } else {
                    respuesta.setMsg("No se elimino el cucop");
                }
            } else {
                respuesta.setMsg("No se elimino el calendario del cucop");
            }
            if (respuesta.isResp()) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (Exception e) {
            if (respuesta == null)
                respuesta = new Respuesta();
            if (conn != null)
                conn.rollback();
            e.printStackTrace();
            e.printStackTrace();
            respuesta.setMsg(e.getMessage());
            respuesta.setResp(false);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
            manager = null;
        }
        return respuesta;
    }

    public Respuesta altaProgramaAnualCapMil(DatosPAAS dat) throws Exception {
        Connection conn = null;
        ConsumePAASManager manager = null;
        Respuesta respuesta = null;
        try {
            conn = getConnection();
            manager = new ConsumePAASManager();
            respuesta = new Respuesta();
            if (!manager.existePAASCapMil(conn, dat)) {
                manager.addPAASCapMil(conn, dat);
                respuesta.setMsg("Se agrego correctamente el PAAS Cap Mil.");
                respuesta.setResp(true);
            } else {
                respuesta.setMsg("El PAAS Cap. Mil ya existe.");
                respuesta.setResp(false);
            }
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                conn.rollback();
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return respuesta;
    }

    public JSONObject consultaSelects() throws Exception {
        Connection conn = null;
        JSONArray arrayObj = null;
        JSONObject jsonObj = new JSONObject();
        try {
            conn = getConnection();
            String query = "\r\nselect \r\ncUnidadEjecutora +' - '+D_DESCRIPCION descrip,cUnidadEjecutora\r\nfrom tCatUnidadEjecutora where cUnidadEjecutora='A03'";
            arrayObj = Util.obtieneDatQuery(conn, query);
            jsonObj.put("catalogoUE", arrayObj);
            arrayObj = null;
            query = "select \r\ncIdSubPartida+' - '+cSubPartida descrip,cIdSubPartida\r\nfrom mCatalogoSubPartida with(Nolock)\r\nwhere cIdCapitulo=1";
            arrayObj = Util.obtieneDatQuery(conn, query);
            jsonObj.put("catalogoPartidaCapMil", arrayObj);
            arrayObj = null;
            query = "SELECT cCapitulo,cIdCapitulo FROM mCatalogoCapitulo WITH(NOLOCK) WHERE cIdCapitulo  in(1)";
            arrayObj = Util.obtieneDatQuery(conn, query);
            jsonObj.put("catalogoCapitulos", arrayObj);
            arrayObj = null;
            query = "SELECT '*' as cPartida,'0' AS cIdPartida union select cIdSubPartida+' - '+cSubPartida cPartida,cIdSubPartida as cIdPartida from mCatalogoSubPartida with(Nolock) where cIdCapitulo=1 ";
            arrayObj = Util.obtieneDatQuery(conn, query);
            jsonObj.put("catalogoPartida", arrayObj);
            arrayObj = null;
            query = "\r\nselect 0.0 mMontoPartida \r\n" + ",1 hayEP\r\n" + ",'FALSE' techoActivado\r\n" + ",ISNULL((select cValor as cEjercicio from mSistema WITH(NOLOCK) where nidParametro=1),'2021')cEjercicio\r\n" + ",isnull((select [dbo].[fn_mDisponibleTotalAnualEPS]('A03','11101')),0.0)as mTechoPresupuestal\r\n" + ",isnull((select sum(mImporteNeto)mImporteNeto from vCucopsGridBDCapMil WITH(NOLOCK) where  cIdSubPartidaCap1000='11101' and cIdUnidadEjecutora='A03'),0.0) mMontoPartida\r\n" + ",isnull((select 1 as tieneUe from mProgramaAnualCap1000 WITH(NOLOCK) where cidunidadEjecutora='A03'),-1)tieneUe";
            arrayObj = Util.datGuardados(conn, query);
            jsonObj.put("datosPrincipales", arrayObj);
            arrayObj = null;
            query = "select mMontoC2,mMontoC3,mMontoC5,mMontoTotal from fn_mProgramaAnualMontosPorCapituloCapMil() WHERE  cIdUnidadEjecutora='A03'";
            arrayObj = Util.datGuardados(conn, query);
            jsonObj.put("montosPorCapitulo", arrayObj);
            arrayObj = null;
            conn.commit();
        } catch (SQLException e) {
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException e2) {
                log.error("Error en el rollback : " + e.getMessage());
            }
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
            arrayObj = null;
        }
        return jsonObj;
    }

    public JSONObject datosCalendario(DatosPAAS dat) throws Exception {
        Connection conn = null;
        JSONArray arrayObj = null;
        JSONObject jsonObj = new JSONObject();
        try {
            conn = getConnection();
            String query = "select DESCRIPCION ivaEdita,VALOR from mCatalogoTipoIVA WITH(NOLOCK) order by VALOR desc";
            arrayObj = Util.obtieneDatQuery(conn, query);
            jsonObj.put("catalogoIVA", arrayObj);
            arrayObj = null;
            query = "SELECT cAdjudicacion,cIdTipoAdj FROM mCatalogoTipoAdjudicacion WITH(NOLOCK)";
            arrayObj = Util.obtieneDatQuery(conn, query);
            jsonObj.put("catalogoTipoAdj", arrayObj);
            arrayObj = null;
            query = "SELECT cProcedencia as tipoProcedimientoEdita ,cIdProcedencia FROM mCatalogoProcedencia WITH(NOLOCK)";
            arrayObj = Util.obtieneDatQuery(conn, query);
            jsonObj.put("catalogoProcedencia", arrayObj);
            arrayObj = null;
            query = "\r\nselect 1 hayEP\r\n" + ",'FALSE' techoActivado\r\n" + ",ISNULL((select cValor as cEjercicio from mSistema WITH(NOLOCK) where nidParametro=1),'2021')cEjercicio\r\n" + ",(SELECT cValor as cTolerancia FROM mSistema  WITH(NOLOCK) where cParametro = 'ToleranciaPAPartida')cTolerancia\r\n" + ",convert(money,isnull((SELECT Modificado as mTechoPresupuestal FROM fn_mDataStoreTechoPresupuestalPartida('" + dat.getcPartidaCapMil() + "', '" + dat.getcUnidadEjecutora() + "')),0.0))mTechoPresupuestal\r\n" + ",convert(money,isnull((SELECT mMontoPartida mMontoPartidaRestCUCOP FROM fn_mProgramaAnualMontoPorPartidaRestCucop('" + dat.getcPartidaCapMil() + "','" + dat.getcCucopEliminar() + "') WHERE cEjercicio='" + dat.getcEjercicio() + "' AND cIdUE='" + dat.getcUnidadEjecutora() + "'),0))mMontoPartidaRestCUCOP\r\n" + ",convert(money,isnull((select [dbo].[fn_mDisponibleTotalAnualEPS]('" + dat.getcUnidadEjecutora() + "','" + dat.getcPartidaCapMil() + "')),0))as montoAnualTotalEPS\r\n";
            arrayObj = Util.datGuardados(conn, query);
            jsonObj.put("datosPrincipales", arrayObj);
            arrayObj = null;
            query = " select ePlurianualidad as plurianualidad,vEstimadoP as plurianualidadv,tipoProcedimiento as tipoAdjudicacion,cIdProcedencia tipoProcedimientoEdita \r\n" + " ,mPorcentajePyme pymeEdita,mPorcentajeNoTratados porcentajeEdita,nPorcentajeIVA ivaEdita ,cab.cCABM cCABMEdita\r\n" + " from mProgramaAnualDetalleCap1000 det WITH(NOLOCK) \r\n" + " inner join mCatalogoCABM as cab WITH(NOLOCK) \r\n" + " on det.cIdCABM=cab.cIdCABM and det.cIdSubPartida=cab.cIdSubPartida\r\n" + " where det.cIdCABM='" + dat.getcCucopEliminar() + "' and cIdUnidadEjecutora='" + dat.getcUnidadEjecutora() + "' and cIdSubPartidaCap1000='" + dat.getcPartidaCapMil() + "'";
            arrayObj = Util.datGuardados(conn, query);
            jsonObj.put("datosguardadosCalendario", arrayObj);
            arrayObj = null;
            query = "select cast(round(totalEdita,2) as money)totalEdita from v_totalEditaCapMil WITH(NOLOCK) WHERE cEjercicio  ='" + dat.getcEjercicio() + "' \r\n" + "AND cIdUnidadEjecutora ='" + dat.getcUnidadEjecutora() + "' AND cIdCABM ='" + dat.getcCucopEliminar() + "' AND cIdSubPartida='" + dat.getcPartidaEliminar() + "' \r\n" + "and cIdSubPartidaCap1000='" + dat.getcPartidaCapMil() + "'";
            arrayObj = Util.datGuardados(conn, query);
            jsonObj.put("datosguardadosTotal", arrayObj);
            arrayObj = null;
            query = "select ePlurianualidad as valorplurianualidad,vEstimadoP as valorplurianualidadv\r\n,tipoProcedimiento as valortipoAdjudicacion,cIdProcedencia tipoProcedimientoEdita \r\nfrom mProgramaAnualDetalleCap1000 WITH(NOLOCK) \r\nwhere cIdCABM='" + dat.getcCucopEliminar() + "' and cIdUnidadEjecutora='" + dat.getcUnidadEjecutora() + "' and cIdSubPartida='" + dat.getcPartidaEliminar() + "' and cIdSubPartidaCap1000='" + dat.getcPartidaCapMil() + "'";
            arrayObj = Util.datGuardados(conn, query);
            jsonObj.put("datosOtros", arrayObj);
            arrayObj = null;
            conn.commit();
        } catch (SQLException e) {
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException e2) {
                log.error("Error en el rollback : " + e.getMessage());
            }
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
            arrayObj = null;
        }
        return jsonObj;
    }

    public JSONObject actualizaMontosPAASCapMil(DatosPAAS dat) throws Exception {
        Connection conn = null;
        JSONArray arrayObj = null;
        JSONObject jsonObj = new JSONObject();
        try {
            conn = getConnection();
            String query = "\r\nselect 1 hayEP\r\n" + ",isnull((select [dbo].[fn_mDisponibleTotalAnualEPS]('" + dat.getcUnidadEjecutora() + "','" + dat.getcPartidaCapMil() + "')),0)as montoAnualTotalEPS\r\n" + ",isnull((select sum(mImporteNeto)mImporteNeto from vCucopsGridBDCapMil WITH(NOLOCK) where  cIdSubPartidaCap1000='" + dat.getcPartidaCapMil() + "' and cIdUnidadEjecutora='" + dat.getcUnidadEjecutora() + "'),0) mMontoPartida\r\n";
            arrayObj = Util.datGuardados(conn, query);
            jsonObj.put("hayEpMonto", arrayObj);
            arrayObj = null;
            query = "select mMontoC2,mMontoC3,mMontoC5,mMontoTotal from fn_mProgramaAnualMontosPorCapituloCapMil() WHERE cEjercicio='" + dat.getcEjercicio() + "' AND cIdUnidadEjecutora='" + dat.getcUnidadEjecutora() + "'";
            arrayObj = Util.datGuardados(conn, query);
            jsonObj.put("montosPorCapitulo", arrayObj);
            arrayObj = null;
            query = "select isnull((SELECT Modificado as mTechoPresupuestal FROM fn_mDataStoreTechoPresupuestalPartida('" + dat.getcPartidaCapMil() + "','" + dat.getcUnidadEjecutora() + "')),0.0) mTechoPresupuestal";
            arrayObj = Util.datGuardados(conn, query);
            jsonObj.put("presupuestoPart", arrayObj);
            arrayObj = null;
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                conn.rollback();
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
            arrayObj = null;
        }
        return jsonObj;
    }

    public Respuesta guardaCalendarioPAASCapMil(DatosPAAS dat) throws Exception {
        Connection conn = null;
        ConsumePAASManager manager = null;
        Respuesta respuesta = null;
        String cadTabla = null;
        String[] arrayTabla = null;
        ArrayList<List<String>> tabla = null;
        try {
            conn = getConnection();
            manager = new ConsumePAASManager();
            cadTabla = dat.getcCadTabla();
            arrayTabla = cadTabla.split(",");
            tabla = Util.creaArray(arrayTabla);
            if (tabla.isEmpty())
                throw new Exception("No se recibieron los datos de la tabla");
            respuesta = validacionesGuardaPAASCapMil(conn, dat, manager, tabla);
            if (respuesta.isResp()) {
                manager.updatePAASCapMil(conn, dat);
                if (manager.existCalendarioPAASCapMil(conn, dat)) {
                    manager.updateCalendarioPAASCapMil(conn, tabla, dat);
                } else {
                    manager.addCalendarioPAASCapMil(conn, tabla, dat);
                }
                Util.bitacoraMovimientos(dat.getcCucopEliminar(), "GUARDA_CALENDARIOPAASCapMil", dat.getcLogin(), conn);
                respuesta.setMsg("Datos Guardados.");
            }
            conn.commit();
        } catch (SQLException e) {
            respuesta = new Respuesta();
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.error("Error al hacer el rollback :" + e2);
                }
            respuesta.setMsg(e.getMessage());
            log.error(e.getMessage());
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
            cadTabla = null;
            arrayTabla = null;
            tabla = null;
        }
        return respuesta;
    }

    private Respuesta validacionesGuardaPAASCapMil(Connection conn, DatosPAAS dat, ConsumePAASManager manager, ArrayList<List<String>> tabla) throws Exception {
        Respuesta respuesta = null;
        double[] montoCalendarizado = { 0.0D, 0.0D };
        double[] montosACalendarizar = { 0.0D, 0.0D };
        String token = "";
        double montoPresupuesto = 0.0D;
        double tolerancia = 0.0D;
        boolean validarPresup = false;
        respuesta = new Respuesta();
        respuesta.setResp(true);
        respuesta.setMsg("");
        //obtener montos a calendarizar
        montosACalendarizar = manager.montosAcalendarizar(tabla, dat);
        if (validarPresup && manager.validarPresupuestoPAAS(conn)) {
            montoPresupuesto = manager.presupuestoPorPartidaCapMil(conn, dat);
            montoCalendarizado = manager.montoCapturadoPorPartidaCapMil(conn, dat);
            tolerancia = manager.toleranciaPAAS(conn);
            log.info("Presupuesto de la partida " + dat.getcPartidaCapMil() + " es : " + montoPresupuesto + " ; monto calendarizado en el PAAS es " + montoCalendarizado[1]);
            if ((montosACalendarizar[1] + montoCalendarizado[1]) > (montoPresupuesto * (1.0D + tolerancia / 100.0D))) {
                respuesta.setMsg("No es posible realizar las modificaciones porque sobrepasan el presupuesto de la partida");
                respuesta.setResp(false);
                token = "\n";
            }
        }
        //Validar plurianualidad
        if (dat.getnPlurianual() > 0) {
            if (dat.getnCantidaEjercicios() <= 0) {
                respuesta.setMsg(respuesta.getMsg() + token + "El campo Ejercicio de plurianualidad debe ser mayor a cero");
                respuesta.setResp(false);
                token = "\n";
            } else {
                if (dat.getmMontoBrutoPlurianual() > (montosACalendarizar[0])) {
                    respuesta.setMsg(respuesta.getMsg() + token + "Al monto sin IVA a ejercer plurianual debe de ser menor al monto sin IVA del cucop.");
                    respuesta.setResp(false);
                    token = "\n";
                }
            }
        } else {
            dat.setnCantidaEjercicios(0);
            dat.setmMontoBrutoPlurianual(0);
        }
        //Validar cantidad destinada a 	MIPYME
        if (dat.getmMontoDestMiPyme() > montosACalendarizar[0]) {
            respuesta.setMsg(respuesta.getMsg() + token + "El pyme debe ser menor igual que el monto bruto.");
            respuesta.setResp(false);
            token = "\n";
        }
        //validar estimado de compras no cubiertas por tratados
        if (dat.getmMontoEstimadoComprasNoCubiertas() > montosACalendarizar[0]) {
            respuesta.setMsg(respuesta.getMsg() + token + "El valor estimado debe ser menor o igual al monto bruto.");
            respuesta.setResp(false);
            token = "\n";
        }
        return respuesta;
    }
}
