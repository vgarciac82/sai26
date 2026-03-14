package com.syc.ejercido.pagado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.syc.contable.core.AplicacionContable;
import com.syc.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class CargaArchivoCadenasP extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(AplicacionContable.class);

    public String insertaLineaSicop(String cNombreProv_0, String caNoContrarrecibo_1, String nNumAcuse_2, String fEmision_3, String fVencimiento_4, String cMoneda_5, String mMonto_6, String cEstatus_7, String cIntermediarioF_8, String nNumSolicitud_9, String cCentroContable_10, String cCampoAdi2_11, String cCampoAdi3_12, String cCampoAdi4_13, String cCampoAdi5_14, String RFC_15, String cConcepto_16, String nClaveEstatus_17, String nPorcentajeDes_18, String mMontoDesc_19, String nNumIdentificador_20, String fRecepcion_21, String cTcompra_22, String cClasificador_23, String cPlazoMax_24) throws SQLException {
        String respuesta = "";
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        Connection conn = null;
        int cuantos = 0;
        String clabe = "";
        String Rfc = "";
        String CBEN = "";
        ResultSet rs = null;
        try {
            conn = getConnection();
            if (cIntermediarioF_8.equals("BANCO INTERACCIONES  S.A.")) {
                ps1 = conn.prepareStatement("Select cClabe,cRfc,CBEN from tBancosIntermediarios WITH(NOLOCK) where nIdBanco = 3 ");
                rs = ps1.executeQuery();
                if (rs.next()) {
                    clabe = rs.getString("cClabe");
                    Rfc = rs.getString("cRfc");
                    CBEN = rs.getString("CBEN");
                }
            }
            if (cIntermediarioF_8.equals("ARRENDADORA Y FACTOR BANORTE SOFOM ER G.F. BANORTE")) {
                ps1 = conn.prepareStatement("Select cClabe,cRfc,CBEN from tBancosIntermediarios WITH(NOLOCK) where nIdBanco = 1 ");
                rs = ps1.executeQuery();
                if (rs.next()) {
                    clabe = rs.getString("cClabe");
                    Rfc = rs.getString("cRfc");
                    CBEN = rs.getString("CBEN");
                }
            }
            if (cIntermediarioF_8.equals("FINANCIERA BAJIO  S.A. DE C.V. SOFOM ER")) {
                ps1 = conn.prepareStatement("Select cClabe,cRfc,CBEN from tBancosIntermediarios WITH(NOLOCK) where nIdBanco = 6 ");
                rs = ps1.executeQuery();
                if (rs.next()) {
                    clabe = rs.getString("cClabe");
                    Rfc = rs.getString("cRfc");
                    CBEN = rs.getString("CBEN");
                }
            }
            if (cIntermediarioF_8.equals("BBVA BANCOMER  S.A.")) {
                ps1 = conn.prepareStatement("Select cClabe,cRfc,CBEN from tBancosIntermediarios WITH(NOLOCK) where nIdBanco = 5 ");
                rs = ps1.executeQuery();
                if (rs.next()) {
                    clabe = rs.getString("cClabe");
                    Rfc = rs.getString("cRfc");
                    CBEN = rs.getString("CBEN");
                }
            }
            if (cIntermediarioF_8.equals("SCOTIABANK INVERLAT, S.A")) {
                ps1 = conn.prepareStatement("Select cClabe,cRfc,CBEN from tBancosIntermediarios WITH(NOLOCK) where nIdBanco = 4 ");
                rs = ps1.executeQuery();
                if (rs.next()) {
                    clabe = rs.getString("cClabe");
                    Rfc = rs.getString("cRfc");
                    CBEN = rs.getString("CBEN");
                }
            }
            if (cIntermediarioF_8.equals("BANCO NACIONAL DE MEXICO, S.A. (BANAMEX)")) {
                ps1 = conn.prepareStatement("Select cClabe,cRfc,CBEN from tBancosIntermediarios WITH(NOLOCK) where nIdBanco = 2 ");
                rs = ps1.executeQuery();
                if (rs.next()) {
                    clabe = rs.getString("cClabe");
                    Rfc = rs.getString("cRfc");
                    CBEN = rs.getString("CBEN");
                }
            }
            ps = conn.prepareStatement(" DELETE FROM tCadenasPDetalle WHERE caNOcontrarrecibo = '" + caNoContrarrecibo_1 + "'");
            int intr = ps.executeUpdate();
            ps = conn.prepareStatement("INSERT INTO tCadenasPDetalle (cNombreProv,caNoContrarrecibo,nNumAcuse,fEmision,fVencimiento,cMoneda,mMonto,cEstatus,cIntermediarioF,nNumSolicitud,cCentroContable,cCampoAdi2,cCampoAdi3,cCampoAdi4,cCampoAdi5," + " RFC,cConcepto,nClaveEstatus,nPorcentajeDes,mMontoDesc,nNumIdentificador,fRecepcion,cTcompra,cClasificador,cPlazoMax,cDescartadaMotivo" + ") VALUES ('" + cNombreProv_0 + "','" + caNoContrarrecibo_1 + "','" + nNumAcuse_2 + "','" + fEmision_3 + "','" + fVencimiento_4 + "','" + cMoneda_5 + "','" + mMonto_6 + "','" + cEstatus_7 + "','" + cIntermediarioF_8 + "','" + nNumSolicitud_9 + "','" + cCentroContable_10 + "','" + clabe + "','" + Rfc + "','" + CBEN + "','" + cCampoAdi5_14 + "'," + "	'" + RFC_15 + "','" + cConcepto_16 + "','" + nClaveEstatus_17 + "','" + nPorcentajeDes_18 + "','" + mMontoDesc_19 + "','" + nNumIdentificador_20 + "','" + fRecepcion_21 + "','" + cTcompra_22 + "','" + cClasificador_23 + "','" + cPlazoMax_24 + "','N/A')");
            intr = ps.executeUpdate();
            //mandarlo cada 100 commit
            if (intr > 0) {
                respuesta = "guardado";
                cuantos = cuantos + 1;
                //System.out.println("cuantos:"+cuantos);
                conn.commit();
                if (cuantos >= 100) {
                    //System.out.println("si mas de 100:"+cuantos);
                    //conn.commit();
                }
            } else {
                respuesta = "no_guardado";
                conn.rollback();
            }
            conn.close();
        } catch (Exception e) {
            log.error("Error occurred", "Error SICOP: " + e);
            conn.rollback();
            conn.close();
        }
        return respuesta;
    }

    //ERRORES CADENAS
    public String insertaLineaSiaff(String RFC_0, String caNoContrarrecibo_1, String fEmision_2, String fVencimiento_3, String FIJO_01_4, String FIJO_1_5, String FIJO_N_6, String tipoPago_7, String cCentroContable_8, String dato1_9, String dato2_10, String dato3_11, String dato4_12, String dato5_13, String dato6_14) throws SQLException {
        String respuesta = "";
        PreparedStatement ps = null;
        int cuantos = 0;
        //ResultSet rs = null;
        Connection conn = null;
        //int ejecutar = Integer.parseInt(cuantos,10);
        try {
            conn = getConnection();
            ps = conn.prepareStatement(" DELETE FROM tCadenasPDetalle WHERE caNOcontrarrecibo = '" + caNoContrarrecibo_1 + "'");
            int intr = ps.executeUpdate();
            ps = conn.prepareStatement("INSERT INTO tCadenasPDetalle (cNombreProv,caNoContrarrecibo,nNumAcuse,fEmision,fVencimiento,cMoneda,mMonto,cEstatus,cIntermediarioF,nNumSolicitud,cCentroContable,cCampoAdi2,cCampoAdi3,cCampoAdi4,cCampoAdi5," + "RFC,cConcepto,nClaveEstatus,nPorcentajeDes,mMontoDesc,nNumIdentificador,fRecepcion,cTcompra,cClasificador,cPlazoMax,cDescartadaMotivo" + ") VALUES ('ERRORES','" + caNoContrarrecibo_1 + "','" + FIJO_01_4 + "','" + fEmision_2 + "','" + fVencimiento_3 + "','MONEDA NACIONAL',0.00,'ERRORES','N/A','" + dato6_14 + "','" + cCentroContable_8 + "','" + dato1_9 + "','" + dato2_10 + "','" + dato3_11 + "','" + dato4_12 + "'," + "'" + RFC_0 + "','" + tipoPago_7 + "','" + FIJO_1_5 + "','N/A','0.00','N/A',convert(varchar(10),GETDATE(),103),'" + FIJO_N_6 + "','" + dato5_13 + "','N/A','N/A')");
            intr = ps.executeUpdate();
            if (intr > 0) {
                respuesta = "guardado";
                cuantos = cuantos + 1;
                //System.out.println("cuantos:"+cuantos);
                conn.commit();
                //if(ejecutar >=100){
                //System.out.println("si mas de 100:"+cuantos);
                //conn.commit();
                //}
            } else {
                respuesta = "no_guardado";
                conn.rollback();
            }
            conn.close();
        } catch (Exception e) {
            log.error("Error occurred", "Error Guardar SIAFF: " + e);
            conn.rollback();
            conn.close();
        }
        return respuesta;
    }
    /*public String limpiarTabla(String nomTabla)throws SQLException{
		String valor = "";
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
			conn = getConnection();
			ps = conn.prepareStatement("DELETE FROM "+nomTabla);
			int intr = ps.executeUpdate();
			if(intr > 0){
				valor = "borrado";
				conn.commit();
			}else{
				valor = "no_borrado";
				conn.rollback();
			}
		}catch(Exception e){
			log.error("Error Limpiar Tabla: "+e);
			conn.rollback();
			conn.close();
		}		
		return valor;
	}*/
}
