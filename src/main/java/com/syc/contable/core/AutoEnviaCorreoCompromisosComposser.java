package com.syc.contable.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.syc.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.utils.ModuleProperties;
import com.syc.utils.mail.MessageComposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class AutoEnviaCorreoCompromisosComposser extends DataSourceManager implements MessageComposer {

    private static final Logger log = LoggerFactory.getLogger(AutoEnviaCorreoCompromisosComposser.class);

    private static ModuleProperties moduleProperties;

    int diasMaximoCompromiso = -1;

    public boolean ambienteDesarrollo = false;

    public List<AlertaCorreoCompromiso> listaCorreoCompromisos() throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<AlertaCorreoCompromiso> advertencias = new ArrayList<AlertaCorreoCompromiso>();
        //" 'faragonv@axtel.com.mx' AS U_EMAIL, " +
        String //" 'faragonv@axtel.com.mx' AS U_EMAIL, " +
        query = " SELECT DISTINCT " + " usuario.U_EMAIL, " + " '<B>Atencion</b></br>Se notifica que el Compromiso:[' + compromisoEncabezado.caNoCompromiso + '], del Contrato:[' + compromisoEncabezado.cIdContrato + '], esta pendiente de Autorizar; Favor de revisar y continuar con el trámite.<br><br>Gracias<br> ' AS mensaje " + " FROM cg_caso caso WITH (NOLOCK) " + " INNER JOIN dbo.tCompromisoEncabezado compromisoEncabezado WITH (NOLOCK) " + " 	ON CONVERT( INT, SUBSTRING(caso.C_FOLIO, 10, 10) ) = compromisoEncabezado.nFolioCompromiso " + " INNER JOIN dbo.tCompromisoDetalle compromisoDetalle WITH (NOLOCK) " + " 	ON compromisoEncabezado.nFolioCompromiso = compromisoDetalle.nFolioCompromiso " + " INNER JOIN CG_USUARIO usuario WITH (NOLOCK) " + " 	ON (usuario.U_LOGIN = compromisoEncabezado.usuario) " + " WHERE caso.ID_TC = 7 " + " AND compromisoEncabezado.cDocumentoHaplicado IS NULL " + " AND SUBSTRING( compromisoDetalle.ep, 40,1) <> '4' " + " AND caso.C_FECHA_INI <= DATEADD(day, " + diasMaximoCompromiso + ",GETDATE())";
        try {
            conn = getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            String mensaje = "";
            String emailUsuario = "";
            while (rs.next()) {
                if (ambienteDesarrollo) {
                    emailUsuario = "faragonv@axtel.com.mx";
                    mensaje = rs.getString("mensaje");
                } else {
                    emailUsuario = rs.getString("U_EMAIL");
                    mensaje = rs.getString("mensaje");
                }
                AlertaCorreoCompromiso advertencia = null;
                advertencia = new AlertaCorreoCompromiso(mensaje, emailUsuario);
                advertencias.add(advertencia);
            }
            return advertencias;
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

    public String composeMessage() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
