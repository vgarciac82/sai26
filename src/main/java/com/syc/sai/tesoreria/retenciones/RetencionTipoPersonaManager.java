package com.syc.sai.tesoreria.retenciones;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.tesoreria.retenciones.core.Retencion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class RetencionTipoPersonaManager {

    private static Logger log = LoggerFactory.getLogger(RetencionTipoPersonaManager.class);

    public static String obtieneTipoPersona(Connection conn, String cRFC) throws Exception {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String tipoPersona = "";
        String query = "SELECT cIdTipoPersonaRFC FROM tBeneficiario WITH (NOLOCK) WHERE dRFC = ?";
        try {
            log.debug("Object: " + String.valueOf("Buscando tipo persona para el RFC " + cRFC));
            log.trace("Object: {}", "Query: " + query);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, cRFC);
            rs = pStatement.executeQuery();
            while (rs.next()) {
                tipoPersona = rs.getString("cIdTipoPersonaRFC");
            }
            return tipoPersona;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pStatement, false);
        }
    }

    public static List<Retencion> obtieneRetenciones(Connection conn, String tipoPersona, String partidas, String cc, String tipoPago, String folioPago) throws Exception {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        List<Retencion> retenciones = new ArrayList<Retencion>();
        String query = "SELECT DISTINCT cIdTipoRetencion,cTipoRetencion,nPorcRetencion " + " FROM pCatalogoTipoRetencion catalogo WITH (NOLOCK) " + " INNER JOIN tRelacionPartidaRetencion Obligatorias WITH (NOLOCK) " + " ON catalogo.cIdTipoRetencion = Obligatorias.idRetencion " + " WHERE partida IN (SELECT Registro FROM dbo.fn_Split_todos(',',?)) " + " AND 1 = CASE WHEN ? = 2 THEN cFisica WHEN ? = 1 THEN cMoral ELSE 0 END";
        PreparedStatement psInsert = null;
        String queryInsert = "INSERT INTO tPagoDirectoRetencion " + "	SELECT YEAR(GETDATE()), ?, ?, ?";
        try {
            log.debug("Object: " + String.valueOf("Buscando retenciones " + tipoPersona + " en la partida " + partidas));
            log.trace("Object: {}", "Query: " + query);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, partidas);
            pStatement.setString(2, tipoPersona);
            pStatement.setString(3, tipoPersona);
            rs = pStatement.executeQuery();
            while (rs.next()) {
                Retencion retencion = new Retencion();
                retencion.setDescRetencion(rs.getString("cTipoRetencion"));
                retencion.setIdRetencion(rs.getInt("cIdTipoRetencion"));
                retencion.setPorcentaje(rs.getFloat("nPorcRetencion"));
                retenciones.add(retencion);
                if (tipoPago.equals("Directo")) {
                    psInsert = conn.prepareStatement(queryInsert);
                    psInsert.setString(1, cc);
                    psInsert.setString(2, folioPago);
                    psInsert.setInt(3, rs.getInt("cIdTipoRetencion"));
                    psInsert.execute();
                    conn.commit();
                }
            }
            return retenciones;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pStatement, false);
            CloseObject.closeObject(psInsert, false);
        }
    }
}
