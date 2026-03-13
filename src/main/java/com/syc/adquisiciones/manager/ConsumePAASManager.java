package com.syc.adquisiciones.manager;

import com.syc.adquisiciones.core.DatosPAAS;
import com.syc.adquisiciones.core.DatosPagoDirectoPAAS;
import com.syc.adquisiciones.core.Respuesta;
import com.syc.adquisiciones.util.Util;
import com.syc.gestion.core.Usuario;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumePAASManager {

    private static Logger log = LoggerFactory.getLogger(ConsumePAASManager.class);

    public boolean guardaBitacora(Usuario usuario) {
        return false;
    }

    public synchronized boolean insertEncabezadoPagoDeirectoPAAS(Connection conn, DatosPagoDirectoPAAS datos) throws Exception {
        String query = "";
        Statement stmEnc = null;
        boolean resp = false;
        try {
            stmEnc = conn.createStatement();
            if (datos.getnCantidadTotal() > 0) {
                query = " INSERT INTO dbo.tPagoDirectoPAAS( cEjercicio ,cIdUnidadEjecutora ,nFolioPagoDirecto ,nLinea ,cIdCABM ,  cIdSubPartida ,cDescripcionAdicional ,nCantidad ,mPrecioUnitario ,nIdIVA ,cIdEstadoLinea ,  mMontoNeto ,fFechaInicio ,fFechaFin ,cIdAlmacenEntrega ,nIdCategoria ,nIdFundamentoLeg) VALUES  ( '" + datos.getcEjercicio() + "' ,'" + datos.getcIdUnidadEjecutora() + "' ," + " " + datos.getnFolioPago() + " ,isnull((select max(nLinea)+1 from tPagoDirectoPAAS with(Nolock) where cEjercicio='" + datos.getcEjercicio() + "' and cIdUnidadEjecutora='" + datos.getcIdUnidadEjecutora() + "' and nFolioPagoDirecto=" + datos.getnFolioPago() + "),1) ,'" + datos.getcCabm() + "' ," + "'" + datos.getcPartida() + "' ,NULL ," + datos.getnCantidadTotal() + " ," + datos.getmPUpromedio() + "," + datos.getnIdIVA() + ",'D' ," + "round((" + datos.getnCantidadTotal() + "*" + datos.getmPUpromedio() + "*(1+(0.01*" + datos.getnValorIVA() + "))),2) ,convert(date,'" + datos.getfFechaInicio() + "') ," + "convert(date,'" + datos.getfFechaFin() + "') ,'" + datos.getcAlmacenEntrega() + "' ," + datos.getnTipoAdjudicacion() + " ," + datos.getnFundamentoLegal() + " )";
                log.info("query: " + query);
                if (stmEnc.executeUpdate(query) == 1)
                    resp = true;
            }
        } finally {
            if (stmEnc != null)
                stmEnc.close();
            stmEnc = null;
        }
        return resp;
    }

    public synchronized boolean actualizaPagoDeirectoPAAS(Connection conn, DatosPagoDirectoPAAS datos, ArrayList<List<String>> tabla) throws Exception {
        String query = "";
        boolean resp = false;
        List<String> fila = new ArrayList<>();
        Iterator<List<String>> itr = tabla.iterator();
        while (itr.hasNext()) {
            fila = itr.next();
            query = "UPDATE dbo.tPagoDirectoPAAS SET cDescripcionAdicional='" + (String) fila.get(2) + "',mPrecioUnitario=" + (String) fila.get(4) + ",nIdIVA=" + (String) fila.get(5) + ",mMontoNeto=(CASE WHEN ABS(" + (String) fila.get(6) + "-(ROUND((nCantidad*" + (String) fila.get(4) + "*(1+(0.01*ISNULL((SELECT VALOR FROM dbo.mCatalogoTipoIVA WITH(NOLOCK) WHERE IDIVA=" + (String) fila.get(5) + "),0)))),2)))>0.5 " + "THEN (ROUND((nCantidad*" + (String) fila.get(4) + "*(1+(0.01*ISNULL((SELECT VALOR FROM dbo.mCatalogoTipoIVA WITH(NOLOCK) WHERE IDIVA=" + (String) fila.get(5) + "),0)))),2)) ELSE " + (String) fila.get(6) + " END) " + ",fFechaInicio=CONVERT(DATE,'" + datos.getfFechaInicio() + "') " + ",fFechaFin=CONVERT(DATE,'" + datos.getfFechaFin() + "'),cIdAlmacenEntrega='" + datos.getcAlmacenEntrega() + "' WHERE cEjercicio='" + datos.getcEjercicio() + "' AND cIdUnidadEjecutora='" + datos.getcIdUnidadEjecutora() + "' AND nFolioPagoDirecto=" + datos.getnFolioPago() + " AND nLinea=" + (String) fila.get(0);
            log.trace("ConsumePAASManager.actualizaPagoDeirectoPAAS Ejecutando:  " + query);
            if (!execQuery(conn, query)) {
                resp = false;
                break;
            }
            query = "UPDATE tPagoDirectoPAASDetalle SET mPrecioUnitario=" + (String) fila.get(4) + " WHERE cEjercicio='" + datos.getcEjercicio() + "' AND cIdUnidadEjecutora='" + datos.getcIdUnidadEjecutora() + "' AND nFolioPagoDirecto=" + datos.getnFolioPago() + " AND nLinea=" + (String) fila.get(0);
            if (!execQuery(conn, query)) {
                resp = false;
                break;
            }
            resp = true;
        }
        return resp;
    }

    public synchronized Respuesta insertDetallePagoDeirectoPAAS(Connection conn, DatosPagoDirectoPAAS datos, ArrayList<List<String>> tabla) throws Exception {
        String query = "";
        Respuesta respuesta = new Respuesta();
        List<String> fila = new ArrayList<String>();
        Iterator<List<String>> itr = tabla.iterator();
        int cantidadTotal = 0;
        double subtotal = 0.0D;
        int valorIVA = 0;
        int idIVA = 0;
        int cantidad = 0;
        int cantidadDisponibilidad = 0;
        while (itr.hasNext()) {
            fila = itr.next();
            cantidad = Integer.parseInt(fila.get(4));
            if (cantidad > 0) {
                cantidadTotal += cantidad;
                subtotal = Double.parseDouble(fila.get(3)) * Integer.parseInt(fila.get(4)) + subtotal;
                cantidadDisponibilidad = Util.obtieneFolio(conn, "SELECT nCantidadDisponibilidad FROM fn_mProgramaAnualDetalleDisponibles('" + datos.getcEjercicio() + "','" + datos.getcIdUnidadEjecutora() + "','" + datos.getcCabm() + "'," + (String) fila.get(0) + "," + (String) fila.get(0) + ",'" + datos.getcPartida() + "')");
                if (cantidadDisponibilidad >= cantidad) {
                    query = "INSERT INTO dbo.tPagoDirectoPAASDetalle( cEjercicio ,cIdUnidadEjecutora ,nFolioPagoDirecto ,nLinea,nIdPeriodo,nCantidad,mPrecioUnitario )   VALUES  ( '" + datos.getcEjercicio() + "' ,'" + datos.getcIdUnidadEjecutora() + "' ," + datos.getnFolioPago() + " ,isnull((select max(nLinea)+1 from tPagoDirectoPAAS with(Nolock) where cEjercicio='" + datos.getcEjercicio() + "' and cIdUnidadEjecutora='" + datos.getcIdUnidadEjecutora() + "' and nFolioPagoDirecto=" + datos.getnFolioPago() + "),1) ," + (String) fila.get(0) + " ," + (String) fila.get(4) + " ," + (String) fila.get(3) + ")";
                    valorIVA = Integer.parseInt(fila.get(2));
                    if (!execQuery(conn, query)) {
                        respuesta.setMsg("No se agrego el detalle.");
                        respuesta.setResp(false);
                        break;
                    }
                    respuesta.setResp(true);
                    respuesta.setMsg("Detalle Guardado.");
                    continue;
                }
                respuesta.setMsg("No hay disponibilidad en el PAAS para el cucop " + datos.getcCabm() + " de la unidad ejecutora " + datos.getcIdUnidadEjecutora() + " en el mes " + (String) fila.get(0));
                respuesta.setResp(false);
                log.warn("No hay disponibilidad en el PAAS para el cucop " + datos.getcCabm() + " de la unidad ejecutora " + datos.getcIdUnidadEjecutora() + " en el mes " + (String) fila.get(0));
                break;
            }
            respuesta.setResp(false);
            respuesta.setMsg("No se pueden agregar lineas con cantidades menores o iguales a 0.");
            log.warn("No se pueden agregar lineas con cantidades menores o iguales a 0.");
        }
        datos.setnCantidadTotal(cantidadTotal);
        if (cantidadTotal > 0) {
            datos.setmPUpromedio(subtotal / cantidadTotal);
            query = "SELECT * FROM dbo.mCatalogoTipoIVA WITH(NOLOCK) WHERE VALOR=" + valorIVA;
            idIVA = Util.obtieneFolio(conn, query);
            datos.setnIdIVA(idIVA);
            datos.setnValorIVA(valorIVA);
        }
        return respuesta;
    }

    public boolean execQuery(Connection conn, String query) throws SQLException {
        boolean resp = false;
        Statement stmEnc = null;
        int n = 0;
        try {
            log.info(query);
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

    public static BigDecimal montoMaximoTipoPago(Connection conn, String tipoPago) throws Exception {
        String query = "SELECT cant_salario * salario AS montoMaximo ";
        query = String.valueOf(query) + "FROM   mcatsalario WITH(nolock)";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if ("PAGODIRECTO".equalsIgnoreCase(tipoPago)) {
                ps = conn.prepareStatement(query);
                rs = ps.executeQuery();
                if (rs.next())
                    return rs.getBigDecimal(1);
                throw new Exception("No se ha definido monto maximo para pago: " + tipoPago);
            }
            throw new Exception("No se ha definido monto maximo para pago: " + tipoPago);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static BigDecimal getTotalCapturado(Connection conn, int folio) throws Exception {
        String query = "";
        query = String.valueOf(query) + "SELECT\tIsnull(Sum(mmontoneto), 0) AS mMontoNeto ";
        query = String.valueOf(query) + "  FROM\ttpagodirectopaas WITH(NOLOCK) ";
        query = String.valueOf(query) + " WHERE\tnfoliopagodirecto = ?  ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, folio);
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getBigDecimal(1);
            return new BigDecimal(0.0D);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public boolean hayCUCOPEnSolicitudes(Connection conn, String cEjercicio, String cIdUnidadEjecutora, String cCucop, String cPartida) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            query = "SELECT * FROM fn_mProgramaAnualDetalleCantidadEnSolicitudes() WHERE cEjercicio =? AND cIdUnidadEjecutora =? AND cIdSubPartida=?  AND cIdCABM =? AND nCantidadEnSolicitudes >0";
            log.info(query);
            log.info("cEjercicio: " + cEjercicio);
            log.info("cIdUnidadEjecutora: " + cIdUnidadEjecutora);
            log.info("cPartida: " + cPartida);
            log.info("cCucop: " + cCucop);
            ps = conn.prepareStatement(query);
            ps.setString(1, cEjercicio);
            ps.setString(2, cIdUnidadEjecutora);
            ps.setString(3, cPartida);
            ps.setString(4, cCucop);
            rs = ps.executeQuery();
            if (rs.next())
                return true;
            return false;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public boolean deleteCUCOPDetallePerioro(Connection conn, String cEjercicio, String cIdUnidadEjecutora, String cCucop, String cPartida) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        int row = -1;
        boolean resp = false;
        try {
            query = "DELETE FROM mProgramaAnualDetallePeriodo WHERE cEjercicio ='" + cEjercicio + "' AND cIdUnidadEjecutora ='" + cIdUnidadEjecutora + "' AND cIdCABM ='" + cCucop + "' AND cIdSubPartida ='" + cPartida + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            row = ps.executeUpdate();
            if (row > 0)
                resp = true;
        } finally {
            query = null;
            CloseObject.closeObject(ps);
        }
        return resp;
    }

    public boolean deleteCUCOPDetallePerioroCapMil(Connection conn, DatosPAAS dat) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        int row = -1;
        boolean resp = false;
        try {
            query = "DELETE FROM mProgramaAnualDetallePeriodoCap1000 WHERE cEjercicio ='" + dat.getcEjercicio() + "' AND cIdUnidadEjecutora ='" + dat.getcUnidadEjecutora() + "' AND cIdCABM ='" + dat.getcCucopEliminar() + "' AND cIdSubPartida ='" + dat.getcPartidaEliminar() + "' and cIdSubPartidaCap1000='" + dat.getcPartidaCapMilEliminar() + "'";
            ps = conn.prepareStatement(query);
            row = ps.executeUpdate();
            if (row > 0)
                resp = true;
        } finally {
            query = null;
            CloseObject.closeObject(ps);
        }
        return resp;
    }

    public boolean deleteCUCOPDetalle(Connection conn, String cEjercicio, String cIdUnidadEjecutora, String cCucop, String cPartida) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        int row = -1;
        boolean resp = false;
        try {
            query = "DELETE FROM mProgramaAnualDetalle WHERE cEjercicio ='" + cEjercicio + "' AND cIdUnidadEjecutora ='" + cIdUnidadEjecutora + "' AND cIdCABM ='" + cCucop + "' AND cIdSubPartida ='" + cPartida + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            row = ps.executeUpdate();
            if (row > 0)
                resp = true;
        } finally {
            query = null;
            CloseObject.closeObject(ps);
        }
        return resp;
    }

    public boolean deleteCUCOPDetalleCapMil(Connection conn, DatosPAAS dat) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        int row = -1;
        boolean resp = false;
        try {
            query = "DELETE FROM mProgramaAnualDetalleCap1000 WHERE cEjercicio ='" + dat.getcEjercicio() + "' AND cIdUnidadEjecutora ='" + dat.getcUnidadEjecutora() + "' AND cIdCABM ='" + dat.getcCucopEliminar() + "' AND cIdSubPartida ='" + dat.getcPartidaEliminar() + "' and cIdSubPartidaCap1000='" + dat.getcPartidaCapMilEliminar() + "'";
            ps = conn.prepareStatement(query);
            row = ps.executeUpdate();
            if (row > 0)
                resp = true;
        } finally {
            query = null;
            CloseObject.closeObject(ps);
        }
        return resp;
    }

    public boolean existePAASCapMil(Connection conn, DatosPAAS dat) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            query = "SELECT * FROM  mProgramaAnualCap1000 pa with(nolock) WHERE pa.cEjercicio=?\tand pa.cidunidadejecutora=? and cIdEntidadContable=?";
            ps = conn.prepareStatement(query);
            ps.setString(1, dat.getcEjercicio());
            ps.setString(2, dat.getcUnidadEjecutora());
            ps.setString(3, dat.getcCentroContable());
            rs = ps.executeQuery();
            if (rs.next())
                return true;
            return false;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public boolean addPAASCapMil(Connection conn, DatosPAAS dat) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        int row = -1;
        boolean resp = false;
        try {
            query = "INSERT INTO mProgramaAnualCap1000  (cEjercicio,cIdUnidadEjecutora,cIdEntidadContable , fCreacion , cIdUsuarioCrea  ) VALUES( ?,?,? ,getdate() ,?)";
            ps = conn.prepareStatement(query);
            ps.setString(1, dat.getcEjercicio());
            ps.setString(2, dat.getcUnidadEjecutora());
            ps.setString(3, dat.getcCentroContable());
            ps.setString(4, dat.getcLogin());
            row = ps.executeUpdate();
            if (row > 0)
                resp = true;
        } finally {
            query = null;
            CloseObject.closeObject(ps);
        }
        return resp;
    }

    public boolean validarPresupuestoPAAS(Connection conn) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            query = "select * from mSistema WITH(NOLOCK) where cParametro='lRequiereTechoPresupuestal' and cvalor='TRUE'";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next())
                return true;
            return false;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public double[] montoCapturadoPorPartidaCapMil(Connection conn, DatosPAAS dat) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        double[] montoCap = new double[2];
        try {
            query = "select sum(mImporteBruto)mImporteBruto,sum(mImporteNeto)mImporteNeto from vCucopsGridBDCapMil WITH(NOLOCK) where  cIdSubPartidaCap1000=? and cIdUnidadEjecutora=? ";
            ps = conn.prepareStatement(query);
            ps.setString(1, dat.getcPartidaCapMil());
            ps.setString(2, dat.getcUnidadEjecutora());
            rs = ps.executeQuery();
            if (rs.next())
                montoCap[0] = rs.getDouble("mImporteBruto");
            montoCap[1] = rs.getDouble("mImporteNeto");
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
        return montoCap;
    }

    public double presupuestoPorPartidaCapMil(Connection conn, DatosPAAS dat) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        double montoPresupuesto = 0.0D;
        try {
            query = "select sum(ds.Modificado) as Modificado from mDataStoreSaldos as ds with (NOLOCK) where ds.cPartida = '" + dat.getcPartidaCapMil() + "' AND ds.cUnidadEjecutora = '" + dat.getcUnidadEjecutora() + "' ";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next())
                montoPresupuesto = rs.getDouble("Modificado");
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
        return montoPresupuesto;
    }

    public double toleranciaPAAS(Connection conn) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        double tolerancia = 0.0D;
        try {
            query = "SELECT cValor as cTolerancia FROM mSistema  WITH(NOLOCK) where cParametro = 'ToleranciaPAPartida' ";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next())
                tolerancia = rs.getDouble("cTolerancia");
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
        return tolerancia;
    }

    public boolean addCalendarioPAASCapMil(Connection conn, ArrayList<List<String>> tabla, DatosPAAS dat) throws Exception {
        List<String> fila = new ArrayList<>();
        Iterator<List<String>> itr = tabla.iterator();
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            while (itr.hasNext()) {
                fila = itr.next();
                if (Integer.parseInt(fila.get(1)) <= 0 && Double.parseDouble(fila.get(2)) <= 0.0D) {
                    continue;
                }
                query = "INSERT INTO mProgramaAnualDetallePeriodoCap1000\r\n  (    cEjercicio ,    cIdUnidadEjecutora ,    cIdCABM ,    cIdSubPartida ,\tcIdSubPartidaCap1000 \r\n\t,    nIdPeriodo ,    nCantidad ,    mPrecioUnitario ,    cIdUsuarioModifica)\r\n  VALUES (    ? ,    ? ,    ? ,    ? , ? ,    ?,    ? ,    ?)";
                ps = conn.prepareStatement(query);
                ps.setString(1, dat.getcEjercicio());
                ps.setString(2, dat.getcUnidadEjecutora());
                ps.setString(3, dat.getcCucopEliminar());
                ps.setString(4, dat.getcPartidaEliminar());
                ps.setString(5, dat.getcPartidaCapMil());
                ps.setInt(6, Integer.parseInt(fila.get(0)));
                ps.setInt(7, Integer.parseInt(fila.get(1)));
                ps.setDouble(8, Double.parseDouble(fila.get(2)));
                ps.setString(9, dat.getcLogin());
                if (ps.executeUpdate() <= 0) {
                    throw new Exception("Error mo se pudo guardar el mes " + (String) fila.get(0) + " cantidad " + (String) fila.get(1) + " PU " + (String) fila.get(2));
                } else {
                    success = true;
                }
            }
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean updateCalendarioPAASCapMil(Connection conn, ArrayList<List<String>> tabla, DatosPAAS dat) throws Exception {
        List<String> fila = new ArrayList<>();
        Iterator<List<String>> itr = tabla.iterator();
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            while (itr.hasNext()) {
                fila = itr.next();
                if (Integer.parseInt(fila.get(1)) <= 0 && Double.parseDouble(fila.get(2)) <= 0.0D) {
                    continue;
                }
                query = "UPDATE mProgramaAnualDetallePeriodoCap1000\r\n    SET nCantidad   = ? ,\r\n      mPrecioUnitario= ? ,\r\n      cIdUsuarioModifica   =?\r\n    WHERE cEjercicio   =?\r\n    AND cIdUnidadEjecutora =? \r\n    AND cIdCABM        =?\r\n    AND cIdSubPartida  =?\r\n\tAND cIdSubPartidaCap1000  =?\r\n    AND nIdPeriodo     =  ?";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Integer.parseInt(fila.get(1)));
                ps.setDouble(2, Double.parseDouble(fila.get(2)));
                ps.setString(3, dat.getcLogin());
                ps.setString(4, dat.getcEjercicio());
                ps.setString(5, dat.getcUnidadEjecutora());
                ps.setString(6, dat.getcCucopEliminar());
                ps.setString(7, dat.getcPartidaEliminar());
                ps.setString(8, dat.getcPartidaCapMil());
                ps.setInt(9, Integer.parseInt(fila.get(0)));
                if (ps.executeUpdate() <= 0) {
                    throw new Exception("Error mo se pudo guardar el mes " + (String) fila.get(0) + " cantidad " + (String) fila.get(1) + " PU " + (String) fila.get(2));
                } else {
                    success = true;
                }
            }
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean updatePAASCapMil(Connection conn, DatosPAAS dat) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "UPDATE mProgramaAnualDetalleCap1000\r\n    SET nPorcentajeIVA=?,cIdProcedencia=?,mPorcentajePyme=?\r\n\t,mPorcentajeNoTratados=?,ePlurianualidad=?,tipoProcedimiento=?\r\n\t,vEstimadoP=?\r\n    WHERE cEjercicio   =?\r\n    AND cIdUnidadEjecutora =? \r\n    AND cIdCABM        =?\r\n    AND cIdSubPartida  =?\r\n\tAND cIdSubPartidaCap1000  =?";
            ps = conn.prepareStatement(query);
            ps.setInt(1, dat.getnIVA());
            ps.setString(2, dat.getcProcedencia());
            ps.setDouble(3, dat.getmMontoDestMiPyme());
            ps.setDouble(4, dat.getmMontoEstimadoComprasNoCubiertas());
            ps.setInt(5, dat.getnCantidaEjercicios());
            ps.setString(6, dat.getcTipoProcedimiento());
            ps.setDouble(7, dat.getmMontoBrutoPlurianual());
            ps.setString(8, dat.getcEjercicio());
            ps.setString(9, dat.getcUnidadEjecutora());
            ps.setString(10, dat.getcCucopEliminar());
            ps.setString(11, dat.getcPartidaEliminar());
            ps.setString(12, dat.getcPartidaCapMil());
            if (ps.executeUpdate() > 0)
                success = true;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean existCalendarioPAASCapMil(Connection conn, DatosPAAS dat) throws Exception {
        String query = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean resp = false;
        try {
            query = "select *from mProgramaAnualDetallePeriodoCap1000 with(NolocK) \r\n WHERE cEjercicio   =?    AND cIdUnidadEjecutora =?     AND cIdCABM        =?\r\n    AND cIdSubPartida  =?\tAND cIdSubPartidaCap1000  =?  ";
            ps = conn.prepareStatement(query);
            ps.setString(1, dat.getcEjercicio());
            ps.setString(2, dat.getcUnidadEjecutora());
            ps.setString(3, dat.getcCucopEliminar());
            ps.setString(4, dat.getcPartidaEliminar());
            ps.setString(5, dat.getcPartidaCapMil());
            rs = ps.executeQuery();
            if (rs.next())
                resp = true;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
        return resp;
    }

    public double[] montosAcalendarizar(ArrayList<List<String>> tabla, DatosPAAS dat) throws Exception {
        List<String> fila = new ArrayList<>();
        Iterator<List<String>> itr = tabla.iterator();
        double[] montos = new double[2];
        try {
            while (itr.hasNext()) {
                fila = itr.next();
                if (Integer.parseInt(fila.get(1)) <= 0 && Double.parseDouble(fila.get(2)) <= 0.0D) {
                    continue;
                }
                //subtotal
                montos[0] += (Integer.parseInt(fila.get(1)) * Double.parseDouble(fila.get(2)));
                //total
                montos[1] += (Integer.parseInt(fila.get(1)) * Double.parseDouble(fila.get(2)) * (1 + (dat.getnIVA() * 0.01)) - Double.parseDouble(fila.get(4)));
            }
        } finally {
            fila.clear();
            itr.remove();
        }
        return montos;
    }
}
