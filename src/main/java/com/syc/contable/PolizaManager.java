package com.syc.contable;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioPropiedades;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ObraPublicaContractBusinessLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PolizaManager {

    public static final Logger log = LoggerFactory.getLogger(PolizaManager.class);

    public static int borraDetallePolizaAplicada(Connection con, String nFolioPoliza, String cCentroContable, String cTipoPoliza, String aEjercicioFiscal) throws PolizaException {
        String qry = " DELETE " + " FROM	tMovimiento " + " WHERE	nFolioPoliza = ? " + " AND	cCentroContable = ? " + " AND cTipoPoliza = ? and aEjercicioFiscal = ?";
        PreparedStatement pstmnt = null;
        int r = 0;
        try {
            pstmnt = con.prepareStatement(qry);
            pstmnt.setString(1, nFolioPoliza);
            pstmnt.setString(2, cCentroContable);
            pstmnt.setString(3, cTipoPoliza);
            pstmnt.setString(4, aEjercicioFiscal);
            r = pstmnt.executeUpdate();
            return r;
        } catch (Exception e) {
            throw new PolizaException(e);
        } finally {
            if (pstmnt != null)
                try {
                    pstmnt.close();
                } catch (Exception e) {
                    log.warn("Error cerrando PreparedStatement", e);
                }
            pstmnt = null;
        }
    }

    public static int borraEncabezadoPolizaAplicada(Connection con, String nFolioPoliza, String cCentroContable, String cTipoPoliza, String aEjercicioFiscal) throws PolizaException {
        String qry = " DELETE " + " FROM	tPoliza " + " WHERE	nFolioPoliza = ? " + " AND	cCentroContable = ? " + " AND cTipoPoliza = ? " + " AND	aEjercicioFiscal = ?";
        PreparedStatement pstmnt = null;
        int r = 0;
        try {
            pstmnt = con.prepareStatement(qry);
            pstmnt.setString(1, nFolioPoliza);
            pstmnt.setString(2, cCentroContable);
            pstmnt.setString(3, cTipoPoliza);
            pstmnt.setString(4, aEjercicioFiscal);
            r = pstmnt.executeUpdate();
            return r;
        } catch (Exception e) {
            throw new PolizaException(e);
        } finally {
            if (pstmnt != null)
                try {
                    pstmnt.close();
                } catch (Exception e2) {
                    log.warn("Error cerrando PreparedStatment", e2);
                }
            pstmnt = null;
        }
    }

    public static int borraPolizaAplicada(Connection con, String nFolioPoliza, String cCentroContable, String cTipoPoliza, String aEjercicioFiscal) throws PolizaException {
        int r = 0;
        r = PolizaManager.borraDetallePolizaAplicada(con, nFolioPoliza, cCentroContable, cTipoPoliza, aEjercicioFiscal);
        r += PolizaManager.borraEncabezadoPolizaAplicada(con, nFolioPoliza, cCentroContable, cTipoPoliza, aEjercicioFiscal);
        return r;
    }

    public static List<Movimiento> cargaDetallePolizaAplicada(Connection con, String nFolioPoliza, String cCentroContable, String cTipoPoliza, String aEjercicioFiscal) throws PolizaException {
        List<Movimiento> detalle = new ArrayList<Movimiento>();
        PreparedStatement pStmnt = null;
        ResultSet rs = null;
        String queryEnc = " SELECT	* " + " FROM	tMovimiento WITH(NOLOCK) " + " WHERE	nFolioPoliza = ? " + " AND	cCentroContable = ? " + " AND cTipoPoliza = ? " + " AND	aEjercicioFiscal = ?";
        try {
            pStmnt = con.prepareStatement(queryEnc);
            pStmnt.setString(1, nFolioPoliza);
            pStmnt.setString(2, cCentroContable);
            pStmnt.setString(3, cTipoPoliza);
            pStmnt.setString(4, aEjercicioFiscal);
            rs = pStmnt.executeQuery();
            while (rs.next()) {
                Movimiento renglonDetalle = new Movimiento();
                renglonDetalle.setAejerciciofiscal(rs.getString("aEjercicioFiscal"));
                renglonDetalle.setCcentrocontable(rs.getString("cCentroContable"));
                renglonDetalle.setCtipodocumento(rs.getString("cTipoDocumento"));
                renglonDetalle.setCtipopoliza(rs.getString("cTipoPoliza"));
                renglonDetalle.setNcuenta(rs.getString("nCuenta"));
                renglonDetalle.setNdocrenglon(rs.getInt("nDocRenglon"));
                renglonDetalle.setNfoliopoliza(rs.getLong("nFolioPoliza"));
                renglonDetalle.setNsubcuenta(rs.getString("nSubCuenta"));
                renglonDetalle.setMmovimiento(rs.getDouble("mMovimiento"));
                renglonDetalle.setCdescripcionmovpol(rs.getString("cDescripcionMovPol"));
                renglonDetalle.setnConsecutivoMovimiento(rs.getLong("nConsecutivoMovimiento"));
                renglonDetalle.setCramo(rs.getString("cRamo"));
                renglonDetalle.setFoperacionmovimiento(rs.getDate("fOperacionMovimiento"));
                renglonDetalle.setCfoliodocumentomovimiento(rs.getLong("cFolioDocumentoMovimiento"));
                renglonDetalle.setCcancelamovimiento(rs.getString("cCancelaMovimiento"));
                renglonDetalle.setFmovimiento(rs.getDate("fMovimiento"));
                renglonDetalle.setDconceptomovimiento(rs.getString("dConceptoMovimiento"));
                renglonDetalle.setCmoneda(rs.getString("cMoneda"));
                renglonDetalle.setCunidadresponsable(rs.getString("cUnidadResponsable"));
                renglonDetalle.setnTipoAjuste(rs.getInt("nTipoAjuste"));
                renglonDetalle.setADEFAS(rs.getString("ADEFAS"));
                renglonDetalle.setPeriodo13(rs.getString("Periodo13"));
                renglonDetalle.setParcial(rs.getString("parcial"));
                detalle.add(renglonDetalle);
            }
            return detalle;
        } catch (Exception e) {
            throw new PolizaException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    log.warn("Error cerrando ResultSet", e);
                } finally {
                    rs = null;
                }
            }
            if (pStmnt != null) {
                try {
                    pStmnt.close();
                } catch (Exception e) {
                    log.warn("Error cerrando PreparedStatement", e);
                } finally {
                    pStmnt = null;
                }
            }
        }
    }

    public static DocPoliza cargaDocPoliza(Connection con, long nFolioDocPoliza, String cCentroContable, String cTipoPoliza, String aEjercicioFiscal) throws PolizaException {
        List<DocPolizaDetalle> detalle = cargaDocPolizaDetalle(con, nFolioDocPoliza, cCentroContable, cTipoPoliza, aEjercicioFiscal);
        DocPolizaEncabezado encabezado = cargaDocPolizaEncabezado(con, nFolioDocPoliza, cTipoPoliza, cCentroContable, aEjercicioFiscal);
        DocPoliza poliza = new DocPoliza(encabezado, detalle);
        return poliza;
    }

    public static List<DocPolizaDetalle> cargaDocPolizaDetalle(Connection con, long nFolioDocPoliza, String cCentroContable, String cTipoPoliza, String aEjercicioFiscal) throws PolizaException {
        List<DocPolizaDetalle> detalle = new ArrayList<DocPolizaDetalle>(0);
        String qry = "SELECT	nFolioDocPoliza, nDocRenglon, nCuenta, nSubCuenta, cEvento, " + "        mImporte, cCentroContable, cTipoPoliza, aEjercicioFiscal, cConcepto " + "       ,nTipoAjuste " + "       ,Periodo13 " + "       ,ADEFAS " + "       ,parcial " + "  FROM	tDocPolizaDetalle WITH(NOLOCK) " + " WHERE	nFolioDocPoliza = ? " + "   AND	cCentroContable = ? " + "   AND	cTipoPoliza = ?     " + "   AND	aEjercicioFiscal = ? ";
        PreparedStatement pStmnt = null;
        ResultSet rs = null;
        try {
            pStmnt = con.prepareStatement(qry);
            pStmnt.setLong(1, nFolioDocPoliza);
            pStmnt.setString(2, cCentroContable);
            pStmnt.setString(3, cTipoPoliza);
            pStmnt.setString(4, aEjercicioFiscal);
            rs = pStmnt.executeQuery();
            while (rs.next()) {
                DocPolizaDetalle detAux = new DocPolizaDetalle();
                detAux.setnFolioDocPoliza(rs.getLong("nFolioDocPoliza"));
                detAux.setnDocRenglon(rs.getInt("nDocRenglon"));
                detAux.setnCuenta(rs.getString("nCuenta"));
                detAux.setnSubCuenta(rs.getString("nSubCuenta"));
                detAux.setcEvento(rs.getString("cEvento"));
                detAux.setmImporte(rs.getDouble("mImporte"));
                detAux.setcCentroContable(rs.getString("cCentroContable"));
                detAux.setcTipoPoliza(rs.getString("cTipoPoliza"));
                detAux.setaEjercicioFiscal(rs.getString("aEjercicioFiscal"));
                detAux.setcConcepto(rs.getString("cConcepto"));
                detAux.setnTipoAjuste(rs.getInt("nTipoAjuste"));
                detAux.setPeriodo13(rs.getString("Periodo13"));
                detAux.setADEFAS(rs.getString("ADEFAS"));
                // detAux.setReferencia(rs.getString("cReferencia"));
                detAux.setParcial(rs.getString("parcial"));
                detalle.add(detAux);
            }
            return detalle;
        } catch (Exception e) {
            throw new PolizaException(e);
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e2) {
                    log.warn("Error cerrando ResultSet", e2);
                }
            if (pStmnt != null)
                try {
                    pStmnt.close();
                } catch (Exception e2) {
                    log.warn("Error cerrando PreparedStatement", e2);
                }
            rs = null;
            pStmnt = null;
        }
    }

    public static DocPolizaEncabezado cargaDocPolizaEncabezado(Connection con, long nFolioDocPoliza, String cTipoPoliza, String cCentroContable, String aEjercicioFiscal) throws PolizaException {
        String qry = " SELECT " + "	 aEjercicioFiscal, " + " 	 cCentroContable, " + " 	 cComentarios, " + " 	 cConcepto, " + " 	 cDescripcionPoliza, " + " 	 cDocumentoHaplicado, " + " 	 cidOrigen, " + " 	 cIdUsuarioAprobacion, " + " 	 cIdUsuarioCaptura, " + " 	 cIdUsuarioRevision, " + " 	 cRamo, " + " 	 cRevisado, " + " 	 cTipoDocumento, " + " 	 cTipoPoliza, " + " 	 cUnidadResponsable, " + " 	 cUnidadResponsableContable, " + " 	 fAplicacion, " + " 	 fCancelacion, " + " 	 fCarga, " + " 	 mTotalAbonos, " + " 	 mTotalCargos, " + " 	 nCambio, " + " 	 nFolioDocPoliza, " + " 	 nFolioPoliza, " + " 	 nFolioPolizaCancelacion, " + " 	 nIdCasoOrigen, " + " 	 nMes  " + "       ,nTipoAjuste " + "       ,Periodo13 " + "       ,ADEFAS " + // + ", cReferenciaPoliza "
        " FROM	tDocPolizaEncabezado with(nolock) " + " WHERE	nFolioDocPoliza = ? " + "    AND	cTipoPoliza = ? " + " AND	cCentroContable = ? " + " AND	aEjercicioFiscal = ? ";
        PreparedStatement pStmnt = null;
        ResultSet rs = null;
        DocPolizaEncabezado encabezado = null;
        try {
            pStmnt = con.prepareStatement(qry);
            pStmnt.setLong(1, nFolioDocPoliza);
            pStmnt.setString(2, cTipoPoliza);
            pStmnt.setString(3, cCentroContable);
            pStmnt.setString(4, aEjercicioFiscal);
            rs = pStmnt.executeQuery();
            List<DocPolizaEncabezado> l = extraeEncabezado(rs);
            if (l.size() > 0)
                encabezado = l.get(0);
            return encabezado;
        } catch (Exception e) {
            throw new PolizaException(e);
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e) {
                    log.warn("Error cerrando ResultSet", e);
                }
            if (pStmnt != null)
                try {
                    pStmnt.close();
                } catch (Exception e2) {
                    log.warn("Error cerrando PreparedStatement", e2);
                }
            rs = null;
            pStmnt = null;
        }
    }

    public static EncabezadoPoliza cargaEncabezadoPolizaAplicada(Connection con, String nFolioPoliza, String cCentroContable, String cTipoPoliza) throws PolizaException {
        PreparedStatement pStmnt = null;
        ResultSet rs = null;
        String queryEnc = " SELECT	* " + " FROM	tPoliza with (nolock) " + " WHERE	nFolioPoliza = ? " + " AND	cCentroContable = ? " + " AND cTipoPoliza = ?";
        EncabezadoPoliza encPoliza = null;
        try {
            pStmnt = con.prepareStatement(queryEnc);
            pStmnt.setString(1, nFolioPoliza);
            pStmnt.setString(2, cCentroContable);
            pStmnt.setString(3, cTipoPoliza);
            rs = pStmnt.executeQuery();
            if (rs.next()) {
                encPoliza = new EncabezadoPoliza();
                encPoliza.setaEjercicioFiscal(rs.getString("aEjercicioFiscal"));
                encPoliza.setcCentroContable(rs.getString("cCentroContable"));
                encPoliza.setcDescripcionPoliza(rs.getString("cDescripcionPoliza"));
                encPoliza.setcTipoDocumento(rs.getString("cTipoDocumento"));
                encPoliza.setcTipoPoliza(rs.getString("cTipoPoliza"));
                encPoliza.setcUsuarioAutorizo(rs.getString("cUsuarioAutorizo"));
                encPoliza.setDocHAplicado(rs.getString("docHAplicado"));
                encPoliza.setfAplicacion(rs.getDate("fAplicacion"));
                encPoliza.setfCreacion(rs.getString("fCreacion"));
                encPoliza.setmTotalAbono(rs.getString("mTotalAbono"));
                encPoliza.setmTotalCargo(rs.getString("mTotalCargo"));
                encPoliza.setnCuenta(rs.getString("nCuenta"));
                encPoliza.setnFolioDocumento(rs.getLong("nFolioDocumento"));
                encPoliza.setnFolioPoliza(rs.getLong("nFolioPoliza"));
                encPoliza.setnMes(rs.getInt("nMes"));
                encPoliza.setnPolizaAutomatica(rs.getString("nPolizaAutomatica"));
            }
            return encPoliza;
        } catch (Exception e) {
            throw new PolizaException(e);
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e) {
                    log.warn("Excepcion cerrando ResultSet", e);
                } finally {
                    rs = null;
                }
            if (pStmnt != null)
                try {
                    pStmnt.close();
                } catch (Exception e2) {
                    log.warn("Excepcion cerrando PreparedStatement", e2);
                } finally {
                    pStmnt = null;
                }
        }
    }

    public static Poliza cargaPolizaAplicada(Connection con, String nFolioPoliza, String cCentroContable, String cTipoPoliza, String aEjercicioFiscal) throws PolizaException {
        Poliza p = new Poliza();
        p.setEncabezado(PolizaManager.cargaEncabezadoPolizaAplicada(con, nFolioPoliza, cCentroContable, cTipoPoliza));
        p.setDetalle(PolizaManager.cargaDetallePolizaAplicada(con, nFolioPoliza, cCentroContable, cTipoPoliza, aEjercicioFiscal));
        return p;
    }

    private static List<DocPolizaEncabezado> extraeEncabezado(ResultSet rs) throws SQLException {
        List<DocPolizaEncabezado> l = new ArrayList<DocPolizaEncabezado>();
        while (rs.next()) {
            DocPolizaEncabezado encabezado = new DocPolizaEncabezado();
            encabezado.setaEjercicioFiscal(rs.getString("aEjercicioFiscal"));
            encabezado.setcCentroContable(rs.getString("cCentroContable"));
            encabezado.setcComentarios(rs.getString("cComentarios"));
            encabezado.setcConcepto(rs.getString("cConcepto"));
            encabezado.setcDescripcionPoliza(rs.getString("cDescripcionPoliza"));
            encabezado.setcDocumentoHaplicado(rs.getString("cDocumentoHaplicado"));
            encabezado.setCidOrigen(rs.getString("cidOrigen"));
            encabezado.setcIdUsuarioAprobacion(rs.getString("cIdUsuarioAprobacion"));
            encabezado.setcIdUsuarioCaptura(rs.getString("cIdUsuarioCaptura"));
            encabezado.setcIdUsuarioRevision(rs.getString("cIdUsuarioRevision"));
            encabezado.setcRamo(rs.getString("cRamo"));
            encabezado.setcRevisado(rs.getString("cRevisado"));
            encabezado.setcTipoDocumento(rs.getString("cTipoDocumento"));
            encabezado.setcTipoPoliza(rs.getString("cTipoPoliza"));
            encabezado.setcUnidadResponsable(rs.getString("cUnidadResponsable"));
            encabezado.setcUnidadResponsable(rs.getString("cUnidadResponsableContable"));
            encabezado.setfAplicacion(rs.getDate("fAplicacion"));
            encabezado.setfCancelacion(rs.getDate("fCancelacion"));
            encabezado.setfCarga(rs.getDate("fCarga"));
            encabezado.setmTotalAbonos(rs.getString("mTotalAbonos"));
            encabezado.setmTotalCargos(rs.getString("mTotalCargos"));
            encabezado.setnCambio(rs.getInt("nCambio"));
            encabezado.setnFolioDocPoliza(rs.getInt("nFolioDocPoliza"));
            encabezado.setnFolioPoliza(rs.getInt("nFolioPoliza"));
            encabezado.setnFolioPolizaCancelacion(rs.getInt("nFolioPolizaCancelacion"));
            encabezado.setnIdCasoOrigen(rs.getInt("nIdCasoOrigen"));
            encabezado.setnMes(rs.getInt("nMes"));
            encabezado.setnTipoAjuste(rs.getInt("nTipoAjuste"));
            encabezado.setPeriodo13(rs.getString("periodo13"));
            encabezado.setADEFAS(rs.getString("ADEFAS"));
            l.add(encabezado);
        }
        return l;
    }

    public static List<DocPolizaEncabezado> getPolizasEnCaptura(Connection conn, String cCentroContable, int nMes, int aEjercicioFiscal, boolean mesActual) throws PolizaException {
        PreparedStatement pStmnt = null;
        ResultSet rs = null;
        String qry = "SELECT	* " + "  FROM	tDocPolizaEncabezado t WITH(nolock)" + " WHERE	(t.cDocumentoHaplicado is null OR t.cDocumentoHaplicado = 'E' or (select  COUNT(*) from CG_CASO_OPERACION WITH(nolock) where id_caso = t.nIdCasoOrigen and ID_OPER!=4 )!=0 )" + "		AND	 t.aEjercicioFiscal = ?";
        if (cCentroContable != null && !"".equals(cCentroContable))
            qry += "		AND	 t.cCentroContable = ?";
        qry += "		AND	 t.nMes " + (mesActual ? "=" : "<") + " ?  AND t.nMes > 0";
        int cnt = 1;
        try {
            pStmnt = conn.prepareStatement(qry);
            pStmnt.setInt(cnt++, aEjercicioFiscal);
            if (cCentroContable != null && !"".equals(cCentroContable))
                pStmnt.setString(cnt++, cCentroContable);
            pStmnt.setInt(cnt++, nMes);
            rs = pStmnt.executeQuery();
            return extraeEncabezado(rs);
        } catch (Exception e) {
            throw new PolizaException(e);
        } finally {
            if (pStmnt != null)
                try {
                    pStmnt.close();
                } catch (Exception e2) {
                    log.warn("Error cerrando PreparedStatement ", e2);
                }
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e2) {
                    log.warn("Error cerrando ResultSet ", e2);
                }
        }
    }

    public static int[] insertTMovimiento(Connection con, List<Movimiento> movimientos) throws PolizaException {
        String qry = "INSERT INTO tmovimiento " + " (nfoliopoliza,  " + " ndocrenglon, " + " ncuenta, " + " nsubcuenta, " + " mmovimiento, " + " ctipomovimiento, " + " cdescripcionmovpol, " + " ccentrocontable, " + " ctipodocumento, " + " cramo, " + " foperacionmovimiento, " + " cfoliodocumentomovimiento, " + " ccancelamovimiento, " + " fmovimiento,  " + " dconceptomovimiento, " + " cmoneda,  " + " aejerciciofiscal, " + " ctipopoliza,  " + " cunidadresponsable, nTipoAjuste, periodo13, ADEFAS, parcial) " + " VALUES     ( ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?) ";
        PreparedStatement pStmnt = null;
        int[] r = new int[0];
        try {
            pStmnt = con.prepareStatement(qry);
            for (Iterator<Movimiento> i = movimientos.iterator(); i.hasNext(); ) {
                Movimiento movimiento = i.next();
                pStmnt.setLong(1, movimiento.getNfoliopoliza());
                pStmnt.setLong(2, movimiento.getNdocrenglon());
                pStmnt.setString(3, movimiento.getNcuenta());
                pStmnt.setString(4, movimiento.getNsubcuenta());
                pStmnt.setDouble(5, movimiento.getMmovimiento());
                pStmnt.setString(6, movimiento.getCtipomovimiento());
                pStmnt.setString(7, movimiento.getCdescripcionmovpol());
                pStmnt.setString(8, movimiento.getCcentrocontable());
                pStmnt.setString(9, movimiento.getCtipodocumento());
                pStmnt.setString(10, movimiento.getCramo());
                pStmnt.setTimestamp(11, (movimiento.getFoperacionmovimiento() == null ? null : new Timestamp(movimiento.getFoperacionmovimiento().getTime())));
                pStmnt.setLong(12, movimiento.getCfoliodocumentomovimiento());
                pStmnt.setString(13, movimiento.getCcancelamovimiento());
                pStmnt.setTimestamp(14, (movimiento.getFmovimiento() == null ? null : new Timestamp(movimiento.getFmovimiento().getTime())));
                pStmnt.setString(15, movimiento.getDconceptomovimiento());
                pStmnt.setString(16, movimiento.getCmoneda());
                pStmnt.setString(17, movimiento.getAejerciciofiscal());
                pStmnt.setString(18, movimiento.getCtipopoliza());
                pStmnt.setString(19, movimiento.getCunidadresponsable());
                pStmnt.setInt(20, movimiento.getnTipoAjuste());
                pStmnt.setString(21, movimiento.getPeriodo13());
                pStmnt.setString(22, movimiento.getADEFAS());
                pStmnt.setString(23, movimiento.getParcial());
                pStmnt.addBatch();
            }
            r = pStmnt.executeBatch();
            return r;
        } catch (Exception e) {
            throw new PolizaException(e);
        } finally {
            if (pStmnt != null)
                try {
                    pStmnt.close();
                } catch (Exception e) {
                    log.warn("Error cerrando PreparedStatement", e);
                }
        }
    }

    public static int insertTMovimiento(Connection con, Movimiento movimiento) throws PolizaException {
        String qry = "INSERT INTO tmovimiento " + " (nfoliopoliza,  " + " ndocrenglon, " + " ncuenta, " + " nsubcuenta, " + " mmovimiento, " + " ctipomovimiento, " + " cdescripcionmovpol, " + " ccentrocontable, " + " ctipodocumento, " + " cramo, " + " foperacionmovimiento, " + " cfoliodocumentomovimiento, " + " ccancelamovimiento, " + " fmovimiento,  " + " dconceptomovimiento, " + " cmoneda,  " + " aejerciciofiscal, " + " ctipopoliza,  " + " cunidadresponsable, nTipoAjuste, Periodo13, ADEFAS, parcial) " + " VALUES     ( ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?) ";
        PreparedStatement pStmnt = null;
        int r = 0;
        try {
            pStmnt = con.prepareStatement(qry);
            pStmnt.setLong(1, movimiento.getNfoliopoliza());
            pStmnt.setLong(2, movimiento.getNdocrenglon());
            pStmnt.setString(3, movimiento.getNcuenta());
            pStmnt.setString(4, movimiento.getNsubcuenta());
            pStmnt.setDouble(5, movimiento.getMmovimiento());
            pStmnt.setString(6, movimiento.getCtipomovimiento());
            pStmnt.setString(7, movimiento.getCdescripcionmovpol());
            pStmnt.setString(8, movimiento.getCcentrocontable());
            pStmnt.setString(9, movimiento.getCtipodocumento());
            pStmnt.setString(10, movimiento.getCramo());
            pStmnt.setDate(11, (movimiento.getFoperacionmovimiento() == null ? null : new Date(movimiento.getFoperacionmovimiento().getTime())));
            pStmnt.setLong(12, movimiento.getCfoliodocumentomovimiento());
            pStmnt.setString(13, movimiento.getCcancelamovimiento());
            pStmnt.setDate(14, (movimiento.getFmovimiento() == null ? null : new Date(movimiento.getFmovimiento().getTime())));
            pStmnt.setString(15, movimiento.getDconceptomovimiento());
            pStmnt.setString(16, movimiento.getCmoneda());
            pStmnt.setString(17, movimiento.getAejerciciofiscal());
            pStmnt.setString(18, movimiento.getCtipopoliza());
            pStmnt.setString(19, movimiento.getCunidadresponsable());
            pStmnt.setInt(20, movimiento.getnTipoAjuste());
            pStmnt.setString(21, movimiento.getPeriodo13());
            pStmnt.setString(22, movimiento.getADEFAS());
            pStmnt.setString(23, movimiento.getParcial());
            r = pStmnt.executeUpdate();
            return r;
        } catch (Exception e) {
            throw new PolizaException(e);
        } finally {
            if (pStmnt != null)
                try {
                    pStmnt.close();
                } catch (Exception e) {
                    log.warn("Error cerrando PreparedStatement", e);
                }
        }
    }

    public static List<DocPolizaEncabezado> resumenPolizas(Connection conn, DocPolizaEncabezado modelo, String preferredDateRange) throws PolizaException {
        PreparedStatement pStmnt = null;
        ResultSet rs = null;
        String queryBase = "SELECT	* FROM tDocPolizaEncabezado with(nolock) ";
        String token = "WHERE";
        if (preferredDateRange == null || "".equals(preferredDateRange))
            preferredDateRange = " = ";
        if (modelo.getnFolioDocPoliza() > 0) {
            queryBase += token + " nFolioDocPoliza = ? ";
            token = "AND";
        }
        if (modelo.getfCarga() != null) {
            queryBase += token + " fCarga " + preferredDateRange + " ? ";
            token = "AND";
        }
        if (modelo.getfAplicacion() != null) {
            queryBase += token + " fAplicacion " + preferredDateRange + " ? ";
            token = "AND";
        }
        if (modelo.getcCentroContable() != null && !"".equals(modelo.getcCentroContable())) {
            queryBase += token + " cCentroContable = ? ";
            token = "AND";
        }
        if (modelo.getcRamo() != null && !"".equals(modelo.getcRamo())) {
            queryBase += token + " cRamo = ? ";
            token = "AND";
        }
        if (modelo.getcUnidadResponsable() != null && !"".equals(modelo.getcUnidadResponsable())) {
            queryBase += token + " cUnidadResponsable = ? ";
            token = "AND";
        }
        if (modelo.getcDocumentoHaplicado() != null && !"".equals(modelo.getcDocumentoHaplicado())) {
            queryBase += token + " cDocumentoHaplicado = ? ";
            token = "AND";
        }
        if (modelo.getnFolioPoliza() > 0) {
            queryBase += token + " nFolioPoliza = ? ";
            token = "AND";
        }
        if (modelo.getcTipoPoliza() != null && !"".equals(modelo.getcTipoPoliza())) {
            queryBase += token + " cTipoPoliza = ? ";
            token = "AND";
        }
        if (modelo.getnMes() > 0) {
            queryBase += token + " nMes = ? ";
            token = "AND";
        }
        if (modelo.getcRevisado() != null && !"".equals(modelo.getcRevisado())) {
            queryBase += token + " cRevisado = ? ";
            token = "AND";
        }
        if (modelo.getaEjercicioFiscal() != null && !"".equals(modelo.getaEjercicioFiscal())) {
            queryBase += token + " aEjercicioFiscal = ? ";
            token = "AND";
        }
        if (modelo.getcUnidadResponsableContable() != null && !"".equals(modelo.getcUnidadResponsableContable())) {
            queryBase += token + " cUnidadResponsableContable = ? ";
            token = "AND";
        }
        if (modelo.getnFolioPolizaCancelacion() > 0) {
            queryBase += token + " nFolioPolizaCancelacion = ? ";
            token = "AND";
        }
        if (modelo.getfCancelacion() != null) {
            queryBase += token + " fCancelacion " + preferredDateRange + " ? ";
            token = "AND";
        }
        if (modelo.getcDescripcionPoliza() != null && !"".equals(modelo.getcDescripcionPoliza())) {
            queryBase += token + " cDescripcionPoliza = ? ";
            token = "AND";
        }
        if (modelo.getcConcepto() != null && !"".equals(modelo.getcConcepto())) {
            queryBase += token + " cConcepto = ? ";
            token = "AND";
        }
        if (modelo.getcIdUsuarioCaptura() != null && !"".equals(modelo.getcIdUsuarioCaptura())) {
            queryBase += token + " cIdUsuarioCaptura = ? ";
            token = "AND";
        }
        if (modelo.getcIdUsuarioRevision() != null && !"".equals(modelo.getcIdUsuarioRevision())) {
            queryBase += token + " cIdUsuarioRevision = ? ";
            token = "AND";
        }
        if (modelo.getcIdUsuarioAprobacion() != null && !"".equals(modelo.getcIdUsuarioAprobacion())) {
            queryBase += token + " cIdUsuarioAprobacion = ? ";
            token = "AND";
        }
        if (modelo.getCidOrigen() != null && !"".equals(modelo.getCidOrigen())) {
            queryBase += token + " cidOrigen = ? ";
            token = "AND";
        }
        if (modelo.getmTotalCargos() != null && !"".equals(modelo.getmTotalCargos())) {
            queryBase += token + " mTotalCargos = ? ";
            token = "AND";
        }
        if (modelo.getmTotalAbonos() != null && !"".equals(modelo.getmTotalAbonos())) {
            queryBase += token + " mTotalAbonos = ? ";
            token = "AND";
        }
        if (modelo.getcTipoDocumento() != null && !"".equals(modelo.getcTipoDocumento())) {
            queryBase += token + " cTipoDocumento = ? ";
            token = "AND";
        }
        if (modelo.getcComentarios() != null && !"".equals(modelo.getcComentarios())) {
            queryBase += token + " cComentarios = ? ";
            token = "AND";
        }
        if (modelo.getnCambio() > 0) {
            queryBase += token + " nCambio = ? ";
            token = "AND";
        }
        if (modelo.getnIdCasoOrigen() > 0) {
            queryBase += token + " nIdCasoOrigen = ? ";
            token = "AND";
        }
        try {
            pStmnt = conn.prepareStatement(queryBase);
            int cnt = 1;
            if (modelo.getnFolioDocPoliza() > 0) {
                pStmnt.setInt(cnt++, modelo.getnFolioDocPoliza());
            }
            if (modelo.getfCarga() != null) {
                pStmnt.setDate(cnt++, new Date(modelo.getfCarga().getTime()));
            }
            if (modelo.getfAplicacion() != null) {
                pStmnt.setDate(cnt++, new Date(modelo.getfAplicacion().getTime()));
            }
            if (modelo.getcCentroContable() != null && !"".equals(modelo.getcCentroContable())) {
                pStmnt.setString(cnt++, modelo.getcCentroContable());
            }
            if (modelo.getcRamo() != null && !"".equals(modelo.getcRamo())) {
                pStmnt.setString(cnt++, modelo.getcRamo());
            }
            if (modelo.getcUnidadResponsable() != null && !"".equals(modelo.getcUnidadResponsable())) {
                pStmnt.setString(cnt++, modelo.getcUnidadResponsable());
            }
            if (modelo.getcDocumentoHaplicado() != null && !"".equals(modelo.getcDocumentoHaplicado())) {
                pStmnt.setString(cnt++, modelo.getcDocumentoHaplicado());
            }
            if (modelo.getnFolioPoliza() > 0) {
                pStmnt.setInt(cnt++, modelo.getnFolioPoliza());
            }
            if (modelo.getcTipoPoliza() != null && !"".equals(modelo.getcTipoPoliza())) {
                pStmnt.setString(cnt++, modelo.getcTipoPoliza());
            }
            if (modelo.getnMes() > 0) {
                pStmnt.setInt(cnt++, modelo.getnMes());
            }
            if (modelo.getcRevisado() != null && !"".equals(modelo.getcRevisado())) {
                pStmnt.setString(cnt++, modelo.getcRevisado());
            }
            if (modelo.getaEjercicioFiscal() != null && !"".equals(modelo.getaEjercicioFiscal())) {
                pStmnt.setString(cnt++, modelo.getaEjercicioFiscal());
            }
            if (modelo.getcUnidadResponsableContable() != null && !"".equals(modelo.getcUnidadResponsableContable())) {
                pStmnt.setString(cnt++, modelo.getcUnidadResponsableContable());
            }
            if (modelo.getnFolioPolizaCancelacion() > 0) {
                pStmnt.setInt(cnt++, modelo.getnFolioPolizaCancelacion());
            }
            if (modelo.getfCancelacion() != null) {
                pStmnt.setDate(cnt++, new Date(modelo.getfCancelacion().getTime()));
            }
            if (modelo.getcDescripcionPoliza() != null && !"".equals(modelo.getcDescripcionPoliza())) {
                pStmnt.setString(cnt++, modelo.getcDescripcionPoliza());
            }
            if (modelo.getcConcepto() != null && !"".equals(modelo.getcConcepto())) {
                pStmnt.setString(cnt++, modelo.getcConcepto());
            }
            if (modelo.getcIdUsuarioCaptura() != null && !"".equals(modelo.getcIdUsuarioCaptura())) {
                pStmnt.setString(cnt++, modelo.getcIdUsuarioCaptura());
            }
            if (modelo.getcIdUsuarioRevision() != null && !"".equals(modelo.getcIdUsuarioRevision())) {
                pStmnt.setString(cnt++, modelo.getcIdUsuarioRevision());
            }
            if (modelo.getcIdUsuarioAprobacion() != null && !"".equals(modelo.getcIdUsuarioAprobacion())) {
                pStmnt.setString(cnt++, modelo.getcIdUsuarioAprobacion());
            }
            if (modelo.getCidOrigen() != null && !"".equals(modelo.getCidOrigen())) {
                pStmnt.setString(cnt++, modelo.getCidOrigen());
            }
            if (modelo.getmTotalCargos() != null && !"".equals(modelo.getmTotalCargos())) {
                pStmnt.setString(cnt++, modelo.getmTotalCargos());
            }
            if (modelo.getmTotalAbonos() != null && !"".equals(modelo.getmTotalAbonos())) {
                pStmnt.setString(cnt++, modelo.getmTotalAbonos());
            }
            if (modelo.getcTipoDocumento() != null && !"".equals(modelo.getcTipoDocumento())) {
                pStmnt.setString(cnt++, modelo.getcTipoDocumento());
            }
            if (modelo.getcComentarios() != null && !"".equals(modelo.getcComentarios())) {
                pStmnt.setString(cnt++, modelo.getcComentarios());
            }
            if (modelo.getnCambio() > 0) {
                pStmnt.setInt(cnt++, modelo.getnCambio());
            }
            if (modelo.getnIdCasoOrigen() > 0l) {
                pStmnt.setLong(cnt++, modelo.getnIdCasoOrigen());
            }
            rs = pStmnt.executeQuery();
            return extraeEncabezado(rs);
        } catch (Exception e) {
            throw new PolizaException(e);
        } finally {
            if (pStmnt != null)
                try {
                    pStmnt.close();
                } catch (Exception e2) {
                    log.warn("Error cerrando PreparedStatement", e2);
                }
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e2) {
                    log.warn("Error cerrando ResultSet", e2);
                }
            pStmnt = null;
            rs = null;
        }
    }

    public static int updateDocPolizaEncabezado(Connection con, DocPolizaEncabezado dpe) throws PolizaException {
        int r = -1;
        String qry = " UPDATE	tDocPolizaEncabezado " + " SET	cComentarios  = ?, " + "	cConcepto  = ?, " + "	cDescripcionPoliza  = ?, " + "	cDocumentoHaplicado = ?, " + "	cidOrigen  = ?, " + "	cIdUsuarioAprobacion  = ?, " + "	cIdUsuarioCaptura  = ?, " + "	cIdUsuarioRevision  = ?, " + "	cRamo = ?, " + "	cRevisado = ?, " + "	cTipoDocumento  = ?, " + "	cUnidadResponsable = ?, " + "	fAplicacion = ?, " + "	fCancelacion = ?, " + "	fCarga = ?, " + "	mTotalAbonos  = ?, " + "	mTotalCargos  = ?, " + "	nCambio  = ?, " + "	nFolioPoliza  = ?, " + "	nFolioPolizaCancelacion  = ?, " + "	nIdCasoOrigen  = ?, " + "	nMes = ? " + // + "	cUnidadResponsableContable = ? "
        " WHERE	nFolioDocPoliza = ? " + " AND	cTipoPoliza = ? " + " AND	cCentroContable = ? " + " AND	aEjercicioFiscal = ? ";
        PreparedStatement pStmnt = null;
        try {
            pStmnt = con.prepareStatement(qry);
            pStmnt.setString(1, dpe.getcComentarios());
            pStmnt.setString(2, dpe.getcConcepto());
            pStmnt.setString(3, dpe.getcDescripcionPoliza());
            pStmnt.setString(4, dpe.getcDocumentoHaplicado());
            pStmnt.setString(5, dpe.getCidOrigen());
            pStmnt.setString(6, dpe.getcIdUsuarioAprobacion());
            pStmnt.setString(7, dpe.getcIdUsuarioCaptura());
            pStmnt.setString(8, dpe.getcIdUsuarioRevision());
            pStmnt.setString(9, dpe.getcRamo());
            pStmnt.setString(10, dpe.getcRevisado());
            pStmnt.setString(11, dpe.getcTipoDocumento());
            pStmnt.setString(12, dpe.getcUnidadResponsable());
            pStmnt.setDate(13, (dpe.getfAplicacion() != null ? new Date(dpe.getfAplicacion().getTime()) : null));
            pStmnt.setDate(14, (dpe.getfCancelacion() != null ? new Date(dpe.getfCancelacion().getTime()) : null));
            pStmnt.setDate(15, (dpe.getfCarga() != null ? new Date(dpe.getfCarga().getTime()) : null));
            pStmnt.setString(16, dpe.getmTotalAbonos());
            pStmnt.setString(17, dpe.getmTotalCargos());
            pStmnt.setInt(18, dpe.getnCambio());
            pStmnt.setInt(19, dpe.getnFolioPoliza());
            pStmnt.setInt(20, dpe.getnFolioPolizaCancelacion());
            pStmnt.setLong(21, dpe.getnIdCasoOrigen());
            pStmnt.setInt(22, dpe.getnMes());
            // pStmnt.setString(23, dpe.getcUnidadResponsableContable());
            pStmnt.setInt(23, dpe.getnFolioDocPoliza());
            pStmnt.setString(24, dpe.getcTipoPoliza());
            pStmnt.setString(25, dpe.getcCentroContable());
            pStmnt.setString(26, dpe.getaEjercicioFiscal());
            r = pStmnt.executeUpdate();
            return r;
        } catch (Exception e) {
            throw new PolizaException(e);
        } finally {
            if (pStmnt != null)
                try {
                    pStmnt.close();
                } catch (Exception e2) {
                    log.warn("Error cerrando PreparedStatemnt", e2);
                }
        }
    }

    public static int updateEncabezadoPolizaAplicada(Connection con, EncabezadoPoliza encabezado) throws PolizaException {
        int r = 0;
        String qry = "UPDATE	tPoliza " + "SET	cDescripcionPoliza = ?, " + "		mTotalCargo = ?, " + "		mTotalAbono = ?, " + "		nCuenta = ?, " + "		nMes = ?, " + "		nPolizaAutomatica = ?, " + "		aEjercicioFiscal = ?, " + "		cTipoDocumento = ?, " + "		DocHAplicado = ?, " + "		cUsuarioAutorizo = ? " + // + "		,cReferencia = ? "
        "WHERE	nFolioPoliza = ? " + "AND	cCentroContable = ? " + "AND	cTipoPoliza = ? ";
        PreparedStatement pStmnt = null;
        try {
            pStmnt = con.prepareStatement(qry);
            pStmnt.setString(1, encabezado.getcDescripcionPoliza());
            pStmnt.setString(2, encabezado.getmTotalCargo());
            pStmnt.setString(3, encabezado.getmTotalAbono());
            pStmnt.setString(4, encabezado.getnCuenta());
            pStmnt.setInt(5, encabezado.getnMes());
            pStmnt.setString(6, encabezado.getnPolizaAutomatica());
            pStmnt.setString(7, encabezado.getaEjercicioFiscal());
            pStmnt.setString(8, encabezado.getcTipoDocumento());
            pStmnt.setString(9, encabezado.getDocHAplicado());
            pStmnt.setString(10, encabezado.getcUsuarioAutorizo());
            // pStmnt.setString(11, encabezado.getReferencia());
            pStmnt.setLong(11, encabezado.getnFolioPoliza());
            pStmnt.setString(12, encabezado.getcCentroContable());
            pStmnt.setString(13, encabezado.getcTipoPoliza());
            r = pStmnt.executeUpdate();
            return r;
        } catch (Exception e) {
            throw new PolizaException(e);
        } finally {
            if (pStmnt != null)
                try {
                    pStmnt.close();
                } catch (Exception e2) {
                    log.warn("Error cerrando PreparedStatement", e2);
                } finally {
                    pStmnt = null;
                }
        }
    }

    public static Caso instanciaCasoPoliza(String ue) throws Exception {
        Usuario u = new Usuario();
        u.setLogin("admin");
        u.setNombre("SYSTEM");
        u.setU_UR(ue);
        FolioGeneratorInterface fg = null;
        ClassLoader cl = PolizaManager.class.getClassLoader();
        Class<?> clase = cl.loadClass(GestionInterface.FOLIO_GENERATOR);
        fg = (FolioGeneratorInterface) clase.newInstance();
        ObraPublicaContractBusinessLogic obl = new ObraPublicaContractBusinessLogic(GestionInterface.ATT_CONEXION);
        Caso c = obl.generaCaso(u, 13, fg, u.getLogin());
        return c;
    }

    public static Caso instanciaCasoPoliza(String ue, String uLogin, String uNombre, String centroContable) throws Exception {
        Usuario u = new Usuario();
        u.setLogin(uLogin);
        u.setNombre(uNombre);
        u.setU_UR(ue);
        UsuarioPropiedades upCC = new UsuarioPropiedades();
        upCC.setLogin(uLogin);
        upCC.setNombre("CCENTROCONTABLE");
        upCC.setValor(centroContable);
        u.setPropiedad("CCENTROCONTABLE", upCC);
        FolioGeneratorInterface fg = null;
        ClassLoader cl = PolizaManager.class.getClassLoader();
        Class<?> clase = cl.loadClass(GestionInterface.FOLIO_GENERATOR);
        fg = (FolioGeneratorInterface) clase.newInstance();
        ObraPublicaContractBusinessLogic obl = new ObraPublicaContractBusinessLogic(GestionInterface.ATT_CONEXION);
        Caso c = obl.generaCaso(u, 13, fg, u.getLogin());
        return c;
    }
}
