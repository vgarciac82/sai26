package com.syc.adquisiciones.util;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.syc.adquisiciones.core.ContratoModificado;
import com.syc.adquisiciones.core.DatosContratoCap4;
import com.syc.adquisiciones.core.Respuesta;
import com.syc.adquisiciones.servlet.CambiaPropiedadesUsuario;
import com.syc.contable.ContableInterface;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Role;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioPropiedades;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {

    private static Logger log = LoggerFactory.getLogger(Util.class);

    // se Crea una tabla en un array list a partir de un arreglo
    public static ArrayList<List<String>> creaArray(String[] arrayTabla) throws Exception {
        ArrayList<List<String>> tabla = new ArrayList<List<String>>();
        List<String> fila = new ArrayList<String>();
        try {
            for (int i = 0; i < arrayTabla.length; i++) {
                if ("|".equals(arrayTabla[i])) {
                    tabla.add(fila);
                    fila = null;
                    fila = new ArrayList<String>();
                } else {
                    fila.add(arrayTabla[i].trim());
                }
            }
        } finally {
            if (!fila.isEmpty()) {
                fila.clear();
            }
            fila = null;
        }
        return tabla;
    }

    public static void bitacoraMovimientos(String cIdContratoDefinitivo, String cAccion, String uLogin, Connection conn) throws SQLException {
        CallableStatement cmst = null;
        try {
            cmst = conn.prepareCall("{call sp_mBitacoraMovimientos (?,?,?)}");
            cmst.setString(1, cIdContratoDefinitivo);
            cmst.setString(2, cAccion);
            cmst.setString(3, uLogin);
            log.info("Object: {}", "call sp_mBitacoraMovimientos ('" + cIdContratoDefinitivo + "','" + cAccion + "','" + uLogin + "')");
            cmst.execute();
        } finally {
            if (cmst != null) {
                cmst.close();
            }
            cmst = null;
        }
    }

    public static String generaCaNoContrarecibo(Usuario usuario, String cEjercicio, String tipo) throws SQLException {
        CFSequenceManager sequence = CFSequenceManager.getInstance();
        int seqFolio = sequence.nextVal(tipo + "-" + usuario.getPropiedad("CCENTROCONTABLE").getValor());
        String seqValue = "000000" + seqFolio;
        seqValue = seqValue.substring(seqValue.length() - 6);
        // seqValue = "1" + seqValue.substring(seqValue.length() - 5);
        seqValue = usuario.getPropiedad("CCENTROCONTABLE").getValor() + tipo + cEjercicio + seqValue;
        return seqValue;
    }

    public static String generaCaNoContrarecibo(String cCentroContable, String cEjercicio, String tipo) throws SQLException {
        CFSequenceManager sequence = CFSequenceManager.getInstance();
        int seqFolio = sequence.nextVal(tipo + "-" + cCentroContable);
        String seqValue = "000000" + seqFolio;
        seqValue = seqValue.substring(seqValue.length() - 6);
        // seqValue = "1" + seqValue.substring(seqValue.length() - 5);
        seqValue = cCentroContable + tipo + cEjercicio + seqValue;
        return seqValue;
    }

    private synchronized static Caso iniciaCasoDes(Usuario user, String ur, String tCaso, String jndiName) throws GestionException {
        // Este metodo es una copia del metodo del GestionServlet para iniciar
        // casos
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
        Caso c = casoTx.IniciaCaso(user.getNombre(), ur, idTC);
        if (c == null) {
            log.error("No se logro crear el caso");
            throw new GestionException("No se logró crear el caso");
        }
        return c;
    }

    public synchronized static Caso generaGuardaCaso(String ur, String tipoCaso, String CONCEPTO_MOV, Usuario usuario, String jndiName, String cEjercicio) throws ServletException, IOException {
        String folioCaso = null;
        Caso c = null;
        Connection conn1 = null;
        try {
            String DATE_FORMAT = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            // today
            Calendar c1 = Calendar.getInstance();
            String today = sdf.format(c1.getTime());
            c = iniciaCasoDes(usuario, ur, tipoCaso, jndiName);
            folioCaso = c.getFolio();
            try {
                Map<String, String> datos = new HashMap<String, String>();
                datos.put("FOLIO", folioCaso);
                datos.put("FECHA_DOCUMENTO", today);
                datos.put("EJERCICIO_FISCAL", cEjercicio);
                datos.put("OPERADOR", usuario.getNombre());
                datos.put("MONEDA", "MXP");
                datos.put("APLICADO_CONT", "false");
                datos.put("CONCEPTO_MOV", CONCEPTO_MOV);
                conn1 = DataSourceManager.getConnection(jndiName);
                CasoDatoManager.update(conn1, c.getIdTC(), c.getIdCaso(), datos);
                conn1.commit();
            } catch (SQLException eSQL) {
                try {
                    conn1.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                eSQL.printStackTrace();
            }
        } catch (GestionException exc) {
            log.error("Iniciando Caso", exc);
            throw new ServletException(exc);
        } finally {
            try {
                if (conn1 != null)
                    conn1.close();
            } catch (SQLException exc) {
                exc.printStackTrace();
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn1 = null;
            folioCaso = null;
        }
        return c;
    }

    public static void creaDetPreCompromisoConvenio(Connection conn, ContratoModificado conv, String nameStoreProcedure) throws SQLException {
        //String cIdContratoDefinitivo, int nConsecutivoMod,int nFolioPreCompromiso,String cCentroContable,String ur, int nTipoPago
        CallableStatement cmst = null;
        try {
            cmst = conn.prepareCall("{call ? (?,?,?,?,?,?)}");
            cmst.setString(1, nameStoreProcedure);
            cmst.setString(2, conv.getcIdContratoDefinitivo());
            cmst.setInt(3, conv.getnConsecutivoModificacion());
            cmst.setInt(4, conv.getnFolioPreCompromiso());
            cmst.setString(5, conv.getcCentroContable());
            cmst.setString(6, conv.getcUnidadEjecutoraLinea());
            cmst.setInt(7, conv.getnEsDescentralizado());
            log.info("Object: {}", "call " + nameStoreProcedure + " ('" + conv.getcIdContratoDefinitivo() + "'," + conv.getnConsecutivoModificacion() + ",'" + conv.getnFolioPreCompromiso() + "','" + conv.getcUnidadEjecutoraLinea() + "'," + conv.getnEsDescentralizado() + ")");
            cmst.execute();
        } finally {
            if (cmst != null) {
                cmst.close();
            }
            cmst = null;
        }
    }

    public static int creaEncPreCompromiso(Usuario usuario, Connection conn, String[] param, String caNoContrarrecibo) throws SQLException {
        String queryEnc = null;
        Statement stmEnc = null;
        int retval = -1;
        String DATE_FORMAT = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        Calendar c2 = Calendar.getInstance();
        c2.add(Calendar.DATE, 8);
        String vigencia = sdf.format(c2.getTime());
        try {
            stmEnc = conn.createStatement();
            queryEnc = "INSERT INTO tPreCompromisoEncabezado (nFolioPreCompromiso,fCarga,cIdContrato,cTipoContrato,fAplicacion,cCentroContable,cRamo " + " ,cUnidadResponsable,cDocumentoHaplicado,nFolioPoliza,caNoPreCompromiso,nEnviadoSICOP,cTipoPoliza" + " ,nMes,cRevisado,aEjercicioFiscal,cUnidadResponsableContable,nFolioPolizaCancelacion,fCancelacion" + " ,cDescripcionPoliza,nStatusFinanciero,fVigencia,C_FOLIO_COMP,ConsecutivoCOMP) " + " values (" + param[4] + ",GETDATE(),'" + param[0] + "','DI',GETDATE()," + usuario.getPropiedad("CCENTROCONTABLE").getValor() + "," + usuario.getU_Ramo() + ",'" + usuario.getU_UR() + "',NULL, NULL, '" + caNoContrarrecibo + "' , 0 , 'CO' , " + "DATEPART(MONTH,GETDATE()), NULL , '" + param[2] + "', 'RHQ' , NULL , NULL , '" + param[3] + "', 0,'" + vigencia + "',NULL,NULL)";
            log.info("Object: {}", queryEnc.toString());
            stmEnc.executeUpdate(queryEnc);
            retval = 0;
        } finally {
            if (stmEnc != null) {
                stmEnc.close();
            }
            if (c2 != null) {
                c2.clear();
            }
            stmEnc = null;
            queryEnc = null;
            DATE_FORMAT = null;
            sdf = null;
            c2 = null;
            vigencia = null;
        }
        return retval;
    }

    public static int integraFoliosCompromiso(Connection conn, String cIdContratoDef, String cEjercicio) throws SQLException {
        int resp = -1;
        CFSequenceManager sequence = null;
        int seqFolio;
        String seqValue;
        StringBuilder query = null;
        PreparedStatement pstm = null;
        try {
            sequence = CFSequenceManager.getInstance();
            seqFolio = sequence.nextVal("INCO");
            seqValue = "INCO" + cEjercicio + String.format("%06d", seqFolio);
            query = new StringBuilder();
            query.append(" insert into tIntegraFoliosCompromiso (caNoIntegradaComp,nFolioCompromiso) ");
            query.append(" select ?,nFolioCompromiso from tCompromisoEncabezado with(Nolock) where cIdContrato=? and cDocumentoHaplicado is null ");
            log.info("Object: {}", query.toString());
            pstm = conn.prepareStatement(query.toString());
            pstm.setString(1, seqValue);
            pstm.setString(2, cIdContratoDef);
            resp = pstm.executeUpdate();
        } finally {
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
            sequence = null;
            pstm = null;
        }
        return resp;
    }

    public static int creaEncDetPreCompromiso(Usuario usuario, Connection conn, String[] param, ArrayList<List<String>> tabla, String cEevento, boolean isDescentralizado) throws SQLException {
        String queryEnc = "", queryDet = "", queryRel = "", queryDoc = "";
        Statement stmEnc = null, stmDet = null, stmRel = null, stmDoc = null;
        int retval = -1;
        int seqFolio;
        String seqValue;
        String caNoContrarrecibo;
        String DATE_FORMAT = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        Calendar c2 = Calendar.getInstance();
        c2.add(Calendar.DATE, 8);
        String vigencia = sdf.format(c2.getTime());
        // String cidContrato,String esRadicado,String cEjercicio,String
        // descripPoliza,int folio,String cFolioPrecom
        CFSequenceManager sequence = null;
        try {
            sequence = CFSequenceManager.getInstance();
            stmEnc = conn.createStatement();
            // Crea el encabezado de la liberacion del compromiso.
            seqFolio = sequence.nextVal("CO-" + usuario.getPropiedad("CCENTROCONTABLE").getValor());
            seqValue = "" + (100000 + seqFolio);
            seqValue = usuario.getPropiedad("CCENTROCONTABLE").getValor() + "CO" + param[2] + seqValue;
            caNoContrarrecibo = seqValue;
            queryEnc = "INSERT INTO tPreCompromisoEncabezado (nFolioPreCompromiso,fCarga,cIdContrato,cTipoContrato,fAplicacion,cCentroContable,cRamo " + " ,cUnidadResponsable,cDocumentoHaplicado,nFolioPoliza,caNoPreCompromiso,nEnviadoSICOP,cTipoPoliza" + " ,nMes,cRevisado,aEjercicioFiscal,cUnidadResponsableContable,nFolioPolizaCancelacion,fCancelacion" + " ,cDescripcionPoliza,nStatusFinanciero,fVigencia,C_FOLIO_COMP,ConsecutivoCOMP) " + " values (" + param[4] + ",GETDATE(),'" + param[0] + "','DI',GETDATE()," + usuario.getPropiedad("CCENTROCONTABLE").getValor() + "," + usuario.getU_Ramo() + ",'" + usuario.getU_UR() + "',NULL, NULL, '" + caNoContrarrecibo + "' , 0 , 'CO' , " + "DATEPART(MONTH,GETDATE()), NULL , '" + param[2] + "', 'RHQ' , NULL , NULL , '" + param[3] + "', 0,'" + vigencia + "',NULL,NULL)";
            log.info("Object: {}", queryEnc.toString());
            stmEnc.executeUpdate(queryEnc);
            // Crea el detalle de la liberacion del precompromiso.
            stmDet = conn.createStatement();
            stmRel = conn.createStatement();
            stmDoc = conn.createStatement();
            List<String> fila = new ArrayList<String>();
            Iterator<List<String>> itr = tabla.iterator();
            String ue = "";
            int i = 1;
            while (itr.hasNext()) {
                fila = itr.next();
                ue = (fila.get(0)).substring(56, 59);
                if (isDescentralizado) {
                    if (Double.parseDouble(fila.get(2)) > 0 && ue.equals(usuario.getU_UR())) {
                        queryDet = "INSERT INTO tPreCompromisoDetalle (nFolioPreCompromiso,nDocRenglon,EP,cEvento,mImporte,mImporteNegativo,cMes,cCentroContable)" + " values(" + param[4] + "," + i + ",'" + fila.get(0) + "','" + cEevento + "'," + fila.get(2) + ",-" + fila.get(2) + "," + fila.get(1) + "," + usuario.getPropiedad("CCENTROCONTABLE").getValor() + ")";
                        log.info("Object: {}", queryDet.toString());
                        stmDet.executeUpdate(queryDet);
                        i++;
                    }
                } else {
                    if (Double.parseDouble(fila.get(2)) > 0) {
                        queryDet = "INSERT INTO tPreCompromisoDetalle (nFolioPreCompromiso,nDocRenglon,EP,cEvento,mImporte,mImporteNegativo,cMes,cCentroContable)" + " values(" + param[4] + "," + i + ",'" + fila.get(0) + "','" + cEevento + "'," + fila.get(2) + ",-" + fila.get(2) + "," + fila.get(1) + "," + usuario.getPropiedad("CCENTROCONTABLE").getValor() + ")";
                        log.info("Object: {}", queryDet.toString());
                        stmDet.executeUpdate(queryDet);
                        i++;
                    }
                }
                fila = null;
                fila = new ArrayList<String>();
            }
            if (i == 1) {
                // No hay detalle
                log.warn("No hay  detalle.");
                return -1;
            }
            // Se guarda la relación de precompromisos con el contrato
            queryRel = "insert into mRelPedContPrecomComp values('" + param[0] + "','" + param[5] + "'," + param[4] + ",NULL,NULL)";
            queryDoc = "insert into mDocumentoFolio values('" + param[2] + "','" + param[0] + "'," + usuario.getPropiedad("CCENTROCONTABLE").getValor() + ",'" + usuario.getU_UR() + "',NULL,NULL,'" + param[5] + "'," + param[4] + ")";
            log.info("Object: {}", queryRel.toString());
            log.info("Object: {}", queryDoc.toString());
            stmRel.executeUpdate(queryRel);
            stmDoc.executeUpdate(queryDoc);
            retval = 0;
        } finally {
            if (stmEnc != null) {
                stmEnc.close();
            }
            if (stmDet != null) {
                stmDet.close();
            }
            if (stmRel != null) {
                stmRel.close();
            }
            if (stmDoc != null) {
                stmDoc.close();
            }
            stmEnc = null;
            stmDet = null;
            stmDoc = null;
            stmRel = null;
            sequence = null;
        }
        return retval;
    }

    public static void avanzaCaso(HttpServletRequest req, Caso c, Usuario u, String prefixPath, String[] responsable, String[] nombre, String jndiName) throws GestionException, ServletException, IOException {
        if (c == null) {
            log.error("Llamada invalida, sin Caso seleccionado");
            throw new GestionException("Llamada inválida, sin Caso seleccionado");
        }
        if (c.getIdCaso() <= 0) {
            log.error("Llamada invalida, identificador de caso menor o igual a cero (<= 0)");
            throw new GestionException("Llamada inválida, identificador de caso menor o igual a cero (<= 0)");
        }
        if (c.getCasoOperacion(0).getIdOperacion() <= 0) {
            log.error("Llamada invalida, sin identificador de caso operacion menor o igual a cero (<= 0)");
            throw new GestionException("Llamada inválida, sin identificador de caso operación menor o igual a cero (<= 0)");
        }
        Map<String, String> m = CasoDatoManager.readValuesCasoDato(req, c.getCasoDato(), true);
        CasoBusinessLogic cbl = new CasoBusinessLogic(jndiName);
        cbl.avanzaCaso(c, u.getLogin(), "", responsable, nombre, m, prefixPath);
    }

    public static void avanzaCaso(Caso c, Usuario u, String prefixPath, String[] responsable, String[] nombre, String jndiName) throws GestionException, ServletException, IOException {
        if (c == null) {
            log.error("Llamada invalida, sin Caso seleccionado");
            throw new GestionException("Llamada inválida, sin Caso seleccionado");
        }
        if (c.getIdCaso() <= 0) {
            log.error("Llamada invalida, identificador de caso menor o igual a cero (<= 0)");
            throw new GestionException("Llamada inválida, identificador de caso menor o igual a cero (<= 0)");
        }
        if (c.getCasoOperacion(0).getIdOperacion() <= 0) {
            log.error("Llamada invalida, sin identificador de caso operacion menor o igual a cero (<= 0)");
            throw new GestionException("Llamada inválida, sin identificador de caso operación menor o igual a cero (<= 0)");
        }
        Map<String, String> m = com.syc.gestion.util.Util.readValuesCasoDato(c.getCasoDato());
        CasoBusinessLogic cbl = new CasoBusinessLogic(jndiName);
        cbl.avanzaCaso(c, u.getLogin(), "", responsable, nombre, m, prefixPath);
    }

    public synchronized static boolean hayPrecompromiso(Connection conn, String cIdContratoDef) throws SQLException {
        boolean resp = false;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            String query = "select *from tPreCompromisoEncabezado with(Nolock) where cIdContrato=? and cDocumentoHaplicado='S'";
            log.info("Object: {}", query.toString());
            pstm = conn.prepareStatement(query);
            pstm.setString(1, cIdContratoDef);
            rs = pstm.executeQuery();
            log.info("Object: {}", "cIdContratoDef : " + cIdContratoDef);
            if (rs.next()) {
                resp = true;
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstm);
        }
        return resp;
    }

    public synchronized static boolean hayCompromiso(Connection conn, String cIdContratoDef) throws SQLException {
        boolean resp = false;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            String query = "select *from tCompromisoEncabezado with(Nolock) where cIdContrato=? and cDocumentoHaplicado='S'";
            log.info("Object: {}", query.toString());
            pstm = conn.prepareStatement(query);
            pstm.setString(1, cIdContratoDef);
            rs = pstm.executeQuery();
            log.info("Object: {}", "cIdContratoDef : " + cIdContratoDef);
            if (rs.next()) {
                resp = true;
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstm);
        }
        return resp;
    }

    public static boolean esRecursoFiscal(Connection conn, String cIdContratoDef) throws SQLException {
        boolean resp = false;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            String query = "SELECT det.EP,SUBSTRING(det.EP,40,1)tipoIngreso FROM tPreCompromisoEncabezado enc WITH(NOLOCK) " + " INNER JOIN dbo.tPreCompromisoDetalle det WITH(NOLOCK) ON enc.nFolioPreCompromiso = det.nFolioPreCompromiso " + " WHERE enc.cDocumentoHaplicado='S' AND enc.cIdContrato=? AND SUBSTRING(det.EP,40,1)<>4 ";
            log.info("Object: {}", query.toString());
            pstm = conn.prepareStatement(query);
            pstm.setString(1, cIdContratoDef);
            rs = pstm.executeQuery();
            log.info("Object: {}", "cIdContratoDef : " + cIdContratoDef);
            if (rs.next()) {
                resp = true;
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstm);
        }
        return resp;
    }

    public synchronized static boolean hayCompromisoPendienteAutSICOP(Connection conn, String cIdContratoDef) throws SQLException {
        boolean resp = false;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            String query = "select *from tCompromisoEncabezado with(Nolock) where cIdContrato='" + cIdContratoDef + "' AND cDocumentoHaplicado IS NULL and (nFolioAutSICOP IS NULL or nFolioAutSICOP='' or nFolioAutSICOP='-1')";
            if (cIdContratoDef.indexOf("-AMP-") > 0) {
                // CC-G02-1/2018-AMP-1
                query = "select *from tCompromisoEncabezado with(Nolock) where cIdContrato=SUBSTRING('" + cIdContratoDef + "',0,CHARINDEX('-AMP-','" + cIdContratoDef + "',1)) AND cDocumentoHaplicado IS NULL and (nFolioAutSICOP IS NULL or nFolioAutSICOP='' or nFolioAutSICOP='-1')";
            }
            if (cIdContratoDef.indexOf("#M") > 0) {
                // CV-A10-15/2017#M1
                query = "select *from tCompromisoEncabezado with(Nolock) where cIdContrato=SUBSTRING('" + cIdContratoDef + "',0,CHARINDEX('#M','" + cIdContratoDef + "',1)) AND cDocumentoHaplicado IS NULL and (nFolioAutSICOP IS NULL or nFolioAutSICOP='' or nFolioAutSICOP='-1')";
            }
            log.info("Object: {}", query.toString());
            pstm = conn.prepareStatement(query);
            rs = pstm.executeQuery();
            log.info("Object: {}", "cIdContratoDef : " + cIdContratoDef);
            if (rs.next()) {
                resp = true;
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstm);
        }
        return resp;
    }

    public synchronized static boolean hayPagosPendienteAutSICOP(Connection conn, String cIdContratoDef) throws SQLException {
        boolean resp = false;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            String query = "SELECT *FROM	tPAGODIVERSOEncabezado WITH (NOLOCK) WHERE	cFolioPAGODIVERSO = '" + cIdContratoDef + "' AND		ISNULL(cDocumentoHaplicado, 'S') = 'S' " + " AND		ISNULL(nEnviadoSICOP, 0) IN (0, 2) AND		cIngresosPropios <> 'S'	";
            if (cIdContratoDef.indexOf("-AMP-") > 0) {
                // CC-G02-1/2018-AMP-1
                query = "SELECT *FROM	tPAGODIVERSOEncabezado WITH (NOLOCK) WHERE	cFolioPAGODIVERSO = SUBSTRING('" + cIdContratoDef + "',0,CHARINDEX('-AMP-','" + cIdContratoDef + "',1)) AND		ISNULL(cDocumentoHaplicado, 'S') = 'S' " + " AND		ISNULL(nEnviadoSICOP, 0) IN (0, 2) AND		cIngresosPropios <> 'S'	";
            }
            if (cIdContratoDef.indexOf("#M") > 0) {
                // CV-A10-15/2017#M1
                query = "SELECT *FROM	tPAGODIVERSOEncabezado WITH (NOLOCK) WHERE	cFolioPAGODIVERSO = SUBSTRING('" + cIdContratoDef + "',0,CHARINDEX('#M','" + cIdContratoDef + "',1)) AND		ISNULL(cDocumentoHaplicado, 'S') = 'S' " + " AND		ISNULL(nEnviadoSICOP, 0) IN (0, 2) AND		cIngresosPropios <> 'S'	";
            }
            log.info("Object: {}", query.toString());
            pstm = conn.prepareStatement(query);
            rs = pstm.executeQuery();
            log.info("Object: {}", "cIdContratoDef : " + cIdContratoDef);
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

    public static int creaEncabezadoDetalleComp(String cIdContratoDefinitivo, int nFolioPrecom, int nFolioComp, String isRadicado, String folioCasoCompromiso, Usuario usuario, Connection conn) throws SQLException {
        Statement stmEnc = null, stmDet = null, stmPrecom = null;
        int retval = -1;
        try {
            // Encabezado
            String query = "insert into tCompromisoEncabezado (nFolioCompromiso,fCarga,cIdContrato,cTipoContrato,fAplicacion,cCentroContable " + ",cRamo,cUnidadResponsable,cDocumentoHaplicado,nFolioPoliza,caNoCompromiso,nEnviadoSICOP " + ",cTipoPoliza,nMes,cRevisado,aEjercicioFiscal,cUnidadResponsableContable,nFolioPolizaCancelacion " + ",fCancelacion,cDescripcionPoliza,usuario,cRadicado,nFolioAutSICOP) " + " select " + nFolioComp + ",fCarga,substring(cIdContrato,1,patindex('%/%' , cIdContrato)+4),cTipoContrato,fAplicacion,cCentroContable, " + " cRamo,cUnidadResponsable,null,null,caNoPreCompromiso,nEnviadoSICOP,cTipoPoliza,nMes,cRevisado,aEjercicioFiscal,cUnidadResponsableContable, " + " nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza,'" + usuario.getLogin() + "','" + isRadicado + "',-1 from tPreCompromisoEncabezado with(nolock) " + " where nFolioPreCompromiso=" + nFolioPrecom + " and cIdContrato='" + cIdContratoDefinitivo + "'";
            log.info("Object: {}", query.toString());
            stmEnc = conn.createStatement();
            stmEnc.executeUpdate(query);
            // Detalle
            String sql = "insert into tCompromisoDetalle (nFolioCompromiso,nDocRenglon,EP,cEvento,mImporte,mImporteNegativo,cMes,cCentroContable) select " + nFolioComp + ",nDocRenglon,EP,'CMP002',convert(money,str(mImporte,15,2))as mImporte, " + " convert(money,str(mImporteNegativo,15,2))as mImporteNegativo,cMes,cCentroContable from tPreCompromisoDetalle with(nolock) " + " where nFolioPreCompromiso=" + nFolioPrecom + " AND cEvento not in( 'APTDDISP','R_APTDDISP' )";
            log.info("Object: {}", sql.toString());
            stmDet = conn.createStatement();
            stmDet.executeUpdate(sql);
            // Actualiza tprecompromiso
            String sqlPrecom = "update tPreCompromisoEncabezado set C_FOLIO_COMP='" + folioCasoCompromiso + "', ConsecutivoCOMP=" + nFolioComp + ",nStatusFinanciero=1 " + " where cIdContrato='" + cIdContratoDefinitivo + "' and nFolioPreCompromiso=" + nFolioPrecom;
            log.info("Object: {}", sqlPrecom.toString());
            stmPrecom = conn.createStatement();
            stmPrecom.executeUpdate(sqlPrecom);
            retval = 0;
        } finally {
            if (stmEnc != null) {
                stmEnc.close();
            }
            if (stmDet != null) {
                stmDet.close();
            }
            if (stmPrecom != null) {
                stmPrecom.close();
            }
            stmEnc = null;
            stmDet = null;
            stmPrecom = null;
        }
        return retval;
    }

    public static int updateQuery(String query, Connection conn) throws SQLException {
        int resp = -1;
        PreparedStatement pstm = null;
        try {
            log.info("Object: {}", query.toString());
            pstm = conn.prepareStatement(query);
            resp = pstm.executeUpdate();
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            pstm = null;
        }
        return resp;
    }

    public static boolean cancelaPrecompromiso(Connection conn, HttpServletRequest request, Usuario usuario, DatosContratoCap4 datosContrato, String prefixPath, String jndiName) throws Exception {
        String ueOriginal = usuario.getU_UR();
        String cCentroContableOrig = usuario.getPropiedad("CCENTROCONTABLE").getValor();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        AplicarContableReturn acr = null;
        Statement stmRel = null, stmDoc = null;
        Caso sc = null, c = null;
        int id_caso = 0;
        String sqlDoc, sqlRel;
        boolean resp = false;
        CambiaPropiedadesUsuario cpu = new CambiaPropiedadesUsuario();
        ContableInterface conInt = new AplicacionContable();
        Map<String, String> m = null;
        try {
            String query = "select enc.nFolioPreCompromiso,det.cCentroContable,enc.cUnidadResponsable,caso.ID_CASO,mdoc.C_FOLIO_PRE " + " from tPreCompromisoEncabezado as enc with(Nolock) " + " inner join tPreCompromisoDetalle as det with(Nolock) on enc.nFolioPreCompromiso=det.nFolioPreCompromiso " + " and enc.cDocumentoHaplicado='S' and enc.cIdContrato='" + datosContrato.getcIdcontratoDefinitivo() + "' " + " inner join mDocumentoFolio as mdoc with(Nolock) on mdoc.ConsecutivoPRECOMP=enc.nFolioPreCompromiso and mdoc.cIdUnidadResponsable=enc.cUnidadResponsable " + " and det.cCentroContable=mdoc.cCentroContable " + " inner join CG_CASO as caso with(nolock) on caso.C_FOLIO=mdoc.C_FOLIO_PRE " + " inner join CG_CASO_OPERACION as oper with(Nolock) on oper.ID_CASO=caso.ID_CASO " + " group by enc.nFolioPreCompromiso,det.cCentroContable,enc.cUnidadResponsable,caso.ID_CASO,mdoc.C_FOLIO_PRE";
            log.info("Object: {}", query.toString());
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            int nFolioPrecom = 0;
            while (rs.next()) {
                // Cambia el centro contable al usuario
                cpu.cambiaCentroContableUE(rs.getString("cUnidadResponsable"), rs.getString("cCentroContable"), usuario);
                // Se obtiene el cso
                sc = new Caso();
                id_caso = rs.getInt("ID_CASO");
                nFolioPrecom = rs.getInt("nFolioPreCompromiso");
                sc.setIdCaso(id_caso);
                c = CasoManager.select(conn, sc);
                // Aplicacion contable
                if (c != null) {
                    m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                    acr = conInt.cancelarAppContableNueva(conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), "");
                    if (!acr.isSuccess()) {
                        return resp;
                    }
                    // Elinar los datos de las tablas mDocumentoFolio,
                    // mRelPedContPrecomComp
                    sqlDoc = "delete mDocumentoFolio where cIdDocumentoDefinitivo='" + datosContrato.getcIdcontratoDefinitivo() + "' and ConsecutivoPRECOMP=" + nFolioPrecom;
                    log.info("Object: {}", sqlDoc.toString());
                    stmDoc = conn.createStatement();
                    stmDoc.executeUpdate(sqlDoc);
                    sqlRel = "delete mRelPedContPrecomComp where cIdPedContDef='" + datosContrato.getcIdcontratoDefinitivo() + "' and nConsecutivoPrecom=" + nFolioPrecom;
                    log.info("Object: {}", sqlRel.toString());
                    stmRel = conn.createStatement();
                    stmRel.executeUpdate(sqlRel);
                    // Guardaar en bitacora los movimientos
                    Util.bitacoraMovimientos(datosContrato.getcIdcontratoDefinitivo(), "Cancelación de precompromiso para contratos del capitulo 4000 con Folio=" + nFolioPrecom, usuario.getLogin(), conn);
                    // Recargando el caso
                    Caso scc = new Caso();
                    scc.setIdCaso(c.getIdCaso());
                    c = CasoManager.select(conn, scc);
                    // VENTANILLA_PRECOMPROMISO
                    String[] responsable = new String[] { "CONSULTA_PRECOMPROMISO" };
                    // autoriza_precomp
                    String[] nombre = new String[] { "consulta_precomp" };
                    // avanzaCaso(request, c, usuario, prefixPath, responsable,
                    // nombre);
                    Util.avanzaCaso(request, c, usuario, prefixPath, responsable, nombre, jndiName);
                    log.debug("Object: {}", c.getCasoDato("APLICADO_CONT").getValor());
                }
            }
            resp = true;
        } finally {
            cpu.cambiaCentroContableUE(ueOriginal, cCentroContableOrig, usuario);
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            rs = null;
            pstmt = null;
            m = null;
        }
        return resp;
    }

    public static boolean generaCompromiso(Connection conn, HttpServletRequest request, Usuario usuario, DatosContratoCap4 datosContrato, String jndiName, String prefixPath, boolean isAmpliacion) throws SQLException, ServletException, IOException, GestionException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        CambiaPropiedadesUsuario cpu = new CambiaPropiedadesUsuario();
        String ueOriginal = usuario.getU_UR();
        String cCentroContableOrig = usuario.getPropiedad("CCENTROCONTABLE").getValor();
        AplicarContableReturn acr = null;
        ContableInterface conInt = new AplicacionContable();
        boolean resp = false;
        String isRadicado = "N";
        Map<String, String> m = null;
        List<Integer> listFoliosComp = null;
        try {
            log.info("Query para obtener todos los precompromisos de un contrato. ");
            String query = "select pe.nFolioPreCompromiso,rpc.cFolioPrecom " + ",pe.cCentroContable " + ",cUnidadResponsable from tPreCompromisoEncabezado pe with(Nolock) " + "inner join mRelPedContPrecomComp as rpc with(Nolock) on pe.cIdContrato=rpc.cIdPedContDef " + "and pe.nFolioPreCompromiso=rpc.nConsecutivoPrecom and pe.cDocumentoHaplicado='S' and pe.cIdContrato=?";
            log.info("Object: {}", query.toString());
            if (datosContrato.getcCuentaDisponible().equals("82109")) {
                isRadicado = "S";
            }
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, datosContrato.getcIdcontratoDefinitivo());
            rs = pstmt.executeQuery();
            int nFolioPrecom;
            String folioComp;
            int folioCompromiso;
            int retVal = 0;
            String validaSaldo = "";
            Caso c = null, sc = null;
            listFoliosComp = new ArrayList<>();
            while (rs.next()) {
                cpu.cambiaCentroContableUE(rs.getString("cUnidadResponsable"), rs.getString("cCentroContable"), usuario);
                c = generaGuardaCaso(usuario.getU_UR(), (GestionInterface.IDTC_COMPROMISO + ""), "Aplicación de Compromiso", usuario, jndiName, datosContrato.getcEjercicio());
                if (c == null) {
                    // mensaje="NO SE PUDO GENERAR EL FOLIO DE COMPROMISO
                    // INTENTE NUEVAMENTE";
                    log.warn("Error al crear el caso.");
                    return resp;
                } else {
                    // obtiene folio de compromiso
                    folioComp = c.getFolio();
                    nFolioPrecom = rs.getInt("nFolioPreCompromiso");
                    folioCompromiso = Integer.parseInt(folioComp.substring(folioComp.lastIndexOf('-') + 1));
                    listFoliosComp.add(folioCompromiso);
                    // Crear encabezado y detalle
                    if (isAmpliacion) {
                        retVal = creaEncabezadoDetalleAmpComp(datosContrato.getcIdcontratoDefinitivo(), nFolioPrecom, folioCompromiso, isRadicado, folioComp, usuario, conn);
                    } else {
                        retVal = creaEncabezadoDetalleComp(datosContrato.getcIdcontratoDefinitivo(), nFolioPrecom, folioCompromiso, isRadicado, folioComp, usuario, conn);
                    }
                    if (retVal != 0) {
                        log.warn("Error al crear el encabezado y detlle.");
                        return resp;
                    }
                    // Aplicación contable
                    if (!esRecursoFiscal(conn, datosContrato.getcIdcontratoDefinitivo())) {
                        m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                        acr = conInt.aplicarContableNuevo(conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), validaSaldo);
                        if (!acr.isSuccess()) {
                            log.warn("Error en la aplicación contable.");
                            return resp;
                        }
                    }
                    // Guardaar en bitacora los movimientos
                    bitacoraMovimientos(datosContrato.getcIdcontratoDefinitivo(), "Generación de compromiso para contratos del capitulo 4000 con Folio=" + folioCompromiso, usuario.getLogin(), conn);
                    // Recargando el caso
                    sc = new Caso();
                    sc.setIdCaso(c.getIdCaso());
                    c = CasoManager.select(conn, sc);
                    // Una vez que ha hecho la aplicación contable avanza el
                    // caso
                    avanzaCaso(request, c, usuario, prefixPath, new String[] { "CONSULTA_PAGOS" }, new String[] { "consulta_compromiso" }, jndiName);
                    log.debug("Object: {}", c.getCasoDato("APLICADO_CONT").getValor());
                }
            }
            if (listFoliosComp.size() > 1) {
                integraFoliosCompromiso(conn, datosContrato.getcIdcontratoDefinitivo(), datosContrato.getcEjercicio());
            }
            resp = true;
        } finally {
            cpu.cambiaCentroContableUE(ueOriginal, cCentroContableOrig, usuario);
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (listFoliosComp != null)
                listFoliosComp.clear();
            listFoliosComp = null;
            pstmt = null;
            rs = null;
            m = null;
        }
        return resp;
    }

    public static int creaEncabezadoDetalleAmpComp(String cIdContratoDefinitivo, int nFolioPrecom, int nFolioComp, String isRadicado, String folioCasoCompromiso, Usuario usuario, Connection conn) throws SQLException {
        Statement stmEnc = null, stmDet = null, stmPrecom = null;
        int retval = -1;
        try {
            // Encabezado
            String query = "insert into tCompromisoEncabezado (nFolioCompromiso,fCarga,cIdContrato,cTipoContrato,fAplicacion,cCentroContable,cRamo" + ",cUnidadResponsable,cDocumentoHaplicado,nFolioPoliza,caNoCompromiso,nEnviadoSICOP,cTipoPoliza,nMes,cRevisado,aEjercicioFiscal,cUnidadResponsableContable,nFolioPolizaCancelacion,fCancelacion " + " ,cDescripcionPoliza,usuario,cRadicado) " + "select " + nFolioComp + ",fCarga,substring(cIdContrato,1,patindex('%/%' , cIdContrato)+4),cTipoContrato,fAplicacion,cCentroContable, " + " cRamo,cUnidadResponsable,null,null,caNoPreCompromiso,nEnviadoSICOP,cTipoPoliza,nMes,cRevisado,aEjercicioFiscal,cUnidadResponsableContable, " + " nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza,'" + usuario.getLogin() + "','" + isRadicado + "' from tPreCompromisoEncabezado with(nolock) " + " where nFolioPreCompromiso=" + nFolioPrecom + " and cIdContrato='" + cIdContratoDefinitivo + "'";
            log.info("Object: {}", query.toString());
            stmEnc = conn.createStatement();
            stmEnc.executeUpdate(query);
            // Detalle
            String sql = "insert into tCompromisoDetalle (nFolioCompromiso,nDocRenglon,EP,cEvento,mImporte,mImporteNegativo,cMes,cCentroContable) select " + nFolioComp + ",nDocRenglon,EP,'CMP002',convert(money,str(mImporte,15,2))as mImporte, " + " convert(money,str(mImporteNegativo,15,2))as mImporteNegativo,cMes,cCentroContable from tPreCompromisoDetalle with(nolock) " + " where nFolioPreCompromiso=" + nFolioPrecom;
            log.info("Object: {}", sql.toString());
            stmDet = conn.createStatement();
            stmDet.executeUpdate(sql);
            // Actualiza tprecompromiso
            String sqlPrecom = "update tPreCompromisoEncabezado set C_FOLIO_COMP='" + folioCasoCompromiso + "', ConsecutivoCOMP=" + nFolioComp + ",nStatusFinanciero=1 " + " where cIdContrato='" + cIdContratoDefinitivo + "' and nFolioPreCompromiso=" + nFolioPrecom;
            log.info("Object: {}", sqlPrecom.toString());
            stmPrecom = conn.createStatement();
            stmPrecom.executeUpdate(sqlPrecom);
            retval = 0;
        } finally {
            if (stmEnc != null) {
                stmEnc.close();
            }
            if (stmDet != null) {
                stmDet.close();
            }
            if (stmPrecom != null) {
                stmPrecom.close();
            }
            stmEnc = null;
            stmDet = null;
            stmPrecom = null;
        }
        return retval;
    }

    public static int obtieneFolio(Connection conn, String query) throws SQLException {
        int folio = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            log.info("Object: {}", query.toString());
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                folio = rs.getInt(1);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            rs = null;
            pstmt = null;
        }
        return folio;
    }

    public static String obtieneEjercicioFiscalActivo(Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String cEjercicio = "";
        try {
            String query = "select *from tEjercicioFiscal with(Nolock) where cActivo=1";
            log.info("Object: {}", query.toString());
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                cEjercicio = rs.getString("aEjercicioFiscal");
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            rs = null;
            pstmt = null;
        }
        return cEjercicio;
    }

    public static String getNameDB(Connection conn, int cEjercicio) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String cNameDB = "";
        try {
            String query = "select *from tEjercicioFiscal with(Nolock) where aEjercicioFiscal=" + cEjercicio;
            log.info("Object: {}", query.toString());
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                cNameDB = rs.getString("cNombreBD");
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            rs = null;
            pstmt = null;
        }
        return cNameDB;
    }

    public static double obtieneDato(Connection conn, String query) throws SQLException {
        double folio = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            log.info("Object: {}", query.toString());
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                folio = rs.getDouble(1);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            rs = null;
            pstmt = null;
        }
        return folio;
    }

    public static String obtieneDatoCadena(Connection conn, String query) throws SQLException {
        String cadena = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            log.info("Object: {}", query.toString());
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                cadena = rs.getString(1);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            rs = null;
            pstmt = null;
        }
        return cadena;
    }

    public static Respuesta HayDocumentos(Connection conn, String tituloAplicacion, String cFolio, String cadenaNameDocto) throws Exception, SQLException {
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        Respuesta resp = new Respuesta();
        String msg = "";
        boolean faltaDoc = true;
        String token = "Falta Adjuntar Documentaci\u00f3n Comprobatoria:\n";
        String quey = "SELECT NOMBRE_DOCUMENTO,NUMERO_PAGINAS FROM IMX_DOCUMENTO WITH(NOLOCK) WHERE TITULO_APLICACION='" + tituloAplicacion + "' AND ID_GABINETE=(SELECT ID_GABINETE FROM IMX_DOCUMENTO WITH(NOLOCK)WHERE TITULO_APLICACION='" + tituloAplicacion + "' AND NOMBRE_DOCUMENTO ='" + cFolio + "')  AND NOMBRE_DOCUMENTO in(" + cadenaNameDocto + ")";
        pstmt = conn.prepareStatement(quey);
        rs = pstmt.executeQuery();
        while (rs.next()) {
            if (rs.getInt("NUMERO_PAGINAS") == 0) {
                faltaDoc = false;
                msg = msg + token + rs.getString("NOMBRE_DOCUMENTO");
                token = "\n";
            }
        }
        resp.setMsg(msg);
        resp.setResp(faltaDoc);
        return resp;
    }

    public static boolean validaEmail(String email) {
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher mather = pattern.matcher(email);
        return mather.find();
    }

    public static String getNameMonth(int numMonth) {
        String name = "";
        switch(numMonth) {
            case 1:
                {
                    name = "Enero";
                    break;
                }
            case 2:
                {
                    name = "Febrero";
                    break;
                }
            case 3:
                {
                    name = "Marzo";
                    break;
                }
            case 4:
                {
                    name = "Abril";
                    break;
                }
            case 5:
                {
                    name = "Mayo";
                    break;
                }
            case 6:
                {
                    name = "Junio";
                    break;
                }
            case 7:
                {
                    name = "Julio";
                    break;
                }
            case 8:
                {
                    name = "Agosto";
                    break;
                }
            case 9:
                {
                    name = "Septiembre";
                    break;
                }
            case 10:
                {
                    name = "Octubre";
                    break;
                }
            case 11:
                {
                    name = "Noviembre";
                    break;
                }
            case 12:
                {
                    name = "Diciembre";
                    break;
                }
            default:
                {
                    name = "Error al calcular el nombre del mes";
                    break;
                }
        }
        return name;
    }

    public static int folio(Caso c) {
        return Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
    }

    public static JSONArray toJSONArray(List<Map<String, String>> resumen) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < resumen.size(); i++) {
            Map<String, String> objMap = resumen.get(i);
            JSONObject object = new JSONObject(objMap);
            array.put(object);
        }
        return array;
    }

    public static boolean validaRoleUsuario(Map<String, Role> rol) throws Exception {
        boolean resp = false;
        Iterator it1 = rol.entrySet().iterator();
        while (it1.hasNext()) {
            Map.Entry<String, String> r = (Map.Entry) it1.next();
            if ("ADMIN_RECMAT".equalsIgnoreCase(r.getKey().toString()) || "ANALISTA".equalsIgnoreCase(r.getKey().toString()) || "JEFES".equalsIgnoreCase(r.getKey().toString())) {
                resp = true;
            }
        }
        return resp;
    }

    public static double redondearDecimales(double valorInicial, int numeroDecimales) {
        double parteEntera, resultado;
        resultado = valorInicial;
        parteEntera = Math.floor(resultado);
        resultado = (resultado - parteEntera) * Math.pow(10, numeroDecimales);
        resultado = Math.round(resultado);
        resultado = (resultado / Math.pow(10, numeroDecimales)) + parteEntera;
        return resultado;
    }

    public static JSONArray obtieneDatQuery(Connection conn, String query) throws SQLException, JSONException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        JSONArray arrayObj = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        try {
            log.info("Object: {}", query.toString());
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            int token = 0;
            while (rs.next()) {
                if (token > 0)
                    jsonObj = new JSONObject();
                jsonObj.put("Descripcion", rs.getString(1));
                jsonObj.put("Id", rs.getString(2));
                token++;
                arrayObj.put(jsonObj);
                jsonObj = null;
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        } catch (JSONException e1) {
            throw new JSONException(e1);
        } finally {
            if (pstmt != null)
                pstmt.close();
            if (rs != null)
                rs.close();
            pstmt = null;
            rs = null;
        }
        return arrayObj;
    }

    public static JSONArray datGuardados(Connection conn, String query) throws SQLException, JSONException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsMetadata = null;
        JSONArray arrayObj = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        try {
            log.info("Object: {}", query.toString());
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            rsMetadata = rs.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            if (rs.next()) {
                jsonObj.put("HAYINFO", true);
                for (int i = 1; i <= totalcolumnas; i++) jsonObj.put(rsMetadata.getColumnName(i), rs.getString(i));
            } else {
                jsonObj.put("HAYINFO", false);
            }
            arrayObj.put(jsonObj);
        } finally {
            jsonObj = null;
            if (pstmt != null)
                pstmt.close();
            if (rs != null)
                rs.close();
            rsMetadata = null;
            pstmt = null;
            rs = null;
        }
        return arrayObj;
    }

    public static JSONArray datGuardadosTable(Connection conn, String query) throws SQLException, JSONException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsMetadata = null;
        JSONArray arrayObj = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        try {
            log.info("Object: {}", query.toString());
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            rsMetadata = rs.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= totalcolumnas; i++) {
                    jsonObj.put(rsMetadata.getColumnName(i), rs.getString(i));
                }
                arrayObj.put(jsonObj);
                jsonObj = null;
                jsonObj = new JSONObject();
            }
            arrayObj.put(jsonObj);
        } finally {
            jsonObj = null;
            if (pstmt != null)
                pstmt.close();
            if (rs != null)
                rs.close();
            rsMetadata = null;
            pstmt = null;
            rs = null;
        }
        return arrayObj;
    }

    public static Cell createExcelCellRep(int index, Row fila, ResultSet rs, String cellName, int tipoDato) throws Exception {
        Cell cell = (fila.getCell(index) == null ? fila.createCell(index) : fila.getCell(index));
        if (tipoDato == Types.BIGINT || tipoDato == Types.BIT || tipoDato == Types.INTEGER || tipoDato == Types.SMALLINT || tipoDato == Types.TINYINT) {
            // Tipos de dato enteros
            int val = rs.getInt(cellName);
            cell.setCellValue(val);
            return cell;
        } else if (tipoDato == Types.DECIMAL || tipoDato == Types.DOUBLE || tipoDato == Types.FLOAT || tipoDato == Types.NUMERIC || tipoDato == Types.REAL) {
            // Tipos de dato reales
            double val = rs.getDouble(cellName);
            cell.setCellValue(val);
            return cell;
        } else if (tipoDato == Types.DATE || tipoDato == Types.TIME || tipoDato == Types.TIMESTAMP) {
            if (rs.getDate(cellName) != null) {
                // Tipo de dato fecha
                Date d = new Date(rs.getDate(cellName).getTime());
                cell.setCellValue(d);
            }
            return cell;
        } else {
            String val = rs.getString(cellName);
            cell.setCellValue(val);
            return cell;
        }
    }

    public static String getSystemSetting(Connection conn, String settingName) throws Exception {
        String query = "select gp_valor from CG_GRUPO_PROPIEDADES with(nolock) where G_NOMBRE = 'PREFERENCIAS_CLIENTE' and GP_NOMBRE = ?";
        ResultSet rs = null;
        PreparedStatement ps = null;
        String val = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, settingName);
            rs = ps.executeQuery();
            if (rs.next())
                val = rs.getString(1);
            return val;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static String generaAccessoAutToken(int numeroEmpleado, String document, int folio) {
        StringBuffer parametrosReales = null;
        /*
		 * Concatena los parametros. El separador sera el caracter | (pipe)
		 */
        parametrosReales = new StringBuffer("?");
        parametrosReales.append("u=").append(StringUtils.reverse(String.valueOf(numeroEmpleado)));
        parametrosReales.append("&");
        parametrosReales.append("d=").append(String.valueOf(document));
        parametrosReales.append("&");
        parametrosReales.append("f=").append(StringUtils.reverse(String.valueOf(folio)));
        log.debug("Object: {}", "Cadena generada: " + parametrosReales);
        return parametrosReales.toString();
    }

    public static boolean updateRelacionPrecomCompromiso(Connection conn, String cFolioCompromiso, int nFolioCompromiso, String cIdContratoDefinitivo, String cFolioPreCompromiso, int nFolioPreCompromiso) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update mRelPedContPrecomComp set cFolioComp=?,nConsecutivoComp=? where cIdPedContDef=? and cFolioPrecom=? and nConsecutivoPrecom=?");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cFolioCompromiso);
            ps.setInt(2, nFolioCompromiso);
            ps.setString(3, cIdContratoDefinitivo);
            ps.setString(4, cFolioPreCompromiso);
            ps.setInt(5, nFolioPreCompromiso);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query.delete(0, query.length());
            query = null;
        }
        return success;
    }

    public static void cambiaCentroContableUE(String UE, String centroContable, Usuario u) throws Exception {
        Map<String, UsuarioPropiedades> propiedades = null;
        try {
            propiedades = u.getPropiedades();
            UsuarioPropiedades up = (UsuarioPropiedades) propiedades.get("CCENTROCONTABLE");
            log.info("Object: {}", "Las propiedades del usuario cambiarón. UnidadEjecutora=" + UE + " y su centroContable=" + centroContable);
            up.setValor(centroContable);
            u.setU_UR(UE);
            u.setPropiedad("CCENTROCONTABLE", up);
        } finally {
            propiedades = null;
        }
    }
}
