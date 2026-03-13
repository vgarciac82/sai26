package com.syc.gestion.reportes;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
//import java.util.Locale;
import java.util.StringTokenizer;
import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
import com.syc.auditoria.core.Auditoria;
import com.syc.auditoria.core.AuditoriaManager;
//import com.syc.contable.anteproyecto.ComparaSaiSicopManager;
import com.syc.contable.core.AnteProyectoAut;
import com.syc.contable.core.AnteProyectoAutCalendario;
import com.syc.contable.core.Auxiliares;
import com.syc.contable.core.PAOP;
import com.syc.contable.core.Balanza;
import com.syc.contable.core.Poliza;
import com.syc.contable.core.Saldo;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.reportes.core.Acumulado;
import com.syc.gestion.reportes.core.General;
import com.syc.gestion.reportes.core.HomoViati;
import com.syc.gestion.reportes.core.ReporteConf;
import com.syc.gestion.reportes.core.ReporteConfManager;
import com.syc.gestion.reportes.core.ReporteManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.gestion.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReporteBussinesLogic extends DataSourceManager {

    public String NombreReporte = "";

    private static Logger log = LoggerFactory.getLogger(ReporteBussinesLogic.class);

    public ReporteBussinesLogic(String jniName) {
        super.init(jniName);
    }

    public ReporteConf getConfiguracion(String u_login, ReporteConf in_rc) throws GestionException {
        ReporteConf rc = null;
        // ReporteConf rc_in = new ReporteConf();
        Connection conn = null;
        try {
            conn = getConnection();
            rc = ReporteConfManager.select(conn, in_rc);
        } catch (SQLException exc) {
            log.warn("Obteniendo Caso Operacion por Usuario", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return rc;
    }

    /*
	 * 
	 * public void Reporte(HttpServletRequest req, HttpServletResponse resp,
	 * String cmd, String rpt_param) { int command = Integer.parseInt(cmd);
	 * 
	 * switch (command) {
	 * 
	 * case GestionInterface.RPT_CONSOLIDADO: //1
	 * 
	 * break;
	 * 
	 * case GestionInterface.RPT_GENERAL: // 2 break; case
	 * GestionInterface.RPT_POR_EMPLEADO: // 3 break; case
	 * GestionInterface.RPT_POR_AREA: // 4
	 * 
	 * break; case GestionInterface.RPT_DETALLADO: // 5 break; }
	 * 
	 * 
	 * }
	 * 
	 * public void Reporte(String cmd, String rpt_param) { int command =
	 * Integer.parseInt(cmd);
	 * 
	 * switch (command) {
	 * 
	 * case GestionInterface.RPT_CONSOLIDADO: //1
	 * 
	 * break;
	 * 
	 * case GestionInterface.RPT_GENERAL: // 2 break; case
	 * GestionInterface.RPT_POR_EMPLEADO: // 3 break; case
	 * GestionInterface.RPT_POR_AREA: // 4 //ReportePorArea(String area, String
	 * regfechaini, String regfechafin, boolean detallado) break; case
	 * GestionInterface.RPT_DETALLADO: // 5 break; }
	 * 
	 * 
	 * }
	 */
    /*
	 * 
	 * SELECT imx.FOLIO, imx.REFERENCIA , CASE WHEN imx.tipoasunto = 'I' THEN
	 * imx.REMINUNOMBRE ELSE imx.RENOMBRE END AS remitente , imx.ASUNTO ,
	 * CONVERT(varchar ,DPC_F_LIMITE,103) AS DPC_F_LIMITE , imx.RESULOGIN ,
	 * imx.RESUNOMBRE , imx.OBSERVACIONES , CASE WHEN bc.cerrado = 'N' THEN
	 * 'PENDIENTE' ELSE 'CONCLUIDO' END AS estatus , CONVERT(varchar ,GETDATE()
	 * ,103) AS HOY FROM IMXEXPEDIENTES imx INNER JOIN cg_bitacora_caso bc ON
	 * imx.folio = bc.folio WHERE imx.folio = ''
	 */
    public StringBuffer ReporteGeneral_(String registroFechaIni, String registroFechaFin, String rem_idarea, String rem_area, String rem_id, String rem_nombre, String rem_tipoInstruccion, String rem_estatus, String rem_prioridad, String res_idarea, String res_area, String res_id, String res_nombre, // Esteban Badillo. Fecha: 11/Sep/2009.
    // Descripcion: Se agrega un campo que condiciona la proyeccion de
    // los campos "Recibidos de" / "Enviados a" del Reporte General.
    String orden) {
        Connection conn = null;
        StringBuffer sb = new StringBuffer();
        ResultSet rs = null;
        try {
            String sql = "";
            String where = "";
            String whereSubQuery = "";
            String where_2 = "";
            // Esteban Badillo. Fecha: 12/Febrero/2010
            // Descripcion: Se establece las fechas de inicio y fin con la hora
            // para evitar omisión de registros.
            if (registroFechaIni.trim().length() > 0)
                registroFechaIni += " 00:00:00";
            if (registroFechaFin.trim().length() > 0)
                registroFechaFin += " 23:59:59";
            if (!rem_prioridad.equals("")) {
                where += " AND imx.prioridad = '" + rem_prioridad + "'";
                // whereSubQuery += " AND maxSO.prioridad = '" + rem_prioridad +
                // "'";
            }
            if (res_idarea != "") {
                where += " AND bo.responsable_area = '" + res_idarea + "'";
                whereSubQuery += " AND maxSO.responsable_area = '" + res_idarea + "'";
            }
            if (rem_idarea != "") {
                where += " AND bo.REMITENTE_AREA = '" + rem_idarea + "'";
                whereSubQuery += " AND  maxSO.REMITENTE_AREA = '" + rem_idarea + "'";
            }
            if (res_id != "") {
                where += " AND  bo.responsable_id = '" + res_id + "'";
                whereSubQuery += " AND  maxSO.responsable_id = '" + res_id + "'";
            }
            if (rem_id != "") {
                where += " AND  bo.remitente_id = '" + rem_id + "'";
                whereSubQuery += " AND  maxSO.remitente_id = '" + rem_id + "'";
            }
            if (!rem_tipoInstruccion.equals("")) {
                // where += " AND co.USER01 LIKE '%" + rem_tipoInstruccion +
                // "%'";
                // whereSubQuery += " AND maxSO.tipo_instruccion LIKE '%" +
                // rem_tipoInstruccion + "%'";
                where_2 += " AND T.TIPO_INSTRUCCION LIKE '%" + rem_tipoInstruccion + "%' ";
            }
            if (!rem_estatus.equals("")) {
                if (rem_estatus.equals(String.valueOf(GestionInterface.RPT_CNS_CONCLUIDOS))) {
                    // where += " AND bo.terminada = 'S'";
                    // whereSubQuery += " AND maxSO.terminada = 'S'";
                    where_2 += " AND T.cerrado = 'CONCLUIDO' ";
                }
                if (rem_estatus.equals(String.valueOf(GestionInterface.RPT_CNS_PENDIENTES))) {
                    // where += " AND bo.terminada = 'N'";
                    // whereSubQuery += " AND maxSO.terminada = 'N'";
                    where_2 += " AND (T.cerrado = 'VENCIDO' OR T.cerrado = 'NO VENCIDO') ";
                }
                if (rem_estatus.equals(String.valueOf(GestionInterface.RPT_CNS_NOVENCIDOS))) {
                    // where +=
                    // " AND bo.terminada = 'N' AND CONVERT(DATETIME,co.user03, 103) > GETDATE()";
                    // //whereSubQuery +=
                    // " AND maxSO.terminada = 'N' AND CONVERT(DATETIME,maxSO.user03, 103) > GETDATE()";
                    where_2 += " AND T.cerrado = 'NO VENCIDO' ";
                }
                if (rem_estatus.equals(String.valueOf(GestionInterface.RPT_CNS_VENCIDOS))) {
                    // where +=
                    // " AND bo.terminada = 'N' AND CONVERT(DATETIME,co.user03, 103) < GETDATE()";
                    // //whereSubQuery +=
                    // " AND maxSO.terminada = 'N' AND CONVERT(DATETIME,maxSO.user03, 103) < GETDATE()";
                    where_2 += " AND T.cerrado = 'VENCIDO' ";
                }
            }
            if (registroFechaIni == "" && registroFechaFin == "") {
                // 19 Ene 2010 comentado por solicitud de Martin Bonilla, que no
                // traiga fechas por default
                // where +=
                // " AND CONVERT(DATETIME, vimx.fechaRegistro, 103) BETWEEN CONVERT(DATETIME, '"
                // + "01/01/" + Integer.toString(c.get(Calendar.YEAR)) +
                // "', 103) AND CONVERT(DATETIME, '" +
                // Integer.toString(c.get(Calendar.DATE)) + "/" +
                // Integer.toString(c.get(Calendar.MONTH + 1)) + "/" +
                // Integer.toString(c.get(Calendar.YEAR)) + "', 103) ";
                // whereSubQuery +=
                // " AND CONVERT(DATETIME, maxSO.fechaRegistro, 103) BETWEEN CONVERT(DATETIME, '"
                // + "01/01/" + Integer.toString(c.get(Calendar.YEAR)) +
                // "', 103) AND CONVERT(DATETIME, '" +
                // Integer.toString(c.get(Calendar.DATE)) + "/" +
                // Integer.toString(c.get(Calendar.MONTH + 1)) + "/" +
                // Integer.toString(c.get(Calendar.YEAR)) + "', 103)";
            } else {
                where += " AND CONVERT(DATETIME, imx.DPC_F_REGISTRO, 103) BETWEEN CONVERT(DATETIME, '" + registroFechaIni + "', 103) AND CONVERT(DATETIME, '" + registroFechaFin + "', 103) ";
                whereSubQuery += " AND CONVERT(DATETIME, maxIMX.DPC_F_REGISTRO, 103) BETWEEN CONVERT(DATETIME, '" + registroFechaIni + "', 103) AND CONVERT(DATETIME, '" + registroFechaFin + "', 103) ";
            }
            sql = "SELECT * FROM (  " + "\rSELECT " + "\r		bo.FOLIO AS FOLIO, " + "\r		imx.REFERENCIA, " + "\r		ca1.D_DESCRIPCION AS DESC_AREA_REM, " + "\r		bo.REMITENTE_AREA, " + "\r		ce1.CE_AP_PATERNO + ' ' + ce1.CE_AP_MATERNO + ' ' + ce1.CE_NOMBRE_COMPLETO AS NOMBRE_REM, " + "\r		cerrado = CASE WHEN bo.TERMINADA = 'S' THEN  " + "\r					isNull( (select distinct " + "\r								CASE " + "\r			  						WHEN cosq.user03 ='' THEN 'NO VENCIDO' " + "\r			  						WHEN cosq.user03 =' 23:59:59' THEN 'NO VENCIDO'    " + "\r			  						WHEN cosq.user03 > GETDATE() THEN 'NO VENCIDO' " + "\r			  						WHEN cosq.user03 < GETDATE() THEN 'VENCIDO' " + "\r			  					END   " + "\r							from cg_caso_operacion cosq, cg_bitacora_operacion bosq  " + "\r							where  " + "\r								cosq.id_caso_oper = bosq.SECUENCIAL_OPERACION and " + "\r								cosq.id_caso = bosq.id_caso  " + "\r								and bosq.folio = bo.folio " + "\r								and bosq.responsable_id = bo.remitente_id   " + "\r								and bosq.remitente_id = bo.remitente_id   " + "\r								and bosq.terminada = 'N'),'CONCLUIDO')  " + "\r				ELSE " + "\r			  		CASE " + "\r			  			WHEN co.user03 ='' THEN 'NO VENCIDO' " + "\r			  			WHEN co.user03 =' 23:59:59' THEN 'NO VENCIDO'     " + "\r			  			WHEN co.user03 > GETDATE() THEN 'NO VENCIDO' " + "\r			  			WHEN co.user03 < GETDATE() THEN 'VENCIDO' " + "\r			  		END   " + "\r				END, " + "\r			isNull(CASE            " + "\r				WHEN co.USER01 LIKE '%Procedente%' THEN CO.USER01  " + "\r				WHEN co.USER01 LIKE '%Grupal%' THEN CO.USER01  " + "\r				WHEN co.USER01 LIKE '%Coordinada%' THEN CO.USER01 " + "\r				WHEN bo.id_operacion = 7 and co.USER01 LIKE '%Conocimiento%' THEN CO.USER01   " + "\r				WHEN bo.id_operacion = 7 and co.USER01 NOT LIKE '%Conocimiento%' THEN 'COPIA'   " + "\r				ELSE " + "\r					(  " + "\r					select user01 " + "\r					from cg_caso_operacion cosq WITH(NOLOCK), cg_bitacora_operacion bosq WITH(NOLOCK) " + "\r					where cosq.id_caso_oper = bosq.SECUENCIAL_OPERACION  " + "\r						and cosq.id_caso = bosq.id_caso  " + "\r						and bosq.folio = bo.folio " + "\r						and cosq.id_caso_oper IN  " + "\r						( " + "\r						   SELECT MAX(cossq.id_caso_oper) FROM cg_caso_operacion cossq WITH(NOLOCK) " + "\r						   WHERE cossq.id_caso = bosq.id_caso " + "\r								and cossq.id_caso_oper < bo.SECUENCIAL_OPERACION " + "\r								and isnull(cossq.user01,'') <> ''  " + "\r						) " + "\r					) " + "\r				END , 'Registro') AS TIPO_INSTRUCCION,  " + "\r			  CASE WHEN imx.prioridad = 'N' THEN 'NORMAL' ELSE 'URGENTE' END as prioridad, " + "\r			  co.CO_OBSERVACION AS ASUNTO,				 " + "\r			  CASE WHEN bo.id_operacion <> 3 THEN ce2.CE_AP_PATERNO + ' ' + ce2.CE_AP_MATERNO + ' ' + ce2.CE_NOMBRE_COMPLETO ELSE " + "\r			  (select ce22.CE_AP_PATERNO + ' ' + ce22.CE_AP_MATERNO + ' ' + ce22.CE_NOMBRE_COMPLETO " + "\r			   from dbo.CG_BITACORA_OPERACION AS bo2 WITH(NOLOCK), dbo.CG_CAT_EMPLEADO AS ce22 WITH(NOLOCK) " + "\r			   where  bo.id_caso = bo2.id_caso  " + "\r					and bo.secuencial_operacion+1 = bo2.secuencial_operacion  " + "\r					and ce22.CE_OS_RESPONSABLE = bo2.RESPONSABLE_ID ) " + "\r			   END as nombre_resp,	 " + "\r			  CASE WHEN bo.id_operacion <> 3 THEN ca2.D_DESCRIPCION ELSE " + "\r			  ( select cat_area.d_descripcion FROM CG_CAT_AREAS cat_area where  bo.responsable_area = cat_area.id_area ) " + "\r			   END as desc_area_resp,	 " + "\r	       CONVERT(varchar,bo.fecha_inicio,103) AS FECHAREGISTRO, " + // +"\r	       co.USER03 AS FECHALIMITE,  "
            "\r           CONVERT(VARCHAR, CASE " + "\r			  WHEN co.user03 NOT LIKE '%__/__/____%' OR co.USER03 IS NULL THEN " + "\r				 CONVERT(VARCHAR, dbo.FnRptGenResFechaLimite(bo.ID_CASO, bo.SECUENCIAL_OPERACION), 103) " + "\r			  ELSE co.user03 " + "\r		   END, 103) AS FECHALIMITE, " + "\r           CONVERT(VARCHAR,CASE " + "\r              WHEN bo.id_operacion = 2 THEN bo.FECHA_INICIO " + "\r              ELSE dbo.FnRptGenResFechaEnvio(bo.ID_CASO, bo.SECUENCIAL_OPERACION) " + "\r           END, 103) AS fechaenvio " + // +"\r	       case when bo.id_operacion = 2 then CONVERT(varchar,bo.FECHA_INICIO,103) else null end as fechaenvio "
            "\r 	 FROM  " + "\r	       dbo.CG_CASO_OPERACION AS co WITH(NOLOCK) LEFT OUTER JOIN " + "\r	       dbo.CG_BITACORA_OPERACION AS bo WITH(NOLOCK) ON bo.ID_CASO = co.ID_CASO AND bo.SECUENCIAL_OPERACION = co.ID_CASO_OPER INNER JOIN " + "\r	       dbo.CG_CASO AS c WITH(NOLOCK) ON c.ID_CASO = co.ID_CASO LEFT OUTER JOIN " + "\r	       dbo.IMXEXPEDIENTES AS imx WITH(NOLOCK) ON imx.ID_GABINETE = c.C_ID_GABINETE INNER JOIN " + "\r	       dbo.CG_CAT_EMPLEADO AS ce2 WITH(NOLOCK)ON ce2.CE_OS_RESPONSABLE = co.CO_RESPONSABLE INNER JOIN " + "\r	       dbo.CG_CAT_AREAS AS ca2 WITH(NOLOCK) ON ce2.ID_AREA = ca2.ID_AREA INNER JOIN " + "\r	       dbo.CAT_PUESTOS AS cp2 WITH(NOLOCK) ON ce2.ID_PUESTO = cp2.ID_PUESTO INNER JOIN " + "\r	       dbo.CG_CAT_EMPLEADO AS ce1 WITH(NOLOCK) ON ce1.CE_OS_RESPONSABLE = bo.REMITENTE_ID INNER JOIN " + "\r	       dbo.CG_CAT_AREAS AS ca1 WITH(NOLOCK) ON ce1.ID_AREA = ca1.ID_AREA " + "\r	 WHERE (c.C_FOLIO NOT LIKE 'TMP-%')  " + "\r	       AND bo.id_operacion IN (2,3,7,10,9,11)   " + where + "\r	       AND bo.SECUENCIAL_OPERACION IN ( " + "\r	               SELECT MAX(maxSO.secuencial_operacion) " + "\r	               FROM CG_BITACORA_OPERACION maxSO WITH(NOLOCK) INNER JOIN " + "\r	                dbo.IMXEXPEDIENTES AS maxIMX WITH(NOLOCK) ON maxIMX.ID_GABINETE = maxSO.ID_GABINETE " + "\r	               WHERE maxSO.folio = c.C_FOLIO " + whereSubQuery + "\r	 ) " + "\r	) T WHERE T.TIPO_INSTRUCCION NOT IN ('COPIA')  " + where_2 + "\r	ORDER BY T.FOLIO  ";
            System.out.println("query de los reportes__ =" + sql);
            // ///////////////
            Runtime runtime = Runtime.getRuntime();
            long freeMemory = runtime.freeMemory();
            long totalMemory = runtime.totalMemory();
            long maxMemory = runtime.maxMemory();
            System.out.println("---------Antes de ejecutar el reporte-----------------");
            System.out.println("Max   Memory  :" + (maxMemory / 1024.0) + " KB");
            System.out.println("Total Memory  :" + (totalMemory / 1024.0) + " KB");
            System.out.println("Free  Memory  :" + (freeMemory / 1024.0) + " KB");
            System.out.println("Used  Memory  :" + (totalMemory / 1024.0 - freeMemory / 1024.0) + " KB");
            conn = getConnection();
            List<?> gral = new ArrayList<Object>();
            gral = ReporteManager.selectAll(conn, sql, 3);
            String tr = "";
            if (gral.isEmpty()) {
                tr = "<tr class=\"alternateRow\">" + "	<td colspan=\"10\"><h3>No hay informaci&oacute;n para mostrar</h3></td>" + "</tr>";
                sb.append(tr);
            } else {
                int row = 0;
                for (Iterator<?> iter = gral.iterator(); iter.hasNext(); row++) {
                    General a = (General) iter.next();
                    tr = RenglonDatoGralHTML(a.getFolio(), a.getReferencia(), a.getArea(), a.getAreaRemitente(), a.getNombreRemitente(), a.getCerrado(), a.getTipoInstruccion(), a.getPrioridad(), a.getAsunto(), a.getResponsableAreaDesc(), a.getResponsableNombre(), a.getFechaRegistro(), a.getFechaEnvio(), a.getFechaLimite(), row, // Esteban Badillo. Fecha: 10/Sep/2009.
                    // Descripcion: Se agrega un campo condicional para
                    // la proyección de los campos "Recibidos de" /
                    // "Enviados a" del Reporte General
                    orden);
                    sb.append(tr);
                }
                freeMemory = runtime.freeMemory();
                System.out.println("---------Despues de ejecutar el reporte-----------------");
                System.out.println("Max   Memory  :" + (maxMemory / 1024.0) + " KB");
                System.out.println("Total Memory  :" + (totalMemory / 1024.0) + " KB");
                System.out.println("Free  Memory  :" + (freeMemory / 1024.0) + " KB");
                System.out.println("Used  Memory  :" + (totalMemory / 1024.0 - freeMemory / 1024.0) + " KB");
                tr = RenglonDatoGralHTML("FIN", "", "", "", "", "", "", "", "", row + 1);
                sb.append(tr);
            }
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                // log.warn("Error en rollback", ex);
            }
            log.error("Actualizando caso", exc);
            // throw new GestionException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            rs = null;
            conn = null;
        }
        return sb;
    }

    /* ************************************************************************ */
    public String ReporteGeneral(String registroFechaIni, String registroFechaFin, String rem_idarea, String rem_area, String rem_id, String rem_nombre, String rem_tipoInstruccion, String rem_estatus, String rem_prioridad, String res_idarea, String res_area, String res_id, String res_nombre, // Esteban Badillo. Fecha: 11/Sep/2009.
    // Descripcion: Se agrega un campo que condiciona la proyeccion de
    // los campos "Recibidos de" / "Enviados a" del Reporte General.
    String orden) {
        Connection conn = null;
        StringBuffer sb = new StringBuffer();
        ResultSet rs = null;
        try {
            String where = "";
            if (registroFechaIni != "" && registroFechaFin != "") {
                where += " AND CONVERT(DATETIME, vimx.fechaRegistro, 103) BETWEEN CONVERT(DATETIME, '" + registroFechaIni + "', 103) AND CONVERT(DATETIME, '" + registroFechaFin + "', 103) ";
            }
            if (rem_area != "") {
                where += " AND  vimx.desc_area_rem IS NOT NULL AND vimx.desc_area_rem = '" + rem_area + "'";
            }
            if (rem_id != "" && rem_nombre != "") {
                where += " AND  vimx.nombre_rem = '" + rem_nombre + "'";
            }
            if (!rem_tipoInstruccion.equals("")) {
                where += "AND vimx.tipo_instruccion LIKE '%" + rem_tipoInstruccion + "%'";
            }
            if (!rem_estatus.equals("")) {
                if (rem_estatus.equals(String.valueOf(GestionInterface.RPT_CNS_CONCLUIDOS))) {
                    where += "AND vimx.terminada = 'S'";
                }
                if (rem_estatus.equals(String.valueOf(GestionInterface.RPT_CNS_PENDIENTES))) {
                    where += "AND vimx.terminada = 'N'";
                }
                if (rem_estatus.equals(String.valueOf(GestionInterface.RPT_CNS_NOVENCIDOS))) {
                    where += "AND vimx.terminada = 'N' AND vimx.fecha_compromiso > GETDATE()";
                }
                if (rem_estatus.equals(String.valueOf(GestionInterface.RPT_CNS_VENCIDOS))) {
                    where += "AND vimx.terminada = 'N' AND vimx.fecha_compromiso < GETDATE()";
                }
            }
            if (!rem_prioridad.equals("")) {
                where += "AND vimx.prioridad = '" + rem_prioridad + "'";
            }
            if (res_idarea != "" && res_area != "") {
                where += " AND  vimx.desc_area_resp IS NOT NULL AND vimx.desc_area_resp = '" + res_area + "'";
            }
            if (res_id != "" && res_nombre != "") {
                where += " AND  vimx.nombre_resp = '" + res_nombre + "'";
            }
            String sql = "";
            /*
			 * "SELECT * FROM (" + "   SELECT DISTINCT vimx.remitente_area, " +
			 * "          vimx.desc_area_rem, " + " 	     vimx.folio, " +
			 * " 	     vimx.referencia, " + " 	     vimx.nombre_rem, " +
			 * " 	     vimx.fechalimite, " + " 	     vimx.cerrado, " +
			 * " 	     vimx.destinatario, " + " 	     vimx.prioridad " +
			 * "     FROM vimx_reportes vimx " +
			 * "    WHERE FOLIO not like 'TMP%'" + "       " + " " +
			 * whereFechaRegistro + " " + whereFechaLimite + " " + whereArea +
			 * " GROUP BY vimx.remitente_area, " +
			 * "          vimx.desc_area_rem, " + "          vimx.folio, " +
			 * "          vimx.referencia, " + " 	     vimx.nombre_rem, " +
			 * " 	     vimx.desc_area_rem, " + " 	     vimx.fechalimite, " +
			 * " 	     vimx.cerrado, " + " 	     vimx.destinatario, " +
			 * " 	     vimx.prioridad " +
			 * "  ) t LEFT OUTER JOIN CG_CAT_AREAS ca ON (ca.id_area = t.remitente_area) "
			 * + "		   WHERE 1=1 " + "                     " + whereEstructura;
			 */
            /*
			 * sql =
			 * "SELECT distinct(vimx.REMITENTE_AREA), vimx.DESC_AREA_REM, ca.AREA_ESTRUCTURA"
			 * + "		, vimx.FOLIO, vimx.REFERENCIA" +
			 * "		, CONVERT(varchar ,vimx.FECHAREGISTRO,103) AS FECHAREGISTRO" +
			 * "		, CONVERT(varchar ,vimx.FECHALIMITE  ,103) AS FECHALIMITE" +
			 * "		, vimx.RESPONSABLE_AREA, vimx.DESC_AREA_RESP, vimx.RESPONSABLE_ID, vimx.NOMBRE_RESP"
			 * + "		, vimx.REMITENTE_ID" +
			 * "		, CASE WHEN imx.TIPOASUNTO = 'I' THEN imx.REMINUNOMBRE ELSE imx.RENOMBRE END AS REMITENTE"
			 * +
			 * "		, CASE WHEN imx.TIPOASUNTO = 'I' THEN 'INTERNO'        ELSE 'EXTERNO'    END AS TIPOASUNTO"
			 * +
			 * "		, CASE WHEN bc.CERRADO     = 'N' THEN 'PENDIENTE'      ELSE 'CONCLUIDO'  END AS ESTATUS"
			 * +
			 * "		, CASE WHEN vimx.PRIORIDAD = 'N' THEN 'NORMAL'         ELSE 'URGENTE'    END AS PRIORIDAD"
			 * + "		, imx.USER01, TIPOINSTRUCCION =" + "		   CASE " +
			 * "				WHEN imx.USER01 LIKE '%Procedente%%Conocimiento%' THEN 'ATENCION PROCEDENTE - PARA SU CONOCIMIENTO'"
			 * +
			 * "				WHEN imx.USER01 LIKE '%Conocimiento%%Procedente%' THEN 'PARA SU CONOCIMIENTO - ATENCION PROCEDENTE'"
			 * +
			 * "				WHEN imx.USER01 LIKE '%Procedente%'               THEN 'ATENCION PROCEDENTE'"
			 * +
			 * "				WHEN imx.USER01 LIKE '%Conocimiento%'             THEN 'PARA SU CONOCIMIENTO'"
			 * +
			 * "				WHEN imx.USER01 LIKE '%Coordinada%'               THEN 'ATENCION COORDINADA'"
			 * +
			 * "				WHEN imx.USER01 LIKE '%Grupal%'                   THEN 'ATENCION GRUPAL'"
			 * + "				ELSE ''" + "			 END" +
			 * "		, CONVERT(varchar, GETDATE(), 103) AS HOY" +
			 * "   FROM IMXEXPEDIENTES imx" +
			 * "          INNER JOIN CG_BITACORA_CASO bc    ON imx.FOLIO =   bc.FOLIO"
			 * +
			 * "          INNER JOIN VIMX_REPORTES    vimx  ON imx.FOLIO = vimx.FOLIO"
			 * +
			 * "     LEFT OUTER JOIN CG_CAT_AREAS     ca    ON ca.ID_AREA= vimx.REMITENTE_AREA"
			 * + "    WHERE 1=1" + whereArea + whereFechaRegistro +
			 * whereFechaLimite + whereEstructura + whereTipoAsunto +
			 * whereTipoInstruccion + whereEstatus + wherePrioridad +
			 * whereResponsableArea;
			 */
            sql = "SELECT" + "       vimx.folio," + "       vimx.referencia," + "       vimx.desc_area_rem, " + "		  vimx.remitente_area," + "		  vimx.nombre_rem," + "		  cerrado = " + "       CASE " + "           WHEN vimx.terminada = 'S' THEN 'CONCLUIDOS'" + "           WHEN vimx.terminada = 'N' AND vimx.fecha_compromiso < GETDATE() THEN 'VENCIDOS'" + "           WHEN vimx.terminada = 'N' AND vimx.fecha_compromiso > GETDATE() THEN 'NO VENCIDOS'" + "       END, " + "		  IsNull(vimx.tipo_instruccion, '') as tipo_instruccion," + "		  CASE WHEN vimx.prioridad = 'N' THEN 'NORMAL' ELSE 'URGENTE' END as prioridad ," + "		  vimx.asunto," + "		  vimx.nombre_resp," + "		  vimx.desc_area_resp," + "       vimx.fecharegistro, " + // Esteban Badillo. Fecha: 09/Ago/2009.
            // Descripcion: Se el campo fechalimite como fecha limite de
            // Fecha de e al Reporte General.
            "       vimx.fechalimite, " + "       case " + "          when vimx.id_operacion = 2 then vimx.fecha_inicio " + "          else null end as fechaenvio " + "FROM VIMX_REPORTES vimx " + // Ricardo:
            "WHERE vimx.ID_OPERACION IN (2,3) " + // Mauricio
            // indica
            // que
            // solo
            // operacion
            // 2
            // y
            // 3,
            // antes
            // estaba
            // asi(2,3,7,9,10,
            // 11)
            where + "  order by cerrado";
            System.out.println("query de los reportes =" + sql);
            conn = getConnection();
            List<?> gral = new ArrayList<Object>();
            gral = ReporteManager.selectAll(conn, sql, 3);
            // Reporte por Empleado
            System.out.println("regresa con el gral" + gral.size());
            sb.append(sql + "|");
            String tr = "";
            if (gral.isEmpty()) {
                tr = "<tr class=\"alternateRow\">" + "	<td colspan=\"10\"><h3>No hay informaci&oacute;n para mostrar</h3></td>" + "</tr>";
                sb.append(tr);
            } else {
                int row = 0;
                for (Iterator<?> iter = gral.iterator(); iter.hasNext(); row++) {
                    General a = (General) iter.next();
                    /*
					 * System.out.println("Antes de generar la tabla HTML");
					 * System.out.println("a.getFechaEnvio: " +
					 * a.getFechaEnvio());
					 * System.out.println("a.getFechaLimite: " +
					 * a.getFechaLimite());
					 */
                    tr = RenglonDatoGralHTML(a.getFolio(), a.getReferencia(), a.getArea(), a.getAreaRemitente(), a.getNombreRemitente(), a.getCerrado(), a.getTipoInstruccion(), a.getPrioridad(), a.getAsunto(), a.getResponsableAreaDesc(), a.getResponsableNombre(), a.getFechaRegistro(), a.getFechaEnvio(), a.getFechaLimite(), row, // Esteban Badillo. Fecha: 10/Sep/2009.
                    // Descripcion: Se agrega un campo condicional para
                    // la proyección de los campos "Recibidos de" /
                    // "Enviados a" del Reporte General
                    orden);
                    // if(row >463)
                    // System.out.println("Tr="+tr);
                    sb.append(tr);
                }
                // System.out.println("ROW="+ row);
                tr = RenglonDatoGralHTML("FIN", "", "", "", "", "", "", "", "", row + 1);
                sb.append(tr);
            }
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                // log.warn("Error en rollback", ex);
            }
            log.error("Actualizando caso", exc);
            // throw new GestionException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            rs = null;
            conn = null;
        }
        return sb.toString();
    }

    public String ReporteHomoViati(String no_oficio, String fechaini, String fechafin, String area) {
        Connection conn = null;
        StringBuffer sb = new StringBuffer();
        List<?> homoviati = new ArrayList<Object>();
        try {
            conn = getConnection();
            String sql = "select g.no_oficio as no_oficio,g.solicitante as area,max(b.fecha)as fecha " + " from " + " (select no_oficio,solicitante,id_gabinete from imxhomoviati where no_oficio not like 'HV-%') g," + " (select b_c_fecha_ini as fecha, b_c_id_gabinete as bgab  from cg_bitacora) b" + " where " + " g.id_gabinete=b.bgab " + (fechaini != null && fechafin != null ? " and b.fecha BETWEEN convert(datetime,'" + fechaini + "') and convert(datetime,'" + fechafin + "') " : "") + (no_oficio != null && !"".equals(no_oficio) ? " and g.no_oficio like '%" + no_oficio + "%'" : "") + (area != null && !"".equals(area) ? " and g.solicitante like '" + area + "'" : "") + " group by  g.no_oficio,g.solicitante";
            //
            homoviati = ReporteManager.selectHomoViati(conn, sql);
            String tr = "";
            int row = 0;
            if (homoviati.isEmpty()) {
                tr = "<tr class=\"alternateRow\">" + "	<td colspan=\"4\"><i>No hay informaci&oacute;n para mostrar</i></td>" + "</tr>";
                sb.append(tr);
            } else {
                for (Iterator<?> iter = homoviati.iterator(); iter.hasNext(); row++) {
                    HomoViati hv = (HomoViati) iter.next();
                    tr = "<tr class=" + ((row % 2) == 0 ? "AlternateRow" : "NormalRow") + ">" + "<td>" + hv.getArea() + "</td>" + "<td>" + hv.getFecha() + "</td>" + "<td>" + hv.getNo_oficio() + "</td>" + "</tr>";
                    // tr=hv.getArea()+"|"+hv.getFecha()+"|"+hv.getNo_oficio();
                    sb.append(tr);
                }
            }
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                // log.warn("Error en rollback", ex);
            }
            log.error("Actualizando caso", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return sb.toString();
    }

    public String ReportePresupuesto(String ep, String EjercicioFiscal, String Cuenta, String UnidadEjecutora, String RamoEP, String UnidadResponsableEP, String GrupoFuncional, String Funcion, String SubFuncion, String ProgramaGeneral, String ProgramaPresupuestario, String ActividadInstitucional, String Partida, String TipoGasto, String FuenteFinanciamiento, String EntidadFederativa, String Cartera, String UnidadNorativa, String ClaveCNA, String cOrddeBy) {
        Connection conn = null;
        StringBuffer sb = new StringBuffer();
        List<?> saldoList = new ArrayList<Object>();
        try {
            conn = getConnection();
            String sql = "SELECT  s.nClaveCNA,  s.ClaveSIAFF, s.ClaveInterna, s.MontoEnero, s.MontoFebrero, s.MontoMarzo, s.MontoAbril, s.MontoMayo, s.MontoJunio, s.MontoJulio, " + "           s.MontoAgosto, s.MontoSeptiembre, s.MontoOctubre, s.MontoNoviembre, s.MontoDiciembre, s.MontoAnual " + "     FROM  vSaldosAnuales s with(nolock) " + // "           tCatalogoUnidadResponsable u  "+
            // "    WHERE s.cUnidadEjecutora = u.cUnidadResponsable "+
            "		WHERE s.aEjercicioFiscal = '" + EjercicioFiscal + "'";
            sql += (Cuenta != "" && Cuenta != null ? "AND s.nCuentaP = '" + Cuenta + "'" : "");
            // tenia
            sql += (ep != "" && ep != null ? "AND s.ClaveSIAFF = '" + ep + "'" : "");
            // s.cSubCuenta
            // like
            // '%"+ep+"%'":"")
            sql += (RamoEP != "" && RamoEP != null ? "AND s.cRamoEP='" + RamoEP + "'" : "");
            sql += (UnidadResponsableEP != "" && UnidadResponsableEP != null ? "AND s.cUnidadResponsableEP='" + UnidadResponsableEP + "'" : "");
            sql += (GrupoFuncional != "" && GrupoFuncional != null ? "AND s.cGrupoFuncional='" + GrupoFuncional + "'" : "");
            sql += (Funcion != "" && Funcion != null ? "AND s.cFuncion='" + Funcion + "'" : "");
            sql += (SubFuncion != "" && SubFuncion != null ? "AND s.cSubFuncion='" + SubFuncion + "'" : "");
            sql += (ProgramaGeneral != "" && ProgramaGeneral != null ? "AND s.cProgramaGeneral='" + ProgramaGeneral + "'" : "");
            sql += (ProgramaPresupuestario != "" && ProgramaPresupuestario != null ? "AND s.cProgramaPresupuestario='" + ProgramaPresupuestario + "'" : "");
            sql += (ActividadInstitucional != "" && ActividadInstitucional != null ? "AND s.cActividadInstitucional='" + ActividadInstitucional + "'" : "");
            sql += (Partida != "" && Partida != null ? "AND s.cPartida='" + Partida + "'" : "");
            sql += (TipoGasto != "" && TipoGasto != null ? "AND s.cTipoGasto='" + TipoGasto + "'" : "");
            sql += (FuenteFinanciamiento != "" && FuenteFinanciamiento != null ? "AND s.cFuenteFinanciamiento='" + FuenteFinanciamiento + "'" : "");
            sql += (EntidadFederativa != "" && EntidadFederativa != null ? "AND s.cEntidadFederativa='" + EntidadFederativa + "'" : "");
            sql += (Cartera != "" && Cartera != null ? "AND s.cCartera='" + Cartera + "'" : "");
            sql += (UnidadNorativa != "" && UnidadNorativa != null ? "AND s.cUnidadNorativa='" + UnidadNorativa + "'" : "");
            sql += (UnidadEjecutora != "" && UnidadEjecutora != null ? "AND s.cUnidadEjecutora='" + UnidadEjecutora + "'" : "");
            sql += (ClaveCNA != "" && ClaveCNA != null ? "AND s.nClaveCNA='" + ClaveCNA + "'" : "");
            sql += (cOrddeBy != "" && cOrddeBy != null ? " Order by " + cOrddeBy + "" : "");
            //
            saldoList = ReporteManager.selectReportePresupuesto(conn, sql);
            String tr = "";
            int row = 0;
            if (saldoList.isEmpty()) {
                tr = "<tr class=\"alternateRow\">" + "	<td colspan=\"4\"><i>No hay informaci&oacute;n para mostrar</i></td>" + "</tr>";
                sb.append(tr);
            } else {
                for (Iterator<?> iter = saldoList.iterator(); iter.hasNext(); row++) {
                    Saldo epSaldo = (Saldo) iter.next();
                    tr = "<tr class=" + ((row % 2) == 0 ? "AlternateRow" : "NormalRow") + ">" + "<td>" + epSaldo.getClaveCNA() + "</td>" + "<td>" + epSaldo.getClaveSIAFF() + "</td>" + "<td>" + epSaldo.getClaveInterna() + "</td>" + "<td>" + epSaldo.getMontoEnero() + "</td>" + "<td>" + epSaldo.getMontoFebrero() + "</td>" + "<td>" + epSaldo.getMontoMarzo() + "</td>" + "<td>" + epSaldo.getMontoAbril() + "</td>" + "<td>" + epSaldo.getMontoMayo() + "</td>" + "<td>" + epSaldo.getMontoJunio() + "</td>" + "<td>" + epSaldo.getMontoJulio() + "</td>" + "<td>" + epSaldo.getMontoAgosto() + "</td>" + "<td>" + epSaldo.getMontoSeptiembre() + "</td>" + "<td>" + epSaldo.getMontoOctubre() + "</td>" + "<td>" + epSaldo.getMontoNoviembre() + "</td>" + "<td>" + epSaldo.getMontoDiciembre() + "</td>" + "<td>" + epSaldo.getMontoAnual() + "</td>" + "</tr>";
                    sb.append(tr);
                }
            }
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
            }
            log.error("Actualizando caso", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return sb.toString();
    }

    public List MultiReportePresupuestal(String EPs, String ProgramaPresupuestario, String Partida, String UnidadResponsableEP, String UnidadEjecutora, String Cartera, String ChkReintegros, String ChkRectificaciones, String ChkAdecuaciones, String ChkPagosAnticipados, String ChkPlurianuales, String ChkOriginal, String ChkModificado, String ChkAmpAutorizada, String ChkRedAutorizada, String ChkAmpenTramite, String ChkRedenTramite, String ChkReienTramite, String ChkRectificacion, String ChkRedSHCPenTramite, String ChkRedSHCPAplicada, String ChkApartado, String ChkPrecomprometido, String ChkComprometido, String ChkDevengado, String ChkEjernoPagado, String ChkDisponibleNeto, String ChkDisponibleBruto, String ChkEjercidoPagado, String sULogin) {
        Connection conn = null;
        List ReportePrespuestalList = new ArrayList();
        try {
            conn = getConnection();
            /**
             * ****************** LIMPIA LAS EPS DE COMILLAS********f *********
             */
            if (EPs.length() > 0)
                EPs = EPs.substring(1, EPs.length() - 1);
            ReportePrespuestalList = ReporteManager.callMultiReportePresupuestal(conn, EPs, ProgramaPresupuestario, Partida, UnidadResponsableEP, UnidadEjecutora, Cartera, ChkReintegros, ChkRectificaciones, ChkAdecuaciones, ChkPagosAnticipados, ChkPlurianuales, ChkOriginal, ChkModificado, ChkAmpAutorizada, ChkRedAutorizada, ChkAmpenTramite, ChkRedenTramite, ChkReienTramite, ChkRectificacion, ChkRedSHCPenTramite, ChkRedSHCPAplicada, ChkApartado, ChkPrecomprometido, ChkComprometido, ChkDevengado, ChkEjernoPagado, ChkDisponibleNeto, ChkDisponibleBruto, ChkEjercidoPagado, sULogin);
            log.debug("salida de callMultiReportePresupuestal");
        } catch (SQLException exc) {
            log.error("Error en MultiReporte Presupuestal", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return ReportePrespuestalList;
    }

    public List ReporteAdecuacionesPorUN(String Ep, String UnidadResponsableEP, String Folio, String Estatus, String FechaIni, String FechaFin, String Monto, String FolioSICOP, String FolioMAP, String Login) {
        Connection conn = null;
        List ReporteAdecuacionesPorUNList = new ArrayList();
        try {
            conn = getConnection();
            ReporteAdecuacionesPorUNList = ReporteManager.ReporteAdecuacionesPorUN(conn, Ep, UnidadResponsableEP, Folio, Estatus, FechaIni, FechaFin, Monto, FolioSICOP, FolioMAP, Login);
            log.debug("salida de Reporte Adecuaciones x UN");
        } catch (SQLException exc) {
            log.error("Error en Reporte Adecuaciones x UN", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return ReporteAdecuacionesPorUNList;
    }

    public Sheet callExcelMultiReportePresupuestal(Sheet hoja, List ReportePrespuestalList, int renglonInicio) throws Exception {
        try {
            Util util = new Util();
            hoja = util.creaExcelReportePrespuestal(ReportePrespuestalList, hoja, renglonInicio);
        } catch (SQLException exc) {
            log.error("Error en MultiReporte Presupuestal", exc);
        } finally {
            log.warn("Cerrando conexion a base de datos");
        }
        return hoja;
    }

    public Sheet callExcelReporteAdecuacionesPorUN(Sheet hoja, List ReporteAdecuacionesPorUNList, int renglonInicio) throws Exception {
        try {
            Util util = new Util();
            hoja = util.creaExcelReporteAdecuacionesPorUN(ReporteAdecuacionesPorUNList, hoja, renglonInicio);
        } catch (SQLException exc) {
            log.error("Error en Reporte Adecuaciones por UN", exc);
        } finally {
            log.warn("Cerrando conexion a base de datos");
        }
        return hoja;
    }

    public String MultiReporteResultado(String ep, String EjercicioFiscal, String Cuenta, String UnidadEjecutora, String RamoEP, String UnidadResponsableEP, String GrupoFuncional, String Funcion, String SubFuncion, String ProgramaGeneral, String ProgramaPresupuestario, String ActividadInstitucional, String Partida, String TipoGasto, String FuenteFinanciamiento, String EntidadFederativa, String Cartera, String UnidadNormativa, String ClaveCNA, String cOrddeBy, String cGroupBy, String InfoRegMes, String TipoReporte, String Usuario, String Filtro, String[] nCtasPresup, String[] dCtasPresup, String Componentes, String Resumen, String tipoEP, String Capitulo) throws Exception {
        Connection conn = null;
        StringBuffer sb = new StringBuffer();
        String opcion = "";
        String unidadResp = "";
        int role = 0;
        String modulo = "PRESUPUESTO";
        conn = getConnection();
        unidadResp = ReporteManager.obtieneUR(conn, Usuario);
        role = ReporteManager.obtieneRole(conn, Usuario);
        int grupoJefatura = ReporteManager.obtieneGrupo(conn, Usuario);
        log.info("Unidad a la que pertenece el usuaio" + unidadResp);
        String Cap = Capitulo;
        if (UnidadEjecutora == "" && role == 0) {
            if (!("A02".equalsIgnoreCase(unidadResp)) && grupoJefatura == 0) {
                UnidadEjecutora = ReporteManager.obtieneVistas(conn, Usuario, modulo);
                log.info("Unidades a las que tiene vista" + UnidadEjecutora);
            }
        }
        List<?> saldoList = new ArrayList<Object>();
        try {
            long tInicioCond = System.currentTimeMillis();
            log.info("Ejecutando multireporte: Generando condiciones: Inicio[ " + (new Date()).toString() + "]");
            String sql = "";
            // sql+=(Cuenta !=""&&Cuenta
            // !=null?" AND s.nCuentaP = '"+Cuenta+"'":"");
            // sql+=(Cuenta !=""&&Cuenta
            // !=null?" AND s.nCuenta like '"+Cuenta+"%'":"");
            sql += (ep != "" && ep != null ? " AND cSubCuenta = '" + ep + "'" : "");
            sql += (EjercicioFiscal != "" && EjercicioFiscal != null ? " AND aEjercicioFiscal_1 = '" + EjercicioFiscal + "'" : "");
            sql += (RamoEP != "" && RamoEP != null ? " AND cRamo_2='" + RamoEP + "'" : "");
            sql += (UnidadResponsableEP != "" && UnidadResponsableEP != null ? " AND cUnidadResponsable_3='" + UnidadResponsableEP + "'" : "");
            sql += (GrupoFuncional != "" && GrupoFuncional != null ? " AND cGrupoFuncional_4='" + GrupoFuncional + "'" : "");
            sql += (Funcion != "" && Funcion != null ? " AND cFuncion_5='" + Funcion + "'" : "");
            sql += (SubFuncion != "" && SubFuncion != null ? " AND cSubFuncion_6='" + SubFuncion + "'" : "");
            sql += (ProgramaGeneral != "" && ProgramaGeneral != null ? " AND cProgramaGeneral_7='" + ProgramaGeneral + "'" : "");
            sql += (ProgramaPresupuestario != "" && ProgramaPresupuestario != null ? " AND cProgramaPresupuestario_9='" + ProgramaPresupuestario + "'" : "");
            sql += (ActividadInstitucional != "" && ActividadInstitucional != null ? " AND cActividadInstitucional_8='" + ActividadInstitucional + "'" : "");
            sql += (Partida != "" && Partida != null ? " AND cPartida_10='" + Partida + "'" : "");
            sql += (TipoGasto != "" && TipoGasto != null ? " AND cTipoGasto_11='" + TipoGasto + "'" : "");
            sql += (FuenteFinanciamiento != "" && FuenteFinanciamiento != null ? " AND cFuenteFinanciamiento_12='" + FuenteFinanciamiento + "'" : "");
            sql += (EntidadFederativa != "" && EntidadFederativa != null ? " AND cEntidadFederativa_13='" + EntidadFederativa + "'" : "");
            sql += (Cartera != "" && Cartera != null ? " AND cCartera_14='" + Cartera + "'" : "");
            sql += (UnidadNormativa != "" && UnidadNormativa != null ? " AND cUnidadResponsable_16='" + UnidadNormativa + "'" : "");
            sql += (UnidadEjecutora != "" && UnidadEjecutora != null ? " AND cUnidadResponsable_15 IN ('" + UnidadEjecutora + "')" : "");
            sql += (ClaveCNA != "" && ClaveCNA != null ? " AND nClaveCNA='" + ClaveCNA + "'" : "");
            sql += (Capitulo != "" && Capitulo != null ? " AND SUBSTRING(cPartida_10,1,1)='" + Capitulo + "'" : "");
            long tFinCond = System.currentTimeMillis();
            log.info("Ejecutando multireporte: Termino de generar condiciones: Fin[ " + (new Date()).toString() + "]");
            log.info("Ejecutando multireporte: Se genero la condicion en :[ " + (tFinCond - tInicioCond) + "ms] equivalente a [" + (tFinCond - tInicioCond) / 1000 + "s.]");
            long tInicioEjec = System.currentTimeMillis();
            log.info("Ejecutando multireporte: Inicio de llamado al SP: Inicio[ " + (new Date()).toString() + "]");
            log.info("Capitulo: " + Cap);
            log.debug("EXECUTE sp_pMultiReporte_syc '" + sql.replace("'", "''") + "', '" + Cuenta.replace("'", "''") + "', '" + cOrddeBy.replace("'", "''") + "', '" + cGroupBy.replace("'", "''") + "', '" + InfoRegMes.replace("'", "''") + "', '" + TipoReporte.replace("'", "''") + "', '" + Usuario.replace("'", "''") + "', @Salida OUTPUT; ");
            saldoList = ReporteManager.callMultiReporte(conn, sql, Cuenta, cOrddeBy, cGroupBy, InfoRegMes, TipoReporte, Usuario, nCtasPresup, dCtasPresup, tipoEP);
            long tFinEjec = System.currentTimeMillis();
            log.info("Ejecutando multireporte: Termino de ejecutarSP: Fin[ " + (new Date()).toString() + "]");
            log.info("Ejecutando multireporte: Se ejecuto el multireporte en :[ " + (tFinEjec - tInicioEjec) + "ms] equivalente a [" + (tFinEjec - tInicioEjec) / 1000 + "s.]");
            log.debug("salida de sp_pMultiReporte_syc ");
            String tr = "";
            int row = 0;
            long tInicioRender = System.currentTimeMillis();
            log.info("Ejecutando multireporte: Inicio de pintado de resultado: Inicio[ " + (new Date()).toString() + "]");
            if (saldoList.isEmpty()) {
                tr = "<tr class=\"alternateRow\">" + "	<td colspan=\"4\"><i>No hay informaci&oacute;n para mostrar</i></td>" + "</tr>";
                sb.append(tr);
                log.debug("No hay informaci&oacute;n para mostrar");
            } else {
                for (Iterator<?> iter = saldoList.iterator(); iter.hasNext(); row++) {
                    Saldo epSaldo = (Saldo) iter.next();
                    tr = "<tr class=" + ((row % 2) == 0 ? "AlternateRow" : "NormalRow") + ">";
                    tr = tr + "<td align=\"center\">" + (row + 1) + "</td>";
                    if (!TipoReporte.equals("GENERAL")) {
                        tr = tr + "<td align=\"center\">" + epSaldo.getDcuenta() + "</td>";
                    }
                    if (cGroupBy.equals("cSubCuenta")) {
                        tr = tr + "<td align=\"center\">" + epSaldo.getClaveCNA() + "</td>";
                    }
                    if (cGroupBy.equals("cSubCuenta")) {
                        tr = tr + "<td align=\"center\">" + epSaldo.getEp() + "</td>";
                    }
                    if (cGroupBy.equals("aEjercicioFiscal_1") || (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))) {
                        tr = tr + "<td align=\"center\">" + epSaldo.getEjercicioFiscal() + "</td>";
                    }
                    if (cGroupBy.equals("cRamo_2") || (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))) {
                        tr = tr + "<td align=\"center\">" + epSaldo.getRamoEP() + "</td>";
                    }
                    if (cGroupBy.equals("cUnidadResponsable_3") || (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))) {
                        tr = tr + "<td align=\"center\">" + epSaldo.getUnidadResponsableEP() + "</td>";
                    }
                    if (cGroupBy.equals("cGrupoFuncional_4") || (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))) {
                        tr = tr + "<td align=\"center\">" + epSaldo.getGrupoFuncional() + "</td>";
                    }
                    if (cGroupBy.equals("cFuncion_5") || (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))) {
                        tr = tr + "<td align=\"center\">" + epSaldo.getFuncion() + "</td>";
                    }
                    if (cGroupBy.equals("cSubFuncion_6") || (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))) {
                        tr = tr + "<td align=\"center\">" + epSaldo.getSubFuncion() + "</td>";
                    }
                    if (cGroupBy.equals("cProgramaGeneral_7") || (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))) {
                        tr = tr + "<td align=\"center\">" + epSaldo.getProgramaGeneral() + "</td>";
                    }
                    if (cGroupBy.equals("cActividadInstitucional_8") || (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))) {
                        tr = tr + "<td align=\"center\">" + epSaldo.getActividadInstitucional() + "</td>";
                    }
                    if (cGroupBy.equals("cProgramaPresupuestario_9") || (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))) {
                        tr = tr + "<td align=\"center\">" + epSaldo.getProgramaPresupuestario() + "</td>";
                    }
                    if (cGroupBy.equals("cPartida_10") || (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))) {
                        tr = tr + "<td align=\"center\">" + epSaldo.getPartida() + "</td>";
                    }
                    if (cGroupBy.equals("cTipoGasto_11") || (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))) {
                        tr = tr + "<td align=\"center\">" + epSaldo.getTipoGasto() + "</td>";
                    }
                    if (cGroupBy.equals("cFuenteFinanciamiento_12") || (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))) {
                        tr = tr + "<td align=\"center\">" + epSaldo.getFuenteFinanciamiento() + "</td>";
                    }
                    if (cGroupBy.equals("cEntidadFederativa_13") || (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))) {
                        tr = tr + "<td align=\"center\">" + epSaldo.getEntidadFederativa() + "</td>";
                    }
                    if (cGroupBy.equals("cCartera_14") || (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))) {
                        tr = tr + "<td align=\"center\">" + epSaldo.getCartera() + "</td>";
                    }
                    if (cGroupBy.equals("cUnidadResponsable_15") || (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))) {
                        tr = tr + "<td align=\"center\">" + epSaldo.getUnidadEjecutora() + "</td>";
                    }
                    if (cGroupBy.equals("cUnidadResponsable_16") || (cGroupBy.equals("cSubCuenta") && Componentes.equals("INCLUIR"))) {
                        tr = tr + "<td align=\"center\">" + epSaldo.getUnidadNorativa() + "</td>";
                    }
                    for (int contador = 0; contador < nCtasPresup.length; contador++) {
                        opcion = dCtasPresup[contador].trim().replace(" ", "_");
                        if ((TipoReporte.equals("GENERAL") && (Cuenta == "" || Cuenta.contains(nCtasPresup[contador]))) || (!TipoReporte.equals("GENERAL") && epSaldo.getDcuenta().equals(dCtasPresup[contador]))) {
                            if (opcion.equals("ORIGINAL")) {
                                tr = tr + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoOriginal()) + "</td>";
                            }
                            if (opcion.equals("MODIFICADO")) {
                                tr = tr + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoModificado()) + "</td>";
                            }
                            if (opcion.equals("AMPLIACION_AUTORIZADA")) {
                                tr = tr + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoAmpliacionAutorizada()) + "</td>";
                            }
                            if (opcion.equals("REDUCCION_AUTORIZADA")) {
                                tr = tr + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoReduccionAutorizada()) + "</td>";
                            }
                            if (opcion.equals("AMPLIACION_EN_TRAMITE")) {
                                tr = tr + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoAmpliacionTramite()) + "</td>";
                            }
                            if (opcion.equals("APARTADO")) {
                                tr = tr + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoApartado()) + "</td>";
                            }
                            if (opcion.equals("PRECOMPROMETIDO")) {
                                tr = tr + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoPrecomprometido()) + "</td>";
                            }
                            if (opcion.equals("COMPROMETIDO")) {
                                tr = tr + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoComprometido()) + "</td>";
                            }
                            if (opcion.equals("DEVENGADO")) {
                                tr = tr + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoDevengado()) + "</td>";
                            }
                            if (opcion.equals("EJERCIDO_NO_PAGADO")) {
                                tr = tr + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoEjercidoNoPagado()) + "</td>";
                            }
                            if (opcion.equals("DISPONIBLE_NETO")) {
                                tr = tr + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoDisponibleNeto()) + "</td>";
                            }
                            if (opcion.equals("DISPONIBLE_NETO_RADICADO")) {
                                tr = tr + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoDisponibleNetoRadicado()) + "</td>";
                            }
                            if (opcion.equals("DISPONIBLE_BRUTO")) {
                                tr = tr + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoDisponibleBruto()) + "</td>";
                            }
                            if (opcion.equals("EJERCIDO_PAGADO")) {
                                tr = tr + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoEjercidoPagado()) + "</td>";
                            }
                            if (opcion.equals("REDUCCION_EN_TRAMITE")) {
                                tr = tr + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoReduccionTramite()) + "</td>";
                            }
                            if (opcion.equals("REINTEGRO_EN_TRAMITE")) {
                                tr = tr + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoReintegroTramite()) + "</td>";
                            }
                            if (opcion.equals("RECTIFICACION")) {
                                tr = tr + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoRectificacion()) + "</td>";
                            }
                            if (opcion.equals("REDUCCION_SHCP_EN_TRAMITE")) {
                                tr = tr + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoReduccionSHCPTramite()) + "</td>";
                            }
                            if (opcion.equals("REDUCCION_SHCP_APLICADA")) {
                                tr = tr + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoReduccionSHCPAplicada()) + "</td>";
                            }
                            if (opcion.equals("PRECOMPROMISO_MATERIALES")) {
                                tr = tr + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoPrecompromisoMateriales()) + "</td>";
                            }
                        }
                    }
                    // log.debug("tr " + tr);
                    if (!TipoReporte.equals("GENERAL")) {
                        tr = tr + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoEnero()) + "</td>" + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoFebrero()) + "</td>" + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoMarzo()) + "</td>" + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoAbril()) + "</td>" + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoMayo()) + "</td>" + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoJunio()) + "</td>" + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoJulio()) + "</td>" + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoAgosto()) + "</td>" + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoSeptiembre()) + "</td>" + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoOctubre()) + "</td>" + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoNoviembre()) + "</td>" + "<td align=\"right\">" + ReporteBussinesLogic.formateaNumero(epSaldo.getMontoDiciembre()) + "</td>";
                    }
                    ;
                    tr = tr + "</tr>";
                    sb.append(tr);
                }
                int VecesQueSeRepite = new StringTokenizer(tr, "</td>").countTokens() - 1;
                Filtro = Filtro.replaceAll("colspan=\"4\"", "colspan=\"" + VecesQueSeRepite + "\"");
                if (Resumen.equals("INCLUIR")) {
                    sb.append(Filtro);
                }
                long tFinRender = System.currentTimeMillis();
                log.info("Ejecutando multireporte: Fin de pintado de resultado: Fin[ " + (new Date()).toString() + "]");
                log.info("Ejecutando multireporte: Se pintaron los resultados en :[ " + (tFinRender - tInicioRender) + "ms] equivalente a [" + (tFinRender - tInicioRender) / 1000 + "s.]");
                log.debug("Termina de cargar " + (row--) + " registros para el reporte");
            }
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                // log.warn("Error en rollback", ex);
            }
            log.error("Error en MultiReporte ", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return sb.toString();
    }

    public String MultiReporteContableResultado(String ep, String EjercicioFiscal, String Cuenta, String UnidadEjecutora, String RamoEP, String UnidadResponsableEP, String GrupoFuncional, String Funcion, String SubFuncion, String ProgramaGeneral, String ProgramaPresupuestario, String ActividadInstitucional, String Partida, String TipoGasto, String FuenteFinanciamiento, String EntidadFederativa, String Cartera, String UnidadNormativa, String ClaveCNA, String cOrddeBy, String cGroupBy, String InfoRegMes, String TipoReporte, String Usuario, String Filtro, String[] nCtasPresup, String cCentroContable, String Componentes, String Resumen) {
        Connection conn = null;
        StringBuffer sb = new StringBuffer();
        //String opcion = "";
        ArrayList<String> fila = null;
        ArrayList<ArrayList<String>> saldoList = null;
        try {
            conn = getConnection();
            String sql = "";
            // sql+=(Cuenta !=""&&Cuenta
            // !=null?" AND s.nCuentaP = '"+Cuenta+"'":"");
            // sql+=(Cuenta !=""&&Cuenta
            // !=null?" AND s.nCuenta like '"+Cuenta+"%'":"");
            sql += (ep != "" && ep != null ? " AND cSubCuenta = '" + ep + "'" : "");
            sql += (EjercicioFiscal != "" && EjercicioFiscal != null ? " AND aEjercicioFiscal_1 = '" + EjercicioFiscal + "'" : "");
            sql += (RamoEP != "" && RamoEP != null ? " AND cRamo_2='" + RamoEP + "'" : "");
            sql += (UnidadResponsableEP != "" && UnidadResponsableEP != null ? " AND cUnidadResponsable_3='" + UnidadResponsableEP + "'" : "");
            sql += (GrupoFuncional != "" && GrupoFuncional != null ? " AND cGrupoFuncional_4='" + GrupoFuncional + "'" : "");
            sql += (Funcion != "" && Funcion != null ? " AND cFuncion_5='" + Funcion + "'" : "");
            sql += (SubFuncion != "" && SubFuncion != null ? " AND cSubFuncion_6='" + SubFuncion + "'" : "");
            sql += (ProgramaGeneral != "" && ProgramaGeneral != null ? " AND cProgramaGeneral_7='" + ProgramaGeneral + "'" : "");
            sql += (ProgramaPresupuestario != "" && ProgramaPresupuestario != null ? " AND cProgramaPresupuestario_9='" + ProgramaPresupuestario + "'" : "");
            sql += (ActividadInstitucional != "" && ActividadInstitucional != null ? " AND cActividadInstitucional_8='" + ActividadInstitucional + "'" : "");
            sql += (Partida != "" && Partida != null ? " AND cPartida_10='" + Partida + "'" : "");
            sql += (TipoGasto != "" && TipoGasto != null ? " AND cTipoGasto_11='" + TipoGasto + "'" : "");
            sql += (FuenteFinanciamiento != "" && FuenteFinanciamiento != null ? " AND cFuenteFinanciamiento_12='" + FuenteFinanciamiento + "'" : "");
            sql += (EntidadFederativa != "" && EntidadFederativa != null ? " AND cEntidadFederativa_13='" + EntidadFederativa + "'" : "");
            sql += (Cartera != "" && Cartera != null ? " AND cCartera_14='" + Cartera + "'" : "");
            sql += (UnidadNormativa != "" && UnidadNormativa != null ? " AND cUnidadResponsable_16='" + UnidadNormativa + "'" : "");
            sql += (UnidadEjecutora != "" && UnidadEjecutora != null ? " AND cUnidadResponsable_15='" + UnidadEjecutora + "'" : "");
            sql += (ClaveCNA != "" && ClaveCNA != null ? " AND nClaveCNA='" + ClaveCNA + "'" : "");
            log.debug("EXECUTE sp_pMultiReporteContable_syc '" + sql.replace("'", "''") + "', '" + Cuenta.replace("'", "''") + "', '" + cOrddeBy.replace("'", "''") + "', '" + cGroupBy.replace("'", "''") + "', '" + InfoRegMes.replace("'", "''") + "', '" + TipoReporte.replace("'", "''") + "', '" + Usuario.replace("'", "''") + "', '" + cCentroContable.replace("'", "''") + "', " + "@Salida OUTPUT; ");
            saldoList = ReporteManager.callMultiReporteContable(conn, sql, Cuenta, cOrddeBy, cGroupBy, InfoRegMes, TipoReporte, Usuario, nCtasPresup, cCentroContable, Componentes);
            log.debug("salida de sp_pMultiReporteContable_syc ");
            String tr = "";
            int row = 0;
            if (saldoList.isEmpty()) {
                tr = "<tr class=\"AlternateRow\">" + "	<td colspan=\"4\"><i>No hay informaci&oacute;n para mostrar</i></td>" + "</tr>";
                sb.append(tr);
                log.debug("No hay informaci&oacute;n para mostrar");
            } else {
                for (int i = 0; i < saldoList.size(); i++) {
                    ArrayList<String> lista = saldoList.get(i);
                    tr = "<tr class=" + ((i % 2) == 0 ? "\"AlternateRow\"" : "\"NormalRow\"") + ">";
                    tr = tr + "<td align=\"center\">" + (i + 1) + "</td>";
                    for (int j = 0; j < lista.size(); j++) {
                        // for (Iterator<?> iter = saldoList.iterator();
                        // iter.hasNext(); row++) {
                        // Saldo epSaldo = (Saldo) iter.next();
                        tr = tr + "<td align=\"center\">" + lista.get(j) + "</td>";
                        // log.debug("tr " + tr);
                    }
                    // }
                    tr = tr + "</tr>";
                    sb.append(tr);
                    // log.debug(tr);
                }
            }
            int VecesQueSeRepite = new StringTokenizer(tr, "</td>").countTokens() - 1;
            Filtro = Filtro.replaceAll("colspan=\"4\"", "colspan=\"" + VecesQueSeRepite + "\"");
            if (Resumen.compareTo("INCLUIR") == 0) {
                sb.append(Filtro);
                log.debug("Adicionando Filtro");
            }
            log.debug("Termina de cargar " + (row--) + " registros para el reporte");
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                // log.warn("Error en rollback", ex);
            }
            log.error("Error en MultiReporteContable ", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return sb.toString();
    }

    public String ContraCuentasResultado(String PolCtroContable, String PolEjercicioFiscal, String PolStatus, String PolFechCapturaIni, String PolFechCapturaFin, String PolTipo, String PolFechAplicacionIni, String PolFechAplicacionFin, String PolAutorizo, String PolNumeroIni, String PolNumeroFin, String PolMontoIni, String PolMontoFin, String PolOrigen, String PolAutomatica) {
        Connection conn = null;
        StringBuffer sb = new StringBuffer();
        List<?> polizaList = new ArrayList<Object>();
        try {
            conn = getConnection();
            // String sql = "";
            String sql = "select m.aEjercicioFiscal, " + "m.cCentroContable," + "m.cTipoPoliza, " + "m.nFolioPoliza,  " + "ROW_NUMBER() OVER(ORDER BY nConsecutivoMovimiento ASC) nDocRenglon, " + "p.nMes,  " + "m.nCuenta, " + "m.cTipoMovimiento, " + "m.mMovimiento, " + "p.cDescripcionPoliza " + "from tMovimiento m " + "inner join (" + "select m.cCentroContable, " + "m.cTipoPoliza, m.nFolioPoliza, " + "m.nDocRenglon  , " + "m.nCuenta, " + "m.mMovimiento, " + "p.cDescripcionPoliza, " + "p.nMes " + "from tMovimiento m " + "inner join tPoliza p on p.cCentroContable=m.cCentroContable and p.cTipoPoliza=m.cTipoPoliza and p.nFolioPoliza=m.nFolioPoliza " + "where m.nCuenta like  ''11301%''') p on p.cCentroContable=m.cCentroContable and p.cTipoPoliza=m.cTipoPoliza and p.nFolioPoliza=m.nFolioPoliza " + "where m.cCentroContable=''10'' and m.cTipoPoliza='''EG'' and m.nFolioPoliza=''12''";
            sql += (PolCtroContable != "" && PolCtroContable != null ? " AND p.cCentroContable = '" + PolCtroContable + "'" : "");
            sql += (PolEjercicioFiscal != "" && PolEjercicioFiscal != null ? " AND p.aEjercicioFiscal = '" + PolEjercicioFiscal + "'" : "");
            sql += (PolStatus != "" && PolStatus != null ? " AND p.DocHAplicado = '" + PolStatus + "'" : "");
            sql += (PolOrigen != "" && PolOrigen != null ? " AND cTipoDocumento = '" + PolOrigen + "'" : "");
            sql += (PolFechCapturaIni != "" && PolFechCapturaIni != null ? " AND convert(date,p.fCreacion, 103) >= convert(date,'" + PolFechCapturaIni + "', 103)" : "");
            sql += (PolFechCapturaFin != "" && PolFechCapturaFin != null ? " AND convert(date,p.fCreacion, 103) <= convert(date,'" + PolFechCapturaFin + "', 103)" : "");
            sql += (PolTipo != "" && PolTipo != null ? " AND p.cTipoPoliza = '" + PolTipo + "'" : "");
            sql += (PolFechAplicacionIni != "" && PolFechAplicacionIni != null ? " AND convert(date,p.fAplicacion, 103) >= convert(date,'" + PolFechAplicacionIni + "', 103)" : "");
            sql += (PolFechAplicacionFin != "" && PolFechAplicacionFin != null ? " AND convert(date,p.fAplicacion, 103) <= convert(date,'" + PolFechAplicacionFin + "', 103)" : "");
            sql += (PolAutomatica != "" && PolAutomatica != null ? " AND nPolizaAutomatica = '" + PolAutomatica + "'" : "");
            sql += (PolAutorizo != "" && PolAutorizo != null ? " AND cUsuarioAutorizo = '" + PolAutorizo + "'" : "");
            sql += (PolAutorizo != "" && PolAutorizo != null ? " AND DocHAplicado = '" + PolAutorizo + "'" : "");
            sql += (PolNumeroIni != "" && PolNumeroIni != null ? " AND p.nFolioPoliza >= '" + PolNumeroIni + "'" : "");
            sql += (PolNumeroFin != "" && PolNumeroFin != null ? " AND p.nFolioPoliza <= '" + PolNumeroFin + "'" : "");
            sql += (PolMontoIni != "" && PolMontoIni != null ? " AND p.mTotalCargo >= '" + PolMontoIni + "'" : "");
            sql += (PolMontoFin != "" && PolMontoFin != null ? " AND p.mTotalCargo <= '" + PolMontoFin + "'" : "");
            sql += " order by m.cCentroContable, m.cTipoPoliza, m.nFolioPoliza, m.nDocRenglon  ,m.nCuenta, m.mMovimiento ";
            log.debug(sql);
            polizaList = ReporteManager.selectConsultaPolizas(conn, sql);
            log.debug("salida de selectConsultaPolizas ");
            String tr = "";
            int row = 0;
            if (polizaList.isEmpty()) {
                tr = "<tr class=\"alternateRow\">" + "	<td colspan=\"4\"><i>No hay informaci&oacute;n para mostrar</i></td>" + "</tr>";
                sb.append(tr);
                log.debug("No hay informaci&oacute;n para mostrar");
            } else {
                for (Iterator<?> iter = polizaList.iterator(); iter.hasNext(); row++) {
                    Poliza cPoliza = (Poliza) iter.next();
                    tr = "<tr class=" + ((row % 2) == 0 ? "AlternateRow" : "NormalRow") + ">";
                    tr += "<td align=\"center\"><li id=\"tipotrabajo\" name=\"tipotrabajo\"><input id=\"selection\" name=\"selection\" value=\"" + "(v.nFolioPoliza=" + cPoliza.getNfolioPoliza() + " and v.cCentroContable='" + cPoliza.getCcentroContable() + "' and v.cTipoPoliza='" + cPoliza.getCtipoPoliza() + "')" + "\" align=\"center\" type=\"checkbox\"></li></td>";
                    tr += "<td align=\"center\">" + cPoliza.getCdescripcionCentroContable() + "</td>";
                    tr += "<td align=\"center\">" + cPoliza.getCdescripcionTipoPoliza() + "</td>";
                    tr += "<td align=\"center\">" + cPoliza.getNfolioPoliza() + "</td>";
                    tr += "<td align=\"center\">" + cPoliza.getFcreacion() + "</td>";
                    tr += "<td align=\"center\">" + cPoliza.getFaplicacion() + "</td>";
                    tr += "<td align=\"center\">" + (cPoliza.getDocHAplicado().equals("C") ? "CANCELADA" : "APLICADA") + "</td>";
                    tr += "<td align=\"center\">" + cPoliza.getCtipoDocumento() + "</td>";
                    tr += "<td align=\"center\">" + (cPoliza.getNpolizaAutomatica().equals("1") ? "AUTOMATICA" : "CONTABILIDAD") + "</td>";
                    tr += "<td align=\"center\">" + (cPoliza.getCdescripcionPoliza().equals("") ? "SIN DESCRIPCION" : cPoliza.getCdescripcionPoliza()) + "</td>";
                    tr += "<td align=\"center\">" + cPoliza.getMtotalAbono() + "</td>";
                    tr += "<td align=\"center\">" + (cPoliza.getCusuarioAutorizo().equals("") ? "SIN USUARIO" : cPoliza.getCusuarioAutorizo()) + "</td>";
                    tr += "</tr>";
                    sb.append(tr);
                }
                log.debug("Termina de cargar " + (row--) + " registros para el reporte");
            }
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                // log.warn("Error en rollback", ex);
            }
            log.error("Error en MultiReporte ", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return sb.toString();
    }

    public String ConsultaPolizasResultado(String PolCtroContable, String PolEjercicioFiscal, String PolStatus, String PolFechCapturaIni, String PolFechCapturaFin, String PolTipo, String PolFechAplicacionIni, String PolFechAplicacionFin, String PolAutorizo, String PolNumeroIni, String PolNumeroFin, String PolMontoIni, String PolMontoFin, String PolOrigen, String PolAutomatica) {
        Connection conn = null;
        StringBuffer sb = new StringBuffer();
        List<?> polizaList = new ArrayList<Object>();
        try {
            conn = getConnection();
            // String sql = "";
            String sql = "SELECT p.nFolioPoliza ,CONVERT(varchar(10), p.fCreacion, 103) fCreacion , isnull(p.cDescripcionPoliza,'') cDescripcionPoliza ,CONVERT(varchar(50), CONVERT(money,p.mTotalCargo), 1) as mTotalCargo ,CONVERT(varchar(50),CONVERT(money,p.mTotalAbono), 1) as mTotalAbono ,p.nMes ,p.nCuenta ,CONVERT(varchar(10), p.fAplicacion, 103) fAplicacion ," + " p.nPolizaAutomatica ,p.cCentroContable cCentroContable,ccc.cDescripcion cDescripcionCentroContable  ,p.aEjercicioFiscal, p.cTipoPoliza cTipoPoliza ,rtrim(ctp.cDescripcionPoliza) cDescripcionTipoPoliza ,p.cTipoDocumento ,p.nFolioDocumento ," + " p.DocHAplicado, isnull(p.cUsuarioAutorizo,'') cUsuarioAutorizo" + " FROM vPolizaMovimientosContables p  WITH (NOLOCK) " + " INNER JOIN tCatalogoTipoPoliza ctp  WITH (NOLOCK) ON ctp.cTipoPoliza=p.cTipoPoliza " + " INNER JOIN tCatalogoCentroContable ccc  WITH (NOLOCK) on ccc.cCentroContable=p.cCentroContable " + " WHERE 1=1 ";
            sql += (PolCtroContable != "" && PolCtroContable != null ? " AND p.cCentroContable = '" + PolCtroContable + "'" : "");
            sql += (PolEjercicioFiscal != "" && PolEjercicioFiscal != null ? " AND p.aEjercicioFiscal = '" + PolEjercicioFiscal + "'" : "");
            sql += (PolStatus != "" && PolStatus != null ? " AND p.DocHAplicado = '" + PolStatus + "'" : "");
            sql += (PolOrigen != "" && PolOrigen != null ? " AND cTipoDocumento = '" + PolOrigen + "'" : "");
            sql += (PolFechCapturaIni != "" && PolFechCapturaIni != null ? " AND convert(date,p.fCreacion, 103) >= convert(date,'" + PolFechCapturaIni + "', 103)" : "");
            sql += (PolFechCapturaFin != "" && PolFechCapturaFin != null ? " AND convert(date,p.fCreacion, 103) <= convert(date,'" + PolFechCapturaFin + "', 103)" : "");
            sql += (PolTipo != "" && PolTipo != null ? " AND p.cTipoPoliza = '" + PolTipo + "'" : "");
            sql += (PolFechAplicacionIni != "" && PolFechAplicacionIni != null ? " AND convert(date,p.fAplicacion, 103) >= convert(date,'" + PolFechAplicacionIni + "', 103)" : "");
            sql += (PolFechAplicacionFin != "" && PolFechAplicacionFin != null ? " AND convert(date,p.fAplicacion, 103) <= convert(date,'" + PolFechAplicacionFin + "', 103)" : "");
            sql += (PolAutomatica != "" && PolAutomatica != null ? " AND nPolizaAutomatica = '" + PolAutomatica + "'" : "");
            sql += (PolAutorizo != "" && PolAutorizo != null ? " AND cUsuarioAutorizo = '" + PolAutorizo + "'" : "");
            sql += (PolAutorizo != "" && PolAutorizo != null ? " AND DocHAplicado = '" + PolAutorizo + "'" : "");
            sql += (PolNumeroIni != "" && PolNumeroIni != null ? " AND p.nFolioPoliza >= '" + PolNumeroIni + "'" : "");
            sql += (PolNumeroFin != "" && PolNumeroFin != null ? " AND p.nFolioPoliza <= '" + PolNumeroFin + "'" : "");
            sql += (PolMontoIni != "" && PolMontoIni != null ? " AND p.mTotalCargo >= '" + PolMontoIni + "'" : "");
            sql += (PolMontoFin != "" && PolMontoFin != null ? " AND p.mTotalCargo <= '" + PolMontoFin + "'" : "");
            log.debug(sql);
            polizaList = ReporteManager.selectConsultaPolizas(conn, sql);
            log.debug("salida de selectConsultaPolizas ");
            String tr = "";
            int row = 0;
            if (polizaList.isEmpty()) {
                tr = "<tr class=\"alternateRow\">" + "	<td colspan=\"8\"><i>No hay informaci&oacute;n para mostrar</i></td>" + "</tr>";
                sb.append(tr);
                log.debug("No hay informaci&oacute;n para mostrar");
            } else {
                for (Iterator<?> iter = polizaList.iterator(); iter.hasNext(); row++) {
                    Poliza cPoliza = (Poliza) iter.next();
                    tr = "<tr class=" + ((row % 2) == 0 ? "AlternateRow" : "NormalRow") + ">";
                    tr += "<td align=\"center\"><li id=\"tipotrabajo\" name=\"tipotrabajo\"><input id=\"selection\" name=\"selection\" value=\"" + // + "(v.nFolioPoliza="
                    // + cPoliza.getNfolioPoliza()
                    // + " and v.cCentroContable=''"
                    // + cPoliza.getCcentroContable()
                    // + "'' and v.cTipoPoliza=''"
                    // + cPoliza.getCtipoPoliza()
                    // + "'')"
                    "(" + cPoliza.getNfolioPoliza() + ",''" + cPoliza.getCcentroContable() + "'',''" + cPoliza.getCtipoPoliza() + "'')" + "\" align=\"center\" type=\"checkbox\"></li></td>";
                    tr += "<td align=\"center\">" + cPoliza.getCdescripcionCentroContable() + "</td>";
                    tr += "<td align=\"center\">" + cPoliza.getCdescripcionTipoPoliza() + "</td>";
                    tr += "<td align=\"center\">" + cPoliza.getNfolioPoliza() + "</td>";
                    tr += "<td align=\"center\">" + cPoliza.getFcreacion() + "</td>";
                    tr += "<td align=\"center\">" + cPoliza.getFaplicacion() + "</td>";
                    tr += "<td align=\"center\">" + (cPoliza.getDocHAplicado().equals("C") ? "CANCELADA" : "APLICADA") + "</td>";
                    tr += "<td align=\"center\">" + cPoliza.getCtipoDocumento() + "</td>";
                    tr += "<td align=\"center\">" + (cPoliza.getNpolizaAutomatica().equals("1") ? "AUTOMATICA" : "CONTABILIDAD") + "</td>";
                    tr += "<td align=\"center\">" + (cPoliza.getCdescripcionPoliza().equals("") ? "SIN DESCRIPCION" : cPoliza.getCdescripcionPoliza()) + "</td>";
                    tr += "<td align=\"center\">" + cPoliza.getMtotalAbono() + "</td>";
                    tr += "<td align=\"center\">" + (cPoliza.getCusuarioAutorizo().equals("") ? "SIN USUARIO" : cPoliza.getCusuarioAutorizo()) + "</td>";
                    tr += "</tr>";
                    sb.append(tr);
                }
                log.debug("Termina de cargar " + (row--) + " registros para el reporte");
            }
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                // log.warn("Error en rollback", ex);
            }
            log.error("Error en MultiReporte ", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return sb.toString();
    }

    public String ConsultaPolizasResultadoJSON(String PolCtroContable, String PolEjercicioFiscal, String PolStatus, String PolFechCapturaIni, String PolFechCapturaFin, String PolTipo, String PolFechAplicacionIni, String PolFechAplicacionFin, String PolAutorizo, String PolNumeroIni, String PolNumeroFin, String PolMontoIni, String PolMontoFin, String PolOrigen, String PolAutomatica) {
        Connection conn = null;
        StringBuffer sb = new StringBuffer();
        List<?> polizaList = new ArrayList<Object>();
        try {
            conn = getConnection();
            String sql;
            if (PolAutomatica.equals("")) {
                sql = "SELECT p.nFolioPoliza ,CONVERT(varchar(10), p.fCreacion, 103) fCreacion , isnull(p.cDescripcionPoliza,'') cDescripcionPoliza " + ",CONVERT(varchar(50), CONVERT(money,p.mTotalCargo), 1) as mTotalCargo ,CONVERT(varchar(50),CONVERT(money,p.mTotalAbono), 1) as mTotalAbono , " + "p.nMes ,p.nCuenta ,CONVERT(varchar(10), p.fAplicacion, 103) fAplicacion , p.nPolizaAutomatica ,p.cCentroContable cCentroContable, " + "ccc.cDescripcion cDescripcionCentroContable  ,p.aEjercicioFiscal, p.cTipoPoliza cTipoPoliza  " + ",rtrim(ctp.cDescripcionPoliza) cDescripcionTipoPoliza ,p.cTipoDocumento ,p.nFolioDocumento ,ISNULL( pd.cDocHAplicado,'A')as cDocHAplicado, " + "isnull(p.cUsuarioAutorizo,'') cUsuarioAutorizo,P.ID_OPER  " + "FROM vConsultaPoliza p  WITH (NOLOCK) " + "LEFT JOIN tCatalogoTipoPoliza ctp  WITH (NOLOCK) " + "ON ctp.cTipoPoliza=p.cTipoPoliza  " + "LEFT JOIN tCatalogoCentroContable ccc  WITH (NOLOCK) " + "on ccc.cCentroContable=p.cCentroContable " + "LEFT join " + "(select tpd.nFolioPoliza,tpd.nFolioPagoDirecto,tpd.cIdEntidadContable,tpd.cDocumentoHaplicado as cDocHAplicado, 'PAGODIRECTO' tipoDoc, tpd.cTipoPoliza tipoPoliza from tPagoDirectoEncabezado as tpd  WITH (NOLOCK)  " + "union  " + "select tapd.nFolioPoliza,tapd.nFolioPAGODIVERSO,tapd.cCentroContable,tapd.cDocumentoHaplicado as cDocHAplicado, 'PAGODIVERSO' tipoDoc, tapd.cTipoPoliza tipoPoliza from tPagoDiversoEncabezado as tapd  WITH (NOLOCK)  " + "union  " + "select pf.nFolioPoliza ,pf.nFolioPAGODIVERSO,pf.cCentroContable,pf.cDocumentoHaplicado as cDocHAplicado, 'PAGOFEDERALIZADO' tipoDoc, pf.cTipoPoliza  tipoPoliza from tPAGODIVERSOEncabezado as pf  WITH (NOLOCK)  " + "union  " + "select po.nFolioPoliza ,po.nFolioPAGOOBRA,po.cCentroContable,po.cDocumentoHaplicado as cDocHAplicado, 'PAGOOBRA' tipoDoc, po.cTipoPoliza tipoPoliza from tPAGOOBRAEncabezado as po  WITH (NOLOCK) " + "union  " + "select rg.nFolioPoliza, rg.nFolioRELACIONGASTOS,rg.cCentroContable,rg.cDocumentoHaplicado as cDocHAplicado, 'RELACIONGASTOS' tipoDoc, rg.cTipoPoliza tipoPoliza from tRELACIONGASTOSEncabezado as rg  WITH (NOLOCK) " + "union  " + "select distinct ae.nFolioPoliza ,ae.nFolioAdecuacionaut,ad.cCentroContable,ae.cDocumentoHaplicado as cDocHAplicado, 'ADECUACIONAUT' tipoDoc,ae.cTipoPoliza tipoPoliza from tADECUACIONAUTEncabezado ae  WITH (NOLOCK),tAdecuacionAutDetalle ad  WITH (NOLOCK) where ae.nFolioAdecuacionaut=ad.nFolioAdecuacionaut  " + "union  " + "select distinct ae.nFolioPoliza ,ae.nFolioAdecuacion,ad.cCentroContable,ae.cDocumentoHaplicado as cDocHAplicado, 'ADECUACION' tipoDoc,ae.cTipoPoliza tipoPoliza from tADECUACIONEncabezado ae  WITH (NOLOCK),tAdecuacionDetalle ad  WITH (NOLOCK) where ae.nFolioAdecuacion=ad.nFolioAdecuacion  " + "union  " + "select distinct ae.nFolioPoliza ,ae.nFolioApartado,ad.cCentroContable,ae.cDocumentoHaplicado as cDocHAplicado, 'APARTADO' tipoDoc,ae.cTipoPoliza tipoPoliza from tApartadoEncabezado ae  WITH (NOLOCK),tApartadoDetalle ad  WITH (NOLOCK) where ae.nFolioApartado=ad.nFolioApartado  " + "union  " + "select distinct ae.nFolioPoliza ,ae.nFolioCancelaCompromiso,ad.cCentroContable,ae.cDocumentoHaplicado as cDocHAplicado, 'COMPROMISO' tipoDoc,ae.cTipoPoliza tipoPoliza from tCancelaCompromisoEncabezado ae  WITH (NOLOCK),tCancelaCompromisoDetalle ad  WITH (NOLOCK) where ae.nFolioCancelaCompromiso=ad.nFolioCancelaCompromiso " + "union  " + "select distinct ae.nFolioPoliza ,ae.nFolioReintegro,ad.cCentroContable,ae.cDocumentoHaplicado as cDocHAplicado, 'REINTEGRO' tipoDoc,ae.cTipoPoliza tipoPoliza from tReintegroEncabezado ae  WITH (NOLOCK),tReintegroDetalle ad  WITH (NOLOCK) where ae.nFolioReintegro=ad.nFolioReintegro " + "union  " + "select distinct ae.nFolioPoliza ,ae.nFolioReintegroaut,ad.cCentroContable,ae.cDocumentoHaplicado as cDocHAplicado, 'REINTEGROAUT' tipoDoc,ae.cTipoPoliza tipoPoliza from tReintegroAutEncabezado ae  WITH (NOLOCK),tReintegroAutDetalle ad  WITH (NOLOCK) where ae.nFolioReintegroaut=ad.nFolioReintegroaut " + "union  " + "select nFolioPoliza ,nFolioNomina,cCentroContable,cDocumentoHaplicado as cDocHAplicado,'NOMINA' tipoDoc,cTipoPoliza tipoPoliza from tNOMINAEncabezado  WITH (NOLOCK)  " + "union  " + "select ch.nFolioPoliza ,ch.nFolioCheque,ch.cCentroContable,ch.cDocumentoHaplicado as cDocHAplicado, 'CHEQUE' tipoDoc,ch.cTipoPoliza from tChequeEncabezado as ch  WITH (NOLOCK)  " + "union  " + "select nFolioPoliza ,nFolioCompromisoNomina,cCentroContable,cDocumentoHaplicado as cDocHAplicado,'COMPROMISO' tipoDoc,cTipoPoliza tipoPoliza from tCompromisoNominaEncabezado  WITH (NOLOCK)  " + "union   " + "select nFolioPoliza ,nFolioCompromiso,cCentroContable,cDocumentoHaplicado as cDocHAplicado,'COMPROMISO' tipoDoc, cTipoPoliza tipoPoliza from tCompromisoEncabezado  WITH (NOLOCK)  " + "union  " + "SELECT distinct  pde.nFolioPoliza,pdd.nFolioPasivoDiferido,pdd.ccentrocontable,pde.cDocumentoHaplicado as cDocHAplicado, 'PASIVODIFERIDO' tipoDoc, pde.cTipoPoliza tipoPoliza FROM tPasivoDiferidoEncabezado pde WITH (NOLOCK),tPasivoDiferidoDetalle pdd  WITH (NOLOCK) where pde.nFolioPasivoDiferido=pdd.nFolioPasivoDiferido " + "union  " + "SELECT distinct  ee.nFolioPoliza,ed.nFolioEjercido,ed.ccentrocontable,ee.cDocumentoHaplicado as cDocHAplicado, 'EJERCIDO' tipoDoc, ee.cTipoPoliza tipoPoliza FROM tEjercidoEncabezado ee  WITH (NOLOCK),tEjercidoDetalle ed  WITH (NOLOCK) where ee.cTipoPago=ed.cTipoPago	and  ee.nFolioPAGO=ed.nFolioPAGO and ee.nFolioEjercido=ed.nFolioEjercido " + "union  " + "select distinct pe.nFolioPoliza,pd.nFolioPagado,pd.cCentroContable,pe.cDocumentoHaplicado as cDocHAplicado, 'PAGADO' tipoDoc, pe.cTipoPoliza tipoPoliza from tPagadoEncabezado pe  WITH (NOLOCK) ,tPagadoDetalle pd  WITH (NOLOCK)	where pe.cTipoPago=pd.cTipoPago	and pe.nFolioPAGO=pd.nFolioPAGO	and pe.nFolioPagado=pd.nFolioPagado " + " )    " + "as pd " + "on p.nFolioPoliza=pd.nFolioPoliza  " + "and p.cCentroContable=pd.cIdEntidadContable " + "and p.cTipoDocumento=pd.tipoDoc " + "and p.cTipoPoliza=pd.tipoPoliza" + " WHERE 1=1 ";
                sql += (!"".equals(PolCtroContable) && PolCtroContable != null ? " AND p.cCentroContable = '" + PolCtroContable + "'" : "");
                sql += (!"".equals(PolEjercicioFiscal) && PolEjercicioFiscal != null ? " AND p.aEjercicioFiscal = '" + PolEjercicioFiscal + "'" : "");
                sql += (!"".equals(PolOrigen) && PolOrigen != null ? " AND cTipoDocumento = '" + PolOrigen + "'" : "");
                sql += (!"".equals(PolTipo) && PolTipo != null ? " AND p.cTipoPoliza = '" + PolTipo + "'" : "");
                sql += (!"".equals(PolFechCapturaIni) && PolFechCapturaIni != null ? " AND convert(date,p.fCreacion, 103) >= convert(date,'" + PolFechCapturaIni + "', 103)" : "");
                sql += (!"".equals(PolFechCapturaFin) && PolFechCapturaFin != null ? " AND convert(date,p.fCreacion, 103) <= convert(date,'" + PolFechCapturaFin + "', 103)" : "");
                sql += (!"".equals(PolFechAplicacionIni) && PolFechAplicacionIni != null ? " AND convert(date,p.fAplicacion, 103) >= convert(date,'" + PolFechAplicacionIni + "', 103)" : "");
                sql += (!"".equals(PolFechAplicacionFin) && PolFechAplicacionFin != null ? " AND convert(date,p.fAplicacion, 103) <= convert(date,'" + PolFechAplicacionFin + "', 103)" : "");
                sql += (!"".equals(PolAutomatica) && PolAutomatica != null ? " AND nPolizaAutomatica = '" + PolAutomatica + "'" : "");
                sql += (!"".equals(PolAutorizo) && PolAutorizo != null ? " AND cUsuarioAutorizo = '" + PolAutorizo + "'" : "");
                sql += (!"".equals(PolStatus) && PolStatus != null ? " AND cDocHAplicado = '" + PolStatus + "'" : "");
                sql += (!"".equals(PolNumeroIni) && PolNumeroIni != null ? " AND p.nFolioPoliza >= '" + PolNumeroIni + "'" : "");
                sql += (!"".equals(PolNumeroFin) && PolNumeroFin != null ? " AND p.nFolioPoliza <= '" + PolNumeroFin + "'" : "");
                sql += (!"".equals(PolMontoIni) && PolMontoIni != null ? " AND p.mTotalCargo >= '" + PolMontoIni + "'" : "");
                sql += (!"".equals(PolMontoFin) && PolMontoFin != null ? " AND p.mTotalCargo <= '" + PolMontoFin + "'" : "");
                /*
				 * sql += "  union " +
				 * " select *from vConsultaPolizaManual as p " + " WHERE 1=1 ";
				 * 
				 * sql += (!"".equals(PolCtroContable) && PolCtroContable !=
				 * null ? " AND p.cCentroContable = '" + PolCtroContable + "'" :
				 * "");
				 * 
				 * sql += (!"".equals(PolEjercicioFiscal) && PolEjercicioFiscal
				 * != null ? " AND p.aEjercicioFiscal = '" + PolEjercicioFiscal
				 * + "'" : ""); sql += (!"".equals(PolTipo) && PolTipo != null ?
				 * " AND p.cTipoPoliza = '" + PolTipo + "'" : "");
				 * 
				 * sql += (!"".equals(PolOrigen) && PolOrigen != null ?
				 * " AND cTipoDocumento = '" + PolOrigen + "'" : ""); sql +=
				 * (!"".equals(PolFechCapturaIni) && PolFechCapturaIni != null ?
				 * " AND convert(date,p.fCreacion, 103) >= convert(date,'" +
				 * PolFechCapturaIni + "', 103)" : ""); sql +=
				 * (!"".equals(PolFechCapturaFin) && PolFechCapturaFin != null ?
				 * " AND convert(date,p.fCreacion, 103) <= convert(date,'" +
				 * PolFechCapturaFin + "', 103)" : ""); sql +=
				 * (!"".equals(PolFechAplicacionIni) && PolFechAplicacionIni !=
				 * null ?
				 * " AND convert(date,p.fAplicacion, 103) >= convert(date,'" +
				 * PolFechAplicacionIni + "', 103)" : ""); sql +=
				 * (!"".equals(PolFechAplicacionFin) && PolFechAplicacionFin !=
				 * null ?
				 * " AND convert(date,p.fAplicacion, 103) <= convert(date,'" +
				 * PolFechAplicacionFin + "', 103)" : "");
				 * 
				 * sql += (!"".equals(PolAutomatica) && PolAutomatica != null ?
				 * " AND nPolizaAutomatica = '" + PolAutomatica + "'" : "");
				 * 
				 * sql += (!"".equals(PolAutorizo) && PolAutorizo != null ?
				 * " AND cUsuarioAutorizo = '" + PolAutorizo + "'" : "");
				 * 
				 * 
				 * 
				 * if(PolStatus.equals("A")){ /// checar tipo polstatus sql +=
				 * (!"".equals(PolStatus) && PolStatus != null ?
				 * " AND P.ID_OPER=4  and cDocHAplicado!='C' "
				 * 
				 * : ""); }
				 * 
				 * else if(PolStatus.equals("C")){ /// checar tipo polstatus sql
				 * += (!"".equals(PolStatus) && PolStatus != null ?
				 * " AND cDocHAplicado ='" + PolStatus + "'" : ""); } else{ sql
				 * += (!"".equals(PolStatus) && PolStatus != null ?
				 * " AND P.ID_OPER= '" + PolStatus + "'" : ""); } sql +=
				 * (!"".equals(PolNumeroIni) && PolNumeroIni != null ?
				 * " AND p.nFolioPoliza >= '" + PolNumeroIni + "'" : ""); sql +=
				 * (!"".equals(PolNumeroFin) && PolNumeroFin != null ?
				 * " AND p.nFolioPoliza <= '" + PolNumeroFin + "'" : ""); sql +=
				 * (!"".equals(PolMontoIni) && PolMontoIni != null ?
				 * " AND p.mTotalCargo >= '" + PolMontoIni + "'" : ""); sql +=
				 * (!"".equals(PolMontoFin) && PolMontoFin != null ?
				 * " AND p.mTotalCargo <= '" + PolMontoFin + "'" : "");
				 */
                sql += " ORDER BY ctipoPoliza,nFolioPoliza";
            } else // termina para polizas manuales y automaticas
            if (// checa si es poliza automatica
            PolAutomatica.equals("1")) {
                sql = "SELECT p.nFolioPoliza ,CONVERT(varchar(10), p.fCreacion, 103) fCreacion , isnull(p.cDescripcionPoliza,'') cDescripcionPoliza " + ",CONVERT(varchar(50), CONVERT(money,p.mTotalCargo), 1) as mTotalCargo ,CONVERT(varchar(50),CONVERT(money,p.mTotalAbono), 1) as mTotalAbono , " + "p.nMes ,p.nCuenta ,CONVERT(varchar(10), p.fAplicacion, 103) fAplicacion , p.nPolizaAutomatica ,p.cCentroContable cCentroContable, " + "ccc.cDescripcion cDescripcionCentroContable  ,p.aEjercicioFiscal, p.cTipoPoliza cTipoPoliza  " + ",rtrim(ctp.cDescripcionPoliza) cDescripcionTipoPoliza ,p.cTipoDocumento ,p.nFolioDocumento ,ISNULL( pd.cDocHAplicado,'A')as cDocHAplicado, " + "isnull(p.cUsuarioAutorizo,'') cUsuarioAutorizo,P.ID_OPER  " + "FROM vConsultaPoliza p  WITH (NOLOCK) " + "LEFT JOIN tCatalogoTipoPoliza ctp  WITH (NOLOCK) " + "ON ctp.cTipoPoliza=p.cTipoPoliza  " + "LEFT JOIN tCatalogoCentroContable ccc  WITH (NOLOCK) " + "on ccc.cCentroContable=p.cCentroContable " + "LEFT join " + "(select tpd.nFolioPoliza,tpd.nFolioPagoDirecto,tpd.cIdEntidadContable,tpd.cDocumentoHaplicado as cDocHAplicado, 'PAGODIRECTO' tipoDoc, tpd.cTipoPoliza tipoPoliza from tPagoDirectoEncabezado as tpd  WITH (NOLOCK)  " + "union  " + "select tapd.nFolioPoliza,tapd.nFolioPAGODIVERSO,tapd.cCentroContable,tapd.cDocumentoHaplicado as cDocHAplicado, 'PAGODIVERSO' tipoDoc, tapd.cTipoPoliza tipoPoliza from tPagoDiversoEncabezado as tapd  WITH (NOLOCK)  " + "union  " + "select pf.nFolioPoliza ,pf.nFolioPAGODIVERSO,pf.cCentroContable,pf.cDocumentoHaplicado as cDocHAplicado, 'PAGOFEDERALIZADO' tipoDoc, pf.cTipoPoliza  tipoPoliza from tPAGODIVERSOEncabezado as pf  WITH (NOLOCK)  " + "union  " + "select po.nFolioPoliza ,po.nFolioPAGOOBRA,po.cCentroContable,po.cDocumentoHaplicado as cDocHAplicado, 'PAGOOBRA' tipoDoc, po.cTipoPoliza tipoPoliza from tPAGOOBRAEncabezado as po  WITH (NOLOCK) " + "union  " + "select rg.nFolioPoliza, rg.nFolioRELACIONGASTOS,rg.cCentroContable,rg.cDocumentoHaplicado as cDocHAplicado, 'RELACIONGASTOS' tipoDoc, rg.cTipoPoliza tipoPoliza from tRELACIONGASTOSEncabezado as rg  WITH (NOLOCK) " + "union  " + "select distinct ae.nFolioPoliza ,ae.nFolioAdecuacionaut,ad.cCentroContable,ae.cDocumentoHaplicado as cDocHAplicado, 'ADECUACIONAUT' tipoDoc,ae.cTipoPoliza tipoPoliza from tADECUACIONAUTEncabezado ae  WITH (NOLOCK),tAdecuacionAutDetalle ad  WITH (NOLOCK) where ae.nFolioAdecuacionaut=ad.nFolioAdecuacionaut  " + "union  " + "select distinct ae.nFolioPoliza ,ae.nFolioAdecuacion,ad.cCentroContable,ae.cDocumentoHaplicado as cDocHAplicado, 'ADECUACION' tipoDoc,ae.cTipoPoliza tipoPoliza from tADECUACIONEncabezado ae  WITH (NOLOCK),tAdecuacionDetalle ad  WITH (NOLOCK) where ae.nFolioAdecuacion=ad.nFolioAdecuacion  " + "union  " + "select distinct ae.nFolioPoliza ,ae.nFolioApartado,ad.cCentroContable,ae.cDocumentoHaplicado as cDocHAplicado, 'APARTADO' tipoDoc,ae.cTipoPoliza tipoPoliza from tApartadoEncabezado ae  WITH (NOLOCK),tApartadoDetalle ad  WITH (NOLOCK) where ae.nFolioApartado=ad.nFolioApartado  " + "union  " + "select distinct ae.nFolioPoliza ,ae.nFolioCancelaCompromiso,ad.cCentroContable,ae.cDocumentoHaplicado as cDocHAplicado, 'COMPROMISO' tipoDoc,ae.cTipoPoliza tipoPoliza from tCancelaCompromisoEncabezado ae  WITH (NOLOCK),tCancelaCompromisoDetalle ad  WITH (NOLOCK) where ae.nFolioCancelaCompromiso=ad.nFolioCancelaCompromiso " + "union  " + "select distinct ae.nFolioPoliza ,ae.nFolioReintegro,ad.cCentroContable,ae.cDocumentoHaplicado as cDocHAplicado, 'REINTEGRO' tipoDoc,ae.cTipoPoliza tipoPoliza from tReintegroEncabezado ae  WITH (NOLOCK),tReintegroDetalle ad  WITH (NOLOCK) where ae.nFolioReintegro=ad.nFolioReintegro " + "union  " + "select distinct ae.nFolioPoliza ,ae.nFolioReintegroaut,ad.cCentroContable,ae.cDocumentoHaplicado as cDocHAplicado, 'REINTEGROAUT' tipoDoc,ae.cTipoPoliza tipoPoliza from tReintegroAutEncabezado ae  WITH (NOLOCK),tReintegroAutDetalle ad  WITH (NOLOCK) where ae.nFolioReintegroaut=ad.nFolioReintegroaut " + "union  " + "select nFolioPoliza ,nFolioNomina,cCentroContable,cDocumentoHaplicado as cDocHAplicado,'NOMINA' tipoDoc,cTipoPoliza tipoPoliza from tNOMINAEncabezado  WITH (NOLOCK)  " + "union  " + "select ch.nFolioPoliza ,ch.nFolioCheque,ch.cCentroContable,ch.cDocumentoHaplicado as cDocHAplicado, 'CHEQUE' tipoDoc,ch.cTipoPoliza from tChequeEncabezado as ch  WITH (NOLOCK)  " + "union  " + "select nFolioPoliza ,nFolioCompromisoNomina,cCentroContable,cDocumentoHaplicado as cDocHAplicado,'COMPROMISONOMINA' tipoDoc,cTipoPoliza tipoPoliza from tCompromisoNominaEncabezado  WITH (NOLOCK)  " + "union   " + "select nFolioPoliza ,nFolioCompromiso,cCentroContable,cDocumentoHaplicado as cDocHAplicado,'COMPROMISO' tipoDoc, cTipoPoliza tipoPoliza from tCompromisoEncabezado  WITH (NOLOCK)  " + "union  " + "SELECT distinct  pde.nFolioPoliza,pdd.nFolioPasivoDiferido,pdd.ccentrocontable,pde.cDocumentoHaplicado as cDocHAplicado, 'PASIVODIFERIDO' tipoDoc, pde.cTipoPoliza tipoPoliza FROM tPasivoDiferidoEncabezado pde WITH (NOLOCK),tPasivoDiferidoDetalle pdd  WITH (NOLOCK) where pde.nFolioPasivoDiferido=pdd.nFolioPasivoDiferido " + "union  " + "SELECT distinct  ee.nFolioPoliza,ed.nFolioEjercido,ed.ccentrocontable,ee.cDocumentoHaplicado as cDocHAplicado, 'EJERCIDO' tipoDoc, ee.cTipoPoliza tipoPoliza FROM tEjercidoEncabezado ee  WITH (NOLOCK),tEjercidoDetalle ed  WITH (NOLOCK) where ee.cTipoPago=ed.cTipoPago	and  ee.nFolioPAGO=ed.nFolioPAGO and ee.nFolioEjercido=ed.nFolioEjercido " + "union  " + "select distinct pe.nFolioPoliza,pd.nFolioPagado,pd.cCentroContable,pe.cDocumentoHaplicado as cDocHAplicado, 'PAGADO' tipoDoc, pe.cTipoPoliza tipoPoliza from tPagadoEncabezado pe  WITH (NOLOCK) ,tPagadoDetalle pd  WITH (NOLOCK)	where pe.cTipoPago=pd.cTipoPago	and pe.nFolioPAGO=pd.nFolioPAGO	and pe.nFolioPagado=pd.nFolioPagado " + " )    " + "as pd " + "on p.nFolioPoliza=pd.nFolioPoliza  " + "and p.cCentroContable=pd.cIdEntidadContable " + "and p.cTipoDocumento=pd.tipoDoc " + "and p.cTipoPoliza=pd.tipoPoliza" + " WHERE 1=1 ";
                sql += (!"".equals(PolCtroContable) && PolCtroContable != null ? " AND p.cCentroContable = '" + PolCtroContable + "'" : "");
                sql += (!"".equals(PolEjercicioFiscal) && PolEjercicioFiscal != null ? " AND p.aEjercicioFiscal = '" + PolEjercicioFiscal + "'" : "");
                sql += (!"".equals(PolTipo) && PolTipo != null ? " AND p.cTipoPoliza = '" + PolTipo + "'" : "");
                sql += (!"".equals(PolOrigen) && PolOrigen != null ? " AND cTipoDocumento = '" + PolOrigen + "'" : "");
                sql += (!"".equals(PolFechCapturaIni) && PolFechCapturaIni != null ? " AND convert(date,p.fCreacion, 103) >= convert(date,'" + PolFechCapturaIni + "', 103)" : "");
                sql += (!"".equals(PolFechCapturaFin) && PolFechCapturaFin != null ? " AND convert(date,p.fCreacion, 103) <= convert(date,'" + PolFechCapturaFin + "', 103)" : "");
                sql += (!"".equals(PolFechAplicacionIni) && PolFechAplicacionIni != null ? " AND convert(date,p.fAplicacion, 103) >= convert(date,'" + PolFechAplicacionIni + "', 103)" : "");
                sql += (!"".equals(PolFechAplicacionFin) && PolFechAplicacionFin != null ? " AND convert(date,p.fAplicacion, 103) <= convert(date,'" + PolFechAplicacionFin + "', 103)" : "");
                sql += (!"".equals(PolAutomatica) && PolAutomatica != null ? " AND nPolizaAutomatica = '" + PolAutomatica + "'" : "");
                sql += (!"".equals(PolAutorizo) && PolAutorizo != null ? " AND cUsuarioAutorizo = '" + PolAutorizo + "'" : "");
                sql += (!"".equals(PolStatus) && PolStatus != null ? " AND cDocHAplicado = '" + PolStatus + "'" : "");
                sql += (!"".equals(PolNumeroIni) && PolNumeroIni != null ? " AND p.nFolioPoliza >= '" + PolNumeroIni + "'" : "");
                sql += (!"".equals(PolNumeroFin) && PolNumeroFin != null ? " AND p.nFolioPoliza <= '" + PolNumeroFin + "'" : "");
                sql += (!"".equals(PolMontoIni) && PolMontoIni != null ? " AND p.mTotalCargo >= '" + PolMontoIni + "'" : "");
                sql += (!"".equals(PolMontoFin) && PolMontoFin != null ? " AND p.mTotalCargo <= '" + PolMontoFin + "'" : "");
                sql += " group by p.nFolioPoliza , fCreacion , p.cDescripcionPoliza , ";
                sql += " mTotalCargo , mTotalAbono , p.nMes ,p.nCuenta , fAplicacion ,";
                sql += " p.nPolizaAutomatica ,p.cCentroContable , ccc.cDescripcion ,p.aEjercicioFiscal,";
                sql += " p.cTipoPoliza ,ctp.cDescripcionPoliza,p.cTipoDocumento ,p.nFolioDocumento ,";
                sql += " pd.cDocHAplicado, p.cUsuarioAutorizo,P.ID_OPER ";
                sql += " ORDER BY ctipoPoliza,nFolioPoliza";
            } else //
            {
                // / tipo de poliza manual
                sql = " select *from vConsultaPolizaManual as p " + " WHERE 1=1 ";
                sql += (!"".equals(PolCtroContable) && PolCtroContable != null ? " AND p.cCentroContable = '" + PolCtroContable + "'" : "");
                sql += (!"".equals(PolEjercicioFiscal) && PolEjercicioFiscal != null ? " AND p.aEjercicioFiscal = '" + PolEjercicioFiscal + "'" : "");
                sql += (!"".equals(PolOrigen) && PolOrigen != null ? " AND cTipoDocumento = '" + PolOrigen + "'" : "");
                sql += (!"".equals(PolFechCapturaIni) && PolFechCapturaIni != null ? " AND convert(date,p.fCreacion, 103) >= convert(date,'" + PolFechCapturaIni + "', 103)" : "");
                sql += (!"".equals(PolFechCapturaFin) && PolFechCapturaFin != null ? " AND convert(date,p.fCreacion, 103) <= convert(date,'" + PolFechCapturaFin + "', 103)" : "");
                sql += (!"".equals(PolFechAplicacionIni) && PolFechAplicacionIni != null ? " AND convert(date,p.fAplicacion, 103) >= convert(date,'" + PolFechAplicacionIni + "', 103)" : "");
                sql += (!"".equals(PolFechAplicacionFin) && PolFechAplicacionFin != null ? " AND convert(date,p.fAplicacion, 103) <= convert(date,'" + PolFechAplicacionFin + "', 103)" : "");
                sql += (!"".equals(PolAutomatica) && PolAutomatica != null ? " AND nPolizaAutomatica = '" + PolAutomatica + "'" : "");
                sql += (!"".equals(PolTipo) && PolTipo != null ? " AND p.cTipoPoliza = '" + PolTipo + "'" : "");
                sql += (!"".equals(PolAutorizo) && PolAutorizo != null ? " AND cUsuarioAutorizo = '" + PolAutorizo + "'" : "");
                if (PolStatus.equals("A")) {
                    // / checar tipo polstatus
                    sql += (!"".equals(PolStatus) && PolStatus != null ? " AND P.ID_OPER=4  and cDocHAplicado!='C'" : "");
                } else if (PolStatus.equals("C")) {
                    // / checar tipo polstatus
                    sql += (!"".equals(PolStatus) && PolStatus != null ? " AND cDocHAplicado = '" + PolStatus + "'" : "");
                } else {
                    sql += (!"".equals(PolStatus) && PolStatus != null ? " AND P.ID_OPER= '" + PolStatus + "'" : "");
                }
                sql += (!"".equals(PolNumeroIni) && PolNumeroIni != null ? " AND p.nFolioPoliza >= '" + PolNumeroIni + "'" : "");
                sql += (!"".equals(PolNumeroFin) && PolNumeroFin != null ? " AND p.nFolioPoliza <= '" + PolNumeroFin + "'" : "");
                sql += (!"".equals(PolMontoIni) && PolMontoIni != null ? " AND p.mTotalCargo >= '" + PolMontoIni + "'" : "");
                sql += (!"".equals(PolMontoFin) && PolMontoFin != null ? " AND p.mTotalCargo <= '" + PolMontoFin + "'" : "");
                sql += " ORDER BY ctipoPoliza,nFolioPoliza";
            }
            log.debug(sql);
            polizaList = ReporteManager.selectConsultaPolizas(conn, sql);
            log.debug("salida de selectConsultaPolizas ");
            int row = 0;
            String jSonI = "{\"aaData\":[";
            String jSonF = "]}";
            String token = "";
            for (Iterator<?> iter = polizaList.iterator(); iter.hasNext(); row++) {
                Poliza cPoliza = (Poliza) iter.next();
                String arrJSON = token + "[";
                arrJSON += "\"" + "<input  id=\\\"selection\\\" " + "name=\\\"selection\\\" " + "value=\\\"" + // + "(v.nFolioPoliza=" + cPoliza.getNfolioPoliza()
                // + " and v.cCentroContable=''"
                // + cPoliza.getCcentroContable()
                // + "'' and v.cTipoPoliza=''" + cPoliza.getCtipoPoliza()
                // + "'')"
                "[" + cPoliza.getNfolioPoliza() + ",''" + cPoliza.getCcentroContable() + "'',''" + cPoliza.getCtipoPoliza() + "'',''" + cPoliza.getDocHAplicado() + "'']" + "\\\"" + " type=\\\"checkbox\\\"" + ">" + "\",";
                arrJSON += "\"" + cPoliza.getCtipoPoliza() + "\",";
                arrJSON += "\"" + cPoliza.getNfolioPoliza() + "\",";
                arrJSON += "\"" + cPoliza.getFcreacion() + "\",";
                arrJSON += "\"" + cPoliza.getFaplicacion() + "\",";
                if (cPoliza.getNpolizaAutomatica().equals("0")) {
                    arrJSON += "\"" + (((cPoliza.getIdOper().equals("4")) && (cPoliza.getDocHAplicado().equals("C"))) ? "CANCELADA" : // CHECAR
                    cPoliza.getIdOper().equals("1") && (cPoliza.getDocHAplicado().equals("E")) ? // CHECAR
                    "EN CAPTURA" : // ID_OPER
                    cPoliza.getIdOper().equals("0") && (cPoliza.getCdescripcionPoliza().equals("Cancelación de DOCPOLIZA")) ? "AUTORIZADA APLICADA" : cPoliza.getIdOper().equals("0") ? "EN REVISION" : cPoliza.getIdOper().equals("2") ? "EN REVISION" : cPoliza.getIdOper().equals("3") ? "EN AUTORIZACION" : cPoliza.getIdOper().equals("4") ? "EN CONSULTA" : "") + "\",";
                } else {
                    arrJSON += "\"" + (cPoliza.getDocHAplicado().equals("C") ? "CANCELADA" : cPoliza.getDocHAplicado().equals("S") ? "AUTORIZADA APLICADA" : cPoliza.getDocHAplicado().equals("A") ? "AUTORIZADA APLICADA" : cPoliza.getDocHAplicado().equals("E") ? "EDITADO" : "") + "\",";
                }
                arrJSON += "\"" + (cPoliza.getNpolizaAutomatica().equals("1") ? "AUTOMATICA" : "CONTABILIDAD") + "\",";
                arrJSON += "\"" + (cPoliza.getCdescripcionPoliza().equals("") || cPoliza.getCdescripcionPoliza() == null ? "SIN DESCRIPCION" : cPoliza.getCdescripcionPoliza().replaceAll("(\r\n|\n\r|\r|\n)", "<br />")) + "\",";
                arrJSON += "\"" + (cPoliza.getCusuarioAutorizo().equals("") ? "SIN USUARIO" : cPoliza.getCusuarioAutorizo()) + "\",";
                arrJSON += "\"" + (cPoliza.getCcentroContable()) + "\"";
                arrJSON += "]";
                jSonI += arrJSON;
                token = ",";
            }
            log.debug("Termina de cargar " + (row--) + " registros para el reporte");
            sb.append(jSonI);
            sb.append(jSonF);
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                // log.warn("Error en rollback", ex);
            }
            log.error("Error en MultiReporte ", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return sb.toString();
    }

    public String ReportePAOP(String aEjercicioFiscal, String mesIni) {
        Connection conn = null;
        StringBuffer sb = new StringBuffer();
        List<?> PAOPList = new ArrayList<Object>();
        try {
            conn = getConnection();
            log.debug("EXECUTE sp_PAOP_syc @Salida OUTPUT, '" + aEjercicioFiscal + "', '" + mesIni + "'; ");
            PAOPList = ReporteManager.callPAOP(conn, aEjercicioFiscal, mesIni);
            log.debug("salida de sp_PAOP_syc ");
            String tr = "";
            int row = 0;
            if (PAOPList.isEmpty()) {
                tr = "<tr class=\"alternateRow\">" + "	<td colspan=\"4\"><i>No hay informaci&oacute;n para mostrar</i></td>" + "</tr>";
                sb.append(tr);
            } else {
                for (Iterator<?> iter = PAOPList.iterator(); iter.hasNext(); row++) {
                    PAOP rPAOP = (PAOP) iter.next();
                    if (rPAOP.getCejercicioFiscal() != null && !rPAOP.getCejercicioFiscal().trim().equals("")) {
                        tr = "<tr class=" + ((row % 2) == 0 ? "AlternateRow" : "NormalRow") + ">";
                        // tr += "<td>"+ rPAOP.getCejercicioFiscal()+ "</td>";
                        // tr += "<td>"+ rPAOP.getNmes()+ "</td>";
                        // tr += "<td>"+ rPAOP.getNrenglon()+ "</td>";
                        tr += "<td>" + rPAOP.getCcVE_CUCOP() + "</td>";
                        tr += "<td>" + rPAOP.getCcONCEPTO() + "</td>";
                        tr += "<td>" + rPAOP.getMmultianualEstimado() + "</td>";
                        tr += "<td>" + rPAOP.getMestimadoMipymes() + "</td>";
                        tr += "<td>" + rPAOP.getMestimadoNoCubiertasTLC() + "</td>";
                        tr += "<td>" + rPAOP.getNcantidad() + "</td>";
                        tr += "<td>" + rPAOP.getNunidadMedida() + "</td>";
                        tr += "<td>" + rPAOP.getCtipoProcContratacion() + "</td>";
                        tr += "<td>" + rPAOP.getNentidadFederativa() + "</td>";
                        tr += "<td>" + rPAOP.getNtrimestre1() + "</td>";
                        tr += "<td>" + rPAOP.getNtrimestre2() + "</td>";
                        tr += "<td>" + rPAOP.getNtrimestre3() + "</td>";
                        tr += "<td>" + rPAOP.getNtrimestre4() + "</td>";
                        tr += "<td>" + rPAOP.getFinicialContrato() + "</td>";
                        tr += "<td>" + rPAOP.getNplurianual() + "</td>";
                        tr += "<td>" + rPAOP.getNejerciciosFicales() + "</td>";
                        tr += "<td>" + rPAOP.getManualEjercer() + "</td>";
                        tr += "<td>" + rPAOP.getCcomentario1() + "</td>";
                        tr += "<td>" + rPAOP.getFfinalContrato() + "</td>";
                        tr += "<td>" + rPAOP.getCcomentario3() + "</td>";
                        tr += "<td>" + rPAOP.getCtipoProcedimiento() + "</td>";
                        // tr += "<td>"+ rPAOP.getCstatus()+ "</td>";
                        tr += "</tr>";
                        sb.append(tr);
                    }
                }
            }
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
            }
            log.error("Actualizando caso", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return sb.toString();
    }

    public String ReporteBalanza(String buscaCuentaIni, String buscaCuentaFin, String aEjercicioFiscal, String DepuraLineas, String DepuraColumnas, String cCentroContable, String TipoReporte, String mesIni, String mesFin, String FiltroSubcuenta) {
        Connection conn = null;
        StringBuffer sb = new StringBuffer();
        List<?> balanzaList = new ArrayList<Object>();
        try {
            conn = getConnection();
            log.debug("EXECUTE sp_tBalanzaAnalitico_syc @Salida OUTPUT, '" + buscaCuentaIni + "', '" + buscaCuentaFin + "', '" + aEjercicioFiscal + "', '" + DepuraLineas + "', '" + DepuraColumnas + "', '" + cCentroContable + "', '" + TipoReporte + "', '" + mesIni + "', '" + mesFin + "', " + FiltroSubcuenta + "'; ");
            balanzaList = ReporteManager.callBalanza(conn, buscaCuentaIni, buscaCuentaFin, aEjercicioFiscal, DepuraLineas, DepuraColumnas, cCentroContable, TipoReporte, mesIni, mesFin, FiltroSubcuenta);
            log.debug("salida de sp_tBalanzaAnalitico_syc ");
            String tr = "";
            int row = 0;
            if (balanzaList.isEmpty()) {
                tr = "<tr class=\"alternateRow\">" + "	<td colspan=\"4\"><i>No hay informaci&oacute;n para mostrar</i></td>" + "</tr>";
                sb.append(tr);
            } else {
                for (Iterator<?> iter = balanzaList.iterator(); iter.hasNext(); row++) {
                    Balanza rBalanza = (Balanza) iter.next();
                    if (rBalanza.getNcuenta() != null && !rBalanza.getNcuenta().trim().equals("")) {
                        tr = "<tr class=" + ((row % 2) == 0 ? "AlternateRow" : "NormalRow") + " >" + "<td style='mso-number-format:\"\\@\"'>" + rBalanza.getNcuenta() + "</td>" + "<td style='mso-number-format:\"\\@\"'>" + rBalanza.getDcuenta() + "</td>";
                        if (DepuraColumnas.equals("N")) {
                            tr += "<td style='mso-number-format:\"Standard\"'>" + rBalanza.getSaldoInicial() + "</td>" + "<td style='mso-number-format:\"Standard\"'>" + rBalanza.getMovimientosAcumuladosDebe() + "</td>" + "<td style='mso-number-format:\"Standard\"'>" + rBalanza.getMovimientosAcumuladosHaber() + "</td>" + "<td style='mso-number-format:\"Standard\"'>" + rBalanza.getSaldoMesAnterior() + "</td>" + "<td style='mso-number-format:\"Standard\"'>" + rBalanza.getMovimientosMesDebe() + "</td>" + "<td style='mso-number-format:\"Standard\"'>" + rBalanza.getMovimientosMesHaber() + "</td>" + "<td style='mso-number-format:\"Standard\"'>" + rBalanza.getSaldoFinal() + "</td>";
                        } else {
                            tr += "<td style='mso-number-format:\"Standard\"'>" + rBalanza.getSaldoInicialDeudor() + "</td>" + "<td style='mso-number-format:\"Standard\"'>" + rBalanza.getSaldoInicialAcreedor() + "</td>" + "<td style='mso-number-format:\"Standard\"'>" + rBalanza.getMovimientosMesDebe() + "</td>" + "<td style='mso-number-format:\"Standard\"'>" + rBalanza.getMovimientosMesHaber() + "</td>" + // "<td>"+rBalanza.getMovimientosAcumuladosDebe()+"</td>"+
                            // "<td>"+rBalanza.getMovimientosAcumuladosHaber()+"</td>"+
                            "<td style='mso-number-format:\"Standard\"'>" + rBalanza.getSaldoFinalDeudor() + "</td>" + "<td style='mso-number-format:\"Standard\"'>" + rBalanza.getSaldoFinalAcreedor() + "</td>";
                        }
                        tr += "</tr>";
                        sb.append(tr);
                    }
                }
            }
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
            }
            log.error("Actualizando caso", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return sb.toString();
    }

    public ArrayList<String> ReporteBalanzaList(String buscaCuentaIni, String buscaCuentaFin, String aEjercicioFiscal, String DepuraLineas, String DepuraColumnas, String cCentroContable, String TipoReporte, String mesIni, String mesFin, String FiltroSubcuenta) {
        Connection conn = null;
        ArrayList<String> balanzaList = new ArrayList<String>();
        try {
            conn = getConnection();
            log.debug("EXECUTE sp_tBalanzaAnalitico_syc @Salida OUTPUT, '" + buscaCuentaIni + "', '" + buscaCuentaFin + "', '" + aEjercicioFiscal + "', '" + DepuraLineas + "', '" + DepuraColumnas + "', '" + cCentroContable + "', '" + TipoReporte + "', '" + mesIni + "', '" + mesFin + "','" + FiltroSubcuenta + "'; ");
            if (TipoReporte.equalsIgnoreCase("Balanza"))
                balanzaList = (ArrayList<String>) ReporteManager.generaBalanza(conn, buscaCuentaIni, buscaCuentaFin, aEjercicioFiscal, DepuraLineas, DepuraColumnas, cCentroContable, TipoReporte, mesIni, mesFin, FiltroSubcuenta);
            else {
                System.out.println("Saltando balanzas");
                balanzaList = (ArrayList<String>) ReporteManager.generaAnalitico(conn, buscaCuentaIni, buscaCuentaFin, aEjercicioFiscal, DepuraLineas, DepuraColumnas, cCentroContable, TipoReporte, mesIni, mesFin, FiltroSubcuenta);
            }
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
            }
            log.error("Actualizando caso", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return balanzaList;
    }

    /*
	 * public ArrayList<String> multiReporteObra(String buscaCuentaIni, String
	 * buscaCuentaFin, String aEjercicioFiscal, String DepuraLineas, String
	 * DepuraColumnas, String cCentroContable, String TipoReporte, String
	 * mesIni, String mesFin, String FiltroSubcuenta) {
	 */
    public ArrayList<String> multiReporteObra(String cCentroContable, String fechaI, String fechaF, String idunidadresponsable, boolean generarVacio, String reportBody, String strUsuario, String strCondicion, String strCondMultiR, String columnasBorrar, String[] NomCols, String[] ForCols, ArrayList Todo) {
        Connection conn = null;
        ArrayList<String> balanzaList = new ArrayList<String>();
        try {
            conn = getConnection();
            balanzaList = (ArrayList<String>) ReporteManager.generaMultiReporteOP(conn, cCentroContable, fechaI, fechaF, idunidadresponsable, generarVacio, reportBody, strUsuario, strCondicion, strCondMultiR, columnasBorrar, NomCols, ForCols, Todo);
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
            }
            log.error("Actualizando caso", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return balanzaList;
    }

    public String ReporteAuxiliares(String swhere, String cCentroContable, String buscaCuentaIni, String fAuxIni, String fAuxFin) {
        Connection conn = null;
        StringBuffer sb = new StringBuffer();
        List<?> auxiliaresList = new ArrayList<Object>();
        try {
            conn = getConnection();
            log.debug("EXECUTE sp_GeneraAuxiliarCNF @Salida OUTPUT, '" + swhere + "', '" + cCentroContable + "', '" + buscaCuentaIni + "', '" + fAuxIni + "', '" + fAuxFin + "'; ");
            auxiliaresList = ReporteManager.callAuxiliares(conn, swhere, cCentroContable, buscaCuentaIni, fAuxIni, fAuxFin);
            log.debug("salida de sp_GeneraAuxiliarCNF ");
            String tr = "";
            int row = 0;
            if (auxiliaresList.isEmpty()) {
                tr = "<tr class=\"alternateRow\">" + "	<td colspan=\"4\"><i>No hay informaci&oacute;n para mostrar</i></td>" + "</tr>";
                sb.append(tr);
            } else {
                for (Iterator<?> iter = auxiliaresList.iterator(); iter.hasNext(); row++) {
                    Auxiliares rAuxiliares = (Auxiliares) iter.next();
                    if (rAuxiliares.getAuxTipo() != null) // && !rAuxiliares.getAuxTipo().trim().equals("")
                    {
                        // if (rAuxiliares.getNcuenta() != null
                        // && !rAuxiliares.getNcuenta().trim().equals("")) {
                        tr = "<tr class=" + ((row % 2) == 0 ? "AlternateRow" : "NormalRow") + ">" + "<td>" + rAuxiliares.getAuxTipo() + "</td>" + "<td>" + (rAuxiliares.getAuxNum().equals("0") ? "" : rAuxiliares.getAuxNum()) + "</td>" + "<td>" + rAuxiliares.getAuxCxP() + "</td>" + "<td style='mso-number-format:\"dd/mm/yyyy\"'>" + rAuxiliares.getAuxFecha() + "</td>" + "<td>" + rAuxiliares.getAuxConcepto() + "</td>" + "<td>" + rAuxiliares.getAuxReferencia() + "</td>" + "<td style='mso-number-format:\"Standard\"'>" + rAuxiliares.getAuxCargos() + "</td>" + "<td style='mso-number-format:\"Standard\"'>" + rAuxiliares.getAuxAbonos() + "</td>" + "<td style='mso-number-format:\"Standard\"'>" + rAuxiliares.getAuxSaldo() + "</td>" + // + rAuxiliares.getNcuenta() + "</td>" + "<td>"
                        // + rAuxiliares.getNfolioPoliza() + "</td>"
                        // + "<td>" + rAuxiliares.getCcentroContable()
                        // + "</td>" + "<td>"
                        // + rAuxiliares.getCtipoDocumento() + "</td>"
                        // + "<td>" + rAuxiliares.getCdescripcionMovPol()
                        // + "</td>" + "<td>"
                        // + rAuxiliares.getCfolioDocumentoMovimiento()
                        // + "</td>" +
                        // "<td style='mso-number-format:\"dd/mm/yyyy\"'>"
                        // + rAuxiliares.getFmovimiento() + "</td>"
                        // + "<td>" + rAuxiliares.getCtipoPoliza()
                        // + "</td>" + "<td>"
                        // + rAuxiliares.getCtipoMovimiento() + "</td>"
                        // + "<td style='mso-number-format:\"Standard\"'>" +
                        // rAuxiliares.getMmovimiento()
                        // + "</td>" +
                        // "<td style='mso-number-format:\"Standard\"'>"
                        // + rAuxiliares.getMacumulado() + "</td>"
                        "</tr>";
                        sb.append(tr);
                    }
                }
            }
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
            }
            log.error("Actualizando caso", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return sb.toString();
    }

    public ArrayList<String> ReporteAuxiliaresList(String swhere, String cCentroContable, String buscaCuentaIni, String fAuxIni, String fAuxFin) {
        Connection conn = null;
        ArrayList<String> auxiliaresList = new ArrayList<String>();
        try {
            conn = getConnection();
            log.debug("EXECUTE sp_GeneraAuxiliarCNF @Salida OUTPUT, '" + swhere + "', '" + cCentroContable + "', '" + buscaCuentaIni + "', '" + fAuxIni + "', '" + fAuxFin + "'; ");
            auxiliaresList = ReporteManager.generaAuxiliar(conn, swhere, cCentroContable, buscaCuentaIni, fAuxIni, fAuxFin);
            log.debug("salida de sp_GeneraAuxiliarCNF ");
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
            }
            log.error("Actualizando caso", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return auxiliaresList;
    }

    public ArrayList<String> ReporteAuxiliarMayor(String buscaCuentaIni, String subCuenta, String cCentroContable, String fAuxIni, String fAuxFin, String subtot) {
        Connection conn = null;
        ArrayList<String> auxiliaresList = new ArrayList<String>();
        try {
            conn = getConnection();
            log.debug("EXECUTE sp_l_GeneraAuxiliarMayor '" + buscaCuentaIni + "', '" + subCuenta + "', '" + cCentroContable + "', '" + fAuxIni + "', '" + fAuxFin + "', '" + subtot + "'; ");
            auxiliaresList = ReporteManager.generaAuxiliarMayor(conn, buscaCuentaIni, subCuenta, cCentroContable, fAuxIni, fAuxFin, subtot);
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
            }
            log.error("Actualizando caso", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return auxiliaresList;
    }

    public ArrayList<String> ReporteAuxiliarMayorSaldos(String buscaCuentaIni, String subCuenta, String cCentroContable, String fAuxIni, String fAuxFin) {
        Connection conn = null;
        ArrayList<String> auxiliaresList = new ArrayList<String>();
        try {
            conn = getConnection();
            log.debug("EXECUTE sp_l_AuxiliarMayorSaldos '" + buscaCuentaIni + "', '" + subCuenta + "', '" + cCentroContable + "', '" + fAuxIni + "', '" + fAuxFin + "'; ");
            auxiliaresList = ReporteManager.generaAuxiliarMayorSaldos(conn, buscaCuentaIni, subCuenta, cCentroContable, fAuxIni, fAuxFin);
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
            }
            log.error("Actualizando caso", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return auxiliaresList;
    }

    public String ReporteAuditoria(String modulo, String accion, String login, String fechaini, String fechafin, String area) {
        Connection conn = null;
        StringBuffer sb = new StringBuffer();
        List<?> auditoria = new ArrayList<Object>();
        try {
            conn = getConnection();
            auditoria = AuditoriaManager.select(conn, modulo, accion, login, fechaini, fechafin, area);
            String tr = "";
            int row = 0;
            if (auditoria.isEmpty()) {
                tr = "<tr class=\"alternateRow\">" + "	<td colspan=\"9\"><i>No hay informaci&oacute;n para mostrar</i></td>" + "</tr>";
                sb.append(tr);
            } else {
                for (Iterator<?> iter = auditoria.iterator(); iter.hasNext(); row++) {
                    Auditoria a = (Auditoria) iter.next();
                    tr = "<tr class=" + ((row % 2) == 0 ? "AlternateRow" : "NormalRow") + "><td>&nbsp;</td>" + "<td>" + a.getNombre_modulo() + "</td>" + "<td>" + a.getAccion() + "</td>" + "<td>" + a.getFecha_operacion() + "</td>" + "<td>" + a.getValor_origen() + "</td>" + "<td>" + a.getValor_destino() + "</td>" + "<td>" + a.getUsuario() + "</td>" + "<td>" + a.getNombre_usuario() + "</td>" + "<td>" + a.getArea_usuario() + "</td>" + "<td>" + a.getArea_padre() + "</td>" + "</tr>";
                    sb.append(tr);
                }
            }
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                // log.warn("Error en rollback", ex);
            }
            log.error("Actualizando caso", exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return sb.toString();
    }

    public String ReportePorArea(String area, String estructura, String regfechaini, String regfechafin, boolean detallado) {
        Connection conn = null;
        StringBuffer sb = new StringBuffer();
        ResultSet rs = null;
        String param = "idRptMaestro=4" + "&estruc=" + estructura + "&fechaini=" + regfechaini + "&fechafin=" + regfechafin + "&detallado=" + detallado;
        try {
            conn = getConnection();
            // Reporte por area
            String whereFechas = "";
            if (regfechaini != "" && regfechafin != "") {
                whereFechas = " AND CONVERT(DATETIME, vimx.fechaRegistro, 103) BETWEEN CONVERT(DATETIME, '" + regfechaini + "', 103) AND CONVERT(DATETIME, '" + regfechafin + "', 103) ";
            }
            String whereEstructura = "";
            if (area != "") {
                whereEstructura = "		     AND ca.area_estructura LIKE '" + estructura + (detallado ? "%" : "") + "' ";
                System.out.println("where estructura: " + estructura);
            }
            String sql = "" + /*
			 * Esteban Badillo. Fecha: 10/Sep/2009 Descripcion: Se elimina la
			 * del grid de la pantalla del Reporte Estadístico por Area, ya que
			 * dichos datos no empataban con la consulta que extrae el detalle
			 * del mismo reporte.
			 */
            /*
			 * + "SELECT " + "     responsable_area" + "   , area_estructura " +
			 * "   , desc_area_resp " +
			 * "   , SUM(pendientesVencidos)   AS pendientesVencidos " +
			 * "   , SUM(pendientesNoVencidos) AS pendientesNoVencidos " +
			 * "   , SUM(pendientes)           AS pendientes " +
			 * "   , SUM(cerrado)              AS cerrado " //+
			 * "   , SUM(casos)                AS casos " //CAMBIO: Esteban
			 * Antonio Badillo Martinez. Fecha: 27/Agosto/2009. Descripcion: Se
			 * elimina. + "	  , SUM(pendientes + cerrado) as casos " //CAMBIO:
			 * Descripcion: Se agrega. +
			 * "   , GETDATE()                 AS HOY1 " + "			FROM ( " +
			 * "					SELECT vimx.responsable_area, vimx.desc_area_resp , 0 AS casos, COUNT(DISTINCT(vimx.id_caso)) AS pendientesNoVencidos, 0 AS pendientesVencidos, 0 AS pendientes, 0 AS cerrado "
			 * + "					  FROM vimx_reportes vimx " +
			 * "					 WHERE vimx.terminada = 'N' " +
			 * "				       AND vimx.id_operacion in(2,3,7, 9,10,11)" +
			 * "                    AND vimx.fecha_compromiso > GETDATE() " +
			 * "                    AND vimx.titular = 'S' " + "						" +
			 * whereFechas +
			 * "				  GROUP BY vimx.responsable_area, vimx.desc_area_resp " +
			 * "		   UNION ALL " +
			 * "					SELECT vimx.responsable_area, vimx.desc_area_resp , 0 AS casos, 0 AS pendientesNoVencidos, COUNT(DISTINCT(vimx.id_caso)) AS pendientesVencidos, 0 AS pendientes, 0 AS cerrado "
			 * + "					  FROM vimx_reportes vimx " +
			 * "					 WHERE vimx.terminada = 'N' " +
			 * "				       AND vimx.id_operacion in(2,3,7, 9,10,11)" +
			 * "                    AND vimx.fecha_compromiso < GETDATE() " +
			 * "                    AND vimx.titular = 'S' " + "						" +
			 * whereFechas +
			 * "				  GROUP BY vimx.responsable_area, vimx.desc_area_resp " +
			 * "		   UNION ALL " +
			 * "					SELECT vimx.responsable_area, vimx.desc_area_resp , 0 AS casos, 0 AS pendientesNoVencidos, 0 AS pendientesVencidos, COUNT(DISTINCT(vimx.id_caso)) AS pendientes, 0 AS cerrado "
			 * + "					  FROM vimx_reportes vimx " +
			 * "					 WHERE vimx.terminada = 'N' " +
			 * "				       AND vimx.id_operacion in(2,3,7, 9,10,11)" +
			 * "                    AND vimx.titular = 'S' " +
			 * "				  GROUP BY vimx.responsable_area, vimx.desc_area_resp " +
			 * "		   UNION ALL " +
			 * "					SELECT vimx.responsable_area, vimx.desc_area_resp , 0 AS casos, 0 AS pendientesNoVencidos, 0 AS pendientesVencidos, 0 AS pendientes, COUNT(DISTINCT(vimx.id_caso)) AS cerrado "
			 * + "					  FROM vimx_reportes vimx " +
			 * "				     WHERE vimx.terminada = 'S'" +
			 * "				       AND vimx.ID_OPERACION in(2,3,7, 9,10,11)" +
			 * "                    AND vimx.titular = 'S' " + "						" +
			 * whereFechas +
			 * "				  GROUP BY vimx.responsable_area, vimx.desc_area_resp " +
			 * "		   UNION ALL " +
			 * "					SELECT vimx.responsable_area, vimx.desc_area_resp , COUNT(DISTINCT(vimx.id_caso)) AS casos, 0 AS pendientesNoVencidos, 0 AS pendientesVencidos, 0 AS pendientes, 0 AS cerrado "
			 * + "					  FROM vimx_reportes vimx " + "					 WHERE 1 = 1 " +
			 * "				       AND vimx.ID_OPERACION in(2,3,7, 9,10,11)" +
			 * "                    AND vimx.titular = 'S' " + "						" +
			 * whereFechas +
			 * "				  GROUP BY vimx.responsable_area, vimx.desc_area_resp " +
			 * "				) t " +
			 * "			   LEFT OUTER JOIN CG_CAT_AREAS ca ON (ca.id_area = t.responsable_area) "
			 * + "		   WHERE desc_area_resp IS NOT NULL " +
			 * "                     " + whereEstructura +
			 * "		GROUP BY responsable_area, area_estructura, desc_area_resp " ;
			 */
            "" + /*
			 * Esteban Badillo. Fecha: 10/Sep/2009 Descripcion: Se sustituye la
			 * consulta a la base de datos que extrae el resumen presentado en
			 * el grid del Reporte Estadístico por Area, ya que el anterior no
			 * empataba con la consulta realizada en el detalle del mismo
			 * reporte.
			 */
            "SELECT DISTINCT " + "   t.id_caso, " + "   t.folio, " + "   t.referencia, " + "   t.nombre_rem, " + "   t.desc_area_rem, " + "   cerrado = CASE " + "      WHEN t.terminada = 'S' THEN 'CONCLUIDOS' " + "      WHEN t.terminada = 'N' AND t.fecha_compromiso < GETDATE() THEN 'VENCIDOS' " + "      WHEN t.terminada = 'N' AND t.fecha_compromiso > GETDATE() THEN 'NO VENCIDOS' END, " + "   CASE " + "      WHEN t.prioridad = 'N' THEN 'NORMAL' " + "      ELSE 'URGENTE' END as prioridad, " + "   t.fecha_compromiso as fechalimite, " + "   t.responsable_area, " + "   t.desc_area_resp, " + "   ca.area_estructura " + "INTO #TMP_VENCIDOS " + "FROM " + "   ( " + "   SELECT " + "	     vimx.* " + "   FROM " + "	     vimx_reportes vimx " + "   WHERE " + "	     1 = 1 " + "	     AND vimx.id_operacion in(2,3,7, 9,10,11) " + "	     AND vimx.terminada = 'N' " + "	     AND vimx.fecha_compromiso < GETDATE() " + "	     AND vimx.titular = 'S' " + "	     " + whereFechas + "   ) t " + "LEFT OUTER JOIN " + "   CG_CAT_AREAS ca " + "   ON (ca.id_area = t.responsable_area) " + "WHERE " + "   t.desc_area_resp IS NOT NULL " + "SELECT DISTINCT " + "   t.id_caso, " + "   t.folio, " + "   t.referencia, " + "   t.nombre_rem, " + "   t.desc_area_rem, " + "   cerrado = CASE " + "      WHEN t.terminada = 'S' THEN 'CONCLUIDOS' " + "      WHEN t.terminada = 'N' AND t.fecha_compromiso < GETDATE() THEN 'VENCIDOS' " + "      WHEN t.terminada = 'N' AND t.fecha_compromiso > GETDATE() THEN 'NO VENCIDOS' END, " + "   CASE " + "      WHEN t.prioridad = 'N' THEN 'NORMAL' " + "      ELSE 'URGENTE' END as prioridad, " + "   t.fecha_compromiso as fechalimite, " + "   t.responsable_area, " + "   t.desc_area_resp, " + "   ca.area_estructura " + "INTO #TMP_NO_VENCIDOS " + "FROM " + "   ( " + "   SELECT " + "      vimx.* " + "   FROM " + "      vimx_reportes vimx " + "   WHERE " + "      1 = 1 " + "      AND vimx.id_operacion in(2,3,7, 9,10,11) " + "      AND vimx.terminada = 'N' " + "      AND vimx.fecha_compromiso > GETDATE() " + "      AND vimx.titular = 'S' " + "      " + whereFechas + "   ) t " + "LEFT OUTER JOIN " + "   CG_CAT_AREAS ca " + "   ON (ca.id_area = t.responsable_area) " + "WHERE " + "   t.desc_area_resp IS NOT NULL " + "SELECT DISTINCT " + "   t.id_caso, " + "   t.folio, " + "   t.referencia, " + "   t.nombre_rem, " + "   t.desc_area_rem, " + "   cerrado = CASE " + "      WHEN t.terminada = 'S' THEN 'CONCLUIDOS' " + "      WHEN t.terminada = 'N' AND t.fecha_compromiso < GETDATE() THEN 'VENCIDOS' " + "      WHEN t.terminada = 'N' AND t.fecha_compromiso > GETDATE() THEN 'NO VENCIDOS' END, " + "   CASE " + "      WHEN t.prioridad = 'N' THEN 'NORMAL' " + "      ELSE 'URGENTE' END as prioridad, " + "   t.fecha_compromiso as fechalimite, " + "   t.responsable_area, " + "   t.desc_area_resp, " + "   ca.area_estructura " + "   INTO #TMP_CONCLUIDOS " + "FROM " + "   ( " + "   SELECT " + "      vimx.* " + "   FROM " + "      vimx_reportes vimx " + "   WHERE " + "      1 = 1 " + "      AND vimx.id_operacion in(2,3,7, 9,10,11) " + "	     AND vimx.terminada = 'S' " + "      AND vimx.titular = 'S' " + "      " + whereFechas + "   ) t " + "LEFT OUTER JOIN " + "   CG_CAT_AREAS ca " + "   ON (ca.id_area = t.responsable_area) " + "WHERE " + "   t.desc_area_resp IS NOT NULL " + "SELECT * INTO #TMP_TODOS FROM #TMP_NO_VENCIDOS " + "UNION ALL " + "SELECT * FROM #TMP_VENCIDOS " + "UNION ALL " + "SELECT * FROM #TMP_CONCLUIDOS " + "SELECT " + "   responsable_area, " + "   area_estructura, " + "   desc_area_resp, " + "   0 as casos, " + "   count(folio) as pendientesNoVencidos, " + "   0 as pendientesVencidos, " + "   0 as pendientes, " + "   0 as cerrado " + "INTO #TMP_NO_VENCIDOS_RESUMEN " + "FROM " + "   #TMP_NO_VENCIDOS " + "GROUP BY " + "   folio, " + "   responsable_area, " + "   area_estructura, " + "   desc_area_resp " + "SELECT " + "   responsable_area, " + "   area_estructura, " + "   desc_area_resp, " + "   0 as casos, " + "   0 as pendientesNoVencidos, " + "   count(folio) as pendientesVencidos, " + "   0 as pendientes, " + "   0 as cerrado " + "INTO #TMP_VENCIDOS_RESUMEN " + "FROM " + "   #TMP_VENCIDOS " + "GROUP BY " + "   folio, " + "   responsable_area, " + "   area_estructura, " + "   desc_area_resp " + "SELECT " + "   responsable_area, " + "   area_estructura, " + "   desc_area_resp, " + "   0 as casos, " + "   0 as pendientesNoVencidos, " + "   0 as pendientesVencidos, " + "   0 as pendientes, " + "   count(folio) as cerrado " + "INTO #TMP_CONCLUIDOS_RESUMEN " + "FROM " + "#TMP_CONCLUIDOS " + "GROUP BY " + "   folio, " + "   responsable_area, " + "   area_estructura, " + "   desc_area_resp " + "SELECT * INTO #TMP_TODOS_RESUMEN FROM #TMP_VENCIDOS_RESUMEN " + "UNION ALL " + "SELECT * FROM #TMP_NO_VENCIDOS_RESUMEN " + "UNION ALL " + "SELECT * FROM #TMP_CONCLUIDOS_RESUMEN " + "SELECT " + "   responsable_area, " + "   area_estructura, " + "   desc_area_resp, " + "   sum(pendientesNoVencidos + pendientesVencidos + cerrado) as casos , " + "   sum(pendientesNoVencidos) as pendientesNoVencidos, " + "   sum(pendientesVencidos) as pendientesVencidos, " + "   sum(pendientesNoVencidos + pendientesVencidos) as pendientes, " + "   sum(cerrado) as cerrado " + "FROM #TMP_TODOS_RESUMEN ca " + "WHERE 1 = 1 " + "   " + whereEstructura + "GROUP BY " + "   responsable_area, " + "   area_estructura, " + "   desc_area_resp " + "ORDER BY " + "   desc_area_resp ";
            System.out.println("Query Por Area:\n" + sql);
            List<?> acum = new ArrayList<Object>();
            acum = ReporteManager.select(conn, sql, "responsable_area", "desc_area_resp");
            // Reporte por Empleado
            sb.append(sql + "|");
            String tr = "";
            String param_tot = "";
            int totalVencidos = 0;
            int totalNoVencidos = 0;
            int totalPendientes = 0;
            int totalConcluidos = 0;
            int totalCasos = 0;
            int row = 0;
            if (acum.isEmpty()) {
                tr = "<tr class=\"alternateRow\">" + "	<td colspan=\"9\"><i>No hay informaci&oacute;n para mostrar</i></td>" + "</tr>";
                sb.append(tr);
            } else {
                for (Iterator<?> iter = acum.iterator(); iter.hasNext(); row++) {
                    Acumulado a = (Acumulado) iter.next();
                    totalVencidos += a.getVencidosNumero();
                    totalNoVencidos += a.getNovencidosNumero();
                    totalPendientes += a.getPendientes();
                    totalConcluidos += a.getConcluidos();
                    totalCasos += a.getTotalTurnos();
                    tr = RenglonDatoHTML(GestionInterface.RPT_POR_AREA_DETALLE, a.getDescripcion(), a.getVencidosNumero(), a.getNovencidosNumero(), a.getPendientes(), a.getConcluidos(), a.getTotalTurnos(), row, param + "&area=" + a.getId());
                    param_tot = param + "&area=" + a.getId();
                    sb.append(tr);
                }
                tr = RenglonDatoHTML(GestionInterface.RPT_POR_AREA_DETALLE, "TOTALES", totalVencidos, totalNoVencidos, totalPendientes, totalConcluidos, totalCasos, row + 1, (row == 1 ? param_tot : param));
                sb.append(tr);
            }
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                // log.warn("Error en rollback", ex);
            }
            log.error("Actualizando caso", exc);
            // throw new GestionException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            rs = null;
            conn = null;
        }
        return sb.toString();
    }

    private // Esteban
    String // Esteban
    RenglonDatoGralHTML(// Esteban
    String area, // Esteban
    String folio, // Esteban
    String referencia, // Esteban
    String remitente_nombre, // Esteban
    String remitente_area, // Esteban
    String responsable_nombre, // Esteban
    String responsable_area, // Esteban
    String fecha_envio, // Esteban
    String fecha_atencion, // Esteban
    String cerrado, // Esteban
    String destinatario, // Esteban
    String prioridad, // Esteban
    String asunto, // Badillo.
    // Fecha:
    // 19/Feb/2010.
    // Se
    // agrega
    // la
    // columna
    // "Asunto"
    int row) {
        // Grid de detalle de datos del Reporte Estadístico por area.
        String[] tipoCSS = { "idTh", "idTdTexto", "idTdNumero" };
        boolean total = "FIN".equals(area);
        String renglon = "\r\n<tr class=" + ((row % 2) == 0 ? "AlternateRow" : "NormalRow") + "> \r\n" + "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[1]) + "\">" + ((total) ? "" : "" + (row + 1)) + "</td>\r\n" + // + "	  <td id=\"" + ((total)? tipoCSS[0]: tipoCSS[1]) + "\">"
        // + area + "</td>"
        "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[1]) + "\">" + folio + "</td>\r\n" + "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + referencia + "</td>\r\n" + "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + remitente_nombre + "</td>\r\n" + "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + remitente_area + "</td>\r\n" + "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + responsable_nombre + "</td>\r\n" + "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + responsable_area + "</td>\r\n" + "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + this.splitFechas(fecha_envio) + "</td>\r\n" + "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + this.splitFechas(fecha_atencion) + "</td>\r\n" + "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + cerrado + "</td>\r\n" + // + "	  <td id=\"" + ((total)? tipoCSS[0]: tipoCSS[2]) + "\">"
        // + destinatario + "</td>"
        "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + prioridad + "</td>\r\n" + "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + asunto + "</td>\r\n" + "</tr>";
        return renglon;
    }

    public String ReportePorAreaDetalle(String area, String estructura, String regfechaini, String regfechafin, int tipoacumulado) {
        Connection conn = null;
        StringBuffer sb = new StringBuffer();
        ResultSet rs = null;
        try {
            conn = getConnection();
            // Reporte por area
            String whereFechas = "";
            if (regfechaini != "" && regfechafin != "") {
                whereFechas = " AND CONVERT(DATETIME, vimx.fechaRegistro, 103) BETWEEN CONVERT(DATETIME, '" + regfechaini + "', 103) AND CONVERT(DATETIME, '" + regfechafin + "', 103) ";
            }
            String whereEstructura = "";
            if (area != "") {
                whereEstructura = "		     AND ca.id_area = " + area;
            }
            String whereBase = "";
            switch(tipoacumulado) {
                case GestionInterface.RPT_CNS_VENCIDOS:
                    whereBase = "                      AND vimx.id_operacion in(2,3,7, 9,10,11) " + "					   AND vimx.terminada = 'N' " + "					   AND vimx.fecha_compromiso < GETDATE() " + "                      AND vimx.titular = 'S' ";
                    break;
                case GestionInterface.RPT_CNS_NOVENCIDOS:
                    whereBase = "                      AND vimx.id_operacion in(2,3,7, 9,10,11) " + "					   AND vimx.terminada = 'N' " + "					   AND vimx.fecha_compromiso > GETDATE()" + "                      AND vimx.titular = 'S' ";
                    break;
                case GestionInterface.RPT_CNS_PENDIENTES:
                    whereBase = "                      AND vimx.id_operacion in(2,3,7, 9,10,11) " + "					   AND vimx.terminada = 'N' " + "                      AND vimx.titular = 'S' ";
                    break;
                case GestionInterface.RPT_CNS_CONCLUIDOS:
                    whereBase = "                      AND vimx.id_operacion in(2,3,7, 9,10,11) " + "					   AND vimx.terminada = 'S' " + "                      AND vimx.titular = 'S' ";
                    break;
                case GestionInterface.RPT_CNS_CASOS:
                    whereBase = "                      AND vimx.id_operacion in(2,3,7, 9,10,11) " + "                      AND vimx.titular = 'S' ";
            }
            // + "        t.responsable_area, "
            // + "        t.desc_area_resp, "
            // + "        t.nombre_resp, "
            String sql = "" + "  SELECT DISTINCT " + "        t.folio, " + "        t.referencia, " + "        t.nombre_rem,  " + "        t.desc_area_rem,  " + "        cerrado = CASE " + "           WHEN t.terminada = 'S' THEN 'CONCLUIDOS'" + "           WHEN t.terminada = 'N' AND t.fecha_compromiso < GETDATE() THEN 'VENCIDOS'" + "           WHEN t.terminada = 'N' AND t.fecha_compromiso > GETDATE() THEN 'NO VENCIDOS'" + "        END, " + "        CASE WHEN t.prioridad = 'N' THEN 'NORMAL' ELSE 'URGENTE' END as prioridad, " + "        t.fecha_compromiso as fechalimite " + "	  FROM ( " + "					SELECT vimx.* " + "					  FROM vimx_reportes vimx " + "					 WHERE 1 = 1" + "                   " + whereBase + "                   " + whereFechas + "			) t " + "			  LEFT OUTER JOIN CG_CAT_AREAS ca ON (ca.id_area = t.responsable_area) " + "   WHERE t.desc_area_resp IS NOT NULL " + "                     " + whereEstructura;
            /*
			 * + "GROUP BY " + "        t.responsable_area," +
			 * "        t.desc_area_resp," + "        t.folio," +
			 * "        t.referencia," + "        t.nombre_rem," +
			 * "        t.fechalimite," + "        t.cerrado," +
			 * "        t.nombre_resp," + "        t.prioridad ";
			 */
            List<?> gral = new ArrayList<Object>();
            gral = ReporteManager.selectAll(conn, sql, 0);
            // Reporte por Empleado
            String tr = "";
            if (gral.isEmpty()) {
                tr = "<tr class=\"alternateRow\">" + "	<td colspan=\"10\"><h3>No hay informaci&oacute;n para mostrar</h3></td>" + "</tr>";
                sb.append(tr);
            } else {
                int row = 0;
                for (Iterator<?> iter = gral.iterator(); iter.hasNext(); row++) {
                    General a = (General) iter.next();
                    tr = RenglonDatoGralHTML(a.getArea(), a.getFolio(), a.getReferencia(), a.getNombreRemitente(), a.getAreaRemitente(), a.getFechaAtencion(), a.getCerrado(), a.getDestinatario(), a.getPrioridad(), row);
                    sb.append(tr);
                }
                tr = RenglonDatoGralHTML("FIN", "", "", "", "", "", "", "", "", row + 1);
                sb.append(tr);
            }
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                // log.warn("Error en rollback", ex);
            }
            log.error("Actualizando caso", exc);
            // throw new GestionException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            rs = null;
            conn = null;
        }
        return sb.toString();
    }

    // Esteban Badillo. Fecha: 26/Febrero/2010.
    // Esteban Badillo. Fecha: 07/Abril/2010. Se agrega el parametro "empleado"
    public StringBuffer[] ReportePorEmpleado(String area, String idArea, String empleado, String searea, String fechaIni, String fechaFin, String login) {
        Connection conn = null;
        StringBuffer sb = new StringBuffer();
        // Esteban Badillo.
        StringBuffer[] resultado = new StringBuffer[5];
        // Fecah:
        // 26/Febrero/2010. Se
        // agrega StringBuffer
        // para devolver los
        // Esteban Badillo.
        StringBuffer sbGrafColumnKeys = new StringBuffer();
        // Fecah:
        // 26/Febrero/2010.
        // Se agrega
        // StringBuffer para
        // devolver los
        // valores que leer
        // reporte.
        // Esteban Badillo.
        StringBuffer sbGrafVencidos = new StringBuffer();
        // Fecah:
        // 26/Febrero/2010.
        // Se agrega
        // StringBuffer para
        // devolver los
        // valores que leer
        // la grfica del
        // reporte.
        // Esteban Badillo.
        StringBuffer sbGrafNoVencidos = new StringBuffer();
        // Fecah:
        // 26/Febrero/2010.
        // Se agrega
        // StringBuffer para
        // devolver los
        // valores que leer
        // la  del
        // reporte.
        // Esteban Badillo.
        StringBuffer sbGrafConcluidos = new StringBuffer();
        // Fecah:
        // 26/Febrero/2010.
        // Se agrega
        // StringBuffer para
        // devolver los
        // valores que
        // la  del
        // reporte.
        // ResultSet rs = null;
        // String tblAll = "";
        String param = // Esteban
        "idRptMaestro=" + GestionInterface.RPT_POR_EMPLEADO + "&area=" + area + "&idarea=" + idArea + "&empleado=" + empleado + // Badillo.
        // Fecha:
        // 07/Abril/2010.
        // Agregado.
        "&searea=" + searea + "&fechaini=" + fechaIni + "&fechafin=" + fechaFin + "&rn=ReporteEstadisticoPorEmpleado.jasper";
        try {
            conn = getConnection();
            String whereFecha = "";
            String andLogin = "";
            String andArea = "";
            if (login != "")
                andLogin = "AND ce_os_responsable = '" + login + "'";
            if (idArea != "")
                andArea = "AND id_area = " + idArea;
            if (fechaIni != "" & fechaFin != "")
                whereFecha = " AND CONVERT(DATETIME, imx.DPC_F_REGISTRO, 103) BETWEEN CONVERT(DATETIME, '" + fechaIni + "', 103) AND CONVERT(DATETIME, '" + fechaFin + "', 103) ";
            String sql = " " + "SELECT " + "ce_os_responsable as responsable_id, " + "ISNULL(ce_ap_paterno,'') + ' ' + ISNULL(ce_ap_materno,'') + ' ' + ISNULL(ce_nombre_completo,'') as nombre_resp, " + "id_area responsable_area, " + "(select count(*) from dbo.CG_CASO_OPERACION AS co WITH(NOLOCK) LEFT OUTER JOIN  " + "	 dbo.CG_BITACORA_OPERACION AS bo WITH(NOLOCK) ON bo.SECUENCIAL_OPERACION = co.ID_CASO_OPER AND bo.ID_CASO = co.ID_CASO  " + "  INNER JOIN dbo.IMXEXPEDIENTES AS imx WITH(NOLOCK)ON imx.ID_GABINETE = bo.ID_GABINETE " + "where  (bo.folio NOT LIKE 'TMP-%') and bo.id_operacion IN (2,3,10,9,11) " + "    and bo.secuencial_operacion IN (	 " + "			select MAX(maxSO.secuencial_operacion) FROM CG_BITACORA_OPERACION maxSO  " + "			WHERE maxSO.folio = bo.folio  " + "				and maxSO.id_operacion in (2,3,9,10,11) " + "				and bo.responsable_id = maxSO.responsable_id  " + "				and bo.terminada = maxSO.terminada)   " + "	and bo.TERMINADA = 'N' and co.user03 < GETDATE() " + "	and bo.responsable_id = ce_os_responsable " + whereFecha + " ) as pendientesVencidos, " + "(select count(*) from dbo.CG_CASO_OPERACION AS co WITH(NOLOCK) LEFT OUTER JOIN  " + "	 dbo.CG_BITACORA_OPERACION AS bo WITH(NOLOCK) ON bo.SECUENCIAL_OPERACION = co.ID_CASO_OPER AND bo.ID_CASO = co.ID_CASO  " + "  INNER JOIN dbo.IMXEXPEDIENTES AS imx WITH(NOLOCK)ON imx.ID_GABINETE = bo.ID_GABINETE " + "where  (bo.folio NOT LIKE 'TMP-%') and bo.id_operacion IN (2,3,10,9,11) " + "    and bo.secuencial_operacion IN (	 " + "			select MAX(maxSO.secuencial_operacion) FROM CG_BITACORA_OPERACION maxSO  " + "			WHERE maxSO.folio = bo.folio  " + "				and maxSO.id_operacion in (2,3,9,10,11) " + "				and bo.responsable_id = maxSO.responsable_id  " + "				and bo.terminada = maxSO.terminada)  " + "	and bo.TERMINADA = 'N' and co.user03 > GETDATE() " + "	and bo.responsable_id = ce_os_responsable " + whereFecha + " ) as pendientesNoVencidos, " + "(select count(*) from dbo.CG_CASO_OPERACION AS co WITH(NOLOCK) LEFT OUTER JOIN  " + "	 dbo.CG_BITACORA_OPERACION AS bo WITH(NOLOCK) ON bo.SECUENCIAL_OPERACION = co.ID_CASO_OPER AND bo.ID_CASO = co.ID_CASO  " + "  INNER JOIN dbo.IMXEXPEDIENTES AS imx WITH(NOLOCK)ON imx.ID_GABINETE = bo.ID_GABINETE " + "where  (bo.folio NOT LIKE 'TMP-%') and bo.id_operacion IN (2,3,10,9,11) " + "    and bo.secuencial_operacion IN (	 " + "			select MAX(maxSO.secuencial_operacion) FROM CG_BITACORA_OPERACION maxSO  " + "			WHERE maxSO.folio = bo.folio  " + "				and maxSO.id_operacion in (2,3,9,10,11) " + "				and bo.responsable_id = maxSO.responsable_id  " + "				and bo.terminada = maxSO.terminada)  " + "	and bo.TERMINADA = 'S'  " + "	and bo.responsable_id = ce_os_responsable " + whereFecha + " ) as cerrado " + "FROM dbo.CG_CAT_EMPLEADO " + "WHERE 1=1 " + " " + andArea + " " + andLogin + " ORDER by ce_ap_paterno,ce_ap_materno,ce_nombre_completo ";
            String nombreProcedure = "";
            if (fechaIni != "" && fechaFin != "" && login != "")
                nombreProcedure = "ReporteEmpleado_individual '" + login + "','" + fechaIni + "','" + fechaFin + "',0";
            else if (login != "")
                nombreProcedure = "ReporteEmpleado_individual '" + login + "',null,null,0";
            else if (login == "" && idArea != "") {
                if (fechaIni != "" && fechaFin != "")
                    nombreProcedure = "ReporteEmpleado_Area " + idArea + ",'" + fechaIni + "','" + fechaFin + "'";
                else if (fechaIni == "" && fechaFin == "")
                    nombreProcedure = "ReporteEmpleado_Area " + idArea + ",null,null";
                else
                    nombreProcedure = "ReporteEmpleado_Area " + idArea + ",null,null";
            }
            // sql = "exec " + nombreProcedure;
            setNombreProcedure(nombreProcedure);
            System.out.println("Query Empleado (Resumen):\n" + sql);
            List<?> acum = new ArrayList<Object>();
            acum = ReporteManager.select(conn, sql, "responsable_id", "nombre_resp");
            sb.append(sql + "|");
            // Reporte por Empleado
            String param_tot = "";
            String tr = "";
            int totalVencidos = 0;
            int totalNoVencidos = 0;
            int totalPendientes = 0;
            int totalConcluidos = 0;
            int totalCasos = 0;
            int row = 0;
            if (acum.isEmpty()) {
                tr = "<tr class=\"alternateRow\">" + "	<td colspan=\"9\"><i>No hay informaci&oacute;n para mostrar</i></td>" + "</tr>";
                sb.append(tr);
            } else {
                String grafColumnKeys = "";
                String grafVencidos = "";
                String grafNoVencidos = "";
                String grafConcluidos = "";
                for (Iterator<?> iter = acum.iterator(); iter.hasNext(); row++) {
                    Acumulado a = (Acumulado) iter.next();
                    totalVencidos += a.getVencidosNumero();
                    totalNoVencidos += a.getNovencidosNumero();
                    totalPendientes += a.getPendientes();
                    totalConcluidos += a.getConcluidos();
                    totalCasos += a.getTotalTurnos();
                    tr = RenglonDatoHTML(GestionInterface.RPT_POR_EMPLEADO_DETALLE, a.getDescripcion(), a.getVencidosNumero(), a.getNovencidosNumero(), a.getPendientes(), a.getConcluidos(), a.getTotalTurnos(), row, param + "&login=" + a.getId());
                    param_tot = param + "&login=" + a.getId();
                    sb.append(tr);
                    if (grafColumnKeys.equals("") && grafVencidos.equals("") && grafNoVencidos.equals("") && grafConcluidos.equals("")) {
                        grafColumnKeys += a.getDescripcion();
                        grafVencidos += a.getVencidosNumero();
                        grafNoVencidos += a.getNovencidosNumero();
                        grafConcluidos += a.getConcluidos();
                    } else {
                        grafColumnKeys += "," + a.getDescripcion();
                        grafVencidos += "," + a.getVencidosNumero();
                        grafNoVencidos += "," + a.getNovencidosNumero();
                        grafConcluidos += "," + a.getConcluidos();
                    }
                }
                // tblAll = RenglonHeader(GestionInterface.RPT_POR_EMPLEADO);
                if (idArea.equals("")) {
                    tr = RenglonDatoHTML(GestionInterface.RPT_POR_EMPLEADO_DETALLE, "TOTALES", totalVencidos, totalNoVencidos, totalPendientes, totalConcluidos, totalCasos, row + 1, (row == 1 ? param_tot : param));
                } else {
                    tr = RenglonDatoHTMLSoloTotalArea(GestionInterface.RPT_POR_EMPLEADO_DETALLE, "TOTALES", totalVencidos, totalNoVencidos, totalPendientes, totalConcluidos, totalCasos, row + 1, (row == 1 ? param_tot : param));
                }
                // DuBois se comento por que no se quieren los totales - 29 Dic
                // 2009
                // sb.append(tr);
                // tblAll.replaceAll("*", sb.toString());
                sbGrafColumnKeys.append(grafColumnKeys);
                sbGrafVencidos.append(grafVencidos);
                sbGrafNoVencidos.append(grafNoVencidos);
                sbGrafConcluidos.append(grafConcluidos);
            }
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                // log.warn("Error en rollback", ex);
            }
            log.error("Actualizando caso", exc);
            // throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        // Grafica
        // return sb.toString();
        resultado[0] = sb;
        resultado[1] = sbGrafColumnKeys;
        resultado[2] = sbGrafVencidos;
        resultado[3] = sbGrafNoVencidos;
        resultado[4] = sbGrafConcluidos;
        return resultado;
    }

    public void setNombreProcedure(String valor) {
        NombreReporte = valor;
    }

    public String getNombreProcedure() {
        return NombreReporte;
    }

    public String ReportePorEmpleadoDetalle(String area, String idArea, String fechaini, String fechafin, String login, int tipoacumulado) {
        Connection conn = null;
        StringBuffer sb = new StringBuffer();
        ResultSet rs = null;
        try {
            conn = getConnection();
            // Reporte por area
            String whereFecha = "";
            String andLogin = "";
            // String andArea = "";
            if (login != "")
                andLogin = "AND ce_os_responsable = '" + login + "' ";
            // if(idArea!="")
            // andArea = "AND id_area = " + idArea + " ";
            if (fechaini != "" & fechafin != "")
                whereFecha = " AND CONVERT(DATETIME, imx.DPC_F_REGISTRO, 103) BETWEEN CONVERT(DATETIME, '" + fechaini + "', 103) AND CONVERT(DATETIME, '" + fechafin + "', 103) ";
            String whereBase = "";
            // int tipoConsulta=tipoacumulado;
            switch(tipoacumulado) {
                case GestionInterface.RPT_CNS_VENCIDOS:
                    whereBase = " AND bo.TERMINADA = 'N' AND co.USER03 < GETDATE() ";
                    // tipoConsulta=1;
                    break;
                case GestionInterface.RPT_CNS_NOVENCIDOS:
                    whereBase = " AND bo.TERMINADA = 'N' AND co.USER03 > GETDATE() ";
                    // tipoConsulta=2;
                    break;
                case GestionInterface.RPT_CNS_PENDIENTES:
                    whereBase = " AND bo.TERMINADA = 'N' ";
                    // tipoConsulta=3;
                    break;
                case GestionInterface.RPT_CNS_CONCLUIDOS:
                    whereBase = " AND bo.TERMINADA = 'S' ";
                    // tipoConsulta=4;
                    break;
                case GestionInterface.RPT_CNS_CASOS:
            }
            String sql = "" + " SELECT bo.folio " + " ,imx.REFERENCIA " + " ,convert(varchar(20), case when bo.id_operacion = 2 then bo.FECHA_INICIO else dbo.FnRptGenResFechaEnvio(bo.ID_CASO, bo.SECUENCIAL_OPERACION) end, 103) as fecha_envio " + " ,CASE WHEN imx.prioridad = 'N' THEN 'NORMAL' ELSE 'URGENTE' END  as prioridad " + " ,imx.dpc_f_registro as fecha_Registro " + " ,CASE WHEN bo.terminada = 'S' THEN 'CONCLUIDOS' ELSE  " + " CASE  " + " 	WHEN co.user03 ='' THEN 'NO VENCIDOS'  " + " 	WHEN co.user03 =' 23:59:59' THEN 'NO VENCIDOS'  " + " 	WHEN co.user03 > GETDATE() THEN 'NO VENCIDOS' " + " 	WHEN co.user03 < GETDATE() THEN 'VENCIDOS' " + " END    " + " END as CERRADO " + " ,ca1.d_descripcion AS desc_area_rem " + " ,ISNULL(ce1.ce_ap_paterno,'') + ' ' + ISNULL(ce1.ce_ap_materno,'') + ' ' + ISNULL(ce1.ce_nombre_completo,'') as nombre_rem " + " ,imx.dpc_f_limite AS fechalimite " + " ,bo.TERMINADA " + " ,substring(convert(varchar, CASE WHEN co.user03 NOT LIKE '%__/__/____%' OR co.USER03 IS NULL THEN CONVERT(VARCHAR, dbo.FnRptGenResFechaLimite(bo.ID_CASO, bo.SECUENCIAL_OPERACION), 103) ELSE co.user03 END, 103),0,12) AS USER03 " + // + " ,co.USER03 "
            " ,co.CO_OBSERVACION AS ASUNTO " + " FROM          " + " dbo.CG_CASO_OPERACION AS co WITH(NOLOCK)  " + " LEFT OUTER JOIN dbo.CG_BITACORA_OPERACION AS bo WITH(NOLOCK) ON bo.SECUENCIAL_OPERACION = co.ID_CASO_OPER AND bo.ID_CASO = co.ID_CASO  " + " INNER JOIN  dbo.IMXEXPEDIENTES AS imx WITH(NOLOCK)ON imx.ID_GABINETE = bo.ID_GABINETE " + " INNER JOIN dbo.CG_CAT_EMPLEADO AS ce1 WITH(NOLOCK) ON ce1.CE_OS_RESPONSABLE = bo.responsable_id " + " INNER JOIN dbo.CG_CAT_AREAS AS ca1 WITH(NOLOCK) ON ca1.ID_AREA = bo.remitente_area  " + " WHERE	bo.folio NOT LIKE 'TMP-%'  " + " AND bo.id_operacion in (2,3,9,10,11) " + andLogin + // + andArea
            whereFecha + whereBase + " AND bo.secuencial_operacion = ( " + " 	SELECT 	MAX(s2bo.secuencial_operacion) FROM 	dbo.CG_BITACORA_OPERACION  s2bo WITH(NOLOCK)  " + " 	WHERE s2bo.id_operacion  in (2,3,9,10,11) " + "        AND bo.responsable_id = s2bo.responsable_id " + "		  AND bo.Terminada = s2bo.Terminada " + "        AND bo.folio = s2bo.folio " + "   ) " + " order by bo.folio ";
            List<?> gral = new ArrayList<Object>();
            /*
			 * if(login!="" && fechaini=="" & fechafin=="")
			 * sql="exec ReporteDetallado_Cerrados '"
			 * +login+"',-1,"+tipoConsulta+",null,null"; if(login!="" &&
			 * fechaini!="" & fechafin!="")
			 * sql="exec ReporteDetallado_Cerrados '"
			 * +login+"',-1,"+tipoConsulta+",'"+fechaini +"','" +fechafin +"'";
			 * 
			 * if(idArea!="" && fechaini=="" & fechafin=="")
			 * sql="exec ReporteDetallado_Cerrados '"
			 * +login+"',"+idArea+","+tipoConsulta+",null,null"; if(idArea!=""
			 * && fechaini!="" & fechafin!="")
			 * sql="exec ReporteDetallado_Cerrados '"
			 * +login+"',"+idArea+","+tipoConsulta+",'"+fechaini +"','"
			 * +fechafin +"'";
			 */
            System.out.println("---SQL del detalle=" + sql);
            gral = ReporteManager.selectAll(conn, sql, 2);
            // Reporte por Empleado
            String tr = "";
            if (gral.isEmpty()) {
                tr = "<tr class=\"alternateRow\">" + "	<td colspan=\"10\"><h3>No hay informaci&oacute;n para mostrar</h3></td>" + "</tr>";
                sb.append(tr);
            } else {
                int row = 0;
                for (Iterator<?> iter = gral.iterator(); iter.hasNext(); row++) {
                    General a = (General) iter.next();
                    tr = RenglonDatoGralHTML(a.getArea(), a.getFolio(), a.getReferencia(), a.getNombreRemitente(), a.getAreaRemitente(), a.getFechaEnvio(), a.getFechaAtencion(), a.getCerrado(), a.getDestinatario(), a.getPrioridad(), a.getAsunto(), row);
                    sb.append(tr);
                }
                tr = RenglonDatoGralHTML("FIN", "", "", "", "", "", "", "", "", "", "", row + 1);
                sb.append(tr);
            }
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                // log.warn("Error en rollback", ex);
            }
            log.error("Actualizando caso", exc);
            // throw new GestionException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            rs = null;
            conn = null;
        }
        return sb.toString();
    }

    public StringBuffer ReporteEmpleadoDetalle(String area, String idArea, String fechaini, String fechafin, String login, int tipoacumulado) {
        Connection conn = null;
        StringBuffer sb = new StringBuffer();
        ResultSet rs = null;
        try {
            conn = getConnection();
            // Reporte por area
            String whereFecha = "";
            String andLogin = "";
            // String andArea = "";
            if (login != "")
                andLogin = "AND ce_os_responsable = '" + login + "' ";
            // if(idArea!="")
            // andArea = "AND id_area = " + idArea + " ";
            if (fechaini != "" & fechafin != "")
                whereFecha = " AND CONVERT(DATETIME, imx.DPC_F_REGISTRO, 103) BETWEEN CONVERT(DATETIME, '" + fechaini + "', 103) AND CONVERT(DATETIME, '" + fechafin + "', 103) ";
            String whereBase = "";
            // int tipoConsulta=tipoacumulado;
            switch(tipoacumulado) {
                case GestionInterface.RPT_CNS_VENCIDOS:
                    whereBase = " AND bo.TERMINADA = 'N' AND co.USER03 < GETDATE() ";
                    // tipoConsulta=1;
                    break;
                case GestionInterface.RPT_CNS_NOVENCIDOS:
                    whereBase = " AND bo.TERMINADA = 'N' AND co.USER03 > GETDATE() ";
                    // tipoConsulta=2;
                    break;
                case GestionInterface.RPT_CNS_PENDIENTES:
                    whereBase = " AND bo.TERMINADA = 'N' ";
                    // tipoConsulta=3;
                    break;
                case GestionInterface.RPT_CNS_CONCLUIDOS:
                    whereBase = " AND bo.TERMINADA = 'S' ";
                    // tipoConsulta=4;
                    break;
                case GestionInterface.RPT_CNS_CASOS:
            }
            String sql = "" + " SELECT bo.folio " + " ,imx.REFERENCIA " + " ,convert(varchar(20), case when bo.id_operacion = 2 then bo.FECHA_INICIO else dbo.FnRptGenResFechaEnvio(bo.ID_CASO, bo.SECUENCIAL_OPERACION) end, 103) as fecha_envio " + " ,CASE WHEN imx.prioridad = 'N' THEN 'NORMAL' ELSE 'URGENTE' END  as prioridad " + " ,imx.dpc_f_registro as fecha_Registro " + " ,CASE WHEN bo.terminada = 'S' THEN 'CONCLUIDOS' ELSE  " + " CASE  " + " 	WHEN co.user03 ='' THEN 'NO VENCIDOS'  " + " 	WHEN co.user03 =' 23:59:59' THEN 'NO VENCIDOS'  " + " 	WHEN co.user03 > GETDATE() THEN 'NO VENCIDOS' " + " 	WHEN co.user03 < GETDATE() THEN 'VENCIDOS' " + " END    " + " END as CERRADO " + " ,ca1.d_descripcion AS desc_area_rem " + " ,ISNULL(ce1.ce_ap_paterno,'') + ' ' + ISNULL(ce1.ce_ap_materno,'') + ' ' + ISNULL(ce1.ce_nombre_completo,'') as nombre_rem " + " ,imx.dpc_f_limite AS fechalimite " + " ,bo.TERMINADA " + " ,substring(convert(varchar, CASE WHEN co.user03 NOT LIKE '%__/__/____%' OR co.USER03 IS NULL THEN CONVERT(VARCHAR, dbo.FnRptGenResFechaLimite(bo.ID_CASO, bo.SECUENCIAL_OPERACION), 103) ELSE co.user03 END, 103),0,12) AS USER03 " + // + " ,co.USER03 "
            " ,co.CO_OBSERVACION AS ASUNTO " + " FROM          " + " dbo.CG_CASO_OPERACION AS co WITH(NOLOCK)  " + " LEFT OUTER JOIN dbo.CG_BITACORA_OPERACION AS bo WITH(NOLOCK) ON bo.SECUENCIAL_OPERACION = co.ID_CASO_OPER AND bo.ID_CASO = co.ID_CASO  " + " INNER JOIN  dbo.IMXEXPEDIENTES AS imx WITH(NOLOCK)ON imx.ID_GABINETE = bo.ID_GABINETE " + " INNER JOIN dbo.CG_CAT_EMPLEADO AS ce1 WITH(NOLOCK) ON ce1.CE_OS_RESPONSABLE = bo.responsable_id " + " INNER JOIN dbo.CG_CAT_AREAS AS ca1 WITH(NOLOCK) ON ca1.ID_AREA = bo.remitente_area  " + " WHERE	bo.folio NOT LIKE 'TMP-%'  " + " AND bo.id_operacion in (2,3,9,10,11) " + andLogin + // + andArea
            whereFecha + whereBase + " AND bo.secuencial_operacion = ( " + " 	SELECT 	MAX(s2bo.secuencial_operacion) FROM 	dbo.CG_BITACORA_OPERACION  s2bo WITH(NOLOCK)  " + " 	WHERE s2bo.id_operacion  in (2,3,9,10,11) " + "        AND bo.responsable_id = s2bo.responsable_id " + "		  AND bo.Terminada = s2bo.Terminada " + "        AND bo.folio = s2bo.folio " + "   ) " + " order by bo.folio ";
            List<?> gral = new ArrayList<Object>();
            /*
			 * if(login!="" && fechaini=="" & fechafin=="")
			 * sql="exec ReporteDetallado_Cerrados '"
			 * +login+"',-1,"+tipoConsulta+",null,null"; if(login!="" &&
			 * fechaini!="" & fechafin!="")
			 * sql="exec ReporteDetallado_Cerrados '"
			 * +login+"',-1,"+tipoConsulta+",'"+fechaini +"','" +fechafin +"'";
			 * 
			 * if(idArea!="" && fechaini=="" & fechafin=="")
			 * sql="exec ReporteDetallado_Cerrados '"
			 * +login+"',"+idArea+","+tipoConsulta+",null,null"; if(idArea!=""
			 * && fechaini!="" & fechafin!="")
			 * sql="exec ReporteDetallado_Cerrados '"
			 * +login+"',"+idArea+","+tipoConsulta+",'"+fechaini +"','"
			 * +fechafin +"'";
			 */
            System.out.println("---SQL del detalle=" + sql);
            gral = ReporteManager.selectAll(conn, sql, 2);
            // Reporte por Empleado
            String tr = "";
            if (gral.isEmpty()) {
                tr = "<tr class=\"alternateRow\">" + "	<td colspan=\"10\"><h3>No hay informaci&oacute;n para mostrar</h3></td>" + "</tr>";
                sb.append(tr);
            } else {
                int row = 0;
                for (Iterator<?> iter = gral.iterator(); iter.hasNext(); row++) {
                    General a = (General) iter.next();
                    tr = RenglonDatoGralHTML(a.getArea(), a.getFolio(), a.getReferencia(), a.getNombreRemitente(), a.getAreaRemitente(), a.getFechaEnvio(), a.getFechaAtencion(), a.getCerrado(), a.getDestinatario(), a.getPrioridad(), a.getAsunto(), row);
                    sb.append(tr);
                }
                tr = RenglonDatoGralHTML("FIN", "", "", "", "", "", "", "", "", "", "", row + 1);
                sb.append(tr);
            }
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                // log.warn("Error en rollback", ex);
            }
            log.error("Actualizando caso", exc);
            // throw new GestionException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            rs = null;
            conn = null;
        }
        return sb;
    }

    public String RenglonHeader(int idReporte) {
        String tblHeader = "";
        switch(idReporte) {
            case GestionInterface.RPT_GENERAL:
                tblHeader = "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"scrollTable\">" + "	<thead class=\"fixedHeader\" id=\"fixedHeader\">" + "		<tr>" + "			<th>&Aacute;REA</th>" + "			<th>FOLIO</th>" + "			<th>REFERENCIA</th>" + "			<th>NOMBRE DEL REMITENTE</th>" + "			<th>&Aacute;REA DEL REMITENTE</th>" + "			<th>FECHA LIMITE</th>" + "			<th>ESTATUS</th>" + "			<th>TURNADO A</th>" + "			<th>PRIORIDAD</th>" + "		</tr>" + "	</thead>" + "	<tbody class=\"scrollContent\">" + "		*" + "   </tbody>" + "</table>";
                break;
            case GestionInterface.RPT_CONSOLIDADO:
                tblHeader = "<table align=\"center\" cellpadding=\"0\" cellspacing=\"1\" border=\"0\" width=\"80%\" class=\"scrollTable\">" + "   <thead class=\"fixedHeader\" id=\"fixedHeader\">" + "       <tr>" + "           <td></td>" + "           <td id=\"idTh\" colspan=\"5\">PENDIENTES</td>" + "           <td></td>" + "           <td></td>" + "           <td></td>" + "       </tr>" + "       <tr>" + "           <td id=\"idTh\">&Aacute;REA</td>" + "           <td id=\"idTh\">VENCIDOS</td>" + "           <td id=\"idTh\">%</td>" + "           <td id=\"idTh\">NO VENCIDOS</td>" + "           <td id=\"idTh\">%</td>" + "           <td id=\"idTh\">TOTAL</td>" + "           <td id=\"idTh\">CONCLUIDOS</td>" + "           <td id=\"idTh\">%</td>" + "           <td id=\"idTh\">TOTAL</td>" + "	    </tr>" + "   </thead>" + "   <tbody class=\"scrollContent\">" + "   	*" + "   </tbody>" + "</table>";
                break;
            case GestionInterface.RPT_POR_AREA:
                tblHeader = "<table align=\"center\" cellpadding=\"0\" cellspacing=\"1\" border=\"0\" width=\"80%\" class=\"scrollTable\">" + "   <thead class=\"fixedHeader\" id=\"fixedHeader\">" + "	    <tr>" + "		    <td></td>" + "		    <td id=\"idTh\" colspan=\"5\">PENDIENTES</td>" + "		    <td></td>" + "		    <td></td>" + "		    <td></td>" + "	    </tr>" + "	    <tr>" + "		    <td id=\"idTh\">&Aacute;REA</td>" + "		    <td id=\"idTh\">VENCIDOS</td>" + "		    <td id=\"idTh\">%</td>" + "		    <td id=\"idTh\">NO VENCIDOS</td>" + "		    <td id=\"idTh\">%</td>" + "		    <td id=\"idTh\">TOTAL</td>" + "		    <td id=\"idTh\">CONCLUIDOS</td>" + "		    <td id=\"idTh\">%</td>" + "		    <td id=\"idTh\">TOTAL</td>" + "	    </tr>" + "   </thead>" + "   <tbody class=\"scrollContent\">" + "	    {rpt_bdy} " + "   </tbody>" + "</table>";
                break;
            case GestionInterface.RPT_POR_EMPLEADO:
                tblHeader = "<table align=\"center\" cellpadding=\"0\" cellspacing=\"1\" border=\"0\" width=\"80%\" class=\"scrollTable\">" + "   <thead class=\"fixedHeader\" id=\"fixedHeader\">" + "	<tr>" + "		<td></td>" + "		<td id=\"idTh\" colspan=\"5\">PENDIENTES</td>" + "		<td></td>" + "		<td></td>" + "		<td></td>" + "	</tr>" + "	<tr>" + "		<td id=\"idTh\">FUNCIONARIO</td>" + "	    <td id=\"idTh\">VENCIDOS</td>" + "		<td id=\"idTh\">%</td>" + "		<td id=\"idTh\">NO VENCIDOS</td>" + "		<td id=\"idTh\">%</td>" + "		<td id=\"idTh\">TOTAL</td>" + "		<td id=\"idTh\">CONCLUIDOS</td>" + "		<td id=\"idTh\">%</td>" + "		<td id=\"idTh\">TOTAL</td>" + "	</tr>" + "   </thead>" + "   <tbody class=\"scrollContent\">" + "	    *" + "   </tbody>" + "</table>";
                break;
            case GestionInterface.RPT_DETALLADO:
                break;
        }
        return tblHeader;
    }

    private String RenglonDatoHTML(int idReporte, String desc, int vencidos, int novencidos, int pendientes, int cerrado, int casos, int row, String param) {
        String[] tipoCSS = { "idTh", "idTdTexto", "idTdNumero" };
        double porcentaje_vencidos = (casos > 0 ? ((new Double(vencidos).doubleValue() / new Double(casos).doubleValue())) : 0);
        double porcentaje_novencidos = (casos > 0 ? (new Double(novencidos).doubleValue() / new Double(casos).doubleValue()) : 0);
        double porcentaje_cerrados = (casos > 0 ? (new Double(cerrado).doubleValue() / new Double(casos).doubleValue()) : 0);
        boolean total = "TOTALES".equals(desc);
        String renglon = "<tr class=" + ((row % 2) == 0 ? "AlternateRow" : "NormalRow") + "> " + "	    <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[1]) + "\">" + ((total) ? "" : "" + (row + 1)) + "</td>" + "	    <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[1]) + "\">" + desc + "</td>" + "		<td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + getAncla(idReporte, GestionInterface.RPT_CNS_VENCIDOS, vencidos, param) + "</td>" + "		<td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">&nbsp;" + Math.round(porcentaje_vencidos * 100) + "%&nbsp;</td>" + "		<td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + getAncla(idReporte, GestionInterface.RPT_CNS_NOVENCIDOS, novencidos, param) + "</td>" + "		<td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">&nbsp;" + Math.round(porcentaje_novencidos * 100) + "%&nbsp;</td>" + "		<td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + getAncla(idReporte, GestionInterface.RPT_CNS_PENDIENTES, pendientes, param) + "</td>" + "		<td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + getAncla(idReporte, GestionInterface.RPT_CNS_CONCLUIDOS, cerrado, param) + "</td>" + "		<td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">&nbsp;" + Math.round(porcentaje_cerrados * 100) + "%&nbsp;</td>" + "		<td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + getAncla(idReporte, GestionInterface.RPT_CNS_CASOS, casos, param) + "</td>" + "	</tr>";
        return renglon;
    }

    private String RenglonDatoHTMLSoloTotalArea(int idReporte, String desc, int vencidos, int novencidos, int pendientes, int cerrado, int casos, int row, String param) {
        String[] tipoCSS = { "idTh", "idTdTexto", "idTdNumero" };
        double porcentaje_vencidos = (casos > 0 ? ((new Double(vencidos).doubleValue() / new Double(casos).doubleValue())) : 0);
        double porcentaje_novencidos = (casos > 0 ? (new Double(novencidos).doubleValue() / new Double(casos).doubleValue()) : 0);
        double porcentaje_cerrados = (casos > 0 ? (new Double(cerrado).doubleValue() / new Double(casos).doubleValue()) : 0);
        boolean total = "TOTALES".equals(desc);
        String renglon = "<tr class=" + ((row % 2) == 0 ? "AlternateRow" : "NormalRow") + "> " + "	    <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[1]) + "\">" + ((total) ? "" : "" + (row + 1)) + "</td>" + "	    <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[1]) + "\">" + desc + "</td>" + "		<td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + getAncla(idReporte, GestionInterface.RPT_CNS_VENCIDOS * 10, vencidos, param) + "</td>" + "		<td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">&nbsp;" + Math.round(porcentaje_vencidos * 100) + "%&nbsp;</td>" + "		<td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + getAncla(idReporte, GestionInterface.RPT_CNS_NOVENCIDOS * 10, novencidos, param) + "</td>" + "		<td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">&nbsp;" + Math.round(porcentaje_novencidos * 100) + "%&nbsp;</td>" + "		<td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + getAncla(idReporte, GestionInterface.RPT_CNS_PENDIENTES * 10, pendientes, param) + "</td>" + "		<td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + getAncla(idReporte, GestionInterface.RPT_CNS_CONCLUIDOS * 10, cerrado, param) + "</td>" + "		<td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">&nbsp;" + Math.round(porcentaje_cerrados * 100) + "%&nbsp;</td>" + "		<td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + getAncla(idReporte, GestionInterface.RPT_CNS_CASOS * 10, casos, param) + "</td>" + "	</tr>";
        return renglon;
    }

    private String getAncla(int idReporte, int tipo_acumulado, int valor, String param) {
        if (valor == 0)
            return "0";
        else
            return "<a href=\"javascript:openDetalle(" + idReporte + "," + tipo_acumulado + ",'" + valor + "','" + param + "');\">" + valor + "</a>";
    }

    private String RenglonDatoGralHTML(String folio, String referencia, String remitente_area_desc, String remitente_area, String remitente_nombre, String cerrado, String tipo_instruccion, String prioridad, String asunto, String responsable_nombre, String responsable_area_desc, String fecha_registro, /*
		 * Esteban Badillo. Fecha: 10/Sep/2009 Descripcion: Se agregan los
		 * campos de fecha_envio y fecha_limite requeridos para los cambios
		 * solicitados al Reporte General.
		 */
    String fecha_envio, String fecha_limite, int row, /*
		 * Esteban Badillo. Fecha: 10/Sep/2009 Descripcion: Se agregan el campo
		 * condicional "orden" para proyectar los campos "Recibidos de" /
		 * "Enviados a" del Reporte General
		 */
    String orden) {
        String[] tipoCSS = { "idTdConsecutivo", "idTh", "idTdTexto", "idTdNumero" };
        boolean total = "FIN".equals(folio);
        /*
		 * Esteban Badillo. Fecha: 11/Sep/2009 Descripcion: Se cambia la
		 */
        /*
		 * String renglon = "<tr class=" + ((row % 2) == 0 ? "AlternateRow" :
		 * "NormalRow") + "> " + "	  <td id=\"" + ((total)? tipoCSS[1]:
		 * tipoCSS[0]) + "\">" + ((total)? "":(row + 1)) + "</td>" +
		 * "	  <td id=\"" + ((total)? tipoCSS[1]: tipoCSS[2]) + "\" nowrap>" +
		 * folio + "</td>" + "	  <td id=\"" + ((total)? tipoCSS[1]: tipoCSS[2])
		 * + "\" nowrap>" + referencia + "</td>" + "	  <td id=\"" + ((total)?
		 * tipoCSS[1]: tipoCSS[2]) + "\">" + remitente_area_desc + "</td>" //+
		 * "	  <td id=\"" + ((total)? tipoCSS[0]: tipoCSS[2]) + "\">" +
		 * remitente_area + "</td>" + "	  <td id=\"" + ((total)? tipoCSS[1]:
		 * tipoCSS[2]) + "\" nowrap>" + remitente_nombre + "</td>" +
		 * "	  <td id=\"" + ((total)? tipoCSS[1]: tipoCSS[2]) + "\">" + cerrado
		 * + "</td>" + "	  <td id=\"" + ((total)? tipoCSS[1]: tipoCSS[2]) +
		 * "\">" + tipo_instruccion + "</td>" + "	  <td id=\"" + ((total)?
		 * tipoCSS[1]: tipoCSS[2]) + "\">" + prioridad + "</td>" +
		 * "	  <td width=\"2000\" id=\"" + ((total)? tipoCSS[1]: tipoCSS[2]) +
		 * "\">" + asunto + "</td>" + "	  <td id=\"" + ((total)? tipoCSS[1]:
		 * tipoCSS[2]) + "\">" + responsable_nombre + "</td>" + "	  <td id=\"" +
		 * ((total)? tipoCSS[1]: tipoCSS[2]) + "\">" + responsable_area_desc +
		 * "</td>" + "	  <td id=\"" + ((total)? tipoCSS[1]: tipoCSS[2]) + "\">"
		 * + fecha_registro + "</td>" + "</tr>";
		 */
        String renglon = "<tr class=" + ((row % 2) == 0 ? "AlternateRow" : "NormalRow") + "> " + "	  <td id=\"" + ((total) ? tipoCSS[1] : tipoCSS[0]) + "\">" + ((total) ? "" : "" + (row + 1)) + "</td>" + "	  <td id=\"" + ((total) ? tipoCSS[1] : tipoCSS[2]) + "\" nowrap>" + folio + "</td>" + "	  <td id=\"" + ((total) ? tipoCSS[1] : tipoCSS[2]) + "\" nowrap>" + referencia + "</td>";
        if (orden.equals("REMITENTE")) {
            renglon += "	  <td id=\"" + ((total) ? tipoCSS[1] : tipoCSS[2]) + "\">" + remitente_area_desc + "</td>" + "	  <td id=\"" + ((total) ? tipoCSS[1] : tipoCSS[2]) + "\" nowrap>" + remitente_nombre + "</td>";
        }
        if (orden.equals("RESPONSABLE")) {
            renglon += "	  <td id=\"" + ((total) ? tipoCSS[1] : tipoCSS[2]) + "\">" + responsable_nombre + "</td>" + "	  <td id=\"" + ((total) ? tipoCSS[1] : tipoCSS[2]) + "\">" + responsable_area_desc + "</td>";
        }
        renglon += "	  <td id=\"" + ((total) ? tipoCSS[1] : tipoCSS[2]) + "\">" + cerrado + "</td>" + "	  <td id=\"" + ((total) ? tipoCSS[1] : tipoCSS[2]) + "\">" + splitFechas(fecha_limite) + "</td>" + "	  <td id=\"" + ((total) ? tipoCSS[1] : tipoCSS[2]) + "\">" + splitFechas(fecha_envio) + "</td>" + "	  <td id=\"" + ((total) ? tipoCSS[1] : tipoCSS[2]) + "\">" + tipo_instruccion + "</td>" + "	  <td id=\"" + ((total) ? tipoCSS[1] : tipoCSS[2]) + "\">" + splitFechas(fecha_registro) + "</td>" + "	  <td id=\"" + ((total) ? tipoCSS[1] : tipoCSS[2]) + "\">" + prioridad + "</td>" + // + "	  <td width=\"2000\" id=\"" + ((total)? tipoCSS[1]:
        // tipoCSS[2]) + "\">" + asunto + "</td>"
        "	  <td id=\"" + ((total) ? tipoCSS[1] : tipoCSS[2]) + "\"><div COLS=100 ROWS=3 style=\"FONT-FAMILY: Verdana,Tahoma,Arial;\">" + asunto + "<div></td>" + "</tr>";
        // System.out.println("Fecha_Envio: " + fecha_envio);
        // System.out.println("Fecha_Limite: " + fecha_limite);
        return renglon;
    }

    protected String splitFechas(String fecha) {
        // String fechaParseada="";
        String aux2 = "";
        if (fecha != null && !fecha.equals("")) {
            String[] aux = fecha.split(" ");
            aux2 = aux[0];
            // String aux3[]= aux2.split("-");
            // fechaParseada = aux3[2]+"/" +aux3[1] + "/" +aux3[0];
        }
        return aux2;
    }

    private String RenglonDatoGralHTML(String area, String folio, String referencia, String remitente_nombre, String remitente_area, String fecha_atencion, String cerrado, String destinatario, String prioridad, int row) {
        String[] tipoCSS = { "idTh", "idTdTexto", "idTdNumero" };
        boolean total = "FIN".equals(area);
        String renglon = "<tr class=" + ((row % 2) == 0 ? "AlternateRow" : "NormalRow") + "> " + "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[1]) + "\">" + ((total) ? "" : "" + (row + 1)) + "</td>" + // + "	  <td id=\"" + ((total)? tipoCSS[0]: tipoCSS[1]) + "\">"
        // + area + "</td>"
        "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[1]) + "\">" + folio + "</td>" + "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + referencia + "</td>" + "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + remitente_nombre + "</td>" + "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + fecha_atencion + "</td>" + "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + cerrado + "</td>" + // + "	  <td id=\"" + ((total)? tipoCSS[0]: tipoCSS[2]) + "\">"
        // + destinatario + "</td>"
        "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + prioridad + "</td>" + "</tr>";
        // System.out.println("renglon="+ renglon);
        return renglon;
    }

    /*
	 * Elaboro: Mauricio Fonseca Rmz. // Fecha: 25 Marzo 2010. // Se agrega este
	 * // Grid del Reporte Estadistico por Empleado para que displiegue los
	 * campos // de fecha de // envio y asunto.
	 */
    private String RenglonDatoGralHTML(String area, String folio, String referencia, String remitente_nombre, String remitente_area, String fecha_envio, String fecha_atencion, String cerrado, String destinatario, String prioridad, String asunto, int row) {
        String[] tipoCSS = { "idTh", "idTdTexto", "idTdNumero" };
        boolean total = "FIN".equals(area);
        String renglon = "<tr class=" + ((row % 2) == 0 ? "AlternateRow" : "NormalRow") + "> " + "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[1]) + "\">" + ((total) ? "" : "" + (row + 1)) + "</td>" + // + "	  <td id=\"" + ((total)? tipoCSS[0]: tipoCSS[1]) + "\">"
        // + area + "</td>"
        "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[1]) + "\">" + folio + "</td>" + "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + referencia + "</td>" + "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + remitente_nombre + "</td>" + "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + remitente_area + "</td>" + "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + fecha_envio + "</td>" + "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + fecha_atencion + "</td>" + "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + cerrado + "</td>" + // + "	  <td id=\"" + ((total)? tipoCSS[0]: tipoCSS[2]) + "\">"
        // + destinatario + "</td>"
        "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + prioridad + "</td>" + "	  <td id=\"" + ((total) ? tipoCSS[0] : tipoCSS[2]) + "\">" + asunto + "</td>" + "</tr>";
        // System.out.println("renglon="+ renglon);
        return renglon;
    }

    /*
	 * //Esteban Badillo. Fecha: 09/Oct/2009 //Metodo que obtiene el campo
	 * private String getEstructuraArea(String usuario) { Connection conn =
	 * null; String respuesta = null; java.sql.PreparedStatement pstmnt = null;
	 * 
	 * ResultSet rs = null; try { conn = getConnection(); //Reporte por area
	 * 
	 * 
	 * 
	 * String sql = ""
	 * 
	 * + "SELECT " + "   e.id_empleado " + "   e.ce_os_responsable, " +
	 * "   a.id_area, " + "   a.d_descripcion, " +
	 * "   a.area_estructura + '.__' as area_estructura, " + "   a.area_nivel "
	 * + "FROM CT_CAT_EMPLEADO e " + "INNER JOIN CT_CAT_AREAS a" +
	 * "ON a.id_area = e.id_area" + "WHERE ce_os_responsable = ?" ;
	 * 
	 * pstmnt = conn.prepareStatement(sql); pstmnt.setString(1, usuario);
	 * 
	 * rs = pstmnt.executeQuery(); respuesta = rs.getString("area_estructura");
	 * } catch (SQLException exc) { try { conn.rollback(); } catch (SQLException
	 * ex) { // log.warn("Error en rollback", ex); }
	 * 
	 * log.error("Actualizando caso", exc); //throw new GestionException(exc); }
	 * finally { try { if (rs != null) rs.close();
	 * 
	 * if (conn != null) conn.close();
	 * 
	 * } catch (SQLException exc) {
	 * log.warn("Cerrando conexion a base de datos", exc); }
	 * 
	 * rs = null; conn = null; }
	 * 
	 * return respuesta; }
	 */
    /*
	 * Esteban Badillo. Fecha: 12/Oct/2009 Descripcion: Se agrega la sobrecarga
	 * del metodo ReportePorArea, incluyendo el parametro que indica el tipo de
	 * pantalla de consulta del Reporte Estadístico por Area.
	 */
    public StringBuffer[] ReportePorArea(String area, String estructura, String regfechaini, String regfechafin, boolean detallado, String tipo_filtro, boolean areasHijas, int idArea) {
        Connection conn = null;
        // Esteban Badillo.
        StringBuffer[] resultado = new StringBuffer[5];
        // Fecha:
        // 26/Febrero/2010. El
        // metodo ahora
        // de dos StringBuffers,
        // en lugar de uno solo
        // como lo hacia antes.
        StringBuffer sb = new StringBuffer();
        // Esteban Badillo.
        StringBuffer sbGrafColumnKeys = new StringBuffer();
        // Fecah:
        // 26/Febrero/2010.
        // Se agrega
        // StringBuffer para
        // devolver los
        // valores que
        // la  del
        // reporte.
        // Esteban Badillo.
        StringBuffer sbGrafVencidos = new StringBuffer();
        // Fecah:
        // 26/Febrero/2010.
        // Se agrega
        // StringBuffer para
        // devolver los
        // valores que
        // la  del
        // reporte.
        // Esteban Badillo.
        StringBuffer sbGrafNoVencidos = new StringBuffer();
        // Fecah:
        // 26/Febrero/2010.
        // Se agrega
        // StringBuffer para
        // devolver los
        // valores que
        // la  del
        // reporte.
        // Esteban Badillo.
        StringBuffer sbGrafConcluidos = new StringBuffer();
        // Fecah:
        // 26/Febrero/2010.
        // Se agrega
        // StringBuffer para
        // devolver los
        // valores que
        // la  del
        // reporte.
        ResultSet rs = null;
        String param = "idRptMaestro=4" + "&idAreaPadre=" + idArea + "&estruc=" + estructura + "&fechaini=" + regfechaini + "&fechafin=" + regfechafin + "&detallado=" + detallado + "&area=" + area;
        String sql = "";
        // String szTipoRep="";
        String whereSub = "";
        try {
            String whereFechas = "";
            if (areasHijas) {
                // szTipoRep="remitente_area";
                whereSub += " AND maxSO.remitente_area = '" + idArea + "' ";
            } else {
                // szTipoRep="responsable_area";
            }
            if (regfechaini != "" && regfechafin != "") {
                whereFechas = " and CONVERT(DATETIME, imx.DPC_F_REGISTRO, 103) BETWEEN CONVERT(DATETIME, '" + regfechaini + "', 103) AND CONVERT(DATETIME, '" + regfechafin + "', 103) ";
                whereSub += " and CONVERT(DATETIME, maxIMX.DPC_F_REGISTRO, 103) BETWEEN CONVERT(DATETIME, '" + regfechaini + "', 103) AND CONVERT(DATETIME, '" + regfechafin + "', 103) ";
            }
            sql = "" + "SELECT  " + "		ca1.ID_AREA as responsable_area, " + "		ca1.d_descripcion AS desc_area_resp, " + "		(select count(*) from  " + "			dbo.CG_CASO_OPERACION AS coV WITH(NOLOCK) LEFT OUTER JOIN " + "			dbo.CG_BITACORA_OPERACION AS boV WITH(NOLOCK) ON boV.ID_CASO = coV.ID_CASO AND boV.SECUENCIAL_OPERACION = coV.ID_CASO_OPER INNER JOIN " + "			dbo.IMXEXPEDIENTES AS imx ON imx.ID_GABINETE = boV.ID_GABINETE " + "		where  (boV.FOLIO NOT LIKE 'TMP-%') and boV.id_operacion IN (2,3,10,9,11) " + "			and boV.secuencial_operacion IN ( " + "					SELECT MAX(maxSO.secuencial_operacion) FROM CG_BITACORA_OPERACION maxSO, IMXEXPEDIENTES maxIMX " + "					WHERE maxSO.folio = boV.folio  AND maxSO.FOLIO = maxIMX.FOLIO " + "                 and maxSO.TERMINADA = 'N' " + "                 and maxSO.responsable_area = convert(varchar,ca1.id_area)" + whereSub + "                 ) " + "			and boV.TERMINADA = 'N' and coV.user03 < GETDATE() " + // + "			and boV." + szTipoRep +
            // " = convert(varchar,ca1.id_area) " //Esteban Badillo.
            // Fecha: 04/Febrero/2010. Eliminado
            "			and boV.responsable_area = convert(varchar,ca1.id_area) " + // Esteban
            // Badillo.
            // Fecha:
            // 04/Febrero/2010.
            // Agregado.
            (// Esteban
            areasHijas ? " AND boV.remitente_area = '" + idArea + "' " : "") + // Badillo.
            // Fecha:
            // 04/Febrero/2010.
            // Agregado
            "	     " + whereFechas + "		 ) as pendientesVencidos, " + "		(select count(*) from  " + "			dbo.CG_CASO_OPERACION AS coV WITH(NOLOCK) LEFT OUTER JOIN " + "			dbo.CG_BITACORA_OPERACION AS boV WITH(NOLOCK) ON boV.ID_CASO = coV.ID_CASO AND boV.SECUENCIAL_OPERACION = coV.ID_CASO_OPER INNER JOIN " + "			dbo.IMXEXPEDIENTES AS imx ON imx.ID_GABINETE = boV.ID_GABINETE " + "		 where  (boV.FOLIO NOT LIKE 'TMP-%') and boV.id_operacion IN (2,3,10,9,11) " + "			and boV.secuencial_operacion IN ( " + "					SELECT MAX(maxSO.secuencial_operacion) FROM CG_BITACORA_OPERACION maxSO, IMXEXPEDIENTES maxIMX " + "					WHERE maxSO.folio = boV.folio AND maxSO.FOLIO = maxIMX.FOLIO " + "                 and maxSO.TERMINADA = 'N'" + "			        and maxSO.responsable_area = convert(varchar,ca1.id_area) " + whereSub + "                 ) " + "			and boV.TERMINADA = 'N' and coV.user03 > GETDATE() " + // + "			and boV." + szTipoRep +
            // " = convert(varchar,ca1.id_area) " //Esteban Badillo.
            // Fecha: 04/Febrero/2010. Eliminado
            "			and boV.responsable_area = convert(varchar,ca1.id_area) " + // Esteban
            // Badillo.
            // Fecha:
            // 04/Febrero/2010.
            // Agregado.
            (// Esteban
            areasHijas ? " AND boV.remitente_area = '" + idArea + "' " : "") + // Badillo.
            // Fecha:
            // 04/Febrero/2010.
            // Agregado
            "	     " + whereFechas + "		 ) as pendientesNoVencidos, " + "		(select count(*) from  " + "			dbo.CG_CASO_OPERACION AS coV WITH(NOLOCK) LEFT OUTER JOIN " + "			dbo.CG_BITACORA_OPERACION AS boV WITH(NOLOCK) ON boV.ID_CASO = coV.ID_CASO AND boV.SECUENCIAL_OPERACION = coV.ID_CASO_OPER INNER JOIN " + "			dbo.IMXEXPEDIENTES AS imx WITH(NOLOCK) ON imx.ID_GABINETE = boV.ID_GABINETE " + "		 where  (boV.FOLIO NOT LIKE 'TMP-%') and boV.id_operacion IN (2,3,10,9,11) " + "			and boV.secuencial_operacion IN ( " + "					SELECT MAX(maxSO.secuencial_operacion) FROM CG_BITACORA_OPERACION maxSO, IMXEXPEDIENTES maxIMX " + "					WHERE maxSO.folio = boV.folio AND maxSO.FOLIO = maxIMX.FOLIO " + "			        and maxSO.TERMINADA = 'S'  " + "			        and maxSO.responsable_area = convert(varchar,ca1.id_area) " + whereSub + ")  " + "			and boV.TERMINADA = 'S'  " + // + "			and boV." + szTipoRep +
            // " = convert(varchar,ca1.id_area) " //Esteban Badillo.
            // Fecha: 04/Febrero/2010. Eliminado.
            "			and boV.responsable_area = convert(varchar,ca1.id_area) " + // Esteban
            // Badillo.
            // Fecha:
            // 04/Febrero/2010.
            // Agregado.
            (// Esteban
            areasHijas ? " AND boV.remitente_area = '" + idArea + "' " : "") + // Badillo.
            // Fecha:
            // 04/Febrero/2010.
            // Agregado
            "	     " + whereFechas + "		 ) as cerrado " + "		FROM dbo.CG_CAT_AREAS AS ca1 " + // + "		WHERE ca1.id_area=" + idArea +
            // " or ca1.id_area_padre='" + idArea + "' " //Esteban
            // Badillo. Fecha: 04/Febrero/2010. Eliminado
            "		WHERE " + (areasHijas ? "" : "ca1.id_area=" + idArea + " or ") + " ca1.id_area_padre='" + idArea + // Esteban
            "' " + // Badillo.
            // Fecha:
            // 04/Febrero/2010.
            // Agregado.
            "		ORDER BY ca1.d_descripcion ";
            System.out.println("Query Por Area:\r\n" + sql);
            conn = getConnection();
            List<?> acum = new ArrayList<Object>();
            acum = ReporteManager.select(conn, sql, "responsable_area", "desc_area_resp");
            sb.append(sql + "|");
            String tr = "";
            // String param_tot="";
            int totalVencidos = 0;
            int totalNoVencidos = 0;
            int totalPendientes = 0;
            int totalConcluidos = 0;
            int totalCasos = 0;
            int row = 0;
            if (acum.isEmpty()) {
                tr = "<tr class=\"alternateRow\">" + "	<td colspan=\"9\"><i>No hay informaci&oacute;n para mostrar</i></td>" + "</tr>";
                sb.append(tr);
            } else {
                String grafColumnKeys = "";
                String grafVencidos = "";
                String grafNoVencidos = "";
                String grafConcluidos = "";
                for (Iterator<?> iter = acum.iterator(); iter.hasNext(); row++) {
                    Acumulado a = (Acumulado) iter.next();
                    totalVencidos += a.getVencidosNumero();
                    totalNoVencidos += a.getNovencidosNumero();
                    totalPendientes += a.getPendientes();
                    totalConcluidos += a.getConcluidos();
                    totalCasos += a.getTotalTurnos();
                    // param_tot = param + "&area=" + a.getId(); //BMEA
                    // 05/Febrero/2010
                    // param_tot = param + "&idArea=" + a.getId();
                    tr = RenglonDatoHTML(GestionInterface.RPT_POR_AREA_DETALLE, a.getDescripcion(), a.getVencidosNumero(), a.getNovencidosNumero(), a.getPendientes(), a.getConcluidos(), a.getTotalTurnos(), row, // param + "&area=" + a.getId());
                    param + "&searea=" + a.getDescripcion() + "&idArea=" + a.getId());
                    sb.append(tr);
                    if (grafColumnKeys.equals("") && grafVencidos.equals("") && grafNoVencidos.equals("") && grafConcluidos.equals("")) {
                        grafColumnKeys += a.getDescripcion();
                        grafVencidos += a.getVencidosNumero();
                        grafNoVencidos += a.getNovencidosNumero();
                        grafConcluidos += a.getConcluidos();
                    } else {
                        grafColumnKeys += "," + a.getDescripcion().toString();
                        grafVencidos += "," + a.getVencidosNumero();
                        grafNoVencidos += "," + a.getNovencidosNumero();
                        grafConcluidos += "," + a.getConcluidos();
                    }
                }
                sbGrafColumnKeys.append(grafColumnKeys);
                sbGrafVencidos.append(grafVencidos);
                sbGrafNoVencidos.append(grafNoVencidos);
                sbGrafConcluidos.append(grafConcluidos);
            }
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                // log.warn("Error en rollback", ex);
            }
            log.error("Error generando reporte x area", exc);
            // throw new GestionException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            rs = null;
            conn = null;
        }
        resultado[0] = sb;
        resultado[1] = sbGrafColumnKeys;
        resultado[2] = sbGrafVencidos;
        resultado[3] = sbGrafNoVencidos;
        resultado[4] = sbGrafConcluidos;
        // return sb.toString();
        return resultado;
    }

    /*
	 * Esteban Badillo. Fecha: 12/Oct/2009 Descripcion: Se agrega la sobrecarga
	 * del metodo ReportePorAreaDetalle, incluyendo el parametro que indica el
	 * en la pantalla de consulta del Reporte Estadístico por Area.
	 */
    public StringBuffer ReportePorAreaDetalle(String area, String estructura, String regfechaini, String regfechafin, int tipoacumulado, boolean areasHijas, int idArea, int idAreaPadre) {
        Connection conn = null;
        StringBuffer sb = new StringBuffer();
        ResultSet rs = null;
        try {
            conn = getConnection();
            // Reporte por area
            String whereFechas = "";
            if (regfechaini != "" && regfechafin != "") {
                whereFechas = " AND CONVERT(DATETIME, vimx.fechaRegistro, 103) BETWEEN CONVERT(DATETIME, '" + regfechaini + "', 103) AND CONVERT(DATETIME, '" + regfechafin + "', 103) ";
            }
            String whereEstructura = "";
            if (area != "") {
                whereEstructura = "		     AND ca.id_area = " + area;
            }
            String whereAreasHijas = "";
            if (areasHijas) {
                whereAreasHijas = " and REMITENTE_AREA = " + idArea + " ";
            }
            String whereBase = "";
            switch(tipoacumulado) {
                case GestionInterface.RPT_CNS_VENCIDOS:
                    // BMEA 15/10/2009
                    // whereBase =
                    // "                      AND vimx.id_operacion in(2,3,7, 9,10,11) "
                    whereBase = "                      AND vimx.id_operacion in(2) " + // BMEA 15/10/2009
                    // + "					   AND vimx.terminada = 'N' "
                    "					   AND vimx.cerrado = 'N' " + "					   AND vimx.fecha_compromiso < GETDATE() " + "                      AND vimx.titular = 'S' ";
                    break;
                case GestionInterface.RPT_CNS_NOVENCIDOS:
                    // BMEA 15/10/2009
                    // whereBase =
                    // "                      AND vimx.id_operacion in(2,3,7, 9,10,11) "
                    whereBase = "                      AND vimx.id_operacion in(2) " + // BMEA 15/10/2009
                    // + "					   AND vimx.terminada = 'N' "
                    "					   AND vimx.cerrado = 'N' " + "					   AND vimx.fecha_compromiso > GETDATE()" + "                      AND vimx.titular = 'S' ";
                    break;
                case GestionInterface.RPT_CNS_PENDIENTES:
                    // BMEA 15/10/2009
                    // whereBase =
                    // "                      AND vimx.id_operacion in(2,3,7, 9,10,11) "
                    whereBase = "                      AND vimx.id_operacion in(2) " + // BMEA 15/10/2009
                    // + "					   AND vimx.terminada = 'N' "
                    "					   AND vimx.cerrado = 'N' " + "                      AND vimx.titular = 'S' ";
                    break;
                case GestionInterface.RPT_CNS_CONCLUIDOS:
                    // BMEA 15/10/2009
                    // whereBase =
                    // "                      AND vimx.id_operacion in(2,3,7, 9,10,11) "
                    whereBase = "                      AND vimx.id_operacion in(2) " + // BMEA 15/10/2009
                    // + "					   AND vimx.terminada = 'S' "
                    "					   AND vimx.cerrado = 'S' " + "                      AND vimx.titular = 'S' ";
                    break;
                case GestionInterface.RPT_CNS_CASOS:
                    // whereBase =
                    // "                      AND vimx.id_operacion in(2,3,7, 9,10,11) "
                    whereBase = "                      AND vimx.id_operacion in(2) " + "                      AND vimx.titular = 'S' ";
            }
            String sql = // SE
            "" + "  SELECT DISTINCT " + "        t.folio, " + "        t.referencia, " + "        t.nombre_rem,  " + "        t.desc_area_rem,  " + "        cerrado = CASE " + "           WHEN t.CERRADO = 'S' THEN 'CONCLUIDOS'" + // CAMBIARON
            // A
            // CERRADO
            // EN
            // LUGAR
            // DE
            // TERMINADA
            "           WHEN t.CERRADO = 'N' AND t.fecha_compromiso < GETDATE() THEN 'VENCIDOS'" + "           WHEN t.CERRADO = 'N' AND t.fecha_compromiso > GETDATE() THEN 'NO VENCIDOS'" + "        END, " + "        CASE WHEN t.prioridad = 'N' THEN 'NORMAL' ELSE 'URGENTE' END as prioridad, " + "        t.fecha_compromiso as fechalimite, " + /*
																																						 * Esteban
																																						 * Badillo
																																						 * .
																																						 * Fecha
																																						 * :
																																						 * 15
																																						 * /
																																						 * Oct
																																						 * /
																																						 * 2009.
																																						 * Se
																																						 * agrega
																																						 * el
																																						 * separador
																																						 * de
																																						 * campo
																																						 */
            "        t.desc_area_resp, " + /*
												 * Esteban Badillo. Fecha:
												 * 15/Oct/2009. Se agrega el
												 * campo desc_area_resp
												 */
            "        t.nombre_resp, " + /*
											 * Esteban Badillo. Fecha:
											 * 15/oct/2009. Se agrega el campo
											 * nombre_resp
											 */
            "        t.fecha_inicio" + /*
											 * Esteban Badillo. Fecha:
											 * 16/Oct/2009. Se agrega el campo
											 * fecha_inicio
											 */
            "	  FROM ( " + "					SELECT vimx.* " + "					  FROM vimx_reportes vimx " + "					 WHERE 1 = 1" + "                   " + whereBase + "                   " + whereFechas + "                   " + (areasHijas ? whereAreasHijas : "") + "			) t " + "			  LEFT OUTER JOIN CG_CAT_AREAS ca ON (ca.id_area = t.responsable_area) " + "   WHERE t.desc_area_resp IS NOT NULL " + "                     " + whereEstructura;
            /*
			 * + "GROUP BY " + "        t.responsable_area," +
			 * "        t.desc_area_resp," + "        t.folio," +
			 * "        t.referencia," + "        t.nombre_rem," +
			 * "        t.fechalimite," + "        t.cerrado," +
			 * "        t.nombre_resp," + "        t.prioridad ";
			 */
            /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
			 * Esteban Badillo. Fecha: 29/Dic/2009 Descripcion: Se modifica la
			 */
            sql = "EXEC ReporteAreaDetalle" + // + " @AREA = " + (area.trim().equals("") ? "NULL" : area)
            // //BMEA 05/Febrero/2010
            " @AREA = " + idArea + ", @FECHA_INICIAL = " + (regfechaini.trim().equals("") ? "NULL" : "'" + regfechaini + "'") + ", @FECHA_FINAL = " + (regfechafin.trim().equals("") ? "NULL" : "'" + regfechafin + "'") + ", @AREAS_HIJAS = " + (areasHijas ? "1" : "0") + ", @TIPO_ACUMULADO = " + (tipoacumulado) + ", @ID_AREA_PADRE = " + ((idAreaPadre == 0) || (!areasHijas) ? "NULL" : idAreaPadre);
            /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
            System.out.println("Query Por Area Detalle: \r\n" + sql);
            List<?> gral = new ArrayList<Object>();
            gral = ReporteManager.selectAll(conn, sql, 0);
            String tr = "";
            if (gral.isEmpty()) {
                tr = "<tr class=\"alternateRow\">" + "	<td colspan=\"10\"><h3>No hay informaci&oacute;n para mostrar</h3></td>" + "</tr>";
                sb.append(tr);
            } else {
                int row = 0;
                for (Iterator<?> iter = gral.iterator(); iter.hasNext(); row++) {
                    General a = (General) iter.next();
                    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
					 * Esteban Badillo. Fecha: 15/Oct/2009 Descripcion: Se
					 * sustitiye el metodo original por la nueva sobrecarga del
					 * mismo para agregar los campos "responsable_nombre",
					 * "responsable_area" y "fecha_envio" al detalle del Reporte
					 * Estadístico por Area.
					 */
                    /*
					 * tr = RenglonDatoGralHTML(a.getArea(), a.getFolio(),
					 * a.getReferencia(), a.getNombreRemitente(),
					 * a.getAreaRemitente(), a.getFechaAtencion(),
					 * a.getCerrado(), a.getDestinatario(), a.getPrioridad(),
					 * row);
					 */
                    tr = this.RenglonDatoGralHTML(a.getArea(), a.getFolio(), a.getReferencia(), a.getNombreRemitente(), a.getAreaRemitente(), a.getResponsableNombre(), a.getResponsableAreaDesc(), a.getFechaEnvio(), a.getFechaAtencion(), a.getCerrado(), a.getDestinatario(), a.getPrioridad(), a.getAsunto(), row);
                    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
                    sb.append(tr);
                }
                tr = RenglonDatoGralHTML("FIN", "", "", "", "", "", "", "", "", row + 1);
                sb.append(tr);
            }
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                // log.warn("Error en rollback", ex);
            }
            log.error("Actualizando caso", exc);
            // throw new GestionException(exc);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            rs = null;
            conn = null;
        }
        // return sb.toString();
        return sb;
    }

    public String ResultadoCreacionDelPresupuesto(String ep, String ejercicioFiscal, String ramoEP, String unidadResponsableEP, String grupoFuncional, String funcion, String subFuncion, String programaGeneral, String programaPresupuestario, String actividadInstitucional, String partida, String tipoGasto, String fuenteFinanciamiento, String entidadFederativa, String cartera, String unidadEjecutora, String unidadNormativa, String cOrderBy, String cGroupBy, String cuenta, String usuario, String filtro) throws Exception {
        List<AnteProyectoAut> sbRec = new ArrayList<AnteProyectoAut>();
        StringBuffer sbEnv = new StringBuffer();
        Connection conn = null;
        try {
            conn = getConnection();
            if (!"".equals(ep)) {
                sbRec = ReporteManager.callReporteAnteproyecto(conn, ep, ejercicioFiscal, cOrderBy, cGroupBy, cuenta);
            } else {
                sbRec = ReporteManager.callReporteAnteproyectoParametros(conn, ejercicioFiscal, ramoEP, unidadResponsableEP, grupoFuncional, funcion, subFuncion, programaGeneral, programaPresupuestario, actividadInstitucional, partida, tipoGasto, fuenteFinanciamiento, entidadFederativa, cartera, unidadEjecutora, unidadNormativa, cOrderBy, cGroupBy, cuenta, usuario, filtro);
            }
            String tr = "";
            if (sbRec.isEmpty()) {
                tr = "<tr class=\"alternateRow\">" + "	<td colspan=\"10\"><h3>No hay informaci&oacute;n para mostrar</h3></td>" + "</tr>";
                sbEnv.append(tr);
            } else {
                int row = 0;
                Iterator<AnteProyectoAut> iter = sbRec.iterator();
                while (iter.hasNext()) {
                    AnteProyectoAut anteproyAut = (AnteProyectoAut) iter.next();
                    row++;
                    if (cuenta == null || cuenta == "") {
                        tr = DetalleHTML(anteproyAut.getFolioAnteProyectoAut(), anteproyAut.getD_NConsecutivo(), anteproyAut.getEjercicioFiscal(), anteproyAut.getUnidadResponsableEP(), anteproyAut.getD_CUnidadEjecutora(), anteproyAut.getD_CClaveSiaff(), anteproyAut.getD_CClaveInterna(), anteproyAut.getD_MCalculado(), anteproyAut.getD_MOptimo(), anteproyAut.getD_MIreductible(), row);
                        sbEnv.append(tr);
                    } else {
                        tr = DetalleHTML(cuenta, anteproyAut.getFolioAnteProyectoAut(), anteproyAut.getD_NConsecutivo(), anteproyAut.getEjercicioFiscal(), anteproyAut.getUnidadResponsableEP(), anteproyAut.getD_CUnidadEjecutora(), anteproyAut.getD_CClaveSiaff(), anteproyAut.getD_CClaveInterna(), anteproyAut.getD_MCalculado(), anteproyAut.getD_MOptimo(), anteproyAut.getD_MIreductible(), row);
                        sbEnv.append(tr);
                    }
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
                // throw new GestionException(e.getMessage());
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return sbEnv.toString();
    }

    private String DetalleHTML(String folioAnteProyectoAut, String nConsecutivo, String ejercicioFiscal, String unidadResponsableEP, String unidadEjecutora, String cClaveSiaff, String cClaveInterna, String mCalculado, String mOptimo, String mIreductible, int row) {
        String[] tipoCSS = { "idTh", "idTdTexto", "idTdNumero" };
        String renglon = "<tr class=" + ((row % 2) == 0 ? "AlternateRow" : "NormalRow") + "> " + "	  <td id=\"" + folioAnteProyectoAut + "\">" + folioAnteProyectoAut + "</td>" + "	  <td id=\"" + nConsecutivo + "\">" + nConsecutivo + "</td>" + "	  <td id=\"" + ejercicioFiscal + "\">" + ejercicioFiscal + "</td>" + "	  <td id=\"" + unidadResponsableEP + "\">" + unidadResponsableEP + "</td>" + "	  <td id=\"" + unidadEjecutora + "\">" + unidadEjecutora + "</td>" + "	  <td id=\"" + cClaveSiaff + "\">" + cClaveSiaff + "</td>" + "	  <td id=\"" + cClaveInterna + "\">" + cClaveInterna + "</td>" + "	  <td id=\"" + mCalculado + "\">" + mCalculado + "</td>" + "	  <td id=\"" + mOptimo + "\">" + mOptimo + "</td>" + "	  <td id=\"" + mIreductible + "\">" + mIreductible + "</td>" + "</tr>";
        return renglon;
    }

    private String DetalleHTML(String cuenta, String folioAnteProyectoAut, String nConsecutivo, String ejercicioFiscal, String unidadResponsableEP, String unidadEjecutora, String cClaveSiaff, String cClaveInterna, String mCalculado, String mOptimo, String mIreductible, int row) {
        String[] tipoCSS = { "idTh", "idTdTexto", "idTdNumero" };
        String renglon = "<tr class=" + ((row % 2) == 0 ? "AlternateRow" : "NormalRow") + "> " + "	  <td id=\"" + folioAnteProyectoAut + "\">" + folioAnteProyectoAut + "</td>" + "	  <td id=\"" + nConsecutivo + "\">" + nConsecutivo + "</td>" + "	  <td id=\"" + ejercicioFiscal + "\">" + ejercicioFiscal + "</td>" + "	  <td id=\"" + unidadResponsableEP + "\">" + unidadResponsableEP + "</td>" + "	  <td id=\"" + unidadEjecutora + "\">" + unidadEjecutora + "</td>" + "	  <td id=\"" + cClaveSiaff + "\">" + cClaveSiaff + "</td>" + "	  <td id=\"" + cClaveInterna + "\">" + cClaveInterna + "</td>";
        if ("calculado".equals(cuenta))
            renglon = renglon + "	  <td id=\"" + mCalculado + "\">" + mCalculado + "</td>";
        if ("optimo".equals(cuenta))
            renglon = renglon + "	  <td id=\"" + mOptimo + "\">" + mOptimo + "</td>";
        if ("irreductible".equals(cuenta))
            renglon = renglon + "	  <td id=\"" + mIreductible + "\">" + mIreductible + "</td>";
        if ("calculado,optimo".equals(cuenta)) {
            renglon = renglon + "	  <td id=\"" + mCalculado + "\">" + mCalculado + "</td>";
            renglon = renglon + "	  <td id=\"" + mOptimo + "\">" + mOptimo + "</td>";
        }
        if ("optimo,calculado".equals(cuenta)) {
            renglon = renglon + "	  <td id=\"" + mCalculado + "\">" + mCalculado + "</td>";
            renglon = renglon + "	  <td id=\"" + mOptimo + "\">" + mOptimo + "</td>";
        }
        if ("calculado,irreductible".equals(cuenta)) {
            renglon = renglon + "	  <td id=\"" + mCalculado + "\">" + mCalculado + "</td>";
            renglon = renglon + "	  <td id=\"" + mIreductible + "\">" + mIreductible + "</td>";
        }
        if ("irreductible,calculado".equals(cuenta)) {
            renglon = renglon + "	  <td id=\"" + mCalculado + "\">" + mCalculado + "</td>";
            renglon = renglon + "	  <td id=\"" + mIreductible + "\">" + mIreductible + "</td>";
        }
        if ("optimo,irreductible".equals(cuenta)) {
            renglon = renglon + "	  <td id=\"" + mOptimo + "\">" + mOptimo + "</td>";
            renglon = renglon + "	  <td id=\"" + mIreductible + "\">" + mIreductible + "</td>";
        }
        if ("irreductible,optimo".equals(cuenta)) {
            renglon = renglon + "	  <td id=\"" + mOptimo + "\">" + mOptimo + "</td>";
            renglon = renglon + "	  <td id=\"" + mIreductible + "\">" + mIreductible + "</td>";
        }
        if ("calculado,optimo,irreductible".equals(cuenta)) {
            renglon = renglon + "	  <td id=\"" + mCalculado + "\">" + mCalculado + "</td>";
            renglon = renglon + "	  <td id=\"" + mOptimo + "\">" + mOptimo + "</td>";
            renglon = renglon + "	  <td id=\"" + mIreductible + "\">" + mIreductible + "</td>";
        }
        if ("calculado,irreductible,optimo".equals(cuenta)) {
            renglon = renglon + "	  <td id=\"" + mCalculado + "\">" + mCalculado + "</td>";
            renglon = renglon + "	  <td id=\"" + mOptimo + "\">" + mOptimo + "</td>";
            renglon = renglon + "	  <td id=\"" + mIreductible + "\">" + mIreductible + "</td>";
        }
        if ("optimo,calculado,irreductible".equals(cuenta)) {
            renglon = renglon + "	  <td id=\"" + mCalculado + "\">" + mCalculado + "</td>";
            renglon = renglon + "	  <td id=\"" + mOptimo + "\">" + mOptimo + "</td>";
            renglon = renglon + "	  <td id=\"" + mIreductible + "\">" + mIreductible + "</td>";
        }
        if ("optimo,irreductible,calculado".equals(cuenta)) {
            renglon = renglon + "	  <td id=\"" + mCalculado + "\">" + mCalculado + "</td>";
            renglon = renglon + "	  <td id=\"" + mOptimo + "\">" + mOptimo + "</td>";
            renglon = renglon + "	  <td id=\"" + mIreductible + "\">" + mIreductible + "</td>";
        }
        if ("irreductible,calculado,optimo".equals(cuenta)) {
            renglon = renglon + "	  <td id=\"" + mCalculado + "\">" + mCalculado + "</td>";
            renglon = renglon + "	  <td id=\"" + mOptimo + "\">" + mOptimo + "</td>";
            renglon = renglon + "	  <td id=\"" + mIreductible + "\">" + mIreductible + "</td>";
        }
        if ("irreductible,optimo,calculado".equals(cuenta)) {
            renglon = renglon + "	  <td id=\"" + mCalculado + "\">" + mCalculado + "</td>";
            renglon = renglon + "	  <td id=\"" + mOptimo + "\">" + mOptimo + "</td>";
            renglon = renglon + "	  <td id=\"" + mIreductible + "\">" + mIreductible + "</td>";
        }
        renglon = renglon + "</tr>";
        return renglon;
    }

    public String ResultadoCreacionDelPresupuestoCalendarizado(String ep, String ejercicioFiscal, String ramoEP, String unidadResponsableEP, String grupoFuncional, String funcion, String subFuncion, String programaGeneral, String programaPresupuestario, String actividadInstitucional, String partida, String tipoGasto, String fuenteFinanciamiento, String entidadFederativa, String cartera, String unidadEjecutora, String unidadNormativa, String cOrderBy, String cGroupBy, String usuario, String filtro) throws Exception {
        List<AnteProyectoAutCalendario> sbRec = new ArrayList<AnteProyectoAutCalendario>();
        StringBuffer sbEnv = new StringBuffer();
        Connection conn = null;
        try {
            conn = getConnection();
            if (!"".equals(ep)) {
                sbRec = ReporteManager.callReporteAnteproyectoCalendarizado(conn, ep, ejercicioFiscal, cOrderBy, cGroupBy);
            } else {
                sbRec = ReporteManager.callReporteAnteproyectoCalendarizadoParametros(conn, ejercicioFiscal, ramoEP, unidadResponsableEP, grupoFuncional, funcion, subFuncion, programaGeneral, programaPresupuestario, actividadInstitucional, partida, tipoGasto, fuenteFinanciamiento, entidadFederativa, cartera, unidadEjecutora, unidadNormativa, cOrderBy, cGroupBy, usuario, filtro);
            }
            String tr = "";
            if (sbRec.isEmpty()) {
                tr = "<tr class=\"alternateRow\">" + "	<td colspan=\"10\"><h3>No hay informaci&oacute;n para mostrar</h3></td>" + "</tr>";
                sbEnv.append(tr);
            } else {
                int row = 0;
                Iterator<AnteProyectoAutCalendario> iter = sbRec.iterator();
                while (iter.hasNext()) {
                    AnteProyectoAutCalendario anteproyAutCal = iter.next();
                    row++;
                    tr = CalendarioHTML(anteproyAutCal.getNFolioAnteProyectoAut(), anteproyAutCal.getNConsecutivo(), anteproyAutCal.getAEjercicioFiscal(), anteproyAutCal.getCUnidadResponsable(), anteproyAutCal.getCClaveSiaff(), anteproyAutCal.getCClaveInterna(), anteproyAutCal.getCCentroContable(), anteproyAutCal.getNFolioAnteProyecto(), anteproyAutCal.getMAnualAutorizado(), anteproyAutCal.getMEnero(), anteproyAutCal.getMFebrero(), anteproyAutCal.getMMarzo(), anteproyAutCal.getMAbril(), anteproyAutCal.getMMayo(), anteproyAutCal.getMJunio(), anteproyAutCal.getMJulio(), anteproyAutCal.getMAgosto(), anteproyAutCal.getMSeptiembre(), anteproyAutCal.getMOctubre(), anteproyAutCal.getMNoviembre(), anteproyAutCal.getMDiciembre(), row);
                    sbEnv.append(tr);
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
                // throw new GestionException(e.getMessage());
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return sbEnv.toString();
    }

    private String CalendarioHTML(String nFolioAnteProyectoAut, String nConsecutivo, String aEjercicioFiscal, String cUnidadResponsable, String cClaveSiaff, String cClaveInterna, String cCentroContable, String nFolioAnteProyecto, String mAnualAutorizado, String mEnero, String mFebrero, String mMarzo, String mAbril, String mMayo, String mJunio, String mJulio, String mAgosto, String mSeptiembre, String mOctubre, String mNoviembre, String mDiciembre, int row) {
        String[] tipoCSS = { "idTh", "idTdTexto", "idTdNumero" };
        String renglon = "<tr class=" + ((row % 2) == 0 ? "AlternateRow" : "NormalRow") + "> " + "<td id=\"" + nFolioAnteProyectoAut + "\">" + nFolioAnteProyectoAut + "</td>" + "<td id=\"" + nConsecutivo + "\">" + nConsecutivo + "</td>" + "<td id=\"" + aEjercicioFiscal + "\">" + aEjercicioFiscal + "</td>" + "<td id=\"" + cUnidadResponsable + "\">" + cUnidadResponsable + "</td>" + "<td id=\"" + cClaveSiaff + "\">" + cClaveSiaff + "</td>" + "<td id=\"" + cClaveInterna + "\">" + cClaveInterna + "</td>" + "<td id=\"" + cCentroContable + "\">" + cCentroContable + "</td>" + "<td id=\"" + nFolioAnteProyecto + "\">" + nFolioAnteProyecto + "</td>" + "<td id=\"" + mAnualAutorizado + "\">" + mAnualAutorizado + "</td>" + "<td id=\"" + mEnero + "\">" + mEnero + "</td>" + "<td id=\"" + mFebrero + "\">" + mFebrero + "</td>" + "<td id=\"" + mMarzo + "\">" + mMarzo + "</td>" + "<td id=\"" + mAbril + "\">" + mAbril + "</td>" + "<td id=\"" + mMayo + "\">" + mMayo + "</td>" + "<td id=\"" + mJunio + "\">" + mJunio + "</td>" + "<td id=\"" + mJulio + "\">" + mJulio + "</td>" + "<td id=\"" + mAgosto + "\">" + mAgosto + "</td>" + "<td id=\"" + mSeptiembre + "\">" + mSeptiembre + "</td>" + "<td id=\"" + mOctubre + "\">" + mOctubre + "</td>" + "<td id=\"" + mNoviembre + "\">" + mNoviembre + "</td>" + "<td id=\"" + mDiciembre + "\">" + mDiciembre + "</td>" + "</tr>";
        return renglon;
    }

    private static final String formatoNumero = "###,##0.00";

    private static final DecimalFormat dm = new DecimalFormat(formatoNumero);

    private static String formateaNumero(String monto) {
        try {
            if (monto != null) {
                return dm.format(Double.parseDouble(monto));
            } else
                return dm.format(0.0d);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return monto;
        }
    }

    public void creaCasoReporteEdoCta(Usuario u, int idTCaso, FolioGeneratorInterface fg, String opResponsable, String jniName) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            Util.generaCaso(u, idTCaso, fg, opResponsable, jniName, conn);
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (Exception e2) {
                log.warn(e2);
            }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
