package com.axtel.contratos.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import com.axtel.contratos.entities.DatEnteraSatisfaccion;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcesoEnteraSatisfaccionManager {

    private static Logger log = LoggerFactory.getLogger(ProcesoEnteraSatisfaccionManager.class);

    public boolean saveServicioEnteraSatisfacccion(Connection conn, DatEnteraSatisfaccion dat) throws SQLException {
        PreparedStatement ps = null;
        StringBuilder query = null;
        boolean success = false;
        try {
            query = new StringBuilder();
            query.append("insert into mServicioEnteraSatisfacccion (cFolio,cIdContratoDefinitivo ,cNoContratoCNET	,nIdLinea 	,nIdPeriodoPago  ");
            query.append(",nCentroTrabajo ,nNumEmpFirmante  ,nServPrestEnteraSatisfaccion ,cUsuarioCaptura	,fFechaCaptura ");
            query.append(",cUsuarioValida	,fFechaValida ,nIdEstatus ,cIdRFC	,cObservacionesTramite) ");
            query.append(" values(?, ?, ?, ?, ?,   ?, ?, ?, ?, convert(date,getdate()), null, null, 1, ?,  null  )");
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, dat.getcFolio());
            ps.setString(2, dat.getcIdContratoDefinitivo());
            ps.setString(3, dat.getcNoContratoCNET());
            ps.setInt(4, dat.getnIdLinea());
            ps.setInt(5, dat.getnIdPeriodoPago());
            ps.setInt(6, dat.getnCentroTrabajo());
            ps.setInt(7, dat.getnNumEmpFirmante());
            ps.setInt(8, dat.getnServPrestEnteraSatisfaccion());
            ps.setString(9, dat.getcUsuarioCaptura());
            ps.setString(10, dat.getcIdRFC());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps);
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
        }
        return success;
    }

    public boolean updateServicioEnteraSatisfacccion(Connection conn, DatEnteraSatisfaccion dat) throws SQLException {
        PreparedStatement ps = null;
        StringBuilder query = null;
        boolean success = false;
        try {
            query = new StringBuilder();
            query.append("update mServicioEnteraSatisfacccion set cIdContratoDefinitivo=?,cNoContratoCNET=?	,nIdLinea=? 	,nIdPeriodoPago=? ");
            query.append(",nCentroTrabajo=? ,nNumEmpFirmante=?  ,nServPrestEnteraSatisfaccion=? ,cUsuarioCaptura=?	,fFechaCaptura=convert(date,GETDATE()) ");
            query.append(",nIdEstatus=1 ,cIdRFC=? where cFolio=? ");
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, dat.getcIdContratoDefinitivo());
            ps.setString(2, dat.getcNoContratoCNET());
            ps.setInt(3, dat.getnIdLinea());
            ps.setInt(4, dat.getnIdPeriodoPago());
            ps.setInt(5, dat.getnCentroTrabajo());
            ps.setInt(6, dat.getnNumEmpFirmante());
            ps.setInt(7, dat.getnServPrestEnteraSatisfaccion());
            ps.setString(8, dat.getcUsuarioCaptura());
            ps.setString(9, dat.getcIdRFC());
            ps.setString(10, dat.getcFolio());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps);
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
        }
        return success;
    }

    public boolean saveAnexo1A(Connection conn, DatEnteraSatisfaccion dat) throws SQLException {
        PreparedStatement ps = null;
        StringBuilder query = null;
        boolean success = false;
        try {
            query = new StringBuilder();
            query.append("insert into mAnexo1A (cFolio,nDiaformalizacion	,cMesFormalizacion	,cAnioFormalizacion	,cDeclaraccion	,cDescripcionServicio	,cInmueble	,cEjercicioPago )");
            query.append("values(?, ?, ?,   ?, ?, ?,  ?,?) ");
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, dat.getcFolio());
            ps.setInt(2, dat.getAnexo().getnDiaformalizacion());
            ps.setString(3, dat.getAnexo().getcMesFormalizacion());
            ps.setString(4, dat.getAnexo().getcAnioFormalizacion());
            ps.setString(5, dat.getAnexo().getcDeclaraccion());
            ps.setString(6, dat.getAnexo().getcDescripcionServicio());
            ps.setString(7, dat.getAnexo().getcInmueble());
            ps.setString(8, dat.getAnexo().getcEjercicioPago());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps);
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
        }
        return success;
    }

    public boolean updateAnexo1A(Connection conn, DatEnteraSatisfaccion dat) throws SQLException {
        PreparedStatement ps = null;
        StringBuilder query = null;
        boolean success = false;
        try {
            query = new StringBuilder();
            query.append("update mAnexo1A set nDiaformalizacion=?	,cMesFormalizacion=?	,cAnioFormalizacion=?	,cDeclaraccion=?	,cDescripcionServicio=?	,cInmueble=?	,cEjercicioPago=?	 where cFolio=? ");
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, dat.getAnexo().getnDiaformalizacion());
            ps.setString(2, dat.getAnexo().getcMesFormalizacion());
            ps.setString(3, dat.getAnexo().getcAnioFormalizacion());
            ps.setString(4, dat.getAnexo().getcDeclaraccion());
            ps.setString(5, dat.getAnexo().getcDescripcionServicio());
            ps.setString(6, dat.getAnexo().getcInmueble());
            ps.setString(7, dat.getAnexo().getcEjercicioPago());
            ps.setString(8, dat.getcFolio());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps);
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
        }
        return success;
    }

    public boolean saveActaHechos(Connection conn, DatEnteraSatisfaccion dat) throws SQLException {
        PreparedStatement ps = null;
        StringBuilder query = null;
        boolean success = false;
        try {
            query = new StringBuilder();
            query.append("insert into mActaCircunstanciadaHechos (cFolio 	,cDescripcion1 ,cDescripcion2, cPuestoFirmante,cFolioFirmante ,nNumEmpTestigo1,cPuestoTestigo1 ,cFolioTestigo1 ,nNumEmpTestigo2 ,cPuestoTestigo2");
            query.append(" ,cFolioTestigo2 ,cLugarAdscripcion,cDescripcionHechos,cDescripCierreHechos) ");
            query.append(" values(?,?,?,    ?,?,?,?,   ?,?,?,?, ?,?,? ) ");
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, dat.getcFolio());
            ps.setString(2, dat.getActaHechos().getcDescripcion1());
            ps.setString(3, dat.getActaHechos().getcDescripcion2());
            ps.setString(4, dat.getcPuestoFirmante());
            ps.setString(5, dat.getcFolioFirmante());
            ps.setInt(6, dat.getActaHechos().getnNumEmpTestigo1());
            ps.setString(7, dat.getActaHechos().getcPuestoTestigo1());
            ps.setString(8, dat.getActaHechos().getcFolioTestigo1());
            ps.setInt(9, dat.getActaHechos().getnNumEmpTestigo2());
            ps.setString(10, dat.getActaHechos().getcPuestoTestigo2());
            ps.setString(11, dat.getActaHechos().getcFolioTestigo2());
            ps.setString(12, dat.getActaHechos().getcLugarAdscripcion());
            ps.setString(13, dat.getActaHechos().getcDescripcionHechos());
            ps.setString(14, dat.getActaHechos().getcDescripCierreHechos());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps);
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
        }
        return success;
    }

    public boolean updateActaHechos(Connection conn, DatEnteraSatisfaccion dat) throws SQLException {
        PreparedStatement ps = null;
        StringBuilder query = null;
        boolean success = false;
        try {
            query = new StringBuilder();
            query.append("update mActaCircunstanciadaHechos set cDescripcion1=? ,cDescripcion2=? ,nNumEmpTestigo1=? ,cFolioTestigo1=? ,nNumEmpTestigo2 =? ");
            query.append(",cFolioTestigo2=? ,cLugarAdscripcion=?,cDescripcionHechos=?,cDescripCierreHechos=?,cFolioFirmante=?	where cFolio=? ");
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, dat.getActaHechos().getcDescripcion1());
            ps.setString(2, dat.getActaHechos().getcDescripcion2());
            ps.setInt(3, dat.getActaHechos().getnNumEmpTestigo1());
            ps.setString(4, dat.getActaHechos().getcFolioTestigo1());
            ps.setInt(5, dat.getActaHechos().getnNumEmpTestigo2());
            ps.setString(6, dat.getActaHechos().getcFolioTestigo2());
            ps.setString(7, dat.getActaHechos().getcLugarAdscripcion());
            ps.setString(8, dat.getActaHechos().getcDescripcionHechos());
            ps.setString(9, dat.getActaHechos().getcDescripCierreHechos());
            ps.setString(10, dat.getcFolioFirmante());
            ps.setString(11, dat.getcFolio());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps);
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
        }
        return success;
    }

    public boolean existInfoEnteraSatisfaccion(Connection conn, String cFolio) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        boolean success = false;
        try {
            query = new StringBuilder();
            query.append(" select *from mServicioEnteraSatisfacccion with(Nolock) where cFolio=?");
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cFolio);
            rs = ps.executeQuery();
            if (rs.next()) {
                success = true;
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
        }
        return success;
    }

    public boolean deleteActaHechos(Connection conn, String cFolio) throws SQLException {
        PreparedStatement ps = null;
        StringBuilder query = null;
        boolean success = false;
        try {
            query = new StringBuilder();
            query.append("delete mActaCircunstanciadaHechos where cFolio=? ");
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cFolio);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps);
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
        }
        return success;
    }

    public boolean updateValidateEnteraSatisfaccion(Connection conn, DatEnteraSatisfaccion dat) throws SQLException {
        PreparedStatement ps = null;
        StringBuilder query = null;
        boolean success = false;
        try {
            query = new StringBuilder();
            query.append("update mServicioEnteraSatisfacccion set cUsuarioValida=?,fFechaValida=convert(date,GETDATE()),nIdEstatus=?,cObservacionesTramite= ?");
            query.append(" where cFolio=? ");
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, dat.getcUsuarioValida());
            ps.setInt(2, dat.getnIdEstatus());
            ps.setString(3, dat.getcObservacionesTramite());
            ps.setString(4, dat.getcFolio());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps);
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
        }
        return success;
    }

    public static boolean updateStateEnteraSatisfaccion(Connection conn, DatEnteraSatisfaccion dat) throws SQLException {
        PreparedStatement ps = null;
        StringBuilder query = null;
        boolean success = false;
        try {
            query = new StringBuilder();
            query.append("update mServicioEnteraSatisfacccion set nIdEstatus=? ");
            query.append(" where cFolio=? ");
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, dat.getnIdEstatus());
            ps.setString(2, dat.getcFolio());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps);
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
        }
        return success;
    }

    public static boolean updateObservationEnteraSatisfaccion(Connection conn, DatEnteraSatisfaccion dat) throws SQLException {
        PreparedStatement ps = null;
        StringBuilder query = null;
        boolean success = false;
        try {
            query = new StringBuilder();
            query.append("update mServicioEnteraSatisfacccion set cObservacionesTramite=? ");
            query.append(" where cFolio=? ");
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, dat.getcObservacionesTramite());
            ps.setString(2, dat.getcFolio());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps);
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
        }
        return success;
    }

    public static Map<String, String> getSignatoryData(Connection conn, String cFolio) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        Map<String, String> datUser = null;
        try {
            query = new StringBuilder();
            query.append(" select *from fn_procesoEnteraSatisfaccion(?)");
            datUser = new HashMap<>();
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cFolio);
            rs = ps.executeQuery();
            if (rs.next()) {
                datUser.put("emailSignatory", rs.getString("emailFirmante"));
                datUser.put("nameSignatory", rs.getString("firmanteResponsable"));
                datUser.put("positionSignatory", rs.getString("lblPuestoFirmante"));
                datUser.put("numberSignatory", rs.getString("numFirmanteResponsable"));
                datUser.put("emailWitness1", rs.getString("emailTestigo1"));
                datUser.put("nameWitness1", rs.getString("testigo1"));
                datUser.put("positionWitness1", rs.getString("lblPuestoTestigo1"));
                datUser.put("numberWitness1", rs.getString("numEmpTestigo1"));
                datUser.put("emailWitness2", rs.getString("emailTestigo2"));
                datUser.put("nameWitness2", rs.getString("testigo2"));
                datUser.put("positionWitness2", rs.getString("lblPuestoTestigo2"));
                datUser.put("numberWitness2", rs.getString("numEmpTestigo2"));
                datUser.put("contractSAI", rs.getString("pedidoContratoCompromiso"));
                datUser.put("contractCNET", rs.getString("cNumCNET"));
                datUser.put("rfc", rs.getString("cIdRFC"));
                datUser.put("supplier", rs.getString("lblProveedor"));
                datUser.put("paymentMonth", rs.getString("lblMes"));
                datUser.put("serviceRendered", rs.getString("nServicioPrestado"));
                datUser.put("emailUserCapture", rs.getString("emailUserCapture"));
                datUser.put("nameUserCapture", rs.getString("nameUserCapture"));
                datUser.put("emailUserValidate", rs.getString("emailUserValidate"));
                datUser.put("nameUserValidate", rs.getString("nameUserValidate"));
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
        }
        return datUser;
    }

    public static DatEnteraSatisfaccion read(Connection conn, int folioenteraSatisfaccion) throws SQLException, ParseException {
        StringBuilder query = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        DatEnteraSatisfaccion ensa = null;
        try {
            query = new StringBuilder();
            query.append("SELECT	* ");
            query.append("  FROM	fn_DatProcesoEnteraSatisfaccion (?) ");
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, folioenteraSatisfaccion);
            rs = ps.executeQuery();
            if (rs.next()) {
                ensa = new DatEnteraSatisfaccion();
                ensa.setnServicioEnteraSatisfaccion(folioenteraSatisfaccion);
                ensa.setcIdContratoDefinitivo(rs.getString("pedidoContratoCompromiso"));
                ensa.setcFolio(rs.getString("cFolio"));
                ensa.setcNoContratoCNET(rs.getString("cNumCNET"));
                ensa.setcIdRFC(rs.getString("cIdRFC"));
                ensa.setnNumEmpFirmante(rs.getInt("numFirmanteResponsable"));
                ensa.setcUsuarioCaptura(rs.getString("cLoginUsuarioCapturista"));
                ensa.setnIdEstatus(rs.getInt("nIdEstatus"));
                ensa.setnIdCaso(rs.getInt("ID_CASO"));
                ensa.setnServPrestEnteraSatisfaccion(rs.getInt("nServicioPrestado"));
                ensa.setcNombreEmpFirmante(rs.getString("firmanteResponsable"));
                return ensa;
            } else {
                throw new SQLException("No se encontro el folio: " + folioenteraSatisfaccion + " del proceso de entera satisfacción");
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static boolean asignedCase(Connection conn, String name, int nIdCaso, int idTC) throws SQLException {
        PreparedStatement ps = null;
        StringBuilder query = null;
        boolean success = false;
        try {
            query = new StringBuilder();
            query.append("update CG_CASO_OPERACION set CO_RESPONSABLE=? where ID_TC=? and ID_CASO=? ");
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, name);
            ps.setInt(2, idTC);
            ps.setInt(3, nIdCaso);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps);
            if (query != null) {
                query.delete(0, query.length());
            }
            query = null;
        }
        return success;
    }

    public static void registraBitacora(Connection conn, String operacion, String cDocument, int nFolio, String cLogin) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tBitacoraFirma ");
        query.append("        ( cTipoDocumento , ");
        query.append("          nFolioDocumento , ");
        query.append("          dFechaOperacion , ");
        query.append("          U_LOGIN , ");
        query.append("          cOperacion ");
        query.append("        ) ");
        query.append("VALUES  ( ?, ");
        query.append("          ?, ");
        query.append("          ?, ");
        query.append("          ?, ");
        query.append("          ? ");
        query.append("        ) ");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cDocument);
            ps.setInt(2, nFolio);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, cLogin);
            ps.setString(5, operacion);
            ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }
}
