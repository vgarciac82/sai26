package com.syc.ejercido.pagado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.syc.contable.core.AplicacionContable;
import com.syc.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CargaArchivo extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(AplicacionContable.class);

    public String insertaLineaSicop(String hojaVisor, String idRamoCr, String idUnidadCr, String folioClc45, String fechaExp, String FechaApl, String TTRANS34, String TSOL35, String TPPTO36, String TMON37, String TCAM38, String Volante39, String FechaOficio42, String NCLC43, String CPAG44, String Estimacion46, String NCTR47, String SOLP48, String TIPOMOVTO_51, String NoPoliza56, String NoPolizaCancela57, String TipoPoliza58, String FechaIni59, String FechaFin60, String TipoCLC67, String CVELeyenda68, String Bene69, String CTAB70, String RFC6, String ApepatApematNombre, String Replace, String TDOC71, String Descripcion100092, String Negociable97, String CIFI98, String CTIF99, String NACU100, String FechaNego101, String NacuCP102, String Total103, String TotalDivisa104, String FechaRef105, String FechaPago106, String Referencia1108, String Referencia2109, String FolioSiaff112, String FechaSiaff113, String CCCTRERROR116, String CentroContable, String DSUFPRE172, String AOT173, String Pagado175, String RFC184, String Estatus, String PRCSClave, String ProcClave, String IdUsuarioCancela, String FechaCancela, String IdUsuarioAplica, String IdUsuarioCR, String IdRolCR, String IdRenglon, String IdEvento, String Evento, String IdRamoML, String IdUnidadML, String NCOM15, String CBEN16, String NRES17, String NOIF18, String ISR20, String Iva21, String MIL522, String IvaDes23, String Anticipo24, String IVAANT25, String ImpRete26, String ImpDivisa27, String ImpEstim28, String Imp500032, String TCONC49, String ConcMov50, String FechaDocto75, String ImpNeto107, String CCMSGError115, String TPAG117, String ImpRema133, String Importe148, String Mes149, String Cani150, String CGFU151, String CFun152, String CSFU153, String CPRG154, String Cain155, String CPPT156, String Ccap157, String Ccon158, String CParg300, String CPar159, String CTGA160, String CFin161, String CGeo164, String CPpi166, String CCau162, String CCop163, String CPla165, String OFin167, String AUX1168, String AUX2169, String AUX3170, String SPag176, String PPag177, String TNom178, String COBG183, String Tunr80) throws SQLException {
        String respuesta = "";
        PreparedStatement ps = null;
        Connection conn = null;
        int cuantos = 0;
        try {
            conn = getConnection();
            ps = conn.prepareStatement("INSERT INTO CLC_SICOP (HOJA_VISOR,ID_RAMO_CR,ID_UNIDAD_CR,FOLIO_CLC_45,FECHA_EXP,FECHA_APL,TTRANS_34,TSOL_35,TPPTO_36,TMON_37,TCAM_38,VOLANTE_39,FECHA_OFICIO_42,NCLC_43,CPAG_44," + " ESTIMACION_46,NCTR_47,SOLP_48,TIPO_MOVTO_51,NO_POLIZA_56,NO_POLIZA_CANCELA_57,TIPO_POLIZA_58,FECHA_INI_59,FECHA_FIN_60,TIPO_CLC_67,CVE_LEYENDA_68,BENE_69,CTAB_70,RFC_6,APEPAT_APEMAT_NOMBRE,REPLACE," + " TDOC_71,DESCRIPCION_1000_92,NEGOCIABLE_97,CIFI_98,CTIF_99,NACU_100,FECHA_NEGO_101,NACU_CP_102,TOTAL_103,TOTAL_DIVISA_104,FECHA_REF_105,FECHA_PAGO_106,REFERENCIA1_108,REFERENCIA2_109,FOLIO_SIAFF_112," + " FECHA_SIAFF_113,CC_CTRERROR_116,CENTRO_CONTABLE,DSUFPRE_172,AOT_173,PAGADO_175,RFC_184,ESTATUS,PRCS_CLAVE,PROC_CLAVE,ID_USUARIO_CANCELA,FECHA_CANCELA,ID_USUARIO_APLICA,ID_USUARIO_CR,ID_ROL_CR,ID_RENGLON," + " ID_EVENTO,EVENTO,ID_RAMO_ML,ID_UNIDAD_ML,NCOM_15,CBEN_16,NRES_17,NOIF_18,ISR_20,IVA_21,MIL5_22,IVADES_23,ANTICIPO_24,IVAANT_25,IMP_RETE_26,IMP_DIVISA_27,IMP_ESTIM_28,IMP_5000_32,TCONC_49,CONC_MOV_50," + " FECHA_DOCTO_75,IMP_NETO_107,CC_MSGERROR_115,TPAG_117,IMP_REMA_133,IMPORTE_148,MES_149,CANI_150,CGFU_151,CFUN_152,CSFU_153,CPRG_154,CAIN_155,CPPT_156,CCAP_157,CCON_158,CPARG_300,CPAR_159,CTGA_160,CFIN_161," + " CGEO_164,CPPI_166,CCAU_162,CCOP_163,CPLA_165,OFIN_167,AUX1_168,AUX2_169,AUX3_170,SPAG_176,PPAG_177,TNOM_178,COBG_183,TUNR_80" + ") VALUES ('" + hojaVisor + "','" + idRamoCr + "','" + idUnidadCr + "','" + folioClc45 + "','" + fechaExp + "','" + FechaApl + "','" + TTRANS34 + "','" + TSOL35 + "','" + TPPTO36 + "','" + TMON37 + "','" + TCAM38 + "','" + Volante39 + "','" + FechaOficio42 + "','" + NCLC43 + "','" + CPAG44 + "'," + "	'" + Estimacion46 + "','" + NCTR47 + "','" + SOLP48 + "','" + TIPOMOVTO_51 + "','" + NoPoliza56 + "','" + NoPolizaCancela57 + "','" + TipoPoliza58 + "','" + FechaIni59 + "','" + FechaFin60 + "','" + TipoCLC67 + "','" + CVELeyenda68 + "','" + Bene69 + "','" + CTAB70 + "','" + RFC6 + "','" + ApepatApematNombre + "','" + Replace + "'," + " '" + TDOC71 + "','" + Descripcion100092 + "','" + Negociable97 + "','" + CIFI98 + "','" + CTIF99 + "','" + NACU100 + "','" + FechaNego101 + "','" + NacuCP102 + "','" + Total103 + "','" + TotalDivisa104 + "','" + FechaRef105 + "','" + FechaPago106 + "','" + Referencia1108 + "','" + Referencia2109 + "','" + FolioSiaff112 + "'," + " '" + FechaSiaff113 + "','" + CCCTRERROR116 + "','" + CentroContable + "','" + DSUFPRE172 + "','" + AOT173 + "','" + Pagado175 + "','" + RFC184 + "','" + Estatus + "','" + PRCSClave + "','" + ProcClave + "','" + IdUsuarioCancela + "','" + FechaCancela + "','" + IdUsuarioAplica + "','" + IdUsuarioCR + "','" + IdRolCR + "','" + IdRenglon + "'," + " '" + IdEvento + "','" + Evento + "','" + IdRamoML + "','" + IdUnidadML + "','" + NCOM15 + "','" + CBEN16 + "','" + NRES17 + "','" + NOIF18 + "','" + ISR20 + "','" + Iva21 + "','" + MIL522 + "','" + IvaDes23 + "','" + Anticipo24 + "','" + IVAANT25 + "','" + ImpRete26 + "','" + ImpDivisa27 + "','" + ImpEstim28 + "','" + Imp500032 + "','" + TCONC49 + "','" + ConcMov50 + "'," + " '" + FechaDocto75 + "','" + ImpNeto107 + "','" + CCMSGError115 + "','" + TPAG117 + "','" + ImpRema133 + "','" + Importe148 + "','" + Mes149 + "','" + Cani150 + "','" + CGFU151 + "','" + CFun152 + "','" + CSFU153 + "','" + CPRG154 + "','" + Cain155 + "','" + CPPT156 + "','" + Ccap157 + "','" + Ccon158 + "','" + CParg300 + "','" + CPar159 + "','" + CTGA160 + "','" + CFin161 + "'," + " '" + CGeo164 + "','" + CPpi166 + "','" + CCau162 + "','" + CCop163 + "','" + CPla165 + "','" + OFin167 + "','" + AUX1168 + "','" + AUX2169 + "','" + AUX3170 + "','" + SPag176 + "','" + PPag177 + "','" + TNom178 + "','" + COBG183 + "','" + Tunr80 + "'   )");
            int intr = ps.executeUpdate();
            //mandarlo cada 100 commit
            if (intr > 0) {
                respuesta = "guardado";
                cuantos = cuantos + 1;
                //System.out.println("cuantos:"+cuantos);
                conn.commit();
                if (cuantos >= 100) {
                    //System.out.println("si mas de 100:"+cuantos);
                    //conn.commit();
                }
            } else {
                respuesta = "no_guardado";
                conn.rollback();
            }
            conn.close();
        } catch (Exception e) {
            log.error("Error SICOP: " + e);
            conn.rollback();
            conn.close();
        }
        return respuesta;
    }

    //SIAFF
    public String insertaLineaSiaff(String idRamoCrS, String idUnidadCrS, String tipoCLC, String folioCLC, String folioDependendia, String estatusCLC, String proceso, String fechaCaptura, String fechaAplicacion, String aplicacionContable, String usuarioCaptura, String aprobRevisor, String usuarioRevisor, String fechaRevision, String aprobAutorizador, String usuarioAutorizador, String fechaAutorizacion, String numeroAMF, String leyenda, String descLeyenda, String referencia1, String referencia2, String folioAMF, String noOfAMF, String fechaReferencia1, String divisa, String totalDivisa, String tipoCambio, String totalMN, String claveBeneficiario, String beneficiario, String ctaBancaria, String fechaPropuesta, String medioPago, String fechaPago, String cuantos) throws SQLException {
        String respuesta = "";
        PreparedStatement ps = null;
        //ResultSet rs = null;
        Connection conn = null;
        //int ejecutar = Integer.parseInt(cuantos,10);
        try {
            conn = getConnection();
            ps = conn.prepareStatement("INSERT INTO CLC_SIAFF_ENC(RAMO_CR,UNIDAD_CR,TIPO_CLC,FOLIO_CLC,FOLIO_DEPENDENCIA,ESTATUS_CLC,PROCESO,FECHA_CAPTURA,FECHA_APLICACION,APLICACION_CONTABLE,USUARIO_CAPTURA,APROB_REVISOR, " + " USUARIO_REVISOR,FECHA_REVISION,APROB_AUTORIZADOR,USUARIO_AUTORIZADOR,FECHA_AUTORIZACION,NUMERO_AMF,LEYENDA,DESC_LEYENDA,REFERENCIA1,REFERENCIA2,FOLIO_AMF,NO_OF_AMF,FECHA_REFERENCIA1,DIVISA,TOTAL_DIVISA,TIPO_CAMBIO,TOTAL_MN,CLAVE_BENEFICIARIO,BENEFICIARIO,CTA_BANCARIA,FECHA_PROPUESTA,MEDIO_PAGO,FECHA_PAGO )" + " VALUES 	('" + idRamoCrS + "','" + idUnidadCrS + "','" + tipoCLC + "','" + folioCLC + "','" + folioDependendia + "','" + estatusCLC + "','" + proceso + "','" + fechaCaptura + "','" + fechaAplicacion + "','" + aplicacionContable + "','" + usuarioCaptura + "','" + aprobRevisor + "', " + " '" + usuarioRevisor + "','" + fechaRevision + "','" + aprobAutorizador + "','" + usuarioAutorizador + "','" + fechaAutorizacion + "','" + numeroAMF + "','" + leyenda + "','" + descLeyenda + "','" + referencia1 + "','" + referencia2 + "','" + folioAMF + "','" + noOfAMF + "','" + fechaReferencia1 + "','" + divisa + "','" + totalDivisa + "','" + tipoCambio + "','" + totalMN + "','" + claveBeneficiario + "','" + beneficiario + "','" + ctaBancaria + "','" + fechaPropuesta + "','" + medioPago + "','" + fechaPago + "'  )");
            int intr = ps.executeUpdate();
            if (intr > 0) {
                respuesta = "guardado";
                cuantos = cuantos + 1;
                //System.out.println("cuantos:"+cuantos);
                conn.commit();
                /*if(ejecutar >=100){
					System.out.println("si mas de 100:"+cuantos);
					conn.commit();
				}*/
            } else {
                respuesta = "no_guardado";
                conn.rollback();
            }
            conn.close();
        } catch (Exception e) {
            log.error("Error Guardar SIAFF: " + e);
            conn.rollback();
            conn.close();
        }
        return respuesta;
    }

    public String limpiarTabla(String nomTabla) throws SQLException {
        String valor = "";
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement("DELETE FROM " + nomTabla);
            int intr = ps.executeUpdate();
            if (intr > 0) {
                valor = "borrado";
                conn.commit();
            } else {
                valor = "no_borrado";
                conn.rollback();
            }
        } catch (Exception e) {
            log.error("Error Limpiar Tabla: " + e);
            conn.rollback();
            conn.close();
        }
        return valor;
    }
}
