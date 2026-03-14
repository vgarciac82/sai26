package com.syc.sai.fonden.model;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import jakarta.servlet.http.HttpServletRequest;
import com.syc.dsmngr.DataSourceManager;
import com.syc.sai.fonden.FondenFacturacion;
import com.syc.sai.fonden.FondenFacturacionEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FondenFacturacionBusinessLogic extends DataSourceManager {

    Logger log = LoggerFactory.getLogger(FondenFacturacionBusinessLogic.class);

    public FondenFacturacion readFondenFacturacion(Integer cIdFonden, Integer nIdFondenMovimiento, Integer nIdFondenFacturacion) throws FondenFacturacionEngineException {
        Connection conn = null;
        try {
            conn = getConnection();
            FondenFacturacion fondenFacturacion = new FondenFacturacion();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return FondenFacturacionManager.readFondenFacturacion(conn, cIdFonden, nIdFondenMovimiento, nIdFondenFacturacion);
        } catch (Exception e) {
            throw new FondenFacturacionEngineException(e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e) {
                    log.error("Error cerrando la base de datos" + e, e);
                }
        }
    }

    public int saveOrUpdateFondenFacturacion(HttpServletRequest req) throws FondenFacturacionEngineException {
        Connection conn = null;
        try {
            conn = getConnection();
            FondenFacturacion fondenFacturacion = new FondenFacturacion();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            fondenFacturacion.setCnumero(req.getParameter("cnumero"));
            fondenFacturacion.setNcantidad(new Long(req.getParameter("ncantidad")));
            fondenFacturacion.setNimporteFactura(new Double(req.getParameter("nimporteFactura").replace("$", "").replace(",", "")));
            fondenFacturacion.setCidFonden(new Integer(req.getParameter("cidFonden")));
            fondenFacturacion.setNidFondenMovimiento(new Integer(req.getParameter("nidFondenMovimiento")));
            fondenFacturacion.setNidFondenFacturacion(new Integer(req.getParameter("nidFondenFacturacion")));
            fondenFacturacion.setnTipoCambio(new Double(req.getParameter("nTipoCambio").replace("$", "").replace(",", "")));
            fondenFacturacion.setnIdTipoPago(new Integer(req.getParameter("nIdTipoPago")));
            fondenFacturacion.setnIdTipoFactura(new Integer(req.getParameter("nIdTipoFactura")));
            fondenFacturacion.setcDescripcionFactura(req.getParameter("cDescripcionFactura"));
            req.getSession().setAttribute("fondenFacturacion", fondenFacturacion);
            return FondenFacturacionManager.saveOrUpdateFondenFacturacion(conn, fondenFacturacion);
        } catch (Exception e) {
            throw new FondenFacturacionEngineException(e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e) {
                    log.error("Error cerrando la base de datos" + e, e);
                }
        }
    }
}
