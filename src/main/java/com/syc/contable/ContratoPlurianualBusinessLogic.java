package com.syc.contable;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

/**
 * Clase encargada de la carga de proyecto desde un archivo excel.
 * cargaExcelPlurianual_EP
 *
 * @author MCF
 * @version 1.0
 */
public class ContratoPlurianualBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(ContratoPlurianualBusinessLogic.class);

    private String uLoginCarga = "";

    /**
     * Construye una nueva instancia del objeto.
     */
    public ContratoPlurianualBusinessLogic(String uLogin) {
        super.init();
        this.uLoginCarga = uLogin;
    }

    public synchronized ArrayList<String> cargaExcelPlurianual_EP(InputStream sExcel, String sFolio, Integer bEspecial, String ur, int nModificacion) throws Exception {
        ArrayList<String> errores = new ArrayList<String>();
        Connection conn = null;
        try {
            conn = getConnection();
            //String a;
            int renglon = 1;
            int iCapitulo = 0;
            String sPartida = "";
            Calendar fecha = Calendar.getInstance();
            int AnioActual = fecha.get(Calendar.YEAR);
            int RENGLON = 30, COLUMNAS = 5;
            int[] Anios = new int[50];
            int AnioIterado = 0;
            double dCons = 0;
            double dSuper = 0;
            double[][] MatrizAnual = new double[RENGLON][COLUMNAS];
            double SaldoModificado = 0;
            int iClasificaGasto = 0;
            int iClasificaGastoTemp = 0;
            int iCapituloTemp = 0;
            Workbook workbook = new HSSFWorkbook(sExcel);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            double monto;
            //String montoFormateado;
            String cve_ppi;
            String EP, pp, sProgramaPres, sSubFuncion, sProgramaGral, sActividadInstitucional, EFederativa;
            //String Arreglo[] =
            Map<String, String> epMap = new LinkedHashMap<String, String>();
            Map<String, String> MapMontoAnual = new LinkedHashMap<String, String>();
            // eliminando registros en caso de haber una carga de Excel anterior
            // anterior
            ContratoPlurianualesManager oP = new ContratoPlurianualesManager();
            if (nModificacion != 0) {
                errores = validarModificaciones(workbook, sheet, sFolio, ur, nModificacion);
                if (!errores.isEmpty()) {
                    return errores;
                }
                oP.fn_EliminaContratoPlurianualDetalle(conn, sFolio);
                oP.EliminaContratoPlurianual_EP(conn, sFolio);
                oP.fn_EliminatContratoPlurianualMontosAnuales(conn, sFolio);
            } else {
                eliminaContratosCargado(sFolio, conn, oP);
            }
            //	conn.commit();
            String tipo = "";
            String tipoSolicitud = "";
            String valContra = "MINIMO";
            //int validaContra =1;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() == 0) {
                    tipo = row.getCell(1).getStringCellValue();
                    if (!("Abierto").equals(tipo) && !("Cerrado").equals(tipo)) {
                        errores.add("El campo tipo debe de ser Abierto &oacute; Cerrado");
                    }
                    tipoSolicitud = row.getCell(3).getStringCellValue();
                    if (!("Plurianualidad").equals(tipoSolicitud) && !("Especial").equals(tipoSolicitud)) {
                        errores.add("El campo solicitud debe de ser Plurianual &oacute; Especial");
                    }
                    if (getPlurianualesBloqueado(tipoSolicitud) == 1) {
                        errores.add("Este tipo de plurianualidades se encuentran bloqueadas");
                        return errores;
                    }
                    if (tipo.equals("Abierto")) {
                        Cell validarContra = row.getCell(5);
                        if (fn_ValidaCelda(validarContra, evaluator, 20)) {
                            errores.add("  La columna valida contra, debe ser 'MINIMO' o 'MAXIMO'");
                        } else {
                            valContra = validarContra.getStringCellValue();
                        }
                    }
                    continue;
                } else if (row.getRowNum() == 1) {
                    continue;
                }
                if (Util.renglonVacio(row))
                    continue;
                // año
                Cell cel_CICLO = row.getCell(0);
                Cell cel_R = row.getCell(1);
                Cell cel_UR = row.getCell(2);
                Cell cel_F = row.getCell(3);
                Cell cel_FN = row.getCell(4);
                Cell cel_SF = row.getCell(5);
                Cell cel_RG = row.getCell(6);
                Cell cel_AI = row.getCell(7);
                Cell cel_M = row.getCell(8);
                Cell cel_PP = row.getCell(9);
                // PARTIDA
                Cell cel_PTA = row.getCell(10);
                Cell cel_TG = row.getCell(11);
                Cell cel_FF = row.getCell(12);
                Cell cel_EF = row.getCell(13);
                Cell cel_CVEPPI = row.getCell(14);
                Cell cel_Unormativa = row.getCell(15);
                Cell cel_Uejecutora = row.getCell(16);
                Cell cel_SPP = row.getCell(17);
                Cell cel_IMPORTEMINIMO = row.getCell(18);
                Cell cel_IMPORTEMAXIMO = row.getCell(19);
                // cAOLUMNAS DEL EXCEL CICLO R UR F FN SF RG AI M PP PTA TG FF EF UN UE
                errores.addAll(validaciones(ur, renglon, evaluator, tipo, cel_CICLO, cel_R, cel_UR, cel_F, cel_FN, cel_SF, cel_RG, cel_AI, cel_M, cel_PP, cel_PTA, cel_TG, cel_FF, cel_EF, cel_CVEPPI, cel_Unormativa, cel_Uejecutora, cel_SPP, cel_IMPORTEMINIMO, cel_IMPORTEMAXIMO));
                if (errores.size() == 0) {
                    AnioIterado = (int) cel_CICLO.getNumericCellValue();
                    if (cel_CVEPPI == null) {
                        cve_ppi = "00000000000";
                    } else {
                        if (cel_CVEPPI.getCellType() == CellType.NUMERIC)
                            cve_ppi = String.valueOf((int) cel_CVEPPI.getNumericCellValue());
                        else
                            cve_ppi = cel_CVEPPI.getStringCellValue();
                        if (cve_ppi.equals("0")) {
                            cve_ppi = "00000000000";
                        }
                    }
                    pp = String.valueOf((int) cel_PP.getNumericCellValue());
                    sProgramaPres = cel_M.getStringCellValue();
                    EFederativa = String.valueOf((int) cel_EF.getNumericCellValue());
                    if (pp.length() == 1)
                        pp = "00" + pp;
                    if (pp.length() == 2)
                        pp = "0" + pp;
                    sProgramaPres = sProgramaPres + pp;
                    sSubFuncion = String.valueOf((int) cel_SF.getNumericCellValue());
                    if (sSubFuncion.length() == 1)
                        sSubFuncion = "0" + sSubFuncion;
                    sProgramaGral = String.valueOf((int) cel_RG.getNumericCellValue());
                    if (sProgramaGral.length() == 1)
                        sProgramaGral = "0" + sProgramaGral;
                    sActividadInstitucional = String.valueOf((int) cel_AI.getNumericCellValue());
                    if (sActividadInstitucional.length() == 1)
                        sActividadInstitucional = "00" + sActividadInstitucional;
                    if (sActividadInstitucional.length() == 2)
                        sActividadInstitucional = "0" + sActividadInstitucional;
                    if (EFederativa.length() == 1)
                        EFederativa = "0" + EFederativa;
                    sPartida = String.valueOf((int) cel_PTA.getNumericCellValue());
                    iCapitulo = (Integer.valueOf(sPartida.substring(0, 1)));
                    //Tipo de Obra en base a la partida
                    // sClasificaGasto =  ContratoPlurianualesManager.f_ClasificaTipoGasto(conn, (int) cel_TG.getNumericCellValue());
                    // Variable  donde formamos la ESTRUCTURA pROGRAMATICA
                    EP = String.valueOf((int) cel_CICLO.getNumericCellValue()) + "." + String.valueOf((int) cel_R.getNumericCellValue()) + "." + cel_UR.getStringCellValue() + "." + String.valueOf((int) cel_F.getNumericCellValue()) + "." + String.valueOf((int) cel_FN.getNumericCellValue()) + "." + sSubFuncion + "." + sProgramaGral + "." + sActividadInstitucional + "." + sProgramaPres + "." + sPartida + "." + String.valueOf((int) cel_TG.getNumericCellValue()) + "." + String.valueOf((int) cel_FF.getNumericCellValue()) + "." + EFederativa + "." + cve_ppi + "." + cel_Unormativa.getStringCellValue() + "." + cel_Uejecutora.getStringCellValue() + "." + cel_SPP.getStringCellValue();
                    epMap.put("nFolioContratoPlurianual", sFolio);
                    epMap.put("nConsecutivo", String.valueOf(renglon));
                    epMap.put("usuario", uLoginCarga);
                    epMap.put("ciclo", String.valueOf((int) cel_CICLO.getNumericCellValue()));
                    epMap.put("r", String.valueOf((int) cel_R.getNumericCellValue()));
                    epMap.put("ur", cel_UR.getStringCellValue());
                    epMap.put("f", String.valueOf((int) cel_F.getNumericCellValue()));
                    epMap.put("fn", String.valueOf((int) cel_FN.getNumericCellValue()));
                    epMap.put("sf", sSubFuncion);
                    epMap.put("rg", sProgramaGral);
                    epMap.put("ai", sActividadInstitucional);
                    epMap.put("cProgramaPres", sProgramaPres);
                    epMap.put("pta", String.valueOf((int) cel_PTA.getNumericCellValue()));
                    epMap.put("tg", String.valueOf((int) cel_TG.getNumericCellValue()));
                    epMap.put("ff", String.valueOf((int) cel_FF.getNumericCellValue()));
                    epMap.put("ef", EFederativa);
                    epMap.put("ep", EP);
                    epMap.put("cve_ppi", cve_ppi);
                    epMap.put("Un", cel_Unormativa.getStringCellValue());
                    epMap.put("Ue", cel_Uejecutora.getStringCellValue());
                    epMap.put("nImporteMinimo", String.valueOf(new BigDecimal(cel_IMPORTEMINIMO.getNumericCellValue())));
                    if ("Abierto".equals(tipo)) {
                        epMap.put("nImporteMaximo", String.valueOf(new BigDecimal(cel_IMPORTEMAXIMO.getNumericCellValue())));
                    } else {
                        epMap.put("nImporteMaximo", String.valueOf(new BigDecimal(cel_IMPORTEMINIMO.getNumericCellValue())));
                    }
                    epMap.put("bValidaContraMinimo", valContra.equals("MINIMO") ? "1" : "0");
                    epMap.put("spp", cel_SPP.getStringCellValue());
                    //Aqui validamos el tipo de gasto sea siempre el mismo en todo los renglones
                    if (renglon == 1) {
                        iClasificaGasto = (int) cel_TG.getNumericCellValue();
                        iClasificaGastoTemp = (int) cel_TG.getNumericCellValue();
                    } else if (renglon > 1) {
                        //iClasificaGastoTemp = iClasificaGasto;
                        iClasificaGasto = (int) cel_TG.getNumericCellValue();
                        if (iClasificaGastoTemp != iClasificaGasto) {
                            errores.add(" Renglon =" + String.valueOf(renglon) + "  el tipo de gasto no es igual a los anteriores \n");
                        }
                    }
                    //Aqui validamos que todos los renglones sean del mismo capitulo
                    if (renglon == 1) {
                        iCapituloTemp = (Integer.valueOf(sPartida.substring(0, 1)));
                    } else if (renglon > 1) {
                        //	iCapituloTemp =(Integer.valueOf(sPartida.substring(0,1)));
                        if (iCapitulo != iCapituloTemp) {
                            errores.add(" Renglon =" + String.valueOf(renglon) + "  el tipo de Capitulo no es igual a los anteriores \n");
                        }
                    }
                    monto = valContra.equals("MINIMO") ? cel_IMPORTEMINIMO.getNumericCellValue() : cel_IMPORTEMAXIMO.getNumericCellValue();
                    //Para el caso donde se debe
                    double montoModificado = 0;
                    if (nModificacion > 0) {
                        int montoUltimaMod = getMontoModificacion(Integer.parseInt(sFolio), valContra.equals("MINIMO"), renglon - 1);
                        montoModificado = monto - montoUltimaMod;
                    }
                    BigDecimal bigMonto = new BigDecimal(monto);
                    if (errores.size() == 0) {
                        String sSaldoModificado;
                        if ("Plurianualidad".equalsIgnoreCase(tipoSolicitud)) {
                            if (AnioActual == cel_CICLO.getNumericCellValue()) {
                                // Validacion 1.- que la ep del excel exista
                                //Descomentar
                                if (ContratoPlurianualesManager.fn_ValidaEp(conn, EP) < 1) {
                                    errores.add("Validación La Clave Presupuestal '" + EP + "' no existe en el sistema, renglon = " + String.valueOf(renglon));
                                    break;
                                }
                                // Validacion 2.- que la ep del excel cuente  saldo en el modificado
                                SaldoModificado = oP.ConsultaDisponiblexMes(conn, "montoanual", EP, "81102");
                                // Validacion 3.- que la que el saldo Modificado sea mayor o = al solicitado
                                //Descomentar
                                if (nModificacion > 0) {
                                    if (montoModificado > SaldoModificado) {
                                        errores.add("Validación La Clave Presupuestal '" + EP + "' no cuenta con Saldo suficiente para su modificación,   renglon = " + String.valueOf(renglon));
                                        break;
                                    }
                                } else {
                                    if (monto > SaldoModificado) {
                                        errores.add("Validación La Clave Presupuestal '" + EP + "' no cuenta con Saldo suficiente,  renglon = " + String.valueOf(renglon));
                                        break;
                                    }
                                }
                            } else {
                                SaldoModificado = 0.00;
                            }
                            // Agregamos el Importemodificado
                            sSaldoModificado = new DecimalFormat("#.00").format(SaldoModificado);
                            //epMap.put("nImporteModificado", String.valueOf(SaldoModificado));
                            epMap.put("nImporteModificado", sSaldoModificado);
                        } else //Solo año Actual
                        {
                            //Especiales contra anteproyecto del año que viene
                            if (AnioActual + 1 == cel_CICLO.getNumericCellValue()) {
                                if (oP.fn_ValidaEpAnteproyecto(conn, EP, AnioActual + 1) < 1) {
                                    errores.add("Validación La Clave Presupuestal '" + EP + "' no existe en el anteproyecto, renglon = " + String.valueOf(renglon));
                                    break;
                                }
                                SaldoModificado = 0.00;
                                // Agregamos el Importemodificado
                                SaldoModificado = oP.ConsultaAnteproyecto(conn, EP, AnioActual + 1);
                                if (nModificacion > 0) {
                                    if (montoModificado > SaldoModificado) {
                                        errores.add("Validación La Clave Presupuestal '" + EP + "' no cuenta con Saldo suficiente para su modificación,   renglon = " + String.valueOf(renglon));
                                        break;
                                    }
                                } else {
                                    if (monto > SaldoModificado) {
                                        errores.add("Validación La Clave Presupuestal '" + EP + "' no cuenta con Saldo suficiente en el anteproyecto,   renglon = " + String.valueOf(renglon));
                                        break;
                                    }
                                }
                            } else {
                                SaldoModificado = 0.00;
                            }
                            sSaldoModificado = new DecimalFormat("#.00").format(SaldoModificado);
                            //epMap.put("nImporteModificado", String.valueOf(SaldoModificado));
                            epMap.put("nImporteModificado", sSaldoModificado);
                        }
                        //Verificando cuando sea Capitulo 6 Solo Obra Publica
                        if (iCapitulo == 6) {
                            if (sPartida == "62903" || sPartida == "62905") {
                                epMap.put("cIdObra", "Super");
                                dCons = monto;
                                dSuper = 0;
                            } else {
                                dCons = 0;
                                dSuper = monto;
                                epMap.put("Construccion", String.valueOf(bigMonto));
                            }
                        } else {
                            epMap.put("cIdObra", "");
                            dCons = 0;
                            dSuper = 0;
                        }
                        epMap.put("Supervision", String.valueOf(new BigDecimal(dCons)));
                        epMap.put("Construccion", String.valueOf(new BigDecimal(dSuper)));
                        ///////////////////
                        int iExiste = BusquedaAnio(Anios, AnioIterado);
                        MatrizAnual[iExiste][0] = AnioIterado;
                        MatrizAnual[iExiste][1] = MatrizAnual[iExiste][1] + monto;
                        MatrizAnual[iExiste][2] = MatrizAnual[iExiste][2] + SaldoModificado;
                        MatrizAnual[iExiste][3] = MatrizAnual[iExiste][3] + dCons;
                        MatrizAnual[iExiste][4] = MatrizAnual[iExiste][4] + dSuper;
                    }
                    if (errores.size() == 0) {
                        log.debug("Object: " + String.valueOf("Procesando renglon " + String.valueOf(renglon++) + " Año:" + String.valueOf((int) cel_CICLO.getNumericCellValue())));
                        ContratoPlurianualesManager.insertaRenglon(conn, epMap, "tContratoPlurianual_EP");
                    }
                }
            }
            if (errores.size() == 0) {
                //insertamos la Suma de montos Anuales obtenidos de la matriz anterior
                //MapMontoAnual
                String sSaldoModificado;
                for (int i = 0; i < RENGLON; ++i) {
                    if (MatrizAnual[i][0] > 0) {
                        MapMontoAnual.put("nFolioContratoPlurianual", sFolio);
                        MapMontoAnual.put("anio", String.valueOf((int) MatrizAnual[i][0]));
                        MapMontoAnual.put("nImporte", String.valueOf(new BigDecimal(MatrizAnual[i][1])));
                        sSaldoModificado = new DecimalFormat("#.00").format(MatrizAnual[i][2]);
                        //MapMontoAnual.put("nImporteModificado",   String.valueOf(MatrizAnual[i][2]));
                        MapMontoAnual.put("nImporteModificado", String.valueOf(new BigDecimal(sSaldoModificado)));
                        MapMontoAnual.put("nImporteConstruccion", String.valueOf(new BigDecimal(MatrizAnual[i][3])));
                        MapMontoAnual.put("nImporteSupervision", String.valueOf(new BigDecimal(MatrizAnual[i][4])));
                        //	log.debug("Procesando renglon " +  String.valueOf(renglon++) + " Año:" +  String.valueOf((int) cel_CICLO.getNumericCellValue()));
                        ContratoPlurianualesManager.insertaRenglon(conn, MapMontoAnual, "tContratoPlurianualMontosAnuales");
                    } else
                        break;
                }
            }
            if (errores.size() == 0) {
                conn.commit();
            } else
                conn.rollback();
            errores.add(tipo);
            errores.add(tipoSolicitud);
            errores.add(valContra);
            return errores;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.debug("Error occurred", "Error: problemas al leer archivo funcion: ContratoPlurianualBussinesLogic.cargaExcelPlurianual_EP :" + e);
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
            sExcel.close();
        }
    }

    private ArrayList<String> validaciones(String ur, int renglon, FormulaEvaluator evaluator, String tipo, Cell cel_CICLO, Cell cel_R, Cell cel_UR, Cell cel_F, Cell cel_FN, Cell cel_SF, Cell cel_RG, Cell cel_AI, Cell cel_M, Cell cel_PP, Cell cel_PTA, Cell cel_TG, Cell cel_FF, Cell cel_EF, Cell cel_CVEPPI, Cell cel_Unormativa, Cell cel_Uejecutora, Cell cel_SPP, Cell cel_IMPORTEMINIMO, Cell cel_IMPORTEMAXIMO) {
        ArrayList<String> errores = new ArrayList<String>();
        if (fn_ValidaCelda(cel_CICLO, evaluator, 0))
            errores.add("  La columna Ciclo debe ser numerico y no puede ir vacia");
        if (fn_ValidaCelda(cel_R, evaluator, 1))
            errores.add("  La columna RG corresponde al Ramo, debe ser numerico  y no puede ir vacia");
        if (fn_ValidaCelda(cel_UR, evaluator, 2))
            errores.add("  La columna UR corresponde a la Unidad Responsable, debe ser de tipo Cadena y no puede ir vacia");
        if (fn_ValidaCelda(cel_F, evaluator, 3))
            errores.add("  La columna F corresponde a la Grupo Funcional, debe ser numerico  y no puede ir vacia");
        if (fn_ValidaCelda(cel_FN, evaluator, 4))
            errores.add("  La columna FN corresponde a la Funcion, debe ser numerico  y no puede ir vacia");
        if (fn_ValidaCelda(cel_SF, evaluator, 5))
            errores.add("  La columna SF corresponde a la SubFuncion, debe ser numerico  y no puede ir vacia");
        if (fn_ValidaCelda(cel_RG, evaluator, 6))
            errores.add("  La columna RG corresponde al Programa General, debe ser numerico  y no puede ir vacia");
        if (fn_ValidaCelda(cel_AI, evaluator, 7))
            errores.add("  La columna AI corresponde a la Actividad Institucional, debe ser numerico  y no puede ir vacia");
        if (fn_ValidaCelda(cel_M, evaluator, 8))
            errores.add("  La columna M corresponde al Programa Presupuestario, debe ser de tipo Cadena y no puede ir vacia");
        if (fn_ValidaCelda(cel_PP, evaluator, 9))
            errores.add("  La columna PP forma parte del Programa Presupuestario, debe ser numerico  y no puede ir vacia");
        if (fn_ValidaCelda(cel_PTA, evaluator, 10))
            errores.add("  La columna PTA corresponde a la Partida, debe ser numerico  y no puede ir vacia");
        if (fn_ValidaCelda(cel_TG, evaluator, 11))
            errores.add("  La columna TG corresponde al tipo Gasto, debe ser numerico  y no puede ir vacia");
        if (fn_ValidaCelda(cel_FF, evaluator, 12))
            errores.add("  La columna FF corresponde a la Fuente de Financiamiento, debe ser numerico  y no puede ir vacia");
        if (fn_ValidaCelda(cel_EF, evaluator, 13))
            errores.add("  La columna EF corresponde a la Entidad Federativa, debe ser numerico  y no puede ir vacia");
        if (fn_ValidaCelda(cel_CVEPPI, evaluator, 14))
            errores.add("  La columna CVE PPI corresponde a la Cartera, debe ser de tipo Cadena y no puede ir vacia");
        if (fn_ValidaCelda(cel_Unormativa, evaluator, 15))
            errores.add("  La columna UN corresponde a la Unidad Normativa, debe ser de tipo Cadena y no puede ir vacia");
        if (fn_ValidaCelda(cel_Uejecutora, evaluator, 16))
            errores.add("  La columna UE corresponde a la Unidad Ejecutora, debe ser de tipo Cadena y no puede ir vacia");
        if (fn_ValidaCelda(cel_SPP, evaluator, 17))
            errores.add("  La columna SPP corresponde al Sub Programa Presupuestario, debe ser de tipo Cadena y no puede ir vacia");
        if (fn_ValidaCelda(cel_IMPORTEMINIMO, evaluator, 18))
            errores.add("  La columna Importe minimo, debe ser de tipo numerico y no puede ir vacio");
        if ("Abierto".equals(tipo)) {
            if (fn_ValidaCelda(cel_IMPORTEMAXIMO, evaluator, 19))
                errores.add("  La columna Importe maximo, debe ser de tipo numerico y no puede ir vacio");
        }
        if (!"B03".equals(ur) && !ur.equals(cel_Uejecutora.getStringCellValue())) {
            errores.add(" La unidad ejecutora del renglon =" + String.valueOf(renglon) + " no corresponde con la Unidad Responsable del usuario \n");
        }
        return errores;
    }

    private void eliminaContratosCargado(String sFolio, Connection conn, ContratoPlurianualesManager oP) throws Exception {
        oP.fn_EliminaContratoPlurianualDetalle(conn, sFolio);
        oP.EliminaContratoPlurianual_EP(conn, sFolio);
        oP.fn_EliminaContratoPlurianualEncabezado(conn, sFolio);
        oP.fn_EliminatContratoPlurianualMontosAnuales(conn, sFolio);
    }

    public boolean fn_ValidaCelda(Cell cell, FormulaEvaluator evaluator, int nCampo) {
        boolean bValida = false;
        if (cell != null) {
            switch(cell.getCellType()) {
                case NUMERIC:
                    if (nCampo == 0 || nCampo == 1 || nCampo == 3 || nCampo == 4 || nCampo == 5 || nCampo == 6 || nCampo == 7 || nCampo == 9 || nCampo == 10 || nCampo == 12 || nCampo == 11 || nCampo == 13 || nCampo == 14 || nCampo == 18 || nCampo == 19) {
                        bValida = false;
                    } else {
                        bValida = true;
                    }
                    // System.out.println(cell.getNumericCellValue());
                    break;
                case STRING:
                    if (nCampo == 2 || nCampo == 3 || nCampo == 4 || nCampo == 8 || nCampo == 14 || nCampo == 15 || nCampo == 16 || nCampo == 17 || nCampo == 20) {
                        bValida = false;
                    } else {
                        bValida = true;
                    }
                    //System.out.println(cell.getStringCellValue());
                    break;
                case BLANK:
                    break;
                case ERROR:
                    //System.out.println(cell.getErrorCellValue());
                    bValida = true;
                    break;
                //    case Cell.CELL_TYPE_BOOLEAN:
                //        System.out.println(cell.getBooleanCellValue());
                //        break;
                // CELL_TYPE_FORMULA will never occur
                case FORMULA:
                    bValida = true;
                    break;
            }
        } else {
            bValida = true;
        }
        if (nCampo == 20 && bValida == true) {
            if (cell.getStringCellValue().equals("MAXIMO") || cell.getStringCellValue().equals("MINIMO")) {
                bValida = true;
            } else {
                bValida = false;
            }
        }
        return bValida;
    }

    public double fn_VerificaSaldoMes(String nombreMes, String sEp) throws Exception {
        ContratoPlurianualesManager objMan = new ContratoPlurianualesManager();
        double dTotal = 0;
        Connection conn = null;
        try {
            conn = getConnection();
            dTotal = objMan.ConsultaDisponiblexMes(conn, nombreMes, sEp, "82106");
            return dTotal;
            //return dTotal = fn_FromatoDec(dTotal,2);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.debug("Error occurred", "Error: problemas ContratoPlurianualBussinesLogic.fn_VerificaSaldoMes:" + e);
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public int fn_ObtenConsecutivo(int iAnio, String sFolio) throws Exception {
        ContratoPlurianualesManager objMan = new ContratoPlurianualesManager();
        int iConsecutivo = 0;
        Connection conn = null;
        try {
            conn = getConnection();
            return iConsecutivo = ContratoPlurianualesManager.ConsultaConsecutivo(conn, sFolio);
        } catch (Exception e) {
            log.debug("Error occurred", "Error: problemas ContratoPlurianualBussinesLogic.fn_ObtenConsecutivo :" + e);
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public boolean fn_InsertaPluirianualApartado(Connection conn, int iAnio, String sFolio, String sEP, String sEvento, String sCC, String sMes, double nImporte, double dImporteMod) throws Exception {
        double dImpoNeg = 0;
        int iConsecutivo = 0;
        nImporte = fn_FromatoDec(nImporte, 2);
        dImpoNeg = fn_FromatoDec(-nImporte, 2);
        dImporteMod = fn_FromatoDec(dImporteMod, 2);
        iConsecutivo = ContratoPlurianualesManager.ConsultaConsecutivo(conn, sFolio);
        try {
            return ContratoPlurianualesManager.InsertContratoPlurianualApartado(conn, sFolio, iConsecutivo, sEP, String.valueOf(iAnio), sEvento, String.valueOf(nImporte), String.valueOf(dImpoNeg), sCC, sMes, String.valueOf(dImporteMod));
        } catch (Exception e) {
            log.debug("Error occurred", "Error: problemas ContratoPlurianualBussinesLogic.fn_InsertaPluirianualApartado :" + e);
            throw e;
        }
    }

    public boolean fn_updateEpImportes(Connection conn, String sFolio, String sEP, double nConst, double nSup) throws Exception {
        try {
            return ContratoPlurianualesManager.fn_updateEp(conn, sFolio, sEP, nConst, nSup);
        } catch (Exception e) {
            log.debug("Error occurred", "Error: problemas ContratoPlurianualBussinesLogic.fn_updateEp :" + e);
            throw e;
        }
    }

    public static double fn_FromatoDec(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static int BusquedaAnio(int[] vector, int Elem) {
        int Valor = 0;
        int UltimoElemento = 0;
        for (int i = 0; i < vector.length; ++i) {
            Valor = vector[i];
            if (Valor == 0) {
                vector[i] = Elem;
                UltimoElemento = i;
                break;
            } else if (Valor == Elem) {
                UltimoElemento = i;
                break;
            }
        }
        return UltimoElemento;
    }

    public void AbrirCerrar(String pluriNormal, String pluriEspecial, String usuario) throws SQLException {
        Connection conn = null;
        Boolean pluriNormalBool;
        Boolean pluriEspecialBool;
        if (pluriNormal.equals("abierto"))
            pluriNormalBool = false;
        else
            pluriNormalBool = true;
        if (pluriEspecial.equals("abierto"))
            pluriEspecialBool = false;
        else
            pluriEspecialBool = true;
        try {
            conn = getConnection();
            ContratoPlurianualesManager.AbrirCerrar(conn, pluriNormalBool, pluriEspecialBool, usuario);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            conn.rollback();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
    }

    public String CancelacionMasiva(String usuario, HttpServletRequest request) throws SQLException {
        Connection conn = null;
        String mensaje = "";
        try {
            conn = getConnection();
            mensaje = ContratoPlurianualesManager.CancelacionMasiva(conn, usuario, request);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            conn.rollback();
            mensaje = exc.getMessage();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return mensaje;
    }

    public List<String> listadoModificaciones(String sFolio) throws Exception {
        List<String> nModificaciones = new ArrayList<String>();
        Connection conn = null;
        try {
            conn = getConnection();
            ContratoPlurianualesManager CPM = new ContratoPlurianualesManager();
            nModificaciones = CPM.ConsultaModificaciones(conn, sFolio);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return nModificaciones;
    }

    public String getAñoFinal(String sFolio) throws Exception {
        String año = "";
        Connection conn = null;
        try {
            conn = getConnection();
            ContratoPlurianualesManager CPM = new ContratoPlurianualesManager();
            año = CPM.ConsultaAñoFinal(conn, sFolio);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return año;
    }

    public String getAñoInicial(String sFolio) throws Exception {
        String año = "";
        Connection conn = null;
        try {
            conn = getConnection();
            ContratoPlurianualesManager CPM = new ContratoPlurianualesManager();
            año = CPM.ConsultaAñoInicio(conn, sFolio);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return año;
    }

    public int getPlurianualesBloqueado(String tipo) throws Exception {
        int bloqueo = 1;
        Connection conn = null;
        try {
            conn = getConnection();
            ContratoPlurianualesManager CPM = new ContratoPlurianualesManager();
            bloqueo = CPM.ConsultaPluBloqueados(conn, tipo);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return bloqueo;
    }

    private String validarModificacion(int nFolio, int nModificacion) throws SQLException {
        String respuesta = "";
        Connection conn = null;
        try {
            conn = getConnection();
            ContratoPlurianualesManager CPM = new ContratoPlurianualesManager();
            int ultimoMonto;
            int montoOriginal;
            montoOriginal = CPM.getMontoTotal(conn, nFolio, false, nModificacion);
            ultimoMonto = CPM.getMontoTotal(conn, nFolio, true, nModificacion);
            if (ultimoMonto < montoOriginal) {
                respuesta = "El monto total de la modificación no puede ser menor al monto total original";
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return respuesta;
    }

    public ArrayList<String> validarModificaciones(Workbook workbook, Sheet sheet, String sFolio, String ur, int nModificacion) throws Exception {
        Connection conn = null;
        ArrayList<String> errores = new ArrayList<String>();
        try {
            conn = getConnection();
            int nFolio = Integer.parseInt(sFolio);
            //ContratoPlurianualesManager oP = new ContratoPlurianualesManager();
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            Iterator<Row> rowIterator = sheet.iterator();
            String tipo = "";
            String tipoSolicitud = "";
            String valContra = getValidarContra(nFolio, nModificacion);
            ArrayList<String> lstEps = getEPModificacion(nFolio, nModificacion);
            int indice = -1;
            int tipoGasto = getTipoGasto(nFolio);
            int ultimoTotal = 0;
            int totalInicial = getTotalInicial(nFolio);
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() == 0) {
                    tipo = row.getCell(1).getStringCellValue();
                    if (!("Abierto").equals(tipo) && !("Cerrado").equals(tipo)) {
                        errores.add("El campo tipo debe de ser Abierto &oacute; Cerrado");
                    }
                    tipoSolicitud = row.getCell(3).getStringCellValue();
                    if (!("Plurianualidad").equals(tipoSolicitud) && !("Especial").equals(tipoSolicitud)) {
                        errores.add("El campo solicitud debe de ser Plurianual &oacute; Especial");
                    }
                    if (getPlurianualesBloqueado(tipoSolicitud) == 1) {
                        errores.add("Este tipo de plurianualidades se encuentran bloqueadas");
                        return errores;
                    }
                    if (tipo.equals("Abierto")) {
                        Cell validarContra = row.getCell(5);
                        if (fn_ValidaCelda(validarContra, evaluator, 20)) {
                            errores.add("  La columna valida contra, debe ser 'MINIMO' o 'MAXIMO'");
                        } else {
                            valContra = validarContra.getStringCellValue();
                        }
                    }
                    continue;
                } else if (row.getRowNum() == 1) {
                    continue;
                }
                if (Util.renglonVacio(row)) {
                    continue;
                }
                indice++;
                // año
                Cell cel_CICLO = row.getCell(0);
                Cell cel_R = row.getCell(1);
                Cell cel_UR = row.getCell(2);
                Cell cel_F = row.getCell(3);
                Cell cel_FN = row.getCell(4);
                Cell cel_SF = row.getCell(5);
                Cell cel_RG = row.getCell(6);
                Cell cel_AI = row.getCell(7);
                Cell cel_M = row.getCell(8);
                Cell cel_PP = row.getCell(9);
                // PARTIDA
                Cell cel_PTA = row.getCell(10);
                Cell cel_TG = row.getCell(11);
                Cell cel_FF = row.getCell(12);
                Cell cel_EF = row.getCell(13);
                Cell cel_CVEPPI = row.getCell(14);
                Cell cel_Unormativa = row.getCell(15);
                Cell cel_Uejecutora = row.getCell(16);
                Cell cel_SPP = row.getCell(17);
                Cell cel_IMPORTEMINIMO = row.getCell(18);
                Cell cel_IMPORTEMAXIMO = row.getCell(19);
                //Validacion de igualdad
                String ep = concatenaEp(cel_CICLO, cel_R, cel_UR, cel_F, cel_FN, cel_SF, cel_RG, cel_AI, cel_M, cel_PTA, cel_TG, cel_FF, cel_EF, cel_CVEPPI, cel_Unormativa, cel_Uejecutora, cel_SPP, cel_PP);
                System.out.println(row.getRowNum() + " ---" + ep + " vs " + lstEps.get(indice));
                if (!ep.equals(lstEps.get(indice))) {
                    errores.add("La ep no." + row.getRowNum() + " no corresponde con la carga original");
                }
                //Validacion de Montos
                if ("C".equals(valContra)) {
                    //Valida contra cerrado
                    if ((int) cel_IMPORTEMINIMO.getNumericCellValue() < getMontoModificacion(nFolio, true, indice)) {
                        //Monto mayor
                        errores.add("El monto de la ep no." + row.getRowNum() + " es menor al original ");
                    }
                    ultimoTotal = ultimoTotal + (int) cel_IMPORTEMINIMO.getNumericCellValue();
                } else {
                    //Valida contra Abierto
                    if (isValMinimo(nFolio)) {
                        //Monto Minimo
                        if ((int) cel_IMPORTEMINIMO.getNumericCellValue() < getMontoModificacion(nFolio, true, indice)) {
                            errores.add("El monto de la ep no." + row.getRowNum() + " es menor al original ");
                        }
                        ultimoTotal = ultimoTotal + (int) cel_IMPORTEMINIMO.getNumericCellValue();
                    } else {
                        //Monto Maximo
                        if ((int) cel_IMPORTEMAXIMO.getNumericCellValue() < getMontoModificacion(nFolio, false, indice)) {
                            errores.add("El monto de la ep no." + row.getRowNum() + " es menor al original ");
                        }
                        ultimoTotal = ultimoTotal + (int) cel_IMPORTEMAXIMO.getNumericCellValue();
                    }
                }
            }
            indice++;
            //Validacion de cantidad
            if (indice != lstEps.size()) {
                errores.add("La cantidad de eps cargadas no corresponde con la cantidad original(" + lstEps.size() + ")");
            }
            errores.add(isPorcentajeValido(BigInteger.valueOf(ultimoTotal), BigInteger.valueOf(totalInicial), tipoGasto));
            if ("".equals(errores.get(errores.size() - 1))) {
                errores.remove(errores.size() - 1);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.debug("Error occurred", "Error: problemas al leer archivo funcion: ContratoPlurianualBussinesLogic.cargaExcelPlurianual_EP :" + e);
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
        return errores;
    }

    private String concatenaEp(Cell cel_CICLO, Cell cel_R, Cell cel_UR, Cell cel_F, Cell cel_FN, Cell cel_SF, Cell cel_RG, Cell cel_AI, Cell cel_M, Cell cel_PTA, Cell cel_TG, Cell cel_FF, Cell cel_EF, Cell cel_CVEPPI, Cell cel_Unormativa, Cell cel_Uejecutora, Cell cel_SPP, Cell cel_PP) {
        String cve_ppi;
        if (cel_CVEPPI == null) {
            cve_ppi = "00000000000";
        } else {
            if (cel_CVEPPI.getCellType() == CellType.NUMERIC)
                cve_ppi = String.valueOf((int) cel_CVEPPI.getNumericCellValue());
            else
                cve_ppi = cel_CVEPPI.getStringCellValue();
            if (cve_ppi.equals("0")) {
                cve_ppi = "00000000000";
            }
        }
        String sSubFuncion = String.valueOf((int) cel_SF.getNumericCellValue());
        if (sSubFuncion.length() == 1)
            sSubFuncion = "0" + sSubFuncion;
        String sProgramaGral = String.valueOf((int) cel_RG.getNumericCellValue());
        if (sProgramaGral.length() == 1)
            sProgramaGral = "0" + sProgramaGral;
        String sActividadInstitucional = String.valueOf((int) cel_AI.getNumericCellValue());
        if (sActividadInstitucional.length() == 1)
            sActividadInstitucional = "00" + sActividadInstitucional;
        if (sActividadInstitucional.length() == 2)
            sActividadInstitucional = "0" + sActividadInstitucional;
        String sPartida = String.valueOf((int) cel_PTA.getNumericCellValue());
        String pp = String.valueOf((int) cel_PP.getNumericCellValue());
        String sProgramaPres = cel_M.getStringCellValue();
        String EFederativa = String.valueOf((int) cel_EF.getNumericCellValue());
        if (pp.length() == 1)
            pp = "00" + pp;
        if (pp.length() == 2)
            pp = "0" + pp;
        sProgramaPres = sProgramaPres + pp;
        if (EFederativa.length() == 1)
            EFederativa = "0" + EFederativa;
        String EP = String.valueOf((int) cel_CICLO.getNumericCellValue()) + "." + String.valueOf((int) cel_R.getNumericCellValue()) + "." + cel_UR.getStringCellValue() + "." + String.valueOf((int) cel_F.getNumericCellValue()) + "." + String.valueOf((int) cel_FN.getNumericCellValue()) + "." + sSubFuncion + "." + sProgramaGral + "." + sActividadInstitucional + "." + sProgramaPres + "." + sPartida + "." + String.valueOf((int) cel_TG.getNumericCellValue()) + "." + String.valueOf((int) cel_FF.getNumericCellValue()) + "." + EFederativa + "." + cve_ppi + "." + cel_Unormativa.getStringCellValue() + "." + cel_Uejecutora.getStringCellValue() + "." + cel_SPP.getStringCellValue();
        return EP;
    }

    public ArrayList<String> getEPModificacion(int nfolio, int nModificacion) throws Exception {
        ArrayList<String> lstEps = new ArrayList<String>();
        Connection conn = null;
        try {
            conn = getConnection();
            ContratoPlurianualesManager CPM = new ContratoPlurianualesManager();
            lstEps = CPM.getEPByModificiacion(conn, nModificacion, nfolio);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return lstEps;
    }

    public ArrayList<String> getMontoModificacion(int nfolio, int nModificacion) throws Exception {
        ArrayList<String> lstEps = new ArrayList<String>();
        Connection conn = null;
        try {
            conn = getConnection();
            ContratoPlurianualesManager CPM = new ContratoPlurianualesManager();
            lstEps = CPM.getEPByModificiacion(conn, nModificacion, nfolio);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return lstEps;
    }

    public String getValidarContra(int nfolio, int nModificacion) throws Exception {
        String tipo = "";
        Connection conn = null;
        try {
            conn = getConnection();
            ContratoPlurianualesManager CPM = new ContratoPlurianualesManager();
            tipo = CPM.getValidarContra(conn, nModificacion, nfolio);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return tipo;
    }

    public int getMontoModificacion(int nfolio, boolean montoMinimo, int nCosecutivo) throws Exception {
        int monto = 0;
        Connection conn = null;
        try {
            conn = getConnection();
            ContratoPlurianualesManager CPM = new ContratoPlurianualesManager();
            monto = CPM.getMontoModificacion(conn, nfolio, montoMinimo, nCosecutivo);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return monto;
    }

    public boolean isValMinimo(int nfolio) throws Exception {
        boolean isMimimo = false;
        Connection conn = null;
        try {
            conn = getConnection();
            ContratoPlurianualesManager CPM = new ContratoPlurianualesManager();
            isMimimo = CPM.isValMinimo(conn, nfolio);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return isMimimo;
    }

    public int getTipoGasto(int nfolio) throws Exception {
        int tipo = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            ContratoPlurianualesManager CPM = new ContratoPlurianualesManager();
            tipo = CPM.getTipoGasto(conn, nfolio);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return tipo;
    }

    public String isPorcentajeValido(BigInteger ultimoTotal, BigInteger totalInicial, int tipoGasto) {
        String error = "";
        BigInteger var1 = ultimoTotal.multiply(new BigInteger("100"));
        BigInteger div = var1.divide(totalInicial);
        BigInteger porcentaje = div.add(new BigInteger("-100"));
        int comparacion = porcentaje.compareTo(new BigInteger("25"));
        int comparacion2 = porcentaje.compareTo(new BigInteger("20"));
        if (tipoGasto == 3 && comparacion == 1) {
            //Obra
            error = " El total de los montos supera el veinticinco por ciento establecido ";
        } else if (tipoGasto != 3 && comparacion2 == 1) {
            error = " El total de los montos supera el veinte por ciento establecido ";
        }
        return error;
    }

    public int getTotalInicial(int nFolio) throws Exception {
        int tipo = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            ContratoPlurianualesManager CPM = new ContratoPlurianualesManager();
            tipo = CPM.getTotalInicial(conn, nFolio);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return tipo;
    }

    public ArrayList<Double> getDiferenciaMontos(int nFolio, int ejercicio, int nModificacion) throws Exception {
        ArrayList<Double> lstDiferencia = new ArrayList<Double>();
        Connection conn = null;
        try {
            conn = getConnection();
            ContratoPlurianualesManager CPM = new ContratoPlurianualesManager();
            lstDiferencia = CPM.getDiferenciaMontos(conn, nFolio, ejercicio, nModificacion);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return lstDiferencia;
    }
}
// fin de la clase
