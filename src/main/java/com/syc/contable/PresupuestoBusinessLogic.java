package com.syc.contable;

import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.SQLException;
//import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Caso;
//import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.PresupuestoManager;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class PresupuestoBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(PresupuestoBusinessLogic.class);

    public PresupuestoBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public ArrayList<String> validaArchivoExcel(String archivo, String ejercicio_fiscal, Caso c, Usuario usuario, Map m, String prefixPath, String u_logion) throws Exception {
        ArrayList<String> arrLResult = new ArrayList<String>();
        //no puede llevar el &aacute porque se corta
        arrLResult.add("Reporte de inconsistencias de EPs vs Catálogos<br>");
        List cellDataList = new ArrayList();
        int IterRegElx = 0;
        int IterCelElx = 0;
        int iRegInsert = 0;
        int iDetallePresup = 0;
        int iNumCelEmti = 0;
        int iNumConfEP = 0;
        String cValosEP = "";
        String cValosEPOld = "";
        String TotPresup;
        double iTotPresup = 0;
        double iTotalSumaPresup = 0;
        double dValor;
        String cValor;
        Connection conn = null;
        String cFileExcel;
        String cCentroContable = "";
        String cUserID = "";
        try {
            conn = getConnection();
            if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
                cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
            }
            cUserID = usuario.getLogin();
            if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
                arrLResult.add("Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.");
                return arrLResult;
            }
            cFileExcel = archivo.substring(0, archivo.length() - 3) + "xls";
            File fFileExcel = new File(archivo.substring(0, archivo.length() - 3) + "xls");
            File fFileOrig = new File(archivo);
            FileChannel in = (new FileInputStream(fFileOrig)).getChannel();
            FileChannel out = (new FileOutputStream(fFileExcel)).getChannel();
            in.transferTo(0, fFileOrig.length(), out);
            in.close();
            out.close();
            FileInputStream fileInputStream = new FileInputStream(cFileExcel);
            POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);
            HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
            HSSFSheet hssfSheet = workBook.getSheetAt(0);
            Iterator rowIterator = hssfSheet.rowIterator();
            int nFolio;
            nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
            String fFechaPaso = c.getCasoDato("FECHA_AP_CONT").getValor();
            PresupuestoManager.insertaPresupuesto(conn, ejercicio_fiscal, nFolio, usuario.getLogin(), cCentroContable, cUserID, fFechaPaso);
            iNumConfEP = PresupuestoManager.maxNumEP(conn, ejercicio_fiscal);
            while (rowIterator.hasNext()) {
                IterRegElx++;
                HSSFRow hssfRow = (HSSFRow) rowIterator.next();
                Iterator iterator = hssfRow.cellIterator();
                List cellTempList = new ArrayList();
                if (IterRegElx == 14) {
                    while (iterator.hasNext()) {
                        IterCelElx++;
                        HSSFCell hssfCell = (HSSFCell) iterator.next();
                        if (IterCelElx == 7) {
                            iTotPresup = hssfCell.getNumericCellValue();
                            break;
                        }
                    }
                }
                if (IterRegElx >= 17) {
                    IterCelElx = 0;
                    iNumCelEmti = 0;
                    System.out.println("linea excel: " + IterRegElx);
                    while (iterator.hasNext()) {
                        IterCelElx++;
                        HSSFCell hssfCell = (HSSFCell) iterator.next();
                        if (IterCelElx <= iNumConfEP) {
                            try {
                                cValor = hssfCell.getStringCellValue();
                            } catch (Exception e) {
                                /*Asume que no esta en formato numerico. Trata de leerlo como numero*/
                                cValor = String.valueOf((int) hssfCell.getNumericCellValue());
                            }
                            if (cValor == "") {
                                iNumCelEmti++;
                            } else {
                                if (IterCelElx == 1) {
                                    if (!ejercicio_fiscal.equals(cValor)) {
                                        arrLResult.add("Error Ejercicio Fiscal No Corresponde con archivo de Carga.");
                                        return (arrLResult);
                                    }
                                } else if (IterCelElx == 15) {
                                    cCentroContable = PresupuestoManager.buscaCentroContable(conn, cValor);
                                }
                                if (cValosEP.length() > 0)
                                    cValosEP = cValosEP + ".";
                                cellTempList = PresupuestoManager.validaEPDetalle(conn, ejercicio_fiscal, IterCelElx, cValor, IterRegElx);
                                cValosEP = cValosEP + cValor;
                            }
                        } else if (IterCelElx > iNumConfEP && cellTempList.isEmpty()) {
                            dValor = hssfCell.getNumericCellValue();
                            iTotalSumaPresup = iTotalSumaPresup + dValor;
                            iDetallePresup++;
                            if (dValor > 0)
                                iRegInsert = PresupuestoManager.insertaDetPresupuesto(conn, ejercicio_fiscal, dValor, cValosEP, nFolio, IterCelElx - 16, iDetallePresup, cCentroContable, cUserID);
                            if (!cValosEPOld.equals(cValosEP)) {
                                cValosEPOld = cValosEP;
                                iRegInsert = PresupuestoManager.insertaCatalogoEP(conn, cValosEP, cCentroContable, cUserID, ejercicio_fiscal);
                            }
                        }
                        if (!cellTempList.isEmpty()) {
                            arrLResult.addAll(cellTempList);
                            System.out.println(cellTempList);
                        }
                        if (iNumCelEmti == iNumConfEP)
                            break;
                        if (IterCelElx >= (iNumConfEP + 12))
                            break;
                        if (IterCelElx >= iNumConfEP && !cellTempList.isEmpty())
                            break;
                    }
                    if (iNumCelEmti == iNumConfEP)
                        break;
                    cValosEP = "";
                }
            }
            if (iTotalSumaPresup != iTotPresup) {
                arrLResult.remove(0);
                arrLResult.add("El archivo de carga no Cuadra en el total autorizado y la suma del detalle.<br>");
                arrLResult.add("El documento de carga de presupuesto no fue generado exitosamente.<br>");
            }
            if (arrLResult.size() > 1) {
                for (int i = 0; i < arrLResult.size(); i++) {
                    System.out.println(arrLResult.get(i).toString());
                }
                conn.rollback();
            } else {
                arrLResult.remove(0);
                arrLResult.add("El archivo de carga no presenta inconsistencias.<br>");
                arrLResult.add("El documento de carga de presupuesto fue generado exitosamente.<br>");
                //despues se hace la aplicacion contable
                ContableInterface conInt = new AplicacionContable();
                //arrLResult.addAll(conInt.aplicarContable(conn, c, "", "", "", 0, "", m, prefixPath, u_logion));//el commit se hace aqui adentro
                AplicarContableReturn acr = conInt.aplicarContableNuevo(conn, c, "", "", "", 0, "", m, prefixPath, u_logion, "");
                arrLResult = (ArrayList) acr.getMessageList();
            }
        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
        } finally {
            conn.commit();
            if (conn != null)
                conn.close();
            conn = null;
        }
        return arrLResult;
    }

    public ArrayList<String> reAplicaDoctos() throws SQLException {
        ArrayList<String> arrLResult = new ArrayList<String>();
        Connection conn = null;
        try {
            conn = getConnection();
            PresupuestoManager.reAplicaDoctos(conn);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return arrLResult;
    }

    public List<String> getFiltroPartidasModulo(String modulo) throws Exception {
        Connection conn = null;
        List<String> partidasFiltro = new ArrayList<String>();
        try {
            conn = getConnection();
            partidasFiltro = PresupuestoManager.getFiltroPartidasModulo(conn, modulo);
            return partidasFiltro;
        } finally {
            CloseObject.closeObject(conn, true);
        }
    }
}
