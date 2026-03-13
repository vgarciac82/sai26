package com.syc.ejercido.pagado;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.jfree.util.Log;
import com.syc.contable.core.AplicacionContable;
import com.syc.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConvenioCarga extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(AplicacionContable.class);

    public String enviaRuta(InputStream in, String tipoArchivo) throws FileNotFoundException {
        BufferedReader brr = new BufferedReader(new InputStreamReader(in));
        String sCadenas = "";
        String valorReturn = "";
        String szError = "";
        Connection conn = null;
        int nRegistro = 0;
        try {
            int nRegistrosCorrectos = 0;
            String noGuardado = "0";
            conn = getConnection();
            while ((sCadenas = brr.readLine()) != null) {
                nRegistro = nRegistro + 1;
                if (nRegistro > 1) {
                    String[] celdas = sCadenas.split(",");
                    if (tipoArchivo.equals("cargaArchivo")) {
                        // Quitar este dato y poner el del Contrato en la tabla dConvenio.
                        String EjercicioFiscal = celdas[0].trim();
                        // Debe existir en la Tabla dContrato
                        String NoContrato = celdas[1].trim();
                        // Debe existir en dContrato y estar en el mismo registro que el dato anterior
                        String NoPrestamo = celdas[2].trim();
                        // Debe ser cuando mucho el 15% del Monto Original del Contrato
                        String ImporteConvenio = celdas[3].trim();
                        // Debe ser posterior a la Fecha Firma del Contrato
                        String FechaFirma = celdas[4].trim();
                        // Debe tener cuando mucho 50 caracteres
                        String NoConvenio = celdas[5].trim();
                        String respFecha = "";
                        // Validaciones para Carga
                        String valor = buscaNoContrato(conn, NoContrato, NoPrestamo, FechaFirma);
                        PreparedStatement id_Contrato = conn.prepareStatement("SELECT c.id_Contrato  FROM dContrato AS c WITH(NOLOCK) WHERE c.NoContrato = ? ");
                        id_Contrato.setString(1, NoContrato);
                        ResultSet rs1 = id_Contrato.executeQuery();
                        String idC = "";
                        if (rs1.next()) {
                            idC = rs1.getString("id_Contrato");
                        }
                        String valor5 = buscaNoConvenio(conn, NoConvenio, idC);
                        String[] v = valor.split("/");
                        respFecha = v[1];
                        String existe = v[0];
                        String longitudNoConvenio = "";
                        if (NoConvenio.length() > 50) {
                            longitudNoConvenio = "Longitud del Convenio Mayor a 50";
                            System.out.println(longitudNoConvenio);
                        }
                        boolean respuestaImportes = comparaImportes(conn, Double.parseDouble(ImporteConvenio), idC);
                        if ("S&iacute; Existe Contrato-Pr&eacute;stamo".equals(existe) && "".equals(longitudNoConvenio) && "Fecha Correcta".equals(respFecha) && "No Existe Convenio".equals(valor5) && respuestaImportes) {
                            String status = insertaLineaComprometidoEnc(conn, EjercicioFiscal, NoContrato, NoPrestamo, ImporteConvenio, FechaFirma, NoConvenio);
                            nRegistrosCorrectos = nRegistrosCorrectos + 1;
                            if (status.equals("noGuardado")) {
                                noGuardado += nRegistro;
                                noGuardado += ",";
                            }
                        } else {
                            szError += "Error en el renglon " + (nRegistro) + " ";
                            if (!"S&iacute; Existe Contrato-Pr&eacute;stamo".equals(existe))
                                szError = szError + existe;
                            if (!"".equals(longitudNoConvenio))
                                szError = szError + ", " + longitudNoConvenio;
                            if (!"Fecha Correcta".equals(respFecha))
                                szError = szError + ", " + respFecha;
                            if (!"No Existe Convenio".equals(valor5))
                                szError = szError + ", " + valor5;
                            if (!respuestaImportes)
                                szError = szError + ", el Importe del Convenio es mayor al 15 por ciento del Importe del Contrato";
                            szError += ";";
                        }
                    }
                }
            }
            nRegistro = nRegistro - 1;
            int diferencia = nRegistro - nRegistrosCorrectos;
            valorReturn += "Registros Totales: = " + nRegistro + " \n";
            valorReturn += "Registros Correctos: = " + nRegistrosCorrectos + " \n";
            valorReturn += "Registros con Error: = " + diferencia + " \n";
            //+ "Error en linea(s): = " + noGuardado
            valorReturn += " \n" + szError;
            conn.commit();
        } catch (Exception se) {
            log.error("Error: " + se);
            se.printStackTrace();
            valorReturn = " error en el renglon " + nRegistro + ", datos incompletos.";
            try {
                conn.rollback();
            } catch (Exception exc) {
                Log.warn("Error: cerrando rollback enviaRuta " + exc);
            }
        } finally {
            try {
                if (brr != null)
                    brr.close();
            } catch (Exception exc) {
                Log.warn("Cerrando BufferedReader", exc);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception exc) {
                Log.warn("Cerrando BufferedReader", exc);
            }
            brr = null;
            conn = null;
        }
        return valorReturn;
    }

    public String insertaLineaComprometidoEnc(Connection conn, String EjercicioFiscal, String NoContrato, String NoPrestamo, String ImporteConvenio, String FechaFirma, String NoConvenio) {
        String respuesta = "noGuardado", idP = "", idC = "";
        PreparedStatement ps = null, pscaso = null, psInserta = null, id_contrato = null;
        ResultSet rs = null, rs1 = null;
        try {
            id_contrato = conn.prepareStatement("SELECT c.id_Contrato, p.id_prestamo  FROM dContrato AS c WITH(NOLOCK), dPrestamo AS p WITH(NOLOCK) WHERE c.NoContrato = ? AND p.NumeroPrestamo = ? ");
            id_contrato.setString(1, NoContrato);
            id_contrato.setString(2, NoPrestamo);
            rs1 = id_contrato.executeQuery();
            if (rs1.next()) {
                idC = rs1.getString("id_contrato");
                idP = rs1.getString("id_prestamo");
            }
            String n = "INSERT INTO dConvenio_Modificatorio (EjercicioFiscal,NoContrato,NoPrestamo,ImporteConvenio,FechaFirma,NoConvenio) VALUES ('" + EjercicioFiscal + "','" + idC + "','" + idP + "','" + ImporteConvenio + "','" + FechaFirma + "','" + NoConvenio + "')";
            System.out.println(n);
            ps = conn.prepareStatement("INSERT INTO dConvenio_Modificatorio (EjercicioFiscal,id_Contrato,id_prestamo,importe,FechaFirma,NumeroConvenio,FechaUltimoMovimiento,TipoCarga) VALUES ('" + EjercicioFiscal + "','" + idC + "','" + idP + "','" + ImporteConvenio + "','" + FechaFirma + "','" + NoConvenio + "',getdate(), 'A')");
            ps.executeUpdate();
            respuesta = "guardado";
        } catch (Exception e) {
            log.error("Error Guardar Compromiso Encabezado: " + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (pscaso != null) {
                    pscaso.close();
                }
                if (psInserta != null) {
                    psInserta.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception ef) {
                log.warn("Error: cerrando statement: " + ef);
            }
            ps = null;
            pscaso = null;
            psInserta = null;
            rs = null;
            rs1 = null;
        }
        return respuesta;
    }

    public String buscaNoContrato(Connection conn, String NoContrato, String NoPrestamo, String FechaFirma) throws SQLException {
        String respuesta = "No Existe Contrato-Prestamo";
        PreparedStatement ps = null;
        ResultSet rs = null;
        String fechaFirmaCt = "";
        String respFecha = "";
        String[] ffCT = new String[3];
        try {
            ps = conn.prepareStatement("SELECT c.NoContrato, c.FechaFirma FROM dContrato AS c WITH(NOLOCK), dPrestamo AS p WITH(NOLOCK) WHERE c.NoContrato = ? AND p.NumeroPrestamo = ? ");
            ps.setString(1, NoContrato);
            ps.setString(2, NoPrestamo);
            rs = ps.executeQuery();
            if (rs.next()) {
                respuesta = "S&iacute; Existe Contrato-Pr&eacute;stamo";
                fechaFirmaCt = rs.getString("FechaFirma");
                ffCT = fechaFirmaCt.split("-");
                String[] Min = new String[2];
                Min = ffCT[2].split(" ");
                fechaFirmaCt = Min[0] + "/" + ffCT[1] + "/" + ffCT[0];
                System.out.println(fechaFirmaCt);
            }
            if (fechaFirmaCt.compareTo(FechaFirma) > 0) {
                System.out.println(FechaFirma + " es posterior a " + fechaFirmaCt);
                respFecha = "Fecha Correcta";
            } else {
                System.out.println(FechaFirma + " es anterior a " + fechaFirmaCt);
                respFecha = "Fecha Incorrecta";
            }
            System.out.println("RES: " + respuesta);
            respuesta = respuesta + "/" + respFecha;
        } catch (Exception e) {
            log.warn("Error: Contrato-Préstamo-Fecha: " + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.warn("Error: cerrando statement: " + e);
            }
            ps = null;
            rs = null;
        }
        return respuesta;
    }

    public String buscaNoConvenio(Connection conn, String NoConvenio, String idC) throws SQLException {
        String respuesta5 = "No Existe Convenio";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT * FROM dConvenio_Modificatorio WITH(NOLOCK) WHERE NumeroConvenio = ? AND id_Contrato = ?");
            ps.setString(1, NoConvenio);
            ps.setString(2, idC);
            rs = ps.executeQuery();
            if (rs.next()) {
                respuesta5 = "Ya Existe ese Numero de Convenio asociado a ese Contrato";
            }
            System.out.println("RES: " + respuesta5 + ": " + NoConvenio);
        } catch (Exception e) {
            log.warn("Error: Buscar Compromiso Aplicado: " + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.warn("Error: cerrando statement: " + e);
            }
            ps = null;
            rs = null;
        }
        return respuesta5;
    }

    public boolean comparaImportes(Connection conn, double ImporteConvenio, String idC) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        double impContrato = 0;
        boolean respuesta = false;
        try {
            ps = conn.prepareStatement("SELECT * FROM dContrato WITH(NOLOCK) WHERE id_Contrato = ?");
            ps.setString(1, idC);
            rs = ps.executeQuery();
            if (rs.next()) {
                impContrato = rs.getDouble("ImporteOriginal");
            }
            // en TRUE sí hace la inserción
            respuesta = (impContrato * .15) >= ImporteConvenio;
            System.out.println("RES: " + respuesta);
        } catch (Exception e) {
            log.warn("Error: Buscar Compromiso Aplicado: " + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.warn("Error: cerrando statement: " + e);
            }
            ps = null;
            rs = null;
        }
        return respuesta;
    }
}
