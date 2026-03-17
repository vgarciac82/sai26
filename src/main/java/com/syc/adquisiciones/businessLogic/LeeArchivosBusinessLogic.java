package com.syc.adquisiciones.businessLogic;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.axtel.contratos.ContractStatus;
import com.axtel.contratos.Procedimiento;
import com.axtel.contratos.ProcedimientoFecha;
import com.axtel.contratos.Requisition;
import com.axtel.contratos.RequisitionStatus;
import com.axtel.contratos.core.Contrato;
import com.axtel.contratos.core.ContratosConGarantia;
import com.axtel.contratos.core.DatosContratoPSP;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.adquisiciones.core.DatosArchivo;
import com.syc.adquisiciones.core.PrecomMaterialesEncabezado;
import com.syc.adquisiciones.core.Respuesta;
import com.syc.adquisiciones.manager.ContratacionFormalizadaManager;
import com.syc.adquisiciones.manager.LeeArchivosManager;
import com.syc.adquisiciones.manager.ProcessAgreementManager;
import com.syc.adquisiciones.manager.ReportesGRMManager;
import com.syc.altaproveedor.AltaProveedorManager;
import com.syc.altaproveedor.DatosProveedor;
import com.syc.contable.ContableInterface;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.Role;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.procesosAutomaticos.AdjuntaArchivoMasivoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

public class LeeArchivosBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(LeeArchivosBusinessLogic.class);

    private static final int TERMINA_POR_RESCISION = 3;

    public Respuesta readFileEXCEL(DatosArchivo datosArchivo, Usuario usuario) throws Exception {
        Respuesta resp = new Respuesta();
        Connection conn = null;
        LeeArchivosManager manager = null;
        int regAct = 0, regNuevos = 0;
        String token = "";
        resp.setMsg("");
        File file = null;
        try {
            conn = getConnection(GestionInterface.ATT_CONEXION);
            manager = new LeeArchivosManager();
            // delete table mHistoriaEFOSSAT
            manager.deleteEFOSTEMP(conn);
            // read file and insert in the table mHistoriaEFOSSAT
            manager.readFileExcelAndInsertDB(conn, datosArchivo);
            // Update rows existing in the table mCatalogoEFOS
            regAct = manager.updateEFOSExisting(conn);
            if (regAct > 0) {
                resp.setMsg("Registros actualizados : " + regAct);
                token = "\n";
            }
            // take of duplicate rows and insert new rows in the table
            // mCatalogoEFOS
            regNuevos = manager.insertEFOS(conn);
            if (regNuevos > 0) {
                resp.setMsg(resp.getMsg() + token + "Se agregaron " + regNuevos + " registros nuevos.");
            }
            // genarate file for attachment in the email
            file = generaReporteProveedoresEFOS(conn, datosArchivo.getNamePlantilla());
            // Send mail with the EFOS file
            manager.sendEmailLoadEFOS(conn, file);
            conn.commit();
            log.info("Termina la carga de layout EFOS.");
        } catch (Exception e) {
            resp.setMsg(e.getMessage().toString());
            resp.setResp(false);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e2) {
                    resp.setMsg("Problemas en rollback: " + e2.getMessage().toString());
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
            }
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (file != null && !file.delete())
                file.deleteOnExit();
            conn = null;
            manager = null;
            file = null;
        }
        return resp;
    }

    public File generaReporteProveedoresEFOS(Connection conn, String namePlantilla) throws Exception {
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteProveedoresEfos_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        File cFileExcelPlantilla = null;
        InputStream fs = null;
        InputStream fsArchivo = null;
        LeeArchivosManager manager = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        XSSFWorkbook workbook = null;
        File fsalida = null;
        XSSFCellStyle estiloTabla = null;
        try {
            manager = new LeeArchivosManager();
            cFileExcelPlantilla = new File(namePlantilla);
            fs = new FileInputStream(cFileExcelPlantilla);
            Util.copiaArchivo(fs, file_name);
            fsArchivo = new FileInputStream(cFileExcelPlantilla);
            workbook = new XSSFWorkbook(fsArchivo);
            XSSFSheet firstSheet = workbook.getSheetAt(0);
            estiloTabla = workbook.createCellStyle();
            estiloTabla.setBorderBottom(BorderStyle.THIN);
            estiloTabla.setBorderLeft(BorderStyle.THIN);
            estiloTabla.setBorderRight(BorderStyle.THIN);
            estiloTabla.setBorderTop(BorderStyle.THIN);
            // Escribe en la hoja 1
            manager.writeSheet1ProveedoresEFOS(conn, firstSheet, estiloTabla);
            // Escribe en la hoja 2
            XSSFSheet secondtSheet = workbook.getSheetAt(1);
            manager.writeSheet2ProveedoresEFOS(conn, secondtSheet, estiloTabla);
            fsalida = new File(file_name);
            fos = new FileOutputStream(fsalida);
            bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos);
            bos.flush();
        } finally {
            if (fos != null) {
                fos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fs != null) {
                fs.close();
            }
            if (workbook != null) {
                workbook.close();
            }
            fs = null;
            bos = null;
            workbook = null;
            fos = null;
            cFileExcelPlantilla = null;
            fs = null;
            manager = null;
        }
        return fsalida;
    }

    public Respuesta uploadContratoFisico(HttpSession session, DatosArchivo datosArchivo, Usuario usuario, Caso c, String jniName) throws Exception {
        Respuesta resp = new Respuesta();
        Connection conn = null;
        LeeArchivosManager manager = null;
        resp.setMsg("Inicia la carga de documento");
        CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);
        Map<String, Role> rol = null;
        try {
            conn = getConnection();
            manager = new LeeArchivosManager();
            rol = usuario.getRoles();
            if (!com.syc.adquisiciones.util.Util.validaRoleUsuario(rol)) {
                throw new Exception("No tiene permiso para ejecutar est\\u00e1 acci\\u00f3n.");
            }
            String ejercicioFiscal = com.syc.adquisiciones.util.Util.obtieneEjercicioFiscalActivo(conn);
            // Copia archivo a un directorio temporal
            Util.copiaArchivo(new DataInputStream(datosArchivo.getArchivoStream()), datosArchivo.getcNombreArchivoDestino());
            // Si no tiene expediente se crea
            if (c.getIdGabinete() == -1) {
                c.getCasoDato("FOLIO").setValor(c.getFolio());
                c.getCasoDato("FECHA_DOCUMENTO").setValor(Util.getToday());
                c.getCasoDato("EJERCICIO_FISCAL").setValor(ejercicioFiscal);
                c.getCasoDato("OPERADOR").setValor(c.getCasoOperacion(0).getResponsable());
                c.setIdGabinete(cbl.creaExpediente(usuario.getLogin(), c));
            }
            // copia archivo al fortimax
            AdjuntaArchivoMasivoManager.adjuntaArchivo(conn, usuario.getLogin(), c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), "DoctosContrato", "Contrato Fisico", new File(datosArchivo.getcNombreArchivoDestino()));
            // Se habilita bandera de carga de documento
            manager.updatePhysicalContractUpload(conn, datosArchivo.getcIdContratoDefinitivo(), 1);
            if (manager.existDoctoContract(conn, datosArchivo.getcIdContratoDefinitivo(), 1)) {
                manager.updateDoctoContrato(conn, datosArchivo.getcIdContratoDefinitivo(), 1, usuario.getLogin());
                // Se guarda bitacora
                com.syc.adquisiciones.util.Util.bitacoraMovimientos(datosArchivo.getcIdContratoDefinitivo(), "Contrato F\u00edsico Actualizado", usuario.getLogin(), conn);
            } else {
                manager.insertDoctoContrato(conn, datosArchivo.getcIdContratoDefinitivo(), 1, usuario.getLogin());
                // Se guarda bitacora
                com.syc.adquisiciones.util.Util.bitacoraMovimientos(datosArchivo.getcIdContratoDefinitivo(), "Contrato F\u00edsico Cargado", usuario.getLogin(), conn);
            }
            // Se crea el arbol.
            ITree tree = cbl.getArbolCaso(c);
            session.setAttribute(GestionInterface.ATT_CASE, c);
            session.setAttribute("tree.model", tree);
            resp.setMsg("Documento cargado");
            resp.setResp(true);
            conn.commit();
        } catch (SQLException e) {
            resp.setMsg(e.getMessage().toString());
            resp.setResp(false);
            log.error(e.getMessage(), e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e2) {
                    resp.setMsg("Problemas en rollback: " + e2.getMessage().toString());
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
            manager = null;
            rol = null;
            cbl = null;
        }
        return resp;
    }

    public String[] downloadContratoFisico(DatosArchivo datosArchivo, Usuario u) throws Exception {
        Caso c = null;
        Connection conn = null;
        ReportesGRMManager manager = null;
        String[] rutas = null;
        Caso srchCase = null;
        try {
            conn = getConnection();
            manager = new ReportesGRMManager();
            // Se obtiene el caso
            srchCase = new Caso();
            srchCase.setFolio(datosArchivo.getcFolio());
            c = CasoManager.select(conn, srchCase);
            // Se obtiene la ruta del archivo
            rutas = manager.selectRutaArchivo(conn, "" + c.getIdCaso(), c.getIdGabinete(), datosArchivo.getcTituloAplicacion(), datosArchivo.getcNombreArchivo());
            // Guarda bitacora
            com.syc.adquisiciones.util.Util.bitacoraMovimientos(datosArchivo.getcIdContratoDefinitivo(), "Descarga Contrato Físico", u.getLogin(), conn);
            conn.commit();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
            }
            throw new Exception("Bug, downloadContratoFisico: " + e.toString());
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
            manager = null;
            srchCase = null;
            c = null;
        }
        return rutas;
    }

    public String[] downloadGarantia(DatosArchivo datosArchivo, Usuario u, boolean isQuery) throws Exception {
        Connection conn = null;
        String[] rutas = null;
        ContratacionFormalizadaManager managerCont = null;
        ContratosConGarantia contrato = null;
        boolean error = true;
        try {
            conn = getConnection();
            contrato = new ContratosConGarantia();
            managerCont = new ContratacionFormalizadaManager();
            contrato.setcIdContratoDefinitivo(datosArchivo.getcIdContratoDefinitivo());
            managerCont.contratosConGarantia(conn, contrato);
            datosArchivo.setcEjercicioContrato(contrato.getcEjercicioFiscal());
            datosArchivo.setcFolio(contrato.getcFolioCaso());
            // Se obtiene la ruta del archivo
            rutas = getPathFile(conn, datosArchivo);
            // Guarda bitacora
            if (!isQuery) {
                com.syc.adquisiciones.util.Util.bitacoraMovimientos(datosArchivo.getcIdContratoDefinitivo(), "Descarga Documento " + datosArchivo.getcNombreArchivo(), u.getLogin(), conn);
            }
            conn.commit();
            error = false;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new Exception("Bug, SdownloadGarantia: " + e.toString());
        } finally {
            if (error) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException e2) {
                        log.warn("Object: {}", "Problemas en rollback: " + e2);
                    }
                }
            }
            if (conn != null) {
                conn.close();
            }
            conn = null;
            managerCont = null;
            contrato = null;
        }
        return rutas;
    }

    private String[] getPathFile(Connection conn, DatosArchivo datosArchivo) throws Exception {
        String[] rutas = null;
        Caso c = null;
        Connection connContrato = null;
        Caso srchCase = null;
        String cDBContrato = "sai";
        ReportesGRMManager manager = null;
        boolean error = true;
        try {
            manager = new ReportesGRMManager();
            // Generar conección del ejercicio del contrato
            cDBContrato = com.syc.adquisiciones.util.Util.getNameDB(conn, Integer.parseInt(datosArchivo.getcEjercicioContrato()));
            connContrato = com.syc.gestion.util.Util.getSAIConnection(conn, cDBContrato, datosArchivo.getcEjercicioContrato());
            // Se obtiene el caso
            srchCase = new Caso();
            srchCase.setFolio(datosArchivo.getcFolio());
            c = CasoManager.select(connContrato, srchCase);
            // Se obtiene la ruta del archivo
            if (c != null)
                rutas = manager.selectRutaArchivo(connContrato, "" + c.getIdCaso(), c.getIdGabinete(), datosArchivo.getcTituloAplicacion(), datosArchivo.getcNombreArchivo());
            connContrato.commit();
            error = false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception("Bug, getPathFile: " + e.toString());
        } finally {
            if (error) {
                if (connContrato != null) {
                    try {
                        connContrato.rollback();
                    } catch (SQLException e2) {
                        log.warn("Object: {}", "Problemas en rollback: " + e2);
                    }
                }
            }
            if (connContrato != null) {
                connContrato.close();
            }
            c = null;
            srchCase = null;
            connContrato = null;
            manager = null;
            cDBContrato = null;
        }
        return rutas;
    }

    public Respuesta recisionDeContrato(DatosArchivo datosArchivo, Usuario usuario) throws Exception {
        Respuesta resp = new Respuesta();
        Connection conn = null;
        Connection connSAIPrincipal = null;
        Connection connAux = null;
        AltaProveedorManager proveedorManager = null;
        LeeArchivosManager manager = null;
        ContratacionFormalizadaManager managerCont = null;
        Caso casoProv = null;
        Caso casoCont = null;
        Caso c = null;
        String nameDocto = "Recision_De_Contrato_" + datosArchivo.getcCodigoContratoCNET();
        String cEjercicioActivo = "-1";
        try {
            conn = getConnection(datosArchivo.getJniName());
            managerCont = new ContratacionFormalizadaManager();
            proveedorManager = new AltaProveedorManager();
            manager = new LeeArchivosManager();
            cEjercicioActivo = com.syc.adquisiciones.util.Util.obtieneEjercicioFiscalActivo(conn);
            boolean esSAIAlterno = "true".equals(ConfiguraAplicativoManager.getSystemSetting(conn, "SAI_AMBIENTAL"));
            if (esSAIAlterno) {
                connSAIPrincipal = Util.getSAIConnection(conn);
            }
            // Validar si existe la recisión
            if (managerCont.existRecisioncontrato(conn, datosArchivo)) {
                throw new Exception("Ya existe está recisión de contrato en SAI.");
            }
            managerCont.addRecisioncontrato(conn, datosArchivo, usuario);
            manager.insertTerminacionAnti(conn, datosArchivo, usuario.getLogin());
            // Obtener el caso del proveedor
            if (esSAIAlterno) {
                casoProv = AltaProveedorManager.getTramiteProveedor(connSAIPrincipal, datosArchivo.getcIdRFC());
            } else {
                casoProv = AltaProveedorManager.getTramiteProveedor(conn, datosArchivo.getcIdRFC());
            }
            if (casoProv == null) {
                throw new Exception("No se encontro el caso del proveedor.");
            }
            // Copia archivo a un directorio temporal
            Util.copiaArchivo(new DataInputStream(datosArchivo.getArchivoStream()), datosArchivo.getcNombreArchivoDestino());
            // agrega el archivo al modulo de proveedores
            if (esSAIAlterno) {
                AdjuntaArchivoMasivoManager.adjuntaArchivo(connSAIPrincipal, usuario.getLogin(), casoProv.getTipoCaso().getGavetaAsociada(), casoProv.getIdGabinete(), "Sanciones", nameDocto, new File(datosArchivo.getcNombreArchivoDestino()));
                proveedorManager.saveBitacoraAltaProveedor(connSAIPrincipal, casoProv.getFolio(), "Se rescindio el contrato " + datosArchivo.getcIdContratoDefinitivo(), "S", "S", 0, 0, usuario.getLogin());
            } else {
                AdjuntaArchivoMasivoManager.adjuntaArchivo(conn, usuario.getLogin(), casoProv.getTipoCaso().getGavetaAsociada(), casoProv.getIdGabinete(), "Sanciones", nameDocto, new File(datosArchivo.getcNombreArchivoDestino()));
                proveedorManager.saveBitacoraAltaProveedor(conn, casoProv.getFolio(), "Se rescindio el contrato " + datosArchivo.getcIdContratoDefinitivo(), "S", "S", 0, 0, usuario.getLogin());
            }
            /**
             * Se obtiene el caso del contrato y se obtiene la conexion de la
             * base donde está cargado el contrato
             */
            if (cEjercicioActivo.equalsIgnoreCase(datosArchivo.getcEjercicioContrato())) {
                connAux = getConnection(datosArchivo.getJniName());
            } else {
                connAux = Util.getSAIConnection(conn, datosArchivo.getcNameDB(), datosArchivo.getcEjercicioContrato());
            }
            c = new Caso();
            c.setFolio(datosArchivo.getcFolio());
            casoCont = CasoManager.select(connAux, c);
            if (casoCont == null) {
                throw new Exception("No se encontro el caso del contrato.");
            }
            // agrega el archivo al proceso del contrato
            AdjuntaArchivoMasivoManager.adjuntaArchivo(connAux, usuario.getLogin(), casoCont.getTipoCaso().getGavetaAsociada(), casoCont.getIdGabinete(), "DoctosContrato", nameDocto, new File(datosArchivo.getcNombreArchivoDestino()));
            resp.setMsg("Datos guardados.");
            resp.setResp(true);
            conn.commit();
            if (connAux != null)
                connAux.commit();
            if (connSAIPrincipal != null)
                connSAIPrincipal.commit();
        } catch (Exception e) {
            resp.setMsg(e.getMessage().toString());
            resp.setResp(false);
            log.error(e.getMessage(), e);
            Util.rollback(conn);
            Util.rollback(connAux);
            Util.rollback(connSAIPrincipal);
        } finally {
            CloseObject.closeObject(conn);
            CloseObject.closeObject(connAux);
            CloseObject.closeObject(connSAIPrincipal);
            managerCont = null;
            casoProv = null;
            casoCont = null;
            manager = null;
            c = null;
        }
        return resp;
    }

    public Respuesta terminacionAnticipadaContrato(DatosArchivo datosArchivo, Usuario usuario, String jniName) throws Exception {
        Respuesta resp = new Respuesta();
        Connection conn = null;
        Connection saiConnection = null;
        LeeArchivosManager manager = null;
        Caso c = null;
        CasoBusinessLogic cbl = null;
        String nameDocto = "Terminacion Anticipada";
        try {
            cbl = new CasoBusinessLogic(jniName);
            conn = getConnection(jniName);
            boolean esSAIAlterno = "true".equals(ConfiguraAplicativoManager.getSystemSetting(conn, "SAI_AMBIENTAL"));
            manager = new LeeArchivosManager();
            AltaProveedorManager proveedorManager = new AltaProveedorManager();
            // Se obtiene el caso
            Caso srchCase = new Caso();
            srchCase.setFolio(datosArchivo.getcFolio());
            c = CasoManager.select(conn, srchCase);
            // Validar que el contrato esté autorizado
            if (!manager.contratoAut(conn, datosArchivo.getcIdContratoDefinitivo())) {
                throw new Exception("El contrato debe de estar autorizado.");
            }
            if (c == null) {
                throw new Exception("No se encontro el caso.");
            }
            String ejercicioFiscal = com.syc.adquisiciones.util.Util.obtieneEjercicioFiscalActivo(conn);
            // Si no tiene expediente se crea
            if (c.getIdGabinete() == -1) {
                c.getCasoDato("FOLIO").setValor(c.getFolio());
                c.getCasoDato("FECHA_DOCUMENTO").setValor(Util.getToday());
                c.getCasoDato("EJERCICIO_FISCAL").setValor(ejercicioFiscal);
                c.getCasoDato("OPERADOR").setValor(c.getCasoOperacion(0).getResponsable());
                c.setIdGabinete(cbl.creaExpediente(usuario.getLogin(), c));
            }
            if (datosArchivo.getnTipoTerminacionCont() == TERMINA_POR_RESCISION) {
                nameDocto = "Recision de Contrato";
            }
            // Copia archivo a un directorio temporal
            Util.copiaArchivo(new DataInputStream(datosArchivo.getArchivoStream()), datosArchivo.getcNombreArchivoDestino());
            // copia archivo al fortimax
            AdjuntaArchivoMasivoManager.adjuntaArchivo(conn, usuario.getLogin(), c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), "DoctosContrato", nameDocto, new File(datosArchivo.getcNombreArchivoDestino()));
            // Guardar los datos en mContratoTerminacionAnticipada
            if (manager.existTerminacionAnti(conn, datosArchivo.getcIdContratoDefinitivo())) {
                manager.updateTerminacionAnti(conn, datosArchivo, usuario.getLogin());
                // Se guarda bitacora
                com.syc.adquisiciones.util.Util.bitacoraMovimientos(datosArchivo.getcIdContratoDefinitivo(), "Actualiza Terminaci\\u00f3n Anticipada", usuario.getLogin(), conn);
            } else {
                manager.insertTerminacionAnti(conn, datosArchivo, usuario.getLogin());
                // Se guarda bitacora
                com.syc.adquisiciones.util.Util.bitacoraMovimientos(datosArchivo.getcIdContratoDefinitivo(), "Terminaci\\u00f3n Anticipada", usuario.getLogin(), conn);
                if (TERMINA_POR_RESCISION == datosArchivo.getnTipoTerminacionCont()) {
                    String rfcProveedor = proveedorManager.proveedoresAdjudicados(conn, datosArchivo.getcIdContratoDefinitivo());
                    proveedorManager.registraProveedorRescision(conn, rfcProveedor);
                    if (esSAIAlterno) {
                        saiConnection = Util.getSAIConnection(conn);
                        proveedorManager.registraProveedorRescision(saiConnection, rfcProveedor);
                    } else {
                        DatosProveedor dp = proveedorManager.getAltaProveedorObjByRFC(conn, rfcProveedor);
                        proveedorManager.saveBitacoraAltaProveedor(conn, dp.getcFolio(), "Se rescindio el contrato " + datosArchivo.getcIdContratoDefinitivo(), "S", "S", 0, 0, usuario.getLogin());
                    }
                }
            }
            resp.setMsg("Datos guardados.");
            resp.setResp(true);
            conn.commit();
            if (saiConnection != null)
                saiConnection.commit();
        } catch (Exception e) {
            resp.setMsg(e.getMessage().toString());
            resp.setResp(false);
            log.error(e.getMessage(), e);
            Util.rollback(saiConnection);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e2) {
                    resp.setMsg("Problemas en rollback: " + e2.getMessage().toString());
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
            }
        } finally {
            CloseObject.closeObject(saiConnection);
            if (conn != null) {
                conn.close();
            }
            conn = null;
            manager = null;
            c = null;
        }
        return resp;
    }

    public Respuesta readLayoutApartado(HttpServletRequest request, DatosArchivo datosArchivo, Usuario usuario) throws Exception {
        Respuesta resp = new Respuesta();
        Connection conn = null;
        Connection connApCont = null;
        LeeArchivosManager manager = null;
        boolean primeraLinea = true;
        BufferedReader br = null;
        ContableInterface conInt = null;
        AplicarContableReturn acr = null;
        String row = "";
        Map<?, ?> m = null;
        Caso c = null;
        Caso sc = null;
        JSONArray arrayObj = null;
        CasoBusinessLogic cbl = null;
        String msg = "";
        String token = "";
        List<String> lista = null;
        String folio = "";
        String folioDecrip = "";
        String folioAutSICOP = "";
        String folioAnterior = "";
        String fechaAut = "";
        try {
            conn = getConnection(datosArchivo.getJniName());
            manager = new LeeArchivosManager();
            // Leer archivo para obtener los folios
            br = new BufferedReader(new InputStreamReader(datosArchivo.getArchivoStream()));
            while ((row = br.readLine()) != null) {
                String[] arrayRow = row.split(",");
                if (primeraLinea) {
                    // Es el encabezado
                    primeraLinea = !primeraLinea;
                } else {
                    if (arrayRow[1].equalsIgnoreCase("RHQ") && "WF_SUFI_PROCURA_2".equalsIgnoreCase(arrayRow[53])) {
                        // obtener el folio de la integrada que viene en el
                        // layout
                        folio = arrayRow[39];
                        if (-1 == arrayRow[9].indexOf("|") || folioAnterior.equalsIgnoreCase(folio)) {
                            continue;
                        }
                        folioDecrip = arrayRow[9].substring(0, (-1 == arrayRow[9].indexOf("|") ? 0 : arrayRow[9].indexOf("|")));
                        fechaAut = arrayRow[3];
                        folioAutSICOP = arrayRow[10];
                        // validar que si haya un folio
                        if (null != folio && !"".equalsIgnoreCase(folio) && folio.equalsIgnoreCase(folioDecrip)) {
                            // Se obtiene datos de SAI del folio de la integrada
                            folioAnterior = folio;
                            lista = manager.obtienDatosLayout(conn, folio);
                            // Valida que exista en SAI
                            if (null != lista) {
                                resp.setResp(true);
                                try {
                                    // Connection para la aplicación contable
                                    // por integrada
                                    connApCont = getConnection(datosArchivo.getJniName());
                                    // Obtener las requis integradas con los
                                    // datos del caso
                                    // lista.get(
                                    arrayObj = manager.obtieneRequisIntegradas(connApCont, Integer.parseInt(lista.get(0)));
                                    // 0)
                                    // es
                                    // nIdIntegraRequi
                                    for (int i = 0; i < arrayObj.length(); i++) {
                                        // Obtener el caso
                                        // arrayObj.getJSONObject(0).getString("cIdRFC")
                                        sc = new Caso();
                                        sc.setFolio(arrayObj.getJSONObject(i).getString("cFolioCASO"));
                                        c = CasoManager.select(connApCont, sc);
                                        if (null != c) {
                                            // Inicia aplicacion contable
                                            conInt = new AplicacionContable();
                                            log.debug("Object: " + String.valueOf("Inicia aplicacion presupuestal " + new Timestamp(System.currentTimeMillis())));
                                            m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                                            acr = conInt.aplicarContableNuevo(connApCont, c, "", "", "", 0, "", m, datosArchivo.getcPrefixPath(), usuario.getLogin(), "");
                                            if (acr.isSuccess()) {
                                                // Actualiza estatus de la
                                                // requisición
                                                manager.updateStatusRequisicion(connApCont, arrayObj.getJSONObject(i).getString("cIdSolicitud"), arrayObj.getJSONObject(i).getString("cEjercicio"));
                                                // Actualizar estatus de layout
                                                manager.updateStatusLayout(connApCont, fechaAut, folioAutSICOP, Integer.parseInt(lista.get(2)));
                                                // Actualizar estatus y folio de
                                                // la integrada
                                                manager.updateStatusIntegrada(connApCont, folioAutSICOP, Integer.parseInt(lista.get(0)));
                                                // Generar bitacora
                                                manager.saveBitacoraRetonoLayout(connApCont, Integer.parseInt(lista.get(2)), Integer.parseInt(lista.get(0)), arrayObj.getJSONObject(i).getString("cIdSolicitud"), "APLICADO PRESUPUESTALMENTE", usuario.getLogin());
                                                // Avanza el caso
                                                cbl = new CasoBusinessLogic(datosArchivo.getJniName());
                                                cbl.avanzaCaso(c, usuario.getLogin(), "", new String[] { "CONSULTA_APARTADO" }, new String[] { "CONSULTA_APTD" }, m, datosArchivo.getcPrefixPath());
                                                log.debug("Object: " + String.valueOf(c.getCasoDato("APLICADO_CONT").getValor()));
                                                log.debug("Object: " + String.valueOf("Termina Aplicacion presupuestal " + new Timestamp(System.currentTimeMillis())));
                                                msg = msg + token + "La integrada " + folio + " con n\u00famero de requisici\u00f3n " + arrayObj.getJSONObject(i).getString("cIdSolicitud") + " \"APARTADO APLICADO PRESUPUESTALMENTE\".";
                                            } else {
                                                throw new SQLException("La integrada " + folio + " con n\u00famero de requisici\u00f3n " + arrayObj.getJSONObject(i).getString("cIdSolicitud") + " " + acr.getMessageList().get(0));
                                            }
                                        } else {
                                            // No Se pudo obtener el caso de la
                                            // requisición
                                            msg = msg + token + "La integrada " + folio + " con n\u00famero de requisici\u00f3n " + arrayObj.getJSONObject(i).getString("cIdSolicitud") + " \"ERROR AL OBTENER EL CASO\".";
                                            log.warn("Object: {}", "No se pudo obtener el caso para la requisición " + arrayObj.getJSONObject(i).getString("cIdSolicitud"));
                                        }
                                        sc = null;
                                        c = null;
                                        token = "\n";
                                    }
                                    connApCont.commit();
                                } catch (SQLException e) {
                                    log.error(e.getMessage(), e);
                                    msg = msg + token + e.getMessage();
                                    if (connApCont != null) {
                                        connApCont.rollback();
                                    }
                                } finally {
                                    if (connApCont != null) {
                                        connApCont.close();
                                    }
                                    connApCont = null;
                                    cbl = null;
                                }
                            } else {
                                log.info("Object: {}", "El folio de la integrada " + folio + ", no se encontro o el estus no es el correcto");
                            }
                        } else {
                            log.info("No se encontro folio en la descripción");
                        }
                    }
                }
            }
            if (!resp.isResp()) {
                msg = "No se encontraron folios para aplicar.";
            }
            conn.commit();
        } catch (Exception e) {
            msg = e.getMessage().toString();
            if (conn != null) {
                conn.rollback();
            }
            log.error(e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (connApCont != null) {
                connApCont.close();
            }
            resp.setMsg(msg);
            connApCont = null;
            conn = null;
            lista = null;
            conInt = null;
            acr = null;
            sc = null;
            arrayObj = null;
        }
        return resp;
    }

    public String cargaContratosCAAS(HttpServletRequest request, DatosArchivo datosArchivo, Usuario usuario) throws Exception {
        Connection conn = null;
        LeeArchivosManager manager = null;
        XSSFWorkbook workbook = null;
        Row row = null;
        // XSSFRow rw = null;
        Cell cell = null;
        JSONObject jsonObj = null;
        Requisition requi = null;
        JSONObject jsonObjConsolidado = null;
        StringBuilder msg = null;
        StringBuilder msgRow = null;
        XSSFSheet secondSheet = null;
        Cell cellDat = null;
        String cCentroContableOriginal = usuario.getPropiedad("CCENTROCONTABLE").getValor();
        try {
            conn = getConnection(datosArchivo.getJniName());
            manager = new LeeArchivosManager();
            msg = new StringBuilder();
            // Leer archivo
            workbook = new XSSFWorkbook(datosArchivo.getArchivoStream());
            secondSheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = secondSheet.rowIterator();
            int initRow = 2, k = 0;
            int totalColumn = 32;
            while (rowIterator.hasNext()) {
                k++;
                row = rowIterator.next();
                if (k < initRow)
                    continue;
                // rw = (secondSheet.getRow(k) == null ?
                // secondSheet.createRow(k) : secondSheet.getRow(k));
                jsonObj = new JSONObject();
                for (int c = row.getFirstCellNum(); c < totalColumn; c++) {
                    cell = row.getCell(c);
                    if (cell == null) {
                        jsonObj.put("" + c, "");
                    } else {
                        manager.fillObject(cell, "" + c, jsonObj);
                    }
                }
                if ("".equalsIgnoreCase(jsonObj.getString("0")))
                    break;
                // Validar que la requi este aprobada y no este en un
                // consolidado
                requi = manager.obtieneRequi(conn, jsonObj.getString("0"));
                jsonObjConsolidado = manager.obtieneConsolidado(conn, jsonObj.getString("0"));
                msgRow = new StringBuilder();
                if (validateRequi(conn, manager, requi, jsonObj, msg, jsonObjConsolidado, msgRow)) {
                    // Generar el proceso de contratación
                    requi.setDescripcion(jsonObj.getString("1"));
                    requi.setIdUsuarioCreacion(usuario.getLogin());
                    com.syc.adquisiciones.util.Util.cambiaCentroContableUE(requi.getIdUnidadEjecutora(), requi.getIdEntidadContable(), usuario);
                    generateProcessContract(request, datosArchivo, manager, usuario, requi, jsonObj, msgRow);
                }
                // Escribir en el excel la observación
                cellDat = row.createCell(totalColumn);
                cellDat.setCellValue(msgRow.toString());
                msgRow.delete(0, msgRow.length());
                msgRow = null;
                row = null;
                jsonObj = null;
                requi = null;
                jsonObjConsolidado = null;
            }
            // Enviar archivo por correo con las observaciones de cada registro
            sendEmailLayoutCAAS(conn, workbook, usuario);
            conn.commit();
        } catch (Exception e) {
            Util.rollback(conn);
            throw new Exception(e);
        } finally {
            com.syc.adquisiciones.util.Util.cambiaCentroContableUE(usuario.getU_UR_Orig(), cCentroContableOriginal, usuario);
            if (conn != null) {
                conn.close();
            }
            if (msgRow != null) {
                msgRow.delete(1, msgRow.length());
            }
            if (workbook != null) {
                workbook.close();
            }
            workbook = null;
            msgRow = null;
            conn = null;
            manager = null;
            workbook = null;
            row = null;
            cell = null;
            jsonObj = null;
            cellDat = null;
            cCentroContableOriginal = null;
        }
        return msg.toString();
    }

    private void generateProcessContract(HttpServletRequest request, DatosArchivo datosArchivo, LeeArchivosManager manager, Usuario usuario, Requisition requi, JSONObject jsonObj, StringBuilder msgRow) {
        Connection connReg = null;
        Procedimiento proced = null;
        String[] cFundamentoLeg = null;
        String[] cCategoriaProcedimiento = null;
        String[] cAreaRequirente = null;
        String[] cAreaResponsable = null;
        String[] cCentroTrabajo = null;
        String[] consolidado = null;
        String[] contratoAbierto = null;
        String[] contratoPlurianual = null;
        String[] esMaestro = null;
        Contrato cont = null;
        DatosContratoPSP psp = null;
        try {
            // Generar una conección por registro
            connReg = getConnection(datosArchivo.getJniName());
            generateConsolidado(connReg, request, manager, usuario, requi, datosArchivo);
            // generar procedimiento
            proced = new Procedimiento();
            proced.setcIdConsolidado(requi.getcIdConsolidado());
            consolidado = requi.getcIdConsolidado().split("-");
            contratoAbierto = jsonObj.getString("13").split(" - ");
            contratoPlurianual = jsonObj.getString("5").split(" - ");
            proced.setnEsContratoAbierto(Integer.parseInt(contratoAbierto[0]));
            proced.setnEsPlurianual(Integer.parseInt(contratoPlurianual[0]));
            proced.setcIdUnidadEjecutora(requi.getIdUnidadEjecutora());
            proced.setcNoProcedCNET(jsonObj.getString("2"));
            proced.setmTotalMinimoHonorarios(jsonObj.getDouble("24"));
            proced.setmTotalMinimoGastosTraslado(jsonObj.getDouble("25"));
            proced.setmTotalMaximoHonorarios(jsonObj.getDouble("26"));
            proced.setmTotalMaximoGastosTraslado(jsonObj.getDouble("27"));
            proced.setmTotalPlurianualHonorarios(jsonObj.getDouble("28"));
            proced.setmTotalPlurianualGastosTraslado(jsonObj.getDouble("29"));
            proced.setmTotalPlurianual(jsonObj.getDouble("28") + jsonObj.getDouble("29"));
            cFundamentoLeg = jsonObj.getString("4").split(" - ");
            cCategoriaProcedimiento = jsonObj.getString("3").split(" - ");
            proced.setnIdCategoria(Integer.parseInt(cCategoriaProcedimiento[0]));
            proced.setnIdFundamentoLeg(Integer.parseInt(cFundamentoLeg[0]));
            proced.setFechasProcedimiento(getFechasProcedimiento(jsonObj));
            proced.setcIdRFC(jsonObj.getString("12"));
            proced.setcEjercicio(requi.getEjercicio());
            proced.setcIdTipoConsolidado(requi.getTipoConsolidado());
            proced.setcDescripcion(requi.getDescripcion());
            proced.setcUsuarioCrea(requi.getIdUsuarioCreacion());
            proced.setcIdEntidadContable(requi.getIdEntidadContable());
            proced.setnIdConsecutivoConsolidado(Integer.parseInt(consolidado[2]));
            proced.setnPorcentajeIVA(16);
            generateProcedimiento(connReg, proced);
            // Generar contrato
            cont = new Contrato();
            psp = new DatosContratoPSP();
            cont.setcIdProcedimiento(proced.getcIdProcedimiento());
            cont.setcIdRequi(requi.getIdSolicitud());
            cont.setcIdRFC(proced.getcIdRFC());
            cont.setnIdconsecutivoAdj(proced.getnIdconsecutivoAdj());
            cont.setcNoContratoCNET(jsonObj.getString("14"));
            cont.setcOficioDG(jsonObj.getString("30"));
            cont.setcFolioMASCP(jsonObj.getString("31"));
            cont.setnEsContratacionPSP(true);
            cont.setnCodContratoCNET(jsonObj.getString("16"));
            cont.setnCodExpedienteCNET(jsonObj.getString("15"));
            cont.setcMecanismosVigilancia(jsonObj.getString("17"));
            cont.setlExcentaGarantia(1);
            cont.setPrefixPath(datosArchivo.getcPrefixPath());
            cont.setJndiName(datosArchivo.getJniName());
            cAreaRequirente = jsonObj.getString("18").split(" - ");
            cAreaResponsable = jsonObj.getString("19").split(" - ");
            cCentroTrabajo = jsonObj.getString("20").split(" - ");
            psp.setcAreaRequirente(cAreaRequirente[0]);
            psp.setcAreaResponsable(cAreaResponsable[0]);
            //validar que el área responsable pertenesca al area requirente
            validaAreaRespAreaReq(connReg, manager, cAreaRequirente[0], cAreaResponsable[0]);
            psp.setcDenominacionProyecto(jsonObj.getString("22"));
            esMaestro = jsonObj.getString("23").split(" - ");
            psp.setlEsMaestro((Integer.parseInt(esMaestro[0]) == 1 ? true : false));
            psp.setmMontoMensual(jsonObj.getDouble("21"));
            psp.setnCentroTrabajo(Integer.parseInt(cCentroTrabajo[0]));
            cont.setDatPSP(psp);
            fillDatsContract(connReg, request, cont, usuario);
            connReg.commit();
            msgRow.append("Ok, Número de consolidado SAI = " + requi.getcIdConsolidado() + ", Número de procedimiento SAI = " + cont.getcIdProcedimiento() + " y Número de contrato SAI = " + cont.getcIdContratoDefinitivo());
        } catch (Exception e) {
            msgRow.append("Error, " + e.getMessage());
            log.error(e.getMessage(), e);
            Util.rollback(connReg);
        } finally {
            if (connReg != null) {
                try {
                    connReg.close();
                } catch (SQLException e) {
                    log.error(e.getMessage(), e);
                }
            }
            if (proced.getFechasProcedimiento() != null) {
                proced.getFechasProcedimiento().clear();
            }
            proced = null;
            cFundamentoLeg = null;
            cCategoriaProcedimiento = null;
            cont = null;
            cFundamentoLeg = null;
            cAreaRequirente = null;
            cAreaResponsable = null;
            cCentroTrabajo = null;
            consolidado = null;
            contratoAbierto = null;
            contratoPlurianual = null;
            esMaestro = null;
        }
    }

    private void validaAreaRespAreaReq(Connection connReg, LeeArchivosManager manager, String cAreaRequirente, String cAreaResponsable) throws Exception {
        if (!manager.areaRespPerteneceAreaRequirente(connReg, cAreaRequirente, cAreaResponsable)) {
            throw new Exception("El área responsable " + cAreaResponsable + " no pertence al área requirente " + cAreaRequirente);
        }
    }

    private void sendEmailLayoutCAAS(Connection conn, XSSFWorkbook workbook, Usuario usuario) throws Exception {
        String fileName = System.getProperty("java.io.tmpdir") + File.separatorChar + "observacionesLayoutCAAS" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        File fsalida = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        String body = null;
        String cEmailJefeAdq = "";
        String cEmailSubAdqCont = "";
        ProcessAgreementManager manager = null;
        String cEjercicioAct = "2024";
        ConfiguraAplicativoBusinessLogic cabl = null;
        try {
            cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
            boolean esAmbienteDesarrollo = "TRUE".equalsIgnoreCase(cabl.getSystemSetting("AMBIENTE_DESARROLLO"));
            boolean esAmbienteLocal = "S".equalsIgnoreCase(cabl.getSystemSetting("AMBIENTE_LOCAL"));
            if (!esAmbienteDesarrollo && !esAmbienteLocal) {
                cEjercicioAct = com.syc.adquisiciones.util.Util.obtieneEjercicioFiscalActivo(conn);
                manager = new ProcessAgreementManager();
                cEmailJefeAdq = manager.getEmailPurchasingDepartment(conn, cEjercicioAct);
                cEmailSubAdqCont = manager.getEmailSubGerenteAdquisiciones(conn, cEjercicioAct);
            }
            fsalida = new File(fileName);
            fos = new FileOutputStream(fsalida);
            bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos);
            bos.flush();
            body = "<B>Atenci\u00f3n.</b></br>" + "Se notifica que fue cargado el layout de contrataciones del CAAS en el sistema SAI.<br><br>" + "Por lo anterior es necesario que se revisen las observaciones de cada registro del layout. <br><br>" + "Gracias y reciban un cordial saludo.<br> ";
            AlarmaManager.procesaAlarmaAttachmentCNF(conn, "", null, null, "Observaciones layout CAAS", usuario.getU_email() + ";" + cEmailJefeAdq + ";" + cEmailSubAdqCont + ";", body, fsalida, true);
        } finally {
            if (fos != null) {
                fos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fsalida.exists()) {
                fsalida.deleteOnExit();
            }
            bos = null;
            fos = null;
            fsalida = null;
            fileName = null;
            body = null;
            cEmailJefeAdq = null;
            cEmailSubAdqCont = null;
            manager = null;
            cEjercicioAct = null;
            cabl = null;
        }
    }

    private boolean validateRequi(Connection conn, LeeArchivosManager manager, Requisition requi, JSONObject jsonObj, StringBuilder msg, JSONObject jsonObjConsolidado, StringBuilder msgRegistro) throws Exception {
        boolean resp = false;
        // Validar que exista
        if (requi == null) {
            msg.append("Error, el número de requisición " + jsonObj.getString("0") + " no existe. ");
            msgRegistro.append("Error, el número de requisición " + jsonObj.getString("0") + " no existe. ");
        } else {
            // Validar que esté aprobada
            if (RequisitionStatus.APPROVED == requi.getIdEstado()) {
                // Valiadar que no esté consolidada.
                if (jsonObjConsolidado != null) {
                    msg.append("Error, no fue posible generar el contrato para la requisición " + jsonObj.getString("0") + ", porque ya se encuentra en el consolidado " + jsonObjConsolidado.getString("cIdConsolidado"));
                    msgRegistro.append("Error, no fue posible generar el contrato para la requisición " + jsonObj.getString("0") + ", porque ya se encuentra en el consolidado " + jsonObjConsolidado.getString("cIdConsolidado"));
                } else {
                    // validar apartado
                    if (manager.apartadoSolicitud(conn, jsonObj.getString("0"))) {
                        resp = true;
                    } else {
                        msg.append("Error, el número de requisición " + jsonObj.getString("0") + " no tiene presupuesto apartado. ");
                        msgRegistro.append("Error, el número de requisición " + jsonObj.getString("0") + " no tiene presupuesto apartado. ");
                    }
                }
            } else {
                msg.append("Error, no fue posible generar el contrato para la requisición " + jsonObj.getString("0") + " porque no está aprobada. ");
                msgRegistro.append("Error, no fue posible generar el contrato para la requisición " + jsonObj.getString("0") + " porque no está aprobada. ");
            }
        }
        return resp;
    }

    private void generateConsolidado(Connection conn, HttpServletRequest request, LeeArchivosManager manager, Usuario usuario, Requisition requi, DatosArchivo datosArchivo) throws Exception {
        Caso caso = null;
        String folioCaso = "";
        int folio = 0;
        int indice = 0;
        String vcaNoCompromiso = "";
        ContratacionFormalizadaManager contManager = null;
        PrecomMaterialesEncabezado precom = null;
        ContableInterface conInt = null;
        AplicarContableReturn acr = null;
        String validaSaldo = "";
        String[] responsable = null;
        String[] nombre = null;
        try {
            contManager = new ContratacionFormalizadaManager();
            precom = new PrecomMaterialesEncabezado();
            // se crea el consolidado
            if (manager.generateConsolidado(conn, requi) != 0) {
                throw new Exception("No se logro insertar en mconsolidado ");
            }
            // Generar el caso del precompromiso materiales
            caso = com.syc.adquisiciones.util.Util.generaGuardaCaso(usuario.getU_UR(), (GestionInterface.IDTC_PRECOMMATERIALES + ""), "Aplicación de PreCompromiso Materiales", usuario, datosArchivo.getJniName(), requi.getEjercicio());
            folioCaso = caso.getFolio();
            indice = folioCaso.lastIndexOf('-') + 1;
            folio = Integer.parseInt(folioCaso.substring(indice));
            vcaNoCompromiso = com.syc.adquisiciones.util.Util.generaCaNoContrarecibo(usuario, requi.getEjercicio(), "PR");
            // insertar en tPrecomMaterialesEncabezado y
            // tPrecomMaterialesDetalle (tPreCompromisoEncabezado_materialesTmp,
            // tPreCompromisoDetalle_materialesTmp)
            precom.setCaNoPreCompromiso(vcaNoCompromiso);
            precom.setcCentroContable(usuario.getPropiedad("CCENTROCONTABLE").getValor());
            precom.setcEjercicioFiscal(requi.getEjercicio());
            precom.setcIdConsolidado(requi.getcIdConsolidado());
            precom.setcRamo(usuario.getU_Ramo());
            precom.setcTipoContrato("DI");
            precom.setcTipoPoliza("PR");
            precom.setcUnidadResponsable(requi.getIdUnidadEjecutora());
            precom.setnEnviadoSICOP(0);
            precom.setnFolioPrecomMateriales(folio);
            precom.setnStatusFinanciero(0);
            contManager.addPrecomMaterialesEnc(conn, precom);
            contManager.addPrecomMaterialesDet(conn, requi.getcIdConsolidado(), folio, usuario.getPropiedad("CCENTROCONTABLE").getValor());
            // Aplicación contable
            conInt = new AplicacionContable();
            Map<String, String> m = CasoDatoManager.readValuesCasoDato(request, caso.getCasoDato(), true);
            acr = conInt.aplicarContableNuevo(conn, caso, "", "", "", 0, "", m, datosArchivo.getcPrefixPath(), usuario.getLogin(), validaSaldo);
            if (!acr.isSuccess()) {
                throw new Exception("No se genera la aplicación contable del precompromiso con folios : " + folio);
            }
            // Actualizar el folio del consolidado
            contManager.updateFolioConsolidado(conn, folio, folioCaso, requi.getcIdConsolidado());
            // Registra vigencia del precompromiso
            contManager.registraVigenciaPrecomMateriales(conn, folioCaso, requi.getcIdConsolidado(), requi.getIdUnidadEjecutora(), requi.getEjercicio());
            // insertar en mPartidasPrecompromiso "sp_mPartidasPrecompromiso"
            contManager.registraPartidasPrecomMateriales(conn, requi.getcIdConsolidado());
            // Avanzar el caso a consulta
            // VENTANILLA_PRECOMPROMISO
            responsable = new String[] { "CONSULTA_PRECOMPROMISO" };
            // autoriza_precomp
            nombre = new String[] { "consulta_precomp" };
            com.syc.adquisiciones.util.Util.avanzaCaso(request, caso, usuario, datosArchivo.getcPrefixPath(), responsable, nombre, datosArchivo.getJniName());
            // Guardar en bitacora
            com.syc.adquisiciones.util.Util.bitacoraMovimientos(requi.getcIdConsolidado(), "Consolidado generado", usuario.getLogin(), conn);
        } finally {
            caso = null;
            folioCaso = null;
            contManager = null;
            precom = null;
            conInt = null;
            acr = null;
            responsable = null;
            nombre = null;
        }
    }

    private ArrayList<ProcedimientoFecha> getFechasProcedimiento(JSONObject jsonObj) throws JSONException {
        ProcedimientoFecha fecha = null;
        ArrayList<ProcedimientoFecha> fechasProced = null;
        try {
            fechasProced = new ArrayList<ProcedimientoFecha>();
            int k = 0;
            // ids de las fechas
            int[] idFechas = { 1, 19, 2, 12, 17, 18 };
            for (int i = 6; i < 12; i++) {
                fecha = new ProcedimientoFecha();
                fecha.setnIdFecha(idFechas[k]);
                fecha.setcFecha(jsonObj.getString("" + i));
                fechasProced.add(fecha);
                k++;
                fecha = null;
            }
        } finally {
            fecha = null;
        }
        return fechasProced;
    }

    private void generateProcedimiento(Connection conn, Procedimiento proced) throws Exception {
        ContratacionFormalizadaManager contManager = null;
        try {
            contManager = new ContratacionFormalizadaManager();
            // 1. Validar que el consolidado no este en un procedimiento y que
            // el proveedor esté dado de alta
            if (contManager.searchConsolidadoEnProcedimiento(conn, proced)) {
                throw new Exception("El consolidado : " + proced.getcIdConsolidado() + " ya se encuentra en el procedimiento " + proced.getcIdProcedimiento());
            }
            if (!contManager.existProveedor(conn, proced)) {
                throw new Exception("El proveedor no se encuentra dado de alta");
            }
            if (!contManager.getTipoProcedimiento(conn, proced)) {
                throw new Exception("No se obtuvo el tipo de procedimiento");
            }
            if (proced.getnEsContratoAbierto() == 1 && (proced.getmTotalMaximoGastosTraslado() + proced.getmTotalMaximoHonorarios()) <= (proced.getmTotalMinimoGastosTraslado() + proced.getmTotalMinimoHonorarios())) {
                throw new Exception("El monto mínimo debe de ser menor al monto máximo");
            }
            if ((proced.getmTotalMinimoGastosTraslado() + proced.getmTotalMinimoHonorarios()) <= 0.00) {
                throw new Exception("El monto mínimo debe de ser mayor a cero");
            }
            if ((proced.getmTotalMinimoGastosTraslado() + proced.getmTotalMinimoHonorarios()) <= 0.00 || (proced.getmTotalMaximoGastosTraslado() + proced.getmTotalMaximoHonorarios()) <= 0) {
                throw new Exception("El monto mínimo y máximo no pueden ser cero");
            }
            // 2. Se obtiene el consecutivo del procedimiento
            contManager.getConsecutivoProcedimiento(conn, proced);
            // 3. Crear procedimiento sp_mProcedimiento
            contManager.generaProcedimiento(conn, proced);
            // 4. Insertar fechas mProcedimientoFechas
            for (int i = 0; i < proced.getFechasProcedimiento().size(); i++) {
                contManager.addFechasProcedimiento(conn, proced.getcIdProcedimiento(), proced.getFechasProcedimiento().get(i).getnIdFecha(), proced.getFechasProcedimiento().get(i).getcFecha());
            }
            // 5. Agregar proveedor sp_mProcedimientoAdjudicacion
            contManager.addProcedimientoAdjudicacion(conn, proced);
            // 6. Generar partidas
            contManager.addProcedimientoAdjudicacionPartidas(conn, proced);
            // Validar montos de las partidas
            int validaMont = contManager.validateMontoRequiMontoMinimoMaximo(conn, proced);
            if (validaMont == 1) {
                throw new Exception("El monto de la requisición no puede ser mayor al monto máximo");
            }
            if (validaMont == 2) {
                throw new Exception("El monto de la requisición no puede ser mayor al monto mínimo");
            }
            if (validaMont == 3) {
                // Se compromete el máximo
                proced.setnComprometeMaximo(1);
                contManager.updateComprometeMaximo(conn, proced);
            }
            // 7. Adjudicar
            generaContrato(conn, contManager, proced);
            contManager.saldoPrecomMateriales(conn, proced);
        } finally {
            contManager = null;
        }
    }

    private void generaContrato(Connection conn, ContratacionFormalizadaManager contManager, Procedimiento proced) throws Exception {
        String cTipoContrato = "CV";
        try {
            // getConsecutivContract
            if (contManager.getnConsecutivoContrato(conn, proced, cTipoContrato) == -1) {
                throw new Exception("No se obtuvo el consecutivo del contrato.");
            }
            proced.setcIdTipoContrato(cTipoContrato);
            generaIdContratoDef(proced);
            contManager.execQuery(conn, " ALTER TABLE mContrato NOCHECK CONSTRAINT FK_mContrato_mContratoDefinitivo ");
            contManager.execQuery(conn, " ALTER TABLE mContratoDefinitivo NOCHECK CONSTRAINT FK_mContratoDefinitivo_mContrato ");
            // INSERT INTO mContratoDefinitivo
            contManager.addContratoDefinitivo(conn, proced);
            // INSERT INTO mContrato
            contManager.addNewContrato(conn, proced);
            contManager.execQuery(conn, "ALTER TABLE mContrato CHECK CONSTRAINT FK_mContrato_mContratoDefinitivo");
            contManager.execQuery(conn, "ALTER TABLE mContratoDefinitivo CHECK CONSTRAINT FK_mContratoDefinitivo_mContrato");
            // INSERT INTO mContratoPartidas
            contManager.addPartidasContrato(conn, proced);
            // INSERT INTO mContratoProrrateo
            contManager.addContratoProrrateo(conn, proced);
            // pa_mContratoComplementaProrrateo
            contManager.complementaProrrateo(conn, proced.getcEjercicio(), proced.getcIdTipoContrato(), proced.getcIdUnidadEjecutora(), proced.getnConsecutivoContrato());
        } finally {
            cTipoContrato = null;
        }
    }

    private void generaIdContratoDef(Procedimiento proced) throws Exception {
        StringBuilder cIdContratoDef = new StringBuilder();
        if (proced.getnEsPlurianual() == 1) {
            cIdContratoDef.append("PLU-");
        }
        cIdContratoDef.append(proced.getcIdTipoContrato() + "-");
        cIdContratoDef.append(proced.getcIdUnidadEjecutora() + "-");
        cIdContratoDef.append(proced.getnConsecutivoContrato() + "/" + proced.getcEjercicio());
        proced.setcIdContrato(proced.getcIdTipoContrato() + "-" + proced.getcIdUnidadEjecutora() + "-" + proced.getnConsecutivoContrato());
        proced.setcIdContratoDefinitivo(cIdContratoDef.toString());
    }

    private void fillDatsContract(Connection conn, HttpServletRequest request, Contrato cont, Usuario usuario) throws Exception {
        ContratacionFormalizadaManager manager = null;
        Caso c = null;
        Caso casoPrecom = null;
        String folioCaso = null;
        String vcaNoCompromiso = null;
        AplicarContableReturn acr = null;
        ContableInterface conInt = null;
        String[] param = new String[6];
        Map<String, String> m = null;
        try {
            manager = new ContratacionFormalizadaManager();
            conInt = new AplicacionContable();
            if (!manager.getContrato(conn, cont)) {
                throw new Exception("No se logro generar el contrato.");
            }
            cont.getDatPSP().setcIdcontratoDefinitivo(cont.getcIdContratoDefinitivo());
            // Actualiza caratula
            manager.updateContrato(conn, cont);
            if (manager.existeDocumentacionHiperv(conn, cont.getcIdContratoDefinitivo())) {
                manager.updateDocumentacionContrato(conn, cont);
            } else {
                manager.addDocumentacionHiperv(conn, cont);
            }
            manager.addDatosPSP(conn, cont.getDatPSP());
            // Agregar retenciones
            manager.agregaRetencionISR(conn, cont.getcIdContratoDefinitivo());
            manager.agregaRetencionIVAHonorarios(conn, cont.getcIdContratoDefinitivo());
            if (cont.getDatPSP().getnCentroTrabajo() == GestionInterface.CENTRO_TRABAJO_NAYARIT) {
                // Cedular
                // para
                // nayarit
                // 1.5
                manager.agregaRetencion(conn, cont.getcIdContratoDefinitivo(), GestionInterface.TIPO_RETENCION_CEDULAR_NAYARIT);
            } else if (cont.getDatPSP().getnCentroTrabajo() == GestionInterface.CENTRO_TRABAJO_GUANAJUATO) {
                // cedular
                // para
                // Guanajuato
                // 2.5
                manager.agregaRetencion(conn, cont.getcIdContratoDefinitivo(), GestionInterface.TIPO_RETENCION_CEDULAR_GUANAJUATO);
            }
            // Agregar EP en tContratoEP_TMP
            manager.addEP_TEMP_Apartado(conn, cont);
            // Aprueba contrato
            c = com.syc.adquisiciones.util.Util.generaGuardaCaso(cont.getcIdUnidadEjecutora(), (GestionInterface.IDTC_CONTRATO_DIVERSO + ""), "Contrato Diverso", usuario, cont.getJndiName(), cont.getcEjercicio());
            folioCaso = c.getFolio();
            int indice = folioCaso.lastIndexOf('-') + 1;
            com.syc.adquisiciones.util.Util.avanzaCaso(request, c, usuario, cont.getPrefixPath(), new String[] { "CONSULTA_CONTRATODIVERSO" }, new String[] { "consulta_contrato" }, cont.getJndiName());
            cont.setcFOLIO(folioCaso);
            cont.setnConsecutivoCDIV(Integer.parseInt(folioCaso.substring(indice)));
            switch(manager.apruebaContrato(conn, cont)) {
                case 1:
                    throw new Exception("El estatus del contrato no es el correcto para aprobar el contrato, revisar el store pa_apruebaContrato.");
                case 2:
                    throw new Exception("El proveedor no es valido, favor de actualizar la información o darlo de alta, revisar el store pa_apruebaContrato.");
                case 3:
                    throw new Exception("No cuenta con estructuras presupuestales, revisar el store pa_apruebaContrato.");
                case 4:
                    throw new Exception("No se logro insertar en pcontratodiverso, revisar el store pa_apruebaContrato");
                case 5:
                    throw new Exception("No se logro insertar en tContratoEP, revisar el store pa_apruebaContrato");
                case 6:
                    throw new Exception("No se logro actualizar el estatus del contrato, revisar el store pa_apruebaContrato");
            }
            // Precompromete
            casoPrecom = com.syc.adquisiciones.util.Util.generaGuardaCaso(cont.getcIdUnidadEjecutora(), (GestionInterface.IDTC_PRECOMPROMISO + ""), "Aplicación de PreCompromiso", usuario, cont.getJndiName(), cont.getcEjercicio());
            vcaNoCompromiso = com.syc.adquisiciones.util.Util.generaCaNoContrarecibo(usuario, cont.getcEjercicio(), "CO");
            param[0] = cont.getcIdContratoDefinitivo();
            param[1] = "N";
            param[2] = cont.getcEjercicio();
            param[3] = "REGISTRO DEL CONTRATO FOLIO " + casoPrecom.getFolio().substring(casoPrecom.getFolio().lastIndexOf("-") + 1);
            param[4] = casoPrecom.getFolio().substring(casoPrecom.getFolio().lastIndexOf("-") + 1);
            param[5] = casoPrecom.getFolio();
            cont.setnConsecutivoPRECOMP(Integer.parseInt(param[4]));
            cont.setC_FOLIO_PRE(casoPrecom.getFolio());
            if ((com.syc.adquisiciones.util.Util.creaEncPreCompromiso(usuario, conn, param, vcaNoCompromiso)) != 0) {
                throw new Exception("Error, al crear el encabezado del precompromiso " + folioCaso);
            }
            manager.addPrecompromisoEncTMP(conn, Integer.parseInt(param[4]));
            cont.setcEvento("COMP_MAT");
            manager.addPrecompromisoDet(conn, cont);
            manager.addPrecompromisoDetTMP(conn, cont.getnConsecutivoPRECOMP());
            // Aplicacion contable
            m = CasoDatoManager.readValuesCasoDato(request, casoPrecom.getCasoDato(), true);
            acr = conInt.aplicarContableNuevo(conn, casoPrecom, "", "", "", 0, "", m, cont.getPrefixPath(), usuario.getLogin(), "");
            if (!acr.isSuccess()) {
                throw new Exception("Error en la aplicación contable del caso precompromiso " + casoPrecom.getFolio());
            }
            cont.setnIdEstado(ContractStatus.BUDGET);
            manager.updateEstateContrato(conn, cont);
            manager.updateFolioPrecomContrato(conn, cont);
            manager.addRelacionPrecomCompromiso(conn, cont.getcIdContratoDefinitivo(), cont.getC_FOLIO_PRE(), cont.getnConsecutivoPRECOMP());
            manager.addRelacionPrecom(conn, cont.getcIdContratoDefinitivo(), cont.getC_FOLIO_PRE(), cont.getnConsecutivoPRECOMP(), cont.getcEjercicio(), usuario.getPropiedad("CCENTROCONTABLE").getValor(), cont.getcIdUnidadEjecutora());
            com.syc.adquisiciones.util.Util.avanzaCaso(request, casoPrecom, usuario, cont.getPrefixPath(), new String[] { "VENTANILLA_PRECOMPROMISO" }, new String[] { "autoriza_precomp" }, cont.getJndiName());
            // Bitacora
            com.syc.adquisiciones.util.Util.bitacoraMovimientos(cont.getcIdContratoDefinitivo(), "Contrato precomprometido", usuario.getLogin(), conn);
        } finally {
            manager = null;
            c = null;
            folioCaso = null;
            casoPrecom = null;
            vcaNoCompromiso = null;
            param = null;
            acr = null;
            m = null;
            conInt = null;
        }
    }

    public Respuesta procesaLayoutPresupuestoRequi(DatosArchivo datosArchivo, Usuario usuario, String jniName) throws Exception {
        Respuesta resp = new Respuesta();
        Connection conn = null;
        LeeArchivosManager manager = null;
        boolean error = true;
        try {
            conn = getConnection(jniName);
            manager = new LeeArchivosManager();
            // Validar que la requi esté en estatus de captura
            manager.validaEstatusRequi(conn, datosArchivo.getcIdsolicitud());
            // Leer archivo
            manager.readLayoutPresupuestoRequi(conn, datosArchivo);
            // Validar unidad y la partida de la EP que sea igual a la de la
            // requi
            manager.validaUEAndPArtidaRequi(conn, datosArchivo.getcIdsolicitud());
            // Validar presupuesto calendarizado total de la linea
            manager.validaPresupuestoCalendarizadoRequi(conn, datosArchivo.getcIdsolicitud());
            // Validar presupuesto disponible
            manager.validaPresupuestoDisponibleRequi(conn, datosArchivo.getcIdsolicitud(), GestionInterface.DISPONIBLE);
            //elimina los datos de la tabla mSolicitudLineasApartado
            manager.deletePresupuestoRequi(conn, datosArchivo.getcIdsolicitud());
            // guardar info
            manager.insertPresupuestoRequi(conn, datosArchivo.getcIdsolicitud());
            // Eliminar los datos de la tabla mLayoutPresupuestoRequi
            manager.deleteLayoutRequi(conn, datosArchivo.getcIdsolicitud());
            error = false;
            conn.commit();
            resp.setMsg("Layout cargado correctamente.");
        } catch (Exception e) {
            log.info("Error en la carga de layout de presupuesto para requisiciones.");
            if (conn != null && error) {
                conn.rollback();
            }
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            manager = null;
            conn = null;
        }
        return resp;
    }
}
