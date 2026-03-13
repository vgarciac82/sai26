package com.syc.ws.obrapublica;

import java.sql.Connection;
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONObject;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ObraPublicaManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.ws.inventario.RespuestaWS;
import com.syc.ws.inventario.WSManager;
import com.syc.ws.obrapublica.core.EstimacionObra;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WSObraPublicaEstadosBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(WSObraPublicaEstadosBusinessLogic.class);

    private String folioGenerator;

    AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);

    public WSObraPublicaEstadosBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public WSObraPublicaEstadosBusinessLogic(String jniName, String folioGenerator) {
        super.init(jniName);
        this.folioGenerator = folioGenerator;
    }

    public JSONObject ObtenerJsonObraPublicaEstados(int nIdEstado) throws Exception, SQLException {
        Connection conn = null;
        JSONObject jsonResp = null;
        JSONArray arrayObj = null;
        try {
            conn = getConnection();
            jsonResp = WSObraPublicaEstadosManager.ObtenerJsonObraPublicaEstados(conn, nIdEstado);
            // obtener los datos que se necesitan
            if (!"FALSE".equalsIgnoreCase(jsonResp.getString("estatus"))) {
                arrayObj = obtieneDatJSON((JSONArray) jsonResp.get("estados"));
                jsonResp.remove("estados");
                jsonResp.put("estados", arrayObj);
            }
            conn.commit();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            conn.rollback();
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return jsonResp;
    }

    private static JSONArray obtieneDatJSON(JSONArray arrayObj) throws Exception {
        JSONArray arrayObjResult = new JSONArray();
        JSONObject jsonObjResult = new JSONObject();
        try {
            jsonObjResult.put("Descripcion", "Seleccionar uno por favor.");
            jsonObjResult.put("id", 0);
            arrayObjResult.put(jsonObjResult);
            jsonObjResult = null;
            for (int i = 0; i < arrayObj.length(); i++) {
                jsonObjResult = new JSONObject();
                jsonObjResult.put("Descripcion", ((JSONObject) arrayObj.get(i)).getString("reName"));
                jsonObjResult.put("id", ((JSONObject) arrayObj.get(i)).getInt("idRealEstate"));
                arrayObjResult.put(jsonObjResult);
                jsonObjResult = null;
            }
            log.info("object JSON: " + arrayObjResult.toString());
        } finally {
            jsonObjResult = null;
        }
        return arrayObjResult;
    }

    public boolean registraEstimacionObra(EstimacionObra estimacion, Usuario u) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            ObraPublicaManager.registraEstimacionObra(conn, estimacion);
            int esFonden = ObraPublicaManager.esFONDEN(conn, estimacion);
            if (esFonden != 1) {
                //ARLA 08102021 si el contrato esta marcado con FONDEN no notifica al sistema de inmuebles
                if (estimacion.getNoEstimacion() > 0) {
                    RespuestaWS respuesta = WSManager.generatePublicWorkPartial(conn, estimacion.getFolioSAI(), estimacion.getNoEstimacion(), estimacion.isEsCapitalizable(), estimacion.isUltimaEstimacion(), u);
                    if (respuesta.getCode() >= 0) {
                        if (WSManager.EXITO.equalsIgnoreCase(respuesta.getEstatus()) || WSManager.EXISTE.equalsIgnoreCase(respuesta.getEstatus()))
                            ObraPublicaManager.actualizaEstimacionInfoInmueble(conn, estimacion, respuesta.getIdRePublicWorkPartial());
                        else if (WSManager.ERROR.equalsIgnoreCase(respuesta.getEstatus()))
                            throw new Exception("El llamado al sistema de inmuebles esta activo y regreso estatus de error. No se guardo informacion");
                    }
                } else {
                    log.info("La estimación de ancicipo no se reporta al sistema de inventario");
                }
            }
            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn(e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    /**
     * @return the folioGenerator
     */
    public String getFolioGenerator() {
        return folioGenerator;
    }

    /**
     * @param folioGenerator
     *            the folioGenerator to set
     */
    public void setFolioGenerator(String folioGenerator) {
        this.folioGenerator = folioGenerator;
    }
}
