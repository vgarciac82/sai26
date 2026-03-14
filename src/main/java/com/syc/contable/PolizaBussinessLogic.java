package com.syc.contable;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.CasoOperacionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class PolizaBussinessLogic extends DataSourceManager {

    public static final Logger log = LoggerFactory.getLogger(PolizaBussinessLogic.class);

    public int borraDetallePolizaAplicada(String nFolioPoliza, String cCentroContable, String cTipoPoliza, String aEjercicioFiscal) throws PolizaException {
        Connection con = null;
        int r = 0;
        try {
            con = getConnection();
            r = PolizaManager.borraDetallePolizaAplicada(con, nFolioPoliza, cCentroContable, cTipoPoliza, aEjercicioFiscal);
            con.commit();
            return r;
        } catch (PolizaException e) {
            try {
                con.rollback();
            } catch (Exception e2) {
                log.error("Error realizando rollback", e2);
            }
            throw e;
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (Exception e2) {
                log.error("Error realizando rollback", e2);
            }
            throw new PolizaException(e);
        } finally {
            if (con != null)
                try {
                    con.close();
                } catch (Exception e2) {
                    log.error("Error cerrando conexion", e2);
                }
            con = null;
        }
    }

    public int borraPolizaAplicada(String nFolioPoliza, String cCentroContable, String cTipoPoliza, String aEjercicioFiscal) throws PolizaException {
        Connection con = null;
        int r = 0;
        try {
            con = getConnection();
            r = PolizaManager.borraPolizaAplicada(con, nFolioPoliza, cCentroContable, cTipoPoliza, aEjercicioFiscal);
            con.commit();
            return r;
        } catch (PolizaException e) {
            try {
                con.rollback();
            } catch (Exception e2) {
                log.error("Error realizando rollback", e2);
            }
            throw e;
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (Exception e2) {
                log.error("Error realizando rollback", e2);
            }
            throw new PolizaException(e);
        } finally {
            if (con != null)
                try {
                    con.close();
                } catch (Exception e2) {
                    log.error("Error cerrando conexion", e2);
                }
            con = null;
        }
    }

    public DocPoliza cargaDocPoliza(long nFolioDocPoliza, String cTipoPoliza, String cCentroContable, String aEjercicioFiscal) throws PolizaException {
        DocPoliza docPoliza = null;
        Connection con = null;
        try {
            con = getConnection();
            DocPolizaEncabezado encabezado = PolizaManager.cargaDocPolizaEncabezado(con, nFolioDocPoliza, cTipoPoliza, cCentroContable, aEjercicioFiscal);
            List<DocPolizaDetalle> detalle = PolizaManager.cargaDocPolizaDetalle(con, nFolioDocPoliza, cCentroContable, cTipoPoliza, aEjercicioFiscal);
            docPoliza = new DocPoliza(encabezado, detalle);
            return docPoliza;
        } catch (Exception e) {
            throw new PolizaException(e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    log.warn("Error cerrando conexion a DB", e);
                }
            }
        }
    }

    public DocPolizaEncabezado cargaDocPolizaEncabezado(long nFolioDocPoliza, String cTipoPoliza, String cCentroContable, String aEjercicioFiscal) throws PolizaException {
        Connection con = null;
        DocPolizaEncabezado docPolizaEncabezado = null;
        try {
            con = getConnection();
            docPolizaEncabezado = PolizaManager.cargaDocPolizaEncabezado(con, nFolioDocPoliza, cTipoPoliza, cCentroContable, aEjercicioFiscal);
            return docPolizaEncabezado;
        } catch (Exception e) {
            throw new PolizaException(e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    log.warn("Error cerrando conexion a BD", e);
                }
            }
        }
    }

    public Poliza cargaPolizaAplicada(String nFolioPoliza, String cCentroContable, String cTipoPoliza, String aEjercicioFiscal) throws PolizaException {
        Connection con = null;
        Poliza poliza = null;
        try {
            con = getConnection();
            poliza = PolizaManager.cargaPolizaAplicada(con, nFolioPoliza, cCentroContable, cTipoPoliza, aEjercicioFiscal);
            con.commit();
            return poliza;
        } catch (PolizaException pe) {
            if (con != null)
                try {
                    con.rollback();
                } catch (Exception e) {
                    log.warn("Error haciendo rollback", e);
                }
            throw pe;
        } catch (Exception ex) {
            if (con != null)
                try {
                    con.rollback();
                } catch (Exception e) {
                    log.warn("Error haciendo rollback", e);
                }
            throw new PolizaException(ex);
        } finally {
            try {
                con.close();
            } catch (Exception e) {
                log.warn("Error cerrando conexion.", e);
            }
        }
    }

    public void generaMovimientosPoliza(Poliza poliza, DocPoliza docPoliza) {
        long nFolioPoliza = poliza.getEncabezado().getnFolioPoliza();
        for (Iterator<DocPolizaDetalle> i = docPoliza.getDetalle().iterator(); i.hasNext(); ) {
            DocPolizaDetalle detalle = i.next();
            Movimiento movimiento = new Movimiento();
            movimiento.setNfoliopoliza(nFolioPoliza);
            movimiento.setNdocrenglon(detalle.getnDocRenglon());
            movimiento.setNcuenta(detalle.getnCuenta());
            movimiento.setNsubcuenta(detalle.getnSubCuenta());
            movimiento.setnTipoAjuste(detalle.getnTipoAjuste());
            movimiento.setPeriodo13(detalle.getPeriodo13());
            movimiento.setADEFAS(docPoliza.getEncabezado().getADEFAS());
            movimiento.setParcial(detalle.getParcial());
            if ("CARGO".equals(detalle.getcEvento()))
                movimiento.setCtipomovimiento("C");
            else
                movimiento.setCtipomovimiento("A");
            movimiento.setMmovimiento(detalle.getmImporte());
            movimiento.setCdescripcionmovpol(detalle.getcConcepto());
            movimiento.setCcentrocontable(detalle.getcCentroContable());
            movimiento.setCtipodocumento("DOCPOLIZA");
            movimiento.setCramo(docPoliza.getEncabezado().getcRamo());
            movimiento.setFoperacionmovimiento(new Date(System.currentTimeMillis()));
            movimiento.setCfoliodocumentomovimiento(docPoliza.getEncabezado().getnFolioDocPoliza());
            movimiento.setFmovimiento(docPoliza.getEncabezado().getfAplicacion());
            movimiento.setDconceptomovimiento(detalle.getcConcepto());
            movimiento.setAejerciciofiscal(detalle.getaEjercicioFiscal());
            movimiento.setCtipopoliza(detalle.getcTipoPoliza());
            movimiento.setCunidadresponsable(docPoliza.getEncabezado().getcUnidadResponsable());
            poliza.detalle.add(movimiento);
        }
    }

    public void reEditaPoliza(String nFolioDocumento, String nFolioPoliza, String cTipoDocumento, String cCentroContable, String cTipoPoliza, String aEjercicioFiscal) throws PolizaException {
        Connection con = null;
        try {
            con = getConnection();
            Poliza p = cargaPolizaAplicada(nFolioPoliza, cCentroContable, cTipoPoliza, aEjercicioFiscal);
            DocPolizaEncabezado dpe = cargaDocPolizaEncabezado(p.getEncabezado().getnFolioDocumento(), p.getEncabezado().getcTipoPoliza(), p.getEncabezado().getcCentroContable(), p.getEncabezado().getaEjercicioFiscal());
            p.getEncabezado().setDocHAplicado("E");
            p.getEncabezado().setcDocumentoHAplicado("N");
            dpe.setnCambio(10);
            dpe.setcDocumentoHaplicado("E");
            CasoOperacion co = new CasoOperacion();
            co.setIdTC(13);
            co.setIdCaso((int) dpe.getnIdCasoOrigen());
            co.setIdOperacion(4);
            List<?> l = CasoOperacionManager.select(con, co);
            if (l == null || l.size() <= 0)
                throw new PolizaException("No se encontro operacion para el caso [" + dpe.getnIdCasoOrigen() + "] No es posible revivir el caso.");
            else if (l.size() > 1)
                throw new PolizaException("Se encontro mas de una operacion para el caso [" + dpe.getnIdCasoOrigen() + "] No es posible revivir el caso.");
            co = (CasoOperacion) l.get(0);
            log.info("Object: {}", co.getResponsable());
            co.setIdOperacion(1);
            co.setResponsable("CAPTURA_POLIZA");
            CasoOperacionManager.update(con, co);
            PolizaManager.updateDocPolizaEncabezado(con, dpe);
            PolizaManager.updateEncabezadoPolizaAplicada(con, p.getEncabezado());
            /*PolizaManager.borraDetallePolizaAplicada(con, nFolioPoliza,
					cCentroContable, cTipoPoliza, aEjercicioFiscal);*/
            con.commit();
        } catch (Exception e) {
            try {
                if (con != null)
                    con.rollback();
            } catch (Exception e2) {
                log.warn("Error realizando rollback", e2);
            }
            throw new PolizaException(e);
        } finally {
            if (con != null)
                try {
                    con.close();
                } catch (Exception e2) {
                    log.warn("Error cerrando conexion a DB.", e2);
                }
            con = null;
        }
    }

    public void terminaEditaPoliza(long nFolioDocPoliza, String cCentroContable, String cTipoPoliza, String aEjercicioFiscal) throws PolizaException {
        Connection con = null;
        try {
            /* Primero se carga el doc poliza con todos los cambios realizados. */
            con = getConnection();
            DocPoliza docPoliza = PolizaManager.cargaDocPoliza(con, nFolioDocPoliza, cCentroContable, cTipoPoliza, aEjercicioFiscal);
            /* Carga la poliza que fue editada */
            Poliza poliza = PolizaManager.cargaPolizaAplicada(con, String.valueOf(docPoliza.getEncabezado().getnFolioPoliza()), docPoliza.getEncabezado().getcCentroContable(), docPoliza.getEncabezado().getcTipoPoliza(), docPoliza.getEncabezado().getaEjercicioFiscal());
            /* Actualiza la poliza con los nuevos valores */
            poliza.getEncabezado().setDocHAplicado("A");
            poliza.getEncabezado().setfAplicacion(docPoliza.getEncabezado().getfAplicacion());
            poliza.getEncabezado().setmTotalAbono(docPoliza.getEncabezado().getmTotalAbonos());
            poliza.getEncabezado().setmTotalCargo(docPoliza.getEncabezado().getmTotalCargos());
            poliza.getEncabezado().setdConceptoMovimiento(docPoliza.getEncabezado().getcConcepto());
            poliza.getEncabezado().setcDescripcionPoliza(docPoliza.getEncabezado().getcConcepto());
            poliza.getEncabezado().setcDescripcionMovPol(docPoliza.getEncabezado().getcDescripcionPoliza());
            // poliza.getEncabezado().setReferencia(docPoliza.getEncabezado().getReferencia());
            poliza.setDetalle(new ArrayList<Movimiento>());
            generaMovimientosPoliza(poliza, docPoliza);
            PolizaManager.updateEncabezadoPolizaAplicada(con, poliza.getEncabezado());
            PolizaManager.borraDetallePolizaAplicada(con, String.valueOf(poliza.getEncabezado().getnFolioPoliza()), cCentroContable, cTipoPoliza, aEjercicioFiscal);
            PolizaManager.insertTMovimiento(con, poliza.getDetalle());
            docPoliza.getEncabezado().setcDocumentoHaplicado("S");
            PolizaManager.updateDocPolizaEncabezado(con, docPoliza.getEncabezado());
            con.commit();
        } catch (Exception e) {
            try {
                if (con != null)
                    con.rollback();
            } catch (Exception e2) {
                log.warn("Error realizando rollback", e2);
            }
            throw new PolizaException(e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    log.warn("Error cerrando la base de datos", e);
                }
            }
            con = null;
        }
    }

    public int cambiaEstatusPoliza(String nFolioPoliza, String cCentroContable, String cTipoPoliza, String aEjercicioFiscal, String docHAplicado) throws PolizaException {
        Connection con = null;
        int r = 0;
        try {
            con = getConnection();
            Poliza p = PolizaManager.cargaPolizaAplicada(con, nFolioPoliza, cCentroContable, cTipoPoliza, aEjercicioFiscal);
            DocPoliza dp = PolizaManager.cargaDocPoliza(con, p.getEncabezado().getnFolioDocumento(), cCentroContable, cTipoPoliza, aEjercicioFiscal);
            if ("S".equals(docHAplicado)) {
                p.getEncabezado().setDocHAplicado("A");
                dp.getEncabezado().setcDocumentoHaplicado(docHAplicado);
            } else {
                p.getEncabezado().setDocHAplicado(docHAplicado);
                dp.getEncabezado().setcDocumentoHaplicado(docHAplicado);
            }
            r = PolizaManager.updateEncabezadoPolizaAplicada(con, p.getEncabezado());
            r += PolizaManager.updateDocPolizaEncabezado(con, dp.getEncabezado());
            con.commit();
            return r;
        } catch (Exception e) {
            if (con != null)
                try {
                    con.rollback();
                } catch (Exception e2) {
                    log.warn("Error haciendo Rollback", e2);
                }
            throw new PolizaException(e);
        } finally {
            if (con != null)
                try {
                    con.close();
                } catch (Exception e2) {
                    log.warn("Error cerrando base de datos", e2);
                }
            con = null;
        }
    }

    public void actualizaMovimientosPoliza(long nFolioDocPoliza, String cCentroContable, String cTipoPoliza, String aEjercicioFiscal) throws PolizaException {
        Connection con = null;
        try {
            /* Primero se carga el doc poliza con todos los cambios realizados. */
            con = getConnection();
            DocPoliza docPoliza = PolizaManager.cargaDocPoliza(con, nFolioDocPoliza, cCentroContable, cTipoPoliza, aEjercicioFiscal);
            /* Carga la poliza que fue editada */
            Poliza poliza = PolizaManager.cargaPolizaAplicada(con, String.valueOf(docPoliza.getEncabezado().getnFolioPoliza()), docPoliza.getEncabezado().getcCentroContable(), docPoliza.getEncabezado().getcTipoPoliza(), docPoliza.getEncabezado().getaEjercicioFiscal());
            poliza.setDetalle(new ArrayList<Movimiento>());
            generaMovimientosPoliza(poliza, docPoliza);
            PolizaManager.borraDetallePolizaAplicada(con, String.valueOf(poliza.getEncabezado().getnFolioPoliza()), cCentroContable, cTipoPoliza, aEjercicioFiscal);
            PolizaManager.insertTMovimiento(con, poliza.getDetalle());
            con.commit();
        } catch (Exception e) {
            try {
                if (con != null)
                    con.rollback();
            } catch (Exception e2) {
                log.warn("Error realizando rollback", e2);
            }
            throw new PolizaException(e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    log.warn("Error cerrando la base de datos", e);
                }
            }
            con = null;
        }
    }
}
