package com.syc.ejercido.pagado;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.jfree.util.Log;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

/**
 * @author Martha Aurora Sánchez Valdivieso
 * para SYC Constructores de Sistemas
 * desarrollo gestion_conagua_sif
 * México D.F. 23/01/2012
 */
public class CompromisoNOMManager {

    public CompromisoNOMManager() {
        super();
    }

    public static ArrayList<String> BuscaCompromisos(Connection conn, String lista_Contratos) throws Exception {
        ArrayList<String> arrListaComp = new ArrayList<String>();
        PreparedStatement pstmntH = null;
        PreparedStatement pstmntD = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        //Datos de Encabezado:
        StringBuilder Sql = new StringBuilder();
        Sql.append(" SELECT tCE.nFolioCompromisoNomina");
        Sql.append(" ,'H' AS Header");
        Sql.append(" ,fAplicacion");
        Sql.append(" ,tCE.fCarga");
        Sql.append(" ,tCE.cRamo");
        Sql.append(" ,tCE.cRamo");
        Sql.append(" ,tCE.cRamo");
        Sql.append(" ,tCEP.cUnidadResponsableEP");
        Sql.append(" ,tCEP.cUnidadResponsableEP");
        Sql.append(" ,tCEP.cUnidadResponsableEP");
        Sql.append(" ,CASE WHEN caNoCompromisoAmpliado IS NULL OR caNoCompromisoAmpliado = '' THEN 'O' ELSE 'A' END AS MOVTO");
        Sql.append(" ,SUM(tCD.mImporte) as mImporteComp");
        Sql.append(" ,'1' AS TipoContratoDiverso");
        Sql.append(" ,'' AS TipoContratoObra");
        Sql.append(" , 'N/A' AS RepresLegal");
        Sql.append(" , '0' AS Plurianual");
        Sql.append(" ,'S24677' as CBEN");
        Sql.append(" ,'6001'  as cIdRFC");
        Sql.append(" , CONVERT(VARCHAR(4), YEAR(fAplicacion)) + '-01-01' as fContratoIni");
        Sql.append(" , CONVERT(VARCHAR(4), YEAR(fAplicacion)) + '-12-31'  as fContratoFin");
        Sql.append(" ,REPLACE(tCE.cIdContrato,',','') cIdContrato");
        Sql.append(" , 'NOMINA-' + CONVERT(VARCHAR(10), tCE.nFolioCompromisoNomina) as cConceptoContrato");
        Sql.append(" ,'' AS ACTO_JURIDICO");
        Sql.append(" ,'' AS TEMPORALIDAD");
        Sql.append(" ,'0' AS APROB_PLA");
        Sql.append(" ,'0' AS CONTRATACION");
        Sql.append(" ,'0' AS ESQ_PRECIO");
        Sql.append(" ,'' AS PRG_ASOC");
        Sql.append(" ,'' AS BIEN_EXPROP");
        Sql.append(" ,'' AS ID_CATASTRAL");
        Sql.append(" ,'0' AS TPROC");
        Sql.append(" ,'' AS POBLACION_OBJ");
        Sql.append(" ,'MXN' AS cCodigoMonedaSiaff");
        Sql.append(" ,'1' AS TCAM");
        Sql.append(" ,SUM(ABS(tCD.mImporte)) AS MONTO_MONORI");
        Sql.append(" ,SUM(ABS(tCD.mImporte)) AS MONTO_EJER");
        Sql.append(" ,SUM(ABS(tCD.mImporte)) AS MONTO_MIN");
        Sql.append(" ,SUM(ABS(tCD.mImporte)) AS MONTO_MAX");
        Sql.append(" ,'' AS CONV_MOD");
        Sql.append(" ,'' AS NUM_PLAZAS");
        Sql.append(" ,'' AS VAR_PLAZAS");
        Sql.append(" ,tCE.caNoCompromiso");
        Sql.append(" ,tCE.nMes");
        Sql.append(" ,'' AS ID_CTR_INT");
        Sql.append(" ,tCE.caNoCompromiso");
        Sql.append(" , ISNULL(tCE.nFolioSICOP, '') nFolioSICOP ");
        Sql.append(" FROM tCompromisoNOMINAEncabezado tCE,");
        Sql.append(" tCompromisoNOMINADetalle tCD,");
        Sql.append(" tCatalogoEP tCEP");
        Sql.append(" WHERE tCE.caNoCompromiso in ('" + lista_Contratos + "')");
        Sql.append(" AND tCE.nFolioCompromisoNomina = tCD.nFolioCompromisoNomina");
        Sql.append(" AND tCEP.EP = tCD.EP");
        Sql.append(" GROUP BY tCE.nFolioCompromisoNomina,tCE.fAplicacion,tCE.fCarga,");
        Sql.append(" tCE.cRamo,tCD.nFolioCompromisoNomina,tCE.cUnidadResponsable,");
        Sql.append(" tCE.cIdContrato,tCE.cIdContrato, tCE.caNoCompromiso,tCE.nMes, tCE.nFolioSICOP,");
        Sql.append(" tCE.caNoCompromiso,tCEP.cUnidadResponsableEP, tCE.caNoCompromisoAmpliado");
        try {
            pstmntH = conn.prepareStatement(Sql.toString());
            rs = pstmntH.executeQuery();
            while (rs.next()) {
                String nFolioCompromiso = rs.getString("nFolioCompromisoNomina");
                SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String fechaALayout = fecha.format(sdf.parse(rs.getString("fAplicacion")));
                String fechaLayout = fecha.format(sdf.parse(rs.getString("fCarga")));
                String fContratoIni = fecha.format(sdf.parse(rs.getString("fContratoIni")));
                String fContratoFin = fecha.format(sdf.parse(rs.getString("fContratoFin")));
                String tipo = rs.getString("MOVTO");
                Float importe = Float.parseFloat(rs.getString("mImporteComp"));
                String tipoMovto = "";
                if ("A".equals(tipo) && importe < 0.0) {
                    tipoMovto = "R";
                }
                if ("A".equals(tipo) && importe > 0.0) {
                    tipoMovto = "A";
                }
                if ("O".equals(tipo)) {
                    tipoMovto = "O";
                }
                String compromiso = "4";
                String erogacion = "";
                Integer tipoContratoD = 0;
                Integer tipoContratoO = 0;
                if (!"".equals(rs.getString("TipoContratoDiverso")) && rs.getString("TipoContratoDiverso") != null) {
                    tipoContratoD = Integer.parseInt(rs.getString("TipoContratoDiverso"), 10);
                }
                if (!"".equals(rs.getString("TipoContratoObra")) && rs.getString("TipoContratoObra") != null) {
                    tipoContratoO = Integer.parseInt(rs.getString("TipoContratoObra"), 10);
                }
                if (tipoContratoD == 1 || tipoContratoD == 4) {
                    //bien
                    erogacion = "1";
                } else if (tipoContratoD == 2 || tipoContratoD == 3) {
                    //servicio
                    erogacion = "2";
                } else if (tipoContratoD == 5) {
                    //obra
                    erogacion = "3";
                } else //else if(tipoContratoO !=0 ){
                {
                    //obra
                    erogacion = "3";
                }
                erogacion = "8";
                String tipoOp = "1";
                String monto = new BigDecimal(rs.getString("MONTO_MONORI")).setScale(2, RoundingMode.HALF_UP).toPlainString();
                StringBuilder encabezado = new StringBuilder();
                // A
                encabezado.append(rs.getString("Header").trim()).append(",");
                // B
                encabezado.append(fechaALayout.trim()).append(",");
                // C
                encabezado.append(fechaLayout.trim()).append(",");
                // D
                encabezado.append(rs.getString("cRamo").trim()).append(",");
                // E
                encabezado.append(rs.getString("cRamo").trim()).append(",");
                // F
                encabezado.append(rs.getString("cRamo").trim()).append(",");
                // G
                encabezado.append(rs.getString("cUnidadResponsableEP").trim()).append(",");
                // H
                encabezado.append(rs.getString("cUnidadResponsableEP").trim()).append(",");
                // I
                encabezado.append(rs.getString("cUnidadResponsableEP").trim()).append(",");
                // J
                encabezado.append(tipoMovto).append(",");
                // K
                encabezado.append(compromiso).append(",");
                // L
                encabezado.append(erogacion).append(",");
                // M
                encabezado.append(rs.getString("nFolioSICOP")).append(",");
                // N
                encabezado.append(tipoOp).append(",");
                // O
                encabezado.append(rs.getString("cIdContrato")).append(",");
                // P
                encabezado.append(rs.getString("cConceptoContrato")).append(",");
                // Q
                encabezado.append(rs.getString("CBEN").trim()).append(",");
                // R
                encabezado.append(rs.getString("cIdRFC").trim()).append(",");
                // S
                encabezado.append(rs.getString("RepresLegal")).append(",");
                // T
                encabezado.append(rs.getString("TPROC")).append(",");
                // U
                encabezado.append(rs.getString("ESQ_PRECIO")).append(",");
                // V
                encabezado.append(rs.getString("CONTRATACION")).append(",");
                // W
                encabezado.append(fContratoIni.trim()).append(",");
                // X
                encabezado.append(fContratoFin.trim()).append(",");
                // Y
                encabezado.append(fContratoIni.trim()).append(",");
                // Z
                encabezado.append("N").append(",");
                // AA
                encabezado.append(rs.getString("APROB_PLA")).append(",");
                // AB
                encabezado.append(rs.getString("ACTO_JURIDICO")).append(",");
                // AC
                encabezado.append(monto).append(",");
                // AD
                encabezado.append(rs.getString("cCodigoMonedaSiaff")).append(",");
                // AE
                encabezado.append(rs.getString("TCAM")).append(",");
                // AF
                encabezado.append(monto).append(",");
                // AG
                encabezado.append(monto).append(",");
                // AH
                encabezado.append(monto).append(",");
                // AI
                encabezado.append("N").append(",");
                // AJ
                encabezado.append(rs.getString("CONV_MOD")).append(",");
                // AK
                encabezado.append(fechaALayout.trim()).append(",");
                // AL
                encabezado.append(rs.getString("Plurianual")).append(",");
                // AM
                encabezado.append(rs.getString("Plurianual")).append(",");
                // AN
                encabezado.append(rs.getString("Plurianual")).append(",");
                // AO
                encabezado.append(rs.getString("nMes").trim()).append(",");
                // AP
                encabezado.append(rs.getString("caNoCompromiso")).append(",");
                // AQ
                encabezado.append(rs.getString("ID_CTR_INT")).append(",");
                // AR
                encabezado.append(rs.getString("caNoCompromiso")).append(",");
                // AS
                encabezado.append(rs.getString("caNoCompromiso")).append(",");
                // AT
                encabezado.append("N").append(",");
                // AU
                encabezado.append("NOMINA").append(",");
                // AV
                encabezado.append("0.00").append(",");
                // AW
                encabezado.append("0").append(",");
                // AX
                encabezado.append("0").append("\r\n");
                arrListaComp.add(encabezado.toString());
                System.out.println("encabezado: / " + encabezado);
                StringBuilder Sql2 = new StringBuilder();
                Sql2.append(" SELECT '627' as ID_EVENTO,'304_OCN' as EVENTO, tCEP.cRamo, tCEP.cUnidadResponsableEP, tCEP.aEjercicioFiscal,");
                Sql2.append(" tCEP.cGrupoFuncional, tCEP.cFuncion, tCEP.cSubFuncion,");
                // CUANDO ES PROGRAMA GENERAL '09' SE MODIFICA PARA QUE MUESTRE '00' PARA EVITAR ERROR EN SICOP.
                Sql2.append(" CASE WHEN tCEP.cProgramaGeneral IN (SELECT cProgramaGeneral FROM tCat_ProGeneralPlurianual WITH (NOLOCK)) THEN '00' ELSE tCEP.cProgramaGeneral END AS cProgramaGeneral,");
                Sql2.append(" tCEP.cActividadInstitucional,");
                Sql2.append(" tCEP.cProgramaPresupuestario, SUBSTRING(tCEP.cPartida,1,1), SUBSTRING(tCEP.cPartida,2,1), SUBSTRING(tCEP.cPartida,3,1), SUBSTRING(tCEP.cPartida,4,2),");
                Sql2.append(" tCEP.cTipoGasto, tCEP.cFuenteFinanciamiento, tCEP.cEntidadFederativa, SUBSTRING(dbo.CambiaEPCarteraMeta(tCD.EP),45,11) AS cCartera,");
                Sql2.append(" '0000000000' as CAU, '00' as COP,");
                Sql2.append(" '000' as PL,'000' as OF_,'00000' as AUX1,'00000' as AUX2,");
                Sql2.append(" '0000000000' as AUX3,'' as Suficiencia,'' as Sol_OLI,");
                Sql2.append(" SUM(CASE tCD.cMes when 1 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Enero,");
                Sql2.append(" SUM(CASE tCD.cMes when 2 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Febrero,");
                Sql2.append(" SUM(CASE tCD.cMes when 3 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Marzo,");
                Sql2.append(" SUM(CASE tCD.cMes when 4 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Abril,");
                Sql2.append(" SUM(CASE tCD.cMes when 5 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Mayo,");
                Sql2.append(" SUM(CASE tCD.cMes when 6 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Junio,");
                Sql2.append(" SUM(CASE tCD.cMes when 7 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Julio,");
                Sql2.append(" SUM(CASE tCD.cMes when 8 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Agosto,");
                Sql2.append(" SUM(CASE tCD.cMes when 9 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Septiembre,");
                Sql2.append(" SUM(CASE tCD.cMes when 10 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Octubre,");
                Sql2.append(" SUM(CASE tCD.cMes when 11 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Noviembre,");
                Sql2.append(" SUM(CASE tCD.cMes when 12 then convert(decimal(14,2), ABS(mImporte)) ELSE 0 END) as Diciembre,");
                Sql2.append(" SUM(convert(decimal(14,2), abs(tCD.mImporte))) as Importe");
                Sql2.append(" FROM tCompromisoNominaDetalle tCD, tCatalogoEP tCEP, tCompromisoNominaEncabezado tCE");
                Sql2.append(" WHERE tCD.nFolioCompromisoNomina = ?");
                Sql2.append(" AND tCD.EP = tCEP.EP");
                Sql2.append(" AND tCE.nFolioCompromisoNomina = tCD.nFolioCompromisoNomina");
                Sql2.append(" GROUP BY tCEP.cRamo, tCEP.aEjercicioFiscal, tCEP.cUnidadResponsableEP, tCEP.cGrupoFuncional, tCEP.cFuncion, tCEP.cSubFuncion,");
                Sql2.append(" tCEP.cProgramaGeneral, tCEP.cActividadInstitucional, tCEP.cProgramaPresupuestario, tCEP.cPartida,");
                Sql2.append(" tCEP.cTipoGasto, tCEP.cFuenteFinanciamiento, tCEP.cEntidadFederativa, SUBSTRING(dbo.CambiaEPCarteraMeta(tCD.EP),45,11)");
                //System.out.println("detalles: "+Sql2+ "/"+nFolioCompromiso);
                pstmntD = conn.prepareStatement(Sql2.toString());
                pstmntD.setString(1, nFolioCompromiso);
                rs2 = pstmntD.executeQuery();
                //arrListaComp.append(
                while (rs2.next()) {
                    StringBuilder detalle = new StringBuilder();
                    // ID_EVENTO
                    detalle.append(rs2.getString("ID_EVENTO"));
                    // EVENTO
                    detalle.append(",").append(rs2.getString("EVENTO"));
                    // tCEP.cRamo
                    detalle.append(",").append(rs2.getString("cRamo").trim());
                    // tCEP.cUnidadResponsableEP
                    detalle.append(",").append(rs2.getString("cUnidadResponsableEP").trim());
                    // tCEP.aEjercicioFiscal
                    detalle.append(",").append(rs2.getString("aEjercicioFiscal").trim());
                    // tCEP.cGrupoFuncional
                    detalle.append(",").append(rs2.getString("cGrupoFuncional").trim());
                    // tCEP.cFuncion
                    detalle.append(",").append(rs2.getString("cFuncion").trim());
                    // tCEP.cSubFuncion
                    detalle.append(",").append(rs2.getString("cSubFuncion").trim());
                    // tCEP.cProgramaGeneral
                    detalle.append(",").append(rs2.getString("cProgramaGeneral"));
                    // tCEP.cActividadInstitucional
                    detalle.append(",").append(rs2.getString("cActividadInstitucional"));
                    // tCEP.cProgramaPresupuestario
                    detalle.append(",").append(rs2.getString("cProgramaPresupuestario"));
                    // SUBSTRING(tCEP.cPartida,1,1)
                    detalle.append(",").append(rs2.getString(12));
                    // SUBSTRING(tCEP.cPartida,2,1)
                    detalle.append(",").append(rs2.getString(13));
                    // SUBSTRING(tCEP.cPartida,3,1)
                    detalle.append(",").append(rs2.getString(14));
                    // SUBSTRING(tCEP.cPartida,4,2)
                    detalle.append(",").append(rs2.getString(15));
                    // tCEP.cTipoGasto
                    detalle.append(",").append(rs2.getString("cTipoGasto"));
                    // tCEP.cFuenteFinanciamiento
                    detalle.append(",").append(rs2.getString("cFuenteFinanciamiento"));
                    // tCEP.cEntidadFederativa
                    detalle.append(",").append(rs2.getString("cEntidadFederativa"));
                    // tCEP.cCartera
                    detalle.append(",").append(rs2.getString("cCartera"));
                    // CAU
                    detalle.append(",").append(rs2.getString("CAU"));
                    // COP
                    detalle.append(",").append(rs2.getString("COP"));
                    // PL
                    detalle.append(",").append(rs2.getString("PL"));
                    // OF_
                    detalle.append(",").append(rs2.getString("OF_"));
                    // CAMBIA VALOR FIJO '00000' // AUX1
                    detalle.append(",").append(rs2.getString("AUX1"));
                    // AUX2
                    detalle.append(",").append(rs2.getString("AUX2"));
                    // AUX3
                    detalle.append(",").append(rs2.getString("AUX3"));
                    // Suficiencia
                    detalle.append(",").append(rs2.getString("Suficiencia"));
                    // Sol_OLI
                    detalle.append(",").append(rs2.getString("Sol_OLI"));
                    // Enero
                    detalle.append(",").append(rs2.getString("Enero"));
                    // Febrero
                    detalle.append(",").append(rs2.getString("Febrero"));
                    // Marzo
                    detalle.append(",").append(rs2.getString("Marzo"));
                    // Abril
                    detalle.append(",").append(rs2.getString("Abril"));
                    // Mayo
                    detalle.append(",").append(rs2.getString("Mayo"));
                    // Junio
                    detalle.append(",").append(rs2.getString("Junio"));
                    // Julio
                    detalle.append(",").append(rs2.getString("Julio"));
                    // Agosto
                    detalle.append(",").append(rs2.getString("Agosto"));
                    // Septiembre
                    detalle.append(",").append(rs2.getString("Septiembre"));
                    // Octubre
                    detalle.append(",").append(rs2.getString("Octubre"));
                    // Noviembre
                    detalle.append(",").append(rs2.getString("Noviembre"));
                    // Diciembre
                    detalle.append(",").append(rs2.getString("Diciembre"));
                    // Importe
                    detalle.append(",").append(rs2.getString("Importe"));
                    detalle.append("\r\n");
                    arrListaComp.add(detalle.toString());
                }
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rs2);
            CloseObject.closeObject(pstmntD);
            CloseObject.closeObject(pstmntH);
        }
        return arrListaComp;
    }

    public static int updateHeaderCompromisos(Connection conn, String listaIds) throws SQLException {
        PreparedStatement pstmnt = null;
        int retval;
        try {
            pstmnt = conn.prepareStatement("UPDATE tCompromisoNominaEncabezado SET nEnviadoSICOP = 1 WHERE caNoCompromiso in ('" + listaIds + "')");
            retval = pstmnt.executeUpdate();
            conn.commit();
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
        }
        return retval;
    }

    public static boolean insertaFiltrados(Connection conn, StringBuffer archivoFiltrado) throws FileNotFoundException, IOException {
        String[] celdas;
        boolean regreso = false;
        boolean actualiza = false;
        boolean inserta = false;
        Date fAppTmp = null;
        Date fExpTmp = null;
        java.sql.Date fAplicacion = null;
        java.sql.Date fExpedicion = null;
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        try {
            String sRegistro = archivoFiltrado.toString();
            String[] tmp = sRegistro.split("\\|");
            for (int m = 0; m < tmp.length; m++) {
                String linea = tmp[m];
                celdas = linea.split(",");
                String clave = celdas[0];
                String cRamo = celdas[1];
                String cUnidadResponsable = celdas[2];
                String folioSICOP = celdas[3];
                String idProceso = celdas[4];
                String cCentroContable = celdas[5];
                String fExp = celdas[6];
                fExpTmp = formato.parse(fExp);
                fExpedicion = new java.sql.Date(fExpTmp.getTime());
                float total = Float.parseFloat(celdas[7]);
                String cTipoPoliza = celdas[8];
                String nFolioPoliza = celdas[9];
                String nPolizaCancelacion = celdas[10];
                String tipoMovimiento = celdas[11];
                String origenPresupuesto = celdas[12];
                String cuentaBancaria = celdas[13];
                String noSolicitud = celdas[14];
                String tCambio = celdas[15];
                String tMoneda = celdas[16];
                String tSolicitud = celdas[17];
                String volante = celdas[18];
                String rfc = celdas[19];
                String caNoCompromiso = celdas[20];
                String codSemarnat2 = celdas[21];
                String estatus = (celdas[22].equals("APLICADO")) ? "2" : "3";
                Integer nEnviadoSICOP = Integer.parseInt(estatus, 10);
                String fAp = celdas[23];
                fAppTmp = formato.parse(fAp);
                fAplicacion = new java.sql.Date(fAppTmp.getTime());
                String documento = celdas[24];
                String nDocumento = celdas[25];
                String descripcion = celdas[26];
                //Actualizando en tabla tCompromisosEscabezado
                actualiza = updateHeaderCompromisosRealimentacion(conn, nEnviadoSICOP, caNoCompromiso);
                if (actualiza) {
                    //Insertando en tabla
                    inserta = insertRegisterLayout(conn, clave, cRamo, cUnidadResponsable, folioSICOP, idProceso, cCentroContable, fExpedicion, total, cTipoPoliza, nFolioPoliza, nPolizaCancelacion, tipoMovimiento, origenPresupuesto, cuentaBancaria, noSolicitud, tCambio, tMoneda, tSolicitud, volante, rfc, caNoCompromiso, codSemarnat2, estatus, fAplicacion, documento, nDocumento, descripcion);
                }
                if (inserta) {
                    regreso = actualiza;
                }
            }
            //fin for
        } catch (Exception exc) {
            exc.printStackTrace();
            Log.warn("Cerrando BufferedReader", exc);
            regreso = false;
        }
        return regreso;
    }

    public static boolean updateHeaderCompromisosRealimentacion(Connection conn, Integer nEnviadoSICOP, String caNoCompromiso) throws SQLException {
        PreparedStatement pstmntL = null;
        boolean insertado = false;
        int renglones = 0;
        String queryUpdateEstatus = "UPDATE tCompromisoEncabezado SET nEnviadoSICOP = " + nEnviadoSICOP + " WHERE caNoCompromiso = '" + caNoCompromiso + "'";
        try {
            pstmntL = conn.prepareStatement(queryUpdateEstatus);
            renglones = pstmntL.executeUpdate();
            conn.commit();
            if (renglones == 0)
                insertado = false;
            if (renglones > 0)
                insertado = true;
        } finally {
            CloseObject.closeObject(pstmntL);
        }
        return insertado;
    }

    public static boolean insertRegisterLayout(Connection conn, String clave, String cRamo, String cUnidadResponsable, String folioSICOP, String idProceso, String cCentroContable, java.sql.Date fExpedicion, float total, String cTipoPoliza, String nFolioPoliza, String nPolizaCancelacion, String tipoMovimiento, String origenPresupuesto, String cuentaBancaria, String noSolicitud, String tCambio, String tMoneda, String tSolicitud, String volante, String rfc, String caNoCompromiso, String codSemarnat2, String estatus, java.sql.Date fAplicacion, String documento, String nDocumento, String descripcion) throws Exception {
        PreparedStatement pstmnt = null;
        boolean insertReg = false;
        boolean existReg = false;
        existReg = existeCaNoCompromiso(conn, caNoCompromiso, codSemarnat2);
        if (existReg == false) {
            try {
                StringBuilder queryInsert = new StringBuilder();
                queryInsert.append("INSERT INTO tLayoutCompromisos(").append("cClave,cRamo,cUnidadCreadora,cFolioCompromisoSICOP,cIdProceso,").append("cCentroContable,fExpedicion,mImporte,cIdTipoPoliza,nPolizaSICOP,").append("nPolizaCancelacionSICOP,cIdTipoMovimiento,nOrigenPresupuesto,nCuentaBancaria,cNoSolicitud,").append("cTipoCambio,cMoneda,cTipoSolicitud,cVolante,dRFC,").append("caNoCompromiso,caNoSemarnat2,cEstatus,fAplicacionSICOP,tDocumento,").append("nDocumento,cDescripcion) ").append("VALUES('").append(clave).append("','").append(cRamo).append("','").append(cUnidadResponsable).append("','").append(folioSICOP).append("','").append(idProceso).append("','").append(cCentroContable).append("','").append(fExpedicion).append("',").append(total).append(",'").append(cTipoPoliza).append("','").append(nFolioPoliza).append("','").append(nPolizaCancelacion).append("','").append(tipoMovimiento).append("','").append(origenPresupuesto).append("','").append(cuentaBancaria).append("','").append(noSolicitud).append("','").append(tCambio).append("','").append(tMoneda).append("','").append(tSolicitud).append("','").append(volante).append("','").append(rfc).append("','").append(caNoCompromiso).append("','").append(codSemarnat2).append("','").append(estatus).append("','").append(fAplicacion).append("','").append(documento).append("','").append(nDocumento).append("','").append(descripcion).append("')");
                pstmnt = conn.prepareStatement(queryInsert.toString());
                int reg = pstmnt.executeUpdate();
                if (reg == 1) {
                    insertReg = true;
                } else {
                    insertReg = false;
                }
                conn.commit();
            } finally {
                CloseObject.closeObject(pstmnt);
            }
        }
        return insertReg;
    }

    public static boolean existeCaNoCompromiso(Connection conn, String caNoCompromiso, String codSemarnat2) throws Exception {
        boolean existe = false;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            String querySelect = "SELECT COUNT(*) FROM tLayoutCompromisos WHERE caNoCompromiso = '" + caNoCompromiso + "' AND caNoSemarnat2= '" + codSemarnat2 + "'";
            pstmnt = conn.prepareStatement(querySelect);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                existe = (rs.getInt(1) > 0);
            } else {
                existe = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
        return existe;
    }

    public static String buscaFechaAplicacionCancelados(Connection conn, String compromisoCancelado) throws SQLException {
        String fAplicacion = "";
        PreparedStatement pstmnt = null;
        PreparedStatement pstmntfApp = null;
        ResultSet rs = null;
        ResultSet rsfApp = null;
        int rows = 0;
        String querySelect = "SELECT COUNT(*)FROM tLayoutCompromisos WHERE caNoCompromiso = '" + compromisoCancelado + "'";
        try {
            pstmnt = conn.prepareStatement(querySelect);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                rows = rs.getInt(1);
            }
            if (rows != 0) {
                String querySelectFechas = "SELECT fAplicacionSICOP FROM tLayoutCompromisos WHERE caNoCompromiso = '" + compromisoCancelado + "'";
                pstmntfApp = conn.prepareStatement(querySelectFechas);
                rsfApp = pstmntfApp.executeQuery();
                while (rsfApp.next()) {
                    fAplicacion = rsfApp.getString(1).trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseObject.closeObject(pstmntfApp);
            CloseObject.closeObject(pstmnt);
        }
        return fAplicacion;
    }
}
