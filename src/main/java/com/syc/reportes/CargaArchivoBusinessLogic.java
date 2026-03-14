package com.syc.reportes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.syc.reportes.core.CargaArchivoManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CargaArchivoBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(CargaArchivoBusinessLogic.class);

    public CargaArchivoBusinessLogic(String jniName) {
        super.init(jniName);
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
        boolean aplicado = false;
        borrarDatos();
        int nRenglon = 1;
        for (String[] renglon : renglonesArchivo) {
            try {
                conn = getConnection();
                log.debug("Object: {}", " Operando el renglon " + nRenglon + " : " + renglon[2]);
                int columna = 3;
                String fAp = renglon[0];
                String fExp = renglon[1];
                int folio = Integer.parseInt(renglon[2]);
                String retiros = renglon[7];
                if ("0".equals(retiros) || "-".equals(retiros)) {
                    continue;
                } else {
                    boolean existe = CargaArchivoManager.existeFolio(conn, folio, retiros);
                    if (existe) {
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
                        fAplicacion = new java.sql.Date(fExpTmp.getTime());
                        CargaArchivoManager.insertArchivo(conn, fExpedicion, fAplicacion, folio, renglon[columna++], renglon[columna++], renglon[columna++], renglon[columna++], renglon[columna++], renglon[columna++]);
                        aplicado = CargaArchivoManager.estaAplicado(conn, folio);
                        if (!aplicado) {
                            //inserta ejercido
                            try {
                                String strFolioEjercido = CargaArchivoManager.insertaEjercido(conn, folio, fAppTmp);
                                if ("0".equals(strFolioEjercido))
                                    throw new Exception("Error al cargar el ejercido");
                                else
                                    CargaArchivoManager.aplicaEjercido(conn, strFolioEjercido);
                                conn.commit();
                            } catch (Exception e2) {
                                String errorMsg = "Error mientras se aplicaba el ejercido " + folio + ". Causa: " + e2.toString();
                                log.warn(errorMsg.getMessage(), errorMsg);
                                throw new Exception(errorMsg, e2);
                            }
                            // Inserta pagado
                            try {
                                String strFolioPag = CargaArchivoManager.insertaPagado(conn, folio, fAppTmp);
                                if ("0".equals(strFolioPag))
                                    throw new Exception("Error al cargar el ejercido");
                                else
                                    CargaArchivoManager.aplicaPagado(conn, strFolioPag);
                                conn.commit();
                            } catch (Exception e4) {
                                String errorMsg = "Error mientras se aplicaba el pagado " + folio + ". Causa: " + e4.toString();
                                log.warn(errorMsg.getMessage(), errorMsg);
                                throw new Exception(errorMsg, e4);
                            }
                        } else {
                            boolean apPagado = CargaArchivoManager.estaAplicadoPagado(conn, folio);
                            if (!apPagado) {
                                try {
                                    String strFolioPag = CargaArchivoManager.insertaPagado(conn, folio, fAppTmp);
                                    if ("0".equals(strFolioPag))
                                        throw new Exception("Error al cargar el ejercido");
                                    else
                                        CargaArchivoManager.aplicaPagado(conn, strFolioPag);
                                    conn.commit();
                                } catch (Exception e4) {
                                    String errorMsg = "Error mientras se aplicaba el pagado " + folio + ". Causa: " + e4.toString();
                                    log.warn(errorMsg.getMessage(), errorMsg);
                                    throw new Exception(errorMsg, e4);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e2) {
                        log.warn("Object: {}", "Problemas en rollback: " + e2);
                    }
                if (resultado == null)
                    resultado = new ArrayList<String>();
                resultado.add("Error procensando renglon " + nRenglon + ". Causa: " + e.toString());
                log.error(e.getMessage(), e);
            } finally {
                nRenglon++;
                CloseObject.closeObject(conn);
            }
        }
        return resultado;
    }

    public List<String> procesaLayoutTaxis(File nombreDestino) throws Exception {
        List<String> resultado = null;
        Connection conn = null;
        InputStream is = new FileInputStream(nombreDestino);
        List<String[]> renglonesArchivo = leerArchivo(is);
        int nRenglon = 1;
        String[] formatosFecha = { "yyyy-MM-dd", "dd/MM/yyyy", "yyyy/MM/dd", "yyyyMMdd", "yyyy-MM-dd HH:mm:ss" };
        Date fExpTmp = null;
        try {
            conn = getConnection();
            for (String[] renglon : renglonesArchivo) {
                String folio = renglon[6];
                String fExp = renglon[0];
                String noEmp = renglon[1];
                int columna = 3;
                for (String fmt : formatosFecha) {
                    try {
                        fExpTmp = new SimpleDateFormat(fmt).parse(fExp.trim());
                        break;
                    } catch (ParseException ignored) {
                    }
                }
                if (fExpTmp == null) {
                    resultado.add("Error procesando renglón " + nRenglon + ": formato de fecha inválido (" + fExp + ")");
                    continue;
                }
                java.sql.Date fAplicacion = new java.sql.Date(fExpTmp.getTime());
                boolean existe = CargaArchivoManager.existeFolioTaxi(conn, folio);
                if (existe) {
                    CargaArchivoManager.borrarFolioTaxi(conn, folio);
                }
                CargaArchivoManager.insertArchivoTaxis(conn, fAplicacion, noEmp, renglon[columna++], renglon[columna++], renglon[columna++], renglon[columna++], renglon[columna++]);
                nRenglon++;
            }
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                conn.rollback();
            resultado.add("Error procesando renglón " + nRenglon + ". Causa: " + e.getMessage());
            log.error("Error en procesaLayoutTaxis", e);
        } finally {
            CloseObject.closeObject(conn);
        }
        return resultado;
    }

    public List<String[]> leerArchivo(InputStream in) throws Exception {
        boolean primeraLinea = true;
        List<String[]> lista = new ArrayList<String[]>();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String renglon = "";
        try {
            while ((renglon = br.readLine()) != null) {
                String[] llaves = renglon.split(",");
                if (primeraLinea) {
                    primeraLinea = !primeraLinea;
                } else {
                    lista.add(llaves);
                }
            }
            // fin while
            return lista;
        } finally {
            br.close();
        }
    }

    private void borrarDatos() throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            CargaArchivoManager.borraTabla(conn);
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
}
