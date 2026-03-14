package com.syc.ejercido.pagado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONObject;
import com.syc.contable.core.AplicacionContable;
import com.syc.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ReportePAOP extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(AplicacionContable.class);

    public JSONObject guardarNuevoMesPAOP(String mesAnterior, String mesNuevo, String year) throws SQLException {
        PreparedStatement pstmnt = null, pstmntInsert = null;
        ResultSet rs = null;
        Connection conn = null;
        JSONObject json = new JSONObject();
        try {
            conn = getConnection();
            //Encabezado de CXP
            /*			pstmnt = conn.prepareStatement(" SELECT cEjercicioFiscal,nMes ,nRenglon, cCVE_CUCOP, cCONCEPTO, mMultianualEstimado, mEstimadoMipymes, mEstimadoNoCubiertasTLC, nCantidad, nUnidadMedida, cTipoProcContratacion, nEntidadFederativa, nTrimestre1, nTrimestre2, nTrimestre3, nTrimestre4, fInicialContrato, nPlurianual, nEjerciciosFicales, mAnualEjercer, cComentario1, fFinalContrato, cComentario3, cTipoProcedimiento " +
										   " FROM TPROGRAMAANUALOBRAPUBLICA WITH(NOLOCK) " +
										   " WHERE nMes = "+mesAnterior+" AND cEjercicioFiscal = '"+year+"' ");*/
            /*rs = pstmnt.executeQuery();
			while(rs.next()){
				
				String fInicialContrato = rs.getString("fInicialContrato");
				String fFinalContrato = rs.getString("fFinalContrato");*/
            /*			pstmntInsert = conn.prepareStatement("INSERT INTO TPROGRAMAANUALOBRAPUBLICA (cEjercicioFiscal,nMes ,nRenglon, cCVE_CUCOP, cCONCEPTO, mMultianualEstimado, mEstimadoMipymes, mEstimadoNoCubiertasTLC, nCantidad, nUnidadMedida, cTipoProcContratacion, nEntidadFederativa, nTrimestre1, nTrimestre2,	nTrimestre3, nTrimestre4, fInicialContrato, nPlurianual, nEjerciciosFicales, mAnualEjercer, cComentario1, fFinalContrato, cComentario3, cTipoProcedimiento, cStatus) "
						+ " VALUES ('"+rs.getString("cEjercicioFiscal")+"' , "+mesNuevo+" , "+rs.getString("nRenglon")+",'"+rs.getString("cCVE_CUCOP")+"','"+rs.getString("cCONCEPTO")+"','"+rs.getString("mMultianualEstimado")+"','"+rs.getString("mEstimadoMipymes")+"','"+rs.getString("mEstimadoNoCubiertasTLC")+"',"+rs.getString("nCantidad")+","+rs.getString("nUnidadMedida")+",'"+rs.getString("cTipoProcContratacion")+"',"+rs.getString("nEntidadFederativa")+","+rs.getString("nTrimestre1")+","+rs.getString("nTrimestre2")+","+rs.getString("nTrimestre3")+","+rs.getString("nTrimestre4")+",'"+fInicialContrato+"',"+rs.getString("nPlurianual")+","+rs.getString("nEjerciciosFicales")+",'"+rs.getString("mAnualEjercer")+"','"+rs.getString("cComentario1")+"','"+fFinalContrato+"','"+rs.getString("cComentario3")+"','"+rs.getString("cTipoProcedimiento")+"',1  )");
				pstmntInsert.executeUpdate();*/
            String strQuery = " declare @mesAnterior int ";
            strQuery += " declare @mesActual int ";
            strQuery += " set @mesAnterior = " + mesAnterior + "  ";
            strQuery += " set @mesActual = " + mesNuevo + " ";
            // MLR Revisar si se agrega a todos los usuarios tomarlo en cuenta en la consulta para que elimine por unidad ejecutora
            strQuery += " delete from tPROGRAMAANUALOBRAPUBLICA where nmes = @mesActual and cstatus = 1 ";
            strQuery += " insert into tPROGRAMAANUALOBRAPUBLICA (cEjercicioFiscal, nMes, nRenglon, cCVE_CUCOP, cCONCEPTO, mMultianualEstimado, mEstimadoMipymes, mEstimadoNoCubiertasTLC, nCantidad, nUnidadMedida, cTipoProcContratacion, nEntidadFederativa, nTrimestre1, nTrimestre2, nTrimestre3, nTrimestre4, fInicialContrato, nPlurianual, nEjerciciosFicales, mAnualEjercer, cComentario1, fFinalContrato, cComentario3, cTipoProcedimiento, cStatus, UE, EP, fechaDelReporte, noContrato, oliAutorizado, valorTotalObra) ";
            strQuery += " select cEjercicioFiscal, @mesActual, nRenglon, cCVE_CUCOP, cCONCEPTO, mMultianualEstimado, mEstimadoMipymes, mEstimadoNoCubiertasTLC, nCantidad, nUnidadMedida, cTipoProcContratacion, nEntidadFederativa, nTrimestre1, nTrimestre2, nTrimestre3, nTrimestre4, fInicialContrato, nPlurianual, nEjerciciosFicales, mAnualEjercer, cComentario1, fFinalContrato, cComentario3, cTipoProcedimiento, 1 cstatus, UE, EP, fechaDelReporte, noContrato, oliAutorizado, valorTotalObra ";
            strQuery += " from tPROGRAMAANUALOBRAPUBLICA paop ";
            strQuery += "    where nMes = @mesAnterior and cStatus = 2 ";
            System.out.println("copiar mes anterior" + strQuery);
            pstmntInsert = conn.prepareStatement(strQuery);
            pstmntInsert.executeUpdate();
            //}
            conn.commit();
            json.put("status", "correcto");
        } catch (Exception e) {
            log.error("Error occurred", "Error: " + e);
            conn.rollback();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmnt != null) {
                    pstmnt.close();
                }
                if (pstmntInsert != null) {
                    pstmntInsert.close();
                }
            } catch (Exception e) {
                log.warn("Error: cerrando statement", e);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                log.warn("Error: cerrando conexion", e);
            }
            pstmnt = null;
            pstmntInsert = null;
            rs = null;
            conn = null;
        }
        return json;
    }
}
