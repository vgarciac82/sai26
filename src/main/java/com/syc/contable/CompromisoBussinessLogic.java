package com.syc.contable;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.axtel.egresos.compromiso.CalendarioDetalle;
import com.axtel.egresos.compromiso.CompromisoDTO;
import com.syc.contable.core.CompromisoManager;
import com.syc.contable.core.PagosDirectosManager;
import com.syc.contable.core.PrecompromisoFinanciero;
import com.syc.contable.core.RelacionGastosManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

public class CompromisoBussinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(CompromisoBussinessLogic.class);

    private String jniName = "";

    private String folioGenerator = GestionInterface.FOLIO_GENERATOR;

    public CompromisoBussinessLogic(String jniName) {
        super.init(jniName);
        this.jniName = jniName;
    }

    public ArrayList<String> buscaCompromisos(String lista_caNoCompromiso, String lista_cIdContrato, boolean esReimpresion) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            // lista_cIdContrato
            arrListaComp = CompromisoManager.BuscaCompromisos(conn, lista_caNoCompromiso, esReimpresion, 0);
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                throw new GestionException(e.getMessage());
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return arrListaComp;
    }

    public boolean estatusCompromiso(String lista_caNoCompromiso) throws FileNotFoundException, IOException, SQLException, GestionException {
        boolean cargandoArchivo = false;
        int renglonesActualizados = 0;
        Connection conn = null;
        try {
            conn = getConnection();
            renglonesActualizados = CompromisoManager.updateHeaderCompromisos(conn, lista_caNoCompromiso);
            if (renglonesActualizados > 0) {
                cargandoArchivo = true;
                conn.commit();
            }
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                throw new GestionException(e.getMessage());
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return cargandoArchivo;
    }

    public boolean insertaArchivoFiltrado(StringBuffer archivoFiltrado) throws Exception {
        boolean cargandoArchivo = false;
        Connection conn = null;
        try {
            conn = getConnection();
            cargandoArchivo = CompromisoManager.insertaFiltrados(conn, archivoFiltrado);
            // conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas con rollback:" + e2);
                }
            }
            throw e;
        } finally {
            // CloseObject.closeObject(conn);
        }
        return cargandoArchivo;
    }

    public String fAplicacionCancelados(String compromisoCancelado) throws FileNotFoundException, IOException, SQLException, GestionException {
        String fAplicacion = "";
        Connection conn = null;
        try {
            conn = getConnection();
            fAplicacion = CompromisoManager.buscaFechaAplicacionCancelados(conn, compromisoCancelado);
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                throw new GestionException(e.getMessage());
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return fAplicacion;
    }

    public boolean estatusCancelado(String caNoCompromiso) throws FileNotFoundException, IOException, SQLException, GestionException {
        boolean cargandoArchivo = false;
        Connection conn = null;
        try {
            conn = getConnection();
            cargandoArchivo = CompromisoManager.updateHeaderCompromisosRealimentacion(conn, 0, caNoCompromiso, "");
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                throw new GestionException(e.getMessage());
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return cargandoArchivo;
    }

    public int cancelacionMensual(int Mes) throws Exception {
        Connection conn = null;
        int totalCancelados = 0;
        try {
            conn = getConnection();
            // if (conn.getAutoCommit())
            // conn.setAutoCommit(false);
            CompromisoManager.ejecutaCancelacioMensual(conn, Mes);
            List<Integer> folios = CompromisoManager.selectCompromisosCancelar(conn);
            for (Iterator<Integer> i = folios.iterator(); i.hasNext(); ) {
                Integer nFolioCompromiso = i.next();
                log.info("Object: {}", "Cancelando folio: " + nFolioCompromiso);
                AccountingEngine motorContable = new AccountingEngine();
                motorContable.makeAccountingApplication(conn, "PRECOMFINANCIERO", nFolioCompromiso.toString(), "tPrecomFinancieroEncabezado", "tPrecomFinancieroDetalle", "nFolioPrecomFinanciero");
                CompromisoManager.borraCompromisosCancelarLista(conn, nFolioCompromiso);
                totalCancelados++;
            }
            conn.commit();
            return totalCancelados;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Error aplicando rollback. Causa: " + e2, e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public int barredoraNoMil(Usuario u, FolioGeneratorInterface fg) throws Exception {
        Connection conn = null;
        try {
            long start = System.currentTimeMillis();
            log.info("Iniciando proceso de limpieza anual de compromisos.");
            conn = getConnection();
            int nFolioCompromiso = CompromisoManager.barredoraCompromisoAnual(conn, true, u, fg);
            AccountingEngine motorContable = new AccountingEngine();
            motorContable.setValidaInsuficienciaDeSaldo(true);
            motorContable.makeAccountingApplication(conn, "COMPROMISO", String.valueOf(nFolioCompromiso), "tcompromisoencabezado", "tcompromisodetalle", "nFolioCompromiso");
            conn.commit();
            long end = System.currentTimeMillis();
            log.info("Object: {}", String.format("Se termino el proceso de limpieza anual de compromisos exitosamente en %2d segundos", (end - start) / 1000));
            return nFolioCompromiso;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.error("Problemas realizando rollback: " + e2, e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public String insertaCompromisoRG(Usuario usuario, String foliosIntegracion, String cuentaBancaria, String fechaIntegracion, String leyenda, String folioGenerator) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            String centroContableUser = usuario.getPropiedad("CCENTROCONTABLE").getValor();
            FolioGeneratorInterface fg = Util.getFolioGenerator(folioGenerator);
            Caso casoCompromiso = CompromisoManager.generaCasoCompromiso(conn, usuario, fg, usuario.getLogin());
            int folioCompromiso = Integer.parseInt(casoCompromiso.getFolio().substring(casoCompromiso.getFolio().lastIndexOf('-') + 1));
            CFSequenceManager sequence = CFSequenceManager.getInstance();
            int seqFolio = 100000 + sequence.nextVal(conn, "CO-" + centroContableUser);
            String contrarecibo = centroContableUser + "CO" + EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal() + String.valueOf(seqFolio);
            String idIntegracion = RelacionGastosManager.generaFolioIntegracion(conn, cuentaBancaria);
            CompromisoManager.insertaCompromisoRGEncabezado(conn, folioCompromiso, idIntegracion, contrarecibo, usuario, "RELACIONGASTOS");
            CompromisoManager.insertaCompromisoRGDetalle(conn, folioCompromiso, foliosIntegracion);
            int nFolioPDNominaCompromiso = CompromisoManager.insertaCompromisoRGRelacionEncabezado(conn, folioCompromiso, contrarecibo, cuentaBancaria, fechaIntegracion, leyenda, usuario);
            CompromisoManager.insertaCompromisoRGRelacionDetalle(conn, nFolioPDNominaCompromiso, foliosIntegracion);
            CompromisoManager.actualizaEnvioSICOPRG(conn, foliosIntegracion);
            avanzaCasoCompromiso(conn, casoCompromiso, usuario);
            RelacionGastosManager.insertaTablasCompromisoRG(conn, usuario, contrarecibo, folioGenerator);
            /*
																											 * Agregó
																											 * Janise
																											 */
            conn.commit();
            return contrarecibo;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public String obtenerContrarreciboPD(String[] foliosPagos) throws Exception {
        Connection conn = null;
        String caNoContrarrecibo = "";
        try {
            conn = getConnection();
            caNoContrarrecibo = CompromisoManager.obtenerCxpDirecto(conn, foliosPagos);
            conn.commit();
            return caNoContrarrecibo;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public String insertaCompromisoPagoDirecto(Usuario usuario, String[] foliosPagos, String cuentaBancaria, String fechaIntegracion, String leyenda, String folioGenerator) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            String centroContableUser = usuario.getPropiedad("CCENTROCONTABLE").getValor();
            StringBuilder caNoContrarrecibo = new StringBuilder();
            for (String folio : foliosPagos) {
                FolioGeneratorInterface fg = Util.getFolioGenerator(folioGenerator);
                Caso casoCompromiso = CompromisoManager.generaCasoCompromiso(conn, usuario, fg, usuario.getLogin());
                int folioCompromiso = Integer.parseInt(casoCompromiso.getFolio().substring(casoCompromiso.getFolio().lastIndexOf('-') + 1));
                CFSequenceManager sequence = CFSequenceManager.getInstance();
                int seqFolio = 100000 + sequence.nextVal("CO-" + centroContableUser);
                String contrarecibo = centroContableUser + "CO" + EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal() + String.valueOf(seqFolio);
                String cxp = CompromisoManager.consultaContrarreciboPD(conn, Integer.parseInt(folio));
                CompromisoManager.insertaCompromisoRGEncabezado(conn, folioCompromiso, cxp, contrarecibo, usuario, "PAGODIRECTO");
                CompromisoManager.insertaCompromisoRGDetalle(conn, folioCompromiso, folio);
                CompromisoManager.insertaCompromisoPagoDirecto(conn, folioCompromiso, cxp, cuentaBancaria, fechaIntegracion, leyenda, usuario, folio, contrarecibo);
                // avanzaCasoCompromiso(casoCompromiso,usuario);
                if (caNoContrarrecibo.length() > 0) {
                    // Añadimos una coma entre
                    caNoContrarrecibo.append(",");
                    // los valores
                }
                caNoContrarrecibo.append(contrarecibo);
            }
            // CAMBIAR LOS PAGOS DIRECTOS CON NENVIADOSICOP = 1
            PagosDirectosManager.updateHeaderCompromisos(conn, Util.join(foliosPagos, ','));
            conn.commit();
            return caNoContrarrecibo.toString();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void avanzaCasoCompromiso(Connection conn, Caso casoCompromiso, Usuario usuario) throws Exception {
        CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
        Map<String, String> data = new HashMap<String, String>();
        data.put("FOLIO", casoCompromiso.getCasoDato("FOLIO").getValor());
        data.put("OPERADOR", casoCompromiso.getCasoDato("OPERADOR").getValor());
        data.put("FECHA_DOCUMENTO", casoCompromiso.getCasoDato("FECHA_DOCUMENTO").getValor());
        data.put("EJERCICIO_FISCAL", casoCompromiso.getCasoDato("EJERCICIO_FISCAL").getValor());
        casoTx.avanzaCaso(conn, casoCompromiso, usuario.getLogin(), "", new String[] { "CONSULTA_COMPROMISO" }, new String[] { "consulta_compromiso" }, data, "");
    }

    public void descargaLayout(HttpServletResponse response, ArrayList<String> cvsDataSQL) throws Exception {
        DateFormat fecha = new SimpleDateFormat("yyyyMMddhhmmsss");
        String sufijo = fecha.format(new Date(System.currentTimeMillis()));
        File filename = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "Compromisos" + sufijo + ".csv");
        /* Guarda el pago */
        BufferedWriter out = new BufferedWriter(new FileWriter(filename));
        StringBuffer archivoPago = new StringBuffer();
        for (int i = 0; i < cvsDataSQL.size(); i++) {
            archivoPago.append(cvsDataSQL.get(i));
        }
        String outTextPago = archivoPago.toString();
        out.write(outTextPago.toPath());
        out.flush();
        out.close();
        /* fin de guarda pago */
        ServletOutputStream outS = null;
        ByteArrayInputStream byteArrayInputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        response.setContentType("text/csv");
        String disposition = "attachment; fileName=" + filename.getName();
        response.setHeader("Content-Disposition", disposition);
        outS = response.getOutputStream();
        byte[] blobData = outTextPago.getBytes();
        byteArrayInputStream = new ByteArrayInputStream(blobData);
        bufferedOutputStream = new BufferedOutputStream(outS);
        int length = blobData.length;
        response.setContentLength(length);
        byte[] buff = new byte[(1024 * 1024) * 2];
        int bytesRead;
        while (-1 != (bytesRead = byteArrayInputStream.read(buff, 0, buff.length))) {
            bufferedOutputStream.write(buff, 0, bytesRead);
        }
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
        outS.flush();
        outS.close();
        byteArrayInputStream.close();
        filename.delete();
    }

    public ArrayList<String> buscaCompromisos(String compromisosQuery, String contratosQuery) throws Exception {
        return buscaCompromisos(compromisosQuery, contratosQuery, false);
    }

    public String listaRGEnCompromiso(String caNoCompromiso) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            return CompromisoManager.listaRGEnCompromiso(conn, caNoCompromiso);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public List<String> procesaLayoutSuficiencia(File nombreDestino) throws Exception {
        List<String> resultado = null;
        Connection conn = null;
        InputStream is = new FileInputStream(nombreDestino);
        conn = getConnection();
        try {
            CompromisoManager.borraTablaSuficiencia(conn);
            List<String[]> renglonesArchivo = leerArchivoSuficiencia(is);
            conn.setAutoCommit(false);
            CompromisoManager.insertarDatosSuficiencia(conn, renglonesArchivo);
            CompromisoManager.actualizaFolioSuficiencia(conn);
            CompromisoManager.actualizaFolioSuficienciaIntegrada(conn);
            CompromisoManager.aplicaDecrementosSuficiencia(conn);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
            if (resultado == null)
                resultado = new ArrayList<String>();
            resultado.add(e.toString());
            log.error(e.getMessage(), e);
        } finally {
            CloseObject.closeObject(conn);
        }
        return resultado;
    }

    public List<String> procesaLayoutAplicarCompromisos(File nombreDestino) throws Exception {
        List<String> resultado = null;
        Connection conn = null;
        InputStream is = new FileInputStream(nombreDestino);
        conn = getConnection();
        try {
            CompromisoManager.borraTablaAnalisis(conn);
            List<String[]> renglonesArchivo = leerArchivoAnalisisCompromisos(is);
            conn.setAutoCommit(false);
            CompromisoManager.insertarCompromisos(conn, renglonesArchivo);
            CompromisoManager.aplicaCompromisos(conn, "mImporte > 0");
            CompromisoManager.aplicaCompromisosIntegrados(conn);
            CompromisoManager.actualizaFolioCompromiso(conn);
            CompromisoManager.actualizaFolioCompromisoIntegrada(conn);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
            if (resultado == null)
                resultado = new ArrayList<String>();
            resultado.add(e.toString());
            log.error(e.getMessage(), e);
        } finally {
            CloseObject.closeObject(conn);
        }
        return resultado;
    }

    public void actualizaFolios() throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            CompromisoManager.actualizaCanoCompromiso(conn);
            conn.commit();
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public List<String> procesaLayoutCompromiso(File nombreDestino) throws Exception {
        List<String> resultado = null;
        Connection conn = null;
        InputStream is = new FileInputStream(nombreDestino);
        List<String[]> renglonesArchivo = leerArchivo(is);
        Date fAppTmp = null;
        Date fExpTmp = null;
        java.sql.Date fAplicacion = null;
        java.sql.Date fExpedicion = null;
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formato2 = new SimpleDateFormat("dd/MM/yyyy");
        boolean existe = false;
        int nRenglon = 1;
        BigDecimal diferencia = new BigDecimal("0");
        // Elimina Compromiso_sicop
        borrarTabla();
        for (String[] renglon : renglonesArchivo) {
            try {
                conn = getConnection();
                log.debug("Object: {}", " Operando el renglon " + nRenglon + " : " + renglon[21]);
                int columna = 0;
                String fAp = renglon[23];
                String fExp = renglon[6];
                BigDecimal total = new BigDecimal(renglon[7]);
                String estatus = renglon[22];
                String envioSicop = "APLICADO".equals(renglon[22]) ? "2" : "3";
                Integer nEnviadoSICOP = Integer.parseInt(envioSicop);
                String scaNoCompromiso = renglon[20];
                String caNoCompromiso = "";
                if (scaNoCompromiso.length() > 14)
                    caNoCompromiso = scaNoCompromiso.substring(0, 14);
                else
                    caNoCompromiso = scaNoCompromiso;
                String nDocumento = renglon[25];
                try {
                    fExpTmp = formato2.parse(fExp);
                } catch (ParseException pe) {
                    fExpTmp = formato.parse(fExp);
                }
                fExpedicion = new java.sql.Date(fExpTmp.getTime());
                try {
                    fAppTmp = formato2.parse(fAp);
                } catch (ParseException pe) {
                    fAppTmp = formato.parse(fAp);
                }
                fAplicacion = new java.sql.Date(fAppTmp.getTime());
                CompromisoManager.insertArchivoCompleto(conn, renglon[columna++], renglon[columna++], renglon[columna++], renglon[columna++], renglon[columna++], renglon[columna++], renglon[columna++], total, renglon[11], renglon[12], renglon[15], renglon[16], renglon[17], renglon[18], renglon[19], caNoCompromiso, renglon[21], estatus, fAplicacion, renglon[24], renglon[25], renglon[26]);
                existe = CompromisoManager.existeCompromiso(conn, caNoCompromiso);
                if ("APLICADO".equals(estatus) && existe) {
                    try {
                        // Insertando en tabla
                        CompromisoManager.insertRegisterLayout(conn, renglon[0], renglon[1], renglon[2], renglon[3], renglon[4], renglon[5], fExpedicion, total, renglon[8], renglon[9], renglon[10], renglon[11], renglon[12], renglon[13], renglon[14], renglon[15], renglon[16], renglon[17], renglon[18], renglon[19], caNoCompromiso, renglon[21], estatus, fAplicacion, renglon[24], nDocumento, renglon[26]);
                        conn.commit();
                    } catch (Exception e2) {
                        String errorMsg = "Error mientras se guardaba el compromiso " + renglon[20] + ". Causa: " + e2.toString();
                        log.warn(errorMsg.getMessage(), errorMsg);
                        throw new Exception(errorMsg, e2);
                    }
                    diferencia = CompromisoManager.diferenciaSICOPvsSAI(conn, caNoCompromiso);
                    if (diferencia.compareTo(BigDecimal.ZERO) == 0) {
                        try {
                            // Quitar aplicación mientras no se actualiza la
                            // extraccion de SICOP
                            // CompromisoManager.updateHeaderCompromisosRealimentacion(
                            // conn, nEnviadoSICOP, caNoCompromiso, nDocumento
                            // );
                            // CompromisoManager.aplicaCompromiso( conn,
                            // caNoCompromiso );
                        } catch (Exception e4) {
                            String errorMsg = "Error mientras se aplicaba el compromiso " + renglon[20] + ". Causa: " + e4.toString();
                            log.warn(errorMsg.getMessage(), errorMsg);
                            throw new Exception(errorMsg, e4);
                        }
                    } else {
                        throw new Exception("No se aplico el compromiso" + renglon[20] + " ya que no cuadra el importe SAI vs SICOP");
                    }
                }
                conn.commit();
            } catch (Exception e) {
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e2) {
                        log.warn("Object: {}", "Problemas en rollback: " + e2);
                    }
                if (resultado == null)
                    resultado = new ArrayList<String>();
                resultado.add(e.toString());
                log.error(e.getMessage(), e);
            } finally {
                nRenglon++;
                CloseObject.closeObject(conn);
            }
        }
        return resultado;
    }

    private void borrarTabla() throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            CompromisoManager.borraTabla(conn);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public List<String[]> leerArchivo(InputStream in) throws Exception {
        boolean primeraLinea = true;
        List<String[]> lista = new ArrayList<String[]>();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String renglon = "";
        // Hashtable<String, Integer> nomColumna = new Hashtable<String,
        // Integer>();
        try {
            while ((renglon = br.readLine()) != null) {
                String[] llaves = renglon.split(",");
                if (primeraLinea) {
                    primeraLinea = !primeraLinea;
                } else {
                    if (llaves[2].equalsIgnoreCase("RHQ") && "COMPROMISO".equalsIgnoreCase(llaves[24]) && !llaves[17].equals("8"))
                        lista.add(llaves);
                }
                // fin else
            }
            // fin while
            return lista;
        } finally {
            br.close();
        }
    }

    public List<String[]> leerArchivoSuficiencia(InputStream in) throws Exception {
        log.info("Iniciando lectura de archivo CSV para la aplicacion de compromiso");
        List<String[]> lista = new ArrayList<>();
        log.trace("Creando formato de lectura CSV");
        CSVFormat format = CSVFormat.DEFAULT.builder().setDelimiter(',').setQuote('"').setIgnoreSurroundingSpaces(true).setTrim(true).setHeader().setSkipHeaderRecord(true).setAllowMissingColumnNames(true).build();
        log.trace("Object: {}", "Formato creado " + format);
        int renglon = 1;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            CSVParser parser = new CSVParser(reader, format)) {
            log.trace("Leyendo archivo CSV para la aplicacion de compromiso");
            for (CSVRecord record : parser) {
                String[] row = new String[record.size()];
                if (record.get(1).equalsIgnoreCase("RHQ") && "SUFICIENCIA".equalsIgnoreCase(record.get(10))) {
                    for (int i = 0; i < record.size(); i++) {
                        row[i] = record.get(i);
                    }
                    lista.add(row);
                    log.trace("Object: {}", "Renglon " + renglon++ + " Leido exitosamente ");
                }
            }
        }
        return lista;
    }

    public List<String[]> leerArchivoAnalisisCompromisos(InputStream in) throws Exception {
        log.info("Iniciando lectura de archivo CSV para la aplicacion de compromiso");
        List<String[]> lista = new ArrayList<>();
        log.trace("Creando formato de lectura CSV");
        CSVFormat format = CSVFormat.DEFAULT.builder().setDelimiter(',').setQuote('"').setIgnoreSurroundingSpaces(true).setTrim(true).setHeader().setSkipHeaderRecord(true).setAllowMissingColumnNames(true).build();
        log.trace("Object: {}", "Formato creado " + format);
        int renglon = 1;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            CSVParser parser = new CSVParser(reader, format)) {
            log.trace("Leyendo archivo CSV para la aplicacion de compromiso");
            for (CSVRecord record : parser) {
                String[] row = new String[record.size()];
                if (record.get(1).equalsIgnoreCase("RHQ")) {
                    for (int i = 0; i < record.size(); i++) {
                        row[i] = record.get(i);
                    }
                    lista.add(row);
                    log.trace("Object: {}", "Renglon " + renglon++ + " Leido exitosamente ");
                }
            }
        }
        return lista;
    }

    public boolean CompromisosFederalizados(HttpServletRequest req) throws Exception {
        boolean bRegresa = false;
        Connection conn = null;
        try {
            conn = getConnection();
            bRegresa = CompromisoManager.insertaCompromisosFederalizados(conn, req);
            if (bRegresa) {
                conn.commit();
            }
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                throw new GestionException(e.getMessage());
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return bRegresa;
    }

    public void AplicaMotorCompromiso(int nFolio) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            AccountingEngine accEng = new AccountingEngine();
            accEng.setValidaInsuficienciaDeSaldo(true);
            accEng.makeAccountingApplication(conn, "COMPROMISO", String.valueOf(nFolio), "tcompromisoencabezado", "tcompromisodetalle", "nFolioCompromiso");
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
                throw new GestionException(e.getMessage());
            }
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public void AplicaMotorCompromisoPrecom(int nFolio) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            AccountingEngine accEng = new AccountingEngine();
            accEng.setValidaInsuficienciaDeSaldo(true);
            accEng.makeAccountingApplication(conn, "PRECOMFINANCIERO", String.valueOf(nFolio), "tPrecomFinancieroEncabezado", "tPrecomFinancieroDetalle", "nFolioPrecomFinanciero");
            accEng.makeAccountingApplication(conn, "COMPROMISO", String.valueOf(nFolio), "tcompromisoencabezado", "tcompromisodetalle", "nFolioCompromiso");
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
                throw new GestionException(e.getMessage());
            }
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public String insertaCompromisoPDNomina(Usuario usuario, String folioPDNomina, String fechaProgramada, String leyenda, String cuentaBancaria, String folioGenerator, String CxPPDNomina) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            String centroContableUser = usuario.getPropiedad("CCENTROCONTABLE").getValor();
            FolioGeneratorInterface fg = Util.getFolioGenerator(folioGenerator);
            Caso casoCompromiso = CompromisoManager.generaCasoCompromiso(conn, usuario, fg, usuario.getLogin());
            int folioCompromiso = Integer.parseInt(casoCompromiso.getFolio().substring(casoCompromiso.getFolio().lastIndexOf('-') + 1));
            CFSequenceManager sequence = CFSequenceManager.getInstance();
            int seqFolio = 100000 + sequence.nextVal("CO-" + centroContableUser);
            String contrarecibo = centroContableUser + "CO" + EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal() + String.valueOf(seqFolio);
            // String idIntegracion =
            // PagosDirectosManager.generaFolioIntegracion(conn,
            // cuentaBancaria);
            CompromisoManager.insertaCompromisoPDNominaEncabezado(conn, folioCompromiso, folioPDNomina, contrarecibo, usuario, CxPPDNomina);
            CompromisoManager.insertaCompromisoPDNominaDetalle(conn, folioCompromiso, folioPDNomina);
            int folioPDNominaCompromiso = CompromisoManager.insertaPDNominaCompromisoEncabezado(conn, folioCompromiso, contrarecibo, cuentaBancaria, fechaProgramada, leyenda, usuario);
            CompromisoManager.insertaPDNominaCompromisoDetalle(conn, folioPDNominaCompromiso, folioPDNomina);
            /* Cambia a 1 los estatus de enviado a sicop */
            PagosDirectosManager.updateHeaderCompromisos(conn, folioPDNomina);
            // avanzaCasoCompromiso(casoCompromiso,usuario);
            conn.commit();
            return contrarecibo;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public ArrayList<String> layoutCompromisos(String foliosCompromiso, String esReimpresion, int esIntegrada, String cxpIntegrada) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            boolean reimprimir = true;
            if ("0".equals(esReimpresion)) {
                reimprimir = false;
            }
            if (esIntegrada == 0) {
                arrListaComp = CompromisoManager.BuscaCompromisos(conn, foliosCompromiso, reimprimir, 0);
            } else {
                int largo = cxpIntegrada.length() - 1;
                String cxp = cxpIntegrada.substring(0, largo);
                arrListaComp = CompromisoManager.BuscaCompromisosIntegrados(conn, foliosCompromiso, reimprimir, 0, cxp);
                foliosCompromiso = CompromisoManager.obtenerFoliosIntegrados(conn, cxp);
            }
            if (!reimprimir) {
                if (esIntegrada == 0) {
                    CompromisoManager.actualizaEnvioSICOPCompromiso(conn, foliosCompromiso);
                } else {
                    CompromisoManager.actualizaEnvioSICOPCompromisoIntegrada(conn, foliosCompromiso);
                }
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                throw new GestionException(e.getMessage());
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return arrListaComp;
    }

    public ArrayList<String> layoutCompromisosCalendario(String foliosCompromiso, String esReimpresion) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            boolean reimprimir = true;
            if ("0".equals(esReimpresion)) {
                reimprimir = false;
            }
            arrListaComp = CompromisoManager.BuscaCompromisos(conn, foliosCompromiso, reimprimir, 1);
            if (!reimprimir) {
                CompromisoManager.actualizaEnvioSICOPCompromiso(conn, foliosCompromiso);
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                throw new GestionException(e.getMessage());
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return arrListaComp;
    }

    public ArrayList<String> layoutSuficiencia(String foliosSuf, String reimprimir, int esIntegrada, String cxpIntegrada, String esCalendario) throws Exception {
        ArrayList<String> arrListaSuf = null;
        Connection conn = null;
        try {
            conn = getConnection();
            if (esIntegrada == 0) {
                arrListaSuf = CompromisoManager.BuscaSuficiencia(conn, foliosSuf, esCalendario);
            } else {
                int largo = cxpIntegrada.length() - 1;
                String cxp = cxpIntegrada.substring(0, largo);
                arrListaSuf = CompromisoManager.BuscaSuficienciaIntegrada(conn, foliosSuf, reimprimir, cxp);
                foliosSuf = CompromisoManager.obtenerFoliosIntegrados(conn, cxp);
            }
            if ("0".equals(reimprimir)) {
                CompromisoManager.actualizaEnvioSICOPSuf(conn, foliosSuf);
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                throw new GestionException(e.getMessage());
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return arrListaSuf;
    }

    public ArrayList<String> extraerExcel(String cxp) throws Exception {
        ArrayList<String> arrListaSuf = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaSuf = CompromisoManager.extraeOficioRG(conn, cxp);
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                throw new GestionException(e.getMessage());
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return arrListaSuf;
    }

    public void generaListadoCompromisoAut(File templateFile) throws Exception {
        try (Connection conn = getConnection();
            FileInputStream fis = new FileInputStream(templateFile);
            Workbook workbook = new XSSFWorkbook(fis)) {
            List<CompromisoDTO> lista = CompromisoManager.generaListadoCompromisoAut(conn);
            Sheet sheet = workbook.getSheetAt(0);
            int startRow = 5;
            for (int i = 0; i < lista.size(); i++) {
                CompromisoDTO dto = lista.get(i);
                Row row = sheet.createRow(startRow + i);
                row.createCell(1).setCellValue(dto.getCidproceso());
                row.createCell(2).setCellValue(dto.getCcompromisosicop());
                row.createCell(3).setCellValue(dto.getTipoMov());
                row.createCell(4).setCellValue(dto.getUr());
                row.createCell(5).setCellValue(dto.getEp());
                row.createCell(6).setCellValue(dto.getCidcontrato());
                row.createCell(7).setCellValue(dto.getCdescripcion());
                row.createCell(8).setCellValue(dto.getOrigen());
                row.createCell(9).setCellValue(dto.getSaldo());
                row.createCell(10).setCellValue(dto.getMimporte());
                row.createCell(11).setCellValue(dto.getDisponible());
            }
            try (FileOutputStream fos = new FileOutputStream(templateFile)) {
                workbook.write(fos.toPath());
            }
        }
    }

    public void guardarImportesCalendario(List<CalendarioDetalle> datos, String ep, int folio, String tipo) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            String evento = "";
            for (CalendarioDetalle detalle : datos) {
                int mes = detalle.getMes();
                BigDecimal importe = detalle.getCaptura();
                if (tipo.equals("reduccion")) {
                    evento = "CMP004";
                } else {
                    evento = "CMP002";
                }
                CompromisoManager.guardarCalendario(conn, folio, ep, mes, importe, evento);
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                throw new GestionException(e.getMessage());
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
    }

    public void eliminaRegistroCalendario(int folio, String ep, String mes) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            CompromisoManager.eliminarRegistroCalendario(conn, folio, ep, mes);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                throw new GestionException(e.getMessage());
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
    }

    public void guardarCompromisoCalendario(int folio, String cxp, Usuario u, String folioCaso) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            boolean revisaClaves = false;
            revisaClaves = CompromisoManager.validarClavesCalendario(conn, folio);
            if (revisaClaves) {
                String ejercicioFiscal = EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal();
                PrecompromisoFinanciero pf = CompromisoManager.insertaPrecompromiso(conn, Integer.parseInt(ejercicioFiscal), folio, u, cxp);
                CompromisoManager.insertaCompromiso(conn, cxp, ejercicioFiscal, u, pf, folio);
                //Aplicar precompromiso
                AccountingEngine ae = new AccountingEngine();
                ae.setValidaInsuficienciaDeSaldo(true);
                ae.makeAccountingApplication(conn, "PRECOMFINANCIERO", String.valueOf(folio), "tPrecomFinancieroEncabezado", "tPrecomFinancieroDetalle", "nFolioPrecomFinanciero");
                Caso c = new Caso();
                c.setFolio(folioCaso);
                c = CasoManager.select(conn, c);
                avanzaCasoCompromiso(conn, c, u);
            } else {
                throw new Exception("Ocurrio un error al validar las claves presupuestales, favor de reportar al administrador.");
            }
            // Si es interna entonces aplica el compromiso
            boolean revisaEsInterna = CompromisoManager.validarCalendarioInterno(conn, folio);
            if (revisaEsInterna) {
                AccountingEngine ae = new AccountingEngine();
                ae.setValidaInsuficienciaDeSaldo(true);
                ae.makeAccountingApplication(conn, "COMPROMISO", String.valueOf(folio), "tcompromisoencabezado", "tcompromisodetalle", "nFolioCompromiso");
                CompromisoManager.updateHeaderCompromisosRealimentacion(conn, 2, cxp, "-1");
            }
            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
                throw new GestionException(e.getMessage());
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
    }

    public void cancelarPrecompSuficiencia(String folio) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            //Revisar si el precompromiso esta aplicado
            boolean bAplicado = CompromisoManager.consultaPrecompromisoAplicado(conn, folio);
            if (bAplicado) {
                //Aplicar precompromiso
                AccountingEngine ae = new AccountingEngine();
                ae.setValidaInsuficienciaDeSaldo(true);
                ae.cancelAccountingApplication(conn, "PRECOMFINANCIERO", String.valueOf(folio), "tPrecomFinancieroEncabezado", "tPrecomFinancieroDetalle", "nFolioPrecomFinanciero");
                CompromisoManager.actualizarStatusCompromiso(conn, folio);
            } else {
                CompromisoManager.actualizarStatusPrecomp(conn, folio);
                CompromisoManager.actualizarStatusCompromiso(conn, folio);
            }
            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
                throw new GestionException(e.getMessage());
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
    }

    public ArrayList<String> layoutSuficienciaCalendario(String folio, String reimprimir) throws Exception {
        ArrayList<String> arrListaSuf = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaSuf = CompromisoManager.BuscaSuficiencia(conn, folio, "R");
            if (!CompromisoManager.existeSuficiencia(conn, folio)) {
                String cxp = CompromisoManager.cxpSuficiencia(conn, folio);
                CompromisoManager.insertaSuficiencia(conn, folio, cxp);
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                throw new GestionException(e.getMessage());
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return arrListaSuf;
    }

    public void aplicarCompromisoDirecto(String folio, String folioSICOP) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            CompromisoManager.aplicaCompromiso(conn, folio, folioSICOP);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                throw new GestionException(e.getMessage());
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
    }
}
