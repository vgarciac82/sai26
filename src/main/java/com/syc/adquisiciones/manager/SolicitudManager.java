package com.syc.adquisiciones.manager;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.syc.adquisiciones.core.DatosRequisicion;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolicitudManager {

    private static Logger log = LoggerFactory.getLogger(SolicitudManager.class);

    public Caso getCaso(HttpSession session, Connection conn) throws Exception {
        PreparedStatement pstmt = null, pstmt1 = null;
        ResultSet rs = null, rs1 = null;
        String sql, tipo, ue, ejercicio, sql1;
        int consecutivo;
        Caso c = null;
        Caso sc = null;
        try {
            ejercicio = (String) session.getAttribute(GestionInterface.ATT_ReqEjercicio);
            tipo = (String) session.getAttribute(GestionInterface.ATT_ReqTipoSolicitud);
            ue = (String) session.getAttribute(GestionInterface.ATT_ReqUnidadEjec);
            consecutivo = Integer.parseInt((String) session.getAttribute(GestionInterface.ATT_ReqConsecutivo));
            sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mSolicitud s with(Nolock), CG_CASO c with(Nolock), CG_CASO_OPERACION o with(Nolock) " + " where s.C_FOLIO_APA=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and s.cEjercicio=?" + " and s.cIdTipoSolicitud = ? " + " and s.cIdUnidadEjecutora = ?" + " and s.nIdConsecutivo = ?";
            sql1 = "SELECT count(c.ID_CASO) as ID_CASO " + " FROM mSolicitud s with(Nolock), CG_CASO c with(Nolock), CG_CASO_OPERACION o with(Nolock) " + " where s.C_FOLIO_APA=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and s.cEjercicio=?" + " and s.cIdTipoSolicitud = ? " + " and s.cIdUnidadEjecutora = ?" + " and s.nIdConsecutivo = ?" + " and s.nIdEstado<>5";
            if (ejercicio.length() > 0 && tipo.length() > 0 && ue.length() > 0 && consecutivo > 0) {
                pstmt1 = conn.prepareStatement(sql1);
                pstmt1.setString(1, ejercicio);
                pstmt1.setString(2, tipo);
                pstmt1.setString(3, ue);
                pstmt1.setInt(4, consecutivo);
                rs1 = pstmt1.executeQuery();
                if (rs1.next()) {
                    int idCaso = rs1.getInt("ID_CASO");
                    if (idCaso > 0) {
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, ejercicio);
                        pstmt.setString(2, tipo);
                        pstmt.setString(3, ue);
                        pstmt.setInt(4, consecutivo);
                        rs = pstmt.executeQuery();
                        if (rs.next()) {
                            String id_Caso = rs.getString("ID_CASO");
                            log.debug(id_Caso);
                            if (id_Caso != null && Integer.parseInt(id_Caso) > 0) {
                                sc = new Caso();
                                sc.setIdCaso(Integer.parseInt(id_Caso));
                                c = CasoManager.select(conn, sc);
                            }
                        }
                    }
                }
            }
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (pstmt1 != null)
                    pstmt1.close();
                if (rs != null)
                    rs.close();
                if (rs1 != null)
                    rs1.close();
            } catch (SQLException exc) {
                log.warn(exc);
            }
            pstmt = null;
            pstmt1 = null;
            rs = null;
            rs1 = null;
        }
        return c;
    }

    public synchronized Caso iniciaCaso(String jndiName, String tCaso, String folioGenerator, Usuario usuario) throws GestionException {
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
        ClassLoader cl = null;
        Class<?> clase = null;
        try {
            cl = getClass().getClassLoader();
            clase = cl.loadClass(folioGenerator);
            fg = (FolioGeneratorInterface) clase.newInstance();
            c = casoTx.IniciaCaso(usuario, idTC, fg);
            log.info(usuario + "_" + idTC + "_" + fg);
            if (c == null) {
                log.error("No se logro crear el caso");
                throw new GestionException("No se logró crear el caso");
            }
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
        } finally {
            casoTx = null;
            cl = null;
            clase = null;
        }
        return c;
    }

    public void avanzaCaso(HttpServletRequest req, Caso c, Usuario u, String prefixPath, String[] responsable, String[] nombre, String jndiName) throws GestionException, ServletException, IOException {
        //Método para avanzar el caso. Se utliza tanto para pre-compromiso como para compromiso
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
        Map m = CasoDatoManager.readValuesCasoDato(req, c.getCasoDato(), true);
        CasoBusinessLogic cbl = new CasoBusinessLogic(jndiName);
        cbl.avanzaCaso(c, u.getLogin(), "", responsable, nombre, m, prefixPath);
    }

    public boolean creaEncabDetApartado(Connection conn, DatosRequisicion datosRequi, Usuario usuario) throws Exception {
        PreparedStatement pstm = null;
        CallableStatement cmst = null;
        try {
            log.info("Creando encabezado y detalle del apartado." + new Timestamp(System.currentTimeMillis()));
            //Crea encabezado
            pstm = conn.prepareStatement(" INSERT INTO tApartadoEncabezado (nFolioApartado, fCarga, fAplicacion, cCentroContable, cRamo,cUnidadResponsable, caNoPreCompromiso," + " cTipoPoliza, nMes, aEjercicioFiscal, nStatusFinanciero, fVigencia, nEnviadoSICOP, cIdSolicitud) " + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            pstm.setInt(1, datosRequi.getnFolioApartado());
            pstm.setString(2, datosRequi.getfCarga());
            pstm.setString(3, datosRequi.getfAplicacion());
            pstm.setString(4, usuario.getPropiedad("CCENTROCONTABLE").getValor());
            pstm.setString(5, usuario.getU_Ramo());
            pstm.setString(6, usuario.getU_UR());
            pstm.setString(7, datosRequi.getCaNoPreCompromiso());
            pstm.setString(8, datosRequi.getcTipoPoliza());
            pstm.setInt(9, datosRequi.getnMes());
            pstm.setString(10, datosRequi.getcEjercicio());
            pstm.setInt(11, datosRequi.getnStatusFinanciero());
            pstm.setString(12, datosRequi.getfVigencia());
            pstm.setInt(13, datosRequi.getnEnviadoSICOP());
            pstm.setString(14, datosRequi.getcIdSolicitud());
            pstm.executeUpdate();
            //Crea detalle
            cmst = conn.prepareCall("{call sp_insertApartadoDetalle (?,?,?,?,?)}");
            cmst.setInt(1, datosRequi.getnFolioApartado());
            cmst.setString(2, usuario.getPropiedad("CCENTROCONTABLE").getValor());
            cmst.setString(3, datosRequi.getcEjercicio());
            cmst.setString(4, usuario.getU_UR());
            cmst.setString(5, datosRequi.getcIdSolicitud());
            cmst.execute();
            log.info("Termina de crear el encabezado y detalle del apartado." + new Timestamp(System.currentTimeMillis()));
            return true;
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            if (cmst != null) {
                cmst.close();
            }
            pstm = null;
            cmst = null;
        }
    }

    public boolean requiIntegrada(Connection conn, String cIdSolicitud) throws Exception {
        boolean resp = false;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            pstm = conn.prepareStatement("select * from v_mRequisIntegradas where nEstatus<>3 and cIdSolicitud=?");
            pstm.setString(1, cIdSolicitud);
            rs = pstm.executeQuery();
            if (rs.next())
                resp = true;
            return resp;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pstm, false);
        }
    }

    public boolean requiIsConvenio(Connection conn, String cIdSolicitud) throws Exception {
        boolean resp = false;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            pstm = conn.prepareStatement("select cIdSolicitud,cIdPedidoDefinitivo from mPedidoModificadoPartida WITH(NOLOCK) WHERE cIdSolicitud = ? " + " union select cIdSolicitud,cIdContratoDefinitivo from mContratoModificadoPartida WITH(NOLOCK) WHERE cIdSolicitud = ? ");
            pstm.setString(1, cIdSolicitud);
            pstm.setString(2, cIdSolicitud);
            rs = pstm.executeQuery();
            if (rs.next())
                resp = true;
            return resp;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pstm, false);
        }
    }

    public boolean requiIsConsolidado(Connection conn, String cIdSolicitud) throws Exception {
        boolean resp = false;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            pstm = conn.prepareStatement("select * from mSolicitud s WITH(NOLOCK) where (s.cIdSolicitud in( select distinct cIdSolicitud " + "from mConsolidadoSolicitud WITH(NOLOCK)) or s.cIdSolicitud in(select distinct cIdSolicitud from mConsolidadoPreseleccionSolicitudes WITH(NOLOCK) )) AND cIdSolicitud= ?");
            pstm.setString(1, cIdSolicitud);
            rs = pstm.executeQuery();
            if (rs.next())
                resp = true;
            return resp;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pstm, false);
        }
    }

    public boolean resetLineasApartado(Connection conn, String cIdsolicitud) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "update msolicitudlineasApartado set mes01=0,mes02=0,mes03=0,mes04=0,mes05=0,mes06=0,mes07=0,mes08=0,mes09=0,mes10=0,mes11=0,mes12=0 where cIdSolicitud=?";
        try {
            ps = conn.prepareStatement(query);
            log.info(query);
            ps.setString(1, cIdsolicitud);
            return ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
            query = null;
        }
    }

    public boolean deleteDetalleApartado(Connection conn, String cIdsolicitud) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "delete from tApartadoDetalle where nFolioApartado in(select nFolioApartado from tApartadoEncabezado with(Nolock) where cIdSolicitud=? and cDocumentoHaplicado is null)";
        try {
            ps = conn.prepareStatement(query);
            log.info(query);
            ps.setString(1, cIdsolicitud);
            return ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
            query = null;
        }
    }

    public boolean deleteEncabezadoApartado(Connection conn, String cIdsolicitud) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "delete from tApartadoEncabezado where cIdSolicitud=? and cDocumentoHaplicado is null";
        try {
            ps = conn.prepareStatement(query);
            log.info(query);
            ps.setString(1, cIdsolicitud);
            return ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
            query = null;
        }
    }

    public boolean resetConsecutivoApartado(Connection conn, String cIdsolicitud) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "update mSolicitud set ConsecutivoAPARTADO=null,C_FOLIO_APA=null where cIdSolicitud = ?";
        try {
            ps = conn.prepareStatement(query);
            log.info(query);
            ps.setString(1, cIdsolicitud);
            return ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
            query = null;
        }
    }
}
