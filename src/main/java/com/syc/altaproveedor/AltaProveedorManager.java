package com.syc.altaproveedor;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.axtel.proveedores.exception.ProveedorException;
import com.axtel.proveedores.model.Proveedor;
import com.syc.adquisiciones.core.Respuesta;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.contabilidad.utils.db.RSToTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AltaProveedorManager {

    private static Logger log = LoggerFactory.getLogger(AltaProveedorManager.class);

    private final CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);

    public boolean changeStatus(Connection conn, String folioSAI, String status) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        String query = "UPDATE taltaproveedor SET cDocumentoHaplicado = ? WHERE cFolio = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, status);
            ps.setString(2, folioSAI);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public JSONArray obtieneDatQuery(Connection conn, String query) throws SQLException, JSONException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        JSONArray arrayObj = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        try {
            log.info(query);
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            int token = 0;
            while (rs.next()) {
                if (token > 0) {
                    jsonObj = new JSONObject();
                }
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
            if (pstmt != null) {
                pstmt.close();
            }
            if (rs != null) {
                rs.close();
            }
            pstmt = null;
            rs = null;
        }
        return arrayObj;
    }

    public JSONArray datGuardados(Connection conn, String query) throws SQLException, JSONException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsMetadata = null;
        JSONArray arrayObj = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        try {
            log.info(query);
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            rsMetadata = rs.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            if (rs.next()) {
                jsonObj.put("HAYINFO", true);
                for (int i = 1; i <= totalcolumnas; i++) {
                    jsonObj.put(rsMetadata.getColumnName(i), rs.getString(i));
                }
            } else {
                jsonObj.put("HAYINFO", false);
            }
            arrayObj.put(jsonObj);
        } finally {
            jsonObj = null;
            if (pstmt != null) {
                pstmt.close();
            }
            if (rs != null) {
                rs.close();
            }
            rsMetadata = null;
            pstmt = null;
            rs = null;
        }
        return arrayObj;
    }

    public boolean updateDatAltaProveedor(Connection conn, DatosProveedor datProv) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "UPDATE dbo.tAltaProveedor SET cIdTipoPersona=" + datProv.getnTipoPersona() + ",cTipoRegistro='" + datProv.getcTipoPB() + "' " + ",cIdRFC='" + datProv.getRfc() + "',cRazonSocial='" + datProv.getcRazonSocial() + "',cCURP='" + datProv.getcCurp() + "',cApellidoPaterno='" + datProv.getcApellidoPat() + "',cApellidoMaterno='" + datProv.getcApellidoMat() + "',cNombre='" + datProv.getcNombre() + "'" + ",cGiro='" + datProv.getcGiro() + "',cIdEntidadFederativa=" + datProv.getnEntidadFederativa() + ",cIdMunicipio=" + datProv.getnMunicipio() + ",cCalle='" + datProv.getcCalle() + "',cNumeroExterno='" + datProv.getcNumeroExt() + "'" + ",cNumeroInterno='" + datProv.getcNumeroInt() + "',cColonia='" + datProv.getcColonia() + "',cCodigoPostal='" + datProv.getcCodigoPost() + "',cEmail='" + datProv.getcEmail() + "',cUrl='" + datProv.getcPaginaWeb() + "',nIdPyme=" + datProv.getnPyme() + ",nIdTipoTelefono=" + datProv.getnTipoTelefono() + ",cTelefono='" + datProv.getcTelefono() + "',cExtranjero=" + datProv.getnExtranjero() + ",cPais='" + datProv.getcPais() + "' " + ",nIdEmpleado=" + (datProv.getnTipoPersona() == 3 ? datProv.getnNumEmpleado() : "null") + ",cNumeroREPSE = '" + datProv.getNumRepse() + "' " + ",id_regimen_fiscal=" + datProv.getIdRegimenFiscal() + ",cIdLocalidad='" + datProv.getcLocalidad() + "' " + " WHERE cFolio='" + datProv.getcFolio() + "'";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean saveDatProveedor(Connection conn, DatosProveedor datProv, Usuario u) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "INSERT INTO dbo.tAltaProveedor (cFolio,cIdTipoPersona,cTipoRegistro,cIdRFC,cRazonSocial,cCURP,cApellidoPaterno,cApellidoMaterno";
            query += ",cNombre,cGiro,cIdEntidadFederativa,cIdMunicipio,cCalle,cNumeroExterno,cNumeroInterno,cColonia,cCodigoPostal,cEmail,cUrl,nIdPyme";
            query += ",nIdTipoTelefono,cTelefono,cIdUsuarioCaptura,cActualizacion,cExtranjero,cPais,nIdEmpleado, cNumeroREPSE, id_regimen_fiscal, alta_rapida,cIdLocalidad) ";
            query += " VALUES(";
            query += "'" + datProv.getcFolio() + "'";
            query += "," + "'" + datProv.getnTipoPersona() + "','";
            query += datProv.getcTipoPB() + "','";
            query += datProv.getRfc() + "','";
            query += datProv.getcRazonSocial() + "','";
            query += datProv.getcCurp() + "','";
            query += datProv.getcApellidoPat() + "','";
            query += datProv.getcApellidoMat() + "','";
            query += datProv.getcNombre() + "','";
            query += datProv.getcGiro() + "',";
            query += datProv.getnEntidadFederativa() + ",";
            query += datProv.getnMunicipio() + ",'";
            query += datProv.getcCalle() + "','";
            query += datProv.getcNumeroExt() + "','";
            query += datProv.getcNumeroInt() + "','";
            query += datProv.getcColonia() + "','";
            query += datProv.getcCodigoPost() + "','";
            query += datProv.getcEmail() + "'";
            query += "," + (datProv.getcPaginaWeb() == null ? "null" : "'" + datProv.getcPaginaWeb() + "'");
            query += "," + datProv.getnPyme() + ",";
            query += datProv.getnTipoTelefono() + ",'";
            query += datProv.getcTelefono() + "','";
            query += u.getLogin() + "','',";
            query += datProv.getnExtranjero();
            query += "," + (datProv.getcPais() == null ? "null" : "'" + datProv.getcPais() + "'");
            query += "," + (datProv.getnTipoPersona() == 3 ? datProv.getnNumEmpleado() : "null");
            query += "," + (datProv.getNumRepse() == null ? "null" : "'" + datProv.getNumRepse() + "'");
            query += "," + datProv.getIdRegimenFiscal();
            query += "," + "'" + datProv.getEsAltaRapida() + "'";
            query += "," + "'" + datProv.getcLocalidad() + "'";
            query += ")";
            log.info("Query Insert Proveedor \n" + query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean existProveedor(Connection conn, String cIdRFC) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        String query = "SELECT * FROM tAltaProveedor WITH(NOLOCK) WHERE cIdRFC='" + cIdRFC + "'";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                success = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean existProveedorEnOtroFolio(Connection conn, String cIdRFC, String cFolio) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        String query = "SELECT * FROM tAltaProveedor WITH(NOLOCK) WHERE cIdRFC='" + cIdRFC + "' and cFolio!='" + cFolio + "'";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                success = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public String otroFolioRFC(Connection conn, String cIdRFC, String cFolio) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String folio = "";
        String query = "SELECT * FROM tAltaProveedor WITH(NOLOCK) WHERE cIdRFC='" + cIdRFC + "' and cFolio!='" + cFolio + "'";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                folio = rs.getString("cFolio");
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return folio;
    }

    public boolean existFolioProveedor(Connection conn, String cFolio) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        String query = "SELECT * FROM tAltaProveedor WITH(NOLOCK) WHERE cFolio='" + cFolio + "'";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                success = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean disabledCtasBancarias(Connection conn, String cIdRFC) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "UPDATE dbo.tBeneficiarioCuentasBancarias SET nBCBEnviadoSICOP=0 WHERE dRFC='" + cIdRFC + "' ";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean disabledtBeneficiario(Connection conn, String cIdRFC) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "UPDATE dbo.tBeneficiario SET nEnviadoSICOP=0 WHERE dRFC='" + cIdRFC + "' ";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean enabledtBeneficiario(Connection conn, String cIdRFC) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "UPDATE dbo.tBeneficiario SET nEnviadoSICOP=1 WHERE dRFC='" + cIdRFC + "' ";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean enabledCtasBancarias(Connection conn, String cIdRFC) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "UPDATE dbo.tBeneficiarioCuentasBancarias SET nBCBEnviadoSICOP=1 WHERE dRFC='" + cIdRFC + "' ";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean updateAltaProveedorAplicado(Connection conn, String cFolio) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "UPDATE dbo.tAltaProveedor SET cDocumentoHaplicado='S' WHERE cFolio='" + cFolio + "' ";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean esCtaBancariaNueva(Connection conn, DatosProveedor datProveedor) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        String query = "SELECT * FROM tBeneficiarioCuentasBancariastmp WITH(NOLOCK) WHERE dRFC='" + datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3() + "' and cBanco='" + datProveedor.getCuentasBancarias().get(0).getcBanco() + "' and cPlaza='" + datProveedor.getCuentasBancarias().get(0).getcPlaza() + "' and dCuentaBancaria='" + datProveedor.getCuentasBancarias().get(0).getcCuentaBancaria() + "' ";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                success = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean hayNuevasCtas(Connection conn, String cIdRFC) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        String query = "SELECT * FROM tBeneficiarioCuentasBancariastmp WITH(NOLOCK) WHERE dRFC='" + cIdRFC + "' ";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                success = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean hayCtasBancarias(Connection conn, String cIdRFC) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        String query = "SELECT * FROM tBeneficiarioCuentasBancarias WITH(NOLOCK) WHERE dRFC='" + cIdRFC + "' ";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                success = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public Respuesta hayDocCtasBancarias(Connection conn, String cFolio) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        Respuesta resp = null;
        String query = "SELECT NOMBRE_DOCUMENTO FROM IMX_DOCUMENTO WITH(NOLOCK) WHERE TITULO_APLICACION='PROVEEDORES' " + " AND ID_GABINETE=(SELECT ID_GABINETE FROM IMX_DOCUMENTO WITH(NOLOCK)WHERE TITULO_APLICACION='PROVEEDORES' AND NOMBRE_DOCUMENTO ='" + cFolio + "')" + " AND ID_CARPETA_PADRE=5 AND NUMERO_PAGINAS<=0 ";
        String docs = "";
        String token = "";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            resp = new Respuesta();
            while (rs.next()) {
                docs = docs + token + rs.getString("NOMBRE_DOCUMENTO");
                success = true;
                token = "\n";
            }
            if (success) {
                resp.setMsg("Falta Documentacion Comprobatoria de las cuentas:\n" + docs);
                resp.setResp(false);
            } else {
                resp.setMsg("Documentación completa.");
                resp.setResp(true);
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return resp;
    }

    public boolean creaDocumentoCtaBancaria(Connection conn, String tituloAplicacion, String cFolio, int idGabinete, int idDocumento, String nameDoc, String login) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "INSERT INTO IMX_DOCUMENTO(TITULO_APLICACION,ID_GABINETE,ID_CARPETA_PADRE,ID_DOCUMENTO,NOMBRE_DOCUMENTO,NOMBRE_USUARIO,PRIORIDAD,ID_TIPO_DOCTO,FH_CREACION," + "FH_MODIFICACION,NUMERO_ACCESOS,NUMERO_PAGINAS,MATERIA,DESCRIPCION,CLASE_DOCUMENTO,ESTADO_DOCUMENTO,TAMANO_BYTES,COMPARTIR,iEsVersion) " + "VALUES('PROVEEDORES'," + idGabinete + ",5," + idDocumento + ",replace('" + nameDoc + "','.',''),'" + login + "',3,1,getdate(),getdate(),0,0,'ORIGINAL','Comprobante de cuenta: " + nameDoc + "',0,'V',0,'N',0)";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public int obtieneIdGabinete(Connection conn, String tituloAplicacion, String cFolio) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = null;
        int idGabinete = -1;
        try {
            query = "SELECT ID_GABINETE FROM IMX_DOCUMENTO WITH(NOLOCK)WHERE TITULO_APLICACION='" + tituloAplicacion + "' AND NOMBRE_DOCUMENTO ='" + cFolio + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                idGabinete = rs.getInt("ID_GABINETE");
            }
        } finally {
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rs, false);
        }
        return idGabinete;
    }

    public int obtieneIdDocumento(Connection conn, String tituloAplicacion, String nombreDocumento, int carpetaPadre, int idGabinete) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = null;
        int idDocumento = -1;
        try {
            query = "SELECT * FROM IMX_DOCUMENTO WITH(NOLOCK) WHERE TITULO_APLICACION='" + tituloAplicacion + "' AND ID_CARPETA_PADRE=" + carpetaPadre + " and ID_GABINETE=" + idGabinete + " and NOMBRE_DOCUMENTO=ltrim(rtrim('" + nombreDocumento + "'))";
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                idDocumento = rs.getInt("ID_DOCUMENTO");
            }
        } finally {
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rs, false);
        }
        return idDocumento;
    }

    public String obtieneCDocumentoHAplicado(Connection conn, String cFolio) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = null;
        String cDocumentoHAplicado = "";
        try {
            query = "SELECT cDocumentoHaplicado FROM tAltaProveedor WITH(NOLOCK) WHERE cFolio='" + cFolio + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                cDocumentoHAplicado = (null == rs.getString("cDocumentoHaplicado") ? "" : rs.getString("cDocumentoHaplicado"));
            }
        } finally {
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rs, false);
        }
        return cDocumentoHAplicado;
    }

    public int obtieneIdDocSiguiente(Connection conn, int idGabinete, String tituloAplicacion) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = null;
        int idDocSig = 1;
        try {
            query = "select count(*)+1 as docSiguiente from IMX_DOCUMENTO WITH(NOLOCK) where ID_CARPETA_PADRE=5 and TITULO_APLICACION='" + tituloAplicacion + "' and ID_GABINETE=" + idGabinete;
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                idDocSig = rs.getInt("docSiguiente");
            }
        } finally {
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rs, false);
        }
        return idDocSig;
    }

    public JSONObject obtieneCtasSinDocumentos(Connection conn, String tituloAplicacion, String rfc, int idGabinete) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = null;
        JSONObject jsonObj = new JSONObject();
        try {
            query = "select " + "(ctas.dBanco+'-'+ctas.dCuentaBancaria) nameDoc " + "from tBeneficiarioCuentasBancarias as ctas with(Nolock) " + "where ctas.dRFC='" + rfc + "' " + "and (ctas.dBanco+'-'+ctas.dCuentaBancaria) not in(SELECT NOMBRE_DOCUMENTO FROM IMX_DOCUMENTO WITH(NOLOCK) WHERE TITULO_APLICACION='" + tituloAplicacion + "' and ID_GABINETE=" + idGabinete + " and ID_CARPETA_PADRE=5)";
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            int i = 0;
            while (rs.next()) {
                jsonObj.put("" + i, rs.getString("nameDoc"));
                i++;
            }
        } finally {
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rs, false);
        }
        return jsonObj;
    }

    public boolean ultimoMovimientoAltaProv(Connection conn, String cFolio, String cLogin) throws Exception {
        CallableStatement cmst = null;
        boolean resp = false;
        try {
            cmst = conn.prepareCall("{ call sp_tAltaProveedorUltimoMovimiento (?,?)}");
            cmst.setString(1, cFolio);
            cmst.setString(2, cLogin);
            cmst.execute();
            resp = true;
        } finally {
            CloseObject.closeObject(cmst, false);
        }
        return resp;
    }

    public boolean updatetAltaProveedor(Connection conn, String cFolio, String cLoginValida, String cDocumentoHaplicado, String cObservaciones) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "UPDATE dbo.tAltaProveedor SET cIdUsuarioValida='" + cLoginValida + "',cDocumentoHaplicado='" + cDocumentoHaplicado + "',cObservaciones='" + cObservaciones + "' WHERE cFolio='" + cFolio + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean updatetAltaProveedorAut(Connection conn, String cFolio, String cLoginAut, String cDocumentoHaplicado, String cObservaciones) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "UPDATE dbo.tAltaProveedor SET cIdUsuarioAutoriza='" + cLoginAut + "',cDocumentoHaplicado='" + cDocumentoHaplicado + "',cObservaciones='" + cObservaciones + "' WHERE cFolio='" + cFolio + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean updatetAltaProveedorMod(Connection conn, String cFolio, String cLoginMod, String cDocumentoHaplicado, String cObservaciones) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "UPDATE dbo.tAltaProveedor SET cIdUsuaUltModif='" + cLoginMod + "',cDocumentoHaplicado='" + cDocumentoHaplicado + "',cObservaciones='" + cObservaciones + "' WHERE cFolio='" + cFolio + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean updateActualizacionAltaProv(Connection conn, String cFolio, String cActualizacion) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "UPDATE dbo.tAltaProveedor SET cActualizacion='" + cActualizacion + "' WHERE cFolio='" + cFolio + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public static boolean existeEnCatalogoProveedor(Connection conn, String cRFC) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        String query = "";
        try {
            query = "select * from mCatalogoProveedor WITH(NOLOCK) WHERE RTRIM(LTRIM(REPLACE(cIdRFC, '-', '')))='" + cRFC.replaceAll("-", "") + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                success = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean existePago(Connection conn, String cRFC, String CTAB) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        String query = "";
        try {
            query = "select nFolioPAGODIVERSO as nFolioPago from tPAGODIVERSOEncabezado with(Nolock) where cDocumentoHaplicado='S' and rfc='" + cRFC + "' and  CTAB='" + CTAB + "' " + "union select nFolioPagoDirecto as nFolioPago from tPagoDirectoEncabezado with(Nolock) where cDocumentoHaplicado='S' and cIdRFC='" + cRFC + "' and  CTAB='" + CTAB + "' " + "union select nFolioPAGOFEDERALIZADO as nFolioPago from tPAGOFEDERALIZADOEncabezado with(Nolock) where cDocumentoHaplicado='S' and RFC='" + cRFC + "' and  CTAB='" + CTAB + "' " + "union select nFolioPAGOOBRA as nFolioPago from tPAGOOBRAEncabezado with(Nolock) where cDocumentoHaplicado='S' and RFC='" + cRFC + "' and  CTAB='" + CTAB + "' ";
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                success = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean existeEnBeneficiario(Connection conn, String cRFC) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        String query = "";
        try {
            query = "select * from tBeneficiario WITH(NOLOCK) WHERE RTRIM(LTRIM(dRFC))='" + cRFC + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                success = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean updateCatalogoProveedor(Connection conn, DatosProveedor datProveedor) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "UPDATE mCatalogoProveedor SET cRazonSocial='" + ((datProveedor.getnTipoPersona() == 1) ? datProveedor.getcRazonSocial() : datProveedor.getcNombre() + " " + datProveedor.getcApellidoPat() + " " + datProveedor.getcApellidoMat()) + "',cGiro='" + datProveedor.getcGiro() + "'" + ",cRepresentanteLegal='" + ((datProveedor.getnTipoPersona() == 1) ? datProveedor.getcNombre() + " " + datProveedor.getcApellidoPat() + " " + datProveedor.getcApellidoMat() : "") + "',cIdEntidadFederativa='" + (datProveedor.getnEntidadFederativa() < 10 ? "0" + datProveedor.getnEntidadFederativa() : datProveedor.getnEntidadFederativa()) + "'" + ",cCalle='" + datProveedor.getcCalle() + "',cNumeroExterno='" + datProveedor.getcNumeroExt() + "'" + ",cNumeroInterno='" + datProveedor.getcNumeroInt() + "',cColonia='" + datProveedor.getcColonia() + "'" + ",cMunicipio=(SELECT cat.mpo_nombre  FROM CAT_MUNICIPIO cat WITH(NOLOCK) where ID_ESTADO=" + datProveedor.getnEntidadFederativa() + " and ID_MUNICIPIO=" + datProveedor.getnMunicipio() + ")" + ",cCodigoPostal='" + datProveedor.getcCodigoPost() + "'" + ",cEmail='" + datProveedor.getcEmail() + "',cUrl='" + datProveedor.getcPaginaWeb() + "'" + ",nIdPyme=" + datProveedor.getnPyme() + ",cCURP='" + ((datProveedor.getnTipoPersona() == 1) ? "" : datProveedor.getcCurp()) + "' " + ",lHabilitado=" + datProveedor.getnProveedorHabilitado() + ",idRegimenFiscal=" + datProveedor.getIdRegimenFiscal() + ",cNumeroREPSE=" + "'" + datProveedor.getNumRepse() + "'" + ",cEsRESICO = " + "'" + Proveedor.esResico(datProveedor.getIdRegimenFiscal()) + "' " + " WHERE cIdRFC='" + datProveedor.getRfc() + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public static boolean deleteCatalogoProveedor(Connection conn, String rfc) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "DELETE mCatalogoProveedor WHERE REPLACE(cIdRFC,'-','') = '" + rfc.replaceAll("-", "") + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean updateCatalogoBeneficiario(Connection conn, DatosProveedor datProveedor) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "UPDATE tBeneficiario SET dNombre='" + ((datProveedor.getnTipoPersona() == 1) ? datProveedor.getcRazonSocial() : datProveedor.getcNombre()) + "',dApellidoPaterno=" + ((datProveedor.getnTipoPersona() == 1) ? "null" : "'" + datProveedor.getcApellidoPat() + "'") + ",dApellidoMaterno=" + ((datProveedor.getnTipoPersona() == 1) ? "null" : "'" + datProveedor.getcApellidoMat() + "'") + ",cIdTipoPersonaRFC='" + datProveedor.getnTipoPersona() + "',dCalleActual='" + datProveedor.getcCalle() + "',dNoDomicilioActual='" + datProveedor.getcNumeroExt() + "',dNoInteriorDomicilioActual='" + datProveedor.getcNumeroInt() + "',dColoniaActual='" + datProveedor.getcColonia() + "',dCodigoPostalActual='" + datProveedor.getcCodigoPost() + "',cMunicipioActual=" + datProveedor.getnMunicipio() + ",cEstadoActual=" + datProveedor.getnEntidadFederativa() + ",dTelefonoActual='" + datProveedor.getcTelefono() + "',dEMailActual='" + datProveedor.getcEmail() + "',dCalleFiscal='" + datProveedor.getcCalle() + "',dNoDomicilioFiscal='" + datProveedor.getcNumeroExt() + "',dNoInteriorDomicilioFiscal='" + datProveedor.getcNumeroInt() + "',dColoniaFiscal='" + datProveedor.getcColonia() + "',dCodigoPostalFiscal='" + datProveedor.getcCodigoPost() + "',cMunicipioFiscal=" + datProveedor.getnMunicipio() + ",cEstadoFiscal=" + datProveedor.getnEntidadFederativa() + ",dTelefonoFiscal='" + datProveedor.getcTelefono() + "',dEMailFiscal='" + datProveedor.getcEmail() + "',dAPaternoApoderado=" + ((datProveedor.getnTipoPersona() == 1) ? "'" + datProveedor.getcApellidoPat() + "'" : "null") + ",dAMaternoApoderado=" + ((datProveedor.getnTipoPersona() == 1) ? "'" + datProveedor.getcApellidoMat() + "'" : "null") + ",dNombreApoderado=" + ((datProveedor.getnTipoPersona() == 1) ? "'" + datProveedor.getcNombre() + "'" : "null") + ",dCURP=" + ((datProveedor.getnTipoPersona() == 1) ? "null" : "'" + datProveedor.getcCurp() + "'") + ",nIdEmpleado=" + ((datProveedor.getnTipoPersona() == 3) ? datProveedor.getnNumEmpleado() : "null") + ",cExtranjero=" + datProveedor.getnExtranjero() + ",nEnviadoSICOP=0 " + " WHERE dRFC='" + datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3() + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean updateCBEN(Connection conn, String CBEN, String cRFC) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "UPDATE tBeneficiario SET CBEN='" + CBEN + "' WHERE dRFC='" + cRFC + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean createCatalogoBeneficiario(Connection conn, DatosProveedor datProveedor) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "INSERT tBeneficiario (dNombre,dApellidoPaterno,dApellidoMaterno,dRFC,dCURP,cIdTipoPersonaRFC" + ",dCalleFiscal,dNoDomicilioFiscal,dNoInteriorDomicilioFiscal,dColoniaFiscal,dCodigoPostalFiscal,cMunicipioFiscal,cEstadoFiscal,dTelefonoFiscal,dEMailFiscal" + ",dCalleActual,dNoDomicilioActual,dNoInteriorDomicilioActual,dColoniaActual,dCodigoPostalActual,cMunicipioActual,cEstadoActual,dTelefonoActual,dEMailActual" + ",dAPaternoApoderado,dAMaternoApoderado,dNombreApoderado,cExtranjero,cRFCValido,nEnviadoSICOP,cBeneficiarioStatus,fBeneficiario,cIdBancario,nIdEmpleado,alta_rapida) " + "VALUES ('" + ((datProveedor.getnTipoPersona() == 1) ? datProveedor.getcRazonSocial() : datProveedor.getcNombre()) + "'," + ((datProveedor.getnTipoPersona() == 1) ? "null" : "'" + datProveedor.getcApellidoPat() + "'") + "," + ((datProveedor.getnTipoPersona() == 1) ? "null" : "'" + datProveedor.getcApellidoMat() + "'") + ",'" + datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3() + "'," + ((datProveedor.getnTipoPersona() == 1) ? "null" : "'" + datProveedor.getcCurp() + "'") + ",'" + datProveedor.getnTipoPersona() + "','" + datProveedor.getcCalle() + "','" + datProveedor.getcNumeroExt() + "','" + datProveedor.getcNumeroInt() + "','" + datProveedor.getcColonia() + "','" + datProveedor.getcCodigoPost() + "','" + datProveedor.getnMunicipio() + "','" + datProveedor.getnEntidadFederativa() + "','" + datProveedor.getcTelefono() + "','" + datProveedor.getcEmail() + "','" + datProveedor.getcCalle() + "','" + datProveedor.getcNumeroExt() + "','" + datProveedor.getcNumeroInt() + "','" + datProveedor.getcColonia() + "','" + datProveedor.getcCodigoPost() + "','" + datProveedor.getnMunicipio() + "','" + datProveedor.getnEntidadFederativa() + "','" + datProveedor.getcTelefono() + "','" + datProveedor.getcEmail() + "'," + ((datProveedor.getnTipoPersona() == 1) ? "'" + datProveedor.getcApellidoPat() + "'" : "null") + "," + ((datProveedor.getnTipoPersona() == 1) ? "'" + datProveedor.getcApellidoMat() + "'" : "null") + "," + ((datProveedor.getnTipoPersona() == 1) ? "'" + datProveedor.getcNombre() + "'" : "null") + "," + datProveedor.getnExtranjero() + ",1,0,1,GETDATE(),'" + datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3() + "'," + ((datProveedor.getnTipoPersona() == 3) ? datProveedor.getnNumEmpleado() : "null") + ",'" + datProveedor.getEsAltaRapida() + "')";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean createCatalogoProveedor(Connection conn, DatosProveedor datProveedor, String unidadEjec) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "INSERT INTO dbo.mCatalogoProveedor (cIdRFC,cRazonSocial,cNumeroRegistro,cGiro,cRepresentanteLegal,cIdEntidadFederativa,cCalle,cNumeroExterno," + "cNumeroInterno,cColonia,cMunicipio,cCodigoPostal,cEmail,cUrl,cIdUnidadEjecutora,nIdPyme,lHabilitado,cCURP,idRegimenFiscal,cNumeroREPSE,cEsRESICO, alta_rapida)" + " VALUES('" + datProveedor.getRfc() + "','" + ((datProveedor.getnTipoPersona() == 1) ? datProveedor.getcRazonSocial() : datProveedor.getcNombre() + " " + datProveedor.getcApellidoPat() + " " + datProveedor.getcApellidoMat()) + "',' ','" + datProveedor.getcGiro() + "','" + ((datProveedor.getnTipoPersona() == 1) ? datProveedor.getcNombre() + " " + datProveedor.getcApellidoPat() + " " + datProveedor.getcApellidoMat() : "") + "','" + (datProveedor.getnEntidadFederativa() < 10 ? "0" + datProveedor.getnEntidadFederativa() : datProveedor.getnEntidadFederativa()) + "','" + datProveedor.getcCalle() + "','" + datProveedor.getcNumeroExt() + "','" + datProveedor.getcNumeroInt() + "','" + datProveedor.getcColonia() + "',(SELECT cat.mpo_nombre  FROM CAT_MUNICIPIO cat WITH(NOLOCK) where ID_ESTADO=" + datProveedor.getnEntidadFederativa() + " and ID_MUNICIPIO=" + datProveedor.getnMunicipio() + "),'" + datProveedor.getcCodigoPost() + "','" + datProveedor.getcEmail() + "','" + datProveedor.getcPaginaWeb() + "','" + unidadEjec + "'," + datProveedor.getnPyme() + "," + datProveedor.getnProveedorHabilitado() + ",'" + ((datProveedor.getnTipoPersona() == 1) ? "" : datProveedor.getcCurp()) + "'" + "," + datProveedor.getIdRegimenFiscal() + "," + "'" + datProveedor.getNumRepse() + "'" + "," + "'" + Proveedor.esResico(datProveedor.getIdRegimenFiscal()) + "','" + datProveedor.getEsAltaRapida() + "')";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean existTipoTelefono(Connection conn, String cRFC, int nTipoTelefono) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        String query = "";
        try {
            query = "select *from mCatalogoProveedorTelefono  WITH(NOLOCK) where cIdRFC='" + cRFC + "' and nIdTipoTelefono=" + nTipoTelefono;
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                success = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean esActCtaBancaria(Connection conn, String cFolio) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int bEsActCta = 0;
        String query = "select isnull(bEsActCta,0) from tAltaProveedor with(Nolock) where cFolio='" + cFolio + "' ";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                bEsActCta = rs.getInt(1);
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return (bEsActCta == 1);
    }

    public static boolean disabledProveedor(Connection conn, String cRFC) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "UPDATE mCatalogoProveedor set lHabilitado=0 where REPLACE(cIdRFC,'-','')='" + cRFC.replaceAll("-", "") + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean enabledProveedor(Connection conn, String cRFC) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "UPDATE mCatalogoProveedor set lHabilitado=1 where cIdRFC='" + cRFC + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean updateTipoTelefono(Connection conn, String cNumeroTelefono, String cRFC, int nTipoTelefono, int nIdTelefono) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "UPDATE mCatalogoProveedorTelefono set cTelefono='" + cNumeroTelefono + "' where cIdRFC='" + cRFC + "' and nIdTelefono=" + nIdTelefono + " and nIdTipoTelefono=" + nTipoTelefono;
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean CreateTipoTelefono(Connection conn, String cNumeroTelefono, String cRFC, int nTipoTelefono, int nIdTelefono) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "INSERT INTO dbo.mCatalogoProveedorTelefono (cIdRFC,nIdTelefono,nIdTipoTelefono,cTelefono)" + " VALUES('" + cRFC + "'," + nIdTelefono + "," + nTipoTelefono + ",'" + cNumeroTelefono + "')";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean deleteTelefonoProveedor(Connection conn, String cRFC) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "Delete dbo.mCatalogoProveedorTelefono where cIdRFC='" + cRFC + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public String obtienecBeneficiario(Connection conn, String cRFC, int nTipoPersona) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String cBeneficiario = "";
        String query = "";
        try {
            query = "SELECT case when " + nTipoPersona + "=3 then'E'+ RIGHT('00000'+CAST(nIdEmpleado AS VARCHAR(5)),5) else 'C'+ RIGHT('00000'+CAST(cBeneficiario AS VARCHAR(5)),5)end ascBeneficiario FROM tBeneficiario WITH(NOLOCK) WHERE RTRIM(LTRIM(dRFC))='" + cRFC + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                cBeneficiario = rs.getString(1);
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return cBeneficiario;
    }

    public String obtieneCBENActual(Connection conn, String cRFC) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String cBeneficiario = "";
        String query = "";
        try {
            query = "SELECT CBEN FROM tBeneficiario WITH(NOLOCK) WHERE RTRIM(LTRIM(dRFC))='" + cRFC + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                cBeneficiario = rs.getString(1);
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return cBeneficiario;
    }

    public String obtieneCBENHistorico(Connection conn, String cRFC, int nTipoCBEN) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String cBeneficiario = "";
        String query = "";
        try {
            query = " SELECT cCBEN FROM tCBENAltaEmpleado WITH(NOLOCK) where nTipoCBEN=" + nTipoCBEN + " and cRFC='" + cRFC + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                cBeneficiario = rs.getString(1);
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return cBeneficiario;
    }

    public String generacBENProv(Connection conn) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String cBENProv = "";
        String query = "";
        try {
            query = "select cParametro+RIGHT('00000'+CAST(cValor AS VARCHAR(5)),5) as cben From mSistema with(nolock) where nIdParametro=34";
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                cBENProv = rs.getString(1);
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return cBENProv;
    }

    public boolean incrementCBENProveedor(Connection conn) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "update mSistema set cValor=cValor+1	where nIdParametro=34";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean saveCBENAlta(Connection conn, String cRFC, int nTipoCBEN, String cCBEN) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "INSERT INTO dbo.tCBENAltaEmpleado (cRFC,nTipoCBEN,cCBEN,fFechaCBEN)" + " VALUES('" + cRFC + "'," + nTipoCBEN + ",'" + cCBEN + "',GETDATE())";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean saveBitacoraCBEN(Connection conn, String cRFC, String cCBENAnterior, String cCBENNuevo) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "INSERT INTO dbo.tBitacoraCBEN (nvoCBEN,CBENAnterior,dRFC,fModificacion)" + " VALUES('" + cCBENNuevo + "','" + cCBENAnterior + "','" + cRFC + "',GETDATE())";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean saveBeneficiarioCaso(Connection conn, String cRFC, int nCaso) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "INSERT INTO dbo.tBeneficiarioCaso   ( id_caso, cRFC )" + " VALUES(" + nCaso + ",'" + cRFC + "')";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean existeBeneficiarioCaso(Connection conn, String cRFC, int nCaso) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        String query = "";
        try {
            query = "select * from tBeneficiarioCaso WITH(NOLOCK) WHERE id_caso=" + nCaso + " and cRFC='" + cRFC + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                success = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean isNewCBEN(Connection conn, String cIdRFC) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        String query = "SELECT isnull(cNvoCBEN,0) as cNvoCBEN FROM tAltaProveedor WITH(NOLOCK) WHERE cIdRFC='" + cIdRFC + "'";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt(1) == 0)
                    success = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean copyCtasTmp(Connection conn, String cFolio, int nBCBEnviadoSICOP) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = new StringBuilder();
        try {
            query.append("INSERT INTO tBeneficiarioCuentasBancarias (dRFC,cBanco,cPlaza,dCuentaBancaria,dDigitoVerificador,cStatusCuenta,dBanco,dSucursal,cUsuarioModifico,fCuentaModifico,nBCBEnviadoSICOP, subCuentaBancaria)");
            query.append("SELECT dRFC,cBanco,cPlaza,dCuentaBancaria,dDigitoVerificador,1,dBanco,dSucursal,cUsuarioModifico,fCuentaModifico," + nBCBEnviadoSICOP);
            query.append(", CAST(cBanco  AS VARCHAR(3) )+ CAST(cPlaza AS VARCHAR(3) ) + dCuentaBancaria + dDigitoVerificador");
            query.append(" FROM tBeneficiarioCuentasBancariasTmp WITH(NOLOCK) WHERE cFolio='" + cFolio + "'");
            log.info(query);
            ps = conn.prepareStatement(query.toString());
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean deleteCtasTmp(Connection conn, String cFolio) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "DELETE FROM tBeneficiarioCuentasBancariasTmp WHERE cFolio='" + cFolio + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean deleteCtaTmp(Connection conn, DatosProveedor datProv) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "DELETE tBeneficiarioCuentasBancariasTmp WHERE dRFC='" + datProv.getRfc1() + datProv.getRfc2() + datProv.getRfc3() + "' AND REPLACE(dBanco,',','')=REPLACE('" + datProv.getCuentasBancarias().get(0).getcNameBanco() + "',',','') AND cPlaza='" + datProv.getCuentasBancarias().get(0).getcPlaza() + "' AND dDigitoVerificador=" + datProv.getCuentasBancarias().get(0).getnDigitoVerificador() + " AND dSucursal='" + datProv.getCuentasBancarias().get(0).getcSucursal() + "' AND cFolio='" + datProv.getcFolio() + "' AND dCuentaBancaria='" + datProv.getCuentasBancarias().get(0).getcCuentaBancaria() + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public static boolean deleteCtaTmp(Connection conn, DatosCtaBancaria ctaBancaria) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "DELETE tBeneficiarioCuentasBancariasTmp WHERE dRFC='" + ctaBancaria.getRfc() + "' AND cBanco='" + ctaBancaria.getcBanco() + "' AND dCuentaBancaria='" + ctaBancaria.getcCuentaBancaria() + "'" + " AND cPlaza='" + ctaBancaria.getcPlaza() + "' AND dDigitoVerificador = " + ctaBancaria.getnDigitoVerificador() + " AND cFolio='" + ctaBancaria.getFolio() + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public static boolean guardaCtaEliminada(Connection conn, DatosProveedor datProv) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "INSERT INTO tBeneficiarioCuentasBancariasEliminadas (cFolio,cUsuarioModifico,dRFC,cBanco,cPlaza,dCuentaBancaria,dDigitoVerificador,cStatusCuenta,dBanco,dSucursal,fCuentaModifico,nBCBEnviadoSICOP)" + " SELECT '" + datProv.getcFolio() + "','" + datProv.getCuentasBancarias().get(0).getcUsuarioModifico() + "',dRFC,cBanco,cPlaza,dCuentaBancaria,dDigitoVerificador,cStatusCuenta,dBanco,dSucursal,GETDATE(),nBCBEnviadoSICOP FROM tBeneficiarioCuentasBancarias WITH(NOLOCK) WHERE dRFC='" + datProv.getRfc1() + datProv.getRfc2() + datProv.getRfc3() + "' AND dBanco=REPLACE('" + datProv.getCuentasBancarias().get(0).getcNameBanco() + "',',','') AND cPlaza='" + datProv.getCuentasBancarias().get(0).getcPlaza() + "' AND dDigitoVerificador=" + datProv.getCuentasBancarias().get(0).getnDigitoVerificador() + " AND dSucursal='" + datProv.getCuentasBancarias().get(0).getcSucursal() + "' AND dCuentaBancaria='" + datProv.getCuentasBancarias().get(0).getcCuentaBancaria() + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean respaldaPaginaDocto(Connection conn, String tituloAplicacion, int idCarpetaPadre, int idGabinete, int idDocumento) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "insert into IMX_PAGINA_BORRADA " + "select *,convert(date,GETDATE())fechaBorrado from IMX_PAGINA  WITH(NOLOCK) WHERE TITULO_APLICACION='" + tituloAplicacion + "' AND ID_CARPETA_PADRE=" + idCarpetaPadre + " AND ID_GABINETE=" + idGabinete + " and ID_DOCUMENTO=" + idDocumento;
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean deletePaginaDocto(Connection conn, String tituloAplicacion, int idCarpetaPadre, int idGabinete, int idDocumento) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "delete IMX_PAGINA WHERE TITULO_APLICACION='" + tituloAplicacion + "' AND ID_CARPETA_PADRE=" + idCarpetaPadre + " AND ID_GABINETE=" + idGabinete + " and ID_DOCUMENTO=" + idDocumento;
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean respaldaDoctoCtaBancaria(Connection conn, String tituloAplicacion, int idCarpetaPadre, int idGabinete, int idDocumento, String cMotivoElimina, String cLogin) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "insert into IMX_DOCUMENTO_BORRADO " + "SELECT  " + "[TITULO_APLICACION],[ID_GABINETE] " + ",[ID_CARPETA_PADRE],[ID_DOCUMENTO] " + ",[NOMBRE_DOCUMENTO],[NOMBRE_USUARIO] " + ",[PRIORIDAD],[ID_TIPO_DOCTO] " + ",[FH_CREACION],[FH_MODIFICACION] " + ",[NUMERO_ACCESOS],[NUMERO_PAGINAS] " + ",[TITULO],[AUTOR] " + ",[MATERIA],[DESCRIPCION] " + ",[CLASE_DOCUMENTO],[ESTADO_DOCUMENTO] " + ",[TAMANO_BYTES],[COMPARTIR] " + ",[TOKEN_COMPARTIR],[FH_VIGENCIA] " + ",'" + cMotivoElimina + "'motivo,'" + cLogin + "'usuarioBorro,CONVERT(date,getdate()) fechaBorrado " + " FROM IMX_DOCUMENTO WITH(NOLOCK) WHERE TITULO_APLICACION='" + tituloAplicacion + "' AND ID_GABINETE=" + idGabinete + " AND ID_CARPETA_PADRE=" + idCarpetaPadre + " AND ID_DOCUMENTO=" + idDocumento;
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean deleteDoctoCtaBancaria(Connection conn, String tituloAplicacion, int idCarpetaPadre, int idGabinete, int idDocumento) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "delete IMX_DOCUMENTO WHERE TITULO_APLICACION='" + tituloAplicacion + "' AND ID_GABINETE=" + idGabinete + " AND ID_CARPETA_PADRE=" + idCarpetaPadre + " AND ID_DOCUMENTO=" + idDocumento;
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean deleteCtaBancaria(Connection conn, DatosProveedor datProv) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "DELETE tBeneficiarioCuentasBancarias WHERE dRFC='" + datProv.getRfc1() + datProv.getRfc2() + datProv.getRfc3() + "' AND REPLACE(dBanco,',','')=REPLACE('" + datProv.getCuentasBancarias().get(0).getcNameBanco() + "',',','') AND cPlaza='" + datProv.getCuentasBancarias().get(0).getcPlaza() + "' AND dDigitoVerificador=" + datProv.getCuentasBancarias().get(0).getnDigitoVerificador() + " AND dSucursal='" + datProv.getCuentasBancarias().get(0).getcSucursal() + "' AND dCuentaBancaria='" + datProv.getCuentasBancarias().get(0).getcCuentaBancaria() + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean copytAltaProveedor(Connection conn, String cFolio, String cLoginMod) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "INSERT INTO tAltaProveedorAnterior(cFolio,cIdTipoPersona,cTipoRegistro,cIdRFC,cRazonSocial,cCURP,cApellidoPaterno,cApellidoMaterno,cNombre,cGiro,cIdEntidadFederativa,cIdMunicipio,cCalle,cNumeroExterno,cNumeroInterno,cColonia,cCodigoPostal,cEmail,cUrl,nIdPyme,nIdTipoTelefono,cTelefono,cActualizacion,cDocumentoHaplicado,cObservaciones,cIdUsuarioCaptura,cIdUsuarioValida,cIdUsuarioAutoriza,cIdUsuaUltModif,cExtranjero,cPais,cNvoCBEN,cFechaModificacion)" + "SELECT cFolio,cIdTipoPersona,cTipoRegistro,cIdRFC,cRazonSocial,cCURP,cApellidoPaterno,cApellidoMaterno,cNombre,cGiro,cIdEntidadFederativa,cIdMunicipio,cCalle,cNumeroExterno,cNumeroInterno,cColonia,cCodigoPostal,cEmail,cUrl,nIdPyme,nIdTipoTelefono,cTelefono,cActualizacion,cDocumentoHaplicado,cObservaciones,cIdUsuarioCaptura,cIdUsuarioValida,cIdUsuarioAutoriza,'" + cLoginMod + "',cExtranjero,cPais,cNvoCBEN,GETDATE() FROM tAltaProveedor WITH(NOLOCK)  WHERE cFolio='" + cFolio + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public int queTipoPersonaEs(Connection conn, String crfc) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int tipoPersona = 0;
        String query = "select case when SUBSTRING(CBEN,1,1)='P' then 1 when SUBSTRING(CBEN,1,1)='E' then 3 else 2 end tipoPers from tBeneficiario WITH (NOLOCK) where dRFC='" + crfc + "'";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                tipoPersona = rs.getInt(1);
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return tipoPersona;
    }

    public boolean hayProcedAdjudicacionEnTramite(Connection conn, String cIdRFC) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        String query = "select *from mProcedimiento proced with(nolock) inner join mProcedimientoAdjudicacion as adj with(nolock) " + " on adj.cIdProcedimiento=proced.cIdProcedimiento where proced.nIdEstado=1 and adj.cIdRFC='" + cIdRFC + "'";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                success = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public static boolean hayAdjudicacionEnTramite(Connection conn, String cIdRFC) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        String query = "select * from " + " mProcedimientoAdjudicacion as adj with(nolock) " + " WHERE REPLACE(cIdRFC,'-','')='" + cIdRFC.replace("-", "") + "'";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                success = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean hayContratoPendienteDePago(Connection conn, String cIdRFC) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        String query = "select cIdContrato,comEnc.totalComp,pagos.Pagado " + "			from (select " + "				cIdContrato,SUM(mImporte)totalComp from tCompromisoEncabezado as comEnc with(nolock)" + "				inner join tCompromisoDetalle comDet with(nolock)" + "				on comDet.nFolioCompromiso=comEnc.nFolioCompromiso" + "				and comEnc.cDocumentoHaplicado='S'" + "				where comEnc.cTipoContrato='DI'" + "				group by cIdContrato" + "			)comEnc" + "			inner join(" + "				select cont.cIdContratoDefinitivo from mContrato as cont with(nolock)  left join mContratoTerminacionAnticipada as terAnt with(Nolock) on terAnt.cIdContratoDefinitivo=cont.cIdContratoDefinitivo where cont.nIdEstado=4  and terAnt.cIdContratoDefinitivo is null and cIdRFC='" + cIdRFC + "'" + "				union" + "				select cIdPedidoDefinitivo  from mPedido as ped with(nolock)where ped.nIdEstado=4 and cIdRFC='" + cIdRFC + "'" + "			)cont on cont.cIdContratoDefinitivo=comEnc.cIdContrato" + "			left join(" + "				SELECT " + "					SUM(ISNULL(sub.pagos,0)-ISNULL(sub.totalReintegro,0))Pagado" + "					,sub.cFolioPAGODIVERSO folioContPed" + "					FROM	" + "						(SELECT " + "						CONVERT(VARCHAR,sum(isnull(d.mImporteMasIva,0)),1)pagos" + "						,ISNULL(reintegro.totalReintegro,0)totalReintegro" + "						,e.cFolioPAGODIVERSO" + "						FROM dbo.tPAGODIVERSOEncabezado e WITH (NOLOCK)	" + "						INNER JOIN dbo.tPAGODIVERSODetalle d WITH (NOLOCK) ON e.nFolioPAGODIVERSO = d.nFolioPAGODIVERSO AND e.cDocumentoHaplicado='S'" + "						and e.RFC=replace('" + cIdRFC + "','-','')		" + "						inner join tPagadoEncabezado as pe with(Nolock) on pe.caNoContrarrecibo=e.caNoContrarrecibo and pe.cDocumentoHaplicado='S'" + "						LEFT join (" + "									SELECT " + "									SUM(mImporte) totalReintegro,cxp" + "									FROM  tReintegroEncabezado as reintEnc with(Nolock)" + "									inner join tReintegroDetalle as reintDet with(Nolock) " + "									on reintDet.nFolioReintegro=reintEnc.nFolioReintegro and reintEnc.cDocumentoHaplicado='S'" + "									GROUP BY cxp" + "						)reintegro ON reintegro.cxp=e.caNoContrarrecibo" + "						GROUP BY reintegro.totalReintegro,e.cFolioPAGODIVERSO" + "						)sub" + "						GROUP BY sub.cFolioPAGODIVERSO	" + "			)pagos on pagos.folioContPed=cont.cIdContratoDefinitivo" + "			where comEnc.totalComp>isnull(pagos.Pagado,0) ";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                success = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean hayContratoFederalizadoPendienteDePago(Connection conn, String cIdRFC) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        String query = "select  " + "comEnc.cIdContrato,comEnc.totalComp " + ",pagos.Pagado " + "from (select  " + "	cIdContrato,SUM(mImporte)totalComp from tCompromisoEncabezado as comEnc with(nolock) " + "	inner join tCompromisoDetalle comDet with(nolock) " + "	on comDet.nFolioCompromiso=comEnc.nFolioCompromiso " + "	and comEnc.cDocumentoHaplicado='S' " + "	where comEnc.cTipoContrato='FE' " + "	group by cIdContrato " + ")comEnc " + "inner join pContratoFederalizado cont on cont.cIdContrato=comEnc.cIdContrato and cIdRFC='" + cIdRFC + "' " + "left join( " + "	SELECT  " + "		SUM(ISNULL(sub.pagos,0)-ISNULL(sub.totalReintegro,0))Pagado " + "		,sub.cFolioContratoObra folioContPed " + "		FROM	 " + "			(SELECT  " + "			CONVERT(VARCHAR,sum(isnull(d.mImporteMasIva,0)),1)pagos " + "			,ISNULL(reintegro.totalReintegro,0)totalReintegro " + "			,e.cFolioContratoObra " + "			FROM dbo.tPAGOFEDERALIZADOEncabezado e WITH (NOLOCK)	 " + "			INNER JOIN dbo.tPAGOFEDERALIZADODetalle d WITH (NOLOCK) ON e.nFolioPAGOFEDERALIZADO = d.nFolioPAGOFEDERALIZADO AND e.cDocumentoHaplicado='S' " + "			and e.RFC='" + cIdRFC + "'	 " + "			inner join tPagadoEncabezado as pe with(Nolock) on pe.caNoContrarrecibo=e.caNoContrarrecibo and pe.cDocumentoHaplicado='S' " + "			LEFT join ( " + "						SELECT  " + "						SUM(mImporte) totalReintegro,cxp " + "						FROM  tReintegroEncabezado as reintEnc with(Nolock) " + "						inner join tReintegroDetalle as reintDet with(Nolock)  " + "						on reintDet.nFolioReintegro=reintEnc.nFolioReintegro and reintEnc.cDocumentoHaplicado='S' " + "						GROUP BY cxp " + "			)reintegro ON reintegro.cxp=e.caNoContrarrecibo " + "			GROUP BY reintegro.totalReintegro,e.cFolioContratoObra " + "			)sub " + "			GROUP BY sub.cFolioContratoObra " + ")pagos on pagos.folioContPed=cont.cIdContrato " + "where comEnc.totalComp>isnull(pagos.Pagado,0) ";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                success = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean hayBoletoAvionPendienteComprobar(Connection conn, String cIdRFC) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        String query = "select *from tLayoutVuelosDet with(Nolock) where Status='A' and RFC='" + cIdRFC + "'";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                success = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean esDeudor(Connection conn, String cIdRFC) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        String query = "select * from tsaldos with(Nolock) where nCuenta like '11232%' and cSubCuenta = '" + cIdRFC + "' and mSaldoArrastre > 0";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                success = true;
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean hayCambioTipoPersona(Connection conn, String cIdRFC, int ntipoPersona, String cPersona) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        String query = "select cIdTipoPersonaRFC,case when substring(CBEN,1,1)='P' then 'PROVEEDOR' else 'BENEFICIARIO' end tipoReg from tBeneficiario with(Nolock) where dRFC='" + cIdRFC + "'";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                if (!(rs.getInt(1) == ntipoPersona && cPersona.equalsIgnoreCase(rs.getString(2)))) {
                    success = true;
                }
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public int idOperAnt(Connection conn, String cFolio) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int nIdOperAnt = 0;
        String query = "select isnull(nIdOperAnt,0) from tAltaProveedor with(Nolock) where cFolio='" + cFolio + "' ";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                nIdOperAnt = rs.getInt(1);
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return nIdOperAnt;
    }

    public int idOperAct(Connection conn, int idCaso) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int nIdOperAct = 0;
        String query = "select ID_OPER from cg_caso_operacion with(Nolock) where id_caso=" + idCaso;
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                nIdOperAct = rs.getInt(1);
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return nIdOperAct;
    }

    public boolean updatetAltaProveedorIdOperAnt(Connection conn, String cFolio, int nIdOper) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "UPDATE dbo.tAltaProveedor SET nIdOperAnt=" + nIdOper + " WHERE cFolio='" + cFolio + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean updatetAltaProveedorObservacion(Connection conn, String cFolio, String cObservaciones) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "UPDATE dbo.tAltaProveedor SET cObservaciones='" + cObservaciones + "' WHERE cFolio='" + cFolio + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean updateHabilitaLiberaCaso(Connection conn, String cFolio, int nLibCaso) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "UPDATE dbo.tAltaProveedor SET bLiberaCaso=" + nLibCaso + " WHERE cFolio='" + cFolio + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean updateEsActCta(Connection conn, String cFolio, int nEsActCta) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "UPDATE dbo.tAltaProveedor SET bEsActCta=" + nEsActCta + " WHERE cFolio='" + cFolio + "'";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean esUnEFO(Connection conn, String cIdRFC) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        String query = "select * from mCatalogoEFOS with(Nolock) where nSituacion in(1,3) and cIdRFC='" + cIdRFC + "'";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                success = true;
                // success="Est\u00e9 contribuyente o sociedad est\u00e1
                // reconocida por la autoridad como Empresa Facturadora de
                // Operaciones Simuladas\"EFOS\"";
            }
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean updateCasoAConsulta(Connection conn, String cFolio) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "UPDATE CG_CASO_OPERACION set ID_OPER=5, CO_RESPONSABLE='CONSULTA_PROVEEDOR' WHERE ID_CASO = (SELECT ID_CASO FROM CG_CASO WITH(NOLOCK) WHERE C_FOLIO='" + cFolio + "')";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean saveBitacoraAltaProveedor(Connection conn, String cFolio, String cDescripcionMovimiento, String cDocumentoHAplicadoActual, String cDocumentoHAplicadoAnterior, int nIdOperActual, int nIdOperAnterior, String cLogin) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "insert into tBitacoraAltaProveedor (cFolio,cDescripcionMovimiento,cDocumentoHAplicadoActual,cDocumentoHAplicadoAnterior,nIdOperActual,nIdOperAnterior,cLogin,fFechaMovimiento)" + "values('" + cFolio + "','" + cDescripcionMovimiento + "','" + cDocumentoHAplicadoActual + "','" + cDocumentoHAplicadoAnterior + "'," + nIdOperActual + "," + nIdOperAnterior + ",'" + cLogin + "',GETDATE())";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public boolean saveBitacoraAltaProveedorVarios(Connection conn, String cFolios, String cDescripcionMovimiento, String cDocumentoHAplicadoActual, String cDocumentoHAplicadoAnterior, int nIdOperActual, int nIdOperAnterior, String cLogin) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "insert into tBitacoraAltaProveedor (cFolio,cDescripcionMovimiento,cDocumentoHAplicadoActual,cDocumentoHAplicadoAnterior,nIdOperActual,nIdOperAnterior,cLogin,fFechaMovimiento)" + " select cFolio,'" + cDescripcionMovimiento + "','" + cDocumentoHAplicadoActual + "','" + cDocumentoHAplicadoAnterior + "'," + nIdOperActual + "," + nIdOperAnterior + ",'" + cLogin + "',GETDATE() from taltaproveedor with(Nolock) where cFolio in(" + cFolios + ") group by cFolio";
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return success;
    }

    public static Caso getTramiteProveedor(Connection conn, String rfc) throws SQLException, ProveedorException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT cFolio FROM tAltaProveedor WITH(NOLOCK) WHERE REPLACE(cidrfc, '-', '') = REPLACE(?, '-', '')");
        ScalarHandler scalarHandler = new ScalarHandler("cFolio");
        QueryRunner runner = new QueryRunner();
        String folio = (String) runner.query(conn, query.toString(), scalarHandler, rfc);
        if (StringUtils.isBlank(folio))
            throw new ProveedorException("No se encontro tramite de alta para el proveedor: " + rfc);
        Caso c = new Caso();
        c.setFolio(folio);
        return CasoManager.select(conn, c);
    }

    public static void insertaProveedorDescartado(Connection conn, String folio, String rfc) throws GestionException {
        StringBuilder query = new StringBuilder("INSERT INTO tAltaProveedorDescartado (cFolio,cIdRFC,fDescartado) VALUES (?,?,GETDATE())");
        PreparedStatement psUpdate = null;
        try {
            psUpdate = conn.prepareStatement(query.toString());
            psUpdate.setString(1, folio);
            psUpdate.setString(2, rfc);
            psUpdate.executeUpdate();
        } catch (SQLException e) {
            throw new GestionException(e);
        } finally {
            CloseObject.closeObject(psUpdate);
        }
    }

    public static boolean esProveedorConPagos(Connection conn, String rfc) throws GestionException {
        StringBuilder sbSelect = new StringBuilder("SELECT COUNT(*) AS pagos FROM v_pagosBeneficiarios2   WITH(NOLOCK) WHERE RFC=REPLACE (?,'-','')");
        PreparedStatement psSelect = null;
        ResultSet rs = null;
        try {
            psSelect = conn.prepareStatement(sbSelect.toString());
            psSelect.setString(1, rfc);
            rs = psSelect.executeQuery();
            if (rs.next())
                return rs.getInt(1) > 0;
            return false;
        } catch (SQLException e) {
            throw new GestionException(e);
        } finally {
            CloseObject.closeObject(psSelect);
        }
    }

    public static void eliminaProveedorBeneficiario(Connection conn, String folio, String rfc) throws GestionException {
        StringBuilder query = new StringBuilder("INSERT INTO tAltaProveedorDescartado (cFolio,cIdRFC,fDescartado) VALUES (?,?,GETDATE())");
        PreparedStatement psUpdate = null;
        try {
            psUpdate = conn.prepareStatement(query.toString());
            psUpdate.setString(1, folio);
            psUpdate.setString(2, rfc);
            psUpdate.executeUpdate();
        } catch (SQLException e) {
            throw new GestionException(e);
        } finally {
            CloseObject.closeObject(psUpdate);
        }
    }

    public static void eliminaTramite(Connection conn, String folio, String login) throws SQLException {
        PreparedStatement pstmntDelProv = null;
        PreparedStatement pstmntDelBenef = null;
        PreparedStatement pstmntUpdate = null;
        StringBuilder queryDelProv = null;
        StringBuilder queryDelBenef = null;
        StringBuilder queryUpdate = null;
        try {
            queryDelProv = new StringBuilder("DELETE from tAltaProveedor with(rowlock) WHERE cFolio='" + folio + "'");
            pstmntDelProv = conn.prepareStatement(queryDelProv.toString());
            pstmntDelProv.executeUpdate();
            queryDelBenef = new StringBuilder("UPDATE tAltaProveedorDescartado SET cIdUsuarioElimina= '" + login + "' WHERE cFolio= '" + folio + "'");
            pstmntDelBenef = conn.prepareStatement(queryDelBenef.toString());
            pstmntDelBenef.executeUpdate();
            queryUpdate = new StringBuilder("DELETE FROM tBeneficiarioCuentasBancariasTmp WHERE cFolio='" + folio + "'");
            pstmntUpdate = conn.prepareStatement(queryUpdate.toString());
            pstmntUpdate.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmntDelProv);
            CloseObject.closeObject(pstmntDelBenef);
            CloseObject.closeObject(pstmntUpdate);
        }
    }

    public static void eliminaBeneficiario(Connection conn, String rfc) throws SQLException {
        StringBuilder queryDel = new StringBuilder("DELETE tBeneficiario  WHERE dRFC = REPLACE (?,'-','')");
        PreparedStatement pstmntUpdate = null;
        try {
            pstmntUpdate = conn.prepareStatement(queryDel.toString());
            pstmntUpdate.setString(1, rfc);
            pstmntUpdate.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmntUpdate);
        }
    }

    public static void eliminaBeneficiarioCtaBancaria(Connection conn, String rfc) throws SQLException {
        StringBuilder queryDel = new StringBuilder("DELETE tBeneficiarioCuentasBancarias  WHERE dRFC = REPLACE (?,'-','')");
        PreparedStatement pstmntUpdate = null;
        try {
            pstmntUpdate = conn.prepareStatement(queryDel.toString());
            pstmntUpdate.setString(1, rfc);
            pstmntUpdate.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmntUpdate);
        }
    }

    public static void cambiaStatusBeneficiario(Connection conn, int status, String rfc) throws SQLException {
        StringBuilder queryUpdate = new StringBuilder("UPDATE tBeneficiario SET nEnviadoSICOP=? WHERE dRFC= REPLACE (?,'-','')");
        PreparedStatement pstmntUpdate = null;
        try {
            pstmntUpdate = conn.prepareStatement(queryUpdate.toString());
            pstmntUpdate.setInt(1, status);
            pstmntUpdate.setString(2, rfc);
            pstmntUpdate.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmntUpdate);
        }
    }

    public static void cambiaStatusCtasBanBeneficiario(Connection conn, int status, String rfc) throws SQLException {
        StringBuilder queryUpdate = new StringBuilder("UPDATE tBeneficiarioCuentasBancarias SET nBCBEnviadoSICOP=? WHERE dRFC= REPLACE (?,'-','')");
        PreparedStatement pstmntUpdate = null;
        try {
            pstmntUpdate = conn.prepareStatement(queryUpdate.toString());
            pstmntUpdate.setInt(1, status);
            pstmntUpdate.setString(2, rfc);
            pstmntUpdate.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmntUpdate);
        }
    }

    public static String getRfcProveedor(Connection conn, String folio) throws GestionException {
        StringBuilder sbSelect = new StringBuilder("select cIdRFC  from taltaproveedor where cFolio = ?");
        PreparedStatement psSelect = null;
        ResultSet rs = null;
        try {
            psSelect = conn.prepareStatement(sbSelect.toString());
            psSelect.setString(1, folio);
            rs = psSelect.executeQuery();
            if (rs.next())
                return rs.getString(1);
            return "";
        } catch (SQLException e) {
            throw new GestionException(e);
        } finally {
            CloseObject.closeObject(psSelect);
        }
    }

    public DatosProveedor getAltaProveedorObj(Connection conn, String folio) throws ProveedorException, SQLException {
        Map<String, String> datosMap = getAltaProveedorMap(conn, folio);
        return getAltaProveedorObj(conn, datosMap);
    }

    public DatosProveedor getAltaProveedorObjByRFC(Connection conn, String rfc) throws ProveedorException, SQLException {
        Map<String, String> datosMap = getAltaProveedorMapByRFC(conn, rfc);
        return getAltaProveedorObj(conn, datosMap);
    }

    private DatosProveedor getAltaProveedorObj(Connection conn, Map<String, String> datosMap) throws SQLException {
        DatosProveedor dp = new DatosProveedor();
        dp.setcApellidoMat(datosMap.get("apellidoMaterno"));
        dp.setcApellidoPat(datosMap.get("apellidoPaterno"));
        dp.setcCalle(datosMap.get("calle"));
        dp.setcCodigoPost(datosMap.get("codigoPostal"));
        dp.setcColonia(datosMap.get("colonia"));
        dp.setcCurp(datosMap.get("curp"));
        dp.setcDocumentoHaplicado(datosMap.get("documentoHaplicado"));
        dp.setcEmail(datosMap.get("correo"));
        dp.setcFolio(datosMap.get("folio"));
        dp.setcGiro(datosMap.get("giro"));
        dp.setcNombre(datosMap.get("nombre"));
        dp.setcNumeroExt(datosMap.get("noExterior"));
        dp.setcNumeroInt(datosMap.get("noInterior"));
        dp.setcObservaciones(datosMap.get("observaciones"));
        dp.setcPaginaWeb(datosMap.get("url"));
        dp.setcPais(datosMap.get("pais"));
        dp.setcRazonSocial(datosMap.get("razonSocial"));
        dp.setcTelefono(datosMap.get("telefono"));
        dp.setCuentasBancarias(listCuentasBancariasTemp(conn, datosMap.get("folio")));
        dp.setEsAltaRapida(datosMap.get("altaRapida"));
        dp.setIdRegimenFiscal(StringUtils.isBlank(datosMap.get("regimenFiscal")) ? 0 : Integer.parseInt(datosMap.get("regimenFiscal")));
        dp.setnEntidadFederativa(StringUtils.isBlank(datosMap.get("estado")) ? 0 : Integer.parseInt(datosMap.get("estado")));
        dp.setnExtranjero(StringUtils.isBlank(datosMap.get("extranjero")) ? 0 : Integer.parseInt(datosMap.get("extranjero")));
        dp.setnMunicipio(StringUtils.isBlank(datosMap.get("municipio")) ? 0 : Integer.parseInt(datosMap.get("municipio")));
        dp.setnNumEmpleado(StringUtils.isBlank(datosMap.get("numEmpleado")) ? 0 : Integer.parseInt(datosMap.get("numEmpleado")));
        dp.setnPyme(StringUtils.isBlank(datosMap.get("pyme")) ? 0 : Integer.parseInt(datosMap.get("pyme")));
        dp.setnTipoPersona(StringUtils.isBlank(datosMap.get("tipoPersona")) ? 0 : Integer.parseInt(datosMap.get("tipoPersona")));
        dp.setnTipoTelefono(StringUtils.isBlank(datosMap.get("tipoTelefono")) ? 0 : Integer.parseInt(datosMap.get("tipoTelefono")));
        dp.setNumRepse(datosMap.get("numRepse"));
        if (!StringUtils.isEmpty(datosMap.get("rfc"))) {
            String rfc = datosMap.get("rfc");
            String[] rfcParts = rfc.split("-");
            dp.setRfc(rfc);
            dp.setRfc1(rfcParts[0]);
            dp.setRfc2(rfcParts[1]);
            dp.setRfc3(rfcParts[2]);
        }
        return dp;
    }

    public StringBuilder getQueryProveedor() {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT tap.cidtipopersona       AS tipoPersona, ");
        query.append("  	   tctp.ctipopersona        AS descTipoPersona, ");
        query.append("        tap.id_regimen_fiscal    AS regimenFiscal, ");
        query.append("        tap.cidrfc               AS rfc, ");
        query.append("        tap.ccurp                AS curp, ");
        query.append("        tap.crazonsocial         AS razonSocial, ");
        query.append("        tap.cnombre              AS nombre, ");
        query.append("        tap.capellidopaterno     AS apellidoPaterno, ");
        query.append("        tap.capellidomaterno     AS apellidoMaterno, ");
        query.append("        tap.cgiro                AS giro, ");
        query.append("        tap.nidpyme              AS pyme, ");
        query.append("        mcp.cpyme                AS descPyme, ");
        query.append("        tap.cidentidadfederativa AS estado, ");
        query.append("        ce.edo_nombre            AS descEstado, ");
        query.append("        tap.cidmunicipio         AS municipio, ");
        query.append("        cm.mpo_nombre            AS descMunicipio, ");
        query.append("        tap.ccalle               AS calle, ");
        query.append("        tap.cnumeroexterno       AS noExterior, ");
        query.append("        tap.cnumerointerno       AS noInterior, ");
        query.append("        tap.ccolonia             AS colonia, ");
        query.append("        tap.ccodigopostal        AS codigoPostal, ");
        query.append("        tap.cemail               AS correo, ");
        query.append("        ISNULL(tap.curl,'')      AS url, ");
        query.append("        tap.ctelefono            AS telefono, ");
        query.append("        tap.nidtipotelefono      AS tipoTelefono, ");
        query.append("        mctt.ctipotelefono       AS descTipoTelefono, ");
        query.append("        UPPER(ISNULL(trf.regimen_fiscal,'')) AS descRegimenFiscal, ");
        query.append("        tap.cDocumentoHaplicado  AS documentoHaplicado, ");
        query.append("        tap.cFolio               AS folio, ");
        query.append("        tap.cObservaciones       AS observaciones, ");
        query.append("        tap.cPais                AS pais, ");
        query.append("        tap.alta_rapida          AS altaRapida     , ");
        query.append("        tap.cExtranjero          AS extranjero     , ");
        query.append("        tap.nIdEmpleado          AS numEmpleado     , ");
        query.append("        tap.cNumeroREPSE         AS numRepse");
        query.append(" FROM   taltaproveedor tap WITH(nolock) ");
        query.append("        INNER JOIN tcattipopersona tctp WITH(nolock) ");
        query.append("                ON tap.cidtipopersona = tctp.ntipopersona ");
        query.append("        INNER JOIN mcatalogopyme mcp WITH(nolock) ");
        query.append("                ON tap.nidpyme = mcp.nidpyme ");
        query.append("        INNER JOIN cat_estados ce WITH(nolock) ");
        query.append("                ON tap.cidentidadfederativa = ce.id_estado ");
        query.append("        INNER JOIN cat_municipio cm WITH(nolock) ");
        query.append("                ON tap.cidmunicipio = cm.id_municipio ");
        query.append("                   AND tap.cidentidadfederativa = cm.id_estado ");
        query.append("        INNER JOIN mcatalogotipotelefono mctt WITH(nolock) ");
        query.append("                ON tap.nidtipotelefono = mctt.nidtipotelefono ");
        query.append("        LEFT JOIN tcat_regimen_fiscal trf WITH(nolock) ");
        query.append("                 ON tap.id_regimen_fiscal  = trf.id_regimen_fiscal  ");
        return query;
    }

    public Map<String, String> getAltaProveedorMapByRFC(Connection conn, String rfc) throws ProveedorException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            StringBuilder query = new StringBuilder();
            query = getQueryProveedor();
            query.append(" WHERE  REPLACE(tap.cidrfc,'-','') = REPLACE( ?,'-','') ");
            log.trace("Executing: " + query + "[" + rfc + "]");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, rfc);
            rs = ps.executeQuery();
            return RSToTable.rsToMapCaseSensitive(rs);
        } catch (Exception e) {
            throw new ProveedorException(e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public Map<String, String> getAltaProveedorMap(Connection conn, String folio) throws ProveedorException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            StringBuilder query = new StringBuilder();
            query = getQueryProveedor();
            query.append("WHERE  cfolio = ?   ");
            log.trace("Executing: " + query + "[" + folio + "]");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, folio);
            rs = ps.executeQuery();
            return RSToTable.rsToMapCaseSensitive(rs);
        } catch (Exception e) {
            throw new ProveedorException(e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public Caso avanzaOperacionAltaRapida(Connection conn, Usuario u, String folio, String responsable, String operacion) throws ProveedorException {
        Caso c = new Caso();
        c.setFolio(folio);
        try {
            c = CasoManager.select(conn, c);
            Map<String, String> datos = com.syc.gestion.util.Util.readValuesCasoDato(c.getCasoDato());
            return cbl.avanzaCaso(conn, c, u.getLogin(), "", new String[] { responsable }, new String[] { operacion }, datos, null);
        } catch (SQLException | GestionException | IOException e) {
            throw new ProveedorException(e);
        }
    }

    public static DatosCtaBancaria buscaCuentaBancariaTemp(Connection conn, String folio, String clabe) throws SQLException {
        StringBuilder querySelect = new StringBuilder();
        querySelect.append(" SELECT cbanco               AS cBanco, ");
        querySelect.append("        dbanco               AS cNameBanco, ");
        querySelect.append("        cplaza               AS cPlaza, ");
        querySelect.append("        ddigitoverificador   AS dDigitoVerificador, ");
        querySelect.append("        dsucursal            AS dSucursal, ");
        querySelect.append("        dcuentabancaria      AS dCuentaBancaria, ");
        querySelect.append("        cbanco + cplaza + dcuentabancaria ");
        querySelect.append("        + ddigitoverificador AS cClabeInterbancaria, ");
        querySelect.append("        nbcbenviadosicop     AS nBCBEnviadoSICOP, ");
        querySelect.append("        0                    AS nEstatusCta, ");
        querySelect.append("        cusuariomodifico     AS cUsuarioModifico, ");
        querySelect.append("        ''                   AS cMotivoEliminaCta, ");
        querySelect.append("        dRFC                 AS rfc, ");
        querySelect.append("        cFolio               AS folio ");
        querySelect.append(" FROM   tbeneficiariocuentasbancariastmp WITH(nolock) ");
        querySelect.append(" WHERE cFolio = ? ");
        querySelect.append("   AND cbanco + cplaza + dcuentabancaria + ddigitoverificador = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        DatosCtaBancaria cta = null;
        try {
            ps = conn.prepareStatement(querySelect.toString());
            ps.setString(1, folio);
            ps.setString(2, clabe);
            rs = ps.executeQuery();
            if (rs.next()) {
                cta = new DatosCtaBancaria();
                cta.setcBanco(rs.getString("cBanco"));
                cta.setcNameBanco(rs.getString("cNameBanco"));
                cta.setcPlaza(rs.getString("cPlaza"));
                cta.setnDigitoVerificador(rs.getInt("dDigitoVerificador"));
                cta.setcSucursal(rs.getString("dSucursal"));
                cta.setcCuentaBancaria(rs.getString("dCuentaBancaria"));
                cta.setcClabeInterbancaria(rs.getString("cClabeInterbancaria"));
                cta.setnBCBEnviadoSICOP(rs.getInt("nBCBEnviadoSICOP"));
                cta.setnEstatusCta(rs.getInt("nEstatusCta"));
                cta.setcUsuarioModifico(rs.getString("cUsuarioModifico"));
                cta.setcMotivoEliminaCta(rs.getString("cMotivoEliminaCta"));
                cta.setFolio(rs.getString("folio"));
                cta.setRfc(rs.getString("rfc"));
            }
            return cta;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public List<DatosCtaBancaria> listCuentasBancariasTemp(Connection conn, String folio) throws SQLException {
        StringBuilder querySelect = new StringBuilder();
        querySelect.append(" SELECT cbanco               AS cBanco, ");
        querySelect.append("        dbanco               AS cNameBanco, ");
        querySelect.append("        cplaza               AS cPlaza, ");
        querySelect.append("        ddigitoverificador   AS dDigitoVerificador, ");
        querySelect.append("        dsucursal            AS dSucursal, ");
        querySelect.append("        dcuentabancaria      AS dCuentaBancaria, ");
        querySelect.append("        cbanco + cplaza + dcuentabancaria ");
        querySelect.append("        + ddigitoverificador AS cClabeInterbancaria, ");
        querySelect.append("        nbcbenviadosicop     AS nBCBEnviadoSICOP, ");
        querySelect.append("        0                    AS nEstatusCta, ");
        querySelect.append("        cusuariomodifico     AS cUsuarioModifico, ");
        querySelect.append("        ''                   AS cMotivoEliminaCta ");
        querySelect.append(" FROM   tbeneficiariocuentasbancariastmp WITH(nolock) ");
        querySelect.append(" WHERE cFolio = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<DatosCtaBancaria> cuentasTemp = new ArrayList<>();
        try {
            ps = conn.prepareStatement(querySelect.toString());
            ps.setString(1, folio);
            rs = ps.executeQuery();
            while (rs.next()) {
                DatosCtaBancaria cta = new DatosCtaBancaria();
                cta.setcBanco(rs.getString("cBanco"));
                cta.setcNameBanco(rs.getString("cNameBanco"));
                cta.setcPlaza(rs.getString("cPlaza"));
                cta.setnDigitoVerificador(rs.getInt("dDigitoVerificador"));
                cta.setcSucursal(rs.getString("dSucursal"));
                cta.setcCuentaBancaria(rs.getString("dCuentaBancaria"));
                cta.setcClabeInterbancaria(rs.getString("cClabeInterbancaria"));
                cta.setnBCBEnviadoSICOP(rs.getInt("nBCBEnviadoSICOP"));
                cta.setnEstatusCta(rs.getInt("nEstatusCta"));
                cta.setcUsuarioModifico(rs.getString("cUsuarioModifico"));
                cta.setcMotivoEliminaCta(rs.getString("cMotivoEliminaCta"));
                cuentasTemp.add(cta);
            }
            return cuentasTemp;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public String proveedoresAdjudicados(Connection conn, String idPedidoContratoDefinitivo) throws ProveedorException {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT mpa.cidrfc ");
        sb.append(" FROM   pcontratodiverso mpa WITH(nolock) ");
        sb.append("	WHERE  mpa.cidcontrato = ?  ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        String rfcProveedor = null;
        try {
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, idPedidoContratoDefinitivo);
            rs = ps.executeQuery();
            while (rs.next()) rfcProveedor = rs.getString(1);
            return rfcProveedor;
        } catch (Exception e) {
            throw new ProveedorException(e.toString(), e);
        }
    }

    public void registraProveedorRescision(Connection conn, String rfc) throws ProveedorException {
        StringBuilder sbProveedor = new StringBuilder("UPDATE mCatalogoProveedor SET rescisionContrato = 'S' WHERE REPLACE(cIdRFC, '-','') =  REPLACE(?, '-','')");
        StringBuilder sbBeneficiario = new StringBuilder("UPDATE tBeneficiario SET rescisionContrato = 'S' WHERE REPLACE(dRFC, '-','') =  REPLACE(?, '-','')");
        PreparedStatement psProveedor = null;
        PreparedStatement psBeneficiario = null;
        try {
            psBeneficiario = conn.prepareStatement(sbBeneficiario.toString());
            psBeneficiario.setString(1, rfc);
            psProveedor = conn.prepareStatement(sbProveedor.toString());
            psProveedor.setString(1, rfc);
            psProveedor.executeUpdate();
            psBeneficiario.executeUpdate();
        } catch (SQLException e) {
            throw new ProveedorException(e.toString(), e);
        } finally {
            CloseObject.closeObject(psProveedor);
            CloseObject.closeObject(psBeneficiario);
        }
    }

    public boolean setNewCBEN(Connection conn, String cIdRFC) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "UPDATE tAltaProveedor SET cNvoCBEN = 1 WHERE REPLACE( cIdRFC, '-', '' ) = REPLACE( '" + cIdRFC + "', '-', '')";
        try {
            log.info(query);
            ps = conn.prepareStatement(query);
            success = ps.executeUpdate() > 0;
            return success;
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    public boolean updateCatalogoProveedorIncumplido(Connection conn, String cIdRFC) throws Exception {
        PreparedStatement ps = null;
        boolean success = false;
        String query = "";
        try {
            query = "UPDATE mCatalogoProveedor SET lProveedorIncumplido=1 where replace(cIdRFC,'-','')=replace(?,'-','')";
            log.info(query);
            ps = conn.prepareStatement(query);
            ps.setString(1, cIdRFC);
            success = ps.executeUpdate() > 0;
        } finally {
            CloseObject.closeObject(ps, false);
            query = null;
        }
        return success;
    }

    public boolean addProveedorIncumplido(Connection conn, DatosProveedorIncumplido datProveedorIncump) throws Exception, ProveedorException {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = new StringBuilder();
        try {
            query.append("insert into mCatalogoProveedorIncumplido ");
            query.append("(cEjercicioFiscal,cIdRFC,cRazonSocial,cOficioSancion ");
            query.append(",fFechaOficioSancion,fFechaTerminoFirma,cNumeroContratoCNET ");
            query.append(",cNumeroProcedimiento,cCodigoExpedienteCNET,cCodigoContratoCNET");
            query.append(",cDescripcion,cLogin,fFechaCaptura) values(?,?,?,?,convert(date,?),convert(date,?),?,?,?,?,?,?,getdate())");
            log.info(query);
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, datProveedorIncump.getcEjercicioFiscal());
            ps.setString(2, datProveedorIncump.getcIdRFC());
            ps.setString(3, datProveedorIncump.getcRazonSocial());
            ps.setString(4, datProveedorIncump.getcOficioSancion());
            ps.setString(5, datProveedorIncump.getfFechaOficioSancion());
            ps.setString(6, datProveedorIncump.getfFechaTerminoFirma());
            if (StringUtils.isBlank(datProveedorIncump.getcNumeroContratoCNET()))
                ps.setNull(7, Types.VARCHAR);
            else
                ps.setString(7, datProveedorIncump.getcNumeroContratoCNET());
            if (StringUtils.isBlank(datProveedorIncump.getcNumeroProcedimiento()))
                ps.setNull(8, Types.VARCHAR);
            else
                ps.setString(8, datProveedorIncump.getcNumeroProcedimiento());
            if (StringUtils.isBlank(datProveedorIncump.getcCodigoExpedienteCNET()))
                ps.setNull(9, Types.VARCHAR);
            else
                ps.setString(9, datProveedorIncump.getcCodigoExpedienteCNET());
            if (StringUtils.isBlank(datProveedorIncump.getcCodigoContratoCNET()))
                ps.setNull(10, Types.VARCHAR);
            else
                ps.setString(10, datProveedorIncump.getcCodigoContratoCNET());
            if (StringUtils.isBlank(datProveedorIncump.getcDescripcion()))
                ps.setNull(11, Types.VARCHAR);
            else
                ps.setString(11, datProveedorIncump.getcDescripcion());
            ps.setString(12, datProveedorIncump.getcLogin());
            success = ps.executeUpdate() > 0;
            return success;
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }
}
