package com.syc.adquisiciones.businessLogic;

import java.sql.Connection;
import java.sql.SQLException;
import com.syc.adquisiciones.core.DatosProcedimiento;
import com.syc.adquisiciones.manager.ProcedimientoManager;
import com.syc.adquisiciones.util.Util;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcedimientoBusinesLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(ProcedimientoBusinesLogic.class);

    public String reclasificaTipoProcedimiento(DatosProcedimiento datProced, Usuario usuario) throws Exception {
        Connection conn = null;
        ProcedimientoManager manager = null;
        String msg = "";
        String token = "";
        int nIdProcedimientoAdj = 0;
        boolean resp = true;
        try {
            conn = getConnection();
            manager = new ProcedimientoManager();
            if (datProced.getmMontoNetoContrato() <= 0.00) {
                resp = false;
                msg = "Error, no se recibio el monto neto del contrato.";
                token = "\n";
                log.warn(msg);
            }
            if (!manager.validaContJustificado(conn, datProced) && !manager.validaTipoProcedPorMonto(conn, datProced.getnCategoriaProcedimientoNuevo(), datProced.getmMontoNetoContrato())) {
                resp = false;
                msg = msg + token + "El tipo de procedimiento seleccionado no corresponde con el monto total del contrato.";
                log.warn(msg);
            }
            if (resp) {
                nIdProcedimientoAdj = manager.getNidConsecutivoAdj(conn, datProced.getcIdContratoDef());
                if (!(datProced.getnFundamentoLegalNuevo() == datProced.getnFundamentoLegal() && datProced.getnCategoriaProcedimientoNuevo() == datProced.getnCategoriaProcedimiento())) {
                    manager.actualizamProcedimiento(conn, datProced.getcIdProcedimiento(), datProced.getnFundamentoLegalNuevo(), datProced.getnCategoriaProcedimientoNuevo());
                    manager.actualizamProcedimientoAdjudicacion(conn, datProced.getcIdProcedimiento(), datProced.getnFundamentoLegalNuevo(), datProced.getcIdRFC(), nIdProcedimientoAdj);
                    Util.bitacoraMovimientos(datProced.getcIdContratoDef(), "Actualiza Fundamento Legal", usuario.getLogin(), conn);
                    if (datProced.getnCategoriaProcedimientoNuevo() != datProced.getnCategoriaProcedimiento()) {
                        if (datProced.getFechas() != null) {
                            manager.borraFechas(conn, datProced.getcIdProcedimiento());
                            manager.guardaFechas(conn, datProced.getcIdProcedimiento(), datProced.getFechas(), datProced.getcIdContratoDef());
                            Util.bitacoraMovimientos(datProced.getcIdContratoDef(), "Se Modifican Fechas", usuario.getLogin(), conn);
                            msg = "Datos Actualizados:\nTipo de procedimiento.\nFundamento Legal \nY Fechas.";
                            log.info(msg);
                        } else {
                            resp = false;
                            msg = "Error en Fechas, el objeto fechas es nulo.";
                            log.warn(msg);
                        }
                    } else {
                        msg = "Datos Actualizados:\nFundamento Legal.";
                        log.info(msg);
                    }
                } else {
                    resp = false;
                    msg = "No es necesario actualizar, por que no hay cambios.";
                    log.warn(msg);
                }
            }
            if (resp) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            // TODO: handle exception
            msg = e.getMessage().toString();
            if (conn != null) {
                conn.rollback();
            }
            log.error(e);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
            manager = null;
            token = null;
        }
        return msg;
    }
}
