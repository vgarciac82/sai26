package com.syc.obrapublica;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import com.syc.contable.AccountingEngine;
import com.syc.contable.PolizaManager;
import com.syc.crud.config.xsd.model.Sql;
import com.syc.gestion.core.Caso;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.contabilidad.utils.db.RSToTable;
import com.syc.ws.inventario.WSManager;
import com.syc.ws.obrapublica.core.EstimacionObra;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ObraPublicaManager {

    private static Logger log = LoggerFactory.getLogger(WSManager.class);

    private static final String QUERY_ACTUALIZA_CM_ENCABEZADO = "UPDATE tobrapublicaconvmodifencabezado " + "SET    ffechainicontr = CONVERT( VARCHAR,CONVERT(date,?), 103), " + "       ffechafincontr = CONVERT( VARCHAR,CONVERT(date,?), 103), " + "       mmonto = ?, " + "       nporceiva = ?, " + "       mmontoconiva = ?, " + "       cdescripcionconvenio = ?, " + "       mmontoincremento = ?, " + "       mmontoincrementoiva = ?, " + "		 cNoConvenio = ? " + "WHERE  nFolioOPConvHeader = ? ";

    // IRD 20131121 RO-0009 se agregan variables inser y actualiza pago pasivo
    private static final String QUERY_ACTUALIZA_PAG_PAS_ENCABEZADO = "UPDATE tObraPublicaPagoPasivoEncabezado " + "SET cTipoPoliza = 'OP',  mMonto = ?, " + "       [nPorceIVA] = ?, " + "       [mMontoConIVA] = ?, " + "       cDescripcionPagoPasivo = ?, foliosai = ?, fAplicacion = CONVERT( VARCHAR,CONVERT(date,?), 103), aEjercicioFiscal = ?  WHERE  nFolioOPPagPasHeader = ? ";

    private static final String QUERY_INSERTA_PAG_PAS_ENCABEZADO = "INSERT INTO tObraPublicaPagoPasivoEncabezado(cCveContrato, FolioSAI, [mMonto],[nPorceIVA],[mMontoConIVA],fAplicacion, aEjercicioFiscal, cRamo, cUnidadResponsableContable, cDocumentoHaplicado, nFolioPoliza, cTipoPoliza, nFolioPolizaCancelacion, fCancelacion, cU_UR, cU_CC, cU_UE, cDescripcionPagoPasivo) " + "  SELECT cCveContrato, FolioSAI, ? [mMonto],? [nPorceIVA],? [mMontoConIVA],? fAplicacion, ? aEjercicioFiscal, cRamo, cUnidadResponsableContable, '' cDocumentoHaplicado, null nFolioPoliza, 'OP' cTipoPoliza, null nFolioPolizaCancelacion, null fCancelacion, cU_UR, cU_CC, cU_UE, ? cDescripcionPagoPasivo " + "  FROM tobrapublicaapartadoencabezado where foliosai = ?  SELECT @@IDENTITY AS cConsecutivo";

    // MLR 20131217 RO-0010 se agregan variables inser y actualiza Plurianual
    private static final String QUERY_ACTUALIZA_PLURIANUAL_ENCABEZADO = "UPDATE tObraPublicaPlurianualEncabezado " + "SET cTipoPoliza = 'OP',  mMonto = ?, " + "       [nPorceIVA] = ?, " + "       [mMontoConIVA] = ?, " + "    foliosai = ?, fAplicacion = CONVERT( VARCHAR,CONVERT(date,?), 103), aEjercicioFiscal = ?  WHERE  nFolioOPPlurianualHeader = ? ";

    private static final String QUERY_INSERTA_PLURIANUAL_ENCABEZADO = "INSERT INTO tobraPublicaPlurianualEncabezado(cCveContrato, FolioSAI, [mMonto],[nPorceIVA],[mMontoConIVA],fAplicacion, aEjercicioFiscal, cRamo, cUnidadResponsableContable, cDocumentoHaplicado, nFolioPoliza, cTipoPoliza, nFolioPolizaCancelacion, fCancelacion, cU_UR, cU_CC, cU_UE) " + "  SELECT cCveContrato, FolioSAI, ? [mMonto],? [nPorceIVA],? [mMontoConIVA],? fAplicacion, ? aEjercicioFiscal, cRamo, cUnidadResponsableContable, '' cDocumentoHaplicado, null nFolioPoliza, 'OP' cTipoPoliza, null nFolioPolizaCancelacion, null fCancelacion, cU_UR, cU_CC, cU_UE " + "  FROM tobrapublicaapartadoencabezado where foliosai = ?  SELECT @@IDENTITY AS cConsecutivo";

    private static final String QUERY_COPIA_CM_ENCABEZADO = "INSERT INTO tObraPublicaConvModifEncabezado " + "            (cnoconvenio, " + "             ccvecontrato, " + "             ffechainicontr, " + "             ffechafincontr, " + "             mmonto, " + "             nporceiva, " + "             mmontoconiva, " + "             cdescripcionconvenio, " + "             ffechaconvenio, " + "             cramo, " + "             cunidadresponsablecontable, " + "             ctipopoliza, " + "             aejerciciofiscal, " + "             cunidadresponsable, " + "             nfolioconvmodif, " + "             cu_ur, " + "             cu_cc, " + "             cu_ue, " + "             mmontoincremento, " + "             mmontoincrementoiva, " + "             cconvenioencaptura," + "             nFolioOPConvHeaderCancel," + "				fAplicacion) " + "SELECT cnoconvenio, " + "       ccvecontrato, " + "       ffechainicontr, " + "       ffechafincontr, " + "       mmonto, " + "       nporceiva, " + "       mmontoconiva, " + "       cdescripcionconvenio, " + "       Getdate() AS fFechaConvenio, " + "       cramo, " + "       cunidadresponsablecontable, " + "       ctipopoliza, " + "       aejerciciofiscal, " + "       cunidadresponsable, " + "       nfolioconvmodif, " + "       cu_ur, " + "       cu_cc, " + "       cu_ue, " + "       mmontoincremento, " + "       mmontoincrementoiva, " + "       'S'       AS cConvenioEnCaptura," + "       nfolioopconvheader AS nFolioOPConvHeaderCancel, " + "       getDate()" + "FROM   tobrapublicaconvmodifencabezado WITH(NOLOCK)" + "WHERE  nfolioopconvheader = ? " + " SELECT @@IDENTITY AS cConsecutivo";

    public static boolean changeStatusContrato(Connection conn, String folioSAI, int status) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        String query = "UPDATE tobrapublicacompromisoencabezado SET iStatus = ? WHERE FolioSAI = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, status);
            ps.setString(2, folioSAI);
            success = ps.executeUpdate() > 0;
            return success;
        } finally {
            try {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(ps, false);
            } catch (Exception e) {
            }
        }
    }

    public static String getStatusContrato(Connection conn, String folioSAI) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statusContrato = null;
        String query = "SELECT e.iStatus FROM tObraPublicaCompromisoEncabezado e  WITH(NOLOCK) WHERE e.FolioSAI = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, folioSAI);
            rs = ps.executeQuery();
            if (rs.next())
                statusContrato = rs.getString("iStatus");
            return statusContrato;
        } finally {
            try {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(ps, false);
            } catch (Exception e) {
            }
        }
    }

    public static boolean cmTieneIncrementoMonto(Connection conn, String folioSAI) throws Exception {
        boolean retVal = false;
        String qry = "SELECT mmontoincremento " + "	FROM   tobrapublicaconvmodifencabezado WITH(nolock) " + "	WHERE  nfolioopconvheader = ?";
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(qry);
            ps.setString(1, folioSAI);
            rs = ps.executeQuery();
            if (rs.next()) {
                double val = rs.getDouble("mmontoincremento");
                retVal = val > 0.0d;
            }
            return retVal;
        } finally {
            try {
                CloseObject.closeObject(rs, false);
                CloseObject.closeObject(ps, false);
            } catch (Exception e) {
            }
        }
    }

    public static int insertaInformacionContratoConvenioModificatorio(Connection conn, String aEjercicioFiscal, String cContable, String cveContrato, String noConvenio, String fInicio, String fFin, double montoModificado) throws Exception {
        int afectados = 0;
        String qry = "INSERT INTO pcontratoobraconvenio " + "            (cejercicio, " + "             cidentidadcontable, " + "             cidcontrato, " + "             cnoconvenio, " + "             finicio, " + "             ftermino, " + "             mtotal) " + "VALUES     (?, " + "            ?, " + "            ?, " + "            ?, " + "            ?, " + "            ?, " + "            ?) ";
        PreparedStatement ps = null;
        try {
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            Date dFechaFin = fFin == null || "".equals(fFin) ? formato.parse("01/01/1900") : formato.parse(fFin);
            Date dFechaIni = fInicio == null || "".equals(fInicio) ? formato.parse("01/01/1900") : formato.parse(fInicio);
            ps = conn.prepareStatement(qry);
            ps.setString(1, aEjercicioFiscal);
            ps.setString(2, cContable);
            ps.setString(3, cveContrato);
            ps.setString(4, noConvenio);
            ps.setDate(5, (dFechaIni == null ? (java.sql.Date) null : new java.sql.Date(dFechaIni.getTime())));
            ps.setDate(6, (dFechaFin == null ? (java.sql.Date) null : new java.sql.Date(dFechaFin.getTime())));
            ps.setDouble(7, montoModificado);
            afectados = ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    public static String esConvenioModificatorioAutorizado(Connection conn, int nFolioOPConvHeader) throws Exception {
        String enCaptura = "N";
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT cdocumentohaplicado " + " FROM   tobrapublicaconvmodifencabezado " + "WHERE  nfolioopconvheader = ? ";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, nFolioOPConvHeader);
            rs = ps.executeQuery();
            if (rs.next())
                enCaptura = rs.getString("cdocumentohaplicado");
            return enCaptura;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static int setConvenioModificatorioEnCaptura(Connection conn, String convenioEnCaptura, int nFolioOPConvHeader) throws Exception {
        int afectados = 0;
        PreparedStatement ps = null;
        String query = "UPDATE tobrapublicaconvmodifencabezado " + "SET    cconvenioencaptura = ?  " + "WHERE  nfolioopconvheader = ? ";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, convenioEnCaptura);
            ps.setInt(2, nFolioOPConvHeader);
            afectados = ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    // IRD 20131121 RO-0009
    public static int setPagoPasivoEnCaptura(Connection conn, String convenioEnCaptura, int nFolioOPConvHeader) throws Exception {
        int afectados = 0;
        PreparedStatement ps = null;
        String query = "UPDATE tobrapublicaPagoPasivoencabezado " + "SET    cconvenioencaptura = ?  " + "WHERE  nfoliooppagpasheader = ? ";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, convenioEnCaptura);
            ps.setInt(2, nFolioOPConvHeader);
            afectados = ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    // MLR 20131220 RO-0010
    public static int setPlurianualEnCaptura(Connection conn, String convenioEnCaptura, int nFolioOPConvHeader) throws Exception {
        int afectados = 0;
        PreparedStatement ps = null;
        String query = "UPDATE tobrapublicaPlurianualencabezado " + "SET    cconvenioencaptura = ?  " + "WHERE  nfolioopplurianualheader = ? ";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, convenioEnCaptura);
            ps.setInt(2, nFolioOPConvHeader);
            afectados = ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    public static int copiaConvenioModificatorioEncabezado(Connection conn, int nFolioOPConvHeaderFrom) throws Exception {
        int nFolioOPConvHeaderResult = -1;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(QUERY_COPIA_CM_ENCABEZADO);
            ps.setInt(1, nFolioOPConvHeaderFrom);
            rs = ps.executeQuery();
            if (rs.next())
                nFolioOPConvHeaderResult = rs.getInt("cConsecutivo");
            return nFolioOPConvHeaderResult;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static int actualizaEncabezadoConvenioModificatorio(Connection conn, String folioConvenio, String fFechaIniContr, String fFechaFinContr, double mMonto, double nPorceIVA, double mMontoConIVA, String cDescripcionConvenio, double mMontoIncremento, double mMontoIncrementoIVA, int nFolioOPConvHeader) throws Exception {
        int afectados = 0;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(QUERY_ACTUALIZA_CM_ENCABEZADO);
            ps.setString(1, fFechaIniContr);
            ps.setString(2, fFechaFinContr);
            ps.setDouble(3, mMonto);
            ps.setDouble(4, nPorceIVA);
            ps.setDouble(5, mMontoConIVA);
            ps.setString(6, cDescripcionConvenio);
            ps.setDouble(7, mMontoIncremento);
            ps.setDouble(8, mMontoIncrementoIVA);
            ps.setString(9, folioConvenio);
            ps.setInt(10, nFolioOPConvHeader);
            afectados = ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    // IRD 20131121 RO-0009
    public static String actualizaEncabezadoPagoPasivo(Connection conn, double mPagoPasivo, double ivaPagPasF, double mTotalPagoPasivo, String cMotivoPagoPasivo, String nFolioOPPagPasHeader, String folioSAI, String fFechaAplicacion, String aEjercicioFiscal) throws Exception {
        String afectados = "0";
        PreparedStatement ps = null;
        try {
            if ("".equalsIgnoreCase(nFolioOPPagPasHeader)) {
                ps = conn.prepareStatement(QUERY_INSERTA_PAG_PAS_ENCABEZADO);
                ps.setDouble(1, mPagoPasivo);
                ps.setDouble(2, ivaPagPasF);
                ps.setDouble(3, mTotalPagoPasivo);
                ps.setString(4, fFechaAplicacion);
                ps.setString(5, aEjercicioFiscal);
                ps.setString(6, cMotivoPagoPasivo);
                ps.setString(7, folioSAI);
                // ps.setString(8, nFolioOPPagPasHeader);
                ResultSet rs = null;
                // afectados = ps.executeUpdate();
                rs = ps.executeQuery();
                if (rs.next())
                    afectados = Integer.toString(rs.getInt("cConsecutivo"));
            } else {
                ps = conn.prepareStatement(QUERY_ACTUALIZA_PAG_PAS_ENCABEZADO);
                ps.setDouble(1, mPagoPasivo);
                ps.setDouble(2, ivaPagPasF);
                ps.setDouble(3, mTotalPagoPasivo);
                ps.setString(4, cMotivoPagoPasivo);
                ps.setString(5, folioSAI);
                ps.setString(6, fFechaAplicacion);
                ps.setString(7, aEjercicioFiscal);
                ps.setString(8, nFolioOPPagPasHeader);
                /* afectados = */
                ps.executeUpdate();
                afectados = nFolioOPPagPasHeader;
            }
            return afectados;
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    // MLR 20131121 RO-0010
    public static String actualizaEncabezadoPlurianual(Connection conn, double mPlurianual, double ivaPlurianual, double mTotalPlurianual, String nFolioOPPlurianualHeader, String folioSAI, String fFechaAplicacion, String aEjercicioFiscal) throws Exception {
        String afectados = "0";
        PreparedStatement ps = null;
        try {
            if ("".equalsIgnoreCase(nFolioOPPlurianualHeader)) {
                ps = conn.prepareStatement(QUERY_INSERTA_PLURIANUAL_ENCABEZADO);
                ps.setDouble(1, mPlurianual);
                ps.setDouble(2, ivaPlurianual);
                ps.setDouble(3, mTotalPlurianual);
                ps.setString(4, fFechaAplicacion);
                ps.setString(5, aEjercicioFiscal);
                // ps.setString(6, cMotivoPagoPasivo);
                ps.setString(6, folioSAI);
                // ps.setString(8, nFolioOPPagPasHeader);
                ResultSet rs = null;
                // afectados = ps.executeUpdate();
                rs = ps.executeQuery();
                if (rs.next())
                    afectados = Integer.toString(rs.getInt("cConsecutivo"));
            } else {
                ps = conn.prepareStatement(QUERY_ACTUALIZA_PLURIANUAL_ENCABEZADO);
                ps.setDouble(1, mPlurianual);
                ps.setDouble(2, ivaPlurianual);
                ps.setDouble(3, mTotalPlurianual);
                // ps.setString(4, cMotivoPlurianual);
                ps.setString(4, folioSAI);
                ps.setString(5, fFechaAplicacion);
                ps.setString(6, aEjercicioFiscal);
                ps.setString(7, nFolioOPPlurianualHeader);
                /* afectados = */
                ps.executeUpdate();
                afectados = nFolioOPPlurianualHeader;
            }
            return afectados;
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    public static Map<String, String> loadCMInfoMap(Connection conn, int folioSAI) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT * FROM tObraPublicaConvModifEncabezado WHERE nFolioOPConvHeader = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, folioSAI);
            rs = ps.executeQuery();
            return RSToTable.rsToMap(rs);
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static int setConvenioModificatorioAplicado(Connection conn, int nFolioOPConvHeader) throws Exception {
        PreparedStatement ps = null;
        int afectados = 0;
        String query = "update tObraPublicaConvModifEncabezado set cDocumentoHaplicado = 'S', fAplicacion = GETDATE(), nFolioPoliza = 0 where nFolioOPConvHeader = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, nFolioOPConvHeader);
            afectados = ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    public static int registraEstimacionObra(Connection conn, EstimacionObra estimacion) throws Exception {
        String query = "INSERT INTO tObraPublicaAvanceFisico( FolioSAI ,cCveContrato,fEntregaVentanilla,noEstimacion,mMontoEstimacion," + "                                      eFiscalPago,nPorceAvanceFisicoEstimado,nPorceAvanceFisicoEjecutado," + "                                      nPorceAvanceFisicoProgramado,mmontoFisicoEjecutado,mmontoFisicoProgramado," + "                                      mesEstimado,fperiodoEstimacionIni,fperiodoEstimacionFin,mMontoEstimacionIva," + "                                      mMontoEstimacionMasIva,mMontoEstimacionAmortizado,mMontoEstimacionRetencion," + "                                      ultimaEstimacion,esCapitalizable)" + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = null;
        int insertados = 0;
        int esultimaEstimacion = 0;
        int esCapitalizable = 0;
        if (estimacion.getUltimaEstimacion())
            esultimaEstimacion = 1;
        if (estimacion.getEsCapitalizable())
            esCapitalizable = 1;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, estimacion.getFolioSAI());
            ps.setString(2, estimacion.getCveContrato());
            if (estimacion.getfEntregaVentanilla() == null)
                ps.setNull(3, Types.DATE);
            else
                ps.setDate(3, new java.sql.Date(estimacion.getfEntregaVentanilla().getTime()));
            ps.setInt(4, estimacion.getNoEstimacion());
            ps.setBigDecimal(5, estimacion.getMontoEstimacion());
            ps.setString(6, estimacion.geteFiscalPago());
            ps.setBigDecimal(7, estimacion.getPorceAvanceFisicoEstimado());
            ps.setBigDecimal(8, estimacion.getPorceAvanceFisicoEjecutado());
            ps.setBigDecimal(9, estimacion.getPorceAvanceFisicoProgramado());
            ps.setBigDecimal(10, estimacion.getMontoFisicoEjecutado());
            ps.setBigDecimal(11, estimacion.getMontoFisicoProgramado());
            ps.setInt(12, estimacion.getMesEstimado());
            ps.setDate(13, new java.sql.Date(estimacion.getfPeriodoEstimacionIni().getTime()));
            ps.setDate(14, new java.sql.Date(estimacion.getfPeriodoEstimacionFin().getTime()));
            ps.setBigDecimal(15, estimacion.getMontoEstimacionIva());
            ps.setBigDecimal(16, estimacion.getMontoEstimacionMasIva());
            ps.setBigDecimal(17, estimacion.getMontoEstimacionAmortizado());
            ps.setBigDecimal(18, estimacion.getMontoEstimacionRetencion());
            ps.setInt(19, esultimaEstimacion);
            ps.setInt(20, esCapitalizable);
            insertados = ps.executeUpdate();
            return insertados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int actualizaEstimacionInfoInmueble(Connection conn, EstimacionObra estimacion, int idRePublicWorkPartial) throws Exception {
        String queryUpdate = "UPDATE	tobrapublicaavancefisico " + "   SET	nidRePublicWorkPartial = ? " + " WHERE	foliosai = ? " + "   AND	noestimacion = ? ";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(queryUpdate);
            ps.setInt(1, idRePublicWorkPartial);
            ps.setString(2, estimacion.getFolioSAI());
            ps.setInt(3, estimacion.getNoEstimacion());
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int esFONDEN(Connection conn, EstimacionObra estimacion) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int esFonden = 0;
        String query = " SELECT cEsFonden FROM tObraPublicaCompromisoEncabezado WHERE FolioSAI = ? ";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, estimacion.getFolioSAI());
            rs = ps.executeQuery();
            if (rs.next())
                esFonden = rs.getInt("cEsFonden");
            return esFonden;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static int esFONDEN(Connection conn, String folioSAI) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int esFonden = 0;
        String query = " SELECT cEsFonden FROM tObraPublicaCompromisoEncabezado WHERE FolioSAI = ? ";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, folioSAI);
            rs = ps.executeQuery();
            if (rs.next())
                esFonden = rs.getInt("cEsFonden");
            return esFonden;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static String esObraEnProceso(Connection conn, String caNoContrarrecibo) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String folioSAI = "";
        StringBuffer query = new StringBuffer();
        query.append(" SELECT TOP 1 foliosai AS folioSAI ");
        query.append(" FROM tPAGOOBRAEncabezado ENC WITH (NOLOCK) ");
        query.append(" JOIN tPAGOOBRADetalle DET WITH (NOLOCK) ON ENC.nFolioPAGOOBRA = DET.nFolioPAGOOBRA ");
        query.append(" JOIN tobrapublicaavancefisico AF WITH (NOLOCK) ON ENC.cFolioContratoObra = ccvecontrato ");
        query.append(" 	AND ENC.cNoEstimacion = noestimacion ");
        query.append(" WHERE caNoContrarrecibo = ? ");
        query.append(" 	AND noestimacion <> 0 ");
        String sql = query.toString();
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, caNoContrarrecibo);
            rs = ps.executeQuery();
            if (rs.next())
                folioSAI = rs.getString("folioSAI");
            return folioSAI;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static int actualizaEstimacionInfoInmueble(Connection conn, String folioSAI, int estimationNo, int idRePublicWorkPartial) throws Exception {
        String queryUpdate = "UPDATE	tobrapublicaavancefisico " + "   SET	nidRePublicWorkPartial = ? " + " WHERE	foliosai = ? " + "   AND	noestimacion = ? ";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(queryUpdate);
            ps.setInt(1, idRePublicWorkPartial);
            ps.setString(2, folioSAI);
            ps.setInt(3, estimationNo);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static String calculaEvento(Connection conn, String folioSAI, String regimen, String lastAppraisalDate, String caNoContrarrecibo, int esCapitalizable) throws Exception {
        String Evento = "";
        CallableStatement cs = null;
        ResultSet rs = null;
        try {
            String query = "{call sp_evento_capitalizacion_obra ( ?, ?, ?, ?, ? )}";
            cs = conn.prepareCall(query);
            cs.setString(1, lastAppraisalDate);
            cs.setString(2, caNoContrarrecibo);
            cs.setString(3, folioSAI);
            cs.setString(4, regimen);
            cs.setInt(5, esCapitalizable);
            rs = cs.executeQuery();
            if (rs.next()) {
                Evento = rs.getString("cEvento");
            }
            return Evento;
        } finally {
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(rs, false);
        }
    }

    public static JSONObject generaPoliza(Connection conn, String folioSAI, String evento, String caNoContrarrecibo, int esCapitalizable) throws Exception {
        JSONObject jsonCapitalizacion = new JSONObject();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            AccountingEngine ae = new AccountingEngine();
            ae.setValidaInsuficienciaDeSaldo(true);
            int nFolioDocPoliza = insertaPoliza(conn, folioSAI, evento, caNoContrarrecibo, esCapitalizable);
            ae.makeAccountingApplicationWithoutEvent(conn, "DOCPOLIZA", String.valueOf(nFolioDocPoliza), "tdocpolizaencabezado", "tdocpolizadetalle", "nFolioDocPoliza");
            StringBuffer query = new StringBuffer();
            query.append("	SELECT compromisoObra.nidRePublicWork AS idRePublicWork,");
            query.append("		compromisoObra.nIDRealEstate AS idRealEstate, ");
            query.append("		efiscalpago,");
            query.append("		CASE WHEN esCapitalizable = 1 THEN 'capitalización_obra_publica' ELSE '' END AS tipo_solicitud,");
            query.append("		compromisoObra.cCentroContable, cU_UE,");
            query.append(" 	ENC.nFolioPoliza,");
            query.append(" 	DET.cPartida AS OBGT,");
            query.append(" 	partida.dPartida,");
            query.append(" 	mImporte AS amount,");
            query.append(" 	esCapitalizable");
            query.append("	FROM tObraPublicaCompromisoEncabezado compromisoObra WITH (NOLOCK)");
            query.append("	INNER JOIN tobrapublicaavancefisico estimacionObra WITH (NOLOCK)");
            query.append("		ON compromisoObra.FolioSAI = estimacionObra.foliosai");
            query.append(" LEFT JOIN tDocPolizaEncabezado ENC WITH (NOLOCK) ON cxp = ?");
            query.append(" LEFT JOIN tDocPolizaDetalle DET WITH (NOLOCK) ON ENC.nFolioDocPoliza = DET.nFolioDocPoliza AND nDocRenglon = 1");
            query.append(" INNER JOIN tCatalogoPartida partida WITH (NOLOCK) ON DET.cPartida = partida.cPartida");
            query.append(" WHERE	compromisoObra.FolioSAI = ?");
            query.append("   AND ultimaEstimacion = 1");
            String sql = query.toString();
            log.info("Object: {}", sql.toString());
            ps = conn.prepareStatement(sql);
            ps.setString(1, caNoContrarrecibo);
            ps.setString(2, folioSAI);
            rs = ps.executeQuery();
            if (rs.next()) {
                jsonCapitalizacion = new JSONObject();
                jsonCapitalizacion.put("idRePublicWork", rs.getString("idRePublicWork"));
                jsonCapitalizacion.put("idRealEstate", rs.getInt("idRealEstate"));
                jsonCapitalizacion.put("aEjercicioFiscal", rs.getInt("efiscalpago"));
                jsonCapitalizacion.put("tipo_solicitud", rs.getString("tipo_solicitud"));
                jsonCapitalizacion.put("centroContable", rs.getInt("cCentroContable"));
                jsonCapitalizacion.put("unidadEjecutora", rs.getString("cU_UE"));
                jsonCapitalizacion.put("poliza", rs.getInt("nFolioPoliza"));
                jsonCapitalizacion.put("partida", rs.getString("OBGT"));
                jsonCapitalizacion.put("dPartida", rs.getString("dPartida"));
                jsonCapitalizacion.put("evento", evento);
                jsonCapitalizacion.put("total", rs.getDouble("amount"));
                jsonCapitalizacion.put("capitalizable", rs.getInt("esCapitalizable") == 1 ? true : false);
            }
            return jsonCapitalizacion;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static int insertaPoliza(Connection conn, String folioSAI, String evento, String caNoContrarrecibo, int esCapitalizable) throws Exception {
        CallableStatement cmst = null;
        String query = "{call sp_inserta_poliza_obra( ?,?,?,?,? )}";
        int nfolioDocPoliza = -1;
        String UE = "";
        String CC = "";
        try {
            UE = folioSAI.substring(5, 8);
            CC = caNoContrarrecibo.substring(0, 2);
            Caso c = PolizaManager.instanciaCasoPoliza(UE, "ADMIN", "ADMIN", CC);
            nfolioDocPoliza = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
            cmst = conn.prepareCall(query);
            cmst.setInt(1, nfolioDocPoliza);
            cmst.setString(2, folioSAI);
            cmst.setString(3, evento);
            cmst.setString(4, caNoContrarrecibo);
            cmst.setInt(5, esCapitalizable);
            cmst.execute();
            return nfolioDocPoliza;
        } finally {
            CloseObject.closeObject(cmst);
        }
    }
}
