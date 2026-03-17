package com.syc.cfdi.core;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import com.syc.cfdi.ComponentesFactura;
import com.syc.cfdi.FacturaBusinessLogic;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ReemplazoCFDIBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(ReemplazoCFDIBusinessLogic.class);

    private FacturaBusinessLogic fbl = null;

    public ReemplazoCFDIBusinessLogic(String jniName, FacturaBusinessLogic fbl) throws Exception {
        super.init(jniName);
        this.fbl = fbl;
    }

    public void reemplazaFacturas(String tipoPago, int folio, String rfc, Usuario u, Map<String, ComponentesFactura> facturas) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            log.info("Object: {}", "Buscando caso para reemplazar facturas. Tramite: " + tipoPago + " folio: " + folio);
            Caso caso = CasoManager.findByFolioLike(conn, tipoPago, String.valueOf(folio));
            log.info("Object: {}", "Reemplazando facturas en: " + caso);
            int afectados = 0;
            afectados = ReemplazoCFDIManager.insertaFacturaReemplazada(conn, u.getLogin(), tipoPago, folio);
            log.debug("Object: " + String.valueOf("Se insertaron " + afectados + " facturas reemplazadas."));
            afectados = ReemplazoCFDIManager.insertaFacturaImpuestosReemplazada(conn, u.getLogin(), tipoPago, folio);
            log.debug("Object: " + String.valueOf("Se insertaron " + afectados + " Impuestos - Factura reemplazadas."));
            afectados = ReemplazoCFDIManager.insertaFacturaRetencionReemplazada(conn, u.getLogin(), tipoPago, folio);
            log.debug("Object: " + String.valueOf("Se insertaron " + afectados + " Retenciones  - Factura reemplazadas."));
            afectados = ReemplazoCFDIManager.insertaFacturaConceptosReemplazada(conn, u.getLogin(), tipoPago, folio);
            log.debug("Object: " + String.valueOf("Se insertaron " + afectados + " Conceptos  - Factura reemplazadas."));
            afectados = ReemplazoCFDIManager.insertaFacturaConceptoImpuestosReemplazada(conn, u.getLogin(), tipoPago, folio);
            log.debug("Object: " + String.valueOf("Se insertaron " + afectados + " Concepto - Impuesto  - Factura reemplazadas."));
            afectados = ReemplazoCFDIManager.insertaFacturaBonificacionReemplazada(conn, u.getLogin(), tipoPago, folio);
            log.debug("Object: " + String.valueOf("Se insertaron " + afectados + " Bonificacion  - Factura reemplazadas."));
            afectados = ReemplazoCFDIManager.insertaFacturaValesCombustibleReemplazada(conn, u.getLogin(), tipoPago, folio);
            log.debug("Object: " + String.valueOf("Se insertaron " + afectados + " Vales  - Factura reemplazadas."));
            int idCarpetaReemplazo = ReemplazoCFDIManager.creaEstructuraCarpeta(conn, caso, u.getLogin());
            ReemplazoCFDIManager.eliminaInformacionFacturas(conn, tipoPago, folio);
            log.debug("Se cambiaran las facturas a la carpeta de respaldo.");
            ReemplazoCFDIManager.mueveArchivosFacturas(conn, caso, idCarpetaReemplazo);
            fbl.insertaFacturas(conn, caso, u, facturas, false);
            // Si los montos entre lo insertado y lo eliminado son correctos da
            // commit, en otro caso se lanza error.
            ReemplazoCFDIManager.insertaBitacora(conn, u.getLogin(), tipoPago, folio);
            List<String> diferencias = ReemplazoCFDIManager.validaDiferencias(conn, tipoPago, folio);
            if (diferencias.size() > 0) {
                String token = "";
                String msgRetorno = "";
                for (int i = 0; i < diferencias.size(); i++) {
                    msgRetorno += token + diferencias.get(i);
                    token = "<br>";
                }
                throw new Exception(msgRetorno);
            }
            List<String> diferenciasForma = ReemplazoCFDIManager.validaFacturasTipo(conn, tipoPago, folio, "PPD");
            if (diferenciasForma.size() > 0) {
                String token = "";
                String msgRetorno = "";
                for (int i = 0; i < diferenciasForma.size(); i++) {
                    msgRetorno += token + diferenciasForma.get(i);
                    token = "<br>";
                }
                throw new Exception(msgRetorno);
            }
            conn.commit();
        } catch (Exception e) {
            Util.rollback(conn);
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public int getFolioPago(String tipoPago, String contrarecibo) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            return ReemplazoCFDIManager.getFoliopago(conn, tipoPago, contrarecibo);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public String getRFCPago(String tipoPago, String contrarecibo) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            return ReemplazoCFDIManager.getRFCPago(conn, tipoPago, contrarecibo);
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
