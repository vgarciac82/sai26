/**
 */
package com.syc.gestion.implementacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.syc.altaproveedor.AltaProveedorBusinessLogic;
import com.syc.altaproveedor.AltaProveedorManager;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.Documento;
import com.syc.gestion.TipoCasoInterface;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

/**
 * @author Propietario
 */
public class AltaProveedorImpl implements TipoCasoInterface {

    private static final Logger log = LoggerFactory.getLogger(AltaProveedorImpl.class);

    /**
     */
    public AltaProveedorImpl() {
        // TODO Auto-generated constructor stub
    }

    public boolean buscaPorExpediente(Caso c, String u_login) {
        // TODO Auto-generated method stub
        return false;
    }

    public void onIniciaCaso(Connection conn, String u_login, Caso c) throws SQLException {
        // TODO Auto-generated method stub
    }

    public void onCreateExpediente(Connection conn, String u_login, Caso c, Aplicacion app) throws SQLException {
        // TODO Auto-generated method stub
    }

    public void onRecibeDocumento(Connection conn, Caso c, Documento d, boolean isSaveEvent) throws SQLException {
        // TODO Auto-generated method stub
    }

    public void onEjecutaCaso(Connection conn, String u_login, Caso c, int id_caso_oper) throws SQLException {
        // TODO Auto-generated method stub
    }

    public void onAvanzaCaso(Connection conn, String u_login, Caso c, int id_caso_oper) throws SQLException {
        int idOper = c.getCasoOperacion(0).getIdOperacion();
        try {
            ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
            if ("S".equalsIgnoreCase(cabl.getSystemSetting("ENVIA_ALERTA_PROVEEDOR"))) {
                String folio = c.getFolio();
                String dest, body;
                String estatus = extraeStatus(conn, folio);
                String subject;
                /* Envia correo al responsable de GRM de validar */
                if (idOper == 2) {
                    String razonSocial = extraeRazonSocial(conn, folio);
                    body = "<B>Atencion</b></br>" + "Se le notifica que se requiere que valide la informacion y la documenacion del alta de proveedor " + razonSocial + " con folio [" + c.getFolio() + " ]<br>" + "Gracias<br> ";
                    if ("A".equals(estatus)) {
                        // Cuando es Actualizacion de
                        // cuentas bancarias.
                        dest = cabl.getSystemSetting("CORREO_VALIDA_BENEFICIARIO");
                        subject = "Cuenta Bancaria pendiente de Autorizar";
                        body = "<B>Atencion</b></br>" + "Se le notifica que se requiere que Autorice la cuenta bancaria de " + razonSocial + " con folio [" + c.getFolio() + " ]<br>" + "Gracias<br> ";
                    } else if ("BENEFICIARIO".equals(extraeTipo(conn, folio)) || "R".equals(estatus)) {
                        dest = cabl.getSystemSetting("CORREO_VALIDA_BENEFICIARIO");
                        if ("R".equals(estatus)) {
                            subject = "Registro pendiente de Autorizar";
                            body = "<B>Atencion</b></br>" + "Se le notifica que se requiere que Autorice del alta de proveedor " + razonSocial + " con folio [" + c.getFolio() + " ]<br>" + "Gracias<br> ";
                        } else
                            subject = "Registro de Beneficiario pendiente de validar";
                    } else {
                        dest = cabl.getSystemSetting("CORREO_VALIDA_PROVEEDOR");
                        subject = "Registro de Proveedor pendiente de validar";
                    }
                    AlarmaManager.procesaAlarmaCNF(conn, "", c.getCasoOperacion(0), c, subject, dest, body);
                } else if (idOper == 3) {
                    // envia respuesta despues de validar
                    // y
                    // revision a cuentas bancarias GRF
                    // respuesta a Usuario de Captura
                    // String dest = cabl.getMailBitacora(idOper,folio);
                    // Aviso revision cuentas bancarias
                    String subject2 = "Registro de proveedor pendiente de Autorizar";
                    String razonSocial = extraeRazonSocial(conn, folio);
                    dest = extraeUsuarioEmail(conn, folio);
                    if ("V".equals(estatus)) {
                        body = "<B>Atencion</b></br>" + "Se notifica que fue incorporada la información solicitada en trámite con folio [" + folio + " ] a nombre de " + razonSocial + " al catálogo de Proveedores de la CONAFOR.<br><br>" + "Gracias<br> ";
                        subject = "Registro de proveedor Validado";
                        String body2 = "<B>Atencion</b></br>" + "Se le notifica que se requiere que autorice la informacion y la cuenta bancaria del alta de proveedor " + razonSocial + " con folio [" + folio + " ]<br>" + "Gracias<br> ";
                        String dest2 = cabl.getSystemSetting("CORREO_AUTORIZA_PROVEEDOR");
                        AlarmaManager.procesaAlarmaCNF(conn, "", c.getCasoOperacion(0), c, subject2, dest2, body2);
                    } else {
                        body = "<B>Atencion</b></br>" + "Se notifica que la solicitud  [" + folio + " ] a nombre de " + razonSocial + " fue rechazada, favor de revisar y continuar con el trámite.<br><br>" + "Gracias<br> ";
                        subject = "Registro de Proveedor Rechazado";
                    }
                    AlarmaManager.procesaAlarmaCNF(conn, "", c.getCasoOperacion(0), c, subject, dest, body);
                } else if (idOper == 4) {
                    // envia respuesta a Usuario captura
                    // respuesta a Usuario de Captura
                    String razonSocial = extraeRazonSocial(conn, folio);
                    dest = extraeUsuarioEmail(conn, folio);
                    if ("S".equals(estatus)) {
                        if (tipoCorreo(conn, folio) == 2) {
                            // Falta algun
                            // registro en
                            // SICOP
                            body = "<B>Atencion</b><br/>" + "Se notifica que fue incorporada la información solicitada en trámite con folio [" + folio + " ] a nombre de " + razonSocial + " al catálogo de beneficiarios y cuentas bancarias de la CONAFOR.<br/><br/>" + "Por lo anterior es necesario que verifique que la información en el  sistema coincide antes de efectuar un trámite de pago a dicho beneficiario/proveedor, " + "en caso de que existiera alguna inconsistencia o cambio favor de realizar trámite de proceso de actualización de proveedor/beneficiario. <br/><br/>" + "Le recuerdo que es responsabilidad del área que solicita el pago la información contenida en la “solicitud de pago” emitida en el SAI, " + "incluyendo nombre y cuenta bancaria del beneficiario del pago.<br/><br/>" + "Gracias<br/> ";
                            subject = "Registro de Proveedor y cuentas bancarias Autorizado";
                            AlarmaManager.procesaAlarmaCNF(conn, "", c.getCasoOperacion(0), c, subject, dest, body);
                        }
                    } else {
                        body = "<B>Atencion</b></br>" + "Se notifica que la solicitud  [" + folio + " ] a nombre de " + razonSocial + " fue rechazada, favor de revisar y continuar con el trámite.<br><br>" + "Gracias<br> ";
                        subject = "Registro de cuentas bancarias Rechazado";
                        AlarmaManager.procesaAlarmaCNF(conn, "", c.getCasoOperacion(0), c, subject, dest, body);
                    }
                    // AlarmaManager.procesaAlarmaCNF(conn, "",
                    // c.getCasoOperacion(0), c, subject, dest, body);
                }
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    public void onTerminaCaso(Connection conn, String u_login, Caso c, String observ, String[] resp, String[] oper, Map data) throws SQLException {
        AltaProveedorBusinessLogic apbl = new AltaProveedorBusinessLogic(GestionInterface.ATT_CONEXION);
        try {
            String rfc = AltaProveedorManager.getRfcProveedor(conn, c.getFolio());
            apbl.deleteAltaProveedor(conn, rfc, u_login, c.getFolio());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String extraeUsuarioEmail(Connection conn, String folio) throws Exception {
        String query = "SELECT U_EMAIL FROM CG_USUARIO with(nolock)  where U_LOGIN in  (SELECT cIdUsuarioCaptura FROM tAltaProveedor with(nolock) WHERE cFolio=?)";
        ResultSet rs = null;
        PreparedStatement ps = null;
        String val = null;
        try {
            if ("true".equalsIgnoreCase(ConfiguraAplicativoManager.getSystemSetting(conn, "AMBIENTE_DESARROLLO")))
                return ConfiguraAplicativoManager.getSystemSetting(conn, "CORREO_ALERTAS_DESARROLLO");
            ps = conn.prepareStatement(query);
            ps.setString(1, folio);
            log.debug("Object: " + String.valueOf(ps.toString()));
            log.debug("Object: " + String.valueOf(folio));
            rs = ps.executeQuery();
            if (rs.next())
                val = rs.getString(1);
            return val;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static String extraeRazonSocial(Connection conn, String folio) throws Exception {
        String query = "" + "SELECT CASE " + "         WHEN crazonsocial = '' THEN capellidopaterno + ' ' + capellidomaterno + " + "                                     ' ' " + "                                     + cnombre " + "         ELSE crazonsocial " + "       END " + "FROM   taltaproveedor with(nolock) " + "WHERE  cfolio = ?";
        ResultSet rs = null;
        PreparedStatement ps = null;
        String val = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, folio);
            log.debug("Object: " + String.valueOf(ps.toString()));
            log.debug("Object: " + String.valueOf(folio));
            rs = ps.executeQuery();
            if (rs.next())
                val = rs.getString(1);
            return val;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static String extraeStatus(Connection conn, String folio) throws Exception {
        String query = "" + "SELECT cdocumentohaplicado " + "FROM   taltaproveedor with(nolock) " + "WHERE  cfolio = ?";
        ResultSet rs = null;
        PreparedStatement ps = null;
        String val = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, folio);
            log.debug("Object: " + String.valueOf(ps.toString()));
            log.debug("Object: " + String.valueOf(folio));
            rs = ps.executeQuery();
            if (rs.next())
                val = rs.getString(1);
            return val;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static int tipoCorreo(Connection conn, String folio) throws Exception {
        String query = "" + "SELECT CBEN  " + "FROM vListaBeneficiario with(nolock) " + "WHERE (nEnviadoSICOP=0 OR nBCBEnviadoSICOP=0 ) AND drfc = (SELECT cIdRFC FROM tAltaProveedor with(nolock) WHERE cFolio= ? )";
        ResultSet rs = null;
        PreparedStatement ps = null;
        int val = 0;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, folio);
            log.debug("Object: " + String.valueOf(ps.toString()));
            log.debug("Object: " + String.valueOf(folio));
            rs = ps.executeQuery();
            if (rs.next())
                val = 1;
            else
                val = 2;
            return val;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static String extraeTipo(Connection conn, String folio) throws Exception {
        String query = "" + "SELECT cTipoRegistro " + "FROM   taltaproveedor with(nolock) " + "WHERE  cfolio = ?";
        ResultSet rs = null;
        PreparedStatement ps = null;
        String val = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, folio);
            log.debug("Object: " + String.valueOf(ps.toString()));
            log.debug("Object: " + String.valueOf(folio));
            rs = ps.executeQuery();
            if (rs.next())
                val = rs.getString(1);
            return val;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public void onVenceCaso(Connection conn, String folio, int id_caso, int id_tc, int id_oper, int porc) {
        /*
		 * debido al cambio de parametro en el WEB.XML se elimina la validacion
		 * de que cada 3 dias envie la alerta dejando que el servlet se
		 * encargue. -- ultima version donde aparece esa validacion:34958
		 */
        try {
            ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
            String correo = null;
            switch(id_oper) {
                // captura_proveedor
                case 1:
                case // captura_cuentabancaria_proveedor
                2:
                    correo = extraeUsuarioEmail(conn, folio);
                    break;
                case // valida_proveedor
                3:
                    if ("true".equalsIgnoreCase(ConfiguraAplicativoManager.getSystemSetting(conn, "AMBIENTE_DESARROLLO"))) {
                        correo = ConfiguraAplicativoManager.getSystemSetting(conn, "CORREO_ALERTAS_DESARROLLO");
                    } else if ("BENEFICIARIO".equals(extraeTipo(conn, folio))) {
                        correo = cabl.getSystemSetting("CORREO_VALIDA_BENEFICIARIO");
                    } else {
                        correo = cabl.getSystemSetting("CORREO_VALIDA_PROVEEDOR");
                    }
                    break;
                case // autoriza_proveedor
                4:
                    if ("true".equalsIgnoreCase(ConfiguraAplicativoManager.getSystemSetting(conn, "AMBIENTE_DESARROLLO"))) {
                        correo = ConfiguraAplicativoManager.getSystemSetting(conn, "CORREO_ALERTAS_DESARROLLO");
                    } else
                        correo = cabl.getSystemSetting("CORREO_AUTORIZA_PROVEEDOR");
                    break;
                case // modifica_proveedor
                6:
                    if ("true".equalsIgnoreCase(ConfiguraAplicativoManager.getSystemSetting(conn, "AMBIENTE_DESARROLLO"))) {
                        correo = ConfiguraAplicativoManager.getSystemSetting(conn, "CORREO_ALERTAS_DESARROLLO");
                    } else
                        correo = cabl.getSystemSetting("CORREO_MODIFICA_PROVEEDOR");
                    break;
                default:
                    log.warn("Object: {}", "Enviaria correo del folio: " + folio + ", pero el operador no lo permite: " + id_oper);
                    break;
            }
            if (StringUtils.isEmpty(correo)) {
                correo = extraeEmail(conn, id_caso, id_tc);
                if (!StringUtils.isEmpty(correo)) {
                    enviaCorreo(conn, correo, folio);
                } else {
                    if (cancelaCaso(conn, id_caso, id_tc, id_oper))
                        AltaProveedorManager.eliminaTramite(conn, folio, "SYSTEM");
                }
            } else {
                enviaCorreo(conn, correo, folio);
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    private void enviaCorreo(Connection conn, String correo, String folio) throws Exception {
        String subject, body;
        subject = "Registro de Alta Proveedor inconcluso";
        body = "<B>Atención</b></br>" + "Se notifica que el trámite de  Alta Proveedor con folio: " + folio + " está por vencerse.<br><br>" + "Por lo anterior es necesario que verifique la información para concluir o en su defecto descartar el trámite del sistema, " + "ya que si no se atiende se descartara automáticamente perdiendo toda la información contenida en dicho trámite. EL tiempo límite de atención es de una semana a partir del 1ero de Junio del 2017 <br><br>";
        log.info("Object: {}", "Enviando Correo al siguiente destinatario: " + correo);
        AlarmaManager.procesaAlarmaCNF(conn, "", null, null, subject, correo, body);
    }

    public String extraeEmail(Connection conn, int id_caso, int id_tc) {
        String email = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "" + "SELECT TOP(1) US.U_EMAIL " + "FROM   CG_USUARIO US  WITH (nolock) " + "       INNER JOIN CG_BITACORA BI  WITH (nolock) " + "               ON US.U_LOGIN = BI.B_CO_RESPONSABLE_EJEC " + "WHERE  BI.B_ID_CASO = ? " + "       AND BI.B_ID_TC = ? " + "ORDER  BY BI.ID_BITACORA";
        try {
            if ("true".equalsIgnoreCase(ConfiguraAplicativoManager.getSystemSetting(conn, "AMBIENTE_DESARROLLO")))
                return ConfiguraAplicativoManager.getSystemSetting(conn, "CORREO_ALERTAS_DESARROLLO");
            ps = conn.prepareStatement(query);
            ps.setInt(1, id_caso);
            ps.setInt(2, id_tc);
            log.info("Object: {}", ps.toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                email = rs.getString("U_EMAIL");
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
        return email;
    }

    public boolean cancelaCaso(Connection conn, int id_caso, int id_tc, int id_operAnterior) {
        String query = "SELECT ID_OPER,O_RESPONSABLE FROM CG_OPERACION WITH (NOLOCK) WHERE O_RESPONSABLE LIKE 'Consulta%' AND ID_TC=  ? ";
        boolean success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int id_oper = 0;
        String o_responsable = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, id_tc);
            rs = ps.executeQuery();
            log.info("Object: {}", ps.toString());
            if (rs.next()) {
                id_oper = rs.getInt("ID_OPER");
                o_responsable = rs.getString("O_RESPONSABLE");
            }
            query = "UPDATE CG_CASO_OPERACION SET ID_OPER=?,CO_RESPONSABLE=? WHERE ID_CASO=? AND ID_TC=?";
            ps = conn.prepareStatement(query);
            ps.setInt(1, id_oper);
            ps.setString(2, o_responsable);
            ps.setInt(3, id_caso);
            ps.setInt(4, id_tc);
            log.info("Object: {}", ps.toString());
            success = ps.executeUpdate() > 0;
            if (success) {
                query = "INSERT INTO tCasosVencidosCancelados VALUES(?,?,?,getdate())";
                ps = conn.prepareStatement(query);
                ps.setInt(1, id_tc);
                ps.setInt(2, id_caso);
                ps.setInt(3, id_operAnterior);
                log.info("Object: {}", ps.toString());
                success = ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
        log.info("Object: {}", "Se Mueve a Consulta el caso " + id_caso + " : " + success);
        return success;
    }

    public void onSolicitaFirmaElectronica(Caso c, Usuario u, String reportPath) throws SQLException {
        return;
    }
}
