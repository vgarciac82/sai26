package com.axtel.egresos.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.axtel.egresos.entities.PaymentDiference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class PaymentValidatorRepository {

    private static final Logger log = LoggerFactory.getLogger(PaymentValidatorRepository.class);

    public List<PaymentDiference> validarPagos(int folioPago, Connection conn) {
        List<PaymentDiference> diferencias = new ArrayList<>();
        diferencias.addAll(validarRetencionesContratoVsCFDI(folioPago, conn));
        diferencias.addAll(validarEncabezadoVsRecepcion(folioPago, conn));
        diferencias.addAll(validarEncabezadoVsDetalle(folioPago, conn));
        return diferencias;
    }

    private List<PaymentDiference> validarRetencionesContratoVsCFDI(int folioPago, Connection conn) {
        String query = "SELECT cfdi.ctipopago, cfdi.nfoliopago, cfdi.retencionfacturas, " + "ISNULL(contrato.retencioncontrato, 0) AS retencionContrato, " + "cfdi.retencionfacturas - ISNULL(contrato.retencioncontrato, 0) AS diferencia " + "FROM (SELECT ctipopago, nfoliopago, SUM(mimporteretencion) AS retencionFacturas " + "FROM tpagofacturaretencion WHERE ctipopago = 'PAGODIVERSO' AND nfoliopago = ? " + "GROUP BY ctipopago, nfoliopago) AS cfdi " + "LEFT OUTER JOIN (SELECT ctipopago, nfoliopago, SUM(importeretencion) AS retencionContrato " + "FROM vpagoretencion WHERE ctipopago = 'PAGODIVERSO' AND nfoliopago = ? " + "GROUP BY ctipopago, nfoliopago) AS contrato " + "ON cfdi.ctipopago = contrato.ctipopago AND cfdi.nfoliopago = contrato.nfoliopago";
        return ejecutarValidacion(query, folioPago, conn, "Retenciones en contrato vs CFDI");
    }

    private List<PaymentDiference> validarEncabezadoVsRecepcion(int folioPago, Connection conn) {
        String query = "SELECT pd.mimporteneto, pd.mimporteretencion, rm.mmontoconiva AS totalRecepcion, " + "pd.mimporteneto + pd.mimporteretencion - rm.mmontoconiva AS diferencia " + "FROM tpagodiversoencabezado pd WITH(NOLOCK) " + "INNER JOIN mrecepcionpmat rm WITH(NOLOCK) " + "ON pd.cfoliopagodiverso = rm.cidpedcontdef AND pd.cidrecepmat = rm.cidrecepmat " + "WHERE nfoliopagodiverso = ?  AND ( ( (pd.mimporteneto + pd.mimporteretencion - rm.mmontoconiva) > 0 )\r\n" + "              OR ( (pd.mimporteneto + pd.mimporteretencion - rm.mmontoconiva) <\r\n" + "                   -0.03 ) ) ";
        return ejecutarValidacion(query, folioPago, conn, "Encabezado vs Recepción de Material");
    }

    private List<PaymentDiference> validarEncabezadoVsDetalle(int folioPago, Connection conn) {
        String query = "SELECT *\r\n" + "FROM   (SELECT pde.nfoliopagodiverso,\r\n" + "               pde.mimportebruto                                   AS\r\n" + "               brutoEncabezado,\r\n" + "               Isnull(pdd.mimportebruto, 0)                        AS\r\n" + "               brutoDetalle,\r\n" + "               pde.mimporteneto                                    AS\r\n" + "               netoEncabezado,\r\n" + "               Isnull(pdd.mimporteneto, 0)                         AS\r\n" + "               netoDetalle,\r\n" + "               pde.mimportemasiva                                  AS\r\n" + "                      importeMasIVAEncabezado,\r\n" + "               Isnull(pdd.mimportemasiva, 0)                       AS\r\n" + "                      importeMasIVADetalle,\r\n" + "               pde.mimporteretencion                               AS\r\n" + "                      importeRetencionEncabezado,\r\n" + "               Isnull(pdd.mretencion, 0)                           AS\r\n" + "                      importeRetencionDetalle,\r\n" + "               ( pde.mimportebruto - Isnull(pdd.mimportebruto, 0) ) + (\r\n" + "               pde.mimporteneto - Isnull(pdd.mimporteneto, 0) ) + (\r\n" + "               pde.mimportemasiva - Isnull(pdd.mimportemasiva, 0) ) + (\r\n" + "               pde.mimporteretencion - Isnull(pdd.mretencion, 0) ) AS diferencia\r\n" + "        FROM   tpagodiversoencabezado pde WITH(nolock)\r\n" + "               LEFT OUTER JOIN (SELECT nfoliopagodiverso,\r\n" + "                                       Sum(mimportebruto)  AS mImporteBruto,\r\n" + "                                       Sum(mimporteneto)   AS mImporteNeto,\r\n" + "                                       Sum(mimportemasiva) AS mImporteMasIVA,\r\n" + "                                       Sum(mretencion)     AS mRetencion\r\n" + "                                FROM   tpagodiversodetalle WITH(nolock)\r\n" + "                                GROUP  BY nfoliopagodiverso) AS pdd\r\n" + "                            ON pde.nfoliopagodiverso = pdd.nfoliopagodiverso\r\n" + "        WHERE  pde.nfoliopagodiverso = ?) AS validation_tbl\r\n" + " WHERE validation_tbl.diferencia > 0.02 ";
        return ejecutarValidacion(query, folioPago, conn, "Encabezado vs Detalle");
    }

    private List<PaymentDiference> ejecutarValidacion(String query, int folioPago, Connection conn, String tipoDiferencia) {
        List<PaymentDiference> diferencias = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, folioPago);
            if ("Retenciones en contrato vs CFDI".equals(tipoDiferencia))
                ps.setInt(2, folioPago);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double diferencia = rs.getDouble("diferencia");
                    if (diferencia != 0) {
                        diferencias.add(new PaymentDiference(tipoDiferencia, diferencia));
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error al ejecutar validación: " + tipoDiferencia, e);
            throw new RuntimeException(e);
        }
        return diferencias;
    }
}
