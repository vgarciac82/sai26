package com.syc.sai.ingresos.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import javax.servlet.ServletException;
import com.syc.contable.AccountingEngine;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.contable.caja.core.CajaManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager para la consolidacion (Integracion) de una relacion de gastos.
 *
 * @author Vicente Garcia Carrillo
 */
public class ConsolidacionRGManager {

    public static Logger log = LoggerFactory.getLogger(ConsolidacionRGManager.class);

    /**
     * Indica si una integracion de gastos es de pago a proveedor o pago de
     * subsidios. Analisa el encabezado de las relaciones de gastos que forman
     * parte de la integracion. Si el destino del gasto termina en RP o SU
     * entonces la relacion de gastos incluye al menos un pago a proveedor o un
     * pago de subsidios, por lo que solo debera aplicarse la integracion y no
     * cada una de sus integrantes.
     *
     * @param conn
     *            Conexion activa a la base de datos.
     * @param nFolioRG
     *            Folio de integracion (campo sauxiliarcomodin) P.E.
     *            G33140801095345
     * @return true si la integracion incluye al menos una relacion de gastos a
     *         proveedor o de subsidios.
     * @throws Exception
     *             Si ocurre algun error de base de datos.
     */
    public static boolean esIntegracionProveedor(Connection conn, String nFolioRG) throws Exception {
        String query = "SELECT Count(*) AS total " + "FROM   dbo.tlayoutscreadosrelaciongastosheader layouts WITH (nolock) " + " INNER JOIN dbo.trelaciongastosencabezado encabezado WITH (nolock) " + " ON layouts.snocontrarrecibo = encabezado.canocontrarrecibo " + " WHERE  id_destino_gasto IN ('CPRP','NORN','CLRE','GLRE') " + " AND layouts.sauxiliarcomodin = ? ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, nFolioRG);
            rs = ps.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("total");
                return total > 0;
            } else
                return false;
        } finally {
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rs, false);
        }
    }

    /**
     * Aplica la integracion de una relacion de gastos. No hace commit al
     * terminar
     *
     * @param conn
     *            Conexion activa a la base de datos.
     * @param nIDIntegracion
     *            Folio de la integracion P.E. G33140801095345
     * @throws Exception
     *             Si ocurre algun error al aplicar la relacion de gastos.
     */
    public static boolean aplicaConsolidacionRelacionGastos(Connection conn, String nIDIntegracion, String reportPath) throws Exception {
        String query = "SELECT nFolioConsolidacion, cdocumentohaplicado FROM dbo.tconsolidacionrelaciongastosEncabezado (NOLOCK) WHERE nIdIntegracion = ?";
        PreparedStatement ps = null, ps2 = null;
        ;
        ResultSet rs = null, rs2 = null;
        //URVP.15082014. HACER UPDATE DE FECHA DE APLICACION AL INTEGRAR DE ACUERDO A LA FECHA DE SIAFF
        PreparedStatement upd_fAplicacion = null;
        String queryupdate = "UPDATE tconsolidacionrelaciongastosEncabezado SET fAplicacion = CONVERT( DATE, ISNULL((SELECT TOP 1 FECHA_PAGO FROM dbo.CLC_SIAFF_ENC SIAFF WITH(NOLOCK) INNER JOIN dbo.CLC_SICOP SICOP WITH(NOLOCK) ON SIAFF.FOLIO_CLC=FOLIO_SIAFF_112 WHERE NCTR_47 = ?), GETDATE()), 103)  WHERE nFolioConsolidacion = ?";
        String queryfecha = "SELECT YEAR(fAplicacion) AS anio FROM tconsolidacionrelaciongastosEncabezado (NOLOCK) WHERE nFolioConsolidacion = ?";
        log.debug(queryupdate);
        log.debug(queryfecha);
        boolean aplicado = false;
        String documentoAplicado = null;
        // today
        Calendar c1 = Calendar.getInstance();
        AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        String aEjercicioFiscal = "";
        try {
            aEjercicioFiscal = adecProy.obtenEjercicioFiscal();
        } catch (Exception e) {
            throw new ServletException(e);
        }
        try {
            boolean esSAIAlterno = "true".equals(ConfiguraAplicativoManager.getSystemSetting(conn, "SAI_AMBIENTAL"));
            if (esSAIAlterno)
                acutalizaFonden(conn, nIDIntegracion);
            ps = conn.prepareStatement(query);
            ps.setString(1, nIDIntegracion);
            int nFolioConsolidacion = -1;
            rs = ps.executeQuery();
            if (rs.next()) {
                nFolioConsolidacion = rs.getInt(1);
                documentoAplicado = rs.getString(2);
            } else
                throw new Exception("No se encontro folio para la integracion de relaciones de gastos[" + nIDIntegracion + "]");
            if ("S".equalsIgnoreCase(documentoAplicado) || "C".equalsIgnoreCase(documentoAplicado)) {
                log.info("Se intento reaplicar la integracion " + nIDIntegracion + " con folio de integracion " + nFolioConsolidacion);
                return true;
            }
            //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
            upd_fAplicacion = conn.prepareStatement(queryupdate);
            upd_fAplicacion.setString(1, nIDIntegracion);
            upd_fAplicacion.setInt(2, nFolioConsolidacion);
            upd_fAplicacion.executeUpdate();
            //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
            ps2 = conn.prepareStatement(queryfecha);
            ps2.setInt(1, nFolioConsolidacion);
            String anio = null;
            rs2 = ps2.executeQuery();
            if (rs2.next()) {
                anio = rs2.getString(1);
            } else
                throw new Exception("No se encontro folio para la integracion de relaciones de gastos[" + nIDIntegracion + "]");
            if (Integer.parseInt(aEjercicioFiscal) != Integer.parseInt(anio)) {
                updateEventoFA(conn, nFolioConsolidacion);
            }
            AccountingEngine motorContable = new AccountingEngine();
            motorContable.makeAccountingApplication(conn, "CONSOLIDACIONRG", String.valueOf(nFolioConsolidacion), "tconsolidacionrelaciongastosencabezado", "tconsolidacionrelaciongastosdetalle", "nFolioConsolidacion");
            motorContable = null;
            if (esIntegracionISNQ(conn, nIDIntegracion)) {
                //AQUI SE INSERTARA Y APLICARA LA SOLICITUD DE CAJA QUE HARA LA TRANFERENCIA DE BANCOS CENTRAL(BANORTE) A NOMINA(BBVA)
                int nFolioCaja = CajaManager.insertaSolicitudNoPresupuestalISN(conn, nIDIntegracion, reportPath);
            }
            aplicado = true;
            return aplicado;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(upd_fAplicacion, false);
        }
    }

    private static void acutalizaFonden(Connection conn, String nIDIntegracion) throws Exception {
        // Primer paso si las RG que estan integradas son de FONDEN se toma el primer
        String query = "SELECT Count(*) AS pagoFonden " + "FROM   tlayoutscreadospagosdiversosrgheader layouts WITH(nolock) " + "       INNER JOIN tpagodiversoencabezado pdEncabezado WITH(nolock) " + "               ON layouts.snocontrarrecibo = pdEncabezado.canocontrarrecibo " + "       INNER JOIN tpagodiversodetalle pdDetalle WITH(nolock) " + "               ON pdEncabezado.nfoliopagodiverso = pdDetalle.nfoliopagodiverso " + "WHERE  sauxiliarcomodin = ? " + "       AND pdDetalle.id_tipo_concepto IN ( 'PC', 'PB' ) ";
        String queryBuscaFondo = "SELECT TOP 1 pdEncabezado.cproyectofonden " + "FROM   dbo.tlayoutscreadospagosdiversosrgheader layouts WITH(nolock) " + "       INNER JOIN dbo.tpagodiversoencabezado pdEncabezado WITH(nolock) " + "               ON layouts.snocontrarrecibo = pdEncabezado.canocontrarrecibo " + "       INNER JOIN dbo.tpagodiversodetalle pdDetalle WITH(nolock) " + "               ON pdEncabezado.nfoliopagodiverso = pdDetalle.nfoliopagodiverso " + "WHERE  sauxiliarcomodin = ? " + "       AND pdDetalle.id_tipo_concepto IN ( 'PC', 'PB' ) " + "       AND pdEncabezado.cproyectofonden IS NOT NULL ";
        String queryUpdate = "UPDATE tconsolidacionrelaciongastosEncabezado SET cProyectoFonden = ? WHERE nIdIntegracion = ?";
        PreparedStatement psUpdate = null;
        PreparedStatement psSelect = null;
        PreparedStatement psBuscaIDFondo = null;
        ResultSet rs = null;
        ResultSet rsFondo = null;
        try {
            psSelect = conn.prepareStatement(query);
            psSelect.setString(1, nIDIntegracion);
            rs = psSelect.executeQuery();
            if (rs.next()) {
                boolean esPagoFonden = rs.getInt(1) > 0;
                if (esPagoFonden) {
                    psBuscaIDFondo = conn.prepareStatement(queryBuscaFondo);
                    psBuscaIDFondo.setString(1, nIDIntegracion);
                    rsFondo = psBuscaIDFondo.executeQuery();
                    if (rsFondo.next()) {
                        String proyecto = rsFondo.getString(1);
                        psUpdate = conn.prepareStatement(queryUpdate);
                        psUpdate.setString(1, proyecto);
                        psUpdate.setString(2, nIDIntegracion);
                        int afectados = psUpdate.executeUpdate();
                        log.debug("Se actulizaron " + afectados + " registros de la integracion " + nIDIntegracion + " con el proyecto: " + proyecto);
                    } else
                        throw new Exception("El pago se marco como FONDEN sin embargo no se encontro la cuenta en ninguno de los integrados");
                }
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rsFondo);
            CloseObject.closeObject(psUpdate);
            CloseObject.closeObject(psSelect);
            CloseObject.closeObject(psBuscaIDFondo);
        }
    }

    public static boolean esRelacionGastosEmpleadoManual(Connection conn, Integer nFolio) throws Exception {
        String query = "SELECT COUNT(*) AS total FROM tRELACIONGASTOSEncabezado WITH (nolock) WHERE ID_DESTINO_GASTO = 'CERE' AND nFolioRELACIONGASTOS = ? ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, nFolio);
            rs = ps.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("total");
                return total > 0;
            } else
                return false;
        } finally {
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rs, false);
        }
    }

    public static boolean existeEjercido(Connection conn, Integer nFolio) throws Exception {
        String query = "SELECT COUNT(*) AS total FROM dbo.tEjercidoEncabezado WHERE cTipoPago='RELACIONGASTOS' AND nFolioPAGO = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, nFolio);
            rs = ps.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("total");
                return total > 0;
            } else
                return false;
        } finally {
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rs, false);
        }
    }

    /**
     * Indica si una integracion de gastos es de pago a proveedor o pago de
     * subsidios. Analisa el encabezado de las relaciones de gastos que forman
     * parte de la integracion. Si el destino del gasto termina en RP o SU
     * entonces la relacion de gastos incluye al menos un pago a proveedor o un
     * pago de subsidios, por lo que solo debera aplicarse la integracion y no
     * cada una de sus integrantes.
     *
     * @param conn
     *            Conexion activa a la base de datos.
     * @param nFolioRG
     *            Folio de integracion (campo sauxiliarcomodin) P.E.
     *            G33140801095345
     * @return true si la integracion incluye al menos una relacion de gastos a
     *         proveedor o de subsidios.
     * @throws Exception
     *             Si ocurre algun error de base de datos.
     */
    public static boolean esIntegracionLaudosDevengados(Connection conn, String integracion) throws Exception {
        String query = "SELECT Count(*) AS total " + "FROM   dbo.tlayoutscreadosrelaciongastosheader layouts WITH (nolock) " + " INNER JOIN dbo.trelaciongastosencabezado encabezado WITH (nolock) " + " ON layouts.snocontrarrecibo = encabezado.canocontrarrecibo " + " WHERE  id_destino_gasto IN ('CLRE') " + " AND layouts.sauxiliarcomodin = ? ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, integracion);
            rs = ps.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("total");
                return total > 0;
            } else
                return false;
        } finally {
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rs, false);
        }
    }

    private static void updateEventoFA(Connection conn, int nFolioConsolidacion) throws Exception {
        String queryUpdate = " UPDATE	tconsolidacionrelaciongastosdetalle " + " SET		cevento = REPLACE(cevento, 'INT_', 'INT_FA_'), RFC = 'TESOFE' " + " WHERE	nFolioConsolidacion = ? ";
        String queryUpdateP = " UPDATE tconsolidacionrelaciongastosEncabezado SET cTipoPoliza = 'DI' WHERE nFolioConsolidacion = ?";
        PreparedStatement psUpdate = null, psUpdateP = null;
        try {
            psUpdate = conn.prepareStatement(queryUpdate);
            psUpdateP = conn.prepareStatement(queryUpdateP);
            psUpdate.setInt(1, nFolioConsolidacion);
            psUpdateP.setInt(1, nFolioConsolidacion);
            int afectados = psUpdate.executeUpdate();
            afectados = afectados + psUpdateP.executeUpdate();
            log.debug("Se actualizaron : " + afectados + " registros del detalle del Folio de Consolidación: " + nFolioConsolidacion);
        } finally {
            CloseObject.closeObject(psUpdate);
        }
    }

    public static boolean esIntegracionISNQ(Connection conn, String integracion) throws Exception {
        String query = "SELECT Count(*) AS total " + "FROM   dbo.tlayoutscreadosrelaciongastosheader layouts WITH (nolock) " + " INNER JOIN dbo.trelaciongastosencabezado encabezado WITH (nolock) " + " ON layouts.snocontrarrecibo = encabezado.canocontrarrecibo " + " WHERE  id_destino_gasto IN ('2NRQ') " + " AND layouts.sauxiliarcomodin = ? ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, integracion);
            rs = ps.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("total");
                return total > 0;
            } else
                return false;
        } finally {
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rs, false);
        }
    }
}
