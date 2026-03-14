package com.syc.sai.ejercido.pagado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class PagadoManager {

    private static String calculaEvento(String eventoDevengado) {
        String[] cEventoCa = eventoDevengado.split("_");
        String cEvento = "P";
        for (int i = 1; i < cEventoCa.length; i++) {
            cEvento += "_";
            cEvento += cEventoCa[i];
        }
        return cEvento;
    }

    public static List<Pagado> generaPagadoIntegracion(Connection conn, String integracion, String usuario, String tipoPoliza) throws Exception {
        List<Pagado> integradas = new ArrayList<Pagado>();
        String queryEncabezado = "SELECT sai.ctipopago,  " + "        sai.nfolio,  " + "        sai.canocontrarrecibo,  " + "        '" + tipoPoliza + "'                                   AS cTipoPoliza,  " + "        '" + usuario + "'                                   AS usuario,  " + "        sai.cdescripcionpoliza,  " + "        sai.cunidadresponsablecontable,  " + "        sicop.nfolioclc,  " + "        sai.integracion,  " + "        sicop.fechasiaff,  " + "        sai.cramo,  " + "        '" + usuario + "'                           AS usuario,  " + "        sicop.fechaaplsicop                  AS FechaAplicacionSicop,  " + "        sicop.fechapagosicop                 AS FechaPagoSicop,  " + "        sicop.solicitudpago,  " + "        sicop.numproceso                     AS NumeroProceso,  " + "        sicop.folio_siaff_112                AS nFolioSIAFF,  " + "        CONVERT(VARCHAR(32), Getdate(), 103) AS fechaPagado  " + " FROM   v_aplicarejercidopagadoencabezado sai WITH(nolock)  " + "        INNER JOIN dbo.tsicopencabezado sicop WITH(nolock)  " + "                ON sai.integracion = sicop.canocontrarrecibo  " + " WHERE  integracion = '" + integracion + "' ";
        String queryDetalle = " SELECT ndocrenglon,  " + "        cmes,  " + "        cejercicio,  " + "        ep,  " + "        cidcuentacontable,  " + "        mcomprometido,  " + "        Isnull(npoliza, 0)    AS nPoliza,  " + "        id_tipo_movimiento,  " + "        id_tipo_concepto,  " + "        cevento,  " + "        ccentrocontable,  " + "        rfc,  " + "        mimporteneto,  " + "        '' AS alm,  " + "        mimportebruto,  " + "        mimportemasiva,  " + "        mimporteiva,  " + "        ncapitulo,  " + "        msancion,  " + "        mdevolucion,  " + "        mimporteamortiza,  " + "        mretencion,  " + "        mpenalizacion,  " + "        m2millar,  " + "        m23iva,  " + "        misrhonorarios,  " + "        mobra5,  " + "        mimporteflete4,  " + "        misrarrenda,  " + "        mretimpuestocedular,  " + "        mimporteneto          AS mimporte,  " + "        mimporteivaarrenda,  " + "        mimporteivahonorarios,  " + "        mimporteflete23,  " + "        mimporteivaprov,  " + "        mimporteobra,  " + "        Isnull(mcnic, 0.00)   AS mCNIC,  " + "        Isnull(mimdt, 0.00)   AS mIMDT,  " + "        Isnull(mtesofe, 0.00) AS mTesofe,  " + "        altaalmacen,  " + "        ccentrocontable       AS cIdEntidadContable,  " + "        cidrelacion,  " + "        ctab,  " + "        Substring(ep, 32, 5)  AS OBGT, " + "        mImporteISRLaudos,  " + "        mPasivoDiferido,  " + "		  cPasivo, " + "		  cUnidadResponsable " + " FROM   v_aplicarejercidopagadodetalle WITH(nolock)  " + " WHERE  ctipopago = ?  " + "        AND nfolio = ?  " + "        AND cevento != 'ANTICIPO'  " + "        AND cevento != 'ANTICIPO_DIV' ";
        Statement stmnt = null;
        PreparedStatement psDetalle = null;
        ResultSet rs = null;
        ResultSet rsDetalle = null;
        try {
            psDetalle = conn.prepareStatement(queryDetalle);
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery(queryEncabezado);
            while (rs.next()) {
                PagadoEncabezado encabezado = PagadoManager.parseResultSetEncabezado(rs);
                List<PagadoDetalle> detalle = new ArrayList<PagadoDetalle>();
                psDetalle.setString(1, encabezado.getTipoPago());
                psDetalle.setInt(2, encabezado.getFolioPAGO());
                rsDetalle = psDetalle.executeQuery();
                while (rsDetalle.next()) {
                    detalle.add(PagadoManager.parsePagado(conn, rsDetalle, encabezado.getTipoPago(), encabezado.getFolioPAGO(), "PAGADO"));
                }
                psDetalle.clearParameters();
                rsDetalle.close();
                rsDetalle = null;
                integradas.add(new Pagado(encabezado, detalle));
            }
            return integradas;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(psDetalle);
            CloseObject.closeObject(stmnt);
        }
    }

    private static int insertaDetalle(Connection conn, List<PagadoDetalle> detalle, PreparedStatement psInsertDet) throws Exception {
        int insertados = 0;
        for (Iterator<PagadoDetalle> i = detalle.iterator(); i.hasNext(); ) {
            PagadoDetalle detInsert = i.next();
            psInsertDet.setString(1, detInsert.getTipoPago());
            psInsertDet.setInt(2, detInsert.getFolioPAGO());
            psInsertDet.setInt(3, detInsert.getDocRenglon());
            psInsertDet.setInt(4, detInsert.getMes());
            psInsertDet.setInt(5, detInsert.getEjercicioFiscal());
            psInsertDet.setString(6, detInsert.getIdEntidadContable());
            psInsertDet.setString(7, detInsert.getIdRelacion());
            psInsertDet.setString(8, detInsert.getEP());
            psInsertDet.setString(9, detInsert.getIdCuentaContable());
            psInsertDet.setBigDecimal(10, detInsert.getImporteComprometido());
            psInsertDet.setInt(11, detInsert.getNumeroPoliza());
            psInsertDet.setString(12, detInsert.getIdTipoMovimiento());
            psInsertDet.setString(13, detInsert.getIdTipoConcepto());
            psInsertDet.setString(14, detInsert.getEvento());
            psInsertDet.setInt(15, detInsert.getMes());
            psInsertDet.setString(16, detInsert.getCentroContable());
            psInsertDet.setString(17, detInsert.getRFC());
            psInsertDet.setBigDecimal(18, detInsert.getImporteNeto());
            psInsertDet.setString(19, detInsert.getALM());
            psInsertDet.setBigDecimal(20, detInsert.getImporteBruto());
            psInsertDet.setBigDecimal(21, detInsert.getImporteMasIva());
            psInsertDet.setBigDecimal(22, detInsert.getImporteIva());
            psInsertDet.setInt(23, detInsert.getCapitulo());
            psInsertDet.setBigDecimal(24, detInsert.getImporteSancion());
            psInsertDet.setBigDecimal(25, detInsert.getImporteDevolucion());
            psInsertDet.setBigDecimal(26, detInsert.getImporteAmortiza());
            psInsertDet.setBigDecimal(27, detInsert.getImporteRetencion());
            psInsertDet.setBigDecimal(28, detInsert.getImportePenalizacion());
            psInsertDet.setBigDecimal(29, detInsert.getImporte2Millar());
            psInsertDet.setBigDecimal(30, detInsert.getImporte23IVA());
            psInsertDet.setBigDecimal(31, detInsert.getImporteISRHonorarios());
            psInsertDet.setBigDecimal(32, detInsert.getImporteObra5());
            psInsertDet.setBigDecimal(33, detInsert.getImporteFlete4());
            psInsertDet.setBigDecimal(34, detInsert.getImporteISRArrenda());
            psInsertDet.setBigDecimal(35, detInsert.getImporteRetImpuestoCedular());
            psInsertDet.setBigDecimal(36, detInsert.getImporteIvaArrenda());
            psInsertDet.setBigDecimal(37, detInsert.getImporteIvaHonorarios());
            psInsertDet.setBigDecimal(38, detInsert.getImporteFlete23());
            psInsertDet.setBigDecimal(39, detInsert.getImporteIvaProv());
            psInsertDet.setBigDecimal(40, detInsert.getImporteObra());
            psInsertDet.setBigDecimal(41, detInsert.getImporteCNIC());
            psInsertDet.setBigDecimal(42, detInsert.getImporteTesofe());
            psInsertDet.setString(43, detInsert.getAltaAlmacen());
            psInsertDet.setInt(44, detInsert.getEjercicioFiscal());
            psInsertDet.setBigDecimal(45, detInsert.getImporteIMDT());
            psInsertDet.setBigDecimal(46, detInsert.getImporte());
            psInsertDet.setInt(47, detInsert.getFolioPagado());
            psInsertDet.setInt(48, detInsert.getOBGT());
            psInsertDet.setString(49, detInsert.getCTAB());
            psInsertDet.setBigDecimal(50, detInsert.getImporteISRLaudos());
            psInsertDet.setBigDecimal(51, detInsert.getPasivoDiferido());
            psInsertDet.setString(52, detInsert.getcPasivo());
            psInsertDet.setString(53, detInsert.getcUnidadResponsable());
            insertados += psInsertDet.executeUpdate();
        }
        return insertados;
    }

    private static int insertaEncabezado(Connection conn, PagadoEncabezado encabezado, PreparedStatement psInsertEnc) throws Exception {
        psInsertEnc.setString(1, encabezado.getTipoPago());
        psInsertEnc.setInt(2, encabezado.getFolioPAGO());
        psInsertEnc.setString(3, encabezado.getCaNoContrarrecibo());
        psInsertEnc.setString(4, encabezado.getTipoPoliza());
        psInsertEnc.setString(5, encabezado.getLogin());
        psInsertEnc.setString(6, encabezado.getDescripcionPoliza());
        psInsertEnc.setString(7, encabezado.getUnidadResponsableContable());
        psInsertEnc.setInt(8, encabezado.getFolioSICOP());
        psInsertEnc.setDate(9, new java.sql.Date(Util.stringToDate(encabezado.getfAplicacion(), "dd/MM/yyyy").getTime()));
        psInsertEnc.setString(10, encabezado.getRamo());
        psInsertEnc.setString(11, encabezado.getLogin());
        psInsertEnc.setDate(12, new java.sql.Date(Util.stringToDate(encabezado.getFechaAplicacionSicop(), "dd/MM/yyyy").getTime()));
        psInsertEnc.setDate(13, new java.sql.Date(Util.stringToDate(encabezado.getFechaPagoSicop(), "dd/MM/yyyy").getTime()));
        psInsertEnc.setInt(14, encabezado.getSolicitudPago());
        psInsertEnc.setInt(15, encabezado.getNumeroProceso());
        psInsertEnc.setString(16, encabezado.getFolioSIAFF());
        psInsertEnc.setDate(17, new java.sql.Date(Util.stringToDate(encabezado.getFechaPagado(), "dd/MM/yyyy").getTime()));
        psInsertEnc.setInt(18, encabezado.getFolioPagado());
        return psInsertEnc.executeUpdate();
    }

    public static int insertaPagado(Connection conn, List<Pagado> pagado) throws Exception {
        String queryInsertEnc = "INSERT INTO tpagadoencabezado  " + "             (ctipopago,  " + "              nfoliopago,  " + "              canocontrarrecibo,  " + "              ctipopoliza,  " + "              u_login,  " + "              cdescripcionpoliza,  " + "              cunidadresponsablecontable,  " + "              nfoliosicop,  " + "              faplicacion,  " + "              cramo,  " + "              cidusuariocaptura,  " + "              fechaaplicacionsicop,  " + "              fechapagosicop,  " + "              solicitudpago,  " + "              numeroproceso,  " + "              nfoliosiaff,  " + "              fechapagado,  " + "              nfoliopagado)  " + " VALUES      ( ?,  " + "               ?,  " + "               ?,  " + "               ?,  " + "               ?,  " + "               ?,  " + "               ?,  " + "               ?,  " + "               ?,  " + "               ?,  " + "               ?,  " + "               ?,  " + "               ?,  " + "               ?,  " + "               ?,  " + "               ?,  " + "               ?,  " + "               ?) ";
        String queryInsertDet = "INSERT INTO tpagadodetalle  " + "             (ctipopago,  " + "              nfoliopago,  " + "              ndocrenglon,  " + "              cmes,  " + "              cejercicio,  " + "              cidentidadcontable,  " + "              cidrelacion,  " + "              ep,  " + "              cidcuentacontable,  " + "              mcomprometido,  " + "              npoliza,  " + "              id_tipo_movimiento,  " + "              id_tipo_concepto,  " + "              cevento,  " + "              nmes,  " + "              ccentrocontable,  " + "              rfc,  " + "              mimporteneto,  " + "              alm,  " + "              mimportebruto,  " + "              mimportemasiva,  " + "              mimporteiva,  " + "              ncapitulo,  " + "              msancion,  " + "              mdevolucion,  " + "              mimporteamortiza,  " + "              mretencion,  " + "              mpenalizacion,  " + "              m2millar,  " + "              m23iva,  " + "              misrhonorarios,  " + "              mobra5,  " + "              mimporteflete4,  " + "              misrarrenda,  " + "              mretimpuestocedular,  " + "              mimporteivaarrenda,  " + "              mimporteivahonorarios,  " + "              mimporteflete23,  " + "              mimporteivaprov,  " + "              mimporteobra,  " + "              mcnic,  " + "              mtesofe,  " + "              altaalmacen,  " + "              aejerciciofiscal,  " + "              mimdt,  " + "              mimporte,  " + "              nfoliopagado,  " + "              obgt,  " + "              ctab," + "				mImporteISRLaudos," + "              mPasivoDiferido," + "				cPasivo, " + "				cUnidadResponsable " + "				)   " + " VALUES     ( ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "              ?,  " + "			    ?) ";
        PreparedStatement psInsertEnc = null;
        PreparedStatement psInsertDet = null;
        int insertados = 0;
        try {
            psInsertEnc = conn.prepareStatement(queryInsertEnc);
            psInsertDet = conn.prepareStatement(queryInsertDet);
            for (Iterator<Pagado> i = pagado.iterator(); i.hasNext(); ) {
                Pagado pInsert = i.next();
                int nFolioPagado = EjercidoPagadoManager.folioSiguiente(conn, "PAGADO");
                pInsert.setFolioPagado(nFolioPagado);
                insertados += PagadoManager.insertaEncabezado(conn, pInsert.getEncabezado(), psInsertEnc);
                insertados += PagadoManager.insertaDetalle(conn, pInsert.getDetalle(), psInsertDet);
                psInsertDet.clearBatch();
                psInsertEnc.clearBatch();
            }
            return insertados;
        } finally {
            CloseObject.closeObject(psInsertDet);
            CloseObject.closeObject(psInsertEnc);
        }
    }

    private static PagadoDetalle parsePagado(Connection conn, ResultSet rs, String tipoPago, int folioPago, String string) throws Exception {
        PagadoDetalle detalle = new PagadoDetalle();
        detalle.setTipoPago(tipoPago);
        detalle.setFolioPAGO(folioPago);
        detalle.setDocRenglon(rs.getInt("nDocRenglon"));
        detalle.setMes(rs.getInt("cMes"));
        detalle.setEjercicioFiscal(rs.getInt("cEjercicio"));
        detalle.setIdEntidadContable(rs.getString("cIdEntidadContable"));
        detalle.setIdRelacion(rs.getString("cIdRelacion"));
        detalle.setEP(rs.getString("EP"));
        detalle.setIdCuentaContable(rs.getString("cIdCuentaContable"));
        detalle.setImporteComprometido(rs.getBigDecimal("mComprometido"));
        detalle.setNumeroPoliza(rs.getInt("nPoliza"));
        detalle.setIdTipoMovimiento(rs.getString("ID_TIPO_MOVIMIENTO"));
        detalle.setIdTipoConcepto(rs.getString("ID_TIPO_CONCEPTO"));
        /*VGC20171121 Se cambia la parametraizacion para el pagado de penas convencionales.
		 * */
        detalle.setEvento("PENACONV".equalsIgnoreCase(rs.getString("cEvento")) ? PagadoManager.calculaEventoPenas(conn, rs.getString("EP"), tipoPago, folioPago) : PagadoManager.calculaEvento(rs.getString("cEvento")));
        detalle.setCentroContable(rs.getString("cCentroContable"));
        detalle.setRFC(rs.getString("RFC"));
        detalle.setImporteNeto(rs.getBigDecimal("mImporteNeto"));
        detalle.setALM(rs.getString("ALM"));
        detalle.setImporteBruto(rs.getBigDecimal("mImporteBruto"));
        detalle.setImporteMasIva(rs.getBigDecimal("mImporteMasIva"));
        detalle.setImporteIva(rs.getBigDecimal("mImporteIva"));
        detalle.setCapitulo(rs.getInt("nCapitulo"));
        detalle.setImporteSancion(rs.getBigDecimal("mSancion"));
        detalle.setImporteDevolucion(rs.getBigDecimal("mDevolucion"));
        detalle.setImporteAmortiza(rs.getBigDecimal("mImporteAmortiza"));
        detalle.setImporteRetencion(rs.getBigDecimal("mRetencion"));
        detalle.setImportePenalizacion(rs.getBigDecimal("mPenalizacion"));
        detalle.setImporte2Millar(rs.getBigDecimal("m2Millar"));
        detalle.setImporte23IVA(rs.getBigDecimal("m23IVA"));
        detalle.setImporteISRHonorarios(rs.getBigDecimal("mISRHonorarios"));
        detalle.setImporteObra5(rs.getBigDecimal("mObra5"));
        detalle.setImporteFlete4(rs.getBigDecimal("mImporteFlete4"));
        detalle.setImporteISRArrenda(rs.getBigDecimal("mISRArrenda"));
        detalle.setImporteRetImpuestoCedular(rs.getBigDecimal("mRetImpuestoCedular"));
        detalle.setImporteIvaArrenda(rs.getBigDecimal("mImporteIvaArrenda"));
        detalle.setImporteIvaHonorarios(rs.getBigDecimal("mImporteIvaHonorarios"));
        detalle.setImporteFlete23(rs.getBigDecimal("mImporteFlete23"));
        detalle.setImporteIvaProv(rs.getBigDecimal("mImporteIvaProv"));
        detalle.setImporteObra(rs.getBigDecimal("mImporteObra"));
        detalle.setImporteCNIC(rs.getBigDecimal("mCNIC"));
        detalle.setImporteTesofe(rs.getBigDecimal("mTesofe"));
        detalle.setAltaAlmacen(rs.getString("altaAlmacen"));
        detalle.setImporteIMDT(rs.getBigDecimal("mIMDT"));
        detalle.setImporte(rs.getBigDecimal("mImporte"));
        detalle.setOBGT(rs.getInt("OBGT"));
        detalle.setCTAB("PENACONV".equalsIgnoreCase(rs.getString("cEvento")) ? "072320005134113068" : rs.getString("CTAB"));
        detalle.setImporteISRLaudos(rs.getBigDecimal("mImporteISRLaudos"));
        detalle.setPasivoDiferido(rs.getBigDecimal("mPasivoDiferido"));
        detalle.setcPasivo(rs.getString("cPasivo"));
        return detalle;
    }

    private static String calculaEventoPenas(Connection conn, String EP, String tipoPago, int folioPago) throws Exception {
        String querySelectPagoPenas = "SELECT	penasDetalle.cTipopago, " + "		penasDetalle.nFolioPago,  " + "		penasDetalle.caNoContrarrecibo " + "  FROM	tPagoPenasConvEncabezado penasEncabezado WITH(NOLOCK) " + "		INNER JOIN  " + "		tPagoPenasConvDetalle penasDetalle WITH(NOLOCK) " + "		ON penasEncabezado.nFolioPagoPenasConv = penasDetalle.nFolioPagoPenasConv " + " WHERE	penasEncabezado.nFolioPagoPenasConv = ?";
        PreparedStatement psBuscaPago = null;
        ResultSet rsBuscaPago = null;
        PreparedStatement psBuscaOrigen = null;
        ResultSet rsBuscaOrigen = null;
        String cEvento = null;
        try {
            psBuscaPago = conn.prepareStatement(querySelectPagoPenas);
            psBuscaPago.setInt(1, folioPago);
            rsBuscaPago = psBuscaPago.executeQuery();
            if (rsBuscaPago.next()) {
                String tblEncabezado = "t" + rsBuscaPago.getString("cTipopago") + "Encabezado";
                String tblDetalle = "t" + rsBuscaPago.getString("cTipopago") + "Detalle";
                String tblFolioCol = "nFolio" + rsBuscaPago.getString("cTipopago");
                int nFolioPago = rsBuscaPago.getInt("nFolioPago");
                String caNoContrarecibo = rsBuscaPago.getString("caNoContrarrecibo");
                String querySelecOrigen = "SELECT	DISTINCT " + "		pagoEncabezado.cRadicado, " + "		pagoEncabezado.cIngresosPropios, " + "		pagoEncabezado.ID_DESTINO_GASTO,  " + "		pagoDetalle.ID_TIPO_CONCEPTO,  " + "		pagoDetalle.ID_TIPO_MOVIMIENTO   " + "  FROM	" + tblEncabezado + " pagoEncabezado WITH(NOLOCK) " + "		INNER JOIN  " + "		" + tblDetalle + " pagoDetalle WITH(NOLOCK) " + "		ON pagoEncabezado." + tblFolioCol + " = " + "pagoDetalle." + tblFolioCol + " WHERE	pagoEncabezado." + tblFolioCol + " = " + nFolioPago + "   AND	pagoEncabezado.caNoContrarrecibo = '" + caNoContrarecibo + "' " + "   AND	pagoDetalle.ep='" + EP + "' ";
                //                                        + "   AND	pagoDetalle.RFC = 'TESOFE'      ";
                psBuscaOrigen = conn.prepareStatement(querySelecOrigen);
                rsBuscaOrigen = psBuscaOrigen.executeQuery();
                if (rsBuscaOrigen.next()) {
                    boolean esRadicado = "S".equalsIgnoreCase(rsBuscaOrigen.getString("cRadicado"));
                    boolean esIP = "S".equalsIgnoreCase(rsBuscaOrigen.getString("cIngresosPropios"));
                    String idDestinoGasto = rsBuscaOrigen.getString("ID_DESTINO_GASTO");
                    String idTipoConcepto = rsBuscaOrigen.getString("ID_TIPO_CONCEPTO");
                    if (esRadicado)
                        cEvento = "P_PENAS_RAD";
                    else if (esIP)
                        cEvento = "P_PENAS_IP";
                    else if (("AL".equalsIgnoreCase(idTipoConcepto) || "PA".equalsIgnoreCase(idTipoConcepto) || "FFM".equalsIgnoreCase(idTipoConcepto) || "AAA".equalsIgnoreCase(idTipoConcepto)) && !"CP".equalsIgnoreCase(idDestinoGasto.substring(0, 2))) {
                        cEvento = "P_PENAS_PAT";
                    } else {
                        switch(Integer.parseInt(EP.substring(31, 32))) {
                            case 1:
                            case 2:
                            case 3:
                                cEvento = "P_PENAS123";
                                break;
                            case 4:
                                cEvento = "P_PENAS4";
                                break;
                            case 5:
                            case 6:
                                cEvento = "P_PENAS56";
                                break;
                            default:
                                break;
                        }
                    }
                }
            } else {
                throw new Exception("No se encontro el pago origen para el pago: " + tipoPago + " con folio:" + folioPago);
            }
        } finally {
            CloseObject.closeObject(rsBuscaPago);
            CloseObject.closeObject(rsBuscaOrigen);
            CloseObject.closeObject(psBuscaPago);
            CloseObject.closeObject(psBuscaOrigen);
        }
        return cEvento;
    }

    private static PagadoEncabezado parseResultSetEncabezado(ResultSet rs) throws Exception {
        PagadoEncabezado encabezado = new PagadoEncabezado();
        encabezado.setTipoPago(rs.getString("ctipopago"));
        encabezado.setFolioPAGO(rs.getInt("nfolio"));
        encabezado.setCaNoContrarrecibo(rs.getString("canocontrarrecibo"));
        encabezado.setTipoPoliza(rs.getString("cTipoPoliza"));
        encabezado.setLogin(rs.getString("usuario"));
        encabezado.setDescripcionPoliza(rs.getString("cdescripcionpoliza"));
        encabezado.setUnidadResponsableContable(rs.getString("cunidadresponsablecontable"));
        encabezado.setFolioCLC(rs.getInt("nfolioclc"));
        encabezado.setIntegracion(rs.getString("integracion"));
        encabezado.setFechaSIAFF(rs.getString("FechaSIAFF"));
        encabezado.setRamo(rs.getString("cramo"));
        encabezado.setFechaAplicacionSicop(rs.getString("FechaAplicacionSicop"));
        encabezado.setFechaPagoSicop(rs.getString("FechaPagoSicop"));
        encabezado.setSolicitudPago(rs.getInt("solicitudpago"));
        encabezado.setNumeroProceso(rs.getInt("NumeroProceso"));
        encabezado.setFolioSIAFF(rs.getString("nFolioSIAFF"));
        encabezado.setfAplicacion(rs.getString("FechaSIAFF"));
        encabezado.setFechaPagado(rs.getString("FechaSIAFF"));
        return encabezado;
    }
}
