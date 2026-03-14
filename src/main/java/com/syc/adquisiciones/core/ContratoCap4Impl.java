package com.syc.adquisiciones.core;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.jfree.util.Log;
import com.axtel.contratos.ContractStatus;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.adquisiciones.ContratoCap4Interface;
import com.syc.adquisiciones.util.Util;
import com.syc.contable.ContableInterface;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContratoCap4Impl implements ContratoCap4Interface, GestionInterface {

    private static Logger log = LoggerFactory.getLogger(ContratoCap4Impl.class);

    public boolean nuevo(Connection conn, HttpSession session, Usuario usuario, DatosContratoCap4 datosContrato) throws SQLException {
        // TODO Auto-generated method stub
        boolean respuesta = false;
        PreparedStatement pstm = null;
        try {
            int consecutivo = consecutivoContrato(conn, datosContrato);
            String query = "insert into mContratoCap4 (cIdContratoDefinitivo,cEjercicio,cIdTipoContrato,cIdUnidadEjecutora,nIdConsecutivo" + ",nIdTipoActividadEconomica,cIdRFC,cConceptoContrato,nIdEstado,cIdTipoCambio,cIdEntidadContable,cIdUsuarioCreacion" + ",cNoContratoCNET,cNumCuentaDisp,cMotivoCancelacion,nIdCategoria,nIdFundamentoLeg,nEsPluriAnual,mTotalPluriAnual" + ",nEsDescentralizado,nEsAbierto,fCreacion,C_FOLIO,ConsecutivoCDIV,ITieneAnticipo) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,GETDATE(),NULL,NULL,0)";
            if (consecutivo == 0) {
                log.warn("No se genero el siguiente consecutivo de contrato.");
                return respuesta;
            }
            log.info("Object: {}", "Query que genera un nuevo contrato de cap 4000: " + query);
            datosContrato.setcIdcontratoDefinitivo((datosContrato.getnEsPlurianual() == 1 ? "PLU-" : "") + "CF-" + datosContrato.getcIdUnidadEjecutora() + "-" + consecutivo + "/" + datosContrato.getcEjercicio());
            pstm = conn.prepareStatement(query);
            pstm.setString(1, (datosContrato.getnEsPlurianual() == 1 ? "PLU-" : "") + "CF-" + datosContrato.getcIdUnidadEjecutora() + "-" + consecutivo + "/" + datosContrato.getcEjercicio());
            pstm.setString(2, datosContrato.getcEjercicio());
            pstm.setString(3, "CF");
            pstm.setString(4, datosContrato.getcIdUnidadEjecutora());
            pstm.setInt(5, consecutivo);
            pstm.setInt(6, datosContrato.getcActividadEconomica());
            //rfc
            pstm.setString(7, null);
            pstm.setString(8, datosContrato.getcDescripcion());
            pstm.setInt(9, 1);
            pstm.setString(10, "01");
            pstm.setString(11, usuario.getPropiedad("CCENTROCONTABLE").getValor());
            pstm.setString(12, usuario.getLogin());
            pstm.setString(13, datosContrato.getcNoContCNET());
            pstm.setString(14, datosContrato.getcCuentaDisponible());
            pstm.setString(15, null);
            pstm.setInt(16, 1);
            pstm.setInt(17, 2);
            pstm.setInt(18, datosContrato.getnEsPlurianual());
            pstm.setDouble(19, 0.00);
            pstm.setInt(20, 0);
            pstm.setInt(21, 0);
            if (pstm.executeUpdate() == 1) {
                respuesta = true;
                Util.bitacoraMovimientos("CF-" + datosContrato.getcIdUnidadEjecutora() + "-" + consecutivo + "/" + datosContrato.getcEjercicio(), "Creación de contrato", usuario.getLogin(), conn);
                session.setAttribute(ATT_ContratCap4Definitivo, "CF-" + datosContrato.getcIdUnidadEjecutora() + "-" + consecutivo + "/" + datosContrato.getcEjercicio());
            } else {
                Util.bitacoraMovimientos("CF-" + datosContrato.getcIdUnidadEjecutora() + "-" + consecutivo + "/" + datosContrato.getcEjercicio(), "Error al crear el contrato", usuario.getLogin(), conn);
            }
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
        }
        return respuesta;
    }

    public boolean guardaContrato(Connection conn, Usuario usuario, DatosContratoCap4 datosContrato) throws SQLException {
        // TODO Auto-generated method stub
        PreparedStatement pstm = null, pstm2 = null;
        boolean resp = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update mContratoCap4 set nIdTipoActividadEconomica=?, cIdRFC=?,cNoContratoCNET=?,cNumCuentaDisp=?,nIdCategoria=?,nIdFundamentoLeg=?,mTotalPluriAnual=?,nEsDescentralizado=?,nEsPluriAnual=?");
            query.append(",cOficioDG=?,cFolioMASCP=?,cNumProcedCNET=?,nCodContratoCNET=?,nCodExpedienteCNET=?");
            query.append(" where cIdContratoDefinitivo=?");
            pstm = conn.prepareStatement(query.toString());
            log.info("Object: {}", query.toString());
            pstm.setInt(1, datosContrato.getcActividadEconomica());
            pstm.setString(2, datosContrato.getcIdRFC());
            pstm.setString(3, datosContrato.getcNoContCNET());
            pstm.setString(4, datosContrato.getcCuentaDisponible());
            pstm.setInt(5, datosContrato.getnIdCategoria());
            pstm.setInt(6, datosContrato.getnIdFundamentoLeg());
            pstm.setDouble(7, datosContrato.getTotalPlurianual());
            pstm.setInt(8, datosContrato.getnEsDescentralizado());
            pstm.setInt(9, datosContrato.getnEsPlurianual());
            pstm.setString(10, "".equals(datosContrato.getcOficioDG()) ? null : datosContrato.getcOficioDG());
            pstm.setString(11, "".equals(datosContrato.getcFolioMASCP()) ? null : datosContrato.getcFolioMASCP());
            pstm.setString(12, datosContrato.getcNoProcedimientoCNET());
            pstm.setString(13, datosContrato.getnCondContratoCNET());
            pstm.setString(14, datosContrato.getnCondExpedienteCNET());
            pstm.setString(15, datosContrato.getcIdcontratoDefinitivo());
            pstm.executeUpdate();
            //Guarda Fechas
            guardaFechas(conn, datosContrato);
            //Actualiza las partidas
            String sql = "update mContratoCap4Partidas set mMontoNetoTotalPluri=0 where cIdContratoDefinitivo=?";
            pstm2 = conn.prepareStatement(sql);
            pstm2.setString(1, datosContrato.getcIdcontratoDefinitivo());
            pstm2.executeUpdate();
            //Guarda en Bitacora
            Util.bitacoraMovimientos(datosContrato.getcIdcontratoDefinitivo(), "Caratula Guardada", usuario.getLogin(), conn);
            resp = true;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            if (pstm2 != null) {
                pstm2.close();
            }
            pstm = null;
            pstm2 = null;
            query = null;
        }
        return resp;
    }

    public boolean guardaFechas(Connection conn, DatosContratoCap4 datosContrato) throws SQLException {
        PreparedStatement pstm = null, pstmDelete = null;
        boolean resp = false;
        ArrayList<FechasContratacion> arrayFechas;
        try {
            //Delete fechas
            String queryDelete = "delete mContratoCap4Fechas where cIdContratoDefinitivo=?";
            log.info("Object: {}", "pstmDelete: " + queryDelete);
            pstmDelete = conn.prepareStatement(queryDelete);
            pstmDelete.setString(1, datosContrato.getcIdcontratoDefinitivo());
            log.info("Object: {}", "Parametro cIdContratoDefinitivo= " + datosContrato.getcIdcontratoDefinitivo());
            pstmDelete.executeUpdate();
            log.info("Se borran Fechas");
            //insert fechas
            String query = "insert into mContratoCap4Fechas values(?,?,CONVERT(date,?))";
            arrayFechas = datosContrato.getArrayFechas();
            int i = 0;
            while (i < arrayFechas.size()) {
                pstm = conn.prepareStatement(query);
                pstm.setInt(1, arrayFechas.get(i).getIdFecha());
                pstm.setString(2, datosContrato.getcIdcontratoDefinitivo());
                pstm.setString(3, arrayFechas.get(i).getValue());
                pstm.executeUpdate();
                log.info("Object: {}", "idFecha= " + arrayFechas.get(i).getIdFecha() + " cIdcontratoDefinitivo= " + datosContrato.getcIdcontratoDefinitivo() + " Fecha=" + arrayFechas.get(i).getValue());
                i++;
            }
            log.info("Se Guardan Fechas");
            resp = true;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            if (pstmDelete != null) {
                pstmDelete.close();
            }
            pstm = null;
            pstmDelete = null;
        }
        return resp;
    }

    public boolean guardaContratoPartidas(Connection conn, Usuario usuario, DatosContratoCap4 datosContrato) throws Exception {
        // TODO Auto-generated method stub
        PreparedStatement pstm = null, pstm2 = null;
        boolean resp = false;
        ArrayList<PartidasContratoCap4> arrayPartidas;
        try {
            String query = "update mContratoCap4Partidas set nCantidadMin=?,nCantidadMax=?,mPrecioUnitario=?,mPrecioUnitarioMax=?	,mMontoNetoLinea=?,mMontoNetoMinimo=?,mMontoNetoMaximo=?,mMontoNetoTotalPluri=? " + ",nIdIVA=?,cDescripcionAdicional=?,cIdUnidadMedida=?  where nIdContratoCap4Partida=? and cIdContratoDefinitivo=?";
            String query2 = "UPDATE mContratoCap4 set nEsAbierto=? where cIdContratoDefinitivo=?";
            log.info("Object: {}", query.toString());
            log.info("Object: {}", query2.toString());
            validaPartidas(datosContrato);
            arrayPartidas = datosContrato.getArrayPartidas();
            int i = 0;
            while (i < arrayPartidas.size()) {
                pstm = conn.prepareStatement(query);
                pstm.setInt(1, arrayPartidas.get(i).getnCantidadMin());
                pstm.setInt(2, arrayPartidas.get(i).getnCantidadMax());
                pstm.setDouble(3, Math.round(arrayPartidas.get(i).getmPrecioU() * 100) / 100.0d);
                pstm.setDouble(4, Math.round(arrayPartidas.get(i).getmPrecioUMax() * 100) / 100.0d);
                pstm.setDouble(5, Math.round(arrayPartidas.get(i).getmMontoNetoLinea() * 100) / 100.0d);
                pstm.setDouble(6, Math.round(arrayPartidas.get(i).getmMontoNetoMin() * 100) / 100.0d);
                pstm.setDouble(7, Math.round(arrayPartidas.get(i).getmMontoNetoMax() * 100) / 100.0d);
                pstm.setDouble(8, Math.round(arrayPartidas.get(i).getmMontoNetoPluri() * 100) / 100.0d);
                pstm.setInt(9, arrayPartidas.get(i).getnIdIVA());
                pstm.setString(10, arrayPartidas.get(i).getcDescripAdi());
                pstm.setString(11, arrayPartidas.get(i).getcIdUnidadMedida());
                pstm.setInt(12, arrayPartidas.get(i).getnIdContatoPartida());
                pstm.setString(13, datosContrato.getcIdcontratoDefinitivo());
                pstm.executeUpdate();
                i++;
            }
            pstm2 = conn.prepareStatement(query2);
            pstm2.setInt(1, datosContrato.getnEsAbierto());
            pstm2.setString(2, datosContrato.getcIdcontratoDefinitivo());
            pstm2.executeUpdate();
            resp = true;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            if (pstm2 != null) {
                pstm2.close();
            }
            pstm = null;
            pstm2 = null;
        }
        return resp;
    }

    public void validaPartidas(DatosContratoCap4 datosContrato) throws Exception {
        //cadenaPartidas+=aData[0]+"#"+$("#cDescripAdi_"+aData[0]).val()+"#"+cantMin+"#"+cantMax+"#"+precioU+"#"+precioUMax+"#"+mMontoNetoLine+"#"mMontoNetoMin+"#"+mMontoNetoMax+"#"+mMontoNetoPluri;
        double montoNetoLineaCalculado = 0;
        double montoNetoMinCalculado = 0;
        double montoNetoMaxCalculado = 0;
        double iva = 0.0;
        ArrayList<PartidasContratoCap4> arrayPartidas;
        arrayPartidas = datosContrato.getArrayPartidas();
        int i = 0;
        while (i < arrayPartidas.size()) {
            if (arrayPartidas.get(i).getnIdIVA() == 2) {
                iva = 16.0;
            } else {
                iva = 0.0;
            }
            montoNetoLineaCalculado = arrayPartidas.get(i).getmPrecioU() * arrayPartidas.get(i).getnCantidadMin() * (1 + (iva * 0.01));
            montoNetoMinCalculado = montoNetoLineaCalculado;
            if (datosContrato.getnEsAbierto() == 1) {
                //CONTRATO ABIERTO
                if (datosContrato.getcActividadEconomica() == 1) {
                    //CONTRATO ABIERTO DE SERVICIOS
                    montoNetoMaxCalculado = arrayPartidas.get(i).getmPrecioUMax() * arrayPartidas.get(i).getnCantidadMin() * (1 + (iva * 0.01));
                    arrayPartidas.get(i).setnCantidadMax(arrayPartidas.get(i).getnCantidadMin());
                } else {
                    arrayPartidas.get(i).setmPrecioUMax(arrayPartidas.get(i).getmPrecioU());
                    montoNetoMaxCalculado = arrayPartidas.get(i).getmPrecioU() * arrayPartidas.get(i).getnCantidadMax() * (1 + (iva * 0.01));
                }
            } else {
                //montoNetoMinCalculado=0;
                montoNetoMaxCalculado = 0;
                //datosContrato.getArrayPartidas().get(i).setmMontoNetoMin(0);
                datosContrato.getArrayPartidas().get(i).setmMontoNetoMax(0);
            }
            //Validación para el ajuste manual de centavos
            if (Math.abs(arrayPartidas.get(i).getmMontoNetoLinea() - montoNetoLineaCalculado) > 0.5 || (arrayPartidas.get(i).getmMontoNetoLinea() == 0 && arrayPartidas.get(i).getmMontoNetoLineaOrig() == 0)) {
                datosContrato.getArrayPartidas().get(i).setmMontoNetoLinea(montoNetoLineaCalculado);
                datosContrato.getArrayPartidas().get(i).setmMontoNetoMin(montoNetoMinCalculado);
            }
            if (Math.abs(arrayPartidas.get(i).getmMontoNetoMax() - montoNetoMaxCalculado) > 0.5 || (arrayPartidas.get(i).getmMontoNetoMax() == 0 && arrayPartidas.get(i).getmMontoNetoMaxOrig() == 0)) {
                datosContrato.getArrayPartidas().get(i).setmMontoNetoMax(montoNetoMaxCalculado);
            }
            i++;
        }
    }

    public boolean apruebaContrato(Connection conn, HttpServletRequest request, Usuario usuario, DatosContratoCap4 datosContrato, String folioGenerator, String jndiName, String prefixPath) throws Exception {
        // TODO Auto-generated method stub
        CallableStatement cmst = null, cmst2 = null;
        int outputValue = -1;
        int folio;
        String folioCaso = null;
        boolean resp = false;
        try {
            cmst = conn.prepareCall("{?= call pa_validaContratoCap4Financiero (?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, datosContrato.getcEjercicio());
            cmst.setString(3, datosContrato.getcIdcontratoDefinitivo());
            log.info("Object: {}", "call pa_validaContratoCap4Financiero (" + datosContrato.getcEjercicio() + "," + datosContrato.getcIdcontratoDefinitivo() + ")");
            cmst.execute();
            outputValue = cmst.getInt(1);
            if (outputValue == 0 || outputValue == 4) {
                folio = -1;
                //No tiene folio previo
                if (outputValue == 0) {
                    //crear el caso
                    Caso c = iniciaCaso(request, "9", folioGenerator, jndiName, usuario);
                    folioCaso = c.getFolio();
                    int indice = folioCaso.lastIndexOf('-') + 1;
                    folio = Integer.parseInt(folioCaso.substring(indice));
                    //se avanza caso para que no se vea la operacion  en el inbox
                    Util.avanzaCaso(request, c, usuario, prefixPath, new String[] { "CONSULTA_CONTRATODIVERSO" }, new String[] { "consulta_contrato" }, jndiName);
                }
                log.info("Object: {}", "exec pa_apruebaContratoCap4 '" + datosContrato.getcEjercicio() + "','" + datosContrato.getcIdcontratoDefinitivo() + "','" + usuario.getLogin() + "','" + folio + "','" + folioCaso + "'");
                cmst2 = conn.prepareCall("{?= call pa_apruebaContratoCap4(?,?,?,?,?)}");
                cmst2.registerOutParameter(1, Types.INTEGER);
                cmst2.setString(2, datosContrato.getcEjercicio());
                cmst2.setString(3, datosContrato.getcIdcontratoDefinitivo());
                cmst2.setString(4, usuario.getLogin());
                cmst2.setString(5, folio + "");
                cmst2.setString(6, folioCaso);
                cmst2.execute();
                outputValue = cmst2.getInt(1);
                if (outputValue == 0) {
                    resp = true;
                }
            }
        } finally {
            if (cmst != null) {
                cmst.close();
            }
            if (cmst2 != null) {
                cmst2.close();
            }
            cmst = null;
            cmst2 = null;
        }
        return resp;
    }

    public boolean precomprometer(Connection conn, HttpServletRequest request, Usuario usuario, DatosContratoCap4 datosContrato, ArrayList<List<String>> tabla, String prefixPath, String jndiName) throws Exception {
        // TODO Auto-generated method stub
        String cEevento = "PRECOM";
        boolean resp = false;
        String[] param = new String[6];
        param[0] = datosContrato.getcIdcontratoDefinitivo();
        param[1] = "N";
        param[2] = Util.obtieneEjercicioFiscalActivo(conn);
        param[3] = datosContrato.getDescripPoliza();
        if (!validaEstatusContratoCap4(conn, datosContrato.getcIdcontratoDefinitivo(), 2)) {
            log.warn("El contrato no se puede precomprometer por que su estatus no lo permite.");
            return resp;
        }
        if (Util.hayPrecompromiso(conn, datosContrato.getcIdcontratoDefinitivo())) {
            log.warn("El contrato ya tiene precompromiso.");
            return resp;
        }
        if (datosContrato.getcCuentaDisponible().equals("82109")) {
            cEevento = "R_" + cEevento;
            param[1] = "S";
        }
        switch(datosContrato.getnEsDescentralizado()) {
            case 0:
                resp = precompromisoCentralizado(conn, request, usuario, param, tabla, jndiName, cEevento, prefixPath);
                //Se cambia de estatus
                if (resp) {
                    Util.updateQuery("update mContratoCap4 set nIdEstado=3,nComprometeMax=" + datosContrato.getnComprometeMax() + " where cIdContratoDefinitivo='" + datosContrato.getcIdcontratoDefinitivo() + "'", conn);
                    log.info("Actualización de estatus del contrato ");
                }
                break;
            case //Descentralizados
            1:
                precompromisoDesCentralizado(conn, request, usuario, param, tabla, jndiName, cEevento, prefixPath);
                break;
            default:
                log.info("Opción desconocida.");
                break;
        }
        return resp;
    }

    public synchronized boolean validaEstatusContratoCap4(Connection conn, String cIdContratoDef, int estatus) throws SQLException {
        boolean resp = false;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            String query = "select *from mContratoCap4 with(Nolock) where cIdContratoDefinitivo=? and nIdEstado=?";
            log.info("Object: {}", query.toString());
            pstm = conn.prepareStatement(query);
            pstm.setString(1, cIdContratoDef);
            pstm.setInt(2, estatus);
            rs = pstm.executeQuery();
            if (rs.next()) {
                resp = true;
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            rs = null;
        }
        return resp;
    }

    public synchronized boolean validaEstatusContratoPluriCap4(Connection conn, String cIdContratoDef, int estatus) throws SQLException {
        boolean resp = false;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            String query = "select *from mContratoPluriCap4 with(Nolock) where cIdContratoDefinitivo=? and nIdEstado=?";
            log.info("Object: {}", query.toString());
            pstm = conn.prepareStatement(query);
            pstm.setString(1, cIdContratoDef);
            pstm.setInt(2, estatus);
            rs = pstm.executeQuery();
            if (rs.next()) {
                resp = true;
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
            rs = null;
        }
        return resp;
    }

    public synchronized boolean precompromisoDesCentralizado(Connection conn, HttpServletRequest request, Usuario usuario, String[] param, ArrayList<List<String>> tabla, String jndiName, String cEevento, String prefixPath) throws ServletException, IOException, SQLException, GestionException {
        try {
        } finally {
        }
        return false;
    }

    public synchronized boolean precompromisoCentralizado(Connection conn, HttpServletRequest request, Usuario usuario, String[] param, ArrayList<List<String>> tabla, String jndiName, String cEevento, String prefixPath) throws ServletException, IOException, SQLException, GestionException {
        Caso caso = null;
        String folio;
        int val = -1;
        boolean resp = false;
        AplicarContableReturn acr = null;
        String validaSaldo = "";
        PreparedStatement pstm = null;
        try {
            ContableInterface conInt = new AplicacionContable();
            caso = Util.generaGuardaCaso(usuario.getU_UR(), (GestionInterface.IDTC_PRECOMPROMISO + ""), param[3], usuario, jndiName, param[2]);
            folio = caso.getFolio().substring(caso.getFolio().lastIndexOf("-") + 1);
            param[4] = folio;
            param[5] = caso.getFolio();
            //Crea Encbezado y detalle
            val = Util.creaEncDetPreCompromiso(usuario, conn, param, tabla, cEevento, false);
            if (val != 0) {
                return resp;
            }
            //Aplicación contable
            Map<String, String> m = CasoDatoManager.readValuesCasoDato(request, caso.getCasoDato(), true);
            acr = conInt.aplicarContableNuevo(conn, caso, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), validaSaldo);
            if (!acr.isSuccess()) {
                return false;
            }
            //Guardaar en bitacora los movimientos
            Util.bitacoraMovimientos(param[0], "Generación de precompromiso para contratos de capitulo 4000 con Folio=" + folio, usuario.getLogin(), conn);
            // Una vez que ha hecho la aplicación contable avanza el caso A CONSULTA
            //VENTANILLA_PRECOMPROMISO
            String[] responsable = new String[] { "CONSULTA_PRECOMPROMISO" };
            //autoriza_precomp
            String[] nombre = new String[] { "consulta_precomp" };
            Util.avanzaCaso(request, caso, usuario, prefixPath, responsable, nombre, jndiName);
            log.debug("Object: {}", caso.getCasoDato("APLICADO_CONT").getValor());
            resp = true;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
        }
        return resp;
    }

    public boolean comprometer(Connection conn, HttpServletRequest request, Usuario usuario, DatosContratoCap4 datosContrato, String jndiName, String prefixPath) throws Exception {
        // TODO Auto-generated method stub
        boolean resp = false;
        PreparedStatement pstm = null;
        try {
            if (!validaEstatusContratoCap4(conn, datosContrato.getcIdcontratoDefinitivo(), 3)) {
                log.warn("El contrato no se puede comprometer por que su estatus no lo permite.");
                return resp;
            }
            if (Util.hayCompromiso(conn, datosContrato.getcIdcontratoDefinitivo())) {
                log.warn("El contrato ya tiene compromiso.");
                return resp;
            }
            //Generar el compromiso
            resp = Util.generaCompromiso(conn, request, usuario, datosContrato, jndiName, prefixPath, false);
            //Se cambia de estatus
            if (resp) {
                pstm = conn.prepareStatement("update mContratoCap4 set nIdEstado=4 where cIdContratoDefinitivo=? ");
                pstm.setString(1, datosContrato.getcIdcontratoDefinitivo());
                pstm.executeUpdate();
                log.info("Actualización de estatus del contrato ");
            }
            resp = true;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
        }
        return resp;
    }

    public int consecutivoContrato(Connection conn, DatosContratoCap4 datosContrato) throws SQLException {
        int consecutivo = 0;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            String query = "select (isnull(MAX(nIdConsecutivo),0)+1) consecutivo from mContratoCap4 with(Nolock) where cIdUnidadEjecutora='" + datosContrato.getcIdUnidadEjecutora() + "'";
            Log.info("Object: {}", "Query para obtener el siguiente consecutivo de contratos cap 400: " + query);
            pstm = conn.prepareStatement(query);
            rs = pstm.executeQuery();
            if (rs.next()) {
                consecutivo = rs.getInt("consecutivo");
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstm != null) {
                pstm.close();
            }
            rs = null;
            pstm = null;
        }
        return consecutivo;
    }

    private synchronized Caso iniciaCaso(HttpServletRequest request, String tCaso, String folioGenerator, String jndiName, Usuario usuario) throws GestionException {
        HttpSession session = request.getSession(false);
        // contrato diverso
        CasoBusinessLogic casoTx = new CasoBusinessLogic(jndiName);
        if (tCaso == null) {
            log.error("Identificador de Tipo de Caso, vacio");
            throw new GestionException("Identificador de Tipo de Caso, vacio");
        }
        int idTC = Integer.parseInt(tCaso);
        if (idTC <= 0) {
            log.error("Identificador de Tipo de Caso, menor o igual a cero (<= 0)");
            throw new GestionException("Identificador de Tipo de Caso, menor o igual a cero (<= 0)");
        }
        Caso c = null;
        FolioGeneratorInterface fg = null;
        try {
            ClassLoader cl = getClass().getClassLoader();
            Class clase = cl.loadClass(folioGenerator);
            fg = (FolioGeneratorInterface) clase.newInstance();
        } catch (ClassNotFoundException exc) {
            log.error("Generador de folios", exc);
            throw new GestionException(exc);
        } catch (InstantiationException exc) {
            log.error("Generador de folios", exc);
            throw new GestionException(exc);
        } catch (IllegalAccessException exc) {
            log.error("Generador de folios", exc);
            throw new GestionException(exc);
        } catch (Exception exc) {
            log.error("algo raro paso", exc);
            exc.printStackTrace();
            throw new GestionException(exc);
        }
        c = casoTx.IniciaCaso(usuario, idTC, fg);
        if (c == null) {
            log.error("No se logro crear el caso");
            throw new GestionException("No se logró crear el caso");
        }
        ITree tree = casoTx.getArbolCaso(c);
        session.setAttribute(ATT_TREE, tree);
        return c;
    }

    public boolean devuelveContrato(Connection conn, Usuario usuario, DatosContratoCap4 datosContrato) throws SQLException {
        // TODO Auto-generated method stub
        CallableStatement cmst = null;
        int outputValue = -1;
        boolean resp = false;
        try {
            log.debug("Object: {}", "pa_devuelveContratoCap4 '" + datosContrato.getcEjercicio() + "','" + datosContrato.getcIdcontratoDefinitivo() + "','" + usuario.getLogin() + "'");
            cmst = conn.prepareCall("{?= call pa_devuelveContratoCap4 (?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, datosContrato.getcEjercicio());
            cmst.setString(3, datosContrato.getcIdcontratoDefinitivo());
            cmst.setString(4, usuario.getLogin());
            cmst.execute();
            outputValue = cmst.getInt(1);
            if (outputValue == 0)
                resp = true;
            System.out.println("Parametro de salida del procedimiento=" + outputValue);
        } finally {
            if (cmst != null) {
                cmst.close();
            }
            cmst = null;
        }
        return resp;
    }

    public boolean devuelvePrecompromiso(Connection conn, HttpServletRequest request, Usuario usuario, DatosContratoCap4 datosContrato, String prefixPath, String jndiName) throws Exception {
        boolean resp = false;
        try {
            //cancela precompromiso
            resp = Util.cancelaPrecompromiso(conn, request, usuario, datosContrato, prefixPath, jndiName);
            //Se cambia de estatus
            String query = "update mContratoCap4 set nIdEstado=2 where cIdContratoDefinitivo='" + datosContrato.getcIdcontratoDefinitivo() + "'";
            if (resp) {
                Util.updateQuery(query, conn);
                log.info("Actualización de estatus del contrato cap4");
            }
        } catch (Exception e) {
            // TODO: handle exception
            log.error("Object: {}", e.getMessage());
            e.printStackTrace();
        }
        return resp;
    }

    public boolean apruebaAmpliacionContrato(Connection conn, Usuario usuario, DatosContratoCap4 datosContrato) throws SQLException {
        // TODO Auto-generated method stub
        boolean resp = false;
        CallableStatement cmst = null;
        int outputValue = -1;
        try {
            log.debug("Inicia la aprobación de la ampliación contrato Cap4");
            cmst = conn.prepareCall("{?= call pa_contratoCap4Ampl (?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, datosContrato.getcEjercicio());
            cmst.setString(3, datosContrato.getcIdcontratoDefinitivo());
            cmst.setInt(4, datosContrato.getnIdAmpliacion());
            cmst.execute();
            outputValue = cmst.getInt(1);
            log.info("Object: {}", "outputValue:" + outputValue);
            if (outputValue == 0) {
                resp = true;
                log.info("Contrato cap4 aprobado correctamente.");
            } else if (outputValue == 1) {
                log.warn("Tipo documento Contrato Cap4. Error al obtener los montos y fechas.");
            } else if (outputValue == 2) {
                log.warn("Tipo documento Contrato Cap4. Error al insertar EP en la tabla tContratoEP_TMP.");
            } else if (outputValue == 3) {
                log.warn("Tipo documento Contrato Cap4. Error al insertar en la tabla pContratoDiversoConvenio.");
            } else if (outputValue == 4) {
                log.warn("Tipo documento Contrato Cap4. Error al insertar en la tabla tContratoEP.");
            }
        } finally {
            if (cmst != null) {
                cmst.close();
            }
            cmst = null;
        }
        return resp;
    }

    public boolean devuelveAmpliacion(Connection conn, Usuario usuario, DatosContratoCap4 datosContrato) throws Exception {
        // TODO Auto-generated method stub
        boolean resp = false;
        try {
            //Delete the contrato
            String queryDelete = "delete pContratoDiversoConvenio where cIdModificacion='" + datosContrato.getcIdcontratoDefinitivo() + "'";
            Util.updateQuery(queryDelete, conn);
            //update the state the contrato
            String queryUpdate = "update mContratoCap4Ampliacion set nIdEstado=1, C_FOLIO_PRE=NULL, ConsecutivoPRECOMP=NULL where cIdContratoDefinitivo+'-AMP-'+CONVERT(VARCHAR,nIdConsecutivoAmpliacion)='" + datosContrato.getcIdcontratoDefinitivo() + "'";
            Util.updateQuery(queryUpdate, conn);
            //Bitacora
            Util.bitacoraMovimientos(datosContrato.getcIdcontratoDefinitivo(), "Se devuelve la ampliación", usuario.getLogin(), conn);
            resp = true;
        } catch (Exception e) {
            // TODO: handle exception
            log.error("Object: {}", e.getMessage());
        }
        return resp;
    }

    public boolean precomprometeAmpliacion(Connection conn, HttpServletRequest request, ArrayList<List<String>> tabla, Usuario usuario, DatosContratoCap4 datosContrato, String jndiName, String prefixPath) throws Exception {
        // TODO Auto-generated method stub
        boolean resp = false;
        String[] param = new String[6];
        param[0] = datosContrato.getcIdcontratoDefinitivo();
        param[1] = "N";
        param[2] = datosContrato.getcEjercicio();
        param[3] = datosContrato.getDescripPoliza();
        String cEevento = "PRECOM";
        if (datosContrato.getcCuentaDisponible().equals("82109")) {
            cEevento = "R_" + cEevento;
            param[1] = "S";
        }
        //Precompromiso centralizado
        resp = precompromisoCentralizado(conn, request, usuario, param, tabla, jndiName, cEevento, prefixPath);
        //Se cambia de estatus
        String query = "update mContratoCap4Ampliacion set nIdEstado=3 where cIdContratoDefinitivo+'-AMP-'+CONVERT(varchar,nIdConsecutivoAmpliacion)='" + datosContrato.getcIdcontratoDefinitivo() + "' and nIdConsecutivoAmpliacion=" + datosContrato.getnIdAmpliacion();
        if (resp) {
            Util.updateQuery(query, conn);
            log.info("Cambia el estatus");
        }
        return resp;
    }

    public boolean devuelvePrecompromisoAmpliacion(Connection conn, HttpServletRequest request, Usuario usuario, DatosContratoCap4 datosContrato, String jndiName, String prefixPath) throws Exception {
        // TODO Auto-generated method stub
        boolean resp = false;
        resp = Util.cancelaPrecompromiso(conn, request, usuario, datosContrato, prefixPath, jndiName);
        //Se cambia de estatus
        String query = "update mContratoCap4Ampliacion set nIdEstado=2 where cIdContratoDefinitivo+'-AMP-'+CONVERT(varchar,nIdConsecutivoAmpliacion)='" + datosContrato.getcIdcontratoDefinitivo() + "' and nIdConsecutivoAmpliacion=" + datosContrato.getnIdAmpliacion();
        if (resp) {
            Util.updateQuery(query, conn);
            log.info("Cambia el estatus");
        }
        return resp;
    }

    public boolean comprometeAmpliacion(Connection conn, HttpServletRequest request, Usuario usuario, DatosContratoCap4 datosContrato, String jndiName, String prefixPath) throws Exception {
        // TODO Auto-generated method stub
        boolean resp = false;
        //Generar el compromiso
        resp = Util.generaCompromiso(conn, request, usuario, datosContrato, jndiName, prefixPath, true);
        //Actualizar el estatus de la ampliación
        String query = "update mContratoCap4Ampliacion set nIdEstado=4 where cIdContratoDefinitivo+'-AMP-'+CONVERT(varchar,nIdConsecutivoAmpliacion)='" + datosContrato.getcIdcontratoDefinitivo() + "' and nIdConsecutivoAmpliacion=" + datosContrato.getnIdAmpliacion();
        if (resp) {
            Util.updateQuery(query, conn);
            log.info("Cambia el estatus");
        }
        return resp;
    }

    @Override
    public boolean nuevoConvenio(Connection conn, HttpSession session, Usuario usuario, DatosContratoCap4 datosContrato) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean updateConvenio(Connection conn, HttpSession session, Usuario usuario, DatosContratoCap4 datosContrato) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean deleteConvenio(Connection conn, HttpSession session, Usuario usuario, DatosContratoCap4 datosContrato) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean migraContratoPlurianual(Connection conn, HttpSession session, Usuario usuario, DatosContratoCap4 datosContrato) throws Exception {
        String cEjercicioActual = "2024";
        String cEjercicioAnt = "2023";
        String cNameDB = "sai";
        boolean resp = false;
        try {
            cEjercicioActual = Util.obtieneEjercicioFiscalActivo(conn);
            int ejercicioAct = Integer.parseInt(cEjercicioActual);
            cEjercicioAnt = "" + (ejercicioAct - 1);
            cNameDB = Util.getNameDB(conn, (ejercicioAct - 1));
            //insertar en mContratoPluriCap4
            insertContratoPluriCap4(conn, datosContrato.getcIdcontratoDefinitivo(), cEjercicioActual, cEjercicioAnt, cNameDB, usuario.getLogin());
            //insertar en mContratoPluriCap4Fechas
            insertFechasContratoPluriCap4(conn, datosContrato.getcIdcontratoDefinitivo(), cNameDB);
            //insertar en mContratoPluriCap4Partidas
            insertPartidasContratoPluriCap4(conn, datosContrato.getcIdcontratoDefinitivo(), cNameDB);
            resp = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        } finally {
            cEjercicioAnt = null;
            cEjercicioActual = null;
            cNameDB = null;
        }
        return resp;
    }

    private boolean insertContratoPluriCap4(Connection conn, String cIdContratoDef, String cEjercicioActual, String cEjercicioAnt, String nameDB, String cLogin) throws Exception {
        StringBuilder query = null;
        PreparedStatement ps = null;
        boolean success = false;
        try {
            query = new StringBuilder();
            query.append(" insert into mContratoPluriCap4 (cIdContratoDefinitivo,cEjercicio,cIdTipoContrato ");
            query.append(" ,cIdUnidadEjecutora,nIdConsecutivo,nIdTipoActividadEconomica,cIdRFC,cConceptoContrato ");
            query.append(" ,nIdEstado,cIdTipoCambio,cIdEntidadContable,cIdUsuarioCreacion,cNoContratoCNET,cNumCuentaDisp ");
            query.append(" ,cMotivoCancelacion,nIdCategoria,nIdFundamentoLeg,mTotalPluriAnual ");
            query.append(" ,mTotalRemanentePluriAnual,nEsDescentralizado,nEsAbierto,fCreacion,C_FOLIO ");
            query.append(" ,ConsecutivoCDIV,ITieneAnticipo,cNumProcedCNET,nCodExpedienteCNET ");
            query.append(" ,nCodContratoCNET,cOficioDG,cFolioMASCP) ");
            query.append(" select  ");
            query.append(" cIdContratoDefinitivo ");
            query.append(" ,? cejercicio,cIdTipoContrato,cIdUnidadEjecutora ");
            query.append(" ,nIdConsecutivo,nIdTipoActividadEconomica ");
            query.append(" ,cIdRFC,cConceptoContrato,? nidEstado ");
            query.append(" ,cIdTipoCambio,cIdEntidadContable ");
            query.append(" ,? usuarioCreacion,cNoContratoCNET,cNumCuentaDisp ");
            query.append(" ,null motivoCancelacion,nIdCategoria,nIdFundamentoLeg ");
            query.append(" ,mTotalPluriAnual,(mTotalRemanentePluriAnual-pagado) remanente,nEsDescentralizado ");
            query.append(" ,nEsAbierto,GETDATE() fechaCreacion,null,null,ITieneAnticipo ");
            query.append(" ,cNumProcedCNET,nCodExpedienteCNET,nCodContratoCNET ");
            query.append(" ,cOficioDG,cFolioMASCP ");
            query.append(" from " + nameDB + ".dbo.fn_mContratosPlurianualesCap4('',?,'',?) ");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cEjercicioActual);
            ps.setInt(2, ContractStatus.CAPTURED);
            ps.setString(3, cLogin);
            ps.setString(4, cIdContratoDef);
            ps.setString(5, cEjercicioAnt);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
            ps = null;
        }
        return success;
    }

    private boolean insertFechasContratoPluriCap4(Connection conn, String cIdContratoDef, String nameDB) throws Exception {
        StringBuilder query = null;
        PreparedStatement ps = null;
        boolean success = false;
        try {
            query = new StringBuilder();
            query.append(" insert into mContratoPluriCap4Fechas (nIdFecha,cIdContratoDefinitivo,fFecha) ");
            query.append(" select nIdFecha,cIdContratoDefinitivo,fFecha  from " + nameDB + ".dbo.mContratoCap4Fechas with(Nolock) where cIdContratoDefinitivo=?");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cIdContratoDef);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
            ps = null;
        }
        return success;
    }

    private boolean insertPartidasContratoPluriCap4(Connection conn, String cIdContratoDef, String nameDB) throws Exception {
        StringBuilder query = null;
        PreparedStatement ps = null;
        boolean success = false;
        try {
            query = new StringBuilder();
            query.append("   insert into mContratoPluriCap4Partidas ( ");
            query.append("   cIdContratoDefinitivo, nIdPartida,  ");
            query.append("   cIdSubPartida, cIdCABM, cIdUnidadMedida,  ");
            query.append("   cDescripcionAdicional, nIdIVA, nCantidadMin,  ");
            query.append("   nCantidadMax, mPrecioUnitario, mPrecioUnitarioMax,  ");
            query.append("   mMontoNetoLinea, mMontoNetoMinimo,  ");
            query.append("   mMontoNetoMaximo, mMontoNetoTotalPluri,  ");
            query.append("   mMontoNetoRemanentePluri ");
            query.append(" )  ");
            query.append(" Select  ");
            query.append("   part.cIdContratoDefinitivo,  ");
            query.append("   nIdPartida,  ");
            query.append("   cIdSubPartida,  ");
            query.append("   cIdCABM,  ");
            query.append("   cIdUnidadMedida,  ");
            query.append("   cDescripcionAdicional,  ");
            query.append("   nIdIVA,  ");
            query.append("   1 nCantidadMin,  ");
            query.append("   case when cont.nIdTipoActividadEconomica=2 then 2 else 1 end nCantidadMax,  ");
            query.append("   case when cont.nIdTipoActividadEconomica=2 then mPrecioUnitario else 0 end mPrecioUnitario,  ");
            query.append("   case when cont.nIdTipoActividadEconomica=2 then mPrecioUnitario else 0 end mPrecioUnitarioMax,  ");
            query.append("   0 mMontoNetoLinea,  ");
            query.append("   0 mMontoNetoMinimo,  ");
            query.append("   0 mMontoNetoMaximo,  ");
            query.append("   mMontoNetoTotalPluri,  ");
            query.append("   ( ");
            query.append("     mMontoNetoTotalPluri - recep.totalRecepcionado ");
            query.append("   ) remanente  ");
            query.append(" from  ");
            query.append("   " + nameDB + ".dbo.mContratoCap4Partidas part with(Nolock) ");
            query.append("   inner join " + nameDB + ".dbo.mContratoCap4 cont with(Nolock) ");
            query.append("   on cont.cIdContratoDefinitivo=part.cIdContratoDefinitivo ");
            query.append("   inner join ( ");
            query.append("     select  ");
            query.append("       recep.cIdpedContDef,  ");
            query.append("       lineas.nIdLineaConsolidado,  ");
            query.append("       sum(lineas.mMontoConIVA) totalRecepcionado  ");
            query.append("     from  ");
            query.append("       " + nameDB + "..mRecepcionpMat as recep with(Nolock)  ");
            query.append("       inner join " + nameDB + "..mRecepcionpMatLineas as lineas with(Nolock) on lineas.cIdRecepMat = recep.cIdRecepMat  ");
            query.append("       and lineas.cIdpedContDef = recep.cIdpedContDef  ");
            query.append("       and lineas.nIdConsecutivoRecepM = recep.nIdConsecutivoRecepM  ");
            query.append("     where  ");
            query.append("       recep.nIdEstadoRecepMat  in( 3,5 ) ");
            query.append("       and recep.cIdpedContDef = ? ");
            query.append("     group by  ");
            query.append("       recep.cIdpedContDef,  ");
            query.append("       lineas.nIdLineaConsolidado ");
            query.append("   ) recep on part.cIdContratoDefinitivo = recep.cIdpedContDef  ");
            query.append("   and part.nIdPartida = recep.nIdLineaConsolidado  ");
            query.append(" where  ");
            query.append("   part.cIdContratoDefinitivo = ? ");
            query.append(" union  ");
            query.append(" Select  ");
            query.append("   part.cIdContratoDefinitivo,  ");
            query.append("   nIdPartida,  ");
            query.append("   cIdSubPartida,  ");
            query.append("   cIdCABM,  ");
            query.append("   cIdUnidadMedida,  ");
            query.append("   cDescripcionAdicional,  ");
            query.append("   nIdIVA,  ");
            query.append("   1 nCantidadMin,  ");
            query.append("   case when cont.nIdTipoActividadEconomica=2 then 2 else 1 end nCantidadMax,  ");
            query.append("   case when cont.nIdTipoActividadEconomica=2 then mPrecioUnitario else 0 end mPrecioUnitario,  ");
            query.append("   case when cont.nIdTipoActividadEconomica=2 then mPrecioUnitario else 0 end mPrecioUnitarioMax,  ");
            query.append("   0 mMontoNetoLinea,  ");
            query.append("   0 mMontoNetoMinimo,  ");
            query.append("   0 mMontoNetoMaximo,  ");
            query.append("   mMontoNetoTotalPluri,  ");
            query.append("   ( ");
            query.append("     part.mMontoNetoRemanentePluri - recep.totalRecepcionado ");
            query.append("   ) remanente ");
            query.append(" from  ");
            query.append("   " + nameDB + "..mContratoPluriCap4Partidas part with(Nolock)  ");
            query.append("   inner join " + nameDB + ".dbo.mContratoPluriCap4 cont with(Nolock) ");
            query.append("   on cont.cIdContratoDefinitivo=part.cIdContratoDefinitivo ");
            query.append("   inner join ( ");
            query.append("     select  ");
            query.append("       recep.cIdpedContDef,  ");
            query.append("       lineas.nIdLineaConsolidado,  ");
            query.append("       sum(lineas.mMontoConIVA) totalRecepcionado  ");
            query.append("     from  ");
            query.append("       " + nameDB + "..mRecepcionpMat as recep with(Nolock)  ");
            query.append("       inner join " + nameDB + "..mRecepcionpMatLineas as lineas with(Nolock) on lineas.cIdRecepMat = recep.cIdRecepMat  ");
            query.append("       and lineas.cIdpedContDef = recep.cIdpedContDef  ");
            query.append("       and lineas.nIdConsecutivoRecepM = recep.nIdConsecutivoRecepM  ");
            query.append("     where  ");
            query.append("       recep.nIdEstadoRecepMat in( 3,5 ) ");
            query.append("       and recep.cIdpedContDef = ? ");
            query.append("     group by  ");
            query.append("       recep.cIdpedContDef,  ");
            query.append("       lineas.nIdLineaConsolidado ");
            query.append("   ) recep on part.cIdContratoDefinitivo = recep.cIdpedContDef  ");
            query.append("   and part.nIdPartida = recep.nIdLineaConsolidado  ");
            query.append(" where  ");
            query.append("   part.cIdContratoDefinitivo = ? ");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cIdContratoDef);
            ps.setString(2, cIdContratoDef);
            ps.setString(3, cIdContratoDef);
            ps.setString(4, cIdContratoDef);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
            ps = null;
        }
        return success;
    }

    @Override
    public boolean savePartidasContPlurianual(Connection conn, HttpSession session, Usuario usuario, DatosContratoCap4 datosContrato) throws Exception {
        //Obtener los datos del contrato.
        getDataContractPlu(conn, datosContrato);
        //Validar si es contrato abierto
        saveItemsContractPlu(conn, datosContrato);
        return true;
    }

    private boolean saveItemsContractPlu(Connection conn, DatosContratoCap4 datosContrato) throws Exception {
        StringBuilder query = null;
        PreparedStatement ps = null;
        boolean success = false;
        ArrayList<PartidasContratoCap4> arrayPartidas = null;
        try {
            query = new StringBuilder();
            arrayPartidas = datosContrato.getArrayPartidas();
            query.append(" update part set part.nCantidadMin=case when cont.nIdTipoActividadEconomica=1 then 1 else ? end ");
            query.append(" ,part.nCantidadMax=case when cont.nIdTipoActividadEconomica=1 then 1 else ? end ");
            query.append(" ,part.mPrecioUnitario=case when cont.nIdTipoActividadEconomica=1 then round((?/(1+(0.01*catIVA.VALOR))),2) else part.mPrecioUnitario end ");
            query.append(" ,part.mPrecioUnitarioMax=case when cont.nIdTipoActividadEconomica=1 then round((?/(1+(0.01*catIVA.VALOR))),2) else part.mPrecioUnitario end ");
            query.append(" ,part.mMontoNetoLinea=?,part.mMontoNetoMinimo=?,part.mMontoNetoMaximo=? ");
            query.append(" from mContratoPluriCap4Partidas as part with(Nolock) ");
            query.append(" inner join mContratoPluriCap4 cont with(Nolock) ");
            query.append(" on cont.cIdContratoDefinitivo=part.cIdContratoDefinitivo ");
            query.append(" inner join mCatalogoTipoIVA catIVA with(Nolock) ");
            query.append(" on catIVA.IDIVA=part.nIdIVA ");
            query.append(" where part.cIdContratoDefinitivo=? and part.nIdContratoPluriCap4Partida=? ");
            int i = 0;
            double mMaximo = 0.0d;
            double mMinimo = 0.0d;
            ps = conn.prepareStatement(query.toString());
            while (i < arrayPartidas.size()) {
                ps.setInt(1, arrayPartidas.get(i).getnCantidadMin());
                ps.setInt(2, arrayPartidas.get(i).getnCantidadMax());
                ps.setDouble(3, Math.round(arrayPartidas.get(i).getmMontoNetoLinea() * 100) / 100.0d);
                ps.setDouble(4, Math.round(arrayPartidas.get(i).getmMontoNetoLinea() * 100) / 100.0d);
                ps.setDouble(5, Math.round(arrayPartidas.get(i).getmMontoNetoLinea() * 100) / 100.0d);
                if (datosContrato.getnEsAbierto() == 1) {
                    //Contrato de mínimos y máxios
                    ps.setDouble(4, Math.round(arrayPartidas.get(i).getmMontoNetoMax() * 100) / 100.0d);
                    ps.setDouble(6, Math.round(arrayPartidas.get(i).getmMontoNetoMin() * 100) / 100.0d);
                    ps.setDouble(7, Math.round(arrayPartidas.get(i).getmMontoNetoMax() * 100) / 100.0d);
                    if (datosContrato.getcActividadEconomica() == 1) {
                        //servicios
                        if ((Math.round(arrayPartidas.get(i).getmMontoNetoLinea() * 100) / 100.0d) == 0.0d) {
                            ps.setDouble(3, Math.round(arrayPartidas.get(i).getmMontoNetoMin() * 100) / 100.0d);
                            ps.setDouble(4, Math.round(arrayPartidas.get(i).getmMontoNetoMin() * 100) / 100.0d);
                            ps.setDouble(5, (Math.round(arrayPartidas.get(i).getmMontoNetoMin() * 100) / 100.0d));
                        }
                        if ((Math.round(arrayPartidas.get(i).getmMontoNetoLinea() * 100) / 100.0d) > (Math.round(arrayPartidas.get(i).getmMontoNetoMax() * 100) / 100.0d)) {
                            ps.setDouble(3, Math.round(arrayPartidas.get(i).getmMontoNetoMax() * 100) / 100.0d);
                            ps.setDouble(5, (Math.round(arrayPartidas.get(i).getmMontoNetoMax() * 100) / 100.0d));
                        }
                    } else {
                        //Bienes
                        mMaximo = Math.round((Math.round(arrayPartidas.get(i).getmPrecioU() * 100) / 100.0d) * arrayPartidas.get(i).getnCantidadMax() * arrayPartidas.get(i).getnIdIVA()) * 100 / 100.0d;
                        mMinimo = Math.round((Math.round(arrayPartidas.get(i).getmPrecioU() * 100) / 100.0d) * arrayPartidas.get(i).getnCantidadMin() * arrayPartidas.get(i).getnIdIVA()) * 100 / 100.0d;
                        ps.setDouble(5, mMinimo);
                        ps.setDouble(6, mMinimo);
                        ps.setDouble(7, mMaximo);
                    }
                } else {
                    //Contrato cerrado
                    ps.setDouble(6, Math.round(arrayPartidas.get(i).getmMontoNetoLinea() * 100) / 100.0d);
                    ps.setDouble(7, Math.round(arrayPartidas.get(i).getmMontoNetoLinea() * 100) / 100.0d);
                }
                ps.setString(8, datosContrato.getcIdcontratoDefinitivo());
                ps.setInt(9, arrayPartidas.get(i).getnIdContatoPartida());
                success = ps.executeUpdate() > 0;
                i++;
            }
        } finally {
            CloseObject.closeObject(ps, false);
            if (!arrayPartidas.isEmpty()) {
                arrayPartidas.clear();
            }
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
            arrayPartidas = null;
        }
        return success;
    }

    private boolean getDataContractPlu(Connection conn, DatosContratoCap4 datosContrato) throws Exception {
        StringBuilder query = null;
        PreparedStatement ps = null;
        boolean success = false;
        ResultSet rs = null;
        try {
            query = new StringBuilder();
            query.append(" select *from mContratoPluriCap4 with(Nolock) where cIdContratoDefinitivo=? ");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, datosContrato.getcIdcontratoDefinitivo());
            rs = ps.executeQuery();
            if (rs.next()) {
                datosContrato.setcActividadEconomica(rs.getInt("nIdTipoActividadEconomica"));
                datosContrato.setnEsAbierto(rs.getInt("nEsAbierto"));
                datosContrato.setnEsDescentralizado(rs.getInt("nEsDescentralizado"));
                datosContrato.setcDescripcion(rs.getString("cConceptoContrato"));
                datosContrato.setcEjercicio(rs.getString("cEjercicio"));
                datosContrato.setcFolioMASCP(rs.getString("cFolioMASCP"));
                datosContrato.setcIdRFC(rs.getString("cIdRFC"));
                datosContrato.setcIdUnidadEjecutora(rs.getString("cIdUnidadEjecutora"));
                datosContrato.setcNoContCNET(rs.getString("cNoContratoCNET"));
                datosContrato.setcNoProcedimientoCNET(rs.getString("cNumProcedCNET"));
                datosContrato.setcOficioDG(rs.getString("cOficioDG"));
                datosContrato.setnCondContratoCNET(rs.getString("nCodContratoCNET"));
                datosContrato.setnCondExpedienteCNET(rs.getString("nCodExpedienteCNET"));
                datosContrato.setnIdCategoria(rs.getInt("nIdCategoria"));
                datosContrato.setnIdFundamentoLeg(rs.getInt("nIdFundamentoLeg"));
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
        }
        return success;
    }

    @Override
    public boolean apruebaContratoPluri(Connection conn, HttpServletRequest request, Usuario usuario, DatosContratoCap4 datosContrato, String folioGenerator, String jndiName, String prefixPath) throws Exception {
        CallableStatement cmst = null, cmst2 = null;
        int outputValue = -1;
        int folio;
        String folioCaso = null;
        boolean resp = false;
        try {
            cmst = conn.prepareCall("{?= call pa_validaContratoPluriCap4Financiero (?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, datosContrato.getcEjercicio());
            cmst.setString(3, datosContrato.getcIdcontratoDefinitivo());
            log.info("Object: {}", "call pa_validaContratoPluriCap4Financiero (" + datosContrato.getcEjercicio() + "," + datosContrato.getcIdcontratoDefinitivo() + ")");
            cmst.execute();
            outputValue = cmst.getInt(1);
            if (outputValue == 0 || outputValue == 4) {
                folio = -1;
                //No tiene folio previo
                if (outputValue == 0) {
                    //crear el caso
                    Caso c = iniciaCaso(request, "9", folioGenerator, jndiName, usuario);
                    folioCaso = c.getFolio();
                    int indice = folioCaso.lastIndexOf('-') + 1;
                    folio = Integer.parseInt(folioCaso.substring(indice));
                    //se avanza caso para que no se vea la operacion  en el inbox
                    Util.avanzaCaso(request, c, usuario, prefixPath, new String[] { "CONSULTA_CONTRATODIVERSO" }, new String[] { "consulta_contrato" }, jndiName);
                }
                log.info("Object: {}", "exec pa_apruebaContratoPluriCap4 '" + datosContrato.getcEjercicio() + "','" + datosContrato.getcIdcontratoDefinitivo() + "','" + usuario.getLogin() + "','" + folio + "','" + folioCaso + "'");
                cmst2 = conn.prepareCall("{?= call pa_apruebaContratoPluriCap4(?,?,?,?,?)}");
                cmst2.registerOutParameter(1, Types.INTEGER);
                cmst2.setString(2, datosContrato.getcEjercicio());
                cmst2.setString(3, datosContrato.getcIdcontratoDefinitivo());
                cmst2.setString(4, usuario.getLogin());
                cmst2.setString(5, folio + "");
                cmst2.setString(6, folioCaso);
                cmst2.execute();
                outputValue = cmst2.getInt(1);
                if (outputValue == 0) {
                    resp = true;
                }
            }
        } finally {
            if (cmst != null) {
                cmst.close();
            }
            if (cmst2 != null) {
                cmst2.close();
            }
            cmst = null;
            cmst2 = null;
            folioCaso = null;
        }
        return resp;
    }

    @Override
    public boolean devuelveContratoPluri(Connection conn, Usuario usuario, DatosContratoCap4 datosContrato) throws SQLException {
        CallableStatement cmst = null;
        int outputValue = -1;
        boolean resp = false;
        try {
            log.debug("Object: {}", "pa_devuelveContratoPluriCap4 '" + datosContrato.getcEjercicio() + "','" + datosContrato.getcIdcontratoDefinitivo() + "','" + usuario.getLogin() + "'");
            cmst = conn.prepareCall("{?= call pa_devuelveContratoPluriCap4 (?,?,?)}");
            cmst.registerOutParameter(1, Types.INTEGER);
            cmst.setString(2, datosContrato.getcEjercicio());
            cmst.setString(3, datosContrato.getcIdcontratoDefinitivo());
            cmst.setString(4, usuario.getLogin());
            cmst.execute();
            outputValue = cmst.getInt(1);
            if (outputValue == 0)
                resp = true;
            System.out.println("Parametro de salida del procedimiento=" + outputValue);
        } finally {
            if (cmst != null) {
                cmst.close();
            }
            cmst = null;
        }
        return resp;
    }

    @Override
    public boolean precomprometerContratoPluri(Connection conn, HttpServletRequest request, Usuario usuario, DatosContratoCap4 datosContrato, ArrayList<List<String>> tabla, String prefixPath, String jndiName) throws Exception {
        String cEevento = "PRECOM";
        boolean resp = false;
        String[] param = new String[6];
        param[0] = datosContrato.getcIdcontratoDefinitivo();
        param[1] = "N";
        param[2] = Util.obtieneEjercicioFiscalActivo(conn);
        param[3] = datosContrato.getDescripPoliza();
        if (!validaEstatusContratoPluriCap4(conn, datosContrato.getcIdcontratoDefinitivo(), 2)) {
            log.warn("El contrato no se puede precomprometer por que su estatus no lo permite.");
            throw new Exception("El contrato no se puede precomprometer por que su estatus no lo permite.");
        }
        if (Util.hayPrecompromiso(conn, datosContrato.getcIdcontratoDefinitivo())) {
            log.warn("El contrato ya tiene precompromiso.");
            throw new Exception("El contrato ya tiene precompromiso.");
        }
        if (datosContrato.getcCuentaDisponible().equals("82109")) {
            cEevento = "R_" + cEevento;
            param[1] = "S";
        }
        switch(datosContrato.getnEsDescentralizado()) {
            case 0:
                resp = precompromisoCentralizado(conn, request, usuario, param, tabla, jndiName, cEevento, prefixPath);
                //Se cambia de estatus
                if (resp) {
                    Util.updateQuery("update mContratoPluriCap4 set nIdEstado=3 where cIdContratoDefinitivo='" + datosContrato.getcIdcontratoDefinitivo() + "'", conn);
                    log.info("Actualización de estatus del contrato ");
                }
                break;
            case //Descentralizados
            1:
                resp = precompromisoDesCentralizado(conn, request, usuario, param, tabla, jndiName, cEevento, prefixPath);
                if (resp) {
                    Util.updateQuery("update mContratoPluriCap4 set nIdEstado=3 where cIdContratoDefinitivo='" + datosContrato.getcIdcontratoDefinitivo() + "'", conn);
                    log.info("Actualización de estatus del contrato ");
                }
                break;
            default:
                log.info("Opción desconocida.");
                break;
        }
        return resp;
    }

    @Override
    public boolean devuelvePrecompromisoContratoPluri(Connection conn, HttpServletRequest request, Usuario usuario, DatosContratoCap4 datosContrato, String prefixPath, String jndiName) throws Exception {
        boolean resp = false;
        try {
            //cancela precompromiso
            resp = Util.cancelaPrecompromiso(conn, request, usuario, datosContrato, prefixPath, jndiName);
            //Se cambia de estatus
            String query = "update mContratoPluriCap4 set nIdEstado=2 where cIdContratoDefinitivo='" + datosContrato.getcIdcontratoDefinitivo() + "'";
            if (resp) {
                Util.updateQuery(query, conn);
                log.info("Actualización de estatus del contrato plurianual cap4");
            }
        } catch (Exception e) {
            // TODO: handle exception
            log.error("Object: {}", e.getMessage());
            throw new Exception(e.getMessage());
        }
        return resp;
    }

    @Override
    public boolean comprometerContratoPluri(Connection conn, HttpServletRequest request, Usuario usuario, DatosContratoCap4 datosContrato, String jndiName, String prefixPath) throws Exception {
        boolean resp = false;
        PreparedStatement pstm = null;
        try {
            if (!validaEstatusContratoPluriCap4(conn, datosContrato.getcIdcontratoDefinitivo(), 3)) {
                log.warn("El contrato no se puede comprometer por que su estatus no lo permite.");
                throw new Exception("El contrato no se puede comprometer por que su estatus no lo permite.");
            }
            if (Util.hayCompromiso(conn, datosContrato.getcIdcontratoDefinitivo())) {
                log.warn("El contrato ya tiene compromiso.");
                throw new Exception("El contrato ya tiene compromiso.");
            }
            //Generar el compromiso
            resp = Util.generaCompromiso(conn, request, usuario, datosContrato, jndiName, prefixPath, false);
            //Se cambia de estatus
            if (resp) {
                pstm = conn.prepareStatement("update mContratoPluriCap4 set nIdEstado=4 where cIdContratoDefinitivo=? ");
                pstm.setString(1, datosContrato.getcIdcontratoDefinitivo());
                pstm.executeUpdate();
                log.info("Actualización de estatus del contrato ");
            }
            resp = true;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
        }
        return resp;
    }
}
