package com.syc.obrapublica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.syc.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.utils.ModuleProperties;
import com.syc.utils.mail.MessageComposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class AdvertenciaAResponsableCancelacionComposser extends DataSourceManager implements MessageComposer {

    private static final Logger log = LoggerFactory.getLogger(AdvertenciaAResponsableCancelacionComposser.class);

    private static ModuleProperties moduleProperties;

    int diasMaximoApartado = -1;

    int diasAdvertencia = -1;

    private static final String ENCABEZADO_CORREO = "<b>Atenci\u00F3n.</b>" + "<br><br>El sistema ha detectado los siguientes contratos con recursos apartados autorizados a punto de llegar a la fecha l\u00EDmite de tolerancia. " + "<br>Se hace este hecho de su conocimiento para que tome las acciones pertinentes." + "<br>Listado de contratos:";

    private static final String FIRMA_CORREO = "<br><br>" + "\nAtte. " + "\nSistema de Administraci\u00F3n Integral SAI" + "\nM\u00F3dulo de Obra P\u00FAblica";

    public AdvertenciaAResponsableCancelacionComposser() throws Exception {
        synchronized (this) {
            if (moduleProperties == null)
                moduleProperties = new ModuleProperties("mop");
            String strDiasMaximoApartado = moduleProperties.getProperty("max.dias.apartado");
            if (strDiasMaximoApartado == null || "".equals(strDiasMaximoApartado))
                throw new Exception("No se especifico en las propiedades del sistema el parametro mop.max.dias.apartado");
            String strDiasAdvertencia = moduleProperties.getProperty("dias.alerta");
            if (strDiasAdvertencia == null || "".equals(strDiasAdvertencia))
                throw new Exception("No se especifico en las propiedades del sistema el parametro mop.dias.alerta");
            this.diasAdvertencia = Integer.parseInt(strDiasAdvertencia);
            this.diasMaximoApartado = Integer.parseInt(strDiasMaximoApartado);
        }
    }

    public String composeMessage() throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT v.foliosai, " + "       CASE " + "         WHEN v.ccvecontrato IS NULL " + "               OR v.ccvecontrato = '' THEN 'No Capturado' " + "         ELSE v.ccvecontrato " + "       END                                                       AS ccvecontrato " + "       , " + "       CONVERT(VARCHAR, v.faplicacion, 103)                      AS " + "       faplicacion, " + "       '$' " + "       + CONVERT( VARCHAR, ( CONVERT( MONEY, v.nmontoconiva) ) ) AS nmontoconiva " + "       , " + "       v.u_login, " + "       v.diasapartado, " + "       ? - v.diasapartado                                       AS " + "       diasRestantes " + "FROM   vobra_publica_apartado_pendiente v " + " WHERE  diasapartado >= ? " + " AND       ? - v.diasapartado > 0 ";
        StringBuffer messageBody = new StringBuffer();
        try {
            conn = getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, diasMaximoApartado);
            ps.setInt(2, diasMaximoApartado - diasAdvertencia);
            ps.setInt(3, diasMaximoApartado);
            rs = ps.executeQuery();
            while (rs.next()) {
                messageBody.append("<br>Contrato: <b>" + rs.getString("ccvecontrato") + "</b>");
                messageBody.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<b>Folio SAI:</b>" + rs.getString("foliosai"));
                messageBody.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<b>Monto Apartado:</b>" + rs.getString("nmontoconiva"));
                messageBody.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<b>Autorizacion apartado:</b>" + rs.getString("faplicacion"));
                messageBody.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<b>Usuario Responsable:</b>" + rs.getString("u_login"));
                messageBody.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<b>Dias Apartado:</b>" + rs.getString("diasapartado"));
                messageBody.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<b>Dias restantes:</b>" + rs.getString("diasRestantes"));
                messageBody.append("<br><br>");
            }
            if ("".equals(messageBody.toString()))
                return "";
            else
                return ENCABEZADO_CORREO + messageBody.toString() + FIRMA_CORREO;
        } finally {
            try {
                CloseObject.closeObject(ps, false);
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(conn, false);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }
}
