package com.syc.contable;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.syc.sai.contabilidad.ClasePlurianualEp;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase manager encargada de la carga de proyecto desde un archivo excel.
 *
 * @author Vicente Garcia Carrillo
 * @version 1.0
 */
public class ContratoPlurianualesManager {

    private static Logger log = LoggerFactory.getLogger(ContratoPlurianualesManager.class);

    /**
     * Inserta un renglon del proyecto
     *
     * @param conn
     *            Conexion activa a la base de datos
     * @param infoRenglon
     *            Informacion del renglon
     * @return Numero de registros insertados
     * @throws Exception
     */
    public static int insertaRenglon(Connection conn, Map<String, String> infoRenglon, String sTabla) throws Exception {
        Statement stmnt = null;
        int r = 0;
        try {
            //	log.debug("Iniciando insercion de renglon");
            stmnt = conn.createStatement();
            r = stmnt.executeUpdate(Util.genInsertFromMap(sTabla, infoRenglon));
            log.trace("Se inserto Exitosamente en " + sTabla);
            return r;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.debug("Error: problemas al leer archivo funcion: ContratoPlurianualesManager.insertaRenglonProyecto" + r);
            throw e;
        } finally {
            CloseObject.closeObject(stmnt, false);
        }
    }

    public int EliminaContratoPlurianual_EP(Connection conn, String cFolio) throws Exception {
        Statement stmnt = null;
        int r = 0;
        try {
            log.trace("Limpiando tablas tContratoPlurianual_EP");
            stmnt = conn.createStatement();
            r = stmnt.executeUpdate("delete tContratoPlurianual_EP where nFolioContratoPlurianual ='" + cFolio + "'");
            log.trace("Se eliminaron registros  de tContratoPlurianual_EP con folio = " + cFolio + " Exitosamente ");
            return r;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.debug("Error: problemas al leer archivo funcion: ContratoPlurianualesManager.EliminaContratoPlurianual_EP" + r);
            throw e;
        } finally {
            CloseObject.closeObject(stmnt, false);
        }
    }

    public int fn_EliminaContratoPlurianualEncabezado(Connection conn, String cFolio) throws Exception {
        Statement stmnt = null;
        int r = 0;
        try {
            log.trace("Limpiando tablas tContratoPlurianualEncabezado");
            stmnt = conn.createStatement();
            r = stmnt.executeUpdate("delete tContratoPlurianualEncabezado where nFolioContratoPlurianual ='" + cFolio + "'");
            log.trace("Se eliminaron registros  de tContratoPlurianualEncabezado con folio = " + cFolio + " Exitosamente ");
            return r;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.debug("Error: problemas al leer archivo funcion: ContratoPlurianualesManager.fn_EliminaContratoPlurianualEncabezado" + r);
            throw e;
        } finally {
            CloseObject.closeObject(stmnt, false);
        }
    }

    public int fn_EliminaContratoPlurianualDetalle(Connection conn, String cFolio) throws Exception {
        Statement stmnt = null;
        int r = 0;
        try {
            log.trace("Limpiando tablas tContratoPlurianual_EP");
            stmnt = conn.createStatement();
            r = stmnt.executeUpdate("delete tContratoPlurianualDetalle where nFolioContratoPlurianual ='" + cFolio + "'");
            log.trace("Se eliminaron registros  de tContratoPlurianualDetalle con folio = " + cFolio + " Exitosamente ");
            return r;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.debug("Error: problemas al leer archivo funcion: ContratoPlurianualesManager.fn_EliminaContratoPlurianualDetalle" + r);
            throw e;
        } finally {
            CloseObject.closeObject(stmnt, false);
        }
    }

    public int fn_EliminatContratoPlurianualMontosAnuales(Connection conn, String cFolio) throws Exception {
        Statement stmnt = null;
        int r = 0;
        try {
            log.trace("Limpiando tablas tContratoPlurianualMontosAnuales");
            stmnt = conn.createStatement();
            r = stmnt.executeUpdate("delete tContratoPlurianualMontosAnuales where nFolioContratoPlurianual =" + cFolio);
            log.trace("Se eliminaron registros  de tContratoPlurianualMontosAnuales con folio = " + cFolio + " Exitosamente ");
            return r;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.debug("Error: problemas al leer archivo funcion: ContratoPlurianualesManager.tContratoPlurianualMontosAnuales" + r);
            throw e;
        } finally {
            CloseObject.closeObject(stmnt, false);
        }
    }

    public static int fn_ValidaEp(Connection conn, String Ep) throws Exception {
        PreparedStatement stmnt = null;
        ResultSet rs = null;
        int total = 0;
        try {
            log.trace("Validando Ep:" + Ep);
            stmnt = conn.prepareStatement("select COUNT(*) as Existe from tCatalogoEP with(nolock) where EP = ? ");
            stmnt.setString(1, Ep);
            rs = stmnt.executeQuery();
            if (rs.next())
                total = rs.getInt(1);
            else
                total = 0;
        } catch (Exception exc) {
            throw new RuntimeException("Error al consultar : ContratoPlurianualesManager.fn_ValidaEp " + exc);
        }
        return total;
    }

    public static int fn_ValidaExisteRenglon(Connection conn, String cFolio, String sAnio) throws Exception {
        PreparedStatement stmnt = null;
        ResultSet rs = null;
        int total = 0;
        try {
            //log.trace("Validando Ep:" + Ep);
            stmnt = conn.prepareStatement("select COUNT(*) as Existe from tContratoPlurianualMontosAnuales with(nolock) where nFolioContratoPlurianual = ?  and  anio = ? ");
            stmnt.setString(1, cFolio);
            stmnt.setString(2, sAnio);
            rs = stmnt.executeQuery();
            if (rs.next())
                total = rs.getInt(1);
            else
                total = 0;
        } catch (Exception exc) {
            throw new RuntimeException("Error al consultar : ContratoPlurianualesManager.fn_ValidaExisteRenglon " + exc);
        }
        return total;
    }

    public static LinkedList<ClasePlurianualEp> fn_LeeEP_paraApartado(Connection conn, String cFolio, String sAnio, String nModificacion) throws Exception {
        LinkedList<ClasePlurianualEp> listaEP = new LinkedList<ClasePlurianualEp>();
        PreparedStatement stmnt = null;
        ResultSet rs = null;
        String sql = "Select nFolioContratoPlurianual, ep,CASE WHEN [bValidaContraMinimo] = 0 THEN [nImporteMaximo] ELSE [nImporteMinimo] END nImporte, nImporteModificado, Construccion, Supervision  " + "from tContratoPlurianual_EP   with(nolock) where nFolioContratoPlurianual = ? and ciclo = ? order by nImporte, ep ";
        try {
            log.trace("Ejecutando ContratoPlurianualesManager.fn_LeeEP_paraApartado  ");
            stmnt = conn.prepareStatement(sql);
            stmnt.setString(1, cFolio);
            stmnt.setString(2, sAnio);
            rs = stmnt.executeQuery();
            int contador = 0;
            while (rs.next()) {
                ClasePlurianualEp obtenEp = new ClasePlurianualEp();
                obtenEp.setnFolioContratoPlurianual(rs.getString("nFolioContratoPlurianual"));
                obtenEp.setep(rs.getString("ep"));
                if (!"0".equals(nModificacion) && !"".equals(nModificacion) && nModificacion != null) {
                    ContratoPlurianualBusinessLogic businessLogic = new ContratoPlurianualBusinessLogic(null);
                    double montoUltimaMod = businessLogic.getDiferenciaMontos(Integer.parseInt(cFolio), Integer.parseInt(sAnio), Integer.parseInt(nModificacion)).get(contador);
                    obtenEp.setnImporte(montoUltimaMod);
                    contador++;
                } else {
                    obtenEp.setnImporte(rs.getDouble("nImporte"));
                }
                obtenEp.setnImporteModificado(rs.getDouble("nImporteModificado"));
                obtenEp.setConstruccion(rs.getDouble("Construccion"));
                obtenEp.setSupervision(rs.getDouble("Supervision"));
                listaEP.add(obtenEp);
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            throw new RuntimeException("Error al consultar : ContratoPlurianualesManager.fn_LeeEP_paraApartado " + exc);
        }
        return listaEP;
    }

    public Double ConsultaDisponiblexMes(Connection conn, String sMes, String sEp, String sCuenta) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Double dTotal = 0.0;
        String sql = "	SELECT isnull(sum(" + sMes + "),0)  AS total  FROM   vsaldosanuales  WITH(NOLOCK)  WHERE  ep =   '" + sEp + "'   AND  nCuentaP = '" + sCuenta + "' ";
        try {
            log.trace("ConsultaDisponiblexMes");
            ps = conn.prepareStatement(sql);
            //.setString(1, sMes);
            //ps.setString(2, sEp);
            //ps.setString(3, sCuenta);
            rs = ps.executeQuery();
            while (rs.next()) {
                dTotal = rs.getDouble(1);
            }
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception exc) {
                log.warn("Cerrando ResultSet", exc);
            }
        }
        return dTotal;
    }

    @SuppressWarnings("finally")
    public static int ConsultaConsecutivo(Connection conn, String sFolio) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int cons = 0;
        String sql = "  select isnull(MAX(nDocRenglon),0) as consecutivo from tContratoPlurianualApartado with(nolock) where  nFolioContratoPlurianual = " + sFolio;
        try {
            log.trace("ConsultaConsecutivo ");
            ps = conn.prepareStatement(sql);
            //	ps.setInt(1,iAnio);
            //	ps.setString(2, sFolio);
            rs = ps.executeQuery();
            if (rs.next()) {
                cons = rs.getInt(1) + 1;
            }
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception exc) {
                log.warn("Cerrando ResultSet", exc);
            }
            return cons;
        }
    }

    public static boolean InsertContratoPlurianualApartado(Connection conn, String sFolio, int iConsecutivo, String sEp, String sAnio, String sEvento, String sImporte, String sImporteNeg, String sCC, String sMes, String cMod) throws SQLException {
        PreparedStatement pstm = null;
        boolean res = true;
        try {
            String sql = "";
            log.debug("insercion de renglon en funcion InsertContratoPlurianualApartado");
            sql = "INSERT INTO tContratoPlurianualApartado(nFolioContratoPlurianual, nDocRenglon, EP, Anio, cEvento, mImporte, mImporteNegativo, cCentroContable, cMes, mSaldoModificado, fRegistro)VALUES(" + sFolio + "," + String.valueOf(iConsecutivo) + ",'" + sEp + "'," + sAnio + ",'" + sEvento + "'," + sImporte + "," + sImporteNeg + "," + sCC + "," + sMes + "," + cMod + " , GETDATE() )";
            log.debug(sql);
            pstm = conn.prepareStatement(sql);
            pstm.execute();
            res = pstm.getUpdateCount() != 0 ? true : false;
        } catch (Exception e) {
            log.warn("Ocurrio un Error en funcion InsertContratoPlurianualApartado : ", e);
            res = false;
            //conn.rollback();
        }
        return res;
    }

    public static boolean fn_updateEp(Connection conn, String sFolio, String sEP, double nConst, double nSup) throws SQLException {
        PreparedStatement pstm = null;
        boolean res = true;
        try {
            String sql = "";
            sql = "update  tContratoPlurianual_ep  " + "set  Construccion = " + String.valueOf(nConst) + ", " + "Supervision = " + String.valueOf(nSup) + " where  nFolioContratoPlurianual = " + sFolio + " and ep = '" + sEP + "'";
            pstm = conn.prepareStatement(sql);
            pstm.execute();
            res = pstm.getUpdateCount() != 0 ? true : false;
        } catch (Exception e) {
            res = false;
            //conn.rollback();
        }
        return res;
    }

    public static void AbrirCerrar(Connection conn, boolean pluriNormal, boolean pluriEspecial, String usuario) throws SQLException {
        PreparedStatement pstmntUp = null;
        try {
            pstmntUp = conn.prepareStatement("UPDATE tContratoPlurianual_Bloqueos SET Actual = 0 ");
            pstmntUp.execute();
            pstmntUp = conn.prepareStatement("INSERT INTO tContratoPlurianual_Bloqueos VALUES(?,?,1,?,GETDATE())");
            pstmntUp.setBoolean(1, pluriNormal);
            pstmntUp.setBoolean(2, pluriEspecial);
            pstmntUp.setString(3, usuario);
            pstmntUp.execute();
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            CloseObject.closeObject(pstmntUp);
        }
    }

    public static String CancelacionMasiva(Connection conn, String usuario, HttpServletRequest request) throws SQLException, GestionException {
        PreparedStatement pstmntUp = null;
        ResultSet rs = null;
        Boolean todoBien;
        String mensaje = "";
        String foliosCancelados = "";
        try {
            pstmntUp = conn.prepareStatement("" + "SELECT DISTINCT CGCO.ID_CASO AS id_caso, CGCO.ID_CASO_OPER AS id_caso_oper, TPA.nFolioPoliza, TPA.nFolioContratoPlurianual,TPA.cFolioSai  " + "FROM CG_CASO_OPERACION AS CGCO (NOLOCK)" + "	 INNER JOIN CG_CASO AS CGC (NOLOCK)" + "	 ON CGCO.ID_CASO = CGC.ID_CASO AND " + "		CGCO.ID_TC = CGC.ID_TC " + "	 INNER JOIN tContratoPlurianualEncabezado AS TPA (NOLOCK)" + "	 ON CGC.C_FOLIO = TPA.cFolioSai AND " + "		TPA.cDocumentoHaplicado = 'S' AND  " + "		YEAR(GETDATE()) = TPA.aEjercicioFiscal " + "	 INNER JOIN tContratoPlurianualApartado AS TPAD (NOLOCK)" + "	 ON TPA.nFolioContratoPlurianual = TPAD.nFolioContratoPlurianual AND  " + "		MONTH(GETDATE()) >= TPAD.cMes " + "WHERE CGCO.ID_TC = 28 AND  " + "	  CGCO.ID_OPER IN(3, 4) AND  " + "	  TPA.CTIPOSOLICITUD = 'PLU' ");
            rs = pstmntUp.executeQuery();
            todoBien = true;
            while (rs.next()) {
                AplicarContableReturn acr = null;
                ContableInterface conInt = new AplicacionContable();
                ArrayList<String> arrLResult = new ArrayList<String>();
                //CasoBusinessLogic CBL = new CasoBusinessLogic();
                Integer nFolioPoliza;
                Caso srchCase = new Caso();
                srchCase.setIdCaso(rs.getInt("id_caso"));
                Caso c = CasoManager.select(conn, srchCase, rs.getInt("id_caso_oper"));
                nFolioPoliza = rs.getInt("nFolioPoliza");
                int nFolioPluranual = rs.getInt("nFolioContratoPlurianual");
                String cFolio = rs.getString("cFolioSai");
                Map<String, String> m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                acr = conInt.cancelarAppContableNueva(conn, c, "tContratoPlurianualEncabezado", "tContratoPlurianualApartado", "nFolioContratoPlurianual", nFolioPoliza, "PLURIANUALPRE", m, "/WEB-INF/mail-bodies/" + File.separator, usuario, "");
                final String[] resp = "REVISOR_PLURIANUALPRE".split(";");
                final String[] oper = "revisa_plurianualpre".split(";");
                CasoBusinessLogic casoTx = new CasoBusinessLogic(null);
                String prefixPath = "/WEB-INF/mail-bodies/" + File.separator;
                arrLResult = (ArrayList<String>) acr.getMessageList();
                if (acr.isSuccess()) {
                    todoBien = todoBien && true;
                    conn.commit();
                    if (!respaldaEncabezado(nFolioPluranual) | !respaldaDetalle(nFolioPluranual) | !quitaPolizas(nFolioPluranual)) {
                        todoBien = false;
                        break;
                    }
                    casoTx.avanzaCaso(c, usuario, "", resp, oper, m, prefixPath);
                    foliosCancelados = foliosCancelados + cFolio + ",";
                } else {
                    mensaje += "El folio de poliza " + String.valueOf(nFolioPoliza) + " no se pudo cancelar. \n";
                    todoBien = todoBien && false;
                }
            }
            if (todoBien) {
                mensaje = "Se cancelaron correctamente los siguientes folios: " + foliosCancelados;
            } else {
                conn.rollback();
            }
        } catch (Exception e) {
            mensaje = "Ocurrio algo inesperado consulte al administrador: " + e.getMessage();
        } finally {
            if (pstmntUp != null)
                try {
                    pstmntUp.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            pstmntUp = null;
        }
        return mensaje;
    }

    private static boolean quitaPolizas(Integer nFolioPoliza) throws SQLException {
        PreparedStatement stmnt = null;
        String query;
        boolean respuesta = false;
        Connection conn = null;
        try {
            ContratoPlurianualBusinessLogic businessLogic = new ContratoPlurianualBusinessLogic(null);
            conn = businessLogic.getConnection();
            query = "update tContratoPlurianualEncabezado set  cDocumentoHaplicado='N', nFolioPolizaCancelacion = NULL,nFolioPoliza=NULL where nFolioContratoPlurianual =" + nFolioPoliza;
            stmnt = conn.prepareStatement(query);
            stmnt.executeUpdate();
            respuesta = true;
            conn.commit();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (stmnt != null) {
                    stmnt.close();
                }
            } catch (SQLException e) {
                log.info(e);
            }
        }
        return respuesta;
    }

    private static boolean respaldaEncabezado(Integer nFolioPoliza) throws SQLException {
        PreparedStatement stmnt = null;
        String query;
        boolean respuesta = false;
        Connection conn = null;
        try {
            ContratoPlurianualBusinessLogic businessLogic = new ContratoPlurianualBusinessLogic(null);
            conn = businessLogic.getConnection();
            query = "insert into tContratoPlurianualEncabezadoCanAut select *  from tContratoPlurianualEncabezado with(nolock) where nFolioContratoPlurianual = " + nFolioPoliza;
            stmnt = conn.prepareStatement(query);
            stmnt.executeUpdate();
            respuesta = true;
            conn.commit();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (stmnt != null) {
                    stmnt.close();
                }
            } catch (SQLException e) {
                log.info(e);
            }
        }
        return respuesta;
    }

    private static boolean respaldaDetalle(Integer nFolioPoliza) throws SQLException {
        PreparedStatement stmnt = null;
        String query;
        boolean respuesta = false;
        Connection conn = null;
        try {
            ContratoPlurianualBusinessLogic businessLogic = new ContratoPlurianualBusinessLogic(null);
            conn = businessLogic.getConnection();
            query = "insert into tContratoPlurianualApartadoCanAut select *  from tContratoPlurianualApartado  with(nolock) where nFolioContratoPlurianual = " + nFolioPoliza;
            stmnt = conn.prepareStatement(query);
            stmnt.executeUpdate();
            respuesta = true;
            conn.commit();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (stmnt != null) {
                    stmnt.close();
                }
            } catch (SQLException e) {
                log.info(e);
            }
        }
        return respuesta;
    }

    public int fn_ValidaEpAnteproyecto(Connection conn, String Ep, Integer Anio) {
        PreparedStatement stmnt = null;
        ResultSet rs = null;
        int total = 0;
        String query;
        try {
            log.trace("Validando Ep:" + Ep);
            query = "SELECT COUNT(*) as Existe FROM sai_" + String.valueOf(Anio) + ".dbo.tProyecto with(nolock) WHERE EP = ? ";
            stmnt = conn.prepareStatement(query);
            stmnt.setString(1, Ep);
            rs = stmnt.executeQuery();
            if (rs.next())
                total = rs.getInt(1);
            else
                total = 0;
        } catch (Exception exc) {
            throw new RuntimeException("Error al consultar : ContratoPlurianualesManager.fn_ValidaEpAnteproyecto " + exc);
        }
        return total;
    }

    public double ConsultaAnteproyecto(Connection conn, String Ep, Integer Anio) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        //Integer iMax = 0;
        Double dTotal = 0.0;
        Double dApartado = 0.0;
        String sql;
        try {
            log.trace("ConsultaSaldoAnteproyecto");
            sql = "SELECT ISNULL(MAX(monto),0)  AS total FROM sai_" + String.valueOf(Anio) + ".dbo.tProyecto WITH(NOLOCK) WHERE EP=? ";
            ps = conn.prepareStatement(sql);
            ps.setString(1, Ep);
            rs = ps.executeQuery();
            while (rs.next()) {
                dTotal = rs.getDouble(1);
            }
            rs.close();
            ps.close();
            sql = "SELECT ISNULL(sum(CASE WHEN [bValidaContraMinimo] = 0 THEN [nImporteMaximo] ELSE [nImporteMinimo] END),0)  AS total FROM tContratoPlurianual_EP WITH(NOLOCK) WHERE ep = ? ";
            ps = conn.prepareStatement(sql);
            ps.setString(1, Ep);
            rs = ps.executeQuery();
            while (rs.next()) {
                dApartado = rs.getDouble(1);
            }
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
        return dTotal - dApartado;
    }

    public List<String> ConsultaModificaciones(Connection conn, String Folio) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql;
        List<String> nModificaciones = new ArrayList<String>();
        try {
            log.trace("Consulta Modificaciones");
            sql = "    SELECT nModificacion FROM vContratoPlurianualEncabezado WITH(NOLOCK) WHERE nFolioContratoPlurianual=? ORDER BY nModificacion  ";
            ps = conn.prepareStatement(sql);
            ps.setString(1, Folio);
            rs = ps.executeQuery();
            while (rs.next()) {
                nModificaciones.add(String.valueOf(rs.getInt(1)));
            }
            rs.close();
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception exc) {
                log.warn("Cerrando ResultSet", exc);
            }
        }
        return nModificaciones;
    }

    public String ConsultaAñoFinal(Connection conn, String Folio) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql;
        String año = "";
        try {
            sql = "select MAX(ciclo) from tContratoPlurianual_EP where nFolioContratoPlurianual=? ";
            ps = conn.prepareStatement(sql);
            ps.setString(1, Folio);
            rs = ps.executeQuery();
            while (rs.next()) {
                año = String.valueOf(rs.getInt(1));
            }
            rs.close();
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception exc) {
                log.warn("Cerrando ResultSet", exc);
            }
        }
        return año;
    }

    public String ConsultaAñoInicio(Connection conn, String Folio) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql;
        String año = "";
        try {
            sql = "select MIN(ciclo) from tContratoPlurianual_EP where nFolioContratoPlurianual=? ";
            ps = conn.prepareStatement(sql);
            ps.setString(1, Folio);
            rs = ps.executeQuery();
            while (rs.next()) {
                año = String.valueOf(rs.getInt(1));
            }
            rs.close();
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception exc) {
                log.warn("Cerrando ResultSet", exc);
            }
        }
        return año;
    }

    public int ConsultaPluBloqueados(Connection conn, String tipo) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql;
        int bloqueo = 1;
        try {
            if ("Especial".equals(tipo)) {
                sql = " SELECT Especiales FROM tContratoPlurianual_Bloqueos WHERE Actual = 1 ";
            } else {
                sql = " SELECT Normales FROM tContratoPlurianual_Bloqueos WHERE Actual = 1 ";
            }
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                bloqueo = rs.getInt(1);
            }
            rs.close();
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception exc) {
                log.warn("Cerrando ResultSet", exc);
            }
        }
        return bloqueo;
    }

    public int getMontoTotal(Connection conn, int nFolio, boolean isModificacion, int nModicacion) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql;
        int monto = 0;
        try {
            if (isModificacion)
                sql = "select nMontoTotal_Calculado from [tContratoPlurianualModificacionEncabezado] where nFolioContratoPlurianual= " + nFolio + "  and nModificacion=  " + nModicacion;
            else
                sql = "select nMontoTotal_Calculado from [tContratoPlurianualEncabezado] where nFolioContratoPlurianual= " + nFolio;
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                monto = rs.getInt(1);
            }
            rs.close();
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception exc) {
                log.warn("Cerrando ResultSet", exc);
            }
        }
        return monto;
    }

    public ArrayList<String> getEPByModificiacion(Connection conn, int nModicacion, int nFolio) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql;
        ArrayList<String> lstEps = new ArrayList<String>();
        try {
            sql = "select ep from tContratoPlurianualModificacion_EP where nFolioContratoPlurianual=" + nFolio + " and nModificacion=" + nModicacion;
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                lstEps.add(rs.getString(1));
            }
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
            } catch (Exception exc) {
                log.warn("Cerrando ResultSet", exc);
            }
        }
        return lstEps;
    }

    public String getValidarContra(Connection conn, int nModicacion, int nFolio) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql;
        String tipo = "";
        try {
            sql = "select lAbierto from tContratoPlurianualModificacionEncabezado with(nolock) where  nFolioContratoPlurianual=" + nFolio + " and nModificacion=" + nModicacion;
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                tipo = rs.getString(1);
            }
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
            } catch (Exception exc) {
                log.warn("Cerrando ResultSet", exc);
            }
        }
        return tipo;
    }

    public int getMontoModificacion(Connection conn, int nFolio, boolean montoMinimo, int nConsecutivo) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql;
        int monto = 0;
        nConsecutivo++;
        try {
            if (montoMinimo) {
                sql = "select nImporteMinimo from tContratoPlurianualModificacion_EP where nFolioContratoPlurianual=" + nFolio + "and nModificacion=( select MAX(nmodificacion) from tContratoPlurianualModificacionDetalle where nFolioContratoPlurianual=" + nFolio + ")and nConsecutivo=" + nConsecutivo;
            } else {
                sql = "select nImporteMaximo from tContratoPlurianualModificacionDetalle where nFolioContratoPlurianual=" + nFolio + "and nModificacion=( select MAX(nmodificacion) from tContratoPlurianualModificacionDetalle where nFolioContratoPlurianual=" + nFolio + ")and nConsecutivo=" + nConsecutivo;
            }
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                monto = rs.getInt(1);
            }
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
            } catch (Exception exc) {
                log.warn("Cerrando ResultSet", exc);
            }
        }
        return monto;
    }

    public boolean isValMinimo(Connection conn, int nFolio) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql;
        boolean isMinimo = false;
        try {
            sql = "select top(1) bValidaContraMinimo from tContratoPlurianual_EP where nFolioContratoPlurianual=" + nFolio;
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) == 1) {
                    isMinimo = true;
                }
            }
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
            } catch (Exception exc) {
                log.warn("Cerrando ResultSet", exc);
            }
        }
        return isMinimo;
    }

    public int getTipoGasto(Connection conn, int nFolio) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql;
        int monto = -1;
        try {
            sql = "select cTipoGasto from tContratoPlurianualEncabezado where nFolioContratoPlurianual=" + nFolio;
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                monto = rs.getInt(1);
            }
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
            } catch (Exception exc) {
                log.warn("Cerrando ResultSet", exc);
            }
        }
        return monto;
    }

    public int getTotalInicial(Connection conn, int nFolio) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql;
        int tipo = -1;
        try {
            sql = "select nMontoTotal_Calculado from tContratoPlurianualEncabezado where nFolioContratoPlurianual=" + nFolio;
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                tipo = rs.getInt(1);
            }
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
            } catch (Exception exc) {
                log.warn("Cerrando ResultSet", exc);
            }
        }
        return tipo;
    }

    public ArrayList<Double> getDiferenciaMontos(Connection conn, int nFolio, int ejercicio, int nModificacion) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql;
        ArrayList<Double> lstMontos = new ArrayList<Double>();
        try {
            sql = "SELECT CASE " + "            WHEN pe.bValidaContraMinimo=0 " + "            THEN pe.nImporteMaximo " + "            ELSE pe.nImporteMinimo " + "        END impMod, " + "            CASE " + "                WHEN md.bValidaContraMinimo=0 " + "                THEN md.nImporteMaximo " + "                ELSE md.nImporteMinimo " + "            END ultImpor " + " FROM tContratoPlurianualModificacion_EP md " + " INNER JOIN " + " tContratoPlurianual_EP pe " + " ON md.nFolioContratoPlurianual=pe.nFolioContratoPlurianual " + " AND md.ep=pe.ep " + " AND pe.ciclo=md.ciclo " + " WHERE md.nFolioContratoPlurianual=" + nFolio + "   AND pe.ciclo=" + ejercicio + "   AND md.nModificacion=" + nModificacion + " ORDER BY pe.nConsecutivo; ";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                lstMontos.add(rs.getDouble(1) - rs.getDouble(2));
            }
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
            } catch (Exception exc) {
                log.warn("Cerrando ResultSet", exc);
            }
        }
        return lstMontos;
    }
}
