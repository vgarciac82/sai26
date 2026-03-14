package com.syc.egresos.core.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang.StringUtils;
import com.axtel.egresos.entities.EgresoExcedeUMA;
import com.syc.adquisiciones.ConsumePAASInterface;
import com.syc.adquisiciones.businessLogic.ConsumePAASImpl;
import com.syc.cfdi.core.FacturaManager;
import com.syc.contable.core.PagosDirectosManager;
import com.syc.egresos.core.Amortizacion;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.core.EgresoRetencion;
import com.syc.egresos.core.EgresoRetencionManager;
import com.syc.ejercido.pagado.core.EgresosManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.contabilidad.utils.db.RSToTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class EgresoPAGODIRECTOEncabezado extends EgresoEncabezado {

    /**
     * Crea una nueva instancia de Encabezado de Pago Directo.
     */
    public EgresoPAGODIRECTOEncabezado() {
        super();
        setTipoPago("PAGODIRECTO");
    }

    private static final Logger log = LoggerFactory.getLogger(EgresoPAGODIRECTOEncabezado.class);

    private static int insertarEncabezado(Connection conn, EgresoPAGODIRECTOEncabezado pde) throws Exception {
        if (!existePago(conn, pde.getFolioPago())) {
            return insertaEncabezadoNuevo(conn, pde);
        } else {
            return actulizaEncabezado(conn, pde);
        }
    }

    private static int actulizaEncabezado(Connection conn, EgresoPAGODIRECTOEncabezado pde) throws SQLException {
        StringBuilder queryUpdate = new StringBuilder();
        queryUpdate.append("UPDATE  tpagodirectoencabezado ");
        queryUpdate.append("  SET   cConcepto=?, ");
        queryUpdate.append("        cDescripcionPoliza=?, ");
        queryUpdate.append("        cIdTipoDocumento=?, ");
        queryUpdate.append("        cIdTipoFondo=?, ");
        queryUpdate.append("        cIdUsuarioCaptura=?, ");
        queryUpdate.append("        CTAB=?, ");
        queryUpdate.append("        cUnidadResponsable=?, ");
        queryUpdate.append("        fAplicacion=?, ");
        queryUpdate.append("        fRecepcion=GETDATE(), ");
        queryUpdate.append("        fRevision=GETDATE(), ");
        queryUpdate.append("        ID_DESTINO_GASTO=?, ");
        queryUpdate.append("        ID_TIPO_CONCEPTO=?, ");
        queryUpdate.append("        ID_TIPO_MOVIMIENTO=?, ");
        queryUpdate.append("        mImporteBruto=?, ");
        queryUpdate.append("        mImporteDescuento=?, ");
        queryUpdate.append("        mImporteIVA=?, ");
        queryUpdate.append("        mImporteNeto=?, ");
        queryUpdate.append("        mImportePenalizacion=?, ");
        queryUpdate.append("        mImporteRetencion=?, ");
        queryUpdate.append("        mOtrosImpuestos=?, ");
        queryUpdate.append("        nEnviadoSICOP=?, ");
        queryUpdate.append("        nIdConcepto=?, ");
        queryUpdate.append("        nIDEstatus=?, ");
        queryUpdate.append("        nNumEmpleadoAut=?, ");
        queryUpdate.append("        nNumEmpleadoElab=?, ");
        queryUpdate.append("        nNumEmpleadoVoBo=?, ");
        queryUpdate.append("        nPorcIVA=?, ");
        queryUpdate.append("        U_LOGIN=? ");
        queryUpdate.append(" WHERE  nFolioPagoDirecto = ? ");
        PreparedStatement ps = null;
        try {
            int i = 1;
            ps = conn.prepareStatement(queryUpdate.toString());
            ps.setString(i++, pde.getConcepto());
            ps.setString(i++, pde.getDescripcionPoliza());
            ps.setString(i++, pde.getIdTipoDocumento());
            ps.setString(i++, pde.getIdTipoFondo());
            ps.setString(i++, pde.getIdUsuarioCaptura());
            ps.setString(i++, pde.getCTAB());
            ps.setString(i++, pde.getUnidadResponsable());
            ps.setDate(i++, new java.sql.Date(pde.getFechaAplicacion().getTime()));
            ps.setString(i++, pde.getIdDestinoGasto());
            ps.setString(i++, pde.getIdTipoConcepto());
            ps.setString(i++, pde.getIdTipoMovimiento());
            ps.setBigDecimal(i++, pde.getImporteBruto());
            ps.setBigDecimal(i++, pde.getImporteDescuento());
            ps.setBigDecimal(i++, pde.getImporteIVA());
            ps.setBigDecimal(i++, pde.getImporteNeto());
            ps.setBigDecimal(i++, pde.getImportePenalizacion());
            ps.setBigDecimal(i++, pde.getImporteRetencion());
            ps.setBigDecimal(i++, pde.getOtrosImpuestos());
            ps.setInt(i++, pde.getEnviadoSICOP());
            ps.setString(i++, pde.getIdConcepto());
            ps.setInt(i++, pde.getIdEstatus());
            ps.setInt(i++, pde.getNumEmpleadoAut());
            ps.setInt(i++, pde.getNumEmpleadoElab());
            ps.setInt(i++, pde.getNumEmpleadoVoBo());
            ps.setBigDecimal(i++, pde.getPorcentajeIVA());
            ps.setString(i++, pde.getIdUsuarioCaptura());
            ps.setInt(i++, pde.getFolioPagoDirecto());
            int actualizados = 0;
            log.info("Object: {}", "Ejecutando: " + queryUpdate + "\n[" + pde.getConcepto() + ", " + pde.getDescripcionPoliza() + ", " + pde.getIdTipoDocumento() + ", " + pde.getIdTipoFondo() + ", " + pde.getIdUsuarioCaptura() + ", " + pde.getCTAB() + ", " + pde.getUnidadResponsable() + ", " + new java.sql.Date(pde.getFechaAplicacion().getTime()) + ", " + pde.getIdDestinoGasto() + ", " + pde.getIdTipoConcepto() + ", " + pde.getIdTipoMovimiento() + ", " + pde.getImporteBruto() + ", " + pde.getImporteDescuento() + ", " + pde.getImporteIVA() + ", " + pde.getImporteNeto() + ", " + pde.getImportePenalizacion() + ", " + pde.getImporteRetencion() + ", " + pde.getOtrosImpuestos() + ", " + pde.getEnviadoSICOP() + ", " + pde.getIdConcepto() + ", " + pde.getIdEstatus() + ", " + pde.getNumEmpleadoAut() + ", " + pde.getNumEmpleadoElab() + ", " + pde.getNumEmpleadoVoBo() + ", " + pde.getPorcentajeIVA() + ", " + pde.getIdUsuarioCaptura() + ", " + pde.getFolioPagoDirecto() + "]");
            actualizados = ps.executeUpdate();
            log.info("Object: {}", "Se actualizaron " + actualizados + " Registros de encabezado pago directo  folio " + pde.getFolioPagoDirecto());
            return actualizados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    private static boolean existePago(Connection conn, int folioPago) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT COUNT(*) AS EXISTE FROM tPagoDirectoEncabezado WITH(NOLOCK) WHERE nFolioPagoDirecto = ?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean existe = false;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, folioPago);
            rs = ps.executeQuery();
            if (rs.next())
                existe = rs.getInt(1) > 0;
            return existe;
        } finally {
            CloseObject.closeObject(rs, ps);
        }
    }

    private static int insertaEncabezadoNuevo(Connection conn, EgresoPAGODIRECTOEncabezado pde) throws SQLException {
        log.info("Object: {}", "Insertando el registro : " + pde);
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tPagoDirectoEncabezado (");
        query.append("nFolioPagoDirecto,");
        query.append("fCarga,");
        query.append("fAplicacion,");
        query.append("cRamo,");
        query.append("cUnidadResponsable,");
        query.append("aEjercicioFiscal,");
        query.append("cIdEntidadContable,");
        query.append("cIdDocumento,");
        query.append("cIdTipoDocumento, ");
        query.append("cIdTipoMontoDesembolso,");
        query.append("cIdRFC,");
        query.append("fRecepcion,");
        query.append("fRevision,");
        query.append("fProgramadaPago,");
        query.append("cConcepto,");
        query.append("nPorcIVA,");
        query.append("mImporteBruto,");
        query.append("mImporteIVA,");
        query.append("mImporteRetencion,");
        query.append("mImporteNeto,");
        query.append("cIdTipoPagoDirecto, ");
        query.append("cIdTipoFondo,");
        query.append("caNoContrarrecibo,");
        query.append("cIdUsuarioCaptura,");
        query.append("ID_DESTINO_GASTO,");
        query.append("ID_TIPO_MOVIMIENTO,");
        query.append("ID_TIPO_CONCEPTO, ");
        query.append("fVigenciaIVA, ");
        query.append("CTAB, ");
        query.append("nIDEstatus, ");
        query.append("cTipopoliza,");
        query.append("nFolioAutSICOP,");
        query.append("nFolioSuficiencia,");
        query.append("cIDContrato,");
        query.append("cDescripcionPoliza)");
        query.append("VALUES( ");
        query.append("?,");
        /* nFolioPagoDirecto */
        query.append("?, ");
        /* fCarga */
        query.append("?, ");
        /* fAplicacion */
        query.append("?, ");
        /* cRamo */
        query.append("?, ");
        /* cUnidadResponsable */
        query.append("?, ");
        /* aEjercicioFiscal */
        query.append("?, ");
        /* cIdEntidadContable */
        query.append("?, ");
        /* cIdDocumento */
        query.append("?, ");
        /* cIdTipoDocumento */
        query.append("?, ");
        /* cIdTipoMontoDesembolso */
        query.append("?, ");
        /* cIdRFC */
        query.append("?, ");
        /* fRecepcion */
        query.append("?, ");
        /* fRevision */
        query.append("?, ");
        /* fProgramadaPago */
        query.append("?, ");
        /* cConcepto */
        query.append("?, ");
        /* nPorcIVA */
        query.append("?, ");
        /* mImporteBruto */
        query.append("?, ");
        /* mImporteIVA */
        query.append("?, ");
        /* mImporteRetencion */
        query.append("?, ");
        /* mImporteNeto */
        query.append("?, ");
        /* cIdTipoPagoDirecto */
        query.append("?, ");
        /* cIdTipoFondo */
        query.append("?, ");
        /* caNoContrarrecibo */
        query.append("?, ");
        /* cIdUsuarioCaptura */
        query.append("?, ");
        /* ID_DESTINO_GASTO */
        query.append("?, ");
        /* ID_TIPO_MOVIMIENTO */
        query.append("?, ");
        /* ID_TIPO_CONCEPTO */
        query.append("GETDATE(),");
        /* fVigenciaIVA */
        ;
        query.append("?,");
        /* CTAB */
        ;
        query.append("?,");
        /* nIDEstatus */
        ;
        query.append("?,");
        /* cTipopoliza */
        query.append("?,");
        /* nFolioAutSICOP */
        query.append("?,");
        /* nFolioSuficiencia */
        query.append("?,");
        /* cIDContrato */
        query.append("?)");
        /* cDescripcionPoliza */
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            StringBuilder sb = new StringBuilder(query);
            sb.append("\n1").append("[").append(pde.getFolioPagoDirecto()).append("]\n");
            sb.append("2").append("[").append(new java.sql.Date(pde.getFechaCarga().getTime())).append("]\n");
            sb.append("3").append("[").append(new java.sql.Date(pde.getFechaAplicacion().getTime())).append("]\n");
            sb.append("4").append("[").append(pde.getRamo()).append("]\n");
            sb.append("5").append("[").append(pde.getUnidadResponsable()).append("]\n");
            sb.append("6").append("[").append(pde.getEjercicioFiscal()).append("]\n");
            sb.append("7").append("[").append(pde.getCentroContable()).append("]\n");
            sb.append("8").append("[").append(pde.getIdDocumento()).append("]\n");
            sb.append("9").append("[").append(pde.getIdTipoDocumento()).append("]\n");
            sb.append("10").append("[").append(pde.getIdTipoMontoDesembolso()).append("]\n");
            sb.append("11").append("[").append(pde.getRfc()).append("]\n");
            sb.append("12").append("[").append(new java.sql.Date(pde.getFechaRecepcion().getTime())).append("]\n");
            sb.append("13").append("[").append(pde.getFechaRevision() == null ? null : new java.sql.Date(pde.getFechaRevision().getTime())).append("]\n");
            sb.append("14").append("[").append(new java.sql.Date(pde.getFechaProgramadaPago().getTime())).append("]\n");
            sb.append("15").append("[").append(pde.getConcepto()).append("]\n");
            sb.append("16").append("[").append(pde.getPorcentajeIVA()).append("]\n");
            sb.append("17").append("[").append(pde.getImporteBruto()).append("]\n");
            sb.append("18").append("[").append(pde.getImporteIVA()).append("]\n");
            sb.append("19").append("[").append(pde.getImporteRetencion()).append("]\n");
            sb.append("20").append("[").append(pde.getImporteNeto()).append("]\n");
            sb.append("21").append("[").append(pde.getIdTipoPagoDirecto()).append("]\n");
            sb.append("22").append("[").append(pde.getIdTipoFondo()).append("]\n");
            sb.append("23").append("[").append(pde.getContrarecibo()).append("]\n");
            sb.append("24").append("[").append(pde.getIdUsuarioCaptura()).append("]\n");
            sb.append("25").append("[").append(pde.getIdDestinoGasto()).append("]\n");
            sb.append("26").append("[").append(pde.getIdTipoMovimiento()).append("]\n");
            sb.append("27").append("[").append(pde.getIdTipoConcepto()).append("]\n");
            sb.append("28").append("[").append(pde.getCTAB()).append("]\n");
            sb.append("29").append("[").append(pde.getIdEstatus()).append("]\n");
            sb.append("30").append("[").append(pde.getTipoPoliza()).append("]\n");
            sb.append("31").append("[").append(pde.getFolioAutSICOP()).append("]\n");
            sb.append("32").append("[").append(pde.getFolioSuficiencia()).append("]\n");
            sb.append("33").append("[").append(pde.getIdContrato()).append("]\n");
            sb.append("34").append("[").append(pde.getDescripcionPoliza()).append("]\n");
            log.info("Object: {}", "Ejecutando Query: " + sb.toString());
            ps.setInt(1, pde.getFolioPagoDirecto());
            ps.setDate(2, new java.sql.Date(pde.getFechaCarga().getTime()));
            ps.setDate(3, new java.sql.Date(pde.getFechaAplicacion().getTime()));
            ps.setString(4, pde.getRamo().trim());
            ps.setString(5, pde.getUnidadResponsable());
            ps.setString(6, pde.getEjercicioFiscal());
            ps.setString(7, pde.getCentroContable());
            ps.setInt(8, pde.getIdDocumento());
            ps.setString(9, pde.getIdTipoDocumento());
            ps.setInt(10, pde.getIdTipoMontoDesembolso());
            ps.setString(11, pde.getRfc());
            ps.setDate(12, new java.sql.Date(pde.getFechaRecepcion().getTime()));
            ps.setDate(13, pde.getFechaRevision() == null ? null : new java.sql.Date(pde.getFechaRevision().getTime()));
            ps.setDate(14, new java.sql.Date(pde.getFechaProgramadaPago().getTime()));
            ps.setString(15, pde.getConcepto());
            ps.setBigDecimal(16, pde.getPorcentajeIVA());
            ps.setBigDecimal(17, pde.getImporteBruto());
            ps.setBigDecimal(18, pde.getImporteIVA());
            ps.setBigDecimal(19, pde.getImporteRetencion());
            ps.setBigDecimal(20, pde.getImporteNeto());
            ps.setInt(21, pde.getIdTipoPagoDirecto());
            ps.setString(22, pde.getIdTipoFondo());
            ps.setString(23, pde.getContrarecibo());
            ps.setString(24, pde.getIdUsuarioCaptura());
            ps.setString(25, pde.getIdDestinoGasto());
            ps.setString(26, pde.getIdTipoMovimiento());
            ps.setString(27, pde.getIdTipoConcepto());
            ps.setString(28, pde.getCTAB());
            ps.setInt(29, pde.getIdEstatus());
            ps.setString(30, pde.getTipoPoliza());
            ps.setString(31, pde.getFolioAutSICOP());
            ps.setString(32, pde.getFolioSuficiencia());
            ps.setString(33, pde.getIdContrato());
            ps.setString(34, pde.getDescripcionPoliza());
            int afectados = ps.executeUpdate();
            log.info("Object: {}", "Se inserto " + afectados + " registros en pago directo para el folio " + pde.getFolioPagoDirecto());
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    private Date fechaRecepcion;

    private int folioPagoDirecto = 0;

    private int idDocumento = 0;

    private int idTipoMontoDesembolso = 0;

    private int idTipoPagoDirecto = 0;

    private String folioAutSICOP;

    private String folioSuficiencia;

    private String idContrato;

    @Override
    public int actualizaMontosRetencion(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE	tPagoDirectoEncabezado ");
        query.append("   SET	mImporteNeto = tbl.nuevoImporteNeto, ");
        query.append("		mImporteRetencion = tbl.retenciones ");
        query.append("  FROM	(");
        query.append("		SELECT	pagoDirecto.mImporteBruto + pagoDirecto.mImporteIVA + pagoDirecto.mOtrosImpuestos - SUM(isnull( retenciones.mImporteRetencion,0) ) AS nuevoImporteNeto, ");
        query.append("				SUM(isnull( retenciones.mImporteRetencion,0) ) AS retenciones ");
        query.append("		  FROM	tPagoDirectoEncabezado pagoDirecto WITH(NOLOCK)");
        query.append("				LEFT OUTER JOIN ");
        query.append("				tPagoDirectoRetencion retenciones WITH(NOLOCK)");
        query.append("				ON ");
        query.append("				pagoDirecto.nFolioPagoDirecto = retenciones.nFolioPagoDirecto ");
        query.append("		 WHERE	pagoDirecto.nFolioPagoDirecto = ? ");
        query.append("		GROUP BY pagoDirecto.mImporteBruto, pagoDirecto.mImporteIVA , pagoDirecto.mOtrosImpuestos ");
        query.append("		) AS tbl ");
        query.append("WHERE nFolioPagoDirecto = ? ");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, getFolioPagoDirecto());
            ps.setInt(2, getFolioPagoDirecto());
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    @Override
    public int actualizaRetencion(Connection conn, int idTipoRetencion, BigDecimal valorRetencion) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE	tPagoDirectoRetencion ");
        query.append("   SET	mImporteRetencion = ? ");
        query.append(" WHERE	nFolioPagoDirecto = ? ");
        query.append("   AND	cIdTipoRetencion = ? ");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setBigDecimal(1, valorRetencion);
            ps.setInt(2, getFolioPagoDirecto());
            ps.setInt(3, idTipoRetencion);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    @Override
    public int avanzaEstatus(Connection conn) throws Exception {
        String query = "";
        query += "UPDATE	tPagoDirectoEncabezado ";
        query += "   SET	nIDEstatus = nIDEstatus + 1 ";
        query += " WHERE	nFolioPagoDirecto = ? ";
        PreparedStatement ps = null;
        int actualizados = 0;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, getFolioPagoDirecto());
            actualizados = ps.executeUpdate();
            return actualizados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    @Override
    public EgresoEncabezado cargaEncabezado(HttpServletRequest req) throws Exception {
        return instanceFromRequest(req);
    }

    @Override
    public EgresoEncabezado cargaEncabezado(int folioEgreso) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            return PagosDirectosManager.cargaEncabezado(conn, folioEgreso);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public int delete(Connection conn) throws Exception {
        return 1;
        /*
		StringBuilder query = new StringBuilder();
		query.append( "DELETE " );
		query.append( "  FROM	tPagoDirectoEncabezado " );
		query.append( " WHERE	nFolioPagoDirecto = ? " );

		PreparedStatement ps = null;
		int afectados = 0;
		try {
			log.trace( "Iniciando eliminacion del pago directo: " + getFolioPagoDirecto() );
			ps = conn.prepareStatement( query.toString() );
			ps.setInt( 1, getFolioPagoDirecto() );

			afectados = ps.executeUpdate();
			log.trace( "Se eliminaron : " + afectados + " pagos con el folio: " + getFolioPagoDirecto() );

			return afectados;
		} finally {
			CloseObject.closeObject( ps );
		}*/
    }

    @Override
    public int eliminaRetencion(Connection conn, int idTipoRetencion) throws Exception {
        StringBuilder query = new StringBuilder("DELETE FROM tPagoDirectoRetencion WHERE nFolioPagoDirecto = ? AND cIdTipoRetencion = ?");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, getFolioPagoDirecto());
            ps.setInt(2, idTipoRetencion);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    @Override
    public int generaRetenciones(Connection conn) throws Exception {
        List<EgresoRetencion> retenciones = EgresoRetencionManager.generaRetencionesEgresoDirecto(conn, this);
        int insertados = 0;
        for (EgresoRetencion retencion : retenciones) {
            insertados += EgresoRetencionManager.insertaRetencionEgresoDirecto(conn, this, retencion);
        }
        log.info("Object: {}", "Se insertaron: " + insertados + " retenciones para el pago directo folio " + getFolioPagoDirecto());
        return insertados;
    }

    public int getEstatusActual(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder("SELECT nIDEstatus FROM tPagoDirectoEncabezado WITH(NOLOCK) WHERE nFolioPagoDirecto = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, this.getFolioPagoDirecto());
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else
                return 0;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
            query = null;
        }
    }

    public Date getFechaRecepcion() {
        return fechaRecepcion;
    }

    public int getFolioPagoDirecto() {
        return folioPagoDirecto;
    }

    public int getIdDocumento() {
        return idDocumento;
    }

    public int getIdTipoMontoDesembolso() {
        return idTipoMontoDesembolso;
    }

    public int getIdTipoPagoDirecto() {
        return idTipoPagoDirecto;
    }

    @Override
    public String getNombreAnexo() {
        return "Anexo1.jasper";
    }

    @Override
    public String getNombreSolicitudPago() {
        return "PolizaPagoNuevoN.jasper";
    }

    @Override
    public String getPrefijoCR(Connection conn) throws Exception {
        String cxpPrefijo = ConfiguraAplicativoManager.getSystemSetting(conn, "CXP_PREFIJO");
        return cxpPrefijo;
    }

    private EgresoEncabezado instanceFromRequest(HttpServletRequest request) throws Exception {
        EgresoEncabezado encabezado = new EgresoPAGODIRECTOEncabezado();
        encabezado = super.readFromRequest(request, encabezado);
        ((EgresoPAGODIRECTOEncabezado) encabezado).setFolioPagoDirecto(Integer.parseInt(request.getParameter("nFolioPago")));
        ((EgresoPAGODIRECTOEncabezado) encabezado).setIdDocumento(Integer.parseInt(request.getParameter("IDDocumento")));
        ((EgresoPAGODIRECTOEncabezado) encabezado).setIdTipoMontoDesembolso(Integer.parseInt(request.getParameter("idTipoDocumento")));
        ((EgresoPAGODIRECTOEncabezado) encabezado).setFechaRecepcion(Util.stringToDate(request.getParameter("fechaAplicacion"), "dd/MM/yyyy"));
        ((EgresoPAGODIRECTOEncabezado) encabezado).setIdTipoPagoDirecto(Integer.parseInt(request.getParameter("idTipoDocumento")));
        ((EgresoPAGODIRECTOEncabezado) encabezado).setCTAB(request.getParameter("CTAB"));
        encabezado.setDescripcionPoliza(encabezado.getConcepto());
        return encabezado;
    }

    @Override
    public void rechazaPago(Connection conn, String motivoRechazo) throws Exception {
        StringBuilder queryUpdate = new StringBuilder();
        queryUpdate.append("UPDATE tsuficienciapagodirectoencabezado SET idStatus = 2 WHERE cIDContrato = ?");
        StringBuilder query = new StringBuilder();
        query.append("UPDATE t").append(getTipoPago()).append("Encabezado ");
        query.append("   SET cDocumentoHAplicado = 'C',");
        query.append("       nIDEstatus = '-1' ");
        query.append(" WHERE nFolio").append(getTipoPago()).append(" = ").append("?");
        StringBuilder query2 = new StringBuilder();
        query2.append("SELECT ID_DESTINO_GASTO,cIDContrato FROM tPagoDirectoEncabezado ");
        query2.append(" WHERE nFolio").append(getTipoPago()).append(" = ").append("?");
        PreparedStatement ps = null, ps2 = null;
        PreparedStatement psUpdate = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        String destinoGasto = "";
        String idContrato = "";
        try {
            ps2 = conn.prepareStatement(query2.toString());
            ps2.setInt(1, getFolioPago());
            rs2 = ps2.executeQuery();
            if (rs2.next()) {
                destinoGasto = rs2.getString(1);
                idContrato = StringUtils.trimToNull(rs2.getString("cIDContrato"));
            }
            if (idContrato != null) {
                log.info("Object: {}", "Regresando estatus de la suficiencia : " + idContrato);
                psUpdate = conn.prepareStatement(queryUpdate.toString());
                psUpdate.setString(1, idContrato);
                psUpdate.executeUpdate();
                log.info("Object: {}", "Suficiencia " + idContrato + " actualizada correctamente.");
            }
            FacturaManager.eliminaFacturas(conn, getTipoPago(), String.valueOf(getFolioPago()));
            if (!"NODR".equals(destinoGasto)) {
                EgresosManager.cancelaApartadoTramite(conn, this);
                ConsumePAASInterface consumePAAS = new ConsumePAASImpl(GestionInterface.ATT_CONEXION);
                ps = conn.prepareStatement(query.toString());
                ps.setInt(1, getFolioPago());
                ps.executeUpdate();
                consumePAAS.liberaPaasPago(conn, getFolioPago());
                notificaRechazo(conn, motivoRechazo);
            }
        } finally {
            CloseObject.closeObject(ps, ps2, rs, rs2);
        }
    }

    @Override
    public Map<String, String> resumenConcepto(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	operacion.TIPO_OPERACION AS idTipoDocumentoLbl, ");
        query.append("		UPPER( destinoGasto.DESTINO_GASTO ) AS idDestinoGastoLbl, ");
        query.append("		directoEncabezado.cConcepto AS conceptoLbl, ");
        query.append("		beneficiario.dRFC + ' - ' + beneficiario.dNombre + ISNULL(' ' + beneficiario.dApellidoPaterno, '') + ISNULL(' ' + beneficiario.dApellidoMaterno, '') AS rfcLbl, ");
        query.append("		dBanco + ' - ' + subCuentaBancaria AS CTABLbl ");
        query.append("  FROM	tPagoDirectoEncabezado directoEncabezado WITH (NOLOCK) ");
        query.append("		INNER JOIN  CAT_TIPO_OPERACION operacion WITH (NOLOCK) ");
        query.append("		ON directoEncabezado.cIdTipoDocumento = operacion.ID_TIPO_OPER ");
        query.append("		AND operacion.TO_TIPO_DOCTO = 'DIRECTO' ");
        query.append("		INNER JOIN CAT_DESTINO_GASTO AS destinoGasto WITH (NOLOCK) ");
        query.append("		ON directoEncabezado.ID_DESTINO_GASTO = destinoGasto.ID_DESTINO_GASTO ");
        query.append("		INNER JOIN tBeneficiario beneficiario WITH (NOLOCK)  ");
        query.append("		ON directoEncabezado.cIdRFC = beneficiario.dRFC ");
        query.append("		INNER JOIN tBeneficiarioCuentasBancarias beneficiarioCB WITH (NOLOCK)  ");
        query.append("		ON directoEncabezado.cIdRFC = beneficiarioCB.dRFC ");
        query.append("		AND directoEncabezado.CTAB  = beneficiarioCB.subCuentaBancaria ");
        query.append(" WHERE	nFolioPagoDirecto = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, getFolioPagoDirecto());
            rs = ps.executeQuery();
            Map<String, String> result = RSToTable.rsToMapCaseSensitive(rs);
            return result;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    @Override
    public Map<String, String> resumenPago(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	pagoDirectoEncabezado.caNoContrarrecibo AS caNoContrarreciboResumen,");
        query.append("		'PAGOD DIRECTO' AS tipoDocumentoResumen,");
        query.append("		CONVERT( VARCHAR(16), pagoDirectoEncabezado.fAplicacion, 103) AS fechaAplicacionResumen,");
        query.append("		pagoDirectoEncabezado.ID_DESTINO_GASTO + ' - ' + destinoGasto.DESTINO_GASTO AS destinoGastoResumen,");
        query.append("		pagoDirectoEncabezado.nFolioPagoDirecto AS nFolioResumen,");
        query.append("		pagoDirectoEncabezado.ID_TIPO_CONCEPTO + ' - ' + tipoConcepto.TIPO_CONCEPTO AS tipoConceptoResumen,");
        query.append("		centroContable.cDescripcion AS centroContableResumen,");
        query.append("		ur.D_DESCRIPCION AS unidadResponsableResumen,");
        query.append("		tipoOperacion.TIPO_OPERACION AS tipoPagoDirectoResumen,");
        query.append("		convert(varchar(32), CONVERT(MONEY, pagoDirectoEncabezado.mImporteNeto) , 103) AS importePagarResumen,");
        query.append("		dbo.fnCantidadLetra(pagoDirectoEncabezado.mImporteNeto) AS importeLetraResumen,");
        query.append("		pagoDirectoEncabezado.cIdRFC AS rfcResumen,");
        query.append("		beneficiario.dNombre + ISNULL( ' ' + dApellidoPaterno , '' ) + ISNULL( ' ' + dApellidoMaterno, '') AS cNombreResumen,");
        query.append("		pagoDirectoEncabezado.CTAB AS ctaBanResumen,");
        query.append("		beneficiarioCB.dBanco AS ctaBanBancoResumen,");
        query.append("		pagoDirectoEncabezado.cConcepto AS conceptoResumen,");
        query.append("		CONVERT(VARCHAR(32), CONVERT(MONEY, pagoDirectoEncabezado.mImporteBruto), 103 ) AS mImporteBrutoResumen,");
        query.append("		CONVERT(VARCHAR(32), CONVERT(MONEY, pagoDirectoEncabezado.mImportePenalizacion ), 103 ) AS importePenasResumen,");
        query.append("		CONVERT(VARCHAR(32), CONVERT(MONEY, pagoDirectoEncabezado.mImporteIVA ), 103 ) AS importeIVAResumen,");
        query.append("		CONVERT(VARCHAR(32), CONVERT(MONEY, pagoDirectoEncabezado.mOtrosImpuestos ), 103 ) AS importeOtrosImpResumen,");
        query.append("		CONVERT(VARCHAR(32), CONVERT(MONEY, pagoDirectoEncabezado.mImporteRetencion ), 103 ) AS importeRetResumen,");
        query.append("		CONVERT(VARCHAR(32), CONVERT(MONEY, pagoDirectoEncabezado.mImporteNeto ), 103 ) AS importeNetoResumen");
        query.append("  FROM	tPagoDirectoEncabezado pagoDirectoEncabezado WITH(NOLOCK)");
        query.append("		left outer join");
        query.append("		tBeneficiario beneficiario WITH(NOLOCK)");
        query.append("		ON pagoDirectoEncabezado.cIdRFC = beneficiario.dRFC");
        query.append("		LEFT OUTER JOIN");
        query.append("		CAT_DESTINO_GASTO destinoGasto WITH(NOLOCK) ");
        query.append("		ON pagoDirectoEncabezado.ID_DESTINO_GASTO = destinoGasto.ID_DESTINO_GASTO");
        query.append("		LEFT OUTER JOIN ");
        query.append("		CAT_TIPO_CONCEPTO AS tipoConcepto WITH(NOLOCK)");
        query.append("		ON pagoDirectoEncabezado.ID_TIPO_CONCEPTO = tipoConcepto.ID_TIPO_CONCEPTO");
        query.append("		LEFT OUTER JOIN ");
        query.append("		tCatalogoCentroContable centroContable WITH(NOLOCK) ");
        query.append("		ON pagoDirectoEncabezado.cIdEntidadContable = centroContable.cCentroContable");
        query.append("		LEFT OUTER JOIN ");
        query.append("		tCatUnidadResponsable ur WITH(NOLOCK) ");
        query.append("		ON pagoDirectoEncabezado.cUnidadResponsable = ur.cUnidadResponsable");
        query.append("		LEFT OUTER JOIN");
        query.append("		CAT_TIPO_OPERACION tipoOperacion WITH(NOLOCK) ");
        query.append("		ON pagoDirectoEncabezado.cIdTipoPagoDirecto = tipoOperacion.ID_TIPO_OPER");
        query.append("		AND tipoOperacion.TO_TIPO_DOCTO = 'DIRECTO'");
        query.append("		LEFT OUTER JOIN");
        query.append("		tBeneficiarioCuentasBancarias beneficiarioCB WITH(NOLOCK) ");
        query.append("		ON pagoDirectoEncabezado.CTAB = beneficiarioCB.subCuentaBancaria");
        query.append("		AND pagoDirectoEncabezado.cIdRFC = beneficiarioCB.dRFC");
        query.append("	WHERE	nFolioPagoDirecto = ?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, getFolioPagoDirecto());
            rs = ps.executeQuery();
            String importe = consultarImporteEncabezado(conn, getFolioPagoDirecto());
            BigDecimal importeEncabezado = new BigDecimal(importe);
            String importeC = consultarImporteCalendario(conn, getFolioPagoDirecto());
            BigDecimal importeCalendario = new BigDecimal(importeC);
            if (importeEncabezado.compareTo(importeCalendario) != 0) {
                throw new Exception("No coincide el Encabezado con el detalle debe DESCARTAR la Solicitud.");
            }
            Map<String, String> result = RSToTable.rsToMapCaseSensitive(rs);
            return result;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static String consultarImporteEncabezado(Connection conn, int folioPago) throws Exception {
        String importeEncabezado = "";
        PreparedStatement pst = null;
        ResultSet rs = null;
        String query = "SELECT  mImporteBruto + mImporteIVA importe FROM tPagoDirectoEncabezado WITH(NOLOCK) WHERE nFolioPagoDirecto = ?";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, folioPago);
            rs = pst.executeQuery();
            if (rs.next()) {
                importeEncabezado = rs.getString(1);
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pst);
        }
        return importeEncabezado;
    }

    public static String consultarImporteCalendario(Connection conn, int folioPago) throws Exception {
        String importe = "";
        PreparedStatement pst = null;
        ResultSet rs = null;
        String query = "select sum(mimporteBruto) importe from tPagoCalendario WITH(NOLOCK) where nFolioPago = ? and cTipoPago = 'PAGODIRECTO'";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, folioPago);
            rs = pst.executeQuery();
            if (rs.next()) {
                importe = rs.getString(1);
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pst);
        }
        return importe;
    }

    @Override
    public List<Map<String, String>> resumenRetenciones(Connection conn) throws Exception {
        List<Map<String, String>> detalle = new ArrayList<Map<String, String>>();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT	cTipoRetencion, base, porcentajeRetencion, retencion ");
        sb.append("  FROM	vPagoDirectoRetencion ");
        sb.append(" WHERE	nFolioPagoDirecto = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            EgresosManager.validaRetencionRegimenRESICO(conn, "PAGODIRECTO", getFolioPagoDirecto());
            ps = conn.prepareStatement(sb.toString());
            ps.setInt(1, getFolioPagoDirecto());
            rs = ps.executeQuery();
            DateConverter converter = new DateConverter(null);
            Map<String, String> resultObj = RSToTable.rsToMapCaseSensitive(rs);
            converter.setPattern("yyyy-MM-dd");
            ConvertUtils.register(converter, Date.class);
            while (resultObj != null) {
                detalle.add(resultObj);
                resultObj = RSToTable.rsToMapCaseSensitive(rs);
            }
            return detalle;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    @Override
    public int save(Connection conn) throws Exception {
        return insertarEncabezado(conn, this);
    }

    public void setFechaRecepcion(Date fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }

    public void setFolioPagoDirecto(int folioPagoDirecto) {
        this.folioPagoDirecto = folioPagoDirecto;
    }

    public void setIdDocumento(int idDocumento) {
        this.idDocumento = idDocumento;
    }

    public void setIdTipoMontoDesembolso(int idTipoMontoDesembolso) {
        this.idTipoMontoDesembolso = idTipoMontoDesembolso;
    }

    public void setIdTipoPagoDirecto(int idTipoPagoDirecto) {
        this.idTipoPagoDirecto = idTipoPagoDirecto;
    }

    /**
     * Valida que este capturado el PAAS y que el monto capturado no exceda el
     * monto maximo permitido por la configuracion.
     *
     * @return Lista con los errores encontrados.
     * @throws Exception
     */
    public List<String> validaCapturaPAAS() throws Exception {
        return validaCapturaPAAS(this);
    }

    /**
     * Valida que este capturado el PAAS y que el monto capturado no exeda el
     * monto maximo permitido por la configuracion.
     *
     * @param encabezado
     *            del pago directo.
     * @return Lista con los errores encontrados.
     */
    public List<String> validaCapturaPAAS(EgresoPAGODIRECTOEncabezado pde) throws Exception {
        ConsumePAASInterface consumePAAS = new ConsumePAASImpl(GestionInterface.ATT_CONEXION);
        Connection conn = null;
        List<String> errores = new ArrayList<String>();
        try {
            conn = getConnection();
            BigDecimal totalPAASCapturado = consumePAAS.getTotalCapturado(pde.getFolioPagoDirecto());
            if (Util.ZERO.compareTo(totalPAASCapturado) == 0)
                errores.add("No se ha guardado el PAAS. Capture y guarde el PAAS para continuar");
            BigDecimal maximoMontoPago = consumePAAS.montoMaximoTipoPago("PAGODIRECTO");
            if (totalPAASCapturado.compareTo(maximoMontoPago) > 0) {
                errores.add("El pago supera el monto maximo permitido de: " + Util.formatNumber(maximoMontoPago));
            }
            String respuesta = consumePAAS.validaPAASvsSuficiencia(conn, pde.getFolioPagoDirecto());
            if (!respuesta.equals("")) {
                errores.add(respuesta);
            }
            return errores;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public Amortizacion getAmortizacion(Connection conn) throws Exception {
        return null;
    }

    @Override
    public boolean retencionEliminable(Connection conn, int idRetencion) throws Exception {
        StringBuilder query = new StringBuilder();
        if (idRetencion == 18) {
            query.append("SELECT CASE when cIdTipoPersonaRFC = 2 AND cRegimenFiscal = 626 THEN 1 ELSE 0 END ");
            query.append(" FROM tBeneficiario ben WITH(NOLOCK)  ");
            query.append("		INNER JOIN tPagoDirectoEncabezado pago WITH(NOLOCK) on ben.dRFC = pago.cIdRFC");
            query.append("		INNER JOIN tPagoFactura fact WITH (NOLOCK) ON fact.cTipoPago = 'PAGODIRECTO' AND pago.nFolioPagoDirecto = fact.nFolioPago");
            query.append("		WHERE nFolioPagoDirecto = ? ");
        } else {
            query.append("SELECT	obligatoria ");
            query.append("  FROM	tpagodirectopaas partidas WITH(NOLOCK) ");
            query.append("      	INNER JOIN trelacionpartidaretencion retenciones  WITH(NOLOCK) ");
            query.append("               ON cidsubpartida = retenciones.partida ");
            query.append("WHERE	idretencion = ? ");
            query.append("  AND	nfoliopagodirecto = ? ");
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean eliminable = false;
        try {
            ps = conn.prepareStatement(query.toString());
            if (idRetencion == 18) {
                ps.setInt(1, getFolioPagoDirecto());
            } else {
                ps.setInt(1, idRetencion);
                ps.setInt(2, getFolioPagoDirecto());
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                eliminable = rs.getInt(1) == 0;
            }
            return eliminable;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }

    private final BeanListHandler<EgresoExcedeUMA> egresoExcedeUMAHandler = new BeanListHandler<>(EgresoExcedeUMA.class);

    private final QueryRunner runner = new QueryRunner();

    @Override
    public List<EgresoExcedeUMA> validaTopeUMASUnidad(Connection conn, String rfc) throws Exception {
        StringBuilder querySelect = new StringBuilder();
        querySelect.append(" SELECT cIdSubPartida AS partida, mMontoNeto AS montoNeto , RFC AS rfc, razonsocial AS razonSocial");
        querySelect.append(" FROM   tpagodirectopaas AS paas WITH(nolock) ");
        querySelect.append("        INNER JOIN tpagodirectoencabezado AS pago WITH(nolock) ");
        querySelect.append("                ON paas.nfoliopagodirecto = pago.nfoliopagodirecto ");
        querySelect.append("        LEFT OUTER JOIN ");
        querySelect.append("        v_pagos_directos_acumulado_ue acumulado_pagos WITH(nolock) ");
        querySelect.append("                     ON paas.cidsubpartida = acumulado_pagos.cpartida ");
        querySelect.append("                        AND Rtrim(Ltrim(Replace(?, '-', ''))) = ");
        querySelect.append("                            Rtrim( ");
        querySelect.append("                            Ltrim(Replace(acumulado_pagos.rfc, '-', ''))) ");
        querySelect.append("                        AND PAAS.cidunidadejecutora = ");
        querySelect.append("                            acumulado_pagos.cunidadejecutora ");
        querySelect.append(" WHERE  PAGO.nfoliopagodirecto = ? ");
        querySelect.append("        AND PAAS.mmontoneto ");
        querySelect.append("            + Isnull(ACUMULADO_PAGOS.mimportemasiva, 0.00) > (SELECT ");
        querySelect.append("            cant_salario * salario ");
        querySelect.append("                                                              FROM ");
        querySelect.append("                mcatsalario WITH(nolock))  ");
        querySelect.append(" AND Rtrim(Ltrim(Replace(?, '-', ''))) NOT IN (SELECT Rtrim( ");
        querySelect.append(" 											  Ltrim(Replace( ");
        querySelect.append(" 											  rfc, '-', ''))) ");
        querySelect.append(" 											   FROM ");
        querySelect.append(" 											tproveedoresexception300umas ");
        querySelect.append(" 																						   WHERE ");
        querySelect.append(" 											unidad_ejecutora = pago.cunidadresponsable ");
        querySelect.append(" 											AND partida = paas.cidsubpartida ");
        querySelect.append(" 											AND rfc = ?) ");
        List<EgresoExcedeUMA> partidaList = runner.query(conn, querySelect.toString(), egresoExcedeUMAHandler, rfc, folioPagoDirecto, rfc, rfc);
        return partidaList;
    }

    public String getFolioAutSICOP() {
        return folioAutSICOP;
    }

    public void setFolioAutSICOP(String folioAutSICOP) {
        this.folioAutSICOP = folioAutSICOP;
    }

    public String getFolioSuficiencia() {
        return folioSuficiencia;
    }

    public void setFolioSuficiencia(String folioSuficiencia) {
        this.folioSuficiencia = folioSuficiencia;
    }

    public String getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(String idContrato) {
        this.idContrato = idContrato;
    }
}
