package com.syc.altaproveedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.axtel.proveedores.exception.ProveedorException;
import com.syc.adquisiciones.core.Respuesta;
import com.syc.adquisiciones.util.Util;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AltaProveedorBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(AltaProveedorBusinessLogic.class);

    private String jniName = null;

    private final AltaProveedorManager manager = new AltaProveedorManager();

    public static final String PROVEEDOR = "PROVEEDOR";

    public static final String EMPLEADO = "EMPLEADO";

    public static final int CBEN_PROVEEDOR = 3;

    public static final int TIPO_PERSONA_MORAL = 1;

    public static final int TIPO_PERSONA_EMPLEADO = 3;

    public AltaProveedorBusinessLogic() {
        super();
    }

    public AltaProveedorBusinessLogic(String jniName) {
        super.init(jniName);
        this.jniName = jniName;
    }

    public Respuesta consultaDocumentacion(DatosProveedor datProveedor) throws Exception {
        Connection conn = null;
        Respuesta resp = null;
        // todo
        String archivos = "'Cedula de Registro(RFC)','Identificacion','Comprobante de Domicilio','Acta de Nacimiento','CURP','Formato del sector MIPYME''Acta Constitutiva-Poder Notarial','Formato del sector MIPYME'";
        try {
            conn = getConnection();
            if (datProveedor.getnTipoPersona() == AltaProveedorBusinessLogic.TIPO_PERSONA_MORAL) {
                // personas
                archivos = "'Cedula de Registro(RFC)','Identificacion','Comprobante de Domicilio','Acta Constitutiva-Poder Notarial','Formato del sector MIPYME'";
                // morales
                // Proveedores
                if (!AltaProveedorBusinessLogic.PROVEEDOR.equalsIgnoreCase(datProveedor.getcTipoPB())) {
                    archivos = "'Cedula de Registro(RFC)','Identificacion','Comprobante de Domicilio','Acta Constitutiva-Poder Notarial'";
                    /* personas morales Beneficiarios */
                }
            } else if (datProveedor.getnTipoPersona() == 2) {
                // persona fisica
                // persona
                archivos = "'Cedula de Registro(RFC)','Identificacion','Comprobante de Domicilio','Acta de Nacimiento','CURP','Formato del sector MIPYME'";
                // fisica
                if (!AltaProveedorBusinessLogic.PROVEEDOR.equalsIgnoreCase(datProveedor.getcTipoPB())) {
                    archivos = "'Cedula de Registro(RFC)','Identificacion','Comprobante de Domicilio','Acta de Nacimiento','CURP'";
                    /*
					 * persona fisica Beneficiarios
					 */
                }
            } else if (datProveedor.getnTipoPersona() == AltaProveedorBusinessLogic.TIPO_PERSONA_EMPLEADO) {
                // empleado
                archivos = "'Cedula de Registro(RFC)','Identificacion','Comprobante de Domicilio','Acta de Nacimiento','CURP'";
            }
            resp = Util.HayDocumentos(conn, datProveedor.getcTituloAplicacion(), datProveedor.getcFolio(), archivos);
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            log.error("Object: {}", e.getMessage());
            throw new Exception(e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return resp;
    }

    public Respuesta consultaDocumentacionCtaBancaria(DatosProveedor datProveedor) throws Exception {
        Connection conn = null;
        Respuesta resp = null;
        try {
            conn = getConnection();
            resp = manager.hayDocCtasBancarias(conn, datProveedor.getcFolio());
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            log.error("Object: {}", e.getMessage());
            throw new Exception(e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return resp;
    }

    public Respuesta validaCuentasBancarias(DatosProveedor datProveedor, Usuario u) throws Exception {
        Connection conn = null;
        Respuesta resp = null;
        String cDescripcionMovimiento = "";
        String cDocumentoHAplicadoAnterior = "";
        int nIdOperAnterior = 0;
        try {
            conn = getConnection();
            cDocumentoHAplicadoAnterior = manager.obtieneCDocumentoHAplicado(conn, datProveedor.getcFolio());
            nIdOperAnterior = manager.idOperAnt(conn, datProveedor.getcFolio());
            manager.updatetAltaProveedorIdOperAnt(conn, datProveedor.getcFolio(), datProveedor.getnIdOper());
            // Aplica documento
            boolean newAcounts = manager.hayNuevasCtas(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
            boolean esActCta = manager.esActCtaBancaria(conn, datProveedor.getcFolio());
            // valida si hay cuentas registradas
            if (newAcounts) {
                // valida documentación
                resp = manager.hayDocCtasBancarias(conn, datProveedor.getcFolio());
                cDescripcionMovimiento = "Se env\u00edo el proveedor a validaci\u00f3n, con cuenta nueva";
                if (esActCta) {
                    cDescripcionMovimiento = "Se env\u00edo el proveedor a autorizaci\u00f3n, con cuenta nueva";
                }
                if (resp.isResp() && !AltaProveedorBusinessLogic.PROVEEDOR.equalsIgnoreCase(datProveedor.getcTipoPB())) {
                    /* cuando no es un proveedor */
                    manager.updatetAltaProveedor(conn, datProveedor.getcFolio(), "", datProveedor.getcDocumentoHaplicado(), datProveedor.getcObservaciones());
                    resp = movimientosParaEmpBenef(datProveedor, conn, manager);
                    cDescripcionMovimiento = "Se env\u00edo el beneficiario a autorizar, con cuenta nueva";
                    if (datProveedor.getnTipoPersona() == AltaProveedorBusinessLogic.TIPO_PERSONA_EMPLEADO) {
                        cDescripcionMovimiento = "Se env\u00edo el empleado a autorizar, con cuenta nueva";
                    }
                }
                manager.saveBitacoraAltaProveedor(conn, datProveedor.getcFolio(), cDescripcionMovimiento, datProveedor.getcDocumentoHaplicado(), cDocumentoHAplicadoAnterior, datProveedor.getnIdOper(), nIdOperAnterior, u.getLogin());
            } else if (esActCta && !newAcounts) {
                resp = new Respuesta();
                if (manager.updateAltaProveedorAplicado(conn, datProveedor.getcFolio())) {
                    manager.enabledCtasBancarias(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
                    manager.updateEsActCta(conn, datProveedor.getcFolio(), 0);
                    manager.saveBitacoraAltaProveedor(conn, datProveedor.getcFolio(), "No hubo ningun cambio en las cuentas bancarias.\n El tramite regresa a consulta.", datProveedor.getcDocumentoHaplicado(), cDocumentoHAplicadoAnterior, datProveedor.getnIdOper(), nIdOperAnterior, u.getLogin());
                    resp.setMsg("No hubo ningun cambio en las cuentas bancarias.\n El tramite regresa a consulta.");
                    resp.setResp(true);
                } else {
                    resp.setMsg("No hubo ningun cambio en las cuentas bancarias.\n No se pudo actualizar el estatus del tramite para regresar a consulta.");
                    resp.setResp(false);
                }
            } else if (manager.hayCtasBancarias(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3()) && !newAcounts && !esActCta) {
                resp = manager.hayDocCtasBancarias(conn, datProveedor.getcFolio());
                cDescripcionMovimiento = "Se env\u00edo el proveedor a validación, con cuenta existente";
                if (resp.isResp() && !AltaProveedorBusinessLogic.PROVEEDOR.equalsIgnoreCase(datProveedor.getcTipoPB())) {
                    /* cuando no es un proveedor */
                    manager.updatetAltaProveedor(conn, datProveedor.getcFolio(), "", datProveedor.getcDocumentoHaplicado(), datProveedor.getcObservaciones());
                    resp = movimientosParaEmpBenef(datProveedor, conn, manager);
                    cDescripcionMovimiento = "Se env\u00edo el beneficiario a autorizar, con cuenta existente";
                    if (datProveedor.getnTipoPersona() == AltaProveedorBusinessLogic.TIPO_PERSONA_EMPLEADO) {
                        cDescripcionMovimiento = "Se env\u00edo el empleado a autorizar, con cuenta existente";
                    }
                }
                manager.saveBitacoraAltaProveedor(conn, datProveedor.getcFolio(), cDescripcionMovimiento, datProveedor.getcDocumentoHaplicado(), cDocumentoHAplicadoAnterior, datProveedor.getnIdOper(), nIdOperAnterior, u.getLogin());
            } else if (!manager.hayCtasBancarias(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3()) && !newAcounts) {
                resp = new Respuesta();
                resp.setMsg("Debe registrar al menos una cuenta bancaria");
                resp.setResp(false);
            } else {
                resp = new Respuesta();
                String msg = "\nSe env\u00edo a validaci\u00f3n.";
                if (!AltaProveedorBusinessLogic.PROVEEDOR.equalsIgnoreCase(datProveedor.getcTipoPB())) {
                    msg = "\nSe env\u00edo a autorizaci\u00f3n.";
                }
                resp.setMsg("No se registraron nuevas cuentas bancarias." + msg);
                manager.saveBitacoraAltaProveedor(conn, datProveedor.getcFolio(), resp.getMsg(), datProveedor.getcDocumentoHaplicado(), cDocumentoHAplicadoAnterior, datProveedor.getnIdOper(), nIdOperAnterior, u.getLogin());
                resp.setResp(true);
            }
            // Ejecuta la transacción
            if (resp.isResp()) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (Exception e) {
            conn.rollback();
            throw new Exception(e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return resp;
    }

    public boolean thereIsNewBanckAcount(DatosProveedor datProveedor) throws Exception {
        Connection conn = null;
        boolean resp = false;
        Respuesta respuesta = null;
        try {
            conn = getConnection();
            respuesta = manager.hayDocCtasBancarias(conn, datProveedor.getcFolio());
            resp = respuesta.isNewCtas();
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                conn.rollback();
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
            respuesta = null;
        }
        return resp;
    }

    public void guardaInfoProveedorCorta(DatosProveedor datProveedor, Usuario u) throws ProveedorException {
        Connection conn = null;
        try {
            conn = getConnection();
            if (manager.esUnEFO(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3()))
                throw new ProveedorException("Est\u00e9 contribuyente o sociedad est\u00e1 reconocida por la autoridad como Empresa Facturadora de Operaciones Simuladas \"EFOS\".");
            if (manager.existFolioProveedor(conn, datProveedor.getcFolio())) {
                manager.updateDatAltaProveedor(conn, datProveedor);
                manager.saveBitacoraAltaProveedor(conn, datProveedor.getcFolio(), "Actualiza datos", "", "", datProveedor.getnIdOper(), 1, u.getLogin());
            } else {
                manager.saveDatProveedor(conn, datProveedor, u);
                manager.saveBitacoraAltaProveedor(conn, datProveedor.getcFolio(), "Inserta en tAltaProveedor \"Nueva Alta\"", "", "", datProveedor.getnIdOper(), 1, u.getLogin());
            }
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            com.syc.gestion.util.Util.rollback(conn);
            throw new ProveedorException(e.toString(), e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public Respuesta guardaInfoProveedor(DatosProveedor datProveedor, Usuario u) throws Exception, JSONException {
        Connection conn = null;
        Respuesta resp = null;
        String cDocHAplicado = "";
        int nIdOperAnterior = 0;
        try {
            conn = getConnection();
            resp = new Respuesta();
            if (datProveedor.getnIdOper() == 1 && manager.existProveedorEnOtroFolio(conn, datProveedor.getRfc(), datProveedor.getcFolio())) {
                // Validar que el RFC no exista en otro folio
                throw new Exception("El RFC ya se encuentra capturado en otro folio");
            }
            validaDatos(datProveedor);
            // Validar RFC
            if (validaRFC(datProveedor.getnTipoPersona(), datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3())) {
                // Guardar datos
                nIdOperAnterior = manager.idOperAnt(conn, datProveedor.getcFolio());
                manager.updateHabilitaLiberaCaso(conn, datProveedor.getcFolio(), 0);
                // Se inabilitan por si son folios proveedores o beneficiarios
                // ya existentes sin un folio de alta proveedor
                if (datProveedor.getnIdOper() == 1) {
                    manager.disabledCtasBancarias(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
                    manager.disabledtBeneficiario(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
                    manager.disabledProveedor(conn, datProveedor.getRfc());
                    manager.saveBitacoraAltaProveedor(conn, datProveedor.getcFolio(), "Deshabilita el nEnviadoSICOP porque es un rfc que no ten\u00eda un alta", "", "", datProveedor.getnIdOper(), 0, u.getLogin());
                }
                if (manager.existFolioProveedor(conn, datProveedor.getcFolio())) {
                    cDocHAplicado = manager.obtieneCDocumentoHAplicado(conn, datProveedor.getcFolio());
                    if (manager.updateDatAltaProveedor(conn, datProveedor)) {
                        if (datProveedor.getnIdOper() == 1 && manager.esUnEFO(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3())) {
                            resp.setMsg2("Est\u00e9 contribuyente o sociedad est\u00e1 reconocida por la autoridad como Empresa Facturadora de Operaciones Simuladas \"EFOS\".");
                        }
                        resp.setMsg("Datos actualizados");
                        resp.setResp(true);
                        if (datProveedor.getnIdOper() == 2) {
                            // Valida cuenta
                            // bancaria
                            boolean tieneCuentasRegistradas = manager.hayNuevasCtas(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
                            boolean tieneCtasAnt = manager.hayCtasBancarias(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
                            if (tieneCtasAnt) {
                                // Validar documentos ctas bancarias
                                resp = manager.hayDocCtasBancarias(conn, datProveedor.getcFolio());
                            }
                            resp.setNewCtas(tieneCuentasRegistradas);
                            resp.setOldCtas(tieneCtasAnt);
                            if ((!tieneCtasAnt && !tieneCuentasRegistradas) || ("A".equalsIgnoreCase(datProveedor.getcDocumentoHaplicado()) && (!tieneCuentasRegistradas))) {
                                resp.setResp(false);
                                resp.setMsg("Debe agregar una cuenta bancaria.");
                            }
                            if (!"A".equalsIgnoreCase(datProveedor.getcDocumentoHaplicado())) {
                                // desactivar cuentas
                                manager.disabledCtasBancarias(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
                            }
                            manager.saveBitacoraAltaProveedor(conn, datProveedor.getcFolio(), "Actualiza datos y cuentas bancarias ", datProveedor.getcDocumentoHaplicado(), cDocHAplicado, datProveedor.getnIdOper(), nIdOperAnterior, u.getLogin());
                        } else {
                            // id_opr =1
                            manager.saveBitacoraAltaProveedor(conn, datProveedor.getcFolio(), "Actualiza datos", cDocHAplicado, cDocHAplicado, datProveedor.getnIdOper(), nIdOperAnterior, u.getLogin());
                        }
                    } else {
                        resp.setMsg("No se actualizaron los datos.");
                        resp.setResp(false);
                    }
                } else {
                    if (manager.saveDatProveedor(conn, datProveedor, u)) {
                        manager.saveBitacoraAltaProveedor(conn, datProveedor.getcFolio(), "Inserta en tAltaProveedor \"Nueva Alta\"", "", "", datProveedor.getnIdOper(), nIdOperAnterior, u.getLogin());
                        if (datProveedor.getnIdOper() == 1 && manager.esUnEFO(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3())) {
                            resp.setMsg2("Est\u00e9 contribuyente o sociedad est\u00e1 reconocida por la autoridad como Empresa Facturadora de Operaciones Simuladas \"EFOS\".");
                        }
                        resp.setMsg("Datos guardados.");
                        resp.setResp(true);
                    } else {
                        resp.setMsg("No se guardaron los datos.");
                        resp.setResp(false);
                    }
                }
            } else {
                resp.setMsg("La cantidad de caracteres del RFC es incorrecta.");
                resp.setResp(false);
            }
            if (resp.isResp()) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (Exception e) {
            resp.setResp(false);
            resp.setMsg(e.getMessage().toString());
            conn.rollback();
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return resp;
    }

    private void validaDatos(DatosProveedor datProveedor) throws Exception {
        // quitar comas
        datProveedor.setcRazonSocial(datProveedor.getcRazonSocial().replaceAll(",", " "));
        datProveedor.setcGiro(datProveedor.getcGiro().replaceAll(",", " "));
        datProveedor.setcCalle(datProveedor.getcCalle().replaceAll(",", " "));
        datProveedor.setcNumeroExt(datProveedor.getcNumeroExt().replaceAll(",", " "));
        datProveedor.setcNumeroInt(datProveedor.getcNumeroInt().replaceAll(",", " "));
        datProveedor.setcColonia(datProveedor.getcColonia().replaceAll(",", " "));
        datProveedor.setcCodigoPost(datProveedor.getcCodigoPost().replaceAll(",", " "));
        datProveedor.setcEmail(datProveedor.getcEmail().replaceAll(",", " "));
        // quitar espacios al rfc
        datProveedor.setRfc1(datProveedor.getRfc1().replaceAll(" ", ""));
        datProveedor.setRfc2(datProveedor.getRfc2().replaceAll(" ", ""));
        datProveedor.setRfc3(datProveedor.getRfc3().replaceAll(" ", ""));
        datProveedor.setRfc(datProveedor.getRfc().replaceAll(" ", ""));
        String msg = "";
        String token = "";
        if (datProveedor.getnTipoPersona() == AltaProveedorBusinessLogic.TIPO_PERSONA_MORAL) {
            datProveedor.setcCurp("");
            if ((datProveedor.getcRazonSocial().replaceAll(" ", "")).length() == 0) {
                msg = "La Raz\u00f3n social es un dato requerido.";
                token = "\n";
            }
            if ((datProveedor.getcNombre().replaceAll(" ", "")).length() == 0) {
                msg = msg + token + "El Nombre del representante legal es un dato requerido.";
                token = "\n";
            }
            if ((datProveedor.getcApellidoPat().replaceAll(" ", "")).length() == 0) {
                msg = msg + token + "El Apellido Paterno del representante legal es un dato requerido.";
                token = "\n";
            }
            if ((datProveedor.getcApellidoMat().replaceAll(" ", "")).length() == 0) {
                msg = msg + token + "El Apellido Materno del representante legal es un dato requerido.";
                token = "\n";
            }
        } else {
            // Persona fisica y empleado
            datProveedor.setcRazonSocial("");
            if ((datProveedor.getcNombre().replaceAll(" ", "")).length() == 0) {
                msg = "El Nombre es un dato requerido.";
                token = "\n";
            }
            if ((datProveedor.getcApellidoPat().replaceAll(" ", "")).length() == 0) {
                msg = msg + token + "El Apellido Paterno es un dato requerido.";
                token = "\n";
            }
            if ((datProveedor.getcApellidoMat().replaceAll(" ", "")).length() == 0) {
                msg = msg + token + "El Apellido Materno es un dato requerido.";
                token = "\n";
            }
            if ((datProveedor.getcCurp().replaceAll(" ", "")).length() == 0 || (datProveedor.getcCurp().replaceAll(" ", "")).length() < 18) {
                msg = msg + token + "El CURP es un dato requerido.";
                token = "\n";
            }
            if (datProveedor.getnTipoPersona() == AltaProveedorBusinessLogic.TIPO_PERSONA_EMPLEADO && datProveedor.getnNumEmpleado() == 0) {
                msg = msg + token + "El N\u00famero de empleado es un dato requerido.";
                token = "\n";
            }
        }
        if (datProveedor.getnMunicipio() == 0) {
            msg = msg + token + "Favor de seleccionar un municipio.";
            token = "\n";
        }
        if (datProveedor.getnTipoPersona() != AltaProveedorBusinessLogic.TIPO_PERSONA_EMPLEADO) {
            if ((datProveedor.getcCalle().replaceAll(" ", "")).length() == 0) {
                msg = msg + token + "La Calle es un dato requerido";
                token = "\n";
            }
            if ((datProveedor.getcNumeroExt().replaceAll(" ", "")).length() == 0) {
                msg = msg + token + "El N\u00famero externo es un dato requerido.";
                token = "\n";
            }
            if ((datProveedor.getcColonia().replaceAll(" ", "")).length() == 0) {
                msg = msg + token + "La Colonia es un dato requerido.";
                token = "\n";
            }
            if ((datProveedor.getcCodigoPost().replaceAll(" ", "")).length() == 0) {
                msg = msg + token + "El C\u00f3digo Postal es un dato requerido.";
                token = "\n";
            }
            if ((datProveedor.getcGiro().replaceAll(" ", "")).length() == 0) {
                msg = msg + token + "El Giro es un dato requerido.";
                token = "\n";
            }
            if ((datProveedor.getcTelefono().replaceAll(" ", "")).length() == 0) {
                msg = msg + token + "El Tel\u00e9fono es un dato requerido.";
                token = "\n";
            }
            if ((datProveedor.getcEmail().replaceAll(" ", "")).length() == 0) {
                msg = msg + token + "El Correo Electr\u00f3nico es un dato requerido.";
                token = "\n";
            }
            if (datProveedor.getIdRegimenFiscal() == 0) {
                msg = msg + token + "El regimen fiscal es un dato requerido.";
                token = "\n";
            } else {
                // validar que sea correcto el email
                if (!Util.validaEmail(datProveedor.getcEmail())) {
                    msg = msg + token + "El Correo Electr\u00f3nico no tiene la estructura correcta.";
                    token = "\n";
                }
            }
        }
        if (datProveedor.getnExtranjero() == 0) {
            datProveedor.setcPais("México");
        } else {
            datProveedor.setcPais("");
        }
        if (!"".equalsIgnoreCase(msg)) {
            throw new Exception(msg);
        }
    }

    public JSONObject consultaInfoProveedor(DatosProveedor datProveedor, Usuario u) throws Exception, JSONException {
        JSONArray arrayObj = null;
        JSONObject jsonObj = new JSONObject();
        Connection conn = null;
        String query = "";
        try {
            conn = getConnection();
            // Catalogo pyme
            query = "SELECT cPyme ,nIdPyme FROM dbo.mCatalogoPyme WITH(NOLOCK) ";
            arrayObj = manager.obtieneDatQuery(conn, query);
            jsonObj.put("catPyme", arrayObj);
            arrayObj = null;
            // Catalogo Entidad Federativa
            query = "SELECT edo_nombre, id_estado as cIdEntidadFederativa FROM CAT_ESTADOS WITH(NOLOCK) ";
            arrayObj = manager.obtieneDatQuery(conn, query);
            jsonObj.put("catEntidadFederativa", arrayObj);
            arrayObj = null;
            // Catalogo Nunicipios
            if (manager.existFolioProveedor(conn, datProveedor.getcFolio())) {
                query = "SELECT cat.mpo_nombre, cat.id_municipio as cIdMunicipio  FROM CAT_MUNICIPIO cat WITH(NOLOCK) " + " inner join tAltaProveedor as alta with(nolock) on cat.ID_ESTADO=alta.cIdEntidadFederativa " + " WHERE alta.cFolio='" + datProveedor.getcFolio() + "'";
            } else {
                query = "SELECT mpo_nombre, id_municipio as cIdMunicipio  FROM CAT_MUNICIPIO WITH(NOLOCK) WHERE ID_ESTADO = 1 ";
            }
            arrayObj = manager.obtieneDatQuery(conn, query);
            jsonObj.put("catMunicipio", arrayObj);
            arrayObj = null;
            // Catalogo Localidades
            if (manager.existFolioProveedor(conn, datProveedor.getcFolio())) {
                query = "SELECT upper(cat.LCD_NOMBRE)LCD_NOMBRE, cat.ID_LOCALIDAD as cIdLocalidad  FROM CAT_LOCALIDAD cat WITH(NOLOCK) inner join tAltaProveedor as alta with(nolock) on cat.ID_ESTADO=alta.cIdEntidadFederativa and cat.ID_MUNICIPIO=alta.cIdMunicipio WHERE alta.cFolio='" + datProveedor.getcFolio() + "'";
            } else {
                query = "SELECT upper(LCD_NOMBRE)LCD_NOMBRE, ID_LOCALIDAD as cIdMunicipio  FROM CAT_LOCALIDAD WITH(NOLOCK) WHERE ID_ESTADO = 1 and ID_MUNICIPIO=1001 ";
            }
            arrayObj = manager.obtieneDatQuery(conn, query);
            jsonObj.put("catLocalidad", arrayObj);
            arrayObj = null;
            // Catalogo tipos de telefonos
            query = "SELECT cTipoTelefono, nIdTipoTelefono FROM mCatalogoTipoTelefono WITH(NOLOCK) ";
            arrayObj = manager.obtieneDatQuery(conn, query);
            jsonObj.put("catTipoTelefono", arrayObj);
            arrayObj = null;
            // Consulta si es un EFO
            query = "select 'Est\u00e9 contribuyente o sociedad est\u00e1 reconocida por la autoridad como Empresa Facturadora de Operaciones Simuladas \"EFOS\".'msgEFOS from tBitacoraEFOS with(Nolock) where cSituacion in(1,3) and cRFC='" + datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3() + "' ";
            arrayObj = manager.datGuardados(conn, query);
            jsonObj.put("EFOS", arrayObj);
            arrayObj = null;
            // datos Guardados
            query = "SELECT convert(int,cIdTipoPersona) cIdTipoPersonaRFC,cTipoRegistro tipoPB,cIdRFC " + " ,case when cIdTipoPersona=1 then(SUBSTRING(cIdRFC,1,3)) else (SUBSTRING(cIdRFC,1,4)) end cIdRFC1 " + " ,case when cIdTipoPersona=1 then(SUBSTRING(cIdRFC,5,6)) else (SUBSTRING(cIdRFC,6,6)) end cIdRFC2 " + " ,case when cIdTipoPersona=1 then(SUBSTRING(cIdRFC,12,len(cIdRFC))) else (SUBSTRING(cIdRFC,13,len(cIdRFC))) end cIdRFC3 " + " ,cRazonSocial,cCURP,cApellidoPaterno,cApellidoMaterno,cNombre,cGiro,convert(int,cIdEntidadFederativa) cEstadoFiscal" + " ,convert(int,cIdMunicipio) cMunicipioFiscal,cCalle,cNumeroExterno,cNumeroInterno,cColonia,cCodigoPostal " + " ,cEmail,cUrl,nIdPyme nIdPyme,convert(int,nIdTipoTelefono) cTipoTelefono,cTelefono,isnull(cDocumentoHaplicado,'') cDocumentoHaplicado" + " ,cObservaciones,cActualizacion,cExtranjero,cPais, isnull(nIdEmpleado,0) NEmp,nIdOperAnt,isnull(bLiberaCaso,0) bLiberaCaso,isnull(bEsActCta,0) bEsActCta,'" + u.getLogin() + "' cIdUsuarioLogeado" + " ,cNumeroREPSE AS cNumeroREPSE" + " ,id_regimen_fiscal AS idRegimenFiscal,cIdLocalidad cLocalidadFiscal " + " FROM tAltaProveedor WITH(NOLOCK) WHERE cFolio='" + datProveedor.getcFolio() + "' ";
            arrayObj = manager.datGuardados(conn, query);
            if (arrayObj.getJSONObject(0).getBoolean("HAYINFO")) {
                datProveedor.setRfc(arrayObj.getJSONObject(0).getString("cIdRFC"));
                datProveedor.setnTipoPersona(arrayObj.getJSONObject(0).getInt("cIdTipoPersonaRFC"));
            } else {
                datProveedor.setRfc("");
                datProveedor.setnTipoPersona(AltaProveedorBusinessLogic.TIPO_PERSONA_MORAL);
            }
            jsonObj.put("datGuardados", arrayObj);
            StringBuilder querySelect = new StringBuilder();
            querySelect.append(" SELECT 'Seleccione el regimen fiscal' AS regimen_fiscal, ");
            querySelect.append(" 	   0                              AS id_regimen_fiscal ");
            querySelect.append(" UNION ");
            querySelect.append(" SELECT regimen_fiscal, ");
            querySelect.append(" 	   id_regimen_fiscal ");
            querySelect.append(" FROM   tcat_regimen_fiscal ");
            querySelect.append(" WHERE  activo = 1 ");
            if (datProveedor.getnIdOper() == 6 && arrayObj != null && arrayObj.length() > 0 && arrayObj.getJSONObject(0).getBoolean("HAYINFO")) {
                // Catalogo tipo persona
                int tipoPer = arrayObj.getJSONObject(0).getInt("cIdTipoPersonaRFC");
                arrayObj = null;
                query = "SELECT cTipoPersona ,nTipoPersona FROM dbo.tCatTipoPersona WITH(NOLOCK) where nTipoPersona>1";
                if (tipoPer == 1) {
                    query = "SELECT cTipoPersona ,nTipoPersona FROM dbo.tCatTipoPersona WITH(NOLOCK) where nTipoPersona=1";
                    querySelect.append(" 	   AND aplica_persona_moral = 1 ");
                } else {
                    querySelect.append(" 	   AND aplica_persona_fisica = 1 ");
                }
                querySelect.append(" ORDER  BY 2 ");
                arrayObj = manager.obtieneDatQuery(conn, query);
                jsonObj.put("catTipoPersona", arrayObj);
                arrayObj = manager.obtieneDatQuery(conn, querySelect.toString());
                jsonObj.put("idRegimenFiscal", arrayObj);
            } else {
                // Catalogo tipo persona
                arrayObj = null;
                query = "SELECT cTipoPersona ,nTipoPersona FROM dbo.tCatTipoPersona WITH(NOLOCK) ";
                arrayObj = manager.obtieneDatQuery(conn, query);
                jsonObj.put("catTipoPersona", arrayObj);
                if (datProveedor.getnTipoPersona() == AltaProveedorBusinessLogic.TIPO_PERSONA_MORAL)
                    querySelect.append(" 	   AND aplica_persona_moral = 1 ");
                else
                    querySelect.append(" 	   AND aplica_persona_fisica = 1 ");
                querySelect.append(" ORDER  BY 2 ");
                arrayObj = manager.obtieneDatQuery(conn, querySelect.toString());
                jsonObj.put("idRegimenFiscal", arrayObj);
            }
            arrayObj = null;
            if (datProveedor.getnIdOper() == 2) {
                // Cuentas Bancarias
                // Catalogo Entidad Federativa
                query = "SELECT dbancoAbreviado,cBanco FROM tBancos WITH(NOLOCK) WHERE CBAN IS NOT NULL  Order by dbancoAbreviado";
                arrayObj = manager.obtieneDatQuery(conn, query);
                jsonObj.put("catBancos", arrayObj);
                arrayObj = null;
            }
            if (datProveedor.getnIdOper() == 4 || datProveedor.getnIdOper() == 5) {
                // obtiene el CBEN
                query = "SELECT CBEN FROM tBeneficiario WITH(NOLOCK) WHERE dRFC=replace('" + datProveedor.getRfc() + "','-','')";
                arrayObj = manager.datGuardados(conn, query);
                jsonObj.put("datGuardadostBeneficiario", arrayObj);
                arrayObj = null;
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw new Exception(e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return jsonObj;
    }

    public Caso generaCasoModificaProveedor(String uLogin, String folioSAI) throws GestionException {
        Connection conn = null;
        Caso c = null;
        try {
            conn = getConnection();
            c = new Caso();
            c.setFolio(folioSAI);
            c = CasoManager.select(conn, c);
            if (c != null) {
                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String fecha = sdf.format(date);
                Map<String, String> data = new HashMap<String, String>();
                data.put("FOLIO", c.getFolio());
                data.put("OPERADOR", uLogin);
                data.put("FECHA_DOCUMENTO", fecha);
                data.put("EJERCICIO_FISCAL", obtieneEjecicioFiscal());
                CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
                casoTx.avanzaCaso(c, uLogin, null, new String[] { "MODIFICA_PROVEEDOR" }, new String[] { "modifica_proveedor" }, data, "");
                if (manager.changeStatus(conn, folioSAI, "P"))
                    c = casoTx.ejecutaCaso(c.getIdCaso(), -1, uLogin);
                int nidoperAnt = manager.idOperAnt(conn, folioSAI);
                manager.updatetAltaProveedorIdOperAnt(conn, folioSAI, 5);
                manager.updateHabilitaLiberaCaso(conn, folioSAI, 1);
                manager.saveBitacoraAltaProveedor(conn, folioSAI, "Inicia modificaci\u00f3n tipo persona", "P", "S", 5, nidoperAnt, uLogin);
            } else {
                throw new GestionException("No se encontro el tramite con folio: " + folioSAI);
            }
            conn.commit();
            return c;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("No se pudo realizar el rollback debido al siguiente error:" + e2, e2);
                }
            throw new GestionException(e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e) {
                    log.warn("No fue posible cerrar la conexion a la base de datos debido al siguiente error: " + e, e);
                }
            conn = null;
        }
    }

    public Caso generaCasoModificaTipoPersona(String uLogin, String folioSAI) throws GestionException {
        Connection conn = null;
        Caso c = null;
        try {
            conn = getConnection();
            c = new Caso();
            c.setFolio(folioSAI);
            c = CasoManager.select(conn, c);
            if (c != null) {
                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String fecha = sdf.format(date);
                Map<String, String> data = new HashMap<String, String>();
                data.put("FOLIO", c.getFolio());
                data.put("OPERADOR", uLogin);
                data.put("FECHA_DOCUMENTO", fecha);
                data.put("EJERCICIO_FISCAL", obtieneEjecicioFiscal());
                CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
                casoTx.avanzaCaso(c, uLogin, null, new String[] { "MODIFICA_PROVEEDOR" }, new String[] { "modifica_proveedor" }, data, "");
                if (manager.changeStatus(conn, folioSAI, "P"))
                    c = casoTx.ejecutaCaso(c.getIdCaso(), -1, uLogin);
                int nidoperAnt = manager.idOperAnt(conn, folioSAI);
                manager.updatetAltaProveedorIdOperAnt(conn, folioSAI, 5);
                manager.updateHabilitaLiberaCaso(conn, folioSAI, 1);
                manager.saveBitacoraAltaProveedor(conn, folioSAI, "Inicia modificaci\u00f3n tipo persona", "P", "S", 5, nidoperAnt, uLogin);
            } else
                throw new GestionException("No se encontro el tramite con folio: " + folioSAI);
            conn.commit();
            return c;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("No se pudo realizar el rollback debido al siguiente error:" + e2, e2);
                }
            throw new GestionException(e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e) {
                    log.warn("No fue posible cerrar la conexion a la base de datos debido al siguiente error: " + e, e);
                }
            conn = null;
        }
    }

    public Caso generaCasoCtaBancaria(String uLogin, String folioSAI) throws GestionException {
        Connection conn = null;
        Caso c = null;
        try {
            conn = getConnection();
            c = new Caso();
            c.setFolio(folioSAI);
            c = CasoManager.select(conn, c);
            if (c != null) {
                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String fecha = sdf.format(date);
                Map<String, String> data = new HashMap<String, String>();
                data.put("FOLIO", c.getFolio());
                data.put("OPERADOR", uLogin);
                data.put("FECHA_DOCUMENTO", fecha);
                data.put("EJERCICIO_FISCAL", obtieneEjecicioFiscal());
                CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
                casoTx.avanzaCaso(c, uLogin, null, new String[] { "CAPTURA_PROVEEDOR" }, new String[] { "captura_cuentabancaria_proveedor" }, data, "");
                if (manager.changeStatus(conn, folioSAI, "A"))
                    c = casoTx.ejecutaCaso(c.getIdCaso(), -1, uLogin);
                int nidoperAnt = manager.idOperAnt(conn, folioSAI);
                manager.updatetAltaProveedorIdOperAnt(conn, folioSAI, 5);
                manager.updateHabilitaLiberaCaso(conn, folioSAI, 1);
                manager.updateEsActCta(conn, folioSAI, 1);
                manager.saveBitacoraAltaProveedor(conn, folioSAI, "Inicia modificaci\u00f3n cuenta bancaria", "A", "S", 5, nidoperAnt, uLogin);
            } else
                throw new GestionException("No se encontro el tramite con folio: " + folioSAI);
            conn.commit();
            return c;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("No se pudo realizar el rollback debido al siguiente error:" + e2, e2);
                }
            throw new GestionException(e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e) {
                    log.warn("No fue posible cerrar la conexion a la base de datos debido al siguiente error: " + e, e);
                }
            conn = null;
        }
    }

    public String obtieneEjecicioFiscal() throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String EjercicioFiscal = "";
        try {
            conn = getConnection();
            String queryEf = "select aEjercicioFiscal from tEjercicioFiscal where cActivo = 1 ";
            ps = conn.prepareStatement(queryEf);
            rs = ps.executeQuery();
            if (rs.next()) {
                EjercicioFiscal = rs.getString("aEjercicioFiscal");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return EjercicioFiscal;
    }

    public JSONObject existeRFCDadoDeAlta(DatosProveedor datProv) throws Exception, JSONException {
        JSONArray arrayObj = null;
        JSONObject jsonObj = new JSONObject();
        Connection conn = null;
        String query = "";
        boolean info = false;
        try {
            conn = getConnection();
            // existe en mCatalogoProveedor
            query = "select 'EXISTE' AS regreso" + " from mCatalogoProveedor WITH(NOLOCK) WHERE RTRIM(LTRIM(cIdRFC))='" + datProv.getRfc() + "'";
            arrayObj = manager.datGuardados(conn, query);
            jsonObj.put("existeProvCatalogoProveedor", arrayObj);
            if (arrayObj != null && arrayObj.length() > 0 && arrayObj.getJSONObject(0).getBoolean("HAYINFO")) {
                arrayObj = null;
                // datos Proveedor
                query = "SELECT case when len(cIdRFC)=14 then 1 else 2 end cIdTipoPersonaRFC,case when len(cIdRFC)=14 then cRepresentanteLegal else cRazonSocial end cNombre" + ",''cApellidoMaterno,''cApellidoPaterno,case when len(cIdRFC)=14 then cRazonSocial else '' end cRazonSocial" + ",case when len(cIdRFC)=14 then '' else isnull(cCURP,'') end cCURP,'PROVEEDOR' tipoPB" + ",cast (cIdEntidadFederativa as int) AS cEstadoFiscal,cCalle,cNumeroExterno,cNumeroInterno," + "cColonia,cCodigoPostal,cEmail,cUrl,nIdPyme,isnull(munip.ID_MUNICIPIO,1) cMunicipioFiscal FROM mCatalogoProveedor prov WITH(NOLOCK) " + "left join CAT_MUNICIPIO  munip with(nolock) on munip.MPO_NOMBRE=prov.cMunicipio " + "where cIdRFC='" + datProv.getRfc() + "'";
                arrayObj = manager.datGuardados(conn, query);
                jsonObj.put("datosCatalogoProveedor", arrayObj);
                arrayObj = null;
                // Catalogo de municipios
                query = "SELECT cat.mpo_nombre, cat.id_municipio as cIdMunicipio  FROM CAT_MUNICIPIO cat WITH(NOLOCK)" + " inner join mCatalogoProveedor prov WITH(NOLOCK) on cast (prov.cIdEntidadFederativa as int)=cat.ID_ESTADO" + " where cIdRFC='" + datProv.getRfc() + "'";
                arrayObj = manager.obtieneDatQuery(conn, query);
                jsonObj.put("catMunicipio", arrayObj);
                arrayObj = null;
                info = true;
            } else {
                arrayObj = null;
            }
            // existe en tAltaProveedor
            query = "SELECT cFolio AS existeFolio FROM tAltaProveedor WITH(NOLOCK) WHERE RTRIM(LTRIM(cIdRFC))='" + datProv.getRfc() + "'";
            arrayObj = manager.datGuardados(conn, query);
            jsonObj.put("existeProvAltaEmpleado", arrayObj);
            arrayObj = null;
            // Existe como beneficiario
            query = "select 'EXISTE' AS regresoBenef from tBeneficiario WITH(NOLOCK) WHERE RTRIM(LTRIM(dRFC))='" + datProv.getRfc1() + datProv.getRfc2() + datProv.getRfc3() + "'";
            arrayObj = manager.datGuardados(conn, query);
            jsonObj.put("existeBeneficiario", arrayObj);
            if (!info && arrayObj != null && arrayObj.length() > 0 && arrayObj.getJSONObject(0).getBoolean("HAYINFO")) {
                arrayObj = null;
                // datos Beneficiario
                query = " select cIdTipoPersonaRFC,'BENEFICIARIO' tipoPB" + " ,CASE WHEN cIdTipoPersonaRFC=1 THEN dNombre else '' END cRazonSocial" + " ,CASE WHEN cIdTipoPersonaRFC=1 THEN isnull(dAPaternoApoderado,'') else isnull(dApellidoPaterno,'') END cApellidoPaterno" + " ,CASE WHEN cIdTipoPersonaRFC=1 THEN isnull(dAMaternoApoderado,'') else isnull(dapellidomaterno,'') END cApellidoMaterno" + " ,CASE WHEN cIdTipoPersonaRFC=1 THEN isnull(dNombreApoderado,'') else isnull(dnombre,'') END cNombre " + " ,CASE WHEN cIdTipoPersonaRFC=1 THEN '' else dCURP END cCURP" + " ,dcallefiscal AS cCalle" + " ,isnull(dnodomiciliofiscal,'') AS cNumeroExterno" + " ,isnull(dnointeriordomiciliofiscal,'') AS cNumeroInterno" + "  , dcoloniafiscal AS cColonia, dcodigopostalfiscal AS cCodigoPostal, cEstadoFiscal,isnull(demailfiscal,'') AS cEmail" + " ,isnull(cMunicipioFiscal,1) cMunicipioFiscal" + "  ,isnull(dEMailActual,'')cEmail,''cUrl" + "  ,CASE WHEN cIdTipoPersonaRFC=3 then 4 else 1 end nIdPyme" + "  FROM   tBeneficiario WITH(NOLOCK)" + " where dRFC='" + datProv.getRfc1() + datProv.getRfc2() + datProv.getRfc3() + "'";
                arrayObj = manager.datGuardados(conn, query);
                jsonObj.put("datosCatalogoBeneficiario", arrayObj);
                arrayObj = null;
                // Catalogo de municipios
                query = "SELECT cat.mpo_nombre, cat.id_municipio as cIdMunicipio  FROM CAT_MUNICIPIO cat WITH(NOLOCK)" + " inner join tBeneficiario benef WITH(NOLOCK) on cast (benef.cEstadoFiscal as int)=cat.ID_ESTADO" + " where dRFC='" + datProv.getRfc1() + datProv.getRfc2() + datProv.getRfc3() + "'";
                arrayObj = manager.obtieneDatQuery(conn, query);
                jsonObj.put("catMunicipio", arrayObj);
                arrayObj = null;
                info = true;
            } else {
                arrayObj = null;
            }
            // Existe como beneficiario sin homoclave
            query = "select 'EXISTESINH' AS regresoSinH from tBeneficiario WITH(NOLOCK) WHERE RTRIM(LTRIM(substring(dRFC,1,10)))='" + datProv.getRfc1() + datProv.getRfc2() + "'";
            arrayObj = manager.datGuardados(conn, query);
            jsonObj.put("existeBeneficiarioSinH", arrayObj);
            if (!info && arrayObj != null && arrayObj.length() > 0 && arrayObj.getJSONObject(0).getBoolean("HAYINFO")) {
                arrayObj = null;
                // datos Beneficiario sin homoclave
                query = " select cIdTipoPersonaRFC,'BENEFICIARIO' tipoPB" + " ,CASE WHEN cIdTipoPersonaRFC=1 THEN dNombre else '' END cRazonSocial" + " ,CASE WHEN cIdTipoPersonaRFC=1 THEN isnull(dAPaternoApoderado,'') else isnull(dApellidoPaterno,'') END cApellidoPaterno" + " ,CASE WHEN cIdTipoPersonaRFC=1 THEN isnull(dAMaternoApoderado,'') else isnull(dapellidopaterno,'') END cApellidoMaterno" + " ,CASE WHEN cIdTipoPersonaRFC=1 THEN isnull(dNombreApoderado,'') else isnull(dnombre,'') END cNombre " + " ,CASE WHEN cIdTipoPersonaRFC=1 THEN '' else dCURP END cCURP" + " ,dcallefiscal AS cCalle" + " ,isnull(dnodomiciliofiscal,'') AS cNumeroExterno" + " ,isnull(dnointeriordomiciliofiscal,'') AS cNumeroInterno" + "  , dcoloniafiscal AS cColonia, dcodigopostalfiscal AS cCodigoPostal, cEstadoFiscal,isnull(demailfiscal,'') AS cEmail" + " ,isnull(cMunicipioFiscal,1) cMunicipioFiscal" + "  ,isnull(dEMailActual,'')cEmail,''cUrl" + "  ,CASE WHEN cIdTipoPersonaRFC=3 then 4 else 1 end nIdPyme" + "  FROM   tBeneficiario WITH(NOLOCK)" + " where dRFC='" + datProv.getRfc1() + datProv.getRfc2() + "'";
                arrayObj = manager.datGuardados(conn, query);
                jsonObj.put("datosCatalogoBeneficiarioSinH", arrayObj);
                arrayObj = null;
                // Catalogo de municipios
                query = "SELECT cat.mpo_nombre, cat.id_municipio as cIdMunicipio  FROM CAT_MUNICIPIO cat WITH(NOLOCK)" + " inner join tBeneficiario benef WITH(NOLOCK) on cast (benef.cEstadoFiscal as int)=cat.ID_ESTADO" + " where dRFC='" + datProv.getRfc1() + datProv.getRfc2() + "'";
                arrayObj = manager.obtieneDatQuery(conn, query);
                jsonObj.put("catMunicipio", arrayObj);
                arrayObj = null;
                info = true;
            } else {
                arrayObj = null;
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw new Exception(e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return jsonObj;
    }

    private boolean validaRFC(int nTipoPersona, String rfc) {
        boolean resp = false;
        if ((nTipoPersona == AltaProveedorBusinessLogic.TIPO_PERSONA_MORAL && rfc.length() == 12) || (nTipoPersona != AltaProveedorBusinessLogic.TIPO_PERSONA_MORAL && rfc.length() == 13)) {
            resp = true;
        }
        return resp;
    }

    public Respuesta validaAltaProveedor(Connection conn, DatosProveedor datProveedor, Usuario usuario) throws Exception {
        boolean existBenf = false;
        boolean existProv = false;
        // Proveedor
        int nTipoCBEN = 3;
        Respuesta resp = new Respuesta();
        resp.setResp(true);
        String descrpMov = "";
        try {
            int nidoperAnt = manager.idOperAnt(conn, datProveedor.getcFolio());
            String cDocHAplicadoAnt = manager.obtieneCDocumentoHAplicado(conn, datProveedor.getcFolio());
            manager.ultimoMovimientoAltaProv(conn, datProveedor.getcFolio(), usuario.getLogin());
            manager.updatetAltaProveedor(conn, datProveedor.getcFolio(), usuario.getLogin(), datProveedor.getcDocumentoHaplicado(), datProveedor.getcObservaciones());
            manager.updatetAltaProveedorIdOperAnt(conn, datProveedor.getcFolio(), datProveedor.getnIdOper());
            if (AltaProveedorBusinessLogic.PROVEEDOR.equalsIgnoreCase(datProveedor.getcTipoPB())) {
                datProveedor.setnProveedorHabilitado(1);
                existProv = manager.existeEnCatalogoProveedor(conn, datProveedor.getRfc());
                existBenf = manager.existeEnBeneficiario(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
                manager.updateActualizacionAltaProv(conn, datProveedor.getcFolio(), "");
                descrpMov = "Se envío modificaci\u00f3n de Proveedor a validaci\u00f3n";
                if (datProveedor.getnIdOper() == 3) {
                    descrpMov = "Se envío Proveedor a autorizaci\u00f3n";
                }
                manager.saveBitacoraAltaProveedor(conn, datProveedor.getcFolio(), descrpMov, datProveedor.getcDocumentoHaplicado(), cDocHAplicadoAnt, datProveedor.getnIdOper(), nidoperAnt, usuario.getLogin());
                if (existProv) {
                    // Actualiza mCatalogoProveesor
                    manager.updateCatalogoProveedor(conn, datProveedor);
                    operacionestBeneficiario(conn, datProveedor, manager, existBenf, nTipoCBEN);
                    resp.setMsg("Proveedor actualizado.");
                } else {
                    // Agrega en mCatalogoProveesor
                    manager.createCatalogoProveedor(conn, datProveedor, usuario.getU_UR());
                    operacionestBeneficiario(conn, datProveedor, manager, existBenf, nTipoCBEN);
                    resp.setMsg("Proveedor dado de alta.");
                }
                // Se valida el num de telefono
                if (manager.existTipoTelefono(conn, datProveedor.getRfc(), datProveedor.getnTipoTelefono())) {
                    manager.updateTipoTelefono(conn, datProveedor.getcTelefono(), datProveedor.getRfc(), datProveedor.getnTipoTelefono(), 1);
                } else {
                    manager.deleteTelefonoProveedor(conn, datProveedor.getRfc());
                    manager.CreateTipoTelefono(conn, datProveedor.getcTelefono(), datProveedor.getRfc(), datProveedor.getnTipoTelefono(), 1);
                }
            } else {
                // Es beneficiario
                String tipoPersona = "Beneficiario";
                resp = movimientosParaEmpBenef(datProveedor, conn, manager);
                if (datProveedor.getnTipoPersona() == AltaProveedorBusinessLogic.TIPO_PERSONA_EMPLEADO) {
                    tipoPersona = "Empleado";
                }
                manager.saveBitacoraAltaProveedor(conn, datProveedor.getcFolio(), "Se envío modificaci\u00f3n de " + tipoPersona + " a autorizaci\u00f3n", datProveedor.getcDocumentoHaplicado(), cDocHAplicadoAnt, datProveedor.getnIdOper(), nidoperAnt, usuario.getLogin());
            }
        } catch (Exception e) {
            resp.setResp(false);
            resp.setMsg((null == e.getMessage() ? "Error" : e.getMessage().toString()));
            log.error("Object: {}", e.getMessage());
            throw new Exception(e);
        }
        return resp;
    }

    public void validaAltaProveedorRapida(Connection conn, String cben, DatosProveedor datProveedor, Usuario usuario) throws Exception {
        try {
            int nidoperAnt = manager.idOperAnt(conn, datProveedor.getcFolio());
            String cDocHAplicadoAnt = manager.obtieneCDocumentoHAplicado(conn, datProveedor.getcFolio());
            manager.ultimoMovimientoAltaProv(conn, datProveedor.getcFolio(), usuario.getLogin());
            manager.updatetAltaProveedor(conn, datProveedor.getcFolio(), usuario.getLogin(), datProveedor.getcDocumentoHaplicado(), datProveedor.getcObservaciones());
            manager.updatetAltaProveedorIdOperAnt(conn, datProveedor.getcFolio(), datProveedor.getnIdOper());
            datProveedor.setnProveedorHabilitado(1);
            manager.updateActualizacionAltaProv(conn, datProveedor.getcFolio(), "");
            String descrpMov = "Se envío Proveedor a autorizaci\u00f3n";
            manager.saveBitacoraAltaProveedor(conn, datProveedor.getcFolio(), descrpMov, datProveedor.getcDocumentoHaplicado(), cDocHAplicadoAnt, datProveedor.getnIdOper(), nidoperAnt, usuario.getLogin());
            if (AltaProveedorManager.existeEnCatalogoProveedor(conn, datProveedor.getRfc())) {
                manager.updateCatalogoProveedor(conn, datProveedor);
            } else {
                manager.createCatalogoProveedor(conn, datProveedor, usuario.getU_UR());
            }
            if (manager.existeEnBeneficiario(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3()))
                manager.updateCatalogoBeneficiario(conn, datProveedor);
            else
                manager.createCatalogoBeneficiario(conn, datProveedor);
            manager.updateCBEN(conn, cben, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
            manager.deleteTelefonoProveedor(conn, datProveedor.getRfc());
            manager.CreateTipoTelefono(conn, datProveedor.getcTelefono(), datProveedor.getRfc(), datProveedor.getnTipoTelefono(), 1);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }

    public Respuesta validaAltaProveedor(DatosProveedor datProveedor, Usuario usuario) throws Exception {
        Connection conn = null;
        Respuesta resp = new Respuesta();
        try {
            conn = getConnection();
            resp = validaAltaProveedor(conn, datProveedor, usuario);
            conn.commit();
        } catch (Exception e) {
            resp.setResp(false);
            resp.setMsg((null == e.getMessage() ? "Error" : e.getMessage().toString()));
            com.syc.gestion.util.Util.rollback(conn);
            log.error("Object: {}", e.getMessage());
            throw new Exception(e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return resp;
    }

    private Respuesta movimientosParaEmpBenef(DatosProveedor datProveedor, Connection conn, AltaProveedorManager manager) throws Exception {
        boolean existBenf = manager.existeEnBeneficiario(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
        int nTipoCBEN = 2;
        String tipoPersona = "Beneficiario";
        String cBENActual = "";
        String cBENHistorico = "";
        String cBEN = "";
        Respuesta resp = new Respuesta();
        resp.setResp(true);
        if (datProveedor.getnTipoPersona() == AltaProveedorBusinessLogic.TIPO_PERSONA_EMPLEADO) {
            nTipoCBEN = 1;
            tipoPersona = "Empleado";
        }
        if (existBenf) {
            cBENActual = manager.obtieneCBENActual(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
            manager.updateCatalogoBeneficiario(conn, datProveedor);
            cBEN = manager.obtienecBeneficiario(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3(), datProveedor.getnTipoPersona());
            cBENHistorico = manager.obtieneCBENHistorico(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3(), nTipoCBEN);
            manager.updateCBEN(conn, cBEN, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
            if ("".equals(cBENHistorico)) {
                manager.saveCBENAlta(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3(), nTipoCBEN, cBEN);
            }
            // Guarda en Bitacora el CBEN
            if (!("".equals(cBENActual)) && !("".equals(cBEN)) && !(cBEN.equalsIgnoreCase(cBENActual))) {
                manager.saveBitacoraCBEN(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3(), cBENActual, cBEN);
            }
            resp.setMsg(tipoPersona + " actualizado.");
        } else {
            manager.createCatalogoBeneficiario(conn, datProveedor);
            // actualiza CBEN
            cBEN = manager.obtienecBeneficiario(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3(), datProveedor.getnTipoPersona());
            manager.updateCBEN(conn, cBEN, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
            manager.saveCBENAlta(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3(), nTipoCBEN, cBEN);
            resp.setMsg(tipoPersona + " dado de alta.");
        }
        if (manager.existeEnCatalogoProveedor(conn, datProveedor.getRfc())) {
            // desactivar en mcatalogoproveedor
            manager.disabledProveedor(conn, datProveedor.getRfc());
        }
        return resp;
    }

    private void operacionestBeneficiario(Connection conn, DatosProveedor datProveedor, AltaProveedorManager manager, boolean existBenf, int nTipoCBEN) throws Exception {
        String cBENActual = "";
        String cBENNuevo = "";
        String cBENHistorico = "";
        /* Ya existe en tbeneficiario */
        if (existBenf) {
            cBENActual = manager.obtieneCBENActual(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
            cBENHistorico = manager.obtieneCBENHistorico(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3(), nTipoCBEN);
            manager.updateCatalogoBeneficiario(conn, datProveedor);
            if ((cBENActual != null && cBENHistorico != null) && ("".equals(cBENActual) || !("P".equalsIgnoreCase(cBENActual.substring(0, 1)))) && ("".equals(cBENHistorico) || !("P".equalsIgnoreCase(cBENHistorico.substring(0, 1))))) {
                cBENNuevo = manager.generacBENProv(conn);
                manager.updateCBEN(conn, cBENNuevo, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
                manager.incrementCBENProveedor(conn);
                manager.saveCBENAlta(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3(), nTipoCBEN, cBENNuevo);
                /* Guarda enBitacora el CBEN */
                if (!("".equals(cBENActual)) && !("".equals(cBENNuevo)) && !(cBENNuevo.equalsIgnoreCase(cBENActual))) {
                    manager.saveBitacoraCBEN(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3(), cBENActual, cBENNuevo);
                }
            } else {
                manager.updateCBEN(conn, cBENHistorico, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
            }
            /* Se inserta en tBeneficiario */
        } else {
            cBENNuevo = manager.generacBENProv(conn);
            if (cBENNuevo == null || "".equalsIgnoreCase(cBENNuevo)) {
                throw new Exception("Error al generar el CBEN.");
            }
            manager.createCatalogoBeneficiario(conn, datProveedor);
            manager.updateCBEN(conn, cBENNuevo, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
            manager.incrementCBENProveedor(conn);
            manager.saveCBENAlta(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3(), nTipoCBEN, cBENNuevo);
        }
    }

    public Respuesta autorizaAltaProveedor(DatosProveedor datProveedor, Usuario usuario) throws Exception {
        Connection conn = null;
        Respuesta resp = new Respuesta();
        try {
            conn = getConnection();
            resp = autorizaAltaProveedor(conn, datProveedor, usuario);
            conn.commit();
        } catch (Exception e) {
            resp.setResp(false);
            resp.setMsg(e.getMessage().toString());
            conn.rollback();
            log.error("Object: {}", e.getMessage());
            throw new Exception(e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return resp;
    }

    public Respuesta autorizaAltaProveedor(Connection conn, DatosProveedor datProveedor, Usuario usuario) throws Exception {
        Respuesta resp = new Respuesta();
        resp.setResp(true);
        int nBCBEnviadoSICOP = 0;
        String tipoPersona = "";
        int nIdOperAnterior = 0;
        String cDocumentoHAplicadoAnterior = "";
        try {
            nIdOperAnterior = manager.idOperAnt(conn, datProveedor.getcFolio());
            cDocumentoHAplicadoAnterior = manager.obtieneCDocumentoHAplicado(conn, datProveedor.getcFolio());
            manager.updatetAltaProveedorAut(conn, datProveedor.getcFolio(), usuario.getLogin(), datProveedor.getcDocumentoHaplicado(), datProveedor.getcObservaciones());
            manager.updatetAltaProveedorIdOperAnt(conn, datProveedor.getcFolio(), datProveedor.getnIdOper());
            tipoPersona = datProveedor.getcTipoPB();
            if (datProveedor.getnTipoPersona() == AltaProveedorBusinessLogic.TIPO_PERSONA_EMPLEADO) {
                tipoPersona = AltaProveedorBusinessLogic.EMPLEADO;
            }
            if (datProveedor.isAutoriza()) {
                manager.updateEsActCta(conn, datProveedor.getcFolio(), 0);
                // actualizaCuentasBancariasExistentes valida si el CBEN es
                // nuevo y si es nuevo desactiva las ctas existentes si no las
                // activa
                if (manager.isNewCBEN(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3()) || datProveedor.getnTipoPersona() == 3) {
                    manager.enabledCtasBancarias(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
                } else {
                    manager.disabledCtasBancarias(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
                }
                // insertaRelCasoRFC
                if (!manager.existeBeneficiarioCaso(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3(), datProveedor.getnIdCaso())) {
                    manager.saveBeneficiarioCaso(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3(), datProveedor.getnIdCaso());
                }
                // registraCuentasBancarias pasa las cuentas bancarias de la
                // temp a la tabla definitiva
                if (datProveedor.getnTipoPersona() == AltaProveedorBusinessLogic.TIPO_PERSONA_EMPLEADO) {
                    /*
																											 * Cuando
																											 * es
																											 * empleado
																											 */
                    nBCBEnviadoSICOP = 1;
                    manager.enabledtBeneficiario(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
                }
                manager.copyCtasTmp(conn, datProveedor.getcFolio(), nBCBEnviadoSICOP);
                // Habilita el proveedor en mcatalogoproveedor, esto es para
                // cuando es una actualización de cuenta
                if (AltaProveedorBusinessLogic.PROVEEDOR.equalsIgnoreCase(datProveedor.getcTipoPB())) {
                    manager.enabledProveedor(conn, datProveedor.getRfc());
                }
                // borrar las ctas tmp
                manager.deleteCtasTmp(conn, datProveedor.getcFolio());
                manager.saveBitacoraAltaProveedor(conn, datProveedor.getcFolio(), "El " + tipoPersona + " se autoriz\u00f3", datProveedor.getcDocumentoHaplicado(), cDocumentoHAplicadoAnterior, datProveedor.getnIdOper(), nIdOperAnterior, usuario.getLogin());
                resp.setMsg("El " + tipoPersona + " se autoriz\u00f3");
            } else {
                manager.disabledProveedor(conn, datProveedor.getRfc());
                manager.saveBitacoraAltaProveedor(conn, datProveedor.getcFolio(), "El " + tipoPersona + " se rechaz\u00f3", datProveedor.getcDocumentoHaplicado(), cDocumentoHAplicadoAnterior, datProveedor.getnIdOper(), nIdOperAnterior, usuario.getLogin());
                resp.setMsg("El " + tipoPersona + " se rechaz\u00f3");
            }
        } catch (Exception e) {
            resp.setResp(false);
            resp.setMsg(e.getMessage().toString());
            log.error("Object: {}", e.getMessage());
            throw new Exception(e);
        }
        return resp;
    }

    public Respuesta modificaAltaProv(DatosProveedor datProveedor, Usuario usuario) throws Exception {
        Connection conn = null;
        Respuesta resp = new Respuesta();
        resp.setResp(true);
        resp.setMsg("Datos modificados.");
        int tipoPersonaActual = 0;
        String msg = "";
        try {
            conn = getConnection();
            int idOperAnt = manager.idOperAnt(conn, datProveedor.getcFolio());
            String cDocumentoHAplicadoAnterior = manager.obtieneCDocumentoHAplicado(conn, datProveedor.getcFolio());
            manager.updateHabilitaLiberaCaso(conn, datProveedor.getcFolio(), 0);
            if (manager.hayCambioTipoPersona(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3(), datProveedor.getnTipoPersona(), datProveedor.getcTipoPB())) {
                tipoPersonaActual = manager.queTipoPersonaEs(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
                if (1 == tipoPersonaActual) {
                    // Es proveedor
                    // 1.-Validar que no tenga un procedimiento de ajudicación
                    // en tramite
                    boolean proced = manager.hayProcedAdjudicacionEnTramite(conn, datProveedor.getRfc());
                    if (proced) {
                        msg = "Tiene procedimientos de adjudicación en tramite.\n";
                    }
                    // 2.-Validar que no tenga contrato pendiente de pagos.
                    boolean hayContrato = manager.hayContratoPendienteDePago(conn, datProveedor.getRfc());
                    if (hayContrato) {
                        msg += "El proveedor tiene contratos pendientes de finiquitar.";
                    }
                    if (proced || hayContrato) {
                        resp.setMsg(msg);
                        resp.setResp(false);
                    } else {
                        // Guardar datos de modificación
                        datProveedor.setcObservaciones("Cambi\u00f3 de PROVEEDOR a " + (datProveedor.getnTipoPersona() == 3 ? AltaProveedorBusinessLogic.EMPLEADO : "BENEFICIARIO"));
                        saveDataMod(conn, manager, datProveedor, usuario);
                        manager.saveBitacoraAltaProveedor(conn, datProveedor.getcFolio(), datProveedor.getcObservaciones(), datProveedor.getcDocumentoHaplicado(), cDocumentoHAplicadoAnterior, datProveedor.getnIdOper(), idOperAnt, usuario.getLogin());
                    }
                } else if (2 == tipoPersonaActual) {
                    // Beneficiario
                    // 1.-Validar que no tenga contrato federalizado pendiente
                    // de pagos.
                    boolean hayContratoFed = manager.hayContratoFederalizadoPendienteDePago(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
                    if (hayContratoFed) {
                        resp.setMsg("El beneficiario tiene contratos pendientes de finiquitar.");
                        resp.setResp(false);
                    } else {
                        // Guardar datos de modificación
                        datProveedor.setcObservaciones("Cambi\u00f3 de BENEFICIARIO a " + (datProveedor.getnTipoPersona() == AltaProveedorBusinessLogic.TIPO_PERSONA_EMPLEADO ? AltaProveedorBusinessLogic.EMPLEADO : AltaProveedorBusinessLogic.PROVEEDOR));
                        saveDataMod(conn, manager, datProveedor, usuario);
                        manager.saveBitacoraAltaProveedor(conn, datProveedor.getcFolio(), datProveedor.getcObservaciones(), datProveedor.getcDocumentoHaplicado(), cDocumentoHAplicadoAnterior, datProveedor.getnIdOper(), idOperAnt, usuario.getLogin());
                    }
                } else if (AltaProveedorBusinessLogic.TIPO_PERSONA_EMPLEADO == tipoPersonaActual) {
                    // Empleado
                    // 1.-Validar boletos de avión pendientes de comprobar
                    boolean hayBoleto = manager.hayBoletoAvionPendienteComprobar(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
                    if (hayBoleto) {
                        msg += "El empleado tiene boletos sin comprobar .";
                    }
                    // 2.-validar que no sea deudor
                    boolean esDeudor = manager.esDeudor(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
                    if (esDeudor) {
                        msg += "El empleado es deudor.";
                    }
                    if (esDeudor || hayBoleto) {
                        resp.setMsg(msg);
                        resp.setResp(false);
                    } else {
                        // Guardar datos de modificación
                        datProveedor.setcObservaciones("Cambi\u00f3 de EMPLEADO a " + datProveedor.getcTipoPB());
                        saveDataMod(conn, manager, datProveedor, usuario);
                        manager.saveBitacoraAltaProveedor(conn, datProveedor.getcFolio(), datProveedor.getcObservaciones(), datProveedor.getcDocumentoHaplicado(), cDocumentoHAplicadoAnterior, datProveedor.getnIdOper(), idOperAnt, usuario.getLogin());
                    }
                } else {
                    // Tipo de persona desconocido
                    resp.setResp(false);
                    resp.setMsg("Tipo de perona actual " + tipoPersonaActual + " desconocido.");
                }
            } else {
                if (4 == idOperAnt || 3 == idOperAnt) {
                    // rechazado de
                    // validación
                    datProveedor.setcObservaciones("Datos actualizados, sin cambiar el tipo persona.");
                    saveDataMod(conn, manager, datProveedor, usuario);
                    manager.saveBitacoraAltaProveedor(conn, datProveedor.getcFolio(), datProveedor.getcObservaciones(), datProveedor.getcDocumentoHaplicado(), cDocumentoHAplicadoAnterior, datProveedor.getnIdOper(), idOperAnt, usuario.getLogin());
                } else {
                    resp.setResp(false);
                    resp.setMsg("El tipo de perona no cambio.");
                }
            }
            if (resp.isResp()) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (Exception e) {
            resp.setResp(false);
            resp.setMsg(e.getMessage().toString());
            conn.rollback();
            log.error("Object: {}", e.getMessage());
            throw new Exception(e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return resp;
    }

    private void saveDataMod(Connection conn, AltaProveedorManager manager, DatosProveedor datProveedor, Usuario usuario) throws Exception {
        validaDatos(datProveedor);
        // Validar RFC
        if (validaRFC(datProveedor.getnTipoPersona(), datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3())) {
            manager.disabledProveedor(conn, datProveedor.getRfc());
            manager.disabledCtasBancarias(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
            manager.disabledtBeneficiario(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
            manager.copytAltaProveedor(conn, datProveedor.getcFolio(), usuario.getLogin());
            manager.updateDatAltaProveedor(conn, datProveedor);
            manager.updatetAltaProveedorMod(conn, datProveedor.getcFolio(), usuario.getLogin(), datProveedor.getcDocumentoHaplicado(), datProveedor.getcObservaciones());
            manager.updateActualizacionAltaProv(conn, datProveedor.getcFolio(), "1");
            // Se valida el num de telefono
            if (datProveedor.getnTipoPersona() == AltaProveedorBusinessLogic.TIPO_PERSONA_MORAL) {
                if (manager.existTipoTelefono(conn, datProveedor.getRfc(), datProveedor.getnTipoTelefono())) {
                    manager.updateTipoTelefono(conn, datProveedor.getcTelefono(), datProveedor.getRfc(), datProveedor.getnTipoTelefono(), 1);
                } else {
                    manager.CreateTipoTelefono(conn, datProveedor.getcTelefono(), datProveedor.getRfc(), datProveedor.getnTipoTelefono(), 1);
                }
            }
        } else {
            throw new Exception("La cantidad de caracteres del RFC es incorrecta.");
        }
    }

    public Respuesta enviar(DatosProveedor datProveedor, Usuario usuario) throws Exception {
        Connection conn = null;
        Respuesta resp = new Respuesta();
        resp.setResp(true);
        resp.setMsg("Datos actualizados.");
        int idGabinete = -1;
        int idDocSig = 1;
        String msg = "", token = "";
        boolean errorCrearDoc = false;
        JSONObject jsonObj = null;
        String cDescripcionMovimiento = "";
        String cDocumentoHAplicadoAnterior = "";
        int nIdOperAnterior = 0;
        try {
            conn = getConnection();
            manager.ultimoMovimientoAltaProv(conn, datProveedor.getcFolio(), usuario.getLogin());
            nIdOperAnterior = manager.idOperAnt(conn, datProveedor.getcFolio());
            cDocumentoHAplicadoAnterior = manager.obtieneCDocumentoHAplicado(conn, datProveedor.getcFolio());
            manager.updatetAltaProveedorIdOperAnt(conn, datProveedor.getcFolio(), datProveedor.getnIdOper());
            if (datProveedor.getnIdOper() != 6) {
                manager.updatetAltaProveedorObservacion(conn, datProveedor.getcFolio(), datProveedor.getcObservaciones());
                manager.changeStatus(conn, datProveedor.getcFolio(), datProveedor.getcDocumentoHaplicado());
                if (datProveedor.getnIdOper() == 1) {
                    cDescripcionMovimiento = "Evia la 1er captura de datos";
                } else if (datProveedor.getnIdOper() == 3 && !datProveedor.isAutoriza()) {
                    /* devolución en la validación */
                    cDescripcionMovimiento = "Se rechaza el tramite \"" + datProveedor.getcObservaciones() + "\"";
                }
            } else {
                cDescripcionMovimiento = "Se envío el tramite a validación";
            }
            manager.saveBitacoraAltaProveedor(conn, datProveedor.getcFolio(), cDescripcionMovimiento, datProveedor.getcDocumentoHaplicado(), cDocumentoHAplicadoAnterior, datProveedor.getnIdOper(), nIdOperAnterior, usuario.getLogin());
            // CREA LOS DOCUMENTOS DE LAS CUENTAS EXISTENTES(CUANDO ES UNA
            // ACTUALIZACIÓN Y NO TIENE UN ALTA REGISTRADA)
            if (datProveedor.getnIdOper() == 1 && manager.hayCtasBancarias(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3())) {
                idGabinete = manager.obtieneIdGabinete(conn, datProveedor.getcTituloAplicacion(), datProveedor.getcFolio());
                if (idGabinete == -1) {
                    throw new Exception("Error: No se obtuvo el ID GABINETE.");
                } else {
                    idDocSig = manager.obtieneIdDocSiguiente(conn, idGabinete, datProveedor.getcTituloAplicacion());
                    jsonObj = manager.obtieneCtasSinDocumentos(conn, datProveedor.getcTituloAplicacion(), datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3(), idGabinete);
                    int i = 0;
                    while (i < jsonObj.length()) {
                        if (!manager.creaDocumentoCtaBancaria(conn, datProveedor.getcTituloAplicacion(), datProveedor.getcFolio(), idGabinete, idDocSig, jsonObj.getString("" + i), usuario.getLogin())) {
                            msg = msg + token + "No fue posible crear el documento para la cta bancaria " + jsonObj.getString("" + i);
                            token = "\n";
                            errorCrearDoc = true;
                        }
                        i++;
                        idDocSig++;
                    }
                    if (errorCrearDoc) {
                        throw new Exception(msg);
                    }
                }
            }
            conn.commit();
        } catch (Exception e) {
            resp.setResp(false);
            resp.setMsg(e.getMessage().toString());
            conn.rollback();
            log.error("Object: {}", e.getMessage());
            throw new Exception(e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            jsonObj = null;
            conn = null;
        }
        return resp;
    }

    public Respuesta EliminaCtaBancaria(DatosProveedor datProveedor, Usuario u) throws Exception, JSONException {
        Connection conn = null;
        Respuesta resp = null;
        DatosCtaBancaria datCtaBanc = null;
        String cDocumentoHAplicadoAnterior = "";
        int nIdOperAnterior = 0;
        try {
            conn = getConnection();
            resp = new Respuesta();
            datCtaBanc = datProveedor.getCuentasBancarias().get(0);
            resp.setResp(false);
            String nombreDocumento = datCtaBanc.getcNameBanco() + "-" + datCtaBanc.getcCuentaBancaria();
            int idCarpetaPadre = 5;
            int idGabinete = manager.obtieneIdGabinete(conn, datProveedor.getcTituloAplicacion(), datProveedor.getcFolio());
            int idDocumento = manager.obtieneIdDocumento(conn, datProveedor.getcTituloAplicacion(), nombreDocumento, idCarpetaPadre, idGabinete);
            if (manager.existePago(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3(), datCtaBanc.getcClabeInterbancaria())) {
                throw new Exception("No se puede eliminar la cuenta bancaria porque ya tiene pagos.");
            }
            cDocumentoHAplicadoAnterior = manager.obtieneCDocumentoHAplicado(conn, datProveedor.getcFolio());
            nIdOperAnterior = manager.idOperAnt(conn, datProveedor.getcFolio());
            if (manager.esCtaBancariaNueva(conn, datProveedor)) {
                // La cta
                // bancaria
                // esta
                // en la
                // temporal
                // Se
                manager.deletePaginaDocto(conn, datProveedor.getcTituloAplicacion(), idCarpetaPadre, idGabinete, idDocumento);
                // elimina
                // la
                // pagina
                if (manager.deleteDoctoCtaBancaria(conn, datProveedor.getcTituloAplicacion(), idCarpetaPadre, idGabinete, idDocumento)) {
                    // Eliminar
                    // documento
                    // de
                    // la
                    // cuenta
                    if (manager.deleteCtaTmp(conn, datProveedor)) {
                        resp.setResp(true);
                        resp.setMsg("Cuenta bancaria eliminada correctamente.");
                        manager.saveBitacoraAltaProveedor(conn, datProveedor.getcFolio(), "Elimina cuenta bancaria de la temporal", datProveedor.getcDocumentoHaplicado(), cDocumentoHAplicadoAnterior, datProveedor.getnIdOper(), nIdOperAnterior, u.getLogin());
                    } else {
                        resp.setResp(false);
                        resp.setMsg("No fue posible eliminar la cuenta bancaria.");
                    }
                } else {
                    resp.setResp(false);
                    resp.setMsg("No fue posible eliminar el documento de la cuenta bancaria.");
                }
            } else {
                // Cuentas en la tabla definitiva
                if (AltaProveedorManager.guardaCtaEliminada(conn, datProveedor)) {
                    manager.respaldaPaginaDocto(conn, datProveedor.getcTituloAplicacion(), idCarpetaPadre, idGabinete, idDocumento);
                    manager.respaldaDoctoCtaBancaria(conn, datProveedor.getcTituloAplicacion(), idCarpetaPadre, idGabinete, idDocumento, datCtaBanc.getcMotivoEliminaCta(), u.getLogin());
                    manager.deletePaginaDocto(conn, datProveedor.getcTituloAplicacion(), idCarpetaPadre, idGabinete, idDocumento);
                    if (manager.deleteDoctoCtaBancaria(conn, datProveedor.getcTituloAplicacion(), idCarpetaPadre, idGabinete, idDocumento)) {
                        // Eliminar
                        // documento
                        // de
                        // la
                        // cuenta
                        if (manager.deleteCtaBancaria(conn, datProveedor)) {
                            // Eliminar
                            // cuenta
                            // bancaria
                            manager.saveBitacoraAltaProveedor(conn, datProveedor.getcFolio(), "Elimina cuenta bancaria", datProveedor.getcDocumentoHaplicado(), cDocumentoHAplicadoAnterior, datProveedor.getnIdOper(), nIdOperAnterior, u.getLogin());
                            resp.setResp(true);
                            resp.setMsg("Cuenta bancaria eliminada correctamente.");
                        } else {
                            resp.setResp(false);
                            resp.setMsg("No fue posible eliminar la cuenta bancaria.");
                        }
                    } else {
                        resp.setResp(false);
                        resp.setMsg("No fue posible eliminar el documento de la cuenta bancaria.");
                    }
                } else {
                    // No fue posible respaldar la cuenta bancaria
                    resp.setResp(false);
                    resp.setMsg("No fue posible respaldar la cuenta bancaria");
                }
            }
            if (resp.isResp()) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (Exception e) {
            resp.setResp(false);
            resp.setMsg(e.getMessage().toString());
            conn.rollback();
            throw new Exception(e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return resp;
    }

    public Respuesta liberaCaso(DatosProveedor datProveedor, Usuario u) throws Exception {
        Connection conn = null;
        Respuesta resp = new Respuesta();
        resp.setResp(true);
        resp.setMsg("Folio Liberado.");
        String cDocumentoHAplicadoAnterior = "";
        int nIdOperAnterior = 0;
        try {
            conn = getConnection();
            cDocumentoHAplicadoAnterior = manager.obtieneCDocumentoHAplicado(conn, datProveedor.getcFolio());
            nIdOperAnterior = manager.idOperAnt(conn, datProveedor.getcFolio());
            if (manager.updateAltaProveedorAplicado(conn, datProveedor.getcFolio()) && manager.updateCasoAConsulta(conn, datProveedor.getcFolio())) {
                manager.updatetAltaProveedorIdOperAnt(conn, datProveedor.getcFolio(), datProveedor.getnIdOper());
                manager.enabledCtasBancarias(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
                manager.enabledtBeneficiario(conn, datProveedor.getRfc1() + datProveedor.getRfc2() + datProveedor.getRfc3());
                manager.updateHabilitaLiberaCaso(conn, datProveedor.getcFolio(), 0);
                manager.saveBitacoraAltaProveedor(conn, datProveedor.getcFolio(), "Libera Caso", datProveedor.getcDocumentoHaplicado(), cDocumentoHAplicadoAnterior, datProveedor.getnIdOper(), nIdOperAnterior, u.getLogin());
                conn.commit();
            } else {
                resp.setResp(false);
                resp.setMsg("Hubo un problema al liberar. Favor de Reportar al administrador.");
                conn.rollback();
            }
        } catch (Exception e) {
            resp.setResp(false);
            resp.setMsg(e.getMessage().toString());
            conn.rollback();
            log.error("Object: {}", e.getMessage());
            throw new Exception(e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return resp;
    }

    public void deleteAltaProveedor(Connection conn, String rfc, String login, String folio) throws ProveedorException {
        log.info("Object: {}", "Eliminando proveedor: " + rfc);
        try {
            if (AltaProveedorManager.esProveedorConPagos(conn, rfc)) {
                AltaProveedorManager.cambiaStatusCtasBanBeneficiario(conn, -1, rfc);
                AltaProveedorManager.cambiaStatusBeneficiario(conn, -1, rfc);
            } else {
                AltaProveedorManager.eliminaTramite(conn, folio, login);
                if (AltaProveedorManager.existeEnCatalogoProveedor(conn, rfc)) {
                    if (AltaProveedorManager.hayAdjudicacionEnTramite(conn, rfc)) {
                        AltaProveedorManager.disabledProveedor(conn, rfc);
                    } else {
                        AltaProveedorManager.deleteCatalogoProveedor(conn, rfc);
                    }
                }
                AltaProveedorManager.eliminaBeneficiarioCtaBancaria(conn, rfc);
                AltaProveedorManager.eliminaBeneficiario(conn, rfc);
            }
        } catch (Exception e) {
            throw new ProveedorException(e);
        }
    }

    public Map<String, String> getAltaProveedorMap(String folio) throws ProveedorException {
        Connection conn = null;
        try {
            conn = getConnection();
            return manager.getAltaProveedorMap(conn, folio);
        } catch (SQLException e) {
            throw new ProveedorException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void avanzaOperacionAltaRapida(Usuario u, String folio, String responsable, String operacion) throws ProveedorException {
        Connection conn = null;
        try {
            conn = getConnection();
            DatosProveedor datosProveedor = manager.getAltaProveedorObj(conn, folio);
            String cben = manager.obtieneCBENHistorico(conn, datosProveedor.getRfc1() + datosProveedor.getRfc2() + datosProveedor.getRfc3(), CBEN_PROVEEDOR);
            if (StringUtils.isBlank(cben)) {
                cben = manager.generacBENProv(conn);
                manager.incrementCBENProveedor(conn);
                manager.saveCBENAlta(conn, datosProveedor.getRfc1() + datosProveedor.getRfc2() + datosProveedor.getRfc3(), AltaProveedorBusinessLogic.CBEN_PROVEEDOR, cben);
                manager.saveBitacoraCBEN(conn, datosProveedor.getRfc1() + datosProveedor.getRfc2() + datosProveedor.getRfc3(), "", cben);
                manager.setNewCBEN(conn, datosProveedor.getRfc1() + datosProveedor.getRfc2() + datosProveedor.getRfc3());
            }
            manager.avanzaOperacionAltaRapida(conn, u, folio, responsable, operacion);
            conn.commit();
        } catch (Exception e) {
            com.syc.gestion.util.Util.rollback(conn);
            throw new ProveedorException(e.toString(), e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void autorizaAltaRapidaProveedor(Usuario user, String folio) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            Caso c = new Caso();
            c.setFolio(folio);
            c = CasoManager.select(conn, c);
            DatosProveedor datosProveedor = manager.getAltaProveedorObj(conn, folio);
            datosProveedor.setnIdCaso(c.getIdCaso());
            datosProveedor.setnIdOper(c.getCasoOperacion(0).getOperacion().getNumero());
            datosProveedor.setcTipoPB(AltaProveedorBusinessLogic.PROVEEDOR);
            datosProveedor.setcTituloAplicacion("");
            datosProveedor.setnProveedorHabilitado(1);
            datosProveedor.setAutoriza(false);
            datosProveedor.setcObservaciones("");
            datosProveedor.setcDocumentoHaplicado("S");
            String cBenGenerado = manager.obtieneCBENHistorico(conn, datosProveedor.getRfc1() + datosProveedor.getRfc2() + datosProveedor.getRfc3(), CBEN_PROVEEDOR);
            validaAltaProveedorRapida(conn, cBenGenerado, datosProveedor, user);
            manager.avanzaOperacionAltaRapida(conn, user, folio, "CONSULTA_ALTAPROVEEDORCORTA", "consulta_alta_proveedor");
            c = CasoManager.select(conn, c);
            datosProveedor.setnIdOper(c.getCasoOperacion(0).getOperacion().getNumero());
            datosProveedor.setAutoriza(true);
            datosProveedor.setcDocumentoHaplicado("S");
            autorizaAltaProveedor(conn, datosProveedor, user);
            conn.commit();
        } catch (Exception e) {
            com.syc.gestion.util.Util.rollback(conn);
            throw new ProveedorException(e.toString(), e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void rechazaProveedor(Usuario user, String folio, String observaciones) throws ProveedorException {
        Connection conn = null;
        Caso c = new Caso(folio);
        try {
            conn = getConnection();
            c = CasoManager.select(conn, c);
            manager.ultimoMovimientoAltaProv(conn, folio, user.getLogin());
            int nIdOperAnterior = manager.idOperAnt(conn, folio);
            String cDocumentoHAplicadoAnterior = manager.obtieneCDocumentoHAplicado(conn, folio);
            manager.updatetAltaProveedorIdOperAnt(conn, folio, c.getCasoOperacion(0).getOperacion().getNumero());
            manager.updatetAltaProveedorObservacion(conn, folio, observaciones);
            String cDescripcionMovimiento = "Se rechaza el tramite \"" + observaciones + "\"";
            manager.saveBitacoraAltaProveedor(conn, folio, cDescripcionMovimiento, "", cDocumentoHAplicadoAnterior, 4, nIdOperAnterior, user.getLogin());
            manager.avanzaOperacionAltaRapida(conn, user, folio, "ALTAPROVEEDORCORTA", "corrige_proveedor_corta");
            conn.commit();
        } catch (Exception e) {
            com.syc.gestion.util.Util.rollback(conn);
            throw new ProveedorException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void deleteAltaProveedor(Usuario user, String rfc, String folio) throws ProveedorException {
        Connection conn = null;
        try {
            conn = getConnection();
            Caso c = new Caso(folio);
            c = CasoManager.select(conn, c);
            deleteAltaProveedor(conn, rfc, user.getLogin(), folio);
            CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);
            Map<String, String> m = com.syc.gestion.util.Util.readValuesCasoDato(c.getCasoDato());
            cbl.avanzaCaso(conn, c, user.getLogin(), "Caso cancelado por el usuario", new String[] { "TERMINAR" }, new String[] { "TERMINAR" }, m, null);
            conn.commit();
        } catch (Exception e) {
            com.syc.gestion.util.Util.rollback(conn);
            throw new ProveedorException(e.toString(), e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public Respuesta saveProveedorIncumplido(DatosProveedorIncumplido datProveedorIncump) throws Exception, ProveedorException {
        Connection conn = null;
        String cEjercicioActivo = null;
        Respuesta resp = null;
        try {
            conn = getConnection();
            resp = new Respuesta();
            cEjercicioActivo = Util.obtieneEjercicioFiscalActivo(conn);
            datProveedorIncump.setcEjercicioFiscal(cEjercicioActivo);
            manager.addProveedorIncumplido(conn, datProveedorIncump);
            manager.updateCatalogoProveedorIncumplido(conn, datProveedorIncump.getcIdRFC());
            resp.setResp(true);
            resp.setMsg("Datos guardados correctamente.");
            conn.commit();
        } catch (Exception e) {
            resp.setResp(false);
            resp.setMsg(e.getMessage().toString());
            throw new ProveedorException(e.toString(), e);
        } finally {
            CloseObject.closeObject(conn);
        }
        return resp;
    }
}
