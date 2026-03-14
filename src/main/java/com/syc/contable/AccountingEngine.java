package com.syc.contable;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang.StringUtils;
import com.scottlogic.util.SortedList;
import com.syc.contable.util.Math;
import com.syc.contable.util.TimeFormat;
import com.syc.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountingEngine extends DataSourceManager {

    public static final String TABLE_HEADER_SUFIX = "Encabezado";

    public static final String TABLE_DETAIL_SUFIX = "Detalle";

    public static final String FIELD_EVENT_NAME = "cEvento";

    private static final int INITIAL_CAPACITY = 5000;

    private static final Logger log = LoggerFactory.getLogger(AccountingEngine.class);

    private static final String sqlInsertPoliza = "INSERT INTO tPoliza (nFolioPoliza, fCreacion, cDescripcionPoliza, mTotalCargo, mTotalAbono, nMes, nCuenta, fAplicacion, " + "nPolizaAutomatica, cCentroContable, aEjercicioFiscal, cTipoPoliza, cTipoDocumento, nFolioDocumento) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String sqlUpdatePoliza = "UPDATE tPoliza WITH(ROWLOCK) SET mTotalCargo = ?, mTotalAbono = ? WHERE aEjercicioFiscal = ? AND cCentroContable = ? AND cTipoPoliza = ? " + "AND nFolioPoliza = ?";

    private static final String sqlInsertMovto = "INSERT INTO tMovimiento (aEjercicioFiscal, cCentroContable, nCuenta, nSubCuenta, nFolioPoliza, cRamo, cUnidadResponsable, " + "nDocRenglon, mMovimiento, cTipoMovimiento, cDescripcionMovPol, cTipoDocumento, fOperacionMovimiento, cFolioDocumentoMovimiento, cCancelaMovimiento, " + "fMovimiento, dConceptoMovimiento, cMoneda, cTipoPoliza, Periodo13, ADEFAS, nTipoAjuste, parcial) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final //TENIA-->ISNULL(cSubCuenta, 'x') = ISNULL(?, 'x')
    String //TENIA-->ISNULL(cSubCuenta, 'x') = ISNULL(?, 'x')
    sqlExisteSaldo = "SELECT * FROM tSaldos WITH(ROWLOCK) " + "WHERE cRamo = ?  AND cUnidadResponsable = ? AND aEjercicioFiscal = ? AND cCentroContable = ? AND nCuenta = ? AND ISNULL(cSubCuenta, '') = ISNULL(?, '')";

    private static final //TENIA ? EN LUGAR DE isnull(?,'')
    String //TENIA ? EN LUGAR DE isnull(?,'')
    sqlInsertSaldo = "INSERT INTO tSaldos (aEjercicioFiscal, cCentroContable, cRamo, cUnidadResponsable, nCuenta,  cSubCuenta, cMoneda, " + "nMesPrimerMovimiento, nMesArrastre, mSaldoArrastre, mSaldo0, mSaldo1, mHaber1, mDeber1, mSaldo2, mHaber2, mDeber2, mSaldo3, mHaber3, mDeber3, " + "mSaldo4, mHaber4, mDeber4, mSaldo5, mHaber5, mDeber5, mSaldo6, mHaber6, mDeber6, mSaldo7, mHaber7, mDeber7, mSaldo8, mHaber8, mDeber8, " + "mSaldo9, mHaber9, mDeber9, mSaldo10, mHaber10, mDeber10, mSaldo11, mHaber11, mDeber11, mSaldo12, mHaber12, mDeber12, mSaldo13, mHaber13, mDeber13) " + "VALUES (?, ?, ?, ?, ?, isnull(?,''), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final // = ISNULL(?, 'x')
    String // = ISNULL(?, 'x')
    sqlUpdateSaldo = "UPDATE tSaldos WITH(ROWLOCK) SET nMesArrastre = ?, mSaldoArrastre = ?, mSaldo1 = ?, mHaber1 = ?, mDeber1 = ?, " + "mSaldo2 = ?, mHaber2 = ?, mDeber2 = ?, mSaldo3 = ?, mHaber3 = ?, mDeber3 = ?, mSaldo4 = ?, mHaber4 = ?, mDeber4 = ?, " + "mSaldo5 = ?, mHaber5 = ?, mDeber5 = ?, mSaldo6 = ?, mHaber6 = ?, mDeber6 = ?, mSaldo7 = ?, mHaber7 = ?, mDeber7 = ?, " + "mSaldo8 = ?, mHaber8 = ?, mDeber8 = ?, mSaldo9 = ?, mHaber9 = ?, mDeber9 = ?, mSaldo10 = ?, mHaber10 = ?, mDeber10 = ?, " + "mSaldo11 = ?, mHaber11 = ?, mDeber11 = ?, mSaldo12 = ?, mHaber12 = ?, mDeber12 = ?, mSaldo13 = ?, mHaber13 = ?, mDeber13 = ?, nMesPrimerMovimiento = ? " + "WHERE cRamo = ? AND cUnidadResponsable = ? AND aEjercicioFiscal = ? AND cCentroContable = ? AND nCuenta = ? AND ISNULL(cSubCuenta, 'x') = ISNULL(?, '')";

    private int batchSize = 15000;

    private boolean conInsuficienciaDeSaldo = true;

    private boolean validaDocumentoAplicadoCancelado = true;

    private String fechaCancelaDocto = null;

    // CONSTRUCTORES
    public AccountingEngine() {
    }

    public AccountingEngine(String jndiName) {
        init(jndiName);
    }

    // METODOS PUBLICOS
    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public boolean validaInsuficienciaDeSaldo() {
        return conInsuficienciaDeSaldo;
    }

    public void setValidaInsuficienciaDeSaldo(boolean conInsuficienciaDeSaldo) {
        this.conInsuficienciaDeSaldo = conInsuficienciaDeSaldo;
    }

    public boolean validaDocumentoAplicadoCancelado() {
        return validaDocumentoAplicadoCancelado;
    }

    public void setValidaDocumentoAplicadoCancelado(boolean validaDocumentoAplicadoCancelado) {
        this.validaDocumentoAplicadoCancelado = validaDocumentoAplicadoCancelado;
    }

    public boolean makeAllAccountingApplication(Connection conn, String[] documentsNames) throws AccountingEngineException {
        boolean retVal = false;
        String token = "";
        StringBuffer sql = new StringBuffer("SELECT * FROM (\n");
        if (documentsNames == null)
            throw new AccountingEngineException("Los nombres de documentos no debe ser nulo");
        // Para que NO VALIDE la insuficiencia de saldo
        setValidaInsuficienciaDeSaldo(false);
        // Para que NO VALIDE documento aplicado contablemente
        setValidaDocumentoAplicadoCancelado(false);
        PreparedStatement psSelect = null;
        ResultSet rs = null;
        try {
            String fieldName;
            // Arma union query con los documentsNames
            for (int i = 0; i < documentsNames.length; i++) {
                fieldName = new String("nFolio" + documentsNames[i]);
                sql.append(token).append("SELECT '" + documentsNames[i] + "' AS docName, fAplicacion, " + fieldName + " AS folio FROM t" + documentsNames[i] + TABLE_HEADER_SUFIX + "  WITH (NOLOCK) WHERE cDocumentoHaplicado = 'R' AND DATEPART(YEAR, fAplicacion) = (select aEjercicioFiscal from tEjercicioFiscal where cActivo=1)\n");
                token = "UNION\n";
            }
            sql.append(") AS tbl\n");
            sql.append("ORDER BY fAplicacion");
            log.debug("Object: {}", "Union Query:\n" + sql);
            try {
                boolean result = false;
                psSelect = conn.prepareStatement(sql.toString());
                rs = psSelect.executeQuery();
                while (rs.next()) {
                    fieldName = new String("nFolio" + rs.getString("docName"));
                    try {
                        result = makeAccountingApplication(conn, rs.getString("docName"), rs.getString("folio"), "t" + rs.getString("docName"), fieldName);
                    } catch (Exception exc) {
                        log.warn("Al procesar documento " + rs.getString("docName") + " con folio " + rs.getString("folio"), exc);
                        result = false;
                    }
                    if (result)
                        conn.commit();
                }
            } finally {
                try {
                    if (rs != null)
                        rs.close();
                } catch (Exception exc) {
                    log.warn("Cerrando ResultSet", exc);
                }
                try {
                    if (psSelect != null)
                        psSelect.close();
                } catch (Exception exc) {
                    log.warn("Cerrando PreparedStatement", exc);
                }
                rs = null;
                psSelect = null;
            }
            retVal = true;
        } catch (Exception exc) {
            if (exc instanceof AccountingEngineException)
                throw (AccountingEngineException) exc;
            else
                throw new AccountingEngineException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception exc) {
                log.warn("Cerrando ResultSet", exc);
            }
            try {
                if (psSelect != null)
                    psSelect.close();
            } catch (Exception exc) {
                log.warn("Cerrando PreparedStatement", exc);
            }
            rs = null;
            psSelect = null;
        }
        return retVal;
    }

    public boolean makeAllAccountingApplication(Connection conn, String[] documentsNames, String[] fieldsNames) throws AccountingEngineException {
        boolean retVal = false;
        if (documentsNames == null)
            throw new AccountingEngineException("Los nombres de documentos no debe ser nulo");
        if (fieldsNames == null)
            throw new AccountingEngineException("Los nombres de campos no debe ser nulo");
        if (documentsNames.length != fieldsNames.length)
            throw new AccountingEngineException("El total de documentos (" + documentsNames.length + ") y campos (" + fieldsNames.length + ") deben ser iguales");
        // Para que NO VALIDE la insuficiencia de saldo
        setValidaInsuficienciaDeSaldo(false);
        // Para que NO VALIDE documento aplicado contablemente
        setValidaDocumentoAplicadoCancelado(false);
        PreparedStatement psSelect = null;
        ResultSet rs = null;
        try {
            for (int i = 0; i < documentsNames.length; i++) {
                log.info("Object: {}", String.format("Procesando documentos de %s...", documentsNames[i]));
                try {
                    psSelect = conn.prepareStatement("SELECT " + fieldsNames[i] + " FROM t" + documentsNames[i] + TABLE_HEADER_SUFIX + " WITH (NOLOCK) WHERE cDocumentoHaplicado = 'R'");
                    rs = psSelect.executeQuery();
                    while (rs.next()) makeAccountingApplication(conn, documentsNames[i], rs.getString(fieldsNames[i]), "t" + documentsNames[i], fieldsNames[i]);
                } finally {
                    try {
                        if (rs != null)
                            rs.close();
                    } catch (Exception exc) {
                        log.warn("Cerrando ResultSet", exc);
                    }
                    try {
                        if (psSelect != null)
                            psSelect.close();
                    } catch (Exception exc) {
                        log.warn("Cerrando PreparedStatement", exc);
                    }
                    rs = null;
                    psSelect = null;
                }
                log.info("Object: {}", String.format("Documentos de %s procesados", documentsNames[i]));
            }
            retVal = true;
        } catch (Exception exc) {
            if (exc instanceof AccountingEngineException)
                throw (AccountingEngineException) exc;
            else
                throw new AccountingEngineException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception exc) {
                log.warn("Cerrando ResultSet", exc);
            }
            try {
                if (psSelect != null)
                    psSelect.close();
            } catch (Exception exc) {
                log.warn("Cerrando PreparedStatement", exc);
            }
            rs = null;
            psSelect = null;
        }
        return retVal;
    }

    public boolean makeAccountingApplication(Connection conn, String documentName, String id, String tablePrefix, String fieldName) throws AccountingEngineException, DocumentAppliedException {
        return makeAccountingApplication(conn, documentName, id, tablePrefix + TABLE_HEADER_SUFIX, tablePrefix + TABLE_DETAIL_SUFIX, fieldName, INITIAL_CAPACITY);
    }

    public boolean makeAccountingApplication(Connection conn, String documentName, String id, String tablePrefix, String fieldName, int initialCapacity) throws AccountingEngineException, DocumentAppliedException {
        return makeAccountingApplication(conn, documentName, id, tablePrefix + TABLE_HEADER_SUFIX, tablePrefix + TABLE_DETAIL_SUFIX, fieldName, initialCapacity);
    }

    public boolean makeAccountingApplication(Connection conn, String documentName, String id, String tableHeader, String tableDetail, String fieldName) throws AccountingEngineException, DocumentAppliedException {
        return makeAccountingApplication(conn, documentName, id, tableHeader, tableDetail, fieldName, INITIAL_CAPACITY);
    }

    public boolean makeAccountingApplication(Connection conn, String documentName, String id, String tableHeader, String tableDetail, String fieldName, int initialCapacity) throws AccountingEngineException, DocumentAppliedException {
        return makeAccountingApplication(conn, documentName, id, tableHeader, tableDetail, fieldName, new AccountingEventProcessor(), initialCapacity);
    }

    @SuppressWarnings("unchecked")
    public boolean makeAccountingApplication(Connection conn, String documentName, String id, String tableHeader, String tableDetail, String fieldName, AccountingEventProcessorInterface aep, int initialCapacity) throws AccountingEngineException, DocumentAppliedException {
        boolean retVal = true;
        PreparedStatement psUpdateDocto = null;
        PreparedStatement psUpdateDoctoProc = null;
        DocumentAccounting da = new DocumentAccounting(initialCapacity);
        String sqlUpdateDocto = "UPDATE " + tableHeader + " WITH(ROWLOCK) SET cDocumentoHaplicado = 'S', nFolioPoliza = ? WHERE " + fieldName + " = ?";
        String sqlUpdateDoctoProc = "UPDATE " + tableHeader + " WITH(ROWLOCK) SET cDocumentoHaplicado = 'P' WHERE " + fieldName + " = ?";
        int folioPoliza = 0;
        try {
            psUpdateDocto = conn.prepareStatement(sqlUpdateDocto);
            psUpdateDoctoProc = conn.prepareStatement(sqlUpdateDoctoProc);
            // Lee encabezado y detalle del documento
            long start = System.currentTimeMillis();
            da = readDocumentData(conn, id, tableHeader, tableDetail, fieldName, initialCapacity);
            if (validaDocumentoAplicadoCancelado() && "P".equalsIgnoreCase(da.getDataHeaderMap().get("cDocumentoHaplicado")))
                throw new AccountingEngineException(String.format("El documento %s con folio %s esta siendo Procesado", documentName.toUpperCase(), id));
            if (validaDocumentoAplicadoCancelado() && "S".equalsIgnoreCase(da.getDataHeaderMap().get("cDocumentoHaplicado")))
                throw new DocumentAppliedException(String.format("El documento %s con folio %s ya hab\u00eda sido aplicado", documentName.toUpperCase(), id));
            if (validaDocumentoAplicadoCancelado() && "C".equalsIgnoreCase(da.getDataHeaderMap().get("cDocumentoHaplicado")))
                throw new AccountingEngineException(String.format("El documento %s con folio %s ya hab\u00eda sido aplicado y cancelado", documentName.toUpperCase(), id));
            psUpdateDoctoProc.setInt(1, Integer.parseInt(id));
            psUpdateDoctoProc.executeUpdate();
            if (!"".equalsIgnoreCase(da.getDataHeaderMap().get("nFolioPoliza"))) {
                folioPoliza = Integer.parseInt(da.getDataHeaderMap().get("nFolioPoliza"));
            }
            // Genera movimientos
            List<AccountingMovement> movements = new SortedList<AccountingMovement>(new AccountingMovementComparator());
            Map<String, String> values, header = da.getDataHeaderMap();
            List<Map<String, String>> detail = da.getDataDetailMap();
            if (log.isDebugEnabled()) {
                DateFormat timeFormatter = new TimeFormat();
                log.debug("Object: {}", String.format("Documento %s con encabezado de %,.0f columnas y detalle de %,.0f columnas con %,.0f renglones, cargado en %s", documentName.toUpperCase(), (double) header.size(), (double) (detail.size() > 0 ? detail.get(0).size() : 0), (double) detail.size(), timeFormatter.format(new Date(System.currentTimeMillis() - start))));
            }
            // Campos Virtuales
            header.put("cTipoDocumento", documentName);
            // header.put("cFolioDocumentoMovimiento", header.get("cRamo") + "." + header.get("cUnidadResponsable") + "." + id);
            header.put("cFolioDocumentoMovimiento", id);
            for (Map<String, String> data : detail) {
                values = new CaseInsensitiveMap();
                values.putAll(header);
                values.putAll(data);
                movements.addAll(aep.getMovements(conn, data.get(FIELD_EVENT_NAME), values));
            }
            // Procesa movimientos
            if (log.isInfoEnabled()) {
                start = System.currentTimeMillis();
                log.info("Object: {}", String.format("Procesando documento %s (%s) con %,.0f movimientos...", documentName, id, (double) movements.size()));
            }
            /* se actualiza fecha de cancelación en el caso de que la fecha de aplicación del documento este en periodo contable cerrado y este se aplique con fecha actual*/
            String sMes = "";
            String sMesAbierto = "";
            String sQueryPeriodoContable = "";
            PreparedStatement pstmntQuery = null;
            ResultSet rs = null;
            String DATE_FORMAT = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            // today
            Calendar c1 = Calendar.getInstance();
            String today = sdf.format(c1.getTime());
            String cUnidadResponsable = "";
            String aEjercicioFiscal = "";
            sMes = String.valueOf(header.get("faplicacion")).split("-")[1];
            sQueryPeriodoContable = "select mesAbierto, aEjercicioFiscal from tMesesContables WITH(nolock) where cUnidadResponsable = ? and nMes = ?";
            pstmntQuery = conn.prepareStatement(sQueryPeriodoContable);
            for (AccountingMovement mov : movements) {
                cUnidadResponsable = mov.getUnidadResponsable();
                break;
            }
            pstmntQuery.setString(1, cUnidadResponsable);
            pstmntQuery.setString(2, sMes);
            rs = pstmntQuery.executeQuery();
            if (rs.next()) {
                sMesAbierto = rs.getString("mesAbierto");
                aEjercicioFiscal = rs.getString("aEjercicioFiscal");
            }
            if (!aEjercicioFiscal.equals(today.split("-")[0])) {
                today = aEjercicioFiscal + "-12-31";
            }
            fechaCancelaDocto = null;
            if ("N".equals(sMesAbierto))
                fechaCancelaDocto = today;
            rs = null;
            pstmntQuery = null;
            /* aqui termina manejo de fecha de cancelación en caso de periodo contable cerrado*/
            ProcessMovementsResult pmr = processMovements(conn, documentName, tableHeader, fieldName, movements, header, folioPoliza);
            if (retVal = pmr.isSuccess()) {
                // Actualiza documento (tableHeader) como aplicado contablemente (TODO cDocumentoHaplicado en todos los encabezados de documentos)
                psUpdateDocto.setInt(1, pmr.getFolioPoliza());
                psUpdateDocto.setString(2, header.get(fieldName));
                psUpdateDocto.executeUpdate();
                if (log.isDebugEnabled()) {
                    log.debug("Object: {}", sqlUpdateDocto + " [" + pmr.getFolioPoliza() + ", (" + fieldName + ")" + header.get(fieldName) + "]");
                    start = System.currentTimeMillis();
                }
            } else {
                throw new AccountingEngineException("No Existe Detalle de Movimientos para Aplicar");
            }
            if (log.isInfoEnabled()) {
                DateFormat timeFormatter = new TimeFormat();
                log.info("Object: {}", String.format("Documento %s (%s) con %,.0f movimientos procesado en %s.", documentName, id, (double) movements.size(), timeFormatter.format(new Date(System.currentTimeMillis() - start))));
            }
        } catch (Exception exc) {
            retVal = false;
            log.error(exc.getMessage(), exc);
            if (exc instanceof AccountingEngineException)
                throw (AccountingEngineException) exc;
            else if (exc instanceof DocumentAppliedException)
                throw (DocumentAppliedException) exc;
            else
                throw new AccountingEngineException(exc);
        } finally {
            try {
                if (psUpdateDocto != null)
                    psUpdateDocto.close();
            } catch (Exception exc) {
                log.warn("Cerrando PreparedStatement", exc);
            }
        }
        return retVal;
    }

    public boolean makeAccountingApplicationWithoutEvent(Connection conn, String documentName, String id, String tablePrefix, String fieldName) throws AccountingEngineException, DocumentAppliedException {
        return makeAccountingApplicationWithoutEvent(conn, documentName, id, tablePrefix, fieldName, INITIAL_CAPACITY);
    }

    public boolean makeAccountingApplicationWithoutEvent(Connection conn, String documentName, String id, String tablePrefix, String fieldName, int initialCapacity) throws AccountingEngineException, DocumentAppliedException {
        return makeAccountingApplicationWithoutEvent(conn, documentName, id, tablePrefix + TABLE_HEADER_SUFIX, tablePrefix + TABLE_DETAIL_SUFIX, fieldName, initialCapacity);
    }

    public boolean makeAccountingApplicationWithoutEvent(Connection conn, String documentName, String id, String tableHeader, String tableDetail, String fieldName) throws AccountingEngineException, DocumentAppliedException {
        return makeAccountingApplicationWithoutEvent(conn, documentName, id, tableHeader, tableDetail, fieldName, INITIAL_CAPACITY);
    }

    public boolean makeAccountingApplicationWithoutEvent(Connection conn, String documentName, String id, String tableHeader, String tableDetail, String fieldName, int initialCapacity) throws AccountingEngineException, DocumentAppliedException {
        return makeAccountingApplicationWithoutEvent(conn, documentName, id, tableHeader, tableDetail, fieldName, new AccountingWithoutEventProcessor(), initialCapacity);
    }

    public boolean makeAccountingApplicationWithoutEvent(Connection conn, String documentName, String id, String tableHeader, String tableDetail, String fieldName, AccountingEventProcessorInterface aep, int initialCapacity) throws AccountingEngineException, DocumentAppliedException {
        return makeAccountingApplication(conn, documentName, id, tableHeader, tableDetail, fieldName, aep, initialCapacity);
    }

    public boolean cancelAccountingApplication(Connection conn, String documentName, String id, String tablePrefix, String fieldName) throws AccountingEngineException {
        return cancelAccountingApplication(conn, documentName, id, tablePrefix + TABLE_HEADER_SUFIX, tablePrefix + TABLE_DETAIL_SUFIX, fieldName, INITIAL_CAPACITY, fechaCancelaDocto);
    }

    public boolean cancelAccountingApplication(Connection conn, String documentName, String id, String tablePrefix, String fieldName, int initialCapacity) throws AccountingEngineException {
        return cancelAccountingApplication(conn, documentName, id, tablePrefix + TABLE_HEADER_SUFIX, tablePrefix + TABLE_DETAIL_SUFIX, fieldName, initialCapacity, fechaCancelaDocto);
    }

    public boolean cancelAccountingApplication(Connection conn, String documentName, String id, String tableHeader, String tableDetail, String fieldName) throws AccountingEngineException {
        return cancelAccountingApplication(conn, documentName, id, tableHeader, tableDetail, fieldName, INITIAL_CAPACITY, fechaCancelaDocto);
    }

    public boolean cancelAccountingApplication(Connection conn, String documentName, String id, String tableHeader, String tableDetail, String fieldName, String fechaCancelaDocto) throws AccountingEngineException {
        return cancelAccountingApplication(conn, documentName, id, tableHeader, tableDetail, fieldName, INITIAL_CAPACITY, fechaCancelaDocto);
    }

    public boolean cancelAccountingApplication(Connection conn, String documentName, String id, String tableHeader, String tableDetail, String fieldName, int initialCapacity, String fechaCancelaDocto) throws AccountingEngineException {
        boolean retVal = false;
        String sqlMovs = "SELECT m.cCentroContable, m.nCuenta, CASE LEN(LTRIM(RTRIM(m.nSubCuenta))) WHEN 0 THEN NULL ELSE LTRIM(RTRIM(m.nSubCuenta)) END AS nSubCuenta, " + "m.mMovimiento * -1 AS mMovimiento, m.cTipoMovimiento AS cargo, c.NaturalezaCuenta AS naturaleza, c.VerificaSaldo AS verificaSaldo, " + "m.aEjercicioFiscal, m.nDocRenglon, c.dCuenta, case when TipoBalance = 'P' and TipoCuenta = 'P' then 1 else 0 end as bPresupuesto FROM tMovimiento AS m WITH (NOLOCK), tCuentas AS c WITH (NOLOCK) " + "WHERE m.nCuenta = c.nCuenta AND cTipoDocumento = ? AND cFolioDocumentoMovimiento = ? AND cCancelaMovimiento IS NULL " + "ORDER BY m.cCentroContable, m.nCuenta, [nSubCuenta]";
        String sqlUpdDoc = "UPDATE " + tableHeader + " WITH(ROWLOCK) SET cDocumentoHaplicado = 'C', fCancelacion = ?, nFolioPolizaCancelacion = ? WHERE " + fieldName + " = ?";
        String sqlUpdMovs = "UPDATE tMovimiento WITH(ROWLOCK) SET cCancelaMovimiento = 'C' WHERE cTipoDocumento = ? AND cFolioDocumentoMovimiento = ? AND nFolioPoliza = ?";
        PreparedStatement psUpdateDoctoProc = null;
        String sqlUpdateDoctoProc = "UPDATE " + tableHeader + " WITH(ROWLOCK) SET cDocumentoHaplicado = 'P' WHERE " + fieldName + " = ?";
        PreparedStatement psSelectMovs = null, psUpdateMovs = null, psUpdateDocto = null;
        ResultSet rs = null;
        DocumentAccounting da = new DocumentAccounting(initialCapacity);
        try {
            // Lee encabezado y detalle del documento
            da = readDocumentData(conn, id, tableHeader, tableDetail, fieldName, initialCapacity);
            if (validaDocumentoAplicadoCancelado() && "P".equalsIgnoreCase(da.getDataHeaderMap().get("cDocumentoHaplicado")))
                throw new AccountingEngineException(String.format("El documento %s con folio %s esta siendo Procesado", documentName.toUpperCase(), id));
            if (validaDocumentoAplicadoCancelado() && "C".equalsIgnoreCase(da.getDataHeaderMap().get("cDocumentoHaplicado")))
                throw new AccountingEngineException(String.format("El documento %s con folio %s ya hab\u00eda sido cancelado", documentName.toUpperCase(), id));
            if (validaDocumentoAplicadoCancelado() && !"S".equalsIgnoreCase(da.getDataHeaderMap().get("cDocumentoHaplicado")))
                throw new AccountingEngineException(String.format("El documento %s con folio %s no ha sido aplicado", documentName.toUpperCase(), id));
            psUpdateDoctoProc = conn.prepareStatement(sqlUpdateDoctoProc);
            psUpdateDoctoProc.setInt(1, Integer.parseInt(id));
            psUpdateDoctoProc.executeUpdate();
            psUpdateMovs = conn.prepareStatement(sqlUpdMovs);
            psUpdateDocto = conn.prepareStatement(sqlUpdDoc);
            Map<String, String> header = da.getDataHeaderMap();
            header.put("cTipoDocumento", documentName);
            header.put("cDescripcionPoliza", "Cancelaci\u00f3n de " + documentName);
            header.put("cFolioDocumentoMovimiento", id);
            // Lee movimientos del documento
            psSelectMovs = conn.prepareStatement(sqlMovs);
            //psSelectMovs.setString(1, header.get("cFolioDocumentoMovimiento"));
            //log.debug(sqlMovs + " [" + header.get("cFolioDocumentoMovimiento") + "]");
            psSelectMovs.setString(1, documentName);
            psSelectMovs.setString(2, id);
            log.debug("Object: {}", sqlMovs + " [" + documentName + ", " + id + "]");
            List<AccountingMovement> movements = readMovementsToCancel(psSelectMovs.executeQuery());
            // validación de fecha de documento y si el periodo contable está abierto
            String sMes = "";
            String sMesC = "";
            String sMesAbierto = "";
            String sQueryPeriodoContable = "";
            PreparedStatement pstmntQuery = null;
            ResultSet rsc = null;
            String DATE_FORMAT = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            // today
            Calendar c1 = Calendar.getInstance();
            String today = sdf.format(c1.getTime());
            String cUnidadResponsable = "";
            String aEjercicioFiscal = "";
            sMes = String.valueOf(header.get("faplicacion")).split("-")[1];
            if (fechaCancelaDocto == null)
                fechaCancelaDocto = today;
            sMesC = ((fechaCancelaDocto.indexOf("-") > 0) ? fechaCancelaDocto.split("-") : fechaCancelaDocto.split("/"))[1];
            sQueryPeriodoContable = "select mesAbierto, aEjercicioFiscal from tMesesContables WITH(nolock)  where cUnidadResponsable = ? and nMes = ? ";
            pstmntQuery = conn.prepareStatement(sQueryPeriodoContable);
            cUnidadResponsable = (StringUtils.isBlank(header.get("cunidadresponsable")) ? da.getDataDetailMap().get(0).get("cunidadresponsable") : header.get("cunidadresponsable"));
            if (StringUtils.isBlank(cUnidadResponsable))
                throw new AccountingEngineException("El encabezado no cuenta con la columna cUnidadResponsable");
            //			for (AccountingMovement mov : movements) {
            //				cUnidadResponsable = mov.getUnidadResponsable();
            //				break;
            //			}
            pstmntQuery.setString(1, cUnidadResponsable);
            pstmntQuery.setString(2, sMes);
            rsc = pstmntQuery.executeQuery();
            if (rsc.next()) {
                sMesAbierto = rsc.getString("mesAbierto");
                aEjercicioFiscal = rsc.getString("aEjercicioFiscal");
            }
            //fechaCancelaDocto = null;
            if ("S".equals(sMesAbierto) && !sMes.equals(sMesC)) {
                c1.set(Calendar.MONTH, Integer.parseInt(sMes) - 1);
                c1.set(Calendar.DAY_OF_MONTH, c1.getActualMaximum(Calendar.DAY_OF_MONTH));
                today = sdf.format(c1.getTime());
                fechaCancelaDocto = today;
            }
            if (!aEjercicioFiscal.equals(today.split("-")[0])) {
                today = aEjercicioFiscal + "-12-31";
                fechaCancelaDocto = today;
            }
            // termina validación fecha de doccumento y periodo contable
            // Procesa Movimientos
            ProcessMovementsResult pmr = processMovements(conn, documentName, tableHeader, fieldName, movements, header, 0, fechaCancelaDocto);
            if (retVal = pmr.isSuccess()) {
                long now = System.currentTimeMillis();
                // Actualiza movimientos (cCancelaMovimiento = 'C')
                psUpdateMovs.setString(1, documentName);
                psUpdateMovs.setString(2, id);
                psUpdateMovs.setString(3, header.get("nFolioPoliza"));
                log.debug("Object: {}", sqlUpdMovs + " [" + documentName + ", " + id + ", " + header.get("nFolioPoliza") + "]");
                psUpdateMovs.executeUpdate();
                // Actualiza documento (tableHeader) como cancelado contablemente
                // (TODO cDocumentoHaplicado, nFolioPolizaCancelacion y fCancelacion en todos los encabezados de documentos)
                psUpdateDocto.setString(1, fechaCancelaDocto);
                psUpdateDocto.setInt(2, pmr.getFolioPoliza());
                psUpdateDocto.setString(3, header.get(fieldName));
                log.debug("Object: {}", sqlUpdDoc + " [" + new Date(now) + ", " + pmr.getFolioPoliza() + ", " + header.get(fieldName) + "]");
                psUpdateDocto.executeUpdate();
            } else {
                throw new AccountingEngineException("No Existe Detalle de Movimientos para Aplicar");
            }
        } catch (Exception exc) {
            retVal = false;
            if (exc instanceof AccountingEngineException)
                throw (AccountingEngineException) exc;
            else
                throw new AccountingEngineException(exc);
        } finally {
            try {
                if ((rs != null) && !rs.isClosed())
                    rs.close();
            } catch (Exception exc) {
                log.warn("Cerrando ResultSet", exc);
            }
            try {
                if ((psSelectMovs != null) && !psSelectMovs.isClosed())
                    psSelectMovs.close();
            } catch (Exception exc) {
                log.warn("Cerrando PreparedStatement", exc);
            }
            rs = null;
            psSelectMovs = null;
        }
        return retVal;
    }

    // METODOS PRIVADOS
    private DocumentAccounting readDocumentData(Connection conn, String id, String tableHeader, String tableDetail, String fieldName, int initialCapacity) throws AccountingEngineException {
        String sql;
        PreparedStatement pstmntHeader = null, pstmntDetail = null;
        DocumentAccounting da = new DocumentAccounting(initialCapacity);
        try {
            sql = "SELECT * FROM " + tableHeader + " WITH (NOLOCK) WHERE " + fieldName + " = ?";
            log.debug("Object: {}", "SQL Header: " + sql + " [" + id + "]");
            pstmntHeader = conn.prepareStatement(sql);
            pstmntHeader.setString(1, id);
            readDataHeaderFromResultSet(pstmntHeader.executeQuery(), da.getDataHeaderMap());
            sql = "SELECT * FROM " + tableDetail + " WITH (NOLOCK) WHERE " + fieldName + " = ?";
            log.debug("Object: {}", "SQL Detail: " + sql + " [" + id + "]");
            pstmntDetail = conn.prepareStatement(sql);
            pstmntDetail.setString(1, id);
            readDataDetailFromResultSet(pstmntDetail.executeQuery(), da.getDataDetailMap());
        } catch (Exception exc) {
            if (exc instanceof AccountingEngineException)
                throw (AccountingEngineException) exc;
            else
                throw new AccountingEngineException(exc);
        } finally {
            try {
                if (pstmntDetail != null)
                    pstmntDetail.close();
            } catch (Exception exc) {
                log.warn("Cerrando PreparedStatement", exc);
            }
            try {
                if (pstmntHeader != null)
                    pstmntHeader.close();
            } catch (Exception exc) {
                log.warn("Cerrando PreparedStatement", exc);
            }
            pstmntDetail = null;
            pstmntHeader = null;
        }
        return da;
    }

    private void readDataHeaderFromResultSet(ResultSet rs, Map<String, String> data) throws AccountingEngineException {
        try {
            ResultSetMetaData mrs = rs.getMetaData();
            int maxCol = mrs.getColumnCount();
            if (rs.next())
                for (int i = 0; i < maxCol; i++) data.put(mrs.getColumnName(i + 1), rs.getString(i + 1) == null ? "" : rs.getString(i + 1));
        } catch (Exception exc) {
            if (exc instanceof AccountingEngineException)
                throw (AccountingEngineException) exc;
            else
                throw new AccountingEngineException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception exc) {
                log.warn("Cerrando ResultSet", exc);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void readDataDetailFromResultSet(ResultSet rs, List<Map<String, String>> data) throws AccountingEngineException {
        try {
            ResultSetMetaData mrs = rs.getMetaData();
            int maxCol = mrs.getColumnCount();
            Map<String, String> row;
            while (rs.next()) {
                row = new CaseInsensitiveMap(maxCol);
                for (int i = 0; i < maxCol; i++) row.put(mrs.getColumnName(i + 1), rs.getString(i + 1) == null ? "" : rs.getString(i + 1));
                data.add(row);
            }
        } catch (Exception exc) {
            if (exc instanceof AccountingEngineException)
                throw (AccountingEngineException) exc;
            else
                throw new AccountingEngineException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception exc) {
                log.warn("Cerrando ResultSet", exc);
            }
        }
    }

    private List<AccountingMovement> readMovementsToCancel(ResultSet rs) throws Exception {
        List<AccountingMovement> movements = new SortedList<AccountingMovement>(new AccountingMovementComparator());
        while (rs.next()) {
            movements.add(new AccountingMovement(rs.getString("nCuenta"), rs.getString("nSubCuenta"), rs.getDouble("mMovimiento"), "C".equals(rs.getString("cargo")), "D".equals(rs.getString("naturaleza")), "S".equals(rs.getString("verificaSaldo")), rs.getString("cCentroContable"), rs.getString("aEjercicioFiscal"), rs.getInt("nDocRenglon"), rs.getString("nCuenta"), rs.getBoolean("bPresupuesto"), ""));
        }
        return movements;
    }

    /*
	private ProcessMovementsResult processMovements(Connection conn, String documentName, String tableHeader, String fieldName, List<AccountingMovement> movements,
	        Map<String, String> values) throws AccountingEngineException {

		boolean retVal = true;

		if (movements.size() <= 0)
			return new ProcessMovementsResult(1, true);

		int folioPoliza = 1;

		PreparedStatement psInsertPoliza = null;
		PreparedStatement psUpdatePoliza = null;
		PreparedStatement psInsertSaldo = null;
		PreparedStatement psUpdateSaldo = null;
		PreparedStatement psInsertMovto = null;

		try {
			psInsertPoliza = conn.prepareStatement(sqlInsertPoliza);
			psUpdatePoliza = conn.prepareStatement(sqlUpdatePoliza);

			psInsertMovto = conn.prepareStatement(sqlInsertMovto);

			psInsertSaldo = conn.prepareStatement(sqlInsertSaldo);
			psUpdateSaldo = conn.prepareStatement(sqlUpdateSaldo);

			boolean firstTime = true;
			double cargos = 0, abonos = 0, cargosGrupoMovtos = 0, abonosGrupoMovtos = 0;
			AccountingMovement movtoAnterior = null;

			if (values.get("cTipoPoliza") == null)
				throw new AccountingEngineException("cTipoPoliza no debe ser nulo");

			if (values.get("cTipoPoliza").trim().length() == 0)
				throw new AccountingEngineException("cTipoPoliza no debe ser vacio y/o con espacios en blanco");

			int batchCount = 0;

			for (AccountingMovement mov : movements) {
				if (firstTime) {
					firstTime = false;
					movtoAnterior = mov;
					folioPoliza = getNextFolioPoliza(mov.getEjercicioFiscal(), mov.getCentroContable(), values.get("cTipoPoliza"));
					// Adiciona Poliza al Batch
					addPoliza(conn, psInsertPoliza, documentName, fieldName, folioPoliza, cargos, abonos, values, movtoAnterior);
				}

				if (!(movtoAnterior.getPK()).equals(mov.getPK())) {
					// Adiciona Saldo al Batch
					updateSaldo(conn, psInsertSaldo, psUpdateSaldo, cargosGrupoMovtos, abonosGrupoMovtos, movtoAnterior, values);

					cargos += cargosGrupoMovtos;
					abonos += abonosGrupoMovtos;
					cargosGrupoMovtos = abonosGrupoMovtos = 0;
				}

				if (!movtoAnterior.getCentroContable().equals(mov.getCentroContable())) {
					if (Math.truncate(cargos, 2) != Math.truncate(abonos, 2))
						throw new AccountingEngineException(String.format("Diferencia en movimientos del centro contable %s (cargos=%,.2f, abonos=%,.2f)",
						        movtoAnterior.getCentroContable(),
						        Math.truncate(cargos, 2),
						        Math.truncate(abonos, 2)));

					// Actualiza totales de la Poliza
					updatePoliza(psUpdatePoliza, movtoAnterior.getEjercicioFiscal(), movtoAnterior.getCentroContable(), values.get("cTipoPoliza"), folioPoliza, cargos, abonos);
					cargos = abonos = 0;

					movtoAnterior = mov;
					folioPoliza = getNextFolioPoliza(mov.getEjercicioFiscal(), mov.getCentroContable(), values.get("cTipoPoliza"));
					// Adiciona Poliza al Batch
					addPoliza(conn, psInsertPoliza, documentName, fieldName, folioPoliza, cargos, abonos, values, movtoAnterior);
				}

				mov.setFolioPoliza(folioPoliza);

				if (mov.isCargo())
					cargosGrupoMovtos += mov.getMovimiento();
				else
					abonosGrupoMovtos += mov.getMovimiento();

				// Adiciona Movimiento al Batch
				addMovement(psInsertMovto, fieldName, mov, values);

				batchCount++;
				// Alcanzo el total de linea de valores deseado del batch?
				if ((batchCount % batchSize) == 0) {
					batchCount = 0;

					// Ejecuta Batch Polizas
					validaTotalRegistrosAfectados(psInsertPoliza.executeBatch(), "InsertPoliza");
					validaTotalRegistrosAfectados(psUpdatePoliza.executeBatch(), "UpdatePoliza");
					// Ejecuta Batch Movimientos
					validaTotalRegistrosAfectados(psInsertMovto.executeBatch(), "InsertMovto");
					// Ejecuta Batches Saldos
					validaTotalRegistrosAfectados(psInsertSaldo.executeBatch(), "InsertSaldo");
					validaTotalRegistrosAfectados(psUpdateSaldo.executeBatch(), "UpdateSaldo");

					// Limpia batches
					psInsertPoliza.clearBatch();
					psUpdatePoliza.clearBatch();
					psInsertMovto.clearBatch();
					psInsertSaldo.clearBatch();
					psUpdateSaldo.clearBatch();
				}

				movtoAnterior = mov;
				if (log.isDebugEnabled())
					log.debug(mov);
			}

			if (batchCount > 0) {
				// Adiciona Saldo al Batch
				updateSaldo(conn, psInsertSaldo, psUpdateSaldo, cargosGrupoMovtos, abonosGrupoMovtos, movtoAnterior, values);

				cargos += cargosGrupoMovtos;
				abonos += abonosGrupoMovtos;
			}

			if (batchCount > 0) {
				if (Math.truncate(cargos, 2) != Math.truncate(abonos, 2))
					throw new AccountingEngineException(String.format("Diferencia en movimientos del centro contable %s (cargos=%,.2f, abonos=%,.2f)",
					        movtoAnterior.getCentroContable(),
					        Math.truncate(cargos, 2),
					        Math.truncate(abonos, 2)));

				// Actualiza totales de la Poliza
				updatePoliza(psUpdatePoliza, movtoAnterior.getEjercicioFiscal(), movtoAnterior.getCentroContable(), values.get("cTipoPoliza"), folioPoliza, cargos, abonos);
			}

			if (batchCount > 0) {
				// Ejecuta Batch Polizas
				validaTotalRegistrosAfectados(psInsertPoliza.executeBatch(), "InsertPoliza");
				validaTotalRegistrosAfectados(psUpdatePoliza.executeBatch(), "UpdatePoliza");
				// Ejecuta Batch Movimientos
				validaTotalRegistrosAfectados(psInsertMovto.executeBatch(), "InsertMovto");
				// Ejecuta Batches Saldos
				validaTotalRegistrosAfectados(psInsertSaldo.executeBatch(), "InsertSaldo");
				validaTotalRegistrosAfectados(psUpdateSaldo.executeBatch(), "UpdateSaldo");
			}
		} catch (Exception exc) {
			retVal = false;
			if (exc instanceof AccountingEngineException)
				throw (AccountingEngineException) exc;
			else
				throw new AccountingEngineException(exc);
		} finally {
			try {
				if (psUpdateSaldo != null)
					psUpdateSaldo.close();
			} catch (Exception exc) {
				log.warn("Cerrando PreparedStatement", exc);
			}

			try {
				if (psInsertSaldo != null)
					psInsertSaldo.close();
			} catch (Exception exc) {
				log.warn("Cerrando PreparedStatement", exc);
			}

			try {
				if (psInsertMovto != null)
					psInsertMovto.close();
			} catch (Exception exc) {
				log.warn("Cerrando PreparedStatement", exc);
			}

			try {
				if (psUpdatePoliza != null)
					psUpdatePoliza.close();
			} catch (Exception exc) {
				log.warn("Cerrando PreparedStatement", exc);
			}

			try {
				if (psInsertPoliza != null)
					psInsertPoliza.close();
			} catch (Exception exc) {
				log.warn("Cerrando PreparedStatement", exc);
			}

			psUpdateSaldo = null;
			psInsertSaldo = null;
			psInsertMovto = null;
			psUpdatePoliza = null;
			psInsertPoliza = null;
		}

		return new ProcessMovementsResult(folioPoliza, retVal);
	}
	*/
    private ProcessMovementsResult processMovements(Connection conn, String documentName, String tableHeader, String fieldName, List<AccountingMovement> movements, Map<String, String> values, int nFolioPoliza) throws AccountingEngineException {
        return processMovements(conn, documentName, tableHeader, fieldName, movements, values, nFolioPoliza, fechaCancelaDocto);
    }

    private ProcessMovementsResult processMovements(Connection conn, String documentName, String tableHeader, String fieldName, List<AccountingMovement> movements, Map<String, String> values, int nFolioPoliza, String fechaCancelaDocto) throws AccountingEngineException {
        boolean retVal = true;
        if (movements.size() <= 0)
            return new ProcessMovementsResult(1, false);
        int folioPoliza = 1;
        PreparedStatement psInsertPoliza = null;
        PreparedStatement psUpdatePoliza = null;
        PreparedStatement psInsertSaldo = null;
        PreparedStatement psUpdateSaldo = null;
        PreparedStatement psInsertMovto = null;
        try {
            psInsertPoliza = conn.prepareStatement(sqlInsertPoliza);
            psUpdatePoliza = conn.prepareStatement(sqlUpdatePoliza);
            psInsertMovto = conn.prepareStatement(sqlInsertMovto);
            List<BalancesToUpdate> balancesToUpdate = new ArrayList<AccountingEngine.BalancesToUpdate>();
            boolean firstTime = true;
            double cargos = 0, abonos = 0, cargosGrupoMovtos = 0, abonosGrupoMovtos = 0;
            AccountingMovement movtoAnterior = null;
            if (values.get("cTipoPoliza") == null)
                throw new AccountingEngineException("cTipoPoliza no debe ser nulo");
            if (values.get("cTipoPoliza").trim().length() == 0)
                throw new AccountingEngineException("cTipoPoliza no debe ser vacio y/o con espacios en blanco");
            int batchCount = 0;
            DateFormat timeFormatter = new TimeFormat();
            long startTime = System.currentTimeMillis();
            log.info("Object: {}", String.format("Procesando %,.0f movimientos de %s(%s)...", (double) movements.size(), documentName, values.get(fieldName)));
            for (AccountingMovement mov : movements) {
                if (firstTime) {
                    firstTime = false;
                    movtoAnterior = mov;
                    if (nFolioPoliza == 0) {
                        folioPoliza = getNextFolioPoliza(conn, mov.getEjercicioFiscal(), mov.getCentroContable(), values.get("cTipoPoliza"));
                        // Adiciona Poliza al Batch
                        addPoliza(conn, psInsertPoliza, documentName, fieldName, folioPoliza, cargos, abonos, values, movtoAnterior, fechaCancelaDocto);
                    } else {
                        folioPoliza = nFolioPoliza;
                    }
                }
                if (!(movtoAnterior.getPK()).equals(mov.getPK())) {
                    // Adiciona Saldo a lista
                    if ((cargosGrupoMovtos != 0) || (abonosGrupoMovtos != 0))
                        balancesToUpdate.add(new BalancesToUpdate(movtoAnterior, cargosGrupoMovtos, abonosGrupoMovtos));
                    cargos += cargosGrupoMovtos;
                    abonos += abonosGrupoMovtos;
                    cargosGrupoMovtos = abonosGrupoMovtos = 0;
                }
                if (!movtoAnterior.getCentroContable().equals(mov.getCentroContable())) {
                    if (Math.truncate(cargos, 2) != Math.truncate(abonos, 2))
                        throw new AccountingEngineException(String.format("Diferencia en movimientos del centro contable %s (cargos=%,.2f, abonos=%,.2f)", movtoAnterior.getCentroContable(), Math.truncate(cargos, 2), Math.truncate(abonos, 2)));
                    // Actualiza totales de la Poliza
                    updatePoliza(psUpdatePoliza, movtoAnterior.getEjercicioFiscal(), movtoAnterior.getCentroContable(), values.get("cTipoPoliza"), folioPoliza, cargos, abonos);
                    cargos = abonos = 0;
                    movtoAnterior = mov;
                    if (nFolioPoliza == 0) {
                        folioPoliza = getNextFolioPoliza(conn, mov.getEjercicioFiscal(), mov.getCentroContable(), values.get("cTipoPoliza"));
                        // Adiciona Poliza al Batch
                        addPoliza(conn, psInsertPoliza, documentName, fieldName, folioPoliza, cargos, abonos, values, movtoAnterior, fechaCancelaDocto);
                    } else {
                        folioPoliza = nFolioPoliza;
                    }
                }
                mov.setFolioPoliza(folioPoliza);
                if (mov.isCargo())
                    cargosGrupoMovtos += mov.getMovimiento();
                else
                    abonosGrupoMovtos += mov.getMovimiento();
                // Adiciona Movimiento al Batch
                addMovement(psInsertMovto, fieldName, mov, values, fechaCancelaDocto);
                batchCount++;
                // Alcanzo el total de linea de valores deseado del batch?
                if ((batchCount % batchSize) == 0) {
                    batchCount = 0;
                    // Ejecuta Batch Polizas
                    validaTotalRegistrosAfectados(psInsertPoliza.executeBatch(), "InsertPoliza");
                    validaTotalRegistrosAfectados(psUpdatePoliza.executeBatch(), "UpdatePoliza");
                    // Ejecuta Batch Movimientos
                    validaTotalRegistrosAfectados(psInsertMovto.executeBatch(), "InsertMovto");
                    // Limpia batches
                    psInsertPoliza.clearBatch();
                    psUpdatePoliza.clearBatch();
                    psInsertMovto.clearBatch();
                }
                movtoAnterior = mov;
                if (log.isDebugEnabled())
                    log.debug("Object: {}", mov);
            }
            long endTime = System.currentTimeMillis();
            log.info("Object: {}", String.format("%,.0f movimientos de %s(%s) procesados en %s", (double) movements.size(), documentName, values.get(fieldName), timeFormatter.format(new Date(endTime - startTime))));
            if (batchCount > 0) {
                // Adiciona Saldo a lista
                if ((cargosGrupoMovtos != 0) || (abonosGrupoMovtos != 0))
                    balancesToUpdate.add(new BalancesToUpdate(movtoAnterior, cargosGrupoMovtos, abonosGrupoMovtos));
                cargos += cargosGrupoMovtos;
                abonos += abonosGrupoMovtos;
            }
            if (batchCount > 0) {
                if (Math.truncate(cargos, 2) != Math.truncate(abonos, 2))
                    throw new AccountingEngineException(String.format("Diferencia en movimientos del centro contable %s (cargos=%,.2f, abonos=%,.2f)", movtoAnterior.getCentroContable(), Math.truncate(cargos, 2), Math.truncate(abonos, 2)));
                // Actualiza totales de la Poliza
                updatePoliza(psUpdatePoliza, movtoAnterior.getEjercicioFiscal(), movtoAnterior.getCentroContable(), values.get("cTipoPoliza"), folioPoliza, cargos, abonos);
            }
            if (batchCount > 0) {
                // Ejecuta Batch Polizas
                validaTotalRegistrosAfectados(psInsertPoliza.executeBatch(), "InsertPoliza");
                validaTotalRegistrosAfectados(psUpdatePoliza.executeBatch(), "UpdatePoliza");
                // Ejecuta Batch Movimientos
                validaTotalRegistrosAfectados(psInsertMovto.executeBatch(), "InsertMovto");
            }
            // Actualiza Saldos
            batchCount = 0;
            startTime = System.currentTimeMillis();
            psInsertSaldo = conn.prepareStatement(sqlInsertSaldo);
            psUpdateSaldo = conn.prepareStatement(sqlUpdateSaldo);
            log.info("Object: {}", String.format("Procesando %,.0f saldos de %s(%s)...", (double) balancesToUpdate.size(), documentName, values.get(fieldName)));
            for (BalancesToUpdate balance : balancesToUpdate) {
                // Adiciona Saldo al Batch
                updateSaldo(conn, psInsertSaldo, psUpdateSaldo, balance.getCargos(), balance.getAbonos(), balance.getMovement(), values);
                batchCount++;
                // Alcanzo el total de linea de valores deseado del batch?
                if ((batchCount % batchSize) == 0) {
                    batchCount = 0;
                    // Ejecuta Batches Saldos
                    validaTotalRegistrosAfectados(psInsertSaldo.executeBatch(), "InsertSaldo");
                    validaTotalRegistrosAfectados(psUpdateSaldo.executeBatch(), "UpdateSaldo");
                    // Limpia batches
                    psInsertSaldo.clearBatch();
                    psUpdateSaldo.clearBatch();
                }
            }
            if (batchCount > 0) {
                // Ejecuta Batches Saldos
                validaTotalRegistrosAfectados(psInsertSaldo.executeBatch(), "InsertSaldo");
                validaTotalRegistrosAfectados(psUpdateSaldo.executeBatch(), "UpdateSaldo");
            }
            endTime = System.currentTimeMillis();
            log.info("Object: {}", String.format("%,.0f saldos de %s(%s) procesados en %s", (double) balancesToUpdate.size(), documentName, values.get(fieldName), timeFormatter.format(new Date(endTime - startTime))));
        } catch (Exception exc) {
            retVal = false;
            log.error(exc.getMessage(), exc);
            if (exc instanceof BatchUpdateException && exc.getMessage().contains("interbloqueo")) {
                log.error(exc.getMessage(), exc);
                throw new AccountingEngineException("Su documento no fue procesado. Favor de intentar de nuevo.");
            }
            if (exc instanceof AccountingEngineException)
                throw (AccountingEngineException) exc;
            else
                throw new AccountingEngineException(exc);
        } finally {
            try {
                if (psUpdateSaldo != null)
                    psUpdateSaldo.close();
            } catch (Exception exc) {
                log.warn("Cerrando PreparedStatement", exc);
            }
            try {
                if (psInsertSaldo != null)
                    psInsertSaldo.close();
            } catch (Exception exc) {
                log.warn("Cerrando PreparedStatement", exc);
            }
            try {
                if (psInsertMovto != null)
                    psInsertMovto.close();
            } catch (Exception exc) {
                log.warn("Cerrando PreparedStatement", exc);
            }
            try {
                if (psUpdatePoliza != null)
                    psUpdatePoliza.close();
            } catch (Exception exc) {
                log.warn("Cerrando PreparedStatement", exc);
            }
            try {
                if (psInsertPoliza != null)
                    psInsertPoliza.close();
            } catch (Exception exc) {
                log.warn("Cerrando PreparedStatement", exc);
            }
            psUpdateSaldo = null;
            psInsertSaldo = null;
            psInsertMovto = null;
            psUpdatePoliza = null;
            psInsertPoliza = null;
        }
        return new ProcessMovementsResult(folioPoliza, retVal);
    }

    private class BalancesToUpdate {

        private double cargos;

        private double abonos;

        private AccountingMovement mov;

        public BalancesToUpdate(AccountingMovement mov, double cargos, double abonos) {
            this.cargos = cargos;
            this.abonos = abonos;
            this.mov = mov;
        }

        public double getCargos() {
            return cargos;
        }

        public double getAbonos() {
            return abonos;
        }

        public AccountingMovement getMovement() {
            return mov;
        }
    }

    private void validaTotalRegistrosAfectados(int[] rows, String rowName) throws AccountingEngineException {
        for (int i = 0; i < rows.length; i++) if ((rows[i] == 0) || (rows[i] > 1))
            throw new AccountingEngineException(String.format("No se actualiz\u00f3 %s correctamente (%d : %d)", rowName, i, rows[i]));
    }

    private class ProcessMovementsResult {

        private int folioPoliza;

        private boolean success;

        public ProcessMovementsResult(int folioPoliza, boolean success) {
            this.folioPoliza = folioPoliza;
            this.success = success;
        }

        public int getFolioPoliza() {
            return folioPoliza;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    private synchronized int getNextFolioPoliza(Connection conn, String ejercicioFiscal, String centroContable, String tipoPoliza) throws AccountingEngineException {
        int retVal = 1;
        PreparedStatement psUpdate = null;
        PreparedStatement psInsert = null;
        PreparedStatement psSelect = null;
        ResultSet rs = null;
        String sqlUpdSeq = "UPDATE cf_sequence  WITH (ROWLOCK) SET seq_value = ? WHERE seq_name = ?";
        String sqlInsSeq = "INSERT INTO cf_sequence (seq_name, seq_value) SELECT 'SEQ_POLIZA_' + aEjercicioFiscal + '_' + cCentroContable + '_' + cTipoPoliza, " + "ISNULL(MAX(nFolioPoliza), 0) + 1 FROM tPoliza WITH (NOLOCK) WHERE aEjercicioFiscal = ? AND cCentroContable = ? AND cTipoPoliza = ? " + "GROUP BY aEjercicioFiscal, cCentroContable, cTipoPoliza";
        String sqlSelSeq = "SELECT seq_value + 1 FROM cf_sequence WITH( ROWLOCK, XLOCK ) WHERE seq_name = ?";
        try {
            log.debug("Object: {}", sqlSelSeq + " [SEQ_POLIZA_" + ejercicioFiscal + "_" + centroContable + "_" + tipoPoliza + "]");
            tipoPoliza = tipoPoliza.toUpperCase();
            psSelect = conn.prepareStatement(sqlSelSeq);
            psSelect.setString(1, "SEQ_POLIZA_" + ejercicioFiscal + "_" + centroContable + "_" + tipoPoliza);
            rs = psSelect.executeQuery();
            if (rs.next()) {
                retVal = rs.getInt(1);
                // Se actualiza primero para bloquear registro
                psUpdate = conn.prepareStatement(sqlUpdSeq);
                psUpdate.setInt(1, retVal);
                psUpdate.setString(2, "SEQ_POLIZA_" + ejercicioFiscal + "_" + centroContable + "_" + tipoPoliza);
                log.debug("Object: {}", sqlUpdSeq + " [" + retVal + ",SEQ_POLIZA_" + ejercicioFiscal + "_" + centroContable + "_" + tipoPoliza + "]");
                psUpdate.executeUpdate();
            } else {
                psInsert = conn.prepareStatement(sqlInsSeq);
                psInsert.setString(1, ejercicioFiscal);
                psInsert.setString(2, centroContable);
                psInsert.setString(3, tipoPoliza);
                log.debug("Object: {}", sqlInsSeq + " [" + ejercicioFiscal + ", " + centroContable + ", " + tipoPoliza + "]");
                psInsert.executeUpdate();
            }
        } catch (Exception exc) {
            if (exc instanceof AccountingEngineException)
                throw (AccountingEngineException) exc;
            else
                throw new AccountingEngineException(exc);
        } finally {
            try {
                if (psUpdate != null)
                    psUpdate.close();
            } catch (Exception exc) {
                log.warn("Cerrando PreparedStatement", exc);
            }
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception exc) {
                log.warn("Cerrando ResultSet", exc);
            }
            try {
                if (psInsert != null)
                    psInsert.close();
            } catch (Exception exc) {
                log.warn("Cerrando PreparedStatement", exc);
            }
            try {
                if (psSelect != null)
                    psSelect.close();
            } catch (Exception exc) {
                log.warn("Cerrando PreparedStatement", exc);
            }
            psUpdate = null;
            psInsert = null;
            rs = null;
            psSelect = null;
        }
        return retVal;
    }

    /*
	private int getNextFolioPoliza(String ejercicioFiscal, String centroContable, String tipoPoliza) throws AccountingEngineException {

		int retVal = 1;
		Connection conn = null;
		PreparedStatement psUpdate = null;
		PreparedStatement psInsert = null;
		PreparedStatement psSelect = null;
		ResultSet rs = null;

		String sqlUpdSeq = "UPDATE cf_sequence SET seq_value = seq_value + 1 WHERE seq_name = ?";
		String sqlInsSeq = "INSERT INTO cf_sequence (seq_name, seq_value) SELECT 'SEQ_POLIZA_' + aEjercicioFiscal + '_' + cCentroContable + '_' + cTipoPoliza, "
		        + "ISNULL(MAX(nFolioPoliza), 0) + 1 FROM tPoliza WITH (NOLOCK) WHERE aEjercicioFiscal = ? AND cCentroContable = ? AND cTipoPoliza = ? "
		        + "GROUP BY aEjercicioFiscal, cCentroContable, cTipoPoliza";
		String sqlSelSeq = "SELECT seq_value FROM cf_sequence WITH (NOLOCK) WHERE seq_name = ?";

		try {
			tipoPoliza = tipoPoliza.toUpperCase();

			synchronized (log) {
				conn = getConnection(); // Se obtiene una conexion distinta a la global

				// Se actualiza primero para bloquear registro
				psUpdate = conn.prepareStatement(sqlUpdSeq);
				psUpdate.setString(1, "SEQ_POLIZA_" + ejercicioFiscal + "_" + centroContable + "_" + tipoPoliza);

				log.debug(sqlUpdSeq + " [SEQ_POLIZA_" + ejercicioFiscal + "_" + centroContable + "_" + tipoPoliza + "]");

				if (psUpdate.executeUpdate() == 0) { // Si no existe (no actualizo nada) se crea el registro
					psInsert = conn.prepareStatement(sqlInsSeq);
					psInsert.setString(1, ejercicioFiscal);
					psInsert.setString(2, centroContable);
					psInsert.setString(3, tipoPoliza);

					log.debug(sqlInsSeq + " [" + ejercicioFiscal + ", " + centroContable + ", " + tipoPoliza + "]");

					psInsert.executeUpdate();
				}
				// Se recupera valor incrementado
				psSelect = conn.prepareStatement(sqlSelSeq);
				psSelect.setString(1, "SEQ_POLIZA_" + ejercicioFiscal + "_" + centroContable + "_" + tipoPoliza);

				log.debug(sqlSelSeq + " [SEQ_POLIZA_" + ejercicioFiscal + "_" + centroContable + "_" + tipoPoliza + "]");

				rs = psSelect.executeQuery();
				if (rs.next())
					retVal = rs.getInt(1);

				conn.commit(); // Se hacen permanentes los cambios
			}
		} catch (Exception exc) {
			try {
				if (conn != null)
					conn.rollback(); // Se deshacen los cambios
			} catch (Exception e) {
				log.warn("En rollback", e);
			}

			if (exc instanceof AccountingEngineException)
				throw (AccountingEngineException) exc;
			else
				throw new AccountingEngineException(exc);
		} finally {
			try {
				if (psUpdate != null)
					psUpdate.close();
			} catch (Exception exc) {
				log.warn("Cerrando PreparedStatement", exc);
			}

			try {
				if (rs != null)
					rs.close();
			} catch (Exception exc) {
				log.warn("Cerrando ResultSet", exc);
			}

			try {
				if (psInsert != null)
					psInsert.close();
			} catch (Exception exc) {
				log.warn("Cerrando PreparedStatement", exc);
			}

			try {
				if (psSelect != null)
					psSelect.close();
			} catch (Exception exc) {
				log.warn("Cerrando PreparedStatement", exc);
			}

			try {
				if (conn != null)
					conn.close();
			} catch (Exception exc) {
				log.warn("Cerrando Conexion a Base de datos", exc);
			}

			psUpdate = null;
			psInsert = null;
			rs = null;
			psSelect = null;
			conn = null;
		}

		return retVal;
	}

*/
    private void addPoliza(Connection conn, PreparedStatement psInsertPoliza, String documentName, String fieldName, int folioPoliza, double cargo, double abono, Map<String, String> values, AccountingMovement mov, String fechaCancelaDocto) throws Exception {
        Date fAplicacion;
        long now = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormatter = null;
        String fechaAplicacion = values.get("fAplicacion");
        if (fechaAplicacion == null)
            throw new AccountingEngineException("La fecha de aplicaci\u00f3n no debe ser nula");
        if (fechaAplicacion.trim().length() == 0)
            throw new AccountingEngineException("La fecha de aplicaci\u00f3n no debe estar vacia");
        try {
            if (fechaCancelaDocto == null) {
                if (values.get("fAplicacion").indexOf("-") > 0)
                    dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                else
                    dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
                fAplicacion = new Date(dateFormatter.parse(values.get("fAplicacion")).getTime());
            } else {
                if (fechaCancelaDocto.indexOf("-") > 0)
                    dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                else
                    dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
                fAplicacion = new Date(dateFormatter.parse(fechaCancelaDocto).getTime());
            }
        } catch (Exception exc) {
            log.warn(exc.getMessage(), exc);
            throw new AccountingEngineException("Formato de fecha de aplicaci\u00f3n inv\u00e1lido. Formato esperado 'yyyy-MM-dd'");
        }
        cal.clear();
        cal.setTimeInMillis(fAplicacion.getTime());
        // nFolioPoliza
        psInsertPoliza.setInt(1, folioPoliza);
        // fCreacion
        psInsertPoliza.setTimestamp(2, new Timestamp(now));
        // cDescripcionPoliza (TODO cDescripcionPoliza en todos los encabezados de documentos)
        psInsertPoliza.setString(3, values.get("cDescripcionPoliza"));
        // mTotalCargo
        psInsertPoliza.setDouble(4, Math.truncate(cargo, 2));
        // mTotalAbono
        psInsertPoliza.setDouble(5, Math.truncate(abono, 2));
        // nMes (Para Calendar el mes de ENERO empieza en 0) (TODO fAplicacion en todos los encabezados de documentos)
        psInsertPoliza.setInt(6, cal.get(Calendar.MONTH) + 1);
        //psInsertPoliza.setString(7, mov.getCuenta()); // nCuenta
        // nCuenta
        psInsertPoliza.setNull(7, Types.VARCHAR);
        // fAplicacion (fAplicacion en todos los encabezados de documentos)
        psInsertPoliza.setDate(8, fAplicacion);
        // nPolizaAutomatica (TODO cTipoPoliza en todos los encabezados de documentos)
        psInsertPoliza.setInt(9, getPolizaAutomatica(conn, documentName, values.get("cTipoPoliza")));
        // cCentroContable
        psInsertPoliza.setString(10, mov.getCentroContable());
        // aEjercicioFiscal
        psInsertPoliza.setString(11, mov.getEjercicioFiscal());
        // cTipoPoliza (TODO cTipoPoliza en todos los encabezados de documentos)
        psInsertPoliza.setString(12, values.get("cTipoPoliza"));
        // cTipoDocumento
        psInsertPoliza.setString(13, values.get("cTipoDocumento"));
        // nFolioDocumento
        psInsertPoliza.setString(14, values.get(fieldName));
        psInsertPoliza.addBatch();
        if (log.isDebugEnabled()) {
            StringBuffer msg = new StringBuffer(sqlInsertPoliza);
            msg.append("\n[").append(folioPoliza).append(", ").append(new Timestamp(now)).append(", ").append(values.get("cDescripcionPoliza")).append(", ").append(Math.truncate(cargo, 2)).append(", ").append(Math.truncate(abono, 2)).append(", ").append(cal.get(Calendar.MONTH) + 1).append(", ").append("null").append(", ").append(fAplicacion).append(", ").append(getPolizaAutomatica(conn, documentName, values.get("cTipoPoliza"))).append(", ").append(mov.getCentroContable()).append(", ").append(mov.getEjercicioFiscal()).append(", ").append(values.get("cTipoPoliza")).append(", ").append(values.get("cTipoDocumento")).append(", ").append(values.get(fieldName)).append("]");
            log.debug("Object: {}", msg);
        }
    }

    private void updatePoliza(PreparedStatement psUpdatePoliza, String ejercicioFiscal, String centroContable, String tipoPoliza, int folioPoliza, double cargos, double abonos) throws Exception {
        psUpdatePoliza.setDouble(1, Math.truncate(cargos, 2));
        psUpdatePoliza.setDouble(2, Math.truncate(abonos, 2));
        psUpdatePoliza.setString(3, ejercicioFiscal);
        psUpdatePoliza.setString(4, centroContable);
        psUpdatePoliza.setString(5, tipoPoliza);
        psUpdatePoliza.setInt(6, folioPoliza);
        psUpdatePoliza.addBatch();
        if (log.isDebugEnabled()) {
            StringBuffer msg = new StringBuffer(sqlUpdatePoliza);
            msg.append("\n[").append(Math.truncate(cargos, 2)).append(", ").append(Math.truncate(abonos, 2)).append(", ").append(ejercicioFiscal).append(", ").append(centroContable).append(", ").append(tipoPoliza).append(", ").append(folioPoliza).append("]");
            log.debug("Object: {}", msg);
        }
    }

    private void updateSaldo(Connection conn, PreparedStatement psInsertSaldo, PreparedStatement psUpdateSaldo, double cargos, double abonos, AccountingMovement mov, Map<String, String> values) throws Exception {
        PreparedStatement psExisteSaldo = null;
        ResultSet rs = null;
        try {
            psExisteSaldo = conn.prepareStatement(sqlExisteSaldo);
            psExisteSaldo.setString(1, values.get("cRamo"));
            // cUnidadResponsableContable
            psExisteSaldo.setString(2, values.get("cUnidadResponsableContable"));
            psExisteSaldo.setString(3, mov.getEjercicioFiscal());
            // +++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // NOTA: SOLO PARA CONAGUA
            // Las cuentas que empiezan con 8 su centro contable es 0
            // +++++++++++++++++++++++++++++++++++++++++++++++++++++++
            psExisteSaldo.setString(4, mov.getbPresupuesto() ? "0" : mov.getCentroContable());
            psExisteSaldo.setString(5, mov.getCuenta());
            psExisteSaldo.setString(6, mov.getSubCuenta());
            Calendar cal = Calendar.getInstance();
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            Date fAplicacion = new Date(dateFormatter.parse(values.get("fAplicacion")).getTime());
            cal.clear();
            cal.setTimeInMillis(fAplicacion.getTime());
            int index = 1;
            int mesAplicacion = "S".equals(values.get("periodo13")) ? 12 : cal.get(Calendar.MONTH);
            double diferencia = mov.esDeudora() ? (Math.truncate(cargos, 2) - Math.truncate(abonos, 2)) : (Math.truncate(abonos, 2) - Math.truncate(cargos, 2));
            diferencia = Math.truncate(diferencia, 2);
            log.debug("Object: {}", sqlExisteSaldo + " [" + values.get("cRamo") + ", " + values.get("cUnidadResponsableContable") + ", " + mov.getEjercicioFiscal() + ", " + (mov.getbPresupuesto() ? "0" : mov.getCentroContable()) + ", " + mov.getCuenta() + ", " + mov.getSubCuenta() + "]");
            rs = psExisteSaldo.executeQuery();
            if (rs.next()) {
                double[] deber = new double[] { rs.getDouble("mDeber1"), rs.getDouble("mDeber2"), rs.getDouble("mDeber3"), rs.getDouble("mDeber4"), rs.getDouble("mDeber5"), rs.getDouble("mDeber6"), rs.getDouble("mDeber7"), rs.getDouble("mDeber8"), rs.getDouble("mDeber9"), rs.getDouble("mDeber10"), rs.getDouble("mDeber11"), rs.getDouble("mDeber12"), rs.getDouble("mDeber13") };
                double[] haber = new double[] { rs.getDouble("mHaber1"), rs.getDouble("mHaber2"), rs.getDouble("mHaber3"), rs.getDouble("mHaber4"), rs.getDouble("mHaber5"), rs.getDouble("mHaber6"), rs.getDouble("mHaber7"), rs.getDouble("mHaber8"), rs.getDouble("mHaber9"), rs.getDouble("mHaber10"), rs.getDouble("mHaber11"), rs.getDouble("mHaber12"), rs.getDouble("mHaber13") };
                double[] saldo = new double[] { rs.getDouble("mSaldo1"), rs.getDouble("mSaldo2"), rs.getDouble("mSaldo3"), rs.getDouble("mSaldo4"), rs.getDouble("mSaldo5"), rs.getDouble("mSaldo6"), rs.getDouble("mSaldo7"), rs.getDouble("mSaldo8"), rs.getDouble("mSaldo9"), rs.getDouble("mSaldo10"), rs.getDouble("mSaldo11"), rs.getDouble("mSaldo12"), rs.getDouble("mSaldo13") };
                int mesArrastre = rs.getInt("nMesArrastre");
                double saldoArrastre = rs.getDouble("mSaldoArrastre");
                int mesPrimerMovimiento = rs.getInt("nMesPrimerMovimiento");
                for (int i = 0; i < saldo.length; i++) {
                    saldo[i] = Math.truncate(saldo[i], 2);
                    haber[i] = Math.truncate(haber[i], 2);
                    deber[i] = Math.truncate(deber[i], 2);
                }
                // Se suman, al mes de aplicacion, cargos al Debe
                deber[mesAplicacion] += cargos;
                // Se suman, al mes de aplicacion, abonos al Haber
                haber[mesAplicacion] += abonos;
                if (mesArrastre >= (mesAplicacion + 1)) {
                    // Actualiza los Saldos del mes de aplicacion al mes de arrastre
                    for (int i = mesAplicacion; i < mesArrastre; i++) {
                        // Se actualiza Saldo de meses posteriores al mes de aplicacion
                        saldo[i] += diferencia;
                        // Comprueba suficiencia de saldo
                        if (validaInsuficienciaDeSaldo() && mov.isVerificaSaldo() && (diferencia < 0d) && (saldo[i] < 0d)) {
                            if (!mov.getbPresupuesto() || (mov.getbPresupuesto() && (i + 1) == mesArrastre)) {
                                throw new AccountingEngineException(String.format("Saldo insuficiente en %s EP %s\nSaldo(%,.2f) + Movimiento(%,.2f) = Saldo Insuficiente(%,.2f)\nCC(%s)\nCta(%s)\nSCta(%s)\nM(%s)", mov.getDescCuenta(), mov.getSubCuenta(), saldo[i] - diferencia, diferencia, saldo[i], mov.getCentroContable(), mov.getCuenta(), mov.getSubCuenta(), i + 1));
                            }
                        }
                    }
                } else {
                    // Se actualiza Saldo de meses anteriores al mes de aplicacion con el saldoArrastre
                    for (int i = mesArrastre; i <= mesAplicacion; i++) saldo[i] = saldoArrastre;
                    // Se actualiza Saldo del mes de aplicacion
                    saldo[mesAplicacion] += diferencia;
                    // Comprueba suficiencia de saldo
                    if (validaInsuficienciaDeSaldo() && mov.isVerificaSaldo() && (diferencia < 0d) && (saldo[mesAplicacion] < 0d)) {
                        throw new AccountingEngineException(String.format("Saldo insuficiente en %s EP %s\nSaldo(%,.2f) + Movimiento(%,.2f) = Saldo Insuficiente(%,.2f)\nCC(%s)\nCta(%s)\nSCta(%s)\nM(%s)", mov.getDescCuenta(), mov.getSubCuenta(), saldo[mesAplicacion] - diferencia, diferencia, saldo[mesAplicacion], mov.getCentroContable(), mov.getCuenta(), mov.getSubCuenta(), mesAplicacion + 1));
                    }
                    // Se actualiza mes de arrastre con el mes de aplicacion
                    mesArrastre = mesAplicacion + 1;
                }
                // Se actualiza saldoArrastre
                saldoArrastre += diferencia;
                // Se actualiza mes del primer movimiento
                if (mesPrimerMovimiento > (mesAplicacion + 1))
                    mesPrimerMovimiento = mesAplicacion + 1;
                // nMesArrastre
                psUpdateSaldo.setInt(index++, mesArrastre);
                // mSaldoArrastre (diferencia dependiendo de naturaleza)
                psUpdateSaldo.setDouble(index++, Math.truncate(saldoArrastre, 2));
                for (int i = 0; i < 13; i++) {
                    // mSaldo(1..13)
                    psUpdateSaldo.setDouble(index++, Math.truncate(saldo[i], 2));
                    // mHaber(1..13)
                    psUpdateSaldo.setDouble(index++, Math.truncate(haber[i], 2));
                    // mDeber(1..13)
                    psUpdateSaldo.setDouble(index++, Math.truncate(deber[i], 2));
                }
                psUpdateSaldo.setInt(index++, mesPrimerMovimiento);
                // cRamo
                psUpdateSaldo.setString(index++, values.get("cRamo"));
                // cUnidadResponsableContable
                psUpdateSaldo.setString(index++, values.get("cUnidadResponsableContable"));
                // aEjercicioFiscal
                psUpdateSaldo.setString(index++, mov.getEjercicioFiscal());
                // cCentroContable
                psUpdateSaldo.setString(index++, mov.getbPresupuesto() ? "0" : mov.getCentroContable());
                // nCuenta
                psUpdateSaldo.setString(index++, mov.getCuenta());
                // cSubCuenta
                psUpdateSaldo.setString(index++, mov.getSubCuenta());
                psUpdateSaldo.addBatch();
                if (log.isDebugEnabled()) {
                    StringBuffer msg = new StringBuffer(sqlUpdateSaldo);
                    msg.append("\n[").append(mesArrastre).append(", ").append(Math.truncate(saldoArrastre, 2));
                    for (int i = 0; i < 13; i++) {
                        msg.append(", ").append(Math.truncate(saldo[i], 2)).append(", ").append(Math.truncate(haber[i], 2)).append(", ").append(Math.truncate(deber[i], 2));
                    }
                    msg.append(", ").append(mesPrimerMovimiento).append(", ").append(values.get("cRamo")).append(", ").append(values.get("cUnidadResponsableContable")).append(", ").append(mov.getEjercicioFiscal()).append(", ").append(mov.getbPresupuesto() ? "0" : mov.getCentroContable()).append(", ").append(mov.getCuenta()).append(", ").append(mov.getSubCuenta()).append("]");
                    log.debug("Object: {}", msg);
                }
            } else {
                if (validaInsuficienciaDeSaldo() && mov.isVerificaSaldo() && (diferencia < 0d))
                    throw new AccountingEngineException(String.format("No se puede reducir el importe %,.2f en clave inexistente %s (C.C. %s, Cta. %s [%s])", diferencia, mov.getSubCuenta(), mov.getbPresupuesto() ? "0" : mov.getCentroContable(), mov.getDescCuenta(), mov.getCuenta()));
                // aEjercicioFiscal
                psInsertSaldo.setString(index++, mov.getEjercicioFiscal());
                // ++++++++++++++++++++++++
                // NOTA: SOLO PARA CONAGUA
                // Las cuentas que empiezan con 8 su centro contable es 0
                // ++++++++++++++++++++++++
                // cCentroContable
                psInsertSaldo.setString(index++, mov.getbPresupuesto() ? "0" : mov.getCentroContable());
                // cRamo
                psInsertSaldo.setString(index++, values.get("cRamo"));
                // cUnidadResponsableContable
                psInsertSaldo.setString(index++, values.get("cUnidadResponsableContable"));
                // nCuenta
                psInsertSaldo.setString(index++, mov.getCuenta());
                // cSubCuenta
                psInsertSaldo.setString(index++, mov.getSubCuenta());
                // cMoneda (TODO cMoneda y nTipoCambio en todos los encabezados de documentos)
                psInsertSaldo.setString(index++, null);
                // nMesPrimerMovimiento (El mes de fAplicacion)
                psInsertSaldo.setInt(index++, mesAplicacion + 1);
                // nMesArrastre (El mes de fAplicacion)
                psInsertSaldo.setInt(index++, mesAplicacion + 1);
                // mSaldoArrastre (diferencia dependiendo de naturaleza)
                psInsertSaldo.setDouble(index++, Math.truncate(diferencia, 2));
                // mSaldo0
                psInsertSaldo.setDouble(index++, 0);
                for (int i = 0; i < 13; i++) {
                    // mSaldo(1..13)
                    psInsertSaldo.setDouble(index++, (i == mesAplicacion) ? Math.truncate(diferencia, 2) : 0);
                    // mHaber(1..13)
                    psInsertSaldo.setDouble(index++, (i == mesAplicacion) ? Math.truncate(abonos, 2) : 0);
                    // mDeber(1..13)
                    psInsertSaldo.setDouble(index++, (i == mesAplicacion) ? Math.truncate(cargos, 2) : 0);
                }
                psInsertSaldo.addBatch();
                if (log.isDebugEnabled()) {
                    StringBuffer msg = new StringBuffer(sqlInsertSaldo);
                    msg.append("\n[").append(mov.getEjercicioFiscal()).append(", ").append(mov.getbPresupuesto() ? "0" : mov.getCentroContable()).append(", ").append(values.get("cRamo")).append(", ").append(values.get("cUnidadResponsableContable")).append(", ").append(mov.getCuenta()).append(", ").append(mov.getSubCuenta()).append(", ").append("null").append(", ").append(mesAplicacion + 1).append(", ").append(mesAplicacion + 1).append(", ").append(Math.truncate(diferencia, 2)).append(", ").append(0).append(", ");
                    for (int i = 0; i < 13; i++) {
                        msg.append((i == mesAplicacion) ? Math.truncate(diferencia, 2) : 0).append(", ").append((i == mesAplicacion) ? Math.truncate(abonos, 2) : 0).append(", ").append((i == mesAplicacion) ? Math.truncate(cargos, 2) : 0);
                    }
                    msg.append("]");
                    log.debug("Object: {}", msg);
                }
            }
        } finally {
            if (psExisteSaldo != null)
                psExisteSaldo.close();
            psExisteSaldo = null;
        }
    }

    private void addMovement(PreparedStatement psInsertMovto, String fieldName, AccountingMovement mov, Map<String, String> values) throws Exception {
        addMovement(psInsertMovto, fieldName, mov, values, fechaCancelaDocto);
    }

    private void addMovement(PreparedStatement psInsertMovto, String fieldName, AccountingMovement mov, Map<String, String> values, String fechaCancelaDocto) throws Exception {
        // Si el movimiento esta en cero no se inserta
        if (Math.truncate(mov.getMovimiento(), 2) == 0)
            return;
        long now = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormatter = null;
        Timestamp fMovimiento = null;
        if (fechaCancelaDocto == null) {
            if (values.get("fAplicacion").indexOf("-") > 0)
                dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            else
                dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
            fMovimiento = new Timestamp(dateFormatter.parse(values.get("fAplicacion")).getTime());
        } else {
            if (fechaCancelaDocto.indexOf("-") > 0)
                dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            else
                dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
            fMovimiento = new Timestamp(dateFormatter.parse(fechaCancelaDocto).getTime());
        }
        cal.clear();
        cal.setTimeInMillis(fMovimiento.getTime());
        // aEjercicioFiscal
        psInsertMovto.setString(1, mov.getEjercicioFiscal());
        // psInsertMovto.setString(2, mov.getCentroContable()); // cCentroContable
        // cCentroContable
        psInsertMovto.setString(2, mov.getCentroContable());
        // nCuenta
        psInsertMovto.setString(3, mov.getCuenta());
        // nSubCuenta
        psInsertMovto.setString(4, mov.getSubCuenta());
        // nFolioPoliza
        psInsertMovto.setInt(5, mov.getFolioPoliza());
        // cRamo
        psInsertMovto.setString(6, values.get("cRamo"));
        // cUnidadResponsableContable
        psInsertMovto.setString(7, values.get("cUnidadResponsableContable"));
        // nDocRenglon
        psInsertMovto.setInt(8, mov.getDocRenglon());
        //psInsertMovto.setInt(9, -1); // nConsecutivoMovimiento (Es auto-incrementable tipo identity)
        // mMovimiento
        psInsertMovto.setDouble(9, Math.truncate(mov.getMovimiento(), 2));
        // cTipoMovimiento
        psInsertMovto.setString(10, mov.getTipoMovimiento());
        // cDescripcionMovPol
        psInsertMovto.setString(11, values.get("cDescripcionMovPol"));
        // cTipoDocumento
        psInsertMovto.setString(12, values.get("cTipoDocumento"));
        // fOperacionMovimiento
        psInsertMovto.setTimestamp(13, new Timestamp(now));
        // cFolioDocumentoMovimiento
        psInsertMovto.setString(14, values.get("cFolioDocumentoMovimiento"));
        //psInsertMovto.setString(15, null); // cCancelaMovimiento
        // cCancelaMovimiento (valor inicial NULL)
        psInsertMovto.setNull(15, Types.CHAR);
        // fMovimiento
        psInsertMovto.setTimestamp(16, fMovimiento);
        // dConceptoMovimiento
        psInsertMovto.setString(17, values.get("dConceptoMovimiento"));
        // cMoneda
        psInsertMovto.setString(18, values.get("cMoneda"));
        // cTipoPoliza
        psInsertMovto.setString(19, values.get("cTipoPoliza"));
        // Periodo13
        psInsertMovto.setString(20, values.get("Periodo13") == null ? "N" : values.get("Periodo13"));
        // ADEFAS
        psInsertMovto.setString(21, values.get("ADEFAS") == null ? "N" : values.get("ADEFAS"));
        // nTipoAjuste
        psInsertMovto.setString(22, values.get("nTipoAjuste") == null ? "0" : values.get("nTipoAjuste"));
        // Parcial
        psInsertMovto.setString(23, mov.getParcial() == null ? "N" : mov.getParcial());
        psInsertMovto.addBatch();
        if (log.isDebugEnabled()) {
            StringBuffer msg = new StringBuffer(sqlInsertMovto);
            msg.append("\n[").append(mov.getEjercicioFiscal()).append(", ").append(mov.getCentroContable()).append(", ").append(mov.getCuenta()).append(", ").append(mov.getSubCuenta()).append(", ").append(mov.getFolioPoliza()).append(", ").append(values.get("cRamo")).append(", ").append(values.get("cUnidadResponsableContable")).append(", ").append(mov.getDocRenglon()).append(", ").append(Math.truncate(mov.getMovimiento(), 2)).append(", ").append(mov.getTipoMovimiento()).append(", ").append(values.get("cDescripcionMovPol")).append(", ").append(values.get("cTipoDocumento")).append(", ").append(fMovimiento).append(", ").append("(").append(fieldName).append(") = ").append(values.get(fieldName)).append(", ").append("null").append(", ").append(new Timestamp(now)).append(", ").append(values.get("dConceptoMovimiento")).append(", ").append(values.get("cMoneda")).append(", ").append(values.get("cTipoPoliza")).append(", ").append(values.get("Periodo13")).append(", ").append(values.get("ADEFAS")).append(", ").append(values.get("nTipoAjuste")).append(", ").append(mov.getParcial() == null ? "N" : mov.getParcial()).append("]");
            log.debug("Object: {}", msg);
        }
    }

    private int getPolizaAutomatica(Connection conn, String documentName, String tipoPoliza) throws Exception {
        int retVal = 1;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT dPolizaAutomatica FROM tDocumentoTipoPoliza WITH (NOLOCK) WHERE cDocumento = ? AND cTipoPoliza = ?");
            pstmnt.setString(1, documentName);
            pstmnt.setString(2, tipoPoliza);
            rs = pstmnt.executeQuery();
            if (!rs.next())
                throw new AccountingEngineException("No se localiz\u00f3 documento '" + documentName + "' con tipo poliza '" + tipoPoliza + "'");
            retVal = rs.getInt(1);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception exc) {
                log.warn("Cerrando ResultSet", exc);
            }
            try {
                if (pstmnt != null)
                    pstmnt.close();
            } catch (Exception exc) {
                log.warn("Cerrando PreparedStatement", exc);
            }
            rs = null;
            pstmnt = null;
        }
        return retVal;
    }
}
