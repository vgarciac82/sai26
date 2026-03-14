package com.syc.adquisiciones.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneraLayoutManager {

    private static Logger log = LoggerFactory.getLogger(GeneraLayoutManager.class);

    public StringBuffer getEncabezadoLayout(Connection conn, String cadenaFolios) throws Exception {
        log.info("Obteniendo datos de layout partado encabezado");
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String query = null;
        String encabezado = null;
        StringBuffer archivoLayout = null;
        try {
            archivoLayout = new StringBuffer();
            query = "select \r\n" + "'H' cHeader\r\n" + ",'O' cTipoMov\r\n" + ",1 cOrig\r\n" + ",''cPrecom\r\n" + ",tipoI.cTipoIntegracion+'-'+integra.cUnidadEjecutora+'-'+CONVERT(varchar,integra.nConsecutivo)+'|'+replace(replace(replace(integra.cDescripcion,',',''),CHAR(13),' '),CHAR(10),' ') cJustificacion\r\n" + ",'RHQ' cUnidad\r\n" + ",'RHQ' cUnidad_creadora\r\n" + ",'RHQ' cUnidad_receptora\r\n" + ",'16' cRamoCreador\r\n" + ",'16' cRamo\r\n" + ",'16' cRamoReceptor\r\n" + ",convert(varchar,convert(date,GETDATE()),103) fFechaExpedicion\r\n" + ",convert(varchar,convert(date,GETDATE()),103) fFechaAplicacion\r\n" + ",integra.nIdIntegraRequi nControlInterno\r\n" + ",tipoI.cTipoIntegracion+'-'+integra.cUnidadEjecutora+'-'+CONVERT(varchar,integra.nConsecutivo) cFolioInterno\r\n" + ",integra.nIdIntegraRequi \r\n" + "from mIntegraRequis integra with(Nolock)\r\n" + "inner join mCatalogoTipoIntegracion tipoI with(Nolock)\r\n" + "on integra.nIdTipoIntegracion=tipoI.nIdTipoIntegracion\r\n" + "where integra.nEstatus=2 and integra.nEnviadoSICOP=0\r\n" + "and integra.nIdIntegraRequi in(" + cadenaFolios + ")\r\n" + "order by integra.nIdIntegraRequi ";
            log.info("Object: {}", "query: " + query);
            pstmnt = conn.prepareStatement(query);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                // A
                encabezado = // B
                rs.getString("cHeader").trim() + "," + rs.getString("cTipoMov").trim() + // C
                "," + rs.getString("cOrig").trim() + // D
                "," + rs.getString("cPrecom").trim() + // E
                "," + rs.getString("cJustificacion").trim() + // F
                "," + rs.getString("cUnidad").trim() + // G
                "," + rs.getString("cUnidad_creadora").trim() + // H
                "," + rs.getString("cUnidad_receptora").trim() + // I
                "," + rs.getString("cRamoCreador").trim() + // J
                "," + rs.getString("cRamo").trim() + // K
                "," + rs.getString("cRamoReceptor").trim() + // L
                "," + rs.getString("fFechaExpedicion").trim() + // M
                "," + rs.getString("fFechaAplicacion").trim() + // N
                "," + rs.getString("cFolioInterno").trim() + // O
                "," + rs.getString("cFolioInterno").trim() + "\r\n";
                archivoLayout.append(encabezado);
                archivoLayout.append(getDetalleLayout(conn, rs.getString("nIdIntegraRequi")));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmnt != null) {
                pstmnt.close();
            }
            encabezado = null;
            pstmnt = null;
            query = null;
            rs = null;
        }
        return archivoLayout;
    }

    public String getDetalleLayout(Connection conn, String cadenaFolios) throws SQLException {
        log.info("Obteniendo datos de layout partado encabezado");
        PreparedStatement pstmnt = null;
        String query = null;
        String detalle = "";
        ResultSet rs = null;
        try {
            query = "select \r\n" + "'542' nEvento,'156_SPOA'cEvento\r\n" + ",'M'cEM,'16'cRamo\r\n" + ",'RHQ' cUR_ML,catEP.aEjercicioFiscal cEjercicio\r\n" + ",catEP.cGrupoFuncional,catEP.cFuncion\r\n" + ",catEP.cSubFuncion,catEP.cProgramaGeneral\r\n" + ",catEP.cActividadInstitucional\r\n" + ",catEP.cProgramaPresupuestario\r\n" + ",SUBSTRING(catEP.cPartida,1,1)capitulo\r\n" + ",SUBSTRING(catEP.cPartida,2,1) CO\r\n" + ",SUBSTRING(catEP.cPartida,3,1) PG\r\n" + ",SUBSTRING(catEP.cPartida,4,2) PE,catEP.cTipoGasto\r\n" + ",catEP.cFuenteFinanciamiento\r\n" + ",catEP.cEntidadFederativa\r\n" + ",catEP.cCartera PPI\r\n" + ",'0000000000' CAU\r\n" + ",'00'cop,'000' pl\r\n" + ",'000' cOf,'00000'aux1\r\n" + ",'00000'aux2,'0000000000'aux3\r\n" + ",eps.mes01+eps.mes02+eps.mes03+eps.mes04+eps.mes05+eps.mes06+eps.mes07+eps.mes08+eps.mes09+eps.mes10+eps.mes11+eps.mes12 importeTotal\r\n" + ",eps.mes01,eps.mes02,eps.mes03,eps.mes04,eps.mes05,eps.mes06,eps.mes07,eps.mes08,eps.mes09,eps.mes10,eps.mes11,eps.mes12\r\n" + "from mIntegraRequis integra with(Nolock)\r\n" + "inner join (\r\n" + "	select \r\n" + "	requisInt.nIdIntegraRequi\r\n" + "	,nIdClaveEgresos+'.'+ClaveInterna ep\r\n" + "	,SUM(mes01)mes01,SUM(mes02)mes02,SUM(mes03)mes03\r\n" + "	,SUM(mes04)mes04,SUM(mes05)mes05,SUM(mes06)mes06\r\n" + "	,SUM(mes07)mes07,SUM(mes08)mes08,SUM(mes09)mes09\r\n" + "	,SUM(mes10)mes10,SUM(mes11)mes11,SUM(mes12)mes12\r\n" + "	from mSolicitudLineasApartado solAp with(Nolock)\r\n" + "	inner join mRequisIntegradas as requisInt with(Nolock)\r\n" + "	on solAp.cIdSolicitud=requisInt.cIdSolicitud\r\n" + "	inner join (select nFolioApartado,EP from tApartadoDetalle as det with(Nolock) group by nFolioApartado,EP)det \r\n" + "	on det.nFolioApartado=requisInt.nFolioApartado\r\n" + "	and nIdClaveEgresos+'.'+ClaveInterna=det.EP\r\n" + "	group by requisInt.nIdIntegraRequi,nIdClaveEgresos,ClaveInterna\r\n" + ")eps on integra.nIdIntegraRequi=eps.nIdIntegraRequi\r\n" + "inner join tCatalogoEP catEP with(Nolock)\r\n" + "on eps.EP=catEP.EP\r\n" + "where integra.nEstatus=2 and integra.nEnviadoSICOP=0\r\n" + "and integra.nIdIntegraRequi in(" + cadenaFolios + ")\r\n" + "order by integra.nIdIntegraRequi";
            log.info("Object: {}", "query: " + query);
            pstmnt = conn.prepareStatement(query);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                // A
                detalle += // B
                rs.getString("nEvento").trim() + "," + rs.getString("cEvento").trim() + // C
                "," + rs.getString("cEM").trim() + // D
                "," + rs.getString("cRamo").trim() + // E
                "," + rs.getString("cUR_ML").trim() + // F
                "," + rs.getString("cEjercicio").trim() + // G
                "," + rs.getString("cGrupoFuncional").trim() + // H
                "," + rs.getString("cFuncion").trim() + // I
                "," + rs.getString("cSubFuncion").trim() + // J
                "," + rs.getString("cProgramaGeneral").trim() + // K
                "," + rs.getString("cActividadInstitucional").trim() + // L
                "," + rs.getString("cProgramaPresupuestario").trim() + // M
                "," + rs.getString("capitulo").trim() + // N
                "," + rs.getString("CO").trim() + // O
                "," + rs.getString("PG").trim() + // p
                "," + rs.getString("PE").trim() + // Q
                "," + rs.getString("cTipoGasto").trim() + // R
                "," + rs.getString("cFuenteFinanciamiento").trim() + // S
                "," + rs.getString("cEntidadFederativa").trim() + // T
                "," + rs.getString("PPI").trim() + // U
                "," + rs.getString("CAU").trim() + // V
                "," + rs.getString("cop").trim() + // W
                "," + rs.getString("pl").trim() + // X
                "," + rs.getString("cOf").trim() + // Y
                "," + rs.getString("aux1").trim() + // Z
                "," + rs.getString("aux2").trim() + // AA
                "," + rs.getString("aux3").trim() + // AB
                "," + rs.getString("importeTotal").trim() + // AC
                "," + rs.getString("mes01").trim() + // AD
                "," + rs.getString("mes02").trim() + // AE
                "," + rs.getString("mes03").trim() + // AF
                "," + rs.getString("mes04").trim() + // AG
                "," + rs.getString("mes05").trim() + // AH
                "," + rs.getString("mes06").trim() + // AI
                "," + rs.getString("mes07").trim() + // AJ
                "," + rs.getString("mes08").trim() + // AK
                "," + rs.getString("mes09").trim() + // AL
                "," + rs.getString("mes10").trim() + // AM
                "," + rs.getString("mes11").trim() + // AN
                "," + rs.getString("mes12").trim() + "\r\n";
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
            query = null;
            rs = null;
        }
        return detalle;
    }

    public boolean guardaDatosLayoutApartado(Connection conn, String cadenaFolios) throws SQLException {
        String query = "";
        PreparedStatement pstm = null;
        boolean resp = false;
        try {
            query = "insert into mLayoutRequis (nIdIntegraRequi,fFechaGenerado,fFechaAutorizado,cFolioSICOP,nEnviadoSICOP)\r\n" + "select nIdIntegraRequi,convert(date,GETDATE()),null,null,1 from mIntegraRequis with(Nolock) where nIdIntegraRequi in(" + cadenaFolios + ")";
            pstm = conn.prepareStatement(query);
            if (pstm.executeUpdate() > 0) {
                resp = true;
            }
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            query = null;
        }
        return resp;
    }

    public boolean updateEstatusIntegraRequi(Connection conn, String cadenaFolios, int nEstatus) throws SQLException {
        String query = "";
        PreparedStatement pstm = null;
        boolean resp = false;
        try {
            query = "update mIntegraRequis set nEnviadoSICOP=" + nEstatus + " where nIdIntegraRequi in(" + cadenaFolios + ") ";
            pstm = conn.prepareStatement(query);
            if (pstm.executeUpdate() > 0) {
                resp = true;
            }
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            query = null;
        }
        return resp;
    }

    public boolean deleteLayoutIntegraRequi(Connection conn, String cadenaFolios) throws SQLException {
        String query = "";
        PreparedStatement pstm = null;
        boolean resp = false;
        try {
            query = "delete mLayoutRequis where nIdIntegraRequi in(" + cadenaFolios + ") ";
            pstm = conn.prepareStatement(query);
            if (pstm.executeUpdate() > 0) {
                resp = true;
            }
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            query = null;
        }
        return resp;
    }
}
