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

public class ContratoCarga extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(AplicacionContable.class);

    public String enviaRuta(InputStream in, String tipoArchivo) throws FileNotFoundException {
        BufferedReader brr = new BufferedReader(new InputStreamReader(in));
        String sCadenas = "";
        String valorReturn = "";
        String szError = "";
        Connection conn = null;
        int nRegistro = 0;
        int nRegistrosCorrectos = 0;
        try {
            String noGuardado = "0";
            conn = getConnection();
            while ((sCadenas = brr.readLine()) != null) {
                nRegistro = nRegistro + 1;
                if (nRegistro > 1) {
                    String[] celdas = sCadenas.split(",");
                    if (tipoArchivo.equals("cargaArchivo")) {
                        // Debe existir en la Tabla tCatalogoEntidadFederativa
                        String EntidadFederativa = celdas[0].trim();
                        // Debe existir en la Tabla dCat_Entidad_Ejec_Resp.
                        String Ejecutor = celdas[1].trim();
                        // Debe tener cuando mucho 50 caracteres y no existir uno igual anterior.
                        String NoContrato = celdas[2].trim();
                        // Debe existir en la Tabla dPrestamo
                        String Prestamo = celdas[3].trim();
                        // Debe existir en la Tabla dCategoriaInversion_prestamo y estar en el mismo registro del id_prestamo
                        String CategoriaInversion = celdas[4].trim();
                        // Debe existir en la Tabla dComponente_Tecnico y estar en el mismo registro del id_prestamo
                        String Componente = celdas[5].trim();
                        String EjercicioFiscal = celdas[6].trim();
                        String ImporteOriginal = celdas[7].trim();
                        String ImporteTotal = celdas[8].trim();
                        // Debe tener cuando mucho 20 caracteres
                        String RFCBeneficiario = celdas[9].trim();
                        // Debe tener cuando mucho 300 caracteres
                        String NombreBeneficiario = celdas[10].trim();
                        String FechaFirma = celdas[11].trim();
                        String FechaInicio = celdas[12].trim();
                        String FechaTerminacion = celdas[13].trim();
                        // Debe tener cuando mucho 500 caracteres
                        String ObjetoObra = celdas[14].trim();
                        // Debe existir en la Tabla oCatalogoTipoAdjudicacion
                        String TipoAdjudicacion = celdas[15].trim();
                        // Validaciones para Carga
                        PreparedStatement id_prestamo = conn.prepareStatement("SELECT p.id_prestamo  FROM dPrestamo AS p WITH(NOLOCK) WHERE p.NumeroPrestamo = ? ");
                        id_prestamo.setString(1, Prestamo);
                        ResultSet rs1 = id_prestamo.executeQuery();
                        String idP = "";
                        if (rs1.next()) {
                            idP = rs1.getString("id_prestamo");
                        }
                        String valor = buscaNoPrestamo(conn, Componente, CategoriaInversion, Prestamo);
                        String valor2 = buscaEntidadFederativa(conn, EntidadFederativa);
                        String valor3 = buscaTipoAdjudicacion(conn, TipoAdjudicacion);
                        String valor4 = buscaEjecutor(conn, Ejecutor);
                        String valor5 = buscaNoContrato(conn, NoContrato, idP);
                        String longitudContrato = "";
                        String longitudRFC = "";
                        String longitudBeneficiario = "";
                        String longitudObjeto = "";
                        if (NoContrato.length() > 50) {
                            longitudContrato = "Longitud del Contrato Mayor a 50";
                            System.out.println(longitudContrato);
                        }
                        if (RFCBeneficiario.length() > 20) {
                            longitudRFC = "Longitud del RFC Mayor a 20";
                            System.out.println(longitudRFC);
                        }
                        if (NombreBeneficiario.length() > 300) {
                            longitudBeneficiario = "Longitud del Beneficiario Mayor a 300";
                            System.out.println(longitudBeneficiario);
                        }
                        if (ObjetoObra.length() > 300) {
                            longitudObjeto = "Longitud del Objeto de la Obra  Mayor a 300";
                            System.out.println(longitudObjeto);
                        }
                        if ("S&iacute; Existe Prestamo-Componente-Categoria".equals(valor) && "".equals(longitudContrato) && "".equals(longitudRFC) && "".equals(longitudBeneficiario) && "".equals(longitudObjeto) && "S&iacute; Existe Entidad Federativa".equals(valor2) && "S&iacute; Existe Tipo Adjudicaci&oacute;n".equals(valor3) && "S&iacute; Existe Ejecutora Responsable".equals(valor4) && "No Existe Contrato".equals(valor5)) {
                            String status = insertaLineaComprometidoEnc(conn, EntidadFederativa, Ejecutor, NoContrato, Prestamo, CategoriaInversion, Componente, EjercicioFiscal, ImporteOriginal, ImporteTotal, RFCBeneficiario, NombreBeneficiario, FechaFirma, FechaInicio, FechaTerminacion, ObjetoObra, TipoAdjudicacion);
                            nRegistrosCorrectos = nRegistrosCorrectos + 1;
                            if (status.equals("noGuardado")) {
                                noGuardado += nRegistro;
                                noGuardado += ",";
                            }
                        } else {
                            szError += "Error en el renglon " + (nRegistro) + " ";
                            if (!"S&iacute; Existe Prestamo-Componente-Categoria".equals(valor))
                                szError = szError + valor;
                            if (!"S&iacute; Existe Entidad Federativa".equals(valor2))
                                szError = szError + ", " + valor2;
                            if (!"S&iacute; Existe Tipo Adjudicaci&oacute;n".equals(valor3))
                                szError = szError + ", " + valor3;
                            if (!"S&iacute; Existe Ejecutora Responsable".equals(valor4))
                                szError = szError + ", " + valor4;
                            if (!"No Existe Contrato".equals(valor5))
                                szError = szError + ", " + valor5;
                            if (!"".equals(longitudContrato))
                                szError = szError + ", " + longitudContrato;
                            if (!"".equals(longitudRFC))
                                szError = szError + ", " + longitudRFC;
                            if (!"".equals(longitudBeneficiario))
                                szError = szError + ", " + longitudBeneficiario;
                            if (!"".equals(longitudObjeto))
                                szError = szError + ", " + longitudObjeto + "\n";
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

    public String insertaLineaComprometidoEnc(Connection conn, String EntidadFederativa, String Ejecutor, String NoContrato, String Prestamo, String CategoriaInversion, String Componente, String EjercicioFiscal, String ImporteOriginal, String ImporteTotal, String RFCBeneficiario, String NombreBeneficiario, String FechaFirma, String FechaInicio, String FechaTerminacion, String ObjetoObra, String TipoAdjudicacion) {
        String respuesta = "noGuardado", idP = "", idCI = "";
        PreparedStatement ps = null, pscaso = null, psInserta = null, id_prestamo = null, id_categoriaInversionPrestamo = null;
        ResultSet rs1 = null, rs2 = null;
        try {
            id_prestamo = conn.prepareStatement("SELECT p.id_prestamo  FROM dPrestamo AS p WITH(NOLOCK) WHERE p.NumeroPrestamo = ? ");
            id_prestamo.setString(1, Prestamo);
            rs1 = id_prestamo.executeQuery();
            if (rs1.next()) {
                idP = rs1.getString("id_prestamo");
            }
            id_categoriaInversionPrestamo = conn.prepareStatement("SELECT ci.id_categoriaInversionPrestamo FROM dCategoriaInversion_prestamo AS ci WITH(NOLOCK), dPrestamo AS p WITH(NOLOCK) WHERE p.NumeroPrestamo = ? AND ci.NumeroCategoria = ? ");
            id_categoriaInversionPrestamo.setString(1, Prestamo);
            id_categoriaInversionPrestamo.setString(2, CategoriaInversion);
            rs2 = id_categoriaInversionPrestamo.executeQuery();
            if (rs2.next()) {
                idCI = rs2.getString("id_categoriaInversionPrestamo");
            }
            String n = "INSERT INTO dContrato (cEntidadFed,id_EntidadEjecResp,NoContrato,id_prestamo,id_categoriaInversionPrestamo,id_componentetecnico,EjercicioFiscal, ImporteOriginal,ImporteTotal,RFCBeneficiario,NombreBeneficiario,FechaFirma,FechaInicio,FechaTerminacion,ObjetoDeObra,TipoAdjudicacion,FechaUltimoMovimiento,TipoCarga ) VALUES ('" + EntidadFederativa + "','" + Ejecutor + "','" + NoContrato + "','" + idP + "','" + idCI + "','" + Componente + "','" + EjercicioFiscal + "','" + ImporteOriginal + "','" + ImporteTotal + "','" + RFCBeneficiario + "','" + NombreBeneficiario + "','" + FechaFirma + "','" + FechaInicio + "','" + FechaTerminacion + "','" + ObjetoObra + "','" + TipoAdjudicacion + "',getdate(), 'A')";
            System.out.println(n);
            ps = conn.prepareStatement("INSERT INTO dContrato (cEntidadFed,id_EntidadEjecResp,NoContrato,id_prestamo,id_categoriaInversionPrestamo,id_componentetecnico,EjercicioFiscal, ImporteOriginal,ImporteTotal,RFCBeneficiario,NombreBeneficiario,FechaFirma,FechaInicio,FechaTerminacion,ObjetoDeObra,TipoAdjudicacion,FechaUltimoMovimiento,TipoCarga ) VALUES ('" + EntidadFederativa + "','" + Ejecutor + "','" + NoContrato + "','" + idP + "','" + idCI + "','" + Componente + "','" + EjercicioFiscal + "','" + ImporteOriginal + "','" + ImporteTotal + "','" + RFCBeneficiario + "','" + NombreBeneficiario + "','" + FechaFirma + "','" + FechaInicio + "','" + FechaTerminacion + "','" + ObjetoObra + "','" + TipoAdjudicacion + "',getdate(), 'A')");
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
                if (rs1 != null) {
                    rs1.close();
                }
                if (rs2 != null) {
                    rs2.close();
                }
            } catch (Exception ef) {
                log.warn("Error: cerrando statement: " + ef);
            }
            ps = null;
            pscaso = null;
            psInserta = null;
            rs1 = null;
            rs2 = null;
        }
        return respuesta;
    }

    public String buscaNoPrestamo(Connection conn, String Componente, String CategoriaInversion, String Prestamo) throws SQLException {
        String respuesta = "No Existe Prestamo-Componente-Categoria";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT p.NumeroPrestamo  FROM dCategoriaInversion_prestamo AS ci WITH(NOLOCK), dPrestamo AS p WITH(NOLOCK) WHERE ci.id_componentetecnico = ? AND ci.NumeroCategoria = ? AND p.NumeroPrestamo = ?  ");
            ps.setString(1, Componente);
            ps.setString(2, CategoriaInversion);
            ps.setString(3, Prestamo);
            rs = ps.executeQuery();
            if (rs.next()) {
                respuesta = "S&iacute; Existe Prestamo-Componente-Categoria";
            }
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

    public String buscaEntidadFederativa(Connection conn, String EntidadFederativa) throws SQLException {
        String respuesta2 = "No Existe Entidad Federativa";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT * FROM tCatalogoEntidadFederativa WITH(NOLOCK) WHERE cEntidadFederativa = ? ");
            ps.setString(1, EntidadFederativa);
            rs = ps.executeQuery();
            if (rs.next()) {
                respuesta2 = "S&iacute; Existe Entidad Federativa";
            }
            System.out.println("RES: " + respuesta2 + ": " + EntidadFederativa);
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
        return respuesta2;
    }

    public String buscaTipoAdjudicacion(Connection conn, String TipoAdjudicacion) throws SQLException {
        String respuesta3 = "No Existe Tipo Adjudicacion";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT * FROM oCatalogoTipoAdjudicacion WITH(NOLOCK) WHERE cIdTipoAdjudicacion = ? ");
            ps.setString(1, TipoAdjudicacion);
            rs = ps.executeQuery();
            if (rs.next()) {
                respuesta3 = "S&iacute; Existe Tipo Adjudicaci&oacute;n";
            }
            System.out.println("RES: " + respuesta3 + ": " + TipoAdjudicacion);
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
        return respuesta3;
    }

    public String buscaEjecutor(Connection conn, String Ejecutor) throws SQLException {
        String respuesta4 = "No Existe Entidad Ejecutora Responsable";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT * FROM dCat_Entidad_Ejec_Resp WITH(NOLOCK) WHERE id_EntidadEjecResp = ? ");
            ps.setString(1, Ejecutor);
            rs = ps.executeQuery();
            if (rs.next()) {
                respuesta4 = "S&iacute; Existe Ejecutora Responsable";
            }
            System.out.println("RES: " + respuesta4 + ": " + Ejecutor);
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
        return respuesta4;
    }

    public String buscaNoContrato(Connection conn, String NoContrato, String id_prestamo) throws SQLException {
        String respuesta5 = "No Existe Contrato";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT * FROM dContrato WITH(NOLOCK) WHERE NoContrato = ? AND id_prestamo = ?");
            ps.setString(1, NoContrato);
            ps.setString(2, id_prestamo);
            rs = ps.executeQuery();
            if (rs.next()) {
                respuesta5 = "Ya Existe ese Numero de Contrato asociado a ese Prestamo";
            }
            System.out.println("RES: " + respuesta5 + ": " + NoContrato);
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
}
