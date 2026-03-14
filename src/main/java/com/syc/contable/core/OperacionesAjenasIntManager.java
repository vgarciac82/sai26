package com.syc.contable.core;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class OperacionesAjenasIntManager {

    public static int insertEncabezado(Connection conn, OperacionesAjenasIntEncabezado encabezado) throws Exception {
        String query = "INSERT INTO dbo.tOperAjenasIntegradoEnc" + "        ( nFolioOperAjenasInt ," + "          cBeneficiario ," + "          fCaptura ," + "          fDesde ," + "          fHasta ," + "          caNoContrarreciboInt ," + "          mImporteNeto ," + "          U_LOGIN ," + "          cCentroContable ," + "          aEjercicioFiscal ," + "          cUnidadResponsable ," + "          cRamo ," + "          fCancelacion ," + "          nEnviadoSICOP ," + "          cIDRFC " + "        )" + "VALUES  ( ? , " + "          ? , " + "          ? , " + "          ? , " + "          ? , " + "          ? , " + "          ? , " + "          ? , " + "          ? , " + "          ? , " + "          ? , " + "          ? , " + "          ? , " + "          ? , " + "          ?   " + "        )";
        int retVal = 0;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, encabezado.getnFolioOperAjenasInt());
            ps.setString(2, encabezado.getcBeneficiario());
            ps.setDate(3, encabezado.getfCaptura() == null ? null : new Date(encabezado.getfCaptura().getTime()));
            ps.setDate(4, encabezado.getfDesde() == null ? null : new Date(encabezado.getfDesde().getTime()));
            ps.setDate(5, encabezado.getFHasta() == null ? null : new Date(encabezado.getFHasta().getTime()));
            ps.setString(6, encabezado.getCaNoContrarrecibo());
            ps.setDouble(7, encabezado.getmImportes());
            ps.setString(8, encabezado.getU_LOGIN());
            ps.setString(9, encabezado.getcCentroContable());
            ps.setString(10, encabezado.getaEjercicioFiscal());
            ps.setString(11, encabezado.getcUnidadResponsable());
            ps.setString(12, encabezado.getcRamo());
            ps.setDate(13, null);
            ps.setString(14, encabezado.getnEnviadoSICOP());
            ps.setString(15, encabezado.getcIDRFC());
            retVal = ps.executeUpdate();
            return retVal;
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    public static int insertDetalle(Connection conn, List<OperacionesAjenasIntDetalle> detalle) throws Exception {
        String queryInsert = "INSERT INTO tOperAjenasIntegradoDet" + "        ( nFolioOperAjenasInt ," + "          nDocRenglon ," + "          nFolioOperAjenas ," + "          caNoContrarreciboOA ," + "          cUnidadResponsable ," + "          cIDRFC ," + "          cCentroContable ," + "          aEjercicioFiscal ," + "          Ep ," + "          mTotal ," + "          nFolioDoc ," + "          caNoContrarrecibo ," + "          cTipoDoc ," + "          cRamo ," + "          nDocRenglonInt, " + "          mAjuste, " + "          cMes " + "        )" + "VALUES  ( ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ? ," + "          ?  " + "        )";
        PreparedStatement ps = null;
        int retVal = 0;
        try {
            ps = conn.prepareStatement(queryInsert);
            for (Iterator<OperacionesAjenasIntDetalle> it = detalle.iterator(); it.hasNext(); ) {
                OperacionesAjenasIntDetalle insertDetObj = it.next();
                ps.setInt(1, insertDetObj.getnFolioOperAjenasInt());
                ps.setInt(2, insertDetObj.getnDocRenglon());
                ps.setInt(3, insertDetObj.getnFolioOperAjenas());
                ps.setString(4, insertDetObj.getCaNoContrarreciboOA());
                ps.setString(5, insertDetObj.getcUnidadResponsable());
                ps.setString(6, insertDetObj.getcIDRFC());
                ps.setString(7, insertDetObj.getcCentroContable());
                ps.setString(8, insertDetObj.getaEjercicioFiscal());
                ps.setString(9, insertDetObj.getEp());
                ps.setDouble(10, insertDetObj.getmTotal());
                ps.setInt(11, insertDetObj.getnFolioDoc());
                ps.setString(12, insertDetObj.getCaNoContrarrecibo());
                ps.setString(13, insertDetObj.getcTipoDoc());
                ps.setString(14, insertDetObj.getcRamo());
                ps.setInt(15, insertDetObj.getnDocRenglonInt());
                ps.setDouble(16, insertDetObj.getmAjuste());
                ps.setString(17, insertDetObj.getcMes());
                retVal += ps.executeUpdate();
            }
            return retVal;
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    public static List<Integer> getFoliosIntegrados(Connection conn, String folio) throws SQLException {
        int nFolio = Util.folio(folio);
        StringBuilder query = new StringBuilder();
        query.append("SELECT	DISTINCT nFolioOperAjenas");
        query.append("  FROM	tOperAjenasIntegradoDet");
        query.append(" WHERE	nFolioOperAjenasInt = ?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Integer> folios = new ArrayList<>();
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, nFolio);
            rs = ps.executeQuery();
            while (rs.next()) {
                folios.add(rs.getInt(1));
            }
            return folios;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }
}
