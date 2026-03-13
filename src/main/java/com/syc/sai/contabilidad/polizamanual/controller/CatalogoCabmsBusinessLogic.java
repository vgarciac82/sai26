package com.syc.sai.contabilidad.polizamanual.controller;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.syc.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.polizamanual.CatalogoCabms;
import com.syc.sai.contabilidad.polizamanual.CatalogoCabmsEngineException;
import com.syc.sai.contabilidad.polizamanual.model.CatalogoCabmsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatalogoCabmsBusinessLogic extends DataSourceManager {

    Logger log = LoggerFactory.getLogger(CatalogoCabmsBusinessLogic.class);

    public List<CatalogoCabms> autocompleteCabms(HttpServletRequest req) throws CatalogoCabmsEngineException {
        Connection conn = null;
        try {
            conn = getConnection();
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String nGrupo = (String) req.getSession().getAttribute("nGrupo");
            String nSubGrupo = (String) req.getSession().getAttribute("nSubGrupo");
            String nEvento = (String) req.getSession().getAttribute("nEvento");
            //String nCABMS = req.getParameter("term");
            String restrictions = "" + "cPartida in (" + "				SELECT DISTINCT cPartida FROM tEventoManual " + "				where 		cIdGrupoEvento=" + nGrupo + " " + "						AND cIdSubGrupoEvento=" + nSubGrupo + " " + "						AND cIdEventoManual=" + nEvento + " and cModulo != 'CAJA');";
            return CatalogoCabmsManager.readCatalogoCabmsBy(conn, restrictions);
        } catch (Exception e) {
            throw new CatalogoCabmsEngineException(e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e) {
                    log.error("Error cerrando la base de datos" + e, e);
                }
        }
    }

    public List<CatalogoCabms> readCatalogoCabmsBy(String restrictions) throws CatalogoCabmsEngineException {
        Connection conn = null;
        try {
            conn = getConnection();
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return CatalogoCabmsManager.readCatalogoCabmsBy(conn, restrictions);
        } catch (Exception e) {
            throw new CatalogoCabmsEngineException(e);
        } finally {
        }
    }

    public int saveOrUpdateCatalogoCabms(HttpServletRequest req) throws CatalogoCabmsEngineException {
        Connection conn = null;
        try {
            conn = getConnection();
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            CatalogoCabms cabms = new CatalogoCabms();
            cabms.setCdescripcion(req.getParameter("cdescripcion"));
            cabms.setNcuenta(req.getParameter("ncuenta"));
            cabms.setNidUnidadMedida(req.getParameter("nidUnidadMedida"));
            cabms.setCcabms(req.getParameter("ccabms"));
            cabms.setCcucop(new Integer(req.getParameter("ccucop")));
            cabms.setCpartida(req.getParameter("cpartida"));
            req.getSession().setAttribute("cabms", cabms);
            return CatalogoCabmsManager.saveOrUpdateCatalogoCabms(conn, cabms);
        } catch (Exception e) {
            throw new CatalogoCabmsEngineException(e);
        } finally {
        }
    }
}
