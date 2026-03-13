package com.axtel.contratos.core;

import java.lang.reflect.InvocationTargetException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import com.axtel.contratos.PrecomprometidoStatus;
import com.axtel.contratos.Requisition;
import com.axtel.contratos.RequisitionStatus;
import com.axtel.contratos.RequisitionType;
import com.syc.cfdi.db.CloseObject;
import com.syc.contable.AccountingEngine;
import com.syc.contable.AccountingEngineException;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.RSToTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequisitionManager {

    private static final Logger log = LogManager.getLogger(RequisitionManager.class);

    private static StringBuilder querySel = new StringBuilder();

    static {
        querySel.append("SELECT	C_FOLIO_APA AS folioApartado, ");
        querySel.append("		cDescripcion AS descripcion, ");
        querySel.append("		cEjercicio AS ejercicio, ");
        querySel.append("		cIdAlmacen AS idAlmacen, ");
        querySel.append("		cIdAlmacenEntrega AS idAlmacenEntrega, ");
        querySel.append("		cIdEntidadContable AS idEntidadContable, ");
        querySel.append("		cIdFuenteFinanciamiento AS idFuenteFinanciamiento, ");
        querySel.append("		cIdSolicitud AS idSolicitud, ");
        querySel.append("		cIdSubPartida AS idSubPartida, ");
        querySel.append("		cIdTipoSolicitud AS idTipoSolicitud, ");
        querySel.append("		cIdUnidadEjecutora AS idUnidadEjecutora, ");
        querySel.append("		cIdUsuarioAnulacion AS idUsuarioAnulacion, ");
        querySel.append("		cIdUsuarioAprobacion AS idUsuarioAprobacion, ");
        querySel.append("		cIdUsuarioCreacion AS idUsuarioCreacion, ");
        querySel.append("		cNumCuentaDisp AS cuentaDisp, ");
        querySel.append("		cObservaciones AS observaciones, ");
        querySel.append("		ConsecutivoAPARTADO AS consecutivoApartado, ");
        querySel.append("		cPlurianualidad AS plurianualidad, ");
        querySel.append("		cTipoGarantia AS tipoGarantia, ");
        querySel.append("		facturarA AS facturarA, ");
        querySel.append("		convert( date, fAnulacion,103) AS fechaAnulacion, ");
        querySel.append("		convert( date,fAprobacion,103) AS fechaAprobacion, ");
        querySel.append("		convert( date,fCreacion,103) AS fechaCreacion, ");
        querySel.append("		convert( date,fRequerida,103) AS fechaRequerida, ");
        querySel.append("		convert( date,fSolicitud,103) AS fechaSolicitud, ");
        querySel.append("		lAnexos AS contieneAnexos, ");
        querySel.append("		mImportePoliza AS importePoliza, ");
        querySel.append("		mNotas AS notas, ");
        querySel.append("		mPorcentajeGarantia AS porcentajeGarantia, ");
        querySel.append("		nIdAlcance AS idAlcance, ");
        querySel.append("		nIdCategoria AS idCategoria, ");
        querySel.append("		nIdConsecutivo AS idConsecutivo, ");
        querySel.append("		nIdEstado AS idEstado, ");
        querySel.append("		nIdEstadoPrecomprometido AS idEstadoPrecomprometido, ");
        querySel.append("		nIdPeriodo AS idPeriodo, ");
        querySel.append("		nIdPlazo AS idPlazo, ");
        querySel.append("		nIdTipoGarantia AS idTipoGarantia, ");
        querySel.append("		isnull(cuestionario.applyQuestionnaire,'N') applyQuestionnaire, ");
        querySel.append("		isnull(aplicaQuest.cAplica15D,'N') cAplica15D ");
        querySel.append("  FROM	mSolicitud with(Nolock) ");
        querySel.append(" left join(SELECT	nStatus AS applyQuestionnaire,cpartida FROM	tPartida15D WITH(NOLOCK) where nStatus='S' ");
        querySel.append(" )cuestionario on cuestionario.cpartida=mSolicitud.cIdSubPartida ");
        querySel.append(" left join (select cIdSolicitud solicitud,cAplica15D from tCuestionarioRequisicionFIEL with(Nolock) )aplicaQuest ");
        querySel.append(" on aplicaQuest.solicitud=mSolicitud.cIdSolicitud ");
    }

    public static int changeLinesStatusCancel(Connection conn, Requisition requisition) throws SQLException {
        StringBuilder queryUpdate = new StringBuilder();
        queryUpdate.append("UPDATE mSolicitudLineas  ");
        queryUpdate.append("   SET cIdEstadoLinea = ?  ");
        queryUpdate.append(" WHERE cEjercicio = ?  ");
        queryUpdate.append("   AND cIdTipoSolicitud = ? ");
        queryUpdate.append("   AND cIdUnidadEjecutora = ?  ");
        queryUpdate.append("   AND nIdConsecutivo = ? ");
        PreparedStatement ps = null;
        int updatedRecords = 0;
        try {
            int index = 1;
            ps = conn.prepareStatement(queryUpdate.toString());
            ps.setString(index++, "C");
            ps.setString(index++, requisition.getEjercicio());
            ps.setString(index++, requisition.getIdTipoSolicitud());
            ps.setString(index++, requisition.getIdUnidadEjecutora());
            ps.setInt(index++, requisition.getIdConsecutivo());
            updatedRecords = ps.executeUpdate();
            return updatedRecords;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int changeStatusCancel(Connection conn, Requisition requisition) throws SQLException {
        StringBuilder queryUpdate = new StringBuilder();
        queryUpdate.append("UPDATE mSolicitud ");
        queryUpdate.append("   SET mNotas=?, ");
        queryUpdate.append("      	fAnulacion = ?, ");
        queryUpdate.append("      	nIdEstado = ?, ");
        queryUpdate.append("      	cIdUsuarioAnulacion = ? ");
        queryUpdate.append(" WHERE cEjercicio = ? ");
        queryUpdate.append("   AND cIdTipoSolicitud = ? ");
        queryUpdate.append("   AND cIdUnidadEjecutora = ? ");
        queryUpdate.append("   AND nIdConsecutivo = ?");
        PreparedStatement ps = null;
        int updatedRecords = 0;
        try {
            int index = 1;
            ps = conn.prepareStatement(queryUpdate.toString());
            ps.setString(index++, requisition.getNotas());
            ps.setDate(index++, new java.sql.Date(System.currentTimeMillis()));
            ps.setInt(index++, RequisitionStatus.CANCELED);
            ps.setString(index++, requisition.getIdUsuarioAnulacion());
            ps.setString(index++, requisition.getEjercicio());
            ps.setString(index++, requisition.getIdTipoSolicitud());
            ps.setString(index++, requisition.getIdUnidadEjecutora());
            ps.setInt(index++, requisition.getIdConsecutivo());
            updatedRecords = ps.executeUpdate();
            return updatedRecords;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int setRequisitionStatus(Connection conn, Requisition requisition, int status, int statusPrecom) throws SQLException {
        StringBuilder queryUpdate = new StringBuilder();
        queryUpdate.append("UPDATE mSolicitud ");
        queryUpdate.append("   SET nIdEstado = ?, ");
        queryUpdate.append("      	nIdEstadoPrecomprometido = ?, ");
        queryUpdate.append("      	cIdUsuarioAprobacion = ? ");
        queryUpdate.append(" WHERE cEjercicio = ? ");
        queryUpdate.append("   AND cIdTipoSolicitud = ? ");
        queryUpdate.append("   AND cIdUnidadEjecutora = ? ");
        queryUpdate.append("   AND nIdConsecutivo = ?");
        PreparedStatement ps = null;
        int updatedRecords = 0;
        try {
            int index = 1;
            ps = conn.prepareStatement(queryUpdate.toString());
            ps.setInt(index++, status);
            ps.setInt(index++, statusPrecom);
            ps.setString(index++, StringUtils.trimToEmpty(requisition.getIdUsuarioAprobacion()));
            ps.setString(index++, requisition.getEjercicio());
            ps.setString(index++, requisition.getIdTipoSolicitud());
            ps.setString(index++, requisition.getIdUnidadEjecutora());
            ps.setInt(index++, requisition.getIdConsecutivo());
            updatedRecords = ps.executeUpdate();
            return updatedRecords;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int authRequisition(Connection conn, Requisition requisition) throws SQLException {
        if (RequisitionType.DE_TIENDA_DIGITAL.equalsIgnoreCase(requisition.getIdTipoSolicitud())) {
            return RequisitionManager.setRequisitionStatus(conn, requisition, RequisitionStatus.WAIT_FOR_SICOP, PrecomprometidoStatus.REQUESTED);
        } else {
            return RequisitionManager.setRequisitionStatus(conn, requisition, RequisitionStatus.APPROVED, PrecomprometidoStatus.APPROVED);
        }
    }

    public static void insertOperationLog(Connection conn, Requisition requisition) throws SQLException {
        CallableStatement cmst = null;
        StringBuilder call = new StringBuilder();
        call.append("{call sp_mBitacoraMovimientos (?,?,?)}");
        try {
            cmst = conn.prepareCall(call.toString());
            cmst.setString(1, requisition.getIdSolicitud());
            cmst.setString(2, "ANULAR");
            cmst.setString(3, requisition.getIdUsuarioAnulacion());
            cmst.executeUpdate();
        } finally {
            CloseObject.closeObject(cmst);
        }
    }

    public static Requisition readRequisition(Connection conn, int folioApartado) throws IllegalAccessException, InvocationTargetException, SQLException {
        StringBuilder query = new StringBuilder(querySel);
        query.append(" WHERE	CONVERT( INT, SUBSTRING( C_FOLIO_APA,10,10) ) = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, folioApartado);
            rs = ps.executeQuery();
            return readRequisition(rs);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static Requisition readRequisition(Connection conn, String requisitionId) throws IllegalAccessException, InvocationTargetException, SQLException {
        StringBuilder query = new StringBuilder(querySel);
        query.append(" WHERE	cIdSolicitud = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, requisitionId);
            rs = ps.executeQuery();
            return readRequisition(rs);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    private static Requisition readRequisition(ResultSet rs) throws SQLException, IllegalAccessException, InvocationTargetException {
        Map<String, String> values = RSToTable.rsToMapCaseSensitive(rs);
        Requisition requisition = new Requisition();
        DateConverter converter = new DateConverter(null);
        converter.setPattern("yyyy-MM-dd");
        ConvertUtils.register(converter, Date.class);
        BeanUtils.populate(requisition, values);
        return requisition;
    }

    public static Requisition readRequisitionByApartado(Connection conn, String folio) throws IllegalAccessException, InvocationTargetException, SQLException {
        StringBuilder query = new StringBuilder(querySel);
        query.append(" WHERE	C_FOLIO_APA = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, folio);
            return readRequisition(rs);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static void cancelAccountingMovements(Connection conn, Requisition requisition) throws AccountingEngineException, SQLException {
        if (isCanceled(conn, requisition))
            return;
        AccountingEngine ae = new AccountingEngine();
        ae.setValidaInsuficienciaDeSaldo(true);
        ae.cancelAccountingApplication(conn, "APARTADO", String.valueOf(Util.folio(requisition.getFolioApartado())), "tApartadoEncabezado", "tApartadoDetalle", "nFolioApartado");
    }

    private static boolean isCanceled(Connection conn, Requisition requisition) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sb = new StringBuilder("SELECT cDocumentoHaplicado FROM tApartadoEncabezado WITH(NOLOCK) WHERE nFolioApartado = ?");
        boolean isCanceled = false;
        try {
            ps = conn.prepareStatement(sb.toString());
            ps.setInt(1, Util.folio(requisition.getFolioApartado()));
            rs = ps.executeQuery();
            if (rs.next())
                isCanceled = "C".equals(rs.getString(1));
            return isCanceled;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }

    public static boolean isAplidCuestionary(Connection conn, int nFolioApartado) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sb = new StringBuilder();
        boolean applyQuestionnaire = false;
        try {
            sb.append("SELECT isnull(cuestionario.applyQuestionnaire,'N') applyQuestionnaire ");
            sb.append(" FROM mSolicitud WITH(NOLOCK) ");
            sb.append(" left join(SELECT	nStatus AS applyQuestionnaire,cpartida FROM	tPartida15D WITH(NOLOCK) where nStatus='S' ");
            sb.append(" )cuestionario on cuestionario.cpartida=mSolicitud.cIdSubPartida ");
            sb.append(" WHERE ConsecutivoAPARTADO=? ");
            ps = conn.prepareStatement(sb.toString());
            ps.setInt(1, nFolioApartado);
            rs = ps.executeQuery();
            if (rs.next() && "S".equalsIgnoreCase(rs.getString("applyQuestionnaire")))
                applyQuestionnaire = true;
            return applyQuestionnaire;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }

    public static void insertSecludedHeader(Connection conn, Requisition contractRequisition, Usuario user) throws Exception {
        if (StringUtils.isEmpty(contractRequisition.getIdEntidadContable()))
            contractRequisition.setIdEntidadContable(user.getPropiedad("CCENTROCONTABLE").getValor());
        int paramCnt = 1;
        PreparedStatement pstm = null;
        StringBuilder query = new StringBuilder();
        query.append(" INSERT INTO tApartadoEncabezado(");
        query.append("nFolioApartado, fCarga, fAplicacion, cCentroContable, cRamo,cUnidadResponsable, caNoPreCompromiso,");
        query.append(" cTipoPoliza, nMes, aEjercicioFiscal, nStatusFinanciero, fVigencia, nEnviadoSICOP, cIdSolicitud) ");
        query.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, DATEADD(DD, 6,GETDATE() ), ?, ?)");
        int consecutive = 100000 + CFSequenceManager.getInstance().nextVal("CO-" + user.getPropiedad("CCENTROCONTABLE").getValor());
        String caNoPreCompromiso = user.getPropiedad("CCENTROCONTABLE").getValor() + "CO" + contractRequisition.getEjercicio() + consecutive;
        try {
            log.info("Creando encabezado del apartado." + new Timestamp(System.currentTimeMillis()));
            pstm = conn.prepareStatement(query.toString());
            pstm.setInt(paramCnt++, contractRequisition.getConsecutivoApartado());
            pstm.setString(paramCnt++, Util.getTodayESMX());
            pstm.setString(paramCnt++, Util.getTodayESMX());
            pstm.setString(paramCnt++, user.getPropiedad("CCENTROCONTABLE").getValor());
            pstm.setString(paramCnt++, user.getU_Ramo());
            pstm.setString(paramCnt++, user.getU_UR());
            pstm.setString(paramCnt++, caNoPreCompromiso);
            pstm.setString(paramCnt++, "PR");
            pstm.setInt(paramCnt++, Util.getCurrentMonth());
            pstm.setString(paramCnt++, contractRequisition.getEjercicio());
            pstm.setInt(paramCnt++, 0);
            pstm.setInt(paramCnt++, 0);
            pstm.setString(paramCnt++, contractRequisition.getIdSolicitud());
            int insertados = pstm.executeUpdate();
            log.info("Se insertaron: " + insertados + " registros");
            log.info("Termina de crear el encabezado del apartado." + new Timestamp(System.currentTimeMillis()));
        } finally {
            CloseObject.closeObject(pstm);
        }
    }

    public static void insertSecludedDetail(Connection conn, Requisition contractRequisition, Usuario user) throws SQLException {
        CallableStatement cmst = null;
        try {
            log.info("Creando  detalle del apartado." + new Timestamp(System.currentTimeMillis()));
            cmst = conn.prepareCall("{call sp_insertApartadoDetalle (?,?,?,?,?)}");
            cmst.setInt(1, contractRequisition.getConsecutivoApartado());
            cmst.setString(2, user.getPropiedad("CCENTROCONTABLE").getValor());
            cmst.setString(3, contractRequisition.getEjercicio());
            cmst.setString(4, user.getU_UR());
            cmst.setString(5, contractRequisition.getIdSolicitud());
            cmst.execute();
            log.info("Termina de crear el detalle del apartado." + new Timestamp(System.currentTimeMillis()));
        } finally {
            CloseObject.closeObject(cmst);
        }
    }

    public static void insertApartado(Connection conn, Requisition contractRequisition, Usuario user) throws Exception {
        insertSecludedHeader(conn, contractRequisition, user);
        insertSecludedDetail(conn, contractRequisition, user);
    }
}
