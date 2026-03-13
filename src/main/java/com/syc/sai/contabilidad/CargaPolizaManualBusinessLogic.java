package com.syc.sai.contabilidad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CargaPolizaManualBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(CargaPolizaManualBusinessLogic.class);

    private String uLoginCarga = "";

    public double TotalCargo = 0;

    public double TotalAbono = 0;

    Calendar fecha = new GregorianCalendar();

    Map<String, String> DataDetailPolMap = null;

    /**
     * Construye una nueva instancia del objeto.
     */
    public CargaPolizaManualBusinessLogic(String uLogin) {
        super.init();
        this.uLoginCarga = uLogin;
    }

    /**
     * Carga el archivo y lo almacena en la base de datos.
     */
    public List<String> cargaExcelPolizaManual(String nombreDestino, String nFolioDocumento, String cCentroContable, String EjercicioFiscal) throws Exception {
        File f = null;
        FileInputStream stream = null;
        try {
            f = new File(nombreDestino);
            if (!f.exists())
                throw new FileNotFoundException("No se encontro el archivo de carga[ " + nombreDestino + "]");
            stream = new FileInputStream(f);
            return cargaExcelPolizaManual(stream, nFolioDocumento, cCentroContable, EjercicioFiscal);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            if (stream != null)
                try {
                    stream.close();
                } catch (Exception e2) {
                    log.warn("No se pudo cerrar el archivo de carga." + e2);
                }
            stream = null;
        }
    }

    @SuppressWarnings("null")
    public synchronized List<String> cargaExcelPolizaManual(InputStream stream, String FolioDocumento, String CentroContable, String EjercicioFiscal) throws Exception {
        List<String> errores = new ArrayList<String>();
        Map<String, String> renglonMap = null;
        Connection conn = null;
        boolean renglonInsertada = true;
        try {
            conn = getConnection();
            Workbook workbook = new HSSFWorkbook(stream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            /* Descarta el encabezado (Renglon 1) */
            rowIterator.next();
            int renglon = 1;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                renglon++;
                Cell CellnCuenta = row.getCell(0);
                Cell CellcSubCuenta = row.getCell(1);
                Cell CellnCargo = row.getCell(2);
                Cell CellnAbono = row.getCell(3);
                int tipoCelda;
                /*Extrae la primer columna. Si esta vacia, salta a la siguiente fila*/
                //log.info("valor de tipo de celda Subcuenta "+CellcSubCuenta.getCellType());
                if (// si viene un renglon en blanco termina de recorrer el excel
                CellnCuenta == null && CellcSubCuenta == null || (CellnCargo == null || CellnAbono == null))
                    break;
                if (CellcSubCuenta == null) {
                    tipoCelda = 0;
                } else {
                    tipoCelda = 1;
                }
                log.info("valores del renglon: " + renglon + "  Tipo de celda en subcuenta: " + CellcSubCuenta + "  Cuenta: " + CellnCuenta + "  SubCuenta: " + CellcSubCuenta + "  Cargo: " + CellnCargo.getNumericCellValue() + "  Abono :" + CellnAbono.getNumericCellValue());
                if (CellnCuenta != null) {
                    log.debug("Procesando renglon " + (renglon));
                    try {
                        String nCuenta = CellnCuenta.getStringCellValue();
                        String cSubCuenta = " ";
                        String nCargo = " ";
                        String nAbono = " ";
                        String Reg;
                        // VALIDACIONES DE TIPOS DE DATOS EN EL EXCEL
                        if (//tipo de celda 3 es que viene vacio como formato general y 1 viene vacio como formato texto
                        CellcSubCuenta == null || CellcSubCuenta.getCellType() == CellType.BLANK || (CellcSubCuenta.getCellType() == CellType.STRING && CellcSubCuenta == null))
                            cSubCuenta = "x";
                        else {
                            if (//tipo celda 0= es numerico y aplica para partidas y obgt
                            CellcSubCuenta.getCellType() == CellType.NUMERIC) {
                                //es celda de partida
                                String obgt = CellcSubCuenta.toString();
                                //log.info(obgt);
                                String[] partida = obgt.split("\\.");
                                //log.info("partida: "+partida[0]);
                                cSubCuenta = partida[0];
                                //log.info("valor de celda partida: "+cSubCuenta);
                            } else {
                                cSubCuenta = CellcSubCuenta.toString();
                            }
                            //	}//fin del if tipocelda
                        }
                        //fin del else
                        if (CellnCargo.getNumericCellValue() == 0.0 && CellnAbono != null) {
                            nCargo = "0.0";
                            nAbono = CellnAbono.toString();
                        } else if (CellnCargo != null && (CellnAbono.getNumericCellValue() == 0.0 || "0.0".equals(CellnAbono.getNumericCellValue()))) {
                            nCargo = CellnCargo.toString();
                            nAbono = "0.0";
                        }
                        renglonMap = new HashMap<String, String>();
                        renglonMap.put("nCuenta", nCuenta);
                        renglonMap.put("nSubCuenta", cSubCuenta);
                        renglonMap.put("nCargo", nCargo);
                        renglonMap.put("nAbono", nAbono);
                        renglonMap.put("cCentroContable", CentroContable);
                        renglonMap.put("nFolioDocumento", FolioDocumento);
                        log.debug("nCuenta[" + nCuenta + "] cSubCuenta[" + cSubCuenta + "] nCargo[" + nCargo + "] nAbono[" + nAbono + "]  cCentroContable[" + CentroContable + "]  FolioDocumento[" + FolioDocumento + "]");
                        //SE MANDA A VALIDAR EL VALOR DE LA SUBCUENTA PARA SEGUIR INSERTANDO REGISTRO O EN SU CASO HACER EL ROLLBACK
                        Reg = CargaPolizaManualManager.validaRenglon(conn, nCuenta, cSubCuenta, renglon);
                        if (Reg == "ok") {
                            TotalCargo = (double) (TotalCargo + Double.parseDouble(nCargo));
                            TotalAbono = (double) (TotalAbono + Double.parseDouble(nAbono));
                            CargaPolizaManualManager.insertaRenglonPol(conn, renglonMap, renglon);
                        } else {
                            //UN error
                            //errores.add("Error en renglon [" + renglon + "]  "+Reg );
                            errores.add(Reg);
                            log.info(Reg);
                            renglonInsertada = false;
                        }
                    } catch (Exception e) {
                        renglonInsertada = false;
                        //errores.add("Error en renglon [" + renglon + "] " + e.toString());
                        errores.add(e.toString());
                        log.info("Error en renglon  [" + renglon + "] " + e.toString());
                    }
                }
            }
            if (renglonInsertada && errores.size() == 0) {
                /// commit para que se inserten todos los renglones del excel
                conn.commit();
                /// SE AGREGAN LOS INSERT PARA QUE SE FORME LA POLIZA MANUAL
            } else {
                TotalCargo = 0;
                TotalAbono = 0;
                conn.rollback();
            }
            workbook.close();
            return errores;
        } catch (Exception e) {
            log.error("Error insertando proyecto " + e, e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("No se pudo realizar rollback en conexion" + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    // fin de carga excel.
    public void generaRegistroDocPoliza(String withD, String Usuario, String FolioDocPoliza, String CentroContable, String FechaCaptura, String FechaAplicacion, String UnidadResponsable, String Mes, String EjercicioFiscal, String Concepto, String CasoOrigen) throws Exception {
        Connection conn = null;
        Map<String, String> DataHeaderPolizaMap = null;
        //Map <String,String> DataDetailPolMap=null;
        Map<String, String> DataBitacoraMap = null;
        DecimalFormat num = new DecimalFormat("#,###.00");
        try {
            //	log.info("mes:"+(fecha.get(Calendar.MONTH)+1)); se le suma un 1 por que el mes enero lo toma como 0
            conn = getConnection();
            DataBitacoraMap = new HashMap<String, String>();
            DataBitacoraMap.put("nFolioDocPoliza", FolioDocPoliza);
            DataBitacoraMap.put("usuario", Usuario);
            //DataBitacoraMap.put("Hora", ""+fecha.get(Calendar.YEAR)+"/0"+(fecha.get(Calendar.MONTH)+1)+"/"+fecha.get(Calendar.DAY_OF_MONTH)+" "+fecha.get(Calendar.HOUR_OF_DAY)+":"+fecha.get(Calendar.MINUTE)+":"+fecha.get(Calendar.SECOND)     );
            DataBitacoraMap.put("Hora", "" + fecha.get(Calendar.DAY_OF_MONTH) + "/" + (fecha.get(Calendar.MONTH) + 1) + "/" + fecha.get(Calendar.YEAR) + " " + fecha.get(Calendar.HOUR_OF_DAY) + ":" + fecha.get(Calendar.MINUTE) + ":" + fecha.get(Calendar.SECOND));
            DataBitacoraMap.put("Descripcion", "Se carga Archivo para poliza");
            //DATOS DE LA TABLA DOCPOLIZAENCABEZADO
            DataHeaderPolizaMap = new HashMap<String, String>();
            DataHeaderPolizaMap.put("nFolioDocPoliza", FolioDocPoliza);
            DataHeaderPolizaMap.put("fCarga", FechaCaptura);
            DataHeaderPolizaMap.put("fAplicacion", FechaAplicacion);
            DataHeaderPolizaMap.put("cCentroContable", CentroContable);
            DataHeaderPolizaMap.put("cRamo", "16");
            DataHeaderPolizaMap.put("cUnidadResponsable", UnidadResponsable);
            DataHeaderPolizaMap.put("cDocumentoHaplicado", null);
            DataHeaderPolizaMap.put("nFolioPoliza", null);
            DataHeaderPolizaMap.put("cTipoPoliza", "DI");
            DataHeaderPolizaMap.put("nMes", Mes);
            DataHeaderPolizaMap.put("cRevisado", " ");
            DataHeaderPolizaMap.put("aEjercicioFiscal", EjercicioFiscal);
            //	DataHeaderPolizaMap.put("cUnidadResponsableContable",UnidadRespContable);
            DataHeaderPolizaMap.put("nFolioPolizaCancelacion", null);
            DataHeaderPolizaMap.put("fCancelacion", null);
            DataHeaderPolizaMap.put("fCancelacion", null);
            DataHeaderPolizaMap.put("cDescripcionPoliza", Concepto);
            DataHeaderPolizaMap.put("cConcepto", Concepto);
            DataHeaderPolizaMap.put("cIdUsuarioCaptura", Usuario);
            DataHeaderPolizaMap.put("cIdUsuarioRevision", null);
            DataHeaderPolizaMap.put("cIdUsuarioAprobacion", null);
            DataHeaderPolizaMap.put("mTotalCargos", num.format(TotalCargo) + "");
            DataHeaderPolizaMap.put("mTotalAbonos", num.format(TotalAbono) + "");
            DataHeaderPolizaMap.put("cTipoDocumento", "POLIZA MANUAL");
            DataHeaderPolizaMap.put("cComentarios", " ");
            DataHeaderPolizaMap.put("nCambio", null);
            DataHeaderPolizaMap.put("nIdCasoOrigen", CasoOrigen);
            DataHeaderPolizaMap.put("Periodo13", "N");
            DataHeaderPolizaMap.put("ADEFAS", "N");
            DataHeaderPolizaMap.put("nTipoAjuste", "0");
            DataHeaderPolizaMap.put("nFormatoPoliza", "1");
            DataHeaderPolizaMap.put("sFirmanteCap", null);
            DataHeaderPolizaMap.put("sPuestoCap", null);
            DataHeaderPolizaMap.put("sFirmanteRev", null);
            DataHeaderPolizaMap.put("sPuestoRev", null);
            DataHeaderPolizaMap.put("sFirmanteAut", null);
            DataHeaderPolizaMap.put("sPuestoRev", null);
            CargaPolizaManualManager.genInsertFromMap(conn, DataBitacoraMap, "tDocPolizaBitacora");
            CargaPolizaManualManager.deleteEncabezado(conn, FolioDocPoliza, CentroContable);
            if ("SI".equals(withD)) {
                //genera el encabezado del docpoliza
                CargaPolizaManualManager.genInsertFromMap(conn, DataHeaderPolizaMap, "tDocPolizaEncabezado");
                conn.commit();
                //genera el detalle
                generaDetalleDocPoliza(conn, FolioDocPoliza, CentroContable, EjercicioFiscal);
            } else {
                CargaPolizaManualManager.genInsertFromMap(conn, DataHeaderPolizaMap, "tDocPolizaEncabezado");
                conn.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generaDetalleDocPoliza(Connection conn, String FolioDocPoliza, String CentroContable, String EjercicioFiscal) throws Exception {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        int renglon = 0;
        String sql = "select nCuenta,case when nSubCuenta='x'then ' ' else nSubCuenta end nSubCuenta ,cCentroContable,nFolioDocumento," + " case when nCargo!=0 AND nAbono=0 then 'CARGO'" + " when nAbono!=0 and nCargo=0 then 'ABONO'" + " END evento," + " case when nCargo!=0 AND nAbono=0 then nCargo" + " when nAbono!=0 and nCargo=0 then nAbono" + " END importe" + " from tCargaPololizaPorLayout where nFolioDocumento=" + FolioDocPoliza + " and cCentroContable='" + CentroContable + "'";
        try {
            log.info("Ejectua query: " + sql);
            pstmnt = conn.prepareStatement(sql);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                renglon++;
                // Va armando el renglon en el mapa del detalle
                DataDetailPolMap = new HashMap<String, String>();
                DataDetailPolMap.put("nFolioDocPoliza", FolioDocPoliza);
                DataDetailPolMap.put("nDocRenglon", Integer.toString(renglon));
                DataDetailPolMap.put("nCuenta", rs.getString("nCuenta"));
                DataDetailPolMap.put("nSubCuenta", rs.getString("nSubCuenta"));
                DataDetailPolMap.put("cEvento", rs.getString("evento"));
                DataDetailPolMap.put("mImporte", rs.getString("importe"));
                DataDetailPolMap.put("cCentroContable", CentroContable);
                DataDetailPolMap.put("cTipoPoliza", "DI");
                DataDetailPolMap.put("aEjercicioFiscal", EjercicioFiscal);
                DataDetailPolMap.put("cConcepto", " ");
                DataDetailPolMap.put("nIdCasoOrigen", null);
                DataDetailPolMap.put("Periodo13", "N");
                DataDetailPolMap.put("ADEFAS", "N");
                DataDetailPolMap.put("nTipoAjuste", "0");
                DataDetailPolMap.put("parcial", "N");
                DataDetailPolMap.put("cCABMS", " ");
                DataDetailPolMap.put("cCUCOP", "0");
                DataDetailPolMap.put("cPartida", " ");
                DataDetailPolMap.put("nIdGrupoEvento", "0");
                DataDetailPolMap.put("nIdSubGrupoEvento", "0");
                DataDetailPolMap.put("cIdEventoManual", " ");
                DataDetailPolMap.put("nNumeroEvento", Integer.toString(renglon));
                CargaPolizaManualManager.genInsertFromMap(conn, DataDetailPolMap, "tDocPolizaDetalle");
            }
            // termina de recorrer los registros del detalle
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        //	return resp;
    }
}
