package com.syc.adquisiciones.businessLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.syc.adquisiciones.core.DatosProcedSAC;
import com.syc.adquisiciones.core.Respuesta;
import com.syc.adquisiciones.manager.BaseSACManager;
import com.syc.adquisiciones.util.Util;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseSACBusinessLogic extends DataSourceManager {

    public static Logger log = LoggerFactory.getLogger(BaseSACBusinessLogic.class);

    public BaseSACBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public JSONObject consultaSelectsProcedimiento(String cCoord, int nTipoProced) throws Exception {
        Connection conn = null;
        JSONArray arrayObj = null;
        JSONObject jsonObj = new JSONObject();
        BaseSACManager manager;
        String cEjercicio = "2021";
        String query;
        ConfiguraAplicativoBusinessLogic configApp = null;
        String desa = "";
        try {
            conn = getConnection();
            manager = new BaseSACManager();
            //codificar
            configApp = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
            cEjercicio = Util.obtieneEjercicioFiscalActivo(conn);
            boolean esSAIDesa = "true".equalsIgnoreCase(configApp.getSystemSetting("AMBIENTE_DESARROLLO"));
            if (esSAIDesa) {
                desa = "_desa";
            }
            //Cat\u00e1logo Coordinaciones "areas requirentes"
            query = "select '<Seleccione una opci\u00f3n>'cunidad,'0' cUejecutora\r\n" + "union select \r\n" + "cUejecutora +' - '+cDescripcion cunidad,cUejecutora\r\n" + "from nomina_" + cEjercicio + desa + ".dbo.nom_Unidad_Ejecutora cat with(Nolock)\r\n" + "inner join(\r\n" + "	select \r\n" + "	c_coordinacion,cDescCoortaCordinacion\r\n" + "	from nomina_" + cEjercicio + desa + ".dbo.nom_Unidad_Ejecutora with(Nolock) \r\n" + "	group by c_coordinacion,cDescCoortaCordinacion\r\n" + ")coor\r\n" + "on coor.c_coordinacion=cat.cUejecutora";
            arrayObj = manager.obtieneDatQuery(conn, query);
            jsonObj.put("catalogoCoordinaciones", arrayObj);
            //String cCoord=(arrayObj!=null && arrayObj.length()>0)?arrayObj.getJSONObject(0).getString( "Id" ):"A01";
            arrayObj = null;
            //catalogo area responsable "unidades ejecutoras"
            query = "select '<Seleccione una opci\u00f3n>'cunidad,'0' cUejecutora";
            if (!"0".equalsIgnoreCase(cCoord)) {
                query = "select '<Seleccione una opci\u00f3n>'cunidad,'0' cUejecutora union SELECT cUejecutora+' - '+cDescripcion AS UNIDAD,cUejecutora \r\n" + "FROM nomina_" + cEjercicio + desa + ".dbo.nom_Unidad_Ejecutora WITH(nOLOCK) WHERE c_coordinacion='" + cCoord + "'\r\n" + "order by cUejecutora";
            }
            arrayObj = manager.obtieneDatQuery(conn, query);
            jsonObj.put("catalogoAreaResponsable", arrayObj);
            arrayObj = null;
            //Cat\u00e1logo tipo procedimiento
            query = "select '<Seleccione una opci\u00f3n>'descrip,'0' nIdcategoria union select \r\n" + "convert(varchar,nIdcategoria)+'.- '+cProcedimiento descrip,nIdcategoria\r\n" + "from mCatalogocategoriaprocedimientoSAC with(Nolock)";
            arrayObj = manager.obtieneDatQuery(conn, query);
            jsonObj.put("catalogoTipoProced", arrayObj);
            arrayObj = null;
            //Cat\u00e1logo materia procedimiento
            query = "select '<Seleccione una opci\u00f3n>'descrip,'0' nIdMateriaProcedimiento union select \r\n" + "convert(varchar,nIdMateriaProcedimiento)+'.- '+cDescripcionMatProced descrip,nIdMateriaProcedimiento\r\n" + "from mCatalogoMateriaProcedimiento with(Nolock)";
            arrayObj = manager.obtieneDatQuery(conn, query);
            jsonObj.put("catalogoMateriaProced", arrayObj);
            arrayObj = null;
            //Cat\u00e1logo proveedor dado de alta
            query = "select '<Seleccione una opci\u00f3n>'descrip,'0' nProveedorDadoAlta_SAICNET union select \r\n" + "convert(varchar,nProveedorDadoAlta_SAICNET)+'.- '+cDescripcionProvDadoAlta descrip,nProveedorDadoAlta_SAICNET\r\n" + "from mCatalogoProvDadoAltaSAICNET with(Nolock)";
            arrayObj = manager.obtieneDatQuery(conn, query);
            jsonObj.put("catalogoEstatusProveedor", arrayObj);
            arrayObj = null;
            //Cat\u00e1logo proceso de contrataci\u00f3n
            query = "select convert(varchar,nIdProcesoContratacion)+'.- '+cProcesoContratacion as proceso,nIdProcesoContratacion from mCatalogoProcesosContratacionSAC with(Nolock)  where nIdProcesoContratacion=0";
            if (nTipoProced > 0) {
                query = "select convert(varchar,ROW_NUMBER() OVER (ORDER BY cat.nIdProcesoContratacion)-1)+'.- '+cProcesoContratacion as proceso,cat.nIdProcesoContratacion from mCatalogoProcesosContratacionSAC as cat with(Nolock) " + "inner join mRelacionCatProcedimiento_ProcesoContratacion as rel with(Nolock) on rel.nIdProcesoContratacion=cat.nIdProcesoContratacion " + "where rel.nIdCategoria=" + nTipoProced + " order by cat.nIdProcesoContratacion";
            }
            arrayObj = manager.obtieneDatQuery(conn, query);
            jsonObj.put("catalogoProcesoContratacion", arrayObj);
            arrayObj = null;
            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            log.error("Object: {}", e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
            manager = null;
            configApp = null;
        }
        return jsonObj;
    }

    public JSONObject consultaAreasResponsables(String cCoord) throws Exception {
        Connection conn = null;
        JSONArray arrayObj = null;
        JSONObject jsonObj = new JSONObject();
        BaseSACManager manager;
        String cEjercicio = "2021";
        String query;
        ConfiguraAplicativoBusinessLogic configApp = null;
        String desa = "";
        try {
            conn = getConnection();
            manager = new BaseSACManager();
            //codificar
            cEjercicio = Util.obtieneEjercicioFiscalActivo(conn);
            configApp = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
            boolean esSAIDesa = "true".equalsIgnoreCase(configApp.getSystemSetting("AMBIENTE_DESARROLLO"));
            if (esSAIDesa) {
                desa = "_desa";
            }
            //catalogo area responsable "unidades ejecutoras"
            query = "select '<Seleccione una opci\u00f3n>'cunidad,'0' cUejecutora union SELECT cUejecutora+' - '+cDescripcion AS UNIDAD,cUejecutora \r\n" + "FROM nomina_" + cEjercicio + desa + ".dbo.nom_Unidad_Ejecutora WITH(nOLOCK) WHERE c_coordinacion='" + cCoord + "'\r\n" + "order by cUejecutora";
            arrayObj = manager.obtieneDatQuery(conn, query);
            jsonObj.put("catalogoAreaResponsable", arrayObj);
            arrayObj = null;
            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            log.error("Object: {}", e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
            manager = null;
            configApp = null;
        }
        return jsonObj;
    }

    public JSONObject consultaProcesoContratacion(int nTipoProced) throws Exception {
        Connection conn = null;
        JSONArray arrayObj = null;
        JSONObject jsonObj = new JSONObject();
        BaseSACManager manager;
        String query;
        try {
            conn = getConnection();
            manager = new BaseSACManager();
            //catalogo procesos de contrataci\u00f3n
            query = "select convert(varchar,ROW_NUMBER() OVER (ORDER BY cat.nIdProcesoContratacion)-1)+'.- '+cProcesoContratacion as proceso,cat.nIdProcesoContratacion from mCatalogoProcesosContratacionSAC as cat with(Nolock) " + "inner join mRelacionCatProcedimiento_ProcesoContratacion as rel with(Nolock) on rel.nIdProcesoContratacion=cat.nIdProcesoContratacion " + "where rel.nIdCategoria=" + nTipoProced + " order by cat.nIdProcesoContratacion";
            ;
            arrayObj = manager.obtieneDatQuery(conn, query);
            jsonObj.put("catalogoProcesoContratacion", arrayObj);
            arrayObj = null;
            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            log.error("Object: {}", e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
            manager = null;
        }
        return jsonObj;
    }

    public Respuesta addNewProcedimiento(DatosProcedSAC datos) throws Exception {
        Connection conn = null;
        BaseSACManager manager;
        Respuesta resp = new Respuesta();
        int nIdProced = 0;
        String cadTabla = null;
        String[] arrayTabla = null;
        ArrayList<List<String>> tabla = null;
        try {
            conn = getConnection();
            manager = new BaseSACManager();
            //codificar
            manager.addNewProcedimiento(conn, datos);
            nIdProced = manager.readIdProcedimiento(conn, datos);
            if (nIdProced != 0) {
                //Agregar el area requirente
                manager.addAreaReq(conn, datos.getcAreaRequirente(), nIdProced);
                //Agregar el area responsable
                manager.addAreaResp(conn, datos.getcAreaResponsable(), nIdProced);
                //Agregar los participantes
                cadTabla = datos.getCadTablaParticipantes();
                arrayTabla = cadTabla.split(",");
                tabla = Util.creaArray(arrayTabla);
                if (!tabla.isEmpty()) {
                    manager.addParticipantes(conn, tabla, nIdProced);
                }
                //Guarda bitacora
                Util.bitacoraMovimientos(datos.getcOficioSolicitud(), "Guarda procedimiento", datos.getcLogin(), conn);
            } else {
                throw new Exception("No se encontro el idProcedimiento.");
            }
            resp.setMsg("Procedimiento agregado.");
            resp.setResp(true);
            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            log.error("Object: {}", e.getMessage());
            e.printStackTrace();
            resp.setMsg(e.getMessage());
            resp.setResp(false);
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
            manager = null;
            arrayTabla = null;
            tabla = null;
        }
        return resp;
    }

    public JSONObject queryProcedimiento(DatosProcedSAC datos) throws Exception {
        Connection conn = null;
        JSONArray arrayObj = null;
        JSONObject jsonObj = new JSONObject();
        BaseSACManager manager = null;
        String query = "";
        try {
            conn = getConnection();
            manager = new BaseSACManager();
            //Obtiene la informaci\u00f3n guardada
            //datos Guardados
            query = "select\r\n" + "	proced.nIdProcedimientoSAC \r\n" + "	,cIdProcedimientoSAC cNumProcedimiento \r\n" + "	,convert(varchar,convert(date,fSolicitud),103) fSolicitud \r\n" + "	,cOficioSolicitud  \r\n" + "	,cAreaTecnica cAreaTec  \r\n" + "	,cProcedimientoTurnado ServidorPublico \r\n" + "	,nTipoProcedimiento nTipoProcedimiento \r\n" + "	,nIdMateriaProcedimiento nIdMateriaProcedimiento \r\n" + "	,cDenominacionProced  cDenominacionProcedimiento \r\n" + "	,cProyectoConvocatoria cProyectoConvocatoria \r\n" + "	,case when year(fAutConvocatoria)<=1900 then ''else convert(varchar,fAutConvocatoria,103) end fAutConvocatoria\r\n" + "	,case when year(fConvocatoria)<=1900 then ''else convert(varchar,fConvocatoria,103) end fPublicacionConvocatoria\r\n" + "	,case when year(fJuntaAclaraciones)<=1900 then ''else convert(varchar,fJuntaAclaraciones,103) end fJuntaAclara\r\n" + "	,case when year(fAperturaProposiciones)<=1900 then ''else convert(varchar,fAperturaProposiciones,103) end fApertProposiciones\r\n" + "	,case when year(fEvaluacionTecnica)<=1900 then ''else convert(varchar,fEvaluacionTecnica,103) end fEvaluacionTecnica\r\n" + "	,case when year(fFallo_ActaAdjucdicacion)<=1900 then ''else convert(varchar,fFallo_ActaAdjucdicacion,103) end fFallo\r\n" + "	,case when year(fGeneracionContratoCNET)<=1900 then ''else convert(varchar,fGeneracionContratoCNET,103) end fGeneracionContrato\r\n" + "	,case when year(fExpedienteTurnadoContrato)<=1900 then ''else convert(varchar,fExpedienteTurnadoContrato,103) end fExpediente\r\n" + "	,nProveedorDadoAlta_SAICNET nProveedorDadoAlta_SAICNET \r\n" + "	,nEstatus \r\n" + "	,req.cAreaRequirente cAreaReq \r\n" + "	,resp.cAreaResponsable cAreaResp \r\n" + "	,proced.nIdProcesoContratacion cProcesoContratacion \r\n" + "	,case when fAtencion is null or year(fAtencion)<=1900 then ''else convert(varchar,fAtencion,103) end fAtencion\r\n" + "   ,isnull(proced.cObservaciones,'')cObservaciones \r\n" + "From mProcedimientoSAC proced with(Nolock) \r\n" + "inner join mAreaRequirenteProcedSAC req with(Nolock) \r\n" + "on req.nIdProcedimientoSAC=proced.nIdProcedimientoSAC \r\n" + "inner join mAreaResponsableProcedSAC resp with(Nolock) \r\n" + "on resp.nIdProcedimientoSAC=proced.nIdProcedimientoSAC \r\n" + "where proced.nIdProcedimientoSAC=" + datos.getnIdProcedimientoSAC();
            arrayObj = manager.datGuardados(conn, query);
            //obtener los selects
            jsonObj = consultaSelectsProcedimiento((arrayObj.getJSONObject(0).getBoolean("HAYINFO")) ? arrayObj.getJSONObject(0).getString("cAreaReq") : "0", (arrayObj.getJSONObject(0).getBoolean("HAYINFO")) ? arrayObj.getJSONObject(0).getInt("nTipoProcedimiento") : 0);
            jsonObj.put("datosGuardados", arrayObj);
            arrayObj = null;
        } catch (SQLException e) {
            if (conn != null) {
                conn.close();
            }
            conn = null;
            manager = null;
            throw e;
        }
        return jsonObj;
    }

    public void updateProcedSAC(DatosProcedSAC datos) throws Exception {
        Connection conn = null;
        BaseSACManager manager = null;
        try {
            conn = getConnection();
            manager = new BaseSACManager();
            //actualizar datos
            manager.updateProcedSAC(conn, datos);
            //Elimina areas requirentes
            manager.deleteAreaReq(conn, datos.getnIdProcedimientoSAC());
            //agrega areas requirentes
            manager.addAreaReq(conn, datos.getcAreaRequirente(), datos.getnIdProcedimientoSAC());
            //Elimina areas responsable
            manager.deleteAreaResponsable(conn, datos.getnIdProcedimientoSAC());
            //agrega areas responsable
            manager.addAreaResp(conn, datos.getcAreaResponsable(), datos.getnIdProcedimientoSAC());
            //Guarda bitacora
            Util.bitacoraMovimientos(datos.getcOficioSolicitud(), "Actualiza procedimiento", datos.getcLogin(), conn);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
            manager = null;
            ;
        }
    }

    public void desiertoProcedSAC(DatosProcedSAC datos) throws Exception {
        Connection conn = null;
        BaseSACManager manager = null;
        try {
            conn = getConnection();
            manager = new BaseSACManager();
            //actualizar datos
            datos.setnEstatus(4);
            manager.updateEstatusProcedSAC(conn, datos);
            //Guarda bitacora
            Util.bitacoraMovimientos(datos.getcOficioSolicitud(), "Se declara desierto el procedimiento", datos.getcLogin(), conn);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
            manager = null;
            ;
        }
    }

    public void procedSACDevuelto(DatosProcedSAC datos) throws Exception {
        Connection conn = null;
        BaseSACManager manager = null;
        try {
            conn = getConnection();
            manager = new BaseSACManager();
            //actualizar datos
            datos.setnEstatus(5);
            manager.updateEstatusProcedSAC(conn, datos);
            //Guarda bitacora
            Util.bitacoraMovimientos(datos.getcOficioSolicitud(), "Se devuelve el procedimiento", datos.getcLogin(), conn);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
            manager = null;
            ;
        }
    }
}
