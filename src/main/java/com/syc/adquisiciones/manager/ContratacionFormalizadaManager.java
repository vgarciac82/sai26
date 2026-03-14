package com.syc.adquisiciones.manager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import com.axtel.contratos.ContractStatus;
import com.axtel.contratos.Procedimiento;
import com.axtel.contratos.ProcedimientoStatus;
import com.axtel.contratos.core.Contrato;
import com.axtel.contratos.core.ContratoAmpliacion;
import com.axtel.contratos.core.ContratoDiversoConvenio;
import com.axtel.contratos.core.ContratosConGarantia;
import com.axtel.contratos.core.ConvenioCap4;
import com.axtel.contratos.core.DatosContratoPSP;
import com.axtel.contratos.core.GarantiaContrato;
import com.axtel.contratos.core.LiberaGarantiaContrato;
import com.axtel.contratos.core.ProcedimientoAdjudicacion;
import com.syc.adquisiciones.ContratoArt25;
import com.syc.adquisiciones.DatosEP_TMP;
import com.syc.adquisiciones.core.ContratoModificado;
import com.syc.adquisiciones.core.DatosArchivo;
import com.syc.adquisiciones.core.PrecomMaterialesEncabezado;
import com.syc.adquisiciones.util.Util;
import com.syc.gestion.core.Usuario;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ContratacionFormalizadaManager {

    private static Logger log = LoggerFactory.getLogger(ContratacionFormalizadaManager.class);

    public boolean updateCategoriaProced(Connection conn, String cidproced, int nIdCategoriaProced) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("UPDATE mProcedimiento SET nIdCategoria=? WHERE cIdProcedimiento=?");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, nIdCategoriaProced);
            ps.setString(2, cidproced);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean updateFundamentoLegProced(Connection conn, String cidproced, int nIdfundamentoLeg) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("UPDATE mProcedimiento SET nIdFundamentoLeg=? WHERE cIdProcedimiento=?");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, nIdfundamentoLeg);
            ps.setString(2, cidproced);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean updateFundamentoLegProcedAdj(Connection conn, ProcedimientoAdjudicacion procedAdj) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("UPDATE mProcedimientoAdjudicacion SET nIdFundamentoLeg=? WHERE cIdProcedimiento=? and nIdconsecutivoAdj=? and cIdRFC=?");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, procedAdj.getnIdfundamentoLeg());
            ps.setString(2, procedAdj.getcIdProcedimiento());
            ps.setInt(3, procedAdj.getnIdconsecutivoAdj());
            ps.setString(4, procedAdj.getcIdRFC());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean updateEstateContrato(Connection conn, Contrato cont) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update  mContrato set nIdEstado=?,C_FOLIO=?,ConsecutivoCDIV=? where cIdContratoDefinitivo=? ");
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, cont.getnIdEstado());
            ps.setString(2, cont.getcFOLIO());
            ps.setInt(3, cont.getnConsecutivoCDIV());
            ps.setString(4, cont.getcIdContratoDefinitivo());
            success = ps.executeUpdate() > 0;
        } finally {
            if (query.length() > 1) {
                query.delete(0, query.length());
            }
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean updateFolioPrecomContrato(Connection conn, Contrato cont) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update  mContrato set C_FOLIO_PRE=?,ConsecutivoPRECOMP=?   where cIdContratoDefinitivo=? ");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cont.getC_FOLIO_PRE());
            ps.setInt(2, cont.getnConsecutivoPRECOMP());
            ps.setString(3, cont.getcIdContratoDefinitivo());
            success = ps.executeUpdate() > 0;
        } finally {
            if (query.length() > 1) {
                query.delete(0, query.length());
            }
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean updateContrato(Connection conn, Contrato cont) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update mContrato set esDescentralizado = ?, cConceptoContrato =?, ");
            query.append(" fInicio =CONVERT( date,?) ,");
            query.append("fFin =CONVERT( date,?) ,fFormalizacion =CONVERT( date,?),fSolicitud =CONVERT( date,?), ");
            query.append("fPropuestas =CONVERT( date,?),fEntrega =CONVERT( date,?),");
            query.append("cNoContratoCNET=LTRIM(RTRIM(? )),cOficioDG=?,cFolioMASCP=?,nCodExpedienteCNET='" + cont.getnCodExpedienteCNET() + "' ");
            query.append(",nCodContratoCNET='" + cont.getnCodContratoCNET() + "',cComentarioJustificaTipoProced=?,lJustificaTipoProced=?");
            query.append(" ,ITieneAnticipo =?,lEsPSP=?");
            query.append(" where cEjercicio = ? and cIdContratoDefinitivo = ?");
            log.info("Object: {}", query.toString());
            log.info("Parametros : ");
            log.info("Object: {}", "1, " + cont.getEsDescentralizado());
            log.info("Object: {}", " 2, " + cont.getcConceptoContrato());
            log.info("Object: {}", " 3, " + cont.getfInicio());
            log.info("Object: {}", " 4, " + cont.getfFin());
            log.info("Object: {}", " 5, " + cont.getfFormalizacion());
            log.info("Object: {}", " 6, " + cont.getfSolicitud());
            log.info("Object: {}", " 7, " + cont.getfPropuestas());
            log.info("Object: {}", " 8, " + cont.getfEntrega());
            log.info("Object: {}", " 9, " + cont.getcNoContratoCNET());
            log.info("Object: {}", " 10, " + cont.getcOficioDG());
            log.info("Object: {}", " 11, " + cont.getcFolioMASCP());
            log.info("Object: {}", " 12, " + cont.getcComentarioJustificaTipoProced());
            log.info("Object: {}", " 13, " + cont.getlJustificaTipoProced());
            log.info("Object: {}", " 14, " + cont.getITieneAnticipo());
            log.info("Object: {}", " 15, " + (cont.isnEsContratacionPSP() == true ? 1 : 0));
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, cont.getEsDescentralizado());
            ps.setString(2, cont.getcConceptoContrato());
            ps.setString(3, cont.getfInicio());
            ps.setString(4, cont.getfFin());
            ps.setString(5, cont.getfFormalizacion());
            ps.setString(6, cont.getfSolicitud());
            ps.setString(7, cont.getfPropuestas());
            ps.setString(8, cont.getfEntrega());
            ps.setString(9, cont.getcNoContratoCNET());
            ps.setString(10, cont.getcOficioDG());
            ps.setString(11, cont.getcFolioMASCP());
            ps.setString(12, cont.getcComentarioJustificaTipoProced());
            ps.setInt(13, cont.getlJustificaTipoProced());
            ps.setInt(14, cont.getITieneAnticipo());
            ps.setInt(15, (cont.isnEsContratacionPSP() == true ? 1 : 0));
            ps.setString(16, cont.getcEjercicio());
            ps.setString(17, cont.getcIdContratoDefinitivo());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean updateContratoDiverso(Connection conn, Contrato cont) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update  pContratoDiverso  set fAdjudicacion=CONVERT( date,?), fContratoIni =CONVERT( date,?), fContratoFin =CONVERT( date,?)");
            query.append(" ,fFirmaContrato=CONVERT( date,?),bTieneAnticipo=? where cEjercicio=? and cIdUnidadAdministrativa=? and cIdContrato=?");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cont.getfFormalizacion());
            ps.setString(2, cont.getfInicio());
            ps.setString(3, cont.getfFin());
            ps.setString(4, cont.getfFormalizacion());
            ps.setInt(5, cont.getITieneAnticipo());
            ps.setString(6, cont.getcEjercicio());
            ps.setString(7, cont.getcIdUnidadEjecutora());
            ps.setString(8, cont.getcIdContratoDefinitivo());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean updateDocumentacionContrato(Connection conn, Contrato cont) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update mDocumentacionContrato set mTotalGarantias=?, cMecanismosVigilancia=?,mGarantiaAnticipo=?,mGarantiaCumplimiento=?,lExcentaGarantia=? WHERE cIdContrato=?");
            query.append("");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setDouble(1, cont.getmTotalGarantia());
            ps.setString(2, cont.getcMecanismosVigilancia());
            ps.setDouble(3, cont.getmGarantiaAnticipo());
            ps.setDouble(4, cont.getmGarantiaCumplimiento());
            ps.setInt(5, cont.getlExcentaGarantia());
            ps.setString(6, cont.getcIdContratoDefinitivo());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean deleteImpuestosAdicionales(Connection conn, Contrato cont) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "DELETE FROM mImpuestosAdicionales WHERE cIdTipoDocumento= ? AND cIdUnidadEjecutora=? AND nIdConsecutivo=?";
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, cont.getcIdtipoContrato());
            ps.setString(2, cont.getcIdUnidadEjecutora());
            ps.setInt(3, cont.getnIdConsecutivo());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean addImpuestosAdicionales(Connection conn, Contrato contrato, int nConsecutivo) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "INSERT INTO mImpuestosAdicionales (cIdTipoDocumento,cIdUnidadEjecutora,nIdConsecutivo,cImpuesto,cDescripcion,cPorcentaje) VALUES(?,?,?,?,?,?)";
            ps = conn.prepareStatement(query);
            ps.setString(1, contrato.getcIdtipoContrato());
            ps.setString(2, contrato.getcIdUnidadEjecutora());
            ps.setInt(3, contrato.getnIdConsecutivo());
            ps.setInt(4, nConsecutivo);
            ps.setString(5, "Descripcion del impuesto");
            ps.setDouble(6, 0);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean deleteOtrosImpuestos(Connection conn, Contrato cont) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "DELETE FROM mOtrosImpuestos WHERE cIdTipoDocumento= ? AND cIdUnidadEjecutora=? AND nIdConsecutivo=?";
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, cont.getcIdtipoContrato());
            ps.setString(2, cont.getcIdUnidadEjecutora());
            ps.setInt(3, cont.getnIdConsecutivo());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean addOtrosImpuestos(Connection conn, Contrato contrato) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            for (int i = 0; i < contrato.getOtrosImp().size(); i++) {
                if (!"".equalsIgnoreCase(contrato.getOtrosImp().get(i).getcImpuesto()) && contrato.getOtrosImp().get(i).getmMonto() > 0) {
                    query = "INSERT INTO mOtrosImpuestos (cIdTipoDocumento,cIdUnidadEjecutora,nIdConsecutivo,cImpuesto,cDescripcion,monto) VALUES(?,?,?,?,?,?)";
                    ps = conn.prepareStatement(query);
                    ps.setString(1, contrato.getcIdtipoContrato());
                    ps.setString(2, contrato.getcIdUnidadEjecutora());
                    ps.setInt(3, contrato.getnIdConsecutivo());
                    ps.setInt(4, contrato.getOtrosImp().get(i).getnId());
                    ps.setString(5, contrato.getOtrosImp().get(i).getcImpuesto());
                    ps.setDouble(6, contrato.getOtrosImp().get(i).getmMonto());
                    success = ps.executeUpdate() > 0;
                }
            }
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean deleteDatosPSP(Connection conn, String cIdcontratoDef) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "delete mDatosContratoPSP where cIdcontratoDefinitivo=?";
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, cIdcontratoDef);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean deletePrestaciondelServicio(Connection conn, String cIdcontratoDef) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "delete mContratoServicioPrestado where cIdcontratoDefinitivo=?";
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, cIdcontratoDef);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean updateEsPSP(Connection conn, String cIdcontratoDef, int esPSP) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "update mcontrato set lEsPSP=" + esPSP + " where cIdContratoDefinitivo='" + cIdcontratoDef + "'";
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean addDatosPSP(Connection conn, DatosContratoPSP datPSP) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "insert into mDatosContratoPSP (cIdcontratoDefinitivo,cAreaRequirente,cAreaResponsable,nCentroTrabajo,lEsMaestro,mMontoMensual,cDenominacionProyecto) values(?,?,?,?,?,?,?)";
            ps = conn.prepareStatement(query);
            ps.setString(1, datPSP.getcIdcontratoDefinitivo());
            ps.setString(2, datPSP.getcAreaRequirente());
            ps.setString(3, datPSP.getcAreaResponsable());
            ps.setInt(4, datPSP.getnCentroTrabajo());
            ps.setInt(5, (datPSP.islEsMaestro() == true ? 1 : 0));
            ps.setDouble(6, datPSP.getmMontoMensual());
            ps.setString(7, datPSP.getcDenominacionProyecto());
            log.info("Object: {}", query.toString());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean existePrecom(Connection conn, Contrato contrato) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select * from tPreCompromisoEncabezado with(nolock) where (cIdContrato=? or cIdContrato=?+'-'+?+'-'+cast(? as varchar)) and cDocumentoHaplicado = 'S'";
        boolean resp = false;
        try {
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, contrato.getcIdContratoDefinitivo());
            ps.setString(2, contrato.getcIdtipoContrato());
            ps.setString(3, contrato.getcIdUnidadEjecutora());
            ps.setInt(4, contrato.getnIdConsecutivo());
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return resp;
    }

    public boolean existeCompromiso(Connection conn, Contrato contrato) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select *from tCompromisoEncabezado with(Nolock) where (cDocumentoHaplicado='S' or cDocumentoHaplicado is null) and cIdContrato=?";
        boolean resp = false;
        try {
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, contrato.getcIdContratoDefinitivo());
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return resp;
    }

    public DatosContratoPSP queryDatosPSP(Connection conn, String cIdContratoDef) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select *from mDatosContratoPSP with(Nolock) where cIdcontratoDefinitivo=?";
        DatosContratoPSP dat = new DatosContratoPSP();
        try {
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, cIdContratoDef);
            rs = ps.executeQuery();
            if (rs.next()) {
                dat.setcAreaRequirente(rs.getString("cAreaRequirente"));
                dat.setcAreaResponsable(rs.getString("cAreaResponsable"));
                dat.setcIdcontratoDefinitivo(cIdContratoDef);
                dat.setnCentroTrabajo(rs.getInt("nCentroTrabajo"));
                dat.setlEsMaestro(rs.getBoolean("lEsMaestro"));
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return dat;
    }

    public boolean updateEstatusContratoRemanente(Connection conn, Contrato contrato) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update  mContratoRemanenteEjercicioAnterior set nEstado=? where cIdContratoDefinitivo=?");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, contrato.getnIdEstado());
            ps.setString(2, contrato.getcIdContratoDefinitivo());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean updateFolioCasoContratoRemanente(Connection conn, Contrato contrato) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update  mContratoRemanenteEjercicioAnterior set C_FOLIO=?,ConsecutivoCDIV=? where cIdContratoDefinitivo=?");
            log.info("Object: {}", query.toString());
            log.info("Object: {}", "C_FOLIO : " + contrato.getcFOLIO());
            log.info("Object: {}", "ConsecutivoCDIV : " + contrato.getnConsecutivoCDIV());
            log.info("Object: {}", "cIdContratoDefinitivo : " + contrato.getcIdContratoDefinitivo());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, contrato.getcFOLIO());
            ps.setInt(2, contrato.getnConsecutivoCDIV());
            ps.setString(3, contrato.getcIdContratoDefinitivo());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean updateFolioCasoContratoDiverso(Connection conn, Contrato contrato) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update pContratoDiverso set id_caso=? where cIdContrato=?");
            log.info("Object: {}", query.toString());
            log.info("Object: {}", "id_caso : " + contrato.getnConsecutivoCDIV());
            log.info("Object: {}", "cIdContratoDefinitivo : " + contrato.getcIdContratoDefinitivo());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, contrato.getnConsecutivoCDIV());
            ps.setString(2, contrato.getcIdContratoDefinitivo());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean deleteFechasContrato(Connection conn, String cIdprocedimiento, int nIdFechaIni, int nIdFechaFin) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "delete from mProcedimientoFechas where  nIdProcedimiento=? and nIdFecha in (12,?,?,13)";
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, cIdprocedimiento);
            ps.setInt(2, nIdFechaIni);
            ps.setInt(3, nIdFechaFin);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean addFechasProcedimiento(Connection conn, String cIdprocedimiento, int nIdFecha, String cFecha) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "insert into mProcedimientoFechas values (?,?,CONVERT(DATE,?,103))";
            ps = conn.prepareStatement(query);
            log.info("Object: {}", query.toString());
            ps.setInt(1, nIdFecha);
            ps.setString(2, cIdprocedimiento);
            ps.setString(3, cFecha);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean addProcedimientoAdjudicacion(Connection conn, Procedimiento proced) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" INSERT INTO mProcedimientoAdjudicacion ( cEjercicio ,cIdTipoProcedimiento ,cIdUnidadEjecutora  ");
            query.append(" ,nIdConsecutivo ,cIdRFC ,cIdTipoConsolidado ,nIdConsecutivoConsolidado ,nIdTipoCambio  ");
            query.append(" ,mTipoCambio ,lContratoAbierto ,nPorcentajeIVA, mMontoTotalPlurianual, nServicio_A_Bienes, cNumProcedimientoCNET )  ");
            query.append(" VALUES(? ,? ,? , ? ,? ,?  , ? ,? , ? , ? , ?  ,?,?,?)  ");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, proced.getcEjercicio());
            ps.setString(2, proced.getcIdTipoProcedimiento());
            ps.setString(3, proced.getcIdUnidadEjecutora());
            ps.setInt(4, proced.getnIdConsecutivo());
            //validar que esté bien el rfc
            ps.setString(5, proced.getcIdRFC());
            ps.setString(6, proced.getcIdTipoConsolidado());
            ps.setInt(7, proced.getnIdConsecutivoConsolidado());
            ps.setString(8, "01");
            ps.setDouble(9, 1.0);
            ps.setInt(10, proced.getnEsContratoAbierto());
            ps.setInt(11, proced.getnPorcentajeIVA());
            ps.setDouble(12, proced.getmTotalPlurianual());
            ps.setInt(13, 0);
            ps.setString(14, proced.getcNoProcedCNET());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
        }
        return success;
    }

    public boolean addProcedimientoAdjudicacionPartidas(Connection conn, Procedimiento proced) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("  INSERT INTO mProcedimientoAdjudicacionPartidas ( ");
            query.append("     cEjercicio ,cIdTipoProcedimiento ,cIdUnidadEjecutora , ");
            query.append("     nIdConsecutivo ,cIdRFC ,cIdTipoConsolidado,  ");
            query.append("     nIdConsecutivoConsolidado ,nIdLineaConsolidado , ");
            query.append("     mMontoMinimo ,mMontoMaximo ,cDescripcion,nIdEstadoPartida, ");
            query.append("     cDescripcionAdicional,nPocentajeIVA,mMontoNetoLinea, ");
            query.append("     mMontoNetoLineaMax,nCantidadLineaMax,mMontoNetoMinimo, ");
            query.append("     mMontoNetoPluri,nIdconsecutivoAdj ) ");
            query.append(" select proced.cEjercicio ,proced.cIdTipoProcedimiento , ");
            query.append("     proced.cIdUnidadEjecutora ,proced.nIdConsecutivo , ");
            query.append("     adj.cIdRFC ,adj.cIdTipoConsolidado , adj.nIdConsecutivoConsolidado , ");
            query.append("     lineas.nIdLineaConsolidado , lineas.mPrecioUnitario  ");
            query.append(" 	,lineas.mPrecioUnitario , lineas.cDescripcion, ");
            query.append("     1, lineas.cDescripcionAdicional,lineas.nPorcentajeIVA, ");
            query.append("     lineas.mMontoNeto,case when nIdLineaConsolidado=1 then ? else ? end mMontoNetoLineaMax, ");
            query.append("     lineas.nCantidad, case when nIdLineaConsolidado=1 then ? else ? end mMontoNetoMinimo,  ");
            query.append(" 	case when nIdLineaConsolidado=1 then ? else ? end mMontoNetoPluri,adj.nIdconsecutivoAdj ");
            query.append(" from mProcedimiento as proced with(Nolock) ");
            query.append(" inner join mProcedimientoAdjudicacion as adj with(Nolock) ");
            query.append(" on proced.cIdProcedimiento=adj.cIdProcedimiento ");
            query.append(" and proced.cEjercicio=adj.cEjercicio ");
            query.append(" and proced.cIdTipoProcedimiento=adj.cIdTipoProcedimiento ");
            query.append(" and proced.cIdUnidadEjecutora=adj.cIdUnidadEjecutora ");
            query.append(" and proced.nIdConsecutivo=adj.nIdConsecutivo ");
            query.append(" inner join( ");
            query.append(" 	select  ");
            query.append(" 	consol.cIdConsolidado,consol.nIdLineaConsolidado ");
            query.append(" 	,linSol.cIdCABM,linSol.cIdSubPartida ");
            query.append(" 	,linSol.nCantidad,linSol.nPorcentajeIVA ");
            query.append(" 	,linSol.mPrecioUnitario,linSol.cDescripcionAdicional,linSol.cDescripcion ");
            query.append(" 	,linSol.mMontoNeto ");
            query.append(" 	from mConsolidadoSolicitud consol with(Nolock) ");
            query.append(" 	inner join mSolicitudLineas as linSol with(Nolock) ");
            query.append(" 	on consol.cIdSolicitud=linSol.cIdSolicitud ");
            query.append(" 	and consol.nIdLineaSolicitud=linSol.nIdLineaSolicitud ");
            query.append(" 	where consol.cIdConsolidado=? ");
            query.append(" )lineas on proced.cIdConsolidado=lineas.cIdConsolidado ");
            query.append(" where proced.cIdConsolidado=? ");
            ps = conn.prepareStatement(query.toString());
            ps.setDouble(1, proced.getmTotalMaximoHonorarios());
            ps.setDouble(2, proced.getmTotalMaximoGastosTraslado());
            ps.setDouble(3, proced.getmTotalMinimoHonorarios());
            ps.setDouble(4, proced.getmTotalMinimoGastosTraslado());
            ps.setDouble(5, proced.getmTotalPlurianualHonorarios());
            ps.setDouble(6, proced.getmTotalPlurianualGastosTraslado());
            ps.setString(7, proced.getcIdConsolidado());
            ps.setString(8, proced.getcIdConsolidado());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
        }
        return success;
    }

    public boolean existeDocumentacionHiperv(Connection conn, String cIdContratoDef) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT * FROM mDocumentacionContrato with (nolock) WHERE cIdContrato=?";
        boolean resp = false;
        try {
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, cIdContratoDef);
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return resp;
    }

    public boolean existenDatosPrestacionServicio(Connection conn, String cIdContratoDef) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT * FROM mContratoServicioPrestado with (nolock) WHERE cIdcontratoDefinitivo=?";
        boolean resp = false;
        try {
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, cIdContratoDef);
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return resp;
    }

    public boolean addDocumentacionHiperv(Connection conn, Contrato cont) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "INSERT INTO mDocumentacionContrato (cMecanismosVigilancia,mTotalGarantias,cIdContrato,mGarantiaAnticipo,mGarantiaCumplimiento,lExcentaGarantia)values(?,?,?,?,?,?)";
            ps = conn.prepareStatement(query);
            ps.setString(1, cont.getcMecanismosVigilancia());
            ps.setDouble(2, cont.getmTotalGarantia());
            ps.setString(3, cont.getcIdContratoDefinitivo());
            ps.setDouble(4, cont.getmGarantiaAnticipo());
            ps.setDouble(5, cont.getmGarantiaCumplimiento());
            ps.setInt(6, cont.getlExcentaGarantia());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean deleteContratoConGarantia(Connection conn, String cIdContratoDef) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "delete from mContratosConGarantia where  cIdContratoDefinitivo=? ";
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, cIdContratoDef);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean addContratoConGarantia(Connection conn, String cIdContratoDef) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" insert into mContratosConGarantia (cEjercicioFiscal,cIdUnidadEjecutora,cIdContratoDefinitivo ");
            query.append(" ,cIdRFC,cConcepto,fFormalizacion,fInicio,fFin,cFolioCaso,cNumContratoCNET,cNumProcedimientoCNET ");
            query.append(" ,cCodigoContratoCNET,cCodigoExpedienteCNET,mTotalGarantias,mTotalContrato) ");
            query.append(" select  ");
            query.append(" cont.cEjercicio ");
            query.append(" ,cont.cIdUnidadEjecutora ");
            query.append(" ,cont.cIdContratoDefinitivo ");
            query.append(" ,cont.cIdRFC ");
            query.append(" ,cont.cConceptoContrato ");
            query.append(" ,convert(date,cont.fFormalizacion)fFormalizacion ");
            query.append(" ,convert(date,cont.fInicio)fInicio ");
            query.append(" ,convert(date,cont.fFin)fFin ");
            query.append(" ,cont.C_FOLIO folioCaso ");
            query.append(" ,cont.cNoContratoCNET ");
            query.append(" ,pro.numProcedimientoCNET ");
            query.append(" ,cont.nCodContratoCNET ");
            query.append(" ,cont.nCodExpedienteCNET ");
            query.append(" ,garant.mTotalGarantias ");
            query.append(" ,pro.montoTotalContrato ");
            query.append(" from mContrato as cont with(Nolock) ");
            query.append(" inner join mDocumentacionContrato as garant with(Nolock) ");
            query.append(" on cont.cIdContratoDefinitivo=garant.cIdContrato ");
            query.append(" inner join mCatalogoProveedor as prov with(Nolock) ");
            query.append(" on prov.cIdRFC=cont.cIdRFC ");
            query.append(" inner join( ");
            query.append(" 	select  ");
            query.append(" 	pro.cIdProcedimiento,adj.cIdRFC,adj.nIdconsecutivoAdj,pro.cOficio numProcedimientoCNET ");
            query.append(" 	,case when pro.isPlurianual=1 then sum(part.mMontoNetoPluri) else  ");
            query.append(" 	case when adj.lContratoAbierto=1 then sum(part.mMontoNetoLineaMax) else sum(part.mMontoNetoMinimo) end  ");
            query.append(" 	end montoTotalContrato ");
            query.append(" 	from mProcedimiento as pro with(nolock) ");
            query.append(" 	inner join mProcedimientoAdjudicacion as adj with(Nolock) ");
            query.append(" 	on pro.cIdProcedimiento=adj.cIdProcedimiento ");
            query.append(" 	inner join mProcedimientoAdjudicacionPartidas as part with(Nolock) ");
            query.append(" 	on part.cIdProcedimiento=adj.cIdProcedimiento ");
            query.append(" 	and adj.cIdRFC=adj.cIdRFC ");
            query.append(" 	and part.nIdconsecutivoAdj=adj.nIdconsecutivoAdj ");
            query.append(" 	group by pro.cIdProcedimiento,adj.cIdRFC ");
            query.append(" 	,adj.nIdconsecutivoAdj,pro.cOficio ");
            query.append(" 	,pro.isPlurianual,adj.lContratoAbierto ");
            query.append(" )pro on pro.cIdProcedimiento=cont.cIdProcedimiento ");
            query.append(" and pro.cIdRFC=cont.cIdRFC ");
            query.append(" and pro.nIdconsecutivoAdj=cont.nIdconsecutivoAdj ");
            query.append(" where  garant.mTotalGarantias>0 ");
            query.append(" and cont.cIdContratoDefinitivo=? ");
            query.append(" and cont.cNoContratoCNET not in(select cNumContratoCNET from mContratosConGarantia with(Nolock)) ");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cIdContratoDef);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean addNewConvCap4(Connection conn, ConvenioCap4 conv) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = new StringBuilder();
        ResultSet rs = null;
        try {
            query.append("insert into mContratoModificadoCap4 (cIdContratoDefinitivo,cIdUnidadEjecutora,cEjercicio,nConsecutivoModificacion,mTotalAnterior,mTotalNuevo ");
            query.append(",nEstatus,fCapturaMod,nTipoModificacion,lEsTotalPluri,lEsConvEjercicioAnt,nIdCategoria,nIdFundamentoLegal,cIdRFC,cNumProcedCNET,nCodExpedienteCNET ");
            query.append(",nCodContratoCNET,cUsuarioCaptura,cFolio,nConsecutivoCDIV) values(?,?,?,?,?, ?,?,convert(date,getDate()),?,?, ?,?,?,?,?,?,?,?,?,?)");
            ps = conn.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, conv.getcIdContratoDefinitivo());
            ps.setString(2, conv.getcIdUnidadEjecutora());
            ps.setString(3, conv.getcEjercicio());
            ps.setInt(4, conv.getnConsecutivoModificacion());
            ps.setDouble(5, conv.getmTotalAnterior());
            ps.setDouble(6, conv.getmTotalAnterior());
            ps.setInt(7, conv.getnEstatus());
            ps.setInt(8, conv.getnTipoModificacion());
            ps.setInt(9, conv.getlEsTotalPluri());
            ps.setInt(10, conv.getlEsConvEjercicioAnt());
            ps.setInt(11, conv.getnIdCategoria());
            ps.setInt(12, conv.getnIdFundamentoLegal());
            ps.setString(13, conv.getcIdRFC());
            ps.setString(14, conv.getcNumProcedCNET());
            ps.setString(15, conv.getnCodExpedienteCNET());
            ps.setString(16, conv.getnCodContratoCNET());
            ps.setString(17, conv.getcUsuarioCaptura());
            ps.setString(18, conv.getcFolio());
            ps.setInt(19, conv.getnConsecutivoCDIV());
            if (conv.getlEsConvEjercicioAnt() == 1) {
                ps.setString(18, null);
                ps.setInt(19, -1);
            }
            success = ps.executeUpdate() > 0;
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                conv.setnIdContModCap4(rs.getInt(1));
            }
            return success;
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    public boolean addPartidasNewConvCap4(Connection conn, ConvenioCap4 conv) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = new StringBuilder();
        String cNameDB = "";
        try {
            if (conv.getlEsConvEjercicioAnt() == 1) {
                cNameDB = Util.getNameDB(conn, Integer.parseInt(conv.getcEjercicio()) - 1);
                query.append(" insert into mContratoModificadoCap4Partida (nIdContModCap4,nIdLineaConsolidado,cIdSubPartida,cIdCABM ");
                query.append(" ,cIdUnidadMedida,cDescripcionAdicional,nIdIVA,nCantidad,mPrecioUnitario,mMontoNeto,nCantidadOriginal,mMontoNetoOriginal) ");
                query.append(" select   ");
                query.append(" ?,part.nIdPartida,part.cIdSubPartida,part.cIdCABM ");
                query.append(" ,part.cIdUnidadMedida,part.cDescripcionAdicional,part.nIdIVA ");
                query.append(" ,case when part.cIdUnidadMedida='SRV' then part.nCantidadMin else 0 end cantidad ");
                query.append(" ,case when part.cIdUnidadMedida='SRV' then 0  else part.mPrecioUnitario end precio ");
                query.append(" ,0 montoneto ");
                query.append(" ,part.nCantidadMax ");
                if (conv.getlEsTotalPluri() == 1) {
                    query.append(" ,part.mMontoNetoTotalPluri ");
                } else {
                    query.append(" ,case when cont.nEsAbierto=1 then part.mMontoNetoMaximo else part.mMontoNetoLinea end montoNetoOriginal");
                }
                query.append(" from " + cNameDB + "..mContratoCap4 cont with(Nolock)   ");
                query.append(" inner join " + cNameDB + "..mContratoCap4Partidas part with(Nolock) ");
                query.append(" on part.cIdContratoDefinitivo=cont.cIdContratoDefinitivo ");
                query.append(" where cont.nIdEstado=4 and cont.cIdContratoDefinitivo=? ");
            } else {
                query.append(" insert into mContratoModificadoCap4Partida (nIdContModCap4,nIdLineaConsolidado,cIdSubPartida,cIdCABM ");
                query.append(" ,cIdUnidadMedida,cDescripcionAdicional,nIdIVA,nCantidad,mPrecioUnitario,mMontoNeto,nCantidadOriginal,mMontoNetoOriginal) ");
                query.append(" select   ");
                query.append(" ?,part.nIdPartida,part.cIdSubPartida,part.cIdCABM ");
                query.append(" ,part.cIdUnidadMedida,part.cDescripcionAdicional,part.nIdIVA ");
                query.append(" ,case when part.cIdUnidadMedida='SRV' then part.nCantidadMin else 0 end cantidad ");
                query.append(" ,case when part.cIdUnidadMedida='SRV' then 0  else part.mPrecioUnitario end precio ");
                query.append(" ,0 montoneto ");
                query.append(" ,part.nCantidadMax ");
                if (conv.getlEsTotalPluri() == 1) {
                    query.append(" ,part.mMontoNetoTotalPluri ");
                } else {
                    query.append(" ,case when cont.nEsAbierto=1 then part.mMontoNetoMaximo else part.mMontoNetoLinea end montoNetoOriginal");
                }
                query.append(" from mContratoCap4 cont with(Nolock)   ");
                query.append(" inner join mContratoCap4Partidas part with(Nolock) ");
                query.append(" on part.cIdContratoDefinitivo=cont.cIdContratoDefinitivo ");
                query.append(" where cont.nIdEstado=4 and cont.cIdContratoDefinitivo=? ");
            }
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, conv.getnIdContModCap4());
            ps.setString(2, conv.getcIdContratoDefinitivo());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean existConvenioCap4SinAutorizar(Connection conn, String cIdContratoDef) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select *from mContratoModificadoCap4 with(Nolock) where cIdContratoDefinitivo=? and nEstatus<4";
        boolean resp = false;
        try {
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, cIdContratoDef);
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return resp;
    }

    public boolean existPcontratoDiverso(Connection conn, ConvenioCap4 conv) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select *from pContratoDiverso with(Nolock) where cIdContrato=? ";
        boolean resp = false;
        try {
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, conv.getcIdContratoDefinitivo());
            rs = ps.executeQuery();
            if (rs.next()) {
                conv.setnConsecutivoCDIV(rs.getInt("id_caso"));
                resp = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return resp;
    }

    public boolean searchContractCap4(Connection conn, ConvenioCap4 conv) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        boolean resp = false;
        String cNameDB = "";
        try {
            query = new StringBuilder();
            query.append("select  ");
            query.append(" cont.cIdContratoDefinitivo,cIdUnidadEjecutora ");
            query.append(" ,case when cont.nEsAbierto=1 then part.mMontoNetoMaximo else mMontoNetoMinimo end mTotalAnterior ");
            query.append(" ,nIdCategoria,nIdFundamentoLeg,cIdRFC, cNumProcedCNET,nCodExpedienteCNET,nCodContratoCNET ");
            query.append(" ,part.mMontoNetoTotalPluri,cont.C_FOLIO,cont.ConsecutivoCDIV ");
            query.append(",0 esConvEjercicioAnt");
            query.append(" from mContratoCap4 cont with(Nolock)  ");
            query.append(" inner join( ");
            query.append(" 	select  ");
            query.append(" 	cIdContratoDefinitivo ");
            query.append(" 	,round(sum(nCantidadMin*mPrecioUnitario),2)mMontoSinIVAMinimo ");
            query.append(" 	,sum(case when mMontoNetoMinimo>0 then mMontoNetoMinimo  else mMontoNetoLinea end )mMontoNetoMinimo ");
            query.append(" 	,sum(mMontoNetoMaximo)mMontoNetoMaximo ");
            query.append(" 	,sum(mMontoNetoTotalPluri)mMontoNetoTotalPluri ");
            query.append(" 	from mContratoCap4Partidas with(Nolock) ");
            query.append(" 	group by cIdContratoDefinitivo ");
            query.append(" )part on part.cIdContratoDefinitivo=cont.cIdContratoDefinitivo ");
            query.append(" where cont.cIdContratoDefinitivo=?");
            //Para contratos del ejercicio anterior
            cNameDB = Util.getNameDB(conn, Integer.parseInt(conv.getcEjercicio()) - 1);
            query.append(" union select  ");
            query.append(" cont.cIdContratoDefinitivo,cIdUnidadEjecutora ");
            query.append(" ,case when cont.nEsAbierto=1 then part.mMontoNetoMaximo else mMontoNetoMinimo end mTotalAnterior ");
            query.append(" ,nIdCategoria,nIdFundamentoLeg,cIdRFC, cNumProcedCNET,nCodExpedienteCNET,nCodContratoCNET ");
            query.append(" ,part.mMontoNetoTotalPluri,cont.C_FOLIO,cont.ConsecutivoCDIV ");
            query.append(",1 esConvEjercicioAnt");
            query.append(" from " + cNameDB + "..mContratoCap4 cont with(Nolock)  ");
            query.append(" inner join( ");
            query.append(" 	select  ");
            query.append(" 	cIdContratoDefinitivo ");
            query.append(" 	,round(sum(nCantidadMin*mPrecioUnitario),2)mMontoSinIVAMinimo ");
            query.append(" 	,sum(case when mMontoNetoMinimo>0 then mMontoNetoMinimo  else mMontoNetoLinea end )mMontoNetoMinimo ");
            query.append(" 	,sum(mMontoNetoMaximo)mMontoNetoMaximo ");
            query.append(" 	,sum(mMontoNetoTotalPluri)mMontoNetoTotalPluri ");
            query.append(" 	from " + cNameDB + "..mContratoCap4Partidas with(Nolock) ");
            query.append(" 	group by cIdContratoDefinitivo ");
            query.append(" )part on part.cIdContratoDefinitivo=cont.cIdContratoDefinitivo ");
            query.append(" where cont.cIdContratoDefinitivo=?");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, conv.getcIdContratoDefinitivo());
            ps.setString(2, conv.getcIdContratoDefinitivo());
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
                conv.setcIdUnidadEjecutora(rs.getString("cIdUnidadEjecutora"));
                conv.setmTotalAnterior(rs.getDouble("mTotalAnterior"));
                if (conv.getlEsTotalPluri() == 1) {
                    conv.setmTotalAnterior(rs.getDouble("mMontoNetoTotalPluri"));
                }
                conv.setnEstatus(1);
                conv.setlEsConvEjercicioAnt(rs.getInt("esConvEjercicioAnt"));
                conv.setnIdCategoria(rs.getInt("nIdCategoria"));
                conv.setnIdFundamentoLegal(rs.getInt("nIdFundamentoLeg"));
                conv.setcIdRFC(rs.getString("cIdRFC"));
                conv.setcNumProcedCNET(rs.getString("cNumProcedCNET"));
                conv.setnCodExpedienteCNET(rs.getString("nCodExpedienteCNET"));
                conv.setnCodContratoCNET(rs.getString("nCodContratoCNET"));
                conv.setcFolio(rs.getString("C_FOLIO"));
                conv.setnConsecutivoCDIV(rs.getInt("ConsecutivoCDIV"));
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return resp;
    }

    public void getnConsecutiveModificationConvCap4(Connection conn, ConvenioCap4 conv) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select isnull(MAX(nConsecutivoModificacion),0)nConsecutivoModificacion from mContratoModificadoCap4 with(Nolock) where cIdContratoDefinitivo=? and nEstatus=4";
        int nConsecutivo = 0;
        try {
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, conv.getcIdContratoDefinitivo());
            rs = ps.executeQuery();
            if (rs.next()) {
                nConsecutivo = rs.getInt("nConsecutivoModificacion");
            }
            conv.setnConsecutivoModificacion(nConsecutivo + 1);
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public boolean deletePartidasConvCap4(Connection conn, ConvenioCap4 conv) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "DELETE FROM mContratoModificadoCap4Partida WHERE nIdContModCap4= ? ";
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setInt(1, conv.getnIdContModCap4());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean deleteConvenioCap4(Connection conn, ConvenioCap4 conv) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "DELETE FROM mContratoModificadoCap4 WHERE nIdContModCap4= ? and cIdContratoDefinitivo=? and nConsecutivoModificacion=? ";
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setInt(1, conv.getnIdContModCap4());
            ps.setString(2, conv.getcIdContratoDefinitivo());
            ps.setInt(3, conv.getnConsecutivoModificacion());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public int stateConvenioCap4(Connection conn, ConvenioCap4 conv) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select *from mContratoModificadoCap4 with(Nolock) where nIdContModCap4= ? and cIdContratoDefinitivo=? and nConsecutivoModificacion=? ";
        int nState = 4;
        try {
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setInt(1, conv.getnIdContModCap4());
            ps.setString(2, conv.getcIdContratoDefinitivo());
            ps.setInt(3, conv.getnConsecutivoModificacion());
            rs = ps.executeQuery();
            if (rs.next()) {
                nState = rs.getInt("nEstatus");
            }
            return nState;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public boolean updateItemConvCap4(Connection conn, ConvenioCap4 conv) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update mContratoModificadoCap4Partida set nCantidad=?,mMontoNeto=?,mPrecioUnitario=? ");
            query.append(" where nIdContModCap4=? and nIdLineaConsolidado=?");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, conv.getPartidas().get(0).getnCantidad());
            ps.setDouble(2, conv.getPartidas().get(0).getmMontoNeto());
            ps.setDouble(3, conv.getPartidas().get(0).getmPrecioUnitario());
            ps.setInt(4, conv.getnIdContModCap4());
            ps.setInt(5, conv.getPartidas().get(0).getnIdLineaConsolidado());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public void queryItemConvCap4(Connection conn, ConvenioCap4 conv) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select *from mContratoModificadoCap4Partida with(Nolock) where nIdContModCap4=? and nIdLineaConsolidado=? ";
        try {
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setInt(1, conv.getnIdContModCap4());
            ps.setInt(2, conv.getPartidas().get(0).getnIdLineaConsolidado());
            rs = ps.executeQuery();
            if (rs.next()) {
                if ("SRV".equalsIgnoreCase(rs.getString("cIdUnidadMedida"))) {
                    conv.getPartidas().get(0).setnCantidad(rs.getInt("nCantidad"));
                } else {
                    conv.getPartidas().get(0).setmPrecioUnitario(rs.getDouble("mPrecioUnitario"));
                }
                conv.getPartidas().get(0).setmMontoNetoOriginal(rs.getDouble("mMontoNetoOriginal"));
                conv.getPartidas().get(0).setnIdIVA(rs.getInt("nIdIVA"));
                conv.getPartidas().get(0).setnCantidadOriginal(rs.getInt("nCantidadOriginal"));
                conv.getPartidas().get(0).setcIdUnidadMedida(rs.getString("cIdUnidadMedida"));
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public int getPorcentajeIVA(Connection conn, int nIdIVA) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select *from mCatalogoTipoIVA with(Nolock) where IDIVA=? ";
        int nPorcentaje = 0;
        try {
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setInt(1, nIdIVA);
            rs = ps.executeQuery();
            if (rs.next()) {
                nPorcentaje = rs.getInt("VALOR");
            }
            return nPorcentaje;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public boolean updateMontoModConvCap4(Connection conn, ConvenioCap4 conv) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update conv set conv.mTotalNuevo=conv.mTotalAnterior+isnull(modificado.montoModificado,0) ");
            query.append("from mContratoModificadoCap4  conv with(Nolock) ");
            query.append("inner join (select nIdContModCap4,sum(mMontoNeto)montoModificado from mContratoModificadoCap4Partida with(Nolock) ");
            query.append("group by nIdContModCap4 )modificado on modificado.nIdContModCap4=conv.nIdContModCap4 ");
            query.append("where conv.cIdContratoDefinitivo=? and conv.nIdContModCap4=? and conv.nConsecutivoModificacion=?");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, conv.getcIdContratoDefinitivo());
            ps.setInt(2, conv.getnIdContModCap4());
            ps.setInt(3, conv.getnConsecutivoModificacion());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean addContratoDiversoConvenio(Connection conn, ConvenioCap4 conv) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("INSERT into pContratoDiversoConvenio( cEjercicio, cIdEntidadContable, cIdContrato, nConsecutivoModificacion,	mConvenio,	mIVAConvenio, ");
            query.append(" mTotal,	mGlobalContrato,	fInicio, fTermino,	fFirmaContrato,		cIdModificacion,cOrigenRM,	fAdjudicacion,cDocumentoAbierto) ");
            query.append("select cont.cEjercicio,'10' cEntidadContable,cont.cIdContratoDefinitivo,cont.nConsecutivoModificacion,part.subtotal,part.montoIVA,part.total,cont.mTotalNuevo ");
            query.append(",fFechaInicioEntrega,fFechaFin,fFechaFormalizacion,cIdContratoDefinitivo+'#M'+convert(varchar,cont.nConsecutivoModificacion),'CONTRATO MODIFICADO',fFechaFormalizacion,0 ");
            query.append(" from mContratoModificadoCap4 cont with(Nolock) ");
            query.append(" inner join (select  ");
            query.append(" part.nIdContModCap4	,convert(money,round(sum(mMontoNeto/(1+(0.01*iva.VALOR))),2))subtotal  ");
            query.append(" ,sum(mMontoNeto)total	,convert(money,sum(mMontoNeto)-round(sum(mMontoNeto/(1+(0.01*iva.VALOR))),2)) montoIVA  ");
            query.append(" from mContratoModificadoCap4Partida part with(Nolock)  ");
            query.append(" inner join mCatalogoTipoIVA as iva on iva.IDIVA=part.nIdIVA ");
            query.append(" group by part.nIdContModCap4 ");
            query.append(" )part on part.nIdContModCap4=cont.nIdContModCap4 ");
            query.append(" where cont.nIdContModCap4=? and cont.cIdContratoDefinitivo=? and cont.nConsecutivoModificacion=? ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, conv.getnIdContModCap4());
            ps.setString(2, conv.getcIdContratoDefinitivo());
            ps.setInt(3, conv.getnConsecutivoModificacion());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean addContratoDiverso(Connection conn, ConvenioCap4 conv) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        String nameDB = "";
        try {
            query = new StringBuilder();
            nameDB = Util.getNameDB(conn, Integer.parseInt(conv.getcEjercicio()) - 1);
            query.append("insert into pContratoDiverso (cEjercicio,cIdEntidadContable,cIdContrato, cIdTipoDocumento, cIdRFC, cIdTipoContratoDiverso, ");
            query.append("cPlazo, cIdUnidadAdministrativa, cIdGRegional, cIdGEstatal,cIdDistritoRiego, cIdTipoFondo, cConceptoContrato, fDocumento,");
            query.append(" cIdTipoAdjudicacion, fAdjudicacion, fContratoIni, fContratoFin,cIdTipoMoneda, fVigenciaIVA, nPorcIVAAplicable, mImporteContrato,");
            query.append("mImporteHonorarios, mImporteViaticos, mImporteBruto, mImporteIVA,mImporteTotal, mContratoMN, mContratoME, cIdUsuarioResponsable, ");
            query.append("fFirmaContrato, id_caso,cOrigenRM,isConvEjercicioAnt,nEsDescentralizado,cNoProcedimientoCNET,nCodContratoCNET,cAprobacionPLU,nCodExpedienteCNET)");
            query.append("select cEjercicio,cIdEntidadContable,cIdContrato, cIdTipoDocumento, cIdRFC, cIdTipoContratoDiverso,");
            query.append("cPlazo, cIdUnidadAdministrativa, cIdGRegional, cIdGEstatal,cIdDistritoRiego, cIdTipoFondo, cConceptoContrato, fDocumento,");
            query.append("cIdTipoAdjudicacion, fAdjudicacion, fContratoIni, fContratoFin,cIdTipoMoneda, fVigenciaIVA, nPorcIVAAplicable, 0,");
            query.append("mImporteHonorarios, mImporteViaticos, 0, 0,0, 0, mContratoME, cIdUsuarioResponsable,");
            query.append("fFirmaContrato, " + conv.getnConsecutivoCDIV() + ",cOrigenRM,isConvEjercicioAnt,nEsDescentralizado,cNoProcedimientoCNET,nCodContratoCNET,cAprobacionPLU,nCodExpedienteCNET from ");
            query.append(nameDB);
            query.append("..pContratoDiverso with(Nolock) where cidcontrato=?");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, conv.getcIdContratoDefinitivo());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean addContratoDiverso(Connection conn, Contrato cont) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("insert into pContratoDiverso (cEjercicio,cIdEntidadContable,cIdContrato, cIdTipoDocumento, cIdRFC, cIdTipoContratoDiverso, ");
            query.append("cPlazo, cIdUnidadAdministrativa, cIdGRegional, cIdGEstatal,cIdDistritoRiego, cIdTipoFondo, cConceptoContrato, fDocumento,");
            query.append(" cIdTipoAdjudicacion, fAdjudicacion, fContratoIni, fContratoFin,cIdTipoMoneda, fVigenciaIVA, nPorcIVAAplicable, mImporteContrato,");
            query.append("mImporteHonorarios, mImporteViaticos, mImporteBruto, mImporteIVA,mImporteTotal, mContratoMN, mContratoME, cIdUsuarioResponsable, ");
            query.append("fFirmaContrato, id_caso,cOrigenRM,isConvEjercicioAnt,nEsDescentralizado,cNoProcedimientoCNET,nCodContratoCNET,cAprobacionPLU,nCodExpedienteCNET)");
            query.append(" select cont.cEjercicio,cont.cIdEntidadContable,cIdContratoDefinitivo ");
            query.append(" ,2 cidTipoDocumento,replace(cIdRFC,'-','')rfc ");
            query.append(" ,case when cont.cIdTipoContrato='CC' or cont.cIdTipoContrato='CR' then 4 else 3 end cidtipoContratoDiverso ");
            query.append(" ,0 cPlazo,cont.cIdUnidadEjecutora,0,0,0,'FF'cIdTipoFondo ");
            query.append(" ,substring(isnull(cNoContratoCNET,'')+' '+REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE ");
            query.append(" (REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(cConceptoContrato+'-',',',' ') ");
            query.append(" ,'%',' '),'&',' '),'(',' '),')',' '),'?','N'),'?','n'),';',' '),'=',''),'''',''),'<',''),'>',''),'?',''),'!','') ");
            query.append(" ,'\',''),'+',''),'{',''),'}',''),'*',''),':',''),'[',''),']',''),'\"',''),1,512) concepto ");
            query.append(" ,SYSDATETIME(),cea.nIdEquivalencia,cont.fFormalizacion,cont.fInicio,fFin ");
            query.append(" ,0,null,(cont.nIVA*0.01)porcentajeIVA ");
            query.append(" ,(ISNULL(dbo.fn_mContratoMontoBruto(cont.cEjercicio,cont.cIdTipoContrato,cont.cIdUnidadEjecutora,cont.nIdConsecutivo),0) )importeCont ");
            query.append(" ,0,0 ");
            query.append(" ,(ISNULL(dbo.fn_mContratoMontoBruto(cont.cEjercicio,cont.cIdTipoContrato,cont.cIdUnidadEjecutora,cont.nIdConsecutivo),0) )importeBruto ");
            query.append(" ,(ISNULL(dbo.fn_mContratoMontoNeto(cont.cEjercicio,cont.cIdTipoContrato,cont.cIdUnidadEjecutora,cont.nIdConsecutivo),0) ) ");
            query.append(" -(ISNULL(dbo.fn_mContratoMontoBruto(cont.cEjercicio,cont.cIdTipoContrato,cont.cIdUnidadEjecutora,cont.nIdConsecutivo),0) )importeIVA   ");
            query.append(" ,(ISNULL(dbo.fn_mContratoMontoNeto(cont.cEjercicio,cont.cIdTipoContrato,cont.cIdUnidadEjecutora,cont.nIdConsecutivo),0) )importeTotal ");
            query.append(" ,(ISNULL(dbo.fn_mContratoMontoNeto(cont.cEjercicio,cont.cIdTipoContrato,cont.cIdUnidadEjecutora,cont.nIdConsecutivo),0) )mContratoMN ");
            query.append(" ,case when cont.nIdTipoCambio='01' then 0 else isnull(dbo.fn_mContratoMontoNetoME(cont.cEjercicio,cont.cIdTipoContrato,cont.cIdUnidadEjecutora,cont.nIdConsecutivo),0)end mContratoME ");
            query.append(" ,cont.cIdUsuarioCreacion,cont.fFormalizacion,?,'CONTRATO',0,cont.esDescentralizado,pro.cOficio,cont.nCodContratoCNET,cont.cFolioMASCP ");
            query.append(" ,cont.nCodExpedienteCNET ");
            query.append(" from mContrato  cont with(Nolock) ");
            query.append(" inner join mProcedimiento pro with(nolock) ");
            query.append(" on cont.cIdProcedimiento=pro.cIdProcedimiento ");
            query.append(" inner join mCatalogoEquivalenciaTipoAdjudicacion cea with(nolock) ");
            query.append(" on cea.nIdCategoriaProcedimiento=pro.nIdCategoria ");
            query.append(" where cIdContratoDefinitivo=? ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, cont.getnConsecutivoCDIV());
            ps.setString(2, cont.getcIdContratoDefinitivo());
            success = ps.executeUpdate() > 0;
        } finally {
            if (query.length() > 1) {
                query.delete(0, query.length());
            }
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean deleteContratoEP_TEMP(Connection conn, String cIdContratoDef, String cEP) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "delete tContratoEP_TMP where cIdContratoDefinitivo=? and nIdClaveEgresos=? ";
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, cIdContratoDef);
            ps.setString(2, cEP);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean deleteContratoEP_TEMP(Connection conn, String cIdContratoDef) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "delete tContratoEP_TMP where cIdContratoDefinitivo=?  ";
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, cIdContratoDef);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean addContratoEP_TEMP(Connection conn, DatosEP_TMP dat) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "insert into tContratoEP_TMP (cEjercicio,cIdUnidadEjecutora,cIdContrato,cIdContratoDefinitivo,cIdTipoContrato,nIdClaveEgresos,ClaveInterna,cIdEntidadContable)" + "values(?,?,?,?,?,?,?,?)";
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, dat.getcEjercicio());
            ps.setString(2, dat.getcIdUnidadEjecutora());
            ps.setString(3, dat.getcIdContrato());
            ps.setString(4, dat.getcIdContratoDefinitivo());
            ps.setString(5, dat.getcIdTipocontrato());
            ps.setString(6, dat.getcIdClaveEgresos());
            ps.setString(7, dat.getcClaveInterna());
            ps.setString(8, dat.getcIdEntidadContable());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean addEP_TEMP_Apartado(Connection conn, Contrato cont) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" insert into tContratoEP_TMP (cEjercicio,cIdUnidadEjecutora,cIdContrato,cIdContratoDefinitivo,cIdTipoContrato,nIdClaveEgresos,ClaveInterna,cIdEntidadContable) ");
            query.append(" select sol.cEjercicio,sol.cIdUnidadEjecutora ");
            query.append(" ,? cidcontrato,? cidContratoDef,? cTipoContrato ");
            query.append(" ,det.EP,SUBSTRING(det.EP,57,7) ClaveInterna,enc.cCentroContable ");
            query.append(" from tApartadoEncabezado enc with(Nolock)  ");
            query.append(" inner join tApartadoDetalle as det with(Nolock)  ");
            query.append(" on enc.nFolioApartado=det.nFolioApartado ");
            query.append(" inner join mSolicitud as sol with(Nolock) ");
            query.append(" on sol.cIdSolicitud=enc.cIdSolicitud ");
            query.append(" where enc.cDocumentoHaplicado='S'and enc.cIdSolicitud=? ");
            query.append(" group by sol.cEjercicio,sol.cIdUnidadEjecutora,det.EP,enc.cCentroContable ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cont.getcIdContrato());
            ps.setString(2, cont.getcIdContratoDefinitivo());
            ps.setString(3, cont.getcIdtipoContrato());
            ps.setString(4, cont.getcIdRequi());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean insertContratoEP(Connection conn, String cIdContratoDef) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("insert into tContratoEP (cEjercicio,cIdContrato,cTipoContrato,EP,cIdEntidadContable) ");
            query.append("select temp.cEjercicio,temp.cIdContratoDefinitivo,'DI',temp.nIdClaveEgresos,temp.cIdEntidadContable ");
            query.append("from tContratoEP_TMP temp with(Nolock) left join tContratoEP as ep with(Nolock) ");
            query.append("on ep.cIdContrato=temp.cIdContratoDefinitivo and ep.EP=temp.nIdClaveEgresos where temp.cIdContratoDefinitivo=? and ep.EP is null ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cIdContratoDef);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean deleteContratoDiversoConvenio(Connection conn, String cIdContratoDef, int nConsecutivoMod, String cIdModificacion) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "delete pContratoDiversoConvenio where cIdContrato=? and nConsecutivoModificacion=? and cIdModificacion=? ";
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, cIdContratoDef);
            ps.setInt(2, nConsecutivoMod);
            //cIdContratoDef+"#M"+nConsecutivoMod
            ps.setString(3, cIdModificacion);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean updatePresupuestoConvCap4(Connection conn, ConvenioCap4 conv) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update mContratoModificadoCap4 set nEstatus=?,fFechaFormalizacion=convert(date,?),fFechaInicioEntrega=convert(date,?),fFechaFin=convert(date,?) ,cObjetoConvenio=?,cNoConvenio=? ");
            query.append(" where nIdContModCap4=? and cIdContratoDefinitivo=? and nConsecutivoModificacion=?");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, conv.getnEstatus());
            ps.setString(2, conv.getfFormalizacion());
            ps.setString(3, conv.getfInicio());
            ps.setString(4, conv.getfFin());
            ps.setString(5, conv.getcObjetoConvenio());
            ps.setString(6, conv.getcNoConvenio());
            ps.setInt(7, conv.getnIdContModCap4());
            ps.setString(8, conv.getcIdContratoDefinitivo());
            ps.setInt(9, conv.getnConsecutivoModificacion());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean updateFolioCasoConvCap4(Connection conn, ConvenioCap4 conv) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update mContratoModificadoCap4 set nConsecutivoCDIV=?,cFolio=? where cIdContratoDefinitivo=? and nIdContModCap4=? and nConsecutivoModificacion=?");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, conv.getnConsecutivoCDIV());
            ps.setString(2, conv.getcFolio());
            ps.setString(3, conv.getcIdContratoDefinitivo());
            ps.setInt(4, conv.getnIdContModCap4());
            ps.setInt(5, conv.getnConsecutivoModificacion());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean updateEstatusConvCap4(Connection conn, ConvenioCap4 conv) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update mContratoModificadoCap4 set nEstatus=? ");
            query.append(" where nIdContModCap4=? and cIdContratoDefinitivo=? and nConsecutivoModificacion=?");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, conv.getnEstatus());
            ps.setInt(2, conv.getnIdContModCap4());
            ps.setString(3, conv.getcIdContratoDefinitivo());
            ps.setInt(4, conv.getnConsecutivoModificacion());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean updatePrecomConvCap4(Connection conn, ConvenioCap4 conv) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update mContratoModificadoCap4 set nEstatus=?,cFolioPre=?,nConsecutivoPrecom=? ");
            query.append(" where nIdContModCap4=? and cIdContratoDefinitivo=? and nConsecutivoModificacion=?");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, conv.getnEstatus());
            ps.setString(2, conv.getcFolioPre());
            ps.setInt(3, conv.getnConsecutivoPrecom());
            ps.setInt(4, conv.getnIdContModCap4());
            ps.setString(5, conv.getcIdContratoDefinitivo());
            ps.setInt(6, conv.getnConsecutivoModificacion());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public int arethereEP(Connection conn, String cIdContratoDef) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select *from tContratoEP_TMP  with(Nolock) where cIdContratoDefinitivo=?  ";
        int exist = 0;
        try {
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, cIdContratoDef);
            rs = ps.executeQuery();
            if (rs.next()) {
                exist = 1;
            }
            return exist;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public int existThisEP(Connection conn, String cIdContratoDef, String ep) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select *from tContratoEP_TMP  with(Nolock) where cIdContratoDefinitivo=?  and nIdClaveEgresos=?";
        int exist = 0;
        try {
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, cIdContratoDef);
            ps.setString(2, ep);
            rs = ps.executeQuery();
            if (rs.next()) {
                exist = 1;
            }
            return exist;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public void getIdCasoConvCap4(Connection conn, ConvenioCap4 conv) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" Select caso.ID_CASO nIdCaso,cont.cFolioPre as cFolioPre,cont.nConsecutivoPrecom from tPreCompromisoEncabezado precom with(Nolock) ");
            query.append(" inner join mContratoModificadoCap4 cont with(Nolock) ");
            query.append(" on precom.cIdContrato=cont.cIdContratoDefinitivo  +'#M'+convert(varchar,cont.nConsecutivoModificacion) ");
            query.append(" inner join CG_CASO as caso with(Nolock) on   caso.C_FOLIO=cont.cFolioPre ");
            query.append(" where precom.cIdContrato =? and cDocumentoHaplicado='S' ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, conv.getcIdContratoDefinitivo() + "#M" + conv.getnConsecutivoModificacion());
            rs = ps.executeQuery();
            if (rs.next()) {
                conv.setnConsecutivoCDIV(rs.getInt("nIdCaso"));
                conv.setcFolioPre(rs.getString("cFolioPre"));
                conv.setnConsecutivoPrecom(rs.getInt("nConsecutivoPrecom"));
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public boolean searchDataConvenioCap4(Connection conn, ConvenioCap4 conv) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        boolean resp = false;
        try {
            query = new StringBuilder();
            query.append("select *from mContratoModificadoCap4 with(Nolock) where cIdContratoDefinitivo=? and nConsecutivoModificacion=? and nIdContModCap4=?  ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, conv.getcIdContratoDefinitivo());
            ps.setInt(2, conv.getnConsecutivoModificacion());
            ps.setInt(3, conv.getnIdContModCap4());
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
                conv.setcIdUnidadEjecutora(rs.getString("cIdUnidadEjecutora"));
                conv.setmTotalAnterior(rs.getDouble("mTotalAnterior"));
                conv.setnEstatus(rs.getInt("nEstatus"));
                conv.setnIdCategoria(rs.getInt("nIdCategoria"));
                conv.setnIdFundamentoLegal(rs.getInt("nIdFundamentoLegal"));
                conv.setcIdRFC(rs.getString("cIdRFC"));
                conv.setcNumProcedCNET(rs.getString("cNumProcedCNET"));
                conv.setnCodExpedienteCNET(rs.getString("nCodExpedienteCNET"));
                conv.setnCodContratoCNET(rs.getString("nCodContratoCNET"));
                conv.setcFolio(rs.getString("cFolio"));
                conv.setnConsecutivoCDIV(rs.getInt("nConsecutivoCDIV"));
                conv.setcFolioPre(rs.getString("cFolioPre"));
                conv.setnConsecutivoPrecom(rs.getInt("nConsecutivoPrecom"));
                conv.setcEjercicio(rs.getString("cEjercicio"));
                conv.setlEsConvEjercicioAnt(rs.getInt("lEsConvEjercicioAnt"));
                conv.setlEsTotalPluri(rs.getInt("lEsTotalPluri"));
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return resp;
    }

    public boolean tienePagoDeAnticipo(Connection conn, String cNameDB, String cIdcontratoDef) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        boolean resp = false;
        try {
            query = new StringBuilder();
            query.append(" select *from " + cNameDB + "..mRecepcionpMat as recep with(Nolock) ");
            query.append(" inner join " + cNameDB + "..mRecepcionpMatAnticipo as anticipo with(Nolock) ");
            query.append(" on anticipo.cIdpedContDef=recep.cIdpedContDef ");
            query.append(" and anticipo.nIdConsecutivoRecepM=recep.nIdConsecutivoRecepM ");
            query.append(" and anticipo.cIdRecepMat=recep.cIdRecepMat ");
            query.append(" where recep.nIdEstadoRecepMat=3 ");
            query.append(" and recep.cIdpedContDef=? ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cIdcontratoDef);
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
            }
        } finally {
            query.delete(0, query.length() - 1);
            query = null;
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return resp;
    }

    public boolean tieneRecepMat(Connection conn, String cNameDB, String cIdcontratoDef) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        boolean resp = false;
        try {
            query = new StringBuilder();
            query.append(" select *from " + cNameDB + "..mRecepcionpMat as recep with(Nolock) ");
            query.append(" where recep.cIdpedContDef=? and recep.nIdEstadoRecepMat=3 and recep.cIdRecepMat like'RM%' ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cIdcontratoDef);
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
            }
        } finally {
            query.delete(0, query.length() - 1);
            query = null;
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return resp;
    }

    public boolean addMontoNoRecepDelEjercicioAnt(Connection conn, String cNameDB, String cIdcontratoDef) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" insert into mCantMontoNoRecepcionadoEjeAnt (cIdpedContDef,nIdLineaConsolidado,nCantidad,mMontoConIVA) ");
            query.append(" select  pluri.cIdContratoDefinitivo ,partidas.nIdLineaConsolidado ");
            query.append(" ,case when partidas.cIdTipoProcedimiento='PS' then 1 else ");
            query.append(" convert( int,((partidas.mMontoNetoPluri/(1+(0.01*partidas.nPocentajeIVA)))/partidas.mMontoMinimo))-pluri.nCantidadMinima end cantidad ");
            query.append(" ,partidas.mMontoNetoLinea ");
            query.append(" from mPartidasContratoPlurianual pluri with(Nolock)  ");
            query.append(" inner join " + cNameDB + "..mContrato contrato with(Nolock)  ");
            query.append(" on pluri.cIdContratoDefinitivo=contrato.cIdContratoDefinitivo ");
            query.append(" inner join " + cNameDB + "..mProcedimientoAdjudicacionPartidas as partidas  ");
            query.append(" on partidas.nIdconsecutivoAdj=contrato.nIdconsecutivoAdj  ");
            query.append(" and partidas.cIdRFC=contrato.cIdRFC  ");
            query.append(" and partidas.cIdProcedimiento=contrato.cIdProcedimiento ");
            query.append(" and partidas.nIdLineaConsolidado=pluri.nIdLineaConsolidado ");
            query.append(" where pluri.cIdContratoDefinitivo=?");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cIdcontratoDef);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean existRecisioncontrato(Connection conn, DatosArchivo datos) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        boolean resp = false;
        try {
            query = new StringBuilder();
            query.append(" select *from mCatalogoRecisionContratos as recision with(Nolock) where  cIdRFC=? and cNumeroContratoCNET=?");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, datos.getcIdRFC());
            ps.setString(2, datos.getcContratoCNET());
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
            }
        } finally {
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return resp;
    }

    public boolean addRecisioncontrato(Connection conn, DatosArchivo datos, Usuario usuario) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" insert into mCatalogoRecisionContratos (cEjercicioFiscal,cIdRFC,cIdContratoDefinitivo,cNumeroContratoCNET,cNumeroProcedimiento ");
            query.append(" ,cCodigoExpedienteCNET,cCodigoContratoCNET,cCausaRecision,lExistePagoPendiente,fFechaRecision,fFechaNotificacionUAF ");
            query.append(" ,cFolioSAI,cLogin,fFechaCaptura,cNameDB) ");
            query.append(" values(?, ?, ?, ?, ?, ?, ?, ?, ?, convert(date,?), convert(date,?), ?, ?, convert(date,getDate()), ?) ");
            ps = conn.prepareStatement(query.toString());
            log.info("Object: {}", query.toString());
            ps.setString(1, datos.getcEjercicioContrato());
            ps.setString(2, datos.getcIdRFC());
            ps.setString(3, datos.getcIdContratoDefinitivo());
            ps.setString(4, datos.getcContratoCNET());
            ps.setString(5, datos.getcNumProcedimientoCNET());
            ps.setString(6, datos.getcNumExpedienteCNET());
            ps.setString(7, datos.getcCodigoContratoCNET());
            ps.setString(8, datos.getcCausa());
            ps.setInt(9, datos.getnTienePagoPendiente());
            ps.setString(10, datos.getfFechaTermino());
            ps.setString(11, datos.getFechaNotificacionUAF());
            ps.setString(12, datos.getcFolio());
            ps.setString(13, usuario.getLogin());
            ps.setString(14, datos.getcNameDB());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean searchDataContArt25(Connection conn, ContratoArt25 cont) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        boolean resp = false;
        try {
            query = new StringBuilder();
            query.append("select *from mContratoArt25 with(Nolock) where cIdContratoDefinitivo=?   ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cont.getcIdContratoDef());
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
                cont.setcIdUnidadEjecutora(rs.getString("cIdUnidadEjecutora"));
                cont.setcEjercicio(rs.getString("cEjercicio"));
                cont.setcConceptoContrato(rs.getString("cConceptoContrato"));
                cont.setCfolio(rs.getString("C_FOLIO"));
                cont.setcIdEntidadContable(rs.getString("cIdEntidadContable"));
                cont.setcIdRFC(rs.getString("cIdRFC"));
                cont.setcIdTipoCambio(rs.getString("cIdTipoCambio"));
                cont.setcIdTipoContrato(rs.getString("cIdTipoContrato"));
                cont.setcIdUsuarioCreacion(rs.getString("cIdUsuarioCreacion"));
                cont.setcNoContratoCNET(rs.getString("cNoContratoCNET"));
                cont.setcNumProcedCET(rs.getString("cNumProcedCNET"));
                cont.setfFechaFallo(rs.getString("fFallo"));
                cont.setfFechaFin(rs.getString("fFin"));
                cont.setfFechaFormalizacion(rs.getString("fFormalizacion"));
                cont.setfFechaInicio(rs.getString("fInicio"));
                cont.setmMontoBruto(rs.getDouble("mMontoBruto"));
                cont.setmMontoIVA(rs.getDouble("mMontoIVA"));
                cont.setmMontoNeto(rs.getDouble("mMontoNeto"));
                cont.setnCodContratoCNET(rs.getString("nCodContratoCNET"));
                cont.setnCodExpedienteCNET(rs.getString("nCodExpedienteCNET"));
                cont.setnConsecutivoCDIV(rs.getInt("ConsecutivoCDIV"));
                cont.setnEsAbierto(rs.getInt("nEsAbierto"));
                cont.setnEsDescentralizado(rs.getInt("nEsDescentralizado"));
                cont.setnIdCategoria(rs.getInt("nIdCategoria"));
                cont.setnIdConsecutivo(rs.getInt("nIdConsecutivo"));
                cont.setnIdEstado(rs.getInt("nIdEstado"));
                cont.setnIdFundamentoLeg(rs.getInt("nIdFundamentoLeg"));
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return resp;
    }

    public boolean updateEstatusContratoArt25(Connection conn, ContratoArt25 contrato) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update  mContratoArt25 set nIdEstado=? where cIdContratoDefinitivo=?");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, contrato.getnIdEstado());
            ps.setString(2, contrato.getcIdContratoDef());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean updateFolioCasoContratoArt25(Connection conn, ContratoArt25 contrato) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update  mContratoArt25 set C_FOLIO=?,ConsecutivoCDIV=? where cIdContratoDefinitivo=?");
            log.info("Object: {}", query.toString());
            log.info("Object: {}", "C_FOLIO : " + contrato.getCfolio());
            log.info("Object: {}", "ConsecutivoCDIV : " + contrato.getnConsecutivoCDIV());
            log.info("Object: {}", "cIdContratoDefinitivo : " + contrato.getcIdContratoDef());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, contrato.getCfolio());
            ps.setInt(2, contrato.getnConsecutivoCDIV());
            ps.setString(3, contrato.getcIdContratoDef());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean addContratoDiverso(Connection conn, ContratoArt25 cont) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("insert into pContratoDiverso (cEjercicio,cIdEntidadContable,cIdContrato, cIdTipoDocumento, cIdRFC, cIdTipoContratoDiverso, ");
            query.append("cPlazo, cIdUnidadAdministrativa, cIdGRegional, cIdGEstatal,cIdDistritoRiego, cIdTipoFondo, cConceptoContrato, fDocumento,");
            query.append(" cIdTipoAdjudicacion, fAdjudicacion, fContratoIni, fContratoFin,cIdTipoMoneda, fVigenciaIVA, nPorcIVAAplicable, mImporteContrato,");
            query.append("mImporteHonorarios, mImporteViaticos, mImporteBruto, mImporteIVA,mImporteTotal, mContratoMN, mContratoME, cIdUsuarioResponsable, ");
            query.append("fFirmaContrato, id_caso,cOrigenRM,isConvEjercicioAnt,nEsDescentralizado,cNoProcedimientoCNET,nCodContratoCNET,cAprobacionPLU,nCodExpedienteCNET)");
            query.append("select cont.cEjercicio,cont.cIdEntidadContable,cIdContratoDefinitivo  ");
            query.append("			 ,2 cidTipoDocumento,replace(cIdRFC,'-','')rfc  ");
            query.append("			 ,case when cont.cIdTipoContrato='CS' then 1 else 4 end cidtipoContratoDiverso  ");
            query.append("			 ,0 cPlazo,cont.cIdUnidadEjecutora,0,0,0,'FF'cIdTipoFondo  ");
            query.append("			 ,substring(isnull(cNoContratoCNET,'')+' '+REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE  ");
            query.append("			 (REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(cConceptoContrato+'-',',',' ')  ");
            query.append("			 ,'%',' '),'&',' '),'(',' '),')',' '),'?','N'),'?','n'),';',' '),'=',''),'''',''),'<',''),'>',''),'?',''),'!','')  ");
            query.append("			 ,'\',''),'+',''),'{',''),'}',''),'*',''),':',''),'[',''),']',''),'\"',''),1,512) concepto  ");
            query.append("			 ,SYSDATETIME(),cea.nIdEquivalencia,cont.fFormalizacion,cont.fInicio,fFin  ");
            query.append("			 ,0,null,case when cont.mMontoIVA>0 then 0.16 else 0 end porcentajeIVA  ");
            query.append("			 ,cont.mMontoBruto importeCont  ");
            query.append("			 ,0,0  ");
            query.append("			 ,cont.mMontoBruto importeBruto  ");
            query.append("			 ,cont.mMontoIVA importeIVA    ");
            query.append("			 ,cont.mMontoNeto importeTotal  ");
            query.append("			 ,cont.mMontoNeto mContratoMN  ");
            query.append("			 ,0 mContratoME  ");
            query.append("			 ,cont.cIdUsuarioCreacion,cont.fFormalizacion ");
            query.append("			 ,? idCaso ");
            query.append("			 ,'CONTRATO',0,cont.nEsDescentralizado,cont.cNumProcedCNET,cont.nCodContratoCNET,'' cFolioMASCP  ");
            query.append("			 ,cont.nCodExpedienteCNET  ");
            query.append("			 from mContratoArt25  cont with(Nolock)  ");
            query.append("			inner join mCatalogoEquivalenciaTipoAdjudicacion cea with(nolock) ");
            query.append("			on cea.nIdCategoriaProcedimiento=cont.nIdCategoria ");
            query.append("			 where cIdContratoDefinitivo=?  ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, cont.getnConsecutivoCDIV());
            ps.setString(2, cont.getcIdContratoDef());
            success = ps.executeUpdate() > 0;
        } finally {
            if (query.length() > 1) {
                query.delete(0, query.length());
            }
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean deleteContratoDiverso(Connection conn, String cIdContratoDef) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "delete pContratoDiverso where cIdContrato=?  ";
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, cIdContratoDef);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean updateTotalGarantiaContrato(Connection conn, String cIdContratoDef) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" update cont ");
            query.append(" set cont.mTotalGarantias=(isnull(sub.montoTotalGarantias,0) ) ");
            query.append(" from mContratosConGarantia cont with(Nolock) ");
            query.append(" inner join ( ");
            query.append("	select ");
            query.append("	sum(isnull(mMontoGarantia,0))montoTotalGarantias ");
            query.append("	,cIdContratoDefinitivo ");
            query.append("	from mGarantiasContrato garantia with(Nolock) ");
            query.append("	group by cIdContratoDefinitivo ");
            query.append(" )sub on sub.cIdContratoDefinitivo=cont.cIdContratoDefinitivo ");
            query.append(" where cont.cIdContratoDefinitivo=? ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cIdContratoDef);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query.delete(0, query.length());
            query = null;
        }
        return success;
    }

    public boolean updateGarantiaContrato(Connection conn, GarantiaContrato garantia, int index) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" update mGarantiasContrato set cAseguradora=?,lFianza=?,lcheque=? ");
            query.append(" ,fFechaExpedicion=convert(date,?),cNumeroChequeFianza=? ");
            query.append(" ,mMontoGarantia=? ");
            query.append(" where cIdContratoDefinitivo=? and nIdtipoProcesoGarantia=? and nTipoGarantia=? and nIdConsecutivoEndosoGarantia=?");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, garantia.getListGarantia().get(index).getcAseguradora());
            ps.setInt(2, garantia.getListGarantia().get(index).getlFianza());
            ps.setInt(3, garantia.getListGarantia().get(index).getlCheque());
            ps.setString(4, garantia.getListGarantia().get(index).getfFechaExpedicion());
            ps.setString(5, garantia.getListGarantia().get(index).getcNumeroChequeFianza());
            ps.setFloat(6, garantia.getListGarantia().get(index).getmMontoGarantia());
            ps.setString(7, garantia.getcIdContratoDefinitivo());
            ps.setInt(8, garantia.getnTipoProcesoGarantia());
            ps.setInt(9, garantia.getListGarantia().get(index).getnTipoGarantia());
            ps.setInt(10, garantia.getListGarantia().get(index).getnIdConsecutivoEndosoGarantia());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query.delete(0, query.length());
            query = null;
        }
        return success;
    }

    public boolean deleteGarantiaContrato(Connection conn, GarantiaContrato garantia, int index) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "delete mGarantiasContrato where cIdContratoDefinitivo=? and nTipoGarantia=? and  nIdtipoProcesoGarantia=? and nIdConsecutivoEndosoGarantia=?";
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, garantia.getcIdContratoDefinitivo());
            ps.setInt(2, garantia.getListGarantia().get(index).getnTipoGarantia());
            ps.setInt(3, garantia.getnTipoProcesoGarantia());
            ps.setInt(4, garantia.getListGarantia().get(index).getnIdConsecutivoEndosoGarantia());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean updateUsuarioCapGarantiaContrato(Connection conn, GarantiaContrato garantia, int index) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" update mUsuarioCapturaGarantia set cUsuarioActualiza=?,fFechaActualiza=getDate() ");
            query.append(" where cIdContratoDefinitivo=? and nIdtipoProcesoGarantia=? and nIdConsecutivoEndosoGarantia=? ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, garantia.getcUsuarioActualiza());
            ps.setString(2, garantia.getcIdContratoDefinitivo());
            ps.setInt(3, garantia.getnTipoProcesoGarantia());
            ps.setInt(4, garantia.getListGarantia().get(index).getnIdConsecutivoEndosoGarantia());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query.delete(0, query.length());
            query = null;
        }
        return success;
    }

    public boolean addGarantiaContrato(Connection conn, GarantiaContrato garantia, int index) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" insert into mGarantiasContrato (cIdContratoDefinitivo,nTipoGarantia,nIdtipoProcesoGarantia,cAseguradora,lFianza,lcheque ");
            query.append(" ,fFechaExpedicion,cNumeroChequeFianza,mMontoGarantia,nIdConsecutivoEndosoGarantia )");
            query.append(" values(?, ?, ?, ?, ?,?, convert(date,?), ?, ?,?) ");
            ps = conn.prepareStatement(query.toString());
            log.info("Object: {}", query.toString());
            ps.setString(1, garantia.getcIdContratoDefinitivo());
            ps.setInt(2, garantia.getListGarantia().get(index).getnTipoGarantia());
            ps.setInt(3, garantia.getnTipoProcesoGarantia());
            ps.setString(4, garantia.getListGarantia().get(index).getcAseguradora());
            ps.setInt(5, garantia.getListGarantia().get(index).getlFianza());
            ps.setInt(6, garantia.getListGarantia().get(index).getlCheque());
            ps.setString(7, garantia.getListGarantia().get(index).getfFechaExpedicion());
            ps.setString(8, garantia.getListGarantia().get(index).getcNumeroChequeFianza());
            ps.setFloat(9, garantia.getListGarantia().get(index).getmMontoGarantia());
            ps.setInt(10, garantia.getListGarantia().get(index).getnIdConsecutivoEndosoGarantia());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query.delete(0, query.length());
            query = null;
        }
        return success;
    }

    public boolean addUsuarioCapGarantiaContrato(Connection conn, GarantiaContrato garantia, int index) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" insert into mUsuarioCapturaGarantia (cIdContratoDefinitivo,nIdtipoProcesoGarantia,cUsuarioCaptura,fFechaCaptura,nIdConsecutivoEndosoGarantia )");
            query.append(" values(?, ?, ?, getDate(),?) ");
            ps = conn.prepareStatement(query.toString());
            log.info("Object: {}", query.toString());
            ps.setString(1, garantia.getcIdContratoDefinitivo());
            ps.setInt(2, garantia.getnTipoProcesoGarantia());
            ps.setString(3, garantia.getcUsuarioCaptura());
            ps.setInt(4, garantia.getListGarantia().get(index).getnIdConsecutivoEndosoGarantia());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query.delete(0, query.length());
            query = null;
        }
        return success;
    }

    public boolean existGarantiaContrato(Connection conn, GarantiaContrato garantia, int index) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        boolean resp = false;
        try {
            query = new StringBuilder();
            query.append("select *from mGarantiasContrato garantia with(Nolock) where garantia.nIdtipoProcesoGarantia=? and garantia.cIdContratoDefinitivo=?  and garantia.nTipoGarantia=? AND nIdConsecutivoEndosoGarantia=?");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, garantia.getnTipoProcesoGarantia());
            ps.setString(2, garantia.getcIdContratoDefinitivo());
            ps.setInt(3, garantia.getListGarantia().get(index).getnTipoGarantia());
            ps.setInt(4, garantia.getListGarantia().get(index).getnIdConsecutivoEndosoGarantia());
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return resp;
    }

    public boolean existUsuarioCapGarantiaContrato(Connection conn, GarantiaContrato garantia, int index) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        boolean resp = false;
        try {
            query = new StringBuilder();
            query.append("select *from mUsuarioCapturaGarantia usuarioGarantia with(Nolock) where  usuarioGarantia.nIdtipoProcesoGarantia=? and usuarioGarantia.cIdContratoDefinitivo=? and nIdConsecutivoEndosoGarantia=?");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, garantia.getnTipoProcesoGarantia());
            ps.setString(2, garantia.getcIdContratoDefinitivo());
            ps.setInt(3, garantia.getListGarantia().get(index).getnIdConsecutivoEndosoGarantia());
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return resp;
    }

    public boolean existGarantiaContrato(Connection conn, GarantiaContrato garantia) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        boolean resp = false;
        try {
            query = new StringBuilder();
            query.append("select *from mGarantiasContrato garantia with(Nolock)where garantia.nIdtipoProcesoGarantia=? and garantia.cIdContratoDefinitivo=? ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, garantia.getnTipoProcesoGarantia());
            ps.setString(2, garantia.getcIdContratoDefinitivo());
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return resp;
    }

    public void contratosConGarantia(Connection conn, ContratosConGarantia contrato) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("select *from mContratosConGarantia cont with(Nolock)where  cont.cIdContratoDefinitivo=?   ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, contrato.getcIdContratoDefinitivo());
            rs = ps.executeQuery();
            while (rs.next()) {
                contrato.setcCodigoContratoCNET(rs.getString("cCodigoContratoCNET"));
                contrato.setcCodigoExpedienteCNET(rs.getString("cCodigoExpedienteCNET"));
                contrato.setcConcepto(rs.getString("cConcepto"));
                contrato.setcEjercicioFiscal(rs.getString("cEjercicioFiscal"));
                contrato.setcFolioCaso(rs.getString("cFolioCaso"));
                contrato.setcIdRFC(rs.getString("cIdRFC"));
                contrato.setcIdUnidadEjecutora(rs.getString("cIdUnidadEjecutora"));
                contrato.setcNumContratoCNET(rs.getString("cNumContratoCNET"));
                contrato.setcNumProcedimientoCNET(rs.getString("cNumProcedimientoCNET"));
                contrato.setfFin(rs.getString("fFin"));
                contrato.setfFormalizacion(rs.getString("fFormalizacion"));
                contrato.setfInicio(rs.getString("fInicio"));
                contrato.setmTotalContrato(rs.getFloat("mTotalContrato"));
                contrato.setmTotalGarantias(rs.getFloat("mTotalGarantias"));
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public boolean existLiberaGarantiaContrato(Connection conn, LiberaGarantiaContrato libGarantia) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        boolean resp = false;
        try {
            query = new StringBuilder();
            query.append("select *from mGarantiacontratoLiberada with(Nolock) where cIdContratoDefinitivo=?  ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, libGarantia.getcIdContratoDefinitivo());
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return resp;
    }

    public boolean addLiberaGarantiaContrato(Connection conn, LiberaGarantiaContrato libGarantia) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" insert into mGarantiacontratoLiberada (cIdContratoDefinitivo,cMotivoLiberacion,cOficioSolicitud ");
            query.append(" ,fFechaSolicitud,cOficioLiberacion,fFechaLiberacion,lchequeEntregadoProveedor,cUsuarioCaptura,fFechaCaptura) ");
            query.append(" values(?,?,?,convert(date,?),?,convert(date,?),?,?,GETDATE()) ");
            ps = conn.prepareStatement(query.toString());
            log.info("Object: {}", query.toString());
            ps.setString(1, libGarantia.getcIdContratoDefinitivo());
            ps.setString(2, libGarantia.getcMotivoLiberacion());
            ps.setString(3, libGarantia.getcOficioSolicitud());
            ps.setString(4, libGarantia.getfFechaSolicitud());
            ps.setString(5, libGarantia.getcOficioLiberacion());
            ps.setString(6, libGarantia.getfFechaLiberacion());
            ps.setInt(7, libGarantia.getlChequeEntregadoProveedor());
            ps.setString(8, libGarantia.getcUsuarioCaptura());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query.delete(0, query.length());
            query = null;
        }
        return success;
    }

    public boolean updateLiberaGarantiaContrato(Connection conn, LiberaGarantiaContrato libGarantia) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update mGarantiacontratoLiberada set cMotivoLiberacion=?");
            query.append(" ,cOficioSolicitud=?,fFechaSolicitud=convert(date,?),cOficioLiberacion=?");
            query.append(" ,fFechaLiberacion=convert(date,?),lchequeEntregadoProveedor=?,cUsuarioActualiza=?");
            query.append(" ,fFechaActualiza=GETDATE() where cIdContratoDefinitivo=?");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, libGarantia.getcMotivoLiberacion());
            ps.setString(2, libGarantia.getcOficioSolicitud());
            ps.setString(3, libGarantia.getfFechaSolicitud());
            ps.setString(4, libGarantia.getcOficioLiberacion());
            ps.setString(5, libGarantia.getfFechaLiberacion());
            ps.setInt(6, libGarantia.getlChequeEntregadoProveedor());
            ps.setString(7, libGarantia.getcUsuarioActualiza());
            ps.setString(8, libGarantia.getcIdContratoDefinitivo());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query.delete(0, query.length());
            query = null;
        }
        return success;
    }

    public void searchDataConvenio(Connection conn, ContratoModificado conv) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select *from mContratoModificado with(nolock) where cIdContratoDefinitivo=? and nConsecutivoModificacion=? ";
        try {
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            ps.setString(1, conv.getcIdContratoDefinitivo());
            ps.setInt(2, conv.getnConsecutivoModificacion());
            rs = ps.executeQuery();
            if (rs.next()) {
                conv.setnEstado(rs.getInt("nEstado"));
                conv.setnConvenioEjercicioAnt(rs.getInt("isConvEjercicioAnt"));
                conv.setcIdRFC(rs.getString("cIdRFC"));
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public boolean updateStateConvenio(Connection conn, ContratoModificado conv) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update mContratoModificado set nEstado=? where cIdContratoDefinitivo=? and nConsecutivoModificacion=?");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, conv.getnEstado());
            ps.setString(2, conv.getcIdContratoDefinitivo());
            ps.setInt(3, conv.getnConsecutivoModificacion());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query.delete(0, query.length());
            query = null;
        }
        return success;
    }

    public boolean updateFolioPrecompromisoConvenio(Connection conn, ContratoModificado conv) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            if (conv.getnFolioPreCompromiso() == 0) {
                query.append("update mContratoModificado set C_FOLIO_PRE=null,ConsecutivoPRECOMP=null where cIdContratoDefinitivo=? and nConsecutivoModificacion=?");
            } else {
                query.append("update mContratoModificado set C_FOLIO_PRE=?,ConsecutivoPRECOMP=? where cIdContratoDefinitivo=? and nConsecutivoModificacion=?");
            }
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            if (conv.getnFolioPreCompromiso() == 0) {
                ps.setString(1, conv.getcIdContratoDefinitivo());
                ps.setInt(2, conv.getnConsecutivoModificacion());
            } else {
                ps.setString(1, conv.getcFolioPreCompromiso());
                ps.setInt(2, conv.getnFolioPreCompromiso());
                ps.setString(3, conv.getcIdContratoDefinitivo());
                ps.setInt(4, conv.getnConsecutivoModificacion());
            }
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query.delete(0, query.length());
            query = null;
        }
        return success;
    }

    public boolean addRelacionPrecomCompromiso(Connection conn, String cIdContratoDef, String cFolioPrecom, int nFolioPrecom) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" insert into mRelPedContPrecomComp (cIdPedContDef,cFolioPrecom,nConsecutivoPrecom,cFolioComp,nConsecutivoComp) ");
            query.append(" values(?,?,?,NULL,NULL) ");
            ps = conn.prepareStatement(query.toString());
            log.info("Object: {}", query.toString());
            ps.setString(1, cIdContratoDef);
            ps.setString(2, cFolioPrecom);
            ps.setInt(3, nFolioPrecom);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query.delete(0, query.length());
            query = null;
        }
        return success;
    }

    public boolean addRelacionPrecom(Connection conn, String cIdContratoDef, String cFolioPrecom, int nFolioPrecom, String cEjercicio, String cCentroContable, String ue) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" insert into mDocumentoFolio (cEjercicio,cIdDocumentoDefinitivo,cCentroContable,cIdUnidadResponsable,C_FOLIO,ConsecutivoCDIV,C_FOLIO_PRE,ConsecutivoPRECOMP) ");
            query.append(" values(?,?,?,?,NULL,NULL,?,?) ");
            ps = conn.prepareStatement(query.toString());
            log.info("Object: {}", query.toString());
            ps.setString(1, cEjercicio);
            ps.setString(2, cIdContratoDef);
            ps.setString(3, cCentroContable);
            ps.setString(4, ue);
            ps.setString(5, cFolioPrecom);
            ps.setInt(6, nFolioPrecom);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query.delete(0, query.length());
            query = null;
        }
        return success;
    }

    public boolean addRetencionConvenio(Connection conn, ContratoModificado conv, int nEsFisica, int nEsMoral, String cEsResico) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" insert into pContratoDiversoRetencion (cEjercicio,cIdEntidadContable,cIdContrato,cIdTipoRetencion) ");
            query.append(" select distinct ?,?,?,");
            query.append(" case when (?=1 and  idRetencion ='4' and ?='S') then '18' else idRetencion end ");
            query.append(" from tRelacionPartidaRetencion with(Nolock) ");
            query.append(" where partida in( ");
            query.append("select distinct cIdSubPartida from mContratoModificadoPartida as part with(Nolock) ");
            query.append(" 		inner join mSolicitudLineas as lin with(Nolock) ");
            query.append(" 		on part.cIdSolicitud=lin.cIdSolicitud ");
            query.append(" 		and part.cIdLineaSolicitud=lin.nIdLineaSolicitud ");
            query.append(" 		where part.cIdContratoDefinitivo=?");
            query.append(" 		and part.nConsecutivoModificacion=?");
            query.append(" 	) ");
            query.append(" and idRetencion not in(select distinct cIdTipoRetencion from pContratoDiversoRetencion where cIdContrato=?) ");
            query.append(" and (cFisica=? or cMoral=?)");
            ps = conn.prepareStatement(query.toString());
            log.info("Object: {}", query.toString());
            ps.setString(1, conv.getcEjercicio());
            ps.setString(2, conv.getcEntidadContable());
            ps.setString(3, conv.getcIdContratoDefinitivo());
            ps.setInt(4, nEsFisica);
            ps.setString(5, cEsResico);
            ps.setString(6, conv.getcIdContratoDefinitivo());
            ps.setInt(7, conv.getnConsecutivoModificacion());
            ps.setString(8, conv.getcIdContratoDefinitivo());
            ps.setInt(9, nEsFisica);
            ps.setInt(10, nEsMoral);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query.delete(0, query.length());
            query = null;
        }
        return success;
    }

    public boolean addRetencionContrato(Connection conn, Contrato cont, int nEsFisica, int nEsMoral, String cEsResico) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" insert into pContratoDiversoRetencion (cEjercicio,cIdEntidadContable,cIdContrato,cIdTipoRetencion) ");
            query.append(" select distinct ?,?,?,");
            query.append(" case when (?=1 and  idRetencion ='4' and ?='S') then '18' else idRetencion end ");
            query.append(" from tRelacionPartidaRetencion with(Nolock) ");
            query.append(" where partida in( ");
            query.append("			select distinct cIdSubPartida from mContratoRemanenteEjercicioAnteriorPartida with(Nolock) where cIdContratoDefinitivo=? ");
            query.append(" 	) ");
            query.append(" and idRetencion not in(select distinct cIdTipoRetencion from pContratoDiversoRetencion where cIdContrato=?) ");
            query.append(" and (cFisica=? or cMoral=?)");
            ps = conn.prepareStatement(query.toString());
            log.info("Object: {}", query.toString());
            ps.setString(1, cont.getcEjercicio());
            ps.setString(2, cont.getcIdEntidadContable());
            ps.setString(3, cont.getcIdContratoDefinitivo());
            ps.setInt(4, nEsFisica);
            ps.setString(5, cEsResico);
            ps.setString(6, cont.getcIdContratoDefinitivo());
            ps.setString(7, cont.getcIdContratoDefinitivo());
            ps.setInt(8, nEsFisica);
            ps.setInt(9, nEsMoral);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query.delete(0, query.length());
            query = null;
        }
        return success;
    }

    public boolean addContratoDiversoConvenio(Connection conn, ContratoDiversoConvenio conv) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("INSERT into pContratoDiversoConvenio( cEjercicio, cIdEntidadContable, cIdContrato, nConsecutivoModificacion,	mConvenio,	mIVAConvenio, ");
            query.append(" mTotal,	mGlobalContrato,	fInicio, fTermino,	fFirmaContrato,		cIdModificacion,cOrigenRM,	fAdjudicacion,cDocumentoAbierto) ");
            query.append(" values(?, ?, ?, ?, ?, ?, ?, ?, convert(date,?), convert(date,?), convert(date,?), ?, ?, convert(date,?), ?) ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, conv.getcEjercicio());
            ps.setString(2, conv.getcIdEntidadContable());
            ps.setString(3, conv.getcIdContrato());
            ps.setInt(4, conv.getnConsecutivoModificacion());
            ps.setDouble(5, conv.getmConvenio());
            ps.setDouble(6, conv.getmIVAConvenio());
            ps.setDouble(7, conv.getmTotal());
            ps.setDouble(8, conv.getmGlobalContrato());
            ps.setString(9, conv.getfInicio());
            ps.setString(10, conv.getfTermino());
            ps.setString(11, conv.getfFirmaContrato());
            ps.setString(12, conv.getcIdModificacion());
            ps.setString(13, conv.getcOrigenRM());
            ps.setString(14, conv.getfAdjudicacion());
            ps.setInt(15, conv.getnDocumentoAbierto());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean updateEstateExtensionContractArt25(Connection conn, String cIdContratoDef, int nEstatus, int nConsecutivoAmpliacion) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update mContratoArt25Ampliacion set nIdEstado=? where cIdContratoDefinitivo=? and nIdConsecutivoAmpliacion=? ");
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, nEstatus);
            ps.setString(2, cIdContratoDef);
            ps.setInt(3, nConsecutivoAmpliacion);
            success = ps.executeUpdate() > 0;
        } finally {
            if (query.length() > 1) {
                query.delete(0, query.length());
            }
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean updateFolioPrecomExtensionContractArt25(Connection conn, String cIdContratoDef, String cFolio, int nConsecutivoAmpliacion, int nfolio) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            if (nfolio == 0) {
                query.append("update mContratoArt25Ampliacion set C_FOLIO_PRE=null,ConsecutivoPRECOMP=null where cIdContratoDefinitivo=? and nIdConsecutivoAmpliacion=? ");
                ps = conn.prepareStatement(query.toString());
                ps.setString(1, cIdContratoDef);
                ps.setInt(2, nConsecutivoAmpliacion);
            } else {
                query.append("update mContratoArt25Ampliacion set C_FOLIO_PRE=?,ConsecutivoPRECOMP=? where cIdContratoDefinitivo=? and nIdConsecutivoAmpliacion=? ");
                ps = conn.prepareStatement(query.toString());
                ps.setString(1, cFolio);
                ps.setInt(2, nfolio);
                ps.setString(3, cIdContratoDef);
                ps.setInt(4, nConsecutivoAmpliacion);
            }
            success = ps.executeUpdate() > 0;
        } finally {
            if (query.length() > 1) {
                query.delete(0, query.length());
            }
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean searchDataExtensionContArt25(Connection conn, ContratoAmpliacion cont) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        boolean resp = false;
        try {
            query = new StringBuilder();
            query.append("select *from mContratoArt25Ampliacion with(Nolock) where cIdContratoDefinitivo=?  and nIdConsecutivoAmpliacion=? ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cont.getcIdContratoDef());
            ps.setInt(2, cont.getnIdConsecutivoAmpliacion());
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
                cont.setcIdUnidadEjecutora(rs.getString("cIdUnidadEjecutora"));
                cont.setcEjercicio(rs.getString("cEjercicio"));
                cont.setnIdEstado(rs.getInt("nIdEstado"));
                cont.setnCantidad(rs.getInt("nCantidad"));
                cont.setmMontoNeto(rs.getDouble("mMontoNeto"));
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return resp;
    }

    public boolean insertContratoEP(Connection conn, String cIdContratoDef, int nConsecutivoAmp) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("insert into tContratoEP (cEjercicio,cIdContrato,cTipoContrato,EP,cIdEntidadContable) ");
            query.append("select amp.cEjercicio,temp.cIdContratoDefinitivo,'DI',temp.cClaveEP,temp.cCentroContable ");
            query.append("from mContratoArt25AmpliacionEP temp with(Nolock)  ");
            query.append("inner join mContratoArt25Ampliacion as amp with(Nolock) ");
            query.append("on amp.cIdContratoDefinitivo=temp.cIdContratoDefinitivo and amp.nIdConsecutivoAmpliacion=temp.nIdConsecutivoAmpliacion ");
            query.append("left join tContratoEP as ep with(Nolock) ");
            query.append("on ep.cIdContrato=temp.cIdContratoDefinitivo and ep.EP=temp.cClaveEP ");
            query.append("where temp.cIdContratoDefinitivo=? and temp.nIdConsecutivoAmpliacion=? and ep.EP is null ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cIdContratoDef);
            ps.setInt(2, nConsecutivoAmp);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean updateMontosDiversoConvenio(Connection conn, ContratoDiversoConvenio conv) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update pContratoDiversoConvenio set mconvenio=?,mIVAConvenio=? ");
            query.append(",mTotal=?,mGlobalContrato=? where cIdContrato=? and cIdModificacion=? ");
            ps = conn.prepareStatement(query.toString());
            ps.setDouble(1, conv.getmConvenio());
            ps.setDouble(2, conv.getmIVAConvenio());
            ps.setDouble(3, conv.getmTotal());
            ps.setDouble(4, conv.getmGlobalContrato());
            ps.setString(5, conv.getcIdContrato());
            ps.setString(6, conv.getcIdModificacion());
            success = ps.executeUpdate() > 0;
        } finally {
            if (query.length() > 1) {
                query.delete(0, query.length());
            }
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean searchMountExtensionContArt25(Connection conn, ContratoAmpliacion cont) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        boolean resp = false;
        try {
            query = new StringBuilder();
            query.append(" select  ");
            query.append(" sum(lin.mMontoNeto)totalAmpliacion ");
            query.append(" ,cast(round(sum(lin.mMontoNeto/(1+(0.01*part.nPorcentajeIVA))),2)as money) subtotalAmpliacion ");
            query.append(" ,cast(round(sum(lin.mMontoNeto)-sum(lin.mMontoNeto/(1+(0.01*part.nPorcentajeIVA))),2)as money) montoIVA ");
            query.append(" from mContratoArt25Ampliacion amp with(Nolock) ");
            query.append(" inner join mContratoArt25AmpliacionLineas as lin with(Nolock) ");
            query.append(" on lin.cIdContratoDefinitivo=amp.cIdContratoDefinitivo ");
            query.append(" and lin.nIdConsecutivoAmpliacion=amp.nIdConsecutivoAmpliacion ");
            query.append(" inner join mContratoArt25Partidas part with(Nolock)  ");
            query.append(" on part.cIdContratoDefinitivo=amp.cIdContratoDefinitivo ");
            query.append(" and part.nIdLineaConsolidado=lin.nIdLineaConsolidado ");
            query.append(" where amp.cIdContratoDefinitivo=?  and amp.nIdConsecutivoAmpliacion=? ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cont.getcIdContratoDef());
            ps.setInt(2, cont.getnIdConsecutivoAmpliacion());
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
                cont.setmMontoNeto(rs.getDouble("totalAmpliacion"));
                cont.setmSubtotal(rs.getDouble("subtotalAmpliacion"));
                cont.setmMontoIVA(rs.getDouble("montoIVA"));
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return resp;
    }

    public boolean addPrecomMaterialesEnc(Connection conn, PrecomMaterialesEncabezado precom) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = " INSERT INTO tPrecomMaterialesEncabezado (nFolioPrecomMateriales, aEjercicioFiscal, fCarga, fAplicacion, cIdConsolidado, cTipoContrato" + ", cCentroContable, cRamo, cUnidadResponsable, caNoPreCompromiso, nEnviadoSICOP, nMes" + ", cTipoPoliza,nStatusFinanciero,fVigencia) " + " VALUES (?, ?, convert(date,getDate()), convert(date,getDate()), ?, ? " + ", ?, ?, ?, ?, ?, MONTH(GETDATE()) " + ", ?, 0,convert(date,(select GETDATE()+(select convert(int,cvalor)  from mSistema WITH(NOLOCK) where cParametro='vigenciaPreCompromisoMateriales') as vigenciaPrecomMateriales)) ) ";
            ps = conn.prepareStatement(query);
            ps.setInt(1, precom.getnFolioPrecomMateriales());
            ps.setString(2, precom.getcEjercicioFiscal());
            ps.setString(3, precom.getcIdConsolidado());
            ps.setString(4, precom.getcTipoContrato());
            ps.setString(5, precom.getcCentroContable());
            ps.setString(6, precom.getcRamo());
            ps.setString(7, precom.getcUnidadResponsable());
            ps.setString(8, precom.getCaNoPreCompromiso());
            ps.setInt(9, precom.getnEnviadoSICOP());
            ps.setString(10, precom.getcTipoPoliza());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean addPrecomMaterialesDet(Connection conn, String cIdConsolidado, int nFolioPreCompromiso, String cCentroContable) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = " INSERT INTO tPrecomMaterialesDetalle (nFolioPrecomMateriales,nDocRenglon,EP,cEvento,mImporte,mImporteNegativo,cMes,cCentroContable) " + " select a.nFolio,a.nDocRenglon,a.ep,a.cEvento, a.mImporte, a.mImporteNegativo, a.mes, a.centroContable " + " from fn_mPrecompromisoConsolidado(?,?,?) a ";
            ps = conn.prepareStatement(query);
            ps.setString(1, cIdConsolidado);
            ps.setInt(2, nFolioPreCompromiso);
            ps.setString(3, cCentroContable);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean updateFolioConsolidado(Connection conn, int nFolioPrecom, String cFolioPrecom, String cIdConsolidado) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("UPDATE mConsolidado SET ConsecutivoPRECOMP = ?, C_FOLIO_PRE = ? ");
            query.append(" where cIdConsolidado=? ");
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, nFolioPrecom);
            ps.setString(2, cFolioPrecom);
            ps.setString(3, cIdConsolidado);
            success = ps.executeUpdate() > 0;
        } finally {
            if (query.length() > 1) {
                query.delete(0, query.length());
            }
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public void registraVigenciaPrecomMateriales(Connection conn, String cFolioPrecom, String cIdConsolidado, String ue, String cEjercicio) throws Exception {
        CallableStatement cmst = null;
        try {
            cmst = conn.prepareCall("{call sp_mVigenciaPrecompromiso (?,?,?,?,?)}");
            cmst.setString(1, cEjercicio);
            cmst.setString(2, ue);
            cmst.setString(3, cIdConsolidado);
            cmst.setString(4, cFolioPrecom);
            cmst.setDate(5, Date.valueOf("2014-01-01"));
            cmst.execute();
        } finally {
            CloseObject.closeObject(cmst, false);
        }
    }

    public void registraPartidasPrecomMateriales(Connection conn, String cIdConsolidado) throws Exception {
        CallableStatement cmst = null;
        try {
            cmst = conn.prepareCall("{call sp_mPartidasPrecompromiso (?)}");
            cmst.setString(1, cIdConsolidado);
            cmst.execute();
        } finally {
            CloseObject.closeObject(cmst, false);
        }
    }

    public boolean searchConsolidadoEnProcedimiento(Connection conn, Procedimiento proced) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        boolean resp = false;
        try {
            query = new StringBuilder();
            query.append(" select *from mProcedimiento with(Nolock) where nIdEstado in(1,2)  and cIdConsolidado=?");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, proced.getcIdConsolidado());
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
                proced.setnIdEstado(rs.getInt("nIdEstado"));
                proced.setcIdProcedimiento(rs.getString("cIdProcedimiento"));
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return resp;
    }

    public boolean existProveedor(Connection conn, Procedimiento proced) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        boolean resp = false;
        try {
            query = new StringBuilder();
            query.append(" select *from  mCatalogoProveedor cp with(nolock) where cp.alta_rapida='N' and cp.lHabilitado=1 and replace(cp.cIdRFC,'-','')=replace(?,'-','')");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, proced.getcIdRFC());
            rs = ps.executeQuery();
            if (rs.next()) {
                proced.setcIdRFC(rs.getString("cIdRFC"));
                resp = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return resp;
    }

    public boolean getTipoProcedimiento(Connection conn, Procedimiento proced) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        boolean resp = false;
        try {
            query = new StringBuilder();
            query.append(" select ctp.cIdTipoProcedimiento from  mConsolidado c with(nolock) ");
            query.append(" INNER JOIN mCatalogoTipoConsolidado ctc  with(nolock) ON c.cIdTipoConsolidado = ctc.cIdTipoConsolidado ");
            query.append(" INNER JOIN mCatalogoTipoProcedimiento ctp with(nolock) ON ctc.nTipoConsolidado = ctp.nTipoProcedimiento ");
            query.append(" where c.cIdConsolidado=? ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, proced.getcIdConsolidado());
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
                proced.setcIdTipoProcedimiento(rs.getString("cIdTipoProcedimiento"));
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return resp;
    }

    public boolean getConsecutivoProcedimiento(Connection conn, Procedimiento proced) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        boolean resp = false;
        try {
            query = new StringBuilder();
            query.append(" select nIdConsecutivo as nIdConsecutivo from fn_GetConsecutivoProcedimiento(?,?,?) ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, proced.getcEjercicio());
            ps.setString(2, proced.getcIdTipoProcedimiento());
            ps.setString(3, proced.getcIdUnidadEjecutora());
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
                proced.setnIdConsecutivo(rs.getInt("nIdConsecutivo"));
                proced.setcIdProcedimiento(proced.getcIdTipoProcedimiento() + "-" + proced.getcIdUnidadEjecutora() + "-" + rs.getInt("nIdConsecutivo"));
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return resp;
    }

    public void generaProcedimiento(Connection conn, Procedimiento proced) throws Exception {
        CallableStatement cmst = null;
        int tipoProceso = 1;
        int autComite = 1;
        try {
            cmst = conn.prepareCall("{call sp_mProcedimiento (?,?,?,?,?,?,?,?,?,?,?,?,?,? ,?,?) }");
            //
            cmst.setString(1, proced.getcEjercicio());
            cmst.setString(2, proced.getcIdTipoProcedimiento());
            cmst.setString(3, proced.getcIdUnidadEjecutora());
            cmst.setInt(4, proced.getnIdConsecutivo());
            //
            cmst.setString(5, proced.getcIdTipoConsolidado());
            ///////
            cmst.setInt(6, proced.getnIdConsecutivoConsolidado());
            cmst.setInt(7, proced.getnIdCategoria());
            //
            cmst.setString(8, proced.getcDescripcion());
            //
            cmst.setString(9, proced.getcUsuarioCrea());
            cmst.setInt(10, ProcedimientoStatus.ADJUDICADO);
            //
            cmst.setString(11, proced.getcIdEntidadContable());
            cmst.setString(12, proced.getcNoProcedCNET());
            cmst.setInt(13, tipoProceso);
            cmst.setInt(14, autComite);
            cmst.setInt(15, 16);
            cmst.setInt(16, proced.getnEsPlurianual());
            cmst.execute();
        } finally {
            CloseObject.closeObject(cmst, false);
        }
    }

    public int validateMontoRequiMontoMinimoMaximo(Connection conn, Procedimiento proced) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        int resp = 0;
        try {
            query = new StringBuilder();
            query.append(" select  ");
            query.append(" adj.lContratoAbierto ");
            query.append(" ,proced.isPlurianual ");
            query.append(" ,SUM(part.mMontoNetoLineaMax)totalMax ");
            query.append(" ,SUM(part.mMontoNetoLinea)mMontoNetoLinea ");
            query.append(" ,SUM(part.mMontoNetoMinimo)mMontoNetoMinimo ");
            query.append(" ,sum(part.mMontoNetoPluri)totalPlurilinea ");
            query.append(" ,adj.mMontoTotalPlurianual,adj.nIdconsecutivoAdj ");
            query.append(" from mProcedimientoAdjudicacion adj with(Nolock) ");
            query.append(" inner join mProcedimientoAdjudicacionPartidas as part with(Nolock) ");
            query.append(" on adj.cIdProcedimiento=part.cIdProcedimiento ");
            query.append(" and adj.cIdRFC=part.cIdRFC ");
            query.append(" and adj.nIdconsecutivoAdj=part.nIdconsecutivoAdj ");
            query.append(" inner join mProcedimiento as proced with(Nolock) ");
            query.append(" on adj.cIdProcedimiento=proced.cIdProcedimiento ");
            query.append(" where part.cIdConsolidado=? and adj.cIdProcedimiento=? ");
            query.append(" group by adj.lContratoAbierto,adj.mMontoTotalPlurianual ");
            query.append(" ,proced.isPlurianual,adj.nIdconsecutivoAdj ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, proced.getcIdConsolidado());
            ps.setString(2, proced.getcIdProcedimiento());
            rs = ps.executeQuery();
            if (rs.next()) {
                proced.setnIdconsecutivoAdj(rs.getInt("nIdconsecutivoAdj"));
                if (rs.getInt("lContratoAbierto") == 1) {
                    if (rs.getDouble("mMontoNetoLinea") > rs.getDouble("totalMax"))
                        resp = 1;
                    if (rs.getDouble("mMontoNetoLinea") == rs.getDouble("totalMax"))
                        resp = 3;
                    else if (rs.getDouble("mMontoNetoLinea") > rs.getDouble("mMontoNetoMinimo"))
                        resp = 2;
                } else {
                    if (rs.getDouble("mMontoNetoLinea") > rs.getDouble("mMontoNetoMinimo"))
                        resp = 2;
                }
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return resp;
    }

    public int generaPedidoContrato(Connection conn, Procedimiento proced) throws Exception {
        CallableStatement cmst = null;
        //proceso corto
        int tipoProceso = 1;
        int outputValue = -1;
        try {
            cmst = conn.prepareCall("{call pa_mGeneraPedidosyContratos (?,?,?,?,?,?)}");
            cmst.setString(1, proced.getcEjercicio());
            cmst.setString(2, proced.getcIdTipoProcedimiento());
            cmst.setString(3, proced.getcIdUnidadEjecutora());
            cmst.setInt(4, proced.getnIdConsecutivo());
            cmst.registerOutParameter(5, Types.INTEGER);
            cmst.setInt(6, tipoProceso);
            cmst.execute();
            outputValue = cmst.getInt(5);
        } finally {
            CloseObject.closeObject(cmst, false);
        }
        return outputValue;
    }

    public int getnConsecutivoContrato(Connection conn, Procedimiento proced, String cIdTipoContrato) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        int nConsecutivoCont = -1;
        try {
            query = new StringBuilder();
            query.append(" select (ISNULL(MAX(nIdConsecutivo),0) + 1)nConsecutivoCont from mContrato as cont with(Nolock) WHERE cIdUnidadEjecutora = ? and cIdTipoContrato = ?");
            log.info("Object: {}", query.toString());
            log.info("Parametros : ");
            log.info("Object: {}", "1, " + proced.getcIdUnidadEjecutora());
            log.info("Object: {}", " 2, " + cIdTipoContrato);
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, proced.getcIdUnidadEjecutora());
            ps.setString(2, cIdTipoContrato);
            rs = ps.executeQuery();
            if (rs.next()) {
                nConsecutivoCont = rs.getInt("nConsecutivoCont");
                proced.setnConsecutivoContrato(nConsecutivoCont);
            }
        } finally {
        }
        return nConsecutivoCont;
    }

    public void saldoPrecomMateriales(Connection conn, Procedimiento proced) throws Exception {
        CallableStatement cmst = null;
        try {
            cmst = conn.prepareCall("{call InsertmSaldoPrecompromisoMateriales (?,?,?,?)}");
            cmst.setString(1, proced.getcEjercicio());
            cmst.setString(2, proced.getcIdTipoProcedimiento());
            cmst.setString(3, proced.getcIdUnidadEjecutora());
            cmst.setInt(4, proced.getnIdConsecutivo());
            cmst.execute();
        } finally {
            CloseObject.closeObject(cmst, false);
        }
    }

    public boolean updateComprometeMaximo(Connection conn, Procedimiento proced) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update mProcedimientoAdjudicacion set lComprometeMaximo=? where cIdProcedimiento=? and cIdRFC=? ");
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, proced.getnComprometeMaximo());
            ps.setString(2, proced.getcIdProcedimiento());
            ps.setString(3, proced.getcIdRFC());
            success = ps.executeUpdate() > 0;
        } finally {
            if (query.length() > 1) {
                query.delete(0, query.length());
            }
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean getContrato(Connection conn, Contrato cont) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        boolean resp = false;
        try {
            query = new StringBuilder();
            query.append(" select *from mContrato as cont with(Nolock) where cIdProcedimiento=? and cIdRFC=? and nIdconsecutivoAdj=? ");
            log.info("Object: {}", query.toString());
            log.info("Parametros : ");
            log.info("Object: {}", "1, " + cont.getcIdProcedimiento());
            log.info("Object: {}", " 2, " + cont.getcIdRFC());
            log.info("Object: {}", " 3, " + cont.getnIdconsecutivoAdj());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cont.getcIdProcedimiento());
            ps.setString(2, cont.getcIdRFC());
            ps.setInt(3, cont.getnIdconsecutivoAdj());
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
                cont.setcIdContratoDefinitivo(rs.getString("cIdContratoDefinitivo"));
                cont.setcIdContrato(rs.getString("cIdContrato"));
                cont.setcIdtipoContrato(rs.getString("cIdTipoContrato"));
                cont.setcIdUnidadEjecutora(rs.getString("cIdUnidadEjecutora"));
                cont.setnIdConsecutivo(rs.getInt("nIdConsecutivo"));
                cont.setcEjercicio(rs.getString("cEjercicio"));
                cont.setfEntrega(rs.getString("fFin"));
                cont.setfFallo(rs.getString("fFallo"));
                cont.setfFin(rs.getString("fFin"));
                cont.setfFormalizacion(rs.getString("fFormalizacion"));
                cont.setfInicio(rs.getString("fInicio"));
                cont.setfPropuestas(rs.getString("fPropuestas"));
                cont.setfSolicitud(rs.getString("fSolicitud"));
                cont.setcConceptoContrato(rs.getString("cConceptoContrato"));
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return resp;
    }

    public int apruebaContrato(Connection conn, Contrato cont) throws Exception {
        CallableStatement cmst = null;
        int outputValue = -1;
        try {
            cmst = conn.prepareCall("{?= call pa_apruebaContrato(?,?,?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, cont.getcEjercicio());
            cmst.setString(3, cont.getcIdContrato());
            cmst.setString(4, cont.getcIdUnidadEjecutora());
            cmst.setString(5, cont.getnConsecutivoCDIV() + "");
            cmst.setString(6, cont.getcFOLIO());
            cmst.execute();
            outputValue = cmst.getInt(1);
        } finally {
            CloseObject.closeObject(cmst, false);
        }
        return outputValue;
    }

    public boolean addPrecompromisoEncTMP(Connection conn, int nFolioPreCompromiso) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = " INSERT INTO tPreCompromisoEncabezado_materialesTmp select * from tPreCompromisoEncabezado with(nolock) where nFolioPrecompromiso = ? ";
            ps = conn.prepareStatement(query);
            ps.setInt(1, nFolioPreCompromiso);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean addPrecompromisoDetTMP(Connection conn, int nFolioPreCompromiso) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = " INSERT INTO tPreCompromisoDetalle_materialesTmp (nFolioPreCompromiso,nDocRenglon,EP,cEvento,mImporte,mImporteNegativo,cMes,cCentroContable)  " + " select * from tPreCompromisoDetalle with(nolock) where nFolioPrecompromiso = ?";
            ps = conn.prepareStatement(query);
            ps.setInt(1, nFolioPreCompromiso);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean addPrecompromisoDet(Connection conn, Contrato cont) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = " insert into tPreCompromisoDetalle (nFolioPreCompromiso,nDocRenglon,EP,cEvento,mImporte,mImporteNegativo,cMes,cCentroContable) " + " select * from  dbo.fn_mAptdPrcpDetalle (?,?,?,?,?)";
            ps = conn.prepareStatement(query);
            ps.setString(1, cont.getcIdProcedimiento());
            ps.setString(2, cont.getcIdRFC());
            ps.setInt(3, cont.getnConsecutivoPRECOMP());
            ps.setString(4, cont.getcEvento());
            ps.setInt(5, cont.getnIdconsecutivoAdj());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean addContratoDefinitivo(Connection conn, Procedimiento proced) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = " INSERT INTO mContratoDefinitivo (cIdContratoDefinitivo,cEjercicio,cIdTipoContrato,cIdUnidadEjecutora,nIdConsecutivo)  VALUES(?,?,?,?,?)";
            ps = conn.prepareStatement(query);
            ps.setString(1, proced.getcIdContratoDefinitivo());
            ps.setString(2, proced.getcEjercicio());
            ps.setString(3, proced.getcIdTipoContrato());
            ps.setString(4, proced.getcIdUnidadEjecutora());
            ps.setInt(5, proced.getnConsecutivoContrato());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean addNewContrato(Connection conn, Procedimiento proced) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        int nIdFechaFallo = 2;
        int nIdFechaInicio = 17;
        int nIdFechaFin = 18;
        int nIdFechaFormalizacion = 12;
        int nIdFechaSolContratacion = 1;
        //int nIdFechaCAAS=19;
        try {
            query = new StringBuilder();
            query.append(" INSERT INTO mContrato (cEjercicio,cIdTipoContrato,cIdUnidadEjecutora ");
            query.append(" ,nIdConsecutivo,cIdTipoProcedimiento,nIdConsecutivoProcedimiento,cIdRFC ");
            query.append(" ,cConceptoContrato,nIdEstado,nIdTipoCambio,mTipoCambio,cIdContratoDefinitivo ");
            query.append(" ,fFallo,fInicio,fFin,cIdEntidadContable,nIVA,cIdUsuarioCreacion,fFormalizacion ");
            query.append(" ,fSolicitud,fPropuestas,fEntrega,cNumCuentaDisp,nIdconsecutivoAdj) ");
            query.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,? ");
            query.append(" ,(isnull((select fecha from mProcedimientoFechas with(nolock) where nIdProcedimiento=? and nIdFecha=?),GETDATE()) )");
            query.append(" ,(isnull((select fecha from mProcedimientoFechas with(nolock) where nIdProcedimiento=? and nIdFecha=?),GETDATE()) )");
            query.append(" ,(isnull((select fecha from mProcedimientoFechas with(nolock) where nIdProcedimiento=? and nIdFecha=?),GETDATE()) )");
            query.append(" ,?,?,?,(isnull((select fecha from mProcedimientoFechas with(nolock) where nIdProcedimiento=? and nIdFecha=?),GETDATE()) ) ");
            query.append(" ,(isnull((select fecha from mProcedimientoFechas with(nolock) where nIdProcedimiento=? and nIdFecha=?),GETDATE()) )");
            query.append(" ,GETDATE()+1,(isnull((select fecha from mProcedimientoFechas with(nolock) where nIdProcedimiento=? and nIdFecha=?),GETDATE()) )");
            query.append(" ,?,?)");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, proced.getcEjercicio());
            ps.setString(2, proced.getcIdTipoContrato());
            ps.setString(3, proced.getcIdUnidadEjecutora());
            ps.setInt(4, proced.getnConsecutivoContrato());
            ps.setString(5, proced.getcIdTipoProcedimiento());
            ps.setInt(6, proced.getnIdConsecutivo());
            ps.setString(7, proced.getcIdRFC());
            ps.setString(8, proced.getcDescripcion());
            ps.setInt(9, ContractStatus.CAPTURED);
            ps.setString(10, "01");
            ps.setDouble(11, 1.00);
            ps.setString(12, proced.getcIdContratoDefinitivo());
            ps.setString(13, proced.getcIdProcedimiento());
            ps.setInt(14, nIdFechaFallo);
            ps.setString(15, proced.getcIdProcedimiento());
            ps.setInt(16, nIdFechaInicio);
            ps.setString(17, proced.getcIdProcedimiento());
            ps.setInt(18, nIdFechaFin);
            ps.setString(19, proced.getcIdEntidadContable());
            ps.setInt(20, proced.getnPorcentajeIVA());
            ps.setString(21, proced.getcUsuarioCrea());
            ps.setString(22, proced.getcIdProcedimiento());
            ps.setInt(23, nIdFechaFormalizacion);
            ps.setString(24, proced.getcIdProcedimiento());
            ps.setInt(25, nIdFechaSolContratacion);
            ps.setString(26, proced.getcIdProcedimiento());
            ps.setInt(27, nIdFechaFin);
            ps.setString(28, "82106");
            ps.setInt(29, proced.getnIdconsecutivoAdj());
            success = ps.executeUpdate() > 0;
        } finally {
            if (query.length() > 1) {
                query.delete(0, query.length());
            }
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean addPartidasContrato(Connection conn, Procedimiento proced) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" INSERT INTO mContratoPartidas SELECT cEjercicio, ?, cIdUnidadEjecutora, ?, cIdTipoConsolidado, nIdConsecutivoConsolidado, nIdLineaConsolidado, ");
            query.append(" cIdTipoProcedimiento, nIdConsecutivo ");
            query.append(" FROM   dbo.fn_mProcedimientoPartidas(?,?,?,?) ");
            query.append("  WHERE cIdRFC = ? and nIdconsecutivoAdj=? ");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, proced.getcIdTipoContrato());
            ps.setInt(2, proced.getnConsecutivoContrato());
            ps.setString(3, proced.getcEjercicio());
            ps.setString(4, proced.getcIdTipoProcedimiento());
            ps.setString(5, proced.getcIdUnidadEjecutora());
            ps.setInt(6, proced.getnIdConsecutivo());
            ps.setString(7, proced.getcIdRFC());
            ps.setInt(8, proced.getnIdconsecutivoAdj());
            success = ps.executeUpdate() > 0;
        } finally {
            if (query.length() > 1) {
                query.delete(0, query.length());
            }
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean addContratoProrrateo(Connection conn, Procedimiento proced) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" INSERT INTO mContratoProrrateo  SELECT cEjercicio,? cIdTipoPedido ");
            query.append(" ,cIdUnidadEjecutora,? nIdConsecutivoPedido,cIdTipoConsolidado,nIdConsecutivoConsolidado ");
            query.append(" ,nIdLineaConsolidado ,cIdTipoProcedimiento,nIdConsecutivoProcedimiento,cIdRFC  ");
            query.append(" ,nCantidadTotal,cIdTipoSolicitud,cIdUnidadEjecutoraSolicitud,nIConsecutivoSolictud ");
            query.append(" ,nIdLineaSolicitud,cIdEntidadContableSolicitud,cAlmacen,nCantidadLineaSolicitud,P1  ");
            query.append(" ,mMontoNetoLinea ,nIdClaveEgresos,nIdMes,mClave ,P2,mMontoMinimo  ");
            query.append(" ,P1 * P2 AS 'P3=P1*P2'  ");
            query.append(" ,CAST(mMontoMinimo AS DECIMAL(27,18)) * CAST(P1 AS DECIMAL(27,18)) * CAST(P2 AS DECIMAL(27,18)) * CAST(1.00 AS DECIMAL(27,18)) AS mClavePedido  ");
            query.append(" FROM fn_mPedidoProrrateo (? ,? ,? ,? ,?) ");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, proced.getcIdTipoContrato());
            ps.setInt(2, proced.getnConsecutivoContrato());
            ps.setString(3, proced.getcEjercicio());
            ps.setString(4, proced.getcIdTipoProcedimiento());
            ps.setString(5, proced.getcIdUnidadEjecutora());
            ps.setInt(6, proced.getnIdConsecutivo());
            ps.setString(7, proced.getcIdRFC());
            success = ps.executeUpdate() > 0;
        } finally {
            if (query.length() > 1) {
                query.delete(0, query.length());
            }
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public int complementaProrrateo(Connection conn, String cEjercicio, String cTipoContrato, String cIdUnidadEjecutora, int nConsecutivoCont) throws Exception {
        CallableStatement cmst = null;
        int outputValue = -1;
        try {
            cmst = conn.prepareCall("{call pa_mContratoComplementaProrrateo(?,?,?,?)}");
            cmst.setString(1, cEjercicio);
            cmst.setString(2, cTipoContrato);
            cmst.setString(3, cIdUnidadEjecutora);
            cmst.setInt(4, nConsecutivoCont);
            cmst.execute();
        } finally {
            CloseObject.closeObject(cmst, false);
        }
        return outputValue;
    }

    public void agregaRetencionAutomatico(Connection conn, Contrato cont) throws Exception {
        CallableStatement cmst = null;
        try {
            cmst = conn.prepareCall("{call pa_mAgreRetenAutomatico (?,?,?,?,?,?,?,?)}");
            cmst.setString(1, cont.getcEjercicio());
            cmst.setString(2, cont.getcIdUnidadEjecutora());
            cmst.setString(3, cont.getcIdtipoProcedimiento());
            cmst.setInt(4, cont.getnIdConsecutivoProcedimiento());
            cmst.setString(5, cont.getcIdRFC());
            cmst.setString(6, cont.getcIdContratoDefinitivo());
            cmst.setString(7, cont.getcIdEntidadContable());
            cmst.setInt(8, cont.getnIdconsecutivoAdj());
            cmst.execute();
        } finally {
            CloseObject.closeObject(cmst, false);
        }
    }

    public boolean agregaRetencionISR(Connection conn, String cIdContratoDef) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" insert into pContratoDiversoRetencion ");
            query.append(" select cEjercicio,cont.cIdEntidadContable,cIdContratoDefinitivo ");
            query.append(" ,case when prov.cEsRESICO='S' then 18 else 4 end ");
            query.append(" from mContrato as cont with(Nolock) ");
            query.append(" inner join mCatalogoProveedor as prov with(Nolock)");
            query.append(" on prov.cIdRFC=cont.cIdRFC where cIdContratoDefinitivo=?");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cIdContratoDef);
            success = ps.executeUpdate() > 0;
        } finally {
            if (query.length() > 1) {
                query.delete(0, query.length());
            }
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean agregaRetencionIVAHonorarios(Connection conn, String cIdContratoDef) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" insert into pContratoDiversoRetencion ");
            query.append(" select cEjercicio,cont.cIdEntidadContable,cIdContratoDefinitivo ");
            query.append(" ,12 ivaHonorarios ");
            query.append(" from mContrato as cont with(Nolock) ");
            query.append(" inner join mCatalogoProveedor as prov with(Nolock)");
            query.append(" on prov.cIdRFC=cont.cIdRFC where cIdContratoDefinitivo=?");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cIdContratoDef);
            success = ps.executeUpdate() > 0;
        } finally {
            if (query.length() > 1) {
                query.delete(0, query.length());
            }
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean agregaRetencion(Connection conn, String cIdContratoDef, int nIdTipoRetencion) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" insert into pContratoDiversoRetencion ");
            query.append(" select cEjercicio,cont.cIdEntidadContable,cIdContratoDefinitivo ");
            query.append(" ,? ");
            query.append(" from mContrato as cont with(Nolock) ");
            query.append(" where cIdContratoDefinitivo=?");
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, nIdTipoRetencion);
            ps.setString(2, cIdContratoDef);
            success = ps.executeUpdate() > 0;
        } finally {
            if (query.length() > 1) {
                query.delete(0, query.length());
            }
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean execQuery(Connection conn, String query) throws SQLException {
        boolean resp = false;
        Statement stmEnc = null;
        int n = 0;
        try {
            log.info("Object: {}", query.toString());
            stmEnc = conn.createStatement();
            n = stmEnc.executeUpdate(query);
            if (n > 0)
                resp = true;
        } finally {
            if (stmEnc != null)
                stmEnc.close();
            stmEnc = null;
        }
        return resp;
    }

    public void registraRemanenteContratoDiverso(Connection conn, String cIdContratoDef, String cLogin) throws Exception {
        CallableStatement cmst = null;
        try {
            cmst = conn.prepareCall("{call sp_RegistroRemanente (?,?)}");
            cmst.setString(1, cIdContratoDef);
            cmst.setString(2, cLogin);
            cmst.execute();
        } finally {
            CloseObject.closeObject(cmst, false);
        }
    }
}
