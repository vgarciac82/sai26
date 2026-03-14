package com.syc.contable;

import java.sql.Connection;
import com.axtel.contratos.core.ContratoDiverso;
import com.axtel.contratos.core.ContratoDiversoManager;
import com.axtel.contratos.core.ContratoDiversoRetencion;
import com.axtel.contratos.exception.ContratoException;
import com.axtel.proveedores.ProveedorService;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContratoDiversoBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(ContratoDiversoBusinessLogic.class);

    public static final Integer ID_RETENCION_ISR_RESICO = 18;

    public static final Integer ID_RETENCION_ISR_HONORARIOS = 4;

    public ContratoDiversoBusinessLogic() {
    }

    /**
     * Construye un nuevo objeto.
     *
     * @param jniName
     */
    public ContratoDiversoBusinessLogic(String jniName) {
        super.init(jniName);
    }

    /**
     * Inserta un contrato diverso utilizando una conexion a la base de datos
     * previamente abierta. No realiza commit/rollback al terminar la operacion
     *
     * @param conn
     *            Conexion abierta a la base de datos
     * @param contrato
     *            Contrato a guardar.
     * @return Numero de registros insertados.
     * @throws ContratoException
     */
    public int insertaContratoDiverso(Connection conn, ContratoDiverso contrato) throws ContratoException {
        int insertados = ContratoDiversoManager.insertaContratoDiverso(conn, contrato);
        insertados += ContratoDiversoManager.insertaContratoEP(conn, contrato.getDetalleEP());
        insertados += ContratoDiversoManager.insertaContratoAnticipo(conn, contrato.getAnticipo());
        insertados += ContratoDiversoManager.insertaContratoRetencion(conn, contrato.getRetenciones());
        insertados += ContratoDiversoManager.insertaJustificacionCnet(conn, contrato);
        return insertados;
    }

    /**
     * Inserta un contrato diverso obteniendo conexion a la base de datos.
     *
     * @param contrato
     *            Contrato a guardar.
     * @return Numero de registros insertados.
     * @throws ContratoException
     */
    public int insertaContratoDiverso(ContratoDiverso contrato) throws ContratoException {
        Connection conn = null;
        try {
            conn = getConnection();
            log.info("Object: {}", "Insertando el contrato diverso: " + contrato);
            int insertados = ContratoDiversoManager.insertaContratoDiverso(conn, contrato);
            log.info("Object: {}", "Se insertaron: " + insertados + " contratos diversos");
            conn.commit();
            return insertados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback", e2);
                }
            throw new ContratoException("Problemas insertando contrato diverso en la DB: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void changeToRESICO(String login, String contractId) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            ContratoDiverso diverseContract = ContratoDiversoManager.select(conn, contractId);
            ContratoDiversoRetencion resicoWithholding = new ContratoDiversoRetencion();
            resicoWithholding.setCentroContable(diverseContract.getCentroContable());
            resicoWithholding.setEjercicio(diverseContract.getEjercicioFiscal());
            resicoWithholding.setIdContrato(diverseContract.getIdContrato());
            resicoWithholding.setIdTipoRetencion(ID_RETENCION_ISR_RESICO);
            ContratoDiversoManager.saveWithholdingEliminated(conn, contractId, login, ContratoDiversoBusinessLogic.ID_RETENCION_ISR_HONORARIOS, "Por cambio de regimen a RESICO.");
            ContratoDiversoManager.deleteWithholding(conn, contractId, ContratoDiversoBusinessLogic.ID_RETENCION_ISR_HONORARIOS);
            ContratoDiversoManager.insertaContratoRetencion(conn, resicoWithholding);
            ProveedorService.convertRESICO(conn, diverseContract);
            conn.commit();
        } catch (Exception e) {
            Util.rollback(conn);
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
