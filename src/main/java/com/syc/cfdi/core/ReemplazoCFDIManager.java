package com.syc.cfdi.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.syc.gestion.core.Caso;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ReemplazoCFDIManager {

    private static final Logger log = LoggerFactory.getLogger(ReemplazoCFDIManager.class);

    public static int insertaFacturaReemplazada(Connection conn, String login, String tipoPago, int folioPago) throws Exception {
        StringBuilder query = new StringBuilder();
        PreparedStatement ps = null;
        query.append("INSERT INTO tpagofactura_reemplazada ");
        query.append("            ( ");
        query.append("                        ctipopago, ");
        query.append("                        nfoliopago, ");
        query.append("                        cfactura, ");
        query.append("                        mimportebruto, ");
        query.append("                        mimporteconiva, ");
        query.append("                        mimporteiva, ");
        query.append("                        crfcfactura, ");
        query.append("                        motrosimpuestos, ");
        query.append("                        cesnotacredito, ");
        query.append("                        mimportedescuento, ");
        query.append("                        cmetodopago, ");
        query.append("                        crazonsocial, ");
        query.append("                        cregimenfiscal, ");
        query.append("                        dfechafactura, ");
        query.append("                        dfechatimbrado, ");
        query.append("                        uloginreemplazo ");
        query.append("            ) ");
        query.append("SELECT ctipopago, ");
        query.append("       nfoliopago, ");
        query.append("       cfactura, ");
        query.append("       mimportebruto, ");
        query.append("       mimporteconiva, ");
        query.append("       mimporteiva, ");
        query.append("       crfcfactura, ");
        query.append("       motrosimpuestos, ");
        query.append("       cesnotacredito, ");
        query.append("       mimportedescuento, ");
        query.append("       cmetodopago, ");
        query.append("       crazonsocial, ");
        query.append("       cregimenfiscal, ");
        query.append("       dfechafactura, ");
        query.append("       dfechatimbrado, ");
        query.append("       ? AS uloginreemplazo ");
        query.append("FROM   tpagofactura WITH(nolock) ");
        query.append("WHERE  ctipopago = ? ");
        query.append("AND    nfoliopago = ? ");
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, login);
            ps.setString(2, tipoPago);
            ps.setInt(3, folioPago);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int insertaFacturaImpuestosReemplazada(Connection conn, String login, String tipoPago, int folioPago) throws Exception {
        StringBuilder query = new StringBuilder();
        PreparedStatement ps = null;
        query.append("INSERT INTO tpagofacturaimpuestos_reemplazada ");
        query.append("            ( ");
        query.append("                        ctipopago , ");
        query.append("                        nfoliopago , ");
        query.append("                        uuid , ");
        query.append("                        cnombreimpuesto , ");
        query.append("                        mimporteimpuesto , ");
        query.append("                        ntazaimpuesto , ");
        query.append("                        ctipofactor , ");
        query.append("                        mimportebase , ");
        query.append("                        uloginreemplazo ");
        query.append("            ) ");
        query.append("SELECT ctipopago , ");
        query.append("       nfoliopago , ");
        query.append("       uuid , ");
        query.append("       cnombreimpuesto , ");
        query.append("       mimporteimpuesto , ");
        query.append("       ntazaimpuesto , ");
        query.append("       ctipofactor , ");
        query.append("       mimportebase, ");
        query.append("       ? as uloginreemplazo ");
        query.append("FROM   tpagofacturaimpuestos WITH(nolock) ");
        query.append("WHERE  ctipopago = ? ");
        query.append("AND    nfoliopago = ? ");
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, login);
            ps.setString(2, tipoPago);
            ps.setInt(3, folioPago);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int insertaFacturaRetencionReemplazada(Connection conn, String login, String tipoPago, int folioPago) throws Exception {
        StringBuilder query = new StringBuilder();
        PreparedStatement ps = null;
        query.append("INSERT INTO tpagofacturaretencion_reemplazada ");
        query.append("            ( ");
        query.append("                        ctipopago , ");
        query.append("                        nfoliopago , ");
        query.append("                        uuid , ");
        query.append("                        cnombreretencion , ");
        query.append("                        mimporteretencion , ");
        query.append("                        uloginreemplazo ");
        query.append("            ) ");
        query.append("SELECT ctipopago , ");
        query.append("       nfoliopago , ");
        query.append("       uuid , ");
        query.append("       cnombreretencion , ");
        query.append("       mimporteretencion , ");
        query.append("       ? AS uloginreemplazo ");
        query.append("FROM   tpagofacturaretencion WITH(nolock) ");
        query.append("WHERE  ctipopago = ? ");
        query.append("AND    nfoliopago = ? ");
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, login);
            ps.setString(2, tipoPago);
            ps.setInt(3, folioPago);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int insertaFacturaConceptosReemplazada(Connection conn, String login, String tipoPago, int folioPago) throws Exception {
        StringBuilder query = new StringBuilder();
        PreparedStatement ps = null;
        query.append("INSERT INTO tcfdiconceptos_reemplazada ");
        query.append("            ( ");
        query.append("                        ctipopago , ");
        query.append("                        nfoliopago , ");
        query.append("                        nidconcepto , ");
        query.append("                        cuuid , ");
        query.append("                        cclaveprodserv , ");
        query.append("                        ncantidad , ");
        query.append("                        cclaveunidad , ");
        query.append("                        cdescripcion , ");
        query.append("                        mvalorunitario , ");
        query.append("                        mimporte , ");
        query.append("                        uloginreemplazo ");
        query.append("            ) ");
        query.append("SELECT ? AS ctipopago , ");
        query.append("       ? AS nfoliopago , ");
        query.append("       nidconcepto , ");
        query.append("       cuuid , ");
        query.append("       cclaveprodserv , ");
        query.append("       ncantidad , ");
        query.append("       cclaveunidad , ");
        query.append("       cdescripcion , ");
        query.append("       mvalorunitario , ");
        query.append("       mimporte , ");
        query.append("       ? AS uloginreemplazo ");
        query.append("FROM   tcfdiconceptos WITH(nolock) ");
        query.append("WHERE  cuuid IN ");
        query.append("       ( ");
        query.append("              SELECT cfactura ");
        query.append("              FROM   tpagofactura WITH(nolock) ");
        query.append("              WHERE  ctipopago = ? ");
        query.append("              AND    nfoliopago = ?  ");
        query.append("		) ");
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, tipoPago);
            ps.setInt(2, folioPago);
            ps.setString(3, login);
            ps.setString(4, tipoPago);
            ps.setInt(5, folioPago);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int insertaFacturaConceptoImpuestosReemplazada(Connection conn, String login, String tipoPago, int folioPago) throws Exception {
        StringBuilder query = new StringBuilder();
        PreparedStatement ps = null;
        query.append("INSERT INTO tcfdiconceptoimpuestos_reemplazada ");
        query.append("            ( ");
        query.append("                        ctipopago , ");
        query.append("                        nfoliopago , ");
        query.append("                        nidconcepto , ");
        query.append("                        cimpuesto , ");
        query.append("                        ctipofactor , ");
        query.append("                        ntasaocuota , ");
        query.append("                        mbase , ");
        query.append("                        mimporte , ");
        query.append("                        uloginreemplazo ");
        query.append("            ) ");
        query.append("SELECT ? AS ctipopago , ");
        query.append("       ? AS nfoliopago , ");
        query.append("       nidconcepto , ");
        query.append("       cimpuesto , ");
        query.append("       ctipofactor , ");
        query.append("       ntasaocuota , ");
        query.append("       mbase , ");
        query.append("       mimporte , ");
        query.append("       ? AS uloginreemplazo ");
        query.append("FROM   tcfdiconceptoimpuestos WITH(nolock) ");
        query.append("WHERE  nidconcepto IN ");
        query.append("       ( ");
        query.append("              SELECT nidconcepto ");
        query.append("              FROM   tcfdiconceptos WITH(nolock) ");
        query.append("              WHERE  cuuid IN ");
        query.append("                     ( ");
        query.append("                            SELECT cfactura ");
        query.append("                            FROM   tpagofactura WITH(nolock) ");
        query.append("                            WHERE  ctipopago = ? ");
        query.append("                            AND    nfoliopago = ? ) ");
        query.append("		) ");
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, tipoPago);
            ps.setInt(2, folioPago);
            ps.setString(3, login);
            ps.setString(4, tipoPago);
            ps.setInt(5, folioPago);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int insertaFacturaBonificacionReemplazada(Connection conn, String login, String tipoPago, int folioPago) throws Exception {
        StringBuilder query = new StringBuilder();
        PreparedStatement ps = null;
        query.append("INSERT INTO tpagofacturabonificacion_reemplazada ");
        query.append("            ( ");
        query.append("                        ctipopago , ");
        query.append("                        nfoliopago , ");
        query.append("                        uuid , ");
        query.append("                        mimporte , ");
        query.append("                        mimporteimpuestos , ");
        query.append("                        mimportetotal , ");
        query.append("                        uloginreemplazo ");
        query.append("            ) ");
        query.append("SELECT ctipopago , ");
        query.append("       nfoliopago , ");
        query.append("       uuid , ");
        query.append("       mimporte , ");
        query.append("       mimporteimpuestos , ");
        query.append("       mimportetotal , ");
        query.append("       ? AS uloginreemplazo ");
        query.append("FROM   tpagofacturabonificacion WITH(nolock) ");
        query.append("WHERE  ctipopago = ? ");
        query.append("AND    nfoliopago = ? ");
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, login);
            ps.setString(2, tipoPago);
            ps.setInt(3, folioPago);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int insertaFacturaECCReemplazada(Connection conn, String login, String tipoPago, int folioPago) throws Exception {
        StringBuilder query = new StringBuilder();
        PreparedStatement ps = null;
        query.append("INSERT INTO tpagofacturaecc_reemplazada ");
        query.append("            ( ");
        query.append("                        ctipopago , ");
        query.append("                        nfoliopago , ");
        query.append("                        uuid , ");
        query.append("                        mimportesubtotal , ");
        query.append("                        mimporteimpuestos , ");
        query.append("                        mimportetotal , ");
        query.append("                        uloginreemplazo ");
        query.append("            ) ");
        query.append("SELECT ctipopago , ");
        query.append("       nfoliopago , ");
        query.append("       uuid , ");
        query.append("       mimportesubtotal , ");
        query.append("       mimporteimpuestos , ");
        query.append("       mimportetotal , ");
        query.append("       ? AS uloginreemplazo ");
        query.append("FROM   tpagofacturaecc WITH(nolock) ");
        query.append("WHERE  ctipopago = ? ");
        query.append("AND    nfoliopago = ? ");
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, login);
            ps.setString(2, tipoPago);
            ps.setInt(3, folioPago);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int insertaFacturaValesCombustibleReemplazada(Connection conn, String login, String tipoPago, int folioPago) throws Exception {
        StringBuilder query = new StringBuilder();
        PreparedStatement ps = null;
        query.append("INSERT INTO tpagofacturavalescombustible_reemplazada ");
        query.append("            ( ");
        query.append("                        ctipopago , ");
        query.append("                        nfoliopago , ");
        query.append("                        uuid , ");
        query.append("                        mimportesubtotal , ");
        query.append("                        mimporteimpuestos , ");
        query.append("                        mimportetotal , ");
        query.append("                        totalemision , ");
        query.append("                        ivaemision , ");
        query.append("                        subtotalemision , ");
        query.append("                        totalfiscal , ");
        query.append("                        ivafiscal , ");
        query.append("                        subtotalfiscal , ");
        query.append("                        uloginreemplazo ");
        query.append("            ) ");
        query.append("SELECT ctipopago , ");
        query.append("       nfoliopago , ");
        query.append("       uuid , ");
        query.append("       mimportesubtotal , ");
        query.append("       mimporteimpuestos , ");
        query.append("       mimportetotal , ");
        query.append("       totalemision , ");
        query.append("       ivaemision , ");
        query.append("       subtotalemision , ");
        query.append("       totalfiscal , ");
        query.append("       ivafiscal , ");
        query.append("       subtotalfiscal , ");
        query.append("       ? AS uloginreemplazo ");
        query.append("FROM   tpagofacturavalescombustible WITH(nolock) ");
        query.append("WHERE  ctipopago = ? ");
        query.append("AND    nfoliopago = ?");
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, login);
            ps.setString(2, tipoPago);
            ps.setInt(3, folioPago);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int insertaBitacora(Connection conn, String login, String tipoPago, int folioPago) throws Exception {
        StringBuilder query = new StringBuilder();
        PreparedStatement ps = null;
        query.append("INSERT INTO tpagofacturareemplazada ");
        query.append("(cTipoPago, ");
        query.append("nFolioPago, ");
        query.append("usuarioReemplazo) ");
        query.append("VALUES     ( ?, ");
        query.append("?, ");
        query.append("?) ");
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, tipoPago);
            ps.setInt(2, folioPago);
            ps.setString(3, login);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    private static int nuevoIDCarpeta(Connection conn, Caso c) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT (MAX(id_carpeta) + 1) AS id_carpeta_nueva  ");
        query.append("FROM   IMX_CARPETA WITH(NOLOCK)  ");
        query.append("WHERE  TITULO_APLICACION = ? ");
        query.append("AND ID_GABINETE = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, c.getTipoCaso().getGavetaAsociada());
            ps.setInt(2, c.getIdGabinete());
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
            else
                throw new RuntimeException("No se encontro expediente para el caso folio:  " + c.getFolio());
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    private static int buscaIDCarpetaCFDI(Connection conn, Caso c) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	id_carpeta AS id_carpeta_cfdi ");
        query.append("  FROM   IMX_CARPETA WITH(NOLOCK)  ");
        query.append(" WHERE  TITULO_APLICACION = ? ");
        query.append("   AND ID_GABINETE = ? ");
        query.append("   AND nombre_carpeta = 'CFDI' ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, c.getTipoCaso().getGavetaAsociada());
            ps.setInt(2, c.getIdGabinete());
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
            else
                throw new RuntimeException("No se encontro expediente para el caso folio:  " + c.getFolio());
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static int creaEstructuraCarpeta(Connection conn, Caso c, String login) throws Exception {
        int idCarpeta = creaCarpeta(conn, c, login);
        creaOrgCarpeta(conn, c, idCarpeta);
        return idCarpeta;
    }

    private static void creaOrgCarpeta(Connection conn, Caso c, int idCarpeta) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO IMX_ORG_CARPETA ");
        query.append("           (TITULO_APLICACION ");
        query.append("           ,ID_GABINETE ");
        query.append("           ,ID_CARPETA_HIJA ");
        query.append("           ,ID_CARPETA_PADRE ");
        query.append("           ,NOMBRE_HIJA) ");
        query.append("     VALUES ");
        query.append("           (? ");
        query.append("           ,? ");
        query.append("           ,? ");
        query.append("           ,0 ");
        query.append("           ,'CFDI Reemplazado') ");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, c.getTipoCaso().getGavetaAsociada());
            ps.setInt(2, c.getIdGabinete());
            ps.setInt(3, idCarpeta);
            ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    private static int creaCarpeta(Connection conn, Caso c, String login) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO IMX_CARPETA ");
        query.append("           (TITULO_APLICACION ");
        query.append("           ,ID_GABINETE ");
        query.append("           ,ID_CARPETA ");
        query.append("           ,NOMBRE_CARPETA ");
        query.append("           ,NOMBRE_USUARIO ");
        query.append("           ,BANDERA_RAIZ ");
        query.append("           ,FH_CREACION ");
        query.append("           ,FH_MODIFICACION ");
        query.append("           ,NUMERO_ACCESOS ");
        query.append("           ,NUMERO_CARPETAS ");
        query.append("           ,NUMERO_DOCUMENTOS ");
        query.append("           ,DESCRIPCION ");
        query.append("           ,PASSWORD) ");
        query.append("     VALUES ");
        query.append("           (? ");
        query.append("           ,? ");
        query.append("           ,? ");
        query.append("           ,'CFDI Reemplazado' ");
        query.append("           ,? ");
        query.append("           ,'N' ");
        query.append("           ,GETDATE() ");
        query.append("           ,GETDATE() ");
        query.append("           ,0 ");
        query.append("           ,0 ");
        query.append("           ,0 ");
        query.append("           ,'Carpeta que contiene las facturas reemplazadas' ");
        query.append("           ,-1) ");
        PreparedStatement ps = null;
        try {
            int idCarpeta = nuevoIDCarpeta(conn, c);
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, c.getTipoCaso().getGavetaAsociada());
            ps.setInt(2, c.getIdGabinete());
            ps.setInt(3, idCarpeta);
            ps.setString(4, login);
            ps.executeUpdate();
            return idCarpeta;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int eliminaInformacionFacturas(Connection conn, String tipoPago, int folioPago) throws SQLException {
        StringBuilder deleteConceptoImpuestos = new StringBuilder("DELETE FROM tCFDIConceptoImpuestos where nIDConcepto IN (select nIDConcepto from tCFDIConceptos where cUUID IN (SELECT cfactura from tPagoFactura where cTipoPago = ? and nFolioPago = ? ) )");
        StringBuilder deleteConceptos = new StringBuilder("DELETE from tCFDIConceptos where cUUID IN (SELECT cfactura from tPagoFactura where cTipoPago = ? and nFolioPago = ? )");
        StringBuilder deleteFacturaImpuestos = new StringBuilder("DELETE from tPagoFacturaImpuestos where cTipoPago = ? and nFolioPago = ? ");
        StringBuilder deleteFacturaRetenciones = new StringBuilder("DELETE from tPagoFacturaRetencion where cTipoPago = ? and nFolioPago = ? ");
        StringBuilder deleteFactura = new StringBuilder("DELETE from tPagoFactura where cTipoPago = ? and nFolioPago = ? ");
        StringBuilder deleteFacturaVales = new StringBuilder("DELETE from tpagofacturavalescombustible where cTipoPago = ? and nFolioPago = ? ");
        StringBuilder deleteFacturaEcc = new StringBuilder("DELETE from tpagofacturaecc where cTipoPago = ? and nFolioPago = ? ");
        StringBuilder deleteFacturaBonificacion = new StringBuilder("DELETE from tpagofacturabonificacion where cTipoPago = ? and nFolioPago = ? ");
        PreparedStatement psDeleteConceptoImpuestos = null;
        PreparedStatement psDeleteConceptos = null;
        PreparedStatement psDeleteFacturaImpuestos = null;
        PreparedStatement pseleteFacturaRetenciones = null;
        PreparedStatement pseleteFactura = null;
        PreparedStatement pseleteFacturaVales = null;
        PreparedStatement pseleteFacturaEcc = null;
        PreparedStatement psDeleteFacturaBonificacion = null;
        try {
            psDeleteConceptoImpuestos = conn.prepareStatement(deleteConceptoImpuestos.toString());
            psDeleteConceptos = conn.prepareStatement(deleteConceptos.toString());
            psDeleteFacturaImpuestos = conn.prepareStatement(deleteFacturaImpuestos.toString());
            pseleteFacturaRetenciones = conn.prepareStatement(deleteFacturaRetenciones.toString());
            pseleteFactura = conn.prepareStatement(deleteFactura.toString());
            pseleteFacturaVales = conn.prepareStatement(deleteFacturaVales.toString());
            pseleteFacturaEcc = conn.prepareStatement(deleteFacturaEcc.toString());
            psDeleteFacturaBonificacion = conn.prepareStatement(deleteFacturaBonificacion.toString());
            int eliminados = 0;
            psDeleteConceptoImpuestos.setString(1, tipoPago);
            psDeleteConceptoImpuestos.setInt(2, folioPago);
            eliminados += psDeleteConceptoImpuestos.executeUpdate();
            psDeleteConceptos.setString(1, tipoPago);
            psDeleteConceptos.setInt(2, folioPago);
            eliminados += psDeleteConceptos.executeUpdate();
            psDeleteFacturaImpuestos.setString(1, tipoPago);
            psDeleteFacturaImpuestos.setInt(2, folioPago);
            eliminados += psDeleteFacturaImpuestos.executeUpdate();
            pseleteFacturaRetenciones.setString(1, tipoPago);
            pseleteFacturaRetenciones.setInt(2, folioPago);
            eliminados += pseleteFacturaRetenciones.executeUpdate();
            pseleteFactura.setString(1, tipoPago);
            pseleteFactura.setInt(2, folioPago);
            eliminados += pseleteFactura.executeUpdate();
            pseleteFacturaVales.setString(1, tipoPago);
            pseleteFacturaVales.setInt(2, folioPago);
            eliminados += pseleteFacturaVales.executeUpdate();
            pseleteFacturaEcc.setString(1, tipoPago);
            pseleteFacturaEcc.setInt(2, folioPago);
            eliminados += pseleteFacturaEcc.executeUpdate();
            psDeleteFacturaBonificacion.setString(1, tipoPago);
            psDeleteFacturaBonificacion.setInt(2, folioPago);
            eliminados += psDeleteFacturaBonificacion.executeUpdate();
            log.info("Object: {}", "Se eliminaron " + eliminados + " facturas del pago: " + tipoPago + " Folio: " + folioPago);
            return eliminados;
        } finally {
            CloseObject.closeObject(psDeleteConceptoImpuestos);
            CloseObject.closeObject(psDeleteConceptos);
            CloseObject.closeObject(psDeleteFacturaImpuestos);
            CloseObject.closeObject(pseleteFacturaRetenciones);
            CloseObject.closeObject(pseleteFactura);
            CloseObject.closeObject(pseleteFacturaVales);
            CloseObject.closeObject(pseleteFacturaEcc);
            CloseObject.closeObject(psDeleteFacturaBonificacion);
        }
    }

    public static void mueveArchivosFacturas(Connection conn, Caso c, int nuevaCarpeta) throws Exception {
        int idCarpetaCFDI = buscaIDCarpetaCFDI(conn, c);
        ejecutarAlterTable(conn, false);
        ejecutarUpdate(conn, c, "imx_pagina", "ID_CARPETA_PADRE", "TITULO_APLICACION = ? AND ID_GABINETE = ? AND ID_CARPETA_PADRE = ?", nuevaCarpeta, idCarpetaCFDI);
        ejecutarUpdate(conn, c, "imx_documento", "ID_CARPETA_PADRE", "TITULO_APLICACION = ? AND ID_GABINETE = ? AND ID_CARPETA_PADRE = ?", nuevaCarpeta, idCarpetaCFDI);
        ejecutarAlterTable(conn, true);
    }

    private static void ejecutarAlterTable(Connection connection, boolean habilitar) throws SQLException {
        String sql = "ALTER TABLE imx_pagina " + (habilitar ? "CHECK" : "NOCHECK") + " CONSTRAINT FK_PAG_DOCUMENTO";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }

    private static void ejecutarUpdate(Connection connection, Caso c, String tabla, String columna, String condicion, int nuevoValor, int idCarpetaAnterior) throws SQLException {
        String sql = "UPDATE " + tabla + " SET " + columna + " = ? WHERE " + condicion;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, nuevoValor);
            statement.setString(2, c.getTipoCaso().getGavetaAsociada());
            statement.setInt(3, c.getIdGabinete());
            statement.setInt(4, idCarpetaAnterior);
            statement.executeUpdate();
        }
    }

    public static int getFoliopago(Connection conn, String tipoPago, String contrarecibo) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT nfolio ");
        query.append("FROM   vtramitesexportar ");
        query.append("WHERE  cdocumento = ? ");
        query.append("       AND canocontrarrecibo = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, tipoPago);
            ps.setString(2, contrarecibo);
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
            else
                throw new RuntimeException("No se encontro folio de pago tipo: " + tipoPago + " con CxP: " + contrarecibo);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static String getRFCPago(Connection conn, String tipoPago, String contrarecibo) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT rfc ");
        query.append("FROM   vtramitesexportar ");
        query.append("WHERE  cdocumento = ? ");
        query.append("       AND canocontrarrecibo = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, tipoPago);
            ps.setString(2, contrarecibo);
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getString(1);
            else
                throw new RuntimeException("No se encontro RFC del pago tipo: " + tipoPago + " con CxP: " + contrarecibo);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static List<String> validaDiferencias(Connection conn, String tipoPago, int folioPago) throws SQLException {
        String[] tables = { "v_totales_pago_factura_vales_combustible", "v_totales_pago_factura_bonificacion", "v_totales_pago_factura_concepto_impuestos", "v_totales_pago_factura_ecc", "v_totales_pago_factura_retenciones", "v_totales_pago_factura_impuestos", "v_totales_pago_factura_reemplazo" };
        List<String> errores = new ArrayList<>();
        for (String table : tables) {
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                String query = "SELECT * FROM " + table + " WHERE ctipopago = ? AND nfoliopago = ?";
                ps = conn.prepareStatement(query);
                log.debug("Object: {}", "Executing: " + query);
                ps.setString(1, tipoPago);
                ps.setInt(2, folioPago);
                rs = ps.executeQuery();
                if (rs.next())
                    errores.add("No se pueden reemplazar las facturas por diferencias en la tabla " + table + " " + makeErrorMSG(rs, table));
            } finally {
                CloseObject.closeObject(rs);
                CloseObject.closeObject(ps);
            }
        }
        return errores;
    }

    private static String makeErrorMSG(ResultSet rs, String table) throws SQLException {
        StringBuilder valores = new StringBuilder(" ");
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                String columnValue = rs.getString(columnName);
                valores.append(columnName).append(" = ").append(columnValue).append(" ");
            }
            return valores.toString();
        } catch (SQLException e) {
            throw e;
        }
    }

    public static List<String> validaFacturasTipo(Connection conn, String tipoPago, int folio, String tipoPermitido) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT 'La factura ' + cfactura ");
        query.append("       + ' contiene el metodo de pago ' ");
        query.append("       + cmetodopago + ' pero solo se aceptan " + tipoPermitido + "' ");
        query.append("  FROM tpagofactura  ");
        query.append(" WHERE ctipopago = ?  ");
        query.append("  AND  nfoliopago = ?  ");
        query.append("  AND  cmetodopago <> ?  ");
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<String> errores = new ArrayList<>();
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, tipoPago);
            ps.setInt(2, folio);
            ps.setString(3, tipoPermitido);
            rs = ps.executeQuery();
            while (rs.next()) {
                errores.add(rs.getString(1));
            }
            return errores;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }
}
