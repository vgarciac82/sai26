package com.syc.sai.interfaces;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.commons.lang.StringUtils;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.gestion.core.Usuario;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class CFDIManager {

    public static void insert(Connection conn, Usuario u, CFDIBusinessLogic cfdiBusinessLogic) throws Exception {
        PreparedStatement ps = null;
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tcfdiencabezado  ");
        query.append("             (  ");
        query.append("                         nfoliocfdi ,  ");
        query.append("                         fcreacion ,  ");
        query.append("                         cunidadresponsable ,  ");
        query.append("                         cdescripcionpoliza ,  ");
        query.append("                         ctipocomprobante ,  ");
        query.append("                         cfechaemision ,  ");
        query.append("                         crfccliente ,  ");
        query.append("                         cnombrecliente ,  ");
        query.append("                         cpaternocliente ,  ");
        query.append("                         cmaternocliente ,  ");
        query.append("                         cdomiciliofiscal ,  ");
        query.append("                         cconcepto ,  ");
        query.append("                         cmetodopago ,  ");
        query.append("                         cformapago ,  ");
        query.append("                         cncuenta ,  ");
        query.append("                         csubtotal ,  ");
        query.append("                         cimpuesto ,  ");
        query.append("                         cdescuento ,  ");
        query.append("                         ctotal ,  ");
        query.append("                         cramo ,  ");
        query.append("                         faplicacion ,  ");
        query.append("                         aejerciciofiscal ,  ");
        query.append("                         u_login ,  ");
        query.append("                         ccentrocontable, cUnidadResponsableContable  ");
        query.append("             )  ");
        query.append("             VALUES  ");
        query.append("             (  ");
        query.append("                         ?,  ");
        query.append("                         ?,  ");
        query.append("                         ?,  ");
        query.append("                         ?,  ");
        query.append("                         ?,  ");
        query.append("                         ?,  ");
        query.append("                         ?,  ");
        query.append("                         ?,  ");
        query.append("                         ?,  ");
        query.append("                         ?,  ");
        query.append("                         ?,  ");
        query.append("                         ?,  ");
        query.append("                         ?,  ");
        query.append("                         ?,  ");
        query.append("                         ?,  ");
        query.append("                         ?,  ");
        query.append("                         ?,  ");
        query.append("                         ?,  ");
        query.append("                         ?,  ");
        query.append("                         '16', ");
        query.append("getdate(), ");
        query.append("?, ");
        query.append("?, ");
        query.append("?, ");
        query.append("'RHQ')");
        try {
            String ejercicioFiscal = EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal();
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, cfdiBusinessLogic.getnFolioCFDI());
            ps.setString(2, cfdiBusinessLogic.getFechaEmision());
            ps.setString(3, StringUtils.isEmpty(u.getU_UR()) ? "A02" : u.getU_UR());
            ps.setString(4, cfdiBusinessLogic.getConcepto());
            ps.setString(5, cfdiBusinessLogic.getTipoComprobante());
            ps.setString(6, cfdiBusinessLogic.getFechaEmision());
            ps.setString(7, cfdiBusinessLogic.getRFCCliente());
            ps.setString(8, cfdiBusinessLogic.getNombreCliente());
            ps.setString(9, cfdiBusinessLogic.getPaternoCliente());
            ps.setString(10, cfdiBusinessLogic.getMaternoCliente());
            ps.setString(11, cfdiBusinessLogic.getDomicilioFiscal());
            ps.setString(12, cfdiBusinessLogic.getConcepto());
            ps.setString(13, cfdiBusinessLogic.getMetodoPago());
            ps.setString(14, cfdiBusinessLogic.getFormaPago());
            ps.setString(15, cfdiBusinessLogic.getnCuenta());
            ps.setString(16, cfdiBusinessLogic.getSubTotal());
            ps.setString(17, cfdiBusinessLogic.getImpuesto());
            ps.setString(18, cfdiBusinessLogic.getDescuento());
            ps.setString(19, cfdiBusinessLogic.getTotal());
            ps.setString(20, ejercicioFiscal);
            ps.setString(21, u.getLogin());
            ps.setString(22, u.getPropiedad("CCENTROCONTABLE").getValor());
            ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static String getNotificacionREPCorreo(Connection conn, String rFCBeneficiario) throws Exception {
        String querySelect = "SELECT cCorreoNotificaciones FROM tBeneficiarioCobranza WITH(NOLOCK) WHERE dRFC = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        String correo = null;
        try {
            ps = conn.prepareStatement(querySelect);
            ps.setString(1, rFCBeneficiario);
            rs = ps.executeQuery();
            if (rs.next())
                correo = rs.getString(1);
            else
                throw new Exception("No se encontro correo de cobranza definido para el beneficiario " + rFCBeneficiario);
            if (StringUtils.isBlank(correo))
                throw new Exception("No se encontro correo de cobranza definido para el beneficiario " + rFCBeneficiario);
            return correo;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static String generaHTMLNotificaREPFaltante(Connection conn, Usuario u, String unidadEjecutora, String rFCBeneficiario) throws Exception {
        String mailBody = "<html>";
        mailBody += "<head>";
        mailBody += "<meta charset=\"UTF-8\">";
        mailBody += "<style type=\"text/css\">";
        mailBody += "body {";
        mailBody += "	font-family: verdana, arial, sans-serif;";
        mailBody += "	font-size: 12px;";
        mailBody += "}";
        mailBody += "table {";
        mailBody += "	font-size: 12px;";
        mailBody += "	color: #333333;";
        mailBody += "	border-width: 1px;";
        mailBody += "	border-color: #666666;";
        mailBody += "	border-collapse: collapse;";
        mailBody += "}";
        mailBody += "table th {";
        mailBody += "	border-width: 1px;";
        mailBody += "	padding: 8px;";
        mailBody += "	border-style: solid;";
        mailBody += "	border-color: #666666;";
        mailBody += "	background-color: #dedede;";
        mailBody += "}";
        mailBody += "table td {";
        mailBody += "	border-width: 1px;";
        mailBody += "	padding: 8px;";
        mailBody += "	border-style: solid;";
        mailBody += "	border-color: #666666;";
        mailBody += "	background-color: #ffffff;";
        mailBody += "}";
        StringBuilder queryFacturasPendientes = new StringBuilder();
        queryFacturasPendientes.append("SELECT canocontrarrecibo, cunidadejecutora + ' - ' + d_descripcion AS areaResponsable, facturauuid, montofactura, mpendientecomprobar ");
        queryFacturasPendientes.append("FROM   v_treciboelectronico_pagofactura pagoFactura WITH(NOLOCK)    INNER JOIN tcatunidadresponsable unidades WITH(NOLOCK) ");
        queryFacturasPendientes.append("               ON pagoFactura.cunidadejecutora = unidades.cunidadresponsable ");
        queryFacturasPendientes.append(" WHERE nComprobado = 0 AND rfc = ? " + (StringUtils.isBlank(unidadEjecutora) ? "" : "       AND cunidadejecutora = ?"));
        StringBuilder queryResponsableCobranza = new StringBuilder();
        queryResponsableCobranza.append(" SELECT cnombreresponsable ");
        queryResponsableCobranza.append("       + Isnull( ' ' + cappaternoresponsable, '' ) ");
        queryResponsableCobranza.append("       + Isnull( ' ' + capmaternoresponsable, '' ) AS responsableNombre, ");
        queryResponsableCobranza.append("       Isnull(ccargoresponsable, '')               AS cargo ");
        queryResponsableCobranza.append(" FROM   tbeneficiariocobranza WITH(nolock) ");
        queryResponsableCobranza.append(" WHERE  drfc = ?  ");
        PreparedStatement psResponsable = null;
        PreparedStatement psFacturas = null;
        ResultSet rsResponsable = null;
        ResultSet rsFacturas = null;
        try {
            psResponsable = conn.prepareStatement(queryResponsableCobranza.toString());
            psResponsable.setString(1, rFCBeneficiario);
            psFacturas = conn.prepareStatement(queryFacturasPendientes.toString());
            psFacturas.setString(1, rFCBeneficiario);
            if (!StringUtils.isBlank(unidadEjecutora))
                psFacturas.setString(2, unidadEjecutora);
            rsResponsable = psResponsable.executeQuery();
            String nombreResponsable = null;
            String cargoResponsable = null;
            if (rsResponsable.next()) {
                nombreResponsable = rsResponsable.getString("responsableNombre");
                cargoResponsable = rsResponsable.getString("cargo");
            }
            mailBody += "</style>";
            mailBody += "</head>";
            mailBody += "<body>";
            mailBody += "	<b>" + nombreResponsable + "</b>";
            mailBody += "	<br>";
            mailBody += "	<b>" + cargoResponsable + "</b>";
            mailBody += "	<br />";
            mailBody += "<p> En base a lo estipulado en la <i>Guía de llenado del comprobante al que ";
            mailBody += "se le incorpore el complemento para recepción de pagos, publicada en el portal del SAT</i>";
            mailBody += " y para dara cumplimiento a los tiempos y documentacion requerida se hace de su conocimiento que";
            mailBody += " al momento de enviar este correo se han realizado exitosamente los siguientes pagos a su favor </p>";
            mailBody += "	<br />";
            mailBody += "	";
            mailBody += "	<table>";
            mailBody += "		<thead>";
            mailBody += "			<tr>";
            mailBody += "				<th>Documento CONAFOR</th>";
            mailBody += "				<th>Area CONAFOR</th>";
            mailBody += "				<th>UUID</th>";
            mailBody += "				<th>Monto Factura</th>";
            mailBody += "				<th>Por Comprobar</th>";
            mailBody += "			</tr>";
            mailBody += "		</thead>";
            mailBody += "		<tbody>";
            rsFacturas = psFacturas.executeQuery();
            while (rsFacturas.next()) {
                mailBody += "<tr>";
                mailBody += "\n<td>" + rsFacturas.getString("canocontrarrecibo") + "</td>";
                mailBody += "\n<td>" + rsFacturas.getString("areaResponsable") + "</td>";
                mailBody += "\n<td>" + rsFacturas.getString("facturauuid") + "</td>";
                mailBody += "\n<td>" + rsFacturas.getString("montofactura") + "</td>";
                mailBody += "\n<td>" + rsFacturas.getString("mpendientecomprobar") + "</td>";
                mailBody += "</tr>";
            }
            mailBody += "</tbody>";
            mailBody += "</table>";
            mailBody += "<br />";
            mailBody += "<br />";
            if (u != null)
                mailBody += "<p>Por favor enviar los recibos de pago faltantes a <b>" + u.getNombre() + " Correo: " + u.getU_email() + "</b><br>";
            else
                mailBody += "<p>Por favor enviar los recibos de pago faltantes a su responsable de contrato en CONAFOR. <br>";
            mailBody += "Se espera que los comprobantes de pago esten en pares <b>PDF + XML llamados de la misma forma.</b> Por ejemplo rep.xml con su correspondiente rep.pdf en un archivo comprimido zip";
            mailBody += "<br />";
            mailBody += "</p>";
            mailBody += "	<br />";
            mailBody += "	<p>";
            mailBody += "		Esta es una Notificacion Automatica por lo que le suplicamos no responda directamente sobre este correo. Responda a la persona indicada.";
            mailBody += "	</p>";
            mailBody += "</body>";
            mailBody += "</html>";
            return mailBody;
        } finally {
            CloseObject.closeObject(rsResponsable);
            CloseObject.closeObject(rsFacturas);
            CloseObject.closeObject(psResponsable);
            CloseObject.closeObject(psFacturas);
        }
    }

    public static int getNumeroDeFacturas(Connection conn, EgresoEncabezado egresoEncabezado) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT COUNT(*) AS numFacturas ");
        query.append("  FROM tPagoFactura WITH(NOLOCK) ");
        query.append(" WHERE cTipoPago = ? ");
        query.append("   AND nFolioPago = ?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        int totalFacturas = 0;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, egresoEncabezado.getTipoPago());
            ps.setInt(2, egresoEncabezado.getFolioPago());
            rs = ps.executeQuery();
            if (rs.next()) {
                totalFacturas = rs.getInt(1);
            }
            return totalFacturas;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }
}
