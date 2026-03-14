package com.syc.reportes.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

public class ReporteIngresosManager {

    private static final Logger log = LoggerFactory.getLogger(ReporteIngresosManager.class);

    public static String ReporteIngresos(Connection conn, String fechaInicio, String fechaFin, String cClave, String columnas, String SNP, String INGM, Map<String, String> plantillas) throws Exception {
        log.info("Object: {}", String.format("Iniciando reporte de Ingresos"));
        long startQueries = System.currentTimeMillis();
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        String[] arrayTupla = columnas.split(",");
        String[] arrayValores = null;
        String C = "";
        String CG = "";
        String GB = "";
        for (int i = 0; i < arrayTupla.length; i++) {
            arrayValores = arrayTupla[i].split(",");
            if ("cClave".equals(arrayValores[0])) {
                C += ", cClave AS \"CLAVE\", dClave AS \"DENOMINACIÓN O RAZÓN SOCIAL DE QUIEN ORIGINÓ EL INGRESO\"";
                CG += ", V.cClave AS \"CLAVE\", dClave AS \"DENOMINACIÓN O RAZÓN SOCIAL DE QUIEN ORIGINÓ EL INGRESO\"";
                GB += ", V.cClave";
            }
            if ("cOrigenTransferencia".equals(arrayValores[0])) {
                C += ", cOrigenTransferencia AS \"DENOMINACIÓN O RAZÓN SOCIAL DE LA PERSONA QUE REALIZÓ LA TRANSFERENCIA O DEPÓSITO\"";
                CG += ", V.cOrigenTransferencia AS \"DENOMINACIÓN O RAZÓN SOCIAL DE LA PERSONA QUE REALIZÓ LA TRANSFERENCIA O DEPÓSITO\"";
                GB += ", V.cOrigenTransferencia";
            }
            if ("cNombreGestion".equals(arrayValores[0])) {
                C += ", cNombreGestion AS \"CONTACTO CONAFOR QUE GESTIONÓ EL ORIGEN DEL RECURSO\"";
                CG += ", V.cNombreGestion AS \"CONTACTO CONAFOR QUE GESTIONÓ EL ORIGEN DEL RECURSO\"";
                GB += ", V.cNombreGestion";
            }
            if ("nesExtranjero".equals(arrayValores[0])) {
                C += ", nesExtranjero AS \"ES EXTRANJERO O MÉXICANO?\"";
                CG += ", V.nesExtranjero AS \"ES EXTRANJERO O MÉXICANO?\"";
                GB += ", V.nesExtranjero";
            }
            if ("cesDonativo".equals(arrayValores[0])) {
                C += ", cesDonativo AS \"ES DONATIVO? SI/NO\"";
                CG += ", V.cesDonativo AS \"ES DONATIVO? SI/NO\"";
                GB += ", V.cesDonativo";
            }
            if ("cComprobanteFiscal".equals(arrayValores[0])) {
                C += ", cComprobanteFiscal AS \"REQUIERE COMPROBANTE FISCAL (CFDI)? SI/NO\"";
                CG += ", V.cComprobanteFiscal AS \"REQUIERE COMPROBANTE FISCAL (CFDI)? SI/NO\"";
                GB += ", V.cComprobanteFiscal";
            }
        }
        String fileName = "";
        try {
            if ("".equals(cClave)) {
                /*INGRESOS PROPIOS*/
                query.append("SELECT Folio AS \"FOLIO INGRESO\"");
                query.append(", nFolioPoliza AS \"NÚMERO DE POLIZA CONTABLE\"");
                query.append(", cConcepto AS \"CONCEPTO QUE ORIGINÓ EL INGRESO\"");
                query.append(", fAplicacion AS \"FECHA DE APLICACIÓN CONTABLE\"");
                query.append(", fRecepcionRecurso AS \"FECHA DE RECEPCIÓN DEL RECURSO (SEGÚN ESTADO DE CUENTA)\"");
                query.append(", mImporte AS \"(+)IMPORTE IP\"");
                query.append(", mImporteRendimientos AS \"(+)IMPORTE RENDIMIENTOS\"");
                query.append(", mImporteRendimientosGreenMex AS \"(+)IMPORTE RENDIMIENTOS GREENMEX\"");
                query.append(", GreenMexBancario AS \"(+)GREENMEX BANCARIO\"");
                query.append(", GreenMexPagado AS \"(+)SOLICITUDES GREENMEX\"");
                query.append(", GreenMexNoPagado AS \"(-)SOLICITUDES GREENMEX NO PAGADAS\"");
                query.append(", CASE WHEN cClave <> 'INT_GREENMEX' AND FOLIO NOT LIKE 'SNP%' THEN mImporte + mImporteRendimientos + GreenMexPagado + GreenMexNoPagado ELSE 0 END AS \"TOTALES IP, NO SE CONSIDERA EL RECURSO BANCARIO DE GREENMEX\"");
                query.append(", nCuenta AS \"CUENTA CONTABLE\"");
                query.append(", cClaveCRI AS \"CLASIFICADOR POR RUBRO DE INGRESOS\"");
                query.append(", dCRI AS \"NOMBRE CLASIFICADOR POR RUBRO DE INGRESOS\"");
                query.append(", CTAB AS \"CUENTA BANCARIA\"");
                if (!"".equals(columnas)) {
                    query.append(C);
                }
                query.append(" FROM vReporteIP");
                query.append(" WHERE CAST(fAplicacion AS DATE) BETWEEN CAST('" + fechaInicio + "' AS DATE) AND CAST('" + fechaFin + "' AS DATE)");
                if ("NO".equals(SNP)) {
                    query.append(" AND Folio NOT LIKE 'GMB%'");
                }
                if ("SI".equals(INGM)) {
                    query.append(" UNION ");
                    /*PAGOS*/
                    query.append("SELECT V.caNoContrarrecibo AS \"FOLIO INGRESO\"");
                    query.append(", V.nFolioPoliza AS \"NÚMERO DE POLIZA CONTABLE\"");
                    query.append(", V.cConcepto AS \"CONCEPTO QUE ORIGINÓ EL INGRESO\"");
                    query.append(", V.fAplicacion AS \"FECHA DE APLICACIÓN CONTABLE\"");
                    query.append(", fRecepcionRecurso AS \"FECHA DE RECEPCIÓN DEL RECURSO (SEGÚN ESTADO DE CUENTA)\"");
                    query.append(", 0 AS \"(+)IMPORTE IP\"");
                    query.append(", 0 AS \"(+)IMPORTE RENDIMIENTOS\"");
                    query.append(", 0 AS \"(+)IMPORTE RENDIMIENTOS GREENMEX\"");
                    query.append(", 0 AS \"(+)GREENMEX BANCARIO\"");
                    query.append(", CASE WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) OR CAST(PAGE.fAplicacion AS DATE) IS NOT NULL THEN SUM(PAGD.mImporte) ELSE 0 END AS \"(+)SOLICITUDES GREENMEX\"");
                    query.append(", CASE WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) OR CAST(PAGE.fAplicacion AS DATE) IS NOT NULL THEN 0 ELSE GreenMex*-1 END AS \"(-)SOLICITUDES GREENMEX NO PAGADAS\"");
                    query.append(", (CASE WHEN CAST('" + fechaFin + "' AS DATE) < CAST(PAGE.fAplicacion AS DATE) OR CAST(PAGE.fAplicacion AS DATE) IS NULL THEN 0 ELSE SUM(PAGD.mImporte) END) + (CASE WHEN CAST('" + fechaFin + "' AS DATE) < CAST(PAGE.fAplicacion AS DATE) OR CAST(PAGE.fAplicacion AS DATE) IS NULL THEN GreenMex*-1 ELSE 0 END) AS \"TOTALES IP, NO SE CONSIDERA EL RECURSO BANCARIO DE GREENMEX\"");
                    query.append(", nCuenta AS \"CUENTA CONTABLE\"");
                    query.append(", cClaveCRI AS \"CLASIFICADOR POR RUBRO DE INGRESOS\"");
                    query.append(", dCRI AS \"NOMBRE CLASIFICADOR POR RUBRO DE INGRESOS\"");
                    query.append(", '' AS \"CUENTA BANCARIA\"");
                    if (!"".equals(columnas)) {
                        query.append(CG);
                    }
                    query.append(" FROM vPagosGreenMex V");
                    query.append(" LEFT JOIN tPagadoEncabezado PAGE WITH (NOLOCK) ON V.caNoContrarrecibo = PAGE.caNoContrarrecibo");
                    query.append(" LEFT JOIN tPagadoDetalle PAGD WITH (NOLOCK) ON PAGE.nFolioPagado = PAGD.nFolioPagado");
                    query.append(" WHERE CAST(V.fAplicacion AS DATE) BETWEEN CAST('" + fechaInicio + "' AS DATE) AND CAST('" + fechaFin + "' AS DATE)");
                    query.append(" GROUP BY V.caNoContrarrecibo, V.nFolioPoliza, V.cTipoPoliza, V.cConcepto, V.fAplicacion, fRecepcionRecurso");
                    query.append(" ,GreenMex, nCuenta, dCuenta, cClaveCRI, dCRI, cClave, dClave, PAGE.fAplicacion");
                    if (!"".equals(columnas)) {
                        query.append(GB);
                    }
                    query.append(" UNION ");
                    /*OPERACIONES AJENAS*/
                    query.append("SELECT V.caNoContrarrecibo + '-' + ISNULL(OAE.caNoContrarrecibo,'10OA') AS \"FOLIO INGRESO\"");
                    query.append(", V.nFolioPoliza AS \"NÚMERO DE POLIZA CONTABLE\"");
                    query.append(", V.cConcepto AS \"CONCEPTO QUE ORIGINÓ EL INGRESO\"");
                    query.append(", V.fAplicacion AS \"FECHA DE APLICACIÓN CONTABLE\"");
                    query.append(", fRecepcionRecurso AS \"FECHA DE RECEPCIÓN DEL RECURSO (SEGÚN ESTADO DE CUENTA)\"");
                    query.append(", 0 AS \"(+)IMPORTE IP\"");
                    query.append(", 0 AS \"(+)IMPORTE RENDIMIENTOS\"");
                    query.append(", 0 AS \"(+)IMPORTE RENDIMIENTOS GREENMEX\"");
                    query.append(", 0 AS \"(+)GREENMEX BANCARIO\"");
                    query.append(", CASE WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) AND OAD.caNoContrarrecibo IS NOT NULL THEN OAD.mRetencion ELSE 0 END AS \"(+)SOLICITUDES GREENMEX\"");
                    query.append(", CASE WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) AND OAD.caNoContrarrecibo IS NOT NULL THEN 0 WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) THEN OAD.mRetencion * -1 ELSE OAD.mRetencion * -1 END AS \"(-)SOLICITUDES GREENMEX NO PAGADAS\"");
                    query.append(", (CASE WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) AND OAD.caNoContrarrecibo IS NOT NULL THEN OAD.mRetencion ELSE 0 END) + (CASE WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) AND OAD.caNoContrarrecibo IS NOT NULL THEN 0 WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) THEN OAD.mRetencion * -1 ELSE OAD.mRetencion * -1 END) AS \"TOTALES IP, NO SE CONSIDERA EL RECURSO BANCARIO DE GREENMEX\"");
                    query.append(", nCuenta AS \"CUENTA CONTABLE\"");
                    query.append(", cClaveCRI AS \"CLASIFICADOR POR RUBRO DE INGRESOS\"");
                    query.append(", dCRI AS \"NOMBRE CLASIFICADOR POR RUBRO DE INGRESOS\"");
                    query.append(", '' AS \"CUENTA BANCARIA\"");
                    if (!"".equals(columnas)) {
                        query.append(CG);
                    }
                    query.append(" FROM vPagosGreenMex V");
                    query.append(" LEFT JOIN (SELECT caNoContrarrecibo, nFolioOperAjenas, SUM(mTotal) AS mRetencion FROM tOperAjenasDetalle WITH (NOLOCK) GROUP BY caNoContrarrecibo, nFolioOperAjenas) AS OAD ON V.caNoContrarrecibo = OAD.caNoContrarrecibo");
                    query.append(" LEFT JOIN tOperAjenasEncabezado OAE WITH (NOLOCK) ON OAD.nFolioOperAjenas = OAE.nFolioOperAjenas");
                    query.append(" LEFT JOIN tPagadoEncabezado PAGE WITH (NOLOCK) ON OAE.caNoContrarrecibo = PAGE.caNoContrarrecibo");
                    query.append(" WHERE CAST(V.fAplicacion AS DATE) BETWEEN CAST('" + fechaInicio + "' AS DATE) AND CAST('" + fechaFin + "' AS DATE) AND OAD.caNoContrarrecibo IS NOT NULL");
                    query.append(" GROUP BY V.caNoContrarrecibo, V.nFolioPoliza, V.cTipoPoliza, V.cConcepto, V.fAplicacion, fRecepcionRecurso, OAD.mRetencion");
                    query.append(" ,GreenMex, nCuenta, dCuenta, cClaveCRI, dCRI, cClave, dClave, PAGE.fAplicacion, OAD.caNoContrarrecibo, OAE.caNoContrarrecibo");
                    if (!"".equals(columnas)) {
                        query.append(GB);
                    }
                    query.append(" UNION ");
                    /*PENAS CONVENCIONALES*/
                    query.append("SELECT V.caNoContrarrecibo + '-' + ISNULL(OAE.caNoContrarrecibo,'10OA') AS \"FOLIO INGRESO\"");
                    query.append(", V.nFolioPoliza AS \"NÚMERO DE POLIZA CONTABLE\"");
                    query.append(", V.cConcepto AS \"CONCEPTO QUE ORIGINÓ EL INGRESO\"");
                    query.append(", V.fAplicacion AS \"FECHA DE APLICACIÓN CONTABLE\"");
                    query.append(", fRecepcionRecurso AS \"FECHA DE RECEPCIÓN DEL RECURSO (SEGÚN ESTADO DE CUENTA)\"");
                    query.append(", 0 AS \"(+)IMPORTE IP\"");
                    query.append(", 0 AS \"(+)IMPORTE RENDIMIENTOS\"");
                    query.append(", 0 AS \"(+)IMPORTE RENDIMIENTOS GREENMEX\"");
                    query.append(", 0 AS \"(+)GREENMEX BANCARIO\"");
                    query.append(", CASE WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) AND OAD.caNoContrarrecibo IS NOT NULL THEN OAD.mRetencion ELSE 0 END AS \"(+)SOLICITUDES GREENMEX\"");
                    query.append(", CASE WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) AND OAD.caNoContrarrecibo IS NOT NULL THEN 0 WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) THEN OAD.mRetencion * -1 ELSE OAD.mRetencion * -1 END AS \"(-)SOLICITUDES GREENMEX NO PAGADAS\"");
                    query.append(", (CASE WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) AND OAD.caNoContrarrecibo IS NOT NULL THEN OAD.mRetencion ELSE 0 END) + (CASE WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) AND OAD.caNoContrarrecibo IS NOT NULL THEN 0 WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) THEN OAD.mRetencion * -1 ELSE OAD.mRetencion * -1 END) AS \"TOTALES IP, NO SE CONSIDERA EL RECURSO BANCARIO DE GREENMEX\"");
                    query.append(", nCuenta AS \"CUENTA CONTABLE\"");
                    query.append(", cClaveCRI AS \"CLASIFICADOR POR RUBRO DE INGRESOS\"");
                    query.append(", dCRI AS \"NOMBRE CLASIFICADOR POR RUBRO DE INGRESOS\"");
                    query.append(", '' AS \"CUENTA BANCARIA\"");
                    if (!"".equals(columnas)) {
                        query.append(CG);
                    }
                    query.append(" FROM vPagosGreenMex V");
                    query.append(" LEFT JOIN (SELECT caNoContrarrecibo, nFolioPagoPenasConv, SUM(mImporteMasIva) AS mRetencion FROM tPagoPenasConvDetalle WITH (NOLOCK) GROUP BY caNoContrarrecibo, nFolioPagoPenasConv) AS OAD ON V.caNoContrarrecibo = OAD.caNoContrarrecibo");
                    query.append(" LEFT JOIN tPagoPenasConvEncabezado OAE WITH (NOLOCK) ON OAD.nFolioPagoPenasConv = OAE.nFolioPagoPenasConv");
                    query.append(" LEFT JOIN tPagadoEncabezado PAGE WITH (NOLOCK) ON OAE.caNoContrarrecibo = PAGE.caNoContrarrecibo");
                    query.append(" WHERE CAST(V.fAplicacion AS DATE) BETWEEN CAST('" + fechaInicio + "' AS DATE) AND CAST('" + fechaFin + "' AS DATE) AND OAD.caNoContrarrecibo IS NOT NULL");
                    query.append(" GROUP BY V.caNoContrarrecibo, V.nFolioPoliza, V.cTipoPoliza, V.cConcepto, V.fAplicacion, fRecepcionRecurso, OAD.mRetencion");
                    query.append(" ,GreenMex, nCuenta, dCuenta, cClaveCRI, dCRI, cClave, dClave, PAGE.fAplicacion, OAD.caNoContrarrecibo, OAE.caNoContrarrecibo");
                    if (!"".equals(columnas)) {
                        query.append(GB);
                    }
                    query.append(" UNION ");
                    /*SALDOS OPERACIONES AJENAS*/
                    query.append("SELECT V.caNoContrarrecibo + '-' + '10OA' AS \"FOLIO INGRESO\"");
                    query.append(", V.nFolioPoliza AS \"NÚMERO DE POLIZA CONTABLE\"");
                    query.append(", V.cConcepto AS \"CONCEPTO QUE ORIGINÓ EL INGRESO\"");
                    query.append(", V.fAplicacion AS \"FECHA DE APLICACIÓN CONTABLE\"");
                    query.append(", fRecepcionRecurso AS \"FECHA DE RECEPCIÓN DEL RECURSO (SEGÚN ESTADO DE CUENTA)\"");
                    query.append(", 0 AS \"(+)IMPORTE IP\"");
                    query.append(", 0 AS \"(+)IMPORTE RENDIMIENTOS\"");
                    query.append(", 0 AS \"(+)IMPORTE RENDIMIENTOS GREENMEX\"");
                    query.append(", 0 AS \"(+)GREENMEX BANCARIO\"");
                    query.append(", 0 \"(+)SOLICITUDES GREENMEX\"");
                    query.append(", CASE WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('30/04/2025' AS DATE) AND OAE.caNoContrarrecibo IS NOT NULL THEN SALDO.mRetencion*-1 WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('30/04/2025' AS DATE) THEN SALDO.mRetencion * -1 ELSE SALDO.mRetencion * -1 END AS \"(-)SOLICITUDES GREENMEX NO PAGADAS\"");
                    query.append(", CASE WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('30/04/2025' AS DATE) AND OAE.caNoContrarrecibo IS NOT NULL THEN SALDO.mRetencion*-1 WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('30/04/2025' AS DATE) THEN SALDO.mRetencion * -1 ELSE SALDO.mRetencion * -1 END AS \"TOTALES IP, NO SE CONSIDERA EL RECURSO BANCARIO DE GREENMEX\"");
                    query.append(", nCuenta AS \"CUENTA CONTABLE\"");
                    query.append(", cClaveCRI AS \"CLASIFICADOR POR RUBRO DE INGRESOS\"");
                    query.append(", dCRI AS \"NOMBRE CLASIFICADOR POR RUBRO DE INGRESOS\"");
                    query.append(", '' AS \"CUENTA BANCARIA\"");
                    if (!"".equals(columnas)) {
                        query.append(CG);
                    }
                    query.append(" FROM vPagosGreenMex V");
                    query.append(" LEFT JOIN (SELECT caNoContrarrecibo ,SUM(Total) AS mRetencion FROM v_OperacionesAjenasSaldo WITH (NOLOCK) GROUP BY caNoContrarrecibo) AS SALDO ON V.caNoContrarrecibo = SALDO.caNoContrarrecibo");
                    query.append(" LEFT JOIN tOperAjenasDetalle OAE WITH (NOLOCK) ON SALDO.caNoContrarrecibo = OAE.caNoContrarrecibo");
                    query.append(" LEFT JOIN tPagadoEncabezado PAGE WITH (NOLOCK) ON OAE.caNoContrarrecibo = PAGE.caNoContrarrecibo");
                    query.append(" WHERE CAST(V.fAplicacion AS DATE) BETWEEN CAST('" + fechaInicio + "' AS DATE) AND CAST('" + fechaFin + "' AS DATE) AND SALDO.caNoContrarrecibo IS NOT NULL");
                    query.append(" GROUP BY V.caNoContrarrecibo, V.nFolioPoliza, V.cTipoPoliza, V.cConcepto, V.fAplicacion, fRecepcionRecurso, SALDO.mRetencion");
                    query.append(" ,GreenMex, nCuenta, dCuenta, cClaveCRI, dCRI, cClave, dClave, PAGE.fAplicacion, SALDO.caNoContrarrecibo, OAE.caNoContrarrecibo");
                    if (!"".equals(columnas)) {
                        query.append(GB);
                    }
                }
                log.trace("Object: {}", "Select[" + query + "]");
                ps = conn.prepareStatement(query.toString());
            } else {
                query.append("SELECT Folio AS \"FOLIO INGRESO\"");
                query.append(", nFolioPoliza AS \"NÚMERO DE POLIZA CONTABLE\"");
                query.append(", cConcepto AS \"CONCEPTO QUE ORIGINÓ EL INGRESO\"");
                query.append(", fAplicacion AS \"FECHA DE APLICACIÓN CONTABLE\"");
                query.append(", fRecepcionRecurso AS \"FECHA DE RECEPCIÓN DEL RECURSO (SEGÚN ESTADO DE CUENTA)\"");
                query.append(", mImporte AS \"(+)IMPORTE IP\"");
                query.append(", mImporteRendimientos AS \"(+)IMPORTE RENDIMIENTOS\"");
                query.append(", mImporteRendimientosGreenMex AS \"(+)IMPORTE RENDIMIENTOS GREENMEX\"");
                query.append(", GreenMexBancario AS \"(+)GREENMEX BANCARIO\"");
                query.append(", GreenMexPagado AS \"(+)SOLICITUDES GREENMEX\"");
                query.append(", GreenMexNoPagado AS \"(-)SOLICITUDES GREENMEX NO PAGADAS\"");
                query.append(", CASE WHEN cClave <> 'INT_GREENMEX' AND FOLIO NOT LIKE 'SNP%' THEN mImporte + mImporteRendimientos + GreenMexPagado + GreenMexNoPagado ELSE 0 END AS \"TOTALES IP, NO SE CONSIDERA EL RECURSO BANCARIO DE GREENMEX\"");
                query.append(", nCuenta AS \"CUENTA CONTABLE\"");
                query.append(", cClaveCRI AS \"CLASIFICADOR POR RUBRO DE INGRESOS\"");
                query.append(", dCRI AS \"NOMBRE CLASIFICADOR POR RUBRO DE INGRESOS\"");
                query.append(", CTAB AS \"CUENTA BANCARIA\"");
                if (!"".equals(columnas)) {
                    query.append(C);
                }
                query.append(" FROM vReporteIP");
                query.append(" WHERE CAST(fAplicacion AS DATE) BETWEEN CAST('" + fechaInicio + "' AS DATE) AND CAST('" + fechaFin + "' AS DATE)");
                query.append(" AND cClave = '" + cClave + "'");
                if ("SI".equals(INGM)) {
                    query.append(" UNION ");
                    query.append("SELECT V.caNoContrarrecibo AS \"FOLIO INGRESO\"");
                    query.append(", V.nFolioPoliza AS \"NÚMERO DE POLIZA CONTABLE\"");
                    query.append(", V.cConcepto AS \"CONCEPTO QUE ORIGINÓ EL INGRESO\"");
                    query.append(", V.fAplicacion AS \"FECHA DE APLICACIÓN CONTABLE\"");
                    query.append(", fRecepcionRecurso AS \"FECHA DE RECEPCIÓN DEL RECURSO (SEGÚN ESTADO DE CUENTA)\"");
                    query.append(", 0 AS \"(+)IMPORTE IP\"");
                    query.append(", 0 AS \"(+)IMPORTE RENDIMIENTOS\"");
                    query.append(", 0 AS \"(+)IMPORTE RENDIMIENTOS GREENMEX\"");
                    query.append(", 0 AS \"(+)GREENMEX BANCARIO\"");
                    query.append(", CASE WHEN CAST('" + fechaFin + "' AS DATE) <= CAST(PAGE.fAplicacion AS DATE) OR CAST(PAGE.fAplicacion AS DATE) IS NULL THEN 0 ELSE SUM(PAGD.mImporte) END AS \"(+)SOLICITUDES GREENMEX\"");
                    query.append(", CASE WHEN CAST('" + fechaFin + "' AS DATE) <= CAST(PAGE.fAplicacion AS DATE) OR CAST(PAGE.fAplicacion AS DATE) IS NULL THEN GreenMex*-1 ELSE 0 END AS \"(-)SOLICITUDES GREENMEX NO PAGADAS\"");
                    query.append(", (CASE WHEN CAST('" + fechaFin + "' AS DATE) <= CAST(PAGE.fAplicacion AS DATE) OR CAST(PAGE.fAplicacion AS DATE) IS NULL THEN 0 ELSE SUM(PAGD.mImporte) END) + (CASE WHEN CAST('" + fechaFin + "' AS DATE) <= CAST(PAGE.fAplicacion AS DATE) OR CAST(PAGE.fAplicacion AS DATE) IS NULL THEN GreenMex*-1 ELSE 0 END) AS \"TOTALES IP, NO SE CONSIDERA EL RECURSO BANCARIO DE GREENMEX\"");
                    query.append(", nCuenta AS \"CUENTA CONTABLE\"");
                    query.append(", cClaveCRI AS \"CLASIFICADOR POR RUBRO DE INGRESOS\"");
                    query.append(", dCRI AS \"NOMBRE CLASIFICADOR POR RUBRO DE INGRESOS\"");
                    query.append(", '' AS \"CUENTA BANCARIA\"");
                    if (!"".equals(columnas)) {
                        query.append(CG);
                    }
                    query.append(" FROM vPagosGreenMex V");
                    query.append(" LEFT JOIN tPagadoEncabezado PAGE WITH (NOLOCK) ON V.caNoContrarrecibo = PAGE.caNoContrarrecibo");
                    query.append(" LEFT JOIN tPagadoDetalle PAGD WITH (NOLOCK) ON PAGE.nFolioPagado = PAGD.nFolioPagado");
                    query.append(" WHERE CAST(V.fAplicacion AS DATE) BETWEEN CAST('" + fechaInicio + "' AS DATE) AND CAST('" + fechaFin + "' AS DATE)");
                    query.append(" AND cClave = '" + cClave + "'");
                    query.append(" GROUP BY V.caNoContrarrecibo, V.nFolioPoliza, V.cTipoPoliza, V.cConcepto, V.fAplicacion, fRecepcionRecurso");
                    query.append(" ,GreenMex, nCuenta, dCuenta, cClaveCRI, dCRI, cClave, dClave, PAGE.fAplicacion");
                    if (!"".equals(columnas)) {
                        query.append(GB);
                    }
                    query.append(" UNION ");
                    /*OPERACIONES AJENAS*/
                    query.append("SELECT V.caNoContrarrecibo + '-' + ISNULL(OAE.caNoContrarrecibo,'10OA') AS \"FOLIO INGRESO\"");
                    query.append(", V.nFolioPoliza AS \"NÚMERO DE POLIZA CONTABLE\"");
                    query.append(", V.cConcepto AS \"CONCEPTO QUE ORIGINÓ EL INGRESO\"");
                    query.append(", V.fAplicacion AS \"FECHA DE APLICACIÓN CONTABLE\"");
                    query.append(", fRecepcionRecurso AS \"FECHA DE RECEPCIÓN DEL RECURSO (SEGÚN ESTADO DE CUENTA)\"");
                    query.append(", 0 AS \"(+)IMPORTE IP\"");
                    query.append(", 0 AS \"(+)IMPORTE RENDIMIENTOS\"");
                    query.append(", 0 AS \"(+)IMPORTE RENDIMIENTOS GREENMEX\"");
                    query.append(", 0 AS \"(+)GREENMEX BANCARIO\"");
                    query.append(", CASE WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) AND OAD.caNoContrarrecibo IS NOT NULL THEN OAD.mRetencion ELSE 0 END AS \"(+)SOLICITUDES GREENMEX\"");
                    query.append(", CASE WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) AND OAD.caNoContrarrecibo IS NOT NULL THEN 0 WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) THEN OAD.mRetencion * -1 ELSE OAD.mRetencion * -1 END AS \"(-)SOLICITUDES GREENMEX NO PAGADAS\"");
                    query.append(", (CASE WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) AND OAD.caNoContrarrecibo IS NOT NULL THEN OAD.mRetencion ELSE 0 END) + (CASE WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) AND OAD.caNoContrarrecibo IS NOT NULL THEN 0 WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) THEN OAD.mRetencion * -1 ELSE OAD.mRetencion * -1 END) AS \"TOTALES IP, NO SE CONSIDERA EL RECURSO BANCARIO DE GREENMEX\"");
                    query.append(", nCuenta AS \"CUENTA CONTABLE\"");
                    query.append(", cClaveCRI AS \"CLASIFICADOR POR RUBRO DE INGRESOS\"");
                    query.append(", dCRI AS \"NOMBRE CLASIFICADOR POR RUBRO DE INGRESOS\"");
                    query.append(", '' AS \"CUENTA BANCARIA\"");
                    if (!"".equals(columnas)) {
                        query.append(CG);
                    }
                    query.append(" FROM vPagosGreenMex V");
                    query.append(" LEFT JOIN (SELECT caNoContrarrecibo, nFolioOperAjenas, SUM(mTotal) AS mRetencion FROM tOperAjenasDetalle WITH (NOLOCK) GROUP BY caNoContrarrecibo, nFolioOperAjenas) AS OAD ON V.caNoContrarrecibo = OAD.caNoContrarrecibo");
                    query.append(" LEFT JOIN tOperAjenasEncabezado OAE WITH (NOLOCK) ON OAD.nFolioOperAjenas = OAE.nFolioOperAjenas");
                    query.append(" LEFT JOIN tPagadoEncabezado PAGE WITH (NOLOCK) ON OAE.caNoContrarrecibo = PAGE.caNoContrarrecibo");
                    query.append(" WHERE CAST(V.fAplicacion AS DATE) BETWEEN CAST('" + fechaInicio + "' AS DATE) AND CAST('" + fechaFin + "' AS DATE) AND OAD.caNoContrarrecibo IS NOT NULL");
                    query.append(" AND cClave = '" + cClave + "'");
                    query.append(" GROUP BY V.caNoContrarrecibo, V.nFolioPoliza, V.cTipoPoliza, V.cConcepto, V.fAplicacion, fRecepcionRecurso, OAD.mRetencion");
                    query.append(" ,GreenMex, nCuenta, dCuenta, cClaveCRI, dCRI, cClave, dClave, PAGE.fAplicacion, OAD.caNoContrarrecibo, OAE.caNoContrarrecibo");
                    if (!"".equals(columnas)) {
                        query.append(GB);
                    }
                    query.append(" UNION ");
                    /*PENAS CONVENCIONALES*/
                    query.append("SELECT V.caNoContrarrecibo + '-' + ISNULL(OAE.caNoContrarrecibo,'10OA') AS \"FOLIO INGRESO\"");
                    query.append(", V.nFolioPoliza AS \"NÚMERO DE POLIZA CONTABLE\"");
                    query.append(", V.cConcepto AS \"CONCEPTO QUE ORIGINÓ EL INGRESO\"");
                    query.append(", V.fAplicacion AS \"FECHA DE APLICACIÓN CONTABLE\"");
                    query.append(", fRecepcionRecurso AS \"FECHA DE RECEPCIÓN DEL RECURSO (SEGÚN ESTADO DE CUENTA)\"");
                    query.append(", 0 AS \"(+)IMPORTE IP\"");
                    query.append(", 0 AS \"(+)IMPORTE RENDIMIENTOS\"");
                    query.append(", 0 AS \"(+)IMPORTE RENDIMIENTOS GREENMEX\"");
                    query.append(", 0 AS \"(+)GREENMEX BANCARIO\"");
                    query.append(", CASE WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) AND OAD.caNoContrarrecibo IS NOT NULL THEN OAD.mRetencion ELSE 0 END AS \"(+)SOLICITUDES GREENMEX\"");
                    query.append(", CASE WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) AND OAD.caNoContrarrecibo IS NOT NULL THEN 0 WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) THEN OAD.mRetencion * -1 ELSE OAD.mRetencion * -1 END AS \"(-)SOLICITUDES GREENMEX NO PAGADAS\"");
                    query.append(", (CASE WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) AND OAD.caNoContrarrecibo IS NOT NULL THEN OAD.mRetencion ELSE 0 END) + (CASE WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) AND OAD.caNoContrarrecibo IS NOT NULL THEN 0 WHEN CAST(PAGE.fAplicacion AS DATE) <= CAST('" + fechaFin + "' AS DATE) THEN OAD.mRetencion * -1 ELSE OAD.mRetencion * -1 END) AS \"TOTALES IP, NO SE CONSIDERA EL RECURSO BANCARIO DE GREENMEX\"");
                    query.append(", nCuenta AS \"CUENTA CONTABLE\"");
                    query.append(", cClaveCRI AS \"CLASIFICADOR POR RUBRO DE INGRESOS\"");
                    query.append(", dCRI AS \"NOMBRE CLASIFICADOR POR RUBRO DE INGRESOS\"");
                    query.append(", '' AS \"CUENTA BANCARIA\"");
                    if (!"".equals(columnas)) {
                        query.append(CG);
                    }
                    query.append(" FROM vPagosGreenMex V");
                    query.append(" LEFT JOIN (SELECT caNoContrarrecibo, nFolioPagoPenasConv, SUM(mImporteMasIva) AS mRetencion FROM tPagoPenasConvDetalle WITH (NOLOCK) GROUP BY caNoContrarrecibo, nFolioPagoPenasConv) AS OAD ON V.caNoContrarrecibo = OAD.caNoContrarrecibo");
                    query.append(" LEFT JOIN tPagoPenasConvEncabezado OAE WITH (NOLOCK) ON OAD.nFolioPagoPenasConv = OAE.nFolioPagoPenasConv");
                    query.append(" LEFT JOIN tPagadoEncabezado PAGE WITH (NOLOCK) ON OAE.caNoContrarrecibo = PAGE.caNoContrarrecibo");
                    query.append(" WHERE CAST(V.fAplicacion AS DATE) BETWEEN CAST('" + fechaInicio + "' AS DATE) AND CAST('" + fechaFin + "' AS DATE) AND OAD.caNoContrarrecibo IS NOT NULL");
                    query.append(" AND cClave = '" + cClave + "'");
                    query.append(" GROUP BY V.caNoContrarrecibo, V.nFolioPoliza, V.cTipoPoliza, V.cConcepto, V.fAplicacion, fRecepcionRecurso, OAD.mRetencion");
                    query.append(" ,GreenMex, nCuenta, dCuenta, cClaveCRI, dCRI, cClave, dClave, PAGE.fAplicacion, OAD.caNoContrarrecibo, OAE.caNoContrarrecibo");
                    if (!"".equals(columnas)) {
                        query.append(GB);
                    }
                }
                log.trace("Object: {}", "Select[" + query + "]");
                ps = conn.prepareStatement(query.toString());
            }
            rs = ps.executeQuery();
            long stopQueries = System.currentTimeMillis();
            log.info("Object: {}", String.format("Ejecucion de consultas terminado en [%2d] segundos", (stopQueries - startQueries) / 1000));
            fileName = generaReporteIngresos(rs, plantillas.get("INGRESOS"), fechaInicio, fechaFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    private static String generaReporteIngresos(ResultSet rs, String plantillaPath, String fechaInicio, String fechaFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteIngresos" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int renglonInicio = 0;
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        sheet0 = Util.resultSetToExcelE(rs, sheet0, renglonInicio, 0, true, estiloTabla);
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }
}
