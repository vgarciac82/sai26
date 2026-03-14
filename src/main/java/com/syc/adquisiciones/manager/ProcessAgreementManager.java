package com.syc.adquisiciones.manager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

public class ProcessAgreementManager {

    private static final Logger log = LoggerFactory.getLogger(ProcessAgreementManager.class);

    public boolean isAvailableProcess(Connection conn, String cParametro) throws SQLException {
        boolean resp = false;
        String query = "";
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            query = "SELECT * FROM mSistema with(Nolock) WHERE cParametro='" + cParametro + "'";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                if ("TRUE".equalsIgnoreCase(rs.getString("cValor"))) {
                    resp = true;
                    log.info("Object: {}", "El proceso de envios de correos para " + cParametro + "  esta habilitado");
                } else {
                    log.info("Object: {}", "El proceso de envios de correos para " + cParametro + " no esta habilitado");
                }
            } else {
                log.info("Object: {}", "El proceso de envios de correos para " + cParametro + " no está la bandera en msistema");
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
        return resp;
    }

    public int sendEmail(Connection conn, String cParametro) throws SQLException {
        int resp = -1;
        String query = "";
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            query = "SELECT * FROM mSistema with(Nolock) WHERE cParametro='" + cParametro + "'";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = Integer.parseInt(rs.getString("cValor"));
            } else {
                log.info("Object: {}", "El proceso de envios de correos para " + cParametro + " no está la bandera de la hora de ejecución en msistema.");
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
        return resp;
    }

    public int numbersDaysBeforeFinishingAgreement(Connection conn) throws SQLException {
        int resp = 0;
        String query = "";
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            query = "SELECT * FROM mSistema with(Nolock) WHERE cParametro='numbersDaysBeforeFinishingAgreement'";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = Integer.parseInt(rs.getString("cValor"));
            } else {
                log.info("El proceso de envios de correos para contratos que estan por terminar no está la bandera de la cantidad de días en msistema.");
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
        return resp;
    }

    public ArrayList<List<String>> getListAgreements(Connection conn, int ncantDays) throws SQLException {
        String query = "";
        ResultSet rs = null;
        PreparedStatement ps = null;
        ArrayList<List<String>> tabla = null;
        List<String> fila = null;
        try {
            //contratos del ejercicio actual centralizados
            //contratos del ejercicio actual descentralizados
            //Contratos plurianuales de ejercicios anteriores centralizados
            //Contratos plurianuales de ejercicios anteriores descentralizados
            query = //Contratos cap4
            "select cIdUnidadEjecutora, \r\n" + "cont.cIdContratoDefinitivo\r\n" + ",cNoContratoCNET\r\n" + ",cConceptoContrato\r\n" + ",ue.D_DESCRIPCION areaReq \r\n" + ",convert(date,fFin) fechaFin\r\n" + "from mcontrato cont with(Nolock) \r\n" + "inner join tCatUnidadEjecutora as ue with(Nolock) on ue.cUnidadEjecutora=cont.cIdUnidadEjecutora \r\n" + "left join mContratoTerminacionAnticipada as term with(Nolock) on term.cIdContratoDefinitivo=cont.cIdContratoDefinitivo\r\n" + "where \r\n" + "nIdEstado=4 and cont.cIdTipoContrato<>'CT' and cont.esDescentralizado=0\r\n" + "and term.fFechaTermino is null\r\n" + "and cont.cIdContratoDefinitivo not in(select cidContratoDefinitivo from mBitacoraSendEmailContrato with(Nolock) where isEmailSend=1) \r\n" + "and convert(date,fFin)>convert(date,GETDATE()) " + "and convert(date,fFin)<=dateadd(day," + ncantDays + ",convert(date,GETDATE()))\r\n" + "union select con.cIdUnidadEjecutoraSolicitud cIdUnidadEjecutora,\r\n" + "cont.cIdContratoDefinitivo,\r\n" + "cNoContratoCNET,\r\n" + "cConceptoContrato,\r\n" + "ue.D_DESCRIPCION areaReq,\r\n" + "convert(date,fFin) fechaFin\r\n" + "from mcontrato cont with(Nolock) \r\n" + "inner join mProcedimiento pro with(Nolock) on pro.cIdProcedimiento=cont.cIdProcedimiento\r\n" + "inner join (\r\n" + "	select cIdUnidadEjecutoraSolicitud,con.cIdConsolidado \r\n" + "	from mConsolidadoSolicitud sol with(Nolock) \r\n" + "	inner join mConsolidado as con with(Nolock) on con.cIdConsolidado=sol.cIdConsolidado\r\n" + "	where con.nIdEstado=2 \r\n" + "	group by cIdUnidadEjecutoraSolicitud,con.cIdConsolidado \r\n" + ")con on con.cIdConsolidado=pro.cIdConsolidado\r\n" + "inner join tCatUnidadEjecutora as ue with(Nolock) on ue.cUnidadEjecutora=con.cIdUnidadEjecutoraSolicitud\r\n" + "left join mContratoTerminacionAnticipada as term with(Nolock) on term.cIdContratoDefinitivo=cont.cIdContratoDefinitivo\r\n" + "where cont.nIdEstado=4 and cont.cIdTipoContrato<>'CT'\r\n" + "and cont.esDescentralizado=1 " + "and term.fFechaTermino is null " + "and cont.cIdContratoDefinitivo not in(select cidContratoDefinitivo from mBitacoraSendEmailContrato with(Nolock) where isEmailSend=1) \r\n" + "and convert(date,fFin)>convert(date,GETDATE()) " + "and convert(date,fFin)<=dateadd(day," + ncantDays + ",convert(date,GETDATE()))\r\n" + "union\r\n" + "select \r\n" + "cIdUnidadEjecutora, \r\n" + "cIdContratoDefinitivo\r\n" + ",cNoContratoCNET\r\n" + ",cConceptoContrato\r\n" + ",ue.D_DESCRIPCION areaReq \r\n" + ",convert(date,fFin) fechaFin\r\n" + "from mPlurianualidadContrato cont with(Nolock) \r\n" + "inner join tCatUnidadEjecutora as ue with(Nolock) on ue.cUnidadEjecutora=cont.cIdUnidadEjecutora \r\n" + "where \r\n" + "nIdEstado=4 and cont.esDescentralizado=0 \r\n" + "and cIdContratoDefinitivo not in(select cidContratoDefinitivo from mBitacoraSendEmailContrato with(Nolock) where isEmailSend=1) \r\n" + "and convert(date,fFin)>convert(date,GETDATE()) " + "and convert(date,fFin)<=dateadd(day," + ncantDays + ",convert(date,GETDATE()))\r\n" + "union\r\n" + "select  \r\n" + "part.cIdUnidadRequi cIdUnidadEjecutora,  \r\n" + "cont.cIdContratoDefinitivo \r\n" + ",cNoContratoCNET \r\n" + ",cConceptoContrato \r\n" + ",upper(ue.D_DESCRIPCION) areaReq \r\n" + ",convert(date,fFin) fechaFin\r\n" + "from mPlurianualidadContrato cont with(Nolock) \r\n" + "inner join(\r\n" + "	select cIdUnidadRequi,cIdContratoDefinitivo \r\n" + "	from mPartidasContratoPlurianual  with(Nolock)\r\n" + "	group by cIdUnidadRequi,cIdContratoDefinitivo\r\n" + ")part on part.cIdContratoDefinitivo=cont.cIdContratoDefinitivo\r\n" + "inner join tCatUnidadEjecutora as ue with(Nolock) on ue.cUnidadEjecutora=part.cIdUnidadRequi\r\n" + "where nIdEstado=4 and cont.esDescentralizado=1\r\n" + "and cont.cIdContratoDefinitivo not in(select cidContratoDefinitivo from mBitacoraSendEmailContrato with(Nolock) where isEmailSend=1) " + " and convert(date,fFin)>convert(date,GETDATE())" + "and convert(date,fFin)<=dateadd(day," + ncantDays + ",convert(date,GETDATE()))\r\n" + "union\r\n" + "select \r\n" + "cIdUnidadEjecutora, \r\n" + "contCap4.cIdContratoDefinitivo\r\n" + ",cNoContratoCNET\r\n" + ",cConceptoContrato\r\n" + ",ue.D_DESCRIPCION areaReq \r\n" + ",convert(date,fechas.fFecha) fechaFin\r\n" + "from mContratoCap4 as contCap4 with(nolock)\r\n" + "inner join tCatUnidadEjecutora as ue with(Nolock) on ue.cUnidadEjecutora=contCap4.cIdUnidadEjecutora \r\n" + "inner join mContratoCap4Fechas as fechas with(nolock)\r\n" + "on fechas.cIdContratoDefinitivo=contCap4.cIdContratoDefinitivo\r\n" + "where contCap4.nIdEstado=4 and fechas.nIdFecha=18\r\n" + "and contCap4.cIdContratoDefinitivo not in(select cidContratoDefinitivo from mBitacoraSendEmailContrato with(Nolock) where isEmailSend=1) \r\n" + " and convert(date,fechas.fFecha)>convert(date,GETDATE()) " + "and convert(date,fechas.fFecha)<=dateadd(day," + ncantDays + ",convert(date,GETDATE()))";
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            tabla = new ArrayList<List<String>>();
            fila = new ArrayList<String>();
            while (rs.next()) {
                for (int i = 0; i < 6; i++) {
                    fila.add(rs.getString(i + 1));
                }
                tabla.add(fila);
                fila = null;
                fila = new ArrayList<String>();
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
            fila = null;
        }
        return tabla;
    }

    public String getCoordination(Connection conn, String cUE) throws SQLException {
        String resp = "";
        String query = "";
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            query = "select *from tCatUnidadesEjecutorasGPP_GRH with(Nolock)\r\n" + "where cIdunidadEjecutoraGPP='" + cUE + "'";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = rs.getString("cCoordinacionGPP");
            } else {
                log.warn("Object: {}", "No se encontro la coordinación de la unidad ejecutora " + cUE);
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
        return resp;
    }

    public String getEmailCoordinator(Connection conn, String cUE, String cEjercicio) throws SQLException {
        String resp = "";
        String query = "";
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            query = "select c_jefeinmediato,d_email,ue.cUejecutora\r\n" + ",ue.cDescCoortaCordinacion,ue.c_coordinacion\r\n" + ",plazas.c_plaza,plazas.d_plaza\r\n" + "from nomina_" + cEjercicio + ".dbo.nom_empleado emp with(Nolock)\r\n" + "inner join nomina_" + cEjercicio + ".dbo.nom_plazas plazas with(Nolock)\r\n" + "on plazas.c_empresa=emp.c_empresa\r\n" + "and plazas.c_plaza=emp.c_plaza\r\n" + "and plazas.c_puesto=emp.c_puesto\r\n" + "inner join nomina_" + cEjercicio + ".dbo.nom_Unidad_Ejecutora ue  with(Nolock)\r\n" + "on emp.cUadministrativa=ue.cUadministrativa\r\n" + "where emp.n_emplstatus=1\r\n" + "and cUejecutora='" + cUE + "'\r\n" + "and c_jefeinmediato in(1,0)\r\n";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = rs.getString("d_email");
            } else {
                log.info("Object: {}", "No se encontro el correo de la coordinaci\u00f3n " + cUE);
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
        return resp;
    }

    public String getEmailManager(Connection conn, String cUE, String cEjercicio) throws SQLException {
        String resp = "";
        String query = "";
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            query = "select c_jefeinmediato,d_email,ueGPP.cIdunidadEjecutoraGPP\r\n" + ",ue.cDescCoortaCordinacion,ue.c_coordinacion\r\n" + ",plazas.c_plaza,plazas.d_plaza\r\n" + "from nomina_" + cEjercicio + ".dbo.nom_empleado emp with(Nolock)\r\n" + "inner join nomina_" + cEjercicio + ".dbo.nom_plazas plazas with(Nolock)\r\n" + "on plazas.c_empresa=emp.c_empresa\r\n" + "and plazas.c_plaza=emp.c_plaza\r\n" + "and plazas.c_puesto=emp.c_puesto\r\n" + "inner join nomina_" + cEjercicio + ".dbo.nom_Unidad_Ejecutora ue  with(Nolock)\r\n" + "on emp.cUadministrativa=ue.cUadministrativa\r\n" + "inner join tCatUnidadesEjecutorasGPP_GRH ueGPP with(Nolock) on ue.cUejecutora=ueGPP.cIdunidadEjecutoraGRH \r\n" + "where emp.n_emplstatus=1\r\n" + "and ueGPP.cIdunidadEjecutoraGPP='" + cUE + "'\r\n" + "and (plazas.d_plaza like'Gerencia%' or plazas.d_plaza like'PROMOTOR_A%')\r\n";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = rs.getString("d_email");
            } else {
                log.info("Object: {}", "No se encontro el correo del gerente de la unidad " + cUE);
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
        return resp;
    }

    public String getEmailPurchasingDepartment(Connection conn, String cEjercicio) throws SQLException {
        String resp = "";
        String query = "";
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            query = "select c_jefeinmediato,d_email,ue.cUejecutora\r\n" + ",ue.cDescCoortaCordinacion,ue.c_coordinacion\r\n" + ",plazas.c_plaza,plazas.d_plaza\r\n" + "from nomina_" + cEjercicio + ".dbo.nom_empleado emp with(Nolock)\r\n" + "inner join nomina_" + cEjercicio + ".dbo.nom_plazas plazas with(Nolock)\r\n" + "on plazas.c_empresa=emp.c_empresa\r\n" + "and plazas.c_plaza=emp.c_plaza\r\n" + "and plazas.c_puesto=emp.c_puesto\r\n" + "inner join nomina_" + cEjercicio + ".dbo.nom_Unidad_Ejecutora ue  with(Nolock)\r\n" + "on emp.cUadministrativa=ue.cUadministrativa\r\n" + "where emp.n_emplstatus=1\r\n" + "and plazas.d_plaza like'%DEPARTAMENTO DE ADQUISICIONES%'\r\n" + "order by cUejecutora";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = rs.getString("d_email");
            } else {
                log.info("No se encontro el correo del departamento de adquisiciones ");
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
        return resp;
    }

    public String getEmailSubGerenteAdquisiciones(Connection conn, String cEjercicio) throws SQLException {
        String resp = "";
        String query = "";
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            query = "select c_jefeinmediato,d_email,ue.cUejecutora\r\n" + ",ue.cDescCoortaCordinacion,ue.c_coordinacion\r\n" + ",plazas.c_plaza,plazas.d_plaza\r\n" + "from nomina_" + cEjercicio + ".dbo.nom_empleado emp with(Nolock)\r\n" + "inner join nomina_" + cEjercicio + ".dbo.nom_plazas plazas with(Nolock)\r\n" + "on plazas.c_empresa=emp.c_empresa\r\n" + "and plazas.c_plaza=emp.c_plaza\r\n" + "and plazas.c_puesto=emp.c_puesto\r\n" + "inner join nomina_" + cEjercicio + ".dbo.nom_Unidad_Ejecutora ue  with(Nolock)\r\n" + "on emp.cUadministrativa=ue.cUadministrativa\r\n" + "where emp.n_emplstatus=1\r\n" + "and plazas.d_plaza like'%SUBGEREN%ADQUISICIONES%'\r\n" + "order by cUejecutora";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = rs.getString("d_email");
            } else {
                log.info("No se encontro el correo del departamento de adquisiciones ");
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
        return resp;
    }

    public boolean saveBinnacleSendEmailAgrements(String cIdContratoDefinitivo, String cObservation, String cEmairPurchasingDepartment, String cEmairManager, String cEmairCoordinator, int isEmailSend) throws Exception {
        boolean resp = false;
        PreparedStatement pstmnt = null;
        String query = null;
        Connection conn = null;
        try {
            //conn=DataSourceManager.getConnection( jniName );
            conn = com.syc.gestion.util.Util.getStandAloneConnection(true);
            query = "insert into mBitacoraSendEmailContrato (cidContratoDefinitivo,cEamilCoordinador,cEamilGerente,cEamilJefeAdquisiciones,cObservaciones,isEmailSend,fFechaEnvio)\r\n" + "values(?,?,?,?,?,?,getdate()) ";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, cIdContratoDefinitivo);
            pstmnt.setString(2, cEmairCoordinator);
            pstmnt.setString(3, cEmairManager);
            pstmnt.setString(4, cEmairPurchasingDepartment);
            pstmnt.setString(5, cObservation);
            pstmnt.setInt(6, isEmailSend);
            resp = pstmnt.executeUpdate() > 0;
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            log.error("Error occurred", "Error al guardar la bitacora de envio de correos: " + e);
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            if (conn != null) {
                conn.close();
            }
            pstmnt = null;
            query = null;
            conn = null;
        }
        return resp;
    }

    public ArrayList<List<String>> getListAreasPresupuestoPAAAS(Connection conn, String cEjercicio) throws SQLException {
        StringBuilder query = new StringBuilder();
        ResultSet rs = null;
        PreparedStatement ps = null;
        ArrayList<List<String>> tabla = null;
        List<String> fila = null;
        ResultSetMetaData rsM = null;
        try {
            query.append("select modd.UE+' - '+unidad.D_DESCRIPCION areaResponsable ");
            query.append(",modd.Modificado PRESUPUESTO_MODIFICADO ");
            query.append(",isnull(paas.subtotal_calendarizado_PAAS,0)subtotal_calendarizado_PAAS ");
            query.append(",isnull(paas.Total_calendarizado_PAAS,0)Total_calendarizado_PAAS ");
            query.append(",modd.UE ");
            query.append(",isnull(manager.d_email,coordinadores.d_email)d_email ");
            query.append(",isnull(manager.d_plaza,coordinadores.d_plaza)d_plaza ");
            query.append(",relUE.cCoordinacionGPP ");
            query.append(",coordinadores.d_email rmailCoordinador ");
            query.append(",isnull(manager.nombreComp,coordinadores.nombreCompCoor)nombre ");
            query.append("from ");
            query.append("(select  ");
            query.append("	substring(csubcuenta,57,3) as UE ");
            query.append("	,SUM(msaldoArrastre)Modificado ");
            query.append("	from fn_tSaldosVistaPorFecha('81102',convert(varchar,convert(date,GETDATE()),103)) ");
            query.append("	where SUBSTRING(csubcuenta,32,1) in('3','2','5') ");
            query.append("	and substring(csubcuenta,57,3) not in(select cIdunidadEjecutora from tBlackListUE_Presupuesto with(Nolock)) ");
            query.append(" and SUBSTRING(csubcuenta,32,3) not  in('321','322','341','343','349','351','375','376','377','378','379','592','593','594','595','596','598') ");
            query.append(" and SUBSTRING(csubcuenta,32,2) not  in('39','58') ");
            query.append("	group by substring(csubcuenta,57,3) ");
            query.append(")modd ");
            query.append("inner join tCatUnidadEjecutora as unidad with(Nolock) ");
            query.append("on unidad.cUnidadEjecutora=modd.UE ");
            query.append("inner join tCatUnidadesEjecutorasGPP_GRH as relUE with(Nolock) ");
            query.append("on relUE.cIdunidadEjecutoraGPP=unidad.cUnidadEjecutora ");
            query.append("left join( ");
            query.append("	select  ");
            query.append("	cIdUnidadEjecutora ");
            query.append("	,sum(subtotal)subtotal_calendarizado_PAAS ");
            query.append("	,SUM(Total)Total_calendarizado_PAAS ");
            query.append("	from	 ");
            query.append("		(select  ");
            query.append("		per.* ");
            query.append("		,nCantidad*mPrecioUnitario as subtotal ");
            query.append("		,round(cast(nCantidad*mPrecioUnitario*(1+(paasDet.nPorcentajeIVA*0.01)) as money),2) as Total ");
            query.append("		,paasDet.nPorcentajeIVA ");
            query.append("		from mProgramaAnual as paas with(nolock) ");
            query.append("		inner join mProgramaAnualDetalle as paasDet with(nolock) ");
            query.append("		on paas.cIdUnidadEjecutora=paasDet.cIdUnidadEjecutora ");
            query.append("		and paas.cEjercicio=paas.cEjercicio ");
            query.append("		inner join mProgramaAnualDetallePeriodo as per with(Nolock) ");
            query.append("		on per.cEjercicio=paasDet.cEjercicio ");
            query.append("		and per.cIdUnidadEjecutora=paasDet.cIdUnidadEjecutora ");
            query.append("		and per.cIdCABM=paasDet.cIdCABM and per.cIdSubPartida=paasDet.cIdSubPartida ");
            query.append("		)sub ");
            query.append("	group by cIdUnidadEjecutora ");
            query.append("	)paas on paas.cIdUnidadEjecutora=modd.UE ");
            query.append("left join ( ");
            query.append("		select c_jefeinmediato,d_email,ueGPP.cIdunidadEjecutoraGPP ");
            query.append("		,ue.cDescCoortaCordinacion,ue.c_coordinacion ");
            query.append("		,plazas.c_plaza,plazas.d_plaza ");
            query.append("		,emp.d_emplnombre+' '+emp.d_emplap+' '+emp.d_emplam nombreComp ");
            query.append("		from nomina_" + cEjercicio + ".dbo.nom_empleado emp with(Nolock) ");
            query.append("		inner join nomina_" + cEjercicio + ".dbo.nom_plazas plazas with(Nolock) ");
            query.append("		on plazas.c_empresa=emp.c_empresa ");
            query.append("		and plazas.c_plaza=emp.c_plaza ");
            query.append("		and plazas.c_puesto=emp.c_puesto ");
            query.append("		inner join nomina_" + cEjercicio + ".dbo.nom_Unidad_Ejecutora ue  with(Nolock) ");
            query.append("		on emp.cUadministrativa=ue.cUadministrativa ");
            query.append("		inner join tCatUnidadesEjecutorasGPP_GRH ueGPP with(Nolock) on ue.cUejecutora=ueGPP.cIdunidadEjecutoraGRH  ");
            query.append("		where emp.n_emplstatus=1 ");
            query.append("		and (plazas.d_plaza like'Gerencia%' or plazas.d_plaza like'UNIDAD%REGIONAL%'or plazas.d_plaza like'UNIDAD%DESARROLLO%' or plazas.d_plaza like'PROMOTOR_A%') ");
            query.append(")manager on manager.cIdunidadEjecutoraGPP=modd.UE ");
            query.append("left join( ");
            query.append("	select c_jefeinmediato,d_email,ue.cUejecutora ");
            query.append("	,ue.cDescCoortaCordinacion,ue.c_coordinacion ");
            query.append("	,plazas.c_plaza,plazas.d_plaza ");
            query.append("	,emp.d_emplnombre+' '+emp.d_emplap+' '+emp.d_emplam nombreCompCoor ");
            query.append("	from nomina_" + cEjercicio + ".dbo.nom_empleado emp with(Nolock) ");
            query.append("	inner join nomina_" + cEjercicio + ".dbo.nom_plazas plazas with(Nolock) ");
            query.append("	on plazas.c_empresa=emp.c_empresa ");
            query.append("	and plazas.c_plaza=emp.c_plaza ");
            query.append("	and plazas.c_puesto=emp.c_puesto ");
            query.append("	inner join nomina_" + cEjercicio + ".dbo.nom_Unidad_Ejecutora ue  with(Nolock) ");
            query.append("	on emp.cUadministrativa=ue.cUadministrativa ");
            query.append("	where emp.n_emplstatus=1 ");
            query.append("	and c_jefeinmediato in(1,0) ");
            query.append(")coordinadores on coordinadores.cUejecutora=relUE.cCoordinacionGPP ");
            query.append("and coordinadores.c_coordinacion=relUE.cCoordinacionGPP ");
            query.append("where modd.Modificado>ISNULL(paas.Total_calendarizado_PAAS,0) ");
            query.append("order by modd.UE ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            rs = ps.executeQuery();
            rsM = rs.getMetaData();
            tabla = new ArrayList<List<String>>();
            fila = new ArrayList<String>();
            while (rs.next()) {
                for (int i = 0; i < rsM.getColumnCount(); i++) {
                    fila.add(rs.getString(i + 1));
                }
                tabla.add(fila);
                fila = null;
                fila = new ArrayList<String>();
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
            fila = null;
            query = null;
            rsM = null;
        }
        return tabla;
    }

    public ArrayList<List<String>> getListAreasBorrarPAAAS(Connection conn, String cEjercicio) throws SQLException {
        StringBuilder query = new StringBuilder();
        ResultSet rs = null;
        PreparedStatement ps = null;
        ArrayList<List<String>> tabla = null;
        List<String> fila = null;
        ResultSetMetaData rsM = null;
        try {
            query.append("	select  ");
            query.append("	paas.cIdUnidadEjecutora+' - '+unidad.D_DESCRIPCION areaResponsable  ");
            query.append("	,paas.totalModificado PRESUPUESTO_MODIFICADO  ");
            query.append("	,isnull(paas.subtotal_calendarizado_PAAS,0)subtotal_calendarizado_PAAS  ");
            query.append("	,isnull(paas.Total_calendarizado_PAAS,0)Total_calendarizado_PAAS  ");
            query.append("	,paas.cIdUnidadEjecutora ");
            query.append("	,isnull(manager.d_email,coordinadores.d_email)d_email  ");
            query.append("	,isnull(manager.d_plaza,coordinadores.d_plaza)d_plaza  ");
            query.append("	,relUE.cCoordinacionGPP  ");
            query.append("	,coordinadores.d_email rmailCoordinador  ");
            query.append("	,isnull(manager.nombreComp,coordinadores.nombreCompCoor)nombre  ");
            query.append("	from	 ");
            query.append("		(select  ");
            query.append("		cIdUnidadEjecutora ");
            query.append("		,sum(subtotal_calendarizado_PAAS) subtotal_calendarizado_PAAS ");
            query.append("		,SUM(Total_calendarizado_PAAS)Total_calendarizado_PAAS ");
            query.append("		,isnull(Modificado,0)totalModificado ");
            query.append("		from  ");
            query.append("			(select   ");
            query.append("				cIdUnidadEjecutora ");
            query.append("				,cIdSubPartida ");
            query.append("				,sum(subtotal)subtotal_calendarizado_PAAS  ");
            query.append("				,SUM(Total)Total_calendarizado_PAAS  ");
            query.append("				from	  ");
            query.append("					(select   ");
            query.append("					per.cIdUnidadEjecutora ");
            query.append("					,sum(nCantidad*mPrecioUnitario) as subtotal  ");
            query.append("					,sum(round(cast(nCantidad*mPrecioUnitario*(1+(paasDet.nPorcentajeIVA*0.01)) as money),2)) as Total  ");
            query.append("					,paasDet.cIdSubPartida ");
            query.append("					,paasDet.cIdCABM ");
            query.append("					from mProgramaAnual as paas with(nolock)  ");
            query.append("					inner join mProgramaAnualDetalle as paasDet with(nolock)  ");
            query.append("					on paas.cIdUnidadEjecutora=paasDet.cIdUnidadEjecutora  ");
            query.append("					and paas.cEjercicio=paas.cEjercicio  ");
            query.append("					inner join mProgramaAnualDetallePeriodo as per with(Nolock)  ");
            query.append("					on per.cEjercicio=paasDet.cEjercicio  ");
            query.append("					and per.cIdUnidadEjecutora=paasDet.cIdUnidadEjecutora  ");
            query.append("					and per.cIdCABM=paasDet.cIdCABM and per.cIdSubPartida=paasDet.cIdSubPartida  ");
            query.append("					group by per.cIdUnidadEjecutora ");
            query.append("					,paasDet.cIdSubPartida ");
            query.append("					,paasDet.cIdCABM ");
            query.append("					)sub ");
            query.append("				group by cIdUnidadEjecutora,cIdSubPartida ");
            query.append("				having sum(subtotal)>0 ");
            query.append("			)paas ");
            query.append("			left join( ");
            query.append("				select   ");
            query.append("				substring(csubcuenta,57,3) as UE ");
            query.append("				,SUBSTRING(csubcuenta,32,5)partida ");
            query.append("				,SUM(msaldoArrastre)Modificado  ");
            query.append("				from fn_tSaldosVistaPorFecha('81102',convert(varchar,convert(date,GETDATE()),103))  ");
            query.append("				where SUBSTRING(csubcuenta,32,1) in('3','2','5')  ");
            query.append("				group by substring(csubcuenta,57,3) ");
            query.append("				,SUBSTRING(csubcuenta,32,5) ");
            query.append("			)modificado on modificado.partida=paas.cIdSubPartida ");
            query.append("			and modificado.UE=paas.cIdUnidadEjecutora ");
            query.append("			where modificado.Modificado is null ");
            query.append("			group by cIdUnidadEjecutora,modificado.Modificado ");
            query.append("		)paas ");
            query.append("		inner join tCatUnidadEjecutora as unidad with(Nolock)  ");
            query.append("		on unidad.cUnidadEjecutora=paas.cIdUnidadEjecutora ");
            query.append("		inner join tCatUnidadesEjecutorasGPP_GRH as relUE with(Nolock)  ");
            query.append("		on relUE.cIdunidadEjecutoraGPP=unidad.cUnidadEjecutora  ");
            query.append("		left join (  ");
            query.append("				select c_jefeinmediato,d_email,ueGPP.cIdunidadEjecutoraGPP  ");
            query.append("				,ue.cDescCoortaCordinacion,ue.c_coordinacion  ");
            query.append("				,plazas.c_plaza,plazas.d_plaza  ");
            query.append("				,emp.d_emplnombre+' '+emp.d_emplap+' '+emp.d_emplam nombreComp  ");
            query.append("				from nomina_" + cEjercicio + ".dbo.nom_empleado emp with(Nolock)  ");
            query.append("				inner join nomina_" + cEjercicio + ".dbo.nom_plazas plazas with(Nolock)  ");
            query.append("				on plazas.c_empresa=emp.c_empresa  ");
            query.append("				and plazas.c_plaza=emp.c_plaza  ");
            query.append("				and plazas.c_puesto=emp.c_puesto  ");
            query.append("				inner join nomina_" + cEjercicio + ".dbo.nom_Unidad_Ejecutora ue  with(Nolock)  ");
            query.append("				on emp.cUadministrativa=ue.cUadministrativa  ");
            query.append("				inner join tCatUnidadesEjecutorasGPP_GRH ueGPP with(Nolock) on ue.cUejecutora=ueGPP.cIdunidadEjecutoraGRH   ");
            query.append("				where emp.n_emplstatus=1  ");
            query.append("				and (plazas.d_plaza like'Gerencia%' or plazas.d_plaza like'UNIDAD%REGIONAL%' or plazas.d_plaza like'PROMOTOR_A%')  ");
            query.append("		)manager on manager.cIdunidadEjecutoraGPP=paas.cIdUnidadEjecutora  ");
            query.append("		left join(  ");
            query.append("			select c_jefeinmediato,d_email,ue.cUejecutora  ");
            query.append("			,ue.cDescCoortaCordinacion,ue.c_coordinacion  ");
            query.append("			,plazas.c_plaza,plazas.d_plaza  ");
            query.append("			,emp.d_emplnombre+' '+emp.d_emplap+' '+emp.d_emplam nombreCompCoor  ");
            query.append("			from nomina_" + cEjercicio + ".dbo.nom_empleado emp with(Nolock)  ");
            query.append("			inner join nomina_" + cEjercicio + ".dbo.nom_plazas plazas with(Nolock)  ");
            query.append("			on plazas.c_empresa=emp.c_empresa  ");
            query.append("			and plazas.c_plaza=emp.c_plaza  ");
            query.append("			and plazas.c_puesto=emp.c_puesto  ");
            query.append("			inner join nomina_" + cEjercicio + ".dbo.nom_Unidad_Ejecutora ue  with(Nolock)  ");
            query.append("			on emp.cUadministrativa=ue.cUadministrativa  ");
            query.append("			where emp.n_emplstatus=1  ");
            query.append("			and c_jefeinmediato in(1,0)  ");
            query.append("		)coordinadores on coordinadores.cUejecutora=relUE.cCoordinacionGPP  ");
            query.append("		and coordinadores.c_coordinacion=relUE.cCoordinacionGPP  ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            rs = ps.executeQuery();
            rsM = rs.getMetaData();
            tabla = new ArrayList<List<String>>();
            fila = new ArrayList<String>();
            while (rs.next()) {
                for (int i = 0; i < rsM.getColumnCount(); i++) {
                    fila.add(rs.getString(i + 1));
                }
                tabla.add(fila);
                fila = null;
                fila = new ArrayList<String>();
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
            fila = null;
            query = null;
            rsM = null;
        }
        return tabla;
    }

    public ArrayList<List<String>> getListAreasPAAASPartidas(Connection conn, String UE) throws SQLException {
        StringBuilder query = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        ArrayList<List<String>> tabla = null;
        List<String> fila = null;
        ResultSetMetaData rsM = null;
        try {
            query = new StringBuilder();
            query.append("		select  ");
            query.append("		cIdUnidadEjecutora+' - '+unidad.D_DESCRIPCION unidad ");
            query.append("		,paas.cIdSubPartida+' - '+partida.cSubPartida partida ");
            query.append("		,isnull(Modificado,0)preupuestoModificado ");
            query.append("		,sum(subtotal_calendarizado_PAAS) subtotal_calendarizado_PAAS ");
            query.append("		,SUM(Total_calendarizado_PAAS)Total_calendarizado_PAAS ");
            query.append("		,paas.cIdSubPartida ");
            query.append("		from  ");
            query.append("			(select   ");
            query.append("				cIdUnidadEjecutora ");
            query.append("				,cIdSubPartida ");
            query.append("				,sum(subtotal)subtotal_calendarizado_PAAS  ");
            query.append("				,SUM(Total)Total_calendarizado_PAAS  ");
            query.append("				from	  ");
            query.append("					( ");
            query.append("						select   ");
            query.append("						per.cIdUnidadEjecutora ");
            query.append("						,sum(nCantidad*mPrecioUnitario) as subtotal  ");
            query.append("						,sum(round(cast(nCantidad*mPrecioUnitario*(1+(paasDet.nPorcentajeIVA*0.01)) as money),2)) as Total  ");
            query.append("						,paasDet.cIdSubPartida ");
            query.append("						,paasDet.cIdCABM ");
            query.append("						from mProgramaAnual as paas with(nolock)  ");
            query.append("						inner join mProgramaAnualDetalle as paasDet with(nolock)  ");
            query.append("						on paas.cIdUnidadEjecutora=paasDet.cIdUnidadEjecutora  ");
            query.append("						and paas.cEjercicio=paas.cEjercicio  ");
            query.append("						inner join mProgramaAnualDetallePeriodo as per with(Nolock)  ");
            query.append("						on per.cEjercicio=paasDet.cEjercicio  ");
            query.append("						and per.cIdUnidadEjecutora=paasDet.cIdUnidadEjecutora  ");
            query.append("						and per.cIdCABM=paasDet.cIdCABM and per.cIdSubPartida=paasDet.cIdSubPartida  ");
            query.append("						where paas.cIdUnidadEjecutora='" + UE + "' ");
            query.append("						group by per.cIdUnidadEjecutora ");
            query.append("						,paasDet.cIdSubPartida ");
            query.append("						,paasDet.cIdCABM ");
            query.append("					)sub ");
            query.append("				group by cIdUnidadEjecutora,cIdSubPartida ");
            query.append("				having sum(subtotal)>0 ");
            query.append("			)paas ");
            query.append("			inner join tCatUnidadEjecutora as unidad with(Nolock) ");
            query.append("			on unidad.cUnidadEjecutora=paas.cIdUnidadEjecutora ");
            query.append("			inner join mCatalogoSubPartida as partida with(Nolock) ");
            query.append("			on paas.cIdSubPartida=partida.cIdSubPartida ");
            query.append("			left join( ");
            query.append("				select   ");
            query.append("				substring(csubcuenta,57,3) as UE ");
            query.append("				,SUBSTRING(csubcuenta,32,5)partida ");
            query.append("				,SUM(msaldoArrastre)Modificado  ");
            query.append("				from fn_tSaldosVistaPorFecha('81102',convert(varchar,convert(date,GETDATE()),103))  ");
            query.append("				where SUBSTRING(csubcuenta,32,1) in('3','2','5')  ");
            query.append("				group by substring(csubcuenta,57,3) ");
            query.append("				,SUBSTRING(csubcuenta,32,5) ");
            query.append("			)modificado on modificado.partida=paas.cIdSubPartida ");
            query.append("			and modificado.UE=paas.cIdUnidadEjecutora ");
            query.append("			where modificado.Modificado is null ");
            query.append("			group by cIdUnidadEjecutora,modificado.Modificado ");
            query.append("			,paas.cIdSubPartida	,unidad.D_DESCRIPCION,partida.cSubPartida ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            rs = ps.executeQuery();
            rsM = rs.getMetaData();
            tabla = new ArrayList<List<String>>();
            fila = new ArrayList<String>();
            while (rs.next()) {
                for (int i = 0; i < rsM.getColumnCount(); i++) {
                    fila.add(rs.getString(i + 1));
                }
                tabla.add(fila);
                fila = null;
                fila = new ArrayList<String>();
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
            fila = null;
            query = null;
            rsM = null;
        }
        return tabla;
    }

    public boolean saveBinnacleSendEmailPAAASPresup(String cIdUnidadEjec, String cObservation, String cEmairPurchasingDepartment, String cEmairManager, int isEmailSend) throws Exception {
        boolean resp = false;
        PreparedStatement pstmnt = null;
        String query = null;
        Connection conn = null;
        try {
            conn = com.syc.gestion.util.Util.getStandAloneConnection(true);
            query = "insert into mBitacoraSendEmailPAAAS_Presupuesto (cIdUnidadEjecutora,cEamilArea,cEamilJefeAdquisiciones,cObservaciones,isEmailSend,fFechaEnvio)\r\n" + "values(?,?,?,?,?,getdate()) ";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, cIdUnidadEjec);
            pstmnt.setString(2, cEmairManager);
            pstmnt.setString(3, cEmairPurchasingDepartment);
            pstmnt.setString(4, cObservation);
            pstmnt.setInt(5, isEmailSend);
            resp = pstmnt.executeUpdate() > 0;
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            log.error("Error occurred", "Error al guardar la bitacora de envio de correos: " + e);
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            if (conn != null) {
                conn.close();
            }
            pstmnt = null;
            query = null;
            conn = null;
        }
        return resp;
    }

    public boolean saveBinnacleBorraPAAAS(Connection conn, String cIdUnidadEjec, String cIdSubpartida, String cDescripcion) throws Exception {
        boolean resp = false;
        PreparedStatement pstmnt = null;
        String query = null;
        try {
            query = "insert into mBitacoraBorraPAAAS (cIdunidadEjecutora,cIdSubpartida,cDescripcion,fFechaCreacion) values(?,?,?,getdate()) ";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, cIdUnidadEjec);
            pstmnt.setString(2, cIdSubpartida);
            pstmnt.setString(3, cDescripcion);
            resp = pstmnt.executeUpdate() > 0;
            conn.commit();
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            pstmnt = null;
            query = null;
        }
        return resp;
    }

    public File generaReportePAAS_Presup(Connection conn, String namePlantilla, String UE, String cEjercicio, boolean isProcesoBorraCucop) throws Exception {
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Presupuesto_vs_PAAAS_" + UE + ".xlsx";
        File cFileExcelPlantilla = null;
        InputStream fs = null;
        InputStream fsArchivo = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        XSSFWorkbook workbook = null;
        File fsalida = null;
        XSSFCellStyle estiloTabla = null;
        try {
            cFileExcelPlantilla = new File(namePlantilla);
            fs = new FileInputStream(cFileExcelPlantilla);
            Util.copiaArchivo(fs, file_name);
            fsArchivo = new FileInputStream(cFileExcelPlantilla);
            workbook = new XSSFWorkbook(fsArchivo);
            XSSFSheet firstSheet = workbook.getSheetAt(0);
            estiloTabla = workbook.createCellStyle();
            estiloTabla.setBorderBottom(BorderStyle.THIN);
            estiloTabla.setBorderLeft(BorderStyle.THIN);
            estiloTabla.setBorderRight(BorderStyle.THIN);
            estiloTabla.setBorderTop(BorderStyle.THIN);
            //Escribe en la hoja 1
            if (isProcesoBorraCucop) {
                writeSheet1Partidas_A_BorrarDelPAAAS(conn, firstSheet, estiloTabla, UE, cEjercicio);
            } else {
                writeSheet1Presup_PAAAS(conn, firstSheet, estiloTabla, UE, cEjercicio);
            }
            fsalida = new File(file_name);
            fos = new FileOutputStream(fsalida);
            bos = new BufferedOutputStream(fos, 1024);
            workbook.write(bos.toPath());
            bos.flush();
        } finally {
            if (fos != null) {
                fos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fs != null) {
                fs.close();
            }
            if (workbook != null) {
                workbook.close();
            }
            fs = null;
            bos = null;
            workbook = null;
            fos = null;
            cFileExcelPlantilla = null;
            fs = null;
        }
        return fsalida;
    }

    private void writeSheet1Presup_PAAAS(Connection conn, XSSFSheet firstSheet, XSSFCellStyle estiloTabla, String UE, String cEjercicio) throws Exception {
        StringBuilder query = new StringBuilder();
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        XSSFRow rw = null;
        XSSFCell celdarsad = null;
        try {
            rw = (firstSheet.getRow(2) == null ? firstSheet.createRow(2) : firstSheet.getRow(2));
            celdarsad = (rw.getCell(0) == null ? rw.createCell(0) : rw.getCell(0));
            celdarsad.setCellValue("Ejercicio Fiscal " + cEjercicio);
            query.append("select modd.UE+' - '+unidad.D_DESCRIPCION areaResponsable ");
            query.append(",modd.partida+' - '+catPartida.cSubPartida partida_presupuestal ");
            query.append(",modd.Modificado PRESUPUESTO_MODIFICADO ");
            query.append(",isnull(paas.subtotal_calendarizado_PAAS,0)subtotal_calendarizado_PAAS ");
            query.append(",isnull(paas.Total_calendarizado_PAAS,0) Total_calendarizado_PAAS ");
            query.append("from ");
            query.append("(select  ");
            query.append("	substring(csubcuenta,57,3) as UE ");
            query.append("	,substring(csubcuenta,32,5) as partida ");
            query.append("	,SUM(msaldoArrastre)Modificado ");
            query.append("	from fn_tSaldosVistaPorFecha('81102',convert(varchar,convert(date,GETDATE()),103)) ");
            query.append("	where SUBSTRING(csubcuenta,32,1) in('3','2','5') ");
            query.append("	and substring(csubcuenta,57,3) not in(select *from tBlackListUE_Presupuesto with(Nolock))  ");
            query.append(" and SUBSTRING(csubcuenta,32,3) not  in('321','322','341','343','349','351','375','376','377','378','379','592','593','594','595','596','598') ");
            query.append(" and SUBSTRING(csubcuenta,32,2) not  in('39','58') ");
            query.append("	group by substring(csubcuenta,32,5) ");
            query.append("	,substring(csubcuenta,57,3) ");
            query.append(")modd ");
            query.append("inner join tCatUnidadEjecutora as unidad with(Nolock) ");
            query.append("on unidad.cUnidadEjecutora=modd.UE ");
            query.append("inner join mCatalogoSubPartida as catPartida with(Nolock) ");
            query.append("on catPartida.cIdSubPartida=modd.partida ");
            query.append("left join( ");
            query.append("	select  ");
            query.append("	cIdUnidadEjecutora ");
            query.append("	,cIdSubPartida ");
            query.append("	,sum(subtotal)subtotal_calendarizado_PAAS ");
            query.append("	,SUM(Total)Total_calendarizado_PAAS ");
            query.append("	from	 ");
            query.append("		(select  ");
            query.append("		per.* ");
            query.append("		,nCantidad*mPrecioUnitario as subtotal ");
            query.append("		,round(cast(nCantidad*mPrecioUnitario*(1+(paasDet.nPorcentajeIVA*0.01)) as money),2) as Total ");
            query.append("		,paasDet.nPorcentajeIVA ");
            query.append("		from mProgramaAnual as paas with(nolock) ");
            query.append("		inner join mProgramaAnualDetalle as paasDet with(nolock) ");
            query.append("		on paas.cIdUnidadEjecutora=paasDet.cIdUnidadEjecutora ");
            query.append("		and paas.cEjercicio=paas.cEjercicio ");
            query.append("		inner join mProgramaAnualDetallePeriodo as per with(Nolock) ");
            query.append("		on per.cEjercicio=paasDet.cEjercicio ");
            query.append("		and per.cIdUnidadEjecutora=paasDet.cIdUnidadEjecutora ");
            query.append("		and per.cIdCABM=paasDet.cIdCABM and per.cIdSubPartida=paasDet.cIdSubPartida ");
            query.append("		)sub ");
            query.append("	group by cIdUnidadEjecutora ");
            query.append("	,cIdSubPartida ");
            query.append("	)paas on paas.cIdUnidadEjecutora=modd.UE ");
            query.append("	and paas.cIdSubPartida=modd.partida ");
            query.append("where  ");
            query.append("modd.Modificado>ISNULL(paas.Total_calendarizado_PAAS,0) ");
            query.append("and modd.UE='" + UE + "' ");
            query.append("order by modd.UE,modd.partida");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            //Escribe el detalle
            int renglonInicio = 5;
            int rows = 0;
            int j = 0;
            int cnt = 0;
            int cantRowsFinal = 1;
            while (rst.next()) {
                rows = renglonInicio + cnt;
                if (j > 7) {
                    firstSheet.shiftRows(rows, rows + cantRowsFinal, 1);
                    rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                } else {
                    rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                }
                j++;
                for (int i = 0; i < totalcolumnas; i++) {
                    com.syc.gestion.util.Util.createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
                cnt++;
            }
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
            celdarsad = null;
            query = null;
        }
    }

    private void writeSheet1Partidas_A_BorrarDelPAAAS(Connection conn, XSSFSheet firstSheet, XSSFCellStyle estiloTabla, String UE, String cEjercicio) throws Exception {
        StringBuilder query = null;
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSetMetaData rsMetadata = null;
        XSSFRow rw = null;
        XSSFCell celdarsad = null;
        try {
            query = new StringBuilder();
            rw = (firstSheet.getRow(2) == null ? firstSheet.createRow(2) : firstSheet.getRow(2));
            celdarsad = (rw.getCell(0) == null ? rw.createCell(0) : rw.getCell(0));
            celdarsad.setCellValue("Ejercicio Fiscal " + cEjercicio);
            query.append("		select  ");
            query.append("		cIdUnidadEjecutora+' - '+unidad.D_DESCRIPCION unidad ");
            query.append("		,paas.cIdSubPartida+' - '+partida.cSubPartida partida ");
            query.append("		,isnull(Modificado,0)preupuestoModificado ");
            query.append("		,sum(subtotal_calendarizado_PAAS) subtotal_calendarizado_PAAS ");
            query.append("		,SUM(Total_calendarizado_PAAS)Total_calendarizado_PAAS ");
            query.append("		,paas.cIdSubPartida ");
            query.append("		from  ");
            query.append("			(select   ");
            query.append("				cIdUnidadEjecutora ");
            query.append("				,cIdSubPartida ");
            query.append("				,sum(subtotal)subtotal_calendarizado_PAAS  ");
            query.append("				,SUM(Total)Total_calendarizado_PAAS  ");
            query.append("				from	  ");
            query.append("					( ");
            query.append("						select   ");
            query.append("						per.cIdUnidadEjecutora ");
            query.append("						,sum(nCantidad*mPrecioUnitario) as subtotal  ");
            query.append("						,sum(round(cast(nCantidad*mPrecioUnitario*(1+(paasDet.nPorcentajeIVA*0.01)) as money),2)) as Total  ");
            query.append("						,paasDet.cIdSubPartida ");
            query.append("						,paasDet.cIdCABM ");
            query.append("						from mProgramaAnual as paas with(nolock)  ");
            query.append("						inner join mProgramaAnualDetalle as paasDet with(nolock)  ");
            query.append("						on paas.cIdUnidadEjecutora=paasDet.cIdUnidadEjecutora  ");
            query.append("						and paas.cEjercicio=paas.cEjercicio  ");
            query.append("						inner join mProgramaAnualDetallePeriodo as per with(Nolock)  ");
            query.append("						on per.cEjercicio=paasDet.cEjercicio  ");
            query.append("						and per.cIdUnidadEjecutora=paasDet.cIdUnidadEjecutora  ");
            query.append("						and per.cIdCABM=paasDet.cIdCABM and per.cIdSubPartida=paasDet.cIdSubPartida  ");
            query.append("						where paas.cIdUnidadEjecutora=? ");
            query.append("						group by per.cIdUnidadEjecutora ");
            query.append("						,paasDet.cIdSubPartida ");
            query.append("						,paasDet.cIdCABM ");
            query.append("					)sub ");
            query.append("				group by cIdUnidadEjecutora,cIdSubPartida ");
            query.append("				having sum(subtotal)>0 ");
            query.append("			)paas ");
            query.append("			inner join tCatUnidadEjecutora as unidad with(Nolock) ");
            query.append("			on unidad.cUnidadEjecutora=paas.cIdUnidadEjecutora ");
            query.append("			inner join mCatalogoSubPartida as partida with(Nolock) ");
            query.append("			on paas.cIdSubPartida=partida.cIdSubPartida ");
            query.append("			left join( ");
            query.append("				select   ");
            query.append("				substring(csubcuenta,57,3) as UE ");
            query.append("				,SUBSTRING(csubcuenta,32,5)partida ");
            query.append("				,SUM(msaldoArrastre)Modificado  ");
            query.append("				from fn_tSaldosVistaPorFecha('81102',convert(varchar,convert(date,GETDATE()),103))  ");
            query.append("				where SUBSTRING(csubcuenta,32,1) in('3','2','5')  ");
            query.append("				group by substring(csubcuenta,57,3) ");
            query.append("				,SUBSTRING(csubcuenta,32,5) ");
            query.append("			)modificado on modificado.partida=paas.cIdSubPartida ");
            query.append("			and modificado.UE=paas.cIdUnidadEjecutora ");
            query.append("			where modificado.Modificado is null ");
            query.append("			group by cIdUnidadEjecutora,modificado.Modificado ");
            query.append("			,paas.cIdSubPartida	,unidad.D_DESCRIPCION,partida.cSubPartida ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, UE);
            rst = ps.executeQuery();
            rsMetadata = rst.getMetaData();
            int totalcolumnas = rsMetadata.getColumnCount();
            //Escribe el detalle
            int renglonInicio = 5;
            int rows = 0;
            int j = 0;
            int cnt = 0;
            int cantRowsFinal = 1;
            while (rst.next()) {
                rows = renglonInicio + cnt;
                if (j > 7) {
                    firstSheet.shiftRows(rows, rows + cantRowsFinal, 1);
                    rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                } else {
                    rw = (firstSheet.getRow(rows) == null ? firstSheet.createRow(rows) : firstSheet.getRow(rows));
                }
                j++;
                for (int i = 0; i < totalcolumnas - 1; i++) {
                    com.syc.gestion.util.Util.createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
                cnt++;
            }
        } catch (Exception e) {
            throw (e);
        } finally {
            if (rst != null) {
                rst.close();
            }
            if (ps != null) {
                ps.close();
            }
            rsMetadata = null;
            rst = null;
            ps = null;
            celdarsad = null;
            query = null;
        }
    }

    public boolean borraCucopPAAASDisponible(Connection conn, String cIdUnidadEjecutora, String cIdSubPartida) throws SQLException {
        StringBuilder query = null;
        PreparedStatement pstmnt = null;
        try {
            query = new StringBuilder();
            query.append("	update  pdp set  pdp.nCantidad=0,pdp.mPrecioUnitario=0 ");
            query.append("	from mProgramaAnualDetallePeriodo pdp with(Nolock) ");
            query.append("	inner join( ");
            query.append("		select *from  ( ");
            query.append("			select  ");
            query.append("			paasDetPer.cEjercicio ");
            query.append("			,paasDetPer.cIdUnidadEjecutora ");
            query.append("			,paasDetPer.cIdSubPartida ");
            query.append("			,paasDetPer.cIdCABM ");
            query.append("			,paasDetPer.nIdPeriodo ");
            query.append("			,paasDetPer.nCantidad-isnull(pagodirectopaas.cantidadGastado,0)-isnull(msl.cantidadGastada,0)disponible ");
            query.append("			from mProgramaAnualDetallePeriodo as paasDetPer with(Nolock) ");
            query.append("			 LEFT JOIN( ");
            query.append("				SELECT pagopaas.cIdUnidadEjecutora ");
            query.append("				,pagopaas.cIdCABM,pagopaas.cIdSubPartida ");
            query.append("				,pagopaas.cEjercicio,detalle.nIdPeriodo ");
            query.append("				,SUM(detalle.nCantidad)cantidadGastado ");
            query.append("				FROM dbo.tPagoDirectoPAAS AS pagopaas WITH(NOLOCK) ");
            query.append("				INNER JOIN dbo.tPagoDirectoPAASDetalle AS detalle WITH(NOLOCK) ");
            query.append("				ON pagopaas.cEjercicio = detalle.cEjercicio ");
            query.append("				AND pagopaas.cIdUnidadEjecutora = detalle.cIdUnidadEjecutora ");
            query.append("				AND pagopaas.nFolioPagoDirecto = detalle.nFolioPagoDirecto ");
            query.append("				AND pagopaas.nLinea = detalle.nLinea ");
            query.append("				WHERE  ");
            query.append("				pagopaas.cIdEstadoLinea NOT IN('L','C') ");
            query.append("				GROUP BY pagopaas.cIdUnidadEjecutora ");
            query.append("				,pagopaas.cIdCABM,pagopaas.cIdSubPartida ");
            query.append("				,pagopaas.cEjercicio,detalle.nIdPeriodo ");
            query.append("			)pagodirectopaas ");
            query.append("			ON paasDetPer.cEjercicio = pagodirectopaas.cEjercicio ");
            query.append("			AND paasDetPer.cIdCABM = pagodirectopaas.cIdCABM ");
            query.append("			AND paasDetPer.cIdSubPartida = pagodirectopaas.cIdSubPartida ");
            query.append("			AND paasDetPer.cIdUnidadEjecutora = pagodirectopaas.cIdUnidadEjecutora ");
            query.append("			AND paasDetPer.nIdPeriodo=pagodirectopaas.nIdPeriodo ");
            query.append("			left join ( ");
            query.append("				SELECT     msl.cEjercicio, msl.cIdSubPartida, slp.nIdPeriodo, msl.cIdCABM, msl.cIdUnidadEjecutora ");
            query.append("				,sum(slp.nCantidad)cantidadGastada ");
            query.append("				FROM dbo.mSolicitudLineas AS MSL WITH(NOLOCK) ");
            query.append("				INNER JOIN mSolicitudLineasPeriodo slp with(nolock) ON slp.cEjercicio = MSL.cEjercicio ");
            query.append("				AND slp.cIdTipoSolicitud = MSL.cIdTipoSolicitud ");
            query.append("				AND slp.cIdUnidadEjecutora = MSL.cIdUnidadEjecutora ");
            query.append("				AND slp.nIdConsecutivo = MSL.nIdConsecutivo ");
            query.append("				AND slp.nIdLineaSolicitud = MSL.nIdLineaSolicitud ");
            query.append("				WHERE    (cIdEstadoLinea not in('L','C')) ");
            query.append("				 GROUP BY msl.cEjercicio, msl.cIdSubPartida, slp.nIdPeriodo, msl.cIdCABM, msl.cIdUnidadEjecutora ");
            query.append("			)msl ");
            query.append("			on msl.cEjercicio=paasDetPer.cEjercicio ");
            query.append("			and msl.cIdUnidadEjecutora=paasDetPer.cIdUnidadEjecutora ");
            query.append("			AND msl.cIdSubPartida=paasDetPer.cIdSubPartida ");
            query.append("			and msl.cIdCABM=paasDetPer.cIdCABM ");
            query.append("			AND msl.nIdPeriodo=paasDetPer.nIdPeriodo ");
            query.append("			where paasDetPer.cIdUnidadEjecutora=? and paasDetPer.cIdSubPartida=? ");
            query.append("		)disp ");
            query.append("		where disp.disponible>0 ");
            query.append("	)disp ");
            query.append("	on disp.cEjercicio=pdp.cEjercicio ");
            query.append("	and disp.cIdUnidadEjecutora=pdp.cIdUnidadEjecutora ");
            query.append("	and disp.cIdSubPartida=pdp.cIdSubPartida ");
            query.append("	and disp.cIdCABM=pdp.cIdCABM ");
            query.append("	and disp.nIdPeriodo=pdp.nIdPeriodo ");
            log.info("Object: {}", query.toString());
            log.info("Object: {}", "Parametros:\ncIdUnidadEjecutora=" + cIdUnidadEjecutora + "\ncIdSubPartida=" + cIdSubPartida);
            pstmnt = conn.prepareStatement(query.toString());
            pstmnt.setString(1, cIdUnidadEjecutora);
            pstmnt.setString(2, cIdSubPartida);
            return pstmnt.executeUpdate() > 0;
        } finally {
            query = null;
            pstmnt = null;
        }
    }

    public HashMap<Integer, ArrayList<List<String>>> getListRequisPendientesFirma(Connection conn, int afterDays) throws SQLException {
        StringBuilder query = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        ArrayList<List<String>> tabla = null;
        HashMap<Integer, ArrayList<List<String>>> infoRequis = null;
        List<String> fila = null;
        ResultSetMetaData rsM = null;
        int numEmpleado = 0;
        int numEmpleadoAnt = 0;
        int init = 0;
        try {
            query = new StringBuilder();
            query.append(" select  ");
            query.append(" sol.cIdSolicitud ");
            query.append(" ,sol.cIdSubPartida ");
            query.append(" ,sol.cDescripcion ");
            query.append(" ,sol.cIdUsuarioCreacion ");
            query.append(" ,enc.fAplicacion ");
            query.append(" ,catFirm.nNumeroEmpleado ");
            query.append(" ,usuario.U_EMAIL ");
            query.append(" ,usuario.U_NOMBRE nombreFirmante ");
            query.append(" ,catFirm.cPuesto ");
            query.append(" ,enc.nFolioApartado ");
            query.append(" from mSolicitud sol with(Nolock) ");
            query.append(" inner join tApartadoEncabezado enc with(Nolock)  ");
            query.append(" on enc.cIdSolicitud=sol.cIdSolicitud ");
            query.append(" inner join mSolicitudFirmantes as solFirm with(Nolock) ");
            query.append(" on solFirm.cIdSolicitud=sol.cIdSolicitud ");
            query.append(" inner join mCatalogoFirmantes catFirm with(Nolock) ");
            query.append(" on catFirm.nIdFirmante=solFirm.nIdFirmante ");
            query.append(" and catFirm.cIdUnidadEjecutora=solFirm.cIdUnidadEjecutora ");
            query.append(" inner join CG_USUARIO as usuario with(Nolock) ");
            query.append(" on usuario.cNumeroEmpleado=catFirm.nNumeroEmpleado ");
            query.append(" where sol.nIdEstado=6 and solFirm.nNumeroFirmante=3 ");
            query.append(" and catFirm.nNumeroEmpleado is not null ");
            query.append(" and enc.cDocumentoHaplicado='S' ");
            query.append(" and DATEDIFF (DAY, enc.fAplicacion , GETDATE() ) >=? ");
            query.append(" order by catFirm.nNumeroEmpleado ");
            log.info("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, afterDays);
            rs = ps.executeQuery();
            tabla = new ArrayList<List<String>>();
            infoRequis = new HashMap<>();
            rsM = rs.getMetaData();
            while (rs.next()) {
                fila = new ArrayList<String>();
                numEmpleado = rs.getInt("nNumeroEmpleado");
                if (init == 0) {
                    numEmpleadoAnt = numEmpleado;
                }
                for (int i = 0; i < rsM.getColumnCount(); i++) {
                    fila.add(rs.getString(i + 1));
                }
                if (numEmpleado == numEmpleadoAnt) {
                    tabla.add(fila);
                } else {
                    infoRequis.put(numEmpleadoAnt, tabla);
                    tabla = null;
                    tabla = new ArrayList<List<String>>();
                    tabla.add(fila);
                }
                numEmpleadoAnt = numEmpleado;
                fila = null;
                init++;
            }
            infoRequis.put(numEmpleadoAnt, tabla);
        } finally {
            query.setLength(0);
            tabla = null;
            query = null;
            ps = null;
            rs = null;
            rsM = null;
        }
        return infoRequis;
    }
}
